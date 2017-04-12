package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec
/**
 * 
 * @author kmagud
 *
 */
class EverLivingPDPDropletSpecification extends BBBExtendedSpec{

	def EverLivingPDPDroplet testObj
	def ProductManager productManagerMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new EverLivingPDPDroplet(productManager : productManagerMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			1 * productManagerMock.getProductStatus("BedBathUS", "prod12345") >> TRUE
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("everLivingProduct", TRUE)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when productId is not defined"(){
		given:
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> null
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
		when:
			testObj.service(requestMock, responseMock)
		then:
			0 * productManagerMock.getProductStatus("BedBathUS", null)
			0 * requestMock.setParameter("everLivingProduct", TRUE)
			0 * requestMock.serviceParameter("output", requestMock, responseMock)
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when productId is empty"(){
		given:
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> ""
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
		when:
			testObj.service(requestMock, responseMock)
		then:
			0 * requestMock.setParameter("everLivingProduct", TRUE)
			0 * requestMock.serviceParameter("output", requestMock, responseMock)
			1 * requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown in service Method"(){
		given:
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			1 * productManagerMock.getProductStatus("BedBathUS", "prod12345") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("everLivingProduct", FALSE)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBBusinessException thrown in service method"(){
		given:
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			1 * requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			1 * productManagerMock.getProductStatus("BedBathUS", "prod12345") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("everLivingProduct", FALSE)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
}
