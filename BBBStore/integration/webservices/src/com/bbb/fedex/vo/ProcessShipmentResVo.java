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

import com.bbb.framework.integration.ServiceResponseBase;
import com.fedex.ws.ship.v15.ProcessShipmentReply;
/**
 * This class is used to create a ProcessShipmentResVo object
 * 
 */
public class ProcessShipmentResVo extends ServiceResponseBase {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This class is used to create a ProcessShipmentReply object
	 * 
	 */
	private ProcessShipmentReply processShipmentReply;

	/**
	 * To Get ProcessShipmentReply
	 * @return ProcessShipmentReply
	 */
	public ProcessShipmentReply getProcessShipmentReply() {
		return processShipmentReply;
	}

	/**
	 * To Set ProcessShipmentReply Object
	 * @param processShipmentReply - to set processShipmentReply Object
	 */
	public void setProcessShipmentReply(ProcessShipmentReply processShipmentReply) {
		this.processShipmentReply = processShipmentReply;
	}
	

}
