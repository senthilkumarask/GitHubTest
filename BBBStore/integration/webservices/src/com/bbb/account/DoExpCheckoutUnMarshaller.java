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

import paypalapi.api.ebay.DoExpressCheckoutPaymentResponseDocument;
import paypalapi.api.ebay.DoExpressCheckoutPaymentResponseType;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.paypal.BBBDoExpressCheckoutPaymentResVO;

import eblbasecomponents.apis.ebay.AckCodeType.Enum;
import eblbasecomponents.apis.ebay.ErrorType;

/**
 * This class contain methods used for unmarshalling the webservice response
 * from PayPal.
 * 
 * @author ssh108
 * 
 */
public class DoExpCheckoutUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */

	@Override
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument) throws BBBSystemException {

		
		logDebug("Entry processResponse of DoExpCheckoutUnMarshaller with ServiceRequestIF object:");
		
		BBBPerformanceMonitor.start("DoExpCheckoutUnMarshaller-processResponse");
		BBBDoExpressCheckoutPaymentResVO doExpRes = new BBBDoExpressCheckoutPaymentResVO();

		ErrorStatus errorStatus = new ErrorStatus();
		errorStatus.setErrorExists(false);
		DoExpressCheckoutPaymentResponseDocument doExpDoc = (DoExpressCheckoutPaymentResponseDocument) pResponseDocument;

		DoExpressCheckoutPaymentResponseType response = doExpDoc.getDoExpressCheckoutPaymentResponse();

		final String correlationID = response.getCorrelationID();

		final Enum status = response.getAck();

		if (status.toString().equalsIgnoreCase(BBBCoreConstants.SUCCESS)) {
			final String transactionId = response.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfoArray(0).getTransactionID();
			final String protectionElig = response.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfoArray(0).getProtectionEligibility();
			doExpRes.setTransactionId(transactionId);
			doExpRes.setCorrelationID(correlationID);
			doExpRes.setAck(status.toString());
			doExpRes.setProtectionElig(protectionElig);
			doExpRes.setOrderTimestamp(response.getTimestamp().getTime());
		} else {
			doExpRes.setWebServiceError(true);
			final ErrorType[] errorType = response.getErrorsArray();
			for (ErrorType type : errorType) {
				errorStatus.setDisplayMessage(type.getShortMessage());
				errorStatus.setErrorId(Integer.parseInt(type.getErrorCode()));
				errorStatus.setErrorExists(true);
				errorStatus.setErrorMessage(type.getLongMessage());
			}

		}
		doExpRes.setErrorStatus(errorStatus);
		BBBPerformanceMonitor.end("DoExpCheckoutUnMarshaller-processResponse");
		
		logDebug("Exit processResponse of DoExpCheckoutUnMarshaller with XmlObject object:");
		
		return doExpRes;

	}

}