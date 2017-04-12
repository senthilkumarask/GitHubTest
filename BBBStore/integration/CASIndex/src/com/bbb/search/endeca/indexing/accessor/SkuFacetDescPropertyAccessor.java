/**
 * 
 */
package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class SkuFacetDescPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	/* (non-Javadoc)
	 * @see atg.repository.search.indexing.PropertyAccessorImpl#getTextOrMetaPropertyValue(atg.repository.search.indexing.Context, atg.repository.RepositoryItem, java.lang.String, atg.repository.search.indexing.specifier.PropertyTypeEnum)
	 */
	@Override
	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		//RepositoryItem facetItem =pContext.getParent();
		if(pItem!=null){
			String facetValue=(String) pItem.getPropertyValue("facetValue");			
			String facetDescription = (String) pItem.getPropertyValue("description");			
			return facetDescription+":"+facetValue;
			
		}
		return null;
	}
}
