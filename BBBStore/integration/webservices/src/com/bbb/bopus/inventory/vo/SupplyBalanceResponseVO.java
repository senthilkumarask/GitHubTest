package com.bbb.bopus.inventory.vo;

/**
 * @author skoner
 * 
 */
public class SupplyBalanceResponseVO extends
		com.bbb.framework.integration.ServiceResponseBase {

	/**
	 * It will contain the response string of supplyBalance operation
	 */
	private String response;

	/**
	 * @return response
	 */
	public String getResponse() {
		return response;
	}

	/**
	 * @param response
	 */
	public void setResponse(final String response) {
		this.response = response;
	}

}
