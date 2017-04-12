package com.bbb.account.vo;

import com.bbb.framework.integration.ServiceRequestBase;

public class AssignOffersRequestVo extends ServiceRequestBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userToken;
	private String siteFlag;
	private String onlineOfferCd;
	private String pOSCouponId;
	private String serviceName;
	private String walletId;
	private String uniqueCouponCd;
	
	
	
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getSiteFlag() {
		return siteFlag;
	}
	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}
	public String getOnlineOfferCd() {
		return onlineOfferCd;
	}
	public void setOnlineOfferCd(String onlineOfferCd) {
		this.onlineOfferCd = onlineOfferCd;
	}
	public String getpOSCouponId() {
		return pOSCouponId;
	}
	public void setpOSCouponId(String pOSCouponId) {
		this.pOSCouponId = pOSCouponId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getWalletId() {
		return walletId;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	public String getUniqueCouponCd() {
		return uniqueCouponCd;
	}
	public void setUniqueCouponCd(String uniqueCouponCd) {
		this.uniqueCouponCd = uniqueCouponCd;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}