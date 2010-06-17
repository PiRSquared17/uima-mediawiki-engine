package uima.wikipedia.types;

public class MWSiteInfo {
	public final String			sitename;
	public final String			base;
	public final String			generator;
	public final String			mcase;
	public final MWNamespaceSet	namespaces;

	public MWSiteInfo(String sitename, String base, String generator, String scase, MWNamespaceSet namespaces) {
		this.sitename = sitename;
		this.base = base;
		this.generator = generator;
		mcase = scase;
		this.namespaces = namespaces;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append(sitename).append('\n').append(base).append('\n').append(generator).append('\n');
		return result.append(mcase).append('\n').append(namespaces.toString()).toString();
	}
}
