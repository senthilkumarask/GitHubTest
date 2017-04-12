/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Prashanth Kumar Bhoomula
 *
 * Created on: 05-July-2012
 * --------------------------------------------------------------------------------
 */

package com.bbb.selfservice.tibco.api;

import java.io.StringWriter;
import java.util.GregorianCalendar;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.healthywoman.AddressType;
import com.bbb.framework.jaxb.healthywoman.HealthyWomanType;
import com.bbb.framework.jaxb.healthywoman.ObjectFactory;
import com.bbb.framework.jaxb.healthywoman.PersonType;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.selfservice.tibco.vo.HWRegistrationVO;
import com.bbb.utils.BBBUtility;

/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class HWRegistrationMsgMarshaller extends MessageMarshaller {

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
		
		HWRegistrationVO hwRegistrationVO = (HWRegistrationVO) pReqVO;
		
		final ObjectFactory msgObjFactory = new ObjectFactory();

		HealthyWomanType hwType = msgObjFactory.createHealthyWomanType();
		hwType.setEmailOffer(hwRegistrationVO.getEmailOffer());
		hwType.setEmail(hwRegistrationVO.getEmailAddr());
		hwType.setSource(hwRegistrationVO.getSiteId());
	    try {
	    	GregorianCalendar gc = new GregorianCalendar();
	    	//gc.setTimeInMillis(hwRegistrationVO.getSubmitDate().getTime());
			hwType.setRegisterDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
		} catch (DatatypeConfigurationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1328,e.getMessage(),e);
		}
		PersonType person = msgObjFactory.createPersonType();
		if(!BBBUtility.isEmpty(hwRegistrationVO.getFirstName())) {
			person.setFirstName(hwRegistrationVO.getFirstName());
		}
		if(!BBBUtility.isEmpty(hwRegistrationVO.getLastName())) {
			person.setLastName(hwRegistrationVO.getLastName());
		}
		hwType.setPerson(person);
		
		AddressType address = msgObjFactory.createAddressType();
		if(!BBBUtility.isEmpty(hwRegistrationVO.getAddressLine1())) {
			address.setLine1(hwRegistrationVO.getAddressLine1());
		}
		if(!BBBUtility.isEmpty(hwRegistrationVO.getAddressLine2())) {
			address.setLine2(hwRegistrationVO.getAddressLine2());
		}
		if(!BBBUtility.isEmpty(hwRegistrationVO.getCity())) {
			address.setCity(hwRegistrationVO.getCity());
		}
		if(!BBBUtility.isEmpty(hwRegistrationVO.getState())) {
			address.setState(hwRegistrationVO.getState());
		}	
		if(!BBBUtility.isEmpty(hwRegistrationVO.getZipcode())) {
			address.setZip(hwRegistrationVO.getZipcode());
		}
		hwType.setAddress(address);
	    StringWriter stringWriter = null;		
		try {
			final JAXBElement<HealthyWomanType> hwRoot = msgObjFactory.createHealthyWoman(hwType);    	
		    stringWriter = new StringWriter();
		    final JAXBContext context = JAXBContext.newInstance(HealthyWomanType.class);
		    final Marshaller marshaller = context.createMarshaller();
		    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    marshaller.marshal(hwRoot, stringWriter);
		    final String hwXmlMessage = stringWriter.getBuffer().toString();
			TextMessage txtMessage = (TextMessage) pMessage;
			txtMessage.setText(hwXmlMessage);		    
		} catch (JMSException jmsException) {
			throw new BBBSystemException(jmsException.getErrorCode(),jmsException.getMessage(),jmsException);
		} catch (PropertyException propertyException) {
			throw new BBBSystemException(propertyException.getErrorCode(),propertyException.getMessage(), propertyException);
		} catch (JAXBException jaxbException) {
			throw new BBBSystemException(jaxbException.getErrorCode(),jaxbException.getMessage(), jaxbException);
		} finally {
			if (stringWriter != null) {
				stringWriter = null;
			}
		}
	}

}
