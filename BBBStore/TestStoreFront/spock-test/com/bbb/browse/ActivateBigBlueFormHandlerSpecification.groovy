package com.bbb.browse

import atg.multisite.Site
import atg.multisite.SiteContext
import javax.servlet.http.HttpSession
import nl.captcha.Captcha;
import nl.captcha.CaptchaBean
import nl.captcha.backgrounds.GradiatedBackgroundProducer;

import com.bbb.account.validatecoupon.ActivateCouponRequestVO
import com.bbb.account.validatecoupon.ActivateCouponResponseVO
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.browse.manager.ActivateCouponManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.tibco.vo.ActivateBigBlueVO
import com.bbb.utils.BBBUtility;
import com.sun.corba.ee.org.omg.CSI.MTEstablishContext;

import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is created to unit test the ActivateBigBlueFormHandler
 *
 */

public class ActivateBigBlueFormHandlerSpecification extends BBBExtendedSpec{

	private ActivateBigBlueFormHandler activateBigBlueFormHandler
	
	private SiteContext siteContextMock
	def tempCaptchaAnswer
	def tempValidateCaptcha
	def currentPageURL
	def currentSite
	def templateUrl
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	
	private Map<String, String> mErrorMap
	private LblTxtTemplateManager lblTxtTemplateManagerMock
	private ActivateCouponRequestVO activateCouponRequestVO
	private BBBValidationRules validationRulesObj
	private ActivateCouponManager activateCouponManagerMock
	private ActivateCouponResponseVO activateCouponResponseVO
	private BBBCatalogTools catalogToolsMock
	private Site siteMock
	
	def tempEmailAddress
	def tempExpiryDate
	def tempSuccessURL
	def siteId 
	def contextAdded
	
	
	def setup() {
		
		siteContextMock = Mock()
		lblTxtTemplateManagerMock = Mock()
		activateCouponRequestVO = new ActivateCouponRequestVO()
		validationRulesObj = new BBBValidationRules()
		activateCouponManagerMock = Mock()
		activateCouponResponseVO = new ActivateCouponResponseVO()
		catalogToolsMock = Mock()
		siteMock = Mock()
		
		tempCaptchaAnswer = "abcd01"
		tempValidateCaptcha = true
		currentPageURL = "/pdp/product01"
		currentSite = "BBB_US"
		templateUrl = "template.bedbath.com/registration/success.html"
		tempEmailAddress = "tester@yopmail.com"
		tempExpiryDate = "01.01.2016"
		tempSuccessURL = "bedbath.com/activateBigBlue/success"
		
		contextAdded = true
		//validationRulesObj.setAlphaNumericPattern(alphaNumericPattern)
		validationRulesObj.setAlphaNumericPattern("^[a-zA-Z0-9]*")
		validationRulesObj.setCrossSiteScriptingPattern(crossSiteScriptingPattern)
		
		activateCouponRequestVO.setEmailAddr(tempEmailAddress)
		
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		
		activateBigBlueFormHandler = new ActivateBigBlueFormHandler([
										mSiteContext : siteContextMock, captchaAnswer : tempCaptchaAnswer, validateCaptcha : tempValidateCaptcha,
										mCurrentPageURL : currentPageURL, mCurrentSite : currentSite, mTemplateUrl : templateUrl,
										lblTxtTemplateManager : lblTxtTemplateManagerMock, activateBigBlueVO : activateCouponRequestVO,
										rules : validationRulesObj, activateCouponManager : activateCouponManagerMock,
										activateCouponRespVO : activateCouponResponseVO, mBbbCatalogTools : catalogToolsMock, 
										emailAddress : tempEmailAddress, expiryDate : tempExpiryDate, successURL : tempSuccessURL,
										mContextAdded : contextAdded
										])
		
	}

	
	
	/*
	 * handleActivateBigBlueRequest - test cases - starts
	 * 
	 * Alternative branches covered : 
	 * 
	 * #281 - if (!BBBUtility.isEmpty(sOfferCd)) - Offer code is empty //(unreachable code) Can't be covered as empty check previously done
	 * #287 - if (!BBBUtility.isStringLengthValid(sOfferCd, 1, 6)) - invalid offer code length
	 */
	
	def "handleActivateBigBlueRequest - activated big blue request successfully" () {
		
		given :
		
		populateTestClassSpy()
		
		def contextPath = "/store/pdp/product01"
		HttpSession sessionMock = Mock()
		Captcha captcha = buildCaptcha()
	 
		siteId = "BBB_US"
		
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		activateBigBlueFormHandler.setContextAdded(false)
		
		activateCouponRequestVO.setOfferCd("OFFER50")
		
		sessionMock.getId() >> "session01"
		requestMock.getSession() >> sessionMock
		requestMock.getContextPath() >> contextPath
		sessionBeanMock.setCaptcha(captcha)
		
		expect :
		 
		activateBigBlueFormHandler.handleActivateBigBlueRequest(requestMock, responseMock)
			
	}

	/*
	 * Alternative branches covered : 
	 * 
	 * #430 - getActivateBigBlueVO().setmSiteFlag(BBBCoreConstants.SITE_BAB_CA_VALUE)
	 * #439 - if(errorMessage !=null && !errorMessage.isEmpty()) - errorMessage exists 
	 * #230 - BBBUtility.isEmpty(getActivateBigBlueVO().getEmailAddr()) - emailAddr - is empty
	 * #214 - if (isValidateCaptcha() && !captcha.isCorrect(getCaptchaAnswer())) - validate captcha - false
	 * #258 - if(!BBBUtility.isEmpty(getActivateBigBlueVO().getOfferCd()) &&
	 * 			 !isValidOfferCode(getActivateBigBlueVO().getOfferCd()))  - valid offerCode
	 * #264 - if (!BBBUtility.isEmpty(getActivateBigBlueVO().getMobilePhone()) &&
	 * 		  !BBBUtility.isValidPhoneNumber(getActivateBigBlueVO().getMobilePhone())) - Valid mobile phone
	 * #333 - if (pContextPath != null && !isContextAdded()) - ContextPath is null
	 */
	
	def "handleActivateBigBlueRequest - BedBathUS site -  Form error exists" () {
		
		given :
		
		populateTestClassSpy()
		
		HttpSession sessionMock = Mock()
		Captcha captcha = buildCaptcha()
		ErrorStatus errorStatusMock = new  ErrorStatus()
		
		//siteId = "BBB_US"
		siteId = BBBCoreConstants.SITE_BAB_US
		
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		errorStatusMock.setErrorExists(true)
		errorStatusMock.setDisplayMessage("Error while creating coupon")
		
		activateBigBlueFormHandler.getFormError() >> false
		//activateBigBlueFormHandler.setContextAdded(false)
		activateBigBlueFormHandler.setValidateCaptcha(false)
		
		activateCouponRequestVO.setEmailAddr("")
		activateCouponRequestVO.setOfferCd("OFF50")
		activateCouponRequestVO.setMobilePhone("1234567890")
		
		sessionMock.getId() >> "session01"
		requestMock.getSession() >> sessionMock
		requestMock.getContextPath() >> null
		sessionBeanMock.setCaptcha(captcha)
		activateCouponResponseVO.setStatus(errorStatusMock) 
		(1.._) * activateCouponManagerMock.activateBigBlue(activateCouponRequestVO) >> activateCouponResponseVO
		//1 * sessionBeanMock.setCouponError(_)
		
		when :
		 
		activateBigBlueFormHandler.handleActivateBigBlueRequest(requestMock, responseMock) == false
		//activateCouponRequestVO.getmSiteFlag().equals(BBBCoreConstants.SITE_BAB_CA_VALUE)
		then:
		activateCouponRequestVO.getmSiteFlag().equals(BBBCoreConstants.SITE_BAB_US_VALUE)
		activateBigBlueFormHandler.isSuccessMessage() == false
		sessionBeanMock.getCouponError().equals( "Error while creating coupon")
		activateBigBlueFormHandler.getEmailAddress().equals("")
		1 * requestMock.setCharacterEncoding("UTF-8")
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #424 - if((BBBCoreConstants.SITE_BBB).equalsIgnoreCase(siteId)) - Site is BuyBuyBaby 
	 * #439 - if(errorMessage !=null && !errorMessage.isEmpty()) - errorMessage is null
	 * #234 - if (!BBBUtility.isEmpty(getActivateBigBlueVO().getEmailAddr())
	 *         && !BBBUtility.isValidEmail(getActivateBigBlueVO().getEmailAddr())) - Email address is invalid
	 * #258 - if(!BBBUtility.isEmpty(getActivateBigBlueVO().getOfferCd()) &&
	 * 			 !isValidOfferCode(getActivateBigBlueVO().getOfferCd()))  - invalid offerCode
	 * #264 - if (!BBBUtility.isEmpty(getActivateBigBlueVO().getMobilePhone()) &&
	 * 		  !BBBUtility.isValidPhoneNumber(getActivateBigBlueVO().getMobilePhone())) - inValid mobile phone
	 * 
	 * #333 - if (pContextPath != null && !isContextAdded()) - contextAdded flag is turned off        
	 * 
	 */
	//TODO - 30.11.2016 - #214 - if (isValidateCaptcha() && !captcha.isCorrect(getCaptchaAnswer())) - incorrect captcha
	def "handleActivateBigBlueRequest - BuyBuyBaby site Form error exists | errorMessage(coupon error) is invaild(null)" () {
		
		given :
		
		populateTestClassSpy()
		
		def contextPath = "/store/pdp/product01"
		def handelMethodStatus
		//def couponError = ""
		HttpSession sessionMock = Mock()
		Captcha captcha = buildCaptcha()
		ErrorStatus errorStatusMock = Mock()
		
		siteId = BBBCoreConstants.SITE_BBB
		activateCouponRequestVO.setEmailAddr("testeryopmail.com")
		sessionBeanMock.setCaptcha(captcha)
		activateBigBlueFormHandler.setCaptchaAnswer(captcha.getAnswer())
		
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock

		errorStatusMock.isErrorExists() >> true
		errorStatusMock.getDisplayMessage() >> null
		
		activateBigBlueFormHandler.setSiteContext(siteContextMock)
		activateBigBlueFormHandler.getFormError() >> false
		activateBigBlueFormHandler.setContextAdded(false)

		activateCouponRequestVO.setOfferCd("@@@")
		activateCouponRequestVO.setMobilePhone("123456789")
		
		sessionMock.getId() >> "session01"
		requestMock.getSession() >> sessionMock
		requestMock.getContextPath() >> contextPath
		sessionBeanMock.setCaptcha(captcha)
		activateCouponResponseVO.setStatus(errorStatusMock)
		(1.._) * activateCouponManagerMock.activateBigBlue(activateCouponRequestVO) >> activateCouponResponseVO
		//1 * sessionBeanMock.setCouponError(_) // not working
		
		when :
		
		handelMethodStatus = activateBigBlueFormHandler.handleActivateBigBlueRequest(requestMock, responseMock)
		
		then : 
		
		handelMethodStatus == false
		activateCouponRequestVO.getmSiteFlag().equals(BBBCoreConstants.SITE_BBB_VALUE)
		activateBigBlueFormHandler.isSuccessMessage() == false
		activateBigBlueFormHandler.getEmailAddress().equals("testeryopmail.com")
		sessionBeanMock.getCouponError() == null
		//2 * activateBigBlueFormHandler.addFormException(_)
		//activateBigBlueFormHandler.getmErrorMap(BBBCoreConstants.REGISTER_ERROR).equals("Coupon could not be activated.")
		//sessionBeanMock.getCouponError().equals(couponError)
	}
	
	
	/*
	 * Alternative branches covered :
	 * 
	 * #253 - if(BBBUtility.isEmpty(getActivateBigBlueVO().getOfferCd())) - Offer code is empty
	 * #439 - if(errorMessage !=null && !errorMessage.isEmpty()) - errorMessage is empty
	 * BedBathCanada site covered
	 */
	
	def "handleActivateBigBlueRequest - BedBathCanada- site Form error exists | errorMessage(coupon error) is invaild(empty)" () {
		
		given :
		
		populateTestClassSpy()
		
		def contextPath = "/store/pdp/product01"
		def handelMethodStatus
		//def couponError = ""
		HttpSession sessionMock = Mock()
		Captcha captcha = buildCaptcha()
		ErrorStatus errorStatusMock = Mock()
		
		//siteId = BBBCoreConstants.SITE_BBB
		siteId = BBBCoreConstants.SITE_BAB_CA
		activateCouponRequestVO.setEmailAddr("testeryopmail.com")
		
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		activateBigBlueFormHandler.setSiteContext(siteContextMock)
		
		errorStatusMock.isErrorExists() >> true
		errorStatusMock.getDisplayMessage() >> ""
		
		activateBigBlueFormHandler.getFormError() >> false

		activateCouponRequestVO.setOfferCd("")
		activateCouponRequestVO.setMobilePhone("123456789")
		
		sessionMock.getId() >> "session01"
		requestMock.getSession() >> sessionMock
		requestMock.getContextPath() >> contextPath
		sessionBeanMock.setCaptcha(captcha)
		activateCouponResponseVO.setStatus(errorStatusMock)
		(1.._) * activateCouponManagerMock.activateBigBlue(activateCouponRequestVO) >> activateCouponResponseVO
		//1 * sessionBeanMock.setCouponError(_) // not working
		
		when :
		
		handelMethodStatus = activateBigBlueFormHandler.handleActivateBigBlueRequest(requestMock, responseMock)
		
		then :
		
		handelMethodStatus == false
		activateCouponRequestVO.getmSiteFlag().equals(BBBCoreConstants.SITE_BAB_CA_VALUE)
		activateBigBlueFormHandler.isSuccessMessage() == false
	}
	

	
	/*
	 * Alternative branches covered :
	 *
	 * #434 - if(activateCouponRespVO.getStatus().isErrorExists()) - No error in coupon response
	 * 
	 */
	
	def "handleActivateBigBlueRequest - No error in coupon response" () {
		
		given :
		
		populateTestClassSpy()
		
		def contextPath = "/store/pdp/product01"
		def handelMethodStatus
		//def couponError = ""
		HttpSession sessionMock = Mock()
		Captcha captcha = buildCaptcha()
		ErrorStatus errorStatusMock = Mock()
		
		//siteId = BBBCoreConstants.SITE_BBB
		siteId = BBBCoreConstants.SITE_BAB_CA
		//activateCouponRequestVO.setEmailAddr(tem)
		activateCouponResponseVO.setEndDate(tempExpiryDate)
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		activateBigBlueFormHandler.setSiteContext(siteContextMock)
		
		errorStatusMock.isErrorExists() >> false
		errorStatusMock.getDisplayMessage() >> ""
		
		activateBigBlueFormHandler.getFormError() >> false
		activateBigBlueFormHandler.setContextAdded(false)

		activateCouponRequestVO.setOfferCd("")
		activateCouponRequestVO.setMobilePhone("123456789")
		
		sessionMock.getId() >> "session01"
		requestMock.getSession() >> sessionMock
		requestMock.getContextPath() >> contextPath
		sessionBeanMock.setCaptcha(captcha)
		activateCouponResponseVO.setStatus(errorStatusMock)
		(1.._) * activateCouponManagerMock.activateBigBlue(activateCouponRequestVO) >> activateCouponResponseVO
		//1 * sessionBeanMock.setCouponError(_) // not working
		
		when :
		
		handelMethodStatus = activateBigBlueFormHandler.handleActivateBigBlueRequest(requestMock, responseMock)
		
		then :
		
		handelMethodStatus == false
		activateCouponRequestVO.getmSiteFlag().equals(BBBCoreConstants.SITE_BAB_CA_VALUE)
		activateBigBlueFormHandler.isSuccessMessage() == true
		sessionBeanMock.getCouponEmail().equals(tempEmailAddress)
		sessionBeanMock.getCouponExpiry().equals(tempExpiryDate)
	}

		
	/*
	 * handleActivateBigBlueRequest - test cases - starts
	 *
	 * Alternative branches covered :
	 *
	 * #281 - if (!BBBUtility.isEmpty(sOfferCd)) - Offer code is empty //(unreachable code) Can't be covered as empty check previously done
	 * #287 - if (!BBBUtility.isStringLengthValid(sOfferCd, 1, 6)) - invalid offer code length
	 */
	
	def "handleActivateBigBlueRequest - Exception while activating big blue request" () {
		
		given :
		
		populateTestClassSpy()
		
		def contextPath = "/store/pdp/product01"
		def subscriptionErrorMessage = "TIBCO subscription error"
		def handelMethodStatus
		
		HttpSession sessionMock = Mock()
		Captcha captcha = buildCaptcha()
		Locale requestLocale = new Locale("en") 
		
		
		siteId = "BBB_US"
		
		siteMock.getId() >> siteId
		siteContextMock.getSite() >> siteMock
		
		activateBigBlueFormHandler.getFormError() >> false
		activateBigBlueFormHandler.setContextAdded(false)
		
		activateCouponRequestVO.setOfferCd("OFFER50")
		
		sessionMock.getId() >> "session01"
		requestMock.getSession() >> sessionMock
		requestMock.getContextPath() >> contextPath
		requestMock.getLocale() >> requestLocale
		sessionBeanMock.setCaptcha(captcha)
		(1.._) * lblTxtTemplateManagerMock.getErrMsg(*_) >> subscriptionErrorMessage
		(1.._) * activateCouponManagerMock.activateBigBlue(activateCouponRequestVO) >> {throw new Exception("TIBCO Exception")}
		
		when :
		 
		handelMethodStatus = activateBigBlueFormHandler.handleActivateBigBlueRequest(requestMock, responseMock)
		
		then :
		
		handelMethodStatus == false
			
	}

	
	
	/*
	 * handleActivateBigBlueRequest - test cases - ends
	 *
	 */
	
	
	
	/*
	 * Data populating methods 
	 */
	
	
	private populateTestClassSpy() {
		
		activateBigBlueFormHandler = Spy()
		activateBigBlueFormHandler.setSiteContext(siteContextMock)
		activateBigBlueFormHandler.setCaptchaAnswer(tempCaptchaAnswer)
		activateBigBlueFormHandler.setValidateCaptcha(tempValidateCaptcha)
		activateBigBlueFormHandler.setCurrentPageURL(currentPageURL)
		activateBigBlueFormHandler.setCurrentSite(currentSite)
		activateBigBlueFormHandler.setTemplateUrl(templateUrl)
		activateBigBlueFormHandler.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		activateBigBlueFormHandler.setActivateBigBlueVO(activateCouponRequestVO)
		activateBigBlueFormHandler.setRules(validationRulesObj)
		activateBigBlueFormHandler.setActivateCouponManager(activateCouponManagerMock)
		activateBigBlueFormHandler.setBbbCatalogTools(catalogToolsMock)
		activateBigBlueFormHandler.setEmailAddress(tempEmailAddress)
		activateBigBlueFormHandler.setExpiryDate(tempExpiryDate)
		activateBigBlueFormHandler.setSuccessURL(tempSuccessURL)
		activateBigBlueFormHandler.setContextAdded(contextAdded)
	}
	
	private Captcha buildCaptcha() {
		Captcha captcha = new Captcha.Builder(100, 200).addText().addBackground(new GradiatedBackgroundProducer()).gimp()
				.addNoise()
				.addBorder()
				.build()
				
		return captcha
	}
	
}
