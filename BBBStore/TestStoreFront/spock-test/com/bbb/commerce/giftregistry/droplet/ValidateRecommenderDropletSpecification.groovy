package com.bbb.commerce.giftregistry.droplet

import static com.bbb.constants.BBBAccountConstants.PARAM_ERROR_MSG;
import atg.userprofiling.Profile

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class ValidateRecommenderDropletSpecification extends BBBExtendedSpec {
	
	ValidateRecommenderDroplet testObj
	GiftRegistryTools giftRegistryToolsMock = Mock()
	GiftRegistryManager giftRegistryManagerMock = Mock()
	Profile profileMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	
	def setup() {
		testObj = new ValidateRecommenderDroplet(giftRegistryTools:giftRegistryToolsMock,giftRegistryManager:giftRegistryManagerMock)
	}
	
	def"service method. This TC is when validateToken returns 1"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232" 
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "true"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> BBBGiftRegistryConstants.VALID_TOKEN
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			sessionBeanMock.getValues().get("recommendedRegistry").equals("256323232")
			sessionBeanMock.getValues().get("recommendedToken").equals("token")
			sessionBeanMock.getValues().get("isRecommenderFromFB").equals("true")
	}
	
	def"service method. This TC is when validateToken returns 3"(){
		given:
			requestMock.getParameter("siteId") >> null
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> null
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> BBBGiftRegistryConstants.PRIVATE_REGISTRY
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(PARAM_ERROR_MSG, "err_private_registry")
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when validateToken returns 2"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> BBBGiftRegistryConstants.INVALID_REGISTRY
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(PARAM_ERROR_MSG, "err_invalid_registry")
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when validateToken returns -1"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> BBBGiftRegistryConstants.INVALID_TOKEN
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(PARAM_ERROR_MSG, "err_invalid_token")
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when validateToken returns 0"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> BBBGiftRegistryConstants.TOKEN_EXPIRED
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(PARAM_ERROR_MSG, "err_token_expired")
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when validateToken returns 5"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> 5
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			0 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when fromURI is VRR and sessionContainsRegistry is true"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "VRR"
			sessionBeanMock.getValues().put("recommendedRegistry","2350125")
			sessionBeanMock.getValues().put("recommendedToken","token1")
			sessionBeanMock.getValues().put("isRecommenderFromFB","true")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"121154")
			RegistrySummaryVO registrySummaryVOMock1 = new RegistrySummaryVO(registryId:"825512")
			RegistrySummaryVO registrySummaryVOMock2 = new RegistrySummaryVO(registryId:"121154")
			1 * giftRegistryManagerMock.persistRecommenderReln("2350125", "token1", "true") >> registrySummaryVOMock
			sessionBeanMock.setRegistrySummaryVO([registrySummaryVOMock1,registrySummaryVOMock2])
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			!sessionBeanMock.getRegistrySummaryVO().equals([registrySummaryVOMock1,registrySummaryVOMock2,registrySummaryVOMock])
	}
	
	def"service method. This TC is when fromURI is VRR and sessionContainsRegistry is false"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "VRR"
			sessionBeanMock.getValues().put("recommendedRegistry","2350125")
			sessionBeanMock.getValues().put("recommendedToken","token1")
			sessionBeanMock.getValues().put("isRecommenderFromFB","true")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"121154")
			RegistrySummaryVO registrySummaryVOMock1 = new RegistrySummaryVO(registryId:"825512")
			RegistrySummaryVO registrySummaryVOMock2 = new RegistrySummaryVO(registryId:"2565252")
			1 * giftRegistryManagerMock.persistRecommenderReln("2350125", "token1", "true") >> registrySummaryVOMock
			sessionBeanMock.setRegistrySummaryVO([registrySummaryVOMock1,registrySummaryVOMock2])
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getRegistrySummaryVO().equals([registrySummaryVOMock1,registrySummaryVOMock2,registrySummaryVOMock])
	}
	
	def"service method. This TC is when fromURI is VRR and sessionBean getRegistrySummaryVO is null"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "VRR"
			sessionBeanMock.getValues().put("recommendedRegistry","2350125")
			sessionBeanMock.getValues().put("recommendedToken","token1")
			sessionBeanMock.getValues().put("isRecommenderFromFB","true")
			RegistrySummaryVO registrySummaryVOMock = new RegistrySummaryVO(registryId:"121154")			
			1 * giftRegistryManagerMock.persistRecommenderReln("2350125", "token1", "true") >> registrySummaryVOMock
			sessionBeanMock.setRegistrySummaryVO(null)
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getRegistrySummaryVO().equals([registrySummaryVOMock])
	}

	def"service method. This TC is when fromURI is VRR and registrySummaryVO is null"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "VRR"
			sessionBeanMock.getValues().put("recommendedRegistry","2350125")
			sessionBeanMock.getValues().put("recommendedToken","token1")
			sessionBeanMock.getValues().put("isRecommenderFromFB","true")
			1 * giftRegistryManagerMock.persistRecommenderReln("2350125", "token1", "true") >> null
			RegistrySummaryVO registrySummaryVOMock1 = new RegistrySummaryVO(registryId:"825512")
			RegistrySummaryVO registrySummaryVOMock2 = new RegistrySummaryVO(registryId:"2565252")
			sessionBeanMock.setRegistrySummaryVO([registrySummaryVOMock1,registrySummaryVOMock2])
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when fromURI is VRR and recommendedRegistry is null"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "VRR"
			sessionBeanMock.getValues().put("recommendedRegistry",null)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when fromURI is URI"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "URI"
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown in validateToken"(){
		given:
			requestMock.getParameter("siteId") >> "BedBathUS"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.getRepositoryId() >> "Id12345"
			requestMock.getParameter("registryId") >> "256323232"
			requestMock.getParameter("token") >> "token"
			requestMock.getParameter("isFromFB") >> "false"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter("fromURI") >> "RLP"
			1 * giftRegistryToolsMock.validateToken("256323232", "token") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter(PARAM_ERROR_MSG, "err_invalid_registry")
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}

}
