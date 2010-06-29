package org.apache.uima.wikipedia.ae.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.wikipedia.ae.parser.block.MWHeaderBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWListBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWParagraphBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWPreformattedBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWTableBlock;
import org.apache.uima.wikipedia.ae.parser.block.MWToCBlock;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

public class MWLanguage extends MediaWikiLanguage {
	MWTemplateResolver	resolver;

	public MWLanguage() {
		super();
		resolver = new MWTemplateResolver();
		final List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

	public void addMacro(String name, String replacement) {
		resolver.addMacro(name, replacement);
		final List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (isEnableMacros()) {
			markupContent = preprocessContent(markupContent);
		}
		super.processContent(parser, markupContent, asDocument);
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
			if (block instanceof MWParagraphBlock) {
				continue;
			}
			// Any other block causes a paragraph to be broken
			paragraphBreakingBlocks.add(block);
		}
	}

	@Override
	protected Block createParagraphBlock() {
		return new MWParagraphBlock();
	}

	private String preprocessContent(String markupContent) {
		return new MWTemplateProcessor(this).processTemplates(markupContent);
	}
}
