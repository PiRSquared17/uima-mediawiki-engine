package org.apache.uima.mediawiki.cr.util;

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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * A quick little tool class to open various types of input files and return an InputStream.
 * <p>
 * This class is inspired of a similar tool that can be found in the MWDumper project of the MediaWiki
 * foundation. It is much simpler though.
 * 
 * @author Maxime Bury &lt;Maxime.bury@gmail.com&gt;
 */
public class Tools {
	private static final int	IN_BUF_SZ	= 1024 * 1024;

	public static InputStream openInputFile(File file) throws IOException {
		return openInputFile(file.getAbsolutePath());
	}

	public static InputStream openInputFile(String arg) throws IOException {
		if (arg.equals("-"))
			new BufferedInputStream(System.in, IN_BUF_SZ);
		final InputStream infile = new BufferedInputStream(new FileInputStream(arg), IN_BUF_SZ);
		if (arg.endsWith(".gz"))
			return new GZIPInputStream(infile);
		else if (arg.endsWith(".bz2"))
			return new BZip2CompressorInputStream(infile);
		else
			return infile;
	}

}
