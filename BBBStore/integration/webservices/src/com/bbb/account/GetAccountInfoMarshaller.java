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
import com.bedbathandbeyond.www.GetAccountInfoDocument;
import com.bedbathandbeyond.www.GetAccountInfoDocument.GetAccountInfo;


public class GetAccountInfoMarshaller extends RequestMarshaller {

	
	private static final long serialVersionUID = 1L;

	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
		
			logDebug("GetAccountInfoMarshaller | buildRequest | starts");
		
		BBBPerformanceMonitor.start("GetAccountInfoMarshaller-buildRequest");
		
		GetAccountInfoDocument getAccountInfoDocument = null;
		try {
			getAccountInfoDocument = GetAccountInfoDocument.Factory.newInstance();
			getAccountInfoDocument.setGetAccountInfo(getDozerMappedResponse(pReqVO));
		} finally {
			BBBPerformanceMonitor.end("GetAccountInfoMarshaller-buildRequest");
		}
			logDebug("GetAccountInfoMarshaller | buildRequest | ends");
		return getAccountInfoDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private GetAccountInfo getDozerMappedResponse(ServiceRequestIF pReqVO)
			throws BBBSystemException {
			logDebug("GetAccountInfo getDozerMappedResponse starts");
		BBBPerformanceMonitor
				.start("GetAccountInfoMarshaller-getDozerMappedResponse");

		GetAccountInfo getAccountInfo = GetAccountInfo.Factory.newInstance();

		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pReqVO, getAccountInfo);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("GetAccountInfoMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1338,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetAccountInfoMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1338,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in
			// response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetAccountInfoMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1338,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetAccountInfoMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1338,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetAccountInfoMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1338,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor
					.end("GetAccountInfoMarshaller-getDozerMappedResponse");
		}
			logDebug("GetAccountInfo getDozerMappedResponse ends");
		return getAccountInfo;

	}
}
