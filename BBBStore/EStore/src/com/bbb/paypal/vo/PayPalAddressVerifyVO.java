package com.bbb.paypal.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.paypal.BBBAddressPPVO;

/**
 */
public class PayPalAddressVerifyVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean success;
	private String redirectUrl;
	private List<String> addressErrorList;
	private BBBAddressPPVO address;
	private boolean isInternationalOrPOError;
	private boolean internationalError;
	private String redirectState;
	private boolean isShipingGroupChanged;
	
	public String getRedirectState() {
		return this.redirectState;
	}
	public void setRedirectState(String redirectState) {
		this.redirectState = redirectState;
	}
	public boolean isInternationalOrPOError() {
		return this.isInternationalOrPOError;
	}
	public void setInternationalOrPOError(boolean isInternationalOrPOError) {
		this.isInternationalOrPOError = isInternationalOrPOError;
	}

	public List<String> getAddressErrorList() {
		return this.addressErrorList;
	}
	public void setAddressErrorList(List<String> addressErrorList) {
		this.addressErrorList = addressErrorList;
	}
	public boolean isSuccess() {
		return this.success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getRedirectUrl() {
		return this.redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public BBBAddressPPVO getAddress() {
		return this.address;
	}
	public void setAddress(BBBAddressPPVO address) {
		this.address = address;
	}
	public boolean isShipingGroupChanged() {
		return this.isShipingGroupChanged;
	}
	public void setShipingGroupChanged(boolean isShipingGroupChanged) {
		this.isShipingGroupChanged = isShipingGroupChanged;
	}
	public boolean isInternationalError() {
		return internationalError;
	}
	public void setInternationalError(boolean internationalError) {
		this.internationalError = internationalError;
	}
}
