package com.bbb.redirectURLs;

import java.io.Serializable;

public class CategoryRedirectURLsVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String desktopCategoryRedirectURL;
	private String mobileCategoryRedirectURL;

	
	/**
	 * @return the desktopCategoryRedirectURL
	 */
	public String getDesktopCategoryRedirectURL() {
		return desktopCategoryRedirectURL;
	}
	
	/**
	 * @param desktopCategoryRedirectURL the desktopCategoryRedirectURL to set
	 */
	public void setDesktopCategoryRedirectURL(String desktopCategoryRedirectURL) {
		this.desktopCategoryRedirectURL = desktopCategoryRedirectURL;
	}
	
	/**
	 * @return the mobileCategoryRedirectURL
	 */
	public String getMobileCategoryRedirectURL() {
		return mobileCategoryRedirectURL;
	}
	
	/**
	 * @param mobileCategoryRedirectURL the mobileCategoryRedirectURL to set
	 */
	public void setMobileCategoryRedirectURL(String mobileCategoryRedirectURL) {
		this.mobileCategoryRedirectURL = mobileCategoryRedirectURL;
	}

}
