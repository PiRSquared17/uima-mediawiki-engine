/*
 *  Copyright [2010] [Fabien Poulard &lt;fabien.poulard@univ-nantes.fr&gt;, Maxime Bury, Maxime Rihouey] 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *   This class is based on the work of the Eclipse Mylyn Open Source Project,
 *   wich is realeased under the Eclipse Public License:
 *   
 *  Copyright (c) 2007, 2009 David Green and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      David Green - initial API and implementation
 */
package org.apache.uima.mediawiki.ae;

import java.io.File;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.mediawiki.ae.factory.MWCasBuilder;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * This class aims to recover a raw view of the CAS (RawWikiText) sent by the Collection Reader, analyze it
 * and add the expected annotations to the default view.
 * <p>
 * It's also the UIMA frontend for this part of the component.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 * @author Maxime Bury &lt;maxime.bury@gmail.com&gt;
 * @author Maxime Rihouey <maxime.rihouey@univ-nantes.fr>
 */
public class AnalysisEngine extends JCasAnnotator_ImplBase {
	private final static String	PARAM_FLG_ENABLEMACROS		= "EnableMacros";
	private final static String	PARAM_INP_DEFINITIONPATH	= "DefinitionFilePath";

	/**
	 * Takes care of the parameters, and configures the CAS factory.
	 * 
	 * @see MWCasBuilder
	 */
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

	/**
	 * Tells the factory to process the CAS.
	 * 
	 * @see MWCasBuilder
	 */
	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		try {
			MWCasBuilder.build(cas);
		} catch (final CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}