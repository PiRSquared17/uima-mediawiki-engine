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

	public static void initialize(JCas cas, String rawViewName) throws CASException {
		// Initialise the CAS
		main = cas;
		rawTextView = main.getView(rawViewName);
		// Initialize the parser
		parser = new MarkupParser(new MWLanguage());
	}

	public static void parseRevisions() {
		// An iterator over the revision annotations.
		FSIterator<Annotation> revisionIterator = rawTextView.getAnnotationIndex(Revision.type).iterator();

		while (revisionIterator.hasNext()) {
			// Instanciate a new builder
			revision = new MWRevisionBuilder();
			// Parse the revision
			parser.parse(revisionIterator.next().getCoveredText());
			// Set the revision annotation
			setAnnotations(revision.getAnnotations());
		}
	}

	public static void finalizeCAS() {

	}

	private static void setAnnotations(List<Annotation> annotations) {
		// TODO Auto-generated method stub

	}
}
