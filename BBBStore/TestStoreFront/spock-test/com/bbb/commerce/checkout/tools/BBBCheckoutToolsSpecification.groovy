package com.bbb.commerce.checkout.tools

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO
import com.bbb.commerce.common.BBBVBVSessionBean
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean
import com.bbb.commerce.order.paypal.PayPalAddressVerification;
import com.bbb.commerce.order.purchase.CheckoutProgressStates
import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException
import com.bbb.paypal.BBBAddressPPVO
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;
import com.bbb.profile.session.BBBSessionBean;
import com.cardinalcommerce.client.CentinelRequest
import com.cardinalcommerce.client.CentinelResponse

import atg.commerce.CommerceException;
import atg.commerce.order.SimpleOrderManager
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec
/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is written to unit test the BBBCheckoutTools class
 *
 */
public class BBBCheckoutToolsSpecification extends BBBExtendedSpec {

	def BBBCheckoutTools checkoutToolsSpy
	def BBBCatalogToolsImpl catalogToolsMock
	String connectTimeout = "60"
	String  readTimeout = "60"
	String vbvTransactionURL = "https://centineltest.cardinalcommerce.com/maps/txns.asp"
	
	def setup() {
		
		checkoutToolsSpy = Spy()
		catalogToolsMock = Mock()
		checkoutToolsSpy.setCatalogTools(catalogToolsMock)
		
	}
	
	/*======================================================================================================================================
	 *
	 * vbvCentinelSendLookupRequest - Test cases start
	 *
	 * Method signature :
	 *
	 * public final BBBVerifiedByVisaVO vbvCentinelSendLookupRequest(CentinelRequest centinelRequest, String cardVerNumber,
	 * BBBOrder bbbOrder, BBBVBVSessionBean vbvSessionBean) throws BBBSystemException, BBBBusinessException
	 *======================================================================================================================================
	 */
	
	
	def "vbvCentinelSendLookupRequest - CentinelLookupRequest call is successful and received resposne - happy flow" () {
	
		given :
		
		CentinelRequest centinelRequestMock = Mock()
		CentinelResponse centinelResponseMock = Mock()
		BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		BBBOrder bbbOrderMock = Mock()
		BBBVBVSessionBean vbvSessionBeanMock = Mock()
		
		String cardVerNumber = "12345678"
		Hashtable centinalResponseHashTable = new Hashtable()
		centinalResponseHashTable.put(BBBVerifiedByVisaConstants.ErrorNo, "error1")
		String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
		String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>"
	
		def vbvTransactionType = "auth"
		def errorNumber = "error1"
		def errorDesc = "Error during payment processing"
		def transactionId = "trans01"
		def orderId = "order01"
		def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
		def eciFlag = "eciEnabeld"
		def acsUrl = "acs.bedbath.com"
		def payload = "payload01"
		
		centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
		centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
		centinelResponseMock.getUnparsedResponse() >> centinalResponseString
		centinelResponseMock.getFormattedResponse() >> centinalResponseString
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber 
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.OrderId) >> orderId
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Enrolled) >> enrolled
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ACSUrl) >> acsUrl
		centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Payload) >> payload
		//TODO use the following code (2 lines) to show the diff b/w Mock & spy
		 
		//vbvSessionBeanMock.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVOMock) // won't work
		
		vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock // will work
		
		catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType) >> [vbvTransactionType]

		when :
		
		BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvCentinelSendLookupRequest(centinelRequestMock, cardVerNumber, bbbOrderMock, vbvSessionBeanMock)
		
		then :
		
			bbbVerifiedByVisaVO.getLookupErrorDesc().equals(errorDesc)
			bbbVerifiedByVisaVO.getTransactionId().equals(transactionId)
			bbbVerifiedByVisaVO.getEnrolled().equals(enrolled)
			bbbVerifiedByVisaVO.getaCSUrl().equals(acsUrl)
			bbbVerifiedByVisaVO.getTransactionType().equals(vbvTransactionType)
			bbbVerifiedByVisaVO.getCardVerNumber().equals(cardVerNumber)
			bbbVerifiedByVisaVO.getLookupErrorNo().equals(errorNumber)
			bbbVerifiedByVisaVO.getOrderId().equals(orderId)
			bbbVerifiedByVisaVO.getLookupEciFlag().equals(eciFlag)
			bbbVerifiedByVisaVO.getPayload().equals(payload)
			bbbVerifiedByVisaVO.getLookupResponse().equals(centinalResponseString)
			bbbVerifiedByVisaVO.getLookupRequest().equals(null)
			
			
			
	}
	
	def "vbvCentinelSendLookupRequest - centinelRequest object is corrupted (null)" () {
		
			given :
			
			CentinelRequest centinelRequestMock = null
			CentinelResponse centinelResponseMock = Mock()
			BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
			BBBOrder bbbOrderMock = Mock()
			BBBVBVSessionBean vbvSessionBeanMock = Mock()
			
			String cardVerNumber = "12345678"
			Hashtable centinalResponseHashTable = new Hashtable()
			centinalResponseHashTable.put(BBBVerifiedByVisaConstants.ErrorNo, "error1")
			String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
			String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>"
		
			def vbvTransactionType = "auth"
			def errorNumber = "error1"
			def errorDesc = "Error during payment processing"
			def transactionId = "trans01"
			def orderId = "order01"
			def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
			def eciFlag = "eciEnabeld"
			def acsUrl = "acs.bedbath.com"
			def payload = "payload01"
			
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock // will work
			
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType) >> [vbvTransactionType]
	
			when :
			
			BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvCentinelSendLookupRequest(centinelRequestMock, cardVerNumber, bbbOrderMock, vbvSessionBeanMock)
			
			then :
			
				0 * centinelRequestMock.getFormattedRequest()
				0 * centinelRequestMock.sendHTTP(*_)
			    thrown NullPointerException
		}
	
	def "vbvCentinelSendLookupRequest - Centinel request object is corrupted (empty)" () {
		
			given :
			
			CentinelRequest centinelRequestMock = Mock()
			CentinelResponse centinelResponseMock = Mock()
			BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
			BBBOrder bbbOrderMock = Mock()
			BBBVBVSessionBean vbvSessionBeanMock = Mock()
			
			String cardVerNumber = "12345678"
			Hashtable centinalResponseHashTable = new Hashtable()
			centinalResponseHashTable.put(BBBVerifiedByVisaConstants.ErrorNo, "error1")
			String formattedCentinelRequest = ""
			String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>"
		
			def vbvTransactionType = "auth"
			def errorNumber = "error1"
			def errorDesc = "Error during payment processing"
			def transactionId = "trans01"
			def orderId = "order01"
			def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
			def eciFlag = "eciEnabeld"
			def acsUrl = "acs.bedbath.com"
			def payload = "payload01"
			
			centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
			centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
			centinelResponseMock.getUnparsedResponse() >> centinalResponseString
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.OrderId) >> orderId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Enrolled) >> enrolled
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ACSUrl) >> acsUrl
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Payload) >> payload
			
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock // will work
			
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType) >> [vbvTransactionType]
	
			when :
			
			BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvCentinelSendLookupRequest(centinelRequestMock, cardVerNumber, bbbOrderMock, vbvSessionBeanMock)
			
			then :
			
				0 * checkoutToolsSpy.logDebug("Centinal Look up Request -- " + formattedCentinelRequest)
				0 * formattedCentinelRequest.replaceAll(*_)
				bbbVerifiedByVisaVO.getLookupErrorDesc().equals(errorDesc)
				bbbVerifiedByVisaVO.getTransactionId().equals(transactionId)
				bbbVerifiedByVisaVO.getEnrolled().equals(enrolled)
		}
	
	def "vbvCentinelSendLookupRequest - centinelResponse object is corrupted (null)" () {
		
			given :
			
			CentinelRequest centinelRequestMock = Mock()
			CentinelResponse centinelResponseMock = null
			CentinelResponse tempCentinelResponseMock = Mock()
			BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
			BBBOrder bbbOrderMock = Mock()
			BBBVBVSessionBean vbvSessionBeanMock = Mock()
			
			String cardVerNumber = "12345678"
			Hashtable centinalResponseHashTable = new Hashtable()
			centinalResponseHashTable.put(BBBVerifiedByVisaConstants.ErrorNo, "error1")
			String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
			String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>"
		
			def vbvTransactionType = "auth"
			def errorNumber = "error1"
			def errorDesc = "Error during payment processing"
			def transactionId = "trans01"
			def orderId = "order01"
			def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
			def eciFlag = "eciEnabeld"
			def acsUrl = "acs.bedbath.com"
			def payload = "payload01"
			
			centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
			centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
			centinelResponseMock.getUnparsedResponse() >> centinalResponseString
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.OrderId) >> orderId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Enrolled) >> enrolled
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ACSUrl) >> acsUrl
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Payload) >> payload
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock // will work
			
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType) >> [vbvTransactionType]
	
			when :
			
			BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvCentinelSendLookupRequest(centinelRequestMock, cardVerNumber, bbbOrderMock, vbvSessionBeanMock)
			
			then :
			
				0 *	tempCentinelResponseMock.getUnparsedResponse()
				0 * centinalResponseString.replaceAll(*_)
				thrown NullPointerException
		}

	def "vbvCentinelSendLookupRequest - centinelResponse is corrupted (empty) " () {
		
			given :
			
			CentinelRequest centinelRequestMock = Mock()
			CentinelResponse centinelResponseMock = Mock()
			BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
			BBBOrder bbbOrderMock = Mock()
			BBBVBVSessionBean vbvSessionBeanMock = Mock()
			
			String cardVerNumber = "12345678"
			Hashtable centinalResponseHashTable = new Hashtable()
			centinalResponseHashTable.put(BBBVerifiedByVisaConstants.ErrorNo, "error1")
			String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
			String centinalResponseString = ""
		
			def vbvTransactionType = "auth"
			def errorNumber = "error1"
			def errorDesc = "Error during payment processing"
			def transactionId = "trans01"
			def orderId = "order01"
			def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
			def eciFlag = "eciEnabeld"
			def acsUrl = "acs.bedbath.com"
			def payload = "payload01"
			
			centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
			centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
			centinelResponseMock.getUnparsedResponse() >> centinalResponseString
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.OrderId) >> orderId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Enrolled) >> enrolled
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ACSUrl) >> acsUrl
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Payload) >> payload
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock // will work
			
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType) >> [vbvTransactionType]
	
			when :
			
			BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvCentinelSendLookupRequest(centinelRequestMock, cardVerNumber, bbbOrderMock, vbvSessionBeanMock)
			
			then :
			
				bbbVerifiedByVisaVO.getLookupErrorDesc().equals(errorDesc)
				bbbVerifiedByVisaVO.getTransactionId().equals(transactionId)
				bbbVerifiedByVisaVO.getEnrolled().equals(enrolled)
				bbbVerifiedByVisaVO.getaCSUrl().equals(acsUrl)
				bbbVerifiedByVisaVO.getTransactionType().equals(vbvTransactionType)
				bbbVerifiedByVisaVO.getCardVerNumber().equals(cardVerNumber)
	 }
	

	def "vbvCentinelSendLookupRequest - CentinelResponse is too lengthy" () {
		
			given :
			
			CentinelRequest centinelRequestMock = Mock()
			CentinelResponse centinelResponseMock = Mock()
			BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
			BBBOrder bbbOrderMock = Mock()
			BBBVBVSessionBean vbvSessionBeanMock = Mock()
			
			String cardVerNumber = "12345678"
			Hashtable centinalResponseHashTable = new Hashtable()
			centinalResponseHashTable.put(BBBVerifiedByVisaConstants.ErrorNo, "error1")
			String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
			String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>"
		
			def vbvTransactionType = "auth"
			def errorNumber = "error1"
			def errorDesc = "Error during payment processing"
			def transactionId = "trans01"
			def orderId = "order01"
			def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
			def eciFlag = "eciEnabeld"
			def acsUrl = "acs.bedbath.com"
			def payload = "payload01"
			
			centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
			centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
			centinelResponseMock.getUnparsedResponse() >> centinalResponseString			
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.OrderId) >> orderId
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Enrolled) >> enrolled
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ACSUrl) >> acsUrl
			centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Payload) >> payload
			vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock // will work
			
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
			catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType) >> [vbvTransactionType]
	
			when :
			
			BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvCentinelSendLookupRequest(centinelRequestMock, cardVerNumber, bbbOrderMock, vbvSessionBeanMock)
			
			then :
			
				bbbVerifiedByVisaVO.getLookupErrorDesc().equals(errorDesc)
				bbbVerifiedByVisaVO.getTransactionId().equals(transactionId)
				bbbVerifiedByVisaVO.getEnrolled().equals(enrolled)
				bbbVerifiedByVisaVO.getaCSUrl().equals(acsUrl)
				bbbVerifiedByVisaVO.getTransactionType().equals(vbvTransactionType)
				bbbVerifiedByVisaVO.getCardVerNumber().equals(cardVerNumber)
		}

		
	/*====================================================
	 * vbvCentinelSendLookupRequest - Test cases - ENDS	 *
	 *====================================================
	 */
	
	/*==========================================================================================================================
	 * vbvUpdateLookupOrderAttributes - Test cases - STARTS																	   *
	 * 																														   *	
	 * public final boolean vbvUpdateLookupOrderAttributes(BBBOrder bbbOrder, 												   *	
	 * BBBVerifiedByVisaVO bBBVerifiedByVisaVO, boolean errorExists, SimpleOrderManager orderManager) throws CommerceException *
	 *==========================================================================================================================
	 */

	 def "vbvUpdateLookupOrderAttributes - updating lookup order attributes is successful - happy flow" () { 
		 
		 given : 
		 
		 def BBBOrder bbbOrderMock = Mock()
		 def BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Mock()
		 def SimpleOrderManager orderManagerMock = Mock()
		 
		 def errorExists = true
		 def vbvTransactionType = "auth"
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def transactionId = "trans01"
		 def orderId = "order01"
		 def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
		 def eciFlag = "eciEnabeld"
		 def acsUrl = "acs.bedbath.com"
		 def payload = "payload01"
		 def centinelPIType = BBBVerifiedByVisaConstants.VISA
		 
		 bBBVerifiedByVisaVOMock.getLookupErrorNo() >> errorNumber
		 bBBVerifiedByVisaVOMock.getLookupErrorDesc() >> errorDesc
		 bBBVerifiedByVisaVOMock.getTransactionId() >> transactionId
		 bBBVerifiedByVisaVOMock.getOrderId() >> orderId
		 bBBVerifiedByVisaVOMock.getEnrolled() >> enrolled
		 bBBVerifiedByVisaVOMock.getLookupEciFlag() >> eciFlag		 
		 bBBVerifiedByVisaVOMock.getCentinel_PIType() >> centinelPIType
		 
		 when : 
		 
		 def result = checkoutToolsSpy.vbvUpdateLookupOrderAttributes(bbbOrderMock,bBBVerifiedByVisaVOMock,errorExists, orderManagerMock)
		 
		 then : 
		 
		 result == false
		 1 * bbbOrderMock.setErrorNo(BBBVerifiedByVisaConstants.ERROR_NO_0)
		 1 * bbbOrderMock.setErrorDesc(errorDesc);
		 1 * bbbOrderMock.setTransactionId(transactionId);
		 1 * bbbOrderMock.setVbvOrderId(orderId);
		 1 * bbbOrderMock.setEnrolled(enrolled);
		 1 * bbbOrderMock.setEci(eciFlag);
	 }
		

	 def "vbvUpdateLookupOrderAttributes - not enrolled for Verified by Visa" () {
		 
		 given :
		 
		 def BBBOrder bbbOrderMock = Mock()
		 def BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Mock()
		 def SimpleOrderManager orderManagerMock = Mock()
		 
		 def errorExists = true
		 def vbvTransactionType = "auth"
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def transactionId = "trans01"
		 def orderId = "order01"
		 def enrolled = "N"
		 def eciFlag = "eciEnabeld"
		 def acsUrl = "acs.bedbath.com"
		 def payload = "payload01"
		 def centinelPIType = BBBVerifiedByVisaConstants.VISA
		 
		 bBBVerifiedByVisaVOMock.getLookupErrorNo() >> errorNumber
		 bBBVerifiedByVisaVOMock.getLookupErrorDesc() >> errorDesc
		 bBBVerifiedByVisaVOMock.getTransactionId() >> transactionId
		 bBBVerifiedByVisaVOMock.getOrderId() >> orderId
		 bBBVerifiedByVisaVOMock.getEnrolled() >> enrolled
		 bBBVerifiedByVisaVOMock.getLookupEciFlag() >> eciFlag
		 bBBVerifiedByVisaVOMock.getCentinel_PIType() >> centinelPIType
		 
		 when :
		 
		 def result = checkoutToolsSpy.vbvUpdateLookupOrderAttributes(bbbOrderMock,bBBVerifiedByVisaVOMock,errorExists, orderManagerMock)
		 
		 then :
		 
		 result == errorExists
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(*_)
		 1 * bbbOrderMock.setCommerceIndicator(*_)
		 1 * bbbOrderMock.setErrorNo(BBBVerifiedByVisaConstants.ERROR_NO_0)
		 1 * bbbOrderMock.setErrorDesc(errorDesc)
		 1 * bbbOrderMock.setTransactionId(transactionId)
		 1 * bbbOrderMock.setVbvOrderId(orderId)
		 1 * bbbOrderMock.setEnrolled(enrolled)
		 1 * bbbOrderMock.setEci(eciFlag)
	 }
	 
	 def "vbvUpdateLookupOrderAttributes - error occured while updating order attributes (errorCode not equals 0)" () {
		 
		 given :
		 
		 def BBBOrder bbbOrderMock = Mock()
		 def BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Mock()
		 def SimpleOrderManager orderManagerMock = Mock()
		 
		 def errorExists = true
		 def vbvTransactionType = "auth"
		 def errorNumber = "err1"
		 def errorDesc = "Error during payment processing"
		 def transactionId = "trans01"
		 def orderId = "order01"
		 def enrolled = BBBVerifiedByVisaConstants.EnrolledForVBV_Yes
		 def eciFlag = "eciEnabeld"
		 def acsUrl = "acs.bedbath.com"
		 def payload = "payload01"
		 def centinelPIType = BBBVerifiedByVisaConstants.VISA
		 
		 bBBVerifiedByVisaVOMock.getLookupErrorNo() >> errorNumber
		 bBBVerifiedByVisaVOMock.getLookupErrorDesc() >> errorDesc
		 bBBVerifiedByVisaVOMock.getTransactionId() >> transactionId
		 bBBVerifiedByVisaVOMock.getOrderId() >> orderId
		 bBBVerifiedByVisaVOMock.getEnrolled() >> enrolled
		 bBBVerifiedByVisaVOMock.getLookupEciFlag() >> eciFlag
		 bBBVerifiedByVisaVOMock.getCentinel_PIType() >> centinelPIType
		 
		 when :
		 
		 def result = checkoutToolsSpy.vbvUpdateLookupOrderAttributes(bbbOrderMock,bBBVerifiedByVisaVOMock,errorExists, orderManagerMock)
		 
		 then :
		 
		 result == errorExists
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(*_)
		 1 * bbbOrderMock.setCommerceIndicator(*_)
		 1 * bbbOrderMock.setErrorNo(errorNumber)
		 1 * bbbOrderMock.setErrorDesc(errorDesc)
		 1 * bbbOrderMock.setTransactionId(transactionId)
		 1 * bbbOrderMock.setVbvOrderId(orderId)
		 1 * bbbOrderMock.setEnrolled(enrolled)
		 1 * bbbOrderMock.setEci(eciFlag)
	 }

	 
	 def "vbvUpdateLookupOrderAttributes - Centinel_PI type is not VISA" () {
		 
		 given :
		 
		 def BBBOrder bbbOrderMock = Mock()
		 def BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Mock()
		 def SimpleOrderManager orderManagerMock = Mock()
		 
		 def errorExists = true
		 def vbvTransactionType = "auth"
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def transactionId = "trans01"
		 def orderId = "order01"
		 def enrolled = "N"
		 def eciFlag = "eciEnabeld"
		 def acsUrl = "acs.bedbath.com"
		 def payload = "payload01"
		 def centinelPIType = "AMEX"
		 
		 bBBVerifiedByVisaVOMock.getLookupErrorNo() >> errorNumber
		 bBBVerifiedByVisaVOMock.getLookupErrorDesc() >> errorDesc
		 bBBVerifiedByVisaVOMock.getTransactionId() >> transactionId
		 bBBVerifiedByVisaVOMock.getOrderId() >> orderId
		 bBBVerifiedByVisaVOMock.getEnrolled() >> enrolled
		 bBBVerifiedByVisaVOMock.getLookupEciFlag() >> eciFlag
		 bBBVerifiedByVisaVOMock.getCentinel_PIType() >> centinelPIType
		 
		 when :
		 
		 def result = checkoutToolsSpy.vbvUpdateLookupOrderAttributes(bbbOrderMock,bBBVerifiedByVisaVOMock,errorExists, orderManagerMock)
		 
		 then :
		 
		 result == errorExists
		 1 * bbbOrderMock.setErrorNo(*_)
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(*_)
		 0 * bbbOrderMock.setCommerceIndicator(*_)
	 }

	 	 
	 	
	/*======================================================
	 * vbvUpdateLookupOrderAttributes - Test cases - ends  *
	 * =====================================================
	 */
	
	 
	 /*===============================================================
	  * vbvSendAuthenticateRequest - Test cases - STARTS			 * 
	  * Method signature :  										 * 	
	  * public final BBBVerifiedByVisaVO vbvSendAuthenticateRequest( * 
	  * CentinelRequest centinelRequest, BBBOrder bbbOrder,			 *
	  * BBBVBVSessionBean vbvSessionBean) 							 *
	  * throws BBBSystemException, BBBBusinessException				 *
	  * ==============================================================
	  */
	 
	 def "vbvSendAuthenticateRequest - VBV AuthenticationRequest is successfully sent - happy flow" () {
		 
		 given :
		 
		 CentinelRequest centinelRequestMock = Mock()
		 CentinelResponse centinelResponseMock = Mock()
		 BBBOrder bbbOrderMock = Mock()
		 BBBVBVSessionBean vbvSessionBeanMock = Mock()
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
		 String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>>"
	 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock
	
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		 
		 centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
		 centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
		 
		 centinelResponseMock.getFormattedResponse()  >> centinalResponseString
		 centinelResponseMock.getUnparsedResponse() >> centinalResponseString
		 
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.PAResStatus) >> parEStatus
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.SignatureVerification) >> signatureVerification
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Xid) >> xId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Cavv) >> caVV
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
		 		 
		 when : 
		 
		  BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvSendAuthenticateRequest(centinelRequestMock, bbbOrderMock, vbvSessionBeanMock)
		  
		  then : 
		  
		  bbbVerifiedByVisaVO.getpAResStatus().equals(parEStatus)
		  bbbVerifiedByVisaVO.getSignatureVerification().equals(signatureVerification)
		  bbbVerifiedByVisaVO.getAuthenticationEciFlag().equals(eciFlag) 
		  bbbVerifiedByVisaVO.getXid().equals(xId)
		  bbbVerifiedByVisaVO.getCavv().equals(caVV)
		  bbbVerifiedByVisaVO.getAuthenticationErrorNo().equals(errorNumber)
		  bbbVerifiedByVisaVO.getAuthenticationErrorDesc().equals(errorDesc)

		  
	 }
	 
	 def "vbvSendAuthenticateRequest - CentinelRequest is corrupted (null)" () {
		 
		 given :
		 
		 CentinelRequest centinelRequestMock = null
		 CentinelRequest tempCentinelRequestMock = Mock()
		 CentinelResponse centinelResponseMock = Mock()
		 BBBOrder bbbOrderMock = Mock()
		 BBBVBVSessionBean vbvSessionBeanMock = Mock()
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Mock()
		 
		 String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
		 String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>>"
	 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def transactionId = "trans01"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		 
		 centinelResponseMock.getFormattedResponse()  >> centinalResponseString
		 centinelResponseMock.getUnparsedResponse() >> centinalResponseString
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.PAResStatus) >> parEStatus
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.SignatureVerification) >> signatureVerification
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Xid) >> xId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Cavv) >> caVV
				  
		 when :
		 
		  checkoutToolsSpy.vbvSendAuthenticateRequest(centinelRequestMock, bbbOrderMock, vbvSessionBeanMock)
		  
		  then :
		  
		  0 * tempCentinelRequestMock.getFormattedRequest()
		  thrown NullPointerException
	 }
	 
	 def "vbvSendAuthenticateRequest - CentinelRequest is corrupted (empty)" () {
		 
		 given :
		 
		 CentinelRequest centinelRequestMock = Mock()
		 CentinelResponse centinelResponseMock = Mock()
		 BBBOrder bbbOrderMock = Mock()
		 BBBVBVSessionBean vbvSessionBeanMock = Mock()
		 //BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Mock()
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = new BBBVerifiedByVisaVO()
		 
		 String formattedCentinelRequest = ""
		 String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>>"
	 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def transactionId = "trans01"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		 
		 centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
		 centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
		 centinelResponseMock.getUnparsedResponse()  >> centinalResponseString
		 
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.PAResStatus) >> parEStatus
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.SignatureVerification) >> signatureVerification
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.TransactionId) >> transactionId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Xid) >> xId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Cavv) >> caVV
				  
		 when :
		 
		  BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvSendAuthenticateRequest(centinelRequestMock, bbbOrderMock, vbvSessionBeanMock)
		  
		  then :
		  
		  0 * checkoutToolsSpy.logDebug("Centinal Authorization API request -- " + formattedCentinelRequest)
		  1 * checkoutToolsSpy.logDebug("Centinal Authorization API response -- "+centinalResponseString)
		  bbbVerifiedByVisaVO.getpAResStatus().equals(parEStatus)
		  bbbVerifiedByVisaVO.getSignatureVerification().equals(signatureVerification)
		  bbbVerifiedByVisaVO.getAuthenticationEciFlag().equals(eciFlag)
		  bbbVerifiedByVisaVO.getXid().equals(xId)
		  bbbVerifiedByVisaVO.getCavv().equals(caVV)
		  bbbVerifiedByVisaVO.getAuthenticationErrorNo().equals(errorNumber)
		  bbbVerifiedByVisaVO.getAuthenticationErrorDesc().equals(errorDesc)
	 }
	 
	 
	 def "vbvSendAuthenticateRequest - centinelResponse is corrupted/invalid (null)" () {
		 
		 given :
		 
		 CentinelRequest centinelRequestMock = Mock()
		 CentinelResponse centinelResponseMock = null
		 BBBOrder bbbOrderMock = Mock()
		 BBBVBVSessionBean vbvSessionBeanMock = Mock()
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
		 String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>>"
	 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock
	
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		 
		 centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
		 centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
		 
		 centinelResponseMock.getFormattedResponse()  >> centinalResponseString
		 centinelResponseMock.getUnparsedResponse() >> centinalResponseString
		 
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.PAResStatus) >> parEStatus
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.SignatureVerification) >> signatureVerification
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Xid) >> xId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Cavv) >> caVV
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
				  
		 when :
		 
		  BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvSendAuthenticateRequest(centinelRequestMock, bbbOrderMock, vbvSessionBeanMock)
		  
		  then :
		  
		  thrown NullPointerException
		  1 * centinelRequestMock.sendHTTP(*_)
		  0 * centinelResponseMock.getFormattedResponse()
		  bbbVerifiedByVisaVO == null
	 }
	 
	 def "vbvSendAuthenticateRequest - centinelResponse is corrupted/invalid (empty)" () {
		 
		 given :
		 
		 CentinelRequest centinelRequestMock = Mock()
		 CentinelResponse centinelResponseMock = Mock()
		 BBBOrder bbbOrderMock = Mock()
		 BBBVBVSessionBean vbvSessionBeanMock = Mock()
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
		 String centinalResponseString = ""
	 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock
	
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		 
		 centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
		 centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
		 
		 centinelResponseMock.getFormattedResponse()  >> centinalResponseString
		 centinelResponseMock.getUnparsedResponse() >> centinalResponseString
		 
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.PAResStatus) >> parEStatus
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.SignatureVerification) >> signatureVerification
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Xid) >> xId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Cavv) >> caVV
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
				  
		 when :
		 
		  BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvSendAuthenticateRequest(centinelRequestMock, bbbOrderMock, vbvSessionBeanMock)
		  
		  then :
		  
		  1 * centinelResponseMock.getFormattedResponse()
		  bbbVerifiedByVisaVO.getpAResStatus().equals(parEStatus)
		  bbbVerifiedByVisaVO.getSignatureVerification().equals(signatureVerification)
		  bbbVerifiedByVisaVO.getAuthenticationEciFlag().equals(eciFlag)
		  bbbVerifiedByVisaVO.getXid().equals(xId)
		  bbbVerifiedByVisaVO.getCavv().equals(caVV)
		  bbbVerifiedByVisaVO.getAuthenticationErrorNo().equals(errorNumber)
		  bbbVerifiedByVisaVO.getAuthenticationErrorDesc().equals(errorDesc)
	 }

	 def "vbvSendAuthenticateRequest - CentinelResponse is too lengthy" () {
		 
		 given :
		 
		 CentinelRequest centinelRequestMock = Mock()
		 CentinelResponse centinelResponseMock = Mock()
		 BBBOrder bbbOrderMock = Mock()
		 BBBVBVSessionBean vbvSessionBeanMock = Mock()
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 String formattedCentinelRequest = "<centinelRequest><cardNumber>6111111111111111</cardNumber> <verified>yes</verified>  <amount>125</amount>  <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelRequest>"
		 String centinalResponseString = "<centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse><centinelResponse><cardNumber>6111111111111111</cardNumber> <amount>125</amount> <token>jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac</token></centinelResponse>"
	 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 vbvSessionBeanMock.getbBBVerifiedByVisaVO() >> bBBVerifiedByVisaVOMock
	
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout) >> [connectTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout) >> [readTimeout]
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL) >> [vbvTransactionURL]
		 
		 centinelRequestMock.sendHTTP(*_) >> centinelResponseMock
		 centinelRequestMock.getFormattedRequest() >> formattedCentinelRequest
		 
		 centinelResponseMock.getFormattedResponse()  >> centinalResponseString
		 centinelResponseMock.getUnparsedResponse() >> centinalResponseString
		 
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.PAResStatus) >> parEStatus
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.SignatureVerification) >> signatureVerification
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.EciFlag) >> eciFlag
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Xid) >> xId
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.Cavv) >> caVV
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorNo) >> errorNumber
		 centinelResponseMock.getValue(BBBVerifiedByVisaConstants.ErrorDesc) >> errorDesc
				  
		 when :
		 
		  BBBVerifiedByVisaVO bbbVerifiedByVisaVO = checkoutToolsSpy.vbvSendAuthenticateRequest(centinelRequestMock, bbbOrderMock, vbvSessionBeanMock)
		  
		  then :
		  
		  bbbVerifiedByVisaVO.getpAResStatus().equals(parEStatus)
		  bbbVerifiedByVisaVO.getSignatureVerification().equals(signatureVerification)
		  bbbVerifiedByVisaVO.getAuthenticationEciFlag().equals(eciFlag)
		  bbbVerifiedByVisaVO.getXid().equals(xId)
		  bbbVerifiedByVisaVO.getCavv().equals(caVV)
		  bbbVerifiedByVisaVO.getAuthenticationErrorNo().equals(errorNumber)
		  bbbVerifiedByVisaVO.getAuthenticationErrorDesc().equals(errorDesc)
	 }
	 	 
	 /*================================================
	  * vbvSendAuthenticateRequest - Test cases - ENDS* 
	  * ===============================================
	  */
	 	 
	/*==================================================================================
	 * vbvUpdateAuthOrderAttributes - Test cases - STARTS  							   *
	 * public final void vbvUpdateAuthOrderAttributes(BBBOrder bbbOrder, 			   *	
	 * BBBVerifiedByVisaVO bBBVerifiedByVisaVO, LblTxtTemplateManager messageHandler,  *
	 * SimpleOrderManager orderManager) 											   *
	 * throws BBBSystemException, BBBBusinessException, CommerceException			   *			
	 * =================================================================================
	 */
	 
	 //PaReStatus - PaResStatus_Y test cases - STARTS 
	 
	 def "vbvUpdateAuthOrderAttributes - update  order authentication properties successful - happy flow | verified by VISA (PaResStatus_Y)" () {
		 
		 given : 
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_Y
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com" 
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus) 
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when : 
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV)
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV)
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV)
		 1 * orderManagerMock.updateOrder(bbbOrderMock)
		 
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verified by VISA (PaResStatus - PaResStatus_Y) & No error exists (error code is 1140)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_1140
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_Y
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV)
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV)
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV)
		 1 * orderManagerMock.updateOrder(bbbOrderMock)
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verified by VISA (PaResStatus - PaResStatus_Y) & unverified Signature" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_1140
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_Y
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_No
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 thrown BBBBusinessException
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verified By VISA (PaReStatus is PaResStatus_Y) | Card type (Centinel_PIType()) is not VISA" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_Y
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def centinelPIType = "AMEX"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(centinelPIType)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV)
		 
	 }
	 
	 //PaReStatus - PaResStatus_Y test cases - STARTS
	 
	 //PaReStatus - PaResStatus_N test cases - STARTS
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_N) | verified signature & error code is ERROR_NO_1140" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_1140
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_N
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 thrown BBBBusinessException
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_N) | verified signature & error code is ERROR_NO_0" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_N
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
				 thrown BBBBusinessException
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_N) | unverified signature and not enrolled for VBV" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_N
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_No
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def enrolled = BBBVerifiedByVisaConstants.PaResStatus_N
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 bBBVerifiedByVisaVOMock.setEnrolled(enrolled)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		  thrown BBBBusinessException
		  0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		  0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		  0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - invaild (not PaResStatus_N) | verified signature & error code is ERROR_NO_1140" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_1140
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "N/A"
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 thrown BBBBusinessException
	 }
	 
	 //PaReStatus - PaResStatus_N test cases - ENDS
	 
	 def "vbvUpdateAuthOrderAttributes - attempted verification by VISA (PaResStatus is PaResStatus_A)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_A
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - signature verification data and ParEStatus are corrupted/invalid (empty) & not enrolled for VBV" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = ""
		 def signatureVerification = ""
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def enrolled = BBBVerifiedByVisaConstants.PaResStatus_N  
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 bBBVerifiedByVisaVOMock.setEnrolled(enrolled)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
	 }
	 	 
	  
	 def "vbvUpdateAuthOrderAttributes - Verification by visa attempted (PaResStatus is PaResStatus_A)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_A
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def cardType = "amex"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(cardType)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :

		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
	 }
	 
	 // PaResStatus_U - test cases - PaResStatus is PaResStatus_U - STARTS 
	 // branch covered - TTTF
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_U), signature is verified & no error (code : 0)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_U
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 
	 }
	 
	 // branch covered - TFTF
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_U), signature not verified & no error (code : 0)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_U
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_No
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 thrown BBBBusinessException
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_U),signature is verified and no error(code : ERROR_NO_1140)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_1140
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_U
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR);
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR);
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR);
	 }
	 
	 
	 def "vbvUpdateAuthOrderAttributes - VBV - not authenticated (PaResStatus is PaResStatus_U) and Signature not verified and no error(code : ERROR_NO_1140)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_1140
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_U
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_No
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 thrown BBBBusinessException
		 
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is PaResStatus_U), centinel PI type is not VISA" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_U
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 def centinelPIType = "amex"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.getCentinel_PIType() >> centinelPIType
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 1 * bBBVerifiedByVisaVOMock.setMessage(vbvNonAuthSuccessUrl)
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Verification by VISA - not authenticated (PaResStatus is invalid)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "N/A"
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvNonAuthSuccessUrl = "billing.bedbath.com"
		 def centinelPIType = "amex"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvNonAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.getCentinel_PIType() >> centinelPIType
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		
		 thrown BBBBusinessException 
		
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR)
		 
	 }
	 
	 // PaResStatus_U - test cases - PaResStatus is PaResStatus_U - ENDs
	 
	 def "vbvUpdateAuthOrderAttributes - Timeout while authenticating (BBBVerifiedByVisaConstants.AuthenticateTimeout_1051)" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.AuthenticateTimeout_1051
		 def errorDesc = "Error during payment processing"
		 def parEStatus = ""
		 def signatureVerification = ""
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 1 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
		 1 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
		 1 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - Timeout while authenticating (BBBVerifiedByVisaConstants.AuthenticateTimeout_1051) and centinel PI type is not VISA" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.AuthenticateTimeout_1051
		 def errorDesc = "Error during payment processing"
		 def parEStatus = ""
		 def signatureVerification = ""
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def centinelPIType = "amex"
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(centinelPIType)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 1 * bBBVerifiedByVisaVOMock.setMessage(*_) 
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
	 }
	 
	 
	 def "vbvUpdateAuthOrderAttributes - PaResStatus is empty" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = ""
		 def signatureVerification = BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		  
		 thrown BBBBusinessException
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - PaResStatus and signature verification are empty and enrolled" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = ""
		 def signatureVerification = ""
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def enrolled = BBBVerifiedByVisaConstants.PaResStatus_Y
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 bBBVerifiedByVisaVOMock.setEnrolled(enrolled)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 thrown BBBBusinessException
	 }
	 
	 def "vbvUpdateAuthOrderAttributes - PaResStatus and signature verification are empty and not enrolled for VBV" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = ""
		 def signatureVerification = ""
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def enrolled = BBBVerifiedByVisaConstants.PaResStatus_N
		 def cardType = "amex"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(cardType)
		 bBBVerifiedByVisaVOMock.setEnrolled(enrolled)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 1 * bBBVerifiedByVisaVOMock.setMessage(*_)
		 0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 0 * checkoutToolsSpy.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
	 }

	 /*def "vbvUpdateAuthOrderAttributes - PaResStatus invalid and signature verification are empty and not enrolled" () {
		 
		 given :
		 
		 def errorNumber = BBBVerifiedByVisaConstants.ERROR_NO_0
		 def errorDesc = "Error during payment processing"
		 def parEStatus = BBBVerifiedByVisaConstants.PaResStatus_N
		 def signatureVerification = "N/A"
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 def vbvAuthSuccessUrl = "billing.bedbath.com"
		 def enrolled = BBBVerifiedByVisaConstants.PaResStatus_N
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 catalogToolsMock.getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess) >> [vbvAuthSuccessUrl]
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 bBBVerifiedByVisaVOMock.setEnrolled(enrolled)
		 
		 when :
		 
		 checkoutToolsMock.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		  thrown BBBBusinessException
		  0 * bBBVerifiedByVisaVOMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		  0 * bbbOrderMock.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		  0 * checkoutToolsMock.logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED)
		 
	 }
*/	 

	 
	 
	 def "vbvUpdateAuthOrderAttributes - Authentication failed" () {
		 
		 given :
		 
		 def errorNumber = "error1"
		 def errorDesc = "Error during payment processing"
		 def parEStatus = "auth"
		 def signatureVerification = "order01"
		 def eciFlag = "eciEnabeld"
		 def xId = "bbbx01"
		 def caVV = "123"
		 
		 BBBVerifiedByVisaVO bBBVerifiedByVisaVOMock = Spy()
		 
		 BBBOrder bbbOrderMock = Mock()
		 LblTxtTemplateManager messageHandlerMock = Mock()
		 SimpleOrderManager orderManagerMock = Mock()
		 
		 bBBVerifiedByVisaVOMock.setpAResStatus(parEStatus)
		 bBBVerifiedByVisaVOMock.setSignatureVerification(signatureVerification)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorNo(errorNumber)
		 bBBVerifiedByVisaVOMock.setAuthenticationEciFlag()
		 bBBVerifiedByVisaVOMock.setXid(xId)
		 bBBVerifiedByVisaVOMock.setCavv(caVV)
		 bBBVerifiedByVisaVOMock.setAuthenticationErrorDesc(errorDesc)
		 bBBVerifiedByVisaVOMock.setCentinel_PIType(BBBVerifiedByVisaConstants.VISA)
		 
		 when :
		 
		 checkoutToolsSpy.vbvUpdateAuthOrderAttributes(bbbOrderMock, bBBVerifiedByVisaVOMock, messageHandlerMock, orderManagerMock)
		 
		 then :
		 
		 thrown BBBBusinessException
	 }
	
	 
	 
	 
	 /*=====================================================
	  * vbvUpdateAuthOrderAttributes - Test cases - ENDS *
	  * ====================================================
	  */
	
	 /*========================================================================================
	  * validatePayPalToken - Test cases - STARTS 											  *
	  * public boolean validatePayPalToken(String token, BBBOrderImpl order, Profile profile) *
	  * =======================================================================================
	  */
	 
	 def "validatePayPalToken - paypal token is valid - happy flow" () {

		 given : 
		 
		 def token = "jmjlAOvfsXdAPjN7HQqRWeQ6kFSnQvZkac"
		 BBBOrderImpl orderMock = Mock()
		 Profile profileMock = Mock()
		 
		 when : 
		 
		 def result = checkoutToolsSpy.validatePayPalToken(token, orderMock, profileMock)
		 
		 then : 
		 
		 result == true
	 }

	 def "validatePayPalToken - paypal token is empty" () {
		 
				  given :
				  
				  def token = ""
				  BBBOrderImpl orderMock = Mock()
				  Profile profileMock = Mock()
				  BBBPayPalServiceManager paypalServiceManagerMock = Mock()
				  checkoutToolsSpy.setPaypalServiceManager(paypalServiceManagerMock)
				  
				  when :
				  
				  def result = checkoutToolsSpy.validatePayPalToken(token, orderMock, profileMock)
				  
				  then :
				  
				  result == false
				  1 * paypalServiceManagerMock.removePayPalPaymentGroup(orderMock, profileMock)
	 }
	 
	 def "validatePayPalToken - Exception while removing paypal details from order" () {
		 
				  given :
				  
				  def token = ""
				  def String errorMessage = "validationError"
				  BBBOrderImpl orderMock = Mock()
				  Profile profileMock = Mock()
				  BBBPayPalServiceManager paypalServiceManagerMock = Mock()
				  checkoutToolsSpy.setPaypalServiceManager(paypalServiceManagerMock)
				  paypalServiceManagerMock.removePayPalPaymentGroup(*_) >> {throw new BBBSystemException(errorMessage)}
				  
				  when :
				  
				  def result = checkoutToolsSpy.validatePayPalToken(token, orderMock, profileMock)
				  
				  then :
				  
				  result == false
				  //(1.._) * checkoutToolsMock.logError("BBBCheckoutTools.validatePayPalToken():: System Exception while removing PayPal Details from Order:"+errorMessage)
				  (1.._) * checkoutToolsSpy.logError(*_)
				  //1 * checkoutToolsMock.logError("BBBCheckoutTools.validatePayPalToken():: System Exception while removing PayPal Details from Order:"+errorMessage)
	 }

	 def "validatePayPalToken - paypal token is null" () {
		 
				  given :
				  
				  def token = "null"
				  BBBOrderImpl orderMock = Mock()
				  Profile profileMock = Mock()
				  BBBPayPalServiceManager paypalServiceManagerMock = Mock()
				  checkoutToolsSpy.setPaypalServiceManager(paypalServiceManagerMock)
				  
				  when :
				  
				  def result = checkoutToolsSpy.validatePayPalToken(token, orderMock, profileMock)
				  
				  then :
				  
				  result == false
				  1 * paypalServiceManagerMock.removePayPalPaymentGroup(orderMock, profileMock)
	 }
	 
	 /*========================================================================================
	  * validatePayPalToken - Test cases - ENDS 											  *
	  * =======================================================================================
	  */
	 
	 
	 /*==========================================================================
	  * getPayPalRedirectUrl - Test cases - STARTS 								*
	  * public String getPayPalRedirectUrl(CheckoutProgressStates checkoutState)*
	  * =========================================================================
	  */
	 
	 def "getPayPalRedirectUrl - paypal redirect URL is retrieved successfully - happy flow" () {
		 
		 given : 
		 
		 CheckoutProgressStates checkoutStateMock = Mock()
		 DynamoHttpServletRequest requestMock = Mock()
		 BBBSessionBean sessionBeanMock = Mock()
		 
		 def currentCheckoutLevel = BBBCoreConstants.CART
		 def checkoutFailureUrl = "bedbath.com/cart"
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		 checkoutStateMock.getCurrentLevel() >> currentCheckoutLevel
		 checkoutStateMock.getCheckoutFailureURLs() >> ["cart" : checkoutFailureUrl ]
		 
		 when : 
		 
		 def payPalRedirectURL = checkoutToolsSpy.getPayPalRedirectUrl(checkoutStateMock)
		 
		 then : 
		 
		 payPalRedirectURL == checkoutFailureUrl
		 
	 }
	 
	 
	 def "getPayPalRedirectUrl - paypal redirect URL is retrieved for review page (SP_REVIEW)" () {
		 
		 given :
		 
		 CheckoutProgressStates checkoutStateMock = Mock()
		 DynamoHttpServletRequest requestMock = Mock()
		 BBBSessionBean sessionBeanMock = Mock()
		 
		 def currentCheckoutLevel = BBBCoreConstants.SP_REVIEW
		 def checkoutFailureUrl = "bedbath.com/review"
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		 checkoutStateMock.getCurrentLevel() >> currentCheckoutLevel
		 checkoutStateMock.getCheckoutFailureURLs() >> ["SP_PAYPAL" : checkoutFailureUrl ]
		 
		 when :
		 
		 def payPalRedirectURL = checkoutToolsSpy.getPayPalRedirectUrl(checkoutStateMock)
		 
		 then :
		 
		 payPalRedirectURL == checkoutFailureUrl
	 }
	 
	 
	 def "getPayPalRedirectUrl - paypal redirect URL is retrieved for payment page (PAYMENT)" () {
		 
		 given :
		 
		 CheckoutProgressStates checkoutStateMock = Mock()
		 DynamoHttpServletRequest requestMock = Mock()
		 BBBSessionBean sessionBeanMock = Mock()
		 
		 def currentCheckoutLevel = BBBPayPalConstants.PAYMENT
		 def checkoutFailureUrl = "bedbath.com/payment"
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		 checkoutStateMock.getCurrentLevel() >> currentCheckoutLevel
		 checkoutStateMock.getCheckoutFailureURLs() >> ["PAYMENT" : checkoutFailureUrl ]
		 
		 when :
		 
		 def payPalRedirectURL = checkoutToolsSpy.getPayPalRedirectUrl(checkoutStateMock)
		 
		 then :
		 
		 payPalRedirectURL == checkoutFailureUrl
	 }
	 
	 def "getPayPalRedirectUrl - checkout level is corrupted/invalid (null)" () {
		 
		 given :
		 
		 CheckoutProgressStates checkoutStateMock = Mock()
		 DynamoHttpServletRequest requestMock = Mock()
		 BBBSessionBean sessionBeanMock = Mock()
		 
		 def currentCheckoutLevel = null
		 def checkoutFailureUrl = "bedbath.com/payment"
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		 checkoutStateMock.getCurrentLevel() >> currentCheckoutLevel
		 checkoutStateMock.getCheckoutFailureURLs() >> ["PAYMENT" : checkoutFailureUrl ]
		 
		 when :
		 
		 def payPalRedirectURL = checkoutToolsSpy.getPayPalRedirectUrl(checkoutStateMock)
		 
		 then :
		 
		 payPalRedirectURL == checkoutFailureUrl
	 }
	 
	 
	 /*==========================================================================
	  * getPayPalRedirectUrl - Test cases - ENDS 								*
	  * =========================================================================
	  */

	 
	 /*===========================================================================================================================================
	  * validateShipping - Test cases - ENDS																	    	 						 *
	  * Method's signature : public PayPalAddressVerifyVO validateShipping(BBBGetExpressCheckoutDetailsResVO voResp, 							 *
	  *  BBBOrderImpl order, PayPalAddressVerifyVO addressVerifyVO, PayPalInputVO paypalInput) throws ServletException, IOException 			 *  												
	  * ==========================================================================================================================================
	  */
	 
	 
	 def  "validateShipping - shipping is valid - happy flow | paypal session is (BBBPayPalConstants.SP_REVIEW)" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		 
		 def validateOrderAddress = true
		 def addressExistInOrder = true
		 def spcSession = true
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when : 
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then : 
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }
	 

	 def  "validateShipping - session is paypal review session (BBBPayPalConstants.REVIEW) | need not validate address | address not in order (validateOrderAddress & addressExistInOrder - false)" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = Mock()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = Mock()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = false
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 voResp.getShippingAddress() >> addresPPVo 
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS1) >> "Jacob lane" 
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS2) >> "Sector 5"
		 requestMock.getObjectParameter(BBBPayPalConstants.CITY_NAME) >> "Mystic falls"
		 requestMock.getObjectParameter(BBBPayPalConstants.STATE_NAME) >> "Texas"
		 requestMock.getObjectParameter(BBBPayPalConstants.ZIP_CODE) > "12345"
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }

	 
	 def  "validateShipping - session is paypal review session (BBBPayPalConstants.REVIEW) | need not validate address | address exists in order (validateOrderAddress & addressExistInOrder - true)" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = Mock()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = Mock()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = false
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> false
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 voResp.getShippingAddress() >> addresPPVo
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS1) >> "Jacob lane"
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS2) >> "Sector 5"
		 requestMock.getObjectParameter(BBBPayPalConstants.CITY_NAME) >> "Mystic falls"
		 requestMock.getObjectParameter(BBBPayPalConstants.STATE_NAME) >> "Texas"
		 requestMock.getObjectParameter(BBBPayPalConstants.ZIP_CODE) > "12345"
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }

	 def  "validateShipping - Transient profile | Invalid paypal ship address | need not validate address | address exists in order" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = Mock()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = Mock()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = false
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 voResp.getShippingAddress() >> addresPPVo
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> false
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS1) >> "Jacob lane"
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS2) >> "Sector 5"
		 requestMock.getObjectParameter(BBBPayPalConstants.CITY_NAME) >> "Mystic falls"
		 requestMock.getObjectParameter(BBBPayPalConstants.STATE_NAME) >> "Texas"
		 requestMock.getObjectParameter(BBBPayPalConstants.ZIP_CODE) > "12345"
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }

	 def  "validateShipping - Transient profile | Invalid paypal ship address | need not validate address | address exists in order | invalid coupons" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = Mock()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = Mock()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = false
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> false
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 voResp.getShippingAddress() >> addresPPVo
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS1) >> "Jacob lane"
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS2) >> "Sector 5"
		 requestMock.getObjectParameter(BBBPayPalConstants.CITY_NAME) >> "Mystic falls"
		 requestMock.getObjectParameter(BBBPayPalConstants.STATE_NAME) >> "Texas"
		 requestMock.getObjectParameter(BBBPayPalConstants.ZIP_CODE) > "12345"
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }
	 
	 
	 	 
	 def  "validateShipping - populate QAS suggested address | address1 is empty" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = Mock()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = Mock()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = false
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 def suggestedAddress = false
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 voResp.getShippingAddress() >> addresPPVo
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS1) >> ""
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS2) >> "Sector 5"
		 requestMock.getObjectParameter(BBBPayPalConstants.CITY_NAME) >> "Mystic falls"
		 requestMock.getObjectParameter(BBBPayPalConstants.STATE_NAME) >> "Texas"
		 requestMock.getObjectParameter(BBBPayPalConstants.ZIP_CODE) > "12345"
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
		 1 * checkoutToolsSpy.logDebug("Qas address: " + suggestedAddress)
	 }
	 
	 def  "validateShipping - populate QAS suggested address | shipping address is null" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = Mock()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = null
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = false
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 def suggestedAddress = false
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 voResp.getShippingAddress() >> addresPPVo
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS1) >> "Philip road"
		 requestMock.getObjectParameter(BBBPayPalConstants.ADDRESS2) >> "Sector 5"
		 requestMock.getObjectParameter(BBBPayPalConstants.CITY_NAME) >> "Mystic falls"
		 requestMock.getObjectParameter(BBBPayPalConstants.STATE_NAME) >> "Texas"
		 requestMock.getObjectParameter(BBBPayPalConstants.ZIP_CODE) > "12345"
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
		 1 * checkoutToolsSpy.logDebug("Qas address: " + suggestedAddress)
	 }
	 
	 
	 def  "validateShipping - address exists in order | invalid order address" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = false
		 def addressExistInOrder = true
		 def spcSession = false
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.REVIEW,addressVerifyRedirectUrl)
		 
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setProfile(profileMock)
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 
		 
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }

		 def  "validateShipping - shipping relationships exists | current stage is shipping | non-transient profile " () {
			 
		given :
		
		BBBPayPalServiceManager paypalServiceManager = Mock()
		PayPalAddressVerification payPalAddressVerificationMock = Mock()
		BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		BBBOrderImpl orderMock = Mock()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalInput = new PayPalInputVO()
		PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		Profile profileMock = Mock()
		
		def validateOrderAddress = true
		def addressExistInOrder = true
		def relationsExist = true
		def spcSession = true
		def addressVerifyRedirectUrl = "bedbath.com/billing"
		def qasVerified = "true"
		def errorMsg = "No relationship exist"
		def address = "California"
		def internationalOrPoError = true
		def internationalError = true
		def currentLevel = "shipping"
		
		Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		
		checkoutProgressStates.setCurrentLevel(currentLevel)
		
		profileMock.isTransient() >> false
		
		paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		paypalInput.setPaypalSessionBean(paypalSessionBean)
		paypalInput.setSpcSession(spcSession)
		paypalInput.setCheckoutState(checkoutProgressStates)
		paypalInput.setProfile(profileMock)
		
		payPalAddressVerificationMock.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		payPalAddressVerificationMock.validateShippingAddressInOrder(orderMock, addressVerifyVO) >> true
		
		LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg
	
		checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerificationMock)
		checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		
		ServletUtil.getCurrentRequest() >> requestMock
		requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		
		paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		
		addressVerifyVO.setAddress(addresPPVo)
		addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		addressVerifyVO.setInternationalError(internationalError)
		
		when :
		
		payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				
		then :
		
		payPalAddressVerifyVO.getAddress() == addresPPVo
		payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		payPalAddressVerifyVO.isInternationalError() == internationalError
	}
	 
	 	 
	 def  "validateShipping - shipping relationships exists | current stage is cart | profile is transient" () {
		 	 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerificationMock = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		 CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = true
		 def addressExistInOrder = true
		 def relationsExist = true
		 def spcSession = true
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 def currentLevel = BBBCoreConstants.CART
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		 
		 checkoutProgressStates.setCurrentLevel(currentLevel)
		 
		 profileMock.isTransient() >> true 
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setCheckoutState(checkoutProgressStates)
		 paypalInput.setProfile(profileMock)
		 
		 payPalAddressVerificationMock.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerificationMock.validateShippingAddressInOrder(orderMock, addressVerifyVO) >> true
		 
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerificationMock)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }
	 
	 def  "validateShipping - shipping relationships exists | current stage is cart | profile is non-transient | valid shipping address in order" () {
		 
		given :
		
		BBBPayPalServiceManager paypalServiceManager = Mock()
		PayPalAddressVerification payPalAddressVerificationMock = Mock()
		BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		BBBOrderImpl orderMock = Mock()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalInput = new PayPalInputVO()
		PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		Profile profileMock = Mock()
		
		def validateOrderAddress = true
		def addressExistInOrder = true
		def relationsExist = true
		def spcSession = true
		def addressVerifyRedirectUrl = "bedbath.com/billing"
		def qasVerified = "true"
		def errorMsg = "No relationship exist"
		def address = "California"
		def internationalOrPoError = true
		def internationalError = true
		def currentLevel = BBBCoreConstants.CART
		
		Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		
		checkoutProgressStates.setCurrentLevel(currentLevel)
		
		profileMock.isTransient() >> false
		
		paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		paypalInput.setPaypalSessionBean(paypalSessionBean)
		paypalInput.setSpcSession(spcSession)
		paypalInput.setCheckoutState(checkoutProgressStates)
		paypalInput.setProfile(profileMock)
		
		payPalAddressVerificationMock.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		payPalAddressVerificationMock.validateShippingAddressInOrder(orderMock, addressVerifyVO) >> true
		
		LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg
	
		checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerificationMock)
		checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		
		ServletUtil.getCurrentRequest() >> requestMock
		requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		
		paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		
		addressVerifyVO.setAddress(addresPPVo)
		addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		addressVerifyVO.setInternationalError(internationalError)
		
		when :
		
		payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				
		then :
		
		payPalAddressVerifyVO.getAddress() == addresPPVo
		payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		payPalAddressVerifyVO.isInternationalError() == internationalError
		0 * checkoutToolsSpy.logDebug("No error while validating shipping address so validating coupons")
	 }
	 
	 def  "validateShipping - shipping relationships exists | current stage is cart | transient profile | valid shipping address in order" () {
		 
		given :
		
		BBBPayPalServiceManager paypalServiceManager = Mock()
		PayPalAddressVerification payPalAddressVerificationMock = Mock()
		BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		BBBOrderImpl orderMock = Mock()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalInput = new PayPalInputVO()
		PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		Profile profileMock = Mock()
		
		def validateOrderAddress = true
		def addressExistInOrder = true
		def relationsExist = true
		def spcSession = true
		def addressVerifyRedirectUrl = "bedbath.com/billing"
		def qasVerified = "true"
		def errorMsg = "No relationship exist"
		def address = "California"
		def internationalOrPoError = true
		def internationalError = true
		def currentLevel = BBBCoreConstants.CART
		
		Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		
		checkoutProgressStates.setCurrentLevel(currentLevel)
		
		profileMock.isTransient() >> true
		
		paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		paypalInput.setPaypalSessionBean(paypalSessionBean)
		paypalInput.setSpcSession(spcSession)
		paypalInput.setCheckoutState(checkoutProgressStates)
		paypalInput.setProfile(profileMock)
		
		payPalAddressVerificationMock.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		payPalAddressVerificationMock.validateShippingAddressInOrder(orderMock, addressVerifyVO) >> false
		
		LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg
	
		checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerificationMock)
		checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		
		ServletUtil.getCurrentRequest() >> requestMock
		requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		
		paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		
		addressVerifyVO.setAddress(addresPPVo)
		addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		addressVerifyVO.setInternationalError(internationalError)
		
		when :
		
		payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				
		then :
		
		payPalAddressVerifyVO.getAddress() == addresPPVo
		payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		payPalAddressVerifyVO.isInternationalError() == internationalError
		0 * checkoutToolsSpy.logDebug("No error while validating shipping address so validating coupons")
	 }
	 
	 def  "validateShipping - shipping relationships exists | current stage is CART | transient profile | valid shipping address in order | invalid coupons" () {
		 
		given :
		
		BBBPayPalServiceManager paypalServiceManager = Mock()
		PayPalAddressVerification payPalAddressVerificationMock = Mock()
		BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		BBBOrderImpl orderMock = Mock()
		PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		PayPalInputVO paypalInput = new PayPalInputVO()
		PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		Profile profileMock = Mock()
		
		def validateOrderAddress = true
		def addressExistInOrder = true
		def relationsExist = true
		def spcSession = true
		def addressVerifyRedirectUrl = "bedbath.com/billing"
		def qasVerified = "true"
		def errorMsg = "No relationship exist"
		def address = "California"
		def internationalOrPoError = true
		def internationalError = true
		def currentLevel = BBBCoreConstants.CART
		
		Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		
		checkoutProgressStates.setCurrentLevel(currentLevel)
		
		profileMock.isTransient() >> true
		
		paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		paypalInput.setPaypalSessionBean(paypalSessionBean)
		paypalInput.setSpcSession(spcSession)
		paypalInput.setCheckoutState(checkoutProgressStates)
		paypalInput.setProfile(profileMock)
		
		payPalAddressVerificationMock.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		payPalAddressVerificationMock.validateShippingAddressInOrder(orderMock, addressVerifyVO) >> true
		
		LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg
	
		checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerificationMock)
		checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		
		ServletUtil.getCurrentRequest() >> requestMock
		requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		
		paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		paypalServiceManager.validateCoupons(orderMock, profileMock) >> false
		
		addressVerifyVO.setAddress(addresPPVo)
		addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		addressVerifyVO.setInternationalError(internationalError)
		
		when :
		
		payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				
		then :
		
		payPalAddressVerifyVO.getAddress() == addresPPVo
		payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		payPalAddressVerifyVO.isInternationalError() == internationalError
		//0 * checkoutToolsSpy.logDebug("No error while validating shipping address so validating coupons")
	 }
	 
	 
	 def  "validateShipping - shipping relationships exists | current stage is shipping | transient profile" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		 CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = true
		 def addressExistInOrder = true
		 def relationsExist = true
		 def spcSession = true
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 def currentLevel = "shipping"
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		 
		 checkoutProgressStates.setCurrentLevel(currentLevel)
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setCheckoutState(checkoutProgressStates)
		 paypalInput.setProfile(profileMock)
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput)  >> true
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> true
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		 
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
	 }
	 

	 def  "validateShipping - shipping relationships exists | current stage is shipping | QAS unverified" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		 CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = true
		 def addressExistInOrder = true
		 def relationsExist = true
		 def spcSession = true
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "false"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 def currentLevel = "shipping"
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 Map<String,String> checkoutFailureUrlMap = new HashMap<>()
		 
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		 checkoutFailureUrlMap.put(currentLevel, addressVerifyRedirectUrl)
		 
		 checkoutProgressStates.setCurrentLevel(currentLevel)
		 checkoutProgressStates.setCheckoutFailureURLs(checkoutFailureUrlMap)
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setCheckoutState(checkoutProgressStates)
		 paypalInput.setProfile(profileMock)
		 
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput)  >> true
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> false
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		   
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddressErrorList() == null
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
		 payPalAddressVerifyVO.getRedirectUrl().equals(addressVerifyRedirectUrl)
		 payPalAddressVerifyVO.getRedirectState().equals(BBBPayPalConstants.SP_REVIEW)
	 }
	 
	 def  "validateShipping - shipping relationships exists | current stage is shipping | invalid coupons" () {
		 
		 given :
		 
		 BBBPayPalServiceManager paypalServiceManager = Mock()
		 PayPalAddressVerification payPalAddressVerification = Mock()
		 BBBGetExpressCheckoutDetailsResVO voResp = new BBBGetExpressCheckoutDetailsResVO()
		 BBBOrderImpl orderMock = Mock()
		 PayPalAddressVerifyVO addressVerifyVO = new PayPalAddressVerifyVO()
		 PayPalInputVO paypalInput = new PayPalInputVO()
		 PayPalAddressVerifyVO payPalAddressVerifyVO = new PayPalAddressVerifyVO()
		 BBBPayPalSessionBean paypalSessionBean = new BBBPayPalSessionBean()
		 LblTxtTemplateManager LblTxtTemplateManagerMock = Mock()
		 BBBAddressPPVO addresPPVo = new BBBAddressPPVO()
		 CheckoutProgressStates checkoutProgressStates = new CheckoutProgressStates()
		 Profile profileMock = Mock()
		 
		 def validateOrderAddress = true
		 def addressExistInOrder = true
		 def relationsExist = true
		 def spcSession = true
		 def addressVerifyRedirectUrl = "bedbath.com/billing"
		 def qasVerified = "true"
		 def errorMsg = "No relationship exist"
		 def address = "California"
		 def internationalOrPoError = true
		 def internationalError = true
		 def currentLevel = "shipping"
		 
		 Map<String,String> addressVerifyRedirectUrlMap = new HashMap<>()
		 Map<String,String> checkoutFailureUrlMap = new HashMap<>()
		 
		 addressVerifyRedirectUrlMap.put(BBBPayPalConstants.SP_REVIEW,addressVerifyRedirectUrl)
		 checkoutFailureUrlMap.put(currentLevel, addressVerifyRedirectUrl)
		 
		 checkoutProgressStates.setCurrentLevel(currentLevel)
		 checkoutProgressStates.setCheckoutFailureURLs(checkoutFailureUrlMap)
		 
		 profileMock.isTransient() >> true
		 
		 paypalSessionBean.setValidateOrderAddress(validateOrderAddress)
		 paypalInput.setPaypalSessionBean(paypalSessionBean)
		 paypalInput.setSpcSession(spcSession)
		 paypalInput.setCheckoutState(checkoutProgressStates)
		 paypalInput.setProfile(profileMock)
		 
		 
		 payPalAddressVerification.getAddressVerifyRedirectUrl() >> addressVerifyRedirectUrlMap
		 payPalAddressVerification.validatePayPalShippingAddress(voResp, orderMock, addressVerifyVO, paypalInput)  >> true
		 LblTxtTemplateManagerMock.getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null) >> errorMsg

		 checkoutToolsSpy.setPaypalServiceManager(paypalServiceManager)
		 checkoutToolsSpy.setPayPalAddressVerification(payPalAddressVerification)
		 checkoutToolsSpy.setLblTxtTemplateManager(LblTxtTemplateManagerMock)
		 
		 ServletUtil.getCurrentRequest() >> requestMock
		 requestMock.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED) >> qasVerified
		 
		 paypalServiceManager.addressInOrder(orderMock) >> addressExistInOrder
		 paypalServiceManager.ShippingCommerceRelationshipExist(orderMock) >> relationsExist
		 paypalServiceManager.validateCoupons(orderMock, profileMock) >> false
		 
		 addressVerifyVO.setAddress(addresPPVo)
		 addressVerifyVO.setInternationalOrPOError(internationalOrPoError)
		 addressVerifyVO.setInternationalError(internationalError)
		   
		 when :
		 
		 payPalAddressVerifyVO = checkoutToolsSpy.validateShipping(voResp, orderMock, addressVerifyVO, paypalInput)
				 
		 then :
		 
		 payPalAddressVerifyVO.getAddress() == addresPPVo
		 payPalAddressVerifyVO.isInternationalOrPOError() == internationalOrPoError
		 payPalAddressVerifyVO.isInternationalError() == internationalError
		 payPalAddressVerifyVO.getRedirectUrl().equals(addressVerifyRedirectUrl)
		 payPalAddressVerifyVO.getRedirectState().equals(currentLevel)
	 }

	 
	 /*==================================================
	  * validateShipping - Test cases - ENDS			*
	  * =================================================
	  */
	 

	 /*===========================================================
	  * isPayPalCallFromCart - Test cases - STARTS				 *
	  * Method Signature :				 						 *
	  * public boolean isPayPalCallFromCart(String currentLevel) *
	  * ==========================================================
	  */
	 
	 def "isPayPalCallFromCart - paypal is called from cart - happy flow" () {
		 
		 given : 
		    
		 		def currentLevel = BBBCoreConstants.CART
		 
		 when : 
		 		def paypalCallFromCart =  checkoutToolsSpy.isPayPalCallFromCart(currentLevel)
		 
		 then : 
		  
		 		paypalCallFromCart == true		 
	 }
	
	 def "isPayPalCallFromCart - paypal is not called from cart" () {
		 
		 given :
			
				 def currentLevel = "shipping"
		 
		 when :
				 def paypalCallFromCart =  checkoutToolsSpy.isPayPalCallFromCart(currentLevel)
		 
		 then :
		  
				 paypalCallFromCart == false
	 }

	 def "isPayPalCallFromCart - current checkout level is corrupted" () {
		 
		 given :
			
				 def currentLevel = null
		 
		 when :
				 def paypalCallFromCart =  checkoutToolsSpy.isPayPalCallFromCart(currentLevel)
		 
		 then :
		  
				 paypalCallFromCart == false
	 }
	 	 
	 
	 /*===========================================================
	  * isPayPalCallFromCart - Test cases - ENDS				 *
	  * public boolean isPayPalCallFromCart(String currentLevel) *
	  * ==========================================================
	  */	 
	
}
