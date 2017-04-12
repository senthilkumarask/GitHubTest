/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ForgetPasswordMarshaller.java
 *
 *  DESCRIPTION: ForgetPasswordMarshaller marshall's the forget info information web-service response. 	
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
import com.bedbathandbeyond.www.ForgetPasswordDocument;
import com.bedbathandbeyond.www.ForgetPasswordDocument.ForgetPassword;

public class ForgetPasswordMarshaller extends RequestMarshaller {

	
	private static final long serialVersionUID = 1L;

	public XmlObject buildRequest(ServiceRequestIF pForgetPasswordReqVO) throws BBBBusinessException, BBBSystemException {
		
			logDebug("ForgetPasswordMarshaller | buildRequest | starts");
		
		BBBPerformanceMonitor.start("ForgetPasswordMarshaller-buildRequest");
		
		ForgetPasswordDocument forgetPasswordDocument = null;
		try {
			forgetPasswordDocument = ForgetPasswordDocument.Factory.newInstance();
			forgetPasswordDocument.setForgetPassword(getDozerMappedResponse(pForgetPasswordReqVO));
		} finally {
			BBBPerformanceMonitor.end("ForgetPasswordMarshaller-buildRequest");
		}
		
			logDebug("ForgetPasswordMarshaller | buildRequest | ends");
		return forgetPasswordDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private ForgetPassword getDozerMappedResponse(ServiceRequestIF pReqVO)
			throws BBBSystemException {
		
		
		
			logDebug("ForgetPassword getDozerMappedResponse starts");
		BBBPerformanceMonitor
				.start("ForgetPasswordMarshaller-buildForgetPassword");

		ForgetPassword forgetPassword = ForgetPassword.Factory.newInstance();

		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pReqVO, forgetPassword);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("ForgetPasswordMarshaller-buildForgetPassword");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1336,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ForgetPasswordMarshaller-buildForgetPassword");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1336,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in
			// response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ForgetPasswordMarshaller-buildForgetPassword");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1336,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ForgetPasswordMarshaller-buildForgetPassword");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1336,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("ForgetPasswordMarshaller-buildForgetPassword");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1336,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor
					.end("ForgetPasswordMarshaller-buildForgetPassword");
		}
			logDebug("ForgetPassword getDozerMappedResponse ends");
		return forgetPassword;

	}
}
