/*
 *
 * File  : MessageMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging;

import javax.jms.Message;

import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.messaging.integration.MsgMarshallerIF;

/**
 * The Class MessageMarshaller. Anything common for all message marshallers
 * would go into this class
 * 
 * 
 * @version 1.0
 */
public class MessageMarshaller extends BBBGenericService implements MsgMarshallerIF {

	// TODO: MsgMarshaller - Things that are required to go in
	// message header should be provided by the static methods in this class.
	// Need to code this class, once the structure of request XML is forzen.
	/**
	 * 
	 */
	private static final long serialVersionUID = -3675504005014591821L;

	@Override
	public void marshall(ServiceRequestIF reqVO, Message message) throws BBBSystemException, BBBBusinessException {
		//overriden method, do nothing
	}

	@Override
	public String marshall(ServiceRequestIF pReqVO) throws BBBSystemException, BBBBusinessException {
		return null;
	}

}
