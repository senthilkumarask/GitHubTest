package com.bbb.commerce.order.purchase

import atg.commerce.order.OrderHolder
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import javax.servlet.ServletException
import spock.lang.specification.BBBExtendedSpec

class ShippingMethodDescriptionDropletSpecification extends BBBExtendedSpec {
	def ShippingMethodDescriptionDroplet smdDroplet 
	def BBBOrder order = Mock()
	def BBBCheckoutManager cManager = Mock()
	def BBBShippingGroupManager sgManager = Mock()
	def OrderHolder cart = Mock()
	def setup(){
		smdDroplet = new ShippingMethodDescriptionDroplet(manager : cManager, shippingGroupManager : sgManager)
	}
	def "service. TC to get single shipping description"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.displaySingleShipping(order, false) >> true
			1*sgManager.getSingleShippingDescription(requestMock, order) >> "descSingle"
			
		when:
			smdDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("output" , requestMock, responseMock)
			1*requestMock.setParameter("shippingMethodDescription", "descSingle")
	}

	def "service. TC to get multy shipping description"(){
		given:
			requestMock.getObjectParameter("order") >> order
			cManager.displaySingleShipping(order, false) >> false
			1*sgManager.getMultiShippingDescription(requestMock, order) >> "multyShippingDesc"
		when:
			smdDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("output" , requestMock, responseMock)
			1*requestMock.setParameter("shippingMethodDescription", "multyShippingDesc")
			0*sgManager.getSingleShippingDescription(requestMock, order)
			
	}

/**********************************exception scenario ***********************************/
		
	def "service. TC for BBBSystemException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			cManager.displaySingleShipping(order, false) >> false
			sgManager.getMultiShippingDescription(requestMock, order) >> {throw new BBBSystemException("exception")}
		when:
			smdDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("empty" , requestMock, responseMock)
			1*requestMock.setParameter("shippingMethodDescription", _)
	}
	
	def "service. TC for BBBBusinessException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			cManager.displaySingleShipping(order, false) >> false
			sgManager.getMultiShippingDescription(requestMock, order) >> {throw new BBBBusinessException("exception")}
		when:
			smdDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.serviceLocalParameter("empty" , requestMock, responseMock)
			1*requestMock.setParameter("shippingMethodDescription", _)
	}

/******************************************getDescription*************************************/
	
	def"getDescription. TC to get description"(){
		given:
			smdDroplet = Spy()
			1*requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			1*smdDroplet.service(requestMock, responseMock) >> {}
			
			requestMock.getObjectParameter("shippingMethodDescription") >> "singleDescrip"
		when:
			String value = smdDroplet.getDescription()
		then:	
			1*requestMock.setParameter("order", cart.getCurrent())
			value == "singleDescrip"
	}
	
	def"getDescription. TC for ServletException"(){
		given:
			smdDroplet = Spy()
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			smdDroplet.service(requestMock, responseMock) >> {throw new ServletException("exception")}
			
		when:
			String value = smdDroplet.getDescription()
		then:
		BBBSystemException exception = thrown()
		value == null
	}
	
	def"getDescription. TC for IOException"(){
		given:
			smdDroplet = Spy()
			requestMock.resolveName("/atg/commerce/ShoppingCart") >> cart
			cart.getCurrent() >> order
			smdDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
			
		when:
			String value = smdDroplet.getDescription()
		then:
		BBBSystemException exception = thrown()
		value == null
	}

}
