package com.bbb.commerce.catalog;

import java.util.ArrayList;
import java.util.List;

import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.NamedQueryView;
import atg.repository.ParameterSupportView;
import atg.repository.Query;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

@SuppressWarnings("serial")
public class IndexProduct extends RepositoryPropertyDescriptor {

	private MutableRepository catalogRepository;

	@SuppressWarnings("unchecked")
	public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
		
		//Index all lead and collection products,  don't check for like_unlike flag
		if ((Boolean)pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)
				|| (Boolean)pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)) {
			return true;
		}
		boolean indexProduct = false;
		final RepositoryItem[] childProductsRelations = this.executeConfigNamedRQLQuery(
															BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID,
															new Object[]{pItem.getRepositoryId()},
															BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR);

		//Index all simple products, don't check for like_unlike flag
		if ((childProductsRelations == null)
				|| childProductsRelations.length == 0) {
			return true;
		}
		for (RepositoryItem childProdRelnItem: childProductsRelations) {
 
			if(isChildOfALeadProduct(childProdRelnItem)
					|| !(Boolean)childProdRelnItem.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME)) {
				indexProduct = true;
				break;
			}
		}
		return indexProduct;
	}
	
	private boolean isChildOfALeadProduct(RepositoryItem productRelationItem) {
		
		//If product is a child of Lead product its an accessory product. Don't check for like_unlike flag for such products
		String productRelationId = productRelationItem.getRepositoryId();
		String productId = productRelationId.substring(0, productRelationId.indexOf("_"));
		RepositoryItem productRepositoryItem = null;
		
		try {
			productRepositoryItem = getCatalogRepository().getItem(productId,
														BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		if(productRepositoryItem != null 
				&& (Boolean)productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)) {
			return true;
		}
		return false;
	}

	public RepositoryItem[] executeConfigNamedRQLQuery(final String pQueryName,
			final Object[] pParams, final String pViewName) {
	
		RepositoryItem[] repositoryItems = null;
		try {
			
			final NamedQueryView view = (NamedQueryView)this.getCatalogRepository().getView(pViewName);
			if (view != null)
			{
				final Query query = view.getNamedQuery(pQueryName);
				if (query != null) {
					repositoryItems = extractDBCall(pParams, view, query);
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return repositoryItems;
	}

	/**
	 * @param pParams
	 * @param view
	 * @param query
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] extractDBCall(final Object[] pParams, final NamedQueryView view, final Query query)
			throws RepositoryException {
		return ((ParameterSupportView) view).executeQuery(query, pParams);
	}

	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return (Repository) Nucleus.getGlobalNucleus()
				.resolveName("/atg/commerce/catalog/ProductCatalog");
	}
}
