/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProfileVO.java
 *
 *  DESCRIPTION: Migrated profile details VO. 
 *   	
 *  HISTORY:
 *  26/03/12 Initial version
 *  	
 */
package com.bbb.account.profile.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hbandl
 *
 */
public class ProfileVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id = "";
	
	/**
	 * user first name 
	 */
	private String firstName = "";

	/**
	 * user last name
	 */
	private String lastName = "";
	
	/**
	 * user email address
	 */
	private String email = "";
	
	/**
	 * user mobile number
	 */
	private String mobileNumber = "";
	
	/**
	 * user phone number
	 */
	private String phoneNumber = "";
	
	/**
	 * user profile last accessed date
	 */
	private Date lastModifiedDate;
	
	/**
	 * user profile last accessed date as String
	 */
	private String lastModifiedDateAsString = "";
	
	/**
	 * user opt In flag
	 */
	private String optInFlag = "";
	
	/**
	 * profile site id
	 */
	private String siteId = "";
	
	/**
	 * error flag
	 */
	private boolean error = false;
	
	/**
	 * error code
	 */
	private String errorCode = "";
	
	/**
	 * response message
	 */
	private String responseMessage = "";
	
	/**
	 * user BBB profile Id
	 */
	private String profileId = "";
	
	/**
	 * member Id of user
	 */
	private String memberId = "";
	
	/**
	 * Favorite store Id
	 */
	private String favStoreId = "";
	
	/**
	 * @return the favStoreId
	 */
	public String getFavStoreId() {
		return favStoreId;
	}

	/**
	 * @param favStoreId the favStoreId to set
	 */
	public void setFavStoreId(String favStoreId) {
		this.favStoreId = favStoreId;
	}
	
//	//QAS updates 
	
	private boolean poBoxAddress;
	public boolean isPoBoxAddress() {
		return poBoxAddress;
	}

	public void setPOBoxAddress(boolean poBoxAddress) {
		this.poBoxAddress = poBoxAddress;
	}

	private boolean qasValidated;
	
	public boolean qasValidated() {
		return qasValidated;
	}

	public void setqasValidated(boolean qasValidated) {
		this.qasValidated = qasValidated;
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
	 * @return the memberId
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * @return the optInFlag
	 */
	public String getOptInFlag() {
		return optInFlag;
	}

	/**
	 * @param optInFlag the optInFlag to set
	 */
	public void setOptInFlag(String optInFlag) {
		this.optInFlag = optInFlag;
	}
	
	/**
	 * @return the lastModifiedDateAsString
	 */
	public String getLastModifiedDateAsString() {
		return lastModifiedDateAsString;
	}

	/**
	 * @param lastModifiedDateAsString the lastModifiedDateAsString to set
	 */
	public void setLastModifiedDateAsString(String lastModifiedDateAsString) {
		this.lastModifiedDateAsString = lastModifiedDateAsString;
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

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the responseMessage
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		
		StringBuffer profileDetails = new StringBuffer("");
		profileDetails.append(this.id + "|");
		profileDetails.append(this.memberId + "|");
		profileDetails.append(this.email + "|");
		profileDetails.append(this.siteId + "|");
		profileDetails.append(this.firstName + "|");
		profileDetails.append(this.lastName + "|");
		profileDetails.append(this.phoneNumber + "|");
		profileDetails.append(this.mobileNumber + "|");
		profileDetails.append(this.optInFlag + "|");
		profileDetails.append(this.lastModifiedDateAsString + "|");
		profileDetails.append(this.favStoreId + "|");
		profileDetails.append(this.profileId + "|");
		profileDetails.append(this.error + "|");
		profileDetails.append(this.errorCode + "|");
		profileDetails.append(this.responseMessage);
		return profileDetails.toString();
		
	}
	
}