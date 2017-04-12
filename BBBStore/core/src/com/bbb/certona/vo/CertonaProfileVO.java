package com.bbb.certona.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CertonaProfileVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userId;
	private String userFirstName;
	private String userLastName;
	private String userType;
	private String locale;
	private Date lastActivity;
	private Date registrationDate;
	private String emailAddress;
	private String mobileNumber;
	private String phoneNumber;
	private String gender;
	private Date dateOfBirth;
	private List<CertonaGiftRegistryVO> giftRegistryVOList=new ArrayList<CertonaGiftRegistryVO>();
	private List<CertonaProfileSiteVO> profileSiteVOList=new ArrayList<CertonaProfileSiteVO>();
	private boolean isFacebookIntegrated;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the userFirstName
	 */
	public String getUserFirstName() {
		return userFirstName;
	}

	/**
	 * @param userFirstName the userFirstName to set
	 */
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	/**
	 * @return the userLastName
	 */
	public String getUserLastName() {
		return userLastName;
	}

	/**
	 * @param userLastName the userLastName to set
	 */
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the lastActivity
	 */
	public Date getLastActivity() {
		return lastActivity;
	}

	/**
	 * @param lastActivity the lastActivity to set
	 */
	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the isFacebookIntegrated
	 */
	public boolean isFacebookIntegrated() {
		return isFacebookIntegrated;
	}

	/**
	 * @param isFacebookIntegrated the isFacebookIntegrated to set
	 */
	public void setFacebookIntegrated(boolean isFacebookIntegrated) {
		this.isFacebookIntegrated = isFacebookIntegrated;
	}

	/**
	 * @return the giftRegistryVOList
	 */
	public List<CertonaGiftRegistryVO> getGiftRegistryVOList() {
		return giftRegistryVOList;
	}

	/**
	 * @param giftRegistryVOList the giftRegistryVOList to set
	 */
	public void setGiftRegistryVOList(List<CertonaGiftRegistryVO> giftRegistryVOList) {
		this.giftRegistryVOList = giftRegistryVOList;
	}

	/**
	 * @return the profileSiteVOList
	 */
	public List<CertonaProfileSiteVO> getProfileSiteVOList() {
		return profileSiteVOList;
	}

	/**
	 * @param profileSiteVOList the profileSiteVOList to set
	 */
	public void setProfileSiteVOList(List<CertonaProfileSiteVO> profileSiteVOList) {
		this.profileSiteVOList = profileSiteVOList;
	}
	

}
