package com.bbb.commerce.giftregistry.droplet

import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author kmagud
 *
 */
class MxDateFormatDropletSpecification extends BBBExtendedSpec {
	
	MxDateFormatDroplet testObj
	
	def setup(){
		testObj = new MxDateFormatDroplet()
	}

	def"service method. This TC is the happy flow of service method"(){
		given:
			requestMock.getParameter("currentDate") >> "12/12/2016"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("mxConvertedDate", "12/12/2016")
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
	
	def"service method. This TC is when currentDate is null"(){
		given:
			requestMock.getParameter("currentDate") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("errorMsg", "currentDate is null")
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
		
	}
}
