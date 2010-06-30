/* First created by JCasGen Thu Feb 25 18:30:15 CET 2010 */
package org.apache.uima.wikipedia.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * A paragraph is a piece of texte composing a section, or an article without sections. Updated by JCasGen Thu
 * Feb 25 18:30:15 CET 2010 XML source: /tmp/wikipedia-cr/desc/wikipedia-ts.xml
 * 
 * @generated
 */
public class Paragraph extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	typeIndexID	= JCasRegistry.register(Paragraph.class);
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	type		= typeIndexID;

	/** @generated */
	@Override
	public int getTypeIndexID() {
		return typeIndexID;
	}

	/**
	 * Never called. Disable default constructor
	 * 
	 * @generated
	 */
	protected Paragraph() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Paragraph(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public Paragraph(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public Paragraph(JCas jcas, int begin, int end) {
		super(jcas);
		setBegin(begin);
		setEnd(end);
		readObject();
	}

	/**
	 * <!-- begin-user-doc --> Write your own initialization here <!-- end-user-doc -->
	 * 
	 * @generated modifiable
	 */
	private void readObject() {
	}

}
