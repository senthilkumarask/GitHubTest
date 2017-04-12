package com.bbb.account;

import atg.multisite.SiteContext;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.order.droplet.LegacyOrderDetailsDroplet;
import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestLegacyOrderDetailsDroplet extends BaseTestCase{
	public void testService()throws Exception{
	    DynamoHttpServletRequest pRequest = getRequest();
    	DynamoHttpServletResponse pResponse = getResponse();
		LegacyOrderDetailsDroplet legacyOrderDroplet=(LegacyOrderDetailsDroplet)getObject("" +
				"getLegacyOrderDetailsDroplet");
		pRequest.setParameter("orderId",
				(String)getObject("orderid"));
		pRequest.setParameter("email","LDUSEJA@SAPIENT.COM");
		
		OrderDetailsManager orderDetailsManager=(OrderDetailsManager)Nucleus.getGlobalNucleus()		
				.resolveName("/com/bbb/account/OrderDetailsManager");
		/*orderDetailsManager.setSiteContext((BBBSiteContext.getBBBSiteContext(
				(String)getObject("siteId"))));*/
		
		legacyOrderDroplet.service(pRequest, pResponse);
		
		if(pRequest.getParameter("empty")==null || !pRequest.getParameter("empty").toString().equals("empty")) {
			if(pRequest.getParameter("error")==null || !pRequest.getParameter("error").toString().equals("error")) {
			
				assertNotNull(pRequest.getParameter("orderDetails"));
			} else {
				assertNotNull(pRequest.getParameter("error"));
			}
		} else {
			assertNotNull(pRequest.getParameter("empty"));
		}
		
	}
}
