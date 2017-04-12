package com.bbb.commerce.order.vo;

import java.io.Serializable;

public class SkuRestrictedZipVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuName;
	private String skuImage;
	private String skuDescription;
	private StringBuilder address;
	 
	
	
	 



	/**
	 * @return the skuName
	 */
	public String getSkuName() {
		return skuName;
	}




	/**
	 * @param skuName the skuName to set
	 */
	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}




	/**
	 * @return the skuImage
	 */
	public String getSkuImage() {
		return skuImage;
	}




	/**
	 * @param skuImage the skuImage to set
	 */
	public void setSkuImage(String skuImage) {
		this.skuImage = skuImage;
	}




	/**
	 * @return the skuDescription
	 */
	public String getSkuDescription() {
		return skuDescription;
	}




	/**
	 * @param skuDescription the skuDescription to set
	 */
	public void setSkuDescription(String skuDescription) {
		this.skuDescription = skuDescription;
	}




	/**
	 * @return the address
	 */
	public StringBuilder getAddress() {
		return address;
	}




	/**
	 * @param address the address to set
	 */
	public void setAddress(StringBuilder address) {
		this.address = address;
	}




	 




	 
	
}
