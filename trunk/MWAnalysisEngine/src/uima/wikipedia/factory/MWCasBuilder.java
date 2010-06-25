package uima.wikipedia.factory;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

import uima.wikipedia.parser.MWLanguage;
import uima.wikipedia.parser.MWRevisionBuilder;
import uima.wikipedia.types.Article;
import uima.wikipedia.types.Revision;

public class MWCasBuilder {
	/** The main CAS we will be working on */
	private static JCas					main;
	/** The view of the cas containing the raw wiki text */
	private static JCas					rawView;
	/** The name of the view containing the raw wiki text */
	private static String				rawViewName;
	/** The mediawiki parser */
	private static MarkupParser			parser;
	/** The language we are parsing */
	private static MWLanguage			language;
	/** A flag indicating if we need to consider macros */
	private static boolean				enableMacros;
	/** The revision builder */
	private static MWRevisionBuilder	revision;
	/** A string builder for the CAS content */
	private static StringBuilder		content;

	/**
	 * Initializes the factory with a new CAS to process.
	 * 
	 * @param rawViewName
	 *            the name of the view where the raw text is located
	 * @throws CASException
	 *             If something goes wrong
	 */
	public static void initialize(String rawViewName, boolean enableMacros) throws CASException {
		// Initialize some parameters
		MWCasBuilder.rawViewName = rawViewName;
		MWCasBuilder.enableMacros = enableMacros;
		// Initialize the parser
		language = new MWLanguage();
		language.setEnableMacros(enableMacros);
		if (enableMacros)
			configureMacros();
		// Others initialization
		content = new StringBuilder();
	}

	/**
	 * Process the CAS content.
	 * <p>
	 * It gathers the revision annotations from the raw view, parses the text, gather the content annotation
	 * and adds the result to the CAS.
	 * 
	 * @throws CASException
	 *             if something goes wrong
	 */
	public static void build(JCas cas) throws CASException {
		// Initialize the CAS
		main = cas;
		rawView = main.getView(rawViewName);
		// An iterator over the revision annotations.
		FSIterator<Annotation> revisionIterator = rawView.getAnnotationIndex(Revision.type).iterator();

		while (revisionIterator.hasNext()) {
			Revision rawRevision = (Revision) revisionIterator.next();
			// Start of the annotation
			int start = content.length();
			// Instanciate a new builder
			revision = new MWRevisionBuilder(main);
			parser.setBuilder(revision);
			// Parse the revision
			parser.parse(rawRevision.getCoveredText());
			// Add the revision's text to the CAS content
			content.append(revision.getText());
			int end = content.length();
			// Add a new revision annotation
			addRevisionAnnotation(rawRevision, start, end);
			// Add the content relative annotations
			for (Annotation a : revision.getAnnotations())
				a.addToIndexes();
		}
		// Finalize the CAS processing.
		finalizeCAS();
	}

	/**
	 * Finalize the CAS processing. In particular it adds the Article annotation.
	 */
	private static void finalizeCAS() {
		// Add the document text.
		main.setDocumentText(content.toString());
		// Add the article annotation.
		Article parsedArticle = new Article(main);
		FSIterator<Annotation> articleIterator = rawView.getAnnotationIndex(Article.type).iterator();

		// We iterate over the Article annotations, there should be only one.
		while (articleIterator.hasNext()) {
			// Get the annotation
			Article rawArticle = (Article) articleIterator.next();
			// Collect information
			parsedArticle.setId(rawArticle.getId());
			parsedArticle.setNamespace(rawArticle.getNamespace());
			parsedArticle.setPrefix(rawArticle.getPrefix());
			parsedArticle.setTitle(rawArticle.getTitle());
			parsedArticle.setBegin(0);
			parsedArticle.setEnd(content.length());
			// Add it to the index
			parsedArticle.addToIndexes();
		}
	}

	private static void configureMacros() {
		// TODO Auto-generated method stub

	}

	private static void addRevisionAnnotation(Revision rawRevision, int start, int end) {
		// Create a new revision
		Revision parsedRevision = new Revision(main);
		// Collect information
		parsedRevision.setBegin(start);
		parsedRevision.setEnd(end);
		parsedRevision.setId(rawRevision.getId());
		parsedRevision.setUser(rawRevision.getUser());
		parsedRevision.setComment(rawRevision.getComment());
		parsedRevision.setIsMinor(rawRevision.getIsMinor());
		// Add it to the index.
		parsedRevision.addToIndexes();
	}
}
