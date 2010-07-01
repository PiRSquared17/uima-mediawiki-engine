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
package org.apache.uima.mediawiki.ae.parser.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.mediawiki.ae.factory.MWRevisionBuilder;
import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.AbstractTableOfContentsBlock;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

public class MWToCBlock extends AbstractTableOfContentsBlock {
	static final Pattern	startPattern	= Pattern.compile("\\s*+__TOC__\\s*+(.*?)");
	private int				blockLineNumber	= 0;
	private final Matcher	matcher			= startPattern.matcher("");

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}

		if (!getMarkupLanguage().isFilterGenerativeContents()) {
			final OutlineParser outlineParser = new OutlineParser(new MediaWikiLanguage());
			final OutlineItem rootItem = outlineParser.parse(state.getMarkupContent());

			((MWRevisionBuilder) builder).beginToC();
			emitToc(rootItem);
			((MWRevisionBuilder) builder).endToC();
		}
		final int start = matcher.start(1);
		if (start > 0)
			setClosed(true);
		return start;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && !getMarkupLanguage().isFilterGenerativeContents()) {
			matcher.reset(line);
			blockLineNumber = 0;
			return matcher.matches();
		} else
			return false;
	}

}
