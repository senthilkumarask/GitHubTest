package com.bbb.search.endeca.tools;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaQueryGenerator;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.vo.AlgorithmComponentVO;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;


public class EndecaSearchToolsImpl extends BBBGenericService implements EndecaSearchTools {
	private Repository searchBoostRepository;
	private String siteSpectQuery;
	private String CLS = "EndecaSearchToolsImpl";
	private BBBConfigTools catalogTools;
	private Map<String, String> pageTypeMap;
	private BBBObjectCache objectCache;
	private BBBLocalCacheContainer localCacheContainer;
	private final String DEFAULT = "DEFAULT";
	private final String SEARCH = "SEARCH";
	private final String L2L3 = "L2L3";
	private final String BRAND = "BRAND";
	private final String COLLEGE = "COLLEGE";
	private EndecaQueryGenerator queryGenerator;
	private EndecaSearch endecaSearch;

	/**
	 * @return the localCacheContainer
	 */
	public BBBLocalCacheContainer getLocalCacheContainer() {
		return localCacheContainer;
	}

	/**
	 * @param localCacheContainer the localCacheContainer to set
	 */
	public void setLocalCacheContainer(BBBLocalCacheContainer localCacheContainer) {
		this.localCacheContainer = localCacheContainer;
	}
	
	public EndecaQueryGenerator getQueryGenerator() {
		return this.queryGenerator;
	}

	/**
	 * @param queryGenerator the queryGenerator to set
	 */
	public void setQueryGenerator(final EndecaQueryGenerator queryGenerator) {
		this.queryGenerator = queryGenerator;
	}
	
	public EndecaSearch getEndecaSearch() {
		return endecaSearch;
	}

	public void setEndecaSearch(EndecaSearch endecaSearch) {
		this.endecaSearch = endecaSearch;
	}
	
	/**
	 * This method used to get the search boosting algorithms from the Search
	 * Boost repository for the requested boostCode. This boostCode is provided
	 * by the SiteSpect in request.
	 * 
	 * @param boostCode
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SearchBoostingAlgorithmVO getSearchBoostingAlgorithms(final Map<String, String> searchAlgorithmParams) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSearchBoostingAlgorithms");
		SearchBoostingAlgorithmVO algorithmVO = null;
		List<AlgorithmComponentVO> algorithmVoList = null;
		AlgorithmComponentVO algorithmComponentVO = null;
		Set<String> siteIds = null;
		RqlStatement statement = null;
		if (BBBUtility.isMapNullOrEmpty(searchAlgorithmParams)) {
			logDebug(CLS + " [getSearchBoostingAlgorithms] - searchAlgorithmParams are NULL or EMPTY.");
			return algorithmVO;
		}
		logDebug(CLS + " [getSearchBoostingAlgorithms] starts with boostCode: " + searchAlgorithmParams.get(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE));
		try {
			statement = RqlStatement.parseRqlStatement(getSiteSpectQuery());
			RepositoryView view = getSearchBoostRepository().getView(BBBEndecaConstants.SEARCH_BOOST_REPOSITORY_VIEW);
			Object[] params = new Object[3];
			params[0] = searchAlgorithmParams.get(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE);
			params[1] = searchAlgorithmParams.get(BBBCoreConstants.PAGE_NAME);
			params[2] = searchAlgorithmParams.get(BBBCoreConstants.SITE_ID);
			RepositoryItem[] items = statement.executeQuery(view, params);
			algorithmVoList = new ArrayList<AlgorithmComponentVO>();
			if (items != null && items.length > 0) {				
				for (RepositoryItem item : items) {
					algorithmVO = new SearchBoostingAlgorithmVO();
					siteIds = new HashSet<String>();
					algorithmVO.setEntryId(((String) item.getRepositoryId()));
					algorithmVO.setSiteSpectCode((String) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE));
					algorithmVO.setPageType(item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_PAGE_TYPE) != null ? (Integer) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_PAGE_TYPE) : 0);
					algorithmVO.setOmnitureEventRequired((Boolean) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_OMNITURE_EVT_REQ));
					algorithmVO.setRandomizationRequired((boolean) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_RANDOMIZATION_REQ));
					siteIds.addAll((Set<String>) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_SITES));
					algorithmVO.setSites(siteIds);
					algorithmVO.setLastModifiedDate((Timestamp) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_LAST_MOD_DATE));
					List algoid = (List) item.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_ID);
					String algorithmDescription = "";
					for (Object object : algoid) {
						RepositoryItem algoRepoItem = (RepositoryItem) object;
						algorithmComponentVO = new AlgorithmComponentVO();
						algorithmComponentVO.setAlgorithmId((String) algoRepoItem.getRepositoryId());
						algorithmComponentVO.setAlgorithmName((String) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_NAME));
						algorithmComponentVO.setPercentage((Integer) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_PERCENTAGE));
						algorithmComponentVO.setAlgorithmType((Integer) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_TYPE));
						algorithmComponentVO.setAlgorithmDescription((String) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_DESCRIPTION));
						if(null!=algorithmComponentVO.getAlgorithmDescription()){
							algorithmDescription += BBBCoreConstants.HASH +algorithmComponentVO.getAlgorithmDescription(); 
						}
						algorithmComponentVO.setEndecaProperty((String) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_ENDECA_PROPERTY));
						algorithmComponentVO.setLastModifiedDate((Timestamp) algoRepoItem.getPropertyValue(BBBEndecaConstants.BOOST_ALGORITHM_LAST_MOD_DATE));
						algorithmVoList.add(algorithmComponentVO);
					}
					algorithmVO.setOmnitureDescription(algorithmDescription);
					algorithmVO.setAlgorithmComponents(algorithmVoList);
				}
			}

		} catch (RepositoryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSearchBoostingAlgorithms");
			this.logError("EndecaSearchToolsImpl [getSearchBoostingAlgorithms] - RepositoryException");
		}
		logDebug(CLS + " [getSearchBoostingAlgorithms] ends.");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSearchBoostingAlgorithms");
		return algorithmVO;
	}

	/**
	 * This method is used to calculate the total boosted products based on the
	 * percentage from algorithm - derived by the Business
	 * 
	 * @param searchBoostingAlgorithm
	 * @param boostingStrategy
	 * @return
	 */
	public Map<String, String> populateBoostedProductParams(SearchBoostingAlgorithmVO searchBoostingAlgorithm, String boostingStrategy,SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_populateBoostedProductParams");
		logDebug(CLS + " - populateBoostedProductParams starts for boostingStrategy : " + boostingStrategy);
		final Map<String, Double> boostPercentageMap = searchBoostingAlgorithm.getBoostPercentageMap();
		final int totalBoostedProdCount = searchBoostingAlgorithm.getTotalBoostedProducts();
		logDebug(CLS + " - populateBoostedProductParams starts for boostPercentageMap : " + boostPercentageMap + " and totalBoostedProdCount: " + totalBoostedProdCount);
		Map<String, String> boostingParams = new HashMap<String, String>();
		int recordCount = 0;
		String endecaProperty = null;
		if (!BBBUtility.isEmpty(boostingStrategy) && searchBoostingAlgorithm != null && !BBBUtility.isListEmpty(searchBoostingAlgorithm.getAlgorithmComponents())) {
			for (AlgorithmComponentVO algorithmComponent : searchBoostingAlgorithm.getAlgorithmComponents()) {
				if (boostingStrategy.equalsIgnoreCase(getBoostingType(algorithmComponent.getAlgorithmType(),pSearchQuery))) {
					recordCount = (int) Math.ceil(totalBoostedProdCount * boostPercentageMap.get(boostingStrategy) / BBBCoreConstants.HUNDRED);
					endecaProperty = algorithmComponent.getEndecaProperty();
					break;
				}
			}
		}
		boostingParams.put(BBBEndecaConstants.BOOST_ALGORITHM_RECORD_COUNT, String.valueOf(recordCount));
		boostingParams.put(BBBEndecaConstants.BOOST_ALGORITHM_ENDECA_PROPERTY,  endecaProperty);

		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_populateBoostedProductParams");
		
		pSearchQuery.appendBoostingLogs("populateBoostedProductParams boostingStrategy"+boostingStrategy+"boostPercentageMap : " + boostPercentageMap + " and totalBoostedProdCount: " + totalBoostedProdCount+" boostingParams:"+boostingParams);
		logDebug(CLS + " - populateBoostedProductParams ends for boostingParams : " + boostingParams);
		return boostingParams;
	}

	/**
	 * This method is used to fetch respective boosting percentage of each
	 * boosting strategy
	 * 
	 * @param searchBoostingAlgorithm
	 * @return
	 */
	public Map<String, Double> getBoostingPercentage(SearchBoostingAlgorithmVO searchBoostingAlgorithm,SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostingPercentage");
		logDebug(CLS + " - getBoostingPercentage starts for searchBoostingAlgorithm : " + searchBoostingAlgorithm);
		Map<String, Double> boostingPercentage = new LinkedHashMap<String, Double>();
		if (searchBoostingAlgorithm != null && !BBBUtility.isListEmpty(searchBoostingAlgorithm.getAlgorithmComponents())) {
			for (AlgorithmComponentVO algorithmComponent : searchBoostingAlgorithm.getAlgorithmComponents()) {
				int algoPercentage = algorithmComponent.getPercentage() != null ? algorithmComponent.getPercentage() : BBBCoreConstants.ZERO;
				Double percentage = new Double(algoPercentage);
				boostingPercentage.put(getBoostingType(algorithmComponent.getAlgorithmType(),pSearchQuery), percentage);
			}
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostingPercentage");
		
		logDebug(CLS + " - getBoostingPercentage ends for boostingPercentage : " + boostingPercentage);
		
		pSearchQuery.appendBoostingLogs("getBoostingPercentage boostingPercentage:"+boostingPercentage);
		return boostingPercentage;
	}

	/**
	 * This method used to get the total number of boosted products
	 * 
	 * @param searchBoostingAlgorithm
	 * @param opbCount
	 * @return
	 */
	public int getTotalNumberOfBoostedProducts(SearchBoostingAlgorithmVO searchBoostingAlgorithm, int opbCount,SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getTotalNumberOfBoostedProducts");
		final Map<String, Double> percentageMap = searchBoostingAlgorithm.getBoostPercentageMap();
		logDebug(CLS + " - getTotalNumberOfBoostedProducts starts for opbCount : " + opbCount + " and boostPercentage map: " + percentageMap);
		int totalBoostedProductsCount = BBBCoreConstants.ZERO;
		// If OPB count is ZERO or no percentage defined for the selected algorithm
		if (opbCount == BBBCoreConstants.ZERO || BBBUtility.isMapNullOrEmpty(percentageMap)) {
			totalBoostedProductsCount = Integer.parseInt(getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.TOTAL_BOOSTED_PRODUCTS_COUNT, String.valueOf(BBBCoreConstants.ZERO)));
			logDebug(CLS + " - getTotalNumberOfBoostedProducts : Value of config key [BoostedProductsForZeroOBP] is : " + totalBoostedProductsCount);
		} else if (searchBoostingAlgorithm.getBoostPercentageMap().get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY) != null
				&& searchBoostingAlgorithm.getBoostPercentageMap().get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY).intValue() == BBBCoreConstants.HUNDRED) {
			// If OPB percentage is 100, boost ONLY OPB products.
			totalBoostedProductsCount = totalBoostedProductsCount + opbCount;
		} else {
			for (Entry<String, Double> entry : percentageMap.entrySet()) {
				if (BBBEndecaConstants.OPB_BOOSTING_STRATEGY.equalsIgnoreCase(entry.getKey())) {
					totalBoostedProductsCount = totalBoostedProductsCount + opbCount;
				} else {
					totalBoostedProductsCount = totalBoostedProductsCount 
							+ (int) Math.ceil(opbCount * entry.getValue() / percentageMap.get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY));
				}
			}
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getTotalNumberOfBoostedProducts");
		logDebug(CLS + " - getTotalNumberOfBoostedProducts ends for totalBoostedProductsCount : " + totalBoostedProductsCount);
		
		pSearchQuery.appendBoostingLogs("getTotalNumberOfBoostedProducts totalBoostedProductsCount:"+totalBoostedProductsCount);
		
		return totalBoostedProductsCount;
	}

	/**
	 * This method used to get the updated endeca query for FACET boosting
	 * strategy
	 * 
	 * @param searchQuery
	 * @param dimIdFromAlgorithm
	 * @param navigationUrl
	 * @return
	 */
	public String getFacetQuery(SearchQuery pSearchQuery, String dimIdFromAlgorithm, List<String> navigationUrl, boolean l2l3BrandBoostingEnabled) {
		logDebug(CLS + " - [getFacetQuery] starts with searchQuery: " + pSearchQuery.getQueryURL() + " and dimIdFromAlgorithm: " + dimIdFromAlgorithm);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getFacetQuery");
		//ILD-70 | Handling extra calls to endeca with SWS
		String searchQuery = fetchSearchQueryURL(pSearchQuery);
		if(BBBUtility.isEmpty(searchQuery)){
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getFacetQuery");
			logDebug(CLS + " - [getFacetQuery] end. EndecaSearchQuery is empty");
			return BBBCoreConstants.BLANK;
		}
		String[] queryParams = searchQuery.split(BBBCoreConstants.AMPERSAND);
		String facetDimId = BBBEndecaConstants.NAVIGATION + BBBCoreConstants.EQUAL;
		String navigationDimId = null;
		String recordOffsetToBeRemoved = null;
		int count = 0;
		boolean navigationExist = false;
		String navRefinementId = "";
		String navKeyWordToBeUpdated = "";
		String updatedNavKeyWord = "";
		String navInventoryStatusId = null;
		boolean removeOOSBoostedProduct;
		
		if (l2l3BrandBoostingEnabled){
			try {
			if(pSearchQuery.isFromBrandPage()) {
				removeOOSBoostedProduct =  Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.REMOVE_OOS_BOOSTED_PRODUCT_BRAND));
			} else {
				removeOOSBoostedProduct =  Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.REMOVE_OOS_BOOSTED_PRODUCT_CATEGORY));
			}
			
			if(removeOOSBoostedProduct) {
				navInventoryStatusId = getEndecaSearch().getCatalogId(BBBEndecaConstants.INVENTORY_STATUS,BBBEndecaConstants.POSITIVE);
			}
			} catch (BBBBusinessException e) {
				logError("Business Exception while fetching dimension id ",e);
			} catch (BBBSystemException e) {
				logError("System Exception while fetching dimension id ",e);
			}
		}
		//fetching catagory id | BBBI-3656
		if(BBBUtility.isEmpty(pSearchQuery.getKeyWord())){
			for (String param : queryParams) {
				if(BBBUtility.isEmpty(pSearchQuery.getKeyWord()) && param.contains(BBBEndecaConstants.NAV_REFINEMENT + BBBCoreConstants.EQUAL)){
					navRefinementId = param.split(BBBCoreConstants.EQUAL)[1];
					break;
				}
			}
		} else if(pSearchQuery.isFromBrandPage()){
			navRefinementId = (String) ServletUtil.getCurrentRequest().getSession().getAttribute(BBBEndecaConstants.BRAND_DIM_ID);
		}
			
		for (String param : queryParams) {
			if(param.contains(BBBEndecaConstants.NAVIGATION + BBBCoreConstants.EQUAL)) {
				navigationExist = true;
				for (String dimId : navigationUrl) {
					if (param.contains(dimId)) {
						if (count == 0) {
							navigationDimId = dimId;
							count++;
						} else {
							navigationDimId = navigationDimId + BBBCoreConstants.SPACE + dimId;
						}
					}
				}
				//fetching catagory id | BBBI-3656
				if(BBBUtility.isNotEmpty(navRefinementId)){
					facetDimId = facetDimId + (navigationDimId != null ? (navigationDimId + BBBCoreConstants.SPACE + dimIdFromAlgorithm) :  dimIdFromAlgorithm) + BBBCoreConstants.SPACE + navRefinementId;
				} else {
					facetDimId = facetDimId + (navigationDimId != null ? (navigationDimId + BBBCoreConstants.SPACE + dimIdFromAlgorithm) :  dimIdFromAlgorithm);
				}
				
				if(BBBUtility.isNotEmpty(navInventoryStatusId)){
					facetDimId = facetDimId + BBBCoreConstants.SPACE + navInventoryStatusId;
				}
				
				searchQuery = searchQuery.replace(param, facetDimId);
			}  else if(param.contains(BBBEndecaConstants.NAV_REC_OFFSET + BBBCoreConstants.EQUAL)){
				recordOffsetToBeRemoved = BBBCoreConstants.AMPERSAND + param;
			}
		}
		if (!navigationExist) {
			searchQuery = searchQuery + BBBCoreConstants.AMPERSAND + facetDimId + dimIdFromAlgorithm;
			if(BBBUtility.isNotEmpty(navRefinementId)){
				searchQuery = searchQuery + BBBCoreConstants.SPACE + navRefinementId;
			}
		}
		//BBBI-3739 | No= param removed from query
		if(BBBUtility.isNotEmpty(recordOffsetToBeRemoved)){
			searchQuery = searchQuery.replace(recordOffsetToBeRemoved, "");
		}
		
		//ILD-70 | Pt 3 | Removing SWS keyword from Ntt || updatedNavKeyWord has only null check becoz it can be empty
		if(updatedNavKeyWord != null && BBBUtility.isNotEmpty(navKeyWordToBeUpdated)){
			searchQuery = searchQuery.replace(navKeyWordToBeUpdated, updatedNavKeyWord);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getFacetQuery");
		logDebug(CLS + " - [getFacetQuery] ends with searchQuery: " + searchQuery);
		return searchQuery;
	}

	/**
	 * This method generates the searchQueryURL to be used for fetching the product boosting results
	 * ignoring SWS and InStore functionality
	 * 
	 * @param pSearchQuery
	 * @return String searchQuery
	 */
	public String fetchSearchQueryURL(SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "fetchSearchQueryURL");
		logDebug(CLS + " - [fetchSearchQueryURL] start with pSearchQuery: " + pSearchQuery);
		final String siteId = SiteContextManager.getCurrentSiteId();
		String searchQuery = BBBCoreConstants.BLANK;
		if(BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown()) || (pSearchQuery.isOnlineTab() && !siteId.contains(BBBCoreConstants.TBS))){
			try {
				SearchQuery tempSearchQuery = (SearchQuery) pSearchQuery.clone();
				tempSearchQuery.setNarrowDown(null);
				tempSearchQuery.setOnlineTab(true);
				this.getQueryGenerator().generateEndecaQuery(tempSearchQuery);
				searchQuery = tempSearchQuery.getQueryURL();
			} catch (BBBBusinessException e) {
				logError("Exception occured while generating endeca query " + e);
			} catch (BBBSystemException e) {
				logError("Exception occured while generating endeca query " + e);
			} catch (CloneNotSupportedException e) {
				logError("Exception occured while generating endeca query " + e);
			}
		} else {
			searchQuery = pSearchQuery.getQueryURL();
			
			boolean firstIndex = true;
			//remove Nrs from query parameters to avoid applying in category/brand pages
			if(searchQuery.contains(BBBCoreConstants.AMPERSAND)) {
				String[] queryParams = searchQuery.split(BBBCoreConstants.AMPERSAND);
				StringBuffer queryBuffer = new StringBuffer();
				for(String queryParam : queryParams) {
					if(!queryParam.contains(BBBEndecaConstants.NEGATIVE_RECORD_FILTER)) {
						if(firstIndex){
							queryBuffer.append(queryParam);
							firstIndex = false;
						}else{
							queryBuffer.append(BBBCoreConstants.AMPERSAND + queryParam);
						}
					}
				}
				if(BBBUtility.isNotEmpty(queryBuffer.toString())) {
					searchQuery = queryBuffer.toString();
				}
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "fetchSearchQueryURL");
		logDebug(CLS + " - [fetchSearchQueryURL] ends with searchQueryUrl: " + searchQuery);
		return searchQuery;
	}

	/**
	 * This method used to get the updated endeca query for SORT boosting
	 * strategy
	 * 
	 * @param searchQuery
	 * @param dimensionIdFromAlgorithm
	 * @return
	 */
	public String getSortQuery(SearchQuery pSearchQuery, String sortField, List<String> navigationUrl, boolean l2l3BrandBoostingEnabled) {
		logDebug(CLS + " - [getSortQuery] starts with searchQuery: " + pSearchQuery.getQueryURL() + " and sortField: " + sortField);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSortQuery");
		String searchQuery = fetchSearchQueryURL(pSearchQuery);
		if(BBBUtility.isEmpty(searchQuery)){
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getFacetQuery");
			logDebug(CLS + " - [getFacetQuery] end. EndecaSearchQuery is empty");
			return BBBCoreConstants.BLANK;
		}
		String numberOfdays = null;
		final String navigationSortField = BBBEndecaConstants.NAV_SORT_FIELD + BBBCoreConstants.EQUAL + sortField;
		final String navigationFilter = BBBEndecaConstants.NAV_FILTER + BBBCoreConstants.EQUAL + BBBEndecaConstants.SORT_BOOSTING_QUERY_NAV_FILTER;
		numberOfdays = getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.SORT_ALGORITHM_NUMBER_OF_DAYS, String.valueOf(BBBCoreConstants.TEN));
		String[] queryParams = searchQuery.split(BBBCoreConstants.AMPERSAND);
		String sortDimId = BBBEndecaConstants.NAVIGATION + BBBCoreConstants.EQUAL;
		String navigationDimId = null;
		String recordOffsetToBeRemoved = null;
		int count = 0;
		String navRefinementId = "";
		String navKeyWordToBeUpdated = "";
		String updatedNavKeyWord = "";
		boolean navigationExist = false;
		String navInventoryStatusId = null;
		boolean removeOOSBoostedProduct;
		
		if (l2l3BrandBoostingEnabled){
			try {
			if(pSearchQuery.isFromBrandPage()) {
				removeOOSBoostedProduct =  Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.REMOVE_OOS_BOOSTED_PRODUCT_BRAND));
			} else {
				removeOOSBoostedProduct =  Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.REMOVE_OOS_BOOSTED_PRODUCT_CATEGORY));
			}
			
			if(removeOOSBoostedProduct) {
				navInventoryStatusId = getEndecaSearch().getCatalogId(BBBEndecaConstants.INVENTORY_STATUS,BBBEndecaConstants.POSITIVE);
			}
			} catch (BBBBusinessException e) {
				logError("Business Exception while fetching dimension id ",e);
			} catch (BBBSystemException e) {
				logError("System Exception while fetching dimension id ",e);
			}
		}
		//appending categoryId/BrandId in sort query | ILD-70 pt 1,2
		if(BBBUtility.isEmpty(pSearchQuery.getKeyWord())){
			for (String param : queryParams) {
				if(BBBUtility.isEmpty(pSearchQuery.getKeyWord()) && param.contains(BBBEndecaConstants.NAV_REFINEMENT + BBBCoreConstants.EQUAL)){
					navRefinementId = param.split(BBBCoreConstants.EQUAL)[1];
					break;
				}
			}
		} else if(pSearchQuery.isFromBrandPage()){
			navRefinementId = (String) ServletUtil.getCurrentRequest().getSession().getAttribute(BBBEndecaConstants.BRAND_DIM_ID);
		}
				
		for (String param : queryParams) {
			if(param.contains(BBBEndecaConstants.NAVIGATION + BBBCoreConstants.EQUAL)) {
				navigationExist = true;
				for (String dimId : navigationUrl) {
					if (param.contains(dimId)) {
						if (count == 0) {
							navigationDimId = dimId;
							count++;
						} else {
							navigationDimId = navigationDimId + BBBCoreConstants.SPACE + dimId;
						}
					}
				}
				if (!BBBUtility.isEmpty(navigationDimId)) {
					sortDimId = sortDimId + navigationDimId;
					if(BBBUtility.isNotEmpty(navRefinementId)){
						sortDimId = sortDimId + BBBCoreConstants.SPACE + navRefinementId;
					}
					if(BBBUtility.isNotEmpty(navInventoryStatusId)){
						sortDimId = sortDimId + BBBCoreConstants.SPACE + navInventoryStatusId;
					}
					searchQuery = searchQuery.replace(param, sortDimId);
				}
			} else if(param.contains(BBBEndecaConstants.NAV_REC_OFFSET + BBBCoreConstants.EQUAL)){
				recordOffsetToBeRemoved = BBBCoreConstants.AMPERSAND + param;
			}
			
		}
		
		if (!navigationExist && BBBUtility.isNotEmpty(navRefinementId)) {
			searchQuery = searchQuery + BBBCoreConstants.AMPERSAND + sortDimId + navRefinementId;
		}
		
		if (!BBBUtility.isEmpty(sortField) && sortField.contains(BBBEndecaConstants.NEW_SORT_TYPE)) {
			searchQuery = searchQuery + BBBCoreConstants.AMPERSAND + navigationSortField + BBBCoreConstants.AMPERSAND + navigationFilter + numberOfdays;
		} else {
			searchQuery = searchQuery + BBBCoreConstants.AMPERSAND + navigationSortField;
		}
		
		//BBBI-3739 | No= param removed from query
		if(BBBUtility.isNotEmpty(recordOffsetToBeRemoved)){
			searchQuery = searchQuery.replace(recordOffsetToBeRemoved, "");
		}
		
		//ILD-70 | Pt 3 | Removing SWS keyword from Ntt
		if(updatedNavKeyWord != null && BBBUtility.isNotEmpty(navKeyWordToBeUpdated)){
			searchQuery = searchQuery.replace(navKeyWordToBeUpdated, updatedNavKeyWord);
		}
		
		logDebug(CLS + " - [getSortQuery] ends with searchQuery: " + searchQuery);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSortQuery");
		return searchQuery;
	}

	/**
	 * This method used to get Boosted product list for SORT and FACET
	 * 
	 * @param queryResults
	 * @param algorithmProductList
	 * @return
	 */
	public List<String> getBoostedProductList(ENEQueryResults queryResults, List<String> algorithmProductList,SearchQuery pSearchQuery) {
		logDebug(CLS + " - [getBoostedProductList] starts with queryResults: " + queryResults);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostedProductList");
		if (queryResults != null) {
			Navigation navigation = queryResults.getNavigation();
			if (navigation != null) {
				ERecList eRecs = navigation.getERecs();
				if (eRecs != null && eRecs.size() > 0) {
					for (Object eRecObj : eRecs) {
						ERec eRec = (ERec) eRecObj;
						PropertyMap properties = eRec.getProperties();
						String productId = (String) properties.get(BBBEndecaConstants.FEATURED_PRODUCT_ID);
						algorithmProductList.add(productId);
					}
				}
			}
		}
		logDebug(CLS + " - [getBoostedProductList] ends with algorithmProductList: " + algorithmProductList);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostedProductList");
		
		pSearchQuery.appendBoostingLogs("getBoostedProductList algorithmProductList:"+algorithmProductList );
		return algorithmProductList;
	}
	/**
	 * @return the searchBoostRepository
	 */
	public Repository getSearchBoostRepository() {
		return searchBoostRepository;
	}

	/**
	 * @param searchBoostRepository the searchBoostRepository to set
	 */
	public void setSearchBoostRepository(Repository searchBoostRepository) {
		this.searchBoostRepository = searchBoostRepository;
	}

	/**
	 * @return the siteSpectQuery
	 */
	public String getSiteSpectQuery() {
		return siteSpectQuery;
	}

	/**
	 * @param siteSpectQuery the siteSpectQuery to set
	 */
	public void setSiteSpectQuery(String siteSpectQuery) {
		this.siteSpectQuery = siteSpectQuery;
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
	 * This method used to get the Boosting type on the basis of algorithmType
	 * code passed.
	 * 
	 * @param algoType
	 * @return
	 */
	private String getBoostingType(Integer algorithmType,SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostingType");
		logDebug(CLS + " [getBoostingType] starts with algorithmType: " + algorithmType);
		String boostingStrategy = null;
		if (algorithmType == null) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostingType");
			return boostingStrategy;
		}
		switch(algorithmType) {
		case 1:
			boostingStrategy = BBBEndecaConstants.OPB_BOOSTING_STRATEGY;
			break;
		case 2:
			boostingStrategy = BBBEndecaConstants.SORT_BOOSTING_STRATEGY;
			break;
		case 3:
			boostingStrategy = BBBEndecaConstants.FACET_BOOSTING_STRATEGY;
			break;
		default: //do nothing
			break;
		}
		logDebug(CLS + " [getBoostingType] ends with boosting type: " + boostingStrategy);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostingType");
		pSearchQuery.appendBoostingLogs("getBoostingType boostingStrategy:"+boostingStrategy);
		return boostingStrategy;
	}

	/**
	 * @return the pageTypeMap
	 */
	public Map<String, String> getPageTypeMap() {
		return pageTypeMap;
	}

	/**
	 * @param pageTypeMap the pageTypeMap to set
	 */
	public void setPageTypeMap(Map<String, String> pageTypeMap) {
		this.pageTypeMap = pageTypeMap;
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
	 * This method used to get the page type[in fact, page type code] which will
	 * be used to get the search algorithm for the specific page
	 * 
	 * @param searchQuery
	 * @param l2l3BrandBoostingEnabled
	 * 
	 * @return
	 */
	public int getPageType(SearchQuery searchQuery, boolean l2l3BrandBoostingEnabled) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getPageType");
		logDebug(CLS + " [getPageType] - searchQuery: " + searchQuery + " and l2l3BrandBoostingEnabled: " + l2l3BrandBoostingEnabled);
		int pageType = Integer.parseInt((String) getPageTypeMap().get(DEFAULT));
		if(searchQuery.isFromBrandPage()) {
			pageType = Integer.parseInt((String) getPageTypeMap().get(BRAND));
		} else if(l2l3BrandBoostingEnabled && !searchQuery.isFromBrandPage()) {
			pageType = Integer.parseInt((String) getPageTypeMap().get(L2L3));
		} else if (searchQuery.getKeyWord() != null && !searchQuery.isFromBrandPage()) {
			pageType = Integer.parseInt((String) getPageTypeMap().get(SEARCH));
		} else if (!searchQuery.isFromCollege()) {
			pageType = Integer.parseInt((String) getPageTypeMap().get(COLLEGE));
		} else if (searchQuery.isFromCategoryLanding()) {
			pageType = Integer.parseInt((String) getPageTypeMap().get(L2L3));
		}
		logDebug(CLS + " [getPageType] - Request is from page: " + pageType);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getPageType");
		
		searchQuery.appendBoostingLogs("[getPageType] - Request is from page: " + pageType);
		return pageType;
	}

	/**
	 * This method used to get the boosted product list from cache
	 * 
	 * @param boostQuery
	 * @param boostedProductList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBoostedProductListFromCache(String boostQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostedProductListFromCache");
		logDebug(CLS + " [getBoostedProductListFromCache] - boosting query: " + boostQuery);

		List<String> boostedProductList = new ArrayList<String>();
		final String cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.BOOSTING_STRATEGY_CACHE_NAME);

		boostedProductList = (List<String>) getObjectCache().get(boostQuery, cacheName);

		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getBoostedProductListFromCache");
		logDebug(CLS + " [getBoostedProductListFromCache] - boostedProductList is " + boostedProductList + " in cacheName: " + cacheName);
		return boostedProductList;
	}

	/**
	 * This method used to insert boosted product list into the cache
	 * 
	 * @param boostQuery
	 * @param boostedProductList
	 */
	@Override
	public void insertBoostedProductsIntoCache(String boostQuery, List<String> boostedProductList) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_insertBoostedProductsIntoCache");
		logDebug(CLS + " [insertBoostedProductsIntoCache] - boosting query: " + boostQuery);
		final String cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.BOOSTING_STRATEGY_CACHE_NAME);
		final long cacheTimeout = Long.valueOf(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.BOOSTING_STRATEGY_CACHE_TIMEOUT));

		/***
		 * If boostedProductList is not empty then insert into cache
		 */
		if (boostedProductList != null) {
			getObjectCache().put(boostQuery, boostedProductList, cacheName, cacheTimeout);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_insertBoostedProductsIntoCache");
		logDebug(CLS + " [insertBoostedProductsIntoCache] - boostedProductList is " + boostedProductList + " in cacheName: " + cacheName + " with cache timeout: " + cacheTimeout);
	}

	/**
	 * This method used to get the search boosting algorithms from the Local Map
	 * for the requested boostCode, site id and page name. This boostCode is provided
	 * by the SiteSpect in request.
	 * 
	 * @param searchAlgorithmParams
	 * @return
	 */
	@Override
	public SearchBoostingAlgorithmVO getSearchBoostingAlgorithmsFromLocalMap(Map<String, String> searchAlgorithmParams) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSearchBoostingAlgorithmsFromLocalMap");
		final String boostCode = searchAlgorithmParams.get(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE);
		final int pageType = Integer.valueOf(searchAlgorithmParams.get(BBBCoreConstants.PAGE_NAME));
		final String siteId = searchAlgorithmParams.get(BBBCoreConstants.SITE_ID);
		final String localMapKey = boostCode + BBBCoreConstants.UNDERSCORE + pageType + BBBCoreConstants.UNDERSCORE + siteId;
		logDebug(CLS + " [getSearchBoostingAlgorithmsFromLocalMap] - localMapKey: " + localMapKey);
		SearchBoostingAlgorithmVO algorithmVO = null;
		try {
			algorithmVO = (SearchBoostingAlgorithmVO) getLocalCacheContainer().get(localMapKey);
		} catch (Exception e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSearchBoostingAlgorithmsFromLocalMap");
			this.logError("EndecaSearchToolsImpl [getSearchBoostingAlgorithmsFromLocalMap] - RepositoryException");
		}

		logDebug(CLS + " [getSearchBoostingAlgorithmsFromLocalMap] - algorithmVO: " + algorithmVO);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_TOOLS + "_getSearchBoostingAlgorithmsFromLocalMap");
		return algorithmVO;
	}
}