package com.bbb.commerce.pricing;

import java.util.Locale;
import java.util.Map;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingCalculatorImpl;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;

import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.order.bean.BBBShippingPriceInfo;

public class TBSShippingPriceCalculator extends ShippingCalculatorImpl {

    @SuppressWarnings("rawtypes")
	public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {

    	vlogDebug("TBSShippingPriceCalculator :: priceShippingGroup() :: START");
		if (isLoggingDebug()){
			logDebug("Inside priceShippingGroup");
		}
		
		if(!((TBSPricingTools)getPricingTools()).isTBSSite(SiteContextManager.getCurrentSite())){
			if(isLoggingDebug()) {
				logDebug("TBSShippingPriceCalculator : priceItem : This is not from TBS sites, hence returning before doing actual pricing operations.");
			}
			return;
		}
		if( isLoggingDebug() ){ 
			logDebug("TBSShippingPriceCalculator : currentSite is a TBSSite: "+SiteContextManager.getCurrentSite().getId());
		}
		
		double shippingCharges = ((BBBShippingPriceInfo)pPriceQuote).getRawShipping();
		double surchargeCharges = ((BBBShippingPriceInfo)pPriceQuote).getSurcharge();
		double shipAmount = ((BBBShippingPriceInfo)pPriceQuote).getAmount();
		
		vlogDebug("RawShipping charges :: "+shippingCharges);
		vlogDebug("surcharge charges :: "+surchargeCharges);
		vlogDebug("Ship amount :: "+shipAmount);
		if( Double.compare(shipAmount, 0.0) == BBBCoreConstants.ZERO ){
			return;
		}
				
		BBBHardGoodShippingGroup tbsShipGrp = (BBBHardGoodShippingGroup)pShippingGroup;
		TBSShippingInfo tbsShipInfo = tbsShipGrp.getTbsShipInfo();

		if( tbsShipInfo == null ) {
			if( isLoggingDebug() ) logDebug("NO TBS Ship Info");
			return;
		}
		
		// Don't change ship price if it's already 0
		if( shippingCharges > 0 ) {
			if( tbsShipInfo.isShipPriceOverride() ) {
				pPriceQuote.getAdjustments().add(new PricingAdjustment("Shipping Override", null, getPricingTools().round(shippingCharges), 1));
				pPriceQuote.setDiscounted(false);
				double shipPrice = tbsShipInfo.getShipPriceValue();
				if( shipPrice < shippingCharges ) {
					shippingCharges = shipPrice;
				}
			}
		}
		
		// Don't change surcharge price if it's already 0
		if( surchargeCharges > 0 ) {
			if( tbsShipInfo.isSurchargeOverride() ) {
				pPriceQuote.getAdjustments().add(new PricingAdjustment("Surcharge Override", null, getPricingTools().round(surchargeCharges), 1));
				pPriceQuote.setDiscounted(false);
				double surchargeValue = tbsShipInfo.getSurchargeValue();
				if( surchargeValue < surchargeCharges ) {
					surchargeCharges = surchargeValue;
				}
			}
		}

		// Don't update any pricing if it didn't change at all;
		double newTotal = shippingCharges + surchargeCharges;
		vlogDebug("Ship newTotal :: "+newTotal);
		if( newTotal < shipAmount ) {
			
			((BBBShippingPriceInfo) pPriceQuote).setRawShipping(getPricingTools().round(shippingCharges));
			((BBBShippingPriceInfo) pPriceQuote).setFinalShipping(getPricingTools().round(shippingCharges));
	
			((BBBShippingPriceInfo) pPriceQuote).setSurcharge(getPricingTools().round(surchargeCharges));
			((BBBShippingPriceInfo) pPriceQuote).setFinalSurcharge(getPricingTools().round(surchargeCharges));
	
			((BBBShippingPriceInfo) pPriceQuote).setAmount(getPricingTools().round(shippingCharges + surchargeCharges));
		}
		vlogDebug("TBSShippingPriceCalculator :: priceShippingGroup() :: END");
    }
}
