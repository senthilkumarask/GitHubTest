package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.ForgetRegistryPasswordDocument;
import com.bedbathandbeyond.www.ForgetRegistryPasswordDocument.ForgetRegistryPassword;


/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author ssha53
 * 
 */
public class ForgetRegistryPasswordServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor
				.start("ForgetRegistryPasswordServiceMarshaller-buildRequest");

		ForgetRegistryPasswordDocument forgetRegistryPasswordDocument = null;
		
		forgetRegistryPasswordDocument = ForgetRegistryPasswordDocument.Factory.newInstance();
		forgetRegistryPasswordDocument.setForgetRegistryPassword(validateForgetRegistryPassword(pRreqVO));


		BBBPerformanceMonitor
				.end("ForgetRegistryPasswordServiceMarshaller-buildRequest");

		return forgetRegistryPasswordDocument;
	}

	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * 
	 * @param validateAddressReqVO
	 *            the validate address req vo
	 * 
	 * @return the validate address type
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private ForgetRegistryPassword validateForgetRegistryPassword(ServiceRequestIF pReqVO)throws BBBSystemException {

		logDebug("ForgetRegistryPasswordServiceMarshaller.validateAddItemToRegistry() method start");
		
		BBBPerformanceMonitor
				.start("ForgetRegistryPasswordServiceMarshaller-buildValidateAddressType");

		ForgetRegistryPassword forgetRegistryPassword = ForgetRegistryPassword.Factory.newInstance();
		//Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, forgetRegistryPassword);
		}
		catch(MappingException me)
		{
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10145 +" MappingException from validateForgetRegistryPassword from ForgetRegistryPasswordServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("ForgetRegistryPasswordServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1367,me.getMessage(), me);
		}
		catch(Exception me)
		{
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10146 +" Exception from validateForgetRegistryPassword from ForgetRegistryPasswordServiceMarshaller",me);
			BBBPerformanceMonitor
			.end("ForgetRegistryPasswordServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1367,me.getMessage(), me);
		}
		finally{
			BBBPerformanceMonitor
			.end("ForgetRegistryPasswordServiceMarshaller-buildValidateAddressType");
		}
	
		logDebug("ForgetRegistryPasswordServiceMarshaller.validateAddItemToRegistry() method ends");

		return forgetRegistryPassword;
	}


}