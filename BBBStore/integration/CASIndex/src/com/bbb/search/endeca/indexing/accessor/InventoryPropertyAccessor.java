package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.search.endeca.indexing.accessor.helper.InventoryPropertyAccessorHelper;

public class InventoryPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	private InventoryPropertyAccessorHelper inventoryHelper;

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		int totalQty = 0;
		String siteId = (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");
		if (pItem != null) {
			totalQty = getInventoryHelper().getInventoryProperty(pItem, siteId);
		}
		return totalQty;

	}

	public InventoryPropertyAccessorHelper getInventoryHelper() {
		return inventoryHelper;
	}

	public void setInventoryHelper(InventoryPropertyAccessorHelper inventoryHelper) {
		this.inventoryHelper = inventoryHelper;
	}
}
