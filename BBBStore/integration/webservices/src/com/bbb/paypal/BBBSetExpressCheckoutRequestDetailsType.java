/**
 * BBBSetExpressCheckoutRequestDetailsType.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;

/**
 * @author ssh108
 *
 */

public class BBBSetExpressCheckoutRequestDetailsType  {

    private BBBBasicAmountType orderTotal;


    private String returnURL;


    private String cancelURL;


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
	 * @return the returnURL
	 */
	public String getReturnURL() {
		return returnURL;
	}


	/**
	 * @param returnURL the returnURL to set
	 */
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}


	/**
	 * @return the cancelURL
	 */
	public String getCancelURL() {
		return cancelURL;
	}


	/**
	 * @param cancelURL the cancelURL to set
	 */
	public void setCancelURL(String cancelURL) {
		this.cancelURL = cancelURL;
	}


}
