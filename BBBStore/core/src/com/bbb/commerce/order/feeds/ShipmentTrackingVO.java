package com.bbb.commerce.order.feeds;


public class ShipmentTrackingVO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String trackingId;
	private String trackingInfo;
	private String carrierCode;
	private Object shipModificationDate;
	private String mUrl;
	private int mShipmentQty;
	/**
	 * @return the url
	 */
	public String getUrl() {
		return mUrl;
	}
	/**
	 * @param pUrl the url to set
	 */
	public void setUrl(String pUrl) {
		mUrl = pUrl;
	}
	/**
	 * @return the trackingId
	 */
	public String getTrackingId() {
		return trackingId;
	}
	/**
	 * @param trackingId the trackingId to set
	 */
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	/**
	 * @return the trackingInfo
	 */
	public String getTrackingInfo() {
		return trackingInfo;
	}
	/**
	 * @param trackingInfo the trackingInfo to set
	 */
	public void setTrackingInfo(String trackingInfo) {
		this.trackingInfo = trackingInfo;
	}
	/**
	 * @return the carrierCode
	 */
	public String getCarrierCode() {
		return carrierCode;
	}
	/**
	 * @param carrierCode the carrierCode to set
	 */
	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}
	/**
	 * @return the shipModificationDate
	 */
	public Object getShipModificationDate() {
		return shipModificationDate;
	}
	/**
	 * @param shipModificationDate the shipModificationDate to set
	 */
	public void setShipModificationDate(Object shipModificationDate) {
		this.shipModificationDate = shipModificationDate;
	}
	
	/**
	 * @return the shipmentQty
	 */
	public int getShipmentQty() {
		return mShipmentQty;
	}
	/**
	 * @param pShipmentQty the shipmentQty to set
	 */
	public void setShipmentQty(int pShipmentQty) {
		mShipmentQty = pShipmentQty;
	}
	@Override
	public String toString() {
		return "ShipmentTrackingVO [trackingId=" + trackingId
				+ ", trackingInfo=" + trackingInfo + ", carrierCode="
				+ carrierCode + ", shipModificationDate="
				+ shipModificationDate + "]";
	}
	
	
	
}
