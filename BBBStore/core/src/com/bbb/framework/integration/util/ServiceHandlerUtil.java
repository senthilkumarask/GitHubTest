/*
 *
 * File  : ServiceHandlerUtil.java
 * Project:     BBB
 */
package com.bbb.framework.integration.util;

import atg.nucleus.Nucleus;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.handlers.MsgServiceHandlerIF;
import com.bbb.framework.integration.handlers.ServiceHandlerIF;
import com.bbb.framework.messaging.handlers.MessageServiceHandler;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.BBBAPIConstants;
import com.bbb.framework.webservices.handlers.ServiceHandler;

/**
 * The Class ServiceHandlerUtil.
 * 
 * @version 1.0
 */
public final class ServiceHandlerUtil {
	/** The service handler. */
	
	/**
	 * dataFactory
	 */
	
	

	private static ServiceHandlerIF serviceHandler;// = getServicehandler();
	
	private static MsgServiceHandlerIF msgServiceHandlerIF;//=
	
	
	/** The logger util. */
	/*static final private LoggerUtil loggerUtil = 
		LoggerUtil.getInstance(ServiceHandlerUtil.class.getName());*/
	
	// TODO All web service call should call my Invoke method irrespective of
	// whether it should be cached or not. For example look at my
	// validateAddress method. This is just the example so I am keeping this
	// method other wise, you should keep it ouside this class.
	
	
	/**
	 * @return the msgservicehandlerif
	 */
	public static MsgServiceHandlerIF getMsgservicehandlerif() {
		
		if(msgServiceHandlerIF == null){

			msgServiceHandlerIF = (MessageServiceHandler) Nucleus.getGlobalNucleus().resolveName(BBBAPIConstants.MSG_SERVICE_HANDLER);
		}
		
		return msgServiceHandlerIF;
	}

	/**
	 * @param pMsgServiceHandlerIf the msgservicehandlerif to set
	 */
	public static void setMsgservicehandlerif(
			MsgServiceHandlerIF pMsgServiceHandlerIf) {
		msgServiceHandlerIF = pMsgServiceHandlerIf;
	}

	/**
	 * @return the servicehandler
	 */
	public static ServiceHandlerIF getServicehandler() {
		
		if(serviceHandler == null){
			serviceHandler = (ServiceHandler) Nucleus.getGlobalNucleus().resolveName(BBBAPIConstants.SERVICE_HANDLER);
			
		}
		
		return serviceHandler;
	}

	/**
	 * @param servicehandler the servicehandler to set
	 */
	public static void setServicehandler(ServiceHandlerIF servicehandler) {
		serviceHandler = servicehandler;
	}

	
	
	/**
	 * Invoke.
	 * 
	 * @param service
	 *            the service
	 * @param voReq
	 *            the vo req
	 * 
	 * @return the service response if
	 * 
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public static ServiceResponseIF invoke(final ServiceRequestIF voReq)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor.start("ServiceHandlerUtil-Invoke");

		String service = voReq.getServiceName();
		ServiceResponseIF voResp = null;
		final String perfLogName = new StringBuilder()
				.append("ServiceUtilInvoke-").append(service).toString();
		BBBPerformanceMonitor.start(perfLogName,
				BBBPerformanceConstants.WEB_SERVICE_CALL);
		voResp = getServicehandler().invoke(service, voReq);
		BBBPerformanceMonitor.end(perfLogName,
				BBBPerformanceConstants.WEB_SERVICE_CALL);
		BBBPerformanceMonitor.end("ServiceHandlerUtil-Invoke");

		return voResp;
	}
	
	/**
	 * Sends message to the queue where queue name is taken from
	 * ServiceRequestIF.getServiceName
	 * 
	 * @param voReq
	 * @return void
	 * @throws Exception 
	 */
	public static void send(final ServiceRequestIF voReq)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor.start("ServiceHandlerUtil-send");

		String service = voReq.getServiceName();

		final String perfLogName = new StringBuilder()
				.append("ServiceUtilSend-").append(service).toString();

		BBBPerformanceMonitor.start(perfLogName,
				BBBPerformanceConstants.MESSAGING_CALL);
		getMsgservicehandlerif().send(service, voReq);

		BBBPerformanceMonitor.end(perfLogName,
				BBBPerformanceConstants.MESSAGING_CALL);
		BBBPerformanceMonitor.end("ServiceHandlerUtil-send");
	}
	
	/**
	 * Sends message to the queue where queue name is taken from
	 * ServiceRequestIF.getServiceName
	 * 
	 * @param voReq
	 * @return void
	 * @throws Exception
	 */
	public static void sendTextMessage(final ServiceRequestIF voReq) throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor.start("ServiceHandlerUtil-sendTextMessage");

		String service = voReq.getServiceName();

		final String perfLogName = new StringBuilder().append("ServiceUtilSendTextMessage-").append(service).toString();

		BBBPerformanceMonitor.start(perfLogName, BBBPerformanceConstants.MESSAGING_CALL);
		getMsgservicehandlerif().sendTextMessage(service, voReq);

		BBBPerformanceMonitor.end(perfLogName, BBBPerformanceConstants.MESSAGING_CALL);
		BBBPerformanceMonitor.end("ServiceHandlerUtil-sendTextMessage");
	}
	
}
