package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

import atg.commerce.order.Order;

/**
 * This class gives you the information
 * about International Checkout Input VO.
 * @version 1.0
 */
public class BBBInternationalCheckoutInputVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to merchantOrderId.
	 */
	private String merchantOrderId;
	/**
	 * This variable is used to point to order.
	 */
	private Order order;
	/**
	 * This variable is used to point to countryCode.
	 */
	private String countryCode;
	/**
	 * This variable is used to point to currencyCode.
	 */
	private String currencyCode;
	/**
	 * This variable is used to point to userIPAddress.
	 */
	private String userIPAddress;
	/**
	 * This variable is used to point to userSessionId.
	 */
	private String userSessionId;
	
	/**
	 * This variable is used to point to merchantId.
	 */
	private String merchantId;
	/**
	 * This variable is used to point to siteId.
	 */
	private String siteId;
	/**
	 * This variable is used to point to shippingAmount
	 */
	private double shippingAmount;
	/**
	 * @return the shippingAmount
	 */
	private String channel;
	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}
	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public final double getShippingAmount() {
		return shippingAmount;
	}
	/**
	 * @param shippingAmount the shippingAmount to set
	 */
	public final void setShippingAmount(double shippingAmount) {
		this.shippingAmount = shippingAmount;
	}
	/**
	 * @return the siteId
	 */
	public final String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public final void setSiteId(final String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the merchantId
	 */
	public final String getMerchantId() {
		return merchantId;
	}
	/**
	 * @param merchantId the merchantId to set
	 */
	public final void setMerchantId(final String merchantId) {
		this.merchantId = merchantId;
	}
	/**
	 * @return the userSessionId
	 */
	public final String getUserSessionId() {
		return userSessionId;
	}
	/**
	 * @param userSessionId the userSessionId to set
	 */
	public final void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}
	/**
	 * @return the userIPAddress
	 */
	public final String getUserIPAddress() {
		return userIPAddress;
	}
	/**
	 * @param userIPAddress the userIPAddress to set
	 */
	public final void setUserIPAddress(final String userIPAddress) {
		this.userIPAddress = userIPAddress;
	}
	/**
	 * @return the merchantOrderId
	 */
	public final String getMerchantOrderId() {
		return merchantOrderId;
	}
	/**
	 * @param merchantOrderId the merchantOrderId to set
	 */
	public final void setMerchantOrderId(final String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}
	/**
	 * @return the order
	 */
	public final Order getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public final void setOrder(final Order order) {
		this.order = order;
	}
	/**
	 * @return the countryCode
	 */
	public final String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public final void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}
	/**
	 * @return the currencyCode
	 */
	public final String getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public final void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}
}
