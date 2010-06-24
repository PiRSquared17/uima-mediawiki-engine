package uima.wikipedia.factory;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uima.wikipedia.types.Revision;

public class MWCasFactory {
	/** The main CAS we will be working on */
	private static JCas	main;
	/** The view of the cas containing the raw wiki text */
	private static JCas	rawTextView;

	public static void initialize(JCas cas, String rawViewName) throws CASException {
		main = cas;
		rawTextView = main.getView(rawViewName);
	}

	public static void parseRevisions() {
		// An iterator over the revision annotations.
		FSIterator<Annotation> revisionIterator = rawTextView.getAnnotationIndex(Revision.type).iterator();
		
		while(revisionIterator.hasNext()) {
			// Get the revision text
			String text = revisionIterator.next().getCoveredText();
			
		}
	}
}
