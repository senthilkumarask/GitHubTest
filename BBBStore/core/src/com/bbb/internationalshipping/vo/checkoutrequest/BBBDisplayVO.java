package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 * This class gives you the information
 * about Basket Display VO.
 * @version 1.0
 */
public class BBBDisplayVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to name.
	 */
	private String name;
	/**
	 * This variable is used to point to description.
	 */
	private String description;
	/**
	 * This variable is used to point to productUrl.
	 */
	private String productUrl;
	/**
	 * This variable is used to point to imageUrl.
	 */
	private String imageUrl;
	/**
	 * This variable is used to point to color.
	 */
	private String color;
	/**
	 * This variable is used to point to size.
	 */
	private String size;
	/**
	 * This variable is used to point to attributes.
	 */
	private String attributes;
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public final void setDescription(final String description) {
		this.description = description;
	}
	/**
	 * @return the productUrl
	 */
	public final String getProductUrl() {
		return productUrl;
	}
	/**
	 * @param productUrl the productUrl to set
	 */
	public final void setProductUrl(final String productUrl) {
		this.productUrl = productUrl;
	}
	/**
	 * @return the imageUrl
	 */
	public final String getImageUrl() {
		return imageUrl;
	}
	/**
	 * @param imageUrl the imageUrl to set
	 */
	public final void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}
	/**
	 * @return the color
	 */
	public final String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public final void setColor(final String color) {
		this.color = color;
	}
	/**
	 * @return the size
	 */
	public final String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public final void setSize(final String size) {
		this.size = size;
	}
	/**
	 * @return the attributes
	 */
	public final String getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public final void setAttributes(final String attributes) {
		this.attributes = attributes;
	}
	
	
}
