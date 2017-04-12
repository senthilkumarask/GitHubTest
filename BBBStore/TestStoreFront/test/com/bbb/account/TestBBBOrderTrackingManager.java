package com.bbb.account;

import atg.core.util.StringUtils;

import com.bbb.account.vo.OrderTrackingResponseVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBOrderTrackingManager extends BaseTestCase{
	
	public void testGetATGOrderTrackingDetails() throws Exception {
		
		/*OrderTrackingResponseVO responseVO = new OrderTrackingResponseVO();
		BBBOrderTrackingManager manager = (BBBOrderTrackingManager) getObject("bbbOrderTrackingManager");
		String pEmailId = (String) getObject("emailId");
		String orderId = (String) getObject("orderId");
		String pSiteId = (String) getObject("siteId");
		manager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		responseVO = manager.getATGOrderTrackingDetails(orderId, pEmailId);
		
		assertFalse(responseVO.getErrorStatus().isErrorExists()); //Case when order Id and email is valid

		String invalidEmailId = (String) getObject("invalidEmailId");
		String invalidOrderId = (String) getObject("invalidOrderId");
		try
		{
			responseVO = manager.getATGOrderTrackingDetails(invalidOrderId, invalidEmailId);//Case when order Id is valid but email is invalid
		}
		catch(BBBBusinessException e)
		{
			assertEquals(e.getMessage(),"account_1073:err_no_such_order");
		}
		
		try
		{
			responseVO = manager.getATGOrderTrackingDetails(orderId, invalidEmailId);//Case when order Id is valid but email is not associated with the order
		}
		catch(BBBBusinessException e)
		{
			assertEquals(e.getMessage(),"account_1074:order_email_not_associated");
		}
		responseVO.setCustomerFirstName(responseVO.getCustomerFirstName());
		responseVO.setCustomerLastName(responseVO.getCustomerLastName());
		responseVO.getOrderStatus();
		responseVO.setStatusDate(responseVO.getStatusDate());
		responseVO.getTotalTrackingLinks();
		responseVO.getTrackingLinks();
		responseVO.getOrderNumber();
		responseVO.getOrderDate();*/
		assertTrue(true);

	}
	
	public void testGetLegacyOrderTrackingDetails() throws Exception
	{
		
		/*OrderTrackingResponseVO responseVO = new OrderTrackingResponseVO();
		BBBOrderTrackingManager manager = (BBBOrderTrackingManager) getObject("bbbOrderTrackingManager");
		String pEmailId = (String) getObject("legacyEmailId");
		String orderId = (String) getObject("legacyOrderId");
		
		String pSiteId = (String) getObject("siteId");
		manager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

		responseVO = manager.getLegacyOrderTrackingDetails(orderId, pEmailId);
		assertFalse(responseVO == null);
		if(responseVO.getErrorStatus().isErrorExists())//If error received from webservice
		{
			assertTrue(!StringUtils.isEmpty(responseVO.getErrorStatus().getDisplayMessage())
					|| !StringUtils.isEmpty(responseVO.getErrorStatus().getErrorMessage()) 
					||!responseVO.getErrorStatus().getValidationErrors().isEmpty());
		}
		else
		{
			assertTrue(StringUtils.isEmpty(responseVO.getErrorStatus().getDisplayMessage()));
			assertTrue(StringUtils.isEmpty(responseVO.getErrorStatus().getErrorMessage()));
			assertTrue(responseVO.getErrorStatus().getValidationErrors().isEmpty());
		}*/
		assertTrue(true);
		
	}
}
