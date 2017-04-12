package com.bbb.seo;

import java.net.URLDecoder;

import javax.servlet.ServletException;

import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * This class extends IndirectUrlTemplate and contain method used to form SEO
 * url for custom landing pages.
 * @author ssha53
 *
 *@author ssha53
 *@version %I%, %G%
 */
public class CustomLandingPageIndirectUrlTemplate extends IndirectUrlTemplate {

	private String[] entities;
	
	/**
	 * This method is used to create url for input params array and 
	 * defaultWebApp.
	 * 
	 * @param pUrlParams
	 *            Catalog repository item
	 * @param pDefaultWebApp
	 *            Mobile Sitemap Generator service that holds global settings
	 * @throws ServletException
	 * @Throws IOException
	 */
	public String formatUrl(final UrlParameter[] pUrlParams, final WebApp pDefaultWebApp)
			throws ItemLinkException {
		String formatUrl = null;
		
		if(isLoggingDebug()){
			logDebug("CustomLandingPageIndirectUrlTemplate.formatUrl() -  start");
			logDebug("CustomLandingPageIndirectUrlTemplate.formatUrl() -  pUrlParams :: " + pUrlParams);
		}
		
		if (pUrlParams != null) {
			final int length = pUrlParams.length;
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					String decodedUrl = URLDecoder.decode(pUrlParams[i].toString());
					decodedUrl = escapeHtmlString(decodedUrl);
					pUrlParams[i].setValue(formattedDisplayName(decodedUrl));
				}
			}
			formatUrl = super.formatUrl(pUrlParams, pDefaultWebApp);
			if(BBBUtility.isNotEmpty(formatUrl)){
				formatUrl=formatUrl.toLowerCase();
			}
			
		}
		
		
		if(isLoggingDebug()){
			logDebug("CustomLandingPageIndirectUrlTemplate.formatUrl() -  formatUrl :: " + formatUrl);
			logDebug("CustomLandingPageIndirectUrlTemplate.formatUrl() -  end");
		}
		return formatUrl;

	}

	
	/**
	 * This Method is used to escape special character/space in product URL
	 * 
	 * @param displayName in <code>String</code> format
	 * @return resutlString in <code>String</code> format
	 */
	private String formattedDisplayName(final String displayName) {
		
		if(isLoggingDebug()){
			logDebug("CustomLandingPageIndirectUrlTemplate.formattedDisplayName() -  start");
			logDebug("CustomLandingPageIndirectUrlTemplate.formattedDisplayName() -  " +
					"displayName :: " + displayName);
		}

		String resutlString = displayName;
		if (!BBBUtility.isEmpty(resutlString)) {
			resutlString = resutlString.replaceAll("[^a-zA-Z0-9]+",
					BBBCoreConstants.HYPHEN);

			if (resutlString != null
					&& resutlString.startsWith(BBBCoreConstants.HYPHEN)) {
				resutlString = resutlString.substring(1);
			}
			if (resutlString != null
					&& resutlString.endsWith(BBBCoreConstants.HYPHEN)) {
				resutlString = resutlString.substring(0,
						resutlString.length() - 1);
			}
		}
		
		if(isLoggingDebug()){
			logDebug("CustomLandingPageIndirectUrlTemplate.formattedDisplayName() " +
					"-  resutlString ::  " + resutlString);
			logDebug("CustomLandingPageIndirectUrlTemplate.formattedDisplayName() -  end");
		}
		
		return resutlString;
	}

	
	/**
	 * Method used to escape HTML Strings
	 * 
	 * @param pStr in <code>String</code> format
	 * @param pEscapeAmp in <code>String</code> format.
	 * @return
	 */
	public String escapeHtmlString(String str) {
		if(isLoggingDebug()){
			logDebug("CustomLandingPageIndirectUrlTemplate.escapeHtmlString() -  start");
			logDebug("CustomLandingPageIndirectUrlTemplate.escapeHtmlString() -  str" + str);
			
		}
		
		if (!BBBUtility.isEmpty(str) && !BBBUtility.isEmpty(getEntities())) {
				for (int i = 0; i < entities.length; i++) {
					if (str.contains(entities[i])) {
						str = str.replaceAll(entities[i],
								BBBCoreConstants.BLANK);
					}
				}
		}
		
		if(isLoggingDebug()){
			logDebug("CustomLandingPageIndirectUrlTemplate.escapeHtmlString() -  str" + str);
			logDebug("CustomLandingPageIndirectUrlTemplate.escapeHtmlString() -  end");
		}
		return str;
	}
	
	
	/**
	 * @return the entities
	 */
	public String[] getEntities() {
		return entities;
	}
	

	/**
	 * @param entities
	 *            the entities to set
	 */
	public void setEntities(final String[] entities) {
		this.entities = entities;
	}

}
