package uima.wikipedia.parser.block;

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
