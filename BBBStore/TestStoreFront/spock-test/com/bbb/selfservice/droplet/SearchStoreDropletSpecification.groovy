package com.bbb.selfservice.droplet

import atg.multisite.Site
import atg.multisite.SiteContext
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.RequestAddressType;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreAddressSuggestion
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper
import com.bbb.selfservice.manager.SearchStoreManager

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class SearchStoreDropletSpecification extends BBBExtendedSpec {
	
	SearchStoreDroplet testObj
	SearchStoreManager searchStoreManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	SiteContext siteContextMock = Mock()
	Site siteMock = Mock()
	StoreDetails storeDetailsMock = Mock()
	StoreDetailsWrapper storeDetailsWrapperMock = Mock()
	StoreAddressSuggestion StoreAddressSuggestionMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	
	def setup(){
		testObj = new SearchStoreDroplet(searchStoreManager:searchStoreManagerMock,catalogTools:catalogToolsMock,siteContext:siteContextMock,radius:"25",staticMapZoom:"7",staticMapSize:"400,400")
	}
	
	////////////////////////////////////////TestCases for service --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) ///////////
	
	def"service method.This TC is the Happy flow of service method"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "cookie"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> "table"
			requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "2345"
			1 * searchStoreManagerMock.searchStoreById("2345", siteMock.getId(), "Merchandize") >> storeDetailsMock
			StoreVO storeVOMock = new StoreVO(contactFlag:TRUE)
			1 * catalogToolsMock.getStoreDetails("2345") >> storeVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * storeDetailsMock.setContactFlag(true)
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS,	storeDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId is empty and has value for pageKey,pagenumber,getStoreAddressSuggestion"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> ["configType"]
			requestMock.getParameter("suggestion") >> TRUE
			requestMock.getParameter("address") >> "xx street"
			requestMock.getParameter("city") >> "New Jercy"
			requestMock.getParameter("stateCode") >> "NJ"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> "2"
			1 * searchStoreManagerMock.searchStorePerPage(requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY),requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER)) >> storeDetailsWrapperMock 
			storeDetailsWrapperMock.getStoreAddressSuggestion() >> [storeAddressSuggestionMock]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER, storeDetailsWrapperMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ADDRESSSUGGESTION, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId, pageNumber is empty and searchType and searchString has values"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> ""
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "12records"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> "available"
			1 * searchStoreManagerMock.searchStoreByAddress("available", "category", "12records") >> storeDetailsWrapperMock
			requestMock.getSession().getAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME) >> "glass"
			StoreDetails storeDetailsMock1 = Mock()
			storeDetailsWrapperMock.getStoreDetails() >> [storeDetailsMock,storeDetailsMock1]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.INPUTSEARCHSTRING, "glass")
			1 * requestMock.setParameter(BBBCoreConstants.TYPE, "category")
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER, storeDetailsWrapperMock)
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS_FOR_REGISTLRY, storeDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT,requestMock, responseMock)
	}
	
	
	def"service method.This TC is when storeId,keylist is empty and getContactFlag is false"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> []
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "2345"
			1 * searchStoreManagerMock.searchStoreById("2345", siteMock.getId(), _) >> storeDetailsMock
			StoreVO storeVOMock = new StoreVO(contactFlag:FALSE)
			1 * catalogToolsMock.getStoreDetails("2345") >> storeVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * storeDetailsMock.setContactFlag(false)
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS,	storeDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method.This TC is when pageSize is null and searchType is 4 and getStoreDetails() is empty"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "cookie"
			2 * requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "4"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> null
			2 * requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> "available" 
			1 * searchStoreManagerMock.searchStoreByCoordinates("available", "4", null) >> storeDetailsWrapperMock
			1 * requestMock.getSession().getAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME) >> "glass"
			storeDetailsWrapperMock.getStoreDetails() >> []
			storeDetailsWrapperMock.getStoreAddressSuggestion() >> [StoreAddressSuggestionMock]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.INPUTSEARCHSTRING, "glass")
			1 * requestMock.setParameter(BBBCoreConstants.TYPE, "4")
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER, storeDetailsWrapperMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ADDRESSSUGGESTION, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId, pageNumber is empty and BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSearchStoreManager(searchStoreManagerMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> ""
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "12records"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> null
			1 * searchStoreManagerMock.searchStoreByAddress(_, "category", "12records") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERRORMESSAGE, 'nullMock for BBBBusinessException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logDebug('Call to set page size in Search Store Manager . Page Size [12records] ')
			1 * testObj.logError('account_1208: BBBBusinessException in SearchStoreDroplet while Store Search based on input address', _)
			
		}
	
	def"service method.This TC is when BBBSystemException thrown in keysList and BBBBusinessException thrown in getStoreDetails"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "2345"
			1 * searchStoreManagerMock.searchStoreById("2345", siteMock.getId(), _) >> storeDetailsMock
			1 * catalogToolsMock.getStoreDetails("2345") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * storeDetailsMock.setContactFlag(false)
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILS,	storeDetailsMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
	}
	
	def"service method.This TC is when BBBBusinessException thrown in keysList and searchStoreById returns null"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> "2345"
			1 * searchStoreManagerMock.searchStoreById("2345", siteMock.getId(), _) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
	
	def"service method.This TC is when BBBSystemException thrown in getStoreType"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSearchStoreManager(searchStoreManagerMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('errorMessages', 'nullMock for BBBSystemException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logError('account_1206: err_store_search_tech_error', _)
	}
	
	def"service method.This TC is when BBBBusinessException thrown in getStoreType"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSearchStoreManager(searchStoreManagerMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('errorMessages', 'nullMock for BBBBusinessException')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * testObj.logError('account_1207: err_store_search_tech_error', _)
	}
	
	def"service method.This TC is when searchType is empty"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "cookie"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> "table"
			requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> ""
			
		expect:
			testObj.service(requestMock, responseMock)
			
	}
	
	def"service method.This TC is when searchString is empty"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "cookie"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> ""
			requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "searchType"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> ""
			
		expect:
			testObj.service(requestMock, responseMock)
			
	}
	
	def"service method.This TC is when storeId is empty, PageKey and PageNumber has value but objStoreDetailsWrapper has null value"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> ["configType"]
			requestMock.getParameter("suggestion") >> TRUE
			requestMock.getParameter("address") >> "xx street"
			requestMock.getParameter("city") >> "New Jercy"
			requestMock.getParameter("stateCode") >> "NJ"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> "2"
			1 * searchStoreManagerMock.searchStorePerPage("key","2") >> null

			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId is empty, PageKey and PageNumber has value and BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSearchStoreManager(searchStoreManagerMock)
			testObj.setCatalogTools(catalogToolsMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> ["configType"]
			requestMock.getParameter("suggestion") >> TRUE
			requestMock.getParameter("address") >> "xx street"
			requestMock.getParameter("city") >> "New Jercy"
			requestMock.getParameter("stateCode") >> "NJ"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> "2"
			1 * searchStoreManagerMock.searchStorePerPage("key","2") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter('error',requestMock, responseMock)
			1 * requestMock.setParameter('errorMessage', 'nullMock for BBBSystemException')
			1 * testObj.logError('account_1209: BBBSystemException in SearchStoreDroplet while Store Search based on input address', _)
	}
	
	def"service method.This TC is when storeId is empty, PageKey and PageNumber has value and getStoreAddressSuggestion is null"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> "size"
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "category"
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> "35"
			1 * catalogToolsMock.getAllValuesForKey("MapQuestStoreType","radius_default_ca") >> ["configType"]
			requestMock.getParameter("suggestion") >> TRUE
			requestMock.getParameter("address") >> "xx street"
			requestMock.getParameter("city") >> "New Jercy"
			requestMock.getParameter("stateCode") >> "NJ"
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> "2"
			1 * searchStoreManagerMock.searchStorePerPage("key","2") >> storeDetailsWrapperMock
			storeDetailsWrapperMock.getStoreAddressSuggestion() >> null
			
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.STOREDETAILSWRAPPER,	storeDetailsWrapperMock)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,	responseMock)
		
	}
	
	def"service method.This TC is when storeId, pageNumber is empty and getStoreDetails,getStoreAddressSuggestion is null"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> ""
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "12records"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> "available"
			1 * searchStoreManagerMock.searchStoreByAddress("available", "BBB", "12records") >> storeDetailsWrapperMock
			requestMock.getSession().getAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME) >> "glass"
			requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "BBB"
			StoreDetails storeDetailsMock1 = Mock()
			storeDetailsWrapperMock.getStoreDetails() >> null
			storeDetailsWrapperMock.getStoreAddressSuggestion() >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('inputSearchString', 'glass')
			1 * requestMock.setParameter('type', 'BBB')
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId, pageNumber is empty and objStoreDetailsWrapper is null"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * searchStoreManagerMock.getStoreType(siteMock.getId()) >> "Merchandize"
			requestMock.getParameter(BBBCoreConstants.SEARCHBASEDON) >> ""
			requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "table"
			requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
			requestMock.getParameter(BBBCoreConstants.RADIUS) >> ""
			requestMock.getParameter("suggestion") >> FALSE
			requestMock.getObjectParameter(BBBCoreConstants.STOREID) >> ""
			requestMock.getObjectParameter(BBBCoreConstants.PAGEKEY) >> "key"
			requestMock.getObjectParameter(BBBCoreConstants.PAGENUMBER) >> ""
			requestMock.getParameter(BBBCoreConstants.PAGESIZE) >> "12records"
			requestMock.getSession().getAttribute(BBBCoreConstants.STATUS) >> "available"
			1 * searchStoreManagerMock.searchStoreByAddress("available", "BBB", "12records") >> null
			requestMock.getSession().getAttribute(SelfServiceConstants.STORESEARCHINPUTSTRINGCOOKIENAME) >> "glass"
			2 * requestMock.getSession().getAttribute(BBBCoreConstants.TYPE) >> "BBB"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.INPUTSEARCHSTRING, "glass")
			1 * requestMock.setParameter(BBBCoreConstants.TYPE, "BBB")
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
	}
	
	//////////////////////////////////////TestCases for service --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getStaticMapKey --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public String getStaticMapKey() ///////////
	
	def"getStaticMapKey. This TC is the Happy flow of getStaticMapKey"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.STATICMAPKEY) >> ["configValue"]
		when:
			def results = testObj.getStaticMapKey()
		then:
			results == "configValue"
	}
	
	def"getStaticMapKey. This TC is when keysList is empty"(){
		given:
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,BBBCoreConstants.STATICMAPKEY) >> []
		when:
			def results = testObj.getStaticMapKey()
		then:
			results == null
	}
	
	//////////////////////////////////////TestCases for getStaticMapKey --> ENDS//////////////////////////////////////////////////////////
	
	

}
