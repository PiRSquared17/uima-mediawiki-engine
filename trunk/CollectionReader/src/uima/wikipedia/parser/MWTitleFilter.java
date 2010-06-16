package uima.wikipedia.parser;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class MWTitleFilter implements StreamFilter {
	private boolean	foundTitle	= false;

	@Override
	public final boolean accept(XMLStreamReader reader) {
		if (!foundTitle) {
			if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("title"))
				// Si on rencontre un tag <title> ouvrant
				foundTitle = true;
		} else if (reader.getEventType() == CHARACTERS && !titleMatch(reader.getText())) {
			// On a trouvé un titre a l'itération précédente
			// On verifie s'il correspond au filtre
			// Si le titre ne correspond pas, on saute au prochain
			boolean pageSkipped = false;
			while (!pageSkipped)
				try {
					reader.next();
					if (reader.getEventType() == START_ELEMENT && reader.getLocalName().equals("page"))
						// When we reach the next title opening tag, we stop
						// iterating
						pageSkipped = true;
				} catch (XMLStreamException e) {
					// We do nothing, this should not happen.
					// If it does, the next call to next() in the main
					// parser will raise it again anyway.
				}
			foundTitle = false;
		} else
			foundTitle = false;
		return true;
	}

	protected abstract boolean titleMatch(String title);
}