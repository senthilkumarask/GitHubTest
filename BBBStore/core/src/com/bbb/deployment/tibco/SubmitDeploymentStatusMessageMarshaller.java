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

package com.bbb.deployment.tibco;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.deployment.tibco.DeploymentStatusVO;
import com.bbb.deployment.vo.SubmitDeploymentStatusVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.messaging.MessageMarshaller;


/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class SubmitDeploymentStatusMessageMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;
	private JAXBContext context;
	private Marshaller marshaller;
	
	/**
	 * This method do marshing activities for TIBCO services of Baby Book request
	 * 
	 * @param pReqVO 
	 * @param pMessage 
	 * @return Void
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public String marshall(ServiceRequestIF pReqVO)
			throws BBBSystemException, BBBBusinessException {
		
		String deploymentStatusXml = null;
		SubmitDeploymentStatusVO submitDeploymentStatusVO = (SubmitDeploymentStatusVO) pReqVO; 
		
		DeploymentStatusVO deploymentStatusVO = new DeploymentStatusVO();
		deploymentStatusVO = submitDeploymentStatusVO.getDeploymentStatusVO();
		StringWriter stringWriter = null;

		try {
			deploymentStatusXml = getDeploymentStatusAsXml(deploymentStatusVO);
		} finally {

			if (stringWriter != null) {
				stringWriter = null;
			}
		}
		return deploymentStatusXml;
	}
	
	private String getDeploymentStatusAsXml(com.bbb.deployment.tibco.DeploymentStatusVO vo) throws BBBSystemException
	{
		StringWriter stringWriter = null;
		String deploymentStatusXml = null;
		try {
			stringWriter = new StringWriter();
			context = JAXBContext.newInstance(com.bbb.deployment.tibco.DeploymentStatusVO.class);
			marshaller = context.createMarshaller();
			marshaller.marshal(vo, stringWriter);
			deploymentStatusXml = stringWriter.getBuffer().toString();
		} catch (JAXBException jaxbException) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1059,jaxbException.getMessage(), jaxbException);
		}	
		return deploymentStatusXml;
	}
}
