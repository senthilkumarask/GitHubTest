package com.bbb.cms.tags;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.tagext.TagSupport;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.taglib.dspjsp.Utils;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.logging.LogMessageFormatter;
/**
 * This class will be called for label taglib.It interacts with LblTxtTemplateManager class. 
 * @author ajosh8
 *
 */

public class LabelTag extends TagSupport{

  /**
   * 
   */

  private static final long serialVersionUID = -5970668841194275262L;
  private String mKey;
  private String mLanguage;
  private String mLabelValue;
  private Map<String, String> mPlaceHolderMap;
  private final static String LBL_MGR_PATH = "/com/bbb/cms/manager/LblTxtTemplateManager";
  private static Nucleus nucleus = Nucleus.getGlobalNucleus();
  private static LblTxtTemplateManager contentManager = (LblTxtTemplateManager)nucleus.resolveName(LBL_MGR_PATH);
  private final static String VALUE_NOT_FOUND="VALUE NOT FOUND FOR KEY ";
  private final static String BLANK_VALUE="BLANK_VALUE";

  /**
   * @return the key
   */
  public String getKey() {
    return mKey;
  }

  /**
   * @param key
   *          the key to set
   */
  public void setKey(String key) {
    this.mKey = key;
  }

  /**
   * @return the language
   */
  public String getLanguage() {
    return mLanguage;
  }

  /**
   * @param language
   *          the language to set
   */
  public void setLanguage(String language) {
    this.mLanguage = language;
  }

  /**
   * @return the labelValue
   */
  public String getLabelValue() {
    return mLabelValue;
  }

  /**
   * @param labelValue
   *          the labelValue to set
   */
  public void setLabelValue(String labelValue) {
    this.mLabelValue = labelValue;
  }

  /**
   *  
   * @return EVAL_PAGE
   */
  public int doStartTag() {
    return EVAL_PAGE;
  }

  /**
   * Returns mPlaceHolderMap Map
   * @return
   */
  public Map<String, String> getPlaceHolderMap() {
    return mPlaceHolderMap;
  }

  /**
   * Sets mPlaceHolderMap map
   * @param placeHolderMap
   */
  public void setPlaceHolderMap(Map<String, String> placeHolderMap) {
    this.mPlaceHolderMap = placeHolderMap;
  }

  /**
   * This method interacts with the site content manager to get the page
   * content.
   * @return EVAL_PAGE.
   */
  public int doEndTag() {
    /*
     * 1. Resolves LblTxtTemplateManager component
     * 2. gets label content from the repository
     * 3. If no content is returned - returns Blank
     */
    try {
      // resolving the dynamo request
      DynamoHttpServletRequest request =
          Utils.getDynamoRequest(pageContext.getRequest());

      if (request == null) {
        throw new Exception("Could not resolve dynamo request.");
      }

      String content =contentManager.getPageLabel(getKey(),getLanguage(),getPlaceHolderMap());
      if (StringUtils.isEmpty(content)) {
        pageContext.getOut().print(VALUE_NOT_FOUND + getKey());
      } 
      else {
    	  if(content.trim().equalsIgnoreCase(BLANK_VALUE)){
    		  pageContext.getOut().print("");
    	  } else {
    		  pageContext.getOut().print(content); 
    	  }        
      }

    }  catch (IOException e) {
      LogMessageFormatter.formatMessage(null, "ioLabel");  
    } catch(Exception e){
      LogMessageFormatter.formatMessage(null, "eLabel");  
    } 
    return EVAL_PAGE;
  }
}
