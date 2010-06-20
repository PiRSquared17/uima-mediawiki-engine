package uima.wikipedia.factory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import uima.wikipedia.types.MWRevision;

public final class MWRevisionFactory {
	/** variables used to recover the revisions properties */
	private int			m_id;
	private Calendar	m_timestamp;
	private String		m_contributor;
	private boolean		m_minor;
	private String		m_comment;
	private String		m_text;

	/**
	 * Default initialisation of the fields. The id is set to -1, the timestamp is set to <code>null</code>,
	 * the minor flag is set to <code>false</code>, the contributor, comment and text are set to the empty
	 * string.
	 */
	public MWRevisionFactory() {
		m_id = -1;
		m_timestamp = null;
		m_contributor = "";
		m_minor = false;
		m_comment = "";
		m_text = "";
	}

	/**
	 * Get the current revison.
	 * 
	 * @return a new instance of MWRevision as crafted by the factory.
	 */
	public final MWRevision newInstance() {
		return new MWRevision(m_id, m_timestamp, m_contributor, m_minor, m_comment, m_text);
	}

	/**
	 * Sets the id of the revision the factory is currently crafting. It gets a String from the parser and
	 * tries to turn it into an integer. If it fails, the default value is -1.
	 * 
	 * @param id
	 *            the revision's id.
	 */
	public final void hasId(String id) {
		try {
			m_id = Integer.parseInt(id);
		} catch (final NumberFormatException e) {
			m_id = -1;
		}
	}

	/**
	 * Sets the timestamp of the revision the factory is currently crafting. It tries to convert the String
	 * parameter into a valid timestamp. If that fails, the timestamp is set to <code>null</code>
	 * 
	 * @param timestamp
	 *            the revision's timestamp
	 */
	public final void hasTimestamp(String timestamp) {
		m_timestamp = Calendar.getInstance();
		try {
			m_timestamp.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timestamp));
		} catch (final ParseException e) {
			m_timestamp = null;
		}
	}

	/**
	 * Sets the username of the revision the factory is currently crafting. It may be the empty string if the
	 * parser has failed to gather text from the <username> tag (it was empty).
	 * 
	 * @param username
	 *            the revision's username
	 */
	public final void hasContributor(String username) {
		m_contributor = username;
	}

	/**
	 * Sets the minor flag of the revision the factory is currently crafting. If the parser encounters an
	 * empty <minor> tag then it is set to <code>false</code>.
	 * 
	 * @param flag
	 *            tells if the revision is a minor one.
	 */
	public final void isMinor(boolean flag) {
		m_minor = flag;
	}

	/**
	 * Sets the comment of the revision the factory is currently crafting. It may be empty if the parser
	 * encounters an empty tag.
	 * 
	 * @param comment
	 *            the revision's comment.
	 */
	public final void hasComment(String comment) {
		m_comment = comment;
	}

	/**
	 * Sets the text of the revision the factory is currently crafting. This should not be empty as the <text>
	 * tag is mandatory.
	 * 
	 * @param text
	 */
	public final void hasText(String text) {
		m_text = text;
	}

	/**
	 * Reinitialize the factory's fields to start a crafting new clean revision.
	 */
	public final void clear() {
		m_id = -1;
		m_timestamp = null;
		m_contributor = "";
		m_minor = false;
		m_comment = "";
		m_text = "";
	}
}
