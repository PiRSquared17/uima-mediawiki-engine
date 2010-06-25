package uima.wikipedia;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import uima.wikipedia.factory.MWCasBuilder;

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
			MWCasBuilder.initialize(cas, "RawWikiText");
			MWCasBuilder.build();
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}