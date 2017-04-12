package com.bbb.search.endeca.indexing.accessor;

import java.text.DecimalFormat;

import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.indexing.accessor.helper.AccessorConstants;
import com.bbb.search.endeca.indexing.accessor.helper.PriceAccessorHelper;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class SortSkuLowPriceMxPropertyAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	private PriceAccessorHelper accessorHelper=null;
	
	protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SortSkuLowPriceMxPropertyAccessor");
		Double salePrice=null;
		if(isLoggingDebug()){
			logDebug("RepositoryItem (Expecting product repository item)\t:"+pItem);		
		}	 
		
		if(null!=pItem){
			String salePriceListId = AccessorConstants.MEXICAN_SALE_PRICE_LIST_ID;
			String listPriceListId = AccessorConstants.MEXICAN_LIST_PRICE_LIST_ID;
			if (isLoggingDebug()) {				
				logDebug("listPriceListId|salePriceListId retrieved is:\t"+listPriceListId+"|"+salePriceListId);
				}
			
			salePrice=getAccessorHelper().getSalePrice(pItem, AccessorConstants.SKU_LOW_PRICE_PROPERTY_NAME, listPriceListId,salePriceListId);			
			//listPrice = getAccessorHelper().getMexicanSkuLowPrice(pItem, AccessorConstants.SKU_LOW_PRICE_PROPERTY_NAME,AccessorConstants.MEXICAN_LIST_PRICE_LIST_ID);
		}		
		if(null!=salePrice){
			try {
				DecimalFormat formatIt=new DecimalFormat(AccessorConstants.DECIMAL_PATTERN);				
				return formatIt.format(salePrice);	
			} catch (ClassCastException e) {
				return salePrice;
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SortSkuLowPriceMxPropertyAccessor");
		return salePrice;
	}

	public PriceAccessorHelper getAccessorHelper() {
		return accessorHelper;
	}

	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
	
}
