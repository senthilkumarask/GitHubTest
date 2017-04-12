package com.bbb.framework.cache;

import static com.bbb.constants.BBBCoreConstants.DROPLET_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.DROPLET_CACHE_TIMEOUT;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.DROPLET_CACHING_ENABLED;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.Cache;
import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.performance.logger.PerformanceLogger;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class BBBCacheDroplet extends Cache implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6542052658889335187L;

	// -------------------------------------
	// Class version string

	static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

	private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle
			.getBundle(MY_RESOURCE_NAME);

	public static final String DO_NOT_CACHE_RESPONSE = "DoNotCacheResponse";
	public static final String PROTOCOL_CHANGE_DROPLET = "ProtocolChangeDroplet";
	public static final String JSESSIONID = "jsessionid";
	private BBBWebCacheIF cacheContainer;
	static String sJsessionid = null;
	public static final int MAX_VIWABLE_ENTRIES = 20000;

    private PerformanceLogger performanceLogger;
    
	// -----------------------------
	// Properties

	// volatile Dictionary mCache = new Hashtable();

	public BBBCacheDroplet() {
		this.mLastPurgedTime = System.currentTimeMillis();
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String methodName = BBBCoreConstants.DROPLET_CACHE;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.DROPLET_CACHE_INTEGRATION, methodName);
        
        
        
		pRequest.serviceParameter("output", pRequest, pResponse);
		if (isLoggingDebug()) {
			logDebug("BBBCacheDroplet :" );
		}
		String contextRoot = pRequest.getContextPath();
		if(isLoggingDebug()){
			logDebug("contextRoot is:" + contextRoot);
		}
		if(!BBBUtility.isEmpty(contextRoot) && !contextRoot.startsWith("//")){
			if(isLoggingDebug()){
				logDebug("putting content into the cache");
			}
			super.service(pRequest, pResponse);
		}else{
			if(isLoggingDebug()){
				logDebug("igmoring cache since context root is not correct");
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.DROPLET_CACHE_INTEGRATION, methodName);
	}
	
	public void removeCachedElement(String key) {
		long removeCacheStartTime = System.currentTimeMillis();
		getCacheContainer().remove(key);
		long removeCacheEndTime = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug(new StringBuffer("Object for key: ")
					.append(key).append(" is removed in cache. Total time taken to put the object ")
					.append(removeCacheEndTime - removeCacheStartTime).toString());
		}
	}
	// ------------------------------
	/** Time interval for the last time we invalidated the cache */
	long mLastPurgedTime;

	/**
	 * Returns true if we should check to see whether the cached output
	 * parameter should be checked again.
	 * <ul>
	 * <li>We always check if CacheCheckMilliseconds is 0
	 * <li>We check the time if the time interval has elapsed since we last
	 * checked.
	 * </ul>
	 */
	boolean shouldRegenerateCachedData(DynamoHttpServletRequest pRequest,
			CacheDataEntry p, long pCheckTime) {
		long now = System.currentTimeMillis();

		/*
		 * First see if we should purge the entire cache
		 */
		if (now - mLastPurgedTime > getPurgeCacheSeconds()*1000L) {
			if (isLoggingDebug()) {
				logDebug(new StringBuffer("now: ").append(now).append(" mLastPurgedTime :").append(mLastPurgedTime)
						.append(" difference : ").append(now - mLastPurgedTime).append(" getPurgeCacheSeconds() :")
						.append(getPurgeCacheSeconds()).toString());
			}
			flushCache();
			mLastPurgedTime = now;
			if (isLoggingDebug()) {
				logDebug("purged the entire cache for: "
						+ ServletUtil.getCurrentPathInfo(pRequest));
			}
			return true;
		}

		if (now - p.creationTime > pCheckTime) {
			if (isLoggingDebug()) {
				logDebug(new StringBuffer("invalidating the cache for: now - p.creationTime : ")
						.append((now - p.creationTime)).append(" pCheckTime :")
						.append(pCheckTime).append(ServletUtil.getCurrentPathInfo(pRequest)).toString());
		}
			return true;
		}
		return false;
	}

	/**
	 * Forces cache to flush the data.
	 */
	@SuppressWarnings("rawtypes")
    public void flushCache() {
		if(getCacheContainer() instanceof BBBATGCacheContainer){

			getPerformanceLogger().logDebug("BBBCacheDroplet.flushCache() | flushing the entire cache");
			
			long flushCacheStartTime = System.currentTimeMillis();
			getCacheContainer().clearCache(null);
			long flushCacheEndTime = System.currentTimeMillis();
			
			
			if (isLoggingDebug()) {
				logDebug("DropletCache-flushed the entire cache. Total time taken=" + (flushCacheEndTime-flushCacheStartTime));
			}
		}
	}

	// -------------------------------------
	// MessageSink implementation
	// -------------------------------------

	// -------------------------------------
	/**
	 * Called by DMS when a Message arrives through the given input port. There
	 * may be concurrent calls of this method from multiple Threads.
	 *
	 * @exception JMSException
	 *                if there is a problem processing the message
	 **/
	public synchronized void receiveMessage(String pPortName, Message pMessage)
			throws JMSException {
	
		if(isLoggingDebug()){
			logDebug("START:BBBCacheDroplet.receiveMessage : pPortName="+pPortName);
		}
		// ignore null messages
		if (pMessage != null) {
		
		// only know how to deal with standard Dynamo messages
		if (!(pMessage instanceof ObjectMessage)) {
			Object[] args = { pMessage.getJMSType(), pPortName };
			throw new JMSException(ResourceUtils.getMsgResource(
					"cacheInvalidMessage", MY_RESOURCE_NAME, sResourceBundle,
					args));
		}
		// for now we don't use any properties of the message itself
		// unused ObjectMessage message = (ObjectMessage) pMessage;

		// just flush the entire cache
			getPerformanceLogger().logDebug(new StringBuffer(
									"BBBCacheDroplet.receiveMessage : calling flushCach for pMessage")
									.append(pMessage.getClass()).append(" pMessageType")
									.append(pMessage.getJMSType()).append(" pPortName=").append(pPortName).toString());
		flushCache();
			getPerformanceLogger().logDebug("BBBCacheDroplet.receiveMessage() :  flushCache completed");

	}
		if(isLoggingDebug()){
			logDebug("END:BBBCacheDroplet.receiveMessage : pPortName="+pPortName);
		}

	}

	/**
	 * Creates and returns a new Servlet that will administer this service.
	 **/
	protected Servlet createAdminServlet() {
		return new CacheAdminServlet(this, getNucleus());
	}

	/**
	 * Class that will administer this service
	 **/
	class CacheAdminServlet extends atg.nucleus.ServiceAdminServlet {

		/**
		 *
		 */
		private static final long serialVersionUID = -8339065070629251261L;
		// -------------------------------------
		/** Class version string */

		public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Cache.java#2 $$Change: 651448 $";

		
		/**
		 * Constructs an instanceof CacheAdminServlet
		 */
		public CacheAdminServlet(Object pService, Nucleus pNucleus) {
			super(pService, pNucleus);
		}

		/**
		 * Performs all admin functionality, then adds on the cache statistics
		 * @throws IOException
		 *
		 */
		@SuppressWarnings("rawtypes")
        protected void normalAdmin(HttpServletRequest pRequest,
				ServletOutputStream pOut) throws IOException  {
			pOut.println("<form action=\"" + pRequest.getRequestURI()
					+ "\" method=\"post\">");
			pOut.println("<input type=\"hidden\" name=\"removeItemsFromCache\" value=\"true\">");
			pOut.println("<h2>Cache usage statistics</h2>");
			pOut.println("<table border>");
			String[] globalKeys = { "entry", "creationTime", "cacheSize",
					"cacheData", "removeItemsFromCache" };

			// print header
			pOut.println("<tr>");
			for (int i = 0; i < globalKeys.length; i++) {
				pOut.println("<th>" + globalKeys[i] + "</th>");
			}
			pOut.println("</tr>");

			// print cache entries
			int nEntries = 0;
			int byteSize = 0;
			int byteEntries = 0;
			int charSize = 0;
			int charEntries = 0;		

			for (Iterator e = getCacheContainer().getAllKeys(); e.hasNext(); nEntries++) {
				if (nEntries > MAX_VIWABLE_ENTRIES) {
					// too many entries, print ... and finish
					pOut.print("<tr><th colspan=3>   ..............");
					pOut.println("</th></tr>");
					break;
				}
				Object cacheDataKey = e.next();
				CacheDataEntry cacheEntry = (CacheDataEntry) get(cacheDataKey);
				Date creationTime = new Date();
				creationTime.setTime(cacheEntry.creationTime);

				int len = 0;
				if (cacheEntry.data != null) {
					len = cacheEntry.data.length;
					byteSize += len;
					byteEntries++;
				}
				if (cacheEntry.charData != null) {
					len = cacheEntry.charData.length;
					charSize += len;
					charEntries++;
				}
				String[] itemCacheValues = {
						String.valueOf(nEntries),
						creationTime.toString(),
						String.valueOf(len),
						"<a href=\"?cacheDataKey="
								+ ServletUtil.escapeURLString(cacheDataKey
										.toString()) + "\">" + cacheDataKey
								+ "</a>",
						"<input type=\"checkbox\" name=\"remove\" value=\""
								+ ServletUtil.escapeURLString(cacheDataKey
										.toString()) + "\">" };

				pOut.println("<tr>");
				for (int i = 0; i < itemCacheValues.length; i++) {
					pOut.println("<th>" + itemCacheValues[i] + "</th>");
				}
				pOut.println("</tr>");
			}
			pOut.println("</table>");
			pOut.println("<input type=\"submit\" value=\"Remove items from Cache\" name\"submit\">");
			pOut.println("</form>");
			if (nEntries <= MAX_VIWABLE_ENTRIES) {
				StringBuffer sb = new StringBuffer("<p>Cache contains ");
				sb.append(byteEntries).append(" entries, containing ");
				sb.append(byteSize).append(" bytes and ").append(charEntries);
				sb.append(" containing ").append(charSize)
						.append(" characters.");
				pOut.println(sb.toString());
			}
		}

		protected void printAdmin(HttpServletRequest pRequest,
				HttpServletResponse pResponse, ServletOutputStream pOut)
				throws ServletException, IOException {
			String cacheDataKey = pRequest.getParameter("cacheDataKey");
			String removeItemsFromCache = pRequest
					.getParameter("removeItemsFromCache");
			// then you are going to the normal admin page use to work
			try {
				if (cacheDataKey == null && removeItemsFromCache == null) {
					// normalAdmin(pRequest, pResponse, pOut);
					normalAdmin(pRequest, pOut);
				}
				if (cacheDataKey != null) {
					// you want to see the data for one cache name
					viewCacheItemAdmin(pRequest, pResponse, pOut);
				}
				if (removeItemsFromCache != null) {
					// you want to remove one or more items from the cache
					removeItemsFromCacheAdmin(pRequest, pResponse, pOut);
				}
			} catch (Exception e) {
				if(isLoggingError()){
					logError(e);
				}
			}
		}

		@SuppressWarnings("rawtypes")
        private void viewCacheItemAdmin(HttpServletRequest pRequest,
				HttpServletResponse pResponse, ServletOutputStream pOut) throws IOException
				  {
			String cacheDataKey = pRequest.getParameter("cacheDataKey");
			if (cacheDataKey == null) {
				logWarning("Error: cacheDataKey was null");
				return;
			}

			pOut.println("<h2>Cache item data is below</h2>");
			pOut.println("<table border>");

			pOut.println("<tr><td><h3>Data</h3></td></tr>");
			pOut.println("<tr><td>");
			CacheDataEntry cacheEntry = null;
			Iterator e = getCacheContainer().getAllKeys();
			while (e.hasNext()) {
				Object elem = e.next();
				if (elem.toString().equals(cacheDataKey)) {
					cacheEntry = (CacheDataEntry) get(elem);
					break;
				}
			}
			String cacheData = null;
			if (cacheEntry != null) {
				cacheData = ServletUtil.escapeHtmlString(cacheEntry.toString());
			} else {
				cacheData = "couldn't find data for key =" + cacheDataKey;
			}

			pOut.println(cacheData);
			pOut.println("</td></tr>");

			pOut.println("<tr><td><h3>Html</h3></td></tr>");
			pOut.println("<tr><td>");
			cacheData = cacheEntry == null ? "null" : cacheEntry.toString();
			pOut.println(cacheData);
			pOut.println("</td></tr>");

			pOut.println("</table>");
		}
	}

	@SuppressWarnings("deprecation")
    void removeItemsFromCacheAdmin(HttpServletRequest pRequest,
			HttpServletResponse pResponse, ServletOutputStream pOut)
			throws ServletException, IOException {
		String[] removeItems = pRequest.getParameterValues("remove");
		// URLDecoder urlDecoder = new URLDecoder();
		if (removeItems != null) {
			for (int i = 0; i < removeItems.length; i++) {
				String decodeKeyName = URLDecoder.decode(removeItems[i]);
				removeCachedElement(decodeKeyName);
			}
			pOut.println("<h2>Cache items have been removed</h2>");
		} else {
			pOut.println("<h2>No cache items were selected for removal</h2>");
		}
	}

	/*
	 * The object stored with each cache entry
	 */
	class CacheDataEntry implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = -5643052103142976133L;

		// -------------------------------------
		/** Class version string */

		public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Cache.java#2 $$Change: 651448 $";

		/** The last time we recreated this content **/
		long creationTime;

		/** The data */
		byte[] data;

		/** Char data (for dsp4jsp) **/
		char[] charData;

		public CacheDataEntry(byte[] pData) {
			creationTime = System.currentTimeMillis();
			data = pData;
		}

		public CacheDataEntry(char[] pCharData) {
			creationTime = System.currentTimeMillis();
			charData = pCharData;
		}

		public String toString() {
			if (data != null) {
				if (data.length < 100) {
					return new String(data);
				}
				return new String(data, 0, 50) + "...."
						+ new String(data, data.length - 50, 50);
			} else if (charData != null) {
				if (charData.length < 100) {
					return new String(charData);
				}
				return new String(charData, 0, 50) + "...."
						+ new String(charData, charData.length - 50, 50);
			} else {
				return "no data";
			}
		}
	}

	// -------------------------------------
	/**
	 *
	 * Returns the cached content for the given request. The key for the content
	 * is a combination of the request URI and the "key" parameter - the "key"
	 * parameter must be specified. The optional "cacheCheckSeconds" parameter
	 * indicates how long the content should remain in the cache.
	 *
	 * @throws Exception
	 **/
	public char[] getCachedContent(DynamoHttpServletRequest pRequest) {
		
		Object key = null;
		if(null != pRequest.getParameter("loggingSchedulerKey"))
		{
			key = pRequest.getParameter("loggingSchedulerKey");
		}
		else
		{
			key = pRequest.getLocalParameter("key");
		}
		if (key == null) {
			if (isLoggingError()) {
				logError(ResourceUtils.getMsgResource("cacheNoKey",
						MY_RESOURCE_NAME, sResourceBundle));
			}
			return null;
		} else {
			// Form the cache key
			String cacheKey = ServletUtil.getCurrentRequestURI(pRequest) + "###" + key;

			// Get the expire time
			long expireTime = getDefaultCacheCheckSeconds() * 1000L;
			String expireTimeStr = pRequest.getParameter("cacheCheckSeconds");
			if (expireTimeStr != null) {
				try {
					expireTime = Long.parseLong(expireTimeStr) * 1000L;
				} catch (NumberFormatException e) {
					if (isLoggingError()) {
						logError("bad value for cacheCheckSeconds parameter");
					}
				}
			}

			// Find in the cache
			CacheDataEntry cdata = (CacheDataEntry) get(cacheKey);
			if(cdata != null && cdata.charData != null && null != pRequest.getParameter("loggingSchedulerKey"))
			{
				return cdata.charData;
			}
			
			if (cdata != null && cdata.charData != null
					&& !shouldRegenerateCachedData(pRequest, cdata, expireTime)) {
				if (isLoggingDebug()) {
					logDebug("Retrieved item from cache");
				}
				return cdata.charData;
			}

			// Otherwise, nothing from the cache
			return null;
		}
	}

	// -------------------------------------
	/**
	 *
	 * Sets the cached content for the given request. The key for the content is
	 * a combination of the request URI and the "key" parameter, which must be
	 * specified.
	 **/
	public void setCachedContent(DynamoHttpServletRequest pRequest,
			char[] pContent) {
		super.getEnabled();
		
		Object key = pRequest.getLocalParameter("key");
		if (key == null) {
			if (isLoggingError()) {
				logError(ResourceUtils.getMsgResource("cacheNoKey",
						MY_RESOURCE_NAME, sResourceBundle));
			}
			return;
		}
		String cacheContent = (String)pRequest.getLocalParameter("cacheContent");
		// Form the cache key
		String contextRoot = pRequest.getContextPath();
		if(isLoggingDebug()){
			logDebug("setCachedContent-contextRoot is:" + contextRoot);
		}
		if(!BBBUtility.isEmpty(contextRoot) && !contextRoot.startsWith("//")){
			if(isLoggingDebug()){
				logDebug("setCachedContent-putting content into the cache");
			}
			String cacheKey = ServletUtil.getCurrentRequestURI(pRequest) + "###" + key;
			
			if(isLoggingDebug()){
				logDebug("------------------------------------------------------------------------------------------------------------------------");
				logDebug("cacheKey: "+cacheKey+ "\n\nContent: "+ Arrays.toString(pContent) + "\n\nRequest Object: "+ pRequest);
				logDebug("------------------------------------------------------------------------------------------------------------------------");
			}
			
			// This PROTOCOL_CHANGE_ATTRIBUTE is unset in the Droplet tag and maybe
			// set later in the ProtocolChange droplet to avoid caching - Bug# 66179
			// See also:
			// /atg/droplet/ProtocolChange.java
			// /atg/taglib/dspjsp/Droplet.java
			if (pRequest.getAttribute(PROTOCOL_CHANGE_DROPLET) != null) {
				if (isLoggingDebug()) {
					logDebug("Found ProtocolChangeDroplet attribute in response - not caching");
				}
			} else if (pRequest.getAttribute(DO_NOT_CACHE_RESPONSE) != null) {
				if (isLoggingDebug()) {
					logDebug("Found DoNotCacheResponse attribute in response - not caching");
				}
			} else{
				CacheDataEntry cdata = new CacheDataEntry(pContent);
				if("true".equalsIgnoreCase(cacheContent) || (cdata!=null && cdata.charData != null && !StringUtils.isEmpty(String.valueOf(cdata.charData).trim())) 
					//logging to check the change in cache data for Top Nav cache in case of CA Site Id.
						&& Boolean.valueOf(pRequest.getParameter(BBBCoreConstants.TO_PUT_IN_CACHE)) ) {
										
					put(cacheKey, cdata);
					}else{
						if(isLoggingDebug()){
							logDebug("Not caching the data , as it contains jsessionid");
						}
				}
					
			}
		}else{
			if(isLoggingDebug()){
				logDebug("setCachedContent-igmoring cache since context root is not correct");
			}
		}
		
	}

	// -------------------------------------

	/**
	 * Initializes the servlet. This is called automatically by the system when
	 * the servlet is first loaded.
	 *
	 * @param pServletConfig
	 *            servlet configuration information
	 * @exception ServletException
	 *                if a servlet exception has occurred
	 */
	public void init(ServletConfig pConfig) throws ServletException {
		super.init(pConfig);

		synchronized (BBBCacheDroplet.class) {
			if (sJsessionid == null) {
				// go ahead and set it, so no matter what happens it will
				// have been set
				sJsessionid = ";jsessionid=";

				if (getSessionURLName() != null) {
					sJsessionid = ";" + getSessionURLName() + "=";
				}
			}
		}
	}

	public BBBWebCacheIF getCacheContainer() {
		return cacheContainer;
	}

	public void setCacheContainer(BBBWebCacheIF cacheContainer) {
		this.cacheContainer = cacheContainer;
	}
	
	public void clearCache(String pCacheName){
		if (isLoggingDebug()) {
			logDebug("Calling clearCache method");
		}
		
		getPerformanceLogger().logDebug("START BBBCacheDroplet.clearCache method for cacheName="+pCacheName);
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,DROPLET_CACHING_ENABLED));
		if(isCachingEnabled){
			long clearCacheStartTime = System.currentTimeMillis();
			getCacheContainer().clearCache(pCacheName);
			long clearCacheEndTime = System.currentTimeMillis();
	
			if (isLoggingDebug()) {
				logDebug(new StringBuffer("DropletCache- Total time to clear cacheName= ")
						.append(pCacheName).append(" totalTimeTaken= ")
						.append((clearCacheEndTime - clearCacheStartTime)).toString());
			}

		} else{
			getPerformanceLogger().logDebug("END BBBCacheDroplet.clearCache method | caching is disabled");
	}
	}
	
	public Object get(Object key) {
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,DROPLET_CACHING_ENABLED));
		if(isCachingEnabled){
			
			Object cacheObject = null;
			long getCacheStartTime = 0;
			long getCacheEndTime = 0;
			if(getCacheContainer() instanceof CoherenceCacheContainer){

				String cacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DROPLET_CACHE_NAME);
				
				getCacheStartTime = System.currentTimeMillis();
								
				cacheObject = getCacheContainer().get(key, cacheName);;
				
				getCacheEndTime = System.currentTimeMillis();		
				
				if(cacheObject!=null){
					if (isLoggingDebug()) {
						logDebug(new StringBuffer(
								"BBBCacheDroplet.get | cacheContainer is CoherenceCacheContainer: Object for key: ")
								.append(key).append(" found in cache ").append(cacheName)
								.append(". Total time taken to search the object ")
								.append((getCacheEndTime - getCacheStartTime)).toString());
					}
				} else{
					if (isLoggingDebug()) {
						logDebug(new StringBuffer(
								"BBBCacheDroplet.get | cacheContainer is CoherenceCacheContainer : Object for key: ")
								.append(key).append(" not found in cache ")
								.append(cacheName).append(". Total time taken to search the object ").append((getCacheEndTime - getCacheStartTime)).toString());
					}
				}
				return cacheObject;
			}
			
			getCacheStartTime = System.currentTimeMillis();
			
			cacheObject =getCacheContainer().get(key);
			
			getCacheEndTime = System.currentTimeMillis();
			
			if(cacheObject!=null){
				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"BBBCacheDroplet.get | cacheContainer is BBBATGCacheContainer: Object for key: ")
							.append(key).append(". Total time taken to search the object ")
							.append((getCacheEndTime - getCacheStartTime)).toString());
				}
			} else{
				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"BBBCacheDroplet.get | cacheContainer is BBBATGCacheContainer : Object for key: ")
							.append(key).append(". Total time taken to search the object ")
							.append((getCacheEndTime - getCacheStartTime)).toString());
				}
				
			}
			return cacheObject;
		}
		
		if(isLoggingDebug()){
			logDebug("BBBCacheDroplet.get | Caching is disabled ");
		}

		return null;
	}

	public void put(Object key, Object value) {
		boolean isCachingEnabled = Boolean.parseBoolean(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY,DROPLET_CACHING_ENABLED));
		if (isLoggingDebug()) {
			logDebug("Droplet Caching is enabled:"+ isCachingEnabled);
		}
		if(isCachingEnabled){
			
			long putCacheStartTime = 0;
			long putCacheEndTime = 0;
			
			if (isLoggingDebug()) {
				logDebug("Adding item to cache\nkey="+key+"\nvalue="+value);
			}
			
			if(getCacheContainer() instanceof CoherenceCacheContainer){
				String cacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DROPLET_CACHE_NAME);
				long timeout = Long.parseLong(BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, DROPLET_CACHE_TIMEOUT));
				
				putCacheStartTime = System.currentTimeMillis();
				getCacheContainer().put(key, value, cacheName,timeout);
				putCacheEndTime = System.currentTimeMillis();	
				
				if (isLoggingDebug()) {
					logDebug(new StringBuffer(
							"BBBCacheDroplet.put | cacheContainer is CoherenceCacheContainer : Object for key ")
							.append(key).append(" is put in cache").append(cacheName).append(" with expiry timeout=")
							.append(timeout).append(". Total time taken to put the object ")
							.append((putCacheEndTime - putCacheStartTime)).toString());
				}
				
				return;
			}
			
			putCacheStartTime = System.currentTimeMillis();
			getCacheContainer().put(key, value);
			putCacheEndTime = System.currentTimeMillis();
			
			if (isLoggingDebug()) {
				logDebug(new StringBuffer(
						"BBBCacheDroplet.put | cacheContainer is BBBATGCacheContainer : Object for key ")
						.append(key).append(" is put in BBBATGCacheContainer. Total time taken to put the object ")
						.append((putCacheEndTime - putCacheStartTime)).toString());
			}
		} else{
			if(isLoggingDebug()){
				logDebug("BBBCacheDroplet.put | Caching is disabled ");
			}
	}
	}
	
	public PerformanceLogger getPerformanceLogger() {
		return performanceLogger;
	}

	public void setPerformanceLogger(PerformanceLogger performanceLogger) {
		this.performanceLogger = performanceLogger;
	}
	public String ensureNoIds(DynamoHttpServletRequest pRequest,
            String cacheData)
		{
		if (isLoggingDebug()) {
		logDebug("Checking wethere cacheData contains jsessionid or not  ");
		}
		if(( cacheData.indexOf(sJsessionid)) != -1)
		{
			pRequest.setParameter(BBBCoreConstants.TO_PUT_IN_CACHE, "false");
		}else{
			pRequest.setParameter(BBBCoreConstants.TO_PUT_IN_CACHE, "true");
		}
		return super.ensureNoIds(pRequest, cacheData);
		}	
}