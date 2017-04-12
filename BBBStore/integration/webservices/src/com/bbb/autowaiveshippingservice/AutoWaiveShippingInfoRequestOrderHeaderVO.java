package com.bbb.autowaiveshippingservice;


import java.io.Serializable;

public class AutoWaiveShippingInfoRequestOrderHeaderVO implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 6752569170957873234L;

	private String userToken;
	
	private String applicationName;
	
	private String hostName;

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	
	
	
}
