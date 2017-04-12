/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: dwaghmare
 *
 * Created on: 25-Feb-2015
 * --------------------------------------------------------------------------------
 */

package com.bbb.account;

//Java Util Imports
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;

//
import org.apache.xmlbeans.XmlObject;

//BBB Imports
import com.bbb.account.vo.UpdateWalletProfileRespVo;
import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;

//Stub Imports

import com.bedbathandbeyond.www.UpdateWalletProfileResponseDocument;
import com.bedbathandbeyond.www.UpdateWalletProfileResponseDocument.UpdateWalletProfileResponse;
import com.bedbathandbeyond.www.UpdateWalletProfileReturn;
import com.bedbathandbeyond.www.Status;

public class UpdateWalletProfileUnMarshaller extends ResponseUnMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)throws BBBSystemException {
			logDebug("Entry processResponse with XmlObject:"+pResponseDocument.getClass());
		BBBPerformanceMonitor.start("updateWalletProfileUnmarshaller-processResponse");
		UpdateWalletProfileRespVo updateWalletProfileRespVo = null;
		
		try
		{
				final UpdateWalletProfileResponse updateProfileRes = ((UpdateWalletProfileResponseDocument) pResponseDocument).getUpdateWalletProfileResponse();
				if (updateProfileRes != null) {
					final UpdateWalletProfileReturn updateWalletProfileReturn = updateProfileRes.getUpdateWalletProfileResult();
					if (!updateWalletProfileReturn.getStatus().getErrorExists()) {
						updateWalletProfileRespVo = (UpdateWalletProfileRespVo) getDozerMappedResponse(updateWalletProfileReturn);
					} 
				else {
					updateWalletProfileRespVo = new UpdateWalletProfileRespVo();
					final Status status = updateWalletProfileReturn.getStatus();
					final ErrorStatus errorStatus = getDozerMappedError(status);
					updateWalletProfileRespVo.setmErrorStatus(errorStatus);
					updateWalletProfileRespVo.setWebServiceError(true);
					}
				}
		}
		finally
		{
			BBBPerformanceMonitor
				.end("updateWalletProfileUnmarshaller-processResponse");
		}
		return updateWalletProfileRespVo;
	}
	
	
	
	private ServiceResponseIF getDozerMappedResponse(final UpdateWalletProfileReturn updateWalletProfileReturn ) throws BBBSystemException {

		
		BBBPerformanceMonitor
				.start("updateWalletProfileUnmarshaller-getDozerMappedResponse");

		final UpdateWalletProfileRespVo updateWalletProfileRespVo = new UpdateWalletProfileRespVo();
		final Status status = updateWalletProfileReturn.getStatus();
		final ErrorStatus errorStatus = getDozerMappedError(status);
		updateWalletProfileRespVo.setmErrorStatus(errorStatus);
		updateWalletProfileRespVo.setWebServiceError(false);
		BBBPerformanceMonitor
		.end("updateWalletProfileUnmarshaller-getDozerMappedResponse");
		return updateWalletProfileRespVo;
	}
	
	@SuppressWarnings("unchecked")
	private ErrorStatus getDozerMappedError(final Status status) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("updateWalletProfileUnmarshaller-getDozerMappedError");

		final ErrorStatus errorStatus= new ErrorStatus();
		
		try
		{
			errorStatus.setDisplayMessage(status.getDisplayMessage());
			errorStatus.setErrorExists(status.getErrorExists());
			errorStatus.setErrorId(status.getID());
			errorStatus.setErrorMessage(status.getErrorMessage());
			errorStatus.setValidationErrors((List<ValidationError>) status.getValidationErrors());
		}
		
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceUnMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceUnMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceUnMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetCouponsServiceUnMarshaller-getDozerMappedError");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1341,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetCouponsServiceUnMarshaller-getDozerMappedError");
		}

		return errorStatus;
	}

	
	
}