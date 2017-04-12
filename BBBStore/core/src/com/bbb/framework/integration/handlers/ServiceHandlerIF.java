/*
 *
 * File  : ServiceHandlerIF.java
 * Project:     BBB
 */
package com.bbb.framework.integration.handlers;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.ServiceResponseIF;

/**
 * The Interface ServiceHandlerIF.
 * 
 * @version 1.0
 */
public interface ServiceHandlerIF {
	
	/**
	 * Invoke.
	 * 
	 * @param service the service
	 * @param voReq the vo req
	 * @return the service response if
	 * @throws Exception the exception
	 */
	public ServiceResponseIF invoke(final String service, final ServiceRequestIF voReq) throws BBBSystemException, BBBBusinessException;
}
