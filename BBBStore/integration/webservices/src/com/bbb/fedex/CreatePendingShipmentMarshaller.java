/*
 *  Copyright 2014, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CreatePendingShipmentReqBuilder.java
 *
 *  DESCRIPTION: to build email tag request for Fedex Service
 *
 *  HISTORY:
 *  12/19/2014 Initial version
 *
 */
package com.bbb.fedex;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.fedex.vo.CreatePendingShipmentReqVo;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.fedex.ws.openship.v7.CreateOpenShipmentRequest;
import com.fedex.ws.openship.v7.CreatePendingShipmentRequestDocument;

/**
 * This class is used to create a Marshaller object
 * 
 */
public class CreatePendingShipmentMarshaller extends RequestMarshaller {

	
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.bbb.framework.webservices.RequestMarshaller#buildRequest(com.bbb.framework.integration.ServiceRequestIF)
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
			logDebug("CreatePendingShipmentMarshaller | buildRequest | starts");
		
		BBBPerformanceMonitor.start("CreatePendingShipmentMarshalle-buildRequest");
		
		CreatePendingShipmentRequestDocument  processShipmentDocument = null;
		try {
			processShipmentDocument = CreatePendingShipmentRequestDocument.Factory.newInstance();
			processShipmentDocument.setCreatePendingShipmentRequest(getDozerMappedResponse(pReqVO));
			//String str = processShipmentDocument.
			//processShipmentDocument.getDomNode().getAttributes().getNamedItem("")
		} finally {
			BBBPerformanceMonitor.end("ProcessShipmentMarshaller-buildRequest");
		}
			logDebug("ProcessShipmentMarshaller | buildRequest | ends");
		return processShipmentDocument;

	}

	
	/**
	 * This method is used request object
	 * @param pReqVO - request object
	 * @return CreateOpenShipmentRequest - return request object
	 * @throws BBBSystemException
	 */
	private CreateOpenShipmentRequest getDozerMappedResponse(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("ProcessShipmentMarshaller.getDozerMappedResponse method start");
		
		BBBPerformanceMonitor
				.start("ProcessShipmentMarshaller.getDozerMappedResponse");
		CreatePendingShipmentReqVo  psReqVo= (CreatePendingShipmentReqVo)pReqVO;
		CreateOpenShipmentRequest psR = psReqVo.getCreateOpenShipmentRequest();
			
		logDebug("ProcessShipmentMarshaller.getDozerMappedResponse method ends");
		BBBPerformanceMonitor
		.end("ProcessShipmentMarshaller.getDozerMappedResponse");

		return psR;
	}
}
