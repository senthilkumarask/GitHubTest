package com.bbb.commerce.giftregistry.tool;



/**
 * This class is used as VO for registrant
 * to send scheduled bulk email.
 *
 * @author apan25
 *
 */

public class RegistryBulkNotificationVO {
	//BPSI-147- Scheduler to send bulk monthly email

	private String registrantEmail;
	private String registFirstName;
	private String registLastName;
	private int recomCount;
	private String registryId;
	private String eventType;
	private String profileId;
	private String siteId;

	/**
	 *
	 * @return the registrant email address.
	 */
	public String getRegistrantEmail() {
		return registrantEmail;
	}

	/**
	 *
	 * @param the registrant email address to set.
	 */
	public void setRegistrantEmail(final String registrantEmail) {
		this.registrantEmail = registrantEmail;
	}

	/**
	 *
	 * @return the registrant first name.
	 */
	public String getRegistFirstName() {
		return registFirstName;
	}

	/**
	 *
	 * @param the registrant first name to set.
	 */
	public void setRegistFirstName(final String registFirstName) {
		this.registFirstName = registFirstName;
	}

	/**
	 *
	 * @return the registrant last name.
	 */
	public String getRegistLastName() {
		return registLastName;
	}

	/**
	 *
	 * @param the registrant last name to set.
	 */
	public void setRegistLastName(final String registLastName) {
		this.registLastName = registLastName;
	}

	/**
	 *
	 * @return the total number of recommendation count.
	 */
	public int getRecomCount() {
		return recomCount;
	}

	/**
	 *
	 * @param the total number of recommendation count to set.
	 */
	public void setRecomCount(final int recomCount) {
		this.recomCount = recomCount;
	}

	/**
	 *
	 * @return the registrant registryId.
	 */
	public String getRegistryId() {
		return registryId;
	}

	/**
	 *
	 * @param the registrant registryId to set.
	 */
	public void setRegistryId(final String registryId) {
		this.registryId = registryId;
	}

	/**
	 *
	 * @return the registrant event type.
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 *
	 * @param the registrant event type to set.
	 */
	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}

	/**
	 *
	 * @return the profile Id of registrant.
	 */
	public String getProfileId() {
		return profileId;
	}

	/**
	 *
	 * @param the profile Id of registrant to set.
	 */
	public void setProfileId(final String profileId) {
		this.profileId = profileId;
	}

	/**
	 *
	 * @return the site id.
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 *
	 * @param the site id to set.
	 */
	public void setSiteId(final String siteId) {
		this.siteId = siteId;
	}

	@Override
	public String toString() {
		return "RegistryBulkNotificationVO [registrantEmail=" + registrantEmail
				+ ", registFirstName=" + registFirstName + ", registLastName="
				+ registLastName + ", recomCount=" + recomCount
				+ ", registryId=" + registryId + ", eventType=" + eventType
				+ ", profileId=" + profileId + ", siteId=" + siteId + "]";
	}


}
