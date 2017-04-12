package com.bbb.commerce.checkout.droplet

import com.bbb.constants.BBBCoreConstants;

import spock.lang.specification.BBBExtendedSpec

class BBBCreditCardDisplayDropletSpecification extends BBBExtendedSpec {

	def BBBCreditCardDisplayDroplet CCDDroplet;
	def setup(){
		CCDDroplet = new BBBCreditCardDisplayDroplet()
		
	}
	
	def" service . TC to check parameter is added to request object when credit card number lenght is 16"(){
	given:
	requestMock.getObjectParameter("creditCardNo") >> "1234567891234567"
	
	when:
	CCDDroplet.service(requestMock, responseMock)
	then:	
	1*requestMock.setParameter(_, "**** **** **** " + "4567")
	1*requestMock.serviceParameter("output",*_)
	}
	
	def" service . TC to check parameter is added to request object when credit card number lenght is 15"(){
		given:
		requestMock.getObjectParameter("creditCardNo") >> "123456789123456"
		
		when:
		CCDDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(_,  "***** ****** " + "3456")
		1*requestMock.serviceParameter("output",*_)
		
	}
	
	def" service . TC when credit card number  is null"(){
		given:
		requestMock.getObjectParameter("creditCardNo") >> null
		
		when:
		CCDDroplet.service(requestMock, responseMock)
		then:
	   0*requestMock.setParameter(_, "**** **** **** " + "4567")
	   1*requestMock.serviceParameter("output",*_)
	   
	}
	
	def" service . TC when credit card number length is not in (15 and 16)"(){
		given:
	   requestMock.getObjectParameter("creditCardNo") >> "123456789123456756"
		
		when:
		CCDDroplet.service(requestMock, responseMock)
		then:
	   0*requestMock.setParameter(_, "**** **** **** " + _)
	   0*requestMock.setParameter(_,  "***** ****** " + _)
	   1*requestMock.serviceParameter("output",*_)
	   
	}
	
}
