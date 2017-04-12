/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Akhajuria
 *
 * Created on: 11-April-2012
 * --------------------------------------------------------------------------------
 */

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

import com.bbb.account.baby.BABYBOOKREQUEST;
import com.bbb.account.baby.Main;
import com.bbb.account.baby.TypeEnum;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.selfservice.tibco.vo.BabyBookVO;

/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class BabyBookMsgMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;
	
	/**
	 * This method do marshing activities for TIBCO services of Baby Book request
	 * 
	 * @param pReqVO 
	 * @param pMessage 
	 * @return Void
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		
		BabyBookVO BabyBookVO = (BabyBookVO) pReqVO;
		BABYBOOKREQUEST BabyBook = new BABYBOOKREQUEST();

		BabyBook.setTYPE(TypeEnum.fromValue(BabyBookVO.getType().toString()));

		BabyBook.setEMAILADDR(BabyBookVO.getEmailAddr());
		BabyBook.setFIRSTNM(BabyBookVO.getFirstName());
		BabyBook.setLASTNM(BabyBookVO.getLastName());
		BabyBook.setADDR1(BabyBookVO.getAddressLine1());
		BabyBook.setADDR2(BabyBookVO.getAddressLine2());
		BabyBook.setCITY(BabyBookVO.getCity());
		BabyBook.setSTATECD(BabyBookVO.getState());
		BabyBook.setZIP(BabyBookVO.getZipcode());
		BabyBook.setDAYPHONE(BabyBookVO.getPhoneNumber());
		// BabyBook.setEVENTDATE((XMLGregorianCalendar)BabyBookVO.getEventDate());
		BabyBook.setEMAILOFFER(BabyBookVO.getEmailOffer());
		
		BabyBook.setSITEFLAG(BabyBookVO.getSiteId());		
		XMLGregorianCalendar submitDate;		
		try {
			if (BabyBookVO.getEventDate() != null) {
				XMLGregorianCalendar eventDate;
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTimeInMillis(BabyBookVO.getEventDate().getTime());
				eventDate = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(gc);
				BabyBook.setEVENTDATE(eventDate);
			}
			submitDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					new GregorianCalendar());
			BabyBook.setSUBMITDT(submitDate);
		} catch (DatatypeConfigurationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1326,e.getMessage(),
					e);
		}

		Main mainContact = new Main();
		mainContact.setDATARECORD(BabyBook);
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
