/**
 * 
 */
package com.bbb.payment.giftcard;

import atg.payment.PaymentStatus;

/**
 * @author vagra4
 * 
 */
public interface GiftCardStatus extends PaymentStatus {

	/**
	 * getter for property authCode
	 */
	public abstract String getAuthCode();

	/**
	 * getter for property traceNumber
	 */
	public abstract String getTraceNumber();

	/**
	 * getter for property authRespCode
	 */
	public abstract String getAuthRespCode();

	/**
	 * getter for property cardClass
	 */
	public abstract String getCardClass();

	/**
	 * getter for property previousBalance
	 */
	public String getPreviousBalance(); 

}
