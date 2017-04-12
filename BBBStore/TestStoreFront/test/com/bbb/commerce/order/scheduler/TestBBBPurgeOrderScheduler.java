package com.bbb.commerce.order.scheduler;

import java.sql.Timestamp;
import java.util.Date;

import atg.commerce.order.OrderManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItemDescriptor;

import com.sapient.common.tests.BaseTestCase;

public class TestBBBPurgeOrderScheduler extends BaseTestCase{
	
	public void testGeneratePurgeQuery() throws Exception {
		
		BBBPurgeOrderScheduler orderScheduler = (BBBPurgeOrderScheduler)getObject("bbbpurgeorderscheduler");
		orderScheduler.setSchedulerEnabled(true);
		OrderManager ordManager = (OrderManager)getObject("ordermanager");
		int daysToPurge = (Integer)getObject("daysToPurge");
		//String creationdate = (String)getObject("creationdate");
		Date myDate = (Date)getObject("myDate");
		MutableRepository mutRep = (MutableRepository) ordManager.getOrderTools().getOrderRepository();
		RepositoryItemDescriptor orderDescriptor = mutRep.getItemDescriptor("order");
		//RepositoryView orderView =  orderDescriptor.getRepositoryView();	
		String orderId = (int)(Math.random()*9999) + "test1234";
		MutableRepositoryItem mutItem = mutRep.createItem(orderId, "order");
		mutItem.setPropertyValue("agentId", "agentId");
		mutItem.setPropertyValue("billingAddress", null);
		mutItem.setPropertyValue("commerceItems", null);		
		mutItem.setPropertyValue("submittedDate", new Timestamp(myDate.getTime()));
		System.out.println("date long: "+myDate.getTime());
		System.out.println("date"+myDate.getYear());
		System.out.println("daysToPurge"+daysToPurge);
		mutRep.addItem(mutItem);
		mutRep.updateItem(mutItem);
		
		//orderScheduler.setDaysToPurge(daysToPurge);
		orderScheduler.doScheduledTask(null, null);
		//RepositoryItem[] orderItems = orderScheduler.findAllPurgeOrders().length;
		assertNull("Order item is not null", mutRep.getItem(orderId, "order"));
		//assertEquals("test123", (String)orderItems[0].getPropertyValue("id"));
		//mutRep.removeItem("test123", "order");		
	}

}
