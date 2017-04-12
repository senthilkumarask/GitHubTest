package com.bbb.commerce.claimable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

public class TBSClaimableManager extends ClaimableManager {

	/**
	 * This method is used to get the store coupons based on the store Id
	 * @param pStoreId
	 * @return
	 * @throws RepositoryException 
	 */
	public List<RepositoryItem> availableCoupons(String pStoreId) throws RepositoryException {
		vlogDebug("TBSClaimableManager :: availableCoupons() :: START " );
		TBSClaimableTools claimableTools = (TBSClaimableTools) getClaimableTools();
		if(StringUtils.isBlank(pStoreId)){
			return null;
		}
		List<RepositoryItem> storeCoupons = claimableTools.availableStoreCoupons(pStoreId);
		vlogDebug("TBSClaimableManager :: availableCoupons() :: END " );
		return storeCoupons;
	}
	
	/**
	 * This method is used to get the promotions from the coupons
	 * @param pCouponCodes
	 * @return
	 * @throws ClaimableException 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public Map<String,List<RepositoryItem>> fetchCouponPromotions(Map<String,List<RepositoryItem>> pPromos, String[] pCouponCodes) throws ClaimableException {

		vlogDebug("TBSClaimableManager :: fetchCouponPromotions() :: START " );
		for (String couponcode : pCouponCodes) {
			if(couponcode != null){
				RepositoryItem coupon;
				coupon = findAndClaimCoupon(couponcode);
			
				String promotionsName = getClaimableTools().getPromotionsPropertyName();

				vlogDebug("Promotion property name:{0}.", new Object[] { promotionsName });

				PromotionTools promotionTools = getPromotionTools();

				Set multiplePromotions = (Set) coupon.getPropertyValue(promotionsName);

				if (multiplePromotions != null) {
					vlogDebug("Multiple promotions for this coupon.", new Object[0]);

					Iterator it = multiplePromotions.iterator();

					List<RepositoryItem> promoItems = null;
					while (it.hasNext()) {
						RepositoryItem repositoryItemPromotion = (RepositoryItem) it.next();
						Set<RepositoryItem> sites = (Set<RepositoryItem>)repositoryItemPromotion.getPropertyValue("sites");
						if(sites != null){
							for (RepositoryItem siteItem : sites) {
								if(siteItem.getRepositoryId().equals(SiteContextManager.getCurrentSiteId())){
									if(pPromos.get(couponcode) == null){
										promoItems = new ArrayList<RepositoryItem>();
										promoItems.add(repositoryItemPromotion);
										pPromos.put(couponcode, promoItems);
									} else {
										promoItems = pPromos.get(couponcode);
										promoItems.add(repositoryItemPromotion);
										pPromos.put(couponcode, promoItems);
									}
								}
							}
						}
						else{
							if(pPromos.get(couponcode) == null){
								promoItems = new ArrayList<RepositoryItem>();
								promoItems.add(repositoryItemPromotion);
								pPromos.put(couponcode, promoItems);
							} else {
								promoItems = pPromos.get(couponcode);
								promoItems.add(repositoryItemPromotion);
								pPromos.put(couponcode, promoItems);
							}
						}
					}
				}
			} 
		}
		vlogDebug("TBSClaimableManager :: fetchCouponPromotions() :: END " );
		return pPromos;
	}
	
}
