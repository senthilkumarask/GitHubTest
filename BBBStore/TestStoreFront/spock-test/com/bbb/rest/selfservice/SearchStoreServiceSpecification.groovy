package com.bbb.rest.selfservice

import java.util.Map

import atg.commerce.inventory.InventoryException
import atg.commerce.order.CommerceItem
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import javax.servlet.http.Cookie;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO
import com.bbb.commerce.common.BBBStoreInventoryContainer
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.integration.exception.ServiceError;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreAddressSuggestion
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper
import com.bbb.selfservice.manager.ScheduleAppointmentManager
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.selfservice.tools.StoreTools
import com.bbb.utils.BBBUtility;

import spock.lang.specification.BBBExtendedSpec;

class SearchStoreServiceSpecification extends BBBExtendedSpec{

	SearchStoreService service
	BBBStoreInventoryContainer container =Mock()

	def setup(){
		service = new SearchStoreService()
		service.setRadius("radius")
		service.setStoreInventoryContainer(container)
	}

	/**
	 * created  private common methods to set map values
	 * @param inputMap
	 * @return
	 */
	private setMapparameters(Map<String,String> inputMap){
		inputMap.put("skuId","1")
		inputMap.put("searchString", "")
		inputMap.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputMap.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputMap.put(BBBCoreConstants.SKUID,"2")
		inputMap.put(BBBCoreConstants.RADIUS, "radius")
		inputMap.put(SelfServiceConstants.LATITUDE, "latitude")
		inputMap.put(SelfServiceConstants.LONGITUDE,"longitude")
	}
	private setMapParametersForsearchSKUInStore(Map<String,String> inputMap){
		inputMap.put("orderedQty","10")
		inputMap.put("skuID","1")
		inputMap.put("searchString", "")
		inputMap.put(BBBCoreConstants.IS_FROM_PDP ,"false")
		inputMap.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputMap.put(BBBCoreConstants.SKUID,"2")
		inputMap.put(BBBCoreConstants.RADIUS, "radius")
	}
	private setMapForsearchStoreForPLP(Map<String, String> inputMap){
		inputMap.put("searchString","")
		inputMap.put("callFromPLP", "true")
		inputMap.put("storeIdFromURL","idFromUrl")
		inputMap.put("siteId","tbs")
		inputMap.put(BBBCoreConstants.RADIUS, "1")
		inputMap.put(SelfServiceConstants.LATITUDE,"latitude")
		inputMap.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputMap.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputMap.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")
	}


	def"searchSKUInStore, when input map is null"(){

		given:
		service = Spy()
		Map<String, String> inputParam = new HashMap()

		when:
		SearchInStoreVO vo =service.searchSKUInStore(null)

		then:
		vo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT
		vo.getErrorCode() == "70701"
		vo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ "err_search_store_invalid_input")
	}

	def"searchSKUInStore, when skuId is empty"(){

		given:
		service = Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*service.logError("Received empty productId = in request")
		vo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT
		vo.getErrorCode() == "70701"
		vo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT)
	}

	def"searchSKUInStore, when quantity is empty"(){

		given:
		service = Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*service.logError("Received empty productId = 1in request")
		vo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT
		vo.getErrorCode() == "70701"
		vo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ SelfServiceConstants.ERROR_SEARCH_STORE_MANDATORY_INPUT)
	}

	def"searchSKUInStore, when NumberFormatException occurs while converting quantity"(){

		given:
		service = Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","abc")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*service.logError("Received invalid quanity = " + "abc" + "in request.")
		vo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_QUANTITY
		vo.getErrorCode() == "70701"
		vo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_QUANTITY)
	}

	def"searchSKUInStore, when quantity is less than zero"(){

		given:
		service = Spy()
		SearchInStoreVO storeVo = new SearchInStoreVO()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","-1")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"abc")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*service.getLocalStoreDetailsForPDP(inputParam,"latitude","longitude", false) >> storeVo
		0*service.getNearestStoreDetailsForPDP(inputParam,"latitude","longitude", false)
		0*service.getLocalStoreDetailsForPDP(inputParam, "latitude","longitude", true)

	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called and  search is based on cookie "(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		setMapparameters(inputParam)
		inputParam.put("orderedQty","10")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "latLngCookie,latLngCookie1"

		//for getLocalStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		service.setSearchStoreManager(manager)

		service.extractSiteId() >>"tbs"
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> "favStore"
		1*manager.getFavStoreByCoordinates("tbs", requestMock,responseMock, "latLngCookie1", "latLngCookie", false) >> {}
		1*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> [store]
		2*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> "radius"
		1*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		RepositoryItem r1 = Mock()
		StoreSpecialityVO sVo= new StoreSpecialityVO()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> [rItem]
		rItem.getPropertyValue("specialityCd") >> [r1]
		cTools.getStoreSpecialityList(_) >> [sVo]

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == "radius"
		wrapper.getInventoryNotAvailable() == "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == [store]
	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called and  search is based on Fav Store "(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		setMapparameters(inputParam)
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getLocalStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		service.setSearchStoreManager(manager)

		service.extractSiteId() >>"tbs"
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> "favStore"
		1*manager.getFavStoreByStoreId("tbs", requestMock, responseMock,"favStore") >> {}

		1*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		1*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		RepositoryItem r1 = Mock()
		StoreSpecialityVO sVo= new StoreSpecialityVO()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> [rItem]
		rItem.getPropertyValue("specialityCd") >> [r1]
		cTools.getStoreSpecialityList(_) >> [sVo]

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getInventoryNotAvailable() == "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == null
	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called and  search is based on Lat/Lng"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		setMapparameters(inputParam)
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getLocalStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		service.setSearchStoreManager(manager)

		service.extractSiteId() >>"tbs"
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> ""
		1*manager.getFavStoreByCoordinates("tbs", requestMock,responseMock, "latitude", "longitude", false) >> {}

		1*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		1*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null
		0*rItem.getPropertyValue("specialityCd")
		0*cTools.getStoreSpecialityList(_)

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getInventoryNotAvailable() == "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == null
		/*store.getStoreSpecialityVO()!= null*/
	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called, latitude is empty,  search is not based on Lat/Lng, FavStore,cookie"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuId","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getLocalStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		service.setSearchStoreManager(manager)
		service.extractSiteId() >>"tbs"
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> ""
		0*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*store.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null
		0*rItem.getPropertyValue("specialityCd")
		0*cTools.getStoreSpecialityList(_)

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == "true"
		wrapper.getInventoryNotAvailable() == null
		wrapper.getErrorIneligibleStates() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == null
		/*store.getStoreSpecialityVO()!= null*/
	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called ,longitude is empty and  search is not based on Lat/Lng, FavStore,cookie"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuId","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getLocalStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		service.setSearchStoreManager(manager)
		service.extractSiteId() >>"tbs"
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> ""
		0*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*store.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null
		0*rItem.getPropertyValue("specialityCd")
		0*cTools.getStoreSpecialityList(_)

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == "true"
		wrapper.getInventoryNotAvailable() == null
		wrapper.getErrorIneligibleStates() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == null
		/*store.getStoreSpecialityVO()!= null*/
	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called and exception is thrown with error message not empty"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuId","1")
		inputParam.put("searchString", "abc")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//for getLocalStoreDetailsForPDP
		LblTxtTemplateManager lblManager =Mock()
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		CommerceItem c = Mock()
		service.setSearchStoreManager(manager)
		service.extractSiteId() >>"tbs"
		service.setLblTxtTemplateManager(lblManager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> ""
		1*manager.getFavStoreByCoordinates("tbs", requestMock,responseMock, "latitude", "longitude", false) >> {throw new Exception("")}
		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> "exception occured"
		0*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> true
		0*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*store.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null
		0*rItem.getPropertyValue("specialityCd")
		0*cTools.getStoreSpecialityList(_)

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getInventoryNotAvailable() == null
		wrapper.getErrorIneligibleStates() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == null
		wrapper.getErrorInViewFavStores() == "exception occured"
		/*store.getStoreSpecialityVO()!= null*/
	}

	def"searchSKUInStore, when is form PDP is true ,getLocalStoreDetailsForPDP is called and exception is thrown with error message empty"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuId","1")
		inputParam.put("searchString", "abc")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//for getLocalStoreDetailsForPDP
		LblTxtTemplateManager lblManager =Mock()
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		CommerceItem c = Mock()
		service.setSearchStoreManager(manager)
		service.extractSiteId() >>"tbs"
		service.setLblTxtTemplateManager(lblManager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("tbs", profile) >> ""
		1*manager.getFavStoreByCoordinates("tbs", requestMock,responseMock, "latitude", "longitude", false) >> {throw new Exception("")}
		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		0*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> true
		0*requestMock.getParameter(BBBCoreConstants.ERROR_MSG_INVENTORY_NOT_AVAILABLE) >> "ERROR_MSG_INVENTORY_NOT_AVAILABLE"
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >> "RESERVE_ONLINE_NOT_AVAILABLE"
		//ends getLocalStoreDetailsForPDP

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*store.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null
		0*rItem.getPropertyValue("specialityCd")
		0*cTools.getStoreSpecialityList(_)

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getLocalStoreDetailsForPDP
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getInventoryNotAvailable() == null
		wrapper.getErrorIneligibleStates() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		wrapper.getStoreDetails() == null
		wrapper.getErrorInViewFavStores() == BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE
		/*store.getStoreSpecialityVO()!= null*/
	}

	def"searchSKUInStore, when favourite store flag is false and getNearestStoreDetailsForPDP is called to get store details and search is perfomed based on coordinates"(){

		given:
		service =Spy()
		service.isLoggingDebug() >> true
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//for getNearestStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		service.extractSiteId() >>"BedBathCanada"
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> "favStore"
		1*manager.getNearestStoresByCoordinates("long1","lat1", requestMock, responseMock, false)
		1*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> [store,store1]
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		2*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> "radius"

		Map<String,Integer> productStatusMap = new HashMap()
		1*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> productStatusMap

		//for setBabyCanadaFlag
		store.getStoreId() >> "3"
		store1.getStoreId() >> "5"
		//end setBabyCanadaFlag

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == "radius"
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		vo.getProductStatusMap() == productStatusMap
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == [store,store1]
		1*store.setBabyCanadaFlag(true)
		1*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false and getNearestStoreDetailsForPDP is called to get store details and Search is based on Favourite Store"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		service.extractSiteId() >>"BedBathUS"
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathUS", profile) >> "favStore"
		1*manager.getNearestStoresByStoreId("favStore", requestMock, responseMock, "favStore", false) >> {}
		1*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null

		Map<String,Integer> productStatusMap = new HashMap()
		1*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> productStatusMap

		//for setBabyCanadaFlag
		store.getStoreId() >> "3"
		store1.getStoreId() >> "5"
		//end setBabyCanadaFlag

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathUS")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		vo.getProductStatusMap() == productStatusMap
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false and getNearestStoreDetailsForPDP is called to get store details and search is based on Lat/Lng"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		service.extractSiteId() >>"BedBathCanada"
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> ""
		1*manager.getNearestStoresByCoordinates("latitude","longitude", requestMock, responseMock, false) >> {}
		1*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null

		Map<String,Integer> productStatusMap = new HashMap()
		1*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> productStatusMap

		//for setBabyCanadaFlag
		store.getStoreId() >> "3"
		store1.getStoreId() >> "5"
		//end setBabyCanadaFlag

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == ""
		wrapper.getEmptyDOMResponse() == "emptyDom"
		vo.getProductStatusMap() == productStatusMap
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false and getNearestStoreDetailsForPDP is called to get store details and search is based on SearchString"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "search")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		service.extractSiteId() >>"BedBathCanada"
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> ""
		1*manager.getStoresBySearchString("search", requestMock,responseMock, false)
		1*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null

		Map<String,Integer> productStatusMap = new HashMap()
		1*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> productStatusMap

		//for setBabyCanadaFlag
		store.getStoreId() >> "3"
		store1.getStoreId() >> "5"
		//end setBabyCanadaFlag

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == ""
		wrapper.getEmptyDOMResponse() == "emptyDom"
		vo.getProductStatusMap() == productStatusMap
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false and getNearestStoreDetailsForPDP is called to get store details and search critirea is empty"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "")
		inputParam.put(SelfServiceConstants.LONGITUDE,"longitude")
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		service.extractSiteId() >>"BedBathCanada"
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> ""

		Map<String,Integer> productStatusMap = new HashMap()
		0*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> productStatusMap

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == "true"
		wrapper.getFavStoreId() == null
		wrapper.getEmptyDOMResponse() == null
		vo.getProductStatusMap() == null
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false and getNearestStoreDetailsForPDP is called to get store details and search critirea is not empty"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "abc")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"")

		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		service.extractSiteId() >>"BedBathCanada"
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> "favstore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, false) >> {}
		1*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null

		Map<String,Integer> productStatusMap = new HashMap()
		1*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> productStatusMap

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getFavStoreId() == "favstore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		vo.getProductStatusMap() == productStatusMap
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false, getNearestStoreDetailsForPDP is called to get store details and exception is thrown while with error message not empty"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "abc")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"")

		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		LblTxtTemplateManager lblManager =Mock()
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		service.extractSiteId() >>"BedBathCanada"
		service.setLblTxtTemplateManager(lblManager)
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> "favstore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, false) >> {throw new Exception("")}
		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,BBBCoreConstants.DEFAULT_LOCALE, null) >> "exception occured"
		0*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> null

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getFavStoreId() == null
		wrapper.getEmptyDOMResponse() == null
		vo.getProductStatusMap() == null
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		wrapper.getErrorInViewAllStores() == "exception occured"
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when favourite store flag is false, getNearestStoreDetailsForPDP is called to get store details and exception is thrown while with error message empty"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		inputParam.put("orderedQty","10")
		inputParam.put("skuID","1")
		inputParam.put("searchString", "abc")
		inputParam.put(BBBCoreConstants.IS_FROM_PDP ,"true")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(BBBCoreConstants.SKUID,"2")
		inputParam.put(BBBCoreConstants.RADIUS, "radius")
		inputParam.put(SelfServiceConstants.LATITUDE, "latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE,"")

		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for getNearestStoreDetailsForPDP
		LblTxtTemplateManager lblManager =Mock()
		Profile profile =Mock()
		SearchStoreManager manager =Mock()
		StoreDetails store =Mock()
		StoreDetails store1 =Mock()

		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiuschange")
		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		service.extractSiteId() >>"BedBathCanada"
		service.setLblTxtTemplateManager(lblManager)
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		1*manager.fetchFavoriteStoreId("BedBathCanada", profile) >> "favstore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, false) >> {throw new Exception("")}
		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		0*requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getObjectParameter(BBBCoreConstants.PRODUCT_AVAILABLE_VIEW_STORE) >> null

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		rep.getView("specialityCodeMap") >> repView
		store.getSpecialtyShopsCd() >> "cd"
		service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		//for getNearestStoreDetailsForPDP
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , "radiuschange")
		1*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SKUID,"1")
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"BedBathCanada")
		StoreDetailsWrapper wrapper =vo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getFavStoreId() == null
		wrapper.getEmptyDOMResponse() == null
		vo.getProductStatusMap() == null
		// for setBabyCanadaFlag
		wrapper.getStoreDetails() == null
		wrapper.getErrorInViewAllStores() == BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES
		0*store.setBabyCanadaFlag(true)
		0*store1.setBabyCanadaFlag(false)
		//end setBabyCanadaFlag
		// for storespecialitycode
		0*store.setStoreSpecialityVO(_)
		0*store1.setStoreSpecialityVO(_)
	}

	def"searchSKUInStore, when isfromPDP is false"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "true")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		3*det.getStoreId() >> "3"
		3*det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> null
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["str4"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("6", 6)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		RepositoryItem rItem = Mock()
		service.setStoreRepository(rep)
		2*rep.getView("specialityCodeMap") >> repView
		1*det.getSpecialtyShopsCd() >> "cd"
		2*service.extractDBCall(repView, _, _) >> [rItem]
		rItem.getPropertyValue("specialityCd") >> [] >> null
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		Map<String, Integer> productStatusMap =vo.getProductStatusMap()
		productStatusMap.containsKey("6") == true
		productStatusMap.containsValue(106) ==  true
		productStatusMap.containsKey("3") == true
		productStatusMap.containsValue(10) ==  true
		productStatusMap.containsKey("16") == true
		productStatusMap.containsValue(16) ==  true
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false ,BopusEligibleStates is not null , and getBopusInEligibleStores contains the store detailID obtained from store details list"(){ //dochange

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "true")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		3*det.getStoreId() >> "3"
		4*det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		det.getState() >> "state"
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("6", 6)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		2*rep.getView("specialityCodeMap") >> repView
		1*det.getSpecialtyShopsCd() >> "cd"
		2*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		Map<String, Integer> productStatusMap =vo.getProductStatusMap()
		productStatusMap.containsKey("6") == true
		productStatusMap.containsValue(106) ==  true
		productStatusMap.containsKey("3") == true
		productStatusMap.containsValue(10) ==  true
		productStatusMap.containsKey("16") == true
		productStatusMap.containsValue(16) ==  true
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false , and input param Map contains false for babyCA"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "false")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		3*det.getStoreId() >> "3"
		4*det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		det.getState() >> "state"
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("6", 6)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		2*rep.getView("specialityCodeMap") >> repView
		1*det.getSpecialtyShopsCd() >> "cd"
		2*service.extractDBCall(repView, _, _) >> null
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		Map<String, Integer> productStatusMap =vo.getProductStatusMap()
		productStatusMap.containsKey("6") == true
		productStatusMap.containsValue(106) ==  true
		productStatusMap.containsKey("3") == true
		productStatusMap.containsValue(10) ==  true
		productStatusMap.containsKey("16") == true
		productStatusMap.containsValue(16) ==  true
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when RepositoryException is thrown in storeSpecialityCode method "(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "false")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		3*det.getStoreId() >> "3"
		4*det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		det.getState() >> "state"
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("6", 6)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		1*rep.getView("specialityCodeMap") >> {throw new RepositoryException()}
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == null
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
		1*service.logError("Repository Exception while fetching store speciality codeatg.repository.RepositoryException")
		vo.getErrorMessage() == "err_something_wrong"
		vo.getErrorCode() == "70701"
		vo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ "err_something_wrong")
	}

	def"searchSKUInStore, when isfromPDP is false , store details list is null, getStoreAddressSuggestion list is not null"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "true")
		storeWrapper.getStoreDetails() >>null
		1*requestMock.getLocale() >> null
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"
		3*storeWrapper.getStoreAddressSuggestion() >> [suggest]

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() == null
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == [suggest]
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false , input param contains babyCA flag as false, store details list is null, getStoreAddressSuggestion list is empty"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "false")
		storeWrapper.getStoreDetails() >> null >> []
		1*requestMock.getLocale() >> null
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"
		2*storeWrapper.getStoreAddressSuggestion() >> []

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() == []
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}


	def"searchSKUInStore, when isfromPDP is false , store details list is null, getStoreAddressSuggestion list is empty"(){  //dochange

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "true")
		storeWrapper.getStoreDetails() >>null
		1*requestMock.getLocale() >> null
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"
		2*storeWrapper.getStoreAddressSuggestion() >> []

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() == null
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false , store details list is null, getStoreAddressSuggestion list is null"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "true")
		storeWrapper.getStoreDetails() >>null
		1*requestMock.getLocale() >> null
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null)
		1*storeWrapper.getStoreAddressSuggestion() >> null

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() == null
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false , inputParamMap contains babyCA as false  objStoreDetailsWrapper is null"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> null
		inputParam.put("babyCA", "false")
		0*storeWrapper.getStoreDetails() >>null
		ServletUtil.setCurrentRequest(null)
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",null , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"
		0*storeWrapper.getStoreAddressSuggestion() >> null

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> null
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == null
		0*storeWrapper.setRadius("radius")
		/*0*storeWrapper.getStoreDetails() == null*/
		0*storeWrapper.setEmptyDOMResponse(null)
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		0*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when site id is null"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >> null
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		1*manager.getStoreType(null) >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> null
		0*storeWrapper.getStoreDetails() >>null
		ServletUtil.setCurrentRequest(null)
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",null , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null) >> "labelForMobileCanada"
		0*storeWrapper.getStoreAddressSuggestion() >> null

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathCanada") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", _,10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability
		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> null
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == null
		0*storeWrapper.setRadius("radius")
		0*storeWrapper.setEmptyDOMResponse(null)
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		0*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when site id is not BedBathCanada"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreAddressSuggestion suggest = Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >> "BedBathUS"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		1*manager.getStoreType("BedBathUS") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> null
		0*storeWrapper.getStoreDetails() >>null
		ServletUtil.setCurrentRequest(null)
		0*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",null , ["inputSearchString":"", "bedBathCanadaStore":"1", "babyCanadaStore":"1", "storeMiles":"radius"], null)
		0*storeWrapper.getStoreAddressSuggestion() >> null

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		0*cTools.getBopusEligibleStates("BedBathUS") >> ["str2"]
		0*det.getState() >> "state"
		0*cTools.getBopusInEligibleStores("storeType", "BedBathUS") >> ["6"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		0*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		0*inventoryManager.getBOPUSProductAvailability("BedBathUS", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> null
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == null
		0*storeWrapper.setRadius("radius")
		0*storeWrapper.setEmptyDOMResponse(null)
		0*det.setBabyCanadaFlag(true)
		0*det1.setBabyCanadaFlag(false)
		0*storeWrapper.setLabelForMobileCanada(null)
		vo.getStoreAddressSuggestion() == null
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false ,and  Store pick not available for state"(){  //dochange

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "false")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		3*det.getStoreId() >> "3"
		3*det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea(_,locale.getLanguage() , _, null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> ["state"]
		det.getState() >> "state"
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> ["str3"]

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("8", 8)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", _,10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		2*rep.getView("specialityCodeMap") >> repView
		1*det.getSpecialtyShopsCd() >> "cd"
		2*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		Map<String, Integer> productStatusMap =vo.getProductStatusMap()
		productStatusMap.containsKey("8") == true
		productStatusMap.containsValue(8) ==  true
		productStatusMap.containsKey("3") == false
		productStatusMap.containsValue(10) ==  false
		productStatusMap.containsKey("16") == true
		productStatusMap.containsValue(16) ==  true
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false ,bopusInEligibleStoreList is null, and  Store pick not available for state"(){  //dochange

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "false")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		2*det.getStoreId() >> "3"
		2*det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , _, null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> ["state"]
		det.getState() >> "state"
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> null

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("8", 8)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> storeInventoryMap
		//ends checkProductAvailability

		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		2*rep.getView("specialityCodeMap") >> repView
		1*det.getSpecialtyShopsCd() >> "cd"
		2*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		Map<String, Integer> productStatusMap =vo.getProductStatusMap()
		productStatusMap.containsKey("8") == true
		productStatusMap.containsValue(8) ==  true
		productStatusMap.containsKey("3") == false
		productStatusMap.containsValue(10) ==  false
		productStatusMap.containsKey("16") == true
		productStatusMap.containsValue(16) ==  true
		vo.getObjStoreDetails() == storeWrapper
		1*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		1*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null
	}

	def"searchSKUInStore, when isfromPDP is false ,InventoryException is thrown while checkingProductAvailability "(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Map<String, String> inputParam = new HashMap()
		setMapParametersForsearchSKUInStore(inputParam)

		// for getStoresForSku
		Locale locale = new Locale("en_US")
		StoreDetails det =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails det1 =Mock()
		service.extractSiteId() >>"BedBathCanada"
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper storeWrapper =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		manager.getStoreType("BedBathCanada") >> "storeType"

		1*service.searchStores(inputParam, "storeType") >> storeWrapper
		inputParam.put("babyCA", "false")
		storeWrapper.getStoreDetails() >> [det, det1]
		2*requestMock.getLocale() >> locale
		det.getStoreId() >> "3"
		det1.getStoreId() >> "6"
		1*lblManager.getPageTextArea("txt_store_canada_1_1_found_msg",locale.getLanguage() , _, null) >> "labelForMobileCanada"

		//for checkProductAvailability inside getStoresForSku
		BBBCatalogTools cTools =Mock()
		BBBStoreInventoryContainer container =Mock()
		BBBInventoryManager inventoryManager =Mock()
		service.setCatalogTools(cTools)
		service.setInventoryManager(inventoryManager)
		1*cTools.getBopusEligibleStates("BedBathCanada") >> null
		1*cTools.getBopusInEligibleStores("storeType", "BedBathCanada") >> null

		Map<String, Integer> storeInventoryMap = new HashMap()
		storeInventoryMap.put("8", 8)
		storeInventoryMap.put("16", 16)
		1*requestMock.resolveName("com/bbb/commerce/common/BBBStoreInventoryContainer") >> container
		1*inventoryManager.getBOPUSProductAvailability("BedBathCanada", "1", ["3","6"],10, BBBInventoryManager.ONLINE_STORE,container, false, null, false,false) >> {throw new InventoryException()}
		//ends checkProductAvailability

		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDOM"
		//ends getStoresForSku

		//for storespecialitycode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		0*rep.getView("specialityCodeMap") >> repView
		0*det.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> [null]
		//ends storespecialitycode

		when:
		SearchInStoreVO vo =service.searchSKUInStore(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*requestMock.setParameter(BBBCoreConstants.ORDER_QUANTITY,inputParam.get(BBBCoreConstants.ORDER_QUANTITY))
		0*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		0*requestMock.setParameter(BBBCoreConstants.SKUID,inputParam.get(BBBCoreConstants.SKUID))
		//for getStoresForSku
		vo.getProductStatusMap() == null
		vo.getObjStoreDetails() == null
		0*storeWrapper.setRadius("radius")
		storeWrapper.getStoreDetails() ==[det,det1]
		0*storeWrapper.setEmptyDOMResponse("emptyDOM")
		1*det.setBabyCanadaFlag(true)
		1*det1.setBabyCanadaFlag(false)
		1*storeWrapper.setLabelForMobileCanada("labelForMobileCanada")
		//end getLocalStoreDetailsForPDP
		//for storespecialitycode
		det.getStoreSpecialityVO() == null

		1*service.logDebug("Inventory exception while fetching store details: atg.commerce.inventory.InventoryException")
		vo.getErrorMessage() == "err_something_wrong"
		vo.getErrorCode() == "70701"
		vo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ "err_something_wrong")
	}

	def"searchStores, when inputParameterMap is null "(){

		given:
		service =Spy()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*service.extractSiteId() >> "siteID"
		String storeType = ""
		1*manager.getStoreType("siteID") >> "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(null, storeType)

		then:
		wrapper == null
		BBBSystemException e = thrown()
	}

	def"searchStores, when inputParameterMap is empty "(){

		given:
		service =Spy()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		0*service.extractSiteId() >> "siteID"
		Map<String, String> inputParam = new HashMap()
		String storeType = "store"
		0*manager.getStoreType("siteID") >> "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		wrapper == null
		BBBSystemException e = thrown()
	}

	def"searchStores, when NumberFormatExxception is thrown while converting radius "(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		0*service.extractSiteId() >> "siteID"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "radiusStr")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put("appointmentCode","appointmentType")
		inputParam.put(BBBCoreConstants.PAGESIZE,"pgsize")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"pageNumber")
		String storeType = "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		1*service.logError("err_invalid_radiusjava.lang.NumberFormatException: "+"For input string: \"" + inputParam.get(SelfServiceConstants.RADIUS) + "\"")
		BBBBusinessException e = thrown()
	}

	def"searchStores, when BBBBusinessException is thrown if radius is less than zero"(){

		given:
		service =Spy()
		0*service.extractSiteId() >> "siteID"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "-1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"pgsize")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"pageNumber")
		String storeType = "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		0*service.logError("err_invalid_radiusjava.lang.NumberFormatException: "+"For input string: \"" + inputParam.get(SelfServiceConstants.RADIUS) + "\"")
		BBBBusinessException e = thrown()
	}

	def"searchStores, when NumberFormatExxception is thrown while converting pageNumber"(){

		given:
		service =Spy()
		0*service.extractSiteId() >> "siteID"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"pgsize")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"pgNumber")
		String storeType = "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		1*service.logError("err_invalid_pageNumberjava.lang.NumberFormatException: "+"For input string: \"" + inputParam.get(BBBCoreConstants.PAGENUMBER) + "\"")
		BBBBusinessException e = thrown()
	}

	def"searchStores, when BBBBusinessException is thrown if pageNumber is less than zero"(){

		given:
		0*service.extractSiteId() >> "siteID"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"pgsize")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"-1")
		String storeType = "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		0*service.logError("err_invalid_pageNumberjava.lang.NumberFormatException: "+"For input string: \"" + inputParam.get(BBBCoreConstants.PAGENUMBER) + "\"")
		BBBBusinessException e = thrown()
	}

	def"searchStores, when NumberFormatException is thrown while converting pageSize"(){

		given:
		service =Spy()
		0*service.extractSiteId() >> "siteID"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"pgsize")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"")
		String storeType = "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		1*service.logError("err_invalid_pageNumberjava.lang.NumberFormatException: "+"For input string: \"" + inputParam.get(BBBCoreConstants.PAGESIZE) + "\"")
		BBBBusinessException e = thrown()
	}

	def"searchStores, when BBBBusinessException is thrown if pagesize is less than zero"(){

		given:
		0*service.extractSiteId() >> "siteID"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"-1")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"")
		String storeType = "store"

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		0*service.logError("err_invalid_pageNumberjava.lang.NumberFormatException: "+"For input string: \"" + inputParam.get(BBBCoreConstants.PAGESIZE) + "\"")
		BBBBusinessException e = thrown()
	}

	def"searchStores, when objStoreDetailsWrapper is null"(){

		given:
		0*service.extractSiteId() >> "siteID"
		SearchStoreManager manager = Mock()
		service.setSearchStoreManager(manager)

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"2")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		String storeType = "store"

		manager.searchStorePerPage("pgKey", "3") >> null

		when:
		StoreDetailsWrapper wrapper =service.searchStores(inputParam, storeType)

		then:
		wrapper == null
	}

	def"searchStores, when page size is empty and pageKey and page Number are not empty"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store1 =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		Profile pr =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> ["s1"]
		wrapper.getStoreDetails() >> [store1,store2,store3]

		2*store1.getStoreType() >> "s1"
		1*store2.getStoreType() >> null
		2*store3.getStoreType() >> "s3type"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		service.setStoreRepository(rep)
		rep.getView("specialityCodeMap") >> repView
		0*store1.getSpecialtyShopsCd() >> "cd1"
		1*store2.getSpecialtyShopsCd() >> "cd2"
		1*store3.getSpecialtyShopsCd() >> "cd3"
		service.extractDBCall(repView, _, _) >> null
		//end storeSpecialityCode

		Map<String, Boolean> appointmentMap = new HashMap()
		appointmentMap.put("string", true)
		1*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),"appCode") >> appointmentMap
		1*scManager.fetchPreSelectedServiceRef("appCode") >> 10
		1*scManager.canScheduleAppointmentForSiteId("tbs") >> true

		1*scManager.checkAppointmentEligible(store2,appointmentMap, true) >> {}
		1*scManager.checkAppointmentEligible(store3,appointmentMap, true) >> {}

		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pr
		1*manager.fetchFavoriteStoreId("tbs", pr) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper.getStoreDetails() == [store2,store3]
		store2.setPreSelectedServiceRef(10)
		store3.setPreSelectedServiceRef(10)
		1*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
	}

	def"searchStores, when page size is empty and pageKey and page Number are not empty, appontment map is empty"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		Profile pr =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> null
		wrapper.getStoreDetails() >> [null]

		Map<String, Boolean> appointmentMap = new HashMap()
		1*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),"appCode") >> appointmentMap
		1*scManager.fetchPreSelectedServiceRef("appCode") >> 10
		0*scManager.canScheduleAppointmentForSiteId("tbs") >> true
		0*scManager.checkAppointmentEligible(null,appointmentMap, true) >> {}

		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pr
		1*manager.fetchFavoriteStoreId("tbs", pr) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper.getStoreDetails() == [null]
		0*store2.setPreSelectedServiceRef(10)
		0*store3.setPreSelectedServiceRef(10)
		1*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
	}

	def"searchStores, when page size is empty and pageKey and page Number are not empty, appontment map is null"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		Profile pr =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> []
		wrapper.getStoreDetails() >> [null]

		1*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),"appCode") >> null
		1*scManager.fetchPreSelectedServiceRef("appCode") >> 10
		0*scManager.canScheduleAppointmentForSiteId("tbs") >> true
		0*scManager.checkAppointmentEligible(null,null, true) >> {}
		0*scManager.checkAppointmentEligible(store3,null, true) >> {}

		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pr
		1*manager.fetchFavoriteStoreId("tbs", pr) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper.getStoreDetails() == [null]
		0*store2.setPreSelectedServiceRef(10)
		0*store3.setPreSelectedServiceRef(10)
		1*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
	}

	def"searchStores, when BBBSystemException is thrown"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> []
		wrapper.getStoreDetails() >> [null]

		1*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),"appCode") >> {throw new BBBSystemException("")}
		0*scManager.fetchPreSelectedServiceRef("appCode") >> 10
		0*scManager.canScheduleAppointmentForSiteId("tbs") >> true
		0*scManager.checkAppointmentEligible(null,null, true) >> {}
		0*scManager.checkAppointmentEligible(store3,null, true) >> {}
		0*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> null
		0*manager.fetchFavoriteStoreId("tbs", null) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper == null
		0*store2.setPreSelectedServiceRef(10)
		0*store3.setPreSelectedServiceRef(10)
		0*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
		2*service.logError(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR+ BBBCoreConstants.MAPQUESTSTORETYPE, _)
		BBBSystemException e = thrown()
	}

	def"searchStores, when page size is empty and pageKey and page Number are not empty,appointment type is null "(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		Profile pr =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", null)
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> []
		wrapper.getStoreDetails() >> [null]

		0*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),null) >> null
		0*scManager.fetchPreSelectedServiceRef(null) >> 10
		0*scManager.canScheduleAppointmentForSiteId("tbs") >> true
		0*scManager.checkAppointmentEligible(null,null, true) >> {}
		0*scManager.checkAppointmentEligible(store3,null, true) >> {}

		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pr
		1*manager.fetchFavoriteStoreId("tbs", pr) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper.getStoreDetails() == [null]
		0*store2.setPreSelectedServiceRef(_)
		0*store3.setPreSelectedServiceRef(_)
		1*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
	}

	def"searchStores, when page size is empty and pageKey and page Number are not empty,storeDetailsList is empty "(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		Profile pr =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> []
		wrapper.getStoreDetails() >> []

		0*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),"appCode") >> null
		0*scManager.fetchPreSelectedServiceRef("appCode") >> 10
		0*scManager.canScheduleAppointmentForSiteId("tbs") >> true
		0*scManager.checkAppointmentEligible(null,null, true) >> {}
		0*scManager.checkAppointmentEligible(store3,null, true) >> {}

		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pr
		1*manager.fetchFavoriteStoreId("tbs", pr) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper.getStoreDetails() == []
		0*store2.setPreSelectedServiceRef(_)
		0*store3.setPreSelectedServiceRef(_)
		1*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
	}

	def"searchStores, when page size is empty and pageKey and page Number are not empty,storeDetailsList is null "(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		BBBCatalogTools cTools =Mock()
		ScheduleAppointmentManager scManager =Mock()
		StoreDetailsWrapper wrapper =Mock()
		StoreDetails store1 =Mock()
		StoreDetails store2 =Mock()
		StoreDetails store3 =Mock()
		Profile pr =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setCatalogTools(cTools)
		service.setScheduleAppointmentManager(scManager)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"pgKey")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		String storeType = "store"

		1*manager.searchStorePerPage("pgKey", "3") >> wrapper
		1*cTools.getAllValuesForKey(BBBCoreConstants.MAPQUESTSTORETYPE,"excluded_store_type") >> []
		wrapper.getStoreDetails() >> [] >>[] >>[] >>null

		0*scManager.checkAppointmentAvailability(wrapper.getStoreDetails(),"appCode") >> null
		0*scManager.fetchPreSelectedServiceRef("appCode") >> 10
		0*scManager.canScheduleAppointmentForSiteId("tbs") >> true
		0*scManager.checkAppointmentEligible(null,null, true) >> {}
		0*scManager.checkAppointmentEligible(store3,null, true) >> {}

		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pr
		1*manager.fetchFavoriteStoreId("tbs", pr) >> "favStore"

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper.getStoreDetails() == null
		0*store2.setPreSelectedServiceRef(_)
		0*store3.setPreSelectedServiceRef(_)
		1*wrapper.setFavStoreId("favStore")
		0*store2.setBabyCanadaFlag(true)
		0*store3.setBabyCanadaFlag(false)
	}

	def"searchStores, when page size is empty,pagekey is empty and objStoreDetailsWrapper is null"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		StoreTools stTools =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc | abc1")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"3")
		inputParam.put("appointmentCode", "appCode")
		inputParam.put(BBBCoreConstants.USE_MY_CURRENT_LOCATION,"US")
		inputParam.put(BBBCoreConstants.IS_FROM_STORE_LOCATOR, "true")
		String storeType = "store"

		1*manager.getMapQuestSearchStringForLatLng() >> "mapQuest"
		1*stTools.getStoresForMobile("abc ", " abc1",_,"store") >> null

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper == null
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"1")
		1*requestMock.setParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION, "US")
		1*requestMock.setParameter(BBBCoreConstants.SET_COOKIE, BBBCoreConstants.FALSE);
	}

	def"searchStores, when page size is empty,pageNumber is empty and objStoreDetailsWrapper is null"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		StoreTools stTools =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"2")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"")
		inputParam.put("appointmentCode", "appCode")
		inputParam.put(BBBCoreConstants.USE_MY_CURRENT_LOCATION,"US")
		inputParam.put(BBBCoreConstants.IS_FROM_STORE_LOCATOR, "false")
		String storeType = "store"

		1*manager.getMapQuestSearchStringForLatLng() >> "mapQuest"
		1*stTools.getStoresForMobile("latitude", "longitude",_,"store") >> null

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper == null
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"1")
		1*requestMock.setParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION, "US")
		0*requestMock.setParameter(BBBCoreConstants.SET_COOKIE, BBBCoreConstants.FALSE);
	}

	def"searchStores, when page size is empty,pageNumber is empty and BBBBusinessException is Thrown"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		StoreTools stTools =Mock()
		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"2")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"")
		inputParam.put("appointmentCode", "appCode")
		inputParam.put(BBBCoreConstants.USE_MY_CURRENT_LOCATION,"US")
		inputParam.put(BBBCoreConstants.IS_FROM_STORE_LOCATOR, "false")
		String storeType = "store"

		1*manager.getMapQuestSearchStringForLatLng() >> {throw new BBBBusinessException("")}
		0*stTools.getStoresForMobile("latitude", "longitude",_,"store") >> null

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper == null
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"1")
		0*requestMock.setParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION, "US")
		0*requestMock.setParameter(BBBCoreConstants.SET_COOKIE, BBBCoreConstants.FALSE);
		1*service.logError(SelfServiceConstants.ERROR_SEARCH_STORE_TECH_ERROR+ BBBCoreConstants.MAPQUESTSTORETYPE,_)
		BBBBusinessException e = thrown()
	}

	def"searchStores, when page size is empty,pageNumber is empty and searchstring is empty"(){

		given:
		service =Spy()
		SearchStoreManager manager = Mock()
		StoreTools stTools =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"2")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"")
		inputParam.put("appointmentCode", "appCode")
		inputParam.put(BBBCoreConstants.USE_MY_CURRENT_LOCATION,"US")
		inputParam.put(BBBCoreConstants.IS_FROM_STORE_LOCATOR, "false")
		String storeType = "store"

		0*manager.getMapQuestSearchStringForLatLng() >> "mapQuest"
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"
		1*stTools.getStoresForMobile("long1", "lat1",_,"store") >> null

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper == null
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"1")
		1*requestMock.setParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION, "US")
		0*requestMock.setParameter(BBBCoreConstants.SET_COOKIE, BBBCoreConstants.FALSE);
	}

	def"searchStores, when page size is empty,pageNumber is empty, searchstring is empty and LAT_LNG_COOKIE is empty"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		SearchStoreManager manager = Mock()
		StoreTools stTools =Mock()
		0*service.extractSiteId() >> "siteID"
		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)
		service.extractSiteId() >> "tbs"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put(SelfServiceConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.PAGESIZE,"")
		inputParam.put(BBBCoreConstants.PAGEKEY,"2")
		inputParam.put(BBBCoreConstants.PAGENUMBER,"")
		inputParam.put("appointmentCode", "appCode")
		inputParam.put(BBBCoreConstants.USE_MY_CURRENT_LOCATION,"US")
		inputParam.put(BBBCoreConstants.IS_FROM_STORE_LOCATOR, "false")
		String storeType = "store"

		0*manager.getMapQuestSearchStringForLatLng() >> "mapQuest"
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""
		1*stTools.getStoresForMobile("latitude", "longitude",_,"store") >> null

		when:
		StoreDetailsWrapper storewrapper =service.searchStores(inputParam, storeType)

		then:
		storewrapper == null
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"1")
		1*requestMock.setParameter(BBBCoreConstants.USE_MY_CURRENT_LOCATION, "US")
		0*requestMock.setParameter(BBBCoreConstants.SET_COOKIE, BBBCoreConstants.FALSE);
	}

	def"modifyLatLngCookieForMobile, modifies the lattitude longitude cookie for mobile"(){

		given:
		service.setLoggingDebug(true)
		SearchStoreManager manager = Mock()
		RepositoryItem rItem =Mock()
		StoreTools stTools =Mock()
		String storeIdFromURL = "storeId"
		String pSiteId = "tbs"

		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)

		1*manager.getStoreType("tbs") >> "store"
		1*stTools.getStores(storeIdFromURL, "store") >> [rItem]
		1*rItem.getPropertyValue(SelfServiceConstants.LATITUDE) >> "lat"
		1*rItem.getPropertyValue(SelfServiceConstants.LONGITUDE) >> "long"

		when:
		String latLong =service.modifyLatLngCookieForMobile (storeIdFromURL , pSiteId)

		then:
		latLong == "long,lat"
	}

	def"modifyLatLngCookieForMobile, when store list is null"(){

		given:
		SearchStoreManager manager = Mock()
		RepositoryItem rItem =Mock()
		service.setLoggingDebug(true)
		StoreTools stTools =Mock()
		String storeIdFromURL = "storeId"
		String pSiteId = "tbs"

		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)

		1*manager.getStoreType("tbs") >> "store"
		1*stTools.getStores(storeIdFromURL, "store") >> null
		0*rItem.getPropertyValue(SelfServiceConstants.LATITUDE) >>null
		0*rItem.getPropertyValue(SelfServiceConstants.LONGITUDE) >> null

		when:
		String latLong =service.modifyLatLngCookieForMobile (storeIdFromURL , pSiteId)

		then:
		latLong == ""
	}

	def"modifyLatLngCookieForMobile, when storeIdFromURL is null"(){

		given:
		service.setLoggingDebug(true)
		SearchStoreManager manager = Mock()
		RepositoryItem rItem =Mock()
		StoreTools stTools =Mock()
		String storeIdFromURL = null
		String pSiteId = "tbs"

		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)

		0*manager.getStoreType("tbs") >> "store"
		0*stTools.getStores(storeIdFromURL, _) >> null
		0*rItem.getPropertyValue(SelfServiceConstants.LATITUDE) >>null
		0*rItem.getPropertyValue(SelfServiceConstants.LONGITUDE) >> null

		when:
		String latLong =service.modifyLatLngCookieForMobile (storeIdFromURL , pSiteId)

		then:
		latLong == ""
	}

	def"modifyLatLngCookieForMobile, when storeIdFromURL is empty"(){

		given:
		service.setLoggingDebug(true)
		SearchStoreManager manager = Mock()
		RepositoryItem rItem =Mock()
		StoreTools stTools =Mock()
		String storeIdFromURL = ""
		String pSiteId = "tbs"

		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)

		0*manager.getStoreType("tbs") >> "store"
		0*stTools.getStores(storeIdFromURL, _) >> null
		0*rItem.getPropertyValue(SelfServiceConstants.LATITUDE) >>null
		0*rItem.getPropertyValue(SelfServiceConstants.LONGITUDE) >> null

		when:
		String latLong =service.modifyLatLngCookieForMobile (storeIdFromURL , pSiteId)

		then:
		latLong == ""
	}

	def"modifyLatLngCookieForMobile, when exception is thrown"(){

		given:
		SearchStoreManager manager = Mock()
		RepositoryItem rItem =Mock()
		StoreTools stTools =Mock()
		String storeIdFromURL = "id"
		String pSiteId = "tbs"

		service.setSearchStoreManager(manager)
		service.setStoreTools(stTools)

		1*manager.getStoreType("tbs") >> "store"
		1*stTools.getStores(storeIdFromURL, "store") >> {throw new Exception("")}
		0*rItem.getPropertyValue(SelfServiceConstants.LATITUDE) >>null
		0*rItem.getPropertyValue(SelfServiceConstants.LONGITUDE) >> null

		when:
		String latLong =service.modifyLatLngCookieForMobile (storeIdFromURL , pSiteId)

		then:
		latLong == null
		BBBBusinessException e = thrown()
	}


	def"searchStoreForPLP , if input param is null"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(null)

		then:
		storeVo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT
		storeVo.getErrorCode() == "70701"
		storeVo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ "err_search_store_invalid_input")
	}

	def"searchStoreForPLP , if input param is empty"(){

		given:
		service =Spy()
		Map<String, String> inputParam = new HashMap()

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		storeVo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_INVALID_INPUT
		storeVo.getErrorCode() == "70701"
		storeVo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ "err_search_store_invalid_input")
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP and store is retreived from getNearestDetailsForPlp"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Cookie c = Mock()
		Cookie c1 = Mock()
		Cookie c2 = Mock()
		StoreTools stTools = Mock()
		service.setStoreTools(stTools)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		setMapForsearchStoreForPLP(inputParam)

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> "lat1,long1"
		requestMock.getCookies() >>[c,c1,c2]
		c.getName() >> "latLngCookie"
		c.getValue() >>"cValue"
		c1.getName() >> "c1Cookie"
		1*stTools.getCookieTimeOut() >> 15
		1*requestMock.getServerName() >>"server"
		2*c2.getName() >> "latLngCookie"
		2*c2.getValue() >>"lat1,long1"
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "latlongCookie"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getNearestStoresByCoordinates("long1","lat1", requestMock, responseMock, true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> [stDetial]
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> "radiusMiles"
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		1*rep.getView("specialityCodeMap") >> repView
		1*stDetial.getSpecialtyShopsCd() >> "cd"
		1*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		2*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == "radiusMiles"
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP,latLng is empty and store is retreived from getNearestDetailsForPlp"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		setMapForsearchStoreForPLP(inputParam)

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getNearestStoresByCoordinates("long1","lat1", requestMock, responseMock, true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> [stDetial]
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		1*rep.getView("specialityCodeMap") >> repView
		1*stDetial.getSpecialtyShopsCd() >> "cd"
		1*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP,  store is retreived from getNearestDetailsForPlP and Search is based on Fav Store"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		setMapForsearchStoreForPLP(inputParam)

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getNearestStoresByStoreId("favStore", requestMock, responseMock, "favStore", true)

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP,  store is retreived from getNearestDetailsForPlP when isCookie is true and search string is not empty"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		StoreTools st =Mock()
		service.setStoreTools(st)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> "lat1,long1"
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""
		st.getCookieTimeOut() >> 15
		1*requestMock.getServerName() >> "server"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, true)

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP,  store is retreived from getNearestDetailsForPlP and Search is based on Lat/Lng"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		setMapForsearchStoreForPLP(inputParam)

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		1*manager.getNearestStoresByCoordinates("latitude","longitude", requestMock, responseMock, true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == ""
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP, latitude is empty  store is retreived from getNearestDetailsForPlP and Search is based on SearchString"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP, when favourite store value is empty in the param map"(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		storeVo ==null
	}

	def"searchStoreForPLP, when storedIDFromUrl is empty "(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		0*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		storeVo ==null
	}

	def"searchStoreForPLP, when siteId is empty "(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","url")
		inputParam.put("siteId","")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		0*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		storeVo ==null
	}

	def"searchStoreForPLP, when callFromPLP is false "(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "")
		inputParam.put("storeIdFromURL","url")
		inputParam.put("siteId","")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		0*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		0*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		1*manager.getStoreType("siteId") >> "storeType"
		1*service.searchStores(inputParam, "storeType") >> null


		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		0*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		storeVo.getObjStoreDetails() ==null

	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP, longitude is empty  store is retreived from getNearestDetailsForPlP and Search is based on SearchString"(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP ,exception is thrown in getNearestDetailsForPlP method and error message is not null "(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setLblTxtTemplateManager(lblManager)
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, true) >> {throw new Exception()}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,BBBCoreConstants.DEFAULT_LOCALE, null) >> "ERROR_MSG"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorInViewAllStores() == "ERROR_MSG"
		wrapper.getEmptyDOMResponse() == null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP ,exception is thrown in getNearestDetailsForPlP method and error message is  null "(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		LblTxtTemplateManager lblManager =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setLblTxtTemplateManager(lblManager)
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, true) >> {throw new Exception()}

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >> "emptyDom"
		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,BBBCoreConstants.DEFAULT_LOCALE, null) >> null

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorInViewAllStores() == BBBCoreConstants.DEFAULT_ERROR_MSG_VIEW_STORES
		wrapper.getEmptyDOMResponse() == null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP,  store is retreived from getNearestDetailsForPlP and Search is based on SearchString"(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getStoresBySearchString("abc", requestMock,responseMock, true)

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == "favStore"
		wrapper.getEmptyDOMResponse() == "emptyDom"
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when call is from PLP,  store is retreived from getNearestDetailsForPlP and Search is not based on lat/Lng , cookie , Fav Store or searchString"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"false")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		0*manager.getStoresBySearchString("abc", requestMock,responseMock, true)

		requestMock.getObjectParameter(BBBCoreConstants.STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreConstants.EMPTY_RESPONSE_DOM) >>"emptyDom"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> repView
		0*stDetial.getSpecialtyShopsCd() >> "cd"
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		1*requestMock.setParameter(SelfServiceConstants.RADIUS_CHANGE , inputParam.get(SelfServiceConstants.RADIUS_CHANGE));
		1*requestMock.setParameter(BBBCoreConstants.RADIUS,inputParam.get(BBBCoreConstants.RADIUS))
		1*requestMock.setParameter(BBBCoreConstants.SITE_ID,"siteId")
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == null
		wrapper.getEmptyInput() == "true"
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when store is retreievd from getLocalStoreDetailsForPLP"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		StoreTools stTools = Mock()
		service.setStoreTools(stTools)
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> "lat1,long1"
		requestMock.getCookies() >>null
		1*stTools.getCookieTimeOut() >> 15
		1*requestMock.getServerName() >>"server"
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "latlongCookie"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "long1", "lat1",true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> [stDetial]
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> "radiusMiles"
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		1*rep.getView("specialityCodeMap") >> repView
		1*stDetial.getSpecialtyShopsCd() >> "cd"
		1*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == "radiusMiles"
		wrapper.getFavStoreId() == null
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when ,latLng is equal to LAT_LNG_COOKIE and store is retreievd from getLocalStoreDetailsForPLP"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Cookie c = Mock()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> "lat1,long1"
		requestMock.getCookies() >>null >>[c]
		c.getName() >> "latLngCookie"
		c.getValue() >>"cValue"
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		StoreDetails stDetial =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "long1", "lat1",true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> [stDetial]
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)

		1*rep.getView("specialityCodeMap") >> repView
		1*stDetial.getSpecialtyShopsCd() >> "cd"
		1*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

		//for StoreSpecilaityVO
		0*stDetial.setStoreSpecialityVO(_)
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when  store is retreievd from getLocalStoreDetailsForPLP and Search is based on Fav Store"(){

		given:
		service =Spy()
		Cookie c = Mock()
		service.extractSiteId() >> "siteId"

		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> ""

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> "favStore"
		1*manager.getFavStoreByStoreIdForPLP("siteId", requestMock, responseMock,"favStore") >> {}

		requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> null
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)

		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\

	}

	def"searchStoreForPLP , return the SearchInStoreVO object when  store is retreievd from getLocalStoreDetailsForPLP and Search is based on Lat/Lng"(){

		given:
		service =Spy()
		Cookie c = Mock()
		service.extractSiteId() >> "siteId"
		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		1*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "latitude", "longitude",true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		1*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> null
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorIneligibleStates() == "RESERVE_ONLINE_NOT_AVAILABLE"
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when  store is retreievd from getLocalStoreDetailsForPLP and Search is not based on fav store/lat or long/ cookie"(){

		given:
		service =Spy()
		Cookie c = Mock()
		service.extractSiteId() >> "siteId"
		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"")
		inputParam.put(SelfServiceConstants.LONGITUDE, "longitude")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP
		Profile pro =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		0*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "", "longitude",true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> null
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == "true"
		wrapper.getFavStoreId() == null
		wrapper.getErrorIneligibleStates() == null
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\
	}

	def"searchStoreForPLP , return the SearchInStoreVO object when  store is retreievd from getLocalStoreDetailsForPLP,longitude is empty and Search is not based on fav store/lat or long/ cookie"(){

		given:
		service =Spy()
		Cookie c = Mock()
		service.extractSiteId() >> "siteId"
		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		0*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "latitude", "",true) >> {}

		requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> null
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == "true"
		wrapper.getFavStoreId() == null
		wrapper.getErrorIneligibleStates() == null
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\
	}

	def"searchStoreForPLP ,when getLocalStoreDetailsForPLP method throws an exception and error message is not null"(){

		given:
		service =Spy()
		service.setLoggingDebug(true)
		Cookie c = Mock()
		service.extractSiteId() >> "siteId"
		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "long")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		SearchStoreManager manager =Mock()
		LblTxtTemplateManager lblManager =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		1*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "latitude", "long",true) >> {throw new Exception()}

		0*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >>"setErrorInViewFavStores"

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> null
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorInViewFavStores() == "setErrorInViewFavStores"
		wrapper.getErrorIneligibleStates() == null
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\
	}

	def"searchStoreForPLP ,when getLocalStoreDetailsForPLP method throws an exception and error message is null"(){

		given:
		service =Spy()
		service.extractSiteId() >> "siteId"
		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		inputParam.put(SelfServiceConstants.LATITUDE,"latitude")
		inputParam.put(SelfServiceConstants.LONGITUDE, "long")
		inputParam.put(BBBCoreConstants.FAVOURITE_STORE,"true")
		inputParam.put(SelfServiceConstants.RADIUS_CHANGE,"radiusChange")

		1*service.modifyLatLngCookieForMobile("idFromUrl", "tbs") >> null
		1*requestMock.getCookieParameter(SelfServiceConstants.LAT_LNG_COOKIE) >> "lat1,long1"

		//getNearestStoreDetailsForPLP\
		Profile pro =Mock()
		SearchStoreManager manager =Mock()
		LblTxtTemplateManager lblManager =Mock()
		service.setSearchStoreManager(manager)
		service.setLblTxtTemplateManager(lblManager)
		1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> pro
		1*manager.fetchFavoriteStoreId("siteId", pro) >> ""
		1*manager.getFavStoreByCoordinates("siteId", requestMock,responseMock, "latitude", "long",true) >> {throw new Exception()}

		0*requestMock.getObjectParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS) >> null
		0*sessionMock.getAttribute(SelfServiceConstants.RADIUSMILES) >> null
		0*requestMock.getParameter(BBBCoreErrorConstants.RESERVE_ONLINE_NOT_AVAILABLE) >>"RESERVE_ONLINE_NOT_AVAILABLE"

		1*lblManager.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> null

		//for storeSpecialityCode
		Repository rep =Mock()
		RepositoryView repView =Mock()
		BBBCatalogTools cTools =Mock()
		service.setStoreRepository(rep)
		service.setCatalogTools(cTools)
		0*rep.getView("specialityCodeMap") >> null
		0*service.extractDBCall(repView, _, _) >> null

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE,BBBCoreConstants.TRUE)
		//getNearestStoreDetailsForPLP\
		StoreDetailsWrapper wrapper =  storeVo.getObjStoreDetails()
		wrapper.getRadius() == null
		wrapper.getEmptyInput() == null
		wrapper.getFavStoreId() == null
		wrapper.getErrorInViewFavStores() == BBBCoreConstants.DEFAULT_ERROR_MSG_FAV_STORE
		wrapper.getErrorIneligibleStates() == null
		wrapper.getEmptyDOMResponse() == null
		wrapper.getErrorInViewAllStores() ==null
		//getNearestStoreDetailsForPLP\
	}

	def"searchStoreForPLP, when BBBSystemException is thrown"(){

		given:
		service =Spy()
		SearchStoreManager manager =Mock()
		service.setSearchStoreManager(manager)
		Map<String, String> inputParam = new HashMap()
		inputParam.put("searchString","abc")
		inputParam.put("callFromPLP", "true")
		inputParam.put("storeIdFromURL","idFromUrl")
		inputParam.put("siteId","tbs")
		inputParam.put(BBBCoreConstants.RADIUS, "1")
		1*manager.getStoreType("tbs") >> {throw new BBBSystemException("")}

		when:
		SearchInStoreVO storeVo = service.searchStoreForPLP(inputParam)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,inputParam.get(BBBCoreConstants.RADIUS))
		storeVo.getErrorMessage() == SelfServiceConstants.ERROR_SEARCH_STORE_ERROR
		storeVo.getErrorCode() == "70701"
		storeVo.isErrorExist() == true
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Code = "+ "70701")
		1*service.logError("SearchStoreService.getSearchInStoreErrorVO() :: Error Message = "+ SelfServiceConstants.ERROR_SEARCH_STORE_ERROR)
	}
}
