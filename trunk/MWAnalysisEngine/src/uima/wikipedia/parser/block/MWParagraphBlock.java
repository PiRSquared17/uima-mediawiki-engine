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
