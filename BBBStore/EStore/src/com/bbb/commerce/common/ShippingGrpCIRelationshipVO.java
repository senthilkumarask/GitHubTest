package com.bbb.commerce.common;

import java.io.Serializable;
import java.util.List;

import atg.commerce.pricing.UnitPriceBean;


/**
 * This is a custom class for order details.
 * 
 * @author msiddi
 * @version $Revision: #1 $
 */

public class ShippingGrpCIRelationshipVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * ===================================================== * MEMBER VARIABLES
	 * =====================================================
	 */
	
	
	private String commerceItemId;
	
	private String quantity;
	
	private double shippingGroupItemTotal;
	
	private List<UnitPriceBean> priceBeans;
	
	private long undiscountedItemsCount;
	
	private String registryId;
	
	
	
	public String getRegistryId() {
		return registryId;
	}

	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	public String getCommerceItemId() {
		return commerceItemId;
	}

	public void setCommerceItemId(String commerceItemId) {
		this.commerceItemId = commerceItemId;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	
	

	public void setPriceBeans(List<UnitPriceBean> priceBeans) {
		this.priceBeans = priceBeans;
	}

	public List<UnitPriceBean> getPriceBeans() {
		return priceBeans;
	}

	public void setUndiscountedItemsCount(long undiscountedItemsCount) {
		this.undiscountedItemsCount = undiscountedItemsCount;
	}

	public long getUndiscountedItemsCount() {
		return undiscountedItemsCount;
	}

	public void setShippingGroupItemTotal(double shippingGroupItemTotal) {
		this.shippingGroupItemTotal = shippingGroupItemTotal;
	}

	public double getShippingGroupItemTotal() {
		return shippingGroupItemTotal;
	}


	
}
