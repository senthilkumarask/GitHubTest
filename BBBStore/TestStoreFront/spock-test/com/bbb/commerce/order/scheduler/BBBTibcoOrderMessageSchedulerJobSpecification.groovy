package com.bbb.commerce.order.scheduler

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.Order
import atg.commerce.order.OrderTools
import atg.multisite.SiteContextException
import atg.multisite.SiteContextManager
import atg.nucleus.GenericService
import atg.repository.Repository
import atg.repository.RepositoryItemDescriptor
import atg.service.scheduler.ScheduledJob
import atg.service.scheduler.Scheduler

import com.bbb.commerce.checkout.tibco.BBBSubmitOrderHandler
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.exception.BBBSystemException

class BBBTibcoOrderMessageSchedulerJobSpecification extends BBBExtendedSpec {
	def BBBOrderManager orderManagerMock = Mock()
	def BBBSubmitOrderHandler orderHelperMock = Mock()
	def BBBTibcoOrderMessageSchedulerJob tibcoOrderMessageScheduler
	def SiteContextManager siteContextManagerMock = Mock()
	def Repository repositoryMock = Mock()
	def RepositoryItemDescriptor itemDecsMock = Mock()
	def OrderTools orderToolsMock = Mock()
	int OrderBatchSize = 3
	int orderSubmittedDurationToSearch = 1
	def setup(){
		tibcoOrderMessageScheduler = new BBBTibcoOrderMessageSchedulerJob(orderManager:orderManagerMock,orderHelper:orderHelperMock,
			siteContextManager:siteContextManagerMock,mOrderBatchSize : OrderBatchSize,mOrderSubmittedDurationToSearch:orderSubmittedDurationToSearch,mOrderPickTimeDiffSeconds:120)
	}
	def "Disabling the submitting of orders when scheduler runs"(){
		given: 
		tibcoOrderMessageScheduler.setLoggingDebug(true)
		0*orderManagerMock.getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED, _,_,_)
		expect:"Submit Order Scheduler is DISABLED !!!"
		tibcoOrderMessageScheduler.doScheduledTask(new Scheduler(),new ScheduledJob("","","",null,null,true))
	}
	def "processing unsubmited order to fulfillment system no more order to be submit"(){
		given:
		tibcoOrderMessageScheduler.setSchedulerEnabled(true)
		orderManagerMock.getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED,_,_,_) >> null
		0*orderManagerMock.getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED, _,_,_)
		expect:
		tibcoOrderMessageScheduler.doScheduledTask()
	}
	def "processing unsubmited order to fulfillment system throws SystemException"(){
		given:
		tibcoOrderMessageScheduler = Spy()
		tibcoOrderMessageScheduler.setOrderManager(orderManagerMock)
		tibcoOrderMessageScheduler.setSchedulerEnabled(true)
		tibcoOrderMessageScheduler.setLoggingError(true)
		tibcoOrderMessageScheduler.setLoggingDebug(true)
		orderManagerMock.getUnsubmittedOrders(_,_,_,_) >> {throw new BBBSystemException("Mock Exception")}
		1*tibcoOrderMessageScheduler.logError("Exception while retrieving unsubmitted orders",_)
		expect:"Exception while retrieving unsubmitted orders"
		tibcoOrderMessageScheduler.doScheduledTask()
	}
	def "processing unsubmited order to fulfillment system "(){
		given:
		tibcoOrderMessageScheduler.setSchedulerEnabled(true)
		List<BBBOrder> orderList = new ArrayList<Order>();
		def BBBOrder order1 = Mock()
		order1.getSiteId() >> "tbs"
		orderList.add(order1)
		Order[] orderItems =  orderList.toArray()
		orderManagerMock.getUnsubmittedOrders(_,_,_,_) >> orderItems
		when:"Submiting Orders to fulfillment !!!"
		tibcoOrderMessageScheduler.doScheduledTask()
		then:
		1*orderHelperMock.processSubmitOrder(_, null);
		1*siteContextManagerMock.getSite("tbs")
	}
	def "processing unsubmited order to fulfillment system batch size and order length same"(){
		given:
		tibcoOrderMessageScheduler.setSchedulerEnabled(true)
		List<BBBOrder> orderList = new ArrayList<Order>();
		List<BBBOrder> orderList1 = new ArrayList<Order>();
		tibcoOrderMessageScheduler.setOrderBatchSize(3)
		def BBBOrder order1 = Mock()
		order1.getSiteId() >> "tbs"
		orderList.add(order1)
		orderList1.add(order1)
		def BBBOrder order2 = Mock()
		order2.getSiteId() >> "tbs"
		orderList.add(order2)
		Order[] orderItems =  orderList.toArray()
		Order[] orderItems2 =  orderList1.toArray()
		orderManagerMock.getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED,_,0,2) >> orderItems
		orderManagerMock.getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED,_,3,2) >> orderItems2
		when:"Submiting Orders to fulfillment !!!"
		tibcoOrderMessageScheduler.doScheduledTask()
		then:
		3*siteContextManagerMock.getSite("tbs")
		3*orderHelperMock.processSubmitOrder(_, null)
	}
	def "processing unsubmited order to fulfillment system throws SiteContext Exception "(){
		given:
		tibcoOrderMessageScheduler =  Spy()
		tibcoOrderMessageScheduler.setOrderManager(orderManagerMock)
		tibcoOrderMessageScheduler.setSiteContextManager(siteContextManagerMock)
		tibcoOrderMessageScheduler.setSchedulerEnabled(true)
		tibcoOrderMessageScheduler.setLoggingError(true)
		List<BBBOrder> orderList = new ArrayList<Order>();
		def BBBOrder order1 = Mock()
		orderList.add(order1)
		Order[] orderItems =  orderList.toArray(new Order[orderList.size()])
		orderManagerMock.getUnsubmittedOrders(_,_,_,_) >> orderItems
		siteContextManagerMock.pushSiteContext(_) >> {throw new SiteContextException("")}
		1*tibcoOrderMessageScheduler.logError('SiteContextException occured in BBBTibcoOrderMessageSchedulerJobwhile while pushing siteId for order null', _)
		expect:"SiteContextException occured in BBBTibcoOrderMessageSchedulerJob while pushing siteId for order"
		tibcoOrderMessageScheduler.doScheduledTask()
	}
}
