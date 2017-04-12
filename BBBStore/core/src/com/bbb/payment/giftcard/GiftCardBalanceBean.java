package com.bbb.payment.giftcard;

import java.io.Serializable;

/** This Bean class contains Gift card balance/redemption information.
 * 
 *  */
public class GiftCardBalanceBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String giftCardId;
    private String balance;
    private String errorMessage;
    private boolean error;

    /** Default Constructor.*/
    public GiftCardBalanceBean() {
    	//default constructor
    }

    /** @return Gift Card ID */
    public final String getGiftCardId() {
        return this.giftCardId;
    }

    /** @param giftCardId Gift Card ID */
    public final void setGiftCardId(final String giftCardId) {
        this.giftCardId = giftCardId;
    }

    /** @return Balance */
    public final String getBalance() {
        return this.balance;
    }

    /** @param balance Balance */
    public final void setBalance(final String balance) {
        this.balance = balance;
    }

    /** @return Error */
    public final boolean getError() {
        return this.error;
    }

    /** @param error Error */
    public final void setError(final boolean error) {
        this.error = error;
    }

    /** @return Error Message */
    public final String getErrorMessage() {
        return this.errorMessage;
    }

    /** @param errorMessage Error Message */
    public final void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
