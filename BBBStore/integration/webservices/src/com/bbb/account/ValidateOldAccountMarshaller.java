/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ValidateOldAccountMarshaller.java
 *
 *  DESCRIPTION: ValidateOldAccountMarshaller marshall's the verify account information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.ValidateOldAccountDocument;
import com.bedbathandbeyond.www.ValidateOldAccountDocument.ValidateOldAccount;


public class ValidateOldAccountMarshaller extends RequestMarshaller {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
		
			logDebug("ValidateOldAccountMarshaller | XmlObject | buildRequest | starts");
		
			logDebug("ValidateOldAccountMarshaller | buildRequest | starts");
		
		BBBPerformanceMonitor.start("ValidateOldAccountMarshaller-buildRequest");
		
		ValidateOldAccountDocument validateOldAccountDocument = null;
		try {
			validateOldAccountDocument = ValidateOldAccountDocument.Factory.newInstance();
			validateOldAccountDocument.setValidateOldAccount(getDozerMappedResponse(pReqVO));
		} finally {
			BBBPerformanceMonitor.end("ValidateOldAccountMarshaller-buildRequest");
		}
		
			logDebug("ValidateOldAccountMarshaller | XmlObject | buildRequest | ends");
		return validateOldAccountDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private ValidateOldAccount getDozerMappedResponse(ServiceRequestIF pReqVO)
			throws BBBSystemException {
		
			logDebug("ValidateOldAccountMarshaller | validateOldAccount | getDozerMappedResponse | starts");
		
		BBBPerformanceMonitor
				.start("ValidateOldAccountMarshaller-buildVerifyAccount");

		ValidateOldAccount validateOldAccount = ValidateOldAccount.Factory.newInstance();

		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pReqVO, validateOldAccount);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("ValidateOldAccountMarshaller-buildVerifyAccount");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1354,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ValidateOldAccountMarshaller-buildVerifyAccount");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1354,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in
			// response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ValidateOldAccountMarshaller-buildVerifyAccount");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1354,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ValidateOldAccountMarshaller-buildVerifyAccount");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1354,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ValidateOldAccountMarshaller-buildVerifyAccount");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1354,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor
					.end("ValidateOldAccountMarshaller-buildVerifyAccount");
		}
		
			logDebug("ValidateOldAccountMarshaller | validateOldAccount | getDozerMappedResponse | ends");
		return validateOldAccount;

	}
}
