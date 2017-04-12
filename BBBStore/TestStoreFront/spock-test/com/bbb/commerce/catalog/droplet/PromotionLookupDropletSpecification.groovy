package com.bbb.commerce.catalog.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.selfservice.droplet.ProcessCouponDroplet

import atg.multisite.SiteContextManager;
import spock.lang.specification.BBBExtendedSpec

class PromotionLookupDropletSpecification extends BBBExtendedSpec {

	PromotionLookupDroplet droplet
	ProcessCouponDroplet processCouponDroplet
	String language
	BBBPromotionTools mPromTools
	
	def setup(){
		droplet = Spy()
		processCouponDroplet = Mock()
		mPromTools = Mock()
		language = "en"
		droplet.setProcessCouponDroplet(processCouponDroplet)
		droplet.setPromTools(mPromTools)
	}
	
	def "service method happy path"(){
		
		given:
			droplet.extractCurrentSiteId() >> ""
			1*requestMock.getParameter("siteId") >> "BBBCanada"
			2*requestMock.getLocalParameter(BBBCoreConstants.PROMOTION_ID) >> "promoId"
			1*mPromTools.getPromotionCouponKey(_, BBBCoreConstants.ACTIVATION_LABEL_ID, _, _, false) >> "promoDetails"
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter(BBBCoreConstants.PROMTION_DETAILS, _)
			1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock)
	}
	
	def "service method with siteId blank and promoId null and promodetails empty"(){
		
		given:
			droplet.extractCurrentSiteId() >> "BBBCanada"
			0*requestMock.getParameter("siteId") >> "BBBCanada"
			1*requestMock.getLocalParameter(BBBCoreConstants.PROMOTION_ID) >> null
			1*mPromTools.getPromotionCouponKey(_, BBBCoreConstants.ACTIVATION_LABEL_ID, _, _, false) >> ""
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			2*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock, responseMock)
	}
}
