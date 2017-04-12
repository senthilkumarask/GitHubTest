package com.bbb.commerce.giftregistry.droplet

import atg.userprofiling.Profile
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;

import com.bbb.profile.session.BBBSessionBean
import spock.lang.specification.BBBExtendedSpec

class AcceptableGiftRegistryInfoDropletSpecification extends BBBExtendedSpec {
	AcceptableGiftRegistryInfoDroplet testObj
	GiftRegistryManager giftRegistryManagerMock =Mock()
	BBBSessionBean bbbSessionBeanMock =Mock()
	Profile profileMock=Mock()
	
	def setup(){
		testObj=new AcceptableGiftRegistryInfoDroplet()
	}
	
	def "service- pass PrimeRegistryCompleted as false"(){
		given:
			testObj=Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			bbbSessionBeanMock.isPrimeRegistryCompleted() >> false
			1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			//giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, _) >> {}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*testObj.logDebug("AcceptableGiftRegistryInfoDroplet,SkinnyVo successfully loaded In session")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service- pass PrimeRegistryCompleted as true"(){
		given:
			testObj=Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			bbbSessionBeanMock.isPrimeRegistryCompleted() >> true
			0*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			//giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, _) >> {}
		when:
			testObj.service(requestMock, responseMock)
		then:
			0*testObj.logDebug("AcceptableGiftRegistryInfoDroplet,SkinnyVo successfully loaded In session")
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			0*requestMock.setParameter("errorMsg", "System Exception");
	}
	def "service- pass PrimeRegistryCompleted as false and throw Exception"(){
		given:
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			bbbSessionBeanMock.isPrimeRegistryCompleted() >> false
			1*requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
			giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, _) >>{throw new Exception("mock Exception")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			0*testObj.logDebug("AcceptableGiftRegistryInfoDroplet,SkinnyVo successfully loaded In session")
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", "System Exception");
	}
}
