package com.bbb.commerce.browse.droplet

import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class ConfigURLDropletSpecification extends BBBExtendedSpec{
	
	def ConfigURLDroplet testObj
	def BBBCatalogTools catalogToolsMock = Mock()
	
	def setup(){
		testObj = new ConfigURLDroplet(catalogTools : catalogToolsMock,imageURLKey : "image_host" , jsURLKey : "js_host" , cssURLKey : "css_host" , scene7URLKey : "scene7_url")
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setImageURLKey("image_host")
			testObj.setJsURLKey("js_host")
			testObj.setCssURLKey("css_host")
			testObj.setScene7URLKey("scene7_url")
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> ["image_host" : "/image/url", "js_host" : "/js/url", "css_host" : "/css/url", "scene7_url" : "/scene7/url"] 
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("imagePath", "/image/url")
			1 * requestMock.setParameter ("cssPath", "/css/url")
			1 * requestMock.setParameter ("jsPath", "/js/url")
			1 * requestMock.setParameter ("scene7Path", "/scene7/url")
			1 * requestMock.serviceParameter ("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when properties in urlMap are empty"(){
		given:
			Map<String,String> urlMapMock = ["image_host" : "", "js_host" : "", "css_host" : "", "scene7_url" : ""]
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> urlMapMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("imagePath", "")
			1 * requestMock.setParameter ("cssPath", "")
			1 * requestMock.setParameter ("jsPath", "")
			1 * requestMock.setParameter ("scene7Path", "")
			1 * requestMock.serviceParameter ("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when properties in urlMap are not defined"(){
		given:
			Map<String,String> urlMapMock = ["image_host" : "null", "js_host" : "null", "css_host" : "null", "scene7_url" : "null"]
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> urlMapMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("imagePath", "")
			1 * requestMock.setParameter ("cssPath", "")
			1 * requestMock.setParameter ("jsPath", "")
			1 * requestMock.setParameter ("scene7Path", "")
			1 * requestMock.serviceParameter ("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when urlMap is not defined"(){
		given:
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			0 * requestMock.setParameter("imagePath", "")
			0 * requestMock.setParameter ("cssPath", "")
			0 * requestMock.setParameter ("jsPath", "")
			0 * requestMock.setParameter ("scene7Path", "")
			0 * requestMock.serviceParameter ("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when urlMap is empty"(){
		given:
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> [:]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			0 * requestMock.setParameter("imagePath", "")
			0 * requestMock.setParameter ("cssPath", "")
			0 * requestMock.setParameter ("jsPath", "")
			0 * requestMock.setParameter ("scene7Path", "")
			0 * requestMock.serviceParameter ("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown in getConfigValueByconfigType method"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('error', 'err_config_url_error')
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			1 * testObj.logError('browse_1026: System Exception in ConfigURLfrom service of ConfigURLDroplet ', _)
			1 * testObj.logDebug('request Parameters value[configType =configType]')
	}
	
	def"service method. This TC is when BBBBusinessException thrown in getConfigValueByconfigType method"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			1 * requestMock.getParameter("configType") >> "configType"
			1 * catalogToolsMock.getConfigValueByconfigType("configType") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('error', 'err_config_url_error')
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			1 * testObj.logError('browse_1027: Business Exception in ConfigURLfrom service of ConfigURLDroplet ', _)
			1 * testObj.logDebug('request Parameters value[configType =configType]')
	}

}
