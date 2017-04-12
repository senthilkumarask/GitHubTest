package com.bbb.commerce.browse.droplet;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * Droplet to access the 3rd part URL and assign it to a variable in page container to access applciation.
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: agoe21
 * Created on: December-2011
 * @author agoe21
 *
 */
public class ConfigURLDroplet extends BBBDynamoServlet {
	public static final String  OPARAM_OUTPUT="output";
	public static final String  OPARAM_EMPTY="empty";
	public static final String  PARAM_CONFIG_TYPE="configType";
	public static final String  PARAM_IMAGE_URL="imagePath";
	public static final String  PARAM_JS_URL="jsPath";
	public static final String  PARAM_CSS_URL="cssPath";
	public static final String  PARAM_SCENE7_URL="scene7Path";
	
	private String imageURLKey;
	private String jsURLKey;
	private String cssURLKey;
	private String scene7URLKey;
	
	private BBBCatalogTools catalogTools;

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * 
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
					throws ServletException, IOException
					{
		final String configType  =request.getParameter(PARAM_CONFIG_TYPE);
	
		logDebug("request Parameters value[configType ="+configType+"]");
		
		Map<String,String> urlMap;
		try {
			
			urlMap = this.getCatalogTools().getConfigValueByconfigType(configType);
			if(urlMap!=null && (!urlMap.isEmpty())){
				logDebug("[OutParameter=output]");
	
				if(("null").equals(urlMap.get(getImageURLKey())) || StringUtils.isEmpty(urlMap.get(getImageURLKey()))){
					request.setParameter (PARAM_IMAGE_URL, "");
				}else{
					request.setParameter (PARAM_IMAGE_URL, urlMap.get(getImageURLKey()));					
				}
				
				if(("null").equals(urlMap.get(getCssURLKey())) || StringUtils.isEmpty(urlMap.get(getCssURLKey()))){
					request.setParameter (PARAM_CSS_URL, "");
				}else{
					request.setParameter (PARAM_CSS_URL, urlMap.get(getCssURLKey()));					
				}
				
				if(("null").equals(urlMap.get(getJsURLKey())) || StringUtils.isEmpty(urlMap.get(getJsURLKey()))){
					request.setParameter (PARAM_JS_URL, "");
				}else{
					request.setParameter (PARAM_JS_URL, urlMap.get(getJsURLKey()));					
				}
				
				if(("null").equals(urlMap.get(getScene7URLKey())) || StringUtils.isEmpty(urlMap.get(getScene7URLKey()))){
					request.setParameter (PARAM_SCENE7_URL, "");
				}else{
					request.setParameter (PARAM_SCENE7_URL, urlMap.get(getScene7URLKey()));
					}
				
				request.serviceParameter (OPARAM_OUTPUT, request, response);
			}
		} 
		catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "System Exception in ConfigURLfrom service of ConfigURLDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1026),e);
			request.setParameter("error", "err_config_url_error");
			request.serviceLocalParameter("error", request, response);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(request, "Business Exception in ConfigURLfrom service of ConfigURLDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1027),e);
			request.setParameter("error", "err_config_url_error");
			request.serviceLocalParameter("error", request, response);
		}
	}

	/**
	 * @return the imageURLKey
	 */
	public String getImageURLKey() {
		return imageURLKey;
	}

	/**
	 * @param pImageURLKey the imageURLKey to set
	 */
	public void setImageURLKey(final String pImageURLKey) {
		imageURLKey = pImageURLKey;
	}

	/**
	 * @return the jsURLKey
	 */
	public String getJsURLKey() {
		return jsURLKey;
	}

	/**
	 * @param pJsURLKey the jsURLKey to set
	 */
	public void setJsURLKey(final String pJsURLKey) {
		jsURLKey = pJsURLKey;
	}

	/**
	 * @return the cssURLKey
	 */
	public String getCssURLKey() {
		return cssURLKey;
	}

	/**
	 * @param pCssURLKey the cssURLKey to set
	 */
	public void setCssURLKey(final String pCssURLKey) {
		cssURLKey = pCssURLKey;
	}

	/**
	 * @return the scene7URLKey
	 */
	public String getScene7URLKey() {
		return scene7URLKey;
	}

	/**
	 * @param scene7urlKey the scene7URLKey to set
	 */
	public void setScene7URLKey(String scene7urlKey) {
		scene7URLKey = scene7urlKey;
	}
}