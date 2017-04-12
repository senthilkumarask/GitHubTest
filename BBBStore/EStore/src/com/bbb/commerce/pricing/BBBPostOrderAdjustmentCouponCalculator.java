package com.bbb.commerce.pricing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.OrderDiscountCalculator;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * 
 * Order post calculator for setting coupon in the item adjustments after
 * getting the value from the skuMap from pExtraParameters
 * 
 */
public class BBBPostOrderAdjustmentCouponCalculator extends OrderDiscountCalculator {

	/**
	 * This method is used to set coupons in the item adjustments
	 * 
	 * @param pPriceQuote
	 * @param pOrder
	 * @param pPricingModel
	 * @param pLocale
	 * @param pProfile
	 * @param pExtraParameters
	 * @return void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void priceOrder(OrderPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale,
			RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
		vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Starts");
		BBBPerformanceMonitor.start("BBBPostOrderAdjustmentCouponCalculator", "priceOrder");
		List<CommerceItem> commItems = pOrder.getCommerceItems();
		Map<String, LinkedHashMap<RepositoryItem, String>> skumap = (Map<String, LinkedHashMap<RepositoryItem, String>>) pExtraParameters
				.get(BBBCoreConstants.SKU_MAP);
		if (skumap != null) {
			// iterate over the commerce items
			for (CommerceItem commerceItem : commItems) {
				List<DetailedItemPriceInfo> priceBeans = commerceItem.getPriceInfo().getCurrentPriceDetails();
				if (skumap.containsKey(commerceItem.getCatalogRefId())) {
					LinkedHashMap<RepositoryItem, String> promomap = skumap.get(commerceItem.getCatalogRefId());
					LinkedHashMap<RepositoryItem, String> copyPromomap = new LinkedHashMap<RepositoryItem, String>();
					copyPromomap.putAll(promomap);
					vlogDebug(
							"BBBPostOrderAdjustmentCouponCalculator.priceOrder: promo Map Initial for SKU {0} is {1} ",
							commerceItem.getCatalogRefId(), promomap.toString());
					List<PricingAdjustment> itemAdjustments = commerceItem.getPriceInfo().getAdjustments();
					// iterate over each adjustment in IPI of the commerce item
					for (PricingAdjustment pricingAdj : itemAdjustments) {
						final RepositoryItem promotion = pricingAdj.getPricingModel();
						try {
							if (null != promotion
									&& BBBCoreConstants.ITEM_DISCOUNT.equalsIgnoreCase(promotion.getItemDescriptor()
											.getItemDescriptorName())) {
								RepositoryItem couponKeyToBeSaved = null;
								String promoId = promotion.getRepositoryId();
								/*
								 * set the coupon in the adjustment if the
								 * mapping is found in the skuMap
								 */
								for (RepositoryItem key : copyPromomap.keySet()) {
									String promotionId = copyPromomap.get(key);
									if (promoId.equalsIgnoreCase(promotionId)) {
										vlogDebug(
												"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion id {0} to set coupon {1}",
												promoId, key);
										couponKeyToBeSaved = key;
									}
								}
								if (null != couponKeyToBeSaved) {
									vlogDebug(
											"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating Coupon {0} in item pricing adjustment for promotion id {1}",
											couponKeyToBeSaved, promoId);
									pricingAdj.setCoupon(couponKeyToBeSaved);
									copyPromomap.remove(couponKeyToBeSaved);
									vlogDebug(
											"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Copy promo Map : {0}",
											copyPromomap.toString());
								}
							}
						} catch (RepositoryException e) {
							vlogError(
									"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion {0}",
									promotion);
							throw new PricingException(
									"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion: "
											+ promotion);
						}
					}
					// iterate over each DPI of the commerce item
					for (DetailedItemPriceInfo unitPriceBean : priceBeans) {
						if (unitPriceBean.getAdjustments() != null && unitPriceBean.getAdjustments().size() > 0) {
							List<PricingAdjustment> adjustments = unitPriceBean.getAdjustments();
							// iterate over each adjustment in each DPI
							for (PricingAdjustment pricingAdj : adjustments) {
								final RepositoryItem promotion = pricingAdj.getPricingModel();
								try {
									if (null != promotion
											&& BBBCoreConstants.ITEM_DISCOUNT.equalsIgnoreCase(promotion
													.getItemDescriptor().getItemDescriptorName())) {
										RepositoryItem couponKeyToBeSaved = null;
										String promoId = promotion.getRepositoryId();
										/*
										 * set the coupon in the adjustment if
										 * the mapping is found in the skuMap
										 */
										for (RepositoryItem key : promomap.keySet()) {
											String promotionId = promomap.get(key);
											if (promoId.equalsIgnoreCase(promotionId)) {
												vlogDebug(
														"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Found matching promotion {0} to set coupon {1}",
														promotion, key);
												couponKeyToBeSaved = key;
											}
										}
										if (null != couponKeyToBeSaved) {
											vlogDebug(
													"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Updating coupon {0} in detailed item pricing adjustment for promotion {1}",
													couponKeyToBeSaved, promotion);
											pricingAdj.setCoupon(couponKeyToBeSaved);
											promomap.remove(couponKeyToBeSaved);
											vlogDebug(
													"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Copy promo Map : {0}",
													copyPromomap.toString());
										}
									}
								} catch (RepositoryException e) {
									vlogError(
											"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion {0}",
											promotion);
									throw new PricingException(
											"BBBPostOrderAdjustmentCouponCalculator.priceOrder: Repository Exception occured while trying to get item descriptor name for promotion: "
													+ promotion);
								}
							}
						}
					}
				}
			}
		}
		BBBPerformanceMonitor.end("BBBPostOrderAdjustmentCouponCalculator", "priceOrder");
		vlogDebug("BBBPostOrderAdjustmentCouponCalculator.priceOrder: Ends");
	}
}
