package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceRequestBase;



/**
 * This class holding Registry Status VO information.
 *
 * @author prbhoomu
 */

public class RegistryStatusVO extends ServiceRequestBase{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The registry id. */
	private String registryId;
	
	/** The registry id ws. */
	private long registryIdWS;
	
	/** The user token. */
	private String userToken;
	
	/** The Profile id. */
	private String mProfileId;
	
	/** The site id. */
	private String siteId;

	/** The service name. */
	private String serviceName;
	
	/** Registry status code  */
	private String statusCode;
	
	/** Registry status description  */
	private String statusDesc;
	
     
	/**
	 * Gets the registry id.
	 *
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}

	/**
	 * Sets the registry id.
	 *
	 * @param pRegistryId the registryId to set
	 */
	public void setRegistryId(String pRegistryId) {
		registryId = pRegistryId;
	}

	/**
	 * Gets the user token.
	 *
	 * @return the userTokenToken
	 */
	public String getUserToken() {
		return userToken;
	}

	/**
	 * Sets the user token.
	 *
	 * @param pUserToken the new user token
	 */
	public void setUserToken(String pUserToken) {
		userToken = pUserToken;
	}

	/**
	 * Gets the profile id.
	 *
	 * @return the mProfileId
	 */
	public String getProfileId() {
		return mProfileId;
	}

	/**
	 * Sets the profile id.
	 *
	 * @param pProfileId the new profile id
	 */
	public void setProfileId(String pProfileId) {
		this.mProfileId = pProfileId;
	}
	
	/**
	 * Gets the site id.
	 *
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 * Sets the site id.
	 *
	 * @param pSiteId the siteId to set
	 */
	public void setSiteId(String pSiteId) {
		siteId = pSiteId;
	}
	
	/**
	 * Gets the service name.
	 *
	 * @return the serviceName
	 */
	/* (non-Javadoc)
	 * @see com.bbb.framework.integration.ServiceRequestBase#getServiceName()
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name.
	 *
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * Very important to implement this method and let the framework know the
	 * whether the web service response needs to be cached.
	 *
	 * @return the boolean
	 */
	@Override
	public Boolean isCacheEnabled() {
		return false;
	}

	/**
	 * Gets the registry id ws.
	 *
	 * @return the registry id ws
	 */
	public long getRegistryIdWS() {
		return registryIdWS;
	}

	/**
	 * Sets the registry id ws.
	 *
	 * @param registryIdWS the new registry id ws
	 */
	public void setRegistryIdWS(long registryIdWS) {
		this.registryIdWS = registryIdWS;
	}

	/**
	 * Gets the registry status code.
	 * @return statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the registry status code.
	 *
	 * @param statusCode
	 */	
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the registry status desc.
	 * @return statusDesc
	 */	
	public String getStatusDesc() {
		return statusDesc;
	}


	/**
	 * Sets the registry status desc.
	 *
	 * @param statusDesc
	 */		
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
}
