/*
 *
 * File  : ServiceHandlerInfoLogAdvice.java
 * Project:     BBB
*/
package com.bbb.framework.webservices.handlers;

import org.apache.xmlbeans.XmlObject;

import com.bbb.framework.integration.handlers.ServiceHandlerAdviceIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * The Class ServiceHandlerInfoLogAdvice.
 */
public class ServiceHandlerInfoLogAdvice implements ServiceHandlerAdviceIF {

	/** The logger util. */
	// TODO Manohar
	// TODO ServiceHandler 2 - uncomment once loggerUtil is in place
	/*protected LoggerUtil loggerUtil = LoggerUtil.getInstance(this.getClass()
			.getName());*/

	/* (non-Javadoc)
	 * @see com.bbb.integration.api.servicehandler.ServiceHandlerAdviceIF#beforeInvoke(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE, java.lang.String, org.apache.xmlbeans.XmlObject, org.apache.xmlbeans.XmlObject)
	 */
	public void beforeInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc) {
		
		BBBPerformanceMonitor
		.start("ServiceHandlerInfoLogAdvice-beforeInvoke");
		
		// TODO Use me for anything that needs to be done before invoking the
		// service. Also see afterInvoke method
		//String requestDocXml=null;
		
		// Edited by Manohar
		// TODO: ServiceHandler 3 - uncomment once loggerUtil is in place
		/*loggerUtil.logInfo(this.getClass().getName(), service + " Request = "
				+ requestDocXml);*/
		BBBPerformanceMonitor
		.end("ServiceHandlerInfoLogAdvice-beforeInvoke");
		
	}

	/* (non-Javadoc)
	 * @see com.bbb.integration.api.servicehandler.ServiceHandlerAdviceIF#afterInvoke(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.String, java.lang.String, org.apache.xmlbeans.XmlObject, org.apache.xmlbeans.XmlObject)
	 */
	public void afterInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc, String timeTaken, String status) {

		BBBPerformanceMonitor
		.start("ServiceHandlerInfoLogAdvice-afterInvoke");
		//String requestDocXml=null;	
		//String responseDocXml=null;
		
		// TODO Use me for anything that needs to be done after invoking the
		// service. It could be for masking (if you want to mask important
		// stuff) or anything to be masked before logging into logs
		
		BBBPerformanceMonitor
		.end("ServiceHandlerInfoLogAdvice-afterInvoke");

	}

	/* (non-Javadoc)
	 * @see com.bbb.integration.api.servicehandler.ServiceHandlerAdviceIF#cancelInvoke(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE, java.lang.String, org.apache.xmlbeans.XmlObject, org.apache.xmlbeans.XmlObject)
	 */
	public void cancelInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc, String timeTaken, String status) {
		
		// TODO Use me to log the time taken and when there is nothing to log
		// after getting the response and before going to next task
		// Say for example persisting ERROR request reply XML to DB
		// Edited by Manohar
		// TODO: ServiceHandler 6 - uncomment once loggerUtil is in place
		/*loggerUtil.logPersistedInfo(service, new StringBuilder()
		.append("Request = ")
		.append((requestDoc==null) ? null : requestDoc.xmlText())
		.append(" | Response = ")
		.append((responseDoc==null) ? null : responseDoc.xmlText()).toString(), timeTaken, status);*/
	}
	
/*	private  String numStringPadding(String str, int digits){
		String returnString = str;

		if(str.length() < digits) {
			StringBuilder tempString = new StringBuilder();
			for (int i = str.length(); i < digits; i++) {
				tempString.append("0");
			} 
			returnString = tempString.toString() + str;
		}

		return returnString;
	}*/
	@Override
	public void afterInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc){
		//do nothing
	}
	@Override
	public void cancelInvoke(final String service, final String endPoint,
			final XmlObject requestDoc, final XmlObject responseDoc){
		//do nothing
	}

}
