/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Indicate a Table of Content block. Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010 XML source:
 * /Users/Bowbaq/Documents/Developpement/Eclipse Workspace/UIMAMediaWikiEngine/desc/wikipedia-ts.xml
 * 
 * @generated
 */
public class TableOfContent extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	typeIndexID	= JCasRegistry.register(TableOfContent.class);
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
	protected TableOfContent() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public TableOfContent(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public TableOfContent(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public TableOfContent(JCas jcas, int begin, int end) {
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
