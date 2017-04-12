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
import com.fedex.ws.ship.v15.ProcessShipmentRequest;
/**
 * This class is used to create a ProcessShipmentReqVo object
 * 
 */
public class ProcessShipmentReqVo extends ServiceRequestBase {
	
	/**
	 * To hold Service Name
	 * 
	 */
	private String serviceName="processShipment";
	
	/**
	 * To get Service Name
	 * 
	 */
	public String getServiceName() {
		return serviceName;
	}
	

	/**
	 * To set Service Name
	 * 
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * To Get ProcessShipmentRequest
	 * 
	 * @return processShipmentRequest
	 */
	public ProcessShipmentRequest getProcessShipmentRequest() {
		return processShipmentRequest;
	}
	
	/**
	 * To Set ProcessShipmentRequest
	 * @param processShipmentRequest
	 */
	public void setProcessShipmentRequest(
			ProcessShipmentRequest processShipmentRequest) {
		this.processShipmentRequest = processShipmentRequest;
	}
	
	/**
	 * To set ProcessShipmentRequest Object
	 * 
	 */
	private ProcessShipmentRequest processShipmentRequest;



}
