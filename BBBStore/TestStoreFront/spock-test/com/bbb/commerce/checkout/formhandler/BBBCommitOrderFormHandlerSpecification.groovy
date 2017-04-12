package com.bbb.commerce.checkout.formhandler

import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException
import atg.commerce.inventory.InventoryException
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder
import atg.commerce.order.PaymentGroup
import atg.commerce.order.PaymentGroupManager
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroup
import atg.commerce.order.SimpleOrderManager
import atg.commerce.pricing.PricingModelHolder
import atg.commerce.states.OrderStates;
import atg.commerce.states.StateDefinitions;
import atg.commerce.util.RepeatingRequestMonitor
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.payment.creditcard.ExtendableCreditCardTools
import atg.repository.RepositoryItem
import atg.service.idgen.IdGenerator
import atg.service.pipeline.PipelineResult
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile
import atg.userprofiling.ProfileTools

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.checkout.manager.BBBCreditCardTools
import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager
import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.common.BBBVBVSessionBean
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.order.BBBCreditCard
import com.bbb.commerce.order.BBBGiftCard
import com.bbb.commerce.order.ManageCheckoutLogging
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBOrderTools
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.integration.ServiceResponseBase
import com.bbb.framework.integration.vo.ResponseErrorVO
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.payment.giftcard.GiftCardBeanInfo
import com.bbb.payment.giftcard.GiftCardStatus;
import com.bbb.paypal.BBBSetExpressCheckoutResVO
import com.bbb.personalstore.manager.PersonalStoreManager
import com.bbb.profile.session.BBBSessionBean
import com.bbb.rest.generic.BBBGenericSessionComponent
import com.bbb.userprofiling.BBBCookieManager
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.CommonConfiguration
import com.bbb.valuelink.ValueLinkGiftCardProcessor
import com.bbb.valuelink.ValueLinkGiftCardUtil
import com.cardinalcommerce.client.CentinelRequest

import javax.ejb.SessionBean
import javax.servlet.ServletException
import javax.servlet.http.Cookie
import javax.transaction.SystemException
import javax.transaction.Transaction
import javax.transaction.TransactionManager

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class BBBCommitOrderFormHandlerSpecification extends BBBExtendedSpec{
	
	def BBBCommitOrderFormHandler testObj
	def LblTxtTemplateManager messageHandlerMock = Mock()
	def ValueLinkGiftCardProcessor giftCardProcessorMock = Mock()
	def BBBInventoryManager inventoryManagerMock = Mock()
	def ValueLinkGiftCardUtil valueLinkGiftCardUtilMock = Mock()
	def BBBCreditCardTools creditCardToolsMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def CheckoutProgressStates checkoutStateMock = Mock()
	def CommonConfiguration commonConfigurationMock = Mock()
	def BBBSessionBean sessionBeanMock = Mock()
	def BBBVBVSessionBean vbvSessionBeanMock = Mock()
	def IdGenerator idGeneratorMock = Mock()
	def ManageCheckoutLogging manageLoggingMock = Mock()
	def PaymentGroupManager paymentGroupManagerMock = Mock()
	def BBBCheckoutManager checkoutManagerMock = Mock()
	def BBBPayPalServiceManager paypalServiceManagerMock = Mock()
	def Profile userProfileMock = Mock()
	def BBBPayPalSessionBean payPalSessionBeanMock = Mock()
	def BBBGenericSessionComponent bbbGenericSessionComponentMock = Mock()
	def PersonalStoreManager psManagerMock = Mock()
	def ProfileTools profileToolsMock = Mock() 
	def BBBCookieManager cookieManagerMock = Mock()
	def BBBSameDayDeliveryManager sameDayDeliveryManagerMock = Mock()
	def BBBOrder bbbOrderMock = Mock()
	def BBBOrderImpl bbbOrderImplMock = Mock() 
	def BBBVerifiedByVisaVO bbbVerifiedByVisaVOMock = Mock()
	def BBBCreditCard bbbCreditCardMock = Mock()
	def BBBCommerceItem bbbCommerceItemMock = Mock()
	def RepositoryItem repositoryItemMock = Mock() 
	def BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock = Mock()
	def CommerceItem commerceItemMock = Mock()
	def BBBGiftCard bbbGiftCardMock = Mock()
	def GiftCardBeanInfo giftCardBeanInfoMock = Mock()
	def AuxiliaryData auxiliaryDataMock = Mock()
	def PaymentGroup paymentGroupMock = Mock()
	def Transaction transactionMock = Mock()
	def BBBSetExpressCheckoutResVO bbbSetExpressCheckoutResVOMock = Mock()
	def RepeatingRequestMonitor repeatingRequestMonitorMock = Mock()
	def ErrorStatus errorStatusMock = Mock()
	def SimpleOrderManager orderManagerMock = Mock()
	def BBBRepositoryContactInfo bbbRepositoryContactInfoMock = Mock()
	def TransactionManager transactionManagerMock = Mock()
	def GiftCardStatus giftCardStatusMock = Mock()
	def RepositoryItem availablePromotionsMock = Mock()
	def RepositoryItem selectedPromotionsMock = Mock()
	def RepositoryItem activePromotionsMock = Mock()
	def PricingModelHolder pricingModelHolderMock = Mock()
	def Cookie cookieMock = Mock()
	def PipelineResult pipelineResultMock = Mock()
	def BBBOrderTools bbbOrderToolsMock = Mock()
	def OrderHolder orderHolderMock = Mock()
	def GiftWrapCommerceItem giftWrapCommerceItemMock = Mock()
	
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	private boolean booleanResult;
	def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
	def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()
	
	
	def setup() {
		testObj = new BBBCommitOrderFormHandler(giftCardProcessor:giftCardProcessorMock,inventoryManager:inventoryManagerMock,checkoutState:checkoutStateMock,
			valueLinkGiftCardUtil:valueLinkGiftCardUtilMock,messageHandler:messageHandlerMock,creditCardTools:creditCardToolsMock,
			manageLogging:manageLoggingMock,sessionBean:sessionBeanMock,catalogTools:catalogToolsMock,commonConfiguration:commonConfigurationMock,
			cartAndCheckOutConfigType:"cartAndCheckOutConfigTypeMock",onlineOrderPrefixKey:"onlineOrderPrefixKeyMock",bopusOrderPrefixKey:"bopusOrderPrefixKey",
			vbvSessionBean:vbvSessionBeanMock,idGenerator:idGeneratorMock,paymentGroupManager:paymentGroupManagerMock,paypalServiceManager:paypalServiceManagerMock,
			checkoutManager:checkoutManagerMock,userProfile:userProfileMock,payPalSessionBean:payPalSessionBeanMock,bbbGenericSessionComponent:bbbGenericSessionComponentMock,
			psManager:psManagerMock,profileTools:profileToolsMock,cookieManager:cookieManagerMock,sameDayDeliveryManager:sameDayDeliveryManagerMock)
		
			BBBConfigRepoUtils.setBbbCatalogTools(catalogToolsMock)
	}
	
	
	////////////////////////////////////////TestCases for preCommitOrder --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public void preCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////

	
	def "preCommitOrder. This TC is the Happy flow of preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			1 * catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> "itemIneligible"
			
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [bbbCreditCardMock,paymentGroupMock]
			bbbCreditCardMock.getCardVerificationNumber() >> ""
			bbbCreditCardMock.getCreditCardNumber() >> "4111111111111111"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			testObj.setCardVerNumber("1")
			1 * creditCardToolsMock.validateSecurityCode(testObj.getCardVerNumber(), bbbCreditCardMock.getCreditCardType()) >> 0
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> true
			bbbOrderMock.getSiteId() >> "BedBathUS"
			1 * inventoryManagerMock.getVDCProductAvailability(bbbOrderMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId(), bbbCommerceItemMock.getQuantity(), BBBCoreConstants.CACHE_DISABLED) >> 1
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue("displayName") >> "Kian Chair"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore"
			requestMock.getContextPath() >> "/store"
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * messageHandlerMock.getErrMsg('err_sdd_preview_error', null, null)
			1 * messageHandlerMock.getErrMsg('err_checkout_insufficient_inventory', 'EN', ['product':'Kian Chair'])
			1 * bbbCreditCardMock.setCardVerificationNumber('1')
			1 * testObj.preCommitOrder(requestMock,responseMock)
			1 * testObj.logDebug('CVV number of CC is not set. User is using express Checkout. \nSetting the CVV number into PG, CVV number entered in Review Page is - 1', null)
			1 * testObj.logDebug('adding form exception: err_checkout_insufficient_inventory', null)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error', null)
			1 * testObj.logInfo('START: Verifying inventory stock for VDC items', null)
			1 * testObj.logInfo('START: getGiftCardBalance during preCommitOrder', null)
			testObj.getCommitOrderErrorURL().equals("/storeatg-rest-ignore")
		
	}
	
	
	def "preCommitOrder. This TC is when validateGiftCard private method returns true"(){
		given:
			spyofPreCommitOrder()
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			testObj.setOrder(bbbOrderMock)
			
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> "marketIneligible"
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [commerceItemMock,bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> true
			bbbOrderMock.getSiteId() >> "BedBathUS"
			1 * inventoryManagerMock.getVDCProductAvailability(bbbOrderMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId(), bbbCommerceItemMock.getQuantity(), BBBCoreConstants.CACHE_DISABLED) >> 2
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue("displayName") >> "Kian Chair"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore"
			requestMock.getContextPath() >> "/store"
			
			//validateGiftCard Private Method Coverage
			testObj.setValueLinkGiftCardUtil(valueLinkGiftCardUtilMock)
			testObj.setGiftCardProcessor(giftCardProcessorMock)
			testObj.getSiteIdFromManager() >> "BedBathUS"
			bbbOrderMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "2323"
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			1 * giftCardProcessorMock.getBalance("gc2322", "gc1", bbbOrderMock.getId(), bbbGiftCardMock.getId(),testObj.getSiteIdFromManager()) >> giftCardBeanInfoMock
			giftCardBeanInfoMock.setStatus(TRUE)
			giftCardBeanInfoMock.setBalance("2")
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * messageHandlerMock.getErrMsg('err_sdd_preview_error', null, null)
			1 * testObj.logInfo('START: getGiftCardBalance during preCommitOrder', null)
			valueLinkGiftCardUtilMock.getAmountInVLFormat(_) >> 000
	
		}
	
	def "preCommitOrder. This TC is when BBBSystemException thrown in validateGiftCard private Method"(){
		given:
			spyofPreCommitOrder()
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> "itemUnavailable"
			
			//checkInventory Private Method Coverage
			def BBBCommerceItem bbbCommerceItemMock1 = Mock()
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock,bbbCommerceItemMock1]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock1.getStoreId() >> "2296666"
			bbbCommerceItemMock.isVdcInd() >> true
			bbbOrderMock.getSiteId() >> "BedBathUS"
			inventoryManagerMock.getVDCProductAvailability(bbbOrderMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId(), bbbCommerceItemMock.getQuantity(), BBBCoreConstants.CACHE_DISABLED) >> 0
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue("displayName") >> "Kian Chair"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore"
			requestMock.getContextPath() >> "/store"
			
			//validateGiftCard Private Method Coverage
			testObj.setValueLinkGiftCardUtil(valueLinkGiftCardUtilMock)
			testObj.setGiftCardProcessor(giftCardProcessorMock)
			testObj.getSiteIdFromManager() >> "BedBathUS"
			bbbOrderMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "2323"
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			giftCardProcessorMock.getBalance("gc2322", "gc1", bbbOrderMock.getId(), bbbGiftCardMock.getId(),testObj.getSiteIdFromManager()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error: err_sdd_preview_error', null)
			1 * testObj.logInfo('START: PRE-Committing order [B223233]', null)
			1 * testObj.logError('checkout_1008: Error while getGiftCardBalance [gc2322] for order [B223233]', _)
		}
	
	
	def "preCommitOrder. This TC is when BBBBusinessException thrown in validateGiftCard private Method"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> "Unavailable"
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> true
			bbbOrderMock.getSiteId() >> "BedBathUS"
			1 * inventoryManagerMock.getVDCProductAvailability(bbbOrderMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId(), bbbCommerceItemMock.getQuantity(), BBBCoreConstants.CACHE_DISABLED) >> 1
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue("displayName") >> "Kian Chair"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			requestMock.getContextPath() >> "/store"
			
			//validateGiftCard Private Method Coverage
			testObj.setValueLinkGiftCardUtil(valueLinkGiftCardUtilMock)
			testObj.setGiftCardProcessor(giftCardProcessorMock)
			testObj.getSiteIdFromManager() >> "BedBathUS"
			bbbOrderMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "2323"
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			giftCardProcessorMock.getBalance("gc2322", "gc1", bbbOrderMock.getId(), bbbGiftCardMock.getId(),testObj.getSiteIdFromManager()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * testObj.isPreCommitChecked()
			1 * testObj.logInfo('START: PRE-Committing order [B223233]', null)
			1 * testObj.logError('checkout_1008: Error while getGiftCardBalance [gc2322] for order [B223233]', _)
			1 * messageHandlerMock.getErrMsg('err_checkout_insufficient_inventory', 'EN', ['product':'Kian Chair'])
			1 * testObj.logDebug('adding form exception: err_checkout_insufficient_inventory', null)
		}
	
	
	def "preCommitOrder. This TC is when validateGiftCard private method returns false"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> ""
			
			//validateCreditCard Private Method Coverage
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >>> [null,bbbVerifiedByVisaVOMock]
			bbbOrderMock.getPaymentGroups() >> [bbbCreditCardMock]
			bbbCreditCardMock.getCardVerificationNumber() >> ""
			bbbCreditCardMock.getCreditCardNumber() >> "4111111111111111"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			bbbVerifiedByVisaVOMock.getCardVerNumber() >> "2"
			1 * creditCardToolsMock.validateSecurityCode(bbbVerifiedByVisaVOMock.getCardVerNumber(), bbbCreditCardMock.getCreditCardType()) >> 0
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
			//validateGiftCard Private Method Coverage
			testObj.setValueLinkGiftCardUtil(valueLinkGiftCardUtilMock)
			testObj.setGiftCardProcessor(giftCardProcessorMock)
			testObj.getSiteIdFromManager() >> "BedBathUS"
			bbbOrderMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "2323"
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			giftCardProcessorMock.getBalance("gc2322", "gc1", bbbOrderMock.getId(), bbbGiftCardMock.getId(),testObj.getSiteIdFromManager()) >> giftCardBeanInfoMock
			giftCardBeanInfoMock.setStatus(FALSE)
			giftCardBeanInfoMock.setBalance("2")
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * testObj.logInfo('START: PRE-Committing order [B223233]', null)
			1 * testObj.logInfo('START: getGiftCardBalance during preCommitOrder', null)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error: err_sdd_preview_error', null)
			1 * bbbCreditCardMock.setCardVerificationNumber('2')
		}
	
	def "preCommitOrder. This TC is when channel is not defined, BBBSystemException throws in preCommitOrder "(){
		given:
			spyofPreCommitOrder()
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
			//validateGiftCard Private Method Coverage
			testObj.setValueLinkGiftCardUtil(valueLinkGiftCardUtilMock)
			testObj.setGiftCardProcessor(giftCardProcessorMock)
			testObj.getSiteIdFromManager() >> "BedBathUS"
			bbbOrderMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "2323"
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			giftCardProcessorMock.getBalance("gc2322", "gc1", bbbOrderMock.getId(), bbbGiftCardMock.getId(),testObj.getSiteIdFromManager()) >> giftCardBeanInfoMock
			giftCardBeanInfoMock.setStatus(TRUE)
			giftCardBeanInfoMock.setBalance("-1")
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * messageHandlerMock.getErrMsg('err_giftcard_getbalance', 'EN', null)
			1 * testObj.logInfo('START: PRE-Committing order [B223233]', null)
			1 * testObj.logInfo('START: getGiftCardBalance during preCommitOrder', null)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error: err_sdd_preview_error', null)
			1 * testObj.logError('BBBSystemException in preCommitOrder',_)
		}
	
	def "preCommitOrder. This TC is when channel is MobileWeb, BBBSystemException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [bbbCreditCardMock]
			bbbCreditCardMock.getCardVerificationNumber() >> ""
			bbbCreditCardMock.getCreditCardNumber() >> "4111111111111111"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			testObj.setCardVerNumber("1")
			1 * creditCardToolsMock.validateSecurityCode(testObj.getCardVerNumber(), bbbCreditCardMock.getCreditCardType()) >> 1
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> true
			bbbOrderMock.getSiteId() >> "BedBathUS"
			1 * inventoryManagerMock.getVDCProductAvailability(bbbOrderMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId(), bbbCommerceItemMock.getQuantity(), BBBCoreConstants.CACHE_DISABLED) >> {throw new InventoryException("Mock for InventoryException")}
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCVV', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_sdd_preview_error', null, null)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error', null)
			1 * testObj.logDebug('checkout_1006: Error Occured while committing an order:', _)
			1 * testObj.logError('checkout_1005: Error Occured while committing an order:', null)
			1 * testObj.logDebug('adding form exception: 230', null)
			1 * testObj.logError('BBBSystemException in preCommitOrder', _)
			1 * testObj.logDebug('CVV number of CC is not set. User is using express Checkout. \nSetting the CVV number into PG, CVV number entered in Review Page is - 1', null)
		}
	
	def "preCommitOrder. This TC is when channel is MobileApp, BBBSystemException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [bbbCreditCardMock]
			bbbCreditCardMock.getCardVerificationNumber() >> "2323"
			bbbCreditCardMock.getCreditCardNumber() >> "4111111111111111"
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * messageHandlerMock.getErrMsg('err_sdd_preview_error', null, null)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error', null)
			1 * testObj.logError('BBBSystemException in preCommitOrder', _)
		}
	
	def "preCommitOrder. This TC is when channel is desktop, BBBSystemException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [bbbCreditCardMock]
			bbbCreditCardMock.getCardVerificationNumber() >> ""
			bbbCreditCardMock.getCreditCardNumber() >> ""
			
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error: err_sdd_preview_error', null)
			1 * testObj.logError('BBBSystemException in preCommitOrder', _)
			
		}
	
	def "preCommitOrder. This TC is when channel is not defined, BBBBusinessException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error: err_sdd_preview_error', null)
			1 * testObj.logError('BBBBusinessException in preCommitOrder', _)
		}
	
	def "preCommitOrder. This TC is when channel is MobileWeb, BBBBusinessException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
			1 * messageHandlerMock.getErrMsg('err_sdd_preview_error', null, null)
			1 * testObj.logError('BBBBusinessException in preCommitOrder', _)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error', null)
		}
	
	def "preCommitOrder. This TC is when channel is MobileApp, BBBBusinessException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
			1 * messageHandlerMock.getErrMsg('err_sdd_preview_error', null, null)
			1 * testObj.logError('BBBBusinessException in preCommitOrder', _)
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error', null)
		}
	
	def "preCommitOrder. This TC is when channel is desktop, BBBBusinessException throws in preCommitOrder"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			1 * sameDayDeliveryManagerMock.checkSBCInventoryForSdd(_,bbbOrderMock,true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
			1 * testObj.logDebug('adding form exception: err_sdd_preview_error: err_sdd_preview_error', null)
			1 * testObj.logError('BBBBusinessException in preCommitOrder', _)
		}
	
	def "preCommitOrder. This TC is when sddEligibleOn is null"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd1212"
			catalogToolsMock.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> [null]
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
		}
	
	def "preCommitOrder. This TC is when sddStoreId is empty"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getSddStoreId() >> ""
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
		}
	
	def "preCommitOrder. This TC is when shipping Method is not SDD"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			bbbOrderMock.getShippingGroups() >> [bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "DDD"
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
		}
	
	def "preCommitOrder. This TC is when shippingGroup is not instance of BBBHardGoodShippingGroup"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			def ShippingGroup shippingGroupMock = Mock() 
			bbbOrderMock.getShippingGroups() >> [shippingGroupMock]
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
		}
	
	def "preCommitOrder. This TC is when shippingGroup is empty"(){
		given:
			spyofPreCommitOrder()
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktop"
			def ShippingGroup shippingGroupMock = Mock()
			bbbOrderMock.getShippingGroups() >> []
			//validateCreditCard Private Method Coverage
			bbbOrderMock.getPaymentGroups() >> [paymentGroupMock]
			//checkInventory Private Method Coverage
			bbbOrderMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> null
			bbbCommerceItemMock.isVdcInd() >> false
			
		when:
			testObj.preCommitOrder(requestMock, responseMock)
		then:
			1 * testObj.isPreCommitChecked()
			1 * vbvSessionBeanMock.getbBBVerifiedByVisaVO()
		}
	
	def "preCommitOrder. This TC is when preCommitChecked is true"() {
		given:
			testObj.setPreCommitChecked(TRUE)
		expect:
			testObj.preCommitOrder(requestMock, responseMock)
	}
	
	public spyofPreCommitOrder(){
		testObj = Spy()
		testObj.setManageLogging(manageLoggingMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setMessageHandler(messageHandlerMock)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setVbvSessionBean(vbvSessionBeanMock)
		testObj.setSameDayDeliveryManager(sameDayDeliveryManagerMock)
		testObj.setCreditCardTools(creditCardToolsMock)
		testObj.setInventoryManager(inventoryManagerMock)
		testObj.setOrder(bbbOrderMock)
		bbbOrderMock.getId() >> "B223233"
	}
	
	////////////////////////////////////////TestCases for preCommitOrder --> ENDS//////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for handleCommitOrder --> STARTS///////////////////////////////////////////////////
	/////////Signature : public boolean handleCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "handleCommitOrder. This TC is the happy flow of handleCommitOrder when validateOrderForPPCall is true"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getCurrentLevel() >> "REVIEW"
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			
			//validateOrderForPPCall Private Method Coverage
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			1 * paypalServiceManagerMock.isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			checkoutStateMock.getCheckoutSuccessURLs() >> checkoutURLs
			checkoutStateMock.getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			testObj.setUserLocale(new Locale("en_US"))
			1 * paypalServiceManagerMock.doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> "token"
			testObj.getPaypalServiceManager().getPayPalRedirectURL() >> "/paypal"
			
		when:
			booleanResult = testObj.handleCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setPayPalSucessURL('/paypaltoken')
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.logDebug('BBBCommitFormHandler ::handleCheckoutWithPaypal method -  start ', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * testObj.logDebug('CommitOrderFormHandler.handleCheckoutWithPaypal() : payPalErrorURL: Scheme://x-channel/store/cart SucessURL: /paypaltoken', null)
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logDebug('BBBCommitFormHandler ::handleCheckoutWithPaypal method - mthod ends ', null)
			1 * testObj.checkFormRedirect('/paypaltoken', 'Scheme://x-channel/store/cart',requestMock,responseMock)
			1 * repeatingRequestMonitorMock.removeRequestEntry('handleCheckoutWithPaypal')
	}
	
	def "handleCommitOrder. This TC is when isPayPalOrder is false in validateOrderForPPCall private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getCurrentLevel() >> "REVIEW"	
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			
			//validateOrderForPPCall Private Method Coverage
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
		when:
			booleanResult = testObj.handleCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			
	}
	
	def "handleCommitOrder. This TC is when currentCheckoutlevel is not (review)"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			checkoutStateMock.getCurrentLevel() >> "Not Review"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			
		when:
			booleanResult = testObj.handleCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * messageHandlerMock.getErrMsg('error_committing_order_invalid_state', 'EN', null)
			1 * testObj.logDebug('adding form exception: error_committing_order_invalid_state', null)
			1 * testObj.checkFormRedirect(null, '/store/atg/rest', requestMock, responseMock)
			1 * testObj.logError('BBBCommitOrderFormHandler.handleCommitOrder :: user has clicked onplace order button but his current Checkout level is Not Review , so redirecting it to /store/atg/rest', null)
			
	}
	
	def "handleCommitOrder. This TC is when BBBSetExpressCheckoutResVO's isErrorExists is false in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getCurrentLevel() >> "REVIEW"
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			
			//validateOrderForPPCall Private Method Coverage
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			1 * paypalServiceManagerMock.isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			checkoutStateMock.getCheckoutSuccessURLs() >> checkoutURLs
			checkoutStateMock.getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			testObj.setUserLocale(new Locale("en_US"))
			def ResponseErrorVO responseErrorVOMock = new ResponseErrorVO()
			def BBBSetExpressCheckoutResVO bBBSetExpressCheckoutResVOMock = new BBBSetExpressCheckoutResVO("error":responseErrorVOMock)
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bBBSetExpressCheckoutResVOMock
			bBBSetExpressCheckoutResVOMock.getToken() >> "token"
			bBBSetExpressCheckoutResVOMock.setErrorStatus(errorStatusMock)
			errorStatusMock.setErrorExists(FALSE)
			testObj.getPaypalServiceManager().getPayPalRedirectURL() >> "/paypal"
			
		when:
			booleanResult = testObj.handleCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE	
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.logDebug('BBBCommitFormHandler ::handleCheckoutWithPaypal method -  start ', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * testObj.logDebug('CommitOrderFormHandler.handleCheckoutWithPaypal() : payPalErrorURL: Scheme://x-channel/store/cart SucessURL: ', null)
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logDebug('BBBCommitFormHandler ::handleCheckoutWithPaypal method - mthod ends ', null)
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock,responseMock)
			1 * repeatingRequestMonitorMock.removeRequestEntry('handleCheckoutWithPaypal')
	}
	
	public handleCommitOrderSpy(){
		testObj = Spy()
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setProfile(userProfileMock)
		testObj.setMessageHandler(messageHandlerMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setOrder(bbbOrderImplMock)
		requestMock.getContextPath() >> "/store"
		checkoutStateMock.getSuccessURL(testObj.getProfile()) >> "/cart"
	}
	
	///////////////////////////////////////////////////TestCases for handleCommitOrder --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for handleSPCommitOrder --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final boolean handleSPCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "handleSPCommitOrder. This TC is when isErrorExists is true in handleCheckoutWithPaypal private Method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> TRUE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			errorStatusMock.getErrorId() >> 10736
			Map<String, String> checkoutFailureURL = new HashMap<String, String>()
			checkoutFailureURL.put("SHIPPING_SINGLE", "/shipping_single.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL 
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.getUserLocale(requestMock,responseMock)
			1 * testObj.commitTransaction(transactionMock)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * repeatingRequestMonitorMock.removeRequestEntry('handleCheckoutWithPaypal')
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * checkoutStateMock.setCurrentLevel('SHIPPING_SINGLE')
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * testObj.logDebug('BBBCartFormHandler.validateOrderForPPCall() | validation failed', null)
			1 * testObj.logError(' Error Msg Recieved while calling paypal service= Error in Paypal Service', null)
			1 * testObj.logDebug('adding form exception: err_paypal_set_express_shipping_error: err_paypal_set_express_shipping_error', null)
			1 * testObj.setTransactionToRollbackOnly()
			1 * testObj.checkFormRedirect(null, '/store/shipping_single.jsp?paypalShipping=true&isFromPreview=true', requestMock,responseMock)
	}
	
	def "handleSPCommitOrder. This TC is when validateOrderForPPCall is false in validateOrderForPPCall private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
	}
	
	def "handleSPCommitOrder. This TC is when BBBSystemException throws in validateOrderForPPCall Private Method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			testObj.commitOrderHandler(requestMock, responseMock) >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.logError('BBBSystemException : Error occured while checking token exipiration status', _)
	}
	
	def "handleSPCommitOrder. This TC is when BBBBusinessException throws in validateOrderForPPCall Private Method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			testObj.commitOrderHandler(requestMock, responseMock) >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.logError('BBBBusinessException : Error occured while checking token exipiration status', _)
	}
	
	
	def "handleSPCommitOrder. This TC is when failureURL equals (atg-rest-ignore-redirect)"() {
		given:
			handleCommitOrderSpy()
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> TRUE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			errorStatusMock.getErrorId() >> 10736
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/storeatg-rest-ignore-redirect')
			1 * testObj.getUserLocale(requestMock,responseMock)
			1 * testObj.commitTransaction(transactionMock)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logError('Error ID: 10736 Error Message: null', null)
			1 * checkoutStateMock.setCurrentLevel('SHIPPING_SINGLE')
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * testObj.logDebug('BBBCartFormHandler.validateOrderForPPCall() | validation failed', null)
			1 * testObj.logError(' Error Msg Recieved while calling paypal service= Error in Paypal Service', null)
			1 * testObj.logDebug('adding form exception: err_paypal_set_express_shipping_error: err_paypal_set_express_shipping_error', null)
			2 * messageHandlerMock.getErrMsg('err_paypal_set_express_shipping_error', 'EN', null)
			1 * testObj.setTransactionToRollbackOnly()
			1 * testObj.checkFormRedirect(null, null, requestMock,responseMock)
	}
	
	def "handleSPCommitOrder. This TC is when failureURL is empty"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> ""
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> null
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> TRUE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			errorStatusMock.getErrorId() >> 10736
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store')
			1 * testObj.getUserLocale(requestMock,responseMock)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logError('Error ID: 10736 Error Message: null', null)
			1 * checkoutStateMock.setCurrentLevel('SHIPPING_SINGLE')
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * testObj.logError(' Error Msg Recieved while calling paypal service= Error in Paypal Service', null)
			2 * messageHandlerMock.getErrMsg('err_paypal_set_express_shipping_error', 'EN', null)
			1 * testObj.setTransactionToRollbackOnly()
			1 * testObj.checkFormRedirect(null, null, requestMock,responseMock)
			
	}
	
	def "handleSPCommitOrder. This TC is when ErrorId is not (10736)"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> TRUE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			errorStatusMock.getErrorId() >> 20786
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			testObj.getErrorMap() == [err_cart_paypal_set_express_service:null]
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart',requestMock,responseMock)
			3 * messageHandlerMock.getErrMsg('err_cart_paypal_set_express_service', 'EN', null)
			1 * testObj.logDebug('BBBCartFormHandler.validateOrderForPPCall() | validation failed', null)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.logError('Error ID: 20786 Error Message: null', null)
			1 * testObj.logDebug('adding form exception: err_cart_paypal_set_express_service', null)
	}
	
	def "handleSPCommitOrder. This TC is when doSetExpressCheckOut returns null"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			1 * testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> null
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock, responseMock)
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.getUserLocale(requestMock, responseMock)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
				
	}
	
	def "handleSPCommitOrder. This TC is when errorExists returns false in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> FALSE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock, responseMock)
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.getUserLocale(requestMock, responseMock)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
				
	}
	
	def "handleSPCommitOrder. This TC is when BBBSystemException throws in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> null
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * messageHandlerMock.getErrMsg('err_cart_paypal_set_express_service', 'EN', null)
			1 * testObj.logError('BBBSystemException occured in CommitOrderFormHandler.handleCheckoutWithPaypal :', _)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock, responseMock)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.setTransactionToRollbackOnly()
	}
	
	def "handleSPCommitOrder. This TC is when BBBSystemException and SystemException throws in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * messageHandlerMock.getErrMsg('err_cart_paypal_set_express_service', 'EN', null)
			1 * testObj.logError('BBBSystemException occured in CommitOrderFormHandler.handleCheckoutWithPaypal :', _)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock, responseMock)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logError('Error occured while rollback the transaction during CommitOrderFormHandler.handleCheckoutWithPaypal call', _)
	}
	
	def "handleSPCommitOrder. This TC is when BBBBusinessException throws in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * messageHandlerMock.getErrMsg('err_cart_paypal_set_express_service', 'EN', null)
			1 * testObj.logError('BBBBusinessException occured in CommitOrderFormHandler.handleCheckoutWithPaypal :', _)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock, responseMock)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.setTransactionToRollbackOnly()
	}
	
	def "handleSPCommitOrder. This TC is when BBBBusinessException and SystemException throws in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> TRUE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> transactionMock
			testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * messageHandlerMock.getErrMsg('err_cart_paypal_set_express_service', 'EN', null)
			1 * testObj.logError('BBBBusinessException occured in CommitOrderFormHandler.handleCheckoutWithPaypal :', _)
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock, responseMock)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Sending isCart() as true as we need to perform validations again and create Billing Address', null)
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logInfo('CommitOrderFormHandler.handleCheckoutWithPaypal() :  Request is from Place Order Page as token got expired', null)
			1 * testObj.logError('Error occured while rollback the transaction during CommitOrderFormHandler.handleCheckoutWithPaypal call', _)
	}
	
	def "handleSPCommitOrder. This TC is when SystemException throws in handleCheckoutWithPaypal private method"() {
		given:
			handleCommitOrderSpy()
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.ensureTransaction() >> null
			1 * testObj.getPaypalServiceManager().doSetExpressCheckOut(bbbOrderImplMock,"Scheme://x-channel/store/cart","Scheme://x-channel/store/valid", testObj.getPayPalSessionBean(), testObj.getUserProfile()) >> bbbSetExpressCheckoutResVOMock
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> TRUE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			errorStatusMock.getErrorId() >> 10736
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.logError('Error occured while rollback the transaction during CommitOrderFormHandler.handleCheckoutWithPaypal call', _)
			1 * messageHandlerMock.getErrMsg('err_cart_paypal_set_express_service', 'EN', null)
			1 * testObj.logDebug('adding form exception: err_cart_paypal_set_express_service', null)
			
	}
	
	def "handleSPCommitOrder. This TC is when isUniqueRequestEntry is false"() {
		given:
			handleCommitOrderSpy()
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			
			//validateOrderForPPCall private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.getPaypalServiceManager().isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
			//handleCheckoutWithPaypal Private Method Coverage
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCheckoutWithPaypal") >> FALSE
			testObj.setUserProfile(userProfileMock)
			requestMock.getScheme() >> "Scheme"
			requestMock.getHeader(BBBCoreConstants.HOST) >> "x-channel"
			requestMock.getContextPath() >> "/store"
			Map<String, String> checkoutURLs = new HashMap<String, String>()
			checkoutURLs.put("CART", "/cart")
			checkoutURLs.put("VALIDATION","/valid")
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			testObj.getCheckoutState().getCheckoutSuccessURLs() >> checkoutURLs
			bbbSetExpressCheckoutResVOMock.getToken() >> ""
			bbbSetExpressCheckoutResVOMock.getErrorStatus() >> errorStatusMock
			errorStatusMock.isErrorExists() >> TRUE
			errorStatusMock.getErrorMessage() >> "Error in Paypal Service"
			errorStatusMock.getErrorId() >> 10736
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
			
		when:
			booleanResult = testObj.handleSPCommitOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.logDebug('BBBCartFormHandler.validateOrderForPPCall() | validation failed', null)
			1 * testObj.logDebug('BBBCommitFormHandler ::handleCheckoutWithPaypal method -  start ', null)
			1 * testObj.logDebug('BBBCommitFormHandler ::handleCheckoutWithPaypal method - mthod ends ', null)
			1 * testObj.checkFormRedirect(null, 'Scheme://x-channel/store/cart', requestMock , responseMock)
			
			
	}
	
	///////////////////////////////////////////////////TestCases for handleSPCommitOrder --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for handleVerifiedByVisaLookup --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final boolean handleVerifiedByVisaLookup(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "handleVerifiedByVisaLookup. This TC is when isOrderNotValidForVBV is true and returns handleCommitOrder method"() {
		given:
			verifiedByVisaSpy()
			testObj.setDcPrefix("DC1")
			testObj.setOrderManager(orderManagerMock)
			testObj.handleCommitOrder(requestMock, responseMock) >> TRUE
			checkoutStateMock.spcEligible(bbbOrderImplMock , false) >> false
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> "TBS_BedBathUS"
			userProfileMock.isTransient() >> TRUE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["true"]
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> TRUE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> "10.84.3.82"
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.setIdGenerator(idGeneratorMock)
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "BOPUS_ONLY"
			
			//addBopusOrderNumber Private Method Coverage
			testObj.setBopusOrderPrefixKey("bopusOrderPrefixKey")
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLPDS232332"
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.logDebug('Affiliate cookie value is:affiliateId', null)
			1 * testObj.logInfo('is transaction enable : true', null)
			1 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB232325323', null)
			1 * testObj.logInfo('BOPIS ORDER number generated : OLPDS232332', null)
			1 * testObj.logInfo('For order ID : BBB232325323 is paypal order : false is bopus order : true is vbv flag on : true', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logInfo('IPHeader value is :10.84.3.82', null)
			1 * testObj.isSpcForRecognizedUser(requestMock)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * bbbOrderImplMock.setPropertyValue('userIP', '10.84.3.82')
			1 * bbbOrderImplMock.setPropertyValue('affiliate', 'affiliateId')
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * bbbOrderImplMock.setPropertyValue('userIP',_)
			2 * requestMock.getHeader('X-bbb-channel')
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * requestMock.getHeader('Traffic_OS')
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbOrderImplMock.setPropertyValue('substatus', 'DUMMY_RESTORE_INVENTORY')
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrint')
			1 * sessionBeanMock.setSinglePageCheckout(false) 
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', 'OLPDS232332')
			1 * sessionMock.getAttribute('sessionBean')
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when isSinglePageCheckout is true and returns handleSPCommitOrder method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setDcPrefix("DC1")
			checkoutStateMock.spcEligible(bbbOrderImplMock , false) >> true
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","OnlineOrderPrefix_TBS_BuyBuyBaby":"XXX","bopusOrderPrefixKey_TBS_BuyBuyBaby":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> "TBS_BuyBuyBaby"
			userProfileMock.isTransient() >> FALSE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> "fingerPrintId"
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> TRUE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> ""
			testObj.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS) >> ["wc_referrer_pattern": "/home"]
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> ""
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["false"]
			
			//handleSPCommitOrder bcoz it is final method
			requestMock.getContextPath() >> "/store"
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			checkoutStateMock.getSuccessURL(testObj.getProfile()) >> "/cart"
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.logDebug('Affiliate cookie value is:', null)
			1 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * testObj.logInfo('For order ID : BBB232325323 is paypal order : true is bopus order : false is vbv flag on : false', null)
			1 * testObj.setCommitOrderSuccessURL('/store/cart')
			1 * testObj.logInfo('IPHeader value is :', null)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS :  for OrderID : BBB232325323', null)
			1 * testObj.logError('DS OrderId is generated Blank', null)
			1 * testObj.logInfo('BOPIS ORDER number generated : OLP', null)
			1 * testObj.logInfo('ONLINE ORDER number generated : XXX', null)
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * testObj.logError('DS OrderId is generated Blank')
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', 'OLP')
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * paypalServiceManagerMock.isTokenExpired(payPalSessionBeanMock,bbbOrderImplMock)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrintId')
			1 * sessionBeanMock.setSinglePageCheckout(true) 
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * bbbOrderImplMock.setPropertyValue('affiliate', '/home')
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', 'XXX')
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbOrderImplMock.setPropertyValue('substatus', 'DUMMY_IGNORE_INVENTORY')
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when isPaymentProgressStep is true and isSinglePageCheckout is true"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setDcPrefix("DC1")
			requestMock.getContextPath() >> "/store"
			testObj.setVbvSPCAuthErrorUrl("/checkout/checkout_single.jsp?#vbvMsg")
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"","OnlineOrderPrefix_TBS_BedBathCanada":"XXX"]
			requestMock.getCookieParameter("refId") >> "bp"
			testObj.getCurrentSiteId() >> "TBS_BedBathCanada"
			userProfileMock.isTransient() >> FALSE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> ""
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS) >> ["bp_referrer_pattern": "/registry"]
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> ""
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "ONLINE_ONLY"
			
			//addOnlineOrderNumber Private Method Coverage
			testObj.setOnlineOrderPrefixKey("OnlineOrderPrefix")
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
			//isPaymentProgressStep Private Method Coverage
			requestMock.getSession().getAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS) >> true 
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logDebug('Affiliate cookie value is:', null)
			1 * testObj.logDebug('adding form exception: err_vbv_refresh_back_error', null)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS :  for OrderID : BBB232325323', null)
			1 * testObj.checkFormRedirect(null, '/store/checkout/checkout_single.jsp?#vbvMsg',requestMock,responseMock)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logInfo('ONLINE ORDER number generated : XXX', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', 'XXX')
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * requestMock.getParameter('atg.formHandlerUseForwards')
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			1 * bbbOrderImplMock.setPropertyValue('affiliate', '/registry')
			/*1 * sessionBeanMock.isSinglePageCheckout()*/
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * messageHandlerMock.getErrMsg('err_vbv_refresh_back_error', 'EN', null)
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when isPaymentProgressStep is true and isSinglePageCheckout is false"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			requestMock.getContextPath() >> "/store"
			testObj.setVbvAuthErrorUrl("/checkout/payment/billing_payment.jsp")
			testObj.setDcPrefix("DC1")
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			Map<String, String> configMap = new HashMap<String, String>() 
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
			requestMock.getCookieParameter("refId") >> "wp"
			testObj.getCurrentSiteId() >> "BedBathUS"
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			1 * profileToolsMock.getItemFromEmail("john@gmail.com") >> repositoryItemMock
			1 * requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> "223221"
			repositoryItemMock.getRepositoryId() >> "223221"
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> ""
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			testObj.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS) >> ["bp_referrer_pattern": "/registry"]
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> ""
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "ONLINE_ONLY"
			
			//addOnlineOrderNumber Private Method Coverage
			testObj.setOnlineOrderPrefixKey("OnlineOrderPrefix")
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
			//isPaymentProgressStep Private Method Coverage
			requestMock.getSession().getAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS) >> true
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logDebug('Affiliate cookie value is:', null)
			1 * testObj.isMobileRequest()
			1 * testObj.logInfo('ONLINE ORDER number generated : XXX', null)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS :  for OrderID : BBB232325323', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * bbbOrderImplMock.setProfileId('223221')
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			/*1 * sessionBeanMock.isSinglePageCheckout()*/
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', 'XXX')
			1 * messageHandlerMock.getErrMsg('err_vbv_refresh_back_error', 'EN', null)
			1 * testObj.checkFormRedirect(null, '/store/checkout/payment/billing_payment.jsp', requestMock, responseMock)
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when formExceptions occurs and isSinglePageCheckout is false"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.getFormExceptions() >> ["",""]
			testObj.setVbvLookupErrorUrl("/checkout/preview/review_cart.jsp")
			requestMock.getContextPath() >> "/store"
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.checkFormRedirect(null, '/store/checkout/preview/review_cart.jsp', requestMock, responseMock)
		/*	1 * sessionBeanMock.isSinglePageCheckout()*/
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when formExceptions occurs and isSinglePageCheckout is true"() {
		given:
			verifiedByVisaSpy()
			/*sessionBeanMock.isSinglePageCheckout() >> TRUE*/
			checkoutStateMock.spcEligible(bbbOrderImplMock , false) >> true
			testObj.getFormExceptions() >> ["",""]
			testObj.setVbvSPCLookupErrorUrl("/checkout/singlePage/preview/review_cart.jsp")
			requestMock.getContextPath() >> "/store"
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			0 * testObj.isSpcForRecognizedUser(requestMock)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * testObj.checkFormRedirect(null, '/store/checkout/singlePage/preview/review_cart.jsp', requestMock, responseMock)
	}
	
	def "handleVerifiedByVisaLookup.This TC is when formExceptions occurs and mobileRequest is true"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.getFormExceptions() >> ["",""]
			testObj.setMobileRequest(TRUE)
			testObj.setVbvAuthErrorUrl("/checkout/payment/billing_payment.jsp")
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			/*1 * sessionBeanMock.isSinglePageCheckout()*/
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * testObj.setVbvAuthErrorUrl('atg-rest-ignore-redirect')
			1 * testObj.checkFormRedirect(null, 'atg-rest-ignore-redirect',requestMock, responseMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when orderId is empty in updateOrderAttributes private Method"() {
		given:
			verifiedByVisaSpy()
			testObj.setDcPrefix("DC1")
			testObj.setOrderManager(orderManagerMock)
			testObj.handleCommitOrder(requestMock, responseMock) >> TRUE
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> "TBS_BedBathUS"
			userProfileMock.isTransient() >> TRUE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["true"]
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> TRUE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> "10.84.3.82"
			bbbOrderImplMock.getId() >> ""
			testObj.setIdGenerator(idGeneratorMock)
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "BOPUS_ONLY"
			
			//addBopusOrderNumber Private Method Coverage
			testObj.setBopusOrderPrefixKey("bopusOrderPrefixKey")
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLPDS232332"
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown NullPointerException
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logDebug('Affiliate cookie value is:affiliateId', null)
			0 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * testObj.isSpcForRecognizedUser(requestMock)
			1 * bbbOrderImplMock.setPropertyValue('userIP', '10.84.3.82')
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * bbbOrderImplMock.setPropertyValue('affiliate', 'affiliateId')
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * sessionMock.getAttribute('sessionBean')
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrint')
			1 * bbbOrderImplMock.setPropertyValue('substatus', 'DUMMY_RESTORE_INVENTORY')
			/*1 * sessionBeanMock.isSinglePageCheckout()*/
	}
	
	def "handleVerifiedByVisaLookup.This TC is when BBBSystemException occurs in updateOrderAttributes private Method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setMobileRequest(TRUE)
			1 * testObj.commitOrderHandler(requestMock, responseMock) >> FALSE
			
			//updateOrderAttributes Private Method Coverage
			1 * requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			Map<String, String> configMap = new HashMap<String, String>()
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
			1 * requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> ""
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			1 * profileToolsMock.getItemFromEmail("john@gmail.com") >> repositoryItemMock
			1 * requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> "3221"
			repositoryItemMock.getRepositoryId() >> "223221"
			testObj.setCookieManager(cookieManagerMock)
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> ""
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			bbbOrderImplMock.getId() >> "BBB232325323"
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> ""
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "ONLINE_ONLY"
			
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> []
				
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
		/*	1 * sessionBeanMock.isSinglePageCheckout()*/
			1 * testObj.logDebug('adding form exception: err_checkout_commitorder_sys_exception: Error Occured while committing an order:', null)
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', null)
			1 * testObj.logError('atg.commerce.order.purchase.PurchaseProcessResources->missingTransactionManager : A TransactionManager is required.')
			1 * testObj.logInfo('For order ID : BBB232325323 is paypal order : false is bopus order : false is vbv flag on : false', null)
			//1 * testObj.checkFormRedirect(null, 'atg-rest-ignore-redirect', requestMock, responseMock)
			//1 * testObj.logDebug('error - redirecting to: atg-rest-ignore-redirect', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * testObj.commitTransaction(null)
			1 * testObj.logInfo('is transaction enable : true', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * bbbOrderImplMock.setProfileId('223221')
			1 * testObj.setCommitOrderErrorURL('atg-rest-ignore-redirect')
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			//1 * testObj.logError('atg.commerce.order.purchase.PurchaseProcessResources->missingTransactionManager : A TransactionManager is required.', null)
			1 * cookieManagerMock.createCookiesForProfile('223221', requestMock, responseMock)
			1 * testObj.logError('checkout_1003: Error Occured while committing an order:', _)
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * testObj.logDebug('Affiliate cookie value is:', null)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when errorExists is false and isSinglePageCheckout is true in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> FALSE
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			requestMock.getContextPath() >> "/store"
			testObj.setVbvSPCLookupSuccessUrl("/checkout/singlePage/preview/cCFrame.jsp")
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> null
			requestMock.getCookieParameter("refId") >> null
			testObj.getCurrentSiteId() >> "BuyBuyBaby"
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> ""
			requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> "23232"
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> "fingerPrintId"
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> ""
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * sessionMock.getAttribute('paymentProgress')
			1 * testObj.logInfo('Calling Centinal Look up API END', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/preview/cCFrame.jsp', null,requestMock,responseMock)
			1 * bbbVerifiedByVisaVOMock.setToken('handleLookup')
			2 * testObj.commitTransaction(transactionMock)
	}

	def "handleVerifiedByVisaLookup. This TC is when errorExists is false and isSinglePageCheckout is false in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> FALSE
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			requestMock.getContextPath() >> "/store"
			testObj.setVbvLookupSuccessUrl("/checkout/preview/cCFrame.jsp")
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> FALSE
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader"]
			requestMock.getCookieParameter("refId") >> null
			testObj.getCurrentSiteId() >> ""
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> ""
			requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> ""
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> "fingerPrintId"
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> ""
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * sessionMock.getAttribute('paymentProgress')
			1 * sessionMock.setAttribute('paymentProgress', true)
			1 * testObj.logInfo('Calling Centinal Look up API END', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			1 * testObj.checkFormRedirect('/store/checkout/preview/cCFrame.jsp', null, requestMock,responseMock)
			1 * bbbVerifiedByVisaVOMock.setToken('handleLookup')
			2 * testObj.commitTransaction(transactionMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when errorExists is false and isMobileRequest is true in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> FALSE
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			testObj.setMobileRequest(TRUE)
			
			//updateOrderAttributes Private Method Coverage
			this.populateUpdateOrderAttributes(requestMock)
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * sessionMock.getAttribute('paymentProgress')
			1 * testObj.logInfo('Calling Centinal Look up API END', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			0 * testObj.checkFormRedirect('/store/checkout/preview/cCFrame.jsp', null, requestMock,responseMock)
			0 * bbbVerifiedByVisaVOMock.setToken('handleLookup')
			2 * testObj.commitTransaction(transactionMock)
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when CommerceException throws in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> {throw new CommerceException("Mock for CommerceException")}
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			testObj.setMobileRequest(TRUE)
			
			//updateOrderAttributes Private Method Coverage
			this.populateUpdateOrderAttributes(requestMock)
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.preCommitOrder(requestMock, responseMock) >> null
			1 * testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			1 * sessionMock.setAttribute('paymentProgress', true)
			1 * testObj.logInfo('Calling Centinal Look up API END', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			2 * testObj.commitTransaction(transactionMock)
			1 * testObj.logError('checkout_1003: Error occured while updating order during Centinel MAPS Lookup call:', _)
			1 * testObj.setTransactionToRollbackOnly()
			1 * testObj.setPreCommitChecked(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when BBBSystemException throws in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			testObj.setMobileRequest(TRUE)
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
			
			//updateOrderAttributes Private Method Coverage
			this.populateUpdateOrderAttributes(requestMock)
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.preCommitOrder(requestMock, responseMock) >> {throw new ServletException("Mock for ServletException")}
			1 * sessionMock.setAttribute('paymentProgress', true)
			1 * testObj.logInfo('Calling Centinal Look up API END', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			2 * testObj.commitTransaction(transactionMock)
			1 * testObj.logError('Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Lookup call', _)
			1 * testObj.logError('Error occured while rollback the transaction during Centinel MAPS Lookup call', _)
			1 * testObj.logError('Exception occurred while preCommiting mobileOrderjavax.servlet.ServletException: Mock for ServletException', null)
			0 * testObj.setPreCommitChecked(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when BBBBusinessException throws in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			testObj.setMobileRequest(TRUE)
			
			//updateOrderAttributes Private Method Coverage
			this.populateUpdateOrderAttributes(requestMock)
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.preCommitOrder(requestMock, responseMock) >> {throw new IOException("Mock for IOException")}
			1 * sessionMock.setAttribute('paymentProgress', true)
			1 * testObj.logInfo('Calling Centinal Look up API END', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			2 * testObj.commitTransaction(transactionMock)
			1 * testObj.logError('Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Lookup call', _)
			1 * testObj.logError('Exception occurred while preCommiting mobileOrderjava.io.IOException: Mock for IOException', null)
			0 * testObj.setPreCommitChecked(true)
			1 * testObj.setTransactionToRollbackOnly()
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when errorExists is true and isSinglePageCheckout is true in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> FALSE
			testObj.ensureTransaction() >> transactionMock
			testObj.setMobileRequest(FALSE)
			
			//updateOrderAttributes Private Method Coverage
			this.populateUpdateOrderAttributes(requestMock)
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
			//handleSPCommitOrder method bcoz it is final
			requestMock.getContextPath() >> "/store"
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			checkoutStateMock.getSuccessURL(testObj.getProfile()) >> "/cart"
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrintId')
			1 * testObj.getVbvONOFFConfigKey()
			1 * sessionMock.getAttribute('paymentProgress')
			2 * testObj.commitTransaction(transactionMock)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', 'OLPDS232332')
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB232325323', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when errorExists is true and isSinglePageCheckout is false in handleVerifiedByVisaLookup method"() {
		given:
			verifiedByVisaSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setDcPrefix("DC1")
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> FALSE
			testObj.ensureTransaction() >> transactionMock
			testObj.setMobileRequest(FALSE)
			
			//updateOrderAttributes Private Method Coverage
			this.populateUpdateOrderAttributes(requestMock)
			
			this.populateOrderNumber()
			//isOrderNotValidForVBV Private Method Coverage
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.handleCommitOrder(requestMock, responseMock) >> TRUE
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrintId')
			1 * testObj.getVbvONOFFConfigKey()
			1 * sessionMock.getAttribute('paymentProgress')
			2 * testObj.commitTransaction(transactionMock)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', 'OLPDS232332')
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB232325323', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			
	}
	
	def "handleVerifiedByVisaLookup. This TC is when BBBSystemException throws in getVBVOnOffFlag private Method"() {
		given:
			verifiedByVisaSpy()
			testObj.setDcPrefix("DC1")
			testObj.setOrderManager(orderManagerMock)
			testObj.handleCommitOrder(requestMock, responseMock) >> FALSE
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> "TBS_BedBathUS"
			userProfileMock.isTransient() >> TRUE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["true"]
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> TRUE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> "10.84.3.82"
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.setIdGenerator(idGeneratorMock)
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "BOPUS_ONLY"
			
			//addBopusOrderNumber Private Method Coverage
			testObj.setBopusOrderPrefixKey("bopusOrderPrefixKey")
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLPDS232332"
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logError('System Exception occured while fetching key(vbvONOFF) from FlagDrivenFunctions', _)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB232325323', null)
			1 * testObj.logDebug('Affiliate cookie value is:affiliateId', null)
			1 * testObj.logInfo('BOPIS ORDER number generated : OLPDS232332', null)
			1 * testObj.isSpcForRecognizedUser(requestMock)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when BBBBusinessException throws in getVBVOnOffFlag private Method"() {
		given:
			verifiedByVisaSpy()
			testObj.setDcPrefix("DC1")
			testObj.setOrderManager(orderManagerMock)
			testObj.handleCommitOrder(requestMock, responseMock) >> FALSE
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> "TBS_BedBathUS"
			userProfileMock.isTransient() >> TRUE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["true"]
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> TRUE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> "10.84.3.82"
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.setIdGenerator(idGeneratorMock)
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "BOPUS_ONLY"
			
			//addBopusOrderNumber Private Method Coverage
			testObj.setBopusOrderPrefixKey("bopusOrderPrefixKey")
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLPDS232332"
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logError('Business Exception occured while fetching key(vbvONOFF) from FlagDrivenFunctions', _)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB232325323', null)
			1 * testObj.logDebug('Affiliate cookie value is:affiliateId', null)
			1 * testObj.logInfo('BOPIS ORDER number generated : OLPDS232332', null)
			1 * testObj.isSpcForRecognizedUser(requestMock)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when getAllValuesForKey returns null in getVBVOnOffFlag private Method"() {
		given:
			verifiedByVisaSpy()
			testObj.setDcPrefix("DC1")
			testObj.setOrderManager(orderManagerMock)
			testObj.handleCommitOrder(requestMock, responseMock) >> FALSE
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> ""
			userProfileMock.isTransient() >> FALSE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["true"]
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> "10.84.3.82"
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.setIdGenerator(idGeneratorMock)
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			this.populateOrderNumber()
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> null
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logDebug('Affiliate cookie value is:affiliateId', null)
			1 * testObj.isSpcForRecognizedUser(requestMock)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "handleVerifiedByVisaLookup. This TC is when PaymentProgress is false in isPaymentProgressStep private method"() {
		given:
			verifiedByVisaSpy()
			testObj.setDcPrefix("DC1")
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			1 * checkoutManagerMock.vbvCentinelAddLookupRequest(requestMock, bbbOrderImplMock,_,_, testObj.getVbvSessionBean()) >> TRUE
			testObj.ensureTransaction() >> transactionMock
			testObj.setCardVerNumber("123")
			1 * checkoutManagerMock.vbvCentinelSendLookupRequest(_, testObj.getCardVerNumber(), bbbOrderImplMock, TRUE, testObj.getOrderManager(), testObj.getVbvSessionBean()) >> FALSE
			testObj.getVbvSessionBean().getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			requestMock.getContextPath() >> "/store"
			testObj.setVbvSPCLookupSuccessUrl("/checkout/singlePage/preview/cCFrame.jsp")
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> ""
			userProfileMock.isTransient() >> FALSE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["true"]
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> "10.84.3.82"
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.setIdGenerator(idGeneratorMock)
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			this.populateOrderNumber()
			
			//getVBVOnOffFlag Private Method Coverage
			testObj.setVbvONOFFConfigKey("vbvONOFF")
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, testObj.getVbvONOFFConfigKey()) >> ["true"]
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			
			//isPaymentProgressStep Private Method Coverage
			requestMock.getSession().getAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS) >> FALSE
			
		when:
			booleanResult = testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logDebug('Affiliate cookie value is:affiliateId', null)
			1 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * testObj.logInfo('ONLINE ORDER number generated : XXX', null)
			1 * testObj.logInfo('Calling Centinal Look up API Start', null)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB232325323', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', 'XXXDS232332')
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * requestMock.getHeader('Traffic_OS')
			1 * testObj.isSpcForRecognizedUser(requestMock)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	private populateOrderNumber() {
		bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "HYBRID"
		
		//addOnlineOrderNumber Private Method Coverage
		testObj.setOnlineOrderPrefixKey("OnlineOrderPrefix")
		bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"

		//addBopusOrderNumber Private Method Coverage
		testObj.setBopusOrderPrefixKey("bopusOrderPrefixKey")
		bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP"
	}
	
	private populateUpdateOrderAttributes(DynamoHttpServletRequest requestMock) {
		requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
		Map<String, String> configMap = new HashMap<String, String>()
		testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
		requestMock.getCookieParameter("refId") >> null
		testObj.getCurrentSiteId() >> "BedBathUS"
		userProfileMock.isTransient() >> TRUE
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> ""
		requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> ""
		testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
		requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> "fingerPrintId"
		requestMock.getRemoteAddr() >> "192.168.1.254"
		bbbOrderImplMock.isDummyOrder() >> FALSE
		requestMock.getSession().getId() >> "fingerPrint"
		requestMock.getHeader("IPHeader") >> ""
		bbbOrderImplMock.getId() >> "BBB232325323"
		testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
	}
	
	
	private verifiedByVisaSpy() {
		testObj = Spy()
		testObj.setVbvSessionBean(vbvSessionBeanMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
		bbbOrderImplMock.getPaymentGroups() >> [paymentGroupMock]
		testObj.setOrderManager(orderManagerMock)
		testObj.setUserProfile(userProfileMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setIdGenerator(idGeneratorMock)
		testObj.setProfile(userProfileMock)
		testObj.setMessageHandler(messageHandlerMock)
		testObj.setProfileTools(profileToolsMock)
	}
	
	///////////////////////////////////////////////////TestCases for handleVerifiedByVisaLookup --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for handlePlaceCurrentOrder --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final boolean handlePlaceCurrentOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "handlePlaceCurrentOrder.This TC is the happy flow of handlePlaceCurrentOrder"() {
		given:
			placeCurrentOrderSpy()
			bbbOrderImplMock.isPayPalOrder() >> TRUE
			paypalServiceManagerMock.isTokenExpired(testObj.getPayPalSessionBean(), bbbOrderImplMock) >> TRUE
			
		when:
			booleanResult = testObj.handlePlaceCurrentOrder(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				  'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * payPalSessionBeanMock.setGetExpCheckoutResponse(null)
			1 * testObj.setPaypalTokenExpired(true)
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				  'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * payPalSessionBeanMock.setValidateOrderAddress(false)
			
	}
	
	def "handlePlaceCurrentOrder.This TC is when isPayPalOrder is false"() {
		given:
			placeCurrentOrderSpy()
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			testObj.setTransactionManager(transactionManagerMock)
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.getFormExceptions() >> ["",""]
			testObj.setVbvLookupErrorUrl("/checkout/preview/review_cart.jsp")
			requestMock.getContextPath() >> "/store"
			
		when:
			booleanResult = testObj.handlePlaceCurrentOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			1 * testObj.logInfo('is transaction enable : true', null)
			1 * testObj.setMobileRequest(true)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * testObj.setVbvAuthErrorUrl('atg-rest-ignore-redirect')
			1 * checkoutStateMock.setCheckoutSuccessURLs(['Success':'url'])
			1 * checkoutStateMock.setCheckoutFailureURLs(['failure':'url'])
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				  'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			
	}
	
	def "handlePlaceCurrentOrder.This TC is when validateOrderForPPCall is false"() {
		given:
			placeCurrentOrderSpy()
			bbbOrderImplMock.isPayPalOrder() >> [TRUE,FALSE]
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.getFormExceptions() >> ["",""]
			testObj.setVbvLookupErrorUrl("/checkout/preview/review_cart.jsp")
			requestMock.getContextPath() >> "/store"
			
		when:
			booleanResult = testObj.handlePlaceCurrentOrder(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			testObj.handleVerifiedByVisaLookup(requestMock, responseMock)
			1 * testObj.setMobileRequest(true)
			1 * testObj.logInfo('is transaction enable : true', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(null)
			1 * testObj.setVbvAuthErrorUrl('atg-rest-ignore-redirect')
			1 * checkoutStateMock.setCheckoutSuccessURLs(['Success':'url'])
			1 * checkoutStateMock.setCheckoutFailureURLs(['failure':'url'])
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				  'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			
	}

	private placeCurrentOrderSpy() {
		testObj = Spy()
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		checkoutFailureURL.put("failure","url")
		checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
		checkoutSuccessURL.put("Success","url")
		checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
		testObj.setPayPalSessionBean(payPalSessionBeanMock)
		testObj.setPaypalServiceManager(paypalServiceManagerMock)
		testObj.setVbvSessionBean(vbvSessionBeanMock)
		testObj.setSessionBean(sessionBeanMock)
		bbbOrderImplMock.getPaymentGroups() >> [paymentGroupMock]
	}
	
	///////////////////////////////////////////////////TestCases for handlePlaceCurrentOrder --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for handlePlaceCurrentOrder --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final String getReferrer(final String configKey) ///////////

	def "getReferrer.This TC is the happy flow of getReferrer"() {
		given:
			String configKey = "wc_referrer_pattern"

		when:
			def results = testObj.getReferrer(configKey)
			
		then:
			results == "/home"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS) >> ["wc_referrer_pattern": "/home"]
			
	}
	
	def "getReferrer.This TC is when BBBBusinessException throws"() {
		given:
			String configKey = "wc_referrer_pattern"
			testObj.getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.REFERRAl_CONTROLS) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			def results = testObj.getReferrer(configKey)
			
		then:
			results == null

	}
	
	///////////////////////////////////////////////////TestCases for handlePlaceCurrentOrder --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for afterSet --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final boolean afterSet(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "afterSet.This TC is the happy flow of afterSet"() {
		given:
			testObj = Spy()
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset End : Override the After set Method', null)
			1 * testObj.afterSetSuperCall(requestMock, responseMock) >> TRUE
			
	}
	
	def "afterSet.This TC is when Exception occurs in afterSet super call"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			bbbOrderImplMock.getState() >> 2000
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * testObj.logInfo('BBBCommitOrderFormHandler-Afterset : Roll Back Gift Card transation', null)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
			
	}
	
	def "afterSet.This TC is when isLoggingInfo is false"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			testObj.setLoggingInfo(FALSE)
			bbbOrderImplMock.getState() >> 2000
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
	}
	
	def "afterSet.This TC is when setCartFormHandlerLogging is false"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			manageLoggingMock.setCartFormHandlerLogging(FALSE)
			bbbOrderImplMock.getState() >> 2000
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
	}
	
	def "afterSet.This TC is when getTransactionSuccess is true in redeemRollbackGiftCardAmount private method"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			testObj.setLoggingInfo(FALSE)
			bbbOrderImplMock.getState() >> 100
			
			//redeemRollbackGiftCardAmount Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock,paymentGroupMock]
			def GiftCardStatus giftCardStatusMock1 = Mock()
			bbbGiftCardMock.getAuthorizationStatus() >> [giftCardStatusMock, giftCardStatusMock1]
			giftCardStatusMock.getTransactionSuccess() >> TRUE
			giftCardStatusMock1.getTransactionSuccess() >> FALSE
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			valueLinkGiftCardUtilMock.getAmountInVLFormat(_) >> 000
			bbbOrderImplMock.getId() >> "2232"
			bbbGiftCardMock.getId() >> "g22323"
			testObj.getSiteIdFromManager() >> "BedBathUS"
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * giftCardProcessorMock.redeemRollback('gc2322', 'gc1', '000', '2232', 'g22323', 'BedBathUS')
			1 * testObj.logDebug('TransactionSuccess of GC is TRUE, RedeemRollback will happen', null)
			1 * testObj.logDebug('Rolling back gift card [gc2322] for order [2232]', null)
			1 * testObj.logDebug('TransactionSuccess of GC is FALSE, RedeemRollback will not be performed', null)
			1 * testObj.logDebug('GiftCard payment group found during redeemRollbak, total gift card: 1', null)
			1 * testObj.logInfo('START: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logInfo('END: Rolling back Gift Card redeemed amount', null)
	}
	
	def "afterSet.This TC is when getAuthorizationStatus is not defined and empty in redeemRollbackGiftCardAmount private method"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			testObj.setLoggingInfo(FALSE)
			testObj.getFormExceptions() >> ["",""]
			bbbOrderImplMock.getState() >> 2000
			
			//redeemRollbackGiftCardAmount Private Method Coverage
			def BBBGiftCard bbbGiftCardMock1 = Mock()
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock,bbbGiftCardMock1]
			bbbGiftCardMock.getAuthorizationStatus() >> []
			bbbGiftCardMock1.getAuthorizationStatus() >> null
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logDebug('GiftCard payment group found during redeemRollbak, total gift card: 2', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * testObj.logInfo('START: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logInfo('END: Rolling back Gift Card redeemed amount', null)
	}
	
	def "afterSet.This TC is when paymentGroup is not instance of BBBGiftCard in redeemRollbackGiftCardAmount private method"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			testObj.setLoggingInfo(FALSE)
			testObj.getFormExceptions() >> ["",""]
			bbbOrderImplMock.getState() >> 2000
			//redeemRollbackGiftCardAmount Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [paymentGroupMock]
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logDebug('NO GIFTCARD Paymentgroup found during method redeemRollbackGiftCardAmount', null)
			1 * testObj.logInfo('START: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logInfo('END: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
	}
	
	def "afterSet.This TC is when BBBSystemException throws in redeemRollbackGiftCardAmount private method"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			testObj.setLoggingInfo(FALSE)
			bbbOrderImplMock.getState() >> 100
			
			//redeemRollbackGiftCardAmount Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock,paymentGroupMock]
			def GiftCardStatus giftCardStatusMock1 = Mock()
			bbbGiftCardMock.getAuthorizationStatus() >> [giftCardStatusMock, giftCardStatusMock1]
			giftCardStatusMock.getTransactionSuccess() >> TRUE
			giftCardStatusMock1.getTransactionSuccess() >> FALSE
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			valueLinkGiftCardUtilMock.getAmountInVLFormat(_) >> 000
			bbbOrderImplMock.getId() >> "2232"
			bbbGiftCardMock.getId() >> "g22323"
			testObj.getSiteIdFromManager() >> "BedBathUS"
			1 * giftCardProcessorMock.redeemRollback('gc2322', 'gc1', '000', '2232', 'g22323', 'BedBathUS') >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logDebug('GiftCard payment group found during redeemRollbak, total gift card: 1', null)
			1 * testObj.logDebug('TransactionSuccess of GC is TRUE, RedeemRollback will happen', null)
			1 * testObj.logDebug('TransactionSuccess of GC is FALSE, RedeemRollback will not be performed', null)
			1 * testObj.logDebug('Rolling back gift card [gc2322] for order [2232]', null)
			1 * testObj.logInfo('START: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logInfo('END: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * testObj.logError('checkout_1007: Error while rolling back gift card [gc2322] for order [2232]', _)
	}
	
	def "afterSet.This TC is when BBBBusinessException throws in redeemRollbackGiftCardAmount private method"() {
		given:
			afterSetSpy()
			testObj.afterSetSuperCall(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			testObj.setLoggingInfo(FALSE)
			bbbOrderImplMock.getState() >> 100
			
			//redeemRollbackGiftCardAmount Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock,paymentGroupMock]
			def GiftCardStatus giftCardStatusMock1 = Mock()
			bbbGiftCardMock.getAuthorizationStatus() >> [giftCardStatusMock, giftCardStatusMock1]
			giftCardStatusMock.getTransactionSuccess() >> TRUE
			giftCardStatusMock1.getTransactionSuccess() >> FALSE
			bbbGiftCardMock.getPropertyValue("cardNumber") >> "gc2322"
			bbbGiftCardMock.getPropertyValue("pin") >> "gc1"
			valueLinkGiftCardUtilMock.getAmountInVLFormat(_) >> 000
			bbbOrderImplMock.getId() >> "2232"
			bbbGiftCardMock.getId() >> "g22323"
			testObj.getSiteIdFromManager() >> "BedBathUS"
			giftCardProcessorMock.redeemRollback('gc2322', 'gc1', '000', '2232', 'g22323', 'BedBathUS') >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			booleanResult = testObj.afterSet(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			thrown DropletFormException
			1 * testObj.logDebug('BBBCommitOrderFormHandler-Afterset Start : Override the After set Method', null)
			1 * testObj.logDebug('GiftCard payment group found during redeemRollbak, total gift card: 1', null)
			1 * testObj.logDebug('TransactionSuccess of GC is TRUE, RedeemRollback will happen', null)
			1 * testObj.logDebug('TransactionSuccess of GC is FALSE, RedeemRollback will not be performed', null)
			1 * testObj.logDebug('Rolling back gift card [gc2322] for order [2232]', null)
			1 * testObj.logInfo('START: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logInfo('END: Rolling back Gift Card redeemed amount', null)
			1 * testObj.logError('Exception Occurred in afterSet:', _)
			1 * testObj.logError('checkout_1007: Error while rolling back gift card [gc2322] for order [2232]', _)
	}
	
	private afterSetSpy() {
		testObj = Spy()
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setOrder(bbbOrderImplMock)
		testObj.setValueLinkGiftCardUtil(valueLinkGiftCardUtilMock)
		testObj.setGiftCardProcessor(giftCardProcessorMock)
	}
	
	///////////////////////////////////////////////////TestCases for afterSet --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for handleVerifiedByVisaAuth --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final boolean handleVerifiedByVisaAuth(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "handleVerifiedByVisaAuth.This TC is when isMobileRequest is false and isSinglePageCheckout is true"() {
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(FALSE)
			testObj.setVbvSessionBean(vbvSessionBeanMock)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
			requestMock.getContextPath() >> "/store"
			testObj.setVbvSPCAuthErrorUrl("/checkout/checkout_single.jsp?#vbvMsg")
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.checkFormRedirect(null, '/store/checkout/checkout_single.jsp?#vbvMsg',requestMock, responseMock)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: ', null)
			1 * testObj.addFormException(*_)
			/*1 * sessionBeanMock.isSinglePageCheckout()*/
	}
	
	def "handleVerifiedByVisaAuth.This TC is when isMobileRequest is false and isSinglePageCheckout is false"() {
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(FALSE)
			testObj.setVbvSessionBean(vbvSessionBeanMock)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
			requestMock.getContextPath() >> "/store"
			testObj.setVbvAuthErrorUrl("/checkout/payment/billing_payment.jsp")
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.checkFormRedirect(null, '/store/checkout/payment/billing_payment.jsp',requestMock, responseMock)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: ', null)
			1 * testObj.addFormException(*_)
			1 * sessionBeanMock.isSinglePageCheckout()
	}
	
	def "handleVerifiedByVisaAuth.This TC is when isMobileRequest is true"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(TRUE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			1 * testObj.commitTransaction(transactionMock)
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutSuccessURLs([:])
			1 * checkoutStateMock.setCheckoutFailureURLs([:])
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * sessionMock.removeAttribute('authResponse')
			1 * requestMock.setParameter('itemLevelExpDelivery', 'itemLevelExpDeliveryReq')
			1 * testObj.logInfo('Calling Centinal Authenticate API Start', null)
			1 * testObj.logInfo('Calling Centinal Authenticate API End', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock)	
	}
	
	def "handleVerifiedByVisaAuth.This TC is when isMobileRequest is false and authenticateResponse is not defined"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes(null)
			testObj.setMobileRequest(FALSE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "cCAuthenticate"
			bbbVerifiedByVisaVOMock.getMessage() >> "Droplet Exception Occurs"
			requestMock.getContextPath() >> "/store"
			testObj.setVbvAuthErrorUrl("/checkout/payment/billing_payment.jsp")
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.checkFormRedirect(null, '/store/checkout/payment/billing_payment.jsp', requestMock,responseMock)
			1 * testObj.commitTransaction(transactionMock)
			1 * sessionBeanMock.isSinglePageCheckout()
			1 * sessionMock.removeAttribute('paymentProgress')
			2 * messageHandlerMock.getErrMsg('err_vbv_no_auth_response', 'EN', null)
			0 * testObj.logInfo('Calling Centinal Authenticate API Start', null)
			1 * testObj.logInfo('Calling Centinal Authenticate API End', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * bbbVerifiedByVisaVOMock.setMessage(null)
			1 * testObj.logDebug('adding form exception: err_vbv_no_auth_response: Droplet Exception Occurs', null)
			
	}
	
	def "handleVerifiedByVisaAuth.This TC is when authenticateResponse is empty and isSinglePageCheckout is true"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setPaRes("")
			testObj.setMobileRequest(FALSE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "cCAuthenticate"
			bbbVerifiedByVisaVOMock.getMessage() >> "Droplet Exception Occurs"
			requestMock.getContextPath() >> "/store"
			testObj.setVbvSPCAuthErrorUrl("/checkout/checkout_single.jsp?#vbvMsg")
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * bbbVerifiedByVisaVOMock.setMessage(null)
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.checkFormRedirect(null, '/store/checkout/checkout_single.jsp?#vbvMsg', requestMock, responseMock)
			2 * messageHandlerMock.getErrMsg('err_vbv_no_auth_response', 'EN', null)
			1 * testObj.logInfo('Calling Centinal Authenticate API End', null)
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * testObj.logDebug('adding form exception: err_vbv_no_auth_response: Droplet Exception Occurs', null)
	}
	
	def "handleVerifiedByVisaAuth.This TC is when BBBSystemException thrown in handleVerifiedByVisaAuth"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(TRUE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * checkoutStateMock.setCheckoutFailureURLs([:])
			1 * checkoutStateMock.setCheckoutSuccessURLs([:])
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logInfo('Calling Centinal Authenticate API Start', null)
			1 * testObj.logInfo('Calling Centinal Authenticate API End', null)
			1 * sessionBeanMock.isSinglePageCheckout()
			1 * sessionMock.removeAttribute('authResponse')
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * testObj.logError('Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Authenticate call.', _)
			1 * testObj.setTransactionToRollbackOnly()
			
	}
	
	def "handleVerifiedByVisaAuth.This TC is when CommerceException thrown in handleVerifiedByVisaAuth"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(TRUE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock) >> {throw new CommerceException("Mock for CommerceException")}
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect',
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * checkoutStateMock.setCheckoutFailureURLs([:])
			1 * checkoutStateMock.setCheckoutSuccessURLs([:])
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logInfo('Calling Centinal Authenticate API Start', null)
			1 * testObj.logInfo('Calling Centinal Authenticate API End', null)
			1 * sessionBeanMock.isSinglePageCheckout()
			1 * sessionMock.removeAttribute('authResponse')
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * testObj.logError('Error occured while updating order during auth call for VBV', _)
			1 * testObj.logError('Error occured while rollback the transaction during VBV Auth call', _)
	}
	
	def "handleVerifiedByVisaAuth.This TC is when BBBBusinessException thrown in handleVerifiedByVisaAuth"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(TRUE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
			def BBBBusinessException bbbBusinessExceptionMock = new BBBBusinessException("")
			bbbBusinessExceptionMock.setErrorCode(BBBVerifiedByVisaConstants.AuthenticateFailure)
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock) >> {throw bbbBusinessExceptionMock}
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * sessionMock.removeAttribute('authResponse')
			1 * testObj.logInfo('Calling Centinal Authenticate API Start', null)
			1 * testObj.logInfo('Calling Centinal Authenticate API End', null)
			1 * checkoutStateMock.setCheckoutSuccessURLs([:])
			1 * checkoutStateMock.setCheckoutFailureURLs([:])
			1 * testObj.logDebug('adding form exception: err_vbv_authenticate_failure', null)
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect', 
				'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SP_ERROR':'atg-rest-ignore-redirect',
				 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect', 'ERROR':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 
				 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * sessionBeanMock.isSinglePageCheckout()
			1 * bbbVerifiedByVisaVOMock.getMessage()
			1 * testObj.commitTransaction(transactionMock)

	}
	
	def "handleVerifiedByVisaAuth.This TC is when BBBBusinessException thrown with errorcode 'err_failure' "() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(TRUE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "authenticate"
			def BBBBusinessException bbbBusinessExceptionMock = new BBBBusinessException("")
			bbbBusinessExceptionMock.setErrorCode("err_failure")
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock) >> {throw bbbBusinessExceptionMock}
					
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * testObj.logError('Error occured while fetching key from ThirdPartyURLs during Centinel MAPS Authenticate call.', _)
			1 * checkoutStateMock.setCheckoutSuccessURLs([:])
			1 * checkoutStateMock.setCheckoutFailureURLs([:])
	}
	
	def "handleVerifiedByVisaAuth.This TC is when errorExists,mobileRequest is false and isSinglePageCheckout is true"() {
		given:
			verifiedByVisaAuthSpy()
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(FALSE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "cCAuthenticate"
			
			//handleSPCommitOrder bcoz it is final method
			requestMock.getContextPath() >> "/store"
			checkoutStateMock.getFailureURL() >> "/atg/rest"
			checkoutStateMock.getSuccessURL(testObj.getProfile()) >> "/cart"
			testObj.setPaypalServiceManager(paypalServiceManagerMock)
			testObj.setPayPalSessionBean(payPalSessionBeanMock)
			bbbOrderImplMock.isPayPalOrder() >> FALSE
			testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == TRUE
			1 * checkoutStateMock.setCurrentLevel('SP_REVIEW')
			0 * testObj.isSpcForRecognizedUser(requestMock)
			1 * sessionMock.removeAttribute('authResponse')
			1 * testObj.setCommitOrderErrorURL('/store/atg/rest')
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * testObj.commitTransaction(transactionMock)
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock)
	}
	
	def "handleVerifiedByVisaAuth.This TC is when errorExists,mobileRequest is false and isSinglePageCheckout is false"() {
		given:
			verifiedByVisaAuthSpy()
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setPaRes("authenticateResponse")
			testObj.setMobileRequest(FALSE)
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bbbVerifiedByVisaVOMock
			bbbVerifiedByVisaVOMock.getToken() >> "cCAuthenticate"
			testObj.commitOrderHandler(requestMock, responseMock) >> TRUE
			
		when:
			booleanResult = testObj.handleVerifiedByVisaAuth(requestMock, responseMock)
			
		then:
			booleanResult == FALSE
			1 * sessionMock.removeAttribute('authResponse')
			1 * vbvSessionBeanMock.setbBBVerifiedByVisaVO(bbbVerifiedByVisaVOMock)
			1 * testObj.commitTransaction(transactionMock)
			1 * checkoutManagerMock.vbvCentinelAuthenticateRequest(bbbOrderImplMock, bbbVerifiedByVisaVOMock, 'authenticateResponse', _, messageHandlerMock, orderManagerMock, vbvSessionBeanMock)
	}

	private verifiedByVisaAuthSpy() {
		testObj = Spy()
		testObj.setSessionBean(sessionBeanMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setVbvSessionBean(vbvSessionBeanMock)
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
		checkoutStateMock.getCheckoutSuccessURLs() >> checkoutSuccessURL
		testObj.setOrder(bbbOrderImplMock)
		testObj.ensureTransaction() >> transactionMock
		testObj.setCheckoutManager(checkoutManagerMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setMessageHandler(messageHandlerMock)
	}
	
	///////////////////////////////////////////////////TestCases for handleVerifiedByVisaAuth --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for postCommitOrder --> STARTS///////////////////////////////////////////////////
	/////////Signature : public void postCommitOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "postCommitOrder.This TC is the happy flow when channel value is MobileWeb"() {
		given:
			postCommitOrderSpy()
			testObj.getFormExceptions() >> null
			bbbOrderImplMock.getId() >> "BBB12345"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> "6357237529"
			1 * bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> "3572375297"
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
			1 * testObj.getCurrentSiteId() >> "BedBathUS"
			1 * testObj.logPersistedInfo('save_user_location', 'BBB12345', null, '6357237529', '3572375297', 'BedBathUS', 'MobileWeb', null, _)
			1 * testObj.sendLogEvent(_)
			1 * sessionMock.setAttribute('couponMailId', '')
	}
	
	
	def "postCommitOrder.This TC is the happy flow when channel value is DesktopWeb"() {
		given:
			postCommitOrderSpy()
			testObj.getFormExceptions() >> ["",""]
			bbbOrderImplMock.getId() >> "BBB12345"
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
			1 * testObj.getCurrentSiteId() >> "BedBathUS"
			1 * bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> "6357237529"
			1 * bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> "3572375297"
	}
	
	def "postCommitOrder.This TC is when getFormExceptions is not null and longitude is empty"() {
		given:
			postCommitOrderSpy()
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getCurrentSiteId() >> "BedBathUS"
			def Cookie cookieMock1 = Mock()
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "BBB"
			psManagerMock.getLbCookieNme() >> "BBB"
			psManagerMock.getLvCookieNme() >> "BBB"
			responseMock.getResponse() >> responseMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> ""
			bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> "3572375297"
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
			1 * psManagerMock.getLastBoughtCookie(bbbOrderImplMock, cookieMock, cookieMock, requestMock, responseMock) >> cookieMock1
			1 * testObj.logDebug('Last Bought Cookie and Last Viewed Cookie found', null)
			1 * testObj.logDebug('Generate the Last Bought Cookie', null)
			1 * sessionMock.setAttribute('couponMailId', '')
			0 * testObj.logPersistedInfo('save_user_location', 'BBB12345', null, '', '3572375297', 'BedBathUS', 'MobileWeb', null, _)
			
	}
	
	def "postCommitOrder.This TC is when flagLB is true and flagLV is false"() {
		given:
			postCommitOrderSpy()
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getCurrentSiteId() >> "BedBathUS"
			def Cookie cookieMock1 = Mock()
			def Cookie cookieMock2 = Mock()
			requestMock.getCookies() >> [cookieMock,cookieMock1]
			cookieMock.getName() >> "BBB"
			cookieMock1.getName() >> "BedBathandBeyond"
			psManagerMock.getLbCookieNme() >> "BBB"
			psManagerMock.getLvCookieNme() >> "SSS"
			responseMock.getResponse() >> responseMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> "6357237529"
			bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> ""
			1 * psManagerMock.getLastBoughtCookie(bbbOrderImplMock, null, cookieMock, requestMock, responseMock) >> cookieMock2
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
			1 * testObj.logDebug('Generate the Last Bought Cookie', null)
			1 * sessionMock.setAttribute('couponMailId', '')
	}
	
	def "postCommitOrder.This TC is when both flagLB is false and flagLV is true"() {
		given:
			postCommitOrderSpy()
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getCurrentSiteId() >> "BedBathUS"
			def Cookie cookieMock1 = Mock()
			requestMock.getCookies() >> [cookieMock]
			cookieMock.getName() >> "BBB"
			psManagerMock.getLbCookieNme() >> "SSS"
			psManagerMock.getLvCookieNme() >> "BBB"
			responseMock.getResponse() >> responseMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> "6357237529"
			bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> ""
			1 * psManagerMock.getLastBoughtCookie(bbbOrderImplMock, cookieMock, null, requestMock, responseMock) >> cookieMock1
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
			1 * testObj.logDebug('Generate the Last Bought Cookie', null)
			1 * sessionMock.setAttribute('couponMailId', '')
	}
	
	def "postCommitOrder.This TC is when cookies is null"() {
		given:
			postCommitOrderSpy()
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getCurrentSiteId() >> "BedBathUS"
			def Cookie cookieMock1 = Mock()
			requestMock.getCookies() >> null
			responseMock.getResponse() >> responseMock
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> "6357237529"
			bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> ""
			1 * psManagerMock.getLastBoughtCookie(bbbOrderImplMock, null, null, requestMock, responseMock) >> cookieMock1
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
			1 * testObj.logDebug('Generate the Last Bought Cookie', null)
			1 * sessionMock.setAttribute('couponMailId', '')

	}
	
	def "postCommitOrder.This TC is when getOrder is null"() {
		given:
			postCommitOrderSpy()
			testObj.getOrder() >> null
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getCurrentSiteId() >> "BedBathUS"
			bbbGenericSessionComponentMock.getDeviceLocationLongitude() >> "6357237529"
			bbbGenericSessionComponentMock.getDeviceLocationLatitude() >> ""
		
		when:
			testObj.postCommitOrder(requestMock, responseMock)
			
		then:
			pricingModelHolderMock.getItemPricingModels() == []
			pricingModelHolderMock.getOrderPricingModels() == []
			pricingModelHolderMock.getShippingPricingModels() == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) == []
			testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) == []
			1 * pricingModelHolderMock.initializeShippingPricingModels()
			1 * pricingModelHolderMock.initializeOrderPricingModels()
			1 * pricingModelHolderMock.initializeItemPricingModels()
	}

	private postCommitOrderSpy() {
		testObj = Spy()
		testObj.setOrder(bbbOrderImplMock)
		testObj.setProfile(userProfileMock)
		testObj.setPsManager(psManagerMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setUserPricingModels(pricingModelHolderMock)
		testObj.setBbbGenericSessionComponent(bbbGenericSessionComponentMock)
		testObj.getProfile().getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [availablePromotionsMock,selectedPromotionsMock]
		testObj.getProfile().getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> [selectedPromotionsMock,activePromotionsMock]
		testObj.getProfile().getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) >> [activePromotionsMock,availablePromotionsMock]
		pricingModelHolderMock.getItemPricingModels() >> [repositoryItemMock]
		pricingModelHolderMock.getOrderPricingModels() >> [repositoryItemMock]
		pricingModelHolderMock.getShippingPricingModels() >> [repositoryItemMock]
	}
	
	
	///////////////////////////////////////////////////TestCases for postCommitOrder --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for commitOrder --> STARTS///////////////////////////////////////////////////
	/////////Signature : public final void commitOrder(final Order pOrder, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "commitOrder.This TC is the happy flow of commitOrder"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			1 * orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "DUMMY_RESTORE_INVENTORY"
			testObj.getShoppingCart() >> orderHolderMock
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.processPipelineErrors(pipelineResultMock)
			1 * requestMock.getHeader('origin_of_traffic')
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, 'DUMMY_RESTORE_INVENTORY')
			1 * orderHolderMock.setCurrent(null)
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * orderHolderMock.setLast(bbbOrderImplMock)
	}
	
	def "commitOrder.This TC is when order substatus is DUMMY_IGNORE_INVENTORY"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> ""
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			requestMock.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC) >> "MobileWeb"
			sessionBeanMock.isSinglePageCheckout() >> FALSE
			testObj.isSpcForRecognizedUser(requestMock) >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.getProcessOrderMap(new Locale("en_US"), null) >> new HashMap()
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			testObj.processPipelineErrors(pipelineResultMock) >> TRUE
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "DUMMY_IGNORE_INVENTORY"
			testObj.getShoppingCart() >> null
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER:  - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE:  - OLP12345', null)
			1 * testObj.setSalesChannel('MOBILE')
			0 * orderHolderMock.setCurrent(null)
			0 * checkoutStateMock.setCurrentLevel('CART')
			0 * orderHolderMock.setLast(bbbOrderImplMock)
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, 'DUMMY_IGNORE_INVENTORY')
			
	}
	
	def "commitOrder.This TC is when order substatus is UNSUBMITTED"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			requestMock.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC) >> "MobileApp"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.getProcessOrderMap(new Locale("en_US"), null) >> new HashMap()
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			testObj.getShoppingCart() >> orderHolderMock
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.processPipelineErrors(pipelineResultMock)
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, 'UNSUBMITTED')
			1 * orderHolderMock.setCurrent(null)
			1 * testObj.setSalesChannel('MOBIAPP')
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * orderHolderMock.setLast(bbbOrderImplMock)
	}
	
	def "commitOrder.This TC is when BBBSystemException throws in updateOrderSubstatus"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.getShoppingCart() >> null
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logError('BBBSystemException while updating the substatus', _)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.processPipelineErrors(pipelineResultMock)
	}
	
	def "commitOrder.This TC is when BBBBusinessException throws in updateOrderAttributes Private Method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			bbbOrderImplMock.getOnlineOrderNumber() >> ""
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> ""
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			testObj.getShoppingCart() >> null
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> "affiliateId"
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> ["TRUE_IP_HEADER":"IPHeader","bopusOrderPrefixKey_TBS_BedBathUS":"OLP"]
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> "TBS_BedBathUS"
			userProfileMock.isTransient() >> TRUE
			testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.processPipelineErrors(pipelineResultMock)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE:  - ', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER:  - ', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logDebug('adding form exception: err_checkout_commitorder_bsys_exception: Error Occured while committing an order:', null)
			1 * testObj.logError('checkout_1003: Error Occured while committing an order:', _)
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, 'UNSUBMITTED')
	}
	
	def "commitOrder.This TC is when ServletException throws in updateOrderAttributes Private Method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			bbbOrderImplMock.getOnlineOrderNumber() >> ""
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> ""
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			testObj.getShoppingCart() >> null
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			Map<String, String> configMap = new HashMap<String, String>()
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> ""
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			1 * profileToolsMock.getItemFromEmail("john@gmail.com") >> repositoryItemMock
			requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> "3221"
			repositoryItemMock.getRepositoryId() >> "223221"
			1 * cookieManagerMock.createCookiesForProfile("223221", requestMock, responseMock) >> {throw new ServletException("Mock for ServletException")}
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER:  - ', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE:  - ', null)
			1 * testObj.logDebug('Affiliate cookie value is:', null)
			1 * testObj.processPipelineErrors(pipelineResultMock)
			1 * testObj.logError('err_create_cookie: Error occured while updating order before commit:', _)
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, 'UNSUBMITTED')
			1 * testObj.getProcessOrderMap(new Locale("en_US"))
	}
	
	def "commitOrder.This TC is when IOException throws in updateOrderAttributes Private Method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			bbbOrderImplMock.getOnlineOrderNumber() >> ""
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> ""
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			testObj.getShoppingCart() >> null
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			Map<String, String> configMap = new HashMap<String, String>()
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
			requestMock.getCookieParameter("refId") >> "wc"
			testObj.getCurrentSiteId() >> ""
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			1 * profileToolsMock.getItemFromEmail("john@gmail.com") >> repositoryItemMock
			requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> "3221"
			repositoryItemMock.getRepositoryId() >> "223221"
			1 * cookieManagerMock.createCookiesForProfile("223221", requestMock, responseMock) >> {throw new IOException("Mock for IOException")}
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER:  - ', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE:  - ', null)
			1 * testObj.logDebug('Affiliate cookie value is:', null)
			1 * testObj.processPipelineErrors(pipelineResultMock)
			1 * testObj.logError('err_create_cookie: Error occured while updating order before commit:', _)
			1 * bbbOrderToolsMock.updateOrderSubstatus(bbbOrderImplMock, 'UNSUBMITTED')
			1 * testObj.getProcessOrderMap(new Locale("en_US"))
	}
	
	def "commitOrder.This TC is when CommerceException throws in updateOrderAttributes Private Method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			bbbOrderImplMock.getOnlineOrderNumber() >> ""
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> ""
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			testObj.getShoppingCart() >> null
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			Map<String, String> configMap = new HashMap<String, String>()
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
			requestMock.getCookieParameter("refId") >> null
			testObj.getCurrentSiteId() >> "BedBathUS"
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> ""
			requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> ""
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> "fingerPrintId"
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> ""
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> "DS232332"
			
			this.populateOrderNumber()
			testObj.getOrderManager().updateOrder(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logError('checkout_1003: Error occured while updating order before commit:', _)
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logInfo('New ONLINE/BOPUS/HYBRID Order Id generated For DS : DS232332 for OrderID : BBB12345', null)
			1 * testObj.logInfo('BOPIS ORDER number generated : ', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER:  - ', null)
			1 * testObj.logInfo('ONLINE ORDER number generated : ', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE:  - ', null)
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', 'OLPDS232332')
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrintId')
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * bbbOrderImplMock.setCreatedByOrderId(_)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', 'XXXDS232332')		
	}
	
	def "commitOrder.This TC is when Exception throws in updateOrderAttributes Private Method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			bbbOrderImplMock.getOnlineOrderNumber() >> ""
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> ""
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			bbbOrderImplMock.getPropertyValue("substatus") >> "UNSUBMITTED"
			testObj.getShoppingCart() >> null
			
			//updateOrderAttributes Private Method Coverage
			requestMock.getCookieParameter(BBBCoreConstants.AFFILIATE_MOM365_COOKIE_KEY) >> ""
			Map<String, String> configMap = new HashMap<String, String>()
			1 * testObj.getCatalogTools().getConfigValueByconfigType(testObj.getCartAndCheckOutConfigType()) >> configMap
			requestMock.getCookieParameter("refId") >> null
			testObj.getCurrentSiteId() >> "BedBathUS"
			userProfileMock.isTransient() >> TRUE
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> ""
			requestMock.getCookieParameter(BBBCoreConstants.DYN_USER_ID) >> ""
			1 * testObj.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_INVENTORY_CHECK) >> ["false"]
			requestMock.getHeader(BBBCoreConstants.TRAFFIC_OS) >> "ANDRIOD"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			requestMock.getHeader(BBBCoreConstants.MOBILE_SESSION_ID) >> "fingerPrintId"
			requestMock.getRemoteAddr() >> "192.168.1.254"
			bbbOrderImplMock.isDummyOrder() >> FALSE
			requestMock.getSession().getId() >> "fingerPrint"
			requestMock.getHeader("IPHeader") >> ""
			bbbOrderImplMock.getId() >> "BBB232325323"
			testObj.getIdGenerator().generateStringId("OrderDS") >> {throw new Exception("Mock for Exception")}
			this.populateOrderNumber()

		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * testObj.logError('Inside Exception: Exception occured while creating Ids for DS Order ID',_)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE:  - ', null)
			1 * testObj.logInfo('START: Update order attributes', null)
			1 * testObj.logDebug('Affiliate cookie value is:', null)
			1 * testObj.logDebug('Generate New Order Numbers for DS', null)
			1 * bbbOrderImplMock.setPropertyValue('onlineOrderNumber', null)
			1 * bbbOrderImplMock.setPropertyValue('deviceFingerprint', 'fingerPrintId')
			1 * bbbOrderImplMock.setPropertyValue('userIP', '192.168.1.254')
			1 * bbbOrderImplMock.setPropertyValue('bopusOrderNumber', null)
			1 * bbbOrderImplMock.setSalesOS('ANDRIOD')
			thrown NullPointerException
	}
	
	def "commitOrder.This TC is when isSinglePageCheckout is true"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			testObj.processPipelineErrors(pipelineResultMock) >> TRUE
			testObj.isTransactionMarkedAsRollBack() >> TRUE
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getState() >> 2000
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logDebug('adding form exception: ERR_CART_GENERIC_ERROR_TRY_LATER', null)
			1 * messageHandlerMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', null, null)
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
	}
	
	def "commitOrder.This TC is when isSinglePageCheckout is false"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			testObj.isSpcForRecognizedUser(requestMock) >> FALSE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> pipelineResultMock
			orderManagerMock.getOrderTools() >> bbbOrderToolsMock
			testObj.processPipelineErrors(pipelineResultMock) >> TRUE
			testObj.isTransactionMarkedAsRollBack() >> TRUE
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getState() >> 2000
			
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logDebug('adding form exception: ERR_CART_GENERIC_ERROR_TRY_LATER', null)
			1 * sessionBeanMock.isSinglePageCheckout()
			1 * messageHandlerMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', null, null)
	}
	
	def "commitOrder.This TC is when CommerceException thrown in commitOrder method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> {throw new CommerceException("Mock for CommerceException")}
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getState() >> 2000
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.processException(_, 'errorCommittingOrder', requestMock,responseMock)
			
	}
	
	def "commitOrder.This TC is when BBBSystemException thrown in commitOrder method"() {
		given:
			commitOrderSpy()
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP12345"
			requestMock.getCookieParameter(BBBCoreConstants.SCHOOL_COOKIE) >> "sch12345"
			sessionBeanMock.isSinglePageCheckout() >> TRUE
			testObj.setUserLocale(new Locale("en_US"))
			orderManagerMock.processOrder(bbbOrderImplMock,testObj.getProcessOrderMap(testObj.getUserLocale())) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.getFormExceptions() >> []
			bbbOrderImplMock.getState() >> 2000
		
		when:
			testObj.commitOrder(bbbOrderImplMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Committing order [BBB12345]', null)
			1 * StateDefinitions.ORDERSTATES.getStateValue(OrderStates.SUBMITTED) == 2000
			1 * testObj.logInfo('ORDER NUMBERS BEFORE: XXX12345 - OLP12345', null)
			1 * testObj.logInfo('ORDER NUMBERS AFTER: XXX12345 - OLP12345', null)
			1 * testObj.processException(_, 'errorCommittingOrder', requestMock,responseMock)
	}
	
	private commitOrderSpy() {
		testObj = Spy()
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrder(bbbOrderImplMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setUserProfile(userProfileMock)
		testObj.setCookieManager(cookieManagerMock)
		testObj.setProfileTools(profileToolsMock)
		testObj.setIdGenerator(idGeneratorMock)
		testObj.setMessageHandler(messageHandlerMock)
	}
	
	///////////////////////////////////////////////////TestCases for commitOrder --> ENDS///////////////////////////////////////////////////

	///////////////////////////////////////////////////TestCases for identifyRedirect --> STARTS///////////////////////////////////////////////////
	/////////Signature : protected final void identifyRedirect(final PipelineResult pResult, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "identifyRedirect.This TC is when errorCode equals '10736'"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			pipelineResultMock.getError("PayPay_Service_Error") >> "10736"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [10736]')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: E12345')
			1 * testObj.setPayPalErrorPage('shipping')
			1 * messageHandlerMock.getErrMsg('err_paypal_set_express_shipping_error', null, null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifyRedirect.This TC is when error_codes_types_one contains errorCode"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "E12345"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: E12345')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.setPayPalErrorPage('billing')
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_one', null, null)
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [E12345]')
			1 * testObj.getFormExceptions()
	}
	
	def "identifyRedirect.This TC is when error_codes_types_one does not contains errorCode"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.setPayPalErrorPage('billing')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: E12345')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: 456E', null)
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_two', null, null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifyRedirect.This TC is when BBBSystemException thrown in identifyRedirect method"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('Error occured while fetching key from WSDL_CONFIG_TYPE', _)
			1 * testObj.setPayPalErrorPage('billing')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: ')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: 456E', null)
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_two', null, null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifyRedirect.This TC is when BBBBusinessException thrown in identifyRedirect method"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('Error occured while fetching key from WSDL_CONFIG_TYPE', _)
			1 * testObj.setPayPalErrorPage('billing')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: ')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: 456E', null)
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_two', null, null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifyRedirect.This TC is errorcode equals '10736' when channel is desktopWeb"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktopWeb"
			pipelineResultMock.getError("PayPay_Service_Error") >> "10736"
			requestMock.getContextPath() >> "/store"
			checkoutFailureURL.put("SHIPPING_SINGLE", "/singlePage.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			2 * testObj.logDebug('Request is from Desktop, redirecting to Error Page', null)
			2 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			2 * checkoutStateMock.setCurrentLevel('SHIPPING_SINGLE')
			2 * testObj.setCommitOrderErrorURL('/store/singlePage.jsp?paypalShipping=true&isFromPreview=true')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [10736]')
			
	}
	
	def "identifyRedirect.This TC is error_codes_types_one contains errorCode when channel is null"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			pipelineResultMock.getError("PayPay_Service_Error") >> "E123"
			requestMock.getContextPath() >> "/store"
			checkoutFailureURL.put("ERROR", "/error.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL
			testObj.getCommitOrderErrorURL() >> "/commitOrder/error"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logError('Request is from Desktop, Error E12345 redirecting to Error Page : /commitOrder/error')
			1 * testObj.logDebug('Request is from Desktop, redirecting to Error Page', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [E123]')
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.setCommitOrderErrorURL('/store/error.jsp?paypalFailOne=true')
	}
	
	def "identifyRedirect.This TC is error_codes_types_one does not contains errorCode when channel is null"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
			requestMock.getContextPath() >> "/store"
			checkoutFailureURL.put("ERROR", "/errorURL.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL
			testObj.getCommitOrderErrorURL() >> "/commitOrder/error"
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp?paypalError=true')
			1 * testObj.logDebug('Request is from Desktop, redirecting to Error Page', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('Request is from Desktop, Error 456Eredirecting to Error Page : /commitOrder/error')
	}
	
	def "identifyRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceAVSFail is TRUE"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> TRUE
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', null, null)
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', 'EN', null)
			1 * testObj.logDebug('adding form exception: 200', null)
			2 * testObj.getFormExceptions()
			testObj.getErrorMap() == [err_checkout_cybersource_error:null]
	}
	
	def "identifyRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceInvalidCVV is TRUE"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceInvalidCVV") >> TRUE
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('adding form exception: 230', null)
			2 * testObj.getFormExceptions()
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', null, null)
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', 'EN', null)
			1 * requestMock.getParameter('cybersourceAVSFail')
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			testObj.getErrorMap() == [err_checkout_cybersource_error:null]
	}
	
	def "identifyRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceInvalidCVV is not TRUE"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> FALSE
			requestMock.getParameter("cybersourceInvalidCVV") >> FALSE
			testObj.getFormExceptions() >> null
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', null, null)
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			testObj.getErrorMap() == [err_checkout_creditcard_error:null]
	}
	
	def "identifyRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceInvalidCVV is empty"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> FALSE
			requestMock.getParameter("cybersourceInvalidCVV") >> ""
			testObj.getFormExceptions() >> null
		
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', null, null)
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			testObj.getErrorMap() == [err_checkout_creditcard_error:null]
	}
	
	def "identifyRedirect.This TC is when failureUrl is not atg-rest-ignore-redirect and getCreditCardInvalidAttempts value equals to maxInvalidCCAttempts"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> TRUE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> ["8"]
			sessionBeanMock.setCreditCardInvalidAttempts(8)

		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('maximum Invalid Auth Attempt reached', null)
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp?cybersourceAVSFail=true')
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp')
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * messageHandlerMock.getErrMsg('err_creditcard_auth_max_invalidattempts', null, null)
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			1 * requestMock.getSession(false)
	}
	
	def "identifyRedirect.This TC is when failureUrl is not atg-rest-ignore-redirect and getCreditCardInvalidAttempts value less than maxInvalidCCAttempts"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceInvalidCVV") >> TRUE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> ["8"]
			sessionBeanMock.setCreditCardInvalidAttempts(5)

		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('Auth failed. Current Invalid attemps:5', null)
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp?cybersourceInvalidCVV=true')
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * requestMock.getParameter('cybersourceAVSFail')
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			sessionBeanMock.getCreditCardInvalidAttempts() == 6
			
	}
	
	def "identifyRedirect.This TC is when failureUrl is not atg-rest-ignore-redirect and cybersourceAVSFail is false"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> FALSE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> []
			sessionBeanMock.setCreditCardInvalidAttempts(5)

		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('maximum Invalid Auth Attempt reached', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			2 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			1 * requestMock.getParameter('cybersourceInvalidCVV')
			1 * messageHandlerMock.getErrMsg('err_creditcard_auth_max_invalidattempts', null, null)
			1 * requestMock.getSession(false)
			1 * checkoutStateMock.setCurrentLevel('CART')
			
	}
	
	def "identifyRedirect.This TC is when NumberFormatException throws in identifyRedirect method"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceInvalidCVV") >> FALSE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> ["true"]
			sessionBeanMock.setCreditCardInvalidAttempts(5)
			//invalidateSession Private Method Coverage
			requestMock.getSession(false) >> sessionMock
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('maximum Invalid Auth Attempt reached', null)
			1 * testObj.logError('Invalid Number format:true', _)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logDebug('session invalidated', null)
			1 * checkoutStateMock.setCurrentLevel('ERROR')
			1 * requestMock.getParameter('cybersourceAVSFail')
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * messageHandlerMock.getErrMsg('err_creditcard_auth_max_invalidattempts', null, null)
			1 * sessionMock.invalidate()
			2 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp')
			
	}
	
	def "identifyRedirect.This TC is when errorKey is FailedPaymentGroupAuth,SameDay_Deliv_Error and channel is MobileWeb"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedPaymentGroupAuth","SameDay_Deliv_Error"]
			pipelineResultMock.getError("FailedPaymentGroupAuth") >> "456E"
			pipelineResultMock.getError("SameDay_Deliv_Error") >> "1456E"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: Error #1 : [SameDay_Deliv_Error] : Description [1456E]')
			1 * testObj.logDebug('Error Key: FailedPaymentGroupAuth', null)
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)', null)
			1 * testObj.logDebug('adding form exception: err_checkout_payment_grp_error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedPaymentGroupAuth] : Description [456E]', null)
			1 * testObj.logDebug('Error Key: SameDay_Deliv_Error', null)
			1 * testObj.logDebug('adding form exception: SameDay_Deliv_Error', null)
			1 * messageHandlerMock.getErrMsg('SameDay_Deliv_Error', null, null)
			1 * messageHandlerMock.getErrMsg('err_checkout_payment_grp_error', null, null)		
	}
	
	def "identifyRedirect.This TC is when errorKey is SameDay_Deliv_Error and channel is MobileApp"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["SameDay_Deliv_Error"]
			pipelineResultMock.getError("SameDay_Deliv_Error") >> "1456E"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: SameDay_Deliv_Error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [SameDay_Deliv_Error] : Description [1456E]')
			1 * testObj.logDebug('adding form exception: SameDay_Deliv_Error', null)
			1 * messageHandlerMock.getErrMsg('SameDay_Deliv_Error', null, null)
	}
	
	def "identifyRedirect.This TC is when errorKey is SameDay_Deliv_Error and channel is null"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["SameDay_Deliv_Error"]
			pipelineResultMock.getError("SameDay_Deliv_Error") >> "1456E"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: Error #0 : [SameDay_Deliv_Error] : Description [1456E]')
			1 * testObj.logDebug('Error Key: SameDay_Deliv_Error', null)
			1 * testObj.logDebug('adding form exception: 1456E: SameDay_Deliv_Error', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
	}
	
	def "identifyRedirect.This TC is when errorKey is SameDay_Deliv_Error and channel is desktopWeb"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["SameDay_Deliv_Error"]
			pipelineResultMock.getError("SameDay_Deliv_Error") >> "1456E"
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktopWeb"
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: 1456E: SameDay_Deliv_Error', null)
			1 * testObj.logDebug('Error Key: SameDay_Deliv_Error', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * testObj.logError('checkout_1009: Error #0 : [SameDay_Deliv_Error] : Description [1456E]')
	}
	
	def "identifyRedirect.This TC is when errorKey is SameDay_Error"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["SameDay_Error"]
			pipelineResultMock.getError("SameDay_Error") >> "1456E"
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('Error Key: SameDay_Error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [SameDay_Error] : Description [1456E]')
			1 * testObj.logDebug('adding form exception: SameDay_Error: SameDay_Error', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
	}
	
	def "identifyRedirect.This TC is when pResult parameter is null"() {
		given:
			identifyRedirectSpy()
			
		when:
			testObj.identifyRedirect(null, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: ERR_CART_GENERIC_ERROR_TRY_LATER', null)
			1 * messageHandlerMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', null, null)
	}
	
	def "identifyRedirect.This TC is when getErrorKeys is empty"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> []
			
		when:
			testObj.identifyRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: ERR_CART_GENERIC_ERROR_TRY_LATER', null)
			1 * messageHandlerMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', null, null)
	}

	private identifyRedirectSpy() {
		testObj = Spy()
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setCartFormHandlerLogging(TRUE)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> TRUE
		testObj.setMessageHandler(messageHandlerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setSessionBean(sessionBeanMock)
	}
	
	///////////////////////////////////////////////////TestCases for identifyRedirect --> ENDS///////////////////////////////////////////////////
	
	///////////////////////////////////////////////////TestCases for identifySPRedirect --> STARTS///////////////////////////////////////////////////
	/////////Signature : protected final void identifySPRedirect(final PipelineResult pResult, final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ///////////
	
	def "identifySPRedirect.This TC is when errorCode equals '10736'"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			pipelineResultMock.getError("PayPay_Service_Error") >> "10736"
			
			
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [10736]')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: E12345')
			1 * testObj.setPayPalErrorPage('SP_CHECKOUT_SINGLE')
			1 * messageHandlerMock.getErrMsg('err_paypal_set_express_shipping_error', null, null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifySPRedirect.This TC is when error_codes_types_one contains errorCode"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "E12345"
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: E12345')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.setPayPalErrorPage('SP_CHECKOUT_SINGLE')
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_one', null, null)
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [E12345]')
			1 * testObj.getFormExceptions()
			
	}
	
	def "identifySPRedirect.This TC is when error_codes_types_one does not contains errorCode"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.setPayPalErrorPage('SP_CHECKOUT_SINGLE')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: E12345')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: 456E', null)
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_two', null, null)
			1 * testObj.getFormExceptions()
			
	}
	
	def "identifySPRedirect.This TC is when BBBSystemException thrown in identifyRedirect method"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('Error occured while fetching key from WSDL_CONFIG_TYPE', _)
			1 * testObj.setPayPalErrorPage('SP_CHECKOUT_SINGLE')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: ')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: 456E', null)
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_two', null, null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifySPRedirect.This TC is when BBBBusinessException thrown in identifyRedirect method"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('Error occured while fetching key from WSDL_CONFIG_TYPE', _)
			1 * testObj.setPayPalErrorPage('SP_CHECKOUT_SINGLE')
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: ')
			1 * testObj.logDebug('Request is from Mobile, adding form exceptions', null)
			1 * testObj.logError('Request is from Mobile, adding form exceptions for error: 456E', null)
			1 * testObj.setPayPalFailure(true)
			1 * messageHandlerMock.getErrMsg('err_checkout_paypal_error_two', null, null)
			1 * testObj.getFormExceptions()
	}
	
	def "identifySPRedirect.This TC is errorcode equals '10736' when channel is desktopWeb"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error","PayPay_Service_Error"]
			catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktopWeb"
			pipelineResultMock.getError("PayPay_Service_Error") >> "10736"
			requestMock.getContextPath() >> "/store"
			checkoutFailureURL.put("SP_CHECKOUT_SINGLE", "/singlePageSP.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [10736]')
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			2 * testObj.setCommitOrderErrorURL('/store/singlePageSP.jsp?paypalShipping=true&isFromPreview=true')
			1 * testObj.logError('checkout_1009: Error #1 : [PayPay_Service_Error] : Description [10736]')
			2 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			2 * testObj.logDebug('Request is from Desktop, redirecting to Error Page', null)
			2 * checkoutStateMock.setCurrentLevel('SP_CHECKOUT_SINGLE')
	}
	
	def "identifySPRedirect.This TC is error_codes_types_one contains errorCode when channel is null"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error"]
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			pipelineResultMock.getError("PayPay_Service_Error") >> "E123"
			requestMock.getContextPath() >> "/store"
			checkoutFailureURL.put("SP_ERROR", "/errorSP.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL
			testObj.getCommitOrderErrorURL() >> "/commitOrder/error"
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [E123]')
			1 * testObj.logDebug('Request is from Desktop, redirecting to Error Page', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logError('Request is from Desktop, Error E12345 redirecting to Error Page : /commitOrder/error')
			1 * testObj.setCommitOrderErrorURL('/store/errorSP.jsp?paypalFailOne=true')
			
	}
	
	
	def "identifySPRedirect.This TC is error_codes_types_one does not contains errorCode when channel is null"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["PayPay_Service_Error"]
			1 * catalogToolsMock.getAllValuesForKey("WSDL_PayPal", "paypalErroCodeWindowOne") >> ["E12345"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
			pipelineResultMock.getError("PayPay_Service_Error") >> "456E"
			requestMock.getContextPath() >> "/store"
			checkoutFailureURL.put("SP_ERROR", "/errorSPUrl.jsp")
			testObj.getCheckoutState().getCheckoutFailureURLs() >> checkoutFailureURL
			testObj.getCommitOrderErrorURL() >> "/commitOrder/error"
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.setCommitOrderErrorURL('/store/errorSPUrl.jsp?paypalError=true')
			1 * testObj.logDebug('Request is from Desktop, redirecting to Error Page', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: PayPay_Service_Error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [PayPay_Service_Error] : Description [456E]')
			1 * testObj.logError('Request is from Desktop, Error 456Eredirecting to Error Page : /commitOrder/error')
			
	}
	
	def "identifySPRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceAVSFail is TRUE"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> TRUE
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', null, null)
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', 'EN', null)
			2 * testObj.getFormExceptions()
			1 * testObj.logDebug('adding form exception: 200', null)
			testObj.getErrorMap() == [err_checkout_cybersource_error:null]
	}
	
	def "identifySPRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceInvalidCVV is TRUE"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceInvalidCVV") >> TRUE
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * testObj.logDebug('adding form exception: 230', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', null, null)
			1 * messageHandlerMock.getErrMsg('err_checkout_cybersource_error', 'EN', null)
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			1 * requestMock.getParameter('cybersourceAVSFail')
			
	}
	
	def "identifySPRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceInvalidCVV is not TRUE"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> FALSE
			requestMock.getParameter("cybersourceInvalidCVV") >> FALSE
			testObj.getFormExceptions() >> null
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', null, null)
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			testObj.getErrorMap() == [err_checkout_creditcard_error:null]
	}
	
	def "identifySPRedirect.This TC is when errorKey startswith FailedCreditCardAuth and cybersourceInvalidCVV is empty"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> FALSE
			requestMock.getParameter("cybersourceInvalidCVV") >> ""
			testObj.getFormExceptions() >> null
		
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_error', null, null)
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			testObj.getErrorMap() == [err_checkout_creditcard_error:null]
	}

	def "identifySPRedirect.This TC is when failureUrl is not atg-rest-ignore-redirect and getCreditCardInvalidAttempts value equals to maxInvalidCCAttempts"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> TRUE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			1 * catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> ["8"]
			sessionBeanMock.setCreditCardInvalidAttempts(8)

		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('maximum Invalid Auth Attempt reached', null)
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp?cybersourceAVSFail=true')
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp')
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * messageHandlerMock.getErrMsg('err_creditcard_auth_max_invalidattempts', null, null)
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			1 * requestMock.getSession(false)
	}
	
	def "identifySPRedirect.This TC is when failureUrl is not atg-rest-ignore-redirect and getCreditCardInvalidAttempts value less than maxInvalidCCAttempts"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceInvalidCVV") >> TRUE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			1 * catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> ["8"]
			sessionBeanMock.setCreditCardInvalidAttempts(5)

		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('Auth failed. Current Invalid attemps:5', null)
			1 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp?cybersourceInvalidCVV=true')
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * requestMock.getParameter('cybersourceAVSFail')
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			sessionBeanMock.getCreditCardInvalidAttempts() == 6		
	}
	
	def "identifySPRedirect.This TC is when failureUrl is not atg-rest-ignore-redirect and cybersourceAVSFail is false"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceAVSFail") >> FALSE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			1 * catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> []
			sessionBeanMock.setCreditCardInvalidAttempts(5)

		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('maximum Invalid Auth Attempt reached', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			2 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			1 * requestMock.getParameter('cybersourceInvalidCVV')
			1 * messageHandlerMock.getErrMsg('err_creditcard_auth_max_invalidattempts', null, null)
			1 * requestMock.getSession(false)
			1 * checkoutStateMock.setCurrentLevel('CART')
	}
	
	def "identifySPRedirect.This TC is when NumberFormatException throws in identifyRedirect method"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedCreditCardAuth"]
			pipelineResultMock.getError("FailedCreditCardAuth") >> "456E"
			checkoutStateMock.getFailureURL() >> "/errorURL.jsp"
			testObj.getTransactionManager() >> transactionManagerMock
			requestMock.getParameter("cybersourceInvalidCVV") >> FALSE
			requestMock.getContextPath() >> "/store"
			testObj.setCartAndCheckOutConfigType("CartAndCheckoutKeys")
			1 * catalogToolsMock.getAllValuesForKey("CartAndCheckoutKeys", "CARD_AUTH_MAX_ATTEMPTS") >> ["true"]
			sessionBeanMock.setCreditCardInvalidAttempts(5)
			//invalidateSession Private Method Coverage
			requestMock.getSession(false) >> sessionMock
			
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('maximum Invalid Auth Attempt reached', null)
			1 * testObj.logError('Invalid Number format:true', _)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedCreditCardAuth] : Description [456E]')
			1 * testObj.logDebug('Error Key: FailedCreditCardAuth', null)
			1 * testObj.logDebug('session invalidated', null)
			1 * checkoutStateMock.setCurrentLevel('SP_ERROR')
			1 * requestMock.getParameter('cybersourceAVSFail')
			1 * checkoutStateMock.setCurrentLevel('CART')
			1 * messageHandlerMock.getErrMsg('err_creditcard_auth_max_invalidattempts', null, null)
			1 * sessionMock.invalidate()
			2 * testObj.setCommitOrderErrorURL('/store/errorURL.jsp')
	}
	
	def "identifySPRedirect.This TC is when errorKey is FailedPaymentGroupAuth"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["FailedPaymentGroupAuth"]
			pipelineResultMock.getError("FailedPaymentGroupAuth") >> "456E"
			
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logError('checkout_1009: Error #0 : [FailedPaymentGroupAuth] : Description [456E]', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)')
			1 * testObj.logDebug('Error Key: FailedPaymentGroupAuth', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_payment_grp_error', null, null)
	}
	
	def "identifySPRedirect.This TC is when errorKey is SameDay_Deliv_Error"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["SameDay_Deliv_Error"]
			pipelineResultMock.getError("SameDay_Deliv_Error") >> "456E"
			
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: 456E: SameDay_Deliv_Error', null)
			1 * testObj.logError('checkout_1009: There are [1] pipeline error(s)', null)
			1 * testObj.logDebug('Error Key: SameDay_Deliv_Error', null)
			1 * testObj.logError('checkout_1009: Error #0 : [SameDay_Deliv_Error] : Description [456E]')
	}
	
	def "identifySPRedirect.This TC is when errorKey is SameDay_Error"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> ["SameDay_Deliv_Error","SameDay_Error"]
			pipelineResultMock.getError("SameDay_Deliv_Error") >> "456E"
			pipelineResultMock.getError("SameDay_Error") >> "678E"
			
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: 456E: SameDay_Deliv_Error', null)
			1 * testObj.logDebug('adding form exception: SameDay_Error: SameDay_Error', null)
			1 * testObj.logError('checkout_1009: Error #1 : [SameDay_Error] : Description [678E]')
			1 * testObj.logDebug('Error Key: SameDay_Deliv_Error', null)
			1 * testObj.logError('checkout_1009: There are [2] pipeline error(s)')
			1 * testObj.logError('checkout_1009: Error #0 : [SameDay_Deliv_Error] : Description [456E]')
			1 * testObj.logDebug('Error Key: SameDay_Error', null)
	}
	
	def "identifySPRedirect.This TC is when pResult parameter is null"() {
		given:
			identifyRedirectSpy()
			
		when:
			testObj.identifySPRedirect(null, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: ERR_CART_GENERIC_ERROR_TRY_LATER', null)
			1 * messageHandlerMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', null, null)
	}
	
	def "identifySPRedirect.This TC is when getErrorKeys is empty"() {
		given:
			identifyRedirectSpy()
			pipelineResultMock.getErrorKeys() >> []
			
		when:
			testObj.identifySPRedirect(pipelineResultMock, requestMock, responseMock)
		
		then:
			1 * testObj.logInfo('START: Processing pipeline errors', null)
			1 * testObj.logInfo('END: Processing pipeline errors', null)
			1 * testObj.logDebug('adding form exception: ERR_CART_GENERIC_ERROR_TRY_LATER', null)
			1 * messageHandlerMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', null, null)
	}
	
	///////////////////////////////////////////////////TestCases for identifySPRedirect --> ENDS///////////////////////////////////////////////////
	
	/////////////////////////////////////////////////TestCases for isSpcForRecognizedUser --> ENDS///////////////////////////////////////////////////
	/////////Signature : public boolean isSpcForRecognizedUser(DynamoHttpServletRequest pRequest) ///////////
	
	def "isSpcForRecognizedUser. This TC is the happy flow of isSpcForRecognizedUser" (){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			requestMock.getSession().getAttribute("sessionBean") >> sessionBeanMock
		
		when:
			booleanResult = testObj.isSpcForRecognizedUser(requestMock)
		then:
			booleanResult == FALSE
			1 * sessionBeanMock.isSinglePageCheckout()	
	}
	
	/////////////////////////////////////////////////TestCases for isSpcForRecognizedUser --> ENDS///////////////////////////////////////////////////
	
	/////////////////////////////////////////////////TestCases for toString --> ENDS///////////////////////////////////////////////////
	
	def "toString method happy flow" (){
		given:
		
		
		when:
			def results = testObj.toString()
		then:
			results != null
	}
	
	/////////////////////////////////////////////////TestCases for toString --> ENDS///////////////////////////////////////////////////
	
}










