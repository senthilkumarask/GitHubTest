package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.ForgetRegistryPasswordResponseDocument;
import com.bedbathandbeyond.www.ForgetRegistryPasswordResponseDocument.ForgetRegistryPasswordResponse;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author sk134
 * 
 */
public class ForgetRegistryPasswordServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * default serial Id 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("ForgetRegistryPasswordServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("ForgetRegistryPasswordServiceUnMarshaller-processResponse");

		final RegistryResVO forgetRegPassResVO = new RegistryResVO();
		if (responseDocument != null) {
			try {

				final ForgetRegistryPasswordResponse forgetRegPassRes = ((ForgetRegistryPasswordResponseDocument) responseDocument)
						.getForgetRegistryPasswordResponse();

				if (forgetRegPassRes != null) {
					if (!forgetRegPassRes.getForgetRegistryPasswordResult().getStatus().getErrorExists()) {
						forgetRegPassResVO.getServiceErrorVO()
						.setErrorExists(false);
					} else {

						forgetRegPassResVO.getServiceErrorVO()
								.setErrorExists(true);
						forgetRegPassResVO.getServiceErrorVO().setErrorId(
								forgetRegPassRes.getForgetRegistryPasswordResult().getStatus().getID());
						forgetRegPassResVO.getServiceErrorVO().setErrorMessage(forgetRegPassRes
												.getForgetRegistryPasswordResult().getStatus().getErrorMessage());
						forgetRegPassResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				forgetRegPassResVO.getServiceErrorVO()
				.setErrorExists(false);
				BBBPerformanceMonitor
				.end("ForgetRegistryPasswordServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1368,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("ForgetRegistryPasswordServiceUnMarshaller-processResponse");
			}
		}

		logDebug("ForgetRegistryPasswordServiceUnMarshaller.processResponse() method ends");
		
		return forgetRegPassResVO;
	}
}
