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

import com.bbb.exception.BBBSystemException;
import com.bbb.fedex.vo.CreatePendingShipmentResVo;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.fedex.ws.openship.v7.CreateOpenShipmentReply;
import com.fedex.ws.openship.v7.CreatePendingShipmentReplyDocument;

/**
 * This class is used to create a UnMarshaller object
 * 
 */
public class CreatePendingShipmentUnMarshaller extends ResponseUnMarshaller {

	/**
	 * to hold serialVersionUID
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.bbb.framework.webservices.ResponseUnMarshaller#processResponse(org.apache.xmlbeans.XmlObject)
	 */
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {
		CreatePendingShipmentResVo resGVo = new CreatePendingShipmentResVo();
		CreatePendingShipmentReplyDocument processReplyDocument = (CreatePendingShipmentReplyDocument) pResponseDocument;
		CreateOpenShipmentReply csr = processReplyDocument.getCreatePendingShipmentReply();
		resGVo.setCreateOpenShipmentReply(csr);
		ServiceResponseIF srF = resGVo;
		return srF;
	}
}
