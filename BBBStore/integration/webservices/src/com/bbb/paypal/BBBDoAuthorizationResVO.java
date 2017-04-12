/**
 * BBBDoAuthorizationResVO.java
 *
 * Project:     BBB
 */
package com.bbb.paypal;

import java.util.Date;


/**
 * @author ssh108
 * 
 */
public class BBBDoAuthorizationResVO extends PayPalResponseVO {


	private static final long serialVersionUID = 1L;
	private String transactionId;
	private Date authTimeStamp;
	private String protectionEligibility;
	
	
	/**
	 * @return the protectionEligibility
	 */
	public String getProtectionEligibility() {
		return protectionEligibility;
	}
	/**
	 * @param protectionEligibility the protectionEligibility to set
	 */
	public void setProtectionEligibility(String protectionEligibility) {
		this.protectionEligibility = protectionEligibility;
	}
	/**
	 * @return the authTimeStamp
	 */
	public Date getAuthTimeStamp() {
		return authTimeStamp;
	}
	/**
	 * @param authTimeStamp the orderTimeStamp to set
	 */
	public void setAuthTimeStamp(Date authTimeStamp) {
		this.authTimeStamp = authTimeStamp;
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
