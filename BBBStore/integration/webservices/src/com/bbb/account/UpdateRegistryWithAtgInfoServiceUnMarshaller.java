/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  UpdateRegistryWithAtgInfoServiceUnMarshaller.java
 *
 *  DESCRIPTION: UpdateRegistryWithAtgInfoServiceUnMarshaller is response unmarshaller for UpdateRegistryWithAtgInfoService. 	
 *  HISTORY:
 *  04/02/12 Initial version
 *
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.ProfileSyncResponseVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.UpdateRegistryWithAtgInfoResponseDocument;
import com.bedbathandbeyond.www.UpdateRegistryWithAtgInfoResponseDocument.UpdateRegistryWithAtgInfoResponse;
import com.bedbathandbeyond.www.UpdateRegistryWithAtgInfoReturn;

/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author Rajesh Saini
 * 
 */
public class UpdateRegistryWithAtgInfoServiceUnMarshaller extends
		ResponseUnMarshaller {

	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {

		logDebug("UpdateRegistryWithAtgInfoServiceUnMarshaller ServiceResponseIF processResponse starts");

		logDebug("Inside UpdateRegistryWithAtgInfoServiceUnMarshaller:processResponse. Response Document is "
				+ pResponseDocument.toString());
		BBBPerformanceMonitor.start("UpdateRegistryWithAtgInfoServiceUnMarshaller-processResponse");

		ProfileSyncResponseVO profileSyncResVO = new ProfileSyncResponseVO();


		final UpdateRegistryWithAtgInfoResponseDocument updateRegWithAtgInfoResDoc = (UpdateRegistryWithAtgInfoResponseDocument) pResponseDocument;
		final UpdateRegistryWithAtgInfoResponse updateRegWithAtgInfoRes = updateRegWithAtgInfoResDoc.getUpdateRegistryWithAtgInfoResponse();
		if (updateRegWithAtgInfoRes != null) {
			final UpdateRegistryWithAtgInfoReturn updateRegWithAtgInfoReturn = updateRegWithAtgInfoRes.getUpdateRegistryWithAtgInfoResult();
			profileSyncResVO = (ProfileSyncResponseVO) getDozerMappedResponse(updateRegWithAtgInfoReturn);
		}
		else{
			profileSyncResVO.setWebServiceError(true);
		}

		BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-processResponse");
		logDebug("UpdateRegistryWithAtgInfoServiceUnMarshaller ServiceResponseIF processResponse ends");
		return profileSyncResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(final UpdateRegistryWithAtgInfoReturn pUpdateRegWithAtgInfoReturn)
			throws BBBSystemException {

		logDebug("UpdateRegistryWithAtgInfoServiceUnMarshaller ServiceResponseIF getDozerMappedResponse starts");

		logDebug("Inside UpdateRegistryWithAtgInfoServiceUnMarshaller:dozerMap.....");
		BBBPerformanceMonitor.start("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");

		final ProfileSyncResponseVO profileSyncResVO = new ProfileSyncResponseVO();

		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pUpdateRegWithAtgInfoReturn, profileSyncResVO);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1353,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1353,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1353,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1353,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1353,e.getMessage(), e);
		}

		BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceUnMarshaller-getDozerMappedResponse");
		logDebug("UpdateRegistryWithAtgInfoServiceUnMarshaller ServiceResponseIF getDozerMappedResponse ends");
		return profileSyncResVO;
	}

}
