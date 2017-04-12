package com.bbb.commerce.order;

import com.bbb.constants.TBSConstants;

import atg.commerce.order.PaymentGroupImpl;

public class PayAtRegister extends PaymentGroupImpl {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return
	 */
	public String getPayerStatus() {
		return (String) getPropertyValue(TBSConstants.PAYER_STATUS);
	}

	/**
	 * @param payerStatus
	 */
	public void setPayerStatus(String payerStatus) {
		setPropertyValue(TBSConstants.PAYER_STATUS, payerStatus);
	}
}
