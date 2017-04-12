package com.bbb.account.order.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

public class TestOrderSummaryDetails extends BaseTestCase{
	public void testService()throws Exception{
	    DynamoHttpServletRequest pRequest = getRequest();
    	DynamoHttpServletResponse pResponse = getResponse();
    	OrderSummaryDetails orderSummaryDetails = (OrderSummaryDetails)getObject("getOrderSummaryDetails");
		pRequest.setParameter("orderNum",
				(String)getObject("orderNum"));
		pRequest.setParameter("wsOrder",
				(String)getObject("wsOrder"));
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		atg.servlet.ServletUtil.setCurrentResponse(this.getResponse());
		
		orderSummaryDetails.service(pRequest, pResponse);
				
		assertNotNull(pRequest.getParameter("BBBTrackOrderVO"));
			
		
		
	}
}
