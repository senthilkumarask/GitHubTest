package com.bbb.commerce.checkout.formhandler

import atg.commerce.CommerceException
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroup
import atg.commerce.order.SimpleOrderManager
import atg.commerce.order.purchase.PurchaseProcessFormHandler
import atg.commerce.promotion.PromotionTools
import atg.commerce.util.RepeatingRequestMonitor
import atg.core.util.Address
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineManager
import atg.service.pipeline.RunProcessException
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import atg.userprofiling.address.AddressTools;

import com.bbb.account.BBBGetCouponsManager
import com.bbb.account.BBBProfileManager
import com.bbb.account.BBBProfileTools
import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressAPIImpl
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.common.BBBAddress
import com.bbb.commerce.common.BBBAddressContainer
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBPaymentGroupManager
import com.bbb.commerce.order.ManageCheckoutLogging
import com.bbb.commerce.order.manager.PromotionLookupManager
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.repository.RepositoryItemMock;
import com.bbb.utils.CommonConfiguration

import java.beans.IntrospectionException
import java.util.List;
import java.util.Map;

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
class BBBSPBillingAddressFormHandlerSpecification extends BBBExtendedSpec {
	
	def BBBSPBillingAddressFormHandler testObj
	def PromotionLookupManager promotionLookupManagerMock = Mock()
	def CheckoutProgressStates checkoutStateMock = Mock()
	def CommonConfiguration commonConfigurationMock = Mock()
	def PromotionTools promotionToolsMock = Mock()
	def LblTxtTemplateManager messageHandlerMock = Mock()
	def ManageCheckoutLogging manageLoggingMock = Mock()
	def BBBProfileManager profileManagerMock = Mock()
	def BBBGetCouponsManager bbbGetCouponsManagerMock = Mock()
	def Profile userProfileMock = Mock()
	def BBBSessionBean sessionBeanMock = Mock()
	def BBBRepositoryContactInfo bbbRepoContInfoMock = Mock()
	def BBBPropertyManager propertyManagerMock = Mock()
	def BBBProfileTools profileToolsMock = Mock()
	def BBBCheckoutManager checkoutManagerMock = Mock()
	def BBBAddressContainer billingAddressContainerMock = Mock()
	def BBBAddressAPI addressAPIMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBCouponUtil couponUtilMock = Mock()
	def BBBOrderImpl bbbOrderImplMock = Mock()
	def BBBOrder bbborderMock = Mock()
	def Order orderMock = Mock()
	def BBBOrderManager bbbOrderManagerMock = Mock()
	def RepositoryItem repositoryItemMock = Mock()
	def SimpleOrderManager simpleOrderManagerMock = Mock()
	def BBBCatalogToolsImpl bbbCatalogToolsImplMock = Mock()
	def BBBRepositoryContactInfo bbbRepositoryContactInfoMock = Mock()
	def BBBAddress bbbAddressMock = Mock()
	def Profile profileMock = Mock()
	def HardgoodShippingGroup hardgoodShippingGroupMock = Mock()
	def PipelineManager pipelineManagerMock = Mock()
	def RepeatingRequestMonitor repeatingRequestMonitorMock = Mock()
	def BBBPaymentGroupManager bbbPaymentGroupManager = Mock()
	def RepositoryItem profileRepositoryItemMock = Mock()
	def BBBAddressAPIImpl bbbAddressAPIImplMock = Mock()
	def BBBAddressImpl bbbAddressImplMock = Mock()
	def Transaction transactionMock = Mock()
	
	
	def setup() {
		testObj = new BBBSPBillingAddressFormHandler(order:bbbOrderImplMock,checkoutState:checkoutStateMock, messageHandler:messageHandlerMock, manageLogging:manageLoggingMock, 
			commonConfiguration:commonConfigurationMock, promotionLookupManager:promotionLookupManagerMock,promotionTools:promotionToolsMock,userProfile:userProfileMock,
			sessionBean:sessionBeanMock,couponUtil:couponUtilMock,propertyManager:propertyManagerMock,profileTools:profileToolsMock,addressAPI:addressAPIMock,
			profileManager:profileManagerMock)
	}
	
	
	/////////////////////////////////TCs for setOrderAddress starts////////////////////////////////////////
	//Signature : public final void setOrderAddress(final BBBRepositoryContactInfo bbbRepoContInfo) ////
	
	def "setOrderAddress. This TC is the happy flow of setOrderAddress"() {
		
		given:
			testObj = Spy()
			def MutableRepositoryItem mRepositoryItemMock = Mock()
			mRepositoryItemMock.setPropertyValue("address1", "abc")
			mRepositoryItemMock.setPropertyValue("address2", "abc")
			bbbRepoContInfoMock.getRepositoryItem() >> mRepositoryItemMock
			testObj.copyContactAddress(*_) >> null
			testObj.setBillingAddress(bbbAddressImplMock);
	
		when:
			testObj.setOrderAddress(bbbRepoContInfoMock)
			
		then:
			bbbAddressImplMock.getAddress1()==null
			bbbAddressImplMock.getAddress2()==null
		
	}
	
	def "setOrderAddress. This TC is when bbbRepoContInfo is null"() {
		
		given:
			testObj = Spy()
			testObj.setManageLogging(manageLoggingMock)
			manageLoggingMock.setBillingAddressHandlerLogging(true)
			1 * testObj.logInfo("bbbRepoContInfo is null due to session time out")
			1 * testObj.logInfo("bbbRepoContInfo is null due to session time out", null);
			
		expect:
			testObj.setOrderAddress(null)
	}
	
	def "setOrderAddress. This TC is when exception occurs"() {
		
		given:
			testObj = Spy()
			testObj.copyContactAddress(*_) >> {throw new IntrospectionException("Mock for Introspection Exception")}
			1 * testObj.logError('checkout_1000: copying order billing Address to handler failed',_)
		
		expect:
			testObj.setOrderAddress(bbbRepoContInfoMock)
	}
	
	////////////////////////////////////TCs for setOrderAddress ends////////////////////////////////////////
	
	
	/////////////////////////////////TCs for handleSaveBillingAddress starts////////////////////////////////////////
	//Signature : public final boolean handleSaveBillingAddress(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) ////
	
	
	def "handleSaveBillingAddress. This TC is the happy flow of handleSaveBillingAddress with preSaveBillingAddress private method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("EDIT")
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore"
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			bbbAddressMock.getCountry() >> "US"
			
			//preSaveBillingAddress private method Coverage
			Map<String, String> cnameMap = new HashMap<String, String>()
			cnameMap.put("US" , "NationalLouisUniversity")
			bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(repositoryItemMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "90210"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid phone number"]
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
			1 * bbbAddressMock.setMobileNumber('9898989898')
			1 * bbbAddressMock.setCountryName('NationalLouisUniversity')
			1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', "9898989898" , 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '90210')
			1 * messageHandlerMock.getErrMsg("ERR_CHECKOUT_INVALID_INPUT", BBBCoreConstants.DEFAULT_LOCALE, null)
			booleanResult == false
	   }
	
	def "handleSaveBillingAddress. This TC is when UserSelectedOption is NEW and deals with private methods and couponmap is empty"() {
	 
	 given:
		 testObj.setCatalogTools(bbbCatalogToolsImplMock)
		 testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
		 requestMock.getContextPath() >> "/store"
		 testObj.setUserSelectedOption("NEW")
		 checkoutStateMock.getFailureURL() >> ""
		 testObj.getBillingAddress() >> bbbAddressMock
		 testObj.setBillingAddress(bbbAddressMock)
		 testObj.setCheckoutManager(checkoutManagerMock)
		 testObj.setOrderManager(simpleOrderManagerMock)
		 
		 //preSaveBillingAddress private method Coverage
		 bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
		 bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
		 bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
		 testObj.setProfile(profileMock)
		 repositoryItemMock.isTransient() >> true
		 bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
		 bbbAddressMock.getMobileNumber() >> "9898989898"
		 bbbAddressMock.getPostalCode() >> "70001"
		 bbbAddressMock.getFirstName() >> "John"
		 bbbAddressMock.getLastName() >> "Kennady"
		 bbbAddressMock.getAddress1() >> "Beverly Hills"
		 bbbAddressMock.getCity() >> "California"
		 bbbAddressMock.getState() >> "Los Angeles"
		 bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
		 testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true  
		 
		 //saveBillingAddress private method Coverage
		 bbbAddressMock.getCountryName() >> "USA"
		 bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
		 bbbRepositoryContactInfoMock.getCountry() >> "US"
		 testObj.setSiteId("BedBathUS")
		 testObj.setSaveToAccount(true)
		 profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
		 profileMock.isTransient() >> true
		 testObj.setProfileTools(profileToolsMock)
		 profileToolsMock.getAllBillingAddresses(_) >> []
		 testObj.setUpdateAddress(false)
		 addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
		 testObj.setBillingAddressContainer(billingAddressContainerMock)
		 billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
		 propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
		 
		 //postSaveBillingAddress private Method Coverage
		 Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
		 bbbOrderImplMock.setCouponMap(couponMap)
		 
		 //rePriceOrder private Method Coverage
		 userProfileMock.getDataSource() >> profileRepositoryItemMock
		 testObj.setRepriceOrderChainId("reprice")
		 testObj.setPipelineManager(pipelineManagerMock)
		 simpleOrderManagerMock.getPipelineManager() >> pipelineManagerMock
	 
	 when:
		def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		
	 then:
	 	booleanResult == true
	 	1 * pipelineManagerMock.runProcess("reprice",_)
		2 * bbbAddressMock.getCountry()
	 	2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
		1 * bbbAddressMock.setMobileNumber('9898989898')
		1 * bbbAddressMock.setState(null)
		1 * bbbAddressMock.setCountryName(null)
		1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
		1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		1 * profileToolsMock.updateProperty('mobileNumber', '9898989898',_)
		1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
		1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	}
	
	def "handleSaveBillingAddress. This TC is when userSelectedOption has different value and couponmap contains value"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("REST")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbAddressMock.getCountry() >> "CA"
			Map<String, String> cnameMap = new HashMap<String, String>()
			bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "90210"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getAddressFromContainer("REST") >> bbbAddressMock
			bbbOrderImplMock.getShippingGroups() >> [hardgoodShippingGroupMock]
			hardgoodShippingGroupMock.getId() >> "REST"
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "CA"
			testObj.setSiteId("BedBathCanada")
			
			//postSaveBillingAddress private method Coverage
			Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
			couponMap.put("10OFF",repositoryItemMock)
			bbbOrderImplMock.getCouponMap() >> couponMap
			testObj.setCollegeAddress("true")
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
			0 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * bbbRepositoryContactInfoMock.setCompanyName('')
			1 * sessionMock.setAttribute('internationalCreditCard', 'false')
			3 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
			1 * bbbAddressMock.getPhoneNumber()
			1 * bbbAddressMock.setPhoneNumber('9898989898')
			1 * bbbAddressMock.setPhoneNumber(null)
			3 * bbbAddressMock.setMobileNumber('9898989898')
	   }
	
	def "handleSaveBillingAddress. This TC is when RunProcessException throws in rePriceOrder private method"() {
		
		given:
			testObj = Spy()
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setOrder(bbbOrderImplMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> ""
			testObj.setUserSelectedOption("REST")
			checkoutStateMock.getFailureURL() >> "null"
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setUserProfile(userProfileMock)
			
			//preSaveBillingAddress private method Coverage
			bbbAddressMock.getCountry() >> "CA"
			Map<String, String> cnameMap = new HashMap<String, String>()
			bbbCatalogToolsImplMock.getCountriesInfo(_) >> cnameMap
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "90210"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getAddressFromContainer("REST") >> bbbAddressMock
			bbbOrderImplMock.getShippingGroups() >> [hardgoodShippingGroupMock]
			hardgoodShippingGroupMock.getId() >> "REST"
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "CA"
			testObj.setSiteId("BedBathCanada")
			
			//rePriceOrder private method Coverage
			userProfileMock.getDataSource() >> profileRepositoryItemMock
			testObj.setRepriceOrderChainId("reprice")
			testObj.setPipelineManager(pipelineManagerMock)
			simpleOrderManagerMock.getPipelineManager() >> pipelineManagerMock
			pipelineManagerMock.runProcess(*_) >> {throw new RunProcessException("Mock for RunProcessException")}
			
			// addSystemException Method
			def TransactionManager transactionManagerMock = Mock()
			testObj.setTransactionManager(transactionManagerMock)
			transactionManagerMock.getTransaction() >> {throw new SystemException("Mock for SystemException")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * testObj.getUserLocale(requestMock , responseMock)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
			0 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
			1 * sessionMock.setAttribute('internationalCreditCard', 'false')
			3 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
			1 * bbbAddressMock.setPhoneNumber('9898989898')
			3 * bbbAddressMock.setMobileNumber('9898989898')
			1 * bbbAddressMock.setPhoneNumber(null)
			2 * profileMock.isTransient()
			1 * testObj.setUserLocale(_)
			3 * testObj.checkFormRedirect(null, 'null', requestMock, responseMock)
			1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
			1 * testObj.logError('checkout_1003: Error in marking the transaction rollback',_)
	   }
	
	
	def "handleSaveBillingAddress. This TC is when IntrospectionException occurs in saveBillingAddress private method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("REST")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbAddressMock.getCountry() >> "Canada"
			bbbCatalogToolsImplMock.getCountriesInfo(_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "90210"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getAddressFromContainer("REST") >> bbbAddressMock
			bbbOrderImplMock.getShippingGroups() >> [billingAddressContainerMock]
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock) >> {throw new IntrospectionException("Mock for IntrospectionException")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
			2 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when RepositoryException occurs in saveBillingAddress private method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("REST")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbAddressMock.getCountry() >> "US"
			bbbCatalogToolsImplMock.getCountriesInfo(_) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "90210"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			testObj.setBillingAddressContainer(null)
			billingAddressContainerMock.getAddressFromContainer("REST") >> bbbAddressMock
			bbbOrderImplMock.getShippingGroups() >> [billingAddressContainerMock]
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock) >> {throw new RepositoryException("Mock for RepositoryException")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when BBBSystemException thrown in preSaveBillingAddress private method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("EDIT")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbAddressMock.getCountry() >> "US"
			bbbCatalogToolsImplMock.getCountriesInfo(_) >> null
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			profileMock.isTransient() >>> [true,true,false]
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "90210"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', "9898989898" , 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '90210') >> {throw new BBBSystemException("Mock for BBBSystemException")}
			bbbAddressMock.getInvalidProperties() >> ["Invalid email"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			testObj.setBillingAddressContainer(null)
			billingAddressContainerMock.getAddressFromContainer("REST") >> bbbAddressMock
			bbbOrderImplMock.getShippingGroups() >> [billingAddressContainerMock]
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "Canada"
			testObj.setSiteId("BedBathCanada")
			testObj.setSaveToAccount(true)
			testObj.getAddressAPI() >> null
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 5
			
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
			2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			0 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * sessionMock.setAttribute('internationalCreditCard', 'false')
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when internationalCard in saveBillingAddress method is true and getCollegeAddress in postSaveBillingAddress is false"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("EDIT")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> ""
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address2"]
			bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '') >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "India"
			testObj.setSiteId("BedBathUS")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			//postSaveBillingAddress private Method Coverage
			Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
			bbbOrderImplMock.setCouponMap(couponMap)
			testObj.setCollegeAddress("false")
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
		   1 * bbbAddressMock.getPhoneNumber()
		   2 * bbbAddressMock.getCountry()
		   2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * profileToolsMock.updateProperty('mobileNumber', null,_)
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
		   1 * sessionMock.setAttribute('internationalCreditCard', 'true')
	   }
	
	def "handleSaveBillingAddress. This TC is when executing addNewBillingAddress in saveBillingAddress private Method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> []
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true
			bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001') >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			testObj.setLoggingError(false)
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BuyBuyBaby")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> ["someData"]
			testObj.setUpdateAddress(false)
			addressAPIMock.addNewBillingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), false) >> bbbAddressMock
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			//postSaveBillingAddress private Method Coverage
			Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
			bbbOrderImplMock.setCouponMap(couponMap)
			
			//rePriceOrder private Method Coverage
			userProfileMock.getDataSource() >> profileRepositoryItemMock
			testObj.setRepriceOrderChainId("reprice")
			testObj.setPipelineManager(pipelineManagerMock)
			simpleOrderManagerMock.getPipelineManager() >> pipelineManagerMock
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
		   1 * pipelineManagerMock.runProcess("reprice",_)
		   2 * bbbAddressMock.getCountry()
		   2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
		   1 * profileToolsMock.updateProperty('mobileNumber', '9898989898',_)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when isUpdateAddress is true in saveBillingAddress private Method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getAddress2() >> "Near Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getCountry() >> "US"
			bbbAddressMock.getCompanyName() >> "BBB" 
			bbbAddressMock.getInvalidProperties() >> null
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true
			bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001') >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.setLoggingError(false)
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			testObj.setSiteId("BuyBuyBaby")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> null
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> ["someData"]
			testObj.setUpdateAddress(true)
			def RepositoryItem shipppingRepositoryItemMock = Mock()
			def RepositoryItem billingRepositoryItemMock = Mock()
			shipppingRepositoryItemMock.getRepositoryId() >> "S232323"
			billingRepositoryItemMock.getRepositoryId() >> "B23232"
			profileToolsMock.getDefaultShippingAddress(testObj.getProfile()) >> shipppingRepositoryItemMock
			profileToolsMock.getDefaultBillingAddress(testObj.getProfile()) >> billingRepositoryItemMock 
			testObj.setCurrentAddressID("S232323")
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			//postSaveBillingAddress private Method Coverage
			Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
			bbbOrderImplMock.setCouponMap(couponMap)
			
			//rePriceOrder private Method Coverage
			userProfileMock.getDataSource() >> profileRepositoryItemMock
			testObj.setRepriceOrderChainId("reprice")
			testObj.setPipelineManager(pipelineManagerMock)
			simpleOrderManagerMock.getPipelineManager() >> pipelineManagerMock
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
		   1 * pipelineManagerMock.runProcess("reprice",_)
		   3 * bbbAddressMock.getCountry()
		   2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
		   1 * profileToolsMock.updateProperty('mobileNumber', '9898989898',_)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
		   1 * profileManagerMock.updateAddressForProfile(_, ['lastName':'Kennady', 'postalCode':'70001', 'poBoxAddress':false, 'nickname':null, 'qasValidated':false, 'state':'Los Angeles', 'address1':'Beverly Hills', 'address2':'Near Hills', 'companyName':'BBB', 'firstName':'John', 'city':'California', 'country':null], true, false, false, 'nickname', 'newNickname')
		   1 * bbbRepositoryContactInfoMock.getCountry()
	   }

	
	def "handleSaveBillingAddress. This TC is when TransactionDemarcationException throws in saveBillingAddress private Method"() {
		
		given:
			testObj = Spy()
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileManager(profileManagerMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getAddress2() >> "Near Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getCountry() >> "US"
			bbbAddressMock.getCompanyName() >> "BBB"
			bbbAddressMock.getInvalidProperties() >> null
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BuyBuyBaby")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> ["someData"]
			testObj.setUpdateAddress(true)
			def RepositoryItem shipppingRepositoryItemMock = Mock()
			def RepositoryItem billingRepositoryItemMock = Mock()
			shipppingRepositoryItemMock.getRepositoryId() >> "S232323"
			billingRepositoryItemMock.getRepositoryId() >> "B23232"
			profileToolsMock.getDefaultShippingAddress(testObj.getProfile()) >> shipppingRepositoryItemMock
			profileToolsMock.getDefaultBillingAddress(testObj.getProfile()) >> billingRepositoryItemMock
			testObj.setCurrentAddressID("B23232")
			profileManagerMock.updateAddressForProfile(*_) >> {throw new TransactionDemarcationException("")} 
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			//postSaveBillingAddress private Method Coverage
			Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
			bbbOrderImplMock.setCouponMap(couponMap)
			
			//rePriceOrder private Method Coverage
			userProfileMock.getDataSource() >> profileRepositoryItemMock
			testObj.setRepriceOrderChainId("reprice")
			testObj.setPipelineManager(pipelineManagerMock)
			simpleOrderManagerMock.getPipelineManager() >> pipelineManagerMock
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
		   1 * testObj.logError('Error in updating profile address',_);
		   1 * pipelineManagerMock.runProcess("reprice",_)
		   3 * bbbAddressMock.getCountry()
		   2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
		   1 * bbbAddressMock.setCountryName(null)
		   1 * bbbAddressMock.setState(null)
		   1 * requestMock.getRequestLocale()
		   1 * profileToolsMock.updateProperty('mobileNumber', '9898989898', profileMock)
		   1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   
	   }
	
	def "handleSaveBillingAddress. This TC is when TransactionDemarcationException throws and loggingerror is false in saveBillingAddress private Method"() {
		
		given:
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setLoggingError(false)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> ""
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getAddress2() >> "Near Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getCountry() >> "US"
			bbbAddressMock.getCompanyName() >> "BBB"
			bbbAddressMock.getInvalidProperties() >> null
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BuyBuyBaby")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> ["someData"]
			testObj.setUpdateAddress(true)
			def RepositoryItem shipppingRepositoryItemMock = Mock()
			def RepositoryItem billingRepositoryItemMock = Mock()
			shipppingRepositoryItemMock.getRepositoryId() >> "S232323"
			billingRepositoryItemMock.getRepositoryId() >> "B23232"
			profileToolsMock.getDefaultShippingAddress(testObj.getProfile()) >> shipppingRepositoryItemMock
			profileToolsMock.getDefaultBillingAddress(testObj.getProfile()) >> billingRepositoryItemMock
			testObj.setCurrentAddressID("B23232")
			profileManagerMock.updateAddressForProfile(*_) >> {throw new TransactionDemarcationException("")}
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			//postSaveBillingAddress private Method Coverage
			Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>()
			bbbOrderImplMock.setCouponMap(couponMap)
			
			//rePriceOrder private Method Coverage
			userProfileMock.getDataSource() >> profileRepositoryItemMock
			testObj.setRepriceOrderChainId("reprice")
			testObj.setPipelineManager(pipelineManagerMock)
			simpleOrderManagerMock.getPipelineManager() >> pipelineManagerMock
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
		   1 * pipelineManagerMock.runProcess("reprice",_)
		   3 * bbbAddressMock.getCountry()
		   2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
		   1 * bbbAddressMock.setCountryName(null)
		   1 * bbbAddressMock.setState(null)
		   1 * requestMock.getRequestLocale()
		   1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   
	   }
	
	def "handleSaveBillingAddress. This TC is when BBBSystemException throws in saveBillingAddress private method"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BedBathUS")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			testObj.setAddressAPI(addressAPIMock)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			0 * pipelineManagerMock.runProcess("reprice",_)
		    2 * bbbAddressMock.getCountry()
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
			1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
			1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
			1 * bbbAddressMock.setMobileNumber('9898989898')
			0 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
			1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when NumberFormatException throws in saveBillingAddress private method"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BedBathUS")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			testObj.setAddressAPI(addressAPIMock)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> {throw new NumberFormatException("Mock for NumberFormatException")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			0 * pipelineManagerMock.runProcess("reprice",_)
			2 * bbbAddressMock.getCountry()
			1 * testObj.logError('checkout_1002: Adding Form Error : ERR_CHECKOUT_INVALID_BILLING_ADDRESS',_)
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_BILLING_ADDRESS', 'EN', null)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString());
			1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
			1 * bbbAddressMock.setMobileNumber('9898989898')
			1 * testObj.setUserLocale(_)
			1 * bbbAddressMock.setState(null)
			1 * bbbAddressMock.setCountryName(null)
			0 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
			1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when checkFormRedirect is false after saveBillingAddress private method"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			commonConfigurationMock.isLoggingDebugForRequestScopedComponents() >> true
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> null
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BedBathUS")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> null
			testObj.setUpdateAddress(false)
			testObj.setAddressAPI(addressAPIMock)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
			0 * pipelineManagerMock.runProcess("reprice",_)
			2 * bbbAddressMock.getCountry()
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
			1 * bbbAddressMock.setMobileNumber('9898989898')
			0 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
			1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
			1 * sessionMock.setAttribute('internationalCreditCard', 'false')
			1 * profileToolsMock.updateProperty('mobileNumber', '9898989898',_)
			1 * testObj.logDebug('Invalid address property. Invalid address1', null)
			1 * testObj.logDebug('adding form exception: ERR_CHECKOUT_INVALID_INPUT : Invalid address1', null)
			1 * testObj.logDebug('email:bbbforcustomer@gmail.com', null)
	   }
	
	def "handleSaveBillingAddress. This TC is when BBBBusinessException throws in saveBillingAddress private method"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >> true
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BedBathUS")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			testObj.setAddressAPI(addressAPIMock)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			0 * pipelineManagerMock.runProcess("reprice",_)
			2 * bbbAddressMock.getCountry()
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_GENERIC_ERROR', 'EN', null)
			1 * testObj.logError('checkout_1004: Adding Form Error : ERR_CHECKOUT_GENERIC_ERROR',_)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
			1 * bbbAddressMock.setMobileNumber('9898989898')
			0 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
			1 * bbbGetCouponsManagerMock.createWalletMobile('bbbforcustomer@gmail.com', '9898989898', 'John', 'Kennady', 'Beverly Hills', 'California', 'Los Angeles', '70001')
			1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when country is US and siteId is BedBathCanada in saveBillingAddress private method"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> ""
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "US"
			testObj.setSiteId("BedBathCanada")
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
			
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
		   2 * bbbAddressMock.getCountry()
		   1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'true')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when country is Canada and siteId is BedBathUS"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> ""
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> ""
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "Canada"
			testObj.setSiteId("BedBathUS")
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
			
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
		   2 * bbbAddressMock.getCountry()
		   1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'true')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when getstate is empty in saveBillingAddress"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "California"
			bbbAddressMock.getState() >> ""
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "Canada"
			testObj.setSiteId("BedBathCanada")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
		   2 * bbbAddressMock.getCountry()
		   1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when getcity is empty in saveBillingAddress"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> ""
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "Canada"
			testObj.setSiteId("BedBathCanada")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
		   2 * bbbAddressMock.getCountry()
		   1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock)
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when getPostalCode is empty in saveBillingAddress"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> ""
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> "Beverly Hills"
			bbbAddressMock.getCity() >> "Los"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "Canada"
			testObj.setSiteId("BedBathCanada")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
		   2 * bbbAddressMock.getCountry()
		   1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when getAddress1 is empty in saveBillingAddress"() {
		
		given:
			testObj = Spy()
			testObj.isTransactionMarkedAsRollBack() >> true
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			testObj.setMessageHandler(messageHandlerMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setProfileTools(profileToolsMock)
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setUserProfile(userProfileMock)
		
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setBbbGetCouponsManager(bbbGetCouponsManagerMock)
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("NEW")
			checkoutStateMock.getFailureURL() >> ""
			testObj.getBillingAddress() >> bbbAddressMock
			testObj.setBillingAddress(bbbAddressMock)
			testObj.setCheckoutManager(checkoutManagerMock)
			testObj.setOrderManager(simpleOrderManagerMock)
			
			//preSaveBillingAddress private method Coverage
			bbbOrderImplMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			testObj.setProfile(profileMock)
			repositoryItemMock.isTransient() >> true
			bbbAddressMock.getEmail() >> "bbbforcustomer@gmail.com"
			bbbAddressMock.getMobileNumber() >> "9898989898"
			bbbAddressMock.getPostalCode() >> "70001"
			bbbAddressMock.getFirstName() >> "John"
			bbbAddressMock.getLastName() >> "Kennady"
			bbbAddressMock.getAddress1() >> ""
			bbbAddressMock.getCity() >> "Los"
			bbbAddressMock.getState() >> "Los Angeles"
			bbbAddressMock.getInvalidProperties() >> ["Invalid address1"]
			
			//saveBillingAddress private method Coverage
			bbbAddressMock.getCountryName() >> "USA"
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbRepositoryContactInfoMock.getCountry() >> "Canada"
			testObj.setSiteId("BedBathCanada")
			testObj.setSaveToAccount(true)
			profileMock.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS) >> 3
			profileMock.isTransient() >> true
			testObj.setProfileTools(profileToolsMock)
			profileToolsMock.getAllBillingAddresses(_) >> []
			testObj.setUpdateAddress(false)
			addressAPIMock.addNewShippingAddress(profileMock, testObj.getBillingAddress(), testObj.getSiteId(), true, true) >> bbbAddressMock
			testObj.setBillingAddressContainer(billingAddressContainerMock)
			billingAddressContainerMock.getDuplicate() >> [bbbAddressMock]
			propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumber"
			
			testObj.checkFormRedirect(null,_, requestMock, responseMock) >>> [true,true,false]
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
		   2 * bbbAddressMock.getCountry()
		   1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
		   1 * checkoutManagerMock.saveBillingAddress(bbbOrderImplMock, bbbAddressMock);
		   1 * bbbAddressMock.setMobileNumber('9898989898')
		   1 * messageHandlerMock.getErrMsg('ERR_CHECKOUT_INVALID_INPUT', 'EN', null)
		   1 * sessionMock.setAttribute('internationalCreditCard', 'false')
		   1 * bbbAddressMock.setEmail('bbbforcustomer@gmail.com')
	   }
	
	def "handleSaveBillingAddress. This TC is when userSelectedOption is 'paypalAddress'"() {
		
		given:
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("paypalAddress")
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> true
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore"
			testObj.checkFormRedirect(null,"/storeatg-rest-ignore", requestMock, responseMock) >> false
			bbbOrderImplMock.isPayPalOrder() >> true
			testObj.setOrderManager(simpleOrderManagerMock)
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			2 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
	   }
	
	def "handleSaveBillingAddress. This TC is when failureURL is empty and throws CommerceException"() {
		
		given:
			
			requestMock.getContextPath() >> "/store"
			testObj.setUserSelectedOption("paypalAddress")
			checkoutStateMock.getFailureURL() >> ""
			testObj.checkFormRedirect(null,"/storeatg-rest-ignore", requestMock, responseMock) >> false
			bbbOrderImplMock.isPayPalOrder() >> false
			testObj.setOrderManager(simpleOrderManagerMock)
			simpleOrderManagerMock.updateOrder(bbbOrderImplMock) >> {throw new CommerceException("Mock Commerce Exception")}
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * messageHandlerMock.getErrMsg("ERR_CHECKOUT_INVALID_BILLING_ADDRESS", "EN", null)
	   }
	
	def "handleSaveBillingAddress. This TC is when failureURL is atg-rest-ignore-redirect"() {
		
		given:
			requestMock.getContextPath() >> "/store"
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> true
			testObj.setUserSelectedOption(null)
			testObj.setUserLocale(new Locale("en_US"))
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			bbbOrderImplMock.isPayPalOrder() >> true
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setFromAjaxSubmission(true)
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
	   }
	
	def "handleSaveBillingAddress. This TC is when checkFormRedirect is false "() {
		
		given:
			testObj = Spy()
			testObj.setCheckoutState(checkoutStateMock)
			
			requestMock.getContextPath() >> "/store"
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> true
			testObj.setUserSelectedOption(null)
			testObj.setUserLocale(new Locale("en_US"))
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.checkFormRedirect(null,"/storeatg-rest-ignore", requestMock, responseMock) >> false
			bbbOrderImplMock.isPayPalOrder() >> true
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.checkFormRedirect(null, _, requestMock, responseMock) >> false
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
			0 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
	   }
	
	def "handleSaveBillingAddress. This TC is when isUniqueRequestEntry returns false"() {
		
		given:
			requestMock.getContextPath() >> "/store"
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> false
		
		when:
		   def booleanResult = testObj.handleSaveBillingAddress(requestMock, responseMock)
		   
		then:
			booleanResult == false
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
	   }
	
	
	
	////////////////////////////////////TCs for handleSaveBillingAddress ends////////////////////////////////////////
	
	/////////////////////////////////TCs for handleAddBillingAddressToOrder starts////////////////////////////////////////
	//Signature : public final boolean handleAddBillingAddressToOrder(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ////
	
	def "handleAddBillingAddressToOrder. This TC is the happy flow of handleAddBillingAddressToOrder"() {
		
		given:
		
			testObj = Spy()
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
			checkoutFailureURL.put("failure","url")
			checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
			def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()
			checkoutSuccessURL.put("Success","url")
			checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
			testObj.handleSaveBillingAddress(requestMock, responseMock) >> true
			testObj.ensureTransaction() >> transactionMock
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
		
		when:
		   def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock)
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * testObj.commitTransaction(transactionMock)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutStateMock.setCheckoutFailureURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect',
				 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 
				 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
			1 * checkoutStateMock.setCheckoutSuccessURLs(['GIFT':'atg-rest-ignore-redirect', 'BILLING':'atg-rest-ignore-redirect', 'SHIPPING_SINGLE':'atg-rest-ignore-redirect', 'COUPONS':'atg-rest-ignore-redirect',
				 'REVIEW':'atg-rest-ignore-redirect', 'PAYMENT':'atg-rest-ignore-redirect', 'GUEST':'atg-rest-ignore-redirect', 'CART':'atg-rest-ignore-redirect', 
				 'SHIPPING_MULTIPLE':'atg-rest-ignore-redirect'])
	   }
	
	def "handleAddBillingAddressToOrder. This TC is when checkGiftCard method returns false"() {
		
		given:
			testObj = Spy()
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			testObj.setOrderManager(simpleOrderManagerMock)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			
			def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
			checkoutFailureURL.put("failure","url")
			checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
			def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()
			checkoutSuccessURL.put("Success","url")
			checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
			testObj.handleSaveBillingAddress(requestMock, responseMock) >> true
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> false
			
		
		when:
		   def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
			1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
	   }
	
	def "handleAddBillingAddressToOrder. This TC is when CommerceException throws"() {
		
		given:
			def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
			checkoutFailureURL.put("failure","url")
			checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
			def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()
			checkoutSuccessURL.put("Success","url")
			checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
			
			requestMock.getContextPath() >> "/store"
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> true
			testObj.setUserSelectedOption(null)
			testObj.setUserLocale(new Locale("en_US"))
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.checkFormRedirect(null,"/storeatg-rest-ignore", requestMock, responseMock) >> false
			bbbOrderImplMock.isPayPalOrder() >> true
			testObj.setOrderManager(simpleOrderManagerMock)
			
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
			bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
			def TransactionManager transactionManagerMock = Mock()
			testObj.setTransactionManager(transactionManagerMock)
			transactionManagerMock.getTransaction() >> {throw new SystemException("Mock for SystemException")}
			
		when:
		   def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
			1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
	   }
	
	def "handleAddBillingAddressToOrder. This TC is when CommerceException throws and isTransactionMarkedAsRollBack is true"() {
		
		given:
			testObj = Spy()
			testObj.setCheckoutState(checkoutStateMock)
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			testObj.setOrder(bbbOrderImplMock)
			testObj.setCommonConfiguration(commonConfigurationMock)
			
			def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
			checkoutFailureURL.put("failure","url")
			checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
			def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()
			checkoutSuccessURL.put("Success","url")
			checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
			testObj.ensureTransaction() >> transactionMock
			testObj.handleSaveBillingAddress(requestMock, responseMock) >> true
			bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
			bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
			testObj.isTransactionMarkedAsRollBack() >> true
			
		when:
		   def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)
		   
		then:
			booleanResult == false
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
			1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
	   }
	
	def "handleAddBillingAddressToOrder. This TC is when setTransactionToRollbackOnly does not return exception"() {
		
		given:
			def Map<String, String> checkoutFailureURL = new HashMap<String, String>()
			checkoutFailureURL.put("failure","url")
			checkoutStateMock.getCheckoutFailureURLs() >> checkoutFailureURL
			def Map<String, String> checkoutSuccessURL = new HashMap<String, String>()
			checkoutSuccessURL.put("Success","url")
			checkoutStateMock.getCheckoutSuccessURLs() >>  checkoutSuccessURL
			
			requestMock.getContextPath() >> "/store"
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("getStackTrace") >> true
			testObj.setUserSelectedOption(null)
			testObj.setUserLocale(new Locale("en_US"))
			checkoutStateMock.getFailureURL() >> "atg-rest-ignore-redirect"
			testObj.checkFormRedirect(null,"/storeatg-rest-ignore", requestMock, responseMock) >> false
			bbbOrderImplMock.isPayPalOrder() >> true
			testObj.setOrderManager(simpleOrderManagerMock)
			
			testObj.setPaymentGroupManager(bbbPaymentGroupManager)
			bbbPaymentGroupManager.checkGiftCard(bbbOrderImplMock) >> true
			bbbPaymentGroupManager.processPaymentGroupStatusOnLoad(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
			def TransactionManager transactionManagerMock = Mock()
			testObj.setTransactionManager(transactionManagerMock)
			
			
		when:
		   def booleanResult = testObj.handleAddBillingAddressToOrder(requestMock, responseMock)
		   
		then:
			booleanResult == true
			1 * simpleOrderManagerMock.updateOrder(bbbOrderImplMock)
			1 * checkoutStateMock.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_BILLING.toString())
			1 * checkoutStateMock.setCheckoutFailureURLs(checkoutFailureURL)
			1 * checkoutStateMock.setCheckoutSuccessURLs(checkoutSuccessURL)
	   }
	
	////////////////////////////////////TCs for handleAddBillingAddressToOrder ends////////////////////////////////////////
	
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

