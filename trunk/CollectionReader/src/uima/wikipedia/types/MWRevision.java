package uima.wikipedia.types;

import java.util.Calendar;

/**
 * This class represents an article's revision. It is designed to be immutable, thus allowing fluent access to
 * it's data fields without fearing user's modifications.
 * <p>
 * Revisions are crafted by the revision factory. Some of the tags we consider in our analysis are optional,
 * the corresponding field will be initialized with a default value if the tag is not present. We also only
 * consider the contributor's username. For more details on this see
 * {@link uima.wikipedia.factory.MWRevisionFactory}.
 * <p>
 * Revisions can be ordered according to their timestamp. Newer revisions are considered greater than older
 * ones.
 * 
 * @author Maxime Bury <Maxime.bury@gmail.com>
 * @see MWArticle
 */
public class MWRevision implements Comparable<MWRevision> {
	public final int		id;
	public final Calendar	timestamp;
	public final String		contributor;
	public final boolean	minor;
	public final String		comment;
	public final String		text;

	/**
	 * Constructs a new MWRevision object. Once initialized, the fields can't be modified anymore, thus making
	 * this object immutable.
	 * 
	 * @param id
	 *            The revision's id.
	 * @param timestamp
	 *            The revision's timestamp
	 * @param contributor
	 *            The name of the revision's contributor
	 * @param minor
	 *            A flag indicating if revision is minor or not
	 * @param comment
	 *            The optional comment of the contributor on this revision
	 * @param text
	 *            The text of the revision
	 */
	public MWRevision(int id, Calendar timestamp, String contributor, boolean minor, String comment, String text) {
		this.id = id;
		this.timestamp = timestamp;
		this.contributor = contributor;
		this.minor = minor;
		this.comment = comment;
		this.text = text;
	}

	/**
	 * Compares a MWRevision object to another based on their timestamp, newer is greater.
	 */
	@Override
	public int compareTo(MWRevision o) {
		return timestamp.compareTo(o.timestamp);
	}
}
