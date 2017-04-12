package com.bbb.commerce.order;

import atg.payment.PaymentStatusImpl;

public class PayAtRegisterStatus extends PaymentStatusImpl {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Property to hold mStatusCode.
	 */
	private String mStatusCode;
	
	/**
	 * Property to hold mStatusMessage.
	 */
	private String mStatusMessage;

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return mStatusCode;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return mStatusMessage;
	}

	/**
	 * @param pStatusCode the statusCode to set
	 */
	public void setStatusCode(String pStatusCode) {
		mStatusCode = pStatusCode;
	}

	/**
	 * @param pStatusMessage the statusMessage to set
	 */
	public void setStatusMessage(String pStatusMessage) {
		mStatusMessage = pStatusMessage;
	}

}
