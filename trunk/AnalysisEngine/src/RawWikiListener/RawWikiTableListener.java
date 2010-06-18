package RawWikiListener;

import org.apache.uima.jcas.JCas;
import org.wikimodel.wem.IWemListenerTable;
import org.wikimodel.wem.WikiParameters;

public class RawWikiTableListener implements IWemListenerTable {
	StringBuilder	buffer;
	JCas			mCas;
	int				currentOffset;

	public RawWikiTableListener(StringBuilder buffer, int offset, JCas mCas) {
		this.buffer = buffer;
		this.mCas = mCas;
		currentOffset = buffer.length();
	}

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
		buffer.append(str);
		currentOffset += str.length();
	}
}
