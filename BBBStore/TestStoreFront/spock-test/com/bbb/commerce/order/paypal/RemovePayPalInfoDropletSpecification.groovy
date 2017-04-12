package com.bbb.commerce.order.paypal

import spock.lang.specification.BBBExtendedSpec
import atg.userprofiling.Profile

import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.exception.BBBSystemException

class RemovePayPalInfoDropletSpecification extends BBBExtendedSpec {

	
	
	def BBBOrderImpl orderMock = Mock()
	def BBBPayPalServiceManager payPalServiceManagerMock = Mock()
	def Profile profileMock = Mock()
	def RemovePayPalInfoDroplet paypalInfoObj
	
	def setup(){
		paypalInfoObj = new RemovePayPalInfoDroplet(paypalServiceManager:payPalServiceManagerMock,order:orderMock,profile:profileMock)
		paypalInfoObj.setLoggingDebug(true)
	}
	def "Remonving paypal payment group paypal billing address and token from Order "(){
		given:
		when:
		paypalInfoObj.removePayPalInfoDroplet()
		then:
		1*payPalServiceManagerMock.removePayPalPaymentGroup(orderMock, profileMock)
	}
	def "Remonving paypal payment group paypal billing address and token from Order throws BBBSystemException "(){
		given:
		1*payPalServiceManagerMock.removePayPalPaymentGroup(orderMock, profileMock) >> {throw new BBBSystemException("Mock Exception while removing payment Group")  }
		expect:
		paypalInfoObj.removePayPalInfoDroplet() 
	}
}
