package com.bbb.commerce.browse.droplet

import java.util.Iterator;
import java.util.Set

import javax.servlet.http.HttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.logging.LogMessageFormatter;

import atg.nucleus.ServiceMap
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class ProductRedirectDropletSpecification extends BBBExtendedSpec {

	def BBBCatalogTools bbbCatalogTools = Mock()
	def MutableRepository catalogRepository = Mock()
	
	ProductRedirectDroplet droplet = new ProductRedirectDroplet()
	
	def setup(){
		droplet.setCatalogRepository(catalogRepository)
		droplet.setBbbCatalogTools(bbbCatalogTools)
	}
	
	def "service method, happy path"(){
		
		given:
			def RepositoryItem skuRepositoryItem = Mock()
			def Set<RepositoryItem> set = new HashSet<RepositoryItem>()
			def RepositoryItem parentProductRepoItem1 = Mock()
			set.add(parentProductRepoItem1)
			requestMock.getParameter("skuId") >> "skuId"
			catalogRepository.getItem(_, _) >> skuRepositoryItem
			skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
			skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> set
			bbbCatalogTools.isProductActive(parentProductRepoItem1) >> true
			
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("product",parentProductRepoItem1)
			1*requestMock.serviceParameter("output", _, _)
			0*droplet.logDebug("skuId is NULL")
			
	}
	
	def "service method, isProductActive is false and seoProp name is empty"(){
		 
		given:
			def RepositoryItem skuRepositoryItem = Mock()
			def Set<RepositoryItem> set = new HashSet<RepositoryItem>()
			def Iterator<RepositoryItem> parentProductIterator = Mock()
			def RepositoryItem parentProductRepoItem = Mock()
			set.add(parentProductRepoItem)
			requestMock.getParameter("skuId") >> "skuId"
			catalogRepository.getItem(_, _) >> skuRepositoryItem
			skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
			skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> set
			bbbCatalogTools.isProductActive(parentProductRepoItem) >> false
			set.iterator() >> parentProductIterator
			parentProductIterator.next() >> parentProductRepoItem
			parentProductRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) >> ""
			
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("product",parentProductRepoItem)
			1*requestMock.serviceParameter("output", _, _)
			1*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
			0*droplet.logDebug("skuId is NULL")
	}
	
	
	def "service method, parent product is null and BBBBusinessException is thrown"(){
		
	   given:
	   	   droplet = Spy()
           droplet.setCatalogRepository(catalogRepository)
		   def RepositoryItem skuRepositoryItem = Mock()
		   def RepositoryItem parentProductRepoItem = Mock()
		   requestMock.getParameter("skuId") >> "skuId"
		   catalogRepository.getItem(_, _) >> skuRepositoryItem
		   skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		   skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> null
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
	   	   1*droplet.logError("skuId have No active products")
		   1*droplet.logError("browse_1032: Business Exception from service of ProductRedirectDroplet for productId=null |skuId=skuId |SiteId=null",_)
		   2*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   2*requestMock.serviceParameter("error", _, _)
		   
   }
	
	def "service method, parent product is empty and BBBBusinessException is thrown"(){
		
	   given:
		   droplet = Spy()
		   droplet.setCatalogRepository(catalogRepository)
		   def RepositoryItem skuRepositoryItem = Mock()
		   def RepositoryItem parentProductRepoItem = Mock()
		   def Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		   requestMock.getParameter("skuId") >> "skuId"
		   catalogRepository.getItem(_, _) >> skuRepositoryItem
		   skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		   skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> set
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
		   1*droplet.logError("skuId have No active products")
		   1*droplet.logError("browse_1032: Business Exception from service of ProductRedirectDroplet for productId=null |skuId=skuId |SiteId=null",_)
		   2*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   2*requestMock.serviceParameter("error", _, _)
   }
	
	def "service method, isStoreSku is true"(){
		
	   given:
		   droplet = Spy()
		   droplet.setCatalogRepository(catalogRepository)
		   def RepositoryItem skuRepositoryItem = Mock()
		   def RepositoryItem parentProductRepoItem = Mock()
		   def Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		   requestMock.getParameter("skuId") >> "skuId"
		   catalogRepository.getItem(_, _) >> skuRepositoryItem
		   skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> true
		   skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> set
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
		   1*droplet.logError("skuId have No active products")
		   1*droplet.logError("browse_1032: Business Exception from service of ProductRedirectDroplet for productId=null |skuId=skuId |SiteId=null",_)
		   2*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   2*requestMock.serviceParameter("error", _, _)
		   
   }
	
	def "service method, skuId is empty"(){
		
	   given:
	   	   droplet = Spy()
		   requestMock.getParameter("skuId") >> ""
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
	   	   1*droplet.logDebug("skuId is NULL")
		   1*droplet.logError("browse_1032: Business Exception from service of ProductRedirectDroplet for productId=null |skuId= |SiteId=null",_)
		   1*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   1*requestMock.serviceParameter("error", _, _)
		   
   }
	
	def "service method, skuId with length 6"(){
		
	   given:
		   def RepositoryItem productRepositoryItem = Mock()
		   def Set<RepositoryItem> set = new HashSet<RepositoryItem>()
		   requestMock.getParameter("skuId") >> "skuId1"
		   catalogRepository.getItem(_, _) >> productRepositoryItem
		   productRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> true
		   productRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> set
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
	   	   1*requestMock.setParameter("product", productRepositoryItem)
		   1*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   1*requestMock.serviceParameter("output", _, _)
		   0*droplet.logError("skuId1 have No active products")
		   
   }
	
	def "service method, skuRepositoryItem is null"(){
		
	   given:
	   	   droplet = Spy()
		   droplet.setCatalogRepository(catalogRepository)
		   requestMock.getParameter("skuId") >> "skuId"
		   catalogRepository.getItem(_, _) >> null
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
	   	   1*droplet.logError("Repository Item is null for sku id skuId")
		   1*droplet.logError("browse_1032: Business Exception from service of ProductRedirectDroplet for productId=null |skuId=skuId |SiteId=null",_)
		   2*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   1*requestMock.serviceParameter(_, _, _)
		   
   }
	
	def "service method, RepositoryException is thrown"(){
		
	   given:
	   	   droplet = Spy()
		   droplet.setCatalogRepository(catalogRepository)
		   requestMock.getParameter("skuId") >> "skuId1"
		   catalogRepository.getItem(_, _) >> {throw new RepositoryException()}
		   
	   when:
		   droplet.service(requestMock, responseMock)
	   
	   then:
	       1*droplet.logError("RepositoryException from service of ProductRedirectDroplet for productId=null |skuId=skuId1 |SiteId=null",_)
		   1*responseMock.setStatus(HttpServletResponse.SC_NOT_FOUND)
		   1*requestMock.serviceParameter("error", _, _)
		   
   }
}
