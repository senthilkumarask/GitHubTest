/**
 * 
 */
package com.bbb.common.vo;

import java.io.Serializable;



/**
 * @author vagra4
 * 
 */
public class ShippingMethodPriceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double mMinOrderPrice;
	private Double mMaxOrderPrice;
	private String mShippingMethodName;
	private String mShippingPrice;
	private String mStateName;

	/**
	 * Parameterized constructor will ease the ShippingMethodPriceVO object
	 * creation outside.
	 * 
	 * @param pMinOrderPrice
	 * @param pMaxOrderPrice
	 * @param pShippingMethodName
	 * @param pShippingPrice
	 */
	public ShippingMethodPriceVO(Double pMinOrderPrice, Double pMaxOrderPrice,
			String pShippingMethodName, String pShippingPrice, String pStateName) {

		mMinOrderPrice = pMinOrderPrice;
		mMaxOrderPrice = pMaxOrderPrice;
		mShippingMethodName = pShippingMethodName;
		mShippingPrice = pShippingPrice;
		mStateName = pStateName;
	}

	/**
	 * @return the mLowershippingPrice
	 */
	public Double getMinOrderPrice() {
		return mMinOrderPrice;
	}

	/**
	 * @param pMinOrderPrice
	 *            the mMinOrderPrice to set
	 */
	public void setMinOrderPrice(Double pMinOrderPrice) {
		mMinOrderPrice = pMinOrderPrice;
	}

	/**
	 * @return the mMaxOrderPrice
	 */
	public Double getMaxOrderPrice() {
		return mMaxOrderPrice;
	}

	/**
	 * @param pMaxOrderPrice
	 *            the mMaxOrderPrice to set
	 */
	public void setMaxOrderPrice(Double pMaxOrderPrice) {
		mMaxOrderPrice = pMaxOrderPrice;
	}

	/**
	 * @return the mShippingMethodName
	 */
	public String getShippingMethodName() {
		return mShippingMethodName;
	}

	/**
	 * @param pShippingMethodName
	 *            the mShippingMethodName to set
	 */
	public void setShippingMethodName(String pShippingMethodName) {
		mShippingMethodName = pShippingMethodName;
	}

	/**
	 * @return the mShippingPrice
	 */
	public String getShippingPrice() {
		return mShippingPrice;
	}

	/**
	 * @param pShippingPrice
	 *            the mShippingPrice to set
	 */
	public void setShippingPrice(String pShippingPrice) {
		mShippingPrice = pShippingPrice;
	}

	/**
	 * @return the mStateName
	 */
	public String getStateName() {
		return mStateName;
	}

	/**
	 * @param pStateName
	 *            the mStateName to set
	 */
	public void setStateName(String pStateName) {
		mStateName = pStateName;
	}

	/**
	 * This method returns the current state of ShippingMethodPriceVO object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ShippingMethodPriceVO [mMinOrderPrice=" + mMinOrderPrice
				+ ", mMaxOrderPrice=" + mMaxOrderPrice
				+ ", mShippingMethodName=" + mShippingMethodName
				+ ", mShippingPrice=" + mShippingPrice + ", mStateName="
				+ mStateName + "]";
	}

}
