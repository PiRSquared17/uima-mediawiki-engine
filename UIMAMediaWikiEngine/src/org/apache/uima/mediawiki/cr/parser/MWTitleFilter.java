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
 * This abstract class provides the basic title filtering mechanism.
 * <p>
 * The idea here is to work directly on the XML stream. Since there is only one title per page, if the title
 * is invalid we can skip the whole page without even bothering to process it. We do that by performing a
 * check on every &lt;title&gt; tag encountered. If the check successes, the XML stream is not altered and the
 * page is processed normally. If the check fails however, we make the XMLStreamReader iterate to the next
 * &lt;page&gt; opening tag. By doing that, we completely hide the rest of the discarded page to the parser
 * using the XMLStreamReader we are filtering.
 * <p>
 * The exact nature of the title check performed is left to be defined in the inheriting classes.
 * 
 * @see javax.xml.stream.StreamFilter
 * @see javax.xml.stream.XMLStreamReader
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public abstract class MWTitleFilter implements StreamFilter {
	private boolean	foundTitle	= false;

	/**
	 * This method is the core of the filtering process. It's job is to tell wether a particular event passes
	 * or not. The next() method of the XMLStreamReader calls this method on each element. Only the accepted
	 * ones get through. In fact, this method never returns <code>false</code>, instead it skips all the
	 * events that would have deserved a <code>false</code> return.
	 * <p>
	 * If the title check passes, the stream isn't altered. If it doesn't, we skip to the next page. From the
	 * point of view of the parser using the filtered stream, we haven't reached the end of a page, so it
	 * keeps processing.
	 */
	@Override
	public final boolean accept(XMLStreamReader reader) {
		if (!foundTitle) {
			// If we encounter an opening <title> tag, we set the flag to true.
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("title"))
				foundTitle = true;
		} else if (reader.getEventType() == CHARACTERS && !titleMatch(reader.getText())) {
			// If the foundTitle flag is set to true, we found a <title> tag on the previous call.
			// <title> tags are mandatory, and can't be empty. This event should be a CHARACTERS one (we check
			// nevertheless)
			// If the title doesn't pass the check, we iterate to the next <page> opening tag.
			boolean pageSkipped = false;
			while (!pageSkipped)
				try {
					reader.next();
					if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("page"))
						// When we reach the next <page> opening tag, we stop iterating
						pageSkipped = true;
				} catch (final XMLStreamException e) {
					// We encountered a malformation, we consider the page skipped
					pageSkipped = true;
				}
			// We are processing a new page, so we set the flag back to false.
			foundTitle = false;
		} else
			// The page passed the title check, we set the flag back to false until the next page.
			foundTitle = false;
		// We always return true, since the events we want to filter are simply skipped by the above code.
		return true;
	}

	/**
	 * @param title
	 *            The raw title string.
	 * @return <code>true</code> if the title passes the check, <code>false</code> otherwise.
	 */
	protected abstract boolean titleMatch(String title);
}