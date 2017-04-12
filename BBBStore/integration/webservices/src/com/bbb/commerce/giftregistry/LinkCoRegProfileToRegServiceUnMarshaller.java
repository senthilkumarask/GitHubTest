package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.LinkCoRegProfileToRegistryResponseDocument;
import com.bedbathandbeyond.www.LinkCoRegProfileToRegistryResponseDocument.LinkCoRegProfileToRegistryResponse;


/**
 * This class contain methods used for unmarshalling the link coReg Profile to Reg webservice response.
 * 
 * @author ssha53
 * 
 */
public class LinkCoRegProfileToRegServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		logDebug("LinkCoRegProfileToRegServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("LinkCoRegProfileToRegServiceUnMarshaller-processResponse");

		final RegistryResVO linkCoRegToRegVO = new RegistryResVO();
		
		if (responseDocument != null) {

			final LinkCoRegProfileToRegistryResponse linkCoRegRes = ((LinkCoRegProfileToRegistryResponseDocument) 
					responseDocument).getLinkCoRegProfileToRegistryResponse();
			try {
				
				if (linkCoRegRes != null) {
					if (!linkCoRegRes.getLinkCoRegProfileToRegistryResult().getStatus().getErrorExists()) {
						//operation success
						linkCoRegToRegVO.getServiceErrorVO().setErrorExists(false);
					} else {

						linkCoRegToRegVO.getServiceErrorVO()
								.setErrorExists(true);
						linkCoRegToRegVO.getServiceErrorVO().setErrorId(
								linkCoRegRes.getLinkCoRegProfileToRegistryResult().getStatus().getID());
						linkCoRegToRegVO.getServiceErrorVO()
								.setErrorMessage(linkCoRegRes
												.getLinkCoRegProfileToRegistryResult().getStatus().getErrorMessage());
						linkCoRegToRegVO.setWebServiceError(true);
					}
				}
			} catch (Exception e) {
				BBBPerformanceMonitor
				.end("LinkCoRegProfileToRegServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1378,e.getMessage(), e);
			}finally{			
			BBBPerformanceMonitor
					.end("LinkCoRegProfileToRegServiceUnMarshaller-processResponse");
			}

			logDebug("LinkCoRegProfileToRegServiceUnMarshaller.processResponse() method ends");
		
		}
		return linkCoRegToRegVO;
		
	}

}
