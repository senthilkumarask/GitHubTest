package com.bbb.commerce.pricing;

import java.util.ArrayList;
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

import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * 
 * An Item pre calculator for setting map of coupon and promotion in
 * extraparameter map to be used for saving coupon in the pricing adjustments
 * 
 */
public class BBBCouponDetailsInitializer extends ItemPriceCalculator {

	private BBBPromotionTools promotionTools;
	private BBBItemPricingEngineService itemPricingEngine;
	private BBBOrderPricingEngineService orderPricingEngine;
	private BBBShippingPricingEngineService shippingPricingEngine;

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
		vlogDebug(LogMessageFormatter.formatMessage(null, "Start - BBBCouponDetailsInitializer.priceItem"));
		/*
		 * For explicit promotion we getting promotion from user active
		 * promotions.
		 */
		final List<RepositoryItem> activePromotions = (List<RepositoryItem>) pProfile
				.getPropertyValue(ACTIVE_PROMOTIONS);
		Map<RepositoryItem, String> promoMap = null;
		if (activePromotions != null && !activePromotions.isEmpty()) {
			promoMap = new LinkedHashMap<RepositoryItem, String>();
			for (RepositoryItem promotionStatus : activePromotions) {
				RepositoryItem promotion = (RepositoryItem) promotionStatus.getPropertyValue(PROMOTION);
				List<RepositoryItem> coupons = (List<RepositoryItem>) promotionStatus.getPropertyValue(COUPONS);
				if (coupons != null && promotion != null) {
					promoMap.put(coupons.get(0), promotion.getRepositoryId());
				}
			}
		}
		/*
		 * 1st we are getting all global promotion from respective pricing
		 * engine and then if any coupon is associated with any global promotion
		 * then we are getting coupons from Claimable repository directly for
		 * that particular global promotion.
		 * 
		 * Finally we are creating a map of couponItem to promotion id.
		 */
		List<RepositoryItem> globalItemPromotions = getItemPricingEngine().getGlobalPromotions();
		List<RepositoryItem> globalOrderPromotions = getOrderPricingEngine().getGlobalPromotions();
		List<RepositoryItem> globalShippingPromotions = getShippingPricingEngine().getGlobalPromotions();
		vlogDebug(
				"BBBCouponDetailsInitializer.priceItem: Global promotions for Item: {0}, Order: {1} and Shipping: {2}",
				globalItemPromotions, globalOrderPromotions, globalShippingPromotions);
		List<RepositoryItem> allGlobalPromotions = new ArrayList<RepositoryItem>();
		allGlobalPromotions.addAll(globalItemPromotions);
		allGlobalPromotions.addAll(globalOrderPromotions);
		allGlobalPromotions.addAll(globalShippingPromotions);
		vlogDebug("BBBCouponDetailsInitializer.priceItem: Complete list of global promotions is {0}",
				allGlobalPromotions);
		if (allGlobalPromotions.isEmpty()) {
			pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap);
			vlogDebug("BBBCouponDetailsInitializer.priceItem: Ends, There is no global promotion associated with the site");
			BBBPerformanceMonitor.end("BBBCouponDetailsInitializer", "priceItem");
			return;
		}
		for (RepositoryItem globalPromotion : allGlobalPromotions) {
			RepositoryItem couponItems[] = null;
			try {
				couponItems = getPromotionTools().getCoupons(globalPromotion);
			} catch (RepositoryException e) {
				vlogError(
						"BBBCouponDetailsInitializer.priceItem: RepositoryException occured while trying to get coupon items from ClaimableRepository using promotion {0}",
						globalPromotion);
			}
			if (null == couponItems || couponItems.length <= 0) {
				/*
				 * there is no coupon rules assigned for the global promotion,
				 * so no need to proceed further to execute coupon rules
				 */
				vlogDebug(
						"BBBCouponDetailsInitializer.priceItem: There is no coupon rules assigned for the global promotion {0}",
						globalPromotion);
			} else {
				if (null == promoMap) {
					promoMap = new LinkedHashMap<RepositoryItem, String>();
				}
				promoMap.put(couponItems[0], globalPromotion.getRepositoryId());
				vlogDebug(
						"BBBCouponDetailsInitializer.priceItem: Found associated coupon {0} for global promotion {1} to execute inclusion/excluusion rules.",
						couponItems[0], globalPromotion);
			}
		}
		pExtraParameters.put(BBBCoreConstants.PROMO_MAP, promoMap);
		vlogDebug("BBBCouponDetailsInitializer.priceItem: value of promo map is {0}", promoMap);
		BBBPerformanceMonitor.end("BBBCouponDetailsInitializer", "priceItem");
	}

	/**
	 * @return the promotionTools
	 */
	public BBBPromotionTools getPromotionTools() {
		return promotionTools;
	}

	/**
	 * @param promotionTools
	 *            the promotionTools to set
	 */
	public void setPromotionTools(BBBPromotionTools promotionTools) {
		this.promotionTools = promotionTools;
	}

	/**
	 * @return the itemPricingEngine
	 */
	public BBBItemPricingEngineService getItemPricingEngine() {
		return itemPricingEngine;
	}

	/**
	 * @param itemPricingEngine
	 *            the itemPricingEngine to set
	 */
	public void setItemPricingEngine(BBBItemPricingEngineService itemPricingEngine) {
		this.itemPricingEngine = itemPricingEngine;
	}

	/**
	 * @return the orderPricingEngine
	 */
	public BBBOrderPricingEngineService getOrderPricingEngine() {
		return orderPricingEngine;
	}

	/**
	 * @param orderPricingEngine
	 *            the orderPricingEngine to set
	 */
	public void setOrderPricingEngine(BBBOrderPricingEngineService orderPricingEngine) {
		this.orderPricingEngine = orderPricingEngine;
	}

	/**
	 * @return the shippingPricingEngine
	 */
	public BBBShippingPricingEngineService getShippingPricingEngine() {
		return shippingPricingEngine;
	}

	/**
	 * @param shippingPricingEngine
	 *            the shippingPricingEngine to set
	 */
	public void setShippingPricingEngine(BBBShippingPricingEngineService shippingPricingEngine) {
		this.shippingPricingEngine = shippingPricingEngine;
	}
}
