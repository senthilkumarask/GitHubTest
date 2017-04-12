package com.bbb.browse

import java.io.IOException;

import javax.servlet.ServletException

import com.bbb.constants.BBBCoreConstants
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is to unit test the AddContextPathDroplet using spock framework
 *
 *
 */

public class AddContextPathDropletSpecification extends BBBExtendedSpec {

	private AddContextPathDroplet addContextPathDroplet
	
	def setup() {
	
		addContextPathDroplet = new AddContextPathDroplet()
			
	}
	
	/*================================================================
	 * Service - Test cases starts						 			 *	 
	 * 															     * 
	 * Method signature : 									   		 * 
	 * 																 * 	
	 * public void service(final DynamoHttpServletRequest pRequest,	 * 	
	 * final DynamoHttpServletResponse pResponse)					 * 
	 *  throws ServletException, IOException 	 					 * 
	 *															 	 *  
	 *================================================================
	 */
	
	def "service - Request from store site - url is not modified if it starts with //- happy flow" () {
		
		given :
		
		
		String inputLink = "//holidayeverydaygiveaway.promo.eprize.com/?affiliate_id=bbby_us?ICID=searchbanner-holidaysweeps2016"
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from store site - url is not modified if it starts with  http:// - happy flow" () {
		
		given :
		
		
		String inputLink = "https://holidayeverydaygiveaway.promo.eprize.com/?affiliate_id=bbby_us?ICID=searchbanner-holidaysweeps2016"
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from store site - url is not modified if it starts with  https:// - happy flow" () {
		
		given :
		
		
		String inputLink = "http://holidayeverydaygiveaway.promo.eprize.com/?affiliate_id=bbby_us?ICID=searchbanner-holidaysweeps2016"
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from store site - ContextPath is successfully modified(added) for store - happy flow" () {
		
		given : 
		
		String pdpURL = "product/product01"
		String inputLink = BBBCoreConstants.STORE + BBBCoreConstants.SLASH + pdpURL
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,BBBCoreConstants.SLASH+inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	} 
	
	def "service - Request from store site - ContextPath is successfully modified(added) for TBS site" () {
		
		given :
		
		String pdpUrl = "product/product01"
		String inputLink = BBBUtility.toLowerCase(BBBCoreConstants.TBS) + BBBCoreConstants.SLASH + pdpUrl
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,(BBBCoreConstants.CONTEXT_STORE + BBBCoreConstants.SLASH + pdpUrl))
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	//BBBCoreConstants.CONTEXT_STORE+ BBBCoreConstants.SLASH
	
	def "service - Request from store site - Input link not having Store context path and contains TBS context path" () {
		
		given :
		
		String pdpURL = "product/product01"
		String inputLink = BBBCoreConstants.CONTEXT_TBS + BBBCoreConstants.SLASH + pdpURL
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,BBBCoreConstants.CONTEXT_STORE + BBBCoreConstants.SLASH + pdpURL)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from store site - Input link having no context path (Link starts with Slash)" () {
		
		given :
		
		String pdpURL = "product/product01"
		String inputLink = BBBCoreConstants.SLASH + pdpURL
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,BBBCoreConstants.CONTEXT_STORE + inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from store site - Input link having no context path (Link starts without Slash)" () {
		
		given :
		
		String pdpURL = "product/product01"
		String inputLink = pdpURL
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK,BBBCoreConstants.CONTEXT_STORE + BBBCoreConstants.SLASH + inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}

	def "service - Request from store site - Input link has store context path" () {
		
		given :
		
		String inputLink = BBBCoreConstants.CONTEXT_STORE+ BBBCoreConstants.SLASH
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from store site - Input link has no context and less than 4 characters" () {
		
		given :
		
		String inputLink = "sku/"
		String contextPath = BBBCoreConstants.STORE
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.CONTEXT_STORE + BBBCoreConstants.SLASH + inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from TBS site" () {
		
		given :
		
		String pdpUrl = "pdp/prod01"
		String inputLink = BBBUtility.toLowerCase(BBBCoreConstants.TBS)+ BBBCoreConstants.SLASH + pdpUrl
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.SLASH + inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}

	def "service - Request from TBS site but the input link has store (word) in the URL" () {
		
		given :
		
		String pdpUrl = "pdp/prod01"
		String inputLink = BBBCoreConstants.STORE + BBBCoreConstants.SLASH + pdpUrl
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.CONTEXT_TBS + BBBCoreConstants.SLASH + pdpUrl)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}

	def "service - Request from TBS site - Input link has store contextPath in URL" () {
		
		given :
		
		String pdpUrl = "pdp/prod01"
		String inputLink = BBBCoreConstants.CONTEXT_STORE + BBBCoreConstants.SLASH + pdpUrl
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.CONTEXT_TBS + BBBCoreConstants.SLASH + pdpUrl)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from TBS site - Input link has store(keyword) in URL" () {
		
		given :
		
		String pdpUrl = "pdp/prod01"
		String inputLink = BBBCoreConstants.CONTEXT_STORE + BBBCoreConstants.SLASH + pdpUrl
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.CONTEXT_TBS + BBBCoreConstants.SLASH + pdpUrl)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from TBS site - Input link has no context" () {
		
		given :
		
		String pdpUrl = "pdp/prod01"
		String inputLink = BBBCoreConstants.SLASH + pdpUrl
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.CONTEXT_TBS + inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from TBS site - Input link has TBS Context path" () {
		
		given :
		
		String inputLink = BBBCoreConstants.CONTEXT_TBS+ BBBCoreConstants.SLASH
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}
	
	def "service - Request from TBS site - Input link has no context and less than 4 characters" () {
		
		given :
		
		String pdpUrl = "pdp/prod01"
		String inputLink = BBBCoreConstants.SLASH
		String contextPath = BBBCoreConstants.TBS
		 
		(1.._) * requestMock.getParameter(BBBCoreConstants.INPUT_LINK) >> inputLink
		1 * requestMock.getContextPath() >> contextPath
		1 * requestMock.setParameter(BBBCoreConstants.OUTPUT_LINK, BBBCoreConstants.CONTEXT_TBS + inputLink)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		expect :
		 
		addContextPathDroplet.service(requestMock, responseMock)
		
	}

		
	/*================================================================
	 * Service - Test cases starts						 		     *	
	 *================================================================
	 */
	
}
