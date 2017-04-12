package atg.sitemap


import java.util.Map;

import com.bbb.constants.BBBCoreConstants;

import atg.repository.RepositoryItem;
import atg.repository.seo.ItemLinkException
import atg.repository.seo.UrlParameter
import atg.repository.seo.UrlParameterLookup
import atg.repository.seo.UrlTemplate
import spock.lang.specification.BBBExtendedSpec;

class BBBMobileChecklistCategoryDynamicSitemapGeneratorSpecification extends BBBExtendedSpec{
	def BBBMobileChecklistCategoryDynamicSitemapGenerator testObj
	def RepositoryItem repositoryItemMock = Mock()
	def SitemapGeneratorService sitemapGeneratorServiceMock = Mock()
	def Map<String, String> urlPrefixFromDomainMap=new HashMap()
	
	def setup()
	{
		testObj=new BBBMobileChecklistCategoryDynamicSitemapGenerator()
	}
	def "getSitemapURL,pass all values"(){
		given:
		testObj.setLoggingDebug(true)
		1*repositoryItemMock.getPropertyValue(BBBCoreConstants.ID) >>'BedBathUS'
		urlPrefixFromDomainMap.put('BedBathUS','BedBathUS=https://m.bedbathandbeyond.com/m')
		testObj.setUrlPrefixFromDomainMap(urlPrefixFromDomainMap)
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, sitemapGeneratorServiceMock,"/FineDining")
		then:
		results == "bedbathus=https://m.bedbathandbeyond.com/m/finedining/"
		
	}
	def "getSitemapURL,pass null value for pSite"(){
		given:
		testObj.setLoggingDebug(false)
		0*repositoryItemMock.getPropertyValue(BBBCoreConstants.ID) >>'BedBathUS'
		urlPrefixFromDomainMap.put('BedBathUS','BedBathUS=https://m.bedbathandbeyond.com/m')
		testObj.setUrlPrefixFromDomainMap(urlPrefixFromDomainMap)
		when:
		def results = testObj.getSitemapURL(null, sitemapGeneratorServiceMock,"")
		then:
		
		results==null
		
	}
	def "getSitemapURL,pass value without / for checklistSEOUrl"(){
		given:
		testObj.setLoggingDebug(false)
		1*repositoryItemMock.getPropertyValue(BBBCoreConstants.ID) >>'BedBathUS'
		urlPrefixFromDomainMap.put('BedBathUS','')
		testObj.setUrlPrefixFromDomainMap(urlPrefixFromDomainMap)
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, sitemapGeneratorServiceMock,"FineDining")
		then:
		
		results==null
		
	}
	def "getSitemapURL,pass empty value for checklistSEOUrl"(){
		given:
		testObj.setLoggingDebug(false)
		1*repositoryItemMock.getPropertyValue(BBBCoreConstants.ID) >>'BedBathUS'
		urlPrefixFromDomainMap.put('BedBathUS','BedBathUS=https://m.bedbathandbeyond.com/m')
		testObj.setUrlPrefixFromDomainMap(urlPrefixFromDomainMap)
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, sitemapGeneratorServiceMock,"")
		then:
		
		results==null
		
	}
		
	def "getSitemapURL,pass all values and throw exception"(){
		given:
		testObj=Spy()
		1*repositoryItemMock.getPropertyValue(BBBCoreConstants.ID) >>{throw new Exception("mock Exception")}
		urlPrefixFromDomainMap.put('BedBathUS','BedBathUS=https://m.bedbathandbeyond.com/m')
		testObj.setUrlPrefixFromDomainMap(urlPrefixFromDomainMap)
		
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, sitemapGeneratorServiceMock,"FineDining")
		then:
		1*testObj.logError(_)
		results == null
	
	}
	def "getSitemapURL,pass logging debug as true and throw exception"(){
		given:
		testObj=Spy()
		repositoryItemMock.getPropertyValue(BBBCoreConstants.ID) >>{throw new Exception("mock Exception")}
		urlPrefixFromDomainMap.put('BedBathUS','BedBathUS=https://m.bedbathandbeyond.com/m')
		testObj.setUrlPrefixFromDomainMap(urlPrefixFromDomainMap)
		testObj.setLoggingDebug(true)
		when:
		def results = testObj.getSitemapURL(repositoryItemMock, sitemapGeneratorServiceMock,"FineDining")
		then:
		2*testObj.logError(_,_)
		results == null
	
	}
}
