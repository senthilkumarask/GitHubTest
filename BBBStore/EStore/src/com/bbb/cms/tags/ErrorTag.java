package com.bbb.cms.tags;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.logging.LogMessageFormatter;

/**
 * This class will be called for error taglib.It interacts with LblTxtTemplateManager class.
 * @author ajosh8
 *
 */
public class ErrorTag extends TagSupport{
  /**
   * 
   */
  private static final long serialVersionUID = -8789817960482501103L;

  private static final String LBL_MANAGER= "/com/bbb/cms/manager/LblTxtTemplateManager";

  // properties

  private String mSiteId;
  private String mKey;
  private String mLanguage;
  private String mErrMsg;
  private Map<String, String> mPlaceHolderMap;

  private static Nucleus nucleus = Nucleus.getGlobalNucleus();
  private static LblTxtTemplateManager contentManager = (LblTxtTemplateManager)nucleus.resolveName(LBL_MANAGER);
  private final static String BLANK_VALUE="BLANK_VALUE";

  /**
   * @return the siteId
   */
  public String getSiteId() {
    return mSiteId;
  }

  /**
   * @param siteId
   *          the siteId to set
   */
  public void setSiteId(String siteId) {
    this.mSiteId = siteId;
  }

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
   * @return the mErrMsg
   */
  public String getErrMsg() {
    return mErrMsg;
  }

  /**
   * @param mErrMsg the mErrMsg to set
   */
  public void setErrMsg(String errMsg) {
    this.mErrMsg = errMsg;
  }



  public Map<String, String> getPlaceHolderMap() {
    return mPlaceHolderMap;
  }

  public void setPlaceHolderMap(Map<String, String> mPlaceHolderMap) {
    this.mPlaceHolderMap = mPlaceHolderMap;
  }

  /**
   *  
   * @return EVAL_PAGE
   */
  public int doStartTag() {
    return EVAL_PAGE;
  }

  /**
   * This method interacts with the LblTxtManager manager to get the page
   * content.
   * @return EVAL_PAGE.
   */

  public int doEndTag() {

    /*
     * 1. Resolves LblTxtTemplateManager component 
     * 2. gets ErrorMessage from the repository 
     * 3. If no content is returned - Nothing will appear at JSP
     */

    JspWriter out = null;
    try {

      // Get the writer object for output.
      out = pageContext.getOut();
      // Interacting with Manager Class 
      String content = contentManager.getErrMsg(getKey(),getLanguage(), getPlaceHolderMap());
      if(StringUtils.isBlank(content)){
        out.print("No value found for key :"+getKey());
      } else {
    	  if(content.trim().equalsIgnoreCase(BLANK_VALUE)){
    		  out.print("");
    	  } else {
    		  out.print(content);
    	  }        
      }

    }  catch (IOException e) {
      LogMessageFormatter.formatMessage(null, "ioError");  
    } catch(Exception e){
      LogMessageFormatter.formatMessage(null, "eError");  
    } 

    return EVAL_PAGE;
  }
}
