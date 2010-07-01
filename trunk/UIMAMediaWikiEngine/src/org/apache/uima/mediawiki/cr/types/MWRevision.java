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
package org.apache.uima.mediawiki.cr.types;

import java.util.Calendar;

/**
 * This class represents an article's revision. It is designed to be immutable, thus allowing fluent access to
 * it's data fields without fearing user's modifications.
 * <p>
 * Revisions are crafted by the revision factory. Some of the tags we consider in our analysis are optional,
 * the corresponding field will be initialized with a default value if the tag is not present. We also only
 * consider the contributor's username. For more details on this see
 * {@link org.apache.uima.mediawiki.cr.factory.MWRevisionFactory}.
 * <p>
 * Revisions can be ordered according to their timestamp. Newer revisions are considered greater than older
 * ones.
 * 
 * @see MWArticle
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
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
