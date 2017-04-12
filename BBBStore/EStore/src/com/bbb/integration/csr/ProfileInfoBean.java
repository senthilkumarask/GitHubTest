package com.bbb.integration.csr;

import java.util.Map;

public class ProfileInfoBean{

	private String profileId;
	private String memberId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String registrationDate;
	private String accountLocked;
	private Map userSiteAssociations;

	public Map getUserSiteAssociations() {
		return userSiteAssociations;
	}

	public void setUserSiteAssociations(Map siteAssociations) {
		this.userSiteAssociations = siteAssociations;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(String accountLocked) {
		this.accountLocked = accountLocked;
	}

}
