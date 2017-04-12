/**
 * --------------------------------------------------------------------------------
 * Copyright 2015 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Sapinent
 *
 * Created on: 12/23/2015
 * --------------------------------------------------------------------------------
 */
package com.bbb.account.wallet;

import com.bbb.framework.integration.ServiceRequestBase;

public class BBBGetProfileRequest extends ServiceRequestBase {

	/**
	 * Default generated serial version.
	 */
	private static final long serialVersionUID = 1L;
	private String userToken, walletId, serviceName;
	
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getWalletId() {
		return walletId;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	@Override
	public String toString() {
		return "GetProfileReq [userToken=" + userToken + ", walletId="
				+ walletId + ", serviceName=" + serviceName + "]";
	}
	
}
