package com.bbb.commerce.pricing.calculator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.ShippingCalculatorImpl;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;

/**
 * @author sdandr
 * 
 */
public class SubTotalRangeCalculator extends ShippingCalculatorImpl {

	/**
	 * Constant for product BBBCatalogTools.
	 */
	private BBBCatalogTools mCatalogUtil;

	/**
	 * @see atg.commerce.pricing.ShippingCalculatorImpl#priceShippingGroup(atg.commerce.order.Order,
	 *      atg.commerce.pricing.ShippingPriceInfo,
	 *      atg.commerce.order.ShippingGroup, atg.repository.RepositoryItem,
	 *      java.util.Locale, atg.repository.RepositoryItem, java.util.Map)
	 * 
	 * @description This method sets the shippingCharges and surchargeCharges in
	 *              the shippingPriceInfo of the given shippingGroup. If there
	 *              are no shipping method in the shippingGroup then it invokes
	 *              the CatalogUtil to get the default shipping method and then
	 *              invokes the priceShippingGroup(), this is required to
	 *              calculate estimated shipping.
	 * 
	 *              It invokes the BBBPricingTools to calculate shipping and
	 *              surcharge charges.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote,
			ShippingGroup pShippingGroup, RepositoryItem pPricingModel,
			Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
			throws PricingException {

		if (isLoggingDebug()){
			logDebug("Inside priceShippingGroup");
		}		
		Boolean isLtlItemPresent = false;
		List<BBBShippingGroupCommerceItemRelationship> sgcirels = (List<BBBShippingGroupCommerceItemRelationship>)pShippingGroup.getCommerceItemRelationships();
		for(BBBShippingGroupCommerceItemRelationship sgcirel : sgcirels){
			if(sgcirel.getCommerceItem() instanceof BBBCommerceItem){
				isLtlItemPresent = ((BBBCommerceItem)sgcirel.getCommerceItem()).isLtlItem();
				break;
			}
		}
		if (pShippingGroup != null
				&& pShippingGroup instanceof HardgoodShippingGroup) {

			if (pShippingGroup.getShippingMethod() != null
					&& (!pShippingGroup.getShippingMethod().equalsIgnoreCase("") || (pShippingGroup.getShippingMethod().equalsIgnoreCase("") && isLtlItemPresent))
					&& !pShippingGroup.getShippingMethod().equalsIgnoreCase("hardgoodShippingGroup")) {

				//String siteId = SiteContextManager.getCurrentSiteId();
				/*
				 * TODO: Once we have siteContext manager we need to revert it
				 * back.
				 */
				//String siteId = "BuyBuyBaby";
				
				try {
					double shippingCharges = 0.0;
					double surchargeCharges = 0.0;
					if(!(pShippingGroup.getShippingMethod().equalsIgnoreCase("") && isLtlItemPresent)){
						if(isLoggingDebug()){
							logDebug("Ltl Present in order, so calculating shipping and surcharge prices");
						}
					shippingCharges = ((BBBPricingTools) getPricingTools())
							.calculateShippingCost(pOrder.getSiteId(),
									pShippingGroup.getShippingMethod(),
									(HardgoodShippingGroup) pShippingGroup, null);

					surchargeCharges = ((BBBPricingTools) getPricingTools())
							.calculateSurcharge(pOrder.getSiteId(),
									(HardgoodShippingGroup) pShippingGroup);
					if(isLoggingDebug()){
						logDebug("shipping charges: " + shippingCharges + " surcharge charges: " + surchargeCharges);
					}
					}
					((BBBShippingPriceInfo) pPriceQuote)
							.setRawShipping(getPricingTools().round(shippingCharges));
					((BBBShippingPriceInfo) pPriceQuote)
							.setFinalShipping(getPricingTools().round(shippingCharges));

					((BBBShippingPriceInfo) pPriceQuote)
							.setSurcharge(getPricingTools().round(surchargeCharges));
					((BBBShippingPriceInfo) pPriceQuote)
							.setFinalSurcharge(getPricingTools().round(surchargeCharges));

					((BBBShippingPriceInfo) pPriceQuote)
							.setAmount(getPricingTools().round(shippingCharges + surchargeCharges));
					// pShippingGroup.getPriceInfo().setAmount(shippingCharges +
					// surchargeCharges);

				} catch (PricingException exp) {
					if (isLoggingError()) {
						logError("Shipping Pricing Error", exp);
					}
				}

			} else {
				// need to revisit this
				try {
					ShipMethodVO shipMethodVO = getCatalogUtil()
							.getDefaultShippingMethod(
							        pOrder.getSiteId());
					pShippingGroup.setShippingMethod(shipMethodVO
							.getShipMethodId());
					priceShippingGroup(pOrder, pPriceQuote, pShippingGroup,
							pPricingModel, pLocale, pProfile, pExtraParameters);
				} catch (BBBSystemException e) {
					if (isLoggingError()) {
						logError("Error getting shipping method", e);
					}
				} catch (BBBBusinessException e) {
					if (isLoggingError()) {
					    logError("Error getting shipping method", e);
					}
				}
			}
			
			//Add EcoFee
			double ecoFeeTotal = 0.0;
			List<CommerceItemRelationship> ciRels = pShippingGroup.getCommerceItemRelationships();
			CommerceItem tempCI = null;
			for (CommerceItemRelationship commerceItemRelationship : ciRels) {
				tempCI = commerceItemRelationship.getCommerceItem();
				if(tempCI instanceof EcoFeeCommerceItem){
					ecoFeeTotal += tempCI.getPriceInfo().getAmount();
				}
			}
			
			/**
			 * Add ecoFeeTotal.
			 */
			((BBBShippingPriceInfo) pPriceQuote).setEcoFee(getPricingTools().round(ecoFeeTotal));
		}

	}

	/**
	 * @see atg.commerce.pricing.ShippingCalculatorImpl#haveItemsToShip(atg.commerce.order.ShippingGroup)
	 * 
	 * @description this method is overridden to skip the storeShippingGroup
	 *              type shipping group, since store shipping is free.
	 */
	protected boolean haveItemsToShip(ShippingGroup pShippingGroup) {

		if (pShippingGroup != null
				&& pShippingGroup instanceof BBBStoreShippingGroup) {
			return false;
		} else {
			return extractedSuperHaveItemsToShip(pShippingGroup);
		}
	}

	protected boolean extractedSuperHaveItemsToShip(ShippingGroup pShippingGroup) {
		return super.haveItemsToShip(pShippingGroup);
	}

	/**
	 * @return BBBCatalogTools
	 */
	public BBBCatalogTools getCatalogUtil() {
		return mCatalogUtil;
	}

	/**
	 * @param catalogUtil
	 */
	public void setCatalogUtil(final BBBCatalogTools catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}



}
