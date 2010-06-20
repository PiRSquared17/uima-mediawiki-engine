package uima.wikipedia.types;

import java.util.Calendar;

public class MWRevision implements Comparable<MWRevision> {
	public final int		id;
	public final Calendar	timestamp;
	public final String		contributor;
	public final boolean	minor;
	public final String		comment;
	public final String		text;

	public MWRevision(int id, Calendar timestamp, String contributor, boolean minor, String comment, String text) {
		this.id = id;
		this.timestamp = timestamp;
		this.contributor = contributor;
		this.minor = minor;
		this.comment = comment;
		this.text = text;
	}

	@Override
	public int compareTo(MWRevision o) {
		return timestamp.compareTo(o.timestamp);
	}
}
