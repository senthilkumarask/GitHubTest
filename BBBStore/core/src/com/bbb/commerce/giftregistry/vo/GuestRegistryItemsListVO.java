/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.util.List;

/**
 * The Class GuestRegistryItemsListVO.
 * This bean would hold the mapping of Category Id to List of RegistryItemsAssociated with that categoryId
 */
public class GuestRegistryItemsListVO implements Serializable  {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The registry item list. */
	private List<RegistryItemVO> registryItemList;
	
	private String categoryId;
	private String displayName;
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * Gets the registry item list.
	 *
	 * @return the registryItemList
	 */
	public List<RegistryItemVO> getRegistryItemList() {
		return registryItemList;
	}
	
	/**
	 * Sets the registry item list.
	 *
	 * @param registryItemList the registryItemList to set
	 */
	public void setRegistryItemList(final List<RegistryItemVO> registryItemList) {
		this.registryItemList = registryItemList;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}

