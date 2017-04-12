package com.bbb.commerce.order.purchase

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.wishlist.GiftListVO

import atg.commerce.order.OrderImpl;
import spock.lang.specification.BBBExtendedSpec

class OrderHasErrorPersonalizedItemDropletSpecification extends BBBExtendedSpec {
	
	def OrderHasErrorPersonalizedItemDroplet ohepiDroplet
	def BBBCheckoutManager cManager =Mock()
	def OrderImpl order = Mock()
	GiftListVO glVO = new GiftListVO()

	def setup(){
		ohepiDroplet = new OrderHasErrorPersonalizedItemDroplet(manager : cManager)
	}
	def "service. TC to check order  contains error personalized  items"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsErrorPersonalizedItem(order) >> true
		when:
			ohepiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasErrorPrsnlizedItem", true)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("orderHasErrorPrsnlizedItem", false)

		
	}
	
	def "service. TC to check  giftListVo contains error personalized  items"(){
		given:
			requestMock.getObjectParameter("order") >> null
			2*requestMock.getObjectParameter("items") >> [glVO]
			1*cManager.savedItemsContainErrorPersonalizedItem([glVO]) >> true
		when:
			ohepiDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("orderHasErrorPrsnlizedItem", true)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("orderHasErrorPrsnlizedItem", false)

		
	}
	
	def "service. TC order is null and giftList vo is also null"(){
		given:
			requestMock.getObjectParameter("order") >> null
			requestMock.getObjectParameter("items") >> null
		when:
			ohepiDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.setParameter("orderHasErrorPrsnlizedItem", false)
			0 * cManager.savedItemsContainErrorPersonalizedItem(_)
			0 * cManager.orderContainsErrorPersonalizedItem(order)
		
	}
	
	/*****************************************exception scenario ************************************/
	
	def "service. TC for BBBSystemException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsErrorPersonalizedItem(order) >> {throw new BBBSystemException("exception")}
		when:
			ohepiDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.setParameter("orderHasErrorPrsnlizedItem", false)

	}
	
	def "service. TC for BBBBusinessException"(){
		given:
			requestMock.getObjectParameter("order") >> order
			1*cManager.orderContainsErrorPersonalizedItem(order) >> {throw new BBBBusinessException("exception")}
		when:
			ohepiDroplet.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.setParameter("orderHasErrorPrsnlizedItem", false)

	}
}
