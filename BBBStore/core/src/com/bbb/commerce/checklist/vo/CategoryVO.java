/**
 * 
 */
package com.bbb.commerce.checklist.vo;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistryItemsListVO.
 *
 * @author skalr2
 */
public class CategoryVO  implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private String categoryId;
	private String categoryName;
	private String displayName;
	private String categoryURL;
	private int sequenceNumber;
	private String imageURL;
	private String uscategoryURL;
	private String babyImageURL;
	private String babycategoryURL;
	private String cacategoryURL;
	private String caImageURL;
	private int suggestedQuantity;
	private String primaryParentCategoryId;
	private int c2CompleteCount;
	private boolean c3ManualComplete;
	private boolean c3AutoComplete;
	private int numberOfC3;
	private int addedQuantity;
	private double c1PercentageComplete;
	private int totalAddedQuantityForPercentageCalc;
	private int totalSuggestedQuantityForPercentageCalc;
	private String labelMessage;
	private List<CategoryVO> childCategoryVO;
	private boolean showCheckList;
	private boolean disabled;
	private boolean configureComplete;
	private boolean isDeleted;
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
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public boolean isConfigureComplete() {
		return configureComplete;
	}
	public void setConfigureComplete(boolean configureComplete) {
		this.configureComplete = configureComplete;
	}
	private boolean showOnRlp;
	private boolean alwaysShowOnRlp;
	
	
	
	
	
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
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the categoryURL
	 */
	public String getCategoryURL() {
		return categoryURL;
	}
	/**
	 * @param categoryURL the categoryURL to set
	 */
	public void setCategoryURL(String categoryURL) {
		this.categoryURL = categoryURL;
	}
	/**
	 * @return the sequenceNumber
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}
	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	/**
	 * @return the suggestedQuantity
	 */
	public int getSuggestedQuantity() {
		return suggestedQuantity;
	}
	/**
	 * @param suggestedQuantity the suggestedQuantity to set
	 */
	public void setSuggestedQuantity(int suggestedQuantity) {
		this.suggestedQuantity = suggestedQuantity;
	}
	/**
	 * @return the primaryParentCategoryId
	 */
	public String getPrimaryParentCategoryId() {
		return primaryParentCategoryId;
	}
	/**
	 * @param primaryParentCategoryId the primaryParentCategoryId to set
	 */
	public void setPrimaryParentCategoryId(String primaryParentCategoryId) {
		this.primaryParentCategoryId = primaryParentCategoryId;
	}
	
	/**
	 * @return the labelMessage
	 */
	public String getLabelMessage() {
		return labelMessage;
	}
	/**
	 * @param labelMessage the labelMessage to set
	 */
	public void setLabelMessage(String labelMessage) {
		this.labelMessage = labelMessage;
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
	 * @return the numberOfC3
	 */
	public int getNumberOfC3() {
		return numberOfC3;
	}
	/**
	 * @param numberOfC3 the numberOfC3 to set
	 */
	public void setNumberOfC3(int numberOfC3) {
		this.numberOfC3 = numberOfC3;
	}
	/**
	 * @return the addedQuantity
	 */
	public int getAddedQuantity() {
		return addedQuantity;
	}
	/**
	 * @param addedQuantity the addedQuantity to set
	 */
	public void setAddedQuantity(int addedQuantity) {
		this.addedQuantity = addedQuantity;
	}
	/**
	 * @return the c3AutoComplete
	 */
	public boolean isC3AutoComplete() {
		return c3AutoComplete;
	}
	/**
	 * @param c3AutoComplete the c3AutoComplete to set
	 */
	public void setC3AutoComplete(boolean c3AutoComplete) {
		this.c3AutoComplete = c3AutoComplete;
	}
	/**
	 * @return the c3ManualComplete
	 */
	public boolean isC3ManualComplete() {
		return c3ManualComplete;
	}
	/**
	 * @param c3ManualComplete the c3ManualComplete to set
	 */
	public void setC3ManualComplete(boolean c3ManualComplete) {
		this.c3ManualComplete = c3ManualComplete;
	}
	/**
	 * @return the c2CompleteCount
	 */
	public int getC2CompleteCount() {
		return c2CompleteCount;
	}
	/**
	 * @param c2CompleteCount the c2CompleteCount to set
	 */
	public void setC2CompleteCount(int c2CompleteCount) {
		this.c2CompleteCount = c2CompleteCount;
	}
	/**
	 * @return the totalAddedQuantityForPercentageCalc
	 */
	public int getTotalAddedQuantityForPercentageCalc() {
		return totalAddedQuantityForPercentageCalc;
	}
	/**
	 * @param totalAddedQuantityForPercentageCalc the totalAddedQuantityForPercentageCalc to set
	 */
	public void setTotalAddedQuantityForPercentageCalc(
			int totalAddedQuantityForPercentageCalc) {
		this.totalAddedQuantityForPercentageCalc = totalAddedQuantityForPercentageCalc;
	}
	/**
	 * @return the totalSuggestedQuantityForPercentageCalc
	 */
	public int getTotalSuggestedQuantityForPercentageCalc() {
		return totalSuggestedQuantityForPercentageCalc;
	}
	/**
	 * @param totalSuggestedQuantityForPercentageCalc the totalSuggestedQuantityForPercentageCalc to set
	 */
	public void setTotalSuggestedQuantityForPercentageCalc(
			int totalSuggestedQuantityForPercentageCalc) {
		this.totalSuggestedQuantityForPercentageCalc = totalSuggestedQuantityForPercentageCalc;
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
	
	public boolean isShowCheckList() {
		return showCheckList;
	}
	public void setShowCheckList(boolean showCheckList) {
		this.showCheckList = showCheckList;
	}
	public boolean isShowOnRlp() {
		return showOnRlp;
	}
	public void setShowOnRlp(boolean showOnRlp) {
		this.showOnRlp = showOnRlp;
	}
	public boolean isAlwaysShowOnRlp() {
		return alwaysShowOnRlp;
	}
	public void setAlwaysShowOnRlp(boolean alwaysShowOnRlp) {
		this.alwaysShowOnRlp = alwaysShowOnRlp;
	}
	public double getC1PercentageComplete() {
		return c1PercentageComplete;
	}
	public void setC1PercentageComplete(double c1PercentageComplete) {
		this.c1PercentageComplete = c1PercentageComplete;
	}
	public String getBabyImageURL() {
		return babyImageURL;
	}
	public void setBabyImageURL(String babyImageURL) {
		this.babyImageURL = babyImageURL;
	}
	public String getCaImageURL() {
		return caImageURL;
	}
	public void setCaImageURL(String caImageURL) {
		this.caImageURL = caImageURL;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<CategoryVO> getChildCategoryVO() {
		return childCategoryVO;
	}

	public void setChildCategoryVO(List<CategoryVO> childCategoryVO) {
		this.childCategoryVO = childCategoryVO;
	}
	
	
}
