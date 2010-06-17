package uima.wikipedia.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import uima.wikipedia.parser.MWDumpReader;
import uima.wikipedia.parser.MWParseException;
import uima.wikipedia.parser.MWRevisionFilter;
import uima.wikipedia.parser.MWTimeStampFilter;
import uima.wikipedia.parser.MWTitleFilter;
import uima.wikipedia.types.MWArticle;
import uima.wikipedia.types.MWSiteInfo;
import uima.wikipedia.util.Tools;

public class MWDumpReaderFactory {
	/** The input stream we are reading from */
	private InputStream				inputstream;
	/** The file from which the inputstream was created */
	private final File				theDump;
	/** The factory that will provide us with various XMLStreamReaders */
	private final XMLInputFactory	factory;
	/**
	 * The XMLStreamReader that will be augmented with filters and passed on the the actual parser
	 */
	private XMLStreamReader			streamReader;
	/** Some other variables */
	private boolean					latestOnly;

	/**
	 * Initialize the factory with the provided XML input stream. You may now get a basic parser by calling
	 * {@link #getParser()}
	 * 
	 * @param Tools
	 *            .openInputFile(theXMLDump) the XML stream to read from.
	 * @throws FactoryConfigurationError
	 *             if something goes wrong.
	 */
	public MWDumpReaderFactory(File theXMLDump) throws FactoryConfigurationError {
		theDump = theXMLDump;
		try {
			inputstream = Tools.openInputFile(theXMLDump);
			factory = XMLInputFactory.newInstance();
			streamReader = factory.createXMLStreamReader(inputstream);
		} catch (final XMLStreamException e) {
			throw new FactoryConfigurationError(e);
		} catch (final IOException e) {
			throw new FactoryConfigurationError(e);
		}
		latestOnly = false;
	}

	/**
	 * <p>
	 * Returns the current parser as crafted by the factory. It includes the filter(s) that you may have specified. You
	 * may get as many as you want. You may also get a different one by adding new filters. You may as well get the
	 * default one after calling {@link #clearFilters()}
	 * <p>
	 * Even though you can get several parsers at the same time, it is not warranted that they are threadsafe. Watchout.
	 * <p>
	 * During the initialisation of the parser, it skips some unuseful heading elements. If the input stream is
	 * malformed it will raise a parse exception.
	 * 
	 * @return a new parser for the provided input stream
	 * @throws MWParseException
	 *             if the parser encounters a malformation in the underlying XML document.
	 */
	public MWDumpReader getParser() throws MWParseException {
		if (latestOnly)
			return new MWDumpReaderLatestOnly(streamReader);
		return new MWDumpReader(streamReader);
	}

	/**
	 * Clears all the filters that may have been applied to the XML stream.
	 * 
	 * @throws XMLStreamException
	 *             if the parser encounters a malformation in the underlying XML document.
	 * @throws IOException
	 */
	public void clearFilters() throws XMLStreamException, IOException {
		inputstream.close();
		inputstream = Tools.openInputFile(theDump);
		streamReader = factory.createXMLStreamReader(inputstream);
	}

	public void addTitleListFilter(String cfgList, boolean exact) throws IOException, XMLStreamException {
		String line, title;
		final List<String> myList = new ArrayList<String>();
		final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(cfgList), "utf-8"));

		line = input.readLine();
		while (line != null) {
			if (!line.startsWith("#")) {
				title = line.replace("_", " ").trim();
				if (!title.isEmpty()) {
					myList.add(title);
				}
			}
			line = input.readLine();
		}
		input.close();
		streamReader = factory.createFilteredReader(streamReader, new TitleListFilter(myList, exact));
	}

	/**
	 * <p>
	 * Filters out all the pages which titles do not match the provided regex.
	 * <p>
	 * Example : "Foo bar.*" will allow all the pages with a title starting with "Foo bar"
	 * 
	 * @param regex
	 *            the regular expression you want the titles to match
	 * @throws XMLStreamException
	 *             if the parser encounters a malformation in the underlying XML document.
	 * @see <a href="http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html#sum"> RegEx sum up </a>
	 */
	public void addTitleRegexFilter(String regex) throws XMLStreamException {
		streamReader = factory.createFilteredReader(streamReader, new TitleRegexFilter(regex));
	}

	public void addNamespaceFilter(MWSiteInfo theSiteInfo, String cfgNamespaces) throws XMLStreamException {
		int nskey;
		boolean exclude = false;
		final List<String> myList = new ArrayList<String>();
		final Set<Integer> keyList = new HashSet<Integer>();

		if (cfgNamespaces.startsWith("!")) {
			cfgNamespaces = cfgNamespaces.substring(1);
			exclude = true;
		}
		final String[] namespaces = cfgNamespaces.split(",");
		for (final String ns : namespaces) {
			ns.trim();
			try {
				nskey = Integer.parseInt(ns);
			} catch (final NumberFormatException e) {
				nskey = 0;
			}
			keyList.add(nskey);
		}
		for (final int key : keyList)
			if (theSiteInfo.namespaces.hasIndex(key)) {
				myList.add(theSiteInfo.namespaces.getPrefix(key));
			}

		streamReader = factory.createFilteredReader(streamReader, new NamespaceFilter(myList, exclude));
	}

	public void addExcludeTalkFilter(MWSiteInfo theSiteInfo) throws XMLStreamException {
		// A list of discussion namespace to ignore
		final ArrayList<String> excludedNS = new ArrayList<String>();
		// Iterator over the namespace object
		final Iterator<Entry<Integer, String>> it = theSiteInfo.namespaces.orderedEntries();
		// A variable to store each couple (key, stringvalue)
		Entry<Integer, String> ns;

		while (it.hasNext()) {
			ns = it.next();
			// Talk namespaces have a odd key
			if (ns.getKey() > 0 && ns.getKey() % 2 == 1) {
				excludedNS.add(ns.getValue());
			}
		}
		// We add the concerned namespaces to the exclude list
		streamReader = factory.createFilteredReader(streamReader, new NamespaceFilter(excludedNS, true));
	}

	public void addRevisionFilter(String cfgRevisionList) throws IOException, XMLStreamException {
		final ArrayList<Integer> myList = new ArrayList<Integer>();
		final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(cfgRevisionList), "utf-8"));
		String line = input.readLine();

		while (line != null) {
			line = line.trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				myList.add(Integer.parseInt(line));
			}
			line = input.readLine();
		}
		input.close();

		streamReader = factory.createFilteredReader(streamReader, new MWRevisionFilter(myList));
	}

	public void addLatestOnlyFilter() {
		latestOnly = true;
	}

	public void addBeforeTimestampFilter(String cfgBeforeTimestamp) throws ParseException, XMLStreamException {
		final Calendar reference = Calendar.getInstance();
		reference.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(cfgBeforeTimestamp));
		streamReader = factory.createFilteredReader(streamReader, new BeforeTimestampFilter(reference));
	}

	public void addAfterTimestampFilter(String cfgAfterTimestamp) throws ParseException, XMLStreamException {
		final Calendar reference = Calendar.getInstance();
		reference.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(cfgAfterTimestamp));
		streamReader = factory.createFilteredReader(streamReader, new AfterTimestampFilter(reference));
	}

	public class TitleRegexFilter extends MWTitleFilter {
		private final Pattern	myPattern;

		public TitleRegexFilter(String regex) {
			myPattern = Pattern.compile(regex);
		}

		@Override
		protected final boolean titleMatch(String title) {
			return myPattern.matcher(title).matches();
		}
	}

	public class TitleListFilter extends MWTitleFilter {
		private final List<String>	listOfTitles;
		private final boolean		exact;

		public TitleListFilter(List<String> myList, boolean exact) {
			listOfTitles = myList;
			this.exact = exact;
		}

		@Override
		protected final boolean titleMatch(String title) {
			for (final String match : listOfTitles) {
				final int pos = title.indexOf(':');
				if (pos != -1 && !exact) {
					title = title.substring(pos);
					if (title.equals(match))
						return true;
				} else if (pos == -1)
					if (title.equals(match))
						return true;
			}
			return false;
		}
	}

	public class NamespaceFilter extends MWTitleFilter {
		private final List<String>	listOfNamespaces;
		private final boolean		exclude;
		private boolean				found;
		private String				namespace;

		public NamespaceFilter(List<String> myList, boolean exclude) {
			listOfNamespaces = myList;
			this.exclude = exclude;
		}

		@Override
		protected boolean titleMatch(String title) {
			final int pos = title.indexOf(':');
			namespace = pos != -1 ? title.substring(0, pos) : "";
			found = listOfNamespaces.contains(namespace.trim());
			if (exclude)
				return !found;
			return found;
		}
	}

	public class AfterTimestampFilter extends MWTimeStampFilter {
		private final Calendar	reference;
		private final Calendar	temp;

		public AfterTimestampFilter(Calendar reference) {
			this.reference = reference;
			temp = Calendar.getInstance();
		}

		@Override
		protected boolean timeStampMatch(String timestamp) {
			try {
				temp.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timestamp));
				return temp.after(reference);
			} catch (final ParseException e) {
				return true;
			}
		}
	}

	public class BeforeTimestampFilter extends MWTimeStampFilter {
		private final Calendar	reference;
		private final Calendar	temp;

		public BeforeTimestampFilter(Calendar reference) {
			this.reference = reference;
			temp = Calendar.getInstance();
		}

		@Override
		protected boolean timeStampMatch(String timestamp) {
			try {
				temp.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timestamp));
				return temp.before(reference);
			} catch (final ParseException e) {
				return true;
			}
		}
	}

	public class MWDumpReaderLatestOnly extends MWDumpReader {
		public MWDumpReaderLatestOnly(XMLStreamReader reader) throws MWParseException {
			super(reader);
		}

		@Override
		public MWArticle getPage() {
			pageComputed = false;
			thePage.latestOnly();
			return thePage.newInstance();
		}
	}
}
