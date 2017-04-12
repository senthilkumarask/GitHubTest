/*
 *
 * File  : ServiceHandlerPerformanceLogAdvice.java
 * Project:     BBB
 * 
 */

package com.bbb.framework.webservices.handlers;

import org.apache.xmlbeans.XmlObject;

import com.bbb.framework.integration.handlers.ServiceHandlerAdviceIF;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * The Class ServiceHandlerPerformanceLogAdvice.
 */
public class ServiceHandlerPerformanceLogAdvice implements
		ServiceHandlerAdviceIF {
	
	/**
	 * Sets the log constant.
	 * 
	 * @param service the service
	 * @param endPoint the end point
	 * 
	 * @return the string
	 */
	protected final String setLogConstant(final String service, final String endPoint) {
		return new StringBuilder().append("Webservice-").append(service)
				.append(" (").append(endPoint).append(")").toString();
	}
	
	/* (non-Javadoc)
	 * @see com.bbb.integration.api.servicehandler.ServiceHandlerAdviceIF#beforeInvoke(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE, java.lang.String, org.apache.xmlbeans.XmlObject, org.apache.xmlbeans.XmlObject)
	 */
	public void beforeInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc) {

		BBBPerformanceMonitor
		.start("ServiceHandlerPerformanceLogAdvice-beforeInvoke");
	
		String perfLogName = setLogConstant(service, endPoint);
		BBBPerformanceMonitor.start(perfLogName,
				BBBPerformanceConstants.WEB_SERVICE_CALL);

		BBBPerformanceMonitor
		.end("ServiceHandlerPerformanceLogAdvice-beforeInvoke");
	
	}

	public void afterInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc) {

		BBBPerformanceMonitor
		.start("ServiceHandlerPerformanceLogAdvice-afterInvoke");
	
		String perfLogName = setLogConstant(service, endPoint);
		BBBPerformanceMonitor.end(perfLogName,
				BBBPerformanceConstants.WEB_SERVICE_CALL);

		BBBPerformanceMonitor
		.end("ServiceHandlerPerformanceLogAdvice-afterInvoke");
	
	}

	public void cancelInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc) {

		BBBPerformanceMonitor
		.start("ServiceHandlerPerformanceLogAdvice-cancelInvoke");
	
		String perfLogName = setLogConstant(service, endPoint);
		BBBPerformanceMonitor.cancel(perfLogName,
				BBBPerformanceConstants.WEB_SERVICE_CALL);

		BBBPerformanceMonitor
		.end("ServiceHandlerPerformanceLogAdvice-cancelInvoke");
	
	}
	@Override
	public void afterInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc, String timeTaken, String status) {

		//do nothing
	
	}
	@Override
	public void cancelInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc, String timeTaken, String status) {

		//do nothing
	
	}

}
