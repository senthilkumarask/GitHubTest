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
import com.bbb.framework.jaxb.subscription.FrequencyEnum;
import com.bbb.framework.jaxb.subscription.Main;
import com.bbb.framework.jaxb.subscription.SUBSCRIPTION;
import com.bbb.framework.jaxb.subscription.TypeEnum;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.selfservice.tibco.vo.SubscriptionVO;


public class SubscriptionMsgMarshaller extends MessageMarshaller {

	
	private static final long serialVersionUID = 1L;
	

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		
		SubscriptionVO subscriptionVO = (SubscriptionVO)pReqVO;
		SUBSCRIPTION subscription = new SUBSCRIPTION();
		
		subscription.setTYPE(TypeEnum.fromValue(subscriptionVO.getType().toString()));
		if(subscriptionVO.getFrequency() != null)
		{
			subscription.setFREQUENCY(FrequencyEnum.fromValue(subscriptionVO.getFrequency().toString()));
		}
		subscription.setEMAILADDR(subscriptionVO.getEmailAddr());
		subscription.setEMAILADDRCONFIRM(subscriptionVO.getConfirmEmailAddr());
		subscription.setSALUTATION(subscriptionVO.getSalutation());
		subscription.setFIRSTNM(subscriptionVO.getFirstName());
		subscription.setLASTNM(subscriptionVO.getLastName());
		subscription.setADDRESSLINE1(subscriptionVO.getAddressLine1());
		subscription.setADDRESSLINE2(subscriptionVO.getAddressLine2());
		subscription.setCITY(subscriptionVO.getCity());
		subscription.setSTATE(subscriptionVO.getState());
		subscription.setZIPCODE(subscriptionVO.getZipcode());
		subscription.setPHONENUM(subscriptionVO.getPhoneNumber());
		subscription.setMOBILENUM(subscriptionVO.getMobileNumber());
		if(subscriptionVO.getEmailOffer())
		{
			subscription.setEMAILOFFER(subscriptionVO.getEmailOffer());
		}
		if(subscriptionVO.getEmailOffer())
		{
			subscription.setDIRECTMAILOFFER(subscriptionVO.getDirectMailOffer());
		}
		if(subscriptionVO.getEmailOffer())
		{
			subscription.setMOBILEOFFER(subscriptionVO.getMobileOffer());
		}
		subscription.setSITEID(subscriptionVO.getSiteId());
		XMLGregorianCalendar xmlGregorianCalendar;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
			subscription.setDATETIME(xmlGregorianCalendar);
			} catch (DatatypeConfigurationException e) {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1330,e.getMessage(),e);
				
		}
		
		
		Main mainContact = new Main();
		mainContact.getDATARECORD().add(subscription);
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
