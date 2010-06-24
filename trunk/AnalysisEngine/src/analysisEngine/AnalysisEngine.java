package analysisEngine;

import metaParser.ParsingCoordinator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.wikimodel.wem.WikiParserException;

import uima.wikipedia.types.Article;
import uima.wikipedia.types.Revision;

/**
 * This class aims to recover a raw view of the CAS (RawWikiText) sent by the Collection Reader, analyzed it
 * and add the expected annotations to the default view (
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 * @author Maxime Rihouey <maxime.rihouey@univ-nantes.fr>
 */
public class AnalysisEngine extends JCasAnnotator_ImplBase {

	private ParsingCoordinator	theConverter;

	@Override
	public void process(JCas theCas) throws AnalysisEngineProcessException {
		try {
			// Get the view containing the raw text
			final JCas rawView = theCas.getView("RawWikiText");

			// Initialize the buffer where the text will be stored
			final StringBuilder casContent = new StringBuilder();

			// Get an iterator for the revisions
			final FSIterator<Annotation> iteratorRevision = rawView.getAnnotationIndex(Revision.type).iterator();
			// Some storage variables
			Revision myRevision, newRevision = new Revision(theCas);

			// Iterate over the revision annotations
			while (iteratorRevision.hasNext()) {
				// Get a revision annotation
				myRevision = (Revision) iteratorRevision.next();
				// Get the text
				final String myRevisionText = myRevision.getCoveredText();
				final int start = casContent.length();

				// Initialize the parser
				theConverter = new ParsingCoordinator();
				theConverter.setUp(theCas, start);
				// Parse the raw text
				theConverter.runParser(myRevisionText);

				// Add the parsed text to buffer
				casContent.append(theConverter.getContent());
				final int end = casContent.length();

				// Add the revision annotation
				addRevisionAnnotation(myRevision, newRevision, start, end);
				// Get all the annotations crafted by the parser and add them
				for (final Annotation myAnnotation : theConverter.getAnnotations())
					myAnnotation.addToIndexes();

			}
			// All the textual content is collected now...
			theCas.setDocumentText(casContent.toString());
			// Add the article annotation.
			addArticleAnnotation(theCas, rawView, 0, casContent.length());

		} catch (final CASException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (final WikiParserException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * @param cas
	 *            the JCas we add the annotation to
	 * @param view
	 *            the view we take the old annotation from
	 * @param start
	 *            the start offset of the article
	 * @param end
	 *            the end offset of the argument
	 */
	private void addArticleAnnotation(JCas cas, JCas view, int start, int end) {
		Article oldArticle, newArticle = new Article(cas);
		final FSIterator<Annotation> iterateurArticle = view.getAnnotationIndex(Article.type).iterator();

		// We iterate over the Article annotations, there should be only one.
		while (iterateurArticle.hasNext()) {
			// Get the annotation
			oldArticle = (Article) iterateurArticle.next();
			// Copy it to a new one
			newArticle.setId(oldArticle.getId());
			newArticle.setNamespace(oldArticle.getNamespace());
			newArticle.setPrefix(oldArticle.getPrefix());
			newArticle.setTitle(oldArticle.getTitle());
			newArticle.setBegin(start);
			newArticle.setEnd(end);
			// Add it
			newArticle.addToIndexes();
		}
	}

	/**
	 * @param oldRevision
	 *            the old revision in the raw view
	 * @param newRevision
	 *            the new revision in the processed view
	 * @param start
	 *            the start offset
	 * @param end
	 *            the end offset
	 */
	private void addRevisionAnnotation(Revision oldRevision, Revision newRevision, int start, int end) {
		// Craft the revision annotation
		newRevision.setBegin(start);
		newRevision.setEnd(end);
		newRevision.setComment(oldRevision.getComment());
		newRevision.setId(oldRevision.getId());
		newRevision.setIsMinor(oldRevision.getIsMinor());
		newRevision.setUser(oldRevision.getUser());
		// Add it to the indexes
		newRevision.addToIndexes();
	}
}
