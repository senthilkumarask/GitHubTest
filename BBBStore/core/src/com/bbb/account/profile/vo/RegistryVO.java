/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  RegistryVO.java
 *
 *  DESCRIPTION: Migrated profile registry details VO. 
 *   	
 *  HISTORY:
 *  26/03/12 Initial version
 *  	
 */
package com.bbb.account.profile.vo;

import java.io.Serializable;



/**
 * @author hbandl
 *
 */
public class RegistryVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id = "";
	
	/**
	 * user registry id
	 */
	private String registryId = "";
	
	/**
	 * user registrant status
	 */
	private boolean registrant;
	
	/**
	 * user registrant status as String
	 */
	private String registrantAsString = "";
	
	/**
	 * type of registry
	 */
	private String registryType = "";
	
	/**
	 * event date
	 */
	private String eventDate = "";
	
	/**
	 * profile site id
	 */
	private String siteId = "";
	
	/**
	 * user email address
	 */
	private String email = "";
	
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
	 * user registryTypeCode
	 */
	private String registryTypeCode = "";
	
	
	/**
	 * @return
	 */
	public String getRegistryTypeCode() {
		return registryTypeCode;
	}

	/**
	 * @param registryTypeCode
	 */
	public void setRegistryTypeCode(String registryTypeCode) {
		this.registryTypeCode = registryTypeCode;
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
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}

	/**
	 * @param registryId the registryId to set
	 */
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	/**
	 * @return the registrant
	 */
	public boolean isRegistrant() {
		return registrant;
	}

	/**
	 * @param registrant the registrant to set
	 */
	public void setRegistrant(boolean registrant) {
		this.registrant = registrant;
	}

	/**
	 * @return the registryType
	 */
	public String getRegistryType() {
		return registryType;
	}

	/**
	 * @param registryType the registryType to set
	 */
	public void setRegistryType(String registryType) {
		this.registryType = registryType;
	}

	/**
	 * @return the eventDate
	 */
	public String getEventDate() {
		return eventDate;
	}

	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	/**
	 * @return the registrantAsString
	 */
	public String getRegistrantAsString() {
		return registrantAsString;
	}

	/**
	 * @param registrantAsString the registrantAsString to set
	 */
	public void setRegistrantAsString(String registrantAsString) {
		this.registrantAsString = registrantAsString;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		
		StringBuffer registryDetails = new StringBuffer("");
		
		registryDetails.append(this.id + "|");
		registryDetails.append(this.registryId + "|");
		registryDetails.append(this.email + "|");
		registryDetails.append(this.siteId + "|");
		registryDetails.append(this.registryType + "|");
		registryDetails.append(this.eventDate + "|");
		registryDetails.append(this.registrantAsString + "|");
		registryDetails.append(this.profileId + "|");
		registryDetails.append(this.error + "|");
		registryDetails.append(this.errorCode + "|");
		registryDetails.append(this.responseMessage);
		
		return registryDetails.toString();
		
	}
	
}
