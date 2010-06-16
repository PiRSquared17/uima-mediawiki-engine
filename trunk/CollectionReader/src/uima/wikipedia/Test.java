package uima.wikipedia;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import javax.xml.stream.XMLStreamException;

import uima.wikipedia.factory.MWDumpReaderFactory;
import uima.wikipedia.parser.MWDumpReader;
import uima.wikipedia.parser.MWParseException;
import uima.wikipedia.types.MWArticle;

public class Test {

	/**
	 * @param args
	 * @throws XMLStreamException
	 */
	public static void main(String[] args) throws XMLStreamException {
		InputStream infile;
		MWDumpReaderFactory f;
		MWArticle a;
		int i = 0;
		Date start = new Date();
		try {
			f = new MWDumpReaderFactory(new File("/Users/Bowbaq/Downloads/frwikinews-20100420-pages-meta-history.xml"));
			f.addLatestOnlyFilter();
			MWDumpReader dr = f.getParser();
			System.out.println(dr.getSiteInfo().toString());
			while (dr.hasPage()) {
				a = dr.getPage();
				// System.out.println(a.toString());
				++i;
				// System.out.println(i);
			}
			Date end = new Date();
			long time = (end.getTime() - start.getTime()) / 1000;
			System.out.println("END OF PARSING - Time = " + time + " PageNbr = " + i);
		} catch (MWParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
