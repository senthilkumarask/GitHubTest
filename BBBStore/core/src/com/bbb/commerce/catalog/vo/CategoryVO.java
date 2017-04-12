package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.search.bean.result.SortOptionVO;

import atg.core.util.StringUtils;
//import atg.repository.RepositoryItem;

public class CategoryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String categoryId;
	private String categoryName;
	private String categoryImage;
	private String categoryDisplayType;
	private List<CategoryVO> subCategories;
	private List<String> childProducts;
	private Boolean isCollege ;
	private List<String> categoryKeywords;
	private String guideId;
	private String seoURL;
	private Boolean isChatEnabled;
	private String defaultViewValue;
	private String errorMessage;
	private boolean isErrorExist;
	private String errorCode;
	private BCCManagedPromoCategoryVO bccManagedPromoCategoryVO;
	private List<CategoryVO> siblingCategories;
	private String catDescription;
	
	public String getCatDescription() {
		return catDescription;
	}
	
	public void setCatDescription(String catDescription) {
		this.catDescription = catDescription;
	}
	/**
	 * @return the siblingCategories
	 */
	public List<CategoryVO> getSiblingCategories() {
		return siblingCategories;
	}
	/**
	 * @param siblingCategories the siblingCategories to set
	 */
	public void setSiblingCategories(List<CategoryVO> siblingCategories) {
		this.siblingCategories = siblingCategories;
	}
	/**
	 * @return the bccManagedPromoCategoryVO
	 */
	public BCCManagedPromoCategoryVO getBccManagedPromoCategoryVO() {
		return bccManagedPromoCategoryVO;
	}
	/**
	 * @param bccManagedPromoCategoryVO the bccManagedPromoCategoryVO to set
	 */
	public void setBccManagedPromoCategoryVO(
			BCCManagedPromoCategoryVO bccManagedPromoCategoryVO) {
		this.bccManagedPromoCategoryVO = bccManagedPromoCategoryVO;
	}
	//@author psin52 Added for story 117-A5 | Grid/List view configurable -	|START
	/**
	 * 
	 * @return defaultViewValue
	 */
	public String getDefaultViewValue() {
		return defaultViewValue;
	}
	/**
	 * 
	 * @param defaultViewValue
	 */
	
	public void setDefaultViewValue(String defaultViewValue) {
		this.defaultViewValue = defaultViewValue;
	}
	
	//@author psin52 Added for story 117-A5 | Grid/List view configurable -	|END

	private String mChatURL;
	private String mChatCode;
	private String chatLinkPlaceholder;
	private SortOptionVO sortOptionVO;
	private Boolean phantomCategory;
	private String displayAskAndAnswer;
	private String bannerContent;
	private String cssFilePath;
	private String jsFilePath;
	/**
	 * @return the bannerContent
	 */
	public String getBannerContent() {
		return bannerContent;
	}
	/**
	 * @param bannerContent the bannerContent to set
	 */
	public void setBannerContent(String bannerContent) {
		this.bannerContent = bannerContent;
	}
	/**
	 * @return the cssFilePath
	 */
	public String getCssFilePath() {
		return cssFilePath;
	}
	/**
	 * @param cssFilePath the cssFilePath to set
	 */
	public void setCssFilePath(String cssFilePath) {
		this.cssFilePath = cssFilePath;
	}
	/**
	 * @return the jsFilePath
	 */
	public String getJsFilePath() {
		return jsFilePath;
	}
	/**
	 * @param jsFilePath the jsFilePath to set
	 */
	public void setJsFilePath(String jsFilePath) {
		this.jsFilePath = jsFilePath;
	}

	/**
	 * @return the displayAskAndAnswer
	 */
	public String getDisplayAskAndAnswer() {
	    return displayAskAndAnswer;
	}
	/**
	 * @param displayAskAndAnswer the displayAskAndAnswer to set
	 */
	public void setDisplayAskAndAnswer(String displayAskAndAnswer) {
	    this.displayAskAndAnswer = displayAskAndAnswer;
	}
	
	/**
	 * @return the sortOptionVO
	 */
	public SortOptionVO getSortOptionVO() {
		return sortOptionVO;
	}
	/**
	 * @param sortOptionVO the sortOptionVO to set
	 */
	public void setSortOptionVO(SortOptionVO sortOptionVO) {
		this.sortOptionVO = sortOptionVO;
	}
	/**
	 * @return the chatLinkPlaceholder
	 */
	public String getChatLinkPlaceholder() {
		return chatLinkPlaceholder;
	}
	/**
	 * @param chatLinkPlaceholder the chatLinkPlaceholder to set
	 */
	public void setChatLinkPlaceholder(String chatLinkPlaceholder) {
		this.chatLinkPlaceholder = chatLinkPlaceholder;
	}
	/**
	 * @return the chatURL
	 */
	public String getChatURL() {
		return mChatURL;
	}
	/**
	 * @param pChatURL the chatURL to set
	 */
	public void setChatURL(String pChatURL) {
		mChatURL = pChatURL;
	}
	/**
	 * @return the chatCode
	 */
	public String getChatCode() {
		return mChatCode;
	}
	/**
	 * @param pChatCode the chatCode to set
	 */
	public void setChatCode(String pChatCode) {
		mChatCode = pChatCode;
	}	
	/**
	 * @return the phantomCategory
	 */
	public Boolean getPhantomCategory() {
		return phantomCategory;
	}
	/**
	 * @param phantomCategory the phantomCategory to set
	 */
	public void setPhantomCategory(Boolean phantomCategory) {
		this.phantomCategory = phantomCategory;
	}

	/*
	 * Added for BV Product feed
	 */	
	private String categoryPageUrl;
	/**
	 * @return the isChatEnabled
	 */
	public Boolean getIsChatEnabled() {
		return isChatEnabled;
	}
	/**
	 * @param pIsChatEnabled the isChatEnabled to set
	 */
	public void setChatEnabled(Boolean pIsChatEnabled) {
		isChatEnabled = pIsChatEnabled;
	}
	
	/**
	 * @return the isChatEnabled
	 */
	public Boolean getChatEnabled() {
		return isChatEnabled;
	}
	
	private List<String> parentCategories;
	private Map<String, String> regionalCatUrlMap;
	private Map<String, String> regionalImgUrlMap;
	private String zoomValue;	
	
	public CategoryVO() {
		// TODO Auto-generated constructor stub
	}
	public CategoryVO(String categoryId,String categoryName,String categoryImage,String categoryDisplayType,
			List<CategoryVO> subCategories,List<String> childProducts){
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.categoryImage = categoryImage;
		this.categoryDisplayType = categoryDisplayType;
		this.subCategories = subCategories;
		this.childProducts = childProducts;
	}

	/**
	 * @return the seoURL
	 */	
	public String getSeoURL() {
		return seoURL;
	}

	/**
	 * @param seoURL the seoURL to set
	 */	
	public void setSeoURL(String seoURL) {
		this.seoURL = seoURL;
	}
	
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the categoryImage
	 */
	public String getCategoryImage() {
		return categoryImage;
	}

	/**
	 * @param categoryImage the categoryImage to set
	 */
	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	/**
	 * @return the categoryDisplayType
	 */
	public String getCategoryDisplayType() {
		return categoryDisplayType;
	}

	/**
	 * @param categoryDisplayType the categoryDisplayType to set
	 */
	public void setCategoryDisplayType(String categoryDisplayType) {
		this.categoryDisplayType = categoryDisplayType;
	}

	/**
	 * @return the subCategories
	 */
	public List<CategoryVO> getSubCategories() {
		return subCategories;
	}

	/**
	 * @param subCategories the subCategories to set
	 */
	public void setSubCategories(List<CategoryVO> subCategories) {
		this.subCategories = subCategories;
	}

	/**
	 * @return the childProducts
	 */
	public List<String> getChildProducts() {
		return childProducts;
	}

	/**
	 * @param childProducts the childProducts to set
	 */
	public void setChildProducts(List<String> childProducts) {
		this.childProducts = childProducts;
	}
	/**
	 * @return the isCollege
	 */
	public Boolean getIsCollege() {
		return isCollege;
	}
	/**
	 * @param isCollege the isCollege to set
	 */
	public void setIsCollege(Boolean isCollege) {
		this.isCollege = isCollege;
	}
	/**
	 * @return the categoryKeywords
	 */
	public List<String> getCategoryKeywords() {
		return categoryKeywords;
	}
	/**
	 * @param categoryKeywords the categoryKeywords to set
	 */
	public void setCategoryKeywords(List<String> categoryKeywords) {
		this.categoryKeywords = categoryKeywords;
	}
	/**
	 * @return the guideId
	 */
	public String getGuideId() {
		return guideId;
	}
	/**
	 * @param guideId the guideId to set
	 */
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}
	public String getCategoryPageUrl() {
		return categoryPageUrl;
	}
	public void setCategoryPageUrl(String categoryPageUrl) {
		this.categoryPageUrl = categoryPageUrl;
	}

	public List<String> getParentCategories() {
		return parentCategories;
	}
	public void setParentCategories(List<String> parentCategories) {
		this.parentCategories = parentCategories;
	}
	public Map<String, String> getRegionalCatUrlMap() {
		return regionalCatUrlMap;
	}
	public void setRegionalCatUrlMap(Map<String, String> regionalCatUrlMap) {
		this.regionalCatUrlMap = regionalCatUrlMap;
	}
	public Map<String, String> getRegionalImgUrlMap() {
		return regionalImgUrlMap;
	}
	public void setRegionalImgUrlMap(Map<String, String> regionalImgUrlMap) {
		this.regionalImgUrlMap = regionalImgUrlMap;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Category details for category id \n ");
		toString.append(categoryId).append("\n")
		.append("Category Name").append(categoryName).append("\n")
		.append("Category Image").append(StringUtils.isEmpty(categoryImage)?"NULL":categoryImage).append("\n")
		.append("Category Display Type").append(StringUtils.isEmpty(categoryDisplayType)?"NULL":categoryDisplayType).append("\n");
		if(subCategories!=null && subCategories.isEmpty()){
			toString.append("\n********Sub Categories in the Category*********** ").append(" \n");
			int i=0;
			for(CategoryVO catVO:subCategories){
				toString.append(++i+") ")
				.append(catVO!=null?catVO.toString():" NULL ").append("\n");
			}
		}
		else{
			toString.append(" No Sub categories for the Category ");
		}
		if(childProducts!=null && childProducts.isEmpty()){
			toString.append("\n********Child Products in the Category*********** ").append(" \n");
			int i=0;
			for(String childProd:childProducts){
				toString.append(++i+") ")
				.append(childProd!=null?childProd:" NULL ").append("\n");
			}
		}
		else{
			toString.append(" No Child Products for the Category ");
		}
		toString.append(" Guide Id value ").append(StringUtils.isEmpty(guideId)?"NULL":guideId).append("\n")
		.append(" Is College flag value ").append(isCollege!=null?isCollege:"NULL").append("\n");
		if(categoryKeywords!=null && categoryKeywords.isEmpty()){
			toString.append("\n********Category Keywords*********** ").append(" \n");
			int i=0;
			for(String keywrds:categoryKeywords){
				toString.append(++i+") ")
				.append(!StringUtils.isEmpty(keywrds)?keywrds:" NULL ").append("\n");
			}
		}
		else{
			toString.append(" No category Keywords available for the Category ");
		}
		return toString.toString();
	}
	public void setZoomValue(String zoomValue) {
		this.zoomValue = zoomValue;
	}
	public String getZoomValue() {
		return zoomValue;
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
