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

import paypalapi.api.ebay.GetExpressCheckoutDetailsReqDocument;
import paypalapi.api.ebay.GetExpressCheckoutDetailsReqDocument.GetExpressCheckoutDetailsReq;
import paypalapi.api.ebay.GetExpressCheckoutDetailsRequestDocument;
import paypalapi.api.ebay.GetExpressCheckoutDetailsRequestType;
import paypalapi.api.ebay.RequesterCredentialsDocument;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsReq;
import com.bbb.paypal.BBBPayPalCredentials;

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
public class GetExpCheckoutMarshaller extends RequestMarshaller {

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
		
		logDebug("Entry buildRequest of GetExpCheckoutMarshaller with ServiceRequestIF object:" + pReqVo.getServiceName());
		
		BBBPerformanceMonitor.start("GetExpCheckoutMarshaller-buildRequest");
		GetExpressCheckoutDetailsReqDocument getExpressCheckoutDoc = null;
		
		try {
			getExpressCheckoutDoc = GetExpressCheckoutDetailsReqDocument.Factory.newInstance();
			
			GetExpressCheckoutDetailsReq getExpressCheckoutReq = getExpressCheckoutDoc.addNewGetExpressCheckoutDetailsReq();
			
			GetExpressCheckoutDetailsRequestDocument getExpressCheckoutDetailRequestDocument = GetExpressCheckoutDetailsRequestDocument.Factory.newInstance();
			
			GetExpressCheckoutDetailsRequestType getExpressCheckoutReqType = getExpressCheckoutDetailRequestDocument.addNewGetExpressCheckoutDetailsRequest();
			
			BBBGetExpressCheckoutDetailsReq req = (BBBGetExpressCheckoutDetailsReq) pReqVo;
			
			getExpressCheckoutReqType.setToken(req.getGetGetExpressCheckoutDetailsRequest().getToken());
			
			this.cred = req.getCred();
			
			getExpressCheckoutReqType.setVersion(this.cred.getVersion());
			
			
			getExpressCheckoutReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutReqType);
			
			
		} finally {
			BBBPerformanceMonitor.end("GetExpCheckoutMarshaller-buildRequest");
		}
		
		logDebug("Exit buildRequest of GetExpCheckoutMarshaller with XmlObject object:" + getExpressCheckoutDoc.getClass());
		
		return getExpressCheckoutDoc;

	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @throws BBBSystemException
	 * 
	 */
	@Override
	public XmlObject buildHeader() throws BBBSystemException, BBBBusinessException {
		
		logDebug("Entry buildHeader of PayPalServiceMarshaller:");		
		
		if (this.cred != null) {
			BBBPerformanceMonitor.start("PayPalServiceMarshaller-buildHeader");
			RequesterCredentialsDocument credDoc = RequesterCredentialsDocument.Factory.newInstance();
			CustomSecurityHeaderType sechdr = credDoc.addNewRequesterCredentials();

			UserIdPasswordType idType = sechdr.addNewCredentials();
			idType.setUsername(this.cred.getUserName());
			idType.setPassword(this.cred.getPassword());
			idType.setSignature(this.cred.getSignature());
			idType.setSubject(this.cred.getSubject());
			sechdr.setCredentials(idType);
			credDoc.setRequesterCredentials(sechdr);
			BBBPerformanceMonitor.end("PayPalServiceMarshaller-buildHeader");
			
			logDebug("Exit buildHeader of PayPalServiceMarshaller:");
			
			return credDoc;
		}
		throw new BBBBusinessException(BBBCoreErrorConstants.PAYPAL_REQUEST_CRED_NULL_EXCEPTION);

	}

}
