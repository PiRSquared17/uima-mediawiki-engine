package uima.wikipedia.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import java.util.NoSuchElementException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import uima.wikipedia.factory.MWArticleFactory;
import uima.wikipedia.factory.MWRevisionFactory;
import uima.wikipedia.types.MWArticle;
import uima.wikipedia.types.MWNamespaceSet;
import uima.wikipedia.types.MWSiteInfo;
import uima.wikipedia.types.MWTag;

public class MWDumpReader {
	// Parser variables
	private final XMLStreamReader		streamReader;
	// Data factory
	protected final MWArticleFactory	thePage;
	protected final MWRevisionFactory	theRevision;
	// Data
	protected MWSiteInfo				theInfo;
	// Other variable
	protected boolean					endOfDocumentReached;
	protected boolean					hasSiteInfo;
	protected boolean					pageComputed;

	public MWDumpReader(XMLStreamReader reader) throws MWParseException {
		// Initialise parser
		try {
			streamReader = reader;
			// Skip the <mediawiki> tag
			nextStartElement(2);
		} catch (XMLStreamException e) {
			throw new MWParseException("An unexpected error occured while starting the parser");
		}
		// Initialise data factory
		// Process website info
		// Check if the <siteinfo> tag is there (it's optional)
		hasSiteInfo = streamReader.getLocalName().toLowerCase().equals("siteinfo");
		if (hasSiteInfo)
			computeSiteInfo();
		// Initialise Article and Revision factorys
		thePage = new MWArticleFactory(theInfo);
		theRevision = new MWRevisionFactory();

		// Some variables
		endOfDocumentReached = false;
		pageComputed = false;
	}

	public MWArticle getPage() {
		pageComputed = false;
		return thePage.newInstance();
	}

	public MWSiteInfo getSiteInfo() {
		return theInfo;
	}

	public final boolean hasSiteInfo() {
		return hasSiteInfo;
	}

	/**
	 * A successful call to this method ensures you that a call to getPage() will succeed as well. The parser tries to
	 * compute a page, taking in account the filters, until it succeeds or reaches the end of the document.
	 * 
	 * @return <code>true</code> if the parser has managed to compute a page; <code>false</code> otherwise
	 */
	public final boolean hasPage() {
		// Clear the page factory
		thePage.clear();
		// Try to compute a page
		pageComputed = false;
		try {
			while (!pageComputed && !endOfDocumentReached)
				pageComputed = computePage();
		} catch (MWParseException e) {
			endOfDocumentReached = true;
			pageComputed = false;
		}
		return pageComputed;
	}

	public final void close() throws XMLStreamException {
		streamReader.close();
	}

	// This method is called only if a call to hasPage() succeeded.
	private final boolean computePage() throws MWParseException {
		boolean endPage = false;
		try {
			nextStartElement(1);
			// Else if we got a page to process
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
						skipThisTag();
						break;
					default:
						endPage = true;
				}
				if (!endPage)
					nextStartElement(1);
			}
		} catch (NoSuchElementException e) {
			endOfDocumentReached = true;
		} catch (XMLStreamException e) {
			endOfDocumentReached = true;
			throw new MWParseException("The parser encountered a malformation in the input file");
		}
		return !thePage.isEmpty();
	}

	private final void computeRevision() throws NoSuchElementException, XMLStreamException {
		StringBuilder textBuilder;
		boolean endRevision = false;

		nextStartElement(1);
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
					nextStartElement(1);
					if (streamReader.getLocalName().equals("username"))
						theRevision.hasContributor(getTagText());
					break;
				case MINOR:
					streamReader.next();
					theRevision.isMinor(streamReader.isEndElement());
					break;
				case COMMENT:
					theRevision.hasComment(getTagText());
					break;
				case TEXT:
					textBuilder = new StringBuilder();
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
				nextStartElement(1);
		}
		thePage.hasRevision(theRevision.newInstance());
	}

	private final void computeSiteInfo() throws MWParseException {
		// The info that we may gather from the <siteinfo> tag
		String sitename = "";
		String base = "";
		String generator = "";
		String thecase = "";
		MWNamespaceSet namespaces = new MWNamespaceSet();
		// A flag
		boolean endSiteInfo = false;

		try {
			nextStartElement(1);
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
						nextStartElement(1);
						// Compute all the available namespaces
						while (MWTag.toTag(streamReader.getLocalName()) == MWTag.NAMESPACE) {
							nsIndex = Integer.parseInt(streamReader.getAttributeValue(0));
							streamReader.next();
							nsName = nsIndex == 0 ? "" : streamReader.getText();
							namespaces.add(nsIndex, nsName);
							nextStartElement(1);
						}
						endSiteInfo = true;
						break;
					default:
						endSiteInfo = true;
				}
				if (!endSiteInfo)
					nextStartElement(1);
			}
		} catch (NoSuchElementException e) {
			endOfDocumentReached = true;
			throw new MWParseException("The parser unexpectedly reached end of document");
		} catch (XMLStreamException e) {
			endOfDocumentReached = true;
			throw new MWParseException("The parser encountered a malformation in the input file");
		}
		theInfo = new MWSiteInfo(sitename, base, generator, thecase, namespaces);
	}

	/**
	 * Places the cursor on the nth opening tag following the current cursor position.
	 * 
	 * @param n
	 *            number of tags to skip
	 * @throws XMLStreamException
	 *             if the parser encounters a document's malformation
	 * @throws NoSuchElementException
	 *             if the parser reaches the end of the document unexpectedly
	 */
	private final void nextStartElement(int n) throws XMLStreamException, NoSuchElementException {
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
	 */
	private final void skipThisTag() throws XMLStreamException {
		String name = streamReader.getLocalName();
		boolean endOfTag = false;
		while (!endOfTag) {
			streamReader.next();
			if (streamReader.getEventType() == END_ELEMENT && streamReader.getLocalName().equals(name))
				endOfTag = true;
		}
	}

	/**
	 * <p>
	 * Returns the text following the current XML opening tag. This method can be called only when the cursor is on an
	 * opening tag.
	 * </p>
	 * <p>
	 * If the current tag is immediately closed (contains no text), the empty string is returned. Throws an exception if
	 * the document ends unexpectedly (this should not happen if the document is well formed)
	 * </p>
	 * <p>
	 * Example : &lt;myOpeningTag&gt; Will return this text &lt;/myOpeningTag&gt;
	 *</p>
	 * 
	 * @return the text following the current opening tag
	 * @throws XMLStreamException
	 */
	private final String getTagText() throws XMLStreamException {
		streamReader.next();
		if (streamReader.getEventType() == CHARACTERS)
			return streamReader.getText();
		return "";
	}
}
