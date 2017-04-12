package com.bbb.certona.vo;

import java.io.Serializable;
import java.util.Date;

public class CertonaGiftRegistryVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String registryId;
	private String siteId;
	private String eventType;
	private Date eventDate;
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
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}
	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return the eventDate
	 */
	public Date getEventDate() {
		return eventDate;
	}
	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	
}
