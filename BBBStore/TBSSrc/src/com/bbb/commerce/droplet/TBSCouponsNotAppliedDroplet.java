package com.bbb.commerce.droplet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.claimable.ClaimableException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.claimable.TBSClaimableManager;
import com.bbb.commerce.order.TBSOrder;
import com.bbb.constants.TBSConstants;

public class TBSCouponsNotAppliedDroplet extends DynamoServlet {

	private BBBPromotionTools promotionTools;

	TBSClaimableManager claimableManager;

	/**
	 * @return the claimableManager
	 */
	public TBSClaimableManager getClaimableManager() {
		return claimableManager;
	}

	/**
	 * @param pClaimableManager
	 *            the claimableManager to set
	 */
	public void setClaimableManager(TBSClaimableManager pClaimableManager) {
		claimableManager = pClaimableManager;
	}

	/**
	 * @return the promotionTools
	 */
	public BBBPromotionTools getPromotionTools() {
		return promotionTools;
	}

	/**
	 * @param pPromotionTools
	 *            the promotionTools to set
	 */
	public void setPromotionTools(BBBPromotionTools pPromotionTools) {
		promotionTools = pPromotionTools;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		vlogDebug("TBSCouponsNotAppliedDroplet.service: Starts");
		TBSOrder order = (TBSOrder) pRequest.getObjectParameter("order");
		Map<String, RepositoryItem> couponMap = order.getTbsCouponsMap();
		if (null == couponMap || couponMap.isEmpty()) {
			vlogDebug("TBSCouponsNotAppliedDroplet.service: Ends, There is no coupons is applied for the order: {0}",
					order);
			return;
		}

		Set<String> coupons = couponMap.keySet();
		List<RepositoryItem> appliedCoupons = getPromotionTools().getCouponListFromOrder(order);

		if (coupons != null && !coupons.isEmpty() && appliedCoupons.isEmpty()) {
			for (String coupon : coupons) {
				RepositoryItem couponItem = null;
				try {
					couponItem = getClaimableManager().claimItem(coupon);
				} catch (ClaimableException e) {
					vlogError("TBSCouponsNotAppliedDroplet.service: not a store coupon", e);
				}
				if (null != couponItem) {
					pRequest.setParameter("promoName", couponItem.getPropertyValue("displayName"));
					pRequest.setParameter("error", "err_coupon_grant_error");
					pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
				} else {
					RepositoryItem promoItem = couponMap.get(coupon);
					if (promoItem.getPropertyValue("description") != null) {
						pRequest.setParameter("promoName", (String) promoItem.getPropertyValue("displayName")
								+ (String) promoItem.getPropertyValue("description"));
					} else {
						pRequest.setParameter("promoName", (String) promoItem.getPropertyValue("displayName"));
					}
					pRequest.setParameter("error", "err_coupon_grant_error");
					pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
				}
			}
		} else if (coupons != null && !coupons.isEmpty() && !appliedCoupons.isEmpty()) {
			for (String coupon : coupons) {
				RepositoryItem couponItem = null;
				try {
					couponItem = getClaimableManager().claimItem(coupon);
				} catch (ClaimableException e) {
					vlogError("TBSCouponsNotAppliedDroplet.service: not a store coupon", e);
				}
				if (!appliedCoupons.contains(couponItem)) {
					if (null != couponItem) {
						pRequest.setParameter("promoName", couponItem.getPropertyValue("displayName"));
						pRequest.setParameter("error", "err_coupon_grant_error");
						pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
					} else {
						RepositoryItem promoItem = couponMap.get(coupon);
						if (promoItem.getPropertyValue("description") != null) {
							pRequest.setParameter("promoName", (String) promoItem.getPropertyValue("displayName")
									+ (String) promoItem.getPropertyValue("description"));
						} else {
							pRequest.setParameter("promoName", (String) promoItem.getPropertyValue("displayName"));
						}
						pRequest.setParameter("error", "err_coupon_grant_error");
						pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
					}
				}
			}
		}
		order.getTbsCouponsMap().clear();
		vlogDebug("TBSCouponsNotAppliedDroplet.service: Ends");
	}
}
