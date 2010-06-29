package org.apache.uima.wikipedia.ae.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.wikipedia.types.Header;
import org.apache.uima.wikipedia.types.Link;
import org.apache.uima.wikipedia.types.Paragraph;
import org.apache.uima.wikipedia.types.Section;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

public class MWAnnotator {
	private final JCas				cas;
	private final List<Header>		headers;
	private final List<Link>		links;
	private final List<Paragraph>	paragraphs;
	private final List<Section>		sections;
	private final Stack<Section>	currentSections;

	public MWAnnotator(JCas cas) {
		this.cas = cas;
		headers = new ArrayList<Header>();
		links = new ArrayList<Link>();
		paragraphs = new ArrayList<Paragraph>();
		sections = new ArrayList<Section>();
		currentSections = new Stack<Section>();
	}

	public void newHeader(int level, int offset) {
		final Header h = new Header(cas);
		h.setBegin(offset);
		h.setLevel(level);
		headers.add(h);
	}

	public void newLink(String label, String href, int offset) {
		final Link l = new Link(cas);
		l.setBegin(offset);
		l.setLabel(label);
		l.setLink(href);
		l.setEnd(offset + label.length());
		links.add(l);
	}

	public void newBlock(BlockType type, int offset) {
		switch (type) {
			case PARAGRAPH:
				final Paragraph p = new Paragraph(cas);
				p.setBegin(offset);
				paragraphs.add(p);
				break;
		}
	}

	public void newSection(int level, int offset) {
		final Section s = new Section(cas);
		s.setBegin(offset);
		s.setLevel(level);
		currentSections.push(s);
	}

	public void newSpan(SpanType type, int offset) {
	}

	public void end(String name, int offset) {

		if (name.equals("header")) {
			headers.get(headers.size() - 1).setEnd(offset);
			currentSections.peek().setTitle(headers.get(headers.size() - 1));
		} else if (name.equals("section")) {
			final Section s = currentSections.pop();
			s.setEnd(offset);
			s.setParent(currentSections.peek());
			sections.add(s);
		} else if (name.equals("unclosed")) {
			final Section root = currentSections.firstElement();
			currentSections.remove(0);
			for (final Section s : currentSections) {
				s.setEnd(offset);
				s.setParent(root);
				sections.add(s);
			}
			root.setEnd(offset);
			sections.add(root);
		}
	}

	public void end(BlockType type, int offset) {
		switch (type) {
			case PARAGRAPH:
				paragraphs.get(paragraphs.size() - 1).setEnd(offset);
				break;
		}
	}

	public List<Annotation> getAnnotations() {
		final List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.addAll(headers);
		annotations.addAll(links);
		annotations.addAll(paragraphs);
		annotations.addAll(sections);
		return annotations;
	}
}
