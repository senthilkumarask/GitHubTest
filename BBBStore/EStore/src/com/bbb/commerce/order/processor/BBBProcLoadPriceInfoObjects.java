/**
 * 
 */
package com.bbb.commerce.order.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcLoadPriceInfoObjects;
import atg.commerce.pricing.TaxPriceInfo;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/**
 * @author Pradeep Reddy
 *
 */
public class BBBProcLoadPriceInfoObjects extends ProcLoadPriceInfoObjects {
	

	private String shippingItemsTaxPriceInfosPropertyName;
	
	protected void loadTaxPriceInfo(Order order, MutableRepositoryItem orderItem, OrderManager orderManager, Boolean invalidateCache)
	        throws Exception
	    {	
		
			superLoadTaxPriceInfo(order, orderItem, orderManager, invalidateCache);
			MutableRepositoryItem tpiRepItem = null;
			Map shipTaxMapRepoItem = null;
			Map itemShipTaxMapRepoItem = null;
			Set<String> shippingGroupIds = null;
			Set<String> commerceItemIds = null;
			TaxPriceInfo itemTaxPriceInfo = null;
			RepositoryItem shipTaxPriceInfoRepItem = null;
			RepositoryItem itemShipTaxPriceInfoRepItem = null;
			
			tpiRepItem = (MutableRepositoryItem)orderItem.getPropertyValue(getTaxPriceInfoProperty());
			if (tpiRepItem != null) {
				shipTaxMapRepoItem = (Map) tpiRepItem.getPropertyValue(getShippingItemsTaxPriceInfosPropertyName());
				
				if (shipTaxMapRepoItem != null) {
					shippingGroupIds = shipTaxMapRepoItem.keySet();
					for (String shippingId : shippingGroupIds) {
						Map<String, TaxPriceInfo> itemShipTaxMap = new HashMap<String, TaxPriceInfo>();
						shipTaxPriceInfoRepItem = (RepositoryItem) shipTaxMapRepoItem.get(shippingId);
						if (shipTaxPriceInfoRepItem != null) {
							itemShipTaxMapRepoItem = (Map) shipTaxPriceInfoRepItem.getPropertyValue(getShippingItemsTaxPriceInfosPropertyName());
							commerceItemIds = itemShipTaxMapRepoItem.keySet();
							for (String commerceItemId : commerceItemIds) {
								itemTaxPriceInfo = new TaxPriceInfo();
								itemShipTaxPriceInfoRepItem = (RepositoryItem) itemShipTaxMapRepoItem.get(commerceItemId);
								itemTaxPriceInfo = readTaxPriceInfoProperties(itemTaxPriceInfo, itemShipTaxPriceInfoRepItem);
								itemShipTaxMap.put(commerceItemId, itemTaxPriceInfo);
							}							
						}
						TaxPriceInfo shipTaxPriceInfo = (TaxPriceInfo) order.getTaxPriceInfo().getShippingItemsTaxPriceInfos().get(shippingId);
						shipTaxPriceInfo.setShippingItemsTaxPriceInfos(itemShipTaxMap);
					}					
				}
			}
		
	    }
	public void superLoadTaxPriceInfo(Order order, MutableRepositoryItem orderItem, OrderManager orderManager, Boolean invalidateCache) throws Exception{
		super.loadTaxPriceInfo(order, orderItem, orderManager, invalidateCache);
	}
	protected TaxPriceInfo readTaxPriceInfoProperties(TaxPriceInfo tpi, RepositoryItem tpiRepoItem) {
		
		tpi.setAmount((Double) tpiRepoItem.getPropertyValue("amount"));
		tpi.setAmountIsFinal((Boolean) tpiRepoItem.getPropertyValue("amountIsFinal"));
		tpi.setCityTax((Double) tpiRepoItem.getPropertyValue("cityTax"));
		if(tpiRepoItem.getPropertyValue("districtTax")!=null){
			tpi.setDistrictTax((Double) tpiRepoItem.getPropertyValue("districtTax"));
		}
		tpi.setCountryTax((Double) tpiRepoItem.getPropertyValue("countryTax"));
		tpi.setCountyTax((Double) tpiRepoItem.getPropertyValue("countyTax"));
		tpi.setCurrencyCode((String) tpiRepoItem.getPropertyValue("currencyCode"));
		tpi.setDiscounted((Boolean) tpiRepoItem.getPropertyValue("discounted"));
		tpi.setFinalReasonCode((String) tpiRepoItem.getPropertyValue("finalReasonCode"));
		tpi.setShippingItemsTaxPriceInfos((Map) tpiRepoItem.getPropertyValue("shippingItemsTaxPriceInfos"));
		tpi.setStateTax((Double) tpiRepoItem.getPropertyValue("stateTax"));
		return tpi;
	}

	/**
	 * @return the shippingItemsTaxPriceInfosPropertyName
	 */
	public String getShippingItemsTaxPriceInfosPropertyName() {
		return shippingItemsTaxPriceInfosPropertyName;
	}

	/**
	 * @param shippingItemsTaxPriceInfosPropertyName the shippingItemsTaxPriceInfosPropertyName to set
	 */
	public void setShippingItemsTaxPriceInfosPropertyName(
			String shippingItemsTaxPriceInfosPropertyName) {
		this.shippingItemsTaxPriceInfosPropertyName = shippingItemsTaxPriceInfosPropertyName;
	}
}
