/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ShippingMethodDescriptionDroplet.java
 *
 *  DESCRIPTION: This Droplet returns Shipping method name..
 *
 *  HISTORY:
 *  Aug 31, 2012  Initial version
 */

package com.bbb.commerce.order.purchase;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * @author vagra4<br>
 *         <ul>
 *         <li>This Droplet displays Shipping method name.</li>
 *         </ul>
 *         <ul>
 *         <li>Display shipping method on Cart page along with the estimated
 *         shipping (for the first time it will always be standard).
 *         <li>Once user selected shipping method on single shipping page and
 *         return to Cart page, then display the selected shipping method.
 *         <li>If user has opted the multi-shipping page and the distinct
 *         shipping method is only one then display that method on the Cart
 *         page.
 *         <li>If user has opted the multi-shipping page and the distinct
 *         shipping method is more than one then display the static text �as
 *         previously selected� on the cart page .
 *         <li>If multiple shipping groups let say 3 shipping group and one is
 *         Store pickup and other 2 are hard good shipping group with different
 *         address and same shipping method then do we need to display shipping
 *         method name or �as previously selected�.
 *         <li>We should consider shipping method as �Store pickup� for BOPUS.
 *         If all shipping group is of type BOPUS, then display �Store pickup�.
 *         </ul>
 * 
 */
public class ShippingMethodDescriptionDroplet extends BBBDynamoServlet {

	private static final String OUTPUT = "output";

	private static final String EMPTY = "empty";

	private static final String SHIP_METHOD_DES = "shippingMethodDescription";

	private BBBCheckoutManager manager;
	private BBBShippingGroupManager shippingGroupManager;

	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		logDebug("[START] service");

		BBBOrder order = (BBBOrder) req
				.getObjectParameter(BBBCoreConstants.ORDER);
		String description = null;
		// second argument is false - as no need to check for single page functionality for mobile
		Boolean isSingleShipping = getManager().displaySingleShipping(order, false);

		try {

			description = (isSingleShipping) ? getShippingGroupManager().getSingleShippingDescription(
					req, order) : getShippingGroupManager().getMultiShippingDescription(req, order);

		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
						"SystemException"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
						"BusinessException"), e);
		}

		req.serviceLocalParameter((description == null) ? EMPTY : OUTPUT, req,
				res);
		req.setParameter(SHIP_METHOD_DES, description);

		logDebug("[END] service");
	
	}

	
	/**
	 * This method is the rest api to return the shipping description to be displayed on cart page
	 * @return the ship method description to be displayed on cart
	 * @throws BBBSystemException
	 */
	public String getDescription() throws BBBSystemException
	{

		logDebug("ShippingMethodDescriptionDroplet.getDescription() method starts");
	
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse= ServletUtil.getCurrentResponse();		
		try {
			final OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
			pRequest.setParameter(BBBCoreConstants.ORDER, cart.getCurrent());
			this.service(pRequest, pResponse);
			String description =(String) pRequest.getObjectParameter(SHIP_METHOD_DES);		
			return description;
		} catch (ServletException e) {
			 throw new BBBSystemException("err_servlet_exception_state_details", "ServletException in ShippingMethodDescriptionDroplet Droplet");
		} catch (IOException e) {
			 throw new BBBSystemException("err_io_exception_state_details", "IO Exception in in ShippingMethodDescriptionDroplet Droplet");
		} finally {
				logDebug(" ShippingMethodDescriptionDroplet.getDescription method ends");
		}
	}
	
	/**
	 * @return the shippingGroupManager
	 */
	public BBBShippingGroupManager getShippingGroupManager() {
		return shippingGroupManager;
	}

	/**
	 * @param shippingGroupManager the shippingGroupManager to set
	 */
	public void setShippingGroupManager(BBBShippingGroupManager shippingGroupManager) {
		this.shippingGroupManager = shippingGroupManager;
	}

	/**
	 * @return the manager
	 */
	public BBBCheckoutManager getManager() {
		return manager;
	}

	/**
	 * @param manager the manager to set
	 */
	public void setManager(BBBCheckoutManager manager) {
		this.manager = manager;
	}
}