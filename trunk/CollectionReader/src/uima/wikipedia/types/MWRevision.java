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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[Id = ");
		builder.append(id);
		builder.append(" | Timestamp = ");
		builder.append(timestamp.getTime().toString());
		builder.append(" | Contributor = ");
		builder.append(contributor);
		builder.append(" | Minor = ");
		builder.append(minor);
		builder.append("]");
		builder.append('\n');
		builder.append("Comment : ");
		builder.append(comment);
		builder.append('\n');
		builder.append('\n');
		builder.append(text);
		builder.append('\n');
		return builder.toString();
	}

	@Override
	public int compareTo(MWRevision o) {
		return timestamp.compareTo(o.timestamp);
	}
}
