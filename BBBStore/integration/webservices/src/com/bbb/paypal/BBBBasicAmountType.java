/**
 * BBBBasicAmountType.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;

/**
 * 
 * @author ssh108
 *
 */
public class BBBBasicAmountType{
    private java.lang.String value;

    private String currencyID;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the currencyID
	 */
	public String getCurrencyID() {
		return currencyID;
	}

	/**
	 * @param currencyID the currencyID to set
	 */
	public void setCurrencyID(String currencyID) {
		this.currencyID = currencyID;
	}

	
}
