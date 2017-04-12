package com.bbb.commerce.pricing.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.vo.order.Shipping;
import com.bbb.commerce.common.ShippingGroupDisplayVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;

public class TBSPriceDisplayDroplet extends BBBPriceDisplayDroplet {

	public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) throws ServletException, IOException {

		Object priceObject = request.getObjectParameter(BBBCoreConstants.PARAM_PRICE_OBJECT);

		if (priceObject != null) {
			if (priceObject instanceof BBBCommerceItem) {
				final Object orderObject = request.getObjectParameter(BBBCoreConstants.PARAM_ORDER_OBJECT);
				if(orderObject !=null){
					// this will populate the LTL specific details as well.
					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							getCommerceItemManager().getItemPriceInfo((BBBCommerceItem) priceObject,(OrderImpl)orderObject));
					
					request.setParameter(BBBCoreConstants.SHIPPING_METHOD_AVL, getCommerceItemManager().isShippingMehthodAvlForCommerceItem((BBBCommerceItem) priceObject));
					
					request.setParameter(BBBCoreConstants.IS_SKU_LTL, ((BBBCommerceItem) priceObject).isLtlItem());
					request.setParameter(BBBCoreConstants.SHIPPING_METHOD_DESC, getCommerceItemManager().getShippingMethodDesc((BBBCommerceItem) priceObject));
				}else{ 
					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							getCommerceItemManager().getItemPriceInfo((BBBCommerceItem) priceObject));
					
				}
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
				
				try {
					double clearancePrice = getCatalogTools().getSalePrice(((BBBCommerceItem) priceObject).getAuxiliaryData().getProductId(), 
																			((BBBCommerceItem) priceObject).getCatalogRefId());
					request.setParameter(BBBCoreConstants.CLEARANCE_PRICE, clearancePrice);
				}
				catch( Exception ex ) {
					logError("Error fetching clearance price");
				}
				
			} else if (priceObject instanceof OrderImpl) {
				String forItemsRawTotal = (String) request.getObjectParameter(BBBCoreConstants.PARAM_FOR_ITEMS_RAW_TOTAL);
				
				if (BBBCoreConstants.TRUE.equalsIgnoreCase(forItemsRawTotal)) {
					request.setParameter(BBBCoreConstants.PARAM_CART_ITEMS_RAW_TOTAL, getCartItemsRawTotal((OrderImpl) priceObject));
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
					request.setParameter(BBBCoreConstants.ORDER_HAS_LTL, orderContainsLTLItem);
					request.setParameter(BBBCoreConstants.ORDER_CONTAINS_EMPTY_SG, orderContainsEmptySG);
				}
				
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
			} else if (priceObject instanceof ShippingGroup) {
				
				String shippingMethodDescription = null;
				String shippingMethodID = (String) request.getObjectParameter(BBBCoreConstants.SHIPPING_METHOD);
				
				if(StringUtils.isBlank(shippingMethodID)){
					shippingMethodID = ((ShippingGroup)priceObject).getShippingMethod();
				}
				RepositoryItem shippingMethod = null;
				if(StringUtils.isNotBlank(shippingMethodID) && !shippingMethodID.equalsIgnoreCase(BBBCoreConstants.STORE_SHIPPING_GROUP)){
					try {
						shippingMethod = getCatalogTools().getShippingMethod(shippingMethodID);
					} catch (BBBBusinessException e) {
						logError("Business Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					} catch (BBBSystemException e) {
						logError("System Error while retrieving shipping method for [" + shippingMethodID + "]", e);
					}
				}
				
				String commerceId = (String) request.getObjectParameter(BBBCoreConstants.PARAM_COMMERCE_ID_ECO_FEE);
				Object orderObject = request.getObjectParameter(BBBCoreConstants.PARAM_ORDER_OBJECT);
				if (StringUtils.isNotBlank(commerceId)) {
					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_ECO_FEE_AMOUNT, 
							getEcoFeeAmount((ShippingGroup) priceObject, commerceId, (OrderImpl) orderObject));
					
				} else {

					request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
							getPricingManager().getShippingPriceInfo((ShippingGroup) priceObject, (OrderImpl) orderObject));
				}
				if(shippingMethod != null){
					shippingMethodDescription = (String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION);
				}
				
				if(StringUtils.isNotBlank(shippingMethodDescription)){
					request.setParameter(BBBCoreConstants.SHIPPING_METHOD_DESC, shippingMethodDescription);
				}
				request.serviceLocalParameter(BBBCoreConstants.OPARAM, request, response);
				
			} else if (priceObject instanceof ShippingGroupCommerceItemRelationship) {
				request.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
						getPricingManager().getShippingGroupCommerceItemPriceInfo((ShippingGroupCommerceItemRelationship) priceObject));
				
				request.serviceParameter(BBBCoreConstants.OPARAM, request, response);
				
			} else if (priceObject instanceof ShippingGroupDisplayVO || priceObject instanceof Shipping) {
				// Part of R2.2 517-b | To get Shipping Method Description by passing the Method Id 
				String shippingMethodDescription = null;
				String shippingMethodID = (String) request.getObjectParameter(BBBCoreConstants.SHIPPING_METHOD);
				if(StringUtils.isNotBlank(shippingMethodID)){
					RepositoryItem shippingMethod;
					try {
						shippingMethod = getCatalogTools().getShippingMethod(shippingMethodID);
						shippingMethodDescription = (String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION);
						
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
}
