/*
 *  Copyright [2010] [Fabien Poulard &lt;fabien.poulard@univ-nantes.fr&gt;, Maxime Bury, Maxime Rihouey] 
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
package org.apache.uima.wikipedia.ae.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.wikipedia.ae.parser.MWLanguage;
import org.apache.uima.wikipedia.ae.parser.MWRevisionBuilder;
import org.apache.uima.wikipedia.types.Article;
import org.apache.uima.wikipedia.types.Revision;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

/**
 * This class orchestrates all the parsing process without actually parsing the text, it takes care of the
 * annotations and the sofa.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 * @author Maxime Rihouey <maxime.rihouey@univ-nantes.fr>
 */
public class MWCasBuilder {
	/** The main CAS we will be working on */
	private static JCas					main;
	/** The view of the cas containing the raw wiki text */
	private static JCas					rawView;
	/** The name of the view containing the raw wiki text */
	private static String				rawViewName;
	/** The mediawiki parser */
	private static MarkupParser			parser;
	/** The language we are parsing */
	private static MWLanguage			language;
	/** The revision builder */
	private static MWRevisionBuilder	revision;
	/** A string builder for the CAS content */
	private static StringBuilder		content;

	/**
	 * Initializes the factory.
	 * 
	 * @param rawViewName
	 *            the name of the view where the raw text is located
	 * @throws CASException
	 *             If something goes wrong
	 */
	public static void initialize(String rawViewName, boolean enableMacros, File def) throws CASException {
		// Initialize some parameters
		MWCasBuilder.rawViewName = rawViewName;
		// Initialize the parser
		language = new MWLanguage();
		language.setEnableMacros(enableMacros);
		if (enableMacros)
			configureMacros(def);
		parser = new MarkupParser(language);
		revision = new MWRevisionBuilder();
	}

	/**
	 * Process the CAS content.
	 * <p>
	 * It gathers the revision annotations from the raw view, parses the text, gather the content annotation
	 * and adds the result to the CAS.
	 * 
	 * @param cas
	 *            the CAS to process
	 * @throws CASException
	 *             if something goes wrong
	 */
	public static void build(JCas cas) throws CASException {
		// Initialize the CAS
		main = cas;
		rawView = main.getView(rawViewName);
		// Initialize the content builder
		content = new StringBuilder();
		// An iterator over the revision annotations.
		final FSIterator<Annotation> revisionIterator = rawView.getAnnotationIndex(Revision.type).iterator();

		while (revisionIterator.hasNext()) {
			final Revision rawRevision = (Revision) revisionIterator.next();
			// Start of the annotation
			final int start = content.length();
			// Instanciate a new builder
			revision.reset(main);
			parser.setBuilder(revision);
			// Parse the revision
			parser.parse(rawRevision.getCoveredText());
			// Add the revision's text to the CAS content
			content.append(revision.getText());
			final int end = content.length();
			// Add a new revision annotation
			addRevisionAnnotation(rawRevision, start, end);
			// Add the content relative annotations
			for (final Annotation a : revision.getAnnotations())
				a.addToIndexes();
		}
		// Finalize the CAS processing.
		finalizeCAS();
	}

	/**
	 * Finalize the CAS processing. In particular it adds the Article annotation.
	 */
	private static void finalizeCAS() {
		// Add the document text.
		main.setDocumentText(content.toString());
		// Add the article annotation.
		final Article parsedArticle = new Article(main);
		final FSIterator<Annotation> articleIterator = rawView.getAnnotationIndex(Article.type).iterator();

		// We iterate over the Article annotations, there should be only one.
		while (articleIterator.hasNext()) {
			// Get the annotation
			final Article rawArticle = (Article) articleIterator.next();
			// Collect information
			parsedArticle.setId(rawArticle.getId());
			parsedArticle.setNamespace(rawArticle.getNamespace());
			parsedArticle.setPrefix(rawArticle.getPrefix());
			parsedArticle.setTitle(rawArticle.getTitle());
			parsedArticle.setBegin(0);
			parsedArticle.setEnd(content.length());
			// Add it to the index
			parsedArticle.addToIndexes();
		}
	}

	private static void configureMacros(File def) {
		try {
			if (def != null && def.exists() && def.isFile()) {
				final BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(def), "utf-8"));
				String line = input.readLine().trim();

				// Read all the macros from the file, one per line
				while (line != null) {
					if (line.contains("->")) {
						final String[] macro = line.split("->");
						if (macro.length >= 2)
							language.addMacro(macro[0].trim(), macro[1].trim());
						else
							language.addMacro(macro[0].trim(), "");
					}
					line = input.readLine();
				}
				input.close();
			}
		} catch (final Exception e) {
			// This should not happen
		}
	}

	private static void addRevisionAnnotation(Revision rawRevision, int start, int end) {
		// Create a new revision
		final Revision parsedRevision = new Revision(main);
		// Collect information
		parsedRevision.setBegin(start);
		parsedRevision.setEnd(end);
		parsedRevision.setId(rawRevision.getId());
		parsedRevision.setUser(rawRevision.getUser());
		parsedRevision.setComment(rawRevision.getComment());
		parsedRevision.setIsMinor(rawRevision.getIsMinor());
		// Add it to the index.
		parsedRevision.addToIndexes();
	}
}
