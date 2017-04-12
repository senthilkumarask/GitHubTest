package com.bbb.selfservice.droplet

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec

class GetCollegeProductDropletSpecification extends BBBExtendedSpec {
	def GetCollegeProductDroplet gcpDroplet
	def BBBCatalogToolsImpl cTools = Mock()
	ProductVO pVO = new ProductVO()
	def setup(){
		gcpDroplet = new GetCollegeProductDroplet(catalogTools : cTools)
	}
	
	def "service . tc TO GET college product list"(){
		given:
			requestMock.getParameter("collegeId") >> "clId"
			requestMock.getParameter("siteId") >> "usBed"
			
			1*cTools.getCollegeProduct("clId", "usBed") >> [pVO]
		
		when:
			gcpDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("productList", _)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

	def "service . tc for BBBBusinessException"(){
		given:
			requestMock.getParameter("collegeId") >> "clId"
			requestMock.getParameter("siteId") >> "usBed"
			
			1*cTools.getCollegeProduct("clId", "usBed") >> {throw new BBBBusinessException("exception")}
		
		when:
			gcpDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("productList", _)
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service . tc for BBBSystemException"(){
		given:
			requestMock.getParameter("collegeId") >> "clId"
			requestMock.getParameter("siteId") >> "usBed"
			
			1*cTools.getCollegeProduct("clId", "usBed") >> {throw new BBBSystemException("exception")}
		
		when:
			gcpDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("productList", _)
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

}
