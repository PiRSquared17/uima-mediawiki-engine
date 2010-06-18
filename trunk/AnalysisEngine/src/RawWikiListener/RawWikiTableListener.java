package RawWikiListener;

import org.wikimodel.wem.IWemListenerTable;
import org.wikimodel.wem.WikiParameters;

public class RawWikiTableListener extends RawWikiListener implements IWemListenerTable {
	@Override
	public void beginTable(WikiParameters params) {
		addContent("\n\n");
	}

	@Override
	public void beginTableCell(boolean tableHead, WikiParameters params) {
	}

	@Override
	public void beginTableRow(WikiParameters params) {
	}

	@Override
	public void endTable(WikiParameters params) {
		addContent("\n\n");
	}

	@Override
	public void endTableCell(boolean tableHead, WikiParameters params) {
		addContent("\t");
	}

	@Override
	public void endTableRow(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void onTableCaption(String str) {
		addContent("\n\n");
	}

	private void addContent(String str) {
		if (str != null) {
			textContent.append(str);
			currentOffset += str.length();
		}
	}
}
