/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ValidateOldAccountUnMarshaller.java
 *
 *  DESCRIPTION: ValidateOldAccountUnMarshaller un-marshall's the verify account information web-service response. 	
 *  HISTORY:
 *  13/12/11 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.verifyoldaccount.ValidateOldAccResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ValidateOldAccountResponseDocument;
import com.bedbathandbeyond.www.ValidateOldAccountResponseDocument.ValidateOldAccountResponse;
import com.bedbathandbeyond.www.ValidateOldAccountReturn;

public class ValidateOldAccountUnMarshaller extends ResponseUnMarshaller {


	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {
		
			logDebug("ValidateOldAccountUnMarshaller ServiceResponseIF processResponse starts");
		
			logDebug("Inside ValidateOldAccountUnMarshaller:processResponse. Response Document is "+ pResponseDocument.toString());
		BBBPerformanceMonitor.start("ValidateOldAccountUnMarshaller-processResponse");

		ValidateOldAccResVO validateOldAccResVO = new ValidateOldAccResVO();
		final ValidateOldAccountResponseDocument validateOldAccResDoc = (ValidateOldAccountResponseDocument) pResponseDocument;
		final ValidateOldAccountResponse validateOldAccRes = validateOldAccResDoc.getValidateOldAccountResponse();
	    if (validateOldAccRes != null) {
			final ValidateOldAccountReturn validateOldAccReturn = validateOldAccRes.getValidateOldAccountResult();
			validateOldAccResVO = (ValidateOldAccResVO) getDozerMappedResponse(validateOldAccReturn);
			
		}
		else{
			validateOldAccResVO.setWebServiceError(true);
		}
		BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-processResponse");
		logDebug("ValidateOldAccountUnMarshaller ServiceResponseIF processResponse ends");
		return validateOldAccResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final ValidateOldAccountReturn pValidateOldAccReturn)
			throws BBBSystemException {
		
			logDebug("ValidateOldAccountUnMarshaller ServiceResponseIF getDozerMappedResponse starts");
		
		BBBPerformanceMonitor.start("ValidateOldAccountUnMarshaller-dozerMap");

		final ValidateOldAccResVO validateOldAccResVO = new ValidateOldAccResVO();
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pValidateOldAccReturn, validateOldAccResVO);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1355,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1355,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1355,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1355,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1355,e.getMessage(), e);
		}

		BBBPerformanceMonitor.end("ValidateOldAccountUnMarshaller-dozerMap");
			logDebug("ValidateOldAccountUnMarshaller ServiceResponseIF getDozerMappedResponse ends");
		return validateOldAccResVO;
	}

}
