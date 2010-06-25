package uima.wikipedia.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import uima.wikipedia.types.Header;
import uima.wikipedia.types.Paragraph;

public class MWAnnotator {
	private JCas					cas;
	private final List<Header>		headers;
	private final List<Paragraph>	paragraphs;

	public MWAnnotator(JCas cas) {
		this.cas = cas;
		headers = new ArrayList<Header>();
		paragraphs = new ArrayList<Paragraph>();
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

	public void newHeader(int level, int offset) {
		Header h = new Header(cas);
		h.setBegin(offset);
		h.setLevel(level);
		headers.add(h);
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
		annotations.addAll(paragraphs);
		return annotations;
	}
}
