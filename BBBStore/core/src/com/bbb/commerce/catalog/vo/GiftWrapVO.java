package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

/**
 * 
 * @author njai13
 *
 */
public class GiftWrapVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wrapSkuId;
	private String wrapProductId;
	private double wrapSkuPrice;
	
	public GiftWrapVO()
	{
		//default constructor
	}
	public GiftWrapVO(String wrapSkuId, String wrapProductId,
			double wrapSkuPrice) {
		this.wrapSkuId = wrapSkuId;
		this.wrapProductId = wrapProductId;
		this.wrapSkuPrice = wrapSkuPrice;
	}
	/**
	 * @return the wrapSkuId
	 */
	public String getWrapSkuId() {
		return wrapSkuId;
	}
	/**
	 * @param wrapSkuId the wrapSkuId to set
	 */
	public void setWrapSkuId(String wrapSkuId) {
		this.wrapSkuId = wrapSkuId;
	}
	/**
	 * @return the wrapProductId
	 */
	public String getWrapProductId() {
		return wrapProductId;
	}
	/**
	 * @param wrapProductId the wrapProductId to set
	 */
	public void setWrapProductId(String wrapProductId) {
		this.wrapProductId = wrapProductId;
	}
	/**
	 * @return the wrapSkuPrice
	 */
	public double getWrapSkuPrice() {
		return wrapSkuPrice;
	}
	/**
	 * @param wrapSkuPrice the wrapSkuPrice to set
	 */
	public void setWrapSkuPrice(double wrapSkuPrice) {
		this.wrapSkuPrice = wrapSkuPrice;
	}
	
	public String toString(){
		StringBuffer toString=new StringBuffer(" Gift Wrap VO Details \n ");
		toString.append("Gift Wrap Sku Id ").append(wrapSkuId).append("\n")
		.append("Gift Wrap product Id ").append(wrapProductId).append("\n")
		.append("Gift Wrap Sku Price ").append(wrapSkuPrice).append("\n");
		
		return toString.toString();
	}
}
