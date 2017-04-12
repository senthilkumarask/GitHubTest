package com.bbb.browse

import atg.core.util.StringUtils
import atg.json.JSONException;
import atg.multisite.Site
import atg.multisite.SiteContext
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile
import atg.userprofiling.email.TemplateEmailException
import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author Velmurugan Moorthy
 * 
 * Changes in Java file :
 *  
 * invokeHandleSend() - added
 * invokeSendEmail() - added 
 * getSiteIdFromContextManager() - added
 * populateSuccessInfoJSON() - added
 * 
 *
 */
public class EmailAFriendFormHandlerSpecification extends BBBExtendedSpec {
	
	private EmailAFriendFormHandler emailAFriendFormHandler
	private BBBEmailSenderFormHandler emailSenderFormHandlerMock
	def pValidateCaptcha
	def emailForSender
	private LblTxtTemplateManager lblTxtTemplateManagerMock
	def ccFlag
	def productIdParamName
	def pCaptchaAnswer
	def emailType
	def currentPageURL
	def currentSite
	def templateUrl
	private SiteContext siteContextMock
	def currentCatalogURL
	def tempEmail
	Map<String,String> errorUrlMap
	def pFromPage
	Map<String,String> pSuccessUrlMap
	def pQueryParam
	private ProductManager productManagerMock
	def siteId
	private Profile profileMock
	private Site siteMock
	
	def profileId
	private Locale localeObject
	def productId 
	private BBBCatalogTools catalogToolsMock 
	def productLongDescription
	def productName
	
	def senderEmail
	def recipientEmail
	def contextPath 
	def setup() {
		
		
		emailSenderFormHandlerMock = Mock()
		lblTxtTemplateManagerMock = Mock()
		siteContextMock = Mock()
		siteMock = Mock()
		profileMock = Mock()
		productManagerMock = Mock()
		
		pSuccessUrlMap = new HashMap<>()
		errorUrlMap = new HashMap<>()
		
		siteId = BBBCoreConstants.SITE_BAB_US
		/*siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock*/
		profileMock.getRepositoryId() >> profileId
		
		pValidateCaptcha = true
		emailForSender = true
		ccFlag = true
		productIdParamName = "productId"
		pCaptchaAnswer = "abcd01"
		emailType = EmailAFriendFormHandler.EMAIL_A_FRIEND_PDP
		currentPageURL = "https://bedbath.com/product?productId=product01"
		currentSite = siteId
		templateUrl = "bedbath.com/templates/emailAFriend"
		currentCatalogURL = "bedbath.com/catalog?"
		tempEmail = "tester@yopmail.com"
		errorUrlMap.putAll(["home":"bedbath.com/product"])
		pFromPage = "home"
		pSuccessUrlMap.putAll(["home":"bedbath.com/cart"])
		pQueryParam = "?skuId="
		localeObject = new Locale("en")
		profileId = "profile01"
		productId = "product01"
		catalogToolsMock = Mock()
		productLongDescription = "simplehuman Polished Stainless Steel Fingerprint-Proof 4-1/2-Liter"
		productName = "Water container"
		senderEmail = "tester2@yopmail.com"
		recipientEmail = "receiver@yopmail.com"
		contextPath = "/store"
		
		requestMock.getLocale() >> localeObject
		
		
		emailAFriendFormHandler = new EmailAFriendFormHandler([
								  	validateCaptcha : pValidateCaptcha, mEmailForSender : emailForSender,
									lblTxtTemplateManager : lblTxtTemplateManagerMock, 
									mCcFlag : ccFlag,
									mProductIdParamName : productIdParamName,
									captchaAnswer : pCaptchaAnswer,
									mEmailType : emailType,
									mCurrentPageURL : currentPageURL,
									mCurrentSite : currentSite,
									mTemplateUrl : templateUrl,
									mCurrentCatalogURL : currentCatalogURL,
									mTempEmail : tempEmail,
									errorUrlMap : errorUrlMap,
									fromPage : pFromPage,
									successUrlMap : pSuccessUrlMap,
									queryParam : pQueryParam,
									productManager : productManagerMock,
									siteId : siteId
								  ])
		
		emailSenderFormHandlerMock.getProfile() >> profileMock
		
	}
	
	
	/*====================================================
	 * 
	 * collectParams - Test cases - STARTS
	 * 
	 * Method signature : 
	 * 
	 * protected final Map collectParams(
	 * final DynamoHttpServletRequest pRequest
	 * ) 
	 * 
	 * ====================================================
	 */

	
	def "collectParams  - construction email parameters successfully" () {
		
		given : 
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		ProductVO productVO = new ProductVO()
		def productLongDescription = "<![CDATA[<UL><LI>simplehuman Polished Stainless Steel Fingerprint-Proof 4-1/2-Liter Round Step Wastebasket has a strong steel pedal engineered to last over 150,000 steps</LI><LI>Features an ABS hinge and removable resin liner</LI><LI>Non-skid base features rubber pads that keep the can stable and are easy on floors</LI><LI>4-1/2-liter capacity</LI><LI>Measures 7-1/4\" diameter x 12.25\" H</LI><LI>Easy to clean</LI><LI>Uses simplehuman code A custom fit liners</LI><LI>Imported</LI></UL>]]>"
		def productName = "Water container"
		ImageVO imageVO = new ImageVO()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		
		imageVO.setLargeImage("largeImage01")
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setSenderEmail(senderEmail)
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		productVO.setLongDescription(productLongDescription)
		productVO.setName(productName)
		productVO.setProductImages(imageVO)
		
		requestDomainName.addAll(["bedbath-26.sapient.com","bedbath-32.sapient.com"])
		
		1 * lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> requestDomainName
		1 * productManagerMock.getMainProductDetails(siteId,productId) >> productVO
		
		when : 
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then : 
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.PAGE_URL).equals(currentCatalogURL + "&" )
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.PAGE_URL_TEXT).equals(BBBGiftRegistryConstants.PRODUCT_DETAIL_PAGE)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDERS_EMAIL).equals(senderEmail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.EMAIL_TYPE).equals(emailType)
		tempPlaceHolderValues.get(BBBCmsConstants.FRM_DATA_PAGE_TITLE).equals(currentCatalogURL + "&" )
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER).equals(senderMessageHeader)
		//tempPlaceHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId) // can't verify as it'll(date) vary in the code & here
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_TITLE).equals(productVO.getName())
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_IMAGE).equals(productVO.getProductImages().getLargeImage())
		//tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO).equals(productLongDescription.substring(4, productLongDescription.length()).concat("..."))
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_REQUEST_DOMAIN).equals(requestDomainName.get(0))
		
	}
	
	/*
	 * Alternative branches covered : 
	 * 
	 * #345 & 370 - if(pageUrl.contains("?")) - pageUrl - contains '?' character
	 * #358 & 386 - if (null == this.siteId) - siteID exits (can't be covered as they've initialized null) 
	 * #394 - if (isEmailForSender()) - disabled
	 * #406 - if (getEmailType().equalsIgnoreCase(EmailAFriendFormHandler.EMAIL_A_FRIEND_PDP)) - emailType is not EMAIL a friend PDP
	 * #461 - !(getSiteContext().getSite().getId().isEmpty()) - SiteID is empty
	 * #459 & #460 - getSiteContext() != null && getSiteContext().getSite() != null can't be covered due to Null ptr exception
	 */
	
	def "collectParams  - Emailing a non-PDP page" () {
		
		given :
		
		//def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		ProductVO productVO = new ProductVO()
		def productLongDescription = "<![CDATA[simplehuman Polished Stainless Steel Fingerprint-Proof 4-1/2-Liter Round Step Wastebasket has a strong steel pedal engineered to last over 150,000 stepsFeatures an ABS hinge and removable resin linerNon-skid base features rubber pads that keep the can stable and are easy on floors4-1/2-liter capacityMeasures 7-1/4\" diameter x 12.25\" HEasy to cleanUses simplehuman code A custom fit linersImported]]>"
		def productName = "Water container"
		ImageVO imageVO = new ImageVO()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		def tempCurrentCatalogUrl = "bedbath.com/product" 

		imageVO.setLargeImage("largeImage01")
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		siteMock.getId() >> ""
		siteContextMock.getSite() >> siteMock
		
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setEmailType("Intimation Mail")
		emailAFriendFormHandler.setSenderEmail(senderEmail)
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		emailAFriendFormHandler.setCurrentPageURL("bedbath.com/product")
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		//emailAFriendFormHandler.setSiteId(null)
		emailAFriendFormHandler.setCurrentCatalogURL(tempCurrentCatalogUrl)
		emailAFriendFormHandler.setEmailForSender(false)
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		0 * lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _)
		0 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE)
		0 * productManagerMock.getMainProductDetails(siteId,productId)
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		!tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL).equals(tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER).equals(BBBCoreConstants.BLANK)
		!tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_TITLE).equals(productName)
		!tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_IMAGE).equals(imageVO)
		!tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO).equals(productLongDescription.substring(4, productLongDescription.length()))
		
	}

	/*
	 * Alternative branches covered :
	 *
	 *
	 */
	

	/*==========================================================
	 *
	 * #429 - if (length >= 352) - contains no <UL> & <LI> tag
	 * #367 - if (!BBBUtility.isEmpty(getCurrentCatalogURL())) 
	 * ==========================================================
	 */
	//TODO #461 - !(getSiteContext().getSite().getId().isEmpty()) - SiteID is empty - not possible - null ptr exception
	
	def "collectParams  - product description contains no <UL> & <LI> tag" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		ProductVO productVO = new ProductVO()
		def productLongDescription = "<![CDATA[simplehuman Polished Stainless Steel Fingerprint-Proof 4-1/2-Liter Round Step Wastebasket has a strong steel pedal engineered to last over 150,000 stepsFeatures an ABS hinge and removable resin linerNon-skid base features rubber pads that keep the can stable and are easy on floors4-1/2-liter capacityMeasures 7-1/4\" diameter x 12.25\" HEasy to cleanUses simplehuman code A custom fit linersImported]]>"
		def productName = "Water container"
		ImageVO imageVO = new ImageVO()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		imageVO.setLargeImage("largeImage01")
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setCurrentCatalogURL("")
		emailAFriendFormHandler.setSenderEmail(senderEmail)
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		productVO.setLongDescription(productLongDescription)
		productVO.setName(productName)
		productVO.setProductImages(imageVO)
		
		requestDomainName.addAll(["bedbath-26.sapient.com","bedbath-32.sapient.com"])
		
		1 * lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> requestDomainName
		1 * productManagerMock.getMainProductDetails(siteId,productId) >> productVO
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
		//tempPlaceHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId) // can't verify as it'll(date) vary in the code & here
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_TITLE, productVO.getName())
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_IMAGE, productVO.getProductImages().getLargeImage())
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO, productLongDescription.substring(4, productLongDescription.length()))
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_REQUEST_DOMAIN).equals(requestDomainName.get(0))
		
	}
	
	/*==========================================================
	 * 
	 * Alternate branches covered : 
	 * 
	 * #427 - if (length >= 352) - contains no <UL> & <LI> tag
	 * 
	 * ==========================================================
	 */
	
	
	def "collectParams  - product description contains no <LI>  tag" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		ProductVO productVO = new ProductVO()
		def productLongDescription = "<![CDATA[<UL>simplehuman Polished Stainless Steel Fingerprint-Proof 4-1/2-Liter Round Step Wastebasket has a strong steel pedal engineered to last over 150,000 stepsFeatures an ABS hinge and removable resin linerNon-skid base features rubber pads that keep the can stable and are easy on floors4-1/2-liter capacityMeasures 7-1/4\" diameter x 12.25\"<UL> HEasy to cleanUses simplehuman code A custom fit linersImported]]>"
		def productName = "Water container"
		ImageVO imageVO = new ImageVO()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		imageVO.setLargeImage("largeImage01")
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setCurrentCatalogURL("")
		emailAFriendFormHandler.setSenderEmail(senderEmail)
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		productVO.setLongDescription(productLongDescription)
		productVO.setName(productName)
		productVO.setProductImages(imageVO)
		
		requestDomainName.addAll(["bedbath-26.sapient.com","bedbath-32.sapient.com"])
		
		1 * lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> requestDomainName
		1 * productManagerMock.getMainProductDetails(siteId,productId) >> productVO
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
		//tempPlaceHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId) // can't verify as it'll(date) vary in the code & here
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_TITLE, productVO.getName())
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_IMAGE).equals(productVO.getProductImages().getLargeImage())
		//tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO, productLongDescription.substring(4, productLongDescription.length()))
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_REQUEST_DOMAIN).equals(requestDomainName.get(0))
		
	}
	
	/*=================================================================
	 *
	 * Alternate branches covered :
	 *
	 * #423 - if (length >= 352) - product long description is short
	 *
	 * =================================================================
	 */
	
	
	def "collectParams  - " () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		ProductVO productVO = new ProductVO()
		def productLongDescription = "<![CDATA[<UL>simplehuman Polished Stainless Steel Fingerprint-Proof 4-1/2-Liter Round Step Wastebasket has a strong steel pedal engineered to last over 150,000 stepsFeatures an ABS hinge and removable resin linerNon-skid base features rubber pads that keep the can stable and are easy on floors"
		def productName = "Water container"
		ImageVO imageVO = new ImageVO()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		imageVO.setLargeImage("largeImage01")
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setCurrentCatalogURL("")
		emailAFriendFormHandler.setSenderEmail(senderEmail)
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		productVO.setLongDescription(productLongDescription)
		productVO.setName(productName)
		productVO.setProductImages(imageVO)
		
		requestDomainName.addAll(["bedbath-26.sapient.com","bedbath-32.sapient.com"])
		
		1 * lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> requestDomainName
		1 * productManagerMock.getMainProductDetails(siteId,productId) >> productVO
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
		//tempPlaceHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId) // can't verify as it'll(date) vary in the code & here
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_TITLE, productVO.getName())
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_IMAGE).equals(productVO.getProductImages().getLargeImage())
		//tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO, productLongDescription.substring(4, productLongDescription.length()))
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_PRODUCT_INFO)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_REQUEST_DOMAIN).equals(requestDomainName.get(0))
		
	}
	
	
	
	def "collectParams  - Product is unavailable in the repository | BBBBusinessException | Error code available " () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		BBBBusinessException businessExceptionObj =  new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY)
		businessExceptionObj.setErrorCode(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY)
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setSenderEmail(senderEmail)
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw businessExceptionObj}
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
	}

	def "collectParams  - Product is disabled | BBBBusinessException" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		BBBBusinessException businessExceptionObj =  new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		businessExceptionObj.setErrorCode(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw businessExceptionObj}
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
	}

	def "collectParams  - Exception while getting data from catalog | BBBBusinessException | Error code not available" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		BBBBusinessException businessExceptionObj =  new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		//businessExceptionObj.setErrorCode(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw businessExceptionObj}
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
	}
		
	def "collectParams  - Product is disabled | BBBSystemException" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		BBBSystemException systemExceptionObj =  new BBBSystemException(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		systemExceptionObj.setErrorCode(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw systemExceptionObj}
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
	}
	
	def "collectParams  - Exception while getting product data from catalog | BBBSystemException | Error code unavailable" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		BBBSystemException systemExceptionObj =  new BBBSystemException(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY)
		//systemExceptionObj.setErrorCode(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY)
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw systemExceptionObj}
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
	}

	def "collectParams  - Product is unavailable in catalog | BBBSystemException" () {
		
		given :
		
		
		def senderMessageHeader = "Registration request success"
		List<String> requestDomainName = new ArrayList<>()
		Map tempEmailParams = new HashMap()
		HashMap tempPlaceHolderValues = new HashMap()
		def message = "Registration confirmation"
		def tempMail = "tempuser@yopmail.com"
		BBBSystemException systemExceptionObj =  new BBBSystemException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY)
		systemExceptionObj.setErrorCode(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY)
		
		siteId = BBBCoreConstants.SITE_BAB_US
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		emailSenderFormHandlerMock.collectParams(requestMock) >> {}
		
		emailAFriendFormHandler.setTempEmail(tempMail)
		emailAFriendFormHandler.setMessage(message)
		emailAFriendFormHandler.setSiteId(siteId)
		emailAFriendFormHandler.setSiteContext(siteContextMock)
		emailAFriendFormHandler.setProfile(profileMock)
		emailAFriendFormHandler.setCatalogTools(catalogToolsMock)
		
		requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> productId
		
		lblTxtTemplateManagerMock.getPageTextArea("txt_sender_email_message_header", "en", _, _) >> senderMessageHeader
		catalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw systemExceptionObj}
		
		when :
		
		tempEmailParams = emailAFriendFormHandler.collectParams(requestMock)
		
		then :
		
		tempPlaceHolderValues.putAll(tempEmailParams.get(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES))
		
		tempEmailParams.get(BBBCoreConstants.SITE_ID).equals(siteId)
		
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SITEID).equals(siteId)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE).equals(message)
		tempPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_EMAIL, tempMail)
		tempPlaceHolderValues.get(BBBGiftRegistryConstants.FRMDATA_SENDER_EMAIL_MESSAGE_HEADER, senderMessageHeader)
		tempPlaceHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, currentPageURL)
	}
	
	
	
	
	/*==========================================================
	 *
	 * collectParams - Test cases - ENDS
	 *
	 * ==========================================================
	 */

	
	
	/*====================================================
	 *
	 * handleEmailAFriendRequest - Test cases - STARTS
	 *
	 * Method signature :
	 *
	 * protected final Map collectParams(
	 * final DynamoHttpServletRequest pRequest
	 * )
	 *
	 * ====================================================
	 */


	def "handleEmailAFriendRequest - Requesting to send email to a friend - successfully" () {
		
		given : 
		
		def requestStatus

		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBEmailSenderFormHandler emailSenderSpy = Spy()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		1 * emailAFriendFormHandlerSpy.invokeSendEmail(requestMock) >> {}
		1 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true
		emailAFriendFormHandlerSpy.setLoggingError(false)
		
		
		expect :
		
		//requestStatus = emailAFriendFormHandlerSpy.handleEmailAFriendRequest(requestMock, responseMock)
		  emailAFriendFormHandlerSpy.handleEmailAFriendRequest(requestMock, responseMock)
		  
		/*then : 
		requestStatus*/
	}	

	/*==================================================================================
	 *
	 * Alternative branches covered :
	 * 
	 * #540 - if (isCcFlag()) - false
	 * #532 - catch (TemplateEmailException e) 
	 * ==================================================================================
	 */
	
	def "handleEmailAFriendRequest - Requesting to send email to a friend without CC" () {
		
		given :
		
		def requestStatus

		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBEmailSenderFormHandler emailSenderSpy = Spy()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		1 * emailAFriendFormHandlerSpy.invokeSendEmail(requestMock) >> {throw new TemplateEmailException("")}
		0 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true
		emailAFriendFormHandlerSpy.setLoggingError(false)
		emailAFriendFormHandlerSpy.setCcFlag(false)
		
		expect :
		
		  emailAFriendFormHandlerSpy.handleEmailAFriendRequest(requestMock, responseMock)
		  
	}
	
	/*==================================================================================
	 *
	 * Alternative branches covered : 
	 * #484 - if (this.getCurrentPageURL() == null ||
	 *        "".equals(this.getCurrentPageURL()))  - currentPageURL is null
	 * #500 - if (BBBUtility.isEmpty(getRecipientEmail())) - recipientEmail is empty
	 * #519 - if (!BBBUtility.isValidEmail(getSenderEmail())) - senderEmail - is invalid       
	 * 
	 * ==================================================================================
	 */
	
	//TODO - 

	def "handleEmailAFriendRequest - recipient email is invalid(empty)" () {
		
		given :
		
		def requestStatus

		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBEmailSenderFormHandler emailSenderSpy = Spy()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.setCurrentPageURL(null)
		emailAFriendFormHandlerSpy.setRecipientEmail("")
		emailAFriendFormHandlerSpy.setSenderEmail("senderyopmmail.com")
		0 * emailAFriendFormHandlerSpy.invokeSendEmail(requestMock) >> {}
		1 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> false
		emailAFriendFormHandlerSpy.setLoggingError(true)
		
		expect :
		 
		emailAFriendFormHandlerSpy.handleEmailAFriendRequest(requestMock, responseMock)
		
	}
	
	/*===========================================================================================
	 *
	 * Alternative branches covered :
	 * 
	 * #484 - if (this.getCurrentPageURL() == null ||
	 * 		   "".equals(this.getCurrentPageURL())) - currentPageURL is empty
	 * #508 -if (!BBBUtility.isValidEmail(email))  - email is invalid
	 * #546 - if (!isSenderEmailSuccess && isLoggingError()) - loggingError - disabled (false)
	 * 
	 * ==========================================================================================
	 */
	
	def "handleEmailAFriendRequest - Invalid email IDs" () {
		
		given :
		
		def requestStatus
		def invaildEmail = "senderyopmmail.com"
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBEmailSenderFormHandler emailSenderSpy = Spy()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.setCurrentPageURL("")
		emailAFriendFormHandlerSpy.setRecipientEmail(recipientEmail + BBBCoreConstants.SEMICOLON + invaildEmail)
		emailAFriendFormHandlerSpy.setSenderEmail("senderyopmmail.com")
		0 * emailAFriendFormHandlerSpy.invokeSendEmail(requestMock) >> {}
		1 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> false
		emailAFriendFormHandlerSpy.setLoggingError(false)
		
		expect :
		 
		emailAFriendFormHandlerSpy.handleEmailAFriendRequest(requestMock, responseMock)
		
	}
	
	/*====================================================
	 *
	 * handleEmailAFriendRequest - Test cases - STARTS
	 *
	 * ====================================================
	 */
	
	
	/*====================================================
	 *
	 * validatingCaptcha - Test cases - STARTS
	 *
	 * Method signature :
	 *
	 * public final boolean validatingCaptcha(
	 * 	final DynamoHttpServletRequest pRequest,
	 * 	final DynamoHttpServletResponse pResponse)
	 * 
	 * ====================================================
	 */
	
	def "validatingCaptcha - validated captcha successfully" () {
		
		given :
		
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		Captcha captcha = buildCaptcha()
		
		emailAFriendFormHandler.setCaptchaAnswer(captcha.getAnswer())
		sessionBeanMock.setCaptcha(captcha)
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		
		expect : 
		emailAFriendFormHandler.validatingCaptcha(requestMock, responseMock)		
	}
	
	/*
	 *
	 * #697 - if (isValidateCaptcha() && !captcha.isCorrect(getCaptchaAnswer()))  - isCorrect - false
	 *  isValidateCaptcha() - false branch not possible as it's already checked.
	 */
	
	def "validatingCaptcha - Mismatching captcha answer" () {
		
		given :
		
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		Captcha captcha = buildCaptcha()
		
		//emailAFriendFormHandler.setCaptchaAnswer(captcha.getAnswer())
		sessionBeanMock.setCaptcha(captcha)
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		
		expect :
		! emailAFriendFormHandler.validatingCaptcha(requestMock, responseMock)
	}
	
	/*
	 * #695 -if (this.validateCaptcha) - false
	 * 
	 */
	
	def "validatingCaptcha - captcha validation disabled" () {
		
		given :
		
		emailAFriendFormHandler.setValidateCaptcha(false)
		
		expect :
		
		emailAFriendFormHandler.validatingCaptcha(requestMock, responseMock)
	}
	
	/*====================================================
	 *
	 * validatingCaptcha - Test cases - ENDS
	 *
	 * ====================================================
	 */
	
	/*====================================================
	 *
	 * handleSend - Test cases - STARTS
	 *
	 * Method signature :
	 *
	 * public final boolean handleSend(
	 *  final DynamoHttpServletRequest pRequest,
	 * 	final DynamoHttpServletResponse pResponse)
	 *
	 * ====================================================
	 */
	
	def "handleSend - email sent successfully" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  true 
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> siteId
		emailAFriendFormHandlerSpy.getQueryParam() >> pQueryParam
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath 
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true >> true
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches : 
	 * 
	 * #651 - if (!isSenderEmailSuccess && isLoggingError()) - senderEmailSuccess & loggingDebug - true
	 * #579 - if(StringUtils.isNotEmpty(getQueryParam()))
	 * #599 - if ((null != siteId && siteId.contains(BBBCoreConstants.TBS)) || validatingCaptcha(pRequest, pResponse)) - siteID - TBS 
	 */
	
	def "handleSend - Invaid captcha | LoggingError enabled (true)" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getQueryParam() >> ""
		emailAFriendFormHandlerSpy.setLoggingError(true)
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true >> false
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 * 
	 * #621 - if (!isSenderEmailSuccess && isLoggingError())  - T & F branch
	 *
	 */
	
	def "handleSend - Email failed - loggingError disabled (false)" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getQueryParam() >> ""
		emailAFriendFormHandlerSpy.setLoggingError(false)
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true >> false
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 *
	 * #577 - if ( StringUtils.isNotEmpty(getFromPage())) - false
	 *
	 */
	
	def "handleSend - Source page URL is invalid (empty)" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getFromPage() >> ""
		emailAFriendFormHandlerSpy.setLoggingError(true)
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true >> false
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 * 
	 * #620 -if (isCcFlag()) - false
	 *
	 */
	
	def "handleSend - Email sent without CC - CC is disabled" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getQueryParam() >> ""
		emailAFriendFormHandlerSpy.setLoggingError(true)
		emailAFriendFormHandlerSpy.setCcFlag(false)
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		1 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true >> false
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 *
	 * #607 - if (isRecipientEmailSuccess) - false
	 * 
	 * #644 - if (!isSenderEmailSuccess && isLoggingError()) - senderEmailSuccess & loggingDebug - false
	 * #579 - if(StringUtils.isNotEmpty(getQueryParam()))
	 *
	 */
	
	def "handleSend - problem in recipient side | loggingError disabled (false)" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getQueryParam() >> ""
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock

		emailAFriendFormHandlerSpy.isLoggingError() >> false
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> false
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 *
	 *
	 * #644 - if (!isSenderEmailSuccess && isLoggingError()) - senderEmailSuccess - true & loggingDebug - true
	 *
	 */
	
	def "handleSend - problem in recipient side | loggingError enabled (true)" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getQueryParam() >> ""
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock

		emailAFriendFormHandlerSpy.isLoggingError() >> true
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 *
	 *
	 * #644 - if (!isSenderEmailSuccess && isLoggingError()) - senderEmailSuccess & loggingDebug - true
	 *
	 */
	
	def "handleSend - | loggingError enabled (true)" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> TBSConstants.SITE_TBS_BAB_US
		emailAFriendFormHandlerSpy.getQueryParam() >> ""
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.getContextPath() >> contextPath
		requestMock.getHeader(BBBCoreConstants.REFERRER) >> currentPageURL
		requestMock.getScheme() >> "post"
		requestMock.getServerName() >> "box22"
		requestMock.getLocale() >> localeObject
		responseMock.getWriter() >> printWriterMock

		emailAFriendFormHandlerSpy.isLoggingError() >> true
		
		(1.._) * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) >> "Email sent"
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> false
		
		emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 * #599 -if ((null != siteId && siteId.contains(BBBCoreConstants.TBS)) || validatingCaptcha(pRequest, pResponse)) - Invalid captcha
	 * 
	 */
	
	def "handleSend - request from desktop site and invalid captcha" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> siteId
		
		responseMock.getWriter() >> printWriterMock
		
		0 * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _)
		0 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock)
		
		expect :
		 
		!emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 * #599 -if ((null != siteId && siteId.contains(BBBCoreConstants.TBS)) || validatingCaptcha(pRequest, pResponse)) - siteID is null
	 *
	 */
	def "handleSend - siteID is invalid - null" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  false
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> null
		
		responseMock.getWriter() >> printWriterMock
		
		0 * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _)
		0 * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock)
		
		expect :
		
		! emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	/*
	 * Alternative branches :
	 *
	 * #659 - catch (JSONException e)  - 
	 *
	 */
	
	def "handleSend - Exception while populating info in JSON Object | JSONException" () {
		
		given :
		 
		EmailAFriendFormHandler emailAFriendFormHandlerSpy = Spy()
		BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
		PrintWriter printWriterMock = Mock()
		
		populateEmailAFriendFormHandlerSpy(emailAFriendFormHandlerSpy)
		
		emailAFriendFormHandlerSpy.validatingCaptcha(requestMock, responseMock) >>  true
		emailAFriendFormHandlerSpy.getSiteIdFromContextManager() >> siteId
		emailAFriendFormHandlerSpy.populateSuccessInfoJSON(requestMock, _, _) >> {throw new JSONException("")}
		responseMock.getWriter() >> printWriterMock
		
		0 * lblTxtTemplateManagerMock.getPageTextArea("txt_email_sent_msg", localeObject.getLanguage(),_, _) 
		(1.._) * emailAFriendFormHandlerSpy.invokeHandleSend(requestMock, responseMock) >> true
		
		expect : 
		
		! emailAFriendFormHandlerSpy.handleSend(requestMock, responseMock)
		
	}
	
	
	/*====================================================
	 *
	 * handleSend - Test cases - ENDS
	 * 
	 * ====================================================
	 */
	
		
	/*
	 * Data populating methods
	 */
	
	private populateEmailAFriendFormHandlerSpy(EmailAFriendFormHandler emailAFriendFormHandlerSpy) {
		
		emailAFriendFormHandlerSpy.setValidateCaptcha(pValidateCaptcha)
		emailAFriendFormHandlerSpy.setEmailForSender(emailForSender)
		emailAFriendFormHandlerSpy.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		emailAFriendFormHandlerSpy.setCcFlag(ccFlag)
		emailAFriendFormHandlerSpy.setProductIdParamName(productIdParamName)
		emailAFriendFormHandlerSpy.setCaptchaAnswer(pCaptchaAnswer)
		emailAFriendFormHandlerSpy.setEmailType(emailType)
		emailAFriendFormHandlerSpy.setCurrentPageURL(currentPageURL)
		emailAFriendFormHandlerSpy.setCurrentSite(currentSite)
		emailAFriendFormHandlerSpy.setTemplateUrl(templateUrl)
		emailAFriendFormHandlerSpy.setCurrentCatalogURL(currentCatalogURL)
		emailAFriendFormHandlerSpy.setTempEmail(tempEmail)
		emailAFriendFormHandlerSpy.setErrorUrlMap(errorUrlMap)
		emailAFriendFormHandlerSpy.setFromPage(pFromPage)
		emailAFriendFormHandlerSpy.setSuccessUrlMap(pSuccessUrlMap)
		emailAFriendFormHandlerSpy.setQueryParam(pQueryParam)
		emailAFriendFormHandlerSpy.setProductManager(productManagerMock)
		emailAFriendFormHandlerSpy.setSiteId(siteId)
		
		//emailSenderFormHandlerMock.getRecipientEmail()  >> recipientEmail
		emailAFriendFormHandlerSpy.setRecipientEmail(recipientEmail)// >> recipientEmail
		emailAFriendFormHandlerSpy.setSenderEmail(senderEmail)// >> senderEmail
		emailAFriendFormHandlerSpy.setCurrentPageURL(currentPageURL)
		
	}
	
	private Captcha buildCaptcha() {
		Captcha captcha = new Captcha.Builder(100, 200).addText().addBackground(new GradiatedBackgroundProducer()).gimp()
				.addNoise()
				.addBorder()
				.build()
				
		return captcha
	}

		
}
