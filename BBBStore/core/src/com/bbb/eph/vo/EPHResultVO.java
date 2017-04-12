package com.bbb.eph.vo;

import java.util.List;

public class EPHResultVO {
	
	private String ephList;
	private String categoryList;
	private boolean ephOrL1L2ListAvailable;
	private List<String> colorList;
	private boolean isColorMatchApplied;
	/**
	 * @return the ephList
	 */
	public String getEphList() {
		return ephList;
	}
	/**
	 * @param ephList the ephList to set
	 */
	public void setEphList(String ephList) {
		this.ephList = ephList;
	}
	/**
	 * @return the categoryList
	 */
	public String getCategoryList() {
		return categoryList;
	}
	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(String categoryList) {
		this.categoryList = categoryList;
	}
	/**
	 * @return the ephOrL1L2ListAvailable
	 */
	public boolean isEphOrL1L2ListAvailable() {
		return ephOrL1L2ListAvailable;
	}
	/**
	 * @param ephOrL1L2ListAvailable the ephOrL1L2ListAvailable to set
	 */
	public void setEphOrL1L2ListAvailable(boolean ephOrL1L2ListAvailable) {
		this.ephOrL1L2ListAvailable = ephOrL1L2ListAvailable;
	}
	/**
	 * @return the colorList
	 */
	public List<String> getColorList() {
		return colorList;
	}
	/**
	 * @param colorList the colorList to set
	 */
	public void setColorList(List<String> colorList) {
		this.colorList = colorList;
	}
	/**
	 * @return the isColorMatchApplied
	 */
	public boolean isColorMatchApplied() {
		return isColorMatchApplied;
	}
	/**
	 * @param isColorMatchApplied the isColorMatchApplied to set
	 */
	public void setColorMatchApplied(boolean isColorMatchApplied) {
		this.isColorMatchApplied = isColorMatchApplied;
	}
	
	@Override
	public String toString() {
		return "EPHResultVO [isEphOrNodeIdFoundForKeyword="
				+ ephOrL1L2ListAvailable + ",ephList=" + ephList + ", categoryList="
				+ categoryList + ",colorList=" + colorList
				+ ", isColorMatchApplied=" + isColorMatchApplied + "]";
	}
	
	
	
}
