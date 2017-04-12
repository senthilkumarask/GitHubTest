package com.bbb.account.vo.order;

import java.io.Serializable;

//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 23-November-2011
//--------------------------------------------------------------------------------
public class ShipmentTrackingInfoVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mTrackingNumber;
	private String mVendorTrackingURL;
	private String mVendorImageURL;
	private String mCarrier;
	private String mCarrierCode;
	private String mShippingDate;
	private String mDeliveryDate;
	/**
	 * @return the trackingNumber
	 */
	public String getTrackingNumber() {
		return mTrackingNumber;
	}
	/**
	 * @param pTrackingNumber the trackingNumber to set
	 */
	public void setTrackingNumber(String pTrackingNumber) {
		mTrackingNumber = pTrackingNumber;
	}
	/**
	 * @return the carrierName
	 */
	public String getCarrier() {
		return mCarrier;
	}
	/**
	 * @param pCarrierName the carrierName to set
	 */
	public void setCarrier(String pCarrier) {
		mCarrier = pCarrier;
	}
	/**
	 * @return the shippingDate
	 */
	public String getShippingDate() {
		return mShippingDate;
	}
	/**
	 * @param pShippingDate the shippingDate to set
	 */
	public void setShippingDate(String pShippingDate) {
		mShippingDate = pShippingDate;
	}
	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate() {
		return mDeliveryDate;
	}
	/**
	 * @param pDeliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(String pDeliveryDate) {
		mDeliveryDate = pDeliveryDate;
	}
	/**
	 * @return the vendorTrackingURL
	 */
	public String getVendorTrackingURL() {
		return mVendorTrackingURL;
	}
	/**
	 * @param pVendorTrackingURL the vendorTrackingURL to set
	 */
	public void setVendorTrackingURL(String pVendorTrackingURL) {
		mVendorTrackingURL = pVendorTrackingURL;
	}
	/**
	 * @return the vendorImageURL
	 */
	public String getVendorImageURL() {
		return mVendorImageURL;
	}
	/**
	 * @param pVendorImageURL the vendorImageURL to set
	 */
	public void setVendorImageURL(String pVendorImageURL) {
		mVendorImageURL = pVendorImageURL;
	}
	/**
	 * @return the carrierCode
	 */
	public String getCarrierCode() {
		return mCarrierCode;
	}
	/**
	 * @param pCarrierCode the carrierCode to set
	 */
	public void setCarrierCode(String pCarrierCode) {
		mCarrierCode = pCarrierCode;
	}
	
}
