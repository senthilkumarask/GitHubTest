package com.bbb.framework.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import org.apache.axis2.java.security.AccessController;

import weblogic.management.provider.ManagementService;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.service.PrivilegedActions;
import atg.dms.patchbay.MessageSink;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.performance.logger.PerformanceLogger;
import com.bbb.utils.BBBUtility;

/**
 * This class implements interface methods to provide wraper layer for atg.service.Cache. The component made of
 * BBBDropletCacheContainer needs to be injected in BBBCacheDroplet's cacheContainer property if ATG's out of box Cache
 * need to be used.
 *
 * @author pprave
 *
 */

public class BBBATGCacheContainer extends atg.service.cache.Cache implements BBBWebCacheIF, MessageSink {

	private static final long serialVersionUID = 152070354201866123L;
	private final String CLS_NAME = "BBBATGCacheContainer";
	private final String KEY_REBUILD_HEADER_CACHE_FLAG = "rebuildHeaderCacheFlag";
	private static final String CACHE_NAVIGATION_CONTENT = "/store/_includes/header/header_top_nav.jsp###";
	private static final String DESKTOP = "DESKTOP";
	private static final String TBS = "TBS";
	private static final String DC3 = "DC3";
	private static final String TBS_UNDERSCORE = "TBS_";
	private String serverPort;
	private HTTPCallInvoker httpCallInvoker;
	private List<String> siteIdList;
	private SiteContextManager siteContextManager;
	private Map<String,String> cacheRefreshServiceUrl;
	private String dcPrefix;
	private BBBCatalogTools catalogTools;
	private String tbsCacheMenuContentsFile;
	/**
	 * @return the dcPrefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}

	/**
	 * @param dcPrefix the dcPrefix to set
	 */
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the cacheRefreshServiceUrl
	 */
	public Map<String, String> getCacheRefreshServiceUrl() {
		return cacheRefreshServiceUrl;
	}

	/**
	 * @param cacheRefreshServiceUrl the cacheRefreshServiceUrl to set
	 */
	public void setCacheRefreshServiceUrl(Map<String, String> cacheRefreshServiceUrl) {
		this.cacheRefreshServiceUrl = cacheRefreshServiceUrl;
	}

	/**
	 * @return the siteContextManager
	 */
	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	/**
	 * @param siteContextManager the siteContextManager to set
	 */
	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	/**
	 * @return the siteIdList
	 */
	public List<String> getSiteIdList() {
		return siteIdList;
	}

	/**
	 * @param siteIdList the siteIdList to set
	 */
	public void setSiteIdList(List<String> siteIdList) {
		this.siteIdList = siteIdList;
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

	private PerformanceLogger performanceLogger;
	@Override
	public synchronized void put(Object key, Object value) {
		if (isLoggingDebug()) {
			logDebug("BBBATGCacheContainer put : "+key);
		}
		getPerformanceLogger().logDebug("BBBATGCacheContainer.put() for key ="+key + " value="+value);
		super.put(key, value);
	}

	@Override
	public synchronized Object get(Object key){
		Object obj=null;
		if (isLoggingDebug()) {
			logDebug("BBBATGCacheContainer get: "+key);
		}
		getPerformanceLogger().logDebug("BBBATGCacheContainer.get() for key ="+key);
		try {
			obj = super.get(key);
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("BBBDropletCacheContainer ",e);
			}
			getPerformanceLogger().logError("BBBATGCacheContainer.get() | error in getting value for key="+key);
		}
		return obj;
	}

	public void receiveMessage(String pPortName,  Message pMessage) throws JMSException {
        if(!(pMessage instanceof ObjectMessage))
            throw new MessageFormatException("Not an ObjectMessage");
        ObjectMessage objMessage = (ObjectMessage)pMessage;
        Object obj = objMessage.getObject();
        if(isLoggingDebug()) {
        	logDebug((new StringBuilder()).append("Received ").append(obj.toString()).append(" on port ").append(pPortName).toString());
        }
        if(obj instanceof CacheInvalidationMessage) {
        	processCacheInvalidation((CacheInvalidationMessage)obj);
        } else {
            if(isLoggingDebug()) {
            	logDebug("BBBATGCacheContainer.receiveMessage() | Not a CacheInvalidationMessage" + " on port " + pPortName + " is an instance of " + pMessage.getClass() + " Hence ignore this function");
            }
        }
    }

	private void processCacheInvalidation(CacheInvalidationMessage cacheInvalidationMessage){
		
		if (isLoggingDebug()) {
			logDebug("START:BBBATGCacheContainer-processCacheInvalidation for CacheInvalidationMessage");
		}
		getPerformanceLogger().logDebug("START:BBBATGCacheContainer.processCacheInvalidation() | invoked by CacheInvalidationMessage");
		StringBuffer stbuff = new StringBuffer();
		long removeCacheStartTime = System.currentTimeMillis();
		
		if(cacheInvalidationMessage.isPaternMatch()){
			
			for (Iterator iterator = getAllKeys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				if(key.toLowerCase().contains(cacheInvalidationMessage.getEntryKey().toLowerCase())){
					if(remove(key)){
						stbuff.append(key);
						stbuff.append(" ");						
					}
				}				
			}
		
		} else {
			if (remove(cacheInvalidationMessage.getEntryKey())){
				stbuff.append(cacheInvalidationMessage.getEntryKey());
			}
		}
		
		boolean rebuildHeaderCache = false;
		List<String> rebuildHeaderCacheFlag = null;
		try {
			rebuildHeaderCacheFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, KEY_REBUILD_HEADER_CACHE_FLAG);
		} catch (BBBSystemException | BBBBusinessException e) {
			if (isLoggingError()) {
				logError("Cannot re-build cache because " + e);
			}
		} 
		if (!BBBUtility.isListEmpty(rebuildHeaderCacheFlag)) {
			rebuildHeaderCache = Boolean.parseBoolean(rebuildHeaderCacheFlag.get(0));
		}
		
		if(rebuildHeaderCache && null != cacheInvalidationMessage.getEntryKey() && (cacheInvalidationMessage.getEntryKey().toLowerCase().contains(CACHE_NAVIGATION_CONTENT)
				|| cacheInvalidationMessage.getEntryKey().toLowerCase().contains(getTbsCacheMenuContentsFile()))){
			rebuildHeaderNavDropletCache(cacheInvalidationMessage);
		}
		
		long removeCacheEndTime = System.currentTimeMillis();
		
		if(isLoggingDebug()) {
			logDebug("END:BBBATGCacheContainer.processCacheInvalidation() |  invoked by CacheInvalidationMessage for " 
					+ cacheInvalidationMessage.getEntryKey() +" following key removed : " + stbuff.toString()
					+ ". Total time take="+ (removeCacheEndTime-removeCacheStartTime));
		}
		getPerformanceLogger().logDebug("END:BBBATGCacheContainer.processCacheInvalidation() | invoked by CacheInvalidationMessage for " 
					+ cacheInvalidationMessage.getEntryKey() +" following key removed : " + stbuff.toString()
					+ ". Total time take="+ (removeCacheEndTime-removeCacheStartTime));
		
		getPerformanceLogger().logDebug("END:BBBATGCacheContainer.processCacheInvalidation()");
	}
	
	private void rebuildHeaderNavDropletCache(CacheInvalidationMessage nearCacheInvalidationMessage){
		final String METHOD_NAME = "rebuildHeaderNavDropletCache";
		BBBPerformanceMonitor.start(CLS_NAME, METHOD_NAME);
		
		if(isLoggingDebug()) {
			logDebug(CLS_NAME+BBBCoreConstants.COLON +METHOD_NAME+": starting for cacheName = " + nearCacheInvalidationMessage.getEntryKey());
		}
		String cacheName = nearCacheInvalidationMessage.getEntryKey();
		
		if(BBBUtility.isEmpty(this.getServerPort())){
			AuthenticatedSubject kernelId = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
			int portNumber = ManagementService.getRuntimeAccess(kernelId).getServer().getListenPort();
			this.setServerPort(String.valueOf(portNumber));
		}
				
		try {
			//set the credentials in request headers for authentication
			InetAddress ip = null;
		    String hostname = "";
			String serviceURI = null;
			Site site = null;
			
			ip = InetAddress.getLocalHost();
	        hostname = ip.getHostName();	
	        
			for(String siteId : this.getSiteIdList()){
				StringBuffer completeUrl = null;
						
				if(nearCacheInvalidationMessage.getEntryKey().toUpperCase().contains(TBS) && DC3.equalsIgnoreCase(getDcPrefix())){
					serviceURI=getCacheRefreshServiceUrl().get(TBS);
					site = this.getSiteContextManager().getSite(TBS_UNDERSCORE+siteId);				
					completeUrl = (new StringBuffer()).append(BBBCoreConstants.HTTP).append(hostname).append(BBBCoreConstants.COLON).
							append(this.getServerPort()).append(serviceURI).append(TBS_UNDERSCORE+siteId);
				} else if(!DC3.equalsIgnoreCase(getDcPrefix()) && nearCacheInvalidationMessage.getEntryKey().toLowerCase().contains(CACHE_NAVIGATION_CONTENT)){
					serviceURI=getCacheRefreshServiceUrl().get(DESKTOP);
					site = this.getSiteContextManager().getSite(siteId);
					completeUrl = (new StringBuffer()).append(BBBCoreConstants.HTTP).append(hostname).append(BBBCoreConstants.COLON).
							append(this.getServerPort()).append(serviceURI).append(siteId);
				}
				
				if (null != site && null != completeUrl) {
					// push siteID for service call
					final SiteContextImpl context = new SiteContextImpl(this.getSiteContextManager(), site);
					this.getSiteContextManager().pushSiteContext(context);
					String url = completeUrl.toString();
					//Call the service which invokes mobile spring controller to get the details
					if (isLoggingDebug()) {
						logDebug("Calling the service with URL = " + url);
					}
					getHttpCallInvoker().executeQuery(url);
					if (isLoggingDebug()) {
						logDebug("Call succeed for URL = " + url);
					}
					// pop siteID after service call is done.
					getSiteContextManager().popSiteContext(context);
				}
			}			
		}catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError("Cannot re-populate cache of " + cacheName + e);
			}			
			BBBPerformanceMonitor.cancel(CLS_NAME, BBBCoreConstants.COLON +METHOD_NAME);
		} catch (BBBSystemException e) {
			if (isLoggingError()) {
				logError("Cannot re-populate cache of " + cacheName + e);
			}			
			BBBPerformanceMonitor.cancel(CLS_NAME, BBBCoreConstants.COLON +METHOD_NAME);
		} catch (SiteContextException e) {
			if (isLoggingError()) {
				logError("Cannot re-populate cache of " + cacheName + e);
			}			
			BBBPerformanceMonitor.cancel(CLS_NAME, BBBCoreConstants.COLON +METHOD_NAME);
		} catch(UnknownHostException  e){
			if (isLoggingError()) {
				logError("Cannot re-populate cache of " + cacheName + e);
			}			
			BBBPerformanceMonitor.cancel(CLS_NAME, BBBCoreConstants.COLON +METHOD_NAME);
	    }
		
		BBBPerformanceMonitor.end(CLS_NAME, METHOD_NAME);
		if(isLoggingDebug()) {
			logDebug(CLS_NAME+BBBCoreConstants.COLON +METHOD_NAME +" ending for cacheName = " + nearCacheInvalidationMessage.getEntryKey());
		}		
	}
	
	@Override
	public Object get(Object key, String pCacheName) {
		return this.get(key);
	}

	@Override
	public void put(Object key, Object value, String pCacheName) {
		this.put(key, value);		
	}

	@Override
	public void put(Object key, Object value, String pCacheName, long timeout) {
		this.put(key, value);
	}

	@Override
	public boolean remove(Object key, String pCacheName) {
		return false;
	}

	@Override
	public Iterator getAllKeys(String pCacheName) {
		return null;
	}

	@Override
	public void clearCache(String pCacheName) {
		super.flush();
		if(isLoggingInfo()){
			logInfo("cache cleared");
		}
	}
	
	public PerformanceLogger getPerformanceLogger() {
		return performanceLogger;
}

	public void setPerformanceLogger(PerformanceLogger performanceLogger) {
		this.performanceLogger = performanceLogger;
	}

	/**
	 * @return the tbsCacheMenuContentsFile
	 */
	public String getTbsCacheMenuContentsFile() {
		return tbsCacheMenuContentsFile;
	}

	/**
	 * @param tbsCacheMenuContentsFile the tbsCacheMenuContentsFile to set
	 */
	public void setTbsCacheMenuContentsFile(String tbsCacheMenuContentsFile) {
		this.tbsCacheMenuContentsFile = tbsCacheMenuContentsFile;
	}
}
