/*
 *
 * File  : ServiceHandler.java
 * Project:     BBB
 *
 *
 *
 * HISTORY:
 * Initial Version: 12/01/2011
 * Modified:	Lokesh Duseja:	12/06/2011:		Setting Authentication Options for client. User name and password are taken from BBBWebServicesConfig
 * Modified:	Lokesh Duseja:	12/08/2011:		1)Changed the class to extend GenericService. 2)Added Logging related code.
 */
package com.bbb.framework.webservices.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.handlers.ServiceHandlerAdviceIF;
import com.bbb.framework.integration.handlers.ServiceHandlerFactoryIF;
import com.bbb.framework.integration.handlers.ServiceHandlerIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.BBBWebservicesConfig;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.utils.BBBUtility;

/**
 *
 *
 * @version 1.0
 */
public class ServiceHandler extends BBBGenericService implements ServiceHandlerIF {

	/** The logger util. */
	/*
	 * private static final LoggerUtil loggerUtil =
	 * LoggerUtil.getInstance(ServiceHandler.class.getName());
	 */

	/** The service method cache. */
	private static Map<String, Method> serviceMethodCache = new ConcurrentHashMap<String, Method>();

	/** The service cross-cutting concern's advice(s). */
	private ServiceHandlerAdviceIF[] serviceAdvices;

	/** The service factory. */
	private ServiceHandlerFactoryIF serviceFactory;

	private BBBWebservicesConfig bbbWebServicesConfig;

	private BBBCatalogTools mCatalogTools;

	public BBBWebservicesConfig getBbbWebServicesConfig() {
		return this.bbbWebServicesConfig;
	}

	public void setBbbWebServicesConfig(
			final BBBWebservicesConfig bbbWebServicesConfig) {
		this.bbbWebServicesConfig = bbbWebServicesConfig;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * Gets the service factory.
	 *
	 * @return the service factory
	 */
	public final ServiceHandlerFactoryIF getServiceFactory() {
		return this.serviceFactory;
	}

	/**
	 * Sets the service factory.
	 *
	 * @param serviceFactory
	 *            the new service factory
	 */
	public final void setServiceFactory(
			final ServiceHandlerFactoryIF serviceFactory) {
		this.serviceFactory = serviceFactory;
	}

	/**
	 * Gets the service advices.
	 *
	 * @return the service advices
	 */
	public final ServiceHandlerAdviceIF[] getServiceAdvices() {
		return this.serviceAdvices;
	}

	/**
	 * Sets the service advices.
	 *
	 * @param serviceAdvices
	 *            the new service advices
	 */
	public final void setServiceAdvices(
			final ServiceHandlerAdviceIF[] serviceAdvices) {
		this.serviceAdvices = serviceAdvices;
	}

	/**
	 * Fetch api method.
	 *
	 * @param service
	 *            the service
	 * @param serviceStub
	 *            the service stub
	 *
	 * @return the method
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	protected Method fetchAPIMethod(final String service, final Stub serviceStub)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor.start("ServiceHandler-fetchAPIMethod");

		Method stubMethod = serviceMethodCache.get(service);
		if (stubMethod == null) {
			final String serviceMethod = getServiceFactory()
					.getEndpointConstant(service);
			// final String serviceMethod =
			// getCatalogTools().getAllValuesForKey("WSEndPoint",service).get(0);
			for (Method m : serviceStub.getClass().getMethods()) {
				if (serviceMethod.equals(m.getName())) {
					// found the method to call
					stubMethod = m;
					// store in cache for future use
					serviceMethodCache.put(service, stubMethod);
					/*
					 * if (loggerUtil.isLoggingInfo()) {
					 * loggerUtil.logInfo("*** " +
					 * m.getParameterTypes().length); }
					 */
					break;
				}
			} // end for
		} // end if
		BBBPerformanceMonitor.end("ServiceHandler-fetchAPIMethod");
		return stubMethod;
	}

	/**
	 * Fetch api method.
	 *
	 * @param service
	 *            - the constant for web-service operation
	 * @param voReq
	 *            - the thin request object
	 *
	 * @return the service response if
	 *
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public ServiceResponseIF invoke(final String service,
			final ServiceRequestIF voReq) throws BBBSystemException,
			BBBBusinessException {


		logDebug("ServiceHandler::invoke method started");


		BBBPerformanceMonitor.start("ServiceHandler-invoke:" + service);

		/** The marshaller. */
		RequestMarshaller marshaller = null;

		/** The unmarshaller. */
		ResponseUnMarshaller unmarshaller = null;

		/** The service stub. */
		Stub serviceStub = null;

		ServiceResponseIF voResp = null;

		logDebug("Create request document, Invoke " + service
					+ " Webservice, and parse response");

		XmlObject responseDocument = null;
		XmlObject requestDocument = null;
		XmlObject header = null;

		try {
			marshaller = getServiceFactory().getServiceMarshaller(service);

			if(isLoggingDebug()){
				logDebug("Marshaller =" +marshaller);
			}

			unmarshaller = getServiceFactory().getServiceUnMarshaller(service);
			if(isLoggingDebug()){
				logDebug("UnMarshaller =" +unmarshaller);
			}

			requestDocument = marshaller.buildRequest(voReq);

			logDebug("============================Request======================================");
			if(isLoggingDebug()){
				logDebug(requestDocument.toString());
			}
			logDebug("=========================================================================");


			header = marshaller.buildHeader();
			if(isLoggingDebug()){
				logDebug(" for Marshaller =" +marshaller+" the hearder = "+ header);
			}

			final String servicewsdl = (String) (getBbbWebServicesConfig()
					.getServiceToWsdlMap()).get(service);

			String endpoint = BBBWebServiceConstants.EMPTY;
			if((getCatalogTools().getAllValuesForKey(
					servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT+BBBWebServiceConstants.TXT_UNDERSCORE+service))!=null) {
				if(getCatalogTools().getAllValuesForKey(servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT+BBBWebServiceConstants.TXT_UNDERSCORE+service).size()>BBBWebServiceConstants.ZERO) {
				endpoint = getCatalogTools().getAllValuesForKey(
						servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT+BBBWebServiceConstants.TXT_UNDERSCORE+service)
						.get(BBBWebServiceConstants.ZERO);
				} else {
					logError("ServiceHandler | ServiceResponseIF |"+BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT+BBBWebServiceConstants.TXT_UNDERSCORE+service+"  - value not found");
				}

			} else {
				BBBPerformanceMonitor.end("ServiceHandler-invoke:"+service);
				final BBBSystemException sysExc = new BBBSystemException("ServiceHandler | ServiceResponseIF | value not found for "+BBBWebServiceConstants.TXT_WSDLKEY_WSENDPOINT+BBBWebServiceConstants.TXT_UNDERSCORE+service);
				throw sysExc;

			}

			serviceStub = getServiceFactory().getServiceStub(service, endpoint);

			logDebug(service + " end point : " + endpoint);

			// call fetchAPIMethod to find the web service method on the Stub
			final Method stubMethod = fetchAPIMethod(service, serviceStub);

			Object[] params = null;
			// query number of input arguments in the web service method
			final int numberOfParams = stubMethod.getParameterTypes().length;
			if (numberOfParams == 2) {
				// most API calls use 2 input params
				params = new Object[] { requestDocument, header };
			} else if (numberOfParams == 3) {
				params = new Object[] { requestDocument, null, header };
			} else if (numberOfParams == 1) {
				params = new Object[] { requestDocument };
			} else {
				BBBPerformanceMonitor.end("ServiceHandler-invoke:"+service);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1000,"No such method");
			}

			int timeout = BBBWebServiceConstants.ZERO;
			if((getCatalogTools().getAllValuesForKey(servicewsdl,BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT))!=null) {
				if((getCatalogTools().getAllValuesForKey(servicewsdl,BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT).size())>BBBWebServiceConstants.ZERO) {
					timeout = Integer.parseInt((getCatalogTools()
					.getAllValuesForKey(servicewsdl,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT)
					.get(BBBWebServiceConstants.ZERO)));
				} else {
					logError("ServiceHandler | invoke |"+BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT+"  - value not found");
				}
			} else {

				final BBBSystemException sysExc = new BBBSystemException("ServiceHandler | invoke | value not found for "+BBBWebServiceConstants.TXT_WSDLKEY_WSTIMEOUT);
				throw sysExc;
			}

			serviceStub._getServiceClient().getOptions()
					.setTimeOutInMilliSeconds(timeout);
			if(BBBWebServiceConstants.SET_EXPRESS_CHECKOUT_SERVICE.equalsIgnoreCase(service) || BBBWebServiceConstants.GET_EXPRESS_CHECKOUT_SERVICE.equalsIgnoreCase(service)
					|| BBBWebServiceConstants.DO_EXPRESS_CHECKOUT_SERVICE.equalsIgnoreCase(service) || BBBWebServiceConstants.DO_AUTHORIZATION.equalsIgnoreCase(service)){
				serviceStub._getServiceClient().getOptions()
				.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
			}

			if(BBBWebServiceConstants.SET_EXPRESS_CHECKOUT_SERVICE.equalsIgnoreCase(service) || BBBWebServiceConstants.GET_EXPRESS_CHECKOUT_SERVICE.equalsIgnoreCase(service)
					|| BBBWebServiceConstants.DO_EXPRESS_CHECKOUT_SERVICE.equalsIgnoreCase(service) || BBBWebServiceConstants.DO_AUTHORIZATION.equalsIgnoreCase(service)){
				serviceStub._getServiceClient().getOptions()
				.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
			}

			// Setting Authentication Options for client. User name and password
			// are taken from BBBWebServicesConfig.
			List<String> authScheme = new ArrayList<String>();
			authScheme.add(Authenticator.BASIC);
			Authenticator authenticator = new Authenticator();
			authenticator.setAuthSchemes(authScheme);

			// authenticator.setUsername(getBbbWebServicesConfig().getUserName());
			// authenticator.setPassword(getBbbWebServicesConfig().getPassword());
			if((getCatalogTools().getAllValuesForKey(
					servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_LOGIN+BBBWebServiceConstants.TXT_UNDERSCORE+service))!=null){
				if(getCatalogTools().getAllValuesForKey(
						servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_LOGIN+BBBWebServiceConstants.TXT_UNDERSCORE+service).size()>BBBWebServiceConstants.ZERO) {
					authenticator.setUsername(getCatalogTools().getAllValuesForKey(
							servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_LOGIN+BBBWebServiceConstants.TXT_UNDERSCORE+service).get(
									BBBWebServiceConstants.ZERO));
				} else {
					authenticator.setUsername("");
					logError("ServiceHandler | ServiceResponseIF |"+BBBWebServiceConstants.TXT_WSDLKEY_LOGIN+BBBWebServiceConstants.TXT_UNDERSCORE+service+"  - value not found");
				}

			} else {
				//changed for the env where we are not using authorization
				authenticator.setUsername("");

				//BBBSystemException sysExc = new BBBSystemException("ServiceHandler | ServiceResponseIF | value not found for "+BBBWebServiceConstants.TXT_WSDLKEY_LOGIN+BBBWebServiceConstants.TXT_UNDERSCORE+service);
				//throw sysExc;
			}
			if((getCatalogTools().getAllValuesForKey(
					servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PASSWORD+BBBWebServiceConstants.TXT_UNDERSCORE+service))!=null) {

				if(getCatalogTools().getAllValuesForKey(
						servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PASSWORD+BBBWebServiceConstants.TXT_UNDERSCORE+service).size()>BBBWebServiceConstants.ZERO){
					authenticator.setPassword(getCatalogTools().getAllValuesForKey(
						servicewsdl, BBBWebServiceConstants.TXT_WSDLKEY_PASSWORD+BBBWebServiceConstants.TXT_UNDERSCORE+service)
						.get(BBBWebServiceConstants.ZERO));
				} else {
					authenticator.setPassword("");
					logError("ServiceHandler | ServiceResponseIF |"+BBBWebServiceConstants.TXT_WSDLKEY_PASSWORD+BBBWebServiceConstants.TXT_UNDERSCORE+service+"  - value not found");
				}
			} else {
				//changed for the env where we are not using authorization
				authenticator.setPassword("");


				//BBBSystemException sysExc = new BBBSystemException("ServiceHandler | ServiceResponseIF | value not found for "+BBBWebServiceConstants.TXT_WSDLKEY_PASSWORD+BBBWebServiceConstants.TXT_UNDERSCORE+service);
				//throw sysExc;
			}


			authenticator.setAllowedRetry(true);
			authenticator.setPreemptiveAuthentication(true);
			serviceStub._getServiceClient().getOptions()
					.setProperty(HTTPConstants.AUTHENTICATE, authenticator);


			/*
			 * if (loggerUtil.isLoggingInfo()) {
			 * loggerUtil.logInfo(this.getClass().getName(), service +
			 * " timeout is set to " + timeout); }
			 */

			logDebug(service + " end point : " + endpoint);

			// ***
			// *** call the API Stub method, ie calling the web service\
			// ***

			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			long startTime = BBBWebServiceConstants.ZERO, timeTaken = BBBWebServiceConstants.ZERO;

			try {
				// calling all the 'before' advices before invoking the web
				// service
				if (this.serviceAdvices != null) {
					for (ServiceHandlerAdviceIF advice : this.serviceAdvices) {
						advice.beforeInvoke(service, endpoint, requestDocument,
								null);
					}
				}
				// invoke the web service
				/*if(isLoggingDebug()){
					logDebug("Webservice request for service name" + service + " "+
						requestDocument);
				}*/
				startTime = System.currentTimeMillis();
				responseDocument = (XmlObject) stubMethod.invoke(serviceStub,
						params);
				if (responseDocument == null) {
					BBBPerformanceMonitor.end("ServiceHandler-invoke:"+service);
					throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1001,
							"Response from web service is NULL");
				}
				logDebug("==============================Response========================================");
				if(isLoggingDebug()){
					logDebug(responseDocument.toString());
				}
				logDebug("=========================================================================");

				timeTaken = System.currentTimeMillis() - startTime;


				logDebug("Total Time to run ServiceHandler.invoke() method is:" + timeTaken +
							" msec for Webservice request for service name:" + service );

				/*if(isLoggingDebug()){
					logDebug("Response from webservice="+ responseDocument);
				}*/
				//timeTaken = gregorianCalendar.getTimeInMillis() - startTime;

				if (this.serviceAdvices != null) {
					for (ServiceHandlerAdviceIF advice : this.serviceAdvices) {
						advice.afterInvoke(service, endpoint, requestDocument,
								responseDocument, Long.toString(timeTaken),
								"SUCCESS");
					}
				}
			}catch(InvocationTargetException e){
				logError(e.getMessage() + " InvocationTargetException Request Document: " + requestDocument + " Response Document: " + responseDocument);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1002,
						"Exception in calling web service" + service, e);
			} catch(SOAPProcessingException e){
				logError(e.getMessage() + " SOAPProcessingException Request Document: " + requestDocument + " Response Document: " + responseDocument);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1002,
						"Exception in calling web service" + service, e);
			} catch(OMException e){
				logError(e.getMessage() + " OMException Request Document: " + requestDocument + " OMException Response Document: " + responseDocument);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1002,
						"Exception in calling web service" + service, e);
			} catch (Exception e) {
				logError(e.getMessage() + "\nRequest Document ["+requestDocument+"] \nResponse Document ["+responseDocument+"]", e);

				timeTaken = gregorianCalendar.getTimeInMillis() - startTime;

				// calling all the 'cancel' advices after an exception in
				// invoking the web service

				if (this.serviceAdvices != null) {
					for (ServiceHandlerAdviceIF advice : this.serviceAdvices) {
						advice.cancelInvoke(service, endpoint, requestDocument,
								null, Long.toString(timeTaken), "FAILIURE");
					}
				}

				/*
				 * if (loggerUtil.isLoggingError()) {
				 * loggerUtil.logError(this.getClass().getName(), e.getCause());
				 * }
				 */

				BBBPerformanceMonitor.end("ServiceHandler-invoke:"+service);
				String errorCode = (String) (getBbbWebServicesConfig().getServiceToErrorCodeMap()).get(service);
				BBBUtility.passErrToPage(errorCode, "Exception in calling web service");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1002,
						"Exception in calling web service" + service, e);

			}
			voResp = unmarshaller.processResponse(responseDocument);
			if(voResp != null){
				ServiceResponseBase resVO = (ServiceResponseBase)voResp;
				if(resVO != null && resVO.isWebServiceError())
				{
					logError("Request Document for service- " + service +  " : " + requestDocument);
					logError("Response Documentfor service- " + service +  " : " + responseDocument);
				}
				else{
					logDebug("No Error from WebService for service: " + service);
				}
			}
			else{
				logError("Response is null from unmarshaller " + unmarshaller);
			}
		} catch (java.lang.IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("ServiceHandler-invoke:" + service);
			String errorCode = (String) (getBbbWebServicesConfig().getServiceToErrorCodeMap()).get(service);
			BBBUtility.passErrToPage(errorCode, "IllegalArgumentException in calling web service");
			throw new BBBBusinessException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1003,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor.end("ServiceHandler-invoke:" + service);
		}
		/*
		 * if (loggerUtil.isLoggingDebug()) {
		 * loggerUtil.logMethodExit(this.getClass().getName(),
		 * "Create request document, Invoke " + service +
		 * " Webservice, and parse response"); }
		 */

		logDebug("ServiceHandler::invoke method end");


		return voResp;
	}

}