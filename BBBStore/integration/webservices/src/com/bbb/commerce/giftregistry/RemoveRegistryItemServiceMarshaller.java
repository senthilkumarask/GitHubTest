/*
 *
 * File  : CreateRegistryServiceMarshaller.java
 * Project:     BBB
 * 
 */
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


/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author ikhan2
 * 
 */
public class RemoveRegistryItemServiceMarshaller extends RequestMarshaller {

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
				.start("RemoveRegistryItemServiceMarshaller-buildRequest");

		AddItemsToRegistryDocument addItemsToRegistryDocument = null;
		try {
			addItemsToRegistryDocument = AddItemsToRegistryDocument.Factory.newInstance();
			addItemsToRegistryDocument.setAddItemsToRegistry(validateAddItemToRegistry(pRreqVO));
		} catch (Exception e) {
			BBBPerformanceMonitor
			.end("RemoveRegistryItemServiceMarshaller-buildRequest");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1379,e.getMessage(), e);
		}finally{
			BBBPerformanceMonitor
					.end("RemoveRegistryItemServiceMarshaller-buildRequest");
		}

		return addItemsToRegistryDocument;
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
	private AddItemsToRegistry validateAddItemToRegistry(ServiceRequestIF pReqVO)throws BBBSystemException {

		logDebug("RemoveRegistryItemServiceMarshaller.validateAddItemToRegistry() method start");
		
		BBBPerformanceMonitor
				.start("RemoveRegistryItemServiceMarshaller-buildValidateAddressType");

		AddItemsToRegistry addItemsToRegistry = AddItemsToRegistry.Factory.newInstance();
		//Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, addItemsToRegistry);
		}
		catch(MappingException me)
		{
			BBBPerformanceMonitor
			.end("RemoveRegistryItemServiceMarshaller-buildValidateAddressType");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10154 +" MappingException from validateAddItemToRegistry from RemoveRegistryItemServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1379,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
			.end("RemoveRegistryItemServiceMarshaller-buildValidateAddressType");
		}
	
		logDebug("RemoveRegistryItemServiceMarshaller.validateAddItemToRegistry() method ends");
		return addItemsToRegistry;
	}

}