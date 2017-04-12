package com.bbb.selfservice.droplet

import atg.commerce.inventory.InventoryException
import atg.commerce.order.OrderHolder
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.common.BBBStoreInventoryContainer
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean
import com.bbb.selfservice.common.StoreAddressSuggestion
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.common.StoreDetailsWrapper
import com.bbb.selfservice.manager.ScheduleAppointmentManager
import com.bbb.selfservice.manager.SearchStoreManager
import spock.lang.specification.BBBExtendedSpec

class SearchInStoreDropletSpecification extends BBBExtendedSpec {

	def SearchInStoreDroplet sisDroplet
	def SearchStoreManager ssManager = Mock()
	def BBBCatalogTools cToolsMock = Mock()
	def OrderHolder cart = Mock()
	def BBBOrderImpl order = Mock()
	def BBBInventoryManager iManagerMock = Mock()
	def BBBStoreInventoryContainer siContainer = Mock()
	def StoreDetailsWrapper storeWraper = Mock()
	def BBBSessionBean sessionBean = Mock()
	def ScheduleAppointmentManager saManager = Mock()
	
	
	def StoreDetails sDetails = Mock() 
	def StoreDetails sDetails1 = Mock()
	def StoreDetails sDetails2 = Mock()
	def StoreAddressSuggestion saSuggestion = Mock()
	
	def setup(){
		sisDroplet = new SearchInStoreDroplet(searchStoreManager : ssManager, catalogTools:cToolsMock, inventoryManager: iManagerMock, storeInventoryContainer : siContainer, scheduleAppointmentManager : saManager, redirectURL:"usbebBath.com")
	}
	
	def"service method. TC when ScheduleAppointment is false"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> "ap"
		requestMock.getParameter("scheduleAppointment") >> "false"
		3*ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> "3"
		requestMock.getParameter("registryId") >> "rId"
		requestMock.getParameter("changeCurrentStore") >> true
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  sDetails
		// check product availability
		2*cToolsMock.getBopusEligibleStates("BedBathCanada") >> ["newyork","ls"]
		2*cToolsMock.getBopusInEligibleStores("bbs", "BedBathCanada") >> ["s1", "s2","3"]
		
		sDetails.getState() >> "delhi"
		sDetails.getStoreId() >> "storeId"
		
		requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
		cart.getCurrent() >> order
		
		
		2*iManagerMock.getBOPUSProductAvailability(*_) >> ["s1" : 10] >> ["3" : 10,"31": null]
		
		//////////////

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> "p1"
		requestMock.getObjectParameter("pageNumber") >> "2"
		1*ssManager.searchStorePerPage("p1", "2") >> storeWraper
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		(1.._)*storeWraper.getStoreDetails() >> [sDetails1,sDetails2] 
		sDetails1.getStoreId() >> "3"
		sDetails2.getStoreId() >> "31"
		sDetails2.getState() >> "sto"
		
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		1* sessionMock.setAttribute("productId", "pId")
		1* sessionMock.setAttribute("orderedQty", 3)
		1*requestMock.setParameter("StoreDetails", sDetails)
		1*requestMock.serviceLocalParameter("favstoreoutput", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*sDetails1.setBabyCanadaFlag(true)
		1*storeWraper.setBabyCanadaStoreCount(2)
		1*storeWraper.setBedBathCanadaStoreCount(0)
		1*requestMock.setParameter("StoreDetailsWrapper", _)
		1 * requestMock.setParameter('productAvailable', ['s1':10, 'storeId':10])
		1 * requestMock.setParameter('productAvailable', ['3':1010, '31':10])
	}
	
	
	def"service method. TC when productStatusMap is empty"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  sDetails
		// check product availability
		2*cToolsMock.getBopusEligibleStates("BedBathCanada") >> ["newyork","ls"]
		2*cToolsMock.getBopusInEligibleStores("bbs", "BedBathCanada") >> ["s1", "s2","3"]
		
		sDetails.getState() >> "newyork"
		sDetails.getStoreId() >> "storeId"
		
		requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
		cart.getCurrent() >> order
		
		
		2*iManagerMock.getBOPUSProductAvailability(*_) >> [:] >> [:]
		
		
		//////////////

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> "p1"
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		storeWraper.getStoreDetails() >> [sDetails1]
		sDetails1.getStoreId() >> "4"
		
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		1*requestMock.setParameter("StoreDetails", sDetails)
		0 * requestMock.setParameter('productAvailable', _)
		0*requestMock.serviceLocalParameter("favstoreoutput", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*sDetails1.setBabyCanadaFlag(false)
		1*storeWraper.setBabyCanadaStoreCount(0)
		1*storeWraper.setBedBathCanadaStoreCount(1)
		1*requestMock.setParameter("StoreDetailsWrapper", _)
	}
	
	def"service method. TC for inventory exception"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  sDetails
		// check product availability
		2*cToolsMock.getBopusEligibleStates("BedBathCanada") >> ["newyork","ls"] >> null
		2*cToolsMock.getBopusInEligibleStores("bbs", "BedBathCanada") >> null
		
		sDetails.getState() >> "newyork"
		sDetails.getStoreId() >> "storeId"
		
		requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
		cart.getCurrent() >> order
		
		
		2*iManagerMock.getBOPUSProductAvailability(*_) >> {throw new InventoryException("inventoryexception")}
		
		
		//////////////
		setSessionAttributes()
		
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		storeWraper.getStoreDetails() >> [sDetails1]
		sDetails1.getStoreId() >> "4"
		//iManagerMock.getBOPUSProductAvailability("BedBathCanada", "pId", [3],_, "storeToStore", siContainer, false, "rId", true , false) >> ["s1" : 10]
		
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		1*requestMock.setParameter("StoreDetails", sDetails)
		0 * requestMock.setParameter('productAvailable', _)
		0*requestMock.serviceLocalParameter("favstoreoutput", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*sDetails1.setBabyCanadaFlag(false)
		1*storeWraper.setBabyCanadaStoreCount(0)
		1*storeWraper.setBedBathCanadaStoreCount(1)
		1*requestMock.setParameter("StoreDetailsWrapper", _)
		1*requestMock.setParameter("errorMessage", _)
		2*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", "inventoryexception")
	}
	
	def"service method. TC for BBBSystemException while getting product availability"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  sDetails
		// check product availability
		2*cToolsMock.getBopusEligibleStates("BedBathCanada") >> {throw new BBBSystemException("systemexception")}
		
		
		//////////////

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		storeWraper.getStoreDetails() >> [sDetails1]
		sDetails1.getStoreId() >> "4"
		
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		1*requestMock.setParameter("StoreDetails", sDetails)
		0 * requestMock.setParameter('productAvailable', _)
		0*requestMock.serviceLocalParameter("favstoreoutput", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*sDetails1.setBabyCanadaFlag(false)
		1*storeWraper.setBabyCanadaStoreCount(0)
		1*storeWraper.setBedBathCanadaStoreCount(1)
		1*requestMock.setParameter("StoreDetailsWrapper", _)
		1*requestMock.setParameter("errorMessage", _)
		2*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", "systemexception")
	}
	
	def"service method. TC for BBBBusinessException while getting product availability"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  sDetails
		// check product availability
		2*cToolsMock.getBopusEligibleStates("BedBathCanada") >> {throw new BBBBusinessException("businessException")}
		
		
		//////////////

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		storeWraper.getStoreDetails() >> [sDetails1]
		sDetails1.getStoreId() >> "4"
		
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		1*requestMock.setParameter("StoreDetails", sDetails)
		0 * requestMock.setParameter('productAvailable', _)
		0*requestMock.serviceLocalParameter("favstoreoutput", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*sDetails1.setBabyCanadaFlag(false)
		1*storeWraper.setBabyCanadaStoreCount(0)
		1*storeWraper.setBedBathCanadaStoreCount(1)
		1*requestMock.setParameter("StoreDetailsWrapper", _)
		1*requestMock.setParameter("errorMessage", _)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", "businessException")
		1*requestMock.serviceLocalParameter("favstoreerror", requestMock, responseMock)
	}
	
	def"service method. TC for BBBBusinessException while search store by id"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  {throw new BBBBusinessException("businessException")}
		// check product availability
		
		
		//////////////

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		storeWraper.getStoreDetails() >> null
		
		storeWraper.getStoreAddressSuggestion() >> [saSuggestion]
		sDetails1.getStoreId() >> "4"
		
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.serviceLocalParameter("favstoreempty", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*requestMock.setParameter("StoreDetailsWrapper", _)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", _)
		1*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock);

	}
	
	def"service method. TC for BBBSystemException while search store by id"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> "storeId"
		
		1*ssManager.searchStoreById("storeId","BedBathCanada","bbs") >>  {throw new BBBSystemException("systemException")}

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> null
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.serviceLocalParameter("favstoreempty", requestMock, responseMock)
		1*requestMock.setParameter("miles", 2)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", _)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)

	}
	
	def"service method. TC store id null"(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		
		storeWraper.getStoreDetails() >> [sDetails1,sDetails]
		sDetails1.getStoreId() >> "2"
		sDetails.getStoreId() >> "3"
		1*cToolsMock.getBopusEligibleStates("BedBathCanada") >> {throw new BBBBusinessException("businessException")}
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.setParameter("miles", 2)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", _)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		1*sDetails.setBabyCanadaFlag(true)
		1*sDetails1.setBabyCanadaFlag(false)
	}
	
	def"service method. TC when store details is null and babyCA parameter of session is null "(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		
		storeWraper.getStoreDetails() >> null
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*sDetails.setBabyCanadaFlag(true)
		0*sDetails1.setBabyCanadaFlag(false)
	}
	
	def"service method. TC when store wraper is null gets from search store by address "(){
		given:
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		

		setSessionAttributes()
		
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> null
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*sDetails.setBabyCanadaFlag(true)
		0*sDetails1.setBabyCanadaFlag(false)
	}
	
	def"service method. TC when site id is not BedBathCanada and store details is empty" (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		

		setSessionAttributes()

		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		storeWraper.getStoreDetails() >> []
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*sDetails.setBabyCanadaFlag(true)
		0*sDetails1.setBabyCanadaFlag(false)
	}
	
	def"service method. TC for BBBBusinessException while getting store wraper " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		

		setSessionAttributes()

		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> {throw new BBBBusinessException("business exception")}
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*sDetails.setBabyCanadaFlag(true)
		0*sDetails1.setBabyCanadaFlag(false)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. TC for BBBSystemException while getting store wraper " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
	
		setSessionAttributes()
		
	
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> {throw new BBBSystemException("business exception")}
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0* sessionMock.setAttribute("productId", "pId")
		0* sessionMock.setAttribute("orderedQty", 3)
		0 * requestMock.setParameter('productAvailable', _)
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*sDetails.setBabyCanadaFlag(true)
		0*sDetails1.setBabyCanadaFlag(false)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. TC for search String is empty " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> ""
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> 2
		sessionMock.getAttribute("productId") >> "pid"
		sessionMock.getAttribute("orderedQty") >> 2
		
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		1*responseMock.setHeader("BBB-ajax-redirect-url", _)
	}
	
	def"service method. TC for search type is empty " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> ""
		sessionMock.getAttribute("type") >> ""
		sessionMock.getAttribute("miles") >> 2
		sessionMock.getAttribute("productId") >> "pid"
		sessionMock.getAttribute("orderedQty") >> 2
		2*requestMock.getContextPath() >> "/store"
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*responseMock.setHeader("BBB-ajax-redirect-url", "/store")
	}
	
	def"service method. TC orderedQty is null " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> "pId"
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> ""
		sessionMock.getAttribute("type") >> ""
		sessionMock.getAttribute("miles") >> 2
		sessionMock.getAttribute("productId") >> "pid"
		sessionMock.getAttribute("orderedQty") >> null
		2*requestMock.getContextPath() >> "/store"
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles", 2)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*responseMock.setHeader("BBB-ajax-redirect-url", "/store")
	}
	
	def"service method. TC productId is null " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> ""
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> ""
		sessionMock.getAttribute("type") >> ""
		sessionMock.getAttribute("miles") >> null
		sessionMock.getAttribute("productId") >> null
		sessionMock.getAttribute("orderedQty") >> null
		2*requestMock.getContextPath() >> "/store"
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles", 0)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*responseMock.setHeader("BBB-ajax-redirect-url", "/store")
	}
	
	def"service method. TC when searchBasedOn is not cookie " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> ""
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		requestMock.getParameter("searchBasedOn") >> "notcookie"
		2*requestMock.getContextPath() >> "/store"
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("miles", 0)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		1*responseMock.setHeader("BBB-ajax-redirect-url", _)
	}
	
	def"service method. TC when searchBasedOn is empty " (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("productId") >> ""
		requestMock.getParameter("orderedQty") >> null
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("changeCurrentStore") >> null
		requestMock.getObjectParameter("storeId") >> null
		
		
		requestMock.getParameter("searchBasedOn") >> ""
		2*requestMock.getContextPath() >> "/store"
	
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("miles", 0)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		1*responseMock.setHeader("BBB-ajax-redirect-url", _)
	}
	
	def"service method. TC for BBBSystemException  while getting store type" (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		1*ssManager.getStoreType("BedBathUS") >> {throw new BBBSystemException("systemException")}
			
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("miles", 0)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*responseMock.setHeader("BBB-ajax-redirect-url", _)
		1*requestMock.setParameter("errorMessage", _)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. TC for BBBBusinessException  while getting store type" (){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> null
		requestMock.getParameter("scheduleAppointment") >> null
		1*ssManager.getStoreType("BedBathUS") >> {throw new BBBBusinessException("systemException")}
			
		when:
			sisDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("miles", 0)
		0*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock)
		0*responseMock.setHeader("BBB-ajax-redirect-url", _)
		1*requestMock.setParameter("errorMessage", _)
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	/************************************************* scheduleAppointment is true *************************************/
	
	def"service method.TC when scheduleAppointment is true"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> 2
		requestMock.getObjectParameter("pageKey") >> "p1"
		requestMock.getObjectParameter("pageNumber") >> "2"
		1*ssManager.searchStorePerPage("p1", "2") >> storeWraper
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "nfdn"
		(1.._)*storeWraper.getStoreDetails() >> [sDetails1,sDetails]
		sDetails1.getStoreId() >> "3"
		sDetails.getStoreId() >> "4"

		1*saManager.checkAppointmentAvailability(_, "ap") >> ["s" : true]
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles", 2)
		1*sDetails1.setBabyCanadaFlag(true)
		1*sDetails.setBabyCanadaFlag(false)
		1*storeWraper.setBabyCanadaStoreCount(1)
		1*storeWraper.setBedBathCanadaStoreCount(1)
		1*requestMock.setParameter("StoreDetailsWrapper", storeWraper)
		1*requestMock.setParameter("appointmentAvailableMap", ["s" : true]);
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)

	}
	
	def"service method.TC for BBBSystemException while getting appointmentAvailableMap"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> "p1"
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		storeWraper.getStoreDetails() >> [sDetails1,sDetails]
		sDetails1.getStoreId() >> "3"
		sDetails.getStoreId() >> "4"

		1*saManager.checkAppointmentAvailability(_, "ap") >> {throw new BBBSystemException("exception")}
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*sDetails1.setBabyCanadaFlag(true)
		0*sDetails.setBabyCanadaFlag(false)
		1*storeWraper.setBabyCanadaStoreCount(1)
		1*storeWraper.setBedBathCanadaStoreCount(1)
		1*requestMock.setParameter("StoreDetailsWrapper", storeWraper)
		0*requestMock.setParameter("appointmentAvailableMap", _);
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)

	}
	
	def"service method.TC when store details is null and  BabyCA parameter is null"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		storeWraper.getStoreDetails() >> null

		storeWraper.getStoreAddressSuggestion() >> [saSuggestion]
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*sDetails1.setBabyCanadaFlag(true)
		0*sDetails.setBabyCanadaFlag(false)
	    1*requestMock.setParameter("StoreDetailsWrapper", storeWraper)
		0*requestMock.setParameter("appointmentAvailableMap", _);
		0*saManager.checkAppointmentAvailability(_, "ap")
		1*requestMock.serviceLocalParameter("addressSuggestion", requestMock, responseMock);


	}
	
	def"service method.TC when StoreDetailsWrapper is null and  BabyCA parameter is also null"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> null
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> null
		requestMock.getContextPath() >> "/store"

		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*sDetails1.setBabyCanadaFlag(true)
		0*sDetails.setBabyCanadaFlag(false)
		0*requestMock.setParameter("StoreDetailsWrapper", _)
		0*requestMock.setParameter("appointmentAvailableMap", _);
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		0*storeWraper.getStoreAddressSuggestion()
		1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)

	}
	
	def"service method.TC when storeDetails is null and  BabyCA parameter is not null"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "ha"
		requestMock.getContextPath() >> "/store"
		storeWraper.getStoreDetails() >> null
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*sDetails1.setBabyCanadaFlag(true)
		0*sDetails.setBabyCanadaFlag(false)
		0*requestMock.setParameter("StoreDetailsWrapper", _)
		0*requestMock.setParameter("appointmentAvailableMap", _);
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)


	}
	
	def"service method.TC when storeDetailsWraper is null and  BabyCA parameter is not null"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> null
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "ha"
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)


	}
	
	def"service method.TC when site id is not BedBathCanada "(){
		given:
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("appointmentType") >> "ap"
		requestMock.getParameter("scheduleAppointment") >> "true"
		ssManager.getStoreType("BedBathUS") >> "bbs"
		
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> storeWraper
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "ha"
		storeWraper.getStoreDetails() >> []
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)


	}
	
	def"service method.TC for BBBBusinessException while getting store details wraper"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> {throw new BBBBusinessException("exception")}
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "ha"
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", _ )


	}
	
	def"service method.TC for BBBSystemException while getting store details wraper"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchBasedOn") >> "cookie"
		sessionMock.getAttribute("status") >> "searchString"
		sessionMock.getAttribute("type") >> "searchType"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		1*ssManager.searchStoreByAddress("searchString", "searchType", null) >> {throw new BBBSystemException("exception")}
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getBabyCA() >> "ha"
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("miles",0)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("errorMessage", _ )


	}
	
	def"service method.TC when searchBasedOn is not cookie"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchString")>> ""
		requestMock.getParameter("searchType")>> "searchType"
		
		requestMock.getParameter("searchBasedOn") >> "notcookie"
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		requestMock.getContextPath() >> "/store"
		
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("miles",0)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
        1 * responseMock.setHeader('BBB-ajax-redirect-url', '/storeusbebBath.com')		
		0*ssManager.searchStoreByAddress(_, _, null)
		1*requestMock.setParameter("inputSearchString", "")

	}
	
	def"service method.TC when searchBasedOn is empty"(){
		given:
		setCommonFields()
		requestMock.getParameter("searchString")>> ""
		requestMock.getParameter("searchType")>> ""
		
		requestMock.getParameter("searchBasedOn") >> ""
		sessionMock.getAttribute("miles") >> null
		requestMock.getObjectParameter("pageKey") >> ""
		requestMock.getObjectParameter("pageNumber") >> ""
		requestMock.getContextPath() >> null
		
		
		when:
		sisDroplet.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("miles",0)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*saManager.checkAppointmentAvailability(_, "ap")
		1 * responseMock.setHeader('BBB-ajax-redirect-url', 'usbebBath.com')
		0*ssManager.searchStoreByAddress(_, _, null)
		1*requestMock.setParameter("inputSearchString", "")

	}
	
	 private void setCommonFields() {
		 
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("appointmentType") >> "ap"
		requestMock.getParameter("scheduleAppointment") >> "true"
		ssManager.getStoreType("BedBathCanada") >> "bbs"
		

	}
	 
	 private setSessionAttributes(){
		 requestMock.getParameter("searchBasedOn") >> "cookie"
		 sessionMock.getAttribute("status") >> "searchString"
		 sessionMock.getAttribute("type") >> "searchType"
		 sessionMock.getAttribute("miles") >> 2
		 sessionMock.getAttribute("productId") >> "pid"
		 sessionMock.getAttribute("orderedQty") >> 2
	 
	 }
	
}
