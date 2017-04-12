package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * 
 * @author schalla
 *
 */

// SN33

public class ProductSiteIDAccessor extends PropertyAccessorImpl implements
		IndexConstants {

	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String ProductSiteID = null;
		if (null != pItem) {
			try {
				String siteId = (String) pItem.getPropertyValue("siteIds");
				String productId = (String) pItem.getRepositoryId();
				if (isLoggingDebug()) {
					logDebug("Product Id =" + pItem.getRepositoryId());
					logDebug("Id=" + siteId);
					ProductSiteID = siteId+"_" +productId;
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		return ProductSiteID;
	}

}
