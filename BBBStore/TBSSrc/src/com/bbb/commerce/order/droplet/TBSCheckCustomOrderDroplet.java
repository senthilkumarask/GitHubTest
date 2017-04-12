package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.order.bean.TBSCommerceItem;

/**
 * This Class checks for Kirsch or CMO items in the Order.
 */
public class TBSCheckCustomOrderDroplet extends BBBDynamoServlet {

	/**
	 * This method gets the order object from the request and loops through the
	 * commerce items to check for Kirsch and CMO.
	 * 
	 * @param pReq
	 *            the req
	 * @param pRes
	 *            the res
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {
		boolean cmo = false;
		boolean kirsch = false;
		Order order = (Order) pReq.getObjectParameter("order");
		if (order != null) {
			List<CommerceItem> commerceItems = order.getCommerceItems();
			for (CommerceItem commerceItem : commerceItems) {
				if (commerceItem instanceof TBSCommerceItem) {
					TBSCommerceItem tbsCItem = (TBSCommerceItem) commerceItem;
					if (tbsCItem.isKirsch()) {
						kirsch = true;
						logDebug("found kirsch item");
					} else if (tbsCItem.isCMO()) {
						cmo = true;
						logDebug("found CMO item");
					}
				}
			}
		}

		pReq.setParameter("cmo", cmo);
		pReq.setParameter("kirsch", kirsch);
		pReq.serviceParameter("output", pReq, pRes);
	}

}
