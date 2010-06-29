package org.apache.uima.wikipedia.ae.parser.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class MWTableBlock extends Block {
	private static final String		T_START		= "{|";
	private static final String		T_END		= "|}";
	private static final String		T_CAPTION	= "|+";
	private static final String		T_ROW		= "|-";
	private static final Pattern	cellLine	= Pattern.compile("([\\|\\!]{1,2})([^\\|\\!]++)(([\\|])([^\\|\\!]++))?");
	private static final Pattern	ignoreCell	= Pattern.compile("[\\|][^\\|]++[\\|]");

	private int						blockLineCount;
	private final Matcher			match		= cellLine.matcher("");
	private final Matcher			ignore		= ignoreCell.matcher("");

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0 && line.trim().startsWith(T_START))
			return true;
		else
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
		line.trim(); // We ignore preceding whitespaces.
		if (blockLineCount == 0) {
			// First line opens the table, it doesn't have cells.
			builder.beginBlock(BlockType.TABLE, null);
			builder.beginBlock(BlockType.TABLE_ROW, null);
			blockLineCount++;
		} else if (line.startsWith(T_CAPTION) && blockLineCount == 1) {
			// Table caption can only occur right after the table opening.
			markupLanguage.emitMarkupLine(getParser(), state, 0, line, T_CAPTION.length());
		} else if (line.startsWith(T_ROW)) {
			// First row is automatically created at the start of the table.
			builder.endBlock();
			builder.beginBlock(BlockType.TABLE_ROW, null);
		} else if (containsCells(line)) {
			// We have one or several cells on this line
			processCells(line);
		} else if (line.startsWith(T_END)) {
			// End of the table reached. Close the last row
			builder.endBlock();
			setClosed(true);
		} else {
			// Probably some list or embedded table
			// TODO : handle it smoothly
			builder.lineBreak();
			markupLanguage.emitMarkupLine(getParser(), state, line, 0);
		}
		return -1;
	}

	private boolean containsCells(String line) {
		return line.startsWith("|") && !line.startsWith(T_END) || line.startsWith("!");
	}

	private void processCells(String line) {
		ignore.reset(line);
		if (ignore.matches())
			return;
		else {
			match.reset(line);
			while (match.find()) {
				builder.beginBlock(BlockType.TABLE_CELL_NORMAL, null);
				if (match.group(3) == null) {
					markupLanguage.emitMarkupLine(getParser(), state, match.group(2).trim(), 0);
				} else {
					markupLanguage.emitMarkupLine(getParser(), state, match.group(5).trim(), 0);
				}
				builder.endBlock();
			}
		}
	}
}
