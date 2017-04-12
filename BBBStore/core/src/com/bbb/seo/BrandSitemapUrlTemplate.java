package com.bbb.seo;

import java.net.URLDecoder;

import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * This class contain methods use to format brand urls.
 * 
 * @author shsa53
 *
 */
public class BrandSitemapUrlTemplate extends IndirectUrlTemplate {

	
	private String[] entities;
	
	private String type;

	/**
	 * This method used to format url for uding brand template.
	 * 
	 * @param pUrlParams
	 * @return pDefaultWebApp
	 */
	public String formatUrl(final UrlParameter[] pUrlParams, final WebApp pDefaultWebApp) throws ItemLinkException {
		if(isLoggingDebug()){
			logDebug("Fetching formatUrl for template : " + getName());
		}
		String formatUrl = null;
		if (pUrlParams != null && pUrlParams.length > 0) {
			String decodedUrl = URLDecoder.decode(pUrlParams[0].toString());
			decodedUrl = escapeHtmlString(decodedUrl);
			pUrlParams[0].setValue(formattedDisplayName(decodedUrl));
			formatUrl = super.formatUrl(pUrlParams, pDefaultWebApp);
			
			if(BBBUtility.isNotEmpty(formatUrl)){
				formatUrl=formatUrl.toLowerCase();
			}
			
			if(isLoggingDebug()){
				logDebug("URL after removing special characters : " + formatUrl);
			}			
		}

		return formatUrl;
	}
		

	/**
	 * Method used to escape special character/space in product URL
	 * 
	 * @param displayName
	 * @return resutlString
	 */
	private String formattedDisplayName(final String displayName) {

		String resutlString = displayName;
		if (!BBBUtility.isEmpty(resutlString)) {
			resutlString = resutlString.replaceAll(BBBCoreConstants.FORMAT_DISPLAY_NAME,
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
			logDebug("BrandSitemapUrlTemplate.formattedDisplayName() Formated display name : " + resutlString);
		}
		return resutlString;
	}

	/**
	 * Method used to escape HTML Strings
	 * 
	 * @param pStr
	 * @param pEscapeAmp
	 * @return
	 */
	private String escapeHtmlString(String str) {
		if (!BBBUtility.isEmpty(str)) {
			if ((getEntities() != null && !(getEntities().length == 0))) {
				for (int i = 0; i < entities.length; i++) {
					if (str.contains(entities[i])) {
						str = str.replaceAll(entities[i],
								BBBCoreConstants.BLANK);
					}
				}
			}
		}
		return str;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
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
