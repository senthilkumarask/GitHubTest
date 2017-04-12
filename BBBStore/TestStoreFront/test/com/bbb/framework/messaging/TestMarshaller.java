package com.bbb.framework.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;

public class TestMarshaller extends MessageMarshaller{
	@Override
	public void marshall(ServiceRequestIF reqVO, Message message)
			throws BBBSystemException, BBBBusinessException{
		
		TextMessage txtMessage = (TextMessage) message;
		try {
			txtMessage.setText("This is test marshaller setting the string for JMS framework testing");
		} catch (JMSException e) {
			throw new BBBSystemException(e.getMessage(), e.getCause());
		}
		// TODO Auto-generated method stub
	}
}
