package com.bbb.commerce.checkout.droplet

import atg.nucleus.naming.ParameterName;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import spock.lang.specification.BBBExtendedSpec

class IsProductSKUShippingDropletSpecification extends BBBExtendedSpec {

	def IsProductSKUShippingDroplet ipssDropletObj
	def IsProductSKUShippingDropletHelper skuShippingHelperMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()

	def setup(){
		ipssDropletObj = new IsProductSKUShippingDroplet(helper:skuShippingHelperMock,catalogTools:catalogToolsMock)
	}
	def "service. To check display Shipping restriction is true and added the vdc message also "(){
		given:
		List list = new ArrayList()
		list.add("25001") 
		requestMock.getParameter(ParameterName.getParameterName("siteId")) >> "USBadBath"
		requestMock.getParameter(ParameterName.getParameterName("skuId")) >> "sk12356"
		requestMock.getParameter(ParameterName.getParameterName("prodId")) >> "p145"
		skuShippingHelperMock.getAttribute("USBadBath", "sk12356",  "p145") >> ["rZipCode":"25001"]		
		//inside try block
		catalogToolsMock.getActualOffsetDate("USBadBath", "sk12356") >> "vdc message"
		
		when:
		 ipssDropletObj.service(requestMock,responseMock)
		
		then:
				
		1 * requestMock.setParameter('restrictedAttributes', _)
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("true"), requestMock, responseMock)
		1*requestMock.serviceLocalParameter("vdcMsg",requestMock,responseMock)
       
		
	}
	
	def "service. To check display Shipping restriction is false  "(){
		given:
		requestMock.getParameter(ParameterName.getParameterName("siteId")) >> "USBadBath"
		requestMock.getParameter(ParameterName.getParameterName("skuId")) >> "sk12356"
		requestMock.getParameter(ParameterName.getParameterName("prodId")) >> "p145"
		skuShippingHelperMock.getAttribute("USBadBath", "sk12356",  "p145") >> null
		//inside try block
		catalogToolsMock.getActualOffsetDate("USBadBath", "sk12356") >> null
		
		when:
		ipssDropletObj.service(requestMock,responseMock)
		
		then:
		1*requestMock.serviceLocalParameter(ParameterName.getParameterName("false"), requestMock, responseMock)
		1*requestMock.setParameter("restrictedAttributesRest",null)
	}
	
	def "service. TC for BBBSystemException while getting VDC Message  "(){
		given:
		Map map = new HashMap()
		requestMock.getParameter(ParameterName.getParameterName("siteId")) >> "USBadBath"
		requestMock.getParameter(ParameterName.getParameterName("skuId")) >> "sk12356"
		requestMock.getParameter(ParameterName.getParameterName("prodId")) >> "p145"
		skuShippingHelperMock.getAttribute("USBadBath", "sk12356",  "p145") >> map
		//inside try block
		catalogToolsMock.getActualOffsetDate("USBadBath", "sk12356") >> {throw new BBBSystemException("system exception")}
		
		when:
		ipssDropletObj.service(requestMock,responseMock)
		
		then:
		0*requestMock.serviceLocalParameter("vdcMsg",requestMock,responseMock)
	}
	
	def "service. TC for BBBBusinessException while getting VDC Message  "(){
		given:
		Map map = new HashMap()
		requestMock.getParameter(ParameterName.getParameterName("siteId")) >> "USBadBath"
		requestMock.getParameter(ParameterName.getParameterName("skuId")) >> "sk12356"
		requestMock.getParameter(ParameterName.getParameterName("prodId")) >> "p145"
		skuShippingHelperMock.getAttribute("USBadBath", "sk12356",  "p145") >> map
		//inside try block
		catalogToolsMock.getActualOffsetDate("USBadBath", "sk12356") >> {throw new BBBBusinessException("system exception")}
		
		when:
		ipssDropletObj.service(requestMock,responseMock)
		
		then:
		0*requestMock.serviceLocalParameter("vdcMsg",requestMock,responseMock)
	}
	
}
