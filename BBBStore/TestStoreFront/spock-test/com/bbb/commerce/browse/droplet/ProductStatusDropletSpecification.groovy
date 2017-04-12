package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.droplet.ProductStatusDroplet
import com.bbb.commerce.browse.droplet.SKUDetailDroplet;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCmsConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class ProductStatusDropletSpecification extends BBBExtendedSpec {
	
	ProductStatusDroplet droplet = new ProductStatusDroplet()
	def BBBCatalogTools catalogTools = Mock()
	
	def setup(){
		droplet.setCatalogTools(catalogTools)
	}
	
	def "service method, Happy path"(){
		
		given:
			droplet = Spy()
			droplet.setCatalogTools(catalogTools)
			
			requestMock.getParameter("productId") >> "prodId"
			droplet.extractSiteId() >> "siteId"
			1*catalogTools.isProductActive(_) >> true
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("isProductActive", true);
			1*requestMock.serviceParameter("output", requestMock, responseMock)
		
	}
	
	def "service method, prodId is null"(){
		
		given:
			requestMock.getParameter("productId") >> null
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
	def "service method, siteId is not null and BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setCatalogTools(catalogTools)
		    requestMock.getParameter("productId") >> "prodId"
		    requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			1*catalogTools.isProductActive(_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception from service of ProductStatusDroplet for productId=prodId |SiteId=siteId",BBBCoreErrorConstants.BROWSE_ERROR_1033),_);
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
	def "service method, siteId is not null and BBBBusinessException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setCatalogTools(catalogTools)
			requestMock.getParameter("productId") >> "prodId"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			1*catalogTools.isProductActive(_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception from service of ProductStatusDroplet for productId=prodId |SiteId=siteId",BBBCoreErrorConstants.BROWSE_ERROR_1032),_);
			1*requestMock.serviceParameter("error", requestMock, responseMock)
		
	}
	
}
