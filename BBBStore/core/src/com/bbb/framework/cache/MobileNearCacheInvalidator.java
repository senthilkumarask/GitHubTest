package com.bbb.framework.cache;

import java.util.HashMap;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import org.apache.axis2.java.security.AccessController;

import weblogic.management.provider.ManagementService;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.service.PrivilegedActions;
import atg.dms.patchbay.MessageSink;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.vo.MobileNearCacheVO;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.utils.BBBUtility;



/**
 * This call implements interface methods to provide wrapper layer for Coherence
 * cache. The component made of CoherenceCacheContainer needs to be injected in
 * BBBCacheDroplet's cacheContainer property if Coherence cache need to be used.
 * 
 * @author pprave
 * 
 */

public class MobileNearCacheInvalidator extends BBBGenericService implements MessageSink {
	
	
	private static final String LOCALHOST = "http://localhost";
	private static final String CACHE_NAME_KEY_PARAM = "cacheName";
	private static final String CACHE_KEYS_PARAM = "cacheKeys";
	/** The http call invoker. */
	private HTTPCallInvoker httpCallInvoker;
	
	private BBBCatalogTools catalogTools;
	
	private String cacheRefreshServiceUrl;
	private String serverPort;
	
	/**
	 * @return the serverPort
	 */
	public String getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return the cacheRefreshServiceUrl
	 */
	public String getCacheRefreshServiceUrl() {
		return cacheRefreshServiceUrl;
	}

	/**
	 * @param cacheRefreshServiceUrl the cacheRefreshServiceUrl to set
	 */
	public void setCacheRefreshServiceUrl(String cacheRefreshServiceUrl) {
		this.cacheRefreshServiceUrl = cacheRefreshServiceUrl;
	}
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}
	
	/**
	 * This method receives the messages which have been received on the same 
	 * destination for which this sink has been registered
	 * 
	 * @param nearCacheInvalidationMessage
	 * @return 
	 */
	@Override
	public void receiveMessage(String pPortName, Message pMessage)
			throws JMSException {
		if (!(pMessage instanceof ObjectMessage))
			throw new MessageFormatException("ERROR: Not an ObjectMessage");
		
		ObjectMessage objMessage = (ObjectMessage) pMessage;
		Object obj = objMessage.getObject();

		logDebug((new StringBuilder()).append("Received ")
				.append(obj.toString()).append(" on port ").append(pPortName)
				.toString());

		if (obj instanceof CacheInvalidationMessage) {
			processCacheInvalidation((CacheInvalidationMessage) obj);
		} else {
			logDebug("MobileNearCacheInvalidator.receiveMessage() | Not a InvalidationMessage on port "
					+ pPortName + " is an instance of " + pMessage.getClass());
		}
	}
	
	/**
	 * This method clears the near mobile cache by calling the service which further calls the spring controller
	 * 
	 * @param nearCacheInvalidationMessage
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	private void processCacheInvalidation(CacheInvalidationMessage nearCacheInvalidationMessage) {
		
		logDebug("MobileNearCacheInvalidator.processCacheInvalidation [Start] cacheName = " + nearCacheInvalidationMessage.getEntryKey());
		String cacheName = nearCacheInvalidationMessage.getEntryKey();
		List<String> listCacheKeys = nearCacheInvalidationMessage.getKeys();
		
		if(BBBUtility.isEmpty(this.getServerPort())){
			AuthenticatedSubject kernelId = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
			int portNumber = ManagementService.getRuntimeAccess(kernelId).getServer().getListenPort();
			this.setServerPort(String.valueOf(portNumber));
		}
		
		try {
			//set the credentials in request headers for authentication
			HashMap<String, String> headerParam = new HashMap<String, String>();
			HashMap<String, String> param = new HashMap<String, String>();
			List<String> username = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.ADMIN_NAME);
			List<String> password = getCatalogTools().getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.ADMIN_PASSWRD);
			headerParam.put(BBBCoreConstants.USERNAME, username.get(0));
			headerParam.put(BBBCoreConstants.PASSWORD, password.get(0));
			
			String serviceURI = getCacheRefreshServiceUrl();
			
			StringBuffer completeUrl = (new StringBuffer()).append(LOCALHOST).append(BBBCoreConstants.COLON).append(this.getServerPort()).append(
					serviceURI);
			param.put(CACHE_NAME_KEY_PARAM, cacheName);
			if(!BBBUtility.isListEmpty(listCacheKeys)){
				String cacheKeyParam = BBBCoreConstants.BLANK;
				int count = 0;
				for(String cacheKey: listCacheKeys){
					if(count == 0){
						cacheKeyParam = cacheKey;
					}else{
						cacheKeyParam = cacheKeyParam + BBBCoreConstants.COMMA + cacheKey;
					}
					count++;
				}
				param.put(CACHE_KEYS_PARAM, cacheKeyParam);
			}
			
			String url = completeUrl.toString();
			
			//Call the service which invokes mobile spring controller to get the details
			logDebug("Calling the service with URL = " + url);
			MobileNearCacheVO mobileNearCacheVO = getHttpCallInvoker().invokeToGetJson(MobileNearCacheVO.class, headerParam, url, param, BBBCoreConstants.POST);
			logDebug("Response after Calling the service with URL = " + completeUrl + " is : " + mobileNearCacheVO);
			
			//if error exists, then log an error
			if(null != mobileNearCacheVO && (BBBCoreConstants.TRUE).equals(mobileNearCacheVO.getErrorExists())){
				logError("Cannot clear near cache of " + cacheName  + mobileNearCacheVO.getErrorMessage());
			}
			
		}catch (BBBBusinessException e) {
			logError("Cannot clear near cache of " + cacheName + e);
		} catch (BBBSystemException e) {
			logError("Cannot clear near cache of " + cacheName + e);
		}
	
	}
	
}