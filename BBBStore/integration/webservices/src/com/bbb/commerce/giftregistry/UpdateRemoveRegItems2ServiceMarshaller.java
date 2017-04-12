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
import com.bedbathandbeyond.www.UpdateRegistryItems2Document;
import com.bedbathandbeyond.www.UpdateRegistryItems2Document.UpdateRegistryItems2;


/**
 * 
 * This class contain methods used for marshalling the webservice requests
 * for Update Registry Items and Remove Registry Items webcalls.
 * 
 * @author ikhan2
 * 
 */
public class UpdateRemoveRegItems2ServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a request.
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBBusinessException, BBBSystemException {

		logDebug("UpdateRemoveRegItems2ServiceMarshaller.buildRequest() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRemoveRegItems2ServiceMarshaller-buildRequest");
		
		UpdateRegistryItems2Document updateRegistryItemsDocument2 = null;

		updateRegistryItemsDocument2 = UpdateRegistryItems2Document.Factory.newInstance();
		updateRegistryItemsDocument2.setUpdateRegistryItems2(validateUpdateRegItems(pRreqVO));

		BBBPerformanceMonitor
				.end("UpdateRemoveRegItems2ServiceMarshaller-buildRequest");

		logDebug("UpdateRemoveRegItems2ServiceMarshaller.buildRequest() method end");
		
		return updateRegistryItemsDocument2;
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
	private UpdateRegistryItems2 validateUpdateRegItems(ServiceRequestIF pReqVO)throws BBBSystemException {

		logDebug("UpdateRemoveRegItems2ServiceMarshaller.validateUpdateRegItems() method start");
		
		BBBPerformanceMonitor
				.start("UpdateRemoveRegItems2ServiceMarshaller-buildUpdateRegItems");

		UpdateRegistryItems2 updateRegistryItems2 = UpdateRegistryItems2.Factory.newInstance();
		
		
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, updateRegistryItems2);
		}
		catch(MappingException me)
		{
			logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10157 +" MappingException from validateUpdateRegItems from UpdateRemoveRegItems2ServiceMarshaller",me);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1383,me.getMessage(), me);
		}finally{
			BBBPerformanceMonitor
			.end("UpdateRemoveRegItems2ServiceMarshaller-buildUpdateRegItems");
		}
	
		logDebug("UpdateRemoveRegItems2ServiceMarshaller.validateUpdateRegItems() method ends");
		return updateRegistryItems2;
	}

}