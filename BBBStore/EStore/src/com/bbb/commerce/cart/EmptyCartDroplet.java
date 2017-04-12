package com.bbb.commerce.cart;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.ShippingGroupManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.logging.LogMessageFormatter;

public class EmptyCartDroplet extends BBBDynamoServlet {

	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
			//OrderHolder shoppingCart = (OrderHolder) req.resolveName(getCart());
			//BBBOrder order = (BBBOrder) shoppingCart.getCurrent();
			BBBOrder order = (BBBOrder) req.getObjectParameter(BBBCoreConstants.ORDER);
			
			synchronized (order) {
				try {
					
					/*getOrderManager().removeOrder(order.getId());
					order.setState(5017);
					getOrderManager().updateOrder(order);*/

					/*getCommerceItemManager().removeAllCommerceItemsFromOrder(order);
					getShippingGroupManager().removeAllShippingGroupsFromOrder(order);
					getPaymentGroupManager().removeAllPaymentGroupsFromOrder(order);

					order.setBillingAddress(new BBBRepositoryContactInfo());*/
					order.setState(5017);
					getOrderManager().updateOrder(order);
					
				} catch (CommerceException e) {
					logError(LogMessageFormatter.formatMessage(req, "Error while emptying the cart"), e);
				}
			}
	}
	
	
	
	private BBBCommerceItemManager mCommerceItemManager;
	
	private ShippingGroupManager mShipManager;
	
	private PaymentGroupManager mPaymentManager;
	
	private BBBOrderManager orderManager;
	
	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return orderManager;
	}
	/**
	 * @param orderManager the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public BBBCommerceItemManager getCommerceItemManager() {
		return mCommerceItemManager;
	}
	public void setCommerceItemManager(BBBCommerceItemManager commerceItemManager) {
		this.mCommerceItemManager = commerceItemManager;
	}
	public ShippingGroupManager getShippingGroupManager() {
		return mShipManager;
	}
	public void setShippingGroupManager(ShippingGroupManager mShipManager) {
		this.mShipManager = mShipManager;
	}
	public PaymentGroupManager getPaymentGroupManager() {
		return mPaymentManager;
	}
	public void setPaymentGroupManager(PaymentGroupManager mPaymentManager) {
		this.mPaymentManager = mPaymentManager;
	}
	 
}
