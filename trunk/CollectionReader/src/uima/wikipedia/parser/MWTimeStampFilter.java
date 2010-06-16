package uima.wikipedia.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class MWTimeStampFilter implements StreamFilter {

	private boolean	foundTimestamp	= false;

	@Override
	public final boolean accept(XMLStreamReader reader) {
		if (!foundTimestamp) {
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("timestamp"))
				// If we encounter an opening <timestamp> tag
				foundTimestamp = true;
			// We found a timestamp tag at the previous iteration
			// We check if it matches the filter
		} else if (reader.getEventType() == CHARACTERS && !timeStampMatch(reader.getText())) {
			// If it doesn't, we iterate to the next <revision> tag
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
			foundTimestamp = false;
		} else
			foundTimestamp = false;
		return true;
	}

	protected abstract boolean timeStampMatch(String timestamp);
}
