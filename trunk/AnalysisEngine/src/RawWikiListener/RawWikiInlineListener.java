package RawWikiListener;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;
import org.wikimodel.wem.IWemListenerInline;
import org.wikimodel.wem.WikiFormat;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.WikiReference;

import uima.wikipedia.types.Link;

public class RawWikiInlineListener extends RawWikiListener implements IWemListenerInline {
	ArrayList<Link>	linkAnnotations;

	public RawWikiInlineListener() {
		linkAnnotations = new ArrayList<Link>();
	}

	@Override
	public List<Annotation> getAnnotations() {
		ArrayList<Annotation> temp = new ArrayList<Annotation>();
		temp.addAll(linkAnnotations);
		return temp;
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
			textContent.append(str);
			currentOffset += str.length();
		}
	}
}
