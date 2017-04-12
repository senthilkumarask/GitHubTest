package com.bbb.selfservice.tibco.api;


import java.io.StringWriter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.contactus.CONTACTUS;
import com.bbb.framework.jaxb.contactus.Main;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.selfservice.tibco.vo.ContactUsVO;


public class ContactUSMsgMarshaller extends MessageMarshaller {

	
	private static final long serialVersionUID = 1L;
	

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		
		ContactUsVO contactUsVO = (ContactUsVO)pReqVO;
		
		CONTACTUS contactus  = new CONTACTUS();
		
		contactus.setCATEGORY(contactUsVO.getSubjectCategory());
		contactus.setMESSAGE(contactUsVO.getEmailMessage());
		contactus.setFIRSTNM(contactUsVO.getFirstName());
		contactus.setLASTNM(contactUsVO.getLastName());
		contactus.setCONTACTTYPE(contactUsVO.getContactType());
		contactus.setPHONENUM(contactUsVO.getPhoneNumber());
		contactus.setPHONEEXT(contactUsVO.getPhoneExt());
		contactus.setBESTTIMETOCALL(contactUsVO.getTimeCall());
		contactus.setTIMEZONE(contactUsVO.getTimeZone());
		contactus.setEMAILADDR(contactUsVO.getEmail());
		contactus.setORDERNUMBER(contactUsVO.getOrderNumber());
		contactus.setGENDER(contactUsVO.getGender());
		contactus.setSUBMITDT(contactUsVO.getSubmitDate());
		contactus.setSITEFLAG(contactUsVO.getSiteFlag());
		
		Main mainContact = new Main();
		mainContact.getDATARECORD().add(contactus);
		StringWriter stringWriter= null;
		
		try{			
			stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(Main.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(mainContact, stringWriter);		
			String contactUSXml = stringWriter.getBuffer().toString();            
			TextMessage txtMessage = (TextMessage) pMessage;						
			txtMessage.setText(contactUSXml);		
		}catch (JMSException jmsException) {
			throw new BBBSystemException(jmsException.getErrorCode(),jmsException.getMessage(), jmsException);
			
		}catch (PropertyException propertyException) {
			throw new BBBSystemException(propertyException.getErrorCode(),propertyException.getMessage(), propertyException  );
		}catch (JAXBException jaxbException) {
			throw new BBBSystemException(jaxbException.getErrorCode(),jaxbException.getMessage(), jaxbException  );
		}finally{
			    
			if(stringWriter != null){
				stringWriter =null;
			 }
		}

		
		
	}
	
	
}
