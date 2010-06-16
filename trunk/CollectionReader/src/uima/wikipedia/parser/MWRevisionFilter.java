package uima.wikipedia.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.List;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class MWRevisionFilter implements StreamFilter {
	private List<Integer>	matchList;
	private boolean			foundRevision	= false;
	private boolean			foundId			= false;

	public MWRevisionFilter(List<Integer> myList) {
		matchList = myList;
	}

	@Override
	public final boolean accept(XMLStreamReader reader) {
		if (!foundRevision) {
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("revision"))
				// If we encounter an opening <revision> tag
				foundRevision = true;
		} else if (foundRevision && !foundId) {
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("id"))
				// If we encounter and <id> tag nested in a revision tag
				foundId = true;
		} else if (reader.getEventType() == CHARACTERS && !revisionMatch(reader.getText())) {
			// If it doesn't match, we iterate to the next <revision> tag
			boolean revisionSkipped = false;
			while (!revisionSkipped)
				try {
					reader.next();
					if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("revision"))
						revisionSkipped = true;
				} catch (XMLStreamException e) {
					// We do nothing, this should not happen.
					// If it does, the next call to next() in the main
					// parser will raise it again anyway.
				}
			foundRevision = false;
			foundId = false;
		} else {
			foundRevision = false;
			foundId = false;
		}
		return true;
	}

	protected boolean revisionMatch(String id) {
		int intId = Integer.parseInt(id);
		return matchList.contains(intId);
	}
}
