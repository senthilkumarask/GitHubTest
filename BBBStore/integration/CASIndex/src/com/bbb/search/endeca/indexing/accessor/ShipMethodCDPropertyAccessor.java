package com.bbb.search.endeca.indexing.accessor;

import java.util.Set;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;

public class ShipMethodCDPropertyAccessor extends PropertyAccessorImpl implements
		IndexConstants {
	@SuppressWarnings("unchecked")
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String shipMethod = null;

		if (pItem != null) {
			Set<RepositoryItem> shipMethodSet = (Set<RepositoryItem>) pItem.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME);
			if (shipMethodSet != null){
				for (RepositoryItem shipMethodIds : shipMethodSet) {
					shipMethod = shipMethodIds.getRepositoryId();
					return shipMethod;
				}
			}
		}
		return null;
	}
}
