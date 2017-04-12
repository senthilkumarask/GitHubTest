package com.bbb.commerce.checkout.formhandler

import java.beans.IntrospectionException

import javax.transaction.SystemException
import javax.transaction.Transaction
import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.CommerceException
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.OrderHolder
import atg.commerce.order.SimpleOrderManager
import atg.commerce.promotion.PromotionTools
import atg.commerce.util.RepeatingRequestMonitor
import atg.droplet.DropletException
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.service.pipeline.PipelineManager
import atg.service.pipeline.RunProcessException
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.DynamoHttpServletResponse
import atg.userprofiling.Profile

import com.bbb.account.BBBGetCouponsManager
import com.bbb.account.BBBProfileManager
import com.bbb.account.BBBProfileTools
import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.vo.CouponListVo
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.order.BBBPaymentGroupManager
import com.bbb.commerce.order.ManageCheckoutLogging
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.TBSConstants
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean
import com.bbb.utils.CommonConfiguration
/**
 * 
 * @author kmagud
 *
 */
public class BBBBillingAddressFormHandlerSpecification extends BBBExtendedSpec{

	def BBBBillingAddressFormHandler testObj
	def OrderHolder cartMock = Mock()
	def BBBOrderImpl bbbOrderImplMock = Mock()
	def PromotionLookupManager promotionLookupManagerMock = Mock()
	def CheckoutProgressStates checkoutStateMock = Mock()
	def CommonConfiguration commonConfigurationMock = Mock()
	def PromotionTools promotionToolsMock = Mock()
	def LblTxtTemplateManager messageHandlerMock = Mock()
	def ManageCheckoutLogging manageLoggingMock = Mock()
	def BBBProfileManager profileManagerMock = Mock()
	def BBBSessionBean sessionBeanMock = Mock()
	def BBBCouponUtil couponUtilMock = Mock()
	def BBBPropertyManager propertyManagerMock = Mock()
	def BBBProfileTools profileToolsMock = Mock()
	def BBBAddressAPI addressAPIMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBRepositoryContactInfo bbbRepositoryContactInfoMock = Mock()
	def BBBAddressImpl bbbAddressImplMock = Mock()
	def BBBAddress bbbAddressMock = Mock()
	def RepeatingRequestMonitor repeatingRequestMonitorMock = Mock()
	def StateVO stateVOMock = Mock()
	def BBBCatalogToolsImpl bbbCatalogToolsImplMock = Mock()
	def Profile profileMock = Mock()
	def BBBCheckoutManager checkoutManagerMock = Mock()
	def BBBAddressContainer bbbAddressContainerMock = Mock()
	def RepositoryItem repositoryItemMock = Mock()
	def SimpleOrderManager orderManagerMock = Mock()
	def BBBPaymentGroupManager bbbPaymentGroupManager = Mock()
	def Transaction transactionMock = Mock()
	def TransactionManager transactionManagerMock = Mock()
	def HardgoodShippingGroup hardgoodShippingGroupMock = Mock()
	def BBBGetCouponsManager bbbGetCouponsManagerMock = Mock()
	def DropletException DropletExceptionMock = Mock()
	def PipelineManager pipelineManagerMock = Mock()
	def MutableRepositoryItem mutableRepositoryItemMock = Mock()
	def RepositoryItemDescriptor repositoryItemDescriptorMock = Mock()
	def RepositoryItem activePromotionsMock = Mock()
	def RepositoryItem activePromotion = Mock()
	def RepositoryItem coupons = Mock()

	private static final boolean TRUE = true
	private static final boolean FALSE = false
	def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
	def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()

	def setup() {
		testObj = Spy()
		/*testObj = new BBBBillingAddressFormHandler(order:bbbOrderImplMock,checkoutState:checkoutStateMock, messageHandler:messageHandlerMock, manageLogging:manageLoggingMock,
		 commonConfiguration:commonConfigurationMock, promotionLookupManager:promotionLookupManagerMock,promotionTools:promotionToolsMock,sessionBean:sessionBeanMock,
		 couponUtil:couponUtilMock,propertyManager:propertyManagerMock,profileTools:profileToolsMock,addressAPI:addressAPIMock,catalogTools:catalogToolsMock,checkoutManager:checkoutManagerMock)*/
		testObj.setManageLogging(manageLoggingMock)
		testObj.setShoppingCart(cartMock)
		cartMock.getCurrent() >> bbbOrderImplMock
		manageLoggingMock.isBillingAddressHandlerLogging() >> false
	}

	/////////////////////////////////TCs for setOrderAddress starts////////////////////////////////////////
	//Signature : public final void setOrderAddress(final BBBRepositoryContactInfo bbbRepoContInfo) ////

	def "setOrderAddress. This TC is the happy flow of setOrderAddress"() {

		given:

		testObj.setBillingAddress(bbbAddressImplMock)

		when:
		testObj.setOrderAddress(bbbRepositoryContactInfoMock)

		then:
		1 * testObj.copyContactAddress(*_) >> null
		testObj.getBillingAddress() >> bbbAddressImplMock
	}

	def "setOrderAddress. This TC is when bbbRepoContInfo is null"() {

		given:

		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setBillingAddressHandlerLogging(true)
		1 * testObj.logInfo("bbbRepoContInfo is null due to session time out")
		1 * testObj.logInfo("bbbRepoContInfo is null due to session time out", null);

		expect:
		testObj.setOrderAddress(null)
	}

	def "setOrderAddress. This TC is when exception occurs"() {

		given:

		testObj.copyContactAddress(*_) >> {throw new IntrospectionException("Mock for Introspection Exception")}
		1 * testObj.logError('checkout_1000: copying order billing Address to handler failed',_)

		expect:
		testObj.setOrderAddress(bbbRepositoryContactInfoMock)
	}

	////////////////////////////////////TCs for setOrderAddress ends////////////////////////////////////////

	/////////////////////////////////TCs for handleSaveBillingAddress starts////////////////////////////////////////
	//Signature : public final boolean handleSaveBillingAddress(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////

	def "handleSaveBillingAddress. This TC is the happy flow of handleSaveBillingAddress when userSelectedOption is (EDIT)"() {

		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		def CouponListVo couponListVoMock = new CouponListVo(mPromoId:"12345")
		bbbOrderImplMock.getCouponListVo() >> [couponListVoMock]
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(checkoutManagerMock)
		bbbRepositoryContactInfoMock.getCountry() >> "US"
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 8
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> "70001"
		bbbAddressMock.getCity() >> "California"
		profileToolsMock.getAllBillingAddresses(testObj.getProfile()) >> ["someAddress"]
		testObj.setSiteId("BedBathUS")
		addressAPIMock.addNewBillingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), false) >> bbbAddressMock
		testObj.setBillingAddressContainer(bbbAddressContainerMock)
		bbbAddressContainerMock.getDuplicate() >> [bbbAddressMock]

		//isSchoolPromotion private Method Coverage
		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.setCouponMap(couponMap)

		//postSaveBillingAddress protected method coverage
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathUSSiteCode","BedBathUSSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUSSiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_us") >> ["true"]
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> promotions
		testObj.setCouponUtil(couponUtilMock)
		couponUtilMock.applySchoolPromotion(promotions, testObj.getProfile(), bbbOrderImplMock) >> promotions
		testObj.setCollegeAddress("true")

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * orderManagerMock.updateOrder(bbbOrderImplMock)
		1 * testObj.logInfo('BBBBillingAddressFormHandler.postSaveBillingAddress is called. Order Id is  : null:::Profile ID is : null')
		1 * testObj.logInfo('BBBBillingAddressFormHandler.handleSaveBillingAddress() :: profileID : null OrderID : null')
		3 * testObj.logInfo(' available coupons in order : 12345')
		1 * testObj.logInfo('BBBBillingAddressFormHandler.handleSaveBillingAddress() end:: profileID : null OrderID : null')
		1 * testObj.checkFormRedirect(null, '/storeatg-rest-redirect',requestMock, responseMock)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock,bbbAddressMock)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * testObj.setUserLocale(_)
		1 * testObj.postSaveBillingAddress(requestMock,responseMock)
		1 * bbbRepositoryContactInfoMock.setCompanyName('')
		2 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * testObj.checkFormRedirect('/storeatg-rest-redirect', '/storeatg-rest-redirect', requestMock, responseMock)
		1 * testObj.logDebug('Start: method isSchoolPromotion', null)
		1 * testObj.logDebug('End: method isSchoolPromotion', null)
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
		1 * testObj.logDebug('invalid phone:Invalid phone number', null)
		1 * testObj.logDebug('phone:9898989898', null)
	}

	def "handleSaveBillingAddress. This TC is when userSelectedOption is NEW"(){
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("NEW")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
		testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
		repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> TRUE
		testObj.setUserLocale(new Locale("en_US"))
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(FALSE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "CA"
		Map<String, String> cnameMap = new HashMap<String, String>()
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid address1"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//validateStateForBilling private Method Coverage
		//bbbCatalogToolsImplMock.getStateList() >> [stateVOMock]
		bbbCatalogToolsImplMock.getCurrentSiteId() >> "BedBathUS"
		bbbCatalogToolsImplMock.getStates("BedBathUS", true,null) >> [stateVOMock]

		stateVOMock.getStateCode() >> "Los Angeles"
		stateVOMock.isShowOnBilling() >> TRUE

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "9797979797"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "CA"
		testObj.setSiteId("BedBathCanada")
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> "70001"
		bbbAddressMock.getCity() >> "California"
		profileToolsMock.getAllBillingAddresses(testObj.getProfile()) >> []
		addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
		testObj.setBillingAddressContainer(bbbAddressContainerMock)
		bbbAddressContainerMock.getDuplicate() >> [bbbAddressMock]

		//isSchoolPromotion private Method Coverage
		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		bbbOrderImplMock.setCouponMap(couponMap)

		//postSaveBillingAddress protected method coverage
		testObj.setCollegeAddress("false")

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * orderManagerMock.updateOrder(bbbOrderImplMock);
		3 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * repeatingRequestMonitorMock.removeRequestEntry('getStackTrace')
		1 * testObj.logDebug('Address1: Beverly Hills', null)
		2 * testObj.logDebug('phone:9898989898', null)
		1 * testObj.logDebug('Begin BBBBillingAddressFormHandler.validateStateForBilling', null)
		1 * testObj.logDebug('Start: method isSchoolPromotion', null)
		1 * testObj.postSaveBillingAddress(requestMock, responseMock)
		2 * testObj.logDebug('email:bbbforcustomer@gmail.com', null)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * profileToolsMock.updateProperty('mobileNumber', '9797979797', profileMock)
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', false)
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.logDebug('invalid country:CA', null)
		1 * testObj.logDebug('End: method isSchoolPromotion', null)
		1 * testObj.logDebug('Invalid address property. Invalid email', null)
	}


	def "handleSaveBillingAddress. This TC is when userSelectedOption is not EDIT or New, isTransient is true"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> ""
		testObj.setUserSelectedOption("DIFFERENT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "null"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		requestMock.getParameter("isInternationalBilling") >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "Canada"
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> null
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> ""
		bbbAddressMock.getMobileNumber() >> ""
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "bbb@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> ""
		testObj.setBillingAddressContainer(bbbAddressContainerMock)
		bbbAddressContainerMock.getAddressFromContainer("DIFFERENT") >> bbbAddressMock
		bbbOrderImplMock.getShippingGroups() >> ["ShippingGroup",hardgoodShippingGroupMock]
		hardgoodShippingGroupMock.getId() >> "DIFFERENT"
		bbbAddressMock.getPostalCode() >> "70001"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.setCheckoutManager(null)

		bbbRepositoryContactInfoMock.getCountry() >> "Canada"
		testObj.setSaveToAccount(TRUE)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 8
		bbbAddressContainerMock.getDuplicate() >> [bbbAddressMock]
		testObj.setSiteId("TBS_BedBathCanada")

		//isSchoolPromotion private Method Coverage
		bbbOrderImplMock.getCouponMap() >> null

		//postSaveBillingAddress protected method coverage
		testObj.setCollegeAddress("")

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * orderManagerMock.updateOrder(bbbOrderImplMock);
		1 * testObj.logDebug('End: method isSchoolPromotion', null)
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * bbbAddressMock.setPhoneNumber('')
		1 * testObj.checkFormRedirect(null, null, requestMock,responseMock)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.logDebug('Start: method isSchoolPromotion', null)
		2 * bbbAddressMock.setEmail('')
		2 * bbbAddressMock.setMobileNumber('')
		1 * bbbAddressMock.setPhoneNumber('9898989898')
		1 * testObj.postSaveBillingAddress(requestMock, responseMock)
		2 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * bbbGetCouponsManagerMock.createWalletMobile('', '9898989898', null, null, null, null, 'Los Angeles', '70001')
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * testObj.logDebug('invalid country:Canada', null)
		1 * testObj.logDebug('is_single_page_checkout_enagled:true', null)
		1 * testObj.logDebug('Invalid address property. ERR_CHECKOUT_INVALID_COUNTRY', null)
		1 * testObj.logDebug('phone2:9898989898', null)
		1 * testObj.logDebug('property2:Invalid phone number', null)
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
	}
	
	def "handleSaveBillingAddress. This TC is when billingPhoneChanged is true in postSaveBillingAddress Method"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> ""
		testObj.setUserSelectedOption("DIFFERENT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "null"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		requestMock.getParameter("isInternationalBilling") >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "Canada"
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> null
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> ""
		bbbAddressMock.getMobileNumber() >> ""
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "bbb@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> ""
		testObj.setBillingAddressContainer(bbbAddressContainerMock)
		bbbAddressContainerMock.getAddressFromContainer("DIFFERENT") >> bbbAddressMock
		bbbOrderImplMock.getShippingGroups() >> ["ShippingGroup",hardgoodShippingGroupMock]
		hardgoodShippingGroupMock.getId() >> "DIFFERENT"
		bbbAddressMock.getPostalCode() >> "70001"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.setCheckoutManager(null)

		bbbRepositoryContactInfoMock.getCountry() >> "Canada"
		testObj.setSaveToAccount(TRUE)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 8
		bbbAddressContainerMock.getDuplicate() >> [bbbAddressMock]
		testObj.setSiteId("TBS_BedBathCanada")

		//isSchoolPromotion private Method Coverage
		bbbOrderImplMock.getCouponMap() >> null

		//postSaveBillingAddress protected method coverage
		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathUSSiteCode","BedBathUSSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUSSiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_us") >> ["false"]
		profileMock.isTransient() >> true

		testObj.setPromotionTools(promotionToolsMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> promotions
		testObj.setCouponUtil(couponUtilMock)
		couponUtilMock.applySchoolPromotion(promotions, testObj.getProfile(), bbbOrderImplMock) >> promotions
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * profileMock.getPropertyValue('activePromotions')
		1 * profileMock.getPropertyValue('availablePromotionsList')
		3 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * orderManagerMock.updateOrder(bbbOrderImplMock)
		1 * bbbOrderImplMock.setCouponMap([:])
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * bbbGetCouponsManagerMock.createWalletMobile('', '9898989898', null, null, null, null, 'Los Angeles', '70001')
		1 * checkoutStateMock.setCurrentLevel('BILLING')
	}

	def "handleSaveBillingAddress. This TC is when getFormError is true"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		manageLoggingMock.isBillingAddressHandlerLogging() >> TRUE
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("NEW")
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		def CouponListVo couponListVoMock = new CouponListVo(mPromoId:"12345")
		bbbOrderImplMock.getCouponListVo() >> [couponListVoMock]
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> true
		testObj.getFormExceptions() >> [DropletExceptionMock,"someMock"]
		testObj.checkFormRedirect(null, _, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> ""
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> ""
		bbbAddressMock.getInvalidProperties() >> []
		testObj.setConfirmedEmail("bbb@gmail.com")
		bbbAddressMock.getEmail() >> "customer@gmail.com"

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * testObj.logInfo(' available coupons in order : 12345')
		1 * testObj.logInfo('BBBBillingAddressFormHandler.handleSaveBillingAddress() :: profileID : p12345 OrderID : BBB12345')
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.getUserLocale(requestMock,responseMock)
		1 * DropletExceptionMock.getErrorCode()
		1 * DropletExceptionMock.getMessage()
		1 * testObj.logDebug('email:customer@gmail.com', null)
		1 * testObj.logDebug('exception::someMock', null)
		1 * testObj.logDebug('confirmed_email:bbb@gmail.com', null)
		1 * testObj.logDebug('exception message:null', null)
		1 * testObj.logDebug('exception errorCode:null', null)
		1 * testObj.logDebug('redirect to errorURL:/storeatg-rest-redirect', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * bbbAddressMock.setCountryName(null)
	}

	def "handleSaveBillingAddress. This TC is when getFormError is true and securityStatus is null"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.setOrder(bbbOrderImplMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.getFormError() >> true
		testObj.getFormExceptions() >> ["someMock"]
		testObj.checkFormRedirect(null, _, requestMock, responseMock) >>> [true,false]

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "INDIA"
		bbbAddressMock.getInvalidProperties() >> ["Invalid address2"]

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> ""
		bbbAddressMock.getMobileNumber() >> ""
		testObj.setCheckoutManager(null)
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setSiteId("BedBathUS")
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> "70001"
		bbbAddressMock.getCity() >> "California"
		bbbRepositoryContactInfoMock.getCountry() >> ""
		testObj.setSaveToAccount(TRUE)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> null
		testObj.setAddressAPI(addressAPIMock)
		profileToolsMock.getAllBillingAddresses(testObj.getProfile()) >> null
		addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
		testObj.setBillingAddressContainer(bbbAddressContainerMock)
		bbbAddressContainerMock.getDuplicate() >> [bbbAddressMock]

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', false)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * bbbCatalogToolsImplMock.getCountriesInfo('INDIA')
		1 * bbbAddressMock.setState('INDIA')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * testObj.logDebug('exception::someMock', null)
		1 * testObj.logDebug('email:', null)
		1 * testObj.logDebug('phone:null', null)
	}

	def "handleSaveBillingAddress. This TC is when userSelectedOption is not defined"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption(null)
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getCouponListVo() >> []
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setFromAjaxSubmission(TRUE)

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"

		//validateStateForBilling private Method Coverage
		bbbCatalogToolsImplMock.getCurrentSiteId() >> "BedBathUS"
		def StateVO stateVOMock1 = Mock()
		bbbCatalogToolsImplMock.getStates("BedBathUS", true,null) >> [stateVOMock,stateVOMock1]
		stateVOMock.getStateName() >> "Los Angeles"
		stateVOMock.getStateCode() >> "Los"
		stateVOMock1.getStateCode() >> "Los Angeles"
		stateVOMock1.getStateName() >> "Losy"
		stateVOMock1.setShowOnBilling(TRUE)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setBillingAddressHandlerLogging(true)

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.logInfo('BBBBillingAddressFormHandler.handleSaveBillingAddress() :: profileID : p12345 OrderID : BBB12345')
		1 * testObj.logInfo('BBBBillingAddressFormHandler.handleSaveBillingAddress() end:: profileID : p12345 OrderID : BBB12345')
		1 * testObj.logDebug('is_single_page_checkout_enagled:true', null)
		1 * requestMock.getParameter('isInternationalBilling')
		2 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * bbbCatalogToolsImplMock.getCountriesInfo('US')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_NULL_BILLING_ADDRESS', 'EN', null)
		1 * orderManagerMock.updateOrder(bbbOrderImplMock)
		1 * testObj.getUserLocale(requestMock, responseMock)
		1 * testObj.logInfo('Invalid state for billing in single page checkout - Los Angeles')
		1 * testObj.logDebug('Invalid address property. Invalid State', null)
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid State', null)
		1 * testObj.logError('checkout_1002: Adding Form Error : ERR_CHECKOUT_NULL_BILLING_ADDRESS', null)
	}


	def "handleSaveBillingAddress. This TC is when isUniqueRequestEntry returns false"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		requestMock.getContextPath() >> "/store"
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getCouponListVo() >> []
		testObj.setUserSelectedOption("NEW")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
		repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> FALSE

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.logInfo('BBBBillingAddressFormHandler.handleSaveBillingAddress() :: profileID : p12345 OrderID : BBB12345')
		1 * testObj.getRepeatingRequestMonitor()

	}

	def "handleSaveBillingAddress. This TC is when userSelectedOption is paypalAddress"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getCouponListVo() >> null
		testObj.setUserSelectedOption("paypalAddress")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> true

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		bbbAddressMock.getInvalidProperties() >> null

		//validateStateForBilling private Method Coverage
		bbbCatalogToolsImplMock.getCurrentSiteId() >> "BedBathUS"
		bbbCatalogToolsImplMock.getStates("BedBathUS", true,null) >> [stateVOMock]
		stateVOMock.getStateName() >> "Los"
		stateVOMock.getStateCode() >> "Los"
		stateVOMock.setShowOnBilling(TRUE)
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setBillingAddressHandlerLogging(true)

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * checkoutStateMock.setCurrentLevel('REVIEW')
		1 * testObj.checkFormRedirect(null, null, requestMock, responseMock)
		1 * testObj.logInfo('Invalid state for billing in single page checkout - Los Angeles', null)
		2 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * orderManagerMock.updateOrder(bbbOrderImplMock)
		1 * testObj.logDebug('Begin BBBBillingAddressFormHandler.validateStateForBilling', null)
	}

	def "handleSaveBillingAddress. This TC is when CommerceException throws in updateOrder and BBBBusinessException throws in private Methods"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getCouponListVo() >> []
		testObj.setUserSelectedOption("paypalAddress")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> false
		testObj.isTransactionMarkedAsRollBack() >> true
		orderManagerMock.updateOrder(testObj.getOrder()) >> {throw new CommerceException("Mock for CommerceException")}

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		bbbAddressMock.getInvalidProperties() >> []

		//validateStateForBilling private Method Coverage
		bbbCatalogToolsImplMock.getCurrentSiteId() >> "BedBathUS"
		testObj.setSiteId("BedBathUS")
		bbbCatalogToolsImplMock.getStates("BedBathUS", true,null) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setBillingAddressHandlerLogging(true)

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.logError('ERROR:  getStates call has failed for Billing on site id: BedBathUS',_)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_COUNTRY', 'EN', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_BILLING_ADDRESS', 'EN', null)
		1 * messageHandlerMock.getErrMsg('Invalid State', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.logError('checkout_1003: Adding Form Error : ERR_CHECKOUT_INVALID_BILLING_ADDRESS',_)
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_INVALID_COUNTRY',_)
		1 * testObj.logError('checkout_1002: Adding Form Error : Invalid State', null)

	}

	def "handleSaveBillingAddress. This TC is when RepositoryException throws in saveBillingAddress private Method"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("NEW")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> false
		testObj.isTransactionMarkedAsRollBack() >> true

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> ""
		bbbAddressMock.getInvalidProperties() >> null
		testObj.setConfirmedEmail("")

		//validateStateForBilling private Method Coverage
		bbbCatalogToolsImplMock.getCurrentSiteId() >> "BedBathUS"
		testObj.setSiteId("BedBathUS")
		bbbCatalogToolsImplMock.getStates("BedBathUS", true,null) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		testObj.setManageLogging(manageLoggingMock)
		manageLoggingMock.setBillingAddressHandlerLogging(true)

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		bbbAddressMock.getPostalCode() >> ""
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> null
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> null
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getCity() >> "California"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', null, null, null, 'Beverly Hills', 'California', 'Los Angeles', '') >> {throw new BBBSystemException("Mock for BBBSystemException")}
		testObj.setCheckoutManager(checkoutManagerMock)
		checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
		1 * bbbAddressMock.setCountryName(null)
		1 * messageHandlerMock.getErrMsg('Invalid State', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
		1 * testObj.logError('ERROR:  getStates call has failed for Billing on site id: BedBathUS',_)
		1 * testObj.logError('checkout_1002: Adding Form Error : Invalid State', null)
		1 * testObj.logError('BBBBillingAddressFormHandler.saveBillingAddress exception', _)
		2 * testObj.logDebug('adding form exception: checkout_1003', null)
	}

	def "handleSaveBillingAddress. This TC is when IntrospectionException throws in saveBillingAddress private Method"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("NEW")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> false
		testObj.isTransactionMarkedAsRollBack() >> true

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		bbbAddressMock.getInvalidProperties() >> null
		testObj.setConfirmedEmail("bbb@gmail.com")

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbb@gmail.com"
		bbbAddressMock.getMobileNumber() >> null
		bbbAddressMock.getCountryName() >> "USA"
		bbbAddressMock.getPostalCode() >> ""
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> null
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getCity() >> "California"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		bbbGetCouponsManagerMock.createWalletMobile('bbb@gmail.com', null, null, null, 'Beverly Hills', 'California', 'Los Angeles', '') >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		testObj.setCheckoutManager(checkoutManagerMock)
		checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock) >> {throw new IntrospectionException("Mock for IntrospectionException")}

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_COUNTRY', 'EN', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_INVALID_COUNTRY',_)
		1 * testObj.logError('BBBBillingAddressFormHandler.saveBillingAddress exception',_)
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
	}


	def "handleSaveBillingAddress. This TC is when BBBSystemException throws in saveBillingAddress private Method"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> false
		testObj.isTransactionMarkedAsRollBack() >> true

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> ""
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> null
		testObj.setConfirmedEmail("bbb@gmail.com")

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbb@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9898998988"
		bbbAddressMock.getCountryName() >> "USA"
		bbbAddressMock.getPostalCode() >> "70003"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getCity() >> "California"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.setCheckoutManager(null)
		bbbOrderImplMock.getBillingAddress() >> null
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 8
		profileToolsMock.getAllBillingAddresses(testObj.getProfile()) >> ["someAddress"]
		testObj.setSiteId("BedBathUS")
		addressAPIMock.addNewBillingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), false) >> {throw new BBBSystemException("Mock for BBBSystemException")}

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
		1 * testObj.logDebug('adding form exception: checkout_1003', null)
	}


	def "handleSaveBillingAddress. This TC is when BBBBusinessException throws in saveBillingAddress private Method"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("DIFFERENT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> false
		//testObj.isTransactionMarkedAsRollBack() >> true

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> null
		testObj.setConfirmedEmail("bbb@gmail.com")

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbb@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9898998988"
		bbbAddressMock.getCountryName() >> "USA"
		bbbAddressMock.getPostalCode() >> "70003"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getCity() >> "California"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.setLoggingError(false)
		bbbGetCouponsManagerMock.createWalletMobile('bbb@gmail.com', null, null, null, 'Beverly Hills', 'California', 'Los Angeles', '70003') >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		testObj.getBillingAddressContainer() >> null
		testObj.setCheckoutManager(null)
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		//for bbbRepositoryContactInfoMock.getMobileNumber coverage because it is final method
		bbbRepositoryContactInfoMock.mRepositoryItem = mutableRepositoryItemMock
		mutableRepositoryItemMock.getItemDescriptor() >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.hasProperty("mobileNumber") >> false

		bbbRepositoryContactInfoMock.getCountry() >> "US"
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		profileToolsMock.getAllBillingAddresses(testObj.getProfile()) >> ["someAddress"]
		testObj.setSiteId("BuyBuyBaby")
		addressAPIMock.addNewBillingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), false) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}

		// addSystemException Method
		def TransactionManager transactionManagerMock = Mock()
		testObj.setTransactionManager(transactionManagerMock)
		transactionManagerMock.getTransaction() >> {throw new SystemException("Mock for SystemException")}

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		0 * testObj.logError('BBBBillingAddressFormHandler.saveBillingAddress exception',_)
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
		2 * bbbRepositoryContactInfoMock.getEmail()
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.logDebug('adding form exception: checkout_1003', null)
		1 * testObj.logError('checkout_1003: Error in marking the transaction rollback',_)
	}

	def "handleSaveBillingAddress. This TC is when RunProcessException throws in rePriceOrder private Method"() {
		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("DIFFERENT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> false
		testObj.isTransactionMarkedAsRollBack() >> true
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> ""
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> null
		testObj.setConfirmedEmail("bbb@gmail.com")

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbb@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9898998988"
		bbbAddressMock.getCountryName() >> "USA"
		bbbAddressMock.getPostalCode() >> "70003"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getCity() >> "California"
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.setLoggingError(false)
		bbbGetCouponsManagerMock.createWalletMobile('bbb@gmail.com', null, null, null, 'Beverly Hills', 'California', '', '70003') >> {throw new BBBSystemException("Mock for BBBSystemException")}
		testObj.setBillingAddressContainer(bbbAddressContainerMock)
		bbbAddressContainerMock.getAddressFromContainer("DIFFERENT") >> bbbAddressMock
		bbbOrderImplMock.getShippingGroups() >> [hardgoodShippingGroupMock]
		hardgoodShippingGroupMock.getId() >> "NEW"
		bbbAddressContainerMock.getDuplicate() >> [bbbAddressMock]
		testObj.setCheckoutManager(null)
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		//for bbbRepositoryContactInfoMock.getMobileNumber coverage because it is final method
		bbbRepositoryContactInfoMock.mRepositoryItem = mutableRepositoryItemMock
		mutableRepositoryItemMock.getItemDescriptor() >> repositoryItemDescriptorMock
		repositoryItemDescriptorMock.hasProperty("mobileNumber") >> false

		bbbRepositoryContactInfoMock.getCountry() >> "US"
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		testObj.setSiteId("BuyBuyBaby")
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"

		//isSchoolPromotion private Method Coverage
		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.setCouponMap(couponMap)

		//postSaveBillingAddress protected method coverage
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BuyBuyBabySiteCode","BuyBuyBabySiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BuyBuyBabySiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_baby") >> ["true"]
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [repositoryItemMock]
		def RepositoryItem activePromotionsMock1 = Mock()
		profileMock.getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) >> [activePromotionsMock,activePromotionsMock1]
		activePromotionsMock.getPropertyValue(BBBCoreConstants.PROMOTION) >> activePromotion
		activePromotionsMock1.getPropertyValue(BBBCoreConstants.PROMOTION) >> activePromotion
		activePromotionsMock.getPropertyValue("coupons") >> [coupons]
		activePromotionsMock1.getPropertyValue("coupons") >> [coupons]
		coupons.getPropertyValue(TBSConstants.STORE_ONLY) >> false
		testObj.setPromotionTools(promotionToolsMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> promotions
		testObj.setCouponUtil(couponUtilMock)
		couponUtilMock.applySchoolPromotion(promotions, testObj.getProfile(), bbbOrderImplMock) >> promotions

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock
		pipelineManagerMock.runProcess('reprice', ['OrderManager':orderManagerMock, 'Order':bbbOrderImplMock, 'Profile':profileMock,'Locale':new Locale("en_US"),'PricingOp':'ORDER_TOTAL']) >> {throw new RunProcessException("Mock for RunProcessException")}

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * profileToolsMock.updateProperty('mobileNumber', '9898998988',profileMock)
		1 * bbbAddressMock.setEmail('bbb@gmail.com')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * promotionToolsMock.initializePricingModels(requestMock,responseMock)
		2 * activePromotion.equals(repositoryItemMock)
		2 * promotionToolsMock.removePromotion(profileMock,activePromotion, false)
		1 * bbbAddressMock.setPhoneNumber('9898998988')
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * bbbAddressMock.setMobileNumber('9898998988')
		1 * testObj.rePriceOrder(bbbOrderImplMock)
		1 * profileMock.setPropertyValue('availablePromotionsList', [repositoryItemMock])
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR', _)
		0 * testObj.logError('BBBBillingAddressFormHandler.saveBillingAddress exception', _)
		2 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Added promotion: {0} to removeAvailablePromotions list to remove from available promotion list: {1}', [activePromotion, [repositoryItemMock]])
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Removed removeAvailablePromotions list: {0} from available promotion list: {1}', [[activePromotion, activePromotion], [repositoryItemMock]])
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Removing removeAvailablePromotions list: {0} from available promotion list: {1}', [[activePromotion, activePromotion], [repositoryItemMock]])
	}

	def "handleSaveBillingAddress. This TC is when siteId is TBS_BedBathUS and country is US"() {

		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "US"
		testObj.setSiteId("TBS_BedBathUS")
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> "70001"
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * testObj.logDebug('phone:9898989898', null)
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * profileToolsMock.updateProperty('mobileNumber', '9797979797', profileMock)
		1 * testObj.getUserLocale(requestMock,responseMock)
		1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', null, null, 'Beverly Hills', '', 'Los Angeles', '70001')
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid phone number', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * testObj.logDebug('invalid phone:Invalid phone number', null)
	}


	def "handleSaveBillingAddress. This TC is when siteId is TBS_BuyBuyBaby and Country is US"() {

		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "US"
		testObj.setSiteId("TBS_BuyBuyBaby")
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> ""
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid phone number', null)
		1 * testObj.logDebug('invalid phone:Invalid phone number', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * profileToolsMock.updateProperty('mobileNumber', '9797979797', profileMock)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', null, null, 'Beverly Hills', '', 'Los Angeles', '')
	}

	def "handleSaveBillingAddress. This TC is when siteId is BedBathCanada and Country is US"() {

		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> true
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "US"
		testObj.setSiteId("BedBathCanada")
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> ""
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * testObj.logDebug('phone:9898989898', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid phone number', null)
		1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', null, null, 'Beverly Hills', '', 'Los Angeles', '')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * sessionMock.setAttribute('internationalCreditCard', 'true')
		1 * profileToolsMock.updateProperty('mobileNumber', null, profileMock)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
	}

	def "handleSaveBillingAddress. This TC is when country is IND"() {

		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "IND"
		testObj.setSiteId("BedBathCanada")
		testObj.setSaveToAccount(FALSE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> ""
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * testObj.logDebug('invalid phone:Invalid phone number', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * sessionMock.setAttribute('internationalCreditCard', 'true')
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * testObj.logDebug('phone:9898989898', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
	}

	def "handleSaveBillingAddress. This TC is when siteId is BedBathUS and country is Canada"() {

		given:
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "Canada"
		testObj.setSiteId("BedBathUS")
		testObj.setSaveToAccount(FALSE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> ""
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * sessionMock.setAttribute('internationalCreditCard', 'true')
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid phone number', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
	}

	def "handleSaveBillingAddress. This TC is when saveToAccount is false"() {

		given:

		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "CA"
		testObj.setSiteId("BedBathCanada")
		testObj.setSaveToAccount(FALSE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> "Beverly Hills"
		bbbAddressMock.getPostalCode() >> ""
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid phone number', null)
	}

	def "handleSaveBillingAddress. This TC is when address1 is empty"() {

		given:

		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption("EDIT")
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> "atg-rest-redirect"
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		testObj.checkFormRedirect(null,_, requestMock, responseMock) >> false

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		//requestMock.getParameter("isInternationalBilling") >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		Map<String, String> cnameMap = new HashMap<String, String>()
		cnameMap.put("US" , "NationalLouisUniversity")
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
		bbbAddressMock.getInvalidProperties() >> ["Invalid email","Invalid phone number"]
		bbbAddressMock.getPhoneNumber() >> "9898989898"

		//saveBillingAddress private Method Coverage
		testObj.setProfile(profileMock)
		bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		bbbAddressMock.getMobileNumber() >> "9797979797"
		bbbAddressMock.getCountryName() >> "USA"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "true"
		profileMock.isTransient() >> false
		testObj.setProfileTools(profileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		bbbRepositoryContactInfoMock.getEmail() >> "customer@gmail.com"
		bbbRepositoryContactInfoMock.getMobileNumber() >> "96969699696"
		testObj.setCheckoutManager(null)
		bbbRepositoryContactInfoMock.getCountry() >> "CA"
		testObj.setSiteId("BedBathCanada")
		testObj.setSaveToAccount(TRUE)
		testObj.setAddressAPI(addressAPIMock)
		profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 2
		bbbAddressMock.getAddress1() >> ""
		bbbAddressMock.getPostalCode() >> ""
		bbbAddressMock.getCity() >> ""

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * testObj.logDebug('invalid phone:Invalid phone number', null)
		1 * testObj.logDebug('phone:9898989898', null)
		1 * testObj.logDebug('Invalid address property. Invalid phone number', null)
		1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid phone number', null)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
	}

	def "handleSaveBillingAddress. This TC is when NumberFormatException throws in updateOrder"() {
		given:

		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setMessageHandler(messageHandlerMock)
		requestMock.getContextPath() >> "/store"
		testObj.setUserSelectedOption(null)
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getCouponListVo() >> null
		testObj.setCheckoutState(checkoutStateMock)
		checkoutStateMock.getFailureURL() >> ""
		testObj.setUserLocale(null)
		testObj.getFormError() >> false
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setOrderManager(orderManagerMock)
		bbbOrderImplMock.isPayPalOrder() >> true
		testObj.isTransactionMarkedAsRollBack() >> true
		orderManagerMock.updateOrder(testObj.getOrder()) >> {throw new NumberFormatException("Mock for NumberFormatException")}

		//preSaveBillingAddress private Method Coverage
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		testObj.setBillingAddress(bbbAddressMock)
		bbbAddressMock.getState() >> "Los Angeles"
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		bbbAddressMock.getCountry() >> "US"
		bbbCatalogToolsImplMock.getCountriesInfo(_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		bbbAddressMock.getInvalidProperties() >> []

		when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * testObj.checkFormRedirect(null, null, requestMock,responseMock)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
		1 * testObj.getUserLocale(requestMock,responseMock)
		1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_INVALID_COUNTRY',_)
		1 * testObj.logError('checkout_1002: Adding Form Error : ERR_CHECKOUT_NULL_BILLING_ADDRESS', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_NULL_BILLING_ADDRESS', 'EN', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_BILLING_ADDRESS', 'EN', null)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_COUNTRY', 'EN', null)

	}

	////////////////////////////////////TCs for handleSaveBillingAddress ends////////////////////////////////////////

	/////////////////////////////////TCs for postSaveBillingAddress starts////////////////////////////////////////
	//Signature : protected void postSaveBillingAddress(final DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) ////

	def "postSaveBillingAddress. This TC is when ordersiteId is BedBathCanadaSiteCode"() {

		given:

		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathCanadaSiteCode","BedBathCanadaSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathCanadaSiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_ca") >> ["true"]

		requestMock.getSession().getAttribute("couponMailId") >> ""
		profileMock.isTransient() >> true

		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [repositoryItemMock]
		profileMock.getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) >> []
		coupons.getPropertyValue(TBSConstants.STORE_ONLY) >> false
		testObj.setPromotionTools(promotionToolsMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> promotions
		testObj.setCouponUtil(couponUtilMock)
		couponUtilMock.applySchoolPromotion(promotions, testObj.getProfile(), bbbOrderImplMock) >> promotions
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock

		1 * promotionToolsMock.initializePricingModels(requestMock, responseMock)
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Removing removeAvailablePromotions list: {0} from available promotion list: {1}', [[], [repositoryItemMock]])
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Removed removeAvailablePromotions list: {0} from available promotion list: {1}', [[], [repositoryItemMock]])
		1 * checkoutStateMock.setCurrentLevel('COUPONS')
		1 * testObj.rePriceOrder(bbbOrderImplMock)
		1 * profileMock.setPropertyValue('availablePromotionsList', [repositoryItemMock])

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)

	}

	def "postSaveBillingAddress. This TC is when ordersiteId is BedBathUS"() {

		given:
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathCanadaSiteCode","BedBathCanadaSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUS"
		profileMock.isTransient() >> true
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [repositoryItemMock]
		testObj.setCouponUtil(couponUtilMock)
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		1 * checkoutStateMock.setCurrentLevel('COUPONS')
		1 * sessionMock.getAttribute('couponMailId')

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)

	}

	def "postSaveBillingAddress. This TC is when BBBException throws in postSaveBillingAddress"() {
		given:
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		bbbOrderImplMock.getCouponMap() >> couponMap
		bbbOrderImplMock.getCouponListVo() >> []
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathUSSiteCode","BedBathUSSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUSSiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_us") >> ["true"]
		requestMock.getSession().getAttribute("couponMailId") >> ""
		profileMock.isTransient() >> true

		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [repositoryItemMock]
		profileMock.getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) >> [activePromotionsMock]
		activePromotionsMock.getPropertyValue(BBBCoreConstants.PROMOTION) >> activePromotion
		activePromotionsMock.getPropertyValue("coupons") >> [coupons]
		coupons.getPropertyValue(TBSConstants.STORE_ONLY) >> true
		testObj.setPromotionTools(promotionToolsMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> {throw new BBBException("Mock for BBBException")}
		testObj.setCouponUtil(couponUtilMock)
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock

		1 * checkoutStateMock.setCurrentLevel('PAYMENT')
		1 * profileMock.setPropertyValue('availablePromotionsList', [repositoryItemMock])
		1 * promotionToolsMock.initializePricingModels(requestMock, responseMock)
		1 * pipelineManagerMock.runProcess('reprice', ['OrderManager':orderManagerMock, 'Order':bbbOrderImplMock, 'Profile':profileMock,'Locale':new Locale("en_US"),'PricingOp':'ORDER_TOTAL'])
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Removed removeAvailablePromotions list: {0} from available promotion list: {1}', [[], [repositoryItemMock]])
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: The applied promotion {0} attached to coupon {1} is store only', [activePromotion,coupons])
		1 * testObj.vlogDebug('BBBBillingAddressFormHandler.postSaveBillingAddress: Removing removeAvailablePromotions list: {0} from available promotion list: {1}', [[], [repositoryItemMock]])
		1 * testObj.rePriceOrder(bbbOrderImplMock)
		1 * testObj.logError('checkout_1001: Error findind the coupons', _)

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)
	}

	def "postSaveBillingAddress. This TC is when couponOn returns false"() {

		given:
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathUSSiteCode","BedBathUSSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUSSiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_us") >> ["false"]
		requestMock.getSession().getAttribute("couponMailId") >> ""
		profileMock.isTransient() >> true

		testObj.setPromotionTools(promotionToolsMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> promotions
		testObj.setCouponUtil(couponUtilMock)
		couponUtilMock.applySchoolPromotion(promotions, testObj.getProfile(), bbbOrderImplMock) >> promotions
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock

		1 * checkoutStateMock.setCurrentLevel('COUPONS')
		1 * profileMock.getPropertyValue('availablePromotionsList')
		1 * profileMock.getPropertyValue('activePromotions')
		1 * pipelineManagerMock.runProcess('reprice', ['OrderManager':orderManagerMock, 'Order':bbbOrderImplMock, 'Profile':profileMock,'Locale':new Locale("en_US"),'PricingOp':'ORDER_TOTAL'])

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)

	}

	def "postSaveBillingAddress. This TC is when availablePromotions contains removePromotion"() {

		given:

		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathCanadaSiteCode","BedBathCanadaSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathCanadaSiteCode"
		bbbCatalogToolsImplMock.getContentCatalogConfigration("CouponTag_ca") >> ["true"]

		requestMock.getSession().getAttribute("couponMailId") >> ""
		profileMock.isTransient() >> true
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [activePromotion]
		profileMock.getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) >> [activePromotionsMock]
		activePromotionsMock.getPropertyValue(BBBCoreConstants.PROMOTION) >> activePromotion
		activePromotionsMock.getPropertyValue("coupons") >> [coupons]
		coupons.getPropertyValue(TBSConstants.STORE_ONLY) >> false
		testObj.setPromotionTools(promotionToolsMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>()
		promotionLookupManagerMock.populateValidPromotions(testObj.getProfile(),bbbOrderImplMock, bbbOrderImplMock.getSiteId(), true) >> promotions
		testObj.setCouponUtil(couponUtilMock)
		couponUtilMock.applySchoolPromotion(promotions, testObj.getProfile(), bbbOrderImplMock) >> promotions
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock

		1 * checkoutStateMock.setCurrentLevel('COUPONS')
		1 * promotionToolsMock.initializePricingModels(requestMock, responseMock)
		1 * promotionToolsMock.removePromotion(profileMock, activePromotion, false)
		1 * pipelineManagerMock.runProcess('reprice', ['OrderManager':orderManagerMock, 'Order':bbbOrderImplMock, 'Profile':profileMock,'Locale':new Locale("en_US"),'PricingOp':'ORDER_TOTAL'])
		2 * activePromotion.equals(activePromotion)

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)

	}

	def "postSaveBillingAddress. This TC is when couponMailId is not blank"() {

		given:
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		requestMock.getSession().getAttribute("couponMailId") >> "coupon"
		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathCanadaSiteCode","BedBathCanadaSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUS"
		profileMock.isTransient() >> true
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [repositoryItemMock]
		testObj.setCouponUtil(couponUtilMock)
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		1 * checkoutStateMock.setCurrentLevel('COUPONS')

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)

	}

	def "postSaveBillingAddress. This TC is when active Promotions returns null"() {
		given:
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCatalogTools(bbbCatalogToolsImplMock)
		testObj.setProfile(profileMock)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setPromotionTools(promotionToolsMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		testObj.setPromotionLookupManager(promotionLookupManagerMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true

		requestMock.getSession().getAttribute("couponMailId") >> ""
		Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		couponMap.put("schoolPromo",repositoryItemMock)
		bbbOrderImplMock.getCouponMap() >> couponMap
		requestMock.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED) >> "false"
		Map<String, String> configMap = new HashMap<String, String>()
		configMap.put("BedBathCanadaSiteCode","BedBathCanadaSiteCode")
		bbbCatalogToolsImplMock.getConfigValueByconfigType("ContentCatalogKeys") >> configMap
		bbbOrderImplMock.getSiteId() >> "BedBathUS"
		profileMock.isTransient() >> true
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [activePromotion]
		profileMock.getPropertyValue(BBBCoreConstants.ACTIVE_PROMOTIONS) >> null
		activePromotionsMock.getPropertyValue(BBBCoreConstants.PROMOTION) >> activePromotion
		activePromotionsMock.getPropertyValue("coupons") >> [coupons]
		testObj.setCouponUtil(couponUtilMock)
		testObj.setUserSelectedOption("NEW")
		testObj.setCollegeAddress("")

		//rePriceOrder private method Coverage
		testObj.setUserLocale(new Locale("en_US"))
		testObj.setRepriceOrderChainId("reprice")
		testObj.setPipelineManager(pipelineManagerMock)
		orderManagerMock.getPipelineManager() >> pipelineManagerMock

		1 * checkoutStateMock.setCurrentLevel('COUPONS')
		1 * promotionToolsMock.initializePricingModels(requestMock,responseMock)
		1 * profileMock.setPropertyValue('availablePromotionsList', [activePromotion])
		1 * couponUtilMock.applySchoolPromotion(null, profileMock,bbbOrderImplMock)
		1 * promotionLookupManagerMock.populateValidPromotions(profileMock,bbbOrderImplMock, 'BedBathUS', true)
		1 * pipelineManagerMock.runProcess('reprice', ['OrderManager':orderManagerMock, 'Order':bbbOrderImplMock, 'Profile':profileMock,'Locale':new Locale("en_US"),'PricingOp':'ORDER_TOTAL'])

		expect:
		testObj.postSaveBillingAddress(requestMock, responseMock)

	}

	////////////////////////////////////TCs for postSaveBillingAddress ends////////////////////////////////////////


	/////////////////////////////////TCs for handleAddBillingAddressToOrder starts////////////////////////////////////////
	//Signature : public final boolean handleAddBillingAddressToOrder(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////

	def "handleAddBillingAddressToOrder. This TC is happy flow of handleAddBillingAddressToOrder"() {

		given:
		addBillingAddressSpy()
		testObj.ensureTransaction() >> transactionMock
		bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true

		when:
		def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock)
		1 * testObj.commitTransaction(transactionMock)
		1 * repeatingRequestMonitorMock.isUniqueRequestEntry('getStackTrace')
		1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
	}

	def "handleAddBillingAddressToOrder. This TC is when checkGiftCard method returns false"() {

		given:
		addBillingAddressSpy()
		bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> false


		when:
		def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCurrentLevel('BILLING')
	}

	def "handleAddBillingAddressToOrder. This TC is when CommerceException throws"() {

		given:
		addBillingAddressSpy()
		bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
		bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}

		testObj.setTransactionManager(transactionManagerMock)
		transactionManagerMock.getTransaction() >> {throw new SystemException("Mock for SystemException")}

		when:
		def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * testObj.logError('cart_1021: Error in marking the transaction rollback',_)
		1 * testObj.logError('handleAddBillingAddressToOrder :: repriceGiftCard-Error updating in Commerce Item in repository',_)
		1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCurrentLevel('BILLING')
	}

	def "handleAddBillingAddressToOrder. This TC is when CommerceException throws and isTransactionMarkedAsRollBack is true"() {

		given:
		addBillingAddressSpy()
		testObj.ensureTransaction() >> null

		bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
		bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
		testObj.isTransactionMarkedAsRollBack() >> true

		when:
		def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		0 * testObj.logError('cart_1021: Error in marking the transaction rollback',_)
		1 * testObj.logError('handleAddBillingAddressToOrder :: repriceGiftCard-Error updating in Commerce Item in repository',_)
	}

	def "handleAddBillingAddressToOrder. This TC is when setTransactionToRollbackOnly does not return exception"() {

		given:
		addBillingAddressSpy()
		testObj.ensureTransaction() >> transactionMock
		bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
		bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
		testObj.setTransactionManager(transactionManagerMock)

		when:
		def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
		1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect',
			'COUPONS':'atg-rest-ignore-redirect', 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect',
			'CART':'atg-rest-ignore-redirect', 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
		1 * checkoutStateMock.setCurrentLevel('BILLING')
		0 * testObj.logError('cart_1021: Error in marking the transaction rollback',_)
		1 * testObj.logError('handleAddBillingAddressToOrder :: repriceGiftCard-Error updating in Commerce Item in repository',_)
	}

	private addBillingAddressSpy() {
		testObj.setCheckoutState(checkoutStateMock)
		testObj.setPaymentGroupManager(bbbPaymentGroupManager)
		testObj.setOrder(bbbOrderImplMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		checkoutFailureURL.put("failure","url")
		checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
		checkoutSuccessURL.put("Success","url")
		checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
		testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
		testObj.setProfile(profileMock)
		profileMock.getRepositoryId() >> "p12345"
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getCouponListVo() >> []
	}

	////////////////////////////////////TCs for handleAddBillingAddressToOrder ends////////////////////////////////////////

	/////////////////////////////////TCs for handleSetEmailSignUpInOrder starts////////////////////////////////////////
	//Signature : public final boolean handleSetEmailSignUpInOrder(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////

	def "handleSetEmailSignUpInOrder. This TC is the Happy flow of handleSetEmailSignUpInOrder"() {

		given:
		testObj.ensureTransaction() >> transactionMock
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setOrderManager(orderManagerMock)
		testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
		repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> TRUE

		when:
		def booleanResult = testObj.handleSetEmailSignUpInOrder(requestMock, responseMock)

		then:
		booleanResult == TRUE
		1 * orderManagerMock.updateOrder(bbbOrderImplMock)
		1 * testObj.commitTransaction(transactionMock)
		1 * repeatingRequestMonitorMock.removeRequestEntry('getStackTrace')
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
	}

	def "handleSetEmailSignUpInOrder. This TC is when CommerceException throws"() {

		given:
		testObj.ensureTransaction() >> transactionMock
		testObj.setOrder(bbbOrderImplMock)
		testObj.setEmailSignUp(TRUE)
		testObj.setOrderManager(orderManagerMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setRepeatingRequestMonitor(null)
		testObj.isTransactionMarkedAsRollBack() >> TRUE
		testObj.setMessageHandler(messageHandlerMock)
		orderManagerMock.updateOrder(testObj.getOrder()) >>  {throw new CommerceException("Mock for CommerceException")}

		when:
		def booleanResult = testObj.handleSetEmailSignUpInOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * messageHandlerMock.getErrMsg('err_setting_email_signup_in_order', 'EN', null)
		1 * testObj.logError('checkout_1003: Adding Form Error : err_setting_email_signup_in_order',_)
		1 * testObj.commitTransaction(transactionMock)
		1 * bbbOrderImplMock.setPropertyValue('emailSignUp', true)
	}

	def "handleSetEmailSignUpInOrder. This TC is when isUniqueRequestEntry returns false "() {

		given:
		testObj.setOrderManager(orderManagerMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
		repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> FALSE
		testObj.isTransactionMarkedAsRollBack() >> TRUE

		when:
		def booleanResult = testObj.handleSetEmailSignUpInOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * repeatingRequestMonitorMock.isUniqueRequestEntry('getStackTrace')
	}

	def "handleSetEmailSignUpInOrder. This TC is when order is null"() {
		given:
		testObj.setOrderManager(orderManagerMock)
		testObj.setOrder(null)
		testObj.setCommonConfiguration(commonConfigurationMock)
		commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
		testObj.isTransactionMarkedAsRollBack() >> TRUE

		when:
		def booleanResult = testObj.handleSetEmailSignUpInOrder(requestMock, responseMock)

		then:
		booleanResult == FALSE
		1 * testObj.getRepeatingRequestMonitor()
	}


	////////////////////////////////////TCs for handleSetEmailSignUpInOrder ends////////////////////////////////////////


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
