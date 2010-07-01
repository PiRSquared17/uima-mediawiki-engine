package org.apache.uima.mediawiki.types;

/* First created by JCasGen Fri Jun 11 14:18:24 CEST 2010 */

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

/**
 * Updated by JCasGen Fri Jun 11 14:18:24 CEST 2010 XML source:
 * /home/barneystinson/workspace/AnalysisEngine/aeDescriptor.xml
 * 
 * @generated
 */
public class Annotation extends org.apache.uima.jcas.tcas.Annotation {
	/**
	 * @generated
	 * @ordered
	 */
	public final static int	typeIndexID	= JCasRegistry.register(Annotation.class);
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
	protected Annotation() {
	}

	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 */
	public Annotation(int addr, TOP_Type type) {
		super(addr, type);
		readObject();
	}

	/** @generated */
	public Annotation(JCas jcas) {
		super(jcas);
		readObject();
	}

	/** @generated */
	public Annotation(JCas jcas, int begin, int end) {
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

}
