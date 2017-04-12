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
import com.bedbathandbeyond.www.UpdateRegistryItemsDocument;
import com.bedbathandbeyond.www.UpdateRegistryItemsDocument.UpdateRegistryItems;


/**
 * 
 * This class contain methods used for marshalling the webservice requests
 * for Update Registry Items and Remove Registry Items webcalls.
 * 
 * @author ikhan2
 * 
 */
public class UpdateRemoveRegItemsServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBBusinessException, BBBSystemException {

		logDebug("UpdateRemoveRegItemsServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRemoveRegItemsServiceMarshaller-buildRequest");

		
		UpdateRegistryItemsDocument updateRegistryItemsDocument = null;
		

		updateRegistryItemsDocument = UpdateRegistryItemsDocument.Factory.newInstance();
		updateRegistryItemsDocument.setUpdateRegistryItems(validateUpdateRegItems(pRreqVO));

		BBBPerformanceMonitor
				.end("UpdateRemoveRegItemsServiceMarshaller-buildRequest");

		logDebug("UpdateRemoveRegItemsServiceMarshaller.buildRequest() method end");
		
		return updateRegistryItemsDocument;
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
	private UpdateRegistryItems validateUpdateRegItems(ServiceRequestIF pReqVO)throws BBBSystemException {

		logDebug("UpdateRemoveRegItemsServiceMarshaller.validateUpdateRegItems() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRemoveRegItemsServiceMarshaller-buildUpdateRegItems");

		UpdateRegistryItems updateRegistryItems = UpdateRegistryItems.Factory.newInstance();
		
		
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, updateRegistryItems);
		}
		catch(MappingException me)
		{
			BBBPerformanceMonitor
			.end("UpdateRemoveRegItemsServiceMarshaller-buildUpdateRegItems");
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10157 +" MappingException from validateUpdateRegItems from UpdateRemoveRegItemsServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1383,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
			.end("UpdateRemoveRegItemsServiceMarshaller-buildUpdateRegItems");
		}
	
		logDebug("UpdateRemoveRegItemsServiceMarshaller.validateUpdateRegItems() method ends");
		return updateRegistryItems;
	}

}