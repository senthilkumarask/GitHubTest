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
import paypalapi.api.ebay.DoAuthorizationResponseDocument;
import paypalapi.api.ebay.DoAuthorizationResponseType;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.paypal.BBBDoAuthorizationResVO;
import eblbasecomponents.apis.ebay.AckCodeType.Enum;
import eblbasecomponents.apis.ebay.ErrorType;

/**
 * This class contain methods used for unmarshalling the webservice response
 * from PayPal.
 * 
 * @author ssh108
 * 
 */
public class DoAuthorizationUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */

	@Override
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument) throws BBBSystemException {

		
		logDebug("Entry processResponse of DoAuthorizationUnMarshaller with ServiceRequestIF object:");
		
		BBBPerformanceMonitor.start("DoAuthorizationUnMarshaller-processResponse");

		BBBDoAuthorizationResVO bbbDoAuthRes = new BBBDoAuthorizationResVO();
		ErrorStatus errorStatus = new ErrorStatus();
		errorStatus.setErrorExists(false);
		DoAuthorizationResponseDocument authDoc = (DoAuthorizationResponseDocument) pResponseDocument;

		DoAuthorizationResponseType response = authDoc.getDoAuthorizationResponse();

		final String transactionId = response.getTransactionID();

		final Enum status = response.getAck();

		if (status.toString().equalsIgnoreCase(BBBCoreConstants.SUCCESS)) {
			bbbDoAuthRes.setTransactionId(transactionId);
			bbbDoAuthRes.setAck(status.toString());
			bbbDoAuthRes.setAuthTimeStamp(response.getTimestamp().getTime());
			bbbDoAuthRes.setCorrelationID(response.getCorrelationID());
			bbbDoAuthRes.setProtectionEligibility(response.getAuthorizationInfo().getProtectionEligibility());
		} else {
			bbbDoAuthRes.setWebServiceError(true);
			final ErrorType[] errorType = response.getErrorsArray();
			for (ErrorType type : errorType) {
				errorStatus.setDisplayMessage(type.getShortMessage());
				errorStatus.setErrorId(Integer.parseInt(type.getErrorCode()));
				errorStatus.setErrorExists(true);
				errorStatus.setErrorMessage(type.getLongMessage());
			}
			
		}
		bbbDoAuthRes.setErrorStatus(errorStatus);
		BBBPerformanceMonitor.end("DoAuthorizationUnMarshaller-processResponse");
		logDebug("Exit processResponse of DoAuthorizationUnMarshaller with XmlObject object:");
		
		return bbbDoAuthRes;

	}

}