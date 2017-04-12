package com.bbb.commerce.browse.droplet

import javax.servlet.http.HttpServletResponse;

import com.bbb.constants.BBBCoreConstants
import com.bbb.utils.BBBUtility;
import com.sun.org.apache.xalan.internal.lib.sql.QueryParameter;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse
import atg.servlet.ServletUtil;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author Velmurugan Moorthy
 *
 */
class BBBClearFilterRedirectDropletSpecification extends BBBExtendedSpec {

	private BBBClearFilterRedirectDroplet clearFilterRedirectDroplet 
	
	def url
	/*def DynamoHttpServletRequest requestSpy
	def DynamoHttpServletResponse responseSpy*/
	
	def setup() {
		
		clearFilterRedirectDroplet = new BBBClearFilterRedirectDroplet()
		/*requestSpy = Spy()
		responseSpy = Spy()
		
		ServletUtil.setCurrentRequest(requestSpy)
		ServletUtil.setCurrentResponse(responseSpy)*/
		
		url = "https://www.bedbath.com/pdp/product01"
		
	}
	
	/*
	 *  service - method test cases STARTS
	 *  
	 *  Method signature : 
	 *  
	 *  public void service 
	 *  (
	 *  final DynamoHttpServletRequest requestSpy, 
	 *  final DynamoHttpServletResponse pResponse
	 *  ) 
	 *  
	 */
	
	def "service - Filter redirect done succesfully" () {
		
		given : 
		
		def queryparams = "skuId=sku01&color=red"
		def contextPath = "/store"
		def encodedRedirectUrl = "/store?skuId=sku01&color=red"
		
		requestMock.getParameter("url") >> url
		requestMock.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING) >> queryparams
		requestMock.getContextPath() >> contextPath
		
		responseMock.encodeRedirectURL(url) >> encodedRedirectUrl
		
		clearFilterRedirectDroplet.setLoggingDebug(true)
		
		1 * responseMock.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY)
		1*responseMock.sendRedirect(_)
		
		expect :
		
		clearFilterRedirectDroplet.service(requestMock, responseMock)
		
	}
	
	/*
	 * Alternate branches covered :
	 * 
	 * #35 - if (!BBBUtility.isEmpty(queryparams)) - false
	 *
	 * 
	 * 
	 */
	
	def "service - Filter failed | query params are empty | LoggingDebug is disabled (false)" () {
		
		given :
		
		//def queryparams = ""
		def contextPath = "/store"
		def encodedRedirectUrl = "/store?skuId=sku01&color=red"
		
		requestMock.getParameter("url") >> url
		requestMock.getAttribute(BBBCoreConstants.FORWARD_QUERY_STRING) >> ""
		requestMock.getContextPath() >> contextPath
		
		responseMock.encodeRedirectURL(url) >> encodedRedirectUrl
		
		clearFilterRedirectDroplet.setLoggingDebug(false)
		
		1 * responseMock.sendRedirect(_)
		1 * responseMock.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY)
		
		expect :
		 
		clearFilterRedirectDroplet.service(requestMock, responseMock)
		
	}
	
	
	/*
	 *  service - method test cases ENDS
	 *
	 */
	
	
}
