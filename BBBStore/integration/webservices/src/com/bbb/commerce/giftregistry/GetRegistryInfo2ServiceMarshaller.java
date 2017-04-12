/*
 *
 * File  : GetRegistryInfoServiceMarshaller.java
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
import com.bedbathandbeyond.www.GetRegistryInfo2Document;
import com.bedbathandbeyond.www.GetRegistryInfo2Document.GetRegistryInfo2;
 



/**
 * 
 * This class contain methods used for marshalling the Get registry info webservice request.
 * 
 * @author ssha53
 * 
 */
public class GetRegistryInfo2ServiceMarshaller extends RequestMarshaller {

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

		logDebug("GetRegistryInfoServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("GetRegistryInfoServiceMarshaller-buildRequest");

		
		GetRegistryInfo2Document getRegistryInfoDocument = null;
		
	
		try {
			getRegistryInfoDocument = GetRegistryInfo2Document.Factory.newInstance();
			getRegistryInfoDocument.setGetRegistryInfo2((GetRegistryInfo2) mapGetRegistryInfoRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("GetRegistryInfoServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1371,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
					.end("GetRegistryInfoServiceMarshaller-buildRequest");
		}
		
		logDebug("GetRegistryInfoServiceMarshaller.buildRequest() method ends");

		return getRegistryInfoDocument;
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
	private GetRegistryInfo2 mapGetRegistryInfoRequest(ServiceRequestIF pRreqVO)
			throws BBBSystemException {

		logDebug("GetRegistryInfoServiceMarshaller.mapGetRegistryInfoRequest() method start");
		
		BBBPerformanceMonitor
				.start("GetRegistryInfoServiceMarshaller-mapGetRegistryInfoRequest");

		GetRegistryInfo2 getRegistryInfo = GetRegistryInfo2.Factory.newInstance();
		
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pRreqVO, getRegistryInfo);
		}catch(MappingException me) {
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10147 +" MappingException from mapGetRegistryInfoByProfileIdRequest from GetRegistryByProfileIdServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("GetRegistryInfoServiceMarshaller-mapGetRegistryInfoRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1371,me.getMessage(), me);			
		}finally{
			BBBPerformanceMonitor
					.end("GetRegistryInfoServiceMarshaller-mapGetRegistryInfoRequest");
		}
		logDebug("GetRegistryInfoServiceMarshaller.mapGetRegistryInfoRequest() method ends");
		return getRegistryInfo;
	}

}