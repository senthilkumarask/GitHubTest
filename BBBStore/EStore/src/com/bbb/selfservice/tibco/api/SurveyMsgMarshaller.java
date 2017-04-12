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
import com.bbb.framework.jaxb.survey.Main;
import com.bbb.framework.jaxb.survey.SURVEY;
import com.bbb.framework.messaging.MessageMarshaller;

import com.bbb.selfservice.tibco.vo.SurveyVO;

public class SurveyMsgMarshaller extends MessageMarshaller {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	public void marshall(ServiceRequestIF pReqVO, Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		
		SurveyVO surveyVO = (SurveyVO)pReqVO;
		
		SURVEY survey  = new SURVEY();
		
		survey.setSUBMITDT(surveyVO.getSubmitDate());
		survey.setNAME(surveyVO.getUserName());
		survey.setSENDEMAIL(surveyVO.getSendEmail());
		survey.setSHOPPEDATBEFORE(surveyVO.getShoppedAtBefore());
		survey.setFEATURES(surveyVO.getFeatures());
		survey.setFAVORITE(surveyVO.getFavorite());
		survey.setCOMMENTS(surveyVO.getComments());
		survey.setEMAILADDR(surveyVO.getEmailAddress());
		survey.setGENDER(surveyVO.getGender());
		survey.setAGE(surveyVO.getAge());
		survey.setLOCATION(surveyVO.getLocation());
		survey.setSITEFLAG(surveyVO.getSiteFlag());
		
		Main mainSurvey = new Main();
		mainSurvey.getDATARECORD().add(survey);
		StringWriter stringWriter= null;
		// create JAXB context and instantiate marshaller
	
		try{
			
			stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(Main.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(mainSurvey, stringWriter);
		
			String surveyXml = stringWriter.getBuffer().toString();
     		TextMessage txtMessage = (TextMessage) pMessage;
			txtMessage.setText(surveyXml);
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
