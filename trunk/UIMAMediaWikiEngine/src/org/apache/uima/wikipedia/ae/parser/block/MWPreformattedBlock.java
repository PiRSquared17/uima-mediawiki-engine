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
 *   wich is released under the Eclipse Public License:
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
package org.apache.uima.wikipedia.ae.parser.block;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class MWPreformattedBlock extends Block {
	private int	blockLineCount	= 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (!line.isEmpty() && lineOffset == 0 && line.charAt(0) == ' ') {
			blockLineCount = 0;
			return true;
		}
		return false;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.PREFORMATTED, null);
		}
		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		builder.charactersUnescaped(line);
		blockLineCount++;
		return -1;
	}

}
