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

import java.util.ArrayList;

import org.apache.uima.mediawiki.cr.types.MWArticle;
import org.apache.uima.mediawiki.cr.types.MWRevision;
import org.apache.uima.mediawiki.cr.types.MWSiteinfo;

/**
 * This class is a factory for the articles. It is used for two main purposes :
 * <p>
 * The first goal was to extract the processing of the string the parser gives us out of the parser itself,
 * thus making the parsers code much more readable. It also makes the parser's code more robust, since you
 * don't have to dig into it when you find the need to modify how you handle the titles for example.
 * <p>
 * The second goal was to allow the building of articles as immutable objects. This way, we can make the
 * MWArticle object's fields public, thus avoiding the needs of verbose getters to access the data, while
 * making sure the state of the object remains untouched.
 * <p>
 * Only a subset of the data available in the XML files we process is considered. For more information on this
 * you should read the wiki.
 * 
 * @see MWRevisionFactory
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class MWArticleFactory {
	// Article variables
	private static String					m_title;
	private static int						m_namespace;
	private static int						m_id;
	private static ArrayList<MWRevision>	m_revisions;

	// Website info
	private static MWSiteinfo				m_siteinfo;

	/**
	 * Default initialisation of the fields. The title is set to the empty string, the namespace is set to 0
	 * (default namespace), the id is set to -1 and the revision list is initialized (empty).
	 */
	public static void init(MWSiteinfo siteinfo) {
		// Default initialisation
		m_title = "";
		m_namespace = 0;
		m_id = -1;
		m_revisions = new ArrayList<MWRevision>();
		// Website info
		m_siteinfo = siteinfo;
	}

	/**
	 * Get the current article.
	 * 
	 * @return a new instance of MWArticle as crafted by the factory.
	 */
	public static MWArticle newInstance() {
		return new MWArticle(m_title, m_namespace, m_id, m_revisions);
	}

	/**
	 * Sets the title and namespace of the article the factory is currently crafting. The namespace is stored
	 * as an integer which is the key to the corresponding name in the MWSiteInfo object. In order to get an
	 * accurate namespace index, we rely on the fact that the parser managed to gather the website info
	 * correctly. If that's not the case, we just assume the article is in the default (0) namespace.
	 * 
	 * @param prefixedTitle
	 *            the article's title
	 */
	public static void hasTitle(String prefixedTitle) {
		// We look for a colon
		final int pos = prefixedTitle.indexOf(':');
		// If we find one, we process the title and namespace
		if (pos > 0) {
			final String prefix = prefixedTitle.substring(0, pos);
			if (m_siteinfo.namespaces.hasPrefix(prefix)) {
				m_namespace = m_siteinfo.namespaces.getIndex(prefix);
				m_title = prefixedTitle.substring(pos + 1);
				return;
			}
		}
		// If not, the article is in the default namespace
		m_namespace = 0;
		m_title = prefixedTitle;
	}

	/**
	 * Sets the id of the article the factory is currently crafting. It gets a String from the parser and
	 * tries to turn it into an integer. If it fails, the default value is -1.
	 * 
	 * @param id
	 *            the revision's id.
	 */
	public static void hasId(String id) {
		try {
			m_id = Integer.parseInt(id);
		} catch (final NumberFormatException e) {
			m_id = -1;
		}
	}

	/**
	 * Adds a revision to the revision list of the article currently crafted by the factory.
	 * 
	 * @param revision
	 *            the revision to add.
	 */
	public static void hasRevision(MWRevision revision) {
		m_revisions.add(revision);
	}

	/**
	 * Removes all the revisions but the latest from the current article's revision list.
	 */
	public static void latestOnly() {
		MWRevision latest = m_revisions.get(0);
		for (final MWRevision rev : m_revisions)
			if (rev.compareTo(latest) > 0)
				latest = rev;
		m_revisions.clear();
		m_revisions.add(latest);
	}

	/**
	 * Checks if the article is empty or not. An article is considered empty if it holds no revisions.
	 * 
	 * @return <code>true</code> if the revision list is empty <code>false</code> otherwise
	 */
	public static boolean isEmpty() {
		return m_revisions.isEmpty();
	}

	/**
	 * Reinitialize the factory's fields to start a crafting new clean article.
	 */
	public static void clear() {
		m_title = "";
		m_namespace = 0;
		m_id = -1;
		m_revisions.clear();
	}
}
