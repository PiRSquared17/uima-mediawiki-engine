/*******************************************************************************
 * Copyright (c) 2005,2007 Cognium Systems SA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Contributors:
 *     Cognium Systems SA - initial API and implementation
 *******************************************************************************/
package org.wikimodel.wem.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an internal utility class used as a context to keep in memory the current state of parsed trees (list items).
 * 
 * @author kotelnikov
 */
public class ListBuilder {

	static class CharPos implements TreeBuilder.IPos<CharPos> {

		private final int	fPos;

		private final char	fRowChar;

		private final char	fTreeChar;

		public CharPos(char treeChar, char rowChar, int pos) {
			fPos = pos;
			fTreeChar = treeChar;
			fRowChar = rowChar;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof CharPos))
				return false;
			final CharPos pos = (CharPos) obj;
			return equalsData(pos) && pos.fPos == fPos;
		}

		public boolean equalsData(CharPos pos) {
			return pos.fTreeChar == fTreeChar;
		}

		public int getPos() {
			return fPos;
		}

	}

	TreeBuilder<CharPos>		fBuilder	= new TreeBuilder<CharPos>(new TreeBuilder.ITreeListener<CharPos>() {

												public void onBeginRow(CharPos pos) {
													fListener.beginRow(pos.fTreeChar, pos.fRowChar);
												}

												public void onBeginTree(CharPos pos) {
													fListener.beginTree(pos.fTreeChar);
												}

												public void onEndRow(CharPos pos) {
													fListener.endRow(pos.fTreeChar, pos.fRowChar);
												}

												public void onEndTree(CharPos pos) {
													fListener.endTree(pos.fTreeChar);
												}

											});

	private final IListListener	fListener;

	/**
	 * @param listener
	 */
	public ListBuilder(IListListener listener) {
		fListener = listener;
	}

	/**
	 * @param row
	 * @param treeParams
	 * @param rowParams
	 *            the parameters of the row
	 */
	public void alignContext(String row) {
		final List<CharPos> list = getCharPositions(row);
		fBuilder.align(list);
	}

	private List<CharPos> getCharPositions(String s) {
		final List<CharPos> list = new ArrayList<CharPos>();
		final char[] array = s.toCharArray();
		int pos = 0;
		for (final char element : array) {
			final char ch = element;
			if (ch == '\r' || ch == '\n') {
				continue;
			}
			if (!Character.isSpaceChar(ch)) {
				final char treeChar = getTreeType(ch);
				list.add(new CharPos(treeChar, ch, pos));
			}
			pos++;
		}
		return list;
	}

	/**
	 * @param rowType
	 *            the type of the row
	 * @return the type of the tree corresponding to the given row type
	 */
	protected char getTreeType(char rowType) {
		return rowType;
	}

}
