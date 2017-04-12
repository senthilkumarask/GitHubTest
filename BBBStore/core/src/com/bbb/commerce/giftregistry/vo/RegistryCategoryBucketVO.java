package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class RegistryCategoryBucketVO.
 *
 *
 */
public class RegistryCategoryBucketVO implements Serializable {
	
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
	/**
	 * @return the items
	 */
	public List<RegistryItemVO> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<RegistryItemVO> items) {
		this.items = items;
	}
	private List<RegistryItemVO> items;
}
