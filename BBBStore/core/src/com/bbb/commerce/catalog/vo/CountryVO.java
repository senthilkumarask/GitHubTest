package com.bbb.commerce.catalog.vo;

import java.io.Serializable;


/**
 * @author magga3
 * This country VO is used to store the country codes and 
 * country names for mobile ICC.
 *
 */
public class CountryVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String countryCode;
	private String countryName;
	
	/**
	 * @param countryCode
	 * @param countryName
	 */
	public CountryVO(final String countryCode,final String countryName) {
		this.countryCode = countryCode;
		this.countryName = countryName;
	}

	
	/**
	 * @return countryCode
	 */
	public final String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * @param countryCode
	 */
	public final void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return countryName
	 */
	public final String getCountryName() {
		return this.countryName;
	}

	/**
	 * @param countryName
	 */
	public final void setCountryName(final String countryName) {
		this.countryName = countryName;
	}

	@Override
	public final String toString(){
		StringBuffer toString=new StringBuffer(" Country VO Details \n ");
		toString.append("Country Code  ").append(this.countryCode).append("\n")
		.append(" Country Name  ").append(this.countryName).append("\n");
		return toString.toString();
	}
}
