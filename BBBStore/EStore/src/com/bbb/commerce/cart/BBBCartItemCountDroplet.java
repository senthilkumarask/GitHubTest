//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Sunil Dandriyal
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.cart;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;

/**
 * This class is to populate itemPriceVO and OrderPriceVO objects. It fetches
 * the price details from the itemPriceInfo object and orderPriceInfo objects.
 * 
 */
public class BBBCartItemCountDroplet extends BBBDynamoServlet {

	/**
	 * This method takes ShoppingCart as input parameter type and 
	 * sets commerceItemCount 
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings("unchecked")
    public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		int giftWrapItemCount = 0;
		int ecoFeeItemCount = 0;
		long commerceItemCount = 0;
		int ltlAssemblyCommerceItem = 0;
		int ltlDeliveryChargeCommerceItemCount=0;
		
		try {
			final Order shoppingCart = (Order) pRequest.getObjectParameter(BBBCoreConstants.SHOPPING_CART);
			if (null != shoppingCart && shoppingCart.getCommerceItemCount() >= 0) {

				commerceItemCount = shoppingCart.getTotalCommerceItemCount();
				final List<CommerceItem> commerceItems = (List<CommerceItem>) shoppingCart.getCommerceItems();

				for (CommerceItem commerceItem : commerceItems) {
					if (commerceItem instanceof GiftWrapCommerceItem) {
						giftWrapItemCount += commerceItem.getQuantity();
					}

					if (commerceItem instanceof EcoFeeCommerceItem) {
						ecoFeeItemCount += commerceItem.getQuantity();
					}
					if (commerceItem instanceof LTLAssemblyFeeCommerceItem) {
						ltlAssemblyCommerceItem += commerceItem.getQuantity();
					}
					if (commerceItem instanceof LTLDeliveryChargeCommerceItem) {
						ltlDeliveryChargeCommerceItemCount += commerceItem.getQuantity();
					}
				}

				if (giftWrapItemCount > 0) {
					commerceItemCount = commerceItemCount - giftWrapItemCount;
				}

				if (ecoFeeItemCount > 0) {
					commerceItemCount = commerceItemCount - ecoFeeItemCount;
				}
				if (ltlAssemblyCommerceItem > 0) {
					commerceItemCount = commerceItemCount - ltlAssemblyCommerceItem;
				}
				if (ltlDeliveryChargeCommerceItemCount > 0) {
					commerceItemCount = commerceItemCount - ltlDeliveryChargeCommerceItemCount;
				}
			}
		} catch (Exception e) {
			logError("Exception occurred while reading cart count:", e);
		}
		pRequest.setParameter(BBBCoreConstants.COMMERCE_ITEM_COUNT,
				commerceItemCount);
		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);

	}
}