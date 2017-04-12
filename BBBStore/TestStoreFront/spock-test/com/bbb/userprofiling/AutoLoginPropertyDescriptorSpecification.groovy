package com.bbb.userprofiling

import atg.repository.RepositoryItemImpl;
import spock.lang.specification.BBBExtendedSpec

class AutoLoginPropertyDescriptorSpecification extends BBBExtendedSpec {
	
	def AutoLoginPropertyDescriptor testObj
	def RepositoryItemImpl repositoryItemMock = Mock()
	def Object valueMock = Mock()
	
	def setup() {
		testObj = new AutoLoginPropertyDescriptor()
	}
	
	def "autologin property for tbs contextpath"() {
		
		given:
		
		requestMock.getContextPath() >> "/tbs"
		testObj.setLoggingDebug(true)
		when:
		def results = testObj.getPropertyValue(repositoryItemMock, valueMock)
		then:
		results == false
	}
	
	
	
	def "autologin property for store contextpath"() {
		
		given:
		
		requestMock.getContextPath() >> "/store"
		
		when:
		def results = testObj.getPropertyValue(repositoryItemMock, valueMock)
		then:
		results == valueMock
	}

}
