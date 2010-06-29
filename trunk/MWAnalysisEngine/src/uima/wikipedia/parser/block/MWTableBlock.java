package uima.wikipedia.parser.block;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class MWTableBlock extends Block {
	private static final String	T_START		= "{|";
	private static final String	T_END		= "|}";
	private static final String	T_CAPTION	= "|+";
	private static final String	T_ROW		= "|-";

	private int					blockLineCount;
	private boolean				firstRow;

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		firstRow = true;
		if (lineOffset == 0 && line.startsWith(T_START))
			return true;
		else
			return false;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed)
			builder.endBlock();
		super.setClosed(closed);
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			// First line opens the table, it doesn't have cells.
			builder.beginBlock(BlockType.TABLE, null);
			blockLineCount++;
			return -1;
		} else if (line.startsWith(T_CAPTION) && blockLineCount == 1) {
			markupLanguage.emitMarkupLine(getParser(), state, 0, line, T_CAPTION.length());
			blockLineCount++;
			return -1;
		} else if (line.startsWith(T_ROW)) {
			if (!firstRow) {
				builder.endBlock();
				builder.beginBlock(BlockType.TABLE_ROW, null);
			} else {
				builder.beginBlock(BlockType.TABLE_ROW, null);
				firstRow = false;
			}
			return -1;
		} else if (line.startsWith(T_END)) {
			setClosed(true);
			return -1;
		}
		builder.beginBlock(BlockType.TABLE_ROW, null);
		builder.characters(line);
		builder.endBlock();
		blockLineCount++;
		return -1;
	}
}
