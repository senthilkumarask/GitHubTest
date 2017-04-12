/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetAccountInfoMarshaller.java
 *
 *  DESCRIPTION: GetAccountInfoUnMarshaller  un-marshall's the get account information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.fedex;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBSystemException;
import com.bbb.fedex.vo.ProcessShipmentResVo;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.fedex.ws.ship.v15.ProcessShipmentReply;
import com.fedex.ws.ship.v15.ProcessShipmentReplyDocument;

public class ProcessShipmentUnMarshaller extends ResponseUnMarshaller {

	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {
		ProcessShipmentResVo resGVo = new ProcessShipmentResVo();
		ProcessShipmentReplyDocument processReplyDocument = (ProcessShipmentReplyDocument) pResponseDocument;
		ProcessShipmentReply psR = processReplyDocument.getProcessShipmentReply();
		resGVo.setProcessShipmentReply(psR);
		ServiceResponseIF srF = resGVo;
		return srF;
	}
}
