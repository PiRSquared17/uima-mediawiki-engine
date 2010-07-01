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
 * A link (internal or external). Updated by JCasGen Thu Feb 25 18:30:15 CET 2010
 * 
 * @generated
 */
public class Link_Type extends Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (Link_Type.this.useExistingInstance) {
														// Return eq fs instance if already created
														FeatureStructure fs = Link_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new Link(addr, Link_Type.this);
															Link_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new Link(addr, Link_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= Link.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("org.apache.uima.mediawiki.types.Link");

	/** @generated */
	final Feature				casFeat_label;
	/** @generated */
	final int					casFeatCode_label;

	/** @generated */
	public String getLabel(int addr) {
		if (featOkTst && casFeat_label == null) {
			jcas.throwFeatMissing("label", "org.apache.uima.mediawiki.types.Link");
		}
		return ll_cas.ll_getStringValue(addr, casFeatCode_label);
	}

	/** @generated */
	public void setLabel(int addr, String v) {
		if (featOkTst && casFeat_label == null) {
			jcas.throwFeatMissing("label", "org.apache.uima.mediawiki.types.Link");
		}
		ll_cas.ll_setStringValue(addr, casFeatCode_label, v);
	}

	/** @generated */
	final Feature	casFeat_link;
	/** @generated */
	final int		casFeatCode_link;

	/** @generated */
	public String getLink(int addr) {
		if (featOkTst && casFeat_link == null) {
			jcas.throwFeatMissing("link", "org.apache.uima.mediawiki.types.Link");
		}
		return ll_cas.ll_getStringValue(addr, casFeatCode_link);
	}

	/** @generated */
	public void setLink(int addr, String v) {
		if (featOkTst && casFeat_link == null) {
			jcas.throwFeatMissing("link", "org.apache.uima.mediawiki.types.Link");
		}
		ll_cas.ll_setStringValue(addr, casFeatCode_link, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Link_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

		casFeat_label = jcas.getRequiredFeatureDE(casType, "label", "uima.cas.String", featOkTst);
		casFeatCode_label = null == casFeat_label ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_label).getCode();

		casFeat_link = jcas.getRequiredFeatureDE(casType, "link", "uima.cas.String", featOkTst);
		casFeatCode_link = null == casFeat_link ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_link).getCode();

	}
}
