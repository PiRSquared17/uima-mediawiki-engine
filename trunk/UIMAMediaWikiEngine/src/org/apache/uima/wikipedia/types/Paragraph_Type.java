/* First created by JCasGen Thu Feb 25 18:30:15 CET 2010 */
package org.apache.uima.wikipedia.types;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * A paragraph is a piece of texte composing a section, or an article without sections. Updated by JCasGen Thu
 * Feb 25 18:30:15 CET 2010
 * 
 * @generated
 */
public class Paragraph_Type extends Annotation_Type {
	/** @generated */
	@Override
	protected FSGenerator getFSGenerator() {
		return fsGenerator;
	}

	/** @generated */
	private final FSGenerator	fsGenerator	= new FSGenerator() {
												public FeatureStructure createFS(int addr, CASImpl cas) {
													if (Paragraph_Type.this.useExistingInstance) {
														// Return eq fs instance if already created
														FeatureStructure fs = Paragraph_Type.this.jcas.getJfsFromCaddr(addr);
														if (null == fs) {
															fs = new Paragraph(addr, Paragraph_Type.this);
															Paragraph_Type.this.jcas.putJfsFromCaddr(addr, fs);
															return fs;
														}
														return fs;
													} else
														return new Paragraph(addr, Paragraph_Type.this);
												}
											};
	/** @generated */
	public final static int		typeIndexID	= Paragraph.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	public final static boolean	featOkTst	= JCasRegistry.getFeatOkTst("uima.wikipedia.types.Paragraph");

	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 */
	public Paragraph_Type(JCas jcas, Type casType) {
		super(jcas, casType);
		casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

	}
}
