package RawWikiListener;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.wikimodel.wem.IWemListenerDocument;
import org.wikimodel.wem.WikiParameters;

import uima.wikipedia.types.Header;
import uima.wikipedia.types.Section;

public class RawWikiDocumentListener implements IWemListenerDocument {
	StringBuilder	buffer;
	JCas			mCas;
	int				currentOffset;
	List<Header>	headerAnnotations;
	List<Section>	unclosedSections;
	List<Section>	closedSections;

	public RawWikiDocumentListener(StringBuilder buffer, int offset, JCas mCas) {
		this.buffer = buffer;
		this.mCas = mCas;
		currentOffset = offset;
		headerAnnotations = new ArrayList<Header>();
		unclosedSections = new ArrayList<Section>();
		closedSections = new ArrayList<Section>();
	}

	/**
	 * Create a new annotation when we encounter a header.
	 */
	@Override
	public void beginHeader(int headerLevel, WikiParameters params) {
		addContent("\n\n"); // Jump a line
		// Create a new header annotation
		final Header newHeader = new Header(mCas);
		newHeader.setLevel(headerLevel);
		newHeader.setBegin(currentOffset);
		// Add it to the list
		headerAnnotations.add(newHeader);
		// Add is as the header of the last unclosed section
		if (!unclosedSections.isEmpty())
			unclosedSections.get(unclosedSections.size() - 1).setTitle(newHeader);
	}

	/**
	 * Create a new annotation when we encounter a section. Temporarily add it to the unclosed section list. A further
	 * call to endSection() shall close it.
	 */
	@Override
	public void beginSection(int docLevel, int headerLevel, WikiParameters params) {
		// Create a new annotation
		final Section newSection = new Section(mCas);
		newSection.setBegin(currentOffset);
		newSection.setLevel(headerLevel);
		// The parent section is the last unclosed one
		if (unclosedSections.size() > 0)
			newSection.setParent(unclosedSections.get(unclosedSections.size() - 1));
		// The next encounered header will be set as title
		unclosedSections.add(newSection);
	}

	/**
	 * Finishes the last header annotation.
	 */
	@Override
	public void endHeader(int headerLevel, WikiParameters params) {
		// Update the last header's ending value
		headerAnnotations.get(headerAnnotations.size() - 1).setEnd(currentOffset);
		addContent("\n\n)"); // Jump a line
	}

	/**
	 * Closes the last unclosed section
	 */
	@Override
	public void endSection(int docLevel, int headerLevel, WikiParameters params) {
		// Retrieve the last unclosed section
		final Section section = unclosedSections.get(unclosedSections.size() - 1);
		// Close it
		section.setEnd(currentOffset);
		// Move it to the closed sections.
		unclosedSections.remove(unclosedSections.size() - 1);
		closedSections.add(section);
	}

	public List<Annotation> getAnnotations() {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		// Close the unclosed sections
		for (Section s : unclosedSections)
			s.setEnd(currentOffset);
		annotations.addAll(headerAnnotations);
		annotations.addAll(closedSections);
		annotations.addAll(unclosedSections);
		return annotations;
	}

	@Override
	public void beginDocument(WikiParameters params) {
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
	public void endSectionContent(int docLevel, int headerLevel, WikiParameters params) {
		// TODO Auto-generated method stub
	}

	private void addContent(String str) {
		if (str != null) {
			buffer.append(str);
			currentOffset += str.length();
		}
	}
}
