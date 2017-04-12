package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.vo.CouponListVo;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * @author dwaghmare
 */
public class AppliedCouponsDroplet extends BBBDynamoServlet {

	private BBBPromotionTools promotionTools;

	@SuppressWarnings({ "rawtypes" })
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		BBBPerformanceMonitor.start("AppliedCouponsDroplet", "service");
		logDebug("Starting method AppliedCouponsDroplet.service");

		Order order = (Order) pRequest.getObjectParameter(BBBCoreConstants.ORDER);
		final List<RepositoryItem> appliedCoupons = getPromotionTools().getCouponListFromOrder(order);
		List<CouponListVo> couponList = new ArrayList<CouponListVo>();
		for (RepositoryItem couponItem : appliedCoupons) {
			if (null == couponItem) {
				vlogDebug("AppliedCouponsDroplet.service: Coupons is null");
				continue;
			}
			RepositoryItem[] currentPromos = null;
			try {
				currentPromos = getPromotionTools().getCatalogTools().getPromotions(couponItem.getRepositoryId());
				if (null == currentPromos
						|| currentPromos.length <= 0
						|| !(currentPromos[0].getItemDescriptor().getItemDescriptorName()
								.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT) || currentPromos[0]
								.getItemDescriptor().getItemDescriptorName()
								.equalsIgnoreCase(BBBCoreConstants.ORDER_DISCOUNT))) {
					vlogDebug("AppliedCouponsDroplet.service: There is no promotions associated to the coupon {0}",
							couponItem);
					continue;
				}
			} catch (BBBSystemException e) {
				vlogError(
						"AppliedCouponsDroplet.service: BBBSystemException occurred while trying to fetch promotion from the coupon {0}",
						couponItem);
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			} catch (BBBBusinessException e) {
				vlogError(
						"AppliedCouponsDroplet.service: BBBBusinessException occurred while trying to fetch promotion from the coupon {0}",
						couponItem);
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			} catch (RepositoryException e) {
				vlogError(
						"AppliedCouponsDroplet.service: RepositoryException occurred while trying to fetch promotion type from promotion {0}",
						currentPromos[0]);
			}
			vlogDebug("AppliedCouponsDroplet.service: Promotions {0} found in associated coupon {1}", currentPromos,
					couponItem);
			/*
			 * Taking only the 1st promotion from the promotion list as there
			 * will be a single associated promotion to any coupons.
			 */
			CouponListVo couponVO = new CouponListVo();
			couponVO.setCouponsDescription(String.valueOf(currentPromos[0]
					.getPropertyValue(BBBCoreConstants.DISPLAY_NAME)));
			Map mediaMap = (Map) currentPromos[0].getPropertyValue(BBBCoreConstants.MEDIA);
			if (null != mediaMap) {
				RepositoryItem mediaIteam = (RepositoryItem) mediaMap.get(BBBCoreConstants.PROMOTION_MAIN_IMAGE_KEY);
				if (null != mediaIteam) {
					couponVO.setCouponsImageUrl((String) (mediaIteam.getPropertyValue(BBBCoreConstants.URL) != null ? mediaIteam
							.getPropertyValue(BBBCoreConstants.URL) : BBBCoreConstants.BLANK));

				}
			}
			couponList.add(couponVO);
		}
		pRequest.setParameter(BBBCoreConstants.COUPON_LIST, couponList);
		pRequest.serviceLocalParameter(BBBCoreConstants.OUTPUT, pRequest, pResponse);
		logDebug("Exiting method AppliedCouponsDroplet.service");
		BBBPerformanceMonitor.end("AppliedCouponsDroplet", "service");

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

}