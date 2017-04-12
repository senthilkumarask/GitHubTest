package com.bbb.seo;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import atg.commerce.catalog.CatalogTools;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * This class forms category urls
 * 
 * @author aban13
 *
 */
public class BBBCategoryUrlTemplate extends IndirectUrlTemplate {

	private String[] entities;
	private CatalogTools catalogTools;
	private String type;
	private BBBCatalogTools bbbCatalogTools;
	private List<String> listOfExcludedUrlsExpressions;
	
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String formatUrl(UrlParameter[] pUrlParams, WebApp pDefaultWebApp) throws ItemLinkException {
		String formatUrl = null;
		if(isLoggingDebug()){
			logDebug("Fetching formatUrl for template : " + getName());
		}
		if (pUrlParams != null && pUrlParams.length > 0) {
			String catId = pUrlParams[pUrlParams.length - 2].toString();
			String siteId = "";
			if (null != ServletUtil.getCurrentRequest()) {
				siteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
			}
			if (BBBUtility.isEmpty(siteId)){
				siteId = SiteContextManager.getCurrentSiteId();
			}
			RepositoryItem item = getBbbCatalogTools().getCategoryRepDetail(siteId, catId);
			if (item != null) {
				LinkedList ancestors = new LinkedList();
				RepositoryItem parent = item;
			    while (true) {
					if (isLoggingDebug()) logDebug("finding the ancestors.  parent = " + parent.getRepositoryId());
					final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) parent.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME);
                    if ((parentCategorySet != null) && !parentCategorySet.isEmpty()) {
                    	for (final RepositoryItem parentCatItem : parentCategorySet) {
                            parent = (RepositoryItem) parentCatItem;
                            break;
                        }
                    } else {
                    	break;
                    }
                    if (parent == null) {
			        	break;
			        }
                    ancestors.addFirst(parent);
			    }
				if (ancestors != null && ancestors.size() > 1) {
					Iterator<RepositoryItem> itr = ancestors.iterator();
					formatUrl = getType();
					boolean check = true;
					while (itr.hasNext()) {
						RepositoryItem parent1 = itr.next();
						String catName = (String) parent1.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						if(check){
							check = false;
							continue;
						}
						
						formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(catName);
					}
					formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam((String) item.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)) + BBBCoreConstants.SLASH + catId + BBBCoreConstants.SLASH;
					
				} else {
				
					if (pUrlParams != null) {
						int length = pUrlParams.length;
						for (int i = 0; i < length; i++) {
							if (i == 0) {
								String decodedUrl = URLDecoder.decode(pUrlParams[i].toString());
								decodedUrl = escapeHtmlString(decodedUrl);
								pUrlParams[i].setValue(formattedDisplayName(decodedUrl));
							}
						}
					}
					formatUrl = super.formatUrl(pUrlParams, pDefaultWebApp);
				}
			}
			//exclude url expressions from format url and convert format url to lower case
			if(BBBUtility.isNotEmpty(formatUrl)){
				if (listOfExcludedUrlsExpressions != null
						&& !listOfExcludedUrlsExpressions.isEmpty()) {
					for (String exclusionExpression : listOfExcludedUrlsExpressions) {
						if (formatUrl.contains(exclusionExpression)) {
							return null;
						}
					}
				}
				formatUrl = formatUrl.toLowerCase();
			}
		}
		if(isLoggingDebug()){
			logDebug("URL after removing special characters : " + formatUrl);
		}
		return formatUrl;
	}
		
	/**
	 * This method format all the category level names
	 * 
	 * @param urlParam
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String formatUrlParam(String urlParam){
		if(urlParam.contains("%")){
			urlParam = urlParam.replaceAll("%"," percentage");
		}
		String decodedUrl = URLDecoder.decode(urlParam);
		decodedUrl = escapeHtmlString(decodedUrl);
		urlParam = formattedDisplayName(decodedUrl);
		return urlParam;
	}

	/**
	 * Method used to escape special character/space in product URL
	 * 
	 * @param displayName
	 * @return resutlString
	 */
	protected String formattedDisplayName(String displayName) {

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
			if (!(getEntities() == null) || (getEntities() != null && !(getEntities().length == 0))) {
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
}
