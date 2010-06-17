package analysisEngine;

// Java dependencies
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.wikimodel.wem.IWemListener;
import org.wikimodel.wem.WikiFormat;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.WikiParserException;
import org.wikimodel.wem.WikiReference;
import org.wikimodel.wem.mediawiki.MediaWikiParser;

import uima.wikipedia.types.Header;
import uima.wikipedia.types.Link;
import uima.wikipedia.types.Paragraph;
import uima.wikipedia.types.Section;

/**
 * This class aims at converting the Mediawiki revisions content into something close to a CAS structure : - extract
 * plain text data - save annotations indexes in order to produce a CAS.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class ParsingCoordinator implements IWemListener {

	/** references types */
	public static String			REFERENCE_IMAGE	= "Image:";
	public static String			REFERENCE_WIKI	= "w:";

	/** parsing related variables */
	private final MediaWikiParser	theParser;

	/** content related variables */
	private StringBuffer			theTextContent;
	private Integer					theOffset;

	/** collected annotations */
	private JCas					theCas;
	private ArrayList<Header>		theHeadersAnnotations;
	private ArrayList<Link>			theLinksAnnotations;
	private ArrayList<Section>		theUnclosedSections;
	private ArrayList<Section>		theClosedSections;
	private ArrayList<Paragraph>	theParagraphAnnotations;

	/** states variables */
	private Integer					theListLevel;

	/**
	 * Constructor. It initializes the MediaWiki parser.
	 * 
	 * @param stringsource
	 */
	public ParsingCoordinator() {
		// Initialize the parser
		theParser = new MediaWikiParser();
	}

	/**
	 * This method resets all the data in the converter instance in order to process a new file. It takes in parameter
	 * an offset corresponding to the index at which the text will be inserted in the JCas.
	 * 
	 * @param offset
	 *            the text offset
	 */
	public void setUp(JCas mCas, int offset) {
		// General
		theCas = mCas;
		theOffset = offset;
		theTextContent = new StringBuffer();
		// Annotations
		theHeadersAnnotations = new ArrayList<Header>();
		theLinksAnnotations = new ArrayList<Link>();
		theUnclosedSections = new ArrayList<Section>();
		theClosedSections = new ArrayList<Section>();
		theParagraphAnnotations = new ArrayList<Paragraph>();
		// Parsing related data
		theListLevel = 0;
	}

	/**
	 * This method launches the parsing of the raw wiki text passed in parameter. From this point, the text will be
	 * parsed, analysed and the annotations will be prepared.
	 * 
	 * @param rawWikitext
	 * @throws IOException
	 */
	public void runParser(String rawWikiText) throws WikiParserException {
		// We use a string reader to parse the raw wiki texte
		final StringReader reader = new StringReader(rawWikiText);
		// Parsing
		theParser.parse(reader, this);
	}

	public String getContent() {
		return theTextContent.toString();
	}

	/**
	 * This method returns a list with all the annotations we have collected during the processing.
	 * 
	 * @return
	 */
	public ArrayList<Annotation> getAnnotations() {
		final ArrayList<Annotation> list = new ArrayList<Annotation>();
		// Add headers annotations
		for (final Annotation a : theHeadersAnnotations)
			list.add(a);
		// Add links annotations
		for (final Annotation a : theLinksAnnotations)
			list.add(a);
		// Close all sections
		for (final Section s : theUnclosedSections) {
			s.setEnd(theOffset);
			theClosedSections.add(s);
		}
		theUnclosedSections.clear();
		// Add closed sections
		for (final Annotation a : theClosedSections)
			list.add(a);
		// Add paragraphs
		for (final Annotation a : theParagraphAnnotations)
			list.add(a);
		return list;
	}

	// Higher level API --------------------------------------------------------

	/**
	 * This method adds the string in parameter into the collected content and then increment the offset by the size of
	 * this string.
	 * 
	 * @param str
	 *            the string to be added to the content
	 */
	protected void addToContent(String str) {
		if (str != null) {
			theTextContent.append(str);
			theOffset += str.length();
		}
	}

	/**
	 * This method checks if the reference in parameter is a reference to an image.
	 * 
	 * @param ref
	 *            the reference to check
	 * @return true if the reference is a reference to an image
	 */
	protected boolean isImageRef(WikiReference ref) {
		return ref.getLink().startsWith(REFERENCE_IMAGE);
	}

	/**
	 * This method checks if the reference in parameter is a reference to a wiki page.
	 * 
	 * @param ref
	 *            the reference to check
	 * @return true if the reference is a reference to a wiki page
	 */
	protected boolean isWikiRef(WikiReference ref) {
		return ref.getLink().startsWith(REFERENCE_WIKI);
	}

	// IWemListener API --------------------------------------------------------

	// Text related hooks

	/** Called when something is escaped */
	public void onEscape(String str) {
		addToContent(str);
	}

	/** Called when a space is encountered */
	public void onSpace(String str) {
		addToContent(str);
	}

	/** Called when a special symbol is encountered */
	public void onSpecialSymbol(String str) {
		addToContent(str);
	}

	/** Called when a word is encountered */
	public void onWord(String str) {
		addToContent(str);
	}

	/** Called when a line break is encountered */
	public void onLineBreak() {
		addToContent("\n");
	}

	/** Called when a new line is started */
	public void onNewLine() {
		addToContent("\n");
	}

	/** Called when an empty line is encountered */
	public void onEmptyLines(int count) {
		addToContent("\n");
	}

	/** Ignore horizontal rules */
	public void onHorizontalLine(WikiParameters params) {
	}

	// Links related hooks

	/**
	 * When a string is found, we transform it into a WikiReference and call the method for a wiki reference.
	 */
	public void onReference(String str) {
		onReference(new WikiReference(str));
	}

	/**
	 * When a reference is found, we prepare a link annotation and add the label into the content.
	 */
	public void onReference(WikiReference ref) {
		if (isImageRef(ref)) {
			// we ignore images
			// TODO : make it configurable
		} else {
			// Create the link annotation
			final Link link = new Link(theCas);
			link.setBegin(theOffset);
			link.setLabel(ref.getLabel());
			link.setLink(ref.getLink());
			// Add the label in the content
			addToContent(ref.getLabel());
			// Deduce the annotation ending
			link.setEnd(theOffset);
			// Add the annotation to the list
			theLinksAnnotations.add(link);
		}
	}

	// Sections and Headers related hooks

	/**
	 * When we encounter a new header, we create an annotation for it.
	 */
	public void beginHeader(int headerLevel, WikiParameters params) {
		// Jump a line
		addToContent("\n\n");
		// Create the annotation
		final Header header = new Header(theCas);
		header.setLevel(headerLevel);
		header.setBegin(theOffset);
		// Add it to the list
		theHeadersAnnotations.add(header);
		// Add it as header of the last unclosed section
		if (theUnclosedSections.size() > 0) {
			final Section section = theUnclosedSections.get(theUnclosedSections.size() - 1);
			section.setTitle(header);
		}
	}

	/**
	 * Add the ending value of the last started header.
	 */
	public void endHeader(int headerLevel, WikiParameters params) {
		// Retrieve the last header
		final Header header = theHeadersAnnotations.get(theHeadersAnnotations.size() - 1);
		// Update its ending value
		header.setEnd(theOffset);
		// Jump a line
		addToContent("\n\n");
	}

	/**
	 * When we encounter a new section, we create an annotation for it.
	 */
	public void beginSection(int docLevel, int headerLevel, WikiParameters params) {
		// Create the annotation
		final Section section = new Section(theCas);
		section.setBegin(theOffset);
		section.setLevel(headerLevel);
		// The parent section is the last non closed one
		if (theUnclosedSections.size() > 0)
			section.setParent(theUnclosedSections.get(theUnclosedSections.size() - 1));
		// The next encounered header will be set as title
		// Add it to the list
		theUnclosedSections.add(section);
	}

	/**
	 * When a section ends, we set its ending and move it to the ClosedSections list.
	 */
	public void endSection(int docLevel, int headerLevel, WikiParameters params) {
		// Retrieve the last unclosed section
		final Section section = theUnclosedSections.get(theUnclosedSections.size() - 1);
		// Close it
		section.setEnd(theOffset);
		theUnclosedSections.remove(theUnclosedSections.size() - 1);
		theClosedSections.add(section);
	}

	public void beginSectionContent(int docLevel, int headerLevel, WikiParameters params) {
	}

	public void endSectionContent(int docLevel, int headerLevel, WikiParameters params) {
	}

	// Paragraph and InfoBlocks related hooks

	public void beginInfoBlock(String infoType, WikiParameters params) {
	}

	public void endInfoBlock(String infoType, WikiParameters params) {
	}

	/**
	 * When we encounter a new paragraph, we create an annotation for it.
	 */
	public void beginParagraph(WikiParameters params) {
		// Create the annotation
		final Paragraph para = new Paragraph(theCas);
		para.setBegin(theOffset);
		// Add it to the list
		theParagraphAnnotations.add(para);
	}

	/**
	 * We set the ending of the lastly added paragraph
	 */
	public void endParagraph(WikiParameters params) {
		final Paragraph para = theParagraphAnnotations.get(theParagraphAnnotations.size() - 1);
		para.setEnd(theOffset);
	}

	// Lists related hooks

	/** When a new list is started we increase the list level */
	public void beginList(WikiParameters params, boolean ordered) {
		// Add a list level
		theListLevel += 1;
		// Jump to a new line
		addToContent("\n");
	}

	/** When a list is ended we decrease the list level */
	public void endList(WikiParameters params, boolean ordered) {
		theListLevel -= 1;
	}

	/** We begin an item by a bullet */
	public void beginListItem() {
		for (int i = 0; i < theListLevel - 1; i++)
			addToContent("\t");
		addToContent("* ");
	}

	/** ... and end it with a line jump */
	public void endListItem() {
		addToContent("\n");
	}

	// Blocks related hooks

	public void onVerbatimBlock(String str, WikiParameters params) {
		addToContent("\n" + str + "\n");
	}

	public void onVerbatimInline(String str, WikiParameters params) {
		addToContent(str);
	}

	public void beginFormat(WikiFormat format) {
		addToContent("\n");
	}

	public void endFormat(WikiFormat format) {
		addToContent("\n");
	}

	public void beginDefinitionDescription() {
		addToContent("\n");
	}

	public void endDefinitionDescription() {
		addToContent("\n");
	}

	public void beginDefinitionList(WikiParameters params) {
		addToContent("\n");
	}

	public void endDefinitionList(WikiParameters params) {
		addToContent("\n");
	}

	public void beginDefinitionTerm() {
		addToContent("\t");
	}

	public void endDefinitionTerm() {
		addToContent("\n");
	}

	public void beginQuotation(WikiParameters params) {
		addToContent("\n");
	}

	public void endQuotation(WikiParameters params) {
		addToContent("\n");
	}

	public void beginQuotationLine() {
		addToContent("\t");
	}

	public void endQuotationLine() {
		addToContent("\n");
	}

	// Tables related hooks

	public void beginTable(WikiParameters params) {
	}

	public void beginTableCell(boolean tableHead, WikiParameters params) {
	}

	public void beginTableRow(WikiParameters params) {
	}

	public void endTable(WikiParameters params) {
	}

	public void endTableCell(boolean tableHead, WikiParameters params) {
	}

	public void endTableRow(WikiParameters params) {
	}

	public void onTableCaption(String str) {
	}

	// Some hooks we ignore

	public void onImage(String ref) {
	}

	public void onImage(WikiReference ref) {
	}

	public void beginDocument(WikiParameters params) {
	}

	public void endDocument(WikiParameters params) {
	}

	public void beginPropertyBlock(String propertyUri, boolean doc) {
	}

	public void endPropertyBlock(String propertyUri, boolean doc) {
	}

	public void beginPropertyInline(String propertyUri) {
	}

	public void endPropertyInline(String propertyUri) {
	}

	public void onExtensionBlock(String extensionName, WikiParameters params) {
	}

	public void onExtensionInline(String extensionName, WikiParameters params) {
	}

	public void onMacroBlock(String macroName, WikiParameters params, String content) {
	}

	public void onMacroInline(String macroName, WikiParameters params, String content) {
	}
}
