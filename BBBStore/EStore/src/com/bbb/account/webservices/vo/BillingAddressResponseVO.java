package com.bbb.account.webservices.vo;

import java.io.Serializable;
import java.util.Map;

import atg.core.util.Address;

public class BillingAddressResponseVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String profileId;
	private String email;
	private Address defaultBillingAddress;
	private Map<String, String> errorMap;
	private boolean error;
	
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
	 * @return the defaultShipAddress
	 */
	public Address getDefaultBillingAddress() {
		return defaultBillingAddress;
	}
	/**
	 * @param defaultShipAddress the defaultShipAddress to set
	 */
	public void setDefaultBillingAddress(Address defaultBillingAddress) {
		this.defaultBillingAddress = defaultBillingAddress;
	}
	@Override
	public String toString() {
		return "BillingAddressResponseVO [profileId=" + profileId
				+ ", defaultShipAddress=" + getDefaultBillingAddress() +
				", isError()=" + isError() + ", getErrorCodes()="
				+ "]";
	}
	/**
	 * @return the errorMap
	 */
	public Map<String, String> getErrorMap() {
		return errorMap;
	}
	/**
	 * @param errorMap the errorMap to set
	 */
	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}

}
