package com.bbb.commerce.giftregistry.droplet

import atg.multisite.Site
import atg.multisite.SiteContext
import atg.repository.RepositoryException
import atg.userprofiling.Profile
import atg.userprofiling.ProfileTools

import com.bbb.account.BBBProfileTools
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.checklist.manager.CheckListManager
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.commerce.giftregistry.vo.ShippingVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RegistryInfoDisplayDropletSpecification extends BBBExtendedSpec {
	
	RegistryInfoDisplayDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	SiteContext siteContextMock = Mock()
	BBBPropertyManager pmgrMock = Mock()
	GiftRegSessionBean giftRegSessionBeanMock = Mock()
	LblTxtTemplateManager contentManagerMock = Mock()
	ProfileTools profileToolsMock = Mock()
	BBBProfileTools bbbProfileToolsMock = Mock()
	CheckListManager checkListManagerMock = Mock()
	Site siteMock = Mock()
	Profile profileMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup() {
		testObj = new RegistryInfoDisplayDroplet(giftRegistryManager:giftRegistryManagerMock,catalogTools:catalogToolsMock,
			siteContext:siteContextMock,pmgr:pmgrMock,giftRegSessionBean:giftRegSessionBeanMock,contentManager:contentManagerMock,
			profileTools:bbbProfileToolsMock,checkListManager:checkListManagerMock,mRegistryInfoServiceName:"getRegistryInfo2",regGuestViewURL:"/giftregistry/view_registry_guest.jsp")
		
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
	}

	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			profileMock.isTransient() >> FALSE
			
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeName:"wedding")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(primaryRegistrantEmail:"notMasked",primaryRegistrantMobileNum:"8989898989",
				primaryRegistrantPrimaryPhoneNum:"9999999999",registryType:registryTypesMock,eventDate:"12/12/2016",
				futureShippingDate:"05/01/2017",primaryRegistrantFirstName:"John",primaryRegistrantLastName:"Kennedy",coRegistrantFirstName:"Stalin",coRegistrantLastName:"Raj")
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistrantVO coRegistrantMock = new RegistrantVO(cellPhone:"8585858585",primaryPhone:"9595959595",email:"son@gmail.com")
			RegistrantVO primaryRegistrantMock = new RegistrantVO(cellPhone:"8787878787",primaryPhone:"9797979797",email:"john@gmail.com")
			RegistrantVO registrantVOMock = new RegistrantVO(cellPhone:"8686868686",primaryPhone:"9696969696")
			ShippingVO shippingVOMock = new ShippingVO()
			EventVO eventVOMock = new EventVO()
			RegistryVO registryVOMock = new RegistryVO(coRegistrant:coRegistrantMock,primaryRegistrant:primaryRegistrantMock,registrantVO:registrantVOMock,
				shipping:shippingVOMock,event:eventVOMock,prefStoreNum:"2233",isPublic:"1")
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock,registryVO:registryVOMock)
			
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock,"registryIdList":null])
	
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			//1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> TRUE
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			1 * giftRegistryManagerMock.canScheuleAppointment("2233","wedding", "BedBathUS") >> TRUE
			pmgrMock.getLoginPropertyName() >> "signIn"
			1 * profileMock.getPropertyValue(pmgrMock.getLoginPropertyName()) >> "son@gmail.com"
			1 * catalogToolsMock.getRegistryTypeName("wedding","BedBathUS") >> "anniversery"
			1 * checkListManagerMock.showCheckListButton("wedding") >> TRUE
			1 * giftRegistryManagerMock.getRegistryStatusFromRepo("BedBathUS", "2325522") >> "Submitted"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> "Submitted,Completed,Pending"
			requestMock.getRequestURI() >> "giftRegistry.jsp"
			1 * giftRegistryManagerMock.isCoOwerByProfile("p12345", "2325522", "BedBathUS") >> FALSE
			1 * catalogToolsMock.isInviteFriends() >> TRUE
			
						
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('validCheck', true)
			1 * requestMock.setParameter("isChecklistDisabled",TRUE)
			1 * requestMock.setParameter("coRegFlag",TRUE)
			1 * requestMock.setParameter("registryURL", "/giftregistry/view_registry_guest.jsp")
			1 * requestMock.setParameter("eventDate", "12/12/2016")
			1 * requestMock.setParameter("futureShippingDate", "05/01/2017")
			1 * requestMock.setParameter("registrySummaryVO", registrySummaryVOMock)
			1 * requestMock.setParameter("invite", TRUE)
			1 * requestMock.setParameter("registryVO",registryVOMock)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			1 * giftRegSessionBeanMock.setRegistryOperation(BBBGiftRegistryConstants.OWNER_VIEW)
			registrySummaryVOMock.getPrimaryRegistrantEmail().equals("john@gmail.com")
			registrySummaryVOMock.getPrimaryRegistrantMobileNum().equals("8787878787")
			registrySummaryVOMock.getPrimaryRegistrantPrimaryPhoneNum().equals("9797979797")
			registrySummaryVOMock.getEventVO().equals(eventVOMock)
			registrySummaryVOMock.getFavStoreId().equals("2233")
			registrySummaryVOMock.isAllowedToScheduleAStoreAppointment().equals(TRUE)
			registrySummaryVOMock.getEventType().equals("anniversery")
			registrySummaryVOMock.getEventDate().equals("12/12/2016")
			registrySummaryVOMock.getPrimaryRegistrantFirstName().equals("John")
			sessionBeanMock.getValues().get("userRegistrysummaryVO").equals(registrySummaryVOMock)
			registryVOMock.isCoRegOwner().equals(FALSE)
			registrySummaryVOMock.getRegTitle().equals("John Kennedy & Stalin Raj - anniversery")
			
	}

	
	def"service method. This TC is when displayView is owner and registryId not equals to userRegList"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock 
			profileMock.getRepositoryId() >> "p12345"
			profileMock.isTransient() >> TRUE
			sessionBeanMock.getValues().putAll(["userRegistriesList":["856522"]])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * giftRegistryManagerMock.getUserRegistryList(profileMock, "BedBathUS")
			1 * requestMock.setParameter('errorMsg', 'err_invalid_reg_info_req')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when userRegistriesList is null and errorDisplayMessage has value"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "merchant"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(primaryRegistrantEmail:"masked")
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":null,"2325522_REG_SUMMARY":registryResVOMock])
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"displayMessage",errorId:200)
			RegistryResVO registryResVOMock1 = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.getRegistryInfo(_) >> registryResVOMock1
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1011: p12345|false|Fatal error from service of RegistriesInfoDisplayDroplet : Error Id is:200')
			1 * requestMock.setParameter("validCheck", FALSE)
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_fatal_error')
			1 * testObj.logDebug('siteId[BedBathUS]')
			1 * testObj.logDebug('registryId[2325522]')
			1 * testObj.logDebug(' RegistryInfoDisplayDroplet service - registryResVO is null and not in session, fetching it from DB')
			1 * testObj.logDebug(' RegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
			sessionBeanMock.getValues().get("2325522_REG_SUMMARY").equals(registryResVOMock1)
	}
	
	
	def"service method. This TC is when userRegistriesList is null and errorDisplayMessage is empty and errorId is 900"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "merchant"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(primaryRegistrantEmail:null)
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorMessage:"errorMessage",errorId:900)
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":null,"2325522_REG_SUMMARY":registryResVOMock])
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1011: p12345|false|Fatal error from service of RegistriesInfoDisplayDroplet : Error Id is:900')
			1 * requestMock.setParameter("validCheck", FALSE)
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_fatal_error')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when registrySummaryVO is null and errorMessage has value and errorId is 901"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "merchant"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorMessage:"errorMessage",errorId:901)
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:null,serviceErrorVO:serviceErrorVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":null,"2325522_REG_SUMMARY":registryResVOMock])
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1002: p12345|false|Either user token or site flag invalid from service of RegistryInfoDisplayDroplet : Error Id is:901')
			1 * requestMock.setParameter("validCheck", FALSE)
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_siteflag_usertoken_error')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when registryResVO is null and errorMessage has value and errorId is 902"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "merchant"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorMessage:"errorMessage",errorId:902)
			sessionBeanMock.getValues().putAll(["userRegistriesList":null,"2325522_REG_SUMMARY":null])
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			RegistryResVO registryResVOMock = new RegistryResVO(serviceErrorVO:serviceErrorVOMock)
			1 * giftRegistryManagerMock.getRegistryInfo(_) >> registryResVOMock
			
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('giftregistry_1049: p12345|false|GiftRegistry input fields format error from service() of RegistryInfoDisplayDroplet | webservice error code=902')
			1 * requestMock.setParameter("validCheck", FALSE)
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_invalid_input_format')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
			sessionBeanMock.getValues().get("2325522_REG_SUMMARY").equals(registryResVOMock)
	}
	
	def"service method. This TC is when RepositoryException thrown in getUserRegistryList"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			sessionBeanMock.getValues().putAll(["userRegistriesList":["23255"]])
			1 * giftRegistryManagerMock.getUserRegistryList(profileMock, "BedBathUS") >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('Exception occurred while fetching registries', _)
			1 * requestMock.setParameter('errorMsg', 'err_invalid_reg_info_req')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when displayView and registryResVO is null"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> null
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			sessionBeanMock.getValues().putAll(["userRegistriesList":null,"2325522_REG_SUMMARY":null])
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			1 * giftRegistryManagerMock.getRegistryInfo(_) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("validCheck", FALSE)
			1 * requestMock.setParameter('errorMsg', 'err_no_reg_info')
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
			sessionBeanMock.getValues().get("2325522_REG_SUMMARY").equals(null)
	}
	
	def"service method. This TC is when cellPhone and primaryPhone are null in updateRegistryResVORecUser Private Method"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			pmgrMock.getLoginPropertyName() >> "signIn"
			1 * profileMock.getPropertyValue(pmgrMock.getLoginPropertyName()) >> "bbb@gmail.com"
			1 * giftRegistryManagerMock.getRegistryStatusFromRepo("BedBathUS", "2325522") >> "Submitted"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> "Submitted,Completed,Pending"
			requestMock.getRequestURI() >> "view_registry_guest.jsp"
			1 * giftRegistryManagerMock.isCoOwerByProfile("p12345", "2325522", "BedBathUS") >> TRUE
			1 * catalogToolsMock.isInviteFriends() >> FALSE
			
			RegistryTypes registryTypesMock = new RegistryTypes(registryTypeName:null)
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(primaryRegistrantEmail:null,primaryRegistrantMobileNum:null,
				primaryRegistrantPrimaryPhoneNum:null,registryType:registryTypesMock,eventDate:"12/12/2016",
				futureShippingDate:null,primaryRegistrantFirstName:"John",primaryRegistrantLastName:"Kennedy",coRegistrantFirstName:null,coRegistrantLastName:null)
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistrantVO coRegistrantMock = new RegistrantVO(cellPhone:null,primaryPhone:null)
			RegistrantVO primaryRegistrantMock = new RegistrantVO(cellPhone:null,primaryPhone:null,email:"john@gmail.com")
			RegistrantVO registrantVOMock = new RegistrantVO(cellPhone:null,primaryPhone:null)
			EventVO eventVOMock = new EventVO()
			RegistryVO registryVOMock = new RegistryVO(coRegistrant:coRegistrantMock,primaryRegistrant:primaryRegistrantMock,registrantVO:registrantVOMock,
				shipping:null,event:eventVOMock,prefStoreNum:"",isPublic:"1")
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock,registryVO:registryVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock,"registryIdList":[registrySummaryVOMock]])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter('validCheck', true)
			1 * requestMock.setParameter("coRegFlag",FALSE)
			1 * requestMock.setParameter("registryURL", "/giftregistry/view_registry_guest.jsp")
			1 * requestMock.setParameter("eventDate", "12/12/2016")
			1 * requestMock.setParameter("futureShippingDate", null)
			1 * requestMock.setParameter("registrySummaryVO", registrySummaryVOMock)
			1 * requestMock.setParameter("invite", FALSE)
			1 * requestMock.setParameter("registryVO",registryVOMock)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			sessionBeanMock.getValues().get("registryIdList").equals([registrySummaryVOMock])
			
			registrySummaryVOMock.getPrimaryRegistrantEmail().equals("john@gmail.com")
			registrySummaryVOMock.getEventVO().equals(eventVOMock)
			registrySummaryVOMock.getFavStoreId().equals("")
			registrySummaryVOMock.getEventDate().equals("12/12/2016")
			registryVOMock.isCoRegOwner().equals(TRUE)
			registrySummaryVOMock.getRegTitle().equals("John Kennedy - null")
	}
	
	def"service method. This TC is when registrySummaryVO and registryVO is null"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			//1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> TRUE
			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:null,serviceErrorVO:serviceErrorVOMock,registryVO:null)
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('giftregistry_1002: p12345|false|Registry Sync Issue for recognized user profile : p12345 and registry id: 2325522')
			1 * requestMock.setParameter("errorMsg", "Registry Sync Issue for Recognized User")
			1 * requestMock.serviceParameter("error", requestMock , responseMock)
			1 * requestMock.setParameter('validCheck', true)
	}

	def"service method. This TC is when registryVO is null"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
			//1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> TRUE
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock,registryVO:null)
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("errorMsg", "Registry Sync Issue for Recognized User")
			1 * requestMock.serviceParameter("error", requestMock , responseMock)
			1 * requestMock.setParameter('validCheck', true)
	}
	
	def"service method. This TC is when isRecognizedUser is false and displayView is not 'owner'"(){
		given:
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "merchant"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			profileMock.isTransient() >> TRUE
			//1 * bbbProfileToolsMock.isRecognizedUser(requestMock, profileMock) >> FALSE
			1 * giftRegistryManagerMock.getRegistryStatusFromRepo("BedBathUS", "2325522") >> "Submitted"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> ""
			1 * giftRegistryManagerMock.isCoOwerByProfile("p12345", "2325522", "BedBathUS") >> TRUE
			1 * catalogToolsMock.isInviteFriends() >> FALSE
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryType:null,eventDate:null)
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			EventVO eventVOMock = new EventVO()
			RegistryVO registryVOMock = new RegistryVO(event:eventVOMock,prefStoreNum:"",isPublic:"3")
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock,registryVO:registryVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter('validCheck', TRUE)
			1 * requestMock.setParameter("coRegFlag",FALSE)
			1 * requestMock.setParameter('invite', FALSE)
			1 * requestMock.setParameter('eventDate', null)
			1 * requestMock.setParameter('registryURL', '/giftregistry/view_registry_guest.jsp')
			1 * requestMock.setParameter('futureShippingDate', null)
			1 * requestMock.setParameter('registrySummaryVO', registrySummaryVOMock)
			1 * requestMock.setParameter('registryVO', registryVOMock)
			
	}
	
	def"service method. This TC is when BBBBusinessException thrown"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
			pmgrMock.getLoginPropertyName() >> "signIn"
			1 * profileMock.getPropertyValue(pmgrMock.getLoginPropertyName()) >> "masked"
			1 * giftRegistryManagerMock.getRegistryStatusFromRepo("BedBathUS", "2325522") >> "Submitted"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> "Completed,Pending"
			requestMock.getRequestURI() >> "view_registry_guest.jsp"
			1 * giftRegistryManagerMock.isCoOwerByProfile("p12345", "2325522", "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorMessage:"errorMessage",errorId:200)
			EventVO eventVOMock = new EventVO()
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbb@gmail.com")
			RegistryVO registryVOMock = new RegistryVO(coRegistrant:coRegistrantMock,event:eventVOMock,prefStoreNum:"",isPublic:"3")
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock,registryVO:registryVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('BBBBusinessException', _)
			1 * requestMock.serviceLocalParameter("error", requestMock , responseMock)
			1 * requestMock.setParameter('validCheck', true)
	}
	
	def"service method. This TC is when BBBSystemException thrown"(){
		given:
			this.spyMethod()
			requestMock.getLocale() >> new Locale("en_US")
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2325522"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			requestMock.getParameter("fromGiftGiver") >> "giftgiver"
			requestMock.getParameter("requestURL") >> "/store/giftRegistry.jsp"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "p12345"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
			pmgrMock.getLoginPropertyName() >> "signIn"
			1 * profileMock.getPropertyValue(pmgrMock.getLoginPropertyName()) >> "masked"
			1 * giftRegistryManagerMock.getRegistryStatusFromRepo("BedBathUS", "2325522") >> "Submitted"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> "Completed,Pending"
			requestMock.getRequestURI() >> "view_registry_guest.jsp"
			1 * giftRegistryManagerMock.isCoOwerByProfile("p12345", "2325522", "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorMessage:"",errorId:200)
			EventVO eventVOMock = new EventVO()
			RegistrantVO coRegistrantMock = new RegistrantVO(email:"bbb@gmail.com")
			RegistryVO registryVOMock = new RegistryVO(coRegistrant:coRegistrantMock,event:eventVOMock,prefStoreNum:"",isPublic:"3")
			RegistryResVO registryResVOMock = new RegistryResVO(registrySummaryVO:registrySummaryVOMock,serviceErrorVO:serviceErrorVOMock,registryVO:registryVOMock)
			sessionBeanMock.getValues().putAll(["userRegistriesList":["2325522"],"2325522_REG_SUMMARY":registryResVOMock])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('BBBSystemException', _)
			1 * requestMock.setParameter('errorMsg', 'err_reginfo_sys_error')
			1 * requestMock.serviceLocalParameter("error", requestMock , responseMock)
			1 * requestMock.setParameter('validCheck', true)
	}
	
	private spyMethod() {
		testObj = Spy()
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setSiteContext(siteContextMock)
		testObj.setPmgr(pmgrMock)
	}
	
	def"saveRegistryInfoToSession. This TC is when registrySummaryVO is null"(){
		given: 
			sessionBeanMock.getValues().putAll(["registryIdList":null])
			
		expect:
			testObj.saveRegistryInfoToSession(null, requestMock)
			
	}
	
	def"saveRegistryInfoToSession. This TC is when usersCurrentlyViewedRegistry does not contain registrySummaryVO"(){
		given:
			RegistrySummaryVO registrySummaryVOMock1 = new RegistrySummaryVO()
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO()
			sessionBeanMock.getValues().putAll(["registryIdList": [registrySummaryVOMock]])
			
		when:
			testObj.saveRegistryInfoToSession(registrySummaryVOMock1, requestMock)
			
		then:
			sessionBeanMock.getValues().get("registryIdList").equals([registrySummaryVOMock,registrySummaryVOMock1])
	}
	
	

}
 