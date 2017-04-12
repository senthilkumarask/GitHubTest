/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
//import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * This class provides the shipping information properties.
 *
 * @author sku134
 */
public class ShippingVO implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The future shipping date. */
	private String futureShippingDate;
	
	/** The future shipping date ws. */
	private String futureShippingDateWS;
	
	/** The shipping address. */
	private AddressVO shippingAddress;
	
	/** The futureshipping address. */
	private AddressVO futureshippingAddress;
	
	/**
	 * Gets the future shipping date.
	 *
	 * @return the future shipping date
	 */
	public String getFutureShippingDate() {
		return futureShippingDate;
	}

	/**
	 * Sets the future shipping date.
	 *
	 * @param futureShippingDate the new future shipping date
	 */
	public void setFutureShippingDate(String futureShippingDate) {
		this.futureShippingDate = futureShippingDate;
	}

	/**
	 * Gets the shipping address.
	 *
	 * @return the shippingAddress
	 */
	public AddressVO getShippingAddress() {
		if (shippingAddress != null) {
			return shippingAddress;

		} else {
			shippingAddress = new AddressVO();
			return shippingAddress;
			
		}
	}

	/**
	 * Sets the shipping address.
	 *
	 * @param shippingAddress the shippingAddress to set
	 */
	public void setShippingAddress(final AddressVO shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	/**
	 * Gets the futureshipping address.
	 *
	 * @return the futureshippingAddress
	 */
	public AddressVO getFutureshippingAddress() {
		if (futureshippingAddress != null) {
			return futureshippingAddress;

		} else {
			futureshippingAddress = new AddressVO();
			return futureshippingAddress;

		}

	}

	/**
	 * Sets the futureshipping address.
	 *
	 * @param futureshippingAddress the futureshippingAddress to set
	 */
	public void setFutureshippingAddress(final AddressVO futureshippingAddress) {
		this.futureshippingAddress = futureshippingAddress;
	}

	/**
	 * Gets the future shipping date ws.
	 *
	 * @return the future shipping date ws
	 */
	public String getFutureShippingDateWS() {
		return futureShippingDateWS;
	}

	/**
	 * Sets the future shipping date ws.
	 *
	 * @param futureShippingDateWS the new future shipping date ws
	 */
	public void setFutureShippingDateWS(String futureShippingDateWS) {
		this.futureShippingDateWS = futureShippingDateWS;
	}

}
