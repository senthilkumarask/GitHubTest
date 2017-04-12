/*
 *
 * File  : UpdateRegistryWithAtgInfoServiceMarshaller.java 
 * 
 * Project:     BBB
 * 
 * Description: Request Marshaller class for UpdateRegistryWithAtgInfo Web Service
 * 
 * HISTORY:
 * Initial Version: 02/04/2012
 * 
 * 
 */

package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.UpdateRegistryWithAtgInfoDocument;
import com.bedbathandbeyond.www.UpdateRegistryWithAtgInfoDocument.UpdateRegistryWithAtgInfo;

/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author Rajesh Saini
 * 
 */
public class UpdateRegistryWithAtgInfoServiceMarshaller extends
		RequestMarshaller {

	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor.start("ServiceMarshaller-buildRequest");

		UpdateRegistryWithAtgInfoDocument registryWithAtgInfoDocument = null;
		try {
			registryWithAtgInfoDocument = UpdateRegistryWithAtgInfoDocument.Factory.newInstance();
			registryWithAtgInfoDocument.setUpdateRegistryWithAtgInfo(getDozerMappedRequest(pRreqVO));

		} finally {
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildRequest");
		}
		return registryWithAtgInfoDocument;
	}

	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * 
	 * @throws BBBSystemException
	 * 
	 */
	private UpdateRegistryWithAtgInfo getDozerMappedRequest(
			ServiceRequestIF pReqVO) throws BBBSystemException {

		BBBPerformanceMonitor.start("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");

		UpdateRegistryWithAtgInfo registryWithAtgInfo = UpdateRegistryWithAtgInfo.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pReqVO, registryWithAtgInfo);
		} catch (MappingException me) {
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");
			logError(me.getMessage(), me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1352,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1352,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1352,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1352,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1352,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor.end("UpdateRegistryWithAtgInfoServiceMarshaller-buildValidateAddressType");
		}
		return registryWithAtgInfo;
	}

}