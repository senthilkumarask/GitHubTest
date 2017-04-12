package com.bbb.commerce.checklist.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.giftregistry.vo.RegistryItemVO;

/**
 * The Class MyItemCategoryVO.
 */
public class MyItemCategoryVO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The category id. */
	private String categoryId;
	
	/** The display name. */
	private String displayName;
	
	/** The category url. */
	private String categoryURL;
	private String uscategoryURL;
	private String babycategoryURL;
	private String cacategoryURL;
	/** The sequence number. */
	private int sequenceNumber;
	
	/** The primary parent category id. */
	private String primaryParentCategoryId;
	
	/** The child categories list. */
	private List<MyItemCategoryVO> childCategoryVO;
	
	/** The registry items. */
	private List<RegistryItemVO> registryItems;
	
	/** The registry items count. */
	private int registryItemsCount;
	
	/** The show on rlp. */
	private boolean showOnRlp;
	
	/** The always show on rlp. */
	private boolean alwaysShowOnRlp;
	
	/** The disabled. */
	private boolean disabled;
	
	private boolean usOverriddenURL;
	private boolean caOverriddenURL;
	private boolean babyOverriddenURL;
	/**
	 * @return the usOverriddenURL
	 */
	public boolean isUsOverriddenURL() {
		return usOverriddenURL;
	}
	
	/**
	 * @param usOverriddenURL the usOverriddenURL to set
	 */
	public void setUsOverriddenURL(boolean usOverriddenURL) {
		this.usOverriddenURL = usOverriddenURL;
	}
	
	/**
	 * @return the caOverriddenURL
	 */
	public boolean isCaOverriddenURL() {
		return caOverriddenURL;
	}
	
	/**
	 * @param caOverriddenURL the caOverriddenURL to set
	 */
	public void setCaOverriddenURL(boolean caOverriddenURL) {
		this.caOverriddenURL = caOverriddenURL;
	}
	
	/**
	 * @return the babyOverriddenURL
	 */
	public boolean isBabyOverriddenURL() {
		return babyOverriddenURL;
	}
	
	/**
	 * @param babyOverriddenURL the babyOverriddenURL to set
	 */
	public void setBabyOverriddenURL(boolean babyOverriddenURL) {
		this.babyOverriddenURL = babyOverriddenURL;
	}

	/**
	 * Gets the category id.
	 *
	 * @return the category id
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * Sets the category id.
	 *
	 * @param categoryId the new category id
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName the new display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Gets the category url.
	 *
	 * @return the category url
	 */
	public String getCategoryURL() {
		return categoryURL;
	}

	/**
	 * Sets the category url.
	 *
	 * @param categoryURL the new category url
	 */
	public void setCategoryURL(String categoryURL) {
		this.categoryURL = categoryURL;
	}

	/**
	 * Gets the sequence number.
	 *
	 * @return the sequence number
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * Sets the sequence number.
	 *
	 * @param sequenceNumber the new sequence number
	 */
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Gets the primary parent category id.
	 *
	 * @return the primary parent category id
	 */
	public String getPrimaryParentCategoryId() {
		return primaryParentCategoryId;
	}

	/**
	 * Sets the primary parent category id.
	 *
	 * @param primaryParentCategoryId the new primary parent category id
	 */
	public void setPrimaryParentCategoryId(String primaryParentCategoryId) {
		this.primaryParentCategoryId = primaryParentCategoryId;
	}

	/**
	 * Gets the registry items.
	 *
	 * @return the registry items
	 */
	public List<RegistryItemVO> getRegistryItems() {
		return registryItems;
	}

	/**
	 * Sets the registry items.
	 *
	 * @param registryItems the new registry items
	 */
	public void setRegistryItems(List<RegistryItemVO> registryItems) {
		this.registryItems = registryItems;
	}

	/**
	 * Gets the registry items count.
	 *
	 * @return the registry items count
	 */
	public Integer getRegistryItemsCount() {
		return registryItemsCount;
	}

	/**
	 * Sets the registry items count.
	 *
	 * @param registryItemsCount the new registry items count
	 */
	public void setRegistryItemsCount(Integer registryItemsCount) {
		this.registryItemsCount = registryItemsCount;
	}

	/**
	 * Checks if is show on rlp.
	 *
	 * @return true, if is show on rlp
	 */
	public boolean isShowOnRlp() {
		return showOnRlp;
	}

	/**
	 * Sets the show on rlp.
	 *
	 * @param showOnRlp the new show on rlp
	 */
	public void setShowOnRlp(boolean showOnRlp) {
		this.showOnRlp = showOnRlp;
	}

	/**
	 * Checks if is always show on rlp.
	 *
	 * @return true, if is always show on rlp
	 */
	public boolean isAlwaysShowOnRlp() {
		return alwaysShowOnRlp;
	}

	/**
	 * Sets the always show on rlp.
	 *
	 * @param alwaysShowOnRlp the new always show on rlp
	 */
	public void setAlwaysShowOnRlp(boolean alwaysShowOnRlp) {
		this.alwaysShowOnRlp = alwaysShowOnRlp;
	}

	/**
	 * Checks if is disabled.
	 *
	 * @return true, if is disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Sets the disabled.
	 *
	 * @param disabled the new disabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}


	public void setRegistryItemsCount(int registryItemsCount) {
		this.registryItemsCount = registryItemsCount;
	}

	public String getUscategoryURL() {
		return uscategoryURL;
	}

	public void setUscategoryURL(String uscategoryURL) {
		this.uscategoryURL = uscategoryURL;
	}

	public String getBabycategoryURL() {
		return babycategoryURL;
	}

	public void setBabycategoryURL(String babycategoryURL) {
		this.babycategoryURL = babycategoryURL;
	}

	public String getCacategoryURL() {
		return cacategoryURL;
	}

	public void setCacategoryURL(String cacategoryURL) {
		this.cacategoryURL = cacategoryURL;
	}

	public List<MyItemCategoryVO> getChildCategoryVO() {
		return childCategoryVO;
	}

	public void setChildCategoryVO(List<MyItemCategoryVO> childCategoryVO) {
		this.childCategoryVO = childCategoryVO;
	}

}
