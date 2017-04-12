package com.bbb.autowaiveshippingservice;


import java.io.Serializable;
import java.math.BigInteger;

public class AutoWaiveShippingInfoRequestOrderLineItemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4370384645741863995L;

	private String orderLineId;
	
	private BigInteger sku;
	
	private BigInteger orderQty;
	
	private BigInteger registryNum;
	
	private BigInteger storeOnHandQty;
	
	private String skuClassification;

	public String getOrderLineId() {
		return orderLineId;
	}

	public void setOrderLineId(String orderLineId) {
		this.orderLineId = orderLineId;
	}

	public BigInteger getSku() {
		return sku;
	}

	public void setSku(BigInteger sku) {
		this.sku = sku;
	}

	public BigInteger getOrderQty() {
		return orderQty;
	}

	public void setOrderQty(BigInteger orderQty) {
		this.orderQty = orderQty;
	}

	public BigInteger getRegistryNum() {
		return registryNum;
	}

	public void setRegistryNum(BigInteger registryNum) {
		this.registryNum = registryNum;
	}

	public BigInteger getStoreOnHandQty() {
		return storeOnHandQty;
	}

	public void setStoreOnHandQty(BigInteger storeOnHandQty) {
		this.storeOnHandQty = storeOnHandQty;
	}

	public String getSkuClassification() {
		return skuClassification;
	}

	public void setSkuClassification(String skuClassification) {
		this.skuClassification = skuClassification;
	}
	
}
