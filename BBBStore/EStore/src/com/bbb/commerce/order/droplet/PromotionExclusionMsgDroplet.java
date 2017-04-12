package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

import atg.commerce.order.CommerceItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This Droplet prepares a Map of commerceItems which are having some excluded
 * promotions.
 * 
 * @author vagra
 * 
 */
public class PromotionExclusionMsgDroplet extends BBBDynamoServlet {

	private BBBPromotionTools mPromotionTools;

	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		BBBPerformanceMonitor.start("PromotionExclusionMsgDroplet", "service");
		logDebug("Start: service() method");

		BBBOrderImpl order = (BBBOrderImpl) pRequest.getObjectParameter(BBBCoreConstants.ORDER);

		Map<String, Set<CommerceItem>> excludedPromoComItem = order.getExcludedPromotionMap();
		vlogDebug("PromotionExclusionMsgDroplet.service: excludedPromoComItem: {0}", excludedPromoComItem);
		Map<String, Set<RepositoryItem>> comItemExclMap = null;
		List<RepositoryItem> appliedCoupons = getPromotionTools().getCouponListFromOrder(order);
		if (!excludedPromoComItem.isEmpty()) {
			comItemExclMap = new HashMap<String, Set<RepositoryItem>>();
			/*
			 * The bellow section is for collecting commerce item which are
			 * excluded to get any promotion by coupon exclusion rules defined
			 * in CouponRuleRepository.
			 */  
			if (!BBBUtility.isListEmpty(appliedCoupons)) {
				for (RepositoryItem coupon : appliedCoupons) {
					String couponId = coupon.getRepositoryId();
					if (!excludedPromoComItem.containsKey(couponId)) {
						vlogDebug(
								"PromotionExclusionMsgDroplet.service: There is no exclusion rules defined for this coupon: {0}",
								coupon);
						continue;
					}
					try {
						Set<CommerceItem> comItemSet = excludedPromoComItem.get(couponId);
						RepositoryItem[] promotion = getPromotionTools().getCatalogTools().getPromotions(
								couponId);
						if (null == promotion || promotion.length <= 0) {
							vlogDebug(
									"PromotionExclusionMsgDroplet.service: There is no promotion for this coupon: {0}",
									coupon);
							continue;
						}
						for (CommerceItem commerceItem : comItemSet) {
							if (!comItemExclMap.containsKey(commerceItem.getId())) {
								Set<RepositoryItem> excludedPromos = new HashSet<RepositoryItem>();
								excludedPromos.add(promotion[0]);
								comItemExclMap.put(commerceItem.getId(), excludedPromos);
								vlogDebug(
										"PromotionExclusionMsgDroplet.service: CommerceItem: {0} to promotion: {1} map is not present so creating an empty map to add promotion",
										commerceItem, promotion[0]);
							} else {
								comItemExclMap.get(commerceItem.getId()).add(promotion[0]);
								vlogDebug(
										"PromotionExclusionMsgDroplet.service: CommerceItem: {0} to promotion map is already present so adding promotion: {1} to map",
										commerceItem, promotion[0]);
							}
						}
					} catch (BBBSystemException e) {
						pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
						vlogError(
								"PromotionExclusionMsgDroplet.service: BBBSystemException occur while trying to get promotion from coupon: {0}",
								coupon);
					} catch (BBBBusinessException e) {
						pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
						vlogError(
								"PromotionExclusionMsgDroplet.service: BBBBusinessException occur while trying to get promotion from coupon: {0}",
								coupon);
					}
				}
			}
		}

		vlogDebug("PromotionExclusionMsgDroplet.service: comItemExclMap: {0}" , comItemExclMap);

		if (!BBBUtility.isMapNullOrEmpty(comItemExclMap)) {
			pRequest.setParameter(BBBCheckoutConstants.PROMO_EXCLUSION_MAP, comItemExclMap);
			pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
		} else {
			pRequest.serviceLocalParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
		}

		logDebug("End: service() method");
		BBBPerformanceMonitor.end("PromotionExclusionMsgDroplet", "service");
	}

	/**
	 * @return the mPromotionTools
	 */
	public BBBPromotionTools getPromotionTools() {
		return mPromotionTools;
	}

	/**
	 * @param pPromotionTools
	 *            the mPromotionTools to set
	 */
	public void setPromotionTools(BBBPromotionTools pPromotionTools) {
		mPromotionTools = pPromotionTools;
	}
}
