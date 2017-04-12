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
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.wishlist.GiftListVO;

/**
 * @author sanam The method sees whether the order or the giftListVo contains
 * error personalized  items
 * 
 */
public class OrderHasErrorPersonalizedItemDroplet extends BBBDynamoServlet {

	public static final String ORDER_HAS_ERR_PRSNL_ITEM = "orderHasErrorPrsnlizedItem";
	private static final String OUTPUT = "output";

	//private BBBCheckoutManager manager;
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
	@SuppressWarnings("unchecked")
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		boolean orderHasErrorPrsnlizedItem=false;
		  try {
			  if ( req.getObjectParameter(BBBCoreConstants.ORDER)!= null) {
				  
			      Order order = (Order) req.getObjectParameter(BBBCoreConstants.ORDER);
				  orderHasErrorPrsnlizedItem = getManager().orderContainsErrorPersonalizedItem(order);
				  
		        } else if (req.getObjectParameter("items") != null) {
		        	
				  List<GiftListVO> giftList = (List<GiftListVO>) req.getObjectParameter("items");
				  orderHasErrorPrsnlizedItem = getManager().savedItemsContainErrorPersonalizedItem(giftList);
				  
			  }
		  	} catch (BBBSystemException e) {
				logError("BBBSystemException occourred from Catalog API call");
		  	} catch (BBBBusinessException e) {
				logError("BBBBusinessException occourred from Catalog API call");
		  	}
		if (orderHasErrorPrsnlizedItem) {
			req.setParameter(ORDER_HAS_ERR_PRSNL_ITEM, true);
		} else {
			req.setParameter(ORDER_HAS_ERR_PRSNL_ITEM, false);
		}
		req.serviceLocalParameter(OUTPUT, req, res);
	}

}
