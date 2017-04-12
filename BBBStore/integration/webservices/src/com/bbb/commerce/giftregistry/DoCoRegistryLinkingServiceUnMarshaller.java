/*
 *
 * File  : GetRegistryInfoServiceUnMarshaller.java
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

import com.bedbathandbeyond.www.LinkCoRegToRegResponseDocument;
import com.bedbathandbeyond.www.LinkCoRegToRegResponseDocument.LinkCoRegToRegResponse;


/**
 * This class contain methods used for unmarshalling the link coReg to Reg webservice response.
 * 
 * @author skalr2
 * 
 */
public class DoCoRegistryLinkingServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("DoCoRegistryLinkingServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("DoCoRegistryLinkingServiceUnMarshaller-processResponse");

		final RegistryResVO linkCoRegToRegVO = new RegistryResVO();
		
		if (responseDocument != null) {

			final LinkCoRegToRegResponse linkCoRegRes = ((LinkCoRegToRegResponseDocument) 
					responseDocument).getLinkCoRegToRegResponse();
			try {
				
				if (linkCoRegRes != null) {
					if (!linkCoRegRes.getLinkCoRegToRegResult().getStatus().getErrorExists()) {
						//operation success
						linkCoRegToRegVO.getServiceErrorVO().setErrorExists(false);
					} else {

						linkCoRegToRegVO.getServiceErrorVO()
								.setErrorExists(true);
						linkCoRegToRegVO.getServiceErrorVO().setErrorId(
								linkCoRegRes.getLinkCoRegToRegResult().getStatus().getID());
						linkCoRegToRegVO.getServiceErrorVO()
								.setErrorMessage(linkCoRegRes
												.getLinkCoRegToRegResult().getStatus().getErrorMessage());
						linkCoRegToRegVO.setWebServiceError(true);
					}
				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("DoCoRegistryLinkingServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1364,e.getMessage(), e);
			}			

			BBBPerformanceMonitor
					.end("DoCoRegistryLinkingServiceUnMarshaller-processResponse");

			logDebug("DoCoRegistryLinkingServiceUnMarshaller.processResponse() method ends");
		
		}
		return linkCoRegToRegVO;
		
	}

}
