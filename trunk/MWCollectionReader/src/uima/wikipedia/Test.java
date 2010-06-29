package uima.wikipedia;

import java.io.File;

import javax.xml.stream.XMLStreamException;

import uima.wikipedia.factory.MWDumpReaderFactory;
import uima.wikipedia.parser.MWDumpReader;
import uima.wikipedia.parser.MWDumpReader.MWParseException;

public class Test {

	/**
	 * @param args
	 * @throws XMLStreamException
	 */
	public static void main(String[] args) throws XMLStreamException {
		// XML
		MWDumpReaderFactory factory;
		int i = 0;
		try {
			factory = new MWDumpReaderFactory(new File("/Users/Bowbaq/Desktop/ultimetest.xml"));
			final MWDumpReader XMLParser = factory.getParser();
			new StringBuilder();
			while (XMLParser.hasPage()) {
				++i;
			}

			System.out.println("END OF PARSING - PageNbr = " + i);
		} catch (final MWParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
