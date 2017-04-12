/**
 * Paypal.java
 *
 * Project:     BBB
 */

package com.bbb.commerce.order;

import com.bbb.constants.BBBPayPalConstants;

import atg.commerce.order.PaymentGroupImpl;

/**
 * @author ssh108
 *
 */

public class Paypal extends PaymentGroupImpl{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @return
	 */
	public String getPayerStatus() {
		return (String) getPropertyValue("payerStatus");
	}

	/**
	 * @param payerStatus
	 */
	public void setPayerStatus(String payerStatus) {
		setPropertyValue("payerStatus", payerStatus);
	}
	/**
	 * @return
	 */
	public String getPayerEmail() {
		return (String) getPropertyValue("payerEmail");
	}

	/**
	 * @param payerEmail
	 */
	public void setPayerEmail(String payerEmail) {
		setPropertyValue("payerEmail", payerEmail);
	}

	/**
	 * @return
	 */
	public String getToken() {
		return (String) getPropertyValue("token");
	}

	
	/**
	 * @param token
	 */
	public void setToken(String token) {
		setPropertyValue("token", token);
	}
	
	
	/**
	 * @return
	 */
	public String getPayerId() {
		return (String) getPropertyValue(BBBPayPalConstants.PAYERID);
	}

	/**
	 * @param payerId
	 */
	public void setPayerId(String payerId) {
		setPropertyValue(BBBPayPalConstants.PAYERID, payerId);
	}
	
	/**
	 * @return
	 */
	public String getEmail() {
		return (String) getPropertyValue("email");
	}

	
	/**
	 * @param email
	 */
	public void setEmail(String email) {
		setPropertyValue("email", email);
	}

}