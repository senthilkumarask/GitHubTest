package com.bbb.commerce.giftregistry.vo;

//import java.util.List;

import java.io.Serializable;

import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;


// TODO: Auto-generated Javadoc
/**
 * The Class RegistryCategoryBucketVO.
 *
 *
 */
public class RegistryCategoriesVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String catgoryName;
	/**
	 * @return the catgoryName
	 */
	public String getCatgoryName() {
		return catgoryName;
	}
	/**
	 * @param catgoryName the catgoryName to set
	 */
	public void setCatgoryName(String catgoryName) {
		this.catgoryName = catgoryName;
	}
	
	private RegistryCategoryMapVO categoryDetail;
	/**
	 * @return the category
	 */
	public RegistryCategoryMapVO getCategoryDetail() {
		return categoryDetail;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategoryDetail(RegistryCategoryMapVO categoryDetail) {
		this.categoryDetail = categoryDetail;
	}
}
