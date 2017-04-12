package com.bbb.cms;

import java.io.Serializable;

import atg.core.util.StringUtils;

/**
 * 
 * @author njai13
 *
 */
public class PromoImageVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mImageUrl;
	private String imageAltText;
	private String linkLabel; 
	private String linkUrl;

	

	/**
	 * 
	 */
	public PromoImageVO() {
		super();
	}
	/**
	 * @param pImageUrl
	 * @param pImageAltText
	 * @param pLinkLabel
	 * @param pLinkUrl
	 */
	public PromoImageVO(String pImageUrl, String pImageAltText, String pLinkLabel, String pLinkUrl) {
		super();
		mImageUrl = pImageUrl;
		imageAltText = pImageAltText;
		linkLabel = pLinkLabel;
		linkUrl = pLinkUrl;
	}
	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return mImageUrl;
	}
	/**
	 * @param pImageUrl the imageUrl to set
	 */
	public void setImageUrl(String pImageUrl) {
		mImageUrl = pImageUrl;
	}
	/**
	 * @return the imageAltText
	 */
	public String getImageAltText() {
		return imageAltText;
	}
	/**
	 * @param pImageAltText the imageAltText to set
	 */
	public void setImageAltText(String pImageAltText) {
		imageAltText = pImageAltText;
	}
	/**
	 * @return the linkLabel
	 */
	public String getLinkLabel() {
		return linkLabel;
	}
	/**
	 * @param pLinkLabel the linkLabel to set
	 */
	public void setLinkLabel(String pLinkLabel) {
		linkLabel = pLinkLabel;
	}
	/**
	 * @return the linkUrl
	 */
	public String getLinkUrl() {
		return linkUrl;
	}
	/**
	 * @param pLinkUrl the linkUrl to set
	 */
	public void setLinkUrl(String pLinkUrl) {
		linkUrl = pLinkUrl;
	}

	public String toString(){
		StringBuffer toString=new StringBuffer(" Promo Image VO Details \n ");
		toString.append("  Image  URL").append(StringUtils.isEmpty(mImageUrl)?"NULL":mImageUrl).append("\n")
		.append("  Image Alt Text ").append(StringUtils.isEmpty(imageAltText)?"NULL":imageAltText).append("\n")
		.append(" Link Label ").append(StringUtils.isEmpty(linkLabel)?"NULL":linkLabel).append("\n")
		.append(" Link URL ").append(StringUtils.isEmpty(linkUrl)?"NULL":linkUrl).append("\n");

		return toString.toString();
	}

}
