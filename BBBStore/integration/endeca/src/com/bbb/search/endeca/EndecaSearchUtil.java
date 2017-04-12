package com.bbb.search.endeca;

import static com.bbb.search.endeca.constants.BBBEndecaConstants.NAVIGATION;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.QUESTION_MARK;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.URL_ENCODING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;




//import java.util.TreeMap;
//import atg.core.util.StringUtils;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy;
import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBSearchConstants;
import com.bbb.eph.customizer.ColorSearchTermCustomizer;
import com.bbb.eph.util.EPHLookUpUtil;
import com.bbb.eph.vo.EPHResultVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResult;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.bean.result.SkuVO;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.tools.EndecaSearchTools;
import com.bbb.search.endeca.vo.BBBDimVal;
import com.bbb.search.endeca.vo.BBBDimension;
import com.bbb.search.endeca.vo.BBBQueryResults;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.endeca.infront.cartridge.ResultsListConfig;
import com.endeca.infront.navigation.model.CollectionFilter;
import com.endeca.infront.navigation.model.DimensionFilter;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.ESearchAutoSuggestion;
import com.endeca.navigation.ESearchDYMSuggestion;
import com.endeca.navigation.ESearchReport;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;
import com.endeca.navigation.UrlGen;
import com.endeca.soleng.urlformatter.UrlFormatException;
import com.endeca.soleng.urlformatter.UrlState;
import com.endeca.soleng.urlformatter.basic.BasicUrlFormatter;

public class EndecaSearchUtil extends BBBGenericService {

	private static final String ONE = "1";
	private static final String _8 = "8";
	private static final String _10 = "10";
	private static final String _ID = "id";
	private static final String IMG_PATTERN_FROM_ENDECA = "\\$.*?\\$";
	private static final String IMG_SIZE_FROM_ENDECA = "400";
	private static final String IMG_SIZE_FOR_GRID3x3 = "229";
	private static final String IMG_SIZE_FOR_GRID3x3_newPlp = "320";
	private static final String IMG_SIZE_FOR_GRID4 = "170";
	private static final String CONSTANT_DOLLAR = "\\$";
	private static final String IMG_SIZE_FROM_DESKTOP_LISTVIEW = "146";
	private EndecaClient endecaClient;
	private EndecaQueryGenerator queryGenerator;

	private static final String GETTING_CACHE_TIMEOUT_FOR = "getting cacheTimeout for ";
	private static final String ENABLE_MAX_CACHING_FOR_SITESPECT = "ENABLE_MAX_CACHING_FOR_SITESPECT";
	private int productCacheTimeout;
	private String defaultPerPageSize;
	private Map<String, String> typeAheadMaxCacheSizeMap;
	//Property to hold siteIds Map.
	private Map<String, String> siteConfig;
	//added for testing color relevancy 
	private String colorSearchTermCustomizer;
	
	private EPHLookUpUtil ephLookUpUtil;
	private EndecaSearchTools endecaSearchTools;
	private BBBGiftRegistryUtils giftRegistryUtils;
	/**
	 * @return the typeAheadMaxCacheSizeMap
	 */
	public Map<String, String> getTypeAheadMaxCacheSizeMap() {
		return this.typeAheadMaxCacheSizeMap;
	}
	/**
	 * @param typeAheadMaxCacheSizeMap the typeAheadMaxCacheSizeMap to set
	 */
	public void setTypeAheadMaxCacheSizeMap(
			Map<String, String> typeAheadMaxCacheSizeMap) {
		this.typeAheadMaxCacheSizeMap = typeAheadMaxCacheSizeMap;
	}

	public String getDefaultPerPageSize() {
		return this.defaultPerPageSize;
	}

	public void setDefaultPerPageSize(final String defaultPerPageSize) {
		this.defaultPerPageSize = defaultPerPageSize;
	}

	private BBBCatalogTools catalogToolsImpl;
	/**
	 * @return the catalogToolsImpl
	 */
	public BBBCatalogTools getCatalogToolsImpl() {
		return catalogToolsImpl;
	}
	/**
	 * @param catalogToolsImpl the catalogToolsImpl to set
	 */
	public void setCatalogToolsImpl(BBBCatalogTools catalogToolsImpl) {
		this.catalogToolsImpl = catalogToolsImpl;
	}

	/**
	 * @return the mCatalogToolsImpl
	 */

	private BBBConfigTools mCatalogTools;
	/**
	 * object for Coherence cache
	 */
	private BBBObjectCache mObjectCache;

	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * @param mObjectCache the mObjectCache to set
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
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
	 * @return the mCatalogTools
	 */
	public BBBConfigTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBConfigTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	private BBBConfigTools configTools;

	
	/**
	 * @return the configTools
	 */
	public BBBConfigTools getConfigTools() {
		return configTools;
	}
	/**
	 * @param configTools the configTools to set
	 */
	public void setConfigTools(BBBConfigTools configTools) {
		this.configTools = configTools;
	}
	/**
	 * Get results per page size
	 *  -If it is set in cookie return it
	 *  -Otherwise get from Configured highest value from config keys
	 * @param cookiePageSizeFilter
	 * @return
	 */
	private String getPagePerSize(String cookiePageSizeFilter){

		List<String> dropdownList = fetchPerPageDropdownList();

		String highest = this.getDefaultPerPageSize();

		if(dropdownList!=null){
			highest = dropdownList.get(dropdownList.size() - 1);
		}

		if(null !=cookiePageSizeFilter && !dropdownList.contains(cookiePageSizeFilter)){
			// If View All is selected then set Page Size to highest
			return highest;
		} else if( cookiePageSizeFilter==null && highest!=null){
			return highest;
		} else {
			return cookiePageSizeFilter;
		}

	}
	
	/**
	 * @param searchQuery
	 * @param l2L3BrandBoostingEnabled
	 * @param originOfTraffic
	 * @return
	 */
	public boolean checkL2L3BrandBoostingFlag(SearchQuery searchQuery,
			boolean l2L3BrandBoostingEnabled,
			final String originOfTraffic) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_checkL2L3BrandBoostingFlag");
		final String l2L3BoostingEnabled =  getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.L2L3_BOOST_FLAG, BBBCoreConstants.FALSE);
		final String brandBoostingEnabled =  getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.BRANDS_BOOST_FLAG, BBBCoreConstants.FALSE);
		boolean isl2L3BrandBoostingEnabled	=	l2L3BrandBoostingEnabled;
		logDebug("Start : BBBResultsListHandler.checkL2L3BrandBoostingFlag,searchQuery " + searchQuery.toString()
				+ " , l2L3BoostingEnabled =" + l2L3BoostingEnabled
				+ " , brandBoostingEnabled =" + brandBoostingEnabled
				+ " and originOfTraffic=" + originOfTraffic);
		if (BBBEndecaConstants.DEFAULT_0.equalsIgnoreCase(searchQuery.getSortString())
				&& searchQuery.getCatalogRef() != null){
					if(searchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_ID) == null){
						logDebug("No CategoryId found hence Repostitory call not executed and no L2/L3/Brand page boosting performed!");
					} else {
							if(originOfTraffic !=null && originOfTraffic.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)){
							if(Boolean.valueOf(l2L3BoostingEnabled) && searchQuery.getKeyWord() == null){
								isl2L3BrandBoostingEnabled = true;
							} else if(Boolean.valueOf(brandBoostingEnabled) && searchQuery.getCatalogRef().get(BBBEndecaConstants.FRM_BRAND_PAGE)!=null && searchQuery.getCatalogRef().get(BBBEndecaConstants.FRM_BRAND_PAGE).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
								isl2L3BrandBoostingEnabled = true;
							}
							} else {
								if(Boolean.valueOf(l2L3BoostingEnabled) && searchQuery.getKeyWord() == null && searchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_REF_ID) != null){
									isl2L3BrandBoostingEnabled = true;
								} else if(Boolean.valueOf(brandBoostingEnabled) && searchQuery.getCatalogRef().get(BBBEndecaConstants.FRM_BRAND_PAGE)!=null && searchQuery.getCatalogRef().get(BBBEndecaConstants.FRM_BRAND_PAGE).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
									isl2L3BrandBoostingEnabled = true;
								}
						}
					}
		}
		logDebug("End : BBBResultsListHandler.checkL2L3BrandBoostingFlag, isl2L3BrandBoostingEnabled: " + isl2L3BrandBoostingEnabled);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.RESULTS_LIST_HANDLER + "_checkL2L3BrandBoostingFlag");
		return isl2L3BrandBoostingEnabled;
	}
	

	/**
	 * Call Endeca to retrieve results of a popular term in L2 departments
	 *
	 * @param pFacetQueryResults
	 * @param topPopularTerm
	 * @param maxL2Departments
	 * @param pFacetQuery
	 * @param siteDimensionId
	 * @param cacheTimeout
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void populateSearchInDept(FacetQueryResults pFacetQueryResults,
			String topPopularTerm, FacetQuery pFacetQuery, String siteDimensionId, int cacheTimeout) 
					throws BBBSystemException, BBBBusinessException {

		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		int maxL2Departments = 	getMaxCountForCacheEntry(BBBCoreConstants.SEARCH_IN_DEPT_TYPE_AHEAD_SIZE,request);
		logDebug("Entering EndecaSearchUtil.populateSearchInDept method.");

		final String methodName = BBBCoreConstants.ENDECA_FACET_SEARCH_IN_DEPT;

		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,	methodName);

		logDebug("EndecaSearchUtil.populateSearchInDept() Maximum results to be fetched for Search within departments are : "
				+ maxL2Departments);
        try {
		if(maxL2Departments != 0 && !BBBUtility.isEmpty(topPopularTerm)) {

			final String L2deptCacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.POPULAR_KEYWORDS_CACHE_NAME).get(0);
			pFacetQueryResults.setTopPopularTerm(topPopularTerm);

			logDebug("EndecaSearchUtil.populateSearchInDept() Top popular term set in the second endeca query : " + topPopularTerm);

			String parameterId = BBBEndecaConstants.BLANK_STRING;
			
			parameterId = this.getL2DeptDimId().get(pFacetQuery.getSiteId());

			final UrlGen secondUrlGen = new UrlGen(null, getEndecaClient().getEncoding());
			if(!BBBUtility.isEmpty(pFacetQuery.getCatalogId())){
				secondUrlGen.addParam(BBBEndecaConstants.NAVIGATION, pFacetQuery.getCatalogId() + " " + siteDimensionId);
			} else {
				secondUrlGen.addParam(BBBEndecaConstants.NAVIGATION, siteDimensionId);
			}

			secondUrlGen.addParam(BBBEndecaConstants.NAV_KEYWORD,pFacetQueryResults.getTopPopularTerm());
			secondUrlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME,BBBEndecaConstants.ALL);
			// Added 2 parameters for BELL-17 defect to modify the query for phrases set up in Endeca.
			secondUrlGen.addParam(BBBEndecaConstants.NTPC,ONE);
			secondUrlGen.addParam(BBBEndecaConstants.NTPR,ONE);

			if(!BBBUtility.isEmpty(pFacetQuery.getCatalogId())){
				secondUrlGen.addParam(BBBEndecaConstants.NAV_REFINEMENT, pFacetQuery.getCatalogId() +" " +_8);
				secondUrlGen.addParam(BBBEndecaConstants.NAV_REFINEMENT_CONFIG,_ID+" " +pFacetQuery.getCatalogId()+" "+BBBEndecaConstants.DYNCOUNT+" "+_10+" "+BBBEndecaConstants.DYNRANK+" "+BBBEndecaConstants.ENABLED+BBBEndecaConstants.PIPE+_ID+" "+_8+" "+BBBEndecaConstants.DYNCOUNT+" "+_10);
			} else {
				secondUrlGen.addParam(BBBEndecaConstants.NAV_REFINEMENT,parameterId+" " +_8);
				secondUrlGen.addParam(BBBEndecaConstants.NAV_REFINEMENT_CONFIG,_ID+" " +parameterId+" "+BBBEndecaConstants.DYNCOUNT+" "+_10+BBBEndecaConstants.PIPE+_ID+" "+_8+" "+BBBEndecaConstants.DYNCOUNT+" "+_10);
			}

			logDebug("EndecaSearchUtil.populateSearchInDept():Second Endeca Query "
					+ "String to fetch results for search within departments : " + secondUrlGen.toString());

			FacetQueryResult pFacetQueryResult3 =new FacetQueryResult();
			pFacetQueryResult3 = (FacetQueryResult) getObjectCache().get(secondUrlGen.toString(), L2deptCacheName);

			if(pFacetQueryResult3 == null || pFacetQueryResult3.getMatches() == null
					|| pFacetQueryResult3.getMatches().isEmpty()){
				boolean categoryBoosted = false;
				//When no L1 selected
                if(BBBUtility.isEmpty(pFacetQuery.getCatalogId())) {
	                	
					List<String> ocbBoostingFlag = new ArrayList<String>();
						String omnitureL2BoostingEnabled = BBBCoreConstants.TRUE;
						
							try {
					ocbBoostingFlag = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_OCB_BOOSTING_FLAG);
							} catch (BBBSystemException | BBBBusinessException e) {
								vlogError("Exception while fetching ocb boosting flag ",e);
							}
							
	                if(!ocbBoostingFlag.isEmpty()) {
						
					omnitureL2BoostingEnabled = ocbBoostingFlag.get(0);
								
								vlogDebug("No L1 selected so fetching Omniture boosting L2s, omnitureL2BoostingEnabled flag value "+omnitureL2BoostingEnabled);
								
	            	//OCB boosting controlled from BCC
					if(BBBCoreConstants.TRUE.equalsIgnoreCase(omnitureL2BoostingEnabled)) {
						logDebug("EndecaSearchUtil.populateSearchInDept: fetching omniture boosted L2s");
						    categoryBoosted = populateL2BoostedSearchInDept(pFacetQueryResults, pFacetQuery, siteDimensionId, cacheTimeout,
						    		maxL2Departments, L2deptCacheName, topPopularTerm, secondUrlGen.toString());
						}
					}
                } 
                //if no category boosted or boosted is disabled then fetch suggestion based on product count
                if(!categoryBoosted) {
                	
                logDebug("EndecaSearchUtil.populateSearchInDept: fetch L2 based on product count");
				populateSearchInDept(pFacetQueryResults, pFacetQuery,siteDimensionId, cacheTimeout, maxL2Departments,
						L2deptCacheName, secondUrlGen.toString());
				
                }
			} else {
				logDebug("EndecaSearchUtil.populateSearchInDept:Search within departments results FOUND in its own endeca cache hence getting results from there");

				if(!BBBUtility.isListEmpty(pFacetQueryResults.getResults())
						&& BBBEndecaConstants.L2_DEPARTMENT.equalsIgnoreCase(pFacetQueryResults.getResults().get(0).getFacetName())) {
					pFacetQueryResults.getResults().remove(0);
					pFacetQueryResults.getResults().add(0,pFacetQueryResult3);
				}
				else if(pFacetQueryResults.getResults() != null){
					pFacetQueryResults.getResults().add(0,pFacetQueryResult3);
				}
			}
		}
        } finally {
        	BBBPerformanceMonitor.cancel(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
        }

		logDebug("Exiting EndecaSearchUtil.populateSearchInDept method.");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
	}
	
	/**
	 * Method to fetch L2 boosted categories for given search term.
	 * 
	 * @param facetQueryResults
	 * @param facetQuery
	 * @param siteDimensionId
	 * @param cacheTimeout
	 * @param maxL2Departments
	 * @param L2deptCacheName
	 * @param topPopularTerm
	 * @param l2DeptQuery
	 * @return l2boosted
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private boolean populateL2BoostedSearchInDept(FacetQueryResults facetQueryResults,
			FacetQuery facetQuery, String siteDimensionId, int cacheTimeout,
			int maxL2Departments, final String L2deptCacheName,
			String topPopularTerm,String l2DeptQuery) throws BBBBusinessException,
			BBBSystemException {
		
		logDebug("Enter EndecaSearchUtil.populateL2BoostedSearchInDept method.");
		final String methodName = BBBCoreConstants.POPULATE_L2_BOOSTED_SEARCH_IN_DEPT;

		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,	methodName);
		List<CategoryVO> omnitureBoostedL2s = fetchOmnitureBoostedL2s(topPopularTerm, siteDimensionId,null);
		boolean l2boosted = false ;
		
		if(omnitureBoostedL2s != null && !omnitureBoostedL2s.isEmpty()) {
			
			l2boosted = true;
			//Add record type=Product in query for search in department suggestions
			String productRecTypeDimID = getCatalogId(BBBEndecaConstants.RECORD_TYPE, BBBEndecaConstants.PROD_RECORD_TYPE);
			String pagePerSize = this.getPagePerSize(facetQuery.getPageFilterSize());
			final Map<String,String> matchTree = new LinkedHashMap<String, String>();
			FacetQueryResult facetQueryL2Results = new FacetQueryResult();
			
			for (CategoryVO categoryVO : omnitureBoostedL2s) {
				if(matchTree.size() < maxL2Departments){
					String pageURL = productRecTypeDimID+BBBEndecaConstants.DASH_LITERAL+siteDimensionId+BBBEndecaConstants.DASH_LITERAL+categoryVO.getCategoryId()+BBBEndecaConstants.CI_CONTENT_PATH_SEPARATOR+BBBEndecaConstants.DEFAULT_PAG_NUM +BBBEndecaConstants.DASH_LITERAL+pagePerSize;
					logDebug("Search within departments results,Creating L2 boosted suggestions with dimValID="+pageURL+" and name"+categoryVO.getCategoryName());
					 matchTree.put(String.valueOf(pageURL), categoryVO.getCategoryName());
				}
			}
			
			facetQueryL2Results.setMatches(matchTree);
			facetQueryL2Results.setFacetName(BBBEndecaConstants.L2_DEPARTMENT);
			
			if(!BBBUtility.isListEmpty(facetQueryResults.getResults())
					&& BBBEndecaConstants.L2_DEPARTMENT.equalsIgnoreCase(facetQueryResults.getResults().get(0).getFacetName())) {
				facetQueryResults.getResults().remove(0);
				facetQueryResults.getResults().add(0,facetQueryL2Results);
			} else if (facetQueryResults.getResults() != null) {
				facetQueryResults.getResults().add(0,facetQueryL2Results);
			}

			if(!facetQueryL2Results.getMatches().isEmpty()){
				getObjectCache().put(l2DeptQuery, facetQueryL2Results, L2deptCacheName, cacheTimeout);
			}

		}
		
		logDebug("Exiting EndecaSearchUtil.populateL2BoostedSearchInDept method. L2 boosted "+l2boosted);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,	methodName);
		return l2boosted;
	}
	
	/**
	 * @param facetQueryResults
	 * @param facetQuery
	 * @param siteDimensionId
	 * @param cacheTimeout
	 * @param maxL2Departments
	 * @param L2deptCacheName
	 * @param l2DeptQuery
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void populateSearchInDept(FacetQueryResults facetQueryResults,
			FacetQuery facetQuery, String siteDimensionId, int cacheTimeout,
			int maxL2Departments, final String L2deptCacheName,
			String l2DeptQuery) throws BBBBusinessException,
			BBBSystemException {
		FacetQueryResult pFacetQueryResult3;
				logDebug("EndecaSearchUtil.populateSearchInDept() Search within departments results NOT FOUND in its own endeca cache hence firing endeca query");
		String dimensionForSpecificL2 = BBBEndecaConstants.BLANK_STRING;
		String dimensionForAllL2 = BBBEndecaConstants.BLANK_STRING;

		dimensionForSpecificL2 = this.getDepartmentConfig().get(facetQuery.getSiteId());
		dimensionForAllL2 = this.getL2DepartmentConfig().get(facetQuery.getSiteId());
		
		//flag to indicate that all department was selected in L1 dropdown in typeaheadsearch
		boolean allDepttSelected = false;

		//Add record type=Product in query for search in department suggestions
		String productRecTypeDimID = getCatalogId(BBBEndecaConstants.RECORD_TYPE, BBBEndecaConstants.PROD_RECORD_TYPE);
		String pagePerSize = this.getPagePerSize(facetQuery.getPageFilterSize());
		
				try{
					ENEQuery navQueryObject = null;
			navQueryObject = new UrlENEQuery(l2DeptQuery, getQueryGenerator().getEncoding());
					navQueryObject.setNavNumERecs(0);
					pFacetQueryResult3 = new FacetQueryResult();
					// Execute Endeca Query
					ENEQueryResults navigationalResults = getEndecaClient().executeQuery(navQueryObject);
					if(navigationalResults != null && navigationalResults.containsNavigation()){
						DimValList dimvalList = null;
						final Map<String,String> matchTree = new LinkedHashMap<String, String>();

						DimensionList dimenList = navigationalResults.getNavigation().getCompleteDimensions();
						if(dimenList != null && !dimenList.isEmpty()){

							if(!BBBUtility.isEmpty(facetQuery.getCatalogId()) && dimenList.getDimension(dimensionForSpecificL2) != null){
								dimvalList = dimenList.getDimension(dimensionForSpecificL2).getRefinements();
							} else if (BBBUtility.isEmpty(facetQuery.getCatalogId()) && dimenList.getDimension(dimensionForAllL2) != null){
								dimvalList = dimenList.getDimension(dimensionForAllL2).getRefinements();
								allDepttSelected = true;
							}

							if(dimvalList != null && !dimvalList.isEmpty()) {
								final ListIterator iterRoots = dimvalList.listIterator();
								pFacetQueryResult3.setFacetName(BBBEndecaConstants.L2_DEPARTMENT);

								String pDimValId = null;
								while (iterRoots.hasNext()) {
									final DimVal dimVal = (DimVal) iterRoots.next();
									long dimValID = dimVal.getId();
									boolean phantom=false;
									//When All Department was selected, read dimValueId from Properties
									if(allDepttSelected && dimVal.getProperties()!=null){
										pDimValId = (String)(dimVal.getProperties().get(BBBEndecaConstants.NODE_ID));

										if(null !=pDimValId) {
											logDebug("EndecaSearchUtil.populateSearchInDept(): All was selected in dropdown, "
													+ "Retrieved id from NODE_ID properties>>"+pDimValId);
											dimValID = Long.parseLong(pDimValId);
											//PS-61408 |Phantom Flag Failing to Control Dept. Facet - HOLIDAY Impacting
											CategoryVO category = getCatalogToolsImpl().getCategoryDetail(SiteContextManager.getCurrentSiteId(),pDimValId , false);
											logDebug("site Id is ::" + SiteContextManager.getCurrentSiteId());
											if(null != category && null!=category.getPhantomCategory() && category.getPhantomCategory())
											{
												logDebug("category ID is ::" + category.getCategoryId());
												phantom=true;
											}
										}
									}

									String name = dimVal.getName();
									if(matchTree.size() < maxL2Departments){
										if(!phantom){
											String navIdWithPageNumSize = productRecTypeDimID+BBBEndecaConstants.DASH_LITERAL+siteDimensionId+BBBEndecaConstants.DASH_LITERAL+dimValID+"/"+BBBEndecaConstants.DEFAULT_PAG_NUM +BBBEndecaConstants.DASH_LITERAL+pagePerSize;
											logDebug("Search within departments results,Creating L2 suggestions with dimValID="+navIdWithPageNumSize+" and name"+name);

											matchTree.put( String.valueOf(navIdWithPageNumSize), name);
										}
									} else {
										break;
									}
								}
								pFacetQueryResult3.setMatches(matchTree);
								if(!BBBUtility.isListEmpty(facetQueryResults.getResults())
										&& BBBEndecaConstants.L2_DEPARTMENT.equalsIgnoreCase(facetQueryResults.getResults().get(0).getFacetName())) {
									facetQueryResults.getResults().remove(0);
									facetQueryResults.getResults().add(0,pFacetQueryResult3);
								}
								else if (facetQueryResults.getResults() != null) {
									facetQueryResults.getResults().add(0,pFacetQueryResult3);
								}

								if(!pFacetQueryResult3.getMatches().isEmpty()){
									getObjectCache().put(l2DeptQuery, pFacetQueryResult3, L2deptCacheName, cacheTimeout);
									vlogDebug("Fetched result from endeca put into cache");
								}
							} else {
								if(!BBBUtility.isListEmpty(facetQueryResults.getResults())
										&& BBBEndecaConstants.L2_DEPARTMENT.equalsIgnoreCase(facetQueryResults.getResults().get(0).getFacetName())) {
									facetQueryResults.getResults().remove(0);
								}
							}
						}
					}
				}
				catch(ENEQueryException e){
					BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQueryException in calling Endeca populateSearchInDept");
					BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, "populateSearchInDept");
					throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"Endeca Exception",e);
				}
				}

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
		String channelThemeId = null;

		String siteId = SiteContextManager.getCurrentSiteId();
		try {

			logDebug("Added new condition for Search by Brand");

			if(null != ServletUtil.getCurrentRequest()){
				channelId = ServletUtil.getCurrentRequest().getHeader("X-bbb-channel");
				if(BBBUtility.isNotEmpty(channelId)){
					logDebug("Channel Id "+channelId);
				}
				channelThemeId = ServletUtil.getCurrentRequest().getHeader("X-bbb-channel-theme");
				if(BBBUtility.isNotEmpty(channelThemeId)){
					logDebug("Channel Id "+channelThemeId);
				}
			}

			if(parentName != null && parentName.equalsIgnoreCase("Brand")){
				final String siteDimId = getCatalogId("Site_ID",getSiteIdMap().get(SiteContextManager.getCurrentSiteId()));
				endecaQuery = new UrlENEQuery(BBBEndecaConstants.NAVIGATION+"="+siteDimId,getEndecaClient().getEncoding());
				cacheKeyForEndeca = BBBEndecaConstants.NAVIGATION+"="+siteDimId;
				logDebug("Search is by brand. So Endeca query is : "+endecaQuery);
			} else {
				endecaQuery = new UrlENEQuery(BBBEndecaConstants.NAVIGATION+"=0",getEndecaClient().getEncoding());
				cacheKeyForEndeca = BBBEndecaConstants.NAVIGATION+"=0";
				logDebug("Search is not by brand. So endeca query is : "+endecaQuery);
			}

			endecaQuery.setNavAllRefinements(true);
			if(BBBUtility.isNotEmpty(channelId)){
				if (BBBUtility.isNotEmpty(channelThemeId)) {
					cacheKeyForEndeca = cacheKeyForEndeca + "AllNavRef_" + channelId + "_" + channelThemeId + "_" + siteId;
				} else {
				cacheKeyForEndeca = cacheKeyForEndeca + "AllNavRef_" + channelId + "_" + siteId;
				}
			}else {
				cacheKeyForEndeca = cacheKeyForEndeca + "AllNavRef_" + siteId;
			}
			logDebug("cache key:" + BBBCoreConstants.PRODUCT_DIM_ID_CACHE_NAME);
			if(parentName != null && parentName.equalsIgnoreCase("MerchandisingPages")){
				logDebug("Search is by MerchandisingPages for AllBrands, Endeca query is : "+endecaQuery + " Cache key is :" + cacheKeyForEndeca);
			}

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
				if (BBBUtility.isNotEmpty(channelThemeId)) {
					logDebug("EndecaSearch.getCatalogId - Looking into " + cacheName +  " Cache for the value for key: " + (parentName+"+"+dimName+"+"+channelId+"+"+channelThemeId+"+"+siteId));
					dimensionId = (String) getObjectCache().get(parentName+"+"+dimName+"+"+channelId+"+"+channelThemeId+"+"+siteId, cacheName);
				} else {
				logDebug("EndecaSearch.getCatalogId - Looking into " + cacheName +  " Cache for the value for key: " + (parentName+"+"+dimName+"+"+channelId+"+"+siteId));
				dimensionId = (String) getObjectCache().get(parentName+"+"+dimName+"+"+channelId+"+"+siteId, cacheName);
				}

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
											if (BBBUtility.isNotEmpty(channelThemeId)) {
												getObjectCache().put(parentName+"+"+dimName+"+"+channelId+"+"+channelThemeId+"+"+siteId, dimensionId, cacheName, cacheTimeout);
											} else {
											getObjectCache().put(parentName+"+"+dimName+"+"+channelId+"+"+siteId, dimensionId, cacheName, cacheTimeout);
											}
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
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery Exception ", endecaException);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
		if(parentName != null && parentName.equalsIgnoreCase("MerchandisingPages")){
			logDebug("Search is by MerchandisingPages for AllBrands, Product Dimension Id is : "+ dimensionId);
		}
		logDebug("Product Dimension Id: " +dimensionId);
		logDebug("Exit EndecaSearch.getCatalogId method.");
		return dimensionId;
	}

	@SuppressWarnings("rawtypes")
	public synchronized BBBQueryResults getEndecaResults(String cacheKeyForEndeca, ENEQuery endecaQuery, String cacheName,
			int cacheTimeout) throws BBBSystemException{

		logDebug("EndecaSearch.getEndecaResults() :: Querying results from Endeca for cacheKey: " + cacheKeyForEndeca + " Cache Name: " + cacheName);
		BBBQueryResults results = (BBBQueryResults) getObjectCache().get(cacheKeyForEndeca, cacheName);
		try {
			if(results == null){
				logDebug("EndecaSearch:: Results Not Found in cache, Cache Name " + cacheName + "cacheKey: " + cacheKeyForEndeca);
				ENEQueryResults endecaresults = getEndecaClient().getEneConnection().query(endecaQuery);
				results = new BBBQueryResults();
				results.setContainsNavigation(endecaresults.containsNavigation());

				List<BBBDimension> bbbDimList = new ArrayList<BBBDimension>();
				DimensionList dimList = endecaresults.getNavigation().getRefinementDimensions();
				final Iterator dimIterator =  dimList.listIterator();
				BBBDimension bbbDim = null;
				while(dimIterator.hasNext()){
					bbbDim = new BBBDimension();
					final Dimension rootDim = (Dimension) dimIterator.next();
						bbbDim.setRootDimName(rootDim.getName());
						bbbDim.setRootDimId(""+rootDim.getId());
						DimValList childList = rootDim.getRefinements();
						List<BBBDimVal> dimValList = new ArrayList<BBBDimVal>();
						final ListIterator childIter = childList.listIterator();
						while(childIter.hasNext()){
							DimVal childDimVal = (DimVal) childIter.next();
							BBBDimVal dimVal = new BBBDimVal();
							dimVal.setDimValName(childDimVal.getName());
							dimVal.setDimValId(childDimVal.getId());
							dimValList.add(dimVal);
						}
						bbbDim.setDimValList(dimValList);
						bbbDimList.add(bbbDim);
					}

					results.setDimList(bbbDimList);
					getObjectCache().put(cacheKeyForEndeca, results, cacheName, cacheTimeout);
				}
			else{
				logDebug("EndecaSearch:: Results found in cache, Cache Name " + cacheName);
			}
		} catch (ENEQueryException endecaException) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQueryException in calling Endeca performSearch");
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery Exception ", endecaException);
		}

		return results;
	}


	/**
	 * Get size of items to be cached for type-ahead bucket
	 * Bucket can be 'Department, 'SearchInDepartment', 'Brand', 'Popular terms'  
	 * @param pBucketName
	 * @param pRequest
	 * @return
	 */
	public int getMaxCountForCacheEntry(String pBucketName,DynamoHttpServletRequest pRequest){

		logDebug("Entering EndecaSearchUtil.getMaxCountForCacheEntry() method [START]");
		int cacheEntrySize;
		try {
			cacheEntrySize = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS ,BBBEndecaConstants.MAX_MATCHES).get(0));

			// For SiteSpect Purpose -Caching size is increased. This can disable increased caching.
			String enableMaxCachingForSiteSpect = BBBCoreConstants.TRUE;
			try {
				enableMaxCachingForSiteSpect = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS ,ENABLE_MAX_CACHING_FOR_SITESPECT).get(0);
			} catch (BBBBusinessException excep){
				logError("Business exception while fetching config key to store type ahead cache in EndecaSearch.performFacetSearch() method hence making it to TRUE");
			}catch (BBBSystemException excep){
				logError("System exception while fetching config key to store type ahead cache in EndecaSearch.performFacetSearch() method hence making it to TRUE");
			}

			if(Boolean.parseBoolean(enableMaxCachingForSiteSpect.toLowerCase())){
				logDebug("The caching for type ahead values is enabled hence fetching the values from BCC for the respective bucket");
				if(pBucketName !=null && pBucketName.equalsIgnoreCase(BBBCoreConstants.SEARCH_IN_DEPT_TYPE_AHEAD_SIZE)){
					cacheEntrySize = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS ,BBBEndecaConstants.MAX_L2_DEPT_COUNT).get(0));
				} else if(pBucketName !=null && pBucketName.equalsIgnoreCase(BBBCoreConstants.BRAND_TYPE_AHEAD_SIZE)){
					cacheEntrySize = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS ,BBBEndecaConstants.MAX_BRANDS_COUNT).get(0));
				} else if(pBucketName !=null && pBucketName.equalsIgnoreCase(BBBCoreConstants.DEPT_TYPE_AHEAD_SIZE)){
					cacheEntrySize = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS ,BBBEndecaConstants.MAX_DEPARTMENT_COUNT).get(0));
				} else if(pBucketName !=null && pBucketName.equalsIgnoreCase(BBBCoreConstants.POPULAR_TYPE_AHEAD_SIZE)){
					cacheEntrySize = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS ,BBBEndecaConstants.MAX_POPULAR_ITEMS_COUNT).get(0));
				}
			} else{
				logDebug("The caching for type ahead values is disabled hence fetching the default values of the type ahead bucket");
				cacheEntrySize = getDefaultTypeAheadBucketSize(pBucketName);
			}
		} catch (BBBSystemException e) {
			logError("System exception occurred while fetching the maximum cap size for bucket [" +pBucketName + "]" + " hence setting it to default size cap");
			cacheEntrySize = Integer.parseInt(getTypeAheadMaxCacheSizeMap().get(pBucketName));
		} catch (BBBBusinessException e) {
			logError("Business exception occurred while fetching the maximum cap size for bucket [" +pBucketName + "]" + " hence setting it to default size cap");
			cacheEntrySize = Integer.parseInt(getTypeAheadMaxCacheSizeMap().get(pBucketName));
		}
		logDebug("The maximum cap size configured for the bucket [" + pBucketName+ "] is [" + cacheEntrySize + "]");
		logDebug("Exiting EndecaSearchUtil.getMaxCountForCacheEntry() method [END]");
		return cacheEntrySize;
	}
	
	/**
	 * Put cap/limit on Endeca results so that desired count are displayed on
	 * each bucket of L2Departments, Brands, Popular Terms and Departments for
	 * Desktop. It will fetch the parameters from the sitespec and then put
	 * a upper cap/ limit on the results as per the cap 
	 * @param pFacetQueryResults
	 * @return 
	 */
	public FacetQueryResults doCapCacheResults(FacetQueryResults pFacetQueryResults){
		
		logDebug("Entering EndecaSearchUtil.doCapCacheResults() method [START]");
		if(pFacetQueryResults ==null){
			logDebug("MTHD=[doCapCacheResults] MSG=[Input pFacetQueryResults is null]");
			return null;
		}
		
		FacetQueryResults finalFacetQueryResults = new FacetQueryResults(); 
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, "doCapCacheResults");

		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		
		long timeStart = System.currentTimeMillis();
		
		//variables for each bucket max size on type-ahead
		int depttMaxCap= 0;
		int brandMaxCap= 0;
		int popularMaxCap= 0;
		int l2DeparttsMaxCap = 0;

		//get count to print on type-ahead for department bucket
		depttMaxCap = getTypeAheadBucketSize(BBBCoreConstants.DEPT_TYPE_AHEAD_SIZE, request);
		
		//get count to print on type-ahead for brand bucket
		brandMaxCap = getTypeAheadBucketSize(BBBCoreConstants.BRAND_TYPE_AHEAD_SIZE, request);
		
		//get count to print on type-ahead for popular bucket
		popularMaxCap = getTypeAheadBucketSize(BBBCoreConstants.POPULAR_TYPE_AHEAD_SIZE, request);
		
		//get count to print on type-ahead for L2 department bucket
		l2DeparttsMaxCap = getTypeAheadBucketSize(BBBCoreConstants.SEARCH_IN_DEPT_TYPE_AHEAD_SIZE, request);
		
		List<FacetQueryResult> results = pFacetQueryResults.getResults();
		List<FacetQueryResult> finalResults = new ArrayList<FacetQueryResult>(); 
		
		Map<String,String> capedResultMap = null;
		String brandFacetName = BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE, EndecaSearch.BRAND_KEY);
		if(results != null){
			for(FacetQueryResult result:results){
				FacetQueryResult queryResult = new FacetQueryResult(); 
				if(result !=null && result.getFacetName()!=null && result.getFacetName().equalsIgnoreCase(BBBEndecaConstants.DEPARTMENT)){
					logDebug("MTHD=[doCapCacheResults] MSG=[OriginalMap for department bucket="+result.getMatches());
					capedResultMap = doCapTypeAheadResultsByCount(result.getMatches(),depttMaxCap);
					queryResult.setMatches(capedResultMap);
					queryResult.setFacetName(result.getFacetName());
					finalResults.add(queryResult);
					logDebug("MTHD=[doCapCacheResults] MSG=[capedResultMap for department bucket="+capedResultMap);
					
				}else if(result !=null && result.getFacetName()!=null && result.getFacetName().equalsIgnoreCase(brandFacetName)){
					logDebug("MTHD=[doCapCacheResults] MSG=[OriginalMap for brands bucket="+result.getMatches());
					capedResultMap = doCapTypeAheadResultsByCount(result.getMatches(),brandMaxCap);
					queryResult.setMatches(capedResultMap);
					queryResult.setFacetName(result.getFacetName());
					finalResults.add(queryResult);
					logDebug("MTHD=[doCapCacheResults] MSG=[capedResultMap for brands bucket="+capedResultMap);
					
				}else if(result !=null && result.getFacetName()!=null && result.getFacetName().equalsIgnoreCase(BBBEndecaConstants.POPULAR)){
					logDebug("MTHD=[doCapCacheResults] MSG=[OriginalMap for popular bucket="+result.getMatches());
					capedResultMap = doCapTypeAheadResultsByCount(result.getMatches(),popularMaxCap);
					queryResult.setMatches(capedResultMap);
					queryResult.setFacetName(result.getFacetName());
					finalResults.add(queryResult);
					logDebug("MTHD=[doCapCacheResults] MSG=[capedResultMap for popular bucket="+capedResultMap);
					
				}else if(result !=null && result.getFacetName()!=null && result.getFacetName().equalsIgnoreCase(BBBEndecaConstants.L2_DEPARTMENT)){
					logDebug("MTHD=[doCapCacheResults] MSG=[OriginalMap for L2 department bucket="+result.getMatches());
					capedResultMap = doCapTypeAheadResultsByCount(result.getMatches(), l2DeparttsMaxCap);
					queryResult.setMatches(capedResultMap);
					queryResult.setFacetName(result.getFacetName());
					finalResults.add(queryResult);
					logDebug("MTHD=[doCapCacheResults] MSG=[capedResultMap for L2 department bucket="+capedResultMap);
				}
			}
		}
		
		finalFacetQueryResults.setResults(finalResults);
		long endTime = System.currentTimeMillis();
		
		logDebug("MTHD=[doCapCacheResults] MSG=[Total time taken="+(endTime-timeStart));
		logDebug("Exiting EndecaSearchUtil.doCapCacheResults() method [END]");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, "doCapCacheResults");
		return finalFacetQueryResults;
	}
	
	
	/**
	 * Put cap/limit on Endeca results so that desired count are displayed on
	 * each bucket of Popular Terms. This call is only for mobile as we show
	 * only popular terms for mobile.
	 * @param pFacetQueryResults
	 */
	public void doCapCacheResultsMobile(FacetQueryResults pFacetQueryResults) {

		if(pFacetQueryResults ==null){
			logDebug("MTHD=[doCapCacheResults] MSG=[Input pFacetQueryResults is null]");
			return;
		}

		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, "doCapCacheResults");

		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

		long timeStart = System.currentTimeMillis();

		//variables for each bucket max size on type-ahead
		int popularMaxCap= 0;

		//get count to print on type-ahead for popular bucket
		popularMaxCap = getTypeAheadBucketSize(BBBCoreConstants.POPULAR_TYPE_AHEAD_SIZE, request);

		List<FacetQueryResult> results = pFacetQueryResults.getResults();
		Map<String,String> capedResultMap = null;

		if(results != null){
			for(FacetQueryResult result:results){
				if(result !=null && result.getFacetName()!=null && result.getFacetName().equalsIgnoreCase(BBBEndecaConstants.POPULAR)){
					logDebug("MTHD=[doCapCacheResults] MSG=[OriginalMap for popular bucket="+result.getMatches());
					capedResultMap = doCapTypeAheadResultsByCount(result.getMatches(),popularMaxCap);
					result.setMatches(capedResultMap);
					logDebug("MTHD=[doCapCacheResults] MSG=[capedResultMap for popular bucket="+capedResultMap);

				}
			}
		}
		long endTime = System.currentTimeMillis();

		logDebug("MTHD=[doCapCacheResults] MSG=[Total time taken="+(endTime-timeStart));

		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, "doCapCacheResults");
	}
	
	/**
	 * Generic method to return either siteSpect value or BCC driven for a bucket.
	 * Bucket name can be "popular", 'department', 'brands', or "L2departments'
	 * @param bucketSiteSpectName
	 * @param pRequest
	 * @return
	 */
	private int getTypeAheadBucketSize(String bucketSiteSpectName, DynamoHttpServletRequest pRequest){

		//size of bucket to print on front end
		int printTypeAheadBucketSize = 0;
		String bucketSiteSpectValue = pRequest.getParameter(bucketSiteSpectName);
		
		logDebug("MTHD=[getTypeAheadBucketSize] MSG=[SiteSpect Values for "+bucketSiteSpectName
				+ "="+bucketSiteSpectValue+"}" );
		
		if(BBBUtility.isEmpty(bucketSiteSpectValue)){
			String bucketKey = getTypeAheadBucketsSizeMap().get(bucketSiteSpectName);
			printTypeAheadBucketSize = getTypeAheadMaxMatches(bucketKey);

			logDebug("MTHD=[getTypeAheadBucketSize] MSG=[SiteSpect Values is null, picked value from BCC using key= "
					+ "="+bucketKey+"} value="+printTypeAheadBucketSize);
		}else{
			try{
				printTypeAheadBucketSize = Integer.parseInt(bucketSiteSpectValue);
			}catch (NumberFormatException e) {
				//take default value
				printTypeAheadBucketSize = getMaxMatches();
				logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
			}
		}
		
		return printTypeAheadBucketSize;
	}
	
	/**
	 * Generic method to return either siteSpect value or BCC driven for a bucket.
	 * Bucket name can be "popular", 'department', 'brands', or "L2departments'
	 * @param bucketSiteSpectName
	 * @param pRequest
	 * @return
	 */
	private int getDefaultTypeAheadBucketSize(String bucketSiteSpectName) {

		//size of bucket to print on front end
		int printTypeAheadBucketSize = 0;

		String bucketKey = getTypeAheadBucketsSizeMap().get(bucketSiteSpectName);
		printTypeAheadBucketSize = getTypeAheadMaxMatches(bucketKey);;

		logDebug("MTHD=[getTypeAheadBucketSize] MSG=[SiteSpect Values is null, picked value from BCC using key= "
				+ "="+bucketKey+"} value="+printTypeAheadBucketSize);

		return printTypeAheadBucketSize;
	}
	
	
	
	
	/**
	 * Create sub map of result map based on capCount
	 * 
	 * @param resultMap
	 * @param capCount count to limit entry in result
	 */
	private Map<String, String> doCapTypeAheadResultsByCount(final Map<String, String> resultMap,
			final int capCount){
		
		Map<String,String> capedResultMap =null;
		
		if(resultMap instanceof LinkedHashMap){
			capedResultMap = new LinkedHashMap<String, String>();
		}else if(resultMap instanceof TreeMap){
			capedResultMap = new TreeMap<String, String>();
		} else{
			capedResultMap = new HashMap<String, String>();
		}
		
		int count = 0;
		if(resultMap !=null ){
		
			Iterator<Map.Entry<String,String>> resultIterator = (Iterator<Map.Entry<String,String>>)resultMap.entrySet().iterator();
			while (resultIterator.hasNext() && count < capCount){
				Map.Entry<String,String> keyValueEntry = (Map.Entry<String,String>)resultIterator.next();
				capedResultMap.put(keyValueEntry.getKey(), keyValueEntry.getValue());
				count++;
			}
		}
		return capedResultMap;
	}
	
	/**
	 * Get BCC configured count for type-ahead bucket
	 * @param typeAheadKey
	 * @return
	 */
	public int getTypeAheadMaxMatches(String typeAheadKey) {

		int matches = getMaxMatches();
		try{
			if(null!=getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,typeAheadKey) 
					&& null!=getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,typeAheadKey).get(0)){
				matches = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS,typeAheadKey).get(0));
			} 
		}catch (NumberFormatException e) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
		}catch (BBBBusinessException e) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
		} catch (BBBSystemException e) {
			logError(BBBCoreErrorConstants.CATALOG_ERROR_1014, e);
		}
		return matches;
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


	//Property to hold siteIds Map.
	private Map<String, String> siteIdMap;

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
	 * @return productCacheTimeout
	 */
	public int getProductCacheTimeout() {
		return this.productCacheTimeout;
	}

	/**
	 * @param productCacheTimeout
	 */
	public void setProductCacheTimeout(final int productCacheTimeout) {
		this.productCacheTimeout = productCacheTimeout;
	}


	/**
	 * Fetch configured per page dropdown list
	 * @return
	 */
	public List<String> fetchPerPageDropdownList()
	{
		logDebug("SearchManager.fetchPerPageDropdownList : START");
		List<String> dropdownList = null;

		try {

			List<String> per_page_dropdown = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PER_PAGE_DROPDOWN);
			if(per_page_dropdown != null && !per_page_dropdown.isEmpty()){
				String[] dropdown = per_page_dropdown.get(0).split(BBBCoreConstants.COMMA);
				dropdownList = new ArrayList<String>(Arrays.asList(dropdown));
				logDebug("List of page size configured in BCC:" + dropdownList);
			}

		} catch (BBBSystemException e) {
			logError("SearchManager.fetchPerPageDropdownList():: System Exception thrown while fetching per page dropdown list from config key",e);
		} catch (BBBBusinessException e) {
			logError("SearchManager.fetchPerPageDropdownList():: Business Exception thrown while fetching per page dropdown list from config key",e);
		}

		logDebug("SearchManager.fetchPerPageDropdownList : END");
		return dropdownList;
	}

	//Property to hold L2 Department Dimension Name Map.
	private Map<String, String> l2DepartmentConfig;

	//Property to hold Department Dimension Name Map.
	private Map<String, String> departmentConfig;

	//Property to hold DimId for L2DeptDimension Dimensions
	private Map<String, String> l2DeptDimId;

	public Map<String, String> getL2DepartmentConfig() {
		return this.l2DepartmentConfig;
	}

	public void setL2DepartmentConfig(final Map<String, String> l2DepartmentConfig) {
		this.l2DepartmentConfig = l2DepartmentConfig;
	}

	public Map<String, String> getL2DeptDimId() {
		return this.l2DeptDimId;
	}

	public void setL2DeptDimId(final Map<String, String> l2DeptDimId) {
		this.l2DeptDimId = l2DeptDimId;
	}

	/** @return departmentConfig
	 */
	public Map<String, String> getDepartmentConfig() {
		return this.departmentConfig;
	}

	/**
	 * @param departmentConfig
	 */
	public void setDepartmentConfig(final Map<String, String> departmentConfig) {
		this.departmentConfig = departmentConfig;
	}
		// Property to limit the number of Departments/Brands in TypeAhead Flyout to.
	private int maxMatches;

	public int getMaxMatches() {
		return this.maxMatches;
	}

	public void setMaxMatches(final int maxMatches) {
		this.maxMatches = maxMatches;
	}

	/** Map for type-ahead bucket that contains BCC key name for its size value */
	private Map<String, String> typeAheadBucketsSizeMap;

	/**
	 * Getter for typeAheadBucketsSizeMap
	 * @return
	 */
	public Map<String, String> getTypeAheadBucketsSizeMap() {
		return typeAheadBucketsSizeMap;
	}

	/**
	 * Setter for typeAheadBucketsSizeMap
	 * @param typeAheadBucketsSizeMap
	 */
	public void setTypeAheadBucketsSizeMap(
			Map<String, String> typeAheadBucketsSizeMap) {
		this.typeAheadBucketsSizeMap = typeAheadBucketsSizeMap;
	}
	/**
	 * 
	 * Extracts facet ref filter from navigation state's n values and assigns to facetRefinement
	 * Extracts catalog ID by comparing current navigation state with refinement nav state and assigns to facetRefinement
	 * @param facetRefinement
	 * @param navigationState
	 * @return boolean to indicate whether to add refinment or not
	 */
	public static boolean assignFacetRefFilterAndCatalogId(
			FacetRefinementVO facetRefinement, String navigationState, SearchQuery pSearchQuery) throws UrlFormatException {
		if(navigationState.startsWith(QUESTION_MARK)) {
			navigationState = navigationState.substring(1);
		}
		
		String nValue = getParameter(navigationState, NAVIGATION);
		if(BBBUtility.isEmpty(nValue)) {
			return false;
		}
		facetRefinement.setFacetRefFilter(nValue.replaceAll(" ","-"));
		
		String catalogId = getCatalogIdUsingRequestQuery(nValue,false, pSearchQuery);
		if(BBBUtility.isEmpty(catalogId)) {
			return false; 
		} 
		facetRefinement.setCatalogId(catalogId);
		return true;
	}
	/**
	 * 
	 * method for extracting nvalue from query 
	 * this is used for forming remove link of descriptor and selection link of facets
	 * @param navigationState
	 * @return
	 */
	public static String getParameter(String navigationState, String parameterName) throws UrlFormatException {
		BasicUrlFormatter urlFormatter =  new BasicUrlFormatter();
		UrlState urlState = urlFormatter.parseRequest(navigationState, URL_ENCODING);
		String nValue = urlState.getParam(parameterName);
		return nValue;
	}
	/**
	 * 
	 * method to extract catalog ID to be used for refinements and descriptors of current refinement
	 * this method uses query string extracted from endecaqueryvo
	 * extractFromQuery indicates whether querystring would have complete n value list 
	 * @param nValue
	 * @param extractFromQuery
	 * @return
	 */
	public static String getCatalogIdUsingRequestQuery(String nValue,boolean extractFromQuery, SearchQuery pSearchQuery) throws UrlFormatException {
		//catalogId is required for forming descriptor links & facet links
		String currentQueryString = null;
		if(null != pSearchQuery) {
			currentQueryString = pSearchQuery.getQueryURL();
		} else {
			currentQueryString = ServletUtil.getCurrentRequest().getQueryString();
		}
		
		//ServletUtil.getCurrentRequest().getQueryString();
		
		if(null != currentQueryString) {
			String currentNavigationState = currentQueryString.toString();
			
			if(null != getParameter(currentNavigationState,NAVIGATION)) {
				Set<String> navStateFromRefinement = new HashSet<String>(Arrays.asList(nValue.split(" ")));
				Set<String> navState = new HashSet<String>(Arrays.asList(getParameter(currentNavigationState,NAVIGATION).split(" ")));
				//check if refinement nav state would include catalogid to be extracted
				if(!extractFromQuery) {
					//extracting catalogid for facet
					navStateFromRefinement.removeAll(navState);
					
					//remove null and 0 if they exist
					navStateFromRefinement.remove(null);
					navStateFromRefinement.remove(""+0);
					
					if(navStateFromRefinement.size() == 1) {
						//getting the first element
						return navStateFromRefinement.iterator().next();
					} 
				} else {
					//extracting catalogid for descriptor
					navState.removeAll(navStateFromRefinement);
					
					//remove null and 0 if they exist
					navState.remove(null);
					navState.remove(""+0);
					
					if(navState.size() == 1) {
						//getting the first element
						return navState.iterator().next();
					}
				}
	
			}
		}		
	
		return "";
	}
	
	/*public static String getCurrentSiteId(NavigationState pNavState){
		if(null != pNavState){
			return pNavState.getParameter(BBBCoreConstants.SITE_ID);
		}
		return null;
	}*/
	/**
	 * @param pSearchQuery
	 * @return
	 */
	public static boolean isCategoryPageRequest(SearchQuery pSearchQuery){
		if(null != pSearchQuery && !pSearchQuery.isHeaderSearch() && null!=pSearchQuery.getCatalogRef() && null!=pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_REF_ID)){
			return true;
		}
		return false;
	}
	/**
	 * @param pSearchQuery
	 * @return
	 */
	public static boolean isBrandPageRequest(SearchQuery pSearchQuery){
		if(null != pSearchQuery && pSearchQuery.isFromBrandPage()){
			return true;
		}
		return false;
	}
	/**
	 * @param pSearchQuery
	 * @return
	 */
	public static boolean isCollegePageRequest(SearchQuery pSearchQuery){
		if(null != pSearchQuery && pSearchQuery.isFromCollege()){
			return true;
		}
		return false;
	}
	/**
	 * @param pMdexRequest
	 * @param pSearchQuery
	 * @return
	 */
	public static boolean isKeywordSearchRequest(SearchQuery pSearchQuery){
		//is not
		if(null != pSearchQuery && !isCategoryPageRequest(pSearchQuery) && !isBrandPageRequest(pSearchQuery) && !isCollegePageRequest(pSearchQuery)){
			//AND IS
			if(StringUtils.isNotEmpty(pSearchQuery.getKeyWord()))
				return true;
		}
		return false;
	}
	/**
	 * @param pMdexRequest
	 * @param pSearchQuery
	 * @return
	 */
	public static boolean isHeaderSearchRequest(SearchQuery pSearchQuery){
		if(pSearchQuery.isHeaderSearch()){
			return true;
		}
		return false;
	}
	
	public FilterState addSiteIdFilter(SearchQuery pSearchQuery, FilterState pNavFilterState){
		if(pSearchQuery == null || pNavFilterState == null){
			return pNavFilterState;
		}
		String pSiteId = pSearchQuery.getSiteId();
		
		List<String> recFilters = pNavFilterState.getRecordFilters();
		//add directly since either the actual list or an empty list would be returned.
		recFilters.add(getSiteConfig().get(pSiteId));
		return pNavFilterState;
	}

	/**
	 * @return siteConfig
	 */
	public Map<String, String> getSiteConfig() {
		return this.siteConfig;
	}

	/**
	 * @param siteConfig
	 */
	public void setSiteConfig(final Map<String, String> siteConfig) {
		this.siteConfig = siteConfig;
	}
	
	/**
	 * This method is used to set product image URL for different views to be used directly while rendering 
	 * images on jsp
	 * @param bbbProduct
	 */
	
	
	public void setProductImageURLForDifferentView(BBBProduct bbbProduct) {
		if(isLoggingDebug()){
			logDebug("[START] EndecaSearchUtil.setProductImageURLForDifferentView() method for product :: " + bbbProduct.getProductID());
		}
		String scene7EndecaImageSize = null;
		try {
			scene7EndecaImageSize = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS , BBBCoreConstants.SCENE7_IMAGE_SIZE_FROM_ENDECA).get(0);
			if (BBBUtility.isEmpty(scene7EndecaImageSize))
            {
                  scene7EndecaImageSize = IMG_SIZE_FROM_ENDECA;
            }
            else if(scene7EndecaImageSize.contains(BBBCoreConstants.DOLLAR))
            {
               scene7EndecaImageSize=BBBUtility.getOnlyDigits(scene7EndecaImageSize);
            }

		} catch (BBBSystemException e) {
			logError("Error occured while config key : scene7EndecaImageSize "+e);
		} catch (BBBBusinessException e) {
			logError("Error occured while config key : scene7EndecaImageSize "+e);
		}
		if (!BBBUtility.isEmpty(bbbProduct.getImageURL())) {
			String imageUrl  = bbbProduct.getImageURL();
			Pattern endecaImageSizePattern = Pattern.compile(IMG_PATTERN_FROM_ENDECA);
			 Matcher m = endecaImageSizePattern.matcher(imageUrl);
			if (m.find()) {
				 if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())) {

				imageUrl = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+scene7EndecaImageSize+CONSTANT_DOLLAR);
		         } else {

		                imageUrl = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+IMG_SIZE_FROM_DESKTOP_LISTVIEW+CONSTANT_DOLLAR);
		         } 
				String imageUrlForGrid3x3 = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID3x3+CONSTANT_DOLLAR);
				String imageUrlForGrid3x3_newPlp = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID3x3_newPlp+CONSTANT_DOLLAR);
				String imageUrlForGrid4 = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID4+CONSTANT_DOLLAR);
				if (isLoggingDebug()) {
					logDebug("Setting imageUrlForGrid3x3 :: " + imageUrlForGrid3x3 +  " and imageUrlForGrid4 :: " + imageUrlForGrid4 + "and image URL"+ imageUrl +" for Product " + bbbProduct.getProductID());
				}
				bbbProduct.setImageURL(imageUrl);
				bbbProduct.setProductImageUrlForGrid3x3(imageUrlForGrid3x3);
				bbbProduct.setProductImageUrlForGrid3x3_newPlp(imageUrlForGrid3x3_newPlp);
				bbbProduct.setProductImageUrlForGrid4(imageUrlForGrid4);
			}
		}
		if(isLoggingDebug()){
			logDebug("[END] EndecaSearchUtil.setProductImageURLForDifferentView() method for product :: " + bbbProduct.getProductID());
		}
	}
	
	
	/**
	 * This method is used to set sku color swatch image URL for different views to be used directly while rendering 
	 * images on jsp
	 * @param skuVO
	 */
	
public void setSkuImageURLForDifferentView(SkuVO skuVO) {
		
		if(isLoggingDebug()){
			logDebug("[START] EndecaSearchUtil.setSkuImageURLForDifferentView() method for sku :: " + skuVO.getSkuID());
		}
		
		String scene7EndecaImageSize = getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS,
				BBBCoreConstants.SCENE7_IMAGE_SIZE_FROM_ENDECA,	IMG_SIZE_FROM_ENDECA);

		if (scene7EndecaImageSize.contains(BBBCoreConstants.DOLLAR)) {
			scene7EndecaImageSize = BBBUtility
					.getOnlyDigits(scene7EndecaImageSize);
		}

		String imageUrl  = skuVO.getSkuMedImageURL();
		if (!BBBUtility.isEmpty(imageUrl)) {
			
			Pattern endecaImageSizePattern = Pattern.compile(IMG_PATTERN_FROM_ENDECA);
			 Matcher m = endecaImageSizePattern.matcher(imageUrl);
			if (m.find()) {
				imageUrl = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+scene7EndecaImageSize+CONSTANT_DOLLAR);
			    String imageUrlForGrid3x3 = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID3x3+CONSTANT_DOLLAR);
				String imageUrlForGrid4 = imageUrl.replaceAll(IMG_PATTERN_FROM_ENDECA, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID4+CONSTANT_DOLLAR);
				if (isLoggingDebug()) {
					logDebug("Setting imageUrl :: " + imageUrl + ", skuMedImageUrlForGrid3x3 :: " + imageUrlForGrid3x3 +  " and skuMedImageUrlForGrid4 :: " + imageUrlForGrid4 + " for Sku " + skuVO.getSkuID());
				}
				skuVO.setSkuMedImageURL(imageUrl);
				skuVO.setSkuMedImageUrlForGrid3x3(imageUrlForGrid3x3);
				skuVO.setSkuMedImageUrlForGrid4(imageUrlForGrid4);
			}
		}
		if(isLoggingDebug()){
			logDebug("[END] EndecaSearchUtil.setSkuImageURLForDifferentView() method for sku :: " + skuVO.getSkuID());
		}
	}

/**
 * Method will check the color searched from Keyword, SWS and Facet and show color for Multi-SKU products on PLPs 
 * according to the color searched and color relevancy table
 * @param searchResults
 * @param pSearchQuery
 */
public void updateProductImageWithSwatchImageUrls(SearchResults searchResults, final SearchQuery pSearchQuery) {
	BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_UTIL, "updateProductImageWithSwatchImageUrls");
	Calendar startTime = Calendar.getInstance();
	logDebug("EndecaSearchUtil.updateProductImageWithSwatchImageUrls enter. start time - " + startTime.getTimeInMillis());

	if (pSearchQuery == null) {
		logDebug("EndecaSearchUtil.updateProductImageWithSwatchImageUrls exit: searchQuery is null");
		return;
	}
	
	List<String> listOfColorsInKeyword = new ArrayList<String>();
	List<String> listOfColorsInSWS = new ArrayList<String>();
	List<String> listOfColorsInFacet = new ArrayList<String>();
	
	//primary search keyword
	String keyword = pSearchQuery.getKeyWord();
	
	//Identifying color in keyword
	if (BBBUtility.isNotEmpty(keyword)) {
		if(pSearchQuery.isEPHFound() && pSearchQuery.getEphResultVO() != null && pSearchQuery.getEphResultVO().isColorMatchApplied()){
			listOfColorsInKeyword = pSearchQuery.getEphResultVO().getColorList();
		}else{
			getColorCustomizer().removeAll(keyword, listOfColorsInKeyword);
		}
	}
	
	//Identifying color in SWS
	if (null != pSearchQuery.getNarrowDown()) {
		List<String> userInput = new ArrayList<String>(Arrays.asList(pSearchQuery.getNarrowDown().trim()
						.toLowerCase().split(BBBCoreConstants.NARROWDOWN_DELIMITER_FL+BBBCoreConstants.UNDERSCORE)));
			for(String swsKeyword : userInput) {
				if(BBBUtility.isNotEmpty(swsKeyword)){
					getColorCustomizer().removeAll(swsKeyword, listOfColorsInSWS);
				}
			}
	}
	//Identifying color in color facet apply.
	if(null!= searchResults && null!= searchResults.getDescriptors()) {
		
		if(null != searchResults.getBbbProducts()){
			
			String colorGroup = getConfigTools().getConfigKeyValue(BBBCoreConstants.DIM_DISPLAY_CONFIGTYPE, BBBCoreConstants.COLORGROUP_KEY, BBBCoreConstants.FACET_DESCRIPTOR_COLORGROUP);
			
			for(CurrentDescriptorVO pVo : searchResults.getDescriptors()){
				if((null != pVo.getRootName()) && (colorGroup.equalsIgnoreCase(pVo.getRootName()))){
					listOfColorsInFacet.add(pVo.getName());
				}	
			}
		}
	}
	if(listOfColorsInKeyword != null && listOfColorsInSWS != null && listOfColorsInFacet != null){
		/*
		 * apply keyword searched color, when 1 color is being searched in primary search and colors in SWS and color facet are null
		 */
		if(listOfColorsInKeyword.size() == 1 && listOfColorsInSWS.size() == 0 && listOfColorsInFacet.size() ==0){
			logDebug("EndecaSearchUtil.updateProductImageWithSwatchImageUrls: primary Search color applied - " + listOfColorsInKeyword.get(0));
			applyMatchingColor(listOfColorsInKeyword.get(0), searchResults);
		}
		/*
		 * apply color in SWS, when 1 color is being searched in both primary search and SWS and color facet is null
		 */
		else if(listOfColorsInSWS.size() == 1 && listOfColorsInFacet.size() == 0){
			logDebug("EndecaSearchUtil.updateProductImageWithSwatchImageUrls: SWS color applied - " + listOfColorsInSWS.get(0));
			applyMatchingColor(listOfColorsInSWS.get(0), searchResults);
		}
		/*
		 * apply color in facet, when 1 color is being searched in both primary search and color Facet and SWS is null
		 */
		else if(listOfColorsInSWS.size() == 0 && listOfColorsInFacet.size() == 1){
			logDebug("EndecaSearchUtil.updateProductImageWithSwatchImageUrls: Facet color applied - " + listOfColorsInFacet.get(0));
			applyMatchingColor(listOfColorsInFacet.get(0), searchResults);
		}
	}
	
	Calendar endTime = Calendar.getInstance();
	long timeTaken = endTime.getTimeInMillis()
			- startTime.getTimeInMillis();
	logDebug("EndecaSearchUtil.updateProductImageWithSwatchImageUrls exit. end time in millisecs - "
			+ endTime.getTimeInMillis()
			+ " time taken in ms - "
			+ timeTaken);
	BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_UTIL, "updateProductImageWithSwatchImageUrls");
}

/**
 * The method will apply the color in pcolor to product's image in case of Multi-SKU products
 * 
 * @param pKeyword
 * @param pSearchResults
 * @return
 */
private void applyMatchingColor(String pcolor, SearchResults pSearchResults) {

	BBBPerformanceMonitor.start(BBBPerformanceConstants.ENDECA_SEARCH_UTIL, "applyMatchingColor");
	Calendar startTime = Calendar.getInstance();
	logDebug("EndecaSearchUtil.applyMatchingColor enter. start time - " + startTime.getTimeInMillis());
	
		if (null != pSearchResults && null != pSearchResults.getBbbProducts()) {
			logDebug("EndecaSearchUtil.applyMatchingColor : Checking color application for Product Count : " + pSearchResults.getBbbProducts().getBBBProductCount());
			for (BBBProduct product : pSearchResults.getBbbProducts().getBBBProducts()) {
				Map<String, SkuVO> skuMap = product.getSkuSet();
				for (SkuVO skuVO : skuMap.values()) {
					if ((null != skuVO.getColorGroup() && skuVO.getColorGroup().equalsIgnoreCase(pcolor))
							|| (null != skuVO.getColor() && skuVO.getColor().equalsIgnoreCase(pcolor))) {
						logDebug("EndecaSearchUtil.applyMatchingColor applying color in search results - " + pcolor);
						product.setImageURL(skuVO.getSkuMedImageURL());
						product.setVerticalImageURL(skuVO.getSkuVerticalImageURL());
						product.setProductImageUrlForGrid3x3(skuVO.getSkuMedImageUrlForGrid3x3());
						product.setProductImageUrlForGrid4(skuVO.getSkuMedImageUrlForGrid4());
						break;
					}
				}
			}
		}

		Calendar endTime = Calendar.getInstance();
		long timeTaken = endTime.getTimeInMillis()
				- startTime.getTimeInMillis();
		logDebug("EndecaSearchUtil.applyMatchingColor exit. end time in millisecs - "
				+ endTime.getTimeInMillis()
				+ " time taken in ms - "
				+ timeTaken);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.ENDECA_SEARCH_UTIL, "applyMatchingColor");
}

/**
 * Method to fetch omniture boosted L2 for given search term and site
 * @param topPopularTerm
 * @param siteDimensionId
 * @return
 */
public List<CategoryVO> fetchOmnitureBoostedL2s(String topPopularTerm, String siteDimensionId,SearchQuery searchQuery) {
	
	vlogDebug("Enter :: fetchOmnitureBoostedL2s, Search term "+topPopularTerm);
	List<CategoryVO> omnitureBoostedL2List = new ArrayList<CategoryVO>();
	
	if (BBBUtility.isEmpty(topPopularTerm)) {
		return omnitureBoostedL2List;
	}
	
	try {
			
		UrlGen urlGen = new UrlGen(BBBEndecaConstants.NAVIGATION+BBBCoreConstants.EQUAL+ siteDimensionId, getQueryGenerator().getEncoding());
		urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getQueryGenerator().getNtxParamMatchAllExact());
		urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, topPopularTerm.trim());
		urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, BBBEndecaConstants.NTK_L2_BOOST_SEARCH_TERM);	
		
		// Removed NR param from query - PS-62981.
		
		
		vlogDebug("Omniture L2 boost query for search term "+topPopularTerm+" is "+urlGen.toString());
		ENEQuery ocbQueryObject = new UrlENEQuery(urlGen.toString(), getQueryGenerator().getEncoding());
		ocbQueryObject.setNavNumERecs(getQueryGenerator().getOmnitureRecordCount());
		ENEQueryResults ocbResults = getEndecaClient().executeQuery(ocbQueryObject);
		
		
		
		if(ocbResults != null) {
			Navigation navigation = ocbResults.getNavigation();
			if(navigation !=null) {
				ERecList eRecs = navigation.getERecs();
				if(eRecs!=null && eRecs.size() > 0) {
					
					  ERec eRec = (ERec) eRecs.get(BBBCoreConstants.ZERO);
					  
					  PropertyMap properties = eRec.getProperties();
					  String omnitureKeyword = (String)properties.get(BBBEndecaConstants.NTK_L2_BOOST_SEARCH_TERM);
					  
					   if(properties !=null && topPopularTerm.equalsIgnoreCase(omnitureKeyword)) {
						   String boostedL2s = (String)properties.get(BBBEndecaConstants.L2_BOOST_L2S);
						   String[] L2Data = boostedL2s.split(BBBEndecaConstants.OCB_L2_DEL);
						   if(L2Data != null) {
						   for (String categoryData : L2Data) {
							  
							  CategoryVO subCategoryVO = new CategoryVO();
							  String[] ocbCategoryData = categoryData.split(BBBEndecaConstants.OCB_DATA_DEL);
								  
								  if(ocbCategoryData != null && ocbCategoryData.length == BBBCoreConstants.THREE) {
							  subCategoryVO.setCategoryName(ocbCategoryData[BBBEndecaConstants.OCB_DATA_L2_NAME_INDEX]);
							  subCategoryVO.setCategoryId(ocbCategoryData[BBBEndecaConstants.OCB_DATA_L2_ID_INDEX]);
							  omnitureBoostedL2List.add(subCategoryVO);
						   }
								  
					   }
				}
			}
		}
			}
		}
	} catch (UrlENEQueryParseException e) {
		vlogError("UrlENEQueryParseException while fetching omniture boosted L2 categories "+e);
	} catch (ENEQueryException e) {
		vlogError("ENEQueryException while fetching omniture boosted categories "+e);
	}
	
	vlogDebug("Exit :: fetchOmnitureBoostedL2s, category list "+omnitureBoostedL2List);
	return omnitureBoostedL2List;
}

	/**
	 * Method to fetch single omniture boosted l2 id for search term.
	 * @param searchTerm
	 * @param searchQuery
	 * @return
	 */
	public String fetchOmnitureBoostedL2ID(Navigation navigation,SearchQuery searchQuery) {
		
		vlogDebug("Enter :: fetchOmnitureBoostedL2ID, searchTerm ");
		
		String omnitureBoostedL2Id = null;
		List<String> ocbBoostingFlag = new ArrayList<String>();
		String omnitureL2BoostingEnabled = BBBCoreConstants.TRUE;
		
		try {
			ocbBoostingFlag = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_OCB_BOOSTING_FLAG);
		} catch (BBBSystemException | BBBBusinessException exception) {
			vlogError("while fetching omniture boosted L2 flag"+ exception);
		}
		
        if(!ocbBoostingFlag.isEmpty()) {
			
		    omnitureL2BoostingEnabled = ocbBoostingFlag.get(0);
        }
             vlogDebug("Omniture boosted flag value "+omnitureL2BoostingEnabled);
    	     //OCB boosting controlled from BCC
			if(BBBCoreConstants.TRUE.equalsIgnoreCase(omnitureL2BoostingEnabled)) {
				String searchKeyword = getKeywordForOmnitureQuery(navigation,searchQuery);
				if(!BBBUtility.isEmpty(searchKeyword)) {
					try {
			String siteDimensionId = getCatalogId(BBBEndecaConstants.SITE_ID,getSiteIdMap().get(searchQuery.getSiteId()));
			
						List<CategoryVO> omnitureBoostedL2s = fetchOmnitureBoostedL2s(searchKeyword, siteDimensionId,searchQuery);
			if(!BBBUtility.isListEmpty(omnitureBoostedL2s)) {
				CategoryVO  categoryVO = omnitureBoostedL2s.get(BBBCoreConstants.ZERO);
							omnitureBoostedL2Id = categoryVO.getCategoryId();
						}
		} catch (BBBBusinessException | BBBSystemException e) {
						logError("Exception while fetching site dim id "+e.getMessage());
		}
				}
			}
        
		vlogDebug("Exit :: fetchOmnitureBoostedL2ID, l2 ID "+omnitureBoostedL2Id);
		return omnitureBoostedL2Id;
	}
	
	 /**
     * Method to fetch suitable keyword for endeca query.
     * @param navigation
     * @param searchQuery
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private String getKeywordForOmnitureQuery(Navigation navigation, SearchQuery searchQuery) {
    	vlogDebug("Enter :: getKeywordForOmnitureQuery");
    	String keyword = null;
    	
    	if(navigation == null) {
    		return keyword;
    	}
    		 	 
    	Map completeSearchReportsMap = navigation.getESearchReportsComplete();
    	List<ESearchReport> searchReportForAllInterface = (List<ESearchReport>) completeSearchReportsMap.get(BBBEndecaConstants.ALL);
    	
            if(!BBBUtility.isListEmpty(searchReportForAllInterface)) {
            	
            	ESearchReport searchReport = searchReportForAllInterface.get(BBBCoreConstants.ZERO);
            	
            	if(searchReport != null) {
            		
            		List<ESearchAutoSuggestion> autoSuggestions = (List<ESearchAutoSuggestion>)searchReport.getAutoSuggestions();
            		
            		if(!BBBUtility.isListEmpty(autoSuggestions)) {
            			
            			ESearchAutoSuggestion searchAutoSuggestion = autoSuggestions.get(BBBCoreConstants.ZERO);
            			if(searchAutoSuggestion != null && !BBBUtility.isEmpty(searchAutoSuggestion.getTerms()) 
            					&& ! searchAutoSuggestion.didSuggestionIncludeAutomaticPhrasing()) {
            	     		keyword = searchAutoSuggestion.getTerms();
            	     		vlogDebug("Fetching keyword from autosuggestions , keyword is "+keyword);
            	     		return keyword;
            			}
            		}
            		
            		List dymSuggestions = searchReport.getDYMSuggestions();
            		
            		if(!BBBUtility.isListEmpty(dymSuggestions)) {
            			
	            		ESearchDYMSuggestion dymSuggestion = null;	
	            		ListIterator iterDYMSuggestions = dymSuggestions.listIterator();
	            		
	                    while (iterDYMSuggestions.hasNext()) {
	                    	dymSuggestion = (ESearchDYMSuggestion) iterDYMSuggestions.next();
	                    	if(!BBBUtility.isEmpty(dymSuggestion.getTerms())) {
	            	     		keyword = dymSuggestion.getTerms();
	            	     		vlogDebug("Fetching keyword from did you mean suggestions , keyword is "+keyword);
	            	     		return keyword;
	            			}
	                    }
            		}
            	}
            }
            
            //in the case autosuggestion or did you term is not available then use user entered search term
            
            if(BBBUtility.isEmpty(keyword)) {
            	keyword = searchQuery.getKeyWord();
            } 	
        vlogDebug("Exit :: getKeywordForOmnitureQuery , keyword is "+keyword);    
    	return keyword;
    }
    
	/**
	 * @return the colorSearchTermCustomizer
	 */
	public String getColorSearchTermCustomizer() {
		return colorSearchTermCustomizer;
	}
	
	public ColorSearchTermCustomizer getColorCustomizer() {
		return (ColorSearchTermCustomizer) Nucleus.getGlobalNucleus().resolveName(getColorSearchTermCustomizer());
	}
	/**
	 * @param colorSearchTermCustomizer the colorSearchTermCustomizer to set
	 */
	public void setColorSearchTermCustomizer(String colorSearchTermCustomizer) {
		this.colorSearchTermCustomizer = colorSearchTermCustomizer;
	}
	/** BBBI-1743
	 *  This method checks for the autocorrection in the keyword searched by endeca
	 * @param pSearchQuery
	 * @return keyword
	 * @throws UrlENEQueryParseException
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public  String getAutoSuggestedKeyword(SearchQuery pSearchQuery) throws UrlENEQueryParseException, BBBBusinessException, BBBSystemException{
		final String autoSuggestTrue = "&AutoSuggest=true";
		long start = Calendar.getInstance().getTimeInMillis();
		boolean isCacheEnabled = Boolean.valueOf(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBEndecaConstants.OPB_BOOST_STRATEGY_CACHE_ENABLED, BBBCoreConstants.FALSE));
		logDebug("Enter :: EndecaSearchUtil.getAutoSuggestedKeyword, Search query " + pSearchQuery + " and isCacheEnabled: " + isCacheEnabled);
		BBBPerformanceMonitor.start("EndecaSearchUtil" + "_getAutoSuggestedKeyword");
		if(BBBUtility.isEmpty(pSearchQuery.getKeyWord()) || pSearchQuery.isFromBrandPage()){
			logDebug("Exit :: EndecaSearchUtil.getAutoSuggestedKeyword : Request  not from Keyword Search");
			BBBPerformanceMonitor.end("EndecaSearchUtil" + "_getAutoSuggestedKeyword");
			return null;
		}
		List<String> cachedAttributeList = new ArrayList<String>();
		List<String> attributeList = new ArrayList<String>();
		String keyword = null;
		//ILD - 174 | Start | Query modified to avoid duplicate entries in cache
		String queryURL=this.getEndecaSearchTools().fetchSearchQueryURL(pSearchQuery);
	try {
		String searchMode=getParameter(queryURL , BBBEndecaConstants.NAV_SEARCH_MODE);
		String searchQueryKeyWord = pSearchQuery.getKeyWord().replaceAll("'", "");
		searchQueryKeyWord = ((searchQueryKeyWord.replaceAll("[^0-9A-Za-z\"\']"," ")).replaceAll("[\']", "")).replaceAll(" +", " ");
		searchQueryKeyWord = searchQueryKeyWord.replaceAll("-", " ");
		
		 if(BBBUtility.isEmpty(searchQueryKeyWord.trim())){
			 logDebug("Exit :: EndecaSearchUtil.getAutoSuggestedKeyword : Blank keyword");	
			 return null;
            }
		searchQueryKeyWord=searchQueryKeyWord.trim(); 
		String siteDimId = this.getCatalogId(BBBEndecaConstants.SITE_ID2,getSiteIdMap().get(pSearchQuery.getSiteId()));
		String productRecTypeDimID = this.getCatalogId(BBBEndecaConstants.RECORD_TYPE, BBBEndecaConstants.PROD_RECORD_TYPE);
		final UrlGen urlGen = new UrlGen(BBBEndecaConstants.NAVIGATION+BBBCoreConstants.EQUAL+ siteDimId + BBBCoreConstants.SPACE + productRecTypeDimID, getQueryGenerator().getEncoding());
		urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, this.getQueryGenerator().getSearchField());
		urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, searchMode);
		urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, searchQueryKeyWord);
		urlGen.addParam(BBBEndecaConstants.NAV_DID_YOU_MEAN, this.getQueryGenerator().getDidYouMean());
		urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPC, this.getQueryGenerator().getQueryParamNtpc());
		urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPR, this.getQueryGenerator().getQueryParamNtpr());
		
		if(BBBCmsConstants.SEARCH_MODE_ALLPARTIAL.equalsIgnoreCase(searchMode))
		{
		   urlGen.addParam(BBBEndecaConstants.NAV_NR, BBBEndecaConstants.SITE_ID2+":"+getSiteIdMap().get(pSearchQuery.getSiteId()));
		}
		
		String autoSuggestEndecaQuery = urlGen.toString();
		logDebug("[EndecaSearchUtil.getAutoSuggestedKeyword], Endeca Query for AutoSuggestion :" + autoSuggestEndecaQuery);
		
		//ILD-174 | end
		
			if (isCacheEnabled) {
				logDebug("[EndecaSearchUtil.getAutoSuggestedKeyword], Query Searched for AutoSuggestion in cache :" + autoSuggestEndecaQuery+autoSuggestTrue);
				cachedAttributeList = getEndecaSearchTools().getBoostedProductListFromCache(autoSuggestEndecaQuery+autoSuggestTrue);
			}
			if (isCacheEnabled && !BBBUtility.isListEmpty(cachedAttributeList)) {
				logDebug("Exit :: EndecaSearchUtil_getAutoSuggestedKeyword, cachedAttributeList [contains: auto-corrected keyword and total number of records] from cache: " + cachedAttributeList);
				BBBPerformanceMonitor.end("EndecaSearchUtil" + "_getAutoSuggestedKeyword");
				keyword = cachedAttributeList.get(BBBCoreConstants.ZERO);
				pSearchQuery.setAutoSuggestedKeyword(keyword);
	     		pSearchQuery.setAutoSuggested(true);
	     		pSearchQuery.setNumberOfProduct(Long.valueOf(cachedAttributeList.get(BBBCoreConstants.ONE)));
			} else {
				ENEQuery opbQueryObject = new UrlENEQuery(autoSuggestEndecaQuery, getQueryGenerator().getEncoding());
				opbQueryObject.setNavNumERecs(0);
				opbQueryObject.setNavERecsOffset(0L);
				opbQueryObject.setNavNumAggrERecs(0);
				opbQueryObject.setNavNumBulkERecs(0);
				opbQueryObject.setNavNumBulkAggrERecs(0);
				opbQueryObject.setNavAllRefinements(false);
				ENEQueryResults opbResults = getEndecaClient().executeQuery(opbQueryObject);
				if(opbResults != null && opbResults.getNavigation() != null) {
					Map completeSearchReportsMap = opbResults.getNavigation().getESearchReportsComplete();
					if(completeSearchReportsMap != null){
						List<ESearchReport> searchReportForAllInterface = (List<ESearchReport>) completeSearchReportsMap.get(BBBEndecaConstants.ALL);
				    	
			            if(!BBBUtility.isListEmpty(searchReportForAllInterface)) {
			            	
			            	ESearchReport searchReport = searchReportForAllInterface.get(BBBCoreConstants.ZERO);
			            	
			            	if(searchReport != null) {
			            		
			            		List<ESearchAutoSuggestion> autoSuggestions = (List<ESearchAutoSuggestion>)searchReport.getAutoSuggestions();
			            		
			            		if(!BBBUtility.isListEmpty(autoSuggestions)) {
			            			
			            			ESearchAutoSuggestion searchAutoSuggestion = autoSuggestions.get(BBBCoreConstants.ZERO);
			            			if(searchAutoSuggestion != null && !BBBUtility.isEmpty(searchAutoSuggestion.getTerms()) 
			            					&&  searchAutoSuggestion.didSuggestionIncludeSpellingCorrection() && !searchAutoSuggestion.didSuggestionIncludeAutomaticPhrasing()) {
			            	     		keyword = searchAutoSuggestion.getTerms();
			            	     		logDebug("Fetching keyword from autosuggestions , keyword is "+keyword);
			            	     		pSearchQuery.setAutoSuggestedKeyword(keyword);
			            	     		pSearchQuery.setAutoSuggested(true);
			            			}
			            		}
			            	}
			            }
						
					}
					attributeList.add(keyword);
					attributeList.add(String.valueOf(opbResults.getNavigation().getTotalNumERecs()));
					pSearchQuery.setNumberOfProduct(opbResults.getNavigation().getTotalNumERecs());
				}
				/***
				 * Insert Auto-corrected keyword and total number into cache
				 */
				if (isCacheEnabled && !BBBUtility.isListEmpty(attributeList)) {
					logDebug("[EndecaSearchUtil.getAutoSuggestedKeyword], attributeList [contains: auto-corrected keyword and total number of records] saved to cache is: " + attributeList);
					logDebug("[EndecaSearchUtil.getAutoSuggestedKeyword], Query cached for AutoSuggestion in cache :" + autoSuggestEndecaQuery+autoSuggestTrue);
					getEndecaSearchTools().insertBoostedProductsIntoCache(autoSuggestEndecaQuery+autoSuggestTrue, attributeList);
				}
			}
			
		} catch (ENEQueryException | UrlFormatException e) {
			BBBPerformanceMonitor.end("EndecaSearchUtil" + "_getAutoSuggestedKeyword");
			logError("Exception occured while fetching results from endeca for autoSuggestedKeyword :"+e);
		}
		BBBPerformanceMonitor.end("EndecaSearchUtil" + "_getAutoSuggestedKeyword");
		if(keyword == null){
			logDebug("EXIT :: EndecaSearchUtil.getAutoSuggestedKeyword, searched term NOT auto corrected!!");
		}else {
			logDebug("EXIT :: EndecaSearchUtil.getAutoSuggestedKeyword, autoCorrectedKeyword :"+ keyword + "for searched term :"+pSearchQuery.getKeyWord());
		}
		
		long end = Calendar.getInstance().getTimeInMillis();
		logDebug("time taken for EndecaSearchUtil.getAutoSuggestedKeyword method in milliseconds: "+(end-start));
		return keyword;
	}
	
	
	public SearchQuery lookupEPHForBoosting(SearchQuery searchQuery){
	logDebug("EndecaSearchUtil :: lookupEPHForBoosting Start: searchTerm:["+searchQuery.getKeyWord()+"]");	
	
	BBBPerformanceMonitor.start("EndecaSearchUtil"+BBBCoreConstants.UNDERSCORE+"lookupEPHForBoosting");
	
	long startTime=System.currentTimeMillis();
	
	String searchTermFromVO=BBBUtility.trimToNotNull(searchQuery.getKeyWord()).toLowerCase();
	
	
	 try {
		       		String ephQueryScheme=searchQuery.getEphQueryScheme();
		       		HttpServletRequest request= ServletUtil.getCurrentRequest();
		       		HttpSession session=request.getSession(false);
				  
		       		Map<String,EPHResultVO> ephResultMap=(Map<String, EPHResultVO>) session.getAttribute(BBBCoreConstants.EPH_RESULT_MAP);
				  
				 
				 
				    // do not find eph for a keyword from cache container if it already lookup in same session
				 
			 		if( ephResultMap==null || ! ephResultMap .containsKey(searchTermFromVO) || ephResultMap.get(searchTermFromVO) == null) 
					 {	 
			 			String 	searchTerm=getAutoSuggestedKeyword(searchQuery);
			 			
						//if no  AutoSuggestedKeyword for search Term 
						if(StringUtils.isBlank(searchTerm))
					    {
					    	searchTerm=searchTermFromVO;
					    }
						
						String prodCountForEPHAndCustomBoosting= getEphLookUpUtil().getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.PROD_COUNT_FOR_EPH_AND_CUSTOM_BOOSTING, BBBCoreConstants.STRING_ZERO);	
					    
						int productCountForEPHAndCustomBoosting=Integer.parseInt(prodCountForEPHAndCustomBoosting);
						
						int productInSearchResult= (int) searchQuery.getNumberOfProduct();
						if(isLoggingDebug()){
									logDebug("EndecaSearchUtil :: lookupEPHForBoosting Start: searchTerm:["
									+ searchQuery.getKeyWord()
									+ "],productInSearchResult["
									+ productInSearchResult
									+ "],MaxProductCountForEPHAndCustomBoosting:["
									+ productCountForEPHAndCustomBoosting + "]");
						}
						searchQuery.appendBoostingLogs("EndecaSearchUtil :: lookupEPHForBoosting searchTerm:["
									+ searchQuery.getKeyWord()
									+ "],productInSearchResult["
									+ productInSearchResult
									+ "],MaxProductCountForEPHAndCustomBoosting:["
									+ productCountForEPHAndCustomBoosting + "]" );	
						
						//if a keyword has less number of product in search result than the configured productCountForEPHAndCustomBoosting then no need to do eph processing
						if(productCountForEPHAndCustomBoosting != 0 && productCountForEPHAndCustomBoosting < productInSearchResult)
					    {
					    	EPHResultVO ephResultVO=getEphLookUpUtil().getEPHCodeForSearchTerm(searchTerm);
					    	
					    	searchQuery.appendBoostingLogs("EndecaSearchUtil :: lookupEPHForBoosting :ephResultVO"+ephResultVO);
					    	
					    	logDebug("EndecaSearchUtil :: lookupEPHForBoosting :ephResultVO"+ephResultVO);
					    	
					    	if(ephResultVO != null)
					    	{
					    		if(ephResultVO.isEphOrL1L2ListAvailable()){
					    			searchQuery.setEPHFound(true);
					    			searchQuery.setEphResultVO(ephResultVO);
					    			if(BBBCoreConstants.FILTER_VALUE.equalsIgnoreCase(ephQueryScheme)){
						    			appendEPHOrNodeIdInQueryURL(searchQuery, ephResultVO, searchTerm);
						    		}
					    		}
					    		
						    	if(ephResultMap == null){
									  ephResultMap = new HashMap<String,EPHResultVO>();
									  session.setAttribute(BBBCoreConstants.EPH_RESULT_MAP, ephResultMap);
								   }
					    		//put EPH detail in http session for future use
					    		ephResultMap.put(searchTermFromVO, ephResultVO);
					    	}
					    	
					    }
					}
			 		else
			 		{	
			 			EPHResultVO ephResultVO=ephResultMap.get(searchTermFromVO);
			 			
			 			logDebug("EndecaSearchUtil :: lookupEPHForBoosting : ephResultVO found in the Session for searchTerm:["+searchTermFromVO+"],ephResultVO:"+ephResultVO);
			 			searchQuery.appendBoostingLogs("EndecaSearchUtil :: lookupEPHForBoosting : ephResultVO found in the Session for searchTerm:["+searchTermFromVO+"],ephResultVO"+ephResultVO);
			 			
			 			if(ephResultVO != null && ephResultVO.isEphOrL1L2ListAvailable()){
			 				
				 			if(BBBCoreConstants.FILTER_VALUE.equalsIgnoreCase(ephQueryScheme))
				    		{
				    			appendEPHOrNodeIdInQueryURL(searchQuery, ephResultVO, searchTermFromVO);
				    			searchQuery.setEPHFound(true);
				    		}
				 			else{
				 				searchQuery.setEphResultVO(ephResultVO);
				 				searchQuery.setEPHFound(true);
				 			   }
			 			}
			 		}
		}
	   catch (UrlENEQueryParseException qpException) 
	   {
			logError("EndecaSearchUtil: lookupEPHForBoosting Start: searchTerm:["+searchQuery.getKeyWord()+"],UrlENEQueryParseException"+qpException);
	   }
	 	catch (Exception exception) 
	   {
			logError("EndecaSearchUtil: lookupEPHForBoosting Start: searchTerm:["+searchQuery.getKeyWord()+"],Exception"+exception);
	   }
	 	finally
	 	{	
			long endTime=System.currentTimeMillis();
			
			BBBPerformanceMonitor.start("EndecaSearchUtil"+BBBCoreConstants.UNDERSCORE+"lookupEPHForBoosting");
			
			logDebug("EndecaSearchUtil :: lookupEPHForBoosting End: searchTerm:["+searchQuery.getKeyWord()+"],tookTime:["+(endTime-startTime)+"] Millis");
	 	}
	 
	return searchQuery;
	
	}
	
	
	
	
	/**
	 * This method used to add sort EPH/NodeId property in the  OOTB BoostStrata
	 * @param config
	 * @param ephResultVO
	 * @param searchTerm
	 * @return 
	 */

	public  void sortBoostStrataWithEPH(ResultsListConfig config,EPHResultVO ephResultVO,String searchTerm, SearchQuery pSearchQuery) {
		
		logDebug("EndecaSearchUtil :: sortBoostStrataWithEPH Start: searchTerm:["+searchTerm+"]");	
		
		BBBPerformanceMonitor.start("EndecaSearchUtil"+BBBCoreConstants.UNDERSCORE+"sortBoostStrataWithEPH");
		
		boolean isEphSorting=false;
		boolean isL1L2Node=false;
		
		Map<String, String> allEPHDimensionValues = new HashMap<String, String>();
		String ephOrL1L2NodeList=null;
		
		if(ephResultVO != null && BBBUtility.isNotBlank(ephResultVO.getEphList())){
			 ephOrL1L2NodeList=ephResultVO.getEphList();
			 isEphSorting=true;
			 fetchAllDimensionValues(BBBCoreConstants.EPH_ID ,null,allEPHDimensionValues);
		}else if(ephResultVO != null && BBBUtility.isNotBlank(ephResultVO.getCategoryList())){
			 ephOrL1L2NodeList=ephResultVO.getCategoryList();
			 isL1L2Node=true;
			 fetchAllDimensionValues(BBBCoreConstants.NODE_ID,null,allEPHDimensionValues);
		}
		
		StringBuffer logDetail= new StringBuffer("EndecaSearchUtil :: sortBoostStrataWithEPH:searchTerm:["+searchTerm+"],");
		
		if(BBBUtility.isNotBlank(ephOrL1L2NodeList) && ( isEphSorting  || isL1L2Node))
			{
					StringTokenizer st= new StringTokenizer(ephOrL1L2NodeList,BBBCoreConstants.COMMA);
					List <CollectionFilter> ephSorting = new ArrayList<CollectionFilter>();
					while(st.hasMoreTokens())
					{
						DimensionFilter dimensionFilter = null;
						 
						String ephNodeId= (String) st.nextToken();
						
							 dimensionFilter = new DimensionFilter();
							 if(isEphSorting){
									 dimensionFilter.setDimensionName(BBBCoreConstants.EPH_ID);
								}else{
								 dimensionFilter.setDimensionName(BBBCoreConstants.NODE_ID);
								}
							 String dValiId=allEPHDimensionValues.get(ephNodeId);
							 
							 logDebug("EndecaSearchUtil :: sortBoostStrataWithEPH :searchTerm:["+searchTerm+"],EPH_ID/NodeId:["+ephNodeId+"], adding dValiId:["+dValiId+"] for sorting");
							
							 logDetail.append(",EPH_ID/NodeId:["+ephNodeId+"], adding dValiId:["+dValiId+"] for sorting");
							 
							if(StringUtils.isBlank(dValiId)){
								logDebug("EndecaSearchUtil :: sortBoostStrataWithEPH :searchTerm:["+searchTerm+"],dValiId is blank for EPH_ID/NodeId:["+ephNodeId+"] ,skipping this eph for sorting");
								logDetail.append(",dValiId is blank for EPH_ID/NodeId:["+ephNodeId+"] ,skipping this eph for sorting");
								continue;
							}
							dimensionFilter.setDvalId(dValiId);
							CollectionFilter collectionFilter = new CollectionFilter();
							collectionFilter.setInnerFilter(dimensionFilter);
							ephSorting.add(collectionFilter);
					}
					
					
					
					List<CollectionFilter> ootbBoostStrata =config.getBoostStrata();
					
					if(ootbBoostStrata != null)
					{
						ootbBoostStrata.addAll(ephSorting);
						config.setBoostStrata(ootbBoostStrata);
					}
					else{
						config.setBoostStrata(ephSorting);
					}
			}
		pSearchQuery.setEphApplied(true);
		
		logDetail.append(" and EphApplied flag set to true");
		
		pSearchQuery.appendBoostingLogs(logDetail.toString());
		
		BBBPerformanceMonitor.end("EndecaSearchUtil"+BBBCoreConstants.UNDERSCORE+"sortBoostStrataWithEPH");
		logDebug("EndecaSearchUtil :: sortBoostStrataWithEPH Ends:");
		
	}
	
	/**
	 * This method add EPH/NodeId filter in SearchQuery.QueryURL
	 * @param searchQuery
	 * @param ephResultVO
	 * @param searchTerm
	 */
	private void appendEPHOrNodeIdInQueryURL(SearchQuery searchQuery,EPHResultVO ephResultVO,String searchTerm) {
		
		logDebug("EndecaSearchUtil :: appendEPHOrNodeIdInQueryURL to filter Search Result Start: searchTerm:["+searchTerm+"]");	
		
		BBBPerformanceMonitor.start("EndecaSearchUtil"+BBBCoreConstants.UNDERSCORE+"appendEPHOrNodeIdInQueryURL");
		
		boolean isEphSorting=false;
		boolean isL1L2Node=false;
		String ephOrL1L2NodeList=null;
		
		if(ephResultVO != null && BBBUtility.isNotBlank(ephResultVO.getEphList()))
		{
			 ephOrL1L2NodeList=ephResultVO.getEphList();
			 isEphSorting=true;
		}
		else if(ephResultVO != null && BBBUtility.isNotBlank(ephResultVO.getCategoryList()))
		{
			 ephOrL1L2NodeList=ephResultVO.getCategoryList();
			 isL1L2Node=true;
		}
		
	if(BBBUtility.isNotBlank(ephOrL1L2NodeList) && ( isEphSorting  || isL1L2Node))
			{
					int count=0;		
					StringTokenizer ephListTokenizer= new StringTokenizer(ephOrL1L2NodeList,BBBCoreConstants.COMMA);
					
					StringBuffer queryString= new StringBuffer();
					
					queryString.append(BBBCoreConstants.OR);
					
					while(ephListTokenizer.hasMoreTokens())
					{
						String ephOrL1L2Node= (String) ephListTokenizer.nextToken();
								if(count > 0)
								{
									queryString.append(BBBCoreConstants.COMMA);
									queryString.append(BBBCoreConstants.OR);
								}
								
									queryString.append(BBBCoreConstants.LEFT_PARENTHESIS);
								if(isEphSorting)
								{
									queryString.append(BBBCoreConstants.EPH_ID);
								}
								else
								{
									queryString.append(BBBCoreConstants.NODE_ID);
								}
									queryString.append(BBBCoreConstants.COLON);
									queryString.append(ephOrL1L2Node);
								
								if(count > 0)
								{
									queryString.append(BBBCoreConstants.RIGHT_PARENTHESIS);
								}
								
								logDebug("EndecaSearchUtil :: appendEPHOrNodeIdInQueryURL :searchTerm:["+searchTerm+"],EPH_ID/NODE_ID:["+ephOrL1L2Node+"],queryString:["+queryString+"]");
								count++;
					}
				queryString.append(BBBCoreConstants.RIGHT_PARENTHESIS);
				searchQuery.setEphFilterString(queryString.toString());;
				logDebug("EndecaSearchUtil :: appendEPHOrNodeIdInQueryURL :newQueryURL  :["+queryString.toString()+"]");
			
			}
		
		BBBPerformanceMonitor.end("EndecaSearchUtil"+BBBCoreConstants.UNDERSCORE+"appendEPHOrNodeIdInQueryURL");
		
		logDebug("EndecaSearchUtil :: appendEPHOrNodeIdInQueryURL to filter Search Result Ends:  ");
		
	}

	/**
	 * Method used to get Query Scheme (filter/sort/off) from SiteSpect variable,if siteSpect dont have information then this method will 
	 * pull  information from BCC configuration	 * 
	 * @param searchQuery
	 * @return QueryScheme(OFF/SORT/FILTER) default is OFF
	 */
	
	
	public String getEPHBoostingScheme(SearchQuery searchQuery){
		
		logDebug("Enter :: EndecaSearchUtil.getEPHBoostingScheme::");
		
		BBBPerformanceMonitor.start("EndecaSearchUtil" + "_getEPHBoostingScheme");
		
		String ephQueryScheme=null;
		Boolean isDepartmentFilterApplied=true;
		Boolean isSWD=false;
		Boolean resetEph=false;
		String searchParamValue=null;
		DynamoHttpServletRequest request=ServletUtil.getCurrentRequest();
		
		ephQueryScheme = (String) request.getSession().getAttribute(BBBCoreConstants.SAVED_EPH_SCHEME);
		
		//parameter Name in search request URL. If user do search within department then this parameter will have false value.
		String configuredSearchParamName= getEphLookUpUtil().getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.SWD_SEARCH_URL_PARAM_NAME, BBBCoreConstants.SWD_SEARCH_URL_PARAM_NAME_DEFAULT_VALUE);
		
		if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants. MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){
			 searchParamValue=request.getHeader(BBBCoreConstants.SWD_SEARCH_URL_PARAM_NAME);
			 
			}
		else{
			searchParamValue=request.getParameter(configuredSearchParamName);
			 
		}
		if(BBBCoreConstants.FALSE.equalsIgnoreCase(searchParamValue)){
			isSWD=true;
		}
		String catalogId=searchQuery.getCatalogRef().get(BBBSearchConstants.CATALOG_ID);
		if(StringUtils.isNotBlank(catalogId)){
			StringTokenizer dimTokenizer=null;
			//TODO this block must be fixed before go live
			if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants. MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){
				dimTokenizer=new StringTokenizer(catalogId,BBBCoreConstants.SPACE);
			}
			else{
				dimTokenizer=new StringTokenizer(catalogId,BBBCoreConstants.PLUS);
			}
			
			if(dimTokenizer.countTokens() <3)
			{
				isDepartmentFilterApplied= false;
			}
		}
		
		logDebug("EndecaSearchUtil.getEPHBoostingScheme ,ephQueryScheme in session ["+ephQueryScheme+"],isDepartmentFilterApplied:["+isDepartmentFilterApplied+"],SWD Request:["+isSWD+"]");
		if( (!BBBCoreConstants.EPH_QUERY_SCHEME_DEFAULT_OFF.equalsIgnoreCase(ephQueryScheme) )
				&& (isSWD || (!isDepartmentFilterApplied && BBBCoreConstants.SORT_VALUE.equalsIgnoreCase(ephQueryScheme)) ))
		{
				resetEph=true;
				logDebug("EndecaSearchUtil.getEPHBoostingScheme  resetEph ["+resetEph+"]");
		}
		
		if(BBBUtility.isEmpty(ephQueryScheme) ||  resetEph )
		{
					
					ephQueryScheme=BBBCoreConstants.EPH_QUERY_SCHEME_DEFAULT_OFF;
					
					String sitSpectParameter= getEphLookUpUtil().getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.SITESPECT_EPH_SCHEME_PARAMETER, BBBCoreConstants.SITESPECT_EPH_SCHEME_PARAMETER_VALUE);
					
					String siteSpectQueryScheme=request.getHeader(sitSpectParameter);
					
					
					 
					logDebug("Enter :: EndecaSearchUtil.checkEphQueryScheme: siteSpectQueryScheme:["+siteSpectQueryScheme+"],sitSpectParameter:["+sitSpectParameter+"]");
					
					if (BBBCoreConstants.FILTER_VALUE
							.equalsIgnoreCase(siteSpectQueryScheme)
							|| BBBCoreConstants.SORT_VALUE
									.equalsIgnoreCase(siteSpectQueryScheme)
							|| BBBCoreConstants.SORT_FILTER_OFF
									.equalsIgnoreCase(siteSpectQueryScheme)){
						ephQueryScheme=siteSpectQueryScheme;
					}
					else{
						String configuredQueryScheme= getEphLookUpUtil().getConfigTools().getConfigKeyValue(BBBCoreConstants.OMNITURE_BOOSTING, BBBCoreConstants.EPH_QUERY_SCHEME, BBBCoreConstants.EPH_QUERY_SCHEME_DEFAULT_OFF);
						
						logDebug("Enter :: EndecaSearchUtil.getEPHBoostingScheme: configuredQueryScheme from BCC:["+configuredQueryScheme+"]");
						
						if (BBBCoreConstants.FILTER_VALUE
								.equalsIgnoreCase(configuredQueryScheme)
								|| BBBCoreConstants.SORT_VALUE
										.equalsIgnoreCase(configuredQueryScheme)
								|| BBBCoreConstants.SORT_FILTER_OFF
										.equalsIgnoreCase(configuredQueryScheme)) {
							ephQueryScheme=configuredQueryScheme;
						}
						
					}
					
			// change ephQueryScheme to sort if user doing SWD
			if(BBBCoreConstants.FALSE.equalsIgnoreCase(searchParamValue) && BBBCoreConstants.FILTER_VALUE.equalsIgnoreCase(ephQueryScheme))	
			{	
				ephQueryScheme=BBBCoreConstants.SORT_VALUE;
				logDebug(":: EndecaSearchUtil.getEPHBoostingScheme:  ephQueryScheme changed from FILTER to SORT (SORT Set in session)as SWD in progress.");
			}
		
			request.getSession().setAttribute(BBBCoreConstants.SAVED_EPH_SCHEME,ephQueryScheme);
		
		}
		
		
		logDebug(":: EndecaSearchUtil.getEPHBoostingScheme End: queryScheme:["+ephQueryScheme+"]");
		
		BBBPerformanceMonitor.end("EndecaSearchUtil" + "_getEPHBoostingScheme");
		searchQuery.setEphQueryScheme(ephQueryScheme);
		
		searchQuery.appendBoostingLogs("EndecaSearchUtil.getEPHBoostingScheme ephQueryScheme["+ephQueryScheme+"]");
		return ephQueryScheme;
		
	}
	
	
	
	/**
	 * This method is used to get the boost code from request header to apply
	 * boosting on the search result page
	 * 
	 * @return
	 */
	public String getBoostCode(final DynamoHttpServletRequest pRequest) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION + "_getBoostCode");
		final String siteSpectHeader =  getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, "SITE_SPECT_BOOST_CODE_HEADER", BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE);
		logDebug("EndecaSearch - [getBoostCode] starts with siteSpectHeader param in BCC: " + siteSpectHeader);
		String boostCode = (String) pRequest.getSession().getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE);
		String boostCodeFromHeader = null;
		//Added check because the siteSpect for vendor and boost code is same, so when we get siteSpect value as boostCode then only save it in boostCodeFromHeader
		String activeVendors = getCatalogTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.SEARCH_VENDOR_LIST_ACTIVE, "");
		if(BBBUtility.isNotEmpty(activeVendors) && BBBUtility.isNotEmpty(pRequest.getHeader(siteSpectHeader)) && !activeVendors.contains(pRequest.getHeader(siteSpectHeader))){
			logDebug("EndecaSearchUtil.getBoostCode- siteSpect contains boostCode: " + pRequest.getHeader(siteSpectHeader));
			boostCodeFromHeader = pRequest.getHeader(siteSpectHeader);
		}
		if (!BBBUtility.isEmpty(boostCode) && !BBBUtility.isEmpty(boostCodeFromHeader) && !boostCode.equalsIgnoreCase(boostCodeFromHeader)) {
			boostCode = boostCodeFromHeader;
			pRequest.getSession().setAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, boostCode);
		}
		if (BBBUtility.isEmpty(boostCode)) {
			boostCode = !BBBUtility.isEmpty(boostCodeFromHeader) ? boostCodeFromHeader :BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE;
			pRequest.getSession().setAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, boostCode);
		}
		logDebug("EndecaSearch - [getBoostCode] - Value of boost code = " + boostCode);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION + "_getBoostCode");
		return boostCode;
	}
	
	/**
	 * Method to fetch all dimension values & ids using either dimension name or dimension id
	 * @param pDimensionName
	 * @param pDimensionId
	 * @param returnDimensionValues
	 */
	public void fetchAllDimensionValues(String pDimensionName, String pDimensionId, Map<String, String> returnDimensionValues) {
        
        vlogDebug("Enter :: fetchAllDimensionValues, dimensionName - "+pDimensionName+", dimensionId - "+pDimensionId);
        
        //HashMap<String, String> returnDimensionValues = new HashMap<String, String>();
        if (pDimensionName == null && pDimensionId == null) {
               return;
        }
        
        Map<String, Map<String, String>> allDimensions = new HashMap<String, Map<String, String>>();
        DimensionList dimensions = null;
        Dimension dimension = null;
        ListIterator iterDimensions = null;
        DimValList dimVals = null;
        DimVal dimVal = null;
        String dimensionName = "";
        String dimensionId = "";
        HashMap<String, String> dimensionValues;
        
        
        try {
               
               final String cacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME).get(0);
               int cacheTimeout = 0;
               try {
                     cacheTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT ).get(0));
               } catch (NumberFormatException exception) {
                     logError("EndecaSearchUtil.fetchAllDimensionValues || NumberFormatException occured in " +
                                   GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, exception);
                     cacheTimeout = 3600000;
               } catch (NullPointerException exception) {
                     logError("EndecaSearchUtil.fetchAllDimensionValues || NullPointerException occured in " +
                                   GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, exception);
                     cacheTimeout = 3600000;
               }
               
               final String boostingStategycacheName = getCatalogTools().getConfigKeyValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.BOOSTING_STRATEGY_CACHE_NAME,BBBCoreConstants.BOOSTING_STRATEGY_CACHE_NAME_VALUE);
               
               final String storeAllDimInBoostCache = getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.STORE_ALL_DIMENSION_IN_BOOSTING_CACHE,BBBCoreConstants.TRUE);
               
               UrlGen urlGen = new UrlGen(BBBEndecaConstants.NAVIGATION+BBBCoreConstants.EQUAL+ "0", getQueryGenerator().getEncoding());
               vlogDebug("root navigation query for current dimensions - "+urlGen.toString());
               String keyName = urlGen.toString();
               if(BBBCoreConstants.TRUE.equalsIgnoreCase(storeAllDimInBoostCache))
               {
            	   vlogDebug("fetchAllDimensionValues - get allDimensions to object Cache:storeAllDimInBoostCache:["+storeAllDimInBoostCache+"],boostingStategycacheName:["+boostingStategycacheName+"]");
            	   allDimensions = (HashMap<String, Map<String, String>>) getObjectCache().get(keyName, boostingStategycacheName);
               }
               else{
            	   allDimensions = (HashMap<String, Map<String, String>>) getObjectCache().get(keyName, cacheName);
               }
               
               if(allDimensions == null ||allDimensions.isEmpty()) {
                     allDimensions = new HashMap<String, Map<String, String>>();
                     
                     ENEQuery rootNavQueryObject = new UrlENEQuery(urlGen.toString(), getQueryGenerator().getEncoding());
                     rootNavQueryObject.setNavNumERecs(0);
                     rootNavQueryObject.setNavAllRefinements(true);
                     ENEQueryResults rootNavResults = getEndecaClient().executeQuery(rootNavQueryObject);
                     
                      if(rootNavResults != null) {
                            Navigation navigation = rootNavResults.getNavigation();
                            if(navigation !=null) {
                                   dimensions = navigation.getRefinementDimensions();
                                   iterDimensions = dimensions.listIterator();
                                   
                                   while (iterDimensions.hasNext()) {
                                          dimension = (Dimension) iterDimensions.next();

                                          dimensionName = dimension.getName();
                                          dimensionId = dimension.getId()+"";
                                          dimensionValues = new HashMap<String, String>();
                                          
                                          dimVals = dimension.getRefinements();
                                          final Iterator iterDimVals = dimVals.listIterator();
                                          while (iterDimVals.hasNext()) {
                                                 dimVal = (DimVal) iterDimVals.next();
                                                 dimensionValues.put(dimVal.getName(),dimVal.getId()+"");
                                          }
                                          
                                          allDimensions.put(dimensionName,dimensionValues);
                                          allDimensions.put(dimensionId,dimensionValues);
                                          if(pDimensionName!= null && dimensionName.equals(pDimensionName)
                                                        || pDimensionId!= null && dimensionId.equals(pDimensionId)) {
                                                 returnDimensionValues.putAll(dimensionValues);
                                          }
                                   }
                                   
                            }
                            if (allDimensions != null  && !allDimensions.isEmpty()) {
                                   
                                   if(BBBCoreConstants.TRUE.equalsIgnoreCase(storeAllDimInBoostCache))
                                   {
                                	   vlogDebug("fetchAllDimensionValues - Adding allDimensions to object Cache:storeAllDimInBoostCache:["+storeAllDimInBoostCache+"],boostingStategycacheName:["+boostingStategycacheName+"]");
                                		getObjectCache().put(keyName, allDimensions,boostingStategycacheName );  
                                   }
                                   else{
                                	   vlogDebug("fetchAllDimensionValues - Adding allDimensions to object cacheName:["+cacheName+"],cacheTimeout:["+cacheTimeout+"]");
                                	   	getObjectCache().put(keyName, allDimensions, cacheName, cacheTimeout);
                                   }
                            }
                     }
                     
               } else {
                     if(pDimensionName!= null && allDimensions.get(pDimensionName) != null) {
                            returnDimensionValues.putAll((Map<String, String>) allDimensions.get(pDimensionName));
                     } else if(pDimensionId!= null && allDimensions.get(pDimensionId) != null) {
                            returnDimensionValues.putAll((Map<String, String>) allDimensions.get(pDimensionId));
                     } 
               }
        } catch (UrlENEQueryParseException e) {
               vlogError("UrlENEQueryParseException while fetching all dimensions "+e);
        } catch (ENEQueryException e) {
               vlogError("ENEQueryException while fetching all dimensions "+e);
        } catch (BBBSystemException | BBBBusinessException e) {
               vlogError("BBBBusinessException while fetching all dimensions "+e);
        } 
        
        vlogDebug("Exit :: fetchAllDimensionValues");
 }
	public long getProductCount(String dimId,String siteId) throws BBBBusinessException, BBBSystemException
	 {   
		vlogDebug("EndecaSearchUtil: getProductCount Start:dimId:"+dimId+",siteId:"+siteId);
		long startTime=System.currentTimeMillis();
		String siteDimId =BBBCoreConstants.BLANK;
		if(BBBUtility.isNotBlank(siteId)){
		siteDimId = this.getCatalogId(BBBEndecaConstants.SITE_ID2,getSiteIdMap().get(siteId));
		}
		final UrlGen urlGen = new UrlGen(BBBEndecaConstants.NAVIGATION+BBBCoreConstants.EQUAL+ siteDimId  + BBBCoreConstants.SPACE + dimId , getQueryGenerator().getEncoding());
		String eneQuery = urlGen.toString();
		ENEQuery prodCountQueryObject;
		long prodCount=0;
		try {
			prodCountQueryObject = new UrlENEQuery(eneQuery, getQueryGenerator().getEncoding());
			
			prodCountQueryObject.setNavNumERecs(0);
			prodCountQueryObject.setNavERecsOffset(0L);
			prodCountQueryObject.setNavNumAggrERecs(0);
			prodCountQueryObject.setNavNumBulkERecs(0);
			prodCountQueryObject.setNavNumBulkAggrERecs(0);
			prodCountQueryObject.setNavAllRefinements(false);
			ENEQueryResults endecaResult = getEndecaClient().executeQuery(prodCountQueryObject);
			prodCount= endecaResult.getNavigation().getTotalNumERecs();
			
		} catch (UrlENEQueryParseException urlENEQueryParseException) {
			logError("UrlENEQueryParseException in EndecaSearchUtil: getProductCount for dimId:"+dimId+",siteId:"+siteId,urlENEQueryParseException);
			throw new BBBSystemException("UrlENEQueryParseException in EndecaSearchUtil: getProductCount for dimId:"+dimId+",siteId:"+siteId,urlENEQueryParseException);
		} catch (ENEQueryException eneEQueryException) {
			logError("ENEQueryException in EndecaSearchUtil: getProductCount for dimId:"+dimId+",siteId:"+siteId,eneEQueryException);
			throw new BBBSystemException("ENEQueryException in EndecaSearchUtil: getProductCount for dimId:"+dimId+",siteId:"+siteId,eneEQueryException);
		}
		 finally{
			 vlogDebug("EndecaSearchUtil: getProductCount End:dimId:"+dimId+",siteId:"+siteId+",timeTook:"+(System.currentTimeMillis()-startTime)+",prodCount:"+prodCount);
		 }
		return prodCount; 
	 }
	
	
	public String getCheckListSeoDimId(SearchQuery pSearchQuery,DynamoHttpServletRequest pRequest) {
		
		logDebug("EndecaSearchUtil getCheckListSeoDimId start.");
		long startTime=System.currentTimeMillis();
		String dimensionId= null;
		String	cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.CHECKLIST_PLP_SEO_URL_CACHE_NAME);
		String reqSeoUrl=getGiftRegistryUtils().populateChecklistSEOUrl(pRequest,pSearchQuery);
		reqSeoUrl = reqSeoUrl.substring(1,reqSeoUrl.length()-1);
		CheckListSeoUrlHierarchy checkListSeoUrlHierarchyVo=(CheckListSeoUrlHierarchy) getObjectCache().get(reqSeoUrl, cacheName);
		
		if(checkListSeoUrlHierarchyVo != null){
			dimensionId=checkListSeoUrlHierarchyVo.getSeoUrlDimensionId();
			pSearchQuery.setCheckListSeoUrlHierarchyVo(checkListSeoUrlHierarchyVo);
		}
		vlogDebug("CheckListCategoryHierarchyCacheTool loadProcessedVoInCache End : TimeTook[" + (System.currentTimeMillis() - startTime)+",cacheName:"+cacheName+":dimensionId:"+dimensionId+",reqSeoUrl"+reqSeoUrl);
		return dimensionId;
	 }
	
	/**
	 * @return the ephLookUpUtil 
	 */
	public EPHLookUpUtil getEphLookUpUtil() {
		return ephLookUpUtil;
	}
	/**
	 * @param ephLookUpUtil the ephLookUpUtil to set
	 */
	public void setEphLookUpUtil(EPHLookUpUtil ephLookUpUtil) {
		this.ephLookUpUtil = ephLookUpUtil;
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
	 * @return the giftRegistryUtils
	 */
	public BBBGiftRegistryUtils getGiftRegistryUtils() {
		return giftRegistryUtils;
	}
	
	/**
	 * @param giftRegistryUtils the giftRegistryUtils to set
	 */
	public void setGiftRegistryUtils(BBBGiftRegistryUtils giftRegistryUtils) {
		this.giftRegistryUtils = giftRegistryUtils;
	}
	
}
