package uima.wikipedia.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

import uima.wikipedia.parser.block.MWHeaderBlock;
import uima.wikipedia.parser.block.MWListBlock;
import uima.wikipedia.parser.block.MWParagraphBlock;
import uima.wikipedia.parser.block.MWPreformattedBlock;
import uima.wikipedia.parser.block.MWTableBlock;
import uima.wikipedia.parser.block.MWToCBlock;

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
		blocks.add(new MWListBlock());
		blocks.add(new MWPreformattedBlock());
		blocks.add(new MWTableBlock());
		blocks.add(new MWToCBlock());

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
