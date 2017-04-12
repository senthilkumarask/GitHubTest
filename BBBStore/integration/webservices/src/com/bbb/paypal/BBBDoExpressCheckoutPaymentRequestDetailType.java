/**
 * BBBDoExpressCheckoutPaymentRequestDetailType.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;


/**
 * @author ssh108
 *
 */

public class BBBDoExpressCheckoutPaymentRequestDetailType {
	
	private String token;
	
	private String payerId;
	
	private BBBBasicAmountType orderTotal;
	
	
	/**
	 * @return the orderTotal
	 */
	public BBBBasicAmountType getOrderTotal() {
		return orderTotal;
	}

	/**
	 * @param orderTotal the orderTotal to set
	 */
	public void setOrderTotal(BBBBasicAmountType orderTotal) {
		this.orderTotal = orderTotal;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the payerId
	 */
	public String getPayerId() {
		return payerId;
	}

	/**
	 * @param payerId the payerId to set
	 */
	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	/**
	 * @return the paymentdetail
	 */
		
	
}
