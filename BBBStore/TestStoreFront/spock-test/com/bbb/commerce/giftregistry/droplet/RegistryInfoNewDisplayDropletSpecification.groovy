package com.bbb.commerce.giftregistry.droplet

import atg.multisite.Site
import atg.multisite.SiteContext
import atg.repository.RepositoryException
import atg.userprofiling.Profile

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegInfoVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.BBBPropertyManager
import com.bbb.profile.session.BBBSessionBean

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RegistryInfoNewDisplayDropletSpecification extends BBBExtendedSpec {

	RegistryInfoNewDisplayDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	Profile profileMock = Mock()
	SiteContext siteContextMock = Mock()
	GiftRegSessionBean giftRegSessionBeanMock = Mock()
	BBBPropertyManager pmgrMock = Mock()
	Site siteMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	
	def setup(){
		testObj = new RegistryInfoNewDisplayDroplet(giftRegistryManager:giftRegistryManagerMock,catalogTools:catalogToolsMock,
			profile:profileMock,siteContext:siteContextMock,giftRegSessionBean:giftRegSessionBeanMock,pmgr:pmgrMock,regGuestViewURL:"/giftregistry/view_registry_guest.jsp",
			registryInfoServiceName:"getRegistryInfo2")
		
		//isUserOwnRegistry is Static method in GiftRegistryManager
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "grid"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			RegInfoVO regInfoVOMock = Mock()
			1 * giftRegistryManagerMock.getRegistryInfoFromDB( "2322565", "BedBathUS") >> regInfoVOMock
			1 * regInfoVOMock.getRegistryNum() >> 2322565
			1 * regInfoVOMock.getEventType() >> "wedding"
			1 * regInfoVOMock.getEventDate() >> 01012017
			
			//validateRegistry Private method Coverage
			//isUserOwnRegistry is Static method in GiftRegistryManager
			sessionBeanMock.getValues().put("userRegistriesList",["2322565"])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("validCheck", TRUE)
			1 * requestMock.setParameter("registryNum", 2322565)
			1 * requestMock.setParameter("eventType","wedding")
			1 * requestMock.setParameter("registryDT",01012017)
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
	}
	
	def"service method. This TC is when profile isTransient is true in validateRegistry Private method"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			//validateRegistry Private method Coverage
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock 
			profileMock.isTransient() >> TRUE
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			0 * requestMock.setParameter("validCheck", true)
	}
	
	def"service method. This TC is when profile isTransient is false in validateRegistry Private method"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			
			//validateRegistry Private method Coverage
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			profileMock.isTransient() >> FALSE
			//isUserOwnRegistry is Static method in GiftRegistryManager
			sessionBeanMock.getValues().put("userRegistriesList",["232256"])
		
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("validCheck", FALSE)
	}
	
	def"service method. This TC is when pRegistryId is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> "owner"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when displayView and regInfoVO is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> null
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.getRegistryInfoFromDB( "2322565", "BedBathUS") >> null
			
			//validateRegistry Private method Coverage
			//isUserOwnRegistry is Static method in GiftRegistryManager
			sessionBeanMock.getValues().put("userRegistriesList",["2322565"])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("validCheck", TRUE)
			1 * requestMock.setParameter("errorMsg", "err_no_reg_info")
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> null
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.getRegistryInfoFromDB( "2322565", "BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//validateRegistry Private method Coverage
			//isUserOwnRegistry is Static method in GiftRegistryManager
			sessionBeanMock.getValues().put("userRegistriesList",["2322565"])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('Mock for BBBSystemException', _)
			1 * requestMock.setParameter("validCheck", TRUE)
			1 * requestMock.setParameter('registryDT', 0)
			1 * requestMock.setParameter('eventType', null)
			1 * requestMock.setParameter('registryNum', 0)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			1 * testObj.logDebug('siteId[BedBathUS]')
			1 * testObj.logDebug(' RegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start')
			1 * testObj.logDebug('registryId[2322565]')
	}
	
	def"service method. This TC is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> null
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.getRegistryInfoFromDB( "2322565", "BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//validateRegistry Private method Coverage
			//isUserOwnRegistry is Static method in GiftRegistryManager
			sessionBeanMock.getValues().put("userRegistriesList",["2322565"])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('Mock for BBBBusinessException', _)
			1 * requestMock.setParameter("validCheck", TRUE)
			1 * requestMock.setParameter('registryDT', 0)
			1 * requestMock.setParameter('eventType', null)
			1 * requestMock.setParameter('registryNum', 0)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when RepositoryException thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "2322565"
			requestMock.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW) >> null
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			1 * giftRegistryManagerMock.getRegistryInfoFromDB( "2322565", "BedBathUS") >> {throw new RepositoryException("Mock for RepositoryException")}
			
			//validateRegistry Private method Coverage
			//isUserOwnRegistry is Static method in GiftRegistryManager
			sessionBeanMock.getValues().put("userRegistriesList",["2322565"])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('Mock for RepositoryException', _)
			1 * requestMock.setParameter("validCheck", TRUE)
			1 * requestMock.setParameter('registryDT', 0)
			1 * requestMock.setParameter('eventType', null)
			1 * requestMock.setParameter('registryNum', 0)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
}
