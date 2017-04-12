package com.bbb.account.vo.order;

import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

/**
 * OrderDetailInfoReturn.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */


public class OrderDetailInfoReturn extends ServiceResponseBase {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ErrorStatus status;

    private Billing billing;

    private Shipping shipping;

    private Payments payments;

    private GiftPackaging giftPackaging;

    private OrderInfo orderInfo;

    private List<Shipment> shipments;

	/**
	 * @return the status
	 */
	public ErrorStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ErrorStatus status) {
		this.status = status;
	}

	/**
	 * @return the billing
	 */
	public Billing getBilling() {
		return billing;
	}

	/**
	 * @param billing the billing to set
	 */
	public void setBilling(Billing billing) {
		this.billing = billing;
	}

	/**
	 * @return the shipping
	 */
	public Shipping getShipping() {
		return shipping;
	}

	/**
	 * @param shipping the shipping to set
	 */
	public void setShipping(Shipping shipping) {
		this.shipping = shipping;
	}

	/**
	 * @return the payments
	 */
	public Payments getPayments() {
		return payments;
	}

	/**
	 * @param payments the payments to set
	 */
	public void setPayments(Payments payments) {
		this.payments = payments;
	}

	/**
	 * @return the giftPackaging
	 */
	public GiftPackaging getGiftPackaging() {
		return giftPackaging;
	}

	/**
	 * @param giftPackaging the giftPackaging to set
	 */
	public void setGiftPackaging(GiftPackaging giftPackaging) {
		this.giftPackaging = giftPackaging;
	}

	/**
	 * @return the orderInfo
	 */
	public OrderInfo getOrderInfo() {
		return orderInfo;
	}

	/**
	 * @param orderInfo the orderInfo to set
	 */
	public void setOrderInfo(OrderInfo orderInfo) {
		this.orderInfo = orderInfo;
	}

	/**
	 * @return the shipments
	 */
	public List<Shipment> getShipments() {
		return shipments;
	}

	/**
	 * @param shipments the shipments to set
	 */
	public void setShipments(List<Shipment> shipments) {
		this.shipments = shipments;
	}
	
    
}
