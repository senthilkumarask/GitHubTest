/*
 *
 * File  : BBBJMSInitialContextFactory.java
 * Project:     BBB
 * 
 */

package com.bbb.framework.messaging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import com.bbb.common.BBBGenericService;

/**
 * Initializes InitialContext with the details of the test destination to send
 * messages. Used only for Sapeunit testing.
 * 
 * @author manohar
 * @version 1.0
 */
public class BBBJMSInitialContextFactory extends BBBGenericService implements InitialContextFactory  {
	
	/**
	 * 
	 */
	private String cfUserName;
	/**
	 * 
	 */
	private String cfPassword;
	/**
	 * 
	 */
    private String initialContextFactory;
    
    /**
     * 
     */
    private String providerURL;
    
    private BBBConfigTools catalogTools;
    
    /**
	 * queueType
	 */
	private String queueType;
	
    /**
     * 
     */
    private String factoryUrlPkgs;
    
    /**
     * 
     */
    private Properties extraProperties;
    
    /**
     * 
     */
    private InitialContext mJmsNamingContext = null;
    
    /**
     * 
     * @return extraProperties
     */
	public Properties getExtraProperties() {
		return extraProperties;
	}

	/**
	 * 
	 * @param extraProperties
	 */
	public void setExtraProperties(Properties extraProperties) {
		this.extraProperties = extraProperties;
	}

	/**
	 * 
	 * @return initialContextFactory
	 */
	public String getInitialContextFactory() {
		List<String> contextList = new ArrayList<String>();
		try {
			contextList = getCatalogTools().getAllValuesForKey(getQueueType(), "initialContextFactory");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception occurred while fetching initial Context Factory", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching initial Context Factory", bbbsEx);
			
		}
		if(!contextList.isEmpty()){
			initialContextFactory = contextList.get(0);
		}
		return initialContextFactory;
	}
	
	/**
	 * 
	 * @param initialContextFactory
	 */
	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}
	
	/**
	 * 
	 * @return providerURL
	 */
	public String getProviderURL() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "providerURL");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception occurred while fetching Provider URL list", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching Provider URL list", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			providerURL = jmsList.get(0);
		}
		return providerURL;
	}
	
	/**
	 * 
	 * @param providerURL
	 */
	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}
	
	/**
	 * 
	 * @return factoryUrlPkgs
	 */
	public String getFactoryUrlPkgs() {
		List<String> factoryUrlPkgsList = new ArrayList<String>();
		try {
			factoryUrlPkgsList = getCatalogTools().getAllValuesForKey(getQueueType(), "factoryUrlPkgs");
		} catch (BBBBusinessException bbbbEx) {
			logError("Business Exception occurred while fetching factory Url Package list", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching factory Url Package list", bbbsEx);
			
		}
		if(!factoryUrlPkgsList.isEmpty()){
			factoryUrlPkgs = factoryUrlPkgsList.get(0);
		}
		return factoryUrlPkgs;
	}
	
	/**
	 * 
	 * @param factoryUrlPkgs
	 */
	public void setFactoryUrlPkgs(String factoryUrlPkgs) {
		this.factoryUrlPkgs = factoryUrlPkgs;
	}
	
	/**
	 * @return jmsNamingContext
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public InitialContext getInitialContext(Hashtable hTable) throws NamingException {
		if (hTable == null) {
			hTable = new Hashtable();
		}

		if (getInitialContextFactory() != null) {
			hTable.put("java.naming.factory.initial", getInitialContextFactory());
		}
		if(getProviderURL() != null) {
			hTable.put("java.naming.provider.url", getProviderURL());
		}
    	if(getFactoryUrlPkgs() != null) {
    		hTable.put("java.naming.factory.url.pkgs", getFactoryUrlPkgs());
    	}
    	if(getCfUserName() != null && getCfPassword()!=null) {
    		hTable.put(Context.SECURITY_PRINCIPAL, getCfUserName());
    		hTable.put(Context.SECURITY_CREDENTIALS, getCfPassword());
    	}
    	
    	if(getExtraProperties() != null) {
            Enumeration extraPropertyNames = getExtraProperties().propertyNames();
	        String propertyKey;
	        for( ;extraPropertyNames != null && extraPropertyNames.hasMoreElements(); ) {
	            propertyKey = (String)extraPropertyNames.nextElement();
	            hTable.put(propertyKey, getExtraProperties().getProperty(propertyKey));
	        }
    	}
		InitialContext jmsNamingContext = new InitialContext(hTable);
    	return jmsNamingContext;
	}
	
	/**
	 * 
	 * @return mJmsNamingContext
	 * @throws NamingException
	 */
	@SuppressWarnings("rawtypes")
	public InitialContext getInitialContext() throws NamingException {
		if (mJmsNamingContext == null) {
			mJmsNamingContext = getInitialContext(new Hashtable());
		}
		return mJmsNamingContext;
	}

	/**
	 * @return the queueType
	 */
	public String getQueueType() {
		return queueType;
	}

	/**
	 * @param queueType the queueType to set
	 */
	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBConfigTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBConfigTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	public String getCfUserName() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "userName");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception occurred while fetching Cf User Name list", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception occurred while fetching Cf User Name list", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			cfUserName = jmsList.get(0);
		}
		return cfUserName;
	}

	public void setCfUserName(String cfUserName) {
		this.cfUserName = cfUserName;
	}

	public String getCfPassword() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "password");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception while fetching CfPassword", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception while fetching CfPassword ", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			cfPassword = jmsList.get(0);
		}
		return cfPassword;
	}

	public void setCfPassword(String cfPassword) {
		this.cfPassword = cfPassword;
	}
}
