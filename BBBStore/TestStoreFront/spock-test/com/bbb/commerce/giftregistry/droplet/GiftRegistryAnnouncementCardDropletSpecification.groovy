package com.bbb.commerce.giftregistry.droplet

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.repository.RepositoryException
import atg.userprofiling.Profile;
import spock.lang.specification.BBBExtendedSpec

class GiftRegistryAnnouncementCardDropletSpecification extends BBBExtendedSpec {
	GiftRegistryAnnouncementCardDroplet droplet
	GiftRegistryManager giftRegistryManager
	LblTxtTemplateManager lblTxtTemplateManager
	
	def setup(){
		droplet = new GiftRegistryAnnouncementCardDroplet()
		giftRegistryManager = Mock()
		lblTxtTemplateManager = Mock()
		droplet.setGiftRegistryManager(giftRegistryManager)
		droplet.setLblTxtTemplateManager(lblTxtTemplateManager)
	}
	
	def "service method hapy path"(){
		given:
			Profile profile = Mock()
			RegistrySkinnyVO registry = new RegistrySkinnyVO()
			requestMock.getParameter("siteId") >> "BuyBuyBaby"
			requestMock.getObjectParameter("profile") >> profile
			1*giftRegistryManager.fetchUsersBabyRegistries(_,_) >> [registry]
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registries", _)
			1*requestMock.setParameter("count", 1)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("errorMsg", _)
	}
	
	def "service method with profile non transient"(){
		given:
			Profile profile = Mock()
			Locale locale = new Locale("")
			profile.isTransient() >> true
			requestMock.getObjectParameter("profile") >> profile
			requestMock.getLocale() >> locale
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("registries", _)
			0*requestMock.setParameter("count", 1)
			1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
	
	def "service method with profile null"(){
		given:
			Locale locale = new Locale("")
			requestMock.getObjectParameter("profile") >> null
			requestMock.getLocale() >> locale
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("registries", _)
			0*requestMock.setParameter("count", 1)
			1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
	
	def "service method registries null and siteId as BedBathUs"(){
		given:
			Profile profile = Mock()
			Locale locale = new Locale("")
			requestMock.getParameter("siteId") >> "BedBathUs"
			requestMock.getObjectParameter("profile") >> profile
			giftRegistryManager.fetchUsersBabyRegistries(_,_) >> null
			requestMock.getLocale() >> locale
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("registries", _)
			0*requestMock.setParameter("count", 1)
			1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
	def "service method registries empty and siteId as BuyBuyBaby"(){
		given:
			Profile profile = Mock()
			Locale locale = new Locale("")
			requestMock.getParameter("siteId") >> "BuyBuyBaby"
			requestMock.getObjectParameter("profile") >> profile
			giftRegistryManager.fetchUsersBabyRegistries(_,_) >> []
			requestMock.getLocale() >> locale
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("registries", _)
			0*requestMock.setParameter("count", 1)
			1*requestMock.serviceLocalParameter("empty", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
	
	def "service method with RepositoryException is thrown"(){
		given:
			Profile profile = Mock()
			requestMock.getParameter("siteId") >> "BuyBuyBaby"
			requestMock.getObjectParameter("profile") >> profile
			giftRegistryManager.fetchUsersBabyRegistries(_,_) >> {throw new RepositoryException()}
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
	
	def "service method with BBBSystemException is thrown"(){
		given:
			Profile profile = Mock()
			requestMock.getParameter("siteId") >> "BuyBuyBaby"
			requestMock.getObjectParameter("profile") >> profile
			giftRegistryManager.fetchUsersBabyRegistries(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
	
	def "service method with BBBBusinessException is thrown"(){
		given:
			Profile profile = Mock()
			requestMock.getParameter("siteId") >> "BuyBuyBaby"
			requestMock.getObjectParameter("profile") >> profile
			giftRegistryManager.fetchUsersBabyRegistries(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("errorMsg", _)
	}
}
