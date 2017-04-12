package com.bbb.commerce.browse.category;

import java.util.ArrayList;
import java.util.List;

import atg.servlet.ServletUtil;

import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * Helper class to retrieve all clearance items from catalog Copyright 2011 Bath
 * & Beyond, Inc. All Rights Reserved. Reproduction or use of this file without
 * explicit written consent is prohibited. Created by: njai13 Created on:
 * November-2011
 * 
 * @author njai13
 * 
 */
public class ClearanceToutHelper extends BBBGenericService implements
		IProdToutHelper {
	private BBBCatalogTools catalogTools;
	private BBBObjectCache objectCache;
	private int clearanceCacheTimeout;
	private static final String CONFIG_KEY = "clearanceCategories";
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * @return the objectCache
	 */
	public BBBObjectCache getObjectCache() {
		return objectCache;
	}

	/**
	 * @param objectCache the objectCache to set
	 */
	public void setObjectCache(BBBObjectCache objectCache) {
		this.objectCache = objectCache;
	}

	/**
	 * @return the clearanceCacheTimeout
	 */
	public int getClearanceCacheTimeout() {
		return clearanceCacheTimeout;
	}

	/**
	 * @param clearanceCacheTimeout the clearanceCacheTimeout to set
	 */
	public void setClearanceCacheTimeout(int clearanceCacheTimeout) {
		this.clearanceCacheTimeout = clearanceCacheTimeout;
	}
	
	/*
	 * This method will return the List of ProducyVO objects (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.commerce.browse.category.IProdToutHelper#getProducts(java.lang
	 * .String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ProductVO> getProducts(final String siteId,
			final String categoryId) throws BBBSystemException,
			BBBBusinessException {
	
		logDebug("Start of ClearanceToutHelper:getProduct");
	
		String cacheName = "";
		String cacheKey = "";
		List<ProductVO> productList = new ArrayList<ProductVO>();
		String key = siteId + "_" + CONFIG_KEY;

		List<String> listOfClearanceCatItems = (List<String>) catalogTools
				.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, key);
		//BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(null);
		
		String countryCode="";
		if(null != sessionBean){
			countryCode=(String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
		}
		if (null != listOfClearanceCatItems
				&& listOfClearanceCatItems.size() > 0){
			
			cacheKey = siteId + listOfClearanceCatItems.get(0);
			if(! BBBUtility.isEmpty(countryCode)){
				cacheKey = cacheKey+countryCode;
			}
			cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.CLEARANCE_PRODS_CACHE_NAME);

			logDebug("Get productlist of cache key::" + cacheKey +" from cache::"+ cacheName);
		
			
			productList = (List<ProductVO>) getObjectCache().get(cacheKey, cacheName);
			if(productList == null){
				//productlist is not there in the cache, get it from catalog and put it in the cache
			
				logDebug("No productlist for cache key::" + cacheKey +" from cache::"+ cacheName);
				
				productList = getCatalogTools().getClearanceProducts(siteId, listOfClearanceCatItems.get(0));
				
					if( productList != null && productList.size() > 0){
						logDebug("total number of prod retreived from catalog::"+productList.size());
					}
				
				//put the retrieved results in the cache
				getObjectCache().put(cacheKey, productList, cacheName, getCacheTimeout());
			}else{
			
					logDebug("products from the cache::"+productList.size());
				
			}
		}
		return productList;
	}
	
	/**
	 * Get cache Timeout for Clearance products
	 * @return
	 */
	private int getCacheTimeout(){
		int timeout = this.getClearanceCacheTimeout();
		try {
			timeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CLEARANCE_PRODS_CACHE_TIMEOUT ));
		} catch (NumberFormatException nfe) {
			
				logError("NFE while getting cache timeout for clearance prods", nfe);
			
		} catch (Exception e) {			
				logError("Exception while getting cache timeout for clearance prods", e);
			
		}
		
			logDebug("timeout for cache::"+timeout);
		
		return timeout;
	}
}
