package com.bbb.paypal;

public class PayPalProdDescVO {
	private String prodID;
	private String skuId;
	private String skuName;
	private String prodURL;
	private String prodName;
	private String skuDescription;
	private double amount;
	private long quantity;

	/**
	 * @return the quantity
	 */
	public long getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
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
	 * @return the prodID
	 */
	public String getProdID() {
		return prodID;
	}
	/**
	 * @param prodID the prodID to set
	 */
	public void setProdID(String prodID) {
		this.prodID = prodID;
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
	 * @return the prodURL
	 */
	public String getProdURL() {
		return prodURL;
	}
	/**
	 * @param prodURL the prodURL to set
	 */
	public void setProdURL(String prodURL) {
		this.prodURL = prodURL;
	}
	/**
	 * @return the prodName
	 */
	public String getProdName() {
		return prodName;
	}
	/**
	 * @param prodName the prodName to set
	 */
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	
}
