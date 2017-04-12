///*
// *  Copyright 2011, The BBB  All Rights Reserved.
// *
// *  Reproduction or use of this file without express written
// *  consent is prohibited.
// *
// *  FILE:  ReclaimAccountMarshaller.java
// *
// *  DESCRIPTION: ReclaimAccountUnMarshaller un-marshall's the reclaim account information web-service response. 	
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
//import com.bbb.account.vo.reclaim.ReclaimAccountResponseVO;
//import com.bbb.exception.BBBSystemException;
//import com.bbb.framework.integration.ServiceResponseIF;
//import com.bbb.framework.performance.BBBPerformanceMonitor;
//import com.bbb.framework.webservices.ResponseUnMarshaller;
//import com.bedbathandbeyond.www.ReclaimAccountResponseDocument;
//import com.bedbathandbeyond.www.ReclaimAccountResponseDocument.ReclaimAccountResponse;
//import com.bedbathandbeyond.www.ReclaimAccountReturn;
//
//
//public class ReclaimAccountUnMarshaller extends ResponseUnMarshaller {
//
//
//	private static final long serialVersionUID = 1L;
//
//	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
//			throws BBBSystemException {
//		
//			logDebug("ReclaimAccountUnMarshaller ServiceResponseIF processResponse starts");
//		
//			logDebug("Inside ReclaimAccountUnMarshaller:processResponse. Response Document is "+ pResponseDocument.toString());
//		BBBPerformanceMonitor.start("ReclaimAccountUnMarshaller-processResponse");
//
//		ReclaimAccountResponseVO reclaimAccountResponseVO = new ReclaimAccountResponseVO();
//
//		if (pResponseDocument != null) {
//
//			ReclaimAccountResponseDocument reclaimAccountResponseDocument = (ReclaimAccountResponseDocument) pResponseDocument;
//			if (reclaimAccountResponseDocument != null) { 
//
//				final ReclaimAccountResponse reclaimAccountResponse = reclaimAccountResponseDocument.getReclaimAccountResponse();
//
//				if (reclaimAccountResponse != null) {
//					ReclaimAccountReturn reclaimAccountReturn = reclaimAccountResponse.getReclaimAccountResult();
//					reclaimAccountResponseVO = (ReclaimAccountResponseVO) getDozerMappedResponse(reclaimAccountReturn);
//					
//				}
//				
//			}
//		}
//
//		BBBPerformanceMonitor.end("ReclaimAccountUnMarshaller-processResponse");
//		
//			logDebug("ReclaimAccountUnMarshaller ServiceResponseIF processResponse ends");
//		return reclaimAccountResponseVO;
//	}
//
//	private ServiceResponseIF getDozerMappedResponse(
//			ReclaimAccountReturn pVerifyAcInfoReturn)
//			throws BBBSystemException {
//		
//			logDebug("ReclaimAccountUnMarshaller ServiceResponseIF getDozerMappedResponse starts");
//		
//		BBBPerformanceMonitor.start("ReclaimAccountUnMarshaller-dozerMap");
//
//		ReclaimAccountResponseVO getReclaimAccountResponseVO = new ReclaimAccountResponseVO();
//		
//		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
//		try {
//			mapper.map(pVerifyAcInfoReturn, getReclaimAccountResponseVO);
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
//		BBBPerformanceMonitor.end("ReclaimAccountUnMarshaller-dozerMap");
//			logDebug("ReclaimAccountUnMarshaller ServiceResponseIF getDozerMappedResponse ends");
//		return getReclaimAccountResponseVO;
//	}
//
//}
