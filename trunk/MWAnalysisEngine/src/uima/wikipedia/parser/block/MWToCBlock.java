package uima.wikipedia.parser.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.block.AbstractTableOfContentsBlock;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

public class MWToCBlock extends AbstractTableOfContentsBlock {
	static final Pattern	startPattern	= Pattern.compile("\\s*+__TOC__\\s*+(.*?)");
	private int				blockLineNumber	= 0;
	private Matcher			matcher			= startPattern.matcher("");

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}

		if (!getMarkupLanguage().isFilterGenerativeContents()) {
			final OutlineParser outlineParser = new OutlineParser(new MediaWikiLanguage());
			final OutlineItem rootItem = outlineParser.parse(state.getMarkupContent());

			emitToc(rootItem);
		}
		final int start = matcher.start(1);
		if (start > 0)
			setClosed(true);
		return start;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && !getMarkupLanguage().isFilterGenerativeContents()) {
			matcher.reset(line);
			blockLineNumber = 0;
			return matcher.matches();
		} else
			return false;
	}

}
