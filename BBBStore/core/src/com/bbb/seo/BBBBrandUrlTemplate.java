package com.bbb.seo;

import java.net.URLDecoder;
import atg.commerce.catalog.CatalogTools;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * This class forms brands url
 * 
 * @author aban13
 *
 */
public class BBBBrandUrlTemplate extends IndirectUrlTemplate {

	private String[] entities;
	private CatalogTools catalogTools;
	private String type;
	private BBBCatalogTools bbbCatalogTools;

	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * @param bbbCatalogTools the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
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
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the catalogTools
	 */
	public CatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(CatalogTools catalogTools) {
		this.catalogTools = catalogTools;
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
	public void setEntities(String[] entities) {
		this.entities = entities;
	}

	public String formatUrl(UrlParameter[] pUrlParams, WebApp pDefaultWebApp) throws ItemLinkException {
		String formatUrl = null;
		if(isLoggingDebug()){
			logDebug("Fetching formatUrl for template : " + getName());
		}
		
		if (pUrlParams != null && pUrlParams.length > 0) {
			String brandName = pUrlParams[0].toString();
			int length = pUrlParams.length;
			for (int i = 0; i < length; i++) {
				if (i == 0) {
					String decodedUrl = URLDecoder.decode(pUrlParams[i].toString());
					decodedUrl = escapeHtmlString(decodedUrl);
					pUrlParams[i].setValue(formattedDisplayName(decodedUrl));
				}
			}
			formatUrl = super.formatUrl(pUrlParams, pDefaultWebApp);
			
			//R2.2 RM DEFECT- 23496 Start.Not able to see the changes made in Endeca in IST forSEO copy inbrand page
			
			String brandId=getBbbCatalogTools().getBrandId(brandName);
			formatUrl=formatUrl + BBBCoreConstants.SLASH + brandId;
			
			//R2.2 RM DEFECT- 23496 END
			
			if(BBBUtility.isNotEmpty(formatUrl)){
				formatUrl=formatUrl.toLowerCase();
			}
			
		}
		
		
		/*formatUrl = super.formatUrl(pUrlParams, pDefaultWebApp);
		
		if(isLoggingDebug()){
			logDebug("URL before removing special characters : " + formatUrl);
		}
		// //R2.2 Seo Friendly Url - 504 A - Format url for special characters and spaces and change upper case to lower case
		if(BBBUtility.isNotEmpty(formatUrl)){
			String[] url = formatUrl.split(BBBCoreConstants.SLASH);
			
			String finalUrl = BBBCoreConstants.SLASH;
			for(int i=0; i < url.length; i ++){
				if(i != 0){
					String decodedUrl = URLDecoder.decode(url[i].toString());
					decodedUrl = escapeHtmlString(decodedUrl);
					url[i] = formattedDisplayName(decodedUrl);
					finalUrl = finalUrl + url[i] + BBBCoreConstants.SLASH;
				}
			}
			formatUrl=finalUrl;
			formatUrl = formatUrl.toLowerCase();*/
			
			
			
		if(isLoggingDebug()){
			logDebug("URL after removing special characters : " + formatUrl);
		}
		return formatUrl;
	}
		

	/**
	 * Method used to escape special character/space in product URL
	 * 
	 * @param displayName
	 * @return resutlString
	 */
	private String formattedDisplayName(String displayName) {

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
		return resutlString;
	}

	/**
	 * Method used to escape HTML Strings
	 * 
	 * @param pStr
	 * @param pEscapeAmp
	 * @return
	 */
	public String escapeHtmlString(String str) {
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
	
	public String generateSeoUrl(String pBrandName) throws ItemLinkException {
		
		logDebug("Creating SEO Url for Brand Name: " + pBrandName);
		WebApp pDefaultWebApp = null;
		UrlParameter[] pUrlParams = cloneUrlParameters();
		pUrlParams[0].setValue(pBrandName);
		return formatUrl(pUrlParams, pDefaultWebApp); 
	}

}
