package com.bbb.commerce.checkout.droplet

import spock.lang.specification.BBBExtendedSpec

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.ecommerce.order.BBBOrder;


class DisplayBillingDropletSpecification  extends BBBExtendedSpec  {
	def DisplayBillingDroplet DBDropletObject
	def BBBOrder orderMock = Mock()
	def setup(){
		 DBDropletObject = new DisplayBillingDroplet()
	}
	
	def "service Method. TC to add 'displayBilling' OParam in request when order having commerceItems " (){
		given:
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getCommerceItemCount() >>  2
		when:
		DBDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceLocalParameter("displayBilling", requestMock, responseMock)
	}
	
	def "service Method. TC to add 'displayCart' OParam in request, order having no commerceItems " (){
		given:
		requestMock.getObjectParameter("order") >> orderMock
		orderMock.getCommerceItemCount() >>  0
		
		when:
		DBDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceLocalParameter("displayCart", requestMock, responseMock)
	}
	
	def "service Method. TC to add 'displayCart' OParam in request, order is null " (){
		given:
		requestMock.getObjectParameter("order") >> null
		when:
		DBDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceLocalParameter("displayCart", requestMock, responseMock)
	}
}
