/* First created by JCasGen Thu Feb 25 18:30:15 CET 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * A link (internal or external). Updated by JCasGen Thu Feb 25 18:30:15 CET 2010 XML source:
 * /tmp/wikipedia-cr/desc/wikipedia-ts.xml
 * 
 * @generated
 */
public class Link extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	typeIndexID	= JCasRegistry.register(Link.class);
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
	protected Link() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Link(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public Link(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public Link(JCas jcas, int begin, int end) {
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
	// * Feature: label

	/**
	 * getter for label - gets The link label.
	 * 
	 * @generated
	 */
	public String getLabel() {
		if (Link_Type.featOkTst && ((Link_Type) jcasType).casFeat_label == null) {
			jcasType.jcas.throwFeatMissing("label", "uima.wikipedia.types.Link");
		}
		return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type) jcasType).casFeatCode_label);
	}

	/**
	 * setter for label - sets The link label.
	 * 
	 * @generated
	 */
	public void setLabel(String v) {
		if (Link_Type.featOkTst && ((Link_Type) jcasType).casFeat_label == null) {
			jcasType.jcas.throwFeatMissing("label", "uima.wikipedia.types.Link");
		}
		jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type) jcasType).casFeatCode_label, v);
	}

	// *--------------*
	// * Feature: link

	/**
	 * getter for link - gets The address the link is pointing to.
	 * 
	 * @generated
	 */
	public String getLink() {
		if (Link_Type.featOkTst && ((Link_Type) jcasType).casFeat_link == null) {
			jcasType.jcas.throwFeatMissing("link", "uima.wikipedia.types.Link");
		}
		return jcasType.ll_cas.ll_getStringValue(addr, ((Link_Type) jcasType).casFeatCode_link);
	}

	/**
	 * setter for link - sets The address the link is pointing to.
	 * 
	 * @generated
	 */
	public void setLink(String v) {
		if (Link_Type.featOkTst && ((Link_Type) jcasType).casFeat_link == null) {
			jcasType.jcas.throwFeatMissing("link", "uima.wikipedia.types.Link");
		}
		jcasType.ll_cas.ll_setStringValue(addr, ((Link_Type) jcasType).casFeatCode_link, v);
	}
}
