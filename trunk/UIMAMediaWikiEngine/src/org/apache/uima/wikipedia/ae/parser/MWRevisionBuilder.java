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
 *  Copyright (c) 2007, 2009 David Green and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      David Green - initial API and implementation
 */
package org.apache.uima.wikipedia.ae.parser;

import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.wikipedia.ae.factory.MWAnnotator;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

/**
 * This class is a helper for the actual parser. It acts as a sink for the parser events. It is used to notify
 * the annotator for each new annotation, and also to build the revision's text without the wiki syntax.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWRevisionBuilder extends DocumentBuilder {
	/** The StringBuilder for the revision's content */
	private StringBuilder			content;
	/** A stack to keep track of the kind of block we are in */
	private final Stack<BlockType>	blockContext;
	/** A more specialized stack for the type of list we are in */
	private final Stack<BlockType>	listContext;
	/** A stack to count the items per list level */
	private final Stack<Integer>	itemCount;
	/** A stack to keep stack of the different sections */
	private final Stack<Integer>	sectionsLevel;
	/** Some flag */
	private boolean					firstLine;

	/**
	 * Initialize the revision builder.
	 */
	public MWRevisionBuilder() {
		content = new StringBuilder();
		blockContext = new Stack<BlockType>();
		listContext = new Stack<BlockType>();
		itemCount = new Stack<Integer>();
		sectionsLevel = new Stack<Integer>();
		firstLine = true;
		MWAnnotator.init();
	}

	/**
	 * Resets the Revision builder to it's default values and initialize it with a new CAS. Avoid the overhead
	 * of creating a new object.
	 * 
	 * @param cas
	 *            the CAS we are processing
	 */
	public void reset(JCas cas) {
		content = new StringBuilder();
		blockContext.clear();
		listContext.clear();
		itemCount.clear();
		sectionsLevel.clear();
		firstLine = true;
		MWAnnotator.reset(cas);
	}

	/**
	 * Handles the beginning of a title and it's corresponding section.
	 */
	@Override
	public void beginHeading(int level, Attributes attributes) {
		if (!firstLine) {
			content.append("\n\n");
		} else {
			firstLine = false;
		}
		MWAnnotator.newHeader(level, content.length());

		if (sectionsLevel.isEmpty() || level > sectionsLevel.peek()) {
			sectionsLevel.push(level);
		} else {
			while (!sectionsLevel.isEmpty() && level <= sectionsLevel.peek()) {
				sectionsLevel.pop();
				MWAnnotator.end("section", content.length());
			}
			sectionsLevel.push(level);
		}
		MWAnnotator.newSection(level, content.length());
	}

	/**
	 * Handles the beginning of various types of blocks (lists, tables, ...) and process accordingly.
	 */
	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		// Keep track of which block we are in
		blockContext.push(type);
		// Process according to the block type
		switch (type) {
			case BULLETED_LIST:
			case NUMERIC_LIST:
			case DEFINITION_LIST:
				listContext.push(type);
				itemCount.push(0);
				break;
			case LIST_ITEM:
			case DEFINITION_TERM:
			case DEFINITION_ITEM:
				content.append('\n');
				for (int level = 0; level < listContext.size() - 1; level++) {
					content.append('\t');
				}
				final int count = itemCount.pop() + 1;
				itemCount.push(count);
				switch (listContext.peek()) {
					case BULLETED_LIST:
						content.append("* ");
						break;
					case NUMERIC_LIST:
						content.append(count + ". ");
						break;
				}
				break;
			case TABLE:
			case TABLE_ROW:
				content.append('\n');
				break;
			case PARAGRAPH:
				if (!firstLine) {
					content.append("\n\n");
				} else {
					firstLine = false;
				}
				// Let the annotator know we have entered a new block.
				MWAnnotator.newBlock(type, content.length());
				break;
		}
	}

	/**
	 * For now we do nothing with the span (embedded in a line) elements.
	 */
	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
	}

	/**
	 * Starts a global section annotation that will be used as a kind of default parent for all the unclosed
	 * sections.
	 */
	@Override
	public void beginDocument() {
		MWAnnotator.newSection(1, content.length());
	}

	/**
	 * Handles the end of various types of blocks, especially table cells and lists.
	 */
	@Override
	public void endBlock() {
		final BlockType type = blockContext.pop();
		MWAnnotator.end(type, content.length());
		switch (type) {
			case TABLE_CELL_HEADER:
			case TABLE_CELL_NORMAL:
				content.append('\t');
				break;
			case BULLETED_LIST:
			case NUMERIC_LIST:
			case DEFINITION_LIST:
				listContext.pop();
				itemCount.pop();
				break;
		}
	}

	/**
	 * Indicate the end of a title to the annotator.
	 */
	@Override
	public void endHeading() {
		MWAnnotator.end("header", content.length());
	}

	/**
	 * For now we do nothing with the span elements
	 */
	@Override
	public void endSpan() {
	}

	/**
	 * Close all the unclosed sections before the revision is shipped to the CAS builder.
	 */
	@Override
	public void endDocument() {
		MWAnnotator.end("unclosed", content.length());
	}

	@Override
	public void link(Attributes attributes, String href, String label) {
		MWAnnotator.newLink(label, href, content.length());
		content.append(label);
	}

	/**
	 * Appends the provided <code>text</code> to the content.
	 */
	@Override
	public void characters(String text) {
		content.append(text);
	}

	/** Appends the provided <code>literal</code> to the content. */
	@Override
	public void charactersUnescaped(String literal) {
		content.append(literal);
	}

	@Override
	public void lineBreak() {
		content.append('\n');
	}

	@Override
	public void acronym(String arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void entityReference(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void image(Attributes arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imageLink(Attributes arg0, Attributes arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
	}

	public List<Annotation> getAnnotations() {
		return MWAnnotator.getAnnotations();
	}

	public String getText() {
		return content.toString();
	}
}
