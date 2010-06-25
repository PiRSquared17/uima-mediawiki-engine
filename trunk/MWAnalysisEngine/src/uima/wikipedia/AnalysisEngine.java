package uima.wikipedia;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uima.wikipedia.factory.MWCasBuilder;

/**
 * This class aims to recover a raw view of the CAS (RawWikiText) sent by the Collection Reader, analyzed it
 * and add the expected annotations to the default view (
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 * @author Maxime Rihouey <maxime.rihouey@univ-nantes.fr>
 */
public class AnalysisEngine extends JCasAnnotator_ImplBase {
	private final static String	PARAM_FLG_ENABLEMACROS	= "enableMacros";

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		Boolean enableMacros = (Boolean) context.getConfigParameterValue(PARAM_FLG_ENABLEMACROS);
		try {
			MWCasBuilder.initialize("RawWikiText", enableMacros);
		} catch (CASException e) {
			throw new ResourceInitializationException("There was an error initializin the analysis engine.", null);
		}
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		try {
			MWCasBuilder.build(cas);
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}