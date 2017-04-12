///*
// *  Copyright 2011, The BBB  All Rights Reserved.
// *
// *  Reproduction or use of this file without express written
// *  consent is prohibited.
// *
// *  FILE:  ReclaimAccountMarshaller.java
// *
// *  DESCRIPTION: ReclaimAccountMarshaller marshall's the reclaim account information web-service response. 	
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
//import com.bedbathandbeyond.www.ReclaimAccountDocument;
//import com.bedbathandbeyond.www.ReclaimAccountDocument.ReclaimAccount;
//
//public class ReclaimAccountMarshaller extends RequestMarshaller {
//
//	private static final long serialVersionUID = 1L;
//
//	public XmlObject buildRequest(ServiceRequestIF pReqVO) throws BBBBusinessException, BBBSystemException {
//		
//			logDebug("ReclaimAccountMarshaller | buildRequest | starts");
//		
//		BBBPerformanceMonitor.start("ReclaimAccountMarshaller-buildRequest");
//		
//		ReclaimAccountDocument reclaimAccountDocument = null;
//		try {
//			reclaimAccountDocument = ReclaimAccountDocument.Factory.newInstance();
//			reclaimAccountDocument.setReclaimAccount(getDozerMappedResponse(pReqVO));
//		} finally {
//			BBBPerformanceMonitor.end("ReclaimAccountMarshaller-buildRequest");
//		}
//			logDebug("ReclaimAccountMarshaller | buildRequest | ends");
//		return reclaimAccountDocument;
//
//	}
//
//	/**
//	 * 
//	 * @param reqVO
//	 * @return
//	 * @throws BBBSystemException
//	 */
//	private ReclaimAccount getDozerMappedResponse(ServiceRequestIF pReqVO)
//			throws BBBSystemException {
//			logDebug("ReclaimAccountMarshaller | getDozerMappedResponse | starts");
//		BBBPerformanceMonitor
//				.start("ReclaimAccountMarshaller-buildReclaimAccount");
//
//		ReclaimAccount reclaimAccount = ReclaimAccount.Factory.newInstance();
//
//		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
//		try {
//			mapper.map(pReqVO, reclaimAccount);
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
//					.end("ReclaimAccountMarshaller-buildReclaimAccount");
//		}
//			logDebug("ReclaimAccountMarshaller | getDozerMappedResponse | ends");
//		return reclaimAccount;
//
//	}
//}
