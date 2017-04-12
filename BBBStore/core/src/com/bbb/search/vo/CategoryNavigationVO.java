package com.bbb.search.vo;

import java.io.Serializable;


import com.bbb.search.bean.result.CategoryParentVO;


// TODO: Auto-generated Javadoc
/**
 * The Class RegistryCategoryBucketVO.
 *
 *
 */
public class CategoryNavigationVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String catgoryID;
	/**
	 * @return the catgoryID
	 */
	public String getCatgoryID() {
		return catgoryID;
	}
	/**
	 * @param catgoryID the catgoryID to set
	 */
	public void setCatgoryID(String catgoryID) {
		this.catgoryID = catgoryID;
	}
	private CategoryParentVO categoryParent;
	/**
	 * @return the categoryParent
	 */
	public CategoryParentVO getCategoryParent() {
		return categoryParent;
	}
	/**
	 * @param categoryParent the categoryParent to set
	 */
	public void setCategoryParent(CategoryParentVO categoryParent) {
		this.categoryParent = categoryParent;
	}
}
