package com.bbb.commerce.common;

import java.io.Serializable;
import java.util.List;

public class PriceBeanDisplayVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double unitPrice;
	private long quantity;
	private List<String> promotionDisplayNameList;
	//R 2.2 Ommniture 
	private double ommnitureDiscountAmount;
	private String couponDiscountType;
	
	
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setPromotionDisplayNameList(List<String> promotionDisplayNameList) {
		this.promotionDisplayNameList = promotionDisplayNameList;
	}
	public List<String> getPromotionDisplayNameList() {
		return promotionDisplayNameList;
	}
	/**
	 * @return the ommnitureDiscountAmount
	 */
	public double getOmmnitureDiscountAmount() {
		return ommnitureDiscountAmount;
	}
	/**
	 * @param ommnitureDiscountAmount the ommnitureDiscountAmount to set
	 */
	public void setOmmnitureDiscountAmount(double ommnitureDiscountAmount) {
		this.ommnitureDiscountAmount = ommnitureDiscountAmount;
	}
	/**
	 * @return the couponDiscountType
	 */
	public String getCouponDiscountType() {
		return couponDiscountType;
	}
	/**
	 * @param couponDiscountType the couponDiscountType to set
	 */
	public void setCouponDiscountType(String couponDiscountType) {
		this.couponDiscountType = couponDiscountType;
	}


}
