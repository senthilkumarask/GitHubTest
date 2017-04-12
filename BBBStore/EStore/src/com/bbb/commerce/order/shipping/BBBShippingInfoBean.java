package com.bbb.commerce.order.shipping;

import java.io.Serializable;
import java.util.Date;

import com.bbb.commerce.common.BBBAddress;



public class BBBShippingInfoBean implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBAddress mAddress;
    private String mShippingMethod;
    private boolean mRequiresGiftPacking;
    private Date mPackAndHoldDate;
    private boolean mSendShippingConfirmation;
    private String mShippingConfirmationEmail;
    private String mShippingGroupId;
    private String mRegistryId;
    private String mRegistryInfo;
    
    private String mGiftMessage;
    private boolean mGiftWrap;
    private boolean mGiftingFlag;
    
    public BBBAddress getAddress() {
        return mAddress;
    }
    public void setAddress(BBBAddress pAddress) {
        mAddress = pAddress;
    }
    public String getShippingMethod() {
        return mShippingMethod;
    }
    public void setShippingMethod(String pShippingMethod) {
        mShippingMethod = pShippingMethod;
    }
    public final String getGiftMessage() {
        return mGiftMessage;
    }
    public final boolean isRequiresGiftPacking() {
        return mRequiresGiftPacking;
    }
    public final Date getPackAndHoldDate() {
        return mPackAndHoldDate;
    }
    public final boolean isSendShippingConfirmation() {
        return mSendShippingConfirmation;
    }
    public final String getShippingConfirmationEmail() {
        return mShippingConfirmationEmail;
    }
    public final String getShippingGroupId() {
        return mShippingGroupId;
    }
    public final void setGiftMessage(String pGiftMessage) {
        mGiftMessage = pGiftMessage;
    }
    public final void setRequiresGiftPacking(boolean pRequiresGiftPacking) {
        mRequiresGiftPacking = pRequiresGiftPacking;
    }
    public final void setPackAndHoldDate(Date pPackAndHoldDate) {
        mPackAndHoldDate = pPackAndHoldDate;
    }
    public final void setSendShippingConfirmation(boolean pSendShippingConfirmation) {
        mSendShippingConfirmation = pSendShippingConfirmation;
    }
    public final void setShippingConfirmationEmail(
            String pShippingConfirmationEmail) {
        mShippingConfirmationEmail =pShippingConfirmationEmail;
    }
    public final void setShippingGroupId(String pShippingGroupId) {
        mShippingGroupId = pShippingGroupId;
    }
	/**
	 * @return the mGiftWrap
	 */
	public boolean getGiftWrap() {
		return mGiftWrap;
	}
	/**
	 * @param pGiftWrap the mGiftWrap to set
	 */
	public void setGiftWrap(boolean pGiftWrap) {
		mGiftWrap = pGiftWrap;
	}
    public String getRegistryId() {
        return mRegistryId;
    }
    public void setRegistryId(String registryId) {
        this.mRegistryId = registryId;
    }
    public String getRegistryInfo() {
        return mRegistryInfo;
    }
    public void setRegistryInfo(String registryInfo) {
        this.mRegistryInfo = registryInfo;
    }
	public boolean isGiftingFlag() {
		return mGiftingFlag;
	}
	public void setGiftingFlag(boolean pGiftingFlag) {
		mGiftingFlag = pGiftingFlag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BBBShippingInfoBean [mAddress=" + mAddress
				+ ", mShippingMethod=" + mShippingMethod
				+ ", mRequiresGiftPacking=" + mRequiresGiftPacking
				+ ", mPackAndHoldDate=" + mPackAndHoldDate
				+ ", mSendShippingConfirmation=" + mSendShippingConfirmation
				+ ", mShippingConfirmationEmail=" + mShippingConfirmationEmail
				+ ", mShippingGroupId=" + mShippingGroupId + ", mRegistryId="
				+ mRegistryId + ", mRegistryInfo=" + mRegistryInfo
				+ ", mGiftMessage=" + mGiftMessage + ", mGiftWrap=" + mGiftWrap
				+ ", mGiftingFlag=" + mGiftingFlag + "]";
	}
	
	
}
