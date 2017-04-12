package com.bbb.selfservice.droplet

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec;

class ChatDropletSpecification extends BBBExtendedSpec {
	
	ChatDroplet droplet
	BBBCatalogTools cTools =Mock()
	
	def setup(){
		droplet = new ChatDroplet()
		droplet.setCatalogTools(cTools)
	}
	
	def"service , check if Chat is enabled for the Site."(){
		
		given:
		1*cTools.checkGlobalChat(_) >> true
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("chatglobalFlag", true);
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def"service, when BBBSystemException is thrown"(){
		
		given:
		1*cTools.checkGlobalChat(_) >> {throw new BBBSystemException("")}
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		0*requestMock.setParameter("chatglobalFlag", true);
		1*requestMock.serviceParameter("error", requestMock, responseMock)	
	}

}
