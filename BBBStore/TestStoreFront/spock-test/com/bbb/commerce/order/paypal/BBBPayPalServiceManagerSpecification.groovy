package com.bbb.commerce.order.paypal

import java.sql.Timestamp
import java.util.LinkedHashMap;

import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.CommerceException
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.CreditCard
import atg.commerce.order.OrderHolder
import atg.commerce.order.ShippingGroupManager
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.pricing.PricingConstants
import atg.commerce.pricing.UnitPriceBean
import atg.core.util.Address
import atg.dtm.TransactionDemarcationException
import atg.repository.MutableRepository
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.service.lockmanager.ClientLockManager
import atg.service.lockmanager.DeadlockException
import atg.service.lockmanager.LockManagerException
import atg.service.pipeline.RunProcessException
import atg.userprofiling.Profile

import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.order.manager.OrderDetailsManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.cart.utils.RepositoryPriorityComparator;
import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.catalog.vo.SiteVO
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.common.PriceInfoDisplayVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.order.BBBCommerceItemManager
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.commerce.order.BBBPaymentGroupManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.commerce.order.Paypal
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.commerce.pricing.bean.PriceInfoVO
import com.bbb.commerce.pricing.droplet.BBBPriceDisplayDroplet
import com.bbb.commerce.vo.PayPalInputVO
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBPayPalConstants
import com.bbb.constants.BBBWebServiceConstants
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.webservices.BBBWebservicesConfig
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBItemPriceInfo
import com.bbb.order.bean.EcoFeeCommerceItem
import com.bbb.paypal.BBBAddressPPVO
import com.bbb.paypal.BBBDoAuthorizationResVO
import com.bbb.paypal.BBBDoExpressCheckoutPaymentResVO
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO
import com.bbb.paypal.BBBSetExpressCheckoutResVO
import com.bbb.paypal.PayPalProdDescVO;
import com.bbb.paypal.PayerInfoVO
import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.BBBUtility

class BBBPayPalServiceManagerSpecification extends BBBExtendedSpec {
	
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBWebservicesConfig bbbWebServicesConfigMock = Mock()
	def OrderDetailsManager orderDetailsManagerMock = Mock()
	def BBBCheckoutManager checkoutMgrMock = Mock()
	def GiftRegistryManager giftRegistryManagerMock = Mock()
	def LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	def BBBPurchaseProcessHelper shippingHelperMock = Mock()
	def BBBShippingGroupManager shippingManagerMock = Mock()
	def BBBOrderManager orderManagerMock = Mock()
	def BBBPriceDisplayDroplet priceDisplayDropletMock = Mock()
	def BBBPaymentGroupManager paymentGroupManagerMock = Mock()
	def ClientLockManager localLockManagerMock = Mock()
	def TransactionManager transactionManagerMock = Mock()
	def ShippingGroupManager shippingGroupManagerMock = Mock()
	def Repository siteRepositoryMock = Mock()
	def BBBPricingTools mPricingToolsMock = Mock()
	def MutableRepository catalogRepositoryMock = Mock()
	def BBBCommerceItemManager commerceItemManagerMock = Mock()
	def PromotionLookupManager promoManagerMock = Mock()
	def BBBPayPalServiceManager payPalServiceManagerObj 
	def BBBOrderImpl orderMock = Mock()
	def Profile profileMock = Mock()
	def BBBHardGoodShippingGroup hShip = Mock()
	def OrderHolder orderHolderMock = Mock()
	BBBPayPalSessionBean payPalSessionBean = new BBBPayPalSessionBean()
	
	def setup(){
		payPalServiceManagerObj = new BBBPayPalServiceManager(lblTxtTemplateManager:lblTxtTemplateManagerMock,orderManager:orderManagerMock,checkoutMgr:checkoutMgrMock,transactionManager:transactionManagerMock,localLockManager:localLockManagerMock,setExpressCheckoutService:"setExpressCheckout",bbbWebServicesConfig:bbbWebServicesConfigMock,catalogTools:catalogToolsMock,catalogRepository:catalogRepositoryMock,mPricingTools:mPricingToolsMock,paymentGroupManager:paymentGroupManagerMock,commerceItemManager:commerceItemManagerMock)
		bbbWebServicesConfigMock.getServiceToWsdlMap() >> ["setExpressCheckout":"WSDL_PayPal","getExpressCheckoutDetails":"WSDL_PayPal","doExpressCheckoutPayment":"WSDL_PayPal","doAuthorization":"WSDL_PayPal"]
		payPalSessionBean.setOldCartMap([:])
		requestMock.resolveName(BBBPayPalConstants.PAYPAL_SESSION_BEAN_PATH) >> payPalSessionBean
		BBBCatalogTools tools =  new BBBCatalogToolsImpl()
		BBBUtility.setCatalogTools(tools)
	}
	
	def "making webservice call to get paypal token for given credientials"(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setSetExpressCheckoutService("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock() 
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		productItemMock.setProperties(["displayName":"DisplayItem","seoUrl":"URL"])
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		UnitPriceBean itemUnitPriceBean = new UnitPriceBean()
		itemPriceVo.setPriceBeans([itemUnitPriceBean])
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		1*catalogToolsMock.getSiteDetailFromSiteId(_) >> site
		getAllValuesForKey()
		def BBBSetExpressCheckoutResVO setPaypal = Mock() 
		setPaypal.getToken() >> "token1234"
		payPalServiceManagerObj.invokeServiceMethod(_) >> setPaypal
		when:
		BBBSetExpressCheckoutResVO resType  = payPalServiceManagerObj.doSetExpressCheckOut(orderMock, "", "", payPalSessionBean, profileMock)
		then:
		payPalSessionBean.getGetExpCheckoutResponse() == null
		payPalSessionBean.isValidateOrderAddress() == false
		resType == setPaypal
		1*profileMock.setPropertyValue(BBBCoreConstants.TOKEN, "token1234")
	}
	def "making webservice call to get paypal token for given credientials for TBS_BedBathCanada"(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setSetExpressCheckoutService("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		cItem.getCatalogRefId() >> "12345567"
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		hShip.getRegistryInfo() >> "Info"
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		1*catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		RepositoryItemMock skuItemMock = new RepositoryItemMock()
		1*catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		UnitPriceBean itemUnitPriceBean = new UnitPriceBean()
		itemPriceVo.setPriceBeans([itemUnitPriceBean])
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		1*catalogToolsMock.getSiteDetailFromSiteId(_) >> site
		getAllValuesForKey()
		def BBBSetExpressCheckoutResVO setPaypal = Mock()
		setPaypal.getToken() >> "token1234"
		payPalServiceManagerObj.invokeServiceMethod(_) >> setPaypal
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathCanada"
		PriceInfoDisplayVO infoVO =new   PriceInfoDisplayVO()
		infoVO.setMaxDeliverySurchargeReached(true)
		payPalServiceManagerObj.populatePriceInfo(orderMock, _, _) >> infoVO
		when:
		BBBSetExpressCheckoutResVO resType  = payPalServiceManagerObj.doSetExpressCheckOut(orderMock, "", "", payPalSessionBean, profileMock)
		then:
		resType == setPaypal
		1*profileMock.setPropertyValue(BBBCoreConstants.TOKEN, "token1234")
	}
	def "making webservice call to get paypal token for given credientials response has null"(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setSetExpressCheckoutService(null)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip, new BBBStoreShippingGroup()]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		commerceItemManagerMock.getItemPriceInfo(cItem) >> null
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		1*catalogToolsMock.getSiteDetailFromSiteId(_) >> site
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_LOGIN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PASSWORD + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>  ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SIGNATURE + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_VERSION + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_ADDR_OR + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>[ "1"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PAY_ACT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["PAYACT" ]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES) >> ["1234"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SUBJECT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["SUBJECCT"]
		payPalServiceManagerObj.invokeServiceMethod(_) >> null
		payPalServiceManagerObj.getSiteId() >> "BedBathCanada"
		when:
		BBBSetExpressCheckoutResVO resType  = payPalServiceManagerObj.doSetExpressCheckOut(orderMock, "", "", payPalSessionBean, profileMock)
		then:
		resType == null
		0*profileMock.setPropertyValue(BBBCoreConstants.TOKEN, "token1234")
	}
	def "making webservice call to get paypal token for given credientials for BedBathUS"(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setSetExpressCheckoutService("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> null
		cItem.getCatalogRefId() >> "12345567"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		RepositoryItemMock skuItemMock = new RepositoryItemMock()
		skuItemMock.setProperties(["displayName":"DisplayItem"])
		1*catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		UnitPriceBean itemUnitPriceBean = new UnitPriceBean()
		itemPriceVo.setPriceBeans([itemUnitPriceBean])
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		1*catalogToolsMock.getSiteDetailFromSiteId(_) >> site
		getAllValuesForKey()
		def BBBSetExpressCheckoutResVO setPaypal = Mock()
		setPaypal.getToken() >> "token1234"
		payPalServiceManagerObj.invokeServiceMethod(_) >> setPaypal
		payPalServiceManagerObj.getSiteId() >> "BedBathUS"
		when:
		BBBSetExpressCheckoutResVO resType  = payPalServiceManagerObj.doSetExpressCheckOut(orderMock, "", "", payPalSessionBean, profileMock)
		then:
		resType == setPaypal
		1*profileMock.setPropertyValue(BBBCoreConstants.TOKEN, "token1234")
	}
	
	def "validating for a given profile coupons is applied for BEDBATTHUS"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock() 
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "US"
		orderMock.getBillingAddress() >> repContactInfo
		repContactInfo.setMobileNumber(_) >> {}
		repContactInfo.setPhoneNumber(_) >> {}
		payPalServiceManagerObj.getSiteId() >> "BedBathUS"
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == false
	}
	def "validating for a given profile coupons is applied for BuyBuyBaby "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "US"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "BuyBuyBaby"
		promoManagerMock.getCouponMap(orderMock, profileMock) >> [:]
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == false
	}
	def "validating for a given profile coupons is applied for TBS_BedBathUS "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "US"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathUS"
		RepositoryItemMock itemMock = new RepositoryItemMock()
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >> ["CODE":itemMock]
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validating for a given profile coupons is applied for TBS_BuyBuyBaby "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "US"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "TBS_BuyBuyBaby"
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	
	def "validating for a given profile coupons is applied for BedBathCanada "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "CA"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "BedBathCanada"
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validating for a given profile coupons is applied for TBS_BedBathCanada "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "CA"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathCanada"
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validating for a given profile coupons is applied or not when order is null "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		1*promoManagerMock.getCouponMap(null, profileMock) >>null
		0*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(null, profileMock)
		then:
		applied == false
	}
	def "validating for a given profile coupons is applied or not when billingAddress is null "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		0*promoManagerMock.getCouponMap(null, profileMock) >>null
		orderMock.getBillingAddress() >>  null
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == false
	}
	def "validating for a given profile coupons is applied for TBS_BedBathCanada and site id as null"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "CA"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> null
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validating for a given profile coupons is applied for TBS_BedBathCanada and country id as null"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> null
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathCanada"
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validating for a given profile coupons is applied for TBS_BedBathCanada and country  as Indaia"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "IN"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathCanada"
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validating for a given profile coupons is applied for BedBathUS and country  as Indaia"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setPromoManager(promoManagerMock)
		def BBBRepositoryContactInfo repContactInfo = Mock()
		repContactInfo.getMobileNumber() >> "111111111111"
		repContactInfo.getPhoneNumber() >> "111111111111"
		repContactInfo.getCountry() >> "IN"
		orderMock.getBillingAddress() >> repContactInfo
		payPalServiceManagerObj.getSiteId() >> "BedBathUS"
		1*promoManagerMock.getCouponMap(orderMock, profileMock) >>null
		1*promoManagerMock.isSchoolPromotion(orderMock) >> true
		when:
		boolean applied = payPalServiceManagerObj.validateCoupons(orderMock, profileMock)
		then:
		applied == true
	}
	def "validatin token is availabe in order and bypass this method from unit testing"(){
		given:
		payPalServiceManagerObj.setEnableTesting(true)
		payPalServiceManagerObj.setTokenExpTest(true)
		when:
		boolean available = payPalServiceManagerObj.isTokenExpired(null, orderMock)
		then:
		available
		
	}
	def "validatin token is availabe in order and token is not availalbe in order"(){
		given:
		orderMock.getToken() >> ""
		when:
		boolean available = payPalServiceManagerObj.isTokenExpired(null, orderMock)
		then:
		available
		
	}
	def "validatin token is availabe in order and compare with current date,time stamp not available"(){
		given:
		orderMock.getToken() >> "t1234"
		payPalServiceManagerObj.setSetExpressCheckoutService("setExpressCheckout")
		0*bbbWebServicesConfigMock.getServiceToWsdlMap() >> ["setExpressCheckout":"servicewsdl"]
		1*catalogToolsMock.getAllValuesForKey("WSDL_PayPal", BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES) >> ["12345678"]
		when:
		boolean available = payPalServiceManagerObj.isTokenExpired(null, orderMock)
		then:
		available
	}
	def "validatin token is availabe in order and compare with current date,time stamp not expired" (){
		given:
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		paypalsession.setValidateOrderAddress(true)
		paypalsession.setGetExpCheckoutResponse(new BBBGetExpressCheckoutDetailsResVO())
		orderMock.getToken() >> "t1234"
		Date today = new Date()
		orderMock.getTimeStamp() >> new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())
		payPalServiceManagerObj.setSetExpressCheckoutService("setExpressCheckout")
		1*catalogToolsMock.getAllValuesForKey("WSDL_PayPal", BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES) >> ["12345678"]
		when:
		boolean available = payPalServiceManagerObj.isTokenExpired(paypalsession, orderMock)
		then:
		null != paypalsession.getGetExpCheckoutResponse() 
		!available
		paypalsession.isValidateOrderAddress() == true
		
	}
	def "validatin token is availabe in order and compare with current date,time stamp expired" (){
		given:
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		paypalsession.setValidateOrderAddress(true)
		paypalsession.setGetExpCheckoutResponse(new BBBGetExpressCheckoutDetailsResVO())
		orderMock.getToken() >> "t1234"
		Date today = new Date()
		orderMock.getTimeStamp() >> new Timestamp((new Date(today.getTime() - (1000 * 60 * 60 * 24))).getTime())
		payPalServiceManagerObj.setSetExpressCheckoutService("setExpressCheckout")
		1*catalogToolsMock.getAllValuesForKey("WSDL_PayPal", BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES) >> ["1135"]
		when:
		boolean available = payPalServiceManagerObj.isTokenExpired(paypalsession, orderMock)
		then:
		available
		paypalsession.isValidateOrderAddress() == false
		paypalsession.getGetExpCheckoutResponse() == null
		
	}
	def "making webservice call to doAuthorization paypal failed not having transcation id"(){
		when:
		BBBDoAuthorizationResVO res = payPalServiceManagerObj.doAuthorization("", orderMock)
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_LOGIN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PASSWORD + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>  ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SIGNATURE + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_VERSION + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_ADDR_OR + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>[ "1"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PAY_ACT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["PAYACT" ]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES) >> ["1234"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SUBJECT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["SUBJECCT"]
		then:
		0 * payPalServiceManagerObj.logDebug('Fetching product details: product Id: 145487')
		0 * payPalServiceManagerObj.logError('BBBPayPalServiceManager.skuRepoItem: sku id is null')
		0 * payPalServiceManagerObj.logDebug('End  getExpCheckoutDetails of BBBPayPalService returns  voResp :Mock for type \'BBBDoAuthorizationResVO\' named \'setPaypal\'')
		0 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Key for Map created by Price Beans of Commerce Item is : null')
		0 * payPalServiceManagerObj.logError('BBBPayPalServiceManager.skuRepoItem: sku id is null', null)
		0 * payPalServiceManagerObj.logInfo('BBB Commerce Item: ProductId: 145487 Sku ID: null Quanity: 0 Unit Price: 0.0', null)
		0 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails:  sku id: nullproduct id145487')
		res == null
	}
	def "making webservice call to doAuthorization paypal for given token"(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setDoAuthorization("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		productItemMock.setProperties(["displayName":"DisplayItem","seoUrl":"URL"])
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		UnitPriceBean itemUnitPriceBean = new UnitPriceBean()
		UnitPriceBean itemUnitPriceBean1 = new UnitPriceBean()
		itemPriceVo.setPriceBeans([itemUnitPriceBean,itemUnitPriceBean1,null])
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		getAllValuesForKey()
		def BBBDoAuthorizationResVO setPaypal = Mock()
		payPalServiceManagerObj.invokeServiceMethod(_) >> setPaypal
		when:
		BBBDoAuthorizationResVO resType  = payPalServiceManagerObj.doAuthorization("1234567",orderMock)
		then:
		resType == setPaypal
		1 * payPalServiceManagerObj.logDebug('Fetching product details: product Id: 145487')
		1 * payPalServiceManagerObj.logError('BBBPayPalServiceManager.skuRepoItem: sku id is null')
		1 * payPalServiceManagerObj.logDebug('End  getExpCheckoutDetails of BBBPayPalService returns  voResp :Mock for type \'BBBDoAuthorizationResVO\' named \'setPaypal\'')
		0 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Key for Map created by Price Beans of Commerce Item is : null')
		1 * payPalServiceManagerObj.logError('BBBPayPalServiceManager.skuRepoItem: sku id is null', null)
		2 * payPalServiceManagerObj.logInfo('BBB Commerce Item: ProductId: 145487 Sku ID: null Quanity: 0 Unit Price: 0.0', null)
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails:  sku id: nullproduct id145487')
	}
	def "making webservice call to doAuthorization paypal for given token for BedBathCanada "(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setDoAuthorization("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		productItemMock.setProperties(["displayName":"DisplayItem","seoUrl":"URL"])
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		itemPriceVo.setPriceBeans(null)
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		getAllValuesForKey()
		def BBBDoAuthorizationResVO setPaypal = Mock()
		payPalServiceManagerObj.invokeServiceMethod(_) >> null
		payPalServiceManagerObj.getSiteId() >> "BedBathCanada"
		when:
		BBBDoAuthorizationResVO resType  = payPalServiceManagerObj.doAuthorization("1234567",orderMock)
		then:
		resType == null
		0 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Key for Map created by Price Beans of Commerce Item is : null')
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: End')
		1 * payPalServiceManagerObj.logDebug('Start  getPayPalCred of BBBPayPalService with  WSDL_service:WSDL_PayPal')
		1 * payPalServiceManagerObj.logDebug('************paypalCredentialsVO**************BBBPayPalCredentials [version=test, soapApiURL=null, addressOverRide=1, paymentAction=PAYACT, userName=user, password=user]')
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Start')
		1 * payPalServiceManagerObj.logDebug('Start  doAuthorization of BBBPayPalService with transactionID:1234567')
	}
	def "making webservice call to doAuthorization paypal for given token for BedBathUS "(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setDoAuthorization("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		productItemMock.setProperties(["displayName":"DisplayItem","seoUrl":"URL"])
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		itemPriceVo.setPriceBeans(null)
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		getAllValuesForKey()
		def BBBDoAuthorizationResVO setPaypal = Mock()
		payPalServiceManagerObj.invokeServiceMethod(_) >> null
		payPalServiceManagerObj.getSiteId() >> "BedBathUS"
		when:
		BBBDoAuthorizationResVO resType  = payPalServiceManagerObj.doAuthorization("1234567",orderMock)
		then:
		resType == null
		0 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Key for Map created by Price Beans of Commerce Item is : null')
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: End')
		1 * payPalServiceManagerObj.logDebug('Start  getPayPalCred of BBBPayPalService with  WSDL_service:WSDL_PayPal')
		1 * payPalServiceManagerObj.logDebug('************paypalCredentialsVO**************BBBPayPalCredentials [version=test, soapApiURL=null, addressOverRide=1, paymentAction=PAYACT, userName=user, password=user]')
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Start')
		1 * payPalServiceManagerObj.logDebug('Start  doAuthorization of BBBPayPalService with transactionID:1234567')
	}
	
	def "making webservice call to doAuthorization paypal for given token and site id is TBS_BedBathCanada"(){
		given:
		payPalServiceManagerObj =  Spy()
		payPalServiceManagerObj.setDoAuthorization("setExpressCheckout")
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		hShip.getRegistryInfo() >> "RegistryInfo"
		orderMock.getShippingGroups() >> [hShip,new BBBStoreShippingGroup()]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		PriceInfoVO orderPriceVo = new PriceInfoVO()
		1*mPricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceVo
		1*paymentGroupManagerMock.getPaymentGroups(orderMock, BBBCheckoutConstants.GIFTCARD) >> []
		1*paymentGroupManagerMock.getAmountCoveredByGiftCard([], orderMock) >> 2.0
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		productItemMock.setProperties(["displayName":"DisplayItem","seoUrl":"URL"])
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		PriceInfoVO itemPriceVo = new PriceInfoVO()
		commerceItemManagerMock.getItemPriceInfo(cItem) >> itemPriceVo
		itemPriceVo.setPriceBeans([null])
		SiteVO site = new SiteVO()
		site.setCountryCode("US")
		getAllValuesForKey()
		def BBBDoAuthorizationResVO setPaypal = Mock()
		payPalServiceManagerObj.invokeServiceMethod(_) >> setPaypal
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathCanada"
		
		when:
		BBBDoAuthorizationResVO resType  = payPalServiceManagerObj.doAuthorization("1234567",orderMock)
		then:
		resType == setPaypal
		1 * payPalServiceManagerObj.logDebug('Start  doAuthorization of BBBPayPalService with transactionID:1234567')
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: Start')
		1 * payPalServiceManagerObj.logDebug('BBBPayPalServiceManager.populateOrderItemDetails: End')
		1 * payPalServiceManagerObj.logDebug('************paypalCredentialsVO**************BBBPayPalCredentials [version=test, soapApiURL=null, addressOverRide=1, paymentAction=PAYACT, userName=user, password=user]')
		1 * payPalServiceManagerObj.doAuthorization('1234567',orderMock)
	}
	def "creating paypal payment group with out token id "(){
		given:
		BBBAddressContainer addContainer = new BBBAddressContainer()
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("", orderMock, profileMock, addContainer)
		then:
		voResp == null
	}
	def "creating paypal payment group with  token id "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLoggingDebug(true)
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OHIO")
		billAddress.setState("OH")
		resp.setShippingAddress(shipAddress)
		resp.setBillingAddress(billAddress)
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()		
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		resp.setPayerInfo(payerInfo)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		cItem1.getId() >> "123451"
		cItem1.getQuantity() >> 3
		orderMock.getCommerceItems() >> [cItem1,cItem]
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == resp
		shipAddress.getState().equalsIgnoreCase("OH")
	}
	def "creating paypal payment group with  token id ship state as null "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		billAddress.setState("OH")
		resp.setShippingAddress(shipAddress)
		resp.setBillingAddress(billAddress)
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		resp.setPayerInfo(payerInfo)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		cItem1.getId() >> "123451"
		cItem1.getQuantity() >> 3
		orderMock.getCommerceItems() >> [cItem1,cItem]
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == resp
	}
	def "creating paypal payment group with  token id billing address as null "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OH")
		resp.setShippingAddress(shipAddress)
		resp.setBillingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		resp.setPayerInfo(payerInfo)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		cItem1.getId() >> "123451"
		cItem1.getQuantity() >> 3
		orderMock.getCommerceItems() >> [cItem1,cItem]
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == resp
	}
	def "creating paypal payment group with  token id billing address state as null "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OH")
		resp.setShippingAddress(shipAddress)
		resp.setBillingAddress(billAddress)
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		resp.setPayerInfo(payerInfo)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		cItem1.getId() >> "123451"
		cItem1.getQuantity() >> 3
		orderMock.getCommerceItems() >> [cItem1,cItem]
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == resp
	}
	def "creating paypal payment group with  token id and having error response from paypal  "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		payPalServiceManagerObj.setLoggingDebug(true)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(true)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		cItem1.getId() >> "123451"
		cItem1.getQuantity() >> 3
		orderMock.getCommerceItems() >> [cItem1,cItem]
		0*paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_GET_SERVICE_FAIL, "EN", null) >> "err_paypal_get_service_fail"
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_get_service_fail:err_paypal_get_service_fail")
	}
	def "creating paypal payment group with  token id faliure call "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> true
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		payPalServiceManagerObj.invokeServiceMethod(_) >> null
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		orderMock.getCommerceItems() >> [cItem]
		0*paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_GET_SERVICE_FAIL, "EN", null) >> "err_paypal_get_service_fail"
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_get_service_fail:err_paypal_get_service_fail")
	}
	def "creating paypal payment group with  token id throws dead lock exception "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		localLockManagerMock.hasWriteLock(_,_) >> false
		localLockManagerMock.acquireWriteLock(_,_) >> {throw new DeadlockException("Mock of deadloack exception")} 
		localLockManagerMock.releaseWriteLock(_,_,true) >> {throw new LockManagerException("Mock of lcok manager exception")}
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_LOGIN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PASSWORD + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>  ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SIGNATURE + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_VERSION + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		1*payPalServiceManagerObj.logError("DeadlockException while getExpCheckoutDetails", _);
		1*payPalServiceManagerObj.logError("TransactionDemarcationException releasing lock on profile", _)
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token id throws transaction Managerexception "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(null)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_LOGIN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PASSWORD + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>  ["user"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SIGNATURE + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_VERSION + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
		0*paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		1*payPalServiceManagerObj.logError("TransactionDemarcationException while getExpCheckoutDetails", _);
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token id throws Business Exception because of VALIDATION_FAILED"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		resp.setShippingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> {throw new BBBBusinessException("VALIDATION_FAILED","VALIDATION_FAILED")}
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown() 
		ex.getMessage().equalsIgnoreCase("VALIDATION_FAILED:VALIDATION_FAILED")
	}
	def "creating paypal payment group with  token id throws Business Exception message is null"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		resp.setShippingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> {throw new BBBBusinessException(null)}
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token id throws Business Exception message non VALIDATION_FAILED error"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		resp.setShippingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> {throw new BBBBusinessException("Mock of the business")}
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token id throws system Exception message non PAYPAL_GET_SERVICE_FAILED error"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		resp.setShippingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> {throw new BBBSystemException("Mock of the business")}
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token id throws system Exception message as null"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		resp.setShippingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> {throw new BBBSystemException(null) }
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token id throws Business Exception"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_FAIL_GENERIC_MSG, "EN", null) >> "err_paypal_fail_generic_msg"
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		resp.setShippingAddress(null)
		ErrorStatus eStatus = new ErrorStatus()
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		payPalServiceManagerObj.invokeServiceMethod(_) >> {throw new BBBBusinessException("Mock the Business exception")}
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg:err_paypal_fail_generic_msg")
	}
	def "creating paypal payment group with  token throws commerece exception "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		payPalServiceManagerObj.setGetExpressCheckoutService("getExpressCheckoutDetails")
		getAllValuesForKey()
		BBBGetExpressCheckoutDetailsResVO resp = new BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		billAddress.setState("TX")
		billAddress.setState("TEXAS")
		resp.setShippingAddress(null)
		resp.setBillingAddress(billAddress)
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		resp.setErrorStatus(eStatus)
		resp.setPayerInfo(payerInfo)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		def BBBCommerceItem cItem =Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem.getId() >> "12345"
		cItem.getQuantity() >> 2
		cItem1.getId() >> "123451"
		cItem1.getQuantity() >> 3
		orderMock.getCommerceItems() >> [cItem1,cItem]
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		payPalServiceManagerObj.updateTokenInOrder(_, orderMock,_, profileMock) >> {throw new CommerceException("Mock of commerece exception")}
		when:
		BBBGetExpressCheckoutDetailsResVO voResp = payPalServiceManagerObj.getExpCheckoutDetails("t1234", orderMock, profileMock, addContainer)
		then:
		voResp == null
		Exception ex = thrown() 
		ex.getMessage().equalsIgnoreCase("err_paypal_fail_generic_msg")
	}
	def "do express checkout method is called "(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		payPalServiceManagerObj.setDoExpressCheckoutService("doExpressCheckoutPayment")
		payPalServiceManagerObj.setGiftRegistryManager(giftRegistryManagerMock)
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock the Respository Exception")}
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		cItem.getRegistryId() >> "123456"
		def AuxiliaryData aData = Mock() 
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		localLockManagerMock.hasWriteLock(_,_) >> false
		RegistryVO regVO = new RegistryVO()
		1*giftRegistryManagerMock.getRegistryDetailInfo("123456",_) >> regVO
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_from_text" ,"EN", null, null) >> "lbl_cart_registry_from_text"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_name_suffix", "EN", null, null) >> "lbl_cart_registry_name_suffix"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_text", "EN", null, null) >> "lbl_cart_registry_text"
		orderMock.getRegistryMap() >> null
		getAllValuesForKey()
		BBBDoExpressCheckoutPaymentResVO resp = new BBBDoExpressCheckoutPaymentResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OHIO")
		billAddress.setState("OH")
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		when:
		BBBDoExpressCheckoutPaymentResVO voResp = payPalServiceManagerObj.doExpressCheckout(orderMock)
		then:
		voResp == resp
		1*payPalServiceManagerObj.logError("BBBPayPalServiceManager.productRepoItem: Error occured while fetching product details for product ID " + "145487")
		1*payPalServiceManagerObj.logError("BBBPayPalServiceManager.populateOrderItemDetails: Error occured while fetching registry details for registry ID 123456");
	}
	def "do express checkout method is called site id asBedBathCanada"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getSiteId() >> "BedBathCanada"
		PriceInfoDisplayVO infoVO =new   PriceInfoDisplayVO()
		infoVO.setMaxDeliverySurchargeReached(true)
		payPalServiceManagerObj.populatePriceInfo(orderMock, _, _) >> infoVO
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		payPalServiceManagerObj.setDoExpressCheckoutService("doExpressCheckoutPayment")
		payPalServiceManagerObj.setGiftRegistryManager(giftRegistryManagerMock)
		RepositoryItemMock productItemMock = new RepositoryItemMock()
		productItemMock.setProperties(["displayName":"DisplayItem","seoUrl":"URL"])
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productItemMock
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>{throw new RepositoryException()}
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 0
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		cItem.getCatalogRefId() >> "12345567"
		cItem.getRegistryId() >> "123456"
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		orderMock.getShippingGroups() >> [hShip]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		localLockManagerMock.hasWriteLock(_,_) >> false
		RegistryVO regVO = new RegistryVO()
		1*giftRegistryManagerMock.getRegistryDetailInfo("123456",_) >> regVO
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_from_text" ,"EN", null, null) >> "lbl_cart_registry_from_text"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_name_suffix", "EN", null, null) >> "lbl_cart_registry_name_suffix"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_text", "EN", null, null) >> "lbl_cart_registry_text"
		orderMock.getRegistryMap() >> [:]
		RepositoryItemMock skuItemMock = new RepositoryItemMock()
		getAllValuesForKey()
		BBBDoExpressCheckoutPaymentResVO resp = new BBBDoExpressCheckoutPaymentResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OHIO")
		billAddress.setState("OH")
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		when:
		BBBDoExpressCheckoutPaymentResVO voResp = payPalServiceManagerObj.doExpressCheckout(orderMock)
		then:
		voResp == resp
		0*payPalServiceManagerObj.logError("BBBPayPalServiceManager.productRepoItem: Error occured while fetching product details for product ID " + "145487")
		1*payPalServiceManagerObj.logError("BBBPayPalServiceManager.skuRepoItem: Error occured while fetching sku details for sku ID 12345567" );
	}
	def "do express checkout method is called site id as TBS_BedBathCanada"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathCanada"
		PriceInfoDisplayVO infoVO =new   PriceInfoDisplayVO()
		infoVO.setMaxDeliverySurchargeReached(true)
		payPalServiceManagerObj.populatePriceInfo(orderMock, _, _) >> infoVO
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		payPalServiceManagerObj.setDoExpressCheckoutService("doExpressCheckoutPayment")
		payPalServiceManagerObj.setGiftRegistryManager(giftRegistryManagerMock)
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock the Respository Exception")}
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		cItem.getRegistryId() >> "123456"
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		hShip.getRegistryInfo() >> "infot"
		orderMock.getShippingGroups() >> [hShip,new BBBStoreShippingGroup()]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		localLockManagerMock.hasWriteLock(_,_) >> false
		RegistryVO regVO = new RegistryVO()
		1*giftRegistryManagerMock.getRegistryDetailInfo("123456",_) >> regVO
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_from_text" ,"EN", null, null) >> "lbl_cart_registry_from_text"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_name_suffix", "EN", null, null) >> "lbl_cart_registry_name_suffix"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_text", "EN", null, null) >> "lbl_cart_registry_text"
		RegistrySummaryVO regSummVO = new RegistrySummaryVO()
		orderMock.getRegistryMap() >> ["123456":regSummVO]
		getAllValuesForKey()
		BBBDoExpressCheckoutPaymentResVO resp = new BBBDoExpressCheckoutPaymentResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OHIO")
		billAddress.setState("OH")
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		when:
		BBBDoExpressCheckoutPaymentResVO voResp = payPalServiceManagerObj.doExpressCheckout(orderMock)
		then:
		voResp == resp
		1*payPalServiceManagerObj.logError("BBBPayPalServiceManager.productRepoItem: Error occured while fetching product details for product ID " + "145487")
	}
	def "do express checkout method is called site id as BedBathUS"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getSiteId() >> "BedBathUS"
		PriceInfoDisplayVO infoVO =new   PriceInfoDisplayVO()
		infoVO.setMaxDeliverySurchargeReached(true)
		payPalServiceManagerObj.populatePriceInfo(orderMock, _, _) >> infoVO
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setPricingTools(mPricingToolsMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setBbbWebServicesConfig(bbbWebServicesConfigMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setCommerceItemManager(commerceItemManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setCatalogRepository(catalogRepositoryMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		payPalServiceManagerObj.setDoExpressCheckoutService("doExpressCheckoutPayment")
		payPalServiceManagerObj.setGiftRegistryManager(giftRegistryManagerMock)
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock the Respository Exception")}
		BBBAddressContainer addContainer = new BBBAddressContainer()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> orderHolderMock
		orderHolderMock.getCurrent() >>  orderMock
		OrderPriceInfo pInfo =new  OrderPriceInfo()
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		BBBItemPriceInfo itemPrice = new BBBItemPriceInfo()
		itemPrice.setAmount(14.0)
		cItem.getPriceInfo() >> itemPrice
		cItem.getRegistryId() >> "123456"
		def AuxiliaryData aData = Mock()
		aData.getProductId() >> "145487"
		cItem.getAuxiliaryData() >> aData
		hShip.getId() >>  "SG0001"
		hShip.getCommerceItemRelationships() >> [itemRelShip]
		hShip.getRegistryInfo() >> "infot"
		orderMock.getShippingGroups() >> [hShip,new BBBStoreShippingGroup()]
		def EcoFeeCommerceItem ecoItemMock = Mock()
		orderMock.getCommerceItems() >> [cItem,ecoItemMock]
		pInfo.setAmount(20.2)
		pInfo.setCurrencyCode("USD")
		orderMock.getPriceInfo() >>pInfo
		localLockManagerMock.hasWriteLock(_,_) >> false
		RegistryVO regVO = new RegistryVO()
		1*giftRegistryManagerMock.getRegistryDetailInfo("123456",_) >> regVO
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_from_text" ,"EN", null, null) >> "lbl_cart_registry_from_text"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_name_suffix", "EN", null, null) >> "lbl_cart_registry_name_suffix"
		1*lblTxtTemplateManagerMock.getPageLabel("lbl_cart_registry_text", "EN", null, null) >> "lbl_cart_registry_text"
		RegistrySummaryVO regSummVO = new RegistrySummaryVO()
		RegistryTypes type= new RegistryTypes()
		regSummVO.setRegistryType(type)
		RegistrantVO registrantVO = new RegistrantVO () 
		regVO.setCoRegistrant(registrantVO)
		registrantVO.setFirstName("BBBB")
		orderMock.getRegistryMap() >> ["123456":regSummVO]
		getAllValuesForKey()
		BBBDoExpressCheckoutPaymentResVO resp = new BBBDoExpressCheckoutPaymentResVO()
		BBBAddressPPVO shipAddress = new BBBAddressPPVO()
		BBBAddressPPVO billAddress = new BBBAddressPPVO()
		shipAddress.setState("OHIO")
		billAddress.setState("OH")
		ErrorStatus eStatus = new ErrorStatus()
		PayerInfoVO payerInfo = new PayerInfoVO()
		payerInfo.setPayerID("pid1234")
		payerInfo.setPayerEmail("BBB@test.com")
		BBBAddressPPVO addressPPVo = new BBBAddressPPVO()
		payerInfo.setAddress(addressPPVo)
		eStatus.setErrorExists(false)
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		orderMock.getPaymentGroups() >> []
		StateVO st1 = new StateVO()
		st1.setStateName("OHIO")
		st1.setStateCode("OH")
		StateVO st2 = new StateVO()
		st2.setStateName("Calfonia")
		st2.setStateCode("CA")
		catalogToolsMock.getStateList() >> [st1,st2]
		paymentGroupManagerMock.createPayPalPaymentGroup(_, orderMock, profileMock) >> true
		payPalServiceManagerObj.invokeServiceMethod(_) >> resp
		when:
		BBBDoExpressCheckoutPaymentResVO voResp = payPalServiceManagerObj.doExpressCheckout(orderMock)
		then:
		voResp == resp
		1*payPalServiceManagerObj.logError("BBBPayPalServiceManager.productRepoItem: Error occured while fetching product details for product ID " + "145487")
	}
	def "checking wether commerce item relationship is exist"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		Address adr = new Address()
		hShip.getShippingAddress() >> adr
		hShip.getRegistryInfo() >>  ""
		hShip.getCommerceItemRelationshipCount() >> 1
		orderMock.getShippingGroups() >>[hShip]
		1*checkoutMgrMock.getRegistryShippingAddress(_,orderMock) >>  null
		when:
		boolean exist = payPalServiceManagerObj.ShippingCommerceRelationshipExist(orderMock)
		then:
		!exist
	}
	def "checking wether commerce item relationship is exist address not null"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		Address adr = new Address()
		adr.setAddress1("43206 parsons ave")
		hShip.getShippingAddress() >> adr
		hShip.getRegistryInfo() >>  "123456"
		hShip.getCommerceItemRelationshipCount() >> 1
		orderMock.getShippingGroups() >>[hShip]
		BBBAddress badr = new BBBAddressImpl()
		badr.setId("123456")
		1*checkoutMgrMock.getRegistryShippingAddress(_,orderMock) >>  [badr]
		when:
		boolean exist = payPalServiceManagerObj.ShippingCommerceRelationshipExist(orderMock)
		then:
		exist
	}
	def "checking wether commerce item relationship is exist relation ship count is zero"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		Address adr = new Address()
		hShip.getShippingAddress() >> adr
		hShip.getRegistryId() >>  "12345677"
		hShip.getCommerceItemRelationshipCount() >> 0
		orderMock.getShippingGroups() >>[hShip]
		BBBAddress badr = new BBBAddressImpl()
		badr.setId("123456")
		1*checkoutMgrMock.getRegistryShippingAddress(_,orderMock) >>  [badr]
		when:
		boolean exist = payPalServiceManagerObj.ShippingCommerceRelationshipExist(orderMock)
		then:
		!exist
	}
	def "checking wether commerce item relationship is exist but registry info not null"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		Address adr = new Address()
		hShip.getRegistryInfo() >>  "123456"
		hShip.getShippingAddress() >> adr
		hShip.getRegistryId() >>  "12345677"
		hShip.getCommerceItemRelationshipCount() >> 1
		orderMock.getShippingGroups() >>[hShip]
		BBBAddress badr = new BBBAddressImpl()
		badr.setId("123456")
		1*checkoutMgrMock.getRegistryShippingAddress(_,orderMock) >>  [badr]
		when:
		boolean exist = payPalServiceManagerObj.ShippingCommerceRelationshipExist(orderMock)
		then:
		!exist
	}
	def "checking wether commerce item relationship is exist but city is not null"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		Address adr = new Address()
		adr.setCity("columbus")
		hShip.getShippingAddress() >> adr
		hShip.getRegistryId() >>  "123456"
		hShip.getCommerceItemRelationshipCount() >> 1
		orderMock.getShippingGroups() >>[hShip,sShip]
		BBBAddress badr = new BBBAddressImpl()
		badr.setId("123456")
		1*checkoutMgrMock.getRegistryShippingAddress(_,orderMock) >>  [badr]
		when:
		boolean exist = payPalServiceManagerObj.ShippingCommerceRelationshipExist(orderMock)
		then:
		exist
	}
	def "Method Performing address exist in order ,shipping group count greater then one"(){
		given:
		orderMock.getShippingGroupCount() >> 2
		when:
		boolean exist = payPalServiceManagerObj.addressInOrder(orderMock)
		then:
		exist
	}
	def "Method Performing address exist in order ,shipping group count one city as null"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroupCount() >> 1
		Address adr = new Address()
		hShip.getShippingAddress() >> adr
		orderMock.getShippingGroups() >>[hShip,sShip]
		when:
		boolean exist = payPalServiceManagerObj.addressInOrder(orderMock)
		then:
		!exist
	}
	def "Method Performing address exist in order ,shipping group count one and address1 as null"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroupCount() >> 1
		Address adr = new Address()
		adr.setCity("Columbus")
		hShip.getRegistryInfo() >> "RegistryInfo"
		hShip.getShippingAddress() >> adr
		orderMock.getShippingGroups() >>[hShip,sShip]
		when:
		boolean exist = payPalServiceManagerObj.addressInOrder(orderMock)
		then:
		exist
	}
	def "Method Performing address exist in order ,shipping group count one"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		def BBBStoreShippingGroup sShip = Mock()
		orderMock.getShippingGroupCount() >> 1
		Address adr = new Address()
		adr.setCity("Columbus")
		adr.setAddress1("43206 parsons ave")
		hShip.getShippingAddress() >> adr
		orderMock.getShippingGroups() >>[hShip,sShip]
		when:
		boolean exist = payPalServiceManagerObj.addressInOrder(orderMock)
		then:
		exist
	}
	def "getting shipping methods for items in the cart "(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> null
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def EcoFeeCommerceItem cItem1 = Mock()
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		1*checkoutMgrMock.canItemShipByMethod(_, _, _) >> true
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:	
		shippingMethod == null
		0*catalogToolsMock.getShippingMethodsForSku(_, _, false)
	}
	def "getting shipping methods for items in the cart then return shipping method as null"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		1*checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard"
		2*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		shippingMethod == null
	}
	def "getting shipping methods for items in the cart then return shipping method as expedite"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		1*orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >>  false
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		2*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		1*commerceItemManagerMock.generateRangesForOrder(orderMock)
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		shippingMethod.equalsIgnoreCase("Standard1")
		1*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		1*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		1*paymentGroupManagerMock.runPricingEngine(profileMock,orderMock,PricingConstants.OP_REPRICE_ORDER_SUBTOTAL)
	}
	def "getting shipping methods for items in the cart then throws run processor exception"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >> true
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		2*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL) >> {throw new RunProcessException("Mock of Run Process")}
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null)>> "err_paypal_shipping_fail"
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_shipping_fail:err_paypal_shipping_fail")
		shippingMethod == null
		1*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		1*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
	}
	def "getting shipping methods for items in the cart then throws  commerce exception"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >> true
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		2*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		1*commerceItemManagerMock.generateRangesForOrder(orderMock) >> {throw new CommerceException("Mock of commerce exception")}
		0*paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL)
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null)>> "err_paypal_shipping_fail"
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_shipping_fail:err_paypal_shipping_fail")
		shippingMethod == null
		1*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		1*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		1*payPalServiceManagerObj.logError("CommerceException occured while validating shipping method ", _);
	}
	def "getting shipping methods for items in the cart then throws  deadlock exception"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		localLockManagerMock.acquireWriteLock(_,_) >> {throw new DeadlockException("Mock the dead lock excpetion")}		
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		0*checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		0*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		commerceItemManagerMock.generateRangesForOrder(orderMock) >> {throw new CommerceException("Mock of commerce exception")}
		0*paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL)
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null)>> "err_paypal_shipping_fail"
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_shipping_fail:err_paypal_shipping_fail")
		shippingMethod == null
		0*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		0*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		0*payPalServiceManagerObj.logError("CommerceException occured while validating shipping method ", _);
		1*payPalServiceManagerObj.logError("DeadlockException  occured while validating shipping method", _)
	}
	def "getting shipping methods for items in the cart then throws  TransactionDemarcationException exception"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(null)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		0*checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		0*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		commerceItemManagerMock.generateRangesForOrder(orderMock) >> {throw new CommerceException("Mock of commerce exception")}
		0*paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL)
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null)>> "err_paypal_shipping_fail"
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_shipping_fail:err_paypal_shipping_fail")
		shippingMethod == null
		0*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		0*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		0*payPalServiceManagerObj.logError("CommerceException occured while validating shipping method ", _);
		0*payPalServiceManagerObj.logError("DeadlockException  occured while validating shipping method", _)
		1*payPalServiceManagerObj.logError("TransactionDemarcationException  occured while validating shipping method", _)
	}
	def "getting shipping methods for items in the cart then throws  Business exception"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		1*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> {throw new BBBBusinessException("Mock of business exception")} >> [shipVO1,shipVO]
		0*paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL)
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null)>> "err_paypal_shipping_fail"
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_shipping_fail:err_paypal_shipping_fail")
		shippingMethod == null
		0*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		0*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		0*payPalServiceManagerObj.logError("CommerceException occured while validating shipping method ", _);
		0*payPalServiceManagerObj.logError("DeadlockException  occured while validating shipping method", _)
		0*payPalServiceManagerObj.logError("TransactionDemarcationException  occured while validating shipping method", _)
		1*payPalServiceManagerObj.logError("BBBBusinessException Exception occured while validating shipping method ", _);
	}
	def "getting shipping methods for items in the cart then throws  system exception"(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		payPalServiceManagerObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >> false
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		1*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> {throw new BBBSystemException("Mock of System exception")} >> [shipVO1,shipVO]
		0*paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL)
		1*lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SHIPPING_FAIL, "EN", null)>> "err_paypal_shipping_fail"
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_shipping_fail:err_paypal_shipping_fail")
		shippingMethod == null
		0*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		0*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		0*payPalServiceManagerObj.logError("CommerceException occured while validating shipping method ", _);
		0*payPalServiceManagerObj.logError("DeadlockException  occured while validating shipping method", _)
		0*payPalServiceManagerObj.logError("TransactionDemarcationException  occured while validating shipping method", _)
		1*payPalServiceManagerObj.logError("BBBSystem Exception occured while validating shipping method ", _);
	}
	def "getting shipping methods for items in the cart then throws  TransactionDemarcationException in finally block "(){
		given:
		def BBBHardGoodShippingGroup hShip = Mock()
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getStringValue() >> true
		payPalServiceManagerObj.setLocalLockManager(localLockManagerMock)
		payPalServiceManagerObj.setTransactionManager(transactionManagerMock)
		payPalServiceManagerObj.setCheckoutMgr(checkoutMgrMock)
		payPalServiceManagerObj.setCatalogTools(catalogToolsMock)
		payPalServiceManagerObj.setOrderManager(orderManagerMock)
		payPalServiceManagerObj.setPaymentGroupManager(paymentGroupManagerMock)
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		localLockManagerMock.hasWriteLock(_,_) >>  false
		def CommerceItemRelationship itemRelShip = Mock()
		def BBBCommerceItem cItem = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		itemRelShip.getQuantity() >> 2
		itemRelShip.getCommerceItem() >> cItem
		def CommerceItemRelationship itemRelShip1 = Mock()
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getCatalogRefId() >> "sku12345"
		itemRelShip1.getQuantity() >> 2
		itemRelShip1.getCommerceItem() >> cItem1
		hShip.getCommerceItemRelationships() >> [itemRelShip,itemRelShip1]
		orderMock.getShippingGroups() >>[hShip]
		checkoutMgrMock.canItemShipByMethod(_, _, _) >> false
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("Standard")
		ShipMethodVO shipVO1 = new ShipMethodVO()
		shipVO1.setShipMethodId("expedite")
		hShip.getShippingMethod() >> "Standard1"
		2*catalogToolsMock.getShippingMethodsForSku(_, _, true) >> null >> [shipVO1,shipVO]
		paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL) >> {
			transactionManagerMock.getTransaction() >> { throw new TransactionDemarcationException()}
		}
		localLockManagerMock.releaseWriteLock(_,_,true) >> {throw new LockManagerException("Mock of LockManager Exception")}
		when:
		String shippingMethod = payPalServiceManagerObj.validateShippingMethod(orderMock,profileMock)
		then:
		shippingMethod.equalsIgnoreCase("Standard1")
		1*payPalServiceManagerObj.logDebug("No skumethodvo not founds so this order is restricted")
		1*payPalServiceManagerObj.logInfo("Shipping method changed for shipping group id - null Shipping Method - Standard1")
		1*payPalServiceManagerObj.logError("TransactionDemarcationException  occured while validating shipping method", _)
		1*payPalServiceManagerObj.logError("TransactionDemarcationException releasing lock on profile",_)
	}
	def "create shipping group for paypal user"(){
	given:
	BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
	BBBAddressPPVO addr = new BBBAddressPPVO()
	Address shipAddr = new Address()
	shipAddr.setAddress1("43206 parsons ave")
	shipAddr.setCity("columbus")
	shipAddr.setCountry("US")
	resVO.setShippingAddress(addr)
	PayPalInputVO inputVO = new PayPalInputVO()
	inputVO.setProfile(profileMock)
	def BBBHardGoodShippingGroup hShip = Mock()
	hShip.getId() >>  "SG12345"
	hShip.getShippingAddress() >> shipAddr
	orderMock.getShippingGroups() >> [hShip]
	when:
	payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
	then:
	1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
	1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
	}
	def "create shipping group for paypal user address1 has null"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		def BBBHardGoodShippingGroup hShip1 = Mock()
		hShip1.getId() >>  "SG123451"
		hShip1.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip,]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		def BBBAddress badr = Mock()
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >>  badr
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 3
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		1*paymentGroupManagerMock.runPricingEngine(_,_,_)>>{}
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		1*hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		1 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		1 * commerceItemManagerMock.generateRangesForOrder(orderMock)
		1 * orderManagerMock.updateOrder(orderMock)
		((BBBAddressContainer)inputVO.getMultiShipAddContainer()).getNewAddressMap().get("SG12345")!=null
		((BBBAddressContainer)inputVO.getAddressCoontainer()).getNewAddressMap().get("SG12345")!=null
		1*sglr.setQuantity(3)
		0*sglr1.setQuantity(2)
		}
	def "create shipping group for paypal user city has null"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		profileMock.isTransient() >>  true
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		def BBBAddress badr = Mock()
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >>  badr
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1 * requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		1 * hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		0 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		1 * commerceItemManagerMock.generateRangesForOrder(orderMock)
		1 * orderManagerMock.updateOrder(orderMock)
		0 * sglr.setQuantity(3)
		0 * sglr1.setQuantity(2)
		}
	def "create shipping group for paypal user city has null throw business exception"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		profileMock.isTransient() >>  false
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >>  {throw new BBBBusinessException("Mock of Business exception")}
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1 * requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		1 * hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		0 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		1 * commerceItemManagerMock.generateRangesForOrder(orderMock)
		1 * orderManagerMock.updateOrder(orderMock)
		0 * sglr.setQuantity(3)
		0 * sglr1.setQuantity(2)
		}
	def "create shipping group for paypal user city has null throw System exception"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		profileMock.isTransient() >>  false
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >>  {throw new BBBSystemException("Mock of System exception")}
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1 * requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1 * requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		1 * hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		0 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		1 * commerceItemManagerMock.generateRangesForOrder(orderMock)
		1 * orderManagerMock.updateOrder(orderMock)
		0 * sglr.setQuantity(3)
		0 * sglr1.setQuantity(2)
		}
	def "create shipping group for paypal user city has null throw CommerceException exception"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		profileMock.isTransient() >>  false
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		def BBBAddress badr = Mock()
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >> badr
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		commerceItemManagerMock.generateRangesForOrder(orderMock) >> {throw new CommerceException("Mock the Commerce Exception")}
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1 * requestMock.serviceParameter('error', requestMock, responseMock)
		1 * requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		1 * hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		1 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		0 * orderManagerMock.updateOrder(orderMock)
		0 * sglr.setQuantity(3)
		0 * sglr1.setQuantity(2)
		}
	def "create shipping group for paypal user city has null throw RunProcessException exception"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		profileMock.isTransient() >>  false
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		def BBBAddress badr = Mock()
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >> badr
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL) >> {throw new RunProcessException("Mock the RunProcess Exception")}
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1 * requestMock.serviceParameter('error', requestMock, responseMock)
		1 * requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		1 * hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		1 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		1 * commerceItemManagerMock.generateRangesForOrder(orderMock)
		1 * orderManagerMock.updateOrder(orderMock)
		0 * sglr.setQuantity(3)
		0 * sglr1.setQuantity(2)
		}
	def "create shipping group for paypal user city has empty"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		Address shipAddr = new Address()
		shipAddr.setAddress1("43206 parsons ave")
		shipAddr.setCountry("US")
		resVO.setShippingAddress(addr)
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
	def "create shipping group for paypal user country has not empty"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		Address shipAddr = new Address()
		shipAddr.setCountry("US")
		resVO.setShippingAddress(addr)
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		orderMock.getShippingGroups() >> [hShip]
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
	def "create shipping group for paypal for shipping group lis empty"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		Address shipAddr = new Address()
		shipAddr.setCountry("US")
		resVO.setShippingAddress(addr)
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		orderMock.getShippingGroups() >> []
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
		1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
	def "create shipping group for paypal throws TransactionDemarcationException"(){
		given:
		payPalServiceManagerObj.setTransactionManager(null)
		localLockManagerMock.hasWriteLock(_,_)>> true
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		Address shipAddr = new Address()
		shipAddr.setCountry("US")
		resVO.setShippingAddress(addr)
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		orderMock.getShippingGroups() >> []
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
		1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
		def "create shipping group for paypal throws TransactionDemarcationException in finally block"(){
		given:
		def BBBAddressAPI addressApi = Mock()
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		BBBAddressContainer singleAddrCon = new BBBAddressContainer()
		BBBAddressContainer multiAddrCon = new BBBAddressContainer()
		inputVO.setMultiShipAddContainer(multiAddrCon)
		inputVO.setAddressCoontainer(singleAddrCon)
		Address shipAddr = new Address()
		Address shipAddr1 = new Address()
		shipAddr1.setState("OH")
		resVO.setShippingAddress(addr)
		inputVO.setProfile(profileMock)
		profileMock.isTransient() >>  false
		def BBBHardGoodShippingGroup hShip = Mock()
		hShip.getId() >>  "SG12345"
		hShip.getShippingAddress() >> shipAddr
		def BBBHardGoodShippingGroup hShip1 = Mock()
		hShip1.getId() >>  "SG123451"
		hShip1.getShippingAddress() >> shipAddr1
		orderMock.getShippingGroups() >> [hShip]
		checkoutMgrMock.getProfileAddressTool() >> addressApi
		def BBBAddress badr = Mock()
		addressApi.addNewPayPalShippingAddress(profileMock, _, _,false, false) >> badr
		def BBBCommerceItem cItem = Mock()
		cItem.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr = Mock()
		sglr.getQuantity() >> 2
		cItem.getShippingGroupRelationships() >> [sglr]
		def BBBCommerceItem cItem1 = Mock()
		cItem1.getQuantity() >> 2
		def BBBShippingGroupCommerceItemRelationship sglr1 = Mock()
		sglr1.getQuantity() >> 2
		cItem1.getShippingGroupRelationships() >> [sglr1]
		orderMock.getCommerceItems() >> [cItem,cItem1,new EcoFeeCommerceItem()]
		orderManagerMock.getCommerceItemManager() >> commerceItemManagerMock
		paymentGroupManagerMock.runPricingEngine(profileMock, orderMock, PricingConstants.OP_REPRICE_ORDER_SUBTOTAL) >> {transactionManagerMock.getTransaction() >> {throw new TransactionDemarcationException()}}
		localLockManagerMock.releaseWriteLock(_, _, true) >> {throw new LockManagerException()}
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1 * requestMock.serviceParameter('error', requestMock, responseMock)
		1 * requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		0 * hShip1.setShippingMethod("3g")
		1 * hShip.setShippingMethod("3g")
		1 * hShip.setSaveAllProperties(true)
		1 * hShip.setPropertyValue('sourceId', null)
		1 * hShip.setPropertyValue('isFromPaypal', true)
		1 * hShip.setPropertyValue('addressStatus', null)
		1 * commerceItemManagerMock.generateRangesForOrder(orderMock)
		1 * orderManagerMock.updateOrder(orderMock)
		0 * sglr.setQuantity(3)
		0 * sglr1.setQuantity(2)
		}
	def "create shipping group for paypal throws Deadlock Exception"(){
		given:
		payPalServiceManagerObj.setTransactionManager(null)
		localLockManagerMock.acquireWriteLock(_,_)>> {throw new DeadlockException("Mock of Deadlock Exception")}
		BBBGetExpressCheckoutDetailsResVO resVO =new   BBBGetExpressCheckoutDetailsResVO()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		resVO.setShippingAddress(addr)
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		when:
		payPalServiceManagerObj.createShippingGroupPP(resVO, orderMock, inputVO)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
		1*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
	def "create shipping group for paypal throws Business exception"(){
		given:
		payPalServiceManagerObj = Spy()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		payPalServiceManagerObj.createShippingGroup(_, orderMock, inputVO) >>  {throw new BBBBusinessException("Mock of the Business exception")}
		when:
		payPalServiceManagerObj.createShippingGroupPP(null, orderMock, inputVO)
		then:
		0*requestMock.serviceParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
		0*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
	def "create shipping group for paypal throws System exception"(){
		given:
		payPalServiceManagerObj = Spy()
		BBBAddressPPVO addr = new BBBAddressPPVO()
		PayPalInputVO inputVO = new PayPalInputVO()
		inputVO.setProfile(profileMock)
		payPalServiceManagerObj.createShippingGroup(_, orderMock, inputVO) >>  {throw new BBBSystemException("Mock of the System exception")}
		when:
		payPalServiceManagerObj.createShippingGroupPP(null, orderMock, inputVO)
		then:
		0*requestMock.serviceParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
		0*requestMock.setParameter(BBBPayPalConstants.PAYPAL_ADDRESS, addr);
		}
	def "method to get redirect url for paypal call"(){
		given:
		catalogToolsMock.getAllValuesForKey(_, "PPWSDLRedirectURL_setExpressCheckout")>> ["/paypal.jsp"]
		when:
		String url = payPalServiceManagerObj.getPayPalRedirectURL()
		then:
		url.equals("/paypal.jsp")
	}
	def "creating paypal payment group for ExpressCheckout already payapl exist"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO =  new BBBGetExpressCheckoutDetailsResVO()
		def Paypal paypal = Mock()
		orderMock.getPaymentGroups() >> [paypal]
		when:
		boolean success = payPalServiceManagerObj.createPaymentGroup(resVO,orderMock,profileMock)
		then:
		success
	}
	def "creating paypal payment group for ExpressCheckout "(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO =  new BBBGetExpressCheckoutDetailsResVO()
		orderMock.getPaymentGroups() >> [new CreditCard()]
		paymentGroupManagerMock.createPayPalPaymentGroup(resVO, orderMock, profileMock) >> false
		when:
		boolean success = payPalServiceManagerObj.createPaymentGroup(resVO,orderMock,profileMock)
		then:
		!success
	}
	def "validating phone number in international response"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO = new  BBBGetExpressCheckoutDetailsResVO()
		resVO.setContactPhone("adqweq")
		BBBAddressPPVO adr = new BBBAddressPPVO()
		adr.setCountry("IN")
		resVO.setBillingAddress(adr)
		when:
		payPalServiceManagerObj.validateDetails(resVO)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_phone_not_valid:VALIDATION_FAILED")
	}
	def "validating phone number is invalid in response"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathUS"
		BBBGetExpressCheckoutDetailsResVO resVO = new  BBBGetExpressCheckoutDetailsResVO()
		resVO.setContactPhone("wqw")
		BBBAddressPPVO adr = new BBBAddressPPVO()
		adr.setCountry("US")
		resVO.setBillingAddress(adr)
		when:
		payPalServiceManagerObj.validateDetails(resVO)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_phone_not_valid:VALIDATION_FAILED")
	}
	def "validating email id in response"(){
		given:
		payPalServiceManagerObj = Spy()
		BBBGetExpressCheckoutDetailsResVO resVO = new  BBBGetExpressCheckoutDetailsResVO()
		PayerInfoVO payerVO = new PayerInfoVO()
		payPalServiceManagerObj.getSiteId() >> "TBS_BedBathUS"
		resVO.setContactPhone("9642394258")
		BBBAddressPPVO adr = new BBBAddressPPVO()
		adr.setCountry("US")
		resVO.setBillingAddress(adr)
		resVO.setPayerInfo(payerVO)
		when:
		payPalServiceManagerObj.validateDetails(resVO)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_email_not_valid:VALIDATION_FAILED")
	}
	def "validating payeer id in response"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO = new  BBBGetExpressCheckoutDetailsResVO()
		PayerInfoVO payerVO = new PayerInfoVO()
		resVO.setContactPhone("9642394258")
		BBBAddressPPVO adr = new BBBAddressPPVO()
		adr.setCountry("US")
		resVO.setBillingAddress(adr)
		resVO.setPayerInfo(payerVO)
		payerVO.setPayerEmail("test@bbb.com")
		when:
		payPalServiceManagerObj.validateDetails(resVO)
		then:
		Exception ex = thrown()
		ex.getMessage().equals(BBBPayPalConstants.ERR_PAYPAL_PAYER_ID_NOT_VALID+":VALIDATION_FAILED")
	}
	def "validating payeer info null in response"(){
		given:
		BBBGetExpressCheckoutDetailsResVO resVO = new  BBBGetExpressCheckoutDetailsResVO()
		resVO.setContactPhone("9642394258")
		BBBAddressPPVO adr = new BBBAddressPPVO()
		adr.setCountry("US")
		resVO.setBillingAddress(adr)
		expect:
		payPalServiceManagerObj.validateDetails(resVO)
	}
	def "update token in order throw dead lock exception"(){
		given:
		localLockManagerMock.acquireWriteLock(_,_) >> {throw new DeadlockException()}
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, "EN", null) >> "err_paypal_token_update_fail"
		when:
		payPalServiceManagerObj.updateTokenInOrder("token123", orderMock, "payer123", profileMock)
		then:
		0*orderMock.setPayerId("payer123")
		0*orderMock.setToken("token123")
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_token_update_fail:err_paypal_token_update_fail")
	}
	def "update token in order throw TransactionDemarcation Exception"(){
		given:
		localLockManagerMock.hasWriteLock(_,_) >> true
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, "EN", null) >> "err_paypal_token_update_fail"
		payPalServiceManagerObj.setTransactionManager(null)
		when:
		payPalServiceManagerObj.updateTokenInOrder("token123", orderMock, "payer123", profileMock)
		then:
		0*orderMock.setPayerId("payer123")
		0*orderMock.setToken("token123")
		Exception ex = thrown()
		ex.getMessage().equalsIgnoreCase("err_paypal_token_update_fail:err_paypal_token_update_fail")
	}
	def "update token in order throw TransactionDemarcation Exception in finlly block"(){
		given:
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_TOKEN_UPDATE_FAIL, "EN", null) >> "err_paypal_token_update_fail"
		orderManagerMock.updateOrder(orderMock) >> {transactionManagerMock.getTransaction() >> {throw new TransactionDemarcationException()}}
		localLockManagerMock.releaseWriteLock(_,_,true) >> {throw new LockManagerException()}
		when:
		payPalServiceManagerObj.updateTokenInOrder("token123", orderMock, "payer123", profileMock)
		then:
		1 * orderMock.setPropertyValue('payerId', 'payer123')
		1 * orderMock.toString()
		1 * orderMock.setPropertyValue('token', 'token123')
		1 * orderMock.setTimeStamp(_)	
	}
	def "update token in order where token id is null"(){
		when:
		payPalServiceManagerObj.updateTokenInOrder("", orderMock, "payer123", profileMock)
		then:
		0 * orderMock.setPropertyValue('payerId', 'payer123')
		0 * orderMock.setPropertyValue('token', 'token123')
		0 * orderMock.setTimeStamp(_)
	}
	def "update token in order where order is null"(){
		when:
		payPalServiceManagerObj.updateTokenInOrder("t1234", null, "payer123", profileMock)
		then:
		0 * orderMock.setPropertyValue('payerId', 'payer123')
		0 * orderMock.setPropertyValue('token', 'token123')
		0 * orderMock.setTimeStamp(_)
	}
	def "compare old cart with new cart"(){
		given:
		payPalSessionBean.setOldCartMap(["ci12345":2L])
		def OrderHolder order = Mock()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> order
		order.getCurrent() >> orderMock
		def BBBCommerceItem citem = Mock()
		citem.getId() >> "ci12345"
		citem.getQuantity() >> 2
		orderMock.getCommerceItems() >> [citem]
		when:
		boolean valid = payPalServiceManagerObj.compareOldAndNewCart(false)
		then:
		valid
	}
	def "compare old cart with new cart diffrerent quantity"(){
		given:
		payPalSessionBean.setOldCartMap(["ci12345":2L])
		def OrderHolder order = Mock()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> order
		order.getCurrent() >> orderMock
		def BBBCommerceItem citem = Mock()
		citem.getId() >> "ci12345"
		citem.getQuantity() >> 3
		orderMock.getCommerceItems() >> [citem]
		when:
		boolean valid = payPalServiceManagerObj.compareOldAndNewCart(false)
		then:
		!valid
	}
	def "compare old cart with new cart diffrerent commerce items"(){
		given:
		payPalSessionBean.setOldCartMap(["ci12345":2L])
		def OrderHolder order = Mock()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> order
		requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		order.getCurrent() >> orderMock
		def BBBCommerceItem citem = Mock()
		citem.getId() >> "ci123456"
		citem.getQuantity() >> 3
		orderMock.getCommerceItems() >> [citem]
		orderMock.getPaymentGroups() >> []
		when:
		boolean valid = payPalServiceManagerObj.compareOldAndNewCart(true)
		then:
		!valid
	}
	def "compare old cart with new cart  commerce items empty"(){
		given:
		payPalSessionBean.setOldCartMap(["ci12345":2L])
		def OrderHolder order = Mock()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> order
		order.getCurrent() >> orderMock
		orderMock.getCommerceItems() >> []
		when:
		boolean valid = payPalServiceManagerObj.compareOldAndNewCart(false)
		then:
		valid
	}
	def "compare old cart with new cart  and old cart are not matching"(){
		given:
		payPalServiceManagerObj.setLoggingDebug(true)
		payPalSessionBean.setOldCartMap(["ci12345":2L,"ci1234567":3L])
		def OrderHolder order = Mock()
		requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> order
		order.getCurrent() >> orderMock
		def BBBCommerceItem citem = Mock()
		citem.getId() >> "ci12345"
		citem.getQuantity() >> 2
		orderMock.getCommerceItems() >> [citem]
		when:
		boolean valid = payPalServiceManagerObj.compareOldAndNewCart(false)
		then:
		!valid
	}
	def"remove paypal payment group from order"(){
		given:
		def Paypal paypal = Mock()
		orderMock.getPaymentGroups() >> [paypal]
		def BBBRepositoryContactInfo  contactinfo = Mock()
		orderMock.getBillingAddress() >> contactinfo
		contactinfo.getIsFromPaypal() >> true
		orderManagerMock.getPaymentGroupManager() >> paymentGroupManagerMock
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		1*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		1*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
		1*orderManagerMock.updateOrder(orderMock)
		1*orderMock.setPropertyValue('token',null)
	}
	def"remove paypal payment group from order billing address is null"(){
		given:
		localLockManagerMock.hasWriteLock(_,_) >> true
		def Paypal paypal = Mock()
		orderMock.getPaymentGroups() >> [paypal]
		orderManagerMock.getPaymentGroupManager() >> paymentGroupManagerMock
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		1*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
		1*orderManagerMock.updateOrder(orderMock)
	}
	def"remove paypal payment group from order billing address not from paypal"(){
		given:
		localLockManagerMock.hasWriteLock(_,_) >> true
		def Paypal paypal = Mock()
		orderMock.getPaymentGroups() >> [new CreditCard(),paypal]
		def BBBRepositoryContactInfo  contactinfo = Mock()
		orderMock.getBillingAddress() >> contactinfo
		contactinfo.getIsFromPaypal() >> false
		orderManagerMock.getPaymentGroupManager() >> paymentGroupManagerMock
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		1*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
		1*orderManagerMock.updateOrder(orderMock)
	}
	def"remove paypal payment group from order throws Deadlock exception"(){
		given:
		localLockManagerMock.acquireWriteLock(_,_) >> {throw new DeadlockException("Mock the Dead lcok exception")}
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null) >> "err_paypal_details_remove_fail"
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_details_remove_fail:err_paypal_details_remove_fail")
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		0*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
		0*orderManagerMock.updateOrder(orderMock)
	}
	def"remove paypal payment group from order throws commerce excpetion"(){
		given:
		localLockManagerMock.hasWriteLock(_,_) >> true
		def Paypal paypal = Mock()
		orderMock.getPaymentGroups() >> [new CreditCard(),paypal]
		def BBBRepositoryContactInfo  contactinfo = Mock()
		orderMock.getBillingAddress() >> contactinfo
		contactinfo.getIsFromPaypal() >> false
		orderManagerMock.getPaymentGroupManager() >> paymentGroupManagerMock
		orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock the commerce exception")}
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null) >> "err_paypal_details_remove_fail"
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_details_remove_fail:err_paypal_details_remove_fail")
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		1*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
	}
	def"remove paypal payment group from order throws TransactionDemarcationException exception"(){
		given:
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null) >> "err_paypal_details_remove_fail"
		payPalServiceManagerObj.setTransactionManager(null)
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_details_remove_fail:err_paypal_details_remove_fail")
		0*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		0*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
		0*orderManagerMock.updateOrder(orderMock)
	}
	def"remove paypal payment group from order throws TransactionDemarcationException exception in finally block"(){
		given:
		def Paypal paypal = Mock()
		orderMock.getPaymentGroups() >> [paypal]
		def BBBRepositoryContactInfo  contactinfo = Mock()
		orderMock.getBillingAddress() >> contactinfo
		contactinfo.getIsFromPaypal() >> true
		orderManagerMock.getPaymentGroupManager() >> paymentGroupManagerMock
		1*orderManagerMock.updateOrder(orderMock) >> { transactionManagerMock.getTransaction() >> {throw new TransactionDemarcationException("Mock of TransactionDemarcation Exception")}}
		localLockManagerMock.releaseWriteLock(_,_,true) >> {throw new LockManagerException()}
		lblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_DETAILS_REMOVE_FAIL, "EN", null) >> "err_paypal_details_remove_fail"
		when:
		payPalServiceManagerObj.removePayPalPaymentGroup(orderMock,profileMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("err_paypal_details_remove_fail:err_paypal_details_remove_fail")
		1*orderMock.setPropertyValue(BBBCoreConstants.BILLING_ADDRESS, null)
		1*paymentGroupManagerMock.removePaymentGroupFromOrder(orderMock,_)
	}
	def "get cuureny code based on site"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setSiteRepository(siteRepositoryMock)
		def RepositoryItem repItem = Mock()
		siteRepositoryMock.getItem("BedBathCanada", BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> repItem
		repItem.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "CAD"
		payPalServiceManagerObj.getSiteId() >> "BedBathCanada"
		when:
		String code = payPalServiceManagerObj.getPricingCurrencyCode()
		then:
		code.equals("CAD")
	}
	def "get cuureny code based on site is null"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setSiteRepository(siteRepositoryMock)
		siteRepositoryMock.getItem("BedBath", BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> null
		payPalServiceManagerObj.getSiteId() >> "BedBath"
		when:
		String code = payPalServiceManagerObj.getPricingCurrencyCode()
		then:
		code.equals("USD")
	}
	def "get cuureny code based on site throws RepositoryPriorityComparator exception"(){
		given:
		payPalServiceManagerObj = Spy()
		payPalServiceManagerObj.setSiteRepository(siteRepositoryMock)
		siteRepositoryMock.getItem("BedBath", BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> {throw new RepositoryException("Mock of repository exception")}
		payPalServiceManagerObj.getSiteId() >> "BedBath"
		when:
		String code = payPalServiceManagerObj.getPricingCurrencyCode()
		then:
		code.equals("USD")
		1*payPalServiceManagerObj.logError(_)
	}
	void getAllValuesForKey(){
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_LOGIN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["user"]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PASSWORD + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>  ["user"]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SIGNATURE + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_VERSION + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["test"]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_ADDR_OR + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >>[ "1"]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_PAY_ACT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["PAYACT" ]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_WSTOKEN + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_EXP_MINUTES) >> ["1234"]
	1*catalogToolsMock.getAllValuesForKey(_, BBBWebServiceConstants.TXT_WSDLKEY_PP_SUBJECT + BBBWebServiceConstants.TXT_UNDERSCORE + BBBWebServiceConstants.TXT_WSDLKEY_PP_CREDIENTAIL) >> ["SUBJECCT"]
	}
	
}
