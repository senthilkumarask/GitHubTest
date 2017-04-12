package com.bbb.commerce.order.droplet

import atg.nucleus.naming.ParameterName;
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.constants.BBBCheckoutConstants;

import spock.lang.specification.BBBExtendedSpec

class EcoFeeApplicabilityCheckDropletSpecification extends BBBExtendedSpec {
	def EcoFeeApplicabilityCheckDroplet efacDroplet
	def BBBCatalogToolsImpl catalogToolsMock = Mock()
	def MutableRepository catalogRep = Mock()
	def RepositoryItem skuRepositoryItem = Mock()
	def RepositoryItem ecoFreeSku= Mock()
	
	def setup(){
		efacDroplet = new EcoFeeApplicabilityCheckDroplet(catalogTools : catalogToolsMock)
	}
	def"service method. TC checks if Eco Fee is applicable for particular sku"(){
		def Set ecoFreeSkuSet = [ecoFreeSku]
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getCatalogRepository() >> catalogRep
		1*catalogRep.getItem(skuId, "sku")  >> skuRepositoryItem
		1*catalogToolsMock.isSkuActive(skuRepositoryItem) >> true
		2*skuRepositoryItem.getPropertyValue("ecoFeeSKUs") >> ecoFreeSkuSet
		 
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", true)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}
	
	def"service method. TC checks if Eco Fee is applicable for particular sku when ecoFreeSkuSet is empty "(){
		def Set ecoFreeSkuSet = []
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getCatalogRepository() >> catalogRep
		1*catalogRep.getItem(skuId, "sku")  >> skuRepositoryItem
		1*catalogToolsMock.isSkuActive(skuRepositoryItem) >> true
		2*skuRepositoryItem.getPropertyValue("ecoFeeSKUs") >> ecoFreeSkuSet
		 
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", false)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}
	
	def"service method. TC checks if Eco Fee is applicable for particular sku when ecoFreeSkuSet is null "(){
		def Set ecoFreeSkuSet = []
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getCatalogRepository() >> catalogRep
		1*catalogRep.getItem(skuId, "sku")  >> skuRepositoryItem
		1*catalogToolsMock.isSkuActive(skuRepositoryItem) >> true
		1*skuRepositoryItem.getPropertyValue("ecoFeeSKUs") >> null
		 
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", false)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}
	
	def"service method. TC checks if Eco Fee is applicable for particular sku when sku is not active  "(){
		def Set ecoFreeSkuSet = [ecoFreeSku]
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getCatalogRepository() >> catalogRep
		1*catalogRep.getItem(skuId, "sku")  >> skuRepositoryItem
		1*catalogToolsMock.isSkuActive(skuRepositoryItem) >> false
		 
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", false)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}
	
	def"service method. TC checks if Eco Fee is applicable for particular sku when skuRepositoryItem is not active  "(){
		def Set ecoFreeSkuSet = [ecoFreeSku]
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getCatalogRepository() >> catalogRep
		1*catalogRep.getItem(skuId, "sku")  >> null
		 
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", false)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}
	
	def"service method. TC checks if Eco Fee is applicable for particular sku for RepositoryException   "(){
		def Set ecoFreeSkuSet = [ecoFreeSku]
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> skuId
		catalogToolsMock.getCatalogRepository() >> catalogRep
		1*catalogRep.getItem(skuId, "sku")  >> {throw new RepositoryException("exception")}
		 
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", false)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}
	def"service method. TC when skuId is blanck  "(){
		def Set ecoFreeSkuSet = [ecoFreeSku]
		given:
		String skuId = "skuId"
		requestMock.getObjectParameter("skuId") >> ""
		
		when:
		efacDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("isEcoFee", false)
		0*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
	}

}
