package com.bbb.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.rest.RestException;
import atg.rest.servlet.RestPipelineServlet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 *  Description :- A Pipeline Component that handles REST Related Service 
 *  for Cache-Control .   
 *  The CacheControl will be only set in the Response for the services directly 
 *  invoked through MOB App.
 */

public class BBBRestResponsePropertiesConfigurationServlet extends RestPipelineServlet {
	
    private Map<String, String> requestUriCacheConfigMap = new HashMap<String, String>();
	
	private List<String> filteredChannelList = null;
	
	public BBBRestResponsePropertiesConfigurationServlet(){
		//default constructor
	}

	/**
	 * @return the requestUriCacheConfigMap
	 */
	public Map<String, String> getRequestUriCacheConfigMap() {
		return requestUriCacheConfigMap;
	}


	/**
	 * @param requestUriCacheConfigMap the requestUriCacheConfigMap to set
	 */
	public void setRequestUriCacheConfigMap(
			Map<String, String> requestUriCacheConfigMap) {
		this.requestUriCacheConfigMap = requestUriCacheConfigMap;
	}

	/**
	 * @return the filteredChannelList
	 */
	public final List<String> getFilteredChannelList() {
		return filteredChannelList;
	}

	/**
	 * @param filteredChannelList the filteredChannelList to set
	 */
	public final void setFilteredChannelList(List<String> filteredChannelList) {
		this.filteredChannelList = filteredChannelList;
	}

	/* Method to set the CacheControl parameters to the response.
	 * The CacheControl will be only set for the services directly invoked through MOB App.
	 * For Filtered Channel list the properties will not be set.
	 */
	public void serviceRESTRequest(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws IOException, ServletException, RestException{
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CACHE_CONTROL, 
				BBBCoreConstants.BBBRESTRESPONSEPROPERTIESCONFIGURATIONSERVLET_SERVICE);
		
		if(isLoggingDebug()) {
			logDebug("BBBRestResponsePropertiesConfigurationServlet serviceRESTRequest Method : Method Start : Channel :- " 
		              +pRequest.getHeader(BBBCoreConstants.CHANNEL));
		}
		final String uri = pRequest.getRequestURI();
		
		if(!getFilteredChannelList().contains(pRequest.getHeader(BBBCoreConstants.CHANNEL)) && null != getRequestUriCacheConfigMap().get(uri)) {
			
			    pResponse.setHeader(BBBCoreConstants.PROPERTY_CACHE_CONTROL, getRequestUriCacheConfigMap().get(uri));
				if(isLoggingDebug()) {
					logDebug("BBBRestResponsePropertiesConfigurationServlet serviceRESTRequest Method Cache-Control for Channel : "
				              +pRequest.getHeader(BBBCoreConstants.CHANNEL)	+ " -- Cache-Control : " +getRequestUriCacheConfigMap().get(uri));
			}
		}
		
		if(isLoggingDebug()) {
			logDebug("BBBRestResponsePropertiesConfigurationServlet serviceRESTRequest Method :- Method End");
		}
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CACHE_CONTROL, 
				BBBCoreConstants.BBBRESTRESPONSEPROPERTIESCONFIGURATIONSERVLET_SERVICE);
		
	}

}
