package com.bbb.commerce.catalog

import static org.junit.Assert.*;

import org.junit.Test;
import atg.repository.RepositoryItemImpl
import atg.servlet.ServletUtil
import spock.lang.specification.BBBExtendedSpec
import atg.multisite.SiteContextManager;


class ActionURLOverrideSpecification extends BBBExtendedSpec {

	def ActionURLOverride testObj 
	def RepositoryItemImpl itemMock = Mock()
	
	def setup(){
		testObj = new ActionURLOverride()
		ServletUtil.setCurrentRequest(requestMock)
	}
	
	def "getPropertyValue. When siteId having TBS prefix"(){
		given:
		testObj = Spy()
		Object value = "aaa/store"
		requestMock.getAttribute("siteId") >> "TBSUSBedBath"
		requestMock.getContextPath() >> "/contexPath"
		testObj.callSupergetPropertyValue(itemMock, value) >> "bbbb/store"
		 
		when:
		Object returnValue = testObj.getPropertyValue(itemMock, value)
		then:
		String.valueOf(returnValue) == "bbbb/contexPath"
	}
	
	def "getPropertyValue. When siteId is null and value gets from getPropertyValu() method is also null"(){
		given:
		testObj = Spy()
		ServletUtil.setCurrentRequest(null)
		
		Object value = "aaa/store"
		requestMock.getContextPath() >> "/contexPath"
		testObj.callSupergetPropertyValue(itemMock, value) >> "some"
		 
		when:
		Object returnValue = testObj.getPropertyValue(itemMock, value)
		then:
		returnValue == "some"
	}
	
	

	
	def "getPropertyValue. When siteId is not null but value gets from super getPropertyvalue method is  null and siteId having TBS prefix"(){
		given:
		testObj = Spy()		
		Object value = "aaa/store"
		requestMock.getAttribute("siteId") >> "TBSUSBedBath"
		
		requestMock.getContextPath() >> "/contexPath"
		testObj.callSupergetPropertyValue(itemMock, value) >> null
		 
		when:
		Object returnValue = testObj.getPropertyValue(itemMock, value)
		then:
		returnValue == null
	}
	
	def "getPropertyValue. When siteId is not null , value gets from super getPropertyvalue method is not null and siteId dont have TBS prefix "(){
		given:
		testObj = Spy()
		
		Object value = "aaa/store"
		requestMock.getAttribute("siteId") >> "USBedBath"
		
		requestMock.getContextPath() >> "/contexPath"
		testObj.callSupergetPropertyValue(itemMock, value) >> "bbbb/store"
		 
		when:
		Object returnValue = testObj.getPropertyValue(itemMock, value)
		then:
		String.valueOf(returnValue) == "bbbb/store"
	}
}
