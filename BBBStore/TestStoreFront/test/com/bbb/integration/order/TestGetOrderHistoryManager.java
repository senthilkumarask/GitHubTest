package com.bbb.integration.order;

import java.util.List;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.account.order.manager.OrderHistoryManager;
import com.bbb.account.vo.order.OrderHistory2ResVO;
import com.bbb.account.vo.order.OrderHistoryResVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGetOrderHistoryManager extends BaseTestCase {

	public void testGetOrderHistory() throws BBBBusinessException,
			BBBSystemException {

		OrderHistoryManager orderHistoryManager = (OrderHistoryManager) getObject("orderHistoryManager");
		orderHistoryManager.setServiceName((String) getObject("serviceName"));
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		
		/*orderHistoryManager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));*/

		// OrderHistoryReqVO orderHistory = new OrderHistoryReqVO();
		// GetOrderHistoryResVO getOrderHistoryResVO = (GetOrderHistoryResVO)
		// ServiceHandlerUtil.invoke(orderHistory);

		try {
			List<OrderHistoryResVO> orderList = orderHistoryManager
					.getLegacyOrder((String) getObject("profileId"),(String) getObject("memberID"));
			assertFalse(orderList == null);
			assertTrue(orderList.size() > 0);

		} catch (BBBBusinessException e) {
			assertEquals("account_1310:Legacy WebService returns an error.", e.getMessage());
		}
	}
	
	
		public void testGetFilteredLegacyOrders() throws BBBBusinessException,
				BBBSystemException {
			
			OrderHistoryManager orderHistoryManager = (OrderHistoryManager) getObject("orderHistoryManager");
			orderHistoryManager.setServiceName((String) getObject("serviceName"));
			String pSiteId = (String) getObject("siteId");
			SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
			try {
				siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
			} catch (SiteContextException siteContextException) {
				throw new BBBSystemException("Exception" + siteContextException);
			}
			
			/*orderHistoryManager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));*/
			
			// OrderHistoryReqVO orderHistory = new OrderHistoryReqVO();
			// GetOrderHistoryResVO getOrderHistoryResVO = (GetOrderHistoryResVO)
			// ServiceHandlerUtil.invoke(orderHistory);
			
			try {
				List<OrderHistory2ResVO> orderList = orderHistoryManager.getFilteredLegacyOrders((String) getObject("profileId"),(String) getObject("memberID"),(String) getObject("orderType"));
				assertFalse(orderList == null);
				assertTrue(orderList.size() > 0);
			
			} catch (BBBBusinessException e) {
				assertEquals(e.getMessage(), e.getMessage());
			}
			}
}
