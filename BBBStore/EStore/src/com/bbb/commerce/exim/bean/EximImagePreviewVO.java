package com.bbb.commerce.exim.bean;

import java.io.Serializable;


/**
 * @author sanam jain
 * The Class EximImagePreviewVO.
 */
public class EximImagePreviewVO implements Serializable {
	
	/** The size. */
	private String size;
	
	/** The width. */
	private int width;
	
	/** The height. */
	private int height;
	
	/** The url. */
	private String  url;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EximImagePreviewVO [size=").append(this.size)
                        .append(", width=").append(this.width)
                        .append(", height=").append(this.height)
                        .append(", url=").append(this.url)
                        .append("]");
		return builder.toString();
    }
	
	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	
	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(String size) {
		this.size = size;
	}
	
	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the width.
	 *
	 * @param width the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Sets the height.
	 *
	 * @param height the new height
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
