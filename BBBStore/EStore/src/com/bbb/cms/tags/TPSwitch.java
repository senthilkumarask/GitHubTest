package com.bbb.cms.tags;

import java.io.IOException;

import javax.servlet.jsp.tagext.BodyTagSupport;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.taglib.dspjsp.Utils;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.logging.LogMessageFormatter;
/**
 * This class will be called for label taglib.It interacts with TPSwitch class. 
 * @author msaha1
 *
 */

public class TPSwitch extends BodyTagSupport {

  /**
   * 
   */
	
	//private final static String VALUE_NOT_FOUND="VALUE NOT FOUND FOR KEY ";
	
	private static final long serialVersionUID = -7970689641194275262L;
	private String mTagName;
	private final static String CATALOG_TOOLS_PATH = "/com/bbb/commerce/catalog/BBBCatalogTools";
	private static Nucleus nucleus = Nucleus.getGlobalNucleus();
	private static BBBCatalogTools contentManager = (BBBCatalogTools)nucleus.resolveName(CATALOG_TOOLS_PATH);

  /**
   * @return the key
   */
  public String getTagName() {
    return mTagName;
  }

  /**
   * @param key
   *          the key to set
   */
  public void setTagName(String pTagName) {
    this.mTagName = pTagName;
  }


  /**
   *  
   * @return EVAL_PAGE
   */
  public int doStartTag() {
    return EVAL_PAGE;
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

      String content =contentManager.getContentCatalogConfigration(this.getTagName()).get(0);
      if (StringUtils.isEmpty(content)) {
    	  //request.getSession().setAttribute("keyValue", VALUE_NOT_FOUND);
        //pageContext.setAttribute("keyValue", "Value Not found");
        pageContext.getOut().print(content); 
      } 
      else {
    	  //request.getSession().setAttribute("keyValue", content);
    	  //pageContext.setAttribute("keyValue", content);
    	  pageContext.getOut().print(content); 
    	 

      }

    }  catch (IOException e) {
      LogMessageFormatter.formatMessage(null, "IOException");  
    } catch(Exception e){
      LogMessageFormatter.formatMessage(null, "Exception");  
    } 
    return EVAL_PAGE;
  }
}
