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
package org.apache.uima.wikipedia.ae.parser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

/**
 * This class aims at replacing macros, identified by their name, by a replacement text.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWTemplateResolver extends TemplateResolver {
	private static Map<String, String>	customTemplates	= new HashMap<String, String>();
	static {
		customTemplates.put("mdash", "&nbsp;&mdash; "); //$NON-NLS-1$//$NON-NLS-2$
		customTemplates.put("ndash", "&nbsp;&ndash; "); //$NON-NLS-1$//$NON-NLS-2$
		customTemplates.put("emdash", "&nbsp;&mdash; "); //$NON-NLS-1$//$NON-NLS-2$
		customTemplates.put("endash", "&nbsp;&ndash; "); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Checks if the macro is known and returns the corresponding Template object. If it's known, the provided
	 * replacement text is used to build the Template object. Else, the method returns null, indicating the
	 * parser that this macro should be replaced by an empty string.
	 */
	@Override
	public Template resolveTemplate(String templateName) {
		final String templateText = customTemplates.get(templateName);
		if (templateText != null) {
			final Template template = new Template();
			template.setName(templateName);
			template.setTemplateMarkup(templateText);
			return template;
		}
		return null;
	}

	/**
	 * Adds a new macro to the known set. For more insight on syntax, see
	 * {@link org.apache.uima.wikipedia.ae.parser.MWLanguage#addMacro(String, String) here}.
	 * 
	 * @param name
	 *            the macro identifier (lower case)
	 * @param replacement
	 *            the replacement text.
	 */
	public void addMacro(String name, String replacement) {
		customTemplates.put(name, replacement);
	}
}