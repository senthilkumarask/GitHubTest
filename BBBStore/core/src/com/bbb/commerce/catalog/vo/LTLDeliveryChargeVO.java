package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

/**
 * 
 * @author naga13
 *
 */
public class LTLDeliveryChargeVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ltlDeliveryChargeSkuId
	 */
	private String ltlDeliveryChargeSkuId;
	/**
	 * ltlDeliveryChargeProductId
	 */
	private String ltlDeliveryChargeProductId;
	/**
	 * ltlDeliveryChargeSkuPrice
	 */
	private double ltlDeliveryChargeSkuPrice;
	
	public LTLDeliveryChargeVO(){
		//default constructor
	}

	public LTLDeliveryChargeVO(String ltlDeliveryChargeSkuId, String ltlDeliveryChargeProductId,
			double ltlDeliveryChargeSkuPrice) {
		this.ltlDeliveryChargeSkuId = ltlDeliveryChargeSkuId;
		this.ltlDeliveryChargeProductId = ltlDeliveryChargeProductId;
		this.ltlDeliveryChargeSkuPrice = ltlDeliveryChargeSkuPrice;
	}

	
	/**
	 * @return the ltlDeliveryChargeSkuId
	 */
	public String getLtlDeliveryChargeSkuId() {
		return ltlDeliveryChargeSkuId;
	}

	/**
	 * @param ltlDeliveryChargeSkuId the ltlDeliveryChargeSkuId to set
	 */
	public void setLtlDeliveryChargeSkuId(String ltlDeliveryChargeSkuId) {
		this.ltlDeliveryChargeSkuId = ltlDeliveryChargeSkuId;
	}

	/**
	 * @return the ltlDeliveryChargeProductId
	 */
	public String getLtlDeliveryChargeProductId() {
		return ltlDeliveryChargeProductId;
	}

	/**
	 * @param ltlDeliveryChargeProductId the ltlDeliveryChargeProductId to set
	 */
	public void setLtlDeliveryChargeProductId(String ltlDeliveryChargeProductId) {
		this.ltlDeliveryChargeProductId = ltlDeliveryChargeProductId;
	}

	/**
	 * @return the ltlDeliveryChargeSkuPrice
	 */
	public double getLtlDeliveryChargeSkuPrice() {
		return ltlDeliveryChargeSkuPrice;
	}

	/**
	 * @param ltlDeliveryChargeSkuPrice the ltlDeliveryChargeSkuPrice to set
	 */
	public void setLtlDeliveryChargeSkuPrice(double ltlDeliveryChargeSkuPrice) {
		this.ltlDeliveryChargeSkuPrice = ltlDeliveryChargeSkuPrice;
	}

	public String toString(){
		StringBuffer toString=new StringBuffer(" Delivery Charge VO Details \n ");
		toString.append("Delivery Charge Sku Id ").append(ltlDeliveryChargeSkuId).append("\n")
		.append("Delivery Charge product Id ").append(ltlDeliveryChargeProductId).append("\n")
		.append("Delivery Charge Sku Price ").append(ltlDeliveryChargeSkuPrice).append("\n");
		
		return toString.toString();
	}
}
