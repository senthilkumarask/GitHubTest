/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceRequestBase;


// TODO: Auto-generated Javadoc
/**
 * This class provides input for the forget password web service.
 *
 * @author sku134
 */
public class ForgetRegPassRequestVO  extends ServiceRequestBase {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The forget pass registry id. */
	private String forgetPassRegistryId;
	
	/** The registry id ws. */
	private long registryIdWS;
	
	/** The forget pass registry email. */
	private String forgetPassRegistryEmail;
	
	/** The site flag. */
	private String siteFlag;
	
	/** The user token. */
	private String userToken;
	
	/** The service name. */
	private String serviceName;
	
	/**
	 * Gets the forget pass registry id.
	 *
	 * @return the forgetPassRegistryId
	 */
	public String getForgetPassRegistryId() {
		return forgetPassRegistryId;
	}
	
	/**
	 * Sets the forget pass registry id.
	 *
	 * @param pForgetPassRegistryId the forgetPassRegistryId to set
	 */
	public void setForgetPassRegistryId(String pForgetPassRegistryId) {
		forgetPassRegistryId = pForgetPassRegistryId;
	}
	
	/**
	 * Gets the forget pass registry email.
	 *
	 * @return the forgetPassRegistryEmail
	 */
	public String getForgetPassRegistryEmail() {
		return forgetPassRegistryEmail;
	}
	
	/**
	 * Sets the forget pass registry email.
	 *
	 * @param pForgetPassRegistryEmail the forgetPassRegistryEmail to set
	 */
	public void setForgetPassRegistryEmail(String pForgetPassRegistryEmail) {
		forgetPassRegistryEmail = pForgetPassRegistryEmail;
	}
	
	/**
	 * Gets the site flag.
	 *
	 * @return the siteFlag
	 */
	public String getSiteFlag() {
		return siteFlag;
	}
	
	/**
	 * Sets the site flag.
	 *
	 * @param pSiteFlag the siteFlag to set
	 */
	public void setSiteFlag(String pSiteFlag) {
		siteFlag = pSiteFlag;
	}
	
	/**
	 * Gets the user token.
	 *
	 * @return the userToken
	 */
	public String getUserToken() {
		return userToken;
	}
	
	/**
	 * Sets the user token.
	 *
	 * @param pUserToken the userToken to set
	 */
	public void setUserToken(String pUserToken) {
		userToken = pUserToken;
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
	 * @param pServiceName the serviceName to set
	 */
	public void setServiceName(String pServiceName) {
		serviceName = pServiceName;
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
	
		
}
