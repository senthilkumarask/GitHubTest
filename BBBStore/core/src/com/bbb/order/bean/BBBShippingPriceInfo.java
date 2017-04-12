package com.bbb.order.bean;

import atg.commerce.pricing.ShippingPriceInfo;

/** This class is extended to add additional ShippingPriceInfo properties.
 *
 * @author Sunil */
public class BBBShippingPriceInfo extends ShippingPriceInfo {

    /** Constant for serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Constant for rawSurcharge. */
    private double surcharge;

    /** Constant for finalSurcharge. */
    private double finalSurcharge;

    /** Constant for finalShipping. */
    private double finalShipping;

    /** Property for ecoFee. */
    private double ecoFee;

    /** @return the surcharge */
    public double getSurcharge() {
        return this.surcharge;
    }

    /** @param surcharge the surcharge to set */
    public void setSurcharge(final double surcharge) {
        this.surcharge = surcharge;
    }

    /** @return the finalSurcharge */
    public double getFinalSurcharge() {
        return this.finalSurcharge;
    }

    /** @param finalSurcharge the finalSurcharge to set */
    public void setFinalSurcharge(final double finalSurcharge) {
        this.finalSurcharge = finalSurcharge;
    }

    /** @return the finalShipping */
    public double getFinalShipping() {
        return this.finalShipping;
    }

    /** @param finalShipping the finalShipping to set */
    public void setFinalShipping(final double finalShipping) {
        this.finalShipping = finalShipping;
    }

    /** @return Eco Fee*/
    public double getEcoFee() {
        return this.ecoFee;
    }

    /** @param ecoFee */
    public void setEcoFee(final double ecoFee) {
        this.ecoFee = ecoFee;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBShippingPriceInfo [surcharge=").append(this.surcharge).append(", finalSurcharge=")
                        .append(this.finalSurcharge).append(", finalShipping=").append(this.finalShipping)
                        .append(", ecoFee=").append(this.ecoFee).append(", getRawShipping()=")
                        .append(this.getRawShipping()).append(", getCurrencyCode()=").append(this.getCurrencyCode())
                        .append(", getAmount()=").append(this.getAmount()).append(", isDiscounted()=")
                        .append(this.isDiscounted()).append(", getAdjustments()=").append(this.getAdjustments())
                        .append(", isAmountIsFinal()=").append(this.isAmountIsFinal())
                        .append(", getClosenessQualifiers()=").append(this.getClosenessQualifiers())
                        .append(", getFinalReasonCode()=").append(this.getFinalReasonCode()).append("]");
        return builder.toString();
    }
}
