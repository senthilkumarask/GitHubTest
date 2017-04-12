package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ImportRegistryResponseDocument;
import com.bedbathandbeyond.www.ImportRegistryResponseDocument.ImportRegistryResponse;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author sk134
 * 
 */
public class ImportRegistryServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * default serial Id 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("ImportRegistryServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("ImportRegistryServiceUnMarshaller-processResponse");

		final RegistryResVO importRegistryResVO = new RegistryResVO();
		if (responseDocument != null) {
			try {

				final ImportRegistryResponse importRegRes = ((ImportRegistryResponseDocument) responseDocument)
						.getImportRegistryResponse();

				if (importRegRes != null && importRegRes.getImportRegistryResult() !=null) {
					if (!importRegRes.getImportRegistryResult().getStatus().getErrorExists()) {
						importRegistryResVO.getServiceErrorVO()
						.setErrorExists(false);
						

						final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
						try {
							mapper.map(importRegRes, importRegistryResVO);

						} catch (MappingException me) {
							logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10152 +" MappingException from processResponse from ImportRegistryServiceUnMarshaller",me);
							throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1376,me.getMessage(), me);
						}
						
					} else {

						importRegistryResVO.getServiceErrorVO()
								.setErrorExists(true);
						importRegistryResVO.getServiceErrorVO().setErrorId(
								importRegRes.getImportRegistryResult().getStatus().getID());
						importRegistryResVO.getServiceErrorVO()
								.setErrorMessage(importRegRes
												.getImportRegistryResult().getStatus().getErrorMessage());
						importRegistryResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10155 +" Exception from processResponse from ImportRegistryServiceUnMarshaller",e);
				BBBPerformanceMonitor
				.end("ImportRegistryServiceUnMarshaller-processResponse");
				importRegistryResVO.getServiceErrorVO()
				.setErrorExists(false);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1376,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("ImportRegistryServiceUnMarshaller-processResponse");
			}
		}

		logDebug("ImportRegistryServiceUnMarshaller.processResponse() method ends");
		
		return importRegistryResVO;
	}
}
