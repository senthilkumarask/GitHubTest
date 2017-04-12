package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.LinkRegistryToATGProfileResponseDocument;
import com.bedbathandbeyond.www.LinkRegistryToATGProfileResponseDocument.LinkRegistryToATGProfileResponse;


/**
 * This class contain methods used for unmarshalling the link coReg Profile to Reg webservice response.
 * 
 * @author ssha53
 * 
 */
public class LinkRegistryToATGProfileServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("LinkRegistryToATGProfileUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("LinkRegistryToATGProfileUnMarshaller-processResponse");

		RegistryResVO linkRegToATGProfileResVO = new RegistryResVO();
		
		if (responseDocument != null) {

			final LinkRegistryToATGProfileResponse linkRegToATGProfileRes = ((LinkRegistryToATGProfileResponseDocument) 
					responseDocument).getLinkRegistryToATGProfileResponse();
			try {
				
				if (linkRegToATGProfileRes != null) {
					if (!linkRegToATGProfileRes.getLinkRegistryToATGProfileResult().getStatus().getErrorExists()) {
						//operation success
						linkRegToATGProfileResVO.getServiceErrorVO().setErrorExists(false);
					} else {

						linkRegToATGProfileResVO.getServiceErrorVO()
								.setErrorExists(true);
						linkRegToATGProfileResVO.getServiceErrorVO().setErrorId(
								linkRegToATGProfileRes.getLinkRegistryToATGProfileResult().getStatus().getID());
						linkRegToATGProfileResVO.getServiceErrorVO()
								.setErrorMessage(linkRegToATGProfileRes
												.getLinkRegistryToATGProfileResult().getStatus().getErrorMessage());
						linkRegToATGProfileResVO.setWebServiceError(true);
					}
				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("LinkRegistryToATGProfileUnMarshaller-processResponse");
				throw new BBBSystemException(e.getMessage(), e);
			}finally{			
			BBBPerformanceMonitor
					.end("LinkRegistryToATGProfileUnMarshaller-processResponse");
			}

			logDebug("LinkRegistryToATGProfileUnMarshaller.processResponse() method ends");
		
		}
		return linkRegToATGProfileResVO;
		
	}

}
