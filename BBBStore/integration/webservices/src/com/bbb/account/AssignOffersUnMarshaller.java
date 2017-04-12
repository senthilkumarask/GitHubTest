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
import java.util.IllegalFormatException;
import java.util.List;

//
import org.apache.xmlbeans.XmlObject;

//BBB Imports
import com.bbb.account.vo.AssignOffersRespVo;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;

//Stub Imports
import com.bedbathandbeyond.www.AssignOffersResponseDocument;
import com.bedbathandbeyond.www.AssignOffersResponseDocument.AssignOffersResponse;
import com.bedbathandbeyond.www.AssignOffersReturn;
import com.bedbathandbeyond.www.Status;

public class AssignOffersUnMarshaller extends ResponseUnMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)throws BBBSystemException {
		AssignOffersRespVo AssignOffersRespVo = null;
		if(null != pResponseDocument){
			logDebug("Entry processResponse with XmlObject:"+pResponseDocument.getClass());
			BBBPerformanceMonitor.start("AssignOffersUnmarshaller-processResponse");
			try
			{
					final AssignOffersResponse cwRes = ((AssignOffersResponseDocument) pResponseDocument).getAssignOffersResponse();
					if (cwRes != null) {
						final AssignOffersReturn AssignOffersReturn = cwRes.getAssignOffersResult();
						if (!AssignOffersReturn.getStatus().getErrorExists()) {
							AssignOffersRespVo = (AssignOffersRespVo) getDozerMappedResponse(AssignOffersReturn);
						} 
					else {
						AssignOffersRespVo = new AssignOffersRespVo();
						final Status status = AssignOffersReturn.getStatus();
						final ErrorStatus errorStatus = getDozerMappedError(status);
						AssignOffersRespVo.setmErrorStatus(errorStatus);
						AssignOffersRespVo.setWebServiceError(true);
						}
					}
			}
			finally
			{
				BBBPerformanceMonitor
					.end("AssignOffersUnmarshaller-processResponse");
			}
		}		
		return AssignOffersRespVo;
	}
	
	
	
	private ServiceResponseIF getDozerMappedResponse(final AssignOffersReturn AssignOffersReturn ) throws BBBSystemException {

		
		BBBPerformanceMonitor
				.start("AssignOffersUnmarshaller-getDozerMappedResponse");

		final AssignOffersRespVo AssignOffersRespVo = new AssignOffersRespVo();
		AssignOffersRespVo.setUniqueCouponCd(AssignOffersReturn.getUniqueCouponCd());
		final Status status = AssignOffersReturn.getStatus();
		final ErrorStatus errorStatus = getDozerMappedError(status);
		AssignOffersRespVo.setmErrorStatus(errorStatus);		
		return AssignOffersRespVo;
	}
	
	@SuppressWarnings("unchecked")
	private ErrorStatus getDozerMappedError(final Status status) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("AssignOffersUnmarshaller-getDozerMappedError");

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