package uima.wikipedia.parser;

import java.util.List;
import java.util.Stack;

import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class MWRevisionBuilder extends DocumentBuilder {
	// The text builder
	StringBuilder		builder;
	// A stack for the type of block we are in.
	Stack<BlockType>	blockContext;
	// A stack for the type of list we are in.
	Stack<BlockType>	listContext;
	// Item count for the list we are in.
	Stack<Integer>		itemCount;

	public MWRevisionBuilder() {
		builder = new StringBuilder();
		blockContext = new Stack<BlockType>();
		listContext = new Stack<BlockType>();
		itemCount = new Stack<Integer>();
	}

	@Override
	public void acronym(String arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		blockContext.push(type);
		switch (type) {
			case BULLETED_LIST:
			case NUMERIC_LIST:
				listContext.push(type);
				itemCount.push(0);
				break;
			case LIST_ITEM:
				if (itemCount.peek() != 0 || listContext.size() != 1)
					builder.append('\n');
				for (int level = 0; level < listContext.size() - 1; level++)
					builder.append('\t');
				int count = itemCount.pop();
				count++;
				itemCount.push(count);
				switch (listContext.peek()) {
					case BULLETED_LIST:
						builder.append("* ");
						break;
					case NUMERIC_LIST:
						builder.append(count + ". ");
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
	public void beginDocument() {
		// TODO Handle Document annotation here
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		// TODO Handle header annotation here
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
	}

	@Override
	public void characters(String text) {
		builder.append(text);
	}

	@Override
	public void charactersUnescaped(String literal) {
		builder.append(literal);
	}

	@Override
	public void endBlock() {
		BlockType type = blockContext.pop();

		switch (type) {
			case TABLE:
			case TABLE_ROW:
				builder.append('\n');
				break;
			case TABLE_CELL_HEADER:
			case TABLE_CELL_NORMAL:
				builder.append('\t');
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
				builder.append("\n\n");
			default:
		}
	}

	@Override
	public void endDocument() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endHeading() {
		builder.append("\n\n");
	}

	@Override
	public void endSpan() {
		// TODO Auto-generated method stub

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
		builder.append('\n');
	}

	@Override
	public void link(Attributes attributes, String href, String label) {
		builder.append(label);
	}

	public List<Annotation> getAnnotations() {
		// TODO : Return all the annotations
		return null;
	}

	public String getText() {
		// TODO : Return the parsed text
		return null;
	}
}
