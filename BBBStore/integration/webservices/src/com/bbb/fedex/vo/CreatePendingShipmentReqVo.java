/*
 *  Copyright 2014, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CreatePendingShipmentReqBuilder.java
 *
 *  DESCRIPTION: to build email tag request for Fedex Service
 *
 *  HISTORY:
 *  12/19/2014 Initial version
 *
 */
package com.bbb.fedex.vo;

import com.bbb.framework.integration.ServiceRequestBase;
import com.fedex.ws.openship.v7.CreateOpenShipmentRequest;

/**
 * This class is used to create a request object
 * 
 */
public class CreatePendingShipmentReqVo extends ServiceRequestBase{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * to hold service name
	 */
	private String serviceName="createPendingShipment";
	
	/* (non-Javadoc)
	 * @see com.bbb.framework.integration.ServiceRequestBase#getServiceName()
	 */
	public String getServiceName() {
		return serviceName;
	}
	
	/**
	 * To Get service Name
	 * @param serviceName - return service name
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * to hold createOpenShipmentRequest
	 */
	CreateOpenShipmentRequest createOpenShipmentRequest;

	/**
	 * to get CreateOpenShipmentRequest object
	 * @return createOpenShipmentRequest
	 */
	public CreateOpenShipmentRequest getCreateOpenShipmentRequest() {
		return createOpenShipmentRequest;
	}
	
	
	/**
	 * to set CreateOpenShipmentRequest
	 * @param createOpenShipmentRequest
	 */
	public void setCreateOpenShipmentRequest(
			CreateOpenShipmentRequest createOpenShipmentRequest) {
		this.createOpenShipmentRequest = createOpenShipmentRequest;
	}
	
	
}
