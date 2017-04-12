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


/**
 * <p>
 * The Property Accessor for retrieving highest LIST_PRICE from DCS_PRICE Table(for the SKU_ID & PRICE_LIST_ID(MexicoListPrice) to a given PRODUCT_ID)
 * </p>
 * @author dkhadka
 *
 */

//SNO:25 & 136

public class SkuHighPriceMxPropertyAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	private PriceAccessorHelper accessorHelper=null;
 
	
	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SkuHighPriceMxPropertyAccessor");
		Double listPrice=null;
		if(isLoggingDebug()){
			logDebug("RepositoryItem (Expecting product repository item)\t:"+pItem);			
			logDebug("getSiteIDs=:::"+pContext.getIndexInfo().getSiteIndexInfo().getSiteIDs());
		}	 
		
		if(null!=pItem){
			listPrice = getAccessorHelper().getMexicanSkuHighPrice(pItem, AccessorConstants.SKU_HIGH_PRICE_PROPERTY_NAME,AccessorConstants.MEXICAN_LIST_PRICE_LIST_ID);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "SkuHighPriceMxPropertyAccessor");
		return listPrice;
	}
			
	public PriceAccessorHelper getAccessorHelper() {
		return accessorHelper;
	}

	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
	
}
