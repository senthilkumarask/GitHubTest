/*
 *
 * File  : UpdateRegistryServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.UpdateRegistryResponseDocument;
import com.bedbathandbeyond.www.UpdateRegistryResponseDocument.UpdateRegistryResponse;


/**
 * This class contain methods used for unmarshalling the Update registry webservice response.
 * 
 * @author ssha53
 * 
 */
public class UpdateRegistryServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("UpdateRegistryServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("UpdateRegistryServiceUnMarshaller-processResponse");
		
		final RegistryResVO registryResVO = new RegistryResVO();
		if (responseDocument != null) {
			try {

				final UpdateRegistryResponse updateRegRes = ((UpdateRegistryResponseDocument) responseDocument)
						.getUpdateRegistryResponse();

				if (updateRegRes != null) {
					if (!updateRegRes.getUpdateRegistryResult().getStatus().getErrorExists()) {
						registryResVO.getServiceErrorVO()
						.setErrorExists(false);
					} else {

						registryResVO.getServiceErrorVO()
								.setErrorExists(true);
						registryResVO.getServiceErrorVO().setErrorId(
								updateRegRes.getUpdateRegistryResult().getStatus().getID());
						registryResVO.getServiceErrorVO()
								.setErrorMessage(updateRegRes
												.getUpdateRegistryResult().getStatus().getErrorMessage());
						registryResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("UpdateRegistryServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1382,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("UpdateRegistryServiceUnMarshaller-processResponse");
			}
		}

		logDebug("UpdateRegistryServiceUnMarshaller.processResponse() method ends");
		
		return registryResVO;
	}
}
