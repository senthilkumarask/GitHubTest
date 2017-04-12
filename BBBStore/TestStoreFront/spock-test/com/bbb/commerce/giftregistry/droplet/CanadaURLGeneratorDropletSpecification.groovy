package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec

class CanadaURLGeneratorDropletSpecification extends BBBExtendedSpec {

	CanadaURLGeneratorDroplet droplet = new CanadaURLGeneratorDroplet()
	BBBCatalogTools catalogTools = Mock()
	
	def setup(){
		droplet.setCatalogTools(catalogTools)
	}
	
	def "service method happy path"(){
		
		given:
			1*catalogTools.getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,	BBBCoreConstants.BABY_CANADA_SOURCE_URL) >> ["https://buybuybaby.com"]
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("canadaURL", "https://buybuybaby.com")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}	
	
	
	def "service method with buyBuyBabyCAURL as empty"(){
		
		given:
			1*catalogTools.getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,	BBBCoreConstants.BABY_CANADA_SOURCE_URL) >> []
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("canadaURL", null)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with BBBSystemException thrown"(){
		
		given:
			1*catalogTools.getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,	BBBCoreConstants.BABY_CANADA_SOURCE_URL) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			0*requestMock.setParameter("canadaURL", "https://buybuybaby.com")
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with BBBBusinessException thrown"(){
		
		given:
			1*catalogTools.getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,	BBBCoreConstants.BABY_CANADA_SOURCE_URL) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			0*requestMock.setParameter("canadaURL", "https://buybuybaby.com")
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
}
