package com.bbb.commerce.pricing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.priceLists.ItemPriceCalculator;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * 
 * An Item pre calculator for setting map of coupon and promotion in
 * extraparameter map to be used for saving coupon in the pricing adjustments
 * 
 */
public class BBBPostCouponDetailsInitializer extends ItemPriceCalculator {

	public static final String ACTIVE_PROMOTIONS = "activePromotions";
	public static final String PROMOTION = "promotion";
	public static final String COUPONS = "coupons";

	/**
	 * This method is called to set map of coupon and promotion in
	 * extraparameter map to be used for saving coupon in the pricing
	 * adjustments *
	 * 
	 * @param pPriceQuote
	 * @param pItem
	 * @param pPricingModel
	 * @param pLocale
	 * @param pProfile
	 * @param pExtraParameters
	 * @return void
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters) throws PricingException {

		BBBPerformanceMonitor.start("BBBCouponDetailsInitializer", "priceItem");
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null, "Start - BBBCouponDetailsInitializer.priceItem"));
		}
		Map<RepositoryItem, String> promoMap = null;
		// my offers automation - Get Promo map from extraparameter map
		if (pExtraParameters.get(BBBCoreConstants.PROMO_MAP) != null) {
			promoMap = (Map<RepositoryItem, String>) pExtraParameters.get(BBBCoreConstants.PROMO_MAP);
		}
		// Get the Coupon code for the current promotion from
		if (null != pPricingModel && promoMap != null) {
			for (RepositoryItem key : promoMap.keySet()) {
				String promotionId = promoMap.get(key);

				if (promotionId.equalsIgnoreCase(pPricingModel.getRepositoryId())) {
					try {
						if (pPricingModel.getItemDescriptor().getItemDescriptorName()
								.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)) {
							promoMap.remove(key);
						}
					} catch (RepositoryException e) {
						if (isLoggingDebug()) {
							logDebug(LogMessageFormatter.formatMessage(null,
									"Repository Exception- BBBCouponDetailsInitializer.priceItem " + e));
						}
					}
					pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap);
					break;
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug(LogMessageFormatter.formatMessage(null,
					"End- BBBCouponDetailsInitializer.priceItem :: value of map " + promoMap));
		}

		BBBPerformanceMonitor.end("BBBCouponDetailsInitializer", "priceItem");

	}

	public void logDebug(String msg) {
		if (isLoggingDebug()) {
			super.logDebug(msg);
		}
	}

}
