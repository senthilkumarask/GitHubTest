package com.bbb.tools

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spock.lang.Specification
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement;
import atg.repository.RepositoryItemImpl
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.ServletUtil

import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.logger.PerformanceLogger
import com.bbb.repository.RepositoryItemMock;
import com.bbb.selfservice.manager.ScheduleAppointmentManager


class BBBStoreRepositoryToolsSpecification extends Specification {
	
	def BBBStoreRepositoryTools testObj
	def MutableRepository storeRepositoryMock = Mock()
	def GlobalRepositoryTools globalRepositoryToolsMock = Mock()
	def RepositoryItemMock repositoryItemMock = Mock()
	def ScheduleAppointmentManager scheduleAppointmentManagerMock = Mock()
	def DynamoHttpServletRequest requestMock = Mock()
	def PerformanceLogger perfMock = Mock()
	def EcoFeeSKUVO ecoFeeSKUVOMock = Mock()
	def MutableRepository catalogRepositoryMock = Mock()
	def MutableRepository shippingRepositoryMock = Mock()
	def RepositoryView repositoryViewMock = Mock()
	def RqlStatement rqlStatementMock = Mock()
	def QueryBuilder queryBuilderMock = Mock()
	def QueryExpression queryExpressionMock = Mock()
	def QueryExpression queryExpMock = Mock()
	def Query queryMock = Mock()
	def StoreVO storeVOMock = Mock()
	def StoreSpecialityVO storeSpecialityVOMock = Mock()
	def StoreDetails storeDetailsMock = Mock()
	
	
	def setup() {
		
		testObj = new BBBStoreRepositoryTools(storeRepository:storeRepositoryMock,scheduleAppointmentManager:scheduleAppointmentManagerMock,
canadaStoresQuery:"storeType=50 and displayOnline=1",storesQuery:"storeType!=50",bopusInEligibleStoreQuery:"storeType=?0",
bopusDisabledStoreQuery:"bopus=false",globalRepositoryTools:globalRepositoryToolsMock)
		
		ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		requestMock.resolveName("/com/bbb/framework/performance/logger/PerformanceLogger") >> perfMock
		perfMock.isEnableCustomComponentsMonitoring() >> false
		
	}
	
	/* isEcoFeeEligibleForStore - Test Cases START */
	
	def "isEcoFeeEligibleForStore by passing storeId" (){
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME) >> "NJ"
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.getCatalogRepository().getView(BBBCatalogConstants.ECO_FEE_ITEM_DESCRIPTOR) >> repositoryViewMock
			
			repositoryViewMock.getQueryBuilder() >> queryBuilderMock 
			queryBuilderMock.createPropertyQueryExpression(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> queryExpressionMock
			queryBuilderMock.createConstantQueryExpression("NJ") >> queryExpMock 
			queryBuilderMock.createComparisonQuery(queryExpMock, queryExpressionMock, QueryBuilder.EQUALS) >> queryMock
			repositoryViewMock.executeQuery(queryMock) >>  [repositoryItemMock]
		
		when:
			testObj.isEcoFeeEligibleForStore("338")
		
		then:
			testObj.isEcoFeeEligibleForStore("338") == true
	}
	
	def "isEcoFeeEligibleForStore when Repository Exception occurs" (){
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def results = testObj.isEcoFeeEligibleForStore("338")
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "isEcoFeeEligibleForStore when passing descrip property as null" (){
		
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME) >> null
		
		when:
			def results = testObj.isEcoFeeEligibleForStore("338")
		
		then:
			results == false
	}
	
	def "isEcoFeeEligibleForStore when passing descrip property as empty" (){
		
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME) >> ""
			
		when:
			def results = testObj.isEcoFeeEligibleForStore("338")
		
		then:
			results == false
	}
	
	def "isEcoFeeEligibleForStore when item returns null" (){
		given:
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> null
				
		when:
			def results = testObj.isEcoFeeEligibleForStore("338")
		
		then:
		results == false
	}
	
	def "isEcoFeeEligibleForStore when storeId is null" (){
		given:
			storeRepositoryMock.getItem(null,BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
				
		when:
			def results = testObj.isEcoFeeEligibleForStore(null)
		
		then:
			results == null
			thrown BBBBusinessException
			
	}
	
	def "isEcoFeeEligibleForStore when storeId is empty" (){
		
		given:
			storeRepositoryMock.getItem("",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
				
		when:
			def results = testObj.isEcoFeeEligibleForStore("")
		
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	/* isEcoFeeEligibleForStore - Test Cases ENDS */
	
	/* getUSAStoreDetails - Test Cases STARTS */
	
	def "getUSAStoreDetails when repository item values are passed" (){
		given:
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> repositoryItemList
		    storeRepositoryMock.getItem("3456",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			item1.setRepositoryId("3456")
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont" 
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" , 
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			def  List<StoreVO> results = testObj.getUSAStoreDetails()
		
		then:
			results[0].storeId == "3456"
			results[0].storeName == "Manalapan"
			results[0].address == "370 West Pleasant View Avenue"
			results[0].city == "Hackensack"
			results[0].state == "NJ"
			results[0].postalCode == "07601"
			results[0].displayOnline == true
			results[0].monClose == 2522
			results[0].storeSpecialityVO == null
		
	}
	
	
	def "getUSAStoreDetails when repository exception occurs" (){
		given:
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> {throw new RepositoryException("Mock Repository Exception")}
					
		when:
			def  List<StoreVO> results = testObj.getUSAStoreDetails()
		
		then:
			results == null
			thrown BBBSystemException	
	}
	
	def "getUSAStoreDetails when specialityItemSet and bopusSiteMap are empty" (){
		
		given:
		
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> repositoryItemList
			storeRepositoryMock.getItem("456",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			item1.setRepositoryId("456")
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
		
		when:
		
			def  List<StoreVO> results = testObj.getUSAStoreDetails()
		
		then:
			
			results[0].storeId == "456"
			results[0].storeName == "Manalapan"
			results[0].address == "370 West Pleasant View Avenue"
			results[0].city == "Hackensack"
			results[0].state == "NJ"
			results[0].postalCode == "07601"
			results[0].displayOnline == true
			results[0].monClose == 2522
			results[0].storeSpecialityVO == null
		
	}
	
	def "getUSAStoreDetails when repository item values are passed as null" (){
		
		given:
		
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
					  
			testObj.executeRQLQuery(testObj.getStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> repositoryItemList
			storeRepositoryMock.getItem("3456",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			item1.setRepositoryId("3456")
			
			item1.setProperties(["storeName" : null, "address" : null, "city" : null , "state" : null , "postalCode" : null,
				"countryCode" : "USA" , "phone" : null , "longitude" : null , "latitude" : null, "storeType" : null , "hours" : null , "latLongSrc" : null ,
				"rowXngDt" : null , "rowXngUser" : null , "hiringInd" : null , "facadeStoreType" : null , "commonNamePhonetic" : null , "addressPhonetic" : null ,
				"cityPhonetic" : null, "mqTransCode" : null , "displayOnline" : null , "monOpen" : null , "monClose" : null, "tuesOpen" : null , "tuesClose" : null, "wedOpen" : null,
				"wedClose" : null, "thursOpen" : null , "thursClose" : null , "friOpen" : null , "friClose" : null , "satOpen" : null , "satClose" : null , "sunOpen" : null , "sunClose" : null ,"specialMsg" : null,
				"contactFlag" : null ])
				
		when:
			def  List<StoreVO> results = testObj.getUSAStoreDetails()
		
		then:
			results[0].storeId == "3456"
			results[0].storeName == ""
			results[0].address == ""
			results[0].city == ""
			results[0].state == ""
			results[0].postalCode == ""
			results[0].displayOnline == false
			results[0].monClose == 0
		
	}
	
	def "getUSAStoreDetails when repsoitoryItem returns null" (){
		
		given:
		
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
					  
			testObj.executeRQLQuery(testObj.getStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> null
		
		when:
		
			def  List<StoreVO> results = testObj.getUSAStoreDetails()
		
		then:
			
			results == null
			thrown BBBBusinessException
	}
	
	def "getUSAStoreDetails when repsoitoryItem returns item size 0" (){
		
		given:
		
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
					  
			testObj.executeRQLQuery(testObj.getStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> []
			
		when:
		
			def  List<StoreVO> results = testObj.getUSAStoreDetails()
		
		then:
		
			results == null
			thrown BBBBusinessException
	}

	/* getUSAStoreDetails - Test Cases ENDS */
	
	/* getEcoFeeSKUDetailForStore - Test Cases START */
	
	def "getEcoFeeSKUDetailForStore by passing storeID and skuId"(){
		given:
		
		testObj = Spy()
		testObj.setStoreRepository(storeRepositoryMock)
		testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
		storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> "CA"
		globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
			.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "43690480") >> ecoFeeSKUVOMock
		
		when:
			def EcoFeeSKUVO results = testObj.getEcoFeeSKUDetailForStore("338","43690480")
		
		then:
			results == null
			thrown NullPointerException
	}
	
	def "getEcoFeeSKUDetailForStore when Repository Exception occurs"(){
		given:
		
		testObj = Spy()
		testObj.setStoreRepository(storeRepositoryMock)
		testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
		storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def EcoFeeSKUVO results = testObj.getEcoFeeSKUDetailForStore("338","43690480")
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getEcoFeeSKUDetailForStore when repository item is null"(){
		given:
		
		storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> null
		
		globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
			.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "43690480") >> ecoFeeSKUVOMock
		
		when:
			def results = testObj.getEcoFeeSKUDetailForStore("338","43690480")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForStore when province property as null"(){
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> null
			
			globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
				.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "43690480") >> ecoFeeSKUVOMock
			
		when:
			def results = testObj.getEcoFeeSKUDetailForStore("338","43690480")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForStore when province property as empty"(){
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> ""
			
			globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
				.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "43690480") >> ecoFeeSKUVOMock
			
		when:
			def results = testObj.getEcoFeeSKUDetailForStore("338","43690480")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForStore by passing storeID as null" () {
		given:
		
			storeRepositoryMock.getItem(null,BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> null
			
			globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
				.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "43690480") >> ecoFeeSKUVOMock
			
		when:
			def results = testObj.getEcoFeeSKUDetailForStore(null,"43690480")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForStore by passing storeID as empty" () {
		given:
		
			storeRepositoryMock.getItem("",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> ""
			
			globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
				.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "43690480") >> ecoFeeSKUVOMock
			
		when:
			def results = testObj.getEcoFeeSKUDetailForStore("","43690480")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForStore by passing skuId as null" () {
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> null
			
			globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
				.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), null) >> ecoFeeSKUVOMock
			
		when:
			def results = testObj.getEcoFeeSKUDetailForStore("338", null)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForStore by passing skuId as empty" () {
		given:
		
			storeRepositoryMock.getItem("338",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) >> "CA"
			
			globalRepositoryToolsMock.getEcoFeeSKUDetailForState((String) repositoryItemMock
				.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), "") >> ecoFeeSKUVOMock
			
		when:
			def results = testObj.getEcoFeeSKUDetailForStore("338", "")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	/* getEcoFeeSKUDetailForStore - Test Cases ENDS */
	
	/* getBopusInEligibleStores - Test Cases STARTS */
	
	def "getBopusInEligibleStores by passing storeId and siteId" (){
	 given:
	 
		 	testObj = Spy()
		 
		 	testObj.setBopusInEligibleStoreQuery("storeType=?0")
			testObj.setStoreRepository(storeRepositoryMock)
		 	def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item2]
			 
			testObj.executeRQLQuery(testObj.getBopusInEligibleStoreQuery(),["338"],BBBCatalogConstants.STORE_ITEM_DESCRIPTOR,storeRepositoryMock) >> repositoryItemList
			 
			Map<String, Boolean> bopusSiteMap = new HashMap<String, Boolean>()
			bopusSiteMap.put("BEDBATH_US", false)
			 
			item2.setRepositoryId("3456")
			item2.getRepositoryId() >> "3456"
			 
			item2.setProperties(["bopus" : bopusSiteMap])
			 
	 when:
	 	def List<String> results = testObj.getBopusInEligibleStores("338", "BEDBATH_US")
	 
	 then:
	 	results.size() > 0
	}
	
	def "getBopusInEligibleStores by passing bopusSiteMap as null and empty" (){
		
		given:
		
			testObj = Spy()
		
			testObj.setBopusInEligibleStoreQuery("storeType=?0")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1,item2]
			
			testObj.executeRQLQuery(testObj.getBopusInEligibleStoreQuery(),["338"],BBBCatalogConstants.STORE_ITEM_DESCRIPTOR,storeRepositoryMock) >> repositoryItemList
			
			Map<String, Boolean> bopusSiteMap = new HashMap<String, Boolean>()
			
			item1.setRepositoryId("3456")
			item2.setRepositoryId("3456")
			
			item1.setProperties(["bopus" : null])
			item2.setProperties(["bopus" : bopusSiteMap])
				
		when:
			def List<String> results = testObj.getBopusInEligibleStores("338", "BEDBATH_US")
		
		then:
			results.size() == 0
	   }
	
	def "getBopusInEligibleStores when bopusSiteMap's pSiteId null and boolean is true" (){
		
		given:
		
			testObj = Spy()
			
			testObj.setBopusInEligibleStoreQuery("storeType=?0")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1,item2]
			
			testObj.executeRQLQuery(testObj.getBopusInEligibleStoreQuery(),["338"],BBBCatalogConstants.STORE_ITEM_DESCRIPTOR,storeRepositoryMock) >> repositoryItemList
			
			Map<String, Boolean> bopusSiteMap1 = new HashMap<String, Boolean>()
			bopusSiteMap1.put(null, false)
			
			Map<String, Boolean> bopusSiteMap2 = new HashMap<String, Boolean>()
			bopusSiteMap2.put("BEDBATH_US", true)
			
			item1.setRepositoryId("3456")
			item2.setRepositoryId("3456")
			
			item1.setProperties(["bopus" : bopusSiteMap1])
			item2.setProperties(["bopus" : bopusSiteMap2])
				
		when:
			def List<String> results = testObj.getBopusInEligibleStores("338", "BEDBATH_US")
		
		then:
			results.size() == 0
			notThrown(NullPointerException)
	   }
	
	def "getBopusInEligibleStores when repsoitory item is null" (){
		
		given:
		
			testObj = Spy()
		
			testObj.setBopusInEligibleStoreQuery("storeType=?0")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item2]
			testObj.executeRQLQuery(testObj.getBopusInEligibleStoreQuery(),["338"],BBBCatalogConstants.STORE_ITEM_DESCRIPTOR,storeRepositoryMock) >> null
				
		when:
			def List<String> results = testObj.getBopusInEligibleStores("338", "BEDBATH_US")
		
		then:
			results != null
	   }
	
	def "getBopusInEligibleStores when repsoitory item is empty" (){
		
		given:
		
			testObj = Spy()
		
			testObj.setBopusInEligibleStoreQuery("storeType=?0")
			testObj.setStoreRepository(storeRepositoryMock)
		
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item2]
			testObj.executeRQLQuery(testObj.getBopusInEligibleStoreQuery(),["338"],BBBCatalogConstants.STORE_ITEM_DESCRIPTOR,storeRepositoryMock) >> []
				
		when:
			def List<String> results = testObj.getBopusInEligibleStores("338", "BEDBATH_US")
		
		then:
			results != null
	   }
	
	/* getBopusInEligibleStores - Test Cases ENDS */
	
	/* getBopusDisabledStores - Test Cases STARTS */
	
	def "getBopusDisabledStores getting list values" (){
		given:
		
			testObj = Spy()
			testObj.setBopusDisabledStoreQuery("bopus=false")
			testObj.setStoreRepository(storeRepositoryMock)
			
			def RepositoryItemMock item3 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item3]
			testObj.executeRQLQuery(testObj.getBopusDisabledStoreQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> repositoryItemList
				
		when:
			def List<String> resultList = testObj.getBopusDisabledStores()
		 
		then:
			resultList.size() > 0
	}
	
	def "getBopusDisabledStores when Repository Exception occurs" (){
		
		given:
		
			testObj = Spy()
			testObj.setBopusDisabledStoreQuery("bopus=false")
			testObj.setStoreRepository(storeRepositoryMock)
			
			def RepositoryItemMock item3 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item3]
			testObj.executeRQLQuery(testObj.getBopusDisabledStoreQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> {throw new RepositoryException("Mock Repository Exception")}
				
		when:
			def List<String> resultList = testObj.getBopusDisabledStores()
		 
		then:
			resultList == null
			thrown BBBSystemException
	}
	
	def "getBopusDisabledStores repository item is null" (){
		
		given:
		
			testObj = Spy()
			testObj.setBopusDisabledStoreQuery("bopus=false")
			testObj.setStoreRepository(storeRepositoryMock)
			
			def RepositoryItemMock item3 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item3]
					  
			testObj.executeRQLQuery(testObj.getBopusDisabledStoreQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> null
				
		when:
			def List<String> resultList = testObj.getBopusDisabledStores()
		 
		then:
			resultList.size() == 0
	}
	
	def "getBopusDisabledStores repository item length is 0" (){
		
		given:
		
			testObj = Spy()
			testObj.setBopusDisabledStoreQuery("bopus=false")
			testObj.setStoreRepository(storeRepositoryMock)
			
			def RepositoryItemMock item3 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item3]
					  
			testObj.executeRQLQuery(testObj.getBopusDisabledStoreQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> []
				
		when:
			def List<String> resultList = testObj.getBopusDisabledStores()
		 
		then:
			resultList.size() == 0
	}
	
	/* getBopusDisabledStores - Test Cases ENDS */
	
	/* getStoreAppointmentDetails - Test Cases START */
	
	def "getStoreAppointmentDetails when passing storeId"(){
		
		given:

			storeRepositoryMock.getItem("332",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> 332
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_ACCEPTING_APPOITMENTS_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENT_TYPES_PROPERTY_NAME) >> "somevalue"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_REGISTRY_APPOITMENT_TYPES_PROPERTY_NAME) >> "BRD,COM"
			Calendar calendar = Calendar.getInstance()
			java.util.Date now = calendar.getTime()
			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime())
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENTS_LAST_MODIFIED_DATE_PROPERTY_NAME) >> currentTimestamp
				
		when:
			  def StoreVO results = testObj.getStoreAppointmentDetails("332")

		then:
			results.acceptingAppointments == true
			results.appointmentTypes == ["somevalue","BRD","COM"]
	}
	
	def "getStoreAppointmentDetails when repository exception occurs"(){
		
		given:

			storeRepositoryMock.getItem("332",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
				
		when:
			  def StoreVO results = testObj.getStoreAppointmentDetails("332")

		then:
			results == null
	}
	
	def "getStoreAppointmentDetails when regAppointmentTypes is null"(){
		
		given:

			storeRepositoryMock.getItem("332",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> 332
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_ACCEPTING_APPOITMENTS_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENT_TYPES_PROPERTY_NAME) >> "somevalue"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_REGISTRY_APPOITMENT_TYPES_PROPERTY_NAME) >> null

		when:
			  def StoreVO results = testObj.getStoreAppointmentDetails("332")

		then:
			
			results.acceptingAppointments == true
			results.appointmentTypes == ["somevalue"]
	}
	
	def "getStoreAppointmentDetails when passing storeId as null"(){
		
		given:
		
		when:
			   def StoreVO results = testObj.getStoreAppointmentDetails(null)
			
		then:
			results == null
	}
	
	def "getStoreAppointmentDetails when passing storeId as empty"(){
		
		given:
		
		when:
			   def StoreVO results = testObj.getStoreAppointmentDetails("")
		then:
			results == null
	}
	
	def "getStoreAppointmentDetails when storeRepositoryItem is null"(){
		
		given:
		
		storeRepositoryMock.getItem("332",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> null
		
		when:
			   def StoreVO results = testObj.getStoreAppointmentDetails("332")
			   
		then:
			results == null
	}
	
	def "getStoreAppointmentDetails when repositoryItem property is null"(){
		
		given:
			storeRepositoryMock.getItem("332",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> 332
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_ACCEPTING_APPOITMENTS_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENT_TYPES_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_REGISTRY_APPOITMENT_TYPES_PROPERTY_NAME) >> "BRD,COM"
			
		when:
			   def StoreVO results = testObj.getStoreAppointmentDetails("332")
			
		then:
			results.acceptingAppointments == false
			results.appointmentTypes == null
			
	}
	
	/* getStoreAppointmentDetails - Test Cases ENDS */
	
	/* getCanadaStoreLocatorInfo - Test Cases STARTS */
	
	def "getCanadaStoreLocatorInfo for passed parameters"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("3232");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0].storeName == "Manalapan"
			results[0].state == "NJ"
			results[0].city == "Hackensack"
			results[0].displayOnline == true
			results[0].monOpen == 221
	}
	
	def "getCanadaStoreLocatorInfo when Repository Exception occurs"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> {throw new RepositoryException("Mock Repository Exception")}
					
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getCanadaStoreLocatorInfo when BBBSystemException occurs"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("3232");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> {throw new BBBSystemException("Mock BBBSystemException")}
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results == null
			thrown BBBSystemException
			
	}
	
	def "getCanadaStoreLocatorInfo when setting channel as MobileWeb"(){
		
		given:
			
			testObj = Spy()
			
			ServletUtil.setCurrentRequest(requestMock)
			
			requestMock.getHeader(*_) >> "MobileWeb"
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("3232");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0].storeName == "Manalapan"
			results[0].state == "NJ"
			results[0].city == "Hackensack"
			results[0].displayOnline == true
			results[0].monOpen == 221
			
	}
	
	def "getCanadaStoreLocatorInfo when request channel not as MobileWeb"(){
		given:
		
			testObj = Spy()
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			
			ServletUtil.setCurrentRequest(requestMock)
			
			requestMock.getHeader(*_) >> "MobileApp"
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("3232");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0].storeName == "Manalapan"
			results[0].state == "NJ"
			results[0].city == "Hackensack"
			results[0].displayOnline == true
			results[0].monOpen == 221
	}
	
	def "getCanadaStoreLocatorInfo when two items returned from repository"(){
		given:
		
			testObj = Spy()
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)

			ServletUtil.setCurrentRequest(requestMock)
			requestMock.getHeader(*_) >> "MobileWeb"
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1,item2]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> repositoryItemList
			item1.setRepositoryId("3232");
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			item2.setRepositoryId("3332");
			storeRepositoryMock.getItem("3332",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item2
			
			testObj.setShippingRepository(shippingRepositoryMock)
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
			item2.setProperties(["storeName" : "Alabama", "address" : "370 West Pleasant View Avenue", "city" : "NewJercy" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : false , "monOpen" : 222 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0].storeName == "Alabama"
			results[0].state == "NJ"
			results[0].city == "NewJercy"
			results[0].displayOnline == false
			results[0].monOpen == 222
			results[1].storeName == "Manalapan"
			results[1].state == "NJ"
			results[1].city == "Hackensack"
			results[1].displayOnline == true
			results[1].monOpen == 221
	}
	
	def "getCanadaStoreLocatorInfo when appointmentMap is empty"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("3232");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getShippingRepository() >> shippingRepositoryMock
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
			
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0].storeName == "Manalapan"
			results[0].state == "NJ"
			results[0].city == "Hackensack"
			results[0].displayOnline == true
			results[0].monOpen == 221
			
	}
	
	def "getCanadaStoreLocatorInfo when repository Item returns null"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> null
					
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results == null
			def e = thrown(BBBBusinessException)
			e.cause == null
	}
	
	def "getCanadaStoreLocatorInfo when repository Item returns empty"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> []
					
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results == null
			thrown BBBBusinessException
			
	}
	
	def "getCanadaStoreLocatorInfo for passsing appointmentType as null"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("3232");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getShippingRepository() >> shippingRepositoryMock
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getGlobalRepositoryTools() >> globalRepositoryToolsMock
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add(null)
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
		
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0].storeName == "Manalapan"
			results[0].state == "NJ"
			results[0].city == "Hackensack"
			results[0].displayOnline == true
			results[0].monOpen == 221
			
	}
	
	
	def "getCanadaStoreLocatorInfo for passsing canadaStoreList as empty"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getShippingRepository() >> shippingRepositoryMock
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getGlobalRepositoryTools() >> globalRepositoryToolsMock
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			
			item1.setProperties(["storeName" : null, "address" : null, "city" : null , "state" : null , "postalCode" : null,
				"countryCode" : "USA" , "phone" : null , "longitude" : null , "latitude" : null, "storeType" : null , "hours" : null , "latLongSrc" : null ,
				"rowXngDt" : null , "rowXngUser" : null , "hiringInd" : null , "facadeStoreType" : null , "commonNamePhonetic" : null , "addressPhonetic" : null ,
				"cityPhonetic" : null, "mqTransCode" : null , "displayOnline" : null , "monOpen" : null , "monClose" : null, "tuesOpen" : null , "tuesClose" : null, "wedOpen" : null,
				"wedClose" : null, "thursOpen" : null , "thursClose" : null , "friOpen" : null , "friClose" : null , "satOpen" : null , "satClose" : null , "sunOpen" : null , "sunClose" : null ,"specialMsg" : null,
				"contactFlag" : null ])
		
		
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results[0] == null
	}
	
	def "getCanadaStoreLocatorInfo for passsing logdebug as false"(){
		
		given:
			
			testObj = Spy()
			
			testObj.setLoggingError(false)
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setCanadaStoresQuery("storeType=50 and displayOnline=1")
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getCanadaStoresQuery(),BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, storeRepositoryMock) >> [item1]
			item1.setRepositoryId("");
			
			storeRepositoryMock.getItem("3232",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getShippingRepository() >> shippingRepositoryMock
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getGlobalRepositoryTools() >> globalRepositoryToolsMock
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			List<String> appointmentTypeList = new ArrayList<String>()
			appointmentTypeList.add("somevalue")
			
			testObj.getAllValuesForKey(*_) >> appointmentTypeList
			
			Map<String, Boolean> appointmentMapValue = new HashMap<String, Boolean>()
			appointmentMapValue.put("somevalue", true)
			
			testObj.setScheduleAppointmentManager(scheduleAppointmentManagerMock)
			scheduleAppointmentManagerMock.checkAppointmentAvailabilityCanada(*_) >> appointmentMapValue
			scheduleAppointmentManagerMock.fetchPreSelectedServiceRef(*_) >> 28
			scheduleAppointmentManagerMock.canScheduleAppointmentForSiteId(*_) >> true
			
			
			item1.setProperties(["storeName" : null, "address" : null, "city" : null , "state" : null , "postalCode" : null,
				"countryCode" : "USA" , "phone" : null , "longitude" : null , "latitude" : null, "storeType" : null , "hours" : null , "latLongSrc" : null ,
				"rowXngDt" : null , "rowXngUser" : null , "hiringInd" : null , "facadeStoreType" : null , "commonNamePhonetic" : null , "addressPhonetic" : null ,
				"cityPhonetic" : null, "mqTransCode" : null , "displayOnline" : null , "monOpen" : null , "monClose" : null, "tuesOpen" : null , "tuesClose" : null, "wedOpen" : null,
				"wedClose" : null, "thursOpen" : null , "thursClose" : null , "friOpen" : null , "friClose" : null , "satOpen" : null , "satClose" : null , "sunOpen" : null , "sunClose" : null ,"specialMsg" : null,
				"contactFlag" : null ])
			
		
		when:
			  def List<StoreVO> results  = testObj.getCanadaStoreLocatorInfo()
			
		then:
			results != null
	}
	
	/* getCanadaStoreLocatorInfo - Test Cases ENDS */
	
	/* setSpecialityVO - Test Cases STARTS */
	
	def "setSpecialityVO when passing parameter" () {
		given:
		
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
				
			storeDetailsMock.getSunStoreTimings() >> "sunStore"
			storeDetailsMock.getSatStoreTimings() >> "satStore"
			storeDetailsMock.getWeekdaysStoreTimings() >> "weekDayStore"
			storeDetailsMock.getSpecialtyShopsCd() >> "specialityShop"
			storeRepositoryMock.getView("specialityCodeMap") >> repositoryViewMock
			testObj.getRqlSetSpecialityVO() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["specialityShop"]) >> [item1]
			
			List<RepositoryItem> specialityItemList = new ArrayList<RepositoryItem>() 
			specialityItemList.add(repositoryItemMock)
			item1.setProperties(["specialityCd" : specialityItemList])
			
			Set<RepositoryItem> specialityItemSet = new HashSet<RepositoryItem>()
			specialityItemSet.add(repositoryItemMock)
			
			List<StoreSpecialityVO> specialityVOlistMock = new ArrayList<StoreSpecialityVO>()
			specialityVOlistMock.add(storeSpecialityVOMock)
			
			globalRepositoryToolsMock.getStoreSpecialityList(specialityItemSet) >> specialityVOlistMock 
			
		when:
			def StoreDetails results = testObj.setSpecialityVO(storeDetailsMock)  
		
		then:
			results.sunStoreTimings == "sunStore"
			results.satStoreTimings == "satStore"
			results.weekdaysStoreTimings == "weekDayStore"
			results.specialtyShopsCd == "specialityShop"
		
	}
	
	def "setSpecialityVO when specialityCd is empty" () {
		given:
		
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
				
			storeDetailsMock.getSunStoreTimings() >> "sunStore"
			storeDetailsMock.getSatStoreTimings() >> "satStore"
			storeDetailsMock.getWeekdaysStoreTimings() >> "weekDayStore"
			storeDetailsMock.getSpecialtyShopsCd() >> "specialityShop"
			storeRepositoryMock.getView("specialityCodeMap") >> repositoryViewMock
			testObj.getRqlSetSpecialityVO() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["specialityShop"]) >> [item1]
			
			List<RepositoryItem> specialityItemList = new ArrayList<RepositoryItem>()
			item1.setProperties(["specialityCd" : specialityItemList])
			
			Set<RepositoryItem> specialityItemSet = new HashSet<RepositoryItem>()
			specialityItemSet.add(repositoryItemMock)
			
			List<StoreSpecialityVO> specialityVOlistMock = new ArrayList<StoreSpecialityVO>()
			specialityVOlistMock.add(storeSpecialityVOMock)
			
			globalRepositoryToolsMock.getStoreSpecialityList(specialityItemSet) >> specialityVOlistMock
		
		when:
			def StoreDetails results = testObj.setSpecialityVO(storeDetailsMock)
		
		then:
			results.sunStoreTimings == "sunStore"
			results.satStoreTimings == "satStore"
			results.weekdaysStoreTimings == "weekDayStore"
			results.specialtyShopsCd == "specialityShop"
		
	}
	
	def "setSpecialityVO when repository Item is null" () {
		given:
		
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
				
			storeDetailsMock.getSunStoreTimings() >> null
			storeDetailsMock.getSatStoreTimings() >> null
			storeDetailsMock.getWeekdaysStoreTimings() >> null
			storeDetailsMock.getSpecialtyShopsCd() >> "specialityShop"
			storeRepositoryMock.getView("specialityCodeMap") >> repositoryViewMock
			testObj.getRqlSetSpecialityVO() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["specialityShop"]) >> null
		
		when:
			def StoreDetails results = testObj.setSpecialityVO(storeDetailsMock)
		
		then:
			results.sunStoreTimings == null
			results.satStoreTimings == null
			results.weekdaysStoreTimings == null
			results.specialtyShopsCd == "specialityShop"
	}
	
	def "setSpecialityVO when repository item[0] is null" () {
		given:
		
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
				
			storeDetailsMock.getSunStoreTimings() >> null
			storeDetailsMock.getSatStoreTimings() >> null
			storeDetailsMock.getWeekdaysStoreTimings() >> null
			storeDetailsMock.getSpecialtyShopsCd() >> "specialityShop"
			storeRepositoryMock.getView("specialityCodeMap") >> repositoryViewMock
			testObj.getRqlSetSpecialityVO() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [null,item2]
			rqlStatementMock.executeQuery(repositoryViewMock,["specialityShop"]) >>  repositoryItemList
		
		when:
			def StoreDetails results = testObj.setSpecialityVO(storeDetailsMock)
		
		then:
			results.sunStoreTimings == null
			results.satStoreTimings == null
			results.weekdaysStoreTimings == null
			results.specialtyShopsCd == "specialityShop"
	}
	
	def "setSpecialityVO when passing specialityItemList is null" () {
		given:
		
			testObj = Spy()
			
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
				
			storeDetailsMock.getSunStoreTimings() >> "sunStore"
			storeDetailsMock.getSatStoreTimings() >> "satStore"
			storeDetailsMock.getWeekdaysStoreTimings() >> "weekDayStore"
			storeDetailsMock.getSpecialtyShopsCd() >> "specialityShop"
			storeRepositoryMock.getView("specialityCodeMap") >> repositoryViewMock
			testObj.getRqlSetSpecialityVO() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["specialityShop"]) >> [item1]
			
			item1.setProperties(["specialityCd" : null])
			
			Set<RepositoryItem> specialityItemSet = new HashSet<RepositoryItem>()
			specialityItemSet.add(repositoryItemMock)
			
			List<StoreSpecialityVO> specialityVOlistMock = new ArrayList<StoreSpecialityVO>()
			specialityVOlistMock.add(storeSpecialityVOMock)
			
			globalRepositoryToolsMock.getStoreSpecialityList(specialityItemSet) >> specialityVOlistMock
		
		when:
			def StoreDetails results = testObj.setSpecialityVO(storeDetailsMock)
		
		then:
			results.sunStoreTimings == "sunStore"
			results.satStoreTimings == "satStore"
			results.weekdaysStoreTimings == "weekDayStore"
			results.specialtyShopsCd == "specialityShop"
		
	}
	
	/* setSpecialityVO - Test Cases ENDS */
	 
	/* getStoreDetails - Test Cases STARTS */
	
	def "getStoreDetails when storeId passed as empty" () {
		given:
		
		testObj = Spy()
		
		when:
			def StoreVO results = testObj.getStoreDetails("")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getStoreDetails when id of repository Item is null" () {
		given:
			testObj = Spy()
			
			testObj.setStoresQuery("storeType!=50")
			testObj.setStoreRepository(storeRepositoryMock)
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]

			storeRepositoryMock.getItem("3456",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> item1
			
			item1.setRepositoryId(null)
			
			shippingRepositoryMock.getItem("VT",BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "Vermont"
			
			Set<RepositoryItem> repositoryItemSet = new HashSet<>()
			repositoryItemSet.add(repositoryItemMock)
			
			testObj.globalRepositoryTools.getStoreSpecialityList(repositoryItemSet)
			
			Map<String,Boolean> bopusSiteMap = new HashMap<String,Boolean>()
			bopusSiteMap.put("BUYBUY", true)
			
			item1.setProperties(["storeName" : "Manalapan", "address" : "370 West Pleasant View Avenue", "city" : "Hackensack" , "state" : "NJ" , "postalCode" : "07601", "province" : "VT" ,
				"countryCode" : "USA" , "phone" : "(732) 972-0663" , "longitude" : "74.0478" , "latitude" : "40.888", "storeType" : "30" , "hours" : "Monday" , "latLongSrc" : "1" ,
				"rowXngDt" : "26-DEC-2011", "rowXngUser" : "MINGRAM" , "hiringInd" : true , "facadeStoreType" : "30" , "commonNamePhonetic" : "phone" , "addressPhonetic" : "address" ,
				"cityPhonetic" : "city", "mqTransCode" : "ASDF" , "displayOnline" : true , "monOpen" : 221 , "monClose" : 2522 , "tuesOpen" : 2212 , "tuesClose" : 111 , "wedOpen" : 22,
				"wedClose" : 21, "thursOpen" : 11 , "thursClose" : 25 , "friOpen" : 25 , "friClose" : 22 , "satOpen" : 52 , "satClose" : 52 , "sunOpen" : 52 , "sunClose" : 55 ,
				"specialMsg" : "good","contactFlag" : true , "specialityCodeId" : repositoryItemSet , "bopus" : bopusSiteMap])
		
		when:
			def StoreVO results = testObj.getStoreDetails("3456")
		
		then:
			results.storeName == "Manalapan"
			results.address == "370 West Pleasant View Avenue"
			results.storeId == null
	}
	
	def "getStoreDetails when storeId passed as null" () {
		given:
		
		testObj = Spy()
		
		when:
			def StoreVO results = testObj.getStoreDetails(null)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getStoreDetails when repository item returns null" () {
		given:
		
		testObj = Spy()
		testObj.setStoreRepository(storeRepositoryMock)
		storeRepositoryMock.getItem("1424",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> null
		
		when:
			def StoreVO results = testObj.getStoreDetails("1424")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getStoreDetails when repository exception occurs" () {
		given:
		
		testObj = Spy()
		testObj.setStoreRepository(storeRepositoryMock)
		storeRepositoryMock.getItem("1424",BBBCatalogConstants.STORE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def StoreVO results = testObj.getStoreDetails("1424")
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	/* getStoreDetails - Test Cases ENDS */

}
