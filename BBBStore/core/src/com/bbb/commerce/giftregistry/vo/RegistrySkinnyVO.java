/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * This class provides the registry future lists.
 *
 * @author sku134
 */
public class RegistrySkinnyVO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The registry id. */
	private String registryId;
	
	/** The event date. */
	private String eventDate;
	
	/** The event type. */
	private String eventType;
	
	/** The event type. */
	private String eventCode;
	
	/** The Registry Status. */
	private String status;
    
	/** The Registry Status. */
	private String thresholdMsg;
	
	/** The Registry alternatePhone. */
	private String alternatePhone;
	/** The shipping address. */
	private boolean poBoxAddress;;
	
	/** The primary registrant first name. */
	private String primaryRegistrantFirstName;
	
	/** The co registrant first name. */
	private String coRegistrantFirstName;
	
	/**
	 * Gets the alternatePhone .
	 *
	 * @return the alternatePhone
	 */
	public String getAlternatePhone() {
		return alternatePhone;
	}

	/**
	 * Sets the alternatePhone.
	 *
	 * @param pRegistryId the alternatePhone to set
	 */
	public void setAlternatePhone(String alternatePhone) {
		this.alternatePhone = alternatePhone;
	}

	/**
	 * Gets the registry id.
	 *
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}
	
	/**
	 * Sets the registry id.
	 *
	 * @param pRegistryId the registryId to set
	 */
	public void setRegistryId(String pRegistryId) {
		registryId = pRegistryId;
	}
	
	/**
	 * Gets the event date.
	 *
	 * @return the eventDate
	 */
	public String getEventDate() {
		return eventDate;
	}
	
	/**
	 * Sets the event date.
	 *
	 * @param pEventDate the eventDate to set
	 */
	public void setEventDate(String pEventDate) {
		eventDate = pEventDate;
	}
	
	/**
	 * Gets the event type.
	 *
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}
	
	/**
	 * Sets the event type.
	 *
	 * @param pEventType the eventType to set
	 */
	public void setEventType(String pEventType) {
		eventType = pEventType;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "RegistrySkinnyVO [registryId=" + registryId + ", eventDate="
				+ eventDate + ", eventType=" + eventType + ", eventCode="
				+ eventCode + ", status=" + status + ", thresholdMsg="
				+ thresholdMsg + ", alternatePhone=" + alternatePhone
				+ ", poBoxAddress=" + poBoxAddress
				+ ", primaryRegistrantFirstName=" + primaryRegistrantFirstName
				+ ", coRegistrantFirstName=" + coRegistrantFirstName + "]";
	}

	

	public boolean isPoBoxAddress() {
		return poBoxAddress;
	}

	public void setPoBoxAddress(boolean poBoxAddress) {
		this.poBoxAddress = poBoxAddress;
	}

	public String getThresholdMsg() {
		return thresholdMsg;
	}

	public void setThresholdMsg(String thresholdMsg) {
		this.thresholdMsg = thresholdMsg;
	}
	
	public String getPrimaryRegistrantFirstName() {
		return primaryRegistrantFirstName;
	}

	public void setPrimaryRegistrantFirstName(String primaryRegistrantFirstName) {
		this.primaryRegistrantFirstName = primaryRegistrantFirstName;
	}

	public String getCoRegistrantFirstName() {
		return coRegistrantFirstName;
	}

	public void setCoRegistrantFirstName(String coRegistrantFirstName) {
		this.coRegistrantFirstName = coRegistrantFirstName;
	}
	

}
