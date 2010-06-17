package RawWikiListener;

import org.wikimodel.wem.AgregatingWemListener;

public class RawWikiListener extends AgregatingWemListener {
	private final StringBuilder	textContent;
	private int					currentOffset;

	public RawWikiListener() {
		textContent = new StringBuilder();
		currentOffset = 0;
	}

}
