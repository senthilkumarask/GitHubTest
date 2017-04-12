package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.ForgetPasswordDocument;
import com.bedbathandbeyond.www.ForgetPasswordDocument.ForgetPassword;

/**
 * Marshalling the web service
 * 
 * @author sku134
 * 
 */
public class ForgetPasswordServiceMarshaller extends RequestMarshaller {

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

		logDebug("ForgetPasswordServiceMarshaller.buildRequest() method start");

		BBBPerformanceMonitor
				.start("ForgetPasswordServiceMarshaller-buildRequest");

		ForgetPasswordDocument forgetPasswordDocument = null;
		try {
			forgetPasswordDocument = ForgetPasswordDocument.Factory
					.newInstance();
			forgetPasswordDocument.setForgetPassword(mapImportRegistryRequest(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("ForgetPasswordServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1365,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
			.end("ForgetPasswordServiceMarshaller-buildRequest");
			
		}

		logDebug("ForgetPasswordServiceMarshaller.buildRequest() method ends");

		return forgetPasswordDocument;
	}

	/**
	 * This method is used to map a page specific VO to web service VO.
	 * 
	 * @param pReqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private ForgetPassword mapImportRegistryRequest(ServiceRequestIF pReqVO)
			throws BBBSystemException {

		logDebug("ForgetPasswordServiceMarshaller.mapCreateRegistryRequest() method start");

		BBBPerformanceMonitor
				.start("ForgetPasswordServiceMarshaller-mapCreateRegistryRequest");

		ForgetPassword forgetPassword = ForgetPassword.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pReqVO, forgetPassword);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("ForgetPasswordServiceMarshaller-mapCreateRegistryRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1365,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
					.end("ForgetPasswordServiceMarshaller-mapCreateRegistryRequest");
		}

		logDebug("ForgetPasswordServiceMarshaller.mapCreateRegistryRequest() method ends");

		return forgetPassword;
	}

}