package org.apache.uima.wikipedia.types;

/* First created by JCasGen Fri Jun 11 14:18:24 CEST 2010 */

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * Updated by JCasGen Fri Jun 11 14:18:24 CEST 2010
 * 
 * @generated
 */
public class Annotation_Type extends org.apache.uima.jcas.tcas.Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (Annotation_Type.this.useExistingInstance) {
														// Return eq fs instance if already created
														FeatureStructure fs = Annotation_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new Annotation(addr, Annotation_Type.this);
															Annotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new Annotation(addr, Annotation_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= Annotation.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("Annotation");

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Annotation_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

	}
}