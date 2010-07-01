

/* First created by JCasGen Thu Jul 01 16:43:28 CEST 2010 */
package org.apache.uima.mediawiki.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** An article is the unity describing a concept or an idea in the Wikipedia. It is composed of several revisions, each of which proposing a content for the article.
 * Updated by JCasGen Thu Jul 01 16:43:28 CEST 2010
 * XML source: /Users/Bowbaq/Documents/Developpement/Eclipse Workspace/UIMAMediaWikiEngine/desc/wikipedia-ts.xml
 * @generated */
public class Article extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Article.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Article() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Article(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Article(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Article(JCas jcas, int begin, int end) {
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
  //* Feature: namespace

  /** getter for namespace - gets There are potentially several namespaces in a MediaWiki. However this feature is not really used in the Wikipedia.
   * @generated */
  public int getNamespace() {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_namespace == null)
      jcasType.jcas.throwFeatMissing("namespace", "org.apache.uima.mediawiki.types.Article");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Article_Type)jcasType).casFeatCode_namespace);}
    
  /** setter for namespace - sets There are potentially several namespaces in a MediaWiki. However this feature is not really used in the Wikipedia. 
   * @generated */
  public void setNamespace(int v) {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_namespace == null)
      jcasType.jcas.throwFeatMissing("namespace", "org.apache.uima.mediawiki.types.Article");
    jcasType.ll_cas.ll_setIntValue(addr, ((Article_Type)jcasType).casFeatCode_namespace, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets The title of the article, it mostly corresponds to the address of the content on the Wikipedia.
   * @generated */
  public String getTitle() {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Article");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Article_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets The title of the article, it mostly corresponds to the address of the content on the Wikipedia. 
   * @generated */
  public void setTitle(String v) {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.apache.uima.mediawiki.types.Article");
    jcasType.ll_cas.ll_setStringValue(addr, ((Article_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets Internal identifier of the article in the corresponding MediaWiki.
   * @generated */
  public double getId() {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Article");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Article_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets Internal identifier of the article in the corresponding MediaWiki. 
   * @generated */
  public void setId(double v) {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.apache.uima.mediawiki.types.Article");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Article_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: prefix

  /** getter for prefix - gets 
   * @generated */
  public String getPrefix() {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_prefix == null)
      jcasType.jcas.throwFeatMissing("prefix", "org.apache.uima.mediawiki.types.Article");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Article_Type)jcasType).casFeatCode_prefix);}
    
  /** setter for prefix - sets  
   * @generated */
  public void setPrefix(String v) {
    if (Article_Type.featOkTst && ((Article_Type)jcasType).casFeat_prefix == null)
      jcasType.jcas.throwFeatMissing("prefix", "org.apache.uima.mediawiki.types.Article");
    jcasType.ll_cas.ll_setStringValue(addr, ((Article_Type)jcasType).casFeatCode_prefix, v);}    
  }

    