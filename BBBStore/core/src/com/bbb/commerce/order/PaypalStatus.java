/**
 * 
 */
package com.bbb.commerce.order;

import java.util.Date;

import atg.payment.PaymentStatus;

/**
 * @author ssh108
 * 
 */
public interface PaypalStatus extends PaymentStatus{
	
	/**
	 * getter for property PaypalOrder
	 */
	public abstract String getPaypalOrder();
	
	/**
	 * getter for property CorrelationId
	 */
	public abstract String getCorrelationId();
	
	/**
	 * getter for property ProtectionEligibility
	 */
	public abstract String getProtectionEligibility();
	
	/**
	 * getter for property TransId
	 */
	public abstract String getTransId();
	
	/**
	 * getter for property OrderTimestamp
	 */
	public abstract Date getOrderTimestamp();

	/**
	 * getter for property AuthTimeStamp
	 */
	public abstract Date getAuthTimeStamp();
}
