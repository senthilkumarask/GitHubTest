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
import com.bedbathandbeyond.www.AddItemsToRegistryDocument;
import com.bedbathandbeyond.www.AddItemsToRegistryDocument.AddItemsToRegistry;
import com.bedbathandbeyond.www.RegCopyDocument;
import com.bedbathandbeyond.www.RegCopyDocument.RegCopy;


/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author ssha53
 * 
 */
public class RegCopyServiceMarshaller extends RequestMarshaller {

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
				.start("RegCopyServiceMarshaller-buildRequest");

		RegCopyDocument  regdocument = null;
	
		regdocument = RegCopyDocument.Factory.newInstance();
		regdocument.setRegCopy(validateRegCopy(pRreqVO) );

		BBBPerformanceMonitor
				.end("RegCopyServiceMarshaller-buildRequest");

		return regdocument;
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
	private RegCopy validateRegCopy(ServiceRequestIF pReqVO)throws BBBSystemException {

		logDebug("RegCopyServiceMarshaller.validateAddItemToRegistry() method start");
		
		BBBPerformanceMonitor
				.start("RegCopyServiceMarshaller-buildValidateAddressType");

		 RegCopy regCopy = RegCopy.Factory.newInstance();
		//Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, regCopy);
		}
		catch(MappingException me)
		{
			BBBPerformanceMonitor
			.end("RegCopyServiceMarshaller-buildValidateAddressType");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10140 +" MappingException from validateAddItemToRegistry from AddItemsToRegistryServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1358,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
			.end("RegCopyServiceMarshaller-buildValidateAddressType");
		}
	
		logDebug("RegCopyServiceMarshaller.validateAddItemToRegistry() method ends");

		return regCopy;
	}


}