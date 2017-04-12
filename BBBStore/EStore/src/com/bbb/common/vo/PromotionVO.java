/**
 * 
 */
package com.bbb.common.vo;

import java.io.Serializable;

import atg.repository.RepositoryItem;


/**
 * @author Pradeep Reddy
 * 
 */
public class PromotionVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long quantity;
	private Double discountedPrice;
	private RepositoryItem pricingModel;
	
	
	/**
	 * @return the quantity
	 */
	public long getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the discountedPrice
	 */
	public Double getDiscountedPrice() {
		return discountedPrice;
	}
	/**
	 * @param discountedPrice the discountedPrice to set
	 */
	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	/**
	 * @return the pricingModel
	 */
	public RepositoryItem getPricingModel() {
		return pricingModel;
	}
	/**
	 * @param pricingModel the pricingModel to set
	 */
	public void setPricingModel(RepositoryItem pricingModel) {
		this.pricingModel = pricingModel;
	}	
}
