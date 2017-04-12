/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 18-Feb-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.commerce.order.paypal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.common.BBBGenericService;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;


/**
 * @author aban13
 *
 */
public class BBBPayPalSessionBean extends BBBGenericService {
	
	private BBBAddressPPVO address;
	private List<String> errorList;
	private boolean isInternationalOrPOError;
	private boolean internationalError;
	private BBBGetExpressCheckoutDetailsResVO getExpCheckoutResponse;
	private boolean sessionBeanNull;
	private boolean validateOrderAddress;
	private boolean fromPayPalPreview;
	private String porchServiceErrorMsg;
	
	private Map<String, Long> oldCartMap;
	
	public Map<String, Long> getOldCartMap() {
		if(this.oldCartMap==null)
			this.oldCartMap = new HashMap<String, Long>();
		return oldCartMap;
	}

	public void setOldCartMap(Map<String, Long> oldCartMap) {
		this.oldCartMap = oldCartMap;
	}

	public boolean isFromPayPalPreview() {
		return this.fromPayPalPreview;
	}

	public void setFromPayPalPreview(boolean fromPayPalPreview) {
		this.fromPayPalPreview = fromPayPalPreview;
	}

	public boolean isValidateOrderAddress() {
		return this.validateOrderAddress;
	}

	public void setValidateOrderAddress(boolean validateOrderAddress) {
		this.validateOrderAddress = validateOrderAddress;
	}
	
	public BBBGetExpressCheckoutDetailsResVO getGetExpCheckoutResponse() {
		return this.getExpCheckoutResponse;
	}
	public void setGetExpCheckoutResponse(
			BBBGetExpressCheckoutDetailsResVO getExpCheckoutResponse) {
		this.getExpCheckoutResponse = getExpCheckoutResponse;
	}
	public List<String> getErrorList() {
		return this.errorList;
	}
	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	public BBBAddressPPVO getAddress() {
		return this.address;
	}
	public void setAddress(BBBAddressPPVO address) {
		this.address = address;
	}
	public boolean isInternationalOrPOError() {
		return this.isInternationalOrPOError;
	}
	public void setInternationalOrPOError(boolean isInternationalOrPOError) {
		this.isInternationalOrPOError = isInternationalOrPOError;
	}

	
	public boolean getSessionBeanNull() {
		this.address = null;
		this.errorList = null;
		this.isInternationalOrPOError = false;
		this.internationalError = false;
		this.sessionBeanNull = true;
		this.oldCartMap = null;
		return this.sessionBeanNull;
	}

	public boolean isInternationalError() {
		return internationalError;
	}

	public void setInternationalError(boolean internationalError) {
		this.internationalError = internationalError;
	}

	/**
	 * @return the porchServiceErrorMsg
	 */
	public String getPorchServiceErrorMsg() {
		return porchServiceErrorMsg;
	}

	/**
	 * @param porchServiceErrorMsg the porchServiceErrorMsg to set
	 */
	public void setPorchServiceErrorMsg(String porchServiceErrorMsg) {
		this.porchServiceErrorMsg = porchServiceErrorMsg;
	}
}
