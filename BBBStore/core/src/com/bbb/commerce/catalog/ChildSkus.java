package com.bbb.commerce.catalog;

import java.util.ArrayList;
import java.util.List;

import com.bbb.utils.BBBUtility;

import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

@SuppressWarnings("serial")
public class ChildSkus extends RepositoryPropertyDescriptor {

	private MutableRepository catalogRepository;

	@SuppressWarnings("unchecked")
	public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
		final List<String> childSkusList = new ArrayList<String>();
		String productID ;
		Repository catalogRepository = (Repository) Nucleus.getGlobalNucleus()
				.resolveName("/atg/commerce/catalog/ProductCatalog");
		
		List<RepositoryItem> skuRepositoryItems;
		try {
			if (pItem != null){
				 productID = pItem.getRepositoryId();
				if(catalogRepository.getItem(productID,
						BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) != null) {

					RepositoryItem productRepositoryItem = catalogRepository
							.getItem(productID,
									BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

					final List<RepositoryItem> childProductsRelationList = (List<RepositoryItem>) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);

					skuRepositoryItems = (List<RepositoryItem>) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);

					if (!BBBUtility.isListEmpty(skuRepositoryItems)) {
						for (final RepositoryItem sku : skuRepositoryItems) {
							String skuId = sku.getRepositoryId();
							childSkusList.add(skuId);
						}

					}
					if (!BBBUtility.isListEmpty(childProductsRelationList)) {
						RepositoryItem childProdItem = null;
						for (int i = 0; i < childProductsRelationList.size(); i++) {
							childProdItem = (RepositoryItem) childProductsRelationList
									.get(i)
									.getPropertyValue(
											BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME);
							skuRepositoryItems = (List<RepositoryItem>) childProdItem
									.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
							if (skuRepositoryItems != null) {
								for (RepositoryItem sku : skuRepositoryItems) {
									String skuId = sku.getRepositoryId();
									childSkusList.add(skuId);
								}
							}
						}

					}
				}
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return childSkusList;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository
	 *            the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
}
