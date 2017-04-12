package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.search.endeca.indexing.accessor.helper.InventoryPropertyAccessorHelper;

public class InventoryDimensionAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	private InventoryPropertyAccessorHelper inventoryHelper;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String siteId = (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");
		String dimension = null;
		if (pItem != null) {
			int totalQty = getInventoryHelper().getInventoryProperty(pItem, siteId);
			if (totalQty==0){
				dimension="Zero";
			}else {
				dimension="Positive";
			}
		}
		return dimension;

	}

	public InventoryPropertyAccessorHelper getInventoryHelper() {
		return inventoryHelper;
	}

	public void setInventoryHelper(InventoryPropertyAccessorHelper inventoryHelper) {
		this.inventoryHelper = inventoryHelper;
	}
}
