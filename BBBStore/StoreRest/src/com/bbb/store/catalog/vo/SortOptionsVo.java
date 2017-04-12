package com.bbb.store.catalog.vo;

import java.io.Serializable;

public class SortOptionsVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sortingCode;
	private String sortingValue;
	private int sortingOrder;
	private boolean isDefault;
	public String getSortingCode() {
		return sortingCode;
	}
	public void setSortingCode(String sortingCode) {
		this.sortingCode = sortingCode;
	}
	public String getSortingValue() {
		return sortingValue;
	}
	public void setSortingValue(String sortingValue) {
		this.sortingValue = sortingValue;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public int getSortingOrder() {
		return sortingOrder;
	}
	public void setSortingOrder(int sortingOrder) {
		this.sortingOrder = sortingOrder;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	

}
