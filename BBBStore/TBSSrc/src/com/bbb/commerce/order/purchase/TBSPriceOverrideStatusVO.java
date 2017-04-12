package com.bbb.commerce.order.purchase;

import java.util.List;

public class TBSPriceOverrideStatusVO {
	
	private String mSuccess;
	private String mCommerceItemId;
	private String mShippingGroupId;
	private int mOverrideQty;
	private double mOverridePrice;
	private List<TBSPriceOverrideErrorMessageVO> mErrorMessages;
	
	public TBSPriceOverrideStatusVO(){
		//default constructor
	}
	
	public TBSPriceOverrideStatusVO(String pSuccess){
		this.mSuccess = pSuccess;
	}

	public TBSPriceOverrideStatusVO(String pSuccess, String pCommerceItemId){
		this.mSuccess = pSuccess;
		this.mCommerceItemId = pCommerceItemId;
	}
	
	public String getSuccess() {
		return mSuccess;
	}
	public void setSuccess(String pSuccess) {
		this.mSuccess = pSuccess;
	}		

	public String getCommerceItemId() {
		return mCommerceItemId;
	}

	public void setCommerceItemId(String pCommerceItemId) {
		this.mCommerceItemId = pCommerceItemId;
	}	

	public String getShippingGroupId() {
		return mShippingGroupId;
	}

	public void setShippingGroupId(String pShippingGroupId) {
		this.mShippingGroupId = pShippingGroupId;
	}

	public int getOverrideQty() {
		return mOverrideQty;
	}

	public void setOverrideQty(int pOverrideQty) {
		this.mOverrideQty = pOverrideQty;
	}

	public double getOverridePrice() {
		return mOverridePrice;
	}

	public void setOverridePrice(double pOverridePrice) {
		this.mOverridePrice = pOverridePrice;
	}

	public List<TBSPriceOverrideErrorMessageVO> getErrorMessages() {
		return mErrorMessages;
	}

	public void setErrorMessages(List<TBSPriceOverrideErrorMessageVO> pErrorMessages) {
		this.mErrorMessages = pErrorMessages;
	}

}
