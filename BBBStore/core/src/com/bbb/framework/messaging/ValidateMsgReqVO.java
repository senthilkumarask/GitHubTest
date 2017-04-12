/*
 *
 * File  : ValidateMsgReqVO.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging;

import com.bbb.framework.integration.ServiceRequestBase;

/**
 * Test request vo to test Messaging framework
 * 
 * @author manohar
 * @version 1.0
 */
public class ValidateMsgReqVO extends ServiceRequestBase{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getServiceName(){
		return "testMessage";
	}

}
