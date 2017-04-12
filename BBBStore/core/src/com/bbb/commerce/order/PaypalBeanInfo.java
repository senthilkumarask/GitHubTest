/**
 * Paypal.java
 *
 * Project:     BBB
 */

package com.bbb.commerce.order;

import java.io.Serializable;
import java.util.Date;

import com.bbb.ecommerce.order.BBBOrderImpl;

/**
 * @author ssh108
 * 
 */
public class PaypalBeanInfo implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBOrderImpl order;
	private String mPaymentGroupId;
	private String mSiteID;
	private Double mAmount;
	private String paypalOrder;
	private String correlationId;
	private String protectionEligibility;
	private String transId;
	private Date orderTimestamp;
	private Date authTimeStamp;
	
	
	
	
	/**
	 * @return the authTimeStamp
	 */
	public Date getAuthTimeStamp() {
		return authTimeStamp;
	}

	/**
	 * @param authTimeStamp the authTimeStamp to set
	 */
	public void setAuthTimeStamp(Date authTimeStamp) {
		this.authTimeStamp = authTimeStamp;
	}

	/**
	 * @return the order
	 */
	public BBBOrderImpl getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(BBBOrderImpl order) {
		this.order = order;
	}
	
	/**
	 * @return the paypalOrder
	 */
	public String getPaypalOrder() {
		return paypalOrder;
	}

	/**
	 * @param paypalOrder the paypalOrder to set
	 */
	public void setPaypalOrder(String paypalOrder) {
		this.paypalOrder = paypalOrder;
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
	 * @return the transId
	 */
	public String getTransId() {
		return transId;
	}

	/**
	 * @param transId the transId to set
	 */
	public void setTransId(String transId) {
		this.transId = transId;
	}

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
	 * getter for amount
	 */
	public Double getAmount() {
		return mAmount;
	}

	/**
	 * setter for amount
	 * 
	 * @param pAmount
	 */
	public void setAmount(Double pAmount) {
		mAmount = pAmount;
	}

	
	/**
	 * @return the mPaymentGroupId
	 */
	public String getPaymentGroupId() {
		return mPaymentGroupId;
	}

	/**
	 * @param pPaymentGroupId
	 *            the mPaymentGroupId to set
	 */
	public void setPaymentGroupId(String pPaymentGroupId) {
		mPaymentGroupId = pPaymentGroupId;
	}

	/**
	 * @return the mSite
	 */
	public String getSiteID() {
		return mSiteID;
	}

	/**
	 * @param pSite
	 *           
	 */
	public void setSiteID(String pSiteID) {
		mSiteID = pSiteID;
	}
}
