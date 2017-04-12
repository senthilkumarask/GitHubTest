package com.bbb.commerce.giftregistry.droplet

import atg.userprofiling.Profile
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class ValidateRegistryDropletSpecification extends BBBExtendedSpec {
	
	ValidateRegistryDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	Profile profileMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	
	def setup(){
		testObj = new ValidateRegistryDroplet(giftRegistryManager:giftRegistryManagerMock,profile:profileMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "22354532"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistriesList",["22354532"])
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter("valid", requestMock, responseMock)
	}
	
	def"service method. This TC is when validCheck is false"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "22354532"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
			sessionBeanMock.getValues().put("userRegistriesList",null)
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter("inValid", requestMock, responseMock)
	}

}
