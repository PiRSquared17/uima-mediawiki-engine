package uima.wikipedia.factory;

import java.util.List;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

import uima.wikipedia.parser.MWLanguage;
import uima.wikipedia.parser.MWRevisionBuilder;
import uima.wikipedia.types.Revision;

public class MWCasFactory {
	/** The main CAS we will be working on */
	private static JCas					main;
	/** The view of the cas containing the raw wiki text */
	private static JCas					rawTextView;
	/** The mediawiki parser */
	private static MarkupParser			parser;
	/** The revision builder */
	private static MWRevisionBuilder	revision;
	/** A string builder for the CAS content */
	private static StringBuilder		content;

	public static void initialize(JCas cas, String rawViewName) throws CASException {
		// Initialise the CAS
		main = cas;
		rawTextView = main.getView(rawViewName);
		// Initialize the parser
		parser = new MarkupParser(new MWLanguage());
		// Others
		content = new StringBuilder();
	}

	public static void parseRevisions() {
		// An iterator over the revision annotations.
		FSIterator<Annotation> revisionIterator = rawTextView.getAnnotationIndex(Revision.type).iterator();

		while (revisionIterator.hasNext()) {
			Revision rawRevision = (Revision) revisionIterator.next();
			// Start of the annotation
			int start = content.length();
			// Instanciate a new builder
			revision = new MWRevisionBuilder();
			// Parse the revision
			parser.parse(rawRevision.getCoveredText());
			// Add the revision's text to the CAS content
			content.append(revision.getText());
			int end = content.length();
			// Create a new revision annotation
			craftRevisionAnnotation(rawRevision, start, end);
			// Gather the revision's content annotations
			gatherAnnotations(revision.getAnnotations());
		}
	}

	private static void craftRevisionAnnotation(Revision rawRevision, int start, int end) {
		// TODO Create the new revision annotation (id, user, ...)

	}

	public static void finalizeCAS() {
		// TODO : Add the article annotation
	}

	private static void gatherAnnotations(List<Annotation> annotations) {
		// TODO : Handle the annotations

	}
}
