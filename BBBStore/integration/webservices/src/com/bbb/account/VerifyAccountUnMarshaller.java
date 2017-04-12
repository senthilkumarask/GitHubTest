///*
// *  Copyright 2011, The BBB  All Rights Reserved.
// *
// *  Reproduction or use of this file without express written
// *  consent is prohibited.
// *
// *  FILE:  VerifyAccountUnMarshaller.java
// *
// *  DESCRIPTION: VerifyAccountUnMarshaller un-marshall's the verify account information web-service response. 	
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
//import com.bbb.account.vo.reclaim.VerifyAccountResponseVO;
//import com.bbb.exception.BBBSystemException;
//import com.bbb.framework.integration.ServiceResponseIF;
//import com.bbb.framework.performance.BBBPerformanceMonitor;
//import com.bbb.framework.webservices.ResponseUnMarshaller;
//import com.bedbathandbeyond.www.VerifyAccountResponseDocument;
//import com.bedbathandbeyond.www.VerifyAccountResponseDocument.VerifyAccountResponse;
//import com.bedbathandbeyond.www.VerifyAccountReturn;
//
//public class VerifyAccountUnMarshaller extends ResponseUnMarshaller {
//
//
//	private static final long serialVersionUID = 1L;
//
//	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
//			throws BBBSystemException {
//		
//			logDebug("VerifyAccountUnMarshaller ServiceResponseIF processResponse starts");
//		
//			logDebug("Inside VerifyAccountUnMarshaller:processResponse. Response Document is "+ pResponseDocument.toString());
//		BBBPerformanceMonitor.start("VerifyAccountUnMarshaller-processResponse");
//
//		VerifyAccountResponseVO verifyAccountResponseVO = new VerifyAccountResponseVO();
//
//		if (pResponseDocument != null) {
//
//			VerifyAccountResponseDocument verifyAccountResponseDocument = (VerifyAccountResponseDocument) pResponseDocument;
//			if (verifyAccountResponseDocument != null) { 
//
//				final VerifyAccountResponse verifyAccountResponse = verifyAccountResponseDocument.getVerifyAccountResponse();
//
//				if (verifyAccountResponse != null) {
//					VerifyAccountReturn verifyAccountReturn = verifyAccountResponse.getVerifyAccountResult();
//					verifyAccountResponseVO = (VerifyAccountResponseVO) getDozerMappedResponse(verifyAccountReturn);
//					
//				}
//				
//			}
//		}
//
//		BBBPerformanceMonitor.end("VerifyAccountUnMarshaller-processResponse");
//			logDebug("VerifyAccountUnMarshaller ServiceResponseIF processResponse ends");
//		return verifyAccountResponseVO;
//	}
//
//	private ServiceResponseIF getDozerMappedResponse(
//			VerifyAccountReturn pVerifyAcInfoReturn)
//			throws BBBSystemException {
//		
//			logDebug("VerifyAccountUnMarshaller ServiceResponseIF getDozerMappedResponse starts");
//		
//		BBBPerformanceMonitor.start("VerifyAccountUnMarshaller-dozerMap");
//
//		VerifyAccountResponseVO getVerifyAccountResponseVO = new VerifyAccountResponseVO();
//		
//		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
//		try {
//			mapper.map(pVerifyAcInfoReturn, getVerifyAccountResponseVO);
//		} catch (MappingException me) {
//			logError(me);
//			throw new BBBSystemException(me.getMessage(), me);
//		}
//		catch (IllegalFormatException e )
//		{
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		}
//		catch (IllegalArgumentException e) {
//			// usually this is a result of un-expected Enum value in response
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		} catch (SecurityException e) {
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		}
//		catch(ClassCastException e)
//		{
//			logError(e.getMessage(), e);
//			throw new BBBSystemException(e.getMessage(), e);
//		}
//
//		BBBPerformanceMonitor.end("VerifyAccountUnMarshaller-dozerMap");
//			logDebug("VerifyAccountUnMarshaller ServiceResponseIF getDozerMappedResponse ends");
//		return getVerifyAccountResponseVO;
//	}
//
//}
