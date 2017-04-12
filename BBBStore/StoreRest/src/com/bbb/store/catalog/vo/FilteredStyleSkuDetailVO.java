package com.bbb.store.catalog.vo;

import java.io.Serializable;
import java.util.Map;


/**
 * 
 * Class containing sku details required for Style [specific to STOFU]
 * 
 * @author mgup39
 *
 */
public class FilteredStyleSkuDetailVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private String displayName;
	private String description;
	private String longDescription;
	private String color;
	private String size;
	private String upc;
	private double listPrice;
	private double salePrice;
	private Map<String,String> skuImageMap;
	
	
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
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the upc
	 */
	public String getUpc() {
		return upc;
	}
	/**
	 * @param upc the upc to set
	 */
	public void setUpc(String upc) {
		this.upc = upc;
	}
	/**
	 * @return the listPrice
	 */
	public double getListPrice() {
		return listPrice;
	}
	/**
	 * @param listPrice the listPrice to set
	 */
	public void setListPrice(double listPrice) {
		this.listPrice = listPrice;
	}
	/**
	 * @return the salePrice
	 */
	public double getSalePrice() {
		return salePrice;
	}
	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	/**
	 * @return the skuImageMap
	 */
	public Map<String, String> getSkuImageMap() {
		return skuImageMap;
	}
	/**
	 * @param skuImageMap the skuImageMap to set
	 */
	public void setSkuImageMap(Map<String, String> skuImageMap) {
		this.skuImageMap = skuImageMap;
	}

	
}
