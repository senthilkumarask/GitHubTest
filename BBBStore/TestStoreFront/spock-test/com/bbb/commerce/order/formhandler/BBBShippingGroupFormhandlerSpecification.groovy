package com.bbb.commerce.order.formhandler

import java.beans.IntrospectionException
import java.text.SimpleDateFormat

import javax.servlet.http.HttpSession
import javax.transaction.SystemException
import javax.transaction.Transaction
import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.CommerceException
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.CreditCard
import atg.commerce.order.InvalidParameterException
import atg.commerce.order.OrderHolder
import atg.commerce.order.ShippingGroupCommerceItemRelationship
import atg.commerce.order.ShippingGroupNotFoundException
import atg.commerce.order.purchase.CommerceItemShippingInfo
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer
import atg.commerce.order.purchase.CommerceItemShippingInfoTools
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.PricingTools
import atg.commerce.promotion.PromotionTools
import atg.commerce.util.RepeatingRequestMonitor
import atg.core.util.Address
import atg.droplet.DropletConstants
import atg.droplet.DropletException
import atg.naming.NameContext
import atg.repository.RepositoryException
import atg.service.pipeline.RunProcessException
import atg.userprofiling.Profile

import com.bbb.account.BBBGetCouponsManager
import com.bbb.account.BBBProfileManager
import com.bbb.account.BBBProfileTools
import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressAPIConstants
import com.bbb.account.api.BBBAddressVO
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.order.BBBCommerceItemManager
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.commerce.order.BBBPaymentGroupManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.commerce.order.Paypal
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean
import com.bbb.commerce.order.purchase.BBBMultiShippingManager
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper
import com.bbb.commerce.order.purchase.BBBShippingGroupContainerService
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.commerce.order.shipping.BBBShippingInfoBean
import com.bbb.common.vo.CommerceItemShipInfoVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.constants.BBBPayPalConstants
import com.bbb.constants.TBSConstants
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.profile.session.BBBSavedItemsSessionBean
import com.bbb.profile.session.BBBSessionBean
import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.CommonConfiguration
import com.bbb.wishlist.GiftListVO

class BBBShippingGroupFormhandlerSpecification extends BBBExtendedSpec {
	
	BBBShippingGroupFormhandler shipObj 
	def BBBPurchaseProcessHelper purchaseProcessHelperMock = Mock()
	def BBBOrderManager orderManagerMock = Mock()
	def BBBPurchaseProcessHelper shipHelperMock = Mock()
	def CheckoutProgressStates pState = Mock()
	def PricingTools pTools = Mock()
	def CommonConfiguration cConfig = Mock()
	def OrderHolder orderHolderMock = Mock()
	def BBBOrderImpl orderMock = Mock()
	def BBBPayPalSessionBean payPalSessionBean = Mock()
	def BBBCatalogTools catalogUtilMock = Mock()
	def BBBSessionBean sesssionBeanMock = Mock()
	def LblTxtTemplateManager errorMSGMock = Mock()
	def TransactionManager tManager  = Mock()
	def RepeatingRequestMonitor repRequest = Mock()
	def Profile profileMock = Mock()
	def BBBShippingGroupManager sGroupManager = Mock()
	def BBBCheckoutManager checkoutManagerMock =Mock()
	def PromotionTools promotools = Mock()
	def PromotionLookupManager  pLooLupManager = Mock()
	def BBBCouponUtil  couponutil = Mock()
	def BBBProfileTools profileTools = Mock()
	def BBBProfileManager pManager =Mock()
	def BBBAddressAPI adrAPI = Mock()
	def BBBHardGoodShippingGroup hShip = Mock()
	def BBBStoreShippingGroup sShip = Mock()
	def BBBCommerceItemManager commerceItmeManagerMock = Mock()
	def BBBShippingGroupContainerService  shipServiceContainer = Mock()
	def BBBInventoryManager inventoryManagerMock = Mock()
	def BBBPaymentGroupManager paymentGroupManagr = Mock()
	def BBBSavedItemsSessionBean sessionitemBean = Mock()
	def BBBMultiShippingManager multiShippingManagerMock = Mock()
	def BBBPayPalServiceManager ppmanager = Mock()
	def CommerceItemShippingInfoTools cInfoTools = Mock()
	BBBGetCouponsManager cManager = Mock()
	Map propertyMap= [	"firstName":"First Name",
		"lastName":"Last Name",
		"address1":"Street Address",
		"city":"City",
		"state":"State",
		"country":"Country",
		"postalCode":"Zip Code",
		"phoneNumber":"Phone Number",
		"company":"Company",
		"address2":"Street 2 Address",
		"address3":"Apartment Number",
		"countryAndState":"Country and Sate combination",
		"phoneNumber":"Phone Number",
		"email":"Email",
		"alternatePhoneNumber":"Alternate Phone Number"]
	Map url = ["CART":"/checkout/shipping/shipping.jsp","SHIPPING_SINGLE":"/checkout/billing/billing.jsp",
"SHIPPING_MULTIPLE":"/checkout/billing/billing.jsp","BILLING":"/checkout/coupons/coupon.jsp","COUPONS":"/checkout/payment/billing_payment.jsp",
"PAYMENT":"/checkout/preview/review_cart.jsp","REVIEW":"/checkout/checkout_confirmation.jsp","GIFT":"/checkout/billing/billing.jsp",
"VALIDATION":"/cart/paypalRedirect.jsp","INTERMEDIATE_PAYPAL":"/cart/paypalRedirect.jsp?isCart=false","SP_GUEST":"/checkout/singlePage/ajax/guest_json.jsp",
"SP_CHECKOUT_SINGLE":"/checkout/checkout_single.jsp","SP_PAYPAL":"/checkout/singlePage/preview/review_cart.jsp","SP_SHIPPING_SINGLE":"/checkout/singlePage/ajax/singleShip_json.jsp",
"SP_SHIPPING_MULTIPLE":"/checkout/singlePage/ajax/multiShip_json.jsp","SP_BILLING":"/checkout/singlePage/ajax/billing_json.jsp","SP_COUPONS":"/checkout/singlePage/coupons/coupon.jsp",
"SP_PAYMENT":"/checkout/singlePage/payment/billing_payment.jsp","SP_REVIEW":"/checkout/checkout_confirmation.jsp","SP_GIFT":"/checkout/singlePage/ajax/gifting_json.jsp",]
	BBBAddressContainer addContainer = new BBBAddressContainer()
	Map map = ["firstName":"First Name","lastName":"Last Name","address1":"Street Address","city":"City","state":"State","country":"Country","postalCode":"Zip Code",
"phoneNumber":"Phone Number","company":"Company","address2":"Street 2 Address","address3":"Apartment Number",
"countryAndState":"Country and Sate combination",
"phoneNumber":"Phone Number","email":"Email","alternatePhoneNumber":"Alternate Phone Number"]
	def setup(){
		shipObj = new BBBShippingGroupFormhandler(commerceItemManager:commerceItmeManagerMock,inventoryManager:inventoryManagerMock,checkoutManager:checkoutManagerMock,messageHandler:errorMSGMock,catalogTools:catalogUtilMock,commonConfiguration:cConfig,purchaseProcessHelper:purchaseProcessHelperMock,orderManager:orderManagerMock,shippingHelper:shipHelperMock,checkoutProgressStates:pState)
		shipObj.setShoppingCart(orderHolderMock)
		shipObj.setPayPalSessionBean(payPalSessionBean)
		orderHolderMock.getCurrent() >> orderMock
		shipObj.setManager(sGroupManager)
		shipObj.setShippingGroupManager(sGroupManager)
		shipObj.setProfile(profileMock)
		shipObj.setTools(profileTools)
		shipObj.setMultiShippingManager(multiShippingManagerMock)
		shipObj.setShippingGroupContainerService(shipServiceContainer)
		shipObj.setSuccessUrlMap(["changeToShipOnline":"/checkout/shipping/shipping.jsp?shippingGr=multi"])
		shipObj.setErrorUrlMap(["changeToShipOnline":"/checkout/shipping/shipping.jsp?shippingGr=multi"])
		shipObj.setCommerceItemShippingInfoTools(cInfoTools)
		catalogUtilMock.getConfigValueByconfigType("ContentCatalogKeys") >> ["BedBathUSSiteCode":"BedBathUS","BuyBuyBabySiteCode":"BuyBuyBaby","BedBathCanadaSiteCode":"BedBathCanada"]
		requestMock.resolveName(BBBCoreConstants.SAVEDCOMP) >> sessionitemBean
		pState.setCheckoutSuccessURLs(url)
		pState.setCheckoutFailureURLs(url)
		shipObj.setPaypalServiceManager(ppmanager)
	} 
	
	def "This method to change the shipping group to store pickup shipping group"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> sShip
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		1*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, cItem, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
		1*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		1*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.isEmpty()
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 2 >> 2 >> 2 >> 3
		cItem.getCatalogRefId() >> "sku01234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> null
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, cItem, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.isEmpty()
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
		1*sessionMock.removeAttribute(BBBCoreConstants.COOKIE_USED_ONCE)
		1*orderHolderMock.deleteOrder(_)
	}
	def "This method to change the shipping group to store pickup shipping group and paypal flag is false "(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 2
		cItem.getCatalogRefId() >> "sku01234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> sShip
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		def Paypal paypal =Mock()
		def CreditCard cc = Mock()
		orderMock.getPaymentGroups() >> [cc,paypal]
		paypal.getId() >> "pg1234"
		def BBBRepositoryContactInfo repInfo = Mock() 
		repInfo.isFromPaypal() >> false
		orderMock.getBillingAddress() >> repInfo
		orderManagerMock.getPaymentGroupManager() >>paymentGroupManagr
		1*paymentGroupManagr.removePaymentGroupFromOrder(orderMock, "pg1234")
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, cItem, "st1234", "SG1234", true,_)
		1*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
		1*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.isEmpty()
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		2*orderManagerMock.updateOrder(orderMock)
	}
	def "This method to change the shipping group to store pickup and remove paypal payment group"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def CommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> hShip
		hShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		def Paypal paypal =Mock()
		orderMock.getPaymentGroups() >> [paypal]
		paypal.getId() >> "pg1234"
		def BBBRepositoryContactInfo repInfo = Mock() 
		repInfo.isFromPaypal() >> true
		orderMock.getBillingAddress() >> repInfo
		orderManagerMock.getPaymentGroupManager() >>paymentGroupManagr
		1*paymentGroupManagr.removePaymentGroupFromOrder(orderMock, "pg1234")
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, cItem, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
		1*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		2*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.size() == 1
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
		1*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
	}
	def "This method to change the shipping group to hard shipping group"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> hShip
		def CommerceItemRelationship itemRelShip = Mock() 
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 1
		List<CommerceItemRelationship> listItemRelships= [itemRelShip]
		hShip.getCommerceItemRelationships() >> listItemRelships 
		hShip.getId() >> "SG1234"
		Map orderAvailabilityMap = [:]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":1]
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, cItem, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.isEmpty()
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
	}
	def "This method to change the shipping group to store pickup shipping group for commerece item coount is zero"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setFromPage("formPage")
		shipObj.setRepeatingRequestMonitor(null)
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 0
		0*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, _, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, _, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		shipObj.getSuccessURL().equals(null)
		shipObj.getErrorURL().equals(null)
		shipObj.getSystemErrorURL().equals(null)
	}
	def "This method to change the shipping group to store pickup shipping group commerceItem id is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId(null)
		shipObj.setNewQuantity("2")
		Locale locale =new Locale("EN")
		shipObj.setUserLocale(locale)
		orderMock.getCommerceItemCount() >> 1
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, _, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, _, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.size() == 1
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group store id is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId(null)
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity("2")
		Locale locale =new Locale("EN")
		shipObj.setUserLocale(locale)
		orderMock.getCommerceItemCount() >> 1
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, _, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, _, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.size() == 1
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group quantity is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity(null)
		Locale locale =new Locale("EN")
		shipObj.setUserLocale(locale)
		orderMock.getCommerceItemCount() >> 1
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, _, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, _, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.size() == 1
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group,order is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		shipObj.setQueryParam("")
		shipObj.setRepeatingRequestMonitor(repRequest)
		Locale locale =new Locale("EN")
		shipObj.setUserLocale(locale)
		orderMock.getCommerceItemCount() >> 1
		Map orderAvailabilityMap = ["ci12345":2]
		def OrderHolder orderHolderMock1 = Mock()
		shipObj.setShoppingCart(orderHolderMock1)
		orderHolderMock1.getCurrent() >> null
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		0*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, _, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, _, "st1234")
		0*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		0*orderManagerMock.updateOrder(orderMock)
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.size() == 1
		shipObj.getSuccessURL().equals(null)
		shipObj.getErrorURL().equals(null)
		shipObj.getSystemErrorURL().equals(null)
	}
	def "This method to change the shipping group to store pickup shipping group throws runprocessException"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> sShip
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*inventoryManagerMock.getBOPUSProductAvailability( "BedBathUS","sku01234", ["st1234"], 2, BBBInventoryManager.ONLINE_STORE,_, true,null, false , false) >>  ["st1234":0]
		orderMock.getPaymentGroups() >> []
		1*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, 2, cItem, "st1234", "SG1234", true,_)
		0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
		1*orderManagerMock.updateAvailabilityMapInOrder(requestMock, orderMock)
		1*orderManagerMock.updateOrder(orderMock)
		purchaseProcessHelperMock.runRepricingProcess(_, _, orderMock, _, _, profileMock, _) >> {throw new RunProcessException("Mock of run process exception")}
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		orderAvailabilityMap.isEmpty()
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group throws Number Format exception"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("2")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		shipObj.preChangeToStorePickup(requestMock, responseMock) >> {throw new NumberFormatException("Mock of NumberFormat exception")}
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group is not unique request"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		requestMock.getObjectParameter("locale") >> "EN"
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group form error before syn block"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.getFormError() >> true
		requestMock.getObjectParameter("locale") >> "EN"
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "This method to change the shipping group to store pickup shipping group form error inside syn block"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setQueryParam("QueryParam")
		shipObj.setRepeatingRequestMonitor(repRequest)
		orderMock.getCommerceItemCount() >> 1
		shipObj.getFormError() >> false >> false >> true
		shipObj.preChangeToStorePickup(requestMock, responseMock) >> {}
		orderMock.getPaymentGroups() >> []
		shipObj.changeToStorePickup(requestMock, responseMock) >> {}
		requestMock.getObjectParameter("locale") >> "EN"
		when:
		shipObj.handleChangeToStorePickup(requestMock, responseMock)
		then:
		shipObj.isChangeStoreAfterSplitFlag()  == false
		shipObj.getSuccessURL().equals("QueryParam")
		shipObj.getErrorURL().equals("QueryParam")
		shipObj.getSystemErrorURL().equals("QueryParam")
	}
	def "The method to change the shipping group to online shipping group"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		def BBBCommerceItem cItem2 = Mock()
		def CommerceItem cItem3 = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		cItem1.getId() >> "ci12346"
		cItem1.getCatalogRefId() >> "sku01235"
		cItem2.getId() >> "ci12347"
		cItem2.getCatalogRefId() >> "sku01234"
		cItem2.isMsgShownOOS() >> true
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		shipServiceContainer.getShippingGroup("SG1234") >> sShip
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  0
		orderMock.getCommerceItems() >> [cItem,cItem3,cItem1,cItem2]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		2*orderManagerMock.updateOrder(orderMock)
		1*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
		shipObj.getSuccessURL()equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
		shipObj.getErrorURL()equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
		shipObj.getSystemErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	}
	def "The method to change the shipping group to online shipping group ship id is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 4
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId(null)
		shipServiceContainer.getShippingGroup("SG1234") >> sShip
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  0
		orderMock.getCommerceItems() >> [cItem]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
	}
	def "The method to change the shipping group to online shipping group ship commerce quantity is less than for given quantity"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 2
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId(null)
		shipServiceContainer.getShippingGroup("SG1234") >> sShip
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  0
		orderMock.getCommerceItems() >> [cItem]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
	}
	def "The method to change the shipping group to online shipping group ship commerce rel quantity is less than for given quantity"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 4
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getId() >> "ci123456"
		cItem1.getQuantity() >> 4
		cItem1.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		orderMock.getShippingGroup("SG1234") >> sShip
		def CommerceItemRelationship itemRelShip = Mock()
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 1
		def CommerceItemRelationship itemRelShip1 = Mock()
		itemRelShip1.getCommerceItem() >> cItem1
		itemRelShip1.getQuantity() >> 1
		List<CommerceItemRelationship> listItemRelships= [itemRelShip1,itemRelShip]
		sShip.getCommerceItemRelationships() >> listItemRelships
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		orderMock.getCommerceItems() >> [cItem]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
	}
	def "The method to change the shipping group to online shipping group ship commerce rel quantity is greather than for given quantity"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		def BBBCommerceItem cItem2 = Mock()
		def CommerceItem cItem3 = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		cItem1.getId() >> "ci12346"
		cItem1.getCatalogRefId() >> "sku01235"
		cItem2.getId() >> "ci12347"
		cItem2.getCatalogRefId() >> "sku01234"
		cItem2.isMsgShownOOS() >> false
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		orderMock.getShippingGroup("SG1234") >> sShip
		def CommerceItemRelationship itemRelShip = Mock()
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 4
		List<CommerceItemRelationship> listItemRelships= [itemRelShip]
		sShip.getCommerceItemRelationships() >> listItemRelships
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  0
		orderMock.getCommerceItems() >> [cItem,cItem1,cItem2,cItem3]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		GiftListVO gf = new GiftListVO()
		GiftListVO gf1 = new GiftListVO()
		gf.setMsgShownOOS(false)
		gf1.setMsgShownOOS(true)
		sessionitemBean.getItems() >> [gf,gf1]
		1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new CommerceException("Mock of commerce exception")}
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		2*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
		1*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem,null)
		1*commerceItmeManagerMock.setInventoryStatus(orderMock, _, cItem, BBBInventoryManager.STORE_ONLINE)
		1*cItem.setMsgShownOOS(true)
	}
	def "The method to change the shipping group to online shipping group ship commerce rel quantity is greather than for given quantity and gift list is empty from session bean"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		def BBBCommerceItem cItem2 = Mock()
		def CommerceItem cItem3 = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 3
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		cItem1.getId() >> "ci12346"
		cItem1.getCatalogRefId() >> "sku01235"
		cItem2.getId() >> "ci12347"
		cItem2.getCatalogRefId() >> "sku01234"
		cItem2.isMsgShownOOS() >> false
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		orderMock.getShippingGroup("SG1234") >> sShip
		def CommerceItemRelationship itemRelShip = Mock()
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 4
		List<CommerceItemRelationship> listItemRelships= [itemRelShip]
		sShip.getCommerceItemRelationships() >> listItemRelships
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		1*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  0
		orderMock.getCommerceItems() >> [cItem]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		sessionitemBean.getItems() >> []
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
		1*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem,null)
		1*commerceItmeManagerMock.setInventoryStatus(orderMock, _, cItem, BBBInventoryManager.STORE_ONLINE)
		0*cItem.setMsgShownOOS(true)
	}
	def "The method to change the shipping group to online shipping group IS hardgood SHIPP"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 4
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		orderMock.getShippingGroup("SG1234") >> hShip
		def CommerceItemRelationship itemRelShip = Mock()
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 1
		hShip.getCommerceItemRelationships() >> []
		hShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		orderMock.getCommerceItems() >> [cItem]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
	}
	def "The method to change the shipping group to online shipping group  commerce item id is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId(null)
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group new quantity is null"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity(null)
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group  commerceritem is null"(){
		given:
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setUserLocale(new Locale("EN"))
		shipObj.setRepeatingRequestMonitor(null)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity("3")
		def CommerceItem cItem = Mock()
		orderMock.getCommerceItem("ci1234") >> cItem
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		1*purchaseProcessHelperMock.runRepricingProcess(_, _, orderMock, _, _, profileMock, _) >> {throw new RunProcessException("Mock of run process exception")}
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group  commerce quantity is lessthan new quantity"(){
		given:
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setUserLocale(new Locale("EN"))
		shipObj.setRepeatingRequestMonitor(null)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity("3")
		def BBBCommerceItem cItem = Mock()
		cItem.getStoreId() >> "st1234"
		cItem.getQuantity() >> 2
		orderMock.getCommerceItem("ci1234") >> cItem
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 2
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group  commerce relation quantity is lessthan new quantity"(){
		given:
		shipObj.setFromPage(null)
		shipObj.setUserLocale(new Locale("EN"))
		shipObj.setRepeatingRequestMonitor(null)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity("3")
		def BBBCommerceItem cItem = Mock()
		cItem.getStoreId() >> "st1234"
		cItem.getQuantity() >> 4
		cItem.getId() >> "ci1234"
		cItem.isMsgShownOOS() >> true
		orderMock.getCommerceItem("ci1234") >> cItem
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 2
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		orderMock.getShippingGroup("SG1234") >> sShip
		def CommerceItemRelationship itemRelShip = Mock()
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 4
		sShip.getCommerceItemRelationships() >> [itemRelShip]
		sShip.getId() >> "SG1234"
		orderMock.getCommerceItems() >> [cItem]
		1*purchaseProcessHelperMock.checkInventory( "BedBathUS",_, null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group  store id is null"(){
		given:
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setUserLocale(new Locale("EN"))
		shipObj.setRepeatingRequestMonitor(null)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci1234")
		shipObj.setNewQuantity("3")
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() < 4
		cItem.isMsgShownOOS() >>  true
		orderMock.getCommerceItem("ci1234") >> cItem
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of commerce exception")}
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*cItem.setMsgShownOOS(true)
	}
	def "The method to change the shipping group to online shipping group IS store SHIP but relation ship is empty"(){
		given:
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		shipObj.setStoreId("st1234")
		shipObj.setCommerceItemId("ci12345")
		shipObj.setNewQuantity("3")
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		def BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci12345"
		cItem.getQuantity() >> 4
		cItem.getCatalogRefId() >> "sku01234"
		cItem.getStoreId() >> "st1234"
		orderMock.getCommerceItem("ci12345") >> cItem
		orderMock.getSiteId() >> "BedBathUS"
		shipObj.setOldShippingId("SG1234")
		orderMock.getShippingGroup("SG1234") >> sShip
		def CommerceItemRelationship itemRelShip = Mock()
		itemRelShip.getCommerceItem() >> cItem
		itemRelShip.getQuantity() >> 1
		sShip.getCommerceItemRelationships() >> []
		sShip.getId() >> "SG1234"
		Map orderAvailabilityMap = ["ci12345":2]
		orderMock.getAvailabilityMap() >> orderAvailabilityMap
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		orderMock.getCommerceItems() >> [cItem]
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
		0*cItem.setMsgShownOOS(true)
		orderAvailabilityMap.isEmpty()
	}
	def "The method to change the shipping group to online shipping group form error before Pre Method"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		shipObj.getFormError() >> true
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group form error before post Method"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		shipObj.getFormError() >> false >> false >> true
		shipObj.preChangeToShipOnline(requestMock, responseMock) >> {}
		shipObj.changeToShipOnline(requestMock, responseMock) >> {}
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "The method to change the shipping group to online shipping group  number format exception"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		shipObj.preChangeToShipOnline(requestMock, responseMock) >> {throw new NumberFormatException()}
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.setFromPage("changeToShipOnline")
		shipObj.setRepeatingRequestMonitor(repRequest)
		requestMock.getObjectParameter("locale") >> "EN"
		orderMock.getCommerceItemCount() >> 1
		0*purchaseProcessHelperMock.checkInventory( "BedBathUS","sku01234", null, orderMock,3,BBBInventoryManager.STORE_ONLINE,_,0) >>  1
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleChangeToShipOnline(requestMock, responseMock)
		then:
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip 
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1)
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group InvalidParameterException "(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		sBean.setShippingGroupId("asdfg")
		orderMock.getShippingGroup("asdfg") >> {throw new InvalidParameterException("Mock if InvalidParameterException")}
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean)
		List<BBBShippingInfoBean> beanList = [sBean]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group BBBSystem Exception"(){
		given:
		pState.getFailureURL() >> null
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1) >> {throw new BBBSystemException("Mock of BBBSystem exception")}
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		orderMock.isPayPalOrder() >> true
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group BBBBusinesss Exception"(){
		given:
		pState.getFailureURL() >> null
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1) >> {throw new BBBBusinessException("Mock of BBBBusiness exception")}
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		orderMock.isPayPalOrder() >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group commerce Exception"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1) >> {throw new CommerceException("Mock of commerce exception")}
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		orderMock.isPayPalOrder() >> true
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group ShippingGroupNotFoundException "(){
		given:
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		sBean.setShippingGroupId("SG12345")
		orderMock.getShippingGroup("SG12345") >> {throw new ShippingGroupNotFoundException("Mock if ShippingGroupNotFound Exception")}
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean)
		List<BBBShippingInfoBean> beanList = [sBean]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	
	def "This method either adds or removes gift messaging to existing shipping group is Store shipping group"(){
		given:
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		sBean.setShippingGroupId("SG12345")
		orderMock.getShippingGroup("SG12345") >> sShip
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean)
		List<BBBShippingInfoBean> beanList = [sBean]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> false
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group commerce exception on update order"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException()}
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1)
	}
	def "This method either adds or removes gift messaging to existing shipping group and paypal as payment group"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		orderMock.isPayPalOrder() >> true
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1)
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group and paypal as payment group from session bean"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		BBBShippingInfoBean sBean = new BBBShippingInfoBean()
		BBBShippingInfoBean sBean1 = new BBBShippingInfoBean()
		sBean1.setShippingGroupId("SG123456")
		orderMock.getShippingGroup("SG123456") >> hShip
		List<BBBShippingInfoBean> beanList = [null,sBean,sBean1]
		shipObj.setBBBShippingInfoBeanList(beanList)
		shipHelperMock.getPricingTools() >> pTools
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		orderMock.isPayPalOrder() >> true
		payPalSessionBean.isFromPayPalPreview() >> true
		pState.getCheckoutFailureURLs() >> ["REVIEW":"REVIEW"]
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, sBean1)
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "This method either adds or removes gift messaging to existing shipping group form error before giftmessage method"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		pState.getFailureURL() >> "atg-rest-ignore"
		shipObj.getFormError() >> true
		when:
		shipObj.handleGiftMessaging(requestMock, responseMock)
		then:
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, _)
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "This handler method will validate shipping address and apply to the input shipping group"(){
		given:
		shipObj.setPoBoxStatus("Y")
		shipObj.setPoBoxFlag("P")
		shipObj.setQasOnProfileAddress(true)
		shipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		adr.setState("OH")
		addContainer.addAddressToContainer("shipName", adr)
		shipObj.setShipToAddressName("shipName")
		checkoutManagerMock.getProfileAddressTool() >> adrAPI
		catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
		List error = new ArrayList()
		error.add(0, [])
		error.add(1,[])
		1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
		shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> error
		shipObj.setShippingAddressContainer(addContainer)
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		((BBBAddress)shipObj.getAddress()).isQasValidated()
		0*requestMock.setParameter("newAddressKey", _);
		1*adrAPI.updateAddressToProfile(profileMock, _, "shipName", _)
		2 * orderMock.getSiteId()
		1 * orderMock.getCommerceItems()
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
		((BBBAddress)shipObj.getAddress()).isQasValidated()
		((BBBAddress)shipObj.getAddress()).isPoBoxAddress()
	}
	def "This handler method will validate shipping address and apply to the input shipping group requierd first name and invaild last name"(){
		given:
		shipObj.setPoBoxStatus(null)
		shipObj.setPoBoxFlag(null)
		shipObj.setQasOnProfileAddress(true)
		shipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		adr.setState("OH")
		addContainer.addAddressToContainer("PROFILEText", adr)
		shipObj.setShipToAddressName("PROFILEText")
		checkoutManagerMock.getProfileAddressTool() >> adrAPI
		catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		List error = new ArrayList()
		error.add(0,["firstName"])
		error.add(1,["LastName"])
		0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
		shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> error
		shipObj.setShippingAddressContainer(addContainer)
		1*adrAPI.updateAddressToProfile(profileMock, _, "Text", _) >> {throw new BBBSystemException("Mock of System Exception")}
		Address newadr = new BBBAddressVO()
		shipObj.setAddress(newadr)
		2*shipHelperMock.getAddressPropertyNameMap() >> propertyMap
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		((BBBAddress)shipObj.getAddress()).isQasValidated()
		0*requestMock.setParameter("newAddressKey", _);
		1 * orderMock.getSiteId()
		0 * orderMock.getCommerceItems()
		1 * errorMSGMock.getErrMsg('err_am_cc_po_box', 'EN', null)
		1 * errorMSGMock.getErrMsg('MSG_MISSING_REQUIRED_ADDRESS_PROPERTY', 'EN', null)
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
		addContainer.getAddressFromContainer("PROFILEText").getState() == null
		((BBBAddress)shipObj.getAddress()).isQasValidated() == true
		((BBBAddress)shipObj.getAddress()).isPoBoxAddress() == false
	}
	def "This handler method will validate shipping address and apply to the input shipping group save address in profile"(){
		given:
		shipObj.setPoBoxStatus("N")
		shipObj.setPoBoxFlag("S")
		shipObj.setQasOnProfileAddress(false)
		shipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		adr.setState("OH")
		adr.setAddress1("1076 parsons ave")
		addContainer.addAddressToContainer("PROFILEText", adr)
		shipObj.setShipToAddressName("PROFILEText")
		checkoutManagerMock.getProfileAddressTool() >> adrAPI
		catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		List error = new ArrayList()
		error.add(0,[])
		error.add(1,[])
		1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
		shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> error
		shipObj.setShippingAddressContainer(addContainer)
		0*adrAPI.updateAddressToProfile(profileMock, _, "Text", _)
		Address newadr = new BBBAddressVO()
		newadr.setAddress1("1076 parsons ave")
		shipObj.setAddress(newadr)
		0*shipHelperMock.getAddressPropertyNameMap() >> propertyMap
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setSaveShippingAddress(true)
		1*checkoutManagerMock.saveAddressToProfile(profileMock, newadr, _) >> newadr
		shipObj.setCisiIndex("-1")
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		1*requestMock.setParameter("newAddressKey", _);
		2 * orderMock.getSiteId()
		1 * orderMock.getCommerceItems()
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
		addContainer.getAddressFromContainer("PROFILEText").getState().equals("OH")
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		((BBBAddress)shipObj.getAddress()).isPoBoxAddress() == false
	}
	def "This handler method will validate shipping address and apply to the input shipping group system exception"(){
		given:
		shipObj.setPoBoxStatus("N")
		shipObj.setPoBoxFlag("S")
		shipObj.setQasOnProfileAddress(false)
		shipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		adr.setState("OH")
		adr.setAddress1("1076 parsons ave")
		addContainer.addAddressToContainer("PROFILEText", adr)
		shipObj.setShipToAddressName("PROFILEText")
		checkoutManagerMock.getProfileAddressTool() >> adrAPI
		catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		List error = new ArrayList()
		error.add(0,[])
		error.add(1,[])
		1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
		shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> error
		shipObj.setShippingAddressContainer(addContainer)
		0*adrAPI.updateAddressToProfile(profileMock, _, "Text", _)
		Address newadr = new BBBAddressVO()
		newadr.setAddress1("1076 parsons ave")
		shipObj.setAddress(newadr)
		0*shipHelperMock.getAddressPropertyNameMap() >> propertyMap
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setSaveShippingAddress(true)
		1*checkoutManagerMock.saveAddressToProfile(profileMock, newadr, _) >> {throw new BBBSystemException("Mock of system exception")}
		shipObj.setCisiIndex("-1")
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		0*requestMock.setParameter("newAddressKey", _);
		2 * orderMock.getSiteId()
		1 * orderMock.getCommerceItems()
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
		addContainer.getAddressFromContainer("PROFILEText").getState().equals("OH")
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		((BBBAddress)shipObj.getAddress()).isPoBoxAddress() == false
	}
	def "This handler method will validate shipping address and apply to the input shipping group Business exception"(){
		given:
		shipObj.setPoBoxStatus("N")
		shipObj.setPoBoxFlag("S")
		shipObj.setQasOnProfileAddress(false)
		shipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		adr.setState("OH")
		adr.setAddress1("1076 parsons ave")
		addContainer.addAddressToContainer("PROFILEText", adr)
		shipObj.setShipToAddressName("PROFILEText")
		checkoutManagerMock.getProfileAddressTool() >> adrAPI
		catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		List error = new ArrayList()
		error.add(0,[])
		error.add(1,[])
		1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
		shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> error
		shipObj.setShippingAddressContainer(addContainer)
		0*adrAPI.updateAddressToProfile(profileMock, _, "Text", _)
		Address newadr = new BBBAddressVO()
		newadr.setAddress1("1076 parsons ave")
		shipObj.setAddress(newadr)
		0*shipHelperMock.getAddressPropertyNameMap() >> propertyMap
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		shipObj.setSaveShippingAddress(true)
		1*checkoutManagerMock.saveAddressToProfile(profileMock, newadr, _) >> {throw new BBBBusinessException("Mock of Business exception")}
		shipObj.setCisiIndex("-1")
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		0*requestMock.setParameter("newAddressKey", _);
		2 * orderMock.getSiteId()
		1 * orderMock.getCommerceItems()
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
		addContainer.getAddressFromContainer("PROFILEText").getState().equals("OH")
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		((BBBAddress)shipObj.getAddress()).isPoBoxAddress() == false
	}
	def "This handler method will validate shipping address and apply to the input shipping group commerce index is 1"(){
		given:
		shipObj.setPoBoxStatus("N")
		shipObj.setPoBoxFlag("S")
		shipObj.setQasOnProfileAddress(false)
		shipObj.setAddressContainer(addContainer)
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		BBBAddress adr = new BBBAddressVO()
		def CommerceItemShippingInfo shipInfo = Mock()
		def CommerceItemShippingInfo shipInfo1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		adr.setState("OH")
		adr.setAddress1("1076 parsons ave")
		addContainer.addAddressToContainer("PROFILEText", adr)
		shipObj.setShipToAddressName("PROFILEText")
		checkoutManagerMock.getProfileAddressTool() >> adrAPI
		catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		List error = new ArrayList()
		error.add(0,[])
		error.add(1,[])
		0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
		1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> error
		shipObj.setShippingAddressContainer(addContainer)
		0*adrAPI.updateAddressToProfile(profileMock, _, "Text", _)
		Address newadr = new BBBAddressVO()
		newadr.setAddress1("1076 parsons ave")
		shipObj.setAddress(newadr)
		0*shipHelperMock.getAddressPropertyNameMap() >> propertyMap
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setSaveShippingAddress(false)
		0*checkoutManagerMock.saveAddressToProfile(profileMock, newadr, _)
		1*checkoutManagerMock.canItemShipToCiSiIndexAddress(_, shipInfo1, newadr) >> true
		shipObj.setCisiIndex("1")
		def CommerceItemShippingInfoContainer infoContainer = Mock() 
		shipObj.setCommerceItemShippingInfoContainer(infoContainer)
		shipInfo.getCommerceItem() >> cItem
		shipInfo1.getCommerceItem() >> cItem1
		infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		1*requestMock.setParameter("newAddressKey", _);
		1 * orderMock.getSiteId()
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
		addContainer.getAddressFromContainer("PROFILEText").getState().equals("OH")
		((BBBAddress)shipObj.getAddress()).isQasValidated() == false
		((BBBAddress)shipObj.getAddress()).isPoBoxAddress() == false
		1*shipInfo1.setShippingGroupName(_)
	}
	def "This handler method will validate shipping address and apply to the input shipping group form multiple request"(){
		given:
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> false
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
	}
	def "This handler method will validate shipping address and apply to the input shipping group before pre method have from error"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.getFormError() >> true
		shipObj.setAddNewAddressURL("AddNewAddressURL")
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
	}
	def "This handler method will validate shipping address and apply to the input shipping group system exception in pre method"(){
		given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.getFormError() >> false
		shipObj.setAddNewAddressURL("AddNewAddressURL")
		shipObj.preAddNewAddress(requestMock, responseMock) >> {throw new SystemException("Mock of system exception")}
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
	}
def "This handler method will validate shipping address and apply to the input shipping group after post method have from error"(){
	given:
		shipObj = Spy()
		spyObjIntilization(shipObj)
		shipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		shipObj.getFormError() >> false >> false >> false >> true
		shipObj.preAddNewAddress(requestMock, responseMock) >> {}
		shipObj.addNewAddress(requestMock, responseMock) >> {}
		shipObj.setAddNewAddressURL("AddNewAddressURL") 
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		shipObj.handleRestAddNewAddress(requestMock, responseMock)
		then:
		1 * pState.setCurrentLevel('SHIPPING_MULTIPLE')
}
def "Update Hold and Pack Info method for REST API"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> BBBCoreConstants.SITE_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "null"
	shipObj.setPackNHold(true)
	0*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	1*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	1*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1*orderManagerMock.updateOrder(orderMock)
	2*hShip.setShipOnDate(_)
	}
def "Update Hold and Pack Info method for REST API college id is null"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> BBBCoreConstants.SITE_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> false
	shipObj.setPackNHoldDate(null)
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	0 * orderManagerMock.updateOrder(orderMock)
	1 * hShip.setShipOnDate(_)
	4 * shipObj.getOrder()
	}
def "Update Hold and Pack Info method for REST API parse exception"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> BBBCoreConstants.SITE_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	0*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> false
	shipObj.setPackNHoldDate("asasa")
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	0*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	shipObj.preUpdateHoldNPackInfo(requestMock) >> {}
	1 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	0 * hShip.setShipOnDate(_)
	2 * shipObj.getOrder()
	}
def "Update Hold and Pack Info method for REST API parse exception in pre method"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> BBBCoreConstants.SITE_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate("asasa")
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MaobileApp"
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	4 * shipObj.getOrder()
	}
def "Update Hold and Pack Info method for REST API pack date is null"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> BBBCoreConstants.SITE_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	0*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> false
	shipObj.setPackNHoldDate(null)
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	0*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	shipObj.preUpdateHoldNPackInfo(requestMock) >> {}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	0 * hShip.setShipOnDate(_)
	2 * shipObj.getOrder()
	1 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse expiry pack date"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> TBSConstants.SITE_TBS_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(-2, "MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	4 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse pack date is null"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> TBSConstants.SITE_TBS_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(null)
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	4 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse not holded for packing"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> "BedBathUS"
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	1*catalogUtilMock.isPackNHoldWindow(_, _) >> false
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	4 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse shipping option is different"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> "BedBathUS"
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception()}
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	1*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard1")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	5 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse shipping option is default shipping method is null"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> "BedBathUS"
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception()}
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	1*catalogUtilMock.getDefaultShippingMethod(_) >> null
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	5 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse shipping option is default shipping method id is null"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> "BedBathUS"
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception()}
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	1*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO()
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	5 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API parse shipping option is default shipping method throw exception"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> "BedBathUS"
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception()}
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	1*catalogUtilMock.getDefaultShippingMethod(_) >> {throw new Exception()}
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	0 * orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1 * hShip.setShipOnDate(_)
	5 * shipObj.getOrder()
	0 * orderManagerMock.updateOrder(orderMock)
	}
def "Update Hold and Pack Info method for REST API pack is false"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> BBBCoreConstants.SITE_BAB_CA
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> null
	shipObj.setPackNHold(false)
	0*checkoutManagerMock.hasEvenSingleCollegeItem(_, orderMock) >> true
	shipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
	requestMock.getLocale() >> new Locale("EN")
	0*catalogUtilMock.isPackNHoldWindow(_, _) >> true
	shipHelperMock.getCatalogTools() >> catalogUtilMock
	shipObj.setShippingOption("Standard")
	0*catalogUtilMock.getDefaultShippingMethod(_) >> new ShipMethodVO(shipMethodId:"Standard")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1*orderManagerMock.updateOrder(orderMock)
	1*hShip.setShipOnDate(_)
	4 * shipObj.getOrder()
	}
def "Update Hold and Pack Info method for REST API throws exception"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.preUpdateHoldNPackInfo(requestMock) >> {throw new Exception()}
	requestMock.getLocale() >> new Locale("EN")
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	1*orderManagerMock.updateOrder(orderMock)
	0*hShip.setShipOnDate(_)
	2 * shipObj.getOrder()
	}
def "Update Hold and Pack Info method for REST API multiplr request"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> false
	requestMock.getLocale() >> new Locale("EN")
	when:
	shipObj.handleUpdateHoldNPackInfo(requestMock, responseMock)
	then:
	0*orderManagerMock.updateOrder(orderMock)
	0*hShip.setShipOnDate(_)
	0 * shipObj.getOrder()
	}
def "Populates ShippingInfoBean from the input gift message "(){
	given:
	cConfig.isLogDebugEnableOnCartFormHandler() >>  true
	shipObj.setGiftMsgAPIDelimiter(";")
	shipObj.setGiftMsgAPIPropertiesDelimiter(":")
	shipObj.setGiftMessageInput("sg1234:s:true:true")
	shipObj.setSiteId("BedBathUS")
	shipObj.checkFormRedirect(null, _, requestMock, responseMock) >> false
	shipHelperMock.getPricingTools() >> pTools
	when:
	shipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
	then:
	BBBShippingInfoBean shipBeanInfo = shipObj.getBBBShippingInfoBeanList().get(0)
	shipBeanInfo.getShippingGroupId().equals("sg1234")
	shipBeanInfo.getGiftMessage().equals("s")
	shipBeanInfo.isGiftingFlag()
	shipBeanInfo.getGiftWrap()
	1*requestMock.setContextPath("")
	shipObj.getSiteId().equals(null)
	1*pState.setCheckoutFailureURLs([:])
}
def "Populates ShippingInfoBean from the input gift bean proprties length is not equal to four "(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	cConfig.isLogDebugEnableOnCartFormHandler() >>  true
	shipObj.setGiftMsgAPIDelimiter(";")
	shipObj.setGiftMsgAPIPropertiesDelimiter(":")
	shipObj.setGiftMessageInput(";a:s:d:f;")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	when:
	shipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
	then:
	1*shipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
}
def "Populates ShippingInfoBean from the input gift bean second propertie is true "(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	cConfig.isLogDebugEnableOnCartFormHandler() >>  true
	shipObj.setGiftMsgAPIDelimiter(";")
	shipObj.setGiftMsgAPIPropertiesDelimiter(":")
	shipObj.setGiftMessageInput("a:true:d:f;")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
	when:
	shipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
	then:
	1*shipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
}
def "Populates ShippingInfoBean from the input gift bean second propertie is false "(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	cConfig.isLogDebugEnableOnCartFormHandler() >>  true
	shipObj.setGiftMsgAPIDelimiter(";")
	shipObj.setGiftMsgAPIPropertiesDelimiter(":")
	shipObj.setGiftMessageInput(":a:false:f;")
	when:
	shipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
	then:
	1*shipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
}
def "Populates ShippingInfoBean from the input gift bean first  propertie is blank "(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	cConfig.isLogDebugEnableOnCartFormHandler() >>  true
	shipObj.setGiftMsgAPIDelimiter(";")
	shipObj.setGiftMsgAPIPropertiesDelimiter(":")
	shipObj.setGiftMessageInput(":a:true:f;")
	when:
	shipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
	then:
	1*shipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
}
def "Populates ShippingInfoBean from the input gift message string have exception"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	cConfig.isLogDebugEnableOnCartFormHandler() >>  true
	when:
	shipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
	then:
	1*shipObj.logError("Input string is empty")
}
def "Validate if order object contains sku which is state restricted for shipping address selected"(){
	given:
	orderMock.getShippingGroups() >> [hShip]
	def BBBRepositoryContactInfo repC = Mock()
	hShip.getShippingAddress() >> repC
	List error = new ArrayList()
	error.add(0, [])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(_, requestMock) >> error
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
	requestMock.getHeader("X-bbb-channel")>>"MobileApp"
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	repC.getPostalCode() >> "43206"
	repC.getState() >> "OH"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	hShip.getShippingMethod() >> "Standard"
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> {throw new BBBSystemException("Mock of SystemException")}
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	1*checkoutManagerMock.canItemShipByMethod(_, _, "Standard") >> false
	shipObj.setLTLCommerceItem(true)
	when:
	boolean success = shipObj.handleValidateOrderForShippingRestriction(requestMock, responseMock)
	then:
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	success 
}
def "Validate if order object contains sku which is state restricted for shipping address selected for MobileWeb"(){
	given:
	orderMock.getShippingGroups() >> [hShip]
	def BBBRepositoryContactInfo repC = Mock()
	hShip.getShippingAddress() >> repC
	List error = new ArrayList()
	error.add(0, [])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(_, requestMock) >> error
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
	requestMock.getHeader("X-bbb-channel")>>"MobileWeb"
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	repC.getPostalCode() >> "43206"
	repC.getState() >> "OH"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	hShip.getShippingMethod() >> "Standard"
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >>true
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	0*checkoutManagerMock.canItemShipByMethod(_, _, "Standard") >> false
	shipObj.setLTLCommerceItem(true)
	when:
	boolean success = shipObj.handleValidateOrderForShippingRestriction(requestMock, responseMock)
	then:
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	success == false
}
def "Validate if order object contains sku which is state restricted for shipping address selected for MobileWeb is singlePageCheckoutEnabled disabled"(){
	given:
	orderMock.getShippingGroups() >> [hShip]
	def BBBRepositoryContactInfo repC = Mock()
	hShip.getShippingAddress() >> repC
	List error = new ArrayList()
	error.add(0, [])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(_, requestMock) >> error
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
	requestMock.getHeader("X-bbb-channel")>>"MobileWeb"
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	repC.getPostalCode() >> "43206"
	repC.getState() >> "OH"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	hShip.getShippingMethod() >> "Standard"
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >>true
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	1*checkoutManagerMock.canItemShipByMethod(_, _, "Standard") >> false
	shipObj.setLTLCommerceItem(true)
	when:
	boolean success = shipObj.handleValidateOrderForShippingRestriction(requestMock, responseMock)
	then:
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	success
}
def "Validate if order object contains sku which is state restricted for shipping address selected for MobileWeb is singlePageCheckoutEnabled null"(){
	given:
	orderMock.getShippingGroups() >> [hShip]
	def BBBRepositoryContactInfo repC = Mock()
	hShip.getShippingAddress() >> repC
	List error = new ArrayList()
	error.add(0, [])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(_, requestMock) >> error
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
	requestMock.getHeader("X-bbb-channel")>>"MobileWeb"
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> null
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	repC.getPostalCode() >> "43206"
	repC.getState() >> "OH"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	hShip.getShippingMethod() >> "Standard"
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >>true
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	1*checkoutManagerMock.canItemShipByMethod(_, _, "Standard") >> false
	shipObj.setLTLCommerceItem(true)
	when:
	boolean success = shipObj.handleValidateOrderForShippingRestriction(requestMock, responseMock)
	then:
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	success
}
def "Validate if order object contains sku which is state restricted for shipping address selected for DesktopWeb "(){
	given:
	orderMock.getShippingGroups() >> [hShip]
	def BBBRepositoryContactInfo repC = Mock()
	hShip.getShippingAddress() >> repC
	List error = new ArrayList()
	error.add(0, [])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(_, requestMock) >> error
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
	requestMock.getHeader("X-bbb-channel")>> null
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> null
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	repC.getPostalCode() >> "43206"
	repC.getState() >> "OH"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	hShip.getShippingMethod() >> "Standard"
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >>true
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	1*checkoutManagerMock.canItemShipByMethod(_, _, "Standard") >> false
	shipObj.setLTLCommerceItem(true)
	shipObj.setLoggingError(false)
	when:
	boolean success = shipObj.handleValidateOrderForShippingRestriction(requestMock, responseMock)
	then:
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	success
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo objects."(){
	given:
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setCisiIndex("1")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	1*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	1*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	1*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer)
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	1*orderManagerMock.updateOrder(orderMock)
	1*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo objects cisindex is -1."(){
	given:
	shipObj.setFromPage(null)
	pState.getFailureURL() >> null
	shipObj.setCisiIndex("-1")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	0*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	0*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	0*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer)
	1*orderManagerMock.updateOrder(orderMock) >> {throw new Exception()}
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals(null)
	shipObj.getShipToMultiplePeopleErrorURL().equals(null)
	1*pState.setCurrentLevel("SHIPPING_MULTIPLE")
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIP_TO_MULTIPLE,"EN", null)
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo objects quantity is zeros."(){
	given:
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setCisiIndex("1")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> null
	shipInfo1.getQuantity() >> 0
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	0*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	0*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	1*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer) >> {throw new CommerceException("Mock of commerce exception")}
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIP_TO_MULTIPLE,"EN", null)
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group name not available in container."(){
	given:
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setCisiIndex("1")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	0*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	0*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	1*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer) >> {throw new BBBSystemException("Mock of system MSG")}
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIP_TO_MULTIPLE,"EN", null)
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group business exception ."(){
	given:
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setCisiIndex("1")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	cItem1.isLtlItem() >> true
	shipHelperMock.getPricingTools() >> pTools
	0*pTools.priceOrderSubtotalShipping(orderMock, _,_, profileMock, [:]) >> {throw new PricingException("Mock of PricingException")}
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	0*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	0*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	1*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer) >> {throw new BBBBusinessException("Mock of Business MSG")}
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIP_TO_MULTIPLE,"EN", null)
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group NumberFormat exception ."(){
	given:
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setCisiIndex("1")
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	cItem1.isLtlItem() >> true
	shipHelperMock.getPricingTools() >> pTools
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	0*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	0*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	1*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer) >> {throw new NumberFormatException("Mock of NumberFormat MSG")}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIP_TO_MULTIPLE,"EN", null)
}
def"This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group PricingException exception ."(){
	given:
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setCisiIndex("1")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	cItem1.isLtlItem() >> true
	shipHelperMock.getPricingTools() >> pTools
	1*pTools.priceOrderSubtotalShipping(orderMock, _,_, profileMock, [:]) >> {throw new PricingException("Mock of PricingException")}
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setMultiShippingAddrCont(addContainer)
	addContainer.addAddressToContainer("shipGrpName1", new BBBAddressVO())
	requestMock.getParameter("cisiShipGroupName") >> "cisiShipGroupName"
	0*shipServiceContainer.getShippingGroup("cisiShipGroupName") >> hShip
	0*shipServiceContainer.addShippingGroup("shipGrpName1", hShip)
	1*sGroupManager.splitCommerceItemShippingInfo(shipInfo1, orderMock, shipServiceContainer) 
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def "This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group multi request from browser"(){
	given:
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> false
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals(null)
	shipObj.getShipToMultiplePeopleErrorURL().equals(null)
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def "This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group form exception after pre method"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> false >> true
	shipObj.preShipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals(null)
	shipObj.getShipToMultiplePeopleErrorURL().equals(null)
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def "This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group form exception before pre method"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> true >> true
	shipObj.preShipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def "This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group form exception after ShipToMultiplePeople method"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> false >> false >> true
	shipObj.preShipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.shipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def "This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group form exception before post method"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> false >> false >> false >> true
	shipObj.preShipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.shipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	0*orderManagerMock.updateOrder(orderMock)
	0*pState.setCurrentLevel("SHIPPING_MULTIPLE")
}
def "This handler method will This method breaks the CommerceItemShippingInfo object and creates multiple CommerceItemShippingInfo shipping group throw exception in post method"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.setFromPage("changeToShipOnline")
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.getFormError() >> false >> false >> false
	shipObj.preShipToMultiplePeople(requestMock, responseMock) >> { }
	shipObj.shipToMultiplePeople(requestMock, responseMock) >> {throw new Exception() }
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleSplitCurrentItem(requestMock, responseMock)
	then:
	shipObj.getShipToMultiplePeopleSuccessURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	shipObj.getShipToMultiplePeopleErrorURL().equals("null/checkout/shipping/shipping.jsp?shippingGr=multi")
	1*orderManagerMock.updateOrder(orderMock)
	1*pState.setCurrentLevel("SHIPPING_MULTIPLE")
	1*errorMSGMock.getErrMsg("err_shipping_multiple_people","EN", null)
}
def"This method either adds or removes gift messages : REST API"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false) >> {}
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	when:
	shipObj.handleMultiShippingDisplayAPI(requestMock, responseMock)
	then:
	shipObj.getCiInfo() == shipInfo1
	List infoList = shipObj.getCommerceItemShipInfoVOList()
	infoList.size() == 2
	((CommerceItemShipInfoVO)infoList.get(0)).getShippingGroupName().equals("shipGrpName")
	((CommerceItemShipInfoVO)infoList.get(0)).getShippingMethod().equals("shippingMethod")
	((CommerceItemShipInfoVO)infoList.get(1)).getShippingGroupName().equals("shipGrpName1")
	((CommerceItemShipInfoVO)infoList.get(1)).getShippingMethod().equals("shippingMethod1")
	}
def"This method is to reset mulit shipping group"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	1*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	1*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	
}
def"This method is to reset mulit shipping group new quantity is greater than actuall quantity"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 4 >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	
}
def"This method is to reset mulit shipping group new quantity is greater than old shipping id is null"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2 >> 4
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	0*sGroupManager.removeEmptyShippingGroups(orderMock)
	0*orderManagerMock.updateOrder(orderMock)
	Exception excp =thrown()
	
}
def"This method is to reset mulit shipping group new quantity is greater than old shipping id is not null"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2 >> 2>> 4
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipServiceContainer.getShippingGroup("sg1234") >> sShip
	shipObj.setOldShippingId("sg1234")
	shipObj.setChangeStoreAfterSplitFlag(false)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	
}
def"This method is to reset mulit shipping group new quantity is greater than old shipping group relation ship is empty"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2 >> 2>> 4
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipServiceContainer.getShippingGroup("sg1234") >> hShip
	shipObj.setOldShippingId("sg1234")
	shipObj.setChangeStoreAfterSplitFlag(false)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	hShip.getCommerceItemRelationships() >> []
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	
}
def"This method is to reset mulit shipping group new quantity is greater than old shipping group diff commerce item ids"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2 >> 2>> 4
	cItem.getId( ) >> "ci1234" >> "ci12345"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	CommerceItemShippingInfo shipInfo = Mock()
	CommerceItemShippingInfo shipInfo1 = Mock()
	BBBCommerceItem cItem2 = Mock()
	BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipServiceContainer.getShippingGroup("sg1234") >> hShip
	shipObj.setOldShippingId("sg1234")
	shipObj.setChangeStoreAfterSplitFlag(false)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	cConfig.isLoggingDebugForRequestScopedComponents() >> false
	CommerceItemRelationship relMokc = Mock()
	relMokc.getCommerceItem() >> cItem
	hShip.getCommerceItemRelationships() >> [relMokc]
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	
}
def"This method is to reset mulit shipping group new quantity is greater than old shipping group relation qunatity is greate than item quantity"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234",storeId:"st1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2 >> 2 >> 4
	cItem.getId( ) >> "ci1234" 
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	1*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	1*commerceItmeManagerMock.splitCommerceItemByQuantity(orderMock, *_)
	shipHelperMock.getPricingTools() >> pTools
	CommerceItemShippingInfo shipInfo = Mock()
	CommerceItemShippingInfo shipInfo1 = Mock()
	BBBCommerceItem cItem2 = Mock()
	BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipServiceContainer.getShippingGroup("sg1234") >> hShip
	shipObj.setOldShippingId("sg1234")
	shipObj.setChangeStoreAfterSplitFlag(false)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	cConfig.isLoggingDebugForRequestScopedComponents() >> false
	CommerceItemRelationship relMokc = Mock()
	relMokc.getCommerceItem() >> cItem
	hShip.getCommerceItemRelationships() >> [relMokc]
	relMokc.getQuantity() >> 4
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	
}
def"This method is to reset mulit shipping group throws commercer exception"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setErrorURL("errorurl")
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	0*sGroupManager.removeEmptyShippingGroups(orderMock)
	0*orderManagerMock.updateOrder(orderMock)
	Exception ex = thrown()
	ex.getMessage().equals(null)
	
}
def"This method is to reset mulit shipping group create hard good shipping group"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",addressId:"adr1234",packAndHoldDate:addDays(1, "mm/dd/yyyy"))]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> "true"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	1*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', null)
	0*hShip.setIsFromPaypal(true)
	0*hShip.setAddressStatus(_)
	1*hShip.setShipOnDate(_)
	addContainer.getNewAddressKey().equals(null)
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	1*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	1 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
}

def"This method is to reset mulit shipping group create hard good shipping group redirect state has coupon"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	shipObj.setUserProfile(profileMock)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",addressId:"adr1234",packAndHoldDate:"asdf")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["qasValidated": true,"poBoxAddress":true])
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> "true"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	1*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	orderMock.isPayPalOrder() >> true
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	1*ppmanager.validateCoupons(orderMock, profileMock) >> true
	profileMock.isTransient() >> true
	1*ppmanager.validateShippingMethod(orderMock, profileMock) >> "2a"
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1 * errorMSGMock.getErrMsg('err_paypal_shipping_method_change_expedit', 'EN', null) >>"err_paypal_shipping_method_change_expedit"
	1 * purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,hShip, null, _) >>  {throw new BBBBusinessException("Mock of Business exception")}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', null)
	1 * hShip.setPropertyValue('isFromPaypal', true)
	1 * hShip.setPropertyValue('addressStatus', null)
	0*hShip.setShipOnDate(_)
	addContainer.getNewAddressKey().equals(null)
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	1*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	1 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	shipObj.isShippingGroupChanged()
	shipObj.getShippingGroupErrorMsg().equals("err_paypal_shipping_method_change_expedit")
}
def"This method is to reset mulit shipping group create hard good shipping group non registry address"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	shipObj.setUserProfile(profileMock)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",packAndHoldDate:"asdf")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["qasValidated": true,"poBoxAddress":true])
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> null
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	0*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	orderMock.isPayPalOrder() >> true
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1 * errorMSGMock.getErrMsg(shipObj.ERROR_MULTIPLE_SHIPPING,"EN", null) >>"err_shipping_multiple_shipping"
	1 * purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,hShip, null, _) >>  {throw new BBBSystemException("Mock of Business exception")}
	1*ppmanager.validateCoupons(orderMock, _) >> true
	1*ppmanager.validateShippingMethod(orderMock, profileMock) >> "1a"
	1*errorMSGMock.getErrMsg(BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPRESS, 'EN', null) >> "err_paypal_shipping_method_change_express"
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:]) >> {throw new Exception()}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', _)
	1 * hShip.setPropertyValue('isFromPaypal', true)
	1 * hShip.setPropertyValue('addressStatus', null)
	0*hShip.setShipOnDate(_)
	addContainer.getNewAddressKey().isEmpty() == false
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	shipObj.isShippingGroupChanged()
	shipObj.getShippingGroupErrorMsg().equals("err_paypal_shipping_method_change_express")
	0*checkoutManagerMock.saveAddressToProfile(*_)
}
def"This method is to reset mulit shipping group create hard good shipping group non registry address save to profile"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	shipObj.setUserProfile(profileMock)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",packAndHoldDate:"asdf")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["qasValidated": true,"poBoxAddress":true])
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> null
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	0*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBSystemException("mock of system exception")}
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	orderMock.isPayPalOrder() >> true
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	profileMock.isTransient() >> false
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1 * errorMSGMock.getErrMsg(shipObj.ERROR_MULTIPLE_SHIPPING,"EN", null) >>"err_shipping_multiple_shipping"
	1 * purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,hShip, null, _) >>  {throw new BBBSystemException("Mock of Business exception")}
	1*ppmanager.validateCoupons(orderMock, _) >> true
	1*ppmanager.validateShippingMethod(orderMock, profileMock) >> "3a"
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:]) >> {throw new Exception()}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	1*checkoutManagerMock.saveAddressToProfile(profileMock, _, _) >> adrnew
	shipObj.setUserProfile(profileMock)
	shipObj.setSaveShippingAddress(true)
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', _)
	1 * hShip.setPropertyValue('isFromPaypal', true)
	1 * hShip.setPropertyValue('addressStatus', null)
	0*hShip.setShipOnDate(_)
	addContainer.getNewAddressKey().isEmpty() == false
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	shipObj.isShippingGroupChanged() == false
	shipObj.getShippingGroupErrorMsg().equals(null)
}
def"This method is to reset mulit shipping group create hard good shipping group non registry address save to profile and preview is not single checkout"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	shipObj.setUserProfile(profileMock)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",packAndHoldDate:"asdf")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["qasValidated": true,"poBoxAddress":true])
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> null
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	0*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBSystemException("mock of system exception")}
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	0*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	orderMock.isPayPalOrder() >> true
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	profileMock.isTransient() >> false
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1 * errorMSGMock.getErrMsg(shipObj.ERROR_MULTIPLE_SHIPPING,"EN", null) >>"err_shipping_multiple_shipping"
	0*ppmanager.validateCoupons(orderMock, _) >> true
	0*ppmanager.validateShippingMethod(orderMock, profileMock) >> "3a"
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:]) >> {throw new Exception()}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	1*checkoutManagerMock.saveAddressToProfile(profileMock, _, _) >> adrnew
	shipObj.setUserProfile(profileMock)
	shipObj.setSaveShippingAddress(true)
	shipObj.setCisiIndex("1")
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	0 * hShip.setPropertyValue('sourceId', _)
	0 * hShip.setPropertyValue('isFromPaypal', true)
	0 * hShip.setPropertyValue('addressStatus', null)
	0*hShip.setShipOnDate(_)
	0*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	0*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	0*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	0*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	shipObj.isShippingGroupChanged() == false
	shipObj.getShippingGroupErrorMsg().equals(null)
}
def"This method is to reset mulit shipping group create hard good shipping group non registry address save to profile throws business exception"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	shipObj.setUserProfile(profileMock)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",packAndHoldDate:"asdf")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["qasValidated": true,"poBoxAddress":true])
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> null
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	0*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	0*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBSystemException("mock of system exception")}
	0*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	orderMock.isPayPalOrder() >> true
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	profileMock.isTransient() >> false
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1 * errorMSGMock.getErrMsg("err_shipping_invalid_shipping_address","EN", null) >>"err_shipping_invalid_shipping_address"
	1 * purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,hShip, null, _) >>  {throw new BBBSystemException("Mock of Business exception")}
	0*ppmanager.validateCoupons(orderMock, _) >> true
	0*ppmanager.validateShippingMethod(orderMock, profileMock) >> "3a"
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:]) >> {throw new Exception()}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	1*checkoutManagerMock.saveAddressToProfile(profileMock, _, _) >> {throw new BBBBusinessException("mokc of BBBBusinessException")}
	shipObj.setUserProfile(profileMock)
	shipObj.setSaveShippingAddress(true)
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', _)
	1 * hShip.setPropertyValue('isFromPaypal', true)
	1 * hShip.setPropertyValue('addressStatus', null)
	0*hShip.setShipOnDate(_)
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	shipObj.isShippingGroupChanged() == false
	shipObj.getShippingGroupErrorMsg().equals(null)
}

def"This method is to reset mulit shipping group create hard good shipping group pobox address"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",addressId:"adr1234")]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	RepositoryItemMock  adrnewrep = new RepositoryItemMock()
	adrnewrep.setProperties(["qasValidated": true,"poBoxAddress":true])
	adrnewrep.setProperties(["identifier":"identifier"])
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> "true"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("indentifier")
	checkoutManagerMock.getProfileTools() >> profileTools
	1*profileTools.getAddressesById(profileMock,"adr1234") >> adrnewrep
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	0*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	requestMock.getLocale() >> new Locale("en_US")
	orderMock.isPayPalOrder() >> true
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> {throw new BBBBusinessException("Mock of business exception")}
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	0 * hShip.setPropertyValue('sourceId', null)
	0*hShip.setIsFromPaypal(true)
	0*hShip.setAddressStatus(_)
	0*hShip.setShipOnDate(_)
	addContainer.getNewAddressKey().equals(null)
	0*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	0*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	0*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	0*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
}
def"This method is to reset mulit shipping group create hard good shipping group address start with registry"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",addressId:"registryadr1234",)]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	BBBAddressImpl adrnew1 = new  BBBAddressImpl()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> "false"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("registryadr1234")
	adrnew1.setIdentifier("registryadr123456")
	adrnew.setId("adr1234")
	adrnew.setRegistryId("rg1234")
	adrnew.setRegistryInfo("rgInfo")
	orderMock.isPayPalOrder() >> true
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1*checkoutManagerMock.getRegistryShippingAddress(_, orderMock) >> [adrnew1,adrnew]
	checkoutManagerMock.getProfileTools() >> profileTools
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBBusinessException("mock of business exception")}
	1*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	shipServiceContainer.getShippingGroup("shipGrpName") >> sShip
	requestMock.getLocale() >> new Locale("en_US")
	ppmanager.validateCoupons(orderMock, profileMock) >> false
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', _)
	1 * hShip.setPropertyValue('addressStatus', null)
	1 * hShip.setPropertyValue('isFromPaypal', true)
	1*hShip.setShipOnDate(_)
	addContainer.getNewAddressKey().equals("registryadr1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0*hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	BBBAddress contAdr = addContainer.getNewAddressMap().get("registryadr1234")
	contAdr.getIdentifier().equals("registryadr1234")
	contAdr.getId().equals("adr1234")
	contAdr.getRegistryId().equals("rg1234")
	contAdr.getRegistryInfo().equals("rgInfo")
}
def"This method is to reset mulit shipping group create hard good shipping group address start with registry throw system exception"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"bstoreShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"hardgoodShippingGroup",commerceItemId:"ci1234,ci1235",addressId:"registryadr1234",)]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	shipHelperMock.getPricingTools() >> pTools
	BBBAddressImpl adrnew =new  BBBAddressImpl()
	BBBAddressImpl adrnew1 = new  BBBAddressImpl()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getSplitShippingGroupName() >> "false"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	adrnew.setIdentifier("registryadr1234")
	adrnew1.setIdentifier("registryadr123456")
	adrnew.setId("adr1234")
	adrnew.setRegistryId("rg1234")
	adrnew.setRegistryInfo("rgInfo")
	orderMock.isPayPalOrder() >> false
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,_) >> false
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	orderMock.getShippingGroups() >> [sShip,hShip]
	1*checkoutManagerMock.getRegistryShippingAddress(_, orderMock) >> []
	checkoutManagerMock.getProfileTools() >> profileTools
	0*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBBusinessException("mock of business exception")}
	0*shipHelperMock.checkForRequiredAddressProperties(_, requestMock) >> [new ArrayList(0),new ArrayList(0)]
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.createHardGoodShippingGroup(orderMock, _ ,_) >> hShip
	shipObj.setShippingAddressContainer(addContainer)
	cItem1.getId() >> "ci1234"
	cItem2.getId() >> "ci1235"
	orderMock.getCommerceItem("ci1234") >> cItem1
	orderMock.getCommerceItem("ci1235") >> cItem2
	1*shipServiceContainer.getShippingGroup("shipGrpName1") >> hShip
	1*shipServiceContainer.getShippingGroup("shipGrpName") >> sShip
	requestMock.getLocale() >> new Locale("en_US")
	0*ppmanager.validateCoupons(orderMock, profileMock) >> false
	1*errorMSGMock.getErrMsg("err_shipping_invalid_shipping_address","EN", null) >> "err_shipping_invalid_shipping_address"
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*orderManagerMock.updateOrder(orderMock)
	1 * hShip.setPropertyValue('sourceId', _)
	0 * hShip.setPropertyValue('addressStatus', null)
	0 * hShip.setPropertyValue('isFromPaypal', true)
	1*hShip.setShipOnDate(_)
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1234")
	1*commerceItmeManagerMock.removeAllRelationshipsFromCommerceItem(orderMock, "ci1235")
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1234",_,_)
	1*commerceItmeManagerMock.addItemQuantityToShippingGroup(orderMock, "ci1235",_,_)
	0*hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
	0*hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
	
}
def"This method is to reset mulit shipping group exception (error) in store shipping group creation"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >>  true
	Map<String , CommerceItemShipInfoVO> mapShipInfo =  ["ItemShipInfoVO": new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup")
		,"ItemShipInfoVO1":new CommerceItemShipInfoVO(shippingGroupType:"storeShippingGroup",commerceItemId:"ci1234"),"item": new CommerceItemShipInfoVO()]
	def BBBCommerceItem cItem = Mock()
	cItem.getQuantity() >> 2
	cItem.getId( ) >> "ci1234"
	cItem.getCatalogRefId() >> "sku1234"
	orderMock.getCommerceItem("ci1234") >> cItem
	shipObj.setSGCommerceItemShipInfoVO(mapShipInfo)
	sGroupManager.getHardGoodShippingGroupType() >> "hardgoodShippingGroup"
	sGroupManager.getStoreShippingGroupType() >> "storeShippingGroup"
	0*inventoryManagerMock.getBOPUSProductAvailability(_, "sku1234",["st1234"], 2, BBBInventoryManager.ONLINE_STORE, _, true,null, false , false) >>["st1234" :0]
	0*commerceItmeManagerMock.modifyCommerceItemShippingGroup(orderMock, cItem, "st1234")
	shipHelperMock.getPricingTools() >> pTools
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new CommerceException("Mock of Commerce Exception")}
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	1*orderManagerMock.updateAvailabilityMapInOrder(requestMock,_)
	0*orderManagerMock.updateOrder(orderMock)
	1*errorMSGMock.getErrMsg(shipObj.ERR_CART_INVALID_INPUT,shipObj.LOCALE_EN, null)
	1*errorMSGMock.getErrMsg(shipObj.ERROR_MULTIPLE_SHIPPING,shipObj.LOCALE_EN, null)
	Exception exce = thrown()
	exce.getMessage().equals(null)
}
def "This method is to reset mulit shipping group multiple request"(){
	given:
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> false
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateOrder(orderMock)
}
def "This method is to reset mulit shipping group commerce info is size zero"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	shipObj.setSGCommerceItemShipInfoVO([:])
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateOrder(orderMock)
}
def "This method is to reset mulit shipping group commerce info is null"(){
	given:
	1*multiShippingManagerMock.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, true, false, false, true, false)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	shipObj.setSGCommerceItemShipInfoVO(null)
	when:
	shipObj.handleRestMultipleShipping(requestMock, responseMock)
	then:
	0*orderManagerMock.updateOrder(orderMock)
}
def"This method is to shippinggroup id to info object "(){
	given:
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId() >> "sg1234"
	pState.getFailureURL() >> "faliureurl"
	when:
	boolean valid = shipObj.handleSaveNewAddress(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	valid == false
	1*shipInfo1.setShippingGroupName(_)
}
def"This method is to shippinggroup id to info object failure url in null"(){
	given:
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId() >> "sg1234"
	when:
	boolean valid = shipObj.handleSaveNewAddress(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	valid == true
	1*shipInfo1.setShippingGroupName(_)
}
def"This method is to shippinggroup id to info object failure url in atg-rest-ignore-redirect "(){
	given:
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId() >> "sg1234"
	pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
	when:
	boolean valid = shipObj.handleSaveNewAddress(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	valid == true
	1*shipInfo1.setShippingGroupName(_)
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service throws exception while update order."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	1*orderManagerMock.updateOrder(orderMock) >> {throw new Exception("Mock of exception")}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	0 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service contains gift included in order ."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setOrderIncludesGifts(true)
	orderMock.getOnlineBopusItemsStatusInOrder( ) >> "BOPUS_ONLY"
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service contains gift included in order and non bopus."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setOrderIncludesGifts(true)
	orderMock.getOnlineBopusItemsStatusInOrder( ) >> "BOPUS"
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
	1*pState.setCurrentLevel(_)
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service contains gift included in order , non bopus and url as non rest ."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	pState.getFailureURL() >> "atg"
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setOrderIncludesGifts(true)
	orderMock.getOnlineBopusItemsStatusInOrder( ) >> "BOPUS"
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid == false
	1*pState.setCurrentLevel(_)
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service contains gift included in order , non bopus and url as  resturl ."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setOrderIncludesGifts(true)
	orderMock.getOnlineBopusItemsStatusInOrder( ) >> "BOPUS"
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
	1*pState.setCurrentLevel(_)
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service after pre method."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	pState.getFailureURL() >> "atg-rest-ignore"
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.getFormError() >> true
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	0*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	0*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	0 * orderManagerMock.updateOrder(orderMock)
	0 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid == false
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service sku is not eligible for given state."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	repRequest.isUniqueRequestEntry(_) >> true
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> true
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1 * orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service sku is not eligible for given state redirect url is not null."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	repRequest.isUniqueRequestEntry(_) >> true
	pState.getFailureURL() >> "atg-rest-ignore"
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> true
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1 * orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid == false
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service with reset url."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service with reset url and paypal order ."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
	orderMock.isPayPalOrder() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service with non reset url."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> false >> false >> false >> true
	orderMock.isPayPalOrder() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid == false
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service with non reset url and paypal order."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	pState.getFailureURL() >> "atg-rest-ignore"
	orderMock.isPayPalOrder() >> true
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid == false
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service with non reset url and paypalsession."(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.preMultipleShipping(requestMock, responseMock) >> {}
	shipObj.multipleShipping(requestMock, responseMock) >> {}
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":infoVO1,"2":null])
	2*catalogUtilMock.isShippingZipCodeRestrictedForSku("sku1234",_,_) >> false
	orderMock.getShippingGroups() >> [null,hShip,sShip]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	relShip.getCommerceItem() >> cItem2
	relShip1.getCommerceItem() >> cItem1
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getShippingAddress() >> repC
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	shipHelperMock.getPricingTools() >> pTools
	pState.getFailureURL() >> "atg-rest-ignore"
	orderMock.isPayPalOrder() >> true
	payPalSessionBean.isFromPayPalPreview() >> true
	pState.getCheckoutFailureURLs() >> ["REVIEW":"REVIEW"]
	when:
	boolean valid = shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	then:
	1*shipInfo.setShippingGroupName(_)
	1*shipInfo.setShippingMethod(_)
	1*shipInfo1.setShippingGroupName(_)
	1*shipInfo1.setShippingMethod(_)
	1*hShip.setShipOnDate(_)
	0*sShip.setShipOnDate(_)
	1*orderManagerMock.updateOrder(orderMock)
	1 * pTools.priceOrderSubtotalShipping(orderMock, null, _, profileMock, [:])
	valid == false
}
def "THis method is used to expose multi shipping functionality of desktop site as a rest service  multiple"(){
	given:
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> false
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	CommerceItemShipInfoVO infoVO =new CommerceItemShipInfoVO()
	infoVO.setShippingGroupName("infoVO")
	infoVO.setShippingMethod("infoVO")
	CommerceItemShipInfoVO infoVO1 = new CommerceItemShipInfoVO()
	infoVO1.setShippingGroupName("infoVO11")
	infoVO1.setShippingMethod("infoVO1")
	shipObj.setSGCommerceItemShipInfoVO(["0":infoVO,"1":null])
	when:
	shipObj.handleAddMultipleShippingToOrder(requestMock, responseMock)
	
	then:
	0*orderManagerMock.updateOrder(orderMock)
}
def"This method place holder for the preMultipleShipping order object is null"(){
	given:
	def OrderHolder holder = Mock() 
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> null >> orderMock 
	orderMock.getCommerceItemCount() >> 0
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	infoContainer.getAllCommerceItemShippingInfos() >> []
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
}
def"This method place holder for the preMultipleShipping commerece item count is 1"(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	infoContainer.getAllCommerceItemShippingInfos() >> []
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
}
def"This method place holder for the preMultipleShipping commerece item count is 0"(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 0
def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shipmethod"
	shipInfo.getShippingGroupName() >> "grpNAme"
	shipInfo1.getShippingMethod() >> "LWA"
	shipInfo1.getShippingGroupName() >> "grpNAme1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
}
def"This method place holder for the preMultipleShipping "(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shipmethod"
	shipInfo.getShippingGroupName() >> "grpNAme"
	shipInfo1.getShippingMethod() >> "LWA"
	shipInfo1.getShippingGroupName() >> "grpNAme1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(false)
	adr1.setAddress1("1076 parsons ave")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	2*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"] >> ["false"]
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredAddressProperties(adr, requestMock)  >> error
	1*shipHelperMock.checkForRequiredLTLAddressProperties(adr1, requestMock)  >> error
	1*checkoutManagerMock.checkItemShipToAddress(_, "sku1234", adr) >> true
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem2.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	1*cItem1.setShipMethodUnsupported(false)
    1*cItem1.setLtlShipMethod(_)
	1*cItem1.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.TRUE)
	1*shipInfo1.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
}
def"This method place holder for the preMultipleShipping error ALTERNATE_ADDRESS "(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shipmethod"
	shipInfo.getShippingGroupName() >> "grpNAme"
	shipInfo1.getShippingMethod() >> "LWA"
	shipInfo1.getShippingGroupName() >> "grpNAme1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(false)
	adr1.setAddress1("1076 parsons ave")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	2*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"] >> ["false"]
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	2*shipHelperMock.checkForRequiredLTLAddressProperties(adr1, requestMock)  >> error
	1*checkoutManagerMock.checkItemShipToAddress(_, "sku1234", adr) >> false
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	1*checkoutManagerMock.isPostOfficeBoxAddress(_) >> true
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem2.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	1*cItem1.setShipMethodUnsupported(false)
	1*cItem1.setLtlShipMethod(_)
	1*cItem1.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.TRUE)
	1*shipInfo1.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
}
def"This method place holder for the preMultipleShipping address is pos box "(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> false
	cItem2.isLtlItem() >> false
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shipmethod"
	shipInfo.getShippingGroupName() >> "grpNAme"
	shipInfo1.getShippingMethod() >> "LWA"
	shipInfo1.getShippingGroupName() >> "grpNAme1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setPoBoxAddress(true)
	adr1.setAddress1("1076 parsons ave")
	adr.setPoBoxAddress(true)
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	2*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"] 
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	2*shipHelperMock.checkForRequiredAddressProperties(adr1, requestMock)  >> error
	0*shipHelperMock.checkForRequiredLTLAddressProperties(adr1, requestMock)  >> error
	1*checkoutManagerMock.checkItemShipToAddress(_, "sku1234", adr) >> false
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	checkoutManagerMock.isPostOfficeBoxAddress(_) >> false >> true
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem2.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	0*cItem1.setShipMethodUnsupported(false)
	0*cItem1.setLtlShipMethod(_)
	1*cItem1.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.TRUE)
	1*shipInfo1.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
}
def"This method place holder for the preMultipleShipping SOURCE ID NOT Available "(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shipmethod"
	shipInfo.getShippingGroupName() >> "grpNAme"
	shipInfo1.getShippingMethod() >> "LWA"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(false)
	adr1.setAddress1("1076 parsons ave")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredAddressProperties(adr, requestMock)  >> error
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem2.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	1*cItem1.setShipMethodUnsupported(false)
	1*cItem1.setLtlShipMethod(_)
	1*cItem1.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.TRUE)
	1*shipInfo1.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
}
def"This method place holder for the preMultipleShipping SOURCE ID NOT Available throw business exception "(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	cItem1.isLtlItem() >> true
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shipmethod"
	shipInfo.getShippingGroupName() >> "grpNAme"
	shipInfo1.getShippingMethod() >> "LWA"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(false)
	adr1.setAddress1("1076 parsons ave")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> { throw new BBBBusinessException("Mock of business excception")}
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredAddressProperties(adr, requestMock)  >> error
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	1*checkoutManagerMock.checkItemShipToAddress(_, "sku1234", adr) >> false
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem2.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	1*cItem1.setShipMethodUnsupported(false)
	1*cItem1.setLtlShipMethod(_)
	1*cItem1.setLtlShipMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.TRUE)
	1*shipInfo1.setShippingMethod(BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD)
}
def"This method place holder for the preMultipleShipping shipping method as Store"(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	1*sGroupManager.getStorePickupShippingGroup("sg1234",orderMock) >> sShip
	sShip.getId() >> "sg1234"
	cItem1.isLtlItem() >> true
	cItem2.getStoreId() >> "sg1234"
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	cItem1.getRegistryId() >> "rg1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "store"
	shipInfo1.getShippingMethod() >> "LW"
	shipInfo1.getShippingGroupName() >> "grpNAme1"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(true)
	adr1.setAddress1("1076 parsons ave")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>["false"]
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(adr1, requestMock)  >> error
	1*checkoutManagerMock.checkItemShipToAddress(_, "sku12345", adr1) >> true
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem1.setShipMethodUnsupported(false)
	1*cItem1.setLtlShipMethod(_)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	1*shipInfo.setShippingGroupName("sg1234")
}
def"This method place holder for the preMultipleShipping shipping method as Store throw system exception"(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo2 = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def CommerceItemShippingInfo shipInfo3 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	0*sGroupManager.getStorePickupShippingGroup("sg1234",orderMock) >> sShip
	sShip.getId() >> "sg1234"
	cItem1.isLtlItem() >> true
	cItem2.getStoreId() >> null
	cItem2.getStoreId() >> "sg1234"
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	cItem3.getStoreId() >> "sg1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem2
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo2.getCommerceItem() >> cItem3
	shipInfo3.getCommerceItem() >> cItem4
	shipInfo.getShippingMethod() >> "store"
	shipInfo1.getShippingMethod() >> "LW"
	shipInfo1.getShippingGroupName() >> "grpNAme1"
	shipInfo2.getShippingGroupName() >> "grpNAme1"
	shipInfo2.getShippingMethod() >> "store"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo2,shipInfo1,shipInfo3]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(true)
	adr1.setAddress1("1076 parsons ave")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", null)
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>{throw new BBBSystemException("Mock of system exception")}
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(null, requestMock)  >> error
	0*checkoutManagerMock.checkItemShipToAddress(_, "sku12345", adr1) >> true
	
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem1.setShipMethodUnsupported(false)
	1*cItem1.setLtlShipMethod(_)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	0*shipInfo.setShippingGroupName("sg1234")
	1*errorMSGMock.getErrMsg(shipObj.ERROR_INCORRECT_SHIP_METHOD,shipObj.LOCALE_EN, null)
}
def"This method place holder for the preMultipleShipping shipping method as Store throw exception"(){
	given:
	def OrderHolder holder = Mock()
	shipObj.setShoppingCart(holder)
	holder.getCurrent() >> orderMock
	orderMock.getCommerceItemCount() >> 1
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem1 = Mock()
	1*sGroupManager.getStorePickupShippingGroup("sg1234",orderMock) >> {throw new CommerceException()}
	sShip.getId() >> "sg1234"
	cItem1.isLtlItem() >> true
	cItem2.getStoreId() >> "sg1234"
	cItem2.getCatalogRefId() >> "sku1234"
	cItem1.getCatalogRefId() >> "sku12345"
	cItem1.getRegistryId() >> "rg1234"
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem1
	shipInfo1.getCommerceItem() >> cItem2
	shipInfo.getShippingMethod() >> "LW"
	shipInfo1.getShippingMethod() >> "store"
	shipInfo.getShippingGroupName() >> "grpNAme"
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	shipObj.setStoreSGMethodName("Store")
	shipObj.setMultiShippingAddrCont(addContainer)
	BBBAddress adr = new BBBAddressVO()
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setIsNonPOBoxAddress(true)
	adr.setAddress1("POBOX")
	adr.setMobileNumber("21111111")
	adr.setState("OH")
	addContainer.addAddressToContainer("grpNAme", adr)
	addContainer.addAddressToContainer("grpNAme1", adr1)
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >>["false"]
	List error = new ArrayList()
	error.add(0,[])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(adr, requestMock)  >> error
	1*checkoutManagerMock.checkItemShipToAddress(_, "sku12345", adr1) >> false
	when:
	shipObj.preMultipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer() == false
	1*cItem1.setShipMethodUnsupported(false)
	1*cItem1.setLtlShipMethod(_)
	1*cItem1.setWhiteGloveAssembly(BBBCatalogConstants.FALSE)
	0*shipInfo.setShippingGroupName("sg1234")
	adr1.getMobileNumber().equals(adr1.getAlternatePhoneNumber())
}
def "checking gift card is restircted or not"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	def BBBStoreShippingGroup sShip = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def CommerceItemShippingInfo relItem3 =Mock()
	def CommerceItemShippingInfo relItem4 =Mock()
	def CommerceItemShippingInfo relItem5 =Mock()
	def BBBCommerceItem cItem = Mock()
	def CommerceItem cItem1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	def BBBCommerceItem cItem5 = Mock()
	relItem1.getCommerceItem() >> cItem >> cItem1
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >>cItem
	1*relItem.getShippingGroupName() >> "shippingGroupNAme"
	2*relItem.getShippingMethod() >> "method"
	1*cItem.getCatalogRefId() >> "SKU1234"
	1*relItem2.getShippingGroupName() >> "shippingGroupNAme1"
	2*relItem2.getShippingMethod() >> BBBCoreConstants.SHIP_METHOD_EXPRESS_ID
	relItem3.getCommerceItem() >> cItem3
	relItem4.getCommerceItem() >> cItem4
	relItem5.getCommerceItem() >> cItem5
	1*relItem3.getShippingGroupName() >> "shippingGroupNAme"
	2*relItem3.getShippingMethod() >> BBBCoreConstants.SHIP_METHOD_EXPRESS_ID
	1*relItem4.getShippingGroupName() >> "shippingGroupNAme"
	2*relItem4.getShippingMethod() >> BBBCoreConstants.SHIP_METHOD_EXPRESS_ID
	1*relItem5.getShippingGroupName() >> "shippingGroupNAme1"
	2*relItem5.getShippingMethod() >> BBBCoreConstants.SHIP_METHOD_EXPRESS_ID
	1*cItem2.getCatalogRefId() >> "SKU12341"
	1*cItem3.getCatalogRefId() >> "SKU12343"
	1*cItem4.getCatalogRefId() >> "SKU12342"
	1*cItem5.getCatalogRefId() >> "SKU12345"
	1*checkoutManagerMock.isGiftCardItem(_,"SKU12341") >> true
	1*checkoutManagerMock.isGiftCardItem(_,"SKU12343") >> {throw new BBBBusinessException("Mock of the BBBBusinessException")}
	1*checkoutManagerMock.isGiftCardItem(_,"SKU12345") >> {throw new BBBSystemException("Mock of the BBBBusinessException")}
	1*checkoutManagerMock.isGiftCardItem(_,"SKU12342") >> true
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2,relItem3,relItem4,relItem5]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.checkGiftCardRestriction(requestMock,responseMock)
	then:
	0*shipObj.logError('Can not ship only Gift card items through express shipment', null)
}
def "checking gift card is restircted or not with restriction"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	def BBBStoreShippingGroup sShip = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def BBBCommerceItem cItem = Mock()
	def CommerceItem cItem1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	def BBBCommerceItem cItem5 = Mock()
	relItem1.getCommerceItem() >>cItem1
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >>cItem
	1*relItem.getShippingGroupName() >> "shippingGroupNAme"
	2*relItem.getShippingMethod() >> "method"
	1*cItem.getCatalogRefId() >> "SKU1234"
	1*relItem2.getShippingGroupName() >> "shippingGroupNAme"
	2*relItem2.getShippingMethod() >> BBBCoreConstants.SHIP_METHOD_EXPRESS_ID
	1*cItem2.getCatalogRefId() >> "SKU12341"
	1*checkoutManagerMock.isGiftCardItem(_,"SKU12341") >> true
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	cConfig.isLoggingDebugForRequestScopedComponents() >> false
	when:
	shipObj.checkGiftCardRestriction(requestMock,responseMock)
	then:
	1*shipObj.logError('Can not ship only Gift card items through express shipment', null)
}
def"create map of commerce items in a shipping group for which shipping method is empty"(){
	given:
	orderMock.getShippingGroups() >> [hShip,hShip]
	def BBBShippingGroupCommerceItemRelationship cShipRel = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel1 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel2 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel3 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel4 = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def CommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	relItem.getShippingMethod() >> "store"
	relItem1.getShippingGroupName() >> "sgName"
	relItem1.getShippingMethod() >> "spMethod"
	shipObj.setStoreSGMethodName("store")
	relItem1.getCommerceItem() >>cItem
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >> cItem1
	cShipRel.getCommerceItem() >> cItem
	cShipRel1.getCommerceItem() >> cItem1
	cShipRel2.getCommerceItem() >> cItem2
	cShipRel3.getCommerceItem() >> cItem3
	cShipRel4.getCommerceItem() >> cItem4
	cItem.getAssemblyItemId()  >> "assemblyId"
	cItem.getDeliveryItemId() >> "deliveryId"
	cItem.isLtlItem() >> true
	cItem3.isLtlItem() >> true
	cItem4.isLtlItem() >> true
	cItem.getId() >> "ci1234"
	cItem1.getId() >> "ci12345"
	cItem2.getId() >> "ci123425"
	cItem3.getId() >> "ci1234225"
	cItem4.getId() >> "ci12342225"
	cItem4.getWhiteGloveAssembly() >> "true"
	cItem3.getWhiteGloveAssembly() >> "false"
	hShip.getShippingMethod() >> "shipMethod" >> null
	hShip.getId() >> "sg1234" >> "sg12345"
	sShip.getShippingMethod() >> "shipMethod"
	hShip.getCommerceItemRelationships() >> [cShipRel2,cShipRel,cShipRel3,cShipRel4,cShipRel1]
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	1*sGroupManager.getMatchingSGForIdAndMethod(orderMock, _, "sgName", "spMethod") >> hShip
	infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
	infoContainer.getCommerceItemShippingInfos("assemblyId") >> [relItem1]
	when:
	shipObj.multipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer()
	1*sGroupManager.createDelAssItemForSGCIMap(_,orderMock,null,infoContainer)
	1*shipServiceContainer.addShippingGroup("sgName;;spMethod", hShip)
	2*relItem1.setShippingGroupName("sgName;;spMethod")
	1*relItem1.setShippingMethod("spMethod")
	1*relItem.setShippingGroupName("sgName;;spMethod")
	1*relItem.setShippingMethod("spMethod")
	1*sGroupManager.applyLTLItemAssocMap(_, orderMock)
	2 * sGroupManager.mergeCISIforSameLTLShipMethod(*_)
	1 * sGroupManager.removeEmptyShippingGroups(orderMock)
	1 * sGroupManager.addRemoveAssForCommerceItems(*_)
}
def"create map of commerce items in a shipping group for which shipping method is empty throw CommerceException "(){
	given:
	orderMock.getShippingGroups() >> [hShip,hShip]
	def BBBShippingGroupCommerceItemRelationship cShipRel = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel1 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel2 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel3 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel4 = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def CommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	relItem.getShippingMethod() >> "store"
	relItem1.getShippingGroupName() >> "sgName"
	relItem1.getShippingMethod() >> "spMethod"
	shipObj.setStoreSGMethodName("store")
	relItem1.getCommerceItem() >>cItem
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >> cItem1
	cShipRel.getCommerceItem() >> cItem
	cShipRel1.getCommerceItem() >> cItem1
	cShipRel2.getCommerceItem() >> cItem2
	cShipRel3.getCommerceItem() >> cItem3
	cShipRel4.getCommerceItem() >> cItem4
	cItem.getAssemblyItemId()  >> null
	cItem.getDeliveryItemId() >> "deliveryId"
	cItem.isLtlItem() >> true
	cItem3.isLtlItem() >> true
	cItem4.isLtlItem() >> true
	cItem.getId() >> "ci1234"
	cItem1.getId() >> "ci12345"
	cItem2.getId() >> "ci123425"
	cItem3.getId() >> "ci1234225"
	cItem4.getId() >> "ci12342225"
	cItem4.getWhiteGloveAssembly() >> "true"
	cItem3.getWhiteGloveAssembly() >> "false"
	hShip.getShippingMethod() >> "shipMethod" >> null
	hShip.getId() >> "sg1234" >> "sg12345"
	sShip.getShippingMethod() >> "shipMethod"
	hShip.getCommerceItemRelationships() >> [cShipRel2,cShipRel,cShipRel3,cShipRel4,cShipRel1]
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	1*sGroupManager.getMatchingSGForIdAndMethod(orderMock, _, "sgName", "spMethod") >> hShip
	infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
	infoContainer.getCommerceItemShippingInfos("assemblyId") >> [relItem1]
	1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new CommerceException("mock of CommerceException")}
	when:
	shipObj.multipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer()
	1*sGroupManager.createDelAssItemForSGCIMap(_,orderMock,null,infoContainer)
	1*shipServiceContainer.addShippingGroup("sgName;;spMethod", hShip)
	1*relItem1.setShippingGroupName("sgName;;spMethod")
	0*relItem1.setShippingMethod("spMethod")
	1*relItem.setShippingGroupName("sgName;;spMethod")
	1*relItem.setShippingMethod("spMethod")
	1*sGroupManager.applyLTLItemAssocMap(_, orderMock)
	2 * sGroupManager.mergeCISIforSameLTLShipMethod(*_)
	1 * sGroupManager.addRemoveAssForCommerceItems(*_)
	Exception exce=thrown()
}
def"create map of commerce items in a shipping group for which shipping method is empty throw BBBSystemException "(){
	given:
	orderMock.getShippingGroups() >> []
	def BBBShippingGroupCommerceItemRelationship cShipRel = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel1 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel2 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel3 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel4 = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def CommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	relItem.getShippingMethod() >> "store"
	relItem1.getShippingGroupName() >> "sgName"
	relItem1.getShippingMethod() >> "spMethod"
	shipObj.setStoreSGMethodName("store")
	relItem1.getCommerceItem() >>cItem
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >> cItem1
	cShipRel.getCommerceItem() >> cItem
	cShipRel1.getCommerceItem() >> cItem1
	cShipRel2.getCommerceItem() >> cItem2
	cShipRel3.getCommerceItem() >> cItem3
	cShipRel4.getCommerceItem() >> cItem4
	cItem.getAssemblyItemId()  >> "assemblyId"
	cItem.getDeliveryItemId() >> "deliveryId"
	cItem.getId() >> "ci1234"
	cItem1.getId() >> "ci12345"
	cItem2.getId() >> "ci123425"
	cItem3.getId() >> "ci1234225"
	cItem4.getId() >> "ci12342225"
	cItem4.getWhiteGloveAssembly() >> "true"
	cItem3.getWhiteGloveAssembly() >> "false"
	hShip.getShippingMethod() >> "shipMethod" >> null
	hShip.getId() >> "sg1234" >> "sg12345"
	sShip.getShippingMethod() >> "shipMethod"
	hShip.getCommerceItemRelationships() >> [cShipRel2,cShipRel,cShipRel3,cShipRel4,cShipRel1]
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	1*sGroupManager.getMatchingSGForIdAndMethod(orderMock, _, "sgName", "spMethod") >> hShip
	infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
	infoContainer.getCommerceItemShippingInfos("assemblyId") >> [relItem1]
	1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new BBBSystemException("mock of BBBSystemException")}
	when:
	shipObj.multipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer()
	0*sGroupManager.createDelAssItemForSGCIMap(_,orderMock,null,infoContainer)
	1*shipServiceContainer.addShippingGroup("sgName;;spMethod", hShip)
	1*relItem1.setShippingGroupName("sgName;;spMethod")
	0*relItem1.setShippingMethod("spMethod")
	0*relItem.setShippingGroupName("sgName;;spMethod")
	0*relItem.setShippingMethod("spMethod")
	1*sGroupManager.applyLTLItemAssocMap(_, orderMock)
	0 * sGroupManager.mergeCISIforSameLTLShipMethod(*_)
	1 * sGroupManager.addRemoveAssForCommerceItems(*_)
}
def"create map of commerce items in a shipping group for which shipping method is empty throw BBBBusinessException "(){
	given:
	orderMock.getShippingGroups() >> [hShip]
	def BBBShippingGroupCommerceItemRelationship cShipRel = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel1 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel2 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel3 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel4 = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def CommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	relItem.getShippingMethod() >> "store"
	relItem1.getShippingGroupName() >> "sgName"
	relItem1.getShippingMethod() >> "spMethod"
	shipObj.setStoreSGMethodName("store")
	relItem1.getCommerceItem() >>cItem
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >> cItem1
	cShipRel.getCommerceItem() >> cItem
	cShipRel1.getCommerceItem() >> cItem1
	cShipRel2.getCommerceItem() >> cItem2
	cShipRel3.getCommerceItem() >> cItem3
	cShipRel4.getCommerceItem() >> cItem4
	cItem.getAssemblyItemId()  >> "assemblyId"
	cItem.getDeliveryItemId() >> "deliveryId"
	cItem.getId() >> "ci1234"
	cItem1.getId() >> "ci12345"
	cItem2.getId() >> "ci123425"
	cItem3.getId() >> "ci1234225"
	cItem4.getId() >> "ci12342225"
	cItem4.getWhiteGloveAssembly() >> "true"
	cItem3.getWhiteGloveAssembly() >> "false"
	hShip.getShippingMethod() >> "shipMethod" >> null
	hShip.getId() >> "sg1234" >> "sg12345"
	sShip.getShippingMethod() >> "shipMethod"
	hShip.getCommerceItemRelationships() >> []
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	1*sGroupManager.getMatchingSGForIdAndMethod(orderMock, _, "sgName", "spMethod") >> hShip
	infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
	infoContainer.getCommerceItemShippingInfos("assemblyId") >> [relItem1]
	1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new BBBBusinessException("mock of BBBBusinessException")}
	when:
	shipObj.multipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer()
	0*sGroupManager.createDelAssItemForSGCIMap(_,orderMock,null,infoContainer)
	1*shipServiceContainer.addShippingGroup("sgName;;spMethod", hShip)
	1*relItem1.setShippingGroupName("sgName;;spMethod")
	0*relItem1.setShippingMethod("spMethod")
	0*relItem.setShippingGroupName("sgName;;spMethod")
	0*relItem.setShippingMethod("spMethod")
	1*sGroupManager.applyLTLItemAssocMap(_, orderMock)
	1 * sGroupManager.mergeCISIforSameLTLShipMethod(*_)
	1 * sGroupManager.addRemoveAssForCommerceItems(*_)
}
def"create map of commerce items in a shipping group for which shipping method is empty throw RepositoryException "(){
	given:
	orderMock.getShippingGroups() >> []
	def BBBShippingGroupCommerceItemRelationship cShipRel = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel1 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel2 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel3 = Mock()
	def BBBShippingGroupCommerceItemRelationship cShipRel4 = Mock()
	def CommerceItemShippingInfo relItem =Mock()
	def CommerceItemShippingInfo relItem1 =Mock()
	def CommerceItemShippingInfo relItem2 =Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def CommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	relItem.getShippingMethod() >> "store"
	relItem1.getShippingGroupName() >> "sgName"
	relItem1.getShippingMethod() >> "spMethod"
	shipObj.setStoreSGMethodName("store")
	relItem1.getCommerceItem() >>cItem
	relItem2.getCommerceItem() >>cItem2
	relItem.getCommerceItem() >> cItem1
	cShipRel.getCommerceItem() >> cItem
	cShipRel1.getCommerceItem() >> cItem1
	cShipRel2.getCommerceItem() >> cItem2
	cShipRel3.getCommerceItem() >> cItem3
	cShipRel4.getCommerceItem() >> cItem4
	cItem.getAssemblyItemId()  >> "assemblyId"
	cItem.getDeliveryItemId() >> "deliveryId"
	cItem.getId() >> "ci1234"
	cItem1.getId() >> "ci12345"
	cItem2.getId() >> "ci123425"
	cItem3.getId() >> "ci1234225"
	cItem4.getId() >> "ci12342225"
	cItem4.getWhiteGloveAssembly() >> "true"
	cItem3.getWhiteGloveAssembly() >> "false"
	hShip.getShippingMethod() >> "shipMethod" >> null
	hShip.getId() >> "sg1234" >> "sg12345"
	sShip.getShippingMethod() >> "shipMethod"
	hShip.getCommerceItemRelationships() >> [cShipRel2,cShipRel,cShipRel3,cShipRel4,cShipRel1]
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2]
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	1*sGroupManager.getMatchingSGForIdAndMethod(orderMock, _, "sgName", "spMethod") >> hShip
	infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
	infoContainer.getCommerceItemShippingInfos("assemblyId") >> [relItem1]
	1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new RepositoryException("mock of RepositoryException")}
	when:
	shipObj.multipleShipping(requestMock, responseMock)
	then:
	shipObj.isClearContainer()
	0*sGroupManager.createDelAssItemForSGCIMap(_,orderMock,null,infoContainer)
	1*shipServiceContainer.addShippingGroup("sgName;;spMethod", hShip)
	1*relItem1.setShippingGroupName("sgName;;spMethod")
	0*relItem1.setShippingMethod("spMethod")
	0*relItem.setShippingGroupName("sgName;;spMethod")
	0*relItem.setShippingMethod("spMethod")
	1*sGroupManager.applyLTLItemAssocMap(_, orderMock)
	0 * sGroupManager.mergeCISIforSameLTLShipMethod(*_)
	1 * sGroupManager.addRemoveAssForCommerceItems(*_)
}
def "This method creates map of commerce items with same sku in a shipping group."(){
	given:
	def BBBShippingGroupCommerceItemRelationship scRel = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel1 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel2 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel3 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel4 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel5 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel6 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel7 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel8 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel9 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel10 = Mock()
	def BBBShippingGroupCommerceItemRelationship scRel11 = Mock()
	def CommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	def BBBCommerceItem cItem5 = Mock()
	def BBBCommerceItem cItem6 = Mock()
	def BBBCommerceItem cItem7 = Mock()
	def BBBCommerceItem cItem8 = Mock()
	def BBBCommerceItem cItem9 = Mock()
	def BBBCommerceItem cItem10 = Mock()
	def BBBCommerceItem cItem11 = Mock()
	scRel.getCommerceItem() >> cItem
	scRel1.getCommerceItem() >> cItem1
	scRel2.getCommerceItem() >> cItem2
	scRel3.getCommerceItem() >> cItem3
	scRel4.getCommerceItem() >> cItem4
	scRel5.getCommerceItem() >> cItem5
	scRel6.getCommerceItem() >> cItem6
	scRel7.getCommerceItem() >>cItem7
	scRel8.getCommerceItem() >>cItem8
	scRel9.getCommerceItem() >>cItem9
	scRel10.getCommerceItem() >>cItem10
	scRel11.getCommerceItem() >>cItem11
	cItem2.isLtlItem() >> true
	cItem2.getWhiteGloveAssembly() >> "false"
	cItem2.getCatalogRefId() >> "sku12345"
	cItem2.getId() >> "ci1234"
	cItem3.isLtlItem() >> true
	cItem3.getWhiteGloveAssembly() >> "true"
	cItem3.getCatalogRefId() >> "sku12346"
	cItem3.getId() >> "ci1235"
	cItem4.isLtlItem() >> true
	cItem4.getWhiteGloveAssembly() >> "true"
	cItem4.getCatalogRefId() >> "sku12346"
	cItem4.getId() >> "ci1236"
	cItem5.isLtlItem() >> true
	cItem5.getWhiteGloveAssembly() >> "false"
	cItem5.getCatalogRefId() >> "sku12345"
	cItem5.getId() >> "ci1237"
	cItem6.isLtlItem() >> true
	cItem6.getWhiteGloveAssembly() >> null
	cItem6.getCatalogRefId() >> "sku12347"
	cItem6.getId() >> "ci1238"
	cItem7.isLtlItem() >> true
	cItem7.getWhiteGloveAssembly() >> "true"
	cItem7.getCatalogRefId() >> "sku12346"
	cItem7.getRegistryId() >> "rg12344"
	cItem7.getId() >> "ci12347"
	cItem8.isLtlItem() >> true
	cItem8.getWhiteGloveAssembly() >> "false"
	cItem8.getCatalogRefId() >> "sku12345"
	cItem8.getRegistryId() >> "rg12345"
	cItem8.getId() >> "ci12348"
	cItem9.isLtlItem() >> true
	cItem9.getWhiteGloveAssembly() >> "true"
	cItem9.getCatalogRefId() >> "sku12346"
	cItem9.getRegistryId() >> "rg12344"
	cItem9.getId() >> "ci12349"
	cItem10.isLtlItem() >> true
	cItem10.getWhiteGloveAssembly() >> "false"
	cItem10.getCatalogRefId() >> "sku12347"
	cItem10.getId() >> "ci123410"
	cItem10.getRegistryId() >> "rg1234"
	cItem11.isLtlItem() >> true
	cItem11.getWhiteGloveAssembly() >> "false"
	cItem11.getCatalogRefId() >> "sku12345"
	cItem11.getId() >> "ci123411"
	cItem11.getRegistryId() >> "rg12345"
	hShip.getCommerceItemRelationships() >>  [scRel,scRel1,scRel7,scRel8,scRel2,scRel3,scRel4,scRel5,scRel6,scRel9,scRel10,scRel11]
	when:
	Map map= shipObj.createSkuToCommItemsMap(hShip)
	then:
	map.size() == 6
	((List)map.get("sku12346Assembly")).size() == 2
	((List)map.get("sku12346Assembly")).get(0).equals("ci1235")
	((List)map.get("sku12346Assembly")).get(1).equals("ci1236")
	((List)map.get("sku12345")).size() == 2
	((List)map.get("sku12345")).get(0).equals("ci1234")
	((List)map.get("sku12345")).get(1).equals("ci1237")
	((List)map.get("sku12347")).get(0).equals("ci1238")
	((List)map.get("rg12345sku12345")).size() == 2
	((List)map.get("rg12345sku12345")).get(0).equals("ci12348")
	((List)map.get("rg12345sku12345")).get(1).equals("ci123411")
	((List)map.get("rg12344sku12346Assembly")).size() == 2
	((List)map.get("rg12344sku12346Assembly")).get(0).equals("ci12347")
	((List)map.get("rg12344sku12346Assembly")).get(1).equals("ci12349")
	}
def "This method creates map of commerce items with same sku in a shipping group relation ship is empty."(){
	given:
	hShip.getCommerceItemRelationships() >>  []
	when:
	Map map= shipObj.createSkuToCommItemsMap(hShip)
	then:
	map.size() == 0
	}
def "this method checks for restricted states for not standard Shipping method"(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("AE")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(shippingMethodMap)
		shipObj.setShippingOption("standard")
		1*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		1*catalogUtilMock.getShippingMethod("standar") >> itemMock
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, "standar", requestMock, responseMock)
	then:
	valid  == false
}
def "this method checks for restricted states and Shipping method is null"(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("AE")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(shippingMethodMap)
		shipObj.setShippingOption("standard")
		1*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		0*catalogUtilMock.getShippingMethod("standar") >> itemMock
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, null, requestMock, responseMock)
	then:
	valid  == false
}
def "this method checks for restricted states and Shipping method  throws BBBBusinessException "(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("AE")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(shippingMethodMap)
		shipObj.setShippingOption("standard")
		1*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		1*catalogUtilMock.getShippingMethod("standar") >> {throw new BBBBusinessException("Mock of BBBBusinessException")}
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		shipObj.setLoggingError(false)
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, "standar", requestMock, responseMock)
	then:
	valid  == false
}
def "this method checks for restricted states and Shipping method map is null "(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("AE")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(null)
		shipObj.setShippingOption("standard")
		0*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		0*catalogUtilMock.getShippingMethod("standar") >> {throw new BBBBusinessException("Mock of BBBBusinessException")}
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, "standar", requestMock, responseMock)
	then:
	valid  == false
}
def "this method checks for restricted states and Shipping method  throws BBBSystemException "(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("AE")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(shippingMethodMap)
		shipObj.setShippingOption("standard")
		1*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		1*catalogUtilMock.getShippingMethod("standar") >> {throw new BBBSystemException("Mock of BBBSystemException")}
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, "standar", requestMock, responseMock)
	then:
	valid  == false
}
def "this method checks for restricted states for standard Shipping method"(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("AE")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(shippingMethodMap)
		shipObj.setShippingOption("standard")
		0*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		0*catalogUtilMock.getShippingMethod("standar") >> itemMock
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, "standard", requestMock, responseMock)
	then:
	valid  == false
}
def "this method checks for restricted states is eligble for shipping"(){
	given:
	 BBBAddress adr = new BBBAddressVO()
	adr.setState("OH")
	Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		shipObj.setStates(["AA","AE","AP"])
		shipObj.setShippingMethodMap(shippingMethodMap)
		shipObj.setShippingOption("standard")
		0*errorMSGMock.getErrMsg(shipObj.RESTRICTED_SHIPPING_METHOD,shipObj.LOCALE_EN,null) >> shipObj.RESTRICTED_SHIPPING_METHOD
		 RepositoryItemMock itemMock =new  RepositoryItemMock()
		0*catalogUtilMock.getShippingMethod("standar") >> itemMock
		itemMock.setProperties(["shipMethodDescription":"shipMethodDescription"])
	when:
	boolean valid = shipObj.checkItemShipByMethod(adr, "standard", requestMock, responseMock)
	then:
	valid  == false
}
def"This method either adds or removes gift messaging to existing shipping group."(){
	given:
	orderMock.getShippingGroup(_) >> sShip
	when:
	shipObj.giftMessaging()
	then:
	0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(*_)
}
def"This method either adds or removes gift messaging to existing shipping group throws BBBSystemException."(){
	given:
	orderMock.getShippingGroup(_) >> hShip
	1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(*_) >> {throw new BBBSystemException("Mock of BBBSystemException")}
	expect:
	shipObj.giftMessaging()
	
}
def"This method either adds or removes gift messaging to existing shipping group throws ShippingGroupNotFoundException."(){
	given:
	orderMock.getShippingGroup(_) >> {throw new ShippingGroupNotFoundException("Mokc of ShippingGroupNotFoundException")}
	when:
	shipObj.giftMessaging()
	then:
	0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(*_)
}
def"This method either adds or removes gift messaging to existing shipping group throws BBBBusinessException."(){
	given:
	orderMock.getShippingGroup(_) >> hShip
	1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(*_) >>{throw new BBBBusinessException("Mock of BBBBusinessException")}
	expect:
	shipObj.giftMessaging()
}
def"This method either adds or removes gift messaging to existing shipping group throws CommerceException."(){
	given:
	orderMock.getShippingGroup(_) >> hShip
	1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(*_) >>{throw new CommerceException("Mock of CommerceExceptionr")}
	expect:
	shipObj.giftMessaging()
}
def"This method either adds or removes gift messaging to existing shipping group throws InvalidParameterException."(){
	given:
	orderMock.getShippingGroup(_) >> {throw new InvalidParameterException("Mokc of InvalidParameterException")}
	when:
	shipObj.giftMessaging()
	then:
	0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(*_)
}
def "This handler method will validate shipping address and apply the shipping groups to the order"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	BBBAddress newadr =new  BBBAddressVO()
	newadr.setState("OH")
	shipObj.setAddress(newadr)
	shipObj.setShipToAddressName("college")
	shipObj.setNewShipToAddressName("newcollege")
	shipObj.setNewAddress(false)
	shipObj.setAddressContainer(addContainer)
	shipObj.setQasOnProfileAddress(true)
	sGroupManager.isAnyHardgoodShippingGroups(orderMock) >>  true 
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> false
	requestMock.getParameter("checkoutfirstName") >> "checkoutfirstName"
	requestMock.getParameter("checkoutlastName") >> "checkoutlastName"
	requestMock.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS1) >> BBBAddressAPIConstants.PPTY_ADDRESS1
	requestMock.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS2) >> BBBAddressAPIConstants.PPTY_ADDRESS2
	requestMock.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS3) >> BBBAddressAPIConstants.PPTY_ADDRESS3
	requestMock.getParameter("cityName") >> "cityName"
	requestMock.getParameter("stateName") >> "stateName"
	requestMock.getParameter("zipUS") >> "zipUS"
	requestMock.getParameter("company") >> "company"
	requestMock.getParameter("X-bbb-channel")>>"MobileApp"
	shipObj.setLTLCommerceItem(true)
	List error = new ArrayList()
	error.add(0, [])
	error.add(1,[])
	1*shipHelperMock.checkForRequiredLTLAddressProperties(newadr, requestMock) >> error
	1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> true
	1*checkoutManagerMock.canItemShipByMethod(_, _, "Standard") >> true
	shipObj.setShippingOption("Standard")
	orderMock.getShippingGroups() >> [sShip,hShip]
	hShip.getGiftMessage() >> "giftMessage"
	hShip.isContainsGiftWrap() >> true
	hShip.getId() >> "SG12345"
	hShip.getGiftWrapInd() >> true
	shipObj.getCurrentSiteID() >> "BedBathUS"
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	requestMock.getLocale() >> new Locale("en_US")
	3*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	RegistrySummaryVO sVo = new RegistrySummaryVO()
	sVo.setPrimaryRegistrantFirstName("firstName")
	sVo.setPrimaryRegistrantLastName("firstName")
	sVo.setCoRegistrantFirstName("firstNAme")
	sVo.setCoRegistrantLastName("lastName")
	orderMock.getRegistryMap() >> ["newcollege": sVo]
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	hShip.getShippingAddress() >> repC
	1 * checkoutManagerMock.getItemsRegistryCount(_) >> 1
	shipHelperMock.getPricingTools() >> pTools
	orderMock.getShippingGroup("SG12345") >> hShip
	1*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, _)
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleAddShippingAddressToOrder(requestMock, responseMock)
	then:
	shipObj.getNewAddress()
	shipObj.getShipToAddressName().equals("newcollege")
	BBBAddress adr = addContainer.getAddressFromContainer("newcollege")
	adr.getId().equals("newcollege")
	adr.getIsWebLinkOrderAddr()
	adr.getAddress1().equals(BBBAddressAPIConstants.PPTY_ADDRESS1)
	adr.getAddress2().equals(BBBAddressAPIConstants.PPTY_ADDRESS2)
	adr.getAddress3().equals(BBBAddressAPIConstants.PPTY_ADDRESS3)
	adr.getCity().equals("cityName")
	adr.getState().equals("stateName")
	adr.getCity().equals("cityName")
	adr.getCompanyName().equals("company")
	adr.getIsWebLinkOrderAddr()
	shipObj.getBBBShippingInfoBean().getGiftMessage().equals(null)
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getAddress().getState().equals("stateName")
	shipObj.getBBBShippingInfoBean().getShippingMethod().equals("Standard")
	shipObj.getBBBShippingInfoBean().getShippingGroupId().equals("SG12345")
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals("<strong>firstName firstName & firstNAme lastName</strong> (Registry #newcollege)")
	shipObj.getBBBShippingInfoBean().getRegistryId().equals("newcollege")
	1*sGroupManager.shipToAddress(_, hShip)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order   "(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	requestMock.getHeader("X-bbb-channel")>>"MobileApp"
	orderMock.getShippingGroups() >> [hShip]
	hShip.getGiftMessage() >> "giftMessage"
	hShip.isContainsGiftWrap() >> true
	hShip.getGiftWrapInd() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> true
	hShip.getShippingAddress() >> repC
	shipObj.setShipToAddressName("shipto")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("shipto", adr)
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().getGiftMessage().equals("giftMessage")
	shipObj.getBBBShippingInfoBean().isGiftingFlag()
	shipObj.getBBBShippingInfoBean().getGiftWrap()
	0*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order failure url is empty  "(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	requestMock.getHeader("X-bbb-channel")>>"MobileWeb"
	orderMock.getShippingGroups() >> [sShip] >>[sShip] >> [sShip] >> [hShip]
	hShip.getGiftMessage() >> "giftMessage"
	hShip.isContainsGiftWrap() >> true
	hShip.getGiftWrapInd() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> true
	hShip.getShippingAddress() >> repC
	shipObj.setShipToAddressName("shipto")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("shipto", adr)
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	0*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order amd shipping group is null  "(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	requestMock.getHeader("X-bbb-channel")>>"MobileWeb"
	orderMock.getShippingGroups() >> [null] >> [null] >> []
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(addDays(0, "dd/MM/yyyy"))
	shipObj.setShipToAddressName("registry")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	1*sGroupManager.shipToAddress(_, hShip) >> {throw new IntrospectionException("Mock of IntrospectionException")}
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIPPING_GENERIC_ERROR, shipObj.LOCALE_EN, null) >> shipObj.ERROR_SHIPPING_GENERIC_ERROR
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals("SendShippingEmail")
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation()
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	0*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order throws repository exception "(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	requestMock.getHeader("X-bbb-channel")>>"MobileWeb"
	orderMock.getShippingGroups() >> [] 
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate("sasasa")
	shipObj.setShipToAddressName("sasas")
	shipObj.setAddressContainer(addContainer)
	shipObj.setShipToAddressName("registry")
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>1
	1*sGroupManager.shipToAddress(_, hShip) >> {throw new RepositoryException("Mock of RepositoryException")}
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIPPING_GENERIC_ERROR, shipObj.LOCALE_EN, null) >> shipObj.ERROR_SHIPPING_GENERIC_ERROR
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	orderMock.getRegistryMap() >> [:]
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals("SendShippingEmail")
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation()
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	0*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order throws commerce exception "(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> []
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
	1*errorMSGMock.getErrMsg(shipObj.ERROR_SHIPPING_GENERIC_ERROR, shipObj.LOCALE_EN, null) >> shipObj.ERROR_SHIPPING_GENERIC_ERROR
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.shipToAddress(_, hShip)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order  "(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> true
	hShip.getShippingAddress() >> repC
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order  failure url is not empty"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> true
	hShip.getShippingAddress() >> repC
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order throws Exception"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	hShip.getShippingAddress() >> repC
	shipObj.runProcessValidateShippingGroups(orderMock, _, _, profileMock, _) >> {throw new Exception("Mock of exception")}
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order form error after giftmessaging method"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	Transaction tr =Mock()
	shipObj.ensureTransaction() >> tr
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	hShip.getShippingAddress() >> repC
	7*shipObj.getFormError() >> false >> false >> false >> false>>false >>true 
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	0*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order throw exception after update order"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	Transaction tr =Mock()
	shipObj.ensureTransaction() >> tr
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	2*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	hShip.getShippingAddress() >> repC
	1*orderManagerMock.updateOrder(orderMock) >> {throw new Exception("Mock of exception")}
	1*shipHelperMock.getPricingTools() >> pTools
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having paypal as payment group"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	Transaction tr =Mock()
	shipObj.ensureTransaction() >> tr
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	2*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	hShip.getShippingAddress() >> repC
	1*shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	1*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having paypal as payment group and session also contain paypal details"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	Transaction tr =Mock()
	shipObj.ensureTransaction() >> tr
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	2*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	def ShippingGroupCommerceItemRelationship relShip = Mock()
	def ShippingGroupCommerceItemRelationship relShip1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	relShip.getCommerceItem() >> cItem
	relShip1.getCommerceItem() >> cItem1
	cItem.getCatalogRefId() >> "sk1234"
	cItem1.getCatalogRefId() >> "sk12345"
	def BBBRepositoryContactInfo repC = Mock()
	repC.getPostalCode() >> "43206"
	hShip.getCommerceItemRelationships() >> [relShip,relShip1]
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk1234",_,"43206") >> false
	1*catalogUtilMock.isShippingZipCodeRestrictedForSku("sk12345",_,"43206") >> false
	hShip.getShippingAddress() >> repC
	1*shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setPayPalSessionBean(payPalSessionBean)
	payPalSessionBean.isFromPayPalPreview() >> true
	1*pState.getCheckoutFailureURLs() >> ["REVIEW":"REVIEW"]
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	1*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having paypal as payment group and session also contain paypal details url is resturl"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	Transaction tr =Mock()
	shipObj.ensureTransaction() >> tr
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	2*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	shipObj.isOrderContainRestrictedSKU(orderMock, _, _) >> false
	1*shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setPayPalSessionBean(payPalSessionBean)
	payPalSessionBean.isFromPayPalPreview() >> true
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	1*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having paypal as payment group and session also contain paypal details url is empty"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	Transaction tr =Mock()
	shipObj.ensureTransaction() >> tr
	repRequest.isUniqueRequestEntry(_) >> true
	requestMock.getContextPath() >> "BedBathUS"
	pState.getFailureURL() >> ""
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	requestMock.getHeader("X-bbb-channel")>>"DesktopWeb"
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSiteId(BBBCoreConstants.SITE_BAB_CA)
	shipObj.setPackNHold(true)
	shipObj.setPackNHoldDate(null)
	shipObj.setShipToAddressName(null)
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr = new BBBAddressVO()
	adr.setPostalCode("43206")
	addContainer.addAddressToContainer("registry", adr)
	requestMock.getLocale() >> new Locale("us_EN")
	shipObj.setSendShippingConfEmail(true)
	shipObj.setSendShippingEmail("SendShippingEmail")
	2*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	hShip.getId()>>"sg1234"
	1*checkoutManagerMock.getItemsRegistryCount(_)>>0
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	shipObj.isOrderContainRestrictedSKU(orderMock, _, _) >> false
	1*shipHelperMock.getPricingTools() >> pTools
	orderMock.isPayPalOrder() >> true
	shipObj.setPayPalSessionBean(payPalSessionBean)
	payPalSessionBean.isFromPayPalPreview() >> true
	when:
	shipObj.handleAddShipping(requestMock, responseMock)
	then:
	shipObj.getBBBShippingInfoBean().isGiftingFlag() == false
	shipObj.getBBBShippingInfoBean().getGiftWrap() == false
	shipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals(null)
	shipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
	shipObj.getBBBShippingInfoBean().getRegistryId().equals(null)
	shipObj.getBBBShippingInfoBean().getRegistryInfo().equals(null)
	1*sGroupManager.removeEmptyShippingGroups(orderMock)
	1*sGroupManager.shipToAddress(_, hShip)
	1*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having error before pre method"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> true
	when:
	boolean valid = shipObj.handleAddShipping(requestMock, responseMock)
	then:
	valid == false
	0*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having error after pre method"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	pState.getFailureURL() >> "atg-rest-ignore"
	shipObj.getFormError() >> false >> true
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	when:
	boolean valid = shipObj.handleAddShipping(requestMock, responseMock)
	then:
	valid == false
	0*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having error after post method"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	pState.getFailureURL() >> "atg-rest-ignore-redirect"
	shipObj.getFormError() >> false >> false >> false >> false >> false >> true
	shipObj.preAddShipping(requestMock, responseMock) >> {}
	shipObj.addShipping(requestMock, responseMock) >> {}
	shipObj.postAddShipping(requestMock, responseMock) >> {}
	shipObj.isOrderContainRestrictedSKU(orderMock, _, _) >> false
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	orderMock.isPayPalOrder() >> true
	shipObj.setCollegeAddress(false)
	when:
	boolean valid = shipObj.handleAddShipping(requestMock, responseMock)
	then:
	valid
	1*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups to the order having error after post method and redirect url is rest url"(){
	given:
	shipObj =Spy()
	spyObjIntilization(shipObj)
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> true
	shipObj.getFormError() >> false >> false >> false >> false >> false >> true
	shipObj.setSingleShippingGroupCheckout(false)
	shipObj.addShipping(requestMock, responseMock) >> {}
	shipObj.postAddShipping(requestMock, responseMock) >> {}
	shipObj.isOrderContainRestrictedSKU(orderMock, _, _) >> false
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	orderMock.isPayPalOrder() >> true
	shipObj.setCollegeAddress(false)
	when:
	boolean valid = shipObj.handleAddShipping(requestMock, responseMock)
	then:
	valid 
	1*orderManagerMock.updateOrder(orderMock)
	}
def "This handler method will validate shipping address and apply the shipping groups  has multiple request"(){
	given:
	shipObj.setRepeatingRequestMonitor(repRequest)
	repRequest.isUniqueRequestEntry(_) >> false
	when:
	boolean valid = shipObj.handleAddShipping(requestMock, responseMock)
	then:
	valid  == false
	0*orderManagerMock.updateOrder(orderMock)
	}
def "This method validates the user inputs for the Move To Billing process ship to name as college address"(){
	given:
	shipObj = Spy()
	spyObjIntilization(shipObj)
	shipObj.getCurrentSiteID() >> "BedBathCanada"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("q")
	shipObj.setPoBoxStatus("N")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("college")
	shipObj.setNewShipToAddressName("college")
	shipObj.setAddressContainer(addContainer)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("OH")
	requestMock.getParameter("checkoutfirstName") >> "checkoutfirstName"
	requestMock.getParameter("checkoutlastName") >> "checkoutlastName"
	requestMock.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS1) >> BBBAddressAPIConstants.PPTY_ADDRESS1
	requestMock.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS2) >> BBBAddressAPIConstants.PPTY_ADDRESS2
	requestMock.getParameter(BBBAddressAPIConstants.PPTY_ADDRESS3) >> BBBAddressAPIConstants.PPTY_ADDRESS3
	requestMock.getParameter("cityName") >> "cityName"
	requestMock.getParameter("stateName") >> "stateName"
	requestMock.getParameter("zipCA") >> "zipUS"
	requestMock.getParameter("company") >> "company"
	1*catalogUtilMock.getStateList() >> {throw new BBBBusinessException("Mock of BBBBusinessException")}
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr1 = new BBBAddressVO()
	1*shipHelperMock.checkForRequiredAddressProperties(adr1,requestMock) >> list
	adr1.setState("OH")
	adr1.setIsNonPOBoxAddress(true)
	shipObj.setAddress(adr1)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> false
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	BBBAddress adr = addContainer.getAddressFromContainer("college")
	adr.getId().equals("college")
	adr.getIsWebLinkOrderAddr()
	adr.getAddress1().equals(BBBAddressAPIConstants.PPTY_ADDRESS1)
	adr.getAddress2().equals(BBBAddressAPIConstants.PPTY_ADDRESS2)
	adr.getAddress3().equals(BBBAddressAPIConstants.PPTY_ADDRESS3)
	adr.getCity().equals("cityName")
	adr.getState().equals("stateName")
	adr.getCity().equals("cityName")
	adr.getCompanyName().equals("company")
	adr.getIsWebLinkOrderAddr()
	shipObj.isCollegeAddress() 
	shipObj.getNewAddress()
	shipObj.getShipToAddressName().equals("college")
	
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE "(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	shipObj.setAddress(null)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged() == false
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE shipping group is store"(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getShippingAddress() >> repc
	repc.getPostalCode() >> ""
	orderMock.getShippingGroups() >> [sShip]
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged() == false
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE shipping method is null from sGroup"(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getShippingAddress() >> repc
	repc.getPostalCode() >> "43206"
	BBBAddress aadr = new BBBAddressVO()
	aadr.setPostalCode("43206")
	shipObj.setAddress(aadr)
	orderMock.getShippingGroups() >> [hShip]
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged() == false
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE same postal codes"(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getShippingAddress() >> repc
	repc.getPostalCode() >> "43206"
	BBBAddress aadr = new BBBAddressVO()
	aadr.setPostalCode("43206")
	shipObj.setAddress(aadr)
	orderMock.getShippingGroups() >> [hShip]
	hShip.getShippingMethod() >> "sdd"
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged() == false
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE diff postal codes"(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getShippingAddress() >> repc
	repc.getPostalCode() >> "43207"
	BBBAddress aadr = new BBBAddressVO()
	shipObj.setAddress(aadr)
	aadr.setPostalCode("43206")
	orderMock.getShippingGroups() >> [hShip]
	hShip.getShippingMethod() >> "SD"
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged() == false
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE same shipping method"(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getShippingAddress() >> repc
	repc.getPostalCode() >> "43207"
	BBBAddress aadr = new BBBAddressVO()
	shipObj.setAddress(aadr)
	aadr.setPostalCode("43206")
	orderMock.getShippingGroups() >> [hShip]
	hShip.getShippingMethod() >> "SDD"
	shipObj.setShippingOption("sd")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged() == false
	}
def "This method validates the user inputs for the Move To Billing process ship to name as PROFILE same shipping option"(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("n")
	shipObj.setPoBoxStatus("q")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("PROFILE")
	shipObj.setAddressContainer(addContainer)
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getShippingAddress() >> repc
	repc.getPostalCode() >> "43207"
	BBBAddress aadr = new BBBAddressVO()
	shipObj.setAddress(aadr)
	aadr.setPostalCode("43206")
	orderMock.getShippingGroups() >> [hShip]
	hShip.getShippingMethod() >> "SDD"
	shipObj.setShippingOption("SDD")
	Properties shippingMethodMap = new Properties()
	shippingMethodMap.put("standard", "standard")
	shipObj.setShippingMethodMap(shippingMethodMap)
	cConfig.isLoggingDebugForRequestScopedComponents() >> true
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)  == false
	shipObj.isShippingGroupChanged()
	shipObj.getShippingOption().equals("standard")
	}
def "This method validates the user inputs for the Move To Billing process ship "(){
	given:
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("true")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("OH")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	shipObj.setAddress(adr)
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> false
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getNewAddress()
	shipObj.getShipToAddressName().equals("new")
	BBBAddress adr1 = shipObj.getAddress()
	adr1.getId().equals("new")
	addContainer.getAddressFromContainer("new") != null
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is userAddress"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("userAddress")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.isCollegeAddress() == false
	shipObj.getNewAddress()
	shipObj.getShipToAddressName().equals("new")
	BBBAddress adr1 = shipObj.getAddress()
	adr1.getId().equals("new")
	addContainer.getAddressFromContainer("new") != null
	
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is profile"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> false
	shipObj.setShipToAddressName("Profile")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setFirstName("first")
	adr1.setLastName("last")
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState("OH")
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("Profile", adr1)
	shipObj.setQasOnProfileAddress(true)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	0*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setFirstName("first")
	adr.setLastName("last")
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	shipObj.setAddress(adr)
	1*checkoutManagerMock.canItemShipToCiSiIndexAddress(_, _, _) >> false
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setCisiIndex("0")
	def CommerceItemShippingInfoContainer infoContainer = Mock()
	def CommerceItemShippingInfo shipInfo = Mock()
	def CommerceItemShippingInfo shipInfo1 = Mock()
	def BBBCommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	shipObj.setCommerceItemShippingInfoContainer(infoContainer)
	shipInfo.getCommerceItem() >> cItem
	shipInfo1.getCommerceItem() >> cItem1
	shipInfo.getShippingMethod() >> "shippingMethod"
	shipInfo.getShippingGroupName() >> "shipGrpName"
	shipInfo1.getShippingMethod() >> "shippingMethod1"
	shipInfo1.getShippingGroupName() >> "shipGrpName1"
	shipInfo1.getQuantity() >> 2
	infoContainer.getAllCommerceItemShippingInfos() >> [shipInfo,shipInfo1]
	1*errorMSGMock.getErrMsg(shipObj.ERROR_INCORRECT_POBOX,shipObj.LOCALE_EN, null) >> shipObj.ERROR_INCORRECT_POBOX
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated()
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE)
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is null"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> false
	shipObj.setShipToAddressName(null)
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setFirstName("first")
	adr1.setLastName("last")
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState("OH")
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("Profile", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	0*catalogUtilMock.getStateList() >> [vo,vo1]
	0*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setFirstName("first")
	adr.setLastName("last")
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	0*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToCiSiIndexAddress(_, _, _) >> false
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	1*errorMSGMock.getErrMsg(shipObj.NO_SHIPPING_ADDRESS_SELECTED,shipObj.LOCALE_EN, null) >> shipObj.NO_SHIPPING_ADDRESS_SELECTED
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is from"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("from")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setFirstName("first")
	adr1.setLastName("last")
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState("OH")
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("PROFILEfrom", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("OH")
	vo1.setStateName("name")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setFirstName("first")
	adr.setLastName("last")
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	shipObj.setAddress(adr)
	1*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is registry"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("registry")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState(null)
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("PROFILEregistry", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	adr1.setRegistryInfo("first last")
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is registry last name is empty"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("registry")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState(null)
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("PROFILEregistry", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	adr1.setRegistryInfo("first")
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method validates the user inputs for the Move To Billing process ship to address name is registry info is empty"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("registry")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState(null)
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("PROFILEregistry", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	adr1.setRegistryInfo(null)
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method validates the user inputs for the Move To Billing process ship to address first name is not empty "(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("registry")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setFirstName("first")
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState(null)
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("PROFILEregistry", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	1*catalogUtilMock.getStateList() >> [vo,vo1]
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(adr,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	adr1.setRegistryInfo(null)
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setShippingOption("SDD")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method validates the user inputs for the Move To Billing process ,address from container is null"(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(false)
	shipObj.setPoBoxFlag("P")
	shipObj.setPoBoxStatus("Y")
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShipToAddressName("registry")
	shipObj.setNewShipToAddressName("new")
	shipObj.setAddressContainer(addContainer)
	BBBAddress adr1 = new BBBAddressVO()
	adr1.setAddress1("1076 parsons ave")
	adr1.setCity("columbus")
	adr1.setState("OH")
	adr1.setPostalCode("4320")
	adr1.setCountry("US")
	adr1.setAddress2("asdr2")
	addContainer.addAddressToContainer("PROFILE_registry", adr1)
	shipObj.setQasOnProfileAddress(false)
	StateVO vo = new StateVO()
	vo.setStateCode("code")
	vo.setStateName("name")
	StateVO vo1 = new StateVO()
	vo1.setStateCode("code")
	vo1.setStateName("name")
	
	1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
	List list = new ArrayList()
	list.add(0,[])
	list.add(1,[])
	BBBAddress adr = new BBBAddressVO()
	adr.setAddress1("1076 parsons ave")
	adr.setCity("columbus")
	adr.setState("OH")
	adr.setPostalCode("4320")
	adr.setCountry("US")
	adr.setAddress2("asdr2")
	adr1.setIsNonPOBoxAddress(true)
	1*shipHelperMock.checkForRequiredAddressProperties(_,requestMock) >> list
	adr.setState("OH")
	adr.setIsNonPOBoxAddress(true)
	adr1.setRegistryInfo("first last")
	shipObj.setAddress(adr)
	0*checkoutManagerMock.canItemShipToAddress(_, _, _) >> true
	1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
	shipObj.setShippingOption("SDD")
	1*catalogUtilMock.getStateList() >> {throw new BBBSystemException("Mokc of BBBSystemException ")}
	1*errorMSGMock.getErrMsg(shipObj.ERROR_INCORRECT_STATE, shipObj.LOCALE_EN, null) >>shipObj.ERROR_INCORRECT_STATE
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getNewAddress() == false
	adr1.isQasValidated() == false
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
}
def "This method validates the user inputs for the Move To Billing process ,does not one college item "(){
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setPackNHold(true)
	1*checkoutManagerMock.hasEvenSingleCollegeItem(_,_) >> false
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	when:
	shipObj.preAddShipping(requestMock, responseMock)
	then:
	shipObj.getRedirectState().equals(BBBPayPalConstants.SHIPPING_SINGLE) == false
	}
def "This method will reprice the order to catch address problems through CyberSource"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	sesssionBeanMock.getSddStoreId() >> "12345"
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setSaveShippingAddress(true)
	shipObj.setShipToAddressName("asdsadas")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	1*adrAPI.addNewShippingAddress(_, _, _, true, true) >> adr
	shipObj.setSaveEmail("saveenail")
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	shipObj.setAddressContainer(addContainer)
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	1 * hShip.setPropertyValue('sddStoreId', '12345')
	1*repc.setEmail("saveenail")
	1*hShip.setPropertyValue('sourceId', 'Identifier')
	
	}
def "This method will reprice the order to catch address problems through CyberSource store is null"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setSaveShippingAddress(true)
	shipObj.setShipToAddressName("asdsadas")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	1*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	0 * hShip.setPropertyValue('sddStoreId', '12345')
	1*repc.setEmail("saveenail")
	0*hShip.setPropertyValue('sourceId', 'Identifier')
	
	}
def "This method will reprice the order to catch address problems through CyberSource address is available in profile"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setSaveShippingAddress(true)
	shipObj.setShipToAddressName("asdsadas")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	0*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	shipObj.setQasOnProfileAddress(true)
	shipObj.setSavePhone("11111111")
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	0 * hShip.setPropertyValue('sddStoreId', '12345')
	0*repc.setEmail("saveenail")
	0*repc.setPhoneNumber("11111111")
	0*hShip.setPropertyValue('sourceId', 'Identifier')
	}
def "This method will reprice the order to catch address problems through CyberSource address is available from registry"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setSaveShippingAddress(true)
	shipObj.setShipToAddressName("registry")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	0*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	sesssionBeanMock.getSddStoreId() >> "12345"
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	repc.getPostalCode() >> "43206"
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	shipObj.setQasOnProfileAddress(true)
	shipObj.setSavePhone("11111111")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	profileMock.isTransient() >> false >> true
	shipObj.setBbbGetCouponsManager(cManager)
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	1 * hShip.setPropertyValue('sddStoreId', '12345')
	1*repc.setEmail("saveenail")
	1*repc.setPhoneNumber("11111111")
	1*hShip.setPropertyValue('sourceId', _)
	1*cManager.createWalletMobile(*_)
	}
def "This method will reprice the order to catch address problems through CyberSource for transient profile"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setSaveShippingAddress(true)
	shipObj.setShipToAddressName("registry")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	0*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	sesssionBeanMock.getSddStoreId() >> "12345"
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	repc.getPostalCode() >> "43206"
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	shipObj.setQasOnProfileAddress(true)
	shipObj.setSavePhone("11111111")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	profileMock.isTransient() >> true
	shipObj.setBbbGetCouponsManager(cManager)
	1*cManager.createWalletMobile(*_) >> {throw new BBBBusinessException("Mock of business exception")}
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	1 * hShip.setPropertyValue('sddStoreId', '12345')
	1*repc.setEmail("saveenail")
	1*repc.setPhoneNumber("11111111")
	1*hShip.setPropertyValue('sourceId', _)
	}
def "This method will reprice the order to catch address problems through CyberSource for transient profile throws system exception"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(true)
	shipObj.setSaveShippingAddress(false)
	shipObj.setShipToAddressName("registry")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	0*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	sesssionBeanMock.getSddStoreId() >> "12345"
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	repc.getPostalCode() >> "43206"
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	shipObj.setQasOnProfileAddress(true)
	shipObj.setSavePhone("11111111")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	profileMock.isTransient() >> true
	shipObj.setBbbGetCouponsManager(cManager)
	1*cManager.createWalletMobile(*_) >> {throw new BBBSystemException("Mock of System exception")}
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	1 * hShip.setPropertyValue('sddStoreId', '12345')
	1*repc.setEmail("saveenail")
	1*repc.setPhoneNumber("11111111")
	1*hShip.setPropertyValue('sourceId', _)
	}
def "This method will reprice the order to catch address problems through CyberSource for postal code is null in billing address"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> true
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(false)
	shipObj.setSaveShippingAddress(false)
	shipObj.setShipToAddressName("registry")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	0*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	sesssionBeanMock.getSddStoreId() >> "12345"
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	shipObj.setQasOnProfileAddress(true)
	shipObj.setSavePhone("11111111")
	1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	profileMock.isTransient() >> true
	shipObj.setBbbGetCouponsManager(cManager)
	1*cManager.createWalletMobile(*_) >> {throw new BBBSystemException("Mock of System exception")}
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	1 * hShip.setPropertyValue('sddStoreId', '12345')
	1*repc.setEmail("saveenail")
	1*repc.setPhoneNumber("11111111")
	1*hShip.setPropertyValue('sourceId', _)
	}
def "This method will reprice the order to catch address problems through CyberSource throws PricingException"(){
	given:
	requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> false
	shipObj.setShippingOption("SDD")
	orderMock.getShippingGroups() >> [hShip]
	shipObj.setSessionBean(sesssionBeanMock)
	shipObj.setSingleShippingGroupCheckout(false)
	shipObj.setSaveShippingAddress(false)
	shipObj.setShipToAddressName("registry")
	checkoutManagerMock.getProfileAddressTool() >> adrAPI
	BBBAddressVO adr = new BBBAddressVO()
	adr.setIdentifier("Identifier")
	0*adrAPI.addNewShippingAddress(_, _, _, false, false) >> adr
	shipObj.setSaveEmail("saveenail")
	sesssionBeanMock.getSddStoreId() >> "12345"
	BBBRepositoryContactInfo repc =Mock()
	orderMock.getBillingAddress() >> repc
	shipObj.setAddressContainer(addContainer)
	profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"asa"]
	shipObj.setQasOnProfileAddress(true)
	shipObj.setSavePhone("11111111")
	0*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
	profileMock.isTransient() >> true
	shipObj.setBbbGetCouponsManager(cManager)
	0*cManager.createWalletMobile(*_) >> {throw new BBBSystemException("Mock of System exception")}
	shipHelperMock.getPricingTools() >> pTools
	1*pTools.priceOrderSubtotalShipping(*_) >> {throw new PricingException("mock of PricingException")}
	when:
	shipObj.postAddShipping(requestMock, responseMock)
	then:
	1 * hShip.setPropertyValue('sddStoreId', '12345')
	0*repc.setEmail("saveenail")
	0*repc.setPhoneNumber("11111111")
	0*hShip.setPropertyValue('sourceId', _)
	}
def "clear the exception when session expired"(){
	given:
	def NameContext ctx = Mock()
	requestMock.getRequestScope() >>ctx
	Vector <DropletException> objVector = new Vector <DropletException>()
	objVector.add(new DropletException("err_generic_error1","err_generic_error1"))
	objVector.add(new DropletException("err_generic_error2","err_generic_error2"))
	ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE) >> objVector
	when:
	shipObj.clearExceptionsFromRequest(requestMock)
	then:
	1*requestMock.setParameter("exceptions", null)
	objVector.size() == 0
}
def "clear the exception when session expired no exception available"(){
	given:
	def NameContext ctx = Mock()
	requestMock.getRequestScope() >>ctx
	ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE) >> null
	when:
	shipObj.clearExceptionsFromRequest(requestMock)
	then:
	1*requestMock.setParameter("exceptions", null)
}
	void spyObjIntilization(BBBShippingGroupFormhandler spShipObj){
		spShipObj.setSessionBean(sesssionBeanMock)
		spShipObj.setCheckoutProgressStates(pState)
		spShipObj.setShoppingCart(orderHolderMock)
		spShipObj.setCommonConfiguration(cConfig)
		spShipObj.setCatalogUtil(catalogUtilMock)
		spShipObj.setPayPalSessionBean(payPalSessionBean)
		spShipObj.setMsgHandler(errorMSGMock)
		spShipObj.setOrderManager(orderManagerMock)
		spShipObj.setPurchaseProcessHelper(purchaseProcessHelperMock)
		spShipObj.setCheckoutManager(checkoutManagerMock)
		spShipObj.setShippingHelper(shipHelperMock)
		orderHolderMock.getCurrent() >> orderMock
		spShipObj.setManager(sGroupManager)
		spShipObj.setShippingGroupManager(sGroupManager)
		spShipObj.setProfile(profileMock)
		spShipObj.setSuccessUrlMap(["changeToShipOnline":"/checkout/shipping/shipping.jsp?shippingGr=multi"])
		spShipObj.setErrorUrlMap(["changeToShipOnline":"/checkout/shipping/shipping.jsp?shippingGr=multi"])
		shipObj.setMultiShippingManager(multiShippingManagerMock)
		shipObj.setShippingGroupContainerService(shipServiceContainer)
		catalogUtilMock.getConfigValueByconfigType("ContentCatalogKeys") >> ["BedBathUSSiteCode":"BedBathUS","BuyBuyBabySiteCode":"BuyBuyBaby","BedBathCanadaSiteCode":"BedBathCanada"]
		requestMock.resolveName(BBBCoreConstants.SAVEDCOMP) >> sessionitemBean
		pState.setCheckoutSuccessURLs(url)
		pState.setCheckoutFailureURLs(url)
	}
	public static String addDays(int days ,String format)
	{	SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);  // number of days to add
		return sdf.format(c.getTime()).toString();
	}
}
