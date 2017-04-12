/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: ssh108
 *
 * Created on: 21-Mar-2014
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

import java.text.DecimalFormat;

import org.apache.xmlbeans.XmlObject;

import paypalapi.api.ebay.DoAuthorizationReqDocument;
import paypalapi.api.ebay.DoAuthorizationReqDocument.DoAuthorizationReq;
import paypalapi.api.ebay.DoAuthorizationRequestDocument;
import paypalapi.api.ebay.DoAuthorizationRequestType;
import paypalapi.api.ebay.RequesterCredentialsDocument;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.paypal.BBBDoAuthorizationReq;
import com.bbb.paypal.BBBPayPalCredentials;

import corecomponenttypes.apis.ebay.BasicAmountType;
import eblbasecomponents.apis.ebay.CurrencyCodeType;
import eblbasecomponents.apis.ebay.CustomSecurityHeaderType;
import eblbasecomponents.apis.ebay.UserIdPasswordType;

/**
 * The class is the marshaller class which takes the payPal requestVo and
 * creates a XML request for the Webservice to call The class will require a
 * request VO object which will contain the request parameters for the XML
 * request
 * 
 * @author ssh108
 * 
 */
public class DoAuthorizationMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BBBPayPalCredentials cred;

	/**
	 * The method is extension of the RequestMarshaller service which will take
	 * the request Vo object and create a XML Object for the webservice
	 * 
	 * @param ServiceRequestIF
	 *            the validate payPal pRreqVO vo
	 * 
	 */
	@Override
	public XmlObject buildRequest(ServiceRequestIF pReqVo) throws BBBBusinessException, BBBSystemException {
		
		logDebug("Entry buildRequest of DoAuthorizationMarshaller with ServiceRequestIF object:" + pReqVo.getServiceName());
		
		BBBPerformanceMonitor.start("DoAuthorizationMarshaller-buildRequest");

		DoAuthorizationReqDocument doAuthReqDoc = null;

		doAuthReqDoc = DoAuthorizationReqDocument.Factory.newInstance();

		DoAuthorizationReq doAuthReq = doAuthReqDoc.addNewDoAuthorizationReq();

		DoAuthorizationRequestDocument doAuthRequestDoc = DoAuthorizationRequestDocument.Factory.newInstance();

		DoAuthorizationRequestType doAuthRequest = doAuthRequestDoc.addNewDoAuthorizationRequest();
		try {
			BBBDoAuthorizationReq req = (BBBDoAuthorizationReq) pReqVo;

			this.cred = req.getCred();

			doAuthRequest.setTransactionID(req.getDoAuthorizationRequestType().getTransactionId());

			// ************Set Version****************
			doAuthRequest.setVersion(this.cred.getVersion());

			// Setting amount
			CurrencyCodeType.Enum curr = CurrencyCodeType.USD;
			DecimalFormat df = new DecimalFormat("####0.00");
			BasicAmountType amount = doAuthRequest.addNewAmount();
			String currID = req.getDoAuthorizationRequestType().getAmount().getCurrencyID();
			if (currID != null && currID.equalsIgnoreCase(CurrencyCodeType.CAD.toString())) {
				curr = CurrencyCodeType.CAD;
				amount.setCurrencyID(curr);
			} else {
				curr = CurrencyCodeType.USD;
				amount.setCurrencyID(curr);
			}

			amount.setStringValue(df.format(Double.parseDouble(req.getOrderTotal())));
			doAuthRequest.setAmount(amount);

			doAuthReq.setDoAuthorizationRequest(doAuthRequest);

		} finally {
			BBBPerformanceMonitor.end("DoAuthorizationMarshaller-buildRequest");
		}
		
		logDebug("Exit buildRequest of DoAuthorizationMarshaller with XmlObject object:");
		
		return doAuthReqDoc;

	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @throws BBBSystemException
	 * 
	 */
	@Override
	public XmlObject buildHeader() throws BBBSystemException, BBBBusinessException {
		
		logDebug("Entry buildHeader of DoAuthorizationMarshaller:");
		
		BBBPerformanceMonitor.start("DoAuthorizationMarshaller-buildHeader");
		if (this.cred != null) {
			RequesterCredentialsDocument credDoc = RequesterCredentialsDocument.Factory.newInstance();
			CustomSecurityHeaderType sechdr = credDoc.addNewRequesterCredentials();

			UserIdPasswordType idType = sechdr.addNewCredentials();
			idType.setUsername(this.cred.getUserName());
			idType.setPassword(this.cred.getPassword());
			idType.setSignature(this.cred.getSignature());
			idType.setSubject(this.cred.getSubject());
			sechdr.setCredentials(idType);
			credDoc.setRequesterCredentials(sechdr);
			BBBPerformanceMonitor.end("DoAuthorizationMarshaller-buildHeader");
			
			logDebug("Exit buildHeader of DoAuthorizationMarshaller:");
			
			return credDoc;
		}
		throw new BBBBusinessException(BBBCoreErrorConstants.PAYPAL_REQUEST_CRED_NULL_EXCEPTION);

	}

}
