package com.bbb.commerce.giftregistry.droplet

import atg.userprofiling.Profile

import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegSearchResVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.profile.session.BBBSessionBean

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class MyRegistriesDisplayDropletSpecification extends BBBExtendedSpec {
	
	MyRegistriesDisplayDroplet testObj
	BBBCatalogTools catalogToolsMock = Mock()
	GiftRegistryManager registryManagerMock = Mock()
	Profile profileMock = Mock()
	RegistrySummaryVO registrySummaryVOMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	 
	def setup(){
		testObj = new MyRegistriesDisplayDroplet(catalogTools:catalogToolsMock,registryInfoServiceName:"getRegistryInfoByProfileId",registryManager:registryManagerMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["3"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			RegSearchResVO regSearchResVOMock = new RegSearchResVO() 
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO()
			regSearchResVOMock.setServiceErrorVO(serviceErrorVOMock)
			serviceErrorVOMock.setErrorExists(FALSE)
			RegistrySummaryVO registrySummaryVOMock1 = Mock()
			RegistrySummaryVO registrySummaryVOMock2 = Mock()
			regSearchResVOMock.setListRegistrySummaryVO([registrySummaryVOMock,registrySummaryVOMock1,registrySummaryVOMock2])
			BBBSessionBean sessionBeanMock = new BBBSessionBean() 
			requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistriesList",["user1","22323256"])
			
			registrySummaryVOMock.getAddrSubType() >> "CO"
			registrySummaryVOMock.getPrimaryRegistrantFullName() >> "primaryRegistrant"
			registrySummaryVOMock.getRegistryId() >> "22323256"
			registrySummaryVOMock.getEventDate() >> "01/01/2017"
			
			registrySummaryVOMock1.getAddrSubType() >> "PR"
			registrySummaryVOMock1.getPrimaryRegistrantFullName() >> "primaryRegistrant1"
			registrySummaryVOMock1.getRegistryId() >> "78787888"
			registrySummaryVOMock1.getEventDate() >> "12/12/2016"
			
			registrySummaryVOMock2.getAddrSubType() >> "YR"
			registrySummaryVOMock2.getPrimaryRegistrantFullName() >> "primaryRegistrant2"
			registrySummaryVOMock2.getRegistryId() >> null
			registrySummaryVOMock2.getEventDate() >> "11/12/2016"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO,	[registrySummaryVOMock,registrySummaryVOMock1,registrySummaryVOMock2])
			1 * requestMock.setParameter('registryCount', 3)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * registrySummaryVOMock2.setEventDateCanada('12/11/2016')
			1 * registrySummaryVOMock.setCoRegistrantFullName('primaryRegistrant')
			1 * registrySummaryVOMock1.setEventDateCanada('12/12/2016')
			1 * registrySummaryVOMock.setEventDateCanada('01/01/2017')
	}
	
	
	def"service method. This TC is when errorId is 200"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setRegistryManager(registryManagerMock)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> null
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"Error Msg with code 200",errorId:200)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock)
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_sys_exception')
			1 * testObj.logError('giftregistry_1078: Gift Registry Input fields Exception from service of MyRegistryDisplayDroplet 200')
			1 * testObj.logError('giftregistry_1005: BBBSystemException from SERVICE of MyRegistryDisplayDroplet', _)
			1 * testObj.logDebug('Entering Service Method of MyRegistries Droplet.')
			1 * testObj.logDebug('Exit Service Method of MyRegistries Droplet.')
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}
	
	def"service method. This TC is when errorId is 900"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setRegistryManager(registryManagerMock)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> null
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]			
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"Error Msg with code 900",errorId:900)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock)
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * testObj.logError('giftregistry_1011: Fatal error from service of MyRegistriesDisplayDroplet : Error Id is:900')
			1 * testObj.logError('giftregistry_1005: BBBSystemException from SERVICE of MyRegistryDisplayDroplet', _)
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_sys_exception')
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}
	
	def"service method. This TC is when errorId is 901"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setRegistryManager(registryManagerMock)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> null
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"Error Msg with code 901",errorId:901)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock)
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * testObj.logError('giftregistry_1002: Either user token or site flag invalid from service of MyRegistriesDisplayDroplet : Error Id is:901')
			1 * testObj.logError('giftregistry_1005: BBBSystemException from SERVICE of MyRegistryDisplayDroplet', _)
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_sys_exception')
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when errorId is 902"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setRegistryManager(registryManagerMock)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> null
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"Error Msg with code 902",errorId:902)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock)
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * testObj.logError('giftregistry_1049: GiftRegistry input fields format error from service() of MyRegistriesDisplayDroplet | webservice error code=902')
			1 * testObj.logError('giftregistry_1005: BBBSystemException from SERVICE of MyRegistryDisplayDroplet', _)
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_sys_exception')
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when errorId is 907 and getListRegistrySummaryVO is null"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["3"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"Error Msg with code 907",errorId:907)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock,listRegistrySummaryVO:null)
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter("registryCount", 0)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

	def"service method. This TC is when getErrorDisplayMessage is empty and getListRegistrySummaryVO is empty"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["3"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:200)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock,listRegistrySummaryVO:[])
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistriesList",null)
			
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter("registryCount", 0)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, [])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when registrySearchVO siteId is not 3"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:200)
			RegSearchResVO regSearchResVOMock = new RegSearchResVO(serviceErrorVO:serviceErrorVOMock,listRegistrySummaryVO:[])
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> regSearchResVOMock
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistriesList",null)
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter("registryCount", 0)
			1 * requestMock.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, [])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

	def"service method. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_biz_exception')
			1 * testObj.logError('giftregistry_1004: BBBBusinessException from service of MyRegistryDisplayDroplet', _)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when regSearchResVO is null"(){
		given:
			requestMock.getObjectParameter("profile") >> profileMock
			requestMock.getParameter("siteId") >> "BedBathUS"
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, "BedBathUS") >> null
			1 * catalogToolsMock.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,	BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
			1 * registryManagerMock.searchRegistries(_,"BedBathUS") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('registrySummaryVO', null)
			1 * requestMock.setParameter('registryCount', 0)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}
	

}
