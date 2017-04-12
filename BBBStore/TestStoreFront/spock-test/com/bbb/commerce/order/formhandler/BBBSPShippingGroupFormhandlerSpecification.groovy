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
import atg.commerce.order.InvalidParameterException
import atg.commerce.order.OrderHolder
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship
import atg.commerce.order.ShippingGroupNotFoundException
import atg.commerce.order.SimpleOrderManager
import atg.commerce.order.purchase.CommerceItemShippingInfo
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer
import atg.commerce.order.purchase.ShippingGroupFormHandler
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

import com.bbb.account.BBBProfileManager
import com.bbb.account.BBBProfileTools
import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressVO
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper
import com.bbb.commerce.order.purchase.BBBShippingGroupContainerService
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.commerce.order.shipping.BBBShippingInfoBean
import com.bbb.constants.BBBCoreConstants
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.profile.session.BBBSessionBean
import com.bbb.repository.RepositoryItemMock
import com.bbb.selfservice.vo.BeddingShipAddrVO
import com.bbb.utils.CommonConfiguration

class BBBSPShippingGroupFormhandlerSpecification extends BBBExtendedSpec {

	private BBBSPShippingGroupFormhandler spShipObj 
	 BBBPurchaseProcessHelper purchaseProcessHelperMock = Mock()
	SimpleOrderManager orderManagerMock = Mock()
	BBBPurchaseProcessHelper shipHelperMock = Mock()
	CheckoutProgressStates pState = Mock()
	PricingTools pTools = Mock()
	CommonConfiguration cConfig = Mock()
	OrderHolder orderHolderMock = Mock()
	BBBOrderImpl orderMock = Mock()
	BBBPayPalSessionBean payPalSessionBean = Mock()
	BBBCatalogTools catalogUtilMock = Mock()
	BBBSessionBean sesssionBeanMock = Mock()
	LblTxtTemplateManager errorMSGMock = Mock()
	TransactionManager tManager  = Mock()
	RepeatingRequestMonitor repRequest = Mock()
	Profile profileMock = Mock()
	BBBShippingGroupManager sGroupManager = Mock()
	BBBCheckoutManager checkoutManagerMock =Mock()
	PromotionTools promotools = Mock()
	PromotionLookupManager  pLooLupManager = Mock()
	BBBCouponUtil  couponutil = Mock()
	BBBProfileTools profileTools = Mock()
	BBBProfileManager pManager =Mock()
	BBBAddressAPI adrAPI = Mock()
	BBBShippingGroupContainerService  shipServiceContainer = Mock()
	BBBAddressContainer addContainer = new BBBAddressContainer()
	Map map = ["firstName":"First Name","lastName":"Last Name","address1":"Street Address","city":"City","state":"State","country":"Country","postalCode":"Zip Code",
"phoneNumber":"Phone Number","company":"Company","address2":"Street 2 Address","address3":"Apartment Number",
"countryAndState":"Country and Sate combination",
"phoneNumber":"Phone Number","email":"Email","alternatePhoneNumber":"Alternate Phone Number"]
	
	def setup(){
		spShipObj = new BBBSPShippingGroupFormhandler(checkoutManager:checkoutManagerMock,messageHandler:errorMSGMock,catalogTools:catalogUtilMock,commonConfiguration:cConfig,purchaseProcessHelper:purchaseProcessHelperMock,orderManager:orderManagerMock,shippingHelper:shipHelperMock,checkoutProgressStates:pState)
		spShipObj.setShoppingCart(orderHolderMock)
		spShipObj.setPayPalSessionBean(payPalSessionBean)
		orderHolderMock.getCurrent() >> orderMock
		spShipObj.setManager(sGroupManager)
		spShipObj.setShippingGroupManager(sGroupManager)
		spShipObj.setProfile(profileMock)
		spShipObj.setPromotionTools(promotools)
		spShipObj.setPromotionLookupManager(pLooLupManager)
		spShipObj.setCouponUtil(couponutil)
		spShipObj.setTools(profileTools)
		spShipObj.setProfileManager(pManager)
		spShipObj.setShippingGroupContainerService(shipServiceContainer)
		catalogUtilMock.getConfigValueByconfigType("ContentCatalogKeys") >> ["BedBathUSSiteCode":"BedBathUS","BuyBuyBabySiteCode":"BuyBuyBaby","BedBathCanadaSiteCode":"BedBathCanada"]
		
	}
	def "handle method to remove gift messaging to existing shipping grouop"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success == false
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,_,_,_)
		1 * pTools.priceOrderTotal(orderMock, null, _, profileMock, [:])
		1 * pState.setCurrentLevel('SP_BILLING')
		1 * orderMock.isPayPalOrder()
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_GIFT')
	}
	def "handle method to remove gift messaging to shipping grouop throws ShippingGroupNotFound  exception"(){
		given:
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroup("sg1234") >> {throw new ShippingGroupNotFoundException("Mock of ShippingGroupNotFound exception")}
		orderMock.getShippingGroup("sg1235") >> hShip
		orderMock.getShippingGroup("SG12345") >> sShip
		BBBShippingInfoBean shipBean = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean2 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean3 = new BBBShippingInfoBean()
		shipBean2.setShippingGroupId("sg1235")
		shipBean3.setShippingGroupId("sg1234")
		shipBean.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBeanList([null,shipBean1,shipBean,shipBean2,shipBean3])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success
		1 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,hShip,_,shipBean2)
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,sShip,_,shipBean)
		1 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_BILLING')
		1 * orderMock.isPayPalOrder()
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to existing shipping grouop paypal payment group"(){
		given:
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		orderMock.isPayPalOrder() >> true
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success == true
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,_,_,_)
		1 * pTools.priceOrderTotal(orderMock, null, _, profileMock, [:])
		1 * pState.setCurrentLevel('SP_REVIEW')
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_GIFT')
	}
	def "handle method to remove gift messaging to existing shipping grouop paypal payment rest url"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		orderMock.isPayPalOrder() >> true
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success == true
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,_,_,_)
		1 * pTools.priceOrderTotal(orderMock, null, _, profileMock, [:])
		1 * pState.setCurrentLevel('SP_REVIEW')
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_GIFT')
	}

	def "handle method to remove gift messaging to  shipping grouop throws pricing exception"(){
		given:
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		1*pTools.priceOrderTotal(orderMock, _, _, _, _) >> {throw new PricingException("Mock of pricing exception")}
		orderMock.isPayPalOrder() >> true
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success
		0*purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,_,_,_)
		1 * pState.setCurrentLevel('SP_BILLING')
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to shipping grouop throws commerce  exception"(){
		given:
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of commerce exception")}
		spShipObj.setLoggingDebug(true)
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,_,_,_)
		0 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		1 * pState.setCurrentLevel('SP_BILLING')
		1 * orderMock.isPayPalOrder()
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to shipping grouop throws InvalidParameterException  exception"(){
		given:
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroup("sg1234") >> {throw new InvalidParameterException("Mock of InvalidParameterException ")}
		orderMock.getShippingGroup("sg1235") >> hShip
		orderMock.getShippingGroup("SG12345") >> sShip
		BBBShippingInfoBean shipBean = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean2 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean3 = new BBBShippingInfoBean()
		shipBean2.setShippingGroupId("sg1235")
		shipBean3.setShippingGroupId("sg1234")
		shipBean.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBeanList([null,shipBean1,shipBean,shipBean2,shipBean3])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success
		1 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,hShip,_,shipBean2)
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,sShip,_,shipBean)
		1 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_BILLING')
		1 * orderMock.isPayPalOrder()
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to shipping grouop throws InvalidParameterException  exception logging error turn off"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroup("sg1234") >> {throw new InvalidParameterException("Mock of InvalidParameterException ")}
		orderMock.getShippingGroup("sg1235") >> hShip
		orderMock.getShippingGroup("SG12345") >> sShip
		BBBShippingInfoBean shipBean = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean2 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean3 = new BBBShippingInfoBean()
		shipBean2.setShippingGroupId("sg1235")
		shipBean3.setShippingGroupId("sg1234")
		shipBean.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBeanList([null,shipBean1,shipBean,shipBean2,shipBean3])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setLoggingError(false)
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success
		1 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,hShip,_,shipBean2)
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,sShip,_,shipBean)
		1 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_BILLING')
		1 * orderMock.isPayPalOrder()
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to shipping grouop throws CommerceException  exception"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		orderMock.isPayPalOrder() >> true
		payPalSessionBean.isFromPayPalPreview() >> true
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroup("sg1235") >> hShip
		orderMock.getShippingGroup("SG12345") >> sShip
		BBBShippingInfoBean shipBean2 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean3 = new BBBShippingInfoBean()
		shipBean2.setShippingGroupId("sg1235")
		shipBean3.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBeanList([shipBean3,shipBean2])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		pState.getCheckoutFailureURLs() >> ["REVIEW":"url"]
		1 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,hShip,_,shipBean2) >> {throw new CommerceException("Mock of CommerceException")}
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success == false
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,sShip,_,shipBean3)
		1 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_REVIEW')
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to shipping grouop throws BBBBusinessException  exception"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		orderHolderMock.getCurrent() >> orderMock
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroup("sg1235") >> hShip
		orderMock.getShippingGroup("SG12345") >> sShip
		BBBShippingInfoBean shipBean2 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean3 = new BBBShippingInfoBean()
		shipBean2.setShippingGroupId("sg1235")
		shipBean3.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBeanList([shipBean3,shipBean2])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		1 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,hShip,_,shipBean2) >> {throw new BBBBusinessException("Mock of BBBBusinessException")}
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,sShip,_,shipBean3)
		1 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_BILLING')
		1 * orderMock.isPayPalOrder()
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "handle method to remove gift messaging to shipping grouop throws BBBSystemException  exception"(){
		given:
		pState.getFailureURL() >> "atg-rest-ignore"
		orderMock.getShippingGroupCount() >> 1
		shipHelperMock.getPricingTools() >> pTools
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.isPayPalOrder() >> true
		orderMock.getShippingGroup("sg1235") >> hShip
		orderMock.getShippingGroup("SG12345") >> sShip
		BBBShippingInfoBean shipBean2 = new BBBShippingInfoBean()
		BBBShippingInfoBean shipBean3 = new BBBShippingInfoBean()
		shipBean2.setShippingGroupId("sg1235")
		shipBean3.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBeanList([shipBean3,shipBean2])
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		1 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,hShip,_,shipBean2) >> {throw new BBBSystemException("Mock of BBBSystemException")}
		when:
		boolean success = spShipObj.handleGiftMessaging(requestMock,responseMock)
		then:
		success == false
		0 *	purchaseProcessHelperMock.manageAddOrRemoveGiftWrapToShippingGroup(orderMock,,sShip,_,shipBean3)
		1 *	pTools.priceOrderTotal(orderMock, _, _, _, _)
		2 * orderManagerMock.updateOrder(orderMock)
		1 * pState.setCurrentLevel('SP_REVIEW')
		1 * pState.setCurrentLevel('SP_GIFT')
		
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order form "(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.getFormError() >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {} 
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> true
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		//errorMSGMock.getErrMsg("ERROR_SHIPPING_GENERIC_ERROR", "EN", null) >> "ERROR_SHIPPING_GENERIC_ERROR"
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order form  and having failure url"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.getFormError() >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> true
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		//errorMSGMock.getErrMsg("ERROR_SHIPPING_GENERIC_ERROR", "EN", null) >> "ERROR_SHIPPING_GENERIC_ERROR"
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order form throw Exception"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.getFormError() >> false
		spShipObj.isEmailSignUp() >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		spShipObj.runProcessValidateShippingGroups(_,_,_,_,null) >>  {throw new RunProcessException("Mock of Run Process Exception")}
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
		sesssionBeanMock.getEmailChecked().equals("checked")
		1 * orderMock.setPropertyValue('emailSignUp', true)
		2 * pState.setCurrentLevel('SP_SHIPPING_SINGLE')
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order after pipe line call we have errors"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		spShipObj.runProcessValidateShippingGroups(_,_,_,_,null) >>  { spShipObj.getFormError() >>  true }
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order after throws BBBSystemException after giftMesssing Method"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.isCollegeAddress() >> true 
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		orderMock.getShippingGroup("SG12345") >> hShip
		orderMock.isPayPalOrder() >> true
		orderManagerMock.updateOrder(orderMock) >> {spShipObj.getFormError() >> true}
		((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1) >>  {throw new BBBSystemException("Mock of system exception")}
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  true
		sesssionBeanMock.getEmailChecked().equals("unChecked")
		1 * orderMock.setPropertyValue('emailSignUp', false)
		2 * pState.setCurrentLevel('SP_SHIPPING_SINGLE')
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order after throws BBBBusinessException after giftMesssing Method"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		orderMock.getShippingGroup("SG12345") >> hShip
		1*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1) >>  {throw new BBBBusinessException("Mock of Business exception")}
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order after throws commerce after giftMesssing Method"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		orderMock.getShippingGroup("SG12345") >> hShip
		1*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1) >>  {throw new CommerceException("Mock of commerce exception")}
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  true
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order after throws ShippingGroupNotFoundException after giftMesssing Method"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		orderMock.getShippingGroup("SG12345") >> {throw new ShippingGroupNotFoundException("Mock of ShippingGroupNotFound Exception")}
		0*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1)
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order after throws InvalidParameterException after giftMesssing Method"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		orderMock.getShippingGroup("SG12345") >> {throw new InvalidParameterException("Mock of InvalidParameter Exception")}
		0*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1)
		orderMock.isPayPalOrder() >> true
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  true
		orderMock.setPropertyValue("emailSignUp", false)
		sesssionBeanMock.getEmailChecked().equals("unChecked")
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order paypal order"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		1*orderMock.getShippingGroup("SG12345") >> hShip
		1*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1)
		orderMock.isPayPalOrder() >> true
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
		orderMock.setPropertyValue("emailSignUp", false)
		sesssionBeanMock.getEmailChecked().equals("unChecked")
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order paypal order and session value from paypal"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		pState.getCheckoutFailureURLs() >> ["SP_SHIPPING_SINGLE":"SP_SHIPPING_SINGLE"]
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		payPalSessionBean.isFromPayPalPreview() >> true
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		1*orderMock.getShippingGroup("SG12345") >> hShip
		1*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1)
		orderMock.isPayPalOrder() >> true
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
		orderMock.setPropertyValue("emailSignUp", false)
		sesssionBeanMock.getEmailChecked().equals("unChecked")
	}
	def "This handler method will validate shipping address and apply the shipping groups multi request"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		def Transaction tr= Mock()
		spShipObj.ensureTransaction() >> tr
		orderHolderMock.getCurrent() >> orderMock
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
	}
	def "This handler method will validate shipping address and apply the shipping groups throw exception on calling update order"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.setRepeatingRequestMonitor(repRequest)
		repRequest.isUniqueRequestEntry(_) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		def Transaction tr= Mock()
		spShipObj.ensureTransaction() >> tr
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		BBBShippingInfoBean shipBean1 = new BBBShippingInfoBean()
		shipBean1.setShippingGroupId("SG12345")
		spShipObj.setBBBShippingInfoBean(shipBean1)
		1*orderMock.getShippingGroup("SG12345") >> hShip
		1*((BBBPurchaseProcessHelper)purchaseProcessHelperMock).manageAddOrRemoveGiftWrapToShippingGroup(orderMock, hShip, _, shipBean1)
		orderManagerMock.updateOrder(orderMock) >> {throw new Exception("Mock of Exception")}
		orderMock.isPayPalOrder() >> true
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success ==  false
		orderMock.setPropertyValue("emailSignUp", false)
		sesssionBeanMock.getEmailChecked().equals("unChecked")
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order form  sku not found  BBBBusinessException"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.getFormError() >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		orderMock.isPayPalOrder() >> true
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> {throw new BBBBusinessException("Mock of Business exception")}
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		//errorMSGMock.getErrMsg("ERROR_SHIPPING_GENERIC_ERROR", "EN", null) >> "ERROR_SHIPPING_GENERIC_ERROR"
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success
		1*spShipObj.logError("Sku not present in the catalog0476467" , _);
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order form  sku not found  BBBSystemException"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.getFormError() >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.addShipping(requestMock, responseMock) >> {}
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		orderMock.isPayPalOrder() >> true
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		1*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> {throw new BBBSystemException("Mock of Business exception")}
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		//errorMSGMock.getErrMsg("ERROR_SHIPPING_GENERIC_ERROR", "EN", null) >> "ERROR_SHIPPING_GENERIC_ERROR"
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success
		1*spShipObj.logError("Error occurred processing sku0476467",_)
	}
	def "This handler method will validate shipping address and apply the shipping groups to the order form errors after addshipping method"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(false)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.preAddShipping(requestMock,responseMock) >> {}
		spShipObj.addShipping(requestMock, responseMock) >> {spShipObj.getFormError() >> true}
		orderHolderMock.getCurrent() >> orderMock
		Address sdr = new Address()
		sdr.setPostalCode("43206")
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip =Mock()
		def ShippingGroupCommerceItemRelationship relShip = Mock()
		def ShippingGroupCommerceItemRelationship relship1 = Mock()
		def BBBCommerceItem cItem = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "0476465"
		cItem1.getCatalogRefId() >> "0476467"
		relShip.getCommerceItem() >> cItem
		relship1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [relShip,relship1]
		hShip.getShippingAddress() >> sdr
		orderMock.getShippingGroups() >> [sShip,hShip]
		orderMock.getSiteId() >> "BedBathUS"
		orderMock.isPayPalOrder() >> true
		0*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476465","BedBathUS","43206") >> false
		0*catalogUtilMock.isShippingZipCodeRestrictedForSku("0476467","BedBathUS","43206") >> false
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock) >> {}
		spShipObj.postAddShipping(requestMock,responseMock) >> {}
		//errorMSGMock.getErrMsg("ERROR_SHIPPING_GENERIC_ERROR", "EN", null) >> "ERROR_SHIPPING_GENERIC_ERROR"
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success
	}
	
	def "This handler method will validate shipping address and apply the shipping groups to the order form error after pre method"(){
		given:
		spShipObj = Spy()
		requestMock.getContextPath() >> "BedBathUS"
		spShipObj.setCheckoutProgressStates(pState)
		pState.getFailureURL() >> "atg-rest-ignore"
		spyObjIntilization(spShipObj)
		spShipObj.checkFormRedirect(null,"BedBathUSatg-rest-ignore",requestMock,responseMock) >> false
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success == false
	}
	def "This handler method will validate shipping address and apply the shipping groups to the error before synchronisation"(){
		given:
		spShipObj = Spy()
		pState.getFailureURL() >> "atg-rest-ignore-redirect"
		spShipObj.setCheckoutProgressStates(pState)
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.getFormError() >> true
		spShipObj.setCommonConfiguration(cConfig)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setShoppingCart(orderHolderMock)
		spShipObj.setPayPalSessionBean(payPalSessionBean)
		orderHolderMock.getCurrent() >> orderMock
		when:
		boolean success =spShipObj.handleAddShipping(requestMock,responseMock)
		then:
		success
	}
	def "this method to validate preshipping address no shipping groups availalbe"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setShipToAddressName("college")
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> false
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress()
		spShipObj.getNewAddress()
		spShipObj.getShipToAddressName().contains("P12345")
		addContainer.getAddressMap().size() ==  1
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
	}
	def "this method to validate preshipping address have shipping groups availalbe"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> null
		spShipObj.setPackNHold(false)
		spShipObj.setShipToAddressName("userAddress")
		spShipObj.setPoBoxStatus("N")
		spShipObj.setPoBoxFlag("s")
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,["firstName"])
		list.add(1,["lastName"])
		shipHelperMock.checkForRequiredAddressProperties(_,_) >> list
		shipHelperMock.getAddressPropertyNameMap() >> map
		checkoutManagerMock.canItemShipByMethod(_,_,_) >> true
		orderHolderMock.getCurrent() >> orderMock
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress()
		spShipObj.getShipToAddressName().contains("P12345")
		addContainer.getAddressMap().size() ==  1
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe shitp to address name true"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setShipToAddressName("true")
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBSystemException("Mock of BBBSystemException")}
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		shipHelperMock.checkForRequiredAddressProperties(_,_) >> list
		shipHelperMock.getAddressPropertyNameMap() >> map
		checkoutManagerMock.canItemShipByMethod(_,_,_) >> false
		orderHolderMock.getCurrent() >> orderMock
		spShipObj.setPoBoxStatus("Y")
		spShipObj.setPoBoxFlag("p")
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress()
		spShipObj.getShipToAddressName().contains("P12345")
		addContainer.getAddressMap().size() ==  1
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated()
		adr.isPoBoxAddress()
		spShipObj.getRedirectState().equals("SHIPPING_SINGLE")
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		0*checkoutManagerMock.canItemShipByMethod(_,_) >> false
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_IMPROPER_DATE_PACK_HOLD", "EN", null) >> "ERROR_SHIPPING_IMPROPER_DATE_PACK_HOLD"
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and dose not have college item"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> null
		spShipObj.setPackNHold(true)
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_NOT_ALL_PACK_HOLD", "EN", null) >> "ERROR_SHIPPING_NOT_ALL_PACK_HOLD"
		1*checkoutManagerMock.hasEvenSingleCollegeItem(_,orderMock) >> false
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item ParseException"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate("PackNHoldDate")
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_IMPROPER_DATE_PACK_HOLD", "EN", null) >> "ERROR_SHIPPING_NOT_ALL_PACK_HOLD"
		0*checkoutManagerMock.hasEvenSingleCollegeItem(_,orderMock) >> true
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item "(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(-1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_IMPROPER_DATE_PACK_HOLD", "EN", null) >> "ERROR_SHIPPING_NOT_ALL_PACK_HOLD"
		0*checkoutManagerMock.hasEvenSingleCollegeItem(_,orderMock) >> true
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_INVALID_ENDDATE_PACK_HOLD", "EN", null) >> "ERROR_SHIPPING_INVALID_ENDDATE_PACK_HOLD"
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> false
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after throw s exception"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_GENERIC_ERROR", "EN", null) >> "ERROR_SHIPPING_GENERIC_ERROR"
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception("Mock of exception")}
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		1*catalogUtilMock.getDefaultShippingMethod(_) >> {throw new Exception("Mock of exception")}
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after and shipping method is null"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD", "EN", null) >> "ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD"
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception("Mock of exception")}
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		1*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after and shipping method is not  null"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD", "EN", null) >> "ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD"
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> {throw new Exception("Mock of exception")}
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		1*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after,get default shipping method"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> null
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"dd/MM/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		1*errorMSGMock.getErrMsg("ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD", "EN", null) >> "ERROR_SHIPPING_IMPROPER_SHIPPING_METHOD_HOLD"
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*checkoutManagerMock.hasEvenSingleCollegeItem(_,orderMock) >> true
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> true
		1*shipHelperMock.getCatalogTools() >> catalogUtilMock
		1*catalogUtilMock.getDefaultShippingMethod(_) >> null
		spShipObj.getCurrentSiteId() >> "BedBathCanada"
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after and as shipto name is registry"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("registry")
		spShipObj.setCollegeAddress(true)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> true
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		1*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		BBBAddress address = new BBBAddressVO()
		address.setId("registry")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		shipAdrVO.setAddrLine1("AddrLine1")
		shipAdrVO.setAddrLine2("addr2")
		shipAdrVO.setCity("columbus")
		shipAdrVO.setZip("43206")
		shipAdrVO.setState("OH")
		shipAdrVO.setCompanyName("BBB")
		1*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> {throw new BBBBusinessException("Mock of BBBBusinessException")}
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("registry", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		1*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == true
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.getAddress1().equals("AddrLine1")
		adr.getAddress2().equals("addr2")
		adr.getCity().equals("columbus")
		adr.getPostalCode().equals("43206")
		adr.getState().equals("OH")
		adr.getCompanyName().equals("BBB")
		adr.getFirstName().equals("firstName")
		adr.getLastName().equals("lastName")
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after and as shipto name non registry"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("otheraddress")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> true
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		1*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("otheraddress")
		address.setState("columbus")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("otheraddress", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("1")
		def BBBRepositoryContactInfo contactInfo = Mock()
		orderMock.getShippingAddress() >> contactInfo
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged()
		spShipObj.isShippingPhoneChanged()
	}
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after and as shipto name PROFILE"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> true
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		1*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("0")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		orderMock.getShippingAddress() >> contactInfo
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		CommerceItemShippingInfo itemShipinfo = new CommerceItemShippingInfo()
		def BBBCommerceItem cItem = Mock()
		itemShipinfo.setCommerceItem(cItem)
		CommerceItemShippingInfo itemShipinfo1 = new CommerceItemShippingInfo()
		def CommerceItem cItem1 = Mock()
		itemShipinfo1.setCommerceItem(cItem1)
		infoContainer.getAllCommerceItemShippingInfos() >> [itemShipinfo,itemShipinfo1]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		1*checkoutManagerMock.canItemShipToCiSiIndexAddress(_,itemShipinfo, _) >> false
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
		spShipObj.setRedirectState("SP_SHIPPING_SINGLE")
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "43206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("columbus")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption("standard")
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and shipping group as store Shipping group"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "43206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroups() >> [sShip]
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption("standard1")
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		RepositoryItemMock itemMock = new RepositoryItemMock() 
		itemMock.getPropertyValue("shipMethodDescription") >> "shipMethodDescription"
		1*catalogUtilMock.getShippingMethod("standard1") >> itemMock
		spShipObj.setPoBoxFlag("p")
		spShipObj.setPoBoxStatus("Y")
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == true
		adr.isPoBoxAddress() == true
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and shipping group having shipping method"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "43206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "standard"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption("standard")
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and shipping group having shipping method and it not SDD"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		address.setPostalCode("43206")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "43206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "standard"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption(null)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setLoggingError(false)
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and shipping group having shipping method and different postal code"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		 BBBAddress address = new BBBAddressVO()
		address.setId("PROFILE")
		address.setState("columbus")
		address.setPostalCode("43206")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "43206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "standard"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption("SDD")
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		spShipObj.setLoggingError(false)
		1*catalogUtilMock.getShippingMethod("SDD") >> {throw new BBBSystemException("Mock of BBBSystemException")}
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and shipping group having shipping method as SDD"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		 BBBAddress address = new BBBAddressVO()
		address.setId("PROFILE")
		address.setState("columbus")
		address.setPostalCode("43206")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "431206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "SDD"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption("SDD")
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setLoggingError(false)
		def HttpSession session = Mock()
		requestMock.getSession() >> session
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
		spShipObj.getShippingOption().equals("standard")
		1*requestMock.getSession().setAttribute("displayChangedToStd", true)
		spShipObj.isShippingGroupChanged()
	}
	
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and shipping group having shipping method and it SDD"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		address.setPostalCode("43206")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "43206"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "standard"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(shippingMethodMap)
		spShipObj.setShippingOption("SDD")
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setLoggingError(false)
		1*catalogUtilMock.getShippingMethod("SDD") >> {throw new BBBBusinessException("Mock of BBBBusinessException")}
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and current shipping"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		address.setPostalCode("43206")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["false"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		contactInfo.getPostalCode() >> "432106"
		orderMock.getShippingAddress() >> contactInfo
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "SDD"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(null)
		spShipObj.setShippingOption("Standard")
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setLoggingError(false)
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr.getId().equals(spShipObj.getShipToAddressName())
		adr.isQasValidated() == false
		adr.isPoBoxAddress() == false
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "this method to validate preshipping address have shipping groups availalbe college shipto name PROFILE and address null from container"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("PROFILE")
		spShipObj.setCollegeAddress(false)
		def BBBAddressContainer aContainer = Mock()
		spShipObj.setAddressContainer(aContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		def BBBAddress address = Mock()
		address.setId("PROFILE")
		address.setState("columbus")
		address.setPostalCode("43206")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		address.setIsNonPOBoxAddress(true)
		BeddingShipAddrVO shipAdrVO= new BeddingShipAddrVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> shipAdrVO
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie")
		addContainer.addAddressToContainer("PROFILE", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("-1")
		spShipObj.setUpdateAddress(true)
		def BBBHardGoodShippingGroup hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		hShip.getShippingMethod() >> "SDD"
		1*checkoutManagerMock.canItemShipToAddress(_,_, _) >> true
		spShipObj.setPoBoxStatus("n")
		spShipObj.setPoBoxFlag("s")
		BBBAddress adr1 = new BBBAddressVO()
		spShipObj.setAddress(adr1)
		adr1.setAddress1("parsons ave")
		adr1.setState("AA")
		adr1.setEmail("bbb@test.com")
		adr1.setPhoneNumber("9642394258")
		Properties shippingMethodMap = new Properties()
		shippingMethodMap.put("standard", "standard")
		spShipObj.setStates(["AA","AE","AP"])
		spShipObj.setShippingMethodMap(null)
		spShipObj.setShippingOption("Standard")
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		spShipObj.setLoggingError(false)
		2*aContainer.getAddressFromContainer("PROFILE") >> null
		aContainer.getAddressMap() >> [:]
		def OrderHolder orderHolderMock1 = Mock()
		orderHolderMock1.getCurrent() >> orderMock >> orderMock >> orderMock >> orderMock >> orderMock >> orderMock >> orderMock >> orderMock >> null
		spShipObj.setShoppingCart(orderHolderMock1)
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == false
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		spShipObj.isShippingEmailChanged() == true
		1*aContainer.addAddressToContainer("PROFILE", _)
	}
	
	def "this method to validate preshipping address have shipping groups availalbe college id value is null and at least single college item pack hold date is day after and as shipto name collage address"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName("12")
		spShipObj.setCollegeAddress(true)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		1*catalogUtilMock.isPackNHoldWindow(_, _) >> true
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		1*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		 BBBAddress address = new BBBAddressVO()
		address.setId("registry")
		address.setEmail("bbb1@test.com")
		address.setPhoneNumber("9542394258")
		address.setState("columbus")
		requestMock.getParameter("checkoutfirstName") >> "firstName"
		requestMock.getParameter("checkoutlastName") >> "lastName"
		1*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> null
		1*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
		1*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie") >> null
		addContainer.addAddressToContainer("PROFILE12", address)
		spShipObj.setLTLCommerceItem(true)
		List<List<String>> list = new ArrayList<List<String>>()
		list.add(0,[])
		list.add(1,[])
		1*shipHelperMock.checkForRequiredLTLAddressProperties(_,_) >> list
		0*checkoutManagerMock.canItemShipToAddress(_,_,_) >> false
		spShipObj.setCisiIndex("0")
		spShipObj.setUpdateAddress(true)
		def BBBRepositoryContactInfo contactInfo = Mock()
		contactInfo.getEmail() >> "bbb@test.com"
		contactInfo.getPhoneNumber() >> "9642394258"
		orderMock.getShippingAddress() >> contactInfo
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		CommerceItemShippingInfo itemShipinfo = new CommerceItemShippingInfo()
		def BBBCommerceItem cItem = Mock()
		itemShipinfo.setCommerceItem(cItem)
		CommerceItemShippingInfo itemShipinfo1 = new CommerceItemShippingInfo()
		def CommerceItem cItem1 = Mock()
		itemShipinfo1.setCommerceItem(cItem1)
		infoContainer.getAllCommerceItemShippingInfos() >> [itemShipinfo,itemShipinfo1]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		1*checkoutManagerMock.canItemShipToCiSiIndexAddress(_,itemShipinfo, _) >> true
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == true
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr == null
		spShipObj.isShippingEmailChanged()
		spShipObj.isShippingPhoneChanged()
	}
	def "this method to validate preshipping address have shipping groups availalbe  shipto name  null"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		requestMock.getCookieParameter("SchoolCookie") >> "SchoolCookie"
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1,"MM/dd/yyyy"))
		spShipObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "P12345"
		spShipObj.setShipToAddressName(null)
		spShipObj.setCollegeAddress(true)
		spShipObj.setAddressContainer(addContainer)
		spShipObj.setShippingGroupManager(sGroupManager)
		Locale local = new Locale("en")
		requestMock.getLocale() >> local
		shipHelperMock.getCatalogTools() >> catalogUtilMock
		ShipMethodVO defaultShippingMethod = new ShipMethodVO()
		defaultShippingMethod.setShipMethodId("Standrad")
		spShipObj.setShippingOption("Standrad")
		0*catalogUtilMock.getDefaultShippingMethod(_) >> defaultShippingMethod
		1*sGroupManager.isAnyHardgoodShippingGroups(orderMock) >> true
		 BBBAddress address = new BBBAddressVO()
		0*catalogUtilMock.validateBedingKitAtt(_,"SchoolCookie") >> null
		0*catalogUtilMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON) >> ["true"]
		0*catalogUtilMock.getBeddingShipAddrVO("SchoolCookie") >> null
		BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.preAddShipping(requestMock,responseMock)
		then:
		spShipObj.isCollegeAddress() == true
		spShipObj.getNewAddress() == false
		BBBAddress adr = addContainer.addressMap.get(spShipObj.getShipToAddressName())
		adr == null
		spShipObj.isShippingEmailChanged() == false
		spShipObj.isShippingPhoneChanged() == false
	}
	def "Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the order"(){
		given:
		Locale lc = new Locale("EN")
		requestMock.getLocale() >> lc
		spShipObj.setShipToAddressName("registry")
		BBBAddress adr = new BBBAddressVO()
		adr.setId("Address1234")
		addContainer.addAddressToContainer("registry", adr)
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
		spShipObj.setSendShippingConfEmail(true)
		spShipObj.setSendShippingEmail("bbb@test.com")
		spShipObj.setShippingOption("Standard")
		spShipObj.setAddressContainer(addContainer)
		def BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		hShip.getId() >>  "SG12345"
		RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
		rSummaryVO.setPrimaryRegistrantFirstName("first")
		rSummaryVO.setPrimaryRegistrantLastName("Last")
		rSummaryVO.setCoRegistrantFirstName("coFirst")
		orderMock.getRegistryMap() >> [Address1234:rSummaryVO]
		1*checkoutManagerMock.getItemsRegistryCount(_) >> 1
		when:
		spShipObj.addShipping(requestMock, responseMock)
		then:
		spShipObj.getBBBShippingInfoBean().getAddress() == adr
		spShipObj.getBBBShippingInfoBean().getPackAndHoldDate().equals(new SimpleDateFormat("MM/dd/yyyy").parse(spShipObj.getPackNHoldDate()))
		spShipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals("bbb@test.com")
		spShipObj.getBBBShippingInfoBean().isSendShippingConfirmation()
		spShipObj.getBBBShippingInfoBean().getShippingMethod().equals("Standard")
		spShipObj.getBBBShippingInfoBean().getShippingGroupId().equals("SG12345")
		spShipObj.getBBBShippingInfoBean().getRegistryId().equals("Address1234")
		spShipObj.getBBBShippingInfoBean().getRegistryInfo().equals("<strong>first Last & coFirst null</strong> (Registry #Address1234)")
		1*sGroupManager.shipToAddress(_,hShip)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
	def "Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the order SendShippingConfEmail false"(){
		given:
		Locale lc = new Locale("EN")
		requestMock.getLocale() >> lc
		spShipObj.setShipToAddressName("registry")
		BBBAddress adr = new BBBAddressVO()
		adr.setId("Address1234")
		addContainer.addAddressToContainer("registry", adr)
		spShipObj.setPackNHold(false)
		spShipObj.setPackNHoldDate(addDays(1, "MM/dd/yyyy"))
		spShipObj.setSendShippingConfEmail(false)
		spShipObj.setSendShippingEmail("bbb@test.com")
		spShipObj.setShippingOption("Standard")
		spShipObj.setAddressContainer(addContainer)
		def BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		hShip.getId() >>  "SG12345"
		1*sGroupManager.shipToAddress(_,hShip) >> {throw new RepositoryException()}
		RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
		rSummaryVO.setPrimaryRegistrantFirstName("first")
		rSummaryVO.setPrimaryRegistrantLastName("Last")
		orderMock.getRegistryMap() >> [Address1234:rSummaryVO]
		1*checkoutManagerMock.getItemsRegistryCount(_) >> 1
		when:
		spShipObj.addShipping(requestMock, responseMock)
		then:
		spShipObj.getBBBShippingInfoBean().getAddress() == adr
		spShipObj.getBBBShippingInfoBean().getPackAndHoldDate() == null
		spShipObj.getBBBShippingInfoBean().getShippingConfirmationEmail().equals("bbb@test.com") == false
		spShipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
		spShipObj.getBBBShippingInfoBean().getShippingMethod().equals("Standard")
		spShipObj.getBBBShippingInfoBean().getShippingGroupId().equals("SG12345")
		spShipObj.getBBBShippingInfoBean().getRegistryId().equals("Address1234")
		spShipObj.getBBBShippingInfoBean().getRegistryInfo().equals("<strong>first Last</strong> (Registry #Address1234)")
		0*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
	def "Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the order item from registry is Zero"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		Locale lc = new Locale("EN")
		requestMock.getLocale() >> lc
		spShipObj.setShipToAddressName(null)
		BBBAddress adr = new BBBAddressVO()
		adr.setId("Address1234")
		addContainer.addAddressToContainer(null, adr)
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate("sdasda")
		spShipObj.setSendShippingConfEmail(true)
		spShipObj.setSendShippingEmail("bbb@test.com")
		spShipObj.setShippingOption("Standard")
		spShipObj.setAddressContainer(addContainer)
		def BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		hShip.getId() >>  "SG12345"
		1*sGroupManager.shipToAddress(_,hShip) >> {throw new IntrospectionException()}
		RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
		rSummaryVO.setPrimaryRegistrantFirstName("first")
		rSummaryVO.setPrimaryRegistrantLastName("Last")
		orderMock.getRegistryMap() >> ["Address1234":rSummaryVO]
		1*checkoutManagerMock.getItemsRegistryCount(_) >> 0
		when:
		spShipObj.addShipping(requestMock, responseMock)
		then:
		spShipObj.getBBBShippingInfoBean().getAddress() == adr
		spShipObj.getBBBShippingInfoBean().getPackAndHoldDate() == null
		spShipObj.getBBBShippingInfoBean().getShippingConfirmationEmail() == null
		spShipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
		spShipObj.getBBBShippingInfoBean().getShippingMethod().equals("Standard")
		spShipObj.getBBBShippingInfoBean().getShippingGroupId().equals("SG12345")
		spShipObj.getBBBShippingInfoBean().getRegistryId() == null
		spShipObj.getBBBShippingInfoBean().getRegistryInfo() == null
		1*spShipObj.logError(LogMessageFormatter.formatMessage(requestMock, "PACK_DATE_NOT_PROPER"), _)
		1*spShipObj.logError(" some error occured while updating shipping address", _)
		0*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
	def "Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the order item from shiptoname is college"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		Locale lc = new Locale("EN")
		requestMock.getLocale() >> lc
		spShipObj.setShipToAddressName("college")
		BBBAddress adr = new BBBAddressVO()
		adr.setId("Address12345")
		addContainer.addAddressToContainer("college", adr)
		spShipObj.setPackNHold(true)
		spShipObj.setPackNHoldDate(null)
		spShipObj.setSendShippingConfEmail(true)
		spShipObj.setSendShippingEmail("bbb@test.com")
		spShipObj.setShippingOption("Standard")
		spShipObj.setAddressContainer(addContainer)
		def BBBHardGoodShippingGroup hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		hShip.getId() >>  "SG12345"
		1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new CommerceException("Mock of commerece Exception")}
		RegistrySummaryVO rSummaryVO = new RegistrySummaryVO()
		rSummaryVO.setPrimaryRegistrantFirstName("first")
		rSummaryVO.setPrimaryRegistrantLastName("Last")
		orderMock.getRegistryMap() >> ["Address1234":rSummaryVO]
		1*checkoutManagerMock.getItemsRegistryCount(_) >> 1
		when:
		spShipObj.addShipping(requestMock, responseMock)
		then:
		spShipObj.getBBBShippingInfoBean().getAddress() == adr
		spShipObj.getBBBShippingInfoBean().getPackAndHoldDate() == null
		spShipObj.getBBBShippingInfoBean().getShippingConfirmationEmail() == null
		spShipObj.getBBBShippingInfoBean().isSendShippingConfirmation() == false
		spShipObj.getBBBShippingInfoBean().getShippingMethod().equals("Standard")
		spShipObj.getBBBShippingInfoBean().getShippingGroupId().equals("SG12345")
		spShipObj.getBBBShippingInfoBean().getRegistryId() == null
		spShipObj.getBBBShippingInfoBean().getRegistryInfo() == null
		1*sGroupManager.shipToAddress(_,hShip)
		1*spShipObj.logError(" some error occured while updating shipping address", _)
	}
	def "This method will update coupons for guest user for BedBathUS"(){
		given:
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.getContentCatalogConfigration("CouponTag_us") >> ["true"]
		profileMock.isTransient() >> true
		spShipObj.setShippingEmailChanged(true)
		spShipObj.setShippingPhoneChanged(false)
		RepositoryItemMock item = new RepositoryItemMock()
		RepositoryItemMock item1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>[item,item1]
		1*promotools.removePromotion(profileMock, item, false)
		1*promotools.removePromotion(profileMock, item1, false)
		Map map = ["promo":item,"promo1":item1]
		1*pLooLupManager.populateSPCValidPromotions(profileMock, orderMock,"BedBathUS") >> map
		1*couponutil.applySchoolPromotion(map, profileMock, orderMock) >> map
		when:
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock)
		then:
		1*promotools.initializePricingModels(requestMock, responseMock)
		1*profileMock.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, _)
		1*orderMock.setCouponMap(map)
	}
	def "This method will update coupons for guest user for BuyBuyBaby"(){
		given:
		orderMock.getSiteId() >> "BuyBuyBaby"
		1*catalogUtilMock.getContentCatalogConfigration("CouponTag_baby") >> ["true"]
		profileMock.isTransient() >> true
		spShipObj.setShippingEmailChanged(false)
		spShipObj.setShippingPhoneChanged(true)
		RepositoryItemMock item = new RepositoryItemMock()
		RepositoryItemMock item1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>[item,item1]
		1*promotools.removePromotion(profileMock, item, false)
		1*promotools.removePromotion(profileMock, item1, false)
		Map map = ["promo":item,"promo1":item1]
		1*pLooLupManager.populateSPCValidPromotions(profileMock, orderMock,"BuyBuyBaby") >> map
		1*couponutil.applySchoolPromotion(map, profileMock, orderMock) >> map
		when:
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock)
		then:
		1*promotools.initializePricingModels(requestMock, responseMock)
		1*profileMock.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, _)
		1*orderMock.setCouponMap(map)
	}
	def "This method will update coupons for guest user for BuyBuyBaby and coupon not avialbe"(){
		given:
		orderMock.getSiteId() >> "BuyBuyBaby"
		1*catalogUtilMock.getContentCatalogConfigration("CouponTag_baby") >> ["false"]
		profileMock.isTransient() >> true
		spShipObj.setShippingEmailChanged(false)
		spShipObj.setShippingPhoneChanged(true)
		RepositoryItemMock item = new RepositoryItemMock()
		RepositoryItemMock item1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>[item,item1]
		0*promotools.removePromotion(profileMock, item, false)
		0*promotools.removePromotion(profileMock, item1, false)
		Map map = ["promo":item,"promo1":item1]
		0*pLooLupManager.populateSPCValidPromotions(profileMock, orderMock,"BuyBuyBaby") >> map
		0*couponutil.applySchoolPromotion(map, profileMock, orderMock) >> map
		when:
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock)
		then:
		0*promotools.initializePricingModels(requestMock, responseMock)
		0*profileMock.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, _)
		0*orderMock.setCouponMap(map)
	}
	def "This method will update coupons for guest user for BedBathUSTBS email id and phone number is not changed"(){
		given:
		orderMock.getSiteId() >> "BedBathUSTBS"
		0*catalogUtilMock.getContentCatalogConfigration(_) >> ["true"]
		profileMock.isTransient() >> true
		spShipObj.setShippingEmailChanged(false)
		spShipObj.setShippingPhoneChanged(false)
		RepositoryItemMock item = new RepositoryItemMock()
		RepositoryItemMock item1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>[item,item1]
		0*promotools.removePromotion(profileMock, item, false)
		0*promotools.removePromotion(profileMock, item1, false)
		Map map = ["promo":item,"promo1":item1]
		0*pLooLupManager.populateSPCValidPromotions(profileMock, orderMock,"BedBathUSTBS") >> map
		0*couponutil.applySchoolPromotion(map, profileMock, orderMock) >> map
		when:
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock)
		then:
		0*promotools.initializePricingModels(requestMock, responseMock)
		0*profileMock.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, _)
	}
	def "This method will update coupons for guest user for BedBathCanada"(){
		given:
		orderMock.getSiteId() >> "BedBathCanada"
		1*catalogUtilMock.getContentCatalogConfigration("CouponTag_ca") >> ["true"]
		profileMock.isTransient() >> false
		spShipObj.setShippingEmailChanged(false)
		spShipObj.setShippingPhoneChanged(false)
		RepositoryItemMock item = new RepositoryItemMock()
		RepositoryItemMock item1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>[item,item1]
		0*promotools.removePromotion(profileMock, item, false)
		0*promotools.removePromotion(profileMock, item1, false)
		Map map = ["promo":item,"promo1":item1]
		0*pLooLupManager.populateSPCValidPromotions(profileMock, orderMock,"BedBathCanada") >> map
		0*couponutil.applySchoolPromotion(map, profileMock, orderMock) >> map
		when:
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock)
		then:
		0*promotools.initializePricingModels(requestMock, responseMock)
		0*profileMock.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, _)
		0*orderMock.setCouponMap(map)
	}
	
	def "This method will update coupons for guest user for BedBathUS throw excpetion"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		orderMock.getSiteId() >> "BedBathUS"
		1*catalogUtilMock.getContentCatalogConfigration("CouponTag_us") >> ["true"]
		profileMock.isTransient() >> true
		spShipObj.setShippingEmailChanged(true)
		spShipObj.setShippingPhoneChanged(false)
		RepositoryItemMock item = new RepositoryItemMock()
		RepositoryItemMock item1 = new RepositoryItemMock()
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >>[item,item1]
		1*promotools.removePromotion(profileMock, item, false)
		1*promotools.removePromotion(profileMock, item1, false)
		Map map = ["promo":item,"promo1":item1]
		1*pLooLupManager.populateSPCValidPromotions(profileMock, orderMock,"BedBathUS") >> map
		1*couponutil.applySchoolPromotion(map, profileMock, orderMock) >> {throw new BBBBusinessException("Mock of Buisness exception")}
		when:
		spShipObj.updateCouponDetails(orderMock,requestMock,responseMock)
		then:
		1*promotools.initializePricingModels(requestMock, responseMock)
		1*profileMock.setPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, _)
		0*orderMock.setCouponMap(map)
		1*spShipObj.logError("Mock of Buisness exception")
	}
	def "This method will reprice the order to catch address problems through CyberSource"(){
		given:
		spShipObj.setShippingOption("SDD")
		def BBBHardGoodShippingGroup  hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		spShipObj.setSessionBean(sesssionBeanMock)
		sesssionBeanMock.getSddStoreId() >> "12345"
		shipHelperMock.getPricingTools() >> pTools
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.postAddShipping(requestMock,responseMock)
		then:
		1 * hShip.setPropertyValue('sddStoreId', '12345')
		1*pTools.priceOrderTotal(orderMock, _, _, profileMock, _)
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method will reprice the order to catch address problems through CyberSource and store id is null"(){
		given:
		spShipObj.setShippingOption("SDD")
		def BBBHardGoodShippingGroup  hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		spShipObj.setSessionBean(sesssionBeanMock)
		shipHelperMock.getPricingTools() >> pTools
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.postAddShipping(requestMock,responseMock)
		then:
		0*hShip.setSddStoreId("12345")
		1*pTools.priceOrderTotal(orderMock, _, _, profileMock, _)
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method will reprice the order to catch address problems through CyberSource and shipping option is null"(){
		given:
		spShipObj.setShippingOption(null)
		def BBBHardGoodShippingGroup  hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		spShipObj.setSessionBean(sesssionBeanMock)
		shipHelperMock.getPricingTools() >> pTools
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.postAddShipping(requestMock,responseMock)
		then:
		0*hShip.setSddStoreId("12345")
		1*pTools.priceOrderTotal(orderMock, _, _, profileMock, _)
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method will reprice the order to catch address problems through CyberSource exception getFormError() true"(){
		given:
		spShipObj.setShippingOption("standard")
		def BBBHardGoodShippingGroup  hShip = Mock()
		orderMock.getShippingGroups() >> [hShip]
		spShipObj.setSessionBean(sesssionBeanMock)
		sesssionBeanMock.getSddStoreId() >> "12345"
		shipHelperMock.getPricingTools() >> pTools
		0*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		1*pTools.priceOrderTotal(orderMock, _, _, profileMock, _) >> {throw new PricingException()}
		when:
		spShipObj.postAddShipping(requestMock,responseMock)
		then:
		0*hShip.setSddStoreId("12345")
		0*hShip.setSourceId(Long.toString(new Date().getTime()))
	}
	def "This method will reprice the order to catch address problems through CyberSource exception getFormError() true after pricing"(){
		given:
		spShipObj =Spy()
		spShipObj.getFormError() >> true
		when:
		spShipObj.postAddShipping(requestMock,responseMock)
		then:
		0*pTools.priceOrderTotal(orderMock, _, _, profileMock, _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(true)
		def BBBHardGoodShippingGroup  hShip = Mock()
		RepositoryItemMock shipItem = new RepositoryItemMock()
		shipItem.setRepositoryId("sh1234")
		RepositoryItemMock billItem = new RepositoryItemMock()
		billItem.setRepositoryId("ba1234")
		profileTools.getDefaultShippingAddress(profileMock) >> shipItem
		profileTools.getDefaultBillingAddress(profileMock) >> billItem
		spShipObj.setCurrentAddressID("sh1234")
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * pManager.updateAddressForProfile(profileMock, _, true, false, false, "nickname", "newNickname")
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option billing address update"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(true)
		def BBBHardGoodShippingGroup  hShip = Mock()
		RepositoryItemMock shipItem = new RepositoryItemMock()
		shipItem.setRepositoryId("sh1234")
		RepositoryItemMock billItem = new RepositoryItemMock()
		billItem.setRepositoryId("ba1234")
		profileTools.getDefaultShippingAddress(profileMock) >> shipItem
		profileTools.getDefaultBillingAddress(profileMock) >> billItem
		spShipObj.setCurrentAddressID("ba1234")
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * pManager.updateAddressForProfile(profileMock, _, false, true, false, "nickname", "newNickname")
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address and address from profile in null"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> null
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		1*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true) >> rtnAddressVO
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', "Identifier")
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address and address from profile in empty"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> [:]
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		1*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true) >> rtnAddressVO
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', "Identifier")
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address and address from profile is not empty"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"sada"]
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		1*adrAPI.addNewShippingAddress(profileMock,adr,_,false,false) >> rtnAddressVO
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', "Identifier")
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address and address for registered user"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		profileMock.isTransient() >> true
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"sada"]
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,false,false)
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true)
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address and address id from registryr"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("registry")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"sada"]
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,false,false)
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true)
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address is not from single page checkout"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(false)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("registry")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,false,false)
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true)
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding new address is not to save the shipping address"(){
		given:
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(false)
		spShipObj.setShipToAddressName("registry")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		1*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> ["asa":"sada"]
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,false,false)
		0*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true)
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		1 * hShip.setPropertyValue('sourceId', _)
	}
	def "This method initializes the billing address from the shipping address if the user selected that option adding throws exception"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSingleShippingGroupCheckout(true)
		spShipObj.setSaveShippingAddress(true)
		spShipObj.setShipToAddressName("college")
		spShipObj.setUpdateAddress(false)
		def BBBHardGoodShippingGroup  hShip = Mock()
		0*sGroupManager.getFirstNonGiftHardgoodShippingGroupWithRels(orderMock) >> hShip
		profileMock.getPropertyValue("secondaryAddresses") >> null
		checkoutManagerMock.getProfileAddressTool() >>  adrAPI
		spShipObj.setAddressContainer(addContainer)
		BBBAddress adr = new BBBAddressVO()
		addContainer.addAddressToContainer("college", adr)
		BBBAddressVO rtnAddressVO = new BBBAddressVO()
		rtnAddressVO.setIdentifier("Identifier")
		1*adrAPI.addNewShippingAddress(profileMock,adr,_,true,true) >> { throw new BBBSystemException("Mock of System Exception")}
		when:
		spShipObj.postShipToNewAddress(requestMock, responseMock)
		then:
		0 * hShip.setPropertyValue('sourceId', "Identifier")
		1*spShipObj.logError('Error while saving address',_)
	}
	def "populating vlues from address object to map"(){
		given:
		BBBAddress adr = new BBBAddressVO()
		adr.setFirstName("first")
		adr.setLastName("last")
		adr.setPostalCode("43206")
		adr.setCity("columbus")
		adr.setState("OH")
		adr.setCountry("US")
		adr.setAddress1("parsons ave")
		adr.setAddress2("address2")
		adr.setCompanyName("company name")
		spShipObj.setNickname("nickname")
		when:
		Map map = spShipObj.getEditValueMap(adr)
		then:
		map.size() == 12
		map.get(BBBCoreConstants.CC_LAST_NAME).equals("last")
		map.get(BBBCoreConstants.CC_FIRST_NAME).equals("first")
		map.get(BBBCoreConstants.CC_POSTAL_CODE).equals("43206")
		map.get(BBBCoreConstants.CC_NICKNAME).equals("nickname")
		map.get(BBBCoreConstants.CC_STATE).equals("OH")
		map.get(BBBCoreConstants.CC_COUNTRY).equals("US")
		map.get(BBBCoreConstants.CC_ADDRESS1).equals("parsons ave")
		map.get(BBBCoreConstants.CC_ADDRESS2).equals("address2")
		map.get(BBBCoreConstants.CC_COMPANY_NAME).equals("company name")
		map.get(BBBCoreConstants.CC_FIRST_NAME).equals("first")
		map.get(BBBCoreConstants.CC_QASVALIDATED) == false
		map.get(BBBCoreConstants.CC_POBOXADDRESS) == false
	}
	def "populating vlues from address object to map poboxflag as q"(){
		given:
		BBBAddress adr = new BBBAddressVO()
		adr.setFirstName("first")
		adr.setLastName("last")
		adr.setPostalCode("43206")
		adr.setCity("columbus")
		adr.setState("OH")
		adr.setCountry("US")
		adr.setAddress1("parsons ave")
		adr.setAddress2("address2")
		adr.setCompanyName("company name")
		spShipObj.setNickname("nickname")
		spShipObj.setPoBoxFlag("q")
		spShipObj.setPoBoxStatus("N")
		when:
		Map map = spShipObj.getEditValueMap(adr)
		then:
		map.size() == 12
		map.get(BBBCoreConstants.CC_LAST_NAME).equals("last")
		map.get(BBBCoreConstants.CC_FIRST_NAME).equals("first")
		map.get(BBBCoreConstants.CC_POSTAL_CODE).equals("43206")
		map.get(BBBCoreConstants.CC_NICKNAME).equals("nickname")
		map.get(BBBCoreConstants.CC_STATE).equals("OH")
		map.get(BBBCoreConstants.CC_COUNTRY).equals("US")
		map.get(BBBCoreConstants.CC_ADDRESS1).equals("parsons ave")
		map.get(BBBCoreConstants.CC_ADDRESS2).equals("address2")
		map.get(BBBCoreConstants.CC_COMPANY_NAME).equals("company name")
		map.get(BBBCoreConstants.CC_FIRST_NAME).equals("first")
		map.get(BBBCoreConstants.CC_QASVALIDATED) == false
		map.get(BBBCoreConstants.CC_POBOXADDRESS) == false
		
	}
	def "populating vlues from address object to map poboxflag as p"(){
		given:
		BBBAddress adr = new BBBAddressVO()
		adr.setFirstName("first")
		adr.setLastName("last")
		adr.setPostalCode("43206")
		adr.setCity("columbus")
		adr.setState("OH")
		adr.setCountry("US")
		adr.setAddress1("parsons ave")
		adr.setAddress2("address2")
		adr.setCompanyName("company name")
		spShipObj.setNickname("nickname")
		spShipObj.setPoBoxFlag("P")
		spShipObj.setPoBoxStatus("Y")
		when:
		Map map = spShipObj.getEditValueMap(adr)
		then:
		map.size() == 12
		map.get(BBBCoreConstants.CC_LAST_NAME).equals("last")
		map.get(BBBCoreConstants.CC_FIRST_NAME).equals("first")
		map.get(BBBCoreConstants.CC_POSTAL_CODE).equals("43206")
		map.get(BBBCoreConstants.CC_NICKNAME).equals("nickname")
		map.get(BBBCoreConstants.CC_STATE).equals("OH")
		map.get(BBBCoreConstants.CC_COUNTRY).equals("US")
		map.get(BBBCoreConstants.CC_ADDRESS1).equals("parsons ave")
		map.get(BBBCoreConstants.CC_ADDRESS2).equals("address2")
		map.get(BBBCoreConstants.CC_COMPANY_NAME).equals("company name")
		map.get(BBBCoreConstants.CC_FIRST_NAME).equals("first")
		map.get(BBBCoreConstants.CC_QASVALIDATED) == true
		map.get(BBBCoreConstants.CC_POBOXADDRESS) == true
		
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
		spShipObj.clearExceptionsFromRequest(requestMock)
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
		spShipObj.clearExceptionsFromRequest(requestMock)
		then:
		1*requestMock.setParameter("exceptions", null)
	}
	def "clear the exception when session expired name context is null"(){
		given:
		requestMock.getRequestScope() >>null
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		spShipObj.clearExceptionsFromRequest(requestMock)
		then:
		1*requestMock.setParameter("exceptions", null)
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
	def CommerceItem cItem = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	def BBBCommerceItem cItem5 = Mock()
	def BBBCommerceItem cItem6 = Mock()
	scRel.getCommerceItem() >> cItem
	scRel1.getCommerceItem() >> cItem1
	scRel2.getCommerceItem() >> cItem2
	scRel3.getCommerceItem() >> cItem3
	scRel4.getCommerceItem() >> cItem4
	scRel5.getCommerceItem() >> cItem5
	scRel6.getCommerceItem() >> cItem6
	cItem2.isLtlItem() >> true
	cItem2.getWhiteGloveAssembly() >> "false"
	cItem2.getCatalogRefId() >> "sku12345"
	cItem2.getId() >> "ci1234"
	cItem3.isLtlItem() >> true
	cItem3.getWhiteGloveAssembly() >> "true"
	cItem3.getCatalogRefId() >> "sku12346"
	cItem3.getId() >> "ci1235"
	def ShippingGroup sg = Mock()
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
	sg.getCommerceItemRelationships() >>  [scRel,scRel1,scRel2,scRel3,scRel4,scRel5,scRel6]
	when:
	Map map= spShipObj.createSkuToCommItemsMap(sg)
	then:
	map.size() == 3
	((List)map.get("sku12346Assembly")).size() == 2
	((List)map.get("sku12346Assembly")).get(0).equals("ci1235")
	((List)map.get("sku12346Assembly")).get(1).equals("ci1236")
	((List)map.get("sku12345")).size() == 2
	((List)map.get("sku12345")).get(0).equals("ci1234")
	((List)map.get("sku12345")).get(1).equals("ci1237")
	((List)map.get("sku12347")).get(0).equals("ci1238")
	}
	def "This method creates map of commerce items with same sku in a shipping group is empty."(){
		given:
		def ShippingGroup sg = Mock()
		sg.getCommerceItemRelationships() >>  []
		when:
		Map map= spShipObj.createSkuToCommItemsMap(sg)
		then:
		map.size() == 0
		}
	def "This method is Add System Exceptio"(){
	given:
	spShipObj.setTransactionManager(tManager)
	tManager.getStatus() >> javax.transaction.Status.STATUS_COMMITTED
	errorMSGMock.getErrMsg("Message", "EN", null) >> "Message"
	when:
	spShipObj.addSystemException("Message",new Exception())
	then:
	1 *	errorMSGMock.getErrMsg("Message", "EN", null)
	}
	def "This method is Add System Exception tool back as false"(){
	given:
	spShipObj.setLoggingError(false)
	spShipObj.setTransactionManager(tManager)
	tManager.getStatus() >> javax.transaction.Status.STATUS_MARKED_ROLLBACK
	errorMSGMock.getErrMsg("Message", "EN", null) >> "Message"
	when:
	spShipObj.addSystemException("Message",new Exception())
	then:
	1 *	errorMSGMock.getErrMsg("Message", "EN", null)
	}
	def "This method is Add System Exception throw SystemException"(){
		given:
		spShipObj.setTransactionManager(tManager)
		tManager.getStatus() >> javax.transaction.Status.STATUS_COMMITTED
		tManager.getTransaction() >> {throw new SystemException("Mock of system exception")}
		errorMSGMock.getErrMsg("Message", "EN", null) >> "Message"
		when:
		spShipObj.addSystemException("Message",new Exception())
		then:
		1 *	errorMSGMock.getErrMsg("Message", "EN", null)
		}
	def "Applies the address to new HardGoodShippingGroup in order"(){
		given:
		spShipObj.setSaveShippingAddress(true)
		 BBBAddressImpl newAddress = new BBBAddressImpl()
		newAddress.setIdentifier("addressSaved")
		1*checkoutManagerMock.saveAddressToProfile(profileMock, _,_) >> newAddress 
		spShipObj.setCisiIndex("1")
		def CommerceItemShippingInfo relItem =Mock()
		def BBBCommerceItem itemMock = Mock()
		def CommerceItemShippingInfo relItem1 =Mock()
		def BBBCommerceItem itemMock1 = Mock()
		relItem.getCommerceItem() >> itemMock
		relItem1.getCommerceItem() >> itemMock1
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		spShipObj.setShippingAddressContainer(addContainer)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		spShipObj.addNewAddress(requestMock,responseMock)
		then:
		1*relItem1.setShippingGroupName("addressSaved")
		1*requestMock.setParameter("newAddressKey", "addressSaved")
	}
	def "Applies the address to new HardGoodShippingGroup in order not save address in profile"(){
		given:
		spShipObj.setSaveShippingAddress(false)
		0*checkoutManagerMock.saveAddressToProfile(profileMock, _,_)
		spShipObj.setCisiIndex("-1")
		def CommerceItemShippingInfo relItem =Mock()
		def BBBCommerceItem itemMock = Mock()
		def CommerceItemShippingInfo relItem1 =Mock()
		def BBBCommerceItem itemMock1 = Mock()
		relItem.getCommerceItem() >> itemMock
		relItem1.getCommerceItem() >> itemMock1
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		spShipObj.setShippingAddressContainer(addContainer)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		spShipObj.addNewAddress(requestMock,responseMock)
		then:
		0*relItem1.setShippingGroupName("addressSaved")
		1*requestMock.setParameter("newAddressKey",_)
	}
	def "Applies the address to new HardGoodShippingGroup in order throw system exception"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSaveShippingAddress(true)
		 BBBAddressImpl newAddress = new BBBAddressImpl()
		newAddress.setIdentifier("addressSaved")
		1*checkoutManagerMock.saveAddressToProfile(profileMock, _,_) >> { throw new BBBSystemException("Mock of system exception")}
		spShipObj.setCisiIndex("1")
		def CommerceItemShippingInfo relItem =Mock()
		def BBBCommerceItem itemMock = Mock()
		def CommerceItemShippingInfo relItem1 =Mock()
		def BBBCommerceItem itemMock1 = Mock()
		relItem.getCommerceItem() >> itemMock
		relItem1.getCommerceItem() >> itemMock1
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		spShipObj.setShippingAddressContainer(addContainer)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		spShipObj.addNewAddress(requestMock,responseMock)
		then:
		0*relItem1.setShippingGroupName("addressSaved")
		0*requestMock.setParameter("newAddressKey", "addressSaved")
		1*spShipObj.logError('error occourred while saving the address into profile',_)
	}
	def "Applies the address to new HardGoodShippingGroup in order throw Business exception"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		spShipObj.setSaveShippingAddress(true)
		 BBBAddressImpl newAddress = new BBBAddressImpl()
		newAddress.setIdentifier("addressSaved")
		1*checkoutManagerMock.saveAddressToProfile(profileMock, _,_) >> { throw new BBBBusinessException("Mock of Business exception")}
		spShipObj.setCisiIndex("1")
		def CommerceItemShippingInfo relItem =Mock()
		def BBBCommerceItem itemMock = Mock()
		def CommerceItemShippingInfo relItem1 =Mock()
		def BBBCommerceItem itemMock1 = Mock()
		relItem.getCommerceItem() >> itemMock
		relItem1.getCommerceItem() >> itemMock1
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		spShipObj.setShippingAddressContainer(addContainer)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		spShipObj.addNewAddress(requestMock,responseMock)
		then:
		0*relItem1.setShippingGroupName("addressSaved")
		0*requestMock.setParameter("newAddressKey", "addressSaved")
		1*spShipObj.logError('error occourred while saving the address into profile',_)
	}
	def "this method is used to validate order level school promotion is applied or not"(){
		given:
		orderMock.getCouponMap() >> ["schoolPromo":null]
		when:
		boolean applied = spShipObj.isSchoolPromotion()
		then:
		applied
	}
	def "this method is used to validate order level school promotion is applied  not"(){
		given:
		orderMock.getCouponMap() >> ["schoolPromo1":null]
		when:
		boolean applied = spShipObj.isSchoolPromotion()
		then:
		applied == false
	}
	def "this method is used to validate order level school promotion is in order coupon map is null"(){
		given:
		orderMock.getCouponMap() >> null
		when:
		boolean applied = spShipObj.isSchoolPromotion()
		then:
		applied == false
	}
	def "this method is used to validate order level school promotion is in order coupon map is empty"(){
		given:
		orderMock.getCouponMap() >> [:]
		when:
		boolean applied = spShipObj.isSchoolPromotion()
		then:
		applied == false
	}
	def "This method is used to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl"(){
		given:
		def CommerceItemShippingInfo relItem =Mock()
		def BBBCommerceItem itemMock = Mock()
		relItem.getCommerceItem() >> itemMock
		relItem.getShippingMethod() >> "shippingMethod"
		itemMock.getDeliveryItemId() >> "deliveryId"
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		when:
		spShipObj.updateCISIForDelAndAssItems(relItem,"ShippingGroupKey")
		then:
		1*relItem.setShippingGroupName("ShippingGroupKey")
		1*relItem.setShippingMethod("shippingMethod")
	}
	def "This method is used to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl and Assembly"(){
		given:
		def CommerceItemShippingInfo relItem =Mock()
		def BBBCommerceItem itemMock = Mock()
		relItem.getCommerceItem() >> itemMock
		relItem.getShippingMethod() >> "shippingMethod"
		itemMock.getDeliveryItemId() >> "deliveryId"
		itemMock.getAssemblyItemId() >> "Assembly"
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getCommerceItemShippingInfos("deliveryId") >> [relItem]
		infoContainer.getCommerceItemShippingInfos("Assembly")>> [relItem]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		when:
		spShipObj.updateCISIForDelAndAssItems(relItem,"ShippingGroupKey")
		then:
		2*relItem.setShippingGroupName("ShippingGroupKey")
		2*relItem.setShippingMethod("shippingMethod")
	}
	def "the method to validate the to send conformation"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		def CommerceItemShippingInfo relItem =Mock()
		def CommerceItemShippingInfo relItem1 =Mock()
		def CommerceItemShippingInfo relItem2 =Mock()
		def CommerceItemShippingInfo relItem3 =Mock()
		def BBBCommerceItem cItem = Mock()
		relItem1.getCommerceItem() >>cItem
		relItem2.getCommerceItem() >>cItem
		relItem3.getCommerceItem() >>cItem
		relItem.getCommerceItem() >>cItem
		relItem.getShippingGroupName() >> "store"
		relItem1.getShippingGroupName() >> "hard"
		relItem1.getSplitShippingGroupName() >> "true"
		relItem2.getShippingGroupName() >> "hard"
		relItem2.getSplitShippingGroupName() >> "false"
		relItem3.getShippingGroupName() >> null
		relItem1.getSplitShippingGroupName() >> "split name"
		def CommerceItemShippingInfoContainer infoContainer = Mock()
		infoContainer.getAllCommerceItemShippingInfos() >> [relItem,relItem1,relItem2,relItem3]
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		2*shipServiceContainer.getShippingGroup("hard") >> hShip
		1*shipServiceContainer.getShippingGroup("store") >> sShip
		1*shipServiceContainer.getShippingGroup(null) >> hShip
		when:
		spShipObj.applyRegistrantEmailToSG()
		then:
		1 * hShip.setSpecialInstructions(['IS_COMFIRMATION_ASKED':'true'])
		1 * hShip.setSpecialInstructions(['CONFIRMATION_EMAIL_ID':'true'])
		2 * hShip.getSpecialInstructions()
		1 * relItem3.getSplitShippingGroupName()
	}
	def "checking gift card is restircted or not"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
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
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		cConfig.isLoggingDebugForRequestScopedComponents() >> true
		when:
		spShipObj.checkGiftCardRestriction(requestMock,responseMock)
		then:
		0*spShipObj.logError('Can not ship only Gift card items through express shipment', null)
	}
	def "checking gift card is restircted or not with restriction"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		def BBBStoreShippingGroup sShip = Mock()
		def CommerceItemShippingInfo relItem =Mock()
		def CommerceItemShippingInfo relItem1 =Mock()
		def CommerceItemShippingInfo relItem2 =Mock()
		def BBBCommerceItem cItem = Mock()
		def CommerceItem cItem1 = Mock()
		def BBBCommerceItem cItem2 = Mock()
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
		spShipObj.setCommerceItemShippingInfoContainer(infoContainer)
		cConfig.isLoggingDebugForRequestScopedComponents() >> false
		when:
		spShipObj.checkGiftCardRestriction(requestMock,responseMock)
		then:
		1*spShipObj.logError('Can not ship only Gift card items through express shipment', null)
	}
	
	def "remove empty shipping group"(){
		when:
		boolean succes = spShipObj.getRemoveEmptyShippingGroup()
		then:
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		succes == true
	}
	def "remove empty shipping group thorw exception"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		1*sGroupManager.removeEmptyShippingGroups(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
		when:
		boolean succes = spShipObj.getRemoveEmptyShippingGroup()
		then:
		succes == true
		1*spShipObj.logError("Error removing Shipping group", _)
	}
	def "inclued gift card in order flag in request"(){
		given:
		def HttpSession session = Mock()
		requestMock.getSession() >> session
		when:
		spShipObj.setOrderIncludesGifts(true)
		then:
		0*session.setAttribute("giftsAreIncludedInOrder",_)
	}
	def "Populates ShippingInfoBean from the input gift message "(){
		given:
		cConfig.isLogDebugEnableOnCartFormHandler() >>  true
		spShipObj.setGiftMsgAPIDelimiter(";")
		spShipObj.setGiftMsgAPIPropertiesDelimiter(":")
		spShipObj.setGiftMessageInput("sg1234:s:true:true")
		spShipObj.setSiteId("BedBathUS")
		spShipObj.checkFormRedirect(null, _, requestMock, responseMock) >> false
		shipHelperMock.getPricingTools() >> pTools
		when:
		spShipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
		then:
		BBBShippingInfoBean shipBeanInfo = spShipObj.getBBBShippingInfoBeanList().get(0)
		shipBeanInfo.getShippingGroupId().equals("sg1234")
		shipBeanInfo.getGiftMessage().equals("s")
		shipBeanInfo.isGiftingFlag()
		shipBeanInfo.getGiftWrap()
		1*requestMock.setContextPath("")
		spShipObj.getSiteId().equals(null)
		1*pState.setCheckoutFailureURLs([:])
	}
	def "Populates ShippingInfoBean from the input gift bean proprties length is not equal to four "(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		cConfig.isLogDebugEnableOnCartFormHandler() >>  true
		spShipObj.setGiftMsgAPIDelimiter(";")
		spShipObj.setGiftMsgAPIPropertiesDelimiter(":")
		spShipObj.setGiftMessageInput(";a:s:d:f;")
		when:
		spShipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
		then:
		1*spShipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
	}
	def "Populates ShippingInfoBean from the input gift bean second propertie is true "(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		cConfig.isLogDebugEnableOnCartFormHandler() >>  true
		spShipObj.setGiftMsgAPIDelimiter(";")
		spShipObj.setGiftMsgAPIPropertiesDelimiter(":")
		spShipObj.setGiftMessageInput("a:true:d:f;")
		when:
		spShipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
		then:
		1*spShipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
	}
	def "Populates ShippingInfoBean from the input gift bean second propertie is false "(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		cConfig.isLogDebugEnableOnCartFormHandler() >>  true
		spShipObj.setGiftMsgAPIDelimiter(";")
		spShipObj.setGiftMsgAPIPropertiesDelimiter(":")
		spShipObj.setGiftMessageInput(":a:false:f;")
		when:
		spShipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
		then:
		1*spShipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
	}
	def "Populates ShippingInfoBean from the input gift bean first  propertie is blank "(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		cConfig.isLogDebugEnableOnCartFormHandler() >>  true
		spShipObj.setGiftMsgAPIDelimiter(";")
		spShipObj.setGiftMsgAPIPropertiesDelimiter(":")
		spShipObj.setGiftMessageInput(":a:true:f;")
		when:
		spShipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
		then:
		1*spShipObj.logError("Input data for Shipping Group No: 1 is incorrect throw an error")
	}
	def "Populates ShippingInfoBean from the input gift message string have exception"(){
		given:
		spShipObj = Spy()
		spyObjIntilization(spShipObj)
		cConfig.isLogDebugEnableOnCartFormHandler() >>  true
		when:
		spShipObj.handleSaveGiftInfoToOrder(requestMock,responseMock)
		then:
		1*spShipObj.logError("Input string is empty")
	}
	void spyObjIntilization(BBBSPShippingGroupFormhandler spShipObj){
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
		spShipObj.setPromotionTools(promotools)
		spShipObj.setPromotionLookupManager(pLooLupManager)
		spShipObj.setCouponUtil(couponutil)
	}
	public static String addDays(int days ,String format)
	{	SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);  // number of days to add
		return sdf.format(c.getTime()).toString();
	}
}
