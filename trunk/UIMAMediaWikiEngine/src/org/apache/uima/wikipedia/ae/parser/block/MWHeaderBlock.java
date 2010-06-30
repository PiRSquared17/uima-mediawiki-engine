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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class MWHeaderBlock extends Block {

	private static final Pattern	pattern	= Pattern.compile("[ \\t]*+(\\={2,6})([^\\=]++)(?>\\={2,6})?\\s*");
	private final Matcher			matcher;

	public MWHeaderBlock() {
		// Initialize the matcher
		matcher = pattern.matcher("");
	}

	/**
	 * Checks if we are in presence of a title
	 */
	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			// Begining of the line, initialize the matcher with the inpu
			matcher.reset(line);
			return matcher.matches();
		} else
			return false;
	}

	/**
	 * If we have a title, we gather data and send it to the MWRevisionBuilder
	 */
	@Override
	public int processLineContent(String line, int offset) {

		final int level = matcher.group(1).length();
		final String text = matcher.group(2);

		builder.beginHeading(level, null);
		builder.characters(text.trim());
		builder.endHeading();

		setClosed(true);
		return -1;
	}

}
