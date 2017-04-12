package com.bbb.account.webservices.vo;

public class BillingAddressRequestVO extends GenericProfileRequestVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String profileId;
	private String securityToken;
	private String siteId;
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}
	/**
	 * @param profileId the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	/**
	 * @return the securityToken
	 */
	public String getSecurityToken() {
		return securityToken;
	}
	/**
	 * @param securityToken the securityToken to set
	 */
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}
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

	@Override
	public String toString() {
		return "AddressRequestVO [profileId=" + profileId + ", email=" + email + ", securityToken="
				+ securityToken + ", siteId=" + siteId + "]";
	}
	
}
