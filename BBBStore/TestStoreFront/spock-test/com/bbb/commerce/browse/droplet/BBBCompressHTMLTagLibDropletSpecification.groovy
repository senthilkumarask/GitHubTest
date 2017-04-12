package com.bbb.commerce.browse.droplet

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author Velmurugan Moorthy
 *
 */
class BBBCompressHTMLTagLibDropletSpecification extends BBBExtendedSpec {
	
	private BBBCompressHTMLTagLibDroplet compressHtmlTagLibDroplet
	private BBBCatalogTools catalogToolsMock
	def htmlTagLibUserAgents
	private BBBSessionBean sessionBeanMock
	def setup() {
		
		catalogToolsMock = Mock()
		sessionBeanMock = Mock()
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		htmlTagLibUserAgents = "(.*)(iPad|ipad)(.*)"
		compressHtmlTagLibDroplet = new BBBCompressHTMLTagLibDroplet([catalogTools : catalogToolsMock])
		
	}
	
	
	/*=======================================================
	 * 
	 * service - test cases - STARTS
	 * 
	 * Method signature : 
	 * 
	 * public void service
	 * (
	 *  final DynamoHttpServletRequest pRequest,
	 * 	final DynamoHttpServletResponse pResponse
	 * )
	 *======================================================= 
	 */
	
	def "service - HTML data compressed successfully" () {
		
		given : 
		
		def header = "ipad"
		
		BBBCompressHTMLTagLibDroplet compressHtmlTagLibDropletSpy = Spy()
		
		sessionBeanMock.getCompressHtmlTagLib() >> htmlTagLibUserAgents
		
		compressHtmlTagLibDropletSpy.setCatalogTools(catalogToolsMock)
		
		requestMock.getHeader("User-Agent") >>header
		1 * catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS) >> [htmlTagLibUserAgents,htmlTagLibUserAgents,"",null]
		when:
		compressHtmlTagLibDropletSpy.service(requestMock, responseMock)
		then:
		1 * sessionBeanMock.setCompressHtmlTagLib("true")
		
	}
	
	
	def "service - Exception while compressing the data | BBBSystemException" () {
		
		given :
		
		def header = "ipad"
		
		BBBCompressHTMLTagLibDroplet compressHtmlTagLibDropletSpy = Spy()
		
		sessionBeanMock.getCompressHtmlTagLib() >> htmlTagLibUserAgents
		
		compressHtmlTagLibDropletSpy.setCatalogTools(catalogToolsMock)
		
		requestMock.getHeader("User-Agent") >>header
		1*catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS) >> {throw new BBBSystemException("")}
		
		
		when : 
		
		compressHtmlTagLibDropletSpy.service(requestMock, responseMock)
		then:
		1 * sessionBeanMock.setCompressHtmlTagLib("true")
		
	}
	
	
	def "service - Exception while compressing the data | BBBBusinessException" () {
		
		given :
		
		def header = "ipad"
		
		BBBCompressHTMLTagLibDroplet compressHtmlTagLibDropletSpy = Spy()
		
		sessionBeanMock.getCompressHtmlTagLib() >> htmlTagLibUserAgents
		
		compressHtmlTagLibDropletSpy.setCatalogTools(catalogToolsMock)
		
		requestMock.getHeader("User-Agent") >>header
		1*catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS) >> {throw new BBBBusinessException("")}
		
		when :
		
		compressHtmlTagLibDropletSpy.service(requestMock, responseMock)
		then:
		1 * sessionBeanMock.setCompressHtmlTagLib("true")
		
	}
	
	def "service - Exception while compressing the data | Exception" () {
		
		given :
		
		def header = "ipad"
		
		BBBCompressHTMLTagLibDroplet compressHtmlTagLibDropletSpy = Spy()
		
		sessionBeanMock.getCompressHtmlTagLib() >> htmlTagLibUserAgents
		
		compressHtmlTagLibDropletSpy.setCatalogTools(catalogToolsMock)
		
		requestMock.getHeader("User-Agent") >>header
		1*catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS) >> {throw new Exception("")}
		1 * sessionBeanMock.setCompressHtmlTagLib("true")
		
		expect :
		
		compressHtmlTagLibDropletSpy.service(requestMock, responseMock)
		
	}
	
	/* 
	 * Alternative branches : 
	 * 
	 * #53 - if(compressHTMLTagLibUserAgent != null && !compressHTMLTagLibUserAgent.isEmpty()) - compressHTMLTagLibUserAgent - empty 
	 */
	def "service - Compressed HTML Taglib is empty" () {
		
		given :
		
		def header = "ipad"
		
		BBBCompressHTMLTagLibDroplet compressHtmlTagLibDropletSpy = Spy()
		
		sessionBeanMock.getCompressHtmlTagLib() >> ""
		
		compressHtmlTagLibDropletSpy.setCatalogTools(catalogToolsMock)
		
		//requestMock.getHeader("User-Agent") >>header
		1 * catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS) >> []
		
		expect :
		
		compressHtmlTagLibDropletSpy.service(requestMock, responseMock)
		
	}
	
	
	/*
	 * Alternative branches covered : 
	 * 
	 * #56 - if(pRequest !=null && BBBUtility.isStringPatternValid(pUserAgent, pRequest.getHeader(USER_AGENT)))-  invalid pattern
	 * pRequest !=null  - can't be covered as it leads to Null pointer exception
	 * 
	 */
	
	//def numericOnlyPattern = /^[0-9]*$/
	
	def "service - HTML agent doesn't match with the pattern" () {
		
		given :
		
		def header = "ipad"
		def emptyPattern = ""
		
		BBBCompressHTMLTagLibDroplet compressHtmlTagLibDropletSpy = Spy()
		
		sessionBeanMock.getCompressHtmlTagLib() >> htmlTagLibUserAgents
		
		compressHtmlTagLibDropletSpy.setCatalogTools(catalogToolsMock)
		
		requestMock.getHeader("User-Agent") >>header
		1 * catalogToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS) >> [htmlTagLibUserAgents,emptyPattern,null]
		
		expect :
		
		compressHtmlTagLibDropletSpy.service(requestMock, responseMock)
		
	}

	
	/*=============================
	 * service - test cases - ENDS
	 *=============================
	 */
	
	/*
	 * Data populating methods
	 */
	
	
	
}
