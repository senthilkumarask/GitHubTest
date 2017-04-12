package com.bbb.search.endeca.indexing.accessor;


import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.search.endeca.indexing.accessor.helper.IndexingHelper;

public class ChildProductAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	private IndexingHelper accessorHelper=null;
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String childProduct = null;

		if (pItem != null) {
			childProduct = getAccessorHelper().getChildProduct(pItem);
		}
		return childProduct;
		
	
	}
	/**
	 * @return the accessorHelper
	 */
	public IndexingHelper getAccessorHelper() {
		return accessorHelper;
	}
	/**
	 * @param accessorHelper the accessorHelper to set
	 */
	public void setAccessorHelper(IndexingHelper accessorHelper) {
		this.accessorHelper = accessorHelper;
	}
}
  
		  
		  