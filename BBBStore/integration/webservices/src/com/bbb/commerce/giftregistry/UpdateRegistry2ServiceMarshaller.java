/*
 *
 * File  : UpdateRegistryServiceMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.UpdateRegistry2Document;
import com.bedbathandbeyond.www.UpdateRegistry2Document.UpdateRegistry2;



/**
 * 
 * This class contain methods used for marshalling the update registry webservice request.
 * 
 * @author ssha53
 * 
 */
public class UpdateRegistry2ServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 * 
	 *  * @param ServiceRequestIF
	 *            the validate address pRreqVO vo
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBSystemException {

		logDebug("UpdateRegistryServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRegistryServiceMarshaller-buildRequest");

		
		UpdateRegistry2Document updateRegistryDocument = null;
		try {
			updateRegistryDocument = UpdateRegistry2Document.Factory.newInstance();
			updateRegistryDocument.setUpdateRegistry2(mapUpdateRegistryRequest(pRreqVO));
			
		} finally{
			BBBPerformanceMonitor
			.end("UpdateRegistryServiceMarshaller-buildRequest");
		}

		logDebug("UpdateRegistryServiceMarshaller.buildRequest() method ends");

		return updateRegistryDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param ServiceRequestIF
	 *            the registry pReqVOs vo
	 * 
	 * @return the update registry.
	 * @throws BBBSystemException 
	 * 
	 */
	private UpdateRegistry2 mapUpdateRegistryRequest(ServiceRequestIF pReqVO) throws BBBSystemException {

		logDebug("UpdateRegistryServiceMarshaller.mapUpdateRegistryRequest() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRegistryServiceMarshaller-mapUpdateRegistryRequest");

		UpdateRegistry2 updateRegistry = UpdateRegistry2.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, updateRegistry);
		}
		catch(MappingException me)
		{
			BBBPerformanceMonitor
			.end("UpdateRegistryServiceMarshaller-mapUpdateRegistryRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10156 +" MappingException from mapUpdateRegistryRequest from UpdateRegistryServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1381,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
			.end("UpdateRegistryServiceMarshaller-mapUpdateRegistryRequest");
		}
		
		logDebug("UpdateRegistryServiceMarshaller.mapUpdateRegistryRequest() method ends");

		return updateRegistry;
	}

}