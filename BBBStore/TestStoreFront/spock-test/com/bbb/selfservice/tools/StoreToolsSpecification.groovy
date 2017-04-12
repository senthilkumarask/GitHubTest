package com.bbb.selfservice.tools

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.sql.DataSource

import org.apache.commons.beanutils.DynaBean;

import spock.lang.specification.BBBExtendedSpec
import atg.adapter.gsa.GSARepository
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement
import atg.servlet.ServletUtil
import net.sf.ezmorph.bean.MorphDynaBean
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.bbb.cache.LocalStoreVO
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.commerce.catalog.vo.ThresholdVO
import com.bbb.commerce.inventory.BBBInventoryManagerImpl
import com.bbb.constants.BBBCoreConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.CoherenceCacheContainer
import com.bbb.framework.httpquery.HTTPCallInvoker
import com.bbb.repository.RepositoryItemMock
import com.bbb.selfservice.common.RouteDetails;
import com.bbb.selfservice.common.SelfServiceConstants
import com.bbb.selfservice.common.StoreAddressSuggestion
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper

class StoreToolsSpecification extends BBBExtendedSpec {

	StoreTools sToolsObj
	BBBCatalogToolsImpl cTools = Mock()
	BBBInventoryManagerImpl iManager  =Mock()
	CoherenceCacheContainer cContainer = Mock()
	MutableRepository mRepository = Mock()
	HTTPCallInvoker callInvoker  = Mock()
	MutableRepository mRepo =Mock()
	RepositoryView rView = Mock()
	RqlStatement rqlSt = Mock()
	DataSource dataSrc = Mock()
	Connection conn = Mock()
	PreparedStatement pStata = Mock()
	ResultSet rs = Mock()
	ThresholdVO thresHoldVO =Mock()
	def setup(){
		sToolsObj = new StoreTools(catalogTools:cTools,localStoreRepository:mRepository,regularExpression:/^[1-9]/,cacheContainer:cContainer,
		inventoryManager:iManager,httpCallInvoker:callInvoker)
	}

	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is COORDINATES BASED SEARCH "(){
	given:
	sToolsObj = Spy()
	spyObjIntilization(sToolsObj)
	sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> BBBCoreConstants.STORE_TYPE
	sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
	sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
	StoreDetailsWrapper wrap = Mock()
	sToolsObj.getStoreDetails(SelfServiceConstants.LAT, SelfServiceConstants.LNG, BBBCoreConstants.STORE_TYPE) >> wrap
	when:
	StoreDetailsWrapper wrapper =sToolsObj.searchStore(SelfServiceConstants.COORDINATES_BASED_SEARCH, "pSearchString")
	then:
	wrapper == wrap
	}
	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is ADDRESS BASED STORE SEARCH "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> BBBCoreConstants.STORE_TYPE
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.getStoreDetails(SelfServiceConstants.LAT, SelfServiceConstants.LNG, BBBCoreConstants.STORE_TYPE) >> wrap
		sessionMock.getAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE) >> "true"
		Cookie latLngCookie = Mock()
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT_LNG_COOKIE
		requestMock.getCookies() >>[latLngCookie]
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore(SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH, "pSearchString")
		then:
		wrapper == wrap
		}
	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is ZIPCODE BASED STORE SEARCH "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> BBBCoreConstants.STORE_TYPE
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.getStoreDetailsByLatLng("pSearchString", "pSearchString", BBBCoreConstants.STORE_TYPE, requestMock, responseMock) >> wrap
		Cookie latLngCookie = Mock()
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> "pSearchString"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH, "pSearchString")
		then:
		wrapper == wrap
		}
	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is ZIPCODE BASED STORE SEARCH and search result is null "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> BBBCoreConstants.STORE_TYPE
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.getStoreDetailsByLatLng("pSearchString", "pSearchString", BBBCoreConstants.STORE_TYPE, requestMock, responseMock) >> wrap
		Cookie latLngCookie = Mock()
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> null
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH, "pSearchString")
		then:
		wrapper == null
		}
	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is ZIPCODE BASED STORE SEARCH and searchBasedOnLatLngCookie as false "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> BBBCoreConstants.STORE_TYPE
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.getStoreDetailsByLatLng("pSearchString", "pSearchString", BBBCoreConstants.STORE_TYPE, requestMock, responseMock) >> wrap
		Cookie latLngCookie = Mock()
		sessionMock.getAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE) >> "false"
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> "pSearchString"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH, "pSearchString")
		then:
		wrapper == wrap
		}
	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is ZIPCODE BASED STORE SEARCH and searchBasedOnLatLngCookie as false ans search result is empty "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> BBBCoreConstants.STORE_TYPE
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.getStoreDetailsByLatLng("pSearchString", "pSearchString", BBBCoreConstants.STORE_TYPE, requestMock, responseMock) >> wrap
		Cookie latLngCookie = Mock()
		sessionMock.getAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE) >> "false"
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> null
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore(SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH, "pSearchString")
		then:
		wrapper == null
		}
	def "Search store method sends a MapQuest request via MapQuestService class and get the Latitude and longitude serch type is empty and searchBasedOnLatLngCookie as false ans search result is empty "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> null
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.storeJSONObjectParser("pSearchString", "pSearchString") >> wrap
		Cookie latLngCookie = Mock()
		sessionMock.getAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE) >> "false"
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> "pSearchString"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore("", "pSearchString")
		then:
		wrapper == wrap
		}
	def "Search store method sends a MapQuest request via MapQuestService class throw business exception"(){
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> null
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.storeJSONObjectParser("pSearchString", "pSearchString") >> wrap
		Cookie latLngCookie = Mock()
		sessionMock.getAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE) >> "false"
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> {throw new BBBBusinessException("Mock of Business exception")}
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore("", "pSearchString")
		then:
		wrapper == null
		}
	def "Search store method sends a MapQuest request via MapQuestService class throw System exception"(){
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(BBBCoreConstants.STORE_TYPE) >> null
		sessionMock.getAttribute(SelfServiceConstants.LAT) >> SelfServiceConstants.LAT
		sessionMock.getAttribute(SelfServiceConstants.LNG) >> SelfServiceConstants.LNG
		StoreDetailsWrapper wrap = Mock()
		sToolsObj.storeJSONObjectParser("pSearchString", "pSearchString") >> wrap
		Cookie latLngCookie = Mock()
		sessionMock.getAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE) >> "false"
		latLngCookie.getValue() >> "Lng,Lat"
		latLngCookie.getName()>>  SelfServiceConstants.LAT
		requestMock.getCookies() >>[latLngCookie]
		1*callInvoker.executeQuery("pSearchString") >> {throw new BBBSystemException("Mock of System exception")}
		when:
		StoreDetailsWrapper wrapper =sToolsObj.searchStore("", "pSearchString")
		then:
		wrapper == null
		}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec"(){
		given:
		String jsonValue= "{info:{statusCode:0},totalPages:1,options:{units:'10',pageKey:'pageKey',currentPage:1},resultsCount:1,searchResults:[{fields:{display_online:'n'}},{fields:{hours:'',postal:'43123',Lng:-79.677195,Lat:39.875542,specialty_shops_cd:0,store_type:0}},{fields:{hours:'',postal:'43123',Lng:null,Lat:null,specialty_shops_cd:null,store_type:null},distance:20,resultnumber:1},{fields1:{display_online:'n'}},{fields:{display_online:null,postal:'00000'}},{fields:{'country':'USA','Lng':-83.043389,'WED_CLOSE':null,'HBC':0,'FRI_OPEN':null,'SAT_OPEN':null,'SUN_CLOSE':null,'TUES_CLOSE':null,'SUN_OPEN':null,'address_phonetic':'','VERDI':0,'name_phonetic':'','THURS_CLOSE':null,'state':'OH','mqap_quality':'U1XXX','row_xng_usr':'STOREOPS','store_type':10,'FTG':0,'postal':'43123','STUDIO':0,'specialty_shops_cd':0,'city':'Grove City','SAT_CLOSE':null,'city_phonetic':'','N':'Grove City','WED_OPEN':null,'TUES_OPEN':null,'T':'3100','BW':0,'Phone':'(614) 539-4999','WM':0,'THURS_OPEN':null,'RecordId':'787','SPECIAL_MSG':'','mqap_geography':{'latLng':{'lng':-83.043389,'lat':39.878697}},'display_online':'Y','mqap_id':'d091653d-9bb2-41de-b63a-35ef7a345e18','hours':'Monday-Friday: 9:00am - 9:00pm , Saturday: 9:00am - 9:00pm , Sunday: 10:00am - 7:00pm','address':'1747 Stringtown Road','row_xng_dt':'07/05/2016','GeoCodeQuality':'','MON_CLOSE':null,'facade_store_type':10,'latlong_src':null,'lc':'0','hiring_ind':'N','FRI_CLOSE':null,'HD':0,'MON_OPEN':null,'Lat':39.878697}}]}"
		StoreVO sVo = new StoreVO()
		1*cTools.getStoreDetails("787") >> sVo
		sVo.setContactFlag(true)
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper!= null
		wrapper.getPageKey().equals("pageKey")
		wrapper.getCurrentPage() == 1
		wrapper.getTotalPageCount() == 1
		wrapper.getStoreDetails().size() == 3
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec throw Business exception"(){
		given:
		String jsonValue= "{info:{statusCode:0},totalPages:1,options:{units:'10',pageKey:'pageKey',currentPage:1},resultsCount:1,searchResults:[{fields:{'country':'USA','Lng':-83.043389,'WED_CLOSE':null,'HBC':0,'FRI_OPEN':null,'SAT_OPEN':null,'SUN_CLOSE':null,'TUES_CLOSE':null,'SUN_OPEN':null,'address_phonetic':'','VERDI':0,'name_phonetic':'','THURS_CLOSE':null,'state':'OH','mqap_quality':'U1XXX','row_xng_usr':'STOREOPS','store_type':10,'FTG':0,'postal':'43123','STUDIO':0,'specialty_shops_cd':0,'city':'Grove City','SAT_CLOSE':null,'city_phonetic':'','N':'Grove City','WED_OPEN':null,'TUES_OPEN':null,'T':'3100','BW':0,'Phone':'(614) 539-4999','WM':0,'THURS_OPEN':null,'RecordId':'787','SPECIAL_MSG':'','mqap_geography':{'latLng':{'lng':-83.043389,'lat':39.878697}},'display_online':'Y','mqap_id':'d091653d-9bb2-41de-b63a-35ef7a345e18','hours':' ','address':'1747 Stringtown Road','row_xng_dt':'07/05/2016','GeoCodeQuality':'','MON_CLOSE':null,'facade_store_type':10,'latlong_src':null,'lc':'0','hiring_ind':'N','FRI_CLOSE':null,'HD':0,'MON_OPEN':null,'Lat':39.878697}}]}"
		StoreVO sVo = new StoreVO()
		1*cTools.getStoreDetails("787") >> {throw new BBBBusinessException("mock of business exception")}
		sVo.setContactFlag(true)
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper!= null
		wrapper.getPageKey().equals("pageKey")
		wrapper.getCurrentPage() == 1
		wrapper.getTotalPageCount() == 1
		wrapper.getStoreDetails().size() == 1
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec throw System exception"(){
		given:
		String jsonValue= "{info:{statusCode:0},totalPages:1,options:{units:'10',pageKey:'pageKey',currentPage:1},resultsCount:1,searchResults:[{fields:{display_online:'n'}},{fields:{hours:'',postal:'43123'}},{fields:{hours:'',Lng:null,Lat:null,specialty_shops_cd:null,store_type:null},distance:20,resultnumber:1},{fields1:{display_online:'n'}},{fields:{display_online:null,postal:'00000'}},{fields:{'country':'USA','Lng':-83.043389,'WED_CLOSE':null,'HBC':0,'FRI_OPEN':null,'SAT_OPEN':null,'SUN_CLOSE':null,'TUES_CLOSE':null,'SUN_OPEN':null,'address_phonetic':'','VERDI':0,'name_phonetic':'','THURS_CLOSE':null,'state':'OH','mqap_quality':'U1XXX','row_xng_usr':'STOREOPS','store_type':10,'FTG':0,'postal':'43123','STUDIO':0,'specialty_shops_cd':0,'city':'Grove City','SAT_CLOSE':null,'city_phonetic':'','N':'Grove City','WED_OPEN':null,'TUES_OPEN':null,'T':'3100','BW':0,'Phone':'(614) 539-4999','WM':0,'THURS_OPEN':null,'RecordId':'787','SPECIAL_MSG':'','mqap_geography':{'latLng':{'lng':-83.043389,'lat':39.878697}},'display_online':'Y','mqap_id':'d091653d-9bb2-41de-b63a-35ef7a345e18','hours':'Monday-Friday: 9:00am - 9:00pm , Saturday: 9:00am - 9:00pm , Sunday: 10:00am - 7:00pm,BlackMonday: 8:00am - 8:00pm,CyberDay: 8:00am - 8:00pm','address':'1747 Stringtown Road','row_xng_dt':'07/05/2016','GeoCodeQuality':'','MON_CLOSE':null,'facade_store_type':10,'latlong_src':null,'lc':'0','hiring_ind':'N','FRI_CLOSE':null,'HD':0,'MON_OPEN':null,'Lat':39.878697}}]}"
		StoreVO sVo = new StoreVO()
		1*cTools.getStoreDetails("787") >> {throw new BBBSystemException("mock of business exception")}
		sVo.setContactFlag(true)
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper!= null
		wrapper.getPageKey().equals("pageKey")
		wrapper.getCurrentPage() == 1
		wrapper.getTotalPageCount() == 1
		wrapper.getStoreDetails().size() == 3
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec recordCount is zero"(){
		given:
		String jsonValue= "{info:{statusCode:0}}"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper == null
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec search result is null"(){
		given:
		String jsonValue= "{info:{statusCode:0},totalPages:1,options:{},resultsCount:1}"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper.getPageKey() ==  null
		wrapper.getCurrentPage() == 0
		wrapper.getTotalPageCount() == 1
		wrapper.getStoreDetails().size() == 0
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec status code is 610"(){
		given:
		String jsonValue= "{info:{statusCode:610},collections:[[{adminArea5:'adminArea5'}],[{adminArea4:'adminArea4'}],[{adminArea3:'adminArea3'}],[{street:'street'}]]}"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper != null
		wrapper.getStoreAddressSuggestion().size() == 4
		StoreAddressSuggestion addrsug0 = wrapper.getStoreAddressSuggestion().get(0)
		addrsug0.getCity().equals("adminArea5")
		StoreAddressSuggestion addrsug1 = wrapper.getStoreAddressSuggestion().get(1)
		addrsug1.getAddress().equals("adminArea4")
		StoreAddressSuggestion addrsug2 = wrapper.getStoreAddressSuggestion().get(2)
		addrsug2.getStateCode().equals("adminArea3")
		StoreAddressSuggestion addrsug3 = wrapper.getStoreAddressSuggestion().get(3)
		addrsug3.getStreet().equals("street")
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec status code is 610 throw Morph Exception "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		String jsonValue= "{info:{statusCode:610}}"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper == null
		1*sToolsObj.logError("MorphException in storeJSONObjectParser statusCode610")
	}
	def "This method is used to parse StoreJSON object and populate the store details in StoreDetailsWrapper objec status code is info not availble"(){
		given:
		String jsonValue= "{info:{statusCode:1,messages:'search call is falied'}}"
		when:
		StoreDetailsWrapper wrapper =sToolsObj.storeJSONObjectParser(jsonValue, "pRequestString")
		then:
		wrapper == null
		Exception ex = thrown()
		ex.getMessage().equals("1:search call is falied")
	}
	def"This method get store details when request is from mobile"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		sToolsObj.getStoreDetails("latitude", "longitude","storeType") >> sWrapperMock
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "true"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("latitude", "longitude", "searchString", "storeType")
		then:
		sWrapper == sWrapperMock
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		1*httpres.addHeader("Set-Cookie", _)
	}
	def"This method get store details when request is from mobile it is not from current location"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		sToolsObj.getStoreDetails("latitude", "longitude","storeType") >> sWrapperMock
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "false"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("latitude", "longitude", "searchString", "storeType")
		then:
		sWrapper == sWrapperMock
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method get store details when request is from mobile it is not from current location and request object is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		sToolsObj.getStoreDetails("latitude", "longitude","storeType") >> sWrapperMock
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "false"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		ServletUtil.setCurrentRequest(null)
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("latitude", "longitude", "searchString", "storeType")
		then:
		sWrapper == sWrapperMock
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method get store details when request is from mobile lognitude is null and search result is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		sToolsObj.getStoreDetails("latitude", "longitude","storeType") >> sWrapperMock
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "true"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		1*callInvoker.executeQuery("searchString") >> null
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("latitude", "", "searchString", "storeType")
		then:
		sWrapper == null
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method get store details when request is from mobile lognitude is null and search result throw exception"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		sToolsObj.getStoreDetails("latitude", "longitude","storeType") >> sWrapperMock
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "true"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		1*callInvoker.executeQuery("searchString") >> {throw new Exception()}
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("latitude", "", "searchString", "storeType")
		then:
		sWrapper == null
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
		Exception ex = thrown()
	}
	def"This method get store details when request is from mobile lognitude is null and searching string empty"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "true"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		0*callInvoker.executeQuery("searchString") >> {throw new Exception()}
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("latitude", "", "", "storeType")
		then:
		sWrapper == null
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method get store details when request is from mobile latitude is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		StoreDetailsWrapper sWrapperMock = Mock()
		sToolsObj.getStoreDetailsByLatLng("searchString", "searchString","storeType",requestMock,responseMock) >> sWrapperMock
		requestMock.getParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION) >> "true"
		requestMock.getServerName() >> "servername"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		1*callInvoker.executeQuery("searchString") >> "searchString"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresForMobile("", "", "searchString", "storeType")
		then:
		sWrapper == sWrapperMock
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sToolsObj.setLoggingDebug(true)
		sToolsObj.getStoreDetails(_,_, "storeType") >> null
		String jsonValue= "{info:{statuscode:0},results:[{},{locations:[{}]},{locations:[{latLng:{lng:3.010,lat:4.010}}]}]}}"
		requestMock.getParameter(BBBCoreConstants.SET_COOKIE) >> "true"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		requestMock.getServerName() >> "servername"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		1*httpres.addHeader("Set-Cookie", _)
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db  result is empty "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sToolsObj.setLoggingDebug(true)
		sToolsObj.getStoreDetails(_,_, "storeType") >> null
		String jsonValue= "{info:{statuscode:0}}"
		requestMock.getParameter(BBBCoreConstants.SET_COOKIE) >> "true"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		requestMock.getServerName() >> "servername"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db  throw Morph while try get lng "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sToolsObj.setLoggingDebug(true)
		sToolsObj.getStoreDetails(_,_, "storeType") >> null
		String jsonValue= "{info:{statuscode:0},results:[{},{locations:[{}]},{locations:[{latLng:{lat:4.010}}]}]}}"
		requestMock.getParameter(BBBCoreConstants.SET_COOKIE) >> "true"
		HttpServletResponse httpres = Mock()
		responseMock.getResponse()>>httpres
		requestMock.getServerName() >> "servername"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		0*httpres.addHeader("Set-Cookie", _)
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db  alt and lng is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		String jsonValue= "{info:{statuscode:0},results:[{},{locations:[{}]},{locations:[{latLng:{lng:null,lat:null}}]}]}}"
		requestMock.getParameter(BBBCoreConstants.SET_COOKIE) >> "false"
		sToolsObj.getStoreDetails(_,_, "storeType") >> null
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
			
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db latlan bean is null"(){
		given:
		String jsonValue= "{info:{statuscode:0},results:[{},{locations:[{}]},{locations:[{latLng:null}]}]}}"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", null, responseMock)
		then:
		sWrapper==null
			
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db status code is info not availble"(){
		given:
		String jsonValue= "{info:{statuscode:1,messages:'search call is falied'}}"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
		Exception ex = thrown()
		ex.getMessage().equals("1:search call is falied")
	}
	
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db throw Morph Exception"(){
		given:
		String jsonValue= "{info:{statusCode:1,messages:'search call is falied'}}"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
		Exception ex = thrown()
		ex.getMessage().equals("Unspecified property for statuscode")
	}
	def"This method parses JSon response of Map quest to get latitude and longitude and then call method to get store details from db throw Morph Exception Unspecified property for info"(){
		given:
		String jsonValue= "{}"
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetailsByLatLng(jsonValue, "pRequestString", "storeType", requestMock, responseMock)
		then:
		sWrapper==null
		Exception ex = thrown()
		ex.getMessage().equals("Unspecified property for info")
	}
	def"This method fetch store details from db"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> 100.0
		sToolsObj.getCurrentSiteId()>> "BedBathCanada"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		RepositoryItemMock itemMock =new  RepositoryItemMock()
		RepositoryItemMock itemMock1 =new  RepositoryItemMock()
		RepositoryItemMock itemMock2 =new  RepositoryItemMock()
		RepositoryItemMock itemMock3 =new  RepositoryItemMock()
		RepositoryItemMock itemMock4 =new  RepositoryItemMock()
		itemMock.setRepositoryId("asd1234")
		itemMock1.setRepositoryId("1234")
		itemMock.setProperties(["latitude":null])
		itemMock2.setRepositoryId("1234")
		itemMock2.setProperties(["latitude":10,"longitude":null])
		itemMock3.setRepositoryId("1234")
		itemMock4.setRepositoryId("1234")
		itemMock3.setProperties(["latitude":"11","longitude":"10"])
		itemMock4.setProperties(["latitude":"6","longitude":"5"])
		1*rqlSt.executeQuery(rView, _) >> [itemMock,itemMock1,itemMock2,itemMock3,itemMock4]
		sToolsObj.setRegularExpression("^[1-9]")
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetails("11", "11", "storeType")
		then:
		sWrapper != null
		sWrapper.getStoreDetails().get(0).toString().equals("1234-----")
		sWrapper.getTotalPageCount() == 1
	}
	def"This method fetch store details from db for Site BedBathUS"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >>100
		sToolsObj.getCurrentSiteId()>> "BedBathUS"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		RepositoryItemMock itemMock2 =new  RepositoryItemMock()
		RepositoryItemMock itemMock3 =new  RepositoryItemMock()
		RepositoryItemMock itemMock4 =new  RepositoryItemMock()
		itemMock2.setRepositoryId("1234")
		itemMock2.setProperties(["latitude":10,"longitude":null])
		itemMock3.setRepositoryId("1234")
		itemMock3.setProperties(["latitude":"11","longitude":"10"])
		itemMock4.setRepositoryId("1234")
		itemMock4.setProperties(["latitude":"12","longitude":"10"])
		1*rqlSt.executeQuery(rView, _) >> [itemMock2,itemMock3,itemMock4]
		sToolsObj.setRegularExpression("^[1-9]")
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetails("11", "11", "storeType")
		then:
		sWrapper != null
		sWrapper.getStoreDetails().size() == 2
		sWrapper.getTotalPageCount() == 2
		sWrapper.getStoreDetails().get(0).toString().equals("1234-----")
		sWrapper.getStoreDetails().get(1).toString().equals("1234-----")
	}
	def"This method fetch store details from db for Site BedBathUS repository items is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >>null
		sToolsObj.getCurrentSiteId()>> "BedBathUS"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> null
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetails("11", "11", "storeType")
		then:
		sWrapper != null
		sWrapper.getStoreDetails().isEmpty()
		sWrapper.getTotalPageCount() == 0
	}
	def"This method fetch store details from db for Site BedBathUS throw exception"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >>"asas"
		sToolsObj.getCurrentSiteId()>> "BedBathUS"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> {throw new RepositoryException()}
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetails("11", "11", "storeType")
		then:
		sWrapper == null
		Exception ex =thrown()
	}
	def"This method fetch store details from db for Site BedBathUS latitude is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >>""
		sToolsObj.getCurrentSiteId()>> "BedBathUS"
		cTools.getStoreRepository() >> mRepo
		0*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement() >> rqlSt
		0*rqlSt.executeQuery(rView, _) >> {throw new RepositoryException()}
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetails(null, "11", "storeType")
		then:
		sWrapper == null
	}
	def"This method fetch store details from db for Site BedBathUS longitude is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >>"asas"
		sToolsObj.getCurrentSiteId()>> "BedBathUS"
		cTools.getStoreRepository() >> mRepo
		0*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement() >> rqlSt
		0*rqlSt.executeQuery(rView, _) >> {throw new RepositoryException()}
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoreDetails("10", null, "storeType")
		then:
		sWrapper == null
	}
	def"This method adds store details to list"(){
		given:
		RepositoryItemMock itemMock = new RepositoryItemMock()
		List<StoreDetails> storeList = new ArrayList<StoreDetails>()
		itemMock.setProperties(["state":"OH","hours":"Monday-Friday: 9:00am - 9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true])
		sToolsObj.setLoggingDebug(true)
		ServletUtil.setCurrentRequest(null)
		when:
		List<StoreDetails> list =  sToolsObj.addStoreDetailsToList(itemMock, storeList, "10", "10", "1234", mRepo, "676")
		then:
		storeList.size() == 1
		storeList.get(0).getStoreName().equals("storeName")
		storeList.get(0).getStoreId().equals("1234")
		storeList.get(0).getStorePhone().equals("9700000001")
		storeList.get(0).getWeekdaysStoreTimings().equals("Mon-Fri: 9:00am - 9:00pm")
		storeList.get(0).getState().equals("OH")
		storeList.get(0).getStoreDescription().equals("specialMsg")
		storeList.get(0).getAddress().equals("1076 parsons ave")
		storeList.get(0).getCity().equals("columbus")
		storeList.get(0).getCountry().equals("US")
		storeList.get(0).getPostalCode().equals("43206")
		storeList.get(0).getContactFlag()
		storeList.get(0).getStoreType().equals("storeType")
	}
	def"This method adds store details to list store timings including weekened"(){
		given:
		RepositoryItemMock itemMock = new RepositoryItemMock()
		List<StoreDetails> storeList = new ArrayList<StoreDetails>()
		itemMock.setProperties(["state":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "null"
		when:
		List<StoreDetails> list =  sToolsObj.addStoreDetailsToList(itemMock, storeList, "10", "10", "1234", mRepo, "676")
		then:
		storeList.size() == 1
		storeList.get(0).getStoreName().equals("storeName")
		storeList.get(0).getStoreId().equals("1234")
		storeList.get(0).getStorePhone().equals("9700000001")
		storeList.get(0).getWeekdaysStoreTimings().equals("Mon-Fri: 9:00am - 9:00pm")
		storeList.get(0).getSatStoreTimings().equals("Sat: 9:00am - 9:00pm")
		storeList.get(0).getState().equals("OH")
		storeList.get(0).getStoreDescription().equals("specialMsg")
		storeList.get(0).getAddress().equals("1076 parsons ave")
		storeList.get(0).getCity().equals("columbus")
		storeList.get(0).getCountry().equals("US")
		storeList.get(0).getPostalCode().equals("43206")
		storeList.get(0).getContactFlag()
		storeList.get(0).getStoreType().equals("storeType")
	}
	def"This method adds store details to list site id BedBathCanada"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		List<StoreDetails> storeList = new ArrayList<StoreDetails>()
		itemMock.setProperties(["province":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "12345"
		sToolsObj.getCurrentSiteId() >> "BedBathCanada"
		when:
		List<StoreDetails> list =  sToolsObj.addStoreDetailsToList(itemMock, storeList, "10", "10", "1234", mRepo, "676")
		then:
		storeList.size() == 1
		storeList.get(0).getStoreName().equals("storeName")
		storeList.get(0).getStoreId().equals("1234")
		storeList.get(0).getStorePhone().equals("9700000001")
		storeList.get(0).getWeekdaysStoreTimings().equals("Mon-Fri: 9:00am - 9:00pm")
		storeList.get(0).getSatStoreTimings().equals("Sat: 9:00am - 9:00pm")
		storeList.get(0).getSunStoreTimings().equals("Sun: 10:00am - 7:00pm")
		storeList.get(0).getState().equals("OH")
		storeList.get(0).getStoreDescription().equals("specialMsg")
		storeList.get(0).getAddress().equals("1076 parsons ave")
		storeList.get(0).getCity().equals("columbus")
		storeList.get(0).getCountry().equals("US")
		storeList.get(0).getPostalCode().equals("43206")
		storeList.get(0).getContactFlag()
		storeList.get(0).getStoreType().equals("storeType")
	}
	def"This method adds store details to list site id favouriteStore matches from session"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		List<StoreDetails> storeList = new ArrayList<StoreDetails>()
		itemMock.setProperties(["hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm"])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "1234"
		sToolsObj.getCurrentSiteId() >> "BedBathCanada"
		when:
		List<StoreDetails> list =  sToolsObj.addStoreDetailsToList(itemMock, storeList, "10", "10", "1234", mRepo, "676")
		then:
		storeList.size() == 1
		storeList.get(0).getStoreName().isEmpty()
		storeList.get(0).getStoreId().equals("1234")
		storeList.get(0).getStorePhone().isEmpty()
		storeList.get(0).getWeekdaysStoreTimings().equals("Mon-Fri: 9:00am - 9:00pm")
		storeList.get(0).getSatStoreTimings().equals("Sat: 9:00am - 9:00pm")
		storeList.get(0).getSunStoreTimings().equals("Sun: 10:00am - 7:00pm")
		storeList.get(0).getOtherTimings1().equals("CyberMon:8:00am-9:00pm")
		storeList.get(0).getState().isEmpty()
		storeList.get(0).getStoreDescription().isEmpty()
		storeList.get(0).getAddress().isEmpty()
		storeList.get(0).getContactFlag() == false
		storeList.get(0).getStoreType().isEmpty()
	}
	def"This method gets stores by store id of user's favorite store"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["province":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm,CyberMon:8:00am-9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true,"longitude":"longitude","latitude":"latitude"])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "1234"
		sToolsObj.getCurrentSiteId() >> "BedBathCanada"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		rqlSt.executeQuery(rView, _) >> [itemMock]
		when:
		StoreDetailsWrapper warp = sToolsObj.getStoresByStoreId("1234","storeType" ,false)
		then:
		List<StoreDetails> storeList = warp.getStoreDetails()
		warp.getCurrentPage() == 1
		warp.getTotalPageCount() == 0
		storeList.get(0).getOtherTimings1().equals("CyberMon:8:00am-9:00pm")
		storeList.get(0).getStoreName().equals("storeName")
		storeList.get(0).getStoreId().equals("1234")
		storeList.get(0).getStorePhone().equals("9700000001")
		storeList.get(0).getWeekdaysStoreTimings().equals("Mon-Fri: 9:00am - 9:00pm")
		storeList.get(0).getOtherTimings2().equals("CyberMon:8:00am-9:00pm")
		storeList.get(0).getSatStoreTimings().equals("Sat: 9:00am - 9:00pm")
		storeList.get(0).getSunStoreTimings().equals("Sun: 10:00am - 7:00pm")
		storeList.get(0).getState().equals("OH")
		storeList.get(0).getStoreDescription().equals("specialMsg")
		storeList.get(0).getAddress().equals("1076 parsons ave")
		storeList.get(0).getCity().equals("columbus")
		storeList.get(0).getCountry().equals("US")
		storeList.get(0).getPostalCode().equals("43206")
		storeList.get(0).getContactFlag()
		storeList.get(0).getStoreType().equals("storeType")
		storeList.get(0).getLat().equals("latitude")
		storeList.get(0).getLng().equals("longitude")
	}
	def"This method gets stores by store id of user's favorite store for site Bebbathus"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["state":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm,CyberMon:8:00am-9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true,"longitude":"longitude","latitude":"latitude"])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "1234"
		sToolsObj.getCurrentSiteId() >> "Bebbathus"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		rqlSt.executeQuery(rView, _) >> [itemMock]
		when:
		StoreDetailsWrapper warp = sToolsObj.getStoresByStoreId("1234","storeType" ,false)
		then:
		List<StoreDetails> storeList = warp.getStoreDetails()
		warp.getCurrentPage() == 1
		warp.getTotalPageCount() == 0
		storeList.get(0).getOtherTimings1().equals("CyberMon:8:00am-9:00pm")
		storeList.get(0).getStoreName().equals("storeName")
		storeList.get(0).getStoreId().equals("1234")
		storeList.get(0).getStorePhone().equals("9700000001")
		storeList.get(0).getWeekdaysStoreTimings().equals("Mon-Fri: 9:00am - 9:00pm")
		storeList.get(0).getOtherTimings2().equals("CyberMon:8:00am-9:00pm")
		storeList.get(0).getSatStoreTimings().equals("Sat: 9:00am - 9:00pm")
		storeList.get(0).getSunStoreTimings().equals("Sun: 10:00am - 7:00pm")
		storeList.get(0).getState().equals("OH")
		storeList.get(0).getStoreDescription().equals("specialMsg")
		storeList.get(0).getAddress().equals("1076 parsons ave")
		storeList.get(0).getCity().equals("columbus")
		storeList.get(0).getCountry().equals("US")
		storeList.get(0).getPostalCode().equals("43206")
		storeList.get(0).getContactFlag()
		storeList.get(0).getStoreType().equals("storeType")
		storeList.get(0).getLat().equals("latitude")
		storeList.get(0).getLng().equals("longitude")
	}
	def"This method gets stores by store id of user's favorite store throws exception"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["province":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm,CyberMon:8:00am-9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true,"longitude":"longitude","latitude":"latitude"])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "1234"
		sToolsObj.getCurrentSiteId() >> "BedBathCanada"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> {throw new BBBBusinessException("Mock of Business exception")}
		when:
		StoreDetailsWrapper warp = sToolsObj.getStoresByStoreId("1234","storeType" ,false)
		then:
		warp == null
		Exception ex = thrown()
	}
	def"This method gets stores by store id of user's favorite store throws Repository exception"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["province":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm,CyberMon:8:00am-9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true,"longitude":"longitude","latitude":"latitude"])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "1234"
		sToolsObj.getCurrentSiteId() >> "BedBathCanada"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> {throw new RepositoryException("Mock of Repository exception")}
		when:
		StoreDetailsWrapper warp = sToolsObj.getStoresByStoreId("1234","storeType" ,false)
		then:
		warp == null
		Exception ex = thrown()
	}
	def"This method gets stores by store id of user's favorite not item found with give store id "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["province":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm,CyberMon:8:00am-9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true,"longitude":"longitude","latitude":"latitude"])
		sToolsObj.setLoggingDebug(true)
		requestMock.getAttribute(BBBCoreConstants.FAVOURITE_STORE_ID) >> "1234"
		sToolsObj.getCurrentSiteId() >> "BedBathCanada"
		cTools.getStoreRepository() >> mRepo
		1*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> null
		when:
		StoreDetailsWrapper warp = sToolsObj.getStoresByStoreId("1234","storeType" ,false)
		then:
		warp == null
	}
	def"This method gets stores by store id of user's favorite from PDP"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["province":"OH","hours":"Monday-Friday: 9:00am - 9:00pm,Saturday: 9:00am - 9:00pm,Sunday: 10:00am - 7:00pm,CyberMonday:8:00am-9:00pm,CyberMon:8:00am-9:00pm","storeName":"storeName","specialMsg":"specialMsg",
			"address":"1076 parsons ave","city":"columbus","countryCode":"US","postalCode":"43206","phone":"9700000001","storeType":"storeType","contactFlag":true,"longitude":"longitude","latitude":"11"])
		sToolsObj.getCurrentSiteId()>> "BedBathUS"
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >>null
		cTools.getStoreRepository() >> mRepo
		2*mRepo.getView(SelfServiceConstants.STORE) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		2*rqlSt.executeQuery(rView, _) >> [itemMock] >> []
		when:
		StoreDetailsWrapper sWrapper= sToolsObj.getStoresByStoreId("1234","storeType" ,true)
		then:
		sWrapper != null
		sWrapper.getStoreDetails().isEmpty()
		sWrapper.getTotalPageCount() == 0
	}
	def"This method gets inventory for one sku/store"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["stockLevel":1])
		1*mRepository.getView(BBBCoreConstants.STORE_LOCAL_INVENTORY) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> [itemMock] 
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryForStoreFromDb("sku1234", "storeId")
		then:
		storeVo != null
		storeVo.getStockLevel() ==  1
	}
	def"This method gets inventory for one sku/store repsitory items null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["stockLevel":1])
		1*mRepository.getView(BBBCoreConstants.STORE_LOCAL_INVENTORY) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> null
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryForStoreFromDb("sku1234", "storeId")
		then:
		storeVo == null
	}
	def"This method gets inventory for one sku/store not items found"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["stockLevel":1])
		1*mRepository.getView(BBBCoreConstants.STORE_LOCAL_INVENTORY) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> []
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryForStoreFromDb("sku1234", "storeId")
		then:
		storeVo == null
	}
	def"This method gets inventory for one sku/store  stock value null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["stockLevel":null])
		1*mRepository.getView(BBBCoreConstants.STORE_LOCAL_INVENTORY) >> rView
		sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> [itemMock]
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryForStoreFromDb("sku1234", "storeId")
		then:
		storeVo == null
	}
	def"This method gets the inventory for local store from coherence "(){
		given:
		2*cContainer.get("storeId"+BBBCoreConstants.HYPHEN+"sku1234", BBBCoreConstants.CACHE_STORE_INV) >> "0"
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryFromLocalStore("storeId", "sku1234")
		then:
		storeVo != null
		storeVo.getSkuId().equals("sku1234")
		storeVo.getStoreId().equals("storeId")
		storeVo.getStockLevel() == 0
		
	}
	def"This method gets the inventory for local store from coherence sku is not available"(){
		given:
		1*cContainer.get("storeId"+BBBCoreConstants.HYPHEN+"sku1234", BBBCoreConstants.CACHE_STORE_INV) >> null
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryFromLocalStore("storeId", "sku1234")
		then:
		storeVo == null
	}
	def"This method gets the inventory for local store from coherence throw exception"(){
		given:
		1*cContainer.get("storeId"+BBBCoreConstants.HYPHEN+"sku1234", BBBCoreConstants.CACHE_STORE_INV) >> {throw new Exception("Mock of Exception")}
		when:
		LocalStoreVO storeVo = sToolsObj.getInventoryFromLocalStore("storeId", "sku1234")
		then:
		storeVo == null
		Exception ex =thrown()
	}
	def"This method queries db to get stock level of store/sku store details "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		rs.next() >> true
		2*rs.getString(BBBCoreConstants.STORE_ID_COLUMN) >>> ["12345","123457"]
		2*rs.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN) >>> [1,0]
		StoreDetails sDetails1  =new StoreDetails("pStoreId", " pStoreName", " pStoreDescription", " pAddress", 
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		StoreDetails sDetails3  =new StoreDetails("123457", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		creatingConnection(sToolsObj)
		sToolsObj.executeQuery(pStata) >> rs
		1*iManager.getInventoryStatus(1, 1, thresHoldVO , "12345") >> 1
		//1*iManager.getInventoryStatus(2, 1, thresHoldVO , "123456") >> 1
		1*iManager.getInventoryStatus(0, 1, thresHoldVO , "123457") >> 0
		when:
		StoreDetails sDetails = sToolsObj.getInventoryFromDb([sDetails1,sDetails3],"1234",1,thresHoldVO,null)
		then:
		sDetails != null
		sDetails.toString().equals("123457- pStoreName- pAddress- pCity- pState- pPostalCode")
		1*cContainer.put(_,_,_)
	}
	def"This method queries db to get stock level of store/sku store details limted stock "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		rs.next() >> true
		2*rs.getString(BBBCoreConstants.STORE_ID_COLUMN) >>> ["12345","123457"]
		1*rs.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN) >>> [2]
		StoreDetails sDetails1  =new StoreDetails("pStoreId", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		StoreDetails sDetails3  =new StoreDetails("123457", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		creatingConnection(sToolsObj)
		1*sToolsObj.executeQuery(pStata) >> rs
		0*iManager.getInventoryStatus(1, 1, thresHoldVO , "12345") >> 1
		//1*iManager.getInventoryStatus(2, 1, thresHoldVO , "123456") >> 1
		1*iManager.getInventoryStatus(2, 1, thresHoldVO , "123457") >> 2
		when:
		StoreDetails sDetails = sToolsObj.getInventoryFromDb([sDetails1,sDetails3],"1234",1,thresHoldVO,["12345"])
		then:
		sDetails != null
		sDetails.toString().equals("123457- pStoreName- pAddress- pCity- pState- pPostalCode")
		1*cContainer.put(_,_,_)
	}
	def"This method queries db to get stock level of store/sku store details not results found "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		rs.next() >> false
		0*rs.getString(BBBCoreConstants.STORE_ID_COLUMN) >>> ["12345","123457"]
		0*rs.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN) >>> [1,2]
		StoreDetails sDetails1  =new StoreDetails("pStoreId", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		StoreDetails sDetails3  =new StoreDetails("123457", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		creatingConnection(sToolsObj)
		1*sToolsObj.executeQuery(pStata) >> rs
		0*iManager.getInventoryStatus(1, 1, thresHoldVO , "12345") >> 1
		//1*iManager.getInventoryStatus(2, 1, thresHoldVO , "123456") >> 1
		0*iManager.getInventoryStatus(2, 1, thresHoldVO , "123457") >> 2
		when:
		StoreDetails sDetails = sToolsObj.getInventoryFromDb([sDetails1,sDetails3],"1234",1,thresHoldVO,null)
		then:
		sDetails == null
	}
	def"This method queries db to get stock level of store/sku store details no store id macth "(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		rs.next() >> true
		1*rs.getString(BBBCoreConstants.STORE_ID_COLUMN) >>> ["123457"]
		1*rs.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN) >>> [2]
		StoreDetails sDetails3  =new StoreDetails("123458", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		creatingConnection(sToolsObj)
		sToolsObj.executeQuery(pStata) >> rs
		1*iManager.getInventoryStatus(2, 1, thresHoldVO , "123457") >> 2
		when:
		StoreDetails sDetails = sToolsObj.getInventoryFromDb([sDetails3],"1234",1,thresHoldVO,null)
		then:
		sDetails == null
	}
	def"This method queries db to get stock level of store/sku store details throw exception"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		rs.next() >> true
		1*rs.getString(BBBCoreConstants.STORE_ID_COLUMN) >>> ["123457"]
		1*rs.getInt(BBBCoreConstants.STOCK_LEVEL_COLUMN) >>> [2]
		StoreDetails sDetails3  =new StoreDetails("123458", " pStoreName", " pStoreDescription", " pAddress",
			" pCity", " pState", " pCountry", " pPostalCode",
			" pSatStoreTimings", " pSunStoreTimings",
			" pWeekdaysStoreTimings", " pOtherTimings1",
			" pOtherTimings2", " pStorePhone",
			" pImageCode", " pDistance", " pDistanceUnit",
			" pRecordNumber", " pLng", " pLat",
			" pSpecialtyShopsCd", " pStoreType")
		creatingConnection(sToolsObj)
		1*sToolsObj.executeQuery(pStata) >> rs
		1*iManager.getInventoryStatus(2, 1, thresHoldVO , "123457") >> {throw new Exception("Mock of Exception")}
		when:
		StoreDetails sDetails = sToolsObj.getInventoryFromDb([sDetails3],"1234",1,thresHoldVO,null)
		then:
		sDetails == null
		Exception ex = thrown()
	}

	def"This method queries db to get stock level of store/sku store details is empty"(){
		when:
		StoreDetails sDetails = sToolsObj.getInventoryFromDb(null,null,0,null,null)
		then:
		sDetails == null
	}
	def"This method fetches specialityShopCd from db"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		set.add(itemMock1)
		itemMock1.setRepositoryId("rep1234")
		itemMock1.setProperties(["specialityCodeName":"specialityCodeName","specialityShopCd":"specialityShopCd"])
		itemMock.setProperties(["specialityCodeId":set]) 
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		2*sToolsObj.parseRqlStatement(_) >> rqlSt
		2*rqlSt.executeQuery(rView, _) >> [itemMock1]
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value.equals("specialityShopCd")
	}
	def"This method fetches specialityShopCd from db items from specialityCodeMap view is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		set.add(itemMock1)
		itemMock1.setRepositoryId("rep1234")
		itemMock1.setProperties(["specialityCodeName":"specialityCodeName","specialityShopCd":"specialityShopCd"])
		itemMock.setProperties(["specialityCodeId":set])
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		2*sToolsObj.parseRqlStatement(_) >> rqlSt
		2*rqlSt.executeQuery(rView, _) >> [itemMock1] >> null
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value.isEmpty()
	}
	def"This method fetches specialityShopCd from db items from specialityCodeMap view is empty"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		set.add(itemMock1)
		itemMock1.setRepositoryId("rep1234")
		itemMock1.setProperties(["specialityCodeName":"specialityCodeName","specialityShopCd":"specialityShopCd"])
		itemMock.setProperties(["specialityCodeId":set])
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		2*sToolsObj.parseRqlStatement(_) >> rqlSt
		2*rqlSt.executeQuery(rView, _) >> [itemMock1] >> []
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value.isEmpty()
	}
	def"This method fetches specialityShopCd from db items from specialityCode view is null"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		set.add(itemMock1)
		itemMock1.setRepositoryId("rep1234")
		itemMock1.setProperties(["specialityCodeName":"specialityCodeName","specialityShopCd":"specialityShopCd"])
		itemMock.setProperties(["specialityCodeId":set])
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		1*sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> null
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value.isEmpty()
	}
	def"This method fetches specialityShopCd from db items from specialityCode view is empty"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		set.add(itemMock1)
		itemMock1.setRepositoryId("rep1234")
		itemMock1.setProperties(["specialityCodeName":"specialityCodeName","specialityShopCd":"specialityShopCd"])
		itemMock.setProperties(["specialityCodeId":set])
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		1*sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> [] 
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value.isEmpty()
	}
	def"This method fetches specialityShopCd from db items from specialityCodeId size is 0"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		itemMock.setProperties(["specialityCodeId":set])
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		0*sToolsObj.parseRqlStatement(_) >> rqlSt
		0*rqlSt.executeQuery(rView, _) >> []
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value.isEmpty()
	}
	def"This method fetches specialityShopCd from db throw Repository exception"(){
		given:
		sToolsObj = Spy()
		spyObjIntilization(sToolsObj)
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		set.add(itemMock1)
		itemMock1.setRepositoryId("rep1234")
		itemMock1.setProperties(["specialityCodeName":"specialityCodeName","specialityShopCd":"specialityShopCd"])
		itemMock.setProperties(["specialityCodeId":set])
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE)>>rView
		mRepository.getView(SelfServiceConstants.SPECIALITY_CODE_MAP) >> rView
		1*sToolsObj.parseRqlStatement(_) >> rqlSt
		1*rqlSt.executeQuery(rView, _) >> {throw new RepositoryException("Mokc of Repository exception")}
		when:
		String value  = sToolsObj.getSpecialityShopCd(mRepository, itemMock)
		then:
		value == null
		Exception ex = thrown()
	}
	def"Get direction for store on making http call  "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{info:{statusCode:0},route:{distance:9.6664}}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails != null
		rDetails.getTotalDistance().equals("9.66")
	}
	def"Get direction for store on making http call in route properties not available "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{info:{statusCode:0},route:{}}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails != null
	}
	def"Get direction for store on making http call dose not contain route property  "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{info:{statusCode:0}}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails == null
	}
	def"Get direction for store on making http call dose route property is null "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{info:{statusCode:0},route:null}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails == null
	}
	def"Get direction for store on making http call throw MorphException "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{info:{}}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails == null
		Exception ex = thrown()
		ex.getMessage().equals("Unspecified property for statuscode")
	}
	def"Get direction for store on making http call result status code is 1 "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{info:{statusCode:1,messages:'messages'}}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails == null
		Exception ex = thrown()
		ex.getMessage().equals("1:messages")
	}
	def"Get direction for store on making http call info property is not available "(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+")>> "{}"
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString ")
		then:
		rDetails == null
		Exception ex = thrown()
		ex.getMessage().equals("Unspecified property for info")
	}
	def"Get direction for store on making http call result is null"(){
		given:
		1*callInvoker.executeQuery("pDirectionsString+|+")>> null
		when:
		RouteDetails rDetails = sToolsObj.storeDirections("pDirectionsString | ") 
		then:
		rDetails == null
	}
	def"Get direction for store on making http call throw system exception"(){
		given:
		1*callInvoker.executeQuery(null) >> {throw new BBBSystemException("Mock of system exception")}
		when:
		RouteDetails rDetails = sToolsObj.storeDirections(null)
		then:
		rDetails == null
	}
	def"Populating JSON response to RouteDetails Obj"(){
		given:
		RouteDetails rDetails1 = new RouteDetails()
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava((JSONObject) JSONSerializer.toJSON("{info:{statusCode:0},route:{distance:9,sessionId:'sessionId',formattedTime:'00:00',hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,locations:[{latLng:{lat:'lat',lng:'lng'},adminArea3:'adminArea3',adminArea5:'adminArea5',postalCode:'43206'},{latLng:{lat:'lat',lng:'lng'}}],legs:[{},{hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,index:'1',distance:'8',formattedTime:'12:15',maneuvers:null}]}}"))
		when:
		RouteDetails rDetails = sToolsObj.setRouteDetails((MorphDynaBean) JSONResultbean.get(SelfServiceConstants.ROUTE), rDetails1)
		then:
		rDetails != null
		rDetails.getTotalDistance().equals("9")
		rDetails.getSessionId().equals("sessionId")
		rDetails.getFormattedTime().equals('')
		rDetails.isHasUnpaved()
		rDetails.isHasCountryCross()
		rDetails.isHasFerry()
		rDetails.isHasHighway()
		rDetails.isHasTollRoad()
		rDetails.isHasSeasonalClosure()
		rDetails.getStartPointLat().equals('lat')
		rDetails.getStartPointLng().equals('lng')
		rDetails.getEndPointLat().equals('lat')
		rDetails.getEndPointLng().equals('lng')
		rDetails.getRouteStartPoint().equals("adminArea5, adminArea3 43206")
		rDetails.getRouteLegs().size() == 2
		rDetails.getRouteLegs().get(0).isHasUnpaved()  == false
		rDetails.getRouteLegs().get(0).isHasCountryCross() == false
		rDetails.getRouteLegs().get(0).isHasFerry() == false
		rDetails.getRouteLegs().get(0).isHasHighway()== false
		rDetails.getRouteLegs().get(0).isHasTollRoad()== false
		rDetails.getRouteLegs().get(0).isHasSeasonalClosure()== false
		rDetails.getRouteLegs().get(1).isHasUnpaved()
		rDetails.getRouteLegs().get(1).isHasCountryCross()
		rDetails.getRouteLegs().get(1).isHasFerry()
		rDetails.getRouteLegs().get(1).isHasHighway()
		rDetails.getRouteLegs().get(1).isHasTollRoad()
		rDetails.getRouteLegs().get(1).isHasSeasonalClosure()
		rDetails.getRouteLegs().get(1).getLegDistance().equals("8")
		rDetails.getRouteLegs().get(1).getIndex().equals("1")
		
	}
	def"Populating JSON response to RouteDetails Obj locations is empty"(){
		given:
		RouteDetails rDetails1 = new RouteDetails()
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava((JSONObject) JSONSerializer.toJSON("{info:{statusCode:0},route:{sessionId:'sessionId',formattedTime:'00:00',hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,locations:[]}}"))
		when:
		RouteDetails rDetails = sToolsObj.setRouteDetails((MorphDynaBean) JSONResultbean.get(SelfServiceConstants.ROUTE), rDetails1)
		then:
		rDetails != null
		rDetails.getSessionId().equals("sessionId")
		rDetails.getFormattedTime().equals('')
		rDetails.isHasUnpaved()
		rDetails.isHasCountryCross()
		rDetails.isHasFerry()
		rDetails.isHasHighway()
		rDetails.isHasTollRoad()
		rDetails.isHasSeasonalClosure()
		rDetails.getRouteLegs() == null
		
	}
	def"Populating JSON response to RouteDetails Obj in location property latlang is null"(){
		given:
		RouteDetails rDetails1 = new RouteDetails()
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava((JSONObject) JSONSerializer.toJSON("{info:{statusCode:0},route:{distance:9.6,sessionId:'sessionId',formattedTime:'0:12',hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,locations:[{}],legs:[]}}"))
		when:
		RouteDetails rDetails = sToolsObj.setRouteDetails((MorphDynaBean) JSONResultbean.get(SelfServiceConstants.ROUTE), rDetails1)
		then:
		rDetails != null
		rDetails.getTotalDistance().equals("9")
		rDetails.getSessionId().equals("sessionId")
		rDetails.getFormattedTime().equals(' 12minute')
		rDetails.isHasUnpaved()
		rDetails.isHasCountryCross()
		rDetails.isHasFerry()
		rDetails.isHasHighway()
		rDetails.isHasTollRoad()
		rDetails.isHasSeasonalClosure()
		rDetails.getRouteLegs() == null
		
		
	}
	def"Populating JSON response to RouteDetails Obj in location property latlang property latitude is null"(){
		given:
		RouteDetails rDetails1 = new RouteDetails()
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava((JSONObject) JSONSerializer.toJSON("{info:{statusCode:0},route:{distance:9.6,sessionId:'sessionId',formattedTime:'12:1',hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,locations:[{latLng:{lng:'lng'},adminArea3:'adminArea3',postalCode:'43206'},{latLng:{lng:'lng'}}],legs:[{hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,index:'1',distance:'8',formattedTime:'12:15',maneuvers:[{transportMode:'transportMode',index:2,direction:10,narrative:'narrative',distance:9.664},{distance:9.66,mapUrl:'//mapUrl'},{mapUrl:''},{},{distance:9,iconUrl:'iconUrl',attributes:1,mapUrl:'http://mapUrl',startPoint:{},formattedTime:'12.00',time:14,directionName:'directionName'}]}]}}"))
		when:
		RouteDetails rDetails = sToolsObj.setRouteDetails((MorphDynaBean) JSONResultbean.get(SelfServiceConstants.ROUTE), rDetails1)
		then:
		rDetails != null
		rDetails.getTotalDistance().equals("9")
		rDetails.getSessionId().equals("sessionId")
		rDetails.getFormattedTime().equals('12hours, 1minute')
		rDetails.isHasUnpaved()
		rDetails.isHasCountryCross()
		rDetails.isHasFerry()
		rDetails.isHasHighway()
		rDetails.isHasTollRoad()
		rDetails.isHasSeasonalClosure()
		rDetails.getRouteLegs().size() == 1
		rDetails.getRouteLegs().get(0).isHasUnpaved()  
		rDetails.getRouteLegs().get(0).isHasCountryCross()
		rDetails.getRouteLegs().get(0).isHasFerry() 
		rDetails.getRouteLegs().get(0).isHasHighway()
		rDetails.getRouteLegs().get(0).isHasTollRoad()
		rDetails.getRouteLegs().get(0).isHasSeasonalClosure()
		rDetails.getRouteLegs().get(0).getManeuvers().size() == 4
		rDetails.getRouteLegs().get(0).getManeuvers().get(0).getTransportMode().equals("transportMode")
		rDetails.getRouteLegs().get(0).getManeuvers().get(0).getIndex() == 2
		rDetails.getRouteLegs().get(0).getManeuvers().get(0).getNarrative().equals("narrative")
		rDetails.getRouteLegs().get(0).getManeuvers().get(0).getDistance().equals("9.66")
		rDetails.getRouteLegs().get(0).getManeuvers().get(0).getDirection()==10
		rDetails.getRouteLegs().get(0).getManeuvers().get(1).getDistance().equals("9")
		rDetails.getRouteLegs().get(0).getManeuvers().get(1).getMapUrl().equals('//mapUrl')
		rDetails.getRouteLegs().get(0).getManeuvers().get(2).getMapUrl().equals('')
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getDistance().equals('9')
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getMapUrl().equals('//mapUrl')
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getIconUrl().equals("iconUrl")
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getAttributes() == 1
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getFormattedTime().equals("12.00")
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getTime() == 14
		rDetails.getRouteLegs().get(0).getManeuvers().get(3).getDirectionName().equals("directionName")
	}
	def"Populating JSON response to RouteDetails Obj in location property latlang property lng is null"(){
		given:
		RouteDetails rDetails1 = new RouteDetails()
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava((JSONObject) JSONSerializer.toJSON("{info:{statusCode:0},route:{distance:9.6,sessionId:'sessionId',formattedTime:'12:0',hasUnpaved:true,hasHighway:true,hasFerry:true,hasSeasonalClosure:true,hasCountryCross:true,hasTollRoad:true,locations:[{latLng:{lat:'lat'},postalCode:'43206'},{latLng:{lat:'lat'}}],legs:[]}}"))
		when:
		RouteDetails rDetails = sToolsObj.setRouteDetails((MorphDynaBean) JSONResultbean.get(SelfServiceConstants.ROUTE), rDetails1)
		then:
		rDetails != null
		rDetails.getTotalDistance().equals("9")
		rDetails.getSessionId().equals("sessionId")
		rDetails.getFormattedTime().equals('12hours')
		rDetails.isHasUnpaved()
		rDetails.isHasCountryCross()
		rDetails.isHasFerry()
		rDetails.isHasHighway()
		rDetails.isHasTollRoad()
		rDetails.isHasSeasonalClosure()
		rDetails.getRouteLegs() == null
		
	}
	def"This method takes max age of cookie from config keys"(){
		given:
		cTools.getAllValuesForKey("ThirdPartyURLs", "CookieLatLng") >> ["10"]
		when:
		int value = sToolsObj.getCookieTimeOut()
		then:
		value == 10
	}
	void spyObjIntilization(StoreTools sToolsObj){
		sToolsObj.setCatalogTools(cTools)
		sToolsObj.setCacheContainer(cContainer)
		sToolsObj.setLocalStoreRepository(mRepository)
		sToolsObj.setRegularExpression("^[1-9]")
		sToolsObj.setInventoryManager(iManager)
		sToolsObj.setHttpCallInvoker(callInvoker)
	}
	private creatingConnection(StoreTools sToolsObj) {
		GSARepository gsaRepository = Mock()
		sToolsObj.setLocalStoreRepository(gsaRepository)
		1*gsaRepository.getDataSource() >>  dataSrc
		1*dataSrc.getConnection() >> conn
		1*conn.prepareStatement(_) >> pStata
	}
}