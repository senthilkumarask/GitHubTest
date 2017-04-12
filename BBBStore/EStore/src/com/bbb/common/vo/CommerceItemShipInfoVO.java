package com.bbb.common.vo;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.json.JSONException;
import atg.json.JSONObject;

import com.bbb.account.api.BBBAddressVO;
import com.bbb.constants.BBBCoreConstants;


public class CommerceItemShipInfoVO implements Serializable {

	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mAddressId;
	 private String mCommerceItemId;
	 private String mShippingGroupType;	 
	 private String mShippingMethod;
	 private String mShippingGroupName;
	 private String storeId;

	 private String mPackAndHoldDate;
	 private String mGiftMessage;
	 private boolean mGiftWrap;
	 private boolean mGiftingFlag;
	 
	 Address bbbAddress = null;

	 
	 public String getAddressId() {
		return mAddressId;
	}

	public void setAddressId(String addressId) {
		this.mAddressId = addressId;
	}

	public String getCommerceItemId() {
		return mCommerceItemId;
	}

	public void setCommerceItemId(String mCommerceItemId) {
		this.mCommerceItemId = mCommerceItemId;
	}

	public String getShippingGroupType() {
		return mShippingGroupType;
	}

	public void setShippingGroupType(String mShippingGroupType) {
		this.mShippingGroupType = mShippingGroupType;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getShippingMethod() {
		return mShippingMethod;
	 }

	 public void setShippingMethod(String shippingMethod) {
		this.mShippingMethod = shippingMethod;
	 }

	 public String getShippingGroupName() {
		return mShippingGroupName;
	 }

	 public void setShippingGroupName(String shippingGroupName) {
		this.mShippingGroupName = shippingGroupName;
	 }

	public Address getBbbAddress() {
		if(bbbAddress==null) {
			bbbAddress = new BBBAddressVO();
		}
		return bbbAddress;
	}

	public void setBbbAddress(Address bbbAddress) {
		this.bbbAddress = bbbAddress;
	}

	public void copyFromJsonObject(String jsonString) {
		
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			setCommerceItemId((String)jsonObject.get("commerceItemId"));
			setShippingGroupType((String)jsonObject.get("commerceItemId"));
			setShippingMethod((String)jsonObject.get("commerceItemId"));
			setStoreId((String)jsonObject.get("storeId"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getPackAndHoldDate() {
		return mPackAndHoldDate;
	}

	public void setPackAndHoldDate(String mPackAndHoldDate) {
		this.mPackAndHoldDate = mPackAndHoldDate;
	}

	public String getGiftMessage() {
		if (!StringUtils.isEmpty(mGiftMessage))
		{
			try {
				mGiftMessage=URLDecoder.decode(mGiftMessage, BBBCoreConstants.UTF_ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return mGiftMessage;
	}

	public void setGiftMessage(String mGiftMessage) {
		this.mGiftMessage = mGiftMessage;
	}

	public boolean getGiftWrap() {
		return mGiftWrap;
	}

	public void setGiftWrap(boolean mGiftWrap) {
		this.mGiftWrap = mGiftWrap;
	}

	public boolean getGiftingFlag() {
		return mGiftingFlag;
	}

	public void setGiftingFlag(boolean mGiftingFlag) {
		this.mGiftingFlag = mGiftingFlag;
	}
}
