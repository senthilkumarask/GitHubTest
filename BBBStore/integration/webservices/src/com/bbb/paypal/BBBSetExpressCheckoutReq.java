/**
 * BBBSetExpressCheckoutReq.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;

import java.util.Map;

import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.framework.integration.ServiceRequestBase;

/**
 * @author ssh108
 *
 */

public class BBBSetExpressCheckoutReq extends ServiceRequestBase {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBSetExpressCheckoutRequestType setExpressCheckoutRequest;
    private BBBPayPalCredentials cred;
    private BBBOrderImpl order;
    private Map<String, PayPalProdDescVO> item;
    private String orderShippingCost;
	private String orderTax;
	private String orderTotal;
	private String surcharge;
	private String giftWrapTotal;
	private String shippingDiscount;
	private boolean isFromCart;
	private String local;
	private String shippingGroupId;
	private String giftCardAmount;
	private String ehfAmount;
	private String estimatedPST;
	private String estimatedGSTHST;
	
	private String ltlDeliveryTotal;
	private String ltlAssemblyTotal;


	/**
	 * @return the ehfAmount
	 */
	public String getEhfAmount() {
		return ehfAmount;
	}

	/**
	 * @param ehfAmount the ehfAmount to set
	 */
	public void setEhfAmount(String ehfAmount) {
		this.ehfAmount = ehfAmount;
	}

	/**
	 * @return
	 */
	public String getGiftCardAmount() {
		return giftCardAmount;
	}

	/**
	 * @param giftCardAmount
	 */
	public void setGiftCardAmount(String giftCardAmount) {
		this.giftCardAmount = giftCardAmount;
	}

	/**
	 * @return the local
	 */
	public String getLocal() {
		return local;
	}

	/**
	 * @param local the local to set
	 */
	public void setLocal(String local) {
		this.local = local;
	}

	/**
	 * @return the isFromCart
	 */
	public boolean isFromCart() {
		return this.isFromCart;
	}

	/**
	 * @param isFromCart the isFromCart to set
	 */
	public void setFromCart(boolean isFromCart) {
		this.isFromCart = isFromCart;
	}

	/**
	 * @return the shippingDiscount
	 */
	public String getShippingDiscount() {
		return this.shippingDiscount;
	}

	/**
	 * @param shippingDiscount the shippingDiscount to set
	 */
	public void setShippingDiscount(String shippingDiscount) {
		this.shippingDiscount = shippingDiscount;
	}

	
	
    /**
	 * @return the giftWrapTotal
	 */
	public String getGiftWrapTotal() {
		return this.giftWrapTotal;
	}

	/**
	 * @param giftWrapTotal the giftWrapTotal to set
	 */
	public void setGiftWrapTotal(String giftWrapTotal) {
		this.giftWrapTotal = giftWrapTotal;
	}

	/**
	 * @return the orderTotal
	 */
	public String getOrderTotal() {
		return this.orderTotal;
	}

	/**
	 * @param orderTotal the orderTotal to set
	 */
	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	/**
	 * @return the surcharge
	 */
	public String getSurcharge() {
		return this.surcharge;
	}

	/**
	 * @param surcharge the surcharge to set
	 */
	public void setSurcharge(String surcharge) {
		this.surcharge = surcharge;
	}

	/**
	 * @return the orderShippingCost
	 */
	public String getOrderShippingCost() {
		return this.orderShippingCost;
	}

	/**
	 * @param orderShippingCost the orderShippingCost to set
	 */
	public void setOrderShippingCost(String orderShippingCost) {
		this.orderShippingCost = orderShippingCost;
	}

	/**
	 * @return the orderTax
	 */
	public String getOrderTax() {
		return this.orderTax;
	}

	/**
	 * @param orderTax the orderTax to set
	 */
	public void setOrderTax(String orderTax) {
		this.orderTax = orderTax;
	}

	/**
	 * @return the item
	 */
	public Map<String, PayPalProdDescVO> getItem() {
		return this.item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Map<String, PayPalProdDescVO> item) {
		this.item = item;
	}

	/**
	 * @return the order
	 */
	public BBBOrderImpl getOrder() {
		return this.order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(BBBOrderImpl order) {
		this.order = order;
	}

	/**
	 * @return the cred
	 */
	public BBBPayPalCredentials getCred() {
		return this.cred;
	}

	/**
	 * @param cred the cred to set
	 */
	public void setCred(BBBPayPalCredentials cred) {
		this.cred = cred;
	}

	private String serviceName;

	/**
	 * @return the setExpressCheckoutRequest
	 */
	public BBBSetExpressCheckoutRequestType getSetExpressCheckoutRequest() {
		return this.setExpressCheckoutRequest;
	}

	/**
	 * @param setExpressCheckoutRequest the setExpressCheckoutRequest to set
	 */
	public void setSetExpressCheckoutRequest(BBBSetExpressCheckoutRequestType setExpressCheckoutRequest) {
		this.setExpressCheckoutRequest = setExpressCheckoutRequest;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the shippingGroupId
	 */
	public String getShippingGroupId() {
		return shippingGroupId;
	}

	/**
	 * @param shippingGroupId the shippingGroupId to set
	 */
	public void setShippingGroupId(String shippingGroupId) {
		this.shippingGroupId = shippingGroupId;
	}

	/**
	 * @return the estimatedGSTHST
	 */
	public String getEstimatedGSTHST() {
		return estimatedGSTHST;
	}

	/**
	 * @param estimatedGSTHST the estimatedGSTHST to set
	 */
	public void setEstimatedGSTHST(String estimatedGSTHST) {
		this.estimatedGSTHST = estimatedGSTHST;
	}

	/**
	 * @return the estimatedPST
	 */
	public String getEstimatedPST() {
		return estimatedPST;
	}

	/**
	 * @param estimatedPST the estimatedPST to set
	 */
	public void setEstimatedPST(String estimatedPST) {
		this.estimatedPST = estimatedPST;
	}
	
 	/**
	 * @return the ltlDeliveryTotal
	 */
	public String getLtlDeliveryTotal() {
		return ltlDeliveryTotal;
	}

	/**
	 * @param ltlDeliveryTotal the ltlDeliveryTotal to set
	 */
	public void setLtlDeliveryTotal(String ltlDeliveryTotal) {
		this.ltlDeliveryTotal = ltlDeliveryTotal;
	}

	/**
	 * @return the ltlAssemblyTotal
	 */
	public String getLtlAssemblyTotal() {
		return ltlAssemblyTotal;
	}

	/**
	 * @param ltlAssemblyTotal the ltlAssemblyTotal to set
	 */
	public void setLtlAssemblyTotal(String ltlAssemblyTotal) {
		this.ltlAssemblyTotal = ltlAssemblyTotal;
	}
	
}
