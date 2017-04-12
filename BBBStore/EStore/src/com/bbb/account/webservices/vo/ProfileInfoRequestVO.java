package com.bbb.account.webservices.vo;


public class ProfileInfoRequestVO extends GenericProfileRequestVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email; 	
	private String profileId;
	private String password;
	private String siteId;
	private boolean autoExtendProfile;	
	
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
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	/**
	 * @return the autoExtendProfile
	 */
	public boolean isAutoExtendProfile() {
		return autoExtendProfile;
	}
	/**
	 * @param autoExtendProfile the autoExtendProfile to set
	 */
	public void setAutoExtendProfile(boolean autoExtendProfile) {
		this.autoExtendProfile = autoExtendProfile;
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
	
}
