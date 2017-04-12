package com.bbb.commerce.pricing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.Constants;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemSalePriceCalculator;
import atg.core.util.Range;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;

public class TBSItemPriceCalculator extends ItemSalePriceCalculator {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
            throws PricingException
    {
		if( isLoggingDebug() ){ 
			logDebug("TBSItemPriceCalculator :: Start");
		}
		
		if(!((TBSPricingTools)getPricingTools()).isTBSSite(SiteContextManager.getCurrentSite())){
			if(isLoggingDebug()) {
				logDebug("TBSItemPriceCalculator : priceItem : This is not from TBS sites, hence returning before doing actual pricing operations.");
			}
			return;
		}
		if( isLoggingDebug() ){ 
			logDebug("TBSItemPriceCalculator : currentSite is a TBSSite: "+SiteContextManager.getCurrentSite().getId());
		}
		
    	// First, make sure we have a TBSCommerce item, if not, do nothing.
    	if( pItem instanceof TBSCommerceItem ) {
    		if( isLoggingDebug() ){
    			logDebug("It's a TBS Item");
    		}
    		TBSCommerceItem tbsItem = (TBSCommerceItem)pItem;
    		
    		 Set<RepositoryItem> skuAttrRelation = null;
    		 RepositoryItem skuAttribute = null;
    		 String skuAttrId = null;
    		// 
    		// Check for price override and use that value to price the item
    		TBSItemInfo itemInfo = tbsItem.getTBSItemInfo();
    		if( itemInfo == null ) {
    			return;
    		}
    		
    		RepositoryItem skuItem = (RepositoryItem) tbsItem.getAuxiliaryData().getCatalogRef();
			if(skuItem != null){
				skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
			}
			if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
				vlogDebug("skuAttrRelation :: "+skuAttrRelation );
				for (RepositoryItem skuAttrReln : skuAttrRelation) {
					skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
					if(skuAttribute != null){
						skuAttrId = skuAttribute.getRepositoryId();
					}
					if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
						vlogDebug("Calculating for Kirsch or CMO");
						vlogDebug("Price :: "+itemInfo.getRetailPrice());
						if(itemInfo.getRetailPrice() > 0.0){
							priceItem(itemInfo.getRetailPrice(), pItem.getQuantity(), TBSConstants.TBS_CUSTOM_PRICE, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, false);
						}
					}
				}
			}
    			
			if(itemInfo.isPriceOveride()) {
				vlogDebug("Overriding price!");
				vlogDebug("Price override :: "+itemInfo.getOverridePrice());
				vlogDebug("Quantity to override: " + itemInfo.getOverrideQuantity());
				priceItem(itemInfo.getOverridePrice(), itemInfo.getOverrideQuantity(), TBSConstants.PRICE_OVERRIDE_DESC, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, true);
			}
			return;
    	}
    	
    	if( pItem instanceof LTLDeliveryChargeCommerceItem ) {
    		vlogDebug("It's a non-merch Item :: LTLDeliveryChargeCommerceItem");
    		LTLDeliveryChargeCommerceItem ci = (LTLDeliveryChargeCommerceItem)pItem;
    		
    		// Check for price override and use that value to price the item
    		TBSItemInfo itemInfo = ci.getTBSItemInfo();
    		if( itemInfo == null ) {
    			return;
        	}
    		if( itemInfo.isPriceOveride() ) {  
    			vlogDebug("Price override :: "+itemInfo.getOverridePrice());
    			priceItem(itemInfo.getOverridePrice(), 1, TBSConstants.PRICE_OVERRIDE_DESC, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, true);
        	}
    	}
    	
    	if( pItem instanceof LTLAssemblyFeeCommerceItem ) {
    		vlogDebug("It's a non-merch Item :: LTLAssemblyFeeCommerceItem");
    		LTLAssemblyFeeCommerceItem ci = (LTLAssemblyFeeCommerceItem)pItem;
    		
    		// Check for price override and use that value to price the item
    		TBSItemInfo itemInfo = ci.getTBSItemInfo();
    		if( itemInfo == null ) {    			
    			return;
    		}
    		if( itemInfo.isPriceOveride() ) {
    			vlogDebug("Price override :: "+itemInfo.getOverridePrice());
    			priceItem(itemInfo.getOverridePrice(), 1, TBSConstants.PRICE_OVERRIDE_DESC, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, true);
        	}
    	}
    	/* Added for giftwrap price override -- starts */
    	if( pItem instanceof GiftWrapCommerceItem ) {
    		vlogDebug("It's a non-merch Item :: GiftWrapCommerceItem");
    		GiftWrapCommerceItem ci = (GiftWrapCommerceItem)pItem;
    		
    		// Check for price override and use that value to price the item
    		TBSItemInfo itemInfo = ci.getTBSItemInfo();
    		if( itemInfo == null ) {    			
    			return;
    		}
    		if( itemInfo.isPriceOveride() ) {
    			vlogDebug("Price override :: "+itemInfo.getOverridePrice());
    			priceItem(itemInfo.getOverridePrice(), 1, TBSConstants.PRICE_OVERRIDE_DESC, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, true);
        	}
    	}
    	/* Added for giftwrap price override -- Ends */
    }

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
    protected void priceItem(double pPrice, long quantity, String adjustDesc, ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, 
            Map pExtraParameters, boolean pOnSale) throws PricingException {
		
		vlogDebug("Price :: "+pPrice);
        String perfName = "priceItem";
        PerformanceMonitor.startOperation("TBSItemSalePriceCalculator", perfName);
        boolean perfCancelled = false;
        DetailedItemPriceInfo detailedItemPriceInfo = null;
		DetailedItemPriceInfo tobeAdjusted = null;
		double totalPrice = 0.0D;
		long totalDiscounted = 0L;
        try {
        	double origPrice = pPriceQuote.getListPrice();
            double oldAmount = pPriceQuote.getAmount();
            //this is used only for calculating the unit saved amount.
            //don't set the salePrice flag as it will cause issue while order promotions
            if(pOnSale){
            	pPriceQuote.setSalePrice(pPrice);
            } else {
            	pPriceQuote.setListPrice(pPrice);
            	totalPrice = pPrice * quantity;
            	
            	totalPrice = getPricingTools().round(totalPrice);
            	pPriceQuote.setRawTotalPrice(totalPrice);
            	pPriceQuote.setAmount(totalPrice);

                List detailsList = pPriceQuote.getCurrentPriceDetails();

                if (detailsList == null) {
                  detailsList = new ArrayList(1);
                }

                if (detailsList.size() > 0) {
                  detailsList.clear();
                }

                List newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION);

                detailsList.addAll(newDetails);

                List adjustments = pPriceQuote.getAdjustments();

                if (adjustments.size() > 0) {
                  adjustments.clear();
                }

                double adjustAmount = totalPrice;
                PricingAdjustment adjustment = new PricingAdjustment(adjustDesc, null, getPricingTools().round(adjustAmount), pItem.getQuantity());

                adjustments.add(adjustment);
                return;
            }
            
            double newAmount = (double)(quantity * pPrice);
            newAmount += (pItem.getQuantity() - quantity) * origPrice;
            vlogDebug((new StringBuilder()).append("rounding item sale price: ").append(newAmount).toString());
            newAmount = getPricingTools().round(newAmount);
            
            vlogDebug((new StringBuilder()).append("rounded item sale price to: ").append(newAmount).toString());
            pPriceQuote.setAmount(newAmount);
            double itemAdjustAmount = pPriceQuote.getAmount() - oldAmount;
            
            //creating adjustment
            PricingAdjustment itemAdjustment = new PricingAdjustment(adjustDesc, pPricingModel, getPricingTools().round(itemAdjustAmount), quantity);
            pPriceQuote.getAdjustments().add(itemAdjustment);
            pPriceQuote.setOnSale(pOnSale);
            assignDetailPrice(pPriceQuote, pItem, tobeAdjusted);
        }
        finally
        {
            try
            {
                if(!perfCancelled)
                {
                    PerformanceMonitor.endOperation("ItemSalePriceCalculator", perfName);
                    perfCancelled = true;
                }
            }
            catch(PerfStackMismatchException e)
            {
                if(isLoggingWarning())
                    logWarning(e);
            }
        }
    }
	

	/**
	 * This method is used to created the detailed price adjustment based on the overridden price and quantity.
	 * @param pPriceQuote
	 * @param pItem
	 * @param tobeAdjusted
	 * @throws PricingException
	 */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private void assignDetailPrice(ItemPriceInfo pPriceQuote, CommerceItem pItem, DetailedItemPriceInfo tobeAdjusted) throws PricingException {
		
		vlogDebug("TBSItemPriceCalculator :: assignDetailPrice() method :: START");
		DetailedItemPriceInfo detailedItemPriceInfo;
		List detailsList = pPriceQuote.getCurrentPriceDetails();
		ListIterator detailsIterator = detailsList.listIterator();
		if(detailsList == null)
		    throw new PricingException(MessageFormat.format(Constants.ITEM_NOT_LIST_PRICED, new Object[] {
		        pItem.getId()
		    }));
		//getPricingTools().getDetailedItemPriceTools().assignSalePriceToDetails(detailsList, pPrice, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters, Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION);
		for(int z=0; z < detailsList.size(); z++) {
		    detailedItemPriceInfo = (DetailedItemPriceInfo)detailsList.get(z);
		    
		    if(detailedItemPriceInfo.getAdjustments().size()==1){
		    	tobeAdjusted = detailedItemPriceInfo;
		        break;
		    } else {
		    	List<PricingAdjustment> adjustments = detailedItemPriceInfo.getAdjustments();
		    	for (PricingAdjustment priceAdjustment : adjustments) {
		    		if(priceAdjustment.getAdjustmentDescription().equalsIgnoreCase(Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION)){
		    			tobeAdjusted = detailedItemPriceInfo;
		                break;
		    		}
				}
		    }
		}
		DetailedItemPriceInfo dpi = tobeAdjusted;
		
		List newdpis = updateDetailedPriceInfos(dpi, pItem);
		if(newdpis != null) {
		    // this will be the total number of items receiving the discount
		    for(int i=0; i < newdpis.size(); i++) {
		      DetailedItemPriceInfo newdpi = (DetailedItemPriceInfo) newdpis.get(i);

		        if (dpi == newdpi) {
		          vlogDebug("updated original detail " + newdpi.getRange() + ": " + newdpi);
		        }
		        else {
		          vlogDebug("new detail " + newdpi.getRange() + ": " + newdpi);
		        }

		      // if we get nothing back, none of this details was discounted
		      // try again with the next details
		      if (newdpi != null ){

		        // if the object returned isn't the one passed in, that means
		        // that we have a new dpi (undiscounted by this calc) to add to
		        // the list of DetailedItemPriceInfos. Add it, and add its price to the totals.
		        if (newdpi != dpi) {
		          // add the new one to the details list
		          detailsIterator.add(newdpi);

		        } // end if we got a new one

		      } // end if we got something back
		    }
		  }
		vlogDebug("TBSItemPriceCalculator :: assignDetailPrice() method :: END");
	}
	
	/**
	 * Updates the detailed price info.
	 * @param pDetailedItemPriceInfo
	 * @param pCommerceItem
	 * @return
	 * @throws PricingException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List updateDetailedPriceInfos(DetailedItemPriceInfo pDetailedItemPriceInfo, CommerceItem pCommerceItem) throws PricingException {
		
		vlogDebug("TBSItemPriceCalculator :: updateDetailedPriceInfos() method :: START");
		DetailedItemPriceInfo dpi = pDetailedItemPriceInfo;

		if ((dpi == null) || (dpi.getAmount() < 0.0D)) {
			vlogDebug(Constants.NO_INPUT_PRICE);
			return null;
		}

		TBSItemInfo iteminfo = null;
        if(pCommerceItem instanceof TBSCommerceItem){
        	iteminfo =  ((TBSCommerceItem)pCommerceItem).getTBSItemInfo();
        } else if(pCommerceItem instanceof LTLAssemblyFeeCommerceItem){
        	iteminfo =  ((LTLAssemblyFeeCommerceItem)pCommerceItem).getTBSItemInfo();
        } else if(pCommerceItem instanceof LTLDeliveryChargeCommerceItem ) {
        	iteminfo =  ((LTLDeliveryChargeCommerceItem)pCommerceItem).getTBSItemInfo();
        } else if(pCommerceItem instanceof GiftWrapCommerceItem){
        	iteminfo =  ((GiftWrapCommerceItem)pCommerceItem).getTBSItemInfo();
        }
        
		Object newOnes;
		if (iteminfo != null && iteminfo.getOverrideQuantity() == dpi.getQuantity()) {
			adjustEntireDetailedPrice(dpi, iteminfo);

			newOnes = new ArrayList();
			((List) newOnes).add(dpi);
			return (List) newOnes;
		}
		vlogDebug("TBSItemPriceCalculator :: updateDetailedPriceInfos() method :: END");
		return adjustPartialDetailedPrice(dpi, iteminfo);
	}
	
	/**
	 * adjusts EntireDetailedPrice info.
	 * @param pDetailedItemPriceInfo
	 * @param pTBSItemInfo
	 * @throws PricingException
	 */
	@SuppressWarnings("unchecked")
	private void adjustEntireDetailedPrice(DetailedItemPriceInfo pDetailedItemPriceInfo, TBSItemInfo pTBSItemInfo) throws PricingException {
		
		vlogDebug("TBSItemPriceCalculator :: adjustEntireDetailedPrice() method :: START");
		vlogDebug("discount this whole details, quantity: "	+ pDetailedItemPriceInfo.getQuantity());

		double currentAmount = pDetailedItemPriceInfo.getAmount();

		double newUnitPrice = pTBSItemInfo.getOverridePrice();

		if (Double.isNaN(newUnitPrice)) {
			return;
		}

		double newPrice = pTBSItemInfo.getOverridePrice() * pTBSItemInfo.getOverrideQuantity();

		newPrice = getPricingTools().round(newPrice);

		vlogDebug("after rounding, newPrice = " + newPrice);

		pDetailedItemPriceInfo.setAmount(newPrice);

		double newAdjustAmount = newPrice - currentAmount;

		newAdjustAmount = getPricingTools().round(newAdjustAmount);

		vlogDebug("after rounding, newAdjustAmount = " + newAdjustAmount);

		PricingAdjustment newAdjustment = new PricingAdjustment(TBSConstants.PRICE_OVERRIDE_DESC, null, newAdjustAmount, pDetailedItemPriceInfo.getQuantity());

		pDetailedItemPriceInfo.getAdjustments().add(newAdjustment);
		vlogDebug("TBSItemPriceCalculator :: adjustEntireDetailedPrice() method :: END");

	}

	/**
	 * Returns the partial adjustments.
	 * @param pOverriddenDpi
	 * @param itemDiscount
	 * @return List of detailedpriceInfo.
	 * @throws PricingException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List adjustPartialDetailedPrice(DetailedItemPriceInfo pOverriddenDpi, TBSItemInfo pTBSItemInfo) throws PricingException {
		
		if(pTBSItemInfo.getOverrideQuantity() <= 0){
			return null;
		}
		
		vlogDebug("TBSItemPriceCalculator :: adjustPartialDetailedPrice() method :: START");
		vlogDebug("splitting details of quantity: "	+ pOverriddenDpi.getQuantity());

		long oldQuantity = pOverriddenDpi.getQuantity();
		double oldUnitPrice = pOverriddenDpi.getDetailedUnitPrice();
		double oldAmount = pOverriddenDpi.getAmount();

		vlogDebug("old details quantity: " + oldQuantity);
		vlogDebug("old details unitprice: " + oldUnitPrice);
		vlogDebug("old details amount: " + oldAmount);

		DetailedItemPriceInfo unOverriddendDpi = getPricingTools().createDetailedItemPriceInfo(pOverriddenDpi);

		unOverriddendDpi.setItemPriceInfo(pOverriddenDpi.getItemPriceInfo());

		long difference = pOverriddenDpi.getQuantity() - pTBSItemInfo.getOverrideQuantity();
		unOverriddendDpi.setQuantity(difference);

		pOverriddenDpi.setQuantity(pTBSItemInfo.getOverrideQuantity());

	
		pOverriddenDpi.getRange().setHighBound(pOverriddenDpi.getRange().getHighBound() - difference);
		long newLowBound = pOverriddenDpi.getRange().getHighBound() + 1L;


		Range newRange = new Range(newLowBound, newLowBound	+ unOverriddendDpi.getQuantity() - 1L);
		unOverriddendDpi.setRange(newRange);
		 

		double undiscountedAmount = oldUnitPrice * unOverriddendDpi.getQuantity();

		vlogDebug("rounding non-discounted detail amount: "	+ undiscountedAmount);

		unOverriddendDpi.setAmount(undiscountedAmount);

		double splittedAmount = oldUnitPrice * pOverriddenDpi.getQuantity();
		double overriddenAmount = pTBSItemInfo.getOverridePrice() * pOverriddenDpi.getQuantity();

		vlogDebug("before rounding, discountedAmount = " + splittedAmount);

		pOverriddenDpi.setAmount(splittedAmount);

		vlogDebug("checking if non-discounted detail + discounted detail == total amount");

		double totalAmount = pOverriddenDpi.getAmount()	+ unOverriddendDpi.getAmount();

		totalAmount = getPricingTools().round(totalAmount);

		vlogDebug("after round, totalAmount = " + totalAmount);

		if (Double.compare(totalAmount, oldAmount) != BBBCoreConstants.ZERO) {
			try {
				PerformanceMonitor
				.cancelOperation("ItemDiscountCalculator_priceDetailedItemPriceInfo");
			} catch (Exception x) {
				if (isLoggingError()) {
					logError(x.toString());
				}
			}

			throw new PricingException(Constants.BAD_DETAILS_SPLIT);
		}

		undiscountedAmount = getPricingTools().round(undiscountedAmount);

		vlogDebug("rounded undiscounted detail amount to: "	+ undiscountedAmount);

		unOverriddendDpi.setAmount(undiscountedAmount);

		overriddenAmount = getPricingTools().round(overriddenAmount);

		vlogDebug("rounded discounted detail amount to: " + overriddenAmount);

		pOverriddenDpi.setAmount(overriddenAmount);

		double newUnitPrice = pTBSItemInfo.getOverridePrice();

		if (Double.isNaN(newUnitPrice)) {
			return null;
		}

		double totalDiscount = (oldUnitPrice - newUnitPrice) * pOverriddenDpi.getQuantity();

		totalDiscount = getPricingTools().round(totalDiscount);

		vlogDebug("after rounding, newAdjustAmount = " + totalDiscount);

		PricingAdjustment newAdjustment = new PricingAdjustment(TBSConstants.PRICE_OVERRIDE_DESC, null, totalDiscount, pOverriddenDpi.getQuantity());

		pOverriddenDpi.getAdjustments().add(newAdjustment);

		vlogDebug("Now have two details, OverriddenDpi with quantity : " + pOverriddenDpi.getQuantity() + " and unOverriddendDpi one with quantity : " + unOverriddendDpi.getQuantity());

		List dpis = null;
		dpis = new ArrayList(2);
		dpis.add(pOverriddenDpi);
		dpis.add(unOverriddendDpi);
		vlogDebug("TBSItemPriceCalculator :: adjustPartialDetailedPrice() method :: END");
		return dpis;
	}

	
}


