package com.bbb.commerce.giftregistry.droplet

import spock.lang.specification.BBBExtendedSpec

class GiftRegistryIdDecriptorDropletSpecification extends BBBExtendedSpec {

	GiftRegistryIdDecriptorDroplet droplet = new GiftRegistryIdDecriptorDroplet()
	
	def "service method happy path"(){
		given:
			requestMock.getParameter("registryEncriptedId") >> "-2"
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("registryId", 399906560)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with registryEncriptedId more than 0"(){
		given:
			requestMock.getParameter("registryEncriptedId") >> "2"
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("registryId", 399906560)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with registryEncriptedId as empty"(){
		given:
			requestMock.getParameter("registryEncriptedId") >> ""
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("registryId", 0)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
}
