package com.bbb.framework.cache;

import javax.jms.Message
import javax.jms.MessageFormatException
import javax.jms.ObjectMessage

import spock.lang.specification.BBBExtendedSpec

import com.bbb.redirectURLs.CategoryRedirectURLLoader

public class BBBCategoryRedirectURLCacheContainerSpecification extends BBBExtendedSpec {
	
	Message messageMock = Mock()
	ObjectMessage objectMessageMock = Mock()
	BBBCategoryRedirectURLCacheContainer testBBBCategoryRedirectURLCacheContainer;
	CategoryRedirectURLCacheInvalidationMessage CategoryRedirectURLCacheInvalidationMessageMock = Mock()
	CategoryRedirectURLLoader categoryRedirectURLLoaderMock = Mock()
			
	def setup() {
		
		testBBBCategoryRedirectURLCacheContainer = Spy()
		testBBBCategoryRedirectURLCacheContainer.setCategoryRedirectURL(categoryRedirectURLLoaderMock)
		
	}
	
	def "receiveMessage : Happy Flow"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> true
		  testBBBCategoryRedirectURLCacheContainer.getCacheType() >> "redirectCache"
		  CategoryRedirectURLCacheInvalidationMessageMock.getCacheType() >> "redirectCache"
		  objectMessageMock.getObject() >> CategoryRedirectURLCacheInvalidationMessageMock
		  categoryRedirectURLLoaderMock.getCategoryRedirectURLMap() >> ["54546" : "redirectURL"]
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, objectMessageMock)
		   
		then:
		  2 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() == [:]
	}
	
	def "receiveMessage : Message not an instance of ObjectMessage"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> true
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, messageMock)
		   
		then:
		  0 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
		  MessageFormatException exception = thrown()
	}
	
	def "receiveMessage : Message not an instance of CategoryRedirectURLCacheInvalidationMessage"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> true
		  testBBBCategoryRedirectURLCacheContainer.getCacheType() >> "redirectCache"
		  CategoryRedirectURLCacheInvalidationMessageMock.getCacheType() >> "redirectCache"
		  objectMessageMock.getObject() >> objectMessageMock
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() >> ["54546" : "redirectURL"]
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, objectMessageMock)
		   
		then:
		  0 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() == ["54546" : "redirectURL"]
	}
	
	def "receiveMessage : CacheType not equal to this.cacheType"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> true
		  testBBBCategoryRedirectURLCacheContainer.getCacheType() >> "redirectCache"
		  CategoryRedirectURLCacheInvalidationMessageMock.getCacheType() >> "otherCache"
		  objectMessageMock.getObject() >> CategoryRedirectURLCacheInvalidationMessageMock
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() >> ["54546" : "redirectURL"]
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, objectMessageMock)
		   
		then:
		  0 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() == ["54546" : "redirectURL"]
	}
	
	def "receiveMessage : CategoryRedirectURLMap as null"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> true
		  testBBBCategoryRedirectURLCacheContainer.getCacheType() >> "redirectCache"
		  CategoryRedirectURLCacheInvalidationMessageMock.getCacheType() >> "redirectCache"
		  objectMessageMock.getObject() >> CategoryRedirectURLCacheInvalidationMessageMock
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() >> null
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, objectMessageMock)
		   
		then:		
		  1 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
	}
	
	def "receiveMessage : isEnabled as false"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> false
		  testBBBCategoryRedirectURLCacheContainer.getCacheType() >> "redirectCache"
		  CategoryRedirectURLCacheInvalidationMessageMock.getCacheType() >> "redirectCache"
		  objectMessageMock.getObject() >> CategoryRedirectURLCacheInvalidationMessageMock
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() >> ["54546" : "redirectURL"]
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, messageMock)
		   
		then:
		  0 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL().getCategoryRedirectURLMap() == ["54546" : "redirectURL"]
	}
	
	def "receiveMessage : exception scenarios"() {
		
		given:
		  String portName = "aa"
		  testBBBCategoryRedirectURLCacheContainer.isEnabled() >> true
		  testBBBCategoryRedirectURLCacheContainer.getCacheType() >> "redirectCache"
		  CategoryRedirectURLCacheInvalidationMessageMock.getCacheType() >> "redirectCache"
		  objectMessageMock.getObject() >> CategoryRedirectURLCacheInvalidationMessageMock
		  testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL() >> null
		when:
		
		  testBBBCategoryRedirectURLCacheContainer.receiveMessage(portName, objectMessageMock)
		   
		then:
		  1 * testBBBCategoryRedirectURLCacheContainer.getCategoryRedirectURL()
		  4 * testBBBCategoryRedirectURLCacheContainer.logDebug(_)
	}

}
