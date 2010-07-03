package org.apache.uima.mediawiki.cr;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.mediawiki.cr.factory.MWDumpReaderFactory;
import org.apache.uima.mediawiki.cr.parser.MWDumpReader;
import org.apache.uima.mediawiki.cr.parser.MWDumpReader.MWParseException;
import org.apache.uima.mediawiki.cr.types.MWArticle;
import org.apache.uima.mediawiki.cr.types.MWRevision;
import org.apache.uima.mediawiki.cr.types.MWSiteinfo;
import org.apache.uima.mediawiki.types.Article;
import org.apache.uima.mediawiki.types.Revision;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

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

/**
 * This class serves as the front end for the Apache UIMA framework. It initializes the parser, adds the
 * filters, and then gets the pages one by one until the end of the file.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWCollectionReader extends CollectionReader_ImplBase {

	/** Component related constants */
	public static String		COMPONENT_NAME				= "MediaWiki Collection Reader";
	public static String		COMPONENT_VERSION			= "1.0";
	public static String		COMPONENT_ID				= COMPONENT_NAME + "-" + COMPONENT_VERSION;

	/** Setting up the logger for this class */
	private static Logger		theLogger;
	/** Configuration parameters */
	private static final String	PARAM_XMLDUMP				= "InputFileXmlDump";
	/** Filters parameters */
	private static String		PARAM_FLG_IGNORETALKS		= "IgnoreTalks";
	private static String		PARAM_INP_NAMESPACE			= "ConfigNamespacesFilter";
	private static String		PARAM_FLG_LATESTREVISION	= "LatestRevisionOnly";
	private static String		PARAM_INP_TITLEMATCH		= "ConfigTitleMatch";
	private static String		PARAM_INP_LIST				= "ConfigListFilter";
	private static String		PARAM_INP_EXACTLIST			= "ConfigExactListFilter";
	private static String		PARAM_INP_REVISIONLIST		= "ConfigRevisionListFilter";
	private static String		PARAM_INP_BEFORETM			= "ConfigBeforeTimestampFilter";
	private static String		PARAM_INP_AFTERTM			= "ConfigAfterTimestampFilter";
	/** Configuration values */
	private static File			theXMLDump;
	/** Progress counter */
	private int					cCasProduced;
	/** XML processing */
	private static MWDumpReader	parser;

	/** Website info, useful to recover the namespaces */
	private static MWSiteinfo	theSiteInfo;

	/**
	 * In this method we initialize a first parser to try to gather the website info. We then clear the
	 * factory, add the filters specified by the user, and instantiate a new parser. We also create a logger.
	 */
	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		// Prepare the logger
		theLogger = getLogger();
		theLogger.log(Level.INFO, "Initializing the collection reader");
		// Retrieve the XMLDump file
		theXMLDump = new File((String) getConfigParameterValue(PARAM_XMLDUMP));
		// Check if the XML dump file exists
		if (theXMLDump.exists() && theXMLDump.isFile()) {
			try {
				// Initialize the factory
				MWDumpReaderFactory.initialize(theXMLDump);
				// Get a first parser to compute the website info, in particular the namespaces.
				parser = MWDumpReaderFactory.getParser();
				if (parser.hasSiteInfo())
					theSiteInfo = parser.getSiteInfo();
				else {
					// Default empty website info.
					theSiteInfo = new MWSiteinfo("", "", "", "", new HashMap<Integer, String>());
					theLogger.log(Level.INFO, "The website info is unavailable, we know nothing about the namespaces.");
				}
				MWDumpReaderFactory.clearFilters();
				// Configure the various filters that may have been specified by the user.
				configureFilters();
				// Get a new parser that takes in account those filters.
				parser = MWDumpReaderFactory.getParser();
				// Log start
				theLogger.log(Level.INFO, COMPONENT_ID + " initialized.");
			} catch (final FactoryConfigurationError e) {
				throw new ResourceInitializationException("There was an error initializing the XML parser", null);
			} catch (final IOException e) {
				throw new ResourceInitializationException("The path to the XML dump file (?) is invalid", null);
			} catch (final MWParseException e) {
				throw new ResourceInitializationException("The underlying XML document appears to be malformed", null);
			} catch (final XMLStreamException e) {
				throw new ResourceInitializationException("The underlying XML document appears to be malformed", null);
			}
			// Initialize the progress counter.
			cCasProduced = 0;
		} else
			throw new ResourceInitializationException("The path to the XML dump does not point to a valid file", null);
	}

	/**
	 * In this method we get an available page from the parser and put it into a CAS.
	 */
	@Override
	public void getNext(CAS newCas) throws IOException, CollectionException {
		final CAS RawWikiTextView = newCas.createView("RawWikiText");
		JCas newJCas;
		try {
			newJCas = RawWikiTextView.getJCas();
			populateJCas(newJCas, parser.getPage());
			// We are done with the CAS production
			cCasProduced++;
			if (cCasProduced % 100 == 0) {
				Runtime r = Runtime.getRuntime();
				UIMAFramework.getLogger().log(Level.INFO, cCasProduced + " CAS produced. " + r.freeMemory() + " memory left.");
				// Calling Garbage Collector
				r.gc();
			}
		} catch (final CASException e) {
			theLogger.log(Level.SEVERE, "Error while creating the CAS");
			throw new CollectionException("There was an error while creating the CAS", null);
		}
	}

	/**
	 * If the parser has a page, we should keep processing.
	 */
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return parser.hasPage();
	}

	/** We cannot do much with this one, we don't know how much there is. */
	@Override
	public Progress[] getProgress() {
		// We do not really know how much there is left
		return new Progress[] { new ProgressImpl(cCasProduced, 0, Progress.ENTITIES) };
	}

	/**
	 * Attempt to close the resources cleanly.
	 */
	@Override
	public void close() throws IOException {
		MWDumpReaderFactory.close();
		parser.close();
	}

	private void configureFilters() throws ResourceInitializationException {
		try {
			// Filter to ignore the talks pages
			final Boolean enIgnoreTalks = (Boolean) getConfigParameterValue(PARAM_FLG_IGNORETALKS);
			if (enIgnoreTalks != null && enIgnoreTalks) {
				MWDumpReaderFactory.addExcludeTalkFilter(theSiteInfo);
				theLogger.log(Level.INFO, "Added 'notalk' filter");
			}
			// Filter to consider only some namespaces
			final String cfgNamespaces = (String) getConfigParameterValue(PARAM_INP_NAMESPACE);
			if (cfgNamespaces != null) {
				MWDumpReaderFactory.addNamespaceFilter(theSiteInfo, cfgNamespaces);
				theLogger.log(Level.INFO, "Added 'namespace' filter with configuration : '" + cfgNamespaces + "'");
			}
			// Select only latest revisions
			final Boolean enLastRevision = (Boolean) getConfigParameterValue(PARAM_FLG_LATESTREVISION);
			if (enLastRevision != null && enLastRevision) {
				MWDumpReaderFactory.addLatestOnlyFilter();
				theLogger.log(Level.INFO, "Added 'latest' filter");
			}
			// Select only some pages depending on a regexp on name
			final String cfgTitleMatch = (String) getConfigParameterValue(PARAM_INP_TITLEMATCH);
			if (cfgTitleMatch != null) {
				MWDumpReaderFactory.addTitleRegexFilter(cfgTitleMatch);
				theLogger.log(Level.INFO, "Added 'titlematch' filter with configuration : '" + cfgTitleMatch + "'");
			}
			// Select only some pages specified in a list in a file
			final String cfgList = (String) getConfigParameterValue(PARAM_INP_LIST);
			if (cfgList != null) {
				MWDumpReaderFactory.addTitleListFilter(cfgList, false);
				theLogger.log(Level.INFO, "Added 'list' filter with configuration : '" + cfgList + "'");
			}
			final String cfgExactList = (String) getConfigParameterValue(PARAM_INP_EXACTLIST);
			if (cfgExactList != null) {
				MWDumpReaderFactory.addTitleListFilter(cfgExactList, true);
				theLogger.log(Level.INFO, "Added 'exactlist' filter with configuration : '" + cfgList + "'");
			}
			// Select only some revisions specified in a file
			final String cfgRevisionList = (String) getConfigParameterValue(PARAM_INP_REVISIONLIST);
			if (cfgRevisionList != null) {
				MWDumpReaderFactory.addRevisionFilter(cfgRevisionList);
				theLogger.log(Level.INFO, "Added 'revlist' filter with configuration : '" + cfgList + "'");
			}
			// Select only some data that have been produced before some time
			final String cfgBeforeTimestamp = (String) getConfigParameterValue(PARAM_INP_BEFORETM);
			if (cfgBeforeTimestamp != null) {
				MWDumpReaderFactory.addBeforeTimestampFilter(cfgBeforeTimestamp);
				theLogger.log(Level.INFO, "Added 'before' filter with configuration : '" + cfgList + "'");
			}
			// Select only some data that have been produced after some time
			final String cfgAfterTimestamp = (String) getConfigParameterValue(PARAM_INP_AFTERTM);
			if (cfgAfterTimestamp != null) {
				MWDumpReaderFactory.addAfterTimestampFilter(cfgAfterTimestamp);
				theLogger.log(Level.INFO, "Added 'after' filter with configuration : '" + cfgList + "'");
			}
		} catch (final Exception e) {
			theLogger.log(Level.SEVERE, "Failed to initialize one of the filters");
			throw new ResourceInitializationException(e);
		}
	}

	private void populateJCas(JCas newJCas, MWArticle page) throws CASException {
		int start, end;
		// Initialize the buffer where the text will be stored
		final StringBuilder casContent = new StringBuilder();
		// Create all the revision annotations
		for (final MWRevision myRevision : page.revisions) {
			// Set text
			start = casContent.length();
			casContent.append(myRevision.text);
			end = casContent.length();
			// Set annotations
			addRevisionAnnotation(newJCas, myRevision, start, end);
		}
		// All the textual content is collected now...
		newJCas.setDocumentText(casContent.toString());
		// We can add the Article annotation...
		addArticleAnnotation(newJCas, page, 0, casContent.length());
		addSourceDocumentAnnotation(newJCas, page, 0, casContent.length());
	}

	/**
	 * Adds a new source document annotation. We hope the CAS consumer component will use this to rename the
	 * files.
	 * 
	 * @param newJCas
	 *            the JCas on which the annotation is indexed
	 * @param page
	 *            the MWPage instance on which is based the Article
	 * @param start
	 *            the beginning offset of the annotation
	 * @param end
	 *            the ending offset of the annotation
	 */
	private void addSourceDocumentAnnotation(JCas newJCas, MWArticle page, int start, int end) {
		final SourceDocumentInformation info = new SourceDocumentInformation(newJCas);
		info.setBegin(start);
		final File temp = new File(theXMLDump, "NS_" + page.namespace + "_" + page.title.replace('/', '.'));
		try {
			final String uri = temp.toURI().toURL().toString().replaceAll("%20", "_");
			info.setUri(uri);
		} catch (final MalformedURLException e) {
			info.setUri(temp.getAbsolutePath());
		}
		info.setEnd(end);
		info.addToIndexes();
	}

	/**
	 * This method add an article annotation over the JCas based on the information retrieve from the Page
	 * instance.
	 * 
	 * @param cas
	 *            the JCas on which the annotation is indexed
	 * @param thePage
	 *            the MWPage instance on which is based the Article
	 * @param start
	 *            the beginning offset of the annotation
	 * @param end
	 *            the ending offset of the annotation
	 */
	private void addArticleAnnotation(JCas cas, MWArticle thePage, int start, int end) {
		final Article myArticle = new Article(cas);
		myArticle.setId(thePage.id);
		if (parser.hasSiteInfo()) {
			myArticle.setNamespace(thePage.namespace);
			myArticle.setPrefix(parser.getSiteInfo().namespaces.getPrefix(thePage.namespace));
			myArticle.setTitle(thePage.title);
		}
		myArticle.setBegin(start);
		myArticle.setEnd(end);
		myArticle.addToIndexes();
	}

	/**
	 * This method add a revision annotation over the JCas based on the information retrieved from the
	 * org.mediawiki.importer.Revision instance passed in parameter.
	 * 
	 * @param cas
	 *            the JCas on which the annotation is indexed
	 * @param rev
	 *            the MWRevision instance on which is based the Revision annotation
	 * @param start
	 *            the beginning offset of the annotation
	 * @param end
	 *            the ending offset of the annotation
	 */
	private void addRevisionAnnotation(JCas cas, MWRevision rev, Integer start, Integer end) {
		final Revision myRevision = new Revision(cas);
		myRevision.setComment(rev.comment);
		myRevision.setId(rev.id);
		myRevision.setIsMinor(rev.minor);
		myRevision.setUser(rev.contributor);
		myRevision.setBegin(start);
		myRevision.setEnd(end);
		myRevision.addToIndexes();
	}
}
