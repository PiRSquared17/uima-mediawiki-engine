package uima.wikipedia.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.List;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This abstract class provides the basic revision filtering mechanism.
 * <p>
 * The idea here is to work directly on the XML stream. Since there is only one id per revision, if the id is
 * invalid we can skip the whole revision without even bothering to process it. We do that by performing a
 * check on every &lt;id&gt; tag encountered. If the check successes, the XML stream is not altered and the
 * page is processed normally. If the check fails however, we make the XMLStreamReader iterate to the next
 * &lt;revision&gt; opening tag. By doing that, we completely hide the rest of the discarded revision to the
 * parser using the XMLStreamReader we are filtering.
 * <p>
 * The exact nature of the id check performed is left to be defined in the inheriting classes.
 * 
 * @see javax.xml.stream.StreamFilter
 * @see javax.xml.stream.XMLStreamReader
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
// TODO : Allow also the exclusion of certain ids
public class MWRevisionFilter implements StreamFilter {
	private final List<Integer>	matchList;
	private boolean				foundRevision	= false;
	private boolean				foundId			= false;

	public MWRevisionFilter(List<Integer> myList) {
		matchList = myList;
	}

	/**
	 * This method is the core of the filtering process. It's job is to tell wether a particular event passes
	 * or not. The next() method of the XMLStreamReader calls this method on each element. Only the accepted
	 * ones get through. In fact, this method never returns <code>false</code>, instead it skips all the
	 * events that would have deserved a <code>false</code> return.
	 * <p>
	 * If the id check passes, the stream isn't altered. If it doesn't, we skip to the next revision. From the
	 * point of view of the parser using the filtered stream, we haven't reached the end of a revision, so it
	 * keeps processing.
	 * <p>
	 * As the pages also got an &lt;id&gt; tag, we need to check if we are nested in a &lt;revision&gt; tag as
	 * well. &lt;id&gt; are not mandatory (but are usually present), if a revision doesn't have it then it
	 * will pass regardless.
	 */
	@Override
	public final boolean accept(XMLStreamReader reader) {
		if (!foundRevision) {
			// If we encounter an opening <timestamp> tag, we set the revision flag to true.
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("revision")) {
				foundRevision = true;
			}
		} else if (foundRevision && !foundId) {
			// If we encounter and <id> tag nested in a <revision> tag we set the id flag to true.
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("id")) {
				foundId = true;
			}
		} else if (reader.getEventType() == CHARACTERS && !revisionMatch(reader.getText())) {
			// If both flags are set true, we found a revision <id> tag on the previous call.
			// <id> tags are NOT mandatory, but we found one. This event should be a CHARACTERS one (we
			// check nevertheless)
			// If the id doesn't pass the check, we iterate to the next <revision> opening tag.
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
			// We skipped the revision, we set both flags back to false.
			foundRevision = false;
			foundId = false;
		} else {
			// Either the id check succeeded or the revision didn't have an id. Either way it passes.
			foundRevision = false;
			foundId = false;
		}
		// We always return true, since the events we want to filter are simply skipped by the above code.
		return true;
	}

	/**
	 * Checks wether the id is valid or not.
	 * 
	 * @param id
	 *            the string representation of the id to check
	 * @return <code>true</code> if it passes, <code>false</code> otherwise.
	 */
	// TODO : If in practice large list are used, it might be worth it to use a sorted array (dichotomy)
	protected boolean revisionMatch(String id) {
		try {
			final int intId = Integer.parseInt(id);
			return matchList.contains(intId);
		} catch (final NumberFormatException e) {
			// If we fail to convert the string to a valid integer, then it can't be in the list.
			return true;
		}
	}
}
