/*
 *
 * File  : RequestMarshallerIF.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.integration;

import java.io.Serializable;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * The Interface MsgMarshallerIF.
 * 
 * 
 * @version 1.0
 */
public interface RequestMarshallerIF extends Serializable {
	
	/**
	 * Builds the request.
	 * 
	 * @param reqVO the req vo
	 * 
	 * @return the xml object
	 * 
	 * @throws Exception the exception
	 */
	public XmlObject buildRequest(final ServiceRequestIF reqVO) throws BBBSystemException, BBBBusinessException;
	
	/**
	 * Builds the header.
	 * 
	 * @param version the version
	 * 
	 * @return the xml object
	 * 
	 * @throws Exception the exception
	 */
	public XmlObject buildHeader()	throws BBBSystemException, BBBBusinessException;
}