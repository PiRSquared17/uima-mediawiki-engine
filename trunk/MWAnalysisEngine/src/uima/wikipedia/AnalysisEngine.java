package uima.wikipedia;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uima.wikipedia.factory.MWCasFactory;
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

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		try {
			MWCasFactory.initialize(cas, "RawWikiText");
			MWCasFactory.parseRevisions();
			MWCasFactory.finalizeCAS();
		} catch (CASException e) {
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
