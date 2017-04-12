package com.bbb.selfservice.common;

import java.io.Serializable;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Seema Singhal
//
//Created on: 16-Jan-2012
//--------------------------------------------------------------------------------

/**
 * @author ssin93
 * 
 */
public class StoreAddressSuggestion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mCity;
	private String mAddress;
	private String mStateCode;
	private String street;

	/**
	 * @param pCity
	 * @param pCounty
	 * @param pStateCode
	 */
	public StoreAddressSuggestion(String pCity, String pAddress,
			String pStateCode, String pStreet) {

		super();

		mCity = pCity;
		mAddress = pAddress;
		mStateCode = pStateCode;
		street = pStreet;

	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return mCity;
	}

	/**
	 * @param pCity
	 *            the city to set
	 */
	public void setCity(String pCity) {
		mCity = pCity;
	}

	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return mStateCode;
	}

	/**
	 * @param pStateCode
	 *            the stateCode to set
	 */
	public void setStateCode(String pStateCode) {
		mStateCode = pStateCode;
	}

	/**
	 * @return the county
	 */
	public String getAddress() {
		return mAddress;
	}

	/**
	 * @param pCounty
	 *            the county to set
	 */
	public void setAddress(String pAddress) {
		mAddress = pAddress;
	}
	
	/**
	 * @return the street
	 */
	public final String getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public final void setStreet(String street) {
		this.street = street;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		strBuild.append(getStreet()).append(",").append(getCity()).append(",").append(getAddress()).append(",")
				.append(getStateCode());
		return strBuild.toString();
	}

}
