/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * This class provides the Registrant information properties.
 *
 * @author sku134
 */
public class RegistrantVO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The first name. */
	private String firstName;
	
	/** The last name. */
	private String lastName;
	
	/** The email. */
	private String email;
	
	/** The primary phone. */
	private String primaryPhone;
	
	/** The cell phone. */
	private String cellPhone;
	
	/** The contact address. */
	private AddressVO contactAddress;
	
	/** The profile id. */
	private String profileId;
	
	/** The baby maiden name. */
	private String babyMaidenName;
	
	/** The address selected. */
	private String addressSelected;
	
	/** The co reg email flag. */
	private String coRegEmailFlag;
	
	/**
	 * Gets the first name.
	 *
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * Sets the first name.
	 *
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * Gets the last name.
	 *
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * Sets the last name.
	 *
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	/**
	 * Gets the primary phone.
	 *
	 * @return the primaryPhone
	 */
	public String getPrimaryPhone() {
		return primaryPhone;
	}
	
	/**
	 * Sets the primary phone.
	 *
	 * @param primaryPhone the primaryPhone to set
	 */
	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}
	
	/**
	 * Gets the cell phone.
	 *
	 * @return the cellPhone
	 */
	public String getCellPhone() {
		return cellPhone;
	}
	
	/**
	 * Sets the cell phone.
	 *
	 * @param cellPhone the cellPhone to set
	 */
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	
	/**
	 * Gets the contact address.
	 *
	 * @return the contactAddress
	 */
	public AddressVO getContactAddress() {
		
		if (contactAddress != null){
			return contactAddress;
		} else {
			contactAddress = new AddressVO();
			return contactAddress;
		}

	}
	
	/**
	 * Sets the contact address.
	 *
	 * @param contactAddress the contactAddress to set
	 */
	public void setContactAddress(AddressVO contactAddress) {
		this.contactAddress = contactAddress;
	}
	
	/**
	 * Gets the baby maiden name.
	 *
	 * @return the babyMaidenName
	 */
	public String getBabyMaidenName() {
		return babyMaidenName;
	}
	
	/**
	 * Sets the baby maiden name.
	 *
	 * @param babyMaidenName the babyMaidenName to set
	 */
	public void setBabyMaidenName(String babyMaidenName) {
		this.babyMaidenName = babyMaidenName;
	}
	
	/**
	 * Gets the profile id.
	 *
	 * @return the profileId
	 */
	public String getProfileId() {
		return profileId;
	}
	
	/**
	 * Sets the profile id.
	 *
	 * @param profileId the profileId to set
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Sets the email.
	 *
	 * @param pEmail the email to set
	 */
	public void setEmail(String pEmail) {
		if(pEmail !=null && pEmail.length() > 0){
		email = pEmail.toLowerCase();
		}
	}
	
	/**
	 * Gets the address selected.
	 *
	 * @return the addressSelected
	 */
	public String getAddressSelected() {
		return addressSelected;
	}
	
	/**
	 * Sets the address selected.
	 *
	 * @param pAddressSelected the addressSelected to set
	 */
	public void setAddressSelected(String pAddressSelected) {
		addressSelected = pAddressSelected;
	}
	
	/**
	 * Gets the co reg email flag.
	 *
	 * @return the co reg email flag
	 */
	public String getCoRegEmailFlag() {
		return coRegEmailFlag;
	}
	
	/**
	 * Sets the co reg email flag.
	 *
	 * @param coRegEmailFlag the new co reg email flag
	 */
	public void setCoRegEmailFlag(String coRegEmailFlag) {
		this.coRegEmailFlag = coRegEmailFlag;
	}
	
	
}
