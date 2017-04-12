package com.bbb.commerce.giftregistry.droplet

import atg.repository.RepositoryException
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter

import spock.lang.specification.BBBExtendedSpec

class GetValidRegistryTypeDropletSpecification extends BBBExtendedSpec {

	GetValidRegistryTypeDroplet droplet = new GetValidRegistryTypeDroplet()
	BBBCatalogTools mCatalogTools = Mock()
	GiftRegistryManager mGiftRegistryManager = Mock()
	
	def setup(){
		droplet.setCatalogTools(mCatalogTools)
		droplet.setGiftRegistryManager(mGiftRegistryManager)
	}
	
	def "service method with valid registry type"(){
		given:
			requestMock.getParameter("regType") >> "regType"
			requestMock.getParameter("siteId") >> "siteId"
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"regType")
			1*mGiftRegistryManager.fetchRegistryTypes("siteId") >> [registryTypeVOMock]
		
			when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("validRegType", "regType")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with invalid registry type"(){
		given:
			requestMock.getParameter("regType") >> "regType"
			requestMock.getParameter("siteId") >> "siteId"
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"regType1")
			1*mGiftRegistryManager.fetchRegistryTypes("siteId") >> [registryTypeVOMock]
			1*mCatalogTools.getAllValuesForKey("GiftRegistryConfig","DefaultRegistryTypeForThirdParty") >> ["BRD"]
			when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("validRegType", "BRD")
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with registry type list null"(){
		given:
			requestMock.getParameter("regType") >> "regType"
			requestMock.getParameter("siteId") >> "siteId"
			1*mGiftRegistryManager.fetchRegistryTypes("siteId") >> []
			1*mCatalogTools.getAllValuesForKey("GiftRegistryConfig","DefaultRegistryTypeForThirdParty") >> []
			when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("validRegType", null)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with BBBSystemException thrown"(){
		given:
		requestMock.getParameter("regType") >> "regType"
		requestMock.getParameter("siteId") >> "siteId"
		RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"regType")
		1*mGiftRegistryManager.fetchRegistryTypes("siteId") >> {throw new BBBSystemException("BBBSystemException is thrown") }
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg", "err_regsearch_sys_exception")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with BBBBusinessException thrown"(){
		
		given:
		requestMock.getParameter("regType") >> "regType"
		requestMock.getParameter("siteId") >> "siteId"
		RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"regType")
		1*mGiftRegistryManager.fetchRegistryTypes("siteId") >> {throw new BBBBusinessException("BBBBusinessException is thrown") }
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg", "err_regsearch_biz_exception")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	
	}
	def "service method with RepositoryException thrown"(){
		
		given:
		requestMock.getParameter("regType") >> "regType"
		requestMock.getParameter("siteId") >> "siteId"
		RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"regType")
		1*mGiftRegistryManager.fetchRegistryTypes("siteId") >> {throw new RepositoryException("RepositoryException is thrown") }
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg", "err_regsearch_repo_exception")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	
	}
}
