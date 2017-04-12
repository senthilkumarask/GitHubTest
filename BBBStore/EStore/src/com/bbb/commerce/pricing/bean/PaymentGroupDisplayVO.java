package com.bbb.commerce.pricing.bean;

import java.io.Serializable;

import com.bbb.commerce.common.BasicBBBCreditCardInfo;

public class PaymentGroupDisplayVO implements Serializable {
    
     

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double amountCredited;
	private String state;
	private double amountAuthorized;	
	private double amount;
	private String id;
	private double balanceAmtGiftCard;
	private double amountDebited;
	private String currencyCode;
	private String paymentMethodType;
	private String giftCardNumber;
	private String payerEmail;
	
	private BasicBBBCreditCardInfo creditCardInfo;
	private double remainingBalance;
	
	public double getRemainingBalance() {
		return remainingBalance;
	}

	public void setRemainingBalance(double remainingBalance) {
		this.remainingBalance = remainingBalance;
	}
	
	public double getAmountCredited() {
		return amountCredited;
	}

	public void setAmountCredited(double amountCredited) {
		this.amountCredited = amountCredited;
	}

	public double getAmountAuthorized() {
		return amountAuthorized;
	}

	public void setAmountAuthorized(double amountAuthorized) {
		this.amountAuthorized = amountAuthorized;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getBalanceAmtGiftCard() {
		return balanceAmtGiftCard;
	}

	public void setBalanceAmtGiftCard(double balanceAmtGiftCard) {
		this.balanceAmtGiftCard = balanceAmtGiftCard;
	}

	public double getAmountDebited() {
		return amountDebited;
	}

	public void setAmountDebited(double amountDebited) {
		this.amountDebited = amountDebited;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPaymentMethodType() {
		return paymentMethodType;
	}

	public void setPaymentMethodType(String paymentMethodType) {
		this.paymentMethodType = paymentMethodType;
	}

	public BasicBBBCreditCardInfo getCreditCardInfo() {
		return creditCardInfo;
	}

	public void setCreditCardInfo(BasicBBBCreditCardInfo creditCardInfo) {
		this.creditCardInfo = creditCardInfo;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public String getGiftCardNumber() {
		return giftCardNumber;
	}

	public void setGiftCardNumber(String giftCardNumber) {
		this.giftCardNumber = giftCardNumber;
	}

	/**
	 * @return the payerEmail
	 */
	public String getPayerEmail() {
		return payerEmail;
	}

	/**
	 * @param payerEmail the payerEmail to set
	 */
	public void setPayerEmail(String payerEmail) {
		this.payerEmail = payerEmail;
	}
    
}

