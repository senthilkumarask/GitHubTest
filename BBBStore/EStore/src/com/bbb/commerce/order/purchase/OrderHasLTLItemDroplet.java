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
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author spruth The service method checks for items in cart and returns
 *         parameter based on order contains ltl item or not
 * 
 */
public class OrderHasLTLItemDroplet extends BBBDynamoServlet {

	public static final String ORDER_HAS_LTL_ITEM = "orderHasLTLItem";
	public static final String ORDER_HAS_VDC_ITEM = "orderHasVDCItem";
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

		boolean ordercContainsLTLItem=false;
		boolean ordercContainsVDCItem=false;
		try {
			ordercContainsLTLItem=getManager().orderContainsLTLItem(order);
			ordercContainsVDCItem=getManager().orderContainsVDCItem(order);
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError("BBBSystemException occourred from Catalog API call");
			}
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError("BBBBusinessException occourred from Catalog API call");
			}
		}
		if (ordercContainsLTLItem) {
			req.setParameter(ORDER_HAS_LTL_ITEM, true);
		} else {
			req.setParameter(ORDER_HAS_LTL_ITEM, false);
		}
		if (ordercContainsVDCItem) {
			req.setParameter(ORDER_HAS_VDC_ITEM, true);
		} else {
			req.setParameter(ORDER_HAS_VDC_ITEM, false);
		}
		req.serviceLocalParameter(OUTPUT, req, res);
	}

}
