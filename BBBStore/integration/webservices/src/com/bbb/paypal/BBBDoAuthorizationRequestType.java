/**
 * BBBDoAuthorizationRequestType.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;

/**
 * @author ssh108
 *
 */

public class BBBDoAuthorizationRequestType  {

	private String transactionId;
	
	private BBBBasicAmountType amount;
	
	

	/**
	 * @return the amount
	 */
	public BBBBasicAmountType getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BBBBasicAmountType amount) {
		this.amount = amount;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

    
}
