/* First created by JCasGen Thu Feb 25 18:30:14 CET 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * A revision is the version of an article by a contributor. It is most likely based on the direct previous
 * revision. Updated by JCasGen Thu Feb 25 18:30:14 CET 2010
 * 
 * @generated
 */
public class Revision_Type extends Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (Revision_Type.this.useExistingInstance) {
														// Return eq fs instance
														// if already created
														FeatureStructure fs = Revision_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new Revision(addr, Revision_Type.this);
															Revision_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new Revision(addr, Revision_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= Revision.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("org.apache.uima.mediawiki.types.Revision");

	/** @generated */
	final Feature				casFeat_user;
	/** @generated */
	final int					casFeatCode_user;

	/** @generated */
	public String getUser(int addr) {
		if (featOkTst && casFeat_user == null) {
			jcas.throwFeatMissing("user", "org.apache.uima.mediawiki.types.Revision");
		}
		return ll_cas.ll_getStringValue(addr, casFeatCode_user);
	}

	/** @generated */
	public void setUser(int addr, String v) {
		if (featOkTst && casFeat_user == null) {
			jcas.throwFeatMissing("user", "org.apache.uima.mediawiki.types.Revision");
		}
		ll_cas.ll_setStringValue(addr, casFeatCode_user, v);
	}

	/** @generated */
	final Feature	casFeat_comment;
	/** @generated */
	final int		casFeatCode_comment;

	/** @generated */
	public String getComment(int addr) {
		if (featOkTst && casFeat_comment == null) {
			jcas.throwFeatMissing("comment", "org.apache.uima.mediawiki.types.Revision");
		}
		return ll_cas.ll_getStringValue(addr, casFeatCode_comment);
	}

	/** @generated */
	public void setComment(int addr, String v) {
		if (featOkTst && casFeat_comment == null) {
			jcas.throwFeatMissing("comment", "org.apache.uima.mediawiki.types.Revision");
		}
		ll_cas.ll_setStringValue(addr, casFeatCode_comment, v);
	}

	/** @generated */
	final Feature	casFeat_timestamp;
	/** @generated */
	final int		casFeatCode_timestamp;

	/** @generated */
	public double getTimestamp(int addr) {
		if (featOkTst && casFeat_timestamp == null) {
			jcas.throwFeatMissing("timestamp", "org.apache.uima.mediawiki.types.Revision");
		}
		return ll_cas.ll_getDoubleValue(addr, casFeatCode_timestamp);
	}

	/** @generated */
	public void setTimestamp(int addr, double v) {
		if (featOkTst && casFeat_timestamp == null) {
			jcas.throwFeatMissing("timestamp", "org.apache.uima.mediawiki.types.Revision");
		}
		ll_cas.ll_setDoubleValue(addr, casFeatCode_timestamp, v);
	}

	/** @generated */
	final Feature	casFeat_isMinor;
	/** @generated */
	final int		casFeatCode_isMinor;

	/** @generated */
	public boolean getIsMinor(int addr) {
		if (featOkTst && casFeat_isMinor == null) {
			jcas.throwFeatMissing("isMinor", "org.apache.uima.mediawiki.types.Revision");
		}
		return ll_cas.ll_getBooleanValue(addr, casFeatCode_isMinor);
	}

	/** @generated */
	public void setIsMinor(int addr, boolean v) {
		if (featOkTst && casFeat_isMinor == null) {
			jcas.throwFeatMissing("isMinor", "org.apache.uima.mediawiki.types.Revision");
		}
		ll_cas.ll_setBooleanValue(addr, casFeatCode_isMinor, v);
	}

	/** @generated */
	final Feature	casFeat_id;
	/** @generated */
	final int		casFeatCode_id;

	/** @generated */
	public int getId(int addr) {
		if (featOkTst && casFeat_id == null) {
			jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Revision");
		}
		return ll_cas.ll_getIntValue(addr, casFeatCode_id);
	}

	/** @generated */
	public void setId(int addr, int v) {
		if (featOkTst && casFeat_id == null) {
			jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Revision");
		}
		ll_cas.ll_setIntValue(addr, casFeatCode_id, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Revision_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

		casFeat_user = jcas.getRequiredFeatureDE(casType, "user", "uima.cas.String", featOkTst);
		casFeatCode_user = null == casFeat_user ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_user).getCode();

		casFeat_comment = jcas.getRequiredFeatureDE(casType, "comment", "uima.cas.String", featOkTst);
		casFeatCode_comment = null == casFeat_comment ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_comment).getCode();

		casFeat_timestamp = jcas.getRequiredFeatureDE(casType, "timestamp", "uima.cas.Double", featOkTst);
		casFeatCode_timestamp = null == casFeat_timestamp ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_timestamp).getCode();

		casFeat_isMinor = jcas.getRequiredFeatureDE(casType, "isMinor", "uima.cas.Boolean", featOkTst);
		casFeatCode_isMinor = null == casFeat_isMinor ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_isMinor).getCode();

		casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Integer", featOkTst);
		casFeatCode_id = null == casFeat_id ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_id).getCode();

	}
}
