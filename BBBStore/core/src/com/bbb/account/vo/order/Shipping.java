package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * Shipping.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */


public class Shipping  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Address address;

    private String addressNm;

    private String addressID;

    private long orderShipmentID;

    private String addToAcct;

    private double shipMethodPrice;

    private String shipToFlag;

    private String shipDt;

    private String deliveryDt;

    private String showFlag;

    private String shipMethodCD;

    private boolean isPreferred;

    private boolean isSelected;

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the addressNm
	 */
	public String getAddressNm() {
		return addressNm;
	}

	/**
	 * @param addressNm the addressNm to set
	 */
	public void setAddressNm(String addressNm) {
		this.addressNm = addressNm;
	}

	/**
	 * @return the addressID
	 */
	public String getAddressID() {
		return addressID;
	}

	/**
	 * @param addressID the addressID to set
	 */
	public void setAddressID(String addressID) {
		this.addressID = addressID;
	}

	/**
	 * @return the orderShipmentID
	 */
	public long getOrderShipmentID() {
		return orderShipmentID;
	}

	/**
	 * @param orderShipmentID the orderShipmentID to set
	 */
	public void setOrderShipmentID(long orderShipmentID) {
		this.orderShipmentID = orderShipmentID;
	}

	/**
	 * @return the addToAcct
	 */
	public String getAddToAcct() {
		return addToAcct;
	}

	/**
	 * @param addToAcct the addToAcct to set
	 */
	public void setAddToAcct(String addToAcct) {
		this.addToAcct = addToAcct;
	}

	/**
	 * @return the shipMethodPrice
	 */
	public double getShipMethodPrice() {
		return shipMethodPrice;
	}

	/**
	 * @param shipMethodPrice the shipMethodPrice to set
	 */
	public void setShipMethodPrice(double shipMethodPrice) {
		this.shipMethodPrice = shipMethodPrice;
	}

	/**
	 * @return the shipToFlag
	 */
	public String getShipToFlag() {
		return shipToFlag;
	}

	/**
	 * @param shipToFlag the shipToFlag to set
	 */
	public void setShipToFlag(String shipToFlag) {
		this.shipToFlag = shipToFlag;
	}

	/**
	 * @return the shipDt
	 */
	public String getShipDt() {
		return shipDt;
	}

	/**
	 * @param shipDt the shipDt to set
	 */
	public void setShipDt(String shipDt) {
		this.shipDt = shipDt;
	}

	/**
	 * @return the deliveryDt
	 */
	public String getDeliveryDt() {
		return deliveryDt;
	}

	/**
	 * @param deliveryDt the deliveryDt to set
	 */
	public void setDeliveryDt(String deliveryDt) {
		this.deliveryDt = deliveryDt;
	}

	/**
	 * @return the showFlag
	 */
	public String getShowFlag() {
		return showFlag;
	}

	/**
	 * @param showFlag the showFlag to set
	 */
	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}

	/**
	 * @return the shipMethodCD
	 */
	public String getShipMethodCD() {
		return shipMethodCD;
	}

	/**
	 * @param shipMethodCD the shipMethodCD to set
	 */
	public void setShipMethodCD(String shipMethodCD) {
		this.shipMethodCD = shipMethodCD;
	}

	/**
	 * @return the isPreferred
	 */
	public boolean isPreferred() {
		return isPreferred;
	}

	/**
	 * @param isPreferred the isPreferred to set
	 */
	public void setPreferred(boolean isPreferred) {
		this.isPreferred = isPreferred;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
