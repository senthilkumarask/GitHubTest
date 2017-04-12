package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter

import spock.lang.specification.BBBExtendedSpec

class GetRegistryTypeNameDropletSpecification extends BBBExtendedSpec {

	GetRegistryTypeNameDroplet droplet = new GetRegistryTypeNameDroplet()
	BBBCatalogTools mCatalogTools = Mock()
	
	def setup(){
		droplet.setCatalogTools(mCatalogTools)
	}
	
	def "service method happy path"(){
		given:
			requestMock.getParameter("registryTypeCode") >> "registryTypeCode"
			requestMock.getParameter("siteId") >> "siteId"
			1*mCatalogTools.getRegistryTypeName("registryTypeCode","siteId") >> "registryTypeName"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("registryTypeName", "registryTypeName")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with registryTypeCode null and siteId blank"(){
		given:
			requestMock.getParameter("registryTypeCode") >> null
			requestMock.getParameter("siteId") >> ""
			0*mCatalogTools.getRegistryTypeName(null,"") >> "registryTypeName"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("registryTypeName", null)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with BBBSystemException thrown"(){
		given:
			droplet = Spy()
			droplet.setCatalogTools(mCatalogTools)
			requestMock.getParameter("registryTypeCode") >> "registryTypeCode"
			requestMock.getParameter("siteId") >> "siteId"
			1*mCatalogTools.getRegistryTypeName("registryTypeCode","siteId") >> {throw new BBBSystemException("BBBSystemException is thrown") }
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg", "BBBSystemException is thrown")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Get Registry name by registy code BBBSystemException from service of GetRegistryTypeNameDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1071),_)
	}
	
	def "service method with BBBBusinessException thrown"(){
		given:
			droplet = Spy()
			droplet.setCatalogTools(mCatalogTools)
			requestMock.getParameter("registryTypeCode") >> "registryTypeCode"
			requestMock.getParameter("siteId") >> "siteId"
			1*mCatalogTools.getRegistryTypeName("registryTypeCode","siteId") >> {throw new BBBBusinessException("BBBBusinessException is thrown") }
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg", "BBBBusinessException is thrown")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Get Registry name by registy code BBBBusinessException from service of GetRegistryTypeNameDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1070),_)
	}
}
