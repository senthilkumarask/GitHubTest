package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.multisite.SiteContext;
import atg.repository.RepositoryException
import spock.lang.specification.BBBExtendedSpec

class GiftRegistryTypesDropletSpecification extends BBBExtendedSpec {

	GiftRegistryTypesDroplet droplet
	GiftRegistryManager mGiftRegistryManager
	
	def setup(){
		droplet = new GiftRegistryTypesDroplet()
		mGiftRegistryManager = Mock()
		droplet.setGiftRegistryManager(mGiftRegistryManager)
	}
	
	def "service method happy path"(){
		
		given:
			requestMock.getParameter("siteId") >> "BBBUS"
			RegistryTypeVO typeVO = new RegistryTypeVO()
			1*mGiftRegistryManager.fetchRegistryTypes("BBBUS") >> [typeVO]
			typeVO.setRegistryIndex(2)
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryTypes", [typeVO]);
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock);
	}
	
	def "service method with BBBSystemException thrown"(){
		
		given:
			requestMock.getParameter("siteId") >> "BBBUS"
			1*mGiftRegistryManager.fetchRegistryTypes("BBBUS") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_sys_exception")
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with BBBBusinessException thrown"(){
		
		given:
			requestMock.getParameter("siteId") >> "BBBUS"
			1*mGiftRegistryManager.fetchRegistryTypes("BBBUS") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_biz_exception")
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with RepositoryException thrown"(){
		
		given:
			requestMock.getParameter("siteId") >> "BBBUS"
			1*mGiftRegistryManager.fetchRegistryTypes("BBBUS") >> {throw new RepositoryException()}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_repo_exception")
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
}
