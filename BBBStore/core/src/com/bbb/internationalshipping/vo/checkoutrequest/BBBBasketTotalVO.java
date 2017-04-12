package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 * This class gives you the information
 * about Basket Total VO.
 * @version 1.0
 */
public class BBBBasketTotalVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to totalSalePrice.
	 */
	private double totalSalePrice;
	/**
	 * This variable is used to point to orderDiscount.
	 */
	private double orderDiscount;
	/**
	 * This variable is used to point to totalProductExtraShipping.
	 */
	private double totalProductExtraShipping;
	/**
	 * This variable is used to point to totalProductExtraHandling.
	 */
	private double totalProductExtraHandling;
	/**
	 * This variable is used to point to totalPrice.
	 */
	private double totalPrice;
	/**
	 * @return the totalSalePrice
	 */
	public final double getTotalSalePrice() {
		return totalSalePrice;
	}
	/**
	 * @param totalSalePrice the totalSalePrice to set
	 */
	public final void setTotalSalePrice(final double totalSalePrice) {
		this.totalSalePrice = totalSalePrice;
	}
	/**
	 * @return the orderDiscount
	 */
	public final double getOrderDiscount() {
		return orderDiscount;
	}
	/**
	 * @param orderDiscount the orderDiscount to set
	 */
	public final void setOrderDiscount(final double orderDiscount) {
		this.orderDiscount = orderDiscount;
	}
	/**
	 * @return the totalProductExtraShipping
	 */
	public final double getTotalProductExtraShipping() {
		return totalProductExtraShipping;
	}
	/**
	 * @param totalProductExtraShipping the totalProductExtraShipping to set
	 */
	public final void setTotalProductExtraShipping(final double totalProductExtraShipping) {
		this.totalProductExtraShipping = totalProductExtraShipping;
	}
	/**
	 * @return the totalProductExtraHandling
	 */
	public final double getTotalProductExtraHandling() {
		return totalProductExtraHandling;
	}
	/**
	 * @param totalProductExtraHandling the totalProductExtraHandling to set
	 */
	public final void setTotalProductExtraHandling(final double totalProductExtraHandling) {
		this.totalProductExtraHandling = totalProductExtraHandling;
	}
	/**
	 * @return the totalPrice
	 */
	public final double getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public final void setTotalPrice(final double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	

}
