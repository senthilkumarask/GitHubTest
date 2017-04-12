package com.bbb.internationalshipping.integration.checkoutrequest

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.framework.httpquery.HTTPCallInvoker
import com.bbb.exception.BBBSystemException

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * @author Velmurugan Moorthy
 *
 * This class is written to unit test the BBBInternationalCartSubmissionService
 *
 */
class BBBInternationalCartSubmissionServiceSpecification extends BBBExtendedSpec {
	
	def BBBInternationalCartSubmissionService internationalCartSubmissionServiceMock
	
	def BBBCatalogTools catalogToolsMock
	def HTTPCallInvoker httpCallInvokerMock
	
	def setup() {
	
		catalogToolsMock = Mock()
		httpCallInvokerMock = new HTTPCallInvoker()
		
		internationalCartSubmissionServiceMock = Spy()
		
		internationalCartSubmissionServiceMock.setCatalogTools(catalogToolsMock)
		internationalCartSubmissionServiceMock.setHttpCallInvoker(httpCallInvokerMock)
	}
	
	def "submitInternationalCart - interational cart submission is successfull -  happy flow " () {
		
		
		given : 
		
		List<String> serviceUrlList = ["http://service1.bedbath.com","http://service2.bedbath.com"]
		List<String> serviceUserList = ["user1","user2"]
		List<String> servicePasswordList = ["pswd1","pswd2"]
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		String responseXml = "<authorized>yes</authorized>"
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> responseXml
				
		when : 
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse.equals(responseXml) 

	}

	def "submitInternationalCart - Envoy response from third party is invalid (null)" () {
		
		
		given :
		
		List<String> serviceUrlList = ["http://service1.bedbath.com","http://service2.bedbath.com"]
		List<String> serviceUserList = ["user1","user2"]
		List<String> servicePasswordList = ["pswd1","pswd2"]
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		//String responseXml = "<authorized>yes</authorized>"
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> null
				
		when :
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse.isEmpty()==true
		1 * internationalCartSubmissionServiceMock.logError("ERROR: Envoy Response received is NULL/BLANK");
	}

	def "submitInternationalCart - Envoy response from third party is invalid (empty)" () {
		
		
		given :
		
		List<String> serviceUrlList = ["http://service1.bedbath.com","http://service2.bedbath.com"]
		List<String> serviceUserList = ["user1","user2"]
		List<String> servicePasswordList = ["pswd1","pswd2"]
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		String responseXml = ""
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> responseXml
				
		when :
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse.isEmpty()
		1 * internationalCartSubmissionServiceMock.logError("ERROR: Envoy Response received is NULL/BLANK");
	}

	
	def "submitInternationalCart - Configure Keys for International Checkouts are invalid (empty)" () {
		
		
		given :
		
		List<String> serviceUrlList = new ArrayList<>()
		List<String> serviceUserList = new ArrayList<>()
		List<String> servicePasswordList = new ArrayList<>()
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		String responseXml = "<authorized>yes</authorized>"
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> responseXml
				
		when :
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse == null
		thrown BBBSystemException
		
	}
   
	//@Ignore
	def "submitInternationalCart - service URL & user password are not set (empty)" () {
		
		
		given :
		
		List<String> serviceUrlList = []
		List<String> serviceUserList =  ["user1","user2"]
		List<String> servicePasswordList = []
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		String responseXml = "<authorized>yes</authorized>"
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> responseXml
				
		when :
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse == null
		thrown IndexOutOfBoundsException
		
	}

	def "submitInternationalCart - service user & password list are not set (empty)" () {
		
		
		given :
		
		List<String> serviceUrlList = []
		List<String> serviceUserList =  []
		List<String> servicePasswordList = ["pswd1","pswd2"]
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		String responseXml = "<authorized>yes</authorized>"
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> responseXml
				
		when :
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse == null
		thrown IndexOutOfBoundsException
		
	}
		
	def "submitInternationalCart - international cart submission failed - (exception while invoking HttpCallInvoker.submitService)" () {
		
		
		given :
		
		List<String> serviceUrlList = ["http://service1.bedbath.com","http://service2.bedbath.com"]
		List<String> serviceUserList = ["user1","user2"]
		List<String> servicePasswordList = ["pswd1","pswd2"]
		
		
		String requestXml = "<orderId>order001</orderId><orderTotal>110</orderTotal>"
		String responseXml = "<authorized>yes</authorized>"
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
				BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_END_POINT) >> serviceUrlList
			
		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_USER_ID) >> serviceUserList

		catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_CHECKOUT_PWD) >> servicePasswordList
				
		internationalCartSubmissionServiceMock.invokeIntlCartSubmission(*_) >> {throw new BBBSystemException(BBBCoreErrorConstants.INTL_SHIPPING_ERROR_1008, BBBCoreErrorConstants.ERROR_INTL_SHIPPING_ERROR_1008)}
				
		when :
			
		def resultResponse = internationalCartSubmissionServiceMock.submitInternationalCart(requestXml)
		
		then :
		
		resultResponse == null
		thrown BBBSystemException
		1 * internationalCartSubmissionServiceMock.logError("BBBSystemException during execution of HttpCallInvoker in submitService",_);
	}
	
}
