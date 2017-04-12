package com.bbb.cms;

import java.io.Serializable;

public class ContentBoxVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String contentBoxId;
	private String imagePosition;
	private String content;
	private PromoBoxVO imageBox;
	
	/**
	 * @return the contentBoxId
	 */
	public String getContentBoxId() {
		return contentBoxId;
	}
	/**
	 * @param contentBoxId the contentBoxId to set
	 */
	public void setContentBoxId(String contentBoxId) {
		this.contentBoxId = contentBoxId;
	}
	/**
	 * @return the imagePosition
	 */
	public String getImagePosition() {
		return imagePosition;
	}
	/**
	 * @param imagePosition the imagePosition to set
	 */
	public void setImagePosition(String imagePosition) {
		this.imagePosition = imagePosition;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the imageBox
	 */
	public PromoBoxVO getImageBox() {
		return imageBox;
	}
	/**
	 * @param imageBox the imageBox to set
	 */
	public void setImageBox(PromoBoxVO imageBox) {
		this.imageBox = imageBox;
	}
}
