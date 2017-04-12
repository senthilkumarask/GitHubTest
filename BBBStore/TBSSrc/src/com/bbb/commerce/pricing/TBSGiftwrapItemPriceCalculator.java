package com.bbb.commerce.pricing;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.axis.utils.StringUtils;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Relationship;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;

public class TBSGiftwrapItemPriceCalculator extends ItemPriceCalculator{
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters) throws PricingException {

		double totalPrice = 0.0;
		List detailsList;
		List newDetails;
		List adjustments;
		double adjustAmount = 0.0;
		PricingAdjustment adjustment;
		double wrapPrice = 0.0;
		boolean registryIdFlag = false;
		List<Relationship> relationsShips = null;
		
		if (pItem instanceof GiftWrapCommerceItem) {
			BBBOrder order = null;
			Map<String, RegistrySummaryVO> regMap = null;
			if(pExtraParameters.get("pricingContext") != null){
				order = (BBBOrder)((PricingContext)pExtraParameters.get("pricingContext")).getOrder();
				regMap = order.getRegistryMap();
			}
			if(regMap != null){
				List<ShippingGroupCommerceItemRelationship> commItemShipRels = pItem.getShippingGroupRelationships();
				String GiftShipId = null;
				if(!commItemShipRels.isEmpty()){
					GiftShipId = ((ShippingGroupCommerceItemRelationship)commItemShipRels.get(0)).getShippingGroup().getId();
				}
				relationsShips = order.getRelationships();
				TBSCommerceItem commerceItem = null;
				for (Relationship relationship : relationsShips) {
					if(relationship.getRelationshipClassType().equals("shippingGroupCommerceItem") ){
						if(((ShippingGroupCommerceItemRelationship)relationship).getShippingGroup().getId().equals(GiftShipId)){
							if(((ShippingGroupCommerceItemRelationship)relationship).getCommerceItem() instanceof TBSCommerceItem){
								commerceItem = (TBSCommerceItem)((ShippingGroupCommerceItemRelationship)relationship).getCommerceItem() ;
								if (!StringUtils.isEmpty(commerceItem.getRegistryId())){
									registryIdFlag = true;
									vlogDebug("TBSGiftwrapItemPriceCalculator : ciRegId : "+commerceItem.getRegistryId());
									break;
								}
							}
						}
					}
					
				}
				
			}
			if (isLoggingDebug()) {
				logDebug("Gift wrap item is present");
			}
			Site siteConfiguration = SiteContextManager.getCurrentSite();
			if(!((TBSPricingTools)getPricingTools()).isTBSSite(siteConfiguration)){
				if(isLoggingDebug()) {
					logDebug("TBSGiftwrapItemPriceCalculator : priceItem : This is not from TBS sites, hence returning before doing actual pricing operations.");
				}
				return;
			}
			
			if( isLoggingDebug() ){ 
				logDebug("TBSItemPriceCalculator : currentSite is a TBSSite: "+SiteContextManager.getCurrentSite().getId());
			}
			
			//get the listprice for giftwrap item from the SiteConfiguration
			if (siteConfiguration.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRICE_SITE_PROPERTY_NAME) != null) {
				wrapPrice = ((Double) siteConfiguration
						.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRICE_SITE_PROPERTY_NAME))
						.doubleValue();
				String giftWrapAutoWaiveFlag=null;
		        try {
		        	giftWrapAutoWaiveFlag=((TBSPricingTools)getPricingTools()).getGiftWrapAutoWaiveFlag(SiteContextManager.getCurrentSiteId());
		        }  catch (BBBSystemException bbbSystemException) {
		        	logError("Exception occured while query networkInventory Flag"+bbbSystemException.getMessage());
				} catch (BBBBusinessException bbbBusinessException) {
					logError("Exception occured while query networkInventory Flag"+bbbBusinessException.getMessage());
				}
				if(registryIdFlag && ("true").equalsIgnoreCase(giftWrapAutoWaiveFlag)){
					wrapPrice = 0.0;
				}
				
				double qty = (double)pItem.getQuantity();
				totalPrice = wrapPrice * qty;
				totalPrice = getPricingTools().round(totalPrice);

				// set list price, raw total price and amount for item price info.
				pPriceQuote.setRawTotalPrice(totalPrice);
				pPriceQuote.setListPrice(wrapPrice);
				pPriceQuote.setAmount(totalPrice);
				

				detailsList = pPriceQuote.getCurrentPriceDetails();
				if (detailsList.size() > 0) {
					detailsList.clear();
				}
				if (isLoggingDebug()) {
					logDebug("price quote: " + pPriceQuote);
				}
				newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice, pPriceQuote, pItem,
						pPricingModel, pLocale, pProfile, pExtraParameters, TBSConstants.TBS_GIFT_WRAP_ADJUSTMENT);
				detailsList.addAll(newDetails);

				adjustments = pPriceQuote.getAdjustments();
				if (adjustments.size() > 0) {
					adjustments.clear();
				}

				adjustAmount = totalPrice;
				adjustment = new PricingAdjustment(TBSConstants.TBS_GIFT_WRAP_ADJUSTMENT, null, getPricingTools().round(adjustAmount),
						pItem.getQuantity());
				adjustments.add(adjustment);
				if (isLoggingDebug()) {
					logDebug("adjustment: " + adjustment + " adjustAmount: " + adjustAmount);
				}
			}
		}
	}
}
