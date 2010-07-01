
/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Header of a section
 * Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010
 * @generated */
public class Header_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Header_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Header_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Header(addr, Header_Type.this);
  			   Header_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Header(addr, Header_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = Header.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.uima.mediawiki.types.Header");
 
  /** @generated */
  final Feature casFeat_Level;
  /** @generated */
  final int     casFeatCode_Level;
  /** @generated */ 
  public int getLevel(int addr) {
        if (featOkTst && casFeat_Level == null)
      jcas.throwFeatMissing("Level", "org.apache.uima.mediawiki.types.Header");
    return ll_cas.ll_getIntValue(addr, casFeatCode_Level);
  }
  /** @generated */    
  public void setLevel(int addr, int v) {
        if (featOkTst && casFeat_Level == null)
      jcas.throwFeatMissing("Level", "org.apache.uima.mediawiki.types.Header");
    ll_cas.ll_setIntValue(addr, casFeatCode_Level, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Header_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Level = jcas.getRequiredFeatureDE(casType, "Level", "uima.cas.Integer", featOkTst);
    casFeatCode_Level  = (null == casFeat_Level) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Level).getCode();

  }
}



    