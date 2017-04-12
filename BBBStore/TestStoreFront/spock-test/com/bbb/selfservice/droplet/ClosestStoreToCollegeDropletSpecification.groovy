package com.bbb.selfservice.droplet

import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.SelfServiceConstants
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.bbb.selfservice.manager.SearchStoreManager

import javax.servlet.http.HttpSession

import spock.lang.specification.BBBExtendedSpec;;

class ClosestStoreToCollegeDropletSpecification extends BBBExtendedSpec {

	ClosestStoreToCollegeDroplet droplet

	def setup(){
		droplet = new ClosestStoreToCollegeDroplet()
	}

	def"service, sets the parameters into session object"(){
		
		given:
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "search"
		requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> objStoreDetailsWrapper
		
		objStoreDetailsWrapper.getStoreDetails() >> [sDetail]
		sDetail.getWeekdaysStoreTimings() >> "Mon-Tue-Wed-Thu-Fri: timing"

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "store")
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		1*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,sDetail)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
	}
	
	def"service, when store details list is empty"(){
		
		given:
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		1*requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "search"
		1*requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> objStoreDetailsWrapper
		
		objStoreDetailsWrapper.getStoreDetails() >> []
		/*sDetail.getWeekdaysStoreTimings() >> "Mon-Tue-Wed-Thu-Fri: timing"*/

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "store")
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		0*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,_)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
	}
	
	def"service, when searchStoreByAddress returns null"(){
		
		given:
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		1*requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "search"
		1*requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> null

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "store")
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		0*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,_)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
	}
	
	def"service, when BBBBusinessException is thrown"(){
		
		given:
		droplet =Spy()
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		1*requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "search"
		1*requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> {throw new BBBBusinessException("")}

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "store")
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		0*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,_)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
		1*requestMock.setParameter(BBBCoreConstants.ERRORMESSAGE, _)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
	}

	def"service, when BBBSystemException is thrown"(){
		
		given:
		droplet =Spy()
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		1*requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "search"
		1*requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> {throw new BBBSystemException("")}

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		1*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, "store")
		1*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		0*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,_)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
		1*requestMock.setParameter(BBBCoreConstants.ERRORMESSAGE, _)
		1*requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
	}

	
	def"service, when search type is empty"(){
		
		given:
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		1*requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> "search"
		1*requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> ""
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		/*1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> objStoreDetailsWrapper*/
		
		objStoreDetailsWrapper.getStoreDetails() >> null

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, _)
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		0*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,_)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
	}
	
	def"service, when search string is empty"(){
		
		given:
		SearchStoreManager manager =Mock()
		StoreDetailsWrapper objStoreDetailsWrapper = Mock()
		StoreDetails sDetail =Mock()
		droplet.setSearchStoreManager(manager)
		
		1*requestMock.getParameter(BBBCoreConstants.SEARCHSTRING) >> ""
		1*requestMock.getParameter(BBBCoreConstants.SEARCHTYPE) >> "searchtype"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getParameter(BBBCoreConstants.RADIUS) >> "radius"
		
		/*1*manager.getStoreType("siteId") >> "store"
		1*manager.searchStoreByAddress(_, "searchtype",null) >> objStoreDetailsWrapper*/
		
		objStoreDetailsWrapper.getStoreDetails() >> null

		when:
		droplet.service(requestMock, responseMock)

		then:
		1*sessionMock.setAttribute(SelfServiceConstants.RADIUSMILES,"radius")
		0*sessionMock.setAttribute(BBBCoreConstants.STORE_TYPE, _)
		0*sessionMock.setAttribute(SelfServiceConstants.SEARCH_BASED_ON_COOKIE , BBBCoreConstants.FALSE)
		0*requestMock.setParameter(BBBCoreConstants.CLOSESTSTOREDETAILS,_)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
		0*requestMock.serviceLocalParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
	}
}
