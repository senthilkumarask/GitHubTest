package com.bbb.account.webservices.vo;

import java.io.Serializable;
import java.util.Map;

import atg.core.util.Address;



public class ProfileInfoResponseVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String profileId;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String mobile;
	private Address defaultShippingAddress;
	private Address defaultBillingAddress;
	private boolean error;
	private Map<String, String> errorMap;
	private boolean autoExtend;
	
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
	@Override
	public String toString() {
		return "ProfileInfoResponseVO [profileId=" + profileId
				+ ", isError()=" + isError()
				+ ", firstName=" + getFirstName()
				+ ", lastName=" + getLastName()
				+ ", Email=" + getEmail()
				+ ", Phone=" + getPhone()				
				+ ", Mobile=" + getMobile()				
				+ ", Default Billing Address=" + getDefaultBillingAddress()
				+ ", Default Shipping Address=" + getDefaultShippingAddress()
				+ "]";
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/**
	 * @return the defaultShippingAddress
	 */
	public Address getDefaultShippingAddress() {
		return defaultShippingAddress;
	}
	/**
	 * @param defaultShippingAddress the defaultShippingAddress to set
	 */
	public void setDefaultShippingAddress(Address defaultShippingAddress) {
		this.defaultShippingAddress = defaultShippingAddress;
	}
	/**
	 * @return the defaultBillingAddress
	 */
	public Address getDefaultBillingAddress() {
		return defaultBillingAddress;
	}
	/**
	 * @param defaultBillingAddress the defaultBillingAddress to set
	 */
	public void setDefaultBillingAddress(Address defaultBillingAddress) {
		this.defaultBillingAddress = defaultBillingAddress;
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
	public void setError(final boolean error) {
		this.error = error;
	}
	
	/**
	 * @return the autoExtend
	 */
	public boolean isAutoExtend() {
		return autoExtend;
	}
	/**
	 * @param autoExtend the autoExtend to set
	 */
	public void setAutoExtend(boolean autoExtend) {
		this.autoExtend = autoExtend;
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
	
}
