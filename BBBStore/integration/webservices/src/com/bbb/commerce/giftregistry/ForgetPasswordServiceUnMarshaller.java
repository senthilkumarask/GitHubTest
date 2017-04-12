package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ForgetPasswordResponseDocument;
import com.bedbathandbeyond.www.ForgetPasswordResponseDocument.ForgetPasswordResponse;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author sk134
 * 
 */
public class ForgetPasswordServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * default serial Id 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("ForgetPasswordServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("ForgetPasswordServiceUnMarshaller-processResponse");

		final RegistryResVO forgetPassResVO = new RegistryResVO();
		if (responseDocument != null) {
			try {

				final ForgetPasswordResponse forgetPassRes = ((ForgetPasswordResponseDocument) responseDocument)
						.getForgetPasswordResponse();

				if (forgetPassRes != null) {
					if (!forgetPassRes.getForgetPasswordResult().getStatus().getErrorExists()) {
						forgetPassResVO.getServiceErrorVO()
						.setErrorExists(false);
					} else {

						forgetPassResVO.getServiceErrorVO()
								.setErrorExists(true);
						forgetPassResVO.getServiceErrorVO().setErrorId(
								forgetPassRes.getForgetPasswordResult().getStatus().getID());
						forgetPassResVO.getServiceErrorVO()
								.setErrorMessage(forgetPassRes
												.getForgetPasswordResult().getStatus().getErrorMessage());
						forgetPassResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				forgetPassResVO.getServiceErrorVO()
				.setErrorExists(false);
				BBBPerformanceMonitor
				.end("ForgetPasswordServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1366,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("ForgetPasswordServiceUnMarshaller-processResponse");
			}
		}

		logDebug("ForgetPasswordServiceUnMarshaller.processResponse() method ends");
		
		return forgetPassResVO;
	}
}
