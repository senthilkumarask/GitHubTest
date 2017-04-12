/*
 *
 * File  : ServiceRequestIF.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.integration;

import java.io.Serializable;

/**
 * The Interface ServiceRequestIF.
 * 
 * @version 1.0
 */
public interface ServiceRequestIF extends Serializable {
	
	/**
	 * Implement me to give the web service name that you are implementing.
	 * 
	 * @return service name String
	 */
	public String getServiceName();
	
	/**
	 * Say yes if you want your response to be cached otherwise say no.
	 * 
	 * @return boolean
	 */
	public Boolean isCacheEnabled();
}
