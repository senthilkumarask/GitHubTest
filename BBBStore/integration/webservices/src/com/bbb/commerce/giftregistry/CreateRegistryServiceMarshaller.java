/*
 *
 * File  : CreateRegistryServiceMarshaller.java
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
import com.bedbathandbeyond.www.CreateRegistryDocument;
import com.bedbathandbeyond.www.CreateRegistryDocument.CreateRegistry;



/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author ssha53
 * 
 */
public class CreateRegistryServiceMarshaller extends RequestMarshaller {

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

		logDebug("CreateRegistryServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("CreateRegistryServiceMarshaller-buildRequest");

		
		CreateRegistryDocument createRegistryDocument = null;
		try {
			createRegistryDocument = CreateRegistryDocument.Factory.newInstance();
			createRegistryDocument.setCreateRegistry(mapCreateRegistryRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("CreateRegistryServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1361,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
			.end("CreateRegistryServiceMarshaller-buildRequest");
		}

		logDebug("CreateRegistryServiceMarshaller.buildRequest() method ends");

		return createRegistryDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param ServiceRequestIF
	 *            the registry pReqVOs vo
	 * 
	 * @return the create registry.
	 * @throws BBBSystemException 
	 * 
	 */
	private CreateRegistry mapCreateRegistryRequest(ServiceRequestIF pReqVO) throws BBBSystemException {

		logDebug("CreateRegistryServiceMarshaller.mapCreateRegistryRequest() method start");
		
		BBBPerformanceMonitor
				.start("CreateRegistryServiceMarshaller-mapCreateRegistryRequest");

		CreateRegistry createRegistry = CreateRegistry.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, createRegistry);
		}
		catch(MappingException me)
		{
			BBBPerformanceMonitor
			.end("CreateRegistryServiceMarshaller-mapCreateRegistryRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10142 +" MappingException from mapCreateRegistryRequest from CreateRegistryServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1361,me.getMessage(), me);
		}
		catch(Exception me)
		{	BBBPerformanceMonitor
			.end("CreateRegistryServiceMarshaller-mapCreateRegistryRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10143 +" Exception from mapCreateRegistryRequest from CreateRegistryServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1361,me.getMessage(), me);
		}
		finally{
			BBBPerformanceMonitor
			.end("CreateRegistryServiceMarshaller-mapCreateRegistryRequest");
		}
		
		logDebug("CreateRegistryServiceMarshaller.mapCreateRegistryRequest() method ends");

		return createRegistry;
	}

}