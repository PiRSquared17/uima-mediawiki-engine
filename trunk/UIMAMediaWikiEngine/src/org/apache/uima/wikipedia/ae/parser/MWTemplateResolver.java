package org.apache.uima.wikipedia.ae.parser;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

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