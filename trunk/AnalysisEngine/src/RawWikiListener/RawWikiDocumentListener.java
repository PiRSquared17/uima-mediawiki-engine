package RawWikiListener;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.wikimodel.wem.IWemListenerDocument;
import org.wikimodel.wem.WikiParameters;

import uima.wikipedia.types.Header;

public class RawWikiDocumentListener implements IWemListenerDocument {
	StringBuilder	buffer;
	JCas			mCas;
	List<Header>	headerAnnotations;

	public RawWikiDocumentListener(StringBuilder buffer, JCas mCas) {
		this.buffer = buffer;
		this.mCas = mCas;
	}

	@Override
	public void beginDocument(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginHeader(int headerLevel, WikiParameters params) {
		buffer.append("\n\n"); // Jump a line

	}

	@Override
	public void beginSection(int docLevel, int headerLevel, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginSectionContent(int docLevel, int headerLevel, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument(WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endHeader(int headerLevel, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endSection(int docLevel, int headerLevel, WikiParameters params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endSectionContent(int docLevel, int headerLevel, WikiParameters params) {
		// TODO Auto-generated method stub

	}
}
