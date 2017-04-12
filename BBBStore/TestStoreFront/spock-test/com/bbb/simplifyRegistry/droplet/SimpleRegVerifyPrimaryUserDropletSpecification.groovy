package com.bbb.simplifyRegistry.droplet

import com.bbb.account.BBBProfileTools
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.profile.session.BBBSessionBean

import spock.lang.specification.BBBExtendedSpec

class SimpleRegVerifyPrimaryUserDropletSpecification extends BBBExtendedSpec {

	SimpleRegVerifyPrimaryUserDroplet srvpuDroplet
	BBBSessionBean sessionBean = new BBBSessionBean()
	BBBProfileTools pToolsMock = Mock()
	
	def setup(){
		srvpuDroplet = new SimpleRegVerifyPrimaryUserDroplet(profileTools : pToolsMock)
	}
	
	def"service.TC when profile is already exist"(){
		given:
			requestMock.getLocalParameter("email") >> "harry@gmail.com"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean 
			pToolsMock.checkForRegistration("harry@gmail.com") >> "profile_already_exist"
			
		when:
			srvpuDroplet.service(requestMock, responseMock)
		then:
			sessionBean.getRegistryProfileStatus() == "regUserAlreadyExists"
			sessionBean.isRegistredUser()
			sessionBean.getUserEmailId().equalsIgnoreCase("harry@gmail.com")
			1*requestMock.setParameter("profileStatus","regUserAlreadyExists")
			1*requestMock.serviceParameter("output", requestMock,	responseMock)
	
		//requestMock.serviceParameter("error", requestMock,	responseMock)
	}
	
	def"service.TC when profile status is profile_available_for_extenstion"(){
		given:
			requestMock.getLocalParameter("email") >> "harry@gmail.com"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			pToolsMock.checkForRegistration("harry@gmail.com") >> "profile_available_for_extenstion"
			
		when:
			srvpuDroplet.service(requestMock, responseMock)
		then:
			sessionBean.getRegistryProfileStatus() == "refProfileExtenssion"
			sessionBean.isRegistredUser()
			sessionBean.getUserEmailId().equalsIgnoreCase("harry@gmail.com")
			1*requestMock.setParameter("profileStatus","refProfileExtenssion")
			1*requestMock.serviceParameter("output", requestMock,	responseMock)
	
		//requestMock.serviceParameter("error", requestMock,	responseMock)
	}
	
	def"service.TC when profile status is Profile not found"(){
		given:
			requestMock.getLocalParameter("email") >> "harry@gmail.com"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			pToolsMock.checkForRegistration("harry@gmail.com") >> "Profile not found"
			
		when:
			srvpuDroplet.service(requestMock, responseMock)
		then:
			sessionBean.getRegistryProfileStatus() == "regNewUser"
			!sessionBean.isRegistredUser()
			sessionBean.getUserEmailId().equalsIgnoreCase("harry@gmail.com")
			1*requestMock.setParameter("profileStatus","regNewUser")
			1*requestMock.serviceParameter("output", requestMock,	responseMock)
	
		//requestMock.serviceParameter("error", requestMock,	responseMock)
	}
	
	def"service.TC when profile status is not in (Profile not found, profile_available_for_extenstion and already exist)"(){
		given:
			requestMock.getLocalParameter("email") >> "harry@gmail.com"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			pToolsMock.checkForRegistration("harry@gmail.com") >> "wrong status"
			
		when:
			srvpuDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("profileStatus",null)
			1*requestMock.serviceParameter("output", requestMock,	responseMock)
	
		//requestMock.serviceParameter("error", requestMock,	responseMock)
	}
	
	def"service.TC when profile status is blanck"(){
		given:
			requestMock.getLocalParameter("email") >> "harry@gmail.com"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			pToolsMock.checkForRegistration("harry@gmail.com") >> ""
			
		when:
			srvpuDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("profileStatus",null)
			1*requestMock.serviceParameter("output", requestMock,	responseMock)
	
		//requestMock.serviceParameter("error", requestMock,	responseMock)
	}
	
	def"service.TC for BBBBusinessException"(){
		given:
			requestMock.getLocalParameter("email") >> "harry@gmail.com"
			requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
			pToolsMock.checkForRegistration("harry@gmail.com") >> {throw new BBBBusinessException("exception")}
			
		when:
			srvpuDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("profileStatus",null)
			0*requestMock.serviceParameter("output", requestMock,	responseMock)
			1*requestMock.serviceParameter("error", requestMock,	responseMock)
	
		//requestMock.serviceParameter("error", requestMock,	responseMock)
	}

}
