/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class RegistryHeaderVO implements Serializable {
	
	private static final long serialVersionUID = 6354695488678933736L;
	private String regNum;
	private String isPublic;
	private String eventType;
	private String eventDate;
	private String promoEmailFlag;
	private String password;
	private String passwordHint;
	private String guestPassword;

	private String showerDate;
	private String otherDate;
	private String networkAffiliation;
	private String estimateNumGuests;
	private String siteId;
	private Long jdaDate;

	/**
	 * @return the regNum
	 */
	public String getRegNum() {
		return regNum;
	}

	/**
	 * @param regNum the regNum to set
	 */
	public void setRegNum(String regNum) {
		this.regNum = regNum;
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
	 * @return the promoEmailFlag
	 */
	public String getPromoEmailFlag() {
		return promoEmailFlag;
	}

	/**
	 * @param promoEmailFlag the promoEmailFlag to set
	 */
	public void setPromoEmailFlag(String promoEmailFlag) {
		this.promoEmailFlag = promoEmailFlag;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the passwordHint
	 */
	public String getPasswordHint() {
		return passwordHint;
	}

	/**
	 * @param passwordHint the passwordHint to set
	 */
	public void setPasswordHint(String passwordHint) {
		this.passwordHint = passwordHint;
	}

	/**
	 * @return the guestPassword
	 */
	public String getGuestPassword() {
		return guestPassword;
	}

	/**
	 * @param guestPassword the guestPassword to set
	 */
	public void setGuestPassword(String guestPassword) {
		this.guestPassword = guestPassword;
	}

	/**
	 * @return the showerDate
	 */
	public String getShowerDate() {
		return showerDate;
	}

	/**
	 * @param showerDate the showerDate to set
	 */
	public void setShowerDate(String showerDate) {
		this.showerDate = showerDate;
	}

	/**
	 * @return the otherDate
	 */
	public String getOtherDate() {
		return otherDate;
	}

	/**
	 * @param otherDate the otherDate to set
	 */
	public void setOtherDate(String otherDate) {
		this.otherDate = otherDate;
	}

	/**
	 * @return the networkAffiliation
	 */
	public String getNetworkAffiliation() {
		return networkAffiliation;
	}

	/**
	 * @param networkAffiliation the networkAffiliation to set
	 */
	public void setNetworkAffiliation(String networkAffiliation) {
		this.networkAffiliation = networkAffiliation;
	}

	/**
	 * @return the estimateNumGuests
	 */
	public String getEstimateNumGuests() {
		return estimateNumGuests;
	}

	/**
	 * @param estimateNumGuests the estimateNumGuests to set
	 */
	public void setEstimateNumGuests(String estimateNumGuests) {
		this.estimateNumGuests = estimateNumGuests;
	}

	/**
	 * @return the isPublic
	 */
	public String getIsPublic() {
		return isPublic;
	}

	/**
	 * @param isPublic
	 */
	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public Long getJdaDate() {
		return jdaDate;
	}

	public void setJdaDate(Long jdaDate) {
		this.jdaDate = jdaDate;
	}

	@Override
	public String toString() {
		return "RegistryHeaderVO [regNum=" + regNum + ", isPublic=" + isPublic
				+ ", eventType=" + eventType + ", eventDate=" + eventDate
				+ ", promoEmailFlag=" + promoEmailFlag + ", password="
				+ password + ", passwordHint=" + passwordHint
				+ ", guestPassword=" + guestPassword + ", showerDate="
				+ showerDate + ", otherDate=" + otherDate
				+ ", networkAffiliation=" + networkAffiliation
				+ ", estimateNumGuests=" + estimateNumGuests + ", siteId="
				+ siteId + ", jdaDate=" + jdaDate + "]";
	}

}
