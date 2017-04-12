package com.bbb.search.endeca.boosting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;
import com.endeca.navigation.UrlGen;

public class OPBSearchBoostingStrategy extends BBBGenericService implements SearchBoostingStrategy {
	private EndecaQueryGenerator queryGenerator;
	private Map<String, String> siteIdMap;
	private EndecaClient endecaClient;
	private EndecaSearchUtil searchUtil;
	private EndecaSearchTools endecaSearchTools;
	private BBBConfigTools catalogTools;

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

	/**
	 * This method is used to get the boosted products for OPB boosting
	 * strategy
	 * 
	 * @param pSearchQuery
	 * @param serachBoostingAlgorithm
	 * @return
	 */
	@Override
	public List<String> getBoostedProducts(SearchQuery pSearchQuery, SearchBoostingAlgorithmVO searchBoostingAlgorithm, boolean l2l3BrandBoostingEnabled) {
		logDebug("Enter :: OPBSearchBoostingStrategy, Search query "+pSearchQuery);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
		List<String> omnitureProductList = new ArrayList<String>();
		List<String> cacheProductList = new ArrayList<String>();
		int recordCount = BBBCoreConstants.ZERO;
		Map<String, String> boostingParams = null;

		if (pSearchQuery == null) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
			return omnitureProductList;
		}

		boostingParams = getEndecaSearchTools().populateBoostedProductParams(searchBoostingAlgorithm, BBBEndecaConstants.OPB_BOOSTING_STRATEGY, pSearchQuery);
		recordCount = Integer.parseInt(boostingParams.get(BBBEndecaConstants.BOOST_ALGORITHM_RECORD_COUNT));
		String searchedKeyword = pSearchQuery.getKeyWord().trim();

		try {
			//checking for auto-correction | BBBI-1743
			
			String autoSuggestedKeyWord = null;
			
			// Used AutoSuggested keyword from EPH lookup
			if(pSearchQuery.isAutoSuggested()){
				autoSuggestedKeyWord =	pSearchQuery.getAutoSuggestedKeyword();
			}
			else{
				autoSuggestedKeyWord =  getSearchUtil().getAutoSuggestedKeyword(pSearchQuery);
			}
			
			
			if(!BBBUtility.isEmpty(autoSuggestedKeyWord)){
				searchedKeyword = autoSuggestedKeyWord.trim();
			}
			//end
			boolean isCacheEnabled = Boolean.valueOf(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBEndecaConstants.OPB_BOOST_STRATEGY_CACHE_ENABLED, BBBCoreConstants.FALSE));
			
			String siteDimId = getSearchUtil().getCatalogId(BBBEndecaConstants.SITE_ID2,getSiteIdMap().get(pSearchQuery.getSiteId()));
			UrlGen urlGen = new UrlGen(BBBEndecaConstants.NAVIGATION+BBBCoreConstants.EQUAL+ siteDimId, getQueryGenerator().getEncoding());
			urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getQueryGenerator().getNtxParamMatchAllExact());
			urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, searchedKeyword.toLowerCase());
			urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, BBBEndecaConstants.NTK_P_OMNITURE_SEARCHTERM);	
			logDebug("OPB product query with search term: " + searchedKeyword + " is " + urlGen.toString() + " and cache enabled: " + isCacheEnabled);
			
			pSearchQuery.appendBoostingLogs("OPBSearchBoostingStrategy: OPB product query with search term: " + searchedKeyword + " is " + urlGen.toString() + " and cache enabled: " + isCacheEnabled);

			/***
			 * If cache is enabled then fetch product list from cache and
			 * return. If not then request to Endeca
			 */
			if (isCacheEnabled && recordCount > 0) {
				cacheProductList = getEndecaSearchTools().getBoostedProductListFromCache(urlGen.toString());
			}
			if (isCacheEnabled && cacheProductList != null) {
				logDebug("Exit :: OPBSearchBoostingStrategy, product list from cache: " + cacheProductList);
				
				pSearchQuery.appendBoostingLogs("OPBSearchBoostingStrategy:: OPBSearchBoostingStrategy, product list from cache: " + cacheProductList);
				
				BBBPerformanceMonitor.end(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
				return cacheProductList;
			} else if (recordCount > 0) {
				logDebug("OPBSearchBoostingStrategy, get product list from Endeca!");
				ENEQuery opbQueryObject = new UrlENEQuery(urlGen.toString(), getQueryGenerator().getEncoding());
				opbQueryObject.setNavNumERecs(getQueryGenerator().getOmnitureRecordCount());
				opbQueryObject.setNavAllRefinements(false);
				ENEQueryResults opbResults = getEndecaClient().executeQuery(opbQueryObject);
				
				if(opbResults != null) {
					Navigation navigation = opbResults.getNavigation();
					if(navigation !=null) {
						ERecList eRecs = navigation.getERecs();
						if(eRecs!=null && eRecs.size() > 0) {
							for (Object eRecObj : eRecs) {
							  ERec eRec = (ERec) eRecObj;
							  PropertyMap properties = eRec.getProperties();
							  String omnitureKeyword = (String)properties.get(BBBEndecaConstants.NTK_P_OMNITURE_SEARCHTERM);
							   if(properties !=null && searchedKeyword.equalsIgnoreCase(omnitureKeyword)) {
								   String productId = (String)properties.get(BBBEndecaConstants.P_OMNITURE_PRODUCT_ID);
								   String[] productIds = productId.split(BBBEndecaConstants.STRING_DEL);
								   for (String prodId : productIds) {
									  omnitureProductList.add(prodId);
								   }
							   }
							}
						}
					}
				}
				/***
				 * Insert Omniture boosted product list into cache
				 */
				if (isCacheEnabled) {
					getEndecaSearchTools().insertBoostedProductsIntoCache(urlGen.toString(), omnitureProductList);
				}
			}
		} catch (BBBBusinessException | BBBSystemException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("Exception while fetching omniture boosted products "+e);
		} catch (UrlENEQueryParseException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("UrlENEQueryParseException while fetching omniture boosted products "+e);
		} catch (ENEQueryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
			logError("ENEQueryException while fetching omniture boosted products "+e);
		}
		
		logDebug("Exit :: OPBSearchBoostingStrategy, product list from Endeca: " + omnitureProductList);
		
		pSearchQuery.appendBoostingLogs("OPBSearchBoostingStrategy::Exit :: OPBSearchBoostingStrategy, product list from Endeca: " + omnitureProductList);
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.OPB_BOOSTING_STRATEGY + "_getBoostedProducts");
		return omnitureProductList;
	}
}

