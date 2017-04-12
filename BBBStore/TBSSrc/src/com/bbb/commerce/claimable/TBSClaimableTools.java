package com.bbb.commerce.claimable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import atg.commerce.claimable.ClaimableTools;
import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

import com.bbb.constants.TBSConstants;

public class TBSClaimableTools extends ClaimableTools {

	/**
	 * This method is used to get all the Store coupons irrespective of storeid.
	 * @param pStoreId
	 * @return
	 * @throws RepositoryException
	 */
	public List<RepositoryItem> availableStoreCoupons(String pStoreId) throws RepositoryException {
		vlogDebug("TBSClaimableTools :: availableStoreCoupons() :: START " );
		RepositoryItem[] coupons = null;
		List<RepositoryItem> storeCoupons = null;
		if(!StringUtils.isBlank(pStoreId)){
			RepositoryItemDescriptor claimableDescriptor = getClaimableRepository().getItemDescriptor("PromotionClaimable");
			RepositoryView claimableView = claimableDescriptor.getRepositoryView();
			QueryBuilder promoQueryBuilder = claimableView.getQueryBuilder();
			
			QueryExpression storeOnlyProperty = promoQueryBuilder.createPropertyQueryExpression("storeOnly");
			QueryExpression storeOnlyValue = promoQueryBuilder.createConstantQueryExpression(1);

			Calendar calFr = Calendar.getInstance();
			calFr.set(calFr.get(Calendar.YEAR), calFr.get(Calendar.MONTH), calFr.get(Calendar.DATE), calFr.get(Calendar.HOUR_OF_DAY),(calFr.get(Calendar.MINUTE)));
			Date expirationDate =  calFr.getTime();
			
			QueryExpression expirationDateProperty = promoQueryBuilder.createPropertyQueryExpression("expirationDate");
			QueryExpression expirationDateValue = promoQueryBuilder.createConstantQueryExpression(expirationDate);
			
			QueryExpression expirationDateNullProperty = promoQueryBuilder.createPropertyQueryExpression("expirationDate");

			Query claimableStoreOnlyQuery = promoQueryBuilder.createComparisonQuery(storeOnlyProperty, storeOnlyValue, QueryBuilder.EQUALS);
			Query expirationDateQuery = promoQueryBuilder.createComparisonQuery(expirationDateProperty, expirationDateValue, QueryBuilder.GREATER_THAN_OR_EQUALS);
			Query expirationDateNullQuery = promoQueryBuilder.createIsNullQuery(expirationDateNullProperty);
			Query expDateOrQuery = promoQueryBuilder.createOrQuery(new Query[] {expirationDateQuery,expirationDateNullQuery});
				
	        Query finalQuery = promoQueryBuilder.createAndQuery(new Query[] { claimableStoreOnlyQuery, expDateOrQuery});
	        vlogDebug("TBSClaimableTools :: availableStoreCoupons():: finalQuery {0} ",finalQuery.toString());
			coupons = claimableView.executeQuery(finalQuery);
		}
		if(coupons != null && coupons.length > TBSConstants.ZERO){
			storeCoupons = storeMatchedCoupons(coupons, pStoreId);
		}
		vlogDebug("TBSClaimableTools :: availableStoreCoupons() :: END " );
		return storeCoupons;
	}

	/**
	 * This method is used for getting the store specific coupons based on the store id
	 * @param pCoupons
	 * @param pStoreId
	 * @return
	 */
	public List<RepositoryItem> storeMatchedCoupons(RepositoryItem[] pCoupons, String pStoreId){
		vlogDebug("TBSClaimableTools :: storeMatchedCoupons() :: START " );
		List<RepositoryItem> storeCoupons = new ArrayList<RepositoryItem>();
		for (RepositoryItem couponItem : pCoupons) {
			String[] storeIds = null;
			String elegibleStores = (String) couponItem.getPropertyValue("eligibleStores");
			if(!StringUtils.isBlank(elegibleStores)){
				storeIds = elegibleStores.split(",");
			}
			if(storeIds != null){
				for (String storeId : storeIds) {
					if(!StringUtils.isBlank(storeId.trim()) && (storeId.trim()).equals(pStoreId)){
						storeCoupons.add(couponItem);
					}
				}
			} else {//if no storeIds present in the coupon then applicable for all the stores.
				storeCoupons.add(couponItem);
			}
		}
		vlogDebug("Store matched promotions :: "+ storeCoupons);
		vlogDebug("TBSClaimableTools :: storeMatchedCoupons() :: END " );
		return storeCoupons;
	}
		
}
