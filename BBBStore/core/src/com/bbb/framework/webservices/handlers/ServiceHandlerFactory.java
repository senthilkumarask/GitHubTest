/*
 *
 * File  : ServiceHandlerFactory.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.webservices.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.axis2.client.Stub;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.handlers.ServiceHandlerFactoryIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.framework.webservices.ResponseUnMarshaller;

/**
 * A factory for creating ServiceHandler objects.
 * 
 * 
 * @version 1.0
 */
public class ServiceHandlerFactory extends BBBGenericService implements ServiceHandlerFactoryIF {
	
	private HttpConnectionManager mConnectionManager;
	
	private BBBCatalogTools mCatalogTools;
	
    /** These needs to be defined in ATG nucleus property file serviceMarshallerMap contains a list of API Marshaller object for each web service. */
    private Map<String, String> serviceMarshallerMap;
    
    /** The service un marshaller map. */
    private Map<String, String> serviceUnMarshallerMap;
    
    /** The service endpoint map. */
    private Map<String, String> serviceEndpointMap;
    
    /** Hashmaps to cache marshaller and unmarshaller object instances. */
    private Map<String, RequestMarshaller> marshallerCache;
    
    /** The unmarshaller cache. */
    private Map<String, ResponseUnMarshaller> unmarshallerCache;
    
    private static final String CONNECTION_TIME_OUT = "CONNECTION_TIME_OUT";
	private static final String MAX_TOTAL_CONNECTIONS = "MAX_TOTAL_CONNECTIONS";
	private static final String MAX_TOTAL_CONN_PER_HOST = "MAX_TOTAL_CONN_PER_HOST";
	private static final String CONNECTION_SO_TIME_OUT = "CONNECTION_SO_TIME_OUT";
	private static final String HTTP_CONNECTION_ATTRIBS = "HTTPConnectionAttributes";
	
    /** The service stub cache. */
    private Map<String, HttpClient> httpClientCache;
    private Map<String, Class<?>> serviceStubClassCache;
    
    {
    	marshallerCache = new ConcurrentHashMap<String, RequestMarshaller>();
    	unmarshallerCache = new ConcurrentHashMap<String, ResponseUnMarshaller>();
    	httpClientCache = new ConcurrentHashMap<String, HttpClient>();
    	serviceStubClassCache = new ConcurrentHashMap<String, Class<?>>();
    }
    
	/**
	 * Instantiates a new service handler factory.
	 */
	public ServiceHandlerFactory() {
		//default constructor
    }
    
	
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
    /* (non-Javadoc)
     * @see com.bbb.integration.api.servicehandler.ServiceHandlerFactoryIF#getServiceMarshaller(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE)
     */
    public RequestMarshaller getServiceMarshaller(String service) throws BBBSystemException {

		BBBPerformanceMonitor
		.start("ServiceHandlerFactory-getServiceMarshaller");
	

		RequestMarshaller marshaller = null;
    	if (marshallerCache.get(service) != null) {
    		marshaller = marshallerCache.get(service);
    	} else {
    		Class<?> marshallerClass;
			try {
				marshallerClass = Class.forName(getServiceMarshallerMap().get(service));
			} catch (ClassNotFoundException e) {
				BBBPerformanceMonitor
				.end("ServiceHandlerFactory-getServiceMarshaller");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			}
    		try {
				marshaller = (RequestMarshaller) marshallerClass.newInstance();
			} catch (InstantiationException e) {
				BBBPerformanceMonitor
				.end("ServiceHandlerFactory-getServiceMarshaller");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			} catch (IllegalAccessException e) {
				BBBPerformanceMonitor
				.end("ServiceHandlerFactory-getServiceMarshaller");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			}
    		marshallerCache.put(service, marshaller);
    	}

		BBBPerformanceMonitor
		.end("ServiceHandlerFactory-getServiceMarshaller");
	

		return marshaller;
    }

    /* (non-Javadoc)
     * @see com.bbb.integration.api.servicehandler.ServiceHandlerFactoryIF#getServiceUnMarshaller(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE)
     */
    public ResponseUnMarshaller getServiceUnMarshaller(String service) throws BBBSystemException {

		BBBPerformanceMonitor
		.start("ServiceHandlerFactory-getServiceUnMarshaller");
	
		ResponseUnMarshaller unmarshaller = null;
    	if (unmarshallerCache.get(service) instanceof ResponseUnMarshaller) {
        	unmarshaller = unmarshallerCache.get(service);
    	} else {
    		Class<?> unmarshallerClass;
			try {
				unmarshallerClass = Class.forName(getServiceUnMarshallerMap().get(service));
			} catch (ClassNotFoundException e) {
				BBBPerformanceMonitor
				.end("ServiceHandlerFactory-getServiceUnMarshaller");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			}
    		try {
				unmarshaller = (ResponseUnMarshaller) unmarshallerClass.newInstance();
			} catch (InstantiationException e) {
				BBBPerformanceMonitor
				.end("ServiceHandlerFactory-getServiceUnMarshaller");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			} catch (IllegalAccessException e) {
				BBBPerformanceMonitor
				.end("ServiceHandlerFactory-getServiceUnMarshaller");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			}
    		unmarshallerCache.put(service, unmarshaller);
    	}

		BBBPerformanceMonitor
		.end("ServiceHandlerFactory-getServiceUnMarshaller");
	
    	return unmarshaller;
    }

    /* (non-Javadoc)
     * @see com.bbb.integration.api.servicehandler.ServiceHandlerFactoryIF#getServiceStub(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE)
     */
    public org.apache.axis2.client.Stub getServiceStub(String service, String endpoint) throws BBBSystemException, BBBBusinessException {

		BBBPerformanceMonitor
		.start("ServiceHandlerFactory-getServiceStub");
	
    	org.apache.axis2.client.Stub serviceStub = null;
    	Stub serviceObj = null;
    	HttpClient httpClient = null;
		
    	Class<?> serviceStubClass;			
		
		if (serviceStubClassCache.get(endpoint) != null ) {
			serviceStubClass = serviceStubClassCache.get(endpoint);
    	} else {
    		   			
			try {
				//serviceStubClass = Class.forName(getServiceStubMap().get(service));
				if((getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSTUBCLASSES,service))!= null) {
					if(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSTUBCLASSES,service).size()>BBBWebServiceConstants.ZERO) {
						serviceStubClass = Class.forName(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSTUBCLASSES,service).get(BBBWebServiceConstants.ZERO));
					} else {
						BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
						BBBSystemException sysExc = new BBBSystemException("ServiceHandlerFactory | value not found for serviceStubClass");
						throw sysExc;
					}
				} else {
					BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
					BBBSystemException sysExc = new BBBSystemException("ServiceHandlerFactory | value found null for serviceStubClass");
					throw sysExc;
				}
			} catch (ClassNotFoundException e) {
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			}  catch (BBBBusinessException e) {
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				BBBSystemException sysExc = new BBBSystemException(e.getMessage(), e);
				throw sysExc;
			}
    		
    		if(serviceStubClass != null){
    			serviceStubClassCache.put(endpoint, serviceStubClass);
    		}
    	}	
    		try {
				
			if(serviceStubClass == null){
				return serviceObj;
			}
			
				serviceStub = (org.apache.axis2.client.Stub) serviceStubClass.newInstance();
				Constructor<Stub> serviceConstructor = (Constructor<Stub>) serviceStub.getClass().getConstructor(new Class[] { String.class });
					serviceObj = (Stub) serviceConstructor.newInstance(endpoint);
					serviceObj._getServiceClient().getOptions().setProperty(HTTPConstants.REUSE_HTTP_CLIENT, BBBWebServiceConstants.TRUE);
			if (httpClientCache.get(endpoint) instanceof HttpClient ) {
               httpClient = httpClientCache.get(endpoint);
		    } else {
               httpClient = new HttpClient(getHTTPConnectionManager());
               httpClientCache.put(endpoint, httpClient);
				}
			serviceObj._getServiceClient().getOptions().setProperty(HTTPConstants.CACHED_HTTP_CLIENT, httpClient);				
			}catch (InvocationTargetException e) {
				logError(e.getMessage(), e);
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1004,e.getMessage(), e);
			}catch (IllegalArgumentException e) {
				logError(e.getMessage(), e);
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1005,e.getMessage(), e);
			}catch (InstantiationException e) {
				logError(e.getMessage(), e);
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1006,e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logError(e.getMessage(), e);
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1007,e.getMessage(), e);
			} catch (SecurityException e) {
				logError(e.getMessage(), e);
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1008,e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				logError(e.getMessage(), e);
				BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1009,e.getMessage(), e);
			}
    		
    		
		BBBPerformanceMonitor.end("ServiceHandlerFactory-getServiceStub");
	
    	return serviceObj;
    }
    
    /**
     * get HttpConnection Manager
     * @return
     * @throws BBBBusinessException 
     * @throws BBBSystemException 
     * @throws NumberFormatException 
     */
    private HttpConnectionManager getHTTPConnectionManager() throws BBBSystemException, BBBBusinessException{
    	
    	if(mConnectionManager == null){
    		logDebug("ServiceHandlerFactory.getHTTPConnectionManager : Initializing Connection Manager Start");
    		
    		try {
	    		final Integer connectionTimeout = Integer.valueOf(getCatalogTools().
	    				getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, CONNECTION_TIME_OUT).get(0));
	    		final int maxTotalConnections = Integer.parseInt(getCatalogTools().
	    				getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, MAX_TOTAL_CONNECTIONS).get(0));
	    		final int maxHostConnectionsPerHost = Integer.parseInt(getCatalogTools().
	    				getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, MAX_TOTAL_CONN_PER_HOST).get(0));
	    		final Integer connectionSOTimeout = Integer.valueOf(getCatalogTools().
	    				getAllValuesForKey(HTTP_CONNECTION_ATTRIBS, CONNECTION_SO_TIME_OUT).get(0));
	    		
	    		mConnectionManager = new MultiThreadedHttpConnectionManager();
	    		mConnectionManager.getParams().setDefaultMaxConnectionsPerHost(maxHostConnectionsPerHost);
	    		mConnectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
	    		mConnectionManager.getParams().setConnectionTimeout(connectionTimeout);
	    		mConnectionManager.getParams().setSoTimeout(connectionSOTimeout);
	    		
	    		logDebug("connectionTimeout="+connectionTimeout
	                		+"connectionSOTimeout="+connectionSOTimeout
	                		+"maxTotalConnections="+maxTotalConnections
	                		+"maxHostConnectionsPerHost="+maxHostConnectionsPerHost);
	    		
    		}catch(NumberFormatException e){
    			logError(e.getMessage(), e);
				throw new BBBSystemException(BBBCoreErrorConstants.WB_SERVICE_ERROR_1010,e.getMessage(), e);
    		}
    		
    		logDebug("ServiceHandlerFactory.getHTTPConnectionManager : Initializing Connection Manager End");
    		
    	}
    	
		return mConnectionManager;
		
    }
    /* (non-Javadoc)
     * @see com.bbb.integration.api.servicehandler.ServiceHandlerFactoryIF#getEndpointConstant(com.bbb.integration.api.servicehandler.ServiceHandlerConstants.SERVICE)
     */
    public String getEndpointConstant(String service) throws BBBBusinessException, BBBSystemException {
    	if (serviceEndpointMap != null) {
    		return serviceEndpointMap.get(service);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Sets the service marshaller map.
     * 
     * @param serviceMarshallerMap the service marshaller map
     */
    public void setServiceMarshallerMap(final Map<String, String> serviceMarshallerMap) {
        this.serviceMarshallerMap = serviceMarshallerMap;
    }
    
    /**
     * Gets the service marshaller map.
     * 
     * @return the service marshaller map
     */
    public Map<String, String> getServiceMarshallerMap() {
        return serviceMarshallerMap;
    }
    
    /**
     * Sets the service un marshaller map.
     * 
     * @param serviceUnMarshallerMap the service un marshaller map
     */
    public void setServiceUnMarshallerMap(final Map<String, String> serviceUnMarshallerMap) {
        this.serviceUnMarshallerMap = serviceUnMarshallerMap;
    }
    
    /**
     * Gets the service un marshaller map.
     * 
     * @return the service un marshaller map
     */
    public Map<String, String> getServiceUnMarshallerMap() {
        return serviceUnMarshallerMap;
    }
    
   /**
	 * Sets the service endpoint map.
	 * 
	 * @param serviceEndpointMap the service endpoint map
	 */
	public void setServiceEndpointMap(Map<String, String> serviceEndpointMap) {
		this.serviceEndpointMap = serviceEndpointMap;
	}

	/**
	 * Gets the service endpoint map.
	 * 
	 * @return the service endpoint map
	 */
	public Map<String, String> getServiceEndpointMap() {
		return serviceEndpointMap;
	}


	/**
	 * Sets the unmarshaller cache.
	 * 
	 * @param unmarshallerCache the unmarshaller cache
	 */
	protected void setUnmarshallerCache(Map<String, ResponseUnMarshaller> unmarshallerCache) {
		this.unmarshallerCache = unmarshallerCache;
	}

    /**
     * Gets the unmarshaller cache.
     * 
     * @return the unmarshaller cache
     */
    protected Map<String, ResponseUnMarshaller> getUnmarshallerCache() {
		return unmarshallerCache;
	}

    /**
     * Sets the marshaller cache.
     * 
     * @param marshallerCache the marshaller cache
     */
    protected void setMarshallerCache(Map<String, RequestMarshaller> marshallerCache) {
		this.marshallerCache = marshallerCache;
	}

    /**
     * Gets the marshaller cache.
     * 
     * @return the marshaller cache
     */
    protected Map<String, RequestMarshaller> getMarshallerCache() {
		return marshallerCache;
	}

}
