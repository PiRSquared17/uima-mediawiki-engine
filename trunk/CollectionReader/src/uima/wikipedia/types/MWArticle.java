package uima.wikipedia.types;

import java.util.ArrayList;

public class MWArticle {
	public final String					title;
	public final int					namespace;
	public final int					id;
	public final ArrayList<MWRevision>	revisions;

	public MWArticle(String title, int namespace, int id, ArrayList<MWRevision> revisions) {
		this.namespace = namespace;
		this.title = title;
		this.id = id;
		this.revisions = revisions;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		int i = 0;

		builder.append("> Id = ");
		builder.append(id);
		builder.append(" | ");
		builder.append(namespace);
		builder.append(" : ");
		builder.append(title);
		builder.append('\n');
		for (final MWRevision r : revisions) {
			builder.append('\n');
			builder.append("Revision > " + i);
			builder.append('\n');
			builder.append(r.toString());
			++i;
		}
		return builder.toString();
	}
}
