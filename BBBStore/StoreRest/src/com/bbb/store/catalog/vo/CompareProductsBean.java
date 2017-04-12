package com.bbb.store.catalog.vo;

import java.io.Serializable;

public class CompareProductsBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private String skuId;
	private String categoryId;
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
	 * @return the skuId
	 */
	public String getSkuId() {
		return skuId;
	}
	/**
	 * @param skuId the skuId to set
	 */
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	/**
	 * 
	 */
	public CompareProductsBean(){
		//default constructor
	}
	
	/**
	 * @param compareProductsBean
	 */
	public CompareProductsBean(CompareProductsBean compareProductsBean){
		this.setCategoryId(compareProductsBean.getCategoryId());
		this.setProductId(compareProductsBean.getProductId());
		this.setSkuId(compareProductsBean.getSkuId());
	}

}
