package com.bbb.internationalshipping.vo.orderconfirmation;

import com.bbb.framework.integration.ServiceRequestBase;

/**
 *
 */

public class BBBInternationalShippingOrderConfReq extends ServiceRequestBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private String merchantOrderId;
	/**
	 * 
	 */
	private String orderId;
	
	/** The service name. */
	private String serviceName;
	/**
	 * @return the merchantOrderId
	 */
	public final String getMerchantOrderId() {
		return merchantOrderId;
	}
	/**
	 * @param merchantOrderId the merchantOrderId to set
	 */
	public final void setMerchantOrderId(final String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}
	/**
	 * @return the orderId
	 */
	public final String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public final void setOrderId(final String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the serviceName
	 */
	public final String getServiceName() {
		return serviceName;
	}
	/**
	 * @param serviceName the serviceName to set
	 */
	public final void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}
	
	
}
