/*
 *
 * File  : ServiceHandlerFactoryIF.java
 * Project:     BBB
 */
package com.bbb.framework.integration.handlers;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.framework.webservices.ResponseUnMarshaller;

/**
 * The Interface ServiceHandlerFactoryIF.
 */
public interface ServiceHandlerFactoryIF {
	
	/**
	 * Gets the service marshaller.
	 * 
	 * @param service
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public RequestMarshaller getServiceMarshaller(String service) throws BBBSystemException, BBBBusinessException;
	
	/**
	 * Gets the service un marshaller.
	 * 
	 * @param service the service
	 * 
	 * @return the service un marshaller
	 * 
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public ResponseUnMarshaller getServiceUnMarshaller(String service) throws BBBSystemException, BBBBusinessException;
	
	/**
	 * Gets the service stub.
	 * 
	 * @param service the service
	 * @param endpoint the endpoint
	 * @return the service stub
	 * 
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public org.apache.axis2.client.Stub getServiceStub(String service, String endpoint) throws BBBSystemException, BBBBusinessException;
    
    /**
     * Gets the endpoint constant.
     * 
     * @param service the service
     * 
     * @return the endpoint constant
     */
    public String getEndpointConstant(String service) throws BBBBusinessException, BBBSystemException;
}
