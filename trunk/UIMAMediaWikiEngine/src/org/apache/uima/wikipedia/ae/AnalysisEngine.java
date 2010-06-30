package org.apache.uima.wikipedia.ae;

import java.io.File;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.wikipedia.ae.factory.MWCasBuilder;

/**
 * This class aims to recover a raw view of the CAS (RawWikiText) sent by the Collection Reader, analyze it
 * and add the expected annotations to the default view.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 * @author Maxime Bury &lt;maxime.bury@gmail.com&gt;
 * @author Maxime Rihouey <maxime.rihouey@univ-nantes.fr>
 */
public class AnalysisEngine extends JCasAnnotator_ImplBase {
	private final static String	PARAM_FLG_ENABLEMACROS		= "EnableMacros";
	private final static String	PARAM_INP_DEFINITIONPATH	= "DefinitionFilePath";

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		// Get the flag value for macro processing
		final Boolean enableMacros = (Boolean) context.getConfigParameterValue(PARAM_FLG_ENABLEMACROS);
		// Get the path of the definition file for the macros
		File definition = null;
		final String path = (String) context.getConfigParameterValue(PARAM_INP_DEFINITIONPATH);
		if (path != null && !path.isEmpty())
			definition = new File(path);
		// Initialize the factory
		try {
			MWCasBuilder.initialize("RawWikiText", enableMacros, definition);
		} catch (final CASException e) {
			throw new ResourceInitializationException("There was an error initializing the analysis engine.\n" + e.getMessage(), null);
		}
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		try {
			MWCasBuilder.build(cas);
		} catch (final CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}