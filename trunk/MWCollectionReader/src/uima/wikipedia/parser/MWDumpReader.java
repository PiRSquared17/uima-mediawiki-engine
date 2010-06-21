package uima.wikipedia.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import uima.wikipedia.factory.MWArticleFactory;
import uima.wikipedia.factory.MWRevisionFactory;
import uima.wikipedia.types.MWArticle;
import uima.wikipedia.types.MWSiteinfo;

/**
 * This class is the core of the Collection reader component. It's dedicated to extract the relevant data from
 * the XML stream. I tried to keep things as simple and robust as possible.
 * <p>
 * I make use of the StAX API to provide a pull-parsing style parser. The basic unit you can get from that
 * parser is a page, also known as an article. You can also get the Siteinfo information (especially the
 * namespaces) if it's present.
 * 
 * @see uima.wikipedia.types.MWSiteinfo
 * @see uima.wikipedia.types.MWArticle
 * @see uima.wikipedia.types.MWRevision
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWDumpReader {
	/** Parser variables */
	private final XMLStreamReader		streamReader;
	/** Data factories */
	protected final MWArticleFactory	thePage;
	protected final MWRevisionFactory	theRevision;
	/** Data */
	protected MWSiteinfo				theInfo;
	/** Some flags */
	protected boolean					endOfDocumentReached;
	protected boolean					hasSiteInfo;
	protected boolean					pageComputed;

	/**
	 * Initializes the parser. In particular, it skips a few unuseful blocks at the beginning and tries to
	 * compute the Siteinfo right away. It also initializes the data factories.
	 * 
	 * @param reader
	 *            An XML stream reader from which we get the data
	 * @throws MWParseException
	 *             Exception is thrown if the underlying XML document is malformed.
	 */
	public MWDumpReader(XMLStreamReader reader) throws MWParseException {
		// Initialise parser
		try {
			streamReader = reader;
			// Skip the <mediawiki> tag
			nextOpeningTag(2);
		} catch (final XMLStreamException e) {
			throw new MWParseException("An unexpected error occured while starting the parser");
		}
		// Initialise data factories
		// Process website info
		// Check if the <siteinfo> tag is there (it's optional)
		hasSiteInfo = streamReader.getLocalName().toLowerCase().equals("siteinfo");
		if (hasSiteInfo)
			computeSiteInfo();
		// Initialise Article and Revision factorys
		thePage = new MWArticleFactory(theInfo);
		theRevision = new MWRevisionFactory();

		// Some flags
		endOfDocumentReached = false;
		pageComputed = false;
	}

	/**
	 * This method returns the last computed page. It should only be used after a successful call to the
	 * {@link #hasPage()} method.
	 * 
	 * @return the last computed page
	 * @see uima.wikipedia.types.MWArticle
	 */
	public MWArticle getPage() {
		pageComputed = false;
		return thePage.newInstance();
	}

	/**
	 * This method return the site info (containing namespaces in particular). It should only be used after a
	 * successful call to {@link #hasSiteInfo()}. Otherwise, the fields of the returned object will have a
	 * default value.
	 * 
	 * @return the site info
	 * @see uima.wikipedia.types.MWSiteinfo
	 */
	public MWSiteinfo getSiteInfo() {
		return theInfo;
	}

	/**
	 * A successful call to this method ensures you that a call to {@link #getSiteInfo()} will return relevant
	 * information.
	 * 
	 * @return <code>true</code> if the parser has managed to compute the site info; <code>false</code>
	 *         otherwise.
	 */
	public final boolean hasSiteInfo() {
		return hasSiteInfo;
	}

	/**
	 * A successful call to this method ensures you that a call to {@link #getPage()} will succeed as well.
	 * The parser tries to compute a page, taking in account the filters, until it succeeds to compute one or
	 * reaches the end of the document.
	 * 
	 * @return <code>true</code> if the parser has managed to compute a page; <code>false</code> otherwise.
	 */
	public final boolean hasPage() {
		// Clear the page factory
		thePage.clear();
		// Try to compute a page
		pageComputed = false;
		try {
			while (!pageComputed && !endOfDocumentReached)
				pageComputed = computePage();
		} catch (final MWParseException e) {
			endOfDocumentReached = true;
			pageComputed = false;
		}
		return pageComputed;
	}

	/**
	 * Use this method to free the ressources the parser was using (meaning the XML stream) in a clean way.
	 * 
	 * @throws XMLStreamException
	 *             If the XML stream fails to be closed.
	 */
	public final void close() {
		try {
			streamReader.close();
		} catch (XMLStreamException e) {
			// If the closing fails, the GC will just have to deal with it.
		}
	}

	/**
	 * This method is normally called only if a call to hasPage() succeeded. It returns true when the parsing
	 * of the page is successful, meaning no malformation were encountered and at least one revision was kept.
	 * It returns false otherwise.
	 * 
	 * @return <code>true</code> on the successful parsing of a page, <code>false</code> otherwise.
	 * @throws MWParseException
	 *             If a malformation is encountered in the underlying XML stream
	 */
	private final boolean computePage() throws MWParseException {
		boolean endPage = false;
		try {
			nextOpeningTag(1);
			// While we don't reach the end of the page, we compute the data we find.
			while (!endPage) {
				switch (MWTag.toTag(streamReader.getLocalName())) {
					case TITLE:
						thePage.hasTitle(getTagText());
						break;
					case ID:
						thePage.hasId(getTagText());
						break;
					case REVISION:
						computeRevision();
						break;
					case INVALID_TAG:
						// If we find an unrelevant tag, we skip it.
						skipThisTag();
						break;
					// When we hit a page tag again, we know we are done with this one.
					case PAGE:
						endPage = true;
						break;
					default:
						endPage = true;
				}
				if (!endPage)
					nextOpeningTag(1);
			}
		} catch (final NoSuchElementException e) {
			// If we reach the end of the document
			endOfDocumentReached = true;
		} catch (final XMLStreamException e) {
			// If we encounter a malformation of some sort
			endOfDocumentReached = true;
			throw new MWParseException("The parser encountered a malformation in the input file. " + e.getMessage());
		}
		// The page is considered empty when it holds no revisions.
		// This can happen when some filters are set.
		return !thePage.isEmpty();
	}

	/**
	 * This is where we get the actually interresting data. The revisions hold the text data. It functions in
	 * a very similar fashion as the computePage() method. Each successfuly parsed revision is added to the
	 * article's list of revisions.
	 * 
	 * @throws NoSuchElementException
	 * @throws XMLStreamException
	 *             If something goes wrong, catched by the computePage() method.
	 */
	private final void computeRevision() throws NoSuchElementException, XMLStreamException {
		StringBuilder textBuilder;
		boolean endRevision = false;

		nextOpeningTag(1);
		// Clear the revision factory
		theRevision.clear();
		while (!endRevision) {
			switch (MWTag.toTag(streamReader.getLocalName())) {
				case ID:
					theRevision.hasId(getTagText());
					break;
				case TIMESTAMP:
					theRevision.hasTimestamp(getTagText());
					break;
				case CONTRIBUTOR:
					// This is actully a nested element. We are only interrested in the user name.
					// The user name tag is however optional.
					nextOpeningTag(1);
					if (streamReader.getLocalName().equals("username"))
						theRevision.hasContributor(getTagText());
					break;
				case MINOR:
					// If the tag is <minor /> then it's set to false
					streamReader.next();
					theRevision.isMinor(streamReader.isEndElement());
					break;
				case COMMENT:
					theRevision.hasComment(getTagText());
					break;
				case TEXT:
					// TODO : Tuning the size of the StringBuilder?
					textBuilder = new StringBuilder();
					// While we get characters events, we add the text to the builder.
					do
						textBuilder.append(getTagText());
					while (streamReader.getEventType() == CHARACTERS);
					textBuilder.trimToSize();
					theRevision.hasText(textBuilder.toString());
					endRevision = true;
					break;
				case INVALID_TAG:
					skipThisTag();
				default:
					endRevision = true;
			}
			if (!endRevision)
				nextOpeningTag(1);
		}
		// Add the revision to the list
		thePage.hasRevision(theRevision.newInstance());
	}

	/**
	 * Process the site info. In particular we gather the namespaces and the associated indexes.
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
		HashMap<Integer, String> namespaces = new HashMap<Integer, String>();
		// A flag
		boolean endSiteInfo = false;

		try {
			nextOpeningTag(1);
			// Iterate through the elements nested in <siteinfo>
			while (!endSiteInfo) {
				// Depending on the tag encountered, process data
				switch (MWTag.toTag(streamReader.getLocalName())) {
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
						while (MWTag.toTag(streamReader.getLocalName()) == MWTag.NAMESPACE) {
							nsIndex = Integer.parseInt(streamReader.getAttributeValue(0));
							streamReader.next();
							nsName = nsIndex == 0 ? "" : streamReader.getText();
							namespaces.put(nsIndex, nsName);
							nextOpeningTag(1);
						}
						endSiteInfo = true;
						break;
					default:
						endSiteInfo = true;
				}
				if (!endSiteInfo)
					nextOpeningTag(1);
			}
		} catch (final NoSuchElementException e) {
			endOfDocumentReached = true;
			throw new MWParseException("The parser unexpectedly reached end of document. " + e.getMessage());
		} catch (final XMLStreamException e) {
			endOfDocumentReached = true;
			throw new MWParseException("The parser encountered a malformation in the input file. " + e.getMessage());
		}
		theInfo = new MWSiteinfo(sitename, base, generator, thecase, namespaces);
	}

	/**
	 * Places the cursor on the nth opening tag following the current cursor position.
	 * 
	 * @param n
	 *            the number of tags to skip
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML document.
	 * @throws NoSuchElementException
	 *             If the parser reaches the end of the document unexpectedly
	 */
	private final void nextOpeningTag(int n) throws XMLStreamException, NoSuchElementException {
		int i = 0;
		while (i < n) {
			streamReader.next();
			if (streamReader.isStartElement())
				++i;
		}
	}

	/**
	 * Places the cursor on the ending tag corresponding to the current opening tag.
	 * 
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML document.
	 */
	private final void skipThisTag() throws XMLStreamException {
		final String name = streamReader.getLocalName();
		boolean endOfTag = false;
		while (!endOfTag) {
			streamReader.next();
			if (streamReader.getEventType() == END_ELEMENT && streamReader.getLocalName().equals(name))
				endOfTag = true;
		}
	}

	/**
	 * <p>
	 * Returns the text following the current XML opening tag. This method can be called only when the cursor
	 * is on an opening tag.
	 * <p>
	 * If the current tag is immediately closed (contains no text), the empty string is returned. Throws an
	 * exception if the document ends unexpectedly (this should not happen if the document is well formed)
	 * <p>
	 * Example : &lt;myOpeningTag&gt; Will return this text &lt;/myOpeningTag&gt;
	 * 
	 * @return the text following the current opening tag
	 * @throws XMLStreamException
	 *             If encountering a malformation in the underlying XML document.
	 */
	private final String getTagText() throws XMLStreamException {
		streamReader.next();
		if (streamReader.getEventType() == CHARACTERS)
			return streamReader.getText();
		return "";
	}

	/**
	 * This enumeration holds all the tags that we consider in the parsing process. It's mainly a trick to
	 * allow usage of a switch structure. All the tags that are not recognized, are converted to the single
	 * INVALID_TAG value. This tells the parser that the block must be skipped.
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
		// INVALID
		INVALID_TAG;

		/**
		 * This little method allows the conversion from the String value of the name to the Integer constant
		 * value. All the unknown tags are returned under as single INVALID_TAG constant.
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
	 * A dedicated exception class for the parser. Exceptions of this type are thrown when the parser
	 * encounter a severe failure. The message should then help the user figure out what happend.
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
