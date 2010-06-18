package RawWikiListener;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.wikimodel.wem.IWemListenerInline;
import org.wikimodel.wem.WikiFormat;
import org.wikimodel.wem.WikiParameters;
import org.wikimodel.wem.WikiReference;

import uima.wikipedia.types.*;

public class RawWikiInlineListener implements IWemListenerInline{
	StringBuilder	buffer;
	JCas			mCas;
	
	public RawWikiInlineListener(StringBuilder buffer, JCas mcas) {
		this.buffer = buffer;
		this.mCas = mcas;
	}
	
	@Override
	public void beginFormat(WikiFormat format) {
		// on ne fait rien
	}

	@Override
	public void endFormat(WikiFormat format) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEscape(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onImage(String ref) {
		buffer.append("IMAGE :"+ref);
		
	}

	@Override
	public void onImage(WikiReference ref) {
		buffer.append("IMAGE :"+ref);
		
	}

	@Override
	public void onLineBreak() {
		buffer.append("\n");		
	}

	@Override
	public void onNewLine() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReference(String ref) {
		buffer.append("reference :"+ref);
		
	}

	@Override
	public void onReference(WikiReference ref) {
		buffer.append("reference :"+ref);
		
	}

	@Override
	public void onSpace(String str) {
		buffer.append(" ");
		
	}

	@Override
	public void onSpecialSymbol(String str) {
		buffer.append(str);
		
	}

	@Override
	public void onVerbatimInline(String str, WikiParameters params) {
		buffer.append("verbatim :"+str);
		
	}

	@Override
	public void onWord(String str) {
		buffer.append(str);
		
	}

}
