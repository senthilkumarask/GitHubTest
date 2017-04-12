package com.bbb.browse

import java.util.HashMap;
import java.util.List
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.email.BBBEmailHelper
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.email.TemplateEmailException
import atg.userprofiling.email.TemplateEmailInfoImpl;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy 
 * 
 * This class is written to unit test the BBBEmailSenderFormHandler 
 *
 */

public class BBBEmailSenderFormHandlerSpecification extends BBBExtendedSpec {

	private BBBEmailSenderFormHandler emailSenderFormHandler
	private TemplateEmailInfoImpl emailInfoMock
	private BBBTemplateEmailSender emailSenderMock
	private BBBCatalogTools catalogToolsMock 
	private TemplateEmailInfoImpl templateEmailInfoMock
	
	def senderEmail
	def recipientEmail
	def templateUrlName
	def templateUrl
	def senderNameParamName
	def senderName
	def senderEmailParamName
	def recipientName
	def recipientNameParamName
	def recipientEmailParamName
	def messageParamName
	def message
	def subjectParamName
	def subject
	def siteIdParamName
	def siteId
	def contextPath
	def storeContextPath
	def serverName
	def serverDomain 
	
	def setup () {
		
		emailInfoMock = Mock()
		emailSenderMock = Mock()
		catalogToolsMock = Mock()
		templateEmailInfoMock = Mock()
		
		templateUrlName = "registrationSuccessTemplate"
		
		senderEmail = "tester@yopmail.com"
		recipientEmail = "tester2@yopmail.com"
		templateUrlName = "RegistrationSuccessTemplate"
		templateUrl = "template.bbb.com/templates/registration/registrationSuccess"
		senderNameParamName = "senderName"
		senderName = "Email Template Tester"
		senderEmailParamName = "senderEmail"
		recipientName = "Email Template tester 2"
		recipientNameParamName = "recipientName"
		recipientEmailParamName = "recipientEmail"
		messageParamName = "message"
		message = "You have been successfully registered at Bed Bath & Beyond"
		subjectParamName = "subject"
		subject = "Registration successful at Bebbath"
		siteIdParamName = "siteId"
		siteId = "bbb"
		contextPath = "/mobile"
		storeContextPath = "/store"
		serverName = "Box22"
		serverDomain = "bedbath-22.sapient.com"
		
		emailSenderFormHandler = new BBBEmailSenderFormHandler([
								 mSubject : subject, mRecipientName : recipientName, mRecipientEmail : recipientEmail,
								 mSenderName : senderName, mSenderEmail : senderEmail, mSiteId : siteId, mMessage : message,
								 mTemplateUrl : templateUrl, mTemplateUrlName : templateUrlName, mRecipientNameParamName : recipientNameParamName,
								 mRecipientEmailParamName : recipientEmailParamName, mSenderNameParamName : senderNameParamName,
								 mSenderEmailParamName : senderEmailParamName, mMessageParamName : messageParamName, 
								 mSiteIdParamName : siteIdParamName, mSubjectParamName : subjectParamName,
								 mActionResult : BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS,
								 mEmailSender : emailSenderMock, mCatalogTools : catalogToolsMock, 
								 mContextPath : contextPath, mStoreContextPath : storeContextPath,
								 mServerName : serverName, mEmailInfo : templateEmailInfoMock
								 ])
		
	}

	/*==================================================================
	 * 
	 * handleSend - test cases - starts
	 * 
	 *  Method signature : 
	 *  
	 *  public boolean handleSend(DynamoHttpServletRequest pRequest, 
	 *  DynamoHttpServletResponse pResponse)
	 *   
	 *  throws ServletException, IOException 
	 *
	 *==================================================================
	 */
	
	def "handleSend - email sent successfully (happy flow)" () {
		
		given :
		
		def channel = BBBCoreConstants.MOBILEWEB
		List<String> configValue = new ArrayList<>()
		
		configValue.addAll([serverDomain])
		
		1 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel 
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> configValue
		
		when : 
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		then : 
				
		emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)
	}

	def "handleSend -> collectParams -  Exception while fetching server name from catalog and Template Exception | BBBSystemException | TemplateEmailException" () {
		
		given :
		
		def channel = BBBCoreConstants.MOBILEAPP
		List<String> configValue = new ArrayList<>()
		
		configValue.addAll([serverDomain])
		
		1 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_) >> {throw new TemplateEmailException("")}
		1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw new BBBSystemException("")}
		
		when :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		then :
				
		emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)
	}
	
	def "handleSend -> collectParams -  Exception while fetching server name from catalog | BBBBusinessException" () {
		
		given :
		
		def channel = BBBCoreConstants.MOBILEWEB
		List<String> configValue = new ArrayList<>()
		
		configValue.addAll([serverDomain])
		
		1 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw new BBBBusinessException("")}
		
		when :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		then :
				
		emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)
	}
	
	
	/*
	 * Alternative branch covered : 
	 * 
	 * #660 - if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)))
	 *  Channel is neither Mobile web nor mobile app
	 * 
	 */
	
	def "handleSend -> collectParams -  Channel is Store" () {
		
		given :
		
		def channel = BBBCoreConstants.BBB_DESKTOP
				
		1 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel
		0 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE)
		
		when :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		then :
				
		emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)
	}
	
	/*
	 * Alternative branches covered : 
	 * 
	 * #665 - if(configValue != null && configValue.size() > 0) -> configValue - null
	 * 
	 */
	
	def "handleSend - No matching config key found for server name given | (configKeys - null)" () {
		
		given :
		
		def channel = BBBCoreConstants.MOBILEWEB
		
		1 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> null
		
		when :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		then :
				
		emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #665 - if(configValue != null && configValue.size() > 0) -> configValue - empty
	 *
	 */
	
	def "handleSend - No matching config key found for server name given | (configKeys - empty)" () {
		
		given :
		
		def channel = BBBCoreConstants.MOBILEWEB
		
		1 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> []
		
		when :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		then :
				
		emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)
	}
	
	def "handleSend - sender Email is invalid" () {
		
		given :
		
		/*def channel = BBBCoreConstants.MOBILEWEB
		List<String> configValue = new ArrayList<>()
		
		configValue.addAll([serverDomain])*/
		emailSenderFormHandler.setSenderEmail("")
		0 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		0 * requestMock.getHeader(BBBCoreConstants.CHANNEL)
		0 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE)
		
		expect :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
	}
	
	def "handleSend - recipient Email is invalid" () {
		
		given :
		
		/*def channel = BBBCoreConstants.MOBILEWEB
		List<String> configValue = new ArrayList<>()
		
		configValue.addAll([serverDomain])*/
		emailSenderFormHandler.setRecipientEmail("")
		emailSenderFormHandler.setSenderEmail("")
		0 * emailSenderMock.sendEmailMessage(templateEmailInfoMock,*_)
		0 * requestMock.getHeader(BBBCoreConstants.CHANNEL)
		0 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE)
		
		expect :
		
		emailSenderFormHandler.handleSend(requestMock, responseMock)
		
		/*then :
				
		! emailSenderFormHandler.getActionResult().equals(BBBEmailSenderFormHandler.MSG_ACTION_SUCCESS)*/
	}
	
	/*==================================================================
	 * handleSend - test cases - ends								   * 
	 *==================================================================
	 */

	/*==================================================================
	 * 
	 * collectParams - Test cases - starts
	 * 
	 *  Method signature : 
	 *  
	 *  protected Map collectParams(DynamoHttpServletRequest pRequest)
	 * 
	 * ==================================================================
	 */
	
	/*==================================================================
	 * Alternative branch covered :
	 *
	 * #660 - if(channel != null && 
	 * (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || 
	 * channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)))
	 *
	 *  Channel is MOBILEWEB
	 *==================================================================
	 */
	
	def "collectParams -  Email parameters successfully populated" () {
		
		given :
		
		def channel = BBBCoreConstants.MOBILEWEB
		Map emailParams = new HashMap()
		def requestServerName = "bedbath-22.sapient.com"
		def requestContextPath = "/bbb"
		
		List<String> configValue = new ArrayList<>()
		
		configValue.addAll([serverDomain])
		
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> channel
		requestMock.getServerName() >> requestServerName
		requestMock.getContextPath() >> requestContextPath
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> configValue
		
		when :
		
		emailParams = emailSenderFormHandler.collectParams(requestMock)
		
		then :
				
		emailParams.get(serverName).equals(serverDomain)
		emailParams.get(contextPath).equals(storeContextPath)
	}
	
	/*
	 * Alternative branch covered :
	 *
	 * #660 - if(channel != null && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)))
	 *
	 *  Channel is null
	 *
	 */
		
	def "collectParams -  Channel is corrupted/invalid (null)" () {
		
		given :
		
		def channel = null
		Map emailParams = new HashMap()
		def requestServerName = "bedbath-22.sapient.com"
		def requestContextPath = "/bbb"
		
		requestMock.getServerName() >> requestServerName
		requestMock.getContextPath() >> requestContextPath
		0 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE)
		
		when :
		
		emailParams = emailSenderFormHandler.collectParams(requestMock)
		
		then :
				
		emailParams.get(serverName).equals(requestServerName)
		emailParams.get(contextPath).equals(requestContextPath)
	}
	
	
	/*====================================
	 * collectParams - Test cases - ENDS * 
	 * ===================================
	 */
	
	
	/*==================================================================
	 *
	 * processException - test cases - starts
	 *
	 *  Method signature :
	 *
	 *  public void processException(Throwable pException, String pMsgId,
	 *  DynamoHttpServletRequest pRequest, 
	 *  DynamoHttpServletResponse pResponse)
	 *
	 *  throws ServletException, IOException
	 *
	 *==================================================================
	 */

	def "processException - Exception is processed successfully (happy flow)" () {
		
		given :
		
		Throwable pException = new Throwable("Exception while processing template email")
		
		expect : 
		emailSenderFormHandler.processException(pException, "Exception01", requestMock, responseMock)
	}
	
	
	def "processException - Exception to be processed is invalid (null)" () {
		
		given :
		
		emailSenderFormHandler.processException(null, "Exception01", requestMock, responseMock)
	}
	
		
	/*==================================================================
	 * processException - test cases - ENDS							   * 
	 *==================================================================
	 */
		
		
}
