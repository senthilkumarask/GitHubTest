/*
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit written consent is prohibited.
 *
 * Created by: Archit Goel
 *
 * Created on: 13-October-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.search.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.browse.vo.BrandsListingVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.search.ISearch;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.seo.CategorySeoLinkGenerator;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * Actually calls Search API based on the Search Query/ keyword paramaters.
 *
 */

public class SearchManager extends BBBGenericService{

	private static final String DROPDOWN_LIST = "dropdownList";
	/* ===================================================== *
 		MEMBER VARIABLES
	 * ===================================================== */
	private ISearch searchHandler;
	private String mFacets;
	private BBBCatalogTools catalogTools;
	private String dimDisplayMapConfig;
	private static final String IS_VIEW_ALL = "isViewAll";
	
	private CategorySeoLinkGenerator categorySeoLinkGenerator;
	public final String CHANNEL_HEADER = "X-bbb-channel";
	private ISearch vendorSearchHandler;

	/**
	 * @return the categorySeoLinkGenerator
	 */
	public CategorySeoLinkGenerator getCategorySeoLinkGenerator() {
		return categorySeoLinkGenerator;
	}

	/**
	 * @param categorySeoLinkGenerator the categorySeoLinkGenerator to set
	 */
	public void setCategorySeoLinkGenerator(
			CategorySeoLinkGenerator categorySeoLinkGenerator) {
		this.categorySeoLinkGenerator = categorySeoLinkGenerator;
	}

	//flag is false till Same day delivery is launched
	private boolean sddLaunched;
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */

	public boolean isSddLaunched() {
		return sddLaunched;
	}

	public void setSddLaunched(boolean sddLaunched) {
		this.sddLaunched = sddLaunched;
	}


	/**
	 * @return vendorSearchHandler
	 */
	public ISearch getVendorSearchHandler() {
		return vendorSearchHandler;
	}

	/**
	 * @param vendorSearchHandler
	 */
	public void setVendorSearchHandler(ISearch vendorSearchHandler) {
		this.vendorSearchHandler = vendorSearchHandler;
	}

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
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public ISearch getSearchHandler() {
		return searchHandler;
	}

	public void setSearchHandler(final ISearch pSearchHandler) {
		this.searchHandler = pSearchHandler;
	}

	/* ===================================================== *
 		STANDARD METHODS
	 * ===================================================== */

	/**
	 * This method calls Search's method to perform Search based on a SearchQuery Object.
	 * 
	 * @param pSearchQuery
	 * @return SearchResults
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public SearchResults performSearch(final SearchQuery pSearchQuery) throws BBBBusinessException,BBBSystemException{

		logDebug("Entering SearchManager.performSearch method.");

		String methodName = "performSearch";
        BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_MANAGER, methodName);
				
		// Actual Method callBoolean.getBoolean(VendorFlagOn)
		SearchResults searchResults;
		String vendorFlagOn = this.getCatalogTools().getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG, BBBCoreConstants.FALSE);
		final String vendorId = (String)ServletUtil.getCurrentRequest().getSession().getAttribute(BBBCoreConstants.VENDOR_PARAM);
		boolean isNonSearchRequest = pSearchQuery.isFromBrandPage() || (BBBUtility.isEmpty(pSearchQuery.getKeyWord()) && pSearchQuery.getCatalogRef()!=null && pSearchQuery.getCatalogRef().get(BBBCoreConstants.CATALOG_REF_ID) != null);
		if(Boolean.valueOf(vendorFlagOn)  && BBBUtility.isNotEmpty(vendorId) && !(isNonSearchRequest)){
	        	ServletUtil.getCurrentRequest().getSession().setAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE, "");
	        	if(BBBUtility.isNotEmpty(pSearchQuery.getKeyWord())){
	        		searchResults = getVendorSearchHandler().performSearch(pSearchQuery);
	        	} else {
	        		//JUP-257 | Handling condition : if redirection from vendor without any search term
	        		return null;
	        	}
        } else {
        	searchResults = getSearchHandler().performSearch(pSearchQuery);
        }
		
        if(isSddLaunched()){
		removeSDDAttributes(searchResults);
        }
       
        // BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		logDebug("Exiting SearchManager.performSearch method.");
		return searchResults;
	}

	/**
	 * Checks SDD eligiblity for PLP, Search and Brand pages for desktop, mobile
	 * and TBS
	 * 
	 * @param searchResults
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void removeSDDAttributes(SearchResults searchResults)
			throws BBBSystemException, BBBBusinessException {
		logDebug("RemoveSDDAttribute time before" + System.currentTimeMillis());
		// BBBH-3526 | STORY 7 | Desktop - PLP, Search and Brand Changes
		String regionPromoAttr;
		long startTime = System.currentTimeMillis();
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION +":removeSDDAttributes");
		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(null);
		
		boolean currentZipEligibility = false;
		final List<String> sddAttributeKeyList = getCatalogTools()
				.getAllValuesForKey(BBBCmsConstants.SDD_KEY,
						BBBCmsConstants.SDD_ATTRIBUTE_LIST);
		List<AttributeVO> sddAttributeList = null;
		AttributeVO attrVO = null;
		String skuAttribute = null;
		String desc = null;
		int priority = 0;
		String attributeDescrip = null;
		boolean sameDayDeliveryFlag = false;
		List<String> hideAttributeList = new ArrayList<String>();
		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(
				BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,
				BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
		if (null != sddEligibleOn) {
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
		
		
		if (!BBBUtility.isListEmpty(sddAttributeKeyList)
				&& null != searchResults
				&& null != searchResults.getBbbProducts()) {
			
			boolean isAkamiOn = false;
			boolean hideAllSDDAttributes = false;
			String akamiOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.HOME_PAGE_CACHING_KEY);
			if (!BBBUtility.isEmpty(akamiOn)) {
				isAkamiOn = Boolean.valueOf(akamiOn);
				logDebug("Akamai Caching config key value:"+isAkamiOn);
			}
			String channelId = BBBUtility.getChannel();
			if (SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BAB_US) || SiteContextManager.getCurrentSiteId().equals(TBSConstants.SITE_TBS_BBB)) {
				hideAllSDDAttributes = true;
			}else if(channelId.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP) || channelId.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)) {
				hideAllSDDAttributes =false;
			}
			else if(null == sessionBean.getCurrentZipcodeVO()|| !sameDayDeliveryFlag || isAkamiOn){
				hideAllSDDAttributes = true;
			}
			
			
			
			
			// fetching the sdd attribute list from bcc
			final String sddAttributes[] = sddAttributeKeyList.get(0).split(
					BBBCoreConstants.COMMA);
			for (BBBProduct bbbProduct : searchResults.getBbbProducts()
					.getBBBProducts()) {
				hideAttributeList =  new ArrayList<String>();
				logDebug("Product ID is " + bbbProduct.getProductID());
				sddAttributeList = bbbProduct.getAttributeVO();
				
				if (!BBBUtility.isListEmpty(sddAttributeList)) {
					for (Iterator<AttributeVO> iterator = sddAttributeList
							.iterator(); iterator.hasNext();) {
						attrVO = iterator.next();
						attrVO.setHideAttribute(false);
						skuAttribute = attrVO.getSkuAttributeId();
						logDebug("skuAttribute id is " + skuAttribute);
						priority = attrVO.getPriority();
						desc = bbbProduct.getAttribute().get(priority);
						attributeDescrip = attrVO.getAttributeDescrip();
						for (final String sddAttributeKey : sddAttributes) {
							if (skuAttribute.equals(sddAttributeKey.trim())) { 
								// Removing SDD attributes for TBS or when session bean doesn't have currentZipCodeVO or when the global flag is off or akamai caching is enabled
								
								
														
								if (hideAllSDDAttributes) {
									logDebug("Removing attriute for product in case of TBS" 
											+ bbbProduct.getProductID()
											+ "& attrribute ID" + skuAttribute);
									attrVO.setHideAttribute(true);	
									
									// in case of tbs
									if (!BBBUtility.isEmpty(desc) && desc.equalsIgnoreCase(attributeDescrip)) {
										hideAttributeList.add(desc);
										
									}
								} else if (null != sessionBean
										&& null != sessionBean
												.getCurrentZipcodeVO()) {
									regionPromoAttr = sessionBean
											.getCurrentZipcodeVO()
											.getPromoAttId();
									currentZipEligibility = sessionBean
											.getCurrentZipcodeVO()
											.isSddEligibility();
									if (!(!StringUtils.isEmpty(regionPromoAttr)
											&& currentZipEligibility && skuAttribute
												.equals(regionPromoAttr))) {
										logDebug("Removing attriute for product"
												+ bbbProduct.getProductID()
												+ "& attrribute ID"
												+ skuAttribute);
										attrVO.setHideAttribute(true);
										
										// Called in case of partial match
										if (!BBBUtility.isEmpty(desc) && desc.equalsIgnoreCase(attributeDescrip)) {
											hideAttributeList.add(desc);
											
											
										}
									}
								}
							}
						}
						
					}
					
				}
				
				bbbProduct.setHideAttributeList(hideAttributeList);
				
			}

		}
		
		long endTime = System.currentTimeMillis();
		logDebug("SearchManager took time to removeSDDAttribute"+ (endTime-startTime));
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION +":removeSDDAttributes");
		logDebug("time after" + System.currentTimeMillis());
	}

	/**
	 * This method calls Search's method to perform Type Ahead Search and return Suggested Departments and Brands.
	 * 
	 * @param facetQuery
	 * @return FacetQueryResults
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public FacetQueryResults performTypeAheadSearch(final FacetQuery pFacetQuery) throws BBBBusinessException,BBBSystemException{

		logDebug("Entering SearchManager.performTypeAheadSearch method.");

		// Performance Monitoring Code.
		// String methodName = "performTypeAheadSearch";
        //BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		// Actual Method call
		FacetQueryResults pFQResults = getSearchHandler().performFacetSearch(pFacetQuery);

		//BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		logDebug("Exiting SearchManager.performTypeAheadSearch method.");
		return pFQResults;
	}

	/**
	 * This method calls Search's method to get list of all Brands.
	 * 
	 * @return BrandsListingVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public BrandsListingVO getAllBrands() throws BBBBusinessException,BBBSystemException{

		logDebug("Entering SearchManager.getAllBrands method.");

		//String methodName = "getAllBrands";
        //BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

        /*
        //Query Construct
        final FacetQuery pFacetQuery = new FacetQuery();
		pFacetQuery.setSiteId(SiteContextManager.getCurrentSiteId());
		List<String> pFacets = new ArrayList<String>();
		pFacets.add("BRAND");
		pFacetQuery.setFacets(pFacets);

		//Actual Method call
		final FacetQueryResults pFQResults = getSearchHandler().performFacetSearch(pFacetQuery);

		List<FacetQueryResult> pList = pFQResults.getResults();

		// Since there will be only one entry in pList as search is for Brand facet only.
		FacetQueryResult pFacetQueryResult = null;
		*/
        //Query Construct
		String pSiteId = SiteContextManager.getCurrentSiteId();
        //Actual Method call
		final BrandsListingVO brandsListingVO = getSearchHandler().getBrands((getCatalogId("MerchandisingPages", "BrandPage")),pSiteId);

		// Process the response from FacetQueryResult type to BrandsListingVO type.


       /* // Actual Search Method Call.
     	final BrandsListingVO brandsListingVO = getSearchHandler().getAllBrands();*/

		//BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

        logDebug("Exiting SearchManager.getAllBrands method.");
		return brandsListingVO;
	}

	/**
	 * This method is to get the list of all products in a given category Id.
	 *
	 * @param pCategoryId
	 * @return EDCProdListDataVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public Map<String,CategoryParentVO> getCategoryTree(final SearchQuery pSearchQuery)
			throws BBBBusinessException, BBBSystemException{

		logDebug("Entering SearchManager.getCategoryTree method.");

		//String methodName = "getCategoryTree";
        //BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		// Actual Search Method Call.
		Map<String,CategoryParentVO> map = getSearchHandler().getCategoryTree(pSearchQuery);

		//BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		logDebug("Exiting SearchManager.getCategoryTree method.");
		return map;
	}





	public String getCatalogId(String parentName, String dimName) throws BBBBusinessException, BBBSystemException{
		return getSearchHandler().getCatalogId(parentName, dimName);
	}

	/**Method to Invoke getAllColleges on Endeca Search Component
	 * @param pCatalogId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<CollegeVO> getAllColleges(String pCatalogId) throws BBBBusinessException, BBBSystemException {
		logDebug("getAllCollege():: Search Manager");
		String pSiteId = SiteContextManager.getCurrentSiteId();
		final List<CollegeVO> lstCollege = getSearchHandler().getColleges(pCatalogId,pSiteId);
		logDebug("Exiting SerachManager.getAllColleges()");
		return lstCollege;
	}

	/**Method to Invoke getStates on Endeca Search Component
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<StateVO> getStates() throws BBBBusinessException, BBBSystemException {
		String stateDimension=null;
		String pSiteId = SiteContextManager.getCurrentSiteId();
		final List<StateVO> lstState = getSearchHandler().getStates("0",pSiteId);
		logDebug("Exiting SerachManager.getStates()");
		return lstState;
	}

	public String getFacets() {
		try {
			Map<String,String> dimDisplayMap = getCatalogTools().getConfigValueByconfigType(getDimDisplayMapConfig());
			mFacets = getCommaSeparatedDim(dimDisplayMap);
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1014+" Business Exception occurred while fetching dimensions display list from  getFacets from SearchDroplet",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1015+" System Exception occurred while fetching dimensions display list from  getFacets from SearchDroplet",bbbsEx);
		}
		//System.out.println("facets list in Search Droplet is: "+mFacets);
		return mFacets;
	}

	// Method to take Map as input and return a comma separated String for Unique Values of the map.
	private String getCommaSeparatedDim(Map<String,String> map1){
		String commaString = null;
		if(null != map1){
			List<String> list = new ArrayList<String>(map1.values());
			Map<String,String> map = new HashMap<String, String>();
			for(String value: list){
				map.put(value, null);
			}
			Iterator it = map.keySet().iterator();
			String facets = null;
			String newCommaFacets = new String();// NOPMD: TODO: This PMD need be fixed with StringBuffer
			while(it.hasNext()){
				facets = (String)it.next();
				newCommaFacets += facets + ",";
			}
			if(null != newCommaFacets){
				newCommaFacets = newCommaFacets.substring(0, newCommaFacets.length()-1);
			}
			commaString = newCommaFacets;
		}
		return commaString;
	}

	/**
	 * Added as part of R2.2 Story - 116-D1 & D2 - PerPage Dropdown list is being fetched from config Key
	 * Manipulate dropdown list on basis of products being returned from Endeca
	 * View All needs to be shown if endeca returned products <= highest per page count
	 *
	 *
	 * @param searchResultCount - Endeca Returned Product count
	 * @param pageSize - Endeca returned Page size value
	 * @param pageSizeFilter - User selected page size
	 * @param pageSizeCookie - Page size stored in cookie
	 *
	 * @return List<String> drop down list to be shown for PLP and search page size
	 *
	 */
	public Map<String, Object> getPerPageDropdownList(long searchResultCount, long pageSize, String pageSizeFilter, String pageSizeCookie) {

		logDebug("SearchManager.getPerPageDropdownList : START");
		List<String> dropdownList = fetchPerPageDropdownList();
		Map<String, Object> dropdownMap = null;

		if(dropdownList != null && searchResultCount > 0){

			dropdownMap = new HashMap<String, Object>();
			String highest = dropdownList.get(dropdownList.size() - 1);
			logDebug("highest Per Page dropdown value: " + highest);
			boolean isResultCountLessThanMax = searchResultCount <= Long.parseLong(highest);

			//Replace highest value with "View All" if endeca returned product <= highest value
			if(isResultCountLessThanMax){
				logDebug("Endeca returned product count <= highest page size dropdown value");
				if(dropdownList.contains(highest)){
					dropdownList.remove(dropdownList.size() - 1);
					dropdownList.add(BBBCatalogConstants.VIEW_ALL);
					logDebug("Page size Dropdown List after manipulation" + dropdownList);

				}
			}
			dropdownMap.put(DROPDOWN_LIST, dropdownList);

			if(BBBUtility.isEmpty(pageSizeFilter) && !BBBUtility.isEmpty(pageSizeCookie) ){
				logDebug("pageSizeCookie - " + pageSizeCookie);
				if((pageSizeCookie.equalsIgnoreCase(highest) || !dropdownList.contains(pageSizeCookie))
						&& isResultCountLessThanMax){
					dropdownMap.put(IS_VIEW_ALL,BBBCoreConstants.TRUE);
				} else {
					dropdownMap.put(IS_VIEW_ALL,BBBCoreConstants.FALSE);
				}
			}
			else if (!BBBUtility.isEmpty(pageSizeFilter) && dropdownList.contains(pageSizeFilter)){
				dropdownMap.put(IS_VIEW_ALL,BBBCoreConstants.FALSE);
			}
			else if(dropdownList.contains(String.valueOf(pageSize))){
				// if pagesize filter and pagesize cookie is empty then check page size which is returned from Endeca
				dropdownMap.put(IS_VIEW_ALL,BBBCoreConstants.FALSE);
			}
			else if(isResultCountLessThanMax){
				dropdownMap.put(IS_VIEW_ALL,BBBCoreConstants.TRUE);
			}
			else{
				dropdownMap.put(IS_VIEW_ALL,BBBCoreConstants.FALSE);
			}
		}

		logDebug("SearchManager.getPerPageDropdownList : END");
		return dropdownMap;
	}

	/**
	 * Added as part of R2.2 Story - 116-D1 & D2
	 * Fetching per page dropdown values of page size from config key and converting to List<String>. This page size will be used to show drop
	 * down of page size on PLP and search result page.
	 *
	 * @return List<String> list of page size drop down values.
	 *
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


	/**
	 * This method is used to filter out the L2 and L3's from the category Tree
	 * @param catalogRefId
	 * @param categoryTree
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, CategoryParentVO> filterL2L3ExclusionItemsFromCategoryTree(String catalogRefId, Map<String, CategoryParentVO> categoryTree)
			throws BBBSystemException, BBBBusinessException {
		logDebug("Entering SearchManager.filterL2L3ExclusionItemsFromCategoryTree method. "
				+ "For catalogRefId: "+ catalogRefId + " and categoryTree: "+ categoryTree);

				Map<String, CategoryParentVO> populateCategoryTree = new LinkedHashMap<String, CategoryParentVO>();

				// Fetching L2 exclusion list
				List<String> exclusionListL2 = getCatalogTools().getBccManagedCategoryList(1, catalogRefId);
				logDebug("List of L2 in exclusion list : " + exclusionListL2 + " for L1: " + catalogRefId);

				for (Map.Entry<String, CategoryParentVO> categoryItemL2 : categoryTree.entrySet()) {
					String categoryIdL2 = categoryItemL2.getKey();
					CategoryParentVO categoryParentVO = categoryItemL2.getValue();

					//Checking for current L2 is in L2 exclusion lists & add that L2 if it is not in L2 exclusion lists
					if (!exclusionListL2.contains(categoryIdL2)) {
						logDebug("Adding L2: " + categoryIdL2);
						addL2L3NotInExclusionList(populateCategoryTree,
								categoryIdL2, categoryParentVO);
					}
				}

			logDebug("Exiting SearchManager.filterL2L3ExclusionItemsFromCategoryTree method. with "
					+ "categoryTree: "+ populateCategoryTree);
			return populateCategoryTree;
	}

	/**
	 * This method checks if there are L3 exclusion items for L2 and populates L2, L3 accordingly.
	 * @param populateCategoryTree
	 * @param categoryIdL2
	 * @param categoryParentVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void addL2L3NotInExclusionList(Map<String, CategoryParentVO> populateCategoryTree,
			String categoryIdL2, CategoryParentVO categoryParentVO) throws BBBSystemException, BBBBusinessException {

		List<String> exclusionListL3 = getCatalogTools().getBccManagedCategoryList(2, categoryIdL2);
		logDebug("List of L3 in exclusion list : " + exclusionListL3 + " for L2: " + categoryIdL2);

		CategoryParentVO populatCategoryParentVO = new CategoryParentVO();
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		String channel = request.getHeader(BBBCoreConstants.CHANNEL);
		logDebug("addL2L3NotInExclusionList: The request is from channel" + channel );
		if (exclusionListL3.size() > 0) {
			//Fetching L3 items not in exclusion list.
			List<CategoryRefinementVO> populateCategoryRefinementVOList =
					fetchL3ItemsNotInExclusionList(populateCategoryTree, categoryIdL2,
					categoryParentVO, exclusionListL3,channel);
			//populating L2 with filtered L3 items
			populatCategoryParentVO.setCategoryRefinement(populateCategoryRefinementVOList);
			populatCategoryParentVO.setIsMoreLinkRequired(true);
		} else {
			//populating the original L3 list if no L3 items in exclusion list
			populatCategoryParentVO.setCategoryRefinement(categoryParentVO.getCategoryRefinement());
			populatCategoryParentVO.setIsMoreLinkRequired(false);
		}

		populatCategoryParentVO.setName(categoryParentVO.getName());
		populatCategoryParentVO.setQuery(categoryParentVO.getQuery());
		populatCategoryParentVO.setIsPortraitEligible(categoryParentVO.getIsPortraitEligible());
		populatCategoryParentVO.setCategoryTree(categoryParentVO.getCategoryTree());
		populatCategoryParentVO.setPhantomCategory(categoryParentVO.getPhantomCategory());
		//BSL-5841 changes start
		if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))
				&& BBBUtility.isListEmpty(categoryParentVO.getCategoryRefinement()))
		{
			if(categoryParentVO.getQuery() != null) {
			populatCategoryParentVO.setCategorySEOUrl(this.getCategorySeoLinkGenerator().formatUrl(categoryParentVO.getQuery(),
					categoryParentVO.getName()));
			logDebug("addL2L3NotInExclusionList: Category parent vo query:: " + categoryParentVO.getQuery() + "::Category parent vo name::"  + categoryParentVO.getName());
			}
		}
		
		populateCategoryTree.put(categoryIdL2, populatCategoryParentVO);
	}

	/**
	 * This method fetches and populates list of L3 items not in exclusion list.
	 * @param populateCategoryTree
	 * @param categoryIdL2
	 * @param categoryParentVO
	 * @param exclusionListL3
	 */
	public List<CategoryRefinementVO> fetchL3ItemsNotInExclusionList(Map<String, CategoryParentVO> populateCategoryTree,
			String categoryIdL2, CategoryParentVO categoryParentVO,	List<String> exclusionListL3,String channel) {

		List<CategoryRefinementVO> categoryRefinementVOList = categoryParentVO.getCategoryRefinement();
		List<CategoryRefinementVO> populateCategoryRefinementVOList = new ArrayList<CategoryRefinementVO>();
		for (CategoryRefinementVO categoryRefinementVO : categoryRefinementVOList) {
			//Checking for current L3 is in L3 exclusion lists & add that L3 if it is not in L3 exclusion lists
			if (!exclusionListL3.contains(categoryRefinementVO.getQuery())) {
				logDebug("Adding L3: " + categoryRefinementVO.getQuery());

				CategoryRefinementVO populateCategoryRefinementVO =new CategoryRefinementVO();
				populateCategoryRefinementVO.setName(categoryRefinementVO.getName());
				populateCategoryRefinementVO.setQuery(categoryRefinementVO.getQuery());;
				populateCategoryRefinementVO.setSize(categoryRefinementVO.getSize());;
				populateCategoryRefinementVO.setIsPortraitEligible(categoryRefinementVO.getIsPortraitEligible());
				populateCategoryRefinementVO.setTotalSize(categoryRefinementVO.getTotalSize());
				//BSL-5841 changes start
				if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))
						&& (populateCategoryRefinementVO.getQuery() != null)){
								logDebug("getRootCategoryNavigation: Getting the SEO URL for category : " + populateCategoryRefinementVO.getQuery() + populateCategoryRefinementVO.getName());
								populateCategoryRefinementVO.setSubCatURL(this.getCategorySeoLinkGenerator().formatUrl(populateCategoryRefinementVO.getQuery(),
										populateCategoryRefinementVO.getName()));
						}
				populateCategoryRefinementVOList.add(populateCategoryRefinementVO);
			}
		
		}
		return populateCategoryRefinementVOList;
	}

	
	
}

