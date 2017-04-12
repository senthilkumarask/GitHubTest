package com.bbb.selfservice.tibco.api;

import java.io.StringWriter;
import java.util.GregorianCalendar;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.tellafriend.Main;
import com.bbb.framework.jaxb.tellafriend.PERSONALDETAILS;
import com.bbb.framework.jaxb.tellafriend.TypeEnum;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.selfservice.tibco.vo.TellAFriendVO;

public class TellAFriendMsgMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		
		TellAFriendVO tellAFriendVO = (TellAFriendVO) pReqVO;
		PERSONALDETAILS sender = new PERSONALDETAILS();
		PERSONALDETAILS recipient = new PERSONALDETAILS();
		
		Main mainContact = new Main();
		mainContact.setTYPE(TypeEnum
				.fromValue(tellAFriendVO.getType().toString()));
		sender.setEMAILADDR(tellAFriendVO.getSenderEmailAddr());
		sender.setFIRSTNM(tellAFriendVO.getSenderFirstName());
		sender.setLASTNM(tellAFriendVO.getSenderLastName());
		
		recipient.setEMAILADDR(tellAFriendVO.getRecipientEmailAddr());
		recipient.setFIRSTNM(tellAFriendVO.getRecipientFirstName());
		recipient.setLASTNM(tellAFriendVO.getRecipientLastName());
		
		mainContact.setREQUESTFROM(sender);
		mainContact.setREQUESTTO(recipient);
		mainContact.setEMAILCOPY(tellAFriendVO.isEmailCopy());
		mainContact.setSITEFLAG(tellAFriendVO.getSiteId());
		
		XMLGregorianCalendar xmlGregorianCalendar;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar());
			mainContact.setSUBMITDT(xmlGregorianCalendar);
		} catch (DatatypeConfigurationException e) {			
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1331,e.getMessage(),
					e);
		}
		
		StringWriter stringWriter = null;
		try {
			stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(Main.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(mainContact, stringWriter);
			String contactUSXml = stringWriter.getBuffer().toString();
			TextMessage txtMessage = (TextMessage) pMessage;
			txtMessage.setText(contactUSXml);
		} catch (JMSException jmsException) {
			throw new BBBSystemException(jmsException.getErrorCode(),jmsException.getMessage(),
					jmsException);

		} catch (PropertyException propertyException) {
			throw new BBBSystemException(propertyException.getErrorCode(),propertyException.getMessage(),
					propertyException);
		} catch (JAXBException jaxbException) {
			throw new BBBSystemException(jaxbException.getErrorCode(),jaxbException.getMessage(),
					jaxbException);
		} finally {
			if (stringWriter != null) {
				stringWriter = null;
			}
		}
	}
}
