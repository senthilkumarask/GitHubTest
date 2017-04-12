package com.bbb.autowaiveshippingservice;


import java.io.Serializable;

public class AutoWaiveShippingInfoResponseOrderLineItemVO implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2749436770444925229L;
	private String orderLineId;
	private int sku;
	private String storeSkuStatusFlag;
	private String skuClassification;
	private String waiveShipFlag;
	private String onHandStatusFlag;
	private int onHandQty;
	private int onOrderQty;
	private int pastStoreSalesQty;
	
	public String getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(String orderLineId) {
		this.orderLineId = orderLineId;
	}
	public int getSku() {
		return sku;
	}
	public void setSku(int sku) {
		this.sku = sku;
	}
	public String getStoreSkuStatusFlag() {
		return storeSkuStatusFlag;
	}
	public void setStoreSkuStatusFlag(String storeSkuStatusFlag) {
		this.storeSkuStatusFlag = storeSkuStatusFlag;
	}
	public String getSkuClassification() {
		return skuClassification;
	}
	public void setSkuClassification(String skuClassification) {
		this.skuClassification = skuClassification;
	}
	public String getWaiveShipFlag() {
		return waiveShipFlag;
	}
	public void setWaiveShipFlag(String waiveShipFlag) {
		this.waiveShipFlag = waiveShipFlag;
	}
	public String getOnHandStatusFlag() {
		return onHandStatusFlag;
	}
	public void setOnHandStatusFlag(String onHandStatusFlag) {
		this.onHandStatusFlag = onHandStatusFlag;
	}
	public int getOnHandQty() {
		return onHandQty;
	}
	public void setOnHandQty(int onHandQty) {
		this.onHandQty = onHandQty;
	}
	public int getOnOrderQty() {
		return onOrderQty;
	}
	public void setOnOrderQty(int onOrderQty) {
		this.onOrderQty = onOrderQty;
	}
	public int getPastStoreSalesQty() {
		return pastStoreSalesQty;
	}
	public void setPastStoreSalesQty(int pastStoreSalesQty) {
		this.pastStoreSalesQty = pastStoreSalesQty;
	}
	
}
