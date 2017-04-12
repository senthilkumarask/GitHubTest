/*
 * --------------------------------------------------------------------------------
 * 
 * Created by: Sneha Ghosh
 * 
 * Created on: 5-March-2016
 * --------------------------------------------------------------------------------
 */

package com.bbb.commerce.browse.vo;

import java.io.Serializable;

/**
 * Bean to hold the properties related to same day delivery feature
 * 
 * @author sghosh
 *
 */
public class SddZipcodeVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
	
	private String regionId;
	private String zipcode;
	private String promoAttId;
	private String cutOffTime;
	private String displayCutoffTime;
	private double minShipFee;
	private boolean sddEligibility;
	private String displayGetByTime;
	private String storeId;

	

	/* ===================================================== *
		GETTERS and SETTERS
	 * ===================================================== */
	
	
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPromoAttId() {
		return promoAttId;
	}
	public void setPromoAttId(String promoAttId) {
		this.promoAttId = promoAttId;
	}
	public String getCutOffTime() {
		return cutOffTime;
	}
	public void setCutOffTime(String cutOffTime) {
		this.cutOffTime = cutOffTime;
	}
	
	public String getDisplayCutoffTime() {
		return displayCutoffTime;
	}
	
	public void setDisplayCutoffTime(String displayCutoffTime) {
		this.displayCutoffTime = displayCutoffTime;
	}
	
	public double getMinShipFee() {
		return minShipFee;
	}
	public void setMinShipFee(double minShipFee) {
		this.minShipFee = minShipFee;
	}
	public boolean isSddEligibility() {
		return sddEligibility;
	}
	public void setSddEligibility(boolean sddEligibility) {
		this.sddEligibility = sddEligibility;
	}

	public String getDisplayGetByTime() {

		return displayGetByTime;

	}

	public void setDisplayGetByTime(String displayGetByTime) {

		this.displayGetByTime = displayGetByTime;

	}
	
	public String getStoreId() {

		return storeId;

	}

	public void setStoreId(String storeId) {

		this.storeId = storeId;

	}
	
	public String toString(){
		StringBuffer toString = new StringBuffer();
		toString.append("Zipcode " + this.zipcode);
		toString.append("Region ID " + this.regionId);
		toString.append("Promo Attribute Id " + this.promoAttId);
		toString.append("Cut Off Time " + this.cutOffTime);
		toString.append("Display Cut Off Time " + this.displayCutoffTime);
		toString.append("Min Ship Fee " + this.minShipFee);
		toString.append("Sdd Eligibility" + this.sddEligibility);
		return toString.toString();
	}
	
}