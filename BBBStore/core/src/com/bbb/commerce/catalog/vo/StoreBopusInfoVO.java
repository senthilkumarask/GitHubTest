package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class StoreBopusInfoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String siteId;
	private Boolean bopusFlag;
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the bopusFlag
	 */
	public Boolean getBopusFlag() {
		return bopusFlag;
	}
	/**
	 * @param bopusFlag the bopusFlag to set
	 */
	public void setBopusFlag(Boolean bopusFlag) {
		this.bopusFlag = bopusFlag;
	}
	
	
}
