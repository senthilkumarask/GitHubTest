package com.bbb.commerce.pricing;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import atg.commerce.pricing.priceLists.Constants;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemListPriceCalculator;
import atg.repository.RepositoryItem;

public class CustomCalculator extends ItemListPriceCalculator {
	
	public void priceItem(RepositoryItem pPrice, ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
            throws PricingException
            {
				
		        double totalPrice;
		        List detailsList;
		        List newDetails;
		        List adjustments;
		        double adjustAmount;
		        PricingAdjustment adjustment;
        
				if(pItem instanceof CommerceItem){
					
					
					totalPrice = 15 * (double)pItem.getQuantity();
					
					logDebug("rounding item price.");
					totalPrice = getPricingTools().round(totalPrice);
					
					pPriceQuote.setRawTotalPrice(totalPrice);
					pPriceQuote.setListPrice(15);
					pPriceQuote.setAmount(totalPrice);
					
					detailsList = pPriceQuote.getCurrentPriceDetails();
					if(detailsList.size() > 0){
						detailsList.clear();
					}
						
					newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION);
					detailsList.addAll(newDetails);
					
					adjustments = pPriceQuote.getAdjustments();
					if(adjustments.size() > 0){
						adjustments.clear();
					}
						
					adjustAmount = totalPrice;
					adjustment = new PricingAdjustment(Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION, null, getPricingTools().round(adjustAmount), pItem.getQuantity());
					adjustments.add(adjustment);
					
					
				}
            }
	
}
