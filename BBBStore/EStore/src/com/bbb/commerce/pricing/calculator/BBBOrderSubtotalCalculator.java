package com.bbb.commerce.pricing.calculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.OrderSubtotalCalculator;
import atg.commerce.pricing.PricingException;
import atg.repository.RepositoryItem;

public class BBBOrderSubtotalCalculator extends OrderSubtotalCalculator {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceOrder(OrderPriceInfo pPriceQuote, Order pOrder,
			RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		
		
		
	    List adjustMentsList = new ArrayList();
	    double ecoFeeTotal = 0;
	    double giftWrapTotal = 0;
	    double storeSubtotal = 0.0;
	    double rawTotal = pPriceQuote.getRawSubtotal();
	    
	    double deliverySurchargeTotal = 0.0;
	    double assemblyFeeTotal = 0.0;
	    
	    List commerceItems = pOrder.getCommerceItems();
		Iterator itemIterator = commerceItems.iterator();
	    
	    adjustMentsList.addAll(pPriceQuote.getAdjustments());
	        
	    superCallPriceOrder(pPriceQuote, pOrder, pPricingModel, pLocale, pProfile,
				pExtraParameters);

		if(((BBBOrderPriceInfo) pPriceQuote).isRestoreAdjustments()) {
            ((BBBOrderPriceInfo) pPriceQuote).setRestoreAdjustments(false);
            pPriceQuote.getAdjustments().clear();
            pPriceQuote.getAdjustments().addAll(adjustMentsList);
            pPriceQuote.setRawSubtotal(getPricingTools().round(rawTotal));
            
            if (pOrder.getTaxPriceInfo() != null) {
            	pPriceQuote.setTax(getPricingTools().round(pOrder.getTaxPriceInfo().getAmount()));
            }
		} else {
		    ((BBBOrderPriceInfo) pPriceQuote).setRestoreAdjustments(true);
		}
		

		

		while (itemIterator.hasNext()) {
			CommerceItem item = (CommerceItem) itemIterator.next();

			if (item instanceof BBBCommerceItem &&
					(((BBBCommerceItem) item).getStoreId() != null
						&& !"".equals(((BBBCommerceItem) item).getStoreId()))
			) {
					storeSubtotal = storeSubtotal
							+ item.getPriceInfo().getAmount();
				
			}
			
			if (item instanceof EcoFeeCommerceItem && item.getPriceInfo() != null) {
				ecoFeeTotal += item.getPriceInfo().getAmount();
			}
			
			if (item instanceof GiftWrapCommerceItem && item.getPriceInfo() != null) {
				giftWrapTotal += item.getPriceInfo().getAmount();
			}
			
			if (item instanceof LTLDeliveryChargeCommerceItem && item.getPriceInfo() != null) {
				deliverySurchargeTotal += item.getPriceInfo().getAmount();
		}

			if (item instanceof LTLAssemblyFeeCommerceItem && item.getPriceInfo() != null) {
				assemblyFeeTotal += item.getPriceInfo().getAmount();
			}
			
		}

		((BBBOrderPriceInfo) pPriceQuote).setOnlineSubtotal(getPricingTools().round(pPriceQuote
				.getAmount() - storeSubtotal));
		((BBBOrderPriceInfo) pPriceQuote).setAmount(getPricingTools().round(pPriceQuote
                .getAmount() - storeSubtotal));
		
		((BBBOrderPriceInfo) pPriceQuote).setStoreSubtotal(getPricingTools().round(storeSubtotal));
		
		((BBBOrderPriceInfo) pPriceQuote).setEcoFee(getPricingTools().round(ecoFeeTotal));
		((BBBOrderPriceInfo) pPriceQuote).setGiftWrapSubtotal(getPricingTools().round(giftWrapTotal));

		((BBBOrderPriceInfo) pPriceQuote).setDeliverySurchargeTotal(getPricingTools().round(deliverySurchargeTotal));
		((BBBOrderPriceInfo) pPriceQuote).setAssemblyFeeTotal(getPricingTools().round(assemblyFeeTotal));
		

		if (isLoggingDebug()) {
			logDebug("BBBOrderPriceInfo : " + (BBBOrderPriceInfo) pPriceQuote);
				logDebug("Store item found, Store pickup item price : "
						+ storeSubtotal);
			}
		((BBBOrderPriceInfo) pPriceQuote).setDollarOffPromoCount(0);
		((BBBOrderPriceInfo) pPriceQuote).setAppliedPromotionThresholdMap(null);
		
		}

	protected void superCallPriceOrder(OrderPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
		super.priceOrder(pPriceQuote, pOrder, pPricingModel, pLocale, pProfile,
				pExtraParameters);
	}


}