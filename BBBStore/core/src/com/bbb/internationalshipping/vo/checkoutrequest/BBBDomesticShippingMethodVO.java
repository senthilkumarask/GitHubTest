package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 * This class gives you the information
 * about Basket Shipping Method VO.
 * @version 1.0
 */
public class BBBDomesticShippingMethodVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to domesticShippingPrice.
	 */
	private double domesticShippingPrice;
	/**
	 * This variable is used to point to domesticHandlingPrice.
	 */
	private double domesticHandlingPrice;
	/**
	 * This variable is used to point to extraInsurancePrice.
	 */
	private double extraInsurancePrice;
	/**
	 * This variable is used to point to deliveryPromiseMinimum.
	 */
	private int deliveryPromiseMinimum;
	/**
	 * This variable is used to point to deliveryPromiseMaximum.
	 */
	private int deliveryPromiseMaximum;
	/**
	 * @return the domesticShippingPrice
	 */
	public final double getDomesticShippingPrice() {
		return domesticShippingPrice;
	}
	/**
	 * @param domesticShippingPrice the domesticShippingPrice to set
	 */
	public final void setDomesticShippingPrice(final double domesticShippingPrice) {
		this.domesticShippingPrice = domesticShippingPrice;
	}
	/**
	 * @return the domesticHandlingPrice
	 */
	public final double getDomesticHandlingPrice() {
		return domesticHandlingPrice;
	}
	/**
	 * @param domesticHandlingPrice the domesticHandlingPrice to set
	 */
	public final void setDomesticHandlingPrice(final double domesticHandlingPrice) {
		this.domesticHandlingPrice = domesticHandlingPrice;
	}
	/**
	 * @return the extraInsurancePrice
	 */
	public final double getExtraInsurancePrice() {
		return extraInsurancePrice;
	}
	/**
	 * @param extraInsurancePrice the extraInsurancePrice to set
	 */
	public final void setExtraInsurancePrice(final double extraInsurancePrice) {
		this.extraInsurancePrice = extraInsurancePrice;
	}
	/**
	 * @return the deliveryPromiseMinimum
	 */
	public final int getDeliveryPromiseMinimum() {
		return deliveryPromiseMinimum;
	}
	/**
	 * @param deliveryPromiseMinimum the deliveryPromiseMinimum to set
	 */
	public final void setDeliveryPromiseMinimum(final int deliveryPromiseMinimum) {
		this.deliveryPromiseMinimum = deliveryPromiseMinimum;
	}
	/**
	 * @return the deliveryPromiseMaximum
	 */
	public final int getDeliveryPromiseMaximum() {
		return deliveryPromiseMaximum;
	}
	/**
	 * @param deliveryPromiseMaximum the deliveryPromiseMaximum to set
	 */
	public final void setDeliveryPromiseMaximum(final int deliveryPromiseMaximum) {
		this.deliveryPromiseMaximum = deliveryPromiseMaximum;
	}
}
