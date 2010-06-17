package uima.wikipedia;

import java.io.File;
import java.util.Date;

import javax.xml.stream.XMLStreamException;

import uima.wikipedia.factory.MWDumpReaderFactory;
import uima.wikipedia.parser.MWDumpReader;
import uima.wikipedia.parser.MWParseException;

public class Test {

	/**
	 * @param args
	 * @throws XMLStreamException
	 */
	public static void main(String[] args) throws XMLStreamException {
		MWDumpReaderFactory f;
		int i = 0;
		final Date start = new Date();
		try {
			f = new MWDumpReaderFactory(new File("/Users/Bowbaq/Downloads/frwikinews-20100420-pages-meta-history.xml"));
			f.addLatestOnlyFilter();
			final MWDumpReader dr = f.getParser();
			System.out.println(dr.getSiteInfo().toString());
			while (dr.hasPage()) {
				dr.getPage();
				// System.out.println(a.toString());
				++i;
				// System.out.println(i);
			}
			final Date end = new Date();
			final long time = (end.getTime() - start.getTime()) / 1000;
			System.out.println("END OF PARSING - Time = " + time + " PageNbr = " + i);
		} catch (final MWParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
