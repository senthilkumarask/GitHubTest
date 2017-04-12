package com.bbb.commerce.browse.droplet

import java.io.IOException;
import java.util.List

import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author Velmurugan Moorthy
 *
 * Changes made in Java file 
 * 
 *
 */

public class BBBSetLatLngCookieDropletSpecification extends BBBExtendedSpec{

	private BBBSetLatLngCookieDroplet setLatLngCookieDroplet
	private BBBCatalogTools catalogToolsMock
	private SearchStoreManager searchStoreManagerMock
	HttpServletResponse httpRes =Mock()
	def setup() {
		
		responseMock.getResponse() >> httpRes
		catalogToolsMock = Mock()
		searchStoreManagerMock = Mock()
		setLatLngCookieDroplet = new BBBSetLatLngCookieDroplet([catalogTools:catalogToolsMock , searchStoreManager : searchStoreManagerMock, loggingDebug : true])
		
			
	}
	
	/*
	 * getCookieTimeOut - test cases STARTS
	 * 
	 * Method signature : 
	 * 
	 * public int getCookieTimeOut()
	 * 
	 */
	
	
	/*
	 * Alternative branches covered : 
	 * 
	 */
	
	def "getCookieTimeOut - retrieved cookie timeout from catalog successfully" () {
		
		given : 
		
		def cookieTimeOut = "10"
		List<String> keysList = new ArrayList<>()
		
		keysList.add(cookieTimeOut)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> keysList
		
		expect : 
		
		setLatLngCookieDroplet.getCookieTimeOut() == Integer.parseInt(cookieTimeOut)
		
	}
	
	/*
	 * Alternative branches covered :
	 * #153 - if (keysList != null && keysList.size() > 0) - keyList is empty
	 * 
	 */
	
	def "getCookieTimeOut - No cookie timeout info present in catalog - empty list" () {
		
		given :
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> []
		
		expect :
		
		setLatLngCookieDroplet.getCookieTimeOut() == 0 
		
	}
		
	/*
	 * Alternative branches covered :
	 *
	 */
	
	def "getCookieTimeOut - Exception while getting cookieTimeOut - BBBSystemException" () {
		
		given :
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> { throw new BBBSystemException("") }
		
		expect :
		
		setLatLngCookieDroplet.getCookieTimeOut() == 0

		/*then : 
		not working
		1 * setLatLngCookieDropletSpy.logError("BBBSystemException | Error occurred while getting cookie time out value for lat/lng")*/
		
	}
	
	/*
	 * Alternative branches covered :
	 *
	 */
	
	def "getCookieTimeOut - Exception while getting cookieTimeOut - BBBBusinessException" () {
		
		given :
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> { throw new BBBBusinessException("") }
		
		expect :
		
		setLatLngCookieDroplet.getCookieTimeOut() == 0

		/*then :
		not working
		1 * setLatLngCookieDropletSpy.logError("BBBSystemException | Error occurred while getting cookie time out value for lat/lng")*/
		
	}
	
	
	/*
	 * getCookieTimeOut - test cases ENDS
	 *
	 */
	
	
	/*
	 * service - test cases STARTS
	 *
	 * Method signature :
	 *
	 * public void service (
	 * DynamoHttpServletRequest pRequest,
	 * DynamoHttpServletResponse pResponse
	 * ) 
	 * throws ServletException, IOException
	 *
	 */

	def "serice - Latitude and longitude details are set in the cookie successfully" () {
		
		given : 
		
		def path = "cookie/path"
		def domain = "bedbath-23.sapient.com"
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = "120E 130W"
		def cookie = "130E 140W"
		
		Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)
		//Cookie[] requestCookies = [requestCookie1,requestCookie2]
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> path
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> domain
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> [requestCookie1,requestCookie2]
		
		//setLatLngCookieDroplet.getCookie(requestMock) >> cookie
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
		
		when:
				
		setLatLngCookieDroplet.service(requestMock, responseMock)
		
		then:
		
		1 * requestMock.setParameter("latLngFromPLP",latLang)
		1 * requestMock.serviceLocalParameter(BBBAccountConstants.OPARAM_OUTPUT, requestMock, responseMock)
	}
	
		/*
		 * Alternative branches covered : 
		 * 
		 * #88 - for (Cookie cookie : requestCookies) - both branches
		 * #89 - if (cookie.getName().equals(SelfServiceConstants.LAT_LNG_COOKIE)) - both branches
		 * #90 - if(cookie.getValue() != latLng.toString()) - cookies doesn't match
		 * 
		 */
	
	def "service - Latitude and longitude details are set in the cookie | overriding the existing cookie | loggingDebug enabled (true)" () {
		
		given :
		
		def path = "cookie/path"
		def domain = "bedbath-23.sapient.com"
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = "120E 130W"
		def cookie = "130E 140W"
		
		
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> ["10"]
		
		Cookie requestCookie1 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, cookie)
		//Cookie[] requestCookies = [requestCookie1,requestCookie2]
		
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> path
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> domain
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> [requestCookie1,requestCookie2]
		requestMock.getServerName() >> "22box"
		////1 * //setLatLngCookieDropletSpy.getCookie(requestMock) >> cookie
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
		
			
		when : 
		
		setLatLngCookieDroplet.service(requestMock, responseMock)
		then:
		1 * requestMock.setParameter("latLngFromPLP",latLang)
		1 * requestMock.serviceLocalParameter(BBBAccountConstants.OPARAM_OUTPUT, requestMock, responseMock)
		1*httpRes.addHeader("Set-Cookie", _)		
	}

	/*
	 * Alternative branches
	 */

	def "service - Latitude and longitude details are set in the cookie | " () {
	
	given :
	
	def path = "cookie/path"
	def domain = "bedbath-23.sapient.com"
	def storeIdFromURL = "bedbath.com/storeId=bbb01"
	def siteId = BBBCoreConstants.SITE_BAB_US
	def latLang = "120E 130W"
	def cookie = "130E 140W"
	
	
	Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
	Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, cookie)
	//Cookie[] requestCookies = [requestCookie1,requestCookie2]
	
	
	requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> path
	requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> domain
	requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
	requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
	requestMock.getCookies() >> [requestCookie1,requestCookie2]
	requestMock.getServerName() >> "22box"
	1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL, SelfServiceConstants.COOKIE_LAT_LNG) >> ["10"]
	//1 * //setLatLngCookieDropletSpy.getCookie(requestMock) >> cookie
	
	1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
	1 * requestMock.setParameter("latLngFromPLP",latLang)
	1 * requestMock.serviceLocalParameter(BBBAccountConstants.OPARAM_OUTPUT, requestMock, responseMock)
		
	expect :
	
	setLatLngCookieDroplet.service(requestMock, responseMock)
	}

	/*
	 * Alternative branches covered :
	 * 
	 * #66 - if(BBBUtility.isEmpty(domain)) - true
	 * #69 - if(BBBUtility.isEmpty(path))  - true
	 * #87 - if (requestCookies != null)  - true
	 */
	
	def "service - Cookies found in request object" () {
		
		given :
		
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = "120E 130W"
		def cookie = "130E 140W"
		
		Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)
		Cookie[] requestCookies = [requestCookie1,requestCookie2]
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> ""
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> ""
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> requestCookies
		requestMock.getServerName() >> "22box"
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
		//1 * //setLatLngCookieDropletSpy.getCookie(requestMock) >> cookie
		 
		expect :
		 		
		setLatLngCookieDroplet.service(requestMock, responseMock)
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #87 - if (requestCookies != null)  - false
	 */
	
	def "service - No cookies found in request object" () {
		
		given :
		
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = "120E 130W"
		def cookie = "130E 140W"
		
		Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)
		Cookie[] requestCookies = [requestCookie1,requestCookie2]
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> ""
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> ""
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> null
		requestMock.getServerName() >> "22box"
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
		//1 * //setLatLngCookieDropletSpy.getCookie(requestMock) >> cookie
		1*responseMock.addCookie(_) 
		expect :
				 
		setLatLngCookieDroplet.service(requestMock, responseMock)
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #85 - if (pRequest != null && latLng != null && getCookie(pRequest) != null) - getCookie - null 
	 *
	 */
	
	def "service - Latitude and longitude cookie is null" () {
		
		given :
		
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = "120E 130W"
		def cookie = "130E 140W"
		
		/*BBBSetLatLngCookieDroplet setLatLngCookieDropletSpy = Spy()
		populateLatLangCookieDropletSpy(setLatLngCookieDropletSpy)*/
		Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> ""
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> ""
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> null
		requestMock.getServerName() >> "22box"
		1 * requestMock.setParameter("latLngFromPLP",latLang)
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
		
			setLatLngCookieDroplet.getCookieTimeOut() >> 10
			//setLatLngCookieDroplet.getCookie(requestMock) >> null
		
		
		expect :
				 
		setLatLngCookieDroplet.service(requestMock, responseMock)
	}
	
	
	/*
	 * Alternative branches covered :
	 * 
	 * #121 - else if (pRequest != null && latLng != null && getCookie(pRequest) == null) - latLng - null
	 * 
	 * #121 - pRequest != null  - not possible as the code uses the object without null check
	 * 
	 */
	
	def "service - Co-ordinates are invalid (null)" () {
		
		given :
		
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = null
		def cookie = "130E 140W"
		
		/*Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)*/
		
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> ""
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> ""
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> null
		requestMock.getServerName() >> "22box"
		
		1 * requestMock.setParameter("latLngFromPLP",latLang)
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> latLang
		
			//setLatLngCookieDropletSpy.getCookie(requestMock) >> null
		
		expect :
				 
		setLatLngCookieDroplet.service(requestMock, responseMock)


	}
	
	/*
	 * Alternative branches covered :
	 *
	 */
	
	def "service - Exception while setting cookie in request | Exception" () {
		
		given :
		
		def storeIdFromURL = "bedbath.com/storeId=bbb01"
		def siteId = BBBCoreConstants.SITE_BAB_US
		def latLang = "120E 130W"
		def cookie = "130E 140W"
		
		Cookie requestCookie1 = new Cookie("SiteId", BBBCoreConstants.SITE_BAB_US)
		Cookie requestCookie2 = new Cookie(SelfServiceConstants.LAT_LNG_COOKIE, latLang)
		
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_PATH) >> ""
		requestMock.getParameter(BBBAccountConstants.PARAM_COOKIE_DOMAIN) >> ""
		requestMock.getParameter("storeIdFromURL") >> storeIdFromURL
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
		requestMock.getCookies() >> null
		
		
		1 * searchStoreManagerMock.modifyLatLngCookie(storeIdFromURL, siteId) >> {throw new Exception("Exception while getting co-ordinates")}
		//setLatLngCookieDropletSpy.getCookie(requestMock) >> cookie
		 
		expect :
				 
		setLatLngCookieDroplet.service(requestMock, responseMock)
	}
	
	/*
	 * service - test cases ENDS
	 *
	 */
		
	/*
	 * Data populating methods 
	 */
	

	
	
}
