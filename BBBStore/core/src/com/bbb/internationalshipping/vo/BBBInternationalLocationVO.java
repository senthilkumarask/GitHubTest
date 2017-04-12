package com.bbb.internationalshipping.vo;

import java.io.Serializable;

/**
 * This class is used for getting the Location details
 * {@link InternationalLocation}.
 * @version 1.0
 */
public class BBBInternationalLocationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to countryCode.
	 */
	private String countryCode;
	/**
	 * This variable is used to point to countryName.
	 */
	private String countryName;
	/**
	 * This variable is used to point to shippingEnabled.
	 */
	private boolean isShippingEnabled;
	/**
	 * @return the countryCode
	 */
	public final String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public final void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the countryName
	 */
	public final String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName the countryName to set
	 */
	public final void setCountryName(final String countryName) {
		this.countryName = countryName;
	}
	/**
	 * @return the isShippingEnabled
	 */
	public final boolean isShippingEnabled() {
		return isShippingEnabled;
	}
	/**
	 * @param isShippingEnabled the isShippingEnabled to set
	 */
	public final void setShippingEnabled(boolean isShippingEnabled) {
		this.isShippingEnabled = isShippingEnabled;
	}
	
}
