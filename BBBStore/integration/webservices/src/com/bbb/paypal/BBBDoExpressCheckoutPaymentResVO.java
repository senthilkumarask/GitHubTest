/**
 * BBBDoExpressCheckoutPaymentResVO.java
 *
 * Project:     BBB
 */
package com.bbb.paypal;

import java.util.Date;


/**
 * @author ssh108
 * 
 */
public class BBBDoExpressCheckoutPaymentResVO extends PayPalResponseVO {


	private static final long serialVersionUID = 1L;
	private String transactionId;
	private String correlationId;
	private String protectionElig;
	private Date orderTimestamp;
	
	
	/**
	 * @return the orderTimestamp
	 */
	public Date getOrderTimestamp() {
		return orderTimestamp;
	}
	/**
	 * @param orderTimestamp the orderTimestamp to set
	 */
	public void setOrderTimestamp(Date orderTimestamp) {
		this.orderTimestamp = orderTimestamp;
	}
	/**
	 * @return the protectionElig
	 */
	public String getProtectionElig() {
		return protectionElig;
	}
	/**
	 * @param protectionElig the protectionElig to set
	 */
	public void setProtectionElig(String protectionElig) {
		this.protectionElig = protectionElig;
	}
	/**
	 * @return the correlationId
	 */
	public String getCorrelationId() {
		return correlationId;
	}
	/**
	 * @param correlationId the correlationId to set
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
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
