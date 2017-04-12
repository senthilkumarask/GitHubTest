package com.bbb.commerce.browse.droplet

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
import atg.servlet.ServletUtil
/**
 * 
 * @author kmagud
 *
 */
public class BVContentDropletSpecification extends BBBExtendedSpec {

	BVContentDroplet testObj
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new BVContentDroplet(subjectType:"p",staging:"false",sdkEnabled:"true",executionTimeOut:0,botExecutionTimeOut:5000,cloudKey:"bedbathbeyond-61d607e6ac37cdfc1442040c7fa59e53",
			bvRootFolder:"2009",includeDisplayIntCode:"false",loadSEOFilesLocally:"false",sslEnabled:"false")
	}
	
	def"service method. This TC is the happy flow of service method"(){
		given:
			testObj.setContentType("qa")
			1 * requestMock.getHeader("User-Agent") >> "desktop"
			1 * requestMock.getParameter("productId") >> "prod12345"
			1 * requestMock.getParameter("pageURL") >> "/category/Health-Living/14408"
			
			//getHost Private Method Coverage
			StringBuffer requestUrl = new StringBuffer()
			requestUrl.append("bedbathandbeyond.com")
			1 * requestMock.getRequestURL() >> requestUrl 
			1 * requestMock.getContextPath() >> "/store"
			
			//getQueryString Private Method Coverage
			1 * requestMock.getQueryString() >> "skuId&SKU12345"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bvContent", _)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.getParameter("content")
		
	}
	
	def"service method. This TC is when contentTypeInput value is not empty"(){
		given:
			testObj.setContentType("qa")
			1 * requestMock.getHeader("User-Agent") >> "desktop"
			1 * requestMock.getParameter("productId") >> "prod12345"
			1 * requestMock.getParameter("pageURL") >> "/category/Health-Living/14408"
			1 * requestMock.getParameter("content") >> "JSON"
			
			//getHost Private Method Coverage
			StringBuffer requestUrl = new StringBuffer()
			requestUrl.append("bedbathandbeyond.com")
			requestMock.getRequestURL() >> requestUrl
			requestMock.getContextPath() >> "/store"
			
			//getQueryString Private Method Coverage
			1 * requestMock.getQueryString() >> "skuId&SKU12345"
		
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bvContent", _)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
		
	}
	
	
}
