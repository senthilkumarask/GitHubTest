package com.bbb.commerce.checklist.vo;

import java.io.Serializable;
import java.util.List;

/**
 * The Class MyItemVO.
 */
public class MyItemVO implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The registry id. */
	private String registryId;
	
	/** The category list vo. */
	private List<MyItemCategoryVO> categoryListVO;
	
	/** The my items type name. */
	private String registryType;

	/**
	 * Gets the registry id.
	 *
	 * @return the registry id
	 */
	public String getRegistryId() {
		return registryId;
	}

	/**
	 * Sets the registry id.
	 *
	 * @param registryId the new registry id
	 */
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	/**
	 * Gets the category list vo.
	 *
	 * @return the category list vo
	 */
	public List<MyItemCategoryVO> getCategoryListVO() {
		return categoryListVO;
	}

	/**
	 * Sets the category list vo.
	 *
	 * @param categoryListVO the new category list vo
	 */
	public void setCategoryListVO(List<MyItemCategoryVO> categoryListVO) {
		this.categoryListVO = categoryListVO;
	}

	/**
	 * Gets the my items type name.
	 *
	 * @return the my items type name
	 */
	public String getMyItemsTypeName() {
		return registryType;
	}

	/**
	 * Sets the my items type name.
	 *
	 * @param myItemsTypeName the new my items type name
	 */
	public void setMyItemsTypeName(String myItemsTypeName) {
		this.registryType = myItemsTypeName;
	}
	
}
