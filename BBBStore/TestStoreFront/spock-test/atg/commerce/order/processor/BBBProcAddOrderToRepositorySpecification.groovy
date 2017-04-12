package atg.commerce.order.processor


import java.util.Map;

import atg.commerce.order.InvalidParameterException
import atg.commerce.order.OrderManager
import atg.commerce.order.OrderTools
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository
import atg.service.pipeline.PipelineResult

import com.bbb.ecommerce.order.BBBOrder
import org.junit.Before;
import org.junit.Test;
import spock.lang.specification.BBBExtendedSpec

class BBBProcAddOrderToRepositorySpecification extends BBBExtendedSpec {

	def BBBProcAddOrderToRepository testObj
	def OrderManager orderManagerMock = Mock()
	def BBBOrder orderMock = Mock()
	def MutableRepositoryItem repositoryItemMock = Mock()
	def PipelineResult resultMock = Mock()
	def OrderTools orderToolsMock = Mock()
	def MutableRepository repositoryMock = Mock()
	def setup(){
		testObj = new BBBProcAddOrderToRepository ()
	}
    
	def "runProcess. to check return value when order is transient and order text map is not empty " () {
		given:
	//	testObj = Spy()
		Map<Object, Object> param =  new HashMap();
		param.put("OrderManager", orderManagerMock );
		param.put("Order", orderMock);
		Map<String, MutableRepositoryItem> orderTaxItem =  ["item" : repositoryItemMock]
	    orderMock.isTransient() >> true
		
		orderToolsMock.getOrderRepository() >> repositoryMock 
		orderManagerMock.getOrderTools() >>  orderToolsMock
		orderMock.getAnonymousOrderTaxItem() >> orderTaxItem
		//testObj.runProcess(param, result) >> 1
		when:
		int value = testObj.runProcess(param, resultMock)
		then:
       	value == 1
		1 * repositoryMock.addItem(repositoryItemMock)
	}
	
	def "runProcess. to check return value when order is transient and 'order tax' map is  empty " () {
		given:
		testObj = Spy()
		Map<Object, Object> param =  new HashMap();
		param.put("OrderManager", orderManagerMock );
		param.put("Order", orderMock);
		Map<String, MutableRepositoryItem> orderTaxItem =  new HashMap();
		orderMock.isTransient() >> true
		
		orderToolsMock.getOrderRepository() >> repositoryMock
		orderManagerMock.getOrderTools() >>  orderToolsMock
		orderMock.getAnonymousOrderTaxItem() >> orderTaxItem
		when:
		int value = testObj.runProcess(param, resultMock)
		then:
		   value == 1
		   0 * repositoryMock.addItem(_)
	}
	
	def "runProcess. to check return value when order is not transient and 'order tax' map is not empty " () {
		given:
		testObj = Spy()
		Map<Object, Object> param =  new HashMap();
		param.put("OrderManager", orderManagerMock );
		param.put("Order", orderMock);
		Map<String, MutableRepositoryItem> orderTaxItem =  ["item" : repositoryItemMock]
		orderMock.isTransient() >> false
		
		orderToolsMock.getOrderRepository() >> repositoryMock
		orderManagerMock.getOrderTools() >>  orderToolsMock
		orderMock.getAnonymousOrderTaxItem() >> orderTaxItem
		when:
		int value = testObj.runProcess(param, resultMock)
		then:
		   0 * repositoryMock.addItem(repositoryItemMock)
	}
	
	def "runProcess. to check 'invalid Parameter Exception when order is null' " () {
		given:
		testObj = Spy()
		Map<Object, Object> param =  new HashMap();
		param.put("OrderManager", orderManagerMock );
		param.put("Order", null);
		
		when:
		int value = testObj.runProcess(param, resultMock)
		then:
		InvalidParameterException exception  = thrown()
	}
	
	def "runProcess. to check 'invalid Parameter Exception when OrderManager is null' " () {
		given:
		testObj = Spy()
		Map<Object, Object> param =  new HashMap();
		param.put("OrderManager", null );
		param.put("Order", orderMock);
		
		when:
		int value = testObj.runProcess(param, resultMock)
		then:
		InvalidParameterException exception  = thrown()
	}


}
