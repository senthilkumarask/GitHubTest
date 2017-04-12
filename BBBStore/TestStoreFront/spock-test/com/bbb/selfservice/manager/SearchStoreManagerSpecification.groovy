package com.bbb.selfservice.manager


import java.util.List
import java.util.Map;

import atg.multisite.SiteContextManager
import atg.repository.MutableRepository
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile
import atg.userprofiling.ProfileTools
import org.apache.http.client.ClientProtocolException
import org.junit.Test

import com.bbb.cache.LocalStoreVO
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.ThresholdVO
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.inventory.BBBInventoryManagerImpl;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.BBBObjectCache
import com.bbb.repository.RepositoryItemMock
import com.bbb.search.droplet.SearchDroplet;
import com.bbb.selfservice.common.RouteDetails
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper
import com.bbb.selfservice.tools.StoreTools
import com.bbb.tools.BBBStoreRepositoryTools
import com.bbb.utils.BBBConfigRepoUtils
import spock.lang.specification.BBBExtendedSpec

class SearchStoreManagerSpecification extends BBBExtendedSpec {
	
	def SearchStoreManager testObj
	def Profile profileMock = Mock(Profile)
	def MutableRepository repositoryMock =  Mock()
	def RepositoryItem repositoryItemMock = Mock()
	def ProfileTools profileToolsMock =  Mock()
	def BBBCatalogToolsImpl testObjCatalogTools
	//def BBBCatalogTools catalogToolsMock = Mock()
	def StoreTools storeToolsMock = Mock()
	def StoreDetailsWrapper storeDetailWrapermock =  Mock()
	def StoreDetails storeDetailsmock = Mock();
	def BBBInventoryManagerImpl inventoryManagerMock = Mock()
	def BBBCatalogToolsImpl catalogToolImplMock = Mock()
	def BBBStoreRepositoryTools storeRepositoryToolsMock = Mock()
	def ThresholdVO thresholdVoMock = Mock()
	def LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	def RouteDetails routDetailsMock = Mock()
	def SiteContextManager siteContextManagerMock = Mock()
	def BBBObjectCache objectCacheMock = Mock()
	def setup(){		
		
		testObjCatalogTools = Spy(BBBCatalogToolsImpl)
		requestMock.resolveName("/atg/multisite/SiteContextManager") >> siteContextManagerMock
		testObj = new SearchStoreManager(catalogTools:catalogToolImplMock,storeTools:storeToolsMock,inventoryManager:inventoryManagerMock,lblTxtTemplateManager:lblTxtTemplateManagerMock,staticMapSize:"10",staticMapZoom:"3",objectCache:objectCacheMock)
		BBBConfigRepoUtils.setBbbCatalogTools(testObjCatalogTools)
	}
	def "fetchFavoriteStoreId for transient user"() {
		
		given:
		  String siteId = "BedBathUS"
		  profileMock.isTransient() >> true
		when:
		   String returnStroeId = testObj.fetchFavoriteStoreId(siteId, profileMock)
		then:
		 returnStroeId == null
	}
	
	def "fetchFavoriteStoreId when profile transient property is false"() {
		
		given:
		profileMock.getRepositoryId() >> "p124566"
		String siteId = "BedBathUS"
		profileMock.isTransient() >> false
		//profileMock.getProfileTools().getProfileRepository() >> repositoryMock
		profileMock.getProfileTools() >> profileToolsMock
		profileToolsMock.getProfileRepository() >> repositoryMock
		
		def RepositoryItemMock userSiteAssoc = new RepositoryItemMock()
		userSiteAssoc.setProperties(["favouriteStoreId":"12545"])
		Map<String, RepositoryItem> lUserSiteMapMock = new HashMap<String, RepositoryItem>()
		lUserSiteMapMock.put(siteId, userSiteAssoc);
		
		repositoryMock.getItem(profileMock.getRepositoryId(), "user") >> repositoryItemMock
		//repositoryItemMock.setProperties(["userSiteItems":lUserSiteMapMock]) 
		repositoryItemMock.getPropertyValue("userSiteItems") >> lUserSiteMapMock 
		
		when:
		String storeId = testObj.fetchFavoriteStoreId(siteId, profileMock)
		
		then:
		storeId == "12545"
		
	}
	
	def "fetchFavoriteStoreId when profile transient property false and lUserSitesMap Map is empty"(){
		given:
		profileMock.getRepositoryId() >> "p124566"
		String siteId = "BedBathUS"
		profileMock.isTransient() >> false
		//profileMock.getProfileTools().getProfileRepository() >> repositoryMock
		profileMock.getProfileTools() >> profileToolsMock
		profileToolsMock.getProfileRepository() >> repositoryMock
		
		repositoryMock.getItem(profileMock.getRepositoryId(), "user") >> repositoryItemMock
		Map<String, RepositoryItem> lUserSiteMapMock = new HashMap<String, RepositoryItem>()
		repositoryItemMock.getPropertyValue("userSiteItems") >> lUserSiteMapMock

		when:
		String id = testObj.fetchFavoriteStoreId(siteId, profileMock)
		
		then:
		id == null
	}
	
	def "fetchFavoriteStoreId when profile transient property false and lUserSitesMap Map is null"(){
		given:
		profileMock.getRepositoryId() >> "p124566"
		String siteId = "BedBathUS"
		profileMock.isTransient() >> false
		//profileMock.getProfileTools().getProfileRepository() >> repositoryMock
		profileMock.getProfileTools() >> profileToolsMock
		profileToolsMock.getProfileRepository() >> repositoryMock
		
		repositoryMock.getItem(profileMock.getRepositoryId(), "user") >> repositoryItemMock
		repositoryItemMock.getPropertyValue("userSiteItems") >> null

		when:
		String id = testObj.fetchFavoriteStoreId(siteId, profileMock)
		
		then:
		id == null
	}

	
	def "fetchFavoriteStoreId with repository Exception " (){
		given:
		profileMock.getRepositoryId() >> "p124566"
		String siteId = "BedBathUS"
		profileMock.isTransient() >> false
		//profileMock.getProfileTools().getProfileRepository() >> repositoryMock
		profileMock.getProfileTools() >> profileToolsMock
		profileToolsMock.getProfileRepository() >> repositoryMock
		repositoryMock.getItem(profileMock.getRepositoryId(), "user") >> {throw new RepositoryException("Repository Exception")}
		when:
	    testObj.fetchFavoriteStoreId(siteId, profileMock)
		
		then:
		BBBSystemException exception = thrown()
	}
	
	def "fetchFavStoreDetails method when site id is null" (){
		given:
		
		when:
		testObj.fetchFavStoreDetails("2589", null)
		then:
		BBBSystemException exception = thrown()
	}
	
	def "fetchFavStoreDetails when no site found for site id " (){
		given:
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ([])
		when:
		testObj.fetchFavStoreDetails("2589", "siteID")
		then:
		BBBSystemException exception = thrown()
	}
	
	def "fetchFavStoreDetails when bopusInEligibleStore list contains the storeid"(){
		given:
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> (["909d"])
		catalogToolImplMock.getBopusInEligibleStores("909d", "909d") >> ["2589"]
		when:
		StoreDetails storeDetail = testObj.fetchFavStoreDetails("2589", "909d")
		then:
		storeDetail == null
	}
	
	def "fetchFavStoreDetails when bopusInEligibleStore  list does not contains the storeid"(){
		given:
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> (["909d"])
		catalogToolImplMock.getBopusInEligibleStores("909d", "909d") >> (["storeId"])
		when:
		StoreDetails storeDetail = testObj.fetchFavStoreDetails("2589", "909d")
		then:
		storeDetail == null
	}
	//TODO change delhi to newYork
	
	def "fetchFavStoreDetails when bopusEligibleStates and store state are different" (){
		given:
		def StoreDetailsWrapper storeDetailWrapermock =  Mock();
		def StoreDetails storeDetailsmock = Mock();
		storeDetailsmock.getStoreName() >> "UsStore" 
		storeDetailsmock.getState() >> "newYork"
		storeDetailWrapermock.getStoreDetails() >> ([storeDetailsmock])
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> (["siteId"])
		catalogToolImplMock.getBopusInEligibleStores("siteId", "909d") >> (["siteId"])
		storeToolsMock.getStoresByStoreId("2589", "siteId" , false) >> storeDetailWrapermock
		
		catalogToolImplMock.getBopusEligibleStates("909d") >>(["delhi"]);
		
		when:
		StoreDetails storeDetailObj = testObj.fetchFavStoreDetails("2589", "909d")
		then:
		storeDetailObj == null
	}
	
	def "fetchFavStoreDetails when bopusEligibleStates an store state are same" (){
		given:
		def StoreDetailsWrapper storeDetailWrapermock =  Mock();
		def StoreDetails storeDetailsmock = Mock();
		storeDetailsmock.getStoreName() >> "UsStore"
		storeDetailsmock.getState() >> "delhi"
		storeDetailWrapermock.getStoreDetails() >> ([storeDetailsmock])
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> (["siteId"])
		catalogToolImplMock.getBopusInEligibleStores("siteId", "909d") >> (["siteId"])
		storeToolsMock.getStoresByStoreId("2589", "siteId" , false) >> storeDetailWrapermock
		
		catalogToolImplMock.getBopusEligibleStates("909d") >>(["delhi"]);
		
		when:
		StoreDetails storeDetailObj = testObj.fetchFavStoreDetails("2589", "909d")
		then:
		storeDetailObj.getState() == "delhi"
	}
	

	
	
	///////////fetchFavStoreDetailsForPDP ////
	

	
	def "fetchFavStoreDetailsForPDP. If bopusInEligibleStore list is empty and inventory status  available"(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		
		storeDetailsmock.getCity() >> "Us"
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores(*_) >> []
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> null
		//testObj.getLocalStoreDbKey() >> "true"
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryForStoreFromDb("skuId", "storeId") >> storeVoMock
		catalogToolImplMock.getSkuThreshold("909d", "skuId") >> {return thresholdVoMock}
		inventoryManagerMock.getInventoryStatus(3, 3, thresholdVoMock , "storeId" ) >> 0
		storeToolsMock.getStores("storeId", "storeType") >> repositoryItemList
		catalogToolImplMock.getStoreRepository() >> repositoryMock
		//catalogToolImplMock.getBopusInEligibleStores() >> repositoryMock
		storeToolsMock.addStoreDetailsToList(*_) >> [storeDetailsmock]
		
		
		when:
		StoreDetails sd= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd !=null
		sd.getCity() == "Us"
	}
	
	def "fetchFavStoreDetailsForPDP. The store id gets from getBopusInEligibleStores and passed  storeId is same"(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		testObj.isLoggingDebug() >> true
		storeDetailsmock.getCity() >> "newYork"
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909d") >> ["storeId"]
		
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		storeToolsMock.getStoresByStoreId("storeId", "storeType", true) >>  storeDetailWrapermock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb([storeDetailsmock], "skuId", 1, _ , _) >> storeDetailsmock
		when:
		StoreDetails sd= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd !=null
		sd.getCity() == "newYork"
	}
	
	def "fetchFavStoreDetailsForPDP. if inventory status is available ,The store id gets from getBopusInEligibleStores and passed  storeId is not same same"(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		storeDetailsmock.getDistance() >> "300"
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909d") >> ["wrongStoreId"]
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> storeVoMock
		catalogToolImplMock.getSkuThreshold("909d", "skuId") >> {return thresholdVoMock}
		inventoryManagerMock.getInventoryStatus(3, 3, thresholdVoMock , "storeId" ) >> 0
		storeToolsMock.getStores("storeId", "storeType") >> repositoryItemList
		catalogToolImplMock.getStoreRepository() >> repositoryMock
		storeToolsMock.addStoreDetailsToList(item1,[],null,null,"storeId",_,null) >> [storeDetailsmock]
		
		
		when:
		StoreDetails sd= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd !=null
		sd.getDistance() == "300"
	}
	
	def "fetchFavStoreDetailsForPDP .if inventory status is limited, The store id gets from getBopusInEligibleStores and passed  storeId is not same same "(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		storeDetailsmock.getDistance() >> "100"
		
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909d") >> ["wrongStoreId"]
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> null
		//testObj.getLocalStoreDbKey() >> "true"
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["false"]
		storeToolsMock.getInventoryForStoreFromDb("skuId", "storeId") >> storeVoMock
		catalogToolImplMock.getSkuThreshold("909d", "skuId") >> {return thresholdVoMock}
		inventoryManagerMock.getInventoryStatus(0, 3, thresholdVoMock , "storeId" ) >> 2
		storeToolsMock.getStores("storeId", "storeType") >> repositoryItemList
		catalogToolImplMock.getStoreRepository() >> repositoryMock
		//catalogToolImplMock.getBopusInEligibleStores() >> repositoryMock
		storeToolsMock.addStoreDetailsToList(item1,[],null,null,"storeId",_,null) >> [storeDetailsmock]
		
		
		when:
		StoreDetails sd= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd !=null
		sd.getDistance() == "100"
		
	}
	
	def "fetchFavStoreDetailsForPDP. if inventory status is limited and lStoreDetailList gets from getStoreTools().addStoreDetailsToList() is empty"(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909d") >> ["wrongStoreId"]
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> null
		//testObj.getLocalStoreDbKey() >> "true"
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["false"]
		storeToolsMock.getInventoryForStoreFromDb("skuId", "storeId") >> storeVoMock
		catalogToolImplMock.getSkuThreshold("909d", "skuId") >> {return thresholdVoMock}
		inventoryManagerMock.getInventoryStatus(0, 3, thresholdVoMock , "storeId") >> 2
		storeToolsMock.getStores("storeId", "storeType") >> repositoryItemList
		catalogToolImplMock.getStoreRepository() >> repositoryMock
		//catalogToolImplMock.getBopusInEligibleStores() >> repositoryMock
		storeToolsMock.addStoreDetailsToList(item1,[],null,null,"storeId",_,null) >> []
		
		
		when:
		StoreDetails sd= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd ==null
	}

	
	def "fetchFavStoreDetailsForPDP. if inventory status is not in (limited or available) and localStoreVO is returning null"(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		testObj.isLoggingDebug() >> true
		storeDetailsmock.getCountry() >> "canada"
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909d") >> ["wrongStoreId"]
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> null
		//testObj.getLocalStoreDbKey() >> "true"
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryForStoreFromDb("skuId", "storeId") >> storeVoMock
		catalogToolImplMock.getSkuThreshold("909d", "skuId") >> {return thresholdVoMock}
		inventoryManagerMock.getInventoryStatus(3, 3, thresholdVoMock , "storeId") >> 4
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		storeToolsMock.getStoresByStoreId("storeId", "storeType",true) >>  storeDetailWrapermock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb([storeDetailsmock], "skuId", 3, _ , _) >> storeDetailsmock
		
		
		when:
		StoreDetails sd= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd !=null
		sd.getCountry() == "canada"
	}

	def "fetchFavStoreDetailsForPDP .if store id gets from 'getCatalogTools().getBopusInEligibleStores()' and passed  storeId is same  and lStoreDetails list  is empty "(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909d") >> ["storeId"]
		
		storeDetailWrapermock.getStoreDetails() >> []
		storeToolsMock.getStoresByStoreId("storeId", "storeType",true) >>  storeDetailWrapermock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		when:
		StoreDetails sd1= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		sd1 == null
	}
	
	def "fetchFavStoreDetailsForPDP for Exception "(){
		given:
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "909d") >> {throw new Exception("exception")}
	when:
		StoreDetails sd1= testObj.fetchFavStoreDetailsForPDP("storeId", "909d", requestMock, responseMock)
		then:
		 Exception exception = thrown()
	}
	
	////////// getStoreWithInventory //////
	
	def "getStoreWithInventory. If Storeid gets from 'getCatalogTools().getBopusInEligibleStores(storeType, siteId)' and passed storeId of storeDetail list is different and inventory status is available "(){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStoreId() >> "storeId"
		
		storeDetailsmock.getCity() >> "newYork"
		storeVoMock.getStockLevel() >> 3
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909s") >> ["wrongStoreId"]
		catalogToolImplMock.getSkuThreshold("909s","skuId") >> thresholdVoMock
		
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> storeVoMock
		inventoryManagerMock.getInventoryStatus(3, 3, thresholdVoMock,"storeId") >> 0
		
		when:
		StoreDetails stoD = testObj.getStoreWithInventory([storeDetailsmock], requestMock, responseMock, "909s" , "storeType")
		then:
		stoD != null
		stoD.getCity() == "newYork"
		
	}
	
	def "getStoreWithInventory. when If Storeid gets from 'getCatalogTools().getBopusInEligibleStores(storeType, siteId)' and passed storeId of storeDetailList is same, LocalStoreDbKey gets from getLocalStoreDbKey() is true " (){
		given:
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909s") >> ["storeId"]
		catalogToolImplMock.getSkuThreshold("909s","skuId") >> thresholdVoMock
		
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb([storeDetailsmock], "skuId", 1, thresholdVoMock, ["storeId"]) >> storeDetailsmock
				
		when:
		StoreDetails stoD = testObj.getStoreWithInventory([storeDetailsmock], requestMock, responseMock, "909s" , "storeType")
		then:
		stoD == null
		1*requestMock.serviceParameter("outputInventoryNotAvailable", requestMock, responseMock)
		
	}
	
	def "getStoreWithInventory. when inventory status is limited stock and BopusInEligibleStores list gets from 'getCatalogTools().getBopusInEligibleStores()' is empty  "(){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		storeVoMock.getStoreId() >> "storeId"
		storeDetailsmock.getStoreId() >> "storeId"
		storeDetailsmock.getCountry() >> "india"
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909s") >> []
		catalogToolImplMock.getSkuThreshold("909s","skuId") >> thresholdVoMock
		
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> storeVoMock
		inventoryManagerMock.getInventoryStatus(3, 3, thresholdVoMock,"storeId") >> 2
		
		when:
		StoreDetails stoD = testObj.getStoreWithInventory([storeDetailsmock], requestMock, responseMock, "909s" , "storeType")
		then:
		stoD != null
		storeDetailsmock.getCountry() == "india"
	}
	
	def "getStoreWithInventory. If inventory statuse  is not in ( limited or available)  "(){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		storeVoMock.getStoreId() >> "storeId"
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909s") >> []
		catalogToolImplMock.getSkuThreshold("909s","skuId") >> thresholdVoMock
		
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> storeVoMock
		inventoryManagerMock.getInventoryStatus(3, 3, thresholdVoMock,"storeId") >> 4
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["false"]
		
		when:
		StoreDetails stoD = testObj.getStoreWithInventory([storeDetailsmock], requestMock, responseMock, "909s" , "storeType")
		then:
		stoD == null
		1*requestMock.serviceParameter("outputInventoryNotAvailable", requestMock, responseMock)
		
		
	}
	
	def "getStoreWithInventory when  StoreDetail gets from storeTools.getInventoryFromDb(*) is null and localStoreDbKey is true "(){
		given:
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "909s") >> ["storeId"]
		catalogToolImplMock.getSkuThreshold("909s","skuId") >> thresholdVoMock
		
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb([storeDetailsmock], "skuId", 1, thresholdVoMock, ["storeId"]) >> null
				
		when:
		StoreDetails stoD = testObj.getStoreWithInventory([storeDetailsmock], requestMock, responseMock, "909s" , "storeType")
		then:
		stoD == null
		
	}
	
	
	/////////////////////getStoreWithInventoryByStoreId///////
	def "getStoreWithInventoryByStoreId . when inventory status is available AND  store id of bopusInEligibleStore list gets from 'catalogTool.getBopusInEligibleStores()'  and passed storeId is different " (){
		given:
		String SId = "storeId" 
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStoreId() >> "storeId" 
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> ["wrongStoreId"]
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> storeVoMock
		inventoryManagerMock.getInventoryStatus(3, 1, thresholdVoMock,"storeId") >> 0
		when:
		String id = testObj.getStoreWithInventoryByStoreId([SId], requestMock, responseMock, "siteId")
		then:
		id == "storeId"
	}
	
	def "getStoreWithInventoryByStoreId. If inventory status is limited stock and bopusInEligibleStore list gets from 'catalogTool.getBopusInEligibleStores()' is empty" (){
		given:
		String SId = "storeId"
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStoreId() >> "storeId"
		
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> null
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryForStoreFromDb("skuId", "storeId") >> storeVoMock
		
		inventoryManagerMock.getInventoryStatus(3, 1, thresholdVoMock,"storeId") >> 2
		when:
		String id = testObj.getStoreWithInventoryByStoreId([SId], requestMock, responseMock, "siteId")
		then:
		id == "storeId"
	}
	
	def "getStoreWithInventoryByStoreId when store id of bopusInEligibleStore list gets from 'catalogTool.getBopusInEligibleStores()'  and passed storeId is same " (){
		given:
		String SId = "storeId"
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> ["storeId"]
		when:
		String id = testObj.getStoreWithInventoryByStoreId([SId], requestMock, responseMock, "siteId")
		then:
		id == null
	}
	
	def "getStoreWithInventoryByStoreId .If inventory status is not in (available , limited ) " (){
		given:
		String SId = "storeId"
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStoreId() >> "storeId"
		
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> storeVoMock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		inventoryManagerMock.getInventoryStatus(3, 1, thresholdVoMock,"storeId") >> 4
		when:
		String id = testObj.getStoreWithInventoryByStoreId([SId], requestMock, responseMock, "siteId")
		then:
		id == null
	}
	
	def "getStoreWithInventoryByStoreId for BBBBusinessException exception" (){
		given:
		String SId = "storeId"
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> ["dummyStore"]
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> {new Exception("inventory exception")}
		when:
		String id = testObj.getStoreWithInventoryByStoreId([SId], requestMock, responseMock, "siteId")
		then:
		BBBBusinessException exception = thrown()
	}
	
	def "getStoreWithInventoryByStoreId. if bopusInEligibleStore list gets from 'catalogTool.getBopusInEligibleStores()'  is empty  " (){
		given:
		String SId = "storeId"
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		when:
		String id = testObj.getStoreWithInventoryByStoreId([], requestMock, responseMock, "siteId")
		then:
		id == null
	}
	
	def "getStoreWithInventoryByStoreId When Local store vo (gets from getInventoryFromLocalStore() is null and localStoreDbKey is false)'" (){
		given:
		String SId = "storeId"
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> ["dummyStore"]
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> null
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["false"]
		
		when:
		String id = testObj.getStoreWithInventoryByStoreId([SId], requestMock, responseMock, "siteId")
		then:
		id == null
	}
	
	
	///////// getStoreWithInventoryByStoreIdChanged ///////////////////////
	def "getStoreWithInventoryByStoreIdChanged .when inventory status is available and store id gets from bopusInEligibleStore list  and passed storeId is different " (){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		storeVoMock.getStoreId() >> "storeId"
		
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> ["dummyStore"]
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> storeVoMock
		inventoryManagerMock.getInventoryStatus(3, 1, thresholdVoMock,"storeId") >> 0
		when:
		String id = testObj.getStoreWithInventoryByStoreIdChanged(["storeId"], requestMock, responseMock ,"siteId")
		then:
		id == "storeId"
	}
	
	def "getStoreWithInventoryByStoreIdChanged when inventory status is Limited and bopusInEligibleStore list gets from catalogToolImplMock.getBopusInEligibleStores(*) is empty " (){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		storeVoMock.getStoreId() >> "storeId"
		
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> null
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryForStoreFromDb("skuId", "storeId") >> storeVoMock
		
		inventoryManagerMock.getInventoryStatus(3, 1, thresholdVoMock,"storeId") >> 2
		when:
		String id = testObj.getStoreWithInventoryByStoreIdChanged(["storeId"], requestMock, responseMock ,"siteId")
		then:
		id == "storeId"
	}
	
	def "getStoreWithInventoryByStoreIdChanged when store id gets from  ' bopusInEligibleStore list'  and Passed store id are same  " (){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> ["storeId"]
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		when:
		String id = testObj.getStoreWithInventoryByStoreIdChanged(["storeId"], requestMock, responseMock ,"siteId")
		then:
		id == null
	}
	
	///////////// checkStoreHasInventoryForSkuId /////////

	def "checkStoreHasInventoryForSkuId when inventory status not in(available or limited stock) and BopusInEligibleStores list is empty  " (){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		storeVoMock.getStoreId() >> "storeId"
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> storeVoMock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		inventoryManagerMock.getInventoryStatus(3, 1, thresholdVoMock,"storeId") >> 4
		when:
		Boolean returnValue = testObj.checkStoreHasInventoryForSkuId("storeId", "skuId", 1, "siteId")
		then:
		returnValue == false
	}

	def "checkStoreHasInventoryForSkuId when localStoreVO is null( gets from  getInventoryFromLocalStore(storeId, skuId) method ) and LocalStoreDbKey is false " (){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		catalogToolImplMock.getSkuThreshold("siteId", "skuId") >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> null
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["false"]
		when:
		Boolean returnValue = testObj.checkStoreHasInventoryForSkuId("storeId", "skuId", 1, "siteId")
		then:
		returnValue == false
	}

	def "checkStoreHasInventoryForSkuId when getInventoryFromLocalStore(_, _) throws Exception " (){
		given:
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores("storeType", "siteId") >> []
		catalogToolImplMock.getSkuThreshold(*_) >> thresholdVoMock
		storeToolsMock.getInventoryFromLocalStore("storeId","skuId") >> {new Exception("inventory exception")}
		when:
		Boolean returnValue = testObj.checkStoreHasInventoryForSkuId("storeId", "skuId", 1, "siteId")
		then:
		BBBBusinessException exception = thrown()
	}
	
	///////////////////////////  searchStoreById //////////
	def "searchStoreById .If StoreDetailsWrapper is null" (){
		given:
		
		when:
		StoreDetails storeDetail = testObj.searchStoreById("storeId", "siteId", "storeType")
		then:
		storeDetail == null
	}
	
	def "searchStoreById .If  StoreDetails   is null" (){
		given:
		
		storeToolsMock.getStoresByStoreId("storeId","storeType", false) >>  storeDetailWrapermock
		storeDetailWrapermock.getStoreDetails() >> null
		
		when:
		StoreDetails storeDetail = testObj.searchStoreById("storeId", "siteId", "storeType")
		then:
		storeDetail == null
	}

	def "searchStoreById when StoreDetails list  size is zero" (){
		given:
		
		storeToolsMock.getStoresByStoreId("storeId","storeType", false) >>  storeDetailWrapermock
		storeDetailWrapermock.getStoreDetails() >> []
		
		when:
		StoreDetails storeDetail = testObj.searchStoreById("storeId", "siteId", "storeType")
		then:
		storeDetail == null
	}
	
	def "searchStoreById when objStoreDetailsWrapper.getStoreDetails()' size is greter then zero" (){
		given:
		
		storeToolsMock.getStoresByStoreId("storeId","storeType", false) >>  storeDetailWrapermock
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		storeDetailsmock.getCountry() >> "india"
		
		when:
		StoreDetails storeDetail = testObj.searchStoreById("storeId", "siteId", "storeType")
		then:
		storeDetail != null
		storeDetail.getCountry() == "india"
		
	}
	
	///////////// searchStoresById ///////////////
	
	def "searchStoresById. If passed  storetype parameter is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		
		when:
		 List<StoreDetails> lStoreType = testObj.searchStoresById("storeId", "siteId", null)
		then:
		lStoreType == null
	}
	
	def "searchStoresById if   storedetail list is null gets from objStoreDetailsWrapper.getStoreDetails() " (){
		given:
		storeToolsMock.getStoresByStoreId("storeId", "storeType", true) >>  storeDetailWrapermock
		
		when:
		 List<StoreDetails> lStoreType = testObj.searchStoresById("storeId", "siteId", "storeType")
		then:
		lStoreType == null
	}

	def "searchStoresById when  storedetail list is empty gets from objStoreDetailsWrapper.getStoreDetails() " (){
		given:
		storeToolsMock.getStoresByStoreId("storeId", "storeType", true) >>  storeDetailWrapermock
		storeDetailWrapermock.getStoreDetails() >> []
		when:
		 List<StoreDetails> lStoreType = testObj.searchStoresById("storeId", "siteId", "storeType")
		then:
		lStoreType == null
	}

	def "searchStoresById when StoreDetails is having values(like contactFlag etc.)" (){
		given:
		storeDetailsmock.getContactFlag() >> true
		storeToolsMock.getStoresByStoreId("storeId", "storeType", true) >>  storeDetailWrapermock
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		when:
		 List<StoreDetails> lStoreType = testObj.searchStoresById("storeId", "siteId", "storeType")
		then:
		lStoreType.get(0).getContactFlag() == true
	}

	
	////////////////////////////////searchStoreByAddress//////////// 
	def "searchStoreByAddress. Tc to check storeDetaiWrapper is null when SearchType string Parameter is empty" (){
		given:
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByAddress("searchCreteria", null, "10")
		then:
		SDW == null
	}
	
	def "searchStoreByAddress. Tc to check storeDetaiWrapper is null when searchCreteria string parameter is empty" (){
		given:
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByAddress(null, "searchType", "10")
		then:
		SDW == null
	}
	
	def "searchStoreByAddress. Tc to check storeDetaiWrapper is null when SearchType and SearchCriteria parameter  string is empty" (){
		given:
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByAddress(null, null, "10")
		then:
		SDW == null
	}
	
	def "searchStoreByAddress. Tc to check storeDetaiWrapper  when SearchType string having search value" (){
		given:
		storeDetailWrapermock.getCurrentPage() >> 1
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng") >> ["/bedBath/toy"]
		storeToolsMock.searchStore("2", _) >> storeDetailWrapermock
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByAddress("searchCreteria", "2", "10")
		then:
		SDW.getCurrentPage() == 1
	}
	
	/////////////////////searchStoreByCoordinates/////////////
	def "searchStoreByCoordinates.Tc to check storeDetaiWrapper is null when SearchType string parameter is null "(){
		given:
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByCoordinates("searchCreteria", null, "10")
		then:
		SDW == null

	}
	
	def "searchStoreByCoordinates.Tc to check storeDetaiWrapper is null when searchCreteria string is null "(){
		given:
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByCoordinates(null, "SearchType", "10")
		then:
		SDW == null

	}
	def "searchStoreByCoordinates.Tc to check storeDetaiWrapper is null when searchCreteria and SearchType  string is null "(){
		given:
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByCoordinates(null, null, "10")
		then:
		SDW == null

	}
	def "searchStoreByCoordinates.Tc to check storeDetaiWrapper having values(is not null) "(){
		given:
		storeDetailWrapermock.getBabyCanadaFlag() >> "true"
		storeToolsMock.searchStore("SearchType" , _) >> storeDetailWrapermock
		
		when:
		 StoreDetailsWrapper SDW = testObj.searchStoreByCoordinates("searchCreteria", "SearchType", "10")
		then:
		SDW.getBabyCanadaFlag() == "true"

	}
	///////////////////searchStoreByLatLng ///////
	def "searchStoreByLatLng. Tc to check store detail, if  Lat and  Lng passed as parameter "(){
		given:
		storeDetailsmock.city >> "us"
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		storeToolsMock.getStoreDetails("12.65", "565.151", "storeType") >> storeDetailWrapermock
		when:
		List<StoreDetails> ST = testObj.searchStoreByLatLng("12.65", "565.151", "storeType")
		then:
		ST.get(0).getCity() == "us"
	}
	
	def "searchStoreByLatLng. Tc to check store details list   is null . when storedetailwrapper having  null value "(){
		given:
		storeToolsMock.getStoreDetails("12.65", "565.151", "storeType") >> null
		when:
		List<StoreDetails> ST = testObj.searchStoreByLatLng("12.65", "565.151", "storeType")
		then:
		ST == null
	}

	///////////////searchStorePerPage ///////////
	def "searchStorePerPage Tc to check StoreDetailsWrapper is nul when page key is null" (){
		given:
		
		when:
		StoreDetailsWrapper SDW = testObj.searchStorePerPage(null, "10")
		then:
		SDW == null 
	}
	def "searchStorePerPage Tc to check StoreDetailsWrapper is nul when page number is null" (){
		given:
		
		when:
		StoreDetailsWrapper SDW = testObj.searchStorePerPage("pageKey", null)
		then:
		SDW == null
	}
	def "searchStorePerPage Tc to check StoreDetailsWrapper is nul when page number and page key is null" (){
		given:
		
		when:
		StoreDetailsWrapper SDW = testObj.searchStorePerPage(null, null)
		then:
		SDW == null
	}
	def "searchStorePerPage Tc to check StoreDetailsWrapper value while passing page number and page key " (){
		given:
		storeDetailWrapermock.getBabyCanadaFlag() >> "CAN"
		storeToolsMock.searchStore(null, _) >> storeDetailWrapermock
		when:
		StoreDetailsWrapper SDW = testObj.searchStorePerPage("K12", "2")
		then:
		SDW.getBabyCanadaFlag() == "CAN"
	}
	//////////////////////////////getSearchKeyURL /////////////////////
	
	def "getSearchKeyURL. Tc the search url is empty when search type is null "(){
		given:
		when:
		String searchUrl = testObj.getSearchKeyURL(null)
		then:
		searchUrl == null
	}
	def "getSearchKeyURL. Tc the search url when search type is zip code base search "(){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng") >> ["http://www.mapquestapi.com/geocoding/v1/12454"]
		when:
		String searchUrl = testObj.getSearchKeyURL("2")
		then:
		searchUrl.contains("//www.mapquestapi.com/")
	}
	def "getSearchKeyURL. Tc the search url when search type is address based sotre search "(){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng") >> ["http://www.mapquestapi.com/geocoding/v1/address"]
		when:
		String searchUrl = testObj.getSearchKeyURL("3")
		then:
		searchUrl.contains("//www.mapquestapi.com/")
	}
	def "getSearchKeyURL. Tc the search url when search type is cordinate based sotre search "(){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> ["http://www.mapquestapi.com/geocoding/v1/codinate"]
		when:
		String searchUrl = testObj.getSearchKeyURL("4")
		then:
		searchUrl.contains("//www.mapquestapi.com/")
	}
	def "getSearchKeyURL. Tc the search url when search type vlaue is wrong "(){
		given:
		when:
		String searchUrl = testObj.getSearchKeyURL("6")
		then:
		searchUrl == null
	}
	/////////////////////////getStoreDirections /////////
	
	def "getStoreDirections. TC to get the routeDetail passing startPoint and routeType etc as parameter" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestDirectionsString") >> ["mapKey"]
		routDetailsMock.getEndPointLat >> "100.2"; 
		routDetailsMock.getSessionId() >> "ses1235"
		routDetailsMock.getStartPointLat >> "1254.232"
	
		storeToolsMock.storeDirections(_) >>  routDetailsMock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "routeMapKey") >> ["routeKey"]
		
		when:
		RouteDetails RD = testObj.getStoreDirections("startP", "endP", "routeType", true, true, true)
		then:
		RD != null
		1*routDetailsMock.setRouteMap(_);
	}
	
	def "getStoreDirections. TC to get the routeDetail if  routeType(objP2PRouteDetails) is null , (seasonalRoad , HighWay and tollRoas as false) and storeToolsMock.storeDirections(_) as null  " (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestDirectionsString") >> ["mapKey"]
		routDetailsMock.getEndPointLat >> "100.2";
		routDetailsMock.getSessionId() >> "ses1235"
		routDetailsMock.getStartPointLat >> "1254.232"
	
		storeToolsMock.storeDirections(_) >>  null
		
		when:
		RouteDetails RD = testObj.getStoreDirections("startP", "endP", null, false, false, false)
		then:
		RD == null
	}
	def "getStoreDirections. TC to get ClientProtocolException  " (){
		given:
	
		storeToolsMock.storeDirections(_) >>  {throw new ClientProtocolException("client exception") }
		
		when:
		RouteDetails RD = testObj.getStoreDirections("startP", "endP", null, false, false, false)
		then:
		RD == null
	}
	
	def "getStoreDirections. TC to get IOException  " (){
		given:
	
		storeToolsMock.storeDirections(_) >>  {throw new IOException("client exception") }
		
		when:
		RouteDetails RD = testObj.getStoreDirections("startP", "endP", null, false, false, false)
		then:
		RD == null
	}

	def "getStoreDirections. TC to get BBBSystemException  " (){
		given:
	
		storeToolsMock.storeDirections(_) >>  {throw new BBBSystemException("client exception") }
		
		when:
		RouteDetails RD = testObj.getStoreDirections("startP", "endP", null, false, false, false)
		then:
		BBBBusinessException exception = thrown()
	}
	
/////////////////////////////getMapQuestCoordinateString ///////////////// 
	
	def "getMapQuestCoordinateString . key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> ["us"]
		when:
		String value = testObj.getMapQuestCoordinateString()
		then:
		value == "us"
		
	}	
	def "getMapQuestCoordinateString.If key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> null
		when:
		String value = testObj.getMapQuestCoordinateString()
		then:
		value == null
		
	}
	def "getMapQuestCoordinateString.If key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> []
		when:
		String value = testObj.getMapQuestCoordinateString()
		then:
		value == null
		
	}

	
	/////////////////////////////getMapQuestRadiusString /////////////////
	
	def "getMapQuestRadiusString .If key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestRadiusString") >> ["us"]
		when:
		String value = testObj.getMapQuestRadiusString()
		then:
		value == "us"
		
	}
	def "getMapQuestRadiusString.If key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestRadiusString") >> null
		when:
		String value = testObj.getMapQuestRadiusString()
		then:
		value == null
		
	}
	def "getMapQuestRadiusString. If key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestRadiusString") >> []
		when:
		String value = testObj.getMapQuestRadiusString()
		then:
		value == null
		
	}
	/////////////////////////////getMapQuestRecordInfoString /////////////////
	
	def "getMapQuestRecordInfoString. when  key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestRecordInfoString") >> ["us"]
		when:
		String value = testObj.getMapQuestRecordInfoString()
		then:
		value == "us"
		
	}
	def "getMapQuestRecordInfoString. when  key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestRecordInfoString") >> null
		when:
		String value = testObj.getMapQuestRecordInfoString()
		then:
		value == null
		
	}
	def "getMapQuestRecordInfoString. when key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestRecordInfoString") >> []
		when:
		String value = testObj.getMapQuestRecordInfoString()
		then:
		value == null
		
	}
	
	/////////////////////////////getMapQuestSearchString /////////////////
	
	def "getMapQuestSearchString. when  key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> ["us"]
		when:
		String value = testObj.getMapQuestSearchString()
		then:
		value == "us"
		
	}
	def "getMapQuestSearchString . when  key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> null
		when:
		String value = testObj.getMapQuestSearchString()
		then:
		value == null
		
	}
	def "getMapQuestSearchString when key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchString") >> []
		when:
		String value = testObj.getMapQuestSearchString()
		then:
		value == null
		
	}
	
	/////////////////////////////getMapQuestPaginationString /////////////////
	
	def "getMapQuestPaginationString. when  key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestPaginationString") >> ["us"]
		when:
		String value = testObj.getMapQuestPaginationString()
		then:
		value == "us"
		
	}
	def "getMapQuestPaginationString. when  key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestPaginationString") >> null
		when:
		String value = testObj.getMapQuestPaginationString()
		then:
		value == null
		
	}
	def "getMapQuestPaginationString. when key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestPaginationString") >> []
		when:
		String value = testObj.getMapQuestPaginationString()
		then:
		value == null
		
	}
	
	/////////////////////////////getMapQuestSearchStringForLatLng /////////////////
	
	def "getMapQuestSearchStringForLatLng. when  key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng") >> ["us"]
		when:
		String value = testObj.getMapQuestSearchStringForLatLng()
		then:
		value == "us"
		
	}
	def "getMapQuestSearchStringForLatLng . when  key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng") >> null
		when:
		String value = testObj.getMapQuestSearchStringForLatLng()
		then:
		value == null
		
	}
	def "getMapQuestSearchStringForLatLng .when key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestSearchStringForLatLng") >> []
		when:
		String value = testObj.getMapQuestSearchStringForLatLng()
		then:
		value == null
		
	}
	
	/////////////////////////////getMapQuestDirectionsString /////////////////
	
	def "getMapQuestDirectionsString. when  key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestDirectionsString") >> ["us"]
		when:
		String value = testObj.getMapQuestDirectionsString()
		then:
		value == "us"
		
	}
	def "getMapQuestDirectionsString when  key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestDirectionsString") >> null
		when:
		String value = testObj.getMapQuestDirectionsString()
		then:
		value == null
		
	}
	def "getMapQuestDirectionsString when key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "mapQuestDirectionsString") >> []
		when:
		String value = testObj.getMapQuestDirectionsString()
		then:
		value == null
		
	}
	/////////////////////////////getRouteMapKey /////////////////
	
	def "getRouteMapKey. when  key list having values" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "routeMapKey") >> ["us"]
		when:
		String value = testObj.getRouteMapKey()
		then:
		value == "us"
		
	}
	def "getRouteMapKey when  key list is null" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "routeMapKey") >> null
		when:
		String value = testObj.getRouteMapKey()
		then:
		value == null
		
	}
	def "getRouteMapKey when key list is empty" (){
		given:
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs", "routeMapKey") >> []
		when:
		String value = testObj.getRouteMapKey()
		then:
		value == null
		
	}
	
	///////////////////////////getStoreType ////////////
	
	def "getStoreType. TC when the storeIds is empty  " (){
		given:
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> [""]
		when:
		String returnValue = testObj.getStoreType("siteId")
		then:
		BBBSystemException exception =   thrown();
	}
	
	////////////////////////getStoresBySearchString ////
	def "getStoresBySearchString() .TC to check output when 'supply balance key is true  " (){
		given:
		requestMock.getParameter("skuId") >> "skuId"
		requestMock.getParameter("registryId") >> "rId"
		requestMock.getParameter("siteId") >> "siteId"
		
		testObj = Spy()
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		
		testObj.getStoreType(_) >> "US"
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		testObj.searchStoreByAddress("location=searchString", "3", null) >> storeDetailWrapermock
		testObj.getSupplyBalanceKey() >> "true"
		catalogToolImplMock.getBopusInEligibleStores(_, _) >> null
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["store":1]
		testObj.checkProductAvailability([storeDetailsmock],"siteId","skuId","rId","false",0L,"storeToStore",requestMock,false) >> null 
		when:
		 testObj.getStoresBySearchString("searchString", requestMock, responseMock, false)
		then:
		1*testObj.checkProductAvailability([storeDetailsmock], requestMock, responseMock,
						"productAvailableViewStore", "outputViewStore",
						"storeDetails", false, "viewStoreEmpty");
		
	}
	
	def "getStoresBySearchString() .TC  when 'supply balance key is false  " (){
		given:
		requestMock.getParameter("skuId") >> "skuId"
		requestMock.getParameter("registryId") >> "rId"
		requestMock.getParameter("siteId") >> "siteId"
		
		testObj = Spy()
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		
		testObj.getStoreType(_) >> "US"
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		testObj.searchStoreByAddress("location=searchString", "3", null) >> storeDetailWrapermock
		testObj.getSupplyBalanceKey() >> "false"
		when:
		 testObj.getStoresBySearchString("searchString", requestMock, responseMock, true)
		then:
		1*requestMock.setParameter("storeDetails" , [storeDetailsmock]);
		
	}
	
	def "getStoresBySearchString() .TC when storeDetailsWrapper is null gets from  searchStoreByAddress()   " (){
		given:
		requestMock.getParameter("skuId") >> "skuId"
		requestMock.getParameter("registryId") >> "rId"
		requestMock.getParameter("siteId") >> "siteId"
		
		testObj = Spy()
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		
		testObj.getStoreType(_) >> "US"
		testObj.searchStoreByAddress("location=searchString", "3", null) >> null
		when:
		 testObj.getStoresBySearchString("searchString", requestMock, responseMock, true)
		then:
         1*requestMock.serviceLocalParameter("viewStoreEmpty", requestMock, responseMock)		
	}

	def "getStoresBySearchString() .TC when  detail of store (storeDetailsWrapper.getStoreDetails())  is empty when storeDetailsWrapper.getStoreDetails() is empty   " (){
		given:
		requestMock.getParameter("skuId") >> "skuId"
		requestMock.getParameter("registryId") >> "rId"
		requestMock.getParameter("siteId") >> "siteId"
		
		testObj = Spy()
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		storeDetailWrapermock.getStoreDetails() >> []
		
		testObj.getStoreType(_) >> "US"
		testObj.searchStoreByAddress("location=searchString", "3", null) >> storeDetailWrapermock
		when:
		 testObj.getStoresBySearchString("searchString", requestMock, responseMock, true)
		then:
		 1*requestMock.serviceLocalParameter("viewStoreEmpty", requestMock, responseMock)
	}
	
	
	
//////////////////////getFavStoreByStoreId ////////////
	def "getFavStoreByStoreId .TC to check store details is added to request object" (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]
        //START :: store detail from PDP method
		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores(*_) >> ["wrongStoreId"]
		storeToolsMock.getInventoryFromLocalStore("storeId", "skuId") >> storeVoMock
		catalogToolImplMock.getSkuThreshold("909d", "skuId") >> {return thresholdVoMock}
		inventoryManagerMock.getInventoryStatus(_, _, _ ,_ ) >> 0
		storeToolsMock.getStores("storeId", "storeType") >> repositoryItemList
		catalogToolImplMock.getStoreRepository() >> repositoryMock
		//catalogToolImplMock.getBopusInEligibleStores() >> repositoryMock
		storeToolsMock.addStoreDetailsToList(*_) >> [storeDetailsmock]
    	//END : Store detail from PDP method
		
		when:
		testObj.getFavStoreByStoreId("siteId", requestMock, responseMock, "storeId" )
		then:
		//print requestMock.getParameter("storeDetailsFavStore")
		1 * requestMock.setParameter("storeDetailsFavStore", [storeDetailsmock])
		
	}	
	
	def "getFavStoreByStoreId .TC to check result when  the  store detail(gets from fetchFavStoreDetailsForPDP()) is null   " (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		
		//START :: store detail from PDP method
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		requestMock.getParameter("inventoryNotAvailableInFavStore") >> null
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores(*_) >> ["storeId"]
		
		storeDetailWrapermock.getStoreDetails() >> []
		storeToolsMock.getStoresByStoreId(_, _, _) >>  storeDetailWrapermock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb(_, _, _, _ , _) >> storeDetailsmock
			//END : Store detail from PDP method
		
		when:
		testObj.getFavStoreByStoreId("siteId", requestMock, responseMock, "storeId" )
		then:
		//print requestMock.getParameter("storeDetailsFavStore")
		1 * requestMock.serviceLocalParameter("favStoreEmpty", requestMock, responseMock )
		
	}
	
	def "getFavStoreByStoreId .TC to check outputFavStore open parameter when local store pdp flag is false" (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["false"]
		when:
		testObj.getFavStoreByStoreId("siteId", requestMock, responseMock, "storeId" )
		
		then:
		1 * requestMock.serviceLocalParameter("outputFavStore", requestMock, responseMock )
		
	}
	
	def "getFavStoreByStoreId .TC  when favoriteStoreId is null " (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		when:
		testObj.getFavStoreByStoreId("siteId", requestMock, responseMock, null )
		
		then:
		0 * testObj.fetchFavStoreDetailsForPDP(null, "siteId", requestMock, responseMock);
		
	}
	
	def "getFavStoreByStoreId .TC to check result when  the  store detail is null and 'inventory not available' parameter exsist in request object  " (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		
		//START :: store detail from PDP method
		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]

		def LocalStoreVO storeVoMock = Mock();
		def ThresholdVO thresholdVoMock = Mock()
		requestMock.getParameter("inventoryNotAvailableInFavStore") >> "inventoryNotAvailable"
		testObj.isLoggingDebug() >> true
		storeVoMock.getStockLevel() >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		catalogToolImplMock.getBopusInEligibleStores(*_) >> ["storeId"]
		
		storeDetailWrapermock.getStoreDetails() >> []
		storeToolsMock.getStoresByStoreId(_, _, _) >>  storeDetailWrapermock
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb(_, _, _, _ , _) >> storeDetailsmock
			//END : Store detail from PDP method
		
		when:
		testObj.getFavStoreByStoreId("siteId", requestMock, responseMock, "storeId" )
		then:
		//print requestMock.getParameter("storeDetailsFavStore")
		0 * requestMock.serviceLocalParameter("favStoreEmpty", requestMock, responseMock )
		
	}
//////////getFavStoreByCoordinates ////////////	
	
	def "getFavStoreByCoordinates .TC to check outputFavStore open parameter when local storepdpkey is false" (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["false"]
		when:
		testObj.getFavStoreByCoordinates("siteId", requestMock, responseMock, "10.22", "12.02", false );
		
		then:
		1 * requestMock.serviceLocalParameter("outputFavStore", requestMock, responseMock )
		
	}
	def "getFavStoreByCoordinates. TC to check store details is added to request object"(){
		given:
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		storeToolsMock.getStoreDetails("10.22","12.02", "storeType") >> storeDetailWrapermock
		// START :getStoreWithInventory method
		def LocalStoreVO storeVoMock = Mock();
		storeVoMock.getStockLevel() >> 3
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("orderedQty") >> 3
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores(*_) >> []
		catalogToolImplMock.getSkuThreshold(*_) >> thresholdVoMock
		
		storeToolsMock.getInventoryFromLocalStore(_, _) >> storeVoMock
		inventoryManagerMock.getInventoryStatus(_, _, _, _) >> 2
	    //END : getStoreWithInventory method 
		
		when:
		testObj.getFavStoreByCoordinates("siteId", requestMock, responseMock, "10.22", "12.02", false );
		then:
		1 * requestMock.setParameter("storeDetailsFavStore", [storeDetailsmock])
		
		
	}
	
	def "getFavStoreByCoordinates. TC to check result when  the  store detail is empty and 'inventory not available' parameter dosent exsist"(){
		given:
		requestMock.getParameter("inventoryNotAvailableInFavStore") >> null
		
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		storeToolsMock.getStoreDetails("10.22","12.02", "storeType") >> storeDetailWrapermock
		// START :getStoreWithInventory method
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores(*_) >> ["storeId"]
		catalogToolImplMock.getSkuThreshold(*_) >> thresholdVoMock
		
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb(*_) >> storeDetailsmock
       	//END : getStoreWithInventory method
		
		when:
		testObj.getFavStoreByCoordinates("siteId", requestMock, responseMock, "10.22", "12.02", false );
		then:
		1 * requestMock.serviceLocalParameter("favStoreEmpty", requestMock, responseMock);
		
		
	}
	
	def "getFavStoreByCoordinates. TC to check result when  the  store detail is empty and 'inventory not available' parameter exsist"(){
		given:
		requestMock.getParameter("inventoryNotAvailableInFavStore") >> "available"
		
		storeDetailWrapermock.getStoreDetails() >> [storeDetailsmock]
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		storeToolsMock.getStoreDetails("10.22", "12.02", "storeType") >> storeDetailWrapermock
		// START :getStoreWithInventory method
		storeDetailsmock.getStoreId() >> "storeId"
		requestMock.getParameter("skuId") >> "skuId"
		catalogToolImplMock.getBopusInEligibleStores(*_) >> ["storeId"]
		catalogToolImplMock.getSkuThreshold(*_) >> thresholdVoMock
		
		catalogToolImplMock.getAllValuesForKey("ThirdPartyURLs","LocalStoreRepoFetch") >> ["true"]
		storeToolsMock.getInventoryFromDb(*_) >> storeDetailsmock
		   //END : getStoreWithInventory method
		
		when:
		testObj.getFavStoreByCoordinates("siteId", requestMock, responseMock, "10.22", "12.02", false );
		then:
		0 * requestMock.serviceLocalParameter("favStoreEmpty", requestMock, responseMock);
		
		
	}
	
	def "getFavStoreByCoordinates. TC to check list of favirote stores is null "(){
		given:
		requestMock.getParameter("inventoryNotAvailableInFavStore") >> "available"
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions","LOCAL_STORE_PDP_FLAG") >> ["true"]
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		storeToolsMock.getStoreDetails("10.22", "12.02", "storeType") >> null
		when:
		testObj.getFavStoreByCoordinates("siteId", requestMock, responseMock, "10.22", "12.02",false );
		
		then:
		1*requestMock.serviceLocalParameter("favStoreEmpty", requestMock, responseMock);
		
	}
//////////////////getNearestStoresByStoreId ///////////
	def "getNearestStoresByStoreId .TC to check the product availability when store detail list is note empty and supplyBalance key is true"(){
		given:
		testObj =  Spy()
		testObj.searchStoresById("storeId", _, null) >> [storeDetailsmock]
		requestMock.getParameter("radiusChange") >> "false"
		
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		
		testObj.getDefaultRadius() >> "5"
		testObj.getStoreType(_) >> "US"
		
		
		testObj.getSupplyBalanceKey() >> "true"
		
		catalogToolImplMock.getBopusInEligibleStores(_, _) >> null
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["store":1]
		
		when:
		testObj.getNearestStoresByStoreId("storeId", requestMock, responseMock, "favStoreId", false)
		
		then:
		1*testObj.checkProductAvailability([storeDetailsmock], requestMock, responseMock,
			"productAvailableViewStore", "outputViewStore",
			"storeDetails", false, "viewStoreEmpty");
	}
	
	def "getNearestStoresByStoreId .TC to check the storeDetails is sets to request object when supply balance key is false "(){
		given:
		testObj =  Spy()
		testObj.searchStoresById("storeId", _, null) >> [storeDetailsmock]
		requestMock.getParameter("radiusChange") >> "true"
		
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		
		testObj.getDefaultRadius() >> "5"
		testObj.getStoreType(_) >> "US"
		
		
		testObj.getSupplyBalanceKey() >> "false"
		
		catalogToolImplMock.getBopusInEligibleStores(_, _) >> null
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["store":1]
		
		when:
		testObj.getNearestStoresByStoreId("storeId", requestMock, responseMock, "favStoreId", false)
		
		then:
		1*requestMock.setParameter("storeDetails" , [storeDetailsmock])
	}
	
	def "getNearestStoresByStoreId .TC to check viewStoreEmpty open Param when storeDetails is empty"(){
		given:
		testObj =  Spy()
		testObj.searchStoresById("storeId", _, null) >> []
		testObj.getDefaultRadius() >> "5"
		requestMock.getParameter("radiusChange") >> "false"
		
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
			
		when:
		testObj.getNearestStoresByStoreId("storeId", requestMock, responseMock, "favStoreId", false)
		
		then:
		1*requestMock.serviceLocalParameter("viewStoreEmpty", requestMock, responseMock)
		1*requestMock.getSession().setAttribute("miles","5");
		
	}
	
	def "getNearestStoresByStoreId .TC to check viewStoreEmpty open Parameter when storeDetails is empt and radius change is true"(){
		given:
		testObj =  Spy()
		testObj.searchStoresById("storeId", _, null) >> []
		requestMock.getParameter("radiusChange") >> "true"
		
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
			
		when:
		testObj.getNearestStoresByStoreId("storeId", requestMock, responseMock, "favStoreId", false)
		
		then:
		1*requestMock.serviceLocalParameter("viewStoreEmpty", requestMock, responseMock)
		
	}
	
	///////// getNearestStoresByCoordinates /////////
	
	def "getNearestStoresByCoordinates . TC to check the product availability when store detail list is note empty and supplyBalance key is true " (){
		given:
		testObj =  Spy()
		testObj.getStoreType(_) >> "storeType" 
		testObj.getStoreDetailsByLatLng(requestMock,responseMock, "10.2", "56.2", "storeType") >> [storeDetailsmock]
		requestMock.getParameter("radiusChange") >> "true"
		testObj.getSupplyBalanceKey() >> "true"
		
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);

		catalogToolImplMock.getBopusInEligibleStores(_, _) >> null
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["store":1]
		
		when:
		testObj.getNearestStoresByCoordinates("10.2", "56.2", requestMock, responseMock, false)
		
		then:
		1*testObj.checkProductAvailability([storeDetailsmock], requestMock, responseMock,
			"productAvailableViewStore", "outputViewStore",
			"storeDetails", false, "viewStoreEmpty");
	}
	
	def "getNearestStoresByCoordinates . TC to check the storeDetails is sets to request object when supply balance key is false " (){
		given:
		testObj =  Spy()
		testObj.getStoreType(_) >> "storeType"
		testObj.getStoreDetailsByLatLng(requestMock,responseMock, "10.2", "56.2", "storeType") >> [storeDetailsmock]
		requestMock.getParameter("radiusChange") >> "false"
		testObj.getSupplyBalanceKey() >> "false"
		
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
	
		when:
		testObj.getNearestStoresByCoordinates("10.2", "56.2", requestMock, responseMock ,false)
		
		then:
		1*requestMock.serviceLocalParameter("outputViewStore", requestMock, responseMock)
	}
	
	def "getNearestStoresByCoordinates . TC to check viewStoreEmpty open Parameter when storeDetails is empty " (){
		given:
		testObj =  Spy()
		testObj.getStoreType(_) >> "storeType"
		testObj.getStoreDetailsByLatLng(requestMock,responseMock, "10.2", "56.2", "storeType") >> []
		requestMock.getParameter("radiusChange") >> "false"
		testObj.getDefaultRadius() >> "5"
			
		when:
		testObj.getNearestStoresByCoordinates("10.2", "56.2", requestMock, responseMock, false)
		
		then:
		1*requestMock.serviceLocalParameter("viewStoreEmpty", requestMock, responseMock)
		1*requestMock.getSession().setAttribute("miles","5");
	}
	
	def "getNearestStoresByCoordinates . TC to check viewStoreEmpty open Parameter when storeDetails is empt and radius change is true " (){
		given:
		testObj =  Spy()
		testObj.getStoreType(_) >> "storeType"
		testObj.getStoreDetailsByLatLng(requestMock,responseMock, "10.2", "56.2", "storeType") >> []
		requestMock.getParameter("radiusChange") >> "true"
		testObj.getDefaultRadius() >> "5"
			
		when:
		testObj.getNearestStoresByCoordinates("10.2", "56.2", requestMock, responseMock, false)
		
		then:
		1*requestMock.serviceLocalParameter("viewStoreEmpty", requestMock, responseMock)
	}
	
	/////////// checkProductAvailability ////
	def"checkProductAvailability.Tc to Check the store details(with inventory) are set to request parameters "(){
		given:
		
		 
		storeDetailsmock.getStoreId() >> "fid"
		requestMock.getParameter("skuId") >> "skuId"
		requestMock.getParameter("registryId") >> "registryId"
		requestMock.getParameter("siteId") >> "siteId"
		
		requestMock.getParameter("changeCurrentStore") >> "true"
		requestMock.getParameter("orderedQty") >> 1
		
		catalogToolImplMock.getAllValuesForKey(*_) >> ["storeType"]
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["store":1]
		
		
		when:
		testObj.checkProductAvailability([storeDetailsmock], requestMock, responseMock, "productAvailabel" ,"out", "storeDetail", true, "eOutput")
		then:
       1*requestMock.setParameter("storeDetail", [storeDetailsmock]);
	   1*requestMock.serviceLocalParameter("out", requestMock, responseMock);
	   
		
	}
	
	def"checkProductAvailability.TC to set the 'inventory not available' error message to parameter   "(){
		given:
		
		 Map<String, Integer> map = new HashMap<String, Integer>()
		 
		storeDetailsmock.getStoreId() >> "fid"
		requestMock.getParameter("skuId") >> "skuId"
		requestMock.getParameter("registryId") >> "registryId"
		requestMock.getParameter("siteId") >> "siteId"
		
		requestMock.getParameter("changeCurrentStore") >> "true"
		requestMock.getParameter("orderedQty") >> 1
		
		catalogToolImplMock.getAllValuesForKey(*_) >> ["storeType"]
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> map
		
		
		when:
		testObj.checkProductAvailability([], requestMock, responseMock, "productAvailabel" ,"out", "storeDetail", true, "eOutput")
		then:
        1*requestMock.serviceParameter("inventoryNotAvailableInFavStore", requestMock, responseMock)
		
	}

	////////////////checkProductAvailability///////////
	def"checkProductAvailability. TC to check  Store pickup not available when storeid is equals to getBopusInEligible Stores Id" (){
		given:
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		catalogToolImplMock.getBopusEligibleStates("siteId") >> ["newYork"]
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		
		catalogToolImplMock.getBopusInEligibleStores("storeType","siteId") >> ["USStore"]
		
		storeDetailsmock.getStoreId() >>"USStore"
		storeDetailsmock.getState() >> "newYork"
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["USStore":1]
		when:
		Map<String, Integer> productAvailability = testObj.checkProductAvailability([storeDetailsmock], "siteId", "skuId", "rId", true, 1, "operation", requestMock, true)
		then:
		productAvailability.get("USStore")==101
		
	}
	
	def"checkProductAvailability. TC to check available state is 1 when storeid of getBopusInEligible is null " (){
		given:
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		catalogToolImplMock.getBopusEligibleStates("siteId") >> ["newYork"]
		catalogToolImplMock.getBopusInEligibleStores("storeType","siteId") >> null
		

		storeDetailsmock.getStoreId() >>"USStore"
		storeDetailsmock.getState() >> null
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["USStore":1]
		when:
		Map<String, Integer> productAvailability = testObj.checkProductAvailability([storeDetailsmock], "siteId", "skuId", "rId", true, 1, "operation", requestMock, true)
		then:
		productAvailability.get("USStore")==1
		
	}
	
	def"checkProductAvailability. TC to check   available state is 1 when storeid is not equals to getBopusInEligible Stores Id " (){
		given:
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		
		catalogToolImplMock.getBopusEligibleStates("siteId") >> ["newYork"]
		catalogToolImplMock.getBopusInEligibleStores("storeType","siteId") >> ["USStore"]
		
		storeDetailsmock.getStoreId() >>"storeId"
		storeDetailsmock.getState() >> null
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["USStore":1]
		when:
		Map<String, Integer> productAvailability = testObj.checkProductAvailability([storeDetailsmock], "siteId", "skuId", "rId", true, 1, "operation", requestMock, true)
		then:
		productAvailability.get("USStore")==1
		
	}
	
	def"checkProductAvailability. TC to check  Store pickup not available when state id is equals to getBopusEligibleStates Id " (){
		given:
		testObj.setCatalogTools(catalogToolImplMock)
		testObj.setInventoryManager(inventoryManagerMock);
		catalogToolImplMock.getAllValuesForKey("MapQuestStoreType", "siteId") >> ["storeType"]
		
		catalogToolImplMock.getBopusEligibleStates("siteId") >> ["newYork"]
		catalogToolImplMock.getBopusInEligibleStores("storeType","siteId") >> ["USStore"]
		
		storeDetailsmock.getStoreId() >>"USStore"
		storeDetailsmock.getState() >> "state"
		inventoryManagerMock.getBOPUSProductAvailability(*_) >> ["USStore":null]
		when:
		Map<String, Integer> productAvailability = testObj.checkProductAvailability([storeDetailsmock], "siteId", "skuId", "rId", true, 1, "operation", requestMock, true)
		then:
		productAvailability.get("USStore")==10
		
	}
	
   ///////////////////////////getDefaultRadius ///////	
	def"getDefaultRadius . TC to get the 30 radius from catalog tools" (){
		given:
		catalogToolImplMock.getAllValuesForKey("DefaultStoreType","radius_fis_pdp") >> ["30"]
		
		when:
		String radius = testObj.getDefaultRadius()
		then:
		radius == "30"
	}
	
	def"getDefaultRadius . TC to get the default(25) radius " (){
		given:
		catalogToolImplMock.getAllValuesForKey("DefaultStoreType","radius_fis_pdp") >> []
		
		when:
		String radius = testObj.getDefaultRadius()
		then:
		radius == "25"
	}
	
	def "getSupplyBalanceKey . TC key is true " (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions", "SUPPLY_BALANCE_ON") >> ["true"]
		
		when:
		String key = testObj.getSupplyBalanceKey()
		then:
		key == "true"
	}
	
	def "getSupplyBalanceKey .TC to check config key is false" (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions", "SUPPLY_BALANCE_ON") >> []
		
		when:
		String key = testObj.getSupplyBalanceKey()
		then:
		key == "false"
	}
	
	def "getLocalStorePDPKey .TC to check store PDP key is false" (){
		given:
		catalogToolImplMock.getAllValuesForKey("FlagDrivenFunctions", "LOCAL_STORE_PDP_FLAG") >> []
		
		when:
		String key = testObj.getLocalStorePDPKey()
		then:
		key == "false"
	}
	
	def "searchStore(). TC for clientProtocolException " () {
		given:
		storeToolsMock.searchStore(_ , _) >> {throw new ClientProtocolException("client exception") }
		when:
		StoreDetailsWrapper SDW = testObj.searchStore("test", "abc", true)
		then:
		SDW == null
     }
	def "searchStore(). TC for IOException  " () {
		given:
		storeToolsMock.searchStore(_ , _) >> {throw new IOException("client exception") }
		when:
		StoreDetailsWrapper SDW = testObj.searchStore("test", "abc", true)
		then:
		SDW == null
	 }
	
	def "getCachedStoreDetails .TC to check the store details" (){
		given:
		def BBBConfigRepoUtils bbbConfigUtilMock = Mock();
		storeDetailWrapermock.getCurrentPage() >> 1
		objectCacheMock.get("abc",_) >> storeDetailWrapermock
		testObjCatalogTools.getAllValuesForKey("ObjectCacheKeys", "MAPQUEST_CACHE_NAME") >> ["true"]
		when:
		StoreDetailsWrapper SDW = testObj.getCachedStoreDetails("abc")
		then:
		SDW.getCurrentPage() == 1
	}
	
	def "getCachedStoreDetails .TC for Exception " (){
		given:
		def BBBConfigRepoUtils bbbConfigUtilMock = Mock();
		storeDetailWrapermock.getCurrentPage() >> 1
		objectCacheMock.get("abc",_) >> {throw new Exception("exception")}
		testObjCatalogTools.getAllValuesForKey("ObjectCacheKeys", "MAPQUEST_CACHE_NAME") >> ["true"]
		when:
		StoreDetailsWrapper SDW = testObj.getCachedStoreDetails("abc")
		then:
		SDW == null
	}
}
