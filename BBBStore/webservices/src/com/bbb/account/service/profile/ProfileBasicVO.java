package com.bbb.account.service.profile;

import java.io.Serializable;

public class ProfileBasicVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mFirstName;
	private String mLastName;
	private String mAddressLine1;
	private String mAddressLine2;
	private String mCity;
	private String mCountry;
	private String mState;
	private String mZipcode;
	private String mPhoneNum;
	private String mMobile;
	private String mEmail;
	private String mProfileId;
	private boolean mEmailOptIn;
	
	/**
	 * @return the mEmailOptIn
	 */
	public boolean isEmailOptIn() {
		return mEmailOptIn;
	}
	/**
	 * @param pEmailOptIn the pEmailOptIn to set
	 */
	public void setEmailOptIn(boolean pEmailOptIn) {
		this.mEmailOptIn = pEmailOptIn;
	}
	/**
	 * @return the mCountry
	 */
	public String getCountry() {
		return mCountry;
	}
	/**
	 * @param pCountry the mCountry to set
	 */
	public void setCountry(String pCountry) {
		this.mCountry = pCountry;
	}
	/**
	 * @return the firstName
	 */
	public String getProfileId() {
		return mProfileId;
	}
	/**
	 * @param pFirstName the firstName to set
	 */
	public void setProfileId(String pProfileId) {
		mProfileId = pProfileId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return mFirstName;
	}
	/**
	 * @param pFirstName the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		mFirstName = pFirstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return mLastName;
	}
	/**
	 * @param pLastName the lastName to set
	 */
	public void setLastName(String pLastName) {
		mLastName = pLastName;
	}
	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return mAddressLine1;
	}
	/**
	 * @param pAddressLine1 the addressLine1 to set
	 */
	public void setAddressLine1(String pAddressLine1) {
		mAddressLine1 = pAddressLine1;
	}
	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return mAddressLine2;
	}
	/**
	 * @param pAddressLine2 the addressLine2 to set
	 */
	public void setAddressLine2(String pAddressLine2) {
		mAddressLine2 = pAddressLine2;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return mCity;
	}
	/**
	 * @param pCity the city to set
	 */
	public void setCity(String pCity) {
		mCity = pCity;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return mState;
	}
	/**
	 * @param pState the state to set
	 */
	public void setState(String pState) {
		mState = pState;
	}
	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return mZipcode;
	}
	/**
	 * @param pZipcode the zipcode to set
	 */
	public void setZipcode(String pZipcode) {
		mZipcode = pZipcode;
	}
	/**
	 * @return the phoneNum
	 */
	public String getPhoneNum() {
		return mPhoneNum;
	}
	/**
	 * @param pPhoneNum the phoneNum to set
	 */
	public void setPhoneNum(String pPhoneNum) {
		mPhoneNum = pPhoneNum;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mMobile;
	}
	/**
	 * @param pMobile the mobile to set
	 */
	public void setMobile(String pMobile) {
		mMobile = pMobile;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return mEmail;
	}
	/**
	 * @param pEmail the email to set
	 */
	public void setEmail(String pEmail) {
		mEmail = pEmail;
	}
	
}
