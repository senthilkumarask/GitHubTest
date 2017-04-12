package com.bbb.commerce.browse.droplet

import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.common.BBBStoreInventoryContainer
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.utils.BBBUtility;

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class BBBFindStoreOnPDPDropletSpecification extends BBBExtendedSpec {
	
	BBBFindStoreOnPDPDroplet testObj
	SearchStoreManager searchStoreManagerMock = Mock()
	BBBInventoryManager inventoryManagerMock = Mock()
	BBBStoreInventoryContainer storeInventoryContainerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	Profile atgProfileMock = Mock() 
	BBBSessionBean sessionBeanMock = Mock()
	Cookie cookieMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new BBBFindStoreOnPDPDroplet(inventoryManager:inventoryManagerMock,storeInventoryContainer:storeInventoryContainerMock,
			searchStoreManager:searchStoreManagerMock,catalogTools:catalogToolsMock,lblTxtTemplateManager:lblTxtTemplateManagerMock,
			atgProfile:atgProfileMock,sessionBean:sessionBeanMock)
		
		responseMock.getResponse() >> responseMock
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) ////
	
	def"service method.This TC is the Happy flow of the service method"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100"; 
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false"; 
			String favStoreId = "fav12345"; String plpCall = "true"; String latLngFromPLP = "plp1,plp2"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getServerName() >> "BedBathandBeyond"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates("plp2", "plp1", requestMock, responseMock, TRUE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage" 
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> ["150"]
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "errorMessage" )
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when latLngFromPLP has value and isReqForFavStore is false"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "plp1,plp2"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> "lat=12107,long=12421"
			1 * requestMock.getServerName() >> "BedBathandBeyond"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates("plp2", "plp1", requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "We're sorry!  A system error occurred. Please try again or contact us (1-800-462-3966) to place order" )
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when latLngFromPLP has value and isReqForFavStore is false but no exception thrown"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "plp1,plp2"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> "lat=12107,long=12421"
			1 * requestMock.getServerName() >> "BedBathandBeyond"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates("plp2", "plp1", requestMock, responseMock, FALSE)
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> []
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when latLngFromPLP has value and isReqForFavStore is true"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "plp1,plp2"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId,requestMock, responseMock, "plp2", "plp1", FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "There is some error in fetching avaibility from your preferred store.")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when latLngFromPLP has value and isReqForFavStore is true and errormessage is empty"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "plp1,plp2"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId,requestMock, responseMock, "plp2", "plp1", FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when latLngFromPLP has value and isReqForFavStore is true but no exception thrown"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "plp1,plp2"
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId,requestMock, responseMock, "plp2", "plp1", FALSE)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and latLngCookie is not empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId, requestMock, responseMock, "plp", "pdp", FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "There is some error in fetching avaibility from your preferred store.")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and latLngCookie is not empty and errMsg is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId, requestMock, responseMock, "plp", "pdp", FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and latLngCookie is not empty but no exception thrown"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId, requestMock, responseMock, "plp", "pdp", FALSE)
		
		expect:
			testObj.service(requestMock, responseMock)
			
	}
	
	def"service method.This TC is when searchString is empty and latLngCookie is not empty and isReqForFavStore is false"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates("plp", "pdp", requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES, BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage"
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> []
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "errorMessage")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and latLngCookie is not empty and isReqForFavStore is false, errMsg is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates("plp", "pdp", requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES, BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> []
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "We're sorry!  A system error occurred. Please try again or contact us (1-800-462-3966) to place order")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and latLngCookie is not empty and isReqForFavStore is false but no exception thrown"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates("plp", "pdp", requestMock, responseMock, FALSE)
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> []
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is not empty"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getStoresBySearchString(searchString, requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES, BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage"
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "errorMessage")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is not empty and errMsg is empty"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getStoresBySearchString(searchString, requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES, BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "We're sorry!  A system error occurred. Please try again or contact us (1-800-462-3966) to place order")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is not empty but no exception thrown"(){
		given:
			String searchString = "glass"; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = "pdp,plp"
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> "pdp,plp"
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getStoresBySearchString(searchString, requestMock, responseMock, FALSE)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and storeId has value and isReqForFavStore is false"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByStoreId(storeId, requestMock,	responseMock, favStoreId, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,	BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage"
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "errorMessage")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and storeId has value and isReqForFavStore is false and errMsg is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByStoreId(storeId, requestMock,	responseMock, favStoreId, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES,	BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "We're sorry!  A system error occurred. Please try again or contact us (1-800-462-3966) to place order")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when searchString is empty and storeId has value and isReqForFavStore is false but no exception thrown"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByStoreId(storeId, requestMock,	responseMock, favStoreId, FALSE)
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		expect:
			testObj.service(requestMock, responseMock)
		
	}
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is false"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = ""; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates(latitude,longitude, requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES, BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage" 
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "errorMessage")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is false and errMsg is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = ""; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates(latitude,longitude, requestMock, responseMock, FALSE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_ALL_STORES, BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, "We're sorry!  A system error occurred. Please try again or contact us (1-800-462-3966) to place order")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_ALL_STORES, requestMock, responseMock)
	}
	
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is false but no exception thrown"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = ""; String isReqForFavStore = "false";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getNearestStoresByCoordinates(latitude,longitude, requestMock, responseMock, FALSE)
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is true and callFromPLP is false"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByStoreId(siteId, requestMock, responseMock,favStoreId) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> "erorMessage"
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "There is some error in fetching avaibility from your preferred store.")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is true and callFromPLP is true"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = ""; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByStoreIdForPLP(siteId, requestMock, responseMock,favStoreId) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE,BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is true and callFromPLP is true but no exception thrown"(){
		given:
			String searchString = ""; String latitude = ""; String longitude = ""; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByStoreIdForPLP(siteId, requestMock, responseMock,favStoreId)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when storeId is empty and isReqForFavStore is true and callFromPLP is false but no exception thrown"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = "fav12345"; String plpCall = "false"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByStoreId(siteId, requestMock, responseMock,favStoreId)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when favStoreId is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = ""; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId, requestMock,responseMock, latitude, longitude, TRUE) >> {throw new Exception("Mock for Exception")} 
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE, BBBCoreConstants.DEFAULT_LOCALE, null) >> "errorMessage"
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "There is some error in fetching avaibility from your preferred store.")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when favStoreId is empty and errMsg is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = ""; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId, requestMock,responseMock, latitude, longitude, TRUE) >> {throw new Exception("Mock for Exception")}
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBCoreConstants.ERROR_IN_VIEW_FAV_STORE, BBBCoreConstants.DEFAULT_LOCALE, null) >> ""
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, "")
			1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_VIEW_FAV_STORES, requestMock, responseMock)
	}
	
	def"service method.This TC is when favStoreId is empty but no Exception thrown"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = "74.0478"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = "s12345"; String isReqForFavStore = "true";
			String favStoreId = ""; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * searchStoreManagerMock.getFavStoreByCoordinates(siteId, requestMock,responseMock, latitude, longitude, TRUE)
			
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method.This TC is when isReqForFavStore is false"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = ""; String radius = "100";
			String siteId = "BedBathUS";	String storeId = ""; String isReqForFavStore = "false";
			String favStoreId = ""; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getServerName() >> "BedBathandBeyond.com"
			1 * responseMock.addHeader('Set-Cookie',_)
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_FIND_IN_YOUR_STORE, BBBCoreConstants.DEFAULT_LOCALE, null) >> "lblFindStore"
			
			//getCookieTimeOut Public method coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.EMPTY_INPUT, "lblFindStore")
			1 * requestMock.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when isReqForFavStore is false and when longitude is empty"(){
		given:
			String searchString = ""; String latitude = "40.888"; String longitude = ""; String radius = "100";
			String siteId = "BedBathUS";	String storeId = ""; String isReqForFavStore = "true";
			String favStoreId = ""; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_FIND_IN_YOUR_STORE, BBBCoreConstants.DEFAULT_LOCALE, null) >> "lblFindStore"
		
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.EMPTY_INPUT, "lblFindStore")
			1 * requestMock.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when isReqForFavStore is false and when latitude is empty"(){
		given:
			String searchString = ""; String latitude = ""; String longitude = "40.888"; String radius = "100";
			String siteId = "BedBathUS";	String storeId = ""; String isReqForFavStore = "true";
			String favStoreId = ""; String plpCall = "true"; String latLngFromPLP = ""
			
			this.populateReqParameters(requestMock, searchString, latitude, longitude, radius, siteId, storeId, isReqForFavStore, favStoreId, plpCall, latLngFromPLP)
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "latLngCookie"
			cookieMock.getValue() >> ""
			1 * requestMock.getHeader("X-Akamai-Edgescape") >> null
			1 * requestMock.getSession().setAttribute(SelfServiceConstants.RADIUSMILES, radius)
			1 * lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_FIND_IN_YOUR_STORE, BBBCoreConstants.DEFAULT_LOCALE, null) >> "lblFindStore"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.EMPTY_INPUT, "lblFindStore")
			1 * requestMock.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, requestMock, responseMock)
	}
	

	private populateReqParameters(DynamoHttpServletRequest requestMock, String searchString, String latitude, String longitude, String radius, String siteId, String storeId, String isReqForFavStore, String favStoreId, String plpCall, String latLngFromPLP) {
		1 * requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> searchString
		1 * requestMock.getParameter(SelfServiceConstants.LATITUDE) >> latitude
		1 * requestMock.getParameter(SelfServiceConstants.LONGITUDE) >> longitude
		1 * requestMock.getParameter(BBBCoreConstants.RADIUS) >> radius
		1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		1 * requestMock.getParameter(BBBCoreConstants.STORE_ID) >> storeId
		1 * requestMock.getParameter(BBBCoreConstants.FAVOURITE_STORE) >> isReqForFavStore
		1 * requestMock.getParameter("favStoreId") >> favStoreId
		1 * requestMock.getParameter("localStoreCallFromPLP") >> plpCall
		1 * requestMock.getParameter("latLngFromPLP") >> latLngFromPLP
	}
	
	/////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for getCookieTimeOut starts////////////////////////////////////////
	//Signature : public int getCookieTimeOut() ////
	
	def"getCookieTimeOut. This TC is when BBBSystemException thrown in getCookieTimeOut"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.getCookieTimeOut()
		then:
			1 * testObj.logError("BBBSystemException | Error occurred while getting cookie time out value for radius")
	}
	
	def"getCookieTimeOut. This TC is when BBBBusinessException thrown in getCookieTimeOut"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.getCookieTimeOut()
		then:
			1 * testObj.logError("BBBBusinessException | Error occurred while getting cookie time out value for radius")
	}
	
	/////////////////////////////////TCs for getCookieTimeOut ends////////////////////////////////////////
	
	
}
