package com.bbb.commerce.vo;

import java.io.Serializable;

public class PriceOverridenVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String mCommerceId;
	private String mProductName;
	private double mOverrideAmount;
	private double mOverridePercent;
	/**
	 * @return the commerceId
	 */
	public String getCommerceId() {
		return mCommerceId;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return mProductName;
	}
	/**
	 * @return the overrideAmount
	 */
	public double getOverrideAmount() {
		return mOverrideAmount;
	}
	/**
	 * @return the overridePercent
	 */
	public double getOverridePercent() {
		return mOverridePercent;
	}
	/**
	 * @param pCommerceId the commerceId to set
	 */
	public void setCommerceId(String pCommerceId) {
		mCommerceId = pCommerceId;
	}
	/**
	 * @param pProductName the productName to set
	 */
	public void setProductName(String pProductName) {
		mProductName = pProductName;
	}
	/**
	 * @param pOverrideAmount the overrideAmount to set
	 */
	public void setOverrideAmount(double pOverrideAmount) {
		mOverrideAmount = pOverrideAmount;
	}
	/**
	 * @param pOverridePercent the overridePercent to set
	 */
	public void setOverridePercent(double pOverridePercent) {
		mOverridePercent = pOverridePercent;
	}

	
}
