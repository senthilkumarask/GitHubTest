/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  InvalidateObjectCacheScheduler.java
 *
 *  DESCRIPTION: Invalidate Object Cache
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  25/08/13 Initial version
 *
 */
package com.bbb.common.scheduler;

import static com.bbb.constants.BBBCoreConstants.INVALIDATE_MOBILE_TOP_CATEGORIES_CACHE;
import static com.bbb.constants.BBBCoreConstants.INVALIDATE_OBJECT_CACHE_NAMES;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.SEARCH_RESULT_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.MOBILE_ALL_TOP_CATEGORIES_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.TRUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;



import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;

import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class InvalidateObjectCacheScheduler extends SingletonSchedulableService {

	private boolean enabled;
	private String cacheNames;

	//Reference variable to SearchManager
	private SearchManager searchManager;
	private BBBObjectCache mObjectCache;
	
	//List of site Ids
	private List<String> siteList;

	public List<String> getSiteList() {
		return this.siteList;
	}

	public void setSiteList(List<String> siteList) {
		this.siteList = siteList;
	}

	@Override
	public void doScheduledTask(final Scheduler scheduler,
			final ScheduledJob job) {

		this.doScheduledTask();
	}

	public void doScheduledTask() {

		if (isLoggingInfo()) {
			logInfo("START:Scheduler started to perform task with job name=["
					+ getJobName() + "]");

		}
		doClearObjectCaches();

		//Clear and re-populate cache for SEARCH_RESULT_CACHE_NAME and Invalidate mobile getAllCateogries cache
		doReInstateAllCategoriesCache();
		

		if (isLoggingInfo()) {
			logInfo("END: Scheduler started to perform task with job name=["
					+ getJobName() + "]");

		}
		
		
	}
	
	public void doClearObjectCaches() {

		if (isEnabled()) {
			if (isLoggingDebug()) {
				logDebug("InvalidateObjectCacheScheduler Is enabled");
				long startTime = System.currentTimeMillis();
				logDebug("InvalidateDropletCacheScheduler start at  :"
						+ startTime);
			}

			cacheNames = BBBConfigRepoUtils.getStringValue(
					OBJECT_CACHE_CONFIG_KEY, INVALIDATE_OBJECT_CACHE_NAMES);
			if (isLoggingDebug()) {
				logDebug("cacheNames: " + cacheNames);
			}

			StringTokenizer st = new StringTokenizer(cacheNames, ",");

			while (st.hasMoreElements()) {
				String cacheName = (String) st.nextElement();
				invalidateObjectCache(cacheName);
			}

			if (isLoggingDebug()) {
				long endTime = System.currentTimeMillis();
				logDebug("InvalidateDropletCacheScheduler end at  :" + endTime);
			}
		} else {
			if (isLoggingDebug()) {
				logDebug("InvalidateObjectCacheScheduler Is disabled");
			}
		}

	}

	
	/**
	 * Clear and re-populate top level categories cache for desktop and mobile both
	 * 
	 * All categories - refer to all L1 categories only 
	 * 
	 * Other reference:
	 * a. performSearch is API that populates L1 categories in SEARCH_RESULT_CACHE_NAME  
	 * 
	 */
	public void doReInstateAllCategoriesCache(){
	
		if (isLoggingDebug()) {
			long startTime = System.currentTimeMillis();
			logDebug(" doReInstateAllCategoriesCache start at  :"	+ startTime);
		}
		
		
		//Check if it is enabled to disable cache:
		
		String invalidateMobileTopCatsCache = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, INVALIDATE_MOBILE_TOP_CATEGORIES_CACHE);
		
		if (isLoggingInfo()) {
			logInfo("doReInstateAllCategoriesCache key value is :" + invalidateMobileTopCatsCache);
		}
		
		//Invalidate if its value is TRUE
		if (BBBUtility.isNotEmpty(invalidateMobileTopCatsCache) && TRUE.equalsIgnoreCase(invalidateMobileTopCatsCache.trim())) {
			
			if (isLoggingInfo()) {
				logInfo("Invalidate " +  SEARCH_RESULT_CACHE_NAME);
			}

			if(this.siteList!=null){
			
				//Clear SEARCH_RESULT_CACHE_NAME - This will invalidate the SEARCH_RESULT_CACHE_NAME for both desktop and mobile
				invalidateObjectCache(SEARCH_RESULT_CACHE_NAME);

				
				boolean isBackendCacheBuild = true;
				
				//re-populate SEARCH_RESULT_CACHE_NAME at backend before invalidating mobile getAllCategories- Cache
				for (String currentSiteId : this.siteList) {

					if(currentSiteId !=null){
						try {
							if (isLoggingInfo()) {
								logInfo("Calling performSearch to create SEARCH_RESULT_CACHE_NAME "
										+ "for top level categories for SiteId " + currentSiteId);
							}
							
							// Querying Search Engine with the populated VO to return Results Object.
							final SearchQuery searchQuery = createHeaderEndecaQuery(currentSiteId);
							
							this.getSearchManager().performSearch(searchQuery);
							
							if (isLoggingInfo()) {
								logInfo("Successfully created SEARCH_RESULT_CACHE_NAME "
										+ "for top level categories for SiteId " + currentSiteId);
							}
							
						} catch (BBBBusinessException e) {
							isBackendCacheBuild = false;
							logError("BBBBusinessException in doReInstateAllCategoriesCache for siteId ="+currentSiteId, e);
						} catch (BBBSystemException e) {
							isBackendCacheBuild = false;
							logError("BBBSystemException in doReInstateAllCategoriesCache for siteId ="+currentSiteId, e);
						}
					}
				}
				
				//if backend cache has been build so we can now disable mobile cache
				if(isBackendCacheBuild){
					//Clear mobile cache for MOBILE_TOP_CATS_RESULT_CACHE_NAME -That contains L1 top categories details
					invalidateObjectCache(MOBILE_ALL_TOP_CATEGORIES_CACHE_NAME);
				}
			}
			

			
		}
		
		if (isLoggingDebug()) {
			long endTime = System.currentTimeMillis();
			logDebug(" doReInstateAllCategoriesCache end at  :" + endTime);
		}
		
	}
	
	/*
	 * Create search query for L1 top categories 
	 * 
	 * Below is query that is made from mobile servers
	 * 
	 * [pagSortOpt=P_Sort_Price,catalogId=0, catalogRefId=, frmBrandPage=false  pagSortOptOrder=1,  pagFilterOpt=10,  pagNum=1, fromCollege=false, keyword=,]
	 * 
	 */
	
	public SearchQuery createHeaderEndecaQuery(String siteId) {

		SearchQuery searchQuery = new SearchQuery();
		Map<String,String> catalogRef = new HashMap<String, String>();
		List<String> pFacets = new ArrayList<String>();
		final SortCriteria sortCriteria = new SortCriteria();

		pFacets = Arrays.asList(this.getSearchManager().getFacets().split(","));
		sortCriteria.setSortFieldName("Date");
		sortCriteria.setSortAscending(false);

		catalogRef.put("catalogId", "0");
		
		//catalogRef.put("catalogRefId", null);
		searchQuery.setFromBrandPage(false);
		
		searchQuery.setSiteId(siteId);
		
		//pageSizeFilter size  
		searchQuery.setPageSize("10");

		searchQuery.setPageNum("1");
		
		searchQuery.setFromCollege(false);
		
		//as not defined in the query
		searchQuery.setHeaderSearch(false);
		
		
		searchQuery.setQueryFacets(pFacets);
		searchQuery.setSortCriteria(sortCriteria);
		searchQuery.setCatalogRef(catalogRef);
		
		return searchQuery;
	}
	
	
	private void invalidateObjectCache(String cacheName) {
		String objectCache = BBBConfigRepoUtils.getStringValue(
				OBJECT_CACHE_CONFIG_KEY, cacheName);
		getObjectCache().clearCache(objectCache);
		logInfo("clearing cache:" + objectCache);
	}

	public BBBObjectCache getObjectCache() {
		return mObjectCache;
	}

	public void setObjectCache(BBBObjectCache mObjectCache) {
		this.mObjectCache = mObjectCache;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the searchManager
	 */
	public SearchManager getSearchManager() {
		return searchManager;
	}

	/**
	 * @param searchManager
	 *            the searchManager to set
	 */
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
}