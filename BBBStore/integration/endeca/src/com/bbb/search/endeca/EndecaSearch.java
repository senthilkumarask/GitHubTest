package com.bbb.search.endeca;

import static com.bbb.search.endeca.constants.BBBEndecaConstants.URL_ENCODING;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.bbb.commerce.browse.vo.BrandsListingVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.KickStarterPriceVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBSearchConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.ISearch;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.AttributesParamVO;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResult;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.PaginationVO;
import com.bbb.search.bean.result.PromoVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.bean.result.SkuVO;
import com.bbb.search.endeca.assembler.content.ContentPathResolver;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.customizer.EndecaResponseCustomizer;
import com.bbb.search.endeca.tools.EndecaSearchTools;
import com.bbb.search.endeca.util.EndecaConfigUtil;
import com.bbb.search.endeca.vo.EndecaQueryVO;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.endeca.infront.assembler.AssemblerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.content.ContentException;
import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValIdList;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.DimensionSearchResult;
import com.endeca.navigation.DimensionSearchResultGroup;
import com.endeca.navigation.DimensionSearchResultGroupList;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ENEQueryToolkit;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.Property;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.StratifiedDimVal;
import com.endeca.navigation.StratifiedDimValList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;
import com.endeca.navigation.UrlGen;
import com.endeca.soleng.urlformatter.UrlFormatException;
import com.endeca.soleng.urlformatter.UrlState;
import com.endeca.soleng.urlformatter.basic.BasicUrlFormatter;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * This class implements ISearch interface and actually talks with Endeca.
 *
 * @author agoe21
 *
 */
public class EndecaSearch extends BBBGenericService implements ISearch {

	//Component Injection
	private EndecaClient endecaClient;
	private EndecaQueryGenerator queryGenerator;
	private HashMap<String, String> ratingFilterMap;
	private boolean stagingServer;
	// EndecaConfigUtil used for all configuration related to results list and
	// refinements
	private EndecaConfigUtil configUtil;

	// ContentPathResolver used for resolving contentCollection path for
	// retrieving assembler content
	private ContentPathResolver contentPathResolver;
	public static final String DIM_DISPLAY_CONFIGTYPE = "DimDisplayConfig";
	public static final String BRAND_KEY = "Brand";
	public static final String COLORGROUP_KEY = "ColorGroup";
	
	private EndecaResponseCustomizer mCustomizers[];

	public EndecaResponseCustomizer[] getCustomizers() {
		return mCustomizers;
	}

	public void setCustomizers(EndecaResponseCustomizer mCustomizers[]) {
		this.mCustomizers = mCustomizers;
	}

	/*
	 * private BBBInternationalCatalogTools internationalCatalogTools; private
	 * BBBLocalCacheContainer skuAttributeCache; public BBBLocalCacheContainer
	 * getSkuAttributeCache() { return skuAttributeCache; } public void
	 * setSkuAttributeCache(BBBLocalCacheContainer skuAttributeCache) {
	 * this.skuAttributeCache = skuAttributeCache; } public
	 * BBBInternationalCatalogTools getInternationalCatalogTools() { return
	 * internationalCatalogTools; } public void setInternationalCatalogTools(
	 * BBBInternationalCatalogTools internationalCatalogTools) {
	 * this.internationalCatalogTools = internationalCatalogTools; }
	 */

	private EndecaSearchUtil mSearchUtil;
	//92F

	public EndecaSearchUtil getSearchUtil() {
		return mSearchUtil;
	}

	public void setSearchUtil(EndecaSearchUtil mSearchUtil) {
		this.mSearchUtil = mSearchUtil;
	}

	/*
	 * //Property to hold Dimension names Map. private Map<String, String>
	 * dimensionMap; //Property to hold siteIds Map. private Map<String, String>
	 * siteConfig;
*/

	//Property to hold siteIds Map.
	private Map<String, String> siteIdMap;

	//Property to hold Department Dimension Name Map.
	//private Map<String, String> departmentConfig;

	//Property to hold JSON property names for "P_Swatch_Info" Endeca Property.
	private Map<String, String> swatchInfoMap;

	/*
	 * //Property to hold Property name map. private Map<String, String>
	 * propertyMap;
*/
	//Property to hold Dimension Property name(s) map.
	private Map<String, String> dimPropertyMap;

	//Property to hold Sort Property name map.
	//private Map<String, String> sortFieldMap;
	//Property to hold Catridge name map.
/*	private Map<String, String> catridgeNameMap;*/

	

	//Property to hold Catridge Property names.
/*	private Map<String, String> attributePropmap;*/

	//Property to hold maximum tabs to be shown in Tabbed Category navigation.
	private int maxCatTabs;

	// Property to limit the number of Departments/Brands in TypeAhead Flyout
	// to.
	private int maxMatches;

	// Property to get the PLSR placeholder.
/*	private String placeHolder;*/

	

	// Property to Node Type value for Categories eligible for Portrait Images
	// on Product Listing Page.
	private String nodeType;
	private BBBCatalogTools mCatalogTools;
	protected static final String GETTING_CACHE_TIMEOUT_FOR="getting cacheTimeout for ";
	private int productCacheTimeout;
	private int keywordCacheTimeout;
	private int searchCacheTimeout;
	private int headerCacheTimeout;
	private IndirectUrlTemplate brandTemplate;
	private EndecaSearchTools endecaSearchTools;
	
	public EndecaSearchTools getEndecaSearchTools() {
		return endecaSearchTools;
	}

	/**
	 * @param endecaSearchTools
	 */
	public void setEndecaSearchTools(EndecaSearchTools endecaSearchTools) {
		this.endecaSearchTools = endecaSearchTools;
	}

	/*private BBBLocalCacheContainer configCacheContainer;*/

	//Property to hold image property.
	private Map<String, String> mImagePropertyMap;

	/**
	 * @return the brandTemplate
	 */
	public IndirectUrlTemplate getBrandTemplate() {
		return this.brandTemplate;
	}

	/**
	 * @param brandTemplate
	 *            the brandTemplate to set
	 */
	public void setBrandTemplate(IndirectUrlTemplate brandTemplate) {
		this.brandTemplate = brandTemplate;
	}

	/**
	 * To return image properties
	 *
	 * @return mImagePropertyMap - to return image properties Map
	 */
	public Map<String, String> getImagePropertyMap() {
		return this.mImagePropertyMap;
	}

	/**
	 * To set image properties
	 *
	 * @param pImagePropertyMap
	 *            - image properties Map
	 */
	public void setImagePropertyMap(final Map<String, String> pImagePropertyMap) {
		this.mImagePropertyMap = pImagePropertyMap;
	}

	/**
	 * @return headerCacheTimeout
	 */
	public int getHeaderCacheTimeout() {
		return this.headerCacheTimeout;
	}

	/**
	 * @param headerCacheTimeout
	 */
	public void setHeaderCacheTimeout(final int headerCacheTimeout) {
		this.headerCacheTimeout = headerCacheTimeout;
	}

	/**
	 * @return searchCacheTimeout
	 */
	public int getSearchCacheTimeout() {
		return this.searchCacheTimeout;
	}

	/**
	 * @param searchCacheTimeout
	 */
	public void setSearchCacheTimeout(final int searchCacheTimeout) {
		this.searchCacheTimeout = searchCacheTimeout;
	}

	/**
	 * @return The keyword cache timeout default value
	 */
	public int getKeywordCacheTimeout() {
		return this.keywordCacheTimeout;
	}

	/**
	 * @param keywordCacheTimeout
	 */
	public void setKeywordCacheTimeout(final int keywordCacheTimeout) {
		this.keywordCacheTimeout = keywordCacheTimeout;
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

/*	*//**
	 * @return dimDisplayMapConfig
			 */
	/*
	 * public String getDimDisplayMapConfig() { return this.dimDisplayMapConfig;
	 * }
	*//**
	 * @param dimDisplayMapConfig
		 */
	/*
	 * public void setDimDisplayMapConfig(final String dimDisplayMapConfig) {
	 * this.dimDisplayMapConfig = dimDisplayMapConfig; }
	*//**
	 * @return dimNonDisplayMapConfig
		 */
	/*
	 * public String getDimNonDisplayMapConfig() { return
	 * this.dimNonDisplayMapConfig; }
	*//**
	 * @param dimNonDisplayMapConfig
		 */
	/*
	 * public void setDimNonDisplayMapConfig(final String
	 * dimNonDisplayMapConfig) { this.dimNonDisplayMapConfig =
	 * dimNonDisplayMapConfig; }
	*//**
	 * @return scene7Path
		 */
	/*
	 * public String getScene7Path() { return this.scene7Path; }
	*//**
	 * @param dimNonDisplayMapConfig
	 *//*
		 * public void setScene7Path(final String scene7Path) { this.scene7Path
		 * = scene7Path; }
*/

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

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
	 * @param mObjectCache
	 *            the mObjectCache to set
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.bbb.search.ISearch#performSearch(com.bbb.search.bean.query.
	 * SearchQuery)
	 */
	@Override
	public SearchResults performSearch(final SearchQuery pSearchQuery) throws BBBBusinessException,BBBSystemException {
		boolean flag	=	Boolean.valueOf((String)ServletUtil.getCurrentRequest().getSession().getAttribute("isCacheEnable"));
		return performSearch(pSearchQuery,!flag);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.bbb.search.ISearch#performSearch(com.bbb.search.bean.query.
	 * SearchQuery)
	 */
	@Override
	public SearchResults performSearch(final SearchQuery pSearchQuery, boolean cacheSkip)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.performSearch method.");

		final String methodName = BBBCoreConstants.ENDECA_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		SearchResults bsVO = null;
		AttributesParamVO attributeParamVO = new AttributesParamVO();
		DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
		String channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);

		if (BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())
				|| BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())
				|| BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())) {

			populateSearchGroup(pSearchQuery, pRequest); // Calling the search
															// config group
															// param for Search
															// Configurable
															// story
		}

		String ntxSearchMode=null;														// DRACO Merge

		ntxSearchMode = BBBUtility.getCookie(pRequest, BBBEndecaConstants.SEARCH_MODE); // PS-27418
																						// -
																						// Ability
																						// to
																						// test
																						// ENDECA
																						// Search
																						// configurations
																						// with
																						// SiteSpect
																						// START

		if(BBBUtility.isBlank(ntxSearchMode)){
			ntxSearchMode = pRequest.getQueryParameter(BBBEndecaConstants.NAV_SEARCH_MODE); // BED
																							// 455
																							// defect
		}

		logDebug("Ntx Search Mode in the URL for the facet and the pagination tabs" +ntxSearchMode);
		if(BBBUtility.isNotEmpty(ntxSearchMode)){
			pSearchQuery.setSearchMode(ntxSearchMode);
			logDebug("searchMode set in pSearchQuery"+pSearchQuery.getSearchMode());
		}
		boolean isPartialMatchSearch = false;
		try {

			//92F start
			String enteredSearchTerm = "";
			logDebug("Entered SearchTerm is: "+pSearchQuery.getKeyWord());
			List<String> stopWrdRemovedString=new ArrayList<String>();
			logDebug("searchMode from url in endecaSearch.performSearch() "+pSearchQuery.getSearchMode());
			// Defect #24526 :searchMode enetered in URL should be excluded from
			// partial search
			if (pSearchQuery.getKeyWord() != null
					&& !(BBBEndecaConstants.TRUE.equalsIgnoreCase(pSearchQuery.getPartialFlag()))
					&& ((pSearchQuery.getSearchMode() != null
							&& (BBBCmsConstants.SEARCH_MODE_ALLPARTIAL).equalsIgnoreCase(pSearchQuery.getSearchMode()))
							|| null==pSearchQuery.getSearchMode())){
				//check if for from brand page keyword is populated or null??
				/*
				 * logDebug("making call to removedStopwords"); commented for
				 * defect PS-25219
				 * enteredSearchTerm=removeStopWords(pSearchQuery.getKeyWord());
				 * logDebug("enteredSearchTerm after Stop word removal;:"
				 * +enteredSearchTerm);
				 */
				enteredSearchTerm = pSearchQuery.getKeyWord(); // added for
																// defect
																// PS-25219
				// Making the check for partial search and setting the
				// searchMode
				stopWrdRemovedString = new ArrayList<String>(Arrays
						.asList(enteredSearchTerm.replaceAll("[\"]", "").replaceAll("\\s+", " ").trim().split(" ")));
				final List<String> minKeywords = getCatalogTools().getAllValuesForKey(
						BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.MIN_PARTIALMATCH_WORDS_KEY);
            	final int minKeywordsCount = Integer.parseInt(minKeywords.get(0));
				final List<String> maxKeywords = getCatalogTools().getAllValuesForKey(
						BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.MAX_PARTIALMATCH_WORDS_KEY);
            	final int maxKeywordsCount = Integer.parseInt(maxKeywords.get(0));
				if (null != stopWrdRemovedString && stopWrdRemovedString.size() > 0
						&& stopWrdRemovedString.size() > minKeywordsCount
						&& stopWrdRemovedString.size()<maxKeywordsCount && enteredSearchTerm !=null){
					logDebug(
							"Going to trigger a Partial Match Search for stopWordRemovedString : " + enteredSearchTerm);
					pSearchQuery.setStopWrdRemovedString(stopWrdRemovedString);
					// pSearchQuery.setKeyWord(enteredSearchTerm.trim());//commented
					// for defect PS-25219
					pSearchQuery.setSearchMode(BBBCmsConstants.SEARCH_MODE_ALLPARTIAL); // Partial
																						// Search
																						// Flag
																						// set
																						// for
																						// Desktop
																						// Web
																						// Channel
					if(!(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)
							|| BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel)
							|| BBBCoreConstants.FF1.equalsIgnoreCase(channel)
							|| BBBCoreConstants.FF2.equalsIgnoreCase(channel))) {
								logDebug("channel is null or desktop");
								logDebug("Partal Match Search set for Desktop");
								isPartialMatchSearch = true;
					}
				}
			}
			String enteredNarrowDown = "";
			logDebug("Entered NarrowDown is: "+pSearchQuery.getNarrowDown());
			if (!(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)
					|| BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel))) {
				logDebug("channel is null or desktop");
				logDebug("searchMode from url in endecaSearch.performSearch() "+pSearchQuery.getSearchMode());
				// Defect #24526 :searchMode enetered in URL should be excluded
				// from partial search
				if(pSearchQuery.getNarrowDown() !=null){
					// check if for from brand page keyword is populated or
					// null??
					logDebug("making call to removedStopwords in case of SWS");
					enteredNarrowDown=removeStopWords(pSearchQuery.getNarrowDown());
					logDebug("enteredNarrowDown after Stop word removal;:"+enteredNarrowDown);
					pSearchQuery.setNarrowDown(enteredNarrowDown.trim());
				}
			}
			//end of 92F

			logDebug("FromBrandPage:" + pSearchQuery.isFromBrandPage());

			final EndecaQueryVO endecaQueryVO = generateEndecaQuery(pSearchQuery); // Generate
																					// ENDECA
																					// Query.
			 String boostCode = this.getSearchUtil().getBoostCode((ServletUtil.getCurrentRequest()));
			 boolean l2L3BrandBoostingEnabled = false;
			 final String originOfTraffic = pRequest.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC);
			 l2L3BrandBoostingEnabled = getSearchUtil().checkL2L3BrandBoostingFlag(pSearchQuery, l2L3BrandBoostingEnabled, originOfTraffic);
			logDebug("BBBResultsListHandler.preprocess - l2L3BrandBoostingEnabled :"+l2L3BrandBoostingEnabled);
			SearchBoostingAlgorithmVO searchBoostingAlgorithm = null;
			Map<String, String> searchAlgorithmParams = new LinkedHashMap<String, String>();
			searchAlgorithmParams.put(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, boostCode);
			searchAlgorithmParams.put(BBBCoreConstants.PAGE_NAME, String.valueOf(getEndecaSearchTools().getPageType(pSearchQuery, l2L3BrandBoostingEnabled)));
			searchAlgorithmParams.put(BBBCoreConstants.SITE_ID, pSearchQuery.getSiteId()); 
			searchBoostingAlgorithm = getEndecaSearchTools().getSearchBoostingAlgorithmsFromLocalMap(searchAlgorithmParams); 
			if(searchBoostingAlgorithm==null){
				boostCode=BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE;
			}

			boolean isEphQuerySchemeOn=false;

			if( ! pSearchQuery.isFromBrandPage() && StringUtils.isNotBlank(pSearchQuery.getKeyWord()) && getSearchUtil().getEphLookUpUtil().isEpHLookUpEnable()
				&& !BBBCoreConstants.OFF.equalsIgnoreCase(getSearchUtil().getEPHBoostingScheme(pSearchQuery))) {
				isEphQuerySchemeOn=true;
			}

			//	Fix for Defect 2257071  Added null check
			if (null != endecaQueryVO) {
				attributeParamVO.setSearchQuery(pSearchQuery);
				attributeParamVO.setBoostCode(boostCode);
				//not checking cache if call is for only facets(Mobile)

			/*Commented BBBI-5072
			 * Mobile | For departments EPH lookup logic is occuring every time not fetching the results from cache
			 * 	if(BBBUtility.isEmpty(pSearchQuery.getProductsReq())
						|| !BBBCoreConstants.FALSE.equalsIgnoreCase(pSearchQuery.getProductsReq())){ */
	    			// fetching search result from cache
	    			logDebug("cache key:" + endecaQueryVO.getQueryString());
	    			if(pSearchQuery.isFromChecklistCategory()) {
	    				// Set cache for Checklist category results
	    				setChecklistCategoryResultsCache(attributeParamVO);
	    			}else if ((pSearchQuery.getKeyWord() == null || pSearchQuery.getKeyWord().equalsIgnoreCase(""))) {
	    				// Set Cache for SearchResults
	    				setSearchResultsCache(attributeParamVO);
	    			} else if(pSearchQuery.isFromBrandPage()){
	    					// Set Cache for Brands
	    					setBrandsCache(attributeParamVO);
	    				}else{
	    					if(BBBUtility.isEmpty(pSearchQuery.getNarrowDown())&& isPartialMatchSearch){
	    						// Set Cache for KeywordSearchMatchall
	    						setKeywordSearchMatchallCache(attributeParamVO);
	    					}else{
	    						// Set Cache for KeywordSearch
	    						setKeywordSearchCache(attributeParamVO);
	    					}
	    				}
	    			}
		    		//BBBSL-2389 Cache retrieval is by passed for staging server.
		    		logDebug("Is Staging Server:: "+ isStagingServer() + " Is Header Search Request :: " + pSearchQuery.isHeaderSearch());

				    // Setting Cache Parameters
				    attributeParamVO.setPartialMatchSearch(isPartialMatchSearch);

	    		if(!cacheSkip){
	    			// Fetch Result from Cache
	    			bsVO = fetchResultFromCache(attributeParamVO, endecaQueryVO,isEphQuerySchemeOn);
					if(bsVO !=null && pSearchQuery.isHeaderSearch() && bsVO.getFacets() != null) {
					Iterator<FacetParentVO> itr = bsVO.getFacets().iterator();
					boolean duplicateFacet = false;
					while(itr.hasNext()) {
						FacetParentVO facetParentVO = (FacetParentVO) itr.next();
						if (BBBSearchConstants.DEPARTMENT.equalsIgnoreCase(facetParentVO.getName())) {
							if (duplicateFacet) {
								logInfo("Found duplicate facet from Cache");
								bsVO = null;
								break;
	    		}
							duplicateFacet = true;
				}
						}
					}
	    			if(bsVO != null && bsVO.isEphApplied()){
	    				setAppliedEphSchemeParam(pSearchQuery);
		    				pSearchQuery.setEphApplied(true);
		    				pSearchQuery.setEPHFound(true);
	    				if(isLoggingDebug()){
	    					logDebug("EPH Detail for Cached Search Results:"+bsVO.getEphQueryDetail());
	    				}
	    			}
	    			if(bsVO!=null){
	    				pRequest.setAttribute(BBBCoreConstants.ACTUALAPPLIEDALGORITHM,bsVO.getActualAlgorithmApplied());
	    			}
	    			pRequest.setAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO, pSearchQuery);
	    			
	    			pRequest.getSession().setAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, boostCode);
	    		}
				//} commented due to BBBI-5072
				if(bsVO == null){

					if(isEphQuerySchemeOn){
						getSearchUtil().lookupEPHForBoosting(pSearchQuery);
						if(pSearchQuery.isEPHFound())
							{
								if(BBBCoreConstants.FILTER_VALUE.equalsIgnoreCase(pSearchQuery.getEphQueryScheme())){
									String pQueryURL=pSearchQuery.getQueryURL()+BBBCoreConstants.AMPERSAND+BBBEndecaConstants.NAV_NR+BBBCoreConstants.EQUAL+pSearchQuery.getEphFilterString();
									pSearchQuery.appendBoostingLogs("EndecaSearch :SearchKeyord:["+pSearchQuery.getKeyWord()+"] ,Query String Overridden by EndecaSearch with EPH Code .Old Query String ["+pSearchQuery.getQueryURL()+"]  and Overridden Query String :["+pQueryURL+"],EphApplied flag set to true");
									pSearchQuery.setQueryURL(pQueryURL);
									pSearchQuery.setEphApplied(true);
									pRequest.getSession().setAttribute("InStoreEphQuery",pQueryURL);
									}
						    setAppliedEphSchemeParam(pSearchQuery);
							}
							}
					// Fetch Result from Endeca if Cache is empty
					bsVO = doSearchandCustomize(attributeParamVO, endecaQueryVO, isEphQuerySchemeOn);

					logInfo("EndecaSearch ::::MLSS::::"+attributeParamVO.getSearchQuery().getBoostingLogs());

				} else {
    				logDebug("Search result fetched from cache");
    				if(bsVO.getPromoMap()!=null && isLoggingDebug())
    				{
    					if (bsVO.getPromoMap().entrySet() != null) {
    						Iterator<Entry<String, List<PromoVO>>> itr = bsVO.getPromoMap()
    								.entrySet().iterator();
    						while (itr.hasNext()) {
    							Map.Entry<String, List<PromoVO>> entry = (Map.Entry<String, List<PromoVO>>) itr
    									.next();
    							if (entry.getValue() !=null) {
                                    for (PromoVO pVO : entry.getValue()) {
                                           if(pVO.getImageSrc()!=null){
                                                  logDebug("In performSearch method of EndecaSearch For site: "+ SiteContextManager.getCurrentSiteId() +":: In PromoVO, Image HREF: - "+pVO.getImageHref()+ " :: Image Alt text: - "+pVO.getImageAlt()+ " :: Image source: - "+pVO.getImageSrc());
                                           }
                                    }
    							} else {
                                    logDebug("In performSearch method of EndecaSearch For site: "+ SiteContextManager.getCurrentSiteId() +":: In PromoVO, PromoMap value null for key - " + entry.getKey());
    							}
    						}
    					}
    				}
    				logDebug("Fetched Search Result from Cache :: Key :: " + attributeParamVO.getCacheName() + " For Site :: " + pSearchQuery.getSiteId() + " :: IsRedirect :: " + pSearchQuery.isRedirected());
    				logDebug("Query String :: " + pSearchQuery.getQueryURL() + " :: Endeca Query :: " + endecaQueryVO.getQueryString());
				}

						if (pSearchQuery != null && pSearchQuery.isOnlineTab()
								&& !BBBUtility.isEmpty(pSearchQuery.getStoreIdFromAjax())
								&& (!BBBUtility.isEmpty(pSearchQuery.getPageNum())
										&& (pSearchQuery.getPageNum()).equalsIgnoreCase("1"))) {
							String queryStringforStoreId =null;
							//adding session query
							if(bsVO.isEphApplied()&& !BBBUtility.isEmpty(attributeParamVO.getSearchResults().getInStoreEphQuery())){								
								queryStringforStoreId =attributeParamVO.getSearchResults().getInStoreEphQuery() ;
								 pRequest.getSession().setAttribute("InStoreEphQuery",null);								
							}else{

								 queryStringforStoreId = endecaQueryVO.getQueryString();
							}
							logDebug("storeCount query BEFORE adding storeId:"+queryStringforStoreId);
							String storeId = pSearchQuery.getStoreIdFromAjax();
							// seeting quesryString
							String ntk = "";
							String ntt = "";
							String ntx = "";

					try {
						ntk = EndecaSearchUtil.getParameter(queryStringforStoreId,
								BBBEndecaConstants.NAV_PROPERTY_NAME);
						ntt = EndecaSearchUtil.getParameter(queryStringforStoreId, BBBEndecaConstants.NAV_KEYWORD);
						ntx = EndecaSearchUtil.getParameter(queryStringforStoreId,
								BBBEndecaConstants.NAV_SEARCH_MODE);

					} catch (UrlFormatException urlFormatExe) {
						logDebug("urlFormatExe while trying to get N value from nav state - "
								+ urlFormatExe.getMessage());
					}
					if (StringUtils.isEmpty(ntk)) {
						ntk = "P_Stores";
					} else {
						ntk = ntk + BBBCoreConstants.PIPE_SYMBOL + BBBEndecaConstants.P_STORES;
					}
					if (StringUtils.isEmpty(ntt)) {
						ntt = storeId;
					} else {
						ntt = ntt + BBBCoreConstants.PIPE_SYMBOL + storeId;
					}

					if (StringUtils.isEmpty(ntx)) {
						ntx = BBBEndecaConstants.SEARCH_MODE_MATCHALL;
					} else {
						ntx = ntx + BBBCoreConstants.PIPE_SYMBOL + BBBEndecaConstants.SEARCH_MODE_MATCHALL;
					}
					final UrlGen urlGen = new UrlGen(queryStringforStoreId, getQueryGenerator().getEncoding());
					urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, ntk);
					urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, ntt);
					urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, ntx);


					String queryStringWithStoreId = urlGen.toString();
					if(bsVO.getBbbProducts() != null)
					logDebug("storeCount query AFTER adding storeId"+queryStringWithStoreId);
					if(bsVO.getBbbProducts()!=null)
					bsVO.getBbbProducts().setStoreInvCount(0);
					// End setting queryString
					if (!cacheSkip) {
						logDebug("InStore count cached queryString::"+queryStringWithStoreId);

						setInventoryCountCache(attributeParamVO);

						if (getObjectCache().get(queryStringWithStoreId, attributeParamVO.getCacheName()) != null) {
							long totalCount = (long) getObjectCache().get(queryStringWithStoreId,
									attributeParamVO.getCacheName());
							if(bsVO.getBbbProducts() != null){
							if (totalCount > 0)
								bsVO.getBbbProducts().setStoreInvCount(totalCount);
							else{
								bsVO.getBbbProducts().setStoreInvCount(0);
							}
							}
						}
					}

					if (bsVO != null && bsVO.getBbbProducts() != null
							&& bsVO.getBbbProducts().getStoreInvCount() == 0) {
						ENEQuery localStoreQueryObject = null;
						localStoreQueryObject = new UrlENEQuery(queryStringWithStoreId,
								getQueryGenerator().getEncoding());
						logDebug("InStore count endeca queryString::"+queryStringWithStoreId);
						localStoreQueryObject.setNavAllRefinements(false);

						localStoreQueryObject.setNavNumERecs(0);

						if (!BBBUtility.isEmpty(bsVO.getBbbProducts().getNegativeMatchQuery())) {
							String negativeMatchExpression = bsVO.getBbbProducts().getNegativeMatchQuery();

							localStoreQueryObject.setNavRecordStructureExpr(negativeMatchExpression);
							}
						ENEQueryResults localStorePLPQueryResults = getEndecaClient()
								.executeQuery(localStoreQueryObject);
						if ((localStorePLPQueryResults.containsNavigation())
								&& (localStorePLPQueryResults.getNavigation() != null)) {
							long totalCount = localStorePLPQueryResults.getNavigation().getTotalNumERecs();

							if (totalCount > 0L) {
								bsVO.getBbbProducts().setStoreInvCount(totalCount);
								getObjectCache().put(queryStringWithStoreId, totalCount,
										attributeParamVO.getCacheName(), attributeParamVO.getCacheTimeout());
			}
		}
					}
				}
			
			}
		catch(ENEQueryException endecaException){
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQueryException in calling Endeca performSearch");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQuery   Exception ",
					endecaException);
		} catch (ContentException endecaException) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"AssemblerException in calling Endeca performSearch");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001, "Assembler   Exception ",
					endecaException);
		} catch (AssemblerException endecaException) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"AssemblerException in calling Endeca performSearch");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001, "Assembler   Exception ",
					endecaException);
		} catch (Exception genericException) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.GENERIC_EXCEPTION, "GenericException in performSearch");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException(BBBCoreErrorConstants.GENERIC_EXCEPTION, "Generic   Exception ",
					genericException);
		}

		finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
		logDebug("Search Results: " +bsVO);
		logDebug("Exit EndecaSearch.performSearch method.");
		return bsVO;
	}

	private void setInventoryCountCache(final AttributesParamVO attributeParamVO) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, "setInventoryCountCache");
		final boolean inventoryCacheFlag = Boolean.parseBoolean(BBBConfigRepoUtils
				.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.INVENTORY_COUNT_CACHE_FLAG));
		// final boolean inventoryCacheFlag = true;
		if (inventoryCacheFlag) {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.INVENTORY_COUNT_CACHE_NAME));
			attributeParamVO.setCacheTimeout(getInventoryCacheTimeout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, "setInventoryCountCache");
		}
	}

	/**
	 * get cache timeout from config key
	 *
	 * @param
	 * @return
	 */
	private int getInventoryCacheTimeout(String pCacheName) {
		int cacheTimeout;
		try {
			if (pCacheName.equalsIgnoreCase(BBBCoreConstants.INVENTORY_COUNT_CACHE_NAME)) {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.INVENTORY_COUNT_CACHE_TIMEOUT));
			} else {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT));
			}
		} catch (NumberFormatException e) {
			logError("EndecaSearch.performSearch || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e);
			cacheTimeout = getKeywordCacheTimeout();
		} catch (NullPointerException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e);
			cacheTimeout = getKeywordCacheTimeout();
		}
		return cacheTimeout;
	}

	/**
	 * This method sets Cache for Keyword Match for Boosted or Normal Search.
	 *
	 * @param boostCode
	 * @param attributeParamVO
	 */
	private void setKeywordSearchCache(final AttributesParamVO attributeParamVO) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,
				BBBCoreConstants.SET_KEYWORD_SEARCH_CACHE);
		final boolean keywordBoostFlag = Boolean.parseBoolean(BBBConfigRepoUtils
				.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.KEYWORD_BOOST_FLAG));
		if (keywordBoostFlag && null != attributeParamVO.getBoostCode()
				&& !attributeParamVO.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_NAME));
			attributeParamVO.setBoostFlag(keywordBoostFlag);
			attributeParamVO.setCacheTimeout(getKeywordSearchTimeout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					BBBCoreConstants.SET_KEYWORD_SEARCH_CACHE);
		} else {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME));
			attributeParamVO.setCacheTimeout(getKeywordSearchTimeout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					BBBCoreConstants.SET_KEYWORD_SEARCH_CACHE);
		}
	}

	/**
	 * This method sets Cache for Partial Keyword Match for Normal Search.
	 *
	 * @param boostCode
	 * @param attributeParamVO
	 */
	private void setKeywordSearchMatchallCache(final AttributesParamVO attributeParamVO) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.SETKEYWORD_SEARCHMATCH_ALL_CACHE);
		final boolean keywordBoostFlag = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.KEYWORD_BOOST_FLAG));
		if(keywordBoostFlag && null != attributeParamVO.getBoostCode() && ! attributeParamVO.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_NAME));
			attributeParamVO.setBoostFlag(keywordBoostFlag);
			attributeParamVO.setCacheTimeout(getKeywordSearchTimeout(attributeParamVO.getCacheName()));
		} else {
		attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.KEYWORD_SEARCH_MATCHALL_CACHE_NAME));
		attributeParamVO.setCacheTimeout(getKeywordSearchTimeout(attributeParamVO.getCacheName()));
		}
		logDebug("cacheName:" + attributeParamVO.getCacheName());
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
				BBBCoreConstants.SETKEYWORD_SEARCHMATCH_ALL_CACHE);
	}

	/**
	 * This method sets Cache when Brand is selected for Boosted or Normal
	 * Search.
	 *
	 * @param boostCode
	 * @param gpVO
	 * @param ppVO
	 */
	private void setBrandsCache(final AttributesParamVO attributeParamVO) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.SET_BRANDS_CACHE);
		final boolean brandsBoostFlag = Boolean.parseBoolean(BBBConfigRepoUtils
				.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.BRANDS_BOOST_FLAG));
		if (brandsBoostFlag && null != attributeParamVO.getBoostCode()
				&& !attributeParamVO.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_NAME));
			attributeParamVO.setBoostFlag(brandsBoostFlag);
			attributeParamVO.setCacheTimeout(getSearchResultCacheTimout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.SET_BRANDS_CACHE);
		} else {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.SEARCH_RESULT_CACHE_NAME));
			attributeParamVO.setCacheTimeout(getSearchResultCacheTimout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.SET_BRANDS_CACHE);
		}

	}

	/**
	 * This method sets Cache .
	 *
	 * @param boostCode
	 * @param gpVO
	 * @param ppVO
	 */
	private void setSearchResultsCache(final AttributesParamVO attributeParamVO) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,
				BBBCoreConstants.SET_SEARCH_RESULTS_CACHE);
		final boolean l2l3BoostFlag = Boolean.parseBoolean(BBBConfigRepoUtils
				.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.L2L3_BOOST_FLAG));
		if(attributeParamVO.getSearchQuery().isHeaderSearch()) {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.SEARCH_RESULT_CACHE_NAME));
			attributeParamVO.setCacheTimeout(getSearchResultCacheTimout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
		} else if (l2l3BoostFlag && null != attributeParamVO.getBoostCode()
				&& !attributeParamVO.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_NAME));
				attributeParamVO.setBoostFlag(l2l3BoostFlag);
				attributeParamVO.setCacheTimeout(getSearchResultCacheTimout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					BBBCoreConstants.SET_SEARCH_RESULTS_CACHE);
		} else {
			attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.SEARCH_RESULT_CACHE_NAME));
			attributeParamVO.setCacheTimeout(getSearchResultCacheTimout(attributeParamVO.getCacheName()));
			logDebug("cacheName:" + attributeParamVO.getCacheName());
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					BBBCoreConstants.SET_SEARCH_RESULTS_CACHE);
		}
	}

	/**
	 * This methods fetches search result from cache
	 *
	 * @param endecaQueryVO
	 *
	 * @param pSearchQuery
	 * @param cacheName
	 * @param bsVO
	 * @param endecaQueryVO
	 * @param boostCode
	 * @param boostFlag
	 * @return
	 */
	private SearchResults fetchResultFromCache(final AttributesParamVO attributeParamVO,
			final EndecaQueryVO endecaQueryVO, boolean isEphQuerySchemeOn) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,
				BBBCoreConstants.FETCH_RESULT_FROMCACHE);
		if(isStagingServer() && attributeParamVO.getSearchQuery().isHeaderSearch()){
			logDebug("Request is for Staging Server thus skippping cache");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					BBBCoreConstants.FETCH_RESULT_FROMCACHE);
		}else{
			logDebug("Request is for Production Server :::: cache key: " + endecaQueryVO.getQueryString());

			String ephSuffixForCacheKey=BBBCoreConstants.BLANK;
			String queryString=null;
			if (isEphQuerySchemeOn) {
				ephSuffixForCacheKey = BBBEndecaConstants.EPH_QUERYSCHEME_TYPE
						+ attributeParamVO.getSearchQuery().getEphQueryScheme();
			}

			/*RM Defect 15480 Redirection Loop Fix*/
			if(attributeParamVO.getSearchQuery().isRedirected()){
				if (attributeParamVO.isBoostFlag() && null != attributeParamVO.getBoostCode() && !attributeParamVO
						.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
					queryString = endecaQueryVO.getQueryString() + BBBEndecaConstants.ISREDIRECTED
							+ BBBEndecaConstants.BOOSTCODE + attributeParamVO.getBoostCode() + ephSuffixForCacheKey;

				} else {
					queryString = endecaQueryVO.getQueryString() + BBBEndecaConstants.ISREDIRECTED
							+ ephSuffixForCacheKey;
				}
			}else{
				logDebug("query string"+endecaQueryVO.getQueryString());
				if (attributeParamVO.isBoostFlag() && null != attributeParamVO.getBoostCode() && !attributeParamVO
						.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
					queryString = endecaQueryVO.getQueryString() + BBBEndecaConstants.BOOSTCODE
							+ attributeParamVO.getBoostCode() + ephSuffixForCacheKey;

					} else {
						queryString=endecaQueryVO.getQueryString()+ephSuffixForCacheKey;
					}
			}
			logDebug("Fetching Results from Cache :- " + attributeParamVO.getCacheName()
					+ ",queryString to fetch result from Cache:[" + queryString + "]");
			attributeParamVO.setSearchResults(
					(SearchResults) getObjectCache().get(queryString, attributeParamVO.getCacheName()));
			/*RM Defect 15480 Redirection Loop Fix */
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					BBBCoreConstants.FETCH_RESULT_FROMCACHE);
		}
		return attributeParamVO.getSearchResults();
	}

	/**
	 *
	 * @param endecaQueryVO
	 * @param pSearchQuery
	 * @param cacheName
	 * @param cacheTimeout
	 * @param bsVO
	 * @param isPartialMatchSearch
	 * @param endecaQueryVO
	 * @param boostCode
	 * @param boostFlag
	 * @return
	 * @throws ENEQueryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws AssemblerException
	 * @throws ContentException
	 */
	private SearchResults doSearchandCustomize(final AttributesParamVO attributeParamVO, final EndecaQueryVO endecaQueryVO, boolean isEphQuerySchemeOn)
			throws ENEQueryException, BBBSystemException, BBBBusinessException, AssemblerException, ContentException {
		    BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.DOSEARCH_AND_CUSTOMIZE);
		    
		    DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();

		    logInfo("Query String :: " + attributeParamVO.getSearchQuery().getQueryURL() + " :: Endeca Query :: " + endecaQueryVO.getQueryString());

		    attributeParamVO.setSearchResults(performEndecaSearch(attributeParamVO.getSearchQuery(), endecaQueryVO, attributeParamVO.isPartialMatchSearch()));
		    attributeParamVO.getSearchResults().setActualAlgorithmApplied((String)ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.ACTUALAPPLIEDALGORITHM));
			
		    doCustomizeResponse(attributeParamVO.getSearchResults(), attributeParamVO.getSearchQuery());


			    String ephSuffixForCacheKey=BBBCoreConstants.BLANK;
				String queryString=null;

				if(isEphQuerySchemeOn)
				{
					ephSuffixForCacheKey=BBBEndecaConstants.EPH_QUERYSCHEME_TYPE+attributeParamVO.getSearchQuery().getEphQueryScheme();
					// Set flag to true to populated hidden field(CurrentEPHScheme) for cache results  and add EPH detail in Result Vo
					if(attributeParamVO.getSearchQuery().isEPHFound() && attributeParamVO.getSearchQuery().isEphApplied()){
						attributeParamVO.getSearchResults().setEphApplied(true);
						if(BBBCoreConstants.FILTER_VALUE.equalsIgnoreCase(attributeParamVO.getSearchQuery().getEphQueryScheme())){
							attributeParamVO.getSearchResults().setEphQueryDetail("EphScheme:["+attributeParamVO.getSearchQuery().getEphQueryScheme()+"],EndecaQuery: ["+attributeParamVO.getSearchQuery().getQueryURL()+"]");
						 }else{
							 attributeParamVO.getSearchResults().setEphQueryDetail("EphScheme:["+attributeParamVO.getSearchQuery().getEphQueryScheme()+"],EphList["+attributeParamVO.getSearchQuery().getEphResultVO().getEphList()+"] : CategoryList:["+attributeParamVO.getSearchQuery().getEphResultVO().getCategoryList()+"]");
						 }
						if(pRequest.getSession().getAttribute("InStoreEphQuery")!=null)
						attributeParamVO.getSearchResults().setInStoreEphQuery((String)pRequest.getSession().getAttribute("InStoreEphQuery"));
				       }
				}
				
				/*RM Defect 15480  Redirection Loop Fix */
				if(attributeParamVO.getSearchQuery().isRedirected()){
					if (attributeParamVO.isBoostFlag() && null != attributeParamVO.getBoostCode() && !attributeParamVO
							.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
						queryString = endecaQueryVO.getQueryString() + BBBEndecaConstants.ISREDIRECTED
								+ BBBEndecaConstants.BOOSTCODE + attributeParamVO.getBoostCode() + ephSuffixForCacheKey;
					} else {
						queryString = endecaQueryVO.getQueryString() + BBBEndecaConstants.ISREDIRECTED
								+ ephSuffixForCacheKey;
					}
					if( null !=attributeParamVO.getSearchResults() &&
							((null !=attributeParamVO.getSearchResults().getBbbProducts() &&
							attributeParamVO.getSearchResults().getBbbProducts().getBBBProductCount() > 0L)|| !(BBBUtility.isListEmpty(attributeParamVO.getSearchResults().getFacets())))){
				               getObjectCache().put(queryString, attributeParamVO.getSearchResults(), attributeParamVO.getCacheName(),
						       attributeParamVO.getCacheTimeout());

				logInfo("Putting Search Result into Cache :: Key [Redirected] :- CacheName : ["
						+ attributeParamVO.getCacheName() + "],CacheKey : [" + queryString + "] ,Query String  :- ["
						+ attributeParamVO.getSearchQuery().getQueryURL() + "]");
				}

				}else{
				if (attributeParamVO.isBoostFlag() && null != attributeParamVO.getBoostCode() && !attributeParamVO
						.getBoostCode().equals(BBBEndecaConstants.BOOST_ALGORITHM_DEFAULT_BOOST_CODE)) {
					queryString = endecaQueryVO.getQueryString() + BBBEndecaConstants.BOOSTCODE
							+ attributeParamVO.getBoostCode() + ephSuffixForCacheKey;
					} else {
						 	queryString=endecaQueryVO.getQueryString()+ephSuffixForCacheKey;
					}
				if( null !=attributeParamVO.getSearchResults() &&
					((null !=attributeParamVO.getSearchResults().getBbbProducts() &&
					attributeParamVO.getSearchResults().getBbbProducts().getBBBProductCount() > 0L)|| !(BBBUtility.isListEmpty(attributeParamVO.getSearchResults().getFacets())))){
				        getObjectCache().put(queryString, attributeParamVO.getSearchResults(), attributeParamVO.getCacheName(),
						attributeParamVO.getCacheTimeout());

				logInfo("Putting Search Result into Cache :: Key- CacheName : [" + attributeParamVO.getCacheName()
						+ "],CacheKey: [" + queryString + "] ,Query String  :- ["
						+ attributeParamVO.getSearchQuery().getQueryURL() + "]");
				}

			}


			/*RM Defect 15480  Redirection Loop Fix */
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.DOSEARCH_AND_CUSTOMIZE);

		return attributeParamVO.getSearchResults();
	}

	/**
	 * get cache timeout from config key
	 *
	 * @param
	 * @return
	 */
	private int getKeywordSearchTimeout(String pCacheName) {
		int cacheTimeout;
		try {
			if(pCacheName.equalsIgnoreCase(BBBCoreConstants.BOOSTED_PRODUCTS_CACHE)) {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_TIMEOUT));
			} else {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT));
			}
		} catch (NumberFormatException e) {
			logError("EndecaSearch.performSearch || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e);
			cacheTimeout = getKeywordCacheTimeout();
		} catch (NullPointerException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e);
			cacheTimeout = getKeywordCacheTimeout();
		}
		return cacheTimeout;
	}

	/**
	 * get cache timeout from config key
	 *
	 * @param pCacheName
	 * @return
	 */
	private int getSearchResultCacheTimout(String pCacheName) {
		int cacheTimeout;
		try {
			if(pCacheName.equalsIgnoreCase(BBBCoreConstants.BOOSTED_PRODUCTS_CACHE)) {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.BOOSTED_PRODUCTS_CACHE_TIMEOUT));
			} else {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.SEARCH_RESULT_CACHE_TIMEOUT));
			}
		} catch (NumberFormatException e) {
			logError("EndecaSearch.performSearch || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.SEARCH_RESULT_CACHE_TIMEOUT, e);
			cacheTimeout = getSearchCacheTimeout();
		} catch (NullPointerException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.SEARCH_RESULT_CACHE_TIMEOUT, e);
			cacheTimeout = getSearchCacheTimeout();
		}
		return cacheTimeout;
	}

	/**
	 * This method customizes the Endeca Response by calling the customizers
	 *
	 * @param bsVO
	 */
	protected void doCustomizeResponse(SearchResults bsVO, SearchQuery pSearchQuery){
		logDebug("[START] EndecaSearch.doCustomizeResponse() for Search Keyword :: " + pSearchQuery.getKeyWord());

		EndecaResponseCustomizer customizers[] = getCustomizers();
        if(customizers != null){
            int len = customizers.length;
            for(int i = 0; i < len; i++){
            	EndecaResponseCustomizer customizer = customizers[i];
                logDebug((new StringBuilder()).append("Processing customizer:").append(customizer).toString());
                customizer.customizeResponse(bsVO, pSearchQuery);
                }
            }
        if(pSearchQuery.isFromChecklistCategory() && pSearchQuery .getCheckListSeoUrlHierarchyVo() != null){
			bsVO.setCheckListSeoUrlHierarchy(pSearchQuery .getCheckListSeoUrlHierarchyVo());
		}
        
        logDebug("[END] EndecaSearch.doCustomizeResponse()");
       }

	/**
	 * This method populates the groupId set by SiteSpect for modifying search
	 *
	 * @param pSearchQuery
	 * @param pRequest
	 */
	private void populateSearchGroup(final SearchQuery pSearchQuery, DynamoHttpServletRequest pRequest) {
		String searchConfigGrp=pRequest.getHeader(BBBCoreConstants.SITESPEC_SEARCHGROUP);
		if(null!=searchConfigGrp || BBBUtility.isNotEmpty(searchConfigGrp)){
			pRequest.getSession().setAttribute(BBBEndecaConstants.GROUP_ID,searchConfigGrp);
		}
		logDebug("searchConfigGrp: " +searchConfigGrp);
		pSearchQuery.setGroupId((String) pRequest.getSession().getAttribute(BBBEndecaConstants.GROUP_ID));
	}

	private SearchResults performEndecaSearch(SearchQuery pSearchQuery, EndecaQueryVO endecaQueryVO,
			boolean isPartialMatchSearch) throws ENEQueryException, BBBSystemException, BBBBusinessException,
					AssemblerException, ContentException {
		/*
		 * ENEQueryResults results = null; ENEContentQuery contenQuery = null;
		 * ENEContentResults contentResults = null;
		 */
		SearchResults bsVO = null;
 		final String methodName = BBBCoreConstants.PERFORM_ENDECA_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		try{

			logDebug("search results not available in cache hence fetching from Endeca");
			logDebug("isPartialMatchSearch : "+isPartialMatchSearch);

			ContentItem responseContentItem;
			String contentCollection = getContentPathResolver().resolveContentCollectionPath(pSearchQuery);

			responseContentItem = executeAssemblerQuery(pSearchQuery, pSearchQuery.getQueryURL(),contentCollection);

			//response is valid
			if(responseContentItem != null) {

				//validate error
				if(responseContentItem.get(BBBEndecaConstants.CI_ERROR_MESSAGE_LOOKUP_KEY) != null) {
					String errorMessage = (String) responseContentItem
							.get(BBBEndecaConstants.CI_ERROR_MESSAGE_LOOKUP_KEY);

					if(errorMessage.contains(BBBEndecaConstants.CI_ERROR_EXCEPTION_LOOKUP_KEY)) {
						//error indicates node path resolution failed
						//call assembler again using default content path
						contentCollection = getContentPathResolver().getDefaultContentCollectionPath(pSearchQuery);
						responseContentItem = executeAssemblerQuery(pSearchQuery, pSearchQuery.getQueryURL(),
								contentCollection);
						if(responseContentItem.get(BBBEndecaConstants.CI_ERROR_MESSAGE_LOOKUP_KEY) != null) {
							// In case of any errors still from response content
							// item
							// avoid processing response and throw system
							// exception
							String secondErrorMessage = (String) responseContentItem
									.get(BBBEndecaConstants.CI_ERROR_MESSAGE_LOOKUP_KEY);
							BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
									"Error found in calling Assembler API even with default path");
							BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
							throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,
									"Error found in calling Assembler API even with default path" + secondErrorMessage);
						}
					} else {
						// In case of any Endeca Exception, catch the same and
						// throw BBBSystemException to be handled in Droplet.
						BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
								"Error found in calling Assembler API - error message is not known");
						BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
						throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,
								"Error found in calling Assembler API " + errorMessage);
					}
				}

				// Call process Response method to retrieve Search Results
				// object.
				// no need to call processResponse as all handling is done in
				// parser
				//bsVO = processResponse(pSearchQuery,responseContentItem);

				//fetch search results from contentitem
				if(responseContentItem.get(BBBEndecaConstants.CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO) != null) {
					bsVO = (SearchResults) responseContentItem
							.get(BBBEndecaConstants.CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO);
				}

				//this is set in processResponse method
				//logDebug("bsVO.setPromoMap");
				//bsVO.setPromoMap(getPromo(responseContentItem));

				// For click on media , guide and other tabs to return partial
				// results in matchpartial Searchmode
				if(BBBEndecaConstants.TRUE.equalsIgnoreCase(pSearchQuery.getPartialFlag())){
					bsVO.setPartialFlag(BBBEndecaConstants.TRUE);
				}
				if(pSearchQuery.getSearchMode()!=null){
				bsVO.setUrlSearchMode(pSearchQuery.getSearchMode());
				logDebug("search mode in the result "+bsVO.getUrlSearchMode());
				}
				logDebug("end of bsVO.setPromoMap");
				List<String> subStringArray = new ArrayList<String>();
				if(bsVO!=null){
					final List<String> minSearchResults = getCatalogTools().getAllValuesForKey(
							BBBCmsConstants.CONTENT_CATALOG_KEYS,
							BBBCmsConstants.MIN_MATCHALLPARTIAL_SEARCH_RESULTS_KEY);
	            	final int minSearchResultsCount = Integer.parseInt(minSearchResults.get(0));
					if (BBBUtility.isEmpty(pSearchQuery.getNarrowDown()) && isPartialMatchSearch
							&& bsVO.getBbbProducts() != null
							&& bsVO.getBbbProducts().getBBBProductCount() < minSearchResultsCount) {
						logDebug("bsVO.getDescriptors()" + bsVO.getDescriptors() + "pSearchQuery.getKeyWord()"
								+ pSearchQuery.getKeyWord());
						if(null != bsVO.getDescriptors() && null != pSearchQuery.getKeyWord()){
							if ((!(pSearchQuery.isHeaderSearch())) && bsVO.getDescriptors().size() == 1
									&& (!(pSearchQuery.isFromBrandPage()))
									&& (null==pSearchQuery.getSortCriteria().getSortFieldName())
									&& (getCatalogId("Record Type", "Product")
											.equalsIgnoreCase(pSearchQuery.getCatalogRef().get("catalogId")))
									&& (BBBEndecaConstants._1.equalsIgnoreCase(pSearchQuery.getPageNum()))){
								//logDebug("calling congigStringSplit");
								String autoCorrectWord=bsVO.getAutoSuggest().get(0).getSpellCorrection();
								List<String> searchList= new ArrayList<String>();
								if(null!=autoCorrectWord){
									searchList = new ArrayList<String>(Arrays.asList(autoCorrectWord.toLowerCase()
											.replaceAll("[\"]", "").replaceAll("\\s+", " ").trim().split(" ")));
								}else{
									searchList=pSearchQuery.getStopWrdRemovedString();
								}
								if(null!=searchList && searchList.size()>2){
									subStringArray=configStringSplit(searchList);
								}

								logDebug("subString Array : "+subStringArray);
								// subString Array contains the list of words
								// that should be used for partial search
								if(subStringArray != null && subStringArray.size() > 0){
									logDebug("Partial Search for : " + subStringArray.size()
											+ " no of strings. The strings are :" + subStringArray.toString());
									bsVO = makeRepeatCallsForPartial(subStringArray, bsVO, pSearchQuery);
								}
							}
						}

					}
				}
				//92F subString logic End
			}

			//only objects required for header search are facets
			//fetch using enequery if facets is empty
//			if(pSearchQuery.isHeaderSearch() &&
//					(null == bsVO.getFacets() || bsVO.getFacets().isEmpty()) ) {
			// ENEQueryResults results =
			// getEndecaClient().executeQuery(endecaQueryVO.getQueryObject());
//				if(results.containsNavigation()) {
			// final String refinedQueryString =
			// getQueryGenerator().refineQuery(pSearchQuery.getQueryURL());
			// List<FacetParentVO> facetList =
			// this.getFacets(results.getNavigation(), refinedQueryString,
			// pSearchQuery,null,null);
			// bsVO =
			// getEndecaClient().getResultObject(null,null,null,facetList,null,null,null,null);
//				}
//			}

		} finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}

		return bsVO;
	}

	//Commenting for now as we finish the implementation in sprint #3
	private SearchResults makeRepeatCallsForPartial(List<String> subStringArray, SearchResults bsVO,
			SearchQuery pSearchQuery)
					throws ENEQueryException, ContentException, BBBSystemException, BBBBusinessException {
		Iterator<String> itr = subStringArray.iterator();
		Map<String, SearchResults> partialResultsMap = new HashMap<String, SearchResults>();
		final String methodName = BBBCoreConstants.PARTIAL_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		String keyword;
		try{
			while(itr.hasNext()){
				String truncatedQuery = itr.next();
				pSearchQuery.setKeyWord(truncatedQuery.trim());
				pSearchQuery.setSearchMode(BBBCmsConstants.SEARCH_MODE_MATCHPARTIAL);
				final EndecaQueryVO endecaQueryVO = generatePartialEndecaQuery(pSearchQuery);
				logDebug("Triggering partial Endeca Search for Term : "+truncatedQuery);
				SearchResults subBSVO = performPartialMatchEndecaSearch(endecaQueryVO,pSearchQuery);
				if(subBSVO.getBbbProducts().getBBBProductCount()>0){
					logDebug("the partial results are not null");
					keyword=truncatedQuery.trim();
					keyword = ((((keyword.replaceAll("[^0-9A-Za-z\"\']", " ")).replaceAll("[\']", ""))
							.replaceAll("\\s+", " ")).replaceAll(" ", "-"));
					subBSVO.setPartialKeywordURL(keyword);
					partialResultsMap.put(pSearchQuery.getKeyWord(), subBSVO);
				}
			}
		bsVO.setPartialSearchResults(partialResultsMap);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
		return bsVO;
	}

	private SearchResults performPartialMatchEndecaSearch(EndecaQueryVO endecaQueryVO, SearchQuery pSearchQuery){
		SearchResults bsVO=new SearchResults();
		ENEQuery queryObject=endecaQueryVO.getQueryObject();
		ENEQueryResults queryResults=null;
		Navigation nav=null;
		BBBProductList bbbProductList=new BBBProductList();
		String color=null;
		//ENEContentResults pCoResults=null;
		List<CurrentDescriptorVO> descriptorList = null;
		String colorGroupFacetName = BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE,
				EndecaSearch.COLORGROUP_KEY);
		final String methodName = BBBCoreConstants.PARTIAL_ENDECA_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		try {
			queryResults=getEndecaClient().executeQuery(queryObject);
			nav=queryResults.getNavigation();
			final String refinedString = getQueryGenerator().refineQuery(pSearchQuery.getQueryURL());
			descriptorList = this.getDescriptors(nav, refinedString, pSearchQuery);

			if(null != descriptorList){
				int count = 0;
				for(CurrentDescriptorVO pVo : descriptorList){
					if((null != pVo.getRootName()) && ((colorGroupFacetName).equalsIgnoreCase(pVo.getRootName()))){
						count = count + 1 ;
						color = pVo.getName();
						// System.out.println("Count = " + count + " & color = "
						// + color);
					}
				}
				if (count == 1) {
					//System.out.println("count is 1");
					bbbProductList = this.getProductList(nav,color,null);
				} else {
					//System.out.println("count is not 1");
					bbbProductList = this.getProductList(nav,null,null);
				}
			}

			//bbbProductList=this.getProductList(nav,color,pCoResults );
			if(bbbProductList!=null)
				bsVO.setBbbProducts(bbbProductList);
			else{
				return null;
			}
		} catch (ENEQueryException e) {

			logError("Exception thrown in performPartialMatchEndecaSearch | ENEQueryException:", e);

		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
		return bsVO;
	}

	/**
	 * This is to generate the Endeca Query from URL parameters.
	 *
	 * @param pSearchQuery
	 * @return String
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private EndecaQueryVO generatePartialEndecaQuery(final SearchQuery pSearchQuery)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.generateEndecaQuery method.");

		// Call Query Generator's class generateEndecaQuery() method
		final EndecaQueryVO pEneQuery = getQueryGenerator().generatePartialEndecaQuery(pSearchQuery);
//		Fix for Defect 2257071
		if(pEneQuery != null){
		    logDebug("Endeca Query String : " + pEneQuery.toString());
		    logDebug("Exit EndecaSearch.generateEndecaQuery method.");
		}
		return pEneQuery;
	}

	/**
	 * This is to generate the Endeca Query from URL parameters.
	 *
	 * @param pSearchQuery
	 * @return String
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public EndecaQueryVO generateEndecaQuery(final SearchQuery pSearchQuery)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.generateEndecaQuery method.");

		// Call Query Generator's class generateEndecaQuery() method
		final EndecaQueryVO pEneQuery = getQueryGenerator().generateEndecaQuery(pSearchQuery);
//		Fix for Defect 2257071
		if(pEneQuery != null){
		    logDebug("Endeca Query String : " + pEneQuery.toString());
		    logDebug("Exit EndecaSearch.generateEndecaQuery method.");
		}
		return pEneQuery;
	}

	
	

	/**
	 * This is to obtain the Result List from the Object.
	 *
	 * @param argNav
	 * @return BBBProductList
	 */
 	@SuppressWarnings("rawtypes")
	public BBBProductList getProductList(final Navigation argNav, final String pColor,BBBProductList bbbProducts){

		logDebug("Entering EndecaSearch.getProductList method.");

		if(argNav == null) {
			return new BBBProductList();
		}

		ERecList records = null;
		ERec record = null;
		ListIterator iterRecords = null;
		PropertyMap properties = null;
		String productID = null;
		bbbProducts = new BBBProductList();
		BBBProduct bbbOthResult = null;
		BBBProduct bbbProduct = null;
		long productCount = 0L;
		final Map<String, List<BBBProduct>> otherResults = new HashMap<String, List<BBBProduct>>();
		String scene7Path = getConfigUtil().getScene7Path();
		String higherShippingThreshhold = ((BBBConfigToolsImpl) getCatalogTools()).getLblTxtTemplateManager()
				.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, null, null,
						SiteContextManager.getCurrentSiteId());
		List<String> ltlAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,
				BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST);
		List<String> shippingAttributesList = BBBConfigRepoUtils.getAllValues(BBBCmsConstants.CONTENT_CATALOG_KEYS,
				BBBCatalogConstants.SHIPPING_ATTRIBUTES_LIST);
		// Obtain total result count
		productCount = argNav.getTotalNumERecs();
		bbbProducts.setBBBProductCount(productCount);

		records = argNav.getERecs();

		/*
		 * // pCoResults is gauranteed to be not null if(null != pCoResults){
		 * final ContentItem contentItem = pCoResults.getContent(); if( null !=
		 * contentItem && null !=
		 * contentItem.getProperty(getCatridgeNameMap().get("CENTER")) && null
		 * !=
		 * contentItem.getProperty(getCatridgeNameMap().get("CENTER")).getValue(
		 * )){ final ContentItemList centerColumnItems =
		 * (ContentItemListImpl)contentItem.getProperty(getCatridgeNameMap().get
		 * ("CENTER")).getValue(); ContentItem cartridge = null; final Iterator
		 * iter = centerColumnItems.iterator(); boolean flag = true; boolean
		 * boostRecordFlag = false; while(iter.hasNext()){ if(boostRecordFlag){
		 * break; } flag = false; cartridge = (ContentItem) iter.next(); if(null
		 * == cartridge || null == cartridge.getTemplateId()){ logDebug(
		 * "Results List from Page Builder configuration not retrieved. -3 ");
		 * records = argNav.getERecs(); } else{ if
		 * (cartridge.getTemplateId().equalsIgnoreCase(getCatridgePropertyMap().
		 * get("RESULT_LIST")) ||
		 * cartridge.getTemplateId().equalsIgnoreCase(getCatridgePropertyMap().
		 * get("RESULT_LIST_ORIGINAL"))) { final NavigationRecords navRecs =
		 * (NavigationRecords)
		 * cartridge.getProperty(getCatridgePropertyMap().get("NAV_RECORDS")).
		 * getValue(); if(null == navRecs){ logDebug(
		 * "Results List from Page Builder configuration not retrieved. -1 ");
		 * records = argNav.getERecs(); } else{ logDebug(
		 * "Results List from Page Builder configuration retrieved."); records =
		 * navRecs.getERecs(); boostRecordFlag = true; } } else{ logDebug(
		 * "Results List from Page Builder configuration not retrieved. -2 ");
		 * records = argNav.getERecs(); } } } if(flag){ logDebug(
		 * "Results List from Page Builder configuration not retrieved. -4 ");
		 * records = argNav.getERecs(); } } else{ logDebug(
		 * "Results List from Page Builder configuration not retrieved. -5 ");
		 * records = argNav.getERecs(); } }else{ logDebug(
		 * "Results List from Page Builder configuration not retrieved. -5 ");
		 * records = argNav.getERecs(); }
		 */
		// Put an iterator on the Obtained Result List.

			//records = argNav.getERecs();
			iterRecords = records.listIterator();
			SortedMap<Integer,String> attr = null;
			Map<String,SkuVO> pSkuSet = null;
			List<SkuVO> skuVOList= null;
			List<String> categoryList = null;
		/*
		 * boolean isColor=false; boolean isColorSize=false;
		 */
			boolean freeShippingBadge = true;
			Map<String, String> propertyMap = getConfigUtil().getPropertyMap();

			while (iterRecords.hasNext()) {
				record = (ERec) iterRecords.next();
				properties = record.getProperties();
				productID = (String) properties.get(propertyMap.get("PRODUCT_ID"));
				bbbProduct = new BBBProduct();
				categoryList = new ArrayList<String>();
			// Map the Endeca properties to corresponding properties of Results
			// VO.
				bbbProduct.setProductID(productID);
				bbbProduct.setProductName((String) properties.get(propertyMap.get("PRODUCT_TITLE")));
			// bbbProduct.setCategory((String)
			// properties.get(propertyMap.get("PRODUCT_CATEGORY_ID")));

			/*
			 * if("Y".equalsIgnoreCase((String)properties.get(propertyMap.get(
			 * "PRODUCT_SWATCH_FLAG")))){ bbbProduct.setSwatchFlag("1");} else{
			 * bbbProduct.setSwatchFlag("0");}
			 */
				bbbProduct.setSwatchFlag((String)properties.get(propertyMap.get("PRODUCT_SWATCH_FLAG")));
				//System.out.println("Swatch flag: " + bbbProduct.getSwatchFlag());

					bbbProduct.setHighPriceMX((String) properties.get(propertyMap.get("PRODUCT_HIGH_PRICE_MX")));
					bbbProduct.setLowPriceMX((String) properties.get(propertyMap.get("PRODUCT_LOW_PRICE_MX")));
					bbbProduct.setWasLowPriceMX((String) properties.get(propertyMap.get("PRODUCT_WAS_LOW_PRICE_MX")));
					bbbProduct.setWasHighPriceMX((String) properties.get(propertyMap.get("PRODUCT_WAS_HIGH_PRICE_MX")));

					bbbProduct.setPriceRangeMX((String) properties.get(propertyMap.get("PRODUCT_PRICE_RANGE_MX")));
					bbbProduct.setWasPriceRangeMX((String) properties.get(propertyMap.get("PRODUCT_WAS_PRICE_RANGE_MX")));

					bbbProduct.setHighPrice((String) properties.get(propertyMap.get("PRODUCT_HIGH_PRICE")));
					bbbProduct.setLowPrice((String) properties.get(propertyMap.get("PRODUCT_LOW_PRICE")));
					bbbProduct.setWasLowPrice((String) properties.get(propertyMap.get("PRODUCT_WAS_LOW_PRICE")));
					bbbProduct.setWasHighPrice((String) properties.get(propertyMap.get("PRODUCT_WAS_HIGH_PRICE")));
					bbbProduct.setPriceRange((String) properties.get(propertyMap.get("PRODUCT_PRICE_RANGE")));
					bbbProduct.setWasPriceRange((String) properties.get(propertyMap.get("PRODUCT_WAS_PRICE_RANGE")));
					bbbProduct.setPriceRangeToken((String) properties.get(propertyMap.get("PRODUCT_PRICE_RANGE_TOKEN")));
					if(properties.get(propertyMap.get("International_Restricted"))!=null){
				bbbProduct.setIntlRestricted(BBBCoreConstants.YES_CHAR
						.equalsIgnoreCase((String) properties.get(propertyMap.get("International_Restricted"))));
					}

			// System.out.println("get Product List () with Product Id: " +
			// productID + " & pColor = " + pColor +" & Swatch Flag = " +
			// bbbProduct.getSwatchFlag() );
				bbbProduct.setImageURL((String) properties.get(propertyMap.get("PRODUCT_IMAGE")));
				getSearchUtil().setProductImageURLForDifferentView(bbbProduct);
				// Set the vertical Image URL for the product
				bbbProduct.setVerticalImageURL((String) properties.get(propertyMap.get("PRODUCT_VERTICAL_IMAGE")));
				//System.out.println("Image URL:" + bbbProduct.getImageURL());

				bbbProduct.setReviews((String)properties.get(propertyMap.get("PRODUCT_REVIEWS")));
				bbbProduct.setRatings((String)properties.get(propertyMap.get("PRODUCT_RATINGS")));
				if (NumberUtils.isNumber(bbbProduct.getRatings())) {
					Integer rating = (int) (Double.valueOf(bbbProduct.getRatings()) * BBBCoreConstants.TEN);
					bbbProduct.setRatingForCSS(rating);
				}
				bbbProduct.setDescription((String) properties.get(propertyMap.get("PRODUCT_DESC")));
				bbbProduct.setVideoId((String) properties.get(propertyMap.get("VIDEO_ID")));
				bbbProduct.setGuideId((String) properties.get(propertyMap.get("GUIDE_ID")));
				bbbProduct.setGuideTitle((String) properties.get(propertyMap.get("GUIDE_TITLE")));
				bbbProduct.setGuideImage((String) properties.get(propertyMap.get("GUIDE_IMAGE")));
				bbbProduct.setGuideAltText((String) properties.get(propertyMap.get("GUIDE_ALT_TEXT")));
				bbbProduct.setGuideShortDesc((String) properties.get(propertyMap.get("GUIDE_SHORT_DESC")));
				bbbProduct.setSeoUrl((String) properties.get(propertyMap.get("PRODUCT_SEO_URL")));

				int scene7Value;
				try {
					scene7Value = Integer.parseInt(bbbProduct.getProductID()) % 10;
					if (null != scene7Path) {
						if (scene7Value % 2 == 0) {
						bbbProduct.setSceneSevenURL(
								scene7Path.replaceFirst(BBBCoreConstants.STRING_X, BBBCoreConstants.STRING_ONE));
						} else {
						bbbProduct.setSceneSevenURL(
								scene7Path.replaceFirst(BBBCoreConstants.STRING_X, BBBCoreConstants.STRING_TWO));
						}
					}

				} catch (NumberFormatException nfe) {
					logError("Exception thrown in scene7Value"+nfe.getMessage());
				}
			// Setting up the new Property to display Was-IS price on
			// PLP,Search,Brand pages

				if(BBBUtility.isNotEmpty((String) properties.get(propertyMap.get("PRODUCT_ON_SALE")))){
					bbbProduct.setOnSale((String) properties.get(propertyMap.get("PRODUCT_ON_SALE")));
				}

				logDebug("SEO Url for "+bbbProduct.getProductID()+" is "+bbbProduct.getSeoUrl());

				bbbProduct.setCollectionFlag(((String)properties.get(propertyMap.get("COLLECTION_PRODUCT"))));
				// added for R2-item 141
				if(("04").equalsIgnoreCase(((String)properties.get(propertyMap.get("PRODUCT_ROLLUP_TYPE_CODE"))))
					|| ("05")
							.equalsIgnoreCase(((String) properties.get(propertyMap.get("PRODUCT_ROLLUP_TYPE_CODE"))))) {
	            	bbbProduct.setRollupFlag(true);
	            }

				String otherCat = (String) properties.get(propertyMap.get("OTHER_RESULT_CATEGORY"));
				String otherTitle = (String) properties.get(propertyMap.get("OTHER_RESULT_TITLE"));
				String otherLink = (String) properties.get(propertyMap.get("OTHER_RESULT_LINK"));
				String otherPageType = (String) properties.get(propertyMap.get("OTHER_RESULT_PAGE_TYPE"));

				if(BBBUtility.isNotEmpty(otherCat)){
					if(otherResults.containsKey(otherCat)){
						bbbOthResult = new BBBProduct();
						bbbOthResult.setProductID(productID);
						bbbOthResult.setOthResLink(otherLink);
						bbbOthResult.setOthResTitle(otherTitle);
						bbbOthResult.setOtherPageType(otherPageType);
						otherResults.get(otherCat).add(bbbOthResult);
				} else if (!otherResults.containsKey(otherCat)) {
						bbbOthResult = new BBBProduct();
						List<BBBProduct> othResList = new ArrayList<BBBProduct>();
						bbbOthResult.setProductID(productID);
						bbbOthResult.setOthResLink(otherLink);
						bbbOthResult.setOthResTitle(otherTitle);
						bbbOthResult.setOtherPageType(otherPageType);
						othResList.add(bbbOthResult);
						otherResults.put(otherCat, othResList);
					}
				}
				// Iterate through Multi valued property for single Result Item.
				final Iterator props = properties.entrySet().iterator();
				attr =new TreeMap<Integer,String>();
				pSkuSet = new HashMap<String,SkuVO>();
				SkuVO pSkuVO = null;
				skuVOList = new ArrayList<SkuVO>();
				// BBBSL-2187 : To Display Swatch in alphabetical order
				Map<String,SkuVO> colorSkuSet = new TreeMap<String, SkuVO>();
				Map<String, String> attributePropMap = getConfigUtil().getAttributePropmap();

				List<AttributeVO> attributeListVO = new ArrayList<AttributeVO>();
				while (props.hasNext()) {
		        	final Property prop = (Property)props.next();

		            // Product Attributes Property.
		        	if(propertyMap.get("PRODUCT_ATTR").equalsIgnoreCase(prop.getKey().toString())){
		        		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(prop.getValue().toString());
		            	final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		        		DynaClass dynaClass = jSONResultbean.getDynaClass();
		        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
		        		List<String> propertyNames = new ArrayList<String>();
		        		for (int i = 0; i < dynaProp.length; i++) {
		        			String name = dynaProp[i].getName();
		        			propertyNames.add(name);
		        		}
					if (propertyNames.contains(attributePropMap.get("PRIORITY"))
							&& propertyNames.contains(getConfigUtil().getAttributePropmap().get("PLACEHOLDER"))
							&& propertyNames.contains(getConfigUtil().getAttributePropmap().get("DISPDESC"))
							&& propertyNames.contains(attributePropMap.get("SKU_ATTRIBUTE_ID"))) {
						String placeholder = (String) jSONResultbean
								.get(getConfigUtil().getAttributePropmap().get("PLACEHOLDER"));
						if (!("".equals(jSONResultbean.get(getConfigUtil().getAttributePropmap().get("PRIORITY"))))
								&& placeholder.indexOf(getConfigUtil().getPlaceHolder()) >= 0) {
		        				AttributeVO attrVo =  new AttributeVO();
							String dispDesc = (String) jSONResultbean
									.get(getConfigUtil().getAttributePropmap().get("DISPDESC"));
		        				String skuAttribute = (String)jSONResultbean.get(attributePropMap.get("SKU_ATTRIBUTE_ID"));
		        				 if ((shippingAttributesList != null) && !shippingAttributesList.isEmpty()) {
								final String shippingAttributes[] = shippingAttributesList.get(0)
										.split(BBBCoreConstants.COMMA);
		                             for (final String shippingAttributeKey : shippingAttributes) {
		                                 if (skuAttribute.contains(shippingAttributeKey)) {
		                                	 freeShippingBadge = false;
		                                     break;
		                                 }
		                             }
		                         }
		        				 if(ltlAttributesList.contains(skuAttribute)){
			        					freeShippingBadge = false;
		        				 }
		        				//String intlProdAttr = "true";
							int priority = Integer.parseInt(
									(String) jSONResultbean.get(getConfigUtil().getAttributePropmap().get("PRIORITY")));
							attr.put(priority,
									(String) jSONResultbean.get(getConfigUtil().getAttributePropmap().get("DISPDESC")));
		        				attrVo.setPriority(priority);
							// this code will be uncommented once we will start
							// getting ISINTL flag from endeca
							if (propertyNames
									.contains(getConfigUtil().getAttributePropmap().get("International_Restricted"))
									&& jSONResultbean.get(getConfigUtil().getAttributePropmap()
											.get("International_Restricted")) != null) {
								String intlProdAttr = (String) jSONResultbean
										.get(getConfigUtil().getAttributePropmap().get("International_Restricted"));
		        					if(intlProdAttr.equalsIgnoreCase("Y")) {
			        					intlProdAttr="true";
			        					attrVo.setIntlProdAttr(intlProdAttr);

			        				} else {
			        					intlProdAttr="false";
			        					attrVo.setIntlProdAttr(intlProdAttr);
			        				}
								logDebug("Endeca Search intlProdAttr |||||||||||||||| value is :" + dispDesc + "-----"
										+ intlProdAttr);
		        				}
		        				attrVo.setAttributeDescrip(dispDesc);
		        				attrVo.setSkuAttributeId(skuAttribute);
		        				attributeListVO.add(attrVo);
		        			}
		        		}
		        	}

		            // Product Swatch Image Info property.
		            if(propertyMap.get("PRODUCT_CHILD_PRODUCT").equalsIgnoreCase(prop.getKey().toString())){
		            	JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(prop.getValue().toString());
		        		final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		        		DynaClass dynaClass = jSONResultbean.getDynaClass();
		        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
		        		List<String> propertyNames = new ArrayList<String>();
		        		for (int i = 0; i < dynaProp.length; i++) {
		        			String name = dynaProp[i].getName();
		        			propertyNames.add(name);
		        		}
		        		if(propertyNames.contains(getSwatchInfoMap().get("SKU_ID"))){
		        			pSkuVO = new SkuVO();
		        			pSkuVO.setSkuID((String) jSONResultbean.get(getSwatchInfoMap().get("SKU_ID")));
		        			if(propertyNames.contains(getSwatchInfoMap().get("SWATCH_IMAGE"))){
							pSkuVO.setSkuSwatchImageURL(
									(String) jSONResultbean.get(getSwatchInfoMap().get("SWATCH_IMAGE")));
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("PRODUCT_IMAGE"))){
							pSkuVO.setSkuMedImageURL(
									(String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_IMAGE")));
								getSearchUtil().setSkuImageURLForDifferentView(pSkuVO);
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("COLOR"))){
		        				pSkuVO.setColor((String) jSONResultbean.get(getSwatchInfoMap().get("COLOR")));
							/*
							 * // added for R2-item 141 isColor=true;
							 */
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE"))){
							pSkuVO.setSkuVerticalImageURL(
									(String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE")));
		        			}

						/*
						 * // added for R2-item 141 if(isColor){
						 * if(propertyNames.contains(getSwatchInfoMap().get(
						 * "SKU_SIZE"))){ isColorSize=true; } }
						 */
		        			skuVOList.add(pSkuVO);
		        			pSkuSet.put(pSkuVO.getSkuID(), pSkuVO);
		        		}
		        		if(null != pColor){
						if (propertyNames.contains(getSwatchInfoMap().get("GROUP"))
								&& propertyNames.contains(getSwatchInfoMap().get("PRODUCT_IMAGE"))) {
				            	if(pColor.equalsIgnoreCase((String) jSONResultbean.get(getSwatchInfoMap().get("GROUP")))){
								// Setting image for a single color group
								// selection.
								bbbProduct.setImageURL(
										(String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_IMAGE")));
				        		}
		        			}
						if (propertyNames.contains(getSwatchInfoMap().get("GROUP"))
								&& propertyNames.contains(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE"))) {
				            	if(pColor.equalsIgnoreCase((String) jSONResultbean.get(getSwatchInfoMap().get("GROUP")))){
								// Setting vertical image for a single color
								// group selection.
								bbbProduct.setVerticalImageURL(
										(String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE")));
				        		}
		        			}
		        		}
	        		}
		            bbbProduct.setSkuSet(pSkuSet);

				/*
				 * if(isColorSize){ bbbProduct.setRollupFlag(true); }
				 */

		            for(SkuVO skuVO : skuVOList){
		            	if(null != skuVO.getColor() && !colorSkuSet.containsKey(skuVO.getColor())){
		            			colorSkuSet.put(skuVO.getColor(), skuVO);
		            	}
		            }
		            bbbProduct.setColorSet(colorSkuSet);
		            /* GS-192 Setting List with Categories tagged to a Product. */
		            if(propertyMap.get("PRODUCT_CATEGORY_ID").equalsIgnoreCase(prop.getKey().toString())) {
		            	categoryList.add(prop.getValue().toString());
		            }
		        }
				// BBBH-220 - ship message display changes
				if(freeShippingBadge){
					updateShippingMessageFlag(bbbProduct, higherShippingThreshhold);
	        	}
				//attr1.put("attributes", attributeList1);
				bbbProduct.setAttributeVO(attributeListVO);
			// System.out.println("Final iamge URl set to: "+
			// bbbProduct.getImageURL());
		        // Adding the Populated Product Item into Product List VO.
				bbbProduct.setCategoryList(categoryList);
		        bbbProducts.getBBBProducts().add(bbbProduct);
		    }
			bbbProducts.setOtherResults(otherResults);

			logDebug("Exit EndecaSearch.getProductList method.");

			return bbbProducts;
	}

	/**
	 * BBBH-220 - ship message display changes This method is used to Update
	 * shipping message flag in bbbProduct and shipping message to be displayed
	 * on PLP and search results pages.
	 *
	 * @param bbbProduct
	 *            the bbb product
	 * @param higherShippingThreshhold
	 */
	protected void updateShippingMessageFlag(BBBProduct bbbProduct, String higherShippingThreshhold) {
    	double higherShipThreshhold = 0.0;
    	if(!StringUtils.isBlank(higherShippingThreshhold)){
			String trimedHigherShippingThreshold = higherShippingThreshhold
					.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
    		if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){
    			higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
    		}
    	} else{
    		return;
    	}
    	String lowPrice = bbbProduct.getLowPrice();
    	String highPrice = bbbProduct.getHighPrice();
    	Map<String, String> placeholderMap = new HashMap<String, String>();
    	placeholderMap.put(BBBCoreConstants.CURRENCY, BBBCoreConstants.DOLLAR);
    	placeholderMap.put(BBBCoreConstants.HIGHER_SHIP_THRESHHOLD, higherShippingThreshhold);

		if (!StringUtils.isBlank(lowPrice) && Double.valueOf(lowPrice) < higherShipThreshhold
				&& !StringUtils.isBlank(highPrice) && Double.valueOf(highPrice) > higherShipThreshhold) {
    		bbbProduct.setShipMsgFlag(true);
			bbbProduct.setDisplayShipMsg(((BBBConfigToolsImpl) this.getCatalogTools()).getLblTxtTemplateManager()
					.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_COLLECTIONS_PRODUCT, placeholderMap));
		} else if (!StringUtils.isBlank(lowPrice) && Double.valueOf(lowPrice) > higherShipThreshhold) {
    		bbbProduct.setShipMsgFlag(true);
			bbbProduct.setDisplayShipMsg(((BBBConfigToolsImpl) this.getCatalogTools()).getLblTxtTemplateManager()
					.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap));
    	}
	}

	
	/**
	 * This method is to set Parent Category VO for the results with single
	 * category products.
	 *
	 * @param SearchQuery
	 * @param CategoryParentVO
	 * @param List<CategoryRefinementVO>
	 * @param Dimension
	 * @return void
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public void setCategoryParentForSingleCategoryResult(final SearchQuery pSearchQuery,
			final CategoryParentVO categoryParent, final List<CategoryRefinementVO> catRefineList,
			final List<CurrentDescriptorVO> listDesc, final Dimension dim) {
		CategoryRefinementVO categoryRefines;
		categoryRefines = new CategoryRefinementVO();
		String name = null;
		long id = 0 ;
		long idUpdated = 0;
		if(pSearchQuery.getRefType().equals(BBBEndecaConstants.TRUE)) {
			if (dim.getCompletePath().size() > 0) {
				name = ((DimVal)dim.getCompletePath().get(dim.getCompletePath().size()-1)).getName();
				id = ((DimVal)dim.getCompletePath().get(dim.getCompletePath().size()-1)).getId();
			}
				DimLocationList dimLocationList = dim.getImplicitLocations();
			if ((dimLocationList != null) && pSearchQuery.getCatalogRef() != null) {
				Iterator<DimLocation> dimLocationListIterator = dimLocationList.iterator();
				while(dimLocationListIterator.hasNext()) {
					DimLocation dimLocation = dimLocationListIterator.next();
					DimValList dimValList = dimLocation.getAncestors();
					DimVal dimVal = dimLocation.getDimValue();
					if (dimValList != null && dimValList.size() > 0) {
						idUpdated = (Long)((DimVal)dimValList.get(dimValList.size()-1)).getId();
					}
					if (pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_REF_ID)
							.equals(String.valueOf(idUpdated)) && dimVal != null) {
						name = dimVal.getName();
						id = dimVal.getId();
					}
				}
			}

		} else {
			if (listDesc != null && !listDesc.isEmpty()) {
					name = listDesc.get(0).getName();
					id = Long.valueOf(listDesc.get(0).getCategoryId());
				}
			}
		categoryRefines.setName(name);
		categoryRefines.setQuery(String.valueOf(id));
		if (dim.getCompletePath() != null && dim.getCompletePath().size() > 0) {
			categoryRefines
					.setTotalSize((String) (((DimVal) dim.getCompletePath().get(dim.getCompletePath().size() - 1))
							.getProperties().get("DGraph.Bins")));
		}
		catRefineList.add(categoryRefines);
		categoryParent.setCategoryRefinement(catRefineList);
		if (dim.getCompletePath() != null && dim.getCompletePath().size() > 0) {
			categoryParent.setName(((DimVal)dim.getCompletePath().get(dim.getCompletePath().size()-1)).getName());
			categoryParent.setQuery(
					String.valueOf(((DimVal) dim.getCompletePath().get(dim.getCompletePath().size() - 1)).getId()));
		}
		categoryParent.setCategoryRefinement(catRefineList);
		if (getNodeType()
				.equalsIgnoreCase((String) (((DimVal) dim.getCompletePath().get(dim.getCompletePath().size() - 1))
						.getProperties().get(getDimPropertyMap().get("NODE_TYPE"))))) {
			categoryParent.setIsPortraitEligible(BBBEndecaConstants.TRUE);
		}
	}

	@SuppressWarnings("unchecked")
	public CategoryRefinementVO getSiblings(final long dimValId, final SearchQuery pSearchQuery,
			final CategoryParentVO categoryParent, final List<CategoryRefinementVO> catRefineList, final String name,
			final int loopSize) throws BBBSystemException {
		ENEQuery usq;
		CategoryRefinementVO categoryRefine = null;
		final CategoryRefinementVO catRefine = new CategoryRefinementVO();
		try {
			final DimValIdList dvalIds = new DimValIdList();
	        dvalIds.addDimValueId(0);
			usq = new UrlENEQuery("&" + BBBEndecaConstants.NAVIGATION + "=" + dimValId,
					getEndecaClient().getEncoding());
			usq.setNavAllRefinements( true );
			final ENEQueryResults eneResults= getEndecaClient().getEneConnection().query(usq);
			final DimensionList dims = eneResults.getNavigation().getRefinementDimensions();
			final ListIterator<Dimension> iter =  dims.listIterator();
			String catName = null;
			long catId = 0;
			while(iter.hasNext()){
				final Dimension dim = iter.next();
				if ((dim.getName()
						.equalsIgnoreCase(getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())))) {
					final DimValList dimvalList = dim.getRefinements();
					final ListIterator<DimVal> iterDim =  dimvalList.listIterator();
					while(iterDim.hasNext()){
						//get the details for selected category Id
						final DimVal dimVal = iterDim.next();
						if (pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_REF_ID)
								.equalsIgnoreCase(String.valueOf(dimVal.getId()))) {
							catName = dimVal.getName();
							catId = dimVal.getId();
						}
						catRefine.setName(catName);
						catRefine.setQuery(String.valueOf(catId));
						PropertyMap dimPropertyMap = dimVal.getProperties();
						if (getNodeType()
								.equalsIgnoreCase((String) dimPropertyMap.get(getDimPropertyMap().get("NODE_TYPE")))) {
							catRefine.setIsPortraitEligible(BBBEndecaConstants.TRUE);
						}
						catRefine.setTotalSize((String)dimPropertyMap.get(getDimPropertyMap().get("DGRAPH.BINS")));
						// System.out.println("Inside sibling 1: Property map
						// for +"+dimVal.getName()+"-"+dimPropertyMap +" value:
						// "+dimPropertyMap.get(getDimPropertyMap().get("NODE_TYPE"))
						// + " Bins:" + dimPropertyMap.get("DGraph.Bins"));
					}
				}
			}
			final ListIterator<Dimension> iter1 =  dims.listIterator();
			while(iter1.hasNext()){
				final Dimension dim = iter1.next();
				if ((dim.getName()
						.equalsIgnoreCase(getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())))) {
					final DimValList dimvalList = dim.getRefinements();
					final ListIterator<DimVal> iterDim =  dimvalList.listIterator();
					while(iterDim.hasNext()){
						DimVal dimVal = iterDim.next();
						PropertyMap dimPropertyMap = dimVal.getProperties();
						categoryRefine = new CategoryRefinementVO();
						categoryRefine.setName(dimVal.getName());
						//categoryRefines.setQuery(getEndecaClient().createRefinementHyperlink(String.valueOf(dimVal.getId()),null));
						categoryRefine.setQuery(String.valueOf(dimVal.getId()));
						if (getNodeType()
								.equalsIgnoreCase((String) dimPropertyMap.get(getDimPropertyMap().get("NODE_TYPE")))) {
							categoryRefine.setIsPortraitEligible(BBBEndecaConstants.TRUE);
						}
						categoryRefine
								.setTotalSize((String) dimPropertyMap.get(getDimPropertyMap().get("DGRAPH.BINS")));
						catRefineList.add(categoryRefine);
						if(iterDim.nextIndex() == loopSize){
							boolean present =false;
							// Iterate through the list of VO's to check if the
							// selected category is present in the list.
							for(CategoryRefinementVO cVo : catRefineList){
								if(String.valueOf(catId).equalsIgnoreCase(cVo.getQuery())){
									present = true;
								}
							}
							// If not present, replace last VO in list with the
							// seelcted category VO.
							if(!present){
								catRefineList.set(catRefineList.size()-1, catRefine);
							}
							break;
		                }
					}
					categoryParent.setCategoryRefinement(catRefineList);
					categoryParent.setName(name);
					categoryParent.setQuery(String.valueOf(dimValId));
				}
            }
		}catch (UrlENEQueryParseException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.INTEGARTION_ENDECA_ERROR_1003,"Endeca Exception",e);
		} catch (ENEQueryException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"Endeca Exception",e);
		}
		return categoryRefine;
	}

	/**
	 * This method is to retrieve current applied filters.
	 *
	 * @param navigation
	 * @param queryString
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CurrentDescriptorVO> getDescriptors(final Navigation navigation, final String queryString,
			final SearchQuery pSearchQuery) {

		logDebug("Entering EndecaSearch.getDescriptors method.");

		DimVal dimValDescriptor=null;
		Dimension dimDescriptor=null;
		final List<CurrentDescriptorVO> descriptorList = new ArrayList<CurrentDescriptorVO>();
		String nParam=null;
		final DimensionList discList = navigation.getDescriptorDimensions();
		final ListIterator<Dimension> iterDiscriptors =  discList.listIterator();
		CurrentDescriptorVO currentDescriptor;
		final Collection<String> facets = getConfigUtil().getFacets(pSearchQuery.getSiteId());
		String brandFacetName = BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE,
				EndecaSearch.BRAND_KEY);
		final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();
		// Added for R2.2 SEO friendly Story : Start
				String queryParamForFacets = null;

				if(!BBBUtility.isEmpty(queryString)){
					queryParamForFacets = queryString.substring(queryString.indexOf("?") + 1);
				}
		// Added for R2.2 SEO friendly Story : End
		// Iterate through the current applied filters.
		while(iterDiscriptors.hasNext()){
			dimDescriptor = iterDiscriptors.next();
			if(facets != null
				&& (facets.contains(dimensionMap.get(dimDescriptor.getName()))
						|| ("RECORD TYPE").equalsIgnoreCase(dimensionMap.get(dimDescriptor.getName())))
				&& (!(BBBEndecaConstants.TRUE.equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))
						&& (brandFacetName).equalsIgnoreCase(dimensionMap.get(dimDescriptor.getName()))))){

				/*
				 * RM Defect 15480 - Remove brand facet when frmBrandPage is
				 * true
				 */
				dimValDescriptor=dimDescriptor.getDescriptor();
				currentDescriptor = new CurrentDescriptorVO();
				nParam = ENEQueryToolkit.removeDescriptor(navigation, dimValDescriptor).toString();
				currentDescriptor.setRootName(dimensionMap.get(dimDescriptor.getName()));
				currentDescriptor.setName(dimValDescriptor.getName());
				currentDescriptor.setCategoryId(String.valueOf(dimValDescriptor.getId()));
				currentDescriptor.setRemovalQuery(
						getEndecaClient().createRemoveDescriptorQuery(nParam, queryParamForFacets, pSearchQuery));
				// Added for R2.2 SEO friendly Story : Start
				currentDescriptor.setDescriptorFilter(nParam.replaceAll(" ", "-"));
				currentDescriptor.setMultiSelect(dimDescriptor.getRoot().isMultiSelectOr());

				// Added for R2.2 SEO friendly Story : End
				descriptorList.add(currentDescriptor);
			}
		}

		logDebug("Exit EndecaSearch.getDescriptors method.");

		return descriptorList;
	}

	/**
	 * This method returns list of facets available for the Navigation Search.
	 *
	 * @param navigation
	 * @param queryString
	 * @param descriptorList
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws ENEQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List<FacetParentVO> getFacets(final Navigation navigation, final String queryString,
			final SearchQuery pSearchQuery,List<FacetParentVO> facetList, List<CurrentDescriptorVO> descriptorList)
					throws BBBBusinessException, BBBSystemException, ENEQueryException{

		logDebug("Entering EndecaSearch.getFacets method.");

		long startTime1 = System.currentTimeMillis();
		logDebug(" getFacets start at  :"	+ startTime1);

		String queryParamForFacets = null;

		if(!BBBUtility.isEmpty(queryString)){
			queryParamForFacets = queryString.substring(queryString.indexOf("?") + 1);
		}
		FacetParentVO facetParent = null;
		List<FacetRefinementVO> facetRefineList;
		FacetRefinementVO facetRefinements = null;
		//FacetRefinementVO facetRefinementL2 = null;
		DimVal childDimVal;
		DimensionList parentDim = null;
		//String ancestercategory=null;

		final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();

		//boolean fectchCategoryTreeStrucutre=false;
	   	//String endecaSiteRootDimension=null;

		
		String brandFacetName = BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE,
				EndecaSearch.BRAND_KEY);
				/*
				 * PSI 6 || BPS-798 || Changes Start || Implemente department as
				 * tree
				 */
				// Below if condition is to check for the keyword search without
				// any facets applied
//		if(descriptorList!=null && descriptorList.size()==1) {
//			CurrentDescriptorVO cdVO=descriptorList.get(0);
				// logDebug("PROD_RECORD_TYPE : "+ cdVO.getName()+" Record Type
				// "+ cdVO.getRootName());
				// fectchCategoryTreeStrucutre=
				// cdVO.getName().equalsIgnoreCase((PROD_RECORD_TYPE)) &&
				// cdVO.getRootName().equalsIgnoreCase((RECORD_TYPE));
//		}
				// logDebug("fectchCategoryTreeStrucutre : "
				// +fectchCategoryTreeStrucutre + "keyword :"
				// +pSearchQuery.getKeyWord());
				/*
				 * PSI 6 || BPS-798 || Changes end || Implemente department as
				 * tree Structure
				 */

		//variable to store facet list that gets returned back
		List<FacetParentVO> facetParentList = new ArrayList<FacetParentVO>();

		//return only when facets exist and list is not empty
		if(null != facetList && (facetList.size() > 0) ) {

			return facetList;

		} else {

			if(null == navigation) {
				return null;
			}

			final String siteId = pSearchQuery.getSiteId();
			final List<String> catIDstobeSupressed = getCatIdsToBeSuppressed(siteId);
			final Collection<String> facets = getConfigUtil().getFacets(siteId);

			List<String> pList = sanitizedAttributeValues(siteId);

			String attributes = getCatalogTools().getConfigValueByconfigType(getConfigUtil().getDimDisplayMapConfig())
					.get("Attributes");

			Map<String,String> attributeMap=getConfigUtil().getAttributeInfo();

			//ContentItemListImpl parentCDim = null;
			//final ContentItem contentItem = pCoResults.getContent();
			//String customDimension = null;
			// Added for R2.2 SEO friendly Story : Start

			// Added for R2.2 SEO friendly Story : End

			parentDim = navigation.getRefinementDimensions();

			//final List<String> pList = sanitizedAttributeValues(siteId);
			final ListIterator parentDimIter = parentDim.listIterator();

			// Put iterator for List of Dimensions
			try {
				while(parentDimIter.hasNext()){
					final Dimension rootDimension = (Dimension)parentDimIter.next();
					// START -- R2.1 -- Attribute Facet Story Scope Item #213(b)
					

					logDebug("Root Dimenion Name :" + rootDimension.getName() + " || Keyword :"
							+ pSearchQuery.getKeyWord());
					// System.out.println("Custom Dimension: "+customDimension
					// +" *& isCustomDimension : "+isCustomDimension +" & root
					// domension name: "+ rootDimension.getName());
					// END  -- R2.1 -- Attribute Facet Story Scope Item #213(b)
					if (facets != null && facets.contains(dimensionMap.get(rootDimension.getName()))
							&& (!(BBBEndecaConstants.TRUE
									.equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))
									&& (brandFacetName).equalsIgnoreCase(dimensionMap.get(rootDimension.getName()))))){
						// RM Defect 15480 - Remove brand facet when
						// frmBrandPage is true
								facetParent = new FacetParentVO();
								facetRefineList = new ArrayList<FacetRefinementVO>();
								facetParent.setName(dimensionMap.get(rootDimension.getName()));

						// This condition added for mobile multiselect
						// requirement
						if (rootDimension.getRoot() != null) {
									facetParent.setMultiSelect(rootDimension.getRoot().isMultiSelectOr());
							    }

						facetParent.setQuery(getEndecaClient().createExposureHyperlink(rootDimension.getId(),
								queryParamForFacets, pSearchQuery));

								final DimValList childDimList = rootDimension.getRefinements();
								final ListIterator childDimIter =  childDimList.listIterator();
						// final ListIterator childDimIter1 =
						// childDimList.listIterator();

								if((attributes).equalsIgnoreCase(dimensionMap.get(rootDimension.getName()))) {
									pList = sanitizedAttributeValues(siteId);
								}

								logDebug("Started Iterating Child Dimenstions. Start Time : ");
								long startTime = System.currentTimeMillis();
								boolean showRatingFacet = true;
								while(childDimIter.hasNext()){
									childDimVal = (DimVal) childDimIter.next();

							if (!(catIDstobeSupressed.contains((String.valueOf(childDimVal.getId()))))) {// &&
																											// showChildDimVal){

								// START -- R2.1 -- Attribute Facet Story #213
								// (a)
								// If Current Dimension is Attribute and there
								// is a list of Attribute values configured at
								// site level to be shown as facets.
										//for rating filters first filter w
										if(("RATINGS").equalsIgnoreCase(dimensionMap.get(rootDimension.getName()))){
											String childDimValue = childDimVal.getName().trim();
											if(!(this.ratingFilterMap.containsValue(childDimValue))){
												showRatingFacet=false;
												break;
											}
										}
										if((attributes).equalsIgnoreCase(dimensionMap.get(rootDimension.getName()))
												&& null != pList && !pList.isEmpty()){
											if(pList.contains(childDimVal.getName().toLowerCase())){
												//for(String pAttrVal : pList){
													//if(childDimVal.getName().equalsIgnoreCase(pAttrVal)){
														facetRefinements = new FacetRefinementVO();
														facetRefinements.setDimensionName(childDimVal.getDimensionName());
														facetRefinements.setName(childDimVal.getName());
														facetRefinements.setIntlFlag(BBBCoreConstants.YES_CHAR);
														for (Map.Entry<String,String> entry : attributeMap.entrySet()) {
												            if(entry.getKey().contains(childDimVal.getName())){
												            	facetRefinements.setIntlFlag(entry.getValue());
												            }
												        }
										final String navigationValue = ENEQueryToolkit
												.selectRefinement(navigation, childDimVal).toString();
										facetRefinements.setQuery(getEndecaClient().createRefinementHyperlink(
												navigationValue, queryParamForFacets, pSearchQuery));
										// Added for R2.2 SEO friendly Story :
										// Start
														facetRefinements.setFacetRefFilter(navigationValue.replaceAll(" ", "-"));
										// Added for R2.2 SEO friendly Story :
										// End
														if(null != childDimVal.getProperties()){
											facetRefinements.setSize((String) childDimVal.getProperties()
													.get(BBBEndecaConstants.DGRAPH_BINS));
														}
														facetRefinements.setCatalogId(String.valueOf(childDimVal.getId()));
														facetRefineList.add(facetRefinements);
													//}
												//}
											}
										}
								// END -- R2.1 -- Attribute Facet Story # 213
								// (a)
										else{
											facetRefinements = new FacetRefinementVO();
											facetRefinements.setDimensionName(childDimVal.getDimensionName());
											facetRefinements.setName(childDimVal.getName());
									final String navigationValue = ENEQueryToolkit
											.selectRefinement(navigation, childDimVal).toString();
									facetRefinements.setQuery(getEndecaClient().createRefinementHyperlink(
											navigationValue, queryParamForFacets, pSearchQuery));
											// Added for R2.2 SEO friendly Story : Start
											facetRefinements.setFacetRefFilter(navigationValue.replaceAll(" ", "-"));
											// Added for R2.2 SEO friendly Story : End
											if(null != childDimVal.getProperties()){
										facetRefinements.setSize((String) childDimVal.getProperties()
												.get(BBBEndecaConstants.DGRAPH_BINS));
											}
											facetRefinements.setCatalogId(String.valueOf(childDimVal.getId()));
											facetRefineList.add(facetRefinements);
										}
									}
								}
								long endTime = System.currentTimeMillis();
						logDebug("Completed Iterating Child Dimenstions. Operating Took :" + (endTime - startTime)
								+ " ms");

								facetParent.setFacetRefinement(facetRefineList);
						// START -- R2.1 -- This condition added for mobile 2.1
						// multiselect requirement
						if (rootDimension.getRoot() != null) {
								facetParent.setMultiSelect(rootDimension.getRoot().isMultiSelectOr());
							    }
								if(("RATINGS").equalsIgnoreCase(dimensionMap.get(rootDimension.getName()))){
									List<FacetRefinementVO> orderedFacetRefineList = new ArrayList<FacetRefinementVO>();
									if(null != facetRefineList && facetRefineList.size() > 1){
								if (facetRefineList.get(0).getName().charAt(0) < facetRefineList.get(1).getName()
										.charAt(0)) {
											for(int i=1; i <= facetRefineList.size();i++){
												orderedFacetRefineList.add(facetRefineList.get(facetRefineList.size()-i));
											}
											facetParent.setFacetRefinement(orderedFacetRefineList);
										}
									}
								}

								// START -- R2.1 -- Attribute Facet Story #213
						// Pass this current facet in result only when there is
						// an eligible Attribute values on that site for
						// ATTRIBUTES dimension.
						if (null != facetParent.getFacetRefinement() && facetParent.getFacetRefinement().size() >= 1
								&& showRatingFacet) {
									facetParentList.add(facetParent);
								}
								// END   -- R2.1 -- Attribute Facet Story #213
					}

				}
			}catch(Exception e){
				logError("Exception thrown in getFacets",e);
			}
		}

		/*
		 * if(!persCheck){ Dimension
		 * rootDimension=navigation.getRefinementDimensions().getDimension(
		 * "Eligible_Customizations"); if(null!=rootDimension){ facetParent =
		 * new FacetParentVO(); facetRefineList = new
		 * ArrayList<FacetRefinementVO>();
		 * facetParent.setName(dimensionMap.get(rootDimension.getName())); //
		 * This condition added for mobile multiselect requirement
		 * if(rootDimension.getRoot() != null) {
		 * facetParent.setMultiSelect(rootDimension.getRoot().isMultiSelectOr())
		 * ; }
		 *
		 * facetParent.setQuery(getEndecaClient().createExposureHyperlink(
		 * rootDimension.getId(), queryParamForFacets,pSearchQuery));
		 *
		 * final DimValList childDimList = rootDimension.getRefinements(); final
		 * ListIterator childDimIter = childDimList.listIterator();
		 * while(childDimIter.hasNext()){ childDimVal = (DimVal)
		 * childDimIter.next(); facetRefinements = new FacetRefinementVO();
		 * facetRefinements.setDimensionName(childDimVal.getDimensionName());
		 * facetRefinements.setName(childDimVal.getName()); final String
		 * navigationValue = ENEQueryToolkit.selectRefinement(navigation,
		 * childDimVal).toString();
		 * facetRefinements.setQuery(getEndecaClient().createRefinementHyperlink
		 * (navigationValue, queryParamForFacets,pSearchQuery)); // Added for
		 * R2.2 SEO friendly Story : Start
		 * facetRefinements.setFacetRefFilter(navigationValue.replaceAll(" ",
		 * "-")); // Added for R2.2 SEO friendly Story : End if(null !=
		 * childDimVal.getProperties()){ facetRefinements.setSize((String)
		 * childDimVal.getProperties().get(BBBEndecaConstants.DGRAPH_BINS)); }
		 * facetRefinements.setCatalogId(String.valueOf(childDimVal.getId()));
		 * facetRefineList.add(facetRefinements); }
		 * facetParent.setFacetRefinement(facetRefineList); if(null !=
		 * facetParent.getFacetRefinement() &&
		 * facetParent.getFacetRefinement().size() >=1){
		 * facetParentList.add(facetParentList.size(),facetParent); } } }
		 */

		//Code for price range MX
		/*
		 * Dimension rootDimensionPriceMx = (null != navigation && null !=
		 * navigation.getRefinementDimensions() && null !=
		 * navigation.getRefinementDimensions().getDimension(BBBCoreConstants.
		 * PRICE_RANGE_MX)) ?
		 * navigation.getRefinementDimensions().getDimension(BBBCoreConstants.
		 * PRICE_RANGE_MX) : null; if(!priceRangeMexicoCheck &&
		 * rootDimensionPriceMx!=null){ //Dimension
		 * rootDimension=navigation.getRefinementDimensions().getDimension(
		 * BBBCoreConstants.PRICE_RANGE_MX); facetParent = new FacetParentVO();
		 * facetRefineList = new ArrayList<FacetRefinementVO>();
		 * facetParent.setName(dimensionMap.get(rootDimensionPriceMx.getName()))
		 * ; // This condition added for mobile multiselect requirement
		 * if(rootDimensionPriceMx.getRoot() != null) {
		 * facetParent.setMultiSelect(rootDimensionPriceMx.getRoot().
		 * isMultiSelectOr()); }
		 * facetParent.setQuery(getEndecaClient().createExposureHyperlink(
		 * rootDimensionPriceMx.getId(), queryParamForFacets,pSearchQuery));
		 * final DimValList childDimList =
		 * rootDimensionPriceMx.getRefinements(); final ListIterator
		 * childDimIter = childDimList.listIterator();
		 * while(childDimIter.hasNext()){ childDimVal = (DimVal)
		 * childDimIter.next(); facetRefinements = new FacetRefinementVO();
		 * facetRefinements.setDimensionName(childDimVal.getDimensionName());
		 * facetRefinements.setName(childDimVal.getName()); final String
		 * navigationValue = ENEQueryToolkit.selectRefinement(navigation,
		 * childDimVal).toString();
		 * facetRefinements.setQuery(getEndecaClient().createRefinementHyperlink
		 * (navigationValue, queryParamForFacets,pSearchQuery)); // Added for
		 * R2.2 SEO friendly Story : Start
		 * facetRefinements.setFacetRefFilter(navigationValue.replaceAll(" ",
		 * "-")); // Added for R2.2 SEO friendly Story : End if(null !=
		 * childDimVal.getProperties()){ facetRefinements.setSize((String)
		 * childDimVal.getProperties().get(DGRAPH_BINS)); }
		 * facetRefinements.setCatalogId(String.valueOf(childDimVal.getId()));
		 * facetRefineList.add(facetRefinements); }
		 * facetParent.setFacetRefinement(facetRefineList); if( (priceRangeIndex
		 * > -1) && null != facetParent.getFacetRefinement() &&
		 * facetParent.getFacetRefinement().size() >=1){
		 * facetParentList.add(priceRangeIndex,facetParent); } }
		 */

		/* PSI 6  || BPS-798 || Changes Start || Implement department as tree*/

		/*
		 * Below condition is added fetch the L2 department wchich has highest
		 * product count, L3 under the L2 and its ancestor category
		 */
		// if(fectchCategoryTreeStrucutre && null==pSearchQuery.getNarrowDown())
		// {
//			if(null != navigation) {
		// logDebug("navigation.getRefinementDimensions()
		// :"+navigation.getRefinementDimensions());
//
		// Dimension rootDimensionSiteID = (null !=
		// navigation.getRefinementDimensions()
//													&& null != navigation.getRefinementDimensions().getDimension(
//															getDepartmentConfig().get(pSearchQuery.getSiteId()))
//												)
//													? navigation.getRefinementDimensions().getDimension(
//															getDepartmentConfig().get(pSearchQuery.getSiteId()))
//													: null;
//
//				if(rootDimensionSiteID != null) {
//					endecaSiteRootDimension=String.valueOf(rootDimensionSiteID.getId());
//
		// final Dimension typeAHeadDimension =
		// navigation.getRefinementDimensions()!=null?navigation.getRefinementDimensions().getDimension(getDepartmentConfig().
//							get(pSearchQuery.getSiteId())+TYPE_AHEAD):null;
//
		// logDebug("typeAHeadDimension : "+typeAHeadDimension+ "|| For Keyword
		// :"+pSearchQuery.getKeyWord());
//
//					if (typeAHeadDimension!=null
		// && !pSearchQuery.isFromBrandPage() && !pSearchQuery.isFromCollege()
		// &&
//										null!=pSearchQuery.getKeyWord()) {
		// logDebug("fetching L2 and L3 category details for Department tree
		// structure for keyword :"+pSearchQuery.getKeyWord());
//						final DimValList childDimList = typeAHeadDimension.getRefinements();
		// final ListIterator<DimVal> childDimIter =
		// childDimList.listIterator();
//						String dimID=null;
//						while(childDimIter.hasNext()){
//							childDimVal = childDimIter.next();
//							facetRefinementL2 = new FacetRefinementVO();
//							facetRefinementL2.setName(childDimVal.getName());
//							facetRefinementL2.setDimensionName(childDimVal.getDimensionName());
//							if(null != childDimVal.getProperties()){
		// facetRefinementL2.setSize((String)
		// childDimVal.getProperties().get(DGRAPH_BINS));
//								dimID=String.valueOf(childDimVal.getProperties().get(NODE_ID));
//							}
//							logDebug("L2 Department : "+dimID);
//							facetRefinementL2.setCatalogId(dimID);
//							ancestercategory=getDepartmentsForTreeNavigation(dimID,endecaSiteRootDimension,pSearchQuery,facetRefinementL2);
		// logDebug("Parent Category for
		// L2:"+childDimVal.getProperties().get(NODE_ID)+" is
		// :"+ancestercategory);
//							if(null!=ancestercategory){
//								fectchCategoryTreeStrucutre=false;
//								break;
//							}
//						}
//					}
		// /* Below Condition is to map l2 Department to ancestor category(L1
		// department)*/
//					if(!facetParentList.isEmpty() && ancestercategory!=null) {
//						for(FacetParentVO fpVO:facetParentList) {
//							if(fpVO.getName().equals(DEPARTMENT)){
		// logDebug("size of Facet Refinements :
		// "+fpVO.getFacetRefinement()!=null?String.valueOf(fpVO.getFacetRefinement().size()):null);
//								for(FacetRefinementVO frVO:fpVO.getFacetRefinement()) {
		// logDebug("frVO.getCatalogId() : "+frVO.getCatalogId() +"
		// ancestercategory: "+ancestercategory);
//									if(frVO.getCatalogId().equals(ancestercategory)) {
		// logDebug("Ancestor Category Matched with L1
		// category:"+ancestercategory);
//										frVO.setFacetsRefinementVOs(new ArrayList<FacetRefinementVO>());
//										frVO.getFacetsRefinementVOs().add(facetRefinementL2);
		// // calling again below method to get all the l2 departments for
		// Ancestor category i.e L1 Department
//										getDepartmentsForTreeNavigation(ancestercategory,endecaSiteRootDimension,pSearchQuery,frVO);
//										ancestercategory=null;
//										break;
//									}
//								}
//							}
//							if(ancestercategory==null) {
//								break;
//							}
//						}
//					}
//				}
//			} else {
		// logDebug("looking for typeAheadDimensionName from facetsList of
		// contentResponseVO :"+typeAheadDimensionName);
		// if(null != facetParentTypeAhead && !pSearchQuery.isFromBrandPage() &&
		// !pSearchQuery.isFromCollege() &&
//						null!=pSearchQuery.getKeyWord()) {
		// logDebug("fetching L2 and L3 category details for Department tree
		// structure for keyword :"+pSearchQuery.getKeyWord());
//					//Use first facetRefinement to fetch DeptTreeStructure
//					if(!facetParentTypeAhead.getFacetRefinement().isEmpty()) {
		// FacetRefinementVO l2Refinement = (FacetRefinementVO)
		// facetParentTypeAhead.getFacetRefinement().get(0);
//
//						facetRefinementL2 = new FacetRefinementVO();
//						facetRefinementL2.setName(l2Refinement.getName());
//						facetRefinementL2.setDimensionName(l2Refinement.getDimensionName());
//						facetRefinementL2.setSize(l2Refinement.getSize());
//						logDebug("L2 Department : "+l2Refinement.getNodeId());
//						facetRefinementL2.setCatalogId(l2Refinement.getNodeId());
//						ancestercategory=getDepartmentsForTreeNavigation(l2Refinement.getNodeId(),l2Refinement.getNodeId(),pSearchQuery,facetRefinementL2);
		// logDebug("Parent Category for L2:"+l2Refinement.getNodeId()+" is
		// :"+ancestercategory);
//						if(null!=ancestercategory){
//							fectchCategoryTreeStrucutre=false;
//						}
//					}
		// /* Below Condition is to map l2 Department to ancestor category(L1
		// department)*/
//					if(!facetParentList.isEmpty() && ancestercategory!=null) {
//						for(FacetParentVO fpVO:facetParentList) {
//							if(fpVO.getName().equals(DEPARTMENT)){
		// logDebug("size of Facet Refinements :
		// "+fpVO.getFacetRefinement()!=null?String.valueOf(fpVO.getFacetRefinement().size()):null);
//								for(FacetRefinementVO frVO:fpVO.getFacetRefinement()) {
		// logDebug("frVO.getCatalogId() : "+frVO.getCatalogId() +"
		// ancestercategory: "+ancestercategory);
//									if(frVO.getCatalogId().equals(ancestercategory)) {
		// logDebug("Ancestor Category Matched with L1
		// category:"+ancestercategory);
//										frVO.setFacetsRefinementVOs(new ArrayList<FacetRefinementVO>());
//										frVO.getFacetsRefinementVOs().add(facetRefinementL2);
		// // calling again below method to get all the l2 departments for
		// Ancestor category i.e L1 Department
//										getDepartmentsForTreeNavigation(ancestercategory,ancestercategory,pSearchQuery,frVO);
//										ancestercategory=null;
//										break;
//									}
//								}
//							}
//							if(ancestercategory==null) {
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
		/* PSI 6  || BPS-798 || Implemente department as tree || Changes End*/

		logDebug("Facet List: " +facetParentList);
		logDebug("Exit EndecaSearch.getFacets method.");

		long EndTime1 = System.currentTimeMillis();
		logDebug(" getFacets ends at  :"	+ EndTime1 + " Total Time Taken="+(EndTime1 -startTime1));
		return facetParentList;
	}

	/**
	 * This method retrieves the Pagination details from Search Engine.
	 *
	 * @param argNav
	 * @param argPageSize
	 * @param argQueryString
	 * @return
	 */
	public PaginationVO getPagination(final BBBProductList products, final String pageSize, final String argQueryString,
			final SearchQuery pSearchQuery) {

		logDebug("Entering EndecaSearch.getPagination method.");

		if(null == products) {
			return null;
		}

		PaginationVO pagingLinks = null;
		long currentOffset = 0L;
		long currentPage = 0L;
		long recordCount = 0L;
		long pageCount = 0L;
		long remainder = 0L;
		final long firstOffset = 1L;
		long nextOffset = 0L;
		String firstPage = null;
		String secondPage = null;
		String thirdPage = null;
		String previousPage = null;
		String nextPage = null;
		String lastPage = null;
		pagingLinks = new PaginationVO();
		final long argPageSize = Integer.parseInt(pageSize);
		String queryParamForPagination = null;
		List<String> pageNumberList = null;
		currentOffset = products.getRecordOffset();
		currentPage = (currentOffset / argPageSize) + 1L;
		recordCount = products.getBBBProductCount();
		remainder = recordCount % argPageSize;
		pageCount = recordCount / argPageSize;

		if (remainder > 0) {
			pageCount++;
		}
		pagingLinks.setPageSize(argPageSize);
		pageNumberList = pagingLinks.getPageNumbers();

		// Added for R2.2 SEO friendly Story : Start
		if(!BBBUtility.isEmpty(argQueryString)){
		
		String filterString = argQueryString.substring(0, argQueryString.indexOf("?"));
		
		if(filterString.contains(BBBCoreConstants.PLUS)) {
			
		String[] nValueArr = filterString.split(BBBCoreConstants.SLASH_PLUS);
		
		List<String> list = Arrays.asList((String[])ArrayUtils.subarray(nValueArr, 2, nValueArr.length));
		Collections.sort(list, new Comparator<String>() {
	        public int compare(String o1, String o2) {
	        	return BigInteger.valueOf(Long.parseLong(o1)).compareTo(BigInteger.valueOf(Long.parseLong(o2)));
	        }
	    });
		StringBuilder sb = new StringBuilder(list.size());
		sb.append(nValueArr[0]).append(BBBCoreConstants.HYPHEN).append(nValueArr[1]);
		 for(String s : list){
			 sb.append(BBBCoreConstants.HYPHEN).append(s);
		 }
		 filterString = sb.toString();
		}
		pagingLinks.setCanonicalPageFilter(filterString);
		pagingLinks.setPageFilter(argQueryString.substring(0, argQueryString.indexOf("?")).replace('+', '-'));
		queryParamForPagination = argQueryString.substring(argQueryString.indexOf("?") + 1);
		}
		String page = null;
		for (long pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
			page = getEndecaClient().createPagingHyperlink(pageNumber, queryParamForPagination, argPageSize,
					pSearchQuery);
			pageNumberList.add(page);
		}

		firstPage = getEndecaClient().createPagingHyperlink(firstOffset, queryParamForPagination, argPageSize,
				pSearchQuery);
		// Added for R2.2 SEO friendly Story : Start

		// Get first page link
		if (currentOffset >= argPageSize) {
			previousPage = getEndecaClient().createPagingHyperlink(currentPage - 1, queryParamForPagination,
					argPageSize, pSearchQuery);

		}
		// get second page link
		if(pageCount>=2){
			secondPage = getEndecaClient().createPagingHyperlink(firstOffset + 1, queryParamForPagination, argPageSize,
					pSearchQuery);
			pagingLinks.setSecondPage(secondPage);
		}
		//get third page link
		if(pageCount>=3){
			thirdPage = getEndecaClient().createPagingHyperlink(firstOffset + 2, queryParamForPagination, argPageSize,
					pSearchQuery);
			pagingLinks.setThirdPage(thirdPage);
		}

		pagingLinks.setThirdLast(getEndecaClient().createPagingHyperlink((currentPage - 2), queryParamForPagination,
				argPageSize, pSearchQuery));
		pagingLinks.setSecondLast(getEndecaClient().createPagingHyperlink((currentPage - 1), queryParamForPagination,
				argPageSize, pSearchQuery));
		pagingLinks.setCurrentPageUrl(getEndecaClient().createPagingHyperlink(currentPage, queryParamForPagination,
				argPageSize, pSearchQuery));
		pagingLinks.setFirstPage(firstPage);
		pagingLinks.setPreviousPage(previousPage);

		// Get the next page and last page link.
		if (currentOffset < ((pageCount - 1) * argPageSize)) {
			nextOffset = currentPage + 1;
			nextPage = getEndecaClient().createPagingHyperlink(nextOffset, queryParamForPagination, argPageSize,
					pSearchQuery);
			lastPage = getEndecaClient().createPagingHyperlink(pageCount, queryParamForPagination, argPageSize,
					pSearchQuery);
			pagingLinks.setNextPage(nextPage);
			pagingLinks.setLastPage(lastPage);
		}
		pagingLinks.setPageCount(pageCount);
		pagingLinks.setCurrentPage(currentPage);

		logDebug("Exit EndecaSearch.getPagination method.");

		return pagingLinks;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.bbb.search.ISearch#performFacetSearch(com.bbb.search.bean.result.
	 * FacetQuery)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public FacetQueryResults performFacetSearch(final FacetQuery pFacetQuery)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.performFacetSearch method.");

		final String methodName = BBBCoreConstants.ENDECA_FACET_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		ENEQueryResults results = null;
		//ENEQuery queryObject = null;
		//FacetQueryResults pFQResults = null;
		//ENEContentQuery contentQuery = null;
		//ENEContentResults contentResults = null;
		// final Map<String,List<FacetQueryResult>> pFinResult = new
		// HashMap<String, List<FacetQueryResult>>();
		final List<FacetQueryResult> pResults = new ArrayList<FacetQueryResult>();
		final FacetQueryResult pFacetQueryResult =new FacetQueryResult();
		final FacetQueryResult pFacetQueryResult1 =new FacetQueryResult();
		FacetQueryResults pFacetQueryResults = null;

		String brandFacetName = BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE,
				EndecaSearch.BRAND_KEY);
		// Generate Dimension query
		// Request is for getAllBrands
		if(pFacetQuery.getKeyword() == null){
			pFacetQuery.setKeyword("");
		}

		String channel = BBBUtility.getChannel();
		String channelThemeId = BBBUtility.getChannelTheme();
		final UrlGen urlGen = new UrlGen(null, getEndecaClient().getEncoding());

		String siteDimensionId = getCatalogId("Site_ID",getSiteIdMap().get(pFacetQuery.getSiteId()));

		setURLGenAndDimFilter(pFacetQuery, channel, channelThemeId, urlGen, siteDimensionId);

		// fetching the search result from cache

		final String cacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
				BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME).get(0);
		int cacheTimeout = 0;
		try {
			cacheTimeout = Integer
					.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
							BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT).get(0));
		} catch (NumberFormatException exception) {
			logError("EndecaSearch.performFacetSearch || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, exception);
			cacheTimeout = getKeywordCacheTimeout();
		} catch (NullPointerException exception) {
			logError("EndecaSearch.performFacetSearch || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, exception);
			cacheTimeout = getKeywordCacheTimeout();
		}

		String showPopularTerms = BBBCoreConstants.FALSE;
		if(pFacetQuery.isShowPopularTerms()){
			showPopularTerms = BBBCoreConstants.TRUE;
		}

		// get keyName for getting type ahead search result from coherence cache
		String keyName = getCacheKeyName(pFacetQuery.getKeyword(), showPopularTerms, pFacetQuery.getSiteId(), channel,
				channelThemeId);
		logDebug("cache key:" + keyName);
		logDebug("EndecaSearch.performFacetSearch() Getting value from cache using KeyName :: " + keyName);
		pFacetQueryResults = (FacetQueryResults) getObjectCache().get(keyName, cacheName);

		// request used to get max search, max popular, max dept size parameter
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

		logDebug("cache Name performFacetSearch:" + cacheName);
		String topPopularTerm = null;
		if (pFacetQueryResults == null || pFacetQueryResults.getResults() == null
				|| pFacetQueryResults.getResults().isEmpty()) {

			try{
				pFacetQueryResults = new FacetQueryResults();
				// call endeca for fetching search result in case not there in
				// cache
				logDebug("search results not available in cache hence fetching from Endeca");
				//actual call to Endeca
				results = getEndecaClient()
						.executeQuery(new UrlENEQuery(urlGen.toString(), getQueryGenerator().getEncoding()));

				/*
				 * ContentItem responseContentItem =
				 * executeAssemblerQuery(urlGen.toString(),
				 * "/content/Web/Search Pages"); if(null!=
				 * responseContentItem.get(
				 * CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO)) { SearchResults
				 * searchResults = (SearchResults) responseContentItem.get(
				 * CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO); }
				 */

				logDebug("Endeca Query String : " + urlGen.toString());

			} catch (ENEQueryException e) {
				BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
						"ENEQueryException in calling Endeca performFacetSearch");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"Endeca Exception",e);
			}
			/*
			 * catch (InitializationException e) { BBBUtility.passErrToPage(
			 * BBBCoreErrorConstants.ERROR_ENDECA_1002,
			 * "InitializationException in calling Endeca performFacetSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new
			 * BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1002,
			 * "Endeca Exception",e); } catch (InvalidQueryException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1003,
			 * "InvalidQueryException in calling Endeca performFacetSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new
			 * BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1003,
			 * "Endeca Exception",e); } catch (ContentException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1004,
			 * "ContentException in calling Endeca performFacetSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new
			 * BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1004,
			 * "Endeca Exception",e); } catch (AssemblerException e) {
			 * BBBUtility.passErrToPage(
			 * BBBCoreErrorConstants.ERROR_ENDECA_1004,
			 * "AssemblerException in calling Endeca performFacetSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new
			 * BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1004,
			 * "Endeca Exception",e); }
			 */

			final DimensionSearchResult dimSearchResult = results.getDimensionSearch();
			DimensionSearchResultGroupList groups = null;
			groups = dimSearchResult.getResults();
			final ListIterator iterGroups = groups.listIterator();
			// Iterator for getting Results map.
			final Map<String,String> pMatches = new LinkedHashMap<String, String>();
			final Map<String,String> pMatches1 = new LinkedHashMap<String, String>();

			//System.out.println("iterGroups.hasNext()"+iterGroups.hasNext());
			final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();

			// BBBSL-4906 defect fixed. Fetching the maximum cap for the type
			// ahead buckets from BCC.
			int deptMaxMatches = getSearchUtil().getMaxCountForCacheEntry(BBBCoreConstants.DEPT_TYPE_AHEAD_SIZE,
					request);
			int brandMaxMatches = getSearchUtil().getMaxCountForCacheEntry(BBBCoreConstants.BRAND_TYPE_AHEAD_SIZE,
					request);
			int popularMaxMatches = getSearchUtil().getMaxCountForCacheEntry(BBBCoreConstants.POPULAR_TYPE_AHEAD_SIZE,
					request);

			setFacetNameAndMatchesInFacetQuery(pFacetQueryResult, pFacetQueryResult1, brandFacetName, iterGroups,
					pMatches, pMatches1, dimensionMap, deptMaxMatches, brandMaxMatches);

			pFacetQueryResults.setFacetQuery(pFacetQuery);

			/* START R2.1 TypeAhead for Most Popular Keywords */

			topPopularTerm = getPopularSearchTerm(pFacetQuery, results, pResults, topPopularTerm, popularMaxMatches);

			logDebug("EndecaSearch.performFacetSearch() :Top popular Term  " + topPopularTerm);

			if(deptMaxMatches!=0 || brandMaxMatches!=0){
				pResults.add(pFacetQueryResult);
			}
			/* END   R2.1 TypeAhead for Most Popular Keywords */

			if(brandMaxMatches!=0)
				pResults.add(pFacetQueryResult1);

			pFacetQueryResults.setResults(pResults);
			// these lines commented out becuase in type ahead if user sele
			// specific dept and then search, department value in Type ahead was
			// not populating correctly.
			// get maximum results to show for search in departments results
			// Search within departments method call
			getSearchUtil().populateSearchInDept(pFacetQueryResults, topPopularTerm, pFacetQuery, siteDimensionId,
					cacheTimeout);
			// SAP-232 defect fixed. Created config key to switch ON/OFF type
			// ahead caching for mobile requests.
			String typeAheadSrchCacheMob = BBBCoreConstants.TRUE;
			try {
				typeAheadSrchCacheMob = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						BBBEndecaConstants.STORE_MOBILE_TYPE_AHEAD_CACHE).get(0);
			} catch (BBBBusinessException excep){
				logError(
						"Business exception while fetching config key to store type ahead cache for mobile in EndecaSearch.performFacetSearch() method hence making it to TRUE");
			}catch (BBBSystemException excep){
				logError(
						"System exception while fetching config key to store type ahead cache for mobile in EndecaSearch.performFacetSearch() method hence making it to TRUE");
			}

			boolean isStoreInCache = true;

			if ((BBBCoreConstants.FALSE.equalsIgnoreCase(typeAheadSrchCacheMob))
					&& (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)
							|| BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel))) {
				isStoreInCache = false;
				logDebug(
						"Request is from Mobile Web or App and config value for storing type ahead search in cache is set to FALSE hence type ahead cache is disabled for Mobile");
			}

			if(!BBBUtility.isListEmpty(pFacetQueryResults.getResults()) && isStoreInCache ){
 				getObjectCache().put(keyName, pFacetQueryResults, cacheName, cacheTimeout);
 				logDebug("EndecaSearch.performFacetSearch() Putting value to cache using KeyName :: " + keyName);
			}else{
				logDebug("EndecaSearch.performFacetSearch() NOT value to cache using KeyName :: " + keyName);
 			}

		/*BAND 770 - Search within departments story - START */
		}else{
			logDebug("Search result fetched from cache, Finding top popular term from cached data");

			// For Desktop channel for search in department feature, find top
			// popular term
			if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())){
				if(!BBBUtility.isListEmpty(pFacetQueryResults.getResults())){
					List<FacetQueryResult> tempResults = pFacetQueryResults.getResults();
					if(tempResults!=null){
						for(FacetQueryResult queryResult : tempResults){
							if (BBBEndecaConstants.POPULAR.equalsIgnoreCase(queryResult.getFacetName())
									&& queryResult.getMatches() != null) {
								topPopularTerm = queryResult.getMatches().get("0");
								logDebug("Top popular term fetched from the endeca popular term results : "
										+ topPopularTerm);
								break;
							}
						}
					}
					getSearchUtil().populateSearchInDept(pFacetQueryResults, topPopularTerm,  pFacetQuery,
							siteDimensionId, cacheTimeout);
				}
			}
		}

		FacetQueryResults facetQueryResults = pFacetQueryResults;
		// BBBSL-4906 defect fixed. Put cap/limit on search results for
		// type-ahead buckets
		if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())){
			facetQueryResults = getSearchUtil().doCapCacheResults(pFacetQueryResults);
			if(facetQueryResults != null){
				facetQueryResults.setFacetQuery(pFacetQueryResults.getFacetQuery());
				facetQueryResults.setTopPopularTerm(pFacetQueryResults.getTopPopularTerm());
			}
		}

		/*
		 * if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel(
		 * ))){ getSearchUtil().doCapCacheResultsMobile(pFacetQueryResults);
		 * BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
		 * methodName); logDebug(
		 * "Exiting EndecaSearch.performFacetSearch method.");
		 * //System.out.println("pFacetQueryResults"+pFacetQueryResults.
		 * getResults().toString()); return pFacetQueryResults; }
		 */

		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		logDebug("Exiting EndecaSearch.performFacetSearch method.");
		//System.out.println("pFacetQueryResults"+pFacetQueryResults.getResults().toString());
		return facetQueryResults;
	}

	/**
	 * This method sets all the matches and facet name in FacetQueryResult
	 *
	 * @param pFacetQueryResult
	 * @param pFacetQueryResult1
	 * @param brandFacetName
	 * @param iterGroups
	 * @param pMatches
	 * @param pMatches1
	 * @param dimensionMap
	 * @param deptMaxMatches
	 * @param brandMaxMatches
	 */
	private void setFacetNameAndMatchesInFacetQuery(final FacetQueryResult pFacetQueryResult,
			final FacetQueryResult pFacetQueryResult1, String brandFacetName, final ListIterator iterGroups,
			final Map<String, String> pMatches, final Map<String, String> pMatches1,
			final Map<String, String> dimensionMap, int deptMaxMatches, int brandMaxMatches) {

		if(deptMaxMatches!=0 || brandMaxMatches!=0){
			while (iterGroups.hasNext()) {
				final DimensionSearchResultGroup group = (DimensionSearchResultGroup) iterGroups.next();
				final DimValList roots = group.getRoots();
				final ListIterator iterRoots = roots.listIterator();
				while (iterRoots.hasNext()) {
					final DimVal root = (DimVal) iterRoots.next();
					final String site = root.getName();
					//System.out.println("Root Name: "  + site);
					//if(site.equals(getCatalog())){
					if (("DEPARTMENT".equals(dimensionMap.get(site)))
							|| (brandFacetName.equals(dimensionMap.get(site)))) {
						final ListIterator iterDimLocationLists = group.listIterator();
						while (iterDimLocationLists.hasNext()) {
							final DimLocationList dimLocationList = (DimLocationList) iterDimLocationLists.next();
							final ListIterator iterDimLocations = dimLocationList.listIterator();
							boolean flag = false;
							while (iterDimLocations.hasNext()) {
								final DimLocation dimLocation = (DimLocation) iterDimLocations.next();
								final DimValList ancestors = dimLocation.getAncestors();
								// System.out.println("Ancestors List: "+
								// ancestors);
								final ListIterator iterAncestors = ancestors.listIterator();
								String name;
								StringBuffer breadcrumb = null;

								while (iterAncestors.hasNext()) {
									final DimVal ancestor = (DimVal) iterAncestors.next();
									name = ancestor.getName();
									if (breadcrumb != null && breadcrumb.length() != 0) {
										breadcrumb.append(" > " + name + " > ");
										flag = true;
									} else {
										breadcrumb = new StringBuffer(name);
									}
								}
								final DimVal dimVal = dimLocation.getDimValue();
								final long ID = dimVal.getId();
								name = dimVal.getName();
								if(breadcrumb != null && breadcrumb.length() !=0 ){
									if (flag){
										breadcrumb.append(name) ;
									} else {
										breadcrumb.append(" > ") ;
										breadcrumb.append(name) ;
									}
								} else {
									breadcrumb = new StringBuffer(name);
								}
								//if(dimVal.getName().equals("Departments")){
								if("DEPARTMENT".equals(dimensionMap.get(site)) && deptMaxMatches!=0){
									pFacetQueryResult.setFacetName("department");
									if(pMatches.size() < deptMaxMatches){
										//PS-61408 | Adding phantom category check to hide phantom categories from Department suggestions
										boolean phantom=false;
										try {
											phantom = getCatalogTools().isPhantomCategory(String.valueOf(ID));
										} catch (BBBSystemException e) {
											logError(" Exception in method setFacetNameAndMatchesInFacetQuery ::" , e);
										} catch (BBBBusinessException e) {
											logError(" Exception in method setFacetNameAndMatchesInFacetQuery ::" +ID);
										} 
										if(!phantom){
											pMatches.put( String.valueOf(ID), breadcrumb.toString());
										}
									}
									pFacetQueryResult.setMatches(pMatches);
									logDebug("EndecaSearch.performFacetSearch() :DEPARTMENT pFacetQueryResult1 "
											+ pFacetQueryResult);
								} else if (brandFacetName.equals(dimensionMap.get(site)) && brandMaxMatches != 0) {
									// else
									// if(dimVal.getName().equals("Brands")){
									pFacetQueryResult1.setFacetName(brandFacetName);
									if(pMatches1.size() < brandMaxMatches){
										pMatches1.put( String.valueOf(ID), breadcrumb.toString());
									}
									pFacetQueryResult1.setMatches(pMatches1);
									logDebug("EndecaSearch.performFacetSearch() :Brand pFacetQueryResult1 "
											+ pFacetQueryResult1);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This methods returns popular search term
	 *
	 * @param pFacetQuery
	 * @param results
	 * @param pResults
	 * @param topPopularTerm
	 * @param popularMaxMatches
	 * @return
	 */
	private String getPopularSearchTerm(final FacetQuery pFacetQuery, ENEQueryResults results,
			final List<FacetQueryResult> pResults, String topPopularTerm, int popularMaxMatches) {

		if(pFacetQuery.isShowPopularTerms() && results.containsNavigation() && popularMaxMatches!=0){
			// Call process Response method to retrieve Search Results object.
			final Navigation nav= results.getNavigation();
			final ERecList records = nav.getERecs();
			ERec record = null;
			PropertyMap properties = null;
			String match = null;
			final ListIterator iterRecords = records.listIterator();
			final FacetQueryResult pFacetQueryResult2 =new FacetQueryResult();
			final Map<String,String> matchTree = new LinkedHashMap<String, String>();
			for (int i = 0; iterRecords.hasNext() && matchTree.size() < popularMaxMatches; i++) {
				pFacetQueryResult2.setFacetName(BBBEndecaConstants.POPULAR);
				record = (ERec) iterRecords.next();
				properties = record.getProperties();
				match = (String) properties.get(getConfigUtil().getPropertyMap().get("POPULAR_SEARCH_TERM"));
				matchTree.put(String.valueOf(i), match);
			}

			pFacetQueryResult2.setMatches(matchTree);
			pResults.add(pFacetQueryResult2);
			logDebug("EndecaSearch.performFacetSearch() :Popular Term pFacetQueryResult2 " + pFacetQueryResult2);

			if(!BBBUtility.isEmpty(pFacetQueryResult2.getFacetName())) {
				topPopularTerm = pFacetQueryResult2.getMatches().get("0");
			} else if (BBBUtility.isEmpty(pFacetQueryResult2.getFacetName()) && iterRecords.hasNext()) {
				record = (ERec) iterRecords.next();
				properties = record.getProperties();
				topPopularTerm = (String) properties.get(getConfigUtil().getPropertyMap().get("POPULAR_SEARCH_TERM"));
			}
		}
		return topPopularTerm;
	}

	/**
	 * Setting URL gen and Dimension Record Filter
	 *
	 * @param pFacetQuery
	 * @param channel
	 * @param channelThemeId
	 * @param urlGen
	 * @param siteDimensionId
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void setURLGenAndDimFilter(final FacetQuery pFacetQuery, String channel, String channelThemeId,
			final UrlGen urlGen, String siteDimensionId) throws BBBBusinessException, BBBSystemException {

		urlGen.addParam(BBBEndecaConstants.DIM_QUERY_KEYWORD,pFacetQuery.getKeyword()+"*");
		urlGen.addParam(BBBEndecaConstants.DIM_QUERY_MODE, getQueryGenerator().getSearchMode());
		urlGen.addParam(BBBEndecaConstants.DIM_REC_FILTER,
				getConfigUtil().getSiteConfig().get(pFacetQuery.getSiteId()));
		urlGen.addParam(BBBEndecaConstants.DK, BBBEndecaConstants._1);

		// BPSI-4428 START - Added channel and theme if available in header to
		// the endeca query as record dimension filter. To be used only for
		// guided selling.

		StringBuffer dimRecordFilter = null;
		if (BBBCoreConstants.CHANNEL_FORM_FACTOR_1.equalsIgnoreCase(channel)
				|| BBBCoreConstants.CHANNEL_FORM_FACTOR_2.equalsIgnoreCase(channel)) {
			dimRecordFilter = new StringBuffer();
			dimRecordFilter.append(BBBCoreConstants.AND_WITH_START_BRACKET);

			//append site id
			dimRecordFilter.append(getConfigUtil().getSiteConfig().get(pFacetQuery.getSiteId()));

			//append channel id
			dimRecordFilter.append(BBBCoreConstants.COMMA);
			dimRecordFilter.append(BBBEndecaConstants.CHANNEL_ID).append(BBBCoreConstants.COLON).append(channel);

			//append theme id if available

			if (channelThemeId != null) {
				dimRecordFilter.append(BBBCoreConstants.COMMA);
				dimRecordFilter.append(BBBEndecaConstants.THEME_ID).append(BBBCoreConstants.COLON)
						.append(channelThemeId);
			}

			dimRecordFilter.append(BBBCoreConstants.END_BRACKET);
			urlGen.addParam(BBBEndecaConstants.DIM_REC_FILTER, dimRecordFilter.toString());
		}
		//BPSI-4428 END

		/* START R2.1 TypeAhead for Most Popular Keywords */

		if(pFacetQuery.isShowPopularTerms()){
			// Get the Dimension Value Id for current Site ID.
			final String typeAheadDimId = getCatalogId("Record Type",
					getConfigUtil().getRecordTypeNames().get("TypeAhead"));
			//	Fix for defect Id 19586
			if(BBBUtility.isNotEmpty(typeAheadDimId)){
				urlGen.addParam(BBBEndecaConstants.NAVIGATION, typeAheadDimId + " " + siteDimensionId);
			} else {
				urlGen.addParam(BBBEndecaConstants.NAVIGATION, siteDimensionId);
			}
			urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD,pFacetQuery.getKeyword()+"*");
			urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME,
					getConfigUtil().getPropertyMap().get("POPULAR_SEARCH_TERM"));
			urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE,"rel+static(P_SearchHit,descending)");
		}

		/* END   R2.1 TypeAhead for Most Popular Keywords */
	}

	/**
	 * This method is used to create keyName for putting Type ahead endeca
	 * response to coherence cache.
	 *
	 * @param arg1
	 *            is searchKeyword in <code>String</code> format
	 * @param arg2
	 *            is true|false for searchPopularWord in <code>String</code>
	 *            format
	 * @param siteId
	 *            is site specific ID in <code>String</code> format
	 * @param channelThemeId
	 *            is Theme specific id to be used in case of Guided selling
	 * @param channel
	 *            is channel specific id to be used in case of Guided Selling
	 * @return keyName in <code>String</code> format.
	 */
	private String getCacheKeyName(final String arg1, final String arg2, final String siteId, String channel,
			String channelThemeId) {

		logDebug("EndecaSearch.getCacheKeyName() method - start");
		logDebug("EndecaSearch.getCacheKeyName() arg1 =" + arg1);
		logDebug("EndecaSearch.getCacheKeyName() arg2 =" + arg2);
		logDebug("EndecaSearch.getCacheKeyName() siteId =" + siteId);

		String keyName = null;
		StringBuffer keyBuff = new StringBuffer(BBBCoreConstants.TYPE_AHEAD_REST_CALL)
				.append(BBBCoreConstants.UNDERSCORE).append(arg1).append(BBBCoreConstants.UNDERSCORE).append(arg2)
				.append(BBBCoreConstants.UNDERSCORE).append(siteId);

		if (BBBCoreConstants.CHANNEL_FORM_FACTOR_1.equalsIgnoreCase(channel)
				|| BBBCoreConstants.CHANNEL_FORM_FACTOR_2.equalsIgnoreCase(channel)) {
			logDebug("EndecaSearch.getCacheKeyName() channel =" + channel);
			keyBuff.append(BBBCoreConstants.UNDERSCORE).append(channel);
			if (channelThemeId != null) {
				logDebug("EndecaSearch.getCacheKeyName() channelThemeId =" + channelThemeId);
				keyBuff.append(BBBCoreConstants.UNDERSCORE).append(channelThemeId);
			}
		}

		keyName = keyBuff.toString();

		logDebug("EndecaSearch.getCacheKeyName() keyName =" + keyName);
		logDebug("EndecaSearch.getCacheKeyName method() - end");

		return keyName;

	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String,CategoryParentVO> getCategoryTree(final SearchQuery pSearchQuery)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.getCategoryTree method.");

		final String methodName = BBBCoreConstants.ENDECA_CATALOG_ID_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,methodName);

		String cacheName = null;
		int cacheTimeout = 0;

		Map<String,CategoryParentVO> pMap =null;
		Map<String,Map<String,CategoryParentVO>> pCategoryMap = null;
		final String dimId = pSearchQuery.getCatalogRef().get(BBBSearchConstants.ROOT_CATALOG_ID);
		final String boostParam = pSearchQuery.getCatalogRef().get(BBBSearchConstants.BOOST_PARAM);
		final String categoryId = pSearchQuery.getCatalogRef().get(BBBSearchConstants.CATALOG_ID);

		boolean isFromCategoryLanding = false;
		isFromCategoryLanding = pSearchQuery.isFromCategoryLanding();
		if (isFromCategoryLanding && !BBBUtility.isEmpty(boostParam)) {
			logDebug("Boost Parameters are  : " + boostParam);
		}
		try {
			Long.parseLong(categoryId);
		} catch (NumberFormatException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1005,"NumberFormatException",e);
		}

		try {
			logDebug("cache key:" + BBBCoreConstants.HEADER_FLYOUT_CACHE_NAME);

			cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.HEADER_FLYOUT_CACHE_NAME);
			try {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.HEADER_FLYOUT_CACHE_TIMEOUT));
			} catch (NumberFormatException exception) {
				logError("EndecaSearch.getCategoryTree || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.HEADER_FLYOUT_CACHE_TIMEOUT, exception);
				cacheTimeout = getHeaderCacheTimeout();
			} catch (NullPointerException exception) {
				logError("EndecaSearch.getCategoryTree || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.HEADER_FLYOUT_CACHE_TIMEOUT, exception);
				cacheTimeout = getHeaderCacheTimeout();
			}
			logDebug("cacheName:" + cacheName);

			if(isFromCategoryLanding){
				logDebug("Request is for Category Landing Page");
				pCategoryMap = (Map<String,Map<String,CategoryParentVO>>) getObjectCache().get(categoryId+"-"+pSearchQuery.isCheckPhantom(), cacheName);
			}else{
				logDebug("Request is for flyout");
				pCategoryMap = (Map<String,Map<String,CategoryParentVO>>) getObjectCache().get(dimId+"-"+pSearchQuery.isCheckPhantom(), cacheName);
			}

			logDebug("getCategoryTree | categoryId-checkPhantom: "+categoryId+"-"+pSearchQuery.isCheckPhantom() + " or dimId-checkPantom: "+ dimId+"-"+pSearchQuery.isCheckPhantom());
			if(pCategoryMap == null){
				if (dimId == null || dimId.equalsIgnoreCase("null")){
					return new HashMap<String,CategoryParentVO>();
			    }
				logDebug("START : Map not found in cache, hence querying for base list of dimensions");
				pCategoryMap = this.getSyncCatTree(dimId, pSearchQuery, isFromCategoryLanding, categoryId, cacheName,
						cacheTimeout, boostParam);
			} else {
				logDebug("Tree map for Root category is fetched from cache.");
			}
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
		logDebug("Exit EndecaSearch.getCategoryTree method.");
		if(pCategoryMap != null){
			pMap = pCategoryMap.get(pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_ID));
			logDebug("Returning Map for Catalog Id: " + pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_ID));
		}
		logDebug("Tree map for Root category: "+dimId+" is :" +pMap);
		return pMap;
	}

	Map<String, Map<String, CategoryParentVO>> retrieveSubTreeFromParentMap(
			Map<String, Map<String, CategoryParentVO>> parentCategoryMap, String categoryId) {

		final Map<String, Map<String, CategoryParentVO>> subCatTree = new HashMap<String, Map<String, CategoryParentVO>>();
		subCatTree.put(categoryId, parentCategoryMap.get(categoryId));
		logDebug("EndecaSearch.retrieveSubTreeFromParentMap categoryId:" + categoryId + " subCatTree size :"
				+ subCatTree.size());
		return subCatTree;
	}

	/**
	 * Synchronized Block to fetch the Category Tree.
	 *
	 * @param dimId
	 * @param pSearchQuery
	 * @param pCategoryMap
	 * @param isFromCategoryLanding
	 * @param categoryId
	 * @param cacheName
	 * @param cacheTimeout
	 * @param boostParam
	 * @return
	 * @throws BBBSystemException
	 */
	private synchronized Map<String, Map<String, CategoryParentVO>> getSyncCatTree(String dimId,
			SearchQuery pSearchQuery, boolean isFromCategoryLanding, String categoryId, String cacheName,
			long cacheTimeout, String boostParam) throws BBBSystemException {

		logDebug("Entering EndecaSearch.getSyncCatTree method. For categoryId: "+categoryId + " or dimId: "+ dimId + ", isFromCategoryLanding: "+ isFromCategoryLanding);
		Map<String, Map<String, CategoryParentVO>> pCategoryMap = null;
		if(isFromCategoryLanding){
			pCategoryMap = (Map<String,Map<String,CategoryParentVO>>) getObjectCache().get(categoryId+"-"+pSearchQuery.isCheckPhantom(), cacheName);
		}else{
			pCategoryMap = (Map<String,Map<String,CategoryParentVO>>) getObjectCache().get(dimId+"-"+pSearchQuery.isCheckPhantom(), cacheName);
		}
		if(pCategoryMap!=null){
			logDebug("getSyncCatTree | pCategoryMap found in cache for categoryId-checkPhantom: " + categoryId+"-"+pSearchQuery.isCheckPhantom() + " or dimId-checkPhantom: "
					+ dimId+"-"+pSearchQuery.isCheckPhantom());
		} else {
			logDebug("getSyncCatTree | pCategoryMap not found in cache for categoryId: " + categoryId + " or dimId: "
					+ dimId+"-"+pSearchQuery.isCheckPhantom());
			final long startTime = System.currentTimeMillis();
			logDebug(
					"Start EndecaSearch.getSyncCatTree.runQuery :  Category Map Not found in Cache hence querying endeca."
							+ startTime);

			DimensionList dimensions = null;
			Map<String, Map<String, CategoryParentVO>> parentCategoryMap = null;
			if(isFromCategoryLanding){
				parentCategoryMap = (Map<String, Map<String, CategoryParentVO>>) getObjectCache().get(dimId+"-"+pSearchQuery.isCheckPhantom(), cacheName);
				
				if (null != parentCategoryMap) {
					logDebug("EndecaSearch.getSyncCatTree parentCategoryMap :"+ parentCategoryMap.size());
					pCategoryMap = retrieveSubTreeFromParentMap(parentCategoryMap, categoryId);
				}
				if (pCategoryMap == null) {
					dimensions = runQuery(Long.parseLong(categoryId), pSearchQuery.getSiteId(), boostParam);
					logDebug("isFromCategoryLanding	categoryId.. " + categoryId + " longValue:"
							+ Long.parseLong(categoryId) + "dimensions:" + dimensions + "  dimension Size: "
							+ dimensions.size());
				} else {
					logDebug("EndecaSearch.getSyncCatTree parentCategoryMap is not null categoryId:" + categoryId);
					getObjectCache().put(categoryId+"-"+pSearchQuery.isCheckPhantom(), pCategoryMap, cacheName, cacheTimeout);

				}

			} else {
				dimensions = runQuery(0,pSearchQuery.getSiteId(), boostParam);
				logDebug("TopNav categoryId as Zero.. dimensions:" + dimensions + " Dimension size: "
						+ dimensions.size());
			}
			if(null!=dimensions) {
				for (int i = 0; i < dimensions.size(); i++) {
		    		final Dimension dimension = (Dimension)dimensions.get( i );
		            final String currDim = String.valueOf(dimension.getId());
					if (dimId.equals(currDim)) {
		            	pCategoryMap = new HashMap<String, Map<String,CategoryParentVO>>() ;
		            	final Map<String,CategoryParentVO> pCatMap = null;
		            	logDebug("START: Querying get Category tree.");
						pCategoryMap = getChildDimensions(dimension.getId(), 0, pCategoryMap, pCatMap, null, null, null,
								pSearchQuery.getSiteId(), dimension.getId(), boostParam, pSearchQuery.isCheckPhantom());
		            	logDebug("END: Querying get Category tree.");
		            	if(pCategoryMap != null){
							if(isFromCategoryLanding){
		 		            	final Map<String,Map<String,CategoryParentVO>> categoryMap = new HashMap<String, Map<String,CategoryParentVO>>();
		 		            	categoryMap.put(categoryId, pCategoryMap.get(categoryId));
		 		            	getObjectCache().put(categoryId+"-"+pSearchQuery.isCheckPhantom(), categoryMap, cacheName, cacheTimeout);
		 		            	logDebug("Added data into cache for category Id : " +categoryId+"-"+pSearchQuery.isCheckPhantom());
		 		            }else{
		 		            	getObjectCache().put(dimId+"-"+pSearchQuery.isCheckPhantom(), pCategoryMap, cacheName, cacheTimeout);
		 		            	logDebug("Added data into cache for dimId : " +dimId+"-"+pSearchQuery.isCheckPhantom());
		 		            }
		            	}
		            	break;
		            }
		         }
			}
	       	final long endTime = System.currentTimeMillis();
			logDebug(
					"End EndecaSearch.getSyncCatTree.runQuery : Category Map was not found in Cache hence queried endeca. Total time taken in ms="
							+ (endTime - startTime));
		}
		logDebug("Exiting EndecaSearch.getSyncCatTree method.");
		return pCategoryMap;
	}

	/*
	 * This method iterates through all dimensions for global search and filter
	 * out the results where dimension name is configured using property named
	 * brandDimName
	 *
	 * return List<BrandVO>
	 */

	@Override
	@SuppressWarnings("rawtypes")
	public BrandsListingVO getBrands(final String catalogId, final String siteId)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.getBrands method.");

        
        ListIterator iterDimensions = null;
        final List<BrandVO> brandList = new ArrayList<BrandVO>();
        final List<BrandVO> featBrandList = new ArrayList<BrandVO>();
        BrandVO brand = null;
        
        ListIterator iterDimVals = null;
        String parentBrandName;
        BrandsListingVO brandListVO= null;

        
		String brandFacetName = BBBConfigRepoUtils.getStringValue(EndecaSearch.DIM_DISPLAY_CONFIGTYPE,
				EndecaSearch.BRAND_KEY);
        String cacheName =null;
        int cacheTimeout = 0;
        String cacheKeyId = null;
        try{
			String queryString = BBBEndecaConstants.NAVIGATION + "=" + catalogId + "+"
					+ (getCatalogId("Site_ID", getSiteIdMap().get(siteId)));
              final UrlGen urlGen = new UrlGen(queryString, getQueryGenerator().getEncoding());
              queryString = urlGen.toString();

              // BBBSL-1805 : To cache the Brand listing.
              cacheKeyId = urlGen.toString()+"_"+siteId+"_BrandList";
			cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.SEARCH_RESULT_CACHE_NAME);
              try {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.SEARCH_RESULT_CACHE_TIMEOUT));
			  } catch (NumberFormatException e) {
				logError("EndecaSearch.getBrands || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.SEARCH_RESULT_CACHE_TIMEOUT, e);
					cacheTimeout = getSearchCacheTimeout();
			  } catch (NullPointerException e) {
				logError("EndecaSearch.getBrands || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.SEARCH_RESULT_CACHE_TIMEOUT, e);
					cacheTimeout = getSearchCacheTimeout();
			  }
              logDebug("cacheName:" + cacheName +" - Key: " +cacheKeyId);

              // Check in Cache if it exits.
              brandListVO = (BrandsListingVO) getObjectCache().get(cacheKeyId, cacheName);

              // If does not exists in cache, fetch from Endeca.
              if(null == brandListVO){
            	  synchronized (this) {
					brandListVO = (BrandsListingVO) getObjectCache().get(cacheKeyId, cacheName);
					if (brandListVO == null || brandListVO.getListBrands() == null
							|| brandListVO.getListBrands().size() == 0) {
						brandListVO = new BrandsListingVO();

						SearchQuery tempSearchQuery = new SearchQuery();
						tempSearchQuery.setSiteId(siteId);
						tempSearchQuery.setFromAllBrandsPage(true);
						tempSearchQuery.setCatalogRef(new HashMap<String, String>(1) {
							{
								put("catalogId", catalogId);
							}
						});
						tempSearchQuery.setQueryURL(queryString);

						String contentCollection = getContentPathResolver()
								.resolveContentCollectionPath(tempSearchQuery);

						// setting frmBrandPage to false so that Brand facet is
						// retained after parsing
						//tempSearchQuery.setFromBrandPage(false);

						ContentItem responseContentItem = executeAssemblerQuery(tempSearchQuery, queryString,
								contentCollection);

						if (responseContentItem
								.get(BBBEndecaConstants.CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO) != null) {
							SearchResults searchResults = (SearchResults) responseContentItem
									.get(BBBEndecaConstants.CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO);

							final List<FacetParentVO> facetList = searchResults.getFacets();
							FacetParentVO facetParent = null;
							List<FacetRefinementVO> facetRefinements = null;
							FacetRefinementVO facetRefinement = null;

							// Iterating through the facet list from content
							// item
							iterDimensions = facetList.listIterator();
							while(iterDimensions.hasNext()) {
								facetParent = (FacetParentVO) iterDimensions.next();
								parentBrandName = facetParent.getName();

								// Check for the parent category Id equals to
								// current navigation state.
								if(brandFacetName.equalsIgnoreCase(parentBrandName)){
									facetRefinements = facetParent.getFacetRefinement();
									iterDimVals = facetRefinements.listIterator();
									 while (iterDimVals.hasNext()) {
										 facetRefinement = (FacetRefinementVO) iterDimVals.next();
			                             brand = new BrandVO();
			                             brand.setBrandName(facetRefinement.getName());
			                             //RM Defect 23496-Start
										brand.setBrandId(String
												.valueOf(getCatalogTools().getBrandId(facetRefinement.getName())));
			                             //RM Defect 23496-End
			                             brandList.add(brand);
			                          }
								}
							}

							final Map<String,List<PromoVO>> promoMap = searchResults.getPromoMap();
							if(null != promoMap) {
								for(String key:promoMap.keySet()){
					            	  if(("TOP").equalsIgnoreCase(key)){
						            	  final List<PromoVO> promoList = promoMap.get(key);
						            	  for(PromoVO featuredBrand:promoList ){
						            		  	brand = new BrandVO();
							  					brand.setBrandImage(featuredBrand.getImageSrc());
							  					brand.setFeaturedBrandURL(featuredBrand.getImageHref());
							  					brand.setBrandImageAltText(featuredBrand.getImageAlt());
							  					featBrandList.add(brand);
							  				}
					            	  }
						  		  }
							}

				              brandListVO.setListBrands(brandList);
				              brandListVO.setListFeaturedBrands(this.getActiveBrandsById(featBrandList));
				              logDebug("Adding retrieved Brand List to object cache for Site : "+ siteId);
				              brandListVO.setListBrands(this.getActiveBrands(brandListVO.getListBrands()));
							// Added Check to Put data in Coherence Cache only
							// if it's not null
							if (brandListVO != null && brandListVO.getListBrands() != null
									&& brandListVO.getListBrands().size() > 0) {
				            	  getObjectCache().put(cacheKeyId, brandListVO, cacheName, cacheTimeout);
				              } else if (brandListVO != null && brandListVO.getListBrands() != null) {
								logError(
										"EndecaSearch:getBrands() || No result set founds for Active Brands to put in Coherence Cache for cacheName:"
												+ cacheName + " ; cacheKeyId: " + cacheKeyId + " ; Result="
												+ brandListVO.getListBrands());
				              }
						}
            		 }
            	  }
			} else {
            	  logDebug("Fetching Brand List from object cache for Site : "+ siteId);
              }
        }
		/*
		 * catch(ENEQueryException e){
		 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
		 * "ENEQueryException in calling Endeca getBrands"); throw new
		 * BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,
		 * "Endeca Exception" , e); }
		 */catch (AssemblerException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"AssemblerException in calling Endeca getBrands");
              throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"Endeca Exception" , e);
        }
        logDebug("Exiting EndecaSearch.getBrands method.");
        return brandListVO;
	}

	/**
	 * @param listAllBrands
	 * @return List of all brands for which display flag is true
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<BrandVO> getActiveBrands(List<BrandVO>listAllBrands) throws BBBBusinessException, BBBSystemException{
		String methodName = "BrandsDroplet:getActiveBrands";
		logDebug(methodName + "[getActiveBrands method Start]");
		final List<BrandVO> listAllActiveBrands = new ArrayList<BrandVO>();
		if (listAllBrands != null && listAllBrands.size()!=0) {
			for (BrandVO brandVO : listAllBrands){

				boolean display_flag = this.getCatalogTools().getBrandDisplayFlag(brandVO.getBrandName());

				if(display_flag){
					WebApp pDefaultWebApp = null;
					UrlParameter[] pUrlParams = getBrandTemplate().cloneUrlParameters();
					pUrlParams[0].setValue(brandVO.getBrandName());
					try {
						brandVO.setSeoURL(getBrandTemplate().formatUrl(pUrlParams, pDefaultWebApp));
					} catch (ItemLinkException e) {
						logError("Exception occourred while creating SEO URL for the category : "
								+ brandVO.getBrandName(), e);
					}
					listAllActiveBrands.add(brandVO);
				}
			}
		}else{
			logDebug(methodName + "[getActiveBrands: listAllBrands is null or empty]");
		}
		logDebug(methodName + "[getActiveBrands method end]");
		return listAllActiveBrands;
	}

	/**
	 * @param listAllBrands
	 * @return List of all Valid Featured brands for which display flag is true
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<BrandVO> getActiveBrandsById(List<BrandVO> listAllBrands) {
		String methodName = "BrandsDroplet:getActiveBrands";
		logDebug(methodName + "[getActiveBrands method Start]");
		final List<BrandVO> listAllActiveBrands = new ArrayList<BrandVO>();
		BrandVO brandVONew = null;
		if (listAllBrands != null && listAllBrands.size() != 0) {
			for (BrandVO brandVO : listAllBrands) {

				try {
					brandVONew = null;
					brandVONew = this.getCatalogTools().getBrandDetails(brandVO.getFeaturedBrandURL(),
							SiteContextManager.getCurrentSiteId());

					boolean display_flag = this.getCatalogTools().getBrandDisplayFlag(brandVONew.getBrandName());

					if (display_flag) {
						WebApp pDefaultWebApp = null;
						UrlParameter[] pUrlParams = getBrandTemplate().cloneUrlParameters();
						pUrlParams[0].setValue(brandVONew.getBrandName());
						try {
							brandVONew.setSeoURL(getBrandTemplate().formatUrl(pUrlParams, pDefaultWebApp));
						} catch (ItemLinkException e) {
							logError("Exception occourred while creating SEO URL for the category : "
											+ brandVO.getBrandName(), e);
						}
						brandVONew.setBrandImage(brandVO.getBrandImage());
						brandVONew.setBrandImageAltText(brandVO.getBrandImageAltText());
						brandVONew.setBrandDesc(brandVO.getBrandDesc());
						listAllActiveBrands.add(brandVONew);
					}

				} catch (BBBBusinessException e) {
					logError("Error while Retrieving Brand from Repository for Brand ID: "
							+ brandVO.getFeaturedBrandURL(), e);
				} catch (BBBSystemException e) {
					logError("Error while Retrieving Brand from Repository for Brand ID: "
							+ brandVO.getFeaturedBrandURL(), e);
				}
			}
		} else {
			logDebug(methodName + "[getActiveBrands: listAllBrands is null or empty]");
		}
		logDebug(methodName + "[getActiveBrands method end]");
		return listAllActiveBrands;
	}

	/**
	 * This method gets the promotional content from Endeca for all pages to be
	 * rendered on.
	 *
	 * @param contentResults
	 * @return
	 */

	/*
	 * private Map<String,List<PromoVO>> getPromo(final ContentItem
	 * pContentItem){ return null; final Map<String,List<PromoVO>> promoMap =
	 * new HashMap<String,List<PromoVO>>(); final ContentItem contentItem =
	 * contentResults.getContent(); if(null!=contentItem){ final Iterator<?>
	 * iter = contentItem.iterator(); while (iter.hasNext()) { String
	 * cartridgeName; final String[] cartridgeNames =
	 * iter.next().toString().split(","); cartridgeName =
	 * cartridgeNames[0].replace("(Name: ", ""); for(String key :
	 * getCatridgeNameMap().keySet()) { if
	 * (cartridgeName.equalsIgnoreCase(getCatridgeNameMap().get(key))) {
	 * promoMap.put(key.toUpperCase(Locale.getDefault()),
	 * getQueryGenerator().getContentList(contentItem,cartridgeName)); } } } }
	 * return promoMap; }
	 */

	/**
	 * This is to retrieve the CatalogId for the particular Dimension name.
	 *
	 * @param parentName
	 * @param dimName
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@Override
	public String getCatalogId(final String parentName, final String dimName)
			throws BBBBusinessException, BBBSystemException {
		return getSearchUtil().getCatalogId(parentName, dimName);
	}

	

	/**
	 * @param argNav
	 * @param argQueryString
	 * @return
	 */
	/*
	 * @SuppressWarnings("rawtypes") private List<AutoSuggestVO>
	 * getAutoSuggestList(final Navigation argNav, final String argQueryString)
	 * { //TextSearchDetailList textSearchDetailList = null; List<AutoSuggestVO>
	 * pList = null; AutoSuggestVO autoSuggestVO = null; Map searchReportMap =
	 * null; ESearchReport searchReport = null; //ESearchReport.Mode mode =
	 * null; List autoSuggestions = null; ESearchAutoSuggestion autoSuggestion =
	 * null; ListIterator iterAutoSuggestions = null; List dymSuggestions =
	 * null; ESearchDYMSuggestion dymSuggestion = null; ListIterator
	 * iterDYMSuggestions = null; String searchKey = null; String searchTerms =
	 * null; String searchMode = null; String spellCorrection = null; String dym
	 * = null; //String removeHyperlink = null; //String dymHyperlink = null;
	 * Set keySet = null; Iterator iterKeySet = null; String key = null; pList =
	 * new ArrayList<AutoSuggestVO>(); searchReportMap =
	 * argNav.getESearchReports(); keySet = searchReportMap.keySet(); iterKeySet
	 * = keySet.iterator(); while (iterKeySet.hasNext()) { key = (String)
	 * iterKeySet.next(); searchReport = (ESearchReport)
	 * searchReportMap.get(key); searchKey = searchReport.getKey(); searchTerms
	 * = searchReport.getTerms(); //mode = searchReport.getMode(); //searchMode
	 * = this.getSearchModeString(mode); searchMode =
	 * getQueryGenerator().getSearchModeAll(); autoSuggestVO = new
	 * AutoSuggestVO(); autoSuggestVO.setSearchKey(searchKey);
	 * autoSuggestVO.setSearchMode(searchMode);
	 * autoSuggestVO.setSearchTerms(searchTerms); //removeHyperlink =
	 * getEndecaClient().createRemoveTextSearchLink(argQueryString);
	 * //autoSuggestVO.setRemoveHyperlink(removeHyperlink); autoSuggestions =
	 * searchReport.getAutoSuggestions(); iterAutoSuggestions =
	 * autoSuggestions.listIterator(); while (iterAutoSuggestions.hasNext()) {
	 * autoSuggestion = (ESearchAutoSuggestion) iterAutoSuggestions.next();
	 * spellCorrection = autoSuggestion.getTerms();
	 * autoSuggestVO.setSpellCorrection(spellCorrection);
	 * if(autoSuggestion.didSuggestionIncludeAutomaticPhrasing()){
	 * autoSuggestVO.setAutoPhrase(true); } } dymSuggestions =
	 * searchReport.getDYMSuggestions(); iterDYMSuggestions =
	 * dymSuggestions.listIterator(); while (iterDYMSuggestions.hasNext()) {
	 * dymSuggestion = (ESearchDYMSuggestion) iterDYMSuggestions.next(); dym =
	 * dymSuggestion.getTerms(); autoSuggestVO.setDymSuggestion(dym);
	 * if(dymSuggestion.didSuggestionIncludeAutomaticPhrasing()){
	 * autoSuggestVO.setAutoPhrase(true); } dymHyperlink =
	 * getEndecaClient().createDYMLink(dym, argQueryString);
	 * textSearchDetail.setDymHyperlink(dymHyperlink); }
	 * pList.add(autoSuggestVO); } return pList; }
	 */

	/**
	 * @param nav
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getRedirectUrl(final List<String> redirectURLs,final String pHostname) {

		if(redirectURLs.isEmpty()) {
			return null;
		}

		String redirectPage = null;
		boolean siteRedirExists = false;
		final ListIterator iterSupplements = redirectURLs.listIterator();
		while (iterSupplements.hasNext() && !siteRedirExists ) {
			final String redir= (String)iterSupplements.next();
			if(redir.startsWith("http") && null!=pHostname && redir.contains(pHostname)){
				redirectPage = redir;
				siteRedirExists = true;
			} else if(redir.startsWith("/")){
				redirectPage = redir;
			}
		}
		return redirectPage;
	}

	/**
	 * @return the endecaClient
	 */
	public EndecaClient getEndecaClient() {
		return this.endecaClient;
	}

	/**
	 * @param endecaClient
	 *            the endecaClient to set
	 */
	public void setEndecaClient(final EndecaClient endecaClient) {
		this.endecaClient = endecaClient;
	}

	/**
	 * @return the queryGenerator
	 */
	public EndecaQueryGenerator getQueryGenerator() {
		return this.queryGenerator;
	}

	/**
	 * @param queryGenerator
	 *            the queryGenerator to set
	 */
	public void setQueryGenerator(final EndecaQueryGenerator queryGenerator) {
		this.queryGenerator = queryGenerator;
	}

/*	*//**
	 * @return the brandDimName
			 */
	/*
	 * public Map<String, String> getDimensionMap() { Map<String,String>
	 * dimNonDisplayMap = null; Map<String,String> dimDisplayMap = null;
	 * Map<String,String> combinedDimensionMap = new HashMap<String, String>();
	 * try{ dimDisplayMap =
	 * getCatalogTools().getConfigValueByconfigType(getDimDisplayMapConfig());
	 * dimNonDisplayMap =
	 * getCatalogTools().getConfigValueByconfigType(getDimNonDisplayMapConfig())
	 * ; combinedDimensionMap.putAll(dimDisplayMap);
	 * combinedDimensionMap.putAll(dimNonDisplayMap); }catch
	 * (BBBBusinessException bbbbEx) {
	 * logError(BBBCoreErrorConstants.BROWSE_ERROR_1040+
	 * " BusinessException in default dimensions list  from getDimensionMap from EndecaSearch"
	 * ,bbbbEx); } catch (BBBSystemException bbbsEx) {
	 * logError(BBBCoreErrorConstants.BROWSE_ERROR_1041+
	 * " SystemException in default dimensions list  from getDimensionMap from EndecaSearch"
	 * ,bbbsEx); } return combinedDimensionMap; }
	*//**
		 * @param brandDimName
		 *            the brandDimName to set
*/
	/*
	 * public void setDimensionMap(final Map<String, String> dimensionMap) {
	 * this.dimensionMap = dimensionMap; }
	 */
/*	*//**
	 * @return siteConfig
			 */
	/*
	 * public Map<String, String> getDepartmentConfig() { return
	 * this.departmentConfig; }
	*//**
	 * @param siteConfig
	 *//*
		 * public void setDepartmentConfig(final Map<String, String>
		 * departmentConfig) { this.departmentConfig = departmentConfig; }
		 */

/*	*//**
	 * @return propertyMap
			 */
	/*
	 * public Map<String, String> getPropertyMap() { return this.propertyMap; }
	*//**
	 * @param propertyMap
		 */
	/*
	 * public void setPropertyMap(final Map<String, String> propertyMap) {
	 * this.propertyMap = propertyMap; }
	*//**
	 * @return siteConfig
		 */
	/*
	 * public Map<String, String> getSiteConfig() { return this.siteConfig; }
	*//**
	 * @param siteConfig
	 *//*
		 * public void setSiteConfig(final Map<String, String> siteConfig) {
		 * this.siteConfig = siteConfig; }
		 */

	/**
	 * @return the swatchInfoMap
	 */
	public Map<String, String> getSwatchInfoMap() {
		return this.swatchInfoMap;
	}

	/**
	 * @param swatchInfoMap
	 *            the swatchInfoMap to set
	 */
	public void setSwatchInfoMap(final Map<String, String> swatchInfoMap) {
		this.swatchInfoMap = swatchInfoMap;
	}

	public Map<String, String> getSortFieldMap() {
		Map<String, String> facetList = null;
		try {
			facetList = getCatalogTools().getSearchSortFieldMap();
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching Type Ahead Dimension Names", bbbsEx);
		}
		return facetList;
	}
	/*
	 * public void setSortFieldMap(final Map<String, String> sortFieldMap) {
	 * this.sortFieldMap = sortFieldMap; }
	 */

	public int getMaxCatTabs() {
		return this.maxCatTabs;
	}

	public void setMaxCatTabs(final int maxCatTabs) {
		this.maxCatTabs = maxCatTabs;
	}

	/*
	 * public Map<String, String> getCatridgeNameMap() { return
	 * this.catridgeNameMap; } public void setCatridgeNameMap(final Map<String,
	 * String> catridgeNameMap) { this.catridgeNameMap = catridgeNameMap; }
	 * public Map<String, String> getCatridgePropertyMap() { return
	 * this.catridgePropertyMap; } public void setCatridgePropertyMap(final
	 * Map<String, String> catridgePropertyMap) { this.catridgePropertyMap =
	 * catridgePropertyMap; }
	 */

	public int getMaxMatches() {
		return this.maxMatches;
	}

	public void setMaxMatches(final int maxMatches) {
		this.maxMatches = maxMatches;
	}

/*	*//**
	 * @return the attributePropmap
			 */
	/*
	 * public Map<String, String> getAttributePropmap() { return
	 * this.attributePropmap; }
	*//**
		 * @param attributePropmap
		 *            the attributePropmap to set
		 */
	/*
	 * public void setAttributePropmap(final Map<String, String>
	 * attributePropmap) { this.attributePropmap = attributePropmap; }
	*//**
	 * @return the placeHolder
		 */

	/*
	 * public String getPlaceHolder() { return this.placeHolder; }
	*//**
		 * @param placeHolder
		 *            the placeHolder to set
	 *//*
		 * public void setPlaceHolder(final String placeHolder) {
		 * this.placeHolder = placeHolder; }
		 */

	public List<String> getCatIdsToBeSuppressed(final String pSiteId) {
		List<String> mFacetList = new ArrayList<String>();
		List<String> facetList = null;
		try {
			facetList = getCatalogTools().getAllValuesForKey(getConfigUtil().getDimNonDisplayMapConfig(), pSiteId);
		} catch (BBBBusinessException bbbbEx) {
			logError("Business Exception occurred while fetching Type Ahead Dimension Names", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching Type Ahead Dimension Names", bbbsEx);
		}
		if(null != facetList){
			mFacetList = facetList;
		}
		return mFacetList;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return this.nodeType;
	}

	/**
	 * @param nodeType
	 *            the nodeType to set
	 */
	public void setNodeType(final String nodeType) {
		this.nodeType = nodeType;
	}

	public Map<String, String> getDimPropertyMap() {
		return this.dimPropertyMap;
	}

	public void setDimPropertyMap(final Map<String, String> dimPropertyMap) {
		this.dimPropertyMap = dimPropertyMap;
	}

	/**
	 * @return the siteIdMap
	 */
	public Map<String, String> getSiteIdMap() {
		return this.siteIdMap;
	}

	/**
	 * @param siteIdMap
	 *            the siteIdMap to set
	 */
	public void setSiteIdMap(final Map<String, String> siteIdMap) {
		this.siteIdMap = siteIdMap;
	}

	private synchronized DimensionList runQuery(final long nValue, final String siteId, final String boostParam)
			throws BBBSystemException {
        final ENEQuery query = new ENEQuery();
        final DimValIdList dvalIds = new DimValIdList();
        dvalIds.addDimValueId( nValue );
        ENEQueryResults eneRes = null;
        query.setNavRecordFilter(getConfigUtil().getSiteConfig().get(siteId));
        query.setNavDescriptors( dvalIds );

        // Change for only exposing Department(Ne=nValue) & Brand(Ne=8) Facet
	    //for Flyout to reduce unnecessary refinements processing.
	    //query.setNavAllRefinements( true );
        try {
			if(null != getCatalogTools().getAllValuesForKey("ContentCatalogKeys", siteId+"RootCategory")
					&& BBBUtility.isNotEmpty(getCatalogTools()
							.getAllValuesForKey("ContentCatalogKeys", siteId + "RootCategory").get(0))) {
				final DimValIdList pList = new DimValIdList();
				pList.addDimValueId(Long.parseLong(
						getCatalogTools().getAllValuesForKey("ContentCatalogKeys", siteId + "RootCategory").get(0)));
				pList.addDimValueId(8L);
				query.setNavExposedRefinements(pList);
			} else {
				// If root Category for a given site is not maintained in Config
				// Keys then expose all refinements.
				query.setNavAllRefinements( true );
			}
		} catch (BBBBusinessException e1) {
			query.setNavAllRefinements( true );
		}

	    if (!BBBUtility.isEmpty(boostParam)) {
			final StratifiedDimValList stratList = new StratifiedDimValList();
			final String[] parameter = boostParam.split(BBBCoreConstants.SEMICOLON);
			StratifiedDimVal stratDval = null;
			int count = 0;
			int boost = parameter.length;
			for (String param : parameter) {
				if (!BBBUtility.isEmpty(parameter)) {
					stratDval = new StratifiedDimVal(boost,Long.parseLong(param));
					stratList.add(count, stratDval);
					boost--;
					count++;
				}
			}
			query.setNavStratifiedDimVals(stratList);
		}
		try {
        	query.setNavNumERecs(0);
        	eneRes = getEndecaClient().getEneConnection().query(query);
       // 	eneRes.getNavigation().getERecs();
            DimensionList dims = eneRes.getNavigation().getDescriptorDimensions();
            if (dims.size() == 0) {
            	dims = eneRes.getNavigation().getCompleteDimensions();
            }

            return dims;
		} catch (ENEQueryException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"ENEQueryException in calling Endeca performSearch");
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"Endeca Exception",e);
		}
    }

	/**
	 * This method returns the category map. Here the filtering is also done in
	 * case of Global nav flyout droplet on the basis of L2/l3 exclusion list.
	 *
	 * @param configurableL2List
	 * @param configurableL3List
	 * @param dimensionID
	 * @param depth
	 * @param categoryParentMap
	 * @param categoryMap
	 * @param level
	 * @param firstLevelID
	 * @param categoryRefinementView
	 * @param siteId
	 * @param dimenId
	 * @param boostParam
	 * @param pSearchQuery
	 * @return categoryParentMap
	 */
	private Map<String,Map<String,CategoryParentVO>> getChildDimensions(final long dimensionID,final int depth,
			final Map<String,Map<String,CategoryParentVO>> categoryParentMap,
			final Map<String,CategoryParentVO> categoryMap,final String level,final String firstLevelID,
			final List<CategoryRefinementVO> categoryRefinementView, final String siteId, final long dimenId,
			final String boostParam,boolean isCheckPhantom) {
   	logDebug("EndecaSearch.getChildDimensions depth is " + depth + ", isCheckPhantom: "+isCheckPhantom);
		int tmpDepth = depth;
		String tmpBoostParam = boostParam;
		String tmpFirstLevelId = firstLevelID;
		String tmpLevel = level;
		Map<String,CategoryParentVO> tmpCategoryMap = categoryMap;
		List<CategoryRefinementVO> tmpCategoryRefinementVO = categoryRefinementView;
		tmpDepth++;
		try {
           DimensionList dimensions = null;
			if (tmpDepth == 2 && !BBBUtility.isEmpty(tmpBoostParam)) {
				dimensions = runQuery(dimensionID, siteId, tmpBoostParam);
				tmpBoostParam = null;
			} else {
				dimensions = runQuery(dimensionID, siteId, null);
			}

			for (int i = 0; i < dimensions.size(); i++) {
               final Dimension dimension = (Dimension)dimensions.get( i );
				if (dimenId == dimension.getId()) {
               	final DimValList dimVals = dimension.getRefinements();
					for (int j = 0; j < dimVals.size(); j++) {
	                	final DimVal dimVal = dimVals.getDimValue(j);
						// if(!(getCatIdsToBeSuppressed(siteId)).contains(String.valueOf(dimVal.getId())))
						// {
		                	if(categoryParentMap.containsKey(null)){
	                    		categoryParentMap.remove(null);
	                    	}
		                    if(tmpDepth == 1){
		                    	categoryParentMap.put(tmpFirstLevelId, tmpCategoryMap);
		                		tmpFirstLevelId = String.valueOf(dimVal.getId());
		                		tmpCategoryMap = new LinkedHashMap<String,CategoryParentVO>();
		                	}
		                    if(tmpDepth ==2){
		                    	final CategoryParentVO pCategoryParentVO = new CategoryParentVO();
		                    	pCategoryParentVO.setQuery(String.valueOf(dimVal.getId()));
		                    	pCategoryParentVO.setName(dimVal.getName());
		                    	//BBBSL-11385 | Filtering L2s on the basis of checkphantom 
		                    	if (!isCheckPhantom || !isPhantomCategory(String.valueOf(dimVal.getId())) ) {
		                    		tmpCategoryMap.put(String.valueOf(dimVal.getId()), pCategoryParentVO);
								}
		                    	//PS-63057 | Moving the temp level out of isPhantom check
		                    	tmpLevel = String.valueOf(dimVal.getId());
		                    	tmpCategoryRefinementVO = new ArrayList<CategoryRefinementVO>();
		                    }
		                    if(tmpDepth ==3){
		                    	final CategoryRefinementVO pCatRefinementVO = new CategoryRefinementVO();
		                    	pCatRefinementVO.setQuery(String.valueOf(dimVal.getId()));
		                    	pCatRefinementVO.setName(dimVal.getName());
		                    	//BBBSL-11385 | Filtering L3s on the basis of checkphantom 
		                    	if(isCheckPhantom){
									if(!isPhantomCategory(String.valueOf(dimVal.getId()))){
										tmpCategoryRefinementVO.add(pCatRefinementVO);
									}
									tmpCategoryMap.get(tmpLevel).setCategoryRefinement(tmpCategoryRefinementVO);
		                    	}
		                    	else{
		                    		tmpCategoryRefinementVO.add(pCatRefinementVO);
		                    		tmpCategoryMap.get(tmpLevel).setCategoryRefinement(tmpCategoryRefinementVO);
		                    	}
		                    }
		                    //printDim(dimVal, depth);
						// Added condition to disable call for L3 level category
						// as it is not required in Category Flyout.
		                    if(tmpDepth !=3){
							getChildDimensions(dimVal.getId(), tmpDepth, categoryParentMap, tmpCategoryMap, tmpLevel,
									tmpFirstLevelId, tmpCategoryRefinementVO, siteId, dimenId, tmpBoostParam, isCheckPhantom);
		                    }
	                	//}
	                }
               }
           }
           categoryParentMap.put(tmpFirstLevelId, tmpCategoryMap);
           return categoryParentMap;
		} catch (Exception e) {
       	logDebug(e.getLocalizedMessage());
       }
       return null;
   }

	
	/**
	 * 
	 * This method checks for a phantom category when category ID
	 * is passed to it
	 * @param categoryId
	 * @return boolean
	 */
	
	private final Boolean isPhantomCategory(String categoryId){
		CategoryVO category =null;
		logDebug("Start isPhantomCategory");
		boolean isPhantomCategory = false;
		try {
			category = getCatalogTools().getCategoryDetail(SiteContextManager.getCurrentSiteId(),categoryId , false);
			isPhantomCategory = (null != category && category.getPhantomCategory()==true);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error occured while determining isPhantom. " , e);
		}
		logDebug("CategoryId = " +category.getCategoryId()+ ", isPhantom = " + isPhantomCategory);
		logDebug("End isPhantomCategory");
		return isPhantomCategory;
	}
	
	
	/*
	 * This Method will return the list of start in which colleges are present
	 * from Endeca search
	 *
	 * @see com.bbb.search.ISearch#getStates(java.lang.String, java.lang.String)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<StateVO> getStates(final String catalogId, final String pSiteId)
			throws BBBBusinessException, BBBSystemException {

		logDebug( BBBCoreConstants.ENDECA_SEARCH_GET_STATES+":::Fetching the States from Endeca");
		final String methodName =  BBBCoreConstants.ENDECA_SEARCH_GET_STATES;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		DimensionList dimensions = null;
		Dimension dimension = null;
		ListIterator iterDimensions = null;
		List<StateVO> statesList = new ArrayList<StateVO>();
		DimValList dimVals = null;
		DimVal dimVal = null;
		ENEQueryResults results = null;
		/*
		 * ENEContentQuery contentQuery = null; ENEContentResults contentResults
		 * = null;
		 */
		String cacheKeyId=null;
		try {
			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+":::Preparing the Query String");
			String queryString = BBBEndecaConstants.NAVIGATION + "=" + catalogId + "+"
					+ (getCatalogId(BBBEndecaConstants.SITE_ID, getSiteIdMap().get(pSiteId)));
			final UrlGen urlGen = new UrlGen(queryString, getQueryGenerator().getEncoding());
			queryString = urlGen.toString();
			//this.getQueryGenerator().setQueryURL(queryString);

			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+":::cache key:" + urlGen.toString());
			cacheKeyId=urlGen.toString()+"_"+pSiteId+"_state";
			final String cacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME).get(0) + "state";
			int cacheTimeout = 0;
			try {
				cacheTimeout = Integer
						.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
								BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT).get(0));
			} catch (NumberFormatException e1) {
				logError("EndecaSearch.getStates || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e1);
				cacheTimeout = getKeywordCacheTimeout();
			} catch (NullPointerException e1) {
				logError("EndecaSearch.getStates || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e1);
				cacheTimeout = getKeywordCacheTimeout();
			}
			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+":::Fetching the records from Cache");
			statesList = (List<StateVO>) getObjectCache().get(cacheKeyId, cacheName);
			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+":::cache Name getStates:" + cacheName);
			if (statesList == null || statesList.isEmpty()) {
				logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+":::Fetching the Records from Endeca");
				statesList = new ArrayList<StateVO>();
				final ENEQuery queryObject = new UrlENEQuery(queryString, getQueryGenerator().getEncoding());
				queryObject.setNavAllRefinements(true);
				queryObject.setNavRecordFilter(getConfigUtil().getSiteConfig().get(pSiteId));

				results = getEndecaClient().executeQuery(queryObject);

				final Navigation nav = results.getNavigation();
				dimensions = nav.getRefinementDimensions();
				iterDimensions = dimensions.listIterator();
				final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();
				while (iterDimensions.hasNext()) {
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+" In while Loop");
					dimension = (Dimension) iterDimensions.next();
					final String parentStates = dimension.getName();
					if (BBBEndecaConstants.SCHOOL_STATE.equalsIgnoreCase(dimensionMap.get(parentStates))) {
						dimVals = dimension.getRefinements();
						final Iterator iterDimVals = dimVals.listIterator();
						while (iterDimVals.hasNext()) {
							dimVal = (DimVal) iterDimVals.next();
							final StateVO states = new StateVO();
							states.setStateName(dimVal.getName());
							states.setStateCode(String.valueOf(dimVal.getId()));
							if(null!=states.getStateName() && null!=states.getStateCode()){
							statesList.add(states);
							}
						}

					}
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+"::::: Existing While Loop");
				}
				if (!statesList.isEmpty()) {
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+"::: adding statesList to object cache");
					getObjectCache().put(cacheKeyId, statesList, cacheName, cacheTimeout);
				}
			}else{
				logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_STATES+"::::fetching results from cache object");
			}
			return statesList;
		} catch (ENEQueryException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"ENEQueryException in calling Endeca performSearch");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException("Endeca Exception", e);
		} /*
			 * catch (InitializationException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1002,
			 * "InitializationException in calling Endeca performSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new BBBSystemException(
			 * "Endeca Exception", e); } catch (InvalidQueryException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1003,
			 * "InvalidQueryException in calling Endeca performSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new BBBSystemException(
			 * "Endeca Exception", e); } catch (ContentException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1004,
			 * "ContentException in calling Endeca performSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new BBBSystemException(
			 * "Endeca Exception", e); }
			 */finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}

	}

	/*
	 * This will return list of collegesVO from Endeca if '0' is passed as
	 * catalogid and list of collegesVOs of particular state if catalogId is
	 * stateCode (non-Java doc)
	 *
	 * @see com.bbb.search.ISearch#getColleges(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<CollegeVO> getColleges(final String catalogId, final String pSiteId)
			throws BBBBusinessException, BBBSystemException {

		logDebug("getColleges()::::Starting Fetching Colleges List From Endeca Search Manager");
		final String methodName =  BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		DimensionList dimensions = null;
		Dimension dimension = null;
		ListIterator iterDimensions = null;
		List<CollegeVO> collegeList = null;
		CollegeVO college = null;
		DimValList dimVals = null;
		DimVal dimVal = null;
		String parentCollegeName = null;
		ENEQueryResults results = null;
		/*
		 * ENEContentQuery contentQuery = null; ENEContentResults contentResults
		 * = null;
		 */
		String cacheKeyId=null;
		try {

			String queryString = BBBEndecaConstants.NAVIGATION + "=" + catalogId + "+"
					+ (getCatalogId(BBBEndecaConstants.SITE_ID, getSiteIdMap().get(pSiteId)));
			final UrlGen urlGen = new UrlGen(queryString, getQueryGenerator().getEncoding());
			queryString = urlGen.toString();
			//this.getQueryGenerator().setQueryURL(queryString);

			cacheKeyId = urlGen.toString()+"_"+pSiteId+"_College_"+catalogId;

			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES+":::cache key:" + cacheKeyId);
			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES+":::fetching the search result from cache");
			final String cacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME).get(0);
			int cacheTimeout = 0;
			try {
				cacheTimeout = Integer
						.parseInt(getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
								BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT).get(0));
			} catch (NumberFormatException e1) {
				logError("EndecaSearch.getColleges || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e1);
				cacheTimeout = getKeywordCacheTimeout();
			} catch (NullPointerException e1) {
				logError("EndecaSearch.getColleges || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
						+ BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e1);
				cacheTimeout = getKeywordCacheTimeout();
			}
			collegeList = (List<CollegeVO>) getObjectCache().get(cacheKeyId, cacheName);
			logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES+":::cache Name getColleges:" + cacheName);
			if (collegeList == null || collegeList.isEmpty()) {
				logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES
						+ ":::Fetching the colleges list from Endeca Search");
				final ENEQuery queryObject = new UrlENEQuery(queryString, getQueryGenerator().getEncoding());
				queryObject.setNavAllRefinements(true);
				queryObject.setNavRecordFilter(getConfigUtil().getSiteConfig().get(pSiteId));

				results = getEndecaClient().executeQuery(queryObject);

				// this is not required to be retrieved using content assembler
				// query
				/*
				 * ENEContentManager contentManager = new ENEContentManager();
				 * contentManager = new ENEContentManager(); contentQuery =
				 * (ENEContentQuery) contentManager.createQuery();
				 * contentQuery.setRuleZone(NAV_PAGE_ZONE);
				 * contentQuery.setENEQuery(queryObject); contentResults =
				 * getEndecaClient().executeQuery(contentQuery); results =
				 * contentResults.getENEQueryResults();
				 */

				final Navigation nav = results.getNavigation();

				logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES
						+ ":::retrieve refinement value for the current navigation state");
				dimensions = nav.getRefinementDimensions();
				logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES+":::Filter Records");
				iterDimensions = dimensions.listIterator();

				final Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();
				while (iterDimensions.hasNext()) {
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES
							+ ":::While Loop::Iterating the iterDimensions");

					dimension = (Dimension) iterDimensions.next();

					parentCollegeName = dimension.getName();
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES
							+ ":::Checking for the parent category Id equals to current navigation state");
					if (BBBEndecaConstants.SCHOOL_NAME.equalsIgnoreCase(dimensionMap.get(parentCollegeName))) {
						collegeList = new ArrayList<CollegeVO>();
						dimVals = dimension.getRefinements();
						final Iterator iterDimVals = dimVals.listIterator();
						logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES+":::Iterating for Child dimensions");
						while (iterDimVals.hasNext()) {
							dimVal = (DimVal) iterDimVals.next();
							college = new CollegeVO();
							college.setCollegeName(dimVal.getName());

							college.setCollegeId((Long.valueOf(dimVal.getId())).toString());
							final PropertyMap dimValuePropertyMap = dimVal.getProperties();
							if (null != (String) dimValuePropertyMap
									.get(getDimPropertyMap().get(BBBEndecaConstants.COLLEGE_LOGO))) {
								college.setCollegeLogo((String) dimValuePropertyMap
										.get(getDimPropertyMap().get(BBBEndecaConstants.COLLEGE_LOGO)));
							}
							collegeList.add(college);
						}
					}

				}
				if (null == collegeList || collegeList.isEmpty()) {
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES
							+ ":::If Condition:::: Fetching the Records from Navigation for College");
					ERecList records = null;
					ERec record = null;
					ListIterator iterRecords = null;
					PropertyMap properties = null;
					records = nav.getERecs();
					iterRecords = records.listIterator();

					if (iterRecords.hasNext()) {
						collegeList = new ArrayList<CollegeVO>();
						record = (ERec) iterRecords.next();
						properties = record.getProperties();
						college = new CollegeVO();
						college.setCollegeLogo(
								(String) properties.get(getConfigUtil().getPropertyMap().get("PRODUCT_COLLEGE_LOGO")));
						college.setCollegeName(
								(String) properties.get(getConfigUtil().getPropertyMap().get("PRODUCT_COLLEGE_NAME")));
						if(null != college.getCollegeLogo()&& null != college.getCollegeName()){
							collegeList.add(college);
						}
					}
				}
				if (collegeList != null  && !collegeList.isEmpty()) {
					logDebug(BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES+":::Adding collegeList to objech Cache");
					getObjectCache().put(cacheKeyId, collegeList, cacheName, cacheTimeout);
				}
			}else{
				logDebug(
						BBBCoreConstants.ENDECA_SEARCH_GET_COLLEGES + ":::Search result are fetched from object cache");
			}
			return collegeList;
		} catch (ENEQueryException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"ENEQueryException in calling Endeca performSearch");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
			throw new BBBSystemException("Endeca Exception", e);
		} /*
			 * catch (InitializationException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1002,
			 * "InitializationException in calling Endeca performSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new BBBSystemException(
			 * "Endeca Exception", e); } catch (InvalidQueryException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1003,
			 * "InvalidQueryException in calling Endeca performSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new BBBSystemException(
			 * "Endeca Exception", e); } catch (ContentException e) {
			 * BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1004,
			 * "ContentException in calling Endeca performSearch");
			 * BBBPerformanceMonitor.end(BBBPerformanceConstants.
			 * SEARCH_INTEGRATION, methodName); throw new BBBSystemException(
			 * "Endeca Exception", e); }
			 */finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		}
	}

	/*
	 * START R2.1 Added Site specific Attribute Values List to be shown Scope
	 * Item #213
	 */

	/**
	 * This method is used to get popular items results.
	 *
	 * @param pBccQuery
	 *            - Search Query entered in BCC
	 * @param pSiteId
	 *            - site id
	 * @param pResultSie
	 *            - size of the endeca results
	 * @return List<ProductVO> - product vo Object
	 *
	 */
	public List<ProductVO> getPopularItemsResults(List<ProductVO> pProductList , String pBccQuery , String pSiteId){

		BBBPerformanceMonitor.start(EndecaSearch.class.getName() + " : "
				+ "getPopularItemsResults(pProductList , pBccQuery , pSiteId, pResultSize)");

		logDebug("Enter.EndecaSearch.getPopularItemsResults(pProductList , pBccQuery , pSiteId, pResultSize)");

		int cacheTimeout = 0;
		int resultsSize = 0;
		try {
			resultsSize = Integer.parseInt(getCatalogTools()
					.getContentCatalogConfigration(BBBCoreConstants.REG_POPULAR_ITEMS_MAX_COUNT).get(0));
		} catch (NumberFormatException e) {
			logError("EndecaSearch.performSearch || NumberFormatException occured in "
					+ BBBCoreConstants.REG_POPULAR_ITEMS_MAX_COUNT, e);
			resultsSize = 50;
		} catch (NullPointerException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in "
					+ BBBCoreConstants.REG_POPULAR_ITEMS_MAX_COUNT, e);
			resultsSize = 50;
		} catch (BBBSystemException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in "
					+ BBBCoreConstants.REG_POPULAR_ITEMS_MAX_COUNT, e);
			resultsSize = 50;
		} catch (BBBBusinessException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in "
					+ BBBCoreConstants.REG_POPULAR_ITEMS_MAX_COUNT, e);
			resultsSize = 50;
		}
		String cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
				BBBCoreConstants.REG_POPULAR_ITEMS_CACHE_NAME);
		cacheTimeout = getSearchResultCacheTimout(cacheName);
		logDebug("cacheName:" + cacheName);

		List<ProductVO> productList = new ArrayList<ProductVO>();

		try {
			//update pBccQuery with Site_ID dimension
			String siteDimId = this.getCatalogId(BBBEndecaConstants.SITE_ID,getSiteIdMap().get(pSiteId));
			if(!pBccQuery.contains(siteDimId)) {
				try {
					BasicUrlFormatter urlFormatter = new BasicUrlFormatter();
					UrlState urlState = urlFormatter.parseRequest(pBccQuery, URL_ENCODING);
					String nValue = urlState.getParam(BBBEndecaConstants.NAVIGATION);
					if(!BBBUtility.isEmpty(nValue)) {
						nValue += " "+siteDimId;
					} else {
						nValue = siteDimId;
					}

					urlState.setParam(BBBEndecaConstants.NAVIGATION, nValue);
					pBccQuery = urlState.toString().substring(1);
				} catch(UrlFormatException urlFormatExe) {
					logDebug(urlFormatExe.getMessage());
				}
			}

			ENEQuery queryObject = new UrlENEQuery(pBccQuery, getQueryGenerator().getEncoding());

			//number of products to return
			queryObject.setNavNumERecs(resultsSize);
			//avoid returning refinements for the search results
			queryObject.setNavAllRefinements(false);

			productList = (List<ProductVO> ) getObjectCache().get(pBccQuery+resultsSize, cacheName);
			if (productList == null) { // Actual Query Call to Endeca to obtain
										// ENEQueryResults Object in case result
										// not found in cache

				SearchQuery tempSearchQuery = new SearchQuery();
				tempSearchQuery.setQueryURL(pBccQuery);
				tempSearchQuery.setPageSize(""+resultsSize);
				tempSearchQuery.setSiteId(pSiteId);

				productList = performPopularItemsEndecaSearch(pProductList, tempSearchQuery,
						(pBccQuery + BBBEndecaConstants.PARAM_PAGESIZE + "=" + resultsSize));
				getObjectCache().put(pBccQuery+resultsSize, productList, cacheName, cacheTimeout);
			} else {
				logDebug("Search result fetched from cache");
			}

		} catch (UrlENEQueryParseException urlEncodingExe) {
			logError(urlEncodingExe.getMessage());
		} catch (BBBSystemException bbbSystemException) {
			logError(bbbSystemException.getMessage());
		} catch (BBBBusinessException bbbBusinessException) {
			logError(bbbBusinessException.getMessage());
		}
		logDebug("Exit.EndecaSearch.getPopularItemsResults(pProductList , pBccQuery , pSiteId, pResultSize)");
		BBBPerformanceMonitor.end(EndecaSearch.class.getName() + " : "
				+ "getPopularItemsResults(pProductList , pBccQuery , pSiteId, pResultSize)");
		return productList;
	}

	private List<ProductVO> performPopularItemsEndecaSearch(List<ProductVO> pProductList, SearchQuery pSearchQuery,
			String queryString) throws BBBSystemException, BBBBusinessException {

		List<ProductVO> productList = new ArrayList<ProductVO>();

		try {
			// no need to pass SearchQuery as this is temporary object created
			// and will not be complete
			ContentItem responseContentItem = executeAssemblerQuery(pSearchQuery, queryString,
					getContentPathResolver().resolveContentCollectionPath(pSearchQuery));

			if(responseContentItem.get(BBBEndecaConstants.CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO) != null) {
				SearchResults searchResults = (SearchResults) responseContentItem
						.get(BBBEndecaConstants.CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO);

				if(null != searchResults && null != searchResults.getBbbProducts()) {
					for(BBBProduct bbbProduct : searchResults.getBbbProducts().getBBBProducts()) {
						productList.add(convertBbbProductToProductVO(bbbProduct));
					}
				}
			}

		} catch (AssemblerException assemblerException) {
			if(isLoggingError()){
				logError(assemblerException.getMessage());
			}
		}
		return productList;
	}

	/**
	 * Util method for converting bbbProduct to ProductVO Pre 11.x code is
	 * converting ERec retrieved from ContentResponse to ProductVO. In
	 * BBBProduct - BrandId, SmallImage, LargeImage, ThumbnailImage & ShortDesc
	 * are missing These are not used in current JSP code
	 *
	 * @param bbbProduct
	 * @return
	 */
	private ProductVO convertBbbProductToProductVO(BBBProduct bbbProduct) {
		//convert to ProductVO from BBBProduct

		ProductVO prodVo  = new ProductVO();

		if(!BBBUtility.isEmpty(bbbProduct.getProductID())) {
			prodVo.setProductId(bbbProduct.getProductID());
			prodVo.setName(bbbProduct.getProductName());
			prodVo.setSeoUrl(bbbProduct.getSeoUrl());
			//prodVo.setBrandId(bbbProduct.get);
			ImageVO imgVo = new ImageVO();
				//imgVo.setSmallImage(bbbProduct.getIm);
				imgVo.setMediumImage(bbbProduct.getImageURL());
				//imgVo.setLargeImage(bbbProduct.getIm);
				//imgVo.setThumbnailImage(bbbProduct.getIm);
				prodVo.setProductImages(imgVo);
			//prodVo.setShortDescription(bbbProduct.getSho);
			prodVo.setCollection(Boolean.parseBoolean(bbbProduct.getCollectionFlag()));

			KickStarterPriceVO kickStarterVO = new KickStarterPriceVO();
			// Setting Token in Description to Replace These Values with
			// Localized Prices Used in Kickstarter For Mobile
			kickStarterVO.setPriceRangeToken(bbbProduct.getPriceRangeToken());
			kickStarterVO.setKickStrtPriceRangeDescrip(bbbProduct.getPriceRange());
			kickStarterVO.setLowPrice(bbbProduct.getLowPrice());
			kickStarterVO.setHighPrice(bbbProduct.getHighPrice());
			kickStarterVO.setWasHighPrice(bbbProduct.getWasHighPrice());
			kickStarterVO.setWasLowPrice(bbbProduct.getWasLowPrice());
			kickStarterVO.setKickStrtWasPriceRangeDescrip(bbbProduct.getWasPriceRange());

			kickStarterVO.setLowPriceMX(bbbProduct.getLowPriceMX());
			kickStarterVO.setHighPriceMX(bbbProduct.getHighPriceMX());

			kickStarterVO.setWasHighPriceMX(bbbProduct.getWasHighPriceMX());
			kickStarterVO.setWasLowPriceMX(bbbProduct.getWasLowPriceMX());

			kickStarterVO.setKickStrtWasPriceRangeDescripMX(bbbProduct.getWasPriceRangeMX());
			kickStarterVO.setKickStrtPriceRangeDescripMX(bbbProduct.getPriceRangeMX());
			kickStarterVO.setShipMsgFlag(bbbProduct.isShipMsgFlag());
			kickStarterVO.setDisplayShipMsg(bbbProduct.getDisplayShipMsg());
			prodVo.setKickStarterPrice(kickStarterVO);

			prodVo.setIntlRestricted(bbbProduct.isIntlRestricted());
			// Set to use in cart and dynamic price changes : BBBH-3489
			prodVo.setBbbProduct(bbbProduct);
			BazaarVoiceProductVO bvVO = new BazaarVoiceProductVO();
			if(!BBBUtility.isEmpty(bbbProduct.getReviews())) {
				int reviews1 = Integer.parseInt(bbbProduct.getReviews());
				bvVO.setTotalReviewCount(reviews1);
			}

			if(!BBBUtility.isEmpty(bbbProduct.getRatings())) {
				bvVO.setRatingsOnlyReviewCount(bbbProduct.getRatings());
//				bvVO.setOverallRatingRange(Integer.parseInt(bbbProduct.getRatings()));
				bvVO.setOverallRatingRange(bbbProduct.getRatings());
				bvVO.setRatingAvailable(true);
			}
			prodVo.setBvReviews(bvVO);
		}

		if(null != bbbProduct.getSkuSet() && !bbbProduct.getSkuSet().isEmpty()) {
			List<String> propertyNames = new ArrayList<String>();
			for(String skuId : bbbProduct.getSkuSet().keySet()) {
				propertyNames.add(skuId);
			}
			prodVo.setChildSKUs(propertyNames);
		}

		SortedMap<String,List<AttributeVO>> attr = null;
		attr =new TreeMap<String,List<AttributeVO>>();
		if(null != bbbProduct.getAttributeVO() && !bbbProduct.getAttributeVO().isEmpty()){
			attr.put(BBBSearchConstants.ATTRIBUTES,bbbProduct.getAttributeVO());
			prodVo.setAttributesList(attr);
		}

		return prodVo;
	}

	/**
	 * This method will use to prepared the product VO
	 *
	 * @param pERec
	 *            - ENE query result object
 * @return ProductVO - this method will return product vo
 */

public	ProductVO prePareProductVo(ERec pERec ){
		ProductVO prodVo  = new ProductVO();
		ERec eRec = pERec;
		PropertyMap pMap = eRec.getProperties();
		Map<String, String> propertyMap = getConfigUtil().getPropertyMap();
		if (propertyMap.get(BBBSearchConstants.RECORDTYPE) != null
				&& pMap.get(propertyMap.get(BBBSearchConstants.RECORDTYPE)).equals(BBBSearchConstants.PRODUCT)) {
			prodVo.setProductId((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_ID)));
			prodVo.setName((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_TITLE)));
			prodVo.setSeoUrl((String)pMap.get(propertyMap.get(BBBSearchConstants.SEO_URL)));
			prodVo.setBrandId((String)pMap.get(propertyMap.get(BBBSearchConstants.BRAND)));
			ImageVO imgVo = new ImageVO();
				imgVo.setSmallImage((String)pMap.get(getImagePropertyMap().get(BBBSearchConstants.SMALL_IMAGE_ID)));
				imgVo.setMediumImage((String)pMap.get(getImagePropertyMap().get(BBBSearchConstants.MEDIUM_IMAGE_ID)));
				imgVo.setLargeImage((String)pMap.get(getImagePropertyMap().get(BBBSearchConstants.LARGE_IMAGE_ID)));
			imgVo.setThumbnailImage(
					(String) pMap.get(getImagePropertyMap().get(BBBSearchConstants.THUMBNAIL_IMAGE_ID)));
				prodVo.setProductImages(imgVo);
			prodVo.setShortDescription((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_DESC)));
			String isCollection = (String) pMap.get(propertyMap.get(BBBSearchConstants.ISCOLLECTION));
			if(isCollection!=null && !isCollection.trim().equals("")){
				prodVo.setCollection(Boolean.parseBoolean(isCollection));
			}

			KickStarterPriceVO kickStarterVO = new KickStarterPriceVO();
			// Setting Token in Description to Replace These Values with
			// Localized Prices Used in Kickstarter For Mobile
			kickStarterVO.setPriceRangeToken(
					(String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_PRICE_RANGE_TOKEN)));
			kickStarterVO.setKickStrtPriceRangeDescrip(
					(String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_PRICE_RANGE)));
			kickStarterVO.setLowPrice((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_LOW_PRICE)));
			kickStarterVO.setHighPrice((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_HIGH_PRICE)));
			kickStarterVO
					.setWasHighPrice((String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_WAS_HIGH_PRICE)));
			kickStarterVO.setWasLowPrice((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_WAS_LOW_PRICE)));
			kickStarterVO.setKickStrtWasPriceRangeDescrip(
					(String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_WAS_PRICE_RANGE)));

			kickStarterVO.setLowPriceMX((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_LOW_PRICE_MX)));
			kickStarterVO.setHighPriceMX((String)pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_HIGH_PRICE_MX)));

			kickStarterVO.setWasHighPriceMX(
					(String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_WAS_HIGH_PRICE_MX)));
			kickStarterVO
					.setWasLowPriceMX((String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_WAS_LOW_PRICE_MX)));

			kickStarterVO.setKickStrtWasPriceRangeDescripMX(
					(String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_WAS_PRICE_RANGE_MX)));
			kickStarterVO.setKickStrtPriceRangeDescripMX(
					(String) pMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_PRICE_RANGE_MX)));
			prodVo.setKickStarterPrice(kickStarterVO);

			prodVo.setIntlRestricted(BBBCoreConstants.YES_CHAR
					.equalsIgnoreCase((String) pMap.get(propertyMap.get(BBBSearchConstants.IS_INTL_RESTRICTED))));

			BazaarVoiceProductVO bvVO = new BazaarVoiceProductVO();
			String ratings = (String)pMap.get((String)pMap.get(propertyMap.get(BBBSearchConstants.NUM_RATINGS)));
			String reviews = (String)pMap.get((String)pMap.get(propertyMap.get(BBBSearchConstants.REVIEWS)));
			if(reviews!=null && !reviews.trim().equals("")){
				int reviews1 = Integer.parseInt(reviews);
				bvVO.setTotalReviewCount(reviews1);
			}

			if(ratings!=null && !ratings.trim().equals("")){
			bvVO.setRatingsOnlyReviewCount(ratings);
//			bvVO.setOverallRatingRange(Integer.parseInt(ratings));
			bvVO.setOverallRatingRange(ratings);
			bvVO.setRatingAvailable(true);
			}
			prodVo.setBvReviews(bvVO);
			}
			if(pMap.get((String)pMap.get(propertyMap.get(BBBSearchConstants.CHILD_PRODUCT)))!=null){
			JSONObject jsonObject = (JSONObject) JSONSerializer
					.toJSON(pMap.get(propertyMap.get(BBBSearchConstants.CHILD_PRODUCT)).toString());
        		final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
        		DynaClass dynaClass = jSONResultbean.getDynaClass();
        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
        		List<String> propertyNames = new ArrayList<String>();
        		for (int i = 0; i < dynaProp.length; i++) {
        			String name = dynaProp[i].getName();
        			propertyNames.add(name);
        		}
        		prodVo.setChildSKUs(propertyNames);
			}
        	 if(propertyMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_ATTR))!=null){
        		 	Map<String, String> attributePropMap = getConfigUtil().getAttributePropmap();
			JSONObject jsonObject = (JSONObject) JSONSerializer
					.toJSON(propertyMap.get(propertyMap.get(BBBSearchConstants.PRODUCT_ATTR)).toString());
	            	final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
	        		DynaClass dynaClass = jSONResultbean.getDynaClass();
	        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
	        		List<String> propertyNames = new ArrayList<String>();
	        		for (int i = 0; i < dynaProp.length; i++) {
	        			String name = dynaProp[i].getName();
	        			propertyNames.add(name);
	        		}
	        		SortedMap<String,List<AttributeVO>> attr = null;
	        		attr =new TreeMap<String,List<AttributeVO>>();
	        		List<AttributeVO> attributeList = new ArrayList<AttributeVO>();
	        		if(propertyNames.contains(attributePropMap.get("PRIORITY"))
	        				&& propertyNames.contains(attributePropMap.get("PLACEHOLDER"))
	        				&& propertyNames.contains(attributePropMap.get("DISPDESC"))
	        				&& propertyNames.contains(attributePropMap.get("SKUATTRIBUTE_ID"))){
	        			String placeholder = (String) jSONResultbean.get(attributePropMap.get(BBBSearchConstants.PLACE_HOLDER));
	        			 AttributeVO attVo = new AttributeVO();
	        			 String priority = attributePropMap.get(BBBSearchConstants.PRIORITY);
	        			 attVo.setPriority((Integer.parseInt(priority)));
	        			 attVo.setPlaceHolder(placeholder);
	        			 attVo.setAttributeDescrip(attributePropMap.get(BBBSearchConstants.DISPLAY_DESCRIP));
	        			 attVo.setSkuAttributeId(attributePropMap.get(BBBSearchConstants.SKU_ATTRIBUTE_ID));
	        			 attributeList.add(attVo);
	        		}
	        		if(attributeList!=null && !attributeList.isEmpty()){
	        			attr.put(BBBSearchConstants.ATTRIBUTES,attributeList);
	        			prodVo.setAttributesList(attr);
	        	}
        	 }
	return prodVo;
	}

	/**
	 * This is to sanitize the Attribute Values and removing HTML mark ups if
	 * any.. Return null if nothing is configured in this Property at site
	 * level.
	 *
	 * @param pSearchQuery
	 * @return List<String>
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private List<String> sanitizedAttributeValues(final String pSiteId)
			throws BBBBusinessException, BBBSystemException {

		logDebug("Entering EndecaSearch.sanitizedAttributeValues method.");

		final List<String> pList =getCatalogTools().siteAttributeValues(pSiteId);
		final List<String> pSanitizedList = new ArrayList<String>();
		if(null != pList){
			for(String pAttribute : pList){
				if(pAttribute.indexOf(">") == -1){
					pSanitizedList.add(pAttribute.toLowerCase());
				} else {
					pSanitizedList.add((pAttribute.substring(pAttribute.indexOf(">") + 1, pAttribute.indexOf("</")))
							.toLowerCase());
				}
				}
			}
		logDebug("Exit EndecaSearch.sanitizedAttributeValues method.");
		return pSanitizedList;
	}
	/*
	 * END R2.1 Added Site specific Attribute Values List to be shown Scope Item
	 * #213
	 */

	//92F starts
			
			private List<String> configStringSplit(List<String> userInputArray){
				logDebug("entered configStringSplit()");
				 List<String> subStringOne=new ArrayList<String>();
				 int outerLoopLimit = userInputArray.size()>3?4:3;
		          for(int oloop=0;oloop<outerLoopLimit;oloop++){
		        	  subStringOne.add("");
		              for(int iloop=0;iloop<userInputArray.size();iloop++){
		                 if(!(iloop==oloop || oloop>userInputArray.size()-1))
		                	 if(!BBBUtility.isEmpty(userInputArray.get(iloop))){
		                		 subStringOne.set(oloop,subStringOne.get(oloop).concat(userInputArray.get(iloop).trim()+" "));
		                	 }
		                 }
		          }
		          for (String pMatchStrings : subStringOne) {
		                 logDebug("The substrings for partial match are:"+pMatchStrings);
		          }
		         return subStringOne;
			}

			private String removeStopWords(String enteredSearchTerm){
				logDebug("Removing STOP words from String : "+enteredSearchTerm.toLowerCase());
		List<String> userInput = new ArrayList<String>(
				Arrays.asList(enteredSearchTerm.trim().toLowerCase().split(" ")));
				List<String> configStopWordList;
				try {
					//stopwordlist should be in lower case
			configStopWordList = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
					BBBCmsConstants.STOP_WORD_LIST);
					logDebug("StopWrdsList : "+configStopWordList.size());
					if(configStopWordList != null && configStopWordList.size() == 1){
						enteredSearchTerm="";
				List<String> stopWordList = new ArrayList<String>(
						Arrays.asList(configStopWordList.get(0).trim().toLowerCase().split(",")));
						try{
							userInput.removeAll(stopWordList);
							for(String searchTerm: userInput)
								enteredSearchTerm+=searchTerm+" ";
							logDebug("After removing stop Word : "+enteredSearchTerm);
						} catch (Exception e) {
							logDebug("Error with the stopWords");
				        	logDebug(e.getLocalizedMessage());
				        }
					} else {
						return enteredSearchTerm;
					}

				} catch (BBBSystemException e) {
					logError("EndecaSearch.removeStopWords || BBBSystemException occured "+ e.getMessage());
				} catch (BBBBusinessException e) {
					logError("EndecaSearch.removeStopWords || BBBBusinessException occured "+ e.getMessage());
				}
				return enteredSearchTerm;
			}
			//92F ends

/*			*//**
					 * will return collection of facets configured the current
					 * site from configure repository
					 *
			 * @return
			 *//*
					 * @SuppressWarnings("unchecked") public Collection<String>
					 * getFacets(String currentSiteId) { String key =
					 * DIM_DISPLAY_VALUE+BBBUtility.getChannel()+
					 * BBBCoreConstants.UNDERSCORE+currentSiteId;
					 * Collection<String> facets=(Collection<String>)
					 * getConfigCacheContainer().get(key); if(facets==null){ try
					 * { Map<String, String> dimDisplayMap = getCatalogTools()
					 * .getConfigValueByconfigType(getConfigUtil().
					 * getDimDisplayMapConfig()); if
					 * (!dimDisplayMap.values().isEmpty()){
					 * facets=dimDisplayMap.values();
					 * getConfigCacheContainer().put(key,facets); } } catch
					 * (BBBBusinessException bbbbEx) {
					 * logError(BBBCoreErrorConstants.BROWSE_ERROR_1014+
					 * " Business Exception occurred while fetching dimensions display list from  getFacets from SearchDroplet"
					 * ,bbbbEx); } catch (BBBSystemException bbbsEx) {
					 * logError(BBBCoreErrorConstants.BROWSE_ERROR_1015+
					 * " System Exception occurred while fetching dimensions display list from  getFacets from SearchDroplet"
					 * ,bbbsEx); } } if(isLoggingDebug()){ logDebug("facets: "
					 * +facets); } return facets; }
					 */
	

	/**
	 * PSI 6 || BPS-798 || Implemente department as tree structure|| Changes
	 * Start Api added for making two aditonal calls for fecthing the l2 and l3
	 * departments for department tree structure. This method will update
	 * (facetRefinementForL1orL2) Category with list of sub-departments based on
	 * the department(L1 or L2) and returns L2 category Id. This method will
	 * make an endeca call to fetch L3's and its ancester category for the L3
	 * departments and update FacetRefinementVO and resturn the ancestor
	 * category. if the ancestor is null, call is for l1 Sub departments
	 *
	 * @param categoryId
	 * @param neDimID
	 * @param pSearchQuery
	 * @param facetRefinementForL1orL2
	 * @param eqlFilterExpression
	 * @return
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public String getDepartmentsForTreeNavigation(final String categoryId, final String neDimID,
																	final SearchQuery pSearchQuery,FacetRefinementVO facetRefinementForL1orL2,
			final String eqlFilterExpression) throws BBBSystemException {
		long startTime = System.currentTimeMillis();
		String ancesterCategoryForL2=null;
		String siteDimensionId = null;
		String productRecTypeDimID = null;
		List<FacetRefinementVO> facetRefineList = null;
		FacetRefinementVO facetRefinements = null;
		FacetRefinementVO updatedfacetRefinementL2inL1 = null;
		DimVal childDimVal;
		logDebug("Entering EndecaSearch.getDepartmentsForTreeNavigation method. categoryId: " + categoryId
				+ " neDimID: " + neDimID);

		final String methodName = "getDepartmentsForTreeNavigation";

		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		try {
			siteDimensionId = getCatalogId(BBBEndecaConstants.SITE_ID, getSiteIdMap().get(pSearchQuery.getSiteId()));
			productRecTypeDimID = this.getCatalogId(BBBEndecaConstants.RECORD_TYPE,
					BBBEndecaConstants.PROD_RECORD_TYPE);
			String navigationUrl=siteDimensionId + " " + productRecTypeDimID;
			String l2NaviagationUrl=navigationUrl+" "+categoryId;
			facetRefinementForL1orL2.setFacetRefFilter(l2NaviagationUrl.replaceAll(" ", "-"));
			final UrlGen secondUrlGen = new UrlGen(null, getEndecaClient().getEncoding());
			// preparing the query parameters for the 2nd query
			if (!BBBUtility.isEmpty(categoryId)) {
				secondUrlGen.addParam(BBBEndecaConstants.NAVIGATION, categoryId + " " + navigationUrl);
				secondUrlGen.addParam(BBBEndecaConstants.NAV_REFINEMENT, neDimID);
				if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
				secondUrlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
				}else{
				secondUrlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getQueryGenerator().getSearchMode());
				}
				secondUrlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPC, String.valueOf(1));
				secondUrlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPR, String.valueOf(1));
				secondUrlGen.addParam(BBBEndecaConstants.NAV_KEYWORD,
						getQueryGenerator().replaceSpecialCharacters(pSearchQuery.getKeyWord()));
				secondUrlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, BBBEndecaConstants.ALL);

				if(pSearchQuery != null && pSearchQuery.isEPHFound() && BBBCoreConstants.FILTER_VALUE.equalsIgnoreCase(pSearchQuery.getEphQueryScheme())
						&& ! BBBUtility.isEmpty(pSearchQuery.getEphFilterString()))
				{
					secondUrlGen.addParam(BBBEndecaConstants.NAV_NR, pSearchQuery.getEphFilterString());
				}

				//Adding new code for local-store
				String storeId = pSearchQuery.getStoreId();
				boolean onlineTab = pSearchQuery.isOnlineTab();
				if (!StringUtils.isEmpty(storeId) && !onlineTab ) {
					String ntk = "";
					String ntt = "";
					String ntx = "";

					try {
						ntk = EndecaSearchUtil.getParameter(secondUrlGen.toString(),
								BBBEndecaConstants.NAV_PROPERTY_NAME);
						 ntt = EndecaSearchUtil.getParameter(secondUrlGen.toString(), BBBEndecaConstants.NAV_KEYWORD);
						ntx = EndecaSearchUtil.getParameter(secondUrlGen.toString(),
								BBBEndecaConstants.NAV_SEARCH_MODE);

					} catch (UrlFormatException urlFormatExe) {
						logDebug("urlFormatExe while trying to get N value from nav state - "
								+ urlFormatExe.getMessage());
					}
					if (StringUtils.isEmpty(ntk)) {
						ntk = "P_Stores";
					} else {
						ntk = ntk + BBBCoreConstants.PIPE_SYMBOL + BBBEndecaConstants.P_STORES;
					}
					if (StringUtils.isEmpty(ntt)) {
						ntt = storeId;
					} else {
						ntt = ntt + BBBCoreConstants.PIPE_SYMBOL + storeId;
					}

					if (StringUtils.isEmpty(ntx)) {
						ntx = BBBEndecaConstants.SEARCH_MODE_MATCHALL;
					} else {
						ntx = ntx + BBBCoreConstants.PIPE_SYMBOL +BBBEndecaConstants.SEARCH_MODE_MATCHALL;
					}

					secondUrlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, ntk);
					secondUrlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, ntt);
					secondUrlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, ntx);

				}
				//End Adding new code for local-store

			}

			logDebug("EndecaSearch.getDepartmentsForTreeNavigation:Second Endeca Query "
					+ "String to fetch results for search L3 departments for passed L2: " + secondUrlGen.toString());

			if (facetRefineList == null || facetRefineList.isEmpty()) {
				ENEQuery navQueryObject = null;
				navQueryObject = new UrlENEQuery(secondUrlGen.toString(), getQueryGenerator().getEncoding());
				//Setting below properties to filter the facet results
				navQueryObject.setNavAllRefinements(false);
				//Setting below property for skipping products data
				navQueryObject.setNavNumERecs(0);

				if(!BBBUtility.isEmpty(eqlFilterExpression)) {
					//setting EQL filter before calling Endeca
					navQueryObject.setNavRecordStructureExpr(eqlFilterExpression);
				}
				ENEQueryResults navigationalResults = getEndecaClient().executeQuery(navQueryObject);

				if (navigationalResults != null && navigationalResults.containsNavigation()) {

					// if L2 ID is retrieved from Exp Mgr configuration,
					// populate required values here instead
					if(facetRefinementForL1orL2.getName() == null) {
						if(navigationalResults.getNavigation().getDescriptorDimensions() != null) {
							DimensionList descriptors = navigationalResults.getNavigation().getDescriptorDimensions();
							ListIterator<Dimension> descriptorDimIter = descriptors.listIterator();
							while(descriptorDimIter.hasNext()) {
								Dimension descriptorDim = descriptorDimIter.next();
								if (getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())
										.equals(descriptorDim.getRoot().getDimensionName())) {
									facetRefinementForL1orL2.setName(descriptorDim.getDescriptor().getName());
									facetRefinementForL1orL2
											.setDimensionName(descriptorDim.getDescriptor().getDimensionName());
									facetRefinementForL1orL2
											.setSize("" + navigationalResults.getNavigation().getTotalNumERecs());
									break;
								}
							}
						}
					}

					DimensionList dimenList = navigationalResults.getNavigation().getCompleteDimensions();
					if (dimenList != null && !dimenList.isEmpty()) {

						final ListIterator<Dimension> parentDimIter = dimenList.listIterator();
						while (parentDimIter.hasNext()) {
							final Dimension rootDimension = parentDimIter.next();
							if (getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())
									.equals(rootDimension.getName())) {
								if(facetRefinementForL1orL2.getFacetsRefinementVOs()==null){
								facetRefineList = new ArrayList<FacetRefinementVO>();
								}else {
									updatedfacetRefinementL2inL1 = facetRefinementForL1orL2.getFacetsRefinementVOs()
											.get(0);
									facetRefineList=facetRefinementForL1orL2.getFacetsRefinementVOs();
									facetRefineList.clear();
								}
								final DimValList childDimAncestorList = rootDimension.getAncestors();
								final ListIterator<DimVal> childDimAncestorIter = childDimAncestorList.listIterator();
								while (childDimAncestorIter.hasNext()) {
									childDimVal = childDimAncestorIter.next();
									ancesterCategoryForL2=String.valueOf(childDimVal.getId());
									logDebug("Fetched Ancestor Category:: "+ancesterCategoryForL2);
								}
								final DimValList childDimList = rootDimension.getRefinements();
								final ListIterator<DimVal> childDimIter = childDimList.listIterator();
								while (childDimIter.hasNext()) {
									childDimVal = childDimIter.next();
									if (updatedfacetRefinementL2inL1 != null && updatedfacetRefinementL2inL1
											.getCatalogId().equalsIgnoreCase((String.valueOf(childDimVal.getId())))) {
										facetRefineList.add(updatedfacetRefinementL2inL1);
										continue;
									}
									facetRefinements = new FacetRefinementVO();
									facetRefinements.setName(childDimVal.getName());
									facetRefinements.setDimensionName(childDimVal.getDimensionName());
									final String navigationValue = navigationUrl+" "+childDimVal.getId();
									facetRefinements.setFacetRefFilter(navigationValue.replaceAll(" ", "-"));
									if (null != childDimVal.getProperties()) {
										facetRefinements.setSize((String) childDimVal.getProperties()
												.get(BBBEndecaConstants.DGRAPH_BINS));
									}
									facetRefinements.setCatalogId(String.valueOf(childDimVal.getId()));
									logDebug("Department Id of l2 or l3" + String.valueOf(childDimVal.getId()));
									facetRefineList.add(facetRefinements);

								}
								if(null!=facetRefineList && !facetRefineList.isEmpty()) {
									facetRefinementForL1orL2.setFacetsRefinementVOs(facetRefineList);
								} else {
									logDebug("Zero child departments are returned for Category"+categoryId);
								}
								break;
							}

						}
					}else {
						logDebug("Zero L3 Departments are returned");
					}
				}
			}
		}

		catch (ENEQueryException e) {
			BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001,
					"ENEQueryException in calling Endeca EndecaSearch.getDepartmentsForTreeNavigation");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
					"EndecaSearch.getDepartmentsForTreeNavigation");
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001, "Endeca Exception", e);
		} catch (BBBBusinessException e2) {
			logError(e2.getMessage());
		}
		logDebug("Time taken to make Endeca call for L3 Departments for Given L2 Department :"
				+ (System.currentTimeMillis() - startTime));
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
				"EndecaSearch.getDepartmentsForTreeNavigation");
		return ancesterCategoryForL2;

	}

	/**
	 * The method is returning the sku attribute list with INTL_FLAG!=Y
	*
	* @return AttributeList
	 * @throws BBBSystemException
	 *//*
		 * public Map<String,String> getAttributeInfo() throws
		 * BBBSystemException{ logDebug(
		 * "Inside EndecaSearch METHOD:getAttributeInfo STARTS");
		 * Map<String,String> attributeMap=new HashMap<String, String>();
		 * Iterator e=this.getSkuAttributeCache().getAllKeys(); if(e.hasNext()
		 * ){ while(e.hasNext()){ String w= (String)e.next();
		 * this.getSkuAttributeCache().get(w); attributeMap.put(w,
		 * (String)this.getSkuAttributeCache().get(w)); } } else{
		 * attributeMap=this.getInternationalCatalogTools().getAttributeInfo();
		 * for (Map.Entry<String,String> entry : attributeMap.entrySet()) {
		 * this.getSkuAttributeCache().put(entry.getKey(), entry.getValue()); }
		 *
		 * } logDebug("Inside EndecaSearch METHOD:getAttributeInfo ENDS");
		 * return attributeMap;
		 *
		 * }
		 */
	/* PSI 6  || BPS-798 || Implemente department as tree || Changes End*/
		//92F ends

			/**
			 * @return the stagingServer
			 */
			public boolean isStagingServer() {
				return stagingServer;
			}

			/**
	 * @param stagingServer
	 *            the stagingServer to set
			 */
			public void setStagingServer(boolean stagingServer) {
				this.stagingServer = stagingServer;
	}

	/*
	 * public BBBLocalCacheContainer getConfigCacheContainer() { return
	 * configCacheContainer; } public void
	 * setConfigCacheContainer(BBBLocalCacheContainer configCacheContainer) {
	 * this.configCacheContainer = configCacheContainer; }
*/

			/**
			 * @param pSearchQuery
			 * @param queryString
			 * @param contentCollection
			 *
			 * @return
			 * @throws AssemblerException
			 */
	protected ContentItem executeAssemblerQuery(SearchQuery pSearchQuery, String queryString, String contentCollection)
			throws AssemblerException {
				//set endecaQueryVO in request param
				DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();

				String currentQueryString = dynamoRequest.getQueryString();
		// set query string to dynamo request so that NavigationState has all
		// required information

				if(pSearchQuery != null){
					dynamoRequest.setAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO, pSearchQuery);
					dynamoRequest.setQueryString(pSearchQuery.getQueryURL());
				} else {
					dynamoRequest.setQueryString(queryString);
				}

				//actual call to Endeca
				ContentItem responseContentItem = getEndecaClient().executeContentQuery(contentCollection);

				//reset query string back to previous string after query is executed
				dynamoRequest.setQueryString(currentQueryString);

				return responseContentItem;
			}

			/**
			 * @return the configUtil
			 */
			public EndecaConfigUtil getConfigUtil() {
				return configUtil;
			}

			/**
	 * @param configUtil
	 *            the configUtil to set
			 */
			public void setConfigUtil(EndecaConfigUtil configUtil) {
				this.configUtil = configUtil;
			}

			/**
			 * This method retrieves the Pagination details from Search Engine.
			 *
			 * @param products
			 * @param pSearchQuery
			 * @return
			 */
			public PaginationVO getPagination(final BBBProductList pBbbProducts, final SearchQuery pSearchQuery) {
				logDebug("Entering EndecaSearch.getPagination public method.");

		String pageSize = String.valueOf(getQueryGenerator().getPageSize());
		;
				if(!StringUtils.isEmpty(pSearchQuery.getPageSize())){
					pageSize = pSearchQuery.getPageSize();
				}

				final String refinedString = getQueryGenerator().refineQuery(pSearchQuery.getQueryURL());

				logDebug("Exit EndecaSearch.getPagination public method.");
				return this.getPagination(pBbbProducts, pageSize, refinedString, pSearchQuery);
			}

	/**
	 * this method retrieves pagesize, refinedString and uses those to call
	 * EndecaClient.createCurrentQuery
	 *
	 * @param pSearchQuery
	 * @return
	 */
	public String getCurrentSearchQuery(final SearchQuery pSearchQuery) {
		String pageSize = String.valueOf(getQueryGenerator().getPageSize());
		if(!StringUtils.isEmpty(pSearchQuery.getPageSize())){
			pageSize = pSearchQuery.getPageSize();
		}

		final String refinedString = getQueryGenerator().refineQuery(pSearchQuery.getQueryURL());

		String queryParamForFacets = null;

		if(!BBBUtility.isEmpty(refinedString)){
			queryParamForFacets = refinedString.substring(refinedString.indexOf("?") + 1);
		}

		return getEndecaClient().createCurrentQuery(queryParamForFacets,pageSize,null,pSearchQuery);
	}

	public List<FacetParentVO> getL1Categories(SearchQuery pSearchQuery, ENEQueryResults pENEQueryResults)
			throws BBBSystemException {
		if(pENEQueryResults == null || pSearchQuery == null){
			return null;
		}
		try {
			if(pENEQueryResults.containsNavigation()) {
				final String refinedQueryString = getQueryGenerator().refineQuery(pSearchQuery.getQueryURL());
				List<FacetParentVO> facetList = this.getFacets(pENEQueryResults.getNavigation(), refinedQueryString,
						pSearchQuery, null, null);
				return facetList;
			}
		} catch (ENEQueryException | BBBSystemException | BBBBusinessException e) {
			logError(e);
			throw new BBBSystemException(e.getMessage());
		}
		return null;
	}

	/**
	 * @return the contentPathResolver
	 */
	public ContentPathResolver getContentPathResolver() {
		return contentPathResolver;
	}

	/**
	 * @param contentPathResolver
	 *            the contentPathResolver to set
	 */
	public void setContentPathResolver(ContentPathResolver contentPathResolver) {
		this.contentPathResolver = contentPathResolver;
	}

	public HashMap<String, String> getRatingFilterMap() {
		return ratingFilterMap;
	}

	public void setRatingFilterMap(HashMap<String, String> ratingFilterMap) {
		this.ratingFilterMap = ratingFilterMap;
	}
	/**
	 * This method set applied EPH Scheme in the Request which will be used to populate hidden filed(currentEPHScheme) on PLP/SRP
	 * @param pSearchQuery
	 */
	private void setAppliedEphSchemeParam(SearchQuery pSearchQuery)
	{
		if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants. MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){
			ServletUtil.getCurrentResponse().addHeader(BBBCoreConstants.CURRENT_EPH_SCHEME,pSearchQuery.getEphQueryScheme());
		}
		else{
			ServletUtil.getCurrentRequest().setAttribute(BBBCoreConstants.CURRENT_EPH_SCHEME,pSearchQuery.getEphQueryScheme());
		}
	}
	
	private void setChecklistCategoryResultsCache(final AttributesParamVO attributeParamVO) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, BBBCoreConstants.SET_CHECKLIST_CATEGORY_RESULTS_CACHE);
		attributeParamVO.setCacheName(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.CHECKLIST_CATEGORY_RESULTS_CACHE));
		attributeParamVO.setCacheTimeout(getChecklistCategoryResultsTimeout(attributeParamVO.getCacheName()));
		logDebug("cacheName:" + attributeParamVO.getCacheName());
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION,
				BBBCoreConstants.SET_CHECKLIST_CATEGORY_RESULTS_CACHE);
	}
	
	/**
	 * get cache timeout from config key
	 * 
	 * @param  
	 * @return
	 */
	private int getChecklistCategoryResultsTimeout(String pCacheName) {
		int cacheTimeout;
		try {
				cacheTimeout = Integer.parseInt(BBBConfigRepoUtils.getStringValue(
						BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CHECKLIST_CATEGORY_RESULTS_CACHE_TIMEOUT));
		} catch (NumberFormatException e) {
			logError("EndecaSearch.performSearch || NumberFormatException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.CHECKLIST_CATEGORY_RESULTS_CACHE_TIMEOUT, e);
			cacheTimeout = getKeywordCacheTimeout();
		} catch (NullPointerException e) {
			logError("EndecaSearch.performSearch || NullPointerException occured in " + GETTING_CACHE_TIMEOUT_FOR
					+ BBBCoreConstants.CHECKLIST_CATEGORY_RESULTS_CACHE_TIMEOUT, e);
			cacheTimeout = getKeywordCacheTimeout();
		}
		return cacheTimeout;
	}
}