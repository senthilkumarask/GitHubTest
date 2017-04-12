package com.bbb.cms;

import java.io.Serializable;
import atg.core.util.StringUtils;

public class PromoBoxVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mId; 
	private String mImageURL; 
	private String mImageAltText;
	private String mImageMapName;
	private String mImageMapContent;
	private String mPromoBoxContent;
	private String mPromoBoxTitle;
	private String mImageLink;
	private String mHeight;
	private String mWidth;

	
	/**
	 * @return the mHeight
	 */
	public String getHeight() {
		return mHeight;
	}
	/**
	 * @param mHeight the mHeight to set
	 */
	public void setHeight(String height) {
		this.mHeight = height;
	}
	/**
	 * @return the mWidth
	 */
	public String getWidth() {
		return mWidth;
	}
	/**
	 * @param mWidth the mWidth to set
	 */
	public void setWidth(String width) {
		this.mWidth = width;
	}
	/**
	 * @return the Id
	 */
	public String getId() {
		return mId;
	}
	/**
	 * @param pId the Id to set
	 */
	public void setId(String pId) {
		this.mId = pId;
	}

	/**
	 * @return the imageLink
	 */
	public String getImageLink() {
		return mImageLink;
	}

	/**
	 * @param pImageLink the imageLink to set
	 */
	public void setImageLink(String pImageLink) {
		mImageLink = pImageLink;
	}



	/**
	 * 
	 */
	public PromoBoxVO() {
		super();
	}

	/**
	 * @param pImageURL
	 * @param pImageAltText
	 * @param pImageMapName
	 * @param pImageMapContent
	 * @param pPromoBoxContent
	 * @param pImageLink
	 */
	public PromoBoxVO(String pId, String pImageURL, String pImageAltText, String pImageMapName, String pImageMapContent,
			String pPromoBoxContent, String pImageLink, String height, String width) {
		super();
		mId = pId;
		mImageURL = pImageURL;
		mImageAltText = pImageAltText;
		mImageMapName = pImageMapName;
		mImageMapContent = pImageMapContent;
		mPromoBoxContent = pPromoBoxContent;
		mImageLink = pImageLink;
		mHeight = height;
		mWidth = width;
	}
	
	/**
	 * @param pImageURL
	 * @param pImageAltText
	 * @param pImageMapName
	 * @param pImageMapContent
	 * @param pPromoBoxContent
	 * @param pImageLink
	 */
	public PromoBoxVO(String pId, String pImageURL, String pImageAltText, String pImageMapName, String pImageMapContent,
			String pPromoBoxContent, String pPromoBoxTitle, String pImageLink, String height, String width) {
		super();
		mId = pId;
		mImageURL = pImageURL;
		mImageAltText = pImageAltText;
		mImageMapName = pImageMapName;
		mImageMapContent = pImageMapContent;
		mPromoBoxContent = pPromoBoxContent;
		mPromoBoxTitle = pPromoBoxTitle;
		mImageLink = pImageLink;
		mHeight = height;
		mWidth = width;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return mImageURL;
	}
	/**
	 * @param pImageURL the imageURL to set
	 */
	public void setImageURL(String pImageURL) {
		mImageURL = pImageURL;
	}
	/**
	 * @return the imageAltText
	 */
	public String getImageAltText() {
		return mImageAltText;
	}
	/**
	 * @param pImageAltText the imageAltText to set
	 */
	public void setImageAltText(String pImageAltText) {
		mImageAltText = pImageAltText;
	}
	/**
	 * @return the imageMapName
	 */
	public String getImageMapName() {
		return mImageMapName;
	}
	/**
	 * @param pImageMapName the imageMapName to set
	 */
	public void setImageMapName(String pImageMapName) {
		mImageMapName = pImageMapName;
	}
	/**
	 * @return the imageMapContent
	 */
	public String getImageMapContent() {
		return mImageMapContent;
	}
	/**
	 * @param pImageMapContent the imageMapContent to set
	 */
	public void setImageMapContent(String pImageMapContent) {
		mImageMapContent = pImageMapContent;
	}
	/**
	 * @return the promoBoxContent
	 */
	public String getPromoBoxContent() {
		return mPromoBoxContent;
	}
	/**
	 * @param pPromoBoxContent the promoBoxContent to set
	 */
	public void setPromoBoxContent(String pPromoBoxContent) {
		mPromoBoxContent = pPromoBoxContent;
	}
	/**
	 * @return the promoBoxTitle
	 */
	public String getPromoBoxTitle() {
		return mPromoBoxTitle;
	}
	/**
	 * @param pPromoBoxTitle the promoBoxTitle to set
	 */
	public void setPromoBoxTitle(String pPromoBoxTitle) {
		this.mPromoBoxTitle = pPromoBoxTitle;
	}
	public boolean isEmpty(){
		return (StringUtils.isEmpty(this.getPromoBoxContent()) && StringUtils.isEmpty(this.getImageURL()));
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Promo Box VO \n ");
		toString.append("Promo Box Content ").append(this.getPromoBoxContent()).append("\n")
		.append("Promo Box Id ").append(this.getId()).append("\n")
		.append("Promo Box image URL ").append(this.getImageURL()).append("\n")
		.append("Promo Box Image Alt Text ").append(this.getImageAltText()).append("\n")
		.append("Promo Box Image Map Name ").append(this.getImageMapName()).append("\n")
		.append("Promo Box Image Map Content ").append(this.getImageMapContent()).append("\n")
		.append("Promo Box Box Title ").append(this.getPromoBoxTitle()).append("\n")
		.append("Promo Box Image Link ").append(this.getImageLink()).append("\n")
		.append("Promo Box Height ").append(this.getHeight()).append("\n")
		.append("Promo Box Width ").append(this.getWidth()).append("\n");
		return toString.toString();
	}


}
