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

public class FacetSearchBoostingStrategy extends BBBGenericService implements SearchBoostingStrategy {
	private EndecaQueryGenerator queryGenerator;
	private Map<String, String> siteIdMap;
	private EndecaClient endecaClient;
	private EndecaSearchUtil searchUtil;
	private EndecaSearchTools endecaSearchTools;
	private BBBConfigTools catalogTools;

	/**
	 * This method is used to get the boosted products for FACET boosting
	 * strategy
	 * 
	 * @param pSearchQuery
	 * @param serachBoostingAlgorithm
	 * @return
	 */
	@Override
	public List<String> getBoostedProducts(SearchQuery pSearchQuery, SearchBoostingAlgorithmVO searchBoostingAlgorithm, boolean l2l3BrandBoostingEnabled) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
		logDebug("Enter :: FACET - getBoostedProducts, Search query " + pSearchQuery);
		List<String> facetAlgorithmProductList = new ArrayList<String>();
		List<String> cacheProductList = new ArrayList<String>();
		int recordCount = BBBCoreConstants.ZERO;
		int recordCountFromAlgo = BBBCoreConstants.ZERO;
		//ILD-283,281
		int recordCountFromBCC = Integer.parseInt(getSearchUtil().getCatalogTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.FACET_BOOSTING_DEFAULT_PRODUCTION_COUNT, BBBCoreConstants.FIFTY_STRING));
		String dimensionIdFromAlgorithm = null;
		Map<String, String> boostingParams = null;

		if (pSearchQuery == null) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
			return facetAlgorithmProductList;
		}

		boostingParams = getEndecaSearchTools().populateBoostedProductParams(searchBoostingAlgorithm, BBBEndecaConstants.FACET_BOOSTING_STRATEGY,pSearchQuery);
		recordCountFromAlgo = Integer.parseInt(boostingParams.get(BBBEndecaConstants.BOOST_ALGORITHM_RECORD_COUNT));
		dimensionIdFromAlgorithm = boostingParams.get(BBBEndecaConstants.BOOST_ALGORITHM_ENDECA_PROPERTY);
	
		List<String> navigationUrl = new ArrayList<String>();
		//ILD-283,281 - fetching max boosted products from endeca
		recordCount = Math.max(recordCountFromBCC, recordCountFromAlgo);
		logDebug("Product count from SortBoostingDefaultProductCount config key :" + recordCountFromBCC + "Total Product Count as per the Algorithm" + recordCountFromAlgo);
		try {
			boolean isCacheEnabled = Boolean.valueOf(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBEndecaConstants.FACET_BOOST_STRATEGY_CACHE_ENABLED, BBBCoreConstants.FALSE));

			String siteId = getSearchUtil().getCatalogId(BBBEndecaConstants.SITE_ID, getSiteIdMap().get(pSearchQuery.getSiteId()));
			String productRecTypeDimID = getSearchUtil().getCatalogId(BBBEndecaConstants.RECORD_TYPE, BBBEndecaConstants.PROD_RECORD_TYPE);
			navigationUrl.add(siteId);
			navigationUrl.add(productRecTypeDimID);
	
			final String facetQuery = getEndecaSearchTools().getFacetQuery(pSearchQuery, dimensionIdFromAlgorithm, navigationUrl, l2l3BrandBoostingEnabled);
			logDebug("Facet product query for search term/Brand ID[If search term is NULL then its for L2L3/Brand]: " + pSearchQuery.getKeyWord() + " is " + facetQuery + " and cache enabled: " + isCacheEnabled);
			pSearchQuery.appendBoostingLogs("FacetSearchBoostingStrategy:Facet product query for search term/Brand ID[If search term is NULL then its for L2L3/Brand]: " + pSearchQuery.getKeyWord() + " is " + facetQuery + " and cache enabled: " + isCacheEnabled);
			/***
			 * If cache is enabled then fetch product list from cache and
			 * return. If not then request to Endeca
			 */
			if (isCacheEnabled && recordCountFromAlgo > 0) {
				logDebug("FacetSearchBoostingStrategy.getBoostedProducts, facetQuery searched in cache:"+ facetQuery);
				cacheProductList = getEndecaSearchTools().getBoostedProductListFromCache(facetQuery);
			}
			if (isCacheEnabled && cacheProductList != null) {
				logDebug("Exit :: FACET_BOOSTING_STRATEGY, product list from cache: " + cacheProductList.subList(0, Math.min(cacheProductList.size(), recordCountFromAlgo)));;
				pSearchQuery.appendBoostingLogs("FacetSearchBoostingStrategy: FACET_BOOSTING_STRATEGY, product list from cache: " + cacheProductList.subList(0, Math.min(cacheProductList.size(), recordCountFromAlgo)));
				BBBPerformanceMonitor.end(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
				return cacheProductList.subList(0, Math.min(cacheProductList.size(), recordCountFromAlgo));
			} else if (recordCountFromAlgo > 0) {
				logDebug("FacetSearchBoostingStrategy, get product list from Endeca!");
				logDebug("Total number of products to be fetched from endeca :"+ recordCount);
				ENEQuery nbpQueryObject = new UrlENEQuery(facetQuery, getQueryGenerator().getEncoding());
				if (pSearchQuery.getNavRecordStructureExpr() != null && !StringUtils.isEmpty(pSearchQuery.getNavRecordStructureExpr())) {
					logDebug("FacetSearchBoostingStrategy, Negative Filters are: " + pSearchQuery.getNavRecordStructureExpr());
					nbpQueryObject.setNavRecordStructureExpr(pSearchQuery.getNavRecordStructureExpr());
				}
	
				// This would be driven from config key
				nbpQueryObject.setNavNumERecs(recordCount);
				FieldList fList = new FieldList();
	
				// Add an Endeca property to the list
				fList.addField(BBBEndecaConstants.FEATURED_PRODUCT_ID);
				nbpQueryObject.setSelection(fList);
				ENEQueryResults opbResults = getEndecaClient().executeQuery(nbpQueryObject);
				facetAlgorithmProductList = getEndecaSearchTools().getBoostedProductList(opbResults, facetAlgorithmProductList,pSearchQuery);
	
				/***
				 * Insert FACET boosted product list into cache
				 */
				if (isCacheEnabled && facetAlgorithmProductList != null) {
					logDebug("FacetSearchBoostingStrategy.getBoostedProducts, facetQuery stored in cache:"+ facetQuery);
					getEndecaSearchTools().insertBoostedProductsIntoCache(facetQuery, facetAlgorithmProductList);
				}
			}

		} catch (BBBBusinessException | BBBSystemException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("FACET search boosting strategy - Exception occurred while fetching dimension Ids: " + e);
		} catch (UrlENEQueryParseException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("UrlENEQueryParseException while fetching facet boosted products "
					+ e);
		} catch (ENEQueryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("ENEQueryException while fetching facet boosted products "
					+ e);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.FACET_BOOSTING_STRATEGY + "_getBoostedProducts");
		if(!BBBUtility.isListEmpty(facetAlgorithmProductList)){
			logDebug("Exit :: FACET - getBoostedProducts, facetAlgorithmProductList from Endeca: " + facetAlgorithmProductList.subList(0, Math.min(facetAlgorithmProductList.size(), recordCountFromAlgo)));
			
			pSearchQuery.appendBoostingLogs("FacetSearchBoostingStrategy: FACET - getBoostedProducts, facetAlgorithmProductList from Endeca: " + facetAlgorithmProductList.subList(0, Math.min(facetAlgorithmProductList.size(), recordCountFromAlgo)));
			
			return facetAlgorithmProductList.subList(0, Math.min(facetAlgorithmProductList.size(), recordCountFromAlgo));
		} else {
			logDebug("Exit :: FACET - getBoostedProducts, facetAlgorithmProductList from Endeca in null");
			
			pSearchQuery.appendBoostingLogs("FacetSearchBoostingStrategy: FACET - getBoostedProducts, facetAlgorithmProductList from Endeca is null/empty");

			return facetAlgorithmProductList;
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
