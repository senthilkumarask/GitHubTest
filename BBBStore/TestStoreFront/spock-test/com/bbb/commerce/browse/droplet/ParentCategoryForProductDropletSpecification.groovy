package com.bbb.commerce.browse.droplet

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class ParentCategoryForProductDropletSpecification extends BBBExtendedSpec {
	
	def BBBCatalogTools mCatalogTools = Mock()
	
	ParentCategoryForProductDroplet droplet = new ParentCategoryForProductDroplet()
	
	def setup(){
		droplet.setBbbCatalogTools(mCatalogTools)
	}
	
	def "service method, happy path"(){
		
		given:
			requestMock.getParameter("productId") >> "prodId"
			requestMock.getParameter("siteId") >> "siteId"
			droplet.bbbCatalogTools.getParentCategoryIdForProduct(_,_) >> "categoryId"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("categoryId",_);
			1*requestMock.serviceParameter("output", _, _)
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setBbbCatalogTools(mCatalogTools)
			requestMock.getParameter("productId") >> "prodId"
			requestMock.getParameter("siteId") >> "siteId"
			droplet.bbbCatalogTools.getParentCategoryIdForProduct(_,_) >>  {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError("System Exception occured while fetching parent category Id from service of ParentCategoryForProductDroplet for product ID : prodId",_)
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setBbbCatalogTools(mCatalogTools)
			requestMock.getParameter("productId") >> "prodId"
			requestMock.getParameter("siteId") >> "siteId"
			droplet.bbbCatalogTools.getParentCategoryIdForProduct(_,_) >>  {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError("Business Exception occured while fetching parent category Id from service of ParentCategoryForProductDroplet for product ID : prodId",_)
	}
	
}
