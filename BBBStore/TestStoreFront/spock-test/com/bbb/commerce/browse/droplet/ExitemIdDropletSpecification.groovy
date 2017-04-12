package com.bbb.commerce.browse.droplet

import java.util.List;

import com.bbb.commerce.catalog.vo.ProductVO;

import spock.lang.specification.BBBExtendedSpec
/**
 * 
 * @author kmagud
 *
 */
class ExitemIdDropletSpecification extends BBBExtendedSpec {
	
	def ExitemIdDroplet testObj
	def ProductVO productVOMock = Mock()
	def ProductVO productVOMock1 = Mock()
	
	def setup(){
		testObj = new ExitemIdDroplet()
	}
	
	def"service method.This TC is the happy flow of service method"(){
		given:
			1 * requestMock.getLocalParameter("lastviewedProductsList") >> [productVOMock,productVOMock1]
			1 * requestMock.getLocalParameter("certonaExcludedItems") >> "certona;certona1;certona"
			2 *	productVOMock.getProductId() >> "prod12345"
			1 * productVOMock1.getProductId() >> "certona"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("productList", "certona;certona1;prod12345")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	
	def"service method.This TC is when lastviewedProductsList is empty and certonaExcludedItems is not defined"(){
		given:
			1 * requestMock.getLocalParameter("lastviewedProductsList") >> []
			1 * requestMock.getLocalParameter("certonaExcludedItems") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("productList", "")
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	
}
