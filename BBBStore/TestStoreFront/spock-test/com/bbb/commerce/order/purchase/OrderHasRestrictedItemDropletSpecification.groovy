package com.bbb.commerce.order.purchase

import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.order.droplet.BBBOrderInfoDroplet
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec

class OrderHasRestrictedItemDropletSpecification extends BBBExtendedSpec {
	def OrderHasRestrictedItemDroplet ohriDroplet
	def BBBCheckoutManager cManager =Mock()
	def BBBOrder order = Mock()
	def setup(){
		ohriDroplet = new OrderHasRestrictedItemDroplet(manager : cManager)
	}
	def"service. Tc to check  order contains ltl item or not "(){
		given:
			1*requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsIntlRestrictedItem(order) >> "true"
			
		when:
			ohriDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasIntlResterictedItem", true)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("orderHasIntlResterictedItem", false)
		
	}
	
	def"service. Tc for BBBSystemException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsIntlRestrictedItem(order) >> {throw new BBBSystemException("exception")}
			
		when:
			ohriDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasIntlResterictedItem", false)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
		
	}
	
	def"service. Tc for BBBBusinessException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsIntlRestrictedItem(order) >> {throw new BBBBusinessException("exception")}
			
		when:
			ohriDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasIntlResterictedItem", false)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
		
	}
	
	def"service. Tc when intlRestrictedItems is null "(){
		given:
			1*requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsIntlRestrictedItem(order) >> null
			
		when:
			ohriDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*requestMock.setParameter("orderHasIntlResterictedItem", false)
		
	}
	
	def"service. Tc when intlRestrictedItems is blanck "(){
		given:
			1*requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsIntlRestrictedItem(order) >> ""
			
		when:
			ohriDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*requestMock.setParameter("orderHasIntlResterictedItem", false)
		
	}
}
