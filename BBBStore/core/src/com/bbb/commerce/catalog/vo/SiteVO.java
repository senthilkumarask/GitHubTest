package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class SiteVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String siteName;
	private String countryCode;
	
	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}
	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String toString(){
		StringBuffer toString=new StringBuffer("Site details from SiteVO  \n");
		toString.append("Site Name").append(siteName).append("\n")
		.append("Country Code").append(countryCode).append("\n");
		return toString.toString();
	}
}
