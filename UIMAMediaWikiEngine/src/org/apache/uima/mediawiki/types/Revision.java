/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * A revision is the version of an article by a contributor. It is most likely based on the direct previous
 * revision. Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010 XML source:
 * /Users/Bowbaq/Documents/Developpement/Eclipse Workspace/UIMAMediaWikiEngine/desc/wikipedia-ts.xml
 * 
 * @generated
 */
public class Revision extends Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	typeIndexID	= JCasRegistry.register(Revision.class);
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
	protected Revision() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Revision(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public Revision(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public Revision(JCas jcas, int begin, int end) {
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
	// * Feature: user

	/**
	 * getter for user - gets The identifier of the contributor in the MediaWiki.
	 * 
	 * @generated
	 */
	public String getUser() {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_user == null) {
			jcasType.jcas.throwFeatMissing("user", "org.apache.uima.mediawiki.types.Revision");
		}
		return jcasType.ll_cas.ll_getStringValue(addr, ((Revision_Type) jcasType).casFeatCode_user);
	}

	/**
	 * setter for user - sets The identifier of the contributor in the MediaWiki.
	 * 
	 * @generated
	 */
	public void setUser(String v) {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_user == null) {
			jcasType.jcas.throwFeatMissing("user", "org.apache.uima.mediawiki.types.Revision");
		}
		jcasType.ll_cas.ll_setStringValue(addr, ((Revision_Type) jcasType).casFeatCode_user, v);
	}

	// *--------------*
	// * Feature: comment

	/**
	 * getter for comment - gets A comment by the contributor that describes what the correspond new revision
	 * is made of in comparison to the previous one.
	 * 
	 * @generated
	 */
	public String getComment() {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_comment == null) {
			jcasType.jcas.throwFeatMissing("comment", "org.apache.uima.mediawiki.types.Revision");
		}
		return jcasType.ll_cas.ll_getStringValue(addr, ((Revision_Type) jcasType).casFeatCode_comment);
	}

	/**
	 * setter for comment - sets A comment by the contributor that describes what the correspond new revision
	 * is made of in comparison to the previous one.
	 * 
	 * @generated
	 */
	public void setComment(String v) {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_comment == null) {
			jcasType.jcas.throwFeatMissing("comment", "org.apache.uima.mediawiki.types.Revision");
		}
		jcasType.ll_cas.ll_setStringValue(addr, ((Revision_Type) jcasType).casFeatCode_comment, v);
	}

	// *--------------*
	// * Feature: timestamp

	/**
	 * getter for timestamp - gets Date when the revision was made.
	 * 
	 * @generated
	 */
	public double getTimestamp() {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_timestamp == null) {
			jcasType.jcas.throwFeatMissing("timestamp", "org.apache.uima.mediawiki.types.Revision");
		}
		return jcasType.ll_cas.ll_getDoubleValue(addr, ((Revision_Type) jcasType).casFeatCode_timestamp);
	}

	/**
	 * setter for timestamp - sets Date when the revision was made.
	 * 
	 * @generated
	 */
	public void setTimestamp(double v) {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_timestamp == null) {
			jcasType.jcas.throwFeatMissing("timestamp", "org.apache.uima.mediawiki.types.Revision");
		}
		jcasType.ll_cas.ll_setDoubleValue(addr, ((Revision_Type) jcasType).casFeatCode_timestamp, v);
	}

	// *--------------*
	// * Feature: isMinor

	/**
	 * getter for isMinor - gets Flag indicating if the revision is a minor one (mostly typo fixes) or
	 * something more.
	 * 
	 * @generated
	 */
	public boolean getIsMinor() {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_isMinor == null) {
			jcasType.jcas.throwFeatMissing("isMinor", "org.apache.uima.mediawiki.types.Revision");
		}
		return jcasType.ll_cas.ll_getBooleanValue(addr, ((Revision_Type) jcasType).casFeatCode_isMinor);
	}

	/**
	 * setter for isMinor - sets Flag indicating if the revision is a minor one (mostly typo fixes) or
	 * something more.
	 * 
	 * @generated
	 */
	public void setIsMinor(boolean v) {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_isMinor == null) {
			jcasType.jcas.throwFeatMissing("isMinor", "org.apache.uima.mediawiki.types.Revision");
		}
		jcasType.ll_cas.ll_setBooleanValue(addr, ((Revision_Type) jcasType).casFeatCode_isMinor, v);
	}

	// *--------------*
	// * Feature: id

	/**
	 * getter for id - gets Internal identifier of the revision in MediaWiki.
	 * 
	 * @generated
	 */
	public int getId() {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_id == null) {
			jcasType.jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Revision");
		}
		return jcasType.ll_cas.ll_getIntValue(addr, ((Revision_Type) jcasType).casFeatCode_id);
	}

	/**
	 * setter for id - sets Internal identifier of the revision in MediaWiki.
	 * 
	 * @generated
	 */
	public void setId(int v) {
		if (Revision_Type.featOkTst && ((Revision_Type) jcasType).casFeat_id == null) {
			jcasType.jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Revision");
		}
		jcasType.ll_cas.ll_setIntValue(addr, ((Revision_Type) jcasType).casFeatCode_id, v);
	}
}
