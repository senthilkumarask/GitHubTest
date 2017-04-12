package com.bbb.commerce.order.purchase

import atg.userprofiling.Profile
import com.bbb.account.api.BBBAddressVO
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.common.BBBAddressImpl
import com.bbb.constants.BBBCoreConstants;

import spock.lang.specification.BBBExtendedSpec

class BBBCheckProfileAddressDropletSpecification extends BBBExtendedSpec {

	def BBBCheckProfileAddressDroplet cpaDroplet
	def Profile profile = Mock()
	def BBBAddressImpl address = Mock()
	def BBBCheckoutManager cManager = Mock()
	BBBAddressVO addressVO = new BBBAddressVO()
	def setup(){
		cpaDroplet = new BBBCheckProfileAddressDroplet(checkoutMgr : cManager)
	}
	
	def"service. tc to add profile address to request object"(){
		given:
			1*requestMock.getObjectParameter("profile") >> profile
			1*requestMock.getObjectParameter("siteId") >> "siteid"
			
			1*cManager.getProfileShippingAddress(profile, "siteid") >> [addressVO]
			
		when:
		   cpaDroplet.service( requestMock, responseMock)
		then:
		
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*requestMock.setParameter("profileAddresses", [addressVO])
	}
	
	def"service. tc for Exception"(){
		given:
			String siteId = "siteId"
			1*requestMock.getObjectParameter("profile") >> profile
			1*requestMock.getObjectParameter("siteId") >> siteId
			
			1*cManager.getProfileShippingAddress(profile, siteId) >> {throw new Exception("exception")}
			
		when:
		   cpaDroplet.service( requestMock, responseMock)
		then:
		
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*requestMock.setParameter("profileAddresses", [])
	}

}
