/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ForgetPasswordMarshaller.java
 *
 *  DESCRIPTION: ForgetPasswordUnMarshaller un-marshall's the forget info information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.reclaim.ForgetPasswordResponseVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ForgetPasswordResponseDocument;
import com.bedbathandbeyond.www.ForgetPasswordResponseDocument.ForgetPasswordResponse;
import com.bedbathandbeyond.www.ForgetPasswordReturn;

public class ForgetPasswordUnMarshaller extends ResponseUnMarshaller {
	
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pForgetPasswordResponseDocument)
			throws BBBSystemException {
		
			logDebug("ForgetPasswordUnMarshaller ServiceResponseIF processResponse starts");
			logDebug("Inside ForgetPasswordUnMarshaller:processResponse. Response Document is "+ pForgetPasswordResponseDocument.toString());
		BBBPerformanceMonitor.start("ForgetPasswordUnMarshaller-processResponse");

		ForgetPasswordResponseVO forgetPassResVO = new ForgetPasswordResponseVO();

			final ForgetPasswordResponseDocument forgetPassResDoc = (ForgetPasswordResponseDocument) pForgetPasswordResponseDocument;

				final ForgetPasswordResponse forgetPassRes = forgetPassResDoc.getForgetPasswordResponse();
				if (forgetPassRes != null) {
					final ForgetPasswordReturn forgetPassReturn = forgetPassRes.getForgetPasswordResult();
					forgetPassResVO = (ForgetPasswordResponseVO) getDozerMappedResponse(forgetPassReturn);
					
				}
				else{
					forgetPassResVO.setWebServiceError(true);
				}
				
		BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-processResponse");
			logDebug("ForgetPasswordUnMarshaller ServiceResponseIF processResponse ends");
		return forgetPassResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final ForgetPasswordReturn forgotPassReturn)
			throws BBBSystemException {
		
			logDebug("ServiceResponseIF getDozerMappedResponse  starts");
		BBBPerformanceMonitor.start("ForgetPasswordUnMarshaller-dozerMap");

		final ForgetPasswordResponseVO forgetPassResVO = new ForgetPasswordResponseVO();
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(forgotPassReturn, forgetPassResVO);
		} catch (MappingException me) {
			logError(me);

			BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-dozerMap");
				logDebug("ServiceResponseIF getDozerMappedResponse  ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1337,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);

			BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-dozerMap");
				logDebug("ServiceResponseIF getDozerMappedResponse  ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1337,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);

			BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-dozerMap");
				logDebug("ServiceResponseIF getDozerMappedResponse  ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1337,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);

			BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-dozerMap");
				logDebug("ServiceResponseIF getDozerMappedResponse  ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1337,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);

			BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-dozerMap");
				logDebug("ServiceResponseIF getDozerMappedResponse  ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1337,e.getMessage(), e);
		}

		BBBPerformanceMonitor.end("ForgetPasswordUnMarshaller-dozerMap");
			logDebug("ServiceResponseIF getDozerMappedResponse  ends");
		return forgetPassResVO;
	}

}
