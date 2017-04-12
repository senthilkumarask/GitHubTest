/*
 *
 * File  : TestMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;

/**
 * Test marshaller to test Messaging framework
 * 
 * @author manohar
 * @version 1.0
 */
public class TestMarshaller extends MessageMarshaller{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5176246451725359288L;

	@Override
	public void marshall(ServiceRequestIF reqVO, Message message)
			throws BBBSystemException, BBBBusinessException{
		
		TextMessage txtMessage = (TextMessage) message;
		try {
			txtMessage.setText("This is test marshaller setting the string for JMS framework testing");
		} catch (JMSException e) {
			throw new BBBSystemException(e.getErrorCode(),e.getMessage(), e);
		}
		// TODO Auto-generated method stub
	}
}
