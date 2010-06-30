package org.apache.uima.wikipedia.ae.parser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

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
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 */

public class MWTemplateResolver extends TemplateResolver {
	private static Map<String, String>	customTemplates	= new HashMap<String, String>();
	static {
		customTemplates.put("mdash", "&nbsp;&mdash; "); //$NON-NLS-1$//$NON-NLS-2$
		customTemplates.put("ndash", "&nbsp;&ndash; "); //$NON-NLS-1$//$NON-NLS-2$
		customTemplates.put("emdash", "&nbsp;&mdash; "); //$NON-NLS-1$//$NON-NLS-2$
		customTemplates.put("endash", "&nbsp;&ndash; "); //$NON-NLS-1$//$NON-NLS-2$
	}

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

	public void addMacro(String name, String replacement) {
		customTemplates.put(name, replacement);
	}
}