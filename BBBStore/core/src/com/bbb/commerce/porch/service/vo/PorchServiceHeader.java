/**
 * 
 */
package com.bbb.commerce.porch.service.vo;

/**
 * @author sm0191
 *
 */
public class PorchServiceHeader {
	
	
	private String userToken;
	
	private String applicationName;
	
	private String hostName;
	
	private String dataCenterName;

	/**
	 * @return the userToken
	 */
	public String getUserToken() {
		return userToken;
	}

	/**
	 * @param userToken the userToken to set
	 */
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	/**
	 * @return the applicationName
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @return the dataCenterName
	 */
	public String getDataCenterName() {
		return dataCenterName;
	}

	/**
	 * @param dataCenterName the dataCenterName to set
	 */
	public void setDataCenterName(String dataCenterName) {
		this.dataCenterName = dataCenterName;
	}
	
	

}
