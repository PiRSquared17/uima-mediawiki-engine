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
 *  This class is based on the work of the Eclipse Mylyn Open Source Project,
 *  wich is released under the Eclipse Public License:
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
import java.util.List;

import org.apache.uima.mediawiki.ae.parser.block.MWHeaderBlock;
import org.apache.uima.mediawiki.ae.parser.block.MWListBlock;
import org.apache.uima.mediawiki.ae.parser.block.MWParagraphBlock;
import org.apache.uima.mediawiki.ae.parser.block.MWPreformattedBlock;
import org.apache.uima.mediawiki.ae.parser.block.MWTableBlock;
import org.apache.uima.mediawiki.ae.parser.block.MWToCBlock;
import org.apache.uima.mediawiki.ae.parser.token.MWLineBreakToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.HyperlinkExternalReplacementToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.HyperlinkInternalReplacementToken;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.ImageReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.EntityReferenceReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLiteralReplacementToken;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

/**
 * This class subclasses the default MediaWiki language implementation provided by MyLyn WikiText in order to
 * finely tune the behaviour for our application.
 * <p>
 * Most of the blocks constituting the MediaWiki language have been rewritten and replaced by new ones. In
 * particular, our blocks do not take care of the CSS attributes (it gets stripped) as our target output is
 * raw text.
 * <p>
 * We also enabled the use of a customized template processor (the RegEx have been tuned) in order to
 * hopefully improve performance and also accept a wider set of valid tokens as templates.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWLanguage extends MediaWikiLanguage {
	private MWTemplateResolver	resolver;

	/**
	 * Constructs a new MWLanguage with the custom template resolver.
	 * 
	 * @see MWTemplateResolver
	 */
	public MWLanguage() {
		super();
		resolver = new MWTemplateResolver();
		final List<TemplateResolver> temp = new ArrayList<TemplateResolver>();
		temp.add(resolver);
		setTemplateProviders(temp);
	}

	/**
	 * Add a new macro (template) to the language.
	 * <p>
	 * There is several kinds of macro. See <a href="http://www.mediawiki.org/wiki/Help:Templates">here</a>
	 * for more details on the syntaxe.
	 * <p>
	 * What you need to know here, is that the identifier is the lower case version of the template name. It
	 * may be composed of several words, and contain any character except '}' and '|'. The replacement text
	 * can be anything of your liking. If you want to take in account parameters, <code>{{{1}}}</code> will be
	 * replaced by the first parameter value. Alternatively, if the parameters are named, you can use
	 * <code>{{{name}}}</code>.
	 * <p>
	 * For more intelligent processing of the macros, one can improve the
	 * {@link org.apache.uima.mediawiki.ae.parser.MWTemplateResolver template resolver}.
	 * 
	 * @param name
	 *            the macro identifier
	 * @param replacement
	 *            the text replacement for the macro
	 */
	public void addMacro(String name, String replacement) {
		resolver.addMacro(name, replacement);
	}

	/**
	 * If macros are enabled, pre process the markup to make the replacements. Unknown macros will be
	 * stripped. Only then the markup is processed. Events are sent to the document builder until the end of
	 * the document is reached.
	 */
	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (isEnableMacros())
			markupContent = preprocessContent(markupContent);
		super.processContent(parser, markupContent, asDocument);
	}

	/**
	 * Adds to the language the different kinds of blocks we consider, and indicate if they are
	 * paragraph-breaking or not.
	 */
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

	/**
	 * Add some token that are to be replaced to the language.
	 */
	@Override
	protected void addStandardTokens(PatternBasedSyntax tokenSyntax) {
		tokenSyntax.add(new MWLineBreakToken());
		tokenSyntax.add(new EntityReferenceReplacementToken("(tm)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(TM)", "#8482")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(c)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(C)", "#169")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(r)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new EntityReferenceReplacementToken("(R)", "#174")); //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new ImageReplacementToken());
		tokenSyntax.add(new HyperlinkInternalReplacementToken());
		tokenSyntax.add(new HyperlinkExternalReplacementToken());
		tokenSyntax.add(new ImpliedHyperlinkReplacementToken());
		tokenSyntax.add(new PatternLiteralReplacementToken("(?:(?<=\\w\\s)(----)(?=\\s\\w))", "<hr/>")); // horizontal rule //$NON-NLS-1$ //$NON-NLS-2$
		tokenSyntax.add(new org.eclipse.mylyn.internal.wikitext.mediawiki.core.token.EntityReferenceReplacementToken());
	}

	/**
	 * Returns an instance of the default paragraph implementation we consider.
	 */
	@Override
	protected Block createParagraphBlock() {
		return new MWParagraphBlock();
	}

	/**
	 * Takes care of all the macros in the text. Known ones are replaced, unknown ones are stripped out.
	 * 
	 * @param markupContent
	 *            the text to process
	 * @return the same text with the macros taken care of.
	 */
	private String preprocessContent(String markupContent) {
		return new MWTemplateProcessor(this).processTemplates(markupContent);
	}
}
