/*
 *  Copyright [2010] [Fabien Poulard <fabien.poulard@univ-nantes.fr>, Maxime Bury, Maxime Rihouey] 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package org.apache.uima.mediawiki.cr.factory;

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

import org.apache.uima.UIMAFramework;
import org.apache.uima.mediawiki.cr.parser.MWDumpReader;
import org.apache.uima.mediawiki.cr.parser.MWRevisionFilter;
import org.apache.uima.mediawiki.cr.parser.MWTimeStampFilter;
import org.apache.uima.mediawiki.cr.parser.MWTitleFilter;
import org.apache.uima.mediawiki.cr.parser.MWDumpReader.MWParseException;
import org.apache.uima.mediawiki.cr.types.MWSiteinfo;
import org.apache.uima.mediawiki.cr.util.Tools;
import org.apache.uima.util.Level;

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
 * @see org.apache.uima.mediawiki.cr.parser.MWDumpReader MWDumpReader
 */
public class MWDumpReaderFactory {
	/** The input stream we are reading from */
	private static InputStream		inputstream;
	/** The file from which the inputstream was created */
	private static File				theDump;
	/** The factory that will provide us with various XMLStreamReaders */
	private static XMLInputFactory	factory;
	/** The XMLStreamReader that will be augmented with filters and passed on the the actual parser */
	private static XMLStreamReader	streamReader;

	/**
	 * Initialize the factory with the provided XML input stream. You may now get a basic parser by calling
	 * {@link #getParser()}
	 * 
	 * @param theXMLDump
	 *            the XML stream to read from.
	 * @throws FactoryConfigurationError
	 *             if something goes wrong.
	 */
	public static void initialize(File theXMLDump) throws FactoryConfigurationError {
		// Save a reference to the file
		theDump = theXMLDump;
		try {
			// Open the XML file
			inputstream = Tools.openInputFile(theXMLDump);
			// Create a basic XMLStreamReader reading from it.
			factory = XMLInputFactory.newInstance();
			factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
			streamReader = factory.createXMLStreamReader(inputstream);
		} catch (final XMLStreamException e) {
			throw new FactoryConfigurationError(e);
		} catch (final IOException e) {
			throw new FactoryConfigurationError(e);
		}
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
	public static MWDumpReader getParser() throws MWParseException {
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
	public static void clearFilters() throws IOException, XMLStreamException {
		try {
			// Close the current resources
			inputstream.close();
			streamReader.close();
		} catch (final Exception e) {
			UIMAFramework.getLogger().log(Level.WARNING, "An exception occured while attempting to close the ressources" + e.getMessage());
		}
		// Initialize new ones
		inputstream = Tools.openInputFile(theDump);
		streamReader = factory.createXMLStreamReader(inputstream);
	}

	/**
	 * This method attempts to cleanly free the ressources used by the factory.
	 */
	public static void close() {
		try {
			inputstream.close();
			streamReader.close();
		} catch (final Exception e) {
			UIMAFramework.getLogger().log(Level.WARNING, "An exception occured while attempting to close the ressources" + e.getMessage());
		}
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
	public static void addTitleListFilter(String cfgList, boolean exact) throws IOException, XMLStreamException {
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
				if (!title.isEmpty()) {
					myList.add(title);
				}
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
	public static void addTitleRegexFilter(String regex) throws XMLStreamException {
		streamReader = factory.createFilteredReader(streamReader, new TitleRegexFilter(regex));
	}

	/**
	 * This method allows you to setup a filter on the namespaces you want to consider. It takes as an input a
	 * string, that should be formed by integers separated by comas. (Ex : "1, 2, 5, 101"). This integers are
	 * the keys associated with the namespaces. This is why we need to have the website info object, so we can
	 * tell what is the string prefix that corresponds to each key.
	 * <p>
	 * The default behaviour of this filter is to include only the specified namespace in the results. If you
	 * want to exclude namespaces, you can prefix the key list by a '!' (Ex : "! 1, 2, 5, 101")
	 * 
	 * @param theSiteInfo
	 *            The website info gathered by the parser.
	 * @param cfgNamespaces
	 *            The list of namespaces to filter.
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 */
	public static void addNamespaceFilter(MWSiteinfo theSiteInfo, String cfgNamespaces) throws XMLStreamException {
		int nskey;
		boolean exclude = false;
		final List<String> myList = new ArrayList<String>();
		final Set<Integer> keyList = new HashSet<Integer>();

		// If the string starts with a '!' we set the exclude flag to true
		if (cfgNamespaces.startsWith("!")) {
			cfgNamespaces = cfgNamespaces.substring(1);
			exclude = true;
		}
		// We separate the key values
		final String[] namespaces = cfgNamespaces.trim().split(",");

		for (final String ns : namespaces) {
			ns.trim();
			try {
				if (!ns.isEmpty()) {
					nskey = Integer.parseInt(ns);
					keyList.add(nskey);
				}
			} catch (final NumberFormatException e) {
				// If we fail to parse the key value, then nothing is added to the set
			}
		}
		// We look for the valid keys in the set we built, and add the corresponding prefixes to the list
		for (final int key : keyList)
			if (theSiteInfo.namespaces.hasIndex(key)) {
				myList.add(theSiteInfo.namespaces.getPrefix(key));
			}
		// Create the new filtered XML stream reader.
		streamReader = factory.createFilteredReader(streamReader, new NamespaceFilter(myList, exclude));
	}

	/**
	 * This method is just a shortcut for the namespace filter, allowing you to filter out all the talk pages
	 * easily. Talk pages have an odd namespace key, compared to the non-talk which have an even one.
	 * 
	 * @param theSiteInfo
	 *            The website info gathered by the parser.
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 */
	public static void addExcludeTalkFilter(MWSiteinfo theSiteInfo) throws XMLStreamException {
		// A list of discussion namespace to ignore
		final ArrayList<String> excludedNamespace = new ArrayList<String>();
		// The map containing the (key, namespace) couples.
		final Map<Integer, String> namespaceMap = theSiteInfo.namespaces.getMap();

		for (final int key : namespaceMap.keySet())
			if (key > 0 && key % 2 == 1) {
				// We add the concerned namespaces to the exclude list
				excludedNamespace.add(namespaceMap.get(key));
			}
		// Create the filter with the proper list of namespaces.
		streamReader = factory.createFilteredReader(streamReader, new NamespaceFilter(excludedNamespace, true));
	}

	/**
	 * This method provides filtering on the revision's id. It works in a very similar fashion as the
	 * {@link #addTitleListFilter(String, boolean)} method described earlier. The ids you want to keep should
	 * appear only one by line in the provided file. You can comment a line with a '#'.
	 * 
	 * @param cfgRevisionList
	 *            The file we are reading from (containing the list of ids)
	 * @throws IOException
	 *             If we fail to open the file containing the ids
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 */
	public static void addRevisionFilter(String cfgRevisionList) throws IOException, XMLStreamException {
		final ArrayList<Integer> myList = new ArrayList<Integer>();
		final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(cfgRevisionList), "utf-8"));
		String line = input.readLine();

		// Read all the ids from the file, one per line
		while (line != null) {
			line = line.trim();
			if (line.length() > 0 && !line.startsWith("#")) {
				try {
					myList.add(Integer.parseInt(line));
				} catch (final NumberFormatException e) {
					// We just don't add it to the list if we can't parse it to a integer
				}
			}
			line = input.readLine();
		}
		input.close();
		// Create the filtered XML stream reader with the proper id list.
		streamReader = factory.createFilteredReader(streamReader, new MWRevisionFilter(myList));
	}

	/**
	 * This method tells the MWArticle factory to keep only the latest revision for each article.
	 */
	public static void addLatestOnlyFilter() {
		MWArticleFactory.setLatestOnly(true);
	}

	/**
	 * This allows you to filter out all the revisions that were not contributed before the given timestamp.
	 * The timestamp format we consider is the following : yyyy-MM-dd'T'HH:mm:ss'Z' (Ex :
	 * 2001-06-21T10:20:30Z).
	 * <p>
	 * Be aware that this filter internally uses a {@link Calendar} object that is lenient. That means that if
	 * you try to specify the month 20, it will try to work arround it and probably consider it like December
	 * + 8 month.
	 * 
	 * @param cfgBeforeTimestamp
	 *            The timestamp to check for.
	 * @throws ParseException
	 *             If the provided timestamp doesn't follow the format specification.
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 */
	public static void addBeforeTimestampFilter(String cfgBeforeTimestamp) throws ParseException, XMLStreamException {
		final Calendar reference = Calendar.getInstance();
		reference.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(cfgBeforeTimestamp));
		streamReader = factory.createFilteredReader(streamReader, new BeforeTimestampFilter(reference));
	}

	/**
	 * This method works exactly the same way as the {@link #addBeforeTimestampFilter(String)} method, except
	 * for the fact that here only the revisions contributed after the given timestamp are considered valid.
	 * 
	 * @param cfgAfterTimestamp
	 *            The timestamp to check for.
	 * @throws ParseException
	 *             If the provided timestamp doesn't follow the format specification.
	 * @throws XMLStreamException
	 *             If we fail to create the new filtered XML stream reader.
	 */
	public static void addAfterTimestampFilter(String cfgAfterTimestamp) throws ParseException, XMLStreamException {
		final Calendar reference = Calendar.getInstance();
		reference.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(cfgAfterTimestamp));
		streamReader = factory.createFilteredReader(streamReader, new AfterTimestampFilter(reference));
	}

	/**
	 * This class provides titles filtering with regular expressions to the parser.
	 * 
	 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
	 */
	public static class TitleRegexFilter extends MWTitleFilter {
		private final Pattern	myPattern;

		/**
		 * Compiles the string parameter into a Pattern object.
		 * 
		 * @param regex
		 *            The regular expression we want the titles to match.
		 */
		public TitleRegexFilter(String regex) {
			myPattern = Pattern.compile(regex);
		}

		/**
		 * Checks if the title matches the regex or not. We don't consider the namespace prefix as part of the
		 * title for this check.
		 * 
		 * @return <code>true</code> if the title passes, <code>false</code> otherwise.
		 */
		@Override
		protected final boolean titleMatch(String title) {
			final int pos = title.indexOf(':');
			title = pos == -1 ? title.trim() : title.substring(pos).trim();
			return myPattern.matcher(title).matches();
		}
	}

	/**
	 * This class provides title matching against a list to the parser.
	 * 
	 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
	 */
	public static class TitleListFilter extends MWTitleFilter {
		private final List<String>	listOfTitles;
		private final boolean		exact;

		/**
		 * Initialize the filter's parameters.
		 * 
		 * @param myList
		 *            The list of titles to match against.
		 * @param exact
		 *            Flag that tells if should only consider the default namespace.
		 */
		public TitleListFilter(List<String> myList, boolean exact) {
			listOfTitles = myList;
			this.exact = exact;
		}

		/**
		 * Check is the title is present in the white list or not. If the exact flag is set to true, the page
		 * also has to be in the default namespace.
		 * 
		 * @return <code>true</code> if the title passes, <code>false</code> otherwise.
		 */
		@Override
		protected final boolean titleMatch(String title) {
			final int pos = title.indexOf(':');
			title = pos == -1 ? title : title.substring(pos).trim();
			final boolean found = listOfTitles.contains(title);
			if (pos == -1)
				// If the page is in the default namespace.
				return found;
			else if (pos != -1 && !exact)
				// If it's not in the default namespace but we don't want an exact match
				return found;
			else
				// If it's not in the default namespace and we DO want an exact match, it's discarded
				return false;
		}
	}

	/**
	 * This class provides namespace filtering against a list to the parser.
	 * 
	 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
	 */
	public static class NamespaceFilter extends MWTitleFilter {
		private final List<String>	listOfNamespaces;
		private String				namespace;
		private final boolean		exclude;

		/**
		 * Initialize the filter's parameters.
		 * 
		 * @param myList
		 *            The list of namespaces to match against.
		 * @param exclude
		 *            Flag indicating whether we should include only or exclude the namespaces in the list.
		 */
		public NamespaceFilter(List<String> myList, boolean exclude) {
			listOfNamespaces = myList;
			this.exclude = exclude;
		}

		/**
		 * Check if the page's namespace is in the list or not. Then depending on whether we include or
		 * exclude those namespaces, it tells if the pages passes or not.
		 * 
		 * @return <code>true</code> if the page has a valid namespace, <code>false</code> otherwise.
		 */
		@Override
		protected boolean titleMatch(String title) {
			final int pos = title.indexOf(':');
			namespace = pos != -1 ? title.substring(0, pos).trim() : "";
			final boolean found = listOfNamespaces.contains(namespace);
			if (exclude)
				return !found;
			return found;
		}
	}

	/**
	 * This class provides revisions filtering depending on their timestamp to the parser.
	 * 
	 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
	 */
	public static class AfterTimestampFilter extends MWTimeStampFilter {
		private final Calendar	reference;
		private final Calendar	temp;

		/**
		 * Initialize the filer's variables.
		 * 
		 * @param reference
		 *            the reference timestamp we are matching against.
		 */
		public AfterTimestampFilter(Calendar reference) {
			this.reference = reference;
			temp = Calendar.getInstance();
		}

		/**
		 * Check if the revision's timestamp is after the reference one. If we fail to parse the timestamp, we
		 * consider it valid.
		 * 
		 * @return <code>true</code> if the revision's timestamp is after the reference one,
		 *         <code>false</code> otherwise.
		 */
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

	/**
	 * This class provides revisions filtering depending on their timestamp to the parser.
	 * 
	 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
	 */
	public static class BeforeTimestampFilter extends MWTimeStampFilter {
		private final Calendar	reference;
		private final Calendar	temp;

		/**
		 * Initialize the filer's variables.
		 * 
		 * @param reference
		 *            the reference timestamp we are matching against.
		 */
		public BeforeTimestampFilter(Calendar reference) {
			this.reference = reference;
			temp = Calendar.getInstance();
		}

		/**
		 * Check if the revision's timestamp is before the reference one. If we fail to parse the timestamp,
		 * we consider it valid.
		 * 
		 * @return <code>true</code> if the revision's timestamp is before the reference one,
		 *         <code>false</code> otherwise.
		 */
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
}
