package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class FacebookImageDropletSpecification extends BBBExtendedSpec {
	
	def FacebookImageDroplet testObj
	def ProductManager productManagerMock = Mock()
	
	def setup(){
		testObj =new FacebookImageDroplet()
		testObj.setProductManager(productManagerMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			def ImageVO imageVOMock = new ImageVO(largeImage:"/url/largeImage")
			1 * requestMock.getParameter("id") >> "prod1234"
			1 * productManagerMock.getProductImages("prod1234") >> imageVOMock
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("productImageLarge", imageVOMock.getLargeImage())
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			imageVOMock.getLargeImage() == "/url/largeImage"
	}
	
	def"service method. This TC is when ProductID is not defined"(){
		given:
		testObj	= Spy()
			1 * requestMock.getParameter("id") >> null
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logDebug('pProductId is NULL')
			1 * testObj.logDebug("productImages is NULL")
			0 * testObj.logDebug('pProductId[prod1234]')
			0 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when getLargeImage is not defined in ImageVO"(){
		given:
			def ImageVO imageVOMock = new ImageVO(largeImage: null)
			1 * requestMock.getParameter("id") >> "prod1234"
			1 * productManagerMock.getProductImages("prod1234") >> imageVOMock
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			0 * requestMock.setParameter("productImageLarge", imageVOMock.getLargeImage())
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			imageVOMock.getLargeImage() == null
	}
	
	def"service method. This TC is when BBBBusinessException is thrown in getProductImages"(){
		given:
		testObj = Spy()
		testObj.setProductManager(productManagerMock)
			1 * requestMock.getParameter("id") >> "prod1234"
			1 * productManagerMock.getProductImages("prod1234") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('browse_1032: Business Exception from service of FacebookImageDroplet for productId=prod1234', _)
	}
	
	def"service method. This TC is when BBBSystemException is thrown in getProductImages"(){
		given:
		testObj = Spy()
		testObj.setProductManager(productManagerMock)
			1 * requestMock.getParameter("id") >> "prod1234"
			1 * productManagerMock.getProductImages("prod1234") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('browse_1033: System Exception from service of FacebookImageDroplet for productId=prod1234', _)
			
	}

}
