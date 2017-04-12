package com.bbb.commerce.catalog.vo;

import java.io.Serializable;



public class RegistryCategoryMapVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String catName;
	private String catId;
	private boolean addItemFlag;
	private boolean recommendedCatFlag;
	private String catSeoUrl;
	private String catImage;
	
	/**
	 * @return the catImage
	 */
	public String getCatImage() {
		return catImage;
	}
	/**
	 * @param catImage the catImage to set
	 */
	public void setCatImage(String catImage) {
		this.catImage = catImage;
	}
	/**
	 * @return the catSeoUrl
	 */
	public String getCatSeoUrl() {
		return catSeoUrl;
	}
	/**
	 * @param catSeoUrl the catSeoUrl to set
	 */
	public void setCatSeoUrl(String catSeoUrl) {
		this.catSeoUrl = catSeoUrl;
	}
	/**
	 * @return the catName
	 */
	public String getCatName() {
		return catName;
	}
	/**
	 * @param catName the catName to set
	 */
	public void setCatName(String catName) {
		this.catName = catName;
	}
	/**
	 * @return the catId
	 */
	public String getCatId() {
		return catId;
	}
	/**
	 * @param catId the catId to set
	 */
	public void setCatId(String catId) {
		this.catId = catId;
	}
	/**
	 * @return the addItemFlag
	 */
	public boolean isAddItemFlag() {
		return addItemFlag;
	}
	/**
	 * @param addItemFlag the addItemFlag to set
	 */
	public void setAddItemFlag(boolean addItemFlag) {
		this.addItemFlag = addItemFlag;
	}
	/**
	 * @return the recommendedCatFlag
	 */
	public boolean isRecommendedCatFlag() {
		return recommendedCatFlag;
	}
	/**
	 * @param recommendedCatFlag the recommendedCatFlag to set
	 */
	public void setRecommendedCatFlag(boolean recommendedCatFlag) {
		this.recommendedCatFlag = recommendedCatFlag;
	}


}
