/*
 *
 * File  : CreateRegistryResVO.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry.vo;

import java.util.List;

import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.checklist.vo.MyItemCategoryVO;
import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistryResVO.
 *
 * @author skumar134
 */
public class RestRegistryInfoDetailVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private RegistryResVO registryResVO;
    
    private List<String> priceRangeList;
	private List<RegistryCategoryBucketVO> categoryBuckets;
    private List<RegistryCategoriesVO> categoryForRegistry;
    private String isListEmpty;
    private String sortSequence;
    private String skuList;
    private List<RegistryCategoryBucketVO> inStockCategoryBuckets;
    private List<RegistryCategoryBucketVO> notInStockCategoryBuckets;
    private int count;
    private boolean outOfStockListFlag;
    private PromoBoxVO promoBox;
    private boolean errorExist;
    private String errorCode;
    private String errorMessage;
    private int[] recommendationCount;
    private int emailOptIn;
    private int recommendationSize;
    private String showStartBrowsing;
    private String isPOBoxAddress;
    private List<MyItemCategoryVO> ephCategoryBuckets;
    private String expandedCategoryId;
    private List<MyItemCategoryVO> inStockEPHCategoryBuckets;
    private List<MyItemCategoryVO> notInStockEPHCategoryBuckets;
    public List<RegistryItemVO> items;
    public String other;
    private String qty;
    private boolean firstRegistry;
    private boolean coRegOwner;
	/**
	 * @return the promoBox
	 */
	public PromoBoxVO getPromoBox() {
		return promoBox;
	}
	/**
	 * @param promoBox the promoBox to set
	 */
	public void setPromoBox(PromoBoxVO promoBox) {
		this.promoBox = promoBox;
	}
	
	
	
	public List<String> getPriceRangeList() {
		return priceRangeList;
	}
	public void setPriceRangeList(List<String> priceRangeList) {
		this.priceRangeList = priceRangeList;
	}
	public List<RegistryCategoryBucketVO> getCategoryBuckets() {
		return categoryBuckets;
	}
	public void setCategoryBuckets(List<RegistryCategoryBucketVO> categoryBuckets) {
		this.categoryBuckets = categoryBuckets;
	}
	
	public void setInStockCategoryBuckets(
			List<RegistryCategoryBucketVO> inStockCategoryBuckets) {
		this.inStockCategoryBuckets = inStockCategoryBuckets;
	}
	public void setNotInStockCategoryBuckets(
			List<RegistryCategoryBucketVO> notInStockCategoryBuckets) {
		this.notInStockCategoryBuckets = notInStockCategoryBuckets;
	}
	public String getIsListEmpty() {
		return isListEmpty;
	}
	public void setIsListEmpty(String isListEmpty) {
		this.isListEmpty = isListEmpty;
	}
	public String getSortSequence() {
		return sortSequence;
	}
	public void setSortSequence(String sortSequence) {
		this.sortSequence = sortSequence;
	}
	public String getSkuList() {
		return skuList;
	}
	public void setSkuList(String skuList) {
		this.skuList = skuList;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isOutOfStockListFlag() {
		return outOfStockListFlag;
	}
	public void setOutOfStockListFlag(boolean outOfStockListFlag) {
		this.outOfStockListFlag = outOfStockListFlag;
	}
	public List<RegistryCategoryBucketVO> getInStockCategoryBuckets() {
		return inStockCategoryBuckets;
	}
	public List<RegistryCategoryBucketVO> getNotInStockCategoryBuckets() {
		return notInStockCategoryBuckets;
	}
	public RegistryResVO getRegistryResVO() {
		return registryResVO;
	}
	public void setRegistryResVO(RegistryResVO registryResVO) {
		this.registryResVO = registryResVO;
	}
	public List<RegistryCategoriesVO> getCategoryForRegistry() {
		return categoryForRegistry;
	}
	public void setCategoryForRegistry(
			List<RegistryCategoriesVO> categoryForRegistry) {
		this.categoryForRegistry = categoryForRegistry;
	}
	public boolean isErrorExist() {
		return errorExist;
	}
	public void setErrorExist(boolean errorExist) {
		this.errorExist = errorExist;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int[] getRecommendationCount() {
		return recommendationCount;
	}
	public void setRecommendationCount(int[] recommendationCount) {
		this.recommendationCount = recommendationCount;
	}
	public int getEmailOptIn() {
		return emailOptIn;
	}
	public void setEmailOptIn(int emailOptIn) {
		this.emailOptIn = emailOptIn;
	}
	public int getRecommendationSize() {
		return recommendationSize;
	}
	public void setRecommendationSize(int recommendationSize) {
		this.recommendationSize = recommendationSize;
	}
	public String getShowStartBrowsing() {
		return showStartBrowsing;
	}
	public void setShowStartBrowsing(String showStartBrowsing) {
		this.showStartBrowsing = showStartBrowsing;
	}
	public String getIsPOBoxAddress() {
		return isPOBoxAddress;
	}
	public void setIsPOBoxAddress(String isPOBoxAddress) {
		this.isPOBoxAddress = isPOBoxAddress;
	}


	public List<MyItemCategoryVO> getNotInStockEPHCategoryBuckets() {
		return notInStockEPHCategoryBuckets;
	}
	public void setNotInStockEPHCategoryBuckets(
			List<MyItemCategoryVO> notInStockEPHCategoryBuckets) {
		this.notInStockEPHCategoryBuckets = notInStockEPHCategoryBuckets;
	}
	public List<MyItemCategoryVO> getInStockEPHCategoryBuckets() {
		return inStockEPHCategoryBuckets;
	}
	public void setInStockEPHCategoryBuckets(
			List<MyItemCategoryVO> inStockEPHCategoryBuckets) {
		this.inStockEPHCategoryBuckets = inStockEPHCategoryBuckets;
	}
	public String getExpandedCategoryId() {
		return expandedCategoryId;
	}
	public void setExpandedCategoryId(String expandedCategoryId) {
		this.expandedCategoryId = expandedCategoryId;
	}
	public List<MyItemCategoryVO> getEphCategoryBuckets() {
		return ephCategoryBuckets;
	}
	public void setEphCategoryBuckets(List<MyItemCategoryVO> ephCategoryBuckets) {
		this.ephCategoryBuckets = ephCategoryBuckets;
	}
	public List<RegistryItemVO> getItems() {
		return items;
	}
	public void setItems(List<RegistryItemVO> items) {
		this.items = items;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	/**
	 * @return the firstRegistry
	 */
	public boolean isFirstRegistry() {
		return firstRegistry;
	}
	/**
	 * @param firstRegistry the firstRegistry to set
	 */
	public void setFirstRegistry(boolean firstRegistry) {
		this.firstRegistry = firstRegistry;
	}
	/**
	 * @return the coRegOwner
	 */
	public boolean isCoRegOwner() {
		return coRegOwner;
	}
	/**
	 * @param coRegOwner the coRegOwner to set
	 */
	public void setCoRegOwner(boolean coRegOwner) {
		this.coRegOwner = coRegOwner;
	}
	
}