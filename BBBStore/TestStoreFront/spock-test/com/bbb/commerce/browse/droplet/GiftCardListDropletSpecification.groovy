package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.droplet.GiftCardListDroplet
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.multisite.SiteContextManager
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class GiftCardListDropletSpecification extends BBBExtendedSpec {

	def BBBCatalogTools mCatalogTools = Mock()
	
	GiftCardListDroplet droplet = new GiftCardListDroplet()
	
	def setup(){
		droplet.setCatalogTools(mCatalogTools)
	}
	
	def "service method, happy path"(){
		
		given:
			def ProductVO vo = new ProductVO()
			requestMock.getParameter("siteId") >> "siteId"
			1*mCatalogTools.getGiftProducts(_) >> [vo]
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter(BBBSearchBrowseConstants.PRODUCT_VO_LIST,[vo]);
			1*requestMock.serviceLocalParameter("output", requestMock,responseMock)
	}
	
	def "service method, siteId is not null and BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setCatalogTools(mCatalogTools)
			
			def ProductVO vo = new ProductVO()
			droplet.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getGiftProducts(_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg",_);
			1*requestMock.serviceLocalParameter("error", _, _)
	}
	
	def "service method, siteId is not null and BBBBusinessException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setCatalogTools(mCatalogTools)
		
			def ProductVO vo = new ProductVO()
			droplet.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getGiftProducts(_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception from service of GiftCardListDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1029),_)
			1*requestMock.setParameter(_,_);
			1*requestMock.serviceLocalParameter(_, _, _)
	}
}
