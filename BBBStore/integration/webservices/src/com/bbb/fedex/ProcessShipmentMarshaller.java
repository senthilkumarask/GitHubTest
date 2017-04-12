/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetAccountInfoMarshaller.java
 *
 *  DESCRIPTION: GetAccountInfoMarshaller  marshall's the get account information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.fedex;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.fedex.vo.ProcessShipmentReqVo;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.fedex.ws.ship.v15.ProcessShipmentRequest;
import com.fedex.ws.ship.v15.ProcessShipmentRequestDocument;

public class ProcessShipmentMarshaller extends RequestMarshaller {

	
	private static final long serialVersionUID = 1L;

	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
			logDebug("ProcessShipmentMarshaller | buildRequest | starts");
		
		BBBPerformanceMonitor.start("ProcessShipmentMarshaller-buildRequest");
		
		ProcessShipmentRequestDocument  processShipmentDocument = null;
		try {
			processShipmentDocument = ProcessShipmentRequestDocument.Factory.newInstance();
			processShipmentDocument.setProcessShipmentRequest(getDozerMappedResponse(pReqVO));
		} finally {
			BBBPerformanceMonitor.end("ProcessShipmentMarshaller-buildRequest");
		}
			logDebug("ProcessShipmentMarshaller | buildRequest | ends");
		return processShipmentDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private ProcessShipmentRequest getDozerMappedResponse(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("ProcessShipmentMarshaller.getDozerMappedResponse method start");
		
		BBBPerformanceMonitor
				.start("ProcessShipmentMarshaller.getDozerMappedResponse");
		ProcessShipmentReqVo  psReqVo= (ProcessShipmentReqVo)pReqVO;
		ProcessShipmentRequest psR = psReqVo.getProcessShipmentRequest();
			
		logDebug("ProcessShipmentMarshaller.getDozerMappedResponse method ends");
		BBBPerformanceMonitor
		.end("ProcessShipmentMarshaller.getDozerMappedResponse");
		return psR;
	}
}
