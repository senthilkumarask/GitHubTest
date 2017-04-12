package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * GiftCard.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */

public class GiftCard  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double balance;

    private double debitAmt;

    private String cardNum;

    private String pin;

    private String responseCode;

    private String responseComment;

	/**
	 * @return the balance
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	/**
	 * @return the debitAmt
	 */
	public double getDebitAmt() {
		return debitAmt;
	}

	/**
	 * @param debitAmt the debitAmt to set
	 */
	public void setDebitAmt(double debitAmt) {
		this.debitAmt = debitAmt;
	}

	/**
	 * @return the cardNum
	 */
	public String getCardNum() {
		return cardNum;
	}

	/**
	 * @param cardNum the cardNum to set
	 */
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	/**
	 * @return the pin
	 */
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the responseComment
	 */
	public String getResponseComment() {
		return responseComment;
	}

	/**
	 * @param responseComment the responseComment to set
	 */
	public void setResponseComment(String responseComment) {
		this.responseComment = responseComment;
	}

}
