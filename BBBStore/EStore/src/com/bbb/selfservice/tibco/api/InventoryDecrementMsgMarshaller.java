package com.bbb.selfservice.tibco.api;

import java.io.StringWriter;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.bbb.framework.jaxb.inventory.BodyType;
import com.bbb.framework.jaxb.inventory.DC;
import com.bbb.framework.jaxb.inventory.DT;
import com.bbb.framework.jaxb.inventory.ESBHeaderType;
import com.bbb.framework.jaxb.inventory.EnvelopeType;
import com.bbb.framework.jaxb.inventory.HeaderElement;
import com.bbb.framework.jaxb.inventory.InventoryDataCenterDecrement;
import com.bbb.framework.jaxb.inventory.SiteId;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.bbb.commerce.inventory.InventoryDecrementVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.inventory.InventoryDecrement;
import com.bbb.framework.jaxb.inventory.ObjectFactory;
import com.bbb.framework.messaging.MessageMarshaller;

public class InventoryDecrementMsgMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;
	
	private JAXBContext context;
	
	private Marshaller marshaller;
	
	public InventoryDecrementMsgMarshaller() throws BBBSystemException {
	    try {            
			context = JAXBContext.newInstance(InventoryDataCenterDecrement.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (PropertyException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1329,e.getMessage(), e);
        } catch (JAXBException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1329,e.getMessage(), e);
        }
	}

	/*
	 * (non-Javadoc) To marshal the InventoryDecrementVO Object which contains
	 * list of Inventory VO Objects
	 * 
	 * @see
	 * com.bbb.framework.messaging.MessageMarshaller#marshall(com.bbb.framework
	 * .integration.ServiceRequestIF, javax.jms.Message)
	 */
	public String marshall(ServiceRequestIF pReqVO) throws BBBSystemException, BBBBusinessException {
		String xmlMessage = null;
		
		JAXBElement<EnvelopeType> envelopeType = setXMLelements(pReqVO);
		
		StringWriter stringWriter = null;
		try {
			context = JAXBContext.newInstance(EnvelopeType.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);	
			stringWriter = new StringWriter();
			marshaller.marshal(envelopeType, stringWriter);
			xmlMessage = stringWriter.getBuffer().toString();
			
		} catch (PropertyException propertyException) {
			// System.out.println(propertyException);
			throw new BBBSystemException(propertyException.getErrorCode(),propertyException.getMessage(), propertyException);
		} catch (JAXBException jaxbException) {
			// System.out.println(jaxbException);
			throw new BBBSystemException(jaxbException.getErrorCode(),jaxbException.getMessage(), jaxbException);
		} finally {

			if (stringWriter != null) {
				stringWriter = null;
			}
		}
		
		return xmlMessage;
	}

	private JAXBElement<EnvelopeType> setXMLelements(ServiceRequestIF pReqVO) throws BBBSystemException {
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		InventoryDecrementVO inventoryDecrementVO = (InventoryDecrementVO) pReqVO;
		List<InventoryVO> inventoryVOs = inventoryDecrementVO.getListOfInventoryVos();
		InventoryDataCenterDecrement inventoryDcDecrement = objectFactory.createInventoryDataCenterDecrement();
		EnvelopeType envelopeType = objectFactory.createEnvelopeType();
		ESBHeaderType esbHeaderType = objectFactory.createESBHeaderType();
		
		esbHeaderType.setConversationId(inventoryDecrementVO.getConversationId());
		esbHeaderType.setMessageCreateTS(inventoryDecrementVO.getMessageCreateTS());
		esbHeaderType.setMessageFormat(inventoryDecrementVO.getMessageFormat());
		esbHeaderType.setMessageId(inventoryDecrementVO.getMessageId());
		esbHeaderType.setMessagePriority(inventoryDecrementVO.getMessagePriority());
		esbHeaderType.setPayLoadType(inventoryDecrementVO.getPayLoad());
		esbHeaderType.setProducer(inventoryDecrementVO.getProducer());
		esbHeaderType.setCorrelationId(inventoryDecrementVO.getCoRelationId());
		esbHeaderType.setConsumer(inventoryDecrementVO.getConsumer());
		HeaderElement headerElement = objectFactory.createHeaderElement();		
		headerElement.setDataCenter(DC
				.fromValue(inventoryDecrementVO.getDataCenter().toString()));
		headerElement.setSite(SiteId
				.fromValue(inventoryDecrementVO.getSite().toString()));
		XMLGregorianCalendar orderSubmissionDate;
		try{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(inventoryDecrementVO.getOrderSubmissionDate().getTime());
		orderSubmissionDate = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(gc);
		headerElement.setDate(orderSubmissionDate);
		}
		catch (DatatypeConfigurationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1327,e.getMessage(),
					e);
		}
		
		inventoryDcDecrement.setHeader(headerElement);

		InventoryDecrement inventoryDecrement = objectFactory.createInventoryDecrement();
		if (inventoryVOs != null && inventoryVOs.size() > 0) {
			for (int i = 0; i < inventoryVOs.size(); i++) {
				InventoryDecrement.Item item =objectFactory.createInventoryDecrementItem();
				item.setSku(inventoryVOs.get(i).getSkuID());
				item.setQty(Long.toString(inventoryVOs.get(i).getOrderedQuantity()));
				item.setDeliveryType(DT
				.fromValue(inventoryVOs.get(i).getDeliveryType().toString()));

				inventoryDecrement.getItem().add(item);
			}
		}
		inventoryDcDecrement.setItemList(inventoryDecrement);
		
		
		BodyType bodyType = new BodyType();
		bodyType.setInvenDataDCDecrement(inventoryDcDecrement);
		
		envelopeType.setESBHeader(esbHeaderType);
		envelopeType.setBody(bodyType);
		
		JAXBElement<EnvelopeType> type = objectFactory.createBBBYEnvelope(envelopeType);
		return type;
	}

}
