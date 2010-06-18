package RawWikiListener;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.tcas.Annotation;
import org.wikimodel.wem.IWemListenerSimpleBlocks;
import org.wikimodel.wem.WikiParameters;

import uima.wikipedia.types.Paragraph;

public class RawWikiBlockListener extends RawWikiListener implements IWemListenerSimpleBlocks {
	ArrayList<Paragraph>	paragraphAnnotations;

	public RawWikiBlockListener() {
		super();
		paragraphAnnotations = new ArrayList<Paragraph>();
	}

	@Override
	public List<Annotation> getAnnotations() {
		ArrayList<Annotation> temp = new ArrayList<Annotation>();
		temp.addAll(paragraphAnnotations);
		return temp;
	}

	@Override
	public void beginInfoBlock(String infoType, WikiParameters params) {
		// TODO We ignore them for the moment
	}

	@Override
	public void beginParagraph(WikiParameters params) {
		// Create a new annotation
		final Paragraph p = new Paragraph(mCas);
		p.setBegin(currentOffset);
		// Add it to the list
		paragraphAnnotations.add(p);
	}

	@Override
	public void endInfoBlock(String infoType, WikiParameters params) {
		// TODO We ignore them for the moment
	}

	@Override
	public void endParagraph(WikiParameters params) {
		paragraphAnnotations.get(paragraphAnnotations.size() - 1).setEnd(currentOffset);
	}

	@Override
	public void onEmptyLines(int count) {
		addContent("\n");
	}

	@Override
	public void onHorizontalLine(WikiParameters params) {
		// We ignore horizontal lines
	}

	@Override
	public void onVerbatimBlock(String str, WikiParameters params) {
		addContent("\n" + str + "\n");
	}

	private void addContent(String str) {
		if (str != null) {
			textContent.append(str);
			currentOffset += str.length();
		}
	}
}
