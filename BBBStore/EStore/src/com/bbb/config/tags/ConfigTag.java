package com.bbb.config.tags;


import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.tagext.TagSupport;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.taglib.dspjsp.Utils;

import com.bbb.config.manager.ConfigTemplateManager;
import com.bbb.logging.LogMessageFormatter;


/**
 * This class will be called for thirdPartyURL taglib.It interacts with ThirdPartyURLTemplateManager class.
 * 
 * @author ssha53
 *
 */
public class ConfigTag extends TagSupport {

	private static final long serialVersionUID = 3327475399203870079L;
	private String mKey;
	private String mLanguage;
	private String mConfigName;

	private Map<String, String> placeHolderMap;
	private static final String CONFIG_MGR_PATH = "/com/bbb/config/manager/ConfigTemplateManager";
	private static Nucleus nucleus = Nucleus.getGlobalNucleus();
	private static ConfigTemplateManager configManager = (ConfigTemplateManager)nucleus.resolveName(CONFIG_MGR_PATH); 
	private static final String VALUE_NOT_FOUND="VALUE NOT FOUND FOR KEY";
	


	/**
	 * @return the configName
	 */
	public String getConfigName() {
		return mConfigName;
	}

	/**
	 * @param configName the configName to set
	 */
	public void setConfigName(String configName) {
		mConfigName = configName;
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
	 * This method interacts with the site config manager to get the url.
	 * @return EVAL_PAGE.
	 */
	public int doEndTag() {
		/*
		 * 1. Resolves ThirdPartyURLTemplateManager component
		 * 2. gets url from the repository
		 * 3. If no content is returned - ??????????
		 */
		try {
			// resolving the dynamo request
			DynamoHttpServletRequest request =
				Utils.getDynamoRequest(pageContext.getRequest());

			if (request == null) {
				throw new Exception("Could not resolve dynamo request.");
			}

			// resolving ThirdPartyURLTemplateManager from the request
			String content =configManager.getThirdPartyURL(getKey(), getConfigName(), getPlaceHolderMap());

			if (StringUtils.isEmpty(content)) {
				pageContext.getOut().print(VALUE_NOT_FOUND + getKey());
			} 
			else {
				pageContext.getOut().print(content); 
			}

		} catch (IOException e) {
			LogMessageFormatter.formatMessage(null, "ioThirdPartyURL");    
		}catch(Exception e){
			LogMessageFormatter.formatMessage(null, "eThirdPartyURL");  
		} 
		return EVAL_PAGE;
	}
}
