/*
 *
 * File  : ServiceRequestBase.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.integration;


/**
 * The Class ServiceResponseBase.
 * 
 * @version 1.0
 */
public class ServiceRequestBase implements ServiceRequestIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2624973425721414534L;

	/* (non-Javadoc)
	 * @see com.bbb.framework.integration.ServiceRequestIF#getServiceName()
	 */
	public String getServiceName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bbb.framework.integration.ServiceRequestIF#isCacheEnabled()
	 */
	public Boolean isCacheEnabled() {
		return false;
	}
}
