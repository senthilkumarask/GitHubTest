package com.bbb.multisite;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextRuleFilter;
import atg.multisite.SiteSessionManager;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

import com.bbb.constants.TBSConstants;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

public class TBSStoreSiteRuleFilter extends GenericService implements SiteContextRuleFilter {
	
	/**
	 * mSearchStoreManager
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	
    /**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}

	/**
	 * This method is used to get the site id from the storeNumber that is there in the session.
	 * If no site found then default assigning TBS_BedBathUS
	 * 
	 * @param pRequest
	 * @param pSiteSessionManager
	 * @return
	 * 
	 */
	@Override
	public String filter(DynamoHttpServletRequest pRequest, SiteSessionManager pSiteSessionManager) {
		
		vlogDebug ("TBSStoreSiteRuleFilter :: filter() :: START");
		
		String siteId = null;
		RepositoryItem storeItem = null;
		String storeType = null;
		String storeNumber = (String)pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		
    	if( storeNumber == null ) {
			return null;
		}
		try {
			storeItem = getSearchStoreManager().getStoreRepository().getItem(storeNumber, TBSConstants.STORE);
			storeType = (String) storeItem.getPropertyValue(TBSConstants.STORE_TYPE);
			vlogDebug ("storeType  == "+storeType);
			//if the store type value is not there then default assigning siteId to TBS_BedBathUS
			if(StringUtils.isBlank(storeType)){
				siteId = TBSConstants.SITE_TBS_BAB_US;
			} else if(storeType.equals(TBSConstants.US_STORE_TYPE)){
				siteId = TBSConstants.SITE_TBS_BAB_US;
			} else if(storeType.equals(TBSConstants.BUYBUYBABY_STORE_TYPE)){
				siteId = TBSConstants.SITE_TBS_BBB;
			} else if(storeType.equals(TBSConstants.BEDBATH_CANADA_STORE_TYPE)){
				siteId = TBSConstants.SITE_TBS_BAB_CA;
			}
		} catch (RepositoryException e) {
			vlogError("RepositoryException occurred while getting storeItem using StoreNumber ::", e);
		}
		vlogDebug ("TBSStoreSiteRuleFilter :: filter() :: END");
		return siteId;
    }
}
