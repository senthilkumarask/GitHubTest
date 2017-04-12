/**
 * 
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
import javax.xml.datatype.XMLGregorianCalendar;

import com.bbb.commerce.porch.service.vo.PorchServiceHeader;
import com.bbb.commerce.porch.service.vo.PorchServiceOrderVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.porchOrder.Order;
import com.bbb.framework.jaxb.porchOrder.Order.Header;
import com.bbb.framework.messaging.MessageMarshaller;

/**
 * @author sm0191
 *
 */
public class PorchOrdersReportMsgMarshaller extends MessageMarshaller {



	private static final long serialVersionUID = 1L;

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		
		PorchServiceOrderVO serviceOrderVO = (PorchServiceOrderVO) pReqVO;
		Order porchTibcoOrderReq = new Order();
		Header porchOrderHeader = new Header();
		
		PorchServiceHeader serviceHeader = serviceOrderVO.getHeader();
		porchOrderHeader.setUserToken(serviceHeader.getUserToken());
		porchOrderHeader.setApplicationName(serviceHeader.getApplicationName());
		porchOrderHeader.setHostname(serviceHeader.getHostName());
		porchOrderHeader.setDataCenterName(serviceHeader.getDataCenterName());
		
		porchTibcoOrderReq.setHeader(porchOrderHeader);
		
		
		XMLGregorianCalendar xmlGregorianCalendar;
		try {
			xmlGregorianCalendar = javax.xml.datatype.DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar());
			porchTibcoOrderReq.setCreationDate(xmlGregorianCalendar);
		} catch (DatatypeConfigurationException e) {			
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1331,e.getMessage(),
					e);
		}
		
		  
		porchTibcoOrderReq.setServiceName(serviceOrderVO.getServiceName());
		porchTibcoOrderReq.setJSONData(serviceOrderVO.getJsonData());
		StringWriter stringWriter = null;
		try {
			stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(Order.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(porchTibcoOrderReq, stringWriter);
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
