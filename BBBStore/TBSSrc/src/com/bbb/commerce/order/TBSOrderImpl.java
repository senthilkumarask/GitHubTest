package com.bbb.commerce.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.repository.RepositoryItem;

import com.bbb.ecommerce.order.BBBOrderImpl;

public class TBSOrderImpl extends BBBOrderImpl implements TBSOrder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Boolean> mOverridePriceMap = new HashMap<String, Boolean>();
	
	private Map<String, RepositoryItem> mTbsCouponsMap = new HashMap<String, RepositoryItem>();
	private List<RepositoryItem> mAvailablePromotionsList = new ArrayList<RepositoryItem>();

	@Override
	public Map<String, Boolean> getOverridePriceMap() {
		return mOverridePriceMap;
	}

	@Override
	public void setOverridePriceMap(Map<String, Boolean> pOverridePriceMap) {
		mOverridePriceMap = pOverridePriceMap;
	}

	/**
	 * @return the tbsCouponsMap
	 */
	public Map<String, RepositoryItem> getTbsCouponsMap() {
		return mTbsCouponsMap;
	}

	/**
	 * @param pTbsCouponsMap the tbsCouponsMap to set
	 */
	public void setTbsCouponsMap(Map<String, RepositoryItem> pTbsCouponsMap) {
		mTbsCouponsMap = pTbsCouponsMap;
	}

	/**
	 * @return the availablePromotionsList
	 */
	public List<RepositoryItem> getAvailablePromotionsList() {
		return mAvailablePromotionsList;
	}

	/**
	 * @param pAvailablePromotionsList the availablePromotionsList to set
	 */
	public void setAvailablePromotionsList(
			List<RepositoryItem> pAvailablePromotionsList) {
		mAvailablePromotionsList = pAvailablePromotionsList;
	}

}
