package com.bbb.search.endeca.boosting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaClient;
import com.bbb.search.endeca.EndecaQueryGenerator;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.tools.EndecaSearchTools;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.bbb.utils.BBBUtility;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.FieldList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;

public class SortSearchBoostingStrategy extends BBBGenericService implements SearchBoostingStrategy {
	private EndecaQueryGenerator queryGenerator;
	private Map<String, String> siteIdMap;
	private EndecaClient endecaClient;
	private EndecaSearchUtil searchUtil;
	private EndecaSearchTools endecaSearchTools;
	private BBBConfigTools catalogTools;

	/**
	 * This method is used to get the boosted products for SORT boosting
	 * strategy
	 * 
	 * @param pSearchQuery
	 * @param serachBoostingAlgorithm
	 * @return
	 */
	@Override
	public List<String> getBoostedProducts(SearchQuery pSearchQuery, SearchBoostingAlgorithmVO searchBoostingAlgorithm, boolean l2l3BrandBoostingEnabled) {
		logDebug("SORT_BOOSTING_STRATEGY, Search query " + pSearchQuery);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
		List<String> sortProdList = new ArrayList<String>();
		List<String> cacheProductList = new ArrayList<String>();
		int recordCountFromAlgo = BBBCoreConstants.ZERO;
		int recordCount = BBBCoreConstants.ZERO;
		//ILD-283,281
		int recordCountFromBCC = Integer.parseInt(getSearchUtil().getCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.SORT_BOOSTING_DEFAULT_PRODUCTION_COUNT, BBBCoreConstants.FIFTY_STRING));
		String sortField = null;

		if (pSearchQuery == null) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
			return sortProdList;
		}

		Map<String, String> boostingParams = getEndecaSearchTools().populateBoostedProductParams(searchBoostingAlgorithm, BBBEndecaConstants.SORT_BOOSTING_STRATEGY,pSearchQuery);
		recordCountFromAlgo = Integer.parseInt(boostingParams.get(BBBEndecaConstants.BOOST_ALGORITHM_RECORD_COUNT));
		sortField = boostingParams.get(BBBEndecaConstants.BOOST_ALGORITHM_ENDECA_PROPERTY);
		List<String> navigationUrl = new ArrayList<String>();
		//ILD-283,281 - fetching max boosted products from endeca
		recordCount = Math.max(recordCountFromBCC, recordCountFromAlgo);
		logDebug("Product count from SortBoostingDefaultProductCount config key :" + recordCountFromBCC + "Total Product Count as per the Algorithm" + recordCountFromAlgo);
		try {
			boolean isCacheEnabled = Boolean.valueOf(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBEndecaConstants.SORT_BOOST_STRATEGY_CACHE_ENABLED, BBBCoreConstants.FALSE));

			String siteId = getSearchUtil().getCatalogId(BBBEndecaConstants.SITE_ID, getSiteIdMap().get(pSearchQuery.getSiteId()));
			String productRecTypeDimID = getSearchUtil().getCatalogId(BBBEndecaConstants.RECORD_TYPE, BBBEndecaConstants.PROD_RECORD_TYPE);
			navigationUrl.add(siteId);
			navigationUrl.add(productRecTypeDimID);
			String sortQuery = getEndecaSearchTools().getSortQuery(pSearchQuery, sortField, navigationUrl, l2l3BrandBoostingEnabled);
			logDebug("SortSearchBoostingStrategy - product query for search term/Brand ID[If search term is NULL then its for L2L3]: " + pSearchQuery.getKeyWord() + " is " + sortQuery + " and cache enabled: " + isCacheEnabled);
			pSearchQuery.appendBoostingLogs("SortSearchBoostingStrategy - product query for search term/Brand ID[If search term is NULL then its for L2L3]: " + pSearchQuery.getKeyWord() + " is " + sortQuery + " and cache enabled: " + isCacheEnabled);
			/***
			 * If cache is enabled then fetch product list from cache and
			 * return. If not then request to Endeca
			 */
			if (isCacheEnabled && recordCountFromAlgo > 0) {
				logDebug("SORT_BOOSTING_STRATEGY, sortQuery searched in cache:"+ sortQuery);
				cacheProductList = getEndecaSearchTools().getBoostedProductListFromCache(sortQuery);
			}
			if (isCacheEnabled && cacheProductList != null) {
				logDebug("Exit :: SORT_BOOSTING_STRATEGY, product list from cache: " + cacheProductList.subList(0, Math.min(cacheProductList.size(), recordCountFromAlgo)));
				
				pSearchQuery.appendBoostingLogs("SortSearchBoostingStrategy Exit :: SORT_BOOSTING_STRATEGY, product list from cache: " + cacheProductList.subList(0, Math.min(cacheProductList.size(), recordCountFromAlgo)));
				
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
				return cacheProductList.subList(0, Math.min(cacheProductList.size(), recordCountFromAlgo));
			} else if (recordCountFromAlgo > 0) {
				logDebug("SortSearchBoostingStrategy, get product list from Endeca!");
				logDebug("Total number of products to be fetched from endeca :"+ recordCount);
				ENEQuery nbpQueryObject = new UrlENEQuery(sortQuery, getQueryGenerator().getEncoding());
				if (pSearchQuery.getNavRecordStructureExpr() != null && !StringUtils.isEmpty(pSearchQuery.getNavRecordStructureExpr())) {
					logDebug("SortSearchBoostingStrategy, Negative Filters are: " + pSearchQuery.getNavRecordStructureExpr());
					
					pSearchQuery.appendBoostingLogs("SortSearchBoostingStrategy, Negative Filters are: " + pSearchQuery.getNavRecordStructureExpr());
					
					nbpQueryObject.setNavRecordStructureExpr(pSearchQuery.getNavRecordStructureExpr());
				}
				// This would be driven from config key
				nbpQueryObject.setNavNumERecs(recordCount);
				FieldList fList = new FieldList();
	
				// Add an Endeca property to the list
				fList.addField(BBBEndecaConstants.FEATURED_PRODUCT_ID);
				nbpQueryObject.setSelection(fList);
				ENEQueryResults opbResults = getEndecaClient().executeQuery(nbpQueryObject);
				sortProdList = getEndecaSearchTools().getBoostedProductList(opbResults, sortProdList,pSearchQuery);
				/***
				 * Insert SORT boosted product list into cache
				 */
				if (isCacheEnabled && sortProdList != null) {
					logDebug("SORT_BOOSTING_STRATEGY, sortQuery added in cache:"+ sortQuery);
					getEndecaSearchTools().insertBoostedProductsIntoCache(sortQuery, sortProdList);
				}
			}

		} catch (BBBBusinessException | BBBSystemException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("SORT search boosting strategy - Exception occurred while fetching dimension Ids: " + e);
		} catch (UrlENEQueryParseException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("UrlENEQueryParseException while fetching Sort boosted products " + e);
		} catch (ENEQueryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("ENEQueryException while fetching Sort boosted products " + e);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.SORT_BOOSTING_STRATEGY + "_getBoostedProducts");
		
		if(!BBBUtility.isListEmpty(sortProdList)) {
		
		pSearchQuery.appendBoostingLogs("SortSearchBoostingStrategy Exit :: SORT_BOOSTING_STRATEGY, product list from Endeca: " + sortProdList.subList(0, Math.min(sortProdList.size(), recordCountFromAlgo)));
		logDebug("Exit :: SORT_BOOSTING_STRATEGY, product list from Endeca: " + sortProdList.subList(0, Math.min(sortProdList.size(), recordCountFromAlgo)));
		return sortProdList.subList(0, Math.min(sortProdList.size(), recordCountFromAlgo));
		} else {
			pSearchQuery.appendBoostingLogs("SortSearchBoostingStrategy Exit :: SORT_BOOSTING_STRATEGY, product list from Endeca is null/empty");
			logDebug("Exit :: SORT_BOOSTING_STRATEGY, product list from Endeca is null");
			return sortProdList;
		}
		
	}

	/**
	 * @return the searchUtil
	 */
	public EndecaSearchUtil getSearchUtil() {
		return searchUtil;
	}

	/**
	 * @param searchUtil the searchUtil to set
	 */
	public void setSearchUtil(EndecaSearchUtil searchUtil) {
		this.searchUtil = searchUtil;
	}

	/**
	 * @return the endecaClient
	 */
	public EndecaClient getEndecaClient() {
		return this.endecaClient;
	}

	/**
	 * @param endecaClient the endecaClient to set
	 */
	public void setEndecaClient(final EndecaClient endecaClient) {
		this.endecaClient = endecaClient;
	}

	/**
	 * @return the siteIdMap
	 */
	public Map<String, String> getSiteIdMap() {
		return this.siteIdMap;
	}

	/**
	 * @param siteIdMap the siteIdMap to set
	 */
	public void setSiteIdMap(final Map<String, String> siteIdMap) {
		this.siteIdMap = siteIdMap;
	}
	
	/**
	 * @return the queryGenerator
	 */
	public EndecaQueryGenerator getQueryGenerator() {
		return this.queryGenerator;
	}

	/**
	 * @param queryGenerator the queryGenerator to set
	 */
	public void setQueryGenerator(final EndecaQueryGenerator queryGenerator) {
		this.queryGenerator = queryGenerator;
	}

	/**
	 * @return the endecaSearchTools
	 */
	public EndecaSearchTools getEndecaSearchTools() {
		return endecaSearchTools;
	}

	/**
	 * @param endecaSearchTools the endecaSearchTools to set
	 */
	public void setEndecaSearchTools(EndecaSearchTools endecaSearchTools) {
		this.endecaSearchTools = endecaSearchTools;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBConfigTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBConfigTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
