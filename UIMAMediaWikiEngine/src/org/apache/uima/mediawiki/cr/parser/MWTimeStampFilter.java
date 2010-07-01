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
package org.apache.uima.mediawiki.cr.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This abstract class provides the basic timestamp filtering mechanism.
 * <p>
 * The idea here is to work directly on the XML stream. Since there is only one timestamp per revision, if the
 * timestamp is invalid we can skip the whole revision without even bothering to process it. We do that by
 * performing a check on every &lt;timestamp&gt; tag encountered. If the check successes, the XML stream is
 * not altered and the page is processed normally. If the check fails however, we make the XMLStreamReader
 * iterate to the next &lt;revision&gt; opening tag. By doing that, we completely hide the rest of the
 * discarded revision to the parser using the XMLStreamReader we are filtering.
 * <p>
 * The exact nature of the timestamp check performed is left to be defined in the inheriting classes.
 * 
 * @see javax.xml.stream.StreamFilter
 * @see javax.xml.stream.XMLStreamReader
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public abstract class MWTimeStampFilter implements StreamFilter {
	private boolean	foundTimestamp	= false;

	/**
	 * This method is the core of the filtering process. It's job is to tell wether a particular event passes
	 * or not. The next() method of the XMLStreamReader calls this method on each element. Only the accepted
	 * ones get through. In fact, this method never returns <code>false</code>, instead it skips all the
	 * events that would have deserved a <code>false</code> return.
	 * <p>
	 * If the timestamp check passes, the stream isn't altered. If it doesn't, we skip to the next revision.
	 * From the point of view of the parser using the filtered stream, we haven't reached the end of a
	 * revision, so it keeps processing.
	 */
	@Override
	public final boolean accept(XMLStreamReader reader) {
		if (!foundTimestamp) {
			// If we encounter an opening <timestamp> tag, we set the flag to true.
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("timestamp")) {
				foundTimestamp = true;
			}
		} else if (reader.getEventType() == CHARACTERS && !timeStampMatch(reader.getText())) {
			// If the foundTimestamp flag is set to true, we found a <timestamp> tag on the previous call.
			// <timestamp> tags are mandatory, and can't be empty. This event should be a CHARACTERS one (we
			// check nevertheless)
			// If the timestamp doesn't pass the check, we iterate to the next <revision> opening tag.
			boolean revisionSkipped = false;
			while (!revisionSkipped) {
				try {
					reader.next();
					if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("revision")) {
						revisionSkipped = true;
					}
				} catch (final XMLStreamException e) {
					// We do nothing, this should not happen.
					// If it does, the next call to next() in the main parser will raise it again anyway.
				}
			}
			// We skipped the revision, we set the flag back to false.
			foundTimestamp = false;
		} else {
			// The timestamp passed the check, we set the flag to false until the next revision.
			foundTimestamp = false;
		}
		// We always return true, since the events we want to filter are simply skipped by the above code.
		return true;
	}

	/**
	 * @param timestamp
	 *            The raw timestamp string.
	 * @return <code>true</code> if the timestamp passes the check, <code>false</code> otherwise.
	 */
	protected abstract boolean timeStampMatch(String timestamp);
}
