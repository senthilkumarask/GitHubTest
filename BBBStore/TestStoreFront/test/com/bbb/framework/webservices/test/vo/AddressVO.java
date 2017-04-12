/*
 *
 * File  : AddressVO.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.webservices.test.vo;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * I am just an example vo. Please ignore me when it comes to real application.
 * I was created to show address management 
 * @author manohar
 * @version 1.0
 */
public class AddressVO implements Serializable {

	/** The address type. */
	private String addressType;
	
	/** The address line1. */
	private String addressLine1;
	
	/** The address line2. */
	private String addressLine2;
	
	/** The city. */
	private String city;
	
	/** The state. */
	private String state;
	
	/** The zip code. */
	private String zipCode;
	
	/** The country. */
	private String country;
	
	/** The us urban name. */
	private String usUrbanName;
	
	/** The match score. */
	private String matchScore;
	
	/** The apartment number. */
	private String apartmentNumber;
	
	/** The apartment type. */
	private String apartmentType;
	
	/** The house number. */
	private String houseNumber;
	
	/** The multiple matches found ind. */
	private boolean multipleMatchesFoundInd;
	
	/** The address not found in code1 ind. */
	private boolean addressNotFoundInCode1Ind;
	
	/** The confidence. */
	private String confidence;

	/**
	 * Gets the apartment number.
	 * 
	 * @return the apartment number
	 */
	public String getApartmentNumber() {
		return apartmentNumber;
	}

	/**
	 * Sets the apartment number.
	 * 
	 * @param apartmentNumber the new apartment number
	 */
	public void setApartmentNumber(String apartmentNumber) {
		this.apartmentNumber = apartmentNumber;
	}

	/**
	 * Gets the apartment type.
	 * 
	 * @return the apartment type
	 */
	public String getApartmentType() {
		return apartmentType;
	}

	/**
	 * Sets the apartment type.
	 * 
	 * @param apartmentType the new apartment type
	 */
	public void setApartmentType(final String apartmentType) {
		this.apartmentType = apartmentType;
	}

	/**
	 * Gets the house number.
	 * 
	 * @return the house number
	 */
	public String getHouseNumber() {
		return houseNumber;
	}

	/**
	 * Sets the house number.
	 * 
	 * @param houseNumber the new house number
	 */
	public void setHouseNumber(final String houseNumber) {
		this.houseNumber = houseNumber;
	}

	/**
	 * Checks if is multiple matches found ind.
	 * 
	 * @return true, if is multiple matches found ind
	 */
	public boolean isMultipleMatchesFoundInd() {
		return multipleMatchesFoundInd;
	}

	/**
	 * Sets the multiple matches found ind.
	 * 
	 * @param multipleMatchesFoundInd the new multiple matches found ind
	 */
	public void setMultipleMatchesFoundInd(final boolean multipleMatchesFoundInd) {
		this.multipleMatchesFoundInd = multipleMatchesFoundInd;
	}

	/**
	 * Gets the address type.
	 * 
	 * @return the address type
	 */
	public String getAddressType() {
		return addressType;
	}

	/**
	 * Sets the address type.
	 * 
	 * @param addressType the new address type
	 */
	public void setAddressType(final String addressType) {
		this.addressType = addressType;
	}

	/**
	 * Gets the address line1.
	 * 
	 * @return the address line1
	 */
	public String getAddressLine1() {
		return addressLine1;
	}

	/**
	 * Sets the address line1.
	 * 
	 * @param addressLine1 the new address line1
	 */
	public void setAddressLine1(final String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * Gets the address line2.
	 * 
	 * @return the address line2
	 */
	public String getAddressLine2() {
		return addressLine2;
	}

	/**
	 * Sets the address line2.
	 * 
	 * @param addressLine2 the new address line2
	 */
	public void setAddressLine2(final String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * Gets the city.
	 * 
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 * 
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
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
	 * @param state the new state
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * Gets the zip code.
	 * 
	 * @return the zip code
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * Sets the zip code.
	 * 
	 * @param zipCode the new zip code
	 */
	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * Gets the country.
	 * 
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 * 
	 * @param country the new country
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * Gets the us urban name.
	 * 
	 * @return the us urban name
	 */
	public String getUsUrbanName() {
		return usUrbanName;
	}

	/**
	 * Sets the us urban name.
	 * 
	 * @param usUrbanName the new us urban name
	 */
	public void setUsUrbanName(final String usUrbanName) {
		this.usUrbanName = usUrbanName;
	}

	/**
	 * Gets the match score.
	 * 
	 * @return the match score
	 */
	public String getMatchScore() {
		return matchScore;
	}

	/**
	 * Sets the match score.
	 * 
	 * @param matchScore the new match score
	 */
	public void setMatchScore(final String matchScore) {
		this.matchScore = matchScore;
	}

	/**
	 * Checks if is address not found in code1 ind.
	 * 
	 * @return true, if is address not found in code1 ind
	 */
	public boolean isAddressNotFoundInCode1Ind() {
		return addressNotFoundInCode1Ind;
	}

	/**
	 * Sets the address not found in code1 ind.
	 * 
	 * @param addressNotFoundInCode1Ind the new address not found in code1 ind
	 */
	public void setAddressNotFoundInCode1Ind(final boolean addressNotFoundInCode1Ind) {
		this.addressNotFoundInCode1Ind = addressNotFoundInCode1Ind;
	}

	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	public String getConfidence() {
		return confidence;
	}

	/**
	 * Sets the confidence.
	 * 
	 * @param confidence the new confidence
	 */
	public void setConfidence(final String confidence) {
		this.confidence = confidence;
	}


}
