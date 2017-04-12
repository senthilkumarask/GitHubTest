package com.bbb.search.endeca.indexing.accessor;

import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.indexing.accessor.helper.AccessorConstants;
import com.bbb.search.endeca.indexing.accessor.helper.PriceRangeAccessorHelper;

import atg.commerce.search.IndexConstants;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

//SNO:97 140

public class MXWasPriceRangeAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	private PriceRangeAccessorHelper priceRangeHelper;
	 
		
	public PriceRangeAccessorHelper getPriceRangeHelper() {
		return priceRangeHelper;
	}

	public void setPriceRangeHelper(PriceRangeAccessorHelper priceRangeHelper) {
		this.priceRangeHelper = priceRangeHelper;
	}
	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "MXWasPriceRangeAccessor");
		String priceRangeDesc=getPriceRangeHelper().getUpdatedPriceRangeDesc(pItem, AccessorConstants.MEXICAN_SALE_PRICE_LIST_ID);	;
		
		if(StringUtils.isNotEmpty(priceRangeDesc)){
			
			if(isLoggingDebug()){
				logDebug("WAS:Current priceRangeDesc is: "+priceRangeDesc);
			}
			priceRangeDesc=priceRangeDesc.replaceAll("\\$+", AccessorConstants.MEXICAN_CURRENCY);
		}
		
		if(isLoggingDebug()){
			logDebug("WAS: MXIsPriceRangeAccessor priceRangeDesc (after replacing $ with MXN) is: "+priceRangeDesc);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "MXWasPriceRangeAccessor");
		return priceRangeDesc;	
		
	}
	
}
