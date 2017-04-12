package com.bbb.search.endeca.indexing.mieaccessor;

import com.bbb.search.endeca.indexing.accessor.helper.AccessorConstants;
import com.bbb.search.endeca.indexing.accessor.helper.PriceAccessorHelper;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class MieCAIsPricePropertyAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	private PriceAccessorHelper accessorHelper=null;
	
	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		Double listPrice=null;
		if(isLoggingDebug()){
			logDebug("SKU RepositoryItem \t:"+pItem);		
		}	 
		
		if(null!=pItem){
			listPrice = getAccessorHelper().getMieCAListPrice(pItem, AccessorConstants.CA_SALE_PRICE_LIST_ID);
		}
		return listPrice;
	}

	public PriceAccessorHelper getAccessorHelper() {
		return accessorHelper;
	}

	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
	
}
