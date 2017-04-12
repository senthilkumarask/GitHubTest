package com.bbb.search.droplet;
/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  SearchDropletSpecification.groovy
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Aug 5, 2016  Initial version
*/


import javax.servlet.http.Cookie
import spock.lang.specification.BBBExtendedSpec
import com.bbb.redirectURLs.CategoryRedirectURLLoader;
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.comparison.BBBProductComparisonList
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.personalstore.manager.PersonalStoreManager
import com.bbb.search.bean.query.SearchQuery
import com.bbb.search.bean.result.Asset
import com.bbb.search.bean.result.AutoSuggestVO
import com.bbb.search.bean.result.BBBProduct
import com.bbb.search.bean.result.BBBProductList
import com.bbb.search.bean.result.CategoryParentVO
import com.bbb.search.bean.result.FacetParentVO
import com.bbb.search.bean.result.PaginationVO
import com.bbb.search.bean.result.SearchResults
import com.bbb.search.integration.SearchManager

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class SearchDropletSpecification extends BBBExtendedSpec {
	
	def SearchManager searchManagerMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def PersonalStoreManager personaleStoreManagerMock = Mock()
	def BBBProductComparisonList productComparisonListMock = Mock(BBBProductComparisonList)
	def CompareProductEntryVO compareProductEntryVO
	def LblTxtTemplateManager lblTxtTempManagerMock = Mock()
	def SearchResults browseSearchVOMock = Mock()
	def BBBProductList bbbProductListMock = Mock()
	def FacetParentVO facetParentVODeptMock = Mock()
	def FacetParentVO facetParentVOColorMock = Mock()
	def PaginationVO paginationVOMock = Mock()
	def Asset otherAssetMock = Mock()
	def Asset mediaAssetMock = Mock()
	def Asset guideAssetMock = Mock()
	def BBBProduct bbbProductMock = Mock()
	def CategoryVO categoryVOMock = Mock()
	def AutoSuggestVO autoSuggestVOMock = Mock()
	def CategoryParentVO categoryParentVOMock = Mock()
	def CategoryRedirectURLLoader categoryRedirectURLLoaderMock = Mock()
	def SearchDroplet testObj
	def SearchQuery searchQueryMock = Mock()	
	def Cookie cookieSearchModeMock = Mock()
	def Cookie cookieSomeMock = Mock()
	
	def setup() {
		cookieSearchModeMock.getName() >> "searchMode"
		cookieSearchModeMock.getValue() >> "test"
		cookieSomeMock.getName() >> "some"
		cookieSomeMock.getValue() >> ""

		def siteIdMapValue=["BedBathUS":1,"BedBathCanada":3,"BuyBuyBaby":"2","GS_BedBathUS":4,"GS_BedBathCanada":6,"GS_BuyBuyBaby":5]//short cut for a Map
		//testObj creation, this could be a Spy if we need to mock the methods from test class, it supports DSL
		testObj = new SearchDroplet(maxAgeCurrentViewCookie:"MaxAgeCurrentViewCookie",
			defaultView:"grid", searchDimConfig:"DimConfig", headerFacetsDim:"HeaderDimension", dimNonDisplayMapConfig:"DimNonDisplayConfig",
			dimDisplayMapConfig:"DimDisplayConfig", siteIdMap:siteIdMapValue, searchManager:searchManagerMock, catalogTools:catalogToolsMock,
			personalStoreMgr:personaleStoreManagerMock, lblTxtTemplateManager:lblTxtTempManagerMock, categoryRedirectURL:categoryRedirectURLLoaderMock)
		
		testObj.setLoggingDebug(true)		
		
		//adding mock for productComparisonList	
		requestMock.resolveName("/atg/commerce/catalog/comparison/ProductComparisonList") >> productComparisonListMock
		compareProductEntryVO = new CompareProductEntryVO()		
	}
	
	def "Search Droplet Performing Internal Redirect with Method as Post"() {
		given:
		 //mocking methods
		 
		 requestMock.getMethod() >> "POST"
		
		when:
		 testObj.service(requestMock,responseMock)//calling the test methods
		 
		then:
		 //checks redirect is set in response
		1 * responseMock.sendLocalRedirect(_, requestMock)
	}
	
	def "Search Droplet Performing Internal Redirect with Method as Post with query parameters and redirect url"() {
		given:
		 //mocking methods
		 testObj.setLoggingDebug(false)
		 requestMock.getMethod() >> "POST"
		 requestMock.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING) >> "search=full";
		 requestMock.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING) >> "http://test.com"
		when:
		 testObj.service(requestMock,responseMock)//calling the test methods
		 
		then:
		 //checks redirect is set in response
		1 * responseMock.sendLocalRedirect("http://test.com?search=full", requestMock)
	}
	
	def "Search Droplet Performing Internal Redirect with GetMethod as GET and search term < 2 and is brand search."() {
		given:
		 //mocking methods
		 catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		 requestMock.getServerName() >> "cookieDomain"
		 responseMock.getResponse() >> responseResponseMock
		 requestMock.getMethod() >> "GET"
		 responseResponseMock.addHeader(_,_) >> {}
		 
		 requestMock.getParameter("origSearchTerm") >> "T"
		 requestMock.getQueryParameter("frmBrandPage") >> "true"
		 
		 searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		when:
		 testObj.service(requestMock,responseMock)//calling the test methods
		 
		then:
		 //checks unmetSearchCriteria is not set in request. 
		0 * requestMock.serviceLocalParameter("unmetSearchCriteria", requestMock, responseMock);
	}
	
	def "Search Droplet Performing Internal Redirect with GetMethod as GET and search term is < 2 and is not brand search."() {
		given:
		 //mocking methods
		 catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		 requestMock.getServerName() >> "cookieDomain"
		 responseMock.getResponse() >> responseResponseMock
		 requestMock.getMethod() >> "GET"
		 responseResponseMock.addHeader(_,_) >> {}
		 
		 requestMock.getParameter("origSearchTerm") >> "T"
		 
		 searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		when:
		 testObj.service(requestMock,responseMock)//calling the test methods
		 
		then:
		 //checks unmetSearchCriteria is set in request. 
		1 * requestMock.serviceLocalParameter("unmetSearchCriteria", requestMock, responseMock);
	}
		
	def "Search Droplet Search method happy path with no Keyword, isHeader, isRedirect, babyItems, pagNum, pagSortOpt, pagFilterOpt, appendProdDimId"() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		requestMock.getParameter("partialFlag") >> "partialFlag"
		requestMock.getParameter("CatalogId") >> "catalog10002"
		requestMock.getParameter("CatalogRefId") >> "40979960"
		requestMock.getParameter("isRedirect") >> false
		requestMock.getParameter("additionalKeyword") >> "additionalKeyword"
		requestMock.getCookieParameter("pageSizeFilter") >> "15"
		requestMock.getParameter("savedUrl") >> "urlCookie"
		requestMock.getParameter("babyItems") >> "false"
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("isHeader") >> "Y"		
				
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
		
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.partialFlag == 'partialFlag' && 
			 searchQuery.catalogRef.get("catalogId") == 'catalog10002' && searchQuery.catalogRef.get("catalogRefId") == '40979960' &&
			 searchQuery.isRedirected() == false && searchQuery.pageSize == '101' && searchQuery.siteId == 'BedBathUS' &&
			 searchQuery.headerSearch == true})
	}
	
	def "Search Droplet with Keyword stored as unescapeHtml in SearchQuery. This TC tests for Keyword property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("Keyword") >> "Sheets&lt;"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery	and keyword as 'Sheets<'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.keyWord == 'Sheets<'})
	}
	
	def "Search Droplet with Keyword stored is session."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		sessionMock.getAttribute("origSearchTerm") >> "old Search"
		requestMock.getParameter("storeId") >> "store 1"
		requestMock.getParameter("storeIdFromAjax") >> "store 2"
		requestMock.getParameter("onlineTab") >> "true"
		requestMock.getParameter("refType") >> "refType"
		
		
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery	and keyword as 'Sheets<'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.keyWord == 'old Search' && searchQuery.onlineTab == true && searchQuery.storeId == 'store 1' && searchQuery.storeIdFromAjax == 'store 2'})
	}
	
	def "Search Droplet with isRedirect stored as true in SearchQuery. This TC tests for isRedirect in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("isRedirect") >> true
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and isRedirected as true
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.isRedirected()})
	}
	
	def "Search Droplet with isHeader stored as true in SearchQuery. This TC tests for isHeaderSearch property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("isHeader") >> "Y"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and isHeaderSearch as true
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.isHeaderSearch()})
	}
	
	def "Search Droplet with isHeader stored as false in SearchQuery. This TC tests for isHeaderSearch property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("isHeader") >> "N"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and isHeaderSearch as false
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && !searchQuery.isHeaderSearch()})
	}
	
	def "Search Droplet with SiteId stored as BedBathUS in SearchQuery. This TC tests for siteId property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("siteId") >> "BedBathUS"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and siteId as 'BedBathUS'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.siteId == 'BedBathUS'})
	}
	
	def "Search Droplet with babyItems and BuyBuyBaby stored in SearchQuery as siteId. This TC tests for siteId property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("babyItems") >> "true"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and siteId as 'BuyBuyBaby'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.siteId == 'BuyBuyBaby'})
	}
	
	def "Search Droplet with searchMode stored in SearchQuery from Cookie. This TC tests for searchMode property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getCookies() >> [cookieSomeMock, cookieSearchModeMock]
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and isHeaderSearch as false
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.searchMode == 'test'})
	}
	
	def "Search Droplet with pagNum stored as 5 in SearchQuery. This TC tests for pagNum property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("pagNum") >> 5
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageNum as '5'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.pageNum == '5'})
		
	}
	
	def "Search Droplet with pagNum = 0 and will throw 404 Error. This TC tests for Error Condition."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("pagNum") >> 0
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the 404 Error is thrown as Response
		1 * requestMock.serviceParameter("error_PageNumOutOfBound", requestMock, responseMock)
        1 * responseMock.setStatus(404)
	}
	
	def "Search Droplet with requesturi catalogId-,ajaxifyFilters true. Sets filtersonLoad true,removes ajaxifyFilters in redirectUrl."() {
		
		given:	
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("CatalogId") >> "13109"	
		requestMock.getParameter("ajaxifyFilters") >> "true"
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING) >> "pagSortOpt=DEFAULT-0&view=grid&refType=true&ajaxifyFilters=true&_requestid=1335"
		requestMock.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING) >> "/store/category/bedding/bedding-basics/13109/13109-4294966331-4294966809/1-48"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
	
		categoryRedirectURLLoaderMock.getCategoryRedirectURLMap() >> ["firstName":"John"]
		requestMock.getResponse() >>   responseMock
		when:
		
		testObj.service(requestMock,responseResponseMock)//calling the test methods
		
		then:
		//checks that the filtersonLoad is set to true in request
		1 * requestMock.setParameter("filtersonLoad", "true")
		1 * requestMock.setParameter("redirectUrl", "/store/category/bedding/bedding-basics/13109/13109-4294966331-4294966809/1-48?pagSortOpt=DEFAULT-0&view=grid&refType=true")
	}
	
	def "Search Droplet with ajaxifyFilters parameter is false. This should set the status in response as 404"() {
		
		given:
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("CatalogId") >> "13109"
		requestMock.getParameter("ajaxifyFilters") >> "false"
		requestMock.getQueryParameter("narrowDown") >> "fl_Sheets::bed"
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING) >> "pagSortOpt=DEFAULT-0&view=grid&refType=true&ajaxifyFilters=true&_requestid=1335"
		requestMock.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING) >> "/store/category/bedding/bedding-basics/13109/13109-4294966331-4294966809/1-48"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		categoryRedirectURLLoaderMock.getCategoryRedirectURLMap() >> [firstName:"John"]
		requestMock.getResponse() >>   responseMock
		when:
		
		testObj.service(requestMock,responseResponseMock)//calling the test methods
		
		then:
		//checks that the filtersonLoad is set to true in request
		1 * responseMock.setStatus(404)
		
	}
	
	def "Search Droplet with fromCollege stored as true in SearchQuery. This TC tests for fromCollege property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("fromCollege") >> true
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and fromCollege as true
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.fromCollege == true})
	}
	
	def "Search Droplet with CatalogId stored as catalogRef in SearchQuery. This TC tests for catalogId property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("CatalogId") >> "catalog10002"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and catalogId as 'catalog10002'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.catalogRef.get("catalogId") == 'catalog10002'})
	}
	
	def "Search Droplet with CatalogRefId stored as catalogRef in SearchQuery. This TC tests for catalogRefId property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("CatalogRefId") >> "40979960"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and catalogRefId as '40979960'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.catalogRef.get("catalogRefId") == '40979960'})
	}
	
	def "Search Droplet with CatalogId & CatalogRefId stored as catalogRef in SearchQuery passed as a QueryParameter. This TC tests for catalogId & catalogRefId properties in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("CatalogId") >> "catalog10002"
		requestMock.getQueryParameter("CatalogRefId") >> "40979960"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and catalogRefId as '40979960'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.catalogRef.get("catalogId") == 'catalog10002' &&
			 searchQuery.catalogRef.get("catalogRefId") == '40979960'})
	}
	
	def "Search Droplet with categoryId and pagSortOpt with '-' for sorting stored as sortAscending=false in SearchQuery. This TC tests for sortAscending & sortFieldName properties in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("pagSortOpt") >> "11-1"
		requestMock.getQueryParameter("categoryId") >> "22507"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
		
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortAscending in sortCriteria as false
		//requestMock.getSession().getAttribute("pagSortOptSess") >> "11-5"
		 
		//1 * (String)requestMock.getSession().getAttribute("pagSortOptSess")
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortAscending == false && searchQuery.sortCriteria.sortFieldName == '11'})
	}
	
	def "Search Droplet with categoryId and pagSortOpt with '-' for sorting stored as sortAscending=true in SearchQuery. This TC tests for sortAscending & sortFieldName properties in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("pagSortOpt") >> "11-5"
		requestMock.getQueryParameter("categoryId") >> "22507"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortAscending in sortCriteria as true		
		1 * sessionMock.setAttribute("pagSortOptSess", "11-5")		
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortAscending == true && searchQuery.sortCriteria.sortFieldName == '11'})
	}
	
	def "Search Droplet with categoryId and pagSortOpt without '-' for sorting stored as sortFieldName!=11 in SearchQuery. This TC tests for sortFieldName property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("pagSortOpt") >> "115"
		requestMock.getQueryParameter("categoryId") >> "22507"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortFieldName in sortCriteria as '11'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortFieldName != '11'})
	}
	
	def "Search Droplet with categoryId and pagSortOptSess from session stored as sortFieldName=11 in SearchQuery. This TC tests for sortFieldName property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"

		1 * sessionMock.getAttribute("pagSortOptSess") >> "11-1"
		requestMock.getQueryParameter("categoryId") >> "22507"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortFieldName in sortCriteria as '11'
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortFieldName == '11'})
	}
	
	def "Search Droplet with bccSortCode and bccSortOrder = '1' stored as sortAscending=false in SearchQuery. This TC tests for sortAscending & sortFieldName properties in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("bccSortCode") >> "5"
		requestMock.getParameter("bccSortOrder") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortAscending in sortCriteria as false
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortAscending == false && searchQuery.sortCriteria.sortFieldName == '5'})
	}
	
	def "Search Droplet with bccSortCode and bccSortOrder != '1' stored as sortAscending=true in SearchQuery. This TC tests for sortAscending & sortFieldName properties in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("bccSortCode") >> "5"
		requestMock.getParameter("bccSortOrder") >> "8"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortAscending in sortCriteria as true
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortAscending == true && searchQuery.sortCriteria.sortFieldName == '5'})
	}
	
	def "Search Droplet without bccSortOrder stored as sortAscending=true in SearchQuery. This TC tests for sortAscending & sortFieldName properties in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("bccSortCode") >> "5"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and sortAscending in sortCriteria as true
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.sortCriteria.sortAscending == true && searchQuery.sortCriteria.sortFieldName == '5'})
	}
	
	def "Search Droplet with Keyword and empty CatalogRef and CatalogId stored as catalogRef in SearchQuery. This TC tests for catalogRef property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		Map<String, String> catalogRef = new HashMap<String, String>()
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("origSearchTerm") >> "Toys"
		//requestMock.getQueryParameter("CatalogId") >> "5555656"
		//requestMock.getQueryParameter("CatalogRefId") >> "40979960"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is not executed with catalogRef.
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.catalogRef.get("CatalogId") == null})
	}
	
	def "Search Droplet with Keyword and  CatalogRef and CatalogId stored as catalogRef in SearchQuery. This TC tests for catalogRef property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		Map<String, String> catalogRef = new HashMap<String, String>()
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("origSearchTerm") >> "Toys"
		requestMock.getQueryParameter("CatalogId") >> "5555656"
		requestMock.getQueryParameter("CatalogRefId") >> "40979960"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is not executed with catalogRef.
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.catalogRef.get("CatalogId") == null})
	}
	
	def "Search Droplet with pagFilterOpt contained in fetchPerPageDropdownList stored as PageSize=5 in SearchQuery. This TC tests for pageSize property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		searchManagerMock.fetchPerPageDropdownList() >> ["101", "5"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 5
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.pageSize == '5'})
	}
	
	def "Search Droplet with pagFilterOpt not contained in fetchPerPageDropdownList stored as PageSize=55(highest) in SearchQuery. This TC tests for pageSize property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		searchManagerMock.fetchPerPageDropdownList() >> ["101", "55"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 55
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.pageSize == '55'})
	}
	
	def "Search Droplet with pageSizeFilter from Cookie contained in fetchPerPageDropdownList stored as PageSize=55 in SearchQuery. This TC tests for pageSize property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getCookieParameter("pageSizeFilter") >> "101"
		searchManagerMock.fetchPerPageDropdownList() >> ["101", "55"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 55
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.pageSize == '101'})
	}
	
	def "Search Droplet with valid IPAD_PATTERN to retrieve default_per_page from BCC. This TC tests for invocation of getAllValuesForKey method in CatalogTools."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getHeader("User-Agent") >> "/5.0 (iPad; U; CPU OS 3_2_1)"
		searchManagerMock.fetchPerPageDropdownList() >> [""]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getAllValuesForKey from CatalogToolsMock is invoked to retrieve default_per_page for valid IPAD_PATTERN.
		1 * catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "per_page_default_ipad")
	}
	
	def "Search Droplet with invalid IPAD_PATTERN to retrieve default_per_page from BCC. This TC tests for invocation of getAllValuesForKey method in CatalogTools."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getHeader("User-Agent") >> "/5.0 (U; CPU OS)"
		searchManagerMock.fetchPerPageDropdownList() >> [""]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getAllValuesForKey from CatalogToolsMock is invoked to retrieve default_per_page for default pattern.
		1 * catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "per_page_default")
	}
	
	def "Search Droplet with default_per_page from BCC stored as PageSize in SearchQuery. This TC tests for pageSize property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT) >> ["55"]
		searchManagerMock.fetchPerPageDropdownList() >> [""]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 55
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.pageSize == '55'})
	}
	
	def "Search Droplet with empty default_per_page from BCC stored as PageSize in SearchQuery. This TC tests for pageSize property in SearchQuery."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT) >> []
		searchManagerMock.fetchPerPageDropdownList() >> [""]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is not executed with searchquery and pageSize as 55
		0 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType' && searchQuery.pageSize == '55'})
	}
	
	def "Search Droplet with appendProdDimId stored as PageSize in SearchQuery. This TC tests for pageSize property being set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("appendProdDimId") >> "Y"
		searchManagerMock.fetchPerPageDropdownList() >> ["101"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 1-101 is set in request
		1 * requestMock.setParameter("pageSize", "1" +"-"+"101");
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType'})
	}
	
	def "Search Droplet with appendProdDimId and pagFilterOpt stored as PageSize in SearchQuery. This TC tests for pageSize property being set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("appendProdDimId") >> "Y"
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		searchManagerMock.fetchPerPageDropdownList() >> ["101", "25"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 1-25 is set in request
		1 * requestMock.setParameter("pageSize", "1" +"-"+"25");
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType'})
	}
	
	def "Search Droplet with appendProdDimId as N and pagFilterOpt stored as PageSize in SearchQuery. This TC tests for pageSize property being set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("appendProdDimId") >> "N"
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		searchManagerMock.fetchPerPageDropdownList() >> ["101", "25"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the performSearch is executed with proper searchquery and pageSize as 1-25 is not set in request
		0 * requestMock.setParameter("pageSize", "1" +"-"+"25");
		1 * searchManagerMock.performSearch({ SearchQuery searchQuery -> searchQuery.refType == 'refType'})
	}
	
	def "Search Droplet with browseSearchVO. Will throw 404 Error. This TC tests for Error Condition."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}		
		requestMock.getParameter("refType") >> "refType"
				
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]				
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		requestMock.getQueryParameter("pagNum") >> "10"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the 404 Error is thrown as Response
		1 * requestMock.serviceParameter("error_PageNumOutOfBound", requestMock, responseMock);
		1 * responseMock.setStatus(404)
	}
		
	def "Search Droplet with browseSearchVO with paginglinks. This TC test the pageDropdown being set in request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.setDescription("Sheets")
		bbbProduct.setProductID("654464")
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProduct]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.pagingLinks >> paginationVOMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.getPerPageDropdownList(_,_,_,_) >> ["isViewAll":"true"]
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getObjectParameter("browseSearchVO") >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the pageDropDown is set in request.
		1 * requestMock.setParameter("isViewAll", "true")
	}
	
	def "Search Droplet with browseSearchVO with paginglinks and productCount as 0. This TC test the pageDropdown being set in request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.setDescription("Sheets")
		bbbProduct.setProductID("654464")
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProduct]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.pagingLinks >> paginationVOMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.getPerPageDropdownList(_,_,_,_) >> ["isViewAll":"true"]
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getObjectParameter("browseSearchVO") >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the pageDropDown is set in request.
		0 * requestMock.setParameter("isViewAll", "true")
	}
	
	def "Search Droplet with browseSearchVO with paginglinks and empty dropdownList. This TC test the pageDropdown being set in request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.setDescription("Sheets")
		bbbProduct.setProductID("654464")
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProduct]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.pagingLinks >> paginationVOMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.getPerPageDropdownList(_,_,_,_) >> [:]
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getObjectParameter("browseSearchVO") >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the pageDropDown is set in request.
		0 * requestMock.setParameter("isViewAll", "true")
	}
	
	def "Search Droplet with browseSearchVO with paginglinks and no dropdownList. This TC test the pageDropdown being set in request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.setDescription("Sheets")
		bbbProduct.setProductID("654464")
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProduct]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.pagingLinks >> paginationVOMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getObjectParameter("browseSearchVO") >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "25"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the pageDropDown is set in request.
		0 * requestMock.setParameter("isViewAll", "true")
	}
	
	def "Search Droplet with browseSearchVO. Will set redirectToParent to request. This TC tests for redirectToParent property being set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getQueryParameter("CatalogId") >> "catalog10002"
		requestMock.getParameter("CatalogId") >> "0"
		requestMock.getContextPath() >> "/store"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the redirectToParent is set in request.
		1 * requestMock.setParameter("redirectToParent", "true");
        1 * requestMock.serviceParameter("redirect", requestMock, responseMock);
	}
	
	def "Search Droplet with browseSearchVO as null. Will set browseSearchVO in request. This TC tests for browseSearchVO property being set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getQueryParameter("narrowDown") >> "Sh"
		
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the browseSearchVO is set in Request.
		1 * requestMock.setParameter("browseSearchVO",browseSearchVOMock);
	}
	
	def "Search Droplet with browseSearchVO with CategoryHeader and Query as not null. This TC tests the getParentCategory method of CatalogTools is not invoked."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		browseSearchVOMock.getCategoryHeader().getQuery() >> "N="
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getParameter("CatalogRefId") >> "40979960"
		requestMock.getParameter("siteId") >> "BedBathUS"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getParentCategory of CatalogTools is not invoked.
		0 * catalogToolsMock.getParentCategory("40979960", "BedBathUS");
	}
	
	def "Search Droplet with browseSearchVO and ParentCategory. This TC tests the query property of categoryParentVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getParameter("CatalogRefId") >> "40979960"
		requestMock.getParameter("siteId") >> "BedBathUS"
		categoryVOMock.getCategoryId() >> "60229960"
		categoryVOMock.getCategoryName() >> "Kitchen"
		catalogToolsMock.getParentCategory("40979960","BedBathUS") >> ["0":categoryVOMock]
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks the query property of categoryParentVO.
		//1 * categoryParentVOMock.setQuery('60229960')
		//1 * categoryParentVOMock.setName("Kitchen")
		1 * browseSearchVOMock.setCategoryHeader({CategoryParentVO pCVo -> pCVo.query == '60229960' && pCVo.name == 'Kitchen'})
	}
	
	def "Search Droplet with browseSearchVO and PhantomCategory as false as stored in CategoryParentVO. This TC tests the query and name properties of categoryParentVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getParameter("CatalogRefId") >> "5559960"
		requestMock.getParameter("siteId") >> "BedBathUS"
		categoryVOMock.getCategoryId() >> "4449960"
		categoryVOMock.getCategoryName() >> "Household"
		categoryVOMock.getPhantomCategory() >> false
		catalogToolsMock.getParentCategory("5559960","BedBathUS") >> ["0":categoryVOMock, "1":categoryVOMock]
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setCategoryHeader is executed with proper query parameters in CategoryParentVO.
		1 * browseSearchVOMock.setCategoryHeader({CategoryParentVO pCVo -> pCVo.query == '4449960' && pCVo.name == 'Household'})
	}
	
	def "Search Droplet with browseSearchVO and PhantomCategory as true. This TC tests the query and name properties of categoryParentVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getParameter("CatalogRefId") >> "5559960"
		requestMock.getParameter("siteId") >> "BedBathUS"
		categoryVOMock.getCategoryId() >> "4449960"
		categoryVOMock.getCategoryName() >> "Outdoor"
		categoryVOMock.getPhantomCategory() >> true
		catalogToolsMock.getParentCategory("5559960","BedBathUS") >> ["0":categoryVOMock, "1":categoryVOMock]
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setCategoryHeader is executed with proper query parameters in CategoryParentVO.
		1 * browseSearchVOMock.setCategoryHeader({CategoryParentVO pCVo -> pCVo.query == '4449960' && pCVo.name == 'Outdoor'})
		
	}
	
	def "Search Droplet with browseSearchVO and empty ParentCategory. This TC tests the name property of categoryParentVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("564446")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getParameter("CatalogRefId") >> "40979960"
		categoryVOMock.getCategoryName() >> "Outdoor"
		requestMock.getParameter("siteId") >> "BedBathUS"
		catalogToolsMock.getParentCategory("40979960","BedBathUS") >> [:]
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setCategoryHeader is executed with proper query parameters in CategoryParentVO.
		0 * browseSearchVOMock.setCategoryHeader({CategoryParentVO pCVO -> pCVO.name == 'Outdoor'})
	}
	
	def "Search Droplet with browseSearchVO with swsterms from bluepills. This TC tests the browseSearchVO & swsTermsList is set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		requestMock.getParameter("swsterms") >> "Sheets::bed"
		requestMock.getQueryParameter("narrowDown") >> "fl_Sheets::bed"
		
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the browseSearchVO and swsterms is set in request.
		1 * requestMock.setParameter("browseSearchVO",browseSearchVOMock);
		1 * requestMock.setParameter("swsTermsList",_);
	}
	
	def "Search Droplet with browseSearchVO without swsterms and null enteredNarrowDown from blue pills. This TC tests the browseSearchVO & swsTermsList is set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		//requestMock.getParameter("swsterms") >> "Sheets::bed"
		requestMock.getQueryParameter("narrowDown") >> "fl_Sheets::bedfl_Sheets::beds"
		
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the browseSearchVO and swsterms is set in request.
		1 * requestMock.setParameter("browseSearchVO",browseSearchVOMock);
		1 * requestMock.setParameter("swsterms", _);
		
	}
	
	def "Search Droplet with browseSearchVO with swsterms from bluepills and autosuggest. This TC tests the browseSearchVO & swsTermsList is set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		//requestMock.getParameter("swsterms") >> "Sheets::bed"
		requestMock.getQueryParameter("narrowDown") >> "fl_Sheets::bed"		
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		autoSuggestVOMock.getSpellCorrection() >> "Sheets"
		autoSuggestVOMock.getDymSuggestion() >> "blue"
		browseSearchVOMock.getAutoSuggest() >> [autoSuggestVOMock]
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the browseSearchVO and swsterms is set in request.
		2 * requestMock.setParameter("lastEnteredSWSKeyword", "Sheets::bed")
	}
	
	def "Search Droplet with browseSearchVO with swsterms from bluepills without autosuggest. This TC tests the browseSearchVO & swsTermsList is set in Request."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		//requestMock.getParameter("swsterms") >> "Sheets::bed"
		requestMock.getQueryParameter("narrowDown") >> "fl_Sheets::bed"
		
		
		//Mocking browseSearchVO
		browseSearchVOMock.getCategoryHeader() >> categoryParentVOMock
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the browseSearchVO and swsterms is set in request.
		1 * requestMock.setParameter("lastEnteredSWSKeyword", "Sheets::bed")
	}
	
	def "Search Droplet with browseSearchVO with isValidSearchCriteria and CompareProductEntryVO contained in BBBProductsList. This TC tests the inCompareDrawer property of BBBProduct."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.setDescription("Sheets")
		bbbProduct.setProductID("654464")
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProduct]
		
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("654464")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getBBBProducts is executed with proper query parameters in BBBProduct.
		browseSearchVOMock.getBbbProducts().getBBBProducts().get(0).inCompareDrawer == true
		
	}
	
	def "Search Droplet with browseSearchVO with isValidSearchCriteria and CompareProductEntryVO not contained in BBBProductsList. This TC tests the inCompareDrawer property of BBBProduct."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.getDescription() >> "Sheets"
		bbbProduct.getProductID() >> "55564"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProduct]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		compareProductEntryVO.setProductId("654464")
		productComparisonListMock.getItems() >> [compareProductEntryVO]
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getBBBProducts is not executed of browseSearchVO.
		browseSearchVOMock.getBbbProducts().getBBBProducts().get(0).inCompareDrawer == false
	}
	
	def "Search Droplet with browseSearchVO with isValidSearchCriteria and empty compareProductEntryVO. This TC tests the inCompareDrawer property of BBBProduct."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 1
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		productComparisonListMock.getItems() >> []
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getBBBProducts is executed with proper query parameters in BBBProduct.
		browseSearchVOMock.getBbbProducts().getBBBProducts().get(0).inCompareDrawer == false
	}
	
	def "Search Droplet with browseSearchVO with isValidSearchCriteria and BBBProductsListCount as 0. This TC tests the invocation of getBBBProducts method of browseSearchVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getBBBProducts is not executed of browseSearchVO.
		0 * browseSearchVOMock.getBbbProducts().getBBBProducts()
	}
	
	def "Search Droplet with browseSearchVO with !isValidSearchCriteria. This TC tests the inCompareDrawer property of BBBProduct."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("origSearchTerm") >> "T"
		requestMock.getQueryParameter("frmBrandPage") >> "false"
		//populating BBBProduct
		BBBProduct bbbProduct = new BBBProduct()
		bbbProduct.setDescription("Sheets")
		bbbProduct.setProductID("654464")
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0		
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getBBBProducts is executed with proper query parameters in BBBProduct.
		0 * Boolean.valueOf(requestMock.getQueryParameter("frmBrandPage"))
		
	}
	
	def "Search Droplet with browseSearchVO with facets as DEPARTMENT. This TC tests the invocation of removePhantomCategory method of CatalogTools."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		facetParentVODeptMock.getName() >> "DEPARTMENT"
		browseSearchVOMock.getFacets() >> [facetParentVODeptMock]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
		testObj.setRemovePhantomCat(true)
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the removePhantomCategory is executed of CatalogTools.
		1 * catalogToolsMock.removePhantomCategory(_, "BedBathUS")
	}
	
	def "Search Droplet with browseSearchVO with facets as Non Department. This TC tests the invocation of removePhantomCategory method of CatalogTools."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		facetParentVOColorMock.getName() >> "COLOR"
		browseSearchVOMock.getFacets() >> [facetParentVOColorMock]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the removePhantomCategory is not executed of CatalogTools.
		0 * catalogToolsMock.removePhantomCategory(_)
	}
	
	def "Search Droplet with browseSearchVO with facets with no name. This TC tests the invocation of removePhantomCategory method of CatalogTools."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getFacets() >> [facetParentVOColorMock]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the removePhantomCategory is not executed of CatalogTools.
		0 * catalogToolsMock.removePhantomCategory(_)
	}
	
	def "Search Droplet with browseSearchVO with empty facets. This TC tests the invocation of removePhantomCategory method of CatalogTools."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getFacets() >> []
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the removePhantomCategory is not executed of CatalogTools.
		0 * catalogToolsMock.removePhantomCategory(_)
	}
	
	def "Search Droplet with browseSearchVO with multiple facets. This TC tests the invocation of remove method of browseSearchVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		facetParentVOColorMock.getName() >> "COLOR"
		facetParentVODeptMock.getName() >> "DEPARTMENT"
		browseSearchVOMock.getFacets() >> [facetParentVOColorMock, facetParentVODeptMock]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the index of facetParentVODeptMock is changed from 1 to 0.
		browseSearchVOMock.getFacets().indexOf(facetParentVODeptMock) == 0
	}
	
	def "Search Droplet with browseSearchVO with Asset Map as not null. This TC tests the invocation of getAssetMap method of browseSearchVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		otherAssetMock.getCount() >> 5
		mediaAssetMock.getCount() >> 5
		guideAssetMock.getCount() >> 5
		browseSearchVOMock.getAssetMap() >> ["Other":otherAssetMock, "Media":mediaAssetMock, "Guide":guideAssetMock]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getAssetMap is executed of browseSearchVO for Other, Media , Guide.
		1 * browseSearchVOMock.getAssetMap().get("Other").getCount()
		1 * browseSearchVOMock.getAssetMap().get("Media").getCount()
		1 * browseSearchVOMock.getAssetMap().get("Guide").getCount()
	}
	
	def "Search Droplet with browseSearchVO with Asset Map qas non null. This TC tests the invocation of getAssetMap method of browseSearchVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		otherAssetMock.getCount() >> 5
		mediaAssetMock.getCount() >> 0
		guideAssetMock.getCount() >> 0
		browseSearchVOMock.getAssetMap() >> ["Other":otherAssetMock, "Media":mediaAssetMock, "Guide":guideAssetMock]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getAssetMap is executed of browseSearchVO for Other, Media , Guide.
		1 * browseSearchVOMock.getAssetMap().get("Other").getCount()
		1 * browseSearchVOMock.getAssetMap().get("Media").getCount()
		1 * browseSearchVOMock.getAssetMap().get("Guide").getCount()
	}
	
	def "Search Droplet with browseSearchVO with Asset Map as null. This TC tests the invocation of getAssetMap method of browseSearchVO."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getAssetMap() >> [:]
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the getAssetMap is not executed of browseSearchVO for Other, Media , Guide.
		0 * browseSearchVOMock.getAssetMap().get("Other").getCount()
		0 * browseSearchVOMock.getAssetMap().get("Media").getCount()
		0 * browseSearchVOMock.getAssetMap().get("Guide").getCount()
	}
	
	def "Search Droplet with browseSearchVO with Redir URL . This TC tests the invocation of setRedirectionURLinRequest method of SearchDroplet."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getRedirUrl() >> "/Store"
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setRedirectionURLinRequest is executed of SearchDroplet.
		1 * requestMock.setParameter("redirectUrl", browseSearchVOMock.getRedirUrl() + "?" + "isRedirect=true")
		1 * requestMock.serviceParameter("redirect", requestMock, responseMock)
	}
	
	def "Search Droplet with browseSearchVO with Redir URL and frmBrandPage . This TC tests the invocation of setRedirectionURLinRequest method of SearchDroplet."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getQueryParameter("frmBrandPage") >> "true"
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getRedirUrl() >> "/Store"
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setRedirectionURLinRequest is not executed of SearchDroplet.
		0 * requestMock.setParameter("redirectUrl", browseSearchVOMock.getRedirUrl() + "?" + "isRedirect=true")
		0 * requestMock.serviceParameter("redirect", requestMock, responseMock)
	}
	
	def "Search Droplet with browseSearchVO with Redir URL and ? . This TC tests the invocation of setRedirectionURLinRequest method of SearchDroplet."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getRedirUrl() >> "/Store?"
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setRedirectionURLinRequest is not executed of SearchDroplet.
		1 * requestMock.setParameter("redirectUrl", browseSearchVOMock.getRedirUrl() + "&" + "isRedirect=true")
		1 * requestMock.serviceParameter("redirect", requestMock, responseMock)
	}
	
	def "Search Droplet with browseSearchVO with empty Redir URL and ? . This TC tests the invocation of setRedirectionURLinRequest method of SearchDroplet."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		requestMock.getParameter("refType") >> "refType"
		
		
		//populating BBBProduct
		bbbProductMock.getDescription() >> "Sheets"
		bbbProductMock.getProductID() >> "654464"
		//populating BBBProductList
		bbbProductListMock.getBBBProductCount() >> 0
		bbbProductListMock.getBBBProducts() >> [bbbProductMock]
		browseSearchVOMock.getBbbProducts() >> bbbProductListMock
		browseSearchVOMock.getRedirUrl() >> ""
		//Mocking browseSearchVO
		searchManagerMock.performSearch(_) >> browseSearchVOMock
		requestMock.getQueryParameter("pagFilterOpt") >> "5"
		requestMock.getQueryParameter("pagNum") >> "1"
		searchManagerMock.fetchPerPageDropdownList() >> ["10"]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the setRedirectionURLinRequest is not executed of SearchDroplet.
		1 * requestMock.setParameter("redirectUrl", "")
		1 * requestMock.serviceParameter("redirect", requestMock, responseMock)
	}
	
	def "Search Droplet with BBBBusinessException scenarios. This TC tests the invocation of BBBBusinessException in SearchDroplet."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getHeader("User-Agent") >> "/5.0 (iPad; U; CPU OS 3_2_1)"
		catalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "per_page_default_ipad") >> {throw new BBBBusinessException("Mock Buisness Exception")}
		searchManagerMock.fetchPerPageDropdownList() >> [""]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the BBBBusinessException in invoked.
		1 * requestMock.setParameter("errorMsg", "err_search_biz_exception");
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock);
	}
	
	def "Search Droplet with BBBSystemException scenarios. This TC tests the invocation of BBBBusinessException in SearchDroplet."() {
		
		given:
		//mocking methods for different mocks
		catalogToolsMock.getAllValuesForKey(testObj.getSearchDimConfig(), testObj.getMaxAgeCurrentViewCookie()) >> ["100","some other value"]//shortcut for lists
		requestMock.getServerName() >> "cookieDomain"
		responseMock.getResponse() >> responseResponseMock
		responseResponseMock.addHeader(_,_) >> {}
		
		requestMock.getParameter("refType") >> "refType"
		
		requestMock.getParameter("appendProdDimId") >> "Y"
		searchManagerMock.getCatalogId("Record Type", "Product") >> {throw new BBBSystemException("Mock System Exception")}
		searchManagerMock.fetchPerPageDropdownList() >> [""]
		requestMock.getResponse() >>   responseMock//This is required for all droplets ATG otherwise uses null for response object
				
		when:
		
		testObj.service(requestMock,responseMock)//calling the test methods
		
		then:
		//checks that the BBBSystemException in invoked.
		1 * requestMock.setParameter("errorMsg", "err_search_sys_exception");
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock);
	}
}
