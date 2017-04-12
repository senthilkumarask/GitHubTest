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

package com.bbb.commerce.pricing.droplet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.promotion.PromotionTools;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.vo.order.Shipping;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.ShippingGroupDisplayVO;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.pricing.BBBPricingManager;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;

/**
 * This class is to populate itemPriceVO and OrderPriceVO objects. It fetches
 * the price details from the itemPriceInfo object and orderPriceInfo objects.
 * 
 */
public class BBBPriceDisplayDroplet extends BBBDynamoServlet {
	
	private BBBPricingTools mPricingTools = null;
	private PromotionTools mPromotionTools;
	private BBBCatalogTools catalogTools;
	private BBBCommerceItemManager commerceItemManager;
	private BBBPricingManager pricingManager;
	
	private BBBCheckoutManager checkOutManager;
	
	/**
	 *   
	 * @return BBBCheckoutManager
	 */
	public BBBCheckoutManager getCheckOutManager() {
		return checkOutManager;
	}
	/**
	 * 
	 * @param checkOutManager
	 */
	public void setCheckOutManager(BBBCheckoutManager checkOutManager) {
		this.checkOutManager = checkOutManager;
	}
	/**
	 * @return the commerceItemManager
	 */
	public BBBCommerceItemManager getCommerceItemManager() {
		return this.commerceItemManager;
	}

	/**
	 * @param commerceItemManager the commerceItemManager to set
	 */
	public void setCommerceItemManager(BBBCommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}
	
	
	/**
	 * This method checks the input parameter type and call the getItemPriceInfo
	 * or getOrderPriceInfo methods. If there is no input parameter then it sets
	 * the null to the priceInfoVO object.
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {

		final Object priceObject = request
				.getObjectParameter(BBBCoreConstants.PARAM_PRICE_OBJECT);

		if (priceObject != null) {
			if (priceObject instanceof BBBCommerceItem) {
				final Object orderObject = request
						.getObjectParameter(BBBCoreConstants.PARAM_ORDER_OBJECT);
				if(orderObject !=null){
					// this will populate the LTL specific details as well.
					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							getCommerceItemManager().getItemPriceInfo((BBBCommerceItem) priceObject,(OrderImpl)orderObject));
					request.setParameter(BBBCoreConstants.SHIPPING_METHOD_AVL, getCommerceItemManager().isShippingMehthodAvlForCommerceItem((BBBCommerceItem) priceObject));
					request.setParameter("isSkuLtl", ((BBBCommerceItem) priceObject).isLtlItem());
					request.setParameter(BBBCoreConstants.SHIPPING_METHOD_DESC, getCommerceItemManager().getShippingMethodDesc((BBBCommerceItem) priceObject));
				}else{ 
					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							getCommerceItemManager().getItemPriceInfo((BBBCommerceItem) priceObject));
				}
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
			} else if (priceObject instanceof OrderImpl) {
				final String forItemsRawTotal = (String) request
						.getObjectParameter(BBBCoreConstants.PARAM_FOR_ITEMS_RAW_TOTAL);
				if (BBBCoreConstants.TRUE.equalsIgnoreCase(forItemsRawTotal)) {
					request.setParameter(BBBCoreConstants.PARAM_CART_ITEMS_RAW_TOTAL,
							getCartItemsRawTotal((OrderImpl) priceObject));
				} else {
					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							getPricingTools().getOrderPriceInfo((OrderImpl) priceObject));
					boolean orderContainsLTLItem=false;
					boolean orderContainsEmptySG = false;
					try {
						orderContainsLTLItem = getCheckOutManager().orderContainsLTLItem((OrderImpl) priceObject);
						orderContainsEmptySG = getCheckOutManager().orderContainsEmptySG((OrderImpl) priceObject);
					} catch (BBBSystemException e) {
						logError("Business Error while checking order has ltl item", e);
					} catch (BBBBusinessException e) {
						logError("Business Error while checking order has ltl item", e);
					}
					request.setParameter("orderHasLtl", orderContainsLTLItem);
					request.setParameter("orderContainsEmptySG", orderContainsEmptySG);
				}
				
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
			} else if (priceObject instanceof ShippingGroup) {
				String shippingMethodDescription = null;
				final String shippingMethodID = (String) request.getObjectParameter(BBBCoreConstants.SHIPPING_METHOD);
				if(StringUtils.isNotBlank(shippingMethodID) && !shippingMethodID.equalsIgnoreCase("storeShippingGroup")){
					RepositoryItem shippingMethod;
					try {
						shippingMethod = getCatalogTools().getShippingMethod(shippingMethodID);
						shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");
						
						if(StringUtils.isNotBlank(shippingMethodDescription)){
							request.setParameter(BBBCoreConstants.SHIPPING_METHOD_DESC, shippingMethodDescription);
						}
					} catch (BBBBusinessException e) {
						logError("Business Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					} catch (BBBSystemException e) {
						logError("System Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					}
				}
				
				final String commerceId = (String) request
						.getObjectParameter(BBBCoreConstants.PARAM_COMMERCE_ID_ECO_FEE);
				final Object orderObject = request
						.getObjectParameter(BBBCoreConstants.PARAM_ORDER_OBJECT);
				if (StringUtils.isNotBlank(commerceId)) {
					request.setParameter(
							BBBCoreConstants.OUTPUT_PARAM_ECO_FEE_AMOUNT,
							getEcoFeeAmount((ShippingGroup) priceObject,
									commerceId, (OrderImpl) orderObject));
					request.serviceParameter(BBBCoreConstants.OPARAM, request,
							response);
				} else {

					request.setParameter(
							BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							this.getPricingManager().getShippingPriceInfo((ShippingGroup) priceObject,
									(OrderImpl) orderObject));
					request.serviceParameter(BBBCoreConstants.OPARAM, request,
							response);
				}
			} else if (priceObject instanceof ShippingGroupCommerceItemRelationship) {
				request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
						this.getPricingManager().getShippingGroupCommerceItemPriceInfo((ShippingGroupCommerceItemRelationship) priceObject));
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
			} else if (priceObject instanceof ShippingGroupDisplayVO || priceObject instanceof Shipping) {
				// Part of R2.2 517-b | To get Shipping Method Description by passing the Method Id 
				String shippingMethodDescription = null;
				final String shippingMethodID = (String) request.getObjectParameter(BBBCoreConstants.SHIPPING_METHOD);
				if(StringUtils.isNotBlank(shippingMethodID)){
					RepositoryItem shippingMethod;
					try {
						shippingMethod = getCatalogTools().getShippingMethod(shippingMethodID);
						shippingMethodDescription = (String) shippingMethod.getPropertyValue("shipMethodDescription");
						
						if(StringUtils.isNotBlank(shippingMethodDescription)){
							request.setParameter(BBBCoreConstants.SHIPPING_METHOD_DESC, shippingMethodDescription);
						}
					} catch (BBBBusinessException e) {
						logError("Business Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					} catch (BBBSystemException e) {
						logError("System Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					}
				}
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
			} else {
				request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO, null);
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
			}
		}
	}	

	/**
	 * To get eco fee amount from the shipping group's Map.
	 * 
	 * @param shippingGroup
	 * @param commerceItem
	 * @param order
	 * @return
	 */
	@SuppressWarnings("rawtypes")
    protected double getEcoFeeAmount(final ShippingGroup shippingGroup,
			final String commerceId, final Order order) {

		double ecoFee = 0.0;
		Map ecoFeeMap = null;

		
		ecoFeeMap = ((BBBShippingGroup) shippingGroup)
					.getEcoFeeItemMap();		

		if(null == ecoFeeMap){
			return ecoFee;
		}
		
		String ecoFeeCommerceItemId = (String) ecoFeeMap.get(commerceId);

		try {
			if (StringUtils.isNotBlank(ecoFeeCommerceItemId)) {

				CommerceItem ecoFeeCI = (CommerceItem) order
						.getCommerceItem(ecoFeeCommerceItemId);
				ecoFee = ecoFeeCI.getPriceInfo().getAmount();
			}

		} catch (CommerceItemNotFoundException e) {
			
				logError(LogMessageFormatter.formatMessage(null,
						"Eco fee commerce Item Missing"), e);
			

		} catch (InvalidParameterException e) {
			
				logError(LogMessageFormatter.formatMessage(null,
						"Invalid Parameters"), e);
			
		}
		return ecoFee;
	}
	
	@SuppressWarnings("unchecked")
    protected double getCartItemsRawTotal (final OrderImpl pOrder) {
		double rawTotal = 0;
		
		List<CommerceItem> commerceItemsList= pOrder.getCommerceItems();
		
		if (commerceItemsList != null) {
			for (CommerceItem ci : commerceItemsList) {
				if (ci instanceof BBBCommerceItem && ci.getPriceInfo() != null) {
					if (ci.getPriceInfo().getSalePrice() > 0) {
						rawTotal += ci.getPriceInfo().getSalePrice() * ci.getQuantity();
					} else {
						rawTotal += ci.getPriceInfo().getListPrice() * ci.getQuantity();
					}
				}					
			}
		}
		return rawTotal;
	}	
	
	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}
	
	public PromotionTools getPromotionTools() {
		return mPromotionTools;
	}

	public void setPromotionTools(PromotionTools pPromotionTools) {
		this.mPromotionTools = pPromotionTools;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogTools pCatalogTools) {
		catalogTools = pCatalogTools;
	}

	/**
	 * @return the pricingManager
	 */
	public BBBPricingManager getPricingManager() {
		return pricingManager;
	}

	/**
	 * @param pricingManager the pricingManager to set
	 */
	public void setPricingManager(BBBPricingManager pricingManager) {
		this.pricingManager = pricingManager;
	}
}
