package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.search.bean.result.RootCategoryRefinementVO;



public class AllCategoriesVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<RootCategoryRefinementVO> rootCategoriesTree;
	
	/**
	 * @return the rootCategoriesTree
	 */
	public List<RootCategoryRefinementVO> getRootCategoriesTree() {
		return rootCategoriesTree;
	}


	/**
	 * @param rootCategoriesTree
	 *            the rootCategoriesTree to set
	 */
	public void setRootCategoriesTree(
			List<RootCategoryRefinementVO> rootCategoriesTree) {
		this.rootCategoriesTree = rootCategoriesTree;
	}

}
