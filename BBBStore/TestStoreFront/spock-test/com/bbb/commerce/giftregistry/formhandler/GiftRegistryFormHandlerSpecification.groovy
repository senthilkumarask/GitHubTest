package com.bbb.commerce.giftregistry.formhandler

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie

import com.bbb.utils.BBBUtility

import nl.captcha.Captcha
import nl.captcha.backgrounds.GradiatedBackgroundProducer

import org.apache.commons.lang.StringEscapeUtils;

import atg.commerce.CommerceException
import atg.commerce.gifts.InvalidGiftTypeException
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.json.JSONException
import atg.multisite.Site
import atg.multisite.SiteContext
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus
import atg.servlet.ServletUtil
import atg.nucleus.naming.ComponentName;
import atg.nucleus.registry.NucleusRegistry
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile
import atg.userprofiling.ProfileFormHashtable
import atg.userprofiling.email.TemplateEmailException
import atg.userprofiling.email.TemplateEmailInfoImpl

import com.bbb.account.BBBProfileTools
import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO
import com.bbb.commerce.exim.bean.EximImagePreviewVO
import com.bbb.commerce.exim.bean.EximImagesVO
import com.bbb.commerce.exim.bean.EximSummaryResponseVO
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.vo.AddressVO
import com.bbb.commerce.giftregistry.vo.EventVO
import com.bbb.commerce.giftregistry.vo.ForgetRegPassRequestVO
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO
import com.bbb.commerce.giftregistry.vo.RegCopyResVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO
import com.bbb.commerce.giftregistry.vo.ShippingVO
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.email.EmailHolder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.integration.vo.ResponseErrorVO
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.RequestAddressType;
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean
import com.bbb.simplifyRegistry.RegistryInputVO
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager
import com.bbb.vo.wishlist.WishListVO
import com.bbb.wishlist.BBBWishlistManager
import com.bbb.wishlist.GiftListVO
import com.bbb.wishlist.manager.BBBGiftlistManager

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class GiftRegistryFormHandlerSpecification extends BBBExtendedSpec {
	
	GiftRegistryFormHandler testObj
	Nucleus nucleusMock = Mock()
	NucleusRegistry sNucleusRegistry = Mock()
	SimplifyRegistryManager simplifyRegistryManagerMock = Mock()
	BBBProfileTools bbbProfileToolsMock = Mock()
	BBBPropertyManager propertyManagerMock = Mock()
	MutableRepositoryItem mutableRepositoryItemMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	SiteContext siteContextMock = Mock()
	Site siteMock = Mock()
	GiftRegistryManager giftRegistryManagerMock = Mock()
	Profile profileMock = Mock()
	GiftRegistryRecommendationManager giftRegistryRecommendationManagerMock = Mock()
	LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	BBBCatalogTools bbbCatalogToolsMock = Mock()
	BBBEximManager eximPricingManagerMock = Mock()
	GiftRegistryTools giftRegistryToolsMock = Mock()
	BBBWishlistManager wishlistManagerMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	BBBGiftlistManager giftListManagerMock = Mock()
	EmailHolder emailHolderMock = Mock()
	PrintWriter printWriterMock = Mock()
	TemplateEmailInfoImpl giftRegEmailInfoMock = Mock()
	MutableRepository profileRepositoryMock = Mock()
	Cookie cookieMock = Mock()
	RepeatingRequestMonitor repeatingRequestMonitorMock = Mock()
	MutableRepository mutableRepositoryMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	
	def setup(){
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
		
		testObj = new GiftRegistryFormHandler(simplifyRegistryManager:simplifyRegistryManagerMock,mProfileTools:bbbProfileToolsMock,propertyManager:propertyManagerMock,
			sessionBean:sessionBeanMock,verifyUserErrorURLPage:"/store/giftregistry/frags/simpleReg_chkuser_json.jsp",siteContext:siteContextMock,
			giftRegistryManager:giftRegistryManagerMock,viewEditFailureURL:"/store/giftregistry/my_registries.jsp",viewEditSuccessURL:"/store/giftregistry/view_registry_owner.jsp",
			registryFlyoutURL:"/store/giftregistry/registry_search_guest.jsp",giftRegistryRecommendationManager:giftRegistryRecommendationManagerMock,
			successUrlMap:["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp","checkoutPageLogin":"/checkout/shipping/shipping.jsp"],
			errorUrlMap:["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp","checkoutPageLogin":"/checkout/guest_checkout.jsp"],
			lblTxtTemplateManager:lblTxtTemplateManagerMock,profile:profileMock,addItemsToRegServiceName:"addItemsToRegistry",bbbCatalogTools:bbbCatalogToolsMock,
			addItemsToReg2ServiceName:"addItemsToRegistry2",eximPricingManager:eximPricingManagerMock,giftRegistryTools:giftRegistryToolsMock,copyRegistryServiceName:"regCopy",
			wishlistManager:wishlistManagerMock,giftListManager:giftListManagerMock,importRegServiceName:"importRegistry",validatedCaptcha:TRUE,
			emailHolder:emailHolderMock,giftRegEmailInfo:giftRegEmailInfoMock,annCardCountServiceName:"setAnnouncementCardCount",
			registryUpdateSuccessURL:"/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp",registryUpdateErrorURL:"/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp",
			updateRegServiceName:"updateRegistry2",profileRepository:profileRepositoryMock,messageHandler:lblTxtTemplateManagerMock,
			guestRegistryUri:"/store/giftregistry/view_registry_guest.jsp?registryId=",loginRedirectUrl:"/store/account/login.jsp")

		giftRegistryManagerMock.getGiftRegistryTools() >> giftRegistryToolsMock
		
	}
	
	
	/* *************************************************************************************************************************************
	 * getRegistryInputsVO Method STARTS
	 * 
	 * Signature : public RegistryInputsByTypeVO getRegistryInputsVO(String pEventType)
	 * 
	 * *************************************************************************************************************************************/
	
	def"getRegistryInputsVO. This TC is the Happy flow of getRegistryInputsVO Method"(){
		given:
			String eventType = "eventType"
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO()
			1 * simplifyRegistryManagerMock.getRegInputsByRegType(eventType) >> registryInputsByTypeVOMock
			 
		when:
			RegistryInputsByTypeVO results = testObj.getRegistryInputsVO(eventType)
			
		then:
			results == registryInputsByTypeVOMock
	}
	
	def"getRegistryInputsVO. This TC is when RepositoryException thrown"(){
		given:
			testObj = Spy()
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			String eventType = "eventType"
			1 * simplifyRegistryManagerMock.getRegInputsByRegType(eventType) >> {throw new RepositoryException("Mock for RepositoryException")}
			 
		when:
			RegistryInputsByTypeVO results = testObj.getRegistryInputsVO(eventType)
			
		then:
			results != null
			1 * testObj.logError('Error occurred in getRegistryInputsVo method when registryInputsByTypeVO', _)
	}
	
	
	/* *************************************************************************************************************************************
	 * getRegistryInputsVO Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleVerifyUser Method STARTS
	 *
	 * Signature : public boolean handleVerifyUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	
	def"handleVerifyUser. This TC is when profileStatus is profile_already_exist"(){
		given:
			testObj = Spy()
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setProfileTools(bbbProfileToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setVerifyUserErrorURLPage("/store/giftregistry/frags/simpleReg_chkuser_json.jsp")
			
			RegistrantVO registrantVOMock = new RegistrantVO(email:"bbbcustomer@bbb.com")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:registrantVOMock)
			testObj.setRegistryVO(registryVOMock)
			
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailAddressProperty"
			1 * bbbProfileToolsMock.checkForRegistration("bbbcustomer@bbb.com") >> "profile_already_exist"
			Map<String, String> errorMapMock = ["1":"error"]
			testObj.setErrorMap(errorMapMock)
			 
		when:
			boolean results = testObj.handleVerifyUser(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getRegistryProfileStatus().equals("regUserAlreadyExists")
			sessionBeanMock.isRegistredUser().equals(TRUE)
			1 * testObj.checkFormRedirect(testObj.getVerifyUserErrorURLPage(), testObj.getVerifyUserErrorURLPage() , requestMock, responseMock)

	}
	
	def"handleVerifyUser. This TC is when profileStatus is profile_available_for_extenstion"(){
		given:
			RegistrantVO registrantVOMock = new RegistrantVO(email:"bbbcustomer@bbb.com")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:registrantVOMock)
			testObj.setRegistryVO(registryVOMock)
			
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailAddressProperty"
			1 * bbbProfileToolsMock.checkForRegistration("bbbcustomer@bbb.com") >> "profile_available_for_extenstion"
			Map<String, String> errorMapMock = [:]
			testObj.setErrorMap(errorMapMock)
			testObj.setLoginEmail("customerEmail@bbb.com")
			 
		when:
			boolean results = testObj.handleVerifyUser(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getRegistryProfileStatus().equals("refProfileExtenssion")
			sessionBeanMock.isRegistredUser().equals(TRUE)
			sessionBeanMock.getUserEmailId().equals("customerEmail@bbb.com")
			testObj.isMigrationFlag().equals(TRUE)

	}
	
	def"handleVerifyUser. This TC is when profileStatus is Profile not found"(){
		given:
			RegistrantVO registrantVOMock = new RegistrantVO(email:"bbbcustomer@bbb.com")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:registrantVOMock)
			testObj.setRegistryVO(registryVOMock)
			
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailAddressProperty"
			1 * bbbProfileToolsMock.checkForRegistration("bbbcustomer@bbb.com") >> "Profile not found"
			Map<String, String> errorMapMock = ["1":"error"]
			testObj.setErrorMap(errorMapMock)
			 
		when:
			boolean results = testObj.handleVerifyUser(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getRegistryProfileStatus().equals("regNewUser")
			sessionBeanMock.isRegistredUser().equals(FALSE)
	}
	
	def"handleVerifyUser. This TC is when profileStatus is no Profile found"(){
		given:
			RegistrantVO registrantVOMock = new RegistrantVO(email:"bbbcustomer@bbb.com")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:registrantVOMock)
			testObj.setRegistryVO(registryVOMock)
			
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailAddressProperty"
			1 * bbbProfileToolsMock.checkForRegistration("bbbcustomer@bbb.com") >> "no Profile found"
			Map<String, String> errorMapMock = ["1":"error"]
			testObj.setErrorMap(errorMapMock)
			 
		when:
			boolean results = testObj.handleVerifyUser(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	def"handleVerifyUser. This TC is when BBBBusinessException thrown and profileStatus is null"(){
		given:
			testObj = Spy()
			testObj.setPropertyManager(propertyManagerMock)
			testObj.setProfileTools(bbbProfileToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setVerifyUserErrorURLPage("/store/giftregistry/frags/simpleReg_chkuser_json.jsp")
			RegistrantVO registrantVOMock = new RegistrantVO(email:"bbbcustomer@bbb.com")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:registrantVOMock)
			testObj.setRegistryVO(registryVOMock)
			
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailAddressProperty"
			1 * bbbProfileToolsMock.checkForRegistration("bbbcustomer@bbb.com") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			Map<String, String> errorMapMock = [:]
			testObj.setErrorMap(errorMapMock)
			testObj.setLoginEmail("customerEmail@bbb.com")
			 
		when:
			boolean results = testObj.handleVerifyUser(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('Error while validating the profile is valid or not by mail id: com.bbb.exception.BBBBusinessException: Mock for BBBBusinessException', _)
			1 * testObj.checkFormRedirect(testObj.getVerifyUserErrorURLPage(), testObj.getVerifyUserErrorURLPage() , requestMock, responseMock)
			sessionBeanMock.getUserEmailId().equals("customerEmail@bbb.com")
			
	}
	
	
	/* *************************************************************************************************************************************
	 * handleVerifyUser Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleRegistryTypes Method STARTS
	 *
	 * Signature : public final void handleRegistryTypes(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleRegistryTypes. This TC is the Happy Flow of handleRegistryTypes method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			sessionBeanMock.setRegistryTypesEvent("wedding")
			testObj.setRegistryEventType("Others")
			ServletUtil.setCurrentUserProfile(mutableRepositoryItemMock)
			 
		when:
			testObj.handleRegistryTypes(requestMock, responseMock)
			
		then:
			sessionBeanMock.getRegistryTypesEvent().equals("Others")
			1 * mutableRepositoryItemMock.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, "txt_login_CreateRegistry")
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp", "/store/checkout/singlePage/ajax/guest_json.jsp", requestMock, responseMock)
			1 * testObj.setSuccessURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.setErrorURL('/store/checkout/singlePage/ajax/guest_json.jsp')
	}
	
	def"handleRegistryTypes. This TC is the when fromPage is empty and RegistryTypesEvent in sessionBean is null"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setFromPage("")
			sessionBeanMock.setRegistryTypesEvent(null)
			testObj.setRegistryEventType("Others")
			ServletUtil.setCurrentUserProfile(mutableRepositoryItemMock)
			 
		when:
			testObj.handleRegistryTypes(requestMock, responseMock)
			
		then:
			sessionBeanMock.getRegistryTypesEvent().equals("Others")
			1 * mutableRepositoryItemMock.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, "txt_login_CreateRegistry")
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock)
	}
	
	/* *************************************************************************************************************************************
	 * handleRegistryTypes Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleLoginToManageActiveRegistry Method STARTS
	 *
	 * Signature : public final void handleLoginToManageActiveRegistry(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleLoginToManageActiveRegistry. This TC is the Happy flow of handleLoginToManageActiveRegistry"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSuccessURL("/cart.jsp")
			testObj.setErrorURL("/cart.jsp")
			ServletUtil.setCurrentUserProfile(mutableRepositoryItemMock)
			
		when:
			testObj.handleLoginToManageActiveRegistry(requestMock, responseMock)
			
		then:
			sessionBeanMock.getMngActRegistry().equals("LoginToManageActiveRegistry")
			1 * mutableRepositoryItemMock.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, "txt_login_CreateRegistry")
			1 * testObj.checkFormRedirect(testObj.getSuccessURL(), testObj.getErrorURL(), requestMock, responseMock)
	}
	
	
	/* *************************************************************************************************************************************
	 * handleLoginToManageActiveRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/

	/* *************************************************************************************************************************************
	 * handleViewManageRegistry Method STARTS
	 *
	 * Signature : public final void handleViewManageRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleViewManageRegistry. This TC is the Happy Flow of handleViewManageRegistry method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRegistryIdEventType("235253_34")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("235253", "BedBathUS") >> registrySummaryVOMock
			 
		when:
			testObj.handleViewManageRegistry(requestMock, responseMock)
			
		then:
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp?registryId=235253&eventType=34', '/store/checkout/singlePage/ajax/guest_json.jsp', requestMock, responseMock)
			1 * testObj.setErrorURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.setSuccessURL('/store/checkout/singlePage/ajax/guest_json.jsp')
	}
	
	def"handleViewManageRegistry. This TC is when fromPage is empty and RegistryIdEventType is null"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			testObj.setRegistryIdEventType(null)
			 
		when:
			testObj.handleViewManageRegistry(requestMock, responseMock)
			
		then:
			1 * testObj.checkFormRedirect('null?registryId=null&eventType=null', null, requestMock, responseMock)
			1 * testObj.setSuccessURL('null?registryId=null&eventType=null')
	}
	
	/* *************************************************************************************************************************************
	 * handleViewManageRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleViewEditRegistry Method STARTS
	 *
	 * Signature : public final void handleViewEditRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleViewEditRegistry. This TC is the Happy Flow of handleViewEditRegistry method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setViewEditSuccessURL("/store/giftregistry/view_registry_owner.jsp")
			testObj.setViewEditFailureURL("/store/giftregistry/my_registries.jsp")
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.getParameter("registryId") >> "235253"
			requestMock.getParameter("eventType") >> "Wedding"
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("235253", "BedBathUS") >> registrySummaryVOMock
			 
		when:
			testObj.handleViewEditRegistry(requestMock, responseMock)
			
		then:
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			1 * testObj.checkFormRedirect('/store/giftregistry/view_registry_owner.jsp?registryId=235253&eventType=Wedding', '/store/giftregistry/my_registries.jsp', requestMock, responseMock)
			1 * testObj.setSuccessURL('/store/giftregistry/view_registry_owner.jsp?registryId=235253&eventType=Wedding')
			1 * testObj.setErrorURL('/store/giftregistry/my_registries.jsp')
	}
	
	def"handleViewEditRegistry. This TC is when eventType is null"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			testObj.setViewEditSuccessURL("/store/giftregistry/view_registry_owner.jsp")
			testObj.setViewEditFailureURL("/store/giftregistry/my_registries.jsp")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.getParameter("registryId") >> "235253"
			requestMock.getParameter("eventType") >> null
			 
		when:
			testObj.handleViewEditRegistry(requestMock, responseMock)
			
		then:
			1 * testObj.checkFormRedirect('/store/giftregistry/view_registry_owner.jsp?registryId=235253&eventType=null', '/store/giftregistry/my_registries.jsp', requestMock, responseMock)
			1 * testObj.setSuccessURL('/store/giftregistry/view_registry_owner.jsp?registryId=235253&eventType=null')
			1 * testObj.setErrorURL('/store/giftregistry/my_registries.jsp')
	}
	
	/* *************************************************************************************************************************************
	 * handleViewEditRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleBuyOffStartBrowsing Method STARTS
	 *
	 * Signature : public boolean handleBuyOffStartBrowsing(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleBuyOffStartBrowsing. This TC is the Happy Flow of handleBuyOffStartBrowsing method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setErrorURL("/my_registries.jsp")
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setBuyoffStartBrowsingSuccessURL("/giftregistry/view_registry_owner.jsp")
			testObj.setRegistryId("235253")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("235253", "BedBathUS") >> registrySummaryVOMock
			1 * testObj.checkFormRedirect("/giftregistry/view_registry_owner.jsp", testObj.getErrorURL(), requestMock, responseMock) >> TRUE
			 
		when:
			boolean results = testObj.handleBuyOffStartBrowsing(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getBuyoffStartBrowsingSummaryVO().equals(registrySummaryVOMock)

	}
	
	def"handleBuyOffStartBrowsing. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			testObj.setErrorURL("/my_registries.jsp")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setBuyoffStartBrowsingSuccessURL("/giftregistry/view_registry_owner.jsp")
			testObj.setRegistryId("235253")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("235253", "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * testObj.checkFormRedirect("/giftregistry/view_registry_owner.jsp", testObj.getErrorURL(), requestMock, responseMock) >> TRUE
			 
		when:
			boolean results = testObj.handleBuyOffStartBrowsing(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('Error in handle buyOff ', _)
	}
	
	/* *************************************************************************************************************************************
	 * handleBuyOffStartBrowsing Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	
	/* *************************************************************************************************************************************
	 * handleRegistrySearchFromFlyout Method STARTS
	 *
	 * Signature : public boolean handleRegistrySearchFromFlyout(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleRegistrySearchFromFlyout. This TC is the Happy flow of handleRegistrySearchFromFlyout method"(){
		given:
			testObj = Spy()
			testObj.setRegistryFlyoutURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setSessionBean(sessionBeanMock)
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeDesc:"Wedding")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registryTypesMock)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			1 * testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			 
		when:
			boolean results = testObj.handleRegistrySearchFromFlyout(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/giftregistry/registry_search_guest.jsp")
	}
	
	def"handleRegistrySearchFromFlyout. This TC is when registryType is null"(){
		given:
			testObj = Spy()
			testObj.setRegistryFlyoutURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setSessionBean(sessionBeanMock)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			1 * testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			 
		when:
			boolean results = testObj.handleRegistrySearchFromFlyout(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/giftregistry/registry_search_guest.jsp")
	}
	
	def"handleRegistrySearchFromFlyout. This TC is when pRegSummaryVO is null"(){
		given:
			testObj = Spy()
			testObj.setRegistryFlyoutURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setSessionBean(sessionBeanMock)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			1 * testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			 
		when:
			boolean results = testObj.handleRegistrySearchFromFlyout(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/giftregistry/registry_search_guest.jsp")
	}
	
	def"handleRegistrySearchFromFlyout. This TC is when sessionBean is null"(){
		given:
			testObj = Spy()
			testObj.setRegistryFlyoutURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setSessionBean(null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			1 * testObj.handleRegistrySearch(requestMock, responseMock) >> FALSE
			 
		when:
			boolean results = testObj.handleRegistrySearchFromFlyout(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getRegistrySearchErrorURL().equals("/store/giftregistry/registry_search_guest.jsp")
	}
	
	
	/* *************************************************************************************************************************************
	 * handleRegistrySearchFromFlyout Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	* handleClearSessionBeanData Method STARTS
	*
	* Signature : public void handleClearSessionBeanData(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	*
	* *************************************************************************************************************************************/
	
	
	def"handleClearSessionBeanData. This TC is the Happy flow of handleClearSessionBeanData method"(){
		given:
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			sessionBeanMock.setKickStarterId("2232321")
			sessionBeanMock.setEventType("Wedding")
			profileMock.isTransient() >> FALSE

		when:
			testObj.handleClearSessionBeanData(requestMock, responseMock)
			
		then:
			sessionBeanMock.getGiftRegistryViewBean().equals(null)
			sessionBeanMock.getKickStarterId().equals("2232321")
			sessionBeanMock.getEventType().equals("Wedding")
	}
	
	
	def"handleClearSessionBeanData. This TC is when isTransient is true and KickStarterId is null"(){
		given:
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			sessionBeanMock.setKickStarterId(null)
			sessionBeanMock.setEventType("Wedding")
			profileMock.isTransient() >> TRUE

		expect:
			testObj.handleClearSessionBeanData(requestMock, responseMock)
	}
	
	def"handleClearSessionBeanData. This TC is EventType is null"(){
		given:
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			sessionBeanMock.setKickStarterId("2232321")
			sessionBeanMock.setEventType(null)
			profileMock.isTransient() >> FALSE

		when:
			testObj.handleClearSessionBeanData(requestMock, responseMock)
		then:
			sessionBeanMock.getGiftRegistryViewBean().equals(null)
	}
	
	
	/* *************************************************************************************************************************************
	 * handleClearSessionBeanData Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleEmailOptInChange Method STARTS
	 *
	 * Signature : public boolean handleEmailOptInChange(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleEmailOptInChange. This TC is the Happy flow of handleEmailOptInChange"(){
		given:
			testObj.setRegistryId("22333222")
			testObj.setEmailOptIn("customer")
			1 * giftRegistryRecommendationManagerMock.setEmailOptInChange(testObj.getRegistryId(), testObj.getEmailOptIn())
			
		when:
			boolean results = testObj.handleEmailOptInChange(requestMock, responseMock)
		then:
			results == FALSE
	}
	
	def"handleEmailOptInChange. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setRegistryId("22333222")
			testObj.setEmailOptIn("customer")
			1 * giftRegistryRecommendationManagerMock.setEmailOptInChange(testObj.getRegistryId(), testObj.getEmailOptIn()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleEmailOptInChange(requestMock, responseMock)
		then:
			results == TRUE
			1 * testObj.logError('Exception while setting email options: com.bbb.exception.BBBSystemException: Mock for BBBSystemException', null)
			1 * testObj.setErrorFlagEmailOptIn(false)
			1 * testObj.setErrorFlagEmailOptIn(true)
	}
	
	/* *************************************************************************************************************************************
	 * handleEmailOptInChange Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handlePrintInvitationCards Method STARTS
	 *
	 * Signature : public boolean handlePrintInvitationCards(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handlePrintInvitationCards. This TC is the Happy flow of handlePrintInvitationCards method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setHtmlMessage("/_assets/bbregistry/images/bridalToolkit")
			testObj.setDownloadFlag(TRUE)
			testObj.setSuccessURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setErrorURL("/store/giftregistry/registry_search_guest.jsp")
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.PRINT_CARDS_AT_HOME_IMAGES_HOST) >> "/bbb"
			1 * giftRegistryManagerMock.generatePDFDocument(requestMock, responseMock, "/bbb/_assets/bbregistry/images/bridalToolkit", true)
			1 * testObj.checkFormRedirect(testObj.getSuccessURL(), testObj.getErrorURL(), requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handlePrintInvitationCards(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getHtmlMessage().equals("/bbb/_assets/bbregistry/images/bridalToolkit")
	}
	
	def"handlePrintInvitationCards. This TC is when imageHost is empty"(){
		given:
			testObj.setHtmlMessage("/_assets/bbregistry/images/bridalToolkit")
			testObj.setDownloadFlag(TRUE)
			testObj.setSuccessURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setErrorURL("/store/giftregistry/registry_search_guest.jsp")
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.PRINT_CARDS_AT_HOME_IMAGES_HOST) >> ""
			1 * giftRegistryManagerMock.generatePDFDocument(requestMock, responseMock, "/_assets/bbregistry/images/bridalToolkit", true)
			
		when:
			boolean results = testObj.handlePrintInvitationCards(requestMock, responseMock)
		then:
			results == FALSE
	}
	
	def"handlePrintInvitationCards. This TC is when htmlMessage is empty"(){
		given:
			testObj.setHtmlMessage("")
			testObj.setDownloadFlag(TRUE)
			testObj.setSuccessURL("/store/giftregistry/registry_search_guest.jsp")
			testObj.setErrorURL("/store/giftregistry/registry_search_guest.jsp")
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.PRINT_CARDS_AT_HOME_IMAGES_HOST) >> "/bbb"
			1 * giftRegistryManagerMock.generatePDFDocument(requestMock, responseMock, "", true)
			
		when:
			boolean results = testObj.handlePrintInvitationCards(requestMock, responseMock)
		then:
			results == FALSE
	}
	
	/* *************************************************************************************************************************************
	 * handlePrintInvitationCards Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleToggleBlockRecommender Method STARTS
	 *
	 * Signature : public boolean handleToggleBlockRecommender(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleToggleBlockRecommender. This TC is when profile transient is false"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setSiteContext(siteContextMock)
			Map<String, String> successUrlMapMock = ["checkoutPageLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["checkoutPageLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("checkoutPageLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			profileMock.isTransient() >> FALSE
			testObj.setRegistryId("22333322")
			testObj.setRecommenderProfileId("p12345")
			testObj.setRequestedFlag("true")
			1 * giftRegistryRecommendationManagerMock.persistToggleRecommenderStatus(testObj.getRegistryId(), testObj.getRecommenderProfileId(), testObj.getRequestedFlag()) >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR_KEY, requestMock.getLocale().getLanguage() , null,null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp?siteId equals 0', '/store/checkout/guest_checkout.jsp?siteId equals 0', requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleToggleBlockRecommender(requestMock, responseMock)
		then:
			results == TRUE
			1 * testObj.setErrorURL('/store/checkout/guest_checkout.jsp?siteId equals 0')
			1 * testObj.setSuccessURL('/store/checkout/shipping/shipping.jsp?siteId equals 0')
			1 * testObj.logError('Exception Occurred', null)
			testObj.getRegErrorMap().get("TogglePersistError").equals("Exception Occurred")
			
	}
	
	def"handleToggleBlockRecommender. This TC is when result is true and Queryparam is empty"(){
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("checkoutPageLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			profileMock.isTransient() >> FALSE
			testObj.setRegistryId("22333322")
			testObj.setRecommenderProfileId("p12345")
			testObj.setRequestedFlag("true")
			1 * giftRegistryRecommendationManagerMock.persistToggleRecommenderStatus(testObj.getRegistryId(), testObj.getRecommenderProfileId(), testObj.getRequestedFlag()) >> TRUE
			
		when:
			boolean results = testObj.handleToggleBlockRecommender(requestMock, responseMock)
		then:
			results == FALSE
	}
	
	def"handleToggleBlockRecommender. This TC is when profile transient is true"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setSiteContext(siteContextMock)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			profileMock.isTransient() >> TRUE
			testObj.setRegistryId("22333322")
			testObj.setRecommenderProfileId("p12345")
			testObj.setRequestedFlag("true")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ILLEGAL_TOGGLE_REQUEST_KEY, requestMock.getLocale().getLanguage() , null,null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> FALSE
			
		when:
			boolean results = testObj.handleToggleBlockRecommender(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logError('Illegal Request is received. Non-Logged in user has requested to true the recommender with profileId:-p12345 for registryId:-22333322', null)
			testObj.getRegErrorMap().get("TogglePersistError").equals("Exception Occurred")
			
	}
	
	def"handleToggleBlockRecommender. This TC is when BBBSystemException occurs"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setSiteContext(siteContextMock)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			testObj.setFromPage("checkoutPageLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			profileMock.isTransient() >> FALSE
			testObj.setRegistryId("22333322")
			testObj.setRecommenderProfileId("p12345")
			testObj.setRequestedFlag("true")
			1 * giftRegistryRecommendationManagerMock.persistToggleRecommenderStatus(testObj.getRegistryId(), testObj.getRecommenderProfileId(), testObj.getRequestedFlag()) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION, requestMock.getLocale().getLanguage(), null,	null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect(null,null, requestMock, responseMock) >> FALSE
			
		when:
			boolean results = testObj.handleToggleBlockRecommender(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logError('Unable to get the registry recommendations.', _)
			testObj.getRegErrorMap().get("TogglePersistError").equals("Exception Occurred")
	}
	
	
	/* *************************************************************************************************************************************
	 * handleToggleBlockRecommender Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleUndoRedo Method STARTS
	 *
	 * Signature : public boolean handleUndoRedo(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleUndoRedo. This TC is the Happy flow of handleUndoRedo method"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setLoggingDebug(TRUE)
			
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> FALSE
			1 * giftRegistryRecommendationManagerMock.performUndo(_, "undoFrom") >> FALSE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			
			1 * testObj.checkFormRedirect(testObj.getUndoSuccessURL(), testObj.getUndoFailureURL(), requestMock, responseMock) >> TRUE
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Public Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			 
			//setRegistryFlags Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			
			//populateGiftRegistryRemove Private Method Coverage
			testObj.setSkuId("sku12345")
			testObj.setProductId("prod12345")
			testObj.getValue().put("rowId", "rowId")
			
			//updateSessionAfterRemovingFromRegistry Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"22333322",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			testObj.setQuantity("2")
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			registrySummaryVOMock.getGiftRegistered().equals(3)
			1 * testObj.logError('p12345|false|BBBBusinessException from handleUndoRedo of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('userToken: userToken', null)
			1 * testObj.logDebug('siteFlag: true', null)
			1 * testObj.logDebug('ServiceName: null', null)
	}
	
	def"handleUndoRedo. This TC is when transient is true"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			1 * testObj.checkFormRedirect("/account/myaccount.jsp", "/account/myaccount.jsp", requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('Non-logged in user.', null)
			
	}
	
	def"handleUndoRedo. This TC is when RegistryId is empty"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("")
			1 * testObj.checkFormRedirect("/account/myaccount.jsp", "/account/myaccount.jsp", requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: Illegal Parameters.', null)
			
	}
	
	def"handleUndoRedo. This TC is when UndoFrom is empty"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("")
			testObj.setRegistryId("22333222")
			1 * testObj.checkFormRedirect("/account/myaccount.jsp", "/account/myaccount.jsp", requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: Illegal Parameters.', null)
			
	}
	
	def"handleUndoRedo. This TC is when registryId is valid"(){
		given:
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId('REG_summary2351211$')
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == FALSE
	}
	
	def"handleUndoRedo. This TC is when registrySummaryVOMock registryId is not equals to registryId"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >>> [FALSE,TRUE]
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> FALSE
			1 * giftRegistryRecommendationManagerMock.performUndo(_, "undoFrom") >> FALSE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> TRUE
			1 * testObj.checkFormRedirect(testObj.getUndoSuccessURL(), testObj.getUndoFailureURL(), requestMock, responseMock) >> TRUE
			
			//preRemoveItemFromGiftRegistry Public Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			 
			//setRegistryFlags Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			
			//updateSessionAfterRemovingFromRegistry Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"5855455")
			RegistrySummaryVO registrySummaryVOMock1 = new RegistrySummaryVO()
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfo("22333322", "BedBathUS") >> registrySummaryVOMock1 
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock1)
			1 * testObj.logError('p12345|true|BBBBusinessException from handleUndoRedo of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1003: p12345|true|User type is transient Exception from preRemoveItemFromGiftRegistry of GiftRegistryFormHandler', null)
			1 * testObj.addFormException(*_)
	}
	
	def"handleUndoRedo. This TC is when registrySummaryVOMock is null"(){
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> FALSE
			1 * giftRegistryRecommendationManagerMock.performUndo(_, "undoFrom") >> FALSE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Public Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			 
			//setRegistryFlags Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			
			//updateSessionAfterRemovingFromRegistry Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == FALSE
	}
	
	def"handleUndoRedo. This TC is when result is true"(){
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> FALSE
			1 * giftRegistryRecommendationManagerMock.performUndo(_, "undoFrom") >> TRUE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Public Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			 
			//setRegistryFlags Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == FALSE
	}
	
	def"handleUndoRedo. This TC is when formError is true"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> TRUE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			1 * testObj.checkFormRedirect(testObj.getUndoSuccessURL(), testObj.getUndoFailureURL(), requestMock, responseMock) >> TRUE
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Public Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	def"handleUndoRedo. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> TRUE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			1 * testObj.checkFormRedirect(testObj.getUndoSuccessURL(), testObj.getUndoFailureURL(), requestMock, responseMock) >> TRUE
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('p12345|false|BBBBusinessException from handleUndoRedo of GiftRegistryFormHandler', null)
			1 * testObj.logError('Mock for BBBBusinessException', _)
	}
	
	def"handleUndoRedo. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setProfile(profileMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setSessionBean(sessionBeanMock)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.setUndoFrom("undoFrom")
			testObj.setRegistryId("22333322")
			testObj.getFormError() >> TRUE
			testObj.setUndoSuccessURL("/account/myaccount.jsp")
			testObj.setUndoFailureURL("/account/myaccount.jsp")
			1 * testObj.checkFormRedirect(testObj.getUndoSuccessURL(), testObj.getUndoFailureURL(), requestMock, responseMock) >> TRUE
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleUndoRedo(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('Mock for BBBSystemException', _)
			1 * testObj.logError('p12345|false|BBBBusinessException from handleUndoRedo of GiftRegistryFormHandler', null)
	}
	
	/* *************************************************************************************************************************************
	 * handleUndoRedo Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleAddItemToGiftRegistryFromCetona Method STARTS
	 *
	 * Signature : public boolean handleAddItemToGiftRegistryFromCetona(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is the Happy flow of handleAddItemToGiftRegistryFromCetona"(){
		given:
			this.spyAddItemToGiftRegistryFromCertona()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> FALSE
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"] 
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			EximImagePreviewVO previewsMock = new EximImagePreviewVO(size:"large",url:"/bbb/large")
			EximImagePreviewVO previewsMock1 = new EximImagePreviewVO(size:"x-small",url:"/bbb/xsmall")
			EximImagePreviewVO previewsMock2 = new EximImagePreviewVO(size:"medium",url:"/bbb/medium")
			EximImagePreviewVO previewsMock3 = new EximImagePreviewVO(size:"extraLarge",url:"/bbb/extraLarge")
			EximImagesVO imagesMock = new EximImagesVO(id:"back")
			EximImagesVO imagesMock1 = new EximImagesVO(id:"front",previews:[previewsMock,previewsMock1,previewsMock2,previewsMock3])
			EximCustomizedAttributesVO eximAtrributesMock = new EximCustomizedAttributesVO(customizationService:"service",namedrop:"nameDrop",images:[imagesMock,imagesMock1],retailPriceAdder:10d)
			EximCustomizedAttributesVO eximAtrributesMock1 = new EximCustomizedAttributesVO()
			EximSummaryResponseVO eximSummaryResponseVOMock = new EximSummaryResponseVO(customizations:[eximAtrributesMock,eximAtrributesMock1])
			1 * eximPricingManagerMock.invokeSummaryAPI("prod12345", null,	"-1") >> eximSummaryResponseVOMock
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			
			1 * giftRegistryManagerMock.addItemToGiftRegistry({GiftRegistryViewBean giftRegistryViewBean -> giftRegistryViewBean.siteFlag == "true" && giftRegistryViewBean.userToken == "userToken" &&
				giftRegistryViewBean.serviceName == "addItemsToRegistry2" && giftRegistryViewBean.parentProductId == "prod12345" && giftRegistryViewBean.registrySize == 1 &&
				giftRegistryViewBean.refNum == "-1" &&	giftRegistryViewBean.assemblyPrices == "" && giftRegistryViewBean.personlizationCodes == "service" && giftRegistryViewBean.assemblySelections == "" &&
				giftRegistryViewBean.itemTypes == "PER" && giftRegistryViewBean.ltlDeliveryPrices == "" && giftRegistryViewBean.ltlDeliveryServices == "" && giftRegistryViewBean.personalizationDescrips == "nameDrop" &&
				giftRegistryViewBean.personalizedImageUrls == "/bbb/large" && giftRegistryViewBean.personalizedImageUrlThumbs == "/bbb/xsmall" && giftRegistryViewBean.personalizedMobImageUrlThumbs == "/bbb/xsmall" &&
				giftRegistryViewBean.personalizedMobImageUrls == "/bbb/medium" && giftRegistryViewBean.personalizationPrices == "10.0" && giftRegistryViewBean.customizationPrices == "10.0"}) >> validateAddItemsResVOMock
			
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"22333322",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			
			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0", "/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
			
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setRegistryId("22333322")
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> TRUE
			
			//setAddItemsBean Private Method Coverage
			testObj.setProductId("prod12345")
			testObj.setQuantity("2")
			testObj.setSkuIds("sku12345")
			testObj.setRefNum("-1")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", testObj.getSkuIds()) >> TRUE
			testObj.setLtlDeliveryServices("LTL")
			testObj.setLtlDeliveryPrices("23")
			1 * bbbCatalogToolsMock.getDeliveryCharge("BedBathUS", testObj.getSkuIds(), testObj.getLtlDeliveryServices()) >> 12d
			testObj.setPersonalizationCode("CR")
			1 * bbbCatalogToolsMock.getListPrice("prod12345", "sku12345") >> 21d
			1 * bbbCatalogToolsMock.getSalePrice("prod12345", "sku12345") >> 19d
						
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.isErrorFlagAddItemToRegistry().equals(FALSE)
			registrySummaryVOMock.getGiftRegistered().equals(7)
			sessionBeanMock.getValues().get("22333322_REG_SUMMARY").equals(null)
			testObj.getRegistryItemOperation().equals("addedCertonaItem")
			testObj.getProductStringAddItemCertona().equals(';prod12345;;;event22=2|event23=38.0;eVar30=sku12345')
			testObj.isItemAddedToRegistry().equals(TRUE)
			1 * testObj.setErrorURL('/store/checkout/guest_checkout.jsp')
			1 * testObj.setSuccessURL('/store/checkout/shipping/shipping.jsp?siteId equals 0')
			1 * testObj.setLtlFlag('true')
			1 * testObj.logDebug('REFNUM IS -1 PersonlizationCode isCR', null)
			1 * testObj.logDebug('siteFlag: true', null)
			1 * testObj.logDebug('userToken: userToken', null)
			1 * testObj.logDebug('ServiceName: null', null)
	}
	
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is when istransient is true and refNum is empty"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry(_) >> validateAddItemsResVOMock
			
			testObj.setRegistryId("22333322")
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"85855515",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			
			//setAddItemsBean Private Method Coverage
			testObj.setProductId("prod12345")
			testObj.setQuantity("2")
			testObj.setSkuIds("sku12345")
			testObj.setRefNum("")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", testObj.getSkuIds()) >> TRUE
			testObj.setLtlDeliveryServices(null)
			testObj.setLtlDeliveryPrices(null)
			testObj.setPersonalizationCode("PB")
			1 * bbbCatalogToolsMock.getListPrice("prod12345", "sku12345") >> 21d
			1 * bbbCatalogToolsMock.getSalePrice("prod12345", "sku12345") >> 0d
						
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.isErrorFlagAddItemToRegistry().equals(FALSE)
			registrySummaryVOMock.getGiftRegistered().equals(5)
			sessionBeanMock.getValues().get("22333322_REG_SUMMARY").equals(null)
			testObj.getRegistryItemOperation().equals("addedCertonaItem")
			testObj.getProductStringAddItemCertona().equals(';prod12345;;;event22=2|event23=42.0;eVar30=sku12345')
			testObj.isItemAddedToRegistry().equals(TRUE)
	}
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is when personalizationCode is empty and userRegistrysummaryVO is null"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry(_) >> validateAddItemsResVOMock
			
			testObj.setRegistryId("22333322")
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistrysummaryVO", null)
			
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			
			//setAddItemsBean Private Method Coverage
			testObj.setProductId("prod12345")
			testObj.setQuantity("2")
			testObj.setSkuIds("sku12345")
			testObj.setRefNum("1")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", testObj.getSkuIds()) >> FALSE
			testObj.setLtlDeliveryServices(null)
			testObj.setLtlDeliveryPrices(null)
			testObj.setPersonalizationCode("")
			1 * bbbCatalogToolsMock.getListPrice("prod12345", "sku12345") >> 21d
			1 * bbbCatalogToolsMock.getSalePrice("prod12345", "sku12345") >> 0d
						
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.isErrorFlagAddItemToRegistry().equals(FALSE)
			sessionBeanMock.getValues().get("22333322_REG_SUMMARY").equals(null)
			testObj.getRegistryItemOperation().equals("addedCertonaItem")
			testObj.getProductStringAddItemCertona().equals(';prod12345;;;event22=2|event23=42.0;eVar30=sku12345')
			testObj.isItemAddedToRegistry().equals(TRUE)
	}
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is when errorId is 900"(){
		given:
			this.spyAddItemToGiftRegistryFromCertona()
		
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"] 
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			EximImagesVO imagesMock = new EximImagesVO(id:"",previews:[])
			EximCustomizedAttributesVO eximAtrributesMock = new EximCustomizedAttributesVO(customizationService:"service",namedrop:"nameDrop",images:[imagesMock],retailPriceAdder:10d)
			EximCustomizedAttributesVO eximAtrributesMock1 = new EximCustomizedAttributesVO()
			EximSummaryResponseVO eximSummaryResponseVOMock = new EximSummaryResponseVO(customizations:[eximAtrributesMock,eximAtrributesMock1])
			1 * eximPricingManagerMock.invokeSummaryAPI("prod12345", null,	"-1") >> eximSummaryResponseVOMock
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorMessage:"ErrorMessage",errorId:900)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry(_) >> validateAddItemsResVOMock
			
			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp?siteId equals 0', '/store/checkout/shipping/shipping.jsp?siteId equals 0', requestMock, responseMock) >> TRUE
			
			//setAddItemsBean Private Method Coverage
			testObj.setProductId("prod12345")
			testObj.setQuantity("2")
			testObj.setSkuIds("sku12345")
			testObj.setRefNum("-1")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", testObj.getSkuIds()) >> TRUE
			testObj.setLtlDeliveryServices("LTL")
			testObj.setLtlDeliveryPrices("23")
			1 * bbbCatalogToolsMock.getDeliveryCharge("BedBathUS", testObj.getSkuIds(), testObj.getLtlDeliveryServices()) >> 12d
			testObj.setPersonalizationCode("PB")
			1 * bbbCatalogToolsMock.getListPrice("prod12345", "sku12345") >> 21d
			1 * bbbCatalogToolsMock.getSalePrice("prod12345", "sku12345") >> 19d
			
			//errorAdditemCertona Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
						
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
			1 * testObj.logError('giftregistry_10102: Fatal error from errorAdditemCertona of GiftRegistryFormHandler | webservice code =900', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
			1 * testObj.logDebug('siteFlag: true', null)
			1 * testObj.logDebug('ServiceName: null', null)
			1 * testObj.logDebug('PersonalizationCode: PB RefNum: -1', null)
			1 * testObj.logDebug('REFNUM IS -1 PersonlizationCode isPB', null)
			1 * testObj.logDebug('userToken: userToken', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
			1 * testObj.setSuccessURL('/store/checkout/shipping/shipping.jsp?siteId equals 0')
			1 * testObj.setErrorURL('/store/checkout/shipping/shipping.jsp?siteId equals 0')
			1 * testObj.setErrorURL('/store/checkout/guest_checkout.jsp')
			1 * testObj.setLtlFlag('true')
	}
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyAddItemToGiftRegistryFromCertona()
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setRegistryId("22333322")
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
								
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleAddItemToGiftRegistryFromCetona of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
	}
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is when BBBSystemException thrown"(){
		given:
			this.spyAddItemToGiftRegistryFromCertona()
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setRegistryId("22333322")
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "22333322", "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
								
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: err_regsearch_sys_exception', null)
			1 * testObj.logError('giftregistry_1005: BBBBusinessException from handleAddItemToGiftRegistryFromCetona of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_sys_exception', 'en_us', null, null)
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
	}
	
	def"handleAddItemToGiftRegistryFromCetona. This TC is when NumberFormatException thrown"(){
		given:
			this.spyAddItemToGiftRegistryFromCertona()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			EximCustomizedAttributesVO eximAtrributesMock = new EximCustomizedAttributesVO(customizationService:"service",namedrop:"nameDrop",images:[],retailPriceAdder:10d)
			EximCustomizedAttributesVO eximAtrributesMock1 = new EximCustomizedAttributesVO()
			EximSummaryResponseVO eximSummaryResponseVOMock = new EximSummaryResponseVO(customizations:[eximAtrributesMock,eximAtrributesMock1])
			1 * eximPricingManagerMock.invokeSummaryAPI("prod12345", null,	"-1") >> eximSummaryResponseVOMock
			
			//setAddItemsBean Private Method Coverage
			testObj.setProductId("prod12345")
			testObj.setQuantity("")
			testObj.setSkuIds("sku12345")
			testObj.setRefNum("-1")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", testObj.getSkuIds()) >> FALSE
			testObj.setLtlDeliveryServices(null)
			testObj.setLtlDeliveryPrices(null)
			testObj.setPersonalizationCode("PY")
			1 * bbbCatalogToolsMock.getListPrice("prod12345", "sku12345") >> 21d
			1 * bbbCatalogToolsMock.getSalePrice("prod12345", "sku12345") >> 0d
						
		when:
			boolean results = testObj.handleAddItemToGiftRegistryFromCetona(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
			1 * testObj.logError('giftregistry_10101: NumberFormatException from handleAddItemToGiftRegistryFromCetona of GiftRegistryFormHandler', _)
			1 * testObj.setSuccessURL('/store/checkout/shipping/shipping.jsp')
			1 * testObj.setErrorURL('/store/checkout/guest_checkout.jsp')
			1 * testObj.logDebug('siteFlag: true', null)
			1 * testObj.logDebug('ServiceName: null', null)
			1 * testObj.logDebug('userToken: userToken', null)
			1 * testObj.logDebug('PersonalizationCode: PY RefNum: -1', null)
			1 * testObj.logDebug('REFNUM IS -1 PersonlizationCode isPY', null)
	}

	private spyAddItemToGiftRegistryFromCertona() {
		testObj = Spy()
		testObj.setSiteContext(siteContextMock)
		testObj.setAddItemsToReg2ServiceName("addItemsToRegistry2")
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setEximPricingManager(eximPricingManagerMock)
		testObj.setProfile(profileMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		giftRegistryManagerMock.getGiftRegistryTools() >> giftRegistryToolsMock
		testObj.setLoggingDebug(TRUE)
	}
	
	
	/* *************************************************************************************************************************************
	 * handleAddItemToGiftRegistryFromCetona Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * preAddItemToGiftRegistry Method STARTS
	 *
	 * Signature : public void preAddItemToGiftRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse, 
	 * 			   final GiftRegistryViewBean giftRegistryViewBean)
	 *
	 * *************************************************************************************************************************************/
		
	def"preAddItemToGiftRegistry. This TC is when quantity is null and it throws NumberFormatException"(){
		given:
			testObj = Spy()
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			AddItemsBean addItemsBeanMock = new AddItemsBean(quantity:null)
			AddItemsBean addItemsBeanMock1 = new AddItemsBean()
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(additem:[addItemsBeanMock,addItemsBeanMock1])
		
		when:
			testObj.preAddItemToGiftRegistry(requestMock, responseMock, giftRegistryViewBeanMock)
		then:
			thrown NumberFormatException
			1 * testObj.logError('giftregistry_1055: GiftRegistry empty items quantity from preAddItemToGiftRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_empty_quantity', 'en_us', null, null)
	}
	
	
	/* *************************************************************************************************************************************
	 * preAddItemToGiftRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleRegistrySearch Method STARTS
	 *
	 * Signature : public boolean handleRegistrySearch(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
		
	def"handleRegistrySearch. This TC is when getHidden is 2 and excludedRegNums has value"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> "excludedRegNums"
			profileMock.isTransient() >> FALSE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BBB123"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken1"]
			testObj.setSearchRegistryServiceName("registryService")
			testObj.setCreateRegistryServiceName("createRegistry")
			RegistryVO registryVOMock = new RegistryVO(siteId:"BedBathUS",userToken:"token1")
			testObj.setRegistryVO(registryVOMock)
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeDesc:"BirthDay")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registryTypesMock)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp?siteId equals 0', '/store/checkout/guest_checkout.jsp?siteId equals 0', requestMock, responseMock) >> TRUE
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennady",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getExcludedRegNums().equals("excludedRegNums")
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			registrySearchVOMock.isFilterRegistriesInProfile().equals(TRUE)
			registrySearchVOMock.getSiteId().equals("BBB123")
			registrySearchVOMock.getUserToken().equals("userToken1")
			registrySearchVOMock.setServiceName("registryService")
			giftRegSessionBeanMock.getRequestVO().equals(registrySearchVOMock)
			1 * testObj.logDebug('siteFlag: BedBathUS', null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: createRegistry', null)
	}
	
	def"handleRegistrySearch. This TC is when istransient is true and excludedRegNums is empty"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> ""
			profileMock.isTransient() >> TRUE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BBB123"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken1"]
			testObj.setSearchRegistryServiceName("registryService")
			testObj.setSessionBean(sessionBeanMock)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennady",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == FALSE
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			registrySearchVOMock.getSiteId().equals("BBB123")
			registrySearchVOMock.getUserToken().equals("userToken1")
			registrySearchVOMock.setServiceName("registryService")
			giftRegSessionBeanMock.getRequestVO().equals(registrySearchVOMock)
	}
	
	def"handleRegistrySearch. This TC is when isHidden is 4 and pRegSummaryVO is null"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			testObj.setHidden(4)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			profileMock.isTransient() >> TRUE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BBB123"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken1"]
			testObj.setSearchRegistryServiceName("registryService")
			sessionBeanMock.getValues().put("userRegistrysummaryVO", null)
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "false"
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
			testObj.setCreateRegistryServiceName("createRegistry")
			RegistryVO registryVOMock = new RegistryVO(siteId:"BedBathUS",userToken:"token1")
			testObj.setRegistryVO(registryVOMock)
			
			1 * testObj.checkFormRedirect(null,null, requestMock, responseMock) >> TRUE
			
		
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getGiftGiver().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getSiteId().equals("BBB123")
			registrySearchVOMock.getUserToken().equals("userToken1")
			registrySearchVOMock.setServiceName("registryService")
			giftRegSessionBeanMock.getRequestVO().equals(registrySearchVOMock)
			1 * testObj.logDebug('siteFlag: BedBathUS', null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: createRegistry', null)
	}
	
	def"handleRegistrySearch. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(4)
			profileMock.isTransient() >> TRUE
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			testObj.setSearchRegistryServiceName("registryService")
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "false"
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getLocale() >> new Locale("en_US")
			
		1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp', '/store/checkout/guest_checkout.jsp', requestMock, responseMock) >> TRUE
			
		
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getGiftGiver().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			giftRegSessionBeanMock.getRequestVO().equals(null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleRegistrySearch of GiftRegistryformHandler', _)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			
	}
	
	def"handleRegistrySearch. This TC is when BBBSystemException thrown"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(4)
			profileMock.isTransient() >> TRUE
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.setSearchRegistryServiceName("registryService")
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "false"
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getLocale() >> new Locale("en_US")
			
		1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp', '/store/checkout/guest_checkout.jsp', requestMock, responseMock) >> TRUE
			
		
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getGiftGiver().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			giftRegSessionBeanMock.getRequestVO().equals(null)
			1 * testObj.logError('giftregistry_1005: BBBSystemException from handleRegistrySearch of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_regsearch_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_sys_exception', 'en_us', null, null)
	}

	def"handleRegistrySearch. This TC is when formerror is true"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> "excludedRegNums"
			profileMock.isTransient() >> FALSE

			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp?siteId equals 0', '/store/checkout/guest_checkout.jsp?siteId equals 0', requestMock, responseMock) >> TRUE
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"",email:"bbb@gmail.com",registryId:"22312121")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getExcludedRegNums().equals("excludedRegNums")
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			giftRegSessionBeanMock.getRequestVO().equals(null)
			1 * testObj.logDebug('adding form exception: err_srch_first_last_email_regid: err_srch_first_last_email_regid property=null.registrySearchFirstLastEmailRegId exception=null', null)
			1 * testObj.logError('giftregistry_1062: Either First name, Last name or Email or registry id should be used from searchLegacyRegistry of GiftRegistryFormhandler', null)
	}
	
	def"handleRegistrySearch. This TC is when isHidden is 1 and sessionBean is null"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			testObj.setHidden(1)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			profileMock.isTransient() >> TRUE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BBB123"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken1"]
			testObj.setSearchRegistryServiceName("registryService")
			testObj.setSessionBean(null)
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "false"
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		when:
			boolean results = testObj.handleRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getGiftGiver().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getSiteId().equals("BBB123")
			registrySearchVOMock.getUserToken().equals("userToken1")
			registrySearchVOMock.setServiceName("registryService")
			giftRegSessionBeanMock.getRequestVO().equals(registrySearchVOMock)
	}
	
	/* *************************************************************************************************************************************
	 * handleRegistrySearch Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleMxRegistrySearch Method STARTS
	 *
	 * Signature : public boolean handleMxRegistrySearch(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	
	def"handleMxRegistrySearch. This TC is when getHidden is 2 and excludedRegNums has value"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> "excludedRegNums"
			profileMock.isTransient() >> FALSE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken1"]
			testObj.setSearchRegistryServiceName("registryService")
			testObj.setCreateRegistryServiceName("createRegistry")
			RegistryVO registryVOMock = new RegistryVO(siteId:"BedBathUS",userToken:"token1")
			testObj.setRegistryVO(registryVOMock)
			
			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp', '/store/checkout/guest_checkout.jsp', requestMock, responseMock) >> TRUE
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleMxRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getExcludedRegNums().equals("excludedRegNums")
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			registrySearchVOMock.isFilterRegistriesInProfile().equals(TRUE)
			registrySearchVOMock.getSiteId().equals("5")
			registrySearchVOMock.setServiceName("registryService")
			registrySearchVOMock.getUserToken().equals("userToken1")
			giftRegSessionBeanMock.getRequestVO().equals(registrySearchVOMock)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_MX, "true")
			1 * testObj.logDebug('siteFlag: BedBathUS', null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: createRegistry', null)
	}
	
	def"handleMxRegistrySearch. This TC is when getHidden is 2 and excludedRegNums is empty"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> ""
			profileMock.isTransient() >> TRUE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken1"]
			testObj.setSearchRegistryServiceName("registryService")
			testObj.setCreateRegistryServiceName("createRegistry")
			RegistryVO registryVOMock = new RegistryVO(siteId:"BedBathUS",userToken:"token1")
			testObj.setRegistryVO(registryVOMock)
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleMxRegistrySearch(requestMock, responseMock)
		then:
			results == FALSE
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			registrySearchVOMock.getSiteId().equals("5")
			registrySearchVOMock.setServiceName("registryService")
			registrySearchVOMock.getUserToken().equals("userToken1")
			giftRegSessionBeanMock.getRequestVO().equals(registrySearchVOMock)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_MX, "true")
	}
	
	def"handleMxRegistrySearch. This TC is when getHidden is 5 and formerror occurs"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			testObj.setHidden(5)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			profileMock.isTransient() >> FALSE
			1 * testObj.checkFormRedirect(null,null, requestMock, responseMock) >> TRUE
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",lastName:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
			//preRegistrySearch Public Method Coverage
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "false"
		
		when:
			boolean results = testObj.handleMxRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getGiftGiver().equals(TRUE)
			giftRegSessionBeanMock.getRequestVO().equals(null)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_MX, "true")
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.registrySearchFirstName exception=null', null)
			
	}
	
	def"handleMxRegistrySearch. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> "excludedRegNums"
			profileMock.isTransient() >> FALSE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			testObj.setSearchRegistryServiceName("registryService")
			requestMock.getLocale() >> new Locale("en_US")
			
			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp', '/store/checkout/guest_checkout.jsp', requestMock, responseMock) >> TRUE
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleMxRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getExcludedRegNums().equals("excludedRegNums")
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			registrySearchVOMock.isFilterRegistriesInProfile().equals(TRUE)
			registrySearchVOMock.getSiteId().equals("5")
			giftRegSessionBeanMock.getRequestVO().equals(null)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_MX, "true")
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleRegistrySearch of GiftRegistryformHandler', _)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
	}
	
	def"handleMxRegistrySearch. This TC is when BBBSystemException thrown"(){
		given:
			this.spyRegistrySearch()
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setHidden(2)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * giftRegistryManagerMock.getCommaSaperatedRegistryIds(testObj.getProfile(), "BedBathUS") >> "excludedRegNums"
			profileMock.isTransient() >> FALSE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			testObj.setSearchRegistryServiceName("registryService")
			requestMock.getLocale() >> new Locale("en_US")
			
			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp', '/store/checkout/guest_checkout.jsp', requestMock, responseMock) >> TRUE
			
			//searchLegacyRegistry Private Method Coverage
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			
		
		when:
			boolean results = testObj.handleMxRegistrySearch(requestMock, responseMock)
		then:
			results == TRUE
			registrySearchVOMock.getExcludedRegNums().equals("excludedRegNums")
			registrySearchVOMock.isReturnLeagacyRegistries().equals(TRUE)
			registrySearchVOMock.getProfileId().equals(profileMock)
			registrySearchVOMock.getGiftGiver().equals(FALSE)
			registrySearchVOMock.isFilterRegistriesInProfile().equals(TRUE)
			registrySearchVOMock.getSiteId().equals("5")
			giftRegSessionBeanMock.getRequestVO().equals(null)
			1 * requestMock.setParameter(BBBCoreConstants.FROM_MX, "true")
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_sys_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1005: BBBSystemException from handleRegistrySearch of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_regsearch_sys_exception', null)
	}

	private spyRegistrySearch() {
		testObj = Spy()
		testObj.setSiteContext(siteContextMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setProfile(profileMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setLoggingDebug(TRUE)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
	}

	
	/* *************************************************************************************************************************************
	 * handleMxRegistrySearch Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * preRegistrySearch Method STARTS
	 *
	 * Signature : public void preRegistrySearch(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preRegistrySearch. This TC is when hidden is 1 and fromMx is true"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(1)
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "true"
			
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"5666",lastName:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.registrySearchFirstName exception=null', null)
			
	}
	
	
	def"preRegistrySearch. This TC is when hidden is 2 and FN,email,registryId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",lastName:"Kennedy",email:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_srch_first_last_email_regid_empty: err_srch_first_last_email_regid_empty property=null.registrySearchFirstLastEmailRegIdEmpty exception=null', null)
			
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and FN,LN,email has values"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy",email:"bbb@gmaill.com")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1061: Either First name, Last name or Email should be used from searchLegacyRegistry of GiftRegistryFormhandler', null)
			1 * testObj.logDebug('adding form exception: err_srch_first_last_email: err_srch_first_last_email property=null.registrySearchFirstLastEmail exception=null', null)
			
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and FN,LN,registryId has values"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"Kennedy",registryId:"5025255")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1063: Either First name, Last name or registry id should be used from searchLegacyRegistry of GiftRegistryFormhandler', null)
			1 * testObj.logDebug('adding form exception: err_srch_first_last_regid: err_srch_first_last_regid property=null.registrySearchFirstLastRegId exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and email,registryId has values"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(email:"bbb@gmail.com",registryId:"5025255")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1060: Either email or registry id should be used from searchLegacyRegistry of GiftRegistryFormhandler', null)
			1 * testObj.logDebug('adding form exception: err_srch_email_regid: err_srch_email_regid property=null.registrySearchEmailRegId exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and email has wrong value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(email:"bbbgmail.com")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1041: GiftRegistry Invalid search email from searchLegacyRegistry of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_invalid_email_length: err_create_reg_invalid_email_length property=null.coRegEmail exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and email has correct value"(){
		given:
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(email:"bbb@gmail.com")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and registryId has wrong value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(registryId:"*reg*")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1045: GiftRegistry Invalid registry id from preRegistrySearch of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_registry_id_invalid: err_create_reg_registry_id_invalid property=null.registrySearchRegId exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 2 and registryId has valid value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(2)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(registryId:'REG_summary2351211$')
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and firstName,RegistryId has value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",registryId:"5025255")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.registrySearchFirstLastRegId exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and firstName,RegistryId are empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchFirstLastlRegIdEmpty exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and registryId has wrong Value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(registryId:"*2231*2")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_registry_id_invalid: err_create_reg_registry_id_invalid property=null.registrySearchRegId exception=null', null)
			1 * testObj.logError('giftregistry_1045: GiftRegistry Invalid registry id from preRegistrySearch of GiftRegistryFormHandler', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and registryId has valid Value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(registryId:'REG_summary2351211$')
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and lastName, RegistryId has value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(lastName:"John",registryId:"5025255")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.registrySearchFirstLastRegId exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and lastName has value, RegistryId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(lastName:"John",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchFirstLastlRegIdEmpty exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and firstName has value, RegistryId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchFirstLastlRegIdEmpty exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and firstName,lastname has value and registryId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"5225",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getParameter('fromMx') >> "false"
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchLastName exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 3 and firstName,lastname has value and registryId is empty, fromMx is true"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(3)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName:"5225",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getParameter('fromMx') >> "true"
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.registrySearchLastName exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN,LN,RegId has values"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName :"Kennady",registryId:"1213112")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			registrySearchVOMock.getFirstName().equals("John")
			registrySearchVOMock.getLastName().equals("Kennady")
			registrySearchVOMock.getRegistryId().equals("1213112")
			1 * testObj.logDebug('adding form exception: err_regsearch_all_filled: err_regsearch_all_filled property=null.preRegistrySearch exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN,LN,RegId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",lastName :"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_regsearch_all_filled: err_regsearch_all_filled property=null.preRegistrySearch exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN,LN is empty and RegId has invaild value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",lastName :"",registryId:"2*2232*2")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_regsearch_invalid_regid: err_regsearch_invalid_regid property=null.preRegistrySearch exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN,LN is empty and RegId has vaild value"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",lastName :"",registryId:'REG_summary2351211$')
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN,LN has values and RegId is empty"(){
		given:
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName :"Kennedy",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "false"
		
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN,LN has values and RegId is empty and fromMx is true"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			
			def mxNamePattern = /^[a-zA-Z]*/
			def namePattern = /^[a-zA-Z]+[a-zA-Z\\-\\'\\ \\.]*$/
			BBBValidationRules bbbRules = new BBBValidationRules()
			bbbRules.setMxNamePattern(mxNamePattern)
			bbbRules.setNamePattern(namePattern)
			BBBUtility.setRules(bbbRules)
			
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName :"Kennedy",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> "true"
			
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when NumberFormatException thrown"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName :"Kennedy",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
			requestMock.getParameter(BBBCoreConstants.FROM_MX) >> {throw new NumberFormatException("Mock for NumberFormatException")}
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_no_format_exception', null)
			1 * testObj.logError('giftregistry_10100:  err_no_format_exception from preRegistrySearch of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_no_format_exception', 'en_us', null, null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 7"(){
		given:
			testObj.setHidden(7)
		
		expect:
			testObj.preRegistrySearch(requestMock, responseMock)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN is empty and RegId,LN has values"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"",lastName :"Kennedy",registryId:"122323")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_regsearch_all_filled: err_regsearch_all_filled property=null.preRegistrySearch exception=null', null)
	}
	
	def"preRegistrySearch. This TC is when hidden is 8 and FN has value and RegId,LN is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHidden(8)
			RegistrySearchVO registrySearchVOMock = new RegistrySearchVO(firstName:"John",lastName :"",registryId:"")
			testObj.setRegistrySearchVO(registrySearchVOMock)
		
		when:
			testObj.preRegistrySearch(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_regsearch_all_filled: err_regsearch_all_filled property=null.preRegistrySearch exception=null', null)
	}
	
	/* *************************************************************************************************************************************
	 * preRegistrySearch Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	def"handleRegistrySearchFromBridalLanding. This TC is the Happy flow of handleRegistrySearchFromBridalLanding method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setBridalLandingURL("/store/bbregistry/bridal_landing.jsp")
			testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleRegistrySearchFromBridalLanding(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/bbregistry/bridal_landing.jsp")
	}
	
	def"handleRegistrySearchFromBabyLanding. This TC is the Happy flow of handleRegistrySearchFromBabyLanding method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setBabyLandingURL("/store/bbregistry/baby_landing.jsp")
			testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleRegistrySearchFromBabyLanding(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/bbregistry/baby_landing.jsp")
	}
	
	
	def"handleRegistrySearchFromNoSearchResultsPage. This TC is the Happy flow of handleRegistrySearchFromNoSearchResultsPage method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setNoSearchResultURL("/store/search/search.jsp")
			testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleRegistrySearchFromNoSearchResultsPage(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/search/search.jsp")
	}
	
	def"handleRegistrySearchFromImportRegistryPage. This TC is the Happy flow of handleRegistrySearchFromImportRegistryPage method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setRegistryImportSearchSuccessURL("/store/giftregistry/reg_import_msg_display.jsp")
			testObj.setRegistryImportSearchErrorURL("/store/giftregistry/reg_import_msg_display.jsp")
			testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleRegistrySearchFromImportRegistryPage(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getRegistrySearchSuccessURL().equals("/store/giftregistry/reg_import_msg_display.jsp")
			testObj.getRegistrySearchErrorURL().equals("/store/giftregistry/reg_import_msg_display.jsp")
	}
	
	def"handleRegistrySearchFromHomePage. This TC is the Happy flow of handleRegistrySearchFromHomePage method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setHomePageURL("/store/cms/homepage.jsp")
			testObj.handleRegistrySearch(requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleRegistrySearchFromHomePage(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getRegistrySearchErrorURL().equals("/store/cms/homepage.jsp")
	}
	
	/* *************************************************************************************************************************************
	 * handleCopyRegistry Method STARTS
	 *
	 * Signature : public boolean handleCopyRegistry(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleCopyRegistry. This TC is the Happy flow of handleCopyRegistry method"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setCopyRegistryServiceName("regCopy")
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setSrcRegistryId("256522")
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeDesc:"BirthDay")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registryTypesMock,registryId:"52114525",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			RegCopyResVO regCopyResVOMock = new RegCopyResVO(error:null,totalNumOfItemsCopied:"23")
			1 * giftRegistryManagerMock.copyRegistry({GiftRegistryViewBean giftRegistryViewBean -> giftRegistryViewBean.siteFlag == "true" && 
				giftRegistryViewBean.userToken == "token1" && giftRegistryViewBean.serviceName == "regCopy" && giftRegistryViewBean.sourceRegistry == "256522" && 
				giftRegistryViewBean.targetRegistry == "52114525" && giftRegistryViewBean.registryName == "BirthDay" }) >> regCopyResVOMock
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp', requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			registrySummaryVOMock.getGiftRegistered().equals(28)
			sessionBeanMock.getValues().get("52114525_REG_SUMMARY").equals(null)
			1 * testObj.setErrorURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.setSuccessURL('/store/checkout/singlePage/ajax/guest_json.jsp')
	
	}
	
	def"handleCopyRegistry. This TC is when hasError is true"(){
		given:
			testObj.setFromPage("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setSrcRegistryId("256522")
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeDesc:"BirthDay")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registryTypesMock,registryId:"52114525",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			ResponseErrorVO responseErrorVOMock = new ResponseErrorVO()
			RegCopyResVO regCopyResVOMock = new RegCopyResVO(error:responseErrorVOMock,totalNumOfItemsCopied:"23")
			1 * giftRegistryManagerMock.copyRegistry({GiftRegistryViewBean giftRegistryViewBean -> giftRegistryViewBean.siteFlag == "true" &&
				giftRegistryViewBean.userToken == "token1" && giftRegistryViewBean.serviceName == "regCopy" && giftRegistryViewBean.sourceRegistry == "256522" &&
				giftRegistryViewBean.targetRegistry == "52114525" && giftRegistryViewBean.registryName == "BirthDay" }) >> regCopyResVOMock
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			registrySummaryVOMock.getGiftRegistered().equals(28)
			!sessionBeanMock.getValues().get("52114525_REG_SUMMARY")
	
	}
	
	def"handleCopyRegistry. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setFromPage("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
			1 * testObj.logError('Mock for BBBSystemException', _)
	}
	
	def"handleCopyRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setFromPage("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
			1 * testObj.logError('Mock for BBBBusinessException', _)
	}
	
	def"handleCopyRegistry. This TC is when regType is null"(){
		given:
			testObj.setFromPage("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setSrcRegistryId("256522")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:null,registryId:"52114525",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
	}
	
	def"handleCopyRegistry. This TC is when pRegSummaryVO is null"(){
		given:
			testObj.setFromPage("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setSrcRegistryId("256522")
			sessionBeanMock.getValues().put("userRegistrysummaryVO", null)
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
	}
	
	def"handleCopyRegistry. This TC is when sessionBean is null"(){
		given:
			testObj.setFromPage("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setSrcRegistryId("256522")
			testObj.setSessionBean(null)
			
		when:
			boolean results = testObj.handleCopyRegistry(requestMock, responseMock)
		then:
			results == TRUE
	}
	
	
	/* *************************************************************************************************************************************
	 * handleCopyRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleMoveAllWishListItemsToRegistry Method STARTS
	 *
	 * Signature : public boolean handleMoveAllWishListItemsToRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleMoveAllWishListItemsToRegistry. This TC is the Happy flow of handleMoveAllWishListItemsToRegistry method"(){
		given:
		 	testObj = Spy()
			testObj.setWishlistManager(wishlistManagerMock)
			testObj.setProfile(profileMock)
			testObj.setLoggingDebug(TRUE)
			
			profileMock.getPropertyValue("email") >> "bbb@gmail.com"
			GiftListVO GiftListVOMock = new GiftListVO(wishListItemId:"w12345")
			GiftListVO GiftListVOMock1 = new GiftListVO(wishListItemId:"w85521")
			WishListVO wishListVOMock = new WishListVO(wishListItems:[GiftListVOMock,null,GiftListVOMock1])
			wishlistManagerMock.getWishListItems() >> wishListVOMock
			testObj.setRegistryId("2232358")
			
			testObj.handleMoveWishListItemToRegistry(requestMock, responseMock) >>> [FALSE,TRUE]
			testObj.getFormError() >>> [false,true]
			testObj.getFormExceptions() >> ["ErrorMessage"]
			
		when:
			boolean results = testObj.handleMoveAllWishListItemsToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			testObj.getMoveAllWishListItemsFailureResult().equals(["w85521":"ErrorMessage"])
			testObj.getWishListItemId().equals("w85521")
			1 * testObj.setWishListItemId('w12345')
			1 * testObj.logDebug('GiftRegistryFormHandler ::handleMoveAllWishListItemsToRegistry getting wish list for profile with email bbb@gmail.com', null)
			
	}
	
	def"handleMoveAllWishListItemsToRegistry. This TC is when itemArray is null"(){
		given:
			WishListVO wishListVOMock = new WishListVO(wishListItems:null)
			wishlistManagerMock.getWishListItems() >> wishListVOMock
			testObj.setRegistryId("2232358")
			
		when:
			boolean results = testObj.handleMoveAllWishListItemsToRegistry(requestMock, responseMock)
		then:
			results == FALSE
	}
	
	def"handleMoveAllWishListItemsToRegistry. This TC is when wishListItems is null"(){
		given:
			wishlistManagerMock.getWishListItems() >> null
			testObj.setRegistryId("2232358")
			
		when:
			boolean results = testObj.handleMoveAllWishListItemsToRegistry(requestMock, responseMock)
		then:
			results == FALSE
	}
	
	def"handleMoveAllWishListItemsToRegistry. This TC is when registryId is empty"(){
		given:
			testObj = Spy()
			testObj.setWishlistManager(wishlistManagerMock)
			testObj.setProfile(profileMock)
			testObj.setLoggingDebug(TRUE)
			profileMock.getPropertyValue("email") >> "bbb@gmail.com"
			WishListVO wishListVOMock = new WishListVO(wishListItems:null)
			wishlistManagerMock.getWishListItems() >> wishListVOMock
			testObj.setRegistryId("")
			
		when:
			boolean results = testObj.handleMoveAllWishListItemsToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_null_or_empty_registry_id: Please provide a valid non-empty registry id in the input', null)
			1 * testObj.logDebug('Input registry id is null or empty', null)
	}
	
	/* *************************************************************************************************************************************
	 * handleMoveAllWishListItemsToRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleMoveWishListItemToRegistry Method STARTS
	 *
	 * Signature : public boolean handleMoveWishListItemToRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleMoveWishListItemToRegistry. This TC is the Happy flow of handleMoveWishListItemToRegistry Method"(){
		given:
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			1 * giftListManagerMock.getGiftitem("w12345") >> repositoryItemMock
			
			AddItemsBean addItemsBeanMock = new AddItemsBean(quantity:"2")
			1 * giftRegistryToolsMock.populateRegistryItemWIthWishListItem(repositoryItemMock) >> addItemsBeanMock
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("223254151")
			testObj.setWishListItemId("w12345")
			
			//addIem Public Method Coverage
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE) 
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry({GiftRegistryViewBean giftRegistryViewBean -> giftRegistryViewBean.additem == [addItemsBeanMock] && giftRegistryViewBean.registryId == "223254151" && 
				giftRegistryViewBean.siteFlag == "true" && giftRegistryViewBean.userToken == "token1" && giftRegistryViewBean.serviceName == "addItemsToRegistry" &&
				giftRegistryViewBean.registrySize == 1}) >> validateAddItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"223254151",giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			//removeItemsFromGiftlist Protected Method Coverage
			1 * giftListManagerMock.removeItemFromGiftlist("id12345", "w12345")
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			testObj.isErrorFlagAddItemToRegistry().equals(FALSE)
			registrySummaryVOMock.getGiftRegistered().equals(7)
			!sessionBeanMock.getGiftRegistryViewBean().equals(null)
			addItemsBeanMock.getRegistryId().equals("223254151")
			
	}
	
	def"handleMoveWishListItemToRegistry. This TC is when errorToAdd is true and errorExists is true in addIem method"(){
		given:
			this.spyMoveWishListItem()
			
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			1 * giftListManagerMock.getGiftitem("w12345") >> repositoryItemMock
			testObj.setAddItemsToRegServiceName("addItemsToRegistry")
			requestMock.getLocale() >> new Locale("en_US")
			
			AddItemsBean addItemsBeanMock = new AddItemsBean()
			1 * giftRegistryToolsMock.populateRegistryItemWIthWishListItem(repositoryItemMock) >> addItemsBeanMock
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("223254151")
			testObj.setWishListItemId("w12345")
			
			//addIem Public Method Coverage
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorMessage:"ErrorMessage",errorId:901)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry({GiftRegistryViewBean giftRegistryViewBean -> giftRegistryViewBean.additem == [addItemsBeanMock] && giftRegistryViewBean.registryId == "223254151" &&
				giftRegistryViewBean.siteFlag == "true" && giftRegistryViewBean.userToken == "token1" && giftRegistryViewBean.serviceName == "addItemsToRegistry" &&
				giftRegistryViewBean.registrySize == 1}) >> validateAddItemsResVOMock
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
			!sessionBeanMock.getGiftRegistryViewBean().equals(null)
			addItemsBeanMock.getRegistryId().equals("223254151")
			1 * testObj.logError('giftregistry_10103: Either user token or site flag invalid from errorAdditemCertona of GiftRegistryFormHandler | webservice code =901', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_move_wishlist_item_to_registry: Error moving wish list item in registry ', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
			1 * testObj.logDebug('error adding item to registry hence not removing item from wish list', null)
			
	}
	
	def"handleMoveWishListItemToRegistry. This TC is when wishListItem is null"(){
		given:
			this.spyMoveWishListItem()
			
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			1 * giftListManagerMock.getGiftitem("w12345") >> null
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("223254151")
			testObj.setWishListItemId("w12345")
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_invalid_wishlistItem: Wish list item is invalid or not available in the repository', null)
			1 * testObj.logDebug('Wish list item is invalid or not available in the repository', null)
			
	}
	
	def"handleMoveWishListItemToRegistry. This TC is when registryId and wishListItemId are empty in preMoveWishListItemToRegistry method"(){
		given:
			this.spyMoveWishListItem()
			
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("")
			testObj.setWishListItemId("")
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logDebug('Input wishlist id is null or empty', null)
			1 * testObj.logDebug('Input registry id is null or empty', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_wishlist_id: Please provide a valid non-empty wishlist item id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_registry_id: Please provide a valid non-empty registry id in the input', null)
	}
	
	def"handleMoveWishListItemToRegistry. This TC is when CommerceException thrown"(){
		given:
			this.spyMoveWishListItem()
			
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			1 * giftListManagerMock.getGiftitem("w12345") >> {throw new CommerceException("Mock for CommerceException")}
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("223254151")
			testObj.setWishListItemId("w12345")
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_fetch_wish_list_item: exception fetching wishlist item to move to registry', null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleMoveWishListItemToRegistry of GiftRegistryFormHandler', _)
	}
	
	def"handleMoveWishListItemToRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyMoveWishListItem()
			
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			giftListManagerMock.getGiftitem("w12345") >> repositoryItemMock
			AddItemsBean addItemsBeanMock = new AddItemsBean(quantity:"2")
			1 * giftRegistryToolsMock.populateRegistryItemWIthWishListItem(repositoryItemMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("223254151")
			testObj.setWishListItemId("w12345")
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_business_config_value: Business Exception while fetching configure values ', null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleMoveWishListItemToRegistry of GiftRegistryFormHandler', _)
	}
	
	def"handleMoveWishListItemToRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyMoveWishListItem()
			
			profileMock.getPropertyValue("wishlist") >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "id12345"
			giftListManagerMock.getGiftitem("w12345") >> repositoryItemMock
			AddItemsBean addItemsBeanMock = new AddItemsBean(quantity:"2")
			1 * giftRegistryToolsMock.populateRegistryItemWIthWishListItem(repositoryItemMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//preMoveWishListItemToRegistry Private method Coverage
			testObj.setRegistryId("223254151")
			testObj.setWishListItemId("w12345")
			
		when:
			boolean results = testObj.handleMoveWishListItemToRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_system_config_value: System Exception while fetching configure values ', null)
			1 * testObj.logError('giftregistry_1005: BBBBusinessException from handleMoveWishListItemToRegistry of GiftRegistryFormHandler', _)
	}
	
	

	private spyMoveWishListItem() {
		testObj = Spy()
		testObj.setProfile(profileMock)
		testObj.setGiftListManager(giftListManagerMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setSiteContext(siteContextMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObj.setLoggingDebug(TRUE)
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
	}
	
	/* *************************************************************************************************************************************
	 * handleMoveWishListItemToRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * addIem Method STARTS
	 *
	 * Signature : public boolean addIem(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse, 
	 * 				final GiftRegistryViewBean giftRegistryViewBean, final String siteId)
	 *
	 * *************************************************************************************************************************************/
	
	def"addIem. This TC is when registryId is not equal to giftRegistryViewBean and registrySummaryVO"(){
		given:
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(registryId:"2323262")	
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry(giftRegistryViewBeanMock) >> validateAddItemsResVOMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"53201215")
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfo("2323262", "BedBathUS") >> registrySummaryVOMock
			
		when:
			boolean results = testObj.addIem(requestMock, responseMock, giftRegistryViewBeanMock, "BedBathUS")
			
		then:
			results == FALSE
			testObj.isErrorFlagAddItemToRegistry().equals(FALSE)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			
	}
	
	def"addIem. This TC is when registrySummaryVO is null"(){
		given:
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(registryId:"2323262")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ValidateAddItemsResVO validateAddItemsResVOMock = new ValidateAddItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.addItemToGiftRegistry(giftRegistryViewBeanMock) >> validateAddItemsResVOMock
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			
		when:
			boolean results = testObj.addIem(requestMock, responseMock, giftRegistryViewBeanMock, "BedBathUS")
			
		then:
			results == FALSE
			testObj.isErrorFlagAddItemToRegistry().equals(FALSE)
			
	}
	
	def"addIem. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(registryId:"2323262")
			1 * giftRegistryManagerMock.addItemToGiftRegistry(giftRegistryViewBeanMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.addIem(requestMock, responseMock, giftRegistryViewBeanMock, "BedBathUS")
			
		then:
			results == TRUE
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
			1 * testObj.logError('giftregistry_10104: BBBBusinessException from Add Item To GiftRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_exception_add_item_to_wishlist: Fatal Error adding item to registry', null)
	}
	
	def"addIem. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(registryId:"2323262")
			1 * giftRegistryManagerMock.addItemToGiftRegistry(giftRegistryViewBeanMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.addIem(requestMock, responseMock, giftRegistryViewBeanMock, "BedBathUS")
			
		then:
			results == TRUE
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
			1 * testObj.logError('giftregistry_10104: BBBSystemException from Add Item To GiftRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_exception_add_item_to_wishlist: Fatal Error adding item to registry', null)
	}
	
	/* *************************************************************************************************************************************
	 * addIem Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * removeItemsFromGiftlist Method STARTS
	 *
	 * Signature : protected void removeItemsFromGiftlist(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse, 
	 * 				final String giftListId, final String[] listItemIdToRemove)
	 *
	 * *************************************************************************************************************************************/
	
	def"removeItemsFromGiftlist. This TC is when listItemIdToRemove parameter passed as null"(){
		given:
			String giftListId = "23122"
			String[] listItemIdToRemove = null
			
		expect:
			testObj.removeItemsFromGiftlist(requestMock, responseMock, giftListId, listItemIdToRemove)
	}
	
	def"removeItemsFromGiftlist. This TC is when RepositoryException thrown"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setGiftListManager(giftListManagerMock)
			String giftListId = "23122"
			String[] listItemIdToRemove = ["id3235"]
			1 * giftListManagerMock.removeItemFromGiftlist("23122", "id3235") >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			testObj.removeItemsFromGiftlist(requestMock, responseMock, giftListId, listItemIdToRemove)
			
		then:
			1 * testObj.logError('account_1251: RepositoryException in GiftRegistryFormHandler while removeItemsFromGiftlist : Item not found', _)
	}
	
	def"removeItemsFromGiftlist. This TC is when InvalidGiftTypeException thrown"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setGiftListManager(giftListManagerMock)
			String giftListId = "23122"
			String[] listItemIdToRemove = ["id3235"]
			1 * giftListManagerMock.removeItemFromGiftlist("23122", "id3235") >> {throw new InvalidGiftTypeException("Mock for InvalidGiftTypeException")}
			
		when:
			testObj.removeItemsFromGiftlist(requestMock, responseMock, giftListId, listItemIdToRemove)
			
		then:
			1 * testObj.logError('account_1251: InvalidGiftTypeException in GiftRegistryFormHandler while removeItemsFromGiftlist : Item not found', _)
	}
	
	/* *************************************************************************************************************************************
	 * removeItemsFromGiftlist Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	def"handleSwitchCategoryPrice. This TC is the Happy flow of handleSwitchCategoryPrice"(){
		given:
			testObj = Spy()
			testObj.setSuccessURL("/account/order_summary.jsp")
			testObj.setErrorURL("/account/order_summary.jsp")
			1 *testObj.checkFormRedirect("/account/order_summary.jsp", "/account/order_summary.jsp", requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleSwitchCategoryPrice(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	
	/* *************************************************************************************************************************************
	 * handleforgetRegistryPassword Method STARTS
	 *
	 * Signature : public boolean handleforgetRegistryPassword(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleforgetRegistryPassword. This TC is the Happy flow of handleforgetRegistryPassword"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(FALSE)
			testObj.isValidSession(requestMock) >> TRUE
			testObj.setForgetPasswordRegistryId('REG_summary2351211$')
			ForgetRegPassRequestVO forgetRegPassRequestVOMock = new ForgetRegPassRequestVO()
			testObj.setForgetRegPassRequestVO(forgetRegPassRequestVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setForgetRegistryPasswordServiceName("passwordServiceName")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.forgetRegPasswordService(forgetRegPassRequestVOMock) >> registryResVOMock
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getForgetRegPassRequestVO().getForgetPassRegistryId().equals('REG_summary2351211$')
			testObj.getForgetRegPassRequestVO().getSiteFlag().equals("true")
			testObj.getForgetRegPassRequestVO().getUserToken().equals("token1")
			testObj.getForgetRegPassRequestVO().getServiceName().equals("passwordServiceName")
	}
	
	def"handleforgetRegistryPassword. This TC is when errorId is 900"(){
		given:
			this.spyForgetRegistryPassword()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> FALSE
			testObj.setForgetPasswordRegistryId('REG_summary2351211$')
			ForgetRegPassRequestVO forgetRegPassRequestVOMock = new ForgetRegPassRequestVO()
			testObj.setForgetRegPassRequestVO(forgetRegPassRequestVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setForgetRegistryPasswordServiceName("passwordServiceName")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorMessage",errorId:900)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.forgetRegPasswordService(forgetRegPassRequestVOMock) >> registryResVOMock
			
			//populateForgetRegPassError Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getForgetRegPassRequestVO().getForgetPassRegistryId().equals('REG_summary2351211$')
			testObj.getForgetRegPassRequestVO().getSiteFlag().equals("true")
			testObj.getForgetRegPassRequestVO().getUserToken().equals("token1")
			testObj.getForgetRegPassRequestVO().getServiceName().equals("passwordServiceName")
			testObj.getImportErrorMessage().equals("err_import")
			1 * testObj.logError('giftregistry_1059: Session expired exception from handleforgetRegistryPassword of GiftRegistryFormhandler', null)
			1 * testObj.logError('giftregistry_10107: Fatal error from handleAddItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=900', null)
			1 * testObj.logDebug('ServiceName: passwordServiceName', null)
			1 * testObj.logDebug('siteId: true', null)
			1 * testObj.logDebug('adding form exception: err_forgot_registry_password: err_import', null)
			1 * testObj.logDebug('adding form exception: err_session_exp_msg: err_session_exp_msg', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error: err_import', null)
	}
	
	def"handleforgetRegistryPassword. This TC is when errorId is 901"(){
		given:
			this.spyForgetRegistryPassword()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "err_password"
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			testObj.setForgetPasswordRegistryId('REG_summary2351211$')
			ForgetRegPassRequestVO forgetRegPassRequestVOMock = new ForgetRegPassRequestVO()
			testObj.setForgetRegPassRequestVO(forgetRegPassRequestVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setForgetRegistryPasswordServiceName("passwordServiceName")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorMessage",errorId:901)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.forgetRegPasswordService(forgetRegPassRequestVOMock) >> registryResVOMock
			
			//populateForgetRegPassError Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getForgetRegPassRequestVO().getForgetPassRegistryId().equals('REG_summary2351211$')
			testObj.getForgetRegPassRequestVO().getSiteFlag().equals("true")
			testObj.getForgetRegPassRequestVO().getUserToken().equals("token1")
			testObj.getForgetRegPassRequestVO().getServiceName().equals("passwordServiceName")
			testObj.getImportErrorMessage().equals("err_import")
			1 * testObj.logError('giftregistry_10112: Either user token or site flag invalid from handleforgetRegistryPassword of GiftRegistryFormHandler Webservice code =901', null)
			1 * testObj.logDebug('adding form exception: err_forgot_registry_password: err_import', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error: err_import', null)
	}
	
	def"handleforgetRegistryPassword. This TC is when errorId is 902"(){
		given:
			this.spyForgetRegistryPassword()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			testObj.setForgetPasswordRegistryId('REG_summary2351211$')
			ForgetRegPassRequestVO forgetRegPassRequestVOMock = new ForgetRegPassRequestVO()
			testObj.setForgetRegPassRequestVO(forgetRegPassRequestVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setForgetRegistryPasswordServiceName("passwordServiceName")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorMessage",errorId:902)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.forgetRegPasswordService(forgetRegPassRequestVOMock) >> registryResVOMock
			
			//populateForgetRegPassError Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getForgetRegPassRequestVO().getForgetPassRegistryId().equals('REG_summary2351211$')
			testObj.getForgetRegPassRequestVO().getSiteFlag().equals("true")
			testObj.getForgetRegPassRequestVO().getUserToken().equals("token1")
			testObj.getForgetRegPassRequestVO().getServiceName().equals("passwordServiceName")
			testObj.getImportErrorMessage().equals("err_import")
			1 * testObj.logDebug('adding form exception: err_forgot_registry_password: err_import', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format: err_import', null)
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from handleAddItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=902', null)
	}
	
	def"handleforgetRegistryPassword. This TC is when errorDisplayMessage is empty"(){
		given:
			this.spyForgetRegistryPassword()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			testObj.setForgetPasswordRegistryId('REG_summary2351211$')
			ForgetRegPassRequestVO forgetRegPassRequestVOMock = new ForgetRegPassRequestVO()
			testObj.setForgetRegPassRequestVO(forgetRegPassRequestVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setForgetRegistryPasswordServiceName("passwordServiceName")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:902)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.forgetRegPasswordService(forgetRegPassRequestVOMock) >> registryResVOMock
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getForgetRegPassRequestVO().getForgetPassRegistryId().equals('REG_summary2351211$')
			testObj.getForgetRegPassRequestVO().getSiteFlag().equals("true")
			testObj.getForgetRegPassRequestVO().getUserToken().equals("token1")
			testObj.getForgetRegPassRequestVO().getServiceName().equals("passwordServiceName")
			testObj.getImportErrorMessage().equals("No value found with key  err_import_srvc")
	}

	def"handleforgetRegistryPassword. This TC is when BBBSystemException thrown"(){
		given:
			this.spyForgetRegistryPassword()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			1 * testObj.preForgetRegistryPassword(requestMock, responseMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getImportErrorMessage().equals("No value found with key  err_import_srvc")
			1 * testObj.logError('giftregistry_1010: FORGET_PASSWORD_EXCEPTION BBBSystemException from handleforgetRegistryPassword of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: No value found with key  err_import_srvc: No value found with key  err_import_srvc', null)
	}
	
	def"handleforgetRegistryPassword. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyForgetRegistryPassword()
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			1 * testObj.preForgetRegistryPassword(requestMock, responseMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getImportErrorMessage().equals("No value found with key  err_import_srvc")
			1 * testObj.logError('giftregistry_1010: FORGET_PASSWORD_EXCEPTION BBBBusinessException from handleforgetRegistryPassword of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: No value found with key  err_import_srvc: No value found with key  err_import_srvc', null)
	}
	
	def"handleforgetRegistryPassword. This TC is when preForgetRegistryPassword returns false"(){
		given:
			this.spyForgetRegistryPassword()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			testObj.setForgetPasswordRegistryId("563*212*2")
			
		when:
			boolean results = testObj.handleforgetRegistryPassword(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('giftregistry_1048: GiftRegistry Invalid registryId from preForgetRegistryPassword of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_forgot_pass_reg_exception', 'en_us', null, null)
	}
	
	private spyForgetRegistryPassword() {
		testObj = Spy()
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setSiteContext(siteContextMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setLoggingDebug(TRUE)
	}
	
	/* *************************************************************************************************************************************
	 * handleforgetRegistryPassword Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleImportRegistry Method STARTS
	 *
	 * Signature : public boolean handleImportRegistry(final DynamoHttpServletRequest pRequest,	final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleImportRegistry. This TC is the Happy flow of handleImportRegistry method"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock 
			1 * giftRegistryManagerMock.linkRegistry(registryVOMock, TRUE) 
			sessionBeanMock.getValues().put("userRegistriesList", ["1231","3255"])
			sessionBeanMock.getValues().put("userActiveRegistriesList",["8558","9998"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			testObj.setImportEventDate("1/1/2017")
			testObj.setImportEventType("BirthDay")
			
			//updateProfileRegistryStatus Private Method Coverage
			testObj.setProfile(profileMock)
			1 * giftRegistryManagerMock.updateProfileRegistriesStatus(profileMock, "BedBathUS")
		
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			registryVOMock.getEvent().getEventDate().equals("1/1/2017")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("BirthDay")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["1231","3255","2232322"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["8558","9998","2232322"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
	}
	
	def"handleImportRegistry. This TC is when userRegList,userActiveRegList are null and BBBSystemException thrown in updateProfileRegistryStatus method"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			1 * giftRegistryManagerMock.linkRegistry(registryVOMock, TRUE)
			sessionBeanMock.getValues().put("userRegistriesList", null)
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			testObj.setImportEventDate("1/1/2017")
			testObj.setImportEventType("BirthDay")
			
			//updateProfileRegistryStatus Private Method Coverage
			testObj.setProfile(profileMock)
			1 * giftRegistryManagerMock.updateProfileRegistriesStatus(profileMock, "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			registryVOMock.getEvent().getEventDate().equals("1/1/2017")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("BirthDay")
			sessionBeanMock.getValues().get("userRegistriesList").equals(null)
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(null)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			1 * testObj.logError('BBBSystemException from mGiftRegistryManager.updateProfileRegistriesStatus method', _)
	}
	
	def"handleImportRegistry. This TC is when BBBBusinessException thrown in updateProfileRegistryStatus private method"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			1 * giftRegistryManagerMock.linkRegistry(registryVOMock, TRUE)
			sessionBeanMock.getValues().put("userRegistriesList", null)
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			testObj.setImportEventDate("1/1/2017")
			testObj.setImportEventType("BirthDay")
			
			//updateProfileRegistryStatus Private Method Coverage
			testObj.setProfile(profileMock)
			1 * giftRegistryManagerMock.updateProfileRegistriesStatus(profileMock, "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			registryVOMock.getEvent().getEventDate().equals("1/1/2017")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("BirthDay")
			sessionBeanMock.getValues().get("userRegistriesList").equals(null)
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(null)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			1 * testObj.logError('BBBBusinessException from mGiftRegistryManager.updateProfileRegistriesStatus method', _)
	}
	
	def"handleImportRegistry. This TC is when errorExists is true and errorId is 305"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:305)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			
			//setFormExceptionsForImportErrors Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_PASSWORD, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logDebug('ServiceName: importRegistry', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: err_import', null)
			1 * testObj.logError('giftregistry_10108: Invalid password GiftRegistry from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code =305', null)
			1 * testObj.setImportErrorMessage('err_import')
	}
	
	def"handleImportRegistry. This TC is when errorExists is true and errorId is 200"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:200)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			
			//setFormExceptionsForImportErrors Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logDebug('ServiceName: importRegistry', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: err_import', null)
			1 * testObj.logError('giftregistry_10109: Invalid input fields from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code =200', null)
			1 * testObj.setImportErrorMessage('err_import')
	}
	
	def"handleImportRegistry. This TC is when errorExists is true and errorId is 900"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:900)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			
			//setFormExceptionsForImportErrors Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logDebug('ServiceName: importRegistry', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: err_import', null)
			1 * testObj.logError('giftregistry_10110: Fatal error from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code =900', null)
			1 * testObj.setImportErrorMessage('err_import')
	}
	
	def"handleImportRegistry. This TC is when errorExists is true and errorId is 901"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:901)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			
			//setFormExceptionsForImportErrors Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logDebug('ServiceName: importRegistry', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: err_import', null)
			1 * testObj.logError('giftregistry_10111: Either user token or site flag invalid from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code =901', null)
			1 * testObj.setImportErrorMessage('err_import')
	}
	
	def"handleImportRegistry. This TC is when errorExists is true and errorId is 902"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:902)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			
			//setFormExceptionsForImportErrors Private Method Coverage
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logDebug('ServiceName: importRegistry', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: err_import', null)
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from handleImportRegistry() of GiftRegistryFormHandler | webservice error code=902', null)
			1 * testObj.setImportErrorMessage('err_import')
	}
	
	def"handleImportRegistry. This TC is when errorExists is true and errorId is 802"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:802)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logDebug('ServiceName: importRegistry', null)
			1 * testObj.setImportErrorMessage('No value found with key  err_import_srvc')
	}
	
	def"handleImportRegistry. This TC is when preImportRegistry method returns false when registryId is not valid"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("23*2322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getRegistryId().equals("23*2322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			1 * testObj.logError('giftregistry_1051: GiftRegistry invalid registry ID from preImportRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_forgot_pass_reg_exception', 'en_us', null, null)
			1 * testObj.setImportErrorMessage('err_forgot_pass_reg_exception')
			1 * testObj.logDebug('siteId: true', null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: importRegistry', null)
	}
	
	def"handleImportRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "err_import"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2323233")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2323233")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			1 * testObj.setImportErrorMessage('err_import')
			1 * testObj.logDebug('adding form exception: err_import_srvc: err_import', null)
			1 * testObj.logError('giftregistry_1052: GiftRegistry error in importing registry from handleImportRegistry of GiftRegistryFormHandler', _)
	}
	
	def"handleImportRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyImportRegistry()
		
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2323233")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2323233")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			1 * testObj.setImportErrorMessage('No value found with key  err_import_srvc')
			1 * testObj.logDebug('adding form exception: err_import_srvc: No value found with key  err_import_srvc', null)
			1 * testObj.logError('giftregistry_10106: BBBBusinessException from handleImportRegistry of GiftRegistryFormHandler', _)
	}
	
	def"handleImportRegistry. This TC is when RepositoryException thrown"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			RegistryVO registryVOMock = new RegistryVO()
			profileMock.isTransient() >> FALSE
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock,importedAsReg:TRUE)
			1 * giftRegistryManagerMock.importRegistry(profileMock, registryVOMock) >> registryResVOMock
			1 * giftRegistryManagerMock.linkRegistry(registryVOMock, TRUE) >> {throw new RepositoryException("Mock for RepositoryException")}
			
			2 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.REPO_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "err_import_reg"
			
			//setRegistryVO Private Method Coverage
			testObj.setRegistryVO(registryVOMock)
			testObj.setRegistryId("2232322")
			testObj.setRegistryPassword("password")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			profileMock.getRepositoryId() >> "p12345"
			profileMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbbcustomer@bbb.com"
			profileMock.getPropertyValue(BBBCoreConstants.FIRST_NAME) >> "John"
			profileMock.getPropertyValue(BBBCoreConstants.LAST_NAME) >> "Kennady"
			profileMock.getPropertyValue(BBBCoreConstants.PHONE_NUM) >> "9898989898"
			profileMock.getPropertyValue(BBBCoreConstants.MOBILE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			
			1 * testObj.preImportRegistry(requestMock, responseMock) >> TRUE
			testObj.setImportEventDate("1/1/2017")
			testObj.setImportEventType("BirthDay")
		
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getRegistryId().equals("2232322")
			registryVOMock.getPassword().equals("password")
			registryVOMock.getRegistrantVO().getProfileId().equals("p12345")
			registryVOMock.getRegistrantVO().getEmail().equals("bbbcustomer@bbb.com")
			registryVOMock.getRegistrantVO().getFirstName().equals("John")
			registryVOMock.getRegistrantVO().getLastName().equals("Kennady")
			registryVOMock.getRegistrantVO().getPrimaryPhone().equals("9898989898")
			registryVOMock.getRegistrantVO().getCellPhone().equals("8989898989")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("importRegistry")
			registryVOMock.getEvent().getEventDate().equals("1/1/2017")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("BirthDay")
			1 * testObj.setImportErrorMessage('err_import_reg')
			1 * testObj.logDebug('adding form exception: err_regsearch_repo_exception: err_import_reg', null)
			1 * testObj.logError('giftregistry_10105: RepositoryException from handleImportRegistry of GiftRegistryFormHandler', _)
	}
	
	def"handleImportRegistry. This TC is when isTransient is true"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(FALSE)
			profileMock.isTransient() >> TRUE
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			sessionBeanMock.getImportRegistryRedirectUrl().equals("/store/giftregistry/my_registries.jsp")
			testObj.getImportURL().equals("/store/account/login.jsp")
	}
	
	def"handleImportRegistry. This TC is when CheckForValidSession is true and isvalidSession is false"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> FALSE
			profileMock.isTransient() >> FALSE
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			sessionBeanMock.getImportRegistryRedirectUrl().equals("/store/giftregistry/my_registries.jsp")
			testObj.getImportURL().equals("/store/account/login.jsp")
			1 * testObj.logError('giftregistry_1059: Session expired exception from handleImportRegistry of GiftRegistryFormhandler', null)
			1 * testObj.logDebug('adding form exception: err_session_exp_msg: err_session_exp_msg', null)
	}
	
	
	def"handleImportRegistry. This TC is when isTransient is true and CheckForValidSession, isValidSession are true"(){
		given:
			this.spyImportRegistry()
			
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> ""
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCheckForValidSession(TRUE)
			testObj.isValidSession(requestMock) >> TRUE
			profileMock.isTransient() >> TRUE
			
		when:
			boolean results = testObj.handleImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			sessionBeanMock.getImportRegistryRedirectUrl().equals("/store/giftregistry/my_registries.jsp")
			testObj.getImportURL().equals("/store/account/login.jsp")
	}

	private spyImportRegistry() {
		testObj = Spy()
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setSiteContext(siteContextMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setImportRegServiceName("importRegistry")
		testObj.setLoggingDebug(TRUE)
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
	}
	
	/* *************************************************************************************************************************************
	 * handleImportRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/

	/* *************************************************************************************************************************************
	 * preImportRegistry Method STARTS
	 *
	 * Signature : public boolean preImportRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preImportRegistry. This TC is when registryPassword is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryPassword("")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.preImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.setImportErrorMessage('err_import_reg_empty_pass')
			1 * testObj.logDebug('adding form exception: err_import_reg_empty_pass', null)
			1 * testObj.logError('giftregistry_1051: GiftRegistry empty password from preImportRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_import_reg_empty_pass', 'en_us', null, null)
	}
	
	def"preImportRegistry. This TC is when importEventDate is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryPassword("123456")
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setImportEventDate("")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.preImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.setImportErrorMessage('ERR_INVALID_EVENT_DATE_IMPORT_REGISTRY')
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.logError('giftregistry_1051: GiftRegistry invalid import event date from preImportRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('ERR_INVALID_EVENT_DATE_IMPORT_REGISTRY', 'en_us', null, null)
	}
	
	def"preImportRegistry. This TC is when importEventType is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryPassword("123456")
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setImportEventDate("12/01/2017")
			testObj.setImportEventType("")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.preImportRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.setImportErrorMessage('ERR_INVALID_EVENT_TYPE_IMPORT_REGISTRY')
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.logError('giftregistry_1051: GiftRegistry invalid import event type from preImportRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('ERR_INVALID_EVENT_TYPE_IMPORT_REGISTRY', 'en_us', null, null)
	}
	
	def"preImportRegistry. This TC is when all parameters are not empty"(){
		given:
			testObj.setRegistryPassword("123456")
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setImportEventDate("12/01/2017")
			testObj.setImportEventType("Wedding")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.preImportRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	/* *************************************************************************************************************************************
	 * preImportRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleEmailRegistry Method STARTS
	 *
	 * Signature : public boolean handleEmailRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleEmailRegistry. This TC is when isValidEmailAddress is false and senderEmailAddress is valid"(){
		given:
			this.spyEmailRegistry()
			testObj.setValidatedCaptcha(TRUE)
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setSuccessQueryParam("/cart")
			testObj.setErrorQueryParam("/account/myaccount.jsp")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com;rajangmail.com"]
			emailHolderMock.getCcFlag() >> TRUE
			requestMock.getLocale() >> new Locale("en_US")
			PrintWriter printWriterMock = Mock()
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_js_email_extended_multiple",requestMock.getLocale().getLanguage(), null, null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp?/cart', '/store/checkout/singlePage/ajax/guest_json.jsp?/cart?/account/myaccount.jsp', 
				requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(captchaMock.getAnswer())
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('john Kennedy@gmail.com;rajangmail.com')
			1 * printWriterMock.print('{"errorMessages":"Exception Occurred","error":"general"}')
			1 * testObj.logDebug('Json String {"errorMessages":"Exception Occurred","error":"general"}', null)
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailRegistry. This TC is when isValidEmailAddress is true and senderEmailAddress is valid"(){
		given:
			this.spyEmailRegistry()
			testObj.setValidatedCaptcha(TRUE)
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setSuccessQueryParam("")
			testObj.setErrorQueryParam("")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> TRUE
			1 * lblTxtTemplateManagerMock.getPageTextArea("txt_registry_email_sent_msg", requestMock.getLocale().getLanguage(), ["recipientEmail":"johnKennedy@gmail.com"], null) >> "Success Message"
			responseMock.getWriter() >> printWriterMock
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp', requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			2 * testObj.setRecipientEmail('johnKennedy@gmail.com')
			1 * printWriterMock.print('{"success":"Success Message"}')
			1 * testObj.logDebug('Json String {"success":"Success Message"}', null)
			1 * testObj.setSuccessURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.setErrorURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.logDebug('SessionId = 123456', null)
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailRegistry. This TC is when isValidEmailAddress is true and senderEmailAddress is not valid"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(FALSE)
			testObj.setFromPage("")
			emailHolderMock.getValues() >> ["senderEmail":"customercarebbb.com","recipientEmail":"john Kennedy@gmail.com","messageCC":"message"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_js_email_extended_multiple",requestMock.getLocale().getLanguage(), null, null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect('', '', requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(captchaMock.getAnswer())
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('johnKennedy@gmail.com')
			1 * printWriterMock.print('{"errorMessages":"Exception Occurred","error":"general"}')
			1 * testObj.logDebug('Json String {"errorMessages":"Exception Occurred","error":"general"}', null)
			1 * testObj.setSuccessURL('')
			1 * testObj.setErrorURL('')
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailRegistry. This TC is when isValidEmailAddress is false and senderEmailAddress is not valid"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(FALSE)
			testObj.setFromPage("")
			emailHolderMock.getValues() >> ["senderEmail":"customercarebbb.com","recipientEmail":"","messageCC":"message"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_js_email_extended_multiple",requestMock.getLocale().getLanguage(), null, null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect('', '', requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(captchaMock.getAnswer())
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('')
			1 * printWriterMock.print('{"errorMessages":"Exception Occurred","error":"general"}')
			1 * testObj.logDebug('Json String {"errorMessages":"Exception Occurred","error":"general"}', null)
			1 * testObj.setSuccessURL('')
			1 * testObj.setErrorURL('')
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailRegistry. This TC is when isEmailSuccess is false"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(TRUE)
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setSuccessQueryParam("")
			testObj.setErrorQueryParam("")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> FALSE
			1 * lblTxtTemplateManagerMock.getErrMsg("err_email_internal_error", requestMock.getLocale().getLanguage(), null, null) >> "Internal Error"
			
			responseMock.getWriter() >> printWriterMock
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp', requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('john Kennedy@gmail.com')
			1 * printWriterMock.print('{"errorMessages":"Internal Error","error":"server"}')
			1 * testObj.logDebug('Json String {"errorMessages":"Internal Error","error":"server"}', null)
			1 * testObj.setSuccessURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.setErrorURL('/store/checkout/singlePage/ajax/guest_json.jsp')
			1 * testObj.logDebug('SessionId = 123456', null)
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sending_exception', 'en_us', null, null)
	}
	
	def"handleEmailRegistry. This TC is when captcha is incorrect"(){
		given:
			this.spyEmailRegistry()
			testObj.setValidatedCaptcha(TRUE)
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setSuccessQueryParam("/cart")
			testObj.setErrorQueryParam("/account/myaccount.jsp")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com;rajangmail.com"]
			emailHolderMock.getCcFlag() >> TRUE
			requestMock.getLocale() >> new Locale("en_US")
			PrintWriter printWriterMock = Mock()
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_email_incorrect_captcha", requestMock.getLocale().getLanguage(), null, null) >> "Error Occurred"
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp?/cart', '/store/checkout/singlePage/ajax/guest_json.jsp?/cart?/account/myaccount.jsp',
				requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer("KILLA")
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * printWriterMock.print('{"errorMessages":"Error Occurred","error":"general"}')
			1 * testObj.logDebug('Json String {"errorMessages":"Error Occurred","error":"general"}', null)
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	
	def"handleEmailRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(TRUE)
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setSuccessQueryParam("")
			testObj.setErrorQueryParam("")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			responseMock.getWriter() >> printWriterMock
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('giftregistry_1097: BBBSystemException from handleEmailRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_email_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleEmailRegistry. This TC is when JSONException thrown"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(TRUE)
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setSuccessQueryParam("")
			testObj.setErrorQueryParam("")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> {throw new JSONException("Mock for JSONException")}
			responseMock.getWriter() >> printWriterMock
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('giftregistry_1096: JSONException from handleEmailRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_email_sending_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sending_exception', 'en_us', null, null)
			
	}

	private spyEmailRegistry() {
		testObj = Spy()
		testObj.setEmailHolder(emailHolderMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setLoggingDebug(TRUE)
		testObj.setSiteContext(siteContextMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setGiftRegEmailInfo(giftRegEmailInfoMock)
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock	
	}
	
	
	private Captcha buildCaptcha() {
		Captcha captcha = new Captcha.Builder(100, 200).addText().addBackground(new GradiatedBackgroundProducer()).gimp()
				.addNoise()
				.addBorder()
				.build()
				
		return captcha
	}
	
	/* *************************************************************************************************************************************
	 * handleEmailRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleEmailMxRegistry Method STARTS
	 *
	 * Signature : public boolean handleEmailMxRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleEmailMxRegistry. This TC is when isValidEmailAddress is false and senderEmailAddress is valid"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setSuccessURL("/store/checkout/singlePage/ajax/guest_json.jsp")
			testObj.setErrorURL("/store/checkout/singlePage/ajax/guest_json.jsp")
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com;rajangmail.com"]
			emailHolderMock.getCcFlag() >> TRUE
			requestMock.getLocale() >> new Locale("en_US")
			PrintWriter printWriterMock = Mock()
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_js_email_extended_multiple",requestMock.getLocale().getLanguage(), null, null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp', requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(captchaMock.getAnswer())
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * testObj.setRecipientEmail('john Kennedy@gmail.com;rajangmail.com')
			1 * printWriterMock.print('{"errorMessages":"Exception Occurred","error":"general"}')
			1 * printWriterMock.close()
			1 * requestMock.setCharacterEncoding('UTF-8')
			1 * printWriterMock.flush()
	}
	
	def"handleEmailMxRegistry. This TC is when isValidEmailAddress is true and senderEmailAddress is valid"(){
		given:
			this.spyEmailRegistry()

			testObj.setValidatedCaptcha(TRUE)
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailMxRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> TRUE
			1 * lblTxtTemplateManagerMock.getPageTextArea("txt_registry_email_sent_msg_mx", requestMock.getLocale().getLanguage(), ["recipientEmail":"johnKennedy@gmail.com"], null) >> "Success Message"
			responseMock.getWriter() >> printWriterMock
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			2 * testObj.setRecipientEmail('johnKennedy@gmail.com')
			1 * printWriterMock.print('{"success":"Success Message"}')
			1 * testObj.logDebug('SessionId = 123456', null)
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailMxRegistry. This TC is when isValidEmailAddress is true and senderEmailAddress is not valid"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(FALSE)
			emailHolderMock.getValues() >> ["senderEmail":"customercarebbb.com","recipientEmail":"john Kennedy@gmail.com","messageCC":"message"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_js_email_extended_multiple",requestMock.getLocale().getLanguage(), null, null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(captchaMock.getAnswer())
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('johnKennedy@gmail.com')
			1 * printWriterMock.print('{"errorMessages":"Exception Occurred","error":"general"}')
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailMxRegistry. This TC is when isValidEmailAddress is false and senderEmailAddress is not valid"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(FALSE)
			emailHolderMock.getValues() >> ["senderEmail":"customercarebbb.com","recipientEmail":"","messageCC":"message"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_js_email_extended_multiple",requestMock.getLocale().getLanguage(), null, null) >> "Exception Occurred"
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(captchaMock.getAnswer())
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('')
			1 * printWriterMock.print('{"errorMessages":"Exception Occurred","error":"general"}')
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	def"handleEmailMxRegistry. This TC is when isEmailSuccess is false"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(TRUE)
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailMxRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> FALSE
			1 * lblTxtTemplateManagerMock.getErrMsg("err_email_internal_error", requestMock.getLocale().getLanguage(), null, null) >> "Internal Error"
			
			responseMock.getWriter() >> printWriterMock
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.setRecipientEmail('john Kennedy@gmail.com')
			1 * printWriterMock.print('{"errorMessages":"Internal Error","error":"server"}')
			1 * testObj.logDebug('SessionId = 123456', null)
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sending_exception', 'en_us', null, null)
	}
	
	def"handleEmailMxRegistry. This TC is when captcha is incorrect"(){
		given:
			this.spyEmailRegistry()
			testObj.setValidatedCaptcha(TRUE)
			
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com;rajangmail.com"]
			emailHolderMock.getCcFlag() >> TRUE
			requestMock.getLocale() >> new Locale("en_US")
			PrintWriter printWriterMock = Mock()
			responseMock.getWriter() >> printWriterMock
			1 * lblTxtTemplateManagerMock.getErrMsg("err_email_incorrect_captcha", requestMock.getLocale().getLanguage(), null, null) >> "Error Occurred"
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer("KILLA")
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * printWriterMock.print('{"errorMessages":"Error Occurred","error":"general"}')
			1 * printWriterMock.flush()
			1 * printWriterMock.close()
	}
	
	
	def"handleEmailMxRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(TRUE)
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailMxRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			responseMock.getWriter() >> printWriterMock
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.logError('giftregistry_1097: BBBSystemException from handleEmailMxRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_email_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleEmailMxRegistry. This TC is when JSONException thrown"(){
		given:
			this.spyEmailRegistry()
			
			testObj.setValidatedCaptcha(TRUE)
			emailHolderMock.getValues() >> ["senderEmail":"customercare@bbb.com","recipientEmail":"john Kennedy@gmail.com"]
			emailHolderMock.getCcFlag() >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			1 * giftRegistryManagerMock.sendEmailMxRegistry("BedBathUS", emailHolderMock.getValues(), giftRegEmailInfoMock) >> {throw new JSONException("Mock for JSONException")}
			responseMock.getWriter() >> printWriterMock
			
			//validateCaptcha Private Method Coverage
			Captcha captchaMock = buildCaptcha()
			sessionBeanMock.setCaptcha(captchaMock)
			testObj.setCaptchaAnswer(null)
			sessionMock.getId() >> "123456"
			
		when:
			boolean results = testObj.handleEmailMxRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * responseMock.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON)
			1 * requestMock.setCharacterEncoding("UTF-8")
			1 * testObj.logError('giftregistry_1096: JSONException from handleEmailMxRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_email_sending_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sending_exception', 'en_us', null, null)
			
	}

	
	/* *************************************************************************************************************************************
	 * handleEmailMxRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleEmailGiftRegistryRecommendation Method STARTS
	 *
	 * Signature : public boolean handleEmailGiftRegistryRecommendation(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleEmailGiftRegistryRecommendation. This TC is when all values are empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setRecipientEmail("")
			testObj.setRegistryURL("")
			testObj.setRegFirstName("")
			testObj.setRegLastName("")
			testObj.setRegistryEventDate("")
			testObj.setEventType("")
			testObj.setRegistryId("")
			testObj.setRegistryName("")
			
		when:
			boolean results = testObj.handleEmailGiftRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_recipient_first_name_empty_or_null: Please enter non empty first name.', null)
			1 * testObj.logDebug('adding form exception: err_recipient_last_name_empty_or_null: Please enter non empty last name.', null)
			1 * testObj.logDebug('adding form exception: err_event_date_empty_or_null: Please enter non empty event date.', null)
			1 * testObj.logDebug('adding form exception: err_recipient_mail_empty_or_null: Please enter non empty registry id.', null)
			1 * testObj.logDebug('adding form exception: err_invalid_reg_name: Please enter valid registry name.', null)
			1 * testObj.logDebug('adding form exception: err_registry_url_empty_or_null: Please enter non empty registry url.', null)
			1 * testObj.logDebug('adding form exception: err_event_type_empty_or_null: Please enter non empty event type value', null)
			1 * testObj.logDebug('adding form exception: err_registry_id_empty_or_null: Please enter non empty registry id.', null)
	}
	
	def"handleEmailGiftRegistryRecommendation. This TC is when all parameters are not empty"(){
		given:
			testObj = Spy()
			testObj.setEmailHolder(emailHolderMock)
			
			def alphaNumericPattern = /^[a-zA-Z0-9_]*\$/
			def registryNamePattern = /^[a-zA-Z]*$/
			BBBValidationRules bbbRules = new BBBValidationRules()
			bbbRules.setRegistryNamePattern(registryNamePattern)
			bbbRules.setAlphaNumericPattern(alphaNumericPattern)
			BBBUtility.setRules(bbbRules)
			
			testObj.setLoggingDebug(TRUE)
			testObj.setRecipientEmail("john@gmail.com")
			testObj.setRegistryURL("/giftregistry/simpleReg_creation_form.jsp")
			testObj.setRegFirstName("John")
			testObj.setRegLastName("Kennedy")
			testObj.setRegistryEventDate("01/04/2017")
			testObj.setEventType("BirthDay")
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setRegistryName("JohnBirthDay")
			testObj.setMessage("Created Successfully")
			
			emailHolderMock.getValues() >> ["One":"someValue"]
			1 * testObj.handleEmailRegistryRecommendation(requestMock, responseMock) >> TRUE
			
		when:
			boolean results = testObj.handleEmailGiftRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == TRUE
			emailHolderMock.getValues().equals(["One":"someValue","recipientEmail":"john@gmail.com","registryURL":"/giftregistry/simpleReg_creation_form.jsp","pRegFirstName":"John",
				"pRegLastName":"Kennedy","message":"Created Successfully","eventType":"BirthDay","configurableType":"JohnBirthDay","registryId":'REG_summary2351211$'])
			
	}
	
	def"handleEmailGiftRegistryRecommendation. This TC is when registryId is not valid"(){
		given:
			testObj = Spy()			
			testObj.setLoggingDebug(TRUE)
			testObj.setRecipientEmail("john@gmail.com")
			testObj.setRegistryURL("/giftregistry/simpleReg_creation_form.jsp")
			testObj.setRegFirstName("John")
			testObj.setRegLastName("Kennedy")
			testObj.setRegistryEventDate("01/04/2017")
			testObj.setEventType("BirthDay")
			testObj.setRegistryId('2323*53')
			testObj.setRegistryName("JohnBirthDay")
			
		when:
			boolean results = testObj.handleEmailGiftRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_registry_id_empty_or_null: Please enter valid registry id.', null)
	}
	
	/* *************************************************************************************************************************************
	 * handleEmailGiftRegistryRecommendation Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleEmailRegistryRecommendation Method STARTS
	 *
	 * Signature : public boolean handleEmailGiftRegistryRecommendation(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleEmailRegistryRecommendation. This TC is the Happy flow of handleEmailRegistryRecommendation method"(){
		given:
			this.spyEmailRegistryRecomm()
			
			Map<String, Object> values = ["recipientEmail":"John@gmail.com,sarath@gmail.com","registryId":'REG_summary2351211$',"configurableType":"Wedding"]
			emailHolderMock.getValues() >> values
			testObj.setEventType("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setRegFirstName("John")
			testObj.setRegistryEventDate("01/10/2017")
			testObj.setEmailRecommendationSuccessURL("/account/login.jsp")
			testObj.setEmailRecommendationErrorURL("/account/login.jsp")
			
			1 * giftRegistryToolsMock.persistRecommendationToken({RegistryVO registryVO -> registryVO.primaryRegistrant.firstName == "John" && registryVO.registryId == 'REG_summary2351211$' &&
				registryVO.event.eventDate == "01/10/2017" && registryVO.registryType.registryTypeName == "" && registryVO.siteId == "BedBathUS"	}, _) >> ["one": repositoryItemMock]
			
			1 * giftRegistryManagerMock.sendEmailRegistryRecommendation("BedBathUS", values, testObj.getGiftRegEmailInfo(), ["one": repositoryItemMock]) >> TRUE
			
			1 * testObj.checkFormRedirect(testObj.getEmailRecommendationSuccessURL(), testObj.getEmailRecommendationErrorURL(), requestMock, responseMock) >> TRUE
				
		when:
			boolean results = testObj.handleEmailRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE, BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, "Wedding")
			1 * testObj.logDebug('GiftRegistryFormHandler.handleEmailRegistryRecommendation() isEmailSuccess=true , email sent to John@gmail.com,sarath@gmail.com', null)
	}
	
	def"handleEmailRegistryRecommendation. This TC is when email is not not valid"(){
		given:
			this.spyEmailRegistryRecomm()
			
			testObj.setRegErrorMap([:])
			Map<String, Object> values = ["recipientEmail":"John gmail.com","registryId":'REG_summary2351211$']
			emailHolderMock.getValues() >> values
			testObj.setEventType("Wedding")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_INVALID_EMAIL_ID, requestMock.getLocale().getLanguage(), null, null) >> "Error Occurred when sending email"
			testObj.setEmailRecommendationSuccessURL("/account/login.jsp")
			testObj.setEmailRecommendationErrorURL("/account/login.jsp")
			
			1 * testObj.checkFormRedirect(testObj.getEmailRecommendationSuccessURL(), testObj.getEmailRecommendationErrorURL(), requestMock, responseMock) >> TRUE
				
		when:
			boolean results = testObj.handleEmailRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegErrorMap().equals(["emailError":"Error Occurred when sending email"])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE, BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, "Wedding")
			1 * testObj.logDebug('adding form exception: err_email_sys_exception: Error Occurred when sending email', null)
			1 * testObj.setRecipientEmail('John gmail.com')
			1 * testObj.logDebug('GiftRegistryFormHandler.handleEmailRegistryRecommendation() invalid emailId: Johngmail.com', null)
	}
	
	def"handleEmailRegistryRecommendation. This TC is when registryEmail is empty"(){
		given:
			Map<String, Object> values = ["recipientEmail":"","registryId":'REG_summary2351211$']
			emailHolderMock.getValues() >> values
			testObj.setEventType("Wedding")
				
		when:
			boolean results = testObj.handleEmailRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE, BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, "Wedding")
	}
	
	def"handleEmailRegistryRecommendation. This TC is when isEmailSuccess is false"(){
		given:
			this.spyEmailRegistryRecomm()
			
			testObj.setRegErrorMap([:])
			Map<String, Object> values = ["recipientEmail":"John@gmail.com","registryId":'REG_summary2351211$',"configurableType":"Wedding"]
			emailHolderMock.getValues() >> values
			testObj.setEventType("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setRegFirstName("John")
			testObj.setRegistryEventDate("01/10/2017")
			testObj.setEmailRecommendationSuccessURL("/account/login.jsp")
			testObj.setEmailRecommendationErrorURL("/account/login.jsp")
			
			1 * giftRegistryToolsMock.persistRecommendationToken({RegistryVO registryVO -> registryVO.primaryRegistrant.firstName == "John" && registryVO.registryId == 'REG_summary2351211$' &&
				registryVO.event.eventDate == "01/10/2017" && registryVO.registryType.registryTypeName == "" && registryVO.siteId == "BedBathUS"	}, _) >> ["one": repositoryItemMock]
			
			1 * giftRegistryManagerMock.sendEmailRegistryRecommendation("BedBathUS", values, testObj.getGiftRegEmailInfo(), ["one": repositoryItemMock]) >> FALSE
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "Error Occurred while sending email"
			
			1 * testObj.checkFormRedirect(testObj.getEmailRecommendationSuccessURL(), testObj.getEmailRecommendationErrorURL(), requestMock, responseMock) >> TRUE
				
		when:
			boolean results = testObj.handleEmailRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegErrorMap().equals(["emailError":"Error Occurred while sending email"])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE, BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, "Wedding")
			1 * testObj.logDebug('GiftRegistryFormHandler.handleEmailRegistryRecommendation() isEmailSuccess=false , there seems to be some problem with SMTP', null)
			1 * testObj.logDebug('adding form exception: err_email_sys_exception: Error Occurred while sending email', null)
	}
	
	def"handleEmailRegistryRecommendation. This TC is when BBBSystemException thrown"(){
		given:
			this.spyEmailRegistryRecomm()
			
			testObj.setRegErrorMap([:])
			Map<String, Object> values = ["recipientEmail":"John@gmail.com","registryId":'REG_summary2351211$',"configurableType":"Wedding"]
			emailHolderMock.getValues() >> values
			testObj.setEventType("")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setRegFirstName("John")
			testObj.setRegistryEventDate("01/10/2017")
			testObj.setEmailRecommendationSuccessURL("/account/login.jsp")
			testObj.setEmailRecommendationErrorURL("/account/login.jsp")
			
			1 * giftRegistryToolsMock.persistRecommendationToken({RegistryVO registryVO -> registryVO.primaryRegistrant.firstName == "John" && registryVO.registryId == 'REG_summary2351211$' &&
				registryVO.event.eventDate == "01/10/2017" && registryVO.registryType.registryTypeName == "" && registryVO.siteId == "BedBathUS" }, _) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION, requestMock.getLocale().getLanguage(), null, null) >> "Error Occurred while sending email"
			
			1 * testObj.checkFormRedirect(testObj.getEmailRecommendationSuccessURL(), testObj.getEmailRecommendationErrorURL(), requestMock, responseMock) >> TRUE
				
		when:
			boolean results = testObj.handleEmailRegistryRecommendation(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegErrorMap().equals(["emailError":"Error Occurred while sending email"])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE, BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, "Wedding")
			1 * testObj.logError('giftregistry_1097: BBBSystemException from handleEmailRegistryRecommendation of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_email_sys_exception: Error Occurred while sending email', null)
	}

	private spyEmailRegistryRecomm() {
		testObj = Spy()
		testObj.setLoggingDebug(TRUE)
		testObj.setEmailHolder(emailHolderMock)
		testObj.setGiftRegistryTools(giftRegistryToolsMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObj.setSiteContext(siteContextMock)
	}
	
	/* *************************************************************************************************************************************
	 * handleEmailRegistryRecommendation Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleEmailGiftRegistry Method STARTS
	 *
	 * Signature : public boolean handleEmailGiftRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleEmailGiftRegistry. This TC is the Happy flow of handleEmailGiftRegistry Method"(){
		given:
			testObj = Spy()
			testObj.setEmailHolder(emailHolderMock)
			testObj.setLoggingDebug(TRUE)
			
			testObj.setSenderEmail("customerSupport@bbb.com")
			testObj.setRecipientEmail("john@gmail.com")
			testObj.setEventType("Wedding")
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setRegistryName("JohnWedding")
			testObj.setDaysToGo(5l)
			testObj.setRegistryURL("/giftregistry/simpleReg_creation_form.jsp")
			testObj.setMessage("Message")
			testObj.setCcFlag(FALSE)
			testObj.setRegFirstName("John")
			testObj.setRegLastName("Kennady")
			testObj.setCoRegFirstName("Sarath")
			testObj.setCoRegLastName("Raj")
			testObj.setRegistryEventDate("12/01/2017")
			testObj.setSubject("Celebrations")
			testObj.setTitle("Wishes")
			testObj.setDateLabel("12thJan")
			emailHolderMock.getValues() >> [:]
			1 * testObj.handleEmailRegistry(requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleEmailGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.isValidatedCaptcha().equals(FALSE)
			emailHolderMock.getValues().equals(["registryURL":"/giftregistry/simpleReg_creation_form.jsp","recipientEmail":"john@gmail.com","message":"Message","senderEmail":"customerSupport@bbb.com",
				"eventType":"Wedding","ccFlag":FALSE,"pRegFirstName":"John","pRegLastName":"Kennady","coRegFirstName":"Sarath","coRegLastName":"Raj","eventDate":"12/01/2017",
				"daysToGo":5l,"configurableType":"JohnWedding","registryId":'REG_summary2351211$',"subject":"Celebrations","title":"Wishes","dateLabel":"<strong>5</strong>&nbsp;12thJan"])
			
	}
	
	def"handleEmailGiftRegistry. This TC is when all parameters are empty"(){
		given:
			testObj = Spy()
			testObj.setEmailHolder(emailHolderMock)
			testObj.setLoggingDebug(TRUE)
			
			testObj.setSenderEmail("")
			testObj.setRecipientEmail("")
			testObj.setEventType("")
			testObj.setRegistryId("")
			testObj.setRegistryName("")
			testObj.setDaysToGo(-1)
			responseMock.getWriter() >> printWriterMock
		
		when:
			boolean results = testObj.handleEmailGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.isValidatedCaptcha().equals(FALSE)
			1 * testObj.logDebug('adding form exception: err_invalid_reg_name: Please enter valid registry name.', null)
			1 * testObj.logDebug('adding form exception: err_sender_mail_empty_or_null: Please enter non empty sender email address', null)
			1 * testObj.logDebug('adding form exception: err_days_to_go_invalid: Please enter valid value for days to go field', null)
			1 * testObj.logDebug('adding form exception: err_registry_id_empty_or_null: Please enter non empty registry id.', null)
			1 * testObj.logDebug('adding form exception: err_recipient_mail_empty_or_null: Please enter non empty recipient email address', null)
			1 * testObj.logDebug('adding form exception: err_event_type_empty_or_null: Please enter non empty event type value', null)
			1 * printWriterMock.print('{"errorMessages":"Please enter valid input values"}')
			1 * printWriterMock.close()
			1 * printWriterMock.flush()
			
	}
	
	def"handleEmailGiftRegistry. This TC is when registryId is not valid"(){
		given:
			testObj = Spy()
			testObj.setEmailHolder(emailHolderMock)
			testObj.setLoggingDebug(TRUE)
			
			testObj.setSenderEmail("customerSupport@bbb.com")
			testObj.setRecipientEmail("john@gmail.com")
			testObj.setEventType("Wedding")
			testObj.setRegistryId("232353*23")
			testObj.setRegistryName("JohnWedding")
			testObj.setDaysToGo(5l)
			responseMock.getWriter() >> printWriterMock
		
		when:
			boolean results = testObj.handleEmailGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.isValidatedCaptcha().equals(FALSE)
			1 * testObj.logDebug('adding form exception: err_registry_id_empty_or_null: Please enter valid registry id.', null)
			1 * printWriterMock.print('{"errorMessages":"Please enter valid input values"}')
			1 * printWriterMock.close()
			1 * printWriterMock.flush()
	}
	
	/* *************************************************************************************************************************************
	 * handleEmailGiftRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleUpdateRegistryItems Method STARTS
	 *
	 * Signature : public boolean handleUpdateRegistryItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleUpdateRegistryItems. This TC is when validateUpdateRegistryItem method returns false"(){
		given:
			testObj = Spy()
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setSkuId("sku12345")
			testObj.setUpdateRegistryId('REG_summary2351211$')
			testObj.setProductId("prod12345")
			testObj.setRowId("12421")
			testObj.setItemTypes("others")
			testObj.setRegItemOldQty("2")
			testObj.setPurchasedQuantity("5")
			1 * testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistryItems(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	def"handleUpdateRegistryItems. This TC is when validateUpdateRegistryItem method returns true and getUpdateRegistryId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setSkuId("")
			testObj.setUpdateRegistryId("")
			testObj.setProductId("")
			testObj.setRowId("")
		
		when:
			boolean results = testObj.handleUpdateRegistryItems(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_null_or_empty_row_id: Please provide a valid non-empty row id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_product_id: Please provide a valid non-empty product id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_sku_id: Please provide a valid non-empty sku id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_registry_id: Please provide a valid non-empty registry id in the input', null)
			1 * testObj.logError('Null or empty registry id ', null)
			1 * testObj.logError('Null or empty row id ', null)
			1 * testObj.logError('Null or empty product id ', null)
			1 * testObj.logError('Null or empty sku id ', null)
	}
	
	def"handleUpdateRegistryItems. This TC is when validateUpdateRegistryItem method returns true and getUpdateRegistryId is not valid"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setSkuId("")
			testObj.setUpdateRegistryId("25552*523")
			testObj.setProductId("")
			testObj.setRowId("")
		
		when:
			boolean results = testObj.handleUpdateRegistryItems(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_null_or_empty_row_id: Please provide a valid non-empty row id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_product_id: Please provide a valid non-empty product id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_sku_id: Please provide a valid non-empty sku id in the input', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_registry_id: Please provide a valid non-empty registry id in the input', null)
			1 * testObj.logError('Null or empty registry id ', null)
			1 * testObj.logError('Null or empty row id ', null)
			1 * testObj.logError('Null or empty product id ', null)
			1 * testObj.logError('Null or empty sku id ', null)
	}
	
	/* *************************************************************************************************************************************
	 * handleUpdateRegistryItems Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleAnnouncementCardCount Method STARTS
	 *
	 * Signature : public boolean handleAnnouncementCardCount(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleAnnouncementCardCount. This TC is the Happy flow of handleAnnouncementCardCount method"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$')
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean(responseHolder:registryVOMock) 
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			requestMock.getParameter("registryAnnouncement") >> "3"
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			SetAnnouncementCardResVO setAnnouncementCardResVOMock = new SetAnnouncementCardResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.assignAnnouncementCardCount(registryVOMock) >> setAnnouncementCardResVOMock
		
		when:
			boolean results = testObj.handleAnnouncementCardCount(requestMock, responseMock)
			
		then:
			results == FALSE
			registryVOMock.getNumRegAnnouncementCards().equals(3)
	}
	
	def"handleAnnouncementCardCount. This TC is when errorId is 900"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$')
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean(responseHolder:registryVOMock)
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			requestMock.getParameter("registryAnnouncement") >> null
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorDisplay",errorId:900)
			SetAnnouncementCardResVO setAnnouncementCardResVOMock = new SetAnnouncementCardResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.assignAnnouncementCardCount(registryVOMock) >> setAnnouncementCardResVOMock
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleAnnouncementCardCount(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('giftregistry_1089: Fatal error from handleAnnouncementCardCount of GiftRegistryFormHandler | webservice error code=900', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: null', null)
			1 * testObj.logDebug('siteId: true', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.900 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
	}
	
	def"handleAnnouncementCardCount. This TC is when errorId is 901"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			
			RegistryVO registryVOMock = new RegistryVO(registryId:'1253*235')
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean(responseHolder:registryVOMock)
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			requestMock.getParameter("registryAnnouncement") >> null
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorDisplay",errorId:901)
			SetAnnouncementCardResVO setAnnouncementCardResVOMock = new SetAnnouncementCardResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.assignAnnouncementCardCount(registryVOMock) >> setAnnouncementCardResVOMock
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleAnnouncementCardCount(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('giftregistry_1090: Either user token or site flag invalid from handleAnnouncementCardCount of GiftRegistryFormHandler | webservice error code=901', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: null', null)
			1 * testObj.logDebug('siteId: true', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.901 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
			
	}
	
	def"handleAnnouncementCardCount. This TC is when errorId is 902"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$')
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean(responseHolder:registryVOMock)
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			requestMock.getParameter("registryAnnouncement") >> null
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorDisplay",errorId:902)
			SetAnnouncementCardResVO setAnnouncementCardResVOMock = new SetAnnouncementCardResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.assignAnnouncementCardCount(registryVOMock) >> setAnnouncementCardResVOMock
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleAnnouncementCardCount(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from handleAnnouncementCardCount() of GiftRegistryFormHandler | webservice error code=902', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_invalid_input_format', 'en_us', null, null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('ServiceName: null', null)
			1 * testObj.logDebug('siteId: true', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.902 exception=null', null)
			
	}
	
	def"handleAnnouncementCardCount. This TC is when errorDisplayMessage is empty"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$')
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean(responseHolder:registryVOMock)
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			requestMock.getParameter("registryAnnouncement") >> null
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:902)
			SetAnnouncementCardResVO setAnnouncementCardResVOMock = new SetAnnouncementCardResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.assignAnnouncementCardCount(registryVOMock) >> setAnnouncementCardResVOMock
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleAnnouncementCardCount(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.902 exception=null', null)
			1 * testObj.logDebug('ServiceName: null', null)
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('siteId: true', null)
	}
	
	def"handleAnnouncementCardCount. This TC is when BBBBusinessException is thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setLoggingDebug(TRUE)
			requestMock.getLocale() >> new Locale("en_US")
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setFromPage("")
			
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$')
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean(responseHolder:registryVOMock)
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			requestMock.getParameter("registryAnnouncement") >> null
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleAnnouncementCardCount(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleAnnoucementCardCount of GiftRegistryformHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
	}
	
	/* *************************************************************************************************************************************
	 * handleAnnouncementCardCount Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleUpdateRegistry Method STARTS
	 *
	 * Signature : public boolean handleUpdateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleUpdateRegistry. This TC is when alternateNum is empty and DeactivateRegistry is false"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setEventType("College")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("male")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"24/01/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/01/2017",showerDate:"26/01/2017",birthDate:"10/09/1992",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Post Office",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock) 
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"",optInWeddingOrBump:"false",shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("false")
			testObj.setMakeRegistryPublic("")
			1 * testObj.preUpdateRegistry(requestMock, responseMock) >> null
			sessionBeanMock.setRegistryTypesEvent("COL")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "College"
			profileMock.getPropertyValue("id") >> "p12345"
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "College Type Desc"
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathUS") >> null
			
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean() 
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setAffiliationValues Private Method Coverage
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.REGISTRY_DEFAULT_STORE_ID) >> "2365"
			
			//setQasValues Private Method Coverage
			testObj.setRegBG("regBG")
			testObj.setCoRegBG("coRegBG")
			testObj.setContactPoBoxStatus("Y")
			testObj.setContactPoBoxFlag("P")
			testObj.setShipPoBoxStatus("Y")
			testObj.setShipPoBoxFlag("P")
			testObj.setFuturePoBoxStatus("Y")
			testObj.setFuturePoBoxFlag("P")
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': registryVOMock1,'REG_summary5261211$': registryVOMock2)
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			sessionBeanMock.getValues().put("registrySkinnyVOList",[registrySkinnyVOMock])
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock1]
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"",eventDate:"26/01/2017")
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary3351211$', "BedBathUS") >> registryResVOMock1 
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			1 * testObj.checkFormRedirect('/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?eventType=College&registryId=REG_summary2351211$', 
				'/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?registryId=REG_summary2351211$', requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("COL")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getEvent().getEventDateWS().equals("20170101")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("2365")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getEvent().getShowerDateWS().equals("20172601")
			registryVOMock.getEvent().getBirthDateWS().equals("19921009")
			registryVOMock.getShipping().getFutureShippingDateWS().equals("20172401")
			testObj.isPoBoxAddress().equals(TRUE)
			registryVOMock.getRegBG().equals("regBG")
			registryVOMock.getCoRegBG().equals("coRegBG")
			registryVOMock.getPrimaryRegistrant().getContactAddress().isQasValidated().equals(TRUE)
			registryVOMock.getPrimaryRegistrant().getContactAddress().isPoBoxAddress().equals(TRUE)
			registryVOMock.getShipping().getShippingAddress().isQasValidated().equals(TRUE)
			registryVOMock.getShipping().getShippingAddress().isPoBoxAddress().equals(TRUE)
			registryVOMock.getShipping().getFutureshippingAddress().isQasValidated().equals(TRUE)
			registryVOMock.getShipping().getFutureshippingAddress().isPoBoxAddress().equals(TRUE)
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCompany().equals("Harward")
			registryVOMock1.getEvent().getBabyGender().equals("male")
			registryVOMock1.getEvent().getGuestCount().equals("0")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get('REG_summary3351211$_REG_SUMMARY').equals(registryResVOMock1)
			registrySummaryVOMock.getEventType().equals("College")
			registryVOMock1.getRegistryType().getRegistryTypeDesc().equals("College Type Desc")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
	}
	
	def"handleUpdateRegistry. This TC is when alternateNum is empty and DeactivateRegistry and MakeRegistryPublic is true"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"24/01/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/01/2017",showerDate:"26/01/2017",birthDate:"10/09/1992",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:"true",shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("true")
			testObj.setMakeRegistryPublic("true")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * testObj.preUpdateRegistry(requestMock, responseMock) >> null
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG("")
			testObj.setCoRegBG("")
			testObj.setContactPoBoxStatus("NO")
			testObj.setContactPoBoxFlag("NO")
			testObj.setShipPoBoxStatus("NO")
			testObj.setShipPoBoxFlag("NO")
			testObj.setFuturePoBoxStatus("NO")
			testObj.setFuturePoBoxFlag("NO")
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs(null)
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			sessionBeanMock.getValues().put("registrySkinnyVOList",[registrySkinnyVOMock])
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathCanada") >> [registrySkinnyVOMock1]
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:"26/01/2017")
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary3351211$', "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("Y")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals("p254585")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getEvent().getEventDateWS().equals("20170101")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("Y")
			registryVOMock.getEvent().getShowerDateWS().equals("20170126")
			registryVOMock.getEvent().getBirthDateWS().equals("19920910")
			registryVOMock.getShipping().getFutureShippingDateWS().equals("20170124")
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("1")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get('REG_summary3351211$_REG_SUMMARY').equals(registryResVOMock1)
			sessionBeanMock.getRegistryVOs().equals(['REG_summary3351211$':registryVOMock1])
			registrySummaryVOMock.getEventDate().equals("26/01/2017")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
	}
	
	def"handleUpdateRegistry. This TC is when alternateNum is empty and DeactivateRegistry is true and MakeRegistryPublic is false"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"24012017")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("true")
			testObj.setMakeRegistryPublic("false")
			testObj.setUpdateSimplified("TRUE")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * testObj.preUpdateSimplifiedRegistry(requestMock, responseMock) >> null
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			sessionBeanMock.getValues().put("registrySkinnyVOList",[registrySkinnyVOMock])
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary3351211$', "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals("24012017")
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get('REG_summary3351211$_REG_SUMMARY').equals(registryResVOMock1)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
	}
	
	def"handleUpdateRegistry. This TC is when alternateNum is empty and DeactivateRegistry is true and MakeRegistryPublic is null"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"24-01-2017")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("true")
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("FALSE")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * testObj.preUpdatePrivateRegistry(requestMock, responseMock) >> null
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary3351211$', "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get('REG_summary3351211$_REG_SUMMARY').equals(registryResVOMock1)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
	}
	
	def"handleUpdateRegistry. This TC is when alternateNum ,UpdateSimplified are empty and DeactivateRegistry is null"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic("false")
			testObj.setUpdateSimplified("")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * testObj.preUpdatePrivateRegistry(requestMock, responseMock) >> null
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:null,registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin(null, "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
	}
	
	def"handleUpdateRegistry. This TC is when alternateNum is empty and resolveName of sessionBean is null"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:null,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic("false")
			testObj.setUpdateSimplified("")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			1 * giftRegistryManagerMock.updateRegistry(_) >> null
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> null
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.checkFormRedirect("", "", requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("0")
			testObj.getRegistryVO().equals(registryVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
	}
	
	def"handleUpdateRegistry. This TC is when coRegOwner is false and UpdateSimplified is true"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("false")
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("true")
			1 * testObj.preUpdateRegistry(requestMock, responseMock) >> null
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistrantVO primaryRegistrantMock1 = new RegistrantVO(email:"rajan@gmail.com",firstName:"Rajan",lastName:"Sridhar",primaryPhone:"9898989898",cellPhone:"9595959595")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1,primaryRegistrant:primaryRegistrantMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			//preGiftRegUpdateUser private Method Coverage
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailProperty"
			1 * propertyManagerMock.getAutoLoginPropertyName() >> "autoLoginProperty"
			1 * propertyManagerMock.getReceiveEmailPropertyName() >> "receiveMailProperty"
			testObj.setShallowProfileChanges(TRUE)
			1 * propertyManagerMock.getStatusPropertyName() >> "statusProperty"
			1 * propertyManagerMock.getFirstNamePropertyName() >> "firstNameProperty"
			1 * propertyManagerMock.getLastNamePropertyName() >> "lastNameProperty"
			1 * propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumberProperty"
			1 * propertyManagerMock.getPhoneNumberPropertyName() >> "phoneNumberProperty"
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary3351211$', "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.superHandleUpdate(requestMock, responseMock) >> null
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?eventType=WeddingEvent&registryId=REG_summary2351211$', 
				'/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?registryId=REG_summary2351211$', requestMock, responseMock) >> TRUE
			
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("1")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.setValueProperty("receiveMailProperty", "yes")
	}
	
	def"handleUpdateRegistry. This TC is when coRegOwner is false and UpdateSimplified is empty and BBBBusinessException thrown"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistrantVO primaryRegistrantMock1 = new RegistrantVO(email:"rajan@gmail.com",firstName:"Rajan",lastName:"Sridhar",primaryPhone:"",cellPhone:"8989898989")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1,primaryRegistrant:primaryRegistrantMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			testObj.setProfileTools(bbbProfileToolsMock)
			
			//preGiftRegUpdateUser private Method Coverage
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailProperty"
			1 * propertyManagerMock.getAutoLoginPropertyName() >> "autoLoginProperty"
			1 * propertyManagerMock.getReceiveEmailPropertyName() >> "receiveMailProperty"
			testObj.setShallowProfileChanges(FALSE)
			0 * propertyManagerMock.getStatusPropertyName() >> "statusProperty"
			1 * propertyManagerMock.getFirstNamePropertyName() >> "firstNameProperty"
			1 * propertyManagerMock.getLastNamePropertyName() >> "lastNameProperty"
			3 * propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumberProperty"
			0 * propertyManagerMock.getPhoneNumberPropertyName() >> "phoneNumberProperty"
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?eventType=WeddingEvent&registryId=REG_summary2351211$',
				'/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?registryId=REG_summary2351211$', requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.setValueProperty("receiveMailProperty", "yes")
			1 * testObj.getStringValueProperty('mobileNumberProperty')
			1 * requestMock.setParameter(BBBCoreConstants.IS_FROM_GIFT_REGISTRY, BBBCoreConstants.TRUE)
			1 * testObj.logError('giftregistry_20131: p12345|true|BBBBusinessException in sending EMAIL', _)
			
	}
	
	def"handleUpdateRegistry. This TC is when coRegOwner is false and UpdateSimplified is false"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("false")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:null)
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistrantVO primaryRegistrantMock1 = new RegistrantVO(email:"rajan@gmail.com",firstName:"Rajan",lastName:"Sridhar",primaryPhone:"",cellPhone:"")
			RegistryVO registryVOMock1 = new RegistryVO(event:eventVOMock1,registryId:'REG_summary3351211$',registryType:registryTypesMock1,primaryRegistrant:primaryRegistrantMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock, "shipping Address", "Future shipping Address", profileMock,
				testObj.getRegistrantAddressFromWS(), testObj.getShippingAddressFromWS(), testObj.getFutureShippingAddressFromWS()) >> registryVOMock1
			
			testObj.setProfileTools(bbbProfileToolsMock)
			
			
			//preGiftRegUpdateUser private Method Coverage
			1 * propertyManagerMock.getLoginPropertyName() >> "loginProperty"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailProperty"
			1 * propertyManagerMock.getAutoLoginPropertyName() >> "autoLoginProperty"
			1 * propertyManagerMock.getReceiveEmailPropertyName() >> "receiveMailProperty"
			testObj.setShallowProfileChanges(FALSE)
			0 * propertyManagerMock.getStatusPropertyName() >> "statusProperty"
			1 * propertyManagerMock.getFirstNamePropertyName() >> "firstNameProperty"
			1 * propertyManagerMock.getLastNamePropertyName() >> "lastNameProperty"
			1 * propertyManagerMock.getMobileNumberPropertyName() >> "mobileNumberProperty"
			0 * propertyManagerMock.getPhoneNumberPropertyName() >> "phoneNumberProperty"
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock1) >> registryResVOMock
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventType:"weddingAnniversary",eventDate:null)
			RegistryResVO registryResVOMock1 = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary3351211$', "BedBathCanada") >> registryResVOMock1
			
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> null
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?eventType=WeddingEvent&registryId=REG_summary2351211$',
				'/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?registryId=REG_summary2351211$', requestMock, responseMock) >> TRUE
			
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			testObj.getRegistryVO().equals(registryVOMock1)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock1)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.setValueProperty("receiveMailProperty", "yes")
			1 * testObj.getStringValueProperty('mobileNumberProperty')
			1 * requestMock.setParameter(BBBCoreConstants.IS_FROM_GIFT_REGISTRY, BBBCoreConstants.TRUE)
	}
	
	def"handleUpdateRegistry. This TC is when errorId is 200 in setFormExceptionsForErrors private method"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("false")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			1 * bbbCatalogToolsMock.getRegistryTypeName("wedding", "BedBathCanada") >> "Wedding Type Desc"
			testObj.setProfileTools(bbbProfileToolsMock)
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"error Message",errorId:200,errorMessage:null)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock) >> registryResVOMock
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			1 * testObj.checkFormRedirect("","" , requestMock, responseMock) >> TRUE			
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			testObj.getRegistryVO().equals(registryVOMock)
			registryVOMock.getRegistryType().getRegistryTypeDesc().equals("Wedding Type Desc")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.logDebug('adding form exception: err_gift_reg_input_field_error', null)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_input_field_error', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1086: Input field invalid from setFormExceptionsForErrors of GiftRegistryformHandler webservice error code=200', null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from setFormExceptionsForErrors of GiftRegistryformHandler', null)
	}
	
	def"handleUpdateRegistry. This TC is when errorId is 900 in setFormExceptionsForErrors Private method"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("false")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			1 * bbbCatalogToolsMock.getRegistryTypeName("wedding", "BedBathCanada") >> "Wedding Type Desc"
			testObj.setProfileTools(bbbProfileToolsMock)
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"error Message",errorId:900,errorMessage:"Error Has been occurred")
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock) >> registryResVOMock
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			1 * testObj.checkFormRedirect("","" , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			testObj.getRegistryVO().equals(registryVOMock)
			registryVOMock.getRegistryType().getRegistryTypeDesc().equals("Wedding Type Desc")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.logDebug('adding form exception: Error Has been occurred: Error Has been occurred property=null.900 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1087: Fatal error from setFormExceptionsForErrors of GiftRegistryformHandler webservice error code=900', null)
	}
	
	def"handleUpdateRegistry. This TC is when errorId is 901 in setFormExceptionsForErrors Private method"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("false")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			1 * bbbCatalogToolsMock.getRegistryTypeName("wedding", "BedBathCanada") >> "Wedding Type Desc"
			testObj.setProfileTools(bbbProfileToolsMock)
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"error Message",errorId:901,errorMessage:null)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock) >> registryResVOMock
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			1 * testObj.checkFormRedirect("","" , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			testObj.getRegistryVO().equals(registryVOMock)
			registryVOMock.getRegistryType().getRegistryTypeDesc().equals("Wedding Type Desc")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from setFormExceptionsForErrors of GiftRegistryformHandler', null)
			1 * testObj.logError('giftregistry_1088: Either user token or site flag invalid from setFormExceptionsForErrors of GiftRegistryformHandler webservice error code=901', null)
	}
	
	def"handleUpdateRegistry. This TC is when errorId is 902 in setFormExceptionsForErrors Private method"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("false")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			1 * bbbCatalogToolsMock.getRegistryTypeName("wedding", "BedBathCanada") >> "Wedding Type Desc"
			testObj.setProfileTools(bbbProfileToolsMock)
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"error Message",errorId:902,errorMessage:null)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock) >> registryResVOMock
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			1 * testObj.checkFormRedirect("","" , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			testObj.getRegistryVO().equals(registryVOMock)
			registryVOMock.getRegistryType().getRegistryTypeDesc().equals("Wedding Type Desc")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format', null)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_invalid_input_format', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from Create/UpdateRegistry of GiftRegistryFormHandler | webservice error code=902', null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from setFormExceptionsForErrors of GiftRegistryformHandler', null)
	}
	
	def"handleUpdateRegistry. This TC is when errorDisplayMessage is empty in setFormExceptionsForErrors Private method"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic(null)
			testObj.setUpdateSimplified("false")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:null)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:shippingVOMock,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			profileMock.getPropertyValue("id") >> "p12345"
			1 * bbbCatalogToolsMock.getRegistryTypeName("wedding", "BedBathCanada") >> "Wedding Type Desc"
			testObj.setProfileTools(bbbProfileToolsMock)
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:902,errorMessage:null)
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.updateRegistry(registryVOMock) >> registryResVOMock
			requestMock.getLocale() >> new Locale("en_US")
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			1 * testObj.checkFormRedirect("","" , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			registryVOMock.getShipping().getFutureShippingDateWS().equals(null)
			testObj.isPoBoxAddress().equals(FALSE)
			testObj.getRegistryVO().equals(registryVOMock)
			registryVOMock.getRegistryType().getRegistryTypeDesc().equals("Wedding Type Desc")
			giftRegSessionBeanMock.getResponseHolder().equals(registryVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("updated")
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from setFormExceptionsForErrors of GiftRegistryformHandler', null)
	}
	
	
	def"handleUpdateRegistry. This TC is when formError is true"(){
		given:
			this.spyUpdateRegistry()
			testObj.getFormError() >> TRUE
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			1 * testObj.checkFormRedirect('/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?eventType=WeddingEvent&registryId=REG_summary2351211$', 
				'/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp?registryId=REG_summary2351211$' , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			
	}
	
	def"handleUpdateRegistry. This TC is when TemplateEmailException thrown"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			requestMock.getLocale() >> new Locale("en_US")
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:null,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic("false")
			testObj.setUpdateSimplified("")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			1 * giftRegistryManagerMock.updateRegistry(_) >> null
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> {throw new TemplateEmailException("Mock for TemplateEmailException")}
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> null
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
			1 * testObj.checkFormRedirect("", "", requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("0")
			testObj.getRegistryVO().equals(registryVOMock)
			1 * testObj.logError('giftregistry_1085: p12345|false|Template Exception from handleUpdateRegistry of GiftRegistryformHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sending_exception', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_email_sending_exception', null)
	}
	
	def"handleUpdateRegistry. This TC is when RepositoryException thrown"(){
		given:
			this.spyUpdateRegistry()
			testObj.setRegContactAddress("contacting address")
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			testObj.setBabygenderStr("")
			requestMock.getLocale() >> new Locale("en_US")
			
			RegistryTypes registryTypesMock2 = new RegistryTypes(registryTypeName:"wedding")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"",college:"Harward")
			AddressVO addressVOMock = new AddressVO(addressLine1:"Alan Street",addressLine2:"Box")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"kennedy@gmail.com",profileId:"")
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:TRUE,registryId:'REG_summary2351211$',registryType:registryTypesMock2,coRegistrant:coRegistrantMock,event:eventVOMock,networkAffiliation:"network",
				prefStoreNum:"prefStoreNum",optInWeddingOrBump:null,shipping:null,primaryRegistrant:primaryRegistrantMock,siteId:"BedBathCanada")
			testObj.setRegistryVO(registryVOMock)
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> "8989898989"
			testObj.setDeactivateRegistry(null)
			testObj.setMakeRegistryPublic("false")
			testObj.setUpdateSimplified("")
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "WeddingEvent"
			profileMock.getPropertyValue("id") >> "p12345"
			
			1 * giftRegistryManagerMock.updateRegistry(_) >> null
			1 * testObj.sendCoregistrantEmail(requestMock, "BedBathCanada") >> {throw new RepositoryException("Mock for RepositoryException")}
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			//setProfileValues Private Method Coverage
			1 * giftRegistryManagerMock.getProfileItemFromEmail("kennedy@gmail.com", "BedBathCanada") >> null
			mutableRepositoryItemMock.getRepositoryId() >> "p254585"
			
			//setServiceParamter Private Method Coverage
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			sessionBeanMock.setRegistryTypesEvent(null)
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setUpdateRegServiceName("updateRegistry2")
			
			//setQasValues Private Method Coverage
			testObj.setRegBG(null)
			testObj.setCoRegBG(null)
			testObj.setContactPoBoxStatus(null)
			testObj.setContactPoBoxFlag(null)
			testObj.setShipPoBoxStatus(null)
			testObj.setShipPoBoxFlag(null)
			testObj.setFuturePoBoxStatus(null)
			testObj.setFuturePoBoxFlag(null)
			
			//setSessionBeanValues Private Method Coverage
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> null
			RegistryVO registryVOMock2 = new RegistryVO()
			sessionBeanMock.setRegistryVOs('REG_summary3351211$': null)
			sessionBeanMock.getValues().put("registrySkinnyVOList",null)
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
			1 * testObj.checkFormRedirect("", "", requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			registryVOMock.getCoRegistrant().getCoRegEmailFlag().equals("N")
			registryVOMock.getPrimaryRegistrant().getProfileId().equals("p12345")
			registryVOMock.getCoRegistrant().getProfileId().equals(null)
			registryVOMock.getRegistryType().getRegistryTypeName().equals("wedding")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("updateRegistry2")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(0)
			registryVOMock.getPrefRegContTime().equals("M")
			registryVOMock.getPrefCoregContMeth().equals(1)
			registryVOMock.getPrefStoreNum().equals("prefStoreNum")
			registryVOMock.getPrefCoregContTime().equals("A")
			registryVOMock.getSignup().equals("N")
			registryVOMock.getHint().equals("registry")
			registryVOMock.getWord().equals("registry")
			registryVOMock.getAffiliateOptIn().equals("N")
			testObj.isPoBoxAddress().equals(FALSE)
			registryVOMock.getIsPublic().equals("0")
			testObj.getRegistryVO().equals(registryVOMock)
			1 * testObj.logError('giftregistry_1001: p12345|false|Repository Exception from handleUpdateRegistry of GiftRegistryFormhandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_repo_exception', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_regsearch_repo_exception', null)
	}
	
	def"handleUpdateRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyUpdateRegistry()
			testObj.getFormError() >> TRUE
			requestMock.getLocale() >> new Locale("en_US")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("false")
			testObj.setMakeRegistryPublic("true")
			1 * testObj.preUpdateRegistry(requestMock, responseMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			1 * testObj.checkFormRedirect(null, null , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleUpdateRegistry of GiftRegistryformHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
	}
	
	def"handleUpdateRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyUpdateRegistry()
			testObj.getFormError() >> TRUE
			requestMock.getLocale() >> new Locale("en_US")
			
			requestMock.getParameter(BBBCoreConstants.ALTERNATE_NUM) >> ""
			testObj.setDeactivateRegistry("false")
			testObj.setMakeRegistryPublic("true")
			1 * testObj.preUpdateRegistry(requestMock, responseMock) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			RegistryVO registryVOMock = new RegistryVO(coRegOwner:FALSE,registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			testObj.setProfile(profileMock)
			profileMock.getRepositoryId() >> "p12345"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathCanada") >> TRUE
			
			1 * testObj.checkFormRedirect(null, null , requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleUpdateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: err_regsearch_sys_exception', null)
			1 * testObj.logError('giftregistry_1005: BBBSystemException from handleUpdateRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_sys_exception', 'en_us', null, null)
	}

	private spyUpdateRegistry() {
		testObj = Spy()
		testObj.setSiteContext(siteContextMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setPropertyManager(propertyManagerMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObj.setLoggingDebug(TRUE)
		testObj.setRegistryUpdateSuccessURL("/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp")
		testObj.setRegistryUpdateErrorURL("/store/giftregistry/simpleRegUpdateFormThroughAjax.jsp")
	}
	
	
	/* *************************************************************************************************************************************
	 * handleUpdateRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * preUpdatePrivateRegistry Method STARTS
	 *
	 * Signature : public void preUpdatePrivateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preUpdatePrivateRegistry. This TC is Happy flow of preUpdatePrivateRegistry public method"(){
		given:
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",contactAddress:addressVOMock) 
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
		
		expect:
			testObj.preUpdatePrivateRegistry(requestMock, responseMock)
	}
	
	def"preUpdatePrivateRegistry. This TC is when firstName and Lastname is not valid"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setSiteContext(siteContextMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John@",lastName:"Kennady#",contactAddress:addressVOMock)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
		
		when:
			testObj.preUpdatePrivateRegistry(requestMock, responseMock)
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("US")
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
	}
	
	def"preUpdatePrivateRegistry. This TC is when firstname and lastname is empty and registrantAddressValidation parameters are not valid"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setSiteContext(siteContextMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			
			AddressVO addressVOMock = new AddressVO(addressLine1:" Street",addressLine2:" Lane",city:"",state:"",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"",lastName:"",contactAddress:addressVOMock)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
		
		when:
			testObj.preUpdatePrivateRegistry(requestMock, responseMock)
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("CA")
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line2_invalid_contact: err_create_reg_address_line2_invalid_contact property=null.regContactAddrLine2 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_city_invalid_contact: err_create_reg_city_invalid_contact property=null.regContactCity exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line1_invalid_contact: err_create_reg_address_line1_invalid_contact property=null.regContactAddrLine1 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_state_invalid_contact: err_create_reg_state_invalid_contact property=null.regContactCity exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			2 * testObj.logError('giftregistry_1035: Invalid registraint city from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1032: Invalid registraint AddressLine1 from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1033: Invalid registraint AddressLine2 from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
	}
	
	def"preUpdatePrivateRegistry. This TC is when AddressLine2 is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setSiteContext(siteContextMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> null
			
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",contactAddress:addressVOMock)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
		
		when:
			testObj.preUpdatePrivateRegistry(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
	}
	
	/* *************************************************************************************************************************************
	 * preUpdatePrivateRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	/* *************************************************************************************************************************************
	 * preUpdateRegistry Method STARTS
	 *
	 * Signature : public void preUpdateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preUpdateRegistry. This TC is the Happy flow of preUpdateRegistry method"(){
		given:
			sessionBeanMock.setRegistryTypesEvent("BRD")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",guestCount:"50")
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"8787878787",,contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock,
				registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setPassword("")
			testObj.setShippingAddress("newShippingAddress")
			testObj.setFutureShippingDateSelected("")
			
		
		when:
			testObj.preUpdateRegistry(requestMock, responseMock)
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getShipping().getFutureShippingDate().equals(null)
			testObj.getFutureShippingAddress().equals(null)
	}
	
	def"preUpdateRegistry. This TC is when parameters are empty and FutureShippingDateSelected is false"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			
			sessionBeanMock.setRegistryTypesEvent("BA1")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",guestCount:"50",babyNurseryTheme:null,babyName:"Sarath")
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"",lastName:"",primaryPhone:"",cellPhone:"",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"",email:"")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock,
				registryId:null)
			testObj.setRegistryVO(registryVOMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			testObj.setPassword("")
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("false")
			
		
		when:
			testObj.preUpdateRegistry(requestMock, responseMock)
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("CA")
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("CA")
			registryVOMock.getShipping().getFutureShippingDate().equals(null)
			testObj.getFutureShippingAddress().equals(null)
			2 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_event_date_empty: err_create_reg_event_date_empty property=null.eventDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logError('giftregistry_1039: GiftRegistry empty event date from validateEventDate of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
	}
	
	def"preUpdateRegistry. This TC is when parameters are not valid and FutureShippingDateSelected is true"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			
			sessionBeanMock.setRegistryTypesEvent("COL")
			EventVO eventVOMock = new EventVO(eventDate:"validDate",college:"Harvard University")
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john@",lastName:"kennady@",primaryPhone:"primaryPhone",cellPhone:"cellPhone",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"Kennady",email:"bbbgmail.com")
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John@",lastName:"Kennady@",addressLine1:" street",addressLine2:" street1",city:"",zip:"")
			ShippingVO shippingVOMock = new ShippingVO(futureshippingAddress:futureshippingAddressMock)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock,
				registryId:null)
			testObj.setRegistryVO(registryVOMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setPassword("")
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("newFutureShippingAddress")
			
		
		when:
			testObj.preUpdateRegistry(requestMock, responseMock)
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getShipping().getFutureshippingAddress().getPrimaryPhone().equals("primaryPhone")
			1 * testObj.logError('giftregistry_1040: GiftRegistry Invalid event date from validateEventDate of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant phone number from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant cell phone from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1032: Invalid New Future shipping AddressLine1 from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1054: GiftRegistry empty future shipping date from futureShipDateValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1033: Invalid New Future shipping AddressLine2 from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1035: Invalid New Shipping city from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1041: GiftRegistry Invalid Co-registrant email from coregistrantValidation of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line1_invalid_future: err_create_reg_address_line1_invalid_future property=null.shippingFutureAddrLine1 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_reg_shipping_date_future_invalid: err_reg_shipping_date_future_invalid property=null.futureShippingDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_city_invalid_future: err_create_reg_city_invalid_future property=null.shippingFutureAddrCity exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line2_invalid_future: err_create_reg_address_line2_invalid_future property=null.shippingFutureAddrLine2 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantMobileNumber exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantPhoneNumber exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_invalid_email_length: err_create_reg_invalid_email_length property=null.coRegEmail exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.shippingFutureAddrLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.shippingFutureAddrFirstName exception=null', null)
			2 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_future: err_create_reg_zip_invalid_future property=null.shippingFutureAddrZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_event_date_invalid: err_create_reg_event_date_invalid property=null.eventDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
	}
	
	def"preUpdateRegistry. This TC is when FutureShippingAddress is not newFutureShippingAddress"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			
			sessionBeanMock.setRegistryTypesEvent("Others")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017")
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"8787878787",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"",email:"bbb@gmail.com")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John@",lastName:"Kennady@",addressLine1:" street",addressLine2:" street1",city:"",zip:"")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureShippingDate:"01/01/2017")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock,
				registryId:null)
			
			testObj.setRegistryVO(registryVOMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setPassword("")
			testObj.setShippingAddress("newShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("futureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
		
		when:
			testObj.preUpdateRegistry(requestMock, responseMock)
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("US")
			1 * testObj.logError('giftregistry_1033: Invalid AddressLine2 from shippingNewAddress of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1054: GiftRegistry Invalid future shipping date from futureShipDateValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1032: Invalid AddressLine1 from shippingNewAddress of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for shipping address from shippingNewAddress of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1035: Invalid Shipping city from shippingNewAddress of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.shippingNewAddrFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.shippingNewAddrLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_shipping: err_create_reg_zip_invalid_shipping property=null.shippingNewAddrZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_reg_shipping_date_future_invalid: err_reg_shipping_date_future_invalid property=null.futureShippingDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line1_invalid_shipping: err_create_reg_address_line1_invalid_shipping property=null.shippingNewAddrLine1 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_city_invalid_shipping: err_create_reg_city_invalid_shipping property=null.shippingNewAddrFirstCity exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line2_invalid_shipping: err_create_reg_address_line2_invalid_shipping property=null.shippingNewAddrLine2 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
	}
	
	/* *************************************************************************************************************************************
	 * preUpdateRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * preUpdateSimplifiedRegistry Method STARTS
	 *
	 * Signature : public void preUpdateSimplifiedRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preUpdateSimplifiedRegistry. This TC is when futureShippingDate is empty"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("BRD")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"showerDate",guestCount:"guest")
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"8787878787",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",email:"bbb@gmail.com")
			AddressVO shippingAddressVOMock = new AddressVO(firstName:"John",lastName:"kennady",addressLine1:"alan Post Box",addressLine2:"",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:shippingAddressVOMock,futureShippingDate:"")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"Y",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:'REG_summary2351211$')
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("newShippingAddress")
			testObj.setFutureShippingDateSelected("")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		when:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
		then:
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getShipping().getFutureShippingDate().equals(null)
			testObj.getFutureShippingAddress().equals(null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_shower_date_invalid: err_create_reg_shower_date_invalid property=null.showerDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_shipping: err_create_reg_zip_invalid_shipping property=null.shippingNewAddrZip exception=null', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1046: GiftRegistry Invalid shower date from validateShowerDate of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for shipping address from shippingNewAddress of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_noofguest_invalid: err_create_reg_noofguest_invalid property=null.guestCount exception=null', null)
			1 * testObj.logError('giftregistry_1043: GiftRegistry invalid format of num of guests from validateNumberOfGuest of GiftRegistryFormHandler', null)
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when futureShippingDate is false and parameters are empty, Eventtype is BRD"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("BRD")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",guestCount:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"",lastName:"",primaryPhone:"",cellPhone:"",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"N",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:'REG_summary2351211$')
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("")
			testObj.setFutureShippingDateSelected("false")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
		
		when:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
		then:
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("CA")
			registryVOMock.getShipping().getFutureShippingDate().equals(null)
			testObj.getFutureShippingAddress().equals(null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when futureShippingDate is true and parameters are not valid,EventType is BA1"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("BA1")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",babyNurseryTheme:null,babyName:"sarath@")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John@",lastName:"kennady@",primaryPhone:"primaryPhone",cellPhone:"cellPhone",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"john@",lastName:"kennady@",email:"bbbgmail.com")
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"02/02/2030",futureshippingAddress:futureshippingAddressMock)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:'REG_summary2351211$')
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("newFutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >>> ["BedBathUS",null]
		
		when:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
		then:
			registryVOMock.getShipping().getFutureshippingAddress().getPrimaryPhone().equals("primaryPhone")
			2 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_invalid_email_length: err_create_reg_invalid_email_length property=null.coRegEmail exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_future: err_create_reg_zip_invalid_future property=null.shippingFutureAddrZip exception=null', null)
			2 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantMobileNumber exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_babyname_invalid: err_create_reg_babyname_invalid property=null.babyName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantPhoneNumber exception=null', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant phone number from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant cell phone from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1041: GiftRegistry Invalid Co-registrant email from coregistrantValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1034: Invalid baby name from validateBabyName of GiftRegistryFormHandler', null)
			
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when registryVO's futureShippingDate is empty and eventType is BA1"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("BA1")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",babyNurseryTheme:"",babyName:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"kennady",email:"bbb@gmail.com")
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"",city:"New Jercy",zip:"70002")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"",futureshippingAddress:futureshippingAddressMock)
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:'REG_summary2351211$')
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("newFutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		when:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
		then:
			registryVOMock.getShipping().getFutureshippingAddress().getPrimaryPhone().equals("8989898989")
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_future: err_create_reg_zip_invalid_future property=null.shippingFutureAddrZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_empty_future_date: err_create_reg_empty_future_date property=null.futureAddressDate exception=null', null)
			
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when eventType is BA1 and decorTheme has value in eventFormSimplifyUpdateValidation method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("BA1")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",babyNurseryTheme:"babyTheme",babyName:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"kennady",email:"bbb@gmail.com")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"02/02/2030")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:'REG_summary2351211$')
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		when:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
		then:
			registryVOMock.getEvent().getBabyNurseryTheme().equals("babyTheme")
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line1_invalid_future: err_create_reg_address_line1_invalid_future property=null.futureAddressDate exception=null', null)
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when eventType is COL with values in eventFormSimplifyUpdateValidation method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("COL")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",college:"Harward College@")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"kennady",email:"bbb@gmail.com")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"02/02/2030")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:null)
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		when:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
		then:
			1 * testObj.logDebug('adding form exception: err_create_reg_college_invalid: err_create_reg_college_invalid property=null.COL exception=null', null)
			1 * testObj.logError('giftregistry_1037: GiftRegistry Invalid college name from validateCollege of GiftRegistryFormHandler', null)
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when eventType is COL with empty values in eventFormSimplifyUpdateValidation method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("COL")
			EventVO eventVOMock = new EventVO(eventDate:"",college:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"kennady",email:"bbb@gmail.com")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"02/02/2030")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:null)
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		expect:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when eventType is Others with values in eventFormSimplifyUpdateValidation method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("Others")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"kennady",email:"bbb@gmail.com")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"02/02/2030")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:null)
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		expect:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
	}
	
	def"preUpdateSimplifiedRegistry. This TC is when eventType is Others with empty values in eventFormSimplifyUpdateValidation method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setLoggingDebug(TRUE)
			sessionBeanMock.setRegistryTypesEvent("Others")
			EventVO eventVOMock = new EventVO(eventDate:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"kennady",email:"bbb@gmail.com")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"02/02/2030")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,networkAffiliation:"",event:eventVOMock,
				coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:null)
			
			testObj.setRegistryVO(registryVOMock)
			
			testObj.setShippingAddress("shippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			requestMock.getLocale() >> new Locale("en_US")
			
			//eventFormSimplifyUpdateValidation Private Method Coverage
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		expect:
			testObj.preUpdateSimplifiedRegistry(requestMock, responseMock)
	}
	
	/* *************************************************************************************************************************************
	 * preUpdateSimplifiedRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleCreateRegistry Method STARTS
	 *
	 * Signature : public  boolean handleCreateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleCreateRegistry. This TC is when RegistryEventType is COL and siteId is BedBathUS"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(college:"Harward University",eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"",optInWeddingOrBump:null,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			testObj.setRegistryEventType("COL")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> TRUE
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client123"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified("true")
			1 * testObj.preCreateSimplifyRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.REGISTRY_DEFAULT_STORE_ID) >> "5623"

			testObj.setBabygenderStr("male")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1) 
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setSuccessURL("/account/myaccount.jsp")
			testObj.setDesktop("true")
			
			1 * testObj.checkFormRedirect("/account/myaccount.jsp?registryId=2351211&eventType=eventType&hoorayModal=true" , 
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COL", requestMock,	responseMock) >> TRUE
			
			//preGiftRegCreateUser Private Method Coverage
			1 * propertyManagerMock.getLoginPropertyName() >> "loginPropery"
			1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailProperty"
			1 * propertyManagerMock.getAutoLoginPropertyName() >> "autoLoginProperty"
			1 * propertyManagerMock.getReceiveEmailPropertyName() >> "receiveEmailProperty"
			testObj.setShallowProfileChanges(TRUE)
			1 * propertyManagerMock.getStatusPropertyName() >> "statusProperty"
			testObj.setPassword("password")
			1 * propertyManagerMock.getPasswordPropertyName() >> "passwordProperty"
			1 * propertyManagerMock.getConfirmPasswordPropertyName() >> "confirmPasswordProperty"
			1 * propertyManagerMock.getFirstNamePropertyName() >> "firstNameProperty"
			1 * propertyManagerMock.getLastNamePropertyName() >> "lastNameProperty"
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile_already_exist"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
			//setQasValues Private Method Coverage
			testObj.setRegBG("regBG")
			testObj.setCoRegBG("coRegBG")
			testObj.setContactPoBoxStatus("Y")
			testObj.setContactPoBoxFlag("P")
			testObj.setShipPoBoxStatus("Y")
			testObj.setShipPoBoxFlag("P")
			testObj.setFuturePoBoxStatus("Y")
			testObj.setFuturePoBoxFlag("P")
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage 
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> null
			profileMock.getPropertyValue("id") >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("true")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getCoRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			MutableRepositoryItem primaryRepositoryItemMock = Mock()
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> primaryRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "item1234"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@gmail.com"
			1 * repositoryItemMock.getPropertyValue("source") >> "client123"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l) 
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			Cookie cookieMock1 = Mock()
			cookieMock.getName() >> "typeEvent"
			cookieMock1.getName() >> "RegistryTypesEvent"
			cookieMock1.getValue() >> "COL"
			requestMock.getCookies() >> [cookieMock,cookieMock1]
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "true")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant("http://www.bedbathandbeyond.com/store/giftregistry/view_registry_guest.jsp?registryId=2351211&eventType=",
				 "http://www.bedbathandbeyond.com/store/account/login.jsp", "BedBathUS", "Thanks for the Gift", resVOMock, testObj.getCoRegEmailFoundPopupStatus(),
				testObj.getCoRegEmailNotFoundPopupStatus(), giftRegEmailInfoMock)
			
			sessionBeanMock.getValues().put("userRegistriesList", ["First","Second"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "BedBathUS") >> registrySummaryVOMock
			
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktopWeb"
			requestMock.getServerName() >> "www.bedbathandbeyond.com"
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("COL")
			registryVOMock.getPrimaryRegistrant().getEmail().equals("bbb@gmail.com")
			1 * testObj.setValueProperty("receiveEmailProperty", "yes")
			1 * requestMock.setParameter("userCreatingRegistry", Boolean.TRUE)
			1 * requestMock.getParameter("favStoreId")
			registryVOMock.getRegBG().equals("regBG")
			registryVOMock.getCoRegBG().equals("coRegBG")
			registryVOMock.getPrimaryRegistrant().getContactAddress().isQasValidated().equals(TRUE)
			registryVOMock.getPrimaryRegistrant().getContactAddress().isPoBoxAddress().equals(TRUE)
			registryVOMock.getShipping().getShippingAddress().isQasValidated().equals(TRUE)
			registryVOMock.getShipping().getShippingAddress().isPoBoxAddress().equals(TRUE)
			registryVOMock.getShipping().getFutureshippingAddress().isQasValidated().equals(TRUE)
			registryVOMock.getShipping().getFutureshippingAddress().isPoBoxAddress().equals(TRUE)
			registryVOMock.getAffiliateTag().equals("BBB-Mobile")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("COL")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("createRegistry")
			registryVOMock.getCoRegistrant().setProfileId("r12345")
			registryVOMock.getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_Y)
			registryVOMock.getPrimaryRegistrant().setProfileId("item1234")
			registryVOMock.getStatus().equals("status")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefStoreNum().equals("5623")
			registryVOMock.getPrefRegContMeth().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefRegContTime().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getPrefCoregContMeth().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefCoregContTime().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getSignup().equals(BBBGiftRegistryConstants.SIGN_UP_NO)
			registryVOMock.getHint().equals(BBBGiftRegistryConstants.REGISTRY)
			registryVOMock.getWord().equals(BBBGiftRegistryConstants.WORD)
			registryVOMock.getAffiliateOptIn().equals(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getEvent().getEventDateWS().equals("20170111")
			registryVOMock.getEvent().getShowerDateWS().equals("20170211")
			registryVOMock.getEvent().getBirthDateWS().equals("19921009")
			registryVOMock.getShipping().getFutureShippingDateWS().equals("20173010")
			resVOMock.getEvent().getBabyGender().equals("male")
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCompany().equals("Harward University")
			resVOMock.getEvent().getGuestCount().equals(BBBCoreConstants.STRING_ZERO)
			resVOMock.isCreate().equals(TRUE)
			testObj.getRegistryVO().equals(resVOMock)
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211", "First", "Second"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211", "First", "Second"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			testObj.getRegistryCreationSuccessURL().equals("/account/myaccount.jsp?registryId=2351211&eventType=eventType&hoorayModal=true")
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('siteFlag: true', null)
			1 * testObj.logDebug('ServiceName: createRegistry', null)
			
	}
	
	def"handleCreateRegistry. This TC is when RegistryEventType is BRD and siteId is BedBathCanada"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> TRUE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathCanada") >> "eventType"
			testObj.setDesktop("false")
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD", requestMock,	responseMock) >> TRUE
			
			//preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathCanada") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathCanada"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathCanada"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			cookieMock.getName() >> "RegistryTypesEvent"
			cookieMock.getValue() >> "BRD"
			requestMock.getCookies() >> [cookieMock]
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant("http://www.bedbathandbeyond.com/store/giftregistry/view_registry_guest.jsp?registryId=2351211&eventType=",
				 "http://www.bedbathandbeyond.com/store/account/login.jsp", "BedBathCanada", "Thanks for the Gift", resVOMock, testObj.getCoRegEmailFoundPopupStatus(),
				testObj.getCoRegEmailNotFoundPopupStatus(), giftRegEmailInfoMock)
			
			sessionBeanMock.getValues().put("userRegistriesList", null)
			RegistryTypes registrySummaryTypesMock = new RegistryTypes(registryTypeDesc:"Wedding Anniversary")  
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registrySummaryTypesMock)
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "BedBathCanada") >> registrySummaryVOMock
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathCanada") >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "DesktopWeb"
			requestMock.getServerName() >> "www.bedbathandbeyond.com"
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> null
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			registryVOMock.getPrimaryRegistrant().getEmail().equals("bbb@gmail.com")
			1 * testObj.setValueProperty("receiveEmailProperty", "yes")
			1 * requestMock.setParameter("userCreatingRegistry", Boolean.TRUE)
			1 * requestMock.getParameter("favStoreId")
			registryVOMock.getAffiliateTag().equals("client887")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("BRD")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("createRegistry")
			registryVOMock.getCoRegistrant().setProfileId(null)
			registryVOMock.getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getPrimaryRegistrant().setProfileId("co12345")
			registryVOMock.getStatus().equals("status")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefRegContTime().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getPrefCoregContMeth().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefCoregContTime().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getSignup().equals(BBBGiftRegistryConstants.SIGN_UP_NO)
			registryVOMock.getHint().equals(BBBGiftRegistryConstants.REGISTRY)
			registryVOMock.getWord().equals(BBBGiftRegistryConstants.WORD)
			registryVOMock.getAffiliateOptIn().equals(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getEvent().getEventDateWS().equals("20171101")
			registryVOMock.getEvent().getShowerDateWS().equals("20171102")
			registryVOMock.getEvent().getBirthDateWS().equals("19920910")
			registryVOMock.getShipping().getFutureShippingDateWS().equals("20171030")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			resVOMock.isCreate().equals(TRUE)
			testObj.getRegistryVO().equals(resVOMock)
			resVOMock.getCookieType().equals(BBBCoreConstants.WED_CHANNEL_REF)
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock,registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(2)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			resVOMock.getRegistryType().getRegistryTypeDesc().equals("Wedding Anniversary")
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logDebug('userToken: token1', null)
			1 * testObj.logDebug('siteFlag: true', null)
			1 * testObj.logDebug('ServiceName: createRegistry', null)
			
	}
	
	def"handleCreateRegistry. This TC is when validateSite method returns false so formerror is true"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"",birthDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"true",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("")
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified("false")
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect(null,null, requestMock,	responseMock) >> FALSE
			
			//preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile_available_for_extenstion"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "item1234"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@gmail.com"
			1 * repositoryItemMock.getPropertyValue("source") >> "client887"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
			
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("COM")
			registryVOMock.getPrimaryRegistrant().getEmail().equals("bbb@gmail.com")
			1 * testObj.setValueProperty("receiveEmailProperty", "yes")
			1 * requestMock.setParameter("userCreatingRegistry", Boolean.TRUE)
			1 * requestMock.getParameter("favStoreId")
			registryVOMock.getAffiliateTag().equals("client887")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("COM")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("createRegistry")
			registryVOMock.getCoRegistrant().setProfileId(null)
			registryVOMock.getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getPrimaryRegistrant().setProfileId("p12345")
			registryVOMock.getStatus().equals("status")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefRegContTime().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getPrefCoregContMeth().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefCoregContTime().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getSignup().equals(BBBGiftRegistryConstants.SIGN_UP_NO)
			registryVOMock.getHint().equals(BBBGiftRegistryConstants.REGISTRY)
			registryVOMock.getWord().equals(BBBGiftRegistryConstants.WORD)
			registryVOMock.getAffiliateOptIn().equals(BBBGiftRegistryConstants.FLAG_Y)
			registryVOMock.getEvent().getEventDateWS().equals("20170111")
			resVOMock.isCreate().equals(TRUE)
			testObj.getRegistryVO().equals(resVOMock)
			1 * testObj.logDebug('adding form exception: err_create_reg_invalid_site: Invalid Site Association is provided.', null)
			1 * testObj.logError('Invalid Site Association is provided.', null)
			
	}
	
	def"handleCreateRegistry. This TC is when source and token are not equal in setProfileItem Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> ""
			1 * testObj.superHandleCreate(requestMock, responseMock) >> null
			sessionBeanMock.setLegacyMemberId("legacy12345")
			testObj.setEmailOptIn(TRUE)
			testObj.setEmailOptIn_BabyCA(TRUE)
			1 * bbbProfileToolsMock.createSiteItemRedirect("bbb@gmail.com", "TBS_BedBathUS", "legacy12345", null, "yes", "yes")
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "item1234"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "bbb@gmail.com"
			1 * repositoryItemMock.getPropertyValue("source") >> "client523"
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("COM")
			registryVOMock.getPrimaryRegistrant().getEmail().equals("bbb@gmail.com")
			1 * testObj.setValueProperty("receiveEmailProperty", "yes")
			1 * requestMock.getParameter("favStoreId")
			registryVOMock.getAffiliateTag().equals("BBB-Desktop")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("COM")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("createRegistry")
			registryVOMock.getCoRegistrant().setProfileId(null)
			registryVOMock.getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getPrimaryRegistrant().setProfileId("co12345")
			registryVOMock.getStatus().equals("status")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefRegContTime().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getPrefCoregContMeth().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefCoregContTime().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getSignup().equals(BBBGiftRegistryConstants.SIGN_UP_NO)
			registryVOMock.getHint().equals(BBBGiftRegistryConstants.REGISTRY)
			registryVOMock.getWord().equals(BBBGiftRegistryConstants.WORD)
			registryVOMock.getAffiliateOptIn().equals(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getEvent().getEventDateWS().equals("20170111")
			registryVOMock.getEvent().getShowerDateWS().equals("20170211")
			registryVOMock.getEvent().getBirthDateWS().equals("19921009")
			resVOMock.isCreate().equals(TRUE)
			testObj.getRegistryVO().equals(resVOMock)
			1 * testObj.logDebug('adding form exception: err_invalid_profileId: Invalid ProfileId is Provided.', null)
			1 * testObj.logError('Invalid ProfileId is Provided.', null)
						
	}
	
	def"handleCreateRegistry. This TC is when emailId and email are not equal in setProfileItem Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> ""
			1 * testObj.superHandleCreate(requestMock, responseMock) >> null
			sessionBeanMock.setLegacyMemberId("")
			testObj.setEmailOptIn(FALSE)
			testObj.setEmailOptIn_BabyCA(FALSE)
			1 * bbbProfileToolsMock.createSiteItemRedirect("bbb@gmail.com", "TBS_BedBathUS", null, null, "no", "no")
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "item1234"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "rajan@gmail.com"
			1 * repositoryItemMock.getPropertyValue("source") >> "client523"
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_invalid_profileId: Invalid ProfileId is Provided.', null)
			1 * testObj.logError('Invalid ProfileId is Provided.', null)
						
	}
	
	def"handleCreateRegistry. This TC is when emailId is null in setProfileItem, exception thrown in createTransientUserReg Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * testObj.superHandleCreate(requestMock, responseMock) >> {throw new Exception("Mock for Exception")}
			sessionBeanMock.setLegacyMemberId("legacy12345")
			testObj.setEmailOptIn(TRUE)
			testObj.setEmailOptIn_BabyCA(TRUE)
			1 * bbbProfileToolsMock.createSiteItemRedirect("bbb@gmail.com", "TBS_BedBathUS", "legacy12345", null, "yes", "yes")
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "item1234"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.EMAIL) >> null
			1 * repositoryItemMock.getPropertyValue("source") >> "client523"
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_invalid_profileId: Invalid ProfileId is Provided.', null)
			1 * testObj.logError('Invalid ProfileId is Provided.', null)
			1 * testObj.logError('Error while validating the profile is valid or not by mail id: com.bbb.exception.BBBBusinessException: Mock for BBBBusinessException', null)
						
	}
	
	def"handleCreateRegistry. This TC is when source is null in setProfileItem Private Method"(){
		given:
			this.spyCreateRegistry()
			testObj.setLoggingError(FALSE)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * testObj.superHandleCreate(requestMock, responseMock) >> null
			sessionBeanMock.setLegacyMemberId("legacy12345")
			testObj.setEmailOptIn(TRUE)
			testObj.setEmailOptIn_BabyCA(TRUE)
			1 * bbbProfileToolsMock.createSiteItemRedirect("bbb@gmail.com", "TBS_BedBathUS", "legacy12345", null, "yes", "yes")
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "item1234"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.EMAIL) >> "rajan@gmail.com"
			1 * repositoryItemMock.getPropertyValue("source") >> null
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_invalid_profileId: Invalid ProfileId is Provided.', null)
			0 * testObj.logError('Error while validating the profile is valid or not by mail id: com.bbb.exception.BBBBusinessException: Mock for BBBBusinessException', null)
						
	}
	
	def"handleCreateRegistry. This TC is when item is null in setProfileItem Private Method"(){
		given:
			this.spyCreateRegistry()
			testObj.setLoggingError(FALSE)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_invalid_profileId: Invalid ProfileId is Provided.', null)
			1 * testObj.logError('Repsoitory Exception occurred in getting Profile Item', _)
						
	}

	def"handleCreateRegistry. This TC is when profileId is empty in setProfileItem Private Method"(){
		given:
			this.spyCreateRegistry()
			testObj.setLoggingError(FALSE)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "true"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> null
			profileMock.getPropertyValue("id") >> ""
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> mutableRepositoryItemMock
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_invalid_profileId: Please provide a profileId', null)
	}
	
	def"handleCreateRegistry. This TC is when profileItem is null when isFromThirdParty is false in setProfileItem Private Method"(){
		given:
			this.spyCreateRegistry()
			testObj.setLoggingError(FALSE)
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:null)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> "false"
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> null
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp" ,
				"/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM", requestMock,	responseMock) >> FALSE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathCanada"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException: Invalid Email Provided.', null)
			1 * testObj.logError('Repsoitory Exception occurred in getting Profile Item', _)
	}
	
	def"handleCreateRegistry. This TC is when RegistryEventType is COM and siteId is TBS_BedBathUS"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "TBS_BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("COM")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "TBS_BedBathUS") >> "eventType"
			testObj.setDesktop("false")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=COM', 
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "TBS_BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "TBS_BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "TBS_BedBathUS"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			cookieMock.getName() >> "RegistryTypesEvent"
			cookieMock.getValue() >> "BRD"
			requestMock.getCookies() >> [cookieMock]
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant(*_)
			
			sessionBeanMock.getValues().put("userRegistriesList", null)
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "TBS_BedBathUS") >> null
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "TBS_BedBathUS") >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			testObj.setTbsEmailSiteMap(["TBS_BedBathUS":"www.tbsbaby.com"])
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> null
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("COM")
			1 * requestMock.getParameter("favStoreId")
			registryVOMock.getAffiliateTag().equals("client887")
			registryVOMock.getRegistryType().getRegistryTypeName().equals("COM")
			registryVOMock.getSiteId().equals("true")
			registryVOMock.getUserToken().equals("token1")
			registryVOMock.getServiceName().equals("createRegistry")
			registryVOMock.getCoRegistrant().setProfileId(null)
			registryVOMock.getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getPrimaryRegistrant().setProfileId("co12345")
			registryVOMock.getStatus().equals("status")
			registryVOMock.getNetworkAffiliation().equals("network")
			registryVOMock.getPrefRegContMeth().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefRegContTime().equals(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getPrefCoregContMeth().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD)
			registryVOMock.getPrefCoregContTime().equals(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME)
			registryVOMock.getSignup().equals(BBBGiftRegistryConstants.SIGN_UP_NO)
			registryVOMock.getHint().equals(BBBGiftRegistryConstants.REGISTRY)
			registryVOMock.getWord().equals(BBBGiftRegistryConstants.WORD)
			registryVOMock.getAffiliateOptIn().equals(BBBGiftRegistryConstants.FLAG_N)
			registryVOMock.getEvent().getEventDateWS().equals("20170111")
			registryVOMock.getEvent().getShowerDateWS().equals("20170211")
			registryVOMock.getEvent().getBirthDateWS().equals("19921009")
			registryVOMock.getShipping().getFutureShippingDateWS().equals("20173010")
			registryVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			resVOMock.isCreate().equals(TRUE)
			testObj.getRegistryVO().equals(resVOMock)
			resVOMock.getCookieType().equals(BBBCoreConstants.WED_CHANNEL_REF)
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(null)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock,registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(2)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			
	}
	
	def"handleCreateRegistry. This TC is when registryTypeEvent is BRD , sessionBean's registryEventType is BA1"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BA1")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop("false")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BA1',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			cookieMock.getName() >> "RegistryTypesEvent"
			cookieMock.getValue() >> "BRD"
			requestMock.getCookies() >> [cookieMock]
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant(*_)
			
			sessionBeanMock.getValues().put("userRegistriesList", null)
			RegistryTypes registrySummaryTypesMock = new RegistryTypes(registryTypeDesc:null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registrySummaryTypesMock)
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "BedBathUS") >> registrySummaryVOMock
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> ["www.bedbathandbeyond.com","com"]
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BA1")
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock,registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(2)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logError('Business Exception occured while fetching key(hoorayModalFlag) from FlagDrivenFunctions', _)
	}
	
	def"handleCreateRegistry. This TC is when registryTypeEvent and sessionBean's registryEventType are BA1"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BA1")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop("false")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BA1',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			cookieMock.getName() >> "RegistryTypesEvent"
			cookieMock.getValue() >> "BA1"
			requestMock.getCookies() >> [cookieMock]
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant(*_)
			
			sessionBeanMock.getValues().put("userRegistriesList", null)
			RegistryTypes registrySummaryTypesMock = new RegistryTypes(registryTypeDesc:null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registrySummaryTypesMock)
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "BedBathUS") >> registrySummaryVOMock
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> ["www.bedbathandbeyond.com"]
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BA1")
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock,registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(2)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			resVOMock.getCookieType().equals(BBBCoreConstants.THEBUMP_REF)
			1 * testObj.logError('System Exception occured while fetching key(hoorayModalFlag) from FlagDrivenFunctions', _)
	}
	
	def"handleCreateRegistry. This TC is when registryTypeEvent is null"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BA1")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop("false")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BA1',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			requestMock.getCookies() >> null
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant(*_)
			
			sessionBeanMock.getValues().put("userRegistriesList", null)
			RegistryTypes registrySummaryTypesMock = new RegistryTypes(registryTypeDesc:null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registrySummaryTypesMock)
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "BedBathUS") >> registrySummaryVOMock
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktopWeb"
			requestMock.getServerName() >> "www.bedbathandbeyond.com"
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BA1")
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock,registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(2)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
	}
	
	def"handleCreateRegistry. This TC is sessionBean's registryTypeEvent is not BA1"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop("false")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			cookieMock.getName() >> "RegistryTypesEvent"
			cookieMock.getValue() >> "BA1"
			requestMock.getCookies() >> [cookieMock]
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant(*_)
			
			sessionBeanMock.getValues().put("userRegistriesList", null)
			RegistryTypes registrySummaryTypesMock = new RegistryTypes(registryTypeDesc:null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:registrySummaryTypesMock)
			1 * giftRegistryManagerMock.getRegistryInfo("2351211", "BedBathUS") >> registrySummaryVOMock
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "desktopWeb"
			requestMock.getServerName() >> "www.bedbathandbeyond.com"
			
			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			sessionBeanMock.getValues().get("userRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userActiveRegistriesList").equals(["2351211"])
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			sessionBeanMock.getValues().get("registrySkinnyVOList").equals([registrySkinnyVOMock,registrySkinnyVOMock1])
			sessionBeanMock.getValues().get("size").equals(2)
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
	}
	
	
	def"handleCreateRegistry. This TC is when createRegistryResVO is null and it throws BBBSystemException"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BA1")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BA1',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> null
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BA1")
			1 * testObj.logError('giftregistry_1005: BBBSystemException from handleCreateRegistry of GiftRegistryFormHandler', _)
			1 * testObj.logDebug('adding form exception: err_regsearch_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleCreateRegistry. This TC is when ErrorId is 200 in createRegistryErrorExist Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop(null)
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:200,errorDisplayMessage:"ErrorMessage")
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l,serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock

			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logError('giftregistry_1082: ErrorMessage:200', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_input_field_error', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_input_field_error', null)
			1 * testObj.logDebug('adding form exception: ErrorMessage: ErrorMessage property=null.200 exception=null', null)
			
	}

	def"handleCreateRegistry. This TC is when ErrorId is 900 in createRegistryErrorExist Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop(null)
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:900,errorDisplayMessage:null)
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l,serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock

			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logError('giftregistry_1083: :900', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: :  property=null.900 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
			
	}

	
	def"handleCreateRegistry. This TC is when ErrorId is 901 in createRegistryErrorExist Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop(null)
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:901,errorDisplayMessage:"ErrorMessage")
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l,serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock

			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logError('giftregistry_1084: ErrorMessage:901', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
			1 * testObj.logDebug('adding form exception: ErrorMessage: ErrorMessage property=null.901 exception=null', null)
			
	}

	
	def"handleCreateRegistry. This TC is when ErrorId is 902 in createRegistryErrorExist Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop(null)
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:902,errorDisplayMessage:"ErrorMessage")
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l,serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock

			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logError('giftregistry_1049: ErrorMessage:902', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_invalid_input_format', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format', null)
			1 * testObj.logDebug('adding form exception: ErrorMessage: ErrorMessage property=null.902 exception=null', null)
			
	}

	def"handleCreateRegistry. This TC is when ErrorId is 0 in createRegistryErrorExist Private Method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			1 * bbbCatalogToolsMock.getRegistryTypeName("collegeType", "BedBathUS") >> "eventType"
			testObj.setDesktop(null)
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:0,errorDisplayMessage:"ErrorMessage")
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l,serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock

			//getHoorayModalFlag Private Method Coverage
			testObj.setHoorayModal("hoorayModal")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "hoorayModal") >> ["true"]
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			testObj.getCreateRegistryResVO().equals(createRegistryResVOMock)
			giftRegSessionBeanMock.getResponseHolder().equals(resVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("created")
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from createRegistryErrorExist of GiftRegistryformHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
			
	}
	
	def"handleCreateRegistry. This TC is when isUniqueRequestEntry returns false"(){
		given:
			this.spyCreateRegistry()
			RegistryVO registryVOMock = new RegistryVO()
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("")
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> FALSE
			
			 1 * testObj.checkFormRedirect(null,null, requestMock, responseMock) >> FALSE
	
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE

	}
	
	def"handleCreateRegistry. This TC is when isTransient is true and isFromThirdParty is false"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(college:"Harward University",eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"",optInWeddingOrBump:null,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			testObj.setRegistryEventType("COL")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> FALSE
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client123"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified("true")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=COL', 
				requestMock, responseMock) >> FALSE
			
			// preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile_already_exist"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
	}
	
	def"handleCreateRegistry. This TC is when formError occurred in createTransientUserReg method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(college:"Harward University",eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"",optInWeddingOrBump:null,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			testObj.setRegistryEventType("COL")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> FALSE
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client123"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified("true")
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=COL',
				requestMock, responseMock) >> FALSE
			
			testObj.getFormError() >> TRUE
			testObj.getFormExceptions() >> ["ErrorMessage"]
			
			// preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile_already_exist"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
	}
	
	
	def"handleCreateRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(college:"Harward University",eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"",optInWeddingOrBump:null,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			testObj.setRegistryEventType("COL")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> TRUE
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client123"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified("true")
			1 * testObj.preCreateSimplifyRegistry(requestMock, responseMock) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp", "/store/checkout/singlePage/ajax/guest_json.jsp?regType=COL", requestMock, responseMock) >> FALSE
			
			// preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile_already_exist"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from handleCreateRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_biz_exception', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_regsearch_biz_exception', null)
	}
	
	def"handleCreateRegistry. This TC is when TemplateEmailException thrown"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			requestMock.getCookies() >> []
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false")
			testObj.setRegContactAddress("contacting address")
			siteMock.getPropertyValue("defaultCountry") >> "US"
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(resVOMock, "shipping Address", "Future shipping Address", profileMock ,"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "Thanks for the Gift"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("/store/giftregistry/view_registry_guest.jsp?registryId=")
			testObj.setLoginRedirectUrl("/store/account/login.jsp")
			testObj.setCoRegEmailNotFoundPopupStatus("true")
			1 * giftRegistryManagerMock.sendEmailToCoregistrant(*_) >> {throw new TemplateEmailException("Mock for TemplateEmailException")}
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			resVOMock.getPrimaryRegistrant().getAddressSelected().equals("contacting address")
			1 * testObj.logDebug('adding form exception: err_email_sending_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_email_sending_exception', 'en_us', null, null)
	}
	
	def"handleCreateRegistry. This TC is when RepositoryException thrown"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"2345",optInWeddingOrBump:"false",shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			testObj.setRepeatingRequestMonitor(repeatingRequestMonitorMock)
			repeatingRequestMonitorMock.isUniqueRequestEntry("handleCreateRegistry") >> TRUE
			
			testObj.setRegistryEventType("BRD")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> null
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client887"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> FALSE
	
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified(null)
			1 * testObj.preCreateRegistry(requestMock, responseMock) >> null
			
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY) >> "status"
			testObj.setBabygenderStr("")
			
			testObj.setFutureShippingAddress("Future shipping Address")
			testObj.setShippingAddress("shipping Address")
			AddressVO addressVOMock1 = new AddressVO()
			AddressVO addressVOMock2 = new AddressVO()
			AddressVO addressVOMock3 = new AddressVO()
			testObj.setRegistrantAddressFromWS(addressVOMock1)
			testObj.setShippingAddressFromWS(addressVOMock2)
			testObj.setFutureShippingAddressFromWS(addressVOMock3)
			
			RegistryTypes registryTypesMock1 = new RegistryTypes(registryTypeName:"collegeType")
			EventVO eventVOMock1 = new EventVO(guestCount:"50")
			RegistryVO resVOMock = new RegistryVO(event:eventVOMock1,registryId:'REG_summary2351211$',registryType:registryTypesMock1)
			1 * giftRegistryManagerMock.setShippingBillingAddr(registryVOMock,"shipping Address",
			"Future shipping Address", profileMock, testObj.getRegistrantAddressFromWS(),
			testObj.getShippingAddressFromWS(),testObj.getFutureShippingAddressFromWS()) >> resVOMock
			
			1 * testObj.checkFormRedirect('/store/checkout/singlePage/ajax/guest_json.jsp', '/store/checkout/singlePage/ajax/guest_json.jsp?regType=BRD',
				requestMock, responseMock) >> TRUE
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
			//setWSDLParameters Private Method Coverage
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token1"]
			testObj.setCreateRegistryServiceName("createRegistry")

			//setProfileItem Private Method Coverage
			requestMock.getParameter(BBBAccountConstants.PROFILE_ID) >> "p12345"
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			testObj.setCoRegEmailFoundPopupStatus("false")
			1 * giftRegistryManagerMock.getProfileItemFromEmail(testObj.getRegistryVO().getPrimaryRegistrant().getEmail(), "true") >> null
			1 * profileRepositoryMock.getItem("p12345", BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME) >> mutableRepositoryItemMock
			mutableRepositoryItemMock.getRepositoryId() >> "co12345"
			
			//validateSite Private Method Coverage
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock.getPropertyValue("userSiteItems") >> ["USsite":repositoryItemMock1]
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			
			//setSessionObjects Private Method Coverage
			requestMock.getLocale() >> new Locale("en_US")
			RegistryResVO createRegistryResVOMock = new RegistryResVO(registryId:2351211l)
			1 * giftRegistryManagerMock.createRegistry(resVOMock) >> createRegistryResVOMock
			requestMock.getCookies() >> []
			1 * giftRegistryManagerMock.giftRegistryRepoEntry(resVOMock, "false") >>  {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getSimplifyRegVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("BRD")
			1 * testObj.logDebug('adding form exception: err_regsearch_repo_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_regsearch_repo_exception', 'en_us', null, null)
	}
	
	def"handleCreateRegistry. This TC is when formError is true in preCreateSimplifyRegistry method"(){
		given:
			this.spyCreateRegistry()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"30/10/2017")
			EventVO eventVOMock = new EventVO(college:"Harward University",eventDate:"01/11/2017",showerDate:"02/11/2017",birthDate:"10/09/1992")
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbbcustomer@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(email:"bbb@gmail.com",firstName:"John",lastName:"kennady")
			RegistryVO registryVOMock = new RegistryVO(primaryRegistrant:primaryRegistrantMock,event:eventVOMock,coRegistrant:coRegistrantMock,
				networkAffiliation:"network",prefStoreNum:"",optInWeddingOrBump:null,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			testObj.setFromPage("spCheckoutLogin")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/singlePage/ajax/guest_json.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			testObj.setRegistryEventType("COL")
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) >> TRUE
			requestMock.getHeader(BBBGiftRegistryConstants.CLIENTID) >> "client123"
			1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			profileMock.isTransient() >> TRUE
	
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.setCreateSimplified("true")
			1 * testObj.preCreateSimplifyRegistry(requestMock, responseMock) >> null
			
			1 * testObj.checkFormRedirect("/store/checkout/singlePage/ajax/guest_json.jsp", "/store/checkout/singlePage/ajax/guest_json.jsp?regType=COL", requestMock, responseMock) >> FALSE
			
			testObj.getFormError() >> TRUE
			
			// preGiftRegCreateUser Private Method Coverage
			this.preGiftRegCreateUser()
			
			//createTransientUserReg Private Method Coverage
			1 * bbbProfileToolsMock.checkForRegistration("bbb@gmail.com") >> "profile_already_exist"
			1 * testObj.superLoginUser(requestMock, responseMock) >> null
			
			//setQasValues Private Method Coverage
			this.setQasValues()
			
		when:
			boolean results = testObj.handleCreateRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
	}
		
	
	private setQasValues() {
		testObj.setRegBG("")
		testObj.setCoRegBG("")
		testObj.setContactPoBoxStatus("NO")
		testObj.setContactPoBoxFlag("NO")
		testObj.setShipPoBoxStatus("NO")
		testObj.setShipPoBoxFlag("NO")
		testObj.setFuturePoBoxStatus("NO")
		testObj.setFuturePoBoxFlag("NO")
	}

	private preGiftRegCreateUser() {
		1 * propertyManagerMock.getLoginPropertyName() >> "loginPropery"
		1 * propertyManagerMock.getEmailAddressPropertyName() >> "emailProperty"
		1 * propertyManagerMock.getAutoLoginPropertyName() >> "autoLoginProperty"
		1 * propertyManagerMock.getReceiveEmailPropertyName() >> "receiveEmailProperty"
		testObj.setShallowProfileChanges(FALSE)
		0 * propertyManagerMock.getStatusPropertyName() >> "statusProperty"
		testObj.setPassword("password")
		1 * propertyManagerMock.getPasswordPropertyName() >> "passwordProperty"
		1 * propertyManagerMock.getConfirmPasswordPropertyName() >> "confirmPasswordProperty"
		1 * propertyManagerMock.getFirstNamePropertyName() >> "firstNameProperty"
		1 * propertyManagerMock.getLastNamePropertyName() >> "lastNameProperty"
	}

	private spyCreateRegistry() {
		testObj = Spy()
		testObj.setSessionBean(sessionBeanMock)
		testObj.setSiteContext(siteContextMock)
		testObj.setLoggingDebug(TRUE)
		testObj.setProfile(profileMock)
		testObj.setProfileTools(bbbProfileToolsMock)
		testObj.setPropertyManager(propertyManagerMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setProfileRepository(profileRepositoryMock)
		testObj.setMessageHandler(lblTxtTemplateManagerMock)
		testObj.setGiftRegEmailInfo(giftRegEmailInfoMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
	}
	
	/* *************************************************************************************************************************************
	 * handleCreateRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * preCreateRegistry Method STARTS
	 *
	 * Signature : public void preCreateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preCreateRegistry. This TC is the Happy flow of preCreateRegistry method"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1)
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"9898989898",cellPhone:"8787878787",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"",email:"")
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"babyTheme",eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"s")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock,registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			testObj.setPassword("password")
			testObj.setShippingAddress("newShippingAddress")
			testObj.setFutureShippingDateSelected("")
			
		when:
			testObj.preCreateRegistry(requestMock, responseMock)
			
		then:
			registryVOMock.getEvent().getBabyNurseryTheme().equals("babyTheme")
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals(BBBGiftRegistryConstants.US)
			registryVOMock.getShipping().getFutureShippingDate().equals(null)
			testObj.getFutureShippingAddress().equals(null)
	}
	
	def"preCreateRegistry. This TC is when FutureShippingDateSelected is false and babyName greater than 30 letters"(){
		given:
			this.spyCreateRegistry()
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")

			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"9898989898",cellPhone:"8787878787",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"asdfghjklasdfghjklasdfghjklasdfghhj")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			testObj.setPassword("")
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("false")
			
		when:
			testObj.preCreateRegistry(requestMock, responseMock)
			
		then:
			registryVOMock.getEvent().getBabyNurseryTheme().equals("")
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals(BBBGiftRegistryConstants.US)
			registryVOMock.getShipping().getFutureShippingDate().equals(null)
			testObj.getFutureShippingAddress().equals(null)
			1 * testObj.logDebug('adding form exception: err_create_reg_babyname_invalid: err_create_reg_babyname_invalid property=null.babyName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_shower_date_invalid: err_create_reg_shower_date_invalid property=null.showerDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1046: GiftRegistry Invalid shower date from validateShowerDate of GiftRegistryFormHandler', null)
	}
	
	def"preCreateRegistry. This TC is when FutureShippingDateSelected is true and FutureShippingAddress is newFutureShippingAddress"(){
		given:
			this.spyCreateRegistry()
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			requestMock.getLocale() >> new Locale("en_US")
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureShippingDate:"02/02/2030",futureshippingAddress:futureshippingAddressMock)
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"9898989898",cellPhone:"8787878787",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			EventVO eventVOMock = new EventVO(babyNurseryTheme:null,eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,registryId:'REG_summary2351211$',shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setPassword("")
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("newFutureShippingAddress")
			
		when:
			testObj.preCreateRegistry(requestMock, responseMock)
			
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals(BBBGiftRegistryConstants.US)
			registryVOMock.getShipping().getFutureshippingAddress().getPrimaryPhone().equals("9898989898")
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_future: err_create_reg_zip_invalid_future property=null.shippingFutureAddrZip exception=null', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_shower_date_invalid: err_create_reg_shower_date_invalid property=null.showerDate exception=null', null)
	}
	
	def"preCreateRegistry. This TC is when FutureShippingDateSelected is true and FutureShippingAddress is not newFutureShippingAddress"(){
		given:
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			requestMock.getLocale() >> new Locale("en_US")
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureShippingDate:"02/02/2030",futureshippingAddress:futureshippingAddressMock)
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"9898989898",cellPhone:"8787878787",contactAddress:addressVOMock)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			EventVO eventVOMock = new EventVO(babyNurseryTheme:null,eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,registryId:null,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setPassword("")
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateRegistry(requestMock, responseMock)
			
		then:
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals(BBBGiftRegistryConstants.US)
	}
	
	/* *************************************************************************************************************************************
	 * preCreateRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * preCreateSimplifyRegistry Method STARTS
	 *
	 * Signature : public void preCreateSimplifyRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"preCreateSimplifyRegistry. This TC is when FutureShippingDateSelected is empty and guestCount is empty"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathCanada"
			sessionBeanMock.setRegistryTypesEvent("BRD")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathCanada") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"numberOfGuests",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,registryInputVOMock2,
				registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock6,registryInputVOMock7,registryInputVOMock8,,registryInputVOMock9,
				registryInputVOMock10],isPublic:TRUE)
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",email:"bbbcustomer@gmail.com")
			AddressVO addressVOMock = new AddressVO(addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",state:"NJ",zip:"40001")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"john",lastName:"kennady",primaryPhone:"9898989898",cellPhone:"8787878787",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(eventDate:"01/01/2017",showerDate:"26/01/2017",guestCount:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("newShippingAddress")
			testObj.setFutureShippingDateSelected("")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("CA")
			registryVOMock.getPrimaryRegistrant().getContactAddress().getCountry().equals("CA")
			registryVOMock.getIsPublic().equals("1")
			1 * testObj.logError('giftregistry_20134: GiftRegistry empty event date from validateNetworkAffiliation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1042: GiftRegistry number of guests empty from validateNumberOfGuest of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for shipping address from shippingNewAddress of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_noofguest_empty: err_create_reg_noofguest_empty property=null.guestCount exception=null', null)
			1 * testObj.logDebug('adding form exception: err_networkAffiliation_empty: err_networkAffiliation_empty property=null.networkAffiliation exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_shipping: err_create_reg_zip_invalid_shipping property=null.shippingNewAddrZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			
	}
	
	def"preCreateSimplifyRegistry. This TC is when FutureShippingDateSelected is false and some parameters are not valid"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BRD")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"numberOfGuests",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,registryInputVOMock2,
				registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock6,registryInputVOMock7,registryInputVOMock8,,registryInputVOMock9,
				registryInputVOMock10],isPublic:FALSE)
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1)
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"john@",lastName:"kennady@",email:"bbbcustomergmail.com")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"@john",lastName:"@kennady",primaryPhone:"primaryPhone",cellPhone:"cellphone",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(eventDate:"01/01/2017",showerDate:"26/01/2017",guestCount:"45")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"NotYes",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,shipping:shippingVOMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("false")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getIsPublic().equals("0")
			1 * testObj.logError('giftregistry_1041: GiftRegistry Invalid Co-registrant email from coregistrantValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1040: GiftRegistry Invalid networkaffiliation flag GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant phone number from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1046: GiftRegistry Invalid shower date from validateShowerDate of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant cell phone from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_invalid_email_length: err_create_reg_invalid_email_length property=null.coRegEmail exception=null', null)
			1 * testObj.logDebug('adding form exception: err_networkAffiliation_invalid: err_networkAffiliation_invalid property=null.networkAffiliation exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_co_reg_last_name_invalid: err_create_co_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantMobileNumber exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantPhoneNumber exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_co_reg_first_name_invalid: err_create_co_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
	}
	
	def"preCreateSimplifyRegistry. This TC is when FutureShippingDateSelected is true , registryId has value and parameters are empty"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BRD")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"numberOfGuests",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock12,registryInputVOMock1,registryInputVOMock2,registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock6,
				registryInputVOMock7,registryInputVOMock8,,registryInputVOMock9])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"02/02/2030")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:null,lastName:"",primaryPhone:"",cellPhone:"",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",guestCount:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:'REG_summary2351211$')
			testObj.setRegistryVO(registryVOMock)
			testObj.setPassword("password")
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("newFutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getCoRegistrant().getContactAddress().getCountry().equals("US")
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getShipping().getFutureshippingAddress().getPrimaryPhone().equals("")
			1 * testObj.logDebug('adding form exception: err_create_reg_last_name_invalid: err_create_reg_last_name_invalid property=null.coRegLastName exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_future: err_create_reg_zip_invalid_future property=null.shippingFutureAddrZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_first_name_invalid: err_create_reg_first_name_invalid property=null.coRegFirstName exception=null', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
	}
	
	def"preCreateSimplifyRegistry. This TC is when FutureShippingDateSelected is true and registryId is null"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BRD")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"numberOfGuests",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock1,registryInputVOMock2,registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock6,
				registryInputVOMock7,registryInputVOMock8,,registryInputVOMock9,registryInputVOMock12])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null,lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"",cellPhone:"",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",guestCount:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setPassword("password")
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("newFutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getShipping().getFutureshippingAddress().getPrimaryPhone().equals("")
			1 * testObj.logDebug('adding form exception: err_create_reg_city_invalid_contact: err_create_reg_city_invalid_contact property=null.regContactCity exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_empty_future_date: err_create_reg_empty_future_date property=null.futureAddressDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line1_invalid_contact: err_create_reg_address_line1_invalid_contact property=null.regContactAddrLine1 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_reg_shipping_date_future_invalid: err_reg_shipping_date_future_invalid property=null.futureShippingDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_future: err_create_reg_zip_invalid_future property=null.shippingFutureAddrZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_zip_invalid_contact: err_create_reg_zip_invalid_contact property=null.regContactZip exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_state_invalid_contact: err_create_reg_state_invalid_contact property=null.regContactCity exception=null', null)
			1 * testObj.logError('giftregistry_1054: GiftRegistry empty future shipping date from futureShipDateValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1032: Invalid registraint AddressLine1 from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler', null)
			2 * testObj.logError('giftregistry_1035: Invalid registraint city from registrantAddressValidation of GiftRegistryFormHandler', null)
			1 * testObj.logError('giftregistry_1047: GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of GiftRegistryFormHandler', null)
			
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is BA1 and isPublic returns false by showContactAddress"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"nurseryTheme",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"babyName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock1,registryInputVOMock2,registryInputVOMock3,registryInputVOMock13,registryInputVOMock4,registryInputVOMock5,
				registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,registryInputVOMock12,registryInputVOMock6])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",email:"bbbcustomer@gmail.com")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"Babytheme",eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"Sarath")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getEvent().getBabyNurseryTheme().equals("Babytheme")
			1 * testObj.logError('giftregistry_1054: GiftRegistry empty future shipping date from futureShipDateValidation of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_reg_shipping_date_future_invalid: err_reg_shipping_date_future_invalid property=null.futureShippingDate exception=null', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_address_line1_invalid_future: err_create_reg_address_line1_invalid_future property=null.futureAddressDate exception=null', null)
			
	}

	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is BA1 and isPublic returns false by showFutureShippingAddr"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"nurseryTheme",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"babyName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock1,registryInputVOMock2,registryInputVOMock3,registryInputVOMock13,registryInputVOMock4,registryInputVOMock5,
				registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,registryInputVOMock12,registryInputVOMock6])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"Babytheme",eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"Sarath")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			registryVOMock.getEvent().getBabyNurseryTheme().equals("Babytheme")
			1 * testObj.logDebug('adding form exception: err_create_reg_shower_date_invalid: err_create_reg_shower_date_invalid property=null.showerDate exception=null', null)
			1 * testObj.logError('giftregistry_1046: GiftRegistry Invalid shower date from validateShowerDate of GiftRegistryFormHandler', null)
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is BA1 and isPublic returns false by babyName"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"nurseryTheme",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"babyName",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock1,registryInputVOMock2,registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,
				registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,registryInputVOMock12,registryInputVOMock6,registryInputVOMock13])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"",babyName:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is BA1 and isPublic returns false by eventDate"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"nurseryTheme",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"babyName",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock2,registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,
				registryInputVOMock12,registryInputVOMock6,registryInputVOMock13,registryInputVOMock1])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"theme",eventDate:"",showerDate:"",babyName:"sarath")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when registryInputList is null"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:null)
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			RegistryVO registryVOMock = new RegistryVO()
			testObj.setRegistryVO(registryVOMock)
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			1 * testObj.logError('Error in retrieving BCC Registry Input fields', null)
			
	}
	
	def"preCreateSimplifyRegistry. This TC is when RepositoryException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BA1")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			1 * testObj.getRegistryInputsVO("eventType") >> {throw new RepositoryException("Mock for RepositoryException")}
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"26/01/2030")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",email:"bbbcustomer@gmail.com")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"theme",eventDate:"01/01/2017",showerDate:"26/01/2017",babyName:"sarath")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			1 * testObj.logError('Error in retrieving BCC Registry Mandatory Input fieldsMock for RepositoryException', _)
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is COL and isPublic returns false by showerDate"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("COL")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"nurseryTheme",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"college",requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock14 = new RegistryInputVO(fieldName:"babyGender",requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,
				registryInputVOMock12,registryInputVOMock6,registryInputVOMock13,registryInputVOMock14,registryInputVOMock1,registryInputVOMock2])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"theme",eventDate:"01/01/2017",showerDate:"",babyName:"sarath",college:"",babyGender:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected("true")
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is COL and isPublic returns false by nurseryTheme"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("COL")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"showerDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"nurseryTheme",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"college",requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock14 = new RegistryInputVO(fieldName:"babyGender",requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock4,registryInputVOMock5,registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,
				registryInputVOMock12,registryInputVOMock6,registryInputVOMock13,registryInputVOMock14,registryInputVOMock1,registryInputVOMock2,registryInputVOMock3])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"01/01/2017",showerDate:"",babyName:"sarath",babyGender:"male",
				college:"XYZ Engineering and Technology,Z school of Engineering, Rajan Teacher Training and Institutions and Groups of Engineering")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			1 * testObj.logError('giftregistry_1038: GiftRegistry Invalid college name length from validateCollege of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_create_reg_college_invalid_length: err_create_reg_college_invalid_length property=null.COL exception=null', null)
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is COL and isPublic returns false by college"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("COL")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock13 = new RegistryInputVO(fieldName:"college",requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock14 = new RegistryInputVO(fieldName:"babyGender",requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock4,registryInputVOMock5,registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,
				registryInputVOMock12,registryInputVOMock6,registryInputVOMock14,registryInputVOMock1,registryInputVOMock13])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"",babyName:"sarath",babyGender:"male", college:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is COL and isPublic returns false by babyGender"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("COL")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock14 = new RegistryInputVO(fieldName:"babyGender",requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock4,registryInputVOMock5,registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,
				registryInputVOMock12,registryInputVOMock6,registryInputVOMock1,registryInputVOMock14])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"",babyName:"sarath",babyGender:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is different and isPublic returns false by showShippingAddress"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BirthDay")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock10 = new RegistryInputVO(fieldName:"showShippingAddress",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock11 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock12 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock10,registryInputVOMock11,
				registryInputVOMock4,registryInputVOMock5,registryInputVOMock7,registryInputVOMock8,registryInputVOMock9,
				registryInputVOMock12,registryInputVOMock6,registryInputVOMock1])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"8989898989",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"01/01/2017",showerDate:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is different and isPublic returns false by PhoneNumber"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BirthDay")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"PhoneNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,
				registryInputVOMock2,registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock6,
				registryInputVOMock7,registryInputVOMock8,registryInputVOMock9])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"",cellPhone:"9898989898",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"01/01/2017",showerDate:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantPhoneNumber exception=null', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant phone number from validateCollege of GiftRegistryFormHandler', null)
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is different and isPublic returns false by MobileNumber"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BirthDay")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"MobileNumber",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"showContactAddress",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock5 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock6 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock7 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock8 = new RegistryInputVO(fieldName:"showFutureShippingAddr",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock9 = new RegistryInputVO(fieldName:"futureShippingDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,
				registryInputVOMock3,registryInputVOMock4,registryInputVOMock5,registryInputVOMock6,
				registryInputVOMock7,registryInputVOMock8,registryInputVOMock9])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"9898989898",cellPhone:"",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
			1 * testObj.logDebug('adding form exception: err_create_reg_phone_number_is_invalid: err_create_reg_phone_number_is_invalid property=null.registrantMobileNumber exception=null', null)
			1 * testObj.logError('giftregistry_1044: GiftRegistry Invalid registrant cell phone from validateCollege of GiftRegistryFormHandler', null)
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is different and isPublic returns false by CoRegistrantFirstName"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BirthDay")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"CoRegistrantFirstName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock4 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,
				registryInputVOMock2,registryInputVOMock3,registryInputVOMock4])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"9898989898",cellPhone:"",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is different and isPublic returns false by CoRegistrantLastName"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BirthDay")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"CoRegistrantLastName",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock3 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:FALSE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,
				registryInputVOMock2,registryInputVOMock3])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"9898989898",cellPhone:"",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	def"preCreateSimplifyRegistry. This TC is when RegistryTypesEvent is different and isPublic returns false by CoRegistrantEmail"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			testObj.setSimplifyRegistryManager(simplifyRegistryManagerMock)
			requestMock.getLocale() >> new Locale("en_US")
			
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			sessionBeanMock.setRegistryTypesEvent("BirthDay")
			1 * bbbCatalogToolsMock.getRegistryTypeName(testObj.getSessionBean().getRegistryTypesEvent(), "BedBathUS") >> "eventType"
			RegistryInputVO registryInputVOMock = new RegistryInputVO(fieldName:"networkAffiliation",displayOnForm:FALSE,requiredInputCreate:FALSE,requiredToMakeRegPublic:TRUE)
			RegistryInputVO registryInputVOMock1 = new RegistryInputVO(fieldName:"eventDate",displayOnForm:TRUE,requiredInputCreate:FALSE,requiredToMakeRegPublic:FALSE)
			RegistryInputVO registryInputVOMock2 = new RegistryInputVO(fieldName:"CoRegistrantEmail",displayOnForm:TRUE,requiredInputCreate:TRUE,requiredToMakeRegPublic:TRUE)
			
			RegistryInputsByTypeVO registryInputsByTypeVOMock = new RegistryInputsByTypeVO(registryInputList:[registryInputVOMock,registryInputVOMock1,
				registryInputVOMock2])
			1 * simplifyRegistryManagerMock.getRegInputsByRegType("eventType") >> registryInputsByTypeVOMock
			
			AddressVO futureshippingAddressMock = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"70002")
			AddressVO addressVOMock1 = new AddressVO(firstName:"John",lastName:"Kennady",addressLine1:"alan Post Box",addressLine2:"alan office",city:"New Jercy",zip:"40001")
			ShippingVO shippingVOMock = new ShippingVO(shippingAddress:addressVOMock1,futureshippingAddress:futureshippingAddressMock,futureShippingDate:"")
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"",lastName:"",email:"")
			AddressVO addressVOMock = new AddressVO(addressLine1:"")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(firstName:"John",lastName:"Kennady",primaryPhone:"9898989898",cellPhone:"",contactAddress:addressVOMock)
			EventVO eventVOMock = new EventVO(babyNurseryTheme:"",eventDate:"",showerDate:"")
			RegistryVO registryVOMock = new RegistryVO(networkAffiliation:"",event:eventVOMock,primaryRegistrant:primaryRegistrantMock,coRegistrant:coRegistrantMock,
				shipping:shippingVOMock,registryId:null)
			testObj.setRegistryVO(registryVOMock)
			testObj.setShippingAddress("ShippingAddress")
			testObj.setFutureShippingDateSelected(null)
			testObj.setFutureShippingAddress("FutureShippingAddress")
			
		when:
			testObj.preCreateSimplifyRegistry(requestMock, responseMock)
		then:
			registryVOMock.getIsPublic().equals("0")
	}
	
	/* *************************************************************************************************************************************
	 * preCreateSimplifyRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleRemoveMultipleItemsForRegistry Method STARTS
	 *
	 * Signature : public boolean handleRemoveMultipleItemsForRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleRemoveMultipleItemsForRegistry. This TC is the Happy flow of handleRemoveMultipleItemsForRegistry method "(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString("firstString,secondString")
			1 * testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.isRemoveSingleItemFlag().equals(FALSE)
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when registryId is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setRegistryId('')
		
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('Null or empty registry id ', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_registry_id: Please provide a valid non-empty registry id in the input', null)
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when InputString is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString("")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			1 * testObj.logError('InputString is null', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('InputString is null', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: InputString is null', null)
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when first splitStringValue is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString(",secondString")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			1 * testObj.logError('Null or empty row id ', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_row_id: Please provide a valid non-empty row id in the input', null)
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when second splitStringValue is empty"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString("firstString,")
			requestMock.getLocale() >> new Locale("en_US")
		
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			1 * testObj.logError('Null or empty sku id ', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_sku_id: Please provide a valid non-empty sku id in the input', null)
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when third splitStringValue is greater than 0"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString("firstString,secondString,23511312")
			requestMock.getLocale() >> new Locale("en_US")
			
			1 * testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			testObj.isRemoveSingleItemFlag().equals(FALSE)
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when third splitStringValue is not greater than 0"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString("firstString,secondString,-5")
			requestMock.getLocale() >> new Locale("en_US")
			
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			1 * testObj.logError('invalid quantity ', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_quant_invalid', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
	}
	
	def"handleRemoveMultipleItemsForRegistry. This TC is when third splitStringValue throws NumberFormatException"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setRegistryId('REG_summary2351211$')
			testObj.setInputString("firstString,secondString,thirdString")
			requestMock.getLocale() >> new Locale("en_US")
			
		when:
			boolean results = testObj.handleRemoveMultipleItemsForRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			1 * testObj.logError('invalid quantity ', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_quant_invalid', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
	}
	
	/* *************************************************************************************************************************************
	 * handleRemoveMultipleItemsForRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleRemoveItemFromGiftRegistry Method STARTS
	 *
	 * Signature : public boolean handleRemoveItemFromGiftRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleRemoveItemFromGiftRegistry. This TC is the Happy flow of handleRemoveItemFromGiftRegistry method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
									
			//sessionUpdateForRemoveItem Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryResVO registryResVOMock = new RegistryResVO() 
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:3)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
		
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("2")
			testObj.getRegistryItemOperation().equals("removed")
			testObj.getRemovedProductId().equals("prod12345")
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			registrySummaryVOMock.getGiftRegistered().equals(1)
			testObj.getTotalGiftRegistered().equals(1)
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when registryId and registrySummaryVO's registryId is different"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('null&status=remove&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId',
				null, requestMock, responseMock) >> FALSE
			
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
						
			//sessionUpdateForRemoveItem Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary555555$',giftRegistered:3)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			RegistrySummaryVO registrySummaryVOMock1 = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo('REG_summary2351211$', "BedBathUS") >> registrySummaryVOMock1 
			
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("")
			testObj.getRegistryItemOperation().equals("removed")
			testObj.getRemovedProductId().equals("prod12345")
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock1)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when registrySummaryVO is null"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			testObj.setSuccessURL("/account/myaccount.jsp")
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('/account/myaccount.jsp&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId',
				null, requestMock, responseMock) >> FALSE
			
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
						
			//sessionUpdateForRemoveItem Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			sessionBeanMock.getValues().put("userRegistrysummaryVO", null)
			
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("")
			testObj.getRegistryItemOperation().equals("removed")
			testObj.getRemovedProductId().equals("prod12345")
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when RegistryId is not valid"(){
		given:
			this.spyRemoveItemformGR()
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", "235321321")
			
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile('p12345', '235321321', 'BedBathUS') >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect("/store/account/login.jsp&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				null, requestMock, responseMock) >> FALSE
			
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_biz_exception', null)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_transient_user: err_rem_regitem_transient_user property=null.shippingNewAddrLastName exception=null', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_biz_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1003: User type is transient Exception from preRemoveItemFromGiftRegistry of GiftRegistryFormHandler', null)
			1 * testObj.logError('BBBBusinessException from handleRemoveItemFromGiftRegistry of GiftRegistryFormHandler', null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when SuccessURL equals atg-rest-ignore-redirect and formerror occurs"(){
		given:
			this.spyRemoveItemformGR()
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setSuccessURL("atg-rest-ignore-redirect")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile('p12345', 'REG_summary2351211$', 'BedBathUS') >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('atg-rest-ignore-redirect', null, requestMock, responseMock) >> FALSE
			
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			1 * testObj.logError('giftregistry_1003: User type is transient Exception from preRemoveItemFromGiftRegistry of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_transient_user: err_rem_regitem_transient_user property=null.shippingNewAddrLastName exception=null', null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when errorId is 900 in errorRemoveItems method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:900)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
		
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("2")
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
			1 * testObj.logError('giftregistry_1094: Fatal error from errorRemoveItems of GiftRegistryFormHandler Webservice error id =900', null)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_sys_exception', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_sys_exception', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when errorId is 901 in errorRemoveItems method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:901)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
		
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("2")
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
			1 * testObj.logError('giftregistry_1095: Either user token or site flag invalid from errorRemoveItems of GiftRegistryFormHandler Webservice error id =901', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_sys_exception', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_sys_exception', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when errorId is 902 in errorRemoveItems method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:902)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
									
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("2")
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from handleRemoveItemFromGiftRegistry() of GiftRegistryFormHandler | webservice error code=902', null)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_sys_exception', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_invalid_input_format', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_sys_exception', 'en_us', null, null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when errorDisplayMessage is empty"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setRefNum("12352")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:902)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.quantity == "0" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
		
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("2")
			giftRegSessionBeanMock.getRegistryOperation().equals("removed")
			1 * testObj.logDebug('adding form exception: err_rem_regitem_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_sys_exception', 'en_us', null, null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_biz_exception', null)
			1 * testObj.logError('giftregistry_1009: BBBBusinessException from handleRemoveItemFromGiftRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_biz_exception', 'en_us', null, null)
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when RepositoryException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			testObj.getValue().put("SKU", "sku12345")
			testObj.getValue().put("PRODUCT_ID", "prod12345")
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}
									
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preRemoveItemFromGiftRegistry Private Method Coverage
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
		
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			thrown NullPointerException
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			testObj.getRegItemOldQty().equals("2")
	}
	
	def"handleRemoveItemFromGiftRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp", requestMock, responseMock) >> TRUE
						
			//populateRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			requestMock.setParameter(BBBCoreConstants.PROFILE, profileMock)
			1 * testObj.logDebug('adding form exception: err_rem_regitem_sys_exception', null)
			1 * testObj.logError('giftregistry_1008: BBBSystemException from handleRemoveItemFromGiftRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_rem_regitem_sys_exception', 'en_us', null, null)
	}

	private spyRemoveItemformGR() {
		testObj = Spy()
		testObj.setLoggingDebug(TRUE)
		testObj.setSiteContext(siteContextMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setGiftRegistryTools(giftRegistryToolsMock)
		testObj.setSessionBean(sessionBeanMock)
		testObj.setProfile(profileMock)
		testObj.setBbbCatalogTools(bbbCatalogToolsMock)
		testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		requestMock.getLocale() >> new Locale("en_US")
	}
	
	/* *************************************************************************************************************************************
	 * handleRemoveItemFromGiftRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleRemoveRegistryItems Method STARTS
	 *
	 * Signature : public boolean handleRemoveRegistryItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleRemoveRegistryItems. This TC is the Happy flow of handleRemoveRegistryItems method"() {
		given:
			testObj = Spy()
			testObj.setSkuId("sku12345")
			testObj.setUpdateRegistryId('REG_summary2351211$')
			testObj.setProductId("prod123456")
			testObj.setRowId("r12345")
			testObj.setRegItemOldQty("3")
			testObj.setItemTypes("ItemTypes")
			testObj.setLtlDeliveryServices("LTL")
			
			1 * testObj.handleRemoveItemFromGiftRegistry(requestMock, responseMock) >> TRUE
			
		when:
			boolean	results = testObj.handleRemoveRegistryItems(requestMock, responseMock)
		then:
			results == TRUE
			testObj.getValue().get("SKU").equals("sku12345")
			testObj.getValue().get("registryId").equals('REG_summary2351211$')
			testObj.getValue().get("PRODUCT_ID").equals("prod123456")
			testObj.getValue().get("regItemOldQty").equals("3")
			testObj.getValue().get("rowId").equals("r12345")
			testObj.getValue().get("itemTypes").equals("ItemTypes")
			testObj.getValue().get("ltlDeliveryServices").equals("LTL")
	}
	
	def"handleRemoveRegistryItems. This TC is the when validateUpdateRegistryItem method returns true"() {
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setSkuId("sku12345")
			testObj.setUpdateRegistryId('REG_summary2351211$')
			testObj.setProductId("prod123456")
			testObj.setRowId("")
			
		when:
			boolean	results = testObj.handleRemoveRegistryItems(requestMock, responseMock)
		then:
			results == FALSE
			1 * testObj.logError('Null or empty row id ', null)
			1 * testObj.logDebug('adding form exception: err_null_or_empty_row_id: Please provide a valid non-empty row id in the input', null)
	}
	
	/* *************************************************************************************************************************************
	 * handleRemoveRegistryItems Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleUpdateRegistryItem Method STARTS
	 *
	 * Signature : public boolean handleUpdateRegistryItem(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
		
	def"handleUpdateRegistryItem. This TC is the Happy flow of handleUpdateRegistryItem method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			testObj.getValue().put("regItemOldQty", "2")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.regItemOldQty == "2" && viewBean.quantity == "4" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp?siteId equals 0", requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
			
			//updateSessionObjInReg Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeName:"wedding")
			EventVO eventVOMock = new EventVO(eventDate:"01/02/2017")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,registryType:registryTypesMock)
			RegistryResVO registryResVOMock = new RegistryResVO(registryVO:registryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			testObj.setAlternateNum("8989898989")
			1 * testObj.handleUpdateRegistry(requestMock, responseMock) >> {}

			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("2")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			registryResVOMock.getRegistryVO().getPrimaryRegistrant().getCellPhone().equals("8989898989")
			registryResVOMock.getRegistryVO().getEvent().getBabyGender().equals(BBBCoreConstants.BLANK)
			registryResVOMock.getRegistryVO().getEvent().getEventDate().equals("01/02/2017")
			testObj.getRegistryVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("wedding")
			requestMock.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE)
			registrySummaryVOMock.getGiftRegistered().equals(7)
			
	}
	
	def"handleUpdateRegistryItem. This TC is when customizationOfferedFlag is not true"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			testObj.setUpdateDslFromModal(TRUE)
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setLtlDeliveryServices("LWA")
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "LW" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "Y" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.regItemOldQty == "0" && viewBean.quantity == "4" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('/store/checkout/shipping/shipping.jsp&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', 
				'/store/checkout/guest_checkout.jsp', requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
			
			//setItemInfoMapReg Private Method Coverage
			testObj.setItemPrice("25.99")
			RepositoryItem repositoryItemMock1 = Mock()
			1 * bbbCatalogToolsMock.getShippingMethod("LWA") >> repositoryItemMock1
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "ltlDelivery Services"
			1 * bbbCatalogToolsMock.getDeliveryCharge("BedBathUS", "sku12345", "LWA") >> 10d
			1 * bbbCatalogToolsMock.getAssemblyCharge("BedBathUS", "sku12345") >> 2d
			
			//updateSessionObjInReg Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			testObj.setAlternateNum("")

			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary36565465$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryServicesDesc").equals("Incl ltlDelivery Services")
			testObj.getUpdatedItemInfoMap().get("assemblySelected").equals("Y")
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryPrices").equals('$12.00')
			testObj.getUpdatedItemInfoMap().get("totalPrice").equals('$37.99')
			testObj.getUpdatedItemInfoMap().get("itemPrice").equals('$25.99')
	}
	
	def"handleUpdateRegistryItem. This TC is when customizationOfferedFlag is not true and userRegistrysummaryVO is null"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			testObj.setUpdateDslFromModal(TRUE)
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setLtlDeliveryServices(null)
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, {GiftRegistryViewBean viewBean -> viewBean.ltlDeliveryPrices == "" &&
				viewBean.ltlDeliveryServices == "" && viewBean.assemblyPrices == "" && viewBean.assemblySelections == "" && viewBean.itemTypes == "itemTypes" &&
				viewBean.regToken == "token1" && viewBean.registryId == 'REG_summary2351211$' && viewBean.rowId == "rid12345" && viewBean.refNum == "12352" &&
				viewBean.regItemOldQty == "0" && viewBean.quantity == "4" && viewBean.sku == "sku12345" && viewBean.siteFlag == "true" && viewBean.userToken == "token2" &&
				viewBean.serviceName == "serviceName" }) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('null&status=remove&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
			
			//setItemInfoMapReg Private Method Coverage
			testObj.setItemPrice(null)
			RepositoryItem repositoryItemMock1 = Mock()
			1 * bbbCatalogToolsMock.getShippingMethod(_) >> repositoryItemMock1
			repositoryItemMock1.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "ltlDelivery Services"
			1 * bbbCatalogToolsMock.getDeliveryCharge("BedBathUS", "sku12345", _) >> 0.0d
			
			//updateSessionObjInReg Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> null
			testObj.setAlternateNum("")
			sessionBeanMock.getValues().put("userRegistrysummaryVO", null)
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(null)
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryServicesDesc").equals("Incl ltlDelivery Services")
			testObj.getUpdatedItemInfoMap().get("assemblySelected").equals("")
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryPrices").equals("FREE")
			testObj.getUpdatedItemInfoMap().get("totalPrice").equals('$0.00')
			testObj.getUpdatedItemInfoMap().get("itemPrice").equals('$0.00')
	}
	
	def"handleUpdateRegistryItem. This TC is when errorId is 900 in manageItemErrorForReg Private method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> FALSE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			testObj.setUpdateDslFromModal(FALSE)
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setLtlDeliveryServices(null)
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:900)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('null&status=remove&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 900', null)
			1 * testObj.logError('giftregistry_1091: Fatal error format error from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=900', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
	}
	
	def"handleUpdateRegistryItem. This TC is when errorId is 901 in manageItemErrorForReg Private method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> FALSE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setLtlDeliveryServices(null)
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:901)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('null&status=remove&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * testObj.logError('giftregistry_1092: Either user token or site flag invalid from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=901', null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 901', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleUpdateRegistryItem. This TC is when errorId is 902 in manageItemErrorForReg Private method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> FALSE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setLtlDeliveryServices(null)
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:902)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('null&status=remove&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 902', null)
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=902', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.902 exception=null', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_invalid_input_format', 'en_us', null, null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleUpdateRegistryItem. This TC is when errorDisplayMessage is empty in manageItemErrorForReg Private method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> FALSE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setLtlDeliveryServices(null)
			testObj.setModifiedItemQuantity("4")
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			testObj.setUpdateRegItemsServiceName("serviceName")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:902)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock

			1 * testObj.checkFormRedirect('null&status=remove&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 902', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.902 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleUpdateRegistryItem. This TC is formError occurs when requestedQuantity is empty in validateRequestedQuantity method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"",regItemOldQty:"4",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('giftregistry_1064: Quantity is not valid from preUpdateAllItemToGiftRegistry of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
			
	}
	
	def"handleUpdateRegistryItem. This TC is formError occurs when purchasedQuantity is empty in validateRequestedQuantity method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"",quantity:"5",regItemOldQty:"4",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('giftregistry_1064: Quantity is not valid from preUpdateAllItemToGiftRegistry of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
			
	}
	
	def"handleUpdateRegistryItem. This TC is formError occurs when NumberFormatException thrown in validateRequestedQuantity method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"five",regItemOldQty:"4",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logError('giftregistry_1064: Quantity is not valid from preUpdateAllItemToGiftRegistry of GiftRegistryFormHandler', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1093: NumberFormatException from validateRequestedQuantity() of GiftRegistryFormHandler', _)
			
	}
	
	def"handleUpdateRegistryItem. This TC is when successURL is atg-rest-ignore-redirect"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setSuccessURL("atg-rest-ignore-redirect")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			1 * testObj.checkFormRedirect('atg-rest-ignore-redirect', null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
	}
	
	def"handleUpdateRegistryItem. This TC is when isTransient and success is TRUE"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			1 * testObj.checkFormRedirect('/store/account/login.jsp&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			
	}
	
	def"handleUpdateRegistryItem. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_update_regitem_biz_exception', null)
			1 * testObj.logError('giftregistry_1007: Update registry item BusinessException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_biz_exception', 'en_us', null, null)
	}
	
	def"handleUpdateRegistryItem. This TC is when BBBSystemException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			1 * testObj.checkFormRedirect(null, null, requestMock, responseMock) >> FALSE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleUpdateRegistryItem. This TC is when RepositoryException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> FALSE
			testObj.getValue().put("regItemOldQty", "")
			testObj.getValue().put("userToken", "token1")
			testObj.getValue().put("rowId", "rid12345")
			testObj.getValue().put("refNum", "")
			testObj.setDefaultRefNum("12352")
			testObj.getValue().put("itemTypes", "itemTypes")
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}
									
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//preUpdateItemToGiftRegistry Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
		
		when:
			boolean results = testObj.handleUpdateRegistryItem(requestMock, responseMock)
			
		then:
			results == FALSE
			thrown NullPointerException
			testObj.getRegItemOldQty().equals("")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			
	}
	
	/* *************************************************************************************************************************************
	 * handleUpdateRegistryItem Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleUpdateItemToGiftRegistry Method STARTS
	 *
	 * Signature : public boolean handleUpdateItemToGiftRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleUpdateItemToGiftRegistry. This TC is the Happy flow of handleUpdateItemToGiftRegistry Method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> FALSE
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setUpdateDslFromModal(TRUE)
			testObj.setItemTypes(null)
			testObj.setRefNum(null)
			repositoryItemMock.getPropertyValue(BBBCoreConstants.LTL_FLAG) >> FALSE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			1 * testObj.checkFormRedirect("/store/checkout/shipping/shipping.jsp?siteId equals 0&status=update&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId",
				"/store/checkout/guest_checkout.jsp?siteId equals 0", requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("spCheckoutLogin")
			testObj.setQueryParam("siteId equals 0")
			requestMock.getContextPath() >> "/store"
			Map<String, String> successUrlMapMock = ["spCheckoutLogin":"/checkout/shipping/shipping.jsp"]
			testObj.setSuccessUrlMap(successUrlMapMock)
			Map<String, String> errorUrlMapMock = ["spCheckoutLogin":"/checkout/guest_checkout.jsp"]
			testObj.setErrorUrlMap(errorUrlMapMock)
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:null)
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"4",sku:"sku12345",rowId:"rowId")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["1","5"]
			
			//setItemInfoMapGiftReg Private Method Coverage
			testObj.setItemPrice("23.99")
			RepositoryItem repositoryItemMock1 = Mock() 
			1 * bbbCatalogToolsMock.getShippingMethod(_) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "Delivery Service"
			1 * bbbCatalogToolsMock.getDeliveryCharge("BedBathUS", "sku12345", _) >> 0.0d
			
			//updateAlternateNumInGiftReg Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			1 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeName:"Wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"10/10/2030")
			EventVO eventVOMock = new EventVO(eventDate:"01/02/2017",showerDate:"05/01/2017",birthDate:"10/09/1992")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,shipping:shippingVOMock,registryType:registryTypesMock)
			RegistryResVO registryResVOMock = new RegistryResVO(registryVO:registryVOMock)
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			
			ShippingVO shippingVOMock1 = new ShippingVO(futureShippingDate:"10/10/2030")
			RegistryVO registryVOMock1 = new RegistryVO(shipping:shippingVOMock1)
			testObj.setRegistryVO(registryVOMock1)
			
			testObj.setAlternateNum("8787878787")
			1 * testObj.handleUpdateRegistry(requestMock, responseMock) >> {}
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryServicesDesc").equals("Delivery Service")
			testObj.getUpdatedItemInfoMap().get("assemblySelected").equals("")
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryPrices").equals("FREE")
			testObj.getUpdatedItemInfoMap().get("totalPrice").equals('$23.99')
			testObj.getUpdatedItemInfoMap().get("itemPrice").equals('$23.99')
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			registryResVOMock.getRegistryVO().getPrimaryRegistrant().getCellPhone().equals("8787878787")
			registryResVOMock.getRegistryVO().getEvent().getBabyGender().equals(BBBCoreConstants.BLANK)
			registryResVOMock.getRegistryVO().getEvent().getEventDate().equals("01/02/2017")
			registryResVOMock.getRegistryVO().getEvent().getShowerDate().equals("05/01/2017")
			registryResVOMock.getRegistryVO().getEvent().getBirthDate().equals("10/09/1992")
			registryResVOMock.getRegistryVO().getShipping().getFutureShippingDate().equals("10/10/2030")
			testObj.getRegistryVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("Wedding")
			requestMock.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when ltlDeliveryServices is LWA and deliveryCharge has value"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("atg-rest-ignore-redirect")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(TRUE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			testObj.setUpdateDslFromModal(TRUE)
			testObj.setItemTypes("")
			testObj.setRefNum("")
			repositoryItemMock.getPropertyValue(BBBCoreConstants.LTL_FLAG) >> TRUE
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			1 * bbbCatalogToolsMock.getAssemblyCharge(_, "sku12345") >> 12.22d
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('atg-rest-ignore-redirect', null , requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"")
			GiftRegistryViewBean giftRegistryViewBeanMock2 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku86562",rowId:null,ltlDeliveryServices:"")
			GiftRegistryViewBean giftRegistryViewBeanMock1 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku12345",
				rowId:"row85898",ltlDeliveryServices:"LWA",ltlDeliveryPrices:"prices")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock1,giftRegistryViewBeanMock2])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["1","5"]
			
			//setItemInfoMapGiftReg Private Method Coverage
			testObj.setItemPrice("23.99")
			RepositoryItem repositoryItemMock1 = Mock()
			1 * bbbCatalogToolsMock.getShippingMethod("deliveryServices") >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "Delivery Service"
			1 * bbbCatalogToolsMock.getDeliveryCharge("BedBathUS", "sku12345", "deliveryServices") >> 5.0d
			1 * bbbCatalogToolsMock.getAssemblyCharge("BedBathUS", "sku12345") >> 10d 
			
			//updateAlternateNumInGiftReg Private Method Coverage
			1 * giftRegistryToolsMock.invalidateRegistry(_)
			2 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeName:"Wedding")
			ShippingVO shippingVOMock = new ShippingVO(futureShippingDate:"")
			EventVO eventVOMock = new EventVO(eventDate:"",showerDate:"",birthDate:"")
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,shipping:shippingVOMock,registryType:registryTypesMock)
			RegistryResVO registryResVOMock = new RegistryResVO(registryVO:registryVOMock)
			
			ShippingVO shippingVOMock1 = new ShippingVO(futureShippingDate:"")
			RegistryVO registryVOMock1 = new RegistryVO(shipping:shippingVOMock1)
			testObj.setRegistryVO(registryVOMock1)
			
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			testObj.setAlternateNum("8787878787")
			1 * testObj.handleUpdateRegistry(requestMock, responseMock) >> {}
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryServicesDesc").equals("Delivery Service With Assembly")
			testObj.getUpdatedItemInfoMap().get("assemblySelected").equals("Y")
			testObj.getUpdatedItemInfoMap().get("ltlDeliveryPrices").equals('$15.00')
			testObj.getUpdatedItemInfoMap().get("totalPrice").equals('$38.99')
			testObj.getUpdatedItemInfoMap().get("itemPrice").equals('$23.99')
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
			registryResVOMock.getRegistryVO().getPrimaryRegistrant().getCellPhone().equals("8787878787")
			registryResVOMock.getRegistryVO().getEvent().getBabyGender().equals(BBBCoreConstants.BLANK)
			testObj.getRegistryVO().equals(registryVOMock)
			sessionBeanMock.getRegistryTypesEvent().equals("Wedding")
			requestMock.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE)
			testObj.getTotalGiftRegistered().equals(5)
			registrySummaryVOMock.getGiftRegistered().equals(5)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when customizationOfferedFlag is false and true, alternateNum is empty"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			2 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku56325", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			RepositoryItem repositoryItemMock2 = Mock()
			1 * mutableRepositoryMock.getItem("sku86562", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock2
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> FALSE
			repositoryItemMock2.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setUpdateDslFromModal(FALSE)
			testObj.setItemTypes("itemTypes1")
			testObj.setRefNum("")
			
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			2 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('&status=update&status=update&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null , requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:null,sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			GiftRegistryViewBean giftRegistryViewBeanMock2 = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:null,regItemOldQty:"5",sku:"sku86562",rowId:"row8559",ltlDeliveryServices:"ltlservice")
			testObj.setViewBeans([giftRegistryViewBeanMock,giftRegistryViewBeanMock2])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["7","1"]
			
			//updateAlternateNumInGiftReg Private Method Coverage
			2 * giftRegistryToolsMock.invalidateRegistry(_)
			2 * giftRegistryToolsMock.invalidateRegistryCache(_)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistryResVO registryResVOMock = new RegistryResVO()
			2 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin('REG_summary2351211$', "BedBathUS") >> registryResVOMock
			testObj.setAlternateNum("")
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getValues().get('REG_summary2351211$_REG_SUMMARY').equals(registryResVOMock)
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when errorId is 900 and manageItemErrorForGiftReg method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku56325", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setUpdateDslFromModal(FALSE)
			testObj.setItemTypes("")
			testObj.setRefNum("")
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:900)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('&status=update&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: err_gift_reg_fatal_error', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.900 exception=null', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_fatal_error', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1091: Fatal error format error from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=900', null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 900', null)
	}
	
	
	def"handleUpdateItemToGiftRegistry. This TC is when errorId is 901 and manageItemErrorForGiftReg method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku56325", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setUpdateDslFromModal(FALSE)
			testObj.setItemTypes("")
			testObj.setRefNum("12165")
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:901)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('&status=update&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.901 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_siteflag_usertoken_error', null)
			1 * testObj.logError('giftregistry_1092: Either user token or site flag invalid from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=901', null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 901', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_siteflag_usertoken_error', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when errorId is 902 and manageItemErrorForGiftReg method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku56325", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setUpdateDslFromModal(FALSE)
			testObj.setItemTypes("")
			testObj.setRefNum("12165")
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"ErrorMessage",errorId:902)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('&status=update&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.902 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_gift_reg_invalid_input_format', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code=902', null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 902', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_gift_reg_invalid_input_format', 'en_us', null, null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when errorDisplayMessage is empty and manageItemErrorForGiftReg method"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			testObj.getValue().put("SKU", "sku12345")
			1 * bbbCatalogToolsMock.isSkuLtl("BedBathUS", "sku12345") >> TRUE
			1 * Nucleus.getGlobalNucleus().resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH) >> mutableRepositoryMock
			1 * mutableRepositoryMock.getItem("sku56325", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getValue().put("itemTypes", "itemTypes")
			repositoryItemMock.getPropertyValue("customizationOfferedFlag") >> TRUE
			testObj.setUpdateDslFromModal(FALSE)
			testObj.setItemTypes("")
			testObj.setRefNum("12165")
			
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["true"]
			1 * bbbCatalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["token2"]
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:902)
			ManageRegItemsResVO manageRegItemsResVOMock = new ManageRegItemsResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.removeUpdateGiftRegistryItem(profileMock, _) >> manageRegItemsResVOMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:'REG_summary2351211$',giftRegistered:5)
			sessionBeanMock.getValues().put("userRegistrysummaryVO", registrySummaryVOMock)
			
			1 * testObj.checkFormRedirect('&status=update&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletFormException property=null.902 exception=null', null)
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is 902', null)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			1 * testObj.checkFormRedirect('', null, requestMock, responseMock) >> FALSE
			 
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_update_regitem_biz_exception', null)
			1 * testObj.logError('giftregistry_1007: Update registry item BusinessException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler', _)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_biz_exception', 'en_us', null, null)
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when BBBSystemException thrown"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			1 * testObj.checkFormRedirect('', null, requestMock, responseMock) >> FALSE
			 
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: err_update_regitem_sys_exception', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_sys_exception', 'en_us', null, null)
			1 * testObj.logError('giftregistry_1006: Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler', _)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when registryId is not valid"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", '235145342')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			1 * testObj.checkFormRedirect('', null, requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", "235145342", "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.logError('giftregistry_20132: Either Items rowIDs, quanties or skus are blank', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quantities_or_skus_invalid', 'en_us', null, null)
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when isTransient is true and success is true"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('/store/account/login.jsp&dataScrollTop=dataScrollTop&dataAccordionId=dataAccordionId', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when isTransient is true and succesUrl is atg-rest-ignore-redirect"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("atg-rest-ignore-redirect")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(FALSE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('atg-rest-ignore-redirect', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"5",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when requestedQuantity is in negative value"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("atg-rest-ignore-redirect")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(TRUE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('atg-rest-ignore-redirect', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"-4",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.logError('giftregistry_1064: Quantity is not valid from preUpdateAllItemToGiftRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
	}
	
	def"handleUpdateItemToGiftRegistry. This TC is when requestedQuantity is greater than 99"(){
		given:
			this.spyRemoveItemformGR()
			GiftRegSessionBean giftRegSessionBeanMock = new GiftRegSessionBean()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setLoggedInFailureURL("/store/account/login.jsp")
			
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID) >> "dataAccordionId"
			requestMock.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP) >> "dataScrollTop"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			testObj.setSuccessURL("atg-rest-ignore-redirect")
			testObj.setLtlDeliveryServices("deliveryServices")
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			testObj.getValue().put("registryId", 'REG_summary2351211$')
			testObj.setRemoveSingleItemFlag(TRUE)
			requestMock.getObjectParameter(BBBCoreConstants.PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			1 * testObj.checkFormRedirect('atg-rest-ignore-redirect', null,
				 requestMock, responseMock) >> TRUE
						
			//setRedirectUrls Private Method Coverage
			testObj.setFromPage("")
			
			//isRegistryOwnedByProfile Private Method Coverage
			profileMock.getRepositoryId() >> "p12345"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12345", 'REG_summary2351211$', "BedBathUS") >> TRUE
			
			//findModifiedItemsBeans Private Method Coverage
			GiftRegistryViewBean giftRegistryViewBeanMock = new GiftRegistryViewBean(purchasedQuantity:"5",quantity:"100",regItemOldQty:"5",sku:"sku56325",rowId:"row12345",ltlDeliveryServices:"service")
			testObj.setViewBeans([giftRegistryViewBeanMock])
			
			//getUpdateBatchSize Public Method Coverage
			3 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["5"]
			
		when:
			boolean results = testObj.handleUpdateItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			1 * testObj.logDebug('adding form exception: atg.droplet.DropletException', null)
			1 * testObj.logError('giftregistry_1064: Quantity is not valid from preUpdateAllItemToGiftRegistry of GiftRegistryFormHandler', null)
			1 * lblTxtTemplateManagerMock.getErrMsg('err_update_regitem_quant_invalid', 'en_us', null, null)
	}
	
	
	/* *************************************************************************************************************************************
	 * handleUpdateItemToGiftRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	def"getUpdateBatchSize. This TC is when listKeys are empty"(){
		given:
			this.spyRemoveItemformGR()
			1 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> []
			
		when:
			int results = testObj.getUpdateBatchSize()
			
		then:
			results == 0
	}
	
	def"getUpdateBatchSize. This TC is when listKeys is null"(){
		given:
			this.spyRemoveItemformGR()
			1 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> [null]
			
		when:
			int results = testObj.getUpdateBatchSize()
			
		then:
			results == 0
	}
	
	def"getUpdateBatchSize. This TC is when BBBSystemException thrown"(){
		given:
			this.spyRemoveItemformGR()
			1 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			int results = testObj.getUpdateBatchSize()
			
		then:
			results == 0
			1 * testObj.logError('BBBSystemException - registry_update_batchsize key not found for sitecom.bbb.exception.BBBSystemException: Mock for BBBSystemException', null)
	}
	
	def"getUpdateBatchSize. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyRemoveItemformGR()
			1 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			int results = testObj.getUpdateBatchSize()
			
		then:
			results == 0
			1 * testObj.logError('BBBSystemException - registry_update_batchsize key not found for sitecom.bbb.exception.BBBBusinessException: Mock for BBBBusinessException', null)
	}
	
	def"getUpdateBatchSize. This TC is when NumberFormatException thrown"(){
		given:
			this.spyRemoveItemformGR()
			1 * bbbCatalogToolsMock.getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize") >> ["batch"]
			
		when:
			int results = testObj.getUpdateBatchSize()
			
		then:
			results == 0
			1 * testObj.logError('NumberFormatException - registry_update_batchsize value format exceptionjava.lang.NumberFormatException: For input string: "batch"', null)
	}
	
	
	/* *************************************************************************************************************************************
	 * sendCoregistrantEmail Method STARTS
	 *
	 * Signature : public void sendCoregistrantEmail(final DynamoHttpServletRequest pRequest, final String siteId)
	 *
	 * *************************************************************************************************************************************/
	
	def"sendCoregistrantEmail. This TC is the Happy flow of sendCoregistrantEmail method"(){
		given:
			sessionBeanMock.setRegistryTypesEvent("BRD")
			siteContextMock.getSite() >> siteMock
			siteMock.getPropertyValue("defaultCountry") >> "US"
			
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:"John")
			EventVO eventVOMock = new EventVO(eventDate:"12/01/2017")
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$',event:eventVOMock,coRegistrant:coRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setCoRegEmailFoundPopupStatus("found")
			testObj.setCoRegEmailNotFoundPopupStatus("notFound")
			
			1 * giftRegistryManagerMock.giftRegistryRepoUpdate(testObj.getRegistryVO(), "found", "notFound")
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(testObj.getRegistryVO(), testObj.getShippingAddress(), testObj.getFutureShippingAddress(), 
				testObj.getProfile(),"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "coRegistrySubject"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("store/giftregistry/view_registry_guest.jsp?registryId=")
			
			1 * giftRegistryManagerMock.sendEmailToCoregistrant('http://store/giftregistry/view_registry_guest.jsp?registryId=REG_summary2351211$&eventType=', 
				'http:///store/account/login.jsp', 'BedBathUS', 'coRegistrySubject', registryVOMock, "found", "notFound", giftRegEmailInfoMock)
			
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo('REG_summary2351211$', "BedBathUS") >> registrySummaryVOMock

						
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> []
			
						
		when:
			testObj.sendCoregistrantEmail(requestMock, "BedBathUS")
		then:
			testObj.getRegistryVO().getRegistryType().getRegistryTypeName().equals("BRD")
			registrySummaryVOMock.getEventDate().equals("12/01/2017")
			registrySummaryVOMock.getCoRegistrantFirstName().equals("John")
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			
	}
	
	def"sendCoregistrantEmail. This TC is when BBBSystemException thrown in getHost method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setGiftRegEmailInfo(giftRegEmailInfoMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			
			sessionBeanMock.setRegistryTypesEvent(null)
			siteContextMock.getSite() >> siteMock
			siteMock.getPropertyValue("defaultCountry") >> "US"
			
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null)
			EventVO eventVOMock = new EventVO(eventDate:"12/01/2017")
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$',event:eventVOMock,coRegistrant:coRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setCoRegEmailFoundPopupStatus("found")
			testObj.setCoRegEmailNotFoundPopupStatus("notFound")
			
			1 * giftRegistryManagerMock.giftRegistryRepoUpdate(testObj.getRegistryVO(), "found", "notFound")
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(testObj.getRegistryVO(), testObj.getShippingAddress(), testObj.getFutureShippingAddress(),
				testObj.getProfile(),"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "coRegistrySubject"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("store/giftregistry/view_registry_guest.jsp?registryId=")
			
			1 * giftRegistryManagerMock.sendEmailToCoregistrant('http://store/giftregistry/view_registry_guest.jsp?registryId=REG_summary2351211$&eventType=', 
				'http://null', 'BedBathUS', 'coRegistrySubject', registryVOMock, "found", "notFound", giftRegEmailInfoMock)
			
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo('REG_summary2351211$', "BedBathUS") >> registrySummaryVOMock

						
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.sendCoregistrantEmail(requestMock, "BedBathUS")
		then:
			registrySummaryVOMock.getEventDate().equals("12/01/2017")
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			2 * testObj.logError('GiftRegistryFormHandler.getHost :: System Exception occured while fetching config value for config key requestDomainNameconfig type MobileWebConfigcom.bbb.exception.BBBSystemException: Mock for BBBSystemException', null)
			
	}
	
	def"sendCoregistrantEmail. This TC is when BBBBusinessException thrown in getHost method"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setGiftRegEmailInfo(giftRegEmailInfoMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			
			sessionBeanMock.setRegistryTypesEvent(null)
			siteContextMock.getSite() >> siteMock
			siteMock.getPropertyValue("defaultCountry") >> "US"
			
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null)
			EventVO eventVOMock = new EventVO(eventDate:"12/01/2017")
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$',event:eventVOMock,coRegistrant:coRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setCoRegEmailFoundPopupStatus("found")
			testObj.setCoRegEmailNotFoundPopupStatus("notFound")
			
			1 * giftRegistryManagerMock.giftRegistryRepoUpdate(testObj.getRegistryVO(), "found", "notFound")
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(testObj.getRegistryVO(), testObj.getShippingAddress(), testObj.getFutureShippingAddress(),
				testObj.getProfile(),"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "coRegistrySubject"
			requestMock.getScheme() >> "http"
			testObj.setGuestRegistryUri("store/giftregistry/view_registry_guest.jsp?registryId=")
			
			1 * giftRegistryManagerMock.sendEmailToCoregistrant('http://store/giftregistry/view_registry_guest.jsp?registryId=REG_summary2351211$&eventType=',
				'http://null', 'BedBathUS', 'coRegistrySubject', registryVOMock, "found", "notFound", giftRegEmailInfoMock)
			
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo('REG_summary2351211$', "BedBathUS") >> registrySummaryVOMock

						
			//getHost Private Method Coverage
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			2 * bbbCatalogToolsMock.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.sendCoregistrantEmail(requestMock, "BedBathUS")
		then:
			registrySummaryVOMock.getEventDate().equals("12/01/2017")
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			2 * testObj.logError('GiftRegistryFormHandler.getHost :: Business Exception occured while fetching config value for config key requestDomainNameconfig type MobileWebConfigcom.bbb.exception.BBBBusinessException: Mock for BBBBusinessException', null)
			
	}
	
	def"sendCoregistrantEmail. This TC is when siteId starts with TBS"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setSessionBean(sessionBeanMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			testObj.setGiftRegEmailInfo(giftRegEmailInfoMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			
			sessionBeanMock.setRegistryTypesEvent(null)
			siteContextMock.getSite() >> siteMock
			siteMock.getPropertyValue("defaultCountry") >> "US"
			
			RegistrantVO coRegistrantMock = new RegistrantVO(firstName:null)
			EventVO eventVOMock = new EventVO(eventDate:"12/01/2017")
			RegistryVO registryVOMock = new RegistryVO(registryId:'REG_summary2351211$',event:eventVOMock,coRegistrant:coRegistrantMock)
			testObj.setRegistryVO(registryVOMock)
			testObj.setCoRegEmailFoundPopupStatus("found")
			testObj.setCoRegEmailNotFoundPopupStatus("notFound")
			
			1 * giftRegistryManagerMock.giftRegistryRepoUpdate(testObj.getRegistryVO(), "found", "notFound")
			1 * giftRegistryManagerMock.updateRegistrantProfileInfo(testObj.getRegistryVO(), testObj.getShippingAddress(), testObj.getFutureShippingAddress(),
				testObj.getProfile(),"US")
			requestMock.getLocale() >> new Locale("en_US")
			1 * lblTxtTemplateManagerMock.getPageLabel("lbl_email_co_registry_subject", requestMock.getLocale().getLanguage(), null, null) >> "coRegistrySubject"
			testObj.setTbsEmailSiteMap(["TBS_BedBathUS":"www.tbsbaby.com"])
			testObj.setGuestRegistryUri("store/giftregistry/view_registry_guest.jsp?registryId=")
			
			1 * giftRegistryManagerMock.sendEmailToCoregistrant('www.tbsbaby.comstore/giftregistry/view_registry_guest.jsp?registryId=', 
				'www.tbsbaby.comnull', 'TBS_BedBathUS', 'coRegistrySubject', registryVOMock, "found", "notFound", giftRegEmailInfoMock)
			
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			1 * giftRegistryManagerMock.getRegistryInfo('REG_summary2351211$', "TBS_BedBathUS") >> registrySummaryVOMock
			
		when:
			testObj.sendCoregistrantEmail(requestMock, "TBS_BedBathUS")
		then:
			registrySummaryVOMock.getEventDate().equals("12/01/2017")
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			
	}
	
	
	/* *************************************************************************************************************************************
	 * sendCoregistrantEmail Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleAddAllItemsToGiftRegistry Method STARTS
	 *
	 * Signature : public boolean handleAddAllItemsToGiftRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleAddAllItemsToGiftRegistry. This TC is the Happy flow of handleAddAllItemsToGiftRegistry method"(){
		given:
			testObj = Spy()
			testObj.setSessionBean(sessionBeanMock)
			1 * testObj.handleAddItemToGiftRegistry(requestMock, responseMock) >> {}
			testObj.setCcFlag(TRUE)
		
		when:
			boolean results = testObj.handleAddAllItemsToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			sessionBeanMock.getAddALLActgion().equals("AddAllItems")
			testObj.getKickStarterAddAllAction().equals("AddAllItems")
	}
	
	
	/* *************************************************************************************************************************************
	 * handleAddAllItemsToGiftRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * recommendationHandling Method STARTS
	 *
	 * Signature : public boolean recommendationHandling(DynamoHttpServletRequest request, DynamoHttpServletResponse response, Profile profile)
	 *
	 * *************************************************************************************************************************************/
	
	def"recommendationHandling. This TC is the Happy flow of handleAddAllItemsToGiftRegistry method"(){
		given:
			testObj.setRegId('REG_summary2351211$')
			testObj.setSkuId("SKU12345")
			testObj.setComment("Comment")
			testObj.setRecommendedQuantity("5")
			profileMock.getRepositoryId() >> "p12345"
			1 * requestMock.setParameter("registryId", "")
			Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager") >> giftRegistryRecommendationManagerMock
			1 * requestMock.resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager")
			
			1 * giftRegistryRecommendationManagerMock.createRegistryRecommendationProduct('REG_summary2351211$', "SKU12345", "Comment", 5 , "p12345")
		
		when:
			boolean results = testObj.recommendationHandling(requestMock, responseMock, profileMock)
			
		then:
			results == TRUE
			
	}
	
	def"recommendationHandling. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setRegId('REG_summary2351211$')
			testObj.setSkuId("SKU12345")
			testObj.setComment("Comment")
			testObj.setRecommendedQuantity("5")
			profileMock.getRepositoryId() >> "p12345"
			1 * requestMock.setParameter("registryId", "")
			Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager") >> giftRegistryRecommendationManagerMock
			1 * requestMock.resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager")
			
			1 * giftRegistryRecommendationManagerMock.createRegistryRecommendationProduct('REG_summary2351211$', "SKU12345", "Comment", 5 , "p12345") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			boolean results = testObj.recommendationHandling(requestMock, responseMock, profileMock)
			
		then:
			results == TRUE
			1 * testObj.logError('Error while adding recommmendation\'sMock for BBBSystemException', _)
			1 * testObj.logDebug('registry id= REG_summary2351211$skuid= SKU12345Quantity= 5recommenderProfileId= p12345comment= Comment', null)
			1 * testObj.logDebug('adding form exception: There was an error while adding recommendation\'s: err_adding_recommender_biz_exception', null)
	}
	
	
	/* *************************************************************************************************************************************
	 * recommendationHandling Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	/* *************************************************************************************************************************************
	 * handleAddItemToGiftRegistry Method STARTS
	 *
	 * Signature : public boolean handleAddItemToGiftRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	 *
	 * *************************************************************************************************************************************/
	
	def"handleAddItemToGiftRegistry. This TC is when RecommededFlag is true"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			requestMock.getParameter("addAll") >> "addAllRegItems"
			testObj.setJasonCollectionObj(null)
			testObj.setRecommededFlag("true")
			1 * testObj.recommendationHandling(requestMock, responseMock, profileMock) >> {}
			testObj.setRecomPopUpSuccessURL("/account/order_registration.jsp")
			testObj.setRecomPopUpErrorURL("/account/personalinfo.jsp") 
			1 * testObj.checkFormRedirect("/account/order_registration.jsp", "/account/personalinfo.jsp", requestMock, responseMock) >> TRUE
		
		when:
			boolean results = testObj.handleAddItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	def"handleAddItemToGiftRegistry. This TC is when AlternateNum is not valid"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			requestMock.getParameter("addAll") >> "addAllRegItems"
			testObj.setJasonCollectionObj('altNumber:0898989john\"')
			testObj.setRecommededFlag("false")
		
		when:
			boolean results = testObj.handleAddItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
			testObj.isErrorFlagAddItemToRegistry().equals(TRUE)
			1 * testObj.logDebug('adding form exception: Invalid phone number: Invalid phone number', null)
	}
	
	def"handleAddItemToGiftRegistry. This TC is when returnValue is false"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			requestMock.getParameter("addAll") >> "addAllRegItems"
			testObj.setJasonCollectionObj('isFromPendingTab:09898989898\"')
			testObj.setRecommededFlag("false")
			testObj.setKickStarterAddAllAction(null)
			profileMock.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE) >> "US"
			profileMock.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE) >> "dollar"
			profileMock.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT) >> TRUE
			
			1 * giftRegistryToolsMock.addItemJSONObjectParser(_, testObj.getJasonCollectionObj())
			
			1 * giftRegistryRecommendationManagerMock.declinePendingItems(_) >> FALSE
			
		when:
			boolean results = testObj.handleAddItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == FALSE
			1 * testObj.logDebug('adding form exception: Profile not found or some system error occurred: err_fetching_profile', null)
			1 * testObj.logError('Profile not found or some system error occurred', null)
	}
	
	def"handleAddItemToGiftRegistry. This TC is when returnValue is true"(){
		given:
			testObj = Spy()
			testObj.setLoggingDebug(TRUE)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setGiftRegistryTools(giftRegistryToolsMock)
			testObj.setGiftRegistryRecommendationManager(giftRegistryRecommendationManagerMock)
			
			requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
			requestMock.getParameter("addAll") >> "addAllRegItems"
			testObj.setJasonCollectionObj('isFromPendingTab:09898989898\"')
			testObj.setRecommededFlag("false")
			testObj.setKickStarterAddAllAction(null)
			profileMock.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE) >> "US"
			profileMock.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE) >> "dollar"
			profileMock.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT) >> TRUE
			
			1 * giftRegistryToolsMock.addItemJSONObjectParser(_, testObj.getJasonCollectionObj())
			
			1 * giftRegistryRecommendationManagerMock.declinePendingItems(_) >> TRUE
			
		when:
			boolean results = testObj.handleAddItemToGiftRegistry(requestMock, responseMock)
			
		then:
			results == TRUE
	}
	
	/* *************************************************************************************************************************************
	 * handleAddItemToGiftRegistry Method ENDS
	 *
	 * *************************************************************************************************************************************/
	
	
}
