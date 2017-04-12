//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Madhur Aggarwal
//
//Created on: 16-September-2013
//--------------------------------------------------------------------------------

package com.bbb.commerce.cart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.framework.performance.BBBPerformanceMonitor;

import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;

/**
 * This class is used to count the total commerce items in the cart and the 
 * total registry items from a single registry. Also it checks whether the 
 * user has selected the shipping address as default on shipping page.
 * 
 */
public class BBBItemCountDroplet extends BBBDynamoServlet {

	private static final String REG_ADDRESSES = "registriesAddresses";
	private static final String SELECTED_ADDRESS = "selectedAddress";
	private static final String REG_ITEM_COUNT = "registryItemCount";
	private static final String ISDEFAULT_SHIP_ADDR = "isDefaultShippAddr";

	private GiftRegistryManager mRegistryManager;

	/**
	 * @return mRegistryManager
	 */
	public GiftRegistryManager getRegistryManager() {
		return this.mRegistryManager;
	}

	/**
	 * @param registryManager
	 */
	public void setRegistryManager(GiftRegistryManager registryManager) {
		this.mRegistryManager = registryManager;
	}
	
	/**
	 * This method takes ShoppingCart as input parameter type and 
	 * sets commerceItemCount, registryItemCount and isDefaultShippAddr
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		BBBPerformanceMonitor.start("BBBItemCountDroplet", "service");
		logDebug("starting - service method BBBItemCountDroplet");


		int giftWrapItemCount = 0;
		int ecoFeeItemCount = 0;
		long commerceItemCount = 0;
		int totalRegistryItems = 0;
		final Order shoppingCart = (Order) pRequest
				.getObjectParameter(BBBCoreConstants.SHOPPING_CART);
		
		if (null != shoppingCart
				&& shoppingCart.getCommerceItemCount() >= 0) {

			commerceItemCount = shoppingCart.getTotalCommerceItemCount();
			final List<CommerceItem> commerceItems = shoppingCart
					.getCommerceItems();

			for (CommerceItem commerceItem:commerceItems) {
				if (commerceItem instanceof GiftWrapCommerceItem) {
					giftWrapItemCount += commerceItem.getQuantity();
				}
				if (commerceItem instanceof EcoFeeCommerceItem) {
					ecoFeeItemCount += commerceItem.getQuantity();
				}
			}

			if (giftWrapItemCount > 0) {
				commerceItemCount = commerceItemCount - giftWrapItemCount;
			}
			if (ecoFeeItemCount > 0) {
				commerceItemCount = commerceItemCount - ecoFeeItemCount;
			}

			Map<String, RegistrySummaryVO> registryMap;
			BBBOrderImpl order = (BBBOrderImpl) pRequest.getObjectParameter(BBBCoreConstants.SHOPPING_CART);
			registryMap = order.getRegistryMap();

			if(registryMap == null) {
				registryMap = new HashMap<String, RegistrySummaryVO>();
			}

			List<CommerceItem> items = order.getCommerceItems();
			Iterator<CommerceItem> itr = items.iterator();

			while(itr.hasNext()){
				Object currentItem = itr.next();
				if(currentItem instanceof BBBCommerceItem) {
					String registryId = ((BBBCommerceItem) currentItem).getRegistryId();
					if(!(StringUtils.isEmpty(registryId)) && (registryMap.containsKey(registryId))) {
						totalRegistryItems += (int) ((BBBCommerceItem) currentItem).getQuantity();
					}
				}
			}
		}

		String registryAddressId = "";
		boolean isDefaultShippingAddress = false;
		String selectedAddress = pRequest.getParameter(SELECTED_ADDRESS);

		List<BBBAddress> registriesAddresses = (List<BBBAddress>)pRequest.getObjectParameter(REG_ADDRESSES);
		if(registriesAddresses != null && !registriesAddresses.isEmpty())
			registryAddressId = registriesAddresses.get(0).getIdentifier();

		if(registryAddressId.equalsIgnoreCase(selectedAddress)){
			isDefaultShippingAddress = true;
		}

		pRequest.setParameter(BBBCoreConstants.COMMERCE_ITEM_COUNT,
				Long.valueOf(commerceItemCount));
		pRequest.setParameter(REG_ITEM_COUNT, Integer.valueOf(totalRegistryItems));
		pRequest.setParameter(ISDEFAULT_SHIP_ADDR, Boolean.valueOf(isDefaultShippingAddress));
		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);

		logDebug("Exiting - service method BBBItemCountDroplet");		
		BBBPerformanceMonitor.end("BBBItemCountDroplet", "service");
		
	}
}
