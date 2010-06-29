package uima.wikipedia.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import uima.wikipedia.types.Header;
import uima.wikipedia.types.Link;
import uima.wikipedia.types.Paragraph;
import uima.wikipedia.types.Section;

public class MWAnnotator {
	private JCas					cas;
	private final List<Header>		headers;
	private final List<Link>		links;
	private final List<Paragraph>	paragraphs;
	private final List<Section> sections;
	private final Stack<Section> currentSections;

	public MWAnnotator(JCas cas) {
		this.cas = cas;
		headers = new ArrayList<Header>();
		links = new ArrayList<Link>();
		paragraphs = new ArrayList<Paragraph>();
		sections = new ArrayList<Section>();
		currentSections = new Stack<Section>();
	}

	public void newHeader(int level, int offset) {
		Header h = new Header(cas);
		h.setBegin(offset);
		h.setLevel(level);
		headers.add(h);
	}

	public void newLink(String label, String href, int offset) {
		Link l = new Link(cas);
		l.setBegin(offset);
		l.setLabel(label);
		l.setLink(href);
		l.setEnd(offset + label.length());
		links.add(l);
	}

	public void newBlock(BlockType type, int offset) {
		switch (type) {
			case PARAGRAPH:
				Paragraph p = new Paragraph(cas);
				p.setBegin(offset);
				paragraphs.add(p);
				break;
		}
	}

	public void newSection(int offset){
		Section s = new Section(cas);
		s.setBegin(offset);
		currentSections.push(s);
	}
	
	public void endSection(int offset){
		Section s = currentSections.pop();
		s.setEnd(offset);
		sections.add(s);
	}
	
	public void newSpan(SpanType type, int offset) {
	}

	public void end(String name, int offset) {
		if (name.equals("header"))
			headers.get(headers.size() - 1).setEnd(offset);
	}

	public void end(BlockType type, int offset) {
		switch (type) {
			case PARAGRAPH:
				paragraphs.get(paragraphs.size() - 1).setEnd(offset);
				break;
		}
	}

	public List<Annotation> getAnnotations() {
		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.addAll(headers);
		annotations.addAll(links);
		annotations.addAll(paragraphs);
		return annotations;
	}
}
