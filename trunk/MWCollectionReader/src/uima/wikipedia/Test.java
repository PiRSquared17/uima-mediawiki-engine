package uima.wikipedia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

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
		// WIKI
		MarkupParser parser = new MarkupParser(new MediaWikiLanguage());
		// FILE
		BufferedWriter output;
		try {
			factory = new MWDumpReaderFactory(new File("/Users/Bowbaq/Desktop/ultimetest.xml"));
			MWDumpReader XMLParser = factory.getParser();
			StringBuilder builder = new StringBuilder();
			while (XMLParser.hasPage()) {
				++i;
				File f = new File("/Users/Bowbaq/Desktop/output/Page_" + i);
				f.createNewFile();
				output = new BufferedWriter(new FileWriter(f));
				// parser.setBuilder(new RawTextBuilder(builder));
				parser.parse(XMLParser.getPage().revisions.get(0).text);
				output.write(builder.toString());
				output.flush();
			}
			// System.out.println(XMLParser.getPage().revisions.get(0).text);
			// System.out.println(builder.toString());
			System.out.println("END OF PARSING - PageNbr = " + i);
		} catch (final MWParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
