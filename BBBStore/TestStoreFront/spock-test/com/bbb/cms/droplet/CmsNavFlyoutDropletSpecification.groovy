package com.bbb.cms.droplet

import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.search.bean.result.CategoryParentVO
import com.bbb.search.bean.result.FacetParentVO
import com.bbb.search.bean.result.PromoVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.integration.SearchManager;

import spock.lang.specification.BBBExtendedSpec;

class CmsNavFlyoutDropletSpecification extends BBBExtendedSpec {
	
	def SearchManager searchManagerMock= Mock()

	def BBBCatalogTools catalogToolMock= Mock()

	def String categoryItemNumberKeyMock
	
	def CmsNavFlyoutDroplet testObj
	
	def setup(){
		
		testObj = Spy()
		
		testObj.setSearchManager(searchManagerMock)
		testObj.setCatalogTools(catalogToolMock)
		testObj.setCategoryItemNumberKey(categoryItemNumberKeyMock)
	}
	
	
	
	def "when categoryTree is not null"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			
			String brandName ="brandName"
			String brandQuery="brandQuery"
			
			String catName1="catName1"
			String query1="query1"
			String categoryId1="categoryId1"
			
			
			String catName2="catName2"
			String query2=brandName
			String categoryId2="categoryId2"
			
			
			FacetParentVO facetParentVO = new FacetParentVO()
			facetParentVO.setQuery(brandQuery)
			facetParentVO.setName(brandName)
			List<FacetParentVO> facets= new ArrayList<FacetParentVO>()
			facets.add(facetParentVO)
			
			CategoryParentVO categoryParentVO1= new CategoryParentVO([name:catName1,query:query1])
			CategoryParentVO categoryParentVO2= new CategoryParentVO([name:catName2,query:query2])
			Map<String,CategoryParentVO> categoryTree = new HashMap<String,CategoryParentVO>()
			categoryTree.put(categoryId1, categoryParentVO1)
			categoryTree.put(categoryId2, categoryParentVO2)
			
			
			
			PromoVO promoVO = new PromoVO()
			List<PromoVO> promos= new ArrayList<PromoVO>()
			promos.add(promoVO)
			
			SearchResults browseSearchVO = new SearchResults()
			
			Map<String,List<PromoVO>> promoMap = new HashMap();
			promoMap.put(catalogRefId, promos)
			
			browseSearchVO.setPromoMap(promoMap)
			browseSearchVO.setFacets(facets)
			
			testObj.getStringValue() >> brandName
			requestMock.getObjectParameter("browseSearchVO") >> browseSearchVO
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			searchManagerMock.getCategoryTree(_) >> categoryTree
			searchManagerMock.filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree) >> categoryTree
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
		1* testObj.logDebug("START: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("END: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("brandCategory : "+browseSearchVO)
		1* requestMock.setParameter("categoryTreeMap", _)
		1* requestMock.setParameter("brandCategory", browseSearchVO)
		1* requestMock.setParameter("brandQuery", brandQuery)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* testObj.logDebug("Existing method CmsNavFlyoutDroplet")
	}
	
	
	
	def "when categoryTree is not null | PromoMap is null in SearchResults and facetName != BCC Brand config key"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			
			String brandName ="brandName"
			String brandQuery="brandQuery"
			
			String catName1="catName1"
			String query1="query1"
			String categoryId1="categoryId1"
			
			
			String catName2="catName2"
			String query2=brandName
			String categoryId2="categoryId2"
			
			
			FacetParentVO facetParentVO = new FacetParentVO()
			facetParentVO.setQuery(brandQuery)
			facetParentVO.setName("not"+brandName)
			List<FacetParentVO> facets= new ArrayList<FacetParentVO>()
			facets.add(facetParentVO)
			
			CategoryParentVO categoryParentVO1= new CategoryParentVO([name:catName1,query:query1])
			CategoryParentVO categoryParentVO2= new CategoryParentVO([name:catName2,query:query2])
			Map<String,CategoryParentVO> categoryTree = new HashMap<String,CategoryParentVO>()
			categoryTree.put(categoryId1, categoryParentVO1)
			categoryTree.put(categoryId2, categoryParentVO2)
			
			
			
			PromoVO promoVO = new PromoVO()
			List<PromoVO> promos= new ArrayList<PromoVO>()
			promos.add(promoVO)
			
			SearchResults browseSearchVO = new SearchResults()
			
			Map<String,List<PromoVO>> promoMap = new HashMap();
			promoMap.put(catalogRefId, promos)
			
			browseSearchVO.setPromoMap(null)
			browseSearchVO.setFacets(facets)
			
			testObj.getStringValue() >> brandName
			requestMock.getObjectParameter("browseSearchVO") >> browseSearchVO
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			searchManagerMock.getCategoryTree(_) >> categoryTree
			searchManagerMock.filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree) >> categoryTree
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
		1* testObj.logDebug("START: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("END: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("brandCategory : "+browseSearchVO)
		1* requestMock.setParameter("categoryTreeMap", _)
		1* requestMock.setParameter("brandCategory", browseSearchVO)
		0* requestMock.setParameter("brandQuery", brandQuery)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* testObj.logDebug("Existing method CmsNavFlyoutDroplet")
	}
	
	def "when categoryTree is not null and categoryTree.size greater than 12(MAX_CATEGORY_COUNT) and empty facet List"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			
			String brandName ="brandName"
			String brandQuery="brandQuery"
			
			String catName1="catName1"
			String query1="query1"
			String categoryId1="categoryId1"
			
			
			String catName2="catName2"
			String query2=brandName
			String categoryId2="categoryId2"
			 
			
			FacetParentVO facetParentVO = new FacetParentVO()
			facetParentVO.setQuery(brandQuery)
			facetParentVO.setName(brandName)
			List<FacetParentVO> facets= new ArrayList<FacetParentVO>()
			//facets.add(facetParentVO)
			
			CategoryParentVO categoryParentVO1= new CategoryParentVO([name:catName1,query:query1])
			CategoryParentVO categoryParentVO2= new CategoryParentVO([name:catName2,query:query2])
			Map<String,CategoryParentVO> categoryTree = new HashMap<String,CategoryParentVO>()
			categoryTree.put(categoryId1, categoryParentVO1)
			categoryTree.put(categoryId2, categoryParentVO2)
			
			categoryTree.put("categoryId3", categoryParentVO2)
			categoryTree.put("categoryId4", categoryParentVO2)
			categoryTree.put("categoryId5", categoryParentVO2)
			categoryTree.put("categoryId6", categoryParentVO2)
			categoryTree.put("categoryId7", categoryParentVO2)
			categoryTree.put("categoryId8", categoryParentVO2)
			categoryTree.put("categoryId9", categoryParentVO2)
			categoryTree.put("categoryId10", categoryParentVO2)
			categoryTree.put("categoryId11", categoryParentVO2)
			categoryTree.put("categoryId12", categoryParentVO2)
			categoryTree.put("categoryId13", categoryParentVO2)
			
			
			
			PromoVO promoVO = new PromoVO()
			List<PromoVO> promos= new ArrayList<PromoVO>()
			promos.add(promoVO)
			
			SearchResults browseSearchVO = new SearchResults()
			
			Map<String,List<PromoVO>> promoMap = new HashMap();
			promoMap.put(catalogRefId, promos)
			
			browseSearchVO.setPromoMap(promoMap)
			browseSearchVO.setFacets(facets)
			
			testObj.getStringValue() >> brandName
			requestMock.getObjectParameter("browseSearchVO") >> browseSearchVO
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			searchManagerMock.getCategoryTree(_) >> categoryTree
			searchManagerMock.filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree) >> categoryTree
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
		1* testObj.logDebug("START: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("END: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("brandCategory : "+browseSearchVO)
		1* requestMock.setParameter("categoryTreeMap", _)
		1* requestMock.setParameter("brandCategory", browseSearchVO)
		0* requestMock.setParameter("brandQuery", brandQuery)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* testObj.logDebug("Existing method CmsNavFlyoutDroplet")
	}
	
	def "when categoryTree is not null and categoryTree.size = 12(MAX_CATEGORY_COUNT) and empty facet List and catalogRefId is null"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = null
			String siteId = "siteId"
			
			String brandName ="brandName"
			String brandQuery="brandQuery"
			
			String catName1="catName1"
			String query1="query1"
			String categoryId1="categoryId1"
			
			
			String catName2="catName2"
			String query2=brandName
			String categoryId2="categoryId2"
			 
			
			FacetParentVO facetParentVO = new FacetParentVO()
			facetParentVO.setQuery(brandQuery)
			facetParentVO.setName(brandName)
			List<FacetParentVO> facets= new ArrayList<FacetParentVO>()
			facets.add(facetParentVO)
			
			CategoryParentVO categoryParentVO1= new CategoryParentVO([name:catName1,query:query1])
			CategoryParentVO categoryParentVO2= new CategoryParentVO([name:catName2,query:query2])
			Map<String,CategoryParentVO> categoryTree = new HashMap<String,CategoryParentVO>()
			categoryTree.put(categoryId1, categoryParentVO1)
			categoryTree.put(categoryId2, categoryParentVO2)
			
			categoryTree.put("categoryId3", categoryParentVO2)
			categoryTree.put("categoryId4", categoryParentVO2)
			categoryTree.put("categoryId5", categoryParentVO2)
			categoryTree.put("categoryId6", categoryParentVO2)
			categoryTree.put("categoryId7", categoryParentVO2)
			categoryTree.put("categoryId8", categoryParentVO2)
			categoryTree.put("categoryId9", categoryParentVO2)
			categoryTree.put("categoryId10", categoryParentVO2)
			categoryTree.put("categoryId11", categoryParentVO2)
			categoryTree.put("categoryId12", categoryParentVO2)
			
			
			PromoVO promoVO = new PromoVO()
			List<PromoVO> promos= new ArrayList<PromoVO>()
			promos.add(promoVO)
			
			SearchResults browseSearchVO = new SearchResults()
			
			Map<String,List<PromoVO>> promoMap = new HashMap();
			promoMap.put(catalogRefId, promos)
			
			browseSearchVO.setPromoMap(promoMap)
			browseSearchVO.setFacets(facets)
			
			testObj.getStringValue() >> brandName
			requestMock.getObjectParameter("browseSearchVO") >> browseSearchVO
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			searchManagerMock.getCategoryTree(_) >> categoryTree
			searchManagerMock.filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree) >> categoryTree
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
		1* testObj.logDebug("START: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("END: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("brandCategory : "+browseSearchVO)
		1* requestMock.setParameter("categoryTreeMap", _)
		1* requestMock.setParameter("brandCategory", browseSearchVO)
		1* requestMock.setParameter("brandQuery", brandQuery)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* testObj.logDebug("Existing method CmsNavFlyoutDroplet")
	}
	
	def "when categoryTree is not null and PromoMap is Empty"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			
			String brandName ="brandName"
			String brandQuery="brandQuery"
			
			String catName1="catName1"
			String query1="query1"
			String categoryId1="categoryId1"
			
			
			String catName2="catName2"
			String query2=brandName
			String categoryId2="categoryId2"
			
			
			FacetParentVO facetParentVO = new FacetParentVO()
			facetParentVO.setQuery(brandQuery)
			facetParentVO.setName(brandName)
			List<FacetParentVO> facets= new ArrayList<FacetParentVO>()
			facets.add(facetParentVO)
			
			CategoryParentVO categoryParentVO1= new CategoryParentVO([name:catName1,query:query1])
			CategoryParentVO categoryParentVO2= new CategoryParentVO([name:catName2,query:query2])
			Map<String,CategoryParentVO> categoryTree = new HashMap<String,CategoryParentVO>()
			categoryTree.put(categoryId1, categoryParentVO1)
			categoryTree.put(categoryId2, categoryParentVO2)
			
			
			
			PromoVO promoVO = new PromoVO()
			List<PromoVO> promos= new ArrayList<PromoVO>()
			//promos.add(promoVO)
			
			SearchResults browseSearchVO = new SearchResults()
			
			Map<String,List<PromoVO>> promoMap = new HashMap();
			promoMap.put(catalogRefId, promos)
			
			browseSearchVO.setPromoMap(promoMap)
			browseSearchVO.setFacets(facets)
			
			testObj.getStringValue() >> brandName
			requestMock.getObjectParameter("browseSearchVO") >> browseSearchVO
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			searchManagerMock.getCategoryTree(_) >> categoryTree
			searchManagerMock.filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree) >> categoryTree
			 
			
		when:
			testObj.service(requestMock, responseMock)
		then:
		1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
		1* testObj.logDebug("START: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("END: Call from Droplet to fetch Category Tree")
		1* testObj.logDebug("brandCategory : "+browseSearchVO)
		1* requestMock.setParameter("categoryTreeMap", _)
		1* requestMock.setParameter("brandCategory", browseSearchVO)
		1* requestMock.setParameter("brandQuery", brandQuery)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* testObj.logDebug("Existing method CmsNavFlyoutDroplet") 
		1* testObj.clearPromoMapEntry(_)
	}
	
	
	
	def "when siteId is in request parameter and categoryTree is null | handle NPE in Exception Block"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			
			String brandName ="brandName"
			String brandQuery="brandQuery"
			
			String catName1="catName1"
			String query1="query1"
			String categoryId1="categoryId1"
			
			
			String catName2="catName2"
			String query2=brandName
			String categoryId2="categoryId2"
			
			
			FacetParentVO facetParentVO = new FacetParentVO()
			facetParentVO.setQuery(brandQuery)
			facetParentVO.setName(brandName)
			List<FacetParentVO> facets= new ArrayList<FacetParentVO>()
			facets.add(facetParentVO)
			
			CategoryParentVO categoryParentVO1= new CategoryParentVO([name:catName1,query:query1])
			CategoryParentVO categoryParentVO2= new CategoryParentVO([name:catName2,query:query2])
			Map<String,CategoryParentVO> categoryTree = new HashMap<String,CategoryParentVO>()
			categoryTree.put(categoryId1, categoryParentVO1)
			categoryTree.put(categoryId2, categoryParentVO2)
			
			
			
			PromoVO promoVO = new PromoVO()
			List<PromoVO> promos= new ArrayList<PromoVO>()
			promos.add(promoVO)
			
			SearchResults browseSearchVO = new SearchResults()
			
			Map<String,List<PromoVO>> promoMap = new HashMap();
			promoMap.put(catalogRefId, promos)
			
			browseSearchVO.setPromoMap(promoMap)
			browseSearchVO.setFacets(facets)
			
			
			testObj.getStringValue() >> brandName
			requestMock.getObjectParameter("browseSearchVO") >> browseSearchVO
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			searchManagerMock.getCategoryTree(_) >> null
			searchManagerMock.filterL2L3ExclusionItemsFromCategoryTree(catalogRefId, categoryTree) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
			1* testObj.logDebug("START: Call from Droplet to fetch Category Tree")
			1* testObj.logError("Exception Generated in Underlying Search Procedure : ", _)
			1* testObj.logDebug("Existing method CmsNavFlyoutDroplet")
	}
	def "when siteId is in request parameter and categoryTree is null | handle BBBBusinessException"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			String brandName="brandName"
			testObj.getStringValue() >> brandName
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			testObj.logDebug("START: Call from Droplet to fetch Category Tree")  >>  {throw new BBBBusinessException("BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
			1* testObj.logError("Exception Generated in Underlying Search Procedure : "+"BBBBusinessException")
	}
	def "when siteId is in request parameter and categoryTree is null | handle BBBSystemException"(){
		given:
			String catalogId ="CatalogId"
			String root = "rootCategory"
			String catalogRefId = "catalogRefId"
			String siteId = "siteId"
			String brandName="brandName"
			testObj.getStringValue() >> brandName
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			requestMock.getParameter("CatalogId") >> catalogId
			requestMock.getParameter("rootCategory") >> root
			requestMock.getParameter("catalogRefId") >> catalogRefId
			testObj.logDebug("START: Call from Droplet to fetch Category Tree")  >>  {throw new BBBSystemException("BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1* testObj.logDebug("Starting method CmsNavFlyoutDroplet")
			1* testObj.logError("Exception Generated in Underlying Search Procedure : "+"BBBSystemException")
	}
	
}
