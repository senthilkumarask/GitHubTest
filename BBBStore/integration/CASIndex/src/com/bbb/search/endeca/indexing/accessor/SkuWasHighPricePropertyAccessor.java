package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.indexing.accessor.helper.AccessorConstants;
import com.bbb.search.endeca.indexing.accessor.helper.PriceAccessorHelper;

public class SkuWasHighPricePropertyAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	private PriceAccessorHelper accessorHelper=null; 	
	
	protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {		
			
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SkuWasHighPricePropertyAccessor");
		Double listPrice = null;
		String siteId="";

		if(null!=pContext){
			siteId = (String) pContext.getAttribute("atg.siteVariantProdcer.siteValue");			
		}
		
		if(null!=pItem){	
			String priceListId = getAccessorHelper().getListPriceListIdBySiteId(siteId);
			listPrice = getAccessorHelper().getWasSkuHighPrice(pItem, AccessorConstants.SKU_HIGH_PRICE_PROPERTY_NAME, priceListId);
			if (isLoggingDebug()) {
				logDebug("\nFor SKU_HIGH_PRICE...SiteID:\t"+siteId+"\nProductID:\t"+pItem.getRepositoryId()+"\nPrice_ListID US(plist100004) or Canada(plist100003):\t"+priceListId+"\nList Price Retrieved:\t"+listPrice);
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SkuWasHighPricePropertyAccessor");
		return listPrice;
	}

	public PriceAccessorHelper getAccessorHelper() {
		return accessorHelper;
	}

	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
}
