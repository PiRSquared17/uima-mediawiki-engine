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

package org.apache.uima.mediawiki.cr.types;

import java.util.Collections;
import java.util.List;

/**
 * This class represents an article. It is designed to be immutable, thus allowing fluent access to it's data
 * fields without fearing user's modifications.
 * <p>
 * It holds the title of the article (also know as the page's title), the namespace index associated with that
 * page (for more info on this, see {@link org.apache.uima.mediawiki.cr.types.MWSiteinfo}), the article's id
 * and the list of it's revisions. The revisions contain the actual text.
 * <p>
 * Articles are crafted by the article factory . Some fields might be initialized with default values (see
 * {@link org.apache.uima.mediawiki.cr.factory.MWArticleFactory} for more details).
 * 
 * @see MWRevision
 * @see MWSiteinfo
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public final class MWArticle {
	public final String				title;
	public final int				namespace;
	public final int				id;
	public final List<MWRevision>	revisions;

	/**
	 * Constructs a new MWArticle object. Once initialized, the fields can't be modified anymore, thus making
	 * this object immutable.
	 * 
	 * @param title
	 *            The article's title
	 * @param namespace
	 *            The article's namespace
	 * @param id
	 *            The article's id
	 * @param revisions
	 *            The list of the article's revision. This list is made immutable as well.
	 */
	public MWArticle(String title, int namespace, int id, List<MWRevision> revisions) {
		this.namespace = namespace;
		this.title = title;
		this.id = id;
		this.revisions = Collections.unmodifiableList(revisions);
	}
}
