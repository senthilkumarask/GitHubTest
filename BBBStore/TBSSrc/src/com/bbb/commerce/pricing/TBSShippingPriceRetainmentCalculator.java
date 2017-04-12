package com.bbb.commerce.pricing;

import java.util.Locale;
import java.util.Map;

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.order.bean.BBBShippingPriceInfo;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingCalculatorImpl;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;

public class TBSShippingPriceRetainmentCalculator extends ShippingCalculatorImpl {

    @SuppressWarnings("rawtypes")
	public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {

		String fromTBSPricingService = (String) pExtraParameters.get(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER);
		
		if (pExtraParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO) != null && fromTBSPricingService != null && fromTBSPricingService.equalsIgnoreCase("true")) {
    	vlogDebug("TBSShippingPriceRetainmentCalculator :: priceShippingGroup() :: START");
		if (isLoggingDebug()){
			logDebug("Inside priceShippingGroup");
		}
		
		if(!((TBSPricingTools)getPricingTools()).isTBSSite(SiteContextManager.getCurrentSite())){
			if(isLoggingDebug()) {
				logDebug("TBSShippingPriceRetainmentCalculator : priceItem : This is not from TBS sites, hence returning before doing actual pricing operations.");
			}
			return;
		}
		
		double shippingCharges = ((BBBShippingPriceInfo)pPriceQuote).getRawShipping();
		double surchargeCharges = ((BBBShippingPriceInfo)pPriceQuote).getSurcharge();
		double shipAmount = ((BBBShippingPriceInfo)pPriceQuote).getAmount();
		
		vlogDebug("Initial RawShipping charges calculated :: "+shippingCharges);
		vlogDebug("Initial surcharge charges calculated :: "+surchargeCharges);
		vlogDebug("Initial Ship amount calculated :: "+shipAmount);

			Map<String, BBBShippingPriceInfo> storedShippingInfo = (Map<String, BBBShippingPriceInfo> )pExtraParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO);
			if (pOrder.getShippingGroups() != null ) {
					if (pShippingGroup instanceof BBBHardGoodShippingGroup) {
						BBBHardGoodShippingGroup hrd = (BBBHardGoodShippingGroup) pShippingGroup;
						if (null != storedShippingInfo.get(hrd.getId())) {
							hrd.setPriceInfo(storedShippingInfo.get(hrd.getId()));
							BBBShippingPriceInfo storePriceInfo = storedShippingInfo.get(hrd.getId());
							((BBBShippingPriceInfo) pPriceQuote).setRawShipping(getPricingTools().round(storePriceInfo.getRawShipping()));
							((BBBShippingPriceInfo) pPriceQuote).setFinalShipping(getPricingTools().round(storePriceInfo.getFinalShipping()));
					
							((BBBShippingPriceInfo) pPriceQuote).setSurcharge(getPricingTools().round(storePriceInfo.getFinalSurcharge()));
							((BBBShippingPriceInfo) pPriceQuote).setFinalSurcharge(getPricingTools().round(storePriceInfo.getFinalSurcharge()));
					
							((BBBShippingPriceInfo) pPriceQuote).setAmount(getPricingTools().round(storePriceInfo.getFinalShipping() + storePriceInfo.getFinalSurcharge()));
							if(isLoggingDebug()){
								logDebug("TBSShippingPriceRetainmentCalculator :: Updated Shipping Prices with Values received from Pricing Service for Shipping Group ID" + hrd.getId());
							}
						}
					}
				}
			}
		
		vlogDebug("TBSShippingPriceRetainmentCalculator :: priceShippingGroup() :: END");
    }
}
