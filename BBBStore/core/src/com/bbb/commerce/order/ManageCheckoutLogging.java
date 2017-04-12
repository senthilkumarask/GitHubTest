package com.bbb.commerce.order;

import com.bbb.common.BBBGenericService;

/** @author sjatas */
public class ManageCheckoutLogging extends BBBGenericService {

    private boolean cartFormHandlerLogging;
    private boolean shippingFormHandlerLogging;
    private boolean paymentFormHandlerLogging;
    private boolean commitOrderFormHandlerLogging;
    private boolean billingAddressHandlerLogging;
    private boolean giftCardHandlerLogging;

    /** Default Constructor. */
    public ManageCheckoutLogging() {
        super();
    }

    /** @return the isGiftCardHandlerLoggingOn */
    public final boolean isGiftCardHandlerLogging() {
        return this.giftCardHandlerLogging;
    }

    /** @param giftCardHandlerLogging the isGiftCardHandlerLoggingOn to set */
    public final void setGiftCardHandlerLogging(final boolean giftCardHandlerLogging) {
        this.giftCardHandlerLogging = giftCardHandlerLogging;
    }

    /** @return the isBillingAddressHandlerLoggingOn */
    public final boolean isBillingAddressHandlerLogging() {
        return this.billingAddressHandlerLogging;
    }

    /** @param billingAddressHandlerLogging the isBillingAddressHandlerLoggingOn to set */
    public final void setBillingAddressHandlerLogging(final boolean billingAddressHandlerLogging) {
        this.billingAddressHandlerLogging = billingAddressHandlerLogging;
    }

    /** @return the isCommitOderFormHandlerLoggingOn */
    public final boolean isCommitOderFormHandlerLogging() {
        return this.commitOrderFormHandlerLogging;
    }

    /** @param isCommitOderFormHandlerLogging the isCommitOderFormHandlerLoggingOn to set */
    public final void setCommitOderFormHandlerLogging(final boolean isCommitOderFormHandlerLogging) {
        this.commitOrderFormHandlerLogging = isCommitOderFormHandlerLogging;
    }

    /** @return the isShippingFormHandlerLoggingOn */
    public final boolean isShippingFormHandlerLogging() {
        return this.shippingFormHandlerLogging;
    }

    /** @param shippingFormHandlerLogging the isShippingFormHandlerLoggingOn to set */
    public final void setShippingFormHandlerLogging(final boolean shippingFormHandlerLogging) {
        this.shippingFormHandlerLogging = shippingFormHandlerLogging;
    }

    /** @return the isPaymentFormHandlerLoggingOn */
    public final boolean isPaymentFormHandlerLogging() {
        return this.paymentFormHandlerLogging;
    }

    /** @param paymentFormHandlerLogging the isPaymentFormHandlerLoggingOn to set */
    public final void setPaymentFormHandlerLogging(final boolean paymentFormHandlerLogging) {
        this.paymentFormHandlerLogging = paymentFormHandlerLogging;
    }

    /** @return the isCartFormHandlerLoggingOn */
    public final boolean isCartFormHandlerLogging() {
        return this.cartFormHandlerLogging;
    }

    /** @param cartFormHandlerLogging the isCartFormHandlerLoggingOn to set */
    public final void setCartFormHandlerLogging(final boolean cartFormHandlerLogging) {
        this.cartFormHandlerLogging = cartFormHandlerLogging;
    }
}
