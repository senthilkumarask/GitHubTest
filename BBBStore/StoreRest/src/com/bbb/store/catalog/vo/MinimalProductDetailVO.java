package com.bbb.store.catalog.vo;

import java.io.Serializable;


/**
 * 
 * Class containing product id and primary Category [specific to STOFU]
 * 
 * @author agu117
 *
 */
public class MinimalProductDetailVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private String primaryCategory;

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}
	/**
	 * @return the primaryCategory
	 */
	public String getPrimaryCategory() {
		return primaryCategory;
	}
	/**
	 * @param primaryCategory the primaryCategory to set
	 */
	public void setPrimaryCategory(String primaryCategory) {
		this.primaryCategory = primaryCategory;
	}
}
