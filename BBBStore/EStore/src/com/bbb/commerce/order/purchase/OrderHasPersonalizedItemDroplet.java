/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  OrderContainsLTLItemDroplet.java
 *
 *  DESCRIPTION: This class shows that the order contains LTL Item or not
 *
 *  HISTORY:
 *  May 14, 2014  Initial version
 */

package com.bbb.commerce.order.purchase;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author spruth The service method checks for items in cart and returns
 *         parameter based on order contains personalized item or not
 * 
 */
public class OrderHasPersonalizedItemDroplet extends BBBDynamoServlet {

	public static final String ORDER_HAS_PERSONALIZED_ITEM = "orderHasPersonalizedItem";
	
	public static final String ATG_ORDER = "/atg/commerce/ShoppingCart";
	public static final String ORDER = "order";
	private static final String OUTPUT = "output";

	//private BBBCatalogToolsImpl catalogTools;
	private BBBCheckoutManager mManager;
	
	  /**
     * @return mManager
     */
    public final BBBCheckoutManager getManager() {
        return mManager;
    }
    /**
     * @param manager
     */
    public final void setManager(BBBCheckoutManager manager) {
        this.mManager = manager;
    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
	 * atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		Order order = (Order) req.getObjectParameter(BBBCoreConstants.ORDER);

		boolean ordercContainsPersonalizedItem=false;
		try {
			ordercContainsPersonalizedItem=getManager().ordercContainsPersonalizedItem(order);
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError("BBBSystemException occourred from Catalog API call");
			}
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError("BBBBusinessException occourred from Catalog API call");
			}
		}
		if (ordercContainsPersonalizedItem) {
			req.setParameter(ORDER_HAS_PERSONALIZED_ITEM, true);
		} else {
			req.setParameter(ORDER_HAS_PERSONALIZED_ITEM, false);
		}
		req.serviceLocalParameter(OUTPUT, req, res);
	}
	
	
	/**
	 * @param orderId : Wrapper to be called for disabling checkout button for international shipping on mobileweb.
	 * @return String:contains true or false based on which checkout button is enable or disabled
	 * @throws BBBSystemException
	 */
	public String getPersonalizedItemFlag() throws BBBSystemException{
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_ORDER);			
		pRequest.setParameter(ORDER, cart.getCurrent());			
			//calls the "service" method to fetch the details of the legacy order
			try {
				service(pRequest, pResponse);			
			} catch (ServletException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details, "Exception occurred while fetching the legacy order details");
		}catch (IOException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_Legacy_Details,"Exception occurred while fetching the legacy order details");
		}	
			return pRequest.getParameter(ORDER_HAS_PERSONALIZED_ITEM);
}

}
