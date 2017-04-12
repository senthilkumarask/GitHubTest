/*
 *
 * File  : MsgMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging.handlers;

import java.util.Map;

import com.bbb.framework.integration.handlers.MsgHandlerFactoryIF;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * Not used
 * 
 * @author manohar
 * @version 1.0
 */
public class MsgHandlerFactory implements MsgHandlerFactoryIF {

	private Map<String, String> msgMarshallerMap;
	
	public Map<String, String> getMsgMarshallerMap() {
		return msgMarshallerMap;
	}

	public void setMsgMarshallerMap(Map<String, String> msgMarshallerMap) {
		this.msgMarshallerMap = msgMarshallerMap;
	}

	@Override
	public MessageMarshaller getMessageMarshaller(String service)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		
		BBBPerformanceMonitor
		.start("MsgHandlerFactory-getMsgMarshaller");
		
		MessageMarshaller marshaller = null;
    	Class<?> marshallerClass = Class.forName(getMsgMarshallerMap().get(service));
    	marshaller = (MessageMarshaller) marshallerClass.newInstance();
    	
		BBBPerformanceMonitor
		.end("MsgHandlerFactory-getMsgMarshaller");
		return marshaller;
	}
}
