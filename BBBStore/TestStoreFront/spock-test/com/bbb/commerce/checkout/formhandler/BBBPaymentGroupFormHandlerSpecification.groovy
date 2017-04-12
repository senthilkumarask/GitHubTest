package com.bbb.commerce.checkout.formhandler

import java.beans.IntrospectionException

import javax.transaction.SystemException
import javax.transaction.Transaction
import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.CommerceException
import atg.commerce.order.CreditCard
import atg.commerce.order.Order
import atg.commerce.order.OrderHolder
import atg.commerce.order.PaymentGroup
import atg.commerce.order.PaymentGroupManager
import atg.commerce.order.SimpleOrderManager
import atg.commerce.pricing.PricingModelHolder
import atg.commerce.util.RepeatingRequestMonitor
import atg.core.util.Address
import atg.payment.creditcard.ExtendableCreditCardTools
import atg.service.pipeline.PipelineManager
import atg.service.pipeline.PipelineResult
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.DynamoHttpServletResponse
import atg.userprofiling.Profile

import com.bbb.account.api.BBBCreditCardAPIImpl
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CreditCardTypeVO
import com.bbb.commerce.checkout.BBBCreditCardContainer
import com.bbb.commerce.checkout.droplet.GetCreditCardsForPayment
import com.bbb.commerce.checkout.manager.BBBCreditCardTools
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.common.BasicBBBCreditCardInfo
import com.bbb.commerce.order.BBBCreditCard
import com.bbb.commerce.order.BBBGiftCard
import com.bbb.commerce.order.BBBPaymentGroupManager
import com.bbb.commerce.order.ManageCheckoutLogging
import com.bbb.commerce.order.Paypal
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBOrderPriceInfo
import com.bbb.utils.BBBConfigRepoUtils
import com.bbb.utils.CommonConfiguration
/**
 * 
 * @author kmagud
 *
 */
class BBBPaymentGroupFormHandlerSpecification extends BBBExtendedSpec {
	
	def BBBPaymentGroupFormHandler testObj
	def LblTxtTemplateManager messageHandlerMock = Mock()
	def CommonConfiguration commonConfigurationMock = Mock()
	def GetCreditCardsForPayment creditCardsForPaymentMock = Mock()
	def Profile profilePathMock = Mock()
	def CheckoutProgressStates checkoutProgressStatesMock = Mock()
	def ExtendableCreditCardTools creditCardToolsMock = Mock()
	def ManageCheckoutLogging manageLoggingMock = Mock()
	def BBBCreditCardAPIImpl creditCardAPIMock =Mock()
	def BBBCreditCardContainer creditCardContainerMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def BasicBBBCreditCardInfo creditCardInfoMock = Mock()
	def PaymentGroupManager paymentGroupManagerMock = Mock()
	def OrderHolder shoppingCartMock
	def Order orderMock = Mock()
	def BBBPaymentGroupManager bbbPaymentGroupManagerMock = Mock()
	def BBBOrderPriceInfo bbbOrderPriceInfoMock = Mock()
	def BasicBBBCreditCardInfo basicBBBCreditCardInfoMock = Mock()
	def SimpleOrderManager orderManagerMock = Mock()
	def Transaction transactionMock = Mock()
	def PaymentGroup paymentGroupMock = Mock()
	def Address addressMock = Mock()
	def BBBCreditCard bbbCreditCardMock = Mock()
	def CreditCard creditCardMock = Mock()
	def BBBOrderImpl bbbOrderImplMock = Mock()
	def TransactionManager transactionManagerMock = Mock()
	def Paypal paypalMock = Mock()
	def BBBRepositoryContactInfo bbbRepositoryContactInfoMock = Mock()
	def RepeatingRequestMonitor repeatingRequestMonitorMock = Mock()
	def BBBCreditCardTools bbbCreditCardToolsMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
		
	def setup() {
		
		testObj = new BBBPaymentGroupFormHandler(creditCardTools:creditCardToolsMock,catalogTools:catalogToolsMock,
			creditCardsForPayment:creditCardsForPaymentMock,profilePath:profilePathMock,
			creditCardAPI:creditCardAPIMock,messageHandler:messageHandlerMock,checkoutProgressStates:checkoutProgressStatesMock,
			creditCardContainer:creditCardContainerMock,commonConfiguration:commonConfigurationMock,manageLogging:manageLoggingMock)
		manageLoggingMock.isPaymentFormHandlerLogging() >> false
		shoppingCartMock = Mock()
		shoppingCartMock.getCurrent() >> orderMock
		
		paymentGroupMock.getId() >> "23232"
		BBBConfigRepoUtils.setBbbCatalogTools(catalogToolsMock)
	}
	
	
	
	/////////////////////////////////TCs for handlePayment starts////////////////////////////////////////
	//Signature : public boolean handlePayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response) ////
	
	def "handlePayment. This TC is the happy flow of handlePayment"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC" 
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [creditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditCardMapMock = new HashMap<String, BasicBBBCreditCardInfo>()
			creditCardMapMock.put("Edit", creditCardInfoMock)
			testObj.getCreditCardContainer().getCreditCardMap() >> creditCardMapMock 
			testObj.getCreditCardContainer().getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> creditCardInfoMock
			
			testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			bbbCreditCardMock.getBillingAddress() >> null
			creditCardMock.getBillingAddress() >> addressMock
			testObj.copyContactAddress(_) >> null
			testObj.setVerificationNumber("2232323")
			creditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			creditCardInfoMock.getCreditCardType() >> "VISA"
			creditCardInfoMock.getExpirationDayOfMonth() >> "12"
			creditCardInfoMock.getExpirationMonth() >> "12"
			creditCardInfoMock.getExpirationYear() >> "2020"
			creditCardInfoMock.getNameOnCard() >> "John"
			creditCardInfoMock.getLastFourDigits() >> "1111"
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "PAYMENT"
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> TRUE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"] 
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"] 
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * testObj.logDebug('BBBPaymentGroupFormHandler.handlePayment Entered', null)
			1 * testObj.logDebug('adding form exception: err_generic_error', null)
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbCreditCardMock.setBillingAddress(null)
			1 * bbbCreditCardMock.setExpirationDayOfMonth('12')
			1 * bbbCreditCardMock.setExpirationMonth('12')
			1 * bbbCreditCardMock.setExpirationYear('2020')
			1 * bbbCreditCardMock.setPropertyValue('nameOnCard', 'John')
			1 * bbbCreditCardMock.setLastFourDigits('1111')
			1 * bbbCreditCardMock.setCardVerficationNumber('2232323')
			1 * bbbOrderImplMock.setDummyOrder(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbCreditCardMock.setCreditCardType('VISA')
			1 * checkoutProgressStatesMock.setCurrentLevel('REVIEW')
			1 * bbbCreditCardMock.setCreditCardNumber('411111111111111')
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
	}
	
	def "handlePayment. This TC is when getSelectedCreditCardId is (New) and paymentGroup instanceof BBBCreditCard"(){
		given:
 			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			testObj.getCurrentSiteId() >> "BedBathUS"
			testObj.setCreditCardAPI(creditCardAPIMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["false"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(false)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('METHOD')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * creditCardAPIMock.addNewCreditCard(profilePathMock,basicBBBCreditCardInfoMock, 'BedBathUS')
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
	}
	
	def "handlePayment. This TC is when paymentGroup instanceof Paypal and internationalCreditCardFlag is true"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [paypalMock,paymentGroupMock]
			testObj.setShoppingCart(shoppingCartMock)
			bbbPaymentGroupManagerMock.createPaymentGroup("creditCard") >> paymentGroupMock
			paypalMock.getId() >> "p2232323"
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			//paymentGroupMock.getId() >> "p223232"
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> null
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> null
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(false)
			2 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('METHOD')
			1 * testObj.setCurrentPaymentGroup(paymentGroupMock)
			1 * bbbPaymentGroupManagerMock.addPaymentGroupToOrder(bbbOrderImplMock,paymentGroupMock)
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, '23232')
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, 'p2232323')
	}
	
	def "handlePayment. This TC is when getFormError is true and session is defined in invalidateSession method"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> true
			testObj.setCreditCardContainer(creditCardContainerMock)
			creditCardContainerMock.setCreditCardRetryCount(3)
			creditCardContainerMock.setMaxCreditCardRetryCount(2)
			requestMock.getLocale() >> {return new Locale("en_US")}
			requestMock.getSession(false) >> sessionMock
			
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "PAYMENT"
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> null
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_invalidattempt', 'en_us', null, null)
			1 * checkoutProgressStatesMock.setCurrentLevel('PAYMENT')
	}
	
	def "handlePayment. This TC is when getFormError is true and method returns true"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> true
			testObj.setCreditCardContainer(creditCardContainerMock)
			creditCardContainerMock.setCreditCardRetryCount(2)
			creditCardContainerMock.setMaxCreditCardRetryCount(3)
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == TRUE
			2 * testObj.getOrder()
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
	}
	
	def "handlePayment. This TC is when orderAmtCoveredByGC is true"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "true"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "PAYMENT"
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> ""
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * checkoutProgressStatesMock.setCurrentLevel('REVIEW')
	}
	
	def "handlePayment. This TC is when getSelectedCreditCardId is empty and getOrder().getBillingAddress() is defined"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [creditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setCurrentPaymentGroup(null)
			bbbPaymentGroupManagerMock.createPaymentGroup("creditCard") >> creditCardMock
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			//paypalMock.getId() >> "p2232323"
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "PAYMENT"
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.setCurrentPaymentGroup(creditCardMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('REVIEW')
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, null)
			1 * bbbPaymentGroupManagerMock.addPaymentGroupToOrder(bbbOrderImplMock,creditCardMock)
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * creditCardMock.setBillingAddress(bbbRepositoryContactInfoMock)
	}
	
	def "handlePayment. This TC is when isSaveProfileFlag is false"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(FALSE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["false"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(false)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('METHOD')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
	}
	
	def "handlePayment. This TC is when the user is tranisent and when CommerceException thrown"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> true
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323') >> {throw new CommerceException("Mock for CommerceException")}
			requestMock.getLocale() >> {return new Locale("en_US")}
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["false"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.logError('Error Occured while updating an order:',_)
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(false)
			1 * checkoutProgressStatesMock.setCurrentLevel('METHOD')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
	}
	
	def "handlePayment. This TC is when IntrospectionException throws as an exception"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			
			bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock) >> {throw new IntrospectionException("Mock for IntrospectionException")}
			requestMock.getLocale() >> {return new Locale("en_US")}
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * checkoutProgressStatesMock.setCurrentLevel('METHOD')
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * testObj.logError('Error Occured while copy billing address to credit card payment group: ',_)
			
	}
	
	def "handlePayment. This TC is when internationalCreditCardFlag is empty"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> ""
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["false"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * testObj.commitTransaction(transactionMock)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(false)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('METHOD')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
			
	}
	
	def "handlePayment. This TC is when BBBSystemException is thrown"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			checkoutProgressStatesMock.getCurrentLevel() >> "METHOD"
			testObj.getCurrentSiteId() >> "BedBathUS"
			testObj.setCreditCardAPI(creditCardAPIMock)
			creditCardAPIMock.addNewCreditCard(profilePathMock,basicBBBCreditCardInfoMock, 'BedBathUS') >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			requestMock.getLocale() >> {return new Locale("en_US")}
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["false"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handlePayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.logError('Error Occured while copy billing address to credit card payment group: ',_)
			
	}
	
	/////////////////////////////////TCs for handlePayment ends////////////////////////////////////////
	
	
	/////////////////////////////////TCs for handleSpPayment starts////////////////////////////////////////
	//Signature : public final boolean handleSpPayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response) ////
	
	def "handleSpPayment. This TC is the happy flow of handleSpPayment"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [creditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditCardMapMock = new HashMap<String, BasicBBBCreditCardInfo>()
			creditCardMapMock.put("Edit", creditCardInfoMock)
			testObj.getCreditCardContainer().getCreditCardMap() >> creditCardMapMock
			testObj.getCreditCardContainer().getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> creditCardInfoMock
			
			testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			creditCardMock.getBillingAddress() >> addressMock
			testObj.copyContactAddress(_) >> null
			testObj.setVerificationNumber("2232323")
			creditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			creditCardInfoMock.getCreditCardType() >> "VISA"
			creditCardInfoMock.getExpirationDayOfMonth() >> "12"
			creditCardInfoMock.getExpirationMonth() >> "12"
			creditCardInfoMock.getExpirationYear() >> "2020"
			creditCardInfoMock.getNameOnCard() >> "John"
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> TRUE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbCreditCardMock.setBillingAddress(null)
			1 * bbbCreditCardMock.setExpirationDayOfMonth('12')
			1 * bbbCreditCardMock.setExpirationMonth('12')
			1 * bbbCreditCardMock.setExpirationYear('2020')
			1 * bbbCreditCardMock.setPropertyValue('nameOnCard', 'John')
			1 * bbbCreditCardMock.setCardVerficationNumber('2232323')
			1 * bbbOrderImplMock.setDummyOrder(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbCreditCardMock.setCreditCardType('VISA')
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * bbbCreditCardMock.setCreditCardNumber('411111111111111')
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
	}
	
	def "handleSpPayment. This TC is when getSelectedCreditCardId is (New) and paymentGroup instanceof BBBCreditCard"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			repeatingRequestMonitorMock.isUniqueRequestEntry("BBBPaymentFormHandler.handleSpPayment") >> true
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			testObj.getCurrentSiteId() >> "BedBathUS"
			testObj.setCreditCardAPI(creditCardAPIMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> [null]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * creditCardAPIMock.addNewCreditCard(profilePathMock,basicBBBCreditCardInfoMock, 'BedBathUS')
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
	}
	
	def "handleSpPayment. This TC is when paymentGroup instanceof Paypal and internationalCreditCardFlag is true"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			repeatingRequestMonitorMock.isUniqueRequestEntry("BBBPaymentFormHandler.handleSpPayment") >> true
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [paypalMock, paymentGroupMock]
			testObj.setShoppingCart(shoppingCartMock)
			bbbPaymentGroupManagerMock.createPaymentGroup("creditCard") >> paymentGroupMock
			paypalMock.getId() >> "p2232323"
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			//paymentGroupMock.getId() >> "p223232"
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> null
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> [""]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(true)
			2 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * testObj.setCurrentPaymentGroup(paymentGroupMock)
			1 * bbbPaymentGroupManagerMock.addPaymentGroupToOrder(bbbOrderImplMock,paymentGroupMock)
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, '23232')
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, 'p2232323')
			1 * repeatingRequestMonitorMock.removeRequestEntry("BBBPaymentFormHandler.handleSpPayment")
	}
	
	def "handleSpPayment. This TC is when getFormError is true and session is defined in invalidateSession method"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> true
			testObj.setCreditCardContainer(creditCardContainerMock)
			creditCardContainerMock.setCreditCardRetryCount(3)
			creditCardContainerMock.setMaxCreditCardRetryCount(2)
			requestMock.getLocale() >> {return new Locale("en_US")}
			
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> null
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_creditcard_invalidattempt', 'en_us', null, null)
			1 * sessionMock.removeAttribute('paymentProgress')
			
	}
	
	def "handleSpPayment. This TC is when getFormError is true and method returns true"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> true
			testObj.setCreditCardContainer(creditCardContainerMock)
			creditCardContainerMock.setCreditCardRetryCount(2)
			creditCardContainerMock.setMaxCreditCardRetryCount(3)
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
	}
	
	def "handleSpPayment. This TC is when orderAmtCoveredByGC is true"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "true"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> null
			testObj.setMessageHandler(messageHandlerMock)
			
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> ""
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
	}
	
	def "handleSpPayment. This TC is when getRepeatingRequestMonitor is defined"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> ""
			checkoutProgressStatesMock.getFailureURL() >> ""
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
	}
	
	def "handleSpPayment. This TC is when getSelectedCreditCardId is empty and getOrder().getBillingAddress() is defined"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "true"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [creditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setCurrentPaymentGroup(null)
			bbbPaymentGroupManagerMock.createPaymentGroup("creditCard") >> creditCardMock
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			paypalMock.getId() >> "p2232323"
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setOrderManager(orderManagerMock)
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.setCurrentPaymentGroup(creditCardMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, null)
			1 * bbbPaymentGroupManagerMock.addPaymentGroupToOrder(bbbOrderImplMock,creditCardMock)
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * creditCardMock.setBillingAddress(bbbRepositoryContactInfoMock)
	}
	
	def "handleSpPayment. This TC is when isSaveProfileFlag is false"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(FALSE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> [null]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
	}
	
	def "handleSpPayment. This TC is when the user is tranisent and when CommerceException thrown"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			repeatingRequestMonitorMock.isUniqueRequestEntry("BBBPaymentFormHandler.handleSpPayment") >> true
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> true
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323') >> {throw new CommerceException("Mock for CommerceException")}
			requestMock.getLocale() >> {return new Locale("en_US")}
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> [""]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["411111111111111"]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.logError('Error Occured while updating an order:',_)
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(true)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
	}
	
	def "handleSpPayment. This TC is when IntrospectionException throws as an exception"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			repeatingRequestMonitorMock.isUniqueRequestEntry("BBBPaymentFormHandler.handleSpPayment") >> true
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			
			bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock) >> {throw new IntrospectionException("Mock for IntrospectionException")}
			requestMock.getLocale() >> {return new Locale("en_US")}
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * testObj.logError('Error Occured while copy billing address to credit card payment group: ',_)
			
	}
	
	def "handleSpPayment. This TC is when BBBSystemException is thrown"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> "false"
			testObj.ensureTransaction() >> transactionMock
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			repeatingRequestMonitorMock.isUniqueRequestEntry("BBBPaymentFormHandler.handleSpPayment") >> true
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			testObj.getCurrentSiteId() >> "BedBathUS"
			testObj.setCreditCardAPI(creditCardAPIMock)
			creditCardAPIMock.addNewCreditCard(profilePathMock,basicBBBCreditCardInfoMock, 'BedBathUS') >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			requestMock.getLocale() >> {return new Locale("en_US")}
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> [null]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * testObj.logError('Error Occured while copy billing address to credit card payment group: ',_)
			
	}
	
	def "handleSpPayment. This TC is when internationalCreditCardFlag is empty"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			requestMock.getParameter("IsOrderAmtCoveredByGC") >> "orderAmtCoveredByGC"
			requestMock.getSession().getAttribute(BBBCoreConstants.INERNATIONAL_CREDIT_CARDS) >> ""
			testObj.ensureTransaction() >> transactionMock
			testObj.getRepeatingRequestMonitor() >> repeatingRequestMonitorMock
			repeatingRequestMonitorMock.isUniqueRequestEntry("BBBPaymentFormHandler.handleSpPayment") >> true
			testObj.setMessageHandler(messageHandlerMock)
			testObj.getFormError() >> false
			testObj.getOrder().getPaymentGroups() >> [bbbCreditCardMock]
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setSelectedCreditCardId("New")
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setSaveProfileFlag(TRUE)
			testObj.setProfilePath(profilePathMock)
			profilePathMock.isTransient() >> false
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			basicBBBCreditCardInfoMock.getCreditCardNumber() >> "411111111111111"
			testObj.setOrderManager(orderManagerMock)
			bbbCreditCardMock.getId() >> "p2232323"
			testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			requestMock.getContextPath() >> "/store"
			checkoutProgressStatesMock.getFailureURL() >> "/home"
			testObj.checkFormRedirect(_, _, requestMock, responseMock) >> FALSE
			
			// dummyOrder public Method Coverage
			testObj.setCatalogTools(catalogToolsMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["411111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> [""]
			
		when:
			def booleanResult = testObj.handleSpPayment(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * messageHandlerMock.getErrMsg('err_generic_error', 'EN', null)
			1 * sessionMock.removeAttribute('paymentProgress')
			1 * bbbOrderImplMock.setDummyOrder(true)
			1 * orderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * testObj.commitTransaction(transactionMock)
			1 * repeatingRequestMonitorMock.removeRequestEntry('BBBPaymentFormHandler.handleSpPayment')
			1 * testObj.setCurrentPaymentGroup(bbbCreditCardMock)
			1 * orderManagerMock.addRemainingOrderAmountToPaymentGroup(bbbOrderImplMock, 'p2232323')
			1 * bbbPaymentGroupManagerMock.createOrUpdateCreditCardPaymentGroup(bbbOrderImplMock,basicBBBCreditCardInfoMock)
			
	}
	
	/////////////////////////////////TCs for handleSpPayment ends////////////////////////////////////////
	
	/////////////////////////////////TCs for runRepricingProcess starts////////////////////////////////////////
	//Signature : protected final void runRepricingProcess(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse, final String pOperationType) ////
	
	def "runRepricingProcess. This TC is the happy flow of runRepricingProcess"(){
		given:
			String operationType = "AnyType"
			testObj.setRepriceOrderChainId("232323")
			testObj.setOrder(orderMock)
			def PricingModelHolder pricingModelHolderMock = Mock()
			def PipelineResult pipelineResultMock = Mock()
			def PipelineManager pipelineManagerMock = Mock()
			testObj.setUserPricingModels(pricingModelHolderMock)
			testObj.setUserLocale(null)
			testObj.setProfile(profilePathMock)
			testObj.setOrderManager(orderManagerMock)
			testObj.setPipelineManager(pipelineManagerMock)
			testObj.runProcess(testObj.getRepriceOrderChainId(),_) >> pipelineResultMock
			testObj.processPipelineErrors(pipelineResultMock)
			
			1 * pipelineManagerMock.runProcess('232323', ['OrderManager':orderManagerMock, 'PricingModels':pricingModelHolderMock, 'Order':orderMock, 'Profile':profilePathMock, 
			'Locale':null, 'PricingOp':'AnyType'])
			
		expect:
			 testObj.runRepricingProcess(requestMock, responseMock, operationType)
	}
	
	def "runRepricingProcess. This TC is when getRepriceOrderChainId value is not defined"(){
		given:
			String operationType = "AnyType"
			testObj.setRepriceOrderChainId(null)
			
		expect:
			 testObj.runRepricingProcess(requestMock, responseMock, operationType)
	}
	
	/////////////////////////////////TCs for runRepricingProcess ends////////////////////////////////////////
	
	/////////////////////////////////TCs for prePayment starts////////////////////////////////////////
	//Signature : public final void prePayment(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response) ////
	
	def "prePayment. This TC is the happy flow of prePayment"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditcardMap.put("Edit", creditCardInfoMock)
			creditCardContainerMock.getCreditCardMap() >>  creditcardMap
			creditCardContainerMock.getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> creditCardInfoMock
			testObj.setVerificationNumber("2223232")
			bbbCreditCardToolsMock.verifyCreditCard(creditCardInfoMock) >> 0
			creditCardInfoMock.getCreditCardType() >> "VISA"
			creditCardInfoMock.getNameOnCard() >> "John"
			bbbCreditCardToolsMock.validateSecurityCode(testObj.getVerificationNumber(),creditCardInfoMock.getCreditCardType()) >> 0
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			
		expect:
			testObj.prePayment(requestMock, responseMock)
			
	}
	
	def "prePayment. This TC is when validateSecurityCode returns not zero"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditcardMap.put("Edit", creditCardInfoMock)
			creditCardContainerMock.getCreditCardMap() >>  creditcardMap
			creditCardContainerMock.getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> creditCardInfoMock
			testObj.setVerificationNumber("2223232")
			bbbCreditCardToolsMock.verifyCreditCard(creditCardInfoMock) >> 0
			creditCardInfoMock.getCreditCardType() >> "VISA"
			creditCardInfoMock.getNameOnCard() >> "John"
			bbbCreditCardToolsMock.validateSecurityCode(testObj.getVerificationNumber(),creditCardInfoMock.getCreditCardType()) >> 2
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCVV', 'EN', null)
			
		expect:
			testObj.prePayment(requestMock, responseMock)
			
	}
	
	def "prePayment. This TC is when verifyCreditCard returns not 0"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditcardMap.put("Edit", creditCardInfoMock)
			creditCardContainerMock.getCreditCardMap() >>  creditcardMap
			creditCardContainerMock.getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> creditCardInfoMock
			testObj.setVerificationNumber("2223232")
			bbbCreditCardToolsMock.verifyCreditCard(creditCardInfoMock) >> 2
			creditCardInfoMock.getNameOnCard() >> "John"
			bbbCreditCardToolsMock.validateSecurityCode(testObj.getVerificationNumber(),creditCardInfoMock.getCreditCardType()) >> 2
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCVV', 'EN', null)
			
		expect:
			testObj.prePayment(requestMock, responseMock)
			
	}
	
	def "prePayment. This TC is when verificationNumber is empty"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditcardMap.put("Edit", creditCardInfoMock)
			creditCardContainerMock.getCreditCardMap() >>  creditcardMap
			creditCardContainerMock.getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> creditCardInfoMock
			testObj.setVerificationNumber("")
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCVV', 'EN', null)
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when creditCardInfo is null"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("Edit")
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditcardMap.put(null, null)
			creditCardContainerMock.getCreditCardMap() >>  creditcardMap
			creditCardContainerMock.getCreditCardMap().get(testObj.getSelectedCreditCardId()) >> null
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when selectedCreditCardId is (New) and verifyCreditCard returns not 0"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("New")
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			bbbCreditCardToolsMock.verifyCreditCard(basicBBBCreditCardInfoMock) >> 2
			bbbCreditCardToolsMock.getStatusCodeMessage(2) >> "someMsg"
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			1 * basicBBBCreditCardInfoMock.getNameOnCard()
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when selectedCreditCardId is (New) and validateSecurityCode returns 0"(){
		given:
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("New")
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			bbbCreditCardToolsMock.verifyCreditCard(basicBBBCreditCardInfoMock) >> 0
			bbbCreditCardToolsMock.getStatusCodeMessage(2) >> "someMsg"
			basicBBBCreditCardInfoMock.setCardVerificationNumber("132")
			basicBBBCreditCardInfoMock.setCreditCardType("VISA")
			bbbCreditCardToolsMock.validateSecurityCode(basicBBBCreditCardInfoMock.getCardVerificationNumber(),basicBBBCreditCardInfoMock.getCreditCardType()) >> 0
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			1 * basicBBBCreditCardInfoMock.getNameOnCard()
			1 * basicBBBCreditCardInfoMock.getCardVerificationNumber()
			1 * basicBBBCreditCardInfoMock.getCreditCardType()
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when selectedCreditCardId is (New) and validateSecurityCode returns 3"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setCreditCardContainer(creditCardContainerMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("New")
			testObj.setCreditCardInfo(basicBBBCreditCardInfoMock)
			bbbCreditCardToolsMock.verifyCreditCard(basicBBBCreditCardInfoMock) >> 0
			bbbCreditCardToolsMock.getStatusCodeMessage(2) >> "someMsg"
			basicBBBCreditCardInfoMock.setCardVerificationNumber("132")
			basicBBBCreditCardInfoMock.setCreditCardType("VISA")
			bbbCreditCardToolsMock.validateSecurityCode(basicBBBCreditCardInfoMock.getCardVerificationNumber(),basicBBBCreditCardInfoMock.getCreditCardType()) >> 3
			
		when:
			testObj.prePayment(requestMock, responseMock)
			
		then:
			1 * basicBBBCreditCardInfoMock.getCreditCardType()
			1 * basicBBBCreditCardInfoMock.getNameOnCard()
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCardName', 'EN', null)
			1 * messageHandlerMock.getErrMsg('err_checkout_invalidCVV', 'EN', null)
			1 * basicBBBCreditCardInfoMock.getCardVerificationNumber()
	}
	
	def "prePayment. This TC is when selectedCreditCardId is empty and getTotal returns 24.2"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("")
			testObj.setOrder(bbbOrderImplMock)
			//bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> "BOPUS_ONLY"
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 5.5d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderPriceInfoMock.getTotal() >> 24.2d
			1 * testObj.logDebug('Gift card does not cover order total', null)
			1 * testObj.logDebug('adding form exception: err_checkout_insufficient_gift_card: err_checkout_insufficient_gift_card', null)
			2 * testObj.getPaymentGroupManager()
			6 * testObj.getOrder()
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when selectedCreditCardId is empty and getTotal returns 2.2"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("")
			testObj.setOrder(bbbOrderImplMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 5.5d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderPriceInfoMock.getTotal() >> 2.2d
			2 * testObj.getPaymentGroupManager()
			6 * testObj.getOrder()
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when getOnlineBopusItemsStatusInOrder is BOPUS_ONLY"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId("")
			testObj.setOrder(bbbOrderImplMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			//covering getOnlineBopusItemsStatusInOrder because this method is final so cannot mock or spy 
			def BBBCommerceItem bbbCommerceItemMock = Mock()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopusItem"
			bbbOrderImplMock.getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderImplMock.getOnlineBopusItemsStatusInOrder() >> BBBCheckoutConstants.BOPUS_ONLY
			1 * testObj.logDebug('BOPUS Order but No credit card added', null)
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	def "prePayment. This TC is when selectedCreditCardId is not defined"(){
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			testObj.setCreditCardTools(bbbCreditCardToolsMock)
			testObj.getCreditCardTools() >> bbbCreditCardToolsMock
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			bbbOrderImplMock.getCommerceItemCount() >> 2
			testObj.setSelectedCreditCardId(null)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 0.0
			
			1 * testObj.logDebug('Credit card is null or empty', null)
			
		expect:
			testObj.prePayment(requestMock, responseMock)
	}
	
	/////////////////////////////////TCs for prePayment ends////////////////////////////////////////
	
	/////////////////////////////////TCs for dummyOrder starts////////////////////////////////////////
	//Signature : public final void dummyOrder(String creditCardNumber) ////
	
	def "dummyOrder. This TC is the happy flow of dummyOrder"(){
		given:
			String creditCardNumber = "4111111111111111"
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setOrder(bbbOrderImplMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["4111111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["4111111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["4111111111111111"]
			1 * bbbOrderImplMock.setDummyOrder(true)
			
		expect:
			testObj.dummyOrder(creditCardNumber)
	}
	
	def "dummyOrder. This TC is when dummyOrderOn is empty"(){
		given:
			String creditCardNumber = "4111111111111111"
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setOrder(bbbOrderImplMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> []
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["4111111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["4111111111111111"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["4111111111111111"]
			1 * bbbOrderImplMock.setDummyOrder(false)
			
		expect:
			testObj.dummyOrder(creditCardNumber)
	}
	
	def "dummyOrder. This TC is when BBBSystemException thrown in dummyOrder"(){
		given:
			String creditCardNumber = "4111111111111111"
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setOrder(bbbOrderImplMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			0 * bbbOrderImplMock.setDummyOrder(false)
			
		expect:
			testObj.dummyOrder(creditCardNumber)
	}
	
	def "dummyOrder. This TC is when BBBBusinessException thrown in dummyOrder"(){
		given:
			String creditCardNumber = "4111111111111111"
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setOrder(bbbOrderImplMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			0 * bbbOrderImplMock.setDummyOrder(false)
			
		expect:
			testObj.dummyOrder(creditCardNumber)
	}
	
	def "dummyOrder. This TC is when dummyvalues not equal to creditCardNumber"(){
		given:
			String creditCardNumber = "4111111111111111"
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setOrder(bbbOrderImplMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> ["4111111111111112"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> ["4111111111111112"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> ["4111111111111112"]
			1 * bbbOrderImplMock.setDummyOrder(false)
			
		expect:
			testObj.dummyOrder(creditCardNumber)
	}
	
	def "dummyOrder. This TC is when dummyvalues are empty"(){
		given:
			String creditCardNumber = "4111111111111111"
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setOrder(bbbOrderImplMock)
			catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG) >> ["true"]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Discover") >> [""]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Master") >> [""]
			catalogToolsMock.getContentCatalogConfigration("DummyCC_Visa") >> [""]
			1 * bbbOrderImplMock.setDummyOrder(false)
			
		expect:
			testObj.dummyOrder(creditCardNumber)
	}
	
	/////////////////////////////////TCs for dummyOrder ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleRemoveGiftCard starts////////////////////////////////////////
	//Signature : public final boolean handleRemoveGiftCard(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ////
	
	def "handleRemoveGiftCard. This TC is the happy flow of handleRemoveGiftCard"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect("atg-rest-ignore-redirect","atg-rest-ignore-redirect", requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleRemoveGiftCard(requestMock, responseMock)
		then:
			booleanResult == TRUE
			testObj.getRedirectURL() == "atg-rest-ignore-redirect"
			
	}
	
	/////////////////////////////////TCs for handleRemoveGiftCard ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleRemovePaymentGroup starts////////////////////////////////////////
	//Signature : public final boolean handleRemovePaymentGroup(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////
	
	def "handleRemovePaymentGroup. This TC is the happy flow of handleRemovePaymentGroup"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> transactionMock
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/failure/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect("/store/failure/url", "/store/failure/url", requestMock, responseMock) >> TRUE

			
		when:
			def booleanResult = testObj.handleRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			1 * testObj.logDebug('Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			
	}
	
	def "handleRemovePaymentGroup. This TC is when CommerceException thrown in updateOrder method"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> transactionMock
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 orderManagerMock.updateOrder(testObj.getOrder()) >> {throw new CommerceException("Mock for CommerceException")}
			 requestMock.getLocale() >> {return new Locale("en_US")}
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/failure/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect("/store/failure/url", "/store/failure/url", requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logDebug('adding form exception: systemError', null)
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * testObj.logError('Error Occured while process the requrest: atg.commerce.CommerceException: Mock for CommerceException')
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
			1 * testObj.logDebug('Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
	}
	
	def "handleRemovePaymentGroup. This TC is when redirectURL is not empty"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("/failure/url")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(_,_, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handleRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
	}
	
	def "handleRemovePaymentGroup. This TC is when getFailureURL is not defined"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> null
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(_,_, requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
	}
	
	def "handleRemovePaymentGroup. This TC is when getFailureURL is empty"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> ""
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(_,_, requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
			
	}
	
	/////////////////////////////////TCs for handleRemovePaymentGroup ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleSpRemoveGiftCard starts////////////////////////////////////////
	//Signature : public final boolean handleSpRemoveGiftCard(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////
	
	def "handleSpRemoveGiftCard. This TC is the happy flow of handleSpRemoveGiftCard"(){
		given:
			 testObj = Spy()
			 testObj.setManageLogging(manageLoggingMock)
			 String redirectUrl = "atg-rest-ignore-redirect"
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(redirectUrl,redirectUrl, requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleSpRemoveGiftCard(requestMock, responseMock)
		then:
			booleanResult == TRUE
			testObj.getRedirectURL() == redirectUrl
			
	}
	
	/////////////////////////////////TCs for handleSpRemoveGiftCard ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleSpRemovePaymentGroup starts////////////////////////////////////////
	//Signature : public final boolean handleSpRemovePaymentGroup(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////
	
	def "handleSpRemovePaymentGroup. This TC is the happy flow of handleSpRemovePaymentGroup"(){
		given:
			 testObj = Spy()
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.ensureTransaction() >> transactionMock
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect("/store/url","/store/url", requestMock, responseMock) >> TRUE

		when:
			def booleanResult = testObj.handleSpRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
			1 * testObj.commitTransaction(transactionMock)
			1 * testObj.logDebug('Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			
	}
	
	def "handleSpRemovePaymentGroup. This TC is when CommerceException thrown in updateOrder method"(){
		given:
			 testObj = Spy()
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> transactionMock
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 orderManagerMock.updateOrder(testObj.getOrder()) >> {throw new CommerceException("Mock for CommerceException")}
			 requestMock.getLocale() >> {return new Locale("en_US")}
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/failure/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect("/store/failure/url", "/store/failure/url", requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleSpRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * testObj.commitTransaction(transactionMock)
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * testObj.getOrderManager()
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			2 * testObj.getPaymentGroupManager()
			1 * testObj.logError('Error Occured while process the requrest: atg.commerce.CommerceException: Mock for CommerceException')
			1 * testObj.logDebug('Existing method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRemovePaymentGroup', null)
			1 * testObj.logDebug('adding form exception: systemError', null)
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
	}
	
	def "handleSpRemovePaymentGroup. This TC is when redirectURL is not empty"(){
		given:
			 testObj = Spy()
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("/failure/url")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> "/url"
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(_,_, requestMock, responseMock) >> FALSE
			
		when:
			def booleanResult = testObj.handleSpRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == FALSE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
	}
	
	def "handleSpRemovePaymentGroup. This TC is when getFailureURL is not defined"(){
		given:
			 testObj = Spy()
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> null
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(_,_, requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleSpRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
	}
	
	def "handleSpRemovePaymentGroup. This TC is when getFailureURL is empty"(){
		given:
			 testObj = Spy()
			 testObj.setCommonConfiguration(commonConfigurationMock)
			 commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			 testObj.setOrder(orderMock)
			 orderMock.getPaymentGroups() >> [paymentGroupMock]
			 testObj.ensureTransaction() >> null
			 testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			 testObj.setMessageHandler(messageHandlerMock)
			 testObj.setPaymentGroupId("23232")
			 testObj.setShoppingCart(shoppingCartMock)
			 testObj.setOrderManager(orderManagerMock)
			 testObj.setRedirectURL("")
			 testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
			 checkoutProgressStatesMock.getFailureURL() >> ""
			 requestMock.getContextPath() >> "/store"
			 testObj.checkFormRedirect(_,_, requestMock, responseMock) >> TRUE
			
		when:
			def booleanResult = testObj.handleSpRemovePaymentGroup(requestMock, responseMock)
		then:
			booleanResult == TRUE
			1 * orderManagerMock.updateOrder(testObj.getOrder())
			1 * checkoutProgressStatesMock.setCurrentLevel('SP_PAYMENT')
			1 * bbbPaymentGroupManagerMock.resetAmountCoveredByGiftCard(orderMock)
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(orderMock, '23232')
	}
	
	/////////////////////////////////TCs for handleSpRemovePaymentGroup ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleAddCreditCardToOrder starts////////////////////////////////////////
	//Signature : public final boolean handleAddCreditCardToOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ////
	
	def "handleAddCreditCardToOrder. This TC is the Happy flow of handleAddCreditCardToOrder"(){
		given:
			addCreditCardSpy()
			testObj.setSelectedCreditCardId("Visa")
			def BasicBBBCreditCardInfo basicBBBCreditCardInfoMock1 = Mock()
			requestMock.getObjectParameter("creditCardInfo") >> [basicBBBCreditCardInfoMock,basicBBBCreditCardInfoMock1]
			basicBBBCreditCardInfoMock.getPaymentId() >> "123456"
			basicBBBCreditCardInfoMock1.getPaymentId() >> "789156"
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditCardContainerMock.getCreditCardMap() >> creditcardMap
			creditCardInfoMock.getCardVerificationNumber() >> "121"
			//testObj.handlePayment(requestMock, responseMock) >> true
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'false')
			1 * requestMock.setParameter('Order',orderMock)
			1 * requestMock.setParameter('Profile', profilePathMock)
			1 * requestMock.setParameter('CreditCardContainer', creditCardContainerMock)
			1 * creditCardsForPaymentMock.service(requestMock, responseMock)
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRestAddCreditCard', null)
			creditcardMap.get("123456") == basicBBBCreditCardInfoMock
			creditcardMap.get("789156") == basicBBBCreditCardInfoMock1
			testObj.getVerificationNumber() == "121"
			1 * testObj.handlePayment(requestMock, responseMock) >> true
	}
	
	def "handleAddCreditCardToOrder. This TC is to call removeGiftCardsFromOrder private method"(){
		given:
			addCreditCardSpy()
			testObj.setIsOrderAmountCoveredByGiftCard("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 1.2d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			
			testObj.setSelectedCreditCardId("Visa")
			requestMock.getObjectParameter("creditCardInfo") >> [basicBBBCreditCardInfoMock]
			basicBBBCreditCardInfoMock.getPaymentId() >> "123456"
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditCardContainerMock.getCreditCardMap() >> creditcardMap
			creditCardInfoMock.getCardVerificationNumber() >> null
			testObj.handlePayment(requestMock, responseMock) >> true
			
			//removeGiftCardsFromOrder private Method Coverage
			def BBBGiftCard bbbGiftCardMock = Mock()
			orderMock.getPaymentGroups() >> [bbbGiftCardMock,paymentGroupMock]
			bbbGiftCardMock.getId() >> "p22323"
			//paymentGroupMock.getId() >> "9999"
			bbbGiftCardMock.getAmount() >> 2.2d
			paymentGroupMock.getAmount() >> 5.2d
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'true')
			1 * requestMock.setParameter('Order',orderMock)
			1 * requestMock.setParameter('Profile', profilePathMock)
			1 * requestMock.setParameter('CreditCardContainer', creditCardContainerMock)
			1 * creditCardsForPaymentMock.service(requestMock, responseMock)
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(_, 'p22323')
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRestAddCreditCard', null)
			1 * testObj.logDebug('paymentGroup.getAmount()2.2', null)
			1 * testObj.logDebug('paymentGroup.getId()23232', null)
			1 * testObj.logDebug('paymentGroup.getAmount()5.2', null)
			1 * testObj.logDebug('paymentGroup.getId()p22323', null)
			testObj.getVerificationNumber() == null
			
	}
	
	def "handleAddCreditCardToOrder. This TC is when getSelectedCreditCardId is (NEW) and paymentGroup is not instance of BBBGiftCard in removeGiftCardsFromOrder method"(){
		given:
			addCreditCardSpy()
			testObj.setIsOrderAmountCoveredByGiftCard("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 1.2d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			
			testObj.setSelectedCreditCardId("New")
			requestMock.getObjectParameter("creditCardInfo") >> [basicBBBCreditCardInfoMock]
			basicBBBCreditCardInfoMock.getPaymentId() >> "123456"
			testObj.handlePayment(requestMock, responseMock) >> true
			
			//removeGiftCardsFromOrder private Method Coverage
			testObj.getOrder().getPaymentGroups() >> [paymentGroupMock]
			//paymentGroupMock.getId() >> "22323"
			paymentGroupMock.getAmount() >> 2.2d
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * testObj.logDebug('paymentGroup.getAmount()2.2', null)
			1 * testObj.logDebug('paymentGroup.getId()23232', null)
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRestAddCreditCard', null)
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'true')
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])
	}
	
	def "handleAddCreditCardToOrder. This TC is when getSelectedCreditCardId is null and CommerceException throws in removeGiftCardsFromOrder private Method"(){
		given:
			addCreditCardSpy()
			testObj.setIsOrderAmountCoveredByGiftCard("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 1.2d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			
			testObj.setSelectedCreditCardId(null)
			testObj.handlePayment(requestMock, responseMock) >> true
			
			//removeGiftCardsFromOrder private Method Coverage
			def BBBGiftCard bbbGiftCardMock = Mock()
			testObj.getOrder().getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "22323"
			bbbGiftCardMock.getAmount() >> 2.2d
			bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(testObj.getOrder(), '22323') >> {throw new CommerceException("Mock for CommerceException")}
			
			//markTransactionRollback private Method Coverage
			testObj.isTransactionMarkedAsRollBack() >> false
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'true')
			1 * testObj.logDebug('paymentGroup.getId()22323', null)
			1 * testObj.logDebug('paymentGroup.getAmount()2.2', null)
			1 * testObj.logDebug('Starting method BBBPaymentGroupFormHandler.handleRestAddCreditCard', null)
			1 * testObj.logError('Error Occured while process the request: ', _)
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])
			1 * bbbOrderPriceInfoMock.getTotal()
			
	}
	
	def "handleAddCreditCardToOrder. This TC is when gcTotalAmount is less than getTotal method"(){
		given:
			addCreditCardSpy()
			testObj.setIsOrderAmountCoveredByGiftCard("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 2.2d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderPriceInfoMock.getTotal() >> 24.2d
			
			testObj.setSelectedCreditCardId("Visa")
			requestMock.getObjectParameter("creditCardInfo") >> [basicBBBCreditCardInfoMock]
			basicBBBCreditCardInfoMock.getPaymentId() >> "123456"
			Map<String, BasicBBBCreditCardInfo> creditcardMap = new HashMap<String, BasicBBBCreditCardInfo>()
			creditCardContainerMock.getCreditCardMap() >> creditcardMap
			creditCardInfoMock.getCardVerificationNumber() >> "121"
			testObj.handlePayment(requestMock, responseMock) >> true
			
			//removeGiftCardsFromOrder private Method Coverage
			def BBBGiftCard bbbGiftCardMock = Mock()
			testObj.getOrder().getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "22323"
			bbbGiftCardMock.getAmount() >> 2.2d
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'true')
			1 * requestMock.setParameter('Order',orderMock)
			1 * requestMock.setParameter('Profile', profilePathMock)
			1 * requestMock.setParameter('CreditCardContainer', creditCardContainerMock)
			1 * creditCardsForPaymentMock.service(requestMock, responseMock)
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])

	}
	
	def "handleAddCreditCardToOrder. This TC is when SystemException throws in markTransactionRollback private Method"(){
		given:
			addCreditCardSpy()
			testObj.setIsOrderAmountCoveredByGiftCard("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 1.2d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			
			testObj.setSelectedCreditCardId("New")
			testObj.handlePayment(requestMock, responseMock) >> true
			
			//removeGiftCardsFromOrder private Method Coverage
			def BBBGiftCard bbbGiftCardMock = Mock()
			testObj.getOrder().getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "22323"
			bbbGiftCardMock.getAmount() >> 2.2d
			bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(testObj.getOrder(), '22323') >> {throw new CommerceException("Mock for CommerceException")}
			
			//markTransactionRollback private Method Coverage
			testObj.isTransactionMarkedAsRollBack() >> false
			testObj.setTransactionToRollbackOnly() >> {throw new SystemException("Mock for SystemException")}
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'true')
			1 * testObj.logError('Error Occured while process the request: ', _)
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])
			1 * testObj.logError('cart_1021: Error in marking the transaction rollback', _)
			1 * bbbOrderPriceInfoMock.getTotal()
	}
	
	def "handleAddCreditCardToOrder. This TC is when isTransactionMarkedAsRollBack is true in markTransactionRollback private Method"(){
		given:
			addCreditCardSpy()
			testObj.setIsOrderAmountCoveredByGiftCard("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			bbbPaymentGroupManagerMock.getGiftCardTotalAmount(testObj.getOrder()) >> 1.2d
			testObj.getOrder().getPriceInfo() >> bbbOrderPriceInfoMock
			
			testObj.setSelectedCreditCardId("New")
			testObj.handlePayment(requestMock, responseMock) >> true
			
			//removeGiftCardsFromOrder private Method Coverage
			def BBBGiftCard bbbGiftCardMock = Mock()
			testObj.getOrder().getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "22323"
			bbbGiftCardMock.getAmount() >> 2.2d
			bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(testObj.getOrder(), '22323') >> {throw new CommerceException("Mock for CommerceException")}
			
			//markTransactionRollback private Method Coverage
			testObj.isTransactionMarkedAsRollBack() >> true
			
		when:
			 def booleanResult = testObj.handleAddCreditCardToOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * requestMock.setContextPath("");
			1 * testObj.logDebug('paymentGroup.getAmount()2.2', null)
			1 * testObj.logDebug('paymentGroup.getId()22323', null)
			1 * requestMock.setParameter('IsOrderAmtCoveredByGC', 'true')
			1 * testObj.logError('Error Occured while process the request: ', _)
			2 * checkoutProgressStatesMock.setCheckoutFailureURLs([:])
	}
	
	private addCreditCardSpy() {
		testObj = Spy()
		testObj.setManageLogging(manageLoggingMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		testObj.setCheckoutProgressStates(checkoutProgressStatesMock)
		testObj.setCreditCardsForPayment(creditCardsForPaymentMock)
		testObj.setCreditCardContainer(creditCardContainerMock)
		testObj.setCreditCardInfo(creditCardInfoMock)
		testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setOrder(orderMock)
		testObj.setProfile(profilePathMock)
	}
	
	/////////////////////////////////TCs for handleAddCreditCardToOrder ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleRemoveCreditCardFromOrder starts////////////////////////////////////////
	//Signature : public final boolean handleRemoveCreditCardFromOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ////
	
	def "handleRemoveCreditCardFromOrder. This TC is the happy flow of handleRemoveCreditCardFromOrder"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(orderMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setPaymentGroupId("23232")
			testObj.setShoppingCart(shoppingCartMock)
			testObj.setOrderManager(orderManagerMock)
			testObj.ensureTransaction() >> transactionMock
			
		when:
			 def booleanResult = testObj.handleRemoveCreditCardFromOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(_, '23232')
			1 * testObj.commitTransaction(_)
			1 * orderManagerMock.updateOrder(_)
			1 * transactionMock.commit()
			1 * testObj.getOrderManager()
	}
	
	def "handleRemoveCreditCardFromOrder. This TC is when CommerceException throws in removePaymentGroupFromOrder"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(FALSE)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(orderMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setPaymentGroupId("23232")
			testObj.setShoppingCart(shoppingCartMock)
			testObj.setOrderManager(orderManagerMock)
			testObj.ensureTransaction() >> transactionMock
			requestMock.getLocale() >> {return new Locale("en_US")}
			
			bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(testObj.getShoppingCart().getCurrent(), testObj.getPaymentGroupId()) >> {throw new CommerceException("Mock for CommerceException")}
			
		when:
			 def booleanResult = testObj.handleRemoveCreditCardFromOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * testObj.commitTransaction(_)
			0 * orderManagerMock.updateOrder(_)
			1 * testObj.getOrder()
			1 * transactionMock.commit()
			1 * testObj.logError('Error Occured while process the request: atg.commerce.CommerceException: Mock for CommerceException')
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
	}
	
	def "handleRemoveCreditCardFromOrder. This TC is when CommerceException throws in updateOrder"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(FALSE)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(orderMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManagerMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setPaymentGroupId("23232")
			testObj.setShoppingCart(shoppingCartMock)
			testObj.setOrderManager(orderManagerMock)
			testObj.ensureTransaction() >> null
			requestMock.getLocale() >> {return new Locale("en_US")}
			
			orderManagerMock.updateOrder(testObj.getOrder()) >> {throw new CommerceException("Mock for CommerceException")}
			
		when:
			 def booleanResult = testObj.handleRemoveCreditCardFromOrder(requestMock, responseMock)
		
		then:
			booleanResult == TRUE
			1 * bbbPaymentGroupManagerMock.removePaymentGroupFromOrder(_, '23232')
			2 * testObj.getOrder()
			1 * testObj.logError('Error Occured while process the request: atg.commerce.CommerceException: Mock for CommerceException', null)
			1 * testObj.logError('Error Occured while process the request: atg.commerce.CommerceException: Mock for CommerceException')
			1 * messageHandlerMock.getErrMsg('systemError', 'en_us', null, null)
	}
	
	def "handleRemoveCreditCardFromOrder. This TC when order object is null"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(FALSE)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(null)
			testObj.getOrder() >> null
			
		when:
			 def booleanResult = testObj.handleRemoveCreditCardFromOrder(requestMock, responseMock)
		
		then:
			booleanResult == null
			thrown NullPointerException
	}
	
	/////////////////////////////////TCs for handleRemoveCreditCardFromOrder ends////////////////////////////////////////
	
	////////////////////////////////////TCs for getCreditCardTypes Starts////////////////////////////////////////
	
	def "getCreditCardTypes. This TC when order object is null"(){
		given:
			testObj = Spy()
			testObj.getCurrentSiteId() >> "BedBathUS"
			testObj.setCatalogTools(catalogToolsMock)
			def CreditCardTypeVO creditCardTypeVOMock = Mock()
			
		when:
			 def List<CreditCardTypeVO> creditCardType = testObj.getCreditCardTypes()
		
		then:
			1 * catalogToolsMock.getCreditCardTypes(testObj.getCurrentSiteId()) >> [creditCardTypeVOMock]
			creditCardType == [creditCardTypeVOMock]
	}
	
	/////////////////////////////////TCs for getCreditCardTypes ends////////////////////////////////////////
	
	////////////////////////////////////TCs for toString Starts////////////////////////////////////////
	
	def "toString method happy flow" (){
		given:
		
		
		when:
			def results = testObj.toString()
		then:
			results != null
	}
	
	////////////////////////////////////TCs for toString Ends////////////////////////////////////////
}
