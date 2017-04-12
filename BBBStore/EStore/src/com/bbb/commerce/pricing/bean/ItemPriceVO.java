package com.bbb.commerce.pricing.bean;

import java.io.Serializable;


/**
 * Bean class for price related information of an Item
 * 
 * @author hbandl
 *
 */
public class ItemPriceVO implements Serializable {
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * list price of Item
     */
    private double listPrice;
    
    /**
     * sale price of Item
     */
    private double salePrice;
    
    /**
     * total price of an Item
     */
    private double totalPrice;
    
    /**
     * sku Id of Item
     */
    private String skuId;
    
    /**
     * quantity of item
     */
    private int quantity;
    
    
    
	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return skuId;
	}

	/**
	 * @param skuId the skuId to set
	 */
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the listPrice
	 */
	public double getListPrice() {
		return listPrice;
	}

	/**
	 * @param listPrice the listPrice to set
	 */
	public void setListPrice(double listPrice) {
		this.listPrice = listPrice;
	}

	/**
	 * @return the salePrice
	 */
	public double getSalePrice() {
		return salePrice;
	}

	/**
	 * @param salePrice the salePrice to set
	 */
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	/**
	 * @return the totalPrice
	 */
	public double getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
    
}

