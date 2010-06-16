package analysisEngine;

import java.util.ArrayList;

import metaParser.ParsingCoordinator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.wikimodel.wem.WikiParserException;

import uima.wikipedia.types.Article;
import uima.wikipedia.types.Revision;

/**
 * This class aims to recover a raw view of the CAS (RawWikiText) sent by the Collection Reader, analyzed it and add the
 * expected annotations to the default view (
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 * @author Maxime Rihouey <maxime.rihouey@univ-nantes.fr>
 */
public class AnalysisEngine extends JCasAnnotator_ImplBase {

	private ParsingCoordinator		theConverter;
	protected ArrayList<Revision>	theRevisions	= null;
	int								cpt				= 0;

	@Override
	public void process(JCas newcas) throws AnalysisEngineProcessException {
		cpt++;
		System.out.println(cpt);
		try {
			// view containing text to parse
			final JCas rawView = newcas.getView("RawWikiText");

			// Initialize the buffer where the text will be stored
			final StringBuffer casContent = new StringBuffer();

			// setup the iteration on revisions
			final FSIterator<Annotation> iteratorRevision = rawView.getAnnotationIndex(Revision.type).iterator();
			Revision myRevision;

			// will store the parsed revision
			final uima.wikipedia.types.Revision newRevision = new uima.wikipedia.types.Revision(newcas);

			// Create all the revision annotations
			while (iteratorRevision.hasNext()) {

				myRevision = (Revision) iteratorRevision.next();

				// the text to parse
				// System.out.println("Indexes de myRevision : " + myRevision.getBegin() + "," + myRevision.getEnd());
				// System.out.println("Taille du texte : " + rawView.getDocumentText().length());
				final String myRevisionText = myRevision.getCoveredText();

				// save the starting point to make the revision annotation
				final Integer start = casContent.length();

				// initialize the converter
				theConverter = new ParsingCoordinator();
				theConverter.setUp(newcas, start);
				// parse the text
				theConverter.runParser(myRevisionText);

				// add parsed text to buffer
				casContent.append(theConverter.getContent());
				final Integer end = casContent.length();

				// add the revision annotation
				newRevision.setBegin(start);
				newRevision.setEnd(end);
				newRevision.setComment(myRevision.getComment());
				newRevision.setId(myRevision.getId());
				newRevision.setIsMinor(myRevision.getIsMinor());
				newRevision.setUser(myRevision.getUser());
				newRevision.addToIndexes();

				// recover and set annotations
				for (final Annotation myAnnotation : theConverter.getAnnotations()) {
					myAnnotation.addToIndexes();
				}

			}

			// All the textual content is collected now...
			newcas.setDocumentText(casContent.toString());

			// We can add the Article annotations...
			final DocumentAnnotation newDocument = new DocumentAnnotation(newcas);
			newDocument.setLanguage("unknown");
			newDocument.setBegin(0);
			newDocument.setEnd(casContent.length() - 1);
			newDocument.addToIndexes();

			// ... and the Article annotations using an iterator
			final FSIterator<Annotation> iterateurArticle = rawView.getAnnotationIndex(Article.type).iterator();
			Article myArticle;
			final Article newArticle = new Article(newcas);
			while (iterateurArticle.hasNext()) {
				myArticle = (Article) iterateurArticle.next();
				newArticle.setId(myArticle.getId());
				newArticle.setNamespace(myArticle.getNamespace());
				newArticle.setPrefix(myArticle.getPrefix());
				newArticle.setTitle(myArticle.getTitle());
				newArticle.setBegin(myArticle.getBegin());
				newArticle.setEnd(myArticle.getEnd());
				newArticle.addToIndexes();
			}

		} catch (final CASException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (final WikiParserException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}
}
