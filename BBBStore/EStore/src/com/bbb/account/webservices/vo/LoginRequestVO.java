package com.bbb.account.webservices.vo;

public class LoginRequestVO extends GenericProfileRequestVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String loginId;
	private String password;
	private boolean loginProfileId;
	private boolean autoExtendProfile;
	private String siteId;
	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(final String loginId) {
		this.loginId = loginId;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the loginProfileId
	 */
	public boolean isLoginProfileId() {
		return loginProfileId;
	}
	/**
	 * @param loginProfileId the loginProfileId to set
	 */
	public void setLoginProfileId(final boolean loginProfileId) {
		this.loginProfileId = loginProfileId;
	}
	/**
	 * @return the autoExtendProfile
	 */
	public boolean isAutoExtendProfile() {
		return autoExtendProfile;
	}
	/**
	 * @param autoExtendProfile the autoExtendProfile to set
	 */
	public void setAutoExtendProfile(final boolean autoExtendProfile) {
		this.autoExtendProfile = autoExtendProfile;
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
	public void setSiteId(final String siteId) {
		this.siteId = siteId;
	}

	@Override
	public String toString() {
		return "LoginRequestVO [loginId=" + loginId + ", loginProfileId=" + loginProfileId + ", autoExtendProfile="
				+ autoExtendProfile + ", siteId=" + siteId + "]";
	}
}
