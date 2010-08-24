/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.uima.mediawiki.utils;

// Java dependencies
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
// UIMA dependencies
import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
// TS dependencies
import org.apache.uima.mediawiki.types.Article;
import org.apache.uima.mediawiki.types.Revision;

/**
 * This class aims at exporting parsed wikimedia articles as raw text files.
 * It creates one directory per page and one text file per revision.
 * 
 * @author Fabien Poulard <fabien.poulard@univ-nantes.fr>
 */
public class MWRawTextExporter extends JCasAnnotator_ImplBase {

	/** Component related constants */
	public static String COMPONENT_NAME    = "MediaWiki Raw Text Exporter";
	public static String COMPONENT_VERSION = "1.0";
	public static String COMPONENT_ID      = COMPONENT_NAME+"-"+COMPONENT_VERSION;
	
	/** Parameters names constants */
	public static final String PARAM_OUTPUT  = "OutputDirectoryText";
	public static final String PARAM_WOUTPUT = "OutputDirectoryWiki";
	public static final String PARAM_MINREV  = "MinNumberOfRevisions";
	public static final String WIKI_VIEWNAME = "RawWikiText";
	
	/** Configuration values */
	private Integer theMinRevThreshold;
	private File theTextOutputDir;
	private File theWikiOutputDir;
	
	/**
	 * Prepare the component for its execution.
	 * In this particular case, it retrieves the parameter regarding
	 * where the files should be exported.
	 */
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		// Retrieve the output directory
		theTextOutputDir = new File(
				(String) aContext.getConfigParameterValue(PARAM_OUTPUT) );
		if (! theTextOutputDir.isDirectory())
			throw new ResourceInitializationException(
					"The output parameter '"+theTextOutputDir+"' is not a directory", 
					null);
		// ... as well as the one for wiki format
		theWikiOutputDir = new File(
				(String) aContext.getConfigParameterValue(PARAM_WOUTPUT) );
		if (! theWikiOutputDir.isDirectory())
			throw new ResourceInitializationException(
					"The output parameter '"+theWikiOutputDir+"' is not a directory", 
					null);
		// Retrieve the minimum revision threshold
		theMinRevThreshold = 
			(Integer) aContext.getConfigParameterValue(PARAM_MINREV);
		// Log the fact the component is initialized
		UIMAFramework.getLogger().log(Level.INFO, 
				"Component "+COMPONENT_ID+" successfully initialized : " +
				"exporting to "+theTextOutputDir+" pages with more than "+
				theMinRevThreshold+" revisions.");
	}

	/**
	 * Export the content of this CAS as a directory of raw text revisions.
	 * In order to do so, browse the article annotations, create a directory
	 * 'page-<pageid>' for each and export in those each revision as a raw 
	 * text file 'p<pageid>-r<revid>.txt'. 
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// The page should be in the default view
		// Look for the article annotations
		FSIterator<Annotation> itArticle = 
			aJCas.getAnnotationIndex(Article.type).iterator();
		while(itArticle.hasNext()) {
			// Get the article id
			Article art = (Article) itArticle.next();
			Double pId = art.getId();
			// Retrieve the iterator over the revisions
			AnnotationIndex<Annotation> idxRevisions = 
				aJCas.getAnnotationIndex(Revision.type);
			FSIterator<Annotation> itRevisions = idxRevisions.subiterator(art);
			// Check there are enough revision
			if ( countRevisions(itRevisions) >= theMinRevThreshold ) {
				String msg = String.format(
						"Exporting page %1d (%2d revisions).", 
						pId.intValue(), countRevisions(itRevisions));
				UIMAFramework.getLogger().log(Level.INFO, msg);
				// Export each revision
				itRevisions.moveToFirst();
				while( itRevisions.hasNext() )
					exportRevision(theTextOutputDir, pId.intValue(), 
							(Revision) itRevisions.next());
				// Also export the wiki format
				try {
					exportRawWikiPage(pId, aJCas.getView(WIKI_VIEWNAME));
				} catch (CASException e) {
					String msg2 = String.format(
							"Could not export the wiki format of page %1d.", 
							pId.intValue());
					UIMAFramework.getLogger().log(Level.WARNING, msg2);
				}
			} else {
				String msg = String.format(
						"Ignoring page %1d (%2d revisions).", 
						pId.intValue(), countRevisions(itRevisions));
				UIMAFramework.getLogger().log(Level.INFO, msg);
			}
		}
	}

	// UTILITY METHODS --------------------------------------------------------
	
	/**
	 * This method export the text content covered by the revision as
	 * a text file.
	 * 
	 * @param rootdir directory where the page directory will be created
	 * @param pId identifier of the article the revision belongs to
	 * @param rev the revision annotation to consider
	 * @throws IOException 
	 */
	private void exportRevision(File rootdir, Integer pId, Revision rev) {
		// Create the page directory if necessary
		File outdir = createPageDirectory(rootdir, pId);
		// Compute the file name
		String fname = String.format("p%1$d-r%2$d.txt", pId, rev.getId());
		File fpath   = new File(outdir , fname);
		// Export the content
		String content = rev.getCoveredText();
		if ( content.length() > 0 ) {
			try {
				FileWriter fstream = new FileWriter(fpath);
				fstream.write(rev.getCoveredText());
				fstream.close();
			} catch (IOException e) {
				UIMAFramework.getLogger().log(Level.SEVERE,
						"Could not export revision "+rev.getId()
						+" in file "+fpath);
			}
		} else {
			UIMAFramework.getLogger().log(Level.SEVERE,
					"Revision "+rev.getId()+" is empty. No exportation.");
		}
	}

	/**
	 * This method counts the number of revisions there are in the iterator
	 * passed in parameter.
	 * 
	 * @param itRevisions iterator over the revision annotations to count
	 * @return the number of revisions
	 */
	private Integer countRevisions(FSIterator<Annotation> itRevisions) {
		Integer tot = 0;
		itRevisions.moveToFirst();
		while(itRevisions.hasNext()) {
			itRevisions.next();
			tot += 1;
		}
		return tot;
	}

	/**
	 * Create the directory where the revision of the article, which id is in
	 * parameter, will be exported.
	 * 
	 * @param outdir directory in which the page directory will be created
	 * @param id the identifier of the page that will be exported
	 * @return the file corresponding of this directory
	 */
	private File createPageDirectory(File outdir, Integer id) {
		// Compute the name of the file
		File pagedir = 
			new File(outdir, String.format("page-%1$d", id));
		// Create if it does not already exist
		if ( ! pagedir.isDirectory() )
			pagedir.mkdir();
		return pagedir;
	}
	
	/**
	 * This method exports the revisions of the page which id is passed 
	 * in parameter.
	 * 
	 * @param pId identifier of the page
	 * @param view the view containing the wiki format of the page
	 */
	private void exportRawWikiPage(Double pId, JCas view) {
		// Look for the right page
		FSIterator<Annotation> itArticle = 
			view.getAnnotationIndex(Article.type).iterator();
		while(itArticle.hasNext()) {
			// Is this the right article ?
			Article art = (Article) itArticle.next();
			if ( art.getId() == pId ) {
				// Export the revisions
				AnnotationIndex<Annotation> idxRevisions = 
					view.getAnnotationIndex(Revision.type);
				FSIterator<Annotation> itRevisions = idxRevisions.subiterator(art);
				while(itRevisions.hasNext()) {
					Revision rev = (Revision) itRevisions.next();
					exportRevision(theWikiOutputDir, pId.intValue(), rev);
				}
			}
		}	
	}
}
