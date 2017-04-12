package com.bbb.commerce.catalog.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class RegionVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String regionId;
	private String regionName;
	private Date cutOffTimeRegion;
	private String displayCutoffTime;
	private boolean sddFlag;
	private String promoAttId;
	private Set<String> zipCodes;
	private Set<String> storeIds;
	private double minShipFee;
	private String displayGetByTime;
	private Set<String> sites;
	private String timeZone;

	/**
	 * 
	 * @return
	 */
	public String getRegionId() {
		return regionId;
	}

	/**
	 * 
	 * @param regionId
	 */
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	/**
	 * 
	 * @return
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * 
	 * @param regionName
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * 
	 * @return
	 */
	public Date getCutOffTimeRegion() {
		return cutOffTimeRegion;
	}

	/**
	 * 
	 * @param cutOffTimeRegion
	 */
	public void setCutOffTimeRegion(Date cutOffTimeRegion) {
		this.cutOffTimeRegion = cutOffTimeRegion;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSddFlag() {
		return sddFlag;
	}

	/**
	 * 
	 * @param sddFlag
	 */
	public void setSddFlag(boolean sddFlag) {
		this.sddFlag = sddFlag;
	}

	/**
	 * 
	 * @return
	 */
	public String getPromoAttId() {
		return promoAttId;
	}

	/**
	 * 
	 * @param promoAttId
	 */
	public void setPromoAttId(String promoAttId) {
		this.promoAttId = promoAttId;
	}

	public Set<String> getZipCodes() {
		return zipCodes;
	}

	public void setZipCodes(Set<String> zipCodes) {
		this.zipCodes = zipCodes;
	}

	public Set<String> getStoreIds() {
		return storeIds;
	}

	public void setStoreIds(Set<String> storeIds) {
		this.storeIds = storeIds;
	}

	public double getMinShipFee() {
		return minShipFee;
	}

	public void setMinShipFee(double minShipFee) {
		this.minShipFee = minShipFee;
	}

	public String getDisplayCutoffTime() {
		return displayCutoffTime;
	}

	public void setDisplayCutoffTime(String displayCutoffTime) {
		this.displayCutoffTime = displayCutoffTime;
	}



	public Set<String> getSites() {
		return sites;
	}

	public void setSites(Set<String> sites) {
		this.sites = sites;
	}

	public String getDisplayGetByTime() {
		return displayGetByTime;
	}

	public void setDisplayGetByTime(String displayGetByTime) {
		this.displayGetByTime = displayGetByTime;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	

}