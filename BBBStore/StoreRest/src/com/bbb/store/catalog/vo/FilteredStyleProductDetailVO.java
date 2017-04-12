package com.bbb.store.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;




/**
 * 
 * Class containing product details required for Style [specific to STOFU]
 * 
 * @author mgup39
 *
 */
public class FilteredStyleProductDetailVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private List<String> childSKUs;
	private String priceRangeDescription;
	private String name;
	private String shortDescription;
	private String longDescription;
	private String primaryCategory;
	private Map<String,String> productImageMap;
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
	 * @return the childSKUs
	 */
	public List<String> getChildSKUs() {
		return childSKUs;
	}
	/**
	 * @param childSKUs the childSKUs to set
	 */
	public void setChildSKUs(List<String> childSKUs) {
		this.childSKUs = childSKUs;
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
	/**
	 * @return the productImageMap
	 */
	public Map<String, String> getProductImageMap() {
		return productImageMap;
	}
	/**
	 * @param productImageMap the productImageMap to set
	 */
	public void setProductImageMap(Map<String, String> productImageMap) {
		this.productImageMap = productImageMap;
	}
		
	
}
