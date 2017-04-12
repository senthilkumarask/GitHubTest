package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.constants.BBBCmsConstants
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class ProductRelatedCategoriesDropletSpecification extends BBBExtendedSpec {
	
	def ProductManager productManager = Mock()
	
	ProductRelatedCategoriesDroplet droplet = new ProductRelatedCategoriesDroplet()
	
	def setup(){
		droplet.setProductManager(productManager)
	}
	
	def "service method, happy path"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			def CategoryVO vo = new CategoryVO()
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> null
			droplet.extractCurrentSiteId() >> "siteId"
			1*productManager.getProductRelatedCategories(_,_) >> [vo]
			
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter(BBBCoreConstants.RELATED_CATEGORIES, [vo])
			1*requestMock.serviceParameter("output", requestMock,responseMock)
	}
	
	def "service method, siteId not null and productId is empty"(){
		
		given:
			droplet = Spy()
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> ""
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter(BBBCoreConstants.RELATED_CATEGORIES, _)
			1*requestMock.serviceParameter("output", requestMock,responseMock)
			1*droplet.logError("ProductRelatedCategoriesDroplet :: Product id is null ")
	}
	
	def "service method, relatedCategories is null"(){
		
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			1*productManager.getProductRelatedCategories(_,_) >> null
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter(BBBCoreConstants.RELATED_CATEGORIES, null)
			1*requestMock.serviceParameter("output", requestMock,responseMock)
			0*droplet.logError("ProductRelatedCategoriesDroplet :: Product id is null ")
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			1*productManager.getProductRelatedCategories(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError("System Exception from service of ProductRelatedCategoriesDroplet for productId=prodId |SiteId=siteId",_)
			1*requestMock.serviceParameter("error", requestMock,responseMock)
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			1*productManager.getProductRelatedCategories(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError("System Exception from service of ProductRelatedCategoriesDroplet for productId=prodId |SiteId=siteId",_)
			1*requestMock.serviceParameter("error", requestMock,responseMock)
	}
}
