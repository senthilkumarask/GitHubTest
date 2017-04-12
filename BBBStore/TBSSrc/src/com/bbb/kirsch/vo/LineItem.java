package com.bbb.kirsch.vo;

import java.io.Serializable;
import java.util.Date;

public class LineItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mConfigId;
	
	private double mCost;
	
	private Date mEstimatedShipDate;
	
	private boolean mHasInstallation;
	
	private String mProductDesc;
	
	private String mProductImage;
	
	private long mQuantity;
	
	private double mRetailPrice;
	
	private String mSku;
	
	public String getConfigId() {
		return mConfigId;
	}
	public void setConfigId(String pConfigId) {
		mConfigId = pConfigId;
	}
	public double getCost() {
		return mCost;
	}
	public void setCost(double pCost) {
		mCost = pCost;
	}
	public Date getEstimatedShipDate() {
		return mEstimatedShipDate;
	}
	public void setEstimatedShipDate(Date pEstimatedShipDate) {
		mEstimatedShipDate = pEstimatedShipDate;
	}
	public boolean isHasInstallation() {
		return mHasInstallation;
	}
	public void setHasInstallation(boolean pHasInstallation) {
		mHasInstallation = pHasInstallation;
	}
	public String getProductDesc() {
		return mProductDesc;
	}
	public void setProductDesc(String pProductDesc) {
		mProductDesc = pProductDesc;
	}
	public String getProductImage() {
		return mProductImage;
	}
	public void setProductImage(String pProductImage) {
		mProductImage = pProductImage;
	}
	public long getQuantity() {
		return mQuantity;
	}
	public void setQuantity(long pQuantity) {
		mQuantity = pQuantity;
	}
	public double getRetailPrice() {
		return mRetailPrice;
	}
	public void setRetailPrice(double pRetailPrice) {
		mRetailPrice = pRetailPrice;
	}
	public String getSku() {
		return mSku;
	}
	public void setSku(String pSku) {
		mSku = pSku;
	}
}
