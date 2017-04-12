package com.bbb.redirectURLs;

import spock.lang.specification.BBBExtendedSpec
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.exception.BBBBusinessException
import com.bbb.framework.cache.BBBObjectCache
import com.bbb.repository.RepositoryItemMock
import com.tangosol.net.NamedCache


public class CategoryRedirectURLLoaderSpecification extends BBBExtendedSpec {

	def CategoryRedirectURLLoader testCategoryRedirectURLLoader
	def BBBCatalogTools bbbCatalogToolsMock = Mock()
	def NamedCache namedCacheMock = Mock()
	def MutableRepository catalogRepositoryMock = Mock()
	def RepositoryItemDescriptor repositoryItemDescriptorMock = Mock()
	def RepositoryView repositoryViewMock = Mock()
	def RqlStatement rqlStatementMock = Mock()
	def RepositoryItemMock repositoryItemMock = new RepositoryItemMock()
	def RepositoryItem[] repositoryItemList = [repositoryItemMock]
	def BBBObjectCache bbbObjectCacheMock = Mock()
	
	def setup() {
		testCategoryRedirectURLLoader = Spy()
		testCategoryRedirectURLLoader.setCatalogTools(bbbCatalogToolsMock)
		testCategoryRedirectURLLoader.setCatalogRepository(catalogRepositoryMock)
		testCategoryRedirectURLLoader.setObjectCache(bbbObjectCacheMock)
	}
	
	def "doStartService method : Happy Flow"() {
		
		given:
		  bbbCatalogToolsMock.getAllValuesForKey(*_) >> ["aaaa"]
		  testCategoryRedirectURLLoader.getCache() >> namedCacheMock
		  testCategoryRedirectURLLoader.convertObjectToMap(namedCacheMock) >> [:]
		  catalogRepositoryMock.getItemDescriptor("bccManagedCategoryRedirectURLs") >> repositoryItemDescriptorMock
		  repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		  repositoryItemMock.setRepositoryId("56556")
		  repositoryItemMock.setProperties(["sourceCategoryId":repositoryItemMock, "mobileTargetRedirectURL":"mobileURL", "targetRedirectURL":"dskURL"])
		  testCategoryRedirectURLLoader.executeRQLQuery(*_) >> repositoryItemList
		when:
		  testCategoryRedirectURLLoader.doStartService()
		  
		then:
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.containsKey("56556")
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.containsValue("dskURL")
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.size() == 1
		  0 * testCategoryRedirectURLLoader.logError(_)
		  
		 
	}
	
	def "doStartService method : Exception Scenarios with loggingDebug true"() {
		
		given:
		  testCategoryRedirectURLLoader.setLoggingDebug(true)
		  testCategoryRedirectURLLoader.fetchCategoryRedirectURLs() >> {throw new RepositoryException("ERROR")}
		when:
		  testCategoryRedirectURLLoader.doStartService()
		  
		then:
		1 * testCategoryRedirectURLLoader.logError(_)
	}
	
	def "fetchCategoryRedirectURLs method : Happy Flow"() {
		
		given:
		  bbbCatalogToolsMock.getAllValuesForKey(*_) >> ["aaaa"]
		  testCategoryRedirectURLLoader.getCache() >> namedCacheMock
	  	  testCategoryRedirectURLLoader.convertObjectToMap(namedCacheMock) >> [:]
		  catalogRepositoryMock.getItemDescriptor("bccManagedCategoryRedirectURLs") >> repositoryItemDescriptorMock
		  repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		  repositoryItemMock.setRepositoryId("56556")
		  repositoryItemMock.setProperties(["sourceCategoryId":repositoryItemMock, "mobileTargetRedirectURL":"mobileURL", "targetRedirectURL":"dskURL"])		
		  testCategoryRedirectURLLoader.executeRQLQuery(*_) >> repositoryItemList
		
		when:
		  def Map<String, String> resultMap = testCategoryRedirectURLLoader.fetchCategoryRedirectURLs()
		  
		then:
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.containsKey("56556")
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.containsValue("dskURL")
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.size() == 1
		  resultMap.size() == 1
		  0 * testCategoryRedirectURLLoader.logError(_)
	}
	
	def "fetchCategoryRedirectURLs method : CacheMap with values"() {
		
		given:
		  bbbCatalogToolsMock.getAllValuesForKey(*_) >> ["aaaa"]
		  testCategoryRedirectURLLoader.getCache() >> namedCacheMock
		  testCategoryRedirectURLLoader.convertObjectToMap(namedCacheMock) >> ["89898":["mobileCategoryRedirectURL":"aa", "desktopCategoryRedirectURL":"bb"]]
		
		when:
		  def Map<String, String> resultMap = testCategoryRedirectURLLoader.fetchCategoryRedirectURLs()
		  
		then:
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.containsKey("89898")
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.containsValue("bb")
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.size() == 1
		  resultMap.size() == 1
		  0 * testCategoryRedirectURLLoader.logError(_)
		  0 * testCategoryRedirectURLLoader.executeRQLQuery(_)
		 
	}
	
	def "fetchCategoryRedirectURLs method : repositoryItemMock as null"() {
		
		given:
		  bbbCatalogToolsMock.getAllValuesForKey(*_) >> ["aaaa"]
		  testCategoryRedirectURLLoader.getCache() >> namedCacheMock
		  testCategoryRedirectURLLoader.convertObjectToMap(namedCacheMock) >> [:]
		  catalogRepositoryMock.getItemDescriptor("bccManagedCategoryRedirectURLs") >> repositoryItemDescriptorMock
		  repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		  testCategoryRedirectURLLoader.executeRQLQuery(*_) >> null
		
		when:
		  def Map<String, String> resultMap = testCategoryRedirectURLLoader.fetchCategoryRedirectURLs()
		  
		then:
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.size() == 0
		  resultMap.size() == 0
		  0 * testCategoryRedirectURLLoader.logError(_)
		  0 * testCategoryRedirectURLLoader.getCategoryRedirectURLMap().put(_)
	}
	
	def "fetchCategoryRedirectURLs method : mobileTargetRedirectURL and targetRedirectURL as null"() {
		
		given:
	      bbbCatalogToolsMock.getAllValuesForKey(*_) >> ["aaaa"]
		  testCategoryRedirectURLLoader.getCache() >> namedCacheMock
		  testCategoryRedirectURLLoader.convertObjectToMap(namedCacheMock) >> [:]
		  catalogRepositoryMock.getItemDescriptor("bccManagedCategoryRedirectURLs") >> repositoryItemDescriptorMock
		  repositoryItemDescriptorMock.getRepositoryView() >> repositoryViewMock
		  repositoryItemMock.setRepositoryId("56556")
		  repositoryItemMock.setProperties(["sourceCategoryId":repositoryItemMock, "mobileTargetRedirectURL":null, "targetRedirectURL":null])
		  testCategoryRedirectURLLoader.executeRQLQuery(*_) >> repositoryItemList
		
		when:
		  def Map<String, String> resultMap = testCategoryRedirectURLLoader.fetchCategoryRedirectURLs()
		  
		then:
		  testCategoryRedirectURLLoader.categoryRedirectURLMap.size() == 0
		  0 * testCategoryRedirectURLLoader.getCategoryRedirectURLMap().put(_)
		  resultMap.size() == 0
		 
	}
	
	def "fetchCategoryRedirectURLs method : exception scenarios with loggingDebug true"() {
		
		given:
		  testCategoryRedirectURLLoader.setLoggingDebug(true)
		  bbbCatalogToolsMock.getAllValuesForKey(*_) >> ["aaaa"]
		  testCategoryRedirectURLLoader.getCache() >> {throw new BBBBusinessException("ERROR")} 
		
		when:
		  def Map<String, String> resultMap = testCategoryRedirectURLLoader.fetchCategoryRedirectURLs()
		  
		then:
		   1 * testCategoryRedirectURLLoader.logError(_)
		   resultMap.size() == 0
		 
	}
}
