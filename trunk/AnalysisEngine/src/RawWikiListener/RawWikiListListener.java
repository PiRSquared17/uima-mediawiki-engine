package RawWikiListener;

import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.wikimodel.wem.IWemListenerList;
import org.wikimodel.wem.WikiParameters;

public class RawWikiListListener implements IWemListenerList {
	StringBuilder				buffer;
	JCas						mCas;
	int							currentOffset;
	private Stack<ListContext>	listContext;

	public RawWikiListListener(StringBuilder buffer, int offset, JCas mCas) {
		this.buffer = buffer;
		this.mCas = mCas;
		currentOffset = buffer.length();
		listContext = new Stack<ListContext>();
	}

	@Override
	public void beginDefinitionDescription() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginDefinitionList(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginDefinitionTerm() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginList(WikiParameters params, boolean ordered) {
		addContent("\n");
		listContext.push(new ListContext(ordered));
	}

	@Override
	public void beginListItem() {
		addContent(listContext.peek().getCount());
	}

	@Override
	public void beginQuotation(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginQuotationLine() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDefinitionDescription() {
		addContent("\n");
	}

	@Override
	public void endDefinitionList(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void endDefinitionTerm() {
		addContent("\n");
	}

	@Override
	public void endList(WikiParameters params, boolean ordered) {
		listContext.pop();
		addContent("\n");
	}

	@Override
	public void endListItem() {
		addContent("\n");
	}

	@Override
	public void endQuotation(WikiParameters params) {
		addContent("\n");
	}

	@Override
	public void endQuotationLine() {
		addContent("\n");
	}

	private void addContent(String str) {
		buffer.append(str);
		currentOffset += str.length();
	}

	private class ListContext {
		public final boolean	ordered;
		private int				count;

		public ListContext(boolean ordered) {
			this.ordered = ordered;
			count = 0;
		}

		public String getCount() {
			if (ordered) {
				count++;
				return Integer.toString(count) + " ";
			} else
				return "* ";
		}
	}
}
