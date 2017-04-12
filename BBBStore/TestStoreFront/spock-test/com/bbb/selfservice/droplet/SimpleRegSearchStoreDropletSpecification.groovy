package com.bbb.selfservice.droplet

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.RequestAddressType;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreAddressSuggestion;
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper
import com.bbb.selfservice.manager.SearchStoreManager

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
import weblogic.wtc.jatmi.ReqOid;
/**
 * 
 * @author kmagud
 *
 */
class SimpleRegSearchStoreDropletSpecification extends BBBExtendedSpec {

	SimpleRegSearchStoreDroplet testObj
	SearchStoreManager searchStoreManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	StoreDetails objStoreDetailsMock = Mock()
	StoreDetailsWrapper objStoreDetailsWrapper = Mock()
	StoreAddressSuggestion storeAddressSuggestionMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = Spy()
		testObj.setSearchStoreManager(searchStoreManagerMock)
		testObj.setCatalogTools(catalogToolsMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >> ["configValue","configValue1"]
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "23251" 
			1 * searchStoreManagerMock.searchStoreById("23251", siteId, "storeType") >> objStoreDetailsMock
			StoreVO StoreVOMock = new StoreVO(contactFlag:TRUE)
			1 * catalogToolsMock.getStoreDetails("23251") >> StoreVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS,	objStoreDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			testObj.getRadius().equals('configValue')
			1 * objStoreDetailsMock.setContactFlag(true)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "configValue")
	}
	
	def"service method. This TC is when objStoreDetails returns null"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			sessionMock.getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >> []
			1 * requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "23251"
			1 * searchStoreManagerMock.searchStoreById("23251", siteId, "storeType") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			testObj.getRadius().equals("100")
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "100")
	}
	
	def"service method. This TC is when isContactFlag is false"(){
		given:
			testObj.setRadius("25")
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			sessionMock.getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "23251"
			1 * searchStoreManagerMock.searchStoreById("23251", siteId, "storeType") >> objStoreDetailsMock
			StoreVO StoreVOMock = new StoreVO(contactFlag:FALSE)
			1 * catalogToolsMock.getStoreDetails("23251") >> StoreVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			testObj.getRadius().equals("25")
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS, objStoreDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			1 * objStoreDetailsMock.setContactFlag(false)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "25")
	}
	
	def"service method. This TC is BBBBusinessException thrown when objStoreDetails is not null"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "23251"
			1 * searchStoreManagerMock.searchStoreById("23251", siteId, "storeType") >> objStoreDetailsMock
			1 * catalogToolsMock.getStoreDetails("23251") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS,	objStoreDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
			testObj.getRadius().equals("35")
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "35")
	}
	
	def"service method. This TC is when storeId is empty and getStoreDetails of objStoreDetailsWrapper has values"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >>  ["configValue"]
			1 * requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "2"
			1 * sessionMock.getAttribute(BBBCoreConstants.STATUS) >> "available"
			StoreDetails objStoreDetailsMock1 = Mock()
			objStoreDetailsWrapper.getStoreDetails() >> [objStoreDetailsMock,objStoreDetailsMock1]
			1 * searchStoreManagerMock.searchStoreByAddress("available","category", "2") >> objStoreDetailsWrapper
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER,objStoreDetailsWrapper)
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS_FOR_REGISTLRY, objStoreDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT,requestMock, responseMock)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "configValue")
			testObj.getRadius().equals("configValue")
	}
	
	def"service method. This TC is when storeId is empty and getStoreDetails of objStoreDetailsWrapper is empty"(){
		given:
			String siteId = "BedBathUS"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "2"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "3"
		
			1 * searchStoreManagerMock.searchStoreByAddress("location=".concat("table"),"2", "3") >> objStoreDetailsWrapper
			objStoreDetailsWrapper.getStoreDetails() >> []
			objStoreDetailsWrapper.getStoreAddressSuggestion() >> null
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "35")
			testObj.getRadius().equals("35")
			1 * sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "storeType")
			1 * sessionMock.setAttribute(BBBCoreConstants.STATUS,"location=".concat("table"))
			1 * sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
			1 * sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "")
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "")
			1 * sessionMock.setAttribute(BBBCoreConstants.STATUS,"")
	}
	
	def"service method. This TC is when storeId is empty and searchType is empty"(){
		given:
			String siteId = "BedBathUS"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			sessionMock.getAttribute(BBBCoreConstants.TYPE) >> ""
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "3"
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			testObj.getRadius().equals("35")
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,	"35")
	}
	
	def"service method. This TC is when storeId is empty and getStoreDetails of objStoreDetailsWrapper is null"(){
		given:
			String siteId = "BedBathUS"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "2"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "3"
			
			1 * searchStoreManagerMock.searchStoreByAddress("location=".concat("table"),"2", "3") >> objStoreDetailsWrapper
			objStoreDetailsWrapper.getStoreDetails() >> null
			objStoreDetailsWrapper.getStoreAddressSuggestion() >> [storeAddressSuggestionMock]
					
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER, objStoreDetailsWrapper)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ADDRESSSUGGESTION, requestMock,responseMock)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "35")
			testObj.getRadius().equals("35")
			1 * sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "storeType")
			1 * sessionMock.setAttribute(BBBCoreConstants.STATUS,"location=".concat("table"))
			1 * sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
			1 * sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "")
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES, "")
			1 * sessionMock.setAttribute(BBBCoreConstants.STATUS,"")
	}
	
	def"service method. This TC is when storeId is empty and when objStoreDetailsWrapper is null"(){
		given:
			String siteId = "BedBathUS"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,	"35")
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "3"
			sessionMock.getAttribute(BBBCoreConstants.STATUS) >> "status"
			1 * searchStoreManagerMock.searchStoreByAddress("status","searchtype", "3") >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown from searchStoreById"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			sessionMock.getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >> []
			1 * requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "23251"
			1 * searchStoreManagerMock.searchStoreById("23251", siteId, "storeType") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERRORMESSAGE,'nullMock for BBBSystemException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logError('account_1209: BBBSystemException in SimpleRegSearchStoreDroplet while Store Search based on input address', _)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,	"100")
			
	}
	
	def"service method. This TC is when BBBBusinessException thrown from searchStoreById"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> "storeType"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			sessionMock.getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType", "radius_default_ca") >> []
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "23251"
			1 * searchStoreManagerMock.searchStoreById("23251", siteId, "storeType") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERRORMESSAGE,'nullMock for BBBBusinessException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logError('account_1208: BBBBusinessException in SimpleRegSearchStoreDroplet while Store Search based on input address', _)
			1 * sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,	"100")
	}
	
	def"service method. This TC is when BBBSystemException thrown from getStoreType"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('errorMessages', 'nullMock for BBBSystemException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logError('account_1206: err_store_search_tech_error', _)
	}
	
	def"service method. This TC is when BBBBusinessException thrown from getStoreType"(){
		given:
			String siteId = "BedBathCanada"
			testObj.getCurrentSiteId() >> siteId
			1 * searchStoreManagerMock.getStoreType(siteId) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('errorMessages', 'nullMock for BBBBusinessException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logError('account_1207: err_store_search_tech_error', _)
	}

	
}
