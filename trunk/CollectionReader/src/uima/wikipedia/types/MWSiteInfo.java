package uima.wikipedia.types;

public class MWSiteInfo {
	public final String			sitename;
	public final String			base;
	public final String			generator;
	public final String			titlecase;
	public final MWNamespaceSet	namespaces;

	public MWSiteInfo(String sitename, String base, String generator, String titlecase, MWNamespaceSet namespaces) {
		this.sitename = sitename;
		this.base = base;
		this.generator = generator;
		this.titlecase = titlecase;
		this.namespaces = namespaces;
	}
}
