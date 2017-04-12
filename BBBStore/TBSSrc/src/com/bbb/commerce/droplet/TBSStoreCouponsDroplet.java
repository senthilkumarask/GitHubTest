package com.bbb.commerce.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.claimable.TBSClaimableManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * 
 * This droplet is used to fetch the store coupons from the ATG repository
 *
 */
public class TBSStoreCouponsDroplet extends DynamoServlet {
	
	private static final String COUPONS_FROM_CONFIG_KEY = "couponsFromConfigKey";
	private static final String PROMOTION_GROUP = "Promotion_Group";
	/**
	 * mClaimableManager to hold TBSClaimableManager
	 */
	private TBSClaimableManager mClaimableManager;
	
	
	private BBBCatalogTools catalogTools;
	
	/**
	 * @return the claimableManager
	 */
	public TBSClaimableManager getClaimableManager() {
		return mClaimableManager;
	}


	/**
	 * @param pClaimableManager the claimableManager to set
	 */
	public void setClaimableManager(TBSClaimableManager pClaimableManager) {
		mClaimableManager = pClaimableManager;
	}

	/**
	 * This method is iverriden to get the store coupons  
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSStoreCouponsDroplet :: service() method :: START");
		
		String storeId = (String) pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		if(storeId==null){
			Cookie[] requestCookies = pRequest.getCookies();
			if (requestCookies != null) {
				 for (Cookie cookie : requestCookies) {
				   if (cookie.getName().equals("store_number")) {
					   storeId = cookie.getValue();
				    }
				  }
				}
		}
		BBBOrderImpl order = (BBBOrderImpl) pRequest.getObjectParameter(BBBCoreConstants.ORDER); 
		Map<RepositoryItem, Boolean> couponsStatus = new LinkedHashMap<RepositoryItem, Boolean>();
		Map<RepositoryItem, Boolean> storeCouponsMap = new LinkedHashMap<RepositoryItem, Boolean>();
		Map<RepositoryItem, Boolean> configCouponMap = new LinkedHashMap<RepositoryItem, Boolean>();
		List<RepositoryItem> appliedStatus = new ArrayList<RepositoryItem>();
		
		try {
			//get all the coupons applied to order.
			List<RepositoryItem> appliedCoupons=((BBBPromotionTools)getClaimableManager().getPromotionTools()).getCouponListFromOrder(order);
			
			List<String> couponsConfigKeys = new ArrayList<String>();
			Map<String, String> couponConfigValues = null;
			try {
				couponConfigValues = this.getCatalogTools().
						getConfigValueByconfigType(BBBCoreConstants.CART_AND_CHECKOUT_KEYS);
			} catch (Exception e) {
				logError("Exception occured while fetching store coupon from CartAndCheckoutKeys: ", e);
			}
			Map<String, String> sortedCouponConfigValues = new TreeMap<>(couponConfigValues);
			
			for (Map.Entry<String, String> entry : sortedCouponConfigValues.entrySet()) {
			    if (entry.getKey().contains(PROMOTION_GROUP)) {
			    	try {
			    		if(!BBBUtility.isListEmpty(this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CART_AND_CHECKOUT_KEYS , entry.getKey()))){
			    			couponsConfigKeys.addAll(this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.CART_AND_CHECKOUT_KEYS , entry.getKey()));
			    		}
			    	}  catch (Exception e) {
						logError("Exception occured while fetching store coupon from Key :" +  entry.getKey(), e);
					}
			    }
			}
			
			List<String> appliedStoreCoupons = new ArrayList<String>();
			Map<String, RepositoryItem> storeCoupons = new HashMap<String, RepositoryItem>();
			Map<String, String> couponsFromConfigKey = new HashMap<String, String>();
			//get the store coupons
			List<RepositoryItem> coupons = getClaimableManager().availableCoupons(storeId);
			
			//get the promotions from the coupon.
			String promotionsName = getClaimableManager().getClaimableTools().getPromotionsPropertyName();
			if (coupons == null || coupons.size() <= TBSConstants.ZERO) {
				vlogDebug("TBSStoreCouponsDroplet.service: There is no coupon configured for store id: {0}", storeId);
				pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
				vlogDebug("TBSStoreCouponsDroplet :: service() method :: END");
				return;
			}
			for (RepositoryItem coupon : coupons) {
				Set<RepositoryItem> multiplePromotions = (Set<RepositoryItem>) coupon.getPropertyValue(promotionsName);
				if (multiplePromotions != null && !multiplePromotions.isEmpty()) {
					Iterator<RepositoryItem> promoIterator = multiplePromotions.iterator();
					RepositoryItem promotion = promoIterator.next();
					Set<RepositoryItem> sites = (Set<RepositoryItem>) promotion
							.getPropertyValue(BBBCoreConstants.SITES);
					boolean isPromoEnable = (Boolean) promotion.getPropertyValue(BBBCoreConstants.ENABLED);
					if (sites == null || !isPromoEnable) {
						vlogDebug("TBSStoreCouponsDroplet.service: Sites for promotion {0} is not configured.",
								promotion);
						continue;
					}
					for (RepositoryItem siteItem : sites) {
						if (siteItem.getRepositoryId().equals(SiteContextManager.getCurrentSiteId())) {
							storeCoupons.put(coupon.getRepositoryId(), coupon);
							if (appliedCoupons.contains(coupon)) {
								appliedStatus.add(coupon);
								appliedStoreCoupons.add(coupon.getRepositoryId());
							}
							storeCouponsMap.put(coupon, false);
						}
					}
				}
			}

			for (String coupon : couponsConfigKeys) {
				if (!BBBUtility.isEmpty(coupon)) {
					boolean couponsRemaining = false;
					String couponRepoId = BBBCoreConstants.EMPTY;
					vlogDebug("Coupons configured in config keys: " + coupon);
					String[] couponsConfigKey = coupon.split(BBBCoreConstants.COLON);
					
					List<String> couponsConfigKeyList = new ArrayList<String>();
					couponsConfigKeyList.addAll(Arrays.asList(couponsConfigKey));
					StringBuffer colonSeparatedRepoId = new StringBuffer();

					for (Iterator<String> iterator = couponsConfigKeyList.iterator(); iterator.hasNext();) {
						String couponId = (String) iterator.next();
						if (storeCoupons.containsKey(couponId)) {
							storeCouponsMap.remove(storeCoupons.get(couponId));
							colonSeparatedRepoId.append(BBBCoreConstants.COLON).append(couponId);
						} else {
							iterator.remove();
							logError("Invalid coupon code defined in promotion groups :" + couponId);
						}
					}
					
					if (colonSeparatedRepoId.length() > 0) {
						colonSeparatedRepoId.deleteCharAt(0);
					} 
					
					for (String couponId : couponsConfigKeyList) {
						if (!appliedStoreCoupons.contains(couponId)) {
							RepositoryItem couponItem = storeCoupons.get(couponId);
							configCouponMap.put(couponItem, false);
							couponRepoId = couponId;
							couponsRemaining = true;
							break;
						}
					}
					
					if (!couponsRemaining && couponsConfigKeyList.size() > 0) {
						String couponId = couponsConfigKeyList.get(couponsConfigKeyList.size()-1);
						RepositoryItem couponItem = storeCoupons.get(couponId);
						configCouponMap.put(couponItem, false);
						couponRepoId = couponId;
					}
					
					couponsFromConfigKey.put(couponRepoId, colonSeparatedRepoId.toString());
				}
			}
			couponsStatus.putAll(configCouponMap);
			couponsStatus.putAll(storeCouponsMap);
			
			if(couponsStatus != null && couponsStatus.size() > TBSConstants.ZERO){
				pRequest.setParameter("siteCoupons", couponsStatus);
				pRequest.setParameter("appliedCoupons", appliedStatus);
				pRequest.setParameter(COUPONS_FROM_CONFIG_KEY, couponsFromConfigKey);
				pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
				vlogDebug("TBSStoreCouponsDroplet.service: Available coupons for store id: {0} are {1} and applied coupons list is: {2}", storeId,couponsStatus,appliedStatus);
			} else {
				pRequest.serviceLocalParameter(TBSConstants.EMPTY, pRequest, pResponse);
				vlogDebug("TBSStoreCouponsDroplet.service: There is no promotions configured for any of the coupons available for this store id: {0}",storeId);
			}
		} catch (RepositoryException e) {
			vlogError("TBSStoreCouponsDroplet.service: RepositoryException occurred while getting store coupons");
			pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
		}
		
		vlogDebug("TBSStoreCouponsDroplet :: service() method :: END");
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
