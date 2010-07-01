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
package org.apache.uima.mediawiki.ae.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.AbstractMediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

/**
 * This class is mostly a copy paste of the original TemplateProcessor built in MyLyn. I have only modified
 * the RegEx to allow matching of a wider set of objects. I have also made an attempt in making sure that the
 * RegEx do not backtrack, hopefully improving the performance somewhat.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */

public class MWTemplateProcessor {

	private static final Pattern			templatePattern				= Pattern.compile("(?:^|(?<!\\{))(\\{\\{([^\\}\\|]++)(\\|[^\\}]*+)?+\\}\\})");	//$NON-NLS-1$

	private static final Pattern			templateParameterPattern	= Pattern.compile("\\{\\{\\{([a-zA-Z0-9]++)\\}\\}\\}");						//$NON-NLS-1$

	private static final Pattern			parameterSpec				= Pattern.compile("\\|\\s*+([^\\|=]++)(?:\\s*+=\\s*+(([^\\|]*+)))?");			//$NON-NLS-1$

	private static final Pattern			includeOnlyPattern			= Pattern.compile("(?>.*?<includeonly>)(.*?)</includeonly>.*+", //$NON-NLS-1$
																		Pattern.DOTALL);

	private static final Pattern			noIncludePattern			= Pattern.compile("<noinclude>(.*?)</noinclude>", Pattern.DOTALL);				//$NON-NLS-1$

	private final AbstractMediaWikiLanguage	mediaWikiLanguage;

	private final Map<String, Template>		templateByName				= new HashMap<String, Template>();

	private final List<Pattern>				excludePatterns				= new ArrayList<Pattern>();

	public MWTemplateProcessor(AbstractMediaWikiLanguage abstractMediaWikiLanguage) {
		mediaWikiLanguage = abstractMediaWikiLanguage;

		for (final Template template : mediaWikiLanguage.getTemplates()) {
			templateByName.put(template.getName().toLowerCase(), normalize(template));
		}
		final String templateExcludes = abstractMediaWikiLanguage.getTemplateExcludes();
		if (templateExcludes != null) {
			final String[] split = templateExcludes.split("\\s*,\\s*"); //$NON-NLS-1$
			for (final String exclude : split) {
				final String pattern = exclude.toLowerCase().replaceAll("([^a-zA-Z:\\*])", "\\$1").replaceAll("\\*", ".*?"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				excludePatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
			}
		}
	}

	public String processTemplates(String markupContent) {

		final StringBuilder processedMarkup = new StringBuilder();

		int lastIndex = 0;
		final Matcher matcher = templatePattern.matcher(markupContent);
		while (matcher.find()) {
			final int start = matcher.start();
			if (lastIndex < start) {
				processedMarkup.append(markupContent.substring(lastIndex, start));
			}
			final String templateName = matcher.group(2);
			final Template template = resolveTemplate(templateName);
			if (template != null) {
				final String parameters = matcher.group(3);
				final String replacementText = processTemplate(template, parameters);
				processedMarkup.append(replacementText);
			}
			lastIndex = matcher.end();
		}
		if (lastIndex == 0)
			return markupContent;
		if (lastIndex < markupContent.length()) {
			processedMarkup.append(markupContent.substring(lastIndex));
		}
		return processedMarkup.toString();
	}

	private String processTemplate(Template template, String parametersText) {
		if (template.getTemplateMarkup() == null)
			return "";
		final String macro = template.getTemplateMarkup();

		final List<Parameter> parameters = processParameters(parametersText);

		final StringBuilder processedMarkup = new StringBuilder();
		int lastIndex = 0;
		final Matcher matcher = templateParameterPattern.matcher(macro);
		while (matcher.find()) {
			final int start = matcher.start();
			if (lastIndex < start) {
				processedMarkup.append(macro.substring(lastIndex, start));
			}
			final String parameterName = matcher.group(1);
			String parameterValue = null;
			try {
				final int parameterIndex = Integer.parseInt(parameterName);
				if (parameterIndex <= parameters.size() && parameterIndex > 0) {
					parameterValue = parameters.get(parameterIndex - 1).value;
				}
			} catch (final NumberFormatException e) {
				for (final Parameter param : parameters)
					if (parameterName.equalsIgnoreCase(param.name)) {
						parameterValue = param.value;
						break;
					}
			}
			if (parameterValue != null) {
				processedMarkup.append(parameterValue);
			}

			lastIndex = matcher.end();
		}
		if (lastIndex == 0)
			return macro;
		if (lastIndex < macro.length()) {
			processedMarkup.append(macro.substring(lastIndex));
		}
		return processedMarkup.toString();
	}

	private List<Parameter> processParameters(String parametersText) {
		final List<Parameter> parameters = new ArrayList<MWTemplateProcessor.Parameter>();
		if (parametersText != null) {
			final Matcher matcher = parameterSpec.matcher(parametersText);
			while (matcher.find()) {
				final String nameOrValue = matcher.group(1);
				final String value = matcher.group(2);
				final Parameter parameter = new Parameter();
				if (value != null) {
					parameter.name = nameOrValue;
					parameter.value = value;
				} else {
					parameter.value = nameOrValue;
				}
				parameters.add(parameter);
			}
		}
		return parameters;
	}

	private Template resolveTemplate(String templateName) {
		templateName = templateName.toLowerCase();
		if (!excludePatterns.isEmpty()) {
			for (final Pattern p : excludePatterns)
				if (p.matcher(templateName).matches())
					return null;
		}
		Template template = templateByName.get(templateName);
		if (template == null) {
			for (final TemplateResolver resolver : mediaWikiLanguage.getTemplateProviders()) {
				template = resolver.resolveTemplate(templateName);
				if (template != null) {
					template = normalize(template);
					break;
				}
			}
			if (template == null) {
				template = new Template();
				template.setName(templateName);
				template.setTemplateMarkup("");
			}
			templateByName.put(template.getName().toLowerCase(), template);
		}
		return template;
	}

	private Template normalize(Template template) {
		final Template normalizedTemplate = new Template();
		normalizedTemplate.setName(template.getName());
		normalizedTemplate.setTemplateMarkup(normalizeTemplateMarkup(template.getTemplateMarkup()));

		return normalizedTemplate;
	}

	private String normalizeTemplateMarkup(String templateMarkup) {
		Matcher matcher = includeOnlyPattern.matcher(templateMarkup);
		if (matcher.matches())
			return matcher.group(1);
		matcher = noIncludePattern.matcher(templateMarkup);
		return matcher.replaceAll("");
	}

	private static class Parameter {
		String	name;
		String	value;
	}

}
