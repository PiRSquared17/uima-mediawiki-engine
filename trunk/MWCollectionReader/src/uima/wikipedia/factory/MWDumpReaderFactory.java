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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import uima.wikipedia.parser.MWDumpReader;
import uima.wikipedia.parser.MWRevisionFilter;
import uima.wikipedia.parser.MWTimeStampFilter;
import uima.wikipedia.parser.MWTitleFilter;
import uima.wikipedia.parser.MWDumpReader.MWParseException;
import uima.wikipedia.types.MWArticle;
import uima.wikipedia.types.MWSiteinfo;
import uima.wikipedia.util.Tools;

/**
 * This class is the factory for the parser. It's designed mainly to handle smoothly the process of adding
 * filters to the XML stream before it's actually processed by the parser. It also serves as the entry point
 * to open the file we will be reading from.
 * <p>
 * The filters are almost all at the XML stream level. There is of course an exception : the "latest only"
 * filter that allows you to keep only the latest revision in an article, is at the parser level. That is
 * because at the XML stream level, we only see one revision at a time, and so we don't have two of them to
 * compare. All the filters will be detailed further in this documentation.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 * @see uima.wikipedia.type.MWDumpReader MWDumpReader
 */
public class MWDumpReaderFactory {
	/** The input stream we are reading from */
	private InputStream				inputstream;
	/** The file from which the inputstream was created */
	private final File				theDump;
	/** The factory that will provide us with various XMLStreamReaders */
	private final XMLInputFactory	factory;
	/** The XMLStreamReader that will be augmented with filters and passed on the the actual parser */
	private XMLStreamReader			streamReader;
	/** Some other variables */
	private boolean					latestOnly;

	/**
	 * Initialize the factory with the provided XML input stream. You may now get a basic parser by calling
	 * {@link #getParser()}
	 * 
	 * @param theXMLDump
	 *            the XML stream to read from.
	 * @throws FactoryConfigurationError
	 *             if something goes wrong.
	 */
	public MWDumpReaderFactory(File theXMLDump) throws FactoryConfigurationError {
		// Save a reference to the file
		theDump = theXMLDump;
		try {
			// Open the XML file
			inputstream = Tools.openInputFile(theXMLDump);
			// Create a basic XMLStreamReader reading from it.
			factory = XMLInputFactory.newInstance();
			streamReader = factory.createXMLStreamReader(inputstream);
		} catch (final XMLStreamException e) {
			throw new FactoryConfigurationError(e);
		} catch (final IOException e) {
			throw new FactoryConfigurationError(e);
		}
		// Set the latesOnly flag to false by default (we keep all the revisions)
		latestOnly = false;
	}

	/**
	 * <p>
	 * Returns the current parser as crafted by the factory. It includes the filter(s) that you may have
	 * specified. You may as well get the default one after calling {@link #clearFilters()}
	 * <p>
	 * Even though you could in theory get several parsers from the same factory, this is not a good idea.
	 * They would all be reading from the same XML stream, and they are not designed for concurrent use. This
	 * would most likely result in each parser reporting the malformedness of the XML stream.
	 * <p>
	 * During the initialisation of the parser, it skips some unuseful heading elements. If the input stream
	 * is malformed it will raise a parse exception.
	 * 
	 * @return A new parser for the provided input stream.
	 * @throws MWParseException
	 *             If the parser encounters a malformation in the underlying XML document.
	 * @throws uima.wikipedia.parser.MWDumpReader.MWParseException
	 */
	public MWDumpReader getParser() throws MWParseException, uima.wikipedia.parser.MWDumpReader.MWParseException {
		if (latestOnly)
			return new MWDumpReaderLatestOnly(streamReader);
		return new MWDumpReader(streamReader);
	}

	/**
	 * Clears all the filters that may have been applied to the XML stream. It does so by closing the current
	 * streams and creating new ones from the original file. This also has the effect to reset the cursors to
	 * 0.
	 * 
	 * @throws XMLStreamException
	 *             If the parser encounters a malformation in the underlying XML document.
	 * @throws IOException
	 *             If the factory fails to open the dump file again.
	 */
	public void clearFilters() throws XMLStreamException, IOException {
		// Close the current streams
		inputstream.close();
		streamReader.close();
		// Initialize new ones
		inputstream = Tools.openInputFile(theDump);
		streamReader = factory.createXMLStreamReader(inputstream);
	}

	/**
	 * This method allows you to set up a title filter easily. The titles you want to keep should be place in
	 * a text file, one by line. You may comment a line with a '#' character at its beginning. You may also
	 * use underscores instead of whitespaces in the titles.
	 * <p>
	 * The exact flag allows you to specify if the match should be exact or not. An exact match means that
	 * only the pages with the specified title and which are in the default namespace, will get through.
	 * 
	 * @param cfgList
	 *            The file we are reading from (containing the list of titles)
	 * @param exact
	 *            A flag indicating if the match should be exact or not
	 * @throws IOException
	 *             If we fail to open the file containing the title list
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 */
	public void addTitleListFilter(String cfgList, boolean exact) throws IOException, XMLStreamException {
		String line, title;
		final List<String> myList = new ArrayList<String>();
		final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(cfgList), "utf-8"));

		line = input.readLine();
		while (line != null) {
			// If the line isn't commented
			if (!line.startsWith("#")) {
				// Replace the underscores by spaces and trim
				title = line.replace("_", " ").trim();
				// Build the list
				if (!title.isEmpty())
					myList.add(title);
			}
			line = input.readLine();
		}
		input.close();
		// Create a new filtered stream
		streamReader = factory.createFilteredReader(streamReader, new TitleListFilter(myList, exact));
	}

	/**
	 * <p>
	 * Filters out all the pages which titles do not match the provided regular expression. The match takes
	 * place only on the actual title, we don't consider the eventual namespace prefix.
	 * <p>
	 * Example : "^Foo bar.*" will allow all the pages with a title starting with "Foo bar"
	 * 
	 * @param regex
	 *            The regular expression you want the titles to match
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 * @see <a href="http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html#sum"> RegEx sum up
	 *      </a>
	 */
	public void addTitleRegexFilter(String regex) throws XMLStreamException {
		streamReader = factory.createFilteredReader(streamReader, new TitleRegexFilter(regex));
	}

	/**
	 * @param theSiteInfo
	 * @param cfgNamespaces
	 * @throws XMLStreamException
	 */
	public void addNamespaceFilter(MWSiteinfo theSiteInfo, String cfgNamespaces) throws XMLStreamException {
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
			if (theSiteInfo.namespaces.hasIndex(key))
				myList.add(theSiteInfo.namespaces.getPrefix(key));

		streamReader = factory.createFilteredReader(streamReader, new NamespaceFilter(myList, exclude));
	}

	public void addExcludeTalkFilter(MWSiteinfo theSiteInfo) throws XMLStreamException {
		// A list of discussion namespace to ignore
		final ArrayList<String> excludedNamespace = new ArrayList<String>();
		// The map containing the (key, namespace) couples.
		final Map<Integer, String> namespaceMap = theSiteInfo.namespaces.getMap();

		for (int key : namespaceMap.keySet())
			if (key > 0 && key % 2 == 1)
				// We add the concerned namespaces to the exclude list
				excludedNamespace.add(namespaceMap.get(key));
		// Create the filter with the proper list of namespaces.
		streamReader = factory.createFilteredReader(streamReader, new NamespaceFilter(excludedNamespace, true));
	}

	public void addRevisionFilter(String cfgRevisionList) throws IOException, XMLStreamException {
		final ArrayList<Integer> myList = new ArrayList<Integer>();
		final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(cfgRevisionList), "utf-8"));
		String line = input.readLine();

		while (line != null) {
			line = line.trim();
			if (line.length() > 0 && !line.startsWith("#"))
				myList.add(Integer.parseInt(line));
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
			final int pos = title.indexOf(':');
			title = pos == -1 ? title.trim() : title.substring(pos).trim();
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
