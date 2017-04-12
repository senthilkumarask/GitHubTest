package com.bbb.integration.cybersource.creditcard;

import atg.integrations.cybersourcesoap.cc.CreditCardProcParams;

public class CyberSourceVO {

	private String amount;
	private CreditCardProcParams creditCardParams;

	/**
	 * @return the creditCardParams
	 */
	public CreditCardProcParams getCreditCardParams() {
		return creditCardParams;
	}

	/**
	 * @param creditCardParams the creditCardParams to set
	 */
	public void setCreditCardParams(CreditCardProcParams creditCardParams) {
		this.creditCardParams = creditCardParams;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
