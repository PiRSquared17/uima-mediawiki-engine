package RawWikiListener;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.wikimodel.wem.AgregatingWemListener;

public class RawWikiListener extends AgregatingWemListener {
	protected static StringBuilder	textContent;
	protected static JCas			mCas;
	protected static int			currentOffset;
	private static RawWikiListener	instance	= null;

	public RawWikiListener() {
	}

	public static RawWikiListener newInstance(JCas mCas, int offset) {
		if (instance != null)
			instance = new RawWikiListener(mCas, offset);
		return instance;
	}

	private RawWikiListener(JCas mCas, int offset) {
		RawWikiListener.mCas = mCas;
		textContent = new StringBuilder();
		currentOffset = offset;
		fDocumentListener = new RawWikiDocumentListener();
		fTableListener = new RawWikiTableListener();
		fListListener = new RawWikiListListener();
		fInlineListener = new RawWikiInlineListener();
		fBlockListener = new RawWikiBlockListener();
		fSemanticListener = new RawWikiSemanticListener();
		fProgrammingListener = new RawWikiProgrammingListener();
	}

	public List<Annotation> getAnnotations() {
		ArrayList<Annotation> result = new ArrayList<Annotation>();
		result.addAll(((RawWikiDocumentListener) fDocumentListener).getAnnotations());
		result.addAll(((RawWikiBlockListener) fBlockListener).getAnnotations());
		result.addAll(((RawWikiInlineListener) fInlineListener).getAnnotations());
		return result;
	}

	public String getContent() {
		return textContent.toString();
	}
}
