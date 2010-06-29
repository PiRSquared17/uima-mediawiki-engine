package uima.wikipedia.parser.block;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class MWListBlock extends Block {
	private static final Pattern	startList		= Pattern.compile("([\\*#;:]++)(.*)?");
	private static final Matcher	matcher			= startList.matcher("");
	private int						blockLineCount	= 0;
	private Stack<BlockType>		listContext		= new Stack<BlockType>();
	private boolean					hasDef			= true;

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			// Initialize the matcher with the new input
			matcher.reset(line);
			return matcher.matches();
		} else
			return false;
	}

	@Override
	public void setClosed(boolean closed) {
		while (!listContext.empty()) {
			builder.endBlock();
			listContext.pop();
		}
		super.setClosed(true);
	}

	// NOTE : The first item of the list generates a list event and an item event
	@Override
	protected int processLineContent(String line, int offset) {
		if (matcher.reset(line).matches()) {
			// Get useful data from the line
			final String listSequence = matcher.group(1);
			final String itemContent = matcher.group(2).trim();
			final BlockType listType = findListType(lastChar(listSequence));
			final BlockType itemType = findItemType(lastChar(listSequence));
			final int level = listSequence.length();
			int definitionOffset = -1;

			if (blockLineCount == 0) {
				// If it's the first line, initialize the context and the block.
				builder.beginBlock(listType, null);
				listContext.push(listType);
			} else {
				int currentLevel = listContext.size();
				if (level > currentLevel) {
					// We have a nested list
					listContext.push(listType);
					builder.beginBlock(listType, null);
				} else if (level < currentLevel)
					// It's the end of a nested list.
					for (int i = 0; i < currentLevel - level; i++) {
						listContext.pop();
						builder.endBlock();
					}
				if (listType != listContext.peek()) {
					// Same level but different type of list
					listContext.pop();
					builder.endBlock();
					listContext.push(listType);
					builder.beginBlock(listType, null);
				}
			}
			// If we encounter a two part definition list item
			// Send a list event for indentation sakes.
			if (!hasDef && itemType == BlockType.DEFINITION_ITEM)
				builder.beginBlock(BlockType.DEFINITION_LIST, null);

			// Send the new item event
			builder.beginBlock(itemType, null);

			// Check for definition item
			if (itemType == BlockType.DEFINITION_TERM)
				// Look for definition item on the same line
				definitionOffset = itemContent.indexOf(':');

			// Process the item
			if (definitionOffset == -1) {
				if (itemType == BlockType.DEFINITION_TERM)
					hasDef = false;
				markupLanguage.emitMarkupLine(getParser(), state, itemContent, 0);
				if (itemType == BlockType.DEFINITION_ITEM && !hasDef) {
					hasDef = true;
					builder.endBlock();
				}
			} else if (itemType == BlockType.DEFINITION_TERM)
				// It's a definition list, and we encountered the "; word : some definition" pattern
				processInlineDef(itemContent, definitionOffset);
			// End the item
			builder.endBlock();
			// The list block has a new line.
			blockLineCount++;
			return -1;
		} else {
			setClosed(true);
			return 0;
		}
	}

	private BlockType findItemType(char lastChar) {
		switch (lastChar) {
			case ';':
				return BlockType.DEFINITION_TERM;
			case ':':
				return BlockType.DEFINITION_ITEM;
			default:
				return BlockType.LIST_ITEM;
		}
	}

	private BlockType findListType(char last) {
		switch (last) {
			case '#':
				return BlockType.NUMERIC_LIST;
			case ';':
			case ':':
				return BlockType.DEFINITION_LIST;
			default:
				return BlockType.BULLETED_LIST;
		}
	}

	private char lastChar(String sequence) {
		return sequence.charAt(sequence.length() - 1);
	}

	private void processInlineDef(String itemContent, int definitionOffset) {
		// We emit the word first.
		markupLanguage.emitMarkupLine(getParser(), state, itemContent.substring(0, definitionOffset).trim(), 0);
		builder.endBlock();
		// We simulate a list event for indentation sake
		builder.beginBlock(BlockType.DEFINITION_LIST, null);
		// We send the new item event
		builder.beginBlock(BlockType.DEFINITION_ITEM, null);
		markupLanguage.emitMarkupLine(getParser(), state, itemContent.substring(definitionOffset + 1).trim(), 0);
		// We call endBlock() to get rid of the simulated list.
		builder.endBlock();
		// We just added a definition item, reset the flag.
		hasDef = true;
	}
}
