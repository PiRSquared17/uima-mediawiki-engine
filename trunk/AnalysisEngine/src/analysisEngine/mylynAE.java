package analysisEngine;

import java.io.StringWriter;

import metaParser.ParsingCoordinator;
import mylynParser.RawTextBuilder;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
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
public class mylynAE extends JCasAnnotator_ImplBase {

	private RawTextBuilder	theConverter;

	@Override
	public void process(JCas newcas) throws AnalysisEngineProcessException {
		try {
			// Get the view containing the raw text
			final JCas rawView = newcas.getView("RawWikiText");

			// Initialize the buffer where the text will be stored
			final StringBuilder casContent = new StringBuilder();

			// Get an iterator for the revisions
			final FSIterator<Annotation> iteratorRevision = rawView.getAnnotationIndex(Revision.type).iterator();
			// Some storage variables
			Revision myRevision, newRevision = new Revision(newcas);

			// Iterate over the revision annotations
			while (iteratorRevision.hasNext()) {
				// Get a revision annotation
				myRevision = (Revision) iteratorRevision.next();

				// Get the text
				final String myRevisionText = myRevision.getCoveredText();

				final int start = casContent.length();

				// Initialize the parser
				MarkupParser parser = new MarkupParser(new MediaWikiLanguage());
				StringBuilder builder = new StringBuilder();
				StringWriter writer = new StringWriter();
				parser.setBuilder(new RawTextBuilder(builder));
				
				
				// Parse the raw text
				parser.parse(myRevisionText);

				// Add the parsed text to buffer
				casContent.append(builder.toString());

				final int end = casContent.length();

				// Craft the revision annotation
				newRevision.setBegin(start);
				newRevision.setEnd(end);
				newRevision.setComment(myRevision.getComment());
				newRevision.setId(myRevision.getId());
				newRevision.setIsMinor(myRevision.getIsMinor());
				newRevision.setUser(myRevision.getUser());
				newRevision.addToIndexes();

				// Get all the annotations crafted by the parser and add them
				for (final Annotation myAnnotation : theConverter.getAnnotations())
					myAnnotation.addToIndexes();

			}

			// All the textual content is collected now...
			newcas.setDocumentText(casContent.toString());

			// We can add the Article annotations using an iterator
			Article myArticle, newArticle = new Article(newcas);
			final FSIterator<Annotation> iterateurArticle = rawView.getAnnotationIndex(Article.type).iterator();
			// We iterate over the Article annotations, there should be only one.
			while (iterateurArticle.hasNext()) {
				// Get the annotation
				myArticle = (Article) iterateurArticle.next();
				// Copy it to a new one
				newArticle.setId(myArticle.getId());
				newArticle.setNamespace(myArticle.getNamespace());
				newArticle.setPrefix(myArticle.getPrefix());
				newArticle.setTitle(myArticle.getTitle());
				newArticle.setBegin(myArticle.getBegin());
				newArticle.setEnd(myArticle.getEnd());
				// Add it
				newArticle.addToIndexes();
			}

		} catch (final CASException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (final WikiParserException e) {
			throw new AnalysisEngineProcessException(e);
		}

	}
}
