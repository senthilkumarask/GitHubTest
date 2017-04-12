package com.bbb.config.manager;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * This class is responsible for getting thiedParty key/url
 * Message it would interact with repository and get the value.
 *
 * @author ssharma53
 *
 */
public class ConfigTemplateManager extends BBBGenericService {

	private BBBCatalogTools mCatalogTools;
	
	private String siteMapType = null;

	public String getSiteMapType() {
		return siteMapType;
	}


	public void setSiteMapType(String siteMapType) {
		this.siteMapType = siteMapType;
	}


	/**
	 * This method will interact with repository and get the url based on Key
	 *
	 * @param pKey
	 * @param pLanguage
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getThirdPartyURL(final String pKey, final String pConfigName, final Map<String, String> placeHolderMap) {

		return this.getThirdPartyURL(pKey, pConfigName);
	}


	/**
	 * This method will interact with repository and get the url based on Key
	 *
	 * @param pKey
	 * @param pLanguage
	 * @param placeHolderMap
	 * @param pSiteId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getThirdPartyURL(final String pKey, final String pConfigName) {
		List<String> urlValue = null;
		String url = null;
		this.logDebug("ThirdPartyURLTemplateManager.getThirdPartyURL() Method Entering");		

		try {
			 urlValue = this.getCatalogTools().getAllValuesForKey(pConfigName, pKey);

			} catch (final BBBSystemException e) {
				if(this.isLoggingError()){

				 this.logError(LogMessageFormatter.formatMessage(null, "ConfigTemplateManager.getThirdPartyURL() | BBBSystemException "));
				}
			} catch (final BBBBusinessException e) {
				if(this.isLoggingError()){

					 this.logError(LogMessageFormatter.formatMessage(null, "ConfigTemplateManager.getThirdPartyURL() | BBBBusinessException "));
			}
			}

		if ((null == urlValue) || (urlValue.size() == 0)) {
			url = "";
		}else{
			url = urlValue.get(0);
		}
		this.logDebug("ThirdPartyURLTemplateManager.getThirdPartyURL() Method Ending");		
		return url;

	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}


}
