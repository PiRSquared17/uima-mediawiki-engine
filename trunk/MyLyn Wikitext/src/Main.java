import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MarkupParser parser = new MarkupParser(new MediaWikiLanguage());
		StringBuilder builder = new StringBuilder();
		StringWriter writer = new StringWriter();
		parser.setBuilder(new RawTextBuilder(builder));
		// parser.setBuilder(new HtmlDocumentBuilder(writer));

		String wikiText = "===Titre de la page de test=== \n";
		wikiText += "Voici un [[w:lien|lien]].\n";
		wikiText += "Sur cette ligne il y'a du texte en ''italique''.\n";
		wikiText += "Sur celle ci du texte en '''gras'''.\n";
		wikiText += "Et sur celle la du '''''gras italique'''''.\n";
		wikiText += "\nParagraphe suivant";
		wikiText += " \n{|\n|+ Tableau \n|-\n|bla \n|blo \n|blu \n|-\n|bli \n|bly \n| \n|}\n";
		wikiText += "* badaboom \n*# bim \n*## surbim \n*# bam \n*# boom \n* taddaaaaaa!\n";
		parser.parse(wikiText);

		System.out.println(wikiText);
		System.out.println(builder.toString());
		// System.out.println(writer.toString());
	}

}
