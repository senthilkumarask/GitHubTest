package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * OrderInfo.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */


public class OrderInfo   implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OrderHeaderInfo orderHeaderInfo;

    private CartDetailInfo cartDetailInfo;

	/**
	 * @return the orderHeaderInfo
	 */
	public OrderHeaderInfo getOrderHeaderInfo() {
		return orderHeaderInfo;
	}

	/**
	 * @param orderHeaderInfo the orderHeaderInfo to set
	 */
	public void setOrderHeaderInfo(OrderHeaderInfo orderHeaderInfo) {
		this.orderHeaderInfo = orderHeaderInfo;
	}

	/**
	 * @return the cartDetailInfo
	 */
	public CartDetailInfo getCartDetailInfo() {
		return cartDetailInfo;
	}

	/**
	 * @param cartDetailInfo the cartDetailInfo to set
	 */
	public void setCartDetailInfo(CartDetailInfo cartDetailInfo) {
		this.cartDetailInfo = cartDetailInfo;
	}

}

