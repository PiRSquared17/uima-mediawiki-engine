package uima.wikipedia.types;

import java.util.Collections;
import java.util.List;

/**
 * This class represents an article. It is designed to be immutable, thus allowing fluent access to it's data
 * fields without fearing user's modifications.
 * <p>
 * It holds the title of the article (also know as the page's title), the namespace index associated with that
 * page (for more info on this, see {@link types.MWSiteInfo}), the article's id and the list of it's
 * revisions. The revisions contain the actual text.
 * 
 * @author Maxime Bury <Maxime.bury@gmail.com>
 * @see MWRevision
 * @see MWSiteInfo
 */
public final class MWArticle {
	public final String				title;
	public final int				namespace;
	public final int				id;
	public final List<MWRevision>	revisions;

	public MWArticle(String title, int namespace, int id, List<MWRevision> revisions) {
		this.namespace = namespace;
		this.title = title;
		this.id = id;
		this.revisions = Collections.unmodifiableList(revisions);
	}
}
