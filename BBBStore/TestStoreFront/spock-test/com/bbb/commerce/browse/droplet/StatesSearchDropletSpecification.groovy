package com.bbb.commerce.browse.droplet

import atg.servlet.DynamoHttpServletRequest
import atg.servlet.DynamoHttpServletResponse
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.search.integration.SearchManager;

import spock.lang.specification.BBBExtendedSpec

class StatesSearchDropletSpecification extends BBBExtendedSpec {
	
	StatesSearchDroplet droplet = new StatesSearchDroplet()
	def SearchManager searchManager = Mock()
	
	def setup(){
		droplet.setSearchManager(searchManager)
	}
	
	def "service method, Happy path"(){
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter("listOfStates",null);
			1*requestMock.serviceLocalParameter("output",requestMock,responseMock)
			1*searchManager.getStates()
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setSearchManager(searchManager)
			1*searchManager.getStates() >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter("listOfStates",null);
			1*requestMock.serviceLocalParameter("output",requestMock,responseMock)
			1*droplet.logError(" BusinessException While fetching State List From Endeca", _)
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setSearchManager(searchManager)
			1*searchManager.getStates() >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
			
		then:
			1*requestMock.setParameter("listOfStates",null);
			1*requestMock.serviceLocalParameter("output",requestMock,responseMock)
			1*droplet.logError("SYstem Exception While fetching State List From Endeca", _)
	}
}
