package com.bbb.commerce.order;

import java.util.List;
import java.util.Map;

import atg.repository.RepositoryItem;

import com.bbb.ecommerce.order.BBBOrder;

public interface TBSOrder extends BBBOrder {

	public Map<String, Boolean> getOverridePriceMap();

	public void setOverridePriceMap(Map<String, Boolean> pOverridePriceMap);
	
	public Map<String, RepositoryItem> getTbsCouponsMap();
	public void setTbsCouponsMap(Map<String, RepositoryItem> pTbsCouponsMap);
	
	public List<RepositoryItem> getAvailablePromotionsList();
	public void setAvailablePromotionsList(List<RepositoryItem> pAvailablePromotionsList);
}
