/* First created by JCasGen Thu Feb 25 18:30:15 CET 2010 */
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
 * A section is a part of the content that is structured in sub-sections or paragraphs. Updated by JCasGen Thu
 * Feb 25 18:30:15 CET 2010
 * 
 * @generated
 */
public class Section_Type extends Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (Section_Type.this.useExistingInstance) {
														// Return eq fs instance if already created
														FeatureStructure fs = Section_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new Section(addr, Section_Type.this);
															Section_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new Section(addr, Section_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= Section.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("org.apache.uima.mediawiki.types.Section");

	/** @generated */
	final Feature				casFeat_level;
	/** @generated */
	final int					casFeatCode_level;

	/** @generated */
	public int getLevel(int addr) {
		if (featOkTst && casFeat_level == null) {
			jcas.throwFeatMissing("level", "org.apache.uima.mediawiki.types.Section");
		}
		return ll_cas.ll_getIntValue(addr, casFeatCode_level);
	}

	/** @generated */
	public void setLevel(int addr, int v) {
		if (featOkTst && casFeat_level == null) {
			jcas.throwFeatMissing("level", "org.apache.uima.mediawiki.types.Section");
		}
		ll_cas.ll_setIntValue(addr, casFeatCode_level, v);
	}

	/** @generated */
	final Feature	casFeat_parent;
	/** @generated */
	final int		casFeatCode_parent;

	/** @generated */
	public int getParent(int addr) {
		if (featOkTst && casFeat_parent == null) {
			jcas.throwFeatMissing("parent", "org.apache.uima.mediawiki.types.Section");
		}
		return ll_cas.ll_getRefValue(addr, casFeatCode_parent);
	}

	/** @generated */
	public void setParent(int addr, int v) {
		if (featOkTst && casFeat_parent == null) {
			jcas.throwFeatMissing("parent", "org.apache.uima.mediawiki.types.Section");
		}
		ll_cas.ll_setRefValue(addr, casFeatCode_parent, v);
	}

	/** @generated */
	final Feature	casFeat_title;
	/** @generated */
	final int		casFeatCode_title;

	/** @generated */
	public int getTitle(int addr) {
		if (featOkTst && casFeat_title == null) {
			jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Section");
		}
		return ll_cas.ll_getRefValue(addr, casFeatCode_title);
	}

	/** @generated */
	public void setTitle(int addr, int v) {
		if (featOkTst && casFeat_title == null) {
			jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Section");
		}
		ll_cas.ll_setRefValue(addr, casFeatCode_title, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Section_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

		casFeat_level = jcas.getRequiredFeatureDE(casType, "level", "uima.cas.Integer", featOkTst);
		casFeatCode_level = null == casFeat_level ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_level).getCode();

		casFeat_parent = jcas.getRequiredFeatureDE(casType, "parent", "org.apache.uima.mediawiki.types.Section", featOkTst);
		casFeatCode_parent = null == casFeat_parent ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_parent).getCode();

		casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "org.apache.uima.mediawiki.types.Header", featOkTst);
		casFeatCode_title = null == casFeat_title ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_title).getCode();

	}
}
