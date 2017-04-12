package com.bbb.internationalshipping.vo.pofileprocessing;

import java.io.Serializable;


public class BBBInternationalOrderPOFileVO implements Serializable

{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to billingIntlAddress.
	 */
	//private BBBIntlAddressVO billingIntlAddress;	
	private String merchantOrderId;
	private String fraudState;
	private boolean isRetry;
	
	
	

	public boolean isRetry() {
		return isRetry;
	}


	public void setRetry(boolean isRetry) {
		this.isRetry = isRetry;
	}


	public String getFraudState() {
		return fraudState;
	}


	public void setFraudState(String fraudState) {
		this.fraudState = fraudState;
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
	public final void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	
	/**
	 * @return the billingIntlAddress
	 */
	/*public BBBIntlAddressVO getBillingIntlAddress() {
		return billingIntlAddress;
	}*/


	/**
	 * @param billingIntlAddress the billingIntlAddress to set
	 */
	/*public void setBillingIntlAddress(BBBIntlAddressVO billingIntlAddress) {
		this.billingIntlAddress = billingIntlAddress;
	}*/




	/**
	 * This variable is used to point to bbbIntlCreditCardVO.
	 */
	//private BBBIntlCreditCardVO bbbIntlCreditCardVO	;

	/**
	 * Order Id - orderId
	 */
	private String orderId;
	
	/**
	 * Exchange order id - e4xOrderId
	 */
	private String e4xOrderId;

	
	
	/**
	 * @return the bbbIntlCreditCardVO
	 */
	/*public BBBIntlCreditCardVO getBbbIntlCreditCardVO() {
		return bbbIntlCreditCardVO;
	}*/


	/**
	 * @param bbbIntlCreditCardVO the bbbIntlCreditCardVO to set
	 */
	/*public void setBbbIntlCreditCardVO(BBBIntlCreditCardVO bbbIntlCreditCardVO) {
		this.bbbIntlCreditCardVO = bbbIntlCreditCardVO;
	}
	*/

	/**
	 * @return the orderId
	 */
	public final String getOrderId() {
		return orderId;
	}


	/**
	 * @param orderId the orderId to set
	 */
	public final void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	/**
	 * @return the e4xOrderId
	 */
	public final String getE4xOrderId() {
		return e4xOrderId;
	}


	/**
	 * @param e4xOrderId the e4xOrderId to set
	 */
	public final void setE4xOrderId(String e4xOrderId) {
		this.e4xOrderId = e4xOrderId;
	}
}

