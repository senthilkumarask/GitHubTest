/**
 * 
 */
package com.bbb.search.endeca.indexing.accessor;

import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class DefaultParentSpecPropertyAccessor extends PropertyAccessorImpl {

	@Override
	protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName,
			PropertyTypeEnum pType) {
		
		
		return "/";
	}

	 
	
	
}
