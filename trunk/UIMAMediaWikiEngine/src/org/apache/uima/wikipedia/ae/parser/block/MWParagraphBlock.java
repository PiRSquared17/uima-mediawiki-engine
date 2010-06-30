package org.apache.uima.wikipedia.ae.parser.block;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

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

public class MWParagraphBlock extends Block {
	/** Number of lines this block spans */
	private int	blockLineCount;

	public MWParagraphBlock() {
		blockLineCount = 0;
	}

	/**
	 * Paragraphs are the base unit of the language. Any line can be the start of a paragraph, that's why this
	 * method always returns true.
	 * 
	 * @return <code>true</code> : a paragraph can start on any line.
	 */
	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		return true;
	}

	/**
	 * Lets the document builder and the parser know that a paragraph has been closed.
	 */
	@Override
	public void setClosed(boolean closed) {
		if (closed) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

	/**
	 * Process the current line. Checks whether it can be the start of a different block or not. If yes, the
	 * paragraph is closed to let the new block begin.
	 */
	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			// Beginning of a paragraph
			builder.beginBlock(BlockType.PARAGRAPH, null);
		} else if (line.trim().isEmpty()) {
			// End of a paragraph
			setClosed(true);
			return 0;
		}

		// Test if this line is the start for another block
		final MediaWikiLanguage dialect = (MediaWikiLanguage) getMarkupLanguage();
		// TODO : Check if the preformatted blocks get caught here
		for (final Block block : dialect.getParagraphBreakingBlocks())
			if (block.canStart(line, offset)) {
				setClosed(true);
				return 0;
			}
		// If not, this block contains one more line
		++blockLineCount;
		if (blockLineCount != 1) {
			// New line
			builder.characters("\n");
		}
		// This line is not any sort of block, process it as a phrase
		dialect.emitMarkupLine(getParser(), state, line, offset);
		return -1;
	}
}
