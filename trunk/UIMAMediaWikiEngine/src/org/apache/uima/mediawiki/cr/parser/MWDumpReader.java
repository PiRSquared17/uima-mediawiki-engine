/*
 * Copyright [2010] [Fabien Poulard <fabien.poulard@univ-nantes.fr>, Maxime
 * Bury, Maxime Rihouey] Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.uima.mediawiki.cr.parser;

import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.uima.UIMAFramework;
import org.apache.uima.mediawiki.cr.factory.MWArticleFactory;
import org.apache.uima.mediawiki.cr.factory.MWRevisionFactory;
import org.apache.uima.mediawiki.cr.types.MWArticle;
import org.apache.uima.mediawiki.cr.types.MWSiteinfo;
import org.apache.uima.util.Level;

/**
 * This class is the core of the Collection reader component. It's dedicated to
 * extract the relevant data from the XML stream. I tried to keep things as
 * simple and robust as possible.
 * <p>
 * I make use of the StAX API to provide a pull-parsing style parser. The basic
 * unit you can get from that parser is a page, also known as an article. You
 * can also get the Siteinfo information (especially the namespaces) if it's
 * present.
 * <p>
 * You can get an instance of this parser with the
 * {@link org.apache.uima.mediawiki.cr.factory.MWDumpReaderFactory
 * MWDumpReaderFactory}. This factory also allows you to place various filters
 * on the XML stream to exclude or include only certain pages or revisions.
 * 
 * @see org.apache.uima.mediawiki.cr.types.MWSiteinfo
 * @see org.apache.uima.mediawiki.cr.types.MWArticle
 * @see org.apache.uima.mediawiki.cr.types.MWRevision
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWDumpReader {
	/** Parser variables */
	private final XMLStreamReader	reader;
	/** Data */
	protected MWSiteinfo			theInfo;
	/** Some flags */
	protected boolean				endOfDocumentReached;
	protected boolean				hasSiteInfo;
	protected boolean				pageComputed;

	/**
	 * Initializes the parser. In particular, it skips a few unuseful blocks at
	 * the beginning and tries to compute the Siteinfo right away. It also
	 * initializes the data factories.
	 * 
	 * @param reader
	 *            An XML stream reader from which we get the data
	 * @throws MWParseException
	 *             is thrown if the underlying XML document is malformed.
	 */
	public MWDumpReader(XMLStreamReader reader) throws MWParseException {
		// Initialise parser
		try {
			this.reader = reader;
			// Skip the <mediawiki> tag
			nextOpeningTag(2);
		} catch (final XMLStreamException e) {
			throw new MWParseException("An unexpected error occured while starting the parser");
		}
		// Initialise data factories
		// Process website info
		// Check if the <siteinfo> tag is there (it's optional)
		hasSiteInfo = reader.getLocalName().equals("siteinfo");
		if (hasSiteInfo) computeSiteInfo();
		// Initialise Article and Revision factorys
		MWArticleFactory.init(theInfo);
		// Some flags
		endOfDocumentReached = false;
		pageComputed = false;
	}

	/**
	 * This method returns the last computed page. It should only be used after
	 * a successful call to the {@link #hasPage()} method.
	 * 
	 * @return the last computed page
	 * @see org.apache.uima.mediawiki.cr.types.MWArticle
	 */
	public MWArticle getPage() {
		pageComputed = false;
		return MWArticleFactory.produceArticle();
	}

	/**
	 * This method return the site info (containing namespaces in particular).
	 * It should only be used after a successful call to {@link #hasSiteInfo()}.
	 * Otherwise, the fields of the returned object will have a default value.
	 * 
	 * @return the site info
	 * @see org.apache.uima.mediawiki.cr.types.MWSiteinfo
	 */
	public MWSiteinfo getSiteInfo() {
		return theInfo;
	}

	/**
	 * A successful call to this method ensures you that a call to
	 * {@link #getSiteInfo()} will return relevant information.
	 * 
	 * @return <code>true</code> if the parser has managed to compute the site
	 *         info; <code>false</code> otherwise.
	 */
	public final boolean hasSiteInfo() {
		return hasSiteInfo;
	}

	/**
	 * A successful call to this method ensures you that a call to
	 * {@link #getPage()} will succeed as well. The parser tries to compute a
	 * page, taking in account the filters, until it succeeds to compute one or
	 * reaches the end of the document.
	 * 
	 * @return <code>true</code> if the parser has managed to compute a page;
	 *         <code>false</code> otherwise.
	 */
	public final boolean hasPage() {
		// Clear the page factory
		MWArticleFactory.clear();
		// Try to compute a page
		pageComputed = false;
		try {
			while (!pageComputed && !endOfDocumentReached)
				pageComputed = computePage();
			return pageComputed;
		} catch (final MWParseException e) {
			UIMAFramework.getLogger().log(Level.SEVERE,
					"XML parser encountered an exception : " + e.getMessage());
			endOfDocumentReached = true;
			pageComputed = false;
			return pageComputed;
		}
	}

	/**
	 * Use this method to free the ressources the parser was using (meaning the
	 * XML stream) in a clean way.
	 * 
	 * @throws XMLStreamException
	 *             If the XML stream fails to be closed.
	 */
	public final void close() {
		try {
			reader.close();
		} catch (final XMLStreamException e) {
			// If the closing fails, the GC will just have to deal with it.
			UIMAFramework.getLogger().log(Level.WARNING, "Cannot close the stream : " + e.getMessage());
		}
	}

	/**
	 * This method is normally called only if a call to hasPage() succeeded. It
	 * returns true when the parsing of the page is successful, meaning no
	 * malformation were encountered and at least one revision was kept. It
	 * returns false otherwise.
	 * 
	 * @return <code>true</code> on the successful parsing of a page,
	 *         <code>false</code> otherwise.
	 * @throws MWParseException
	 *             If a malformation is encountered in the underlying XML stream
	 */
	private final boolean computePage() throws MWParseException {
		boolean endPage = false;
		try {
			// Make sure we start on a <page> tag
			nextTag("page");
			// Move to the first nested tag
			nextOpeningTag(1);
			// While we don't reach the end of the page, we compute the data we
			// find.
			while (!endPage) {
				switch (MWTag.toTag(reader.getLocalName())) {
					case TITLE:
						MWArticleFactory.hasTitle(getTagText());
						break;
					case ID:
						MWArticleFactory.hasId(getTagText());
						break;
					case REVISION:
						computeRevision();
						break;
					case INVALID_TAG:
						// If we find an unrelevant tag, we skip it.
						skipThisTag();
						break;
					case PAGE:
						// When we hit a page tag again, we know we are done
						// with this one.
						endPage = true;
						break;
					default:
						// This happens if we don't process a tag acknoledged in
						// the MWTag enum.
						endPage = true;
				}
				if (!endPage) nextOpeningTag(1);
			}
		} catch (final XMLStreamException e) {
			// If we encounter a malformation of some sort
			throw new MWParseException("The parser encountered a malformation in the input file. "
					+ e.getMessage());
		} catch (final NoSuchElementException e) {
			// We reached the end of the document, last page might me uncomplete
			endOfDocumentReached = true;
		} catch (final IllegalStateException e) {
			// We reached the end of the document, last page might me uncomplete
			endOfDocumentReached = true;
		}

		// The page is considered empty when it holds no revisions.
		// This can happen when some filters are set.
		return !MWArticleFactory.isEmpty();
	}

	/**
	 * This is where we get the actually interresting data. The revisions hold
	 * the text data. It functions in a very similar fashion as the
	 * computePage() method. Each successfuly parsed revision is added to the
	 * article's list of revisions.
	 * 
	 * @throws NoSuchElementException
	 * @throws XMLStreamException
	 *             If something goes wrong, catched by the computePage() method.
	 */
	private final void computeRevision() throws NoSuchElementException, XMLStreamException {
		boolean endRevision = false;
		StringBuilder textBuilder;
		// Make sure we start on a revision
		nextTag("revision");
		// Move to the first nested tag
		nextOpeningTag(1);
		// Clear the revision factory
		MWRevisionFactory.clear();
		while (!endRevision) {
			switch (MWTag.toTag(reader.getLocalName())) {
				case ID:
					MWRevisionFactory.hasId(getTagText());
					break;
				case TIMESTAMP:
					MWRevisionFactory.hasTimestamp(getTagText());
					break;
				case CONTRIBUTOR:
					// We are only interrested in the user name. This is a
					// nested element of this tag.
					// The user name tag is however optional.
					nextOpeningTag(1);
					if (reader.getLocalName().equals("username")) MWRevisionFactory
							.hasContributor(getTagText());
					break;
				case MINOR:
					// If the tag is <minor /> then it's set to false
					reader.next();
					MWRevisionFactory.isMinor(reader.isEndElement());
					break;
				case COMMENT:
					MWRevisionFactory.hasComment(getTagText());
					break;
				case TEXT:
					// TODO : Tuning the size of the StringBuilder?
					textBuilder = new StringBuilder();
					// While we get characters events, we add the text to the
					// builder.
					reader.next();
					while (reader.isCharacters()) {
						textBuilder.append(reader.getText());
						reader.next();
					}
					textBuilder.trimToSize();
					MWRevisionFactory.hasText(textBuilder.toString());
					endRevision = true;
					break;
				case INVALID_TAG:
					skipThisTag();
				default:
					endRevision = true;
			}
			if (!endRevision) nextOpeningTag(1);
		}
		// Add the revision to the list
		MWArticleFactory.hasRevision(MWRevisionFactory.produceRevision());
	}

	/**
	 * Process the site info. In particular we gather the namespaces and the
	 * associated indexes.
	 * 
	 * @throws MWParseException
	 */
	private final void computeSiteInfo() throws MWParseException {
		// The info that we may gather from the <siteinfo> tag
		// We initialize with default (empty) values.
		String sitename = "";
		String base = "";
		String generator = "";
		String thecase = "";
		final HashMap<Integer, String> namespaces = new HashMap<Integer, String>();
		// A flag
		boolean endSiteInfo = false;

		try {
			nextOpeningTag(1);
			// Iterate through the elements nested in <siteinfo>
			while (!endSiteInfo) {
				// Depending on the tag encountered, process data
				switch (MWTag.toTag(reader.getLocalName())) {
					case SITENAME:
						sitename = getTagText();
						break;
					case BASE:
						base = getTagText();
						break;
					case GENERATOR:
						generator = getTagText();
						break;
					case CASE:
						thecase = getTagText();
						break;
					case NAMESPACES:
						// Some variables
						int nsIndex;
						String nsName;
						nextOpeningTag(1);
						// Compute all the available namespaces
						while (MWTag.toTag(reader.getLocalName()) == MWTag.NAMESPACE) {
							nsIndex = Integer.parseInt(reader.getAttributeValue(0));
							reader.next();
							nsName = nsIndex == 0 ? "" : reader.getText();
							namespaces.put(nsIndex, nsName);
							nextOpeningTag(1);
						}
						endSiteInfo = true;
						break;
					default:
						endSiteInfo = true;
				}
				if (!endSiteInfo) nextOpeningTag(1);
			}
		} catch (final NoSuchElementException e) {
			endOfDocumentReached = true;
		} catch (final XMLStreamException e) {
			endOfDocumentReached = true;
			throw new MWParseException("The parser encountered a malformation in the input file. "
					+ e.getMessage());
		}
		theInfo = new MWSiteinfo(sitename, base, generator, thecase, namespaces);
	}

	/**
	 *  Searches for the next opening tag with the given name.
	 * @param name
	 *            the name of the tag we are looking for
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML
	 *             document.
	 * @throws NoSuchElementException
	 *             If the parser reaches the end of the document unexpectedly
	 */
	private void nextTag(String name) throws XMLStreamException, NoSuchElementException {
		while (!reader.isStartElement() && !reader.getLocalName().equals(name))
			if (reader.hasNext()) reader.next();
			else throw new NoSuchElementException("End of document reached");
	}

	/**
	 * Places the cursor on the nth opening tag following the current cursor
	 * position.
	 * 
	 * @param n
	 *            the number of tags to skip
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML
	 *             document.
	 * @throws NoSuchElementException
	 *             If the parser reaches the end of the document unexpectedly
	 */
	private final void nextOpeningTag(int n) throws XMLStreamException {
		int i = 0;
		while (i < n) {
			if (reader.hasNext()) {
				reader.next();
				if (reader.isStartElement()) ++i;
			} else {
				throw new NoSuchElementException("End of document reached");
			}
		}
	}

	/**
	 * Places the cursor on the ending tag corresponding to the current opening
	 * tag.
	 * 
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML
	 *             document.
	 */
	private final void skipThisTag() throws XMLStreamException {
		// We get the name of the current tag, so we know where to stop
		final String name = reader.getLocalName();
		boolean endOfTag = false;
		while (!endOfTag) {
			if (reader.hasNext()) {
				reader.next();
				if (reader.isEndElement() && reader.getLocalName().equals(name)) endOfTag = true;
			} else {
				throw new NoSuchElementException("End of document reached");
			}
		}
	}

	/**
	 * <p>
	 * Returns the text following the current XML opening tag. This method can
	 * be called only when the cursor is on an opening tag.
	 * <p>
	 * If the current tag is immediately closed (contains no text), the empty
	 * string is returned. Throws an exception if the document ends unexpectedly
	 * (this should not happen if the document is well formed)
	 * <p>
	 * Example : &lt;myOpeningTag&gt; Will return this text
	 * &lt;/myOpeningTag&gt;
	 * 
	 * @return the text following the current opening tag
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML
	 *             document.
	 */
	private final String getTagText() throws XMLStreamException {
		reader.next();
		return (reader.isCharacters()) ? reader.getText() : "";
	}

	/**
	 * This enumeration holds all the tags that we consider in the parsing
	 * process. It's mainly a trick to allow usage of a switch structure. All
	 * the tags that are not recognized, are converted to the single INVALID_TAG
	 * value. This tells the parser that the block must be skipped.
	 */
	enum MWTag {
		// ROOT
		MEDIAWIKI,
		// SITE INFO
		SITEINFO, SITENAME, BASE, GENERATOR, CASE, NAMESPACES, NAMESPACE,
		// PAGES
		PAGE, TITLE, ID,
		// REVISIONS
		REVISION, TIMESTAMP, CONTRIBUTOR, USERNAME, MINOR, COMMENT, TEXT,
		// INVALID, WILL BE SKIPPED
		INVALID_TAG;

		/**
		 * This little method allows the conversion from the String value of the
		 * name to the Integer constant value. All the unknown tags are returned
		 * under as single INVALID_TAG constant.
		 * 
		 * @param tagname
		 *            the name of the tag
		 * @return the constant integer value corresponding to the tag.
		 */
		public static MWTag toTag(String tagname) {
			try {
				return valueOf(tagname.toUpperCase());
			} catch (final Exception e) {
				return INVALID_TAG;
			}
		}
	}

	/**
	 * A dedicated exception class for the parser. Exceptions of this type are
	 * thrown when the parser encounter a severe failure. The message should
	 * then help the user figure out what happend.
	 */
	public class MWParseException extends Exception {
		private static final long	serialVersionUID	= 1L;

		public MWParseException() {
			super();
		}

		public MWParseException(String message) {
			super(message);
		}
	}
}
