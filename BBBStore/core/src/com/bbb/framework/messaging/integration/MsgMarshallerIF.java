/*
 *
 * File  : MsgMarshallerIF.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging.integration;

import java.io.Serializable;

import javax.jms.Message;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;

/**
 * The Interface MsgMarshallerIF.
 * 
 * 
 * @version 1.0
 */
public interface MsgMarshallerIF extends Serializable {
	
	/**
	 * 
	 */
	public String marshall(ServiceRequestIF reqVO) throws BBBSystemException, BBBBusinessException;
	
	/**
	 * Prepares reqVo and Message objects with message that needs to be sent
	 * 
	 * @param reqVO
	 * @param message
	 */
	public void marshall(ServiceRequestIF reqVO, Message message) throws BBBSystemException, BBBBusinessException;
	
}