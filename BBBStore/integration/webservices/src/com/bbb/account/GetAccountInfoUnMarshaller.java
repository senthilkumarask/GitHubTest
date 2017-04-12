/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetAccountInfoMarshaller.java
 *
 *  DESCRIPTION: GetAccountInfoUnMarshaller  un-marshall's the get account information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.reclaim.GetAccountInfoResponseVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.GetAccountInfoResponseDocument;
import com.bedbathandbeyond.www.GetAccountInfoResponseDocument.GetAccountInfoResponse;
import com.bedbathandbeyond.www.GetAccountInfoReturn;


public class GetAccountInfoUnMarshaller extends ResponseUnMarshaller {

	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {
		
			logDebug("GetAccountInfoUnMarshaller ServiceResponseIF processResponse starts");
		
			logDebug("Inside GetAccountInfoUnMarshaller:processResponse. Response Document is "+ pResponseDocument.toString());
		BBBPerformanceMonitor.start("GetAccountInfoUnMarshaller-processResponse");

		GetAccountInfoResponseVO getAccInfoResVO = new GetAccountInfoResponseVO();

			final GetAccountInfoResponseDocument getAccInfoResDoc = (GetAccountInfoResponseDocument) pResponseDocument;

				final GetAccountInfoResponse getAccInfoRes = getAccInfoResDoc.getGetAccountInfoResponse();
				if (getAccInfoRes == null) {
					getAccInfoResVO.setWebServiceError(true);	
				}
				else{
					final GetAccountInfoReturn getAccInfoReturn = getAccInfoRes.getGetAccountInfoResult();
					getAccInfoResVO = (GetAccountInfoResponseVO) getDozerMappedResponse(getAccInfoReturn);
				}
				
		BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-processResponse");
			logDebug("GetAccountInfoUnMarshaller ServiceResponseIF processResponse ends");
		return getAccInfoResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final GetAccountInfoReturn getAccInfoReturn)
			throws BBBSystemException {
		
			logDebug("GetAccountInfoUnMarshaller ServiceResponseIF getDozerMappedResponse starts");
		
			logDebug("Inside GetAccountInfoUnMarshaller:dozerMap.....");
		BBBPerformanceMonitor.start("GetAccountInfoUnMarshaller-getDozerMappedResponse");

		final GetAccountInfoResponseVO getAccInfoResVO = new GetAccountInfoResponseVO();
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(getAccInfoReturn, getAccInfoResVO);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1339,me.getMessage(), me); 
		}
		catch (IllegalFormatException e ) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1339,e.getMessage(), e) ;
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1339,e.getMessage(), e) ;
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1339,e.getMessage(), e) ;}
		catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1339, e.getMessage(), e) ;
			}

		BBBPerformanceMonitor.end("GetAccountInfoUnMarshaller-getDozerMappedResponse");
			logDebug("GetAccountInfoUnMarshaller ServiceResponseIF getDozerMappedResponse ends");
		return getAccInfoResVO;
	}

}
