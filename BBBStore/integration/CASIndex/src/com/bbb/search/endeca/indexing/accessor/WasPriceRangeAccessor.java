package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.indexing.accessor.helper.PriceAccessorHelper;
import com.bbb.search.endeca.indexing.accessor.helper.PriceRangeAccessorHelper;


public class WasPriceRangeAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	private PriceRangeAccessorHelper priceRangeHelper;
	private PriceAccessorHelper accessorHelper = null;
	 
	protected Object getTextOrMetaPropertyValue(Context pContext,RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "WasPriceRangeAccessor");	
		String siteId="";
		
		if(null!=pContext){
			siteId = (String) pContext.getAttribute("atg.siteVariantProdcer.siteValue");				 
			
		}	
		String salePriceListId = getAccessorHelper().getSalePriceListIdBySiteId(siteId);				
		String updatedDesc=getPriceRangeHelper().getUpdatedPriceRangeDesc(pItem, salePriceListId);
		if(isLoggingDebug()){
			logDebug("WasPriceRangeAccessor updatedDesc\t="+updatedDesc);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "WasPriceRangeAccessor");
		return updatedDesc;
	}
	
	// Getter for PriceRangeAccessorHelper
	public PriceRangeAccessorHelper getPriceRangeHelper() {
		return priceRangeHelper;
	}

	// Setter for PriceRangeAccessorHelper
	public void setPriceRangeHelper(PriceRangeAccessorHelper priceRangeHelper) {
		this.priceRangeHelper = priceRangeHelper;
	}
	
	public PriceAccessorHelper getAccessorHelper() {
		return accessorHelper;
	}

	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
}
