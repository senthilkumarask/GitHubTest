package com.bbb.cmo.vo;


/**
 * Created by acer on 8/26/2014.
 */
public class LineItemVO {


    private String mConfigId;
    private double mCost;
    private String mProductDescription;
    private long mQuantity;
    private double mRetailPrice;
    private String mSku;
    private String mVendorLeadTime;
    private double mDeliveryFee;
    

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

    public String getProductDescription() {
        return mProductDescription;
    }

    public void setProductDescription(String pProductDescription) {
        mProductDescription = pProductDescription;
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

    public String getVendorLeadTime() {
        return mVendorLeadTime;
    }

    public void setVendorLeadTime(String pVendorLeadTime) {
        mVendorLeadTime = pVendorLeadTime;
    }

	public double getDeliveryFee() {
		return mDeliveryFee;
	}

	public void setDeliveryFee(double pDeliveryFee) {
		mDeliveryFee = pDeliveryFee;
	}
    
	@Override
	public String toString() {
		return "LineItemVO [mConfigId=" + mConfigId + ", mCost="
				+ mCost + ", mProductDescription=" + mProductDescription
				+ ", mQuantity=" + mQuantity
				+ ", mRetailPrice=" + mRetailPrice
				+ ", mSku=" + mSku + ", mVendorLeadTime=" + mVendorLeadTime
				+ ", mDeliveryFee=" + mDeliveryFee + "]";
	}
}