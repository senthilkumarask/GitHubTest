/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  OrderInfoVO.java
 *
 *  DESCRIPTION: Order Info VO for Order 
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  05/02/12 Initial version
 *
 */
package com.bbb.commerce.pricing.bean;

import java.io.Serializable;

public class OrderInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private String price;
	private String currency;
	private String itemCount;
	private String productId;
	
	// added for social integration
	private String productURL;
	private String productTitle;
	private String productImageURL;
	private String productDescription;
	private String registry;
	private String productLargeImageURL;
	// social integration : end 
	
	
	//compare metrics
	private String categoryId;
	public String getProductURL() {
		return productURL;
	}

	public void setProductURL(String productURL) {
		this.productURL = productURL;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public String getProductImageURL() {
		return productImageURL;
	}

	public void setProductImageURL(String productImageURL) {
		this.productImageURL = productImageURL;
	}


	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
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
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the itemCount
	 */
	public String getItemCount() {
		return itemCount;
	}

	/**
	 * @param itemCount the itemCount to set
	 */
	public void setItemCount(String itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param pProductId the productId to set
	 */
	public void setProductId(String pProductId) {
		productId = pProductId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OrderInfoVO [skuId=" + skuId + ", price=" + price
				+ ", currency=" + currency + ", itemCount=" + itemCount
				+ ", productId=" + productId + "]";
	}

	public String getProductLargeImageURL() {
		return productLargeImageURL;
	}

	public void setProductLargeImageURL(String productLargeImageURL) {
		this.productLargeImageURL = productLargeImageURL;
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

	
}
