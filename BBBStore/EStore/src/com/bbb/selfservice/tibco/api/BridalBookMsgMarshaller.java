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
import com.bbb.framework.jaxb.bridalbook.BRIDALBOOKREQUEST;
import com.bbb.framework.jaxb.bridalbook.Main;
import com.bbb.framework.jaxb.bridalbook.TypeEnum;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.selfservice.tibco.vo.BridalBookVO;

public class BridalBookMsgMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {

		BridalBookVO bridalBookVO = (BridalBookVO) pReqVO;
		BRIDALBOOKREQUEST bridalBook = new BRIDALBOOKREQUEST();

		bridalBook.setTYPE(TypeEnum
				.fromValue(bridalBookVO.getType().toString()));

		bridalBook.setEMAILADDR(bridalBookVO.getEmailAddr());
		bridalBook.setFIRSTNM(bridalBookVO.getFirstName());
		bridalBook.setLASTNM(bridalBookVO.getLastName());
		bridalBook.setADDR1(bridalBookVO.getAddressLine1());
		bridalBook.setADDR2(bridalBookVO.getAddressLine2());
		bridalBook.setCITY(bridalBookVO.getCity());
		bridalBook.setSTATECD(bridalBookVO.getState());
		bridalBook.setZIP(bridalBookVO.getZipcode());
		bridalBook.setDAYPHONE(bridalBookVO.getPhoneNumber());
		// bridalBook.setEVENTDATE((XMLGregorianCalendar)bridalBookVO.getEventDate());
		bridalBook.setEMAILOFFER(bridalBookVO.getEmailOffer());
		
		bridalBook.setSITEFLAG(bridalBookVO.getSiteId());		
		XMLGregorianCalendar submitDate;		
		try {
			if (bridalBookVO.getEventDate() != null) {
				XMLGregorianCalendar eventDate;
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTimeInMillis(bridalBookVO.getEventDate().getTime());
				eventDate = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(gc);
				bridalBook.setEVENTDATE(eventDate);
			}
			submitDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					new GregorianCalendar());
			bridalBook.setSUBMITDT(submitDate);
		} catch (DatatypeConfigurationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1327,e.getMessage(),
					e);
		}

		Main mainContact = new Main();
		mainContact.setDATARECORD(bridalBook);
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
			throw new BBBSystemException(propertyException.getMessage(),
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
