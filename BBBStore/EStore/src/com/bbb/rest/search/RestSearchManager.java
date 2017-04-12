package com.bbb.rest.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AllCategoriesVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.AutoSuggestVO;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.PaginationVO;
import com.bbb.search.bean.result.RootCategoryRefinementVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.search.integration.SearchManager;
import com.bbb.search.vo.CategoryNavigationVO;
import com.bbb.seo.CategorySeoLinkGenerator;
import com.bbb.utils.BBBUtility;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
// TODO: Auto-generated Javadoc
/**
 * The Class RestSearchManager.
 *
 * @author agoe21
 */
public class RestSearchManager extends BBBGenericService {

	private static final String CHANNEL_THEME_ID = "channelThemeID";
	private static final String GS_QUERYKEY1 = "GSQuerykey1";



	private static final String GS_QUERY = "GSQuery";
	private static final String REDIRECT_URL_IDENTIFIER="isRedirect=true";



	private static final String CHANNEL_ID = "channelID";
	private static final String GET_ALL_CATEGORIES = "getAllCategories";
	private final String DEFAULT = "DEFAULT";
	private final String SEARCH = "SEARCH";
	private final String L2L3 = "L2L3";
	private final String BRAND = "BRAND";
	private final String COLLEGE = "COLLEGE";
	private CategorySeoLinkGenerator categorySeoLinkGenerator;

	private static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(RestSearchManager.class);

	/** The m search manager. */
	private SearchManager mSearchManager;

	/** String for channelID */
	private String channelIdProperty;
	private Map<String, String> siteHostMap;






	public String getChannelIdProperty() {
		return channelIdProperty;
	}

	public void setChannelIdProperty(String channelIdProperty) {
		this.channelIdProperty = channelIdProperty;
	}



	/** The Catalog Tools. */
	private BBBCatalogTools mCatalogTools;
	private String searchDimConfig;
	private String headerFacetsDim;
	private String mHeaderFacets;
	private String mStoreContextPath; // variable to get context path from properties file - REST Specific
	private Map<String, String> pageTypeMap;
	
	/**
	 * @return the mStoreContextPath
	 */
	public String getStoreContextPath() {
		return mStoreContextPath;
	}

	/**
	 * @param mStoreContextPath the mStoreContextPath to set
	 */
	public void setStoreContextPath(String mStoreContextPath) {
		this.mStoreContextPath = mStoreContextPath;
	}
	/**
	 * @return the searchDimConfig
	 */
	public String getSearchDimConfig() {
		return searchDimConfig;
	}

	/**
	 * @param searchDimConfig the searchDimConfig to set
	 */
	public void setSearchDimConfig(String searchDimConfig) {
		this.searchDimConfig = searchDimConfig;
	}

	/**
	 * @return the headerFacetsDim
	 */
	public String getHeaderFacetsDim() {
		return headerFacetsDim;
	}

	/**
	 * @param headerFacetsDim the headerFacetsDim to set
	 */
	public void setHeaderFacetsDim(String headerFacetsDim) {
		this.headerFacetsDim = headerFacetsDim;
	}



	public final static String ROOT_CATEGORY = "RootCategory";

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/** The m type ahead facets. */
	private String mTypeAheadFacets;

	/**
	 * Gets the type ahead facets.
	 *
	 * @return the type ahead facets
	 */
	public String getTypeAheadFacets() {
		return mTypeAheadFacets;
	}

	/**
	 * Sets the type ahead facets.
	 *
	 * @param pTypeAheadFacets the new type ahead facets
	 */
	public void setTypeAheadFacets(String pTypeAheadFacets) {
		this.mTypeAheadFacets = pTypeAheadFacets;
	}

	/**
	 * Gets the search manager.
	 *
	 * @return the search manager
	 */
	public SearchManager getSearchManager() {
		return mSearchManager;
	}

	/**
	 * Sets the search manager.
	 *
	 * @param pSearchManager the new search manager
	 */
	public void setSearchManager(final SearchManager pSearchManager) {
		this.mSearchManager = pSearchManager;
	}

    /** @return Category SEO Link Generator */
    public final CategorySeoLinkGenerator getCategorySeoLinkGenerator() {
        return this.categorySeoLinkGenerator;
    }

    /** @param seoLinkGenerator */
    public final void setCategorySeoLinkGenerator(final CategorySeoLinkGenerator seoLinkGenerator) {
        this.categorySeoLinkGenerator = seoLinkGenerator;
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

	private String getHeaderFacets() throws BBBBusinessException {
		List<String> headerFacet = new ArrayList<String>();
		try {
			headerFacet = getCatalogTools().getAllValuesForKey(getSearchDimConfig(), getHeaderFacetsDim());
		} catch (BBBBusinessException bbbbEx) {
			throw new BBBBusinessException(BBBCoreErrorConstants.BROWSE_ERROR_1012+" Business Exception occurred while fetching header dimension name from  getHeaderFacets from RestSearchManager",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			throw new BBBBusinessException(BBBCoreErrorConstants.BROWSE_ERROR_1013+" System Exception occurred while fetching header dimension name from  getHeaderFacets from RestSearchManager",bbbsEx);
		}
		if(!headerFacet.isEmpty()){
			mHeaderFacets = headerFacet.get(0);
		}
		//System.out.println("Header facets: "+mHeaderFacets);
		return mHeaderFacets;
	}
	
	/*
	 * Adding new method to send ONLY storeIventory count to improve performance
	 */
	
	public Long storeInventoryCount(Map<String,String> queryParams) throws BBBSystemException,BBBBusinessException{
		SearchResults searchResult = new SearchResults();
		logDebug("Start  storeInventoryCount RestSearchManager"+ System.currentTimeMillis());
		searchResult = performSearch(queryParams);
		
		Long storeInventoryCount =0L;
		
		if(searchResult!=null && searchResult.getBbbProducts()!=null && searchResult.getBbbProducts().getStoreInvCount()>0L){
			storeInventoryCount =   searchResult.getBbbProducts().getStoreInvCount();
		}
		logDebug("End  storeInventoryCount RestSearchManager"+ System.currentTimeMillis());
		return storeInventoryCount;
	}
	/**
	 * Perform search.
	 *
	 * @param pJsonString the json string
	 * @return the search results
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public SearchResults performSearch(Map<String,String> queryParams) throws BBBSystemException,BBBBusinessException{

		 final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();

		 logDebug("Start  Perform Search RestSearchManager"+ System.currentTimeMillis());

		 BBBPerformanceMonitor.end("Start  Perform Search RestSearchManager",myHandleMethod);
		SearchResults searchResult = new SearchResults();

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		
		if(queryParams !=null && !queryParams.isEmpty()){

			final SearchQuery pQuery = createEndecaQuery(queryParams);
			
			// Querying Search Engine with the populated VO to return Results Object.    
			searchResult =  getSearchManager().performSearch(pQuery);
			if(searchResult!=null){

				String redirectUrl = searchResult.getRedirUrl();
				String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
				logDebug("Redirect url from Endeca : " + redirectUrl);
				if(!StringUtils.isEmpty(redirectUrl) && queryParams.get("frmBrandPage") != null && ((String) queryParams.get("frmBrandPage")).equalsIgnoreCase(BBBCoreConstants.FALSE)){
					logDebug("Request is from search page, Not from brand page, Hence creating redirect url");
					if(!redirectUrl.contains(REDIRECT_URL_IDENTIFIER)){
						if (redirectUrl.contains(BBBCoreConstants.QUESTION_MARK)) {
							redirectUrl = redirectUrl + BBBCoreConstants.AMPERSAND + REDIRECT_URL_IDENTIFIER;
						} else {
							redirectUrl = redirectUrl + BBBCoreConstants.QUESTION_MARK + REDIRECT_URL_IDENTIFIER;
						}
					}
				  if(!redirectUrl.startsWith("http") && redirectUrl.startsWith("/")){
					if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
						try {
							List<String> configValue = getCatalogTools().getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
							if(configValue != null && configValue.size() > 0){
								String newRedirectUrl = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + configValue.get(0) + getStoreContextPath() + redirectUrl;
								logDebug("Appending Server and schema name to redirect url : " + newRedirectUrl);
								 //set absolute redirect url
								searchResult.setRedirUrl(newRedirectUrl);
							}
						} catch (BBBSystemException e) {
							MLOGGING.logError("RestSearchManager.performSearch :: System Exception occured while fetching config value for config key " + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
						} catch (BBBBusinessException e) {
							MLOGGING.logError("RestSearchManager.performSearch :: Business Exception occured while fetching config value for config key " + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
						}
					}
				}else{
					searchResult.setRedirUrl(redirectUrl);
				}
			  }
			   else if(queryParams.get("frmBrandPage") != null  && ((String) queryParams.get("frmBrandPage")).equalsIgnoreCase(BBBCoreConstants.TRUE)){
				  if(MLOGGING.isLoggingDebug()){
					  MLOGGING.logDebug("Request is from brand page,hence making redirect url as blank :: redirectUrl - " + searchResult.getRedirUrl());
				  }
				  searchResult.setRedirUrl(BBBCoreConstants.BLANK);
			  }
				//for adding total filter count
				  if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
					  			
					  if(null != searchResult.getDescriptors()){
						  int totCount =0;
						  for(CurrentDescriptorVO currentDescriptorVO : searchResult.getDescriptors()){
							  if(!("RECORD TYPE").equalsIgnoreCase(currentDescriptorVO.getRootName())){
								  totCount = totCount + 1;
							  }
						  }
						  searchResult.setFilterCount(totCount);
					  }
				  }
			} else {
				// To handle special character search query
				searchResult = new SearchResults();
				BBBProductList bbbProducts = new BBBProductList();
				bbbProducts.setBBBProductCount(0);
				EndecaSearch search = (EndecaSearch) getSearchManager()
						.getSearchHandler();
				searchResult = search.getEndecaClient().getResultObject(
						bbbProducts, new PaginationVO(), null,
						new ArrayList<FacetParentVO>(),
						new ArrayList<CurrentDescriptorVO>(), null,
						new ArrayList<AutoSuggestVO>(), null);

			}
			
			
			// Invoking GetOmnitureVariableDroplet to retrieve Omniture Variables
			pRequest.setParameter(BBBCoreConstants.PAGE_NAME, getPageType(pQuery));
			String lOmnitureVariable = this.getCatalogTools().getOmnitureVariable(pRequest);
			searchResult.setOmnitureVariable(lOmnitureVariable);
			if(null != pRequest.getSession().getAttribute(BBBCoreConstants.IS_ENDECA_CONTROL)) { 
				searchResult.setEndecaControl((boolean) pRequest.getSession().getAttribute(BBBCoreConstants.IS_ENDECA_CONTROL));
			} else {
				searchResult.setEndecaControl(false);
			}
		 }
		else{
			throw new BBBBusinessException(BBBSearchBrowseConstants.EMPTY_PARAMETER," Empty Required paramter");
		}
		logDebug("End  Perform Search RestSearchManager"+ System.currentTimeMillis());
		return searchResult;
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
	public String getPageType(SearchQuery searchQuery) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.REST_SEARCH_MANAGER + "_getPageType");		
		String pageType = null;
		if(null != searchQuery) {
			logDebug(" [getPageType] - searchQuery: " + searchQuery);
			pageType = DEFAULT;
			if(searchQuery.isFromBrandPage()) {
				pageType = BRAND;
			} else if (searchQuery.getKeyWord() != null) {
				pageType = SEARCH;
			} else if (searchQuery.isFromCollege()) {
				pageType = COLLEGE;
			} else if (searchQuery.isFromCategoryLanding()) {
				pageType = L2L3;
			}
			logDebug(" [getPageType] - Request is from page: " + pageType);
		}		
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.REST_SEARCH_MANAGER + "_getPageType");
		return pageType;
	}
	
	/**
	 * MobileWeb and STOFU related
	 * Returns Tree structure for header
	 *
	 * The tree strucutre contains all level of categories in tree format
	 * including top level (root), 2nd level and 3rd level categories
	 *
	 * @param queryParams the map of input query parameters.
	 * @return AllCategoriesVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public AllCategoriesVO getAllCategories(Map<String,String> queryParams) throws BBBSystemException,
			BBBBusinessException{
		//Mehod for Story Id BBBH-164 - To fetch L1/L2/L3 categories in a single pull for MobileWeb.
		logDebug("Inside class: RestSearchManager,  method :getAllCategories");

		BBBPerformanceMonitor.start(GET_ALL_CATEGORIES);
		SearchResults searchResults = performSearch(queryParams);
		List<FacetParentVO> facets = searchResults.getFacets();
		AllCategoriesVO allCategoriesVO = new AllCategoriesVO();
		try {
			for (FacetParentVO facet : facets) {
				if (facet != null
						&& facet.getName().equalsIgnoreCase(BBBCoreConstants.DEPARTMENT)) {
					List<FacetRefinementVO> rootFacetValues = facet
							.getFacetRefinement();

					List<RootCategoryRefinementVO> rootCategoriesTree = new ArrayList<RootCategoryRefinementVO>();
					for (FacetRefinementVO rootFacetValue : rootFacetValues) {
						RootCategoryRefinementVO rootCategory = new RootCategoryRefinementVO();

						rootCategory
								.setCatalogId(rootFacetValue.getCatalogId());
						rootCategory.setName(rootFacetValue.getName());
						if(null != rootFacetValue.getCatalogId()){
							logDebug("Inside class: RestSearchManager,  method :getAllCategories L1 category Id: " + rootFacetValue.getCatalogId());
						}
						rootCategory.setQuery(rootFacetValue.getQuery());
						rootCategory.setSize(rootFacetValue.getSize());
						rootCategory.setShopAllCatUrl(this.getCategorySeoLinkGenerator().formatUrl(rootFacetValue.getCatalogId(),
										rootFacetValue.getName()));
						List<CategoryNavigationVO> subCategoriesList = getRootCategoryNavigation(rootFacetValue.getCatalogId(),false);
 						Iterator<CategoryNavigationVO> itNavigation = subCategoriesList.iterator();
 						Map<String, CategoryParentVO> subCategoriesTree = new HashMap<String, CategoryParentVO>();
 						List<String> orderedkeys = new ArrayList<String>();
						while (itNavigation.hasNext()) {

							CategoryNavigationVO catNavigationVO = itNavigation.next();
							subCategoriesTree.put(catNavigationVO.getCatgoryID(),catNavigationVO.getCategoryParent());
							orderedkeys.add(catNavigationVO.getCatgoryID());
						}
						   //BSL-5841 | L2/L3 Exlusion List to hide nodes doesn't flow through to mobile
						if (subCategoriesTree != null) {
							subCategoriesTree = this.getSearchManager().filterL2L3ExclusionItemsFromCategoryTree(rootFacetValue.getCatalogId(), subCategoriesTree);
							logDebug("Inside class: RestSearchManager,  method :getRootCategoryNavigation map " + subCategoriesTree);
						}
						rootCategory.setSubCategoriesTree(subCategoriesTree);
						rootCategory.setOrderedKeys(orderedkeys);
						rootCategoriesTree.add(rootCategory);
					}

					allCategoriesVO.setRootCategoriesTree(rootCategoriesTree);
					break;

				}
			}
		} catch (BBBSystemException bbbSystemException) {
			throw new BBBSystemException(BBBSearchBrowseConstants.SYSTEM_ERROR,
					BBBSearchBrowseConstants.SYSTEM_ERROR_MESSAGE);
		}

		logDebug("Exiting class: RestSearchManager,  method :getAllCategories");

		BBBPerformanceMonitor.end(GET_ALL_CATEGORIES);
		return allCategoriesVO;
	}

	public SearchQuery createEndecaQuery(Map<String, ?> queryParams) throws BBBBusinessException,
	BBBSystemException {
		List<String> pFacets = new ArrayList<String>();
		final SearchQuery pQuery = new SearchQuery();
		final SortCriteria sortCriteria = new SortCriteria();
		final Map<String,String> catalogRef = new HashMap<String, String>();
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		// Setting hostname as mobile hostname
		String mobileHostName = pRequest.getHeader(BBBCoreConstants.MOBILE_HOST_NAME);
		final String siteId = SiteContextManager.getCurrentSiteId();
		pQuery.setSiteId(siteId);
		if(!BBBUtility.isEmpty(mobileHostName)){
			pQuery.setHostname(mobileHostName);
		}else{
			pQuery.setHostname(getSiteHostMap().get(siteId));
		}

		//adding new parameters for local store plp
		if(BBBUtility.isNotEmpty((String)queryParams.get("storeId"))){
			pQuery.setStoreId((String)queryParams.get("storeId"));
		}
		
		//adding new parameters for local store plp
				if(BBBUtility.isNotEmpty((String)queryParams.get("storeIdFromAjax"))){
					pQuery.setStoreIdFromAjax((String)queryParams.get("storeIdFromAjax"));
				}
				
				
		if(BBBUtility.isNotEmpty((String)queryParams.get("onlineTab"))){
			pQuery.setOnlineTab(Boolean.parseBoolean((String)queryParams.get("onlineTab")));
		}

		if(BBBUtility.isNotEmpty((String)queryParams.get("channelID"))){
			pQuery.setChannelId((String)queryParams.get("channelID"));
		}
		if(BBBUtility.isNotEmpty((String)queryParams.get(BBBCoreConstants.PRODUCT_REQUIRED))){
			pQuery.setProductsReq((String)queryParams.get(BBBCoreConstants.PRODUCT_REQUIRED));
		}
		// RM 21910 : START : added for type ahead search based on product name for STOFU
		if(BBBUtility.isNotEmpty((String) queryParams.get(CHANNEL_THEME_ID))){
			pQuery.setChannelThemeId((String) queryParams.get(CHANNEL_THEME_ID));
		}
		// RM 21910 : START : added for type ahead search based on product name for STOFU

		if(BBBUtility.isNotEmpty((String)queryParams.get("searchProperty"))){
			catalogRef.put("searchProperty", (String)queryParams.get("searchProperty"));
		}
		// RM 21910 : END : added for type ahead search based on product name for STOFU
		if(BBBUtility.isNotEmpty((String)queryParams.get("isHeader")) && "Y".equalsIgnoreCase((String)queryParams.get("isHeader")) ){
			if(null!=getHeaderFacets())
				pFacets = Arrays.asList(getHeaderFacets().split(","));
			catalogRef.put("catalogId", (String)queryParams.get("catalogId"));
			catalogRef.put("catalogRefId", (String)queryParams.get("catalogRefId"));
			// Set flag to indicate that Search is for Header section and bypass useless typecasting response.
			pQuery.setHeaderSearch(true);
		}
		else
		{
			pQuery.setHeaderSearch(false);
			if(null!=getHeaderFacets())
				pFacets = Arrays.asList(getSearchManager().getFacets().split(","));


			// Check for valid keyword in Input.
			if(BBBCoreConstants.TRUE.equalsIgnoreCase((String) queryParams.get("frmBrandPage"))){
				pQuery.setKeyWord(((String)queryParams.get("keyword")).trim());
			} else if (BBBUtility.isNotEmpty((String) queryParams.get("keyword"))) {
				//if (((String) queryParams.get("keyword")).toString().length() >= 2) {
					pQuery.setKeyWord(((String) queryParams.get("keyword")).trim());
				//} else {
					//throw new BBBBusinessException(BBBSearchBrowseConstants.ERROR_INVALID_KEYWORD,"Invalid search key entered");
				//}
			}
			if(BBBUtility.isNotEmpty((String)queryParams.get("catalogId")) || BBBUtility.isNotEmpty((String)queryParams.get("catalogRefId")) ){
				// Check if the request if for a particular PLP.
				if(BBBUtility.isNotEmpty((String)queryParams.get("catalogId"))){
					catalogRef.put("catalogId", (String)queryParams.get("catalogId"));
				}
				if(BBBUtility.isNotEmpty((String)queryParams.get("catalogRefId"))){
					catalogRef.put("catalogRefId", (String)queryParams.get("catalogRefId"));
				}
				pQuery.setFromCategoryLanding(true);
			}
			// Check If the request is to fetch all results associated with a Brand.
			if(("true").equalsIgnoreCase((String)queryParams.get("frmBrandPage"))){
				pQuery.setFromBrandPage(true);
				catalogRef.put("frmBrandPage", "true");
			}
			else{
				pQuery.setFromBrandPage(false);
			}

			// Check If the request is to fetch all results associated with a College.
			if(("true").equalsIgnoreCase((String)queryParams.get("fromCollege"))){
				pQuery.setFromCollege(true);
				catalogRef.put("fromCollege", "true");
			}
			else{
				pQuery.setFromCollege(false);
			}



			// Check for Per Page Results count variable if present and if not, set it to 10 by default.
			if(null != (String)queryParams.get("pagFilterOpt") && !BBBUtility.isEmpty((String)queryParams.get("pagFilterOpt"))){
				pQuery.setPageSize((String)queryParams.get("pagFilterOpt"));
			}
			else{
				pQuery.setPageSize("10");
			}

			// Check for current page Number.
			if(null != (String)queryParams.get("pagNum") && !BBBUtility.isEmpty((String)queryParams.get("pagNum"))){
				pQuery.setPageNum((String)queryParams.get("pagNum"));
			}
			else{
				pQuery.setPageNum("1");
			}

			// For a Keyword Search and first time query for Search Results Page, initialise the catalogId in map to the dimension id of RecordType > Product to render Product records.
			/*if( catalogRef.isEmpty() || BBBUtility.isEmpty(catalogRef.get("catalogId")) ){
				catalogRef.put("catalogId", getSearchManager().getCatalogId("Record Type", "Product"));
			}
			pQuery.setCatalogRef(catalogRef);*/

			// Populating Sorting field and order information.

			// Check for valid Sort Field.
			if(null != (String)queryParams.get("pagSortOpt") && !BBBUtility.isEmpty((String)queryParams.get("pagSortOpt"))){
			if (queryParams.get("pagSortOpt").toString().equalsIgnoreCase("Best")){
				sortCriteria.setSortFieldName(null);
			}
			else{
				sortCriteria.setSortFieldName((String)queryParams.get("pagSortOpt"));
			}
		}
			else{
				sortCriteria.setSortFieldName("Date");
			}

			// Check for Sort Order if set.
			if(("0").equalsIgnoreCase((String)queryParams.get("pagSortOptOrder"))){
				sortCriteria.setSortAscending(true);
			}
			else{
				sortCriteria.setSortAscending(false);
			}

			pQuery.setSortCriteria(sortCriteria);
		}
		pQuery.setQueryFacets(pFacets);
		pQuery.setSortCriteria(sortCriteria);
		pQuery.setCatalogRef(catalogRef);
		if(null!=pQuery.getKeyWord() && (pQuery.getCatalogRef().isEmpty()||null==pQuery.getCatalogRef().get("catalogId"))){
			catalogRef.put("catalogId", getSearchManager().getCatalogId("Record Type", "Product"));
			pQuery.setCatalogRef(catalogRef);
		}
		if(BBBUtility.isNotEmpty((String)queryParams.get(BBBCoreConstants.CHECKLIST_ID)) && BBBUtility.isNotEmpty((String)queryParams.get(BBBCoreConstants.CHECKLIST_CATEGORY_ID))) {
			pQuery.setChecklistId((String)queryParams.get(BBBCoreConstants.CHECKLIST_ID));
			pQuery.setChecklistCategoryId((String)queryParams.get(BBBCoreConstants.CHECKLIST_CATEGORY_ID));
			pQuery.setC1name((String)queryParams.get(BBBCoreConstants.CAT1_NAME));
			pQuery.setC2name((String)queryParams.get(BBBCoreConstants.CAT2_NAME));
			pQuery.setC3name((String)queryParams.get(BBBCoreConstants.CAT3_NAME));
			pQuery.setChecklistName((String)queryParams.get(BBBCoreConstants.CHECKLIST_DISPLAY_NAME));
			pQuery.setFromChecklistCategory(true);
		}
		
		
		return pQuery;
	}


	/**
	 * This method.
	 *
	 * @param searchKeyword : To perform type ahead search
	 * @return the facet query results
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	public FacetQueryResults performTypeAheadSearch(final String searchKeyword,final boolean showPopularTerms) throws BBBSystemException,BBBBusinessException{

		FacetQueryResults facetQueryResult = new FacetQueryResults();
		try{
			final FacetQuery pFacetQuery = new FacetQuery();

			/* START R2.1 TypeAhead for Most Popular Keywords */

			if(showPopularTerms){
				pFacetQuery.setShowPopularTerms(true);
			}

			/* END   R2.1 TypeAhead for Most Popular Keywords */

			if (BBBUtility.isNotEmpty(searchKeyword))
			{
				// Need to make search keyword size field configurable
				//if(searchKeyword.length()>=2){
					pFacetQuery.setKeyword(searchKeyword);
				/*}
				else{
					throw new BBBBusinessException(BBBSearchBrowseConstants.ERROR_SEARCH_KEYWORD_LESS_THAN_3,BBBSearchBrowseConstants.ERROR_SEARCH_KEYWORD_LESS_THAN_3_MESSAGE);
				}*/


				// Fetching Site Id from Site Context and setting in SearchQuery.
				final String pSiteId = SiteContextManager.getCurrentSiteId();
				pFacetQuery.setSiteId(pSiteId);

				// Setting the Facets List for which the results should be returned.
				List<String> pFacets = Arrays.asList(getTypeAheadFacets().split(","));
				pFacetQuery.setFacets(pFacets);

				facetQueryResult = getSearchManager().performTypeAheadSearch(pFacetQuery);
			}
			else{
				throw new BBBBusinessException(BBBSearchBrowseConstants.ERROR_INVALID_KEYWORD,BBBSearchBrowseConstants.ERROR_INVALID_KEYWORD);
			}
		}
		catch(BBBSystemException e){
			throw new BBBSystemException(BBBSearchBrowseConstants.SYSTEM_ERROR,BBBSearchBrowseConstants.SYSTEM_ERROR_MESSAGE);
		}
		return facetQueryResult;
	}

	/**
	 * Gets the all navigation minus phantom categories
	 *
	 * @param pCatalogId catalog id
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CategoryNavigationVO> getRootCategoryNavigation(final String pCatalogId) throws BBBSystemException, BBBBusinessException{
		//setting default checkPhantom flag as true
		return getRootCategoryNavigation(pCatalogId, true);
	}
	/**
	 * Gets the all navigation.
	 *
	 * @param pCatalogId catalog id
	 * @param rootCategory root category of catalog tree
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
public List<CategoryNavigationVO> getRootCategoryNavigation(final String pCatalogId, boolean checkPhantom) throws BBBSystemException, BBBBusinessException{

		try{

			final SearchQuery pQuery = new SearchQuery();
			// Fetching Site Id from Site Context and setting in SearchQuery.
	        final String pSiteId = SiteContextManager.getCurrentSiteId();

			HashMap<String, String> pCatalogRef = new HashMap<String, String>();
			pCatalogRef.put("catalogId", pCatalogId);
			String root = null;
			if(null != this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, pSiteId+ROOT_CATEGORY)){
				root = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, pSiteId+ROOT_CATEGORY).get(0);
			}
			pCatalogRef.put("root", root);
			//BBBSL-11385 | Setting check phantom as true
			if(checkPhantom){
				pQuery.setCheckPhantom(checkPhantom);
			}
			pQuery.setCatalogRef(pCatalogRef);
			pQuery.setHeaderSearch(false);
			if (StringUtils.isEmpty(pCatalogId))
			{
				throw new BBBBusinessException(BBBSearchBrowseConstants.ERROR_CATALOGID_MISSING, "Mandatory parameter catalog id missing.");

			}else if (StringUtils.isEmpty(root))
			{
				throw new BBBBusinessException(BBBSearchBrowseConstants.ERROR_ROOT_CATEGORYID_MISSING, "Mandatory parameter root category id missing.");
			}

	        pQuery.setSiteId(pSiteId);
	        Map<String,CategoryParentVO> map = getSearchManager().getCategoryTree(pQuery);
	        List<CategoryNavigationVO> list = new ArrayList<CategoryNavigationVO>();
	        if(map != null){
	        for(String key : map.keySet())
	        {
	        	if(key != null){
	        	CategoryNavigationVO vo = new CategoryNavigationVO();
	        	vo.setCatgoryID(key);
	        	vo.setCategoryParent(map.get(key));

	        	//BPSI-115 Get the SEO URL for subcategories
	        	DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
	        	String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
	        	if(MLOGGING.isLoggingDebug()){
	        		MLOGGING.logDebug("getRootCategoryNavigation: The request is from channel" + channel );
	        	}
	        	if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))
						&& null != vo.getCategoryParent()){
					if(!BBBUtility.isListEmpty(vo.getCategoryParent().getCategoryRefinement())){
						for(final CategoryRefinementVO refinementVO: vo.getCategoryParent().getCategoryRefinement())
						{
							if(refinementVO.getQuery() != null) {
								if(MLOGGING.isLoggingDebug()){
									MLOGGING.logDebug("getRootCategoryNavigation: Getting the SEO URL for category : " + refinementVO.getQuery() + refinementVO.getName());
								}
								refinementVO.setSubCatURL(this.getCategorySeoLinkGenerator().formatUrl(refinementVO.getQuery(),
										refinementVO.getName()));
	                       		}
						}
					}else {
						vo.getCategoryParent().setCategorySEOUrl(this.getCategorySeoLinkGenerator().formatUrl(vo.getCategoryParent().getQuery(),
								vo.getCategoryParent().getName()));
					}
				}
	        	list.add(vo);}
	        }
	       }
	        return list;

		}catch(BBBSystemException e){
			throw new BBBSystemException(BBBSearchBrowseConstants.SYSTEM_ERROR,BBBSearchBrowseConstants.SYSTEM_ERROR_MESSAGE);
		}

	}

	/**
	 * Gets the all Colleges for a given state.
	 *
	 * @param pStateId the state Id.
	 * @return the all navigation
	 * @throws BBBSystemException the bBB system exception
	 * @throws BBBBusinessException the bBB business exception
	 */
	public  List<CollegeVO> getCollegesByState(String pStateCode) throws BBBSystemException,BBBBusinessException{
		try{

			String pStateId = null;

			if(BBBUtility.isEmpty(pStateCode)){
				pStateId = "0";
			}
			else{
				for(StateVO sVo : this.getCatalogTools().getStateList()){
					if(pStateCode.equalsIgnoreCase(sVo.getStateCode())){
						if(null != sVo.getStateName()){
							pStateId = this.getSearchManager().getCatalogId(BBBSearchBrowseConstants.SCHOOL_STATE, sVo.getStateName());
						}
					}
				}
				if(null == pStateId){
					throw new BBBBusinessException(BBBCoreErrorConstants.BROWSE_ERROR_1044,"No Colleges are available for this state code.");
				}
			}
			return getSearchManager().getAllColleges(pStateId);
		}
		catch(BBBSystemException e){
			throw new BBBSystemException(BBBSearchBrowseConstants.SYSTEM_ERROR,BBBSearchBrowseConstants.SYSTEM_ERROR_MESSAGE);
		}
	}
	
	public Map<String, String> getSiteHostMap() {
		return siteHostMap;
	}
	public void setSiteHostMap(Map<String, String> siteHostMap) {
		this.siteHostMap = siteHostMap;
	}
}