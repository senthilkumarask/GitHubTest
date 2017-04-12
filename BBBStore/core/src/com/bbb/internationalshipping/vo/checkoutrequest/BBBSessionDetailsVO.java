package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 * This class gives you the information
 * about Session Details VO.
 * @version 1.0
 */
public class BBBSessionDetailsVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to buyerSessionId.
	 */
	private String buyerSessionId;
	/**
	 * This variable is used to point to buyerIpAddress.
	 */
	private String buyerIpAddress;
	/**
	 * This variable is used to point to buyerCurrency.
	 */
	private String buyerCurrency;
	/**
	 * This variable is used to point to buyerCountry.
	 */
	private String buyerCountry;
	/**
	 * This variable is used to point to checkoutSuccessUrl.
	 */
	private String checkoutSuccessUrl;
	/**
	 * This variable is used to point to checkoutPendingUrl.
	 */
	private String checkoutPendingUrl;
	/**
	 * This variable is used to point to checkoutFailureUrl.
	 */
	private String checkoutFailureUrl;
	/**
	 * This variable is used to point to checkoutBasketUrl.
	 */
	private String checkoutBasketUrl;
	/**
	 * This variable is used to point to checkoutUSCartStartPageUrl.
	 */
	private String checkoutUSCartStartPageUrl;
	/**
	 * This variable is used to point to payPalReturnUrl.
	 */
	private String payPalReturnUrl;
	/**
	 * This variable is used to point to payPalCancelUrl.
	 */
	private String payPalCancelUrl;
	/**
	 * This variable is used to point to payPalHeaderLogoUrl.
	 */
	private String payPalHeaderLogoUrl;
	/**
	 * @return the checkoutSuccessUrl
	 */
	public final String getCheckoutSuccessUrl() {
		return checkoutSuccessUrl;
	}
	/**
	 * @param checkoutSuccessUrl the checkoutSuccessUrl to set
	 */
	public final void setCheckoutSuccessUrl(final String checkoutSuccessUrl) {
		this.checkoutSuccessUrl = checkoutSuccessUrl;
	}
	/**
	 * @return the checkoutPendingUrl
	 */
	public final String getCheckoutPendingUrl() {
		return checkoutPendingUrl;
	}
	/**
	 * @param checkoutPendingUrl the checkoutPendingUrl to set
	 */
	public final void setCheckoutPendingUrl(final String checkoutPendingUrl) {
		this.checkoutPendingUrl = checkoutPendingUrl;
	}
	/**
	 * @return the checkoutFailureUrl
	 */
	public final String getCheckoutFailureUrl() {
		return checkoutFailureUrl;
	}
	/**
	 * @param checkoutFailureUrl the checkoutFailureUrl to set
	 */
	public final void setCheckoutFailureUrl(final String checkoutFailureUrl) {
		this.checkoutFailureUrl = checkoutFailureUrl;
	}
	/**
	 * @return the checkoutBasketUrl
	 */
	public final String getCheckoutBasketUrl() {
		return checkoutBasketUrl;
	}
	/**
	 * @param checkoutBasketUrl the checkoutBasketUrl to set
	 */
	public final void setCheckoutBasketUrl(final String checkoutBasketUrl) {
		this.checkoutBasketUrl = checkoutBasketUrl;
	}
	/**
	 * @return the checkoutUSCartStartPageUrl
	 */
	public final String getCheckoutUSCartStartPageUrl() {
		return checkoutUSCartStartPageUrl;
	}
	/**
	 * @param checkoutUSCartStartPageUrl the checkoutUSCartStartPageUrl to set
	 */
	public final void setCheckoutUSCartStartPageUrl(final 
			String checkoutUSCartStartPageUrl) {
		this.checkoutUSCartStartPageUrl = checkoutUSCartStartPageUrl;
	}
	/**
	 * @return the payPalReturnUrl
	 */
	public final String getPayPalReturnUrl() {
		return payPalReturnUrl;
	}
	/**
	 * @param payPalReturnUrl the payPalReturnUrl to set
	 */
	public final void setPayPalReturnUrl(final String payPalReturnUrl) {
		this.payPalReturnUrl = payPalReturnUrl;
	}
	/**
	 * @return the payPalCancelUrl
	 */
	public final String getPayPalCancelUrl() {
		return payPalCancelUrl;
	}
	/**
	 * @param payPalCancelUrl the payPalCancelUrl to set
	 */
	public final void setPayPalCancelUrl(final String payPalCancelUrl) {
		this.payPalCancelUrl = payPalCancelUrl;
	}
	/**
	 * @return the payPalHeaderLogoUrl
	 */
	public final String getPayPalHeaderLogoUrl() {
		return payPalHeaderLogoUrl;
	}
	/**
	 * @param payPalHeaderLogoUrl the payPalHeaderLogoUrl to set
	 */
	public final void setPayPalHeaderLogoUrl(final String payPalHeaderLogoUrl) {
		this.payPalHeaderLogoUrl = payPalHeaderLogoUrl;
	}
	/**
	 * @return the buyerCurrency
	 */
	public final String getBuyerCurrency() {
		return buyerCurrency;
	}
	/**
	 * @param buyerCurrency the buyerCurrency to set
	 */
	public final void setBuyerCurrency(final String buyerCurrency) {
		this.buyerCurrency = buyerCurrency;
	}
	/**
	 * @return the buyerCountry
	 */
	public final String getBuyerCountry() {
		return buyerCountry;
	}
	/**
	 * @param buyerCountry the buyerCountry to set
	 */
	public final void setBuyerCountry(final String buyerCountry) {
		this.buyerCountry = buyerCountry;
	}
	/**
	 * @return the buyerSessionId
	 */
	public final String getBuyerSessionId() {
		return buyerSessionId;
	}
	/**
	 * @param buyerSessionId the buyerSessionId to set
	 */
	public final void setBuyerSessionId(final String buyerSessionId) {
		this.buyerSessionId = buyerSessionId;
	}
	/**
	 * @return the buyerIpAddress
	 */
	public final String getBuyerIpAddress() {
		return buyerIpAddress;
	}
	/**
	 * @param buyerIpAddress the buyerIpAddress to set
	 */
	public final void setBuyerIpAddress(final String buyerIpAddress) {
		this.buyerIpAddress = buyerIpAddress;
	}

}
