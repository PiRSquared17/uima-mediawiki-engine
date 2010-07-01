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
 * An article is the unity describing a concept or an idea in the Wikipedia. It is composed of several
 * revisions, each of which proposing a content for the article. Updated by JCasGen Thu Feb 25 18:30:14 CET
 * 2010
 * 
 * @generated
 */
public class Article_Type extends Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (Article_Type.this.useExistingInstance) {
														// Return eq fs instance if already created
														FeatureStructure fs = Article_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new Article(addr, Article_Type.this);
															Article_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new Article(addr, Article_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= Article.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("org.apache.uima.mediawiki.types.Article");

	/** @generated */
	final Feature				casFeat_namespace;
	/** @generated */
	final int					casFeatCode_namespace;

	/** @generated */
	public int getNamespace(int addr) {
		if (featOkTst && casFeat_namespace == null) {
			jcas.throwFeatMissing("namespace", "org.apache.uima.mediawiki.types.Article");
		}
		return ll_cas.ll_getIntValue(addr, casFeatCode_namespace);
	}

	/** @generated */
	public void setNamespace(int addr, int v) {
		if (featOkTst && casFeat_namespace == null) {
			jcas.throwFeatMissing("namespace", "org.apache.uima.mediawiki.types.Article");
		}
		ll_cas.ll_setIntValue(addr, casFeatCode_namespace, v);
	}

	/** @generated */
	final Feature	casFeat_title;
	/** @generated */
	final int		casFeatCode_title;

	/** @generated */
	public String getTitle(int addr) {
		if (featOkTst && casFeat_title == null) {
			jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Article");
		}
		return ll_cas.ll_getStringValue(addr, casFeatCode_title);
	}

	/** @generated */
	public void setTitle(int addr, String v) {
		if (featOkTst && casFeat_title == null) {
			jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Article");
		}
		ll_cas.ll_setStringValue(addr, casFeatCode_title, v);
	}

	/** @generated */
	final Feature	casFeat_id;
	/** @generated */
	final int		casFeatCode_id;

	/** @generated */
	public double getId(int addr) {
		if (featOkTst && casFeat_id == null) {
			jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Article");
		}
		return ll_cas.ll_getDoubleValue(addr, casFeatCode_id);
	}

	/** @generated */
	public void setId(int addr, double v) {
		if (featOkTst && casFeat_id == null) {
			jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Article");
		}
		ll_cas.ll_setDoubleValue(addr, casFeatCode_id, v);
	}

	/** @generated */
	final Feature	casFeat_prefix;
	/** @generated */
	final int		casFeatCode_prefix;

	/** @generated */
	public String getPrefix(int addr) {
		if (featOkTst && casFeat_prefix == null) {
			jcas.throwFeatMissing("prefix", "org.apache.uima.mediawiki.types.Article");
		}
		return ll_cas.ll_getStringValue(addr, casFeatCode_prefix);
	}

	/** @generated */
	public void setPrefix(int addr, String v) {
		if (featOkTst && casFeat_prefix == null) {
			jcas.throwFeatMissing("prefix", "org.apache.uima.mediawiki.types.Article");
		}
		ll_cas.ll_setStringValue(addr, casFeatCode_prefix, v);
	}

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Article_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

		casFeat_namespace = jcas.getRequiredFeatureDE(casType, "namespace", "uima.cas.Integer", featOkTst);
		casFeatCode_namespace = null == casFeat_namespace ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_namespace).getCode();

		casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
		casFeatCode_title = null == casFeat_title ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_title).getCode();

		casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.Double", featOkTst);
		casFeatCode_id = null == casFeat_id ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_id).getCode();

		casFeat_prefix = jcas.getRequiredFeatureDE(casType, "prefix", "uima.cas.String", featOkTst);
		casFeatCode_prefix = null == casFeat_prefix ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_prefix).getCode();

	}
}
