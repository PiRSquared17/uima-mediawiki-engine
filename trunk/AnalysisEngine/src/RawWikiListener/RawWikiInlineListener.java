package RawWikiListener;

import java.util.ArrayList;

import org.apache.uima.jcas.JCas;
import org.wikimodel.wem.IWemListenerInline;
import org.wikimodel.wem.WikiFormat;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.WikiReference;

import uima.wikipedia.types.Link;

public class RawWikiInlineListener implements IWemListenerInline {
	StringBuilder	buffer;
	JCas			mCas;
	int				currentOffset;
	ArrayList<Link>	linkAnnotations;

	public RawWikiInlineListener(StringBuilder buffer, int offset, JCas mcas) {
		this.buffer = buffer;
		mCas = mcas;
		currentOffset = offset;
		linkAnnotations = new ArrayList<Link>();
	}

	@Override
	public void beginFormat(WikiFormat format) {
		addContent("\n");
	}

	@Override
	public void endFormat(WikiFormat format) {
		addContent("\n");
	}

	@Override
	public void onEscape(String str) {
		addContent(str);
	}

	@Override
	public void onImage(String ref) {
		// We ignore images
	}

	@Override
	public void onImage(WikiReference ref) {
		// We ignore images
	}

	@Override
	public void onLineBreak() {
		addContent("\n");
	}

	@Override
	public void onNewLine() {
		addContent("\n");
	}

	@Override
	public void onReference(String ref) {
		onReference(new WikiReference(ref));
	}

	@Override
	public void onReference(WikiReference ref) {
		// We ignore images
		// TODO : Make it configurable
		if (!ref.getLink().startsWith("Image:")) {
			// Create a new link annotation
			final Link link = new Link(mCas);
			link.setBegin(currentOffset);
			link.setLabel(ref.getLabel());
			link.setLink(ref.getLink());
			// Add the label in the content
			addContent(ref.getLabel());
			// Add the annotation to the list
			linkAnnotations.add(link);
		}
	}

	@Override
	public void onSpace(String str) {
		addContent(str);
	}

	@Override
	public void onSpecialSymbol(String str) {
		addContent(str);
	}

	@Override
	public void onVerbatimInline(String str, WikiParameters params) {
		addContent(str);
	}

	@Override
	public void onWord(String str) {
		addContent(str);
	}

	private void addContent(String str) {
		if (str != null) {
			buffer.append(str);
			currentOffset += str.length();
		}
	}
}
