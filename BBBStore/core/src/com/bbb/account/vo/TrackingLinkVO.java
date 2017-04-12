/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TrackingLinkVO.java
 *
 *  DESCRIPTION: TrackingLinkVO : VO for tracking links of hardgood shipped order  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */


package com.bbb.account.vo;

import java.io.Serializable;
import java.util.Map;

public class TrackingLinkVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mCarrierName;
	private String mTrackingNum;
	private Map<String,String>  mTrackingURL;
//	private String mTrackingLink;
	/**
	 * @return the mCarrierName
	 */
	public String getCarrierName() {
		return mCarrierName;
	}
	/**
	 * @param mCarrierName the mCarrierName to set
	 */
	public void setCarrierName(String pCarrierName) {
		this.mCarrierName = pCarrierName;
	}
	/**
	 * @return the mTrackingNum
	 */
	public String getTrackingNum() {
		return mTrackingNum;
	}
	/**
	 * @param pTrackingNum the mTrackingNum to set
	 */
	public void setTrackingNum(String pTrackingNum) {
		this.mTrackingNum = pTrackingNum;
	}
	/**
	 * @return the mTrackingURL
	 */
	public Map<String, String> getTrackingURL() {
		return mTrackingURL;
	}
	/**
	 * @param mTrackingURL the mTrackingURL to set
	 */
	public void setTrackingURL(Map<String, String> pTrackingURL) {
		this.mTrackingURL = pTrackingURL;
	}
	
	
	
//	/**
//	 * @return the mTrackingLink
//	 */
//	public String getTrackingLink() {
//		return mTrackingLink;
//	}
//	/**
//	 * @param mTrackingLink the mTrackingLink to set
//	 */
//	public void setTrackingLink(String pTrackingLink) {
//		this.mTrackingLink = pTrackingLink;
//	}
}
