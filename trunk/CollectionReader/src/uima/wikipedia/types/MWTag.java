package uima.wikipedia.types;

public enum MWTag {
	// ROOT
	MEDIAWIKI,
	// SITE INFO
	SITEINFO, SITENAME, BASE, GENERATOR, CASE, NAMESPACES, NAMESPACE,
	// PAGES
	PAGE, TITLE, ID,
	// REVISIONS
	REVISION, TIMESTAMP, CONTRIBUTOR, USERNAME, MINOR, COMMENT, TEXT,
	// INVALID
	INVALID_TAG;

	public static MWTag toTag(String tagname) {
		try {
			return valueOf(tagname.toUpperCase());
		} catch (Exception e) {
			return INVALID_TAG;
		}
	}
}
