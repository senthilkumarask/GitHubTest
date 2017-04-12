package com.bbb.search.endeca.indexing.accessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.search.endeca.indexing.accessor.helper.InventoryPropertyAccessorHelper;

import atg.commerce.endeca.index.dimension.CategoryNodePropertyAccessor;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */

public class L2CategoryTypeAHeadPropertyAccessor extends CategoryNodePropertyAccessor {

	private InventoryPropertyAccessorHelper inventoryHelper;

	private List<String> exculdedL2Category;

	private BBBCatalogTools bbbCatalogTools;
	
	private MutableRepository catalogRepository;
	
	
	@Override
	protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName,
			PropertyTypeEnum pType) {
		
		String categoryId=pItem.getRepositoryId();
		if(getExculdedL2Category().contains(categoryId)){
			return null;
		}
		
		String[] siteIdsFromRepoItem = null;
		Collection<String> siteContextValues;
		try {
			siteContextValues = pItem.getContextMemberships();
			 if ((siteContextValues != null) && (siteContextValues.size() > 0)) {
		          Set<String> ids = new HashSet<String>(siteContextValues);
		          ids.retainAll(pContext.getIndexInfo().getSiteIndexInfo().getSiteIDs());
		          siteIdsFromRepoItem = (String[])ids.toArray(new String[ids.size()]);
		        }
		        
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("error while fetching siteId from context for baseline L2Category "+e,e);
			}
		}
		
		String siteId=null;
		if(siteIdsFromRepoItem.length==0){
			siteId=(String) pContext.getAttribute("atg.siteVariantProdcer.siteValue");
		}
		else {
		siteId= siteIdsFromRepoItem[0];//(String) pContext.getAttribute("atg.siteVariantProdcer.siteValue");
		}
		if(null==siteId){
			return null;
		}
		String siteNumber="1";
		if(siteId.equalsIgnoreCase("BedBathUS")){
			siteNumber="1";
		}
		else if(siteId.equalsIgnoreCase("BuyBuyBaby")){
			siteNumber="2";
		}
		else if(siteId.equalsIgnoreCase("BedBathCanada")){
			siteNumber="3";
		}
		try {
			RepositoryItem categoryRepositoryItem = getCatalogRepository().getItem(categoryId,
			        BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
			if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
                 Boolean isPhantomCategory=(Boolean) categoryRepositoryItem.getPropertyValue("phantomCategory");
                 if(isPhantomCategory){
                	 return null;
                 }
            }
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError("Error while checking PhantomCategory  "+e,e);
			}
		}
		
		Object value = super.getTextOrMetaPropertyValue(pContext, pItem, pPropertyName, pType);
		String parentProducts=(String)value.toString();
		String splitParentProducts[] = parentProducts.split("\\.");
		String categoryL2=null;
		String categoryL1=null;
		if(splitParentProducts.length==1){
			return value;
		}
		else if(splitParentProducts.length==3){
			return value;
		}
		else if(splitParentProducts.length==2){
			categoryL1=splitParentProducts[0];
			categoryL2=splitParentProducts[1];
		}
		
		if(pPropertyName.equalsIgnoreCase("spec") || pPropertyName.equalsIgnoreCase("Endeca.Id")){
			return siteNumber+categoryL1+":"+siteNumber+categoryL2;
		}
		else{
			return categoryL2;
		}
		 
		
		}



	/**
	 * @return the inventoryHelper
	 */
	public InventoryPropertyAccessorHelper getInventoryHelper() {
		return inventoryHelper;
	}



	/**
	 * @param inventoryHelper the inventoryHelper to set
	 */
	public void setInventoryHelper(InventoryPropertyAccessorHelper inventoryHelper) {
		this.inventoryHelper = inventoryHelper;
	}



	/**
	 * @return the exculdedL2Category
	 */
	public List<String> getExculdedL2Category() {
		return exculdedL2Category;
	}



	/**
	 * @param exculdedL2Category the exculdedL2Category to set
	 */
	public void setExculdedL2Category(List<String> exculdedL2Category) {
		this.exculdedL2Category = exculdedL2Category;
	}



	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}



	/**
	 * @param bbbCatalogTools the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}



	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}



	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	
	 

}
