package com.bbb.account.webservices.vo;

import java.io.Serializable;

public class GenericProfileRequestVO  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appId;

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}

