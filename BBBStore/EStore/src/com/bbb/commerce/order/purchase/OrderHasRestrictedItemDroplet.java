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
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.core.util.StringUtils;
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
public class OrderHasRestrictedItemDroplet extends BBBDynamoServlet {

	public static final String ORDER_HAS_RESTRICTED_ITEM = "orderHasIntlResterictedItem";
	public static final String INTL_RESTRICTED_ITEMS = "intlRestrictedItems";
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

		boolean ordercContainsIntlResterictedItem=false;
		String intlRestrictedItems=BBBCoreConstants.BLANK;
		
		
		try {
			 intlRestrictedItems=getManager().orderContainsIntlRestrictedItem(order);
			if(!StringUtils.isBlank(intlRestrictedItems)){
			ordercContainsIntlResterictedItem=true;
			}
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError("BBBSystemException occourred from Catalog API call");
			}
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError("BBBBusinessException occourred from Catalog API call");
			}
		}
		if (ordercContainsIntlResterictedItem) {
			req.setParameter(ORDER_HAS_RESTRICTED_ITEM, true);
			
			req.setParameter(INTL_RESTRICTED_ITEMS, intlRestrictedItems);
			
		} else {
			req.setParameter(ORDER_HAS_RESTRICTED_ITEM, false);
		}
		req.serviceLocalParameter(OUTPUT, req, res);
	}

}
