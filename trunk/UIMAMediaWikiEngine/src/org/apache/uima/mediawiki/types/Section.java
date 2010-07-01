

/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** A section is a part of the content that is structured in sub-sections or paragraphs.
 * Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010
 * XML source: /Users/Bowbaq/Documents/Developpement/Eclipse Workspace/UIMAMediaWikiEngine/desc/wikipedia-ts.xml
 * @generated */
public class Section extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Section.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Section() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Section(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Section(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Section(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
 
    
  //*--------------*
  //* Feature: level

  /** getter for level - gets The level of the section, the higher the deeper.
   * @generated */
  public int getLevel() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_level == null)
      jcasType.jcas.throwFeatMissing("level", "org.apache.uima.mediawiki.types.Section");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Section_Type)jcasType).casFeatCode_level);}
    
  /** setter for level - sets The level of the section, the higher the deeper. 
   * @generated */
  public void setLevel(int v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_level == null)
      jcasType.jcas.throwFeatMissing("level", "org.apache.uima.mediawiki.types.Section");
    jcasType.ll_cas.ll_setIntValue(addr, ((Section_Type)jcasType).casFeatCode_level, v);}    
   
    
  //*--------------*
  //* Feature: parent

  /** getter for parent - gets The parent section, if any, of this current one.
   * @generated */
  public Section getParent() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "org.apache.uima.mediawiki.types.Section");
    return (Section)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Section_Type)jcasType).casFeatCode_parent)));}
    
  /** setter for parent - sets The parent section, if any, of this current one. 
   * @generated */
  public void setParent(Section v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "org.apache.uima.mediawiki.types.Section");
    jcasType.ll_cas.ll_setRefValue(addr, ((Section_Type)jcasType).casFeatCode_parent, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets The title of this section, if any.
   * @generated */
  public Header getTitle() {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Section");
    return (Header)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Section_Type)jcasType).casFeatCode_title)));}
    
  /** setter for title - sets The title of this section, if any. 
   * @generated */
  public void setTitle(Header v) {
    if (Section_Type.featOkTst && ((Section_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Section");
    jcasType.ll_cas.ll_setRefValue(addr, ((Section_Type)jcasType).casFeatCode_title, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    