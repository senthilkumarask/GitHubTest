package com.bbb.internationalshipping.vo;

import java.io.Serializable;


/**
 * This class gives you the information
 * about shoppingCurrency and shippingLocation.
 * @version 1.0
 */
public class BBBInternationalContextVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This variable is used to point to shoppingCurrency.
	 */
	private BBBInternationalCurrencyVO shoppingCurrency;
	/**
	 * This variable is used to point to shippingLocation.
	 */
	private BBBInternationalLocationVO shippingLocation;
	/**
	 * @return the shoppingCurrency
	 */
	public final BBBInternationalCurrencyVO getShoppingCurrency() {
		return shoppingCurrency;
	}
	/**
	 * @param shoppingCurrency the shoppingCurrency to set
	 */
	public final void setShoppingCurrency(final BBBInternationalCurrencyVO shoppingCurrency) {
		this.shoppingCurrency = shoppingCurrency;
	}
	/**
	 * @return the shippingLocation
	 */
	public final BBBInternationalLocationVO getShippingLocation() {
		return shippingLocation;
	}
	/**
	 * @param shippingLocation the shippingLocation to set
	 */
	public final void setShippingLocation(final BBBInternationalLocationVO shippingLocation) {
		this.shippingLocation = shippingLocation;
	}

	
}
