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
 * This class will be called for textarea taglib.It interacts with LblTxtTemplateManager class. 
 * @author ajosh8
 *
 */
public class TextAreaTag extends TagSupport {

	private String mSiteId;
	private String mKey;
	private String mLanguage;
	private String mTextAreaValue;
	private Map<String, String> placeHolderMap;
	private static final String LBL_MGR_PATH = "/com/bbb/cms/manager/LblTxtTemplateManager";
	private static Nucleus nucleus = Nucleus.getGlobalNucleus();
	private static LblTxtTemplateManager contentManager = (LblTxtTemplateManager)nucleus.resolveName(LBL_MGR_PATH); 
	private static final String VALUE_NOT_FOUND="VALUE NOT FOUND FOR KEY ";
	private static final long serialVersionUID = 923754242693759832L;
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
	 * @return the textAreaValue
	 */
	public String getTextAreaValue() {
		return mTextAreaValue;
	}

	/**
	 * @param textAreaValue
	 *          the textAreaValue to set
	 */
	public void setTextAreaValue(String textAreaValue) {
		this.mTextAreaValue = textAreaValue;
	}

	/**
	 * Returns the PlaceHolderMap
	 * @return
	 */
	public Map<String, String> getPlaceHolderMap() {
		return placeHolderMap;
	}

	/**
	 * Sets the placeHolderMap which contains dynamic strings that need to be replaced
	 * @param placeHolderMap
	 */
	public void setPlaceHolderMap(Map<String, String> placeHolderMap) {
		this.placeHolderMap = placeHolderMap;
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
		 * 3. If no content is returned - ??????????
		 */
		try {
			// resolving the dynamo request
			DynamoHttpServletRequest request =
				Utils.getDynamoRequest(pageContext.getRequest());

			if (request == null) {
				throw new Exception("Could not resolve dynamo request.");
			}

			// resolving LblTxtTemplateManager from the request
			String content =contentManager.getPageTextArea(getKey(),getLanguage(),getPlaceHolderMap());

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

		} catch (IOException e) {
			LogMessageFormatter.formatMessage(null, "ioTextArea");    
		}catch(Exception e){
			LogMessageFormatter.formatMessage(null, "eTextarea");  
		} 
		return EVAL_PAGE;
	}
}
