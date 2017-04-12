package com.bbb.search.endeca.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.endeca.assembler.cartridge.handler.ResultsListHandler;
import atg.nucleus.ServiceMap;
import atg.nucleus.logging.VariableArgumentApplicationLogging;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.search.endeca.boosting.FacetSearchBoostingStrategy;
import com.bbb.search.endeca.boosting.L2L3BrandBoostingStrategy;
import com.bbb.search.endeca.boosting.OPBSearchBoostingStrategy;
import com.bbb.search.endeca.boosting.SortSearchBoostingStrategy;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.tools.EndecaSearchTools;
import com.bbb.search.endeca.util.EndecaConfigUtil;
import com.bbb.search.endeca.vo.AlgorithmComponentVO;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.bbb.utils.BBBUtility;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.ResultsList;
import com.endeca.infront.cartridge.ResultsListConfig;
import com.endeca.infront.navigation.model.CollectionFilter;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.PropertyFilter;
import com.endeca.infront.navigation.model.SearchFilter;
import com.endeca.infront.navigation.model.SortOption;
import com.endeca.infront.navigation.model.SortSpec;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.infront.navigation.request.RecordsMdexQuery;
import com.endeca.infront.navigation.request.support.NavigationRequest;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERecSortKey;
import com.endeca.navigation.ERecSortKeyList;

/**
 * This handler class is responsible for custom preprocessing/processing logic requried for results list
 * @author sc0054
 *
 */

public class BBBResultsListHandler extends ResultsListHandler {

	//default sorting to be used
	private String sortField;

	//configutil to retrieve Endeca property name from config
	private EndecaConfigUtil configUtil;
	private VariableArgumentApplicationLogging logger = AssemblerTools.getApplicationLogging();
	private ServiceMap searchBoostingStrategyMap;
	private EndecaSearchTools endecaSearchTools;
	private BBBConfigTools bbbCatalogTools;
	
	/**
	 * @return
	 */
	public EndecaSearchTools getEndecaSearchTools() {
		return endecaSearchTools;
	}

	/**
	 * @param endecaSearchTools
	 */
	public void setEndecaSearchTools(EndecaSearchTools endecaSearchTools) {
		this.endecaSearchTools = endecaSearchTools;
	}

	/**
	 * @return
	 */
	public ServiceMap getSearchBoostingStrategyMap() {
		return searchBoostingStrategyMap;
	}

	/**
	 * @param searchBoostingStrategy
	 */
	public void setSearchBoostingStrategyMap(
			ServiceMap searchBoostingStrategyMap) {
		this.searchBoostingStrategyMap = searchBoostingStrategyMap;
	}




	@Override
	public void preprocess(ResultsListConfig config) throws CartridgeHandlerException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_preprocess");
		SearchQuery searchQuery = null;
		//lookup SearchQuery in current request for checking if request is for Brand page
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		/***
		 * FilterState is used to get the Negative filters which are injected in ExtendedMdexRequestBroker.
		 * The search query to get boosted products should also include the Nrs endeca parameter in the query string.
		 */
		
		
		if(dynamoRequest != null && dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) != null){
			searchQuery = (SearchQuery)dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
			//ILD-239
			if (null != dynamoRequest.getAttribute(BBBEndecaConstants.NEGATIVE_FILTER) && BBBUtility.isNotEmpty(dynamoRequest.getAttribute(BBBEndecaConstants.NEGATIVE_FILTER).toString()))
			{
				String refineQuery = dynamoRequest.getAttribute(BBBEndecaConstants.NEGATIVE_FILTER).toString();
				searchQuery.setNavRecordStructureExpr(refineQuery);
				//update negative filter by removing storeId from EQL
				if(null != searchQuery && !searchQuery.isOnlineTab() 
						&& null != searchQuery.getStoreId() && !(searchQuery.getStoreId().length() == 0) ) {
					//remove stores query EQL from negative filter to avoid impacting custom boosting
					CharSequence eqlStoreQuery = "endeca:matches(.,\""+BBBEndecaConstants.P_STORES+"\",\""+searchQuery.getStoreId()+"\",\"all\") and ";
					if(refineQuery.contains(eqlStoreQuery)) {
						searchQuery.setNavRecordStructureExpr(refineQuery.replace(eqlStoreQuery, ""));
					} 
				}
			}
		}
		boolean opbEnabled = config.getBooleanProperty(BBBCoreConstants.OPB_ENABLED,true);
		List<String> opbProductList = new ArrayList<String>();
		List<String> sortBoostedProductList = new ArrayList<String>();
		List<String> facetBoostedProductList = new ArrayList<String>();
		List<String> allBoostedProductList = new ArrayList<String>();
		boolean l2L3BrandBoostingEnabled = false;
		final String keywordBoostFlag = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.KEYWORD_BOOST_FLAG, BBBCoreConstants.FALSE);
		final String originOfTraffic = dynamoRequest.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC);
		//BBBI-1421 L2L3BrandPageBoosting
		l2L3BrandBoostingEnabled = getSearchUtil().checkL2L3BrandBoostingFlag(searchQuery, l2L3BrandBoostingEnabled, originOfTraffic);

		//end
		if(logger.isLoggingDebug())
		logger.logDebug("BBBResultsListHandler.preprocess - l2L3BrandBoostingEnabled :"+l2L3BrandBoostingEnabled);

		// Populate searchBoostingAlgorithm VO, if either keywordBoostFlag or l2L3BrandBoostingEnabled is TRUE in BCC.
		SearchBoostingAlgorithmVO searchBoostingAlgorithm = null;
		if(logger.isLoggingDebug())
		logger.logDebug("The value of keywordBoostFlag is : " + keywordBoostFlag);
		
		//ILD-70 | Enabling boosting only for products, not for Videos or Guides
		String productCategory;
		Boolean boostingForProducts = false;
		if(BBBUtility.isNotEmpty(searchQuery.getKeyWord()) && !searchQuery.isFromBrandPage()){
			try {
				productCategory = this.getSearchUtil().getCatalogId(BBBEndecaConstants.RECORD_TYPE, BBBEndecaConstants.PROD_RECORD_TYPE);
				boostingForProducts = searchQuery.getQueryURL().contains(productCategory);
			} catch (BBBBusinessException | BBBSystemException e) {
				logger.logError("Error in fetching categoryId for RecordType Product :" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			//this condition would be satisfied for L2 L3 and Brand Pages
			boostingForProducts = true;
		}

		if (boostingForProducts && (Boolean.valueOf(keywordBoostFlag) || l2L3BrandBoostingEnabled)) {
			/***
			 * Add site id and page type along with the boost code to get the more
			 * specific Search Algorithm from Search Boost repository
			 */
			Map<String, String> searchAlgorithmParams = new LinkedHashMap<String, String>();
			searchAlgorithmParams.put(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, getSearchUtil().getBoostCode(dynamoRequest));
			searchAlgorithmParams.put(BBBCoreConstants.PAGE_NAME, String.valueOf(getEndecaSearchTools().getPageType(searchQuery, l2L3BrandBoostingEnabled)));
			searchAlgorithmParams.put(BBBCoreConstants.SITE_ID, searchQuery.getSiteId());

			/***
			 * Now, fetch SearchBoostAlgorithm from local map instead of
			 * fetching from searchBoostingAlgorithm =
			 * getEndecaSearchTools().getSearchBoostingAlgorithms
			 * (searchAlgorithmParams);
			 */
			searchBoostingAlgorithm = getEndecaSearchTools().getSearchBoostingAlgorithmsFromLocalMap(searchAlgorithmParams);
			if(searchBoostingAlgorithm	==	null
					//&& getEndecaSearchTools().getPageType(searchQuery, l2L3BrandBoostingEnabled) ==1
					&& dynamoRequest.getHeader(BBBCoreConstants.SITE_SPECT_VENDOR_CODE)!=null){
				searchAlgorithmParams.put(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE);
				searchBoostingAlgorithm	=	getEndecaSearchTools().getSearchBoostingAlgorithms(searchAlgorithmParams);
				final String localMapKey = BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE + BBBCoreConstants.UNDERSCORE + 
						String.valueOf(getEndecaSearchTools().getPageType(searchQuery, l2L3BrandBoostingEnabled)) + BBBCoreConstants.UNDERSCORE + searchQuery.getSiteId();
				getEndecaSearchTools().getLocalCacheContainer().put(localMapKey, searchBoostingAlgorithm);
				dynamoRequest.getSession().setAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE);
			}
		}

		
		
		boolean isEPHSortApplied =false;
		//EPH  Based Sorting 
		if(searchQuery.isEPHFound() && BBBCoreConstants.SORT_VALUE.equalsIgnoreCase(searchQuery.getEphQueryScheme())){
			isEPHSortApplied= true; 
		  }ServletUtil.getCurrentRequest().getSession().setAttribute("isCacheEnable", "true");
		List<CollectionFilter> xmBoostedProduct= new ArrayList<CollectionFilter>();


		/***
		 * If searchBoostingAlgorithm are available in the SEARCH Boost
		 * repository then calculate the boosted products' percentage and their
		 * count.
		 */
		
		
		if (opbEnabled &&((searchQuery.getKeyWord() !=null && StringUtils.isEmpty(searchQuery.getSortString())) || (l2L3BrandBoostingEnabled))) {
			if (searchBoostingAlgorithm != null && !BBBUtility.isListEmpty(searchBoostingAlgorithm.getAlgorithmComponents())) {
				/***
				 * Calculate OPB products count which to be boosted against all
				 * the OPB products available and set the total boosted products
				 * count in searchBoostingAlgorithm VO which would be used in
				 * calculation of SORT & FACET boosted products count in
				 * EndecaSearchToolsImpl.populateBoostedProductParams
				 */
				final int opbBoostedProductCount = getOmnitureBoostedProductsCount(searchQuery, searchBoostingAlgorithm, opbProductList, l2L3BrandBoostingEnabled);
				List<String> opbProductSubList = new ArrayList<String>();
				/***
				 * Fetch boosted products in a sequence which is identified in
				 * the Search Boost repository for the specific algorithm and
				 * add them into allBoostedProductList in same sequence
				 */
				String actualBoostingApplied	=	"";
				for (AlgorithmComponentVO algorithmComponent : searchBoostingAlgorithm.getAlgorithmComponents()) {
					if (BBBEndecaConstants.OPB_BOOSTING_STRATEGY_TYPE.equals(algorithmComponent.getAlgorithmType())) {
						opbProductSubList = opbProductList.subList(BBBCoreConstants.ZERO, opbBoostedProductCount);
						allBoostedProductList.addAll(opbProductSubList);
							if(!opbProductSubList.isEmpty()){
								actualBoostingApplied	+=	BBBCoreConstants.HASH+algorithmComponent.getAlgorithmDescription();
							}else{
								actualBoostingApplied	+=	BBBCoreConstants.HASH+algorithmComponent.getAlgorithmName()+ BBBCoreConstants.ALGORITHM_DEFAULT_PERCENTAGE;
							}
					} else if (BBBEndecaConstants.SORT_BOOSTING_STRATEGY_TYPE.equals(algorithmComponent.getAlgorithmType())) {
						sortBoostedProductList = ((SortSearchBoostingStrategy) getSearchBoostingStrategyMap().get(BBBEndecaConstants.SORT_BOOSTING_STRATEGY)).getBoostedProducts(searchQuery, searchBoostingAlgorithm,l2L3BrandBoostingEnabled);
						allBoostedProductList.addAll(sortBoostedProductList);
							if(!sortBoostedProductList.isEmpty() ){
								// do your code here
								actualBoostingApplied	+=	BBBCoreConstants.HASH+algorithmComponent.getAlgorithmDescription();
							}else{
								actualBoostingApplied	+=	BBBCoreConstants.HASH+algorithmComponent.getAlgorithmName()+BBBCoreConstants.ALGORITHM_DEFAULT_PERCENTAGE;
							}
					} else if (BBBEndecaConstants.FACET_BOOSTING_STRATEGY_TYPE.equals(algorithmComponent.getAlgorithmType())) {
						facetBoostedProductList = ((FacetSearchBoostingStrategy) getSearchBoostingStrategyMap().get(BBBEndecaConstants.FACET_BOOSTING_STRATEGY)).getBoostedProducts(searchQuery, searchBoostingAlgorithm,l2L3BrandBoostingEnabled);
						allBoostedProductList.addAll(facetBoostedProductList);
							if(!facetBoostedProductList.isEmpty()){
								actualBoostingApplied	+=	BBBCoreConstants.HASH+algorithmComponent.getAlgorithmDescription();
							}else{
								actualBoostingApplied	+=	BBBCoreConstants.HASH+algorithmComponent.getAlgorithmName()+BBBCoreConstants.ALGORITHM_DEFAULT_PERCENTAGE;
							}
						
					}
				}
				if(BBBUtility.isNotEmpty(actualBoostingApplied)){
					dynamoRequest.setAttribute(BBBCoreConstants.ACTUALAPPLIEDALGORITHM, actualBoostingApplied);
					}
				// Remove Brand dimension id from Session
				ServletUtil.getCurrentRequest().getSession().removeAttribute(BBBEndecaConstants.BRAND_DIM_ID);
				if(logger.isLoggingDebug())
				logger.logDebug("BBBResultsListHandler.preprocess - OPB: " + opbProductSubList + "\nSORT: " + sortBoostedProductList + "\nFACET: " + facetBoostedProductList);
	
				/***
				 * If Randomization_Required Flag is true in
				 * searchBoostingAlgorithm(BBB_Search_Boost_Entry table) then
				 * boosted products will be re-arranged in random order and
				 * passed to strata otherwise repository identified sequence
				 * will be passed.
				 */
				if (searchBoostingAlgorithm.isRandomizationRequired()) {
					if(logger.isLoggingDebug())
					logger.logDebug("BBBResultsListHandler.preprocess - Randomization Required flag is TRUE.");
					/***
					 * Initialize the variables
					 */
					List<String> randomProductList = new ArrayList<String>();
					Map<String, List<String>> boostedProductsMap = new ConcurrentHashMap<String, List<String>>();

					/***
					 * Populate boostedProductsMap with each boosted product list
					 */
					boostedProductsMap.put(BBBEndecaConstants.OPB_BOOSTING_STRATEGY, opbProductSubList);
					boostedProductsMap.put(BBBEndecaConstants.SORT_BOOSTING_STRATEGY, sortBoostedProductList);
					boostedProductsMap.put(BBBEndecaConstants.FACET_BOOSTING_STRATEGY, facetBoostedProductList);

					/***
					 * Invoke getRandomizedBoostedProductList method to populate
					 * the product Ids in randomList in random manner
					 */
					getRandomizedBoostedProductList(boostedProductsMap, randomProductList);

					// Add randomProductList to boostStrata in Endeca
					xmBoostedProduct=addCustomBoostProductsToStrata(config, searchQuery, randomProductList,isEPHSortApplied);
				} else {
				// Add allBoostedProductList to boostStrata in Endeca
					xmBoostedProduct=addCustomBoostProductsToStrata(config, searchQuery, allBoostedProductList,isEPHSortApplied);
			}
		}
		}
		
		//EPH Based Sorting
		if(isEPHSortApplied){
			getSearchUtil().sortBoostStrataWithEPH(config,searchQuery.getEphResultVO(),searchQuery.getKeyWord(),searchQuery); 
			//add XM boosted product if custom boosting true  after EPH boosting if Prepend true
			config.getBoostStrata().addAll(xmBoostedProduct);
		}
		
		//process sort parameters from SearchQuery in request objects
		ERecSortKeyList sortKeyList = processSorting(searchQuery);
		String sortParameter = "";
		if(sortKeyList != null) {
			//sort keys exist
			for(int i=0;i<sortKeyList.size();i++) {
				ERecSortKey sortKey = (ERecSortKey)sortKeyList.get(i);
				if(sortKey.getName() != null) {
					if(sortParameter.length() > 0) {
						sortParameter += "||";
					}
					sortParameter += sortKey.getName()+"|"+(sortKey.getOrder() == -1 ? 1 : 0);
					if(sortKey.getName().equalsIgnoreCase("P_Ratings")) {
						sortParameter += "||P_Num_Ratings"+"|"+"1";
					}
				}
			}
		}

    	if(!sortParameter.equals("")) {
    		config.put("sortOption", new SortOption(sortParameter));
    	} else {
    		//convert sort option selected in brand rules to relrank
    		//brand pages are internally treated as search pages
    		//and sort selected in rules is not applied in new version
    		if(null != config.getSortOption() && EndecaSearchUtil.isBrandPageRequest(searchQuery)) {
    			String relRankUpdated = "";
    			for(SortSpec sortSpec : config.getSortOption().getSorts()) {
    				if(!relRankUpdated.equals("")) {
    					relRankUpdated += ",";
    				}
    				relRankUpdated = relRankUpdated+"static("+sortSpec.getKey()+","+(sortSpec.isDescending()?"descending":"ascending")+")";
    			}
    			if(config.getRelRankStrategy() != null && !config.getRelRankStrategy().equals("")) {
    				if(!relRankUpdated.equals("")) {
            			config.setRelRankStrategy(relRankUpdated+","+config.getRelRankStrategy());
    				}
    			} else {
    				if(!relRankUpdated.equals("")) {
            			config.setRelRankStrategy(relRankUpdated);
    				}
    			}
    		}
    	}

    	//updating pageSize from searchQuery to config
    	if(searchQuery != null) {
    		try {
    			int pageSize = Integer.parseInt(searchQuery.getPageSize());
				config.setRecordsPerPage(pageSize);
	    		if(!StringUtils.isEmpty(searchQuery.getPageNum())){
	    			final long pageNum = Long.parseLong(searchQuery.getPageNum());
	    			final Long recordOffset = (pageSize * (pageNum - 1));
	    			config.setOffset(recordOffset);
	    		}
			} catch(NumberFormatException nfe) {
				logger.logError(nfe.getMessage());
			}
    	}

    	if(validToAddDefaultRelRankStrategy(config, searchQuery)) {
    		config.setRelRankStrategy(fetchFirstSearchInterfaceName());
    	}

		super.preprocess(config);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_preprocess");
	}

	/**
	 * This method is used to do the followings
	 * 1) Mark each list with respective MARKER TYPE
	 * 2) Add all markers to the shuffleList
	 * 3) shuffle the order of all the boosted products get the product list in random order.
	 * 4) Populate randomList with the actual product Ids
	 * @param opbProductList
	 * @param sortBoostedProductList
	 * @param facetBoostedProductList
	 * @param randomList
	 */
	@SuppressWarnings("unused")
	private void getRandomizedBoostedProductList(Map<String, List<String>> boostedProductsMap, List<String> randomProductList) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_getRandomizedBoostedProductList");
		if(logger.isLoggingDebug())
		logger.logDebug("BBBResultsListHandler_getRandomizedBoostedProductList - boostedProductsMap is: " + boostedProductsMap);
		/***
		 * Initialize the variables
		 */
		List<String> shuffleProductList = new ArrayList<String>();

		// Check if boostedProductsMap is EMPTY
		if (BBBUtility.isMapNullOrEmpty(boostedProductsMap)) {
			if(logger.isLoggingDebug())
			logger.logDebug("BBBResultsListHandler_getRandomizedBoostedProductList - boostedProductsMap is EMPTY!");
			return;
		}

		/***
		 * Iterate through each list and add marker OPB, SORT and/or FACET to
		 * the shuffleList
		 */
		for (String opbProduct : boostedProductsMap.get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY)) {
			shuffleProductList.add(BBBEndecaConstants.OPB_BOOSTING_STRATEGY);
		}
		for (String sortProduct : boostedProductsMap.get(BBBEndecaConstants.SORT_BOOSTING_STRATEGY)) {
			shuffleProductList.add(sortProduct);
		}
		for (String facetProduct : boostedProductsMap.get(BBBEndecaConstants.FACET_BOOSTING_STRATEGY)) {
			shuffleProductList.add(facetProduct);
		}

		// Use Collections to shuffle the elements of shuffleList
		Collections.shuffle(shuffleProductList);

		/***
		 * Iterate over the shuffleList, Fetch each marker from the shuffleList
		 * and get their respective value from the boostedProductsMap. Then
		 * populate randomList in a sequence that respective boosting strategy
		 * elements are marked in shuffleList.
		 */
		int shuffleProductIndex = 0;
		for (String shuffleProduct : shuffleProductList) {
			if(boostedProductsMap.get(shuffleProductList.get(shuffleProductIndex)) != null && BBBEndecaConstants.OPB_BOOSTING_STRATEGY.equalsIgnoreCase(shuffleProductList.get(shuffleProductIndex))) {
				List<String> productList = boostedProductsMap.get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY);
				randomProductList.add(productList.get(0));
				productList.remove(0);
			} else {
				randomProductList.add(shuffleProduct);
			}
			shuffleProductIndex++;
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_getRandomizedBoostedProductList");
		if(logger.isLoggingDebug())
		logger.logDebug("BBBResultsListHandler_getRandomizedBoostedProductList - randomList is: " + randomProductList);
	}

	/**
	 * This method is used to get the number of omniture boosted products' count
	 * 
	 * @param searchQuery
	 * @param searchBoostingAlgorithm
	 * @param opbProductList
	 * @param l2l3BrandBoostingEnabled
	 * @return
	 */
	private int getOmnitureBoostedProductsCount(SearchQuery searchQuery, SearchBoostingAlgorithmVO searchBoostingAlgorithm, List<String> opbProductList, boolean l2l3BrandBoostingEnabled) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_getOmnitureBoostedProductsCount");
		if(logger.isLoggingDebug())
		logger.logDebug("BBBResultsListHandler.getOmnitureBoostedProductsCount starts with l2l3BrandBoostingEnabled: " + l2l3BrandBoostingEnabled + " and searchQuery: " + searchQuery);
		// Fetch boost percentage for each boost type and Set it to the attribute of SearchBoostingAlgorithm.
		final Map<String, Double> boostPercentageMap = getEndecaSearchTools().getBoostingPercentage(searchBoostingAlgorithm,searchQuery);
		searchBoostingAlgorithm.setBoostPercentageMap(boostPercentageMap);
		Double opbPercentage = 0.00;
		int totalNumberOfBoostedProducts = BBBCoreConstants.ZERO;
		int opbBoostedProductCount = BBBCoreConstants.ZERO;
		if(!BBBUtility.isMapNullOrEmpty(boostPercentageMap)) {
			opbPercentage = boostPercentageMap.get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY);
		}

		/**
		 * Check if OPB is configured in algorithm and it is non-zero then fetch
		 * opbProductList otherwise calculations would be based on config key
		 * BoostedProductsForZeroOBP
		 */
		if(searchQuery != null && opbPercentage != null && opbPercentage.intValue() != BBBCoreConstants.ZERO) {
			int maxPLP = Integer.valueOf(searchQuery.getPageSize());
			int opbCount = (int) Math.ceil(new Double(opbPercentage * maxPLP / BBBCoreConstants.HUNDRED));
			if(logger.isLoggingDebug())
			logger.logDebug("BBBResultsListHandler.getOmnitureBoostedProductsCount - opbPercentage : " + opbPercentage + " , maxPLP: " + maxPLP + " and opbCount: " + opbCount);
			/***
			 * Populate OPB boosted product list if it's L2/L3 page or BRAND
			 * page otherwise OPB boosted products for search page results
			 */
			
			if (l2l3BrandBoostingEnabled) {
				opbProductList.addAll(((L2L3BrandBoostingStrategy) getSearchBoostingStrategyMap().get(BBBCoreConstants.L2L3BRAND)).getBoostedProducts(searchQuery, searchBoostingAlgorithm, l2l3BrandBoostingEnabled));
			} else {
				opbProductList.addAll(((OPBSearchBoostingStrategy) getSearchBoostingStrategyMap().get(BBBEndecaConstants.OPB_BOOSTING_STRATEGY)).getBoostedProducts(searchQuery, searchBoostingAlgorithm, l2l3BrandBoostingEnabled));
			}
			// Calculate total number of OPB boosted products
			int numberOfOPBs = !BBBUtility.isListEmpty(opbProductList) ? opbProductList.size() : BBBCoreConstants.ZERO;
			// Assign lowest value between numberOfOPBs and opbCount to the opbBoostedProductCount
			if (numberOfOPBs > opbCount) {
				opbBoostedProductCount = opbCount;
				totalNumberOfBoostedProducts = getEndecaSearchTools().getTotalNumberOfBoostedProducts(searchBoostingAlgorithm, opbCount,searchQuery);
			} else {
				opbBoostedProductCount = numberOfOPBs;
				totalNumberOfBoostedProducts = getEndecaSearchTools().getTotalNumberOfBoostedProducts(searchBoostingAlgorithm, numberOfOPBs,searchQuery);
			}
		} else {
			// When OPB percentage is ZERO or undefined
			totalNumberOfBoostedProducts = getEndecaSearchTools().getTotalNumberOfBoostedProducts(searchBoostingAlgorithm, BBBCoreConstants.ZERO,searchQuery);
		}
		// Set total number of boosted products to the attribute of SearchBoostingAlgorithm, which could be used in future.
		searchBoostingAlgorithm.setTotalBoostedProducts(totalNumberOfBoostedProducts);
		if(logger.isLoggingDebug())
		logger.logDebug("BBBResultsListHandler.getOmnitureBoostedProductsCount ends with opbBoostedProductCount: " + opbBoostedProductCount);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_getOmnitureBoostedProductsCount");
		return opbBoostedProductCount;
	}

	
	public static String substringAfter(String str, String separator) {
	      if (StringUtils.isEmpty(str)) {
	          return str;
	      }
	      if (separator == null) {
	          return "";
	      }
	      int pos = str.indexOf(separator);
	      if (pos == -1) {
	          return "";
	      }
	      return str.substring(pos + separator.length());
	  }

	/**
	 * Method overridden to call Endeca for in-store product count or for all product count
	 *
	 */
	@Override
	public ResultsList process(ResultsListConfig cartridgeConfig)
			throws CartridgeHandlerException {
		ResultsList results = super.process(cartridgeConfig);

		SearchQuery searchQuery = null;
		//lookup SearchQuery in current request for checking if request is for Brand page
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		if(dynamoRequest != null && dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) != null){
			searchQuery = (SearchQuery)dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
			//Logic to fetch negativeMatchQuery updated with ILD-239 changes 
			if (null != dynamoRequest.getAttribute(BBBEndecaConstants.NEGATIVE_FILTER) && BBBUtility.isNotEmpty(dynamoRequest.getAttribute(BBBEndecaConstants.NEGATIVE_FILTER).toString()))
			{
				String refineQuery = dynamoRequest.getAttribute(BBBEndecaConstants.NEGATIVE_FILTER).toString();
				results.put("negativeMatchQuery", ""+refineQuery);
			}
			
		}

		//check if store product count needs to be calculated
//		if( searchQuery != null
//				&& searchQuery.isOnlineTab()
//				&& !BBBUtility.isEmpty(searchQuery.getStoreId()) && (!BBBUtility.isEmpty(searchQuery.getPageNum()) && (searchQuery.getPageNum()).equalsIgnoreCase("1")) ) {
//
//			String storeId = searchQuery.getStoreId();
//			//check if navigation state and filter state are not empty
//			if(this.getNavigationState() != null && this.getNavigationState().getFilterState() != null) {
//				FilterState filterState = this.getNavigationState().getFilterState().clone();
//
//				//update filter state with store query paramaters
//				SearchFilter searchFilter = new SearchFilter();
//				searchFilter.setKey("P_Stores");
//				searchFilter.setTerms(storeId);
//				searchFilter.setMatchMode(MatchMode.ALL);
//				filterState.getSearchFilters().add(searchFilter);
//
//				RecordsMdexQuery recordsQuery = new RecordsMdexQuery();
//				recordsQuery.setOffset(0);
//				recordsQuery.setRecordsPerPage(0);
//				recordsQuery.setFieldNames(new ArrayList());
//
//				MdexRequest localStorePLPRequest = createMdexRequest(filterState, recordsQuery);
//
//				ENEQueryResults localStorePLPQueryResults = executeMdexRequest(localStorePLPRequest);
//
//				if(localStorePLPQueryResults.containsNavigation() && localStorePLPQueryResults.getNavigation() != null) {
//					long totalCount = localStorePLPQueryResults.getNavigation().getTotalNumERecs();
//
//					if(totalCount > 0 ){
//						results.put("storeCount", ""+totalCount);
//					}
//				}
//
//			}
//
//
//		}
//		else 
			if (searchQuery != null &&!BBBUtility.isEmpty(searchQuery.getStoreId()) && !searchQuery.isOnlineTab() ){
			if(this.getNavigationState() != null && this.getNavigationState().getFilterState() != null) {
				FilterState filterState = this.getNavigationState().getFilterState().clone();

				//remove store related searchFilter from filter state
				List<SearchFilter> searchFilters = filterState.getSearchFilters();
				List<SearchFilter> updatedSearchFilters = new ArrayList<SearchFilter>();
				if(searchFilters != null) {
					for(SearchFilter searchFilter : searchFilters) {
						if(!BBBUtility.isEmpty(searchFilter.getKey()) && !searchFilter.getKey().equalsIgnoreCase("P_Stores")) {
							updatedSearchFilters.add(searchFilter);
						}
					}
				}
				filterState.setSearchFilters(updatedSearchFilters);
				
				RecordsMdexQuery recordsQuery = new RecordsMdexQuery();
				recordsQuery.setOffset(0);
				recordsQuery.setRecordsPerPage(0);
				recordsQuery.setFieldNames(new ArrayList());

				MdexRequest localStorePLPRequest = createMdexRequest(filterState, recordsQuery);

				if(localStorePLPRequest instanceof NavigationRequest) {
					FilterState updatedFilterState = ((NavigationRequest)localStorePLPRequest).getFilterState();
					if(updatedFilterState.getEqlFilter() != null && updatedFilterState.getEqlFilter().getExpression() != null) {
						String eqlFilter = updatedFilterState.getEqlFilter().getExpression();
						//remove stores query EQL from negative filter to avoid impacting online product count
						CharSequence eqlStoreQuery = "endeca:matches(.,\""+BBBEndecaConstants.P_STORES+"\",\""+searchQuery.getStoreId()+"\",\"all\") and ";
						if(eqlFilter.contains(eqlStoreQuery)) {
							((NavigationRequest)localStorePLPRequest).getFilterState().getEqlFilter().setExpression(eqlFilter.replace(eqlStoreQuery, ""));
						} else {
							//there is only one eql filter and it needs to be ignored
							((NavigationRequest)localStorePLPRequest).getFilterState().setEqlFilter(null);
						}
					}
				}
				
				ENEQueryResults onlineProductCount = executeMdexRequest(localStorePLPRequest);

				if(onlineProductCount.containsNavigation() && onlineProductCount.getNavigation() != null) {
					long totalCount = onlineProductCount.getNavigation().getTotalNumERecs();

					if(totalCount > 0 ){
						results.put("onlineProductCount", ""+totalCount);
					}
				}

			}
		}

		return results;
	}




	/**
	 * This method used to add the boosted products to boost strata in ResultListConfig
	 *
	 * @param config
	 * @param searchQuery
	 * @param allBoostedProductList
	 */
	private List<CollectionFilter> addCustomBoostProductsToStrata(ResultsListConfig config, SearchQuery searchQuery, List<String> allBoostedProductList,boolean isEPHSortApplied) {
		if(logger.isLoggingDebug())
		logger.logDebug("START BBBResultsListHandler.addCustomBoostProductsToStrata - All boosted products are: " + allBoostedProductList+",isEPHSortApplied:["+isEPHSortApplied+"]");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_addCustomBoostProductsToStrata");
		
		List<CollectionFilter> xmBoostedProduct = new ArrayList<CollectionFilter>();
		
		if(allBoostedProductList != null && !allBoostedProductList.isEmpty()) {
		    String opbPosition = BBBCoreConstants.OPB_PREPEND;

		    if(!StringUtils.isEmpty((String) config.getTypedProperty(BBBCoreConstants.OPB_RESULTS_POSITION))) {
		    	opbPosition = config.getTypedProperty(BBBCoreConstants.OPB_RESULTS_POSITION);
		    	if(logger.isLoggingDebug())
		    	logger.logDebug("[BBBResultsListHandler.addCustomBoostProductsToStrata] - OOTB value for opbPosition is : " + opbPosition + " where default is PREPEND.");
		    }

		    List <CollectionFilter> allProductsBoostStrata = new ArrayList<CollectionFilter>();
		    List<CollectionFilter> ootbBoostStrata = config.getBoostStrata();
		    List <CollectionFilter> fullBoostStrata = new ArrayList<CollectionFilter>();
		    String siteID = searchQuery.getSiteId();
		    for (String productId : allBoostedProductList) {
		    	PropertyFilter innerFilter = new PropertyFilter(BBBCoreConstants.P_PRODUCT_SITE_ID,siteID+BBBCoreConstants.UNDERSCORE+productId);
				CollectionFilter collectionFilter = new CollectionFilter();
				collectionFilter.setInnerFilter(innerFilter);
				allProductsBoostStrata.add(collectionFilter);
			}
		    //in the case out of the box boosted products are available
		    if(ootbBoostStrata != null && !ootbBoostStrata.isEmpty()) {
			    // opb products
			    if(opbPosition.equalsIgnoreCase(BBBCoreConstants.OPB_PREPEND)) {

			    	if( ! isEPHSortApplied){
				    	allProductsBoostStrata.addAll(ootbBoostStrata);
			    	}else{
			    		xmBoostedProduct.addAll(ootbBoostStrata);// Will be added in BoostStrata after adding dim of EPH Product
			    	}
				    	
				    fullBoostStrata= allProductsBoostStrata;

			    } else {
			    	ootbBoostStrata.addAll(allProductsBoostStrata);
			    	fullBoostStrata = ootbBoostStrata;
			    }
		    } else {
		    	fullBoostStrata = allProductsBoostStrata;
		    }

		    config.setBoostStrata(fullBoostStrata);
	    }
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_addCustomBoostProductsToStrata");
		if(logger.isLoggingDebug())
		logger.logDebug("END BBBResultsListHandler.addCustomBoostProductsToStrata: xmBoostedProduct:["+xmBoostedProduct+"]");
		return xmBoostedProduct;
	}



	/**
	 * checks conditions to add default search interface name as relRank
	 * @param pConfig
	 * @param pSearchQuery
	 * @return
	 */
	private boolean validToAddDefaultRelRankStrategy(ResultsListConfig pConfig, SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_validToAddDefaultRelRankStrategy");
		/*if(pSearchQuery != null && !pSearchQuery.isOnlineTab()){
			return false;
		}*/
		//if current query is search query and when relrank is 
		if( EndecaSearchUtil.isKeywordSearchRequest(pSearchQuery)
				&& (pConfig.getRelRankStrategy() == null || pConfig.getRelRankStrategy().equals(""))) {
			//valid to add search interface name only if either boost or bury exists
			if(pConfig.getBoostStrata() != null && !pConfig.getBoostStrata().isEmpty()) {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_validToAddDefaultRelRankStrategy");
				return true;
			}
			if(pConfig.getBuryStrata() != null && !pConfig.getBuryStrata().isEmpty()) {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_validToAddDefaultRelRankStrategy");
				return true;
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_validToAddDefaultRelRankStrategy");
		return false;
	}



	/**
	 * find first search interface used from navigation state and return it
	 * @return
	 */
	private String fetchFirstSearchInterfaceName() {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_fetchFirstSearchInterfaceName");
		if(null != getNavigationState() && null != getNavigationState().getFilterState() && null != getNavigationState().getFilterState().getSearchFilters()) {
			List<SearchFilter> searchFilters = getNavigationState().getFilterState().getSearchFilters();
			for(SearchFilter searchFilter : searchFilters) {
				if(null != searchFilter) {
					BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_fetchFirstSearchInterfaceName");
					return searchFilter.getKey();
				}
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_fetchFirstSearchInterfaceName");
		return "";
	}



	/**
	 * converts query's sort keys into ERecSortKeyList
	 *
	 * @param pSearchQuery
	 * @return
	 */
	private ERecSortKeyList processSorting(final SearchQuery pSearchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_processSorting");
		if(pSearchQuery ==null || pSearchQuery.getSortCriteria() == null) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_processSorting");
			return null;
		}
		ERecSortKeyList sortKeyList = null;
		String key = null;
		ERecSortKey sortKey = null;

		Map<String, String> sortFieldMap = getConfigUtil().getSortFieldMap();

		if(pSearchQuery !=null && pSearchQuery.getSortCriteria() != null
				&& !BBBUtility.isEmpty(pSearchQuery.getSortCriteria().getSortFieldName()) &&
				null != sortFieldMap &&
				null != sortFieldMap.get(pSearchQuery.getSortCriteria().getSortFieldName().toLowerCase())){
			//logDebug("SortCriteria fieldName"+pSearchQuery.getSortCriteria().getSortFieldName());
			//logDebug("SortAscending: "+pSearchQuery.getSortCriteria().isSortAscending());
			String endecaSortKey = sortFieldMap.get(pSearchQuery.getSortCriteria().getSortFieldName().toLowerCase());
			//logDebug("endecaSortKey=sortFieldMap.get(SortCriteria)"+endecaSortKey);
			// Check to see if the default sort option for the current request is intended to
			//bring the boosted / buried records through Page builder.
			// If yes, then ensure that there is no sort option explicitly passed in our query otherwise it will overrrule PB rule.

			if(!StringUtils.isEmpty(getConfigUtil().getBoostBurrySort())){
				if(!(getConfigUtil().getBoostBurrySort()).equalsIgnoreCase(pSearchQuery.getSortCriteria().getSortFieldName())){
					key = endecaSortKey;
					sortKeyList = new ERecSortKeyList();
					if(key !=null){
						if(BBBUtility.isEmpty(pSearchQuery.getKeyWord()) && !(pSearchQuery.isFromBrandPage())){
							if(key.equalsIgnoreCase("P_Date")){
								String rankKey = "P_Product_Rank";
								sortKey = new ERecSortKey(rankKey, true);
								sortKeyList.add(sortKey);
							}
						}
						sortKey = new ERecSortKey(key,pSearchQuery.getSortCriteria().isSortAscending());
						if(!pSearchQuery.getSortCriteria().isSortAscending()){
							pSearchQuery.setSortString(pSearchQuery.getSortCriteria().getSortFieldName()+"-1");
							//logDebug("sort String in if boost bury: "+pSearchQuery.getSortString());
						}
						else{
							pSearchQuery.setSortString(pSearchQuery.getSortCriteria().getSortFieldName()+"-0");
							//logDebug("sort String in else boost bury: "+pSearchQuery.getSortString());
						}
						sortKeyList.add(sortKey);
					}
				}else{
					pSearchQuery.setSortString(getConfigUtil().getBoostBurrySort()+"-0");
					//logDebug("if fieldname is not BoostBurrySort"+pSearchQuery.getSortString());
				}
			}
		}
		else{
			if(StringUtils.isEmpty(pSearchQuery.getKeyWord())){
				sortKeyList = new ERecSortKeyList();
				// Request is not from Search Results page for the very first time.
				key = getSortField();
				//logDebug("key from getSortField "+key);
				if(key !=null){
					if(key.equalsIgnoreCase("P_Date")){
						String rankKey = "P_Product_Rank";
						sortKey = new ERecSortKey(rankKey,true);
						sortKeyList.add(sortKey);
					}
					sortKey = new ERecSortKey(key,false);
				}
				pSearchQuery.setSortString("Date-1");
				sortKeyList.add(sortKey);
			}
		else{
				pSearchQuery.setSortString("");
			}
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_processSorting");
		return sortKeyList;
	}



	/**
	 * @return the sortField
	 */
	public String getSortField() {
		return sortField;
	}



	/**
	 * @param sortField the sortField to set
	 */
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}



	/**
	 * @return the configUtil
	 */
	public EndecaConfigUtil getConfigUtil() {
		return configUtil;
	}



	/**
	 * @param configUtil the configUtil to set
	 */
	public void setConfigUtil(EndecaConfigUtil configUtil) {
		this.configUtil = configUtil;
	}


	private EndecaSearchUtil mSearchUtil;
	//92F

	public EndecaSearchUtil getSearchUtil() {
		return mSearchUtil;
	}

	public void setSearchUtil(EndecaSearchUtil mSearchUtil) {
		this.mSearchUtil = mSearchUtil;
	}

	public BBBConfigTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBConfigTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

}