/**
 * BBBGetExpressCheckoutDetailsReq.java
 *
 * Project:     BBB
 */

package com.bbb.paypal;

import com.bbb.framework.integration.ServiceRequestBase;

/**
 * @author ssh108
 *
 */

public class BBBGetExpressCheckoutDetailsReq extends ServiceRequestBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BBBGetExpressCheckoutDetailsRequestType getGetExpressCheckoutDetailsRequest;
	
	private String serviceName;
	
	private BBBPayPalCredentials cred;
	
	/**
	 * @return the getGetExpressCheckoutDetailsRequest
	 */
	public BBBGetExpressCheckoutDetailsRequestType getGetGetExpressCheckoutDetailsRequest() {
		return getGetExpressCheckoutDetailsRequest;
	}

	/**
	 * @param getGetExpressCheckoutDetailsRequest the getGetExpressCheckoutDetailsRequest to set
	 */
	public void setGetGetExpressCheckoutDetailsRequest(BBBGetExpressCheckoutDetailsRequestType getGetExpressCheckoutDetailsRequest) {
		this.getGetExpressCheckoutDetailsRequest = getGetExpressCheckoutDetailsRequest;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the cred
	 */
	public BBBPayPalCredentials getCred() {
		return cred;
	}

	/**
	 * @param cred the cred to set
	 */
	public void setCred(BBBPayPalCredentials cred) {
		this.cred = cred;
	}

}
