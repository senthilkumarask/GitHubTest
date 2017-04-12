/*
 *
 * File  : MsgHandlerFactoryIF.java
 * Project:     BBB
 */
package com.bbb.framework.integration.handlers;

import com.bbb.framework.messaging.MessageMarshaller;

/**
 * Factory which provides message marshaller through getMsgMarshaller
 * 
 * @author manohar
 * @version 1.0
 */
public interface MsgHandlerFactoryIF {
	
	/**
	 * Gets the service marshaller.
	 * 
	 * @param service the service
	 * 
	 * @return the service marshaller
	 * 
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public MessageMarshaller getMessageMarshaller(String service) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

}
