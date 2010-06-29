package org.apache.uima.wikipedia.types;

import java.util.Collections;
import java.util.List;

/**
 * This class represents an article. It is designed to be immutable, thus allowing fluent access to it's data
 * fields without fearing user's modifications.
 * <p>
 * It holds the title of the article (also know as the page's title), the namespace index associated with that
 * page (for more info on this, see {@link uima.wikipedia.types.MWSiteinfo}), the article's id and the list of
 * it's revisions. The revisions contain the actual text.
 * <p>
 * Articles are crafted by the article factory . Some fields might be initialized with default values (see
 * {@link org.apache.uima.wikipedia.cr.factory.MWArticleFactory} for more details).
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
