package com.bbb.commerce.checkout.droplet

import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.vo.BeddingShipAddrVO

import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.OrderHolder
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import spock.lang.specification.BBBExtendedSpec
import java.text.ParseException;


class BBBeddingKitsAddrDropleSpecification extends BBBExtendedSpec {
	def BBBBeddingKitsAddrDroplet bkaDropletObject
	def BBBOrderImpl orderMock = Mock()
	def MutableRepository repositoryMock = Mock()
	def HardgoodShippingGroup hgsMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def RepositoryItem repositoryItemMcok = Mock()
	def OrderHolder cartMock 
	BeddingShipAddrVO beddingShipVO =  new BeddingShipAddrVO ()
	Date date = new Date()
	
	def setup() {
	cartMock = Mock()
	bkaDropletObject = new BBBBeddingKitsAddrDroplet()
    requestMock.resolveName("/atg/commerce/ShoppingCart") >> cartMock
	}
	
	def"service. TC when college id is blanck"() {
		given:
		bkaDropletObject = Spy()
		bkaDropletObject.getCurrentSiteId() >> "USBed"
		//SiteContextManager.getCurrentSiteId() >> "USBed"
		requestMock.getCookieParameter("SchoolCookie") >> ""
		when:
		bkaDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("notBeddingKit", requestMock, responseMock)
	}
	
	def"service. TC to check beddingKit serviceParameter when flagBeddingShip is true "() {
		given:
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		repositoryMock.getItem("USBed", "siteConfiguration") >> repositoryItemMcok
		//orderMock.getCollegeId() >> "co456"
		orderMock.setCollegeId("co456")
		bkaDropletObject.getCurrentSiteId() >> "USBed"
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getShippingGroups() >> [hgsMock]
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate("23-March") 
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> beddingShipVO
		// sets flagBeddingShip 
		catalogToolsMock.validateBeddingAttDate("23-March", _) >> true
		when:
		bkaDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("beddingKit", requestMock, responseMock);
		beddingShipVO.getShippingEndDate() == "23-March"
	}
	
	def"service. TC to add shipping end date in beddingShipAddrVO when  beddingShipAddrVO having empty shipping end date and siteid is 'BedBathCanada' "() {
		given:
		String siteId = "BedBathCanada"
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		
		//orderMock.getCollegeId() >> "co456"
		orderMock.setCollegeId("co456")
		bkaDropletObject.getCurrentSiteId() >> siteId
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate()
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> beddingShipVO
		// siteConfiguration repositoryItem
		1*repositoryMock.getItem("BedBathCanada", "siteConfiguration") >> repositoryItemMcok
		//bkaDropletObject.fetchSiteRepositoryItem(_) >>  repositoryItemMcok
		repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> date
    	expect:
		bkaDropletObject.service(requestMock, responseMock)
		/*then:
		true == true*/
		//1*bkaDropletObject.fetchSiteRepositoryItem(siteId);
	}
	def"service. TC to add shipping end date in beddingShipAddrVO when college id is null"() {
		given:
		String siteId = "BedBathCanada"
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		bkaDropletObject.getCurrentSiteId() >> siteId
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate()
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> beddingShipVO
		// siteConfiguration repositoryItem
		1*repositoryMock.getItem("BedBathCanada", "siteConfiguration") >> repositoryItemMcok
		//bkaDropletObject.fetchSiteRepositoryItem(_) >>  repositoryItemMcok
		repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> date
		expect:
		bkaDropletObject.service(requestMock, responseMock)
		/*then:
		true == true*/
		//1*bkaDropletObject.fetchSiteRepositoryItem(siteId);
	}

	def"service. TC to add shipping end date in beddingShipAddrVO when PackNHold is null"() {
		given:
		String siteId = "BedBathCanada"
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		bkaDropletObject.getCurrentSiteId() >> siteId
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> null
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate()
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> beddingShipVO
		// siteConfiguration repositoryItem
		1*repositoryMock.getItem("BedBathCanada", "siteConfiguration") >> repositoryItemMcok
		//bkaDropletObject.fetchSiteRepositoryItem(_) >>  repositoryItemMcok
		repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> date
		expect:
		bkaDropletObject.service(requestMock, responseMock)
		/*then:
		true == true*/
		//1*bkaDropletObject.fetchSiteRepositoryItem(siteId);
	}
	def"service. TC to check beddingKit serviceParameter when isWebLink is true "() {
		given:
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		repositoryMock.getItem("USBed", "siteConfiguration") >> repositoryItemMcok
		//orderMock.getCollegeId() >> "co456"
		orderMock.setCollegeId("co456")
		orderMock.getShippingGroups() >> [hgsMock]
		bkaDropletObject.getCurrentSiteId() >> "USBed"
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate("25-March")
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> null
		catalogToolsMock.getBeddingShipAddrVO("co456") >> beddingShipVO
		// sets isWebLink
		catalogToolsMock.validateBeddingAttDate("25-March", _) >> true
		
		when:
		bkaDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter("weblinkOrder", requestMock, responseMock);
		beddingShipVO.getShippingEndDate() == "25-March"
	}

	def"service. TC to add shipping end date in beddingShipAddrVO when geted beddingShipAddrVO having empty shipping end date and siteid is not 'BedBathCanada' "() {
		given:
		String siteId = "BedBathUS"
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		
		//orderMock.getCollegeId() >> "co456"
		orderMock.setCollegeId("co456")
		bkaDropletObject.getCurrentSiteId() >> siteId
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate()
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> null
		catalogToolsMock.getBeddingShipAddrVO("co456") >> beddingShipVO
		
		// siteConfiguration repositoryItem
		1*repositoryMock.getItem("BedBathUS", "siteConfiguration") >> repositoryItemMcok
		//bkaDropletObject.fetchSiteRepositoryItem(_) >>  repositoryItemMcok
		repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> date
		expect:
		bkaDropletObject.service(requestMock, responseMock)
		/*then:
		true == true*/
		//1*bkaDropletObject.fetchSiteRepositoryItem(siteId);
	}
	
	def"service. TC when beddingShipAddrVO is null gets from 'getCatalogTools().getBeddingShipAddrVO()' and siteid is not 'BedBathCanada' "() {
		given:
		String siteId = "BedBathIN"
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		
		//orderMock.getCollegeId() >> "co456"
		bkaDropletObject.getCurrentSiteId() >> siteId
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> "co456"
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate()
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> null
		catalogToolsMock.getBeddingShipAddrVO("co456") >> null
		
		// siteConfiguration repositoryItem
		1*repositoryMock.getItem("BedBathIN", "siteConfiguration") >> repositoryItemMcok
		//bkaDropletObject.fetchSiteRepositoryItem(_) >>  repositoryItemMcok
		repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> null
		expect:
		bkaDropletObject.service(requestMock, responseMock)
		/*then:
		true == true*/
		//1*bkaDropletObject.fetchSiteRepositoryItem(siteId);
	}
	
	def"service. To check 'repository exception' wile gettign siteItem , isPackHold parameter in request object is null and shippingGroup also null  "() {
		given:
		String siteId = "BedBathjp"
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		
		bkaDropletObject.getCurrentSiteId() >> siteId
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> "co456"
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> null
		orderMock.getShippingGroups()  >> null
		
		// siteConfiguration repositoryItem
		1*repositoryMock.getItem("BedBathjp", "siteConfiguration") >> {throw new RepositoryException("exception")}
		when:
		bkaDropletObject.service(requestMock, responseMock)
		then:
		1*bkaDropletObject.logError("Catalog API Method Name [getShippingEndDate]: RepositoryException "+ "2003", _)
	}
	
	def"service. To check 'ParseException exception' wile gettign flagBeddingShip flag(getCatalogTools().validateBeddingAttDate method)   "() {
		given:
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		
		//orderMock.getCollegeId() >> "co456"
		orderMock.setCollegeId("co456")
		bkaDropletObject.getCurrentSiteId() >> "USBedbath"
		//seting parameter to request object
		requestMock.getCookieParameter("SchoolCookie") >> null
		requestMock.getObjectParameter("order") >> orderMock
		requestMock.getObjectParameter("isPackHold") >> "notNull"
		orderMock.getShippingGroups() >> [hgsMock]
		//seted beddingShipVO
		beddingShipVO.setShippingEndDate("28-March") 
		catalogToolsMock.validateBedingKitAtt([hgsMock], "co456") >> beddingShipVO
		// sets flagBeddingShip 
		catalogToolsMock.validateBeddingAttDate("28-March", _) >> {throw new ParseException("exception", 2)}
		
		1*repositoryMock.getItem("USBedbath", "siteConfiguration") >> repositoryItemMcok
		
		when:
		bkaDropletObject.service(requestMock, responseMock)
	
		then:
		0*requestMock.serviceParameter("weblinkOrder", requestMock, responseMock);
		0*requestMock.serviceParameter("beddingKit", requestMock, responseMock);
}
	
	///////////////////////////////////getPacknHoldDateMobile //////////////////
	
	def"getPacknHoldDateMobile . TC to check PacknHoldDateMobile "(){
		given:
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		bkaDropletObject.getCurrentSiteId() >> "USBedbath"
		ServletUtil.setCurrentRequest(requestMock)
		// setting the current order
		cartMock.getCurrent() >> orderMock
		orderMock.getShippingGroups() >> [hgsMock]
		
		repositoryMock.getItem("USBedbath", "siteConfiguration") >> repositoryItemMcok
		
		2*repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> date
		expect:
		bkaDropletObject.getPacknHoldDateMobile()
	}
	
	def"getPacknHoldDateMobile . TC to check PacknHoldDateMobile when shipping group list is empty "(){
		given:
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		bkaDropletObject.getCurrentSiteId() >> "USBedbath"
		ServletUtil.setCurrentRequest(requestMock)
		// setting the current order
		cartMock.getCurrent() >> orderMock
		orderMock.getShippingGroups() >> null
		
		repositoryMock.getItem("USBedbath", "siteConfiguration") >> repositoryItemMcok
		
		when:
		bkaDropletObject.getPacknHoldDateMobile()
		then:
		0*repositoryItemMcok.getPropertyValue("packAndHoldEndDate")
	}
	
	def"getPacknHoldDateMobile . TC to check packAndHoldEndDate property value of siteConfiguration is null  "(){
		given:
		bkaDropletObject = Spy()
		//setting the seter method
		bkaDropletObject.setSiteRepository(repositoryMock)
		bkaDropletObject.setCatalogTools(catalogToolsMock)
		bkaDropletObject.getCurrentSiteId() >> "USBedbath"
		ServletUtil.setCurrentRequest(requestMock)
		// setting the current order
		cartMock.getCurrent() >> orderMock
		orderMock.getShippingGroups() >> [hgsMock]
		
		repositoryMock.getItem("USBedbath", "siteConfiguration") >> repositoryItemMcok
		
		repositoryItemMcok.getPropertyValue("packAndHoldEndDate") >> null
		when:
		bkaDropletObject.getPacknHoldDateMobile()
		then:
		1*repositoryItemMcok.getPropertyValue("packAndHoldEndDate")
	}
	
}

