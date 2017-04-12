package com.bbb.certona.vo;

import java.io.Serializable;
import java.util.Date;


public class SiteSpecificProductAttr implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String shortDescription;
	private String longDescription;
	private boolean isActiveProduct;
	private String skuLowPrice;
	private String skuHighPrice;
	private String collegeId;
	private String priceRangeDescription;
	private String siteId;
	private boolean weboffered;
	private boolean disable;
	private Date enableDate;
	private String tier;
	private int totalReviewCount;
	private float averageOverallRating;

	
	/**
	 * @return the enableDate
	 */
	public Date getEnableDate() {
		return enableDate;
	}
	/**
	 * @param enableDate the enableDate to set
	 */
	public void setEnableDate(Date enableDate) {
		this.enableDate = enableDate;
	}


	public String toString(){
		StringBuffer toString=new StringBuffer();
		toString.append("Product Name ").append(name).append("\n")
		.append(" weboffered ").append(weboffered)
		.append(" disable ").append(disable)
		.append(" isActiveProduct ").append(isActiveProduct).append("\n")
		.append(" priceRangeDescription ").append(priceRangeDescription )
		.append(" skuLowPrice ").append(skuLowPrice)
		.append(" skuHighPrice ").append(skuHighPrice).append("\n")
		.append(" enableDate ").append(enableDate).append("\n")
		.append("tier").append(tier).append("\n");
	
		return toString.toString();

	}


	/**
	 * @return the weboffered
	 */
	public boolean isWeboffered() {
		return weboffered;
	}
	/**
	 * @param weboffered the weboffered to set
	 */
	public void setWeboffered(boolean weboffered) {
		this.weboffered = weboffered;
	}
	/**
	 * @return the disable
	 */
	public boolean isDisable() {
		return disable;
	}
	/**
	 * @param disable the disable to set
	 */
	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}
	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}
	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	/**
	 * @return the isActiveProduct
	 */
	public boolean isActiveProduct() {
		return isActiveProduct;
	}
	/**
	 * @param isActiveProduct the isActiveProduct to set
	 */
	public void setActiveProduct(boolean isActiveProduct) {
		this.isActiveProduct = isActiveProduct;
	}
	/**
	 * @return the skuLowPrice
	 */
	public String getSkuLowPrice() {
		return skuLowPrice;
	}
	/**
	 * @param skuLowPrice the skuLowPrice to set
	 */
	public void setSkuLowPrice(String skuLowPrice) {
		this.skuLowPrice = skuLowPrice;
	}
	/**
	 * @return the skuHighPrice
	 */
	public String getSkuHighPrice() {
		return skuHighPrice;
	}
	/**
	 * @param skuHighPrice the skuHighPrice to set
	 */
	public void setSkuHighPrice(String skuHighPrice) {
		this.skuHighPrice = skuHighPrice;
	}
	/**
	 * @return the collegeId
	 */
	public String getCollegeId() {
		return collegeId;
	}
	/**
	 * @param collegeId the collegeId to set
	 */
	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}
	/**
	 * @return the priceRangeDescription
	 */
	public String getPriceRangeDescription() {
		return priceRangeDescription;
	}
	/**
	 * @param priceRangeDescription the priceRangeDescription to set
	 */
	public void setPriceRangeDescription(String priceRangeDescription) {
		this.priceRangeDescription = priceRangeDescription;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the tiers
	 */
	public String getTier() {
		return tier;
	}
	/**
	 * @param tier the tier to set
	 */
	public void setTier(String tier) {
		this.tier = tier;
	}

	
	/**
	 * @return the totalReviewCount
	 */
	public int getTotalReviewCount() {
		return this.totalReviewCount;
	}
	/**
	 * @param totalReviewCount the totalReviewCount to set
	 */
	public void setTotalReviewCount(int totalReviewCount) {
		this.totalReviewCount = totalReviewCount;
	}
	
	/**
	 * @return the averageOverallRating
	 */
	public float getAverageOverallRating() {
		return this.averageOverallRating;
	}
	/**
	 * @param averageOverallRating the averageOverallRating to set
	 */
	public void setAverageOverallRating(float averageOverallRating) {
		this.averageOverallRating = averageOverallRating;
	}
}
