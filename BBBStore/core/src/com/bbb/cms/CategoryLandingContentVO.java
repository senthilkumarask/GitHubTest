package com.bbb.cms;

import java.io.Serializable;

public class CategoryLandingContentVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String templateId;
	private String siteId;
	private CarouselVO carouselVO;
	private BannerVO bannerVO;
	private String categoryName;
	private String errorMessage;
	private String errorCode;
	private boolean isErrorExist;
	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public CarouselVO getCarouselVO() {
		return carouselVO;
	}
	public void setCarouselVO(CarouselVO carouselVO) {
		this.carouselVO = carouselVO;
	}
	public BannerVO getBannerVO() {
		return bannerVO;
	}
	public void setBannerVO(BannerVO bannerVO) {
		this.bannerVO = bannerVO;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorExist() {
		return isErrorExist;
	}
	public void setErrorExist(boolean isErrorExist) {
		this.isErrorExist = isErrorExist;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	
}
