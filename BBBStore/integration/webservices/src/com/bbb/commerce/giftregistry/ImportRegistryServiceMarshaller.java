package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.ImportRegistryDocument;
import com.bedbathandbeyond.www.ImportRegistryDocument.ImportRegistry;

/**
 * Marshalling the web service
 * 
 * @author sku134
 * 
 */
public class ImportRegistryServiceMarshaller extends RequestMarshaller {

	/**
	 * default serial verson Id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 * 
	 * @param ServiceRequestIF
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBSystemException {

		logDebug("ImportRegistryServiceMarshaller.buildRequest() method start");

		BBBPerformanceMonitor
				.start("ImportRegistryServiceMarshaller-buildRequest");

		ImportRegistryDocument importRegistryDocument = null;
		try {
			importRegistryDocument = ImportRegistryDocument.Factory
					.newInstance();
			importRegistryDocument
					.setImportRegistry(mapImportRegistryRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("ImportRegistryServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1375,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
			.end("ImportRegistryServiceMarshaller-buildRequest");
			
		}

		logDebug("ImportRegistryServiceMarshaller.buildRequest() method ends");

		return importRegistryDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param pReqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private ImportRegistry mapImportRegistryRequest(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("ImportRegistryServiceMarshaller.mapCreateRegistryRequest() method start");

		BBBPerformanceMonitor
				.start("ImportRegistryServiceMarshaller-mapCreateRegistryRequest");

		ImportRegistry importRegistry = ImportRegistry.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pReqVO, importRegistry);
		} catch (MappingException me) {
			BBBPerformanceMonitor
			.end("ImportRegistryServiceMarshaller-mapCreateRegistryRequest");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10151 +" MappingException from mapImportRegistryRequest from ImportRegistryServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1375,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
					.end("ImportRegistryServiceMarshaller-mapCreateRegistryRequest");
		}

		logDebug("ImportRegistryServiceMarshaller.mapCreateRegistryRequest() method ends");

		return importRegistry;
	}

}