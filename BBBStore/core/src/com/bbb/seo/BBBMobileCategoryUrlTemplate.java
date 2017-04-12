package com.bbb.seo;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * This class override method to format category urls for mobile sites.
 * 
 * @author ssha53
 *
 */
public class BBBMobileCategoryUrlTemplate extends BBBCategoryUrlTemplate {
	

	/**
	 * This method used to format url using CategoryUrlTemplate.
	 * 
	 * @param pUrlParams
	 * @return pDefaultWebApp
	 */
	public String formatUrl(final UrlParameter[] pUrlParams, final WebApp pDefaultWebApp) throws ItemLinkException {
		String formatUrl = null;
		if(isLoggingDebug()){
			logDebug("BBBMobileCategoryUrlTemplate.formatUrl() Fetching formatUrl for template : " + getName());
		}
		if (pUrlParams != null && pUrlParams.length > 0) {
			String categoryId = null;
			if(null != pUrlParams[pUrlParams.length - 2]){
				categoryId = pUrlParams[pUrlParams.length - 2].toString();
			}
			// BPSI-120 Mobile SEO Friendly URLs | Change in MobileSiteMapGenerator for  L2 and L3 categories changes start
			final RepositoryItem item = getBbbCatalogTools().getCategoryRepDetail(SiteContextManager.getCurrentSiteId(), categoryId);
			if (null !=item) {
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
						// forming the mobile seo urls /category/l1/l2/l3/catId
						formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam(catName);
					}
					formatUrl = formatUrl + BBBCoreConstants.SLASH + formatUrlParam((String) item.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)) + BBBCoreConstants.SLASH + categoryId + BBBCoreConstants.SLASH;
					
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
				if (getListOfExcludedUrlsExpressions() != null
						&& !getListOfExcludedUrlsExpressions().isEmpty()) {
					for (final String exclusionExp : getListOfExcludedUrlsExpressions()) {
						if (formatUrl.contains(exclusionExp)) {
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

}
