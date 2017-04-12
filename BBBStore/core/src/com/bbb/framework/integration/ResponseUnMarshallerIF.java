/*
 *
 * File  : ResponseUnMarshallerIF.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.integration;

import java.io.Serializable;
import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * The Interface ResponseUnMarshallerIF.
 * 
 * 
 * @version 1.0
 */
public interface ResponseUnMarshallerIF extends Serializable {
	
	/**
	 * Process response.
	 * 
	 * @param responseDocument the response document
	 * 
	 * @return the service response if
	 * 
	 * @throws Exception the exception
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument) throws BBBSystemException, BBBBusinessException;
}
