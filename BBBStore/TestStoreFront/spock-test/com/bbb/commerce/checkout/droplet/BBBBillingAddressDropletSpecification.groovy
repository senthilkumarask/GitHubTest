package com.bbb.commerce.checkout.droplet

import atg.multisite.SiteContextManager;
import atg.userprofiling.Profile

import java.util.List;

import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressVO
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class BBBBillingAddressDropletSpecification extends BBBExtendedSpec {
 def BBBBillingAddressDroplet baDropletObj
 def BBBOrder orderMock = Mock()
 def Profile profile =  Mock()
 def BBBAddressContainer addressContainer = Mock()
 def BBBRepositoryContactInfo contactInfoMock = Mock()
 def BBBCheckoutManager checkoutManagerMock = Mock()
 def BBBCatalogToolsImpl catalogTools = Mock()
 def BBBAddressAPI addressApi = Mock()
 def BBBShippingGroupManager shippingManagerMock = Mock()
 def BBBRepositoryContactInfo reContactInfoMock = Mock()
 BBBAddressVO address = new BBBAddressVO()
 
 BBBAddressVO addressVO1 = new BBBAddressVO()
 BBBAddressVO addressVO2 = new BBBAddressVO()
 BBBAddressVO defaultAddress = new BBBAddressVO()
 BBBAddressVO restrictedAddress = new BBBAddressVO()
 
 BBBAddress test = new BBBAddressVO()
 
 
 
 def setup(){
	baDropletObj  = new BBBBillingAddressDroplet(checkoutMgr : checkoutManagerMock,addressAPI : addressApi, catalogTools : catalogTools,shippingGroupManager : shippingManagerMock )
 }
 

 
 def "service. TC to add the address map in request object .  when country map is null  " (){
	 given:
	  BBBAddressContainer addressContainer = new  BBBAddressContainer()
	 
	 address.setCountry("us")
	 address.setId("")
	 address.setState("new")
	 address.setAddress1("kkkstreet")
	 
	 addressVO1.setAddress1("jjjStreet")
	 addressVO1.setId("ad1")
	 addressVO2.setId("ad2")
	 
	 restrictedAddress.setCountry("newCountry")
	 restrictedAddress.setId("resId")
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 orderMock.getBillingAddress() >> null
	 addressContainer.getAddressMap() >> [:]
	 1*checkoutManagerMock.getBillingAddress(_) >> address
	 1*catalogTools.getCountriesInfoForCreditCard("us") >> null
	 addressContainer.getSourceKey(address.getId()) >> null
	 ///authentication for user
	 profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 4
	 
	 // duplicate address
	 addressContainer.getDuplicate() >> [addressVO2]
	 1*addressApi.getShippingAddress(profile, null) >> [address]
	 
	 // default address from profile
	 defaultAddress.setFirstName("firstName")
	 1* addressApi.getDefaultBillingAddress(profile, null) >> address
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> [restrictedAddress]
	 2*checkoutManagerMock.getNotSavableStates() >> ["new"]
	 
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "resId");
	 1 * requestMock.setParameter("addresses", _)
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
	 addressContainer.getAddressMap().get("resId").getCountry() == "newCountry"  
 }

 def "service. TC to add th address map in request object  " (){
	 given:
	 BBBAddressContainer addressContainer = new  BBBAddressContainer()
	 
	 address.setCountry("us")
	 address.setId("ad1235")
	 addressVO1.setAddress1("jjjStreet")
	 addressVO1.setId("ad1")
	 addressVO2.setId("ad2")
	 
	 restrictedAddress.setCountry("new")
	 restrictedAddress.setId("resId")
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 orderMock.getBillingAddress() >> reContactInfoMock
	 addressContainer.getAddressMap() >> [:]
	 1*checkoutManagerMock.getBillingAddress(_) >> address
	 1*catalogTools.getCountriesInfoForCreditCard("us") >> ["us" : "UnitedSatate"]
	 
	 addressContainer.addSourceMapping(address.getId(), "sourceKey")
	 ///authentication for user
	 profile.isTransient() >> false
	 profile.getPropertyValue("securityStatus") >> 4
	 
	 // duplicate address
	 addressContainer.getDuplicate() >> [addressVO2]
	 
	 1 * addressApi.getShippingAddress(profile, null) >> [addressVO1]
	 
	 // default address from profile
	 defaultAddress.setFirstName("firstName")
	 1 * addressApi.getDefaultBillingAddress(profile, null) >> defaultAddress
	 
	 3 * shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> [restrictedAddress]
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "ad1235");
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 1 * requestMock.setParameter("isUserAuthed", "TRUE")
	 addressContainer.getAddressMap().get("ad1235").getCountry() == "us"
	 addressContainer.getAddressMap().get("ad1").getAddress1() == "jjjStreet"
	 addressContainer.getAddressMap().get("resId").getCountry() == "new"
 }
 
 
 def "service. TC to add the address map in request . when country map is empty , profile is transient and NonRegistryShippingAddress is empty " (){
	 given:
	 
	  BBBAddressContainer addressContainer = new  BBBAddressContainer()
	 
	 address.setCountry("us")
	 address.setId("id852")
	 address.setState("new")
	 address.setAddress1("kkkstreet")
	 
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 1*checkoutManagerMock.getBillingAddress(_) >> address
	 
	 1*catalogTools.getCountriesInfoForCreditCard("us") >> [:]
	 
	 addressContainer.getSourceKey(address.getId()) >> "sourceKey"
	 ///authentication for user
	 profile.isTransient() >> true
	 1*profile.getPropertyValue("securityStatus") >> null
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> []
	 checkoutManagerMock.getNotSavableStates() >> ["newl"]

	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "id852");
	 1 * requestMock.setParameter("addresses", _)
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 addressContainer.getAddressMap().get("id852").getCountry() == "us"
	 
 }
 
 def "service. TC to add the address map in request . when country of billing address is empty , profile is transient and NonRegistryShippingAddress is null " (){
	 given:
	 
	  BBBAddressContainer addressContainer = new  BBBAddressContainer()
	 

	 address.setId("i52")
	 address.setState("new")
	 address.setAddress1("kkkstreet")
	 
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 1*checkoutManagerMock.getBillingAddress(_) >> address
	 	 
	 addressContainer.getSourceKey(address.getId()) >> "sourceKey"
	 ///authentication for user
	 profile.isTransient() >> true
	 1*profile.getPropertyValue("securityStatus") >> 3
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> null
	 checkoutManagerMock.getNotSavableStates() >> ["newl"]

	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "i52");
	 1 * requestMock.setParameter("addresses", _)
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 addressContainer.getAddressMap().get("i52").getState() == "new"
	 
 }
 
 def "service. TC to add the address map in request . when billing address is null , profile is not  transient and getShippingGroupManager is null . duplicate and shipping address is same  " (){
	 given:
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
     baDropletObj.setAddressAPI(addressApi)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	 
	 address.setId("i52")
	 address.setState("new")
	 address.setAddress1("kkkstreet")
	 
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 checkoutManagerMock.getBillingAddress(_) >> null
		  
	 ///authentication for user
	 profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 5
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> null
	 checkoutManagerMock.getNotSavableStates() >> ["newl"]
     
	 addressContainer.getDuplicate() >> [address]
	 
	 addressApi.getShippingAddress(profile, null) >> [address]
	 
	 defaultAddress.setFirstName("firstName")
	 addressApi.getDefaultBillingAddress(profile, null) >> null
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", null);
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC to add the address map in request . when shipping address list is null , profile is not  transient and order is null .  " (){
	 given:
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	 baDropletObj.setAddressAPI(addressApi)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	 
	 address.setId("i5278")
	 address.setState("new")
	 
	 
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 checkoutManagerMock.getBillingAddress(_) >> address
		  
	 ///authentication for user
	 profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 5
	 
	 
	 addressApi.getShippingAddress(profile, null) >> [null]
	 
	 defaultAddress.setFirstName("firstName")
	 addressApi.getDefaultBillingAddress(profile, null) >> null
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "i5278");
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC to add the address map in request . when Address1 of shipping address  is empty , profile is not  transient and order is null .  " (){
	 given:
	 
	 address.setId("i5278")
	 address.setState("new")
	 
	 
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 checkoutManagerMock.getBillingAddress(_) >> address
		  
	 ///authentication for user
	 profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 5
	 
	 
	 addressApi.getShippingAddress(profile, null) >> [address]
	 
	 defaultAddress.setFirstName("firstName")
	 addressApi.getDefaultBillingAddress(profile, null) >> null
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "i5278");
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC to add the address map in request . when owner Id of billing address  is not empty , profile is not  transient and order is not null .  " (){
	 given:
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	 baDropletObj.setAddressAPI(addressApi)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	 
	 address.setId("i5278")
	 address.setState("new")
	 address.setOwnerId("ownId")
	 address.setAddress1("street")
	 addressVO2.setAddress2("address2")
	 addressVO2.setMiddleName("dfsdf")
	 
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 checkoutManagerMock.getBillingAddress(_) >> address
		  
	 ///authentication for user
	 profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 5
	 
	 
	 addressApi.getShippingAddress(profile, null) >> [address]
	 addressContainer.getDuplicate() >> [addressVO2]
	 
	 
	 defaultAddress.setFirstName("firstName")
	 defaultAddress.setOwnerId(null)
	 addressApi.getDefaultBillingAddress(profile, null) >> defaultAddress
	 baDropletObj.extrachIsNullAddress(defaultAddress) >> true
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "i5278");
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC to add the address  in request . when getAddressAPI is null and NonRegistryShippingAddress and billing address is same .  " (){
	 given:
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	  baDropletObj.setShippingGroupManager(shippingManagerMock)
	  BBBAddressContainer addressContainer = new  BBBAddressContainer()
	  
	  
	 addressVO2.setAddress2("address2")
	 addressVO2.setMiddleName("dfsdf")
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter("billingAddrContainer") >> addressContainer
	 
	  profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 5
	 
	 addressContainer.getDuplicate() >> [addressVO2]
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> [null]
	 
	 
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", null);
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC to add the address  in requet . when getAddressAPI is null . NonRegistryShippingAddress and duplicate address is same .  " (){
	 given:
	  
	  baDropletObj.setAddressAPI(null)
	 address.setAddress1("kk")
	  addressVO2.setAddress1("kk")
	 addressVO2.setAddress2()
	 addressVO2.setMiddleName("dfsdf")
	 addressContainer.getAddressMap() >> new HashMap()
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter("billingAddrContainer") >> addressContainer
	 
	  profile.isTransient() >> false
	 1*profile.getPropertyValue("securityStatus") >> 5
	 
	 addressContainer.getDuplicate() >> [address]
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> [address]
	 baDropletObj.compareAddressesList() >> true
	 
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", null);
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC to add the address  in requet when  NonRegistryShippingAddress and profileAddList address is same .  " (){
	 given:
	  
     baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	 baDropletObj.setAddressAPI(addressApi)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	  baDropletObj.setShippingGroupManager(shippingManagerMock)
	  BBBAddressContainer addressContainer = new  BBBAddressContainer()
	  
	 address.setId("id")
	 address.setAddress1("address2")
	 address.setMiddleName("dfsdf")
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter("billingAddrContainer") >> addressContainer
	 
	  profile.isTransient() >> false
	  profile.getPropertyValue("securityStatus") >> 5
	  addressApi.getShippingAddress(profile, null) >> [address]
	  
	 addressContainer.getDuplicate() >> [addressVO2]
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> [address]
	 
	 
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "id");
	 1 * requestMock.setParameter("addresses", _)
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 addressContainer.getAddressMap().get("id").getMiddleName() == "dfsdf"
 }
 
 def "service. TC to add the address  in requet when .NonRegistryShippingAddress and profileAddList address is same and selectedAddrKey is not blanck  .  " (){
	 given:
	  
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	 baDropletObj.setAddressAPI(addressApi)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	  baDropletObj.setShippingGroupManager(shippingManagerMock)
	  BBBAddressContainer addressContainer = new  BBBAddressContainer()
	  
	 address.setId("id")
	 address.setAddress1("address2")
	 address.setMiddleName("dfsdf")
	 
	 requestMock.getObjectParameter("order") >> orderMock
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter("billingAddrContainer") >> addressContainer
	 
	 //checkoutManagerMock.getBillingAddress(_) >> address
	 
	  profile.isTransient() >> false
	  profile.getPropertyValue("securityStatus") >> 5
	  addressApi.getShippingAddress(profile, null) >> [address]
	  
	 addressContainer.getDuplicate() >> [addressVO2]
	 shippingManagerMock.getNonRegistryShippingAddress(orderMock) >> [address]
	 
	 baDropletObj.extrachIsNullAddress(address) >> false
	 addressApi.getDefaultBillingAddress(profile, null) >> address
	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "id");
	 1 * requestMock.setParameter("addresses", _)
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 addressContainer.getAddressMap().get("id").getMiddleName() == "dfsdf"
 }
 
 
 def "compareAddressesList. when address List and address having address1 , middlename and address2 " (){
	 given:
	 address .setAddress1("add")
	 address.setMiddleName("midel")
	 addressVO1.setAddress2("add")
	 addressVO1.setMiddleName("midel")
	 
	 when:
	 boolean value = baDropletObj.compareAddressesList([addressVO1], address)
	 then:
	 value == false
	 
 }
 
 def "compareAddressesList. when ddress List  having  middlename and address2 empty value" (){
	 given:
	 address .setAddress1("")
	 address.setMiddleName("")
	 addressVO1.setAddress2("add")
	 addressVO1.setMiddleName("midel")
	 
	 when:
	 boolean value = baDropletObj.compareAddressesList([address], addressVO1)
	 then:
	 value == false
	 
 }
 
 def "compareAddressesList. when ddress List  and address is null" (){
	 given:
	 
	 when:
	 boolean value = baDropletObj.compareAddressesList([null], null)
	 then:
	 value == false
	 
 }
 
 def "getSelectedKey. get the key " (){
	 given:
	 address .setAddress1("")
	 address.setMiddleName("")
	 addressVO1.setAddress2("add")
	 addressVO1.setMiddleName("midel")
	 when:
	 String value = baDropletObj.getSelectedKey([address], addressVO1, "key")
	 then:
	 value == "key"
	 
 }
 
 def "getSelectedKey. get the key when address list and address is null " (){
	 given:
	 when:
	 String value = baDropletObj.getSelectedKey([null], null, "key")
	 then:
	 value == "key"
	 
 }
 
 def "getSelectedKey. get the key when address list  is null " (){
	 given:
	 when:
	 String value = baDropletObj.getSelectedKey([null], address, "key")
	 then:
	 value == "key"
	 
 }
 
 def "isUserAuthenticated. security status is 3 " (){
	 given:
	 profile.isTransient() >> false
	 profile.getPropertyValue("securityStatus") >> 3
	 when:
	 String value = baDropletObj.isUserAuthenticated(profile)
	 then:
	 value == "false"
	 
 }
 
 def "isUserAuthenticated. get the key when address list  is null " (){
	 given:
	 profile.isTransient() >> false
	 profile.getPropertyValue("securityStatus") >> null
	 when:
	 String value = baDropletObj.isUserAuthenticated(profile)
	 then:
	 value == "false"
	 
 }
 
///////////////////////////// exception scenario /////////////////////////////// 
 
 def "service. TC for BBBBusinessException whiel countries info   " (){
	 given:
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	 
	 address.setId("i52")
	 address.setCountry("add")
	 
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 checkoutManagerMock.getBillingAddress(_) >> address
	 catalogTools.getCountriesInfoForCreditCard("add") >> {throw new BBBBusinessException("exception")}
	 
	 ///authentication for user
	 profile.isTransient() >> false
	 	 
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "i52");
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC for BBBSystemException whiel countries info   " (){
	 given:
	 baDropletObj = Spy()
	 baDropletObj.setCatalogTools(catalogTools)
	 baDropletObj.setCheckoutMgr(checkoutManagerMock)
	 
	 address.setId("i52")
	 address.setCountry("add")
	 
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 addressContainer.getAddressMap() >> [:]
	 checkoutManagerMock.getBillingAddress(_) >> address
	 catalogTools.getCountriesInfoForCreditCard("add") >> {throw new BBBSystemException("exception")}
	 
	 ///authentication for user
	 profile.isTransient() >> false
		  
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", "i52");
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC for BBBSystemException whie countries info   " (){
	 given:
	 
	 BBBAddressContainer addressContainer = new  BBBAddressContainer()
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 checkoutManagerMock.getBillingAddress(_) >> null
	 addressApi.getShippingAddress(profile, null) >> {throw new BBBSystemException("exception")}
	 
	 ///authentication for user
	 profile.isTransient() >> false
	 profile.getPropertyValue("securityStatus") >> 4
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", null);
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
 def "service. TC for BBBBusinessException whie countries info   " (){
	 given:
	 
	 BBBAddressContainer addressContainer = new  BBBAddressContainer()
	 requestMock.getObjectParameter("order") >> null
	 requestMock.getObjectParameter("profile") >> profile
	 requestMock.getObjectParameter( "billingAddrContainer") >> addressContainer
	 
	 checkoutManagerMock.getBillingAddress(_) >> null
	 addressApi.getShippingAddress(profile, null) >> {throw new BBBBusinessException("exception")}
	 
	 ///authentication for user
	 profile.isTransient() >> false
	 profile.getPropertyValue("securityStatus") >> 4
	 checkoutManagerMock.getNotSavableStates() >> ["new"]
	 when:
	 baDropletObj.service(requestMock, responseMock)
	 then:
	 1 * requestMock.setParameter("selectedAddrKey", null);
	 1 * requestMock.setParameter("addresses", [:])
	 1 * requestMock.serviceParameter("output", requestMock, responseMock)
	 
 }
 
}
