package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class RecommendedCategoryVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String catRecommId;
	private String recommCategoryImageUrl;
	private String recommCategoryText;
	private String recommCategoryLink;
	public String getCatRecommId() {
		return catRecommId;
	}
	
	public void setCatRecommId(String catRecommId) {
		this.catRecommId = catRecommId;
	}
	
	public String getRecommCategoryImageUrl() {
		return recommCategoryImageUrl;
	}
	
	public void setRecommCategoryImageUrl(String recommCategoryImageUrl) {
		this.recommCategoryImageUrl = recommCategoryImageUrl;
	}
	
	public String getRecommCategoryText() {
		return recommCategoryText;
	}
	
	public void setRecommCategoryText(String recommCategoryText) {
		this.recommCategoryText = recommCategoryText;
	}
	
	public String getRecommCategoryLink() {
		return recommCategoryLink;
	}
	
	public void setRecommCategoryLink(String recommCategoryLink) {
		this.recommCategoryLink = recommCategoryLink;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
