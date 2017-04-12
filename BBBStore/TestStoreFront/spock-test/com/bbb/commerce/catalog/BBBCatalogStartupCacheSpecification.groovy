package com.bbb.commerce.catalog

import java.util.concurrent.ExecutorService

import atg.commerce.order.ItemAddedToOrder;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class BBBCatalogStartupCacheSpecification extends BBBExtendedSpec {

	BBBCatalogStartupCache cache
	BBBCatalogTools mCatalogTools
	String mSkuThresholdsRql = "rql"
	int mPoolSize = 10
	int mRangeCount = 0
	
	def setup(){
		cache = Spy()
		mCatalogTools = Mock()
		cache.setCatalogTools(mCatalogTools)
		cache.setSkuThresholdsRql(mSkuThresholdsRql)
		cache.setPoolSize(mPoolSize)
		cache.setRangeCount(mRangeCount)
	}
	
	def "doStartService method"(){
		given:
			RepositoryItem item = Mock()
			Map<String,String> cachingQueries = new HashMap<String,String>()
			cachingQueries.put("rql5", "rql5")
			cachingQueries.put("rql1", "rql1")
			cachingQueries.put("rql2", "rql2")
			cachingQueries.put("rql3", "rql3")
			cachingQueries.put("rql4", "rql4")
			mCatalogTools.getConfigValueByconfigType("CatalogCacheQueries") >> cachingQueries
			mCatalogTools.executeRQLQuery(_,"rql2") >> {throw new RepositoryException()}
			mCatalogTools.executeRQLQuery(_,"rql3") >> {throw new BBBSystemException("BBBSystemException is thrown")}
			mCatalogTools.executeRQLQuery(_,"rql4") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			mCatalogTools.executeRQLQuery(_,"rql5") >> []
			mCatalogTools.executeRQLQuery(_,"rql1") >> [item]
		
		expect:
			cache.doStartService()
		
	}
	def "doStartService method with cachingQueries null"(){
		given:
			mCatalogTools.getConfigValueByconfigType("CatalogCacheQueries") >> null
		
		expect:
			cache.doStartService()
		
	}
	
	def "doStartService method with getConfigValue throwing BBBSystemException"(){
		given:
			mCatalogTools.getConfigValueByconfigType("CatalogCacheQueries") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			cache.doStartService()
		then:
			1*cache.logError("BBBSystemException occured:",_)
		
	}
	
	def "doStartService method with getConfigValue throwing BBBBusinessException"(){
		given:
			mCatalogTools.getConfigValueByconfigType("CatalogCacheQueries") >>  {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			cache.doStartService()
		then:
			1*cache.logError("BBBBusinessException occured:",_);
	}
}
