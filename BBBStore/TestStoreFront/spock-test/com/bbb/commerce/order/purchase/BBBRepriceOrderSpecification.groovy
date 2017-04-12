package com.bbb.commerce.order.purchase

import atg.commerce.order.CommerceItem
import atg.commerce.order.OrderManager
import atg.commerce.order.OrderTools
import atg.repository.Repository
import atg.repository.RepositoryItem
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.utils.CommonConfiguration
import spock.lang.specification.BBBExtendedSpec

class BBBRepriceOrderSpecification extends BBBExtendedSpec {

	def BBBRepriceOrder repOredr
	def CommonConfiguration cConfiguration = Mock()
	def BBBOrderImpl orderMock = Mock() 
	def OrderManager oManagerMock = Mock()
	def OrderTools oToolsMock = Mock()
	def Repository orderRepository = Mock()
	def RepositoryItem orderItem = Mock()
	def CommerceItem cItem1 = Mock()
	def CommerceItem cItem2 = Mock()
	
	def setup(){
		//repOredr = new BBBRepriceOrder(commonConfiguration : cConfiguration, order:orderMock, orderManager : oManagerMock)
		repOredr = Spy()
		repOredr.setCommonConfiguration(cConfiguration)
		repOredr.setOrder(orderMock)
		repOredr.setOrderManager(oManagerMock)
	}
	def "service. TC to logOrderDetails" (){
		given:
			orderMock.getId() >> "id"
			orderMock.getVersion() >> 1
			orderMock.getCommerceItemCount() >> 2
			2*cConfiguration.isLogDebugEnableOnRepriceOrderForOrderDetail() >> true
			oManagerMock.getOrderTools() >> oToolsMock
			2*oToolsMock.getOrderRepository() >> orderRepository
			2*orderRepository.getItem("id", "order") >> orderItem
			orderItem.getPropertyValue("version") >> 1
			
			2*orderMock.getCommerceItems() >> [cItem1, cItem2]
			orderMock.getCommerceItemCount() >> 2
			cItem1.getId() >> "c1"
			cItem2.getId() >> "c2"
			cItem1.getCatalogRefId() >> "sku1"
			cItem2.getCatalogRefId() >> "sku2"
			
			cItem1.getQuantity() >> 2
			cItem2.getQuantity() >> 3
			
			1*repOredr.callSuperService(requestMock, responseMock) >> {}
			
		when:
		
			repOredr.service(requestMock, responseMock)
		
		then:
			1 * repOredr.logDebug('preRepriceOrder: CommerceItems:{commerceItemId [c1,catalogRefId:sku1,Qty:2] commerceItemId [c2,catalogRefId:sku2,Qty:3] }')
			1 * repOredr.logDebug('postRepriceOrder: CommerceItems:{commerceItemId [c1,catalogRefId:sku1,Qty:2] commerceItemId [c2,catalogRefId:sku2,Qty:3] }')	
	}
	
	def "service. TC to logOrderDetails commerce item count is 0" (){
		given:
			orderMock.getId() >> "id"
			orderMock.getVersion() >> 1
			orderMock.getCommerceItemCount() >> 0
			2*cConfiguration.isLogDebugEnableOnRepriceOrderForOrderDetail() >> true
			oManagerMock.getOrderTools() >> oToolsMock
			2*oToolsMock.getOrderRepository() >> orderRepository
			2*orderRepository.getItem("id", "order") >> orderItem
			orderItem.getPropertyValue("version") >> 1
			
			2*orderMock.getCommerceItems() >> [cItem1]
			
			1*repOredr.callSuperService(requestMock, responseMock) >> {}
			
		when:
		
			repOredr.service(requestMock, responseMock)
		
		then:
            1 * repOredr.logDebug('postRepriceOrder:No CommerceItems:{}')
			1 * repOredr.logDebug('preRepriceOrder:No CommerceItems:{}')	
			
			}
	
	def "service. TC isLogDebugEnableOnRepriceOrderForOrderDetail is false" (){
		given:
			cConfiguration.isLogDebugEnableOnRepriceOrderForOrderDetail() >> false
			
		when:
		
			repOredr.service(requestMock, responseMock)
		
		then:
			0 * repOredr.logDebug(_)
			
			}
	
	/********************************* exception scenario ************************************/
	
	def "service. TC for Exception" (){
		given:
		    orderMock.getId() >> "id"
			cConfiguration.isLogDebugEnableOnRepriceOrderForOrderDetail() >> true
			oManagerMock.getOrderTools() >> oToolsMock
			oToolsMock.getOrderRepository() >> orderRepository
			orderRepository.getItem("id", "order")   >> {throw new Exception("exception") }
			1*repOredr.callSuperService(requestMock, responseMock) >> {}
		when:
		
			repOredr.service(requestMock, responseMock)
		
		then:
            2 * repOredr.logError('BBBRepriceOrder:logOrderDetails', _)			
			}

}
