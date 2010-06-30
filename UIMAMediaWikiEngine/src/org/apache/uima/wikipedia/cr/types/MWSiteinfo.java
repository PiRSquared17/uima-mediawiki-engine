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
package org.apache.uima.wikipedia.cr.types;

import java.util.Collections;
import java.util.Map;

/**
 * This class represents the website's information. It is designed to be immutable, thus allowing fluent
 * access to it's data fields without fearing user's modifications.
 * <p>
 * This information is gathered from the &lt;siteinfo&gt; tag that is usually present at the beginning of the
 * document. It holds the namespace information, as well as a couple other informations. Namespaces and site
 * and language specific so if the parser can't gather this information, there is no way to recover it.
 * 
 * @author Maxime Bury <Maxime.bury@gmail.com>
 */
public class MWSiteinfo {
	public final String			sitename;
	public final String			base;
	public final String			generator;
	public final String			titlecase;
	public final MWNamespaceSet	namespaces;

	/**
	 * Constructs a new MWSiteInfo object. Once initialized, the fields can't be modified anymore, thus making
	 * this object immutable.
	 * 
	 * @param sitename
	 *            The website's name (Ex : Wikipedia)
	 * @param base
	 *            The base URL of the website (Ex : http://en.wikipedia.org/wiki/Main_Page)
	 * @param generator
	 *            The name and version of the tool that was used to generate the dump we are working on
	 * @param titlecase
	 *            The way titles must differ from one another to be valid
	 * @param namespaces
	 *            The set of namespaces in use on that website
	 * @see MWNamespaceSet
	 */
	public MWSiteinfo(String sitename, String base, String generator, String titlecase, Map<Integer, String> namespaces) {
		this.sitename = sitename;
		this.base = base;
		this.generator = generator;
		this.titlecase = titlecase;
		this.namespaces = new MWNamespaceSet(namespaces);
	}

	/**
	 * This class is more of an utility class used to handle the namespace set. It provides access by the
	 * integer key or by the string litteral prefix. You can also get an immutable view of the whole
	 * underlying map.
	 */
	public class MWNamespaceSet {
		private final Map<Integer, String>	namespaces;

		public MWNamespaceSet(Map<Integer, String> namespaces) {
			this.namespaces = namespaces;
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

		public Map<Integer, String> getMap() {
			return Collections.unmodifiableMap(namespaces);
		}
	}
}
