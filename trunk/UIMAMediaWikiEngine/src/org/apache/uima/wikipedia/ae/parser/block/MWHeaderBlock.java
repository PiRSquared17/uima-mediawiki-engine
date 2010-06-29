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
