/**
 * 
 */
package com.bbb.search.endeca.assembler.content;

import java.util.Map;

import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.constants.BBBEndecaConstants;

/**
 * @author sc0054
 *
 */
public class ContentPathResolver {
	
	//Property to hold siteToRootContentPath Map.
	private Map<String, String> siteToRootContentPath;
	
	//property to indicate channel specific folder path
	private Map<String, String> channelToContentCollectionPath;
	
	//property to hold different content paths for different type of Endeca drive pages
	private Map<String, String> pageToContentPathMapping;
	
	//property to indicate default content path to be used for node not found exceptions
	private String defaultContentPath;

	/**
	 * method to resolve content collection path 
	 * 
	 * @param pSearchQuery
	 * @return
	 */
	public String resolveContentCollectionPath(SearchQuery pSearchQuery) {
		StringBuffer contentCollection = new StringBuffer();
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		contentCollection.append(BBBEndecaConstants.CI_ROOT_CONTENT_PATH_NAME);
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		contentCollection.append(getSiteToRootContentPath().get(pSearchQuery.getSiteId()));
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		
		//additional code for supporting different content path per channel
		contentCollection.append(getChannelToContentCollectionPath().get(BBBEndecaConstants.CI_CONTENT_PATH_DEFAULT_CHANNEL_NAME));
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		if(pSearchQuery.isHeaderSearch()) {
			contentCollection.append(getPageToContentPathMapping().get(BBBEndecaConstants.CI_FLYOUT_CONTENT_PATH_LOOKUP_KEY));
		} else if(pSearchQuery.isFromBrandPage()) {
			contentCollection.append(getPageToContentPathMapping().get(BBBEndecaConstants.CI_BRAND_PLP_CONTENT_PATH_LOOKUP_KEY));
		} else if(pSearchQuery.isFromAllBrandsPage() ) {
			contentCollection.append(getPageToContentPathMapping().get(BBBEndecaConstants.CI_ALL_BRANDS_CONTENT_PATH_LOOKUP_KEY));
		} else if(pSearchQuery.isFromChecklistCategory()) {
			contentCollection.append(getPageToContentPathMapping().get(BBBEndecaConstants.CI_CHECKLIST_CATEGORY_PLP_CONTENT_PATH_LOOKUP_KEY));
		} else if(pSearchQuery.getKeyWord() == null ) {
			contentCollection.append(getPageToContentPathMapping().get(BBBEndecaConstants.CI_CATEGORY_PLP_CONTENT_PATH_LOOKUP_KEY));
		} else {
			contentCollection.append(getPageToContentPathMapping().get(BBBEndecaConstants.CI_SEARCH_PLP_CONTENT_PATH_LOOKUP_KEY));
		}
		
		return contentCollection.toString();
	}

	
	/**
	 * method to return default content collection path
	 * this default path would be used when path is not found exception is thrown from Assembler calls 
	 * 
	 * @param pSearchQuery
	 * @return
	 */
	public String getDefaultContentCollectionPath(SearchQuery pSearchQuery) {
		StringBuffer contentCollection = new StringBuffer();
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		contentCollection.append(BBBEndecaConstants.CI_ROOT_CONTENT_PATH_NAME);
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		contentCollection.append(getSiteToRootContentPath().get(pSearchQuery.getSiteId()));
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		
		//additional code for supporting different content path per channel
		contentCollection.append(getChannelToContentCollectionPath().get(BBBEndecaConstants.CI_CONTENT_PATH_DEFAULT_CHANNEL_NAME));
		
		contentCollection.append(BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR);
		
		contentCollection.append(getDefaultContentPath());
		
		return contentCollection.toString();
	}
	
	

	/**
	 * @return the siteToRootContentPath
	 */
	public Map<String, String> getSiteToRootContentPath() {
		return siteToRootContentPath;
	}

	/**
	 * @param siteToRootContentPath the siteToRootContentPath to set
	 */
	public void setSiteToRootContentPath(Map<String, String> siteToRootContentPath) {
		this.siteToRootContentPath = siteToRootContentPath;
	}

	/**
	 * @return the pageToContentPathMapping
	 */
	public Map<String, String> getPageToContentPathMapping() {
		return pageToContentPathMapping;
	}

	/**
	 * @param pageToContentPathMapping the pageToContentPathMapping to set
	 */
	public void setPageToContentPathMapping(Map<String, String> pageToContentPathMapping) {
		this.pageToContentPathMapping = pageToContentPathMapping;
	}

	/**
	 * @return the channelToContentCollectionPath
	 */
	public Map<String, String> getChannelToContentCollectionPath() {
		return channelToContentCollectionPath;
	}

	/**
	 * @param channelToContentCollectionPath the channelToContentCollectionPath to set
	 */
	public void setChannelToContentCollectionPath(
			Map<String, String> channelToContentCollectionPath) {
		this.channelToContentCollectionPath = channelToContentCollectionPath;
	}


	/**
	 * @return the defaultContentPath
	 */
	public String getDefaultContentPath() {
		return defaultContentPath;
	}


	/**
	 * @param defaultContentPath the defaultContentPath to set
	 */
	public void setDefaultContentPath(String defaultContentPath) {
		this.defaultContentPath = defaultContentPath;
	}
	


}
