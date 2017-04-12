package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;

/**
 *This class gives you the information
 * about Basket Items VO.
 * @version 1.0
 */
public class BBBBasketItemsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *  This variable is used to point to skuId.
	 */
	private String skuId;
	/**
	 * This variable is used to point to quantity.
	 */
	private long quantity;
	/**
	 * This variable is used to point to BBBPricingVO.
	 */
	private BBBPricingVO bbbPricingVO;
	/**
	 * This variable is used to point to BBBDisplayVO.
	 */
	private BBBDisplayVO bbbDisplayVO;
	/**
	 * @return the skuId
	 */
	public final String getSkuId() {
		return skuId;
	}
	/**
	 * @param skuId the skuId to set
	 */
	public final void setSkuId(final String skuId) {
		this.skuId = skuId;
	}
	/**
	 * @return the quantity
	 */
	public final long getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public final void setQuantity(final long quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the bbbPricingVO
	 */
	public final BBBPricingVO getBbbPricingVO() {
		return bbbPricingVO;
	}
	/**
	 * @param bbbPricingVO the bbbPricingVO to set
	 */
	public final void setBbbPricingVO(final BBBPricingVO bbbPricingVO) {
		this.bbbPricingVO = bbbPricingVO;
	}
	/**
	 * @return the bbbDisplayVO
	 */
	public final BBBDisplayVO getBbbDisplayVO() {
		return bbbDisplayVO;
	}
	/**
	 * @param bbbDisplayVO the bbbDisplayVO to set
	 */
	public final void setBbbDisplayVO(final BBBDisplayVO bbbDisplayVO) {
		this.bbbDisplayVO = bbbDisplayVO;
	}
	
	
}
