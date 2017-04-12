package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.repository.RepositoryException
import atg.userprofiling.Profile;
import spock.lang.specification.BBBExtendedSpec

class BridalToolkitLinkDropletSpecification extends BBBExtendedSpec {
	
	BridalToolkitLinkDroplet droplet = new BridalToolkitLinkDroplet()
	GiftRegistryManager mGiftRegistryManager = Mock()
	
	def setup(){
		droplet.setGiftRegistryManager(mGiftRegistryManager)
	}
	
	def "service method"(){
		
		given:
		 Profile profile = Mock()
		 BridalRegistryVO vo = new BridalRegistryVO()
		 requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		 requestMock.getParameter("siteId") >> "BBBUS"
		 profile.isTransient() >> false
		 1*mGiftRegistryManager.getBridalRegistries(_,_) >> [vo]
		
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("bridalRegistryVOList", _)
			1*requestMock.setParameter("size", 1)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service method with isTransient property is true"(){
		
		given:
		 Profile profile = Mock()
		 requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
		 requestMock.getParameter("siteId") >> "BBBUS"
		 profile.isTransient() >> true
		
		when:
			droplet.service(requestMock,responseMock)
		then:
			0*requestMock.setParameter("bridalRegistryVOList", _)
			0*requestMock.setParameter("size", _)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*mGiftRegistryManager.getBridalRegistries(_,_)
	}
	
	def "service method with RepositoryException thrown"(){
		
		given:
			 Profile profile = Mock()
			 requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			 requestMock.getParameter("siteId") >> "BBBUS"
			 profile.isTransient() >> false
			 mGiftRegistryManager.getBridalRegistries(_,_) >> {throw new RepositoryException("RepositoryException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("errorMsg", _)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with BBBSystemException thrown"(){
		
		given:
			 Profile profile = Mock()
			 requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			 requestMock.getParameter("siteId") >> "BBBUS"
			 profile.isTransient() >> false
			 mGiftRegistryManager.getBridalRegistries(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("errorMsg", _)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with BBBBusinessException thrown"(){
		
		given:
			 Profile profile = Mock()
			 requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			 requestMock.getParameter("siteId") >> "BBBUS"
			 profile.isTransient() >> false
			 mGiftRegistryManager.getBridalRegistries(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		then:
			1*requestMock.setParameter("errorMsg", _)
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
}
