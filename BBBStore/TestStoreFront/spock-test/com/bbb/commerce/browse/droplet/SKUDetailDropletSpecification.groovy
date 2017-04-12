package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class SKUDetailDropletSpecification extends BBBExtendedSpec {

	SKUDetailDroplet droplet 
	def ProductManager productManager = Mock()
	def BBBCatalogTools bbbCatalogTools = Mock()
	
	def setup(){
		droplet = new SKUDetailDroplet()
		droplet.setProductManager(productManager)
		droplet.setBbbCatalogTools(bbbCatalogTools)
	}
	
	def "service method, Happy path"(){

		given:
			
			droplet.setProductManager(productManager)
			droplet.setBbbCatalogTools(bbbCatalogTools)
			
			droplet.setMinimal(true)
			requestMock.getParameter("skuId")>> "skuId"
			requestMock.getParameter("fullDetails") >> "true"
			requestMock.getParameter("personalizedFlag") >> "true"
			requestMock.getParameter("personalizedPrice") >> "20"
			
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("pSKUDetailVO",null)
			1*requestMock.serviceParameter("output",requestMock,responseMock)
	}
	
	def "service method, full details , personalizedFlag is null"(){
		
		given:
			droplet.setMinimal(true)
			requestMock.getParameter("skuId")>> "skuId"
			requestMock.getParameter("siteId")>> "siteId"
			requestMock.getParameter("fullDetails") >> null
			requestMock.getParameter("personalizedFlag") >> null
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("pSKUDetailVO",null)
			1*requestMock.serviceParameter("output",requestMock,responseMock)
	}
	
	def "service method, full details and personalizedflag is false"(){
		
		given:
			droplet.setMinimal(true)
			requestMock.getParameter("skuId")>> "skuId"
			requestMock.getParameter("siteId")>> "siteId"
			requestMock.getParameter("fullDetails") >> "false"
			requestMock.getParameter("personalizedFlag") >> "false"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("pSKUDetailVO",null)
			1*requestMock.serviceParameter("output",requestMock,responseMock)
	}
	
	def "service method, personalizedPrice is null"(){
		
		given:
			
			droplet.setProductManager(productManager)
			droplet.setBbbCatalogTools(bbbCatalogTools)
			
			droplet.setMinimal(true)
			requestMock.getParameter("skuId")>> "skuId"
			requestMock.getParameter("fullDetails") >> "true"
			requestMock.getParameter("personalizedFlag") >> "true"
			requestMock.getParameter("personalizedPrice") >> null
			
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("pSKUDetailVO",null)
			1*requestMock.serviceParameter("output",requestMock,responseMock)
	}
	
	def "service method, skuId is null"(){
		
		given:
			droplet.setMinimal(true)
			requestMock.getParameter("skuId")>> null
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setBbbCatalogTools(bbbCatalogTools)
			droplet.setMinimal(false)
			requestMock.getParameter("skuId")>> "skuId"
			1*bbbCatalogTools.getSKUDetails(_,_, _, _) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception from service of SKUDetailDroplet for skuId=skuId |SiteId=null",BBBCoreErrorConstants.BROWSE_ERROR_1033),_)
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
		droplet = Spy()
			droplet.setBbbCatalogTools(bbbCatalogTools)
			droplet.setMinimal(false)
			requestMock.getParameter("skuId")>> "skuId"
			1*bbbCatalogTools.getSKUDetails(_,_, _, _) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception from service of SKUDetailDroplet for skuId=skuId |SiteId=null",BBBCoreErrorConstants.BROWSE_ERROR_1032),_)
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
}
