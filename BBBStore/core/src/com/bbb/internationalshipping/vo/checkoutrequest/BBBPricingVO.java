package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 * This class gives you the information
 * about Basket Pricing VO.
 * @version 1.0
 */
public class BBBPricingVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to listPrice.
	 */
	private double listPrice;
	/**
	 * This variable is used to point to itemDiscount.
	 */
	private double itemDiscount;
	/**
	 * This variable is used to point to salePrice.
	 */
	private double salePrice;
	/**
	 * This variable is used to point to productExtraShipping.
	 */
	private double productExtraShipping;
	/**
	 * This variable is used to point to productExtraHandling.
	 */
	private double productExtraHandling;
	/**
	 * @return the listPrice
	 */
	public final double getListPrice() {
		return listPrice;
	}
	/**
	 * @param listPrice the listPrice to set
	 */
	public final void setListPrice(final double listPrice) {
		this.listPrice = listPrice;
	}
	/**
	 * @return the itemDiscount
	 */
	public final double getItemDiscount() {
		return itemDiscount;
	}
	/**
	 * @param itemDiscount the itemDiscount to set
	 */
	public final void setItemDiscount(final double itemDiscount) {
		this.itemDiscount = itemDiscount;
	}
	/**
	 * @return the salePrice
	 */
	public final double getSalePrice() {
		return salePrice;
	}
	/**
	 * @param salePrice the salePrice to set
	 */
	public final void setSalePrice(final double salePrice) {
		this.salePrice = salePrice;
	}
	/**
	 * @return the productExtraShipping
	 */
	public final double getProductExtraShipping() {
		return productExtraShipping;
	}
	/**
	 * @param productExtraShipping the productExtraShipping to set
	 */
	public final void setProductExtraShipping(final double productExtraShipping) {
		this.productExtraShipping = productExtraShipping;
	}
	/**
	 * @return the productExtraHandling
	 */
	public final double getProductExtraHandling() {
		return productExtraHandling;
	}
	/**
	 * @param productExtraHandling the productExtraHandling to set
	 */
	public final void setProductExtraHandling(final double productExtraHandling) {
		this.productExtraHandling = productExtraHandling;
	}
}
