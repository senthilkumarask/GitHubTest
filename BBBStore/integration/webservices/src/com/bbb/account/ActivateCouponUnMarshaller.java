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

import com.bbb.account.validatecoupon.ActivateCouponResponseVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.activatebigblue.ActivateBigBlueResponseDocument;
import com.bedbathandbeyond.www.activatebigblue.ActivateBigBlueResponseDocument.ActivateBigBlueResponse;
import com.bedbathandbeyond.www.activatebigblue.ActivateBigBlueReturn;

public class ActivateCouponUnMarshaller extends ResponseUnMarshaller {

	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {
		
			logDebug("ActivateCouponUnMarshaller ServiceResponseIF processResponse starts");
		
			logDebug("Inside ActivateCouponUnMarshaller:processResponse. Response Document is "+ pResponseDocument.toString());
		BBBPerformanceMonitor.start("ActivateCouponUnMarshaller-processResponse");

		ActivateCouponResponseVO activateCpnResVO = new ActivateCouponResponseVO();

			final ActivateBigBlueResponseDocument bigBlueResDoc = (ActivateBigBlueResponseDocument) pResponseDocument;
			
				final ActivateBigBlueResponse bigBlueRes = bigBlueResDoc.getActivateBigBlueResponse();
				if (bigBlueRes != null) {
					final ActivateBigBlueReturn bigBlueReturn = bigBlueRes.getActivateBigBlueResult();
					activateCpnResVO = (ActivateCouponResponseVO) getDozerMappedResponse(bigBlueReturn);
					
				}
				else{
					activateCpnResVO.setWebServiceError(true);
				}

		BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-processResponse");
			logDebug("ActivateCouponUnMarshaller ServiceResponseIF processResponse ends");
		return activateCpnResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final ActivateBigBlueReturn bigBlueReturn)
			throws BBBSystemException {
		
			logDebug("ActivateCouponUnMarshaller ServiceResponseIF getDozerMappedResponse starts");
		
			logDebug("Inside ActivateCouponUnMarshaller:dozerMap.....");
		BBBPerformanceMonitor.start("ActivateCouponUnMarshaller-getDozerMappedResponse");

		final ActivateCouponResponseVO couponResponseVO = new ActivateCouponResponseVO();
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(bigBlueReturn, couponResponseVO);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(e.getMessage(), e);
		}

		BBBPerformanceMonitor.end("ActivateCouponUnMarshaller-getDozerMappedResponse");
			logDebug("ActivateCouponUnMarshaller ServiceResponseIF getDozerMappedResponse ends");
		return couponResponseVO;
	}

}
