package uima.wikipedia.parser;

import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import uima.wikipedia.factory.MWAnnotator;

public class MWRevisionBuilder extends DocumentBuilder {
	// The text builder
	private StringBuilder		content;
	// A stack for the type of block we are in.
	private Stack<BlockType>	blockContext;
	// A stack for the type of list we are in.
	private Stack<BlockType>	listContext;
	// Item count for the list we are in.
	private Stack<Integer>		itemCount;
	// The annotation factory
	private MWAnnotator			annotator;

	public MWRevisionBuilder(JCas cas) {
		content = new StringBuilder();
		blockContext = new Stack<BlockType>();
		listContext = new Stack<BlockType>();
		itemCount = new Stack<Integer>();
		annotator = new MWAnnotator(cas);
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		annotator.newHeader(level, content.length());
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		// Keep track of which block we are in
		blockContext.push(type);
		// Let the annotator know we have entered a new block.
		annotator.newBlock(type, content.length());
		// Process according to the block type
		switch (type) {
			case BULLETED_LIST:
			case NUMERIC_LIST:
				listContext.push(type);
				itemCount.push(0);
				break;
			case LIST_ITEM:
				if (itemCount.peek() != 0 || listContext.size() != 1)
					content.append('\n');
				for (int level = 0; level < listContext.size() - 1; level++)
					content.append('\t');
				int count = itemCount.pop();
				count++;
				itemCount.push(count);
				switch (listContext.peek()) {
					case BULLETED_LIST:
						content.append("* ");
						break;
					case NUMERIC_LIST:
						content.append(count + ". ");
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
	}

	@Override
	public void beginDocument() {
		// TODO Handle Document annotation here
	}

	@Override
	public void endBlock() {
		BlockType type = blockContext.pop();

		switch (type) {
			case TABLE:
			case TABLE_ROW:
				content.append('\n');
				break;
			case TABLE_CELL_HEADER:
			case TABLE_CELL_NORMAL:
				content.append('\t');
				break;
			case BULLETED_LIST:
			case NUMERIC_LIST:
				listContext.pop();
				itemCount.pop();
				break;
			case LIST_ITEM:
				// builder.append('\n');
				break;
			case PARAGRAPH:
				content.append("\n\n");
			default:
		}
	}

	@Override
	public void endHeading() {
		annotator.end("header", content.length());
		content.append("\n\n");
	}

	@Override
	public void endSpan() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endDocument() {
		// TODO Auto-generated method stub

	}

	@Override
	public void characters(String text) {
		content.append(text);
	}

	@Override
	public void charactersUnescaped(String literal) {
		content.append(literal);
	}

	@Override
	public void entityReference(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void image(Attributes arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imageLink(Attributes arg0, Attributes arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lineBreak() {
		content.append('\n');
	}

	@Override
	public void link(Attributes attributes, String href, String label) {
		content.append(label);
	}

	public List<Annotation> getAnnotations() {
		return annotator.getAnnotations();
	}

	public String getText() {
		return content.toString();
	}

	@Override
	public void acronym(String arg0, String arg1) {
		// TODO Auto-generated method stub
	}
}
