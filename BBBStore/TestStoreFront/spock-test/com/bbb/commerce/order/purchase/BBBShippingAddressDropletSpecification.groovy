package com.bbb.commerce.order.purchase

import atg.commerce.order.HardgoodShippingGroup
import atg.core.util.Address
import atg.userprofiling.Profile
import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressVO
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.vo.BeddingShipAddrVO
import java.text.ParseException;
import spock.lang.specification.BBBExtendedSpec

class BBBShippingAddressDropletSpecification extends BBBExtendedSpec {
	def BBBShippingAddressDroplet saDroplet
	BBBAddressContainer addressContainer = new BBBAddressContainer() 
	def Profile profileMcok = Mock()
	def BBBOrder order = Mock()
	def HardgoodShippingGroup hgsg = Mock()
	def HardgoodShippingGroup hgsg1 = Mock()
	
	def BBBCheckoutManager cManager = Mock()
	def BBBShippingGroupManager sgManager = Mock()
	def BBBAddressAPI addressTools = Mock()
	
	BBBAddressVO defaultSAddress = new BBBAddressVO()
	BBBAddressVO defaultBAddress = new BBBAddressVO()
	
	BBBAddressImpl shippingGroupAddress = new BBBAddressImpl()
	

	BBBAddressImpl registryAddress = new BBBAddressImpl()

	BBBAddressVO pShippingAddress = new BBBAddressVO()
	
	BBBCatalogTools cTools = Mock()
		
	Address sAddress = new Address()
	StateVO sVO = new StateVO()
	StateVO sVO1 = new StateVO()
	StateVO sVO2 = new StateVO()
	
	
	BeddingShipAddrVO bSvo = new BeddingShipAddrVO()
	
	
	def setup(){
		saDroplet = new BBBShippingAddressDroplet(checkoutMgr : cManager, catalogTools : cTools, order : order)
	}
	
	def"service.TC to set the shipping into to request object"(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> "s"
			requestMock.getObjectParameter("shippingGroup") >> null
			
			cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> [hgsg]
			
			hgsg.getShippingAddress() >> sAddress
			sAddress.setAddress1("address")
			hgsg.getId() >> "sg1"
			order.getSiteId() >> siteId
			
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> defaultSAddress
			defaultSAddress.setAddress1("daddress1") 
			
			1*addressTools.getDefaultBillingAddress(profileMcok, siteId) >> defaultBAddress
			defaultBAddress.setAddress1("daddress1") 
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> [sVO, sVO1, sVO2]
			
			sVO.setStateName("s11")
			sVO1.setStateName("s1")
			sVO2.setStateName("s111")
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> [registryAddress]
			//addAddressToContainer
			registryAddress.setIdentifier("r1")
			registryAddress.setOwnerId("rw1")
			registryAddress.setAddress2("saddre2")
			registryAddress.setMiddleName("smidleName")

			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			// end addAddressToConainer
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >> [pShippingAddress]
			pShippingAddress.setIdentifier("sr1")
			pShippingAddress.setOwnerId("rw1")
			pShippingAddress.setAddress1("address")
			
			sAddress.setAddress2("")
			sAddress.setMiddleName("")
			
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> "ddf"
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			order.getShippingGroups() >> [hgsg1]
			1*cTools.validateBedingKitAtt([hgsg1], _) >> bSvo
			
			bSvo.setShippingEndDate("12/02")
			
			1*cTools.validateBeddingAttDate(_, _) >> true
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("shippingGroup", hgsg)
			1 * requestMock.setParameter("defaultShippingAddress", defaultSAddress)
			1 * requestMock.setParameter("states", _)
			1 * requestMock.setParameter("collegeAddress", bSvo)
			1 * requestMock.setParameter("defaultBillingAddress", defaultBAddress)
			defaultBAddress.getIsNonPOBoxAddress() == true
			defaultSAddress.getIsNonPOBoxAddress() == true
			addressContainer.getAddressMap().get("r1") == registryAddress
			addressContainer.getAddressMap().get("sr1") == pShippingAddress
			addressContainer.getAddressMap().get("sgaIde") == shippingGroupAddress
			
			pShippingAddress.getAddress2() == ""
			pShippingAddress.getMiddleName() == ""
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			
	}
	
	def"service.TC to set the shipping into  request object when stateVO is null "(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> "s"
			requestMock.getObjectParameter("shippingGroup") >> null
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> [hgsg]
			
			hgsg.getShippingAddress() >> sAddress
			sAddress.setAddress1("address")
			hgsg.getId() >> "sg1"
			order.getSiteId() >> siteId
			
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> defaultSAddress
			defaultSAddress.setAddress1(null)
			
			1*addressTools.getDefaultBillingAddress(profileMcok, siteId) >> defaultBAddress
			defaultBAddress.setAddress1(null)
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> null
			
			sVO.setStateName("s11")
			sVO1.setStateName("s1")
			sVO2.setStateName("s111")
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> [registryAddress]
			//addAddressToContainer
			registryAddress.setIdentifier("r1")
			registryAddress.setOwnerId("rw1")
			registryAddress.setAddress2("saddre2")
			registryAddress.setMiddleName("smidleName")
			registryAddress.setAddress1("address")
			

			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			// end addAddressToConainer
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >> [pShippingAddress]
			pShippingAddress.setIdentifier("sr1")
			pShippingAddress.setOwnerId("rw1")
			
			
				
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> "ddf"
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			order.getShippingGroups() >> [hgsg1]
			1*cTools.validateBedingKitAtt([hgsg1], _) >> bSvo
			
			bSvo.setShippingEndDate("")
			
			//1*cTools.validateBeddingAttDate(_, _) >> true
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("shippingGroup", hgsg)
			1 * requestMock.setParameter("defaultShippingAddress", null)
			1 * requestMock.setParameter("states", _)
			0 * requestMock.setParameter("collegeAddress", bSvo)
			1 * requestMock.setParameter("defaultBillingAddress", null)
			addressContainer.getAddressMap().get("r1") == registryAddress
			addressContainer.getAddressMap().get("sr1") == pShippingAddress
			addressContainer.getAddressMap().get("sgaIde") == shippingGroupAddress
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			0 * cTools.validateBeddingAttDate(_, _)
			
	}
	
	def"service.TC to set the shipping into  request object when default billing address is null "(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> "s"
			requestMock.getObjectParameter("shippingGroup") >> hgsg
			
			//1*cManager.getShippingGroupManager() >> sgManager
			//1*sgManager.getHardgoodShippingGroups(order) >> [hgsg]
			
			hgsg.getShippingAddress() >> sAddress
			sAddress.setAddress1("")
			hgsg.getId() >> "sg1"
			order.getSiteId() >> siteId
			
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> defaultSAddress
			defaultSAddress.setAddress1(null)
			
			1*addressTools.getDefaultBillingAddress(profileMcok, siteId) >> null
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> null
			
			sVO.setStateName("s11")
			sVO1.setStateName("s1")
			sVO2.setStateName("s111")
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> [registryAddress]
			//addAddressToContainer
			registryAddress.setIdentifier("r1")
			registryAddress.setOwnerId("rw1")
			registryAddress.setAddress2("saddre2")
			registryAddress.setMiddleName("smidleName")
			registryAddress.setAddress1("address")
			

			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			// end addAddressToConainer
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >> [pShippingAddress]
			pShippingAddress.setIdentifier("sr1")
			pShippingAddress.setOwnerId("rw1")
			pShippingAddress.setAddress1("address")
			
				
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> "ddf"
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			order.getShippingGroups() >> [hgsg1]
			1*cTools.validateBedingKitAtt([hgsg1], _) >> null
			
			bSvo.setShippingEndDate("")
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("shippingGroup", hgsg)
			1 * requestMock.setParameter("defaultShippingAddress", null)
			1 * requestMock.setParameter("states", _)
			0 * requestMock.setParameter("collegeAddress", bSvo)
			1 * requestMock.setParameter("defaultBillingAddress", null)
			addressContainer.getAddressMap().get("r1") == registryAddress
			addressContainer.getAddressMap().get("sr1") == pShippingAddress
			addressContainer.getAddressMap().get("sgaIde") == shippingGroupAddress
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			0 * cTools.validateBeddingAttDate(_, _)
			
	}
	
	def"service.TC for BBSystemException while getting default shipping group "(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> "s"
			requestMock.getObjectParameter("shippingGroup") >> null
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> []
			
			hgsg.getShippingAddress() >> sAddress
			sAddress.setAddress1("address")
			hgsg.getId() >> "sg1"
			order.getSiteId() >> siteId
			
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> {throw new BBBSystemException("exception")} >> defaultSAddress 
			defaultSAddress.setAddress1("address")
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> null
			
			sVO.setStateName("s11")
			sVO1.setStateName("s1")
			sVO2.setStateName("s111")
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> []
			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >>  {throw new Exception("exception")}
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> null
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			order.getShippingGroups() >> [hgsg1]
			
			bSvo.setShippingEndDate("")
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("states", _)
			0 * requestMock.setParameter("collegeAddress", bSvo)
			addressContainer.getAddressMap().get("sgaIde") == shippingGroupAddress
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			0 * cTools.validateBeddingAttDate(_, _)
			1 * requestMock.setParameter("errMsg","addressError:1023")
			
	}

	def"service.TC for when collegeIdValue is not equalse to shippingGroupAddress's company name  and order's shipping group is null"(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> "s"
			requestMock.getObjectParameter("shippingGroup") >> null
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> null
			
			hgsg.getShippingAddress() >> sAddress
			sAddress.setAddress1("address")
			hgsg.getId() >> "sg1"
			order.getSiteId() >> siteId
			
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> {throw new BBBBusinessException("exception")} >> null 
			defaultSAddress.setAddress1("address")
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> null
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> []
			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >>  {throw new Exception("exception")}
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> null
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s1")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			1*order.getShippingGroups() >> null
			
			bSvo.setShippingEndDate("")
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("states", _)
			0 * requestMock.setParameter("collegeAddress", bSvo)
			addressContainer.getAddressMap().get("sgaIde") == shippingGroupAddress
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			0 * cTools.validateBeddingAttDate(_, _)
			1 * requestMock.setParameter("errMsg","addressError:1023")
			
	}
	
	def"service.TC for when getShippinggroupAddresses is  empty"(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> null
			requestMock.getObjectParameter("shippingGroup") >> null
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> null
				order.getSiteId() >> siteId
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> {throw new BBBBusinessException("exception")} >> {throw new Exception("exception")}
			defaultSAddress.setAddress1("address")
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> null
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> []
			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >>  {throw new Exception("exception")}
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> "h"
			
			1*cManager.getShippinggroupAddresses(order) >> []
			
			1*order.getShippingGroups() >> [hgsg1]
			
			bSvo.setShippingEndDate("")
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("states", _)
			0 * requestMock.setParameter("collegeAddress", bSvo)
			//addressContainer.getAddressMap().get("sgaIde") == null
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			0 * cTools.validateBeddingAttDate(_, _)
			1 * requestMock.setParameter("errMsg","addressError:1023")
			
	}

	def"service.TC for when SchoolCookie is  null"(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> null
			requestMock.getObjectParameter("shippingGroup") >> null
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> null
				order.getSiteId() >> siteId
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> {throw new BBBBusinessException("exception")} >> {throw new Exception("exception")}
			defaultSAddress.setAddress1("address")
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> null
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> []
			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >>  {throw new Exception("exception")}
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> null
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s1")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			1*order.getShippingGroups() >> [hgsg1]
			
			bSvo.setShippingEndDate("")
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("states", _)
			0 * requestMock.setParameter("collegeAddress", bSvo)
			//addressContainer.getAddressMap().get("sgaIde") == null
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			0 * cTools.validateBeddingAttDate(_, _)
			1 * requestMock.setParameter("errMsg","addressError:1023")
			
	}

	def"service.TC for ParseException"(){
		given:
			String siteId = "usBed"
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profileMcok
			requestMock.getObjectParameter("order") >> order
			requestMock.getCookieParameter("SchoolCookie") >> "s"
			requestMock.getObjectParameter("shippingGroup") >> null
			
			1*cManager.getShippingGroupManager() >> sgManager
			1*sgManager.getHardgoodShippingGroups(order) >> [hgsg]
			
			hgsg.getShippingAddress() >> sAddress
			sAddress.setAddress1("address")
			hgsg.getId() >> "sg1"
			order.getSiteId() >> siteId
			
			//setDefaultAddressFromProfile
			cManager.getProfileAddressTool() >> addressTools
			2*addressTools.getDefaultShippingAddress(profileMcok, siteId) >> defaultSAddress
			defaultSAddress.setAddress1("daddress1") 
			
			1*addressTools.getDefaultBillingAddress(profileMcok, siteId) >> defaultBAddress
			defaultBAddress.setAddress1("daddress1") 
			//end setDefaultAddressFromProfile
			1*cManager.getStates(siteId,true, null) >> [sVO, sVO1, sVO2]
			
			sVO.setStateName("s11")
			sVO1.setStateName("s1")
			sVO2.setStateName("s111")
			
			
			1*cManager.getRegistryShippingAddress(siteId, order) >> [registryAddress]
			//addAddressToContainer
			registryAddress.setIdentifier("r1")
			registryAddress.setOwnerId("rw1")
			registryAddress.setAddress2("saddre2")
			registryAddress.setMiddleName("smidleName")

			
			sAddress.setAddress2("saddre2")
			sAddress.setMiddleName("smidleName")
			// end addAddressToConainer
			
			1*cManager.getProfileShippingAddress(profileMcok, siteId) >> [pShippingAddress]
			pShippingAddress.setIdentifier("sr1")
			pShippingAddress.setOwnerId("rw1")
			pShippingAddress.setAddress1("address")
			
			sAddress.setAddress2("")
			sAddress.setMiddleName("")
			
			//getDefaultAddressId
			defaultSAddress.setIdentifier("dsIdentifier")
			
			requestMock.getObjectParameter("isPackHold") >> "ddf"
			
			1*cManager.getShippinggroupAddresses(order) >> [shippingGroupAddress]
			shippingGroupAddress.setCompanyName("s")
			shippingGroupAddress.setIdentifier("sgaIde")
			
			order.getShippingGroups() >> [hgsg1]
			1*cTools.validateBedingKitAtt([hgsg1], _) >> bSvo
			
			bSvo.setShippingEndDate("12/02")
			
			1*cTools.validateBeddingAttDate(_, _) >> {throw new ParseException("exception" , 1)}
			
			
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("shippingGroup", hgsg)
			1 * requestMock.setParameter("defaultShippingAddress", defaultSAddress)
			1 * requestMock.setParameter("states", _)
			1 * requestMock.setParameter("defaultBillingAddress", defaultBAddress)
			defaultBAddress.getIsNonPOBoxAddress() == true
			defaultSAddress.getIsNonPOBoxAddress() == true
			addressContainer.getAddressMap().get("r1") == registryAddress
			addressContainer.getAddressMap().get("sr1") == pShippingAddress
			addressContainer.getAddressMap().get("sgaIde") == shippingGroupAddress
			
			pShippingAddress.getAddress2() == ""
			pShippingAddress.getMiddleName() == ""
			requestMock.setParameter("profileAddresses", [pShippingAddress])
			
	}
		
}
