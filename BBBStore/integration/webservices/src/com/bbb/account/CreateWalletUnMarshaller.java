/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 26-December-2011
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
import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.account.vo.CreateWalletRespVo;
import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;

//Stub Imports

import com.bedbathandbeyond.www.CreateWalletResponseDocument;
import com.bedbathandbeyond.www.CreateWalletResponseDocument.CreateWalletResponse;
import com.bedbathandbeyond.www.CreateWalletReturn;
import com.bedbathandbeyond.www.Status;

public class CreateWalletUnMarshaller extends ResponseUnMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)throws BBBSystemException {
			
		CreateWalletRespVo createWalletRespVo = null;
		if(null != pResponseDocument){
			logDebug("Entry processResponse with XmlObject:"+pResponseDocument.getClass());
			BBBPerformanceMonitor.start("CreateWalletUnmarshaller-processResponse");			
			
			try
			{
					final CreateWalletResponse cwRes = ((CreateWalletResponseDocument) pResponseDocument).getCreateWalletResponse();
					if (cwRes != null) {
						final CreateWalletReturn createWalletReturn = cwRes.getCreateWalletResult();
						if (!createWalletReturn.getStatus().getErrorExists()) {
							createWalletRespVo = (CreateWalletRespVo) getDozerMappedResponse(createWalletReturn);
						} 
					else {
						createWalletRespVo = new CreateWalletRespVo();
						final Status status = createWalletReturn.getStatus();
						final ErrorStatus errorStatus = getDozerMappedError(status);
						createWalletRespVo.setmErrorStatus(errorStatus);
						createWalletRespVo.setWebserviceError(true);
						}
					}
			}
			finally
			{
				BBBPerformanceMonitor
					.end("CreateWalletUnmarshaller-processResponse");
			}
		}
			
		return createWalletRespVo;
	}
	
	
	
	private ServiceResponseIF getDozerMappedResponse(final CreateWalletReturn createWalletReturn ) throws BBBSystemException {

		
		BBBPerformanceMonitor
				.start("CreateWalletUnmarshaller-getDozerMappedResponse");

		final CreateWalletRespVo createWalletRespVo = new CreateWalletRespVo();
		createWalletRespVo.setWalletId(createWalletReturn.getWalletID());
		//createWalletRespVo.setWalletInfomation(createWalletReturn.getWalletInformation());
		createWalletRespVo.setWalletUrl(createWalletReturn.getWalletURL());
		final Status status = createWalletReturn.getStatus();
		final ErrorStatus errorStatus = getDozerMappedError(status);
		createWalletRespVo.setmErrorStatus(errorStatus);
		createWalletRespVo.setWebserviceError(false);
		
		return createWalletRespVo;
	}
	
	@SuppressWarnings("unchecked")
	private ErrorStatus getDozerMappedError(final Status status) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("CreateWalletUnmarshaller-getDozerMappedError");

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