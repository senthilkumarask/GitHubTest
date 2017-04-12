package com.bbb.selfservice.tibco.api;

import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.edwData.EDWProfileDataVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.EDWProfileData.ATGProfileDataInput;
import com.bbb.framework.jaxb.EDWProfileData.ObjectFactory;
import com.bbb.framework.messaging.MessageMarshaller;

public class EDWProfileDataMsgMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;
	
	private JAXBContext context;
	
	private Marshaller marshaller;
	
	public EDWProfileDataMsgMarshaller() throws BBBSystemException {
	    try {            
			context = JAXBContext.newInstance(ATGProfileDataInput.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (PropertyException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1329,e.getMessage(), e);
        } catch (JAXBException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1329,e.getMessage(), e);
        }
	}

	/*
	 * (non-Javadoc) To marshal the EDWProfileDataVO Object which contains
	 * list of EDW Data attributes 
	 * 
	 * @see
	 * com.bbb.framework.messaging.MessageMarshaller#marshall(com.bbb.framework
	 * .integration.ServiceRequestIF, javax.jms.Message)
	 */
	public String marshall(ServiceRequestIF pReqVO) throws BBBSystemException, BBBBusinessException {
		String xmlMessage = null;
		EDWProfileDataVO edwProfileDataVO = (EDWProfileDataVO) pReqVO;
		StringWriter stringWriter = null;
		try {
		List<String> edwAttributes = edwProfileDataVO.getProfileEDWData().getEdwProfileAttributes();
		ObjectFactory objectFactory = new ObjectFactory();
		ATGProfileDataInput edwProfileDataInput = objectFactory.createATGProfileDataInput();
		ATGProfileDataInput.Header edwHeader = objectFactory.createATGProfileDataInputHeader();
		
		//set header value
		edwHeader.setApplicationName(BBBCoreConstants.EDW_APP_NAME);
		edwHeader.setUserToken(edwProfileDataVO.getProfileEDWData().getUserToken());
		edwHeader.setHostname(InetAddress.getLocalHost().getHostName());
		edwHeader.setDataCenterName(edwProfileDataVO.getProfileEDWData().getDataCentre());
	
		ATGProfileDataInput.Body edwBody  = objectFactory.createATGProfileDataInputBody();
		if (edwAttributes != null && edwAttributes.size() > 0) {
			  ATGProfileDataInput.Body.Attributes attributes = objectFactory.createATGProfileDataInputBodyAttributes();	
				attributes.getAttributeName().addAll(edwAttributes);
				edwBody.setAttributes(attributes);
		}
		edwBody.setATGProfileID(edwProfileDataVO.getProfileEDWData().getATGProfileID());
		edwBody.setEmailAddress(edwProfileDataVO.getProfileEDWData().getEmail());
		edwProfileDataInput.setBody(edwBody);
		edwProfileDataInput.setHeader(edwHeader);
		
		
			context = JAXBContext.newInstance(ATGProfileDataInput.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			stringWriter = new StringWriter();
			marshaller.marshal(edwProfileDataInput, stringWriter);
			xmlMessage = stringWriter.getBuffer().toString();
			
		} catch (PropertyException propertyException) {
			// System.out.println(propertyException);
			throw new BBBSystemException(propertyException.getErrorCode(),propertyException.getMessage(), propertyException);
		} catch (JAXBException jaxbException) {
			// System.out.println(jaxbException);
			throw new BBBSystemException(jaxbException.getErrorCode(),jaxbException.getMessage(), jaxbException);
		} catch (UnknownHostException jaxbException) {
			// System.out.println(jaxbException);
			throw new BBBSystemException(jaxbException.getMessage(), jaxbException);
		} finally {

			if (stringWriter != null) {
				stringWriter = null;
			}
		}
		
		return xmlMessage;
	
		}

}
