/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import atg.core.util.StringUtils;



// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry summary information properties.
 *
 * @author sku134
 */
public class RegistrySummaryVO implements Serializable{
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The registry type. */
	private RegistryTypes registryType;
	
	/** The event date. */
	private String eventDate;
	/** The event date. */
	private String eventDateCanada;
	
	/** The primary registrant first name. */
	private String primaryRegistrantFirstName;
	
	private String ownerProfileID;
	
	private String coownerProfileID;
	
	/** The primary registrant last name. */
	private String primaryRegistrantLastName;
	
	/** The primary registrant maiden name. */
	private String primaryRegistrantMaidenName;
	
	/** The co registrant first name. */
	private String coRegistrantFirstName;
	
	/** The co registrant full name. */
	private String coRegistrantFullName;
	
	/** The primary registrant full name. */
	private String primaryRegistrantFullName;
	/** The co registrant last name. */
	private String coRegistrantLastName;
	
	/** The co registrant maiden name. */
	private String coRegistrantMaidenName;
	
	/** The tot entries. */
	private int totEntries;
	
	/** The gift purchased. */
	private int giftPurchased;
	
	/** The gift registered. */
	private int giftRegistered;
	
	/** The gift remaining. */
	private int giftRemaining;
	
	/** The days to go. */
	private int daysToGo;
	
	/** The shipping address. */
	private AddressVO shippingAddress;
	
	/** The future shipping address. */
	private AddressVO futureShippingAddress;
	
	/** The event vo. */
	private EventVO eventVO;
	
	/** The future shipping date. */
	private String futureShippingDate;
	
	/** The registry id. */
	private String registryId;
	
	/** The state. */
	private String state;
	
	/** The event type. */
	private String eventType;
	
	/** The event Description. */
	private String eventDescription;
	
	
	/** The event code. */
	private String eventCode;
	
	/** The registrant email. */
	private String registrantEmail;
	
	/** The co-registrant email. */
	private String coRegistrantEmail;
	
	/** The reg token. */
	private String regToken;
	
	/** The pwsurl. */
	private String pwsurl;
	
	/** The bridal toolkit token. */
	private String bridalToolkitToken;
	/** The bridal toolkit token. */
	private String personalWebsiteToken;
	
	private String AddrSubType;
	
	private boolean registryOwnedByProfile = false;
	
	/** The sub type. */
	private String subType;
	
	/** The eventDate date object */
	private Date eventDateObject;
	
	/** The days to next celebration */
	private int daysToNextCeleb;
	
	/** IS event yet to come */
	private boolean eventYetToCome;
	
	
	private String primaryRegistrantEmail;
	
	private String primaryRegistrantPrimaryPhoneNum;
	
	// LTL Registry Secondary Mobile Number
	private String primaryRegistrantMobileNum;
	
	private String favStoreId;
	
	private boolean allowedToScheduleAStoreAppointment;
	private boolean activeRegistryHasPoBoxAddress;
	
	private String isPublic;
	
	private String regTitle;
	
	/**
	 * @return the regTitle
	 */
	public String getRegTitle() {
		return regTitle;
	}

	/**
	 * @param regTitle the regTitle to set
	 */
	public void setRegTitle(String regTitle) {
		this.regTitle = regTitle;
	}

	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	public String getFavStoreId() {
		return favStoreId;
	}

	public void setFavStoreId(String favStoreId) {
		this.favStoreId = favStoreId;
	}

	public String getPrimaryRegistrantEmail() {
		return primaryRegistrantEmail;
	}

	public void setPrimaryRegistrantEmail(String primaryRegistrantEmail) {
		this.primaryRegistrantEmail = primaryRegistrantEmail;
	}

	public String getPrimaryRegistrantPrimaryPhoneNum() {
		return primaryRegistrantPrimaryPhoneNum;
	}

	public void setPrimaryRegistrantPrimaryPhoneNum(
			String primaryRegistrantPrimaryPhoneNum) {
		this.primaryRegistrantPrimaryPhoneNum = primaryRegistrantPrimaryPhoneNum;
	}

	
	public boolean isEventYetToCome() {
		return eventYetToCome;
	}

	public void setEventYetToCome(boolean eventYetToCome) {
		this.eventYetToCome = eventYetToCome;
	}

	public int getDaysToNextCeleb() {
		return daysToNextCeleb;
	}

	public void setDaysToNextCeleb(int daysToNextCeleb) {
		this.daysToNextCeleb = daysToNextCeleb;
	}

	public String getCoRegistrantFullName() {
		return coRegistrantFullName;
	}

	public void setCoRegistrantFullName(String coRegistrantFullName) {
		this.coRegistrantFullName = coRegistrantFullName;
	}

	public String getPrimaryRegistrantFullName() {
		return primaryRegistrantFullName;
	}

	public void setPrimaryRegistrantFullName(String primaryRegistrantFullName) {
		this.primaryRegistrantFullName = primaryRegistrantFullName;
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
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	
	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
	
	/**
	 * Gets the registry type.
	 *
	 * @return the registryType
	 */
	public RegistryTypes getRegistryType() {
		return registryType;
	}
	
	/**
	 * Sets the registry type.
	 *
	 * @param registryType the registryType to set
	 */
	public void setRegistryType(RegistryTypes registryType) {
		this.registryType = registryType;
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
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	
	/**
	 * Gets the primary registrant first name.
	 *
	 * @return the primaryRegistrantFirstName
	 */
	public String getPrimaryRegistrantFirstName() {
		return primaryRegistrantFirstName;
	}
	
	/**
	 * Sets the primary registrant first name.
	 *
	 * @param primaryRegistrantFirstName the primaryRegistrantFirstName to set
	 */
	public void setPrimaryRegistrantFirstName(String primaryRegistrantFirstName) {
		this.primaryRegistrantFirstName = primaryRegistrantFirstName;
	}
	
	/**
	 * Gets the primary registrant last name.
	 *
	 * @return the primaryRegistrantLastName
	 */
	public String getPrimaryRegistrantLastName() {
		return primaryRegistrantLastName;
	}
	
	/**
	 * Sets the primary registrant last name.
	 *
	 * @param primaryRegistrantLastName the primaryRegistrantLastName to set
	 */
	public void setPrimaryRegistrantLastName(String primaryRegistrantLastName) {
		this.primaryRegistrantLastName = primaryRegistrantLastName;
	}
	
	/**
	 * Gets the co registrant first name.
	 *
	 * @return the coRegistrantFirstName
	 */
	public String getCoRegistrantFirstName() {
		return coRegistrantFirstName;
	}
	
	/**
	 * Sets the co registrant first name.
	 *
	 * @param coRegistrantFirstName the coRegistrantFirstName to set
	 */
	public void setCoRegistrantFirstName(String coRegistrantFirstName) {
		this.coRegistrantFirstName = coRegistrantFirstName;
	}
	
	/**
	 * Gets the co registrant last name.
	 *
	 * @return the coRegistrantLastName
	 */
	public String getCoRegistrantLastName() {
		return coRegistrantLastName;
	}
	
	/**
	 * Sets the co registrant last name.
	 *
	 * @param coRegistrantLastName the coRegistrantLastName to set
	 */
	public void setCoRegistrantLastName(String coRegistrantLastName) {
		this.coRegistrantLastName = coRegistrantLastName;
	}
	
	/**
	 * Gets the gift purchased.
	 *
	 * @return the giftPurchased
	 */
	public int getGiftPurchased() {
		return giftPurchased;
	}
	
	/**
	 * Sets the gift purchased.
	 *
	 * @param giftPurchased the giftPurchased to set
	 */
	public void setGiftPurchased(int giftPurchased) {
		this.giftPurchased = giftPurchased;
	}
	
	/**
	 * Gets the gift registered.
	 *
	 * @return the giftRegistered
	 */
	public int getGiftRegistered() {
		return giftRegistered;
	}
	
	/**
	 * Sets the gift registered.
	 *
	 * @param giftRegistered the giftRegistered to set
	 */
	public void setGiftRegistered(int giftRegistered) {
		this.giftRegistered = giftRegistered;
	}
	
	/**
	 * Gets the gift remaining.
	 *
	 * @return the giftRemaining
	 */
	public int getGiftRemaining() {
		return giftRemaining;
	}
	
	/**
	 * Sets the gift remaining.
	 *
	 * @param giftRemaining the giftRemaining to set
	 */
	public void setGiftRemaining(int giftRemaining) {
		this.giftRemaining = giftRemaining;
	}
	
	/**
	 * Gets the days to go.
	 *
	 * @return the daysToGo
	 */
	public int getDaysToGo() {
		return daysToGo;
	}
	
	/**
	 * Sets the days to go.
	 *
	 * @param daysToGo the daysToGo to set
	 */
	public void setDaysToGo(int daysToGo) {
		this.daysToGo = daysToGo;
	}
	
	/**
	 * Gets the shipping address.
	 *
	 * @return the shippingAddress
	 */
	public AddressVO getShippingAddress() {
		return shippingAddress;
	}
	
	/**
	 * Sets the shipping address.
	 *
	 * @param shippingAddress the shippingAddress to set
	 */
	public void setShippingAddress(AddressVO shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
	
	/**
	 * Gets the future shipping address.
	 *
	 * @return the futureShippingAddress
	 */
	public AddressVO getFutureShippingAddress() {
		return futureShippingAddress;
	}
	
	/**
	 * Sets the future shipping address.
	 *
	 * @param futureShippingAddress the futureShippingAddress to set
	 */
	public void setFutureShippingAddress(AddressVO futureShippingAddress) {
		this.futureShippingAddress = futureShippingAddress;
	}
	
	
	
	/**
	 * Gets the future shipping date.
	 *
	 * @return the futureShippingDate
	 */
	public String getFutureShippingDate() {
		return futureShippingDate;
	}
	
	/**
	 * Sets the future shipping date.
	 *
	 * @param futureShippingDate the futureShippingDate to set
	 */
	public void setFutureShippingDate(String futureShippingDate) {
		this.futureShippingDate = futureShippingDate;
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
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Gets the tot entries.
	 *
	 * @return the totEntries
	 */
	public int getTotEntries() {
		return totEntries;
	}
	
	/**
	 * Sets the tot entries.
	 *
	 * @param totEntries the totEntries to set
	 */
	public void setTotEntries(int totEntries) {
		this.totEntries = totEntries;
	}
	
	/**
	 * Gets the registrant email.
	 *
	 * @return the registrantEmail
	 */
	public String getRegistrantEmail() {
		return registrantEmail;
	}
	
	/**
	 * Sets the registrant email.
	 *
	 * @param registrantEmail the registrantEmail to set
	 */
	public void setRegistrantEmail(String registrantEmail) {
		this.registrantEmail = registrantEmail;
	}
	
	/**
	 * Gets the reg token.
	 *
	 * @return the regToken
	 */
	public String getRegToken() {
		return regToken;
	}
	
	/**
	 * Sets the reg token.
	 *
	 * @param regToken the regToken to set
	 */
	public void setRegToken(String regToken) {
		this.regToken = regToken;
	}
	
	/**
	 * Gets the primary registrant maiden name.
	 *
	 * @return the primaryRegistrantMaidenName
	 */
	public String getPrimaryRegistrantMaidenName() {
		return primaryRegistrantMaidenName;
	}
	
	/**
	 * Sets the primary registrant maiden name.
	 *
	 * @param primaryRegistrantMaidenName the primaryRegistrantMaidenName to set
	 */
	public void setPrimaryRegistrantMaidenName(String primaryRegistrantMaidenName) {
		this.primaryRegistrantMaidenName = primaryRegistrantMaidenName;
	}
	
	/**
	 * Gets the co registrant maiden name.
	 *
	 * @return the coRegistrantMaidenName
	 */
	public String getCoRegistrantMaidenName() {
		return coRegistrantMaidenName;
	}
	
	/**
	 * Sets the co registrant maiden name.
	 *
	 * @param coRegistrantMaidenName the coRegistrantMaidenName to set
	 */
	public void setCoRegistrantMaidenName(String coRegistrantMaidenName) {
		this.coRegistrantMaidenName = coRegistrantMaidenName;
	}
	
	/**
	 * Gets the pwsurl.
	 *
	 * @return the pwsurl
	 */
	public String getPwsurl() {
		return pwsurl;
	}
	
	/**
	 * Sets the pwsurl.
	 *
	 * @param pwsurl the pwsurl to set
	 */
	public void setPwsurl(String pwsurl) {
		this.pwsurl = pwsurl;
	}
	
	/**
	 * Gets the bridal toolkit token.
	 *
	 * @return the bridalToolkitToken
	 */
	public String getBridalToolkitToken() {
		return bridalToolkitToken;
	}
	
	/**
	 * Sets the bridal toolkit token.
	 *
	 * @param bridalToolkitToken the bridalToolkitToken to set
	 */
	public void setBridalToolkitToken(String bridalToolkitToken) {
		this.bridalToolkitToken = bridalToolkitToken;
	}
	
	/**
	 * Gets the sub type.
	 *
	 * @return the subType
	 */
	public String getSubType() {
		return subType;
	}
	
	/**
	 * Sets the sub type.
	 *
	 * @param subType the subType to set
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	/**
	 * Gets the event vo.
	 *
	 * @return the eventVO
	 */
	public EventVO getEventVO() {
		return eventVO;
	}
	
	/**
	 * Sets the event vo.
	 *
	 * @param eventVO the eventVO to set
	 */
	public void setEventVO(EventVO eventVO) {
		this.eventVO = eventVO;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	
	private String registryInfo;

	public void setRegistryInfo(String registryInfo) {
		this.registryInfo = registryInfo;
	}

	public String getRegistryInfo() {
		StringBuilder registryInfo = new StringBuilder();
		registryInfo.append(this.getPrimaryRegistrantFirstName()).append(" ").append(this.getPrimaryRegistrantLastName()).append("  :  ");
		if( !(StringUtils.isEmpty(this.getCoRegistrantFirstName())) && !(StringUtils.isEmpty(this.getCoRegistrantLastName())) ){
			registryInfo.append(this.getCoRegistrantFirstName()).append(" ").append(this.getCoRegistrantLastName()).append("  :  ");			
		}		
		registryInfo.append(this.getRegistryId());		
		this.registryInfo = registryInfo.toString();
		
		return this.registryInfo;
	}

	public Date getEventDateObject() {
		
		if(!StringUtils.isEmpty(eventDate)){
			
			 DateFormat formatter ; 
			 try{ 
				 formatter = new SimpleDateFormat("MM/dd/yyyy");
				 eventDateObject = (Date)formatter.parse(eventDate);  
				 
			  } catch (ParseException e){
				  eventDateObject = null;
			  }  
			 
		}

		return eventDateObject;
	}

	public void setEventDateObject(Date eventDateObject) {
		this.eventDateObject = eventDateObject;
	}

	public String getEventDateCanada() {
		return eventDateCanada;
	}

	public void setEventDateCanada(String eventDateCanada) {
		this.eventDateCanada = eventDateCanada;
	}

	public String getPersonalWebsiteToken() {
		return personalWebsiteToken;
	}

	public void setPersonalWebsiteToken(String personalWebsiteToken) {
		this.personalWebsiteToken = personalWebsiteToken;
	}

	/**
	 * @return the addrSubType
	 */
	public String getAddrSubType() {
		return AddrSubType;
	}

	/**
	 * @param addrSubType the addrSubType to set
	 */
	public void setAddrSubType(String addrSubType) {
		AddrSubType = addrSubType;
	}

	public String getCoRegistrantEmail() {
		return coRegistrantEmail;
	}

	public void setCoRegistrantEmail(String coRegistrantEmail) {
		this.coRegistrantEmail = coRegistrantEmail;
	}

	public String getOwnerProfileID() {
		return ownerProfileID;
	}

	public void setOwnerProfileID(String ownerProfileID) {
		this.ownerProfileID = ownerProfileID;
	}

	public String getCoownerProfileID() {
		return coownerProfileID;
	}

	public void setCoownerProfileID(String coownerProfileID) {
		this.coownerProfileID = coownerProfileID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegistrySummaryVO [registryType=" + registryType
				+ ", eventDate=" + eventDate + ", eventDateCanada="
				+ eventDateCanada + ", primaryRegistrantFirstName="
				+ primaryRegistrantFirstName + ", ownerProfileID="
				+ ownerProfileID + ", coownerProfileID=" + coownerProfileID
				+ ", primaryRegistrantLastName=" + primaryRegistrantLastName
				+ ", primaryRegistrantMaidenName="
				+ primaryRegistrantMaidenName + ", coRegistrantFirstName="
				+ coRegistrantFirstName + ", coRegistrantFullName="
				+ coRegistrantFullName + ", primaryRegistrantFullName="
				+ primaryRegistrantFullName + ", coRegistrantLastName="
				+ coRegistrantLastName + ", coRegistrantMaidenName="
				+ coRegistrantMaidenName + ", totEntries=" + totEntries
				+ ", giftPurchased=" + giftPurchased + ", giftRegistered="
				+ giftRegistered + ", giftRemaining=" + giftRemaining
				+ ", daysToGo=" + daysToGo + ", shippingAddress="
				+ shippingAddress + ", futureShippingAddress="
				+ futureShippingAddress + ", eventVO=" + eventVO
				+ ", futureShippingDate=" + futureShippingDate
				+ ", registryId=" + registryId + ", state=" + state
				+ ", eventType=" + eventType + ", eventCode=" + eventCode
				+ ", registrantEmail=" + registrantEmail
				+ ", coRegistrantEmail=" + coRegistrantEmail + ", regToken="
				+ regToken + ", pwsurl=" + pwsurl + ", bridalToolkitToken="
				+ bridalToolkitToken + ", personalWebsiteToken="
				+ personalWebsiteToken + ", AddrSubType=" + AddrSubType
				+ ", subType=" + subType + ", eventDateObject="
				+ ", registryOwnedByProfile=" + registryOwnedByProfile + ", eventDateObject="
				+ eventDateObject + ", daysToNextCeleb=" + daysToNextCeleb
				+ ", eventYetToCome=" + eventYetToCome + ", registryInfo="
				+ registryInfo + ", regTitle=" + regTitle + "]";
	}

	/**
	 * @return the registryOwnedByProfile
	 */
	public boolean isRegistryOwnedByProfile() {
		return registryOwnedByProfile;
	}

	/**
	 * @param registryOwnedByProfile the registryOwnedByProfile to set
	 */
	public void setRegistryOwnedByProfile(boolean registryOwnedByProfile) {
		this.registryOwnedByProfile = registryOwnedByProfile;
	}

	public boolean isAllowedToScheduleAStoreAppointment() {
		return allowedToScheduleAStoreAppointment;
	}

	public void setAllowedToScheduleAStoreAppointment(
			boolean allowedToScheduleAStoreAppointment) {
		this.allowedToScheduleAStoreAppointment = allowedToScheduleAStoreAppointment;
	}

	// LTL Registry Secondary Mobile Number PSI7
	/**
	 * @return the primaryRegistrantMobileNum
	 */
	public String getPrimaryRegistrantMobileNum() {
		return primaryRegistrantMobileNum;
	}

	/**
	 * @param primaryRegistrantMobileNum the primaryRegistrantMobileNum to set
	 */
	public void setPrimaryRegistrantMobileNum(String primaryRegistrantMobileNum) {
		this.primaryRegistrantMobileNum = primaryRegistrantMobileNum;
	}
	// LTL Registry Secondary Mobile Number PSI7

	public boolean isActiveRegistryHasPoBoxAddress() {
		return activeRegistryHasPoBoxAddress;
	}

	public void setActiveRegistryHasPoBoxAddress(
			boolean activeRegistryHasPoBoxAddress) {
		this.activeRegistryHasPoBoxAddress = activeRegistryHasPoBoxAddress;
	}
}
