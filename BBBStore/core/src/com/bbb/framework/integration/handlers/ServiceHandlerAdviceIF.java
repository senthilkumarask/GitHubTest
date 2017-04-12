/*
 *
 * File  : ServiceHandlerAdviceIF.java
 * Project:     BBB
 */
package com.bbb.framework.integration.handlers;

import org.apache.xmlbeans.XmlObject;

/**
 * The Interface ServiceHandlerAdviceIF.
 * 
 * 
 * @version 1.0
 */
public interface ServiceHandlerAdviceIF {
	
	/**
	 * Before invoke.
	 * 
	 * @param service the service
	 * @param endPoint the end point
	 * @param requestDoc the request doc
	 * @param responseDoc the response doc
	 */
	void beforeInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc);

	/**
	 * After invoke.
	 * 
	 * @param service the service
	 * @param endPoint the end point
	 * @param requestDoc the request doc
	 * @param responseDoc the response doc
	 */
	void afterInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc);

	/**
	 * Cancel invoke.
	 * 
	 * @param service the service
	 * @param endPoint the end point
	 * @param requestDoc the request doc
	 * @param responseDoc the response doc
	 */
	void cancelInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc);
	
	/**
	 * After invoke.
	 * 
	 * @param service the service
	 * @param endPoint the end point
	 * @param requestDoc the request doc
	 * @param responseDoc the response doc
	 */
	void afterInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc, String timeTaken, String status);

	/**
	 * Cancel invoke.
	 * 
	 * @param service the service
	 * @param endPoint the end point
	 * @param requestDoc the request doc
	 * @param responseDoc the response doc
	 */
	void cancelInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc, String timeTaken, String status);
}
