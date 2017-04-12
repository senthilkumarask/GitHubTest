package com.bbb.commerce.order.purchase

import atg.commerce.order.OrderHolder
import atg.commerce.order.ShippingGroup
import atg.commerce.order.purchase.CommerceItemShippingInfo
import atg.multisite.SiteContextManager;
import atg.userprofiling.Profile

import java.util.LinkedHashMap;

import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressVO
import com.bbb.account.vo.order.Address
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.vo.BBBAddressSelectionVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec;

class BBBMultiShippingAddressDropletSpecification extends BBBExtendedSpec {
	def BBBMultiShippingAddressDroplet msaDroplet
	def BBBAddressContainer addressContainer = Mock()
	def Profile profile = Mock()
	def BBBOrder order = Mock()
	def BBBMultiShippingManager msManagerMock = Mock()
	def BBBHardGoodShippingGroup hgsg = Mock()
	def BBBHardGoodShippingGroup hgsg1 = Mock()
	def BBBHardGoodShippingGroup hgsg2 = Mock()
	def BBBHardGoodShippingGroup hgsg3 = Mock()
	def BBBHardGoodShippingGroup hgsg4 = Mock()
	def BBBHardGoodShippingGroup hgsg5 = Mock()
	def BBBHardGoodShippingGroup hgsg6 = Mock()
	def BBBHardGoodShippingGroup hgsg7 = Mock()
	def BBBHardGoodShippingGroup hgsg8 = Mock()

	
	def ShippingGroup sg1 = Mock()
	
	def BBBShippingGroupContainerService sgContainerServiceMock = Mock()
	def BBBCheckoutManager checkoutManager = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBAddressAPI addressTools = Mock()
	
	BBBAddressImpl address = new BBBAddressImpl()
	BBBAddressImpl newAddress = new BBBAddressImpl()
	BBBAddressImpl newAddress1 = new BBBAddressImpl()
	BBBAddressImpl newAddress2 = new BBBAddressImpl()
	BBBAddressImpl tempAddress = new BBBAddressImpl()
	BBBAddressImpl tempAddress1 = new BBBAddressImpl()
	BBBAddressImpl tempAddress2 = new BBBAddressImpl()
	BBBAddressImpl tempAddress3 = new BBBAddressImpl()
	BBBAddressImpl tempAddress4 = new BBBAddressImpl()
	BBBAddressImpl tempAddress5 = new BBBAddressImpl()
	BBBAddressImpl tempAddress6 = new BBBAddressImpl()
	BBBAddressImpl tempAddress7 = new BBBAddressImpl()
	BBBAddressImpl tempAddress8 = new BBBAddressImpl()

	BBBAddressImpl defaultAddress = new BBBAddressImpl()
	BBBAddressImpl registryAddress = new BBBAddressImpl()
	
	BBBAddressVO defaultProfileAddress = new BBBAddressVO()
	BBBAddressVO defaultProfileAddress1 = new BBBAddressVO()
	BBBAddressVO defaultProfileAddress2 = new BBBAddressVO()
	BBBAddressVO defaultProfileAddress3 = new BBBAddressVO()
	
	BBBAddressVO addressVo = new BBBAddressVO()
	OrderHolder cartMock = Mock() 
	Profile profileMock = Mock()
	def CommerceItemShippingInfo csinfo = Mock()
	def CommerceItemShippingInfo csinfo1 = Mock()
	
	def setup(){
		msaDroplet = new BBBMultiShippingAddressDroplet(checkoutMgr : checkoutManager , catalogTools : catalogToolsMock, cart:cartMock, profile: profileMock, multiShippingManager:msManagerMock )
	}
	def" service method.TC to initialize the addressMap "(){
		given:
		    msaDroplet = Spy()
			msaDroplet.setShippingGroupMapContainerPath("/atg/commerce/order/purchase/ShippingGroupContainerService")
			String siteId = "usBed"
			1*requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "sgName"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> ["address1" : address]
			
			25*addressContainer.getNewAddressMap() >> ["newAddress" : newAddress, "newAddress1" : newAddress1 , "newAddress2" : newAddress2]
			msaDroplet.duplicateFound(["address1" : address], newAddress) >> true
			msaDroplet.duplicateFound(["address1" : address], newAddress1) >> true
			msaDroplet.duplicateFound(["address1" : address], newAddress2) >> true
			
			msaDroplet.keyForDuplicate(["address1" : address], newAddress) >> "address1" >> null >> "address1"
			msaDroplet.keyForDuplicate(["address1" : address], newAddress1) >> "address2"
			
			newAddress.setAlternatePhoneNumber("2545636")
			newAddress.setPhoneNumber("9685")
			newAddress.setEmail("harry@gmail.com")
			1*requestMock.resolveName("/atg/commerce/order/purchase/ShippingGroupContainerService") >> sgContainerServiceMock
			2*sgContainerServiceMock.getShippingGroup("sgName") >> hgsg
			
			hgsg.getSourceId() >> "newAddress"
			
			addressContainer.getNewAddressKey() >> "newAddress"
			
			
		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("addressContainer", addressContainer);
			1*requestMock.setParameter("newAddress", addressContainer.getNewAddressKey() );
			1*requestMock.setParameter("keys", []);
			1*requestMock.setParameter("defaultAddId", "address1")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);

		
	}
	
	def" service . TC email id , phone number and laternate phone  number is null "(){
		given:
			msaDroplet = Spy()
			def Map addressmap = ["address1" : address]
			def Map newAddressmap = ["sgNa" : newAddress,"address1" : address , "newAddress" : newAddress1]
			def Map test = ["newAddress" : newAddress1 , "address1" : address]
			
			msaDroplet.setShippingGroupMapContainerPath("/atg/commerce/order/purchase/ShippingGroupContainerService")
			msaDroplet.setCheckoutMgr(checkoutManager)
			msaDroplet.setCatalogTools(catalogToolsMock)
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "sgNa"
			
			///////////addAddressToMap
			defaultProfileAddress.setIdentifier("identi")
			checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
			order.getShippingGroups() >> [hgsg]
			hgsg.getShippingAddress() >> tempAddress
			tempAddress.setAddress1("address1")
			hgsg.getSourceId() >> "newAddress"
			tempAddress.setSource("source")
			
			catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
			checkoutManager.getProfileAddressTool() >> addressTools
			addressTools.getDefaultShippingAddress(profile, "usBed") >> null
			defaultProfileAddress.setFirstName("ha")
			defaultProfileAddress.setAddress1("newjer")
			
			checkoutManager.getRegistryShippingAddress("usBed", order) >> []
			
			
			/////////////////////////
			
			addressContainer.setNewAddressMap( newAddressmap) 
			addressContainer.setAddressMap(addressmap)
			msaDroplet.duplicateFound(_, _) >> false >> false >> false >> true >> false >> true
			
			msaDroplet.keyForDuplicate(_, _) >> "address1" 
			
			newAddress.setAlternatePhoneNumber(null)
			newAddress.setPhoneNumber(null)
			newAddress.setEmail(null)
			requestMock.resolveName("/atg/commerce/order/purchase/ShippingGroupContainerService") >> sgContainerServiceMock
			sgContainerServiceMock.getShippingGroup("sgNamme") >> sg1
			
			hgsg.getSourceId() >> "newAddress"
			
			addressContainer.setNewAddressKey("newAddress") 
			
			
		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("addressContainer", addressContainer);
			1*requestMock.setParameter("newAddress", addressContainer.getNewAddressKey() );
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
			1 * requestMock.setParameter('keys', ['newAddress', 'identi', 'address1'])
			1 * requestMock.setParameter('defaultAddId', 'address1')
			addressContainer.getNewAddressMap().containsKey("newAddress")
			addressContainer.getNewAddressMap().containsKey("address1")
		
	}
	
	def" service when key for duplicate return empty address map"(){
		given:
			msaDroplet = Spy()
			def Map addressmap = ["address1" : address]
			def Map newAddressmap = ["sgNa" : newAddress,"address1" : address , "newAddress" : newAddress1]
			
			msaDroplet.setShippingGroupMapContainerPath("/atg/commerce/order/purchase/ShippingGroupContainerService")
			msaDroplet.setCheckoutMgr(checkoutManager)
			msaDroplet.setCatalogTools(catalogToolsMock)
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "sgNa"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> ["address1" : address]
		
			addressContainer.setNewAddressMap( newAddressmap)
			addressContainer.setAddressMap(addressmap)
			msaDroplet.duplicateFound(_, _) >> false >> true 
			
			msaDroplet.keyForDuplicate(_, _) >> ""
			
			newAddress.setAlternatePhoneNumber(null)
			newAddress.setPhoneNumber(null)
			newAddress.setEmail(null)
			requestMock.resolveName("/atg/commerce/order/purchase/ShippingGroupContainerService") >> sgContainerServiceMock
			sgContainerServiceMock.getShippingGroup("sgNamme") >> sg1
			
			hgsg.getSourceId() >> "newAddress"
			
			addressContainer.setNewAddressKey(null)
			
			
		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("addressContainer", addressContainer);
			1*requestMock.setParameter("newAddress", addressContainer.getNewAddressKey() );
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
			1 * requestMock.setParameter('keys', [])
			1 * requestMock.setParameter('defaultAddId', _)
		
	}
	
	def" service . TC when new address map is  empty"(){
		given:
			msaDroplet = Spy()
			def Map addressmap = ["address1" : address]
			def Map newAddressmap = [:]
			
			msaDroplet.setShippingGroupMapContainerPath("/atg/commerce/order/purchase/ShippingGroupContainerService")
			msaDroplet.setCheckoutMgr(checkoutManager)
			msaDroplet.setCatalogTools(catalogToolsMock)
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "sgNa"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> ["address1" : address]
		
			addressContainer.setNewAddressMap( newAddressmap)
			addressContainer.setAddressMap(addressmap)
			msaDroplet.duplicateFound(_, _) >> false >> true
			
			msaDroplet.keyForDuplicate(_, _) >> ""
			
			newAddress.setAlternatePhoneNumber(null)
			newAddress.setPhoneNumber(null)
			newAddress.setEmail(null)
			requestMock.resolveName("/atg/commerce/order/purchase/ShippingGroupContainerService") >> sgContainerServiceMock
			sgContainerServiceMock.getShippingGroup("sgNamme") >> sg1
			
			hgsg.getSourceId() >> "newAddress"
			
			addressContainer.setNewAddressKey(null)
			
			
		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("addressContainer", addressContainer);
			1*requestMock.setParameter("newAddress", addressContainer.getNewAddressKey() );
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
			1 * requestMock.setParameter('keys', [])
			1 * requestMock.setParameter('defaultAddId', _)
			0 * msaDroplet.keyForDuplicate(_, _)
	}
	
	def" service . TC when new address map is  null"(){
		given:
			msaDroplet = Spy()
			def Map addressmap = ["address1" : address]
			def Map newAddressmap = [:]
			
			msaDroplet.setShippingGroupMapContainerPath("/atg/commerce/order/purchase/ShippingGroupContainerService")
			msaDroplet.setCheckoutMgr(checkoutManager)
			msaDroplet.setCatalogTools(catalogToolsMock)
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> ""
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> ["address1" : address]
		
			addressContainer.setNewAddressMap( null)
			addressContainer.setAddressMap(addressmap)
			msaDroplet.duplicateFound(_, _) >> false >> true
			
			msaDroplet.keyForDuplicate(_, _) >> ""
			
			newAddress.setAlternatePhoneNumber(null)
			newAddress.setPhoneNumber(null)
			newAddress.setEmail(null)
			requestMock.resolveName("/atg/commerce/order/purchase/ShippingGroupContainerService") >> sgContainerServiceMock
			sgContainerServiceMock.getShippingGroup("sgNamme") >> sg1
			
			hgsg.getSourceId() >> "newAddress"
			
			//addressContainer.setNewAddressKey(newAddressmap)
			
			
		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("addressContainer", addressContainer);
			1*requestMock.setParameter("newAddress", addressContainer.getNewAddressKey() );
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
			1 * requestMock.setParameter('keys', [])
			1 * requestMock.setParameter('defaultAddId', _)
			0 * msaDroplet.keyForDuplicate(_, _)
	}
	
	
	def" service . TC when address key list contain default address id "(){
		given:
			msaDroplet = Spy()
			def Map addressmap = ["address1" : address]
			def Map newAddressmap = ["aga":address]
			
			msaDroplet.setShippingGroupMapContainerPath("/atg/commerce/order/purchase/ShippingGroupContainerService")
			msaDroplet.setCheckoutMgr(checkoutManager)
			msaDroplet.setCatalogTools(catalogToolsMock)
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "aga"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> ["address1" : address]
		
			addressContainer.setNewAddressMap( newAddressmap)
			addressContainer.setAddressMap(addressmap)
			msaDroplet.duplicateFound(_, _) >> false >> false
			
			msaDroplet.keyForDuplicate(_, _) >> ""
			
			requestMock.resolveName("/atg/commerce/order/purchase/ShippingGroupContainerService") >> sgContainerServiceMock
			sgContainerServiceMock.getShippingGroup("sgNamme") >> sg1
			
			hgsg.getSourceId() >> "newAddress"
			
			addressContainer.setNewAddressKey(null)
			
			
		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("addressContainer", addressContainer);
			1*requestMock.setParameter("newAddress", addressContainer.getNewAddressKey() );
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
			1 * requestMock.setParameter('keys', ['aga'])
			1 * requestMock.setParameter('defaultAddId', _)
			0 * msaDroplet.keyForDuplicate(_, _)
	}
	
	def "service . TC for BBBBusinessException exception" (){
		given:
			msaDroplet = Spy()
			
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "aga"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> {throw new BBBBusinessException("exception")}

		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("error", _)
			0*requestMock.setParameter("addressContainer", _);
			0*requestMock.setParameter("newAddress", _ );
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock);

	}
	
	def "service . TC for BBBSystemException exception" (){
		given:
			msaDroplet = Spy()
			
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "aga"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> {throw new BBBSystemException("exception")}

		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("error", _)
			0*requestMock.setParameter("addressContainer", _);
			0*requestMock.setParameter("newAddress", _ );
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock);

	}
	
	def "service . TC for Exception " (){
		given:
			msaDroplet = Spy()
			
			String siteId = "usBed"
			BBBAddressContainer addressContainer = new  BBBAddressContainer()
			
			requestMock.getObjectParameter("addressContainer") >> addressContainer
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getObjectParameter("order") >> order
			requestMock.getParameter("siteId") >> siteId
			requestMock.getParameter("shippingGroupName") >> "aga"
			msaDroplet.addAddressToMap(profile, order, siteId, _) >> {throw new Exception("exception")}

		when:
			msaDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("error", _)
			0*requestMock.setParameter("addressContainer", _);
			0*requestMock.setParameter("newAddress", _ );
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock);

	}
	
	/******************************************************addAddressToMap*************************************/
	
	def "addAddressToMap. Tc add address in address map"(){
		given:
		def List<String> addressKeys = new ArrayList()
		defaultProfileAddress.setIdentifier("identi")
		defaultProfileAddress2.setIdentifier("iden")
		defaultProfileAddress1.setIdentifier("ide")
		
		1*checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress,defaultProfileAddress,defaultProfileAddress1,defaultProfileAddress2, defaultProfileAddress3]
		1*order.getShippingGroups() >> [hgsg, hgsg1, hgsg2, hgsg3 ,hgsg4, hgsg5, hgsg6 ,hgsg7, sg1 ]
		hgsg.getShippingAddress() >> tempAddress
		hgsg1.getShippingAddress() >> tempAddress1
		hgsg2.getShippingAddress() >> tempAddress2
		hgsg3.getShippingAddress() >> tempAddress3
		hgsg4.getShippingAddress() >> tempAddress4
		hgsg5.getShippingAddress() >> tempAddress5
		hgsg6.getShippingAddress() >> tempAddress6
		hgsg7.getShippingAddress() >> tempAddress7
		
		
		tempAddress.setAddress1("address1")
		tempAddress1.setAddress1("address1")
		tempAddress2.setAddress1("address1")
		tempAddress3.setAddress1("address1")
		tempAddress4.setAddress1("address1")
		tempAddress5.setAddress1("address1")
		tempAddress6.setAddress1("address1")
		tempAddress7.setAddress1("")
		

		
		hgsg.getSourceId() >> "sgId"
		hgsg1.getSourceId() >> "sgId1"
		hgsg2.getSourceId() >> "sgId2"
		hgsg3.getSourceId() >> "sgId"
		hgsg4.getSourceId() >> "registry"
		hgsg5.getSourceId() >> "PROFILE"
		hgsg6.getSourceId() >> ""
		
		
		tempAddress.setSource("source")
		tempAddress1.setSource("registry")
		tempAddress2.setSource("PROFILE")
		tempAddress3.setSource("registry")

		
		1*catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		checkoutManager.getProfileAddressTool() >> addressTools
		1*addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setFirstName("ha")
		defaultProfileAddress.setAddress1("newjer")
		
		1*checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == tempAddress
		values.get("identi") == defaultProfileAddress
		values.get("identifier") == registryAddress
		addressKeys.contains("sgId")
		addressKeys.contains("identifier")
		addressKeys.contains("identi")
		
	}
	
	def "addAddressToMap . TC for POBOX address"(){
		given:
		def List<String> addressKeys = new ArrayList()
		defaultProfileAddress.setIdentifier("identi")
		defaultProfileAddress2.setIdentifier("iden")
		defaultProfileAddress1.setIdentifier("ide")
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		order.getShippingGroups() >> [hgsg]
		hgsg.getShippingAddress() >> tempAddress
	
		
		tempAddress.setAddress1("address1")
		

		
		hgsg.getSourceId() >> "sgId"
		
		tempAddress.setSource("source")

		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setFirstName("ha")
		defaultProfileAddress.setAddress1("")
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == tempAddress
		values.get("identi") == defaultProfileAddress
		values.get("identifier") == registryAddress
		
	}
	
	def "addAddressToMap . TC when shipTo_POBoxOn is false "(){
		given:
		def List<String> addressKeys = new ArrayList()
		defaultProfileAddress.setIdentifier("identi")
		defaultProfileAddress2.setIdentifier("iden")
		defaultProfileAddress1.setIdentifier("ide")
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		order.getShippingGroups() >> [hgsg]
		hgsg.getShippingAddress() >> tempAddress
	
		
		tempAddress.setAddress1("address1")
		

		
		hgsg.getSourceId() >> "sgId"
		
		tempAddress.setSource("source")

		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["false"]
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setFirstName("ha")
		defaultProfileAddress.setAddress1("")
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == tempAddress
		values.get("identi") == defaultProfileAddress
		values.get("identifier") == registryAddress
		
	}
	
	def "addAddressToMap . TC when shipTo_POBoxOn is false and adresss 1 is not empty of default address"(){
		given:
		def List<String> addressKeys = new ArrayList()
		defaultProfileAddress.setIdentifier("identi")
		defaultProfileAddress2.setIdentifier("iden")
		defaultProfileAddress1.setIdentifier("ide")
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		order.getShippingGroups() >> [hgsg]
		hgsg.getShippingAddress() >> tempAddress
	
		
		tempAddress.setAddress1("address1")
		

		
		hgsg.getSourceId() >> "sgId"
		
		tempAddress.setSource("source")

		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["false"]
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setFirstName("ha")
		defaultProfileAddress.setAddress1("fd")
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == tempAddress
		values.get("identi") == defaultProfileAddress
		values.get("identifier") == registryAddress
		
	}
	
	def "addAddressToMap . TC when default shipping address is null"(){
		given:
		def List<String> addressKeys = new ArrayList()
		defaultProfileAddress.setIdentifier("identi")
		defaultProfileAddress2.setIdentifier("iden")
		defaultProfileAddress1.setIdentifier("ide")
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		order.getShippingGroups() >> [hgsg]
		hgsg.getShippingAddress() >> tempAddress
	
		
		tempAddress.setAddress1("address1")
		

		
		hgsg.getSourceId() >> "sgId"
		
		tempAddress.setSource("source")

		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["false"]
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> null
		defaultProfileAddress.setFirstName("ha")
		defaultProfileAddress.setAddress1("fd")
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == tempAddress
		values.get("identi") == defaultProfileAddress
		values.get("identifier") == registryAddress
		
	}
	
	def "addAddressToMap . TC when default shipping address's owner id is null"(){
		given:
		def List<String> addressKeys = new ArrayList()
		defaultProfileAddress.setIdentifier("identi")
		defaultProfileAddress2.setIdentifier("iden")
		defaultProfileAddress1.setIdentifier("ide")
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		order.getShippingGroups() >> [hgsg]
		hgsg.getShippingAddress() >> tempAddress
	
		
		tempAddress.setAddress1("address1")
		

		
		hgsg.getSourceId() >> "sgId"
		
		tempAddress.setSource("source")

		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["false"]
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setOwnerId(null)
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == tempAddress
		values.get("identi") == defaultProfileAddress
		values.get("identifier") == registryAddress
		
	}
	
	/******************************************** exception scenario *******************************/
	def "addAddressToMap . TC for BBBSystemException "(){
		given:
		def List<String> addressKeys = new ArrayList()
		order.getShippingGroups() >> []
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> {throw new BBBSystemException("exception")}
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setOwnerId(null)
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == null
		values.get("identi") == null
		values.get("identifier") == registryAddress

	}
	
	def "addAddressToMap . TC for BBBBusinessException "(){
		given:
		def List<String> addressKeys = new ArrayList()
		order.getShippingGroups() >> []
		
		checkoutManager.getProfileShippingAddress(profile, "usBed") >> [defaultProfileAddress]
		
		catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> {throw new BBBBusinessException("exception")}
		checkoutManager.getProfileAddressTool() >> addressTools
		addressTools.getDefaultShippingAddress(profile, "usBed") >> defaultProfileAddress
		defaultProfileAddress.setOwnerId(null)
		
		checkoutManager.getRegistryShippingAddress("usBed", order) >> [registryAddress]
		registryAddress.setIdentifier("identifier")
		when:
		LinkedHashMap<String, BBBAddress> values = msaDroplet.addAddressToMap(profile, order, "usBed", addressKeys)
		then:
		values.get("sgId") == null
		values.get("identi") == null
		values.get("identifier") == registryAddress
		
	}
	
/********************************************* duplicateFound ****************************************/
	def "duplicateFound. TC to check duplicate address" () {
		given:
		def Object a = Mock()
		def Map map = ["t1" : tempAddress]
		tempAddress.setRegistryId(null)
		
		when:
		boolean value = msaDroplet.duplicateFound(map, address)
		then:
		value == true
		
	}
	
	def "duplicateFound. TC to check duplicate address when registry id is empty" () {
		given:
		def Map map = ["t1" : tempAddress]
		tempAddress.setRegistryId("")
		
		when:
		boolean value = msaDroplet.duplicateFound(map, address)
		then:
		value == true
		
	}
	
	def "duplicateFound. TC to check duplicate Address when tempaddress type is not Address" () {
		given:
		def BBBAddress a = Mock()
		def Map map = ["t1" : a]
		tempAddress.setRegistryId("")
		
		when:
		boolean value = msaDroplet.duplicateFound(map, address)
		then:
		value == false
		
	}
	
	def "duplicateFound. TC to check duplicate Address when pased address type is not Address" () {
		given:
		def BBBAddress a = Mock()
		
		def Map map = ["t1" : tempAddress]
		tempAddress.setRegistryId("")
		
		when:
		boolean value = msaDroplet.duplicateFound(map, a)
		then:
		value == false
		
	}

	/*************************************** getMultishippingAddresses **************************/
	def "getMultishippingAddresses.TC for multishipping address" () {
		given:
		def Map map = ["c1":"dId","c2":"dId"]
		msaDroplet = Spy()
		msaDroplet.setMultiShippingManager(msManagerMock)
		msaDroplet.setCart(cartMock)
		msaDroplet.setProfile(profileMock)
		
		cartMock.getCurrent() >> order
	
		1*msManagerMock.getCommerceItemShippingInfos(false) >> ["c1" : csinfo , "c2" : csinfo1]
		csinfo.getShippingGroupName() >> "s1"
		csinfo1.getShippingGroupName() >> ""
		
		2*msaDroplet.service(requestMock, responseMock) >> {}
		2*requestMock.getObjectParameter("defaultAddId") >> "dId"
		1*requestMock.getObjectParameter("addressContainer") >> addressContainer
		
		when:
		BBBAddressSelectionVO vo = msaDroplet.getMultishippingAddresses()
		
		then:
		1*requestMock.setParameter("profile", profileMock);
		1*requestMock.setParameter("order", order);
		1*requestMock.setParameter("siteId", _);
		1*requestMock.setParameter("addressContainer",_);
		vo.getNewAddrContainer() == addressContainer
		vo.getDefaultAddIdMap() == map
		1*requestMock.setParameter("shippingGroupName", "s1")
		1*requestMock.setParameter("shippingGroupName", "")
		
	}
	
	/********************************************** keyForDuplicate ******************************/
	def "keyForDuplicate. TC to check duplicate address" () {
		given:
		def Map map = ["t1" : tempAddress]
		tempAddress.setRegistryId(null)
		
		when:
		String value = msaDroplet.keyForDuplicate(map, address)
		then:
		value == "t1"
		
	}
	
	def "keyForDuplicate. TC to check duplicate address when registry id is empty" () {
		given:
		def Map map = ["t1" : tempAddress]
		tempAddress.setRegistryId("")
		
		when:
		String value = msaDroplet.keyForDuplicate(map, address)
		then:
		value == "t1"
		
	}
	
	def "keyForDuplicate. TC to check duplicate Address when tempaddress type is not Address" () {
		given:
		def BBBAddress a = Mock()
		def Map map = ["t1" : a]
		tempAddress.setRegistryId("")
		
		when:
		String value = msaDroplet.keyForDuplicate(map, address)
		then:
		value == ""
		
	}
	
	def "keyForDuplicate. TC to check duplicate Address when pased address type is not Address" () {
		given:
		def BBBAddress a = Mock()
		
		def Map map = ["t1" : tempAddress]
		tempAddress.setRegistryId("")
		
		when:
		String value = msaDroplet.keyForDuplicate(map, a)
		then:
		value == ""
		
	}
}
