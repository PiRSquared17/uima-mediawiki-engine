package uima.wikipedia.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import uima.wikipedia.types.Header;

public class MWAnnotator {
	private JCas					cas;
	private final List<Annotation>	annotations;
	private final List<Header>		headers;

	public MWAnnotator(JCas cas) {
		this.cas = cas;
		annotations = new ArrayList<Annotation>();
		headers = new ArrayList<Header>();
	}

	public void newBlock(BlockType type, int offset) {

	}

	public List<Annotation> getAnnotations() {
		return annotations;
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
}
