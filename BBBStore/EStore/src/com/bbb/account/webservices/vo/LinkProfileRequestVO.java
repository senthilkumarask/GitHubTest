package com.bbb.account.webservices.vo;

public class LinkProfileRequestVO extends GenericProfileRequestVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String profileId;
	private String regId;
	private String eventType;
	private String eventDate;
	private String coRegEmail;
	private String siteId;
	
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
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}
	/**
	 * @param regId the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
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
	 * @return the coRegEmail
	 */
	public String getCoRegEmail() {
		return coRegEmail;
	}
	/**
	 * @param coRegEmail the coRegEmail to set
	 */
	public void setCoRegEmail(String coRegEmail) {
		this.coRegEmail = coRegEmail;
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
	public void setSiteId(final String siteId) {
		this.siteId = siteId;
	}

	@Override
	public String toString() {
		return "LinkProfileRequestVO [profileId=" + profileId + ", regId=" + regId + ", eventType="
				+ eventType + ",eventDate=" + eventDate + ", coRegEmail="
				+ coRegEmail + ", siteId=" + siteId + "]";
	}
}
