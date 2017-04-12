/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Class GuestRegistryItemsListVO.
 * This bean would hold the mapping of Category Id to List of RegistryItemsAssociated with that categoryId
 */
public class GuestRegistryItemsFirstCallVO implements Serializable  {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private List<GuestRegistryItemsListVO> remainingCategoryBuckets;
	private List<String> notInStockCategoryList;
	private GuestRegistryItemsListVO categoryBuckets;
	private boolean showStartBrowsing;
	private String regItemCount;
	
	public GuestRegistryItemsListVO getCategoryBuckets() {
		return categoryBuckets;
	}
	public void setCategoryBuckets(GuestRegistryItemsListVO categoryBuckets) {
		this.categoryBuckets = categoryBuckets;
	}
	public boolean isShowStartBrowsing() {
		return showStartBrowsing;
	}
	public void setShowStartBrowsing(boolean showStartBrowsing) {
		this.showStartBrowsing = showStartBrowsing;
	}
	public String getRegItemCount() {
		return regItemCount;
	}
	public void setRegItemCount(String regItemCount) {
		this.regItemCount = regItemCount;
	}
	public List<GuestRegistryItemsListVO> getRemainingCategoryBuckets() {
		return remainingCategoryBuckets;
	}
	public void setRemainingCategoryBuckets(
			List<GuestRegistryItemsListVO> remainingCategoryBuckets) {
		this.remainingCategoryBuckets = remainingCategoryBuckets;
	}
	public List<String> getNotInStockCategoryList() {
		return notInStockCategoryList;
	}
	public void setNotInStockCategoryList(List<String> notInStockCategoryList) {
		this.notInStockCategoryList = notInStockCategoryList;
	}
	
	
}
