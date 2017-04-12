package com.bbb.search.endeca;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import atg.multisite.SiteContextManager;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.vo.BBBDimVal;
import com.bbb.search.endeca.vo.BBBDimension;
import com.bbb.search.endeca.vo.BBBQueryResults;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.UrlENEQuery;

public class TBSEndecaSearchUtil extends EndecaSearchUtil {

	private static final String NAVIGATION = "N";
	private static final String GETTING_CACHE_TIMEOUT_FOR="getting cacheTimeout for ";

	/**
	 * Get DimValueId of a dimension
	 * 
	 * @param parentName
	 * @param dimName
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getCatalogId(final String parentName, final String dimName) throws BBBBusinessException, BBBSystemException{
		String dimensionId = null;
		String cacheKeyForEndeca;
		//ENEQueryResults results = null;
		//DimVal childDimVal;
		//DimValList childList;

		logDebug("Entering EndecaSearch.getCatalogId method.");

		final String methodName = BBBCoreConstants.ENDECA_CATALOG_ID_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,methodName);

		String cacheName = null;
		int cacheTimeout = 0;
		ENEQuery endecaQuery = null;
		String channelId = null;
		String siteId = SiteContextManager.getCurrentSiteId();
		if( siteId.startsWith("TBS_")) {
			siteId = siteId.substring(4);
		}

		try {

			logDebug("Added new condition for Search by Brand");

			if(null != ServletUtil.getCurrentRequest()){
				channelId = ServletUtil.getCurrentRequest().getHeader("X-bbb-channel");
				if(BBBUtility.isNotEmpty(channelId)){
					logDebug("Channel Id "+channelId);
				}
			}

			if(parentName != null && parentName.equalsIgnoreCase("Brand")){
				final String siteDimId = getCatalogId("Site_ID",getSiteIdMap().get(siteId));
				endecaQuery = new UrlENEQuery(NAVIGATION+"="+siteDimId,getEndecaClient().getEncoding());
				cacheKeyForEndeca = NAVIGATION+"="+siteDimId;
				logDebug("Search is by brand. So Endeca query is : "+endecaQuery);
			} else {
				endecaQuery = new UrlENEQuery(NAVIGATION+"=0",getEndecaClient().getEncoding());
				cacheKeyForEndeca = NAVIGATION+"=0";
				logDebug("Search is not by brand. So endeca query is : "+endecaQuery);
			}

			endecaQuery.setNavAllRefinements(true);
			if(BBBUtility.isNotEmpty(channelId)){		
				cacheKeyForEndeca = cacheKeyForEndeca + "AllNavRef_" + channelId + "_" + siteId;
			}else {
				cacheKeyForEndeca = cacheKeyForEndeca + "AllNavRef_" + siteId;
			}
			logDebug("cache key:" + BBBCoreConstants.PRODUCT_DIM_ID_CACHE_NAME);

			cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.PRODUCT_DIM_ID_CACHE_NAME);
			try {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.PRODUCT_DIM_ID_CACHE_TIMEOUT ));
			} catch (NumberFormatException e) {
				logError("EndecaSearch.getCatalogId || NumberFormatException occured in " +
						GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.PRODUCT_DIM_ID_CACHE_TIMEOUT, e);
				cacheTimeout = getProductCacheTimeout();
			} catch (NullPointerException exc) {
				logError("EndecaSearch.getCatalogId || NullPointerException occured in " +
						GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.PRODUCT_DIM_ID_CACHE_TIMEOUT, exc);
				cacheTimeout = getProductCacheTimeout();
			}
			logDebug("cacheName:" + cacheName);
			if(BBBUtility.isNotEmpty(channelId)){
				logDebug("EndecaSearch.getCatalogId - Looking into " + cacheName +  " Cache for the value for key: " + (parentName+"+"+dimName+"+"+channelId+"+"+siteId));
				dimensionId = (String) getObjectCache().get(parentName+"+"+dimName+"+"+channelId+"+"+siteId, cacheName);
			}else {
				logDebug("EndecaSearch.getCatalogId - Looking into " + cacheName +  " Cache for the value for key: " + (parentName+"+"+dimName+"+"+siteId));
				dimensionId = (String) getObjectCache().get(parentName+"+"+dimName+"+"+siteId, cacheName);
			}

			if(dimensionId == null){
				logDebug("EndecaSearch:: Dimension Id for Product does not exist in cache. Querying Endeca to get the same.");
				
				//R2.2 Brand Performance Issue Fixed. Getting Results from Cache : Start
				logDebug("Looking for Base Query results into " + cacheName +  " Cache for the value for key: " + cacheKeyForEndeca);
				BBBQueryResults queryResults = (BBBQueryResults) getObjectCache().get(cacheKeyForEndeca, cacheName);
				if(queryResults == null){
					logDebug("EndecaSearch:: Endeca Results does not exist in cache. Querying Endeca to get the same.");
					queryResults = this.getEndecaResults(cacheKeyForEndeca, endecaQuery, cacheName, cacheTimeout);
				}
				
				if(queryResults != null && queryResults.isContainsNavigation()){
					List<BBBDimension> dimList = queryResults.getDimList();
					final Iterator dimIterator =  dimList.listIterator();
					while(dimIterator.hasNext()){
						final BBBDimension rootDim = (BBBDimension) dimIterator.next();
						if(null != parentName && parentName.equalsIgnoreCase(rootDim.getRootDimName())){
							//if dimName is null return root dimension's Id
							if(null == dimName) {
								dimensionId = rootDim.getRootDimId();
								break;
							} else {
								List<BBBDimVal> dimValList = rootDim.getDimValList();
								//childList = rootDim.getRefinements();
								final ListIterator childIter = dimValList.listIterator();
								while(childIter.hasNext()){
									BBBDimVal childDimVal = (BBBDimVal) childIter.next();
									if(dimName.equalsIgnoreCase(childDimVal.getDimValName())){
										dimensionId = String.valueOf(childDimVal.getDimValId());
										if(BBBUtility.isNotEmpty(channelId)){
											getObjectCache().put(parentName+"+"+dimName+"+"+channelId+"+"+siteId, dimensionId, cacheName, cacheTimeout);
										}else {
											getObjectCache().put(parentName+"+"+dimName+"+"+siteId, dimensionId, cacheName, cacheTimeout);
										}
									}
								}
							}
						}
					}
				}
				//R2.2 Brand Performance Issue Fixed. Getting Results from Cache : End
			}
			else{
				logDebug("Dimension Id for Product fetched from cache");
			}
		} catch (ENEQueryException endecaException) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQueryException in calling Endeca performSearch");
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery Exception ", endecaException);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
		logDebug("Product Dimension Id: " +dimensionId);
		logDebug("Exit EndecaSearch.getCatalogId method.");
		return dimensionId;
	}

}
