package com.bbb.payment.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.order.bean.TBSCommerceItem;

/**
 * This Droplet performs payment types related front end operations.
 * 
 */
public class TBSPaymentGroupDroplet extends BBBPaymentGroupDroplet {
	
	@Override
	public void getPaymentGroupStatusOnLoad(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		
		boolean isKirsch = false;
		boolean isCMO = false;
		Order order = null;
		Object orderObj = pRequest
				.getLocalParameter(BBBCheckoutConstants.ORDER);
		if (orderObj instanceof Order) {
			order = (Order) orderObj;
			List<CommerceItem> cItems = order.getCommerceItems();
			for (CommerceItem cItem : cItems) {
				if (cItem instanceof TBSCommerceItem) {
					TBSCommerceItem tbsItem = (TBSCommerceItem) cItem;
					if(tbsItem.isKirsch()){
						isKirsch = true;
						if(isLoggingDebug()){
							logDebug("Order with id " + order.getId() + " contains Kirsch items : ");
						}
					} else if(tbsItem.isCMO()){
						isCMO = true;
						if(isLoggingDebug()){
							logDebug("Order with id " + order.getId() + " contains CMO items : ");
						}
					}
				}
			}
		}
		
		pRequest.setParameter(TBSConstants.IS_KIRSCH_ITEM, isKirsch);
		pRequest.setParameter(TBSConstants.IS_CMO_ITEM, isCMO);
		
		super.getPaymentGroupStatusOnLoad(pRequest, pResponse);
		
	}

}
