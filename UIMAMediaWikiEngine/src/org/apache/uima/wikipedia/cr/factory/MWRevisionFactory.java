package org.apache.uima.wikipedia.cr.factory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.uima.wikipedia.cr.types.MWRevision;

/*
 *  Copyright [2010] [Fabien Poulard <fabien.poulard@univ-nantes.fr>, Maxime Bury, Maxime Rihouey] 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

/**
 * This class is a factory for the revisions. It is used for two main purposes :
 * <p>
 * The first goal was to extract the processing of the string the parser gives us out of the parser itself,
 * thus making the parsers code much more readable. It also makes the parser's code more robust, since you
 * don't have to dig into it when you find the need to modify how you handle the timestamps for example.
 * <p>
 * The second goal was to allow the building of revisions as immutable objects. This way, we can make the
 * MWRevision object's fields public, thus avoiding the needs of verbose getters to access the data, while
 * making sure the state of the object remains untouched.
 * <p>
 * Only a subset of the data available in the XML files we process is considered. For more information on this
 * you should read the wiki.
 * 
 * @see MWArticleFactory
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public final class MWRevisionFactory {
	// Revision variables
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
	 * @return A new instance of MWRevision as crafted by the factory.
	 */
	public final MWRevision newInstance() {
		return new MWRevision(m_id, m_timestamp, m_contributor, m_minor, m_comment, m_text);
	}

	/**
	 * Sets the id of the revision the factory is currently crafting. It gets a String from the parser and
	 * tries to turn it into an integer. If it fails, the default value is -1.
	 * 
	 * @param id
	 *            The revision's id.
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
	 *            The revision's timestamp
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
	 * parser has failed to gather text from the &lt;username&gt; tag (it was empty).
	 * 
	 * @param username
	 *            The revision's contributor username
	 */
	public final void hasContributor(String username) {
		m_contributor = username;
	}

	/**
	 * Sets the minor flag of the revision the factory is currently crafting. If the parser encounters an
	 * empty &lt;minor&gt; tag then it is set to <code>false</code>.
	 * 
	 * @param flag
	 *            Tells whether the revision is a minor one.
	 */
	public final void isMinor(boolean flag) {
		m_minor = flag;
	}

	/**
	 * Sets the comment of the revision the factory is currently crafting. It may be empty if the parser
	 * encounters an empty tag.
	 * 
	 * @param comment
	 *            The revision's comment.
	 */
	public final void hasComment(String comment) {
		m_comment = comment;
	}

	/**
	 * Sets the text of the revision the factory is currently crafting. This should not be empty as the
	 * &lt;text&gt; tag is mandatory. If a page gets suppressed, this might however happen.
	 * 
	 * @param text
	 *            The revision's text.
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
