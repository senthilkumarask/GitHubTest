package com.bbb.commerce.browse.droplet

import java.util.Map;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class MobileCategoryOrderDropletSpecification extends BBBExtendedSpec {
	
	def ProductManager productManager = Mock()
	
	MobileCategoryOrderDroplet droplet = Spy()
	
	def setup(){
		droplet.setProductManager(productManager)
		droplet.setLoggingDebug(true)
		droplet.setLoggingError(true)
	}
	
	def "service method, happy path"(){
		
		given:
			def Map<String, CategoryVO> categoryHierarchy = new HashMap<String,CategoryVO>()
			requestMock.getParameter("categoryId") >> "categoryId"
			requestMock.getParameter("siteId") >> "siteId"
			1*productManager.getParentCategory(_,_) >> categoryHierarchy
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("categoryOrder",_)
			1*requestMock.serviceParameter("output", _, _)
			0*droplet.logDebug("Empty Category id for Mobile meta tag content")
	}
	
	def "service method, categoryId is empty"(){
		
		given:
			def Map<String, CategoryVO> categoryHierarchy = new HashMap<String,CategoryVO>()
			requestMock.getParameter("categoryId") >> ""
			requestMock.getParameter("siteId") >> "siteId"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("categoryOrder",_);
			1*requestMock.serviceParameter("output", _, _)
			1*droplet.logDebug("Empty Category id for Mobile meta tag content")
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			requestMock.getParameter("categoryId") >> "categoryId"
			requestMock.getParameter("siteId") >> "siteId"
			1*productManager.getParentCategory(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError("BBBSystem Exception in finding category order for category IdcategoryId",_)
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
			requestMock.getParameter("categoryId") >> "categoryId"
			requestMock.getParameter("siteId") >> "siteId"
			1*productManager.getParentCategory(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError("BBBBusiness Exception in finding category order for category IdcategoryId",_)
	}
}
