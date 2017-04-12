package com.bbb.search.endeca.indexing.accessor;

import java.util.List;

import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;

public class HarmonTabContentPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	private MutableRepository catalogRepository;

	@SuppressWarnings("unchecked")
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {

		String tabContent = null;
		String productID = null;

		if (pItem != null) {
			try {
				productID = pItem.getRepositoryId();
				RepositoryItem productRepositoryItem = catalogRepository
						.getItem(productID,
								BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				List<RepositoryItem> productTabsRepositoryItemsList = (List<RepositoryItem>) productRepositoryItem
						.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME);
				if (productTabsRepositoryItemsList != null) {
					for (int index = 0; index < productTabsRepositoryItemsList
							.size(); index++) {
						final RepositoryItem tabRepositoryItem = productTabsRepositoryItemsList
								.get(index);
						tabContent = (String) tabRepositoryItem
								.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME);
						  if (isLoggingDebug()) {logDebug("tabCon********* \t:"
						  +tabContent);}
						 }
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tabContent;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param pCatalogRepository
	 *            the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
}
