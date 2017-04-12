package com.bbb.integration.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.order.droplet.OrderHistoryDroplet;
import com.bbb.account.vo.order.GetOrderHistoryResVO;
import com.bbb.account.vo.order.OrderHistory2ResVO;
import com.bbb.account.vo.order.OrderHistoryReqVO;
import com.bbb.account.vo.order.OrderHistoryResVO;
import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.sapient.common.tests.BaseTestCase;

public class TestGetOrderHistoryDroplet extends BaseTestCase {

	public void testGetOrderHistory() throws BBBBusinessException,
			BBBSystemException, ServletException, IOException {

		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		getRequest().setParameter("BBBProfileFormHandler",
				bbbProfileFormHandler);
		String pSiteId = (String) getObject("siteId");
		String orderType = (String) getObject("orderType");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext
				.getBBBSiteContext(pSiteId));
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
               siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
               throw new BBBSystemException("Exception" + siteContextException);
        }


		String email = (String) getObject("email");
		String password = (String) getObject("password");

		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		System.out.println("TestGetOrderHistoryDroplet.testGetOrderHistory.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		assertTrue(bbbProfileFormHandler.getProfile().isTransient());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
		bbbProfileFormHandler.setBBBOrder(null);
		boolean isLogin = bbbProfileFormHandler.handleLogin(getRequest(),
				getResponse());
		
		System.out.println("bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		assertFalse(bbbProfileFormHandler.getProfile().isTransient());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
		System.out.println("bbbProfileFormHandler.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		getRequest().setParameter("BBBProfileFormHandler",
				bbbProfileFormHandler);
		getRequest().setParameter("siteId",pSiteId);
		getRequest().setParameter("orderType",orderType);
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		OrderHistoryDroplet orderHistoryDroplet = (OrderHistoryDroplet) getObject("orderHistoryDroplet");
		
		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			orderHistoryDroplet.service(pRequest, pResponse);

			List<OrderHistory2ResVO> orderList = (ArrayList<OrderHistory2ResVO>) getRequest()
					.getObjectParameter("OrderList"); // getOrderHistoryResVO.getOrderList();
			//assertFalse(orderList==null);
			assertFalse(orderList!=null && orderList.size()>0);
			/*OrderHistoryResVO order1 = orderList.get(0);
			addObjectToAssert("orderNumber", order1.getOrderNumber());
			addObjectToAssert("orderState", order1.getOrderStatus());*/

			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
	}

}
