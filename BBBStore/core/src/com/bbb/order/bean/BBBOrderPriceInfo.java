package com.bbb.order.bean;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.pricing.OrderPriceInfo;

/** This class is used by the OrderPriceInfo.
 *
 * @author Sunil Dandriyal */
public class BBBOrderPriceInfo extends OrderPriceInfo {

    private static final long serialVersionUID = 1L;

    private double onlineSubtotal;
    private double giftWrapSubtotal;
    private double storeSubtotal;
    private double ecoFee;

    private int dollarOffPromoCount;

    private boolean restoreAdjustments;

    /**
     * total of delivery surchage 
     */
    private double deliverySurchargeTotal;
    
    private double deliverySurchargeRawTotal;
    
    /**
     * total of Assembly Fee
     */
    private double AssemblyFeeTotal;
    
    private Map<String, Map<String, Double>> appliedPromotionThresholdMap;
    
    /**
	 * @return the deliverySurchargeTotal
	 */
	public double getDeliverySurchargeTotal() {
		return deliverySurchargeTotal;
	}

	/**
	 * @param deliverySurchargeTotal the deliverySurchargeTotal to set
	 */
	public void setDeliverySurchargeTotal(double deliverySurchargeTotal) {
		this.deliverySurchargeTotal = deliverySurchargeTotal;
	}

	/**
	 * @return the assemblyFeeTotal
	 */
	public double getAssemblyFeeTotal() {
		return AssemblyFeeTotal;
	}

	/**
	 * @param assemblyFeeTotal the assemblyFeeTotal to set
	 */
	public void setAssemblyFeeTotal(double assemblyFeeTotal) {
		AssemblyFeeTotal = assemblyFeeTotal;
	}

    /** @return Dollar Off Promotion Count*/
    public final int getDollarOffPromoCount() {
        return this.dollarOffPromoCount;
    }

    /** @param mDollarOffPromoCount */
    public final void setDollarOffPromoCount(final int mDollarOffPromoCount) {
        this.dollarOffPromoCount = mDollarOffPromoCount;
    }

    /** Default Constructor.*/
    public BBBOrderPriceInfo() {
        super();
    }

    /** @return the onlineSubtotal */

    public final double getOnlineSubtotal() {
        return this.onlineSubtotal;
    }

    /** @param onlineSubtotal the onlineSubtotal to set */

    public final void setOnlineSubtotal(final double onlineSubtotal) {
        this.onlineSubtotal = onlineSubtotal;
    }

    /** @return the storeSubtotal */
    public final double getStoreSubtotal() {
        return this.storeSubtotal;
    }

    /** @param storeSubtotal the storeSubtotal to set */
    public final void setStoreSubtotal(final double storeSubtotal) {
        this.storeSubtotal = storeSubtotal;
    }

    /** @return The grand total i.e. (online total + store item total) */
    public final double getOnlineStoreTotal() {
        return this.getTotal() + this.getStoreSubtotal();
    }

    /** @return Eco Fee*/
    public final double getEcoFee() {
        return this.ecoFee;
    }

    /** @param ecoFee */
    public final void setEcoFee(final double ecoFee) {
        this.ecoFee = ecoFee;
    }

    /** @return Gift Wrap Subtotal*/
    public final double getGiftWrapSubtotal() {
        return this.giftWrapSubtotal;
    }

    /** @param giftWrapSubtotal */
    public final void setGiftWrapSubtotal(final double giftWrapSubtotal) {
        this.giftWrapSubtotal = giftWrapSubtotal;
    }

    /** @return Restore Adjustments*/
    public final boolean isRestoreAdjustments() {
        return this.restoreAdjustments;
    }

    /** @param pRestoreAdjustments */
    public final void setRestoreAdjustments(final boolean pRestoreAdjustments) {
        this.restoreAdjustments = pRestoreAdjustments;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBOrderPriceInfo [onlineSubtotal=").append(this.onlineSubtotal)
                        .append(", appliedPromotionThresholdMap=").append(this.appliedPromotionThresholdMap)
                        .append(", giftWrapSubtotal=").append(this.giftWrapSubtotal).append(", storeSubtotal=")
                        .append(this.storeSubtotal).append(", ecoFee=").append(this.ecoFee)
                        .append(", dollarOffPromoCount=").append(this.dollarOffPromoCount)
                        .append(", restoreAdjustments=").append(this.restoreAdjustments)
                        .append(", getAppliedPromotionThresholdMap()=").append(this.getAppliedPromotionThresholdMap())
                        .append(", getDollarOffPromoCount()=").append(this.getDollarOffPromoCount())
                        .append(", getOnlineSubtotal()=").append(this.getOnlineSubtotal())
                        .append(", getStoreSubtotal()=").append(this.getStoreSubtotal())
                        .append(", getOnlineStoreTotal()=").append(this.getOnlineStoreTotal()).append(", getEcoFee()=")
                        .append(this.getEcoFee()).append(", getGiftWrapSubtotal()=").append(this.getGiftWrapSubtotal())
                        .append(", isRestoreAdjustments()=").append(this.isRestoreAdjustments()).append("]");
        return builder.toString();
    }

	/**
	 * @return the deliverySurchargeRawTotal
	 */
	public double getDeliverySurchargeRawTotal() {
		return deliverySurchargeRawTotal;
}

	/**
	 * @param deliverySurchargeRawTotal the deliverySurchargeRawTotal to set
	 */
	public void setDeliverySurchargeRawTotal(double deliverySurchargeRawTotal) {
		this.deliverySurchargeRawTotal = deliverySurchargeRawTotal;
	}

	/**
	 * @return the appliedPromotionThresholdMap
	 */
	public Map<String, Map<String, Double>> getAppliedPromotionThresholdMap() {
		if (null == appliedPromotionThresholdMap) {
			appliedPromotionThresholdMap = new HashMap<String, Map<String, Double>>();
		}
		return appliedPromotionThresholdMap;
	}

	/**
	 * @param appliedPromotionThresholdMap
	 *            the appliedPromotionThresholdMap to set
	 */
	public void setAppliedPromotionThresholdMap(Map<String, Map<String, Double>> appliedPromotionThresholdMap) {
		this.appliedPromotionThresholdMap = appliedPromotionThresholdMap;
	}

}
