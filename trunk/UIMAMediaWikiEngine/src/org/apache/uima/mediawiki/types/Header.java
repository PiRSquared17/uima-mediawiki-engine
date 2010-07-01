/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Header of a section Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010 XML source:
 * /Users/Bowbaq/Documents/Developpement/Eclipse Workspace/UIMAMediaWikiEngine/desc/wikipedia-ts.xml
 * 
 * @generated
 */
public class Header extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	typeIndexID	= JCasRegistry.register(Header.class);
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
	protected Header() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Header(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public Header(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public Header(JCas jcas, int begin, int end) {
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

	// *--------------*
	// * Feature: Level

	/**
	 * getter for Level - gets The header level : 1 is the highest (main title)
	 * 
	 * @generated
	 */
	public int getLevel() {
		if (Header_Type.featOkTst && ((Header_Type) jcasType).casFeat_Level == null) {
			jcasType.jcas.throwFeatMissing("Level", "org.apache.uima.mediawiki.types.Header");
		}
		return jcasType.ll_cas.ll_getIntValue(addr, ((Header_Type) jcasType).casFeatCode_Level);
	}

	/**
	 * setter for Level - sets The header level : 1 is the highest (main title)
	 * 
	 * @generated
	 */
	public void setLevel(int v) {
		if (Header_Type.featOkTst && ((Header_Type) jcasType).casFeat_Level == null) {
			jcasType.jcas.throwFeatMissing("Level", "org.apache.uima.mediawiki.types.Header");
		}
		jcasType.ll_cas.ll_setIntValue(addr, ((Header_Type) jcasType).casFeatCode_Level, v);
	}
}
