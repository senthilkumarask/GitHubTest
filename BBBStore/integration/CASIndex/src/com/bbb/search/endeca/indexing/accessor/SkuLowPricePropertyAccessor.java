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

public class SkuLowPricePropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	private PriceAccessorHelper accessorHelper = null;

	protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {	
		BBBPerformanceMonitor.setFlowName(BBBPerformanceConstants.PRICE_ACCESSOR_CALL);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SkuLowPricePropertyAccessor");
		Double salePrice = null;		
		String siteId="";
		if(null!=pContext){
			siteId = (String) pContext.getAttribute("atg.siteVariantProdcer.siteValue");				 	
		}			
			
		if (null != pItem) {
			String salePriceListId = getAccessorHelper().getSalePriceListIdBySiteId(siteId);
			String listPriceListId = getAccessorHelper().getListPriceListIdBySiteId(siteId);
			
			if (isLoggingDebug()) {				
				logDebug("listPriceListId|salePriceListId|siteId retrieved is:\t"+listPriceListId+"|"+salePriceListId+"|"+siteId);
				}
			
			salePrice=getAccessorHelper().getSalePrice(pItem, AccessorConstants.SKU_LOW_PRICE_PROPERTY_NAME,listPriceListId, salePriceListId);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SkuLowPricePropertyAccessor");
		return salePrice;
	}
	
	public PriceAccessorHelper getAccessorHelper() {
		return accessorHelper;
	}

	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
}
