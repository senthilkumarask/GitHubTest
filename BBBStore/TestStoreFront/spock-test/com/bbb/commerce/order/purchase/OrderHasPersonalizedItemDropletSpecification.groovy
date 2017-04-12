package com.bbb.commerce.order.purchase

import atg.commerce.order.OrderHolder
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import javax.servlet.ServletException

import spock.lang.specification.BBBExtendedSpec

class OrderHasPersonalizedItemDropletSpecification extends BBBExtendedSpec {
	def  OrderHasPersonalizedItemDroplet ohpiDroplet
	def BBBCheckoutManager cManager =Mock()
	def BBBOrder order = Mock()
	def OrderHolder cart = Mock()

	def setup(){
		  ohpiDroplet = new OrderHasPersonalizedItemDroplet(manager : cManager)
	}
	
	def"service TC to check order contains personalized item or not"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.ordercContainsPersonalizedItem(order) >> true
		when:
			ohpiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasPersonalizedItem", true)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("orderHasPersonalizedItem", false)

	}
	
	def"service. Tc for BBBSystemException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.ordercContainsPersonalizedItem(order) >> {throw new BBBSystemException("exception")}
			
		when:
			ohpiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasPersonalizedItem", false)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			
		
	}
	
	def"service. Tc for BBBBusinessException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.ordercContainsPersonalizedItem(order) >> {throw new BBBBusinessException("exception")}
			
		when:
			ohpiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasPersonalizedItem", false)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
		
	}
	
	/********************************************getPersonalizedItemFlag ************************************/
	
	def"getPersonalizedItemFlag.TC to check order contains personalized item or not"(){
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			requestMock.getObjectParameter("order") >> order
			1*cManager.ordercContainsPersonalizedItem(order) >> true
			requestMock.getParameter("orderHasPersonalizedItem") >> true
		when:
			boolean value = ohpiDroplet.getPersonalizedItemFlag()
		then:
			1 * requestMock.setParameter("order", order)
			value == true
	}
	
	def"getPersonalizedItemFlag.TC for ServletException"(){
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			requestMock.getObjectParameter("order") >> order
			1*cManager.ordercContainsPersonalizedItem(order) >> true
			requestMock.getParameter("orderHasPersonalizedItem") >> true
			requestMock.serviceLocalParameter("output", requestMock, responseMock) >> {throw new ServletException("exception")}
		when:
			boolean value = ohpiDroplet.getPersonalizedItemFlag()
		then:
			BBBSystemException exceptiion = thrown()
			exceptiion.getMessage().equals("err_Legacy_Detail_1001:Exception occurred while fetching the legacy order details")
	}
	
	def"getPersonalizedItemFlag.TC for IOException"(){
		given:
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			requestMock.getObjectParameter("order") >> order
			1*cManager.ordercContainsPersonalizedItem(order) >> true
			requestMock.getParameter("orderHasPersonalizedItem") >> true
			requestMock.serviceLocalParameter("output", requestMock, responseMock) >> {throw new IOException("exception")}
		when:
			boolean value = ohpiDroplet.getPersonalizedItemFlag()
		then:
			BBBSystemException exceptiion = thrown()
			exceptiion.getMessage().equals("err_Legacy_Detail_1001:Exception occurred while fetching the legacy order details")
	}
}
