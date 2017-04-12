package com.bbb.search.droplet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.comparison.BBBProductComparisonList;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

import com.bbb.personalstore.manager.PersonalStoreManager;
import com.bbb.redirectURLs.CategoryRedirectURLLoader;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.AutoSuggestVO;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SWSTermsVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/** This droplet takes URL parameter and populate SearchQuery VO with required parameters,
 * and invoke performSearch method.
 * @author agupt8
 *
 */

public class SearchDroplet extends BBBDynamoServlet {

	private static final String LAST_ENTERED_SWS_KEYWORD = "lastEnteredSWSKeyword";
	private static final String SWSEMPTY = "swsempty";
	private static final String CATEGORY_ID = "categoryId";
	private static final String PAG_SORT_OPT_SESS = "pagSortOptSess";

	private static final String SITE_ID = "Site_ID";
	private static final String RECORD_TYPE = "Record Type";
	private static final String PRODUCT = "Product";
	private static final String DIMENSION_IDS = "dimensionIds";
	private static final String PAGE_SIZE = "pageSize";
	private static final String APPEND_PROD_DIM_ID = "appendProdDimId";
	private static final String PATH = "/";
	//private static final String GRID = "grid";
	private static final String CURRENT_VIEW = "currentView";
	private static final String VIEW = "view";
	private static final String SAVEDURL = "SAVEDURL";
	private static final String SEARCH_MODE = "searchMode";
	private static final String PAG_FILTER_OPT = "pagFilterOpt";
	private static final String SWS_TERMS="swsterms";
	private SearchManager mSearchManager;
	private Map<String, Object> convertObjectToMap;
	//private String mFacets;
	private String mHeaderFacets;
	private BBBCatalogTools catalogTools;
	private String searchDimConfig;
	private String headerFacetsDim;
	private String dimDisplayMapConfig;
	private String dimNonDisplayMapConfig;
	private static final String DEPARTMENT = "DEPARTMENT";
	/*private List<String> dropdownList;

	public void setDropdownList(List<String> dropdownList) {
		this.dropdownList = dropdownList;
	}*/

	public static final String OUTPUT_ERROR_MSG = "errorMsg";
	public static final String SEARCH_BIZ_EXCEPTION = "err_search_biz_exception";
	public static final String SEARCH_SYS_EXCEPTION = "err_search_sys_exception";
	
	private static final String LINK_STRING = "linkString";
	private static final String REDIRECT_URL = "redirectUrl";
	private static final String IS_REDIRECT = "isRedirect";
	private static final String OUTPUT_REDIRECT = "redirect";
	private static final String UN_MATCH_SEARCH_CRITERIA = "unmetSearchCriteria";

	private static final String IS_HEADER = "isHeader";
	private static final String KEYWORD = "Keyword";
	private static final String NARROW_DOWN = "narrowDown";
	private static final String FROM_BRAND_PAGE = "frmBrandPage";
	private static final int MIN_KEYWORD_LENGTH = 2;
	private static final String PAGE_SIZE_COOKIE_NAME = "pageSizeFilter";
	private static final String REDIRECT_URL_IDENTIFIER="isRedirect=true"; 
	private static final String FL_FILTER="fl_";
	private static final String COLON_FILTER="::";
	private static final String DEFAULT_PAG_NUM = "1";
	private static final String DASH_LITERAL = "-";
	private static final String STORE_ID = "storeId";
	private static final String ONLINE_TAB = "onlineTab";
	private static final String STORE_ID_FROM_AJAX = "storeIdFromAjax";
	private static final String SEARCHPAGE = "searchPage";
	boolean isHeaderSearch;
	boolean isValidSearchCriteria;
    private String maxAgeCurrentViewCookie;
    private String defaultView;
	private PersonalStoreManager mPersonalStoreMgr;
	private CategoryRedirectURLLoader categoryRedirectURL;
	//Property to hold siteIds Map.
	private Map<String, String> siteIdMap;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private boolean removePhantomCat;
	private SiteContextManager siteContextManager;

	public boolean isRemovePhantomCat() {
		return removePhantomCat;
	}

	public void setRemovePhantomCat(boolean removePhantomCat) {
		this.removePhantomCat = removePhantomCat;
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
	
	/* ===================================================== *
			GETTERS and SETTERS
	 * ===================================================== */

	public String getDefaultView() {
		String defaultPLPView=null;
		try {
			defaultPLPView = getCatalogTools().getDefaultPLPView(SiteContextManager.getCurrentSiteId());
			if(!StringUtils.isBlank(defaultPLPView))
			{
				this.defaultView=defaultPLPView;
			}
		} catch (BBBSystemException e) {
			logError("Error retrieving default PL view "+ e.getMessage());
		}
		return this.defaultView;
	}

	public void setDefaultView(String pDefaultView) {
		this.defaultView=pDefaultView;
	}

	public String getMaxAgeCurrentViewCookie() {
		return maxAgeCurrentViewCookie;
	}

	public void setMaxAgeCurrentViewCookie(String maxAgeCurrentViewCookie) {
		this.maxAgeCurrentViewCookie = maxAgeCurrentViewCookie;
	}



	/**
	 * @return mSearchManager
	 */
	public SearchManager getSearchManager() {
		return this.mSearchManager;
	}

	/**
	 * @param pSearchManager
	 */
	public void setSearchManager(final SearchManager pSearchManager) {
		this.mSearchManager = pSearchManager;
	}	
	
	public String getHeaderFacets() {
		List<String> headerFacet = new ArrayList<String>();
		try {
			headerFacet = getCatalogTools().getAllValuesForKey(getSearchDimConfig(), getHeaderFacetsDim());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1012+" Business Exception occurred while fetching header dimension name from  getHeaderFacets from SearchDroplet",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1013+" System Exception occurred while fetching header dimension name from  getHeaderFacets from SearchDroplet",bbbsEx);
		}
		if(!headerFacet.isEmpty()){
			this.mHeaderFacets = headerFacet.get(0);
		}
		//System.out.println("Header facets: "+mHeaderFacets);
		return this.mHeaderFacets;
	}

	public void setHeaderFacets(final String pHeaderFacets) {
		this.mHeaderFacets = pHeaderFacets;
	}

	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public String getSearchDimConfig() {
		return this.searchDimConfig;
	}

	public void setSearchDimConfig(String searchDimConfig) {
		this.searchDimConfig = searchDimConfig;
	}

	public String getHeaderFacetsDim() {
		return this.headerFacetsDim;
	}

	public void setHeaderFacetsDim(String headerFacetsDim) {
		this.headerFacetsDim = headerFacetsDim;
	}
	
	public String getDimDisplayMapConfig() {
		return this.dimDisplayMapConfig;
	}

	public void setDimDisplayMapConfig(String dimDisplayMapConfig) {
		this.dimDisplayMapConfig = dimDisplayMapConfig;
	}

	public String getDimNonDisplayMapConfig() {
		return this.dimNonDisplayMapConfig;
	}

	public void setDimNonDisplayMapConfig(String dimNonDisplayMapConfig) {
		this.dimNonDisplayMapConfig = dimNonDisplayMapConfig;
	}
	
	public boolean isHeaderSearch() {
		return this.isHeaderSearch;
	}

	public void setHeaderSearch(boolean isHeaderSearch) {
		this.isHeaderSearch = isHeaderSearch;
	}
	
	public boolean isValidSearchCriteria() {
		return this.isValidSearchCriteria;
	}

	public void setValidSearchCriteria(boolean isValidSearchCriteria) {
		this.isValidSearchCriteria = isValidSearchCriteria;
	}

	/**
	 * @return the categoryRedirectURL
	 */
	public CategoryRedirectURLLoader getCategoryRedirectURL() {
		return categoryRedirectURL;
	}
	

	/**
	 * @param categoryRedirectURL the categoryRedirectURL to set
	 */
	public void setCategoryRedirectURL(CategoryRedirectURLLoader categoryRedirectURL) {
		this.categoryRedirectURL = categoryRedirectURL;
	}
	
	/* ===================================================== *
		STANDARD METHODS
	 * ===================================================== */

	

	@SuppressWarnings("unchecked")
	@Override
	public void service(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) throws ServletException,IOException 
	{
		/***
		 * ILD-871 - Added to push site context to rebuild the header
		 * flyout cache across all the sites.
		 * Added check if rebuildHeaderCacheFlag is TRUE in BCC.
		 */
		String pSiteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
		final String rebuildHeaderCacheFlag = this.getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.KEY_REBUILD_HEADER_CACHE_FLAG, BBBCoreConstants.FALSE);
		if (pSiteId != null && rebuildHeaderCacheFlag.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
			try {
				Site site = this.getSiteContextManager().getSite(pSiteId);
				if (site != null) {
					this.getSiteContextManager().pushSiteContext(new SiteContextImpl(this.getSiteContextManager(), site));
					ServletUtil.getCurrentRequest().setAttribute(BBBCoreConstants.SITE_ID, pSiteId);
				}
			} catch (SiteContextException e) {
				if (isLoggingError()) {
					logError("Cannot get site context for the site id: " + pSiteId + " and exception stacktrace: " + e.getMessage());
				}
				return;
			}
		}
		
		//HYD-199. Below code is added to do internal redirect of Post request to Get for Back button issue for SSL.
		if(pRequest.getMethod() != null && pRequest.getMethod().equalsIgnoreCase("POST")){
			if(isLoggingDebug()){
				logDebug(BBBCoreConstants.SEARCH_DROPLET + " : Doing Internal redirect from Post request method to Get");
			}
			String queryparams = (String) pRequest.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING);
			StringBuilder redirectUrl = new StringBuilder();
			redirectUrl.append((String) pRequest.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING));
			if(queryparams!=null){
				redirectUrl.append("?");
				redirectUrl.append(queryparams);
			}
			if(isLoggingDebug()){
				logDebug(BBBCoreConstants.SEARCH_DROPLET + " : Doing Internal redirect to " + redirectUrl.toString());
			}
			
			pRequest.getSession().setAttribute("origSearchTerm", pRequest.getParameter("origSearchTerm"));			
			pResponse.sendLocalRedirect(redirectUrl.toString(),pRequest);
			return;
		}
		logDebug("Entering Service Method of Search Droplet.");
		setValidSearchCriteria(true);
		setHeaderSearch(false);
		
		final String methodName = BBBCoreConstants.SEARCH_DROPLET;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION,	methodName);

		//Creating SearchQuery Object with pRequest parameters.
		final SearchQuery pSearchQuery = new SearchQuery();
		final Map<String, String> catalogRef = new HashMap<String, String>();
		final SortCriteria sortCriteria = new SortCriteria();
		
		//adding new parameters for instore invetory
		String storeId = pRequest.getParameter(STORE_ID);
		String onlineTabValue = pRequest.getParameter(ONLINE_TAB);
		String storeIdFromAjax = pRequest.getParameter(STORE_ID_FROM_AJAX);
		String searchPage = pRequest.getParameter(SEARCHPAGE);
		boolean isAjaxSearchPage =false;
		if(BBBUtility.isNotEmpty(searchPage) && BBBCoreConstants.TRUE.equalsIgnoreCase(searchPage) ){
			isAjaxSearchPage=true;
		}
		
		String origKeyword = pRequest.getQueryParameter(KEYWORD);
		
		String narrowDown = pRequest.getQueryParameter(NARROW_DOWN);
		//92F
		if(null!=pRequest.getParameter("refType")){
			String refType = pRequest.getParameter("refType");
			pSearchQuery.setRefType(refType);
			}
		
		if(BBBUtility.isNotEmpty(storeId)){
			pSearchQuery.setStoreId(storeId);
		}
		
		if(BBBUtility.isNotEmpty(storeIdFromAjax)){
			pSearchQuery.setStoreIdFromAjax(storeIdFromAjax);
		}
		
		if(BBBUtility.isNotEmpty(onlineTabValue)){
			boolean onlineTab = Boolean.parseBoolean(onlineTabValue);
			pSearchQuery.setOnlineTab(onlineTab);			
		}else{
			pSearchQuery.setOnlineTab(true);
		}
		
		
		if(null!=pRequest.getParameter("partialFlag")){
		String partialFlag=pRequest.getParameter("partialFlag");
		pSearchQuery.setPartialFlag(partialFlag);
		}
		//R2.2 Seo Friendly Url - 504 A : Start 
		// Search term to display to user
		String enteredSearchTerm=BBBCoreConstants.BLANK;

		if(null != pRequest.getSession().getAttribute("origSearchTerm")){
			enteredSearchTerm = (String) pRequest.getSession().getAttribute("origSearchTerm");
			if((pRequest.getContextPath()).equalsIgnoreCase(BBBCoreConstants.SLASH+BBBCoreConstants.TBS)){
				pRequest.getSession().setAttribute("origSearchTermDisplay", enteredSearchTerm);
			}
			pRequest.getSession().setAttribute("origSearchTerm", null);
		}else{
			enteredSearchTerm = pRequest.getParameter("origSearchTerm");
			//Reverting code P1 issue in Sanity Search Not working on TBS
			/*if((null==enteredSearchTerm)&&(null!=pRequest.getCookieParameter(BBBCoreConstants.SEARCHTERM))){
				 		enteredSearchTerm=(String)pRequest.getCookieParameter(BBBCoreConstants.SEARCHTERM);
			}*/
		}
		//instore count search term
		if((!pSearchQuery.isOnlineTab() ||BBBUtility.isNotEmpty(pSearchQuery.getStoreIdFromAjax())) &&(null!=pRequest.getCookieParameter(BBBCoreConstants.SEARCHTERM)) && isAjaxSearchPage){
	 		enteredSearchTerm=(String)pRequest.getCookieParameter(BBBCoreConstants.SEARCHTERM);
		}
		
		// SWS code started
		String additionalNarrowDown = pRequest.getParameter("additionalKeyword");
		String enteredNarrowDown = pRequest.getParameter(SWS_TERMS);
       
       if(BBBUtility.isNotEmpty(narrowDown)){
			narrowDown = narrowDown.replaceAll(BBBCmsConstants.HYPHEN, BBBCmsConstants.SPACE).trim();
		}
		if(BBBUtility.isNotEmpty(additionalNarrowDown)){
			additionalNarrowDown = URLDecoder.decode(additionalNarrowDown);
		}
		
		if(BBBUtility.isNotEmpty(narrowDown)){
			narrowDown = StringEscapeUtils.unescapeHtml(narrowDown);
		}
		// SWS code ended
		if(BBBUtility.isNotEmpty(enteredSearchTerm) && !(pRequest.getContextPath()).equalsIgnoreCase(BBBCoreConstants.SLASH+BBBCoreConstants.TBS)){
			enteredSearchTerm = URLDecoder.decode(enteredSearchTerm);
		}
		else if(BBBUtility.isNotEmpty(origKeyword)){
			enteredSearchTerm = origKeyword.replaceAll(BBBCmsConstants.HYPHEN, BBBCmsConstants.SPACE).trim();
		}
		
		if(BBBUtility.isNotEmpty(enteredSearchTerm)){
			enteredSearchTerm = StringEscapeUtils.unescapeHtml(enteredSearchTerm);
		}
		pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
		pRequest.setParameter(NARROW_DOWN, narrowDown);
		pRequest.setParameter("enteredNarrowDown", narrowDown);
		
		//R2.2 Seo Friendly Url - 504 A : End
		pSearchQuery.setHostname(pRequest.getServerName());
		if (null != origKeyword){
			origKeyword = StringEscapeUtils.unescapeHtml(origKeyword);
		}

		String searchTerm = pRequest.getParameter("enteredSearchTerm");
		getPersonalStoreMgr().createLastSearchedCookie(pRequest, pResponse, searchTerm);
		
		//BBBJ-1220
		pSearchQuery.setFromChecklistCategory(false);
		if(BBBUtility.isNotEmpty(pRequest.getParameter(BBBCoreConstants.CHECKLIST_CATEGORY_ID)) && BBBUtility.isNotEmpty(pRequest.getParameter(BBBCoreConstants.CHECKLIST_ID))) {
			pSearchQuery.setChecklistCategoryId(pRequest.getParameter(BBBCoreConstants.CHECKLIST_CATEGORY_ID));
			pSearchQuery.setChecklistId(pRequest.getParameter(BBBCoreConstants.CHECKLIST_ID));
			pSearchQuery.setFromChecklistCategory(true);
		}
		
		//R2.2 Product Comparison Page - 178 A4. Setting last searched url to cookie : Start
		String lastSearchUrl = pRequest.getParameter("savedUrl");
		if(!BBBUtility.isEmpty(lastSearchUrl)){
			String domain=pRequest.getServerName();
			String path=PATH;
			logDebug("path to set :" + path);
			logDebug("domain to set:" + domain);
	
			Cookie cookie = new Cookie(SAVEDURL, lastSearchUrl);
			cookie.setDomain(domain);
			cookie.setPath(path);
			BBBUtility.addCookie(pResponse, cookie, true);
		}
		//R2.2 Product Comparison Page - 178 A4 : End
		
//		Release 2.2.1 Save and retrieve PLP View from Cookies : Start
		
		setCurrentViewUsingCookies(pRequest, pResponse);
		
//		Release 2.2.1 Save and retrieve PLP View from Cookies : End 		
		
		if ((null != pRequest.getParameter(IS_REDIRECT)) && (Boolean.valueOf(pRequest.getParameter(IS_REDIRECT)))) {
			pSearchQuery.setRedirected(true);
		}
		if (null != pRequest.getParameter(IS_HEADER) && (BBBCoreConstants.YES_CHAR).equalsIgnoreCase(pRequest.getParameter(IS_HEADER))) {
			setHeaderSearch(true);
		}else if ((null != enteredSearchTerm && enteredSearchTerm.length() < MIN_KEYWORD_LENGTH) && (!(BBBCoreConstants.TRUE).equalsIgnoreCase(pRequest.getQueryParameter(FROM_BRAND_PAGE)))) {
			setHeaderSearch(true);
			setValidSearchCriteria(false);
		}
		
		if(!isValidSearchCriteria) {
			pRequest.serviceLocalParameter(UN_MATCH_SEARCH_CRITERIA, pRequest, pResponse);
		}
		
		/*String pSiteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);*/
		if (pSiteId == null) {
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		pSearchQuery.setSiteId(pSiteId);
		
		// start of TBS changes
		String babyItems = pRequest.getParameter("babyItems");
		if(!StringUtils.isBlank(babyItems) && babyItems.equals("true")){
			pSearchQuery.setSiteId("BuyBuyBaby");
		}
		// end of TBS changes
		String searchMode=null;
		
		//PS-27418 - Ability to test Endeca Search configurations with SiteSpect START
		searchMode = BBBUtility.getCookie(pRequest,SEARCH_MODE);
		//PS-27418 - Ability to test Endeca Search configurations with SiteSpect END
		
		if(BBBUtility.isBlank(searchMode))
		{
			searchMode = pRequest.getQueryParameter(SEARCH_MODE);
		}
		
		logDebug("SearchDroplet searchMode from Request"+searchMode);
		if(BBBUtility.isNotEmpty(searchMode)){
			pSearchQuery.setSearchMode(searchMode);
			logDebug("searchMode is partial match"+pSearchQuery.getSearchMode());
		}
		
		// Setting the Facets List to Query Object to obtain the results for.
		//List<String> pFacets = null;
		boolean pageNumZeroCheck = false;		
		if (isHeaderSearch() && null!=pRequest.getParameter("CatalogId")) {
			catalogRef.put("catalogId", pRequest.getParameter("CatalogId").replace('-', '+'));
			catalogRef.put("catalogRefId", pRequest.getParameter("CatalogRefId"));
		/*	pFacets = Arrays.asList(getHeaderFacets().split(","));  */
			// Set flag to indicate that Search is for Header section and bypass useless type casting response.
			pSearchQuery.setHeaderSearch(true);
		}		
		else{
			pSearchQuery.setHeaderSearch(false);
			try{
			if(null != pRequest.getQueryParameter("pagNum")) {
				if(0 != Integer.parseInt(pRequest.getQueryParameter("pagNum"))){
					pSearchQuery.setPageNum(pRequest.getQueryParameter("pagNum"));
					logDebug("pagenum value for all pages where null != pRequest.getQueryParameter(pagNum) "+pSearchQuery.getPageNum());
				}
				else {
					pageNumZeroCheck = true;
				}
			}	
			else{
				pSearchQuery.setPageNum("1");
			}
			}
			catch (NumberFormatException e){
				localRedirect(pRequest , pResponse , pSearchQuery);
				return;
			} 
			pSearchQuery.setKeyWord(enteredSearchTerm);
			pSearchQuery.setNarrowDown(narrowDown);
			// Flag to mark If request is coming for Brand.
			if(("true").equalsIgnoreCase(pRequest.getQueryParameter("frmBrandPage"))){
				catalogRef.put("frmBrandPage", "true");
				pSearchQuery.setFromBrandPage(true);
			}
			if(("true").equalsIgnoreCase(pRequest.getQueryParameter("fromCollege"))){
				catalogRef.put("fromCollege", "true");
				pSearchQuery.setFromCollege(true);
			}
			
			//R2.2 Seo Friendly Url - 504 A changes
			//Replacing - with + to pass catalog id to Endeca Query generator 
			if(null==pRequest.getQueryParameter("CatalogId")){
				if(pRequest.getParameter("CatalogId") != null){
					catalogRef.put("catalogId", pRequest.getParameter("CatalogId").replace('-', '+'));
				}
				catalogRef.put("catalogRefId", pRequest.getParameter("CatalogRefId"));
			}
			else{
				catalogRef.put("catalogId", pRequest.getQueryParameter("CatalogId").replace('-', '+'));
				catalogRef.put("catalogRefId", pRequest.getQueryParameter("CatalogRefId"));
			}

			String pagSortOpt = pRequest.getParameter("pagSortOpt");
			/**
			 * BBBSL-4250 start 
			 * If the droplet is called from category page.
			 * If pagSortOpt is available from query param, save pagSortOpt into session otherwise retrieve from session
			 */
			if (null != pRequest.getQueryParameter(CATEGORY_ID) || pSearchQuery.isFromChecklistCategory()) {
				if (null==pagSortOpt ) {
					pagSortOpt = (String) pRequest.getSession().getAttribute(PAG_SORT_OPT_SESS);
				} else {
					pRequest.getSession().setAttribute(PAG_SORT_OPT_SESS, pagSortOpt);
				}
			}
			/**
			 * BBBSL-4250 end
			 */
			
			if(null!=pagSortOpt){
				final String sortString = pagSortOpt;
				if(sortString.contains("-")){
					final StringBuffer sortBuffer = new StringBuffer(pagSortOpt);
					final int sortOrderIndex=sortBuffer.indexOf("-");
					
					// Setting the Sort order for Ascending or not.
					if((sortBuffer.substring(sortOrderIndex+1)).equals( "1")){
						sortCriteria.setSortAscending(false);
					}
					else {
						sortCriteria.setSortAscending(true);
					}
					
					// Setting the Sort field name to be sorted on.
					sortCriteria.setSortFieldName(sortBuffer.substring(0,sortOrderIndex));
				}
			}
			else if(null != pRequest.getParameter("bccSortCode")){
				final String sortString = pRequest.getParameter("bccSortCode");
				final String sortOrderString = pRequest.getParameter("bccSortOrder");
				
				if(!BBBUtility.isEmpty(sortOrderString) && sortOrderString.equals( "1")){
					sortCriteria.setSortAscending(false);
				}
				else {
					sortCriteria.setSortAscending(true);
				}
				
				sortCriteria.setSortFieldName(sortString);
				
			}
	//		pFacets = Arrays.asList(getFacets().split(","));
		}
		
		if(pageNumZeroCheck){
			pRequest.serviceParameter("error_PageNumOutOfBound", pRequest, pResponse);
		    pResponse.setStatus(404);
		}
		else{
	//	pSearchQuery.setQueryFacets(pFacets);
		pSearchQuery.setCatalogRef(catalogRef);
		pSearchQuery.setSortCriteria(sortCriteria);
		try{
			if(null!=pSearchQuery.getKeyWord() && (pSearchQuery.getCatalogRef().isEmpty()|| BBBUtility.isEmpty(pSearchQuery.getCatalogRef().get("catalogId")))){
				catalogRef.put("catalogId", getSearchManager().getCatalogId("Record Type", "Product"));
				pSearchQuery.setCatalogRef(catalogRef);
			}
			
			/*START - Added as part of R2.2 Story - 116-D1 & 116-D2*/
			//Setting the Page size in Query Object, firstly by checking in parameter, if not available then check cookie
			String pageSizeFilter = null;
			pageSizeFilter = pRequest.getQueryParameter(PAG_FILTER_OPT);
			List<String> dropdownList = getSearchManager().fetchPerPageDropdownList();
			String highest = dropdownList.get(dropdownList.size() - 1);
			if(!StringUtils.isEmpty(pageSizeFilter)) {
				if(!dropdownList.contains(pageSizeFilter)){
					// If View All is selected then set Page Size to highest
					pSearchQuery.setPageSize(highest);
				}
				else{
					pSearchQuery.setPageSize(pageSizeFilter);
				}
			}
			else {
				pageSizeFilter = pRequest.getCookieParameter(PAGE_SIZE_COOKIE_NAME);
				if(BBBUtility.isNotEmpty(pageSizeFilter)) {
					if(!dropdownList.contains(pageSizeFilter)) {
						// If View All is selected then set Page Size to highest
						pSearchQuery.setPageSize(highest);
					}else {
						pSearchQuery.setPageSize(pageSizeFilter);
					}
				}	
			}
			//Fetch default perPage value from Configure Key
			if(BBBUtility.isEmpty(pSearchQuery.getPageSize())){
				List<String> defaultPerPage = null;
				DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				if(request !=null && BBBUtility.isStringPatternValid(BBBEndecaConstants.IPAD_PATTERN, request.getHeader(BBBEndecaConstants.USER_AGENT))){
					logDebug("header for ipad request is " + request.getHeader(BBBEndecaConstants.USER_AGENT));
					defaultPerPage = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT_ON_IPAD);
				}else{
					defaultPerPage = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT); 
				}
				if(defaultPerPage != null && !defaultPerPage.isEmpty() && BBBUtility.isEmpty(pSearchQuery.getPageSize())){
					pageSizeFilter = defaultPerPage.get(0);
					pSearchQuery.setPageSize(pageSizeFilter);
				}
			}
			//pSearchQuery.setPageSize(pSearchQuery.getPageSize()!=null ? pSearchQuery.getPageSize() : highest);
			/*END - Added as part of R2.2 Story - 116-D1 & 116-D2*/
			
			if (null != pRequest.getParameter(APPEND_PROD_DIM_ID) && (BBBCoreConstants.YES_CHAR).equalsIgnoreCase(pRequest.getParameter(APPEND_PROD_DIM_ID))) {
				String productDimId = getSearchManager().getCatalogId(RECORD_TYPE, PRODUCT);
				String siteDimId = getSearchManager().getCatalogId(SITE_ID,getSiteIdMap().get(pSearchQuery.getSiteId()));
				pRequest.setParameter(PAGE_SIZE, DEFAULT_PAG_NUM +DASH_LITERAL+(pSearchQuery.getPageSize()!=null ? pSearchQuery.getPageSize() : highest));
				pRequest.setParameter(DIMENSION_IDS, productDimId+DASH_LITERAL+siteDimId+DASH_LITERAL);
			}
			
			final SearchResults browseSearchVO = getSearchManager().performSearch(pSearchQuery);
			
			// populating CategoryRedirectURLMap with new values
	        if(BBBUtility.isMapNullOrEmpty(this.getCategoryRedirectURL().getCategoryRedirectURLMap())) {
	        	      	
	        	Map<String, Object> cacheMap = getConvertObjectToMap();
	        		        	
	        	if(!BBBUtility.isMapNullOrEmpty(cacheMap)) {
	        		for(Entry<String, Object> cache : cacheMap.entrySet()) {
	        			this.getCategoryRedirectURL().getCategoryRedirectURLMap().put(cache.getKey(), ((HashMap<String, String>) cache.getValue()).get("desktopCategoryRedirectURL"));        			
	        		} 
	        	} else {
	        		try {
						this.getCategoryRedirectURL().fetchCategoryRedirectURLs();
					} catch (RepositoryException e) {
						logDebug("Error in re-populating categoryRedirectURLMap..."+e.getMessage());
					}    		
	        	}
	        }
	        
			//added for BPSI-2623
			long totProductCount = -1; 
			int noOfProductOnSinglePage = -1;
			int pageNoFromRequest = -1;
			if(null != browseSearchVO){
				if(null != browseSearchVO.getBbbProducts()){
					totProductCount =browseSearchVO.getBbbProducts().getBBBProductCount();
					noOfProductOnSinglePage = Integer.parseInt(pSearchQuery.getPageSize());
					pageNoFromRequest = Integer.parseInt(pSearchQuery.getPageNum());
				}
			}
			logDebug("Total no of products = " + totProductCount);
			int isFromHeader= 0;
            if(pRequest.getParameter("CatalogId")!=null && (pRequest.getParameter("CatalogId")).equals("0") ){
                  isFromHeader= 1;
            }
//			Added for Defect 2257071
			if(browseSearchVO == null){
				pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
				pRequest.setParameter("enteredNarrowDown", enteredNarrowDown);
				pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest, pResponse);
			    pResponse.setStatus(404);
			}
			else if((null != browseSearchVO) && (null != browseSearchVO.getBbbProducts()) &&
					!(((totProductCount/noOfProductOnSinglePage) + 1) >= pageNoFromRequest)){
				localRedirect(pRequest, pResponse, pSearchQuery);
		    }
			else if(((browseSearchVO.getBbbProducts() == null || browseSearchVO.getBbbProducts().getBBBProductCount() == 0) && pRequest.getQueryParameter("CatalogId") != null && !(BBBUtility.isNotEmpty(narrowDown) )) && (!((pRequest.getContextPath()).contains(BBBUtility.toLowerCase(BBBCoreConstants.TBS)) && isFromHeader==1)) ){
				pRequest.setParameter("redirectToParent", "true");
				logDebug("Redirecting to page without filters. getBbbProducts() = " + browseSearchVO.getBbbProducts() + ", CatalogId=" +pRequest.getQueryParameter("CatalogId"));
				pRequest.serviceParameter(OUTPUT_REDIRECT, pRequest, pResponse);
			} 
			else if(((browseSearchVO.getBbbProducts() == null || browseSearchVO.getBbbProducts().getBBBProductCount() == 0) && (BBBUtility.isNotEmpty(onlineTabValue)) && !(Boolean.parseBoolean(onlineTabValue))) && (!((pRequest.getContextPath()).contains(BBBUtility.toLowerCase(BBBCoreConstants.TBS)) && isFromHeader==1)) ){
				pRequest.setParameter("redirectToParent", "true");
				logDebug("Redirecting to page without filters. getBbbProducts() = " + browseSearchVO.getBbbProducts() + ", CatalogId=" +pRequest.getQueryParameter("CatalogId") + ", onlineTabValue=" + onlineTabValue);
				pRequest.serviceParameter(OUTPUT_REDIRECT, pRequest, pResponse);
			}
			else{
				//setNarrowDownBluePills(narrowDown, browseSearchVO);
				List<SWSTermsVO> swsTermsList = new ArrayList<SWSTermsVO>();
				swsTermsList = setBluePills(narrowDown, enteredNarrowDown);
				
				//for bookmark functionality
				StringBuilder bluePillSWSTermBuilder = new StringBuilder();
				if(narrowDown!= null && enteredNarrowDown == null){
					Iterator<SWSTermsVO> iter=swsTermsList.iterator();
					while (iter.hasNext()) {
						SWSTermsVO result=  iter.next();
						bluePillSWSTermBuilder.append(result.getName());
						if(iter.hasNext())
						bluePillSWSTermBuilder.append(COLON_FILTER);
					}
					logDebug("Set Param swsterms with value= "+bluePillSWSTermBuilder);
					pRequest.setParameter(SWS_TERMS,bluePillSWSTermBuilder);
				}
				//Start : BPS- 1952 | Search Within Search | Auto phrase issue for SWS keywords
				setLastSearchedSWSKewordWhenAutosuggestReturned(pRequest, browseSearchVO, swsTermsList);
				//Start : BPS- 1952 | Search Within Search | Auto phrase issue for SWS keywords
				/*Changes for sorting related search keyword starts
					if (null != browseSearchVO && null != browseSearchVO.getPromoMap()) {
						List<PromoVO> promoVOList = browseSearchVO.getPromoMap()
								.get(getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_PROMO_KEY_CENTER,
										BBBCoreConstants.DEFAULT_LOCALE, null));
						for (PromoVO pvo : promoVOList) {
							Map<String, String> sortedMap = new TreeMap<String, String>();
							if (null != pvo.getRelatedSeperated() && !pvo.getRelatedSeperated().isEmpty()) {
								sortedMap.putAll(pvo.getRelatedSeperated());
								pvo.setRelatedSeperated(sortedMap);
							}
						}
					}
				Changes for sorting related search keyword Ends*/
				pRequest.setParameter("swsTermsList",swsTermsList);
				
				
				//setting parameter for storeInventory count 
				if (browseSearchVO.getBbbProducts() != null && (browseSearchVO.getBbbProducts().getStoreInvCount()== 0 ||browseSearchVO.getBbbProducts().getStoreInvCount()>0) && (!BBBUtility.isEmpty(pSearchQuery.getPageNum()) && (pSearchQuery.getPageNum()).equalsIgnoreCase("1")) ){
					//pRequest.setParameter("storeInvCount", browseSearchVO.getBbbProducts().getStoreInvCount());
					pRequest.getSession().setAttribute("storeInventoryCount", browseSearchVO.getBbbProducts().getStoreInvCount());
				}else if(browseSearchVO.getBbbProducts() != null && pRequest.getSession().getAttribute("storeInventoryCount") !=null){
					browseSearchVO.getBbbProducts().setStoreInvCount((Long)pRequest.getSession().getAttribute("storeInventoryCount"));
					//pRequest.setParameter("storeInvCount", browseSearchVO.getBbbProducts().getStoreInvCount());
				}
				
				
				pRequest.setParameter("browseSearchVO",browseSearchVO);
				if(null != pRequest.getParameter("CatalogId") && null != pRequest.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING) && ((String) pRequest.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING)).contains(pRequest.getParameter("CatalogId") + "-")){
					pRequest.setParameter("filtersonLoad", "true");	
				}
					if (Boolean.parseBoolean(pRequest.getParameter(BBBCoreConstants.AJAXIFY_FILTERS))) {
						pRequest.setParameter(BBBCoreConstants.AJAXIFY_FILTERS, pRequest.getParameter(BBBCoreConstants.AJAXIFY_FILTERS));
						String queryparams = (String) pRequest.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING);
						StringBuilder redirectUrl = new StringBuilder();
						redirectUrl.append((String) pRequest.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING));
						if (queryparams != null) {
							redirectUrl.append("?");
							redirectUrl.append(queryparams);
						}						
						pRequest.setParameter(BBBCoreConstants.REDIRECTURL, redirectUrl.substring(0, redirectUrl.indexOf("&" +BBBCoreConstants.AJAXIFY_FILTERS)));
						
						
					}
				getDropdownList(); //R2.2 Story - 116- D1 & D2
				if(null != browseSearchVO.getCategoryHeader() && null == browseSearchVO.getCategoryHeader().getQuery()){
					CategoryParentVO pCVo = new CategoryParentVO();
					Map<String,CategoryVO> pMap= getCatalogTools().getParentCategory(pSearchQuery.getCatalogRef().get("catalogRefId"), pSearchQuery.getSiteId());
					if(null != pMap && pMap.size() >= 1){
							if(null != pMap.get("1") && !pMap.get("1").getPhantomCategory().booleanValue()){
								pCVo.setQuery(pMap.get("1").getCategoryId());
							}
							else{
								pCVo.setQuery(pMap.get("0").getCategoryId());
							}	
							pCVo.setName(pMap.get("0").getCategoryName());
						pCVo.setPhantomCategory(pMap.get("0").getPhantomCategory());
					}
					browseSearchVO.setCategoryHeader(pCVo);
				}
								
				pRequest.setParameter("searchTerm", origKeyword);
				pRequest.setParameter("narrowDown", narrowDown);
				pRequest.setParameter("enteredNarrowDown", enteredNarrowDown);
				
				
				if (isValidSearchCriteria()) {
					
					//R2.2 Story - 178-a Product Comparison tool Changes | Set inCompareDrawer as true which will be used to show checkbox Pre-selected for comparison
					if(browseSearchVO.getBbbProducts() != null && browseSearchVO.getBbbProducts().getBBBProductCount() > 0 ){
						BBBProductComparisonList productComparisonList = (BBBProductComparisonList)pRequest.resolveName("/atg/commerce/catalog/comparison/ProductComparisonList");
						LinkedHashMap<String,CompareProductEntryVO> compareProductsMap = new LinkedHashMap<String, CompareProductEntryVO>();
						List productsInDrawer = productComparisonList.getItems();
						
						for(Iterator<CompareProductEntryVO> iter = productsInDrawer.iterator(); iter.hasNext();){
							CompareProductEntryVO drawerproductVO = iter.next();
								compareProductsMap.put(drawerproductVO.getProductId(), drawerproductVO);
						}
						List<BBBProduct> list = browseSearchVO.getBbbProducts().getBBBProducts();
						if(compareProductsMap.size() > 0){
							for(BBBProduct product : list){
								if(compareProductsMap.containsKey(product.getProductID())){
									product.setInCompareDrawer(true);
								}
							}
						}
						else{
							for(BBBProduct product : list){
									product.setInCompareDrawer(false);
							}
						}
						
					}
					// START - changes - TBXPS-1694
					if(browseSearchVO.getFacets() != null ){
						List<FacetParentVO> listNew = browseSearchVO.getFacets();
							Collections.sort(listNew, new Comparator<FacetParentVO>() {
								public int compare(FacetParentVO o1, FacetParentVO o2) {
									if(DEPARTMENT.equals(o2.getName())) {
										return 1;
									} 
									else if (DEPARTMENT.equals(o1.getName())) {
										return -1;
									}
									else
										return 0;
								} 
							});
					}
					//R2.2 Changes Story 116A Starts
					/*int pos = 0;
					FacetParentVO tempvo = null;
					if(browseSearchVO.getFacets() != null ){
						List<FacetParentVO> list = browseSearchVO.getFacets();
						Iterator<FacetParentVO> itr = list.iterator();
						while(itr.hasNext()){
							FacetParentVO vo = itr.next();
							if(null != vo.getName() && vo.getName().equalsIgnoreCase(DEPARTMENT)){
								tempvo = vo;
								break;
							}
							pos++;
						}
						if(pos != 0 && tempvo != null){
							list.remove(pos);
							list.add(0,tempvo);
						}
					}*/
					// END - changes - TBXPS-1694
					
//					if (browseSearchVO.getBbbProducts() != null && (browseSearchVO.getBbbProducts().getOnlineProductInvCount()== 0 ||browseSearchVO.getBbbProducts().getOnlineProductInvCount()>0) ){
//						pRequest.setParameter("onlineInvCount", browseSearchVO.getBbbProducts().getOnlineProductInvCount());						
//					}
					//R2.2 Changes Story 116A Ends
					
					boolean frmBrandpage = Boolean.valueOf(pRequest.getQueryParameter(FROM_BRAND_PAGE)) ;
					if ((browseSearchVO.getBbbProducts() == null || browseSearchVO.getBbbProducts().getBBBProductCount() == 0)
							&& (browseSearchVO.getPartialSearchResults()==null || browseSearchVO.getPartialSearchResults().isEmpty())) {
						
						logDebug("CLS = SearchDroplet /MSG=[EMPTY- BBBProducts- AssetMap is null/empty]");
						int othersCount = 0;
						int videoCount = 0;
						int guideCount = 0;
						if(browseSearchVO.getAssetMap() != null){
							logDebug("SearchDroplet --- Asset Map is not null");
							if( browseSearchVO.getAssetMap().get("Other") != null){
								othersCount = browseSearchVO.getAssetMap().get("Other").getCount();
								logDebug("Other count: " + othersCount);
							}
							if( browseSearchVO.getAssetMap().get("Media") != null){
								videoCount = browseSearchVO.getAssetMap().get("Media").getCount();
								logDebug("video Count: " + videoCount);
							}
							if( browseSearchVO.getAssetMap().get("Guide") != null){
								guideCount = browseSearchVO.getAssetMap().get("Guide").getCount();
								logDebug("guide Count: " + guideCount);
							}
						}
						
						if (BBBUtility.isEmpty(pSearchQuery.getNarrowDown()) && !frmBrandpage && null != browseSearchVO.getRedirUrl() && ((null == pRequest.getParameter(IS_REDIRECT)) || (!Boolean.valueOf(pRequest.getParameter(IS_REDIRECT))))) {
							setRedirectionURLinRequest(pRequest, pResponse,
									browseSearchVO.getRedirUrl());
						} else if(BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown()) && !BBBUtility.isListEmpty(swsTermsList)){
								pRequest.setParameter(LAST_ENTERED_SWS_KEYWORD, swsTermsList.get(swsTermsList.size()-1).getName());
								pRequest.serviceParameter(SWSEMPTY, pRequest, pResponse);
								if(!Boolean.parseBoolean(pRequest.getParameter(BBBCoreConstants.AJAXIFY_FILTERS))){
									pResponse.setStatus(404);
								}								
						}
							else if (null != browseSearchVO.getFacets()					
								&& browseSearchVO.getFacets().size() != 0) {
							pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
						}
						
						// Redirect to no search result page of asset map is null or (others count is > 0 and video & guide count == 0) 
						else if(browseSearchVO.getAssetMap() == null || browseSearchVO.getAssetMap().isEmpty() || (videoCount == 0 && othersCount !=0 && guideCount == 0)) {
							pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
							pRequest.setParameter("narrowDown", narrowDown);
							pRequest.setParameter("enteredNarrowDown", enteredNarrowDown);
	
							pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest, pResponse);
							pResponse.setStatus(404);
						}
						else {
							pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
							pRequest.setParameter("narrowDown", narrowDown);
							pRequest.setParameter("enteredNarrowDown", enteredNarrowDown);
							
							pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest, pResponse);
							pResponse.setStatus(404);
						}
					}
					else {
						logDebug("CLS = [SearchDroplet] /MSG=[OUTPUT PARAM =" + browseSearchVO.getAssetMap() + "]");
						if (BBBUtility.isEmpty(pSearchQuery.getNarrowDown()) && !frmBrandpage && null != browseSearchVO.getRedirUrl() && ((null == pRequest.getParameter(IS_REDIRECT)) || (!Boolean.valueOf(pRequest.getParameter(IS_REDIRECT))))) {
							setRedirectionURLinRequest(pRequest, pResponse,
									browseSearchVO.getRedirUrl());
						} else if (browseSearchVO.getBbbProducts() != null || browseSearchVO.getPartialSearchResults() != null) {
								if(browseSearchVO.getBbbProducts() != null){
									final String linkString = createLinkString(browseSearchVO.getBbbProducts().getBBBProducts());
									pRequest.setParameter(LINK_STRING, linkString);
								}
							pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
						}
					}
				}
			}
		}
		catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from service of SearchDroplet", BBBCoreErrorConstants.BROWSE_ERROR_1010),e);
			pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
			pRequest.setParameter(OUTPUT_ERROR_MSG, SEARCH_BIZ_EXCEPTION);
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
		}
		catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException from service of SearchDroplet", BBBCoreErrorConstants.BROWSE_ERROR_1011),e);
			pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
			pRequest.setParameter(OUTPUT_ERROR_MSG, SEARCH_SYS_EXCEPTION);
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
		}
		
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
	}
	public void localRedirect(DynamoHttpServletRequest pRequest , DynamoHttpServletResponse pResponse , SearchQuery pSearchQuery) throws IOException{
		//BBB-973 Redirect the user L2 landing page if the filters selected lead to oops page.
		logDebug("pagenum value is out of Integer limits: "+pSearchQuery.getPageNum());
		String queryparams = (String) pRequest.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING);
		StringBuilder redirectUrl = new StringBuilder();
		redirectUrl.append((String) pRequest.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING));
		if(queryparams!=null){
			redirectUrl.append("?");
			redirectUrl.append(queryparams);
		}
		Pattern pattern = Pattern.compile("^\\D*(\\d)");
	    Matcher matcher = pattern.matcher(redirectUrl);
	    if(matcher.find()){
	    	StringBuilder correctSubStr = new StringBuilder();
	    	correctSubStr.append(redirectUrl.substring(0, matcher.end()-1));
	    	String firstIntInUrl = redirectUrl.substring(correctSubStr.length());
            if(firstIntInUrl.contains(BBBCoreConstants.SLASH)){
                correctSubStr.append(firstIntInUrl.substring(0,firstIntInUrl.indexOf(BBBCoreConstants.SLASH)));
         }
		logDebug("Hence redirecting to L1 landing page: "+correctSubStr.toString());
		pResponse.sendLocalRedirect(correctSubStr.toString(),pRequest);
		pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	    }
	   
		
	}
	protected Map<String, Object> getConvertObjectToMap() {
		if(convertObjectToMap == null) {
			NamedCache objectCache = CacheFactory.getCache(BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.CATEGORY_REDIRECTURLS_CACHE_NAME));
			convertObjectToMap = BBBUtility.convertObjectToMap(objectCache);
		}
		return convertObjectToMap;
	}

	
	public void setConvertObjectToMap(Map<String, Object> convertObjectToMap) {
		this.convertObjectToMap = convertObjectToMap;
	}

	/**
	 * Method to set Redirection URL and service parameter "re-direct" in
	 * request for Endeca redirection rules
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param redirectUrl
	 * @throws ServletException
	 * @throws IOException
	 */
	private void setRedirectionURLinRequest(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String redirectUrl)
			throws ServletException, IOException {
		if (!BBBUtility.isEmpty(redirectUrl)) {
			if (redirectUrl.contains(BBBCoreConstants.QUESTION_MARK)) {
				redirectUrl = redirectUrl + BBBCoreConstants.AMPERSAND + REDIRECT_URL_IDENTIFIER;
			} else {
				redirectUrl = redirectUrl + BBBCoreConstants.QUESTION_MARK + REDIRECT_URL_IDENTIFIER;
			}
		}
		pRequest.setParameter(REDIRECT_URL, redirectUrl);
		pRequest.serviceParameter(OUTPUT_REDIRECT, pRequest,
				pResponse);
	}

	/**
	 * Method to set last entered SWS keyword in request if Auto Suggestion list is not empty
	 * @param pRequest
	 * @param browseSearchVO
	 * @param swsTermsList
	 */
	private void setLastSearchedSWSKewordWhenAutosuggestReturned(
			final DynamoHttpServletRequest pRequest,
			final SearchResults browseSearchVO, List<SWSTermsVO> swsTermsList) {
		if(!BBBUtility.isListEmpty(browseSearchVO.getAutoSuggest()) && !BBBUtility.isListEmpty(swsTermsList)){
			for(AutoSuggestVO autoSuggestVO:browseSearchVO.getAutoSuggest()){
				if(autoSuggestVO.getSpellCorrection()!=null || autoSuggestVO.getDymSuggestion()!=null){
					pRequest.setParameter(LAST_ENTERED_SWS_KEYWORD, swsTermsList.get(swsTermsList.size()-1).getName());
					break;
				}
				logDebug("SWS Search Term for Auto Suggestion::: "+autoSuggestVO.getSearchTerms());
			}
			
		}
	}

	
	/**
	 * Blue Pills creation logic on the basis of narrow down keywords
	 * 
	 * @param narrowDown
	 * @param browseSearchVO
	 */
	private List<SWSTermsVO> setBluePills(String narrowDown, String userEntererdTerms) {
		logDebug(" Entering into SetBluePills method with two params narrow down= "+narrowDown +"userEnteredTerms ="+ userEntererdTerms);
		final List<SWSTermsVO> bluePillsList = new ArrayList<SWSTermsVO>();
		SWSTermsVO swsTermsVO = new SWSTermsVO();
		Object[] swsArray=null;
		if (narrowDown != null) {
			String[] splitSearchTerms = narrowDown.split(FL_FILTER);
			if(splitSearchTerms.length<1)
			{
				logDebug(" splitSearchTerms.length is Zero , returning with null");
				return new ArrayList<SWSTermsVO>();
			}
			List<String> urlTermList = new LinkedList<String>(
					Arrays.asList(splitSearchTerms));
			//remove empty first item in list
			urlTermList.remove(0);
			if(BBBUtility.isNotEmpty(userEntererdTerms)){
			swsArray = userEntererdTerms.split(COLON_FILTER);
			}else{
				swsArray=urlTermList.toArray();
			}
			int oneSWSCounter = 0;
			for(Object userEnteredTerm : swsArray ) {
				if(BBBUtility.isNotEmpty((String) userEnteredTerm)){
				swsTermsVO = new SWSTermsVO();
				try {
					swsTermsVO.setName(URLDecoder.decode((String)userEnteredTerm, BBBCoreConstants.UTF_8));
				} catch (UnsupportedEncodingException e) {
					logError("UnsupportedEncodingException"+e);
				}
				StringBuilder bluePillURLBuilder = new StringBuilder();
				StringBuilder bluePillPostBuilder = new StringBuilder();
				int multipleSWSCounter= 0;
				for(String urlTerm : urlTermList) {
					if (multipleSWSCounter != oneSWSCounter) {
						bluePillURLBuilder.append(FL_FILTER);
						bluePillURLBuilder.append(urlTerm);
						if(multipleSWSCounter < swsArray.length)
						{
							bluePillPostBuilder.append(swsArray[multipleSWSCounter]);
							bluePillPostBuilder.append(COLON_FILTER);
						}
					}
					multipleSWSCounter++;
				}
				swsTermsVO.setValue(bluePillURLBuilder.toString().replace(BBBCmsConstants.SPACE, BBBCmsConstants.HYPHEN));
				swsTermsVO.setRemovalValue(bluePillPostBuilder.toString());
				bluePillsList.add(swsTermsVO);
				oneSWSCounter++;
				}
			}
		}
		logDebug(" Blue pills size " + bluePillsList.size());
		return bluePillsList;
	}
	

	
	private void setCurrentViewUsingCookies(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		if(isLoggingDebug())
		{
			logDebug("setCurrentViewUsingCookies [start]");
		}
		String plpView = (String) pRequest.getParameter(VIEW);
		String currentViewParam = null;
		if (!StringUtils.isBlank(plpView)) {
			currentViewParam = plpView;
		} else {
			final Cookie[] allCookies = pRequest.getCookies();
			if (allCookies != null) {
				for (int cookiesCount = 0; cookiesCount < allCookies.length; cookiesCount++) {
					if (CURRENT_VIEW.equals(allCookies[cookiesCount].getName())) {
						currentViewParam = allCookies[cookiesCount].getValue();
						break;
					}
				}
			if (StringUtils.isBlank(currentViewParam)) {
				// Set default value as grid
				currentViewParam = getDefaultView();
			}
		}
		}
        if(BBBUtility.isAlphaNumeric(currentViewParam)){
	    	currentViewParam = currentViewParam.toLowerCase();
        	if(!(currentViewParam.equals("grid") || currentViewParam.equals("grid4") || currentViewParam.equals("list"))){
        		currentViewParam = getDefaultView();
        		currentViewParam = currentViewParam.toLowerCase();
        	}
	    }else{
	    	currentViewParam = getDefaultView();
	    	currentViewParam = currentViewParam.toLowerCase();
	    }
        String currentViewParamEncoded = StringEscapeUtils.escapeXml(currentViewParam);
        pRequest.setParameter(VIEW, currentViewParamEncoded);       
		List<String> configValue=null;
		try {
			configValue = getCatalogTools().getAllValuesForKey(getSearchDimConfig(), getMaxAgeCurrentViewCookie());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1045+" Business Exception occurred while fetching configValue for configKey maxAgeForCurrentViewCookie from method setCurrentViewUsingCookies in SearchDroplet",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1046+" System Exception occurred while fetching configValue for configKey maxAgeForCurrentViewCookie from method setCurrentViewUsingCookies in SearchDroplet",bbbsEx);
		}
		String maxAgeForCookie=null;
		if (configValue != null && !configValue.isEmpty()) {
			maxAgeForCookie = configValue.get(0);
		}

		// Set this view to cookies
		Cookie cookie = new Cookie(CURRENT_VIEW, currentViewParam);

		String domain = pRequest.getServerName();
		String path = PATH;
		cookie.setDomain(domain);
		cookie.setPath(path);
		if(!StringUtils.isBlank(maxAgeForCookie))
		{
			cookie.setMaxAge(Integer.parseInt(maxAgeForCookie));
		}
		BBBUtility.addCookie(pResponse, cookie, true);
		if(isLoggingDebug())
		{
			logDebug("setCurrentViewUsingCookies [END] :cookie  " +cookie);
		}
		
	}
	
	public String createLinkString(final List<BBBProduct> productIDsList) {
		final StringBuilder finalString = new StringBuilder("");
		
		if(productIDsList != null){
			for (BBBProduct productId : productIDsList) {
				finalString.append(productId.getProductID()).append(';');
			}
		}
		return finalString.toString();
	}
	
	/**
	 * Added as part of R2.2 Story - 116-D1 & D2 - PerPage Dropdown list is being fetched from config Key 
	 * Manipulate dropdown list on basis of products being returned from Endeca
	 * View All needs to be shown if endeca returned products <= highest per page count
	 * 
	 * @return List<String> drop down list to be shown for PLP and search page size
	 */
	public void getDropdownList() {
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		SearchResults browseSearchVO = (SearchResults)pRequest.getObjectParameter(BBBSearchBrowseConstants.BROWSE_SEARCH_VO);
		
		long searchResultCount = 0;
		long pageSize = 0;
		if(browseSearchVO != null && browseSearchVO.getBbbProducts() != null && browseSearchVO.getPagingLinks() != null){
			searchResultCount = browseSearchVO.getBbbProducts().getBBBProductCount();
			pageSize = browseSearchVO.getPagingLinks().getPageSize();
			String pageSizeFilter = pRequest.getQueryParameter(PAG_FILTER_OPT);
			String pageSizeCookie = pRequest.getCookieParameter(PAGE_SIZE_COOKIE_NAME);
			
			if(searchResultCount > 0){
				Map<String, Object> dropdownMap = getSearchManager().getPerPageDropdownList(searchResultCount, pageSize, pageSizeFilter, pageSizeCookie);
				if(dropdownMap != null && !dropdownMap.isEmpty()){
					Set<String> keySet = dropdownMap.keySet();
					if(keySet != null){
						for(String key : keySet){
							if(key != null){
								pRequest.setParameter(key, dropdownMap.get(key));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @return the personalStoreMgr
	 */
	public PersonalStoreManager getPersonalStoreMgr() {
		return mPersonalStoreMgr;
	}

	/**
	 * @param pPersonalStoreMgr the personalStoreMgr to set
	 */
	public void setPersonalStoreMgr(PersonalStoreManager pPersonalStoreMgr) {
		mPersonalStoreMgr = pPersonalStoreMgr;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	/**
	 * @param lblTxtTemplateManager the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * @return the siteContextManager
	 */
	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	/**
	 * @param siteContextManager the siteContextManager to set
	 */
	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}
	
}
