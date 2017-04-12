/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceRequestBase;



// TODO: Auto-generated Javadoc
/**
 * This class holding Registry request VO information.
 *
 * @author ssha53
 */
public class RegistryReqVO extends ServiceRequestBase{

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
	
	/** The password. */
	private String password;
	
	/** The site id. */
	private String siteId;

	/** The service name. */
	private String serviceName;
     
	/** The email id. */
	private String emailId;

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
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
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
	 * Gets the email id.
	 *
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * Sets the email id.
	 *
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	}
