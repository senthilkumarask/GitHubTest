package com.bbb.commerce.giftregistry.droplet

import atg.userprofiling.Profile

import com.bbb.commerce.checklist.vo.NonRegistryGuideVO
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import java.text.ParseException
import java.text.SimpleDateFormat

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class SelectHeaderDropletSpecification extends BBBExtendedSpec {
	
	SelectHeaderDroplet testObj
	GiftRegSessionBean giftRegSessionBeanMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	Profile profileMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new SelectHeaderDroplet(giftRegSessionBean:giftRegSessionBeanMock,
			pagesWithGenericHeadOnly:["/store/giftregistry/view_registry_owner.jsp","/store/giftregistry/view_registry_guest.jsp","/store/page/Bridal"],
			pagesWithGenOrPersistentHead:["/store/_includes/header/subheader.jsp","/store/_includes/header/persistent_subheader.jsp","/store/_ajax/hideNonRegistryGuideSuccessJson.jsp","/store/includes/dynamic_header.jsp"],
			pagesWithPersistentOrNoHead:["/store/giftregistry/kickstarters/shop_the_look_details.jsp","/store/giftregistry/kickstarters/top_consultants_picks.jsp","/store/printCards/printCardsLanding.jsp",
				"/store/bbregistry/bridal_book.jsp","/store/bbregistry/registry_features.jsp","/store/printCards/printCards.jsp","/store/giftregistry/view_kickstarters.jsp",
				"/store/bbregistry/baby_landing.jsp"],
			pagesWithGenericHeadBabyOnly:["/store/giftregistry/registry_creation_confirmation.jsp"],
			refererPagesWithPersistentOrNoHead:["/store/category/","/store/product/","/store/browse/product_details.jsp","/store/brand/","/store/s/","/store/shopthislook/","/store/topconsultant/",
				 "/store/compare/product_comparison.jsp","/store/registry/","/store/printCards/printCardsLanding.jsp","/store/printCards/printCards.jsp","/store/kickstarters/",
				 "/store/wishlist/wish_list.jsp","/store/wishlist/WishList","/store/bbregistry/BridalBook","/store/page/BabyRegistry","/store/page/Registry","/store/static","/store/checklist"],
			pagesWithAjaxCall:["/store/browse/category.jsp","/store/browse/product_details.jsp"])
	}
	
	def"service method. This TC is when pageURI equals view_registry_owner.jsp"(){
		given:
			requestMock.getRequestURI() >> "/store/giftregistry/view_registry_owner.jsp"
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> "bedbathandbeyond.com/store/selfservice/FindStore"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:"20171201")
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO() 
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> TRUE
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			registrySummaryVOMock.getEventDate().equals("12/01/2017")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			1 * requestMock.setParameter("registryOwnerView",BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("registryListViewUri",BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showGenericHeader")
			1 * requestMock.setParameter("registrySummaryVO", registrySummaryVOMock)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service method. This TC is when pageURI equals view_registry_guest.jsp and registrySummaryVO is null"(){
		given:
			requestMock.getRequestURI() >> "/store/giftregistry/view_registry_guest.jsp"
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> "bedbathandbeyond.com/store/selfservice/FindStore"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> TRUE
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("registryListViewUri",BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showGenericHeader")
			1 * requestMock.setParameter("registrySummaryVO", null)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service method. This TC is when pageURI equals page/Bridal and nonRegistryGuideVOs is empty"(){
		given:
			requestMock.getRequestURI() >> "/store/page/Bridal"
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> "bedbathandbeyond.com/store/selfservice/FindStore"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> TRUE
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showGenericHeader")
			1 * requestMock.setParameter("registrySummaryVO", null)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service method. This TC is when refererURI equals view_registry_owner.jsp"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "/store/giftregistry/view_registry_owner.jsp"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * requestMock.setParameter("registryOwnerView",BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("registryListViewUri",BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showGenericHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when refererURI is null"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = null
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when refererURI is view_registry_guest.jsp"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/giftregistry/view_registry_guest.jsp"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('registryListViewUri', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is registry/RegistryChecklist"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "registry/RegistryChecklist"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is registry/GuidesAndAdviceLandingPage"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "registry/GuidesAndAdviceLandingPage"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is registry/RegistryIncentives"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "registry/RegistryIncentives"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is registry/RegistryFeatures"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "registry/RegistryFeatures"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is printCards/printCardsLanding"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "printCards/printCardsLanding"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is bbregistry/BridalBook"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "bbregistry/BridalBook"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is printCards/printCards"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "printCards/printCards"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is kickstarters"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "kickstarters"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is hideNonRegistryGuideSuccessJson.jsp and refererURI is registry/PersonalizedInvitations"(){
		given:
			String pageURI = "/store/_ajax/hideNonRegistryGuideSuccessJson.jsp"
			String refererURI = "registry/PersonalizedInvitations"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('showStaticHeader', 'true')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is registry/RegistryChecklist"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "registry/RegistryChecklist"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is registry/GuidesAndAdviceLandingPage"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "registry/GuidesAndAdviceLandingPage"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is registry/RegistryIncentives"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "registry/RegistryIncentives"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is registry/RegistryFeatures"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "registry/RegistryFeatures"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is printCards/printCardsLanding"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "printCards/printCardsLanding"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is bbregistry/BridalBook"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "bbregistry/BridalBook"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is printCards/printCards"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "printCards/printCards"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is kickstarters"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "kickstarters"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false, registrySummaryVO is null and refererURI is registry/PersonalizedInvitations"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "registry/PersonalizedInvitations"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			1 * profileMock.isTransient() >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(TRUE)
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}

	def"service method. This TC is when isTransient is true and refererURI is registry/RegistryChecklist"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/registry/RegistryChecklist"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >>> [FALSE,TRUE]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is registry/GuidesAndAdviceLandingPage"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/registry/GuidesAndAdviceLandingPage"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is registry/RegistryIncentives"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/registry/RegistryIncentives"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is registry/RegistryFeatures"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/registry/RegistryFeatures"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is printCards/printCardsLanding"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/printCards/printCardsLanding.jsp"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is bbregistry/BridalBook"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/bbregistry/BridalBook"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is printCards/printCards"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/printCards/printCards.jsp"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is kickstarters"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/kickstarters/"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true and refererURI is registry/PersonalizedInvitations"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/store/registry/PersonalizedInvitations"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("subheader", "showPersistentHeader")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false and refererURI is BBBCoreConstants.TOP_CONSULTANTS_PREFIX"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = BBBCoreConstants.TOP_CONSULTANTS_PREFIX
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >>> [FALSE,TRUE,FALSE]
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "updated"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("disableRegistryDropdown", BBBCoreConstants.TRUE)
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * requestMock.setParameter("registrySummaryVO", registrySummaryVOMock)
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is false,registrySummaryVOMock is null,refererURI is BBBCoreConstants.SHOP_THIS_LOOK_PREFIX"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = BBBCoreConstants.SHOP_THIS_LOOK_PREFIX
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "removed"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true,userActiveRegistriesList is null"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when isTransient is true,userActiveRegistriesList is null and guideVoList is empty"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			requestMock.getAttribute("pageVariation") >> "cc"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is not hideNonRegistryGuideSuccessJson.jsp and refererURI is null"(){
		given:
			String pageURI = "/store/_includes/header/persistent_subheader.jsp"
			String refererURI = null
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			requestMock.getAttribute("pageVariation") >> "bc"
			
			//isPersistentHeader Private Method Coverage 
			giftRegSessionBeanMock.getRegistryOperation() >> "owner"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is shop_the_look_details.jsp"(){
		given:
			testObj = Spy()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setPagesWithGenericHeadOnly([""])
			testObj.setPagesWithGenOrPersistentHead([""])
			testObj.setPagesWithAjaxCall([""])
			testObj.setPagesWithPersistentOrNoHead(["/store/giftregistry/kickstarters/shop_the_look_details.jsp"])
			String pageURI = "/store/giftregistry/kickstarters/shop_the_look_details.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:"20160201")
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.getCurrentSiteId() >> "BedBathCanada"
			sessionBeanMock.setRegDiffDateLess(TRUE)
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			registrySummaryVOMock.getEventDate().equals("02/01/2016")
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			sessionBeanMock.isRegDiffDateLess().equals(TRUE)
			1 * requestMock.setParameter('disableRegistryDropdown', 'true')
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.setParameter('registrySummaryVO',registrySummaryVOMock)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			
	}
	
	def"service method. This TC is when pageURI is top_consultants_picks.jsp"(){
		given:
			testObj = Spy()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setPagesWithGenericHeadOnly([""])
			testObj.setPagesWithGenOrPersistentHead([""])
			testObj.setPagesWithAjaxCall(["/store/giftregistry/kickstarters/top_consultants_picks.jsp"])
			testObj.setPagesWithPersistentOrNoHead(["/store/giftregistry/kickstarters/top_consultants_picks.jsp"])
			String pageURI = "/store/giftregistry/kickstarters/top_consultants_picks.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:"Dec 2nd")
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.getCurrentSiteId() >> "BedBathCanada"
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isActivateGuideInRegistryRibbon().equals(FALSE)
			1 * requestMock.setParameter('disableRegistryDropdown', 'true')
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.setParameter("categorypages",BBBCoreConstants.TRUE)
			1 * requestMock.setParameter('registrySummaryVO',registrySummaryVOMock)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			
	}
	
	def"service method. This TC is when pageURI is registry_features.jsp and RegDiffDateLess is false"(){
		given:
			testObj = Spy()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setPagesWithGenericHeadOnly([""])
			testObj.setPagesWithGenOrPersistentHead([""])
			testObj.setPagesWithAjaxCall([""])
			testObj.setPagesWithPersistentOrNoHead(["/store/bbregistry/registry_features.jsp"])
			String pageURI = "/store/bbregistry/registry_features.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(eventDate:"20160201")
			sessionBeanMock.getValues().put("userRegistrysummaryVO",registrySummaryVOMock)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.getCurrentSiteId() >> "BedBathUS"
			sessionBeanMock.setRegDiffDateLess(FALSE)
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.setParameter('showStaticHeader', BBBCoreConstants.TRUE)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is top_consultants_picks.jsp and registrySummaryVO is null"(){
		given:
			String pageURI = "/store/giftregistry/kickstarters/top_consultants_picks.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			sessionBeanMock.setRegDiffDateLess(FALSE)
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('registrySummaryVO', null)
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is printCardsLanding.jsp and guideVoList is empty"(){
		given:
			String pageURI = "/store/printCards/printCardsLanding.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is bridal_book.jsp and guideVoList is empty"(){
		given:
			String pageURI = "/store/bbregistry/bridal_book.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is /store/includes/dynamic_header.jsp and referer URI  contains /store/category/ and has guide and no registry"(){
			given:
			String pageURI = "/store/includes/dynamic_header.jsp"
			String refererURI = "/store/category/"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[nonRegistryGuideVO])
			requestMock.getParameter("checkToActivateGuide") >> TRUE
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.setParameter('registrySummaryVO', null)

	} 
	
	def"service method. This TC is when pageURI is /store/includes/dynamic_header.jsp and referer URI  contains /store/category/ and has no guide and no registry"(){
		given:
			String pageURI = "/store/includes/dynamic_header.jsp"
			String refererURI = "/store/category/"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",null)
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			sessionBeanMock.getValues().put("guideVoList",null)
			requestMock.getParameter("checkToActivateGuide") >> TRUE
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> TRUE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			
	}
	
	def"service method. This TC is when pageURI is printCards.jsp and guideVoList is empty"(){
		given:
			String pageURI = "/store/printCards/printCards.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is view_kickstarters.jsp and guideVoList is empty"(){
		given:
			String pageURI = "/store/giftregistry/view_kickstarters.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("showStaticHeader", BBBCoreConstants.TRUE)
			1 * requestMock.setParameter('subheader', 'showPersistentHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is baby_landing.jsp and guideVoList is empty"(){
		given:
			String pageURI = "/store/bbregistry/baby_landing.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			
			//isPersistentHeader Private Method Coverage
			giftRegSessionBeanMock.getRegistryOperation() >> "merchant"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}

	def"service method. This TC is when pageURI is registry_creation_confirmation.jsp and siteId is BuyBuyBaby"(){
		given:
			testObj = Spy()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setPagesWithGenericHeadOnly([""])
			testObj.setPagesWithGenOrPersistentHead([""])
			testObj.setPagesWithAjaxCall([""])
			testObj.setPagesWithPersistentOrNoHead([""])
			testObj.setPagesWithGenericHeadBabyOnly(["/store/giftregistry/registry_creation_confirmation.jsp"])
			String pageURI = "/store/giftregistry/registry_creation_confirmation.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.getCurrentSiteId() >> "BuyBuyBaby"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is registry_creation_confirmation.jsp and siteId is BedBathUS"(){
		given:
			testObj = Spy()
			testObj.setGiftRegSessionBean(giftRegSessionBeanMock)
			testObj.setPagesWithGenericHeadOnly([""])
			testObj.setPagesWithGenOrPersistentHead([""])
			testObj.setPagesWithAjaxCall([""])
			testObj.setPagesWithPersistentOrNoHead([""])
			testObj.setPagesWithGenericHeadBabyOnly(["/store/giftregistry/registry_creation_confirmation.jsp"])
			String pageURI = "/store/giftregistry/registry_creation_confirmation.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.getCurrentSiteId() >> "BedBathUS"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'showGenericHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when pageURI is not matched with given values"(){
		given:
			String pageURI = "/store/giftregistry/newRegistry.jsp"
			String refererURI = "/home"
			requestMock.getRequestURI() >> pageURI
			requestMock.getHeader(BBBCoreConstants.REFERRER) >> refererURI
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("userActiveRegistriesList",["US"])
			sessionBeanMock.getValues().put("userRegistrysummaryVO",null)
			NonRegistryGuideVO nonRegistryGuideVO = new NonRegistryGuideVO()
			sessionBeanMock.getValues().put("guideVoList",[])
			requestMock.getParameter("checkToActivateGuide") >> null
			1 * requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			testObj.getCurrentSiteId() >> "BedBathUS"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('subheader', 'noHeader')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}

	
}
