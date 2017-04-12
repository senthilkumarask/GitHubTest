/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 26-December-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.account.vo;

import java.io.Serializable;

//import java.util.Date;

public class RedemptionCodeVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String redemptionCodeChannel;
	private String uniqueCouponCd;
	private String couponImageURL;
	private String onlineOfferCode;
	private String pOSCouponID;
	private String printableFlag;
	private String displayFlag;
	private String enforcedExpirationFlag;
	
	
	public String getRedemptionCodeChannel() {
		return redemptionCodeChannel;
	}
	public void setRedemptionCodeChannel(String redemptionCodeChannel) {
		this.redemptionCodeChannel = redemptionCodeChannel;
	}
	public String getUniqueCouponCd() {
		return uniqueCouponCd;
	}
	public void setUniqueCouponCd(String uniqueCouponCd) {
		this.uniqueCouponCd = uniqueCouponCd;
	}
	public String getCouponImageURL() {
		return couponImageURL;
	}
	public void setCouponImageURL(String couponImageURL) {
		this.couponImageURL = couponImageURL;
	}
	public String getOnlineOfferCode() {
		return onlineOfferCode;
	}
	public void setOnlineOfferCode(String onlineOfferCode) {
		this.onlineOfferCode = onlineOfferCode;
	}
	public String getpOSCouponID() {
		return pOSCouponID;
	}
	public void setpOSCouponID(String pOSCouponID) {
		this.pOSCouponID = pOSCouponID;
	}
	public String getPrintableFlag() {
		return printableFlag;
	}
	public void setPrintableFlag(String printableFlag) {
		this.printableFlag = printableFlag;
	}
	public String getDisplayFlag() {
		return displayFlag;
	}
	public void setDisplayFlag(String displayFlag) {
		this.displayFlag = displayFlag;
	}
	public String getEnforcedExpirationFlag() {
		return enforcedExpirationFlag;
	}
	public void setEnforcedExpirationFlag(String enforcedExpirationFlag) {
		this.enforcedExpirationFlag = enforcedExpirationFlag;
	}

	
	@Override
	public String toString() {
		return "RedemptionCodeVO [redemptionCodeChannel="
				+ redemptionCodeChannel + ", uniqueCouponCd=" + uniqueCouponCd
				+ ", couponImageURL=" + couponImageURL + ", onlineOfferCode="
				+ onlineOfferCode + ", pOSCouponID=" + pOSCouponID
				+ ", printableFlag=" + printableFlag + ", displayFlag="
				+ displayFlag + ", enforcedExpirationFlag="
				+ enforcedExpirationFlag + "]";
	}
	
	
	

	
	
}