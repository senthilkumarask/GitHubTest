package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.Map;

public class PromoVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String imageHref;
	private String imageSrc;
	private String imageAlt;
	private String seoText;
	private String caption;
	private String imageHeight;
	private String imageWidth;
	private String linkTarget;
	private String blurbPlp;
	private String relatedSearchString;
	private String relatedSearchStringKeys;
	
	/**
	 * @return the mRelatedSearchStringKeys
	 */
	public String getRelatedSearchStringKeys() {
		return relatedSearchStringKeys;
	}
	/**
	 * @param mRelatedSearchStringKeys the mRelatedSearchStringKeys to set
	 */
	public void setRelatedSearchStringKeys(String mRelatedSearchStringKeys) {
		this.relatedSearchStringKeys = mRelatedSearchStringKeys;
	}
	// BBBSL-3455 - Start Changed list to map for storing sanitized search term also.
	private Map<String, String> relatedSeperated;
	// BBBSL-3455 - End Changed list to map for storing sanitized search term also.

	private String mobileImageHref;
	private String mobileImageSrc;
	private String mobileImageAlt;
	
	//Start Added for R 2.2 568 featured Product
	// To hold Featured Products Info
	private Map<String,String> featuredProducts;
	
	/**
	 * @return Map of featured Products
	 */
	public Map<String, String> getFeaturedProducts() {
		return this.featuredProducts;
	}
	/**
	 * @param featuredProducts
	 */
	public void setFeaturedProducts(Map<String, String> featuredProducts) {
		this.featuredProducts = featuredProducts;
	}
	//End Added for R 2.2 568 featured Product
	
	public Map<String, String> getRelatedSeperated() {
		return relatedSeperated;
	}
	public void setRelatedSeperated(Map<String, String> mRelatedSeperated) {
		this.relatedSeperated = mRelatedSeperated;
	}
	public String getRelatedSearchString() {
		return relatedSearchString;
	}
	public void setRelatedSearchString(String pRelatedSearchString) {
		this.relatedSearchString = pRelatedSearchString;
	}
	public String getBlurbPlp() {
		return blurbPlp;
	}
	public void setBlurbPlp(String pBlurbPlp) {
		this.blurbPlp = pBlurbPlp;
	}
	public String getLinkTarget() {
		return linkTarget;
	}
	public String getImageAlt() {
		return imageAlt;
	}
	public void setImageAlt(String pImageAlt) {
		this.imageAlt = pImageAlt;
	}
	public void setLinkTarget(String pLinkTarget) {
		this.linkTarget = pLinkTarget;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String pCaption) {
		this.caption = pCaption;
	}
	public String getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(String pImageHeight) {
		this.imageHeight = pImageHeight;
	}
	public String getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(String pImageWidth) {
		this.imageWidth = pImageWidth;
	}
	public String getImageHref() {
		return imageHref;
	}
	public void setImageHref(String pImageHref) {
		this.imageHref = pImageHref;
	}
	public String getImageSrc() {
		return imageSrc;
	}
	public void setImageSrc(String pImageSrc) {
		this.imageSrc = pImageSrc;
	}
	public String getSeoText() {
		return seoText;
	}
	public void setSeoText(String pSeoText) {
		this.seoText = pSeoText;
	}
	public String getMobileImageHref() {
		return mobileImageHref;
	}
	public void setMobileImageHref(String mobileImageHref) {
		this.mobileImageHref = mobileImageHref;
	}
	public String getMobileImageSrc() {
		return mobileImageSrc;
	}
	public void setMobileImageSrc(String mobileImageSrc) {
		this.mobileImageSrc = mobileImageSrc;
	}
	public String getMobileImageAlt() {
		return mobileImageAlt;
	}
	public void setMobileImageAlt(String mobileImageAlt) {
		this.mobileImageAlt = mobileImageAlt;
	}
}
