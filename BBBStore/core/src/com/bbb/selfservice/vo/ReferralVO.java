package com.bbb.selfservice.vo;

import java.io.Serializable;


public class ReferralVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String referralUrl;
	private String referralId;
	private String currentUrl;
	private String referrerDomain;
	
	public ReferralVO()
	{
		super();
	}

	/**
	 * @return the mAllowedUrl
	 */
	public String getReferralUrl() {
		return referralUrl;
	}

	/**
	 * @param mAllowedUrl the mAllowedUrl to set
	 */
	public void setReferralUrl(String pReferralUrl) {
		this.referralUrl = pReferralUrl;
	}

	/**
	 * @return the mReferralId
	 */
	public String getReferralId() {
		return referralId;
	}

	/**
	 * @param mReferralId the mReferralId to set
	 */
	public void setReferralId(String pReferralId) {
		this.referralId = pReferralId;
	}

	/**
	 * @return the currentUrl
	 */
	public String getCurrentUrl() {
		return currentUrl;
	}

	/**
	 * @param currentUrl the currentUrl to set
	 */
	public void setCurrentUrl(String currentUrl) {
		this.currentUrl = currentUrl;
	}

	public String getReferrerDomain() {
		return referrerDomain;
	}

	public void setReferrerDomain(String referrerDomain) {
		this.referrerDomain = referrerDomain;
	}

	
}
