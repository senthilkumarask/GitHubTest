package com.bbb.search.endeca.indexing.accessor;


import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.search.endeca.indexing.accessor.helper.SalesPropertyAccessorHelper;

public class UnitSalesPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	
	private SalesPropertyAccessorHelper salesHelper;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		Object unitSale = null;
		if (pItem != null) {
			unitSale = getSalesHelper().getUnitSalesProperty(pItem);
		}
		return unitSale;}

	public SalesPropertyAccessorHelper getSalesHelper() {
		return salesHelper;
	}

	public void setSalesHelper(SalesPropertyAccessorHelper salesHelper) {
		this.salesHelper = salesHelper;
	}
}
		  