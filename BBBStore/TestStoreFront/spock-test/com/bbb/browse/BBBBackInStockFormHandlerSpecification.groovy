package com.bbb.browse

import atg.core.util.StringUtils;
import atg.multisite.Site
import atg.multisite.SiteContext
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBWebServiceConstants
import com.bbb.constants.TBSConstants;
import com.bbb.email.BBBEmailHelper;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.utils.BBBUtility
import com.sun.org.apache.xml.internal.resolver.readers.CatalogReader;

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is written to unit test the BBBBackInStockFormHandler
 * 
 *  Changes in Java file : 
 *  
 *  invokeSendOOSTibcoEmail(emailParams)
 *  getSiteIdFromManager()
 *  invokeSendUnsubscribeTibcoEmail(emailParams)
 *
 */

public class BBBBackInStockFormHandlerSpecification extends BBBExtendedSpec {

	private BBBBackInStockFormHandler backInStockFormHandler
	
	private LblTxtTemplateManager lblTxtTemplateManagerMock
	def String successURL
	def  String errorURL
	def  String catalogRefId
	def  String emailAddress
	def  String productId
	def  String productName
	def  BBBCatalogTools catalogToolsMock
	def  SiteContext siteContextMock
	def  String fromPage
	private Map<String,String> successUrlMap
	private Map<String,String> errorUrlMap
	def confirmEmail
	def customerName
	private Site siteMock 
	def siteId 
	def productDisplayName
	Locale requestLocale
	def contextPath
	
	def setup() {
		
		lblTxtTemplateManagerMock = Mock()
		catalogToolsMock = Mock()
		siteContextMock = Mock()
		siteMock = Mock()
		
		successUrlMap = new HashMap<>()
		errorUrlMap = new HashMap<>()
		contextPath = "/store"
		
		siteId = "BBB_US"
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		requestLocale = new Locale("en")
		
		requestMock.getLocale() >> requestLocale
		requestMock.getContextPath() >> contextPath
		
		successURL = "bedbath.com/oos/emailSent"
		errorURL  = "bedbath.com/oos/emailError"
		catalogRefId = "sku01"
		productId = "product01"
		productName = "German Glass"
		fromPage = "cart"
		emailAddress = "tester@yopmail.com"
		successUrlMap.put("oos",successURL)
		errorUrlMap.put("oos",errorURL)
		confirmEmail = emailAddress
		customerName = "John Dsouza"
		productDisplayName = "German glass - golden frame"
		
		backInStockFormHandler = new BBBBackInStockFormHandler([lblTxtTemplateManager : lblTxtTemplateManagerMock, mSuccessURL : successURL,
								 mErrorURL : errorURL, mCatalogRefId : catalogRefId, mEmailAddress : emailAddress,
								 mProductId : productId, mProductName : productName, catalogTools : catalogToolsMock,
								 siteContext : siteContextMock, fromPage : fromPage, successUrlMap : successUrlMap,
								 errorUrlMap : errorUrlMap, mConfirmEmailAddress : confirmEmail, 
								 mCustomerName : customerName
								 ]) 
	}
	
	
	/*=========================================
	 * 
	 * handleSendOOSEmail - Test cases starts
	 * 
	 * Method signature : 
	 * 
	 * public boolean handleSendOOSEmail(
	 *   DynamoHttpServletRequest pRequest,
	 * 	 DynamoHttpServletResponse pResponse
	 *  )
	 *  throws ServletException, IOException 
	 *  
	 * ========================================
	 */
	
	/*
	 * Alternative branches covered :
	 * 
	 * #405 - (getNoJavascriptErrorURL() != null) - NoJavascriptErrorURL exists
	 * 
	 */
	
	def "handleSendOOSEmail - Out of stock email sent successfully" () {
		
		given : 
		
		SKUDetailVO skuDetailVO = new SKUDetailVO()
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		skuDetailVO.setDisplayName(productDisplayName)
		
		backInStockFormHandlerSpy.setNoJavascriptErrorURL(errorURL)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> [siteId]
		1 * backInStockFormHandlerSpy.invokeSendOOSTibcoEmail(_) >> {}
		
		when : 		
		
		backInStockFormHandlerSpy.handleSendOOSEmail(requestMock, responseMock)
		
		then : 
		
		backInStockFormHandlerSpy.getSiteFlag().equals(siteId)
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #535 - if(StringUtils.isEmpty(getProductName()) && !StringUtils.isEmpty(getCatalogRefId())) - productName is empty & SKU ID not empty
	 * #404 - if ((getNoJavascriptSuccessURL() != null) || (getNoJavascriptErrorURL() != null)) - Both NoJavvScriptErrorURL & successURL are null
	 *  
	 */
	def "handleSendOOSEmail -> createEmailParameters - productName is invalid(empty) & SKU Id is invalid (empty) " () {
		
		given :
		
		SKUDetailVO skuDetailVO = new SKUDetailVO()
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		skuDetailVO.setDisplayName(productDisplayName)
		
		backInStockFormHandlerSpy.setProductName("")
		
		1 * catalogToolsMock.getSKUDetails(*_) >> skuDetailVO
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> [siteId]
		1 * backInStockFormHandlerSpy.invokeSendOOSTibcoEmail(_) >> {}
		
		when :
		
		backInStockFormHandlerSpy.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandlerSpy.getSiteFlag().equals(siteId)
		backInStockFormHandlerSpy.getProductName().equals(skuDetailVO.getDisplayName())
	}
	
	
	/*
	 * Alternative branches covered :
	 *
	 * #537 - if(vo != null) - SKUDetailsVO is null
	 * #548 - if(BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel())) - Channel is MobileWeb
	 * #405 - (getNoJavascriptErrorURL() != null) - NoJavascriptErrorURL - is null
	 * 
	 */
	  
	def "handleSendOOSEmail -> createEmailParameters - SKU details not avaiable for the given SKU" () {
		
		given :
		
		SKUDetailVO skuDetailVO = null
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		backInStockFormHandlerSpy.setProductName("")
		backInStockFormHandlerSpy.getNoJavascriptErrorURL()
		backInStockFormHandlerSpy.setNoJavascriptSuccessURL(successURL)
		
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
		
		1 * catalogToolsMock.getSKUDetails(*_) >> skuDetailVO
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> [siteId]
		1 * backInStockFormHandlerSpy.invokeSendOOSTibcoEmail(_) >> {}
		
		when :
		
		backInStockFormHandlerSpy.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandlerSpy.getSiteFlag().equals(siteId)
	}
	
	/*
	 * Alternative branches covered : 
	 * 
	 * #371 - if (!BBBUtility.isValidEmail(getEmailAddress()) - emailAddress empty
	 * 
	 */
	
	def "handleSendOOSEmail - Product name is invalid (empty) " () {
		
		given :
		
		backInStockFormHandler.setEmailAddress("")
		backInStockFormHandler.setProductName("")
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandler.getProductName().isEmpty()
	}
	
	
	def "handleSendOOSEmail - confirm email address mismatches" () {
		
		given :
		
		backInStockFormHandler.setConfirmEmailAddress("tester2@yopmail.com")
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		!backInStockFormHandler.confirmEmailAddress.equals(emailAddress)
	}
	
	def "handleSendOOSEmail - Exception while prepading email data | BBBBusinessException" () {
		
		given :
		
		backInStockFormHandler.setProductName("")
		
		catalogToolsMock.getSKUDetails(*_) >> {throw new BBBBusinessException("")}
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandler.getSiteFlag() == null
	}
	
	def "handleSendOOSEmail - Exception while prepading email data | BBBSystemException" () {
		
		given :
		
		backInStockFormHandler.setProductName("")
		
		catalogToolsMock.getSKUDetails(*_) >> {throw new BBBSystemException("")}
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandler.getSiteFlag() == null
	}
	
	def "handleSendOOSEmail - confirm email address is invalid" () {
		
		given :
		
		Locale requestLocale = new Locale("en")
		requestMock.getLocale() >> requestLocale
		
		backInStockFormHandler.setConfirmEmailAddress("tester2yopmail.com")
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		!backInStockFormHandler.getConfirmEmailAddress().equals(emailAddress)
	}
	
	def "handleSendOOSEmail - ProductID is invalid(empty)" () {
		
		given :
		
		backInStockFormHandler.setProductId("")
		backInStockFormHandler.setNoJavascriptErrorURL(errorURL)
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandler.getProductId().isEmpty()
		
	}
	
	def "handleSendOOSEmail - ProductID is invalid(null)" () {
		
		given :
		
		backInStockFormHandler.setProductId()
		backInStockFormHandler.setNoJavascriptErrorURL()
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandler.getProductId() == null
		
	}
	
	def "handleSendOOSEmail - SKU ID is invalid(empty)" () {
		
		given :
		
		backInStockFormHandler.setCatalogRefId("")
		backInStockFormHandler.setNoJavascriptErrorURL(errorURL)
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then : 
		
		backInStockFormHandler.getCatalogRefId().isEmpty()
		
	}
	
	def "handleSendOOSEmail - SKU ID is invalid(null)" () {
		
		given :
		
		backInStockFormHandler.setCatalogRefId()
		backInStockFormHandler.setNoJavascriptErrorURL()
		
		when :
		
		backInStockFormHandler.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandler.getCatalogRefId() == null
		
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #405 - (getNoJavascriptErrorURL() != null) - NoJavascriptErrorURL exists
	 * #567 - if(null != siteIds && !siteIds.isEmpty()) - siteIDs empty
	 * siteFlag will not be set in this scenario.
	 */
	
	def "handleSendOOSEmail - SKU is not present in any of the site (siteIDs empty)" () {
		
		given :
		
		SKUDetailVO skuDetailVO = new SKUDetailVO()
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		skuDetailVO.setDisplayName(productDisplayName)
		
		backInStockFormHandlerSpy.setNoJavascriptErrorURL(errorURL)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> []
		1 * backInStockFormHandlerSpy.invokeSendOOSTibcoEmail(_) >> {}
		
		when :
		
		backInStockFormHandlerSpy.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandlerSpy.getSiteFlag() == null
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #567 - if(null != siteIds && !siteIds.isEmpty()) - siteIDs null
	 * #543 - if(StringUtils.isEmpty(getProductName()) && !StringUtils.isEmpty(getCatalogRefId())) - catalogRefId - null
	 * siteFlag will not be set in this scenario.
	 */
	
	def "handleSendOOSEmail - SKU is not present in any of the site (siteIDs null)" () {
		
		given :
		
		SKUDetailVO skuDetailVO = new SKUDetailVO()
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		skuDetailVO.setDisplayName(productDisplayName)
		backInStockFormHandlerSpy.setProductName("")
		backInStockFormHandlerSpy.getCatalogRefId() >> catalogRefId >> catalogRefId >> catalogRefId >> null
		backInStockFormHandlerSpy.setNoJavascriptErrorURL(errorURL)
		
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> null
		1 * backInStockFormHandlerSpy.invokeSendOOSTibcoEmail(_) >> {}
		
		when :
		
		backInStockFormHandlerSpy.handleSendOOSEmail(requestMock, responseMock)
		
		then :
		
		backInStockFormHandlerSpy.getSiteFlag() == null
	}
	
	/*=====================================
	 * handleSendOOSEmail - Test cases ends
	 *=====================================
	 */
	
	
	/*==================================================
	 * handleUnSubscribeOOSEmail - Test cases starts
	 *==================================================
	 */
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - unsubscribe OOSEmail - requested successfully" () {
		
		given : 
		
		List siteIds = new ArrayList<>()
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		siteIds.add("BBB_US")
		requestMock.getContextPath() >> contextPath
		
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> siteIds
		1 * backInStockFormHandlerSpy.invokeSendUnsubscribeTibcoEmail(_) >> {}
		
		expect :
		 
		!backInStockFormHandlerSpy.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
	}
	
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - No matching siteIDs (null) found in the catalog" () {
		
		given :
		
		List siteIds = new ArrayList<>()
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		siteIds.add("BBB_US")
		requestMock.getContextPath() >> contextPath
		
		1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> null
		1 * backInStockFormHandlerSpy.invokeSendUnsubscribeTibcoEmail(_) >> {}
		
		expect :
		
		!backInStockFormHandlerSpy.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
	}
	
	
	/*Alternative branches covered :
	 * #505 -  if ((getNoJavascriptSuccessURL() != null) || (getNoJavascriptErrorURL() != null)) - NoJavascriptSuccessURL & NojavascriptErrorUrl are set
	 */
	
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - Exception while getting site data | BBBBusinessException" () {
		
		given :
		
		List siteIds = new ArrayList<>()
		def success 
		
		siteIds.add("BBB_US")
		requestMock.getContextPath() >> contextPath
		
		backInStockFormHandler.setEmailAddress(emailAddress)
		backInStockFormHandler.setCatalogRefId(catalogRefId)
		backInStockFormHandler.setProductId(productId)
		backInStockFormHandler.setProductName(productName)
		backInStockFormHandler.setNoJavascriptSuccessURL(successURL)
		backInStockFormHandler.setNoJavascriptErrorURL(errorURL)
		1*catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> {throw new BBBBusinessException("")}
		
		when :
		
		success = backInStockFormHandler.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
		then :
			!success
	}
	
	/*Alternative branches covered :	 
	 * #505 -  (getNoJavascriptErrorURL() != null) - NojavascriptErrorUrl is set
	 */
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - Exception while getting site data | BBBSystemException" () {
		
		given :
		
		List siteIds = new ArrayList<>()
		def success
		
		siteIds.add("BBB_US")
		requestMock.getContextPath() >> contextPath
		backInStockFormHandler.setNoJavascriptErrorURL(errorURL)
		1*catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> {throw new BBBSystemException("")}
		
		when :
		
		success = backInStockFormHandler.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
		then :
		
			!success
	}
	
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - Exception while constructing email parameters" () {
		
		given :
		
		List siteIds = new ArrayList<>()
		def success
		
		siteIds.add("BBB_US")
		requestMock.getContextPath() >> contextPath
		
		backInStockFormHandler.setEmailAddress("testeryopmail.com")
		backInStockFormHandler.setCatalogRefId("")
		backInStockFormHandler.setProductId("")
		backInStockFormHandler.setProductName("")
		
		backInStockFormHandler.setNoJavascriptSuccessURL()
		backInStockFormHandler.setNoJavascriptErrorURL()
		
		1 * lblTxtTemplateManagerMock.getErrMsg("err_email_add_invalid", requestLocale.getLanguage() , null, null) >> "Email addres is invalid"
		1 * lblTxtTemplateManagerMock.getErrMsg("err_sku_add_invalid", requestLocale.getLanguage() , null, null) >> "SKU ID is is invalid"
		1 * lblTxtTemplateManagerMock.getErrMsg("err_product_add_invalid", requestLocale.getLanguage() , null, null) >> "product ID is invalid"
		1 * lblTxtTemplateManagerMock.getErrMsg("err_product_name_add_invalid", requestLocale.getLanguage() , null, null) >> "Product name is invalid"
		
		when :
		
		success = backInStockFormHandler.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
		then :
			!success
	}
	
	/*Alternative branches covered :
	 * #505 -  if ((getNoJavascriptSuccessURL() != null) || (getNoJavascriptErrorURL() != null)) - NoJavascriptSuccessURL & NojavascriptErrorUrl are null
	 * #478 - if (!BBBUtility.siteIsTbs(siteId) && StringUtils.isNotEmpty(getFromPage())) - FromPage attribute is empty
	 */
	
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - Exception while getting site data " () {
		
		given :
		
		List siteIds = new ArrayList<>()
		def success
		
		siteIds.add("BBB_US")
		requestMock.getContextPath() >> contextPath
		backInStockFormHandler.setFromPage("")
		backInStockFormHandler.setEmailAddress(emailAddress)
		backInStockFormHandler.setCatalogRefId(catalogRefId)
		backInStockFormHandler.setProductId(productId)
		backInStockFormHandler.setProductName(productName)
		backInStockFormHandler.setNoJavascriptSuccessURL()
		backInStockFormHandler.setNoJavascriptErrorURL()
		1*catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> {throw new BBBBusinessException("")}
		
		when :
		
		success = backInStockFormHandler.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
		then :
			!success
			0 * backInStockFormHandler.setSuccessURL(_)
			0 * backInStockFormHandler.setErrorURL(_)
	}
	
	
	/*
	 * #478 - if (!BBBUtility.siteIsTbs(siteId) && StringUtils.isNotEmpty(getFromPage())) - siteId is TBS
	 * 
	 */
	
	
	def "handleRequestUnSubscribeOOSEmail -> handleUnSubscribeOOSEmail - TBS site request to unsubscribe OOS Email" () {
		
		given :
		
		List siteIds = new ArrayList<>()
		def success
		
		BBBBackInStockFormHandler backInStockFormHandlerSpy = Spy()
		
		siteIds.add(TBSConstants.SITE_TBS_BBB)
		backInStockFormHandlerSpy.getSiteIdFromManager() >> TBSConstants.SITE_TBS_BBB 
		requestMock.getContextPath() >> contextPath
		
		populateBackInStockFormHandlerSpy(backInStockFormHandlerSpy)
		
		1*catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> {throw new BBBBusinessException("")}
		
		when :
		
		success = backInStockFormHandlerSpy.handleRequestUnSubscribeOOSEmail(requestMock, responseMock)
		
		then :
			!success
			0 * backInStockFormHandlerSpy.setSuccessURL(_)
			0 * backInStockFormHandlerSpy.setErrorURL(_)
	}

	
	/*==================================================
	 * handleUnSubscribeOOSEmail - Test cases ends
	 *==================================================
	 */
	
	/*
	 * 
	 * Data populating methods 
	 * 
	 */
	
	private populateBackInStockFormHandlerSpy(BBBBackInStockFormHandler backInStockFormHandlerSpy) {
		
		backInStockFormHandlerSpy.setEmailAddress(emailAddress)
		backInStockFormHandlerSpy.setConfirmEmailAddress(confirmEmail)
		backInStockFormHandlerSpy.setCatalogRefId(catalogRefId)
		backInStockFormHandlerSpy.setProductId(productId)
		backInStockFormHandlerSpy.setProductName(productName)
		backInStockFormHandlerSpy.setNoJavascriptSuccessURL(null)
		backInStockFormHandlerSpy.setNoJavascriptErrorURL(null)
		backInStockFormHandlerSpy.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		backInStockFormHandlerSpy.setSuccessURL(successURL)
		backInStockFormHandlerSpy.setErrorURL(errorURL)
		backInStockFormHandlerSpy.setCatalogRefId(catalogRefId)
		backInStockFormHandlerSpy.setSiteContext(siteContextMock)
		backInStockFormHandlerSpy.setCatalogTools(catalogToolsMock)
	}
	
	
	
}
