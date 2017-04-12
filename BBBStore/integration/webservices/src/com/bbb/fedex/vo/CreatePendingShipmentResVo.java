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

import com.fedex.ws.openship.v7.CreateOpenShipmentReply;

/**
 * This class is used to create a CreatePendingShipmentResVo object
 * 
 */
public class CreatePendingShipmentResVo extends ServiceResponseBase {
	
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * To Get CreateOpenShipmentReply
	 * @return CreateOpenShipmentReply
	 */
	public CreateOpenShipmentReply getCreateOpenShipmentReply() {
		return createOpenShipmentReply;
	}

	/**
	 * to set CreateOpenShipmentReply Object
	 * @param createOpenShipmentReply -to set CreateOpenShipmentReply Object
	 */
	public void setCreateOpenShipmentReply(
			CreateOpenShipmentReply createOpenShipmentReply) {
		this.createOpenShipmentReply = createOpenShipmentReply;
	}
	
	/**
	 * To hold CreateOpenShipmentReply
	 */
	private CreateOpenShipmentReply createOpenShipmentReply;


}
