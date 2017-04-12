/**
 * 
 */
package com.bbb.search.endeca.indexing.accessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.search.endeca.indexing.accessor.helper.InventoryPropertyAccessorHelper;

import atg.commerce.search.IndexConstants;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * @author sm0191
 *
 */
public class L2CategoryTypeAHeadAccessor extends PropertyAccessorImpl implements IndexConstants {
	
	

	private InventoryPropertyAccessorHelper inventoryHelper;

	private List<String> exculdedL2Category;

	private BBBCatalogTools bbbCatalogTools;
	
	private MutableRepository catalogRepository;
	
	private String categorySite;
	

	@Override
	public Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName,
			PropertyTypeEnum pType) {

		
		 String currentSiteId = (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");
		  
		  if(!currentSiteId.equalsIgnoreCase(getCategorySite())){
			  return null;
		  }
		  
		String categoryId=pItem.getRepositoryId();
		if(getExculdedL2Category().contains(categoryId)){
			return null;
		}
		  
		
		String siteNumber = "1";
		
		String siteId=currentSiteId;
		if(siteId.equalsIgnoreCase("BedBathUS")){
			siteNumber="1";
		}
		else if(siteId.equalsIgnoreCase("BuyBuyBaby")){
			siteNumber="2";
		}
		else if(siteId.equalsIgnoreCase("BedBathCanada")){
			siteNumber="3";
		}
		Map<String, String> parents = new HashMap<String, String>();
		try {

			getParent(pItem.getRepositoryId(), siteId, parents);

		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Error while fetching parent categories for category " + pItem.getRepositoryId());
			}
		}
		
		if (parents.isEmpty()) {
			return null;
		}
		
		if(pPropertyName.equalsIgnoreCase("nodeId") && !StringUtils.isBlank(parents.get("categoryL3"))){
			String value = parents.get("categoryL3");
			return value;
		}
		else 
		if(pPropertyName.equalsIgnoreCase("category_typehead") && !StringUtils.isBlank(parents.get("categoryL1")) && !StringUtils.isBlank(parents.get("categoryL2"))){
			String value = siteNumber + parents.get("categoryL1") + ":" + siteNumber +parents.get("categoryL2");
			return value;
		}
		else
			return null;
		 

	}

	/**
	 * @param categoryId
	 * @param siteId
	 * @param categoryList
	 * @throws RepositoryException
	 */
	private void getParent(String categoryId, String siteId, Map<String, String> categoryList)
			throws RepositoryException {

		 
		if (isLoggingDebug()) {
			logDebug(new StringBuffer("request Parameters value[categoryId=").append(categoryId).append("][siteId=")
					.append(siteId).append("]").toString());
		}

		final Map<String, CategoryVO> parentCategoryMap = new HashMap<String, CategoryVO>();
		RepositoryItem categoryRepositoryItem = getCatalogRepository().getItem(categoryId,
				BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
		if (categoryRepositoryItem != null) {
			final CategoryVO categoryVO = new CategoryVO();

			String parent = this.getImmediateParentCat(categoryRepositoryItem, siteId);
			//this.logDebug("immediate parent of the category " + parent);
			 
			@SuppressWarnings("unchecked")
			final Set<String> catSiteId = (Set<String>) categoryRepositoryItem.getPropertyValue("siteIds");
			if (catSiteId.contains(siteId) && (parent != null)) {
				categoryVO.setCategoryId(categoryId);
				categoryVO.setCategoryName((String) categoryRepositoryItem
						.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
				
				  if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
					  categoryVO.setPhantomCategory(((Boolean) categoryRepositoryItem
                                      .getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)));
                  }
				
				parentCategoryMap.put("0", categoryVO);
			}

			int count = 1;
			
			 while (parent != null) {
				final CategoryVO childCategoryVO = new CategoryVO();
				categoryRepositoryItem = getCatalogRepository().getItem(parent,
						BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
				childCategoryVO.setCategoryId(categoryRepositoryItem.getRepositoryId());
				childCategoryVO.setCategoryName((String) categoryRepositoryItem
						.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
				parentCategoryMap.put(String.valueOf(count), childCategoryVO);
				parent = this.getImmediateParentCat(categoryRepositoryItem, siteId);
				if ((parent == null) || StringUtils.isEmpty(parent)) {
					// remove the root category from the map
					parentCategoryMap.remove(String.valueOf(count));
					break;
				}
				count++;
			}

		}

		if (parentCategoryMap != null) {

			for (int count = 0; count < parentCategoryMap.size(); count++) {
				CategoryVO category = parentCategoryMap.get(String.valueOf(count));

				if (category != null) {
					Boolean isPhantomCategory = false;
					 
					if (null != category.getPhantomCategory() && category.getPhantomCategory() == Boolean.TRUE) {
						isPhantomCategory = true;
					}

					if (!isPhantomCategory) {
						if (count == 1) {							 
							categoryList.put("categoryL2", category.getCategoryId());
						}
						if (count == 2) {							 
							categoryList.put("categoryL1",  category.getCategoryId());
						}
						if(count==0){
							categoryList.put("categoryL3",  category.getCategoryId());
						}
					}

				}
			}

		}
		
	}

	/**
	 * This method gets the first category from the set of fixed parent
	 * categories
	 *
	 * @param categoryRepositoryItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getImmediateParentCat(final RepositoryItem categoryRepositoryItem, final String siteId) {

		String parent = null;
		final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>) categoryRepositoryItem
				.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME);
		final RepositoryItem parentRepositoryItem = getBbbCatalogTools().getCategoryForSite(parentCategorySet, siteId);

		if (parentRepositoryItem != null) {
			parent = parentRepositoryItem.getRepositoryId();
			if(isLoggingDebug()){
			this.logDebug(" applicable parent for category " + categoryRepositoryItem.getRepositoryId() + " has id  "
					+ parent + " and name "
					+ parentRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
			}
		} else {
			if(isLoggingDebug()){
			this.logDebug(" No applicable parent for category " + categoryRepositoryItem.getRepositoryId());
			}
		}

		return parent;
	}

	public InventoryPropertyAccessorHelper getInventoryHelper() {
		return inventoryHelper;
	}

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
	 * @param exculdedL2Category
	 *            the exculdedL2Category to set
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
	 * @param bbbCatalogTools
	 *            the bbbCatalogTools to set
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

	/**
	 * @return the categorySite
	 */
	public String getCategorySite() {
		return categorySite;
	}

	/**
	 * @param categorySite the categorySite to set
	 */
	public void setCategorySite(String categorySite) {
		this.categorySite = categorySite;
	}
}
