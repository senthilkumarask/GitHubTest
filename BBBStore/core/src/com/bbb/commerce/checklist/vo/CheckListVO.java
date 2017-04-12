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
 * @author ssi191
 */
public class CheckListVO  implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String checklistId;
	private String checklistTypeName;
	private String displayName;
	private double averageC1Percentage;
	private List<CategoryVO> categoryListVO;
	private boolean checkListDisabled;
	private String registryId;
	private String siteFlag;
	/**
	 * @return the checklistId
	 */
	public String getChecklistId() {
		return checklistId;
	}
	
	/**
	 * @param checklistId the checklistId to set
	 */
	public void setChecklistId(String checklistId) {
		this.checklistId = checklistId;
	}
	/**
	 * @return the checklistTypeName
	 */
	public String getChecklistTypeName() {
		return checklistTypeName;
	}
	/**
	 * @param checklistTypeName the checklistTypeName to set
	 */
	public void setChecklistTypeName(String checklistTypeName) {
		this.checklistTypeName = checklistTypeName;
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
	 * @return the categoryListVO
	 */
	public List<CategoryVO> getCategoryListVO() {
		return categoryListVO;
	}
	/**
	 * @param categoryListVO the categoryListVO to set
	 */
	public void setCategoryListVO(List<CategoryVO> categoryListVO) {
		this.categoryListVO = categoryListVO;
	}
	
	/**
	 * @return the isCheckListDisabled
	 */
	public boolean isCheckListDisabled() {
		return checkListDisabled;
	}
	/**
	 * @param checkListDisabled to set checkListDisabled
	 */
	public void setCheckListDisabled(boolean checkListDisabled) {
		this.checkListDisabled = checkListDisabled;
	}
	
	public String getRegistryId() {
		return registryId;
	}
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	public double getAverageC1Percentage() {
		return averageC1Percentage;
	}

	public void setAverageC1Percentage(double averageC1Percentage) {
		this.averageC1Percentage = averageC1Percentage;
	}

	public String getSiteFlag() {
		return siteFlag;
	}

	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}	
	
}
