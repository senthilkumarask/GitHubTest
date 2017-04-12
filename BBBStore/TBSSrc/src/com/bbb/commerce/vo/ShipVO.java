package com.bbb.commerce.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem;

public class ShipVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double shipAmount;
	private String shipState;
	private List<String> skuIds;
	private String skuName;
	private Map<String, String> skuQty  = new HashMap<String, String>();
	private Map<String, CommerceItem> commItem  = new HashMap<String, CommerceItem>();
	private Map<String, String> autoWaiveClassificationList  = new HashMap<String, String>();
	/**
	 * @return the shipAmount
	 */
	public double getShipAmount() {
		return shipAmount;
	}
	/**
	 * @return the shipState
	 */
	public String getShipState() {
		return shipState;
	}
	/**
	 * @param pShipAmount the shipAmount to set
	 */
	public void setShipAmount(double pShipAmount) {
		shipAmount = pShipAmount;
	}
	/**
	 * @param pShipState the shipState to set
	 */
	public void setShipState(String pShipState) {
		shipState = pShipState;
	}
	/**
	 * @return the skuName
	 */
	public String getSkuName() {
		return skuName;
	}
	/**
	 * @param pSkuName the skuName to set
	 */
	public void setSkuName(String pSkuName) {
		skuName = pSkuName;
	}
	/**
	 * @return the skuIds
	 */
	public List<String> getSkuIds() {
		return skuIds;
	}
	/**
	 * @param pSkuIds the skuIds to set
	 */
	public void setSkuIds(List<String> pSkuIds) {
		skuIds = pSkuIds;
	}
	public Map<String, String> getSkuQty() {
		return skuQty;
	}
	public void setSkuQty(Map<String, String> skuQty) {
		this.skuQty = skuQty;
	}
	public Map<String, CommerceItem> getCommItem() {
		return commItem;
	}
	public void setCommItem(Map<String, CommerceItem> commItem) {
		this.commItem = commItem;
	}
	
	public Map<String, String> getAutoWaiveClassificationList() {
		return autoWaiveClassificationList;
	}

	public void setAutoWaiveClassificationList(
			Map<String, String> autoWaiveClassificationList) {
		this.autoWaiveClassificationList = autoWaiveClassificationList;
	}
	
}
