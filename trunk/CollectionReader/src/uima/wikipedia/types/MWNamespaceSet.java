package uima.wikipedia.types;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MWNamespaceSet {
	public final Map<Integer, String>	namespaces;

	public MWNamespaceSet() {
		namespaces = new LinkedHashMap<Integer, String>();
	}

	public void add(int index, String prefix) {
		namespaces.put(index, prefix);
	}

	public boolean hasPrefix(String prefix) {
		return namespaces.containsValue(prefix);
	}

	public boolean hasIndex(int index) {
		return namespaces.containsKey(index);
	}

	public String getPrefix(int index) {
		if (namespaces.containsKey(index))
			return namespaces.get(index);
		return "";
	}

	public int getIndex(String prefix) {
		for (final int index : namespaces.keySet())
			if (namespaces.get(index).equals(prefix))
				return index;
		return 0;
	}

	public Iterator<Entry<Integer, String>> orderedEntries() {
		return namespaces.entrySet().iterator();
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		for (final String name : namespaces.values()) {
			result.append(name).append('\n');
		}
		return result.toString();
	}
}