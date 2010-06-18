package RawWikiListener;

import org.apache.uima.jcas.JCas;
import org.wikimodel.wem.AgregatingWemListener;

public class RawWikiListener extends AgregatingWemListener {
	private final StringBuilder	textContent;
	private final JCas			mCas;
	private int					currentOffset;

	public RawWikiListener(JCas mCas) {
		this.mCas = mCas;
		textContent = new StringBuilder();
		currentOffset = 0;
	}

	public void addDocumentListener() {
		fDocumentListener = new RawWikiDocumentListener(textContent, currentOffset, mCas);
	}
}
