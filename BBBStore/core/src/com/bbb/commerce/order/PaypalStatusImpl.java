/**
 * 
 */
package com.bbb.commerce.order;

import java.util.Calendar;
import java.util.Date;
import atg.payment.PaymentStatusImpl;

/**
 * @author ssh108
 * 
 */
public class PaypalStatusImpl extends PaymentStatusImpl implements PaypalStatus {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String paypalOrder;

	private String correlationId;

	private String protectionEligibility;

	private String transId;

	private Date orderTimestamp;
	
	private Date authTimeStamp;

	/**
	 * Default constructor to be executed during LoadOrder pipeline.
	 */
	public PaypalStatusImpl() {
		this.paypalOrder = null;
		this.correlationId = null;
		this.protectionEligibility = null;
		this.transId = null;
		this.orderTimestamp = null;
		this.authTimeStamp = null;
	}

	/**
	 * Constructor to instantiate PaypalStatus
	 * 
	 * @param pTransactionId
	 * @param pTransactionSuccess
	 * @param pErrorMsg
	 * @param info
	 */
	public PaypalStatusImpl(String pTransactionId, boolean pTransactionSuccess, String pErrorMsg, PaypalBeanInfo info) {
		super(pTransactionId, info.getAmount(), pTransactionSuccess, pErrorMsg, Calendar.getInstance().getTime());
		this.paypalOrder = info.getPaypalOrder();
		this.correlationId = info.getCorrelationId();
		this.protectionEligibility = info.getProtectionEligibility();
		this.transId = info.getTransId();
		this.orderTimestamp = info.getOrderTimestamp();
		this.authTimeStamp = info.getAuthTimeStamp();
	}
	
	/**
	 * @return the authTimeStamp
	 */
	@Override
	public Date getAuthTimeStamp() {
		return this.authTimeStamp;
	}

	/**
	 * @param authTimeStamp the authTimeStamp to set
	 */
	public void setAuthTimeStamp(Date authTimeStamp) {
		this.authTimeStamp = authTimeStamp;
	}

	/**
	 * @return the paypalOrder
	 */
	@Override
	public String getPaypalOrder() {
		return this.paypalOrder;
	}

	/**
	 * @param paypalOrder
	 *            the paypalOrder to set
	 */
	public void setPaypalOrder(String paypalOrder) {
		this.paypalOrder = paypalOrder;
	}

	/**
	 * @return the correlationId
	 */
	@Override
	public String getCorrelationId() {
		return this.correlationId;
	}

	/**
	 * @param correlationId
	 *            the correlationId to set
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	/**
	 * @return the protectionEligibility
	 */
	@Override
	public String getProtectionEligibility() {
		return this.protectionEligibility;
	}

	/**
	 * @param protectionEligibility
	 *            the protectionEligibility to set
	 */
	public void setProtectionEligibility(String protectionEligibility) {
		this.protectionEligibility = protectionEligibility;
	}

	/**
	 * @return the transId
	 */
	@Override
	public String getTransId() {
		return this.transId;
	}

	/**
	 * @param transId
	 *            the transId to set
	 */
	public void setTransId(String transId) {
		this.transId = transId;
	}

	/**
	 * @return the orderTimestamp
	 */
	@Override
	public Date getOrderTimestamp() {
		return this.orderTimestamp;
	}

	/**
	 * @param orderTimestamp
	 *            the orderTimestamp to set
	 */
	public void setOrderTimestamp(Date orderTimestamp) {
		this.orderTimestamp = orderTimestamp;
	}

}
