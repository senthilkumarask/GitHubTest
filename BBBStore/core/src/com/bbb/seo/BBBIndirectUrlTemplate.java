package com.bbb.seo;

import java.net.URLDecoder;
import java.util.*;

import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

public class BBBIndirectUrlTemplate extends IndirectUrlTemplate {

	private String[] entities;
	private List<String> listOfExcludedUrlsExpressions;
	

	public String formatUrl(UrlParameter[] pUrlParams, WebApp pDefaultWebApp)
			throws ItemLinkException {
		String formatUrl = null;
		if (pUrlParams != null) {
			int length = pUrlParams.length;
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
			if (listOfExcludedUrlsExpressions != null
					&& !listOfExcludedUrlsExpressions.isEmpty()) {
				for (String exclusionExpression : listOfExcludedUrlsExpressions) {
					if (formatUrl.contains(exclusionExpression)) {
						return null;
					}
				}
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
			if (!(getEntities() == null) || !(getEntities().length == 0)) {
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
	/**
	 * The below method will return list of excluded expressions from site Map.
	 * 
	 * @return the listOfExcludedUrlsExpressions
	 */
	public List<String> getListOfExcludedUrlsExpressions() {
		return listOfExcludedUrlsExpressions;
	}


	/**
	 * To store list of excluded expressions from site Map, to set the value
	 * update the property file as show below
	 * listOfExcludedUrlsExpressions=non-navigable-products,\non-navigable-sku
	 * in IndirectUrl template
	 * 
	 * @param listOfExcludedUrlsExpressions
	 *            the listOfExcludedUrlsExpressions to set
	 */
	public void setListOfExcludedUrlsExpressions(
			List<String> listOfExcludedUrlsExpressions) {
		this.listOfExcludedUrlsExpressions = listOfExcludedUrlsExpressions;
	}

}
