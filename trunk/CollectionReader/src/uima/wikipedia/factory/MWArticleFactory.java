package uima.wikipedia.factory;

import java.util.ArrayList;

import uima.wikipedia.types.MWArticle;
import uima.wikipedia.types.MWRevision;
import uima.wikipedia.types.MWSiteInfo;

public class MWArticleFactory {
	// Article variables
	private String					a_title;
	private int						a_namespace;
	private int						a_id;
	private ArrayList<MWRevision>	a_revisions;

	// Website info
	private final MWSiteInfo		a_siteinfo;

	/**
	 * Default initialisation of the fields. The title is set to the empty string, the namespace is set to 0 (default
	 * namespace), the id is set to -1 and the revision list is initialized (empty).
	 */
	public MWArticleFactory(MWSiteInfo siteinfo) {
		// Default initialisation
		a_title = "";
		a_namespace = 0;
		a_id = -1;
		a_revisions = new ArrayList<MWRevision>();
		// Website info
		a_siteinfo = siteinfo;
	}

	/**
	 * Get the current article.
	 * 
	 * @return a new instance of MWArticle as crafted by the factory.
	 */
	public final MWArticle newInstance() {
		return new MWArticle(a_title, a_namespace, a_id, a_revisions);
	}

	/**
	 * Sets the title and namespace of the article the factory is currently crafting. The namespace is stored as an
	 * integer which is the key to the corresponding name in the MWSiteInfo object.
	 * 
	 * @param prefixedTitle
	 *            the article's title
	 */
	public final void hasTitle(String prefixedTitle) {
		// We look for a colon
		final int pos = prefixedTitle.indexOf(':');
		// If we find one, we process the title and namespace
		if (pos > 0) {
			final String prefix = prefixedTitle.substring(0, pos);
			if (a_siteinfo.namespaces.hasPrefix(prefix)) {
				a_namespace = a_siteinfo.namespaces.getIndex(prefix);
				a_title = prefixedTitle.substring(pos + 1);
				return;
			}
		}
		// If not, the article is in the default namespace
		a_namespace = 0;
		a_title = prefixedTitle;
	}

	/**
	 * Sets the id of the article the factory is currently crafting. It gets a String from the parser and tries to turn
	 * it into an integer. If it fails, the default value is -1.
	 * 
	 * @param id
	 *            the revision's id.
	 */
	public final void hasId(String id) {
		try {
			a_id = Integer.parseInt(id);
		} catch (final NumberFormatException e) {
			a_id = -1;
		}
	}

	/**
	 * Adds a revision to the revision list of the article currently crafted by the factory.
	 * 
	 * @param revision
	 *            the revision to add.
	 */
	public final void hasRevision(MWRevision revision) {
		a_revisions.add(revision);
	}

	/**
	 * Removes all the revisions but the latest from the current article's revision list.
	 */
	public final void latestOnly() {
		MWRevision latest = a_revisions.get(0);
		for (final MWRevision rev : a_revisions)
			if (rev.compareTo(latest) > 0) {
				latest = rev;
			}
		a_revisions.clear();
		a_revisions.add(latest);
	}

	/**
	 * Checks if the article is empty or not. An article is considered empty if it holds no revisions.
	 * 
	 * @return <code>true</code> if the revision list is empty <code>false</code> otherwise
	 */
	public final boolean isEmpty() {
		return a_revisions.isEmpty();
	}

	/**
	 * Reinitialize the factory's fields to start a crafting new clean article.
	 */
	public final void clear() {
		a_title = "";
		a_namespace = 0;
		a_id = -1;
		a_revisions = new ArrayList<MWRevision>();
	}
}
