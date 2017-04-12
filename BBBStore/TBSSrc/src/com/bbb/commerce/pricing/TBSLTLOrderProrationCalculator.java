package com.bbb.commerce.pricing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.BBBLTLOrderProrationCalculator;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.repository.RepositoryItem;

public class TBSLTLOrderProrationCalculator extends BBBLTLOrderProrationCalculator {

	/**
	 * This method is called to set OrderPriceInfo for current order according to the proration logic if threshold is achieved for total delivery charge.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceOrder(OrderPriceInfo pPriceQuote, Order pOrder,
			RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		
		BBBPerformanceMonitor.start("TBSLTLOrderProrationCalculator", "priceOrder");
		vlogDebug(LogMessageFormatter.formatMessage(null, "Start - TBSLTLOrderProrationCalculator.priceOrder"));
		//get all commerce items from order
		List<CommerceItem> commItems = pOrder.getCommerceItems();
		List<CommerceItem> deliveryCommItems = new ArrayList<CommerceItem>();
		double totalRawDeliveryChargeAmount = 0.0;
		// to be replaced by config value.
		String thresholdAmountString = null;
		try {
			thresholdAmountString = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT).get(0);
		} catch (BBBSystemException e) {
			vlogError("Error getting thresholdAmount from config keys", e);
		} catch (BBBBusinessException e) {
			vlogError("Error getting thresholdAmount from config keys", e);
		}
		double thresholdAmount = Double.parseDouble(thresholdAmountString);
		double discountTotal = 0.0;
		double discountAmountToApply = 0.0;
		double discountedPrice = 0.0;
		int deliveryItemCount = 0;
		//iterate on commerce items and populate deliveryCommItems if commerce item is instance of LTLDeliveryChargeCommerceItem
		for(CommerceItem commItem : commItems){
			if (commItem instanceof LTLDeliveryChargeCommerceItem){
				deliveryCommItems.add(commItem);
				totalRawDeliveryChargeAmount += commItem.getPriceInfo().getRawTotalPrice();
			}
		}
		//sort all deliveryCommItems
		deliveryCommItems = sortList(deliveryCommItems);
		((BBBOrderPriceInfo) pPriceQuote).setDeliverySurchargeRawTotal(totalRawDeliveryChargeAmount);
		discountAmountToApply = totalRawDeliveryChargeAmount - thresholdAmount;
		int deliveryCommItemsSize = deliveryCommItems.size(); 
		//check if totalRawDeliveryChargeAmount is greater then thresholdAmount than prorate delivery charge amount.
		if(totalRawDeliveryChargeAmount > thresholdAmount){
			LTLDeliveryChargeCommerceItem deliveryItem = null;
			for(CommerceItem commItem : deliveryCommItems){
				if (commItem instanceof LTLDeliveryChargeCommerceItem){
					deliveryItem = (LTLDeliveryChargeCommerceItem) commItem;
					
					if(deliveryItem.getTBSItemInfo() != null){
						double finalAmount = 0.0;
						if(deliveryItem.getTBSItemInfo() != null){
							double overridePrice = deliveryItem.getTBSItemInfo().getOverridePrice();
							int overrideQty = deliveryItem.getTBSItemInfo().getOverrideQuantity();
							finalAmount = overridePrice * overrideQty;
						}
						commItem.getPriceInfo().setAmount(finalAmount);
					} else if(deliveryItem.getTBSItemInfo() == null){
						//discountTotal is the total amount that has been discounted on items one by one.
						//if discountTotal is less then discountAmountToApply the execute below code, else break.
						if(discountTotal < discountAmountToApply){
							double itemRatio = (commItem.getPriceInfo().getRawTotalPrice())/totalRawDeliveryChargeAmount;
							double finalDiscountForItem =   getPricingTools().round(itemRatio * discountAmountToApply);
							
							//if discountTotal exceeds discountAmountToApply due to rounding off apply remaining amount to be discounted to current item and leave the remaining items.
							if((discountTotal + finalDiscountForItem) > discountAmountToApply){
								finalDiscountForItem = discountAmountToApply - discountTotal;
								discountedPrice = getPricingTools().round(commItem.getPriceInfo().getRawTotalPrice() - (finalDiscountForItem));
								commItem.getPriceInfo().setAmount(discountedPrice);
							}else{
								if(deliveryItemCount == deliveryCommItemsSize-1){
									finalDiscountForItem = discountAmountToApply - discountTotal;
								}
								//if discountTotal does not exceed discountAmountToApply then apply discount to current item.
								discountTotal += finalDiscountForItem;
								discountedPrice = getPricingTools().round(commItem.getPriceInfo().getRawTotalPrice() - (finalDiscountForItem));
								commItem.getPriceInfo().setAmount(discountedPrice);
								deliveryItemCount++;
							}
						}
						
					} else {
						break;
					}
					
				}
			}
		}
		
		BBBPerformanceMonitor.end("BBBLTLOrderProrationCalculator", "priceOrder");
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End - BBBLTLOrderProrationCalculator.priceOrder"));
		}
	}
}
