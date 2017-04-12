package com.bbb.commerce.checklist.droplet

import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.constants.BBBCoreConstants;
import com.bbb.search.endeca.EndecaSearchUtil
import spock.lang.specification.BBBExtendedSpec

class CheckListCatProdCountDropletSpecification extends BBBExtendedSpec {
	def CheckListCatProdCountDroplet testObj;
	def EndecaSearchUtil endecaSearchUtilMock = Mock()
	
	def setup(){
		testObj = new CheckListCatProdCountDroplet(searchUtil:endecaSearchUtilMock)
	}
	def "service -pass all values and get counts"(){
		given:
		long prodCount = 300131
		requestMock.getParameter("facetIdCollection")>>"31"
		requestMock.getParameter("seoUrlDimensionId") >> "3001"
		1*endecaSearchUtilMock.getProductCount(*_) >> prodCount
		
		when:
		testObj.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(_,300131)
	}

	def "service -we pass all values and get exception"(){
		given:
		testObj = Spy()
		testObj.setSearchUtil(endecaSearchUtilMock)
		long prodCount = 300131
		requestMock.getParameter("facetIdCollection")>>"31"
		requestMock.getParameter("seoUrlDimensionId") >> "3001"
		endecaSearchUtilMock.getProductCount(*_) >> {throw new Exception("mock Exception")}
		
		when:
		testObj.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(_,0)
		1*testObj.logError(_)
	}
	def "service -we pass all values and get BBBBusinessException"(){
		given:
		testObj = Spy()
		testObj.setSearchUtil(endecaSearchUtilMock)
		long prodCount = 300131
		requestMock.getParameter("facetIdCollection")>>"31"
		requestMock.getParameter("seoUrlDimensionId") >> "3001"
		endecaSearchUtilMock.getProductCount(*_) >> {throw new BBBBusinessException("mock BBBBusinessException")}
		
		when:
		testObj.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(_,0)
		1*testObj.logError(_)
	}
	def "service -we pass all values and get BBBSystemException"(){
		given:
		testObj = Spy()
		testObj.setSearchUtil(endecaSearchUtilMock)
		long prodCount = 300131
		requestMock.getParameter("facetIdCollection")>>"31"
		requestMock.getParameter("seoUrlDimensionId") >> "3001"
		endecaSearchUtilMock.getProductCount(*_) >> {throw new BBBSystemException("mock BBBSystemException")}
		
		when:
		testObj.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(_,0)
		1*testObj.logError(_)
	}
	def "service -we pass empty value for facetIdCollection and get counts"(){
		given:
		long prodCount = 3001
		requestMock.getParameter("facetIdCollection")>>""
		requestMock.getParameter("seoUrlDimensionId") >> "3001"
		0*endecaSearchUtilMock.getProductCount(*_) >> prodCount
		
		when:
		testObj.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(_,_)
		
	}
	def "service -we pass empty value for seoUrlDimensionId and get counts"(){
		given:
		long prodCount = 300131
		requestMock.getParameter("facetIdCollection")>>"31"
		requestMock.getParameter("seoUrlDimensionId") >> ""
		0*endecaSearchUtilMock.getProductCount(*_) >> prodCount
		
		when:
		testObj.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter(_,_)
		
	}
}
