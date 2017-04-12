package com.bbb.commerce.service.pricing;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceCalculator;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.core.util.Range;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.atg.Adjustment;
import com.bedbathandbeyond.atg.Item;

public class ItemPricingWSCalculator extends ItemPriceCalculator {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void priceItems(final List pPriceQuotes, final List pItems, final RepositoryItem pPricingModel, final Locale pLocale, final RepositoryItem pProfile, final Order pOrder, final Map pExtraParameters) {

		if (pExtraParameters.get(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM) != null) {
			final boolean partiallyShipped = (Boolean) pExtraParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED);
			final Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = (HashMap<ShippingGroupCommerceItemRelationship, Item>) pExtraParameters.get(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM);
			CommerceItem commerceItem = null;
			Item shipmentItem = null;
			DetailedItemPriceInfo originalPriceDetail = null;
			final Set<ShippingGroupCommerceItemRelationship> shippingGroupCommerceItemRelationships = itemInfoMap.keySet();
			ItemPriceInfo bbbItemPrice = null;
			for (ShippingGroupCommerceItemRelationship sgcir : shippingGroupCommerceItemRelationships) {
				commerceItem = sgcir.getCommerceItem();
				shipmentItem = itemInfoMap.get(sgcir);

				bbbItemPrice = (ItemPriceInfo) pPriceQuotes.get(pItems.indexOf(commerceItem));
				
				bbbItemPrice.setRawTotalPrice(getPricingTools().round(shipmentItem.getPrice().doubleValue() * commerceItem.getQuantity()));
				setListPriceAdjustments(bbbItemPrice, shipmentItem.getPrice().doubleValue(), sgcir.getRange(), pExtraParameters);
				if (partiallyShipped) {					
					final List detailedPriceInfosForRange = bbbItemPrice.getCurrentPriceDetailsForRange(sgcir.getRange());
					if (!BBBUtility.isListEmpty(detailedPriceInfosForRange)) {
						originalPriceDetail = (DetailedItemPriceInfo) detailedPriceInfosForRange.get(0);

						if (shipmentItem.getAdjustmentList() != null && shipmentItem.getAdjustmentList().getAdjustmentArray() != null) {
							try {
								setCouponsInOrder(bbbItemPrice, originalPriceDetail, shipmentItem);
							} catch (RepositoryException rExp) {
								logError("Pricing exception while performing pricing operation - ItemPricingWSCalculator", rExp);
							} catch (BBBSystemException e) {
								vlogError("BBBSystemException while performing pricing operation - ItemPricingWSCalculator", e);
							} catch (BBBBusinessException e) {
								vlogError("BBBBusinessException while performing pricing operation - ItemPricingWSCalculator", e);
							}
						}
					}
				}
			}
		}
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	protected void priceItem(final double pPrice, final ItemPriceInfo pPriceQuote,
			final CommerceItem pItem, final RepositoryItem pPricingModel, final Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {
		//do nothing
	}
	
	
	@SuppressWarnings("rawtypes")
	private void setListPriceAdjustments(ItemPriceInfo bbbItemPrice, double price, Range range, Map pExtraParameters) {		
		
		bbbItemPrice.setListPrice(getPricingTools().round(price, 2));		
		bbbItemPrice.setSalePrice(0);
		bbbItemPrice.setOnSale(false);
		double newAmount;
		
		List detailedPriceInfos = bbbItemPrice.getCurrentPriceDetailsForRange(range);
		if (!BBBUtility.isListEmpty(detailedPriceInfos)) {
			
			DetailedItemPriceInfo listPriceDetail = (DetailedItemPriceInfo) detailedPriceInfos.get(0);
		    newAmount = price * listPriceDetail.getQuantity();
			listPriceDetail.setAmount(getPricingTools().round(newAmount, 2));	
			
			logDebug("ItemPricingWSCalculator:: setListPriceAdjustments() - List Price Detail: "+ listPriceDetail.getAmount());
			
			PricingAdjustment listDetailPriceAdj = (PricingAdjustment) listPriceDetail.getAdjustments().get(0);
			listDetailPriceAdj.setTotalAdjustment(getPricingTools().round(listPriceDetail.getAmount(), 2));
			bbbItemPrice.setAmount(getPricingTools().round(newAmount, 2));
			
			logDebug("ItemPricingWSCalculator:: setListPriceAdjustments() - Total Adjustment" + listDetailPriceAdj.getTotalAdjustment() +
						"Item Price: "+ bbbItemPrice.getAmount());
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setCouponsInOrder(ItemPriceInfo bbbItemPrice,
			DetailedItemPriceInfo currentDPI, Item item)
			throws RepositoryException, BBBSystemException, BBBBusinessException {
		RepositoryItem[] promotionArray = null;
		RepositoryItem pricingModel = null;
		for (Adjustment itemAdjustment : item.getAdjustmentList().getAdjustmentArray()) {
						
			if(StringUtils.isEmpty(itemAdjustment.getAtgPromotionId())) {
			    promotionArray = ((BBBPricingTools) getPricingTools()).getPromotion(itemAdjustment.getCouponCode(), itemAdjustment.getPromotionType());
			} else {
			    promotionArray = ((BBBPricingTools) getPricingTools()).getPromotion(itemAdjustment.getAtgPromotionId(), itemAdjustment.getPromotionType());
			}
			if (promotionArray != null && promotionArray.length > 0) {
				pricingModel = promotionArray[0];
				PricingAdjustment priceAdjustment = ((BBBPricingTools)getPricingTools()).getPromotionAdjustment(pricingModel, getPricingTools().round(itemAdjustment.
						getDiscountAmount().doubleValue(), 2), item.getQuantity().longValue());
				currentDPI.getAdjustments().add(priceAdjustment);
				bbbItemPrice.setAmount(getPricingTools().round(bbbItemPrice.getAmount() + priceAdjustment.getTotalAdjustment(), 2));
				currentDPI.setDiscounted(true);
				currentDPI.setAmount(getPricingTools().round(currentDPI.getAmount() + priceAdjustment.getTotalAdjustment(), 2));
			}
			
		}		
	}
}
