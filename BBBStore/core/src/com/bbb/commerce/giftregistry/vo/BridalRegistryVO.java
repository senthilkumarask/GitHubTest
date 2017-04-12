/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;


/**
 * This class provides the commitment and wedding registry lists.
 *
 * @author ssha53
 */
public class BridalRegistryVO implements Serializable{
	
	@Override
	public String toString() {
		return "BridalRegistryVO ["
				+ (registryId != null ? "registryId=" + registryId + ", " : "")
				+ (eventDate != null ? "eventDate=" + eventDate + ", " : "")
				+ (eventType != null ? "eventType=" + eventType + ", " : "")
				+ (eventCode != null ? "eventCode=" + eventCode + ", " : "")
				+ (bridalToolkitToken != null ? "bridalToolkitToken="
						+ bridalToolkitToken : "") + "]";
	}

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
	
	/** The bridal toolkit token. */
	private String bridalToolkitToken;

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

	/**
	 * @return the bridalToolkitToken
	 */
	public String getBridalToolkitToken() {
		return bridalToolkitToken;
	}

	/**
	 * @param bridalToolkitToken the bridalToolkitToken to set
	 */
	public void setBridalToolkitToken(String bridalToolkitToken) {
		this.bridalToolkitToken = bridalToolkitToken;
	}
	
}
