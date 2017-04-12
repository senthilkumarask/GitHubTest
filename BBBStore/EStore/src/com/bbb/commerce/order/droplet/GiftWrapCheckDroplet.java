//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Sunil Dandriyal
//
//Created on: 02-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupManager;
import atg.core.util.Address;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.NonMerchandiseCommerceItem;

/**
 * This Droplet performs checks for any gift wrap eligible commerce Item in the
 * passed Shipping group.
 * 
 */
public class GiftWrapCheckDroplet extends BBBDynamoServlet {

	/**
	 * instance of BBBCatalogTools
	 */
	private BBBCatalogTools mBBBCatalogTools;

	/**
	 * instance of ShippingGroupManager
	 */
	private ShippingGroupManager mShippingGroupManager;

	/**
	 * This method performs checks for any gift wrap eligible commerce Item in
	 * the passed Shipping group.
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
	    BBBPerformanceMonitor.start("GiftWrapCheckDroplet", "service");

		logDebug("Starting method GiftWrapCheckDroplet.service");
		
		// multi shipping groups - Gift Wrap
		final String giftWrapOption = (String) pRequest
		.getObjectParameter(BBBCheckoutConstants.GIFT_WRAP_OPTION);
		
		final String siteId = (String) pRequest.getObjectParameter(BBBCheckoutConstants.SITEID);
		String onlyGiftWrapProductDetails= (String) pRequest.getObjectParameter(BBBCheckoutConstants.ONLY_GIFT_WRAP_PRODUCT_DETAILS);
		
		if( null != onlyGiftWrapProductDetails && onlyGiftWrapProductDetails.equalsIgnoreCase(BBBCoreConstants.TRUE)){
			GiftWrapVO giftWrapVO = null;
			double giftWrapPrice = 0.0;
			try {
				giftWrapVO = getBBBCatalogTools().getWrapSkuDetails(siteId);
				if (giftWrapVO != null) {
					giftWrapPrice = giftWrapVO.getWrapSkuPrice();
				}
			} catch (BBBBusinessException bbbBusExc) {
				logError("Error with fetching gift wrap product details for site id :" + siteId, bbbBusExc);
			} catch (BBBSystemException bbbSysExc) {
				logError("Error with fetching gift wrap product details for site id :" + siteId, bbbSysExc);
			}
			pRequest.setParameter(BBBCheckoutConstants.GIFT_WRAP_PRICE, giftWrapPrice);
			pRequest.serviceParameter(BBBCheckoutConstants.OUTPUT, pRequest, pResponse);
			
		} else if(StringUtils.equalsIgnoreCase(giftWrapOption, BBBCheckoutConstants.MULTI_GIFT_OPTION)){
			
		        
		   try {
				processMultiGiftOption(pRequest,pResponse);
			} catch (BBBSystemException bbbSysExc) {
				
					logError("Multi gift option failed", bbbSysExc);
			

			} catch (BBBBusinessException bbbBusExc) {
			    logError("Multi gift option failed", bbbBusExc);
			}
		        		        
		}else{
			// Single shipping group gift wrap check
			BBBHardGoodShippingGroup shippingGroup = null;
			if (pRequest.getObjectParameter(BBBCheckoutConstants.SHIPPING_GROUP) instanceof BBBHardGoodShippingGroup) {
				shippingGroup = (BBBHardGoodShippingGroup) pRequest
				.getObjectParameter(BBBCheckoutConstants.SHIPPING_GROUP);
			}
			
			logDebug("Passed parameters tp GiftWrapCheckDroplet.service --> "
						+ "order: " + shippingGroup + ", siteId:" + siteId);
			
			if (shippingGroup instanceof BBBHardGoodShippingGroup && siteId != null) {
				
				List<CommerceItemRelationship> commerceItemRelationshipList = shippingGroup
				.getCommerceItemRelationships();
				
				boolean giftWrapItemFound = false;
				double giftWrapPrice = 0.0;
				StringBuffer giftWrapNotEligibleSkus = new StringBuffer();
				int index = 0;
				for (CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
					index++;
					GiftWrapVO giftWrapVO = null;
					SKUDetailVO skuVO = null;
					try {
						if (commerceItemRelationship != null) {
							CommerceItem ci = commerceItemRelationship.getCommerceItem();
							if (ci instanceof NonMerchandiseCommerceItem) {
								continue;
							}
								boolean IsGiftWrap = getBBBCatalogTools()
								.isGiftWrapItem(
										siteId,
										commerceItemRelationship
										.getCommerceItem()
										.getCatalogRefId());
								
								giftWrapVO = getBBBCatalogTools().getWrapSkuDetails(
										siteId);
								
								
								if (IsGiftWrap && giftWrapVO != null) {
									giftWrapItemFound = true;
									giftWrapPrice = giftWrapVO.getWrapSkuPrice();
									
									logDebug("IsGiftWrap : " + IsGiftWrap);
									logDebug("giftWrapPrice : " + giftWrapPrice);
																		
								}	else {
									skuVO = getBBBCatalogTools().getSKUDetails(siteId, commerceItemRelationship
																		.getCommerceItem()
																		.getCatalogRefId(), false, true, true);
									if(skuVO != null){
										giftWrapNotEligibleSkus.append(skuVO.getDisplayName());
										if(index < commerceItemRelationshipList.size()){
											giftWrapNotEligibleSkus.append(BBBCoreConstants.COMMA);
										}
									}
								}
							
						} else {
							pRequest.serviceParameter(BBBCheckoutConstants.EMPTY,
									pRequest, pResponse);
							
							logDebug("commerceItemRelationship : "
										+ commerceItemRelationship);
							
						}
					} catch (BBBSystemException bbbSysExc) {
						
							logError("Error with gift messaging", bbbSysExc);
						
						
					} catch (BBBBusinessException bbbBusExc) {
						
							logError("Error with gift messaging", bbbBusExc);
						
					}
				}
				
				
				pRequest.setParameter(
						BBBCheckoutConstants.GIFT_WRAP_FLAG, giftWrapItemFound);
				pRequest.serviceParameter(BBBCheckoutConstants.OUTPUT,
						pRequest, pResponse);
				pRequest.setParameter(
						BBBCheckoutConstants.GIFT_WRAP_PRICE,
						giftWrapPrice);
				pRequest.setParameter(
						BBBCheckoutConstants.SINGLE_NON_WRAP_SKUS,
						giftWrapNotEligibleSkus.toString());
			} else {
				pRequest.serviceParameter(BBBCheckoutConstants.EMPTY, pRequest,
						pResponse);
			}
		}
		
		logDebug("Exiting method GiftWrapCheckDroplet.service");
		
		BBBPerformanceMonitor.end("GiftWrapCheckDroplet", "service");

	}

	/**
	 * This method process the multi gift option request.
	 * 
	 * @param pRequest
	 *            request
	 * @param pResponse
	 *            response
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void processMultiGiftOption(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws BBBSystemException,
			BBBBusinessException, ServletException, IOException {

		Order order = (Order) pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		HardgoodShippingGroup shipGroup = null;

		final String siteId = (String) pRequest
				.getObjectParameter(BBBCheckoutConstants.SITEID);

		if (null == order || order.getCommerceItemCount() == 0
				|| order.getShippingGroups().isEmpty()) {

			pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest,
					pResponse);
		} else {

			GiftWrapVO giftWrapVO = getBBBCatalogTools().getWrapSkuDetails(
					siteId);

			for (Object object : order.getShippingGroups()) {

				HashMap<String, String> giftWrapMap = new HashMap<String, String>();
				boolean groupRequired = false;
				String itemQuantity = null;
				List<CommerceItem> commItemList = new ArrayList<CommerceItem>();

				if (object instanceof HardgoodShippingGroup) {
					StringBuffer nonGiftWrapSkus = new StringBuffer();
					
					shipGroup = (BBBHardGoodShippingGroup) object;

					List<CommerceItemRelationship> commerceItemRelationshipList = shipGroup
							.getCommerceItemRelationships();

					for (CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {

						CommerceItem commerceItem = commerceItemRelationship
								.getCommerceItem();
						
						if (null != commerceItem
								&& !(commerceItem instanceof NonMerchandiseCommerceItem)) {
							itemQuantity = String
									.valueOf(commerceItemRelationship
											.getQuantity());
							commItemList.add(commerceItem);

							Boolean isGiftWrap = getBBBCatalogTools()
									.isGiftWrapItem(
											siteId,
											commerceItemRelationship
													.getCommerceItem()
													.getCatalogRefId());

							giftWrapMap.put(commerceItem.getId(),
									itemQuantity);

							if (isGiftWrap) {

								groupRequired = true;

							} else {
								SKUDetailVO skuVO = getBBBCatalogTools().getSKUDetails(siteId, commerceItem.getCatalogRefId(), false, true, true);
								if(skuVO != null){
									
									if (nonGiftWrapSkus.length() == 0) {
										nonGiftWrapSkus
												.append(skuVO.getDisplayName());
									} else {
										nonGiftWrapSkus
												.append(BBBCoreConstants.COMMA);
										nonGiftWrapSkus
										.append(skuVO.getDisplayName());
									}						
									
								}
							}
						}

					}

					if (groupRequired && null != giftWrapVO) {

						pRequest.setParameter(
								BBBCheckoutConstants.GIFT_WRAP_PRICE,
								giftWrapVO.getWrapSkuPrice());

					}
					
					String shippingMethodDescription = null;
					if(StringUtils.isNotBlank(shipGroup.getShippingMethod())){
						RepositoryItem shippingMethod;
						try {
							shippingMethod = getBBBCatalogTools().getShippingMethod(shipGroup.getShippingMethod());
							shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");
							
							if(StringUtils.isNotBlank(shippingMethodDescription)){
								pRequest.setParameter(BBBCoreConstants.SHIPPING_METHOD_DESC, shippingMethodDescription);
							}
						} catch (BBBBusinessException e) {
							logError("Business Error while retrieving shipping method for [" + shipGroup.getShippingMethod() + "]", e);
						} catch (BBBSystemException e) {
							logError("System Error while retrieving shipping method for [" + shipGroup.getShippingMethod() + "]", e);
						}
					}
					
					Address shipAddress = shipGroup.getShippingAddress();
					String shippingMethod = shipGroup.getShippingMethod();

					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_SHIP_IND,
							((BBBHardGoodShippingGroup) shipGroup)
									.getGiftWrapInd());
					pRequest.setParameter(
							BBBCheckoutConstants.GIFT_ITEM_IND,
							((BBBHardGoodShippingGroup) shipGroup)
									.containsGiftWrap());
					pRequest.setParameter(BBBCheckoutConstants.GIFT_WRAP_FLAG,
							groupRequired);
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_SHIP_PARAM,
							shipGroup);
					pRequest.setParameter(BBBCheckoutConstants.MULTI_GIFT_ADDR,
							shipAddress);
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_METHOD,
							shippingMethod);
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_OPT_MAP,
							giftWrapMap);
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_ITEM_LIST,
							commItemList);
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_SHIP_ID,
							shipGroup.getId());
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_GIFT_SHIP_MSG,
							((BBBHardGoodShippingGroup) shipGroup)
									.getGiftWrapMessage());
					pRequest.setParameter(
							BBBCheckoutConstants.MULTI_NON_WRAP_SKUS,
							nonGiftWrapSkus);
					pRequest.serviceParameter(BBBCoreConstants.OPARAM,
							pRequest, pResponse);

				}

			}

		}

	}

	/**
	 * @return BBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}

	/**
	 * @param pBBBCatalogTools
	 */
	public void setBBBCatalogTools(final BBBCatalogTools pBBBCatalogTools) {
		mBBBCatalogTools = pBBBCatalogTools;
	}

	/**
	 * @return the mShippingGroupManager
	 */
	public ShippingGroupManager getShippingGroupManager() {
		return mShippingGroupManager;
	}

	/**
	 * @param pShippingGroupManager
	 *            the mShippingGroupManager to set
	 */
	public void setShippingGroupManager(
			ShippingGroupManager pShippingGroupManager) {
		mShippingGroupManager = pShippingGroupManager;
	}

}