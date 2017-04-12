package com.bbb.internationalshipping.vo;

import java.io.Serializable;


/**
 * This class gives you the information
 * about currency code, name and symbol for the context.
 * @version 1.0
 */
public class BBBInternationalCurrencyVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to currencyCode.
	 */
	private String currencyCode;
	/**
	 * This variable is used to point to currencyName.
	 */
	private String currencyName;
	/**
	 * This variable is used to point to currencySymbol.
	 */
	private String currencySymbol;
	/**
	 * This variable is used to point to BillingEnabled.
	 */
	private boolean isBillingEnabled;

	/**
	 * @return the currencyCode
	 */
	public final String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public final void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the currencyName
	 */
	public final String getCurrencyName() {
		return currencyName;
	}

	/**
	 * @param currencyName the currencyName to set
	 */
	public final void setCurrencyName(final String currencyName) {
		this.currencyName = currencyName;
	}

	/**
	 * @return the currencySymbol
	 */
	public final String getCurrencySymbol() {
		return currencySymbol;
	}

	/**
	 * @param currencySymbol the currencySymbol to set
	 */
	public final void setCurrencySymbol(final String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	/**
	 * @return the isBillingEnabled
	 */
	public final boolean isBillingEnabled() {
		return isBillingEnabled;
	}

	/**
	 * @param isBillingEnabled the isBillingEnabled to set
	 */
	public final void setBillingEnabled(final boolean isBillingEnabled) {
		this.isBillingEnabled = isBillingEnabled;
	}
}