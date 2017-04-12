package com.bbb.commerce.common;

import atg.commerce.order.Order;

import atg.core.util.Address;

import atg.payment.creditcard.GenericCreditCardInfo;

import com.bbb.commerce.common.BBBCreditCardInfo;


/**
 * This is a non-repository item backed implementation of the StoreCreditCardInfo
 * interface. This class is used to validate credit card information that is
 * entered in profile maintenance.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/payment/BasicStoreCreditCardInfo.java#2 $$Change: 633752 $
 * @updated $DateTime: 2011/02/09 11:14:12 $$Author: rbarbier $
 */
public class BasicBBBCreditCardInfo extends GenericCreditCardInfo implements BBBCreditCardInfo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** Class version string. */
	public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/payment/BasicStoreCreditCardInfo.java#2 $$Change: 633752 $";


	/**
	 * Name on Card.
	 */
	protected String mNameOnCard = null;


	/**
	 * Credit cardId.
	 */
	protected String mCreditCardId = null;


	/**
	 * Source
	 */
	protected String mSource = null;
	
	/**
	 * isDefault
	 */
	protected boolean mDefault = false;

	/**
	 * isExpired
	 */
	protected boolean mExpired = false;

	/**
	 * isDefault
	 */
	protected String mLastFourDigits = null;

	/**
	 * @return the mDefault
	 */
	public boolean isDefault() {
		return mDefault;
	}

	/**
	 * @param mDefault the mDefault to set
	 */
	public void setDefault(boolean pDefault) {
		this.mDefault = pDefault;
	}

	/**
	 * Credit card number.
	 */
	protected String mCreditCardNumber = null;

	/**
	 * Expiration month.
	 */
	protected String mExpirationMonth = null;

	/**
	 * Expiration day of month.
	 */
	protected String mExpirationDayOfMonth = null;

	/**
	 * Expiration year.
	 */
	protected String mExpirationYear = null;

	/**
	 * Credit card type.
	 */
	protected String mCreditCardType = null;

	/**
	 * Amount.
	 */
	protected double mAmount = 0;

	/**
	 * Paymant id.
	 */
	protected String mPaymentId = null;

	/**
	 * Currency code.
	 */
	protected String mCurrencyCode = null;

	/**
	 * Billing address.
	 */
	protected Address mBillingAddress = null;

	/**
	 * Card verification number.
	 */
	protected String mCardVerificationNumber = null;

	/**
	 * @return the mNameOnCard
	 */
	public String getNameOnCard() {
		return mNameOnCard;
	}

	/**
	 * @param mNameOnCard the mNameOnCard to set
	 */
	public void setNameOnCard(String pNameOnCard) {
		this.mNameOnCard = pNameOnCard;
	}

	/**
	 * @return the mCreditCardId
	 */
	public String getCreditCardId() {
		return mCreditCardId;
	}

	/**
	 * @param mCreditCardId the mCreditCardId to set
	 */
	public void setCreditCardId(String pCreditCardId) {
		this.mCreditCardId = pCreditCardId;
	}

	/**
	 * @return the mSource
	 */
	public String getSource() {
		return mSource;
	}

	/**
	 * @param mSource the mSource to set
	 */
	public void setSource(String pSource) {
		this.mSource = pSource;
	}
	
	
	
	/**
	 * @return the credit card number.
	 */
	public String getCreditCardNumber() {
		return mCreditCardNumber;
	}

	/**
	 * @param pCreditCardNumber - the credit card number to set.
	 */
	public void setCreditCardNumber(String pCreditCardNumber) {
		mCreditCardNumber = pCreditCardNumber;
	}

	/**
	 * @return the credit card type.
	 */
	public String getCreditCardType() {
		return mCreditCardType;
	}

	/**
	 * @param pCreditCardType - the credit card type to set.
	 */
	public void setCreditCardType(String pCreditCardType) {
		mCreditCardType = pCreditCardType;
	}

	/**
	 * @return the expiration month.
	 */
	public String getExpirationMonth() {
		return mExpirationMonth;
	}

	/**
	 * @param pExpirationMonth - the expiration month to set.
	 */
	public void setExpirationMonth(String pExpirationMonth) {
		mExpirationMonth = pExpirationMonth;
	}

	/**
	 * @return the expiration year.
	 */
	public String getExpirationYear() {
		return mExpirationYear;
	}

	/**
	 * @param pExpirationYear - the expiration year to set.
	 */
	public void setExpirationYear(String pExpirationYear) {
		mExpirationYear = pExpirationYear;
	}

	/**
	 * @return order.
	 */
	public Order getOrder() {
		return null;
	}

	/**
	 * @return the card verification number.
	 */
	public String getCardVerificationNumber() {
		return mCardVerificationNumber;
	}

	/**
	 * @param pCardVerificationNumber - the card verification number to set.
	 */
	public void setCardVerificationNumber(String pCardVerificationNumber) {
		mCardVerificationNumber = pCardVerificationNumber;
	}

	/**
	 * @return the amount.
	 */
	public double getAmount() {
		return mAmount;
	}

	/**
	 * @param pAmount - the amount to set.
	 */
	public void setAmount(double pAmount) {
		mAmount = pAmount;
	}

	/**
	 * @return the expiration day of month.
	 */
	public String getExpirationDayOfMonth() {
		return mExpirationDayOfMonth;
	}

	/**
	 * @param pExpirationDayOfMonth - the expiration day of month to set.
	 */
	public void setExpirationDayOfMonth(String pExpirationDayOfMonth) {
		mExpirationDayOfMonth = pExpirationDayOfMonth;
	}

	/**
	 * @return the payment id.
	 */
	public String getPaymentId() {
		return mPaymentId;
	}

	/**
	 * @param pPaymentId - the payment id to set.
	 */
	public void setPaymentId(String pPaymentId) {
		mPaymentId = pPaymentId;
	}

	/**
	 * @return the currency code.
	 */
	public String getCurrencyCode() {
		return mCurrencyCode;
	}

	/**
	 * @param pCurrencyCode - the currency code to set.
	 */
	public void setCurrencyCode(String pCurrencyCode) {
		mCurrencyCode = pCurrencyCode;
	}

	/**
	 * @return the billing address.
	 */
	public Address getBillingAddress() {
		return mBillingAddress;
	}

	/**
	 * @param pBillingAddress - the billing address to set.
	 */
	public void setBillingAddress(Address pBillingAddress) {
		mBillingAddress = pBillingAddress;
	}
	
	/**
	 * @return the billing address.
	 */
	public Boolean getExpired() {
		return mExpired;
	}

	/**
	 * @param pBillingAddress - the billing address to set.
	 */
	public void setExpired(boolean pExpired) {
		mExpired = pExpired;
	}
	
	/**
	 * @return the billing address.
	 */
	public String getLastFourDigits() {
		return mLastFourDigits;
	}

	/**
	 * @param pBillingAddress - the billing address to set.
	 */
	public void setLastFourDigits(String pLastFourDigits) {
		mLastFourDigits = pLastFourDigits;
	}
}
