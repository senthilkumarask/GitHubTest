package com.bbb.search.bean.result;


import java.util.List;
import java.util.Map;

/**
 * @author ikhan2
 *
 */
public class RootCategoryRefinementVO extends FacetRefinementVO {

	private static final long serialVersionUID = 1L;
	private Map<String, CategoryParentVO> subCategoriesTree;
	private List<String> orderedKeys;

	/**
	 * @return the subCategoriesTree
	 */ 	
	public Map<String, CategoryParentVO> getSubCategoriesTree() {
		return subCategoriesTree;
	}
	
	/**
	 * @param subCategoriesTree the subCategoriesTree to set
	 */
	public void setSubCategoriesTree(Map<String, CategoryParentVO> subCategoriesTree) {
		this.subCategoriesTree = subCategoriesTree;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getOrderedKeys() {
		return orderedKeys;
	}

	/**
	 * 
	 * @param sortedKeys
	 */
	public void setOrderedKeys(List<String> sortedKeys) {
		this.orderedKeys = sortedKeys;
	}
}
