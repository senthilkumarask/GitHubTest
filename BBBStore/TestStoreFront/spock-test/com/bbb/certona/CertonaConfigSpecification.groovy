package com.bbb.certona

import static org.junit.Assert.*;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import org.junit.Test;
import spock.lang.specification.BBBExtendedSpec

class CertonaConfigSpecification extends BBBExtendedSpec {
    
	def CertonaConfig testObj
	def BBBCatalogTools catalogToolsMock = Mock() 
	
	
	def setup(){
		testObj = new CertonaConfig (catalogTools:catalogToolsMock)
	}
	
	def "getSiteIdAppIdMap. when site id map is null" (){
		given:
		catalogToolsMock.getConfigValueByconfigType("CertonaKeys") >> ["bedBath":"s1245"]
		when:
		Map<String, String> siteIdMap = testObj.getSiteIdAppIdMap()
		then:
		siteIdMap.get("bedBath") == "s1245"
	}
	
	def "getSiteIdAppIdMap. When method throws BBBSystemException" (){
		given:
		catalogToolsMock.getConfigValueByconfigType("CertonaKeys") >> { throw new BBBSystemException("bbb syntem exception") }
		when:
		Map<String, String> siteIdMap = testObj.getSiteIdAppIdMap()
		then:
		siteIdMap.isEmpty()
	}
	
	def "getSiteIdAppIdMap. When method throws BBBBusinessException" (){
		given:
		catalogToolsMock.getConfigValueByconfigType("CertonaKeys") >> { throw new BBBBusinessException("bbb syntem exception") }
		when:
		Map<String, String> siteIdMap = testObj.getSiteIdAppIdMap()
		then:
		siteIdMap.isEmpty()
	}
	
	def "getSiteIdAppIdMap. when site id map is not  null" (){
		given:
		Map<String, String> siteIdAppMap =  new HashMap<String, String>()
		siteIdAppMap.put("siteId", "s5689")
		testObj.setSiteIdAppIdMap(siteIdAppMap)
		when:
		Map<String, String> siteIdMap = testObj.getSiteIdAppIdMap()
		then:
		siteIdMap.get("siteId") == "s5689"
	}
	
   def "doStartService . TC to get the siteId appId map " (){
	   given:
	   testObj = Spy()
	   testObj.getCatalogTools() >> catalogToolsMock
	   catalogToolsMock.getConfigValueByconfigType("CertonaKeys") >> ["bedBath":"s1245"]
	   
	   1 * testObj.getSiteIdAppIdMap()
	   expect:
	   testObj.doStartService()
   }
}
