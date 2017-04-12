package com.bbb.cms;

import java.io.Serializable;
//import java.util.List;
import java.util.Map;

public class StaticContentVO implements Serializable {
 /**
	 * 
	 */
private static final long serialVersionUID = 1L;
private String templateId;
private String pageName;
private String pageTitle;
private String pageHeaderCopy;
private String pageCopy;
private String bbbPageName;

private Integer pageType;
private String seoUrl;

private String siteId;
private Map<String, String> omnitureData;


public Map<String, String> getOmnitureData() {
	return omnitureData;
}

public void setOmnitureData(Map<String, String> omnitureData) {
	this.omnitureData = omnitureData;
}

/**
 * @return the templateId
 */
public String getTemplateId() {
	return templateId;
}

/**
 * @param templateId the templateId to set
 */
public void setTemplateId(String templateId) {
	this.templateId = templateId;
}

/**
 * @return the pageName
 */
public String getPageName() {
	return pageName;
}

/**
 * @param pageName the pageName to set
 */
public void setPageName(String pageName) {
	this.pageName = pageName;
}

/**
 * @return the pageTitle
 */
public String getPageTitle() {
	return pageTitle;
}

/**
 * @param pageTitle the pageTitle to set
 */
public void setPageTitle(String pageTitle) {
	this.pageTitle = pageTitle;
}

/**
 * @return the pageHeaderCopy
 */
public String getPageHeaderCopy() {
	return pageHeaderCopy;
}

/**
 * @param pageHeaderCopy the pageHeaderCopy to set
 */
public void setPageHeaderCopy(String pageHeaderCopy) {
	this.pageHeaderCopy = pageHeaderCopy;
}

/**
 * @return the pageCopy
 */
public String getPageCopy() {
	return pageCopy;
}

/**
 * @param pageCopy the pageCopy to set
 */
public void setPageCopy(String pageCopy) {
	this.pageCopy = pageCopy;
}

/**
 * @return the bbbPageName
 */
public String getBbbPageName() {
	return bbbPageName;
}

/**
 * @param bbbPageName the bbbPageName to set
 */
public void setBbbPageName(String bbbPageName) {
	this.bbbPageName = bbbPageName;
}

/**
 * @return the pageType
 */
public Integer getPageType() {
	return pageType;
}

/**
 * @param pageType the pageType to set
 */
public void setPageType(Integer pageType) {
	this.pageType = pageType;
}

/**
 * @return the seoUrl
 */
public String getSeoUrl() {
	return seoUrl;
}

/**
 * @param seoUrl the seoUrl to set
 */
public void setSeoUrl(String seoUrl) {
	this.seoUrl = seoUrl;
}

/**
 * @return the siteId
 */
public String getSiteId() {
	return siteId;
}

/**
 * @param siteId the siteId to set
 */
public void setSiteId(String siteId) {
	this.siteId = siteId;
}



}
