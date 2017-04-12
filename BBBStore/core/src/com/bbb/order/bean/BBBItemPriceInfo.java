package com.bbb.order.bean;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;

/** This class is used by the ItemPriceInfo.
 *
 * @author Pradeep Reddy */
public class BBBItemPriceInfo extends ItemPriceInfo {

    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    /** Constant for shipTaxMap. */
    private Map<String, TaxPriceInfo> shipTaxMap;
    private double personalizedPrice;
    private double currentPrice;

    /** Default constructor for BBBItemPriceInfo. */
    public BBBItemPriceInfo() {
        super();
    }

    /** @return the shipTaxMap */
    public final Map<String, TaxPriceInfo> getShipTaxMap() {
        if (this.shipTaxMap == null) {
            this.shipTaxMap = new HashMap<String, TaxPriceInfo>();
        }
        return this.shipTaxMap;
    }

    /** @param shipTaxMap the shipTaxMap to set */
    public final void setShipTaxMap(final Map<String, TaxPriceInfo> shipTaxMap) {
        this.shipTaxMap = shipTaxMap;
    }

    /**
	 * @return the personalizedPrice
	 */
	public double getPersonalizedPrice() {
		return this.personalizedPrice;
	}

	/**
	 * @param personalizedPrice the personalizedPrice to set
	 */
	public void setPersonalizedPrice(double personalizedPrice) {
		this.personalizedPrice = personalizedPrice;
	}

    /**
	 * @return the currentPrice
	 */
	public double getCurrentPrice() {
		return this.currentPrice;
	}

	/**
	 * @param currentPrice the currentPrice to set
	 */
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	@Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBItemPriceInfo [shipTaxMap=").append(this.shipTaxMap).append(", getRawTotalPrice()=")
                        .append(this.getRawTotalPrice()).append(", getListPrice()=").append(this.getListPrice())
                        .append(", getSalePrice()=").append(this.getSalePrice()).append(", isOnSale()=")
                        .append(this.isOnSale()).append(", getOrderDiscountShare()=")
                        .append(this.getOrderDiscountShare()).append(", getQuantityDiscounted()=")
                        .append(this.getQuantityDiscounted()).append(", getQuantityAsQualifier()=")
                        .append(this.getQuantityAsQualifier()).append(", getPriceList()=").append(this.getPriceList())
                        .append(", getCurrentPriceDetails()=").append(this.getCurrentPriceDetails())
                        .append(", getCurrentPriceDetailsSorted()=").append(this.getCurrentPriceDetailsSorted())
                        .append(", toString()=").append(super.toString()).append(", getCurrencyCode()=")
                        .append(this.getCurrencyCode()).append(", getAmount()=").append(this.getAmount())
                        .append(", isDiscounted()=").append(this.isDiscounted()).append(", getAdjustments()=")
                        .append(this.getAdjustments()).append(", isAmountIsFinal()=").append(this.isAmountIsFinal())
                        .append(", getClosenessQualifiers()=").append(this.getClosenessQualifiers())
                        .append(", getFinalReasonCode()=").append(this.getFinalReasonCode()).append("]");
        return builder.toString();
    }
}
