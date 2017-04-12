package com.bbb.commerce.order.purchase

import atg.commerce.order.OrderImpl
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec

class OrderHasLTLItemDropletSpecification extends BBBExtendedSpec {
	def OrderHasLTLItemDroplet ohltiDroplet
	def BBBCheckoutManager cManager =Mock()
	def OrderImpl order = Mock()

	def setup(){
		ohltiDroplet = new OrderHasLTLItemDroplet(manager : cManager)
	}
	def"service"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsLTLItem(order) >> true
			
		when:
			ohltiDroplet.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("orderHasLTLItem", true)
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		0*requestMock.setParameter("orderHasLTLItem", false)

	}
	
	def"service. Tc for BBBSystemException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsLTLItem(order) >> {throw new BBBSystemException("exception")}
			
		when:
			ohltiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasLTLItem", false)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
		
	}
	
	def"service. Tc for BBBBusinessException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsLTLItem(order) >> {throw new BBBBusinessException("exception")}
			
		when:
			ohltiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasLTLItem", false)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		
	}
}
