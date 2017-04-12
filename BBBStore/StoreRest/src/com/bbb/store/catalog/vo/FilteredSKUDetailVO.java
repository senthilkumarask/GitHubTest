package com.bbb.store.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.rest.catalog.vo.CatalogItemAttributesVO;

public class FilteredSKUDetailVO implements Serializable {
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
	private boolean skuInStock;
	private String upc;
	private boolean activeFlag;
	private Map<String, List<AttributeVO>> skuAttributes;
	private CatalogItemAttributesVO skuAllAttributeVO ;
	private Double listPrice;
	private Double salePrice;
	private int inventoryStatus;
	private Map<String,String> skuImageMap;
	private Map<String,Integer> skuInventory;
	
	
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public boolean isSkuInStock() {
		return skuInStock;
	}
	public void setSkuInStock(boolean skuInStock) {
		this.skuInStock = skuInStock;
	}
	public String getUpc() {
		return upc;
	}
	public void setUpc(String upc) {
		this.upc = upc;
	}
	public boolean isActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	public Map<String, List<AttributeVO>> getSkuAttributes() {
		return skuAttributes;
	}
	public void setSkuAttributes(Map<String, List<AttributeVO>> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}
	public CatalogItemAttributesVO getSkuAllAttributeVO() {
		return skuAllAttributeVO;
	}
	public void setSkuAllAttributeVO(CatalogItemAttributesVO skuAllAttributeVO) {
		this.skuAllAttributeVO = skuAllAttributeVO;
	}
	public Double getListPrice() {
		return listPrice;
	}
	public void setListPrice(Double listPrice) {
		this.listPrice = listPrice;
	}
	public Double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}
	public int getInventoryStatus() {
		return inventoryStatus;
	}
	public void setInventoryStatus(int inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}
	public Map<String, String> getSkuImageMap() {
		return skuImageMap;
	}
	public void setSkuImageMap(Map<String, String> skuImageMap) {
		this.skuImageMap = skuImageMap;
	}
	public Map<String, Integer> getSkuInventory() {
		return skuInventory;
	}
	public void setSkuInventory(Map<String, Integer> skuInventory) {
		this.skuInventory = skuInventory;
	}
	
	
}
