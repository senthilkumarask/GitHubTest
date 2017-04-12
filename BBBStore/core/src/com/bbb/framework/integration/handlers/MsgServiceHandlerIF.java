/*
 *
 * File  : MsgServiceHandlerIF.java
 * Project:     BBB
 */
package com.bbb.framework.integration.handlers;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;

/**
 * Interface for all Messaging services
 * 
 * @author manohar
 * @version 1.0
 */
public interface MsgServiceHandlerIF {
	
	/**
	 * Sends messages to the queue/topic mentioned by the service
	 * 
	 * @param service
	 * @param voReq
	 * @return
	 * @throws Exception
	 */
	public void send(final String service, final ServiceRequestIF voReq) throws BBBBusinessException, BBBSystemException;
	
	/**
	 * Sends messages to the queue/topic mentioned by the service
	 * 
	 * @param service
	 * @param voReq
	 * @return
	 * @throws Exception
	 */
	public void sendTextMessage(final String service, final ServiceRequestIF voReq) throws BBBBusinessException, BBBSystemException;

}
