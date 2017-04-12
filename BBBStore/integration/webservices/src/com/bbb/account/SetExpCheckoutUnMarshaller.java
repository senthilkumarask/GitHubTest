/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 18-Feb-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

import org.apache.xmlbeans.XmlObject;

import paypalapi.api.ebay.SetExpressCheckoutResponseDocument;
import paypalapi.api.ebay.SetExpressCheckoutResponseType;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.paypal.BBBSetExpressCheckoutResVO;

import eblbasecomponents.apis.ebay.AckCodeType.Enum;
import eblbasecomponents.apis.ebay.ErrorType;

/**
 * This class contain methods used for unmarshalling the webservice response from PayPal.
 * 
 * @author ssh108
 * 
 */
public class SetExpCheckoutUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	/* (non-Javadoc)
	 * @see com.bbb.framework.webservices.ResponseUnMarshaller#processResponse(org.apache.xmlbeans.XmlObject)
	 */
	@Override
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument) throws BBBSystemException {

		
		logDebug("Entry processResponse of SetExpCheckoutUnMarshaller with ServiceRequestIF object:");
	
		BBBPerformanceMonitor.start("SetExpCheckoutUnMarshaller-processResponse");
		BBBSetExpressCheckoutResVO setExpRes = new BBBSetExpressCheckoutResVO();

		ErrorStatus errorStatus = new ErrorStatus();
		SetExpressCheckoutResponseDocument detDoc = (SetExpressCheckoutResponseDocument) pResponseDocument;
		SetExpressCheckoutResponseType response = detDoc.getSetExpressCheckoutResponse();
		final String token = response.getToken();

		final Enum status = response.getAck();

		final String correlationID = response.getCorrelationID();

		if (status.toString().equalsIgnoreCase(BBBCoreConstants.SUCCESS)) {
			setExpRes.setToken(token);
			setExpRes.setCorrelationID(correlationID);
			setExpRes.setAck(status.toString());
		} else {
			setExpRes.setWebServiceError(true);
			final ErrorType[] errorType = response.getErrorsArray();
			for (ErrorType type : errorType) {
				errorStatus.setDisplayMessage(type.getShortMessage());
				errorStatus.setErrorId(Integer.parseInt(type.getErrorCode()));
				errorStatus.setErrorExists(true);
				errorStatus.setErrorMessage(type.getLongMessage());
			}
			setExpRes.setErrorStatus(errorStatus);
		}

		BBBPerformanceMonitor.end("SetExpCheckoutUnMarshaller-processResponse");
		
		logDebug("Exit processResponse of SetExpCheckoutUnMarshaller with XmlObject object:");
		
		return setExpRes;

	}

}