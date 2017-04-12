/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  GetOrderTrackingInfoServiceUnMarshaller.java
 *
 *  DESCRIPTION: GetOrderTrackingInfoServiceUnMarshaller is response unmarshaller for order tracking service. 	
 *  HISTORY:
 *  11/22/11 Initial version
 *
 */


package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.OrderTrackingResponseVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bedbathandbeyond.www.GetOrderTrackingInfoResponseDocument;
import com.bedbathandbeyond.www.GetOrderTrackingInfoResponseDocument.GetOrderTrackingInfoResponse;
import com.bedbathandbeyond.www.Status;
import com.bedbathandbeyond.www.impl.TrackingInfoImpl;

/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author Lokesh Duseja
 * 
 */
public class GetOrderTrackingInfoServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

			logDebug("Inside GetOrderTrackingInfoServiceUnMarshaller:processResponse. Response Document is "+ responseDocument.toString());
		BBBPerformanceMonitor
				.start("GetOrderTrackingInfoServiceUnMarshaller-processResponse");

		OrderTrackingResponseVO orderTrackResVO = null;

		try
		{
				final GetOrderTrackingInfoResponse orderTrackRes = ((GetOrderTrackingInfoResponseDocument) responseDocument)
						.getGetOrderTrackingInfoResponse();

				if (orderTrackRes != null) {
					final TrackingInfoImpl trackingInfo = (TrackingInfoImpl) orderTrackRes.getGetOrderTrackingInfoResult();
					
					if (!trackingInfo.getStatus().getErrorExists()) {
						orderTrackResVO = (OrderTrackingResponseVO) getDozerMappedResponse(trackingInfo);
					} 
				else {
					orderTrackResVO = new OrderTrackingResponseVO();
					final Status status = trackingInfo.getStatus();
					final ErrorStatus errorStatus = getDozerMappedError(status);
					orderTrackResVO.setErrorStatus(errorStatus);
					orderTrackResVO.setWebServiceError(true);
					}
				}
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetOrderTrackingInfoServiceUnMarshaller-processResponse");
		}
		return orderTrackResVO;
	}
	
	private ServiceResponseIF getDozerMappedResponse(final TrackingInfoImpl pTrackingInfo) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");

		final OrderTrackingResponseVO orderTrackRes= new OrderTrackingResponseVO();
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pTrackingInfo, orderTrackRes);
		}
		catch(MappingException me)
		{
			logError(me.getMessage(), me);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedResponse");
		}

		return orderTrackRes;
	}
	private ErrorStatus getDozerMappedError(final Status status) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");

		final ErrorStatus errorStatus= new ErrorStatus();
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(status, errorStatus);
		}
		catch(MappingException me)
		{
			logError(me);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1343,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetOrderTrackingInfoServiceMarshaller-getDozerMappedError");
		}

		return errorStatus;
	}

}
