package com.bbb.cms.droplet

import static org.junit.Assert.*

import com.bbb.cms.PromoBoxContentVO
import com.bbb.cms.manager.LandingTemplateManager
import com.bbb.commerce.browse.droplet.ProductDetailDroplet
import atg.repository.RepositoryException

import org.junit.Test
import spock.lang.specification.BBBExtendedSpec;

class PromotionTabFlyoutDropletSpecification extends BBBExtendedSpec {
	

	def LandingTemplateManager landingTemplateManagerMock = Mock()
	def PromotionTabFlyoutDroplet promoTabFlyoutTestObj
	def PromoBoxContentVO promoBoxContentVOobject = new PromoBoxContentVO()
	
	def setup(){
		promoTabFlyoutTestObj = new PromotionTabFlyoutDroplet(mLandingTemplateManager:landingTemplateManagerMock)
	}
    
	def "service: TC to check whether promoContentVO is returned"(){
		
		given:
		String promoTabId ="DC1600001"
		requestMock.getParameter("promoTabId") >> promoTabId 
		landingTemplateManagerMock.getPromotionTab(promoTabId) >> promoBoxContentVOobject
		when:
		promoTabFlyoutTestObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("promoBoxContentVO", promoBoxContentVOobject)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service: TC for RepositoryException"(){
		
		given:
		String promoTabId ="DC1600002"
		requestMock.getParameter("promoTabId") >> promoTabId
		landingTemplateManagerMock.getPromotionTab(promoTabId) >> {throw new RepositoryException("exception") }
		when:
		promoTabFlyoutTestObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("promoBoxContentVO", null)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service: TC for promoTabId is null"(){
		
		given:
		String promoTabId = null;
		requestMock.getParameter("promoTabId") >> promoTabId
		0*landingTemplateManagerMock.getPromotionTab(promoTabId) >> null
		when:
		promoTabFlyoutTestObj.service(requestMock, responseMock)
		then:
		0*requestMock.setParameter("promoBoxContentVO", null)
		0*requestMock.serviceParameter("output", requestMock, responseMock)
	}
}
