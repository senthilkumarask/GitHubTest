package com.bbb.store.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;

/**
 *
 */
public class CompareProductVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,String> compareProductImage;
	private String displayName;
	private String productLowPrice;
	private String productHighPrice;
	private List<String> goodToKnow;
	private String longDescription;
	private List<FilteredProductDetailVO> siblingProducts;
	private Map<String,List<AttributeVO>> attributeList;
	private float averageOverallRating;
	private String productLowSalePrice;
	private String productHighSalePrice;
	private boolean skuInStock;
	private boolean storeSKU;
	private List<String> childSKUs;
	private Map<String,Integer> skuInventory;
	private Map<String,List<RollupTypeVO>> rollupAttributes;
	private Map<String,String> skuRollupMap;
	
	/**
	 * @return the skuRollupMap
	 */
	public Map<String, String> getSkuRollupMap() {
		return this.skuRollupMap;
	}
	/**
	 * @param skuRollupMap the skuRollupMap to set
	 */
	public void setSkuRollupMap(Map<String, String> skuRollupMap) {
		this.skuRollupMap = skuRollupMap;
	}
	/**
	 * @return the rollupAttributes
	 */
	public Map<String, List<RollupTypeVO>> getRollupAttributes() {
		return this.rollupAttributes;
	}
	/**
	 * @param rollupAttributes the rollupAttributes to set
	 */
	public void setRollupAttributes(Map<String, List<RollupTypeVO>> rollupAttributes) {
		this.rollupAttributes = rollupAttributes;
	}
	/**
	 * @return the childSKUs
	 */
	public List<String> getChildSKUs() {
		return this.childSKUs;
	}
	/**
	 * @param childSKUs the childSKUs to set
	 */
	public void setChildSKUs(List<String> childSKUs) {
		this.childSKUs = childSKUs;
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
	/**
	 * @return the skuInStock
	 */
	public boolean isSkuInStock() {
		return this.skuInStock;
	}
	/**
	 * @param skuInStock the skuInStock to set
	 */
	public void setSkuInStock(boolean skuInStock) {
		this.skuInStock = skuInStock;
	}
	/**
	 * @return the storeSKU
	 */
	public boolean isStoreSKU() {
		return this.storeSKU;
	}
	/**
	 * @param storeSKU the storeSKU to set
	 */
	public void setStoreSKU(boolean storeSKU) {
		this.storeSKU = storeSKU;
	}
	/**
	 * @return the compareProductImage
	 */
	public Map<String,String> getCompareProductImage() {
		return this.compareProductImage;
	}
	/**
	 * @param compareProductImage the compareProductImage to set
	 */
	public void setCompareProductImage(Map<String,String> compareProductImage) {
		this.compareProductImage = compareProductImage;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return this.longDescription;
	}
	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	/**
	 * @return the siblingProducts
	 */
	public List<FilteredProductDetailVO> getSiblingProducts() {
		return this.siblingProducts;
	}
	/**
	 * @param siblingProducts the siblingProducts to set
	 */
	public void setSiblingProducts(List<FilteredProductDetailVO> siblingProducts) {
		this.siblingProducts = siblingProducts;
	}
	/**
	 * @return the attributeList
	 */
	public Map<String, List<AttributeVO>> getAttributeList() {
		return this.attributeList;
	}
	/**
	 * @param attributeList the attributeList to set
	 */
	public void setAttributeList(Map<String, List<AttributeVO>> attributeList) {
		this.attributeList = attributeList;
	}
	/**
	 * @return the productLowPrice
	 */
	public String getProductLowPrice() {
		return this.productLowPrice;
	}
	/**
	 * @param productLowPrice the productLowPrice to set
	 */
	public void setProductLowPrice(String productLowPrice) {
		this.productLowPrice = productLowPrice;
	}
	/**
	 * @return the productHighPrice
	 */
	public String getProductHighPrice() {
		return this.productHighPrice;
	}
	/**
	 * @param productHighPrice the productHighPrice to set
	 */
	public void setProductHighPrice(String productHighPrice) {
		this.productHighPrice = productHighPrice;
	}
	/**
	 * @return the productLowSalePrice
	 */
	public String getProductLowSalePrice() {
		return this.productLowSalePrice;
	}
	/**
	 * @param productLowSalePrice the productLowSalePrice to set
	 */
	public void setProductLowSalePrice(String productLowSalePrice) {
		this.productLowSalePrice = productLowSalePrice;
	}
	/**
	 * @return the productHighSalePrice
	 */
	public String getProductHighSalePrice() {
		return this.productHighSalePrice;
	}
	/**
	 * @param productHighSalePrice the productHighSalePrice to set
	 */
	public void setProductHighSalePrice(String productHighSalePrice) {
		this.productHighSalePrice = productHighSalePrice;
	}
	/**
	 * @return the skuInventory
	 */
	public Map<String, Integer> getSkuInventory() {
		return this.skuInventory;
	}
	/**
	 * @param skuInventory the skuInventory to set
	 */
	public void setSkuInventory(Map<String, Integer> skuInventory) {
		this.skuInventory = skuInventory;
	}
	public List<String> getGoodToKnow() {
		return this.goodToKnow;
	}
	public void setGoodToKnow(List<String> goodToKnow) {
		this.goodToKnow = goodToKnow;
	}
	
}
