package com.bbb.commerce.checklist.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;

public class CheckListSeoUrlHierarchy implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7907916217309990929L;
	private String checkListDisplayName;
	private String checkListId;
	private String seoUrl;
	private String endecaSeoUrl;
	private String seoUrlDimensionId;
	private long productCount;
	private long productCountUS;
	private long productCountBaby;
	private long productCountCa;
	
	private String babyCategoryURL;
	private String caCategoryURL;
	private String tbsCategoryURL;
	private String categoryURL;
	private String siteURL;
	private String checkListCategoryName;
	private CheckListSeoUrlHierarchy parentSeoUrl;
	private List<CheckListSeoUrlHierarchy> siblingsSeoUrls;
	private List<CheckListSeoUrlHierarchy> childsSeoUrls;
	private int categoryLevel;
	
	private boolean processed=false;
	private boolean categoryEnabled=false;
	private boolean regTypeCheckList=false;
	private boolean checkListDisabled=false;
	/**
	 * @return the checkListDisplayName
	 */
	public String getCheckListDisplayName() {
		return checkListDisplayName;
	}
	
	/**
	 * @param checkListDisplayName the checkListDisplayName to set
	 */
	public void setCheckListDisplayName(String checkListDisplayName) {
		this.checkListDisplayName = checkListDisplayName;
	}
	
	/**
	 * @return the checkListId
	 */
	public String getCheckListId() {
		return checkListId;
	}
	
	/**
	 * @param checkListId the checkListId to set
	 */
	public void setCheckListId(String checkListId) {
		this.checkListId = checkListId;
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
	 * @return the endecaSeoUrl
	 */
	public String getEndecaSeoUrl() {
		return endecaSeoUrl;
	}
	
	/**
	 * @param endecaSeoUrl the endecaSeoUrl to set
	 */
	public void setEndecaSeoUrl(String endecaSeoUrl) {
		this.endecaSeoUrl = endecaSeoUrl;
	}
	
	/**
	 * @return the seoUrlDimensionId
	 */
	public String getSeoUrlDimensionId() {
		return seoUrlDimensionId;
	}
	
	/**
	 * @param seoUrlDimensionId the seoUrlDimensionId to set
	 */
	public void setSeoUrlDimensionId(String seoUrlDimensionId) {
		this.seoUrlDimensionId = seoUrlDimensionId;
	}
	
	/**
	 * @return the productCountUS
	 */
	public long getProductCountUS() {
		return productCountUS;
	}
	
	/**
	 * @param productCountUS the productCountUS to set
	 */
	public void setProductCountUS(long productCountUS) {
		this.productCountUS = productCountUS;
	}
	
	/**
	 * @return the productCountBaby
	 */
	public long getProductCountBaby() {
		return productCountBaby;
	}
	
	/**
	 * @param productCountBaby the productCountBaby to set
	 */
	public void setProductCountBaby(long productCountBaby) {
		this.productCountBaby = productCountBaby;
	}
	
	/**
	 * @return the productCountCa
	 */
	public long getProductCountCa() {
		return productCountCa;
	}
	
	/**
	 * @param productCountCa the productCountCa to set
	 */
	public void setProductCountCa(long productCountCa) {
		this.productCountCa = productCountCa;
	}
	
	/**
	 * @return the checkListCategoryName
	 */
	public String getCheckListCategoryName() {
		return checkListCategoryName;
	}
	
	/**
	 * @param checkListCategoryName the checkListCategoryName to set
	 */
	public void setCheckListCategoryName(String checkListCategoryName) {
		this.checkListCategoryName = checkListCategoryName;
	}
	
	
	
	/**
	 * @return the siblingsSeoUrls
	 */
	public List<CheckListSeoUrlHierarchy> getSiblingsSeoUrls() {
		return siblingsSeoUrls;
	}
	
	/**
	 * @param siblingsSeoUrls the siblingsSeoUrls to set
	 */
	public void setSiblingsSeoUrls(List<CheckListSeoUrlHierarchy> siblingsSeoUrls) {
		this.siblingsSeoUrls = siblingsSeoUrls;
	}
	
	/**
	 * @return the childsSeoUrls
	 */
	public List<CheckListSeoUrlHierarchy> getChildsSeoUrls() {
		return childsSeoUrls;
	}
	
	/**
	 * @param childsSeoUrls the childsSeoUrls to set
	 */
	public void setChildsSeoUrls(List<CheckListSeoUrlHierarchy> childsSeoUrls) {
		this.childsSeoUrls = childsSeoUrls;
	}
	
	/**
	 * @return the processed
	 */
	public boolean isProcessed() {
		return processed;
	}
	
	/**
	 * @param processed the processed to set
	 */
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	

	/**
	 * @return the categoryLevel
	 */
	public int getCategoryLevel() {
		return categoryLevel;
	}
	

	/**
	 * @param categoryLevel the categoryLevel to set
	 */
	public void setCategoryLevel(int categoryLevel) {
		this.categoryLevel = categoryLevel;
	}

	

	/**
	 * @return the productCount
	 */
	public long getProductCount() {
		String siteId=SiteContextManager.getCurrentSiteId();
		if(BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(siteId)){
			return getProductCountUS();
		}else if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteId)){
			return getProductCountBaby();
		}else if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
			return getProductCountCa();
		}
		else  return productCount;
	}
	
	
	/**
	 * @return the parentSeoUrl
	 */
	public CheckListSeoUrlHierarchy getParentSeoUrl() {
		return parentSeoUrl;
	}
	

	/**
	 * @param parentSeoUrl the parentSeoUrl to set
	 */
	public void setParentSeoUrl(CheckListSeoUrlHierarchy parentSeoUrl) {
		this.parentSeoUrl = parentSeoUrl;
	}
	

	/**
	 * @param productCount the productCount to set
	 */
	public void setProductCount(long productCount) {
		this.productCount = productCount;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	

	/**
	 * @return the babyCategoryURL
	 */
	public String getBabyCategoryURL() {
		return babyCategoryURL;
	}
	

	/**
	 * @param babyCategoryURL the babyCategoryURL to set
	 */
	public void setBabyCategoryURL(String babyCategoryURL) {
		this.babyCategoryURL = babyCategoryURL;
	}
	

	/**
	 * @return the caCategoryURL
	 */
	public String getCaCategoryURL() {
		return caCategoryURL;
	}
	

	/**
	 * @param caCategoryURL the caCategoryURL to set
	 */
	public void setCaCategoryURL(String caCategoryURL) {
		this.caCategoryURL = caCategoryURL;
	}
	

	/**
	 * @return the tbsCategoryURL
	 */
	public String getTbsCategoryURL() {
		return tbsCategoryURL;
	}
	

	/**
	 * @param tbsCategoryURL the tbsCategoryURL to set
	 */
	public void setTbsCategoryURL(String tbsCategoryURL) {
		this.tbsCategoryURL = tbsCategoryURL;
	}
	

	/**
	 * @return the categoryURL
	 */
	public String getCategoryURL() {
	 return categoryURL;
	}
	
	 
	
	/**
	 * @return the siteURL
	 */
	public String getSiteURL() {
		String siteId=SiteContextManager.getCurrentSiteId();
		if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteId)){
			return getBabyCategoryURL();
		}else if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
			return getCaCategoryURL();
		}else if(siteId  != null && siteId.contains(BBBCoreConstants.TBS)){
			return getTbsCategoryURL();
		}
		else return getCategoryURL();
	}
	

	/**
	 * @param siteURL the siteURL to set
	 */
	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}
	/**
	 * @param categoryURL the categoryURL to set
	 */
	public void setCategoryURL(String categoryURL) {
		this.categoryURL = categoryURL;
	}

	/**
	 * @return the categoryEnabled
	 */
	public boolean isCategoryEnabled() {
		return categoryEnabled;
	}
	

	/**
	 * @param categoryEnabled the categoryEnabled to set
	 */
	public void setCategoryEnabled(boolean categoryEnabled) {
		this.categoryEnabled = categoryEnabled;
	}
	

	/**
	 * @return the regTypeCheckList
	 */
	public boolean isRegTypeCheckList() {
		return regTypeCheckList;
	}
	

	/**
	 * @param regTypeCheckList the regTypeCheckList to set
	 */
	public void setRegTypeCheckList(boolean regTypeCheckList) {
		this.regTypeCheckList = regTypeCheckList;
	}
	

	/**
	 * @return the checkListDisabled
	 */
	public boolean isCheckListDisabled() {
		return checkListDisabled;
	}
	

	/**
	 * @param checkListDisabled the checkListDisabled to set
	 */
	public void setCheckListDisabled(boolean checkListDisabled) {
		this.checkListDisabled = checkListDisabled;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckListSeoUrlHierarchy [checkListDisplayName=" + checkListDisplayName + ", checkListId=" + checkListId
				+ ", seoUrl=" + seoUrl + ", endecaSeoUrl=" + endecaSeoUrl + ", seoUrlDimensionId=" + seoUrlDimensionId
				+ ", productCount=" + productCount + ", productCountUS=" + productCountUS + ", productCountBaby="
				+ productCountBaby + ", productCountCa=" + productCountCa + ", babyCategoryURL=" + babyCategoryURL
				+ ", caCategoryURL=" + caCategoryURL + ", tbsCategoryURL=" + tbsCategoryURL + ", categoryURL="
				+ categoryURL + ", siteURL=" + siteURL + ", checkListCategoryName=" + checkListCategoryName
				+ ", categoryLevel=" + categoryLevel + ", processed=" + processed + ", categoryEnabled="
				+ categoryEnabled + ", regTypeCheckList=" + regTypeCheckList + ", checkListDisabled="
				+ checkListDisabled + "]";
	}
	

	
}
