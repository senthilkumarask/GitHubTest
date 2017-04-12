package com.bbb.commerce.order;

import atg.commerce.order.PaymentGroupImpl;

import com.bbb.payment.giftcard.BBBGiftCardInfo;

/** Bean class for the GiftCard.
 *
 * @author vagra4 */
public class BBBGiftCard extends PaymentGroupImpl implements BBBGiftCardInfo {

    private static final String PIN = "pin";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String BALANCE = "balance";
    private static final long serialVersionUID = -661419719101321901L;

    /** Default Constructor. */
    public BBBGiftCard() {
    	//default constructor
    }

    /** @return cardNumber */
    @Override
    public final String getCardNumber() {
        return (String) this.getPropertyValue(CARD_NUMBER);
    }

    /** @param pCardNumber cardNumber */
    public final void setCardNumber(final String pCardNumber) {
        this.setPropertyValue(CARD_NUMBER, pCardNumber);
    }

    /** @return PIN */
    @Override
    public final String getPin() {
        return (String) this.getPropertyValue(PIN);
    }

    /** @param pPin Pin */
    public final void setPin(final String pPin) {
        this.setPropertyValue(PIN, pPin);
    }

    /** @return Balance */
    @Override
    public final Double getBalance() {
        return (Double) this.getPropertyValue(BALANCE);
    }

    /** @param pBalance Balance */
    public final void setBalance(final Double pBalance) {
        this.setPropertyValue(BALANCE, pBalance);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBGiftCard [getCardNumber()=").append(this.getCardNumber()).append(", getPin()=")
                        .append(this.getPin()).append(", getBalance()=").append(this.getBalance())
                        .append(", getPaymentGroupClassType()=").append(this.getPaymentGroupClassType())
                        .append(", getPaymentMethod()=").append(this.getPaymentMethod()).append(", getAmount()=")
                        .append(this.getAmount()).append(", getAmountAuthorized()=").append(this.getAmountAuthorized())
                        .append(", getAmountDebited()=").append(this.getAmountDebited())
                        .append(", getAmountCredited()=").append(this.getAmountCredited())
                        .append(", getCurrencyCode()=").append(this.getCurrencyCode()).append(", getState()=")
                        .append(this.getState()).append(", getAuthorizationStatus()=")
                        .append(this.getAuthorizationStatus()).append(", getDebitStatus()=")
                        .append(this.getDebitStatus()).append(", getCreditStatus()=").append(this.getCreditStatus())
                        .append(", getSubmittedDate()=").append(this.getSubmittedDate())
                        .append(", getSpecialInstructions()=").append(this.getSpecialInstructions())
                        .append(", getRequisitionNumber()=").append(this.getRequisitionNumber()).append(", getId()=")
                        .append(this.getId()).append("]");
        return builder.toString();
    }
}
