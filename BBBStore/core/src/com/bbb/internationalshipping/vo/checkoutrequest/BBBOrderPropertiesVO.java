package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 * This class gives you the information
 * about Order Properties VO.
 * @version 1.0
 */
public class BBBOrderPropertiesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to currencyQuoteId.
	 */
	private long currencyQuoteId;
	/**
	 * This variable is used to point to lcpRuleId.
	 */
	private String lcpRuleId;
	/**
	 * This variable is used to point to merchantOrderId.
	 */
	private String merchantOrderId;
	/**
	 * This variable is used to point to merchantOrderRef.
	 */
	private String merchantOrderRef;	
	/**
	 * @return the currencyQuoteId
	 */
	public final long getCurrencyQuoteId() {
		return currencyQuoteId;
	}
	/**
	 * @param currencyQuoteId the currencyQuoteId to set
	 */
	public final void setCurrencyQuoteId(final long currencyQuoteId) {
		this.currencyQuoteId = currencyQuoteId;
	}
	/**
	 * @return the lcpRuleId
	 */
	public final String getLcpRuleId() {
		return lcpRuleId;
	}
	/**
	 * @param lcpRuleId the lcpRuleId to set
	 */
	public final void setLcpRuleId(final String lcpRuleId) {
		this.lcpRuleId = lcpRuleId;
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
	 * @return the merchantOrderRef
	 */
	public String getMerchantOrderRef() {
		return merchantOrderRef;
	}
	/**
	 * @param merchantOrderRef the merchantOrderRef to set
	 */
	public void setMerchantOrderRef(String merchantOrderRef) {
		this.merchantOrderRef = merchantOrderRef;
	}
	
}
