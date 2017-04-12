package com.bbb.integration.csr;

import java.io.Serializable;

public class RegistryInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* Registry Details from webservice*/	
	private String registryId;
	private String eventDate;
	private String eventType;
	private String ownerProfileId;
	private String coownerProfileId;
	private String ownerEmailAddress;
	private String ownerFirstName;
	private String ownerLastName;
	private String coownerEmailAddress;
	private String coOwnerFirstName;
	private String coOwnerLastName;

	/* Registry Details stored in DB*/
	private String registryIDATG;
	private String eventDateATG;
	private String eventTypeATG;
	private String ownerProfileIDATG;
	private String ownerEmailAddressATG;
	private String ownerFirstNameATG;
	private String ownerLastNameATG;
	private String ownerRegistrationDateATG;
	private boolean isOwnerMigrated;
	private String  coownerProfileIDATG;
	private String coownerEmailAddressATG;
	private String coOwnerFirstNameATG;
	private String coOwnerLastNameATG;
	private String coownerRegistrationDateATG;
	private boolean isCoownerMigrated;
	
	private boolean isOwnerAndInputEmailSame;
	
	public String getRegistryIDATG() {
		return registryIDATG;
	}

	public void setRegistryIDATG(String registryIDATG) {
		this.registryIDATG = registryIDATG;
	}

	public String getOwnerProfileIDATG() {
		return ownerProfileIDATG;
	}

	public void setOwnerProfileIDATG(String ownerProfileIDATG) {
		this.ownerProfileIDATG = ownerProfileIDATG;
	}

	public String getOwnerEmailAddressATG() {
		return ownerEmailAddressATG;
	}

	public void setOwnerEmailAddressATG(String ownerEmailAddressATG) {
		this.ownerEmailAddressATG = ownerEmailAddressATG;
	}

	public String getOwnerRegistrationDateATG() {
		return ownerRegistrationDateATG;
	}

	public void setOwnerRegistrationDateATG(String ownerRegistrationDateATG) {
		this.ownerRegistrationDateATG = ownerRegistrationDateATG;
	}

	public boolean isOwnerMigrated() {
		return isOwnerMigrated;
	}

	public void setOwnerMigrated(boolean isOwnerMigrated) {
		this.isOwnerMigrated = isOwnerMigrated;
	}

	public String getCoownerProfileIDATG() {
		return coownerProfileIDATG;
	}

	public void setCoownerProfileIDATG(String coownerProfileIDATG) {
		this.coownerProfileIDATG = coownerProfileIDATG;
	}

	public String getCoownerEmailAddressATG() {
		return coownerEmailAddressATG;
	}

	public void setCoownerEmailAddressATG(String coownerEmailAddressATG) {
		this.coownerEmailAddressATG = coownerEmailAddressATG;
	}

	public String getCoownerRegistrationDateATG() {
		return coownerRegistrationDateATG;
	}

	public void setCoownerRegistrationDateATG(String coownerRegistrationDateATG) {
		this.coownerRegistrationDateATG = coownerRegistrationDateATG;
	}

	public boolean isCoownerMigrated() {
		return isCoownerMigrated;
	}

	public void setCoownerMigrated(boolean isCoownerMigrated) {
		this.isCoownerMigrated = isCoownerMigrated;
	}


	
	/* Details of email address CSR user provides in form */
	private String profileIDOfEmail;
	
	public String getProfileOfEmail() {
		return profileIDOfEmail;
	}

	public void setProfileIDOfEmail(String profileOfEmail) {
		this.profileIDOfEmail = profileOfEmail;
	}

	public String getRegistrationDateOfEmailEnteredATG() {
		return registrationDateOfEmailEnteredATG;
	}

	public void setRegistrationDateOfEmailEnteredATG(
			String registrationDateOfEmailEnteredATG) {
		this.registrationDateOfEmailEnteredATG = registrationDateOfEmailEnteredATG;
	}

	private String registrationDateOfEmailEnteredATG;
	
	public String getRegistryId() {
		return registryId;
	}

	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventDateATG() {
		return eventDateATG;
	}

	public void setEventDateATG(String pEventDateATG) {
		this.eventDateATG = pEventDateATG;
	}

	public String getEventTypeATG() {
		return eventTypeATG;
	}

	public void setEventTypeATG(String pEventTypeATG) {
		this.eventTypeATG = pEventTypeATG;
	}

	public String getOwnerProfileId() {
		return ownerProfileId;
	}

	public void setOwnerProfileId(String ownerProfileId) {
		this.ownerProfileId = ownerProfileId;
	}

	public String getCoownerProfileId() {
		return coownerProfileId;
	}

	public void setCoownerProfileId(String coownerProfileId) {
		this.coownerProfileId = coownerProfileId;
	}

	public String getOwnerEmailAddress() {
		return ownerEmailAddress;
	}

	public void setOwnerEmailAddress(String ownerEmailAddress) {
		this.ownerEmailAddress = ownerEmailAddress;
	}

	public String getCoownerEmailAddress() {
		return coownerEmailAddress;
	}

	public void setCoownerEmailAddress(String coownerEmailAddress) {
		this.coownerEmailAddress = coownerEmailAddress;
	}

	public boolean isOwnerAndInputEmailSame() {
		return isOwnerAndInputEmailSame;
	}

	public void setOwnerAndInputEmailSame(boolean isOwnerAndInputEmailSame) {
		this.isOwnerAndInputEmailSame = isOwnerAndInputEmailSame;
	}

	public String getOwnerFirstName() {
		return ownerFirstName;
	}

	public void setOwnerFirstName(String ownerFirstName) {
		this.ownerFirstName = ownerFirstName;
	}

	public String getOwnerLastName() {
		return ownerLastName;
	}

	public void setOwnerLastName(String ownerLastName) {
		this.ownerLastName = ownerLastName;
	}

	public String getCoOwnerFirstName() {
		return coOwnerFirstName;
	}

	public void setCoOwnerFirstName(String coOwnerFirstName) {
		this.coOwnerFirstName = coOwnerFirstName;
	}

	public String getOwnerFirstNameATG() {
		return ownerFirstNameATG;
	}

	public void setOwnerFirstNameATG(String ownerFirstNameATG) {
		this.ownerFirstNameATG = ownerFirstNameATG;
	}

	public String getOwnerLastNameATG() {
		return ownerLastNameATG;
	}

	public void setOwnerLastNameATG(String ownerLastNameATG) {
		this.ownerLastNameATG = ownerLastNameATG;
	}

	public String getCoOwnerFirstNameATG() {
		return coOwnerFirstNameATG;
	}

	public void setCoOwnerFirstNameATG(String coOwnerFirstNameATG) {
		this.coOwnerFirstNameATG = coOwnerFirstNameATG;
	}

	public String getCoOwnerLastNameATG() {
		return coOwnerLastNameATG;
	}

	public void setCoOwnerLastNameATG(String coOwnerLastNameATG) {
		this.coOwnerLastNameATG = coOwnerLastNameATG;
	}

	public String getCoOwnerLastName() {
		return coOwnerLastName;
	}

	public void setCoOwnerLastName(String coOwnerLastName) {
		this.coOwnerLastName = coOwnerLastName;
	}

}
