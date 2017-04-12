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
public class CheckListProgressVO  implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private String categoryIDForC1;
	private String categoryIDForC2;
	private String categoryIDForC3;
	private double categoryProgress;
	private double overAllProgress;
	private String labelMessage;
	private int updatedAddedQuantity;
	private String c1ImageUrl;
	private String error;
	/**
	 * @return the categoryIDForC1
	 */
	public String getCategoryIDForC1() {
		return categoryIDForC1;
	}
	/**
	 * @param categoryIDForC1 the categoryIDForC1 to set
	 */
	public void setCategoryIDForC1(String categoryIDForC1) {
		this.categoryIDForC1 = categoryIDForC1;
	}
	/**
	 * @return the categoryIDForC2
	 */
	public String getCategoryIDForC2() {
		return categoryIDForC2;
	}
	/**
	 * @param categoryIDForC2 the categoryIDForC2 to set
	 */
	public void setCategoryIDForC2(String categoryIDForC2) {
		this.categoryIDForC2 = categoryIDForC2;
	}
	/**
	 * @return the categoryIDForC3
	 */
	public String getCategoryIDForC3() {
		return categoryIDForC3;
	}
	/**
	 * @param categoryIDForC3 the categoryIDForC3 to set
	 */
	public void setCategoryIDForC3(String categoryIDForC3) {
		this.categoryIDForC3 = categoryIDForC3;
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
	
	public int getUpdatedAddedQuantity() {
		return updatedAddedQuantity;
	}
	public void setUpdatedAddedQuantity(int updatedAddedQuantity) {
		this.updatedAddedQuantity = updatedAddedQuantity;
	}
	public String getC1ImageUrl() {
		return c1ImageUrl;
	}
	public void setC1ImageUrl(String c1ImageUrl) {
		this.c1ImageUrl = c1ImageUrl;
	}
	public double getCategoryProgress() {
		return categoryProgress;
	}
	public void setCategoryProgress(double categoryProgress) {
		this.categoryProgress = categoryProgress;
	}
	public double getOverAllProgress() {
		return overAllProgress;
	}
	public void setOverAllProgress(double overAllProgress) {
		this.overAllProgress = overAllProgress;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
