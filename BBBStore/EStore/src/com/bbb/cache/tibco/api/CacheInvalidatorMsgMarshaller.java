/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Akhajuria
 *
 * Created on: 11-April-2012
 * --------------------------------------------------------------------------------
 */

package com.bbb.cache.tibco.api;

import java.io.StringWriter;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.bbb.cache.tibco.vo.CacheInvalidatorVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.cache.CACHEINAVLIDATEREQUEST;
import com.bbb.framework.jaxb.cache.Main;
import com.bbb.framework.messaging.MessageMarshaller;

/**
 * 
 * This class provides the low level functionality for Subscription Module
 * 
 * 
 */

public class CacheInvalidatorMsgMarshaller extends MessageMarshaller {

	private static final long serialVersionUID = 1L;
	
	/**
	 * This method do marshing activities for TIBCO services of Baby Book request
	 * 
	 * @param pReqVO 
	 * @param pMessage 
	 * @return Void
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public String marshall(ServiceRequestIF pReqVO)
			throws BBBSystemException, BBBBusinessException {
		
		String xmlMessage = null;
		CacheInvalidatorVO cacheInavlidatorVO = (CacheInvalidatorVO) pReqVO;
		CACHEINAVLIDATEREQUEST cacheInavlidateRequest = new CACHEINAVLIDATEREQUEST(); 
 
		cacheInavlidateRequest.setCLEARDROPLETCACHE(cacheInavlidatorVO.isClearDropletCache());
		cacheInavlidateRequest.setCLEAROBJECTCACHE(cacheInavlidatorVO.isClearObjectCache());
		if(cacheInavlidatorVO.getObjectCacheTypes()!=null){
			cacheInavlidateRequest.getOBJECTCACHETYPE().addAll(cacheInavlidatorVO.getObjectCacheTypes());
		}
		Main mainRequest = new Main();
		mainRequest.setCACHEINVALIDATERECORD(cacheInavlidateRequest);
		StringWriter stringWriter = null;

		try {
			stringWriter = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(Main.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(mainRequest, stringWriter);
			xmlMessage = stringWriter.getBuffer().toString();
		} catch (PropertyException propertyException) {
			throw new BBBSystemException(propertyException.getErrorCode(),propertyException.getMessage(),
					propertyException);
		} catch (JAXBException jaxbException) {
			throw new BBBSystemException(jaxbException.getErrorCode(),jaxbException.getMessage(),
					jaxbException);
		} finally {

			if (stringWriter != null) {
				stringWriter = null;
			}
		}
		return xmlMessage;
	}

}
