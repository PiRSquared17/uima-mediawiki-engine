package uima.wikipedia.parser.block;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

public class MWParagraphBlock extends Block {
	/** Number of lines this block spans */
	private int	blockLineCount;

	public MWParagraphBlock() {
		blockLineCount = 0;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		return true;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0)
			// Beginning of a paragraph
			builder.beginBlock(BlockType.PARAGRAPH, null);
		else if (line.trim().isEmpty()) {
			// End of a paragraph
			setClosed(true);
			return 0;
		}

		// Test if this line is the start for another block
		// TODO : Check if the preformatted blocks get caught here
		final MediaWikiLanguage dialect = (MediaWikiLanguage) getMarkupLanguage();

		for (final Block block : dialect.getParagraphBreakingBlocks())
			if (block.canStart(line, offset)) {
				setClosed(true);
				return 0;
			}
		// If not, this block contains one more line
		++blockLineCount;
		if (blockLineCount != 1)
			// New line
			builder.characters("\n");
		// This line is not any sort of block, process it as a phrase
		dialect.emitMarkupLine(getParser(), state, line, offset);
		return -1;
	}
}
