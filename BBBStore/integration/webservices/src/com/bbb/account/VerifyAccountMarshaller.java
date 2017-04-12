///*
// *  Copyright 2011, The BBB  All Rights Reserved.
// *
// *  Reproduction or use of this file without express written
// *  consent is prohibited.
// *
// *  FILE:  VerifyAccountMarshaller.java
// *
// *  DESCRIPTION: VerifyAccountMarshaller marshall's the verify account information web-service response. 	
// *  HISTORY:
// *  13/12/11 Initial version
// *
// */
//
//package com.bbb.account;
//
//import java.util.IllegalFormatException;
//
//import org.apache.xmlbeans.XmlObject;
//import org.dozer.DozerBeanMapper;
//import org.dozer.MappingException;
//
//import com.bbb.exception.BBBBusinessException;
//import com.bbb.exception.BBBSystemException;
//import com.bbb.framework.integration.ServiceRequestIF;
//import com.bbb.framework.performance.BBBPerformanceMonitor;
//import com.bbb.framework.webservices.RequestMarshaller;
//import com.bedbathandbeyond.www.VerifyAccountDocument;
//import com.bedbathandbeyond.www.VerifyAccountDocument.VerifyAccount;
//
//public class VerifyAccountMarshaller extends RequestMarshaller {
//
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * 
//	 */
//	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
//		
//			logDebug("VerifyAccountMarshaller | XmlObject | buildRequest | starts");
//		
//			logDebug("VerifyAccountMarshaller | buildRequest | starts");
//		
//		BBBPerformanceMonitor.start("VerifyAccountMarshaller-buildRequest");
//		
//		VerifyAccountDocument verifyAccountDocument = null;
//		try {
//			verifyAccountDocument = VerifyAccountDocument.Factory.newInstance();
//			verifyAccountDocument.setVerifyAccount(getDozerMappedResponse(pReqVO));
//		} finally {
//			BBBPerformanceMonitor.end("VerifyAccountMarshaller-buildRequest");
//		}
//		
//			logDebug("VerifyAccountMarshaller | XmlObject | buildRequest | ends");
//		return verifyAccountDocument;
//
//	}
//
//	/**
//	 * 
//	 * @param reqVO
//	 * @return
//	 * @throws BBBSystemException
//	 */
//	private VerifyAccount getDozerMappedResponse(ServiceRequestIF pReqVO)
//			throws BBBSystemException {
//		
//			logDebug("VerifyAccountMarshaller | VerifyAccount | getDozerMappedResponse | starts");
//		
//		BBBPerformanceMonitor
//				.start("VerifyAccountMarshaller-buildVerifyAccount");
//
//		VerifyAccount verifyAccount = VerifyAccount.Factory.newInstance();
//
//		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
//		try {
//			mapper.map(pReqVO, verifyAccount);
//		} catch (MappingException me) {
//			logError(me);
//			throw new BBBSystemException(me.getMessage(), me);
//		} catch (IllegalFormatException e) {
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		} catch (IllegalArgumentException e) {
//			// usually this is a result of un-expected Enum value in
//			// response
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		} catch (SecurityException e) {
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		} catch (ClassCastException e) {
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		} finally {
//			BBBPerformanceMonitor
//					.end("VerifyAccountMarshaller-buildVerifyAccount");
//		}
//		
//			logDebug("VerifyAccountMarshaller | VerifyAccount | getDozerMappedResponse | ends");
//		return verifyAccount;
//
//	}
//}