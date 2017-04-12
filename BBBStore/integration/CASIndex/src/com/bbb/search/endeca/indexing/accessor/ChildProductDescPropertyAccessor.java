package com.bbb.search.endeca.indexing.accessor;


import java.util.List;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.search.endeca.indexing.accessor.helper.IndexingHelper;

/**
 * 
 * @author sc0151
 *
 */

public class ChildProductDescPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {
	private IndexingHelper accessorHelper=null;
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String childProduct = null;

		if (pItem != null) {
			childProduct = getAccessorHelper().getChildDescProperty(pItem);
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
  
		  
		  