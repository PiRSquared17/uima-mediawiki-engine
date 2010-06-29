package uima.wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

import uima.wikipedia.parser.MWLanguage;
import uima.wikipedia.parser.TestDocumentBuilder;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String path = "/Users/Bowbaq/Desktop/Test_cases/def_list.txt";
			StringBuilder text = new StringBuilder();
			String line;
			TestDocumentBuilder builder = new TestDocumentBuilder();
			MarkupParser parser = new MarkupParser(new MWLanguage());
			parser.setBuilder(builder);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));

			line = reader.readLine();
			while (line != null) {
				text.append(line + '\n');
				line = reader.readLine();
			}

			parser.parse(text.toString());
			System.out.println(builder.getText());

		} catch (FileNotFoundException e) {
			// OSEF
		} catch (IOException e) {
			// OSEF
		}
	}
}
