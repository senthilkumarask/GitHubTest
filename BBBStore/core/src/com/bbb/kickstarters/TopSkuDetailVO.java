package com.bbb.kickstarters;

import java.io.Serializable;

import com.bbb.commerce.catalog.vo.ImageVO;

public class TopSkuDetailVO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	private String description;
	private String color;
	private String size;
	private String upc;
	private ImageVO skuImages;
	private boolean customizableRequired;
    private boolean ltlItem;
    private String displayShipMsg;
    private String pricingLabelCode;
	private boolean inCartFlag;
	
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
	public ImageVO getSkuImages() {
		return skuImages;
	}
	public void setSkuImages(ImageVO skuImages) {
		this.skuImages = skuImages;
	}

	/**
	 * @return the customizableRequired
	 */
	public boolean isCustomizableRequired() {
		return customizableRequired;
	}

	/**
	 * @param customizableRequired the customizableRequired to set
	 */
	public void setCustomizableRequired(boolean customizableRequired) {
		this.customizableRequired = customizableRequired;
	}
	public boolean isLtlItem() {
		return ltlItem;
	}
	public void setLtlItem(boolean ltlItem) {
		this.ltlItem = ltlItem;
	}
	
	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}

	public String getPricingLabelCode() {
		return pricingLabelCode;
	}
	public void setPricingLabelCode(String pricingLabelCode) {
		this.pricingLabelCode = pricingLabelCode;
	}
	public boolean isInCartFlag() {
		return inCartFlag;
	}
	public void setInCartFlag(boolean inCartFlag) {
		this.inCartFlag = inCartFlag;
	}
}
