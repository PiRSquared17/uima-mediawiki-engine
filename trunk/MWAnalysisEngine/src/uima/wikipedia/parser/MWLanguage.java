package uima.wikipedia.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.ListBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.PreformattedBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.TableBlock;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.TableOfContentsBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

import uima.wikipedia.parser.block.MWHeaderBlock;
import uima.wikipedia.parser.block.MWParagraphBlock;

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

	@Override
	protected void addStandardBlocks(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		// IMPORTANT NOTE: Most items below have order dependencies. DO NOT REORDER ITEMS BELOW!!

		blocks.add(new MWHeaderBlock());
		blocks.add(new ListBlock());
		blocks.add(new PreformattedBlock());
		blocks.add(new TableBlock());
		blocks.add(new TableOfContentsBlock());

		for (final Block block : blocks) {
			// Paragraphs cant break themselves
			if (block instanceof MWParagraphBlock)
				continue;
			// Any other block causes a paragraph to be broken
			paragraphBreakingBlocks.add(block);
		}
	}

	@Override
	protected Block createParagraphBlock() {
		return new MWParagraphBlock();
	}
}
