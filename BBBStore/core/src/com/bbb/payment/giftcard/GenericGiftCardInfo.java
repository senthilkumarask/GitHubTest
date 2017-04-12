package com.bbb.payment.giftcard;

import java.io.Serializable;

/** @author sjatas */
public class GenericGiftCardInfo implements Serializable {

    private static final long serialVersionUID = -6276879744652749796L;

    private String giftCardNumber;
    private String pin;
    private String orderId;
    private String paymentGroupId;
    private String siteId;

    private double balance;
    private double amount;

    /** Default Constructor.*/
    public GenericGiftCardInfo() {
    	//default constructor
    }

    /** @return Amount */
    public final double getAmount() {
        return this.amount;
    }

    /** @param amount Amount */
    public final void setAmount(final double amount) {
        this.amount = amount;
    }

    /** @return Gift Card Number */
    public final String getGiftCardNumber() {
        return this.giftCardNumber;
    }

    /** @param giftCardNumber */
    public final void setGiftCardNumber(final String giftCardNumber) {
        this.giftCardNumber = giftCardNumber;
    }

    /** @return PIN */
    public final String getPin() {
        return this.pin;
    }

    /** @param pin PIN */
    public final void setPin(final String pin) {
        this.pin = pin;
    }

    /** @return Balance */
    public final double getBalance() {
        return this.balance;
    }

    /** @param balance */
    public final void setBalance(final double balance) {
        this.balance = balance;
    }

    /** @return the mOrderID */
    public final String getOrderID() {
        return this.orderId;
    }

    /** @param pOrderID the mOrderID to set */
    public final void setOrderID(final String pOrderID) {
        this.orderId = pOrderID;
    }

    /** @return the mPaymentGroupId */
    public final String getPaymentGroupId() {
        return this.paymentGroupId;
    }

    /** @param pPaymentGroupId the mPaymentGroupId to set */
    public final void setPaymentGroupId(final String pPaymentGroupId) {
        this.paymentGroupId = pPaymentGroupId;
    }

    /** @return the mSite */
    public final String getSiteId() {
        return this.siteId;
    }

    /** @param siteId the mSite to set */
    public final void setSiteId(final String siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("GenericGiftCardInfo [giftCardNumber=").append(this.giftCardNumber).append(", pin=")
        .append(this.pin).append(", orderId=").append(this.orderId).append(", paymentGroupId=")
        .append(this.paymentGroupId).append(", siteId=").append(this.siteId).append(", balance=")
        .append(this.balance).append(", amount=").append(this.amount).append("]");
        return builder.toString();
    }
}
