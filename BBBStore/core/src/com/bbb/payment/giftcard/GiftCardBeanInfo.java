package com.bbb.payment.giftcard;

import java.io.Serializable;

//import com.bbb.common.BBBGenericService;

/** This Bean class contains Gift card balance/redemption information.
 * 
 * @author vagra4 */
public class GiftCardBeanInfo implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String giftCardID;
    private String balance;
    private String previousBalance;
    private String pin;
    private String traceNumber;
    private String authCode;
    private String authRespCode;
    private String cardClass;

    private boolean timeoutReversal;
    private boolean status;
    private boolean exceptionStatus;

    /** Default Constructor.*/
    public GiftCardBeanInfo() {
        super();
    }

    /** @return the mGiftCardId */
    public final String getGiftCardID() {
        return this.giftCardID;
    }

    /** @param giftCardId the mGiftCardId to set */
    public final void setGiftCardId(final String giftCardId) {
        this.giftCardID = giftCardId;
    }

    /** @return balance */
    public final String getBalance() {
        return this.balance;
    }

    /** @param balance the mBalance to set */
    public final void setBalance(final String balance) {
        this.balance = balance;
    }

    /** @return the mPin */
    public final String getPin() {
        return this.pin;
    }

    /** @param pin the mPin to set */
    public final void setPin(final String pin) {
        this.pin = pin;
    }

    /** @return the status */
    public final boolean getStatus() {
        return this.status;
    }

    /** @param status the status to set */
    public final void setStatus(final boolean status) {
        this.status = status;
    }

    /** @return the previousBalance */
    public final String getPreviousBalance() {
        return this.previousBalance;
    }

    /** @param previousBalance the previousBalance to set */
    public final void setPreviousBalance(final String previousBalance) {
        this.previousBalance = previousBalance;
    }

    /** @return the exceptionStatus */
    public final boolean getExceptionStatus() {
        return this.exceptionStatus;
    }

    /** @param exceptionStatus the exceptionStatus to set */
    public final void setExceptionStatus(final boolean exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    /** @return the authCode */
    public final String getAuthCode() {
        return this.authCode;
    }

    /** @param authCode the authCode to set */
    public final void setAuthCode(final String authCode) {
        this.authCode = authCode;
    }

    /** @return the traceNumber */
    public final String getTraceNumber() {
        return this.traceNumber;
    }

    /** @param traceNumber the mTraceNumber to set */
    public final void setTraceNumber(final String traceNumber) {
        this.traceNumber = traceNumber;
    }

    /** @return the mAuthRespCode */
    public final String getAuthRespCode() {
        return this.authRespCode;
    }

    /** @param authRespCode the mAuthRespCode to set */
    public final void setAuthRespCode(final String authRespCode) {
        this.authRespCode = authRespCode;
    }

    /** @return the mCardClass */
    public final String getCardClass() {
        return this.cardClass;
    }

    /** @param cardClass the mCardClass to set */
    public final void setCardClass(final String cardClass) {
        this.cardClass = cardClass;
    }

    /** @return the mTimeoutReversal */
    public final boolean getTimeoutReversal() {
        return this.timeoutReversal;
    }

    /** @param timeoutReversal the mTimeoutReversal to set */
    public final void setTimeoutReversal(final boolean timeoutReversal) {
        this.timeoutReversal = timeoutReversal;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("GiftCardBeanInfo [giftCardID=").append(this.giftCardID).append(", balance=")
        .append(this.balance).append(", previousBalance=").append(this.previousBalance).append(", pin=")
        .append(this.pin).append(", traceNumber=").append(this.traceNumber).append(", authCode=")
        .append(this.authCode).append(", authRespCode=").append(this.authRespCode).append(", cardClass=")
        .append(this.cardClass).append(", timeoutReversal=").append(this.timeoutReversal).append(", status=")
        .append(this.status).append(", exceptionStatus=").append(this.exceptionStatus).append("]");
        return builder.toString();
    }
}
