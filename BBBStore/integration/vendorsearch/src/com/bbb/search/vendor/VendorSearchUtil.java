package com.bbb.search.vendor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.Asset;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.PaginationVO;
import com.bbb.search.bean.result.PromoVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.bean.result.SkuVO;
import com.bbb.search.vendor.constants.VendorSearchConstants;
import com.bbb.search.vendor.vo.VendorRequestVO;
import com.bbb.search.vendor.vo.VendorResponseVO;
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

public class VendorSearchUtil extends BBBGenericService{
	private static final String CATALOG_ID = "catalogId";
	private static final String CLASS_NAME = "Vendor_Search_Util";
	private BBBCatalogTools catalogTools;
	private String alternativePhrasing;
	private String alternativePhrasingResults;
	private String defaultValueAlternativePhrasing;
	
	private String defaultValueAlternativePhrasingResults;
	private String didYouMeanEnabled;
	private String searchField;
	private VendorSearchClient vendorSearchClient;
	//Property to hold Record Type names.
	private Map<String, String> recordTypeNames;
	private static final String IMG_PATTERN = "\\$.*?\\$";
	private static final String IMG_SIZE = "400";
	private static final String IMG_SIZE_FOR_GRID3x3 = "229";
	private static final String IMG_SIZE_FOR_GRID4 = "170";
	private static final String CONSTANT_DOLLAR = "\\$";
	private String facetNameFilterPattern;
	
	/**
	 * @return the facetNameFilterPattern
	 */
	public String getFacetNameFilterPattern() {
		return facetNameFilterPattern;
	}

	/**
	 * @param facetNameFilterPattern the facetNameFilterPattern to set
	 */
	public void setFacetNameFilterPattern(String facetNameFilterPattern) {
		this.facetNameFilterPattern = facetNameFilterPattern;
	}

	private String scene7Path;
	
	// holds the key name for fetching refined facet names
	private String dimDisplayMapConfig;
	/**
	 * @return the dimDisplayMapConfig
	 */
	public String getDimDisplayMapConfig() {
		return dimDisplayMapConfig;
	}

	/**
	 * @param dimDisplayMapConfig the dimDisplayMapConfig to set
	 */
	public void setDimDisplayMapConfig(String dimDisplayMapConfig) {
		this.dimDisplayMapConfig = dimDisplayMapConfig;
	}

	/**
	 * @return the dimNonDisplayMapConfig
	 */
	public String getDimNonDisplayMapConfig() {
		return dimNonDisplayMapConfig;
	}

	/**
	 * @param dimNonDisplayMapConfig the dimNonDisplayMapConfig to set
	 */
	public void setDimNonDisplayMapConfig(String dimNonDisplayMapConfig) {
		this.dimNonDisplayMapConfig = dimNonDisplayMapConfig;
	}

	private String dimNonDisplayMapConfig;
	
	/**
	 * @return the scene7Path
	 */
	public String getScene7Path() {
		return scene7Path;
	}

	/**
	 * @param scene7Path the scene7Path to set
	 */
	public void setScene7Path(String scene7Path) {
		this.scene7Path = scene7Path;
	}

	public Map<String, String> getRecordTypeNames() {
		return recordTypeNames;
	}

	public void setRecordTypeNames(Map<String, String> recordTypeNames) {
		this.recordTypeNames = recordTypeNames;
	}

	/**
	 * @return the vendorSearchClient
	 */
	public VendorSearchClient getVendorSearchClient() {
		return vendorSearchClient;
	}

	/**
	 * @param vendorSearchClient the vendorSearchClient to set
	 */
	public void setVendorSearchClient(VendorSearchClient vendorSearchClient) {
		this.vendorSearchClient = vendorSearchClient;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getDidYouMeanEnabled() {
		return didYouMeanEnabled;
	}

	public void setDidYouMeanEnabled(String didYouMeanEnabled) {
		this.didYouMeanEnabled = didYouMeanEnabled;
	}

	public String getAlternativePhrasingResults() {
		return alternativePhrasingResults;
	}

	public void setAlternativePhrasingResults(String alternativePhrasingResults) {
		this.alternativePhrasingResults = alternativePhrasingResults;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * @return the alternativePhrasing
	 */
	public String getAlternativePhrasing() {
		return alternativePhrasing;
	}

	/**
	 * @param alternativePhrasing the alternativePhrasing to set
	 */
	public void setAlternativePhrasing(String alternativePhrasing) {
		this.alternativePhrasing = alternativePhrasing;
	}
	
	/**
	 * @return the defaultValueAlternativePhrasing
	 */
	public String getDefaultValueAlternativePhrasing() {
		return defaultValueAlternativePhrasing;
	}

	/**
	 * @param defaultValueAlternativePhrasing the defaultValueAlternativePhrasing to set
	 */
	public void setDefaultValueAlternativePhrasing(
			String defaultValueAlternativePhrasing) {
		this.defaultValueAlternativePhrasing = defaultValueAlternativePhrasing;
	}

	/**
	 * @return the defaultValueAlternativePhrasingResults
	 */
	public String getDefaultValueAlternativePhrasingResults() {
		return defaultValueAlternativePhrasingResults;
	}

	/**
	 * @param defaultValueAlternativePhrasingResults the defaultValueAlternativePhrasingResults to set
	 */
	public void setDefaultValueAlternativePhrasingResults(
			String defaultValueAlternativePhrasingResults) {
		this.defaultValueAlternativePhrasingResults = defaultValueAlternativePhrasingResults;
	}


	
	/**
	 * This method prepares VendorRequestVO based on the SearchQuery
	 * @param pSearchQuery
	 * @return
	 */
	public VendorRequestVO prepareVendorReuestVO(SearchQuery pSearchQuery){
		BBBPerformanceMonitor.start(CLASS_NAME,
				"prepareVendorReuestVO");
		logDebug("VendorSearchUtil.prepareVendorReuestVO method starts.input: pSearchQuery : "+pSearchQuery.toString());
		VendorRequestVO vReqVO = new VendorRequestVO();
		//populating Group Id - Ntk in endeca
		DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
		String channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);
		if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())){
			if(pSearchQuery.getCatalogRef().get(CATALOG_ID).contains(" ")){
        		String catalogRefFormatted = pSearchQuery.getCatalogRef().get(CATALOG_ID);
        		catalogRefFormatted = catalogRefFormatted.replaceAll(" ", "+");
        		pSearchQuery.getCatalogRef().put(CATALOG_ID, catalogRefFormatted);
        	}
			//Calling the searchconfig group param for Search Configurable story
			populateSearchGroup(pSearchQuery, pRequest);
		}
		
		//Search Mode - ntx in endeca
		String ntxSearchMode = BBBUtility.getCookie(pRequest,VendorSearchConstants.SEARCH_MODE);
		if(BBBUtility.isBlank(ntxSearchMode))
		{
			ntxSearchMode=pRequest.getQueryParameter(VendorSearchConstants.NAV_SEARCH_MODE);
		}
		
		if(BBBUtility.isNotEmpty(ntxSearchMode)){
			pSearchQuery.setSearchMode(ntxSearchMode);
			logDebug("searchMode set in pSearchQuery"+pSearchQuery.getSearchMode());
		}
		
		//partial match flag
		boolean isPartialMatchSearch = checkForPartialMatchSearch(pSearchQuery,
				channel);
			
		// narrow down - for search within search
		populateNarrowedDownKeyword(pSearchQuery, channel);	
		
		
		alternativePhrasing = this.getDefaultValueAlternativePhrasing(); // Alternative phrasing - Ntpc in endeca - default value
		alternativePhrasingResults = this.getDefaultValueAlternativePhrasingResults(); // alternativePhrasingResults phrasing - Ntpr in endeca - default value
		if(BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown())){
			alternativePhrasing = this.getAlternativePhrasing();
			alternativePhrasingResults = this.getAlternativePhrasingResults();
		}
		
		String typeOfResultReq; //Ntk param in endeca
		if(BBBUtility.isEmpty(pSearchQuery.getGroupId())){
			typeOfResultReq = getSearchField();
		} else {
			typeOfResultReq = pSearchQuery.getGroupId();
		}
		
		//populating Sort-String for SearchQuery
		if(pSearchQuery.getSortCriteria().getSortFieldName() != null){
			if(!pSearchQuery.getSortCriteria().isSortAscending()){
				pSearchQuery.setSortString(pSearchQuery.getSortCriteria().getSortFieldName()+"-1");
			}
			else{
				pSearchQuery.setSortString(pSearchQuery.getSortCriteria().getSortFieldName()+"-0");
			}
		}
		
		//populating vendor request vo
		String keyword;
		if(BBBUtility.isNotEmpty(pSearchQuery.getKeyWord())) {
			keyword = pSearchQuery.getKeyWord();
			keyword = keyword.replaceAll("'", "");
			keyword = ((keyword.replaceAll("[^0-9A-Za-z\"\']"," ")).replaceAll("[\']", "")).replaceAll(" +", " ");
	        keyword = keyword.replaceAll("-", " ");
	        vReqVO.setKeyWord(keyword);
		}
		vReqVO.setPageNum(pSearchQuery.getPageNum());
		vReqVO.setCatalogRef(pSearchQuery.getCatalogRef());
		vReqVO.setGroupId(pSearchQuery.getGroupId());
		vReqVO.setmSearchMode(pSearchQuery.getSearchMode());
		vReqVO.setPartialFlag(pSearchQuery.getPartialFlag());
		vReqVO.setPartialMatchSearch(isPartialMatchSearch);
		vReqVO.setStopWrdRemovedString(pSearchQuery.getStopWrdRemovedString());
		vReqVO.setNarrowDown(pSearchQuery.getNarrowDown());
		vReqVO.setSortCriteria(pSearchQuery.getSortCriteria());
		vReqVO.setPageSize(pSearchQuery.getPageSize());
		vReqVO.setSiteId(pSearchQuery.getSiteId());
		vReqVO.setHostname(pSearchQuery.getHostname());
		vReqVO.setAlternativePhrasing(alternativePhrasing);
		vReqVO.setAlternativePhrasingResults(alternativePhrasingResults);
		vReqVO.setDidYouMeanEnabled(this.getDidYouMeanEnabled()); //didYouMeanEnabled - Nty in endeca
		vReqVO.setTypeOfResultReq(typeOfResultReq);
		vReqVO.setStoreId(pSearchQuery.getStoreId());
		vReqVO.setStoreIdFromAjax(pSearchQuery.getStoreIdFromAjax());
		vReqVO.setOnlineTab(pSearchQuery.isOnlineTab());
		
		BBBPerformanceMonitor.end(CLASS_NAME,
				"prepareVendorReuestVO");
		logDebug("VendorSearchUtil.prepareVendorReuestVO method ends. VendorRequestVO :"+vReqVO.toString());
		
		return vReqVO;
	}

	/**
	 * This function populates narrowed down keyword in search query
	 * @param pSearchQuery
	 * @param channel
	 */
	private void populateNarrowedDownKeyword(SearchQuery pSearchQuery,
			String channel) {
		BBBPerformanceMonitor.start(CLASS_NAME,
				"populateNarrowedDownKeyword");
		logDebug("VendorSearchUtil.populateNarrowedDownKeyword method starts. Channel"+channel);
		String enteredNarrowDown;
		logDebug("Entered NarrowDown is: "+pSearchQuery.getNarrowDown());
		if(!(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel) || BBBCoreConstants.MOBILEAPP
						.equalsIgnoreCase(channel))){
			logDebug("channel is null or desktop");
			//Defect #24526 :searchMode enetered in URL should be excluded from partial search
			if(pSearchQuery.getNarrowDown() !=null){
				//check if for from brand page keyword is populated or null??
				logDebug("making call to removedStopwords in case of SWS");
				enteredNarrowDown=removeStopWords(pSearchQuery.getNarrowDown());
				logDebug("enteredNarrowDown after Stop word removal;:"+enteredNarrowDown);
				pSearchQuery.setNarrowDown(enteredNarrowDown.trim());
		}
		}
		BBBPerformanceMonitor.end(CLASS_NAME,
				"populateNarrowedDownKeyword");
		logDebug("VendorSearchUtil.populateNarrowedDownKeyword method ends. Narrowed Down Keyword:"+pSearchQuery.getNarrowDown());
	}

	/**
	 * This function checks whether we need the partial match search result of not.
	 * @param pSearchQuery
	 * @param channel
	 * @return
	 */
	
	private boolean checkForPartialMatchSearch(SearchQuery pSearchQuery,
			String channel) {
		final String CHECK_FOR_PARTIAL_MATCH_SEARCH = "checkForPartialMatchSearch";
		// To -do It will not be required for vendors. Once it confirmed we will remove this method
		BBBPerformanceMonitor.start(CLASS_NAME,
				CHECK_FOR_PARTIAL_MATCH_SEARCH);
		logDebug("Start:VendorSearchUtil.checkForPartialMatchSearch. Input: pSearchQuery" + pSearchQuery.toString() + "channel"+channel);
		boolean isPartialMatchSearch = false;
		String enteredSearchTerm;
		logDebug("Entered SearchTerm is: "+pSearchQuery.getKeyWord());
		List<String> stopWrdRemovedString;
			if(pSearchQuery.getKeyWord() !=null && !(Boolean.getBoolean(pSearchQuery.getPartialFlag()))
					&& ((pSearchQuery.getSearchMode()!=null && (BBBCmsConstants.SEARCH_MODE_ALLPARTIAL).equalsIgnoreCase(pSearchQuery.getSearchMode())) || null==pSearchQuery.getSearchMode())){
				enteredSearchTerm = pSearchQuery.getKeyWord();
				//Making the check for partial search and setting the searchMode
				stopWrdRemovedString= new ArrayList<>(Arrays.asList(enteredSearchTerm.replaceAll("[\"]", "").replaceAll("\\s+", " ").trim().split(" ")));
				List<String> minKeywords;
				try {
					minKeywords = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.MIN_PARTIALMATCH_WORDS_KEY );
	            	final int minKeywordsCount = Integer.parseInt(minKeywords.get(0));
	            	final List<String> maxKeywords = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.MAX_PARTIALMATCH_WORDS_KEY );
	            	final int maxKeywordsCount = Integer.parseInt(maxKeywords.get(0));
					if(null!=stopWrdRemovedString && stopWrdRemovedString.size()>0 && stopWrdRemovedString.size()>minKeywordsCount && stopWrdRemovedString.size()<maxKeywordsCount && enteredSearchTerm !=null){
						logDebug("Going to trigger a Partial Match Search for stopWordRemovedString : "+enteredSearchTerm);
						pSearchQuery.setStopWrdRemovedString(stopWrdRemovedString);
						pSearchQuery.setSearchMode(BBBCmsConstants.SEARCH_MODE_ALLPARTIAL);
						// Partial Search Flag set for Desktop Web Channel
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
			 catch (BBBSystemException | BBBBusinessException e) {
				logError("Exception occured while fetching configure keys: " + e.getMessage());
				BBBPerformanceMonitor.cancel(CLASS_NAME,
						CHECK_FOR_PARTIAL_MATCH_SEARCH);
				e.printStackTrace();
			}
		}
		logDebug("End:VendorSearchUtil.checkForPartialMatchSearch. isPartialMatchSearch" + isPartialMatchSearch);	
		BBBPerformanceMonitor.end(CLASS_NAME,
				CHECK_FOR_PARTIAL_MATCH_SEARCH);
		return isPartialMatchSearch;
	}
	
	/**
	 * This method populates the groupId set by SiteSpect for modifying search
	 * @param pSearchQuery
	 * @param pRequest
	 */
	private void populateSearchGroup(final SearchQuery pSearchQuery,
			DynamoHttpServletRequest pRequest) {
		// To -do It will not be required for vendors. Once it confirmed we will remove this method
		String searchConfigGrp=pRequest.getHeader(BBBCoreConstants.SITESPEC_SEARCHGROUP);
		if(null!=searchConfigGrp || BBBUtility.isNotEmpty(searchConfigGrp)){
			pRequest.getSession().setAttribute(VendorSearchConstants.GROUP_ID,searchConfigGrp);
		}
		logDebug("searchConfigGrp: " +searchConfigGrp);
		pSearchQuery.setGroupId((String) pRequest.getSession().getAttribute(VendorSearchConstants.GROUP_ID));
	}
	
	/**
	 * This method removes the stop words from entered search term
	 * @param enteredSearchTerm
	 * @return
	 */
	private String removeStopWords(String enteredSearchTerm){
		BBBPerformanceMonitor.start(CLASS_NAME,
				"removeStopWords");
		logDebug("Start:VendorSearchUtil.removeStopWords. Input: enteredSearchTerm::"+enteredSearchTerm.toLowerCase());
		List<String> userInput= new ArrayList<>(Arrays.asList(enteredSearchTerm.trim().toLowerCase().split(" ")));
		List<String> configStopWordList;
		try {
			//stopwordlist should be in lower case
			configStopWordList = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.STOP_WORD_LIST);
			logDebug("StopWrdsList : "+configStopWordList.size());
			if(configStopWordList != null && configStopWordList.size() == 1){
				enteredSearchTerm="";
				List<String> stopWordList = new ArrayList<>(Arrays.asList(configStopWordList.get(0).trim().toLowerCase().split(",")));
				try{
					userInput.removeAll(stopWordList);
					for(String searchTerm: userInput)
						enteredSearchTerm+=searchTerm+" ";
					logDebug("After removing stop Word : "+enteredSearchTerm);
				} catch (Exception e) {
					logError("Error with the stopWords");
		        	logError(e.getLocalizedMessage());
		        	BBBPerformanceMonitor.cancel(CLASS_NAME,
							"removeStopWords");
		        }
			} else {
				BBBPerformanceMonitor.end(CLASS_NAME,
						"removeStopWords");
				return enteredSearchTerm;
			}

		} catch (BBBSystemException e) {
			logError("VendorSearchUtil.removeStopWords || BBBSystemException occured "+ e.getMessage());
			BBBPerformanceMonitor.cancel(CLASS_NAME,
					"removeStopWords");
		} catch (BBBBusinessException e) {
			logError("VendorSearchUtil.removeStopWords || BBBBusinessException occured "+ e.getMessage());
			BBBPerformanceMonitor.cancel(CLASS_NAME,
					"removeStopWords");
		}
		logDebug("End:VendorSearchUtil.removeStopWords. Stop words removed string,enteredSearchTerm:"+enteredSearchTerm);
		BBBPerformanceMonitor.end(CLASS_NAME,
				"removeStopWords");
		return enteredSearchTerm;
	}
	
	/**
	 * This method fetches result from vendor by invoking rest api
	 * @param vReqVO
	 * @return
	 */
	public SearchResults performVendorSearch(VendorRequestVO vReqVO){
		BBBPerformanceMonitor.start(CLASS_NAME,
				"performVendorSearch");
		logDebug("Start:VendorSearchUtil.performVendorSearch. Input: vReqVO:"+vReqVO.toString());
		SearchResults bsVo = new SearchResults();
		VendorResponseVO vResponseVo = getVendorSearchClient().fetchVendorResponse(vReqVO);
		//pagination
		if(null != vResponseVo){
			vResponseVo.setPagingLinks(this.getPagination(vResponseVo, vReqVO));
			//update image links to point to current selected color's images
			
			//updating the facet names as per bcc
			this.updateFacets(vResponseVo,vReqVO);
			this.updateDescriptors(vResponseVo,vReqVO);
			bsVo = this.populateSearchResultFromVendorResponse(vResponseVo, vReqVO);
			logDebug("End:VendorSearchUtil.performVendorSearch. Input: bsVo:"+bsVo.toString());
		}
		else{
			logDebug("End:VendorSearchUtil.performVendorSearch. response from vendor is null");
		}
		BBBPerformanceMonitor.end(CLASS_NAME, "performVendorSearch");
		return bsVo;
		
	}
	
	/**
	 * @param vResponseVo
	 * @param vReqVO
	 * @return
	 */
	private SearchResults populateSearchResultFromVendorResponse(VendorResponseVO vResponseVo, VendorRequestVO vReqVO) {
		BBBPerformanceMonitor.start(CLASS_NAME,
				"populateSearchResultFromVendorResponse");
		SearchResults bsVo = new SearchResults();
		bsVo.setBbbProducts(vResponseVo.getBbbProducts());
		bsVo.setFacets(vResponseVo.getFacets());
		bsVo.setAutoSuggest(vResponseVo.getAutoSuggest());
		bsVo.setDescriptors(vResponseVo.getDescriptors());
		bsVo.setPromoMap(vResponseVo.getPromoMap()); //not coming from vendor
		bsVo.setPartialSearchResults(vResponseVo.getPartialSearchResults());
		bsVo.setPartialKeywordURL(vResponseVo.getPartialKeywordURL());
		bsVo.setPagingLinks(vResponseVo.getPagingLinks());
		bsVo.setRedirUrl(vResponseVo.getRedirUrl());
		if(vResponseVo.getFacets() !=null){
			bsVo.setAssetMap(this.getSearchTabs(vResponseVo.getFacets()));
		}
		bsVo.setAutoSuggest(vResponseVo.getAutoSuggest());
		bsVo.setSearchQuery(this.createSearchQuery(vResponseVo,vReqVO));
		//update image links to point to current selected color's images
		this.enrichVendorResponse(bsVo);
		this.populateRelatedSearchStringKey(bsVo);
		BBBPerformanceMonitor.end(CLASS_NAME,
				"populateSearchResultFromVendorResponse");
		return bsVo;
		
	}
	
	/**
	 * This method retrieves the Pagination details from Search Engine.
	 * 
	 * @param products
	 * @param pSearchQuery
	 * @return
	 */
		public PaginationVO getPagination(final VendorResponseVO vResponseVo , final VendorRequestVO vReqVO) {
		final String GET_PAGINATION = "getPagination";
		BBBPerformanceMonitor.start(CLASS_NAME,
				GET_PAGINATION);	
		logDebug("Entering VendorSearchUtil.getPagination public method.");

		String pageSize = String.valueOf(vReqVO.getPageSize());
//		if(!StringUtils.isEmpty(pSearchQuery.getPageSize())){
//			pageSize = pSearchQuery.getPageSize();
//		}
		
		//final String refinedString = getQueryGenerator().refineQuery(pSearchQuery.getQueryURL());
		final BBBProductList products = vResponseVo.getBbbProducts();
		if(null == products) {
			BBBPerformanceMonitor.end(CLASS_NAME,
					GET_PAGINATION);	
			logDebug("Exit VendorSearchUtil.getPagination no products found.");
			return null;
		}
		String firstPage = null;
		String secondPage = null;
		String thirdPage = null;
		String previousPage = null;
		String nextPage = null;
		String lastPage = null;
		long currentOffset;
		long currentPage;
		long recordCount;
		long pageCount;
		long remainder;
		PaginationVO pagingLinks = new PaginationVO();
		final long argPageSize = Integer.parseInt(pageSize);
		List<String> pageNumberList;
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
		pagingLinks.setPageCount(pageCount);
		pagingLinks.setCurrentPage(currentPage); 
		if(BBBUtility.isNotEmpty(vReqVO.getCatalogRef().get("catalogId"))){
			pagingLinks.setPageFilter(vReqVO.getCatalogRef().get("catalogId").replace('+', '-'));
			pagingLinks.setCanonicalPageFilter(vReqVO.getCatalogRef().get("catalogId").replace('+', '-'));
		}
		
		String page = null;
		for (long pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
			page = this.createPagingHyperlink(vResponseVo, vReqVO);
			pageNumberList.add(page);
		}
		
		firstPage = this.createPagingHyperlink(vResponseVo, vReqVO);
		// Get first page link
		if (currentOffset >= argPageSize) {
			previousPage = this.createPagingHyperlink(vResponseVo, vReqVO);

		}
		
		// get second page link
		if(pageCount>=2){
			secondPage = this.createPagingHyperlink(vResponseVo, vReqVO);
			pagingLinks.setSecondPage(secondPage);
		}
		//get third page link
		if(pageCount>=3){
			thirdPage = this.createPagingHyperlink(vResponseVo, vReqVO);
			pagingLinks.setThirdPage(thirdPage);
		}

		pagingLinks.setThirdLast(this.createPagingHyperlink(vResponseVo, vReqVO));
		pagingLinks.setSecondLast(this.createPagingHyperlink(vResponseVo, vReqVO));
		pagingLinks.setCurrentPageUrl(this.createPagingHyperlink(vResponseVo, vReqVO));
		pagingLinks.setFirstPage(firstPage);
		pagingLinks.setPreviousPage(previousPage);
		// Get the next page and last page link.
		if (currentOffset < ((pageCount - 1) * argPageSize)) {
			nextPage = this.createPagingHyperlink(vResponseVo, vReqVO);
			lastPage = this.createPagingHyperlink(vResponseVo, vReqVO);
			pagingLinks.setNextPage(nextPage);
			pagingLinks.setLastPage(lastPage);
		}
		
		pagingLinks.setPageCount(pageCount);
		pagingLinks.setCurrentPage(currentPage);
		
		BBBPerformanceMonitor.end(CLASS_NAME,
				GET_PAGINATION);	
		logDebug("Exit VendorSearchUtil.getPagination public method.");
		return pagingLinks;
	}
		
	/**
	 * This method is to get search tabs to be shown in search plp
	 * and will retrieve the values from content response
	 * @param pContentResponseVO
	 * @param queryString
	 * @param navigationRefine
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String, Asset> getSearchTabs(final List<FacetParentVO> facetParentList) {
		BBBPerformanceMonitor.start(CLASS_NAME, "getSearchTabs");
		logDebug("Entering VendorSearchUtil.getSearchTabs method.");
		
		ListIterator facetParentIter;
		FacetParentVO facetParent;
		final Map<String, Asset> recTypeParent = new HashMap<>();
		List<FacetRefinementVO> facetRefinements;
		ListIterator facetRefinementIter;
		FacetRefinementVO facetRefinement;
		Asset asset;
		
		FacetParentVO facetToRemove = null;
		
		facetParentIter = facetParentList.listIterator();
		
		while(facetParentIter.hasNext()) {
			facetParent = (FacetParentVO) facetParentIter.next();
			if(null != facetParent && (VendorSearchConstants.RECORD_TYPE).equalsIgnoreCase(facetParent.getName())){
				facetToRemove = facetParent;
				facetRefinements = facetParent.getFacetRefinement();
				facetRefinementIter = facetRefinements.listIterator();
				while(facetRefinementIter.hasNext()) {
					facetRefinement = (FacetRefinementVO) facetRefinementIter.next();
					asset = new Asset();
					asset.setQuery(facetRefinement.getQuery());
					asset.setAssetFilter(facetRefinement.getCatalogId());
					try{
						asset.setCount(Integer.valueOf(facetRefinement.getSize()));	
						String assetName = this.getRecordTypeNames().get(facetRefinement.getName());
						if(null != assetName) {
						recTypeParent.put(assetName,asset);
						}
					} catch(NumberFormatException nfr) {
						logError("unable to fetch count of records tagged to this record type. ignoring this value - "+facetRefinement.getName());
					}
				}
				break;
			}
			
		}
		
		//remove RECORD_TYPE from facet list as this is no longer required
		if(null != facetToRemove) {
			facetParentList.remove(facetToRemove);
			//remove null item from facetParentList
			facetParentList.removeAll(Collections.singleton(null));
		}

		logDebug("Exit VendorSearchUtil.getSearchTabs method.");
		BBBPerformanceMonitor.end(CLASS_NAME, "getSearchTabs");
		return recTypeParent;
	}
	

	/**
	 * Method to update swatch images of products based on color selected in descriptor
	 * 
	 * @param bbbProducts
	 * @param color
	 * @return updated bbbProducts list
	 */
	private void enrichVendorResponse(final SearchResults searchResults) {
		BBBPerformanceMonitor.start(CLASS_NAME, "enrichVendorResponse");
		logDebug("Entering VendorSearchUtil.enrichVendorResponse method.");
		int count = 0;
		String color = null;
		Map<String,SkuVO> colorSkuSet = new TreeMap<>();
		if(null!= searchResults && null!= searchResults.getDescriptors()) {
			searchResults.setCurrentCatName(getCurrentCatName(searchResults.getDescriptors()));
			searchResults.setParentCatName(getParentCatName(searchResults.getDescriptors()));
				
			for(CurrentDescriptorVO pVo : searchResults.getDescriptors()) {
				if(null != pVo.getRootName() && "COLORGROUP".equalsIgnoreCase(pVo.getRootName())) {
					count = count + 1 ;
					color = pVo.getName();
				}
			}
		}
		if(null!= searchResults && null!= searchResults.getBbbProducts()) {
			//when there is only one color in descriptors
			//fix sku image shown while selecting color
			for(BBBProduct product : searchResults.getBbbProducts().getBBBProducts()) {
				if(count == 1 && null != color) {
					setColorGroup(color, product);
				}
				populateAttributesForProducts(product);
				populateImageUrlsForProducts(colorSkuSet, product);							
			}
		}
		logDebug("Exit VendorSearchUtil.enrichVendorResponse method.");
		BBBPerformanceMonitor.end(CLASS_NAME, "enrichVendorResponse");
	}

	
	/**
	 * @param product
	 */
	private void populateAttributesForProducts(BBBProduct product) {
		List<AttributeVO> attributeList = product.getAttributeVO();
		if (!BBBUtility.isListEmpty(attributeList)) {
			for (Iterator<AttributeVO> iterator = attributeList
					.iterator(); iterator.hasNext();) {
				AttributeVO attrVO = iterator.next();
				logDebug("placeholder:" + attrVO.getPlaceHolder());
				if(!attrVO.getPlaceHolder().contains(BBBCoreConstants.PLACE_HOLDER_COMPARE_PRODUCT)) {
					iterator.remove();
					}
				}
		}
	}

	/**
	 * @param count
	 * @param color
	 * @param product
	 */
	private void setColorGroup(String color, BBBProduct product) {
			Map<String, SkuVO> skuMap = product.getSkuSet();
			for(SkuVO skuVO : skuMap.values()) {
				if(null != skuVO.getColorGroup() && skuVO.getColorGroup().equalsIgnoreCase(color)) {
					product.setImageURL(skuVO.getSkuMedImageURL());
					product.setVerticalImageURL(skuVO.getSkuVerticalImageURL());
					break;
				}
			}
	}
	
	/**
	 * Method to iterate over descriptors to find current category name
	 * @param descriptors
	 * @return
	 */
	private String getCurrentCatName(List<CurrentDescriptorVO> descriptors) {
		if(null == descriptors) {
			return "";
		}
		
		for(CurrentDescriptorVO currentDescriptor : descriptors) {
			if(currentDescriptor.getRootName().equalsIgnoreCase(VendorSearchConstants.DEPARTMENT)) {
				return currentDescriptor.getName();
			}
		}
		
		return null;
	}


	/**
	 * Method to iterate over descriptors to find parent category name
	 * @param descriptors
	 * @return
	 */
	private String getParentCatName(List<CurrentDescriptorVO> descriptors) {
		if(null == descriptors) {
			return "";
		}
		
		for(CurrentDescriptorVO currentDescriptor : descriptors) {
			if(currentDescriptor.getRootName().equalsIgnoreCase(VendorSearchConstants.DEPARTMENT)) {
				return currentDescriptor.getAncestorName();
			}
		}
		
		return null;
	}

	/**
	 * This method is used to set product image URL for different views to be used directly while rendering 
	 * images on jsp
	 * @param product
	 */
	private void populateImageUrlsForProducts(Map<String, SkuVO> colorSkuSet, BBBProduct product) {
		List<SkuVO> skuVOList = new ArrayList<>();
		this.setProductImageURLForDifferentView(product);
		for(Map.Entry<String, SkuVO> skuVO: product.getSkuSet().entrySet()){
			this.setSkuImageURLForDifferentView(skuVO.getValue());
			if(!skuVOList.contains(skuVO)){
				skuVOList.add(skuVO.getValue());
			}
		}
		for(SkuVO skuVO : skuVOList){
			if(null != skuVO.getColor() && !colorSkuSet.containsKey(skuVO.getColor())){
					colorSkuSet.put(skuVO.getColor(), skuVO);
			}
			if(product.getColorSet().containsKey(skuVO.getColor())){
				product.getColorSet().put(skuVO.getColor(), skuVO);
			}
		}
		this.populateScene7URL(product);
		this.populateRatings(product);
	}

	
	/**
	 * This method populated imageURL for different views for product display
	 * @param bbbProduct
	 */
	private void setProductImageURLForDifferentView(BBBProduct bbbProduct) {
		BBBPerformanceMonitor.start(CLASS_NAME, "setProductImageURLForDifferentView");
		logDebug("[START] VendorSearchUtil.setProductImageURLForDifferentView() method for product :: " + bbbProduct.getProductID());
		String scene7ImageSize = null;
		try {
			scene7ImageSize = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS , BBBCoreConstants.SCENE7_IMAGE_SIZE_FROM_ENDECA).get(0);
			if (BBBUtility.isEmpty(scene7ImageSize))
            {
                  scene7ImageSize = IMG_SIZE;
            }
            else if(scene7ImageSize.contains(BBBCoreConstants.DOLLAR))
            {
               scene7ImageSize=BBBUtility.getOnlyDigits(scene7ImageSize);
            }

		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error occured while config key : scene7ImageSize "+e);
		} 
		if (!BBBUtility.isEmpty(bbbProduct.getImageURL())) {
			String imageUrl  = bbbProduct.getImageURL();
			Pattern imageSizePattern = Pattern.compile(IMG_PATTERN);
			 Matcher m = imageSizePattern.matcher(imageUrl);
			if (m.find()) {
				imageUrl = imageUrl.replaceAll(IMG_PATTERN, CONSTANT_DOLLAR+scene7ImageSize+CONSTANT_DOLLAR);
				String imageUrlForGrid3x3 = imageUrl.replaceAll(IMG_PATTERN, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID3x3+CONSTANT_DOLLAR);
				String imageUrlForGrid4 = imageUrl.replaceAll(IMG_PATTERN, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID4+CONSTANT_DOLLAR);
				if (isLoggingDebug()) {
					logDebug("Setting imageUrlForGrid3x3 :: " + imageUrlForGrid3x3 +  " and imageUrlForGrid4 :: " + imageUrlForGrid4 + " for Product " + bbbProduct.getProductID());
				}
				bbbProduct.setImageURL(imageUrl);
				bbbProduct.setProductImageUrlForGrid3x3(imageUrlForGrid3x3);
				bbbProduct.setProductImageUrlForGrid4(imageUrlForGrid4);
			}
		}
			logDebug("[END] VendorSearchUtil.setProductImageURLForDifferentView() method for product :: " + bbbProduct.getProductID());
			BBBPerformanceMonitor.end(CLASS_NAME, "setProductImageURLForDifferentView");
	}
	
	/**
	 * This method is used to set sku color swatch image URL for different views to be used directly while rendering 
	 * images on jsp
	 * @param skuVO
	 */
	
	public void setSkuImageURLForDifferentView(SkuVO skuVO) {
		BBBPerformanceMonitor.start(CLASS_NAME, "setSkuImageURLForDifferentView");
		logDebug("[START] VendorSearchUtil.setSkuImageURLForDifferentView() method for sku :: " + skuVO.getSkuID());
		
		String scene7ImageSize = getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS,
				BBBCoreConstants.SCENE7_IMAGE_SIZE_FROM_ENDECA,	IMG_SIZE);

		if (scene7ImageSize.contains(BBBCoreConstants.DOLLAR)) {
			scene7ImageSize = BBBUtility
					.getOnlyDigits(scene7ImageSize);
		}

		String imageUrl  = skuVO.getSkuMedImageURL();
		if (!BBBUtility.isEmpty(imageUrl)) {
			
			Pattern imageSizePattern = Pattern.compile(IMG_PATTERN);
			 Matcher m = imageSizePattern.matcher(imageUrl);
			if (m.find()) {
				imageUrl = imageUrl.replaceAll(IMG_PATTERN, CONSTANT_DOLLAR+scene7ImageSize+CONSTANT_DOLLAR);
			    String imageUrlForGrid3x3 = imageUrl.replaceAll(IMG_PATTERN, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID3x3+CONSTANT_DOLLAR);
				String imageUrlForGrid4 = imageUrl.replaceAll(IMG_PATTERN, CONSTANT_DOLLAR+IMG_SIZE_FOR_GRID4+CONSTANT_DOLLAR);
				if (isLoggingDebug()) {
					logDebug("Setting imageUrl :: " + imageUrl + ", skuMedImageUrlForGrid3x3 :: " + imageUrlForGrid3x3 +  " and skuMedImageUrlForGrid4 :: " + imageUrlForGrid4 + " for Sku " + skuVO.getSkuID());
				}
				skuVO.setSkuMedImageURL(imageUrl);
				skuVO.setSkuMedImageUrlForGrid3x3(imageUrlForGrid3x3);
				skuVO.setSkuMedImageUrlForGrid4(imageUrlForGrid4);
			}
		}
		logDebug("[END] VendorSearchUtil.setSkuImageURLForDifferentView() method for sku :: " + skuVO.getSkuID());
		BBBPerformanceMonitor.end(CLASS_NAME, "setSkuImageURLForDifferentView");
	}

	/**
	 * This method updates the scene7Url in all the product
	 * @param bbbProduct
	 */
	private void populateScene7URL(BBBProduct bbbProduct) {
		BBBPerformanceMonitor.start(CLASS_NAME, "populateScene7URL");
		logDebug("[START] VendorSearchUtil.populateScene7URL() method ");
		int scene7Value;
		String scene7Path = this.getScene7Path();
		try {
			scene7Value = Integer.parseInt(bbbProduct.getProductID()) % 10;
			if (null != scene7Path) {
				if (scene7Value % 2 == 0) {
					bbbProduct.setSceneSevenURL(scene7Path.replaceFirst(BBBCoreConstants.STRING_X, BBBCoreConstants.STRING_ONE));
				} else {
					bbbProduct.setSceneSevenURL(scene7Path.replaceFirst(BBBCoreConstants.STRING_X, BBBCoreConstants.STRING_TWO));
				}
			}

		} catch (NumberFormatException nfe) {
			BBBPerformanceMonitor.cancel(CLASS_NAME, "populateScene7URL");
			logError("Exception thrown in scene7Value"+nfe.getMessage());
		}
		BBBPerformanceMonitor.start(CLASS_NAME, "populateScene7URL");
		logDebug("[START] VendorSearchUtil.populateScene7URL() method ");
	}
	
	/**
	 * This method update the facet name with the configured names in BCC and search query to retain the sorting applied
	 * @param vResponseVO
	 */
	private void updateFacets(VendorResponseVO vResponseVO, VendorRequestVO vReqVO){
		BBBPerformanceMonitor.start(CLASS_NAME, "updateFacetNames");
		logDebug("[START] VendorSearchUtil.updateFacetNames() method ");
		final Map<String, String> dimensionMap = this.getDimensionMap();
		final List<String> categoryIdsToBeSuppressed = this.getCatIdsToBeSuppressed(vReqVO.getSiteId());
		String facetName;
		String query;
		List<FacetRefinementVO> tempFacetRefinementList;
		int index;
		List<FacetParentVO> facetList = vResponseVO.getFacets();
		if(!BBBUtility.isListEmpty(facetList)){
			for(FacetParentVO facet : facetList){
				facetName = facet.getName().replaceAll(this.getFacetNameFilterPattern(), BBBCoreConstants.SPACE);
				facetName = facetName.replace(" ", "_");
				tempFacetRefinementList = new ArrayList<>();
				if(!BBBUtility.isListEmpty(facet.getFacetRefinement())) {
					if(dimensionMap.containsKey(facetName)){
						facet.setName(dimensionMap.get(facetName));
					}
					for(FacetRefinementVO refinement: facet.getFacetRefinement()){
							if(!BBBUtility.isListEmpty(categoryIdsToBeSuppressed) && categoryIdsToBeSuppressed.contains(refinement.getCatalogId())) {
								continue;
							}
							if(dimensionMap.containsKey(facetName)){
								refinement.setDimensionName(dimensionMap.get(facetName));
							}
							if(BBBUtility.isNotEmpty(vReqVO.getSortCriteria().getSortFieldName())){
								query = VendorSearchConstants.SORT_FIELD + BBBCoreConstants.EQUAL + vReqVO.getSortCriteria().getSortFieldName()+BBBCoreConstants.HYPHEN;
								if(vReqVO.getSortCriteria().isSortAscending()){
									query = query + BBBCoreConstants.STRING_ZERO;
								} else {
									query = query + BBBCoreConstants.ONE_STRING;
								}
								refinement.setQuery(query);
							}
							if(BBBInternationalShippingConstants.PRICE_RANGE_ATTR.equalsIgnoreCase(facetName) || "Price".equalsIgnoreCase(facetName)){
								refinement.setName(this.updatePriceFacetCurrency(refinement.getName()));
							}
							tempFacetRefinementList.add(refinement);
					}
					facet.setFacetRefinement(tempFacetRefinementList);
					
				}
			}
		}
		BBBPerformanceMonitor.end(CLASS_NAME, "updateFacetNames");
		logDebug("[EXIT] VendorSearchUtil.updateFacetNames() method ");
	}
	
	/**
	 * This populates the map of dimensions as configured in BCC
	 * @return Map<String, String>
	 */
	private Map<String, String> getDimensionMap() {
		Map<String,String> dimNonDisplayMap = null;
		Map<String,String> dimDisplayMap = null;
		Map<String,String> combinedDimensionMap = new HashMap<String, String>();
		try{
			dimDisplayMap = getCatalogTools().getConfigValueByconfigType(getDimDisplayMapConfig());
			dimNonDisplayMap = getCatalogTools().getConfigValueByconfigType(getDimNonDisplayMapConfig());
			combinedDimensionMap.putAll(dimDisplayMap);
			combinedDimensionMap.putAll(dimNonDisplayMap);
		}catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1040+" BusinessException in default dimensions list  from getDimensionMap from EndecaSearch",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1041+" SystemException in default dimensions list  from getDimensionMap from EndecaSearch",bbbsEx);
		}
		return combinedDimensionMap;
	}

	/**
	 * This populates ratingsForCSS from ratings received for products
	 * @param bbbProduct
	 */
	private void populateRatings(BBBProduct bbbProduct) {
		if (NumberUtils.isNumber(bbbProduct.getRatings())) {
			Integer rating = (int) (Double.valueOf(bbbProduct.getRatings()) * BBBCoreConstants.TEN);
			bbbProduct.setRatingForCSS(rating);
		}
	}
	
	/**
	 * This method updates the currency in Price Facets in case of internationalization
	 * 
	 * @param dimName
	 * @return
	 */
	private String updatePriceFacetCurrency(String dimName) {
		BBBPerformanceMonitor.start(CLASS_NAME, "updatePriceFacetCurrency");
		logDebug("[Start] VendorSearchUtil.updatePriceFacetCurrency() method dimName: "+dimName);
		String country=null;
		//check for price  range facets and for mexico specific facets
			DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
			if(pRequest!=null ){
				Profile profileFromReq = (Profile)pRequest.getAttribute(BBBInternationalShippingConstants.PROFILE);
				if(profileFromReq == null){
					profileFromReq = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
					pRequest.setAttribute(BBBInternationalShippingConstants.PROFILE, profileFromReq);
				}
				if(profileFromReq!=null){
					country=(String) profileFromReq.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
				}
			}
			//check for country is not US or blank,then only localize the prices
			if(BBBUtility.isNotEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) ){
				Properties prop = new Properties();
				prop.setProperty(BBBInternationalShippingConstants.ROUND,BBBInternationalShippingConstants.DOWN);
				Pattern pattern = Pattern.compile(BBBCoreConstants.PATTERN_FORMAT);
				Matcher matcher;
				matcher = pattern.matcher(dimName);
				int i = 0;
				String lowPrice =BBBCoreConstants.BLANK;
				String highPrice = BBBCoreConstants.BLANK;
				while (matcher.find()) {
					if (i == 0) {
						lowPrice = matcher.group();
						lowPrice=lowPrice.substring(1);
						i++;
					} else if (i == 1){
						highPrice = matcher.group();
						highPrice=highPrice.substring(1);
					}
	
				}
				 dimName=BBBUtility.convertToInternationalPrice(dimName, lowPrice, highPrice, prop);
		 }
		BBBPerformanceMonitor.end(CLASS_NAME, "updatePriceFacetCurrency");
		logDebug("[EXIT] VendorSearchUtil.updatePriceFacetCurrency() method updated dimName: "+dimName);
		return dimName;
	}
	
	/**
	 * This method populates relatedSearchStringKey which is used to show related searched in mobile
	 * @param bsVo
	 */
	private void populateRelatedSearchStringKey(SearchResults bsVo) {
		StringBuilder relatedSearchStringKeys = new StringBuilder(); 
		if(bsVo.getPromoMap() == null){
			return;
		}
		Map<String, List<PromoVO>> promoMap = bsVo.getPromoMap();
		List<PromoVO> promoVoList = promoMap.get("CENTER");
		String relatedSearchString;
		String relatedString[];
		String key;
		for(PromoVO promoVo:promoVoList) {
			relatedSearchString = promoVo.getRelatedSearchString();
			logDebug("relatedSearchString"+relatedSearchString);
			if (null != relatedSearchString) {
				 relatedString = relatedSearchString.split(BBBCatalogConstants.DELIMITERCOMMA);
				 int sizeRelatedString = relatedString.length;
					for (String commaSeperated : relatedString) {
						key= commaSeperated.trim().toLowerCase().replaceAll("[\'\"]", "").replaceAll("[^a-z0-9]", " ").replaceAll("[ ]+", "-");
						relatedSearchStringKeys.append(key);                    
		                if(sizeRelatedString>1){
		                	relatedSearchStringKeys.append(',');
		  					sizeRelatedString--;
		                }
					}
					promoVo.setRelatedSearchStringKeys(relatedSearchStringKeys.toString());
			}
		}

	}
	
	/**
	 * This method generates the search query to be appended for all the urls formed on the page
	 * @param vReqVO
	 * @return searchQuery
	 */
	private String createSearchQuery(VendorResponseVO vResponseVo,VendorRequestVO vReqVO) {
		BBBPerformanceMonitor.start(CLASS_NAME, "createSearchQuery");
		logDebug("[Start] VendorSearchUtil.createSearchQuery() method VendorRequestVO: "+vReqVO);
		String searchQuery="";
		
		if(BBBUtility.isNotEmpty(vReqVO.getSortCriteria().getSortFieldName())){
			searchQuery = VendorSearchConstants.SORT_FIELD + BBBCoreConstants.EQUAL + vReqVO.getSortCriteria().getSortFieldName()+BBBCoreConstants.HYPHEN;
			if(vReqVO.getSortCriteria().isSortAscending()){
				searchQuery = searchQuery + BBBCoreConstants.STRING_ZERO;
			} else {
				searchQuery = searchQuery + BBBCoreConstants.ONE_STRING;
			}
		}
		if(BBBUtility.isNotEmpty(vResponseVo.getRedirUrl())){
			searchQuery = searchQuery + BBBCoreConstants.AMPERSAND + VendorSearchConstants.IS_REDIRECT + BBBCoreConstants.EQUAL + BBBCoreConstants.TRUE;
		}
		
		if(BBBUtility.isNotEmpty(vReqVO.getPartialFlag())){
			searchQuery = searchQuery + BBBCoreConstants.AMPERSAND + VendorSearchConstants.PARTIAL_FLAG + BBBCoreConstants.EQUAL + vReqVO.getPartialFlag();
		}
		
		
		BBBPerformanceMonitor.end(CLASS_NAME, "createSearchQuery");
		logDebug("[EXIT] VendorSearchUtil.createSearchQuery() method returns searchQuery: "+searchQuery);
		return searchQuery;
	}
	
	/**
	 * This method generates the search query to be appended for all the urls formed on the page
	 * @param vReqVO
	 * @return searchQuery
	 */
	private String createPagingHyperlink(VendorResponseVO vResponseVo,VendorRequestVO vReqVO) {
		BBBPerformanceMonitor.start(CLASS_NAME, "createPagingHyperlink");
		logDebug("[Start] VendorSearchUtil.createPagingHyperlink() method VendorRequestVO: "+ vReqVO);
		String hyperlink="";
		
		if(BBBUtility.isNotEmpty(vReqVO.getSortCriteria().getSortFieldName())){
			hyperlink = VendorSearchConstants.SORT_FIELD + BBBCoreConstants.EQUAL + vReqVO.getSortCriteria().getSortFieldName()+BBBCoreConstants.HYPHEN;
			if(vReqVO.getSortCriteria().isSortAscending()){
				hyperlink = hyperlink + BBBCoreConstants.STRING_ZERO;
			} else {
				hyperlink = hyperlink + BBBCoreConstants.ONE_STRING;
			}
		}
		if(BBBUtility.isNotEmpty(vResponseVo.getRedirUrl())){
			hyperlink = hyperlink + BBBCoreConstants.AMPERSAND + VendorSearchConstants.IS_REDIRECT + BBBCoreConstants.EQUAL + BBBCoreConstants.TRUE;
		}
		
		if(BBBUtility.isNotEmpty(vReqVO.getPartialFlag())){
			hyperlink = hyperlink + BBBCoreConstants.AMPERSAND + VendorSearchConstants.PARTIAL_FLAG + BBBCoreConstants.EQUAL + vReqVO.getPartialFlag();
		}
		
		
		BBBPerformanceMonitor.end(CLASS_NAME, "createPagingHyperlink");
		logDebug("[EXIT] VendorSearchUtil.createPagingHyperlink() method returns hyperlink: " + hyperlink);
		return hyperlink;
	}
	
	/**
	 * This method updates the descriptor with the sorting query to retain sorting criteria on removing filer.
	 * @param vResponseVo
	 * @param vReqVO
	 */
	private void updateDescriptors(VendorResponseVO vResponseVo,VendorRequestVO vReqVO) {
		BBBPerformanceMonitor.start(CLASS_NAME, "updateDescriptors");
		logDebug("[START] VendorSearchUtil.updateDescriptors() method : Params : VendorRequestVO " + vReqVO + "and \n VendorResponseVO : "+vResponseVo);
		List<CurrentDescriptorVO> descriptors = vResponseVo.getDescriptors();
		String searchQuery;
		if(!BBBUtility.isListEmpty(descriptors) && BBBUtility.isNotEmpty(vReqVO.getSortCriteria().getSortFieldName())) {
			for(CurrentDescriptorVO cuurentDescVO:descriptors) {
				searchQuery = VendorSearchConstants.SORT_FIELD + BBBCoreConstants.EQUAL + vReqVO.getSortCriteria().getSortFieldName()+BBBCoreConstants.HYPHEN;
				if(vReqVO.getSortCriteria().isSortAscending()){
					searchQuery = searchQuery + BBBCoreConstants.STRING_ZERO;
				} else {
					searchQuery = searchQuery + BBBCoreConstants.ONE_STRING;
				}
				cuurentDescVO.setRemovalQuery(searchQuery);
			}
		}
		
		BBBPerformanceMonitor.end(CLASS_NAME, "updateDescriptors");
		logDebug("[EXIT] VendorSearchUtil.updateDescriptors() method returns nothing");
	}
	
	
	/**
	 * This method fetched category id's which are not to be shown on front end. | These ids are BCC configurable
	 * @param pSiteId
	 * @return
	 */
	public List<String> getCatIdsToBeSuppressed(final String pSiteId) {
		List<String> mFacetList = new ArrayList<String>();
		List<String> facetList = null;
		try {
			facetList = getCatalogTools().getAllValuesForKey(getDimNonDisplayMapConfig(), pSiteId);
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

}