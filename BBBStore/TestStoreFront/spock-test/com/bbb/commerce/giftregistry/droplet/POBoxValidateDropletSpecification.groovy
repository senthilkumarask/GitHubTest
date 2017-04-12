package com.bbb.commerce.giftregistry.droplet

import java.util.regex.Pattern;

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class POBoxValidateDropletSpecification extends BBBExtendedSpec {
	
	POBoxValidateDroplet testObj
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new POBoxValidateDroplet(pattern:nonPOBoxAddressPattern)
	}
	
	def"service method. This TC is the Happy flow of service method"() {
		given:
			testObj.setPattern(nonPOBoxAddressPattern)
			requestMock.getParameter("address") >> "1076 parsons ave"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isValid", FALSE)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when address is null"() {
		given:
			requestMock.getParameter("address") >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isValid", FALSE)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
	}

}
