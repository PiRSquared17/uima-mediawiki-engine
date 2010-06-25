package uima.wikipedia.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

public class MWLanguage extends MediaWikiLanguage {
	CustomTemplateResolver	resolver;

	public MWLanguage() {
		super();
		resolver = new CustomTemplateResolver();
		List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

	public void addMacro(String name, String replacement) {
		resolver.addMacro(name, replacement);
		List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

}
