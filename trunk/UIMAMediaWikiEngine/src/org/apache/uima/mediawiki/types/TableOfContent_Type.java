/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * Indicate a Table of Content block. Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010
 * 
 * @generated
 */
public class TableOfContent_Type extends Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (TableOfContent_Type.this.useExistingInstance) {
														// Return eq fs instance if already created
														FeatureStructure fs = TableOfContent_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new TableOfContent(addr, TableOfContent_Type.this);
															TableOfContent_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new TableOfContent(addr, TableOfContent_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= TableOfContent.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("org.apache.uima.mediawiki.types.TableOfContent");

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public TableOfContent_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

	}
}
